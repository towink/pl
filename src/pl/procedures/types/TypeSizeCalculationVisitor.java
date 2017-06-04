package pl.procedures.types;

import pl.abstractsyntax.Program;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.procedures.Visitor;
import pl.type.Type.*;

/**
 * This visitor is only applied to types anywhere declared in the program and is
 * responsible for computing their sizes.
 *
 * QUESTION: Can this run into an endless recursion with recursively defined types???
 */
public class TypeSizeCalculationVisitor extends Visitor {

    /* types */

    /* types - definable - atomic */

    @Override
    public void visit(AtomicDefinedType type) {
        // for atomic types (int, bool, real, char, string) the size is always 1
        type.setSize(1);
    }

    /* types - definable - composed */

    @Override
    public void visit(TypeArray type) {
        type.getBaseType().accept(this);
        type.setSize(type.getDim() * type.getBaseType().getSize());
    }

    @Override
    public void visit(TypeRecord type) {
        for(TypeRecord.RecordField f : type.getFields())
            f.getType().accept(this);
        int sum = 0;
        for(TypeRecord.RecordField f : type.getFields())
            sum += f.getType().getSize();
        type.setSize(sum);
    }

    @Override
    public void visit(TypePointer type) {
        // pointers have constant size of 1
        type.setSize(1);
        // do not follow pointers - size is always 1
        /*
        if(!type.getBaseType().isReference()) {
            type.getBaseType().accept(this);
        }
        */
    }

    @Override
    public void visit(TypeRef type) {
        type.getDecReferencedType().getType().accept(this);
        type.setSize(type.getDecReferencedType().getType().getSize());
    }

    /* program */

    @Override
    public void visit(Program prog) {
        // it is sufficient to go only through the programs declaration section
        for(Declaration dec : prog.getDeclarations()) dec.accept(this);
    }

    /* declarations */

    @Override
    public void visit(DeclarationType dec) {
        dec.getType().accept(this);
    }

    @Override
    public void visit(DeclarationVariable dec) {
        dec.getType().accept(this);
    }

}