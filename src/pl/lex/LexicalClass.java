package pl.lex;

/**
 *
 */
public enum LexicalClass {
    
    // identifiers
    IDENT,
    
    // constants
    CONSTINT,
    CONSTREAL,
    CONSTCHAR,
    CONSTSTRING,
    
    // reserved words
    TRUE,
    FALSE,
    INT,
    BOOL,
    FLOAT,
    CHAR,
    STRING,
    STRUCT,
    TYPE,
    VAR,
    WHILE,
    DO,
    IF,
    THEN,
    ELSE,
    SWITCH,
    CASE,
    DEFAULT,
    PROC,
    CALL,
    WITH,
    NULL,
    NEW,
    DELETE,
    READ,
    WRITE,
    
    // operators and punctuation symbols
    PLUS,
    MINUS,
    ASTERISK,
    SLASH,
    PERCENT,
    EQUAL_EQUAL, // '=='
    EXCLAMATION_EQUAL,
    LESS,
    GREATER,
    LESS_EQUAL,
    GREATER_EQUAL,
    BAR_BAR, // '||'
    AND_AND, // '&&'
    EXCLAMATION,
    LEFTPARENTHESIS, // '('
    RIGHTPARENTHESIS, // ')'
    LEFTSQUAREBRACKET, // '['
    RIGHTSQUAREBRACKET, // ']'
    LEFTCURLYBRACKET, // '{'
    RIGHTCURLYBRACKET, // '}'
    COMMA,
    POINT,
    SEMICOLON,
    COLON,
    AND,
    EQUAL,
    UNDERSCORE,
    MINUS_GREATER, // '->' that arrow thing ...
    
    // seperators and comments
    SEPERATOR, // in general whitespaces
    COMMENT,
    
    // end of file
    EOF
}
