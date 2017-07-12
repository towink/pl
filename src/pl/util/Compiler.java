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
    
    // heap size this procedure will use for the virtual machine
    private static final int HEAP_SIZE = 1000;
    private static final int ACTIVATION_STACK_SIZE = 1000;

    private static final boolean DEBUG = true;
    private static final boolean PRINT = false;
    private static final boolean VERBOSE = false;
    private static final boolean LINKER_VERBOSE = false;

    /**
     * Implements the whole compiling procedure for a program represented in
     * its abstract tree of syntax. If compiling was succesful, then the program
     * is run on the virtual machine.
     * 
     * @param p the program to be compiled and run.
     */
    public static void processAndRun(Program p) {
        
        /* PRINTING */
        if(PRINT) {
            PrintingVisitor printer = new PrintingVisitor();
            p.accept(printer);
        }
        
        /* LINKING */
        LinkingVisitor linker = new LinkingVisitor(LINKER_VERBOSE);
        if(VERBOSE) System.out.print("linking ...\n");
        p.accept(linker);
        if(linker.isError())
            System.out.println("linker detected error, aborting\n");
        else {
            if(VERBOSE) System.out.println("linking complete.");
            if(VERBOSE) System.out.println();
        }
        
        // do not continue if linker produced errors
        if(linker.isError()) return;
         
        /* TYPE CHECK */
        TypeCheckVisitor typeCheck = new TypeCheckVisitor(p);
        if(VERBOSE) System.out.print("type checking ... ");
        p.accept(typeCheck);
        if(p.getType().equals(Type.ERROR))
            System.out.println("type checker detected error, aborting\n");
        else
            if(VERBOSE) System.out.println("type check complete.\n");
        
        // do not continue if type check produced errors
        if(p.getType().equals(Type.ERROR)) return;
        
        /* TYPE SIZE CALCULATION */
        TypeSizeCalculationVisitor typeSize
            = new TypeSizeCalculationVisitor();
        if(VERBOSE) System.out.print("calculating type sizes ...");
        p.accept(typeSize);
        if(VERBOSE) System.out.println("type size calculation complete.\n");
        
        /* ADDRESS ASSIGNMENT */
        AddressAssignmentVisitor addrAssig
            = new AddressAssignmentVisitor();
        if(VERBOSE) System.out.print("assigning addresses ... ");
        p.accept(addrAssig);
        if(VERBOSE) {System.out.println(
            "address assignment complete: " + 
            addrAssig.staticMemorySize() + 
            " cells\n");
        }
        
        /* LABELLING */
        LabelingVisitor labeling = new LabelingVisitor(DEBUG);
        if(VERBOSE) System.out.print("labeling ... ");
        p.accept(labeling);
        if(VERBOSE) System.out.println("labeling complete.\n");
        
        /* PRINTING WITH ATTRIBUTES */
        if(PRINT) {
            PrintingVisitor printer = new PrintingVisitor(true);
            p.accept(printer);
            System.out.println();
        }

        /* CREATE VIRTUAL MACHINE */
        VirtualMachine machine = new VirtualMachine(
                addrAssig.staticMemorySize(),
                ACTIVATION_STACK_SIZE,
                HEAP_SIZE,
                addrAssig.numberOfDisplays()
        );

        /* CODE GENERATION */
        CodeGenerationVisitor codeGen
            = new CodeGenerationVisitor(machine, DEBUG);
        if(VERBOSE) System.out.print("generating code ...");
        p.accept(codeGen);
        if(VERBOSE) {System.out.println("code generation complete: " + 
            machine.getCode().size() + " lines\n");
        }
        if(PRINT) {
            machine.printCode();
            System.out.println();
        }
        
        /* EXECUTE MACHINE CODE */
        machine.execute();
        System.out.println();
        //machine.printState();

    }
    
}
