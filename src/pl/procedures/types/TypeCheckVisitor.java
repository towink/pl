package pl.procedures.types;

import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Exp.*;
import pl.errors.Errors;
import pl.abstractsyntax.Program;
import pl.abstractsyntax.Inst;
import pl.abstractsyntax.Inst.*;
import pl.type.Type;
import pl.abstractsyntax.Mem.*;
import pl.procedures.Visitor;
import pl.type.TypeCompatibility;

/**
 * Visitor performing a static type check on the given program tree.
 * This is achieved by decorating each leaf with its type, traversing the
 * tree in postorder.
 */
public class TypeCheckVisitor extends Visitor {

    private Program program; // still needed??

    /**
     * Constructs the visitor.
     *
     * @param program The program tree to perform the type check on
     */
    public TypeCheckVisitor(Program program) {
        this.program = program;
    }
    
    /* program */

    @Override
    public void visit(Program p) {
        boolean ok = true;
        for(Declaration dec : p.getDeclarations()) {
            dec.accept(this);
            if(dec.isDecProc()) {
                ok = ok && dec.toDecProc().getBody().getType() == Type.OK;
            }
        }
        p.getInstruction().accept(this);
        ok = ok && p.getInstruction().getType() == Type.OK;
        if(ok) {
            p.setType(Type.OK);
        }
        else {
            p.setType(Type.ERROR);
        }
    }

    /* instructions */
    
    /* instructions - general */

