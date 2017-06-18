package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.type.Type.DefinedType;

/**
 * Genereal base class for declarations.
 */
public abstract class Declaration implements LinkToSource {
    
    // link to source code
    private String link;
    // a declarattions always declares an identifier
    protected String ident;
    
    public Declaration(String ident) {
        link = NO_LINK_PROVIDED;
        this.ident = ident;
    }
    
    public Declaration(String ident, String link) {
        this(ident);
        this.link = link;
    }
    
    /**
     * Accepts a visitor on this declaration viewed as a node in the
     * program tree.
     * 
     * @param v The visitor to be accepted.
     */
    public abstract void accept(Visitor v);
    
    @Override
    public String getLinkToSource() { return link; }
    public String getIdent() { return ident; }
   
    /**
     * Represents a type declaration (typedef) in the tree of abstract syntax.
     */
    public static class DeclarationType extends Declaration {
        
        private DefinedType type;
        
        public DeclarationType(String ident, DefinedType type) {
            super(ident);
            this.type = type;
        }
        
        public DeclarationType(String ident, DefinedType type, String link) {
            super(ident, link);
            this.type = type;
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        public DefinedType getType() { return type; }
        
    }

    /**
     * Represents a variable declaration. Variables are declared with a type.
     */
    public static class DeclarationVariable extends Declaration {

       private DefinedType type;
       private int dir;

       /**
        * Reduced constructor without link to source.
        * 
        * @param var Name of the variable to ne declared.
        * @param type Type of the new variable.
        * @throws IllegalArgumentException If the type is not valid.
        */
       public DeclarationVariable(String ident, DefinedType type) {
           super(ident);
           this.type = type;
       }

       /**
        * Constructs a variable declaration.
        * 
        * @param ident Name of the variable to ne declared.
        * @param type Type of the new variable.
        * @param linkToSource Position of the declaration in source code.
        * @throws IllegalArgumentException If the type is not valid.
        */
       public DeclarationVariable(
               String ident,
               DefinedType type,
               String linkToSource
       ) {
           super(ident, linkToSource);
           this.type = type;
       }

       @Override
       public void accept(Visitor v) { v.visit(this); }
       
       public DefinedType getType() { return type; }       
       public int getDir() { return dir; }

       public void setDir(int dir) { this.dir = dir; }
       public void setType(DefinedType type) { this.type = type; }
       
    }
    
    /**
     * Subprocedure declaration.
     */
    public static class DeclarationProc extends Declaration {
        
        private DeclarationParam[] params;
        private Instruction body;
        private int level;
        private int size;
        
        public DeclarationProc(
                String ident,
                DeclarationParam[] params,
                Instruction body
        ) {
            super(ident);
            this.params = params;
            this.body = body;
        }
        
        public DeclarationProc(
                String ident,
                String link,
                DeclarationParam[] params,
                Instruction body
        ) {
            super(ident, link);
            this.params = params;
            this.body = body;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public DeclarationParam[] getParams() { return params; }
        public Instruction getBody() { return body; }
        public int getLevel() { return level; }
        public int getSize() { return size; }
        
        public void setLevel(int level) { this.level = level; }
        public void setSize(int size) { this.size = size; }
        
    }
    
    /**
     * Represents the declaration of a parameter to a subprocedure.
     * 
     * Basically a variable declaration with an additional flag 'byReference'.
     * Maybe as inner class to DeclarationProc ???
     */
    public static class DeclarationParam extends DeclarationVariable {
        
        private boolean byReference;
        
        public DeclarationParam(
                String ident,
                DefinedType type,
                boolean byReference,
                String linkToSource
        ) {
            super(ident, type, linkToSource);
            this.byReference = byReference;
        }
        
        public DeclarationParam(
                String ident,
                DefinedType type,
                boolean byReference
        ) {
            super(ident, type);
            this.byReference = byReference;
        }
        
        public boolean isByReference() { return byReference; }
        
    }
    
}