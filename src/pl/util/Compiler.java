package pl.util;

import pl.abstractsyntax.Program;
import pl.procedures.AddressAssignmentVisitor;
import pl.procedures.CodeGenerationVisitor;
import pl.procedures.LabelingVisitor;
import pl.procedures.LinkingVisitor;
import pl.procedures.PrintingVisitor;
import pl.procedures.TypeCheckVisitor;
import pl.procedures.TypeSizeCalculationVisitor;
import pl.type.Type;
import pl.virtualmachine.VirtualMachine;

/**
 *
 */
public class Compiler {

    public static void processAndRun(Program p) {
        
        /* LINKING */
        LinkingVisitor linker = new LinkingVisitor();
        System.out.print("linking ... ");
        p.accept(linker);
        if(linker.isError())
            System.out.println("linker detected error, aborting\n");
        else
            System.out.println("linking complete.\n");
        
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

        /* LABELLING */
        LabelingVisitor labeling = new LabelingVisitor();
        System.out.print("labeling ... ");
        p.accept(labeling);
        System.out.println("labeling complete.\n");
        
        /* TYPE SIZE CALCULATION */
        TypeSizeCalculationVisitor typeSize
            = new TypeSizeCalculationVisitor();
        System.out.print("calculating type sizes ...");
        p.accept(typeSize);
        System.out.println("type size calculation complete.\n");
        
        /* ADDRESS ASSIGNMENT */
        AddressAssignmentVisitor addrAssig
            = new AddressAssignmentVisitor();
        System.out.print("assigning addresses ... ");
        p.accept(addrAssig);
        System.out.println(
            "address assignment complete: " + 
            addrAssig.memorySize() + 
            " cells\n");

        /* CREATE VIRTUAL MACHINE */
        VirtualMachine machine
            = new VirtualMachine(addrAssig.memorySize());

        /* CODE GENERATION */
        CodeGenerationVisitor codeGen
            = new CodeGenerationVisitor(machine);
        System.out.print("generating code ...");
        p.accept(codeGen);
        System.out.println("code generation complete: " + 
            machine.getCode().size() + " lines\n");

        
        /* EXECUTE MACHINE CODE */
        machine.execute();
        System.out.println();

    }
    
}
