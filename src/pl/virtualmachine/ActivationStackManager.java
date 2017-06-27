package pl.virtualmachine;

/**
 * 
 */
public class ActivationStackManager {
    
    private int fin;
    private int[] displays;
    private int pp;
    
    public ActivationStackManager(int comienzo, int fin, int ndisplays) {
       pp = comienzo; 
       this.fin = fin;
       this.displays = new int[ndisplays];
    }
    
    public int createActivationRegister(int tamdatos) {
       if ((pp + tamdatos + 1) > fin) throw new StackOverflowError();
       int base = pp;
       pp += tamdatos + 2;
       return base;
    }
    
    public int freeActivationRegister(int tamdatos) {
       pp -= tamdatos + 2;
       return pp;
    }
    
    public void fixDisplay(int d, int v) { displays[d-1] = v; }
    
    public int getDisplay(int d) { return displays[d-1]; }
    public int pp() { return pp; }
    
}
