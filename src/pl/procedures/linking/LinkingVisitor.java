package pl.procedures.linking;

import pl.abstractsyntax.Mem.Variable;
import pl.errors.Errors;
import java.util.HashMap;
import java.util.Map;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Program;
import pl.procedures.Visitor;
import pl.type.Type;
import pl.type.Type.*;

/**
 * This visitor links variables and types to their declarations.
 *
 * After applying the visitor, isError() will return true if either
 *    - a variable that was used in an expression has never been declared or
 *    - if a variable has been declared twice.
 */
public class LinkingVisitor extends Visitor {

    private Map<String, DeclarationVariable> symTabVars;
    private Map<String, DeclarationType> symTabTypes;
    private boolean error;

    public LinkingVisitor() {
        this.symTabVars = new HashMap<>();
        this.symTabTypes = new HashMap<>();
        this.error = false;
    }

    public boolean isError() {
        return this.error;
    }

    public Map<String, DeclarationVariable> getSymTabVars() {
        return symTabVars;
    }

    public Map<String, DeclarationType> getSymTabTypes() {
        return symTabTypes;
    }

    /**
     * Explain why ...
     * 
     * This visitor is used for the second linking round.
     */
    private class CompletionRefsVisitor extends Visitor {
        @Override
        public void visit(TypeRef r) {
            // this syntax calls method vitit(TypeRef) from outer class
            LinkingVisitor.this.visit(r);
        }
    }

    /* program */

    @Override
    public void visit(Program prog) {
        // first round for declarations
        for(Declaration d : prog.getDeclarations()) d.accept(this);
        // second round for declarations
        CompletionRefsVisitor crefs = new CompletionRefsVisitor();
        for(Declaration d : prog.getDeclarations()) d.accept(crefs);
        // finally link instructions
        prog.getInstruction().accept(this);
    }

    /* variables */

    @Override
    public void visit(Variable var) {
        DeclarationVariable decVar = symTabVars.get(var.getName());
        // if the variable is not in the table then it has not been declared --> error
        if(decVar == null) {
            error = true;
            Errors.printErrorFancy(
                    var, Errors.ERROR_ID_NOT_DECLARED + ": " + var.getName());
        }
        // otherwise we link its declaration to this variable
        else {
           var.setDec(decVar);
        }
    }

    /* declarations */

    @Override
    public void visit(DeclarationVariable dec) {
        // if the declared variable is already in the table --> error
        if(symTabVars.containsKey(dec.getIdent())) {
            error = true;
            Errors.printErrorFancy(
                    dec, Errors.ERROR_ID_DUPLICATED + ": " + dec.getIdent());
        }
        // otherwise add new entry in table
        else {
            symTabVars.put(dec.getIdent(), dec);
            // and process the type
            dec.getType().accept(this);
        }
    }

    @Override
    public void visit(DeclarationType dec) {
        // if the declared variable is already in the table --> error
        if(symTabTypes.containsKey(dec.getIdent())) {
            error = true;
            Errors.printErrorFancy(
                    dec, Errors.ERROR_ID_DUPLICATED + ": " + dec.getIdent());
        }
        // otherwise add new entry in table
        else {
            symTabTypes.put(dec.getIdent(), dec);
            // and process the type
            dec.getType().accept(this);
        }
    }
    
    /* types */
    
    @Override
    public void visit(TypePointer p) {
        // TODO: explain why
        if(!p.isReference()) {
            p.getBaseType().accept(this);
        }
    }
    
    @Override
    public void visit(TypeRef r) {
        DeclarationType dec = symTabTypes.get(r.getAlias());
        if(dec == null) {
            error = true;
            Errors.printError(
                // TODO: link to source?
                Errors.ERROR_ID_NOT_DECLARED + 
                "(" + 
                r.getAlias() + 
                ")"
            );
        }
        else {
            r.setDecReferencedType(dec);
        }
    }

}
