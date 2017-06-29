package pl.procedures.codegeneration;

import pl.abstractsyntax.Program;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Inst;
import pl.abstractsyntax.Inst.*;
import pl.procedures.Visitor;

/**
 * This visitor is only applied to the declarations-section of a program
 * and is responsible for assigning a position in memory to each declaration.
 * 
 * This must be called after the size type calculation.
 */
public class AddressAssignmentVisitor extends Visitor {
    
    private int size;
    private int addr;
    private int level;
    private int nDisplays;

    public AddressAssignmentVisitor() {
        size = 0;
        addr = 0;
        level = 0;
        nDisplays = 0;
    }
    
    public int staticMemorySize() { return size; }
    public int numberOfDisplays() { return nDisplays; }
    
    /* program */
    
    @Override
    public void visit(Program prog) {
        for(Declaration dec : prog.getDeclarations()) {
            dec.accept(this);
        }
        int addrBeforeInst = addr;
        size = 0;
        prog.getInstruction().accept(this);
        size += addrBeforeInst;
    }
    
    /* declarations */
    
    @Override
    public void visit(DeclarationVariable dec) {
        dec.setDir(addr);
        dec.setLevel(level);
        if(dec.isParamByRef()) {
            addr++;
        }
        else {
            addr += dec.getType().getSize();
        }
    }
    
    @Override
    public void visit(DeclarationProc dec) {
        int addrBeforeProc = addr;
        addr = 0;
        level++;
        if(nDisplays < level) {
            nDisplays = level;
        }
        for(DeclarationParam p : dec.getParams()) {
            p.accept(this);
        }
        int paramSize = addr;
        dec.getBody().accept(this);
        dec.setLevel(level);
        dec.setSize(size + paramSize);
        level--;
        addr = addrBeforeProc;
    }
    
    /* instructions */
    
    /* instructions - general */
    
    @Override
    public void visit(InstructionBlock block) {
        int addrBeforeBlock = addr; 
        for(Declaration dec : block.getDecs()) {
            dec.accept(this);
        }
        int blockSize = 0;
        for(Inst i: block.getInsts()) {
            size = 0;
            i.accept(this);
            if(size > blockSize) {
               blockSize = size;
            }
        }    
        size = blockSize + (addr - addrBeforeBlock);
        addr = addrBeforeBlock; 
    }
    
}