package pl.test.backend;

import java.util.ArrayList;
import java.util.Arrays;

import pl.abstractsyntax.Declaration;
import pl.abstractsyntax.Inst;
import pl.type.Type;
import pl.type.Type.*;


public class Test2 extends ProgramWithConstructors {
    
    public Test2() {
        
        // declarations
        
        TypeRecord myInt = typeRecord();
        field(myInt, "valMyInt", Type.INT);
        Declaration decTypeMyInt = decType("MyInt", myInt);
        declarations.add(decTypeMyInt);
        
        TypeRecord list = typeRecord();
        field(list, "val", typeRef("MyInt"));
        field(list, "rest", typePoiner(typeRef("List")));
        Declaration decTypeList = decType("List", list);
        declarations.add(decTypeList);
        
        Declaration decVarList = decVar(
                typeRef("List"), "myList");
        declarations.add(decVarList);
        
        Declaration decVarArrayOfList = decVar(
                typeArray(typeRef("List"), 10),
                "myListArray"
        );
        declarations.add(decVarArrayOfList);
        
        Declaration decVarX = decVar(
                typeRef("MyInt"),
                "x"
        );
        declarations.add(decVarX);
        
        // instructions
        
        ArrayList<Inst> instructions = new ArrayList<>();
        
        Inst i0 = assig(
                select(
                        variable("x"),
                        "valMyInt"
                ),
                constantInt(1)
        );
        instructions.add(i0);
        
        Inst i01 = write(
               select(
                        variable("x"),
                        "valMyInt"
                )
        );
        instructions.add(i01);
        
        Inst i1 = assig(
                select(
                        variable("myList"),
                        "val"
                ),
                variable("x"),
        "i1");
        instructions.add(i1); // maybe put this line in construction function assig(...)
        
        Inst i2 = new_(
                select(
                        variable("myList"),
                        "rest"
                ),
        "i2");
        instructions.add(i2);
        
        Inst i3 = assig(
                select(
                        select(
                                dereference(
                                        select(
                                            variable("myList"),
                                            "rest"
                                    )
                                ),
                                "val"
                        ),
                "valMyInt"
                ),
                constantInt(2),
        "i3");
        instructions.add(i3);
        
        Inst i4 = assig(
                index(
                        variable("myListArray"),
                        constantInt(2)
                ),
                variable("myList"),
        "i4");
        instructions.add(i4);
        
        instruction = block(instructions); 
    }
    
}
