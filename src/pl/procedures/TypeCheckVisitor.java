package pl.procedures;

import pl.errors.Errors;
import pl.abstractsyntax.Program;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Mem.Variable;
import pl.type.Type;
import pl.abstractsyntax.Mem.*;
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

    @Override
    public void visit(Program p) {
        p.getInstruction().accept(this);
        p.setType(p.getInstruction().getType());
    }

    /* instructions */

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
        for(Instruction inst : block.getInstructions()) {
            inst.accept(this);
            ok = ok && inst.getType().equals(Type.OK);
        }
        if(ok)
            block.setType(Type.OK);
        else
            block.setType(Type.ERROR);
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
    public void visit(InstructionWrite inst) {
        inst.getExp().accept(this);
        if(!inst.getExp().getType().equals(Type.ERROR))
            inst.setType(Type.OK);
        else
            inst.setType(Type.ERROR);
    }
    
    /* instructions - control structures */

    @Override
    public void visit(InstructionWhile inst) {
        inst.getCondition().accept(this);
        if( !(inst.getCondition().getType() == Type.ERROR) &&
            !(inst.getCondition().getType() == Type.BOOL)
            ) {
            Errors.printError(inst.getLinkToSource() + ": " + Errors.ERROR_COND);
        }
        inst.getBody().accept(this);
        if(inst.getCondition().getType().equals(Type.BOOL) &&
           inst.getBody().getType().equals(Type.OK)
            ) {
           inst.setType(Type.OK);
        }
        else
           inst.setType(Type.ERROR);
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
        if(inst.getCondition().getType() == Type.BOOL &&
           inst.getBody().getType() ==      Type.OK
           ) {
           inst.setType(Type.OK);
        }
        else
           inst.setType(Type.ERROR);
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
        if(inst.getCondition().getType() == Type.BOOL &&
           inst.getBodyIf().getType() ==    Type.OK &&
           inst.getBodyElse().getType() ==  Type.OK
           ) {
           inst.setType(Type.OK);
        }
        else
           inst.setType(Type.ERROR);
    }

    @Override
    public void visit(InstructionSwitch inst) {
        inst.getExp().accept(this);
        if( inst.getExp().getType() == Type.STRING &&
            inst.getExp().getType() != Type.ERROR
        ) {
            Errors.printError(inst.getLinkToSource() + ": " + Errors.ERROR_SWITCH);
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
            inst.getExp().getType() != Type.ERROR) {
            inst.setType(Type.OK);
        }
        else {
            inst.setType(Type.ERROR);
        }
    }


    /* constants */

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
    
    /* mems */

    @Override
    public void visit(Variable exp) {
        exp.setType(exp.getDec().getType());
    }
    
    @Override
    public void visit(Dereference d) {
        d.getMem().accept(this);
        if(d.getMem().getType().isPointer())
            d.setType(d.getMem().getType().toPointer().getBaseType());
        else {
            if(d.getMem().getType() != Type.ERROR)
                Errors.printErrorFancy(d, Errors.ERROR_DEREF);
            d.setType(Type.ERROR);
        }
    }
    
    @Override
    public void visit(Index i) {
        i.getExp().accept(this);
        i.getMem().accept(this);
        if(i.getExp().getType() != Type.INT) {
            if(i.getExp().getType() != Type.ERROR)
                Errors.printErrorFancy(i, Errors.ERROR_ARRAY_INDEX);
            i.setType(Type.ERROR);
        }
        if(i.getMem().getType().isArray())
            // it has already checked that index is INT
            i.setType(i.getType().toArray().getBaseType());
        else {
            // trying to index something that is not an array
            if(i.getMem().getType() != Type.ERROR)
                Errors.printErrorFancy(i, Errors.ERROR_ARRAY);
            i.setType(Type.ERROR);
        }
    }
    
    @Override
    public void visit(Select s) {
        s.getMem().accept(this);
        if(s.getMem().getType().isRecord()) {
            if(s.getMem().getType().toRecord().getFields().contains(s.getField())) {
                s.setType(s.getType().toRecord().typeOf(s.getField()));
            }
                    
            
        }
    }

    /* unary expressions - miscellaneous */

    @Override
    public void visit(SignChange exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType().equals(Type.ERROR))
            exp.setType(Type.ERROR);
        // type is not error - check if operand type is non-numerical
        else if(
            !(exp.getOp().getType().equals(Type.INT) ||
              exp.getOp().getType().equals(Type.REAL))
            ) {
            // error occured here, so print it
            Errors.printError(
                exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is int or string - everything ok
        else
            exp.setType(exp.getOp().getType());
    }

    /* unary expressions - logical */

    @Override
    public void visit(Not exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType().equals(Type.ERROR))
            exp.setType(Type.ERROR);
        // type is not error - check if operand type is other than Boolean
        else if(!exp.getOp().getType().equals(Type.BOOL)) {
            // error occured here, so print it
            Errors.printError(
                exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is Boolean - everything ok
        else
            exp.setType(Type.BOOL);
    }

    /* unary expressions - explicit type conversions */

    @Override
    public void visit(ConversionInt exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType().equals(Type.ERROR)) {
            exp.setType(Type.ERROR);
        }
        else if(exp.getOp().getType().equals(Type.STRING)) {
            // error occured here, so print it
            Errors.printError(exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        else {
            exp.setType(Type.INT);
        }
    }

    @Override
    public void visit(ConversionReal exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType().equals(Type.ERROR)) {
            exp.setType(Type.ERROR);
        }
        else if(exp.getOp().getType().equals(Type.STRING)) {
            // error occured here, so print it
            Errors.printError(exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        else {
            exp.setType(Type.REAL);
        }
    }

    @Override
    public void visit(ConversionBool exp) {
        exp.getOp().accept(this);
        if(exp.getOp().getType().equals(Type.ERROR)) {
            exp.setType(Type.ERROR);
        }
        // type is string, char or bool
        else if(exp.getOp().getType().equals(Type.STRING) ||
                exp.getOp().getType().equals(Type.CHAR) ||
                exp.getOp().getType().equals(Type.REAL)) {
            // error occured here, so print it
            Errors.printError(exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
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
        if(exp.getOp().getType().equals(Type.ERROR)) {
            exp.setType(Type.ERROR);
        }
        // type is string, bool or real
        else if(exp.getOp().getType().equals(Type.STRING) ||
                exp.getOp().getType().equals(Type.BOOL) ||
                exp.getOp().getType().equals(Type.REAL)) {
            // error occured here, so print it
            Errors.printError(exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
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
        if(exp.getOp().getType().equals(Type.ERROR)) {
            exp.setType(Type.ERROR);
        }
        // type is int, bool or real
        else if(exp.getOp().getType().equals(Type.INT) ||
                exp.getOp().getType().equals(Type.BOOL) ||
                exp.getOp().getType().equals(Type.REAL)) {
            // error occured here, so print it
            Errors.printError(exp.getLinkToSource() + ": " +  Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // type is char or string
        else {
            exp.setType(Type.STRING);
        }
    }

    /* binary expression - miscellaneous */

    @Override
    public void visit(ChainElement exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        // check if op1 is string and op2 int
        if(
            exp.getOp1().getType().equals(Type.STRING) &&
            exp.getOp2().getType().equals(Type.INT)
            ) {
            exp.setType(Type.CHAR);
        }
        // check if at least one of the operand types is not error
        else if(
            !(exp.getOp1().getType().equals(Type.ERROR) &&
              exp.getOp2().getType().equals(Type.ERROR))
            ) {
            Errors.printError(
                exp.getLinkToSource() + ": " + Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // otherwise both operand types are error, do not print in this case
        else
            exp.setType(Type.ERROR);
    }

    /* binary expression - arithmetic */

    @Override
    public void visit(BinaryArithmeticExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        // both operand types are int
        if(
            exp.getOp1().getType().equals(Type.INT) &&
            exp.getOp2().getType().equals(Type.INT)
            ) {
            exp.setType(Type.INT);
        }
        // both operand types are real
        else if(
            exp.getOp1().getType().equals(Type.REAL) &&
            exp.getOp2().getType().equals(Type.REAL)
            ) {
            exp.setType(Type.REAL);
        }
        // one of the operand types is int and the other is real
        else if(
            (exp.getOp1().getType().equals(Type.INT) &&
             exp.getOp2().getType().equals(Type.REAL)) ||
            (exp.getOp1().getType().equals(Type.REAL) &&
             exp.getOp2().getType().equals(Type.INT))
            ) {
            exp.setType(Type.REAL);
        }
        else {
            // we only link the error to source code if exactly one child is
            // error, because than the mistake actually happens here
            if(!exp.getOp1().getType().equals(Type.ERROR) &&
                !exp.getOp2().getType().equals(Type.ERROR)) {
                Errors.printError(
                    exp.getLinkToSource() + ": " + Errors.ERROR_OPERAND_TYPES);
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
            exp.getOp1().getType().equals(Type.INT) &&
            exp.getOp2().getType().equals(Type.INT)
            ) {
            exp.setType(Type.INT);
        }
        // both operand types are real
        else if(
            exp.getOp1().getType().equals(Type.REAL) &&
            exp.getOp2().getType().equals(Type.REAL)
            ) {
            exp.setType(Type.REAL);
        }
        // one of the operand types is int and the other is real
        else if(
            (exp.getOp1().getType().equals(Type.INT) &&
             exp.getOp2().getType().equals(Type.REAL)) ||
            (exp.getOp1().getType().equals(Type.REAL) &&
             exp.getOp2().getType().equals(Type.INT))
            ) {
            exp.setType(Type.REAL);
        }
        // both operand types are String - special for the sum!
        else if(
            exp.getOp1().getType().equals(Type.STRING) &&
            exp.getOp2().getType().equals(Type.STRING)
            ) {
            exp.setType(Type.STRING);
        }
        else {
            // we only link the error to source code if exactly one child is
            // error, because than the mistake actually happens here
            if(!exp.getOp1().getType().equals(Type.ERROR) &&
                !exp.getOp2().getType().equals(Type.ERROR)) {
                Errors.printError(
                    exp.getLinkToSource() + ": " + Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(Product exp) {
        visit((BinaryArithmeticExp)exp);
    }

    @Override
    public void visit(Quotient exp) {
        visit((BinaryArithmeticExp)exp);
    }

    @Override
    public void visit(Difference exp) {
        visit((BinaryArithmeticExp)exp);
    }

    @Override
    public void visit(Rest exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        if(
            exp.getOp1().getType().equals(Type.INT) &&
            exp.getOp2().getType().equals(Type.INT)
            ) {
            exp.setType(Type.INT);
        }
        else {
            if(
                !exp.getOp1().getType().equals(Type.ERROR) &&
                !exp.getOp2().getType().equals(Type.ERROR)
                ) {
                Errors.printError(
                    exp.getLinkToSource() + ":" + Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }


    /* binary expressions - relational */

    @Override
    public void visit(BinaryRelationalExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        if(
            // numerical
            (exp.getOp1().hasNumericalType() &&
             exp.getOp2().hasNumericalType()) ||
            // bool and bool
            (exp.getOp1().getType().equals(Type.BOOL) &&
             exp.getOp2().getType().equals(Type.BOOL)) ||
            // char and char
            (exp.getOp1().getType().equals(Type.CHAR) &&
             exp.getOp2().getType().equals(Type.CHAR)) ||
            // string and string
            (exp.getOp1().getType().equals(Type.STRING) &&
             exp.getOp2().getType().equals(Type.STRING))
            ) {
            exp.setType(Type.BOOL);
        }
        // check if at least one of the operand types is not error
        else if(
            !(exp.getOp1().getType().equals(Type.ERROR) &&
              exp.getOp2().getType().equals(Type.ERROR))
            ) {
            Errors.printError(
                exp.getLinkToSource() + ": " + Errors.ERROR_OPERAND_TYPES);
            exp.setType(Type.ERROR);
        }
        // otherwise both operand types are error, do not print in this case
        else
            exp.setType(Type.ERROR);

    }

    @Override
    public void visit(Equal exp) {
        visit((BinaryRelationalExp)exp);
    }

    @Override
    public void visit(Unequal exp) {
        visit((BinaryRelationalExp)exp);
    }

    @Override
    public void visit(Less exp) {
        visit((BinaryRelationalExp)exp);
    }

    @Override
    public void visit(LessEqual exp) {
        visit((BinaryRelationalExp)exp);
    }

    @Override
    public void visit(Greater exp) {
        visit((BinaryRelationalExp)exp);
    }

    @Override
    public void visit(GreaterEqual exp) {
        visit((BinaryRelationalExp)exp);
    }

    /* binary expressions - logical */

    @Override
    public void visit(BinaryLogicalExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
        if(exp.getOp1().getType().equals(Type.BOOL) &&
            exp.getOp2().getType().equals(Type.BOOL)) {
            exp.setType(Type.BOOL);
        }
        else {
            if (!exp.getOp1().getType().equals(Type.ERROR) &&
                !exp.getOp2().getType().equals(Type.ERROR)) {
                Errors.printError(
                    exp.getLinkToSource() + ":" + Errors.ERROR_OPERAND_TYPES);
            }
            exp.setType(Type.ERROR);
        }
    }

    @Override
    public void visit(And exp) {
        visit((BinaryLogicalExp)exp);
    }

    @Override
    public void visit(Or exp) {
        visit((BinaryLogicalExp)exp);
    }

}