package pl.util;

import pl.abstractsyntax.Program;
import pl.procedures.codegeneration.AddressAssignmentVisitor;
import pl.procedures.codegeneration.CodeGenerationVisitor;
import pl.procedures.codegeneration.LabelingVisitor;
import pl.procedures.linking.LinkingVisitor;
import pl.procedures.printing.PrintingVisitor;
import pl.procedures.types.TypeCheckVisitor;
import pl.procedures.types.TypeSizeCalculationVisitor;
import pl.type.Type;
import pl.virtualmachine.VirtualMachine;

/**
 *
 */
public class Compiler {

    /**
     * Implements the whole compiling procedure for a program represented in
     * its abstract tree of syntax. If compiling was succesful, then the program
     * is run on the virtual machine.
     * 
     * @param p the program to be compiled and run.
     */
    public static void processAndRun(Program p, boolean print) {
        
        // heap size this procedure will use for the virtual machine
        int heapSize = 100;
        
        /* PRINTING */
        if(print) {
            PrintingVisitor printer = new PrintingVisitor();
            p.accept(printer);
        }
        
        /* LINKING */
        LinkingVisitor linker = new LinkingVisitor();
        System.out.print("linking ... ");
        p.accept(linker);
        if(linker.isError())
            System.out.println("linker detected error, aborting\n");
        else {
            System.out.println("linking complete.");
            System.out.println("linked variables: " + linker.getSymTabVars().keySet());
            System.out.println("linked types: " + linker.getSymTabTypes().keySet());
            System.out.println();
        }
        
        // do not continue if linker produced errors
        if(linker.isError()) return;
         
        /* TYPE CHECK */
        TypeCheckVisitor typeCheck = new TypeCheckVisitor(p);
        System.out.print("type checking ... ");
        p.accept(typeCheck);
        if(p.getType().equals(Type.ERROR))
            System.out.println("type checker detected error, aborting\n");
        else
            System.out.println("type check complete.\n");
        
        // do not continue if type check produced errors
        if(p.getType().equals(Type.ERROR)) return;
        
        /* TYPE SIZE CALCULATION */
        TypeSizeCalculationVisitor typeSize
            = new TypeSizeCalculationVisitor();
        System.out.print("calculating type sizes ...");
        p.accept(typeSize);
        System.out.println("type size calculation complete.\n");

        /* LABELLING */
        LabelingVisitor labeling = new LabelingVisitor();
        System.out.print("labeling ... ");
        p.accept(labeling);
        System.out.println("labeling complete.\n");
        
        /* ADDRESS ASSIGNMENT */
        AddressAssignmentVisitor addrAssig
            = new AddressAssignmentVisitor();
        System.out.print("assigning addresses ... ");
        p.accept(addrAssig);
        System.out.println(
            "address assignment complete: " + 
            addrAssig.memorySize() + 
            " cells\n");
        
        /* PRINTING WITH ATTRIBUTES */
        if(print) {
            PrintingVisitor printer = new PrintingVisitor(true);
            p.accept(printer);
            System.out.println();
        }

        /* CREATE VIRTUAL MACHINE */
        VirtualMachine machine
            = new VirtualMachine(addrAssig.memorySize(), heapSize);

        /* CODE GENERATION */
        CodeGenerationVisitor codeGen
            = new CodeGenerationVisitor(machine);
        System.out.print("generating code ...");
        p.accept(codeGen);
        System.out.println("code generation complete: " + 
            machine.getCode().size() + " lines\n");
        if(print) {
            machine.printCode();
            System.out.println();
        }
        
        /* EXECUTE MACHINE CODE */
        machine.execute();
        System.out.println();
        machine.printState();

    }
    
}
