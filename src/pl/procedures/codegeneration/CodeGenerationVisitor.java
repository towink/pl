package pl.procedures.codegeneration;

import pl.virtualmachine.VirtualMachine;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Program;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.procedures.Visitor;
import pl.type.Type.*;

/**
 * Visitor class which generates code for the virtual machine (machine code)
 * Only call on a program which is already type checked!
 * 
 */
public class CodeGenerationVisitor extends Visitor {
    
    private VirtualMachine machine; 

    /**
     * Contructs a visitor for code generation.
     * @param machine The virtual machine to add the code to.
     */
    public CodeGenerationVisitor(VirtualMachine machine) {
        this.machine = machine; 
    }
    
    /* program */
    
    @Override
    public void visit(Program prog) {
        prog.getInstruction().accept(this);
    }
    
    /* instructions */
    
    @Override
    public void visit(InstructionAssignment assig) {
        assig.getMem().accept(this);
        assig.getExp().accept(this);
        if(assig.getExp().isMem()) {
            machine.addInstruction(
                machine.copy(((DefinedType)assig.getExp().getType()).getSize()));
        }
        else {
            //machine.addInstruction(machine.popAndStore());
        }
    }
    
    @Override
    public void visit(InstructionBlock block) {
        for(Instruction inst : block.getInsts()) {
            inst.accept(this);
        }
    }
    
    @Override
    public void visit(InstructionWrite inst) {
        inst.getExp().accept(this);
        machine.addInstruction(machine.write());
    }
    
    @Override
    public void visit(InstructionWhile inst) {
        inst.getCondition().accept(this);
        machine.addInstruction(machine.jumpIfFalse(inst.getNextInstruction()));
        inst.getBody().accept(this);
        machine.addInstruction(machine.jump(inst.getFirstInstruction()));
    }
    
    @Override
    public void visit(InstructionIfThen inst) {
        inst.getCondition().accept(this);
        machine.addInstruction(machine.jumpIfFalse(inst.getNextInstruction()));
        inst.getBody().accept(this);
    }
    
    @Override
    public void visit(InstructionIfThenElse inst) {
        inst.getCondition().accept(this);
        machine.addInstruction(
            machine.jumpIfFalse(inst.getBodyElse().getFirstInstruction()));
        inst.getBodyIf().accept(this);
        machine.addInstruction(machine.jump(inst.getNextInstruction()));
        inst.getBodyElse().accept(this);
    }
    
    @Override
    public void visit(InstructionSwitch inst) {
        inst.getExp().accept(this);
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getLiteral().accept(this);
            machine.addInstruction(machine.equalPop1());
            // +1, because we add another jump imediately after the
            // instruction's code
            machine.addInstruction(
                machine.jumpIfFalse(c.getInst().getNextInstruction() + 1));
            c.getInst().accept(this);
            machine.addInstruction(machine.jump(inst.getNextInstruction()));
        }
        inst.getDefaultInst().accept(this);
    }
    
    /* constants */
    
    @Override
    public void visit(ConstantInt exp) {
        machine.addInstruction(machine.pushInt(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantBool exp) {
        machine.addInstruction(machine.pushBool(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantReal exp) {
        machine.addInstruction(machine.pushReal(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantChar exp) {
        machine.addInstruction(machine.pushChar(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantString exp) {
        machine.addInstruction(machine.pushString(exp.getValue()));         
    }
    
    /* variables */
    
    @Override
    public void visit(Variable var) {
        machine.addInstruction(
            machine.loadPush(var.getDec().getDir()));         
    }
    
    /* unary expressions - miscellaneous */
    
    @Override
    public void visit(SignChange exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.signChange());
    }
    
    /* unary expressions - logical */
    
    @Override
    public void visit(Not exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.not());
    }
    
    /* unary expressions - explicit type conversion */
    
    @Override
    public void visit(ConversionInt exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.convertInt());
    }
    
    @Override
    public void visit(ConversionBool exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.convertBool());
    }
    
    @Override
    public void visit(ConversionChar exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.convertChar());
    }
    
    @Override
    public void visit(ConversionReal exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.convertReal());
    }
    
    @Override
    public void visit(ConversionString exp) {
        exp.getOp().accept(this);
        machine.addInstruction(machine.convertString());
    }
    
    /* binary expressions - miscellaneous */
    
    @Override
    public void visit(ChainElement exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        machine.addInstruction(machine.chainElement());
    }
    
    /* binary operations - arithmetic */
    
    @Override
    public void visit(Sum exp) {
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.addInt());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.addReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.addReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.addReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeString.getInstance()) &&
            exp.getOp2().getType().equals(TypeString.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.concatString());
        }
    }
    
    @Override
    public void visit(Difference exp) {
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.subtInt());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.subtReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.subtReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.subtReal());
        }
    }
    
    @Override
    public void visit(Product exp) {
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.multInt());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.multReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.multReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.multReal());
        }         
    }
    
    @Override
    public void visit(Quotient exp) {
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.divInt());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.divReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.divReal());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.divReal());
        }         
    }
    
    @Override
    public void visit(Rest exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        machine.addInstruction(machine.mod());         
    }
    
    /* binary operations - relational */
    
    @Override
    public void visit(Equal exp) {
        // mixed numericals require type conversions
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.equal());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.equal());
        }
        // otherwise the types are equal (assured by previous type check) and
        // this code is safe
        else {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.equal());
        }
    }
    
    @Override
    public void visit(Unequal exp) {
        // mixed numericals require type conversions
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.unequal());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.unequal());
        }
        // otherwise the types are equal (assured by previous type check) and
        // this code is safe
        else {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.unequal());
        }
    }
    
    @Override
    public void visit(Less exp) {
        // mixed numericals require type conversions
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.less());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.less());
        }
        // otherwise the types are equal (assured by previous type check) and
        // this code is safe
        else {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.less());
        }
    }
    
    @Override
    public void visit(LessEqual exp) {
        // mixed numericals require type conversions
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.lessEqual());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.lessEqual());
        }
        // otherwise the types are equal (assured by previous type check) and
        // this code is safe
        else {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.lessEqual());
        }
    }
    
    @Override
    public void visit(Greater exp) {
        // mixed numericals require type conversions
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.greater());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.greater());
        }
        // otherwise the types are equal (assured by previous type check) and
        // this code is safe
        else {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.greater());
        }
    }
    
    @Override
    public void visit(GreaterEqual exp) {
        // mixed numericals require type conversions
        if(
            exp.getOp1().getType().equals(TypeInt.getInstance()) &&
            exp.getOp2().getType().equals(TypeReal.getInstance())
        ) {
            exp.getOp1().accept(this);
            machine.addInstruction(machine.convertReal());
            exp.getOp2().accept(this);
            machine.addInstruction(machine.greaterEqual());
        }
        else if(
            exp.getOp1().getType().equals(TypeReal.getInstance()) &&
            exp.getOp2().getType().equals(TypeInt.getInstance())
        ) {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.convertReal());
            machine.addInstruction(machine.greaterEqual());
        }
        // otherwise the types are equal (assured by previous type check) and
        // this code is safe
        else {
            exp.getOp1().accept(this);
            exp.getOp2().accept(this);
            machine.addInstruction(machine.greaterEqual());
        }
    }
    
    /* binary operations - logical */
    
    @Override
    public void visit(And exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        machine.addInstruction(machine.and());
    }
    
    @Override
    public void visit(Or exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        machine.addInstruction(machine.or());         
    }
    
}