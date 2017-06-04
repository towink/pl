package pl.errors;

import pl.abstractsyntax.LinkToSource;

/**
 * Class which handles output of errors and warnings.
 */
public class Errors {

    /* errors */

    public final static String ERROR_OPERAND_TYPES
        = "invalid operand types";

    public static final String ERROR_ASSIGNMENT
        = "incompatible types in assignment";

    public static final String ERROR_ID_DUPLICATED
        = "identifier was already declared";

    public static final String ERROR_ID_NOT_DECLARED
        = "undeclared identifier";

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
    
    // error messages to be printed while machine runs
    public static String ERROR_INVALID_ADDRESS
       = "cannot read from or write to invalid address";

    /* warnings */

    public final static String WARNING_MEM_NOT_INITILIAZED
        = "accessing uninitialized memory";
    public static String ERROR_UNINITIALIZED_MEMORY;


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
