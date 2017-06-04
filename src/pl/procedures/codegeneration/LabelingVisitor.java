package pl.procedures.codegeneration;

import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.procedures.Visitor;
import pl.type.Type;

/**
 * MUST BE CALLED AFTER TYPE CHECK!
 */
public class LabelingVisitor extends Visitor {
    private int label;

    public LabelingVisitor() {
        label = 0;
    }
    
    /* instructions */
    
    /* instructions - general */
    
    @Override
    public void visit(InstructionAssignment assig) {
        assig.setFirstInstruction(label);
        assig.getMem().accept(this);
        assig.getExp().accept(this);
        label++; // pop2Store or copy
        assig.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionBlock block) {
        block.setFirstInstruction(label);
        for(Instruction inst : block.getInstructions()) {
            inst.accept(this);
        }
        block.setNextInstruction(label);
    }
    
    /* instructions - IO */
    
    @Override
    public void visit(InstructionWrite inst) {
        inst.setFirstInstruction(label);
        inst.getExp().accept(this);
        inst.setNextInstruction(++label);
    }
    
    /* instructions - memory */
    
    @Override
    public void visit(InstructionNew inst) {
        inst.setFirstInstruction(label);
        inst.getMem().accept(this);
        label++; // alloc(size)
        label++; // pop2store
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionFree inst) {
        inst.setFirstInstruction(label);
        inst.getMem().accept(this);
        label++; // popLoadStore
        label++; //dealloc(size)
        inst.setNextInstruction(label);
    }
    
    /* instructions - control structures */
    
    @Override
    public void visit(InstructionWhile inst) {
        inst.setFirstInstruction(label);
        inst.getCondition().accept(this);
        label++; // jump if false ... 
        inst.getBody().accept(this);
        label++; // jump to condition ...
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionIfThen inst) {
        inst.setFirstInstruction(label);
        inst.getCondition().accept(this);
        label++; // jump if false ...
        inst.getBody().accept(this);
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionIfThenElse inst) {
        inst.setFirstInstruction(label);
        inst.getCondition().accept(this);
        label++; // jump if false ...
        inst.getBodyIf().accept(this);
        label++; // jump after else block ...
        inst.getBodyElse().accept(this);
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionSwitch inst) {
        inst.setFirstInstruction(label);
        inst.getExp().accept(this);
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getLiteral().accept(this);
            label++; // equal pop 1 ...
            label++; // jump if false ...
            c.getInst().accept(this);
            label++; // jump to end ...
        }
        inst.getDefaultInst().accept(this);
        inst.setNextInstruction(label);
    }
    
    /* expressions */
    
    /* expressions - constants */
    
    @Override
    public void visit(Constant c) {
        c.setFirstInstruction(label);
        label++; // push
        c.setNextInstruction(label);
    }
    
    /* expressions - mems */
    
    @Override
    public void visit(Variable exp) {
        exp.setFirstInstruction(label);
        label++; // loadPush(addr)
        exp.setNextInstruction(label);
    }
    
    @Override
    public void visit(Dereference exp) {
        exp.setFirstInstruction(label);
        label++; // popLoadPush
        exp.setNextInstruction(label);
    }
    
    @Override
    public void visit(Select exp) {
        exp.setFirstInstruction(label);
        label++; // 
        exp.setNextInstruction(label);
    }
    
    @Override
    public void visit(Index exp) {
        exp.setFirstInstruction(label);
        label++; //
        exp.setNextInstruction(label);
    }
    
    /* expressions - unary */
    
    @Override
    public void visit(UnaryExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp().accept(this);
        label++; // e.g. not
        exp.setNextInstruction(label);
    }
    
    /* expressions - binary */
    
    @Override
    public void visit(BinaryExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp1().accept(this);
        if(exp.getOp1().isMem()) {
            label++; // popLoadPush
        }
        exp.getOp2().accept(this);
        if(exp.getOp2().isMem()) {
            label++; // popLoadPush
        }
        if(exp.getOp1().getType() != exp.getOp2().getType()) {
            // different operand types - can only be int and real (arithmetic or relational)
            label++; // convert to real
        }
        label++; // e.g. sum
        exp.setNextInstruction(label);
    }
            
}
