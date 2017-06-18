package pl.procedures;

import pl.abstractsyntax.Program;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Mem;
import pl.abstractsyntax.Mem.*;
import pl.type.Type;
import pl.type.Type.*;

/**
 * Represents a general visitor of the program tree and for types.
 *
 * The default behaviour is post order tree traversal (without doing anything).
 */
public abstract class Visitor {

    /* types */

    public void visit(Type type) {}

    public void visit(TypeOk type) {}
    public void visit(TypeError type) {}

    /* types - definable */
    
    public void visit(DefinedType type) {}

    /* types - definable - atomic */
    
    public void visit(AtomicDefinedType type) {}
    public void visit(TypeInt type) { visit((AtomicDefinedType)type); }
    public void visit(TypeBool type) { visit((AtomicDefinedType)type); }
    public void visit(TypeReal type) { visit((AtomicDefinedType)type); }
    public void visit(TypeChar type) { visit((AtomicDefinedType)type); }
    public void visit(TypeString type) { visit((AtomicDefinedType)type); }
    
    /* types - definable - composed */

    public void visit(TypeArray type) { type.getBaseType().accept(this); }
    public void visit(TypePointer type) { type.getBaseType().accept(this); }
    public void visit(TypeRecord type) {
        for(TypeRecord.RecordField f : type.getFields())
            f.getType().accept(this);
    }
    public void visit(TypeRef type) {
        type.getDecReferencedType().getType().accept(this);
    }

    /* program */

    public void visit(Program prog) {
        for(Declaration d : prog.getDeclarations()) d.accept(this);
        prog.getInstruction().accept(this);
    }

    /* declarations */

    public void visit(Declaration dec) {}
    // no default behaviour here because of cyclic type definitions
    public void visit(DeclarationVariable dec) {}
    public void visit(DeclarationType dec) {}

    /* instructions */
    
    /* instructions - general */

    public void visit(InstructionAssignment asg) { 
        asg.getMem().accept(this);
        asg.getExp().accept(this);
    }
    public void visit(InstructionBlock block) {
        for(Instruction i : block.getInstructions()) i.accept(this);
    }
    public void visit(InstructionCall call) {
        call.getBody().accept(this);
    }

    /* instructions - memory */

    public void visit(InstructionNew inst) { inst.getMem().accept(this); }
    public void visit(InstructionFree inst) { inst.getMem().accept(this); }

    /* instructions - IO*/

    public void visit(InstructionWrite inst) { inst.getExp().accept(this); }

    // TODO read ...

    /* instructions - control structures */

    public void visit(InstructionWhile inst) {
        inst.getCondition().accept(this);
        inst.getBody().accept(this);
    }
    public void visit(InstructionIfThen inst) {
        inst.getCondition().accept(this);
        inst.getBody().accept(this);
    }
    public void visit(InstructionIfThenElse inst) {
        inst.getCondition().accept(this);
        inst.getBodyIf().accept(this);
        inst.getBodyElse().accept(this);
    }
    public void visit(InstructionSwitch inst) {
        inst.getExp().accept(this);
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getLiteral().accept(this);
            c.getInst().accept(this);
        }
    }
    
    /* expressions */
    
    public void visit(Exp exp) {}

    /* expressions - constants */

    public void visit(Constant exp) {}
    public void visit(ConstantInt exp) {visit((Constant)exp);}
    public void visit(ConstantBool exp) {visit((Constant)exp);}
    public void visit(ConstantReal exp) {visit((Constant)exp);}
    public void visit(ConstantChar exp) {visit((Constant)exp);}
    public void visit(ConstantString exp) {visit((Constant)exp);}

    /* expressions - mems */

    public void visit(Mem mem) {}
    public void visit(Variable var) {}
    public void visit(Dereference d) { d.getMem().accept(this); }
    public void visit(Select s) { s.getMem().accept(this); }
    public void visit(Index i) {
        i.getExp().accept(this);
        i.getMem().accept(this);
    }

    /* expressions - unary */

    public void visit(UnaryExp exp) { exp.getOp().accept(this); }

    /* expressions - unary - arithmetic */

    public void visit(SignChange exp) { visit((UnaryExp)exp); }

    /* expressions - unary - logical */

    public void visit(Not exp) { visit((UnaryExp)exp); }

    /* expressions - unary - explicit type conversion */

    public void visit(ConversionInt exp) { visit((UnaryExp)exp); }
    public void visit(ConversionBool exp) { visit((UnaryExp)exp); }
    public void visit(ConversionReal exp) { visit((UnaryExp)exp); }
    public void visit(ConversionChar exp) { visit((UnaryExp)exp); }
    public void visit(ConversionString exp) { visit((UnaryExp)exp); }

    /* expressions - binary */

    public void visit(BinaryExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
    }

    /* expressions - binary - miscellaneous */

    public void visit(ChainElement exp) { visit((BinaryExp)exp); }

    /* expressions - binary - arithmetic */

    public void visit(BinaryArithmeticExp exp) { visit((BinaryExp)exp); }
    
    public void visit(Sum exp) { visit((BinaryArithmeticExp)exp); }
    public void visit(Product exp) { visit((BinaryArithmeticExp)exp); }
    public void visit(Difference exp) { visit((BinaryArithmeticExp)exp); }
    public void visit(Quotient exp) { visit((BinaryArithmeticExp)exp); }
    public void visit(Rest exp) { visit((BinaryArithmeticExp)exp); }

    /* expressions - binary - relational */

    public void visit(BinaryRelationalExp exp) {visit((BinaryExp)exp);}
    
    public void visit(Equal exp) { visit((BinaryRelationalExp)exp); }
    public void visit(Unequal exp) { visit((BinaryRelationalExp)exp); }
    public void visit(Less exp) { visit((BinaryRelationalExp)exp); }
    public void visit(LessEqual exp) { visit((BinaryRelationalExp)exp); }
    public void visit(Greater exp) { visit((BinaryRelationalExp)exp); }
    public void visit(GreaterEqual exp) { visit((BinaryRelationalExp)exp); }

    /* expressions - binary - logical */

    public void visit(BinaryLogicalExp exp) { visit((BinaryExp)exp); }
    
    public void visit(And exp) { visit((BinaryLogicalExp)exp); }
    public void visit(Or exp) { visit((BinaryLogicalExp)exp); }

}
