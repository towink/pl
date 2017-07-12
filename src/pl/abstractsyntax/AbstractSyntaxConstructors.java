package pl.abstractsyntax;

import java.util.ArrayList;
import pl.abstractsyntax.Declaration.DeclarationParam;
import pl.abstractsyntax.Exp.Constant;
import pl.abstractsyntax.Inst.*;
import pl.abstractsyntax.Inst.InstructionSwitch.Case;
import pl.errors.Errors;
import pl.type.Type;

public class AbstractSyntaxConstructors {
    
    public static class AstConstException extends RuntimeException {};
        
    /*
    Helper types and constructors for the AST constructor.
    These types themselves are NOT part of the AS.
    */
    
    public int toInt(String s) {
        return Integer.parseInt(s);
    }
    
    public double toReal(String s) {
        return Double.parseDouble(s);
    }
    
    public char toChar(String s) {
        if(s.length() == 3) {
            // for example 'a'
            return s.charAt(1);
        }
        else if(s.equals("'\\n'")) {
            return '\n';
        }
        else if(s.equals("'\\\\'")) {
            return '\\';
        }
        else if(s.equals("'\\''")) {
            return '\'';
        }
        else if(s.length() == 8) {
            return (char) Integer.parseInt( s.substring(3, 7), 16 );
        }
        throw new IllegalArgumentException();
    }
    
    // string literal to java string
    public String toString(String s) {
        //System.out.println(s);
        // TODO
        return s.substring(1, s.length() - 1);
    }

    public static class DecList extends ArrayList<Declaration> {}
    public DecList noDecs() { return new DecList(); }
    public DecList decs(DecList list, Declaration dec) {
        list.add(dec);
        return list;
    }

    public static class InstList extends ArrayList<Inst> {}
    public InstList noInsts() { return new InstList(); }
    public InstList insts(InstList list, Inst inst) {
        list.add(inst);
        return list;
    }

    public static class ParamList extends ArrayList<DeclarationParam> {}
    public ParamList noParams() { return new ParamList(); }
    public ParamList params(ParamList list, Declaration.DeclarationParam dec) {
        list.add(dec);
        return list;
    }

    public static class ArgList extends ArrayList<Exp> {}
    public ArgList noArgs() { return new ArgList(); }
    public ArgList args(ArgList list, Exp exp) {
        list.add(exp);
        return list;
    }

    public static class FieldList extends ArrayList<Type.TypeRecord.RecordField> {}
    public FieldList oneField(Type.TypeRecord.RecordField f) {
        FieldList res = new FieldList();
        res.add(f);
        return res;
    }
    public FieldList fields(FieldList list, Type.TypeRecord.RecordField f) {
        list.add(f);
        return list;
    }

    public static class CaseList extends ArrayList<Case> {}
    public CaseList noCases() { return new CaseList(); }
    public CaseList cases(CaseList list, Case c) {
        list.add(c);
        return list;
    }

    /* types */

    /* types - definable - atomic */

    public Type.DefinedType typeInt() { return Type.INT; }
    public Type.DefinedType typeBool() { return Type.BOOL; }
    public Type.DefinedType typeReal() { return Type.REAL; }
    public Type.DefinedType typeChar() { return Type.CHAR; }
    public Type.DefinedType typeString() { return Type.STRING; }

    /* types - definable - composed */

    public Type.DefinedType typeRef(String alias, String link) {
        return new Type.TypeRef(alias.toLowerCase(), link);
    }
    public Type.DefinedType typeArray(Type.DefinedType baseType, int dim) {
        return new Type.TypeArray(baseType, dim);
    }
    public Type.DefinedType typePointer(Type.DefinedType baseType) {
        return new Type.TypePointer(baseType);
    }
    public Type.DefinedType typeNullPointer() {
        return new Type.TypePointer();
    }
    public Type.DefinedType typeRecord(FieldList fields) {
        return new Type.TypeRecord(fields);
    }
    public Type.TypeRecord.RecordField field(Type.DefinedType type, String id) {
        return new Type.TypeRecord.RecordField(id.toLowerCase(), type);
    }

