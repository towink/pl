package pl.procedures.printing;


import pl.abstractsyntax.Exp.*;
import pl.abstractsyntax.Mem.*;
import pl.abstractsyntax.Program;
import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Declaration.*;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Program.AbstractSyntaxNode;
import pl.procedures.Visitor;
import pl.type.Type.*;

/**
 * Prints a program tree in a human readable format (infex notation)
 */
public class PrintingVisitor extends Visitor {

    private boolean attributes;
    private int currIndent;
    
    /**
     * Change this variable to configure the currIndent depth used by the
     * printing visitor.
     */
    private final static int INDENTATION_DEPTH = 4;

    /**
     * Creates a simple printing visitor.
     */
    public PrintingVisitor() {
        this(false);
    }

     /**
      * Creates a printing visitor.
      * @param attributes If set to true, attributes of the nodes in the
      *        program tree will also be printed.
      */
    public PrintingVisitor(boolean attributes) {
        this.attributes = attributes;
        currIndent = 0;
    }

    private void printAttributes(AbstractSyntaxNode node) {
        if(attributes) {
            print(
                "{" +
                node.getType() + "," +
                node.getFirstInstruction() + "," +
                node.getNextInstruction() +
                "}"
            );
        }
    }

    private void printAttributes(Program prog) {
        if(attributes)
            print("{" + prog.getType() + "}");
    }


    private void indent() {
        for (int i = 0; i < currIndent; i++)
            print(" ");
    }
    
    // just for convenience
    private void print(Object obj) {
        System.out.print(obj);
    }
    
    private void println() {
        System.out.println();
    }
    
    private void println(Object obj) {
        System.out.println(obj);
    }
    
    /* types */
    
    /* atomic types */
    
    @Override
    public void visit(TypeInt type) { print("int");}
    
    @Override
    public void visit(TypeBool type) { print("bool");}
    
    @Override
    public void visit(TypeReal type) { print("real");}
    
    @Override
    public void visit(TypeChar type) { print("char");}
    
    @Override
    public void visit(TypeString type) { print("string");}
    
    @Override
    public void visit(TypeOk type) { print("ok");}
    
    @Override
    public void visit(TypeError type) { print("error");}
    
    /* composed types */
    
    @Override
    public void visit(TypeArray type) {
        type.getBaseType().accept(this);
        print("[");
        print(type.getDim());
        print("]");
    }
    
    @Override
    public void visit(TypeRecord type) {
        println("struct {");
        currIndent += INDENTATION_DEPTH;
        for(TypeRecord.RecordField f : type.getFields()) {
            indent();
            f.getType().accept(this);
            println(" " + f.getIdentifier() + ";");
        }
        currIndent -= INDENTATION_DEPTH;
        print("}");
    }
    
    @Override
    public void visit(TypePointer type) {
        type.getBaseType().accept(this);
        print("*");
    }
    
    @Override
    public void visit(TypeRef type) {
        print(type.getAlias());
    }
    
    
    /* program */

    @Override
    public void visit(Program prog) {
        for(Declaration d : prog.getDeclarations()) d.accept(this);
        println();
        if(prog.getInstruction() != null)
            prog.getInstruction().accept(this);
        printAttributes(prog);
        println();
    }
    
    /* declarations */

    @Override
    public void visit(DeclarationVariable dec) {
        dec.getType().accept(this);
        println(" " + dec.getVar() + ";");
    }
    
    @Override
    public void visit(DeclarationType dec) {
        print("typedef ");
        dec.getType().accept(this);
        println(" " + dec.getAlias() + ";");
    }

    /* instructions */
    
    /* instructions - general */

    @Override
    public void visit(InstructionAssignment inst) {
        indent();
        inst.getMem().accept(this);
        print(" = ");
        inst.getExp().accept(this);
        print(";");
        printAttributes(inst);
        println();
    }

