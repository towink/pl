package pl.virtualmachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pl.errors.Errors;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import pl.type.Type;
import pl.type.Type.AtomicDefinedType;
import pl.virtualmachine.VirtualMachineRuntimeException.*;

/**
 * These types of machines are the target for our generated source code.
 *
 * They have a stack, a simple memory (registers), a program counter and
 * a program storage.
 */
public class VirtualMachine {

    // code containing the compiled program which the machine should execute
    private List<MachineInstruction> code;

    // the machine's internal evaluation stack
    private Stack<Value> stack;

    // memory and mamanger
    private Value[] memory;
    private DynamicMemoryManager dynamicMemoryManager;
    private int staticMemorySize;
    private int activationStackSize;
    private int heapSize;
    
    // ...
    private ActivationStackManager activationStackManager;

    // program counter - necessary for realizing control structures
    private int pc;

    public VirtualMachine(
            int staticMemorySize,
            int activationStackSize,
            int heapSize,
            int ndisplays
    ) {
        this.staticMemorySize = staticMemorySize;
        this.activationStackSize = activationStackSize;
        this.heapSize = heapSize;
        memory = new Value[staticMemorySize + activationStackSize + heapSize];
        code = new ArrayList<>();
        stack = new Stack<>();
        dynamicMemoryManager = new DynamicMemoryManager(
                staticMemorySize + activationStackSize,
                staticMemorySize + activationStackSize + heapSize - 1
        );
        activationStackManager = new ActivationStackManager(
                staticMemorySize,
                staticMemorySize + activationStackSize - 1,
                ndisplays
        );
        pc = 0;
    }
    
    /* public member functions */


    /**
     * Runs the specified program on the virtual machine.
     */
    public void execute() {
        while(pc < code.size()) {
            code.get(pc).execute();
        }
    }

    /**
     * Adds an instruction to this machine's code.
     * @param i MachineInstruction to be appended to the end of the code of
     *          this machine.
     */
    public void addInstruction(MachineInstruction i) { code.add(i); }

    /**
     * Prints the machine's current code.
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

    public List<MachineInstruction> getCode() { return code; }
    public int getStaticMemorySize() { return staticMemorySize; }
    public int getActivationStackSize() { return activationStackSize; }
    public int getHeapSize() { return heapSize; }

    /* values */

    /**
     * Represents a value on the machines stack or in its registers.
     */
    private abstract class Value implements Comparable<Value> {
        
        public int getInt() { throw new TypeException(pc); }
        public boolean getBool() { throw new TypeException(pc); }
        public double getReal() { throw new TypeException(pc); }
        public char getChar() { throw new TypeException(pc); }
        public String getString() { throw new TypeException(pc); }

        public Value signChange() {
            throw new TypeException(pc);
        }

        public ValueInt convertToInt() {
            throw new TypeException(pc);
        }
        public ValueBool convertToBool() {
            throw new TypeException(pc);
        }
        public ValueReal convertToReal() {
            throw new TypeException(pc);
        }
        public ValueChar convertToChar() {
            throw new TypeException(pc);
        }
        public ValueString convertToString() {
            throw new TypeException(pc);
        }

        @Override
        public abstract int compareTo(Value o);

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
        @Override
        public int compareTo(Value o) {
            throw new TypeException(pc);
        }
    }


    /* instructions */

    /**
     * Represents a code instruction the virtual machine can execute.
     */
    private interface MachineInstruction {

        /**
         * Is called when the machine tries to execute this instruction.
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
            else {
                stack.push(UNKNOWN);
            }
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
            return "equalPop1";
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
    
    /* IO instructions */

    private InstructionWrite INSTRUCTION_WRITE = new InstructionWrite();
    private class InstructionWrite implements MachineInstruction {
        @Override
        public void execute() {
            System.out.println("<< " + stack.pop());
            pc++;
        }
        @Override
        public String toString() {
            return "write";
        }
    }
    
    private class InstructionRead implements MachineInstruction {
        private AtomicDefinedType targetType;
        public InstructionRead(AtomicDefinedType targetType) {
            this.targetType = targetType;
        }
        @Override
        public void execute()  {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            System.out.print(">> ");
            String input = "";
            try { input = br.readLine(); }
            catch(IOException e) {
                System.err.println("IO error");
                System.exit(1);
            }
            if(targetType == Type.BOOL) {
                // parseBoolean throws no exception and always works
                boolean v = Boolean.parseBoolean(input);
                stack.push(new ValueBool(v));
            }
            if(targetType == Type.STRING) {
                stack.push(new ValueString(input));
            }
            else if(targetType == Type.INT) {
                try{
                    int v = Integer.parseInt(input);
                    stack.push(new ValueInt(v));
                }
                catch(NumberFormatException e) {
                    stack.push(UNKNOWN);
                }
            }
            else if(targetType == Type.REAL) {
                try{
                    double v = Double.parseDouble(input);
                    stack.push(new ValueReal(v));
                }
                catch(NumberFormatException e) {
                    stack.push(UNKNOWN);
                }
            }
            else if(targetType == Type.CHAR) {
                // only succesful if length of entered string is 1
                if(input.length() == 1) {
                    stack.push(new ValueChar(input.charAt(0)));
                }
                else {
                    stack.push(UNKNOWN);
                }
            }
            pc++;
        }
        @Override
        public String toString() {
            return "read";
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
    @Deprecated
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
                throw new InvalidAddressException(pc, addr);
            }
            memory[addr] = val;
            pc++;
        }
        @Override
        public String toString() { return "pop2AndStore"; }
    }

