package pl.procedures.codegeneration;

import java.util.Stack;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Inst;
import pl.abstractsyntax.Inst.*;
import pl.abstractsyntax.Program;
import pl.procedures.Visitor;
import pl.type.Type;

/**
 * Visitor labeling instructions with their corresponding lines of machine code.
 * 
 * MUST BE CALLED AFTER TYPE CHECK!
 */
public class LabelingVisitor extends Visitor {
    
    private int label;
    private Stack<Declaration.DeclarationProc> pendingProcs;

    public LabelingVisitor() {
        label = 0;
        pendingProcs = new Stack<>();
    }
    
    /* program */
    
    @Override
    public void visit(Program prog) {
        for(Declaration dec : prog.getDeclarations()) {
            if(dec.isDecProc()) {
                pendingProcs.push(dec.toDecProc());
            }
        }
        prog.getInstruction().accept(this);
        label++; // stop
        while(!pendingProcs.isEmpty()) {
            pendingProcs.pop().accept(this);
        }
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
        for(Declaration dec : block.getDecs()) {
            if(dec.isDecProc()) {
                pendingProcs.push(dec.toDecProc());
            }
        }
        for(Inst inst : block.getInsts()) {
            inst.accept(this);
        }
        block.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionCall call) {
        call.setFirstInstruction(label);
        label++; // activate
        for(Exp arg : call.getArgs()) {
            label += 3; // duplicate, pushInt, add
            arg.accept(this);
            label++; // pop2store or copy
        }
        label += 2; // setDisplay, jump
        call.setNextInstruction(label);
    }
    
    /* instructions - IO */
    
    @Override
    public void visit(InstructionRead inst) {
        inst.setFirstInstruction(label);
        inst.getMem().accept(this);
        label += 2; // read, pop2Store
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionWrite inst) {
        inst.setFirstInstruction(label);
        inst.getExp().accept(this);
        if(inst.getExp().isMem()) {
            label++; // popLoadPush
        }
        label++; // write
        inst.setNextInstruction(label);
    }
    
    /* instructions - memory */
    
    @Override
    public void visit(InstructionNew inst) {
        inst.setFirstInstruction(label);
        inst.getMem().accept(this);
        label += 2; // alloc, pop2store
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionFree inst) {
        inst.setFirstInstruction(label);
        inst.getMem().accept(this);
        label += 2; // popLoadStore, dealloc
        inst.setNextInstruction(label);
    }
    
    /* instructions - control structures */
    
    @Override
    public void visit(InstructionWhile inst) {
        inst.setFirstInstruction(label);
        inst.getCondition().accept(this);
        if(inst.getCondition().isMem()) {
            label++; // popLoadPush
        }
        label++; // jump if false
        inst.getBody().accept(this);
        label++; // jump to condition
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionIfThen inst) {
        inst.setFirstInstruction(label);
        inst.getCondition().accept(this);
        if(inst.getCondition().isMem()) {
            label++; // popLoadPush
        }
        label++; // jump if false
        inst.getBody().accept(this);
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionIfThenElse inst) {
        inst.setFirstInstruction(label);
        inst.getCondition().accept(this);
        if(inst.getCondition().isMem()) {
            label++; // popLoadPush
        }
        label++; // jump if false
        inst.getBodyIf().accept(this);
        label++; // jump after else block
        inst.getBodyElse().accept(this);
        inst.setNextInstruction(label);
    }
    
    @Override
    public void visit(InstructionSwitch inst) {
        inst.setFirstInstruction(label);
        inst.getExp().accept(this);
        if(inst.getExp().isMem()) {
            label++; // popLoadPush
        }
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getLiteral().accept(this);
            label++; // equal pop 1
            label++; // jump if false
            c.getInst().accept(this);
            label++; // jump to end
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
        DeclarationVariable dec = exp.getDec();
        if(dec.getLevel() == 0) {
            label++; // pushInt
        }
        else {
            label += 3; // pushDisplay, pushInt, addInt
            if(dec.isParamByRef()) {
                label++; // popLoadPush
            }
        }
        exp.setNextInstruction(label);
    }
    
    @Override
    public void visit(Dereference exp) {
        exp.setFirstInstruction(label);
        exp.getMem().accept(this);
        label++; // popLoadPush
        exp.setNextInstruction(label);
    }
    
    @Override
    public void visit(Select exp) {
        exp.setFirstInstruction(label);
        exp.getMem().accept(this);
        label += 2; // pushInt, addInt
        exp.setNextInstruction(label);
    }
    
    @Override
    public void visit(Index exp) {
        exp.setFirstInstruction(label);
        exp.getMem().accept(this);
        exp.getExp().accept(this);
        label += 3; // pushInt, multInt, addInt
        exp.setNextInstruction(label);
    }
    
    /* expressions - unary */
    
    @Override
    public void visit(UnaryExp exp) {
        exp.setFirstInstruction(label);
        exp.getOp().accept(this);
        if(exp.getOp().isMem()) {
            label++; // popLoadPush
        }
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
        label++; // e.g. sum
        exp.setNextInstruction(label);
    }
            
}