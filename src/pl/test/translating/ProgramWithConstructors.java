package pl.test.translating;

import java.util.ArrayList;

import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Mem;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Program;
import pl.type.Type.*;
import pl.type.Type.TypeRecord.RecordField;

/**
 * Convenience class for writing tests
 * constructors for expressions, instructions, declarations, programs and types
 */
public class ProgramWithConstructors extends Program {

    /* types */
    
    /* types - definable - composed */

    final TypeRef typeRef(String alias) {
        return new TypeRef(alias);
    }
    final TypeArray typeArray(DefinedType baseType, int dim) {
        return new TypeArray(baseType, dim);
    }
    final TypeRecord typeRecord() {
        return new TypeRecord(new ArrayList<>());
    }
    final RecordField field(TypeRecord record, String id, DefinedType type) {
        return record.new RecordField(id, type);
    }
    final TypePointer typePoiner(DefinedType baseType) {
        return new TypePointer(baseType);
    }


    /* program */

    final Program prog(ArrayList<Declaration> decs, Instruction inst) {
        return new Program(decs, inst);
    }

    /* declarations */

    final DeclarationVariable decVar(DefinedType type, String var) {
        return new DeclarationVariable(var, type);
    }
    final DeclarationVariable decVar(DefinedType type, String var, String linkToSource) {
        return new DeclarationVariable(var, type, linkToSource);
    }

    final DeclarationType decType(String alias, DefinedType type) {
        return new DeclarationType(alias, type, null);
    }
    final DeclarationType decType(String alias, DefinedType type, String link) {
        return new DeclarationType(alias, type, link);
    }

    /* instructions */

    /* instructions - general */

    final InstructionBlock block(ArrayList<Instruction> insts) {
        return new InstructionBlock(insts);
    }

    final InstructionAssignment assig(Mem mem, Exp exp) {
        return new InstructionAssignment(mem, exp);
    }
    final InstructionAssignment assig(Mem mem, Exp exp, String linkToSource) {
        return new InstructionAssignment(mem, exp, linkToSource);
    }

    /* instructions - IO */

    final InstructionWrite write(Exp exp) {
        return new InstructionWrite(exp);
    }
    final InstructionWrite write(Exp exp, String linkToSource) {
        return new InstructionWrite(exp, linkToSource);
    }

    /* instructions - memory */

    final Instruction new_(Mem mem) {
        return new InstructionNew(mem);
    }
    final Instruction new_(Mem mem, String linkToSource) {
        return new InstructionNew(mem, linkToSource);
    }

    final Instruction free(Mem mem) {
        return new InstructionNew(mem);
    }
    final Instruction free(Mem mem, String linkToSource) {
        return new InstructionNew(mem, linkToSource);
    }

    /* instructions - control structures */

    final InstructionWhile while_(Exp condition, Instruction body) {
        return new InstructionWhile(condition, body);
    }
    final InstructionWhile while_(Exp condition, Instruction body, String linkToSource) {
        return new InstructionWhile(condition, body, linkToSource);
    }

    final InstructionIfThen ifThen(Exp condition, Instruction body) {
        return new InstructionIfThen(condition, body);
    }
    final InstructionIfThen ifThen(Exp condition, Instruction body, String linkToSource) {
        return new InstructionIfThen(condition, body, linkToSource);
    }

    final InstructionIfThenElse ifThenElse(Exp condition, Instruction body1, Instruction body2) {
        return new InstructionIfThenElse(condition, body1, body2);
    }
    final InstructionIfThenElse ifThenElse(Exp condition, Instruction body1, Instruction body2, String linkToSource) {
        return new InstructionIfThenElse(condition, body1, body2, linkToSource);
    }

    final InstructionSwitch switch_(Exp exp, ArrayList<InstructionSwitch.Case> cases, Instruction defaultInst) {
        return new InstructionSwitch(exp, cases, defaultInst);
    }
    final InstructionSwitch switch_(Exp exp, ArrayList<InstructionSwitch.Case> cases, Instruction defaultInst, String linkToSource) {
        return new InstructionSwitch(exp, cases, defaultInst, linkToSource);
    }

    /* expressions */

    /* constants */

    final Exp constantInt(int val) { return new ConstantInt(val); }
    final Exp constantBool(boolean val) { return new ConstantBool(val); }
    final Exp constantReal(double val) { return new ConstantReal(val); }
    final Exp constantChar(char val) { return new ConstantChar(val); }
    final Exp constantString(String val) { return new ConstantString(val); }

    /* mems */

    final Variable variable(String name) {
        return new Variable(name);
    }
    final Variable variable(String name, String linkToSource) {
        return new Variable(name, linkToSource);
    }
    final Index index(Mem mem, Exp exp) {
        return new Index(mem, exp);
    }
    final Select select(Mem mem, String field) {
        return new Select(mem, field);
    }
    final Dereference dereference(Mem mem) {
        return new Dereference(mem);
    }

    /* unary operations - arithmetic */

