package pl.lex;

/**
 *
 */
public class Token {
    private LexicalClass lc;
    private String lexema; // only in case of tokens that can have multiple values like constants (multivariate tokens)
    
    // information about where in the text the token appeared
    // column corresponds to the first character of the token
    private int line, column;
    
    public Token(LexicalClass lc, String lexema, int line, int column) {
        this.lc = lc;
        this.lexema = lexema;
        this.line = line;
        this.column = column;
    }
    
    public Token(LexicalClass lc, int line, int column) {
        this(lc, null, line, column);
    }
    
    public Token(LexicalClass lc, String lexema) {
        this(lc, lexema, -1, -1);
    }
    
    public Token(LexicalClass lc) {
        this(lc, null, -1, -1);
    }
    
    private boolean isMultivariate() {
        return lexema != null;
    }
    
    public LexicalClass getLexicalClass() {
        return lc;
    }
    
    public String toString() {
        return "Token(" + lc + ", " + lexema + ", " + line + ", " + column +")";
    }
}