    /* program */

    public Program prog(DecList decs, Inst inst) {
        return new Program(decs, inst);
    }

    /* declarations */

    public Declaration decVar(Type.DefinedType type, String var, String linkToSource) {
        return new Declaration.DeclarationVariable(var.toLowerCase(), type, linkToSource);
    }

    public Declaration decType(Type.DefinedType type, String alias, String link) {
        return new Declaration.DeclarationType(alias.toLowerCase(), type, link);
    }

    public Declaration decProc(String alias, ParamList params, Inst inst, String link) {
        return new Declaration.DeclarationProc(alias.toLowerCase(), link, params, inst);
    }

    public Declaration.DeclarationParam decParam(Type.DefinedType type, boolean mode, String ident, String link) {
        return new Declaration.DeclarationParam(ident.toLowerCase(), type, mode, link);
    }

    /* instructions */

    /* instructions - general */

    public Inst assig(Exp mem, Exp exp, String linkToSource) {
        if(!mem.isMem()) {
            Errors.printError(exp, Errors.ERROR_ASTCONST_ASSIGN);
            throw new AstConstException();
        }
        return new InstructionAssignment((Mem)mem, exp, linkToSource);
    }

    public Inst block(DecList decs, InstList insts) {
        return new InstructionBlock(decs, insts);
    }

    public Inst call(String ident, ArgList args, String link) {
        return new InstructionCall(ident.toLowerCase(), args, link);
    }

    /* instructions - IO */

    public Inst write(Exp exp, String linkToSource) {
        return new InstructionWrite(exp, linkToSource);
    }

    public Inst read(Exp exp, String linkToSource) {
        if(!exp.isMem()) {
            Errors.printError(exp, Errors.ERROR_ASTCONST_READ);
            throw new AstConstException();
        }
        return new InstructionRead((Mem)exp, linkToSource);
    }

    /* instructions - memory */

    public Inst new_(Exp mem, String linkToSource) {
        if(!mem.isMem()) {
            Errors.printError(mem, Errors.ERROR_ASTCONST_NEW);
            throw new AstConstException();
        }
        return new InstructionNew((Mem)mem, linkToSource);
    }

    public Inst free(Exp mem, String linkToSource) {
        if(!mem.isMem()) {
            Errors.printError(mem, Errors.ERROR_ASTCONST_FREE);
            throw new AstConstException();
        }
        return new InstructionFree((Mem)mem, linkToSource);
    }

    /* instructions - control structures */

    public Inst while_(Exp condition, Inst body, String linkToSource) {
        return new InstructionWhile(condition, body, linkToSource);
    }

    public Inst doWhile(Inst body, Exp condition, String linkToSource) {
        return new InstructionDoWhile(condition, body, linkToSource);
    }

    public Inst ifThen(Exp condition, Inst body, String linkToSource) {
        return new InstructionIfThen(condition, body, linkToSource);
    }
    
    public Inst ifThenElse(Exp condition, Inst body1, Inst body2, String linkToSource) {
        return new InstructionIfThenElse(condition, body1, body2, linkToSource);
    }

    // switch WITHOUT default
    public Inst switch_(Exp exp, CaseList cases, String linkToSource) {
        return new InstructionSwitch(exp, cases, linkToSource);
    }

    public Inst switchDefault(Exp exp, CaseList cases, Inst defaultInst, String linkToSource) {
        return new InstructionSwitch(exp, cases, defaultInst, linkToSource);
    }

    public Case case_(Exp exp, Inst inst, String linkToSource) {
        if(!exp.isConstant()) {
            Errors.printError(exp, Errors.ERROR_ASTCONST_CASE);
            throw new AstConstException();
        }
        return new Case((Constant)exp, inst, linkToSource);
    }

    /* expressions */

    /* constants */

