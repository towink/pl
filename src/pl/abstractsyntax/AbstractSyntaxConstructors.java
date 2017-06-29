package pl.abstractsyntax;

import java.util.ArrayList;
import pl.type.Type;

public class AbstractSyntaxConstructors {
        
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

    public static class ParamList extends ArrayList<Declaration> {}
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

    public static class CaseList extends ArrayList<Inst.InstructionSwitch.Case> {}
    public CaseList noCases() { return new CaseList(); }
    public CaseList cases(CaseList list, Inst.InstructionSwitch.Case c) {
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

    public Type.DefinedType typeRef(String alias) {
        return new Type.TypeRef(alias);
    }
    public Type.DefinedType typeArray(Type.DefinedType baseType, int dim) {
        return new Type.TypeArray(baseType, dim);
    }
    public Type.DefinedType typePointer(Type.DefinedType baseType) {
        return new Type.TypePointer(baseType);
    }
    public Type.DefinedType typeRecord(FieldList fields) {
        return new Type.TypeRecord(fields);
    }
    public Type.TypeRecord.RecordField field(Type.DefinedType type, String id) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* program */

    public Program prog(DecList decs, Inst inst) {
        return new Program(decs, inst);
    }

    /* declarations */

    public Declaration decVar(Type.DefinedType type, String var) {
        return new Declaration.DeclarationVariable(var, type);
    }
    public Declaration decVar(Type.DefinedType type, String var, String linkToSource) {
        return new Declaration.DeclarationVariable(var, type, linkToSource);
    }

    public Declaration decType(String alias, Type.DefinedType type) {
        return new Declaration.DeclarationType(alias, type, null);
    }
    public Declaration decType(String alias, Type.DefinedType type, String link) {
        return new Declaration.DeclarationType(alias, type, link);
    }

    public Declaration decProc(String alias, ParamList params, Inst inst) {
        return new Declaration.DeclarationProc(alias, (Declaration.DeclarationParam[])params.toArray(), inst);
    }
    public Declaration decProc(String alias, ParamList params, Inst inst, String link) {
        return new Declaration.DeclarationProc(alias, link, (Declaration.DeclarationParam[])params.toArray(), inst);
    }

    public Declaration.DeclarationParam decParam(Type.DefinedType type, boolean mode, String ident) {
        return new Declaration.DeclarationParam(ident, type, mode);
    }
    public Declaration.DeclarationParam decParam(Type.DefinedType type, boolean mode, String ident, String link) {
        return new Declaration.DeclarationParam(ident, type, mode, link);
    }

    /* instructions */

    /* instructions - general */

    public Inst assig(Exp mem, Exp exp) {
        // TODO check if mem is really a mem
        return new Inst.InstructionAssignment((Mem)mem, exp);
    }
    public Inst assig(Exp mem, Exp exp, String linkToSource) {
        // TODO check if mem is really a mem
        return new Inst.InstructionAssignment((Mem)mem, exp, linkToSource);
    }

    public Inst block(DecList decs, InstList insts) {
        return new Inst.InstructionBlock((Declaration[])decs.toArray(), insts);
    }

    public Inst call(String ident, ArgList args) {
        return new Inst.InstructionCall(ident, (Exp[])args.toArray());
    }
    public Inst call(String ident, ArgList args, String link) {
        return new Inst.InstructionCall(ident, (Exp[])args.toArray(), link);
    }

    /* instructions - IO */

    public Inst write(Exp exp) {
        return new Inst.InstructionWrite(exp);
    }
    public Inst write(Exp exp, String linkToSource) {
        return new Inst.InstructionWrite(exp, linkToSource);
    }

    public Inst read(Exp exp) {
        // TODO check if exp is really a mem
        return new Inst.InstructionRead((Mem)exp);
    }
    public Inst read(Exp exp, String linkToSource) {
        // TODO check if exp is really a mem
        return new Inst.InstructionRead((Mem)exp, linkToSource);
    }

    /* instructions - memory */

    public Inst new_(Exp mem) {
        // TODO check if exp is really a pointer
        return new Inst.InstructionNew((Mem)mem);
    }
    public Inst new_(Exp mem, String linkToSource) {
        return new Inst.InstructionNew((Mem)mem, linkToSource);
    }

    public Inst free(Exp mem) {
        return new Inst.InstructionFree((Mem)mem);
    }
    public Inst free(Exp mem, String linkToSource) {
        return new Inst.InstructionFree((Mem)mem, linkToSource);
    }

    /* instructions - control structures */

    public Inst while_(Exp condition, Inst body) {
        return new Inst.InstructionWhile(condition, body);
    }
    public Inst while_(Exp condition, Inst body, String linkToSource) {
        return new Inst.InstructionWhile(condition, body, linkToSource);
    }

    public Inst doWhile(Inst body, Exp condition) {
        throw new UnsupportedOperationException("not implemented");
    }
    public Inst doWhile(Inst body, Exp condition, String linkToSource) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Inst ifThen(Exp condition, Inst body) {
        return new Inst.InstructionIfThen(condition, body);
    }
    public Inst ifThen(Exp condition, Inst body, String linkToSource) {
        return new Inst.InstructionIfThen(condition, body, linkToSource);
    }

    public Inst ifThenElse(Exp condition, Inst body1, Inst body2) {
        return new Inst.InstructionIfThenElse(condition, body1, body2);
    }
    public Inst ifThenElse(Exp condition, Inst body1, Inst body2, String linkToSource) {
        return new Inst.InstructionIfThenElse(condition, body1, body2, linkToSource);
    }

    // switch WITHOUT default
    public Inst switch_(Exp exp, CaseList cases) {
        throw new UnsupportedOperationException("not implemented");
    }
    public Inst switch_(Exp exp, CaseList cases, String linkToSource) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Inst switchDefault(Exp exp, CaseList cases, Inst defaultInst) {
        return new Inst.InstructionSwitch(exp, cases, defaultInst);
    }
    public Inst switchDefault(Exp exp, CaseList cases, Inst defaultInst, String linkToSource) {
        return new Inst.InstructionSwitch(exp, cases, defaultInst, linkToSource);
    }

    public Inst.InstructionSwitch.Case case_(Exp exp, Inst inst) {
        throw new UnsupportedOperationException("not implemented");
    }
    public Inst.InstructionSwitch.Case case_(Exp exp, Inst inst, String linkToSource) {
        throw new UnsupportedOperationException("not implemented");
    }

    /* expressions */

    /* constants */

    public Exp constantInt(int val) { return new Exp.ConstantInt(val); }
    public Exp constantBool(boolean val) { return new Exp.ConstantBool(val); }
    public Exp constantReal(double val) { return new Exp.ConstantReal(val); }
    public Exp constantChar(char val) { return new Exp.ConstantChar(val); }
    public Exp constantString(String val) { return new Exp.ConstantString(val); }

    /* mems */

    public Exp variable(String name) {
        return new Mem.Variable(name);
    }
    public Exp variable(String name, String linkToSource) {
        return new Mem.Variable(name, linkToSource);
    }

    public Exp index(Exp mem, Exp exp) {
        return new Mem.Index((Mem)mem, exp);
    }
    public Exp index(Exp mem, Exp exp, String linkToSource) {
        return new Mem.Index((Mem)mem, exp, linkToSource);
    }

    public Exp select(Exp mem, String field) {
        return new Mem.Select((Mem)mem, field);
    }
    public Exp select(Exp mem, String field, String linkToSource) {
        return new Mem.Select((Mem)mem, field, linkToSource);
    }

    public Exp deref(Exp mem) {
        return new Mem.Dereference((Mem)mem);
    }
    public Exp deref(Exp mem, String linkToSource) {
        return new Mem.Dereference((Mem)mem, linkToSource);
    }

    /* unary operations - arithmetic */

    public Exp signChange(Exp exp) {
        return new Exp.SignChange(exp);
    }
    public Exp signChange(Exp exp, String linkToSource) {
        return new Exp.SignChange(exp, linkToSource);
    }

    /* unary operations - logical */

    public Exp not(Exp exp) {
        return new Exp.Not(exp);
    }
    public Exp not(Exp exp, String linkToSource) {
        return new Exp.Not(exp, linkToSource);
    }

    /* unary operations - explicit conversions */

    public Exp conversionInt(Exp exp) {
        return new Exp.ConversionInt(exp);
    }
    public Exp conversionInt(Exp exp, String linkToSource) {
        return new Exp.ConversionInt(exp, linkToSource);
    }

    public Exp conversionBool(Exp exp) {
        return new Exp.ConversionBool(exp);
    }
    public Exp conversionBool(Exp exp, String linkToSource) {
        return new Exp.ConversionBool(exp, linkToSource);
    }

    public Exp conversionReal(Exp exp) {
        return new Exp.ConversionReal(exp);
    }
    public Exp conversionReal(Exp exp, String linkToSource) {
        return new Exp.ConversionReal(exp, linkToSource);
    }

    public Exp conversionChar(Exp exp) {
        return new Exp.ConversionChar(exp);
    }
    public Exp conversionChar(Exp exp, String linkToSource) {
        return new Exp.ConversionChar(exp, linkToSource);
    }

    public Exp conversionString(Exp exp) {
        return new Exp.ConversionString(exp);
    }
    public Exp conversionString(Exp exp, String linkToSource) {
        return new Exp.ConversionString(exp, linkToSource);
    }

    /* binary expressions */

    /* binary expressions - miscellaneous */

    public Exp chainElement(Exp exp1, Exp exp2) {
        return new Exp.ChainElement(exp1, exp2);
    }
    public Exp chainElement(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.ChainElement(exp1, exp2, linkToSource);
    }

    /* binary operations - arithmetic */

    public Exp sum(Exp exp1, Exp exp2) {
        return new Exp.Sum(exp1, exp2);
    }
    public Exp sum(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Sum(exp1, exp2, linkToSource);
    }

    public Exp prod(Exp exp1, Exp exp2) {
        return new Exp.Product(exp1, exp2);
    }
    public Exp prod(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Product(exp1, exp2, linkToSource);
    }

    public Exp quot(Exp exp1, Exp exp2) {
        return new Exp.Quotient(exp1, exp2);
    }
    public Exp quot(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Quotient(exp1, exp2, linkToSource);
    }

    public Exp diff(Exp exp1, Exp exp2) {
        return new Exp.Difference(exp1, exp2);
    }
    public Exp diff(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Difference(exp1, exp2, linkToSource);
    }

    public Exp rest(Exp exp1, Exp exp2) {
        return new Exp.Rest(exp1, exp2);
    }
    public Exp rest(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Rest(exp1, exp2, linkToSource);
    }

    /* binary expressions - relational */

    public Exp equal(Exp exp1, Exp exp2) {
        return new Exp.Equal(exp1, exp2);
    }
    public Exp equal(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Equal(exp1, exp2, linkToSource);
    }

    public Exp unequal(Exp exp1, Exp exp2) {
        return new Exp.Unequal(exp1, exp2);
    }
    public Exp unequal(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Unequal(exp1, exp2, linkToSource);
    }

    public Exp less(Exp exp1, Exp exp2) {
        return new Exp.Less(exp1, exp2);
    }
    public Exp less(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Less(exp1, exp2, linkToSource);
    }

    public Exp lessEqual(Exp exp1, Exp exp2) {
        return new Exp.LessEqual(exp1, exp2);
    }
    public Exp lessEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.LessEqual(exp1, exp2, linkToSource);
    }

    public Exp greater(Exp exp1, Exp exp2) {
        return new Exp.Greater(exp1, exp2);
    }
    public Exp greater(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Greater(exp1, exp2, linkToSource);
    }

    public Exp greaterEqual(Exp exp1, Exp exp2) {
        return new Exp.GreaterEqual(exp1, exp2);
    }
    public Exp greaterEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.GreaterEqual(exp1, exp2, linkToSource);
    }

    /* binary operations - logical */

    public Exp and(Exp exp1, Exp exp2) {
        return new Exp.And(exp1, exp2);
    }
    public Exp and(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.And(exp1, exp2, linkToSource);
    }

    public Exp or(Exp exp1, Exp exp2) {
        return new Exp.Or(exp1, exp2);
    }
    public Exp or(Exp exp1, Exp exp2, String linkToSource) {
        return new Exp.Or(exp1, exp2, linkToSource);
    }
}
