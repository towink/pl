package pl.test.backend;

import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.abstractsyntax.Declaration;
import java.util.ArrayList;
import pl.type.Type;


public class Test1 extends ProgramWithConstructors {

    public Test1() {
        ArrayList<Declaration> decs = new ArrayList<>();
        decs.add(decVar(Type.INT, "x", "line 1"));
        decs.add(decVar(Type.INT, "y", "line 2"));
        decs.add(decVar(Type.INT, "z", "line 3"));
        decs.add(decVar(Type.REAL, "w", "line x"));
        decs.add(decVar(Type.STRING, "myString", "line y"));
        decs.add(decVar(Type.STRING, "yourString", "line z"));

        ArrayList<Instruction> insts = new ArrayList<>();
        insts.add(assig(
           variable("x"), sum(constantInt(5), constantInt(6), "line 4"), "line 4"));
        insts.add(assig(
           variable("y"), prod(constantInt(2), constantInt(23), "line 5"), "line 5"));
        insts.add(assig(
                variable("z"), conversionInt(constantInt(0), "line 6"), "line 6"));
        insts.add(assig(
                variable("w"), quot(constantInt(1), constantReal(5), "line 7"), "line 7"));
        insts.add(assig(
                variable("myString"), constantString("hallo "), "line 8"));
        insts.add(assig(
                variable("yourString"), constantString("Welt"), "line 9"));
        insts.add(assig(
                variable("myString"), sum(variable("myString"), variable("yourString")), "line 10"));
        insts.add(write(variable("myString")));
        
        insts.add(
            while_(
                greaterEqual(
                        constantInt(0),constantInt(1)
                ),
                write(
                        constantString("hallo")
                )
            )
        );
        
        insts.add(
            ifThen(
                greater(
                        constantInt(1),constantInt(0)
                ),
                write(
                        constantString("0 is less than 1, wow")
                )
            )
        );
        
        insts.add(
            ifThenElse(
                greater(
                        constantInt(0),constantInt(1)
                ),
                write(
                        constantString("0 is greater than 1, wow, makes no sense")
                ),
                write(
                        constantString("0 is not greater than 1, cool")
                )
            )
        );
        
        Exp switchExp = constantInt(11);
        ArrayList<InstructionSwitch.Case> cases = new ArrayList<>();
        cases.add(
                new InstructionSwitch.Case(
                    (Exp.Constant) constantInt(42),
                    write(constantString("you are equal to 42"))
                )
        );
        cases.add(
                new InstructionSwitch.Case(
                    (Exp.Constant) constantInt(99),
                    write(constantString("cool, 99"))
                )
        );
        Instruction switchDefaultInst = write(constantString("executing default instruction ..."));
        InstructionSwitch switchInst = switch_(switchExp, cases, switchDefaultInst, "line xyz");
        insts.add(switchInst);
        
        InstructionBlock mainBlock = block(insts);
        
        this.declarations = decs;
        this.instruction = mainBlock;
    }
    
}
  