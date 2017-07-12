package pl.procedures.codegeneration;

import java.util.Stack;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.virtualmachine.VirtualMachine;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Program;
import pl.abstractsyntax.Inst;
import pl.abstractsyntax.Inst.*;
import pl.procedures.Visitor;
import pl.type.Type;
import pl.type.Type.*;
import pl.virtualmachine.VirtualMachine.MachineInstruction;

/**
 * Visitor class which generates code for the virtual machine (machine code)
 * Only call on a program which is already type checked!
 * 
 */
public class CodeGenerationVisitor extends Visitor {
    
    private VirtualMachine machine; 
    private Stack<DeclarationProc> pendingProcs;
    private boolean debug;

    /**
     * Contructs a visitor for code generation.
     * @param machine The virtual machine to add the code to.
     * @param debug If debugging instructions are generated or not.
     */
    public CodeGenerationVisitor(VirtualMachine machine, boolean debug) {
        this.machine = machine; 
        pendingProcs = new Stack<>();
        this.debug = debug;
    }
    
    /**
     * Contructs a visitor for code generation.
     * @param machine The virtual machine to add the code to.
     */
    public CodeGenerationVisitor(VirtualMachine machine) {
        this(machine, false);
    }
    
    // just to save some typing ...
    private void add(MachineInstruction i) { machine.addInstruction(i); }
    
    /* program */
    
    @Override
    public void visit(Program prog) {
        for(Declaration dec : prog.getDeclarations()) {
            if(dec.isDecProc()) {
                pendingProcs.push(dec.toDecProc());
            }
        }
        prog.getInstruction().accept(this);
        add(machine.stop());
        while(!pendingProcs.isEmpty()) {
            pendingProcs.pop().accept(this);
        }
    }
    
    /* declaration */
    
    @Override
    public void visit(DeclarationProc dec) {
        dec.getBody().accept(this);
        if(debug) add(machine.debug(dec.getLinkToSource()));
        add(machine.deactivate(dec.getLevel(), dec.getSize()));
        add(machine.popJump());
    }
    
    /* instructions */
    
    /* instructions - general */
    
    @Override
    public void visit(InstructionAssignment assig) {
        assig.getMem().accept(this);
        assig.getExp().accept(this);
        if(debug) add(machine.debug(assig.getLinkToSource()));
        if(assig.getExp().isMem()) {
            add(machine.copy(((DefinedType)assig.getExp().getType()).getSize()));
        }
        else {
            add(machine.pop2Store());
        }
    }
    
    @Override
    public void visit(InstructionBlock block) {
        for(Declaration dec : block.getDecs()) {
            if(dec.isDecProc()) {
                pendingProcs.push(dec.toDecProc());
            }
        }
        for(Inst inst : block.getInsts()) {
            inst.accept(this);
        }
    }
    
    @Override
    public void visit(InstructionCall call) {
        if(debug) add(machine.debug(call.getLinkToSource()));
        DeclarationProc decProc = call.getDecProc();
        add(machine.activate(
                decProc.getLevel(),
                decProc.getSize(),
                call.getNextInstruction()
            )
        );
        for(int i = 0; i < decProc.getParams().size(); i++) {
            add(machine.duplicate()); 
            add(machine.pushInt(decProc.getParams().get(i).getDir()));
            add(machine.addInt());
            call.getArgs().get(i).accept(this);
            if(debug) add(machine.debug(call.getLinkToSource()));
            if( decProc.getParams().get(i).isParamByRef() ||
                !call.getArgs().get(i).isMem()
            ) {
               add(machine.pop2Store()); 
            }
            else {
               add(machine.copy(decProc.getParams().get(i).getType().getSize()));
           }
        }
        add(machine.setDisplay(decProc.getLevel()));
        add(machine.jump(decProc.getBody().getFirstInstruction()));      
   }
    
    /* instructions - IO */
    
    @Override
    public void visit(InstructionRead inst) {
        // pushes address of mem
        inst.getMem().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        // pushes values read from console
        add(machine.read(inst.getMem().getType()));
        // stores value in address and pops both
        add(machine.pop2Store());
    }
    
    @Override
    public void visit(InstructionWrite inst) {
        inst.getExp().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        if(inst.getExp().isMem()) {
            add(machine.popLoadPush());
        }
        add(machine.write());
    }
    
    /* instructions - memory */
    
    @Override
    public void visit(InstructionNew inst) {
        inst.getMem().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        add(machine.alloc(
                inst.getMem().getType().toPointer().getBaseType().getSize()
            )
        );
        add(machine.pop2Store());
    }
    
