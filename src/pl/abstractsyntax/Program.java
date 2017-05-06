package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Exp.ConstantBool;
import pl.type.Type;
import pl.type.Type.*;
import pl.abstractsyntax.Declaration.*;
import java.util.ArrayList;
import pl.abstractsyntax.Mem.*;

/**
 * This class represents an (already parsed) program in form of a tree, which 
 * is called the tree of abstract syntax. 
 * 
 * The nodes of this tree consist of objects of the following types:
 *    - declarations of variables and types
 *    - instructions (assign, input/output, alloc/dealloc, control structures)
 *    - nodes which group several instructions into a block
 *    - expressions (literals, arithmetic, relational, logical)
 *    - special expressions related to accesing memory (variables, array index,
 *      pointer dereferenciation, record field selection)
 */
public class Program {
    
    protected ArrayList<Declaration> declarations;
    protected Instruction instruction;
    private Type type;
    
    /**
     * Creates an empty syntax tree.
     */
    public Program() {
        this.declarations = null;
        this.instruction = null;
        this.type = null;
    }

    /**
     * Creates a syntax tree with the specified declarations and intruction.
     * 
     * @param declarations The list of declarations of this program.
     * @param instruction The base instruction of this program (typically a block)
     */
    public Program(ArrayList<Declaration> declarations, Instruction instruction) {
        this.declarations = declarations;
        this.instruction = instruction;
        this.type = null;
    }
    
    /**
     * Call this method to get the list which forms the declaration section of
     * the program. This may contain both variable and type declarations.
     * 
     * @return The list of declarations of the programs.
     */
    public ArrayList<Declaration> getDeclarations() {
        return declarations;
    }

    /**
     * Call this method to retreive the program's base instruction (typically a 
     * block)
     * 
     * @return The program's instruction.
     */
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * Getter for type.
     * 
     * @return Type of the program (Type.ERROR/OK) as set by the TypeCheckVisitor.
     *         Null, if no TypeCheckVisitor was applied.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * This setter is called by the TypeCheckVisitor.
     * 
     * @param type Sets the programs type (Type.ERROR/OK).
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Call this method to apply a visitor to the syntax tree.
     * 
     * @param v Visitor to be applied to the syntax tree.
     */
    public void accept(Visitor v) {
        v.visit(this);
    }
    
    /**
     * General base class for objects representing nodes in the tree of abstract
     * syntax.
     * 
     * At the moment this is either an instruction or an expression.
     */
    public abstract static class AbstractSyntaxNode {
        protected Type type;
        protected int firstInstruction;
        protected int nextInstruction;
        public Type getType() {
            return type;
        }
        public int getFirstInstruction() {
            return firstInstruction;
        }
        public int getNextInstruction() {
            return nextInstruction;
        }
        public void setType(Type type) {
            this.type = type;
        }
        public void setFirstInstruction(int firstInstruction) {
            this.firstInstruction = firstInstruction;
        }
        public void setNextInstruction(int nextInstruction) {
            this.nextInstruction = nextInstruction;
        }
    }
    

    /* constructors for expressions, instructions,
       declarations, programs and types 
    
        DO I ACTUALLY NEED THIS ?
    */
    
    /* types */
    

    /* program */
    
    public Program prog(ArrayList<Declaration> decs, Instruction inst) {
        return new Program(decs, inst);
    }
    
    /* declarations */
    
    public DeclarationVariable decVar(DefinedType type, String var) {
        return new DeclarationVariable(var, type);
    }
    
    public DeclarationVariable decVar(DefinedType type, String var, String linkToSource) {
        return new DeclarationVariable(var, type, linkToSource);
    }
    
    /* instructions */
    
    public InstructionAssignment instAssig(Mem mem, Exp exp) {
        return new InstructionAssignment(mem, exp);
    }
    
    public InstructionAssignment instAssig(Mem mem, Exp exp, String linkToSource) {
        return new InstructionAssignment(mem, exp, linkToSource);
    }
    
