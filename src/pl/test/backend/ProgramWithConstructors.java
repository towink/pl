package pl.test.backend;

import java.util.ArrayList;

import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Inst;
import pl.abstractsyntax.Inst.*;
import pl.abstractsyntax.Inst.InstructionSwitch.Case;
import pl.abstractsyntax.Mem;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Program;
import pl.type.Type;
import pl.type.Type.*;
import pl.type.Type.TypeRecord.RecordField;

/**
 * Convenience class for writing tests
 * constructors for expressions, instructions, declarations, programs and types
 */
public class ProgramWithConstructors extends Program {

    /* helper types and constructors for the AST constructor */
    
    public int toInt(String s) {
        return Integer.parseInt(s);
    }
    
    public double toReal(String s) {
        return Double.parseDouble(s);
    }
    
    public char toChar(String s) {
        if(s.length() != 1) {
            throw new IllegalArgumentException();
        }
        return s.charAt(0);
    }

    public static class DecList extends ArrayList<Declaration> {}
    DecList noDecs() { return new DecList(); }
    DecList decs(DecList list, Declaration dec) {
        list.add(dec);
        return list;
    }

    public static class InstList extends ArrayList<Inst> {}
    InstList noInsts() { return new InstList(); }
    InstList insts(InstList list, Inst inst) {
        list.add(inst);
        return list;
    }

    public static class ParamList extends ArrayList<Declaration> {}
    ParamList noParams() { return new ParamList(); }
    ParamList params(ParamList list, DeclarationParam dec) {
        list.add(dec);
        return list;
    }

    public static class ArgList extends ArrayList<Exp> {}
    ArgList noArgs() { return new ArgList(); }
    ArgList args(ArgList list, Exp exp) {
        list.add(exp);
        return list;
    }

    public static class FieldList extends ArrayList<RecordField> {}
    FieldList oneField(RecordField f) {
        FieldList res = new FieldList();
        res.add(f);
        return res;
    }
    FieldList fields(FieldList list, RecordField f) {
        list.add(f);
        return list;
    }

    public static class CaseList extends ArrayList<Case> {}
    CaseList noCases() { return new CaseList(); }
    CaseList args(CaseList list, Case c) {
        list.add(c);
        return list;
    }

    /* types */

    /* types - definable - atomic */

    DefinedType typeInt() { return Type.INT; }
    DefinedType typeBool() { return Type.BOOL; }
    DefinedType typeReal() { return Type.REAL; }
    DefinedType typeChar() { return Type.CHAR; }
    DefinedType typeString() { return Type.STRING; }

    /* types - definable - composed */

    DefinedType typeRef(String alias) {
        return new TypeRef(alias);
    }
    DefinedType typeArray(DefinedType baseType, int dim) {
        return new TypeArray(baseType, dim);
    }
    DefinedType typePoiner(DefinedType baseType) {
        return new TypePointer(baseType);
    }
    DefinedType typeRecord(FieldList fields) {
        return new TypeRecord(fields);
    }
    RecordField field(DefinedType type, String id) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* program */

    Program prog(DecList decs, Inst inst) {
        return new Program(decs, inst);
    }

    /* declarations */

    Declaration decVar(DefinedType type, String var) {
        return new DeclarationVariable(var, type);
    }
    Declaration decVar(DefinedType type, String var, String linkToSource) {
        return new DeclarationVariable(var, type, linkToSource);
    }

    Declaration decType(String alias, DefinedType type) {
        return new DeclarationType(alias, type, null);
    }
    Declaration decType(String alias, DefinedType type, String link) {
        return new DeclarationType(alias, type, link);
    }

    Declaration decProc(String alias, ParamList params, Inst inst) {
        return new DeclarationProc(alias, (DeclarationParam[])params.toArray(), inst);
    }
    Declaration decProc(String alias, ParamList params, Inst inst, String link) {
        return new DeclarationProc(alias, link, (DeclarationParam[])params.toArray(), inst);
    }

    DeclarationParam decParam(DefinedType type, boolean mode, String ident) {
        return new DeclarationParam(ident, type, mode);
    }
    DeclarationParam decParam(DefinedType type, boolean mode, String ident, String link) {
        return new DeclarationParam(ident, type, mode, link);
    }

    /* instructions */

    /* instructions - general */

    Inst assig(Exp mem, Exp exp) {
        // TODO check if mem is really a mem
        return new InstructionAssignment((Mem)mem, exp);
    }
    Inst assig(Exp mem, Exp exp, String linkToSource) {
        // TODO check if mem is really a mem
        return new InstructionAssignment((Mem)mem, exp, linkToSource);
    }

    Inst block(DecList decs, InstList insts) {
        return new InstructionBlock((Declaration[])decs.toArray(), insts);
    }

