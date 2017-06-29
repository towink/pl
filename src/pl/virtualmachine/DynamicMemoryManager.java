package pl.virtualmachine;

public class DynamicMemoryManager {

    private final static boolean DEBUG = false;
    
    private Hole holes;

    private static class Hole {
        private int first;
        private int size;
        private Hole next;
        public Hole(int first, int size) {
            this.first = first;
            this.size = size;
            next = null;
        }
    }

    public DynamicMemoryManager(int first, int last) {
        holes = new Hole(first, (last - first) + 1);
        if(DEBUG) {
            System.out.print("START:");
            showHoles();
            System.out.println("----");
        }
    }

    public int alloc(int size) {
        Hole h = holes;
        Hole prev = null;
        while(h != null && h.size < size) {
            prev = h;
            h = h.next;
        }
        if(h == null) {
            throw new OutOfMemoryError("alloc " + size);
        }
        int dir = h.first;
        h.first += size;
        h.size -= size;
        if(h.size == 0) {
            if(prev == null) {
                holes = h.next;
            }
            else {
                prev.next = h.next;
            }
        }
        if(DEBUG) {
            System.out.println("alloc(" + size + ")=" + dir);
            showHoles();
            System.out.println("----");
        }
        System.out.println("memory manager: returning cells " + dir + " to " + (dir + size) + " (excluding)");
        return dir;
    }

    public void free(int dir, int size) {
        Hole h = holes;
        Hole prev = null;
        while(h != null && h.first < dir) {
            prev = h;
            h = h.next;
        }
        Hole newHole = new Hole(dir, size);
        newHole.next = h;

        if(prev == null) {
            holes = newHole;
            prev = holes;
        }
        else {
            prev.next = newHole;
            newHole.next = h;
        }
        if(prev != null && prev.first + prev.size == newHole.first) {
            prev.size += newHole.size;
            prev.next = h;
            if(h != null && prev.first + prev.size == h.first) {
                prev.size += h.size;
                prev.next = h.next;
            }
        }
        if(DEBUG) {
            System.out.println("free(" + dir + "," + size + ")");
            showHoles();
            System.out.println("----");
        }
    }

    public void showHoles() {
        Hole h = holes;
        while(h != null) {
            System.out.print("<" + h.first + "," + h.size + "," + (h.first + h.size - 1) + ">");
            h = h.next;
        }
        System.out.println();
    }
}
