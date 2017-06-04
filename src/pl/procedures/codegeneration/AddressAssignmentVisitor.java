package pl.procedures.codegeneration;

import pl.abstractsyntax.Program;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.procedures.Visitor;

/**
 * This visitor is only applied to the declarations-section of a program
 * and is responsible for assigning a position in memory to each declaration.
 * 
 * This must be called after the size type calculation.
 */
public class AddressAssignmentVisitor extends Visitor {
    
    private int addr;

    public AddressAssignmentVisitor() {
        this.addr = 0;
    }
    
    public int memorySize() {
        return addr;
    }
    
    @Override
    public void visit(Program prog) {
        for(Declaration dec : prog.getDeclarations()) {
            dec.accept(this);
        }
    }
    
    @Override
    public void visit(DeclarationVariable dec) {
        dec.setDir(addr);
        addr += dec.getType().getSize();
    }   
}