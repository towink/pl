/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
/**
 *
 * @author Tobias Winkler <tobiwink@ucm.es>
 */
public class LexerTest {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Reader input = new InputStreamReader(new FileInputStream("src\\pl\\lex\\lexer_test_input.txt"));
        MyLexer al = new MyLexer(input);
        //MyLexer al = new MyLexer(System.in);
        Token t;
        do {
          t = al.yylex();
          System.out.println(t);
        }
        while(true);
    }
    
}
