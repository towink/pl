package pl.procedures;

import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
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
    
    @Override
    public void visit(InstructionAssignment assig) {
        assig.setFirstInstruction(label);
        assig.getExp().accept(this);
        assig.setNextInstruction(++label);
    }
    
    @Override
    public void visit(InstructionBlock block) {
        block.setFirstInstruction(label);
        for(Instruction inst : block.getInstructions()) {
            inst.accept(this);
        }
        block.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionWrite inst) {
        inst.setFirstInstruction(label);
        inst.getExp().accept(this);
        inst.setNextInstruction(++label);
    }
    
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
    
    /* constants */
    
    @Override
    public void visit(Constant c) {
        c.setFirstInstruction(label);
        c.setNextInstruction(++label);
    }
    
    /* variables */
    
    @Override
    public void visit(Variable c) {
        c.setFirstInstruction(label);
        c.setNextInstruction(++label);
    }
    
    /* unary expressions */
    
    @Override
    public void visit(UnaryExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp().accept(this);
        exp.setNextInstruction(++label);
    }
    
    /* binary expressions */
    
    @Override
    public void visit(BinaryExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        exp.setNextInstruction(++label);
    }
    
    @Override
    public void visit(BinaryArithmeticExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp1().accept(this);
        if( exp.getOp1().getType().equals(Type.TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(Type.TypeReal.getInstance())
            ) {
            // covert to real
            label++;
        }
        exp.getOp2().accept(this);
        if( exp.getOp2().getType().equals(Type.TypeInt.getInstance()) &&
            exp.getOp1().getType().equals(Type.TypeReal.getInstance())
            ) {
            // covert to real
            label++;
        }
        exp.setNextInstruction(++label);
    }
    
    @Override
    public void visit(BinaryRelationalExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp1().accept(this);
        if( exp.getOp1().getType().equals(Type.TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(Type.TypeReal.getInstance())
            ) {
            // covert to real
            label++;
        }
        exp.getOp2().accept(this);
        if( exp.getOp2().getType().equals(Type.TypeInt.getInstance()) &&
            exp.getOp1().getType().equals(Type.TypeReal.getInstance())
            ) {
            // covert to real
            label++;
        }
        exp.setNextInstruction(++label);
    }
            
}
