package pl.procedures;

import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Mem;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Program;
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
    
    public void visit(DefinedType type) {}
    
    public void visit(AtomicDefinedType type) {}
    public void visit(TypeInt type) { visit((AtomicDefinedType)type); }
    public void visit(TypeBool type) { visit((AtomicDefinedType)type); }
    public void visit(TypeReal type) { visit((AtomicDefinedType)type); }
    public void visit(TypeChar type) { visit((AtomicDefinedType)type); }
    public void visit(TypeString type) { visit((AtomicDefinedType)type); }
    
    public void visit(TypeArray type) { type.getBaseType().accept(this); }
    public void visit(TypeRecord type) {
        for(TypeRecord.RecordField f : type.getFields())
            f.getType().accept(this);
    }
    public void visit(TypePointer type) {
        type.getBaseType().accept(this);
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
    public void visit(DeclarationVariable dec) { dec.getType().accept(this); }
    public void visit(DeclarationType dec) { dec.getType().accept(this); }
    
    /* instructions */
    
    public void visit(InstructionAssignment assig) {
        assig.getExp().accept(this);
    }
    public void visit(InstructionBlock block) {
        for(Instruction i : block.getInstructions()) i.accept(this);
    }
    
    /* instructions - memory */
    
    public void visit(InstructionNew inst) { inst.getMem().accept(this); }
    public void visit(InstructionFree inst) { inst.getMem().accept(this); }
    
    /* instructions - IO*/
    
    public void visit(InstructionWrite inst) {
        inst.getExp().accept(this);
    }
    
    // read ...
    
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
    
    /* constants */
    
    public void visit(Exp exp) {}
    
    public void visit(Constant exp) {} 
    public void visit(ConstantInt exp) {visit((Constant)exp);} 
    public void visit(ConstantBool exp) {visit((Constant)exp);}
    public void visit(ConstantReal exp) {visit((Constant)exp);}
    public void visit(ConstantChar exp) {visit((Constant)exp);}
    public void visit(ConstantString exp) {visit((Constant)exp);}
    
    /* mems */
    
    public void visit(Mem mem) {}
    public void visit(Variable var) {}
    public void visit(Dereference d) { d.getMem().accept(this); }
    public void visit(Index i) {
        i.getExp().accept(this);
        i.getMem().accept(this);
    }
    public void visit(Select s) { s.getMem().accept(this); }
    
    /* unary expressions */
    
    public void visit(UnaryExp exp) {
        exp.getOp().accept(this);
    }
    
    /* unary expressions - miscellaneous */
    
    public void visit(SignChange exp) {visit((UnaryExp)exp);}
    
    /* unary expressions - logical */
    
    public void visit(Not exp) {visit((UnaryExp)exp);}
    
    /* unary expressions - explicit type conversion */
    
    public void visit(ConversionInt exp) {visit((UnaryExp)exp);}
    public void visit(ConversionBool exp) {visit((UnaryExp)exp);}
    public void visit(ConversionReal exp) {visit((UnaryExp)exp);}
    public void visit(ConversionChar exp) {visit((UnaryExp)exp);}
    public void visit(ConversionString exp) {visit((UnaryExp)exp);}
    
    /* binary expressions */
    
    public void visit(BinaryExp exp) {
        exp.getOp1().accept(this);
        exp.getOp2().accept(this);
    }
    
    /* binary expressions - miscellaneous */
    
    public void visit(ChainElement exp) {visit((BinaryExp)exp);}
    
    /* binary expressions - arithmetic */
    
    public void visit(BinaryArithmeticExp exp) {visit((BinaryExp)exp);}
    public void visit(Sum exp) {visit((BinaryArithmeticExp)exp);}
    public void visit(Product exp) {visit((BinaryArithmeticExp)exp);}
    public void visit(Difference exp) {visit((BinaryArithmeticExp)exp);}
    public void visit(Quotient exp) {visit((BinaryArithmeticExp)exp);}
    public void visit(Rest exp) {visit((BinaryArithmeticExp)exp);}
    
    /* binary expressions - relational */
    
    public void visit(BinaryRelationalExp exp) {visit((BinaryExp)exp);}
    public void visit(Equal exp) {visit((BinaryRelationalExp)exp);}
    public void visit(Unequal exp) {visit((BinaryRelationalExp)exp);}
    public void visit(Less exp) {visit((BinaryRelationalExp)exp);}
    public void visit(LessEqual exp) {visit((BinaryRelationalExp)exp);}
    public void visit(Greater exp) {visit((BinaryRelationalExp)exp);}
    public void visit(GreaterEqual exp) {visit((BinaryRelationalExp)exp);}
    
    /* binary expressions - logical */
    
    public void visit(BinaryLogicalExp exp) {visit((BinaryExp)exp);}
    public void visit(And exp) {visit((BinaryLogicalExp)exp);}
    public void visit(Or exp) {visit((BinaryLogicalExp)exp);}
    
}
