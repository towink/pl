package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.abstractsyntax.Exp.Constant;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Program.AbstractSyntaxNode;
import java.util.ArrayList;

/**
 * Base class for all types of insts.
 * In general, an intruction is linkable to a position in the source code.
 */
public abstract class Instruction
        extends AbstractSyntaxNode
        implements LinkToSource
{
    
    private String linkToSource;
    
    public Instruction() { this.linkToSource = NO_LINK_PROVIDED; }
    
    public Instruction(String linkToSource) {
        this.linkToSource = linkToSource;
    }
    
    @Override
    public String getLinkToSource() { return linkToSource; }
    
    public abstract void accept(Visitor v);
    
    /* insttructions - general */
    
    /**
     * Represents an assignment of an expression to a position in memory (for
     * example a variable).
     */
    public static class InstructionAssignment extends Instruction {
        
        private Exp exp;
        private Mem mem;
        
        public InstructionAssignment(Mem mem, Exp exp) {
            this.exp = exp;
            this.mem = mem;
        }
        
        public InstructionAssignment(Mem mem, Exp exp, String linkToSource) {
            super(linkToSource);
            this.exp = exp;
            this.mem = mem;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getExp() { return exp; }
        public Mem getMem() { return mem; }
        
    }

    /**
     * Represents a block of insts.
     */
    public static class InstructionBlock extends Instruction {
        
        private Declaration[] decs;
        private ArrayList<Instruction> insts;
        
        public InstructionBlock(ArrayList<Instruction> insts) {
            this(new Declaration[0], insts);
        }
        
        public InstructionBlock(
                Declaration[] decs,
                ArrayList<Instruction> insts
        ) {
            this.decs = decs;
            this.insts = insts;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public ArrayList<Instruction> getInsts() { return insts; }
        public Declaration[] getDecs() { return decs; }
        
        
    }
    
    public static class InstructionCall extends Instruction {
        
        private String identProc;
        private Exp[] args;
        private DeclarationProc decProc;

        public InstructionCall(String identProc, Exp[] args) {
            this.identProc = identProc;
            this.args = args;
        }

        public InstructionCall(
                String identProc,
                Exp[] args,
                String linkToSource
        ) {
            super(linkToSource);
            this.identProc = identProc;
            this.args = args;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public String getIdentProc() { return identProc; }
        public Exp[] getArgs() { return args; }
        public DeclarationProc getDecProc() { return decProc; }

        public void setDecProc(DeclarationProc decProc) {
            this.decProc = decProc;
        }
        
    }
    
    /* insttructions - IO */

    /**
     * Represents an instruction which prints the value of a given expression.
     */
    public static class InstructionWrite extends Instruction {
        
        private Exp exp;
        
        public InstructionWrite(Exp exp) { this.exp = exp; }
        
        public InstructionWrite(Exp exp, String linkToSource) {
            super(linkToSource);
            this.exp = exp;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getExp() { return exp; }
        
    }
    
    /**
     * Represents an instruction which read a value from the console and stores 
     * it in memory.
     */
    public static class InstructionRead extends Instruction {
        
        private Mem mem;
        
        public InstructionRead(Mem mem) { this.mem = mem; }
        
        public InstructionRead(Mem mem, String linkToSource) {
            super(linkToSource);
            this.mem = mem;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Mem getMem() { return mem; }
        
    }

    /* insttructions - memory */

    /**
     * Memory allocation instruction.
     */
    public static class InstructionNew extends Instruction {
        
        private Mem mem;
        
        public InstructionNew(Mem mem) { this.mem = mem; }
        
        public InstructionNew(Mem mem, String linkToSource) {
            super(linkToSource);
            this.mem = mem;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Mem getMem() {return mem; }
        
    }

    /**
     * Memory deallocation instruction.
     */
    public static class InstructionFree extends Instruction {
        
        private Mem mem;
        
        public InstructionFree(Mem mem) { this.mem = mem; }
        
        public InstructionFree(Mem mem, String linkToSource) {
            super(linkToSource);
            this.mem = mem;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Mem getMem() { return mem; }
        
    }

    /* insttructions - control structures */

    /**
     * Instruction representing a while loop.
     */
    public static class InstructionWhile extends Instruction {
        
        private Exp condition;
        private Instruction body;
        
        public InstructionWhile(Exp condition, Instruction body) {
            this.condition = condition;
            this.body = body;
        }
        
        public InstructionWhile(
                Exp condition,
                Instruction body,
                String linkToSource
        ) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getCondition() { return condition; }
        public Instruction getBody() { return body; }
        
    }

    /**
     * Instruction representing an if-then statement.
     */
    public static class InstructionIfThen extends Instruction {
        
        private Exp condition;
        private Instruction body;
        
        public InstructionIfThen(Exp condition, Instruction body) {
            this.condition = condition;
            this.body = body;
        }
        
        public InstructionIfThen(
                Exp condition,
                Instruction body,
                String linkToSource
        ) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getCondition() { return condition; }
        public Instruction getBody() { return body; }
        
    }

    /**
     * Instruction representing an if-then-else statement.
     */
    public static class InstructionIfThenElse extends Instruction {
        
        private Exp condition;
        private Instruction body1;
        private Instruction body2;
        
        public InstructionIfThenElse(
                Exp condition,
                Instruction body1,
                Instruction body2
        ) {
            this.condition = condition;
            this.body1 = body1;
            this.body2 = body2;
        }
        
        public InstructionIfThenElse(
                Exp condition,
                Instruction body1,
                Instruction body2,
                String linkToSource
        ) {
            super(linkToSource);
            this.condition = condition;
            this.body1 = body1;
            this.body2 = body2;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getCondition() { return condition; }
        public Instruction getBodyIf() { return body1; }
        public Instruction getBodyElse() { return body2; }
        
    }

    /**
     * Instrcution representing the switch statement.
     */
    public static class InstructionSwitch extends Instruction {
        
        private Exp exp;
        private ArrayList<Case> cases;
        private Instruction defaultInst;
        
        public InstructionSwitch(
                Exp exp,
                ArrayList<Case> cases,
                Instruction defaultInst,
                String linkToSource
        ) {
            super(linkToSource);
            this.exp = exp;
            this.cases = cases;
            this.defaultInst = defaultInst;
        }
        
        public InstructionSwitch(
                Exp exp,
                ArrayList<Case> cases,
                Instruction defaultInst
        ) {
            this.exp = exp;
            this.cases = cases;
            this.defaultInst = defaultInst;
        }
        
        public static class Case implements LinkToSource {
            
            private Constant literal;
            private Instruction inst;
            private String linkToSource;
            
            public Case(Constant c, Instruction inst) {
                this(c, inst, NO_LINK_PROVIDED);
            }
            
            public Case(Constant c, Instruction inst, String linkToSource) {
                this.literal = c;
                this.inst = inst;
                this.linkToSource = linkToSource;
            }
            
            @Override
            public String getLinkToSource() { return linkToSource; }
            
            public Constant getLiteral() { return literal; }
            public Instruction getInst() { return inst; }
            
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getExp() { return exp; }
        public Instruction getDefaultInst() { return defaultInst; }
        public ArrayList<Case> getCases() { return cases; }
        
        public void setCases(ArrayList<Case> cases) { this.cases = cases; }
        
    }

}