    public InstructionWrite instWrite(Exp exp) {
        return new InstructionWrite(exp);
    }
    
    public InstructionWrite instWrite(Exp exp, String linkToSource) {
        return new InstructionWrite(exp, linkToSource);
    }
    
    public InstructionBlock instBlock(ArrayList<Instruction> insts) {
        return new InstructionBlock(insts);
    }
    
    public InstructionWhile instWhile(Exp condition, Instruction body) {
        return new InstructionWhile(condition, body);
    }
    
    public InstructionWhile instWhile(Exp condition, Instruction body, String linkToSource) {
        return new InstructionWhile(condition, body, linkToSource);
    }
    
    public InstructionIfThen instIfThen(Exp condition, Instruction body) {
        return new InstructionIfThen(condition, body);
    }
    
    public InstructionIfThen instIfThen(Exp condition, Instruction body, String linkToSource) {
        return new InstructionIfThen(condition, body, linkToSource);
    }
    
    public InstructionIfThenElse instIfThenElse(Exp condition, Instruction body1, Instruction body2) {
        return new InstructionIfThenElse(condition, body1, body2);
    }
    
    public InstructionIfThenElse instIfThenElse(Exp condition, Instruction body1, Instruction body2, String linkToSource) {
        return new InstructionIfThenElse(condition, body1, body2, linkToSource);
    }
    
    public InstructionSwitch instSwitch(Exp exp, ArrayList<InstructionSwitch.Case> cases, Instruction defaultInst) {
        return new InstructionSwitch(exp, cases, defaultInst);
    }
    
    public InstructionSwitch instSwitch(Exp exp, ArrayList<InstructionSwitch.Case> cases, Instruction defaultInst, String linkToSource) {
        return new InstructionSwitch(exp, cases, defaultInst, linkToSource);
    }
    
    /* constants */
    
    public Exp constantInt(int val) {return new ConstantInt(val);}
    public Exp constantBool(boolean val) {return new ConstantBool(val);}
    public Exp constantReal(double val) {return new ConstantReal(val);}
    public Exp constantChar(char val) {return new ConstantChar(val);}
    public Exp constantString(String val) {return new ConstantString(val);}
    
    /* variables */
    
    public Mem variable(String name) {return new Variable(name);}
    public Mem variable(String name, String linkToSource) {
        return new Variable(name, linkToSource);
    }
    
    /* unary operations - miscellaneous */
    
    public Exp signChange(Exp exp) {
        return new SignChange(exp);
    }
    public Exp signChange(Exp exp, String linkToSource) {
        return new SignChange(exp, linkToSource);
    }
    
    /* unary operations - logical */
    
    public Exp not(Exp exp) {
        return new Not(exp);
    }
    public Exp not(Exp exp, String linkToSource) {
        return new Not(exp, linkToSource);
    }
    
    /* unary operations - explicit conversions */
    
    public Exp conversionInt(Exp exp) {return new ConversionInt(exp);}
    public Exp conversionInt(Exp exp, String linkToSource) 
        {return new ConversionInt(exp, linkToSource);}
    
    public Exp conversionBool(Exp exp) {return new ConversionBool(exp);}
    public Exp conversionBool(Exp exp, String linkToSource)
        {return new ConversionBool(exp, linkToSource);}
    
    public Exp conversionReal(Exp exp) {return new ConversionReal(exp);}
    public Exp conversionReal(Exp exp, String linkToSource)
        {return new ConversionReal(exp, linkToSource);}
    
    public Exp conversionChar(Exp exp) {return new ConversionChar(exp);}
    public Exp conversionChar(Exp exp, String linkToSource)
        {return new ConversionChar(exp, linkToSource);}
    
    public Exp conversionString(Exp exp) {return new ConversionString(exp);}
    public Exp conversionString(Exp exp, String linkToSource)
        {return new ConversionString(exp, linkToSource);}
    
    /* binary expressions - miscellaneous */
    
