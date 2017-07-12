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
public abstract class Inst
        extends AbstractSyntaxNode
        implements LinkToSource
{
    
    private String linkToSource;
    
    public Inst() { this.linkToSource = NO_LINK_PROVIDED; }
    
    public Inst(String linkToSource) {
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
    public static class InstructionAssignment extends Inst {
        
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
    public static class InstructionBlock extends Inst {
        
        private ArrayList<Declaration> decs;
        private ArrayList<Inst> insts;
        
        public InstructionBlock(ArrayList<Inst> insts) {
            this(new ArrayList<Declaration>(), insts);
        }
        
        public InstructionBlock(
                ArrayList<Declaration> decs,
                ArrayList<Inst> insts
        ) {
            this.decs = decs;
            this.insts = insts;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public ArrayList<Inst> getInsts() { return insts; }
        public ArrayList<Declaration> getDecs() { return decs; }
        
        
    }
    
    public static class InstructionCall extends Inst {
        
        private String identProc;
        private ArrayList<Exp> args;
        private DeclarationProc decProc;

        public InstructionCall(String identProc, ArrayList<Exp> args) {
            this.identProc = identProc;
            this.args = args;
        }

        public InstructionCall(
                String identProc,
                ArrayList<Exp> args,
                String linkToSource
        ) {
            super(linkToSource);
            this.identProc = identProc;
            this.args = args;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public String getIdentProc() { return identProc; }
        public ArrayList<Exp> getArgs() { return args; }
        public DeclarationProc getDecProc() { return decProc; }

        public void setDecProc(DeclarationProc decProc) {
            this.decProc = decProc;
        }
        
    }
    
    /* insttructions - IO */

    /**
     * Represents an instruction which prints the value of a given expression.
     */
    public static class InstructionWrite extends Inst {
        
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
    public static class InstructionRead extends Inst {
        
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
    public static class InstructionNew extends Inst {
        
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
    public static class InstructionFree extends Inst {
        
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
     * Inst representing a while loop.
     */
    public static class InstructionWhile extends Inst {
        
        private Exp condition;
        private Inst body;
        
        public InstructionWhile(Exp condition, Inst body) {
            this.condition = condition;
            this.body = body;
        }
        
        public InstructionWhile(
                Exp condition,
                Inst body,
                String linkToSource
        ) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getCondition() { return condition; }
        public Inst getBody() { return body; }
        
    }
    
    /**
     * Inst representing a do while loop.
     */
    public static class InstructionDoWhile extends Inst {
        
        private Exp condition;
        private Inst body;
        
        public InstructionDoWhile(Exp condition, Inst body) {
            this.condition = condition;
            this.body = body;
        }
        
        public InstructionDoWhile(
                Exp condition,
                Inst body,
                String linkToSource
        ) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getCondition() { return condition; }
        public Inst getBody() { return body; }
        
    }

    /**
     * Inst representing an if-then statement.
     */
    public static class InstructionIfThen extends Inst {
        
        private Exp condition;
        private Inst body;
        
        public InstructionIfThen(Exp condition, Inst body) {
            this.condition = condition;
            this.body = body;
        }
        
        public InstructionIfThen(
                Exp condition,
                Inst body,
                String linkToSource
        ) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getCondition() { return condition; }
        public Inst getBody() { return body; }
        
    }

    /**
     * Inst representing an if-then-else statement.
     */
    public static class InstructionIfThenElse extends Inst {
        
        private Exp condition;
        private Inst body1;
        private Inst body2;
        
        public InstructionIfThenElse(
                Exp condition,
                Inst body1,
                Inst body2
        ) {
            this.condition = condition;
            this.body1 = body1;
            this.body2 = body2;
        }
        
        public InstructionIfThenElse(
                Exp condition,
                Inst body1,
                Inst body2,
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
        public Inst getBodyIf() { return body1; }
        public Inst getBodyElse() { return body2; }
        
    }

    /**
     * Instrcution representing the switch statement.
     */
    public static class InstructionSwitch extends Inst {
        
        private Exp exp;
        private ArrayList<Case> cases;
        private Inst defaultInst;
        
        public InstructionSwitch(
                Exp exp,
                ArrayList<Case> cases,
                Inst defaultInst,
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
                String linkToSource
        ) {
            this(exp, cases, null, linkToSource);
        }
        
        public InstructionSwitch(
                Exp exp,
                ArrayList<Case> cases,
                Inst defaultInst
        ) {
            this.exp = exp;
            this.cases = cases;
            this.defaultInst = defaultInst;
        }
        
        public InstructionSwitch(
                Exp exp,
                ArrayList<Case> cases
        ) {
            this.exp = exp;
            this.cases = cases;
            this.defaultInst = null;
        }
        
        public static class Case implements LinkToSource {
            
            private Constant literal;
            private Inst inst;
            private String linkToSource;
            
            public Case(Constant c, Inst inst) {
                this(c, inst, NO_LINK_PROVIDED);
            }
            
            public Case(Constant c, Inst inst, String linkToSource) {
                this.literal = c;
                this.inst = inst;
                this.linkToSource = linkToSource;
            }
            
            @Override
            public String getLinkToSource() { return linkToSource; }
            
            public Constant getLiteral() { return literal; }
            public Inst getInst() { return inst; }
            
        }
        
        @Override
        public void accept(Visitor v) { v.visit(this); }
        
        public Exp getExp() { return exp; }
        public Inst getDefaultInst() { return defaultInst; }
        public ArrayList<Case> getCases() { return cases; }
        
        public void setCases(ArrayList<Case> cases) { this.cases = cases; }
        
    }

}