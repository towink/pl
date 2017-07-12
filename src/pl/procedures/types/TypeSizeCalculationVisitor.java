package pl.procedures.types;

import pl.abstractsyntax.Program;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Inst;
import pl.abstractsyntax.Inst.*;
import pl.procedures.Visitor;
import pl.type.Type.*;

/**
 * This visitor is only applied to types anywhere declared in the program and is
 * responsible for computing their sizes.
 */
public class TypeSizeCalculationVisitor extends Visitor {

    /* types */

    /* types - definable - atomic */

    @Override
    public void visit(AtomicDefinedType type) {
        // for atomic types (int, bool, real, char, string) the size is always 1
        if(type.sizeNotSet()) {
            type.setSize(1);
        }
    }

    /* types - definable - composed */

    @Override
    public void visit(TypeArray type) {
        if(type.sizeNotSet()) {
            type.getBaseType().accept(this);
            type.setSize(type.getDim() * type.getBaseType().getSize());
        }
    }

    @Override
    public void visit(TypeRecord type) {
        if(type.sizeNotSet()) {
            int sum = 0;
            for(TypeRecord.RecordField f : type.getFields()) {
                f.getType().accept(this);
                f.setOffset(sum);
                sum += f.getType().getSize();
            }
            type.setSize(sum);
        }
    }

    @Override
    public void visit(TypePointer type) {
        // pointers have constant size of 1 (also null pointer)
        if(type.sizeNotSet()) {
            type.setSize(1);
            type.getBaseType().accept(this);
        }
    }

    @Override
    public void visit(TypeRef type) {
        // TODO size of type.getDecReferencedType().getType() must be known
        // at this point, otherwise error!! why??
        if(type.sizeNotSet()) {
            type.getDecReferencedType().getType().accept(this);
            type.setSize(type.getDecReferencedType().getType().getSize());
        }
    }
    
    // we do not have to visit expressions, just instructions because of blocks
    
    /* instructions */
    
    /* instructions - general */
    
    @Override
    public void visit(InstructionAssignment asg) {}
    @Override
    public void visit(InstructionCall call) {}
    
    /* instructions - memory */

    @Override
    public void visit(InstructionNew inst) {}
    @Override
    public void visit(InstructionFree inst) {}

    /* instructions - IO*/

    @Override
    public void visit(InstructionWrite inst) {}
    @Override
    public void visit(InstructionRead inst) {}
    
    /* instructions - control structures */

    @Override
    public void visit(InstructionWhile inst) {
        inst.getBody().accept(this);
    }
    @Override
    public void visit(InstructionIfThen inst) {
        inst.getBody().accept(this);
    }
    @Override
    public void visit(InstructionIfThenElse inst) {
        inst.getBodyIf().accept(this);
        inst.getBodyElse().accept(this);
    }
    @Override
    public void visit(InstructionSwitch inst) {
        for(InstructionSwitch.Case c : inst.getCases()) {
            c.getInst().accept(this);
        }
    }

}