    Inst call(String ident, ArgList args) {
        return new InstructionCall(ident, (Exp[])args.toArray());
    }
    Inst call(String ident, ArgList args, String link) {
        return new InstructionCall(ident, (Exp[])args.toArray(), link);
    }

    /* instructions - IO */

    Inst write(Exp exp) {
        return new InstructionWrite(exp);
    }
    Inst write(Exp exp, String linkToSource) {
        return new InstructionWrite(exp, linkToSource);
    }

    Inst read(Exp exp) {
        // TODO check if exp is really a mem
        return new InstructionRead((Mem)exp);
    }
    Inst read(Exp exp, String linkToSource) {
        // TODO check if exp is really a mem
        return new InstructionRead((Mem)exp, linkToSource);
    }

    /* instructions - memory */

    Inst new_(Exp mem) {
        // TODO check if exp is really a pointer
        return new InstructionNew((Mem)mem);
    }
    Inst new_(Exp mem, String linkToSource) {
        return new InstructionNew((Mem)mem, linkToSource);
    }

    Inst free(Exp mem) {
        return new InstructionFree((Mem)mem);
    }
    Inst free(Exp mem, String linkToSource) {
        return new InstructionFree((Mem)mem, linkToSource);
    }

    /* instructions - control structures */

    Inst while_(Exp condition, Inst body) {
        return new InstructionWhile(condition, body);
    }
    Inst while_(Exp condition, Inst body, String linkToSource) {
        return new InstructionWhile(condition, body, linkToSource);
    }

    Inst doWhile(Inst body, Exp condition) {
        throw new UnsupportedOperationException("not implemented");
    }
    Inst doWhile(Inst body, Exp condition, String linkToSource) {
        throw new UnsupportedOperationException("not implemented");
    }

    Inst ifThen(Exp condition, Inst body) {
        return new InstructionIfThen(condition, body);
    }
    Inst ifThen(Exp condition, Inst body, String linkToSource) {
        return new InstructionIfThen(condition, body, linkToSource);
    }

    Inst ifThenElse(Exp condition, Inst body1, Inst body2) {
        return new InstructionIfThenElse(condition, body1, body2);
    }
    Inst ifThenElse(Exp condition, Inst body1, Inst body2, String linkToSource) {
        return new InstructionIfThenElse(condition, body1, body2, linkToSource);
    }

    // switch WITHOUT default
    Inst switch_(Exp exp, CaseList cases) {
        throw new UnsupportedOperationException("not implemented");
    }
    Inst switch_(Exp exp, CaseList cases, String linkToSource) {
        throw new UnsupportedOperationException("not implemented");
    }

    Inst switchDefault(Exp exp, CaseList cases, Inst defaultInst) {
        return new InstructionSwitch(exp, cases, defaultInst);
    }
    Inst switchDefault(Exp exp, CaseList cases, Inst defaultInst, String linkToSource) {
        return new InstructionSwitch(exp, cases, defaultInst, linkToSource);
    }

    Case case_(Exp exp, Inst inst) {
        throw new UnsupportedOperationException("not implemented");
    }
    Case case_(Exp exp, Inst inst, String linkToSource) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* expressions */

    /* constants */

    Exp constantInt(int val) { return new ConstantInt(val); }
    Exp constantBool(boolean val) { return new ConstantBool(val); }
    Exp constantReal(double val) { return new ConstantReal(val); }
    Exp constantChar(char val) { return new ConstantChar(val); }
    Exp constantString(String val) { return new ConstantString(val); }

    /* mems */

    Exp variable(String name) {
        return new Variable(name);
    }
    Exp variable(String name, String linkToSource) {
        return new Variable(name, linkToSource);
    }

    Exp index(Exp mem, Exp exp) {
        return new Index((Mem)mem, exp);
    }
    Exp index(Exp mem, Exp exp, String linkToSource) {
        return new Index((Mem)mem, exp, linkToSource);
    }

    Exp select(Exp mem, String field) {
        return new Select((Mem)mem, field);
    }
    Exp select(Exp mem, String field, String linkToSource) {
        return new Select((Mem)mem, field, linkToSource);
    }

    Exp deref(Exp mem) {
        return new Dereference((Mem)mem);
    }
    Exp deref(Exp mem, String linkToSource) {
        return new Dereference((Mem)mem, linkToSource);
    }

    /* unary operations - arithmetic */

    Exp signChange(Exp exp) {
        return new SignChange(exp);
    }
    Exp signChange(Exp exp, String linkToSource) {
        return new SignChange(exp, linkToSource);
    }

    /* unary operations - logical */