    @Override
    public void visit(InstructionBlock block) {
        indent();
        println("{");
        currIndent += INDENTATION_DEPTH;
        for(Instruction i : block.getInstructions())
            i.accept(this);
        currIndent -= INDENTATION_DEPTH;
        indent();
        print("}");
        printAttributes(block);
        println();
    }
    
    /* instructions - IO */

    @Override
    public void visit(InstructionWrite inst) {
        indent();
        print("WRITE ");
        printAttributes(inst);
        inst.getExp().accept(this);
        print(";");
        println();
    }
    
    // missing: read ...
    
    /* instructions - memory */
    
    @Override
    public void visit(InstructionNew inst) {
        indent();
        print("NEW ");
        printAttributes(inst);
        inst.getMem().accept(this);
        print(";");
        println();
    }
    
    @Override
    public void visit(InstructionFree inst) {
        indent();
        print("FREE ");
        printAttributes(inst);
        inst.getMem().accept(this);
        print(";");
        println();
        
    }
    
    /* instructions - control structures */

    @Override
    public void visit(InstructionWhile inst) {
        indent();
        print("WHILE ");
        printAttributes(inst);
        inst.getCondition().accept(this);
        println();
        currIndent += INDENTATION_DEPTH;
        inst.getBody().accept(this);
        currIndent -= INDENTATION_DEPTH;
    }

    @Override
    public void visit(InstructionIfThen inst) {
        indent();
        print("IF ");
        printAttributes(inst);
        inst.getCondition().accept(this);
        println();
        currIndent += INDENTATION_DEPTH;
        inst.getBody().accept(this);
        currIndent -= INDENTATION_DEPTH;
    }

    @Override
    public void visit(InstructionIfThenElse inst) {
        indent();
        print("IF ");
        printAttributes(inst);
        inst.getCondition().accept(this);
        println();
        currIndent += INDENTATION_DEPTH;
        inst.getBodyIf().accept(this);
        currIndent -= INDENTATION_DEPTH;
        indent();
        print("ELSE ");
        println();
        currIndent += INDENTATION_DEPTH;
        inst.getBodyElse().accept(this);
        currIndent -= INDENTATION_DEPTH;
    }

    @Override
    public void visit(InstructionSwitch inst) {
        indent();
        print("SWITCH ");
        printAttributes(inst);
        inst.getExp().accept(this);
        println();
        currIndent += INDENTATION_DEPTH;
        for(InstructionSwitch.Case c : inst.getCases()) {
            indent();
            print("CASE ");
            c.getLiteral().accept(this);
            print(": \n");
            currIndent += INDENTATION_DEPTH;
            c.getInst().accept(this);
            currIndent -= INDENTATION_DEPTH;
        }
        indent();
        println("DEFAULT:");
        currIndent += INDENTATION_DEPTH;
        inst.getDefaultInst().accept(this);
        currIndent -= INDENTATION_DEPTH;
    }
    
    /* expressions */

    /* expressions - constants */

    @Override
    public void visit(ConstantInt exp) {
        print(exp.getValue());
        printAttributes(exp);
    }

    @Override
    public void visit(ConstantBool exp) {
        print(exp.getValue());
        printAttributes(exp);
    }

    @Override
    public void visit(ConstantReal exp) {
        print(exp.getValue());
        printAttributes(exp);
    }

    @Override
    public void visit(ConstantChar exp) {
        print("'" + exp.getValue() + "'");
        printAttributes(exp);
    }

    @Override
    public void visit(ConstantString exp) {
        print("\"" + exp.getValue() + "\"");
        printAttributes(exp);
    }

    /* expressions - mems */

    @Override
    public void visit(Variable exp) {
        print(exp.getName());
        printAttributes(exp);
    }
    
    @Override
    public void visit(Dereference der) {
        print("*");
        printAttributes(der);
        print("(");
        der.getMem().accept(this);
        print(")");
    }
    
