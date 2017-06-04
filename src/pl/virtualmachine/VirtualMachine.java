package pl.virtualmachine;

import pl.errors.Errors;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * These types of machines are the target for our generated source code.
 * They have a stack, a simple memory (registers), a program counter and
 * a program storage.
 */
public class VirtualMachine {
    
    /* private member variables */
    
    // code containing the compiled program which the machine should execute
    private List<MachineInstruction> code;
    
    // the machine's internal stack memory
    private Stack<Value> stack;
    
    // memory
    private Value[] memory;
    DynamicMemoryManager dynamicMemoryManager;
    
    // program counter - necessary for realizing control structures
    private int pc;
    
    // we throw this exception when there is an error during execution
    private static class VirtualMachineRuntimeException extends RuntimeException {}
    
    /* constructors */
    
    public VirtualMachine(int staticMemorySize, int heapSize) {
        code = new ArrayList<>();  
        stack = new Stack<>();
        memory = new Value[staticMemorySize + heapSize];
        dynamicMemoryManager = new DynamicMemoryManager(
                staticMemorySize,
                staticMemorySize + heapSize - 1
        );
        pc = 0;
    }
   
    /* public member funtions */

    public List<MachineInstruction> getCode() {
        return code;
    }
    
    /**
     * Runs the specified program on the virtual machine.
     */
    public void execute() {
        while(pc < code.size()) {
            code.get(pc).execute();
        } 
    }
    
    /**
     * @param i MachineInstruction to be appended to the end of the code of
     *          this machine.
     */
    public void addInstruction(MachineInstruction i) {
        code.add(i); 
    }
    
    /**
     * Prints the code
     */
    public void printCode() {
        System.out.println("Code:");
        for(int i = 0; i < code.size(); i++) {
            System.out.println(" "+ i + ": " + code.get(i));
        }
    }
    
    /**
     * Prints the machine internal state, that is, the stack, program counter
     * and registers.
     */
    public void printState() {
        System.out.println("Stack:");
        for(int i = 0; i < stack.size(); i++) {
            System.out.println(" " + i + ": " + stack.get(i));
        }
        System.out.println("Memory:");
        for(int i = 0; i < memory.length; i++) {
            System.out.println(" " + i +": " + memory[i]);
        }
        System.out.println("PC:");
        System.out.println(" " + pc);
    }
    
    
    /* values */
    
    /**
     * Represents a value on the machines stack or in its registers.
     */
    private abstract class Value implements Comparable<Value> {
        public int getInt() { throw new UnsupportedOperationException(); }  
        public boolean getBool() { throw new UnsupportedOperationException(); }
        public double getReal() { throw new UnsupportedOperationException(); }
        public char getChar() { throw new UnsupportedOperationException(); }
        public String getString() { throw new UnsupportedOperationException(); }
        
        public Value signChange() {
            throw new UnsupportedOperationException("sign change does not apply to this value");
        }
        
        public ValueInt convertToInt() {
            throw new UnsupportedOperationException("value cannot be converted to int");
        }
        public ValueBool convertToBool() {
            throw new UnsupportedOperationException("value cannot be converted to bool");
        }
        public ValueReal convertToReal() {
            throw new UnsupportedOperationException("value cannot be converted to real");
        }
        public ValueChar convertToChar() {
            throw new UnsupportedOperationException("value cannot be converted to char");
        }
        public ValueString convertToString() {
            throw new UnsupportedOperationException("value cannot be converted to string");
        }

