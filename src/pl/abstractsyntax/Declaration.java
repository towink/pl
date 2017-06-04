package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.type.Type;
import pl.type.Type.DefinedType;

/**
 * Genereal base class for declarations.
 */
public abstract class Declaration implements LinkToSource {
    
    private String link;
    
    @Override
    public String getLinkToSource() {
        return link;
    }
    public Declaration() {}
    public Declaration(String link) { this.link = link; }
    

    /**
     * Accepts a visitor on this declaration viewed as a node in the
     * program tree.
     * 
     * @param v The visitor to be accepted.
     */
    public abstract void accept(Visitor v);
    
    /* TODO: extract members identifier and type into superclass */
   
    /**
     * Represents a type declaration (typedef) in the tree of abstract syntax.
     */
    public static class DeclarationType extends Declaration {
        private String alias;
        private DefinedType type;
        public DeclarationType(String alias, DefinedType type) {
            this.alias = alias;
            this.type = type;
        }
        public DeclarationType(String alias, DefinedType type, String link) {
            super(link);
            this.alias = alias;
            this.type = type;
        }
        public String getAlias() {
            return alias;
        }
        public DefinedType getType() {
            return type;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /**
     * Represents a variable declaration. Variables are declared with a type.
     */
    public static class DeclarationVariable extends Declaration {

       private String var;
       private DefinedType type;
       private int dir;

       /**
        * Reduced constructor without link to source.
        * 
        * @param var Name of the variable to ne declared.
        * @param type Type of the new variable.
        * @throws IllegalArgumentException If the type is not valid.
        */
       public DeclarationVariable(String var, DefinedType type) {
           this(var, type, null);
       }

       /**
        * Constructs a variable declaration.
        * 
        * @param var Name of the variable to ne declared.
        * @param type Type of the new variable.
        * @param linkToSource Position of the declaration in source code.
        * @throws IllegalArgumentException If the type is not valid.
        */
       public DeclarationVariable(String var, DefinedType type, String linkToSource) {
           super(linkToSource);
           this.var = var;
           this.type = type;
       }

       /* getters */

       public String getVar() {
           return var;
       }

       public DefinedType getType() {
           return type;
       }
       
       public void setType(DefinedType type) {
            this.type = type;
        }

       public int getDir() {
           return dir;
       }

       /* setters */

       public void setDir(int dir) {
           this.dir = dir;
       }

       @Override
       public void accept(Visitor v) {
           v.visit(this);
       }
    }
}