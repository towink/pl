package pl.testmain;

import java.io.FileReader;
import pl.abstractsyntax.AbstractSyntaxConstructors;
import pl.abstractsyntax.Program;
import pl.frontend.MyParser;
import pl.frontend.ParseException;
import pl.frontend.TokenMgrError;
import pl.procedures.printing.PrintingVisitor;

public class TestMain {
    
    public static void main(String[] args) throws Exception {
        

        String frontEndTestPath = "C:\\Users\\Tobias\\Dropbox\\ucm\\pl\\pl-practica\\src\\pl\\test\\frontend\\";

        String[] testLex = {
            "testLexErrorCharLiteral1.txt",
            "testLexErrorCharLiteral2.txt",
            "testLexErrorCharLiteral3.txt",
            "testLexErrorCharLiteral4.txt",
            "testLexErrorIdent.txt",
            "testLexErrorIntLiteral.txt",
            "testLexErrorRealLiteral.txt",
            "testLexErrorStringLiteral.txt",
            "testLexOkLiterals.txt"
        };
        
        String[] testSyntax = {
            "testSyntaxErrorBlockBracket.txt",
            "testSyntaxErrorCaseStringLiteral.txt",
            "testSyntaxErrorDecType.txt",
            "testSyntaxErrorExpBrackets.txt",
            "testSyntaxErrorParamListComma.txt",
            "testSyntaxOkFull.txt",
        };
        
        String[] testAstConst = {
            "testAstConstErrorIndex.txt",
            "testAstConstErrorSelect.txt"
        };

        String[][] tests = {testLex, testSyntax, testAstConst};

       
        MyParser parser;
        Program prog;
        PrintingVisitor print = new PrintingVisitor();
        
        for(String[] testSet : tests) {
            for(String test : testSet) {
                System.out.println("--------------------------------------------------------------");
                System.out.println("test file: " + test);
                System.out.println("--------------------------------------------------------------");
                parser = new MyParser(new FileReader(frontEndTestPath + test));
                try {
                    prog = parser.parse();
                    prog.accept(print);
                    //pl.util.Compiler.processAndRun(prog, true);
                }
                catch(TokenMgrError e) {
                    System.out.println(e.getMessage());
                }
                catch(ParseException e) {
                    System.out.println(e.getMessage());
                    //e.printStackTrace();
                }
                catch(AbstractSyntaxConstructors.AstConstException e) {
                }
                System.out.println("\n\n\n");
            }
        }
    }

    
}
