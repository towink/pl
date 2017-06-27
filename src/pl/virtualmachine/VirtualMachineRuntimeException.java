package pl.virtualmachine;

import pl.errors.Errors;


public abstract class VirtualMachineRuntimeException extends RuntimeException {
    
    public VirtualMachineRuntimeException() {}
    public VirtualMachineRuntimeException(String msg) { super(msg); }

    /**
     * Thrown by virtual machine when trying to read from unitialized memory.
     */
    public static class UninitializedMemoryAccessException
            extends VirtualMachineRuntimeException {
        
        
        public UninitializedMemoryAccessException(int pc, int addr) {
            super(
                Errors.ERROR_UNINITIALIZED_MEMORY
                + ": instruction: " + pc
                + ", address: " + addr
            );
        }
        
    }
    
    /**
     * Thrown by virtual machine when array or string index out of its bounds.
     */
    public static class OutOfBoundsException
            extends VirtualMachineRuntimeException {
        
        public OutOfBoundsException(int pc) {
            super(Errors.ERROR_OUT_OF_BOUNDS + ": instruction: " + pc);
        }
        
    }
    
    /**
     * Thrown by virtual machine when trying to access a cell in memory  which
     * does not exist.
     */
    public static class InvalidAddressException
            extends VirtualMachineRuntimeException {
        
        public InvalidAddressException(int pc, int addr) {
            super(
                Errors.ERROR_INVALID_ADDRESS
                + ": instruction: " + pc
                + ", address: " + addr
            );
        }
        
    }
    
    /**
     * Thrown by virtual machine when type conversion fails or values have an
     * unexpected type.
     * This should not happen if the static type checker works correct.
     */
    public static class TypeException
            extends VirtualMachineRuntimeException {
        
        public TypeException(int pc) {
            super(Errors.ERROR_TYPE_RUNTIME + ": instruction: " + pc);
        }
        
    }
    
}