    Exp not(Exp exp) {
        return new Not(exp);
    }
    Exp not(Exp exp, String linkToSource) {
        return new Not(exp, linkToSource);
    }

    /* unary operations - explicit conversions */

    Exp conversionInt(Exp exp) {
        return new ConversionInt(exp);
    }
    Exp conversionInt(Exp exp, String linkToSource) {
        return new ConversionInt(exp, linkToSource);
    }

    Exp conversionBool(Exp exp) {
        return new ConversionBool(exp);
    }
    Exp conversionBool(Exp exp, String linkToSource) {
        return new ConversionBool(exp, linkToSource);
    }

    Exp conversionReal(Exp exp) {
        return new ConversionReal(exp);
    }
    Exp conversionReal(Exp exp, String linkToSource) {
        return new ConversionReal(exp, linkToSource);
    }

    Exp conversionChar(Exp exp) {
        return new ConversionChar(exp);
    }
    Exp conversionChar(Exp exp, String linkToSource) {
        return new ConversionChar(exp, linkToSource);
    }

    Exp conversionString(Exp exp) {
        return new ConversionString(exp);
    }
    Exp conversionString(Exp exp, String linkToSource) {
        return new ConversionString(exp, linkToSource);
    }

    /* binary expressions */

    /* binary expressions - miscellaneous */

    Exp chainElement(Exp exp1, Exp exp2) {
        return new ChainElement(exp1, exp2);
    }
    Exp chainElement(Exp exp1, Exp exp2, String linkToSource) {
        return new ChainElement(exp1, exp2, linkToSource);
    }

    /* binary operations - arithmetic */

    Exp sum(Exp exp1, Exp exp2) {
        return new Sum(exp1, exp2);
    }
    Exp sum(Exp exp1, Exp exp2, String linkToSource) {
        return new Sum(exp1, exp2, linkToSource);
    }

    Exp prod(Exp exp1, Exp exp2) {
        return new Product(exp1, exp2);
    }
    Exp prod(Exp exp1, Exp exp2, String linkToSource) {
        return new Product(exp1, exp2, linkToSource);
    }

    Exp quot(Exp exp1, Exp exp2) {
        return new Quotient(exp1, exp2);
    }
    Exp quot(Exp exp1, Exp exp2, String linkToSource) {
        return new Quotient(exp1, exp2, linkToSource);
    }

    Exp diff(Exp exp1, Exp exp2) {
        return new Difference(exp1, exp2);
    }
    Exp diff(Exp exp1, Exp exp2, String linkToSource) {
        return new Difference(exp1, exp2, linkToSource);
    }

    Exp rest(Exp exp1, Exp exp2) {
        return new Rest(exp1, exp2);
    }
    Exp rest(Exp exp1, Exp exp2, String linkToSource) {
        return new Rest(exp1, exp2, linkToSource);
    }

    /* binary expressions - relational */

    Exp equal(Exp exp1, Exp exp2) {
        return new Equal(exp1, exp2);
    }
    Exp equal(Exp exp1, Exp exp2, String linkToSource) {
        return new Equal(exp1, exp2, linkToSource);
    }

    Exp unequal(Exp exp1, Exp exp2) {
        return new Unequal(exp1, exp2);
    }
    Exp unequal(Exp exp1, Exp exp2, String linkToSource) {
        return new Unequal(exp1, exp2, linkToSource);
    }

    Exp less(Exp exp1, Exp exp2) {
        return new Less(exp1, exp2);
    }
    Exp less(Exp exp1, Exp exp2, String linkToSource) {
        return new Less(exp1, exp2, linkToSource);
    }

    Exp lessEqual(Exp exp1, Exp exp2) {
        return new LessEqual(exp1, exp2);
    }
    Exp lessEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new LessEqual(exp1, exp2, linkToSource);
    }

    Exp greater(Exp exp1, Exp exp2) {
        return new Greater(exp1, exp2);
    }
    Exp greater(Exp exp1, Exp exp2, String linkToSource) {
        return new Greater(exp1, exp2, linkToSource);
    }

    Exp greaterEqual(Exp exp1, Exp exp2) {
        return new GreaterEqual(exp1, exp2);
    }
    Exp greaterEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new GreaterEqual(exp1, exp2, linkToSource);
    }

    /* binary operations - logical */

    Exp and(Exp exp1, Exp exp2) {
        return new And(exp1, exp2);
    }
    Exp and(Exp exp1, Exp exp2, String linkToSource) {
        return new And(exp1, exp2, linkToSource);
    }

    Exp or(Exp exp1, Exp exp2) {
        return new Or(exp1, exp2);
    }
    Exp or(Exp exp1, Exp exp2, String linkToSource) {
        return new Or(exp1, exp2, linkToSource);
    }

}