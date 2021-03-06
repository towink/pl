package pl.type;

import java.util.ArrayList;
import java.util.List;
import pl.abstractsyntax.Declaration.DeclarationType;
import pl.abstractsyntax.LinkToSource;
import pl.procedures.Visitor;

/**
 * Represents the type of a node in an expression tree.
 * 
 * We implement the basic types as singletons as they do not have any
 * attributes and only serve us as 'markers'.
 * 
 * A type's toString function is used to print the node's attributes in the
 * printing visitor. However, the printing visitor may also use the toString
 * function to print the node itself. The toString's of the types must print the
 * type in a one-line fashion.
 */
public abstract class Type {

    // definable atomic types
    public static final TypeInt     INT = new TypeInt();
    public static final TypeBool    BOOL = new TypeBool();
    public static final TypeReal    REAL = new TypeReal();
    public static final TypeChar    CHAR = new TypeChar();
    public static final TypeString  STRING = new TypeString();

    // atomic types used by typechecker
    public static final TypeOk      OK = new TypeOk();
    public static final TypeError   ERROR = new TypeError();

    /* these are overwritten by inheriting types to avoid using instanceof */
    public boolean isRecord() { return false; }
    public boolean isArray() { return false; }
    public boolean isReference() { return false; }
    public boolean isPointer() { return false; }

    /* these are overwritten by inheriting types to avoid using explicit casts */
    public TypeRecord toRecord() {
        throw new UnsupportedOperationException("type is no record");
    }
    public TypeArray toArray() {
        throw new UnsupportedOperationException("type is no array");
    }
    public TypePointer toPointer() {
        throw new UnsupportedOperationException("type is no pointer");
    }
    public TypeRef toRef() {
        throw new UnsupportedOperationException("type is no reference");
    }
    
    public void accept(Visitor v) { v.visit(this); }

    /**
     * Defined types are the ones we can use in declarations in the source code.
     */
    public static abstract class DefinedType extends Type {

        /**
         * This is the size that a variable of this type will occupy in the
         * machine's memory.
         * 
         * For all types except array and record, this will always be one cell.
         * The size is set by a visitor.
         * This size is static and known at compile time.
         */
        private int size;

        public DefinedType() {
            size = 0;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public boolean sizeNotSet() { return size == 0; }

        public int getSize() { return size; }
        public void setSize(int size) {
            if(size < 1) {
                throw new IllegalArgumentException();
            }
            this.size = size;
        }
    }
    
    public static abstract class AtomicDefinedType extends DefinedType {}

    /* primitive (atomic) types */

    public static class TypeInt extends AtomicDefinedType {
        private TypeInt() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "INT";}
    }

    public static class TypeBool extends AtomicDefinedType {
        private TypeBool() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "BOOL";}
    }

    public static class TypeReal extends AtomicDefinedType {
        private TypeReal() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "REAL";}
    }

    public static class TypeChar extends AtomicDefinedType {
        private TypeChar() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "CHAR";}
    }

    public static class TypeString extends AtomicDefinedType {
        private TypeString() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "STRING";}
    }
    
    public static class TypeError extends Type {
        private TypeError() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "ERROR";}
    }

    public static class TypeOk extends Type {
        private TypeOk() {}
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {return "OK";}
    }

    /* type references */

    public static class TypeRef extends DefinedType implements LinkToSource {
        
        private String linkToSource;
        private String alias;
        private DeclarationType decReferencedType;

        public TypeRef(String alias) {
            linkToSource = null;
            this.alias = alias;
        }
        
        public TypeRef(String alias, String linkToSource) {
            this.linkToSource = linkToSource;
            this.alias = alias;
        }
        
        
        @Override
        public String getLinkToSource() { return linkToSource; }
        
        public String getAlias() {
            return alias;
        }
        public DefinedType referencedType() {
            DefinedType res = decReferencedType.getType();
            if(res.isReference()) {
                return res.toRef().referencedType();
            }
            else {
                return res;
            }
        }
        public DeclarationType getDecReferencedType() {
            return decReferencedType;
        }
        public void setDecReferencedType(DeclarationType decReferencedType) {
            this.decReferencedType = decReferencedType;
        }
        @Override
        public TypeRef toRef() { return this; }
        @Override
        public boolean isReference() { return true; }
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {
            return "reference to "  + alias;
        }
    }

    /* arrays */

    public static class TypeArray extends DefinedType {
        private DefinedType baseType;
        private int dim;
        public TypeArray(DefinedType baseType, int dim) {
            this.baseType = baseType;
            this.dim = dim;
        }
        public DefinedType getBaseType() {
            return baseType;
        }
        public int getDim() {
            return dim;
        }
        @Override
        public boolean isArray() { return true; }
        @Override
        public TypeArray toArray() {return this; }
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() { return baseType + "[" + dim + "]"; }
    }

    /* records (structs) */

    public static class TypeRecord extends DefinedType {
        
        private List<RecordField> fields;
        public TypeRecord(List<RecordField> fields) {
            this.fields = fields;
        }
        public List<RecordField> getFields() {
            return fields;
        }
        public List<String> getFieldIdentifiers() {
            List<String> res = new ArrayList<>();
            for(RecordField f : fields) {
                res.add(f.getIdentifier());
            }
            return res;
        }
        public DefinedType typeOf(String identifier) {
            for(RecordField f : fields) {
                if(f.identifier.equals(identifier)) return f.type;
            }
            throw new IllegalArgumentException("there is no field " + identifier);
        }
        public RecordField getFieldByIndet(String identifier) {
            for(RecordField f : fields) {
                if(f.identifier.equals(identifier)) return f;
            }
            throw new IllegalArgumentException("there is no field " + identifier);
        }
        @Override
        public boolean isRecord() { return true; }
        @Override
        public TypeRecord toRecord() {return this; }
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {
            String res = "struct {";
            for(RecordField f : fields) {
                res += f + ";";
            }
            return res + "}";
        }
        
    }
    
    public static class RecordField {
        
        private String identifier;
        private DefinedType type;
        private int offset;
        
        public RecordField(String identifier, DefinedType type) {
            this.identifier = identifier;
            this.type = type;
            offset = 0;
        }
        
        public String getIdentifier() { return identifier; }
        public DefinedType getType() { return type; }
        public int getOffset() { return offset; }
        public void setOffset(int offset) { this.offset = offset; }
        
        @Override
        public String toString() {
            return type + " " + identifier;
        }
        
    }

    /* pointers */

    public static class TypePointer extends DefinedType {
        private final DefinedType baseType;
        // null pointer
        public TypePointer() {
            this.baseType = null;
        }
        public TypePointer(DefinedType baseType) {
            this.baseType = baseType;
        }
        public DefinedType getBaseType() {
            return baseType;
        }
        public boolean isNullPointer() { return baseType == null; }
        @Override
        public boolean isPointer() { return true; }
        @Override
        public TypePointer toPointer() {return this; }
        @Override
        public void accept(Visitor v) { v.visit(this); }
        @Override
        public String toString() {
            if(isNullPointer()) { return "NULLPOINTER"; }
            return "pointer to " + baseType;
        }
    }

}