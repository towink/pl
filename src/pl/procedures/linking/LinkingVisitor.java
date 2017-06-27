package pl.procedures.linking;

import pl.abstractsyntax.Mem.Variable;
import pl.errors.Errors;
import java.util.HashMap;
import java.util.Map;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Program;
import pl.procedures.Visitor;
import pl.type.Type;
import pl.type.Type.*;

/**
 * This visitor links variables, subprocedures and types to their declarations.
 *
 * After applying the visitor, isError() will return true if either
 *    - a variable that was used in an expression has never been declared or
 *    - if a variable has been declared twice.
 */
public class LinkingVisitor extends Visitor {

    private SymbolTable symbolTable;
    private boolean error;
    private final CompletionRefsVisitor crefs;
    private boolean verbose;

    public LinkingVisitor() {
        this(false);
    }
    
    public LinkingVisitor(boolean verbose) {
        symbolTable = new SymbolTable();
        error = false;
        crefs = new CompletionRefsVisitor();
        this.verbose = verbose;
    }
    
    /**
     * Explain why ...
     * 
     * This visitor is used for the second linking round.
     */
    private class CompletionRefsVisitor extends Visitor {
        
        @Override
        public void visit(DeclarationType dec) {
            dec.getType().accept(this);
        }
        
        @Override
        public void visit(DeclarationVariable dec) {
            dec.getType().accept(this);
        }
        
        @Override
        public void visit(DeclarationProc dec) {
            for(DeclarationParam p : dec.getParams()) {
                p.getType().accept(this);
            }
        }
        
        @Override
        public void visit(TypePointer p) {
            if(p.getBaseType().isReference()) {
                // this syntax calls method vitit(TypeRef) from outer class
                LinkingVisitor.this.visit(p.getBaseType().toRef());
            }
            else {
                // don't do anything??
            }
        }
        
    }
    
    private void log(String msg) {
        if(verbose) {
            System.out.println("Linker: " + msg);
        }
    }

    public boolean isError() { return this.error; }

    /* program */

    @Override
    public void visit(Program prog) {
        symbolTable.createLevel();
        log("declarations, first round");
        for(Declaration d : prog.getDeclarations()) {
            d.accept(this);
        }
        log("declarations, second round");
        for(Declaration d : prog.getDeclarations()) {
            d.accept(crefs);
        }
        log("instructions");
        prog.getInstruction().accept(this);
    }
    
    /* types */
    
    /* types - definable - composed */
    
    @Override
    public void visit(TypePointer p) {
        // TODO: explain why
        if(!p.getBaseType().isReference()) {
            p.getBaseType().accept(this);
        }
    }
    
    @Override
    public void visit(TypeRef r) {
        DeclarationType dec = symbolTable.decType(r.getAlias());
        if(dec == null) {
            error = true;
            Errors.printErrorFancy(
                dec, Errors.ERROR_ID_NOT_DECLARED +  ": " + r.getAlias());
        }
        else {
            r.setDecReferencedType(dec);
            log("linked type " + r.getAlias());
        }
    }

    /* declarations */

    @Override
    public void visit(DeclarationVariable dec) {
        // if the declared variable is already in the table --> error
        if(symbolTable.decVarDuplicated(dec.getIdent())) {
            error = true;
            Errors.printErrorFancy(
                    dec, Errors.ERROR_ID_DUPLICATED + ": " + dec.getIdent());
        }
        // otherwise add new entry in table
        else {
            symbolTable.insertDecVar(dec.getIdent(), dec);
            log("new entry in symbol table: " + dec.getIdent());
            // and process the type
            dec.getType().accept(this);
        }
    }

    @Override
    public void visit(DeclarationType dec) {
        // if the declared variable is already in the table --> error
        if(symbolTable.decTypeDuplicated(dec.getIdent())) {
            error = true;
            Errors.printErrorFancy(
                    dec, Errors.ERROR_ID_DUPLICATED + ": " + dec.getIdent());
        }
        // otherwise add new entry in table
        else {
            symbolTable.insertDecType(dec.getIdent(), dec);
            log("new entry in symbol table: " + dec.getIdent());
            // and process the type
            dec.getType().accept(this);
        }
    }
    
    @Override
    public void visit(DeclarationProc dec) {
        // if the declared variable is already in the table --> error
        if(symbolTable.decProcDuplicated(dec.getIdent())) {
            error = true;
            Errors.printErrorFancy(
                    dec, Errors.ERROR_ID_DUPLICATED + ": " + dec.getIdent());
        }
        // otherwise add new entry in table
        else {
            symbolTable.insertDecProc(dec.getIdent(), dec);
            log("new entry in symbol table: " + dec.getIdent());
            // open new level
            symbolTable.createLevel();
            for(DeclarationParam p : dec.getParams()) {
                symbolTable.insertDecVar(p.getIdent(), p);
                log("new entry in symbol table: " + p.getIdent());
                // process parameter's type
                p.getType().accept(this);
            }
            dec.getBody().accept(this);
            // finally remove this subprocedure's level
            symbolTable.removeLevel();
        }
    }
    
    /* instructions */
    
    /* instructions - general */
    
    @Override
    public void visit(InstructionBlock block) {
        symbolTable.createLevel();
        log("declaration, first round");
        for(Declaration dec : block.getDecs()) {
            dec.accept(this);
        }
        log("declaration, second round");
        for(Declaration dec : block.getDecs()) {
            dec.accept(crefs);
        }
        log("instructions");
        for(Instruction inst : block.getInsts()) {
            inst.accept(this);
        }
    }
    
    @Override
    public void visit(InstructionCall call) {
        DeclarationProc decProc = symbolTable.decProc(call.getIdentProc());
        if(decProc == null) {
            error = true;
            Errors.printErrorFancy(
                    call,
                    Errors.ERROR_ID_NOT_DECLARED + ": " + call.getIdentProc()
            );
        }
        else {
            call.setDecProc(decProc);
            log("linked proc call" + call.getIdentProc());
        }
        for(Exp arg : call.getArgs()) {
            arg.accept(this);
        }
    }
    
    /* expressions */
    
    /* expressions - mems */

    @Override
    public void visit(Variable var) {
        DeclarationVariable decVar = symbolTable.decVar(var.getName());
        // if the variable is not in the table then it has not been declared
        // --> error
        if(decVar == null) {
            error = true;
            Errors.printErrorFancy(
                    var, Errors.ERROR_ID_NOT_DECLARED + ": " + var.getName());
        }
        // otherwise we link its declaration to this variable
        else {
           var.setDec(decVar);
           log("linked variable " + var.getName());
        }
    }

}