    public Exp chainElement(Exp exp1, Exp exp2) {
        return new ChainElement(exp1, exp2);
    }
    public Exp chainElement(Exp exp1, Exp exp2, String linkToSource) {
        return new ChainElement(exp1, exp2, linkToSource);
    }
    
    /* binary operations - arithmetic */
    
    public Exp sum(Exp exp1, Exp exp2) {
        return new Sum(exp1, exp2);  
    }
    public Exp sum(Exp exp1, Exp exp2, String linkToSource) {
        return new Sum(exp1, exp2, linkToSource);  
    }
    
    public Exp prod(Exp exp1, Exp exp2) {
        return new Product(exp1, exp2);  
    }
    public Exp prod(Exp exp1, Exp exp2, String linkToSource) {
        return new Product(exp1, exp2, linkToSource);  
    }
    
    public Exp quot(Exp exp1, Exp exp2) {
        return new Quotient(exp1, exp2);  
    }
    public Exp quot(Exp exp1, Exp exp2, String linkToSource) {
        return new Quotient(exp1, exp2, linkToSource);  
    }
    
    public Exp diff(Exp exp1, Exp exp2) {
        return new Difference(exp1, exp2);  
    }
    public Exp diff(Exp exp1, Exp exp2, String linkToSource) {
        return new Difference(exp1, exp2, linkToSource);  
    }
    
    public Exp rest(Exp exp1, Exp exp2) {
        return new Rest(exp1, exp2);  
    }
    public Exp rest(Exp exp1, Exp exp2, String linkToSource) {
        return new Rest(exp1, exp2, linkToSource);  
    }
    
    /* binary expressions - relational */
    
    public Exp equal(Exp exp1, Exp exp2) {
        return new Equal(exp1, exp2);
    }
    public Exp equal(Exp exp1, Exp exp2, String linkToSource) {
        return new Equal(exp1, exp2, linkToSource);
    }
    
    public Exp unequal(Exp exp1, Exp exp2) {
        return new Unequal(exp1, exp2);
    }
    public Exp unequal(Exp exp1, Exp exp2, String linkToSource) {
        return new Unequal(exp1, exp2, linkToSource);
    }
    
    public Exp less(Exp exp1, Exp exp2) {
        return new Less(exp1, exp2);
    }
    public Exp less(Exp exp1, Exp exp2, String linkToSource) {
        return new Less(exp1, exp2, linkToSource);
    }
    
    public Exp lessEqual(Exp exp1, Exp exp2) {
        return new LessEqual(exp1, exp2);
    }
    public Exp lessEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new LessEqual(exp1, exp2, linkToSource);
    }
    
    public Exp greater(Exp exp1, Exp exp2) {
        return new Greater(exp1, exp2);
    }
    public Exp greater(Exp exp1, Exp exp2, String linkToSource) {
        return new Greater(exp1, exp2, linkToSource);
    }
    
    public Exp greaterEqual(Exp exp1, Exp exp2) {
        return new GreaterEqual(exp1, exp2);
    }
    public Exp greaterEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new GreaterEqual(exp1, exp2, linkToSource);
    }
    
    /* binary operations - logical */
    
    public Exp and(Exp exp1, Exp exp2) {
        return new And(exp1, exp2);  
    }
    public Exp and(Exp exp1, Exp exp2, String linkToSource) {
        return new And(exp1, exp2, linkToSource);  
    }
    
    public Exp or(Exp exp1, Exp exp2) {
        return new Or(exp1, exp2);  
    }
    public Exp or(Exp exp1, Exp exp2, String linkToSource) {
        return new Or(exp1, exp2, linkToSource);  
    }
    
    /* operations of the program tree */

    /**
     * Prints program on command line.
     */
    /*public void print() {
    PrintVisitor v = new PrintVisitor();
    this.root().accept(v);
    }*/
    
    /**
     * Performs a type check.
     * Decorates the nodes in the program tree with their correct types or error.
     */
    /*public void typeCheck() {
    TypeCheckVisitor v = new TypeCheckVisitor(this);
    this.root().accept(v);
    }*/
   
}