    @Override
    public void visit(InstructionAssignment assig) {
        assig.getMem().accept(this);
        assig.getExp().accept(this);
        if(TypeCompatibility.check(assig.getMem().getType(), assig.getExp().getType())) {
            assig.setType(Type.OK);
        }
        else {
            if( assig.getMem().getType() != Type.ERROR &&
                assig.getExp().getType() != Type.ERROR
            ) {
                Errors.printErrorFancy(assig, Errors.ERROR_ASSIGNMENT);
            }
            assig.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(InstructionBlock block) {
        boolean ok = true;
        for(Declaration dec : block.getDecs()) {
            dec.accept(this);
            if(dec.isDecProc()) {
                ok = ok && 
                    (dec.toDecProc().getBody().getType() == Type.OK);
            }
        }
        for(Inst inst : block.getInsts()) {
            inst.accept(this);
            ok = ok && inst.getType() == Type.OK;
        }
        if(ok) {
            block.setType(Type.OK);
        }
        else {
            block.setType(Type.ERROR);
        }
    }
    
    @Override
    public void visit(InstructionCall call) {
        DeclarationParam[] params = call.getDecProc().getParams();
        Exp[] args = call.getArgs();
        if(params.length != args.length) {
            Errors.printErrorFancy(call, Errors.ERROR_NUMBER_ARGUMENTS);
            for(Exp arg : call.getArgs()) {
                arg.accept(this);
            }
            call.setType(Type.ERROR);
        }
        else {
            boolean error = false;
            for(int i = 0; i < args.length; i++) {
                if(params[i].isParamByRef() && !args[i].isMem()) {
                    Errors.printErrorFancy(call, Errors.ERROR_BY_REFERENCE);
                    error = true;
                }
                args[i].accept(this);
                if(!TypeCompatibility.check( params[i].getType(), args[i].getType())) {
                    if(args[i].getType() != Type.ERROR) {
                        Errors.printErrorFancy(
                            call,
                            Errors.ERROR_ARG_TYPE + ": " + params[i].getIdent());
                    }
                    error = true;
                }
            }
            if(error) {
                call.setType(Type.ERROR);
            }
            else {
                call.setType(Type.OK);
            }
        }
    }

    /* instructions - memory */

    @Override
    public void visit(InstructionNew inst) {
        inst.getMem().accept(this);
        if(inst.getMem().getType().isPointer()) {
            inst.setType(Type.OK);
        }
        else {
            if(inst.getMem().getType() != Type.ERROR) {
                Errors.printErrorFancy(inst, Errors.ERROR_NEW);
            }
            inst.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(InstructionFree inst) {
        inst.getMem().accept(this);
        if(inst.getMem().getType().isPointer()) {
            inst.setType(Type.OK);
        }
        else {
            if(inst.getMem().getType() != Type.ERROR) {
                Errors.printErrorFancy(inst, Errors.ERROR_FREE);
            }
            inst.setType(Type.ERROR);
        }
    }

    /* instructions - IO */

    @Override
    public void visit(InstructionRead inst) {
        inst.getMem().accept(this);
        if(inst.getMem().getType() != Type.ERROR) {
            // can only read univariate types
            if( inst.getMem().getType().isRecord() ||
                inst.getMem().getType().isArray()
            ) {
                Errors.printErrorFancy(inst, Errors.ERROR_READ);
                inst.setType(Type.ERROR);
            }
            else {
                inst.setType(Type.OK);
            }
        }
        else {
           inst.setType(Type.ERROR);
        }
    }
    
    @Override
    public void visit(InstructionWrite inst) {
        inst.getExp().accept(this);
        if(inst.getExp().getType() != Type.ERROR)
            inst.setType(Type.OK);
        else {
           inst.setType(Type.ERROR);
        }
    }

    /* instructions - control structures */

    @Override
    public void visit(InstructionWhile inst) {
        inst.getCondition().accept(this);
        if( !(inst.getCondition().getType() == Type.ERROR) &&
            !(inst.getCondition().getType() == Type.BOOL)
            ) {
            Errors.printError(
                inst.getLinkToSource() + ": " + Errors.ERROR_COND
            );
        }
        inst.getBody().accept(this);
        if( inst.getCondition().getType() == Type.BOOL &&
            inst.getBody().getType() == Type.OK
        ) {
           inst.setType(Type.OK);
        }
        else {
           inst.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(InstructionIfThen inst) {
        inst.getCondition().accept(this);
        if( !(inst.getCondition().getType() == Type.ERROR) &&
            !(inst.getCondition().getType() == Type.BOOL)
        ) {
            Errors.printError(inst.getLinkToSource() + ": " + Errors.ERROR_COND);
        }
        inst.getBody().accept(this);
        if( inst.getCondition().getType() == Type.BOOL &&
            inst.getBody().getType() ==      Type.OK
        ) {
            inst.setType(Type.OK);
        }
        else {
            inst.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(InstructionIfThenElse inst) {
        inst.getCondition().accept(this);
        if( inst.getCondition().getType() != Type.ERROR &&
            inst.getCondition().getType() != Type.BOOL
        ) {
            Errors.printError(inst.getLinkToSource() + ": " + Errors.ERROR_COND);
        }
        inst.getBodyIf().accept(this);
        inst.getBodyElse().accept(this);
        if( inst.getCondition().getType() == Type.BOOL &&
            inst.getBodyIf().getType() ==    Type.OK &&
            inst.getBodyElse().getType() ==  Type.OK
        ) {
            inst.setType(Type.OK);
        }
        else {
            inst.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(InstructionSwitch inst) {
        inst.getExp().accept(this);
        if( inst.getExp().getType() == Type.STRING &&
            inst.getExp().getType() != Type.ERROR
        ) {
            Errors.printError(
                inst.getLinkToSource() + ": " + Errors.ERROR_SWITCH
            );
        }
        boolean ok = true;
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getInst().accept(this);
            ok = ok && c.getInst().getType() == Type.OK;
        }
        inst.getDefaultInst().accept(this);
        ok = ok && inst.getDefaultInst().getType() == Type.OK;
        if( ok &&
            inst.getExp().getType() != Type.STRING &&
            inst.getExp().getType() != Type.ERROR
        ) {
            inst.setType(Type.OK);
        }
        else {
            inst.setType(Type.ERROR);
        }
    }

    /* epressions *

    /* expressions - constants */

    @Override
    public void visit(ConstantInt exp) {
        exp.setType(Type.INT);
    }

    @Override
    public void visit(ConstantBool exp) {
        exp.setType(Type.BOOL);
    }

    @Override
    public void visit(ConstantReal exp) {
        exp.setType(Type.REAL);
    }

    @Override
    public void visit(ConstantChar exp) {
        exp.setType(Type.CHAR);
    }

    @Override
    public void visit(ConstantString exp) {
        exp.setType(Type.STRING);
    }

    /* expressions - mems */

    @Override
    public void visit(Variable var) {
        var.setType(var.getDec().getType());
        // if the declared type was a reference to another type, replace it
        if(var.getType().isReference()) {
            var.setType(var.getDec().getType().toRef().referencedType());
        }
    }

    @Override
    public void visit(Dereference d) {
        d.getMem().accept(this);
        if(d.getMem().getType().isPointer()) {
            d.setType(d.getMem().getType().toPointer().getBaseType());
            // if the base type was a referenced type replace it
            if(d.getType().isReference()) {
                d.setType(d.getType().toRef().referencedType());
            }
        }
        else {
            if(d.getMem().getType() != Type.ERROR)
                Errors.printErrorFancy(d, Errors.ERROR_DEREF);
            d.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(Index ind) {
        ind.getExp().accept(this);
        ind.getMem().accept(this);
        if(ind.getExp().getType() != Type.INT) {
            if(ind.getExp().getType() != Type.ERROR)
                Errors.printErrorFancy(ind, Errors.ERROR_ARRAY_INDEX);
            ind.setType(Type.ERROR);
        }
        if(ind.getMem().getType().isArray()) {
            // it has been already checked that index is INT
            ind.setType(ind.getMem().getType().toArray().getBaseType());
            // check if arary base type is a referenced type
            if(ind.getType().isReference()) {
                ind.setType(ind.getType().toRef().referencedType());
            }
        }
        else {
            // trying to index something that is not an array
            if(ind.getMem().getType() != Type.ERROR)
                Errors.printErrorFancy(ind, Errors.ERROR_ARRAY);
            ind.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(Select s) {
        s.getMem().accept(this);
        if(s.getMem().getType().isRecord()) {
            if(s.getMem().getType().toRecord().getFieldIdentifiers().contains(s.getField())) {
                s.setType(s.getMem().getType().toRecord().typeOf(s.getField()));
                // if type of field was a referenced type, replace it
                if(s.getType().isReference()) {
                    s.setType(s.getType().toRef().referencedType());
                }
            }
        }
        else {
            if(s.getMem().getType() != Type.ERROR) {
                Errors.printErrorFancy(s, Errors.ERROR_SELECT);
            }
            s.setType(Type.ERROR);
        }
    }

    /* expressions - unary */
    
    /* expressions - unary - miscellaneous */

    @Override
    public void visit(SignChange exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR)
            exp.setType(Type.ERROR);
        // type is not error - check if operand type is non-numerical
        else if(
            !(exp.getOp().getType() == Type.INT ||
              exp.getOp().getType() == Type.REAL)
        ) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is int or string - everything ok
        else
            exp.setType(exp.getOp().getType());
    }

    /* expressions - unary - logical */

    @Override
    public void visit(Not exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR)
            exp.setType(Type.ERROR);
        // type is not error - check if operand type is other than Boolean
        else if(exp.getOp().getType() != Type.BOOL) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is Boolean - everything ok
        else
            exp.setType(Type.BOOL);
    }

    /* expressions - unary - explicit type conversions */

    @Override
    public void visit(ConversionInt exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR) {
            exp.setType(Type.ERROR);
        }
        else if(exp.getOp().getType() == Type.STRING) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        else {
            exp.setType(Type.INT);
        }
    }

    @Override
    public void visit(ConversionReal exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR) {
            exp.setType(Type.ERROR);
        }
        else if(exp.getOp().getType() == Type.STRING) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        else {
            exp.setType(Type.REAL);
        }
    }

    @Override
    public void visit(ConversionBool exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR) {
            exp.setType(Type.ERROR);
        }
        // type is string, char or bool
        else if(exp.getOp().getType() == Type.STRING ||
                exp.getOp().getType() == Type.CHAR ||
                exp.getOp().getType() == Type.REAL
        ) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is bool or int
        else {
            exp.setType(Type.BOOL);
        }
    }

    @Override
    public void visit(ConversionChar exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR) {
            exp.setType(Type.ERROR);
        }
        // type is string, bool or real
        else if(exp.getOp().getType() == Type.STRING ||
                exp.getOp().getType() == Type.BOOL ||
                exp.getOp().getType() == Type.REAL
        ) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is char or int
        else {
            exp.setType(Type.CHAR);
        }
    }

    @Override
    public void visit(ConversionString exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType() == Type.ERROR) {
            exp.setType(Type.ERROR);
        }
        // type is int, bool or real
        else if(exp.getOp().getType() == Type.INT ||
                exp.getOp().getType() == Type.BOOL ||
                exp.getOp().getType() == Type.REAL
        ) {
            // error occured here, so print it
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is char or string
        else {
            exp.setType(Type.STRING);
        }
    }
    
    /* expressions - binary */

    /* expressions - binary - miscellaneous */

    @Override
    public void visit(ChainElement exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        // check if op1 is string and op2 int
        if(
            exp.getOp1().getType() == Type.STRING &&
            exp.getOp2().getType() == Type.INT
        ) {
            exp.setType(Type.CHAR);
        }
        // check if at least one of the operand types is not error
        else if(
            !(exp.getOp1().getType() == Type.ERROR &&
              exp.getOp2().getType() == Type.ERROR)
        ) {
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // otherwise both operand types are error, do not print in this case
        else
            exp.setType(Type.ERROR);
    }

    /* expression - binary - arithmetic */

    @Override
    public void visit(BinaryArithmeticExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        // both operand types are int
        if(
            exp.getOp1().getType() == Type.INT &&
            exp.getOp2().getType() == Type.INT
        ) {
            exp.setType(Type.INT);
        }
        // both operand types are real
        else if(
            exp.getOp1().getType() == Type.REAL &&
            exp.getOp2().getType() == Type.REAL
        ) {
            exp.setType(Type.REAL);
        }
        // one of the operand types is int and the other is real
        else if(
            (exp.getOp1().getType() == Type.INT &&
             exp.getOp2().getType() == Type.REAL) ||
            (exp.getOp1().getType() == Type.REAL &&
             exp.getOp2().getType() == Type.INT)
        ) {
            exp.setType(Type.REAL);
        }
        else {
            // we only link the error to source code if exactly one child is
            // error, because than the mistake actually happens here
            if(exp.getOp1().getType() != Type.ERROR &&
                exp.getOp2().getType() != Type.ERROR
            ) {
                Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(Sum exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        // both operand types are int
        if(
            exp.getOp1().getType() == Type.INT &&
            exp.getOp2().getType() == Type.INT
        ) {
            exp.setType(Type.INT);
        }
        // both operand types are real
        else if(
            exp.getOp1().getType() == Type.REAL &&
            exp.getOp2().getType() == Type.REAL
        ) {
            exp.setType(Type.REAL);
        }
        // one of the operand types is int and the other is real
        else if(
            (exp.getOp1().getType() == Type.INT &&
             exp.getOp2().getType() == Type.REAL) ||
            (exp.getOp1().getType() == Type.REAL &&
             exp.getOp2().getType() == Type.INT)
        ) {
            exp.setType(Type.REAL);
        }
        // both operand types are String - special for the sum!
        else if(
            exp.getOp1().getType() == Type.STRING &&
            exp.getOp2().getType() == Type.STRING
        ) {
            exp.setType(Type.STRING);
        }
        else {
            // we only link the error to source code if exactly one child is
            // error, because than the mistake actually happens here
            if(exp.getOp1().getType() != Type.ERROR &&
                exp.getOp2().getType() != Type.ERROR
            ) {
                Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(Rest exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        // rest is different than the other binary arithmetic operators because
        // the operands must both be integers
        if(
            exp.getOp1().getType() == Type.INT &&
            exp.getOp2().getType() == Type.INT
        ) {
            exp.setType(Type.INT);
        }
        else {
            if(
                exp.getOp1().getType() != Type.ERROR &&
                exp.getOp2().getType() != Type.ERROR
            ) {
                Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }
    
    /* expressions - binary - relational */

    @Override
    public void visit(BinaryRelationalExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        if(
            // numerical
            (exp.getOp1().hasNumericalType() &&
             exp.getOp2().hasNumericalType()) ||
            // bool and bool
            (exp.getOp1().getType() == Type.BOOL &&
             exp.getOp2().getType() == Type.BOOL) ||
            // char and char
            (exp.getOp1().getType() == Type.CHAR &&
             exp.getOp2().getType() == Type.CHAR) ||
            // string and string
            (exp.getOp1().getType() == Type.STRING &&
             exp.getOp2().getType() == Type.STRING)
            ) {
            exp.setType(Type.BOOL);
        }
        // check if at least one of the operand types is not error
        else if(
            !(exp.getOp1().getType() == Type.ERROR &&
              exp.getOp2().getType() == Type.ERROR)
            ) {
            Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // otherwise both operand types are error, do not print in this case
        else
            exp.setType(Type.ERROR);

    }

    /* expressions - binary - logical */

    @Override
    public void visit(BinaryLogicalExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        if(exp.getOp1().getType() == Type.BOOL &&
            exp.getOp2().getType() == Type.BOOL) {
            exp.setType(Type.BOOL);
        }
        else {
            if(exp.getOp1().getType() != Type.ERROR &&
                exp.getOp2().getType() != Type.ERROR) {
                Errors.printErrorFancy(exp, Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }

}
