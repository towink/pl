package pl.abstractsyntax;

import pl.type.Type;
import pl.procedures.Visitor;
import pl.abstractsyntax.Program.AbstractSyntaxNode;
import pl.abstractsyntax.Declaration.*;

/**
* Represents an expression like x + 2 * y.
* Expression have types which are set by a TypeCheckVisitor.
*/
public abstract class Exp extends AbstractSyntaxNode {

    public abstract void accept(Visitor v);
    
    /**
     * Call this method only after the type checking visitor has been applied!
     * This is thought to be a convinienve method.
     * 
     * @return true if this expression has a numerical type, i.e. int or real.
     */
    public boolean hasNumericalType() {
        return type.equals(Type.TypeInt.getInstance()) ||
               type.equals(Type.TypeReal.getInstance());
    }
    
    public boolean isMem() {
        return false;
    }
    
   
    /* constants */
    
    public static abstract class Constant extends Exp {
    }
   
    /**
     * Represents an integer constant in the program tree.
     */
    public static class ConstantInt extends Constant {

       private int val;
       public ConstantInt(int val) {
           this.val = val;
       }
       public int getValue() {
           return val;
       }
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }

    /**
    * Represents a Boolean constant in the program tree.
    */
    public static class ConstantBool extends Constant {
       private boolean val;
       public ConstantBool(boolean val) {
           this.val = val;
       }

       public boolean getValue() {
           return val;
       }      
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /**
    * Represents a real constant in the program tree.
    */
    public static class ConstantReal extends Constant {
       private double val;
       public ConstantReal(double val) {
           this.val = val;
       }

       public double getValue() {
           return val;
       }      
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /**
    * Represents a char constant in the program tree.
    */
    public static class ConstantChar extends Constant {
       private char val;
       public ConstantChar(char val) {
           this.val = val;
       }

       public char getValue() {
           return val;
       }      
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /**
    * Represents a string constant in the program tree.
    */
    public static class ConstantString extends Constant {
       private String val;
       public ConstantString(String val) {
           this.val = val;
       }

       public String getValue() {
           return val;
       }      
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /* unary expressions */
    
    public static abstract class UnaryExp extends Exp implements LinkToSource {
        
        /* private member variables */
        
        private Exp op;
        private String linkToSource;
        
        /* constructors */

        public UnaryExp(Exp op) {
            this(op, null);
        }

        public UnaryExp(Exp op, String linkToSource) {
            this.op = op;
            this.linkToSource = linkToSource;
        }
        
        /* getters */

        public String getLinkToSource() {
            return linkToSource;
        }

        public Exp getOp() {
            return op;
        }
    }
    
    /* unary expressions - arithmetic */
    
    public static class SignChange extends UnaryExp {

        public SignChange(Exp op) {
            super(op);
        }
        
        public SignChange(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
    }
    
    /* unary expressions - logical */
    
