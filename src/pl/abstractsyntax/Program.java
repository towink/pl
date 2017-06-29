package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Inst.*;
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
    protected Inst instruction;
    private Type type;
    
    /**
     * Creates an empty syntax tree.
     */
    public Program() {
        this.declarations = new ArrayList<>();
        this.instruction = null;
        this.type = null;
    }

    /**
     * Creates a syntax tree with the specified declarations and intruction.
     * 
     * @param declarations The list of declarations of this program.
     * @param instruction The base instruction of this program (typically a block)
     */
    public Program(ArrayList<Declaration> declarations, Inst instruction) {
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
    public Inst getInstruction() {
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
   
}