    final Exp signChange(Exp exp) {
        return new SignChange(exp);
    }
    final Exp signChange(Exp exp, String linkToSource) {
        return new SignChange(exp, linkToSource);
    }

    /* unary operations - logical */

    final Exp not(Exp exp) {
        return new Not(exp);
    }
    final Exp not(Exp exp, String linkToSource) {
        return new Not(exp, linkToSource);
    }

    /* unary operations - explicit conversions */

    final Exp conversionInt(Exp exp) {
        return new ConversionInt(exp);
    }
    final Exp conversionInt(Exp exp, String linkToSource) {
        return new ConversionInt(exp, linkToSource);
    }

    final Exp conversionBool(Exp exp) {
        return new ConversionBool(exp);
    }
    final Exp conversionBool(Exp exp, String linkToSource) {
        return new ConversionBool(exp, linkToSource);
    }

    final Exp conversionReal(Exp exp) {
        return new ConversionReal(exp);
    }
    final Exp conversionReal(Exp exp, String linkToSource) {
        return new ConversionReal(exp, linkToSource);
    }

    final Exp conversionChar(Exp exp) {
        return new ConversionChar(exp);
    }
    final Exp conversionChar(Exp exp, String linkToSource) {
        return new ConversionChar(exp, linkToSource);
    }

    final Exp conversionString(Exp exp) {
        return new ConversionString(exp);
    }
    final Exp conversionString(Exp exp, String linkToSource) {
        return new ConversionString(exp, linkToSource);
    }

    /* binary expressions */

    /* binary expressions - miscellaneous */

    final Exp chainElement(Exp exp1, Exp exp2) {
        return new ChainElement(exp1, exp2);
    }
    final Exp chainElement(Exp exp1, Exp exp2, String linkToSource) {
        return new ChainElement(exp1, exp2, linkToSource);
    }

    /* binary operations - arithmetic */

    final Exp sum(Exp exp1, Exp exp2) {
        return new Sum(exp1, exp2);
    }
    final Exp sum(Exp exp1, Exp exp2, String linkToSource) {
        return new Sum(exp1, exp2, linkToSource);
    }

    final Exp prod(Exp exp1, Exp exp2) {
        return new Product(exp1, exp2);
    }
    final Exp prod(Exp exp1, Exp exp2, String linkToSource) {
        return new Product(exp1, exp2, linkToSource);
    }

    final Exp quot(Exp exp1, Exp exp2) {
        return new Quotient(exp1, exp2);
    }
    final Exp quot(Exp exp1, Exp exp2, String linkToSource) {
        return new Quotient(exp1, exp2, linkToSource);
    }

    final Exp diff(Exp exp1, Exp exp2) {
        return new Difference(exp1, exp2);
    }
    final Exp diff(Exp exp1, Exp exp2, String linkToSource) {
        return new Difference(exp1, exp2, linkToSource);
    }

    final Exp rest(Exp exp1, Exp exp2) {
        return new Rest(exp1, exp2);
    }
    final Exp rest(Exp exp1, Exp exp2, String linkToSource) {
        return new Rest(exp1, exp2, linkToSource);
    }

    /* binary expressions - relational */

    final Exp equal(Exp exp1, Exp exp2) {
        return new Equal(exp1, exp2);
    }
    final Exp equal(Exp exp1, Exp exp2, String linkToSource) {
        return new Equal(exp1, exp2, linkToSource);
    }

    final Exp unequal(Exp exp1, Exp exp2) {
        return new Unequal(exp1, exp2);
    }
    final Exp unequal(Exp exp1, Exp exp2, String linkToSource) {
        return new Unequal(exp1, exp2, linkToSource);
    }

    final Exp less(Exp exp1, Exp exp2) {
        return new Less(exp1, exp2);
    }
    final Exp less(Exp exp1, Exp exp2, String linkToSource) {
        return new Less(exp1, exp2, linkToSource);
    }

    final Exp lessEqual(Exp exp1, Exp exp2) {
        return new LessEqual(exp1, exp2);
    }
    final Exp lessEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new LessEqual(exp1, exp2, linkToSource);
    }

    final Exp greater(Exp exp1, Exp exp2) {
        return new Greater(exp1, exp2);
    }
    final Exp greater(Exp exp1, Exp exp2, String linkToSource) {
        return new Greater(exp1, exp2, linkToSource);
    }

    final Exp greaterEqual(Exp exp1, Exp exp2) {
        return new GreaterEqual(exp1, exp2);
    }
    final Exp greaterEqual(Exp exp1, Exp exp2, String linkToSource) {
        return new GreaterEqual(exp1, exp2, linkToSource);
    }

    /* binary operations - logical */

    final Exp and(Exp exp1, Exp exp2) {
        return new And(exp1, exp2);
    }
    final Exp and(Exp exp1, Exp exp2, String linkToSource) {
        return new And(exp1, exp2, linkToSource);
    }

    final Exp or(Exp exp1, Exp exp2) {
        return new Or(exp1, exp2);
    }
    final Exp or(Exp exp1, Exp exp2, String linkToSource) {
        return new Or(exp1, exp2, linkToSource);
    }

}
