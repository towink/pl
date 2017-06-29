package pl.test.backend;

import java.util.LinkedList;
import pl.abstractsyntax.Program;
import pl.util.Compiler;

public class TestMain {
    
    private static LinkedList<Program> tests = new LinkedList<>();
    
    public static void main(String[] args) {
        
        System.out.println("\\\"");
        System.exit(0);
        
        // create test programs
        tests.add(new Test1());
        //tests.add(new Test2());
        
        // run all tests
        for(Program test : tests) {
            Compiler.processAndRun(test, true);
        }
        
    }
    
}