    public static class Not extends UnaryExp {
        public Not(Exp op) {
            super(op);
        }
        public Not(Exp op, String linkToSource) {
            super(op, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
    }
    
    /* unary expressions - explicit type conversion */
    
    public static class ConversionInt extends UnaryExp {

        public ConversionInt(Exp op) {
            super(op);
        }
        
        public ConversionInt(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class ConversionBool extends UnaryExp {

        public ConversionBool(Exp op) {
            super(op);
        }
        
        public ConversionBool(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class ConversionReal extends UnaryExp {

        public ConversionReal(Exp op) {
            super(op);
        }
        
        public ConversionReal(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class ConversionChar extends UnaryExp {

        public ConversionChar(Exp op) {
            super(op);
        }
        
        public ConversionChar(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class ConversionString extends UnaryExp {

        public ConversionString(Exp op) {
            super(op);
        }
        
        public ConversionString(Exp op, String linkToSource) {
            super(op, linkToSource);
        }

        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    /* binary expressions */

    /**
    * Intermediate class representing a general binary expression (addition,
    * multiplication, etc.) in the program tree.
    */
    public static abstract class BinaryExp extends Exp implements LinkToSource {

       /* private member variables */

       private Exp op1;
       private Exp ap2;
       private String linkToSource;

       /* constructors */

       public BinaryExp(Exp op1, Exp op2) {
           this(op1, op2, null);
       }

       public BinaryExp(Exp op1, Exp op2, String linkToSource) {
           this.op1 = op1;
           this.ap2 = op2;
           this.linkToSource = linkToSource;  
       }

       /* getters */

       public String getLinkToSource() {
           return linkToSource;
       }  
       public Exp getOp1() {
           return op1;
       }
       public Exp getOp2() {
           return ap2;
       }
    }
    
    /* binary expressions - miscellaneous */
    
    public static class ChainElement extends BinaryExp {
        public ChainElement(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public ChainElement(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
    }
    
    /* binary expressions - arithmetic */
    
    public static abstract class BinaryArithmeticExp extends BinaryExp {
        public BinaryArithmeticExp(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public BinaryArithmeticExp(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
    }
    
    /**
    * Represents a sum expression in the program tree.
    */
    public static class Sum extends BinaryArithmeticExp {

       public Sum(Exp op1, Exp op2) {
           this(op1, op2, null);
       }

       public Sum(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }

       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /**
    * Represents a difference expression in the program tree.
    */
    public static class Difference extends BinaryArithmeticExp {

       public Difference(Exp op1, Exp op2) {
           this(op1, op2, null);
       }

       public Difference(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }

       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /**
    * Represents a division expression in the program tree.
    */
    public static class Quotient extends BinaryArithmeticExp {

       public Quotient(Exp op1, Exp op2) {
           this(op1, op2, null);
       }

       public Quotient(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }

       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }

    /**
    * Represents a product expression in the program tree.
    */
    public static class Product extends BinaryArithmeticExp {

       public Product(Exp op1, Exp op2) {
           this(op1, op2, null);
       }

       public Product(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }

       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /**
    * Represents a modulo expression in the program tree.
    */
    public static class Rest extends BinaryArithmeticExp {

       public Rest(Exp op1, Exp op2) {
           this(op1, op2, null);
       }

       public Rest(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }

       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
    
    /* binary expressions - relational */
    
    public static abstract class BinaryRelationalExp extends BinaryExp {
        public BinaryRelationalExp(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public BinaryRelationalExp(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
    }
    
    public static class Equal extends BinaryRelationalExp {
        public Equal(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public Equal(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class Unequal extends BinaryRelationalExp {
        public Unequal(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public Unequal(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class Less extends BinaryRelationalExp {
        public Less(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public Less(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class LessEqual extends BinaryRelationalExp {
        public LessEqual(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public LessEqual(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class Greater extends BinaryRelationalExp {
        public Greater(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public Greater(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    public static class GreaterEqual extends BinaryRelationalExp {
        public GreaterEqual(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public GreaterEqual(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    /* binary expressions - logical */
    
    public static abstract class BinaryLogicalExp extends BinaryExp {
        public BinaryLogicalExp(Exp op1, Exp op2) {
            super(op1, op2);
        }
        public BinaryLogicalExp(Exp op1, Exp op2, String linkToSource) {
            super(op1, op2, linkToSource);
        }
    }

    /**
    * Represents a logical 'and' in the program tree.
    */
    public static class And extends BinaryLogicalExp {
       public And(Exp op1, Exp op2) {
            this(op1, op2, null);
       }
       public And(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }

    /**
    * Represents a logical 'or' in the program tree.
    */
    public static class Or extends BinaryLogicalExp {
       public Or(Exp op1, Exp op2) {
            this(op1, op2, null);
       }
       public Or(Exp op1, Exp op2, String linkToSource) {
           super(op1, op2, linkToSource);  
       }
       @Override
       public void accept(Visitor v) {
           v.visit(this); 
       }
    }
}