package pl.procedures.linking;

import pl.abstractsyntax.Declaration.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    
    // TODO maybe this should rather be a stack ...
    private List<Level> levels; 
    
    public SymbolTable() { levels = new ArrayList<>(); }
    
    private static class Level {
        
        public Map<String, DeclarationType> types;
        public Map<String, DeclarationVariable> variables;
        public Map<String, DeclarationProc> procs;

        public Level() {
            types = new HashMap<>();  
            variables = new HashMap<>();
            procs = new HashMap<>();
        }
       
    }
    
    public void insertDecType(String tipo, DeclarationType dec) {
        levels.get(0).types.put(tipo,dec);
    }
    
    public void insertDecVar(String var, DeclarationVariable dec) {
        levels.get(0).variables.put(var, dec);
    }
    
    public void insertDecProc(String proc, DeclarationProc dec) {
        levels.get(0).procs.put(proc, dec);
    }
    
    public boolean decTypeDuplicated(String tipo) {
        return levels.get(0).types.containsKey(tipo);
    } 
    
    public boolean decVarDuplicated(String var) {
        return levels.get(0).variables.containsKey(var);
    }
    
    public boolean decProcDuplicated(String proc) {
        return levels.get(0).variables.containsKey(proc);
    }
    
    public DeclarationType decType(String type) {
        Iterator<Level> ilevels = levels.iterator();
        DeclarationType dec = null;
        while(dec == null && ilevels.hasNext()) {
           Level level = ilevels.next();
           dec = level.types.get(type);
        }
        return dec;
    }
    
    public DeclarationVariable decVar(String var) {
        Iterator<Level> ilevels = levels.iterator();
        DeclarationVariable dec = null;
        while(dec == null && ilevels.hasNext()) {
           Level level = ilevels.next();
           dec = level.variables.get(var);
        }
        return dec;
    }
    
    public DeclarationProc decProc(String proc) {
        Iterator<Level> ilevels = levels.iterator();
        DeclarationProc dec = null;
        while(dec == null && ilevels.hasNext()) {
           Level level = ilevels.next();
           dec = level.procs.get(proc);
        }
        return dec;
    }
    
    public void createLevel() { levels.add(0, new Level()); }
    
    public void removeLevel() { levels.remove(0); }
    
}