    public Exp constantInt(int val, String linkToSource) { return new Exp.ConstantInt(val, linkToSource); }
    public Exp constantBool(boolean val, String linkToSource) { return new Exp.ConstantBool(val, linkToSource); }
    public Exp constantReal(double val, String linkToSource) { return new Exp.ConstantReal(val, linkToSource); }
    public Exp constantChar(char val, String linkToSource) { return new Exp.ConstantChar(val, linkToSource); }
    public Exp constantString(String val, String linkToSource) { return new Exp.ConstantString(val, linkToSource); }
    public Exp constantNull(String linkToSource) { return new Exp.ConstantNull(linkToSource); }

    /* mems */

    public Exp variable(String name, String linkToSource) {
        return new Mem.Variable(name.toLowerCase(), linkToSource);
    }
    public Exp index(Exp mem, Exp exp, String linkToSource) {
        if(!mem.isMem()) {
            Errors.printError(mem, Errors.ERROR_ASTCONST_INDEX);
            throw new AstConstException();
        }
        return new Mem.Index((Mem)mem, exp, linkToSource);
    }
    public Exp select(Exp mem, String field, String linkToSource) {
        if(!mem.isMem()) {
            Errors.printError(mem, Errors.ERROR_ASTCONST_SELECT);
            throw new AstConstException();
        }
        return new Mem.Select((Mem)mem, field.toLowerCase(), linkToSource);
    }
    public Exp deref(Exp mem, String linkToSource) {
        if(!mem.isMem()) {
            Errors.printError(mem, Errors.ERROR_ASTCONST_DEREF);
            throw new AstConstException();
        }
        return new Mem.Dereference((Mem)mem, linkToSource);
    }

    /* unary operations - arithmetic */

    public Exp signChange(Exp exp, String linkToSource) {
        return new Exp.SignChange(exp, linkToSource);
    }

    /* unary operations - logical */

    public Exp not(Exp exp, String linkToSource) {
        return new Exp.Not(exp, linkToSource);
    }

    /* unary operations - explicit conversions */

    public Exp conversionInt(Exp exp, String linkToSource) {
        return new Exp.ConversionInt(exp, linkToSource);
    }
    public Exp conversionBool(Exp exp, String linkToSource) {
        return new Exp.ConversionBool(exp, linkToSource);
    }
    public Exp conversionReal(Exp exp, String linkToSource) {
        return new Exp.ConversionReal(exp, linkToSource);
    }
    public Exp conversionChar(Exp exp, String linkToSource) {
        return new Exp.ConversionChar(exp, linkToSource);
    }
    public Exp conversionString(Exp exp, String linkToSource) {
        return new Exp.ConversionString(exp, linkToSource);
    }

    /* binary expressions */

    /* binary expressions - miscellaneous */

    public Exp chainElement(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.ChainElement(exp1, exp2, linkToSource);
    }

    /* binary operations - arithmetic */

    public Exp sum(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Sum(exp1, exp2, linkToSource);
    }
    public Exp prod(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Product(exp1, exp2, linkToSource);
    }
    public Exp quot(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Quotient(exp1, exp2, linkToSource);
    }
    public Exp diff(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Difference(exp1, exp2, linkToSource);
    }
    public Exp rest(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Rest(exp1, exp2, linkToSource);
    }

    /* binary expressions - relational */

    public Exp equal(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Equal(exp1, exp2, linkToSource);
    }
    public Exp unequal(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Unequal(exp1, exp2, linkToSource);
    }
    public Exp less(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Less(exp1, exp2, linkToSource);
    }
    public Exp lessEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.LessEqual(exp1, exp2, linkToSource);
    }
    public Exp greater(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Greater(exp1, exp2, linkToSource);
    }
    public Exp greaterEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.GreaterEqual(exp1, exp2, linkToSource);
    }

    /* binary operations - logical */

    public Exp and(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.And(exp1, exp2, linkToSource);
    }
    public Exp or(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Or(exp1, exp2, linkToSource);
    }
    
}
