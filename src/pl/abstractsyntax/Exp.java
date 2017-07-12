package pl.abstractsyntax;

import pl.type.Type;
import pl.procedures.Visitor;
import pl.abstractsyntax.Program.AbstractSyntaxNode;

/**
* Represents an expression like x + 2 * y.
* Expression have types which are set by a TypeCheckVisitor.
*/
public abstract class Exp extends AbstractSyntaxNode implements LinkToSource {
    
    private String linkToSource;
    
    public Exp() { linkToSource = NO_LINK_PROVIDED; }
    
    public Exp(String linkToSource) { this.linkToSource = linkToSource; }
    
    @Override
    public String getLinkToSource() { return linkToSource; }

    public abstract void accept(Visitor v);
    
    /**
     * Call this method only after the type checking visitor has been applied.
     * This is thought to be a convinienve method.
     * 
     * @return true if this expression has a numerical type, i.e. int or real.
     */
    public boolean hasNumericalType() {
        return type == Type.INT || type == Type.REAL;
    }
    
    public boolean isMem() { return false; }
    public boolean isConstant() { return false; }
    
   
    /* constants */
    
    public static abstract class Constant extends Exp {
        
        public Constant() {}
        
        public Constant(String linkToSource) { super(linkToSource); }
        
        @Override
        public boolean isConstant() { return true; }
        
    }
   
    /**
     * Represents an integer constant in the tree of abstract syntax.
     */
    public static class ConstantInt extends Constant {

       private int val;
       
       public ConstantInt(int val) { this.val = val; }

       public ConstantInt(int val, String linkToSource) {
           super(linkToSource);
           this.val = val;
       }
       
       @Override
       public void accept(Visitor v) { v.visit(this); }
       
       public int getValue() { return val; }
       
    }

    /**
    * Represents a Boolean constant in the tree of abstract syntax.
    */
    public static class ConstantBool extends Constant {
        
       private boolean val;
       
       public ConstantBool(boolean val) { this.val = val; }
       
       public ConstantBool(boolean val, String linkToSource) {
           super(linkToSource);
           this.val = val;
       }
       
       @Override
       public void accept(Visitor v) { v.visit(this); }

       public boolean getValue() { return val; }      
       
    }
    
    /**
    * Represents a real constant in the tree of abstract syntax.
    */
    public static class ConstantReal extends Constant {
        
       private double val;
       
       public ConstantReal(double val) { this.val = val; }
       
       public ConstantReal(double val, String linkTouSource) {
           super(linkTouSource);
           this.val = val;
       }

       @Override
       public void accept(Visitor v) { v.visit(this); }
       
       public double getValue() { return val; }
       
    }
    
    /**
    * Represents a char constant in the tree of abstract syntax.
    */
    public static class ConstantChar extends Constant {
        
       private char val;
       
       public ConstantChar(char val) { this.val = val; }
       
       public ConstantChar(char val, String linkTouSource) {
           super(linkTouSource);
           this.val = val;
       }

       @Override
       public void accept(Visitor v) { v.visit(this); }
       
       public char getValue() { return val; }
       
    }
    
    /**
    * Represents a string constant in the tree of abstract syntax.
    */
    public static class ConstantString extends Constant {
        
       private String val;
       
       public ConstantString(String val) { this.val = val; }
       
       public ConstantString(String val, String linkTouSource) {
           super(linkTouSource);
           this.val = val;
       }

       @Override
       public void accept(Visitor v) { v.visit(this); }
       
       public String getValue() { return val; }
       
    }
    
    public static class ConstantNull extends Constant {
        
        public ConstantNull() {}
        
        public ConstantNull(String linkToSource) { super(linkToSource); }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* unary expressions */

    /**
     * Represent a generic unary expression.
     */
    public static abstract class UnaryExp extends Exp {
        
        private Exp op;
        
        public UnaryExp(Exp op) { this.op = op; }

        public UnaryExp(Exp op, String linkToSource) {
            super(linkToSource);
            this.op = op;
        }

        public Exp getOp()  {return op; }
        
    }
    
    /* unary expressions - arithmetic */

    /**
     * Represents a sign change of a numeric value.
     */
    public static class SignChange extends UnaryExp {

        public SignChange(Exp op) { super(op); }
        