    @Override
    public void visit(Select sel) {
        sel.getMem().accept(this);
        print("." + sel.getField());
        printAttributes(sel);
    }
    
    @Override
    public void visit(Index ind) {
        ind.getMem().accept(this);
        print("[");
        ind.getExp().accept(this);
        print("]");
        printAttributes(ind);
    }
    
    /* expressions - unary */
    
    private void printUnaryExp(UnaryExp exp, String symbol) {
        printUnaryExp(exp, symbol, false);
    }
    
    // FORMAT: If # is the symbol, then "(op1 # op2)" if brackets == true
    //                               or  "op1 # op2"  if brackets == false
    private void printUnaryExp(UnaryExp exp, String symbol, boolean brackets) {
        print(symbol);
        printAttributes(exp);
        if(brackets) print("(");
        exp.getOp().accept(this);
        if(brackets) print(")");
    }

    /* expressions - unary - miscellaneous */

    @Override
    public void visit(SignChange exp) { printUnaryExp(exp, "-"); }

    /* expressions - unary - logical */

    @Override
    public void visit(Not exp) { printUnaryExp(exp, "!", true); }

    /* expressions - unary - explicit type conversions */

    @Override
    public void visit(ConversionInt exp) { printUnaryExp(exp, "(int)"); }

    @Override
    public void visit(ConversionBool exp) { printUnaryExp(exp, "(bool)"); }

    @Override
    public void visit(ConversionReal exp) { printUnaryExp(exp, "(real)"); }

    @Override
    public void visit(ConversionChar exp) { printUnaryExp(exp, "(char)"); }

    @Override
    public void visit(ConversionString exp) { printUnaryExp(exp, "(string)"); }
    
    /* expressions - binary */
    
    private void printBinaryExpInfix(BinaryExp exp, String symbol) {
        printBinaryExpInfix(exp, symbol, true);
    }
    
    // FORMAT: If # is the symbol, then "(op1 #{attributes} op2)" if brackets == true
    //                               or  "op1 #{attributes} op2"  if brackets == false
    private void printBinaryExpInfix(BinaryExp exp, String symbol, boolean brackets) {
        if(brackets) print("(");
        exp.getOp1().accept(this);
        print(" " + symbol + " ");
        printAttributes(exp);
        exp.getOp2().accept(this);
        if(brackets) print(")");
    }

    /* expressions - binary - miscellaneous */

    @Override
    public void visit(ChainElement exp) {
        println(exp.getOp1());
        println("[");
        println(exp.getOp2());
        println("]");
    }

    /* expressions - binary - arithmetic */

    @Override
    public void visit(Sum exp) { printBinaryExpInfix(exp, "+"); }

    @Override
    public void visit(Product exp) { printBinaryExpInfix(exp, "*"); }

    @Override
    public void visit(Difference exp) { printBinaryExpInfix(exp, "-"); }

    @Override
    public void visit(Quotient exp) { printBinaryExpInfix(exp, "/"); }

    @Override
    public void visit(Rest exp) { printBinaryExpInfix(exp, "%"); }

    /* expressions - binary - relational */

    @Override
    public void visit(Equal exp) { printBinaryExpInfix(exp, "==", false); }

    @Override
    public void visit(Unequal exp) { printBinaryExpInfix(exp, "!=", false); }

    @Override
    public void visit(GreaterEqual exp) { printBinaryExpInfix(exp, ">=", false); }

    @Override
    public void visit(Greater exp) { printBinaryExpInfix(exp, ">", false); }

    @Override
    public void visit(LessEqual exp) { printBinaryExpInfix(exp, "<=", false); }

    @Override
    public void visit(Less exp) { printBinaryExpInfix(exp, "<", false); }

    /* expressions - binary - logical */

    @Override
    public void visit(And exp) { printBinaryExpInfix(exp, "&&", false); }

    @Override
    public void visit(Or exp) { printBinaryExpInfix(exp, "||", false); }

}