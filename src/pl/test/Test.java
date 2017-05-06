package pl.test;

import pl.abstractsyntax.Exp;
import pl.abstractsyntax.Instruction;
import pl.abstractsyntax.Instruction.*;
import pl.procedures.TypeCheckVisitor;
import pl.abstractsyntax.Program;
import pl.abstractsyntax.Declaration;
import java.util.ArrayList;
import pl.procedures.AddressAssignmentVisitor;
import pl.procedures.LabelingVisitor;
import pl.procedures.LinkingVisitor;
import pl.procedures.CodeGenerationVisitor;
import pl.procedures.PrintingVisitor;
import pl.type.Type;
import pl.type.TypeCompatibility;
import pl.virtualmachine.VirtualMachine;
import pl.util.Compiler;


public class Test extends Program {

    public Test() {
        ArrayList<Declaration> decs = new ArrayList<>();
        decs.add(decVar(Type.INT, "x", "line 1"));
        decs.add(decVar(Type.INT, "y", "line 2"));
        decs.add(decVar(Type.INT, "z", "line 3"));
        decs.add(decVar(Type.REAL, "w", "line x"));
        decs.add(decVar(Type.STRING, "myString", "line y"));
        decs.add(decVar(Type.STRING, "yourString", "line z"));

        ArrayList<Instruction> insts = new ArrayList<>();
        insts.add(instAssig(
           variable("x"), sum(constantInt(5), constantInt(6), "line 4"), "line 4"));
        insts.add(instAssig(
           variable("y"), prod(constantInt(2), constantInt(23), "line 5"), "line 5"));
        insts.add(instAssig(
                variable("z"), conversionInt(constantInt(0), "line 6"), "line 6"));
        insts.add(instAssig(
                variable("w"), quot(constantInt(1), constantReal(5), "line 7"), "line 7"));
        insts.add(instAssig(
                variable("myString"), constantString("hallo "), "line 8"));
        insts.add(instAssig(
                variable("yourString"), constantString("Welt"), "line 9"));
        insts.add(instAssig(
                variable("myString"), sum(variable("myString"), variable("yourString")), "line 10"));
        insts.add(instWrite(variable("myString")));
        
        insts.add(
            instWhile(
                greaterEqual(
                        constantInt(0),constantInt(1)
                ),
                instWrite(
                        constantString("hallo")
                )
            )
        );
        
        insts.add(
            instIfThen(
                greater(
                        constantInt(1),constantInt(0)
                ),
                instWrite(
                        constantString("0 is less than 1, wow")
                )
            )
        );
        
        insts.add(
            instIfThenElse(
                greater(
                        constantInt(0),constantInt(1)
                ),
                instWrite(
                        constantString("0 is greater than 1, wow, makes no sense")
                ),
                instWrite(
                        constantString("0 is not greater than 1, cool")
                )
            )
        );
        
        Exp switchExp = constantInt(11);
        ArrayList<InstructionSwitch.Case> cases = new ArrayList<>();
        cases.add(
                new InstructionSwitch.Case(
                    (Exp.Constant) constantInt(42),
                    instWrite(constantString("you are equal to 42"))
                )
        );
        cases.add(
                new InstructionSwitch.Case(
                    (Exp.Constant) constantInt(99),
                    instWrite(constantString("cool, 99"))
                )
        );
        Instruction switchDefaultInst = instWrite(constantString("executing default instruction ..."));
        InstructionSwitch switchInst = instSwitch(switchExp, cases, switchDefaultInst, "line xyz");
        insts.add(switchInst);
        
        InstructionBlock mainBlock = instBlock(insts);
        
        this.declarations = decs;
        this.instruction = mainBlock;
    }
    
    public static void main(String[] args) {
        
       
        
        /////
        /////
        
        Test program = new Test();
        
        // Compiler.processAndRun(program);
        
        PrintingVisitor print = new PrintingVisitor();
        program.accept(print);
        
        LinkingVisitor linker = new LinkingVisitor();
        System.out.print("linking ... ");
        program.accept(linker);
        System.out.println("linking complete.\n");
        if(linker.isError())
            System.out.println("linker detected error, aborting\n");
        
        // do not continue if linker produced errors
        if(!linker.isError()) {
            
            TypeCheckVisitor typeCheck = new TypeCheckVisitor(program);
            System.out.print("type checking ... ");
            program.accept(typeCheck);
            System.out.println("type check complete.\n");
            
            LabelingVisitor labeling = new LabelingVisitor();
            System.out.print("labeling ... ");
            program.accept(labeling);
            System.out.println("labeling complete.\n");
            print = new PrintingVisitor(true);
            program.accept(print);
            if(program.getType().equals(Type.ERROR))
                System.out.println("type checker detected error, aborting\n");
            
            // check if type check was succesful
            if(program.getType().equals(Type.OK)) {
                
                AddressAssignmentVisitor addrAssig
                    = new AddressAssignmentVisitor();
                System.out.print("assigning addresses ... ");
                program.accept(addrAssig);
                System.out.println(
                    "address assignment complete: " + 
                    addrAssig.memorySize() + 
                    " cells\n");
                
                // create the machine where the program should run
                VirtualMachine machine
                    = new VirtualMachine(addrAssig.memorySize());
                
                // generate machine code from the decorated progra tree
                CodeGenerationVisitor codeGen
                    = new CodeGenerationVisitor(machine);
                System.out.print("generating code ...");
                program.accept(codeGen);
                System.out.println("code generation complete: " + 
                    machine.getCode().size() + " lines\n");
                
                // show code created by visitor
                machine.printCode();
                System.out.println();
                
                // run the program on our machine!
                machine.execute();
                System.out.println();
                
                // final state after execution
                machine.printState();
                System.out.println();
            }
        }
          
    }
}
  