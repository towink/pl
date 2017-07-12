package pl.errors;

import pl.abstractsyntax.LinkToSource;

/**
 * Class which handles output of errors and warnings.
 * 
 * There are 6 types of errors:
 * <ol>
 *     <li>lexical errors (handled by parser)</li>
 *     <li>syntactical errors (handled by parser)</li>
 *     <li>errors during the construction of the AST</li>
 *     <li>linking errors</li>
 *     <li>type check/inference errors</li>
 *     <li>VM runtime errors</li>
 * </ol>
 */
public class Errors {

    /* ERRORS */
    
    
    /* AST construction errors */
    
    public static final String ERROR_ASTCONST_ASSIGN
        = "cannot assign to value";
    
    public static final String ERROR_ASTCONST_READ
        = "cannot store value read from console in a value";
    
    public static final String ERROR_ASTCONST_NEW
        = "illegal use of NEW";
    
    public static final String ERROR_ASTCONST_FREE
        = "illegal use of FREE";
    
    public static final String ERROR_ASTCONST_CASE
        = "only literals are allowed as cases";
    
    public static final String ERROR_ASTCONST_INDEX
        = "cannot index a value";
    
    public static final String ERROR_ASTCONST_SELECT
        = "cannot select a field from a value";
    
    public static final String ERROR_ASTCONST_DEREF
        = "cannot dereference a value";
    
    
    /* linking errors */
    
    public static final String ERROR_LINKING_ID_DUPLICATED
        = "identifier was already declared";

    public static final String ERROR_LINKING_ID_NOT_DECLARED
        = "identifier not declared";
    
    
    /* type check errors */

    public final static String ERROR_TYPE_OPERAND_TYPES
        = "invalid operand types";

    public static final String ERROR_TYPE_ASSIGNMENT
        = "incompatible types in assignment";

    public static final String ERROR_TYPE_COND
        = "condition is not of type bool";

    public final static String ERROR_TYPE_SWITCH
        = "only expressions of type int, real, bool and char allowed in switch";

    public final static String ERROR_TYPE_NEW
        = "operand of new must be a pointer";

    public final static String ERROR_TYPE_FREE
        = "operand of free must be a pointer";

    public final static String ERROR_TYPE_DEREF
        = "dereferenced type must be a pointer (no null pointer)";

    public static String ERROR_TYPE_ARRAY_INDEX
        = "array index must be an integer";

    public static String ERROR_TYPE_ARRAY
        = "can only use the index operator with an array";
    
    public static String ERROR_TYPE_SELECT
        = "can only use the select operator with a record";
    
    public static String ERROR_TYPE_NUMBER_ARGUMENTS
        = "incorrect number of arguments provided";
    
    public static String ERROR_TYPE_BY_REFERENCE
        = "can not pass a value by reference";
    
    public static String ERROR_TYPE_ARG_TYPE
        = "invalid argument type";
    
    public static String ERROR_TYPE_READ
        = "can only read univariate values from console";
    
    
    /* virtual machine runtime errors */
    
    public static String ERROR_RUNTIME_INVALID_ADDRESS
       = "cannot read from or write to invalid address";
    
    public static String ERROR_RUNTIME_OUT_OF_BOUNDS
       = "array or string index out of bounds";
    
    public static String ERROR_RUNTIME_UNINITIALIZED_MEMORY
        = "reading from uninitialized memory";
    
    public static String ERROR_RUNTIME_TYPE_RUNTIME
        = "type error";
    
    /* WARNINGS */

    public final static String WARNING_RUNTIME_MEM_NOT_INITILIAZED
        = "accessing uninitialized memory";
    


    /* methods for printing */

    public static void printError(String msg) {
        System.out.println("ERROR: " + msg);
    }

    public static void printWarning(String msg) {
        System.out.println("WARNING: " + msg);
    }

    public static void printError(LinkToSource node, String msg) {
        System.out.println("ERROR: " + node.getLinkToSource() + ": " + msg);
    }
    
    
}