    @Override
    public void visit(InstructionFree inst) {
        inst.getMem().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        add(machine.popLoadPush());
        add(machine.dealloc(
                inst.getMem().getType().toPointer().getBaseType().getSize()
            )
        );
    }
    
    /* instructions - control structures */
    
    @Override
    public void visit(InstructionWhile inst) {
        inst.getCondition().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        if(inst.getCondition().isMem()) {
            add(machine.popLoadPush());
        }
        add(machine.jumpIfFalse(inst.getNextInstruction()));
        inst.getBody().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        add(machine.jump(inst.getFirstInstruction()));
    }
    
    @Override
    public void visit(InstructionDoWhile inst) {
        inst.getBody().accept(this);
        inst.getCondition().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        if(inst.getCondition().isMem()) {
            add(machine.popLoadPush());
        }
        add(machine.jumpIfTrue(inst.getFirstInstruction()));
    }
    
    @Override
    public void visit(InstructionIfThen inst) {
        inst.getCondition().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        if(inst.getCondition().isMem()) {
            add(machine.popLoadPush());
        }
        add(machine.jumpIfFalse(inst.getNextInstruction()));
        inst.getBody().accept(this);
    }
    
    @Override
    public void visit(InstructionIfThenElse inst) {
        inst.getCondition().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        if(inst.getCondition().isMem()) {
            add(machine.popLoadPush());
        }
        add(machine.jumpIfFalse(inst.getBodyElse().getFirstInstruction()));
        inst.getBodyIf().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        add(machine.jump(inst.getNextInstruction()));
        inst.getBodyElse().accept(this);
    }
    
