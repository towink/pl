package pl.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.type.Type.*;
import pl.util.Pair;

/**
 * Type compatibility check algorithm.
 * 
 * Two types t1 and t2 are considered to be compatible if t1 can be converted in
 * t2 and vice versa.
 * 
 * Idea: print detailed error messages with reason while types are
 * not comparable...
 */
public class TypeCompatibility {
    
    public static boolean check(Type t1, Type t2) {
        // System.out.println("type checking: " + t1 + ", " + t2);
        return check(t1, t2, new HashSet<>());
    }
    
    private static boolean check(
            Type t1,
            Type t2,
            Set<Pair<Type, Type>> considered
    ) {
        // check if the types have already been compared
        if(considered.add(new Pair<>(t1, t2))) {
            
            if(t1.isReference()) {
                return check(t1.toRef().referencedType(), t2);
            }
            if(t2.isReference()) {
                return check(t1, t2.toRef().referencedType());
            }

            // equal types are of course compatible
            if(t1.equals(t2)) return true;

            // int and real are compatible
            if(t1 == Type.INT && t2 == Type.REAL) return true;
            if(t1 == Type.REAL && t2 == Type.INT) return true;

            // array types
            if(t1.isArray() && t2.isArray()) {
                TypeArray a1 = t1.toArray();
                TypeArray a2 = t2.toArray();
                // dimension must be equal
                if(a1.getDim() != a2.getDim()) return false;
                // base types must be equal
                return check(a1.getBaseType(), a2.getBaseType());
            }

            // record types
            if(t1.isRecord() && t2.isRecord()) {
                TypeRecord r1 = t1.toRecord();
                TypeRecord r2 = t2.toRecord();
                // there  must be the same number of fields
                if(r1.getFields().size() != r2.getFields().size()) {
                    return false;
                }
                List<TypeRecord.RecordField> fields1 = r1.getFields();
                List<TypeRecord.RecordField> fields2 = r2.getFields();
                int size = fields1.size();
                for(int i = 0; i < size; i++) {
                    // first check if the names are pairwise equal
                    if(!fields1.get(i).getIdentifier().equals(fields2.get(i).getIdentifier())) {
                        return false;
                    }
                    // then recursively check if types are compatible
                    return check(fields1.get(i).getType(), fields2.get(i).getType());
                }
            }

            // pointer types
            if(t1.isPointer() && t2.isPointer()) {
                TypePointer p1 = t1.toPointer();
                TypePointer p2 = t2.toPointer();
                // if either of them is the null pointer then they are compatible
                if(p1.isNullPointer() || p2.isNullPointer()) return true;
                // pointers need to have the same base type
                return check(p1.getBaseType(), p1.getBaseType());
            }

            // referenced types
            /*if(t1.isReference() && t2.isReference()) {
                TypeRef r1 = t1.toRef();
                TypeRef r2 = t2.toRef();
                return check(
                    r1.getDecReferencedType().getType(),
                    r2.getDecReferencedType().getType()
                );
            }*/
            
            // else the types are incompatible
            return false;
            
        }
        // return true if the types have already been compared
        // TODO why? is this correct?
        // because this can never be false because the recursive procedures would have returned false before??
        else {
            return true;
        }
    }
}