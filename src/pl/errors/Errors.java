package pl.errors;

import pl.abstractsyntax.LinkToSource;

/**
 * Class which handles output of errors and warnings.
 */
public class Errors {

    /* ERRORS */
    
    /* linking errors */
    
    public static final String ERROR_ID_DUPLICATED
        = "identifier was already declared";

    public static final String ERROR_ID_NOT_DECLARED
        = "identifier not declared";
    
    /* type check errors */

    public final static String ERROR_OPERAND_TYPES
        = "invalid operand types";

    public static final String ERROR_ASSIGNMENT
        = "incompatible types in assignment";

    public static final String ERROR_COND
        = "condition is not of type bool";

    public final static String ERROR_SWITCH
        = "only expressions of type int, real, bool and char allowed in switch";

    public final static String ERROR_NEW
        = "operand of new must be a pointer";

    public final static String ERROR_FREE
        = "operand of free must be a pointer";

    public final static String ERROR_DEREF
        = "dereferenced type must be a pointer";

    public static String ERROR_ARRAY_INDEX
        = "array index must be an integer";

    public static String ERROR_ARRAY
        = "can only use the index operator with an array";
    
    public static String ERROR_SELECT
        = "can only use the select operator with a record";
    
    public static String ERROR_NUMBER_ARGUMENTS
        = "incorrect number of arguments provided";
    
    public static String ERROR_BY_REFERENCE
        = "can not pass a value by reference";
    
    public static String ERROR_ARG_TYPE
        = "invalid argument type";
    
    public static String ERROR_READ
        = "can only read univariate values from console";
    
    /* runtime errors */
    
    public static String ERROR_INVALID_ADDRESS
       = "cannot read from or write to invalid address";
    
    public static String ERROR_OUT_OF_BOUNDS
       = "array or string index out of bounds";
    
    public static String ERROR_UNINITIALIZED_MEMORY
        = "reading from uninitialized memory";
    
    public static String ERROR_TYPE_RUNTIME
        = "type error";
    
    /* WARNINGS */

    public final static String WARNING_MEM_NOT_INITILIAZED
        = "accessing uninitialized memory";
    


    /* methods for printing */

    public static void printError(String msg) {
        System.out.println("ERROR: " + msg);
    }

    public static void printWarning(String msg) {
        System.out.println("WARNING: " + msg);
    }

    public static void printErrorFancy(LinkToSource node, String msg) {
        System.out.println("ERROR: " + node.getLinkToSource() + ": " + msg);
    }
    
    
}