    @Override
    public void visit(InstructionSwitch inst) {
        inst.getExp().accept(this);
        if(debug) add(machine.debug(inst.getLinkToSource()));
        if(inst.getExp().isMem()) {
            add(machine.popLoadPush());
        }
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getLiteral().accept(this);
            if(debug) add(machine.debug(inst.getLinkToSource()));
            add(machine.equalPop1());
            // +1, because we add another jump imediately after the
            // instruction's code
            add(machine.jumpIfFalse(c.getInst().getNextInstruction() + 1));
            c.getInst().accept(this);
            add(machine.jump(inst.getNextInstruction()));
        }
        if(inst.getDefaultInst() != null) {
            inst.getDefaultInst().accept(this);
        }
    }
    
    /* expressions */
    
    /* expressions - constants */
    
    @Override
    public void visit(ConstantInt exp) {
        if(debug) add(machine.debug(exp.getLinkToSource()));
        add(machine.pushInt(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantBool exp) {
        if(debug) add(machine.debug(exp.getLinkToSource()));
        add(machine.pushBool(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantReal exp) {
        if(debug) add(machine.debug(exp.getLinkToSource()));
        add(machine.pushReal(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantChar exp) {
        if(debug) add(machine.debug(exp.getLinkToSource()));
        add(machine.pushChar(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantString exp) {
        if(debug) add(machine.debug(exp.getLinkToSource()));
        add(machine.pushString(exp.getValue()));         
    }
    
    @Override
    public void visit(ConstantNull exp) {
        if(debug) add(machine.debug(exp.getLinkToSource()));
        add(machine.pushNullPointerValue());         
    }
    
    /* expressions - mems */
    
    @Override
    public void visit(Variable var) {
        if(debug) add(machine.debug(var.getLinkToSource()));
        DeclarationVariable dec = var.getDec();
        if(dec.getLevel() == 0) {
            add(machine.pushInt(dec.getDir()));
        }
        else {
            add(machine.pushDisplay(dec.getLevel()));
            add(machine.pushInt(dec.getDir()));
            add(machine.addInt());
            if(dec.isParamByRef()) {
                add(machine.popLoadPush());
            }
        }
    }
    
    @Override
    public void visit(Dereference dref) {
        dref.getMem().accept(this);
        if(debug) add(machine.debug(dref.getLinkToSource()));
        add(machine.popLoadPush());
    }
    
    @Override
    public void visit(Select sel) {
        sel.getMem().accept(this);
        if(debug) add(machine.debug(sel.getLinkToSource()));
        int offset = sel.getMem().getType().toRecord().getFieldByIndet(sel.getField()).getOffset();
        add(machine.pushInt(offset));
        add(machine.addInt());
    }
    
    @Override
    public void visit(Index index) {
        // get dimension and base type size
        int dim = index.getMem().getType().toArray().getDim();
        int baseTypeSize = index.getMem().getType().toArray().getBaseType().getSize();
        index.getMem().accept(this);
        index.getExp().accept(this);
        if(debug) add(machine.debug(index.getLinkToSource()));
        if(index.getExp().isMem()) {
            add(machine.popLoadPush());
        }
        // add range checking instruction after generating code that pushes the
        // index we want to access
        add(machine.inRange(dim));
        add(machine.pushInt(baseTypeSize));
        add(machine.multInt());
        add(machine.addInt());
    }
    
    /* expressions - unary */
    
    private void generateUnaryExpression(UnaryExp exp, MachineInstruction inst) {
        exp.getOp().accept(this);
        if(debug) add(machine.debug(exp.getLinkToSource()));
        if(exp.getOp().isMem()) {
            add(machine.popLoadPush());
        }
        add(inst);
    }
    
    /* expressions - unary - arithmetic */
    
    @Override
    public void visit(SignChange exp) {
        generateUnaryExpression(exp, machine.signChange());
    }
    
    /* expressions - unary - logical */
    
    @Override
    public void visit(Not exp) {
        generateUnaryExpression(exp, machine.not());
    }
    
    /* expressions - unary - explicit type conversion */
    
    @Override
    public void visit(ConversionInt exp) {
        generateUnaryExpression(exp, machine.convertInt());
    }
    
    @Override
    public void visit(ConversionBool exp) {
        generateUnaryExpression(exp, machine.convertBool());
    }
    
    @Override
    public void visit(ConversionChar exp) {
        generateUnaryExpression(exp, machine.convertChar());
    }
    
    @Override
    public void visit(ConversionReal exp) {
        generateUnaryExpression(exp, machine.convertReal());
    }
    
    @Override
    public void visit(ConversionString exp) {
        generateUnaryExpression(exp, machine.convertString());
    }
    
    /* expressions - binary */
    
    private void generateBinaryExpression(BinaryExp exp, MachineInstruction inst) {
        exp.getOp1().accept(this);
        if(debug) add(machine.debug(exp.getLinkToSource()));
        if(exp.getOp1().isMem()) {
            add(machine.popLoadPush());
        }
        exp.getOp2().accept(this);
        if(debug) add(machine.debug(exp.getLinkToSource()));
        if(exp.getOp2().isMem()) {
            add(machine.popLoadPush());
        }
        add(inst);
    }
    
    /* expressions - binary - miscellaneous */
    
    @Override
    public void visit(ChainElement exp) {
        generateBinaryExpression(exp, machine.chainElement());
    }
    
    /* expressions - binary - arithmetic */
    
    private void generateBinaryArithmeticExpression(
            BinaryArithmeticExp exp,
            MachineInstruction instInt,
            MachineInstruction instReal
    ) {
        if( exp.getOp1().getType() == Type.INT &&
            exp.getOp2().getType() == Type.INT
        ) {
            generateBinaryExpression(exp, instInt);
        }
        else{
            generateBinaryExpression(exp, instReal);
        }
        
    }
    
    @Override
    public void visit(Sum exp) {
        if( exp.getOp1().getType() == Type.STRING &&
            exp.getOp2().getType() == Type.STRING
        ) {
            generateBinaryExpression(exp, machine.concatString());
        }
        else {
            generateBinaryArithmeticExpression(exp, machine.addInt(), machine.addReal());
        }
    }
    
    @Override
    public void visit(Difference exp) {
        generateBinaryArithmeticExpression(exp, machine.subtInt(), machine.subtReal());
    }
    
    @Override
    public void visit(Product exp) {
        generateBinaryArithmeticExpression(exp, machine.multInt(), machine.multReal());
    }
    
    @Override
    public void visit(Quotient exp) {
        generateBinaryArithmeticExpression(exp, machine.divInt(), machine.divReal());
    }
    
    @Override
    public void visit(Rest exp) {
        generateBinaryExpression(exp, machine.mod());
    }
    
    /* expressions - binary - relational */
    
    @Override
    public void visit(Equal exp) {
        generateBinaryExpression(exp, machine.equal());
    }
    
    @Override
    public void visit(Unequal exp) {
        generateBinaryExpression(exp, machine.unequal());
    }
    
    @Override
    public void visit(Less exp) {
        generateBinaryExpression(exp, machine.less());
    }
    
    @Override
    public void visit(LessEqual exp) {
       generateBinaryExpression(exp, machine.lessEqual());
    }
    
    @Override
    public void visit(Greater exp) {
        generateBinaryExpression(exp, machine.greater());
    }
    
    @Override
    public void visit(GreaterEqual exp) {
       generateBinaryExpression(exp, machine.greaterEqual());
    }
    
    /* expressions - binary - logical */
    
    @Override
    public void visit(And exp) {
        generateBinaryExpression(exp, machine.and());
    }
    
    @Override
    public void visit(Or exp) {
        generateBinaryExpression(exp, machine.or());
    }
    
}