        @Override
        public int compareTo(Value o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    /**
     * Represents an integer in the evaluation stack or in the machine's memory.
     * 
     * Conversion overview:
     *      int -> bool:    false if 0, else 1
     *      int -> real:    round to nearest integer smaller or equal
     *      int -> char:    chracter with unicode associated to this integer
     *      int -> string:  a string representation of this integer 
     */
    private class ValueInt extends Value{
        private int value;
        public ValueInt(int value) {
            this.value = value; 
        }
        @Override
        public int getInt() {
            return value;
        }
        
        @Override
        public Value signChange() {
            return new ValueInt(-value);
        }
        
        @Override
        public ValueInt convertToInt() {
            return new ValueInt(value);
        }
        @Override
        public ValueBool convertToBool() {
            return new ValueBool(value != 0);
        }
        @Override
        public ValueReal convertToReal() {
            return new ValueReal((double)value);
        }
        @Override
        public ValueChar convertToChar() {
            return new ValueChar((char)value);
        }
        @Override
        public ValueString convertToString() {
            return new ValueString(Integer.toString(value));
        }
        
        @Override
        public int compareTo(Value o) {
            return Integer.valueOf(value).compareTo(o.getInt());
        }
        
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
    
    /**
     * Represents a Boolean in the evaluation stack or in the machine's memory.
     * 
     * Conversion overview:
     *      bool -> int:     1 if true, 0 if false
     *      bool -> real:    1.0 if true, 0.0 if false
     *      bool -> char:    not possible
     *      bool -> string:  not possible
     */
    private class ValueBool extends Value {
       private boolean value;
        public ValueBool(boolean value) {
            this.value = value; 
        }
        @Override
        public boolean getBool() {
            return value;
        }
        @Override
        public ValueInt convertToInt() {
            return new ValueInt(value ? 1 : 0);
        }
        @Override
        public ValueBool convertToBool() {
            return new ValueBool(value);
        }
        @Override
        public ValueReal convertToReal() {
            return new ValueReal(value ? 1.0 : 0.0);
        }
        
        @Override
        public int compareTo(Value o) {
            return Boolean.valueOf(value).compareTo(o.getBool());
        }
        
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
    
    /**
     * Represents a real in the evaluation stack or in the machine's memory.
     * 
     * Conversion overview:
     *      real -> int:     round down to nearest integer 
     *      real -> bool:    not possible
     *      real -> char:    not possible
     *      real -> string:  not possible
     */
    private class ValueReal extends Value {
        private double value;
        public ValueReal(double value) {
            this.value = value; 
        }
        @Override
        public double getReal() {
            return value;
        }
        @Override
        public String toString() {
            return String.valueOf(value);
        }
        
        @Override
        public Value signChange() {
            return new ValueReal(-value);
        }

        @Override
        public ValueInt convertToInt() {
            return new ValueInt((int)value);
        }
        
        @Override
        public int compareTo(Value o) {
            return Double.valueOf(value).compareTo(o.getReal());
        }

        @Override
        public ValueReal convertToReal() {
            return new ValueReal(value);
        }
    }
    
    /**
     * Represents a char in the evaluation stack or in the machine's memory.
     * 
     * Conversion overview:
     *      char -> int:     unicode of this char
     *      char -> bool:    not possible
     *      char -> real:    unicode of this char
     *      char -> string:  string containing only this char
     */
    private class ValueChar extends Value {
        private char value;
        public ValueChar(char value) {
            this.value = value; 
        }
        @Override
        public char getChar() {
            return value;
        }
        
        @Override
        public int compareTo(Value o) {
            return Character.valueOf(value).compareTo(o.getChar());
        }
        
        @Override
        public String toString() {
            return "'" + String.valueOf(value) + "'";
        }

        @Override
        public ValueInt convertToInt() {
            return new ValueInt((int)value);
        }
        @Override
        public ValueReal convertToReal() {
            return new ValueReal((double)value);
        }
        @Override
        public ValueChar convertToChar() {
            return new ValueChar(value);
        }
        @Override
        public ValueString convertToString() {
            return new ValueString("" + value);
        }
    }
    
    /**
     * Represents a string in the evaluation stack or in the machine's memory.
     * 
     * Conversion overview:
     *      cannot be converted to other types.
     */
    private class ValueString extends Value {
       private String value;
        public ValueString(String value) {
            this.value = value; 
        }
        @Override
        public String getString() {
            return value;
        }
        @Override
        public String toString() {
            return "\"" + String.valueOf(value) + "\"";
        }
        
        @Override
        public int compareTo(Value o) {
            return value.compareTo(o.getString());
        }

        @Override
        public ValueString convertToString() {
            return new ValueString(value);
        }
    }
    
    /**
     * Represents a value resulting of expressions with uninitialized variables.
     * Is kept as a singleton.
     * 
     * Conversion overview:
     *      cannot be converted to other types.
     */
    private final Value UNKNOWN = new ValueUnknown();
    private class ValueUnknown extends Value {
        @Override
        public String toString() {
            return "?";
        }
    }
    
    
    /* instructions */

    /**
     * Instructions are ...
     */
    private interface MachineInstruction {

        /**
         * ...
         */
        void execute();  
    }
    
    /**
     * These types of instructions pop the first element from the stack,
     * process it and push the result. Afterwards, the program counter is
     * increased by 1. The instruction has no further parameters.
     * If there is UNKNWOWN on top of the stack, the instruction has no effect.
     */
    private abstract class PopPushInstruction implements MachineInstruction {
        
        /**
         * Subclasses must implement this method to specify how the value is
         * processed.
         * 
         * @param op is the value popped from the stack
         * @return value to be pushed on the stack
         */
        protected abstract Value process(Value op);
        
        @Override
        public void execute() {
            // if there is UNKNWOWN on top --> do not do anything
            if(stack.peek() != UNKNOWN) {
                Value op = stack.pop();
                stack.push(process(op));
            }
            // in each case, increase program counter by 1
            pc++;
        }
    }
    
    /**
     * These types of instructions pop the first two elements from the stack,
     * process them and push the result. Afterwards, the program counter is
     * increased by 1. The instruction has no further parameters.
     * If there is UNKNWOWN among the first to values on the stack, then then
     * instruction pushs UNKNOWN.
     */
    private abstract class Pop2PushInstruction implements MachineInstruction {
        
        /**
         * Subclasses must implement this method to specify how the values are
         * processed.
         *  
         * @param op1 is the element below the topmost element from the stack
         * @param op2 is the topmost element from the stack
         * @return value to be pushed on the stack
         */
        protected abstract Value process(Value op1, Value op2);
        
        @Override
        public void execute() {
            Value op2 = stack.pop();
            Value op1 = stack.pop();
            // if there is UNKNWOWN on top or as second element --> push UNKNOWN
            if(op1 != UNKNOWN && op2 != UNKNOWN) {
                stack.push(process(op1, op2));
            }
            else
                stack.push(UNKNOWN);
            // in each case, increase program counter by 1
            pc++;
        }
    }
    
    /* conversion instructions */
    
    private InstructionConvertInt INSTRUCTION_CONVERT_INT = new InstructionConvertInt();
    private class InstructionConvertInt extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return op.convertToInt();
        }
        @Override
        public String toString() {
            return "convert to int";
        }
    }
    
    private InstructionConvertBool INSTRUCTION_CONVERT_BOOL = new InstructionConvertBool();
    private class InstructionConvertBool extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return op.convertToBool();
        }
        @Override
        public String toString() {
            return "convert to bool";
        }
    }
    