    // "apilaDir"
    @Deprecated
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
                throw new InvalidAddressException(pc, addr);
            }
            if(memory[addr] == null) {
                throw new UninitializedMemoryAccessException(pc, addr);
            }
            stack.push(memory[addr]);
            pc++;
        }
        @Override
        public String toString() { return "popLoadPush"; }
    }
    
    // "dup"
    private InstructionDuplicate INSTRUCTION_DUPLICATE = new InstructionDuplicate();
    private class InstructionDuplicate implements MachineInstruction {
        @Override
        public void execute() {
            stack.push(stack.peek());
            pc++;
        }
        @Override
        public String toString() {
            return "duplicate";
        }
    }
    
    /* memory management instructions */

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
            if(addrFrom + size > memory.length) {
                throw new InvalidAddressException(pc, addrFrom + size);
            }
            if(addrTo + size > memory.length) {
                throw new InvalidAddressException(pc, addrTo + size);
            }
            for(int i = 0; i < size; i++) {
                memory[addrFrom + i] = memory[addrTo + i];
            }
            pc++;
        }
        @Override
        public String toString() { return "copy(" + size + ")"; }
    }
    
    /* activation stack instructions */
    
    private class InstructionActivate implements MachineInstruction {
        private int level;
        private int size;
        private int returnAddr;
        public InstructionActivate(int level, int size, int returnAddr) {
            this.level = level;
            this.size = size;
            this.returnAddr = returnAddr;
        }
        @Override
        public void execute() {
            int base = activationStackManager.createActivationRegister(size);
            memory[base] = new ValueInt(returnAddr);
            memory[base + 1] = new ValueInt(activationStackManager.getDisplay(level));
            stack.push(new ValueInt(base + 2));
            pc++;
        }
        @Override
        public String toString() {
            return "activate(" + level + ", " + size + ", " + returnAddr + ")";
        }
    }
    
    private class InstructionDeactivate implements MachineInstruction {
        private int level;
        private int size;
        public InstructionDeactivate(int level, int size) {
            this.level = level;
            this.size = size;
        }
        @Override
        public void execute() {
            int base = activationStackManager.freeActivationRegister(size);
            activationStackManager.fixDisplay(level, memory[base + 1].getInt());
            stack.push(memory[base]);
            pc++;
        }
        @Override
        public String toString() {
            return "deactivate(" + level + ", " + size + ")";
        }
    }
    
    // "setd"
    private class InstructionSetDisplay implements MachineInstruction {
        private int level;
        public InstructionSetDisplay(int level) { this.level = level; }
        @Override
        public void execute() {
            activationStackManager.fixDisplay(level, stack.pop().getInt());
        }
        @Override
        public String toString() {
            return "setDisplay(" + level + ")";
        }
    }
    
    // "apilad"
    private class InstructionPushDisplay implements MachineInstruction {
        private int level;
        public InstructionPushDisplay(int level) {
            this.level = level;
        }
        @Override
        public void execute() {
            stack.push(new ValueInt(activationStackManager.getDisplay(level)));
            pc++;
        }
        @Override
        public String toString() {
            return "pushDisplay(" + level + ")";
        }
    }
    
    /* miscellaneous instructions */
    
    private InstructionStop INSTRUCTION_STOP = new InstructionStop();
    private class InstructionStop implements MachineInstruction {
        @Override
        public void execute() {
            pc = code.size();
        }
        @Override
        public String toString() {
            return "stop";
        }
    }
    
    private InstructionNop INSTRUCTION_NOP = new InstructionNop();
    private class InstructionNop implements MachineInstruction {
        @Override
        public void execute() {
            pc++;
        }
        @Override
        public String toString() {
            return "nop";
        }
    }
    
    
    
    

    /* jump instructions */

    // "irA"
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

    // "irF"
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
    
    // "irind"
    private InstructionPopJump INSTRUCTION_POP_JUMP = new InstructionPopJump();
    private class InstructionPopJump implements MachineInstruction {
        @Override
        public void execute() {
            pc = stack.pop().getInt();
        }
        @Override
        public String toString() {
            return "popJump";
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