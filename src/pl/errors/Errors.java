package pl.errors;

import pl.abstractsyntax.LinkToSource;

/**
 * Class which handles output of errors and warnings.
 */
public class Errors {
    
    /* errors */
    
    public final static String ERROR_OPERAND_TYPES 
        = "incorrect operand types";
    
    public static final String ERROR_ASSIGNMENT
        = "incompatible types in assignment";
    
    public static final String ERROR_ID_DUPLICATED
        = "Identifier was already declared";
    
    public static final String ERROR_ID_NOT_DECLARED
        = "Undeclared identifier";
    
    public static final String ERROR_COND
        = "condition is not of type bool";
    
    public final static String ERROR_SWITCH
        = "Only expression of type int, real, bool and char allowed in switch";
    
    public final static String ERROR_NEW
        = "Operand of new must be a pointer";
    
    public final static String ERROR_FREE
        = "Operand of free must be a pointer";
    
    public final static String ERROR_DEREF
        = "Dereferenced type must be a pointer";
    
    public static String ERROR_ARRAY_INDEX
        = "Index must be integer";
    
    public static String ERROR_ARRAY
        = "Can only use the index operator with an array";
    
    /* warnings */
    
    public final static String WARNING_MEM_NOT_INITILIAZED
        = "Accessing uninitialized memory";
    
    
    /* methods for printing */
    
    public static void printError(String msg) {
        System.err.println("ERROR: " + msg);  
    }    
    
    public static void printWarning(String msg) {
        System.err.println("WARNING: " + msg);  
    }

    public static void printErrorFancy(LinkToSource node, String msg) {
        System.err.println("ERROR: " + node.getLinkToSource() + ": " + msg);
    }
}