    private InstructionConvertReal INSTRUCTION_CONVERT_REAL = new InstructionConvertReal();
    private class InstructionConvertReal extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return op.convertToReal();
        }
        @Override
        public String toString() {
            return "convert to real";
        }
    }
    
    private InstructionConvertChar INSTRUCTION_CONVERT_CHAR = new InstructionConvertChar();
    private class InstructionConvertChar extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return op.convertToChar();
        }
        @Override
        public String toString() {
            return "convert to char";
        }
    }
    
    private InstructionConvertString INSTRUCTION_CONVERT_STRING = new InstructionConvertString();
    private class InstructionConvertString extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return op.convertToString();
        }
        @Override
        public String toString() {
            return "convert to string";
        }
    }
    
    /* arithmetic instructions */
    
    private InstructionSignChange INSTRUCTION_SIGN_CHANGE = new InstructionSignChange();
    private class InstructionSignChange extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return op.signChange();
        }
        @Override
        public String toString() {
            return "sign change";
        }
    }
    
    private InstructionAddInt INSTRUCTION_ADD_INT = new InstructionAddInt();
    private class InstructionAddInt extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueInt(op1.getInt() + op2.getInt());
        } 
        @Override
        public String toString() {
            return "add int";
        }
    }
    
    private InstructionAddReal INSTRUCTION_ADD_REAL = new InstructionAddReal();
    private class InstructionAddReal extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueReal(op1.getReal() + op2.getReal());
        } 
        @Override
        public String toString() {
            return "add real";
        }
    }
    
    private InstructionMultInt INSTRUCTION_MULT_INT = new InstructionMultInt();
    private class InstructionMultInt extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueInt(op1.getInt() * op2.getInt());
        } 
        @Override
        public String toString() {
            return "mult int";
        }
    }
    
    private InstructionMultReal INSTRUCTION_MULT_REAL = new InstructionMultReal();
    private class InstructionMultReal extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueReal(op1.getReal() * op2.getReal());
        } 
        @Override
        public String toString() {
            return "mult real";
        }
    }
    
    private InstructionSubtInt INSTRUCTION_SUBT_INT = new InstructionSubtInt();
    private class InstructionSubtInt extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueInt(op1.getInt() - op2.getInt());
        } 
        @Override
        public String toString() {
            return "subt int";
        }
    }
    
    private InstructionSubtReal INSTRUCTION_SUBT_REAL = new InstructionSubtReal();
    private class InstructionSubtReal extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueReal(op1.getReal() - op2.getReal());
        } 
        @Override
        public String toString() {
            return "subt real";
        }
    }
    
    private InstructionDivInt INSTRUCTION_DIV_INT = new InstructionDivInt();
    private class InstructionDivInt extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueInt(op1.getInt() / op2.getInt());
        } 
        @Override
        public String toString() {
            return "div int";
        }
    }
    
    private InstructionDivReal INSTRUCTION_DIV_REAL = new InstructionDivReal();
    private class InstructionDivReal extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueReal(op1.getReal() / op2.getReal());
        } 
        @Override
        public String toString() {
            return "div real";
        }
    }
    
    private InstructionRest INSTRUCTION_REST = new InstructionRest();
    private class InstructionRest extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueInt(op1.getInt() % op2.getInt());
        } 
        @Override
        public String toString() {
            return "rest";
        }
    }
    
    
    /* instructions with strings */
    
    private InstructionConcatString INSTRUCTION_CONCAT_STRING = new InstructionConcatString();
    private class InstructionConcatString extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueString(op1.getString() + op2.getString());
        } 
        @Override
        public String toString() {
            return "concat string";
        }
        
    }
    
    private InstructionChainElement INSTRUCTION_CHAIN_ELEMENT = new InstructionChainElement();
    private class InstructionChainElement extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            // what to do if index out of range?
            return new ValueChar(op1.getString().charAt(op2.getInt()));
        } 
        @Override
        public String toString() {
            return "chain element";
        }
        
    }
    
    /* relational instructions */
    
    private InstructionEqual INSTRUCTION_EQUAL = new InstructionEqual();
    private class InstructionEqual extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.compareTo(op2) == 0);
        }
        @Override
        public String toString() {
            return "equal";
        }
    }
    
    private InstructionEqualPop1 INSTRUCTION_EQUAL_POP1 = new InstructionEqualPop1();
    private class InstructionEqualPop1 extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            stack.push(op1); // push op1 back
            return new ValueBool(op1.compareTo(op2) == 0);
        }
        @Override
        public String toString() {
            return "equal pop 1";
        }
    }
    
    private InstructionUnequal INSTRUCTION_UNEQUAL = new InstructionUnequal();
    private class InstructionUnequal extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.compareTo(op2) != 0);
        }
        @Override
        public String toString() {
            return "unequal";
        }
    }
    
    private InstructionLess INSTRUCTION_LESS = new InstructionLess();
    private class InstructionLess extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.compareTo(op2) < 0);
        }
        @Override
        public String toString() {
            return "less";
        }
    }
    
    private InstructionLessEqual INSTRUCTION_LESS_EQUAL = new InstructionLessEqual();
    private class InstructionLessEqual extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.compareTo(op2) <= 0);
        }
        @Override
        public String toString() {
            return "less equal";
        }
    }
    
    private InstructionGreater INSTRUCTION_GREATER = new InstructionGreater();
    private class InstructionGreater extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.compareTo(op2) > 0);
        }
        @Override
        public String toString() {
            return "greater";
        }
    }
    
    private InstructionGreaterEqual INSTRUCTION_GREATER_EQUAL = new InstructionGreaterEqual();
    private class InstructionGreaterEqual extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.compareTo(op2) >= 0);
        }
        @Override
        public String toString() {
            return "greater equal";
        }
    }
    
    /* logical instructions */
    
    private InstructionNot INSTRUCTION_NOT = new InstructionNot();
    private class InstructionNot extends PopPushInstruction {
        @Override
        protected Value process(Value op) {
            return new ValueBool(!op.getBool());
        }
        @Override
        public String toString() {
            return "not";
        }
    }
    
    private InstructionAnd INSTRUCTION_AND = new InstructionAnd();
    private class InstructionAnd extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.getBool() && op2.getBool());
        }
        @Override
        public String toString() {
            return "and";
        }
    }
    
    private InstructionOr INSTRUCTION_OR = new InstructionOr();
    private class InstructionOr extends Pop2PushInstruction {
        @Override
        protected Value process(Value op1, Value op2) {
            return new ValueBool(op1.getBool() || op2.getBool());
        }
        @Override
        public String toString() {
            return "or";
        }
    }
    
    /* load and store instructions */
    
    private class InstructionPushInt implements MachineInstruction {
        private int value;
        public InstructionPushInt(int value) {
            this.value = value;  
        }
        @Override
        public void execute() {
            stack.push(new ValueInt(value)); 
            pc++;
        } 
        @Override
        public String toString() {
            return "pushInt(" + value + ")";
        }
    } 
    private class InstructionPushBool implements MachineInstruction {
        private boolean value;
        public InstructionPushBool(boolean value) {
            this.value = value;  
        }
        @Override
        public void execute() {
            stack.push(new ValueBool(value)); 
            pc++;
        } 
        @Override
        public String toString() {
            return "pushBool(" + value + ")";
        }
    }
    private class InstructionPushReal implements MachineInstruction {
        private double value;
        public InstructionPushReal(double value) {
            this.value = value;  
        }
        @Override
        public void execute() {
            stack.push(new ValueReal(value)); 
            pc++;
        } 
        @Override
        public String toString() {
            return "pushReal(" + value + ")";
        }
    }
    private class InstructionPushChar implements MachineInstruction {
        private char value;
        public InstructionPushChar(char value) {
            this.value = value;  
        }
        @Override
        public void execute() {
            stack.push(new ValueChar(value)); 
            pc++;
        } 
        @Override
        public String toString() {
            return "pushChar(" + value + ")";
        }
    }
    private class InstructionPushString implements MachineInstruction {
        private String value;
        public InstructionPushString(String value) {
            this.value = value;  
        }
        @Override
        public void execute() {
            stack.push(new ValueString(value)); 
            pc++;
        } 
        @Override
        public String toString() {
            return "pushString(" + value + ")";
        }
    }
    
    // "desapilaDir"
    private class InstructionPopStore implements MachineInstruction {
        private int addr;
        public InstructionPopStore(int addr) {
            this.addr = addr;  
        }
        @Override
        public void execute() {
            memory[addr] = stack.pop();
            pc++;
        } 
        @Override
        public String toString() {
            return "popAndStore(" + this.addr + ")";
        }
    }
    
    // "desapilaInd"
    private InstructionPop2Store INSTRUCTION_POP2_STORE = new InstructionPop2Store();
    private class InstructionPop2Store implements MachineInstruction {
        @Override
        public void execute() {
            Value val = stack.pop();
            int addr = stack.pop().getInt();
            if(addr >= memory.length) {
                Errors.printError(Errors.ERROR_INVALID_ADDRESS);
                throw new VirtualMachineRuntimeException();
            }
            memory[addr] = val;
            pc++;
        }
        @Override
        public String toString() { return "pop2AndStore"; }
    }
    
    // "apilaDir"
    private class InstructionLoadPush implements MachineInstruction {
        private int addr;
        public InstructionLoadPush(int addr) {
            this.addr = addr;
        }
        @Override
        public void execute() {
            if(memory[addr] == null) { 
                Errors.printWarning(Errors.WARNING_MEM_NOT_INITILIAZED);
                stack.push(UNKNOWN); 
            }     
            else 
                stack.push(memory[addr]);
            pc++;
        } 
        @Override
        public String toString() {
            return "loadAndPush(" + addr + ")";
        }
   }
    
    // "apilaInd"
    private InstructionPopLoadPush INSTRUCTION_POP_LOAD_PUSH = new InstructionPopLoadPush();
    private class InstructionPopLoadPush implements MachineInstruction {
        @Override
        public void execute() {
            int addr = stack.pop().getInt();
            if(addr >= memory.length) {
                Errors.printError(Errors.ERROR_INVALID_ADDRESS);
                throw new VirtualMachineRuntimeException();
            }
            if(memory[addr] == null) {
                Errors.printError(Errors.ERROR_UNINITIALIZED_MEMORY);
                throw new VirtualMachineRuntimeException();
            }
            stack.push(memory[addr]);
            pc++;
        }
        @Override
        public String toString() { return "loadPopAndPush"; }
    }
    
    private class InstructionAlloc implements MachineInstruction {
        private int size;
        public InstructionAlloc(int size) {
            this.size = size;
        }
        @Override
        public void execute() {
            int first = dynamicMemoryManager.alloc(size);
            stack.push(new ValueInt(first));
            pc++;
        }
        @Override
        public String toString() { return "alloc(" + size + ")"; }
    }
    private class InstructionDealloc implements MachineInstruction {
        private int size;
        public InstructionDealloc(int size) {
            this.size = size;
        }
        @Override
        public void execute() {
            int first = stack.pop().getInt();
            dynamicMemoryManager.free(first, size);
            pc++;
        }
        @Override
        public String toString() { return "dealloc(" + size + ")"; }
    }
    
    private class InstructionCopy implements MachineInstruction {
        private int size;
        public InstructionCopy(int size) {
            this.size = size;
        }
        @Override
        public void execute() {
            int addrFrom = stack.pop().getInt();
            int addrTo = stack.pop().getInt();
            if(addrFrom + size - 1 >= memory.length || addrTo + size - 1 >= memory.length) {
                Errors.printError(Errors.ERROR_INVALID_ADDRESS);
                throw new VirtualMachineRuntimeException();
            }
            for(int i = 0; i < size; i++) {
                memory[addrFrom + i] = memory[addrTo + i];
            }
            pc++;
        }
        @Override
        public String toString() { return "copy(" + size + ")"; }
    }
    
    /* IO instructions */
    
    private InstructionWrite INSTRUCTION_WRITE = new InstructionWrite();
    private class InstructionWrite implements MachineInstruction {
        @Override
        public void execute() {
            System.out.println("<<< " + stack.pop());
            pc++;
        }
        @Override
        public String toString() {
            return "pop and write";
        }
    }
    
    /* jump instructions */
    
    private class InstructionJump implements MachineInstruction {
        private int pos;
        public InstructionJump(int pos) {
            this.pos = pos;
        }
        @Override
        public void execute() {
            pc = pos;
        }
        @Override
        public String toString() {
            return "jump(" + pos + ")";
        }
    }
    
    private class InstructionJumpIfFalse implements MachineInstruction {
        private int pos;
        public InstructionJumpIfFalse(int pos) {
            this.pos = pos;
        }
        @Override
        public void execute() {
            Value value = stack.pop();
            if(!value.getBool())
                pc = pos;
            else
                pc++;
        }
        @Override
        public String toString() {
            return "jumpIfFalse(" + pos + ")";
        }
    }
    
    /* instruction constructors */
    
    // TODO instead of beeing constructors, these methods should add the
    // corresponding instructions directly to the code ???
    
    // "apila"
    public MachineInstruction pushInt(int val) { return new InstructionPushInt(val); }
    public MachineInstruction pushBool(boolean val) { return new InstructionPushBool(val); }
    public MachineInstruction pushReal(double val) { return new InstructionPushReal(val); }
    public MachineInstruction pushChar(char val) { return new InstructionPushChar(val); }
    public MachineInstruction pushString(String val) { return new InstructionPushString(val); }
        
    // "desapilaDir"
    public MachineInstruction popStore(int addr) { return new InstructionPopStore(addr); }
    // "desapilaInd"
    public MachineInstruction pop2Store() { return INSTRUCTION_POP2_STORE; }
    
    // "apilaDir"
    public MachineInstruction loadPush(int addr) { return new InstructionLoadPush(addr); }
    // "apilaInd"
    public MachineInstruction popLoadPush() { return INSTRUCTION_POP_LOAD_PUSH; }
    
    public MachineInstruction jump(int pos) { return new InstructionJump(pos); }
    public MachineInstruction jumpIfFalse(int pos) { return new InstructionJumpIfFalse(pos); }
    
    public MachineInstruction alloc(int size) { return new InstructionAlloc(size); }
    public MachineInstruction dealloc(int size) { return new InstructionDealloc(size); }
    
    public MachineInstruction copy(int size) { return new InstructionCopy(size); }
    
    public MachineInstruction write() { return INSTRUCTION_WRITE; }
    public MachineInstruction read() { throw new UnsupportedOperationException(); }

    public MachineInstruction addInt() { return INSTRUCTION_ADD_INT; }
    public MachineInstruction addReal() { return INSTRUCTION_ADD_REAL; }
    
    public MachineInstruction multInt() { return INSTRUCTION_MULT_INT; }
    public MachineInstruction multReal() { return INSTRUCTION_MULT_REAL; }
    
    public MachineInstruction subtInt() { return INSTRUCTION_SUBT_INT; }
    public MachineInstruction subtReal() { return INSTRUCTION_SUBT_REAL; }
    
    public MachineInstruction divInt() { return INSTRUCTION_DIV_INT; }
    public MachineInstruction divReal() { return INSTRUCTION_DIV_REAL; }
    
    public MachineInstruction signChange() { return INSTRUCTION_SIGN_CHANGE; }
    
    public MachineInstruction mod() { return INSTRUCTION_REST; }
    
    public MachineInstruction and() { return INSTRUCTION_AND; }
    public MachineInstruction or() { return INSTRUCTION_OR; }
    public MachineInstruction not() { return INSTRUCTION_NOT; }
    
    public MachineInstruction equal() { return INSTRUCTION_EQUAL; }
    public MachineInstruction equalPop1() { return INSTRUCTION_EQUAL_POP1; }
    public MachineInstruction unequal() { return INSTRUCTION_UNEQUAL; }
    public MachineInstruction less() { return INSTRUCTION_LESS; }
    public MachineInstruction lessEqual() { return INSTRUCTION_LESS_EQUAL; }
    public MachineInstruction greater() { return INSTRUCTION_GREATER; }
    public MachineInstruction greaterEqual() { return INSTRUCTION_GREATER_EQUAL; }
    
    public MachineInstruction concatString() { return INSTRUCTION_CONCAT_STRING; }
    public MachineInstruction chainElement() { return INSTRUCTION_CHAIN_ELEMENT; }
      
    public MachineInstruction convertInt() { return INSTRUCTION_CONVERT_INT; }
    public MachineInstruction convertBool() { return INSTRUCTION_CONVERT_BOOL; }
    public MachineInstruction convertReal() { return INSTRUCTION_CONVERT_REAL; }
    public MachineInstruction convertChar() { return INSTRUCTION_CONVERT_CHAR; }
    public MachineInstruction convertString() { return INSTRUCTION_CONVERT_STRING; }
    
}