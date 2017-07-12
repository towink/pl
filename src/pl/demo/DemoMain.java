package pl.demo;

import java.io.FileReader;
import pl.abstractsyntax.AbstractSyntaxConstructors;
import pl.abstractsyntax.Program;
import pl.frontend.MyParser;
import pl.frontend.ParseException;
import pl.frontend.TokenMgrError;
import pl.procedures.printing.PrintingVisitor;

public class DemoMain {
    
    public static void main(String[] args) throws Exception {
        

        String demoPath = "C:\\Users\\Tobias\\Dropbox\\ucm\\pl\\pl-practica\\src\\pl\\demo\\";

        String file = "listSorting.txt";

       
        MyParser parser;
        Program prog;

        //for(String test : testSet) {
            System.out.println("--------------------------------------------------------------");
            System.out.println("demo file: " + file);
            System.out.println("--------------------------------------------------------------");
            parser = new MyParser(new FileReader(demoPath + file));
            try {
                prog = parser.parse();
                //prog.accept(print);
                pl.util.Compiler.processAndRun(prog);
            }
            catch(TokenMgrError e) {
                System.out.println(e.getMessage());
            }
            catch(ParseException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
            }
            catch(AbstractSyntaxConstructors.AstConstException e) {
                e.printStackTrace();
            }
            System.out.println("\n\n\n");
        //}

    }

    
}
