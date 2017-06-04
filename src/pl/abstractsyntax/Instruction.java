package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.abstractsyntax.Exp.Constant;
import pl.abstractsyntax.Declaration.*;
import java.util.ArrayList;

/**
 * Base class for all types of instructions.
 * In general, an intruction is linkable to a position in the source code.
 */
public abstract class Instruction
        extends Program.AbstractSyntaxNode
        implements LinkToSource
{
    private String linkToSource;
    public Instruction() {
        this.linkToSource = NO_LINK_PROVIDED;
    }
    public Instruction(String linkToSource) {
        this.linkToSource = linkToSource;
    }
    @Override
    public String getLinkToSource() {
        return linkToSource;
    }
    public abstract void accept(Visitor v);
    
    /* instructions - general */
    
    /**
     * Represents an assignment of an expression to a variable.
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
        public Exp getExp() {
            return exp;
        }
        public Mem getMem() {
            return mem;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /**
     * Represents a block of instructions.
     */
    public static class InstructionBlock extends Instruction {
        private ArrayList<Instruction> instructions;
        public InstructionBlock(ArrayList<Instruction> instructions) {
            this.instructions = instructions;
        }
        public ArrayList<Instruction> getInstructions() {
            return instructions;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    /* instructions - IO */

    /**
     * Represents an instructions which prints the value of a given expression.
     */
    public static class InstructionWrite extends Instruction {
        private Exp exp;
        public InstructionWrite(Exp exp) {
            this(exp, null);
        }
        public InstructionWrite(Exp exp, String linkToSource) {
            super(linkToSource);
            this.exp = exp;
        }
        public Exp getExp() {
            return exp;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /* instructions - memory */

    /**
     *
     */
    public static class InstructionNew extends Instruction {
        private Mem mem;
        public InstructionNew(Mem mem) {
            this.mem = mem;
        }
        public InstructionNew(Mem mem, String linkToSource) {
            super(linkToSource);
            this.mem = mem;
        }
        public Mem getMem() {
            return mem;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /**
     *
     */
    public static class InstructionFree extends Instruction {
        private Mem mem;
        public InstructionFree(Mem mem) {
            this.mem = mem;
        }
        public InstructionFree(Mem mem, String linkToSource) {
            super(linkToSource);
            this.mem = mem;
        }
        public Mem getMem() {
            return mem;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /* instructions - control structures */

    /**
     *
     */
    public static class InstructionWhile extends Instruction {
        private Exp condition;
        private Instruction body;
        public InstructionWhile(Exp condition, Instruction body) {
            this.condition = condition;
            this.body = body;
        }
        public InstructionWhile(Exp condition, Instruction body, String linkToSource) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        public Exp getCondition() {
            return condition;
        }
        public Instruction getBody() {
            return body;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /**
     *
     */
    public static class InstructionIfThen extends Instruction {
        private Exp condition;
        private Instruction body;
        public InstructionIfThen(Exp condition, Instruction body) {
            this.condition = condition;
            this.body = body;
        }
        public InstructionIfThen(Exp condition, Instruction body, String linkToSource) {
            super(linkToSource);
            this.condition = condition;
            this.body = body;
        }
        public Exp getCondition() {
            return condition;
        }
        public Instruction getBody() {
            return body;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /**
     *
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
            this(condition, body1, body2, null);
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
        public Exp getCondition() {
            return condition;
        }
        public Instruction getBodyIf() {
            return body1;
        }
        public Instruction getBodyElse() {
            return body2;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    /**
     *
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
            this(exp, cases, defaultInst, null);
        }
        public static class Case implements LinkToSource {
            private Constant literal;
            private Instruction inst;
            private String linkToSource;
            public Case(Constant c, Instruction inst, String linkToSource) {
                this.literal = c;
                this.inst = inst;
                this.linkToSource = linkToSource;
            }
            public Case(Constant c, Instruction inst) {
                this(c, inst, null);
            }
            public Constant getLiteral() {
                return literal;
            }
            public Instruction getInst() {
                return inst;
            }
            @Override
            public String getLinkToSource() {
                return linkToSource;
            }
        }
        public void setCases(ArrayList<Case> cases) {
            this.cases = cases;
        }
        public Exp getExp() {
            return exp;
        }
        public Instruction getDefaultInst() {
            return defaultInst;
        }
        public ArrayList<Case> getCases() {
            return cases;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

}