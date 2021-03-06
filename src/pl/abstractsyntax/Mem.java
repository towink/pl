package pl.abstractsyntax;

import pl.procedures.Visitor;
import pl.abstractsyntax.Declaration.*;

/**
 * Abstract base class for memory expressions.
 * 
 * These can be regarded as a concept similar (but more general) to variables.
 * Memory expressions need to consult the machines heap memory in order to be
 * evaluated.
 * They are either
 *    - variables,
 *    - an index in an array,
 *    - a field in a record or
 *    - a pointer dereference.
 */
public abstract class Mem extends Exp implements LinkToSource {
    
    private String link;
    public Mem() {
        this.link = NO_LINK_PROVIDED;
    }
    public Mem(String link) {
        this.link = link;
    }
    @Override
    public String getLinkToSource() {
        return link;
    }
    @Override
    public boolean isMem() {
        return true;
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
    /**
     */
    public static class Variable extends Mem {
        private String name;
        private DeclarationVariable dec;
        public Variable(String name) {
            this(name, null);
        }
        public Variable(String name, String link) {
            super(link);
            this.name = name;
            this.dec = null;
        }
        public String getName() {
            return name;
        }
        public DeclarationVariable getDec() {
            return dec;
        }
        public void setDec(DeclarationVariable dec) {
            this.dec = dec;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this); 
        }
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * mem[exp]
     */
    public static class Index extends Mem {
        private Mem mem;
        private Exp exp;
        public Index(Mem mem, Exp exp) {
            this.mem = mem;
            this.exp = exp;
        }
        public Index(Mem mem, Exp exp, String link) {
            super(link);
            this.mem = mem;
            this.exp = exp;
        }
        public Mem getMem() {
            return mem;
        }
        public Exp getExp() {
            return exp;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    /**
     * mem.field
     */
    public static class Select extends Mem {
        private Mem mem;
        private String field;
        private int offset;
        public Select(Mem mem, String field) {
            this.mem = mem;
            this.field = field;
            offset = -1;
        }
        public Select(Mem mem, String field, String link) {
            super(link);
            this.mem = mem;
            this.field = field;
            offset = -1;
        }
        public Mem getMem() {
            return mem;
        }
        public String getField() {
            return field;
        }
        public int getOffset() {
            return offset;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
    /**
     * *mem
     */
    public static class Dereference extends Mem {
        private Mem mem;
        public Dereference(Mem mem) {
            this.mem = mem;
        }
        public Dereference(Mem mem, String link) {
            super(link);
            this.mem = mem;
        }
        public Mem getMem() {
            return mem;
        }
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
    }
    
}