        public SignChange(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* unary expressions - logical */

    /**
     * Represents logical negation of a boolean.
     */
    public static class Not extends UnaryExp {
        
        public Not(Exp op) { super(op); }
        
        public Not(Exp op, String linkToSource) { super(op, linkToSource); }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* unary expressions - explicit type conversion */

    /**
     * Represents explicit conversion (cast) to type int.
     */
    public static class ConversionInt extends UnaryExp {

        public ConversionInt(Exp op) { super(op); }
        
        public ConversionInt(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     * Represents explicit conversion (cast) to type bool.
     */
    public static class ConversionBool extends UnaryExp {

        public ConversionBool(Exp op) { super(op); }
        
        public ConversionBool(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     * Represents explicit conversion (cast) to type real.
     */
    public static class ConversionReal extends UnaryExp {

        public ConversionReal(Exp op) { super(op); }
        
        public ConversionReal(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
    }
    
    /**
     * Represents explicit conversion (cast) to type char.
     */
    public static class ConversionChar extends UnaryExp {

        public ConversionChar(Exp op) { super(op); }
        
        public ConversionChar(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     * Represents explicit conversion (cast) to type string.
     */
    public static class ConversionString extends UnaryExp {

        public ConversionString(Exp op) { super(op); }
        
        public ConversionString(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* binary expressions */

    /**
    * Intermediate class representing a general binary expression (addition,
    * multiplication, etc.) in the tree of abstract syntax.
    */
    public static abstract class BinaryExp extends Exp {

       private Exp op1;
       private Exp ap2;

       public BinaryExp(Exp op1, Exp op2) {
           this.op1 = op1;
           this.ap2 = op2;
       }

       public BinaryExp(Exp op1, Exp op2, String linkToSource) {
           super(linkToSource);
           this.op1 = op1;
           this.ap2 = op2;
       }

       public Exp getOp1() { return op1; }
       public Exp getOp2() { return ap2; }
       
    }
    
    /* binary expressions - miscellaneous */

    /**
     * Represents the string indexing expression.
     */
    public static class ChainElement extends BinaryExp {
        
        public ChainElement(Exp op1, Exp op2) { super(op1, op2); }
        
        public ChainElement(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* binary expressions - arithmetic */

    /**
     * Represents a generic binary arithmetic expression.
     */
    public static abstract class BinaryArithmeticExp extends BinaryExp {
        
        public BinaryArithmeticExp(Exp op1, Exp op2) { super(op1, op2); }
        
        public BinaryArithmeticExp(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
    }
    
    /**
    * Represents an arithmetic sum expression in the tree of abstract syntax.
    */
    public static class Sum extends BinaryArithmeticExp {

        public Sum(Exp op1, Exp op2) { this(op1, op2, null); }

        public Sum(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
    * Represents a difference expression in the tree of abstract syntax.
    */
    public static class Difference extends BinaryArithmeticExp {

        public Difference(Exp op1, Exp op2) { this(op1, op2, null); }

        public Difference(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
    * Represents a division expression in the tree of abstract syntax.
    */
    public static class Quotient extends BinaryArithmeticExp {

        public Quotient(Exp op1, Exp op2) { this(op1, op2, null); }

        public Quotient(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }

    /**
    * Represents a product expression in the tree of abstract syntax.
    */
    public static class Product extends BinaryArithmeticExp {

        public Product(Exp op1, Exp op2) { this(op1, op2, null); }

        public Product(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
    * Represents a modulo expression in the tree of abstract syntax.
    */
    public static class Rest extends BinaryArithmeticExp {

        public Rest(Exp op1, Exp op2) { this(op1, op2, null); }

        public Rest(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }

        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* binary expressions - relational */

    /**
     * Represents a generic binary relational expression.
     */
    public static abstract class BinaryRelationalExp extends BinaryExp {
        
        public BinaryRelationalExp(Exp op1, Exp op2) { super(op1, op2); }
        
        public BinaryRelationalExp(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
    }
    
    /**
     *
     */
    public static class Equal extends BinaryRelationalExp {
        
        public Equal(Exp op1, Exp op2) { super(op1, op2); }
        
        public Equal(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     *
     */
    public static class Unequal extends BinaryRelationalExp {
        
        public Unequal(Exp op1, Exp op2) { super(op1, op2); }
        
        public Unequal(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     *
     */
    public static class Less extends BinaryRelationalExp {
        
        public Less(Exp op1, Exp op2) { super(op1, op2); }
        
        public Less(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     *
     */
    public static class LessEqual extends BinaryRelationalExp {
        
        public LessEqual(Exp op1, Exp op2) { super(op1, op2); }
        
        public LessEqual(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     *
     */
    public static class Greater extends BinaryRelationalExp {
        
        public Greater(Exp op1, Exp op2) { super(op1, op2); }
        
        public Greater(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /**
     *
     */
    public static class GreaterEqual extends BinaryRelationalExp {
        
        public GreaterEqual(Exp op1, Exp op2) { super(op1, op2); }
        
        public GreaterEqual(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
    /* binary expressions - logical */

    /**
     * Represents a generic binary logical expression.
     */
    public static abstract class BinaryLogicalExp extends BinaryExp {
        
        public BinaryLogicalExp(Exp op1, Exp op2) { super(op1, op2); }

        public BinaryLogicalExp(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        
    }

    /**
    * Represents a logical 'and' in the program tree.
    */
    public static class And extends BinaryLogicalExp {
        
        public And(Exp op1, Exp op2) { this(op1, op2, null); }
        
        public And(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }

    /**
    * Represents a logical 'or' in the program tree.
    */
    public static class Or extends BinaryLogicalExp {
        
        public Or(Exp op1, Exp op2) { this(op1, op2, null); }
        
        public Or(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);  
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
    }
    
}