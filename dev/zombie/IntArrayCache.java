// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.Stack;
import gnu.trove.map.hash.TIntObjectHashMap;

public class IntArrayCache
{
    public static IntArrayCache instance;
    TIntObjectHashMap<Stack<Integer[]>> Map;
    
    public IntArrayCache() {
        this.Map = (TIntObjectHashMap<Stack<Integer[]>>)new TIntObjectHashMap();
    }
    
    public void Init() {
        for (int i = 0; i < 100; ++i) {
            final Stack<Integer[]> stack = new Stack<Integer[]>();
            for (int j = 0; j < 1000; ++j) {
                stack.push(new Integer[i]);
            }
        }
    }
    
    public void put(final Integer[] array) {
        if (this.Map.containsKey(array.length)) {
            ((Stack)this.Map.get(array.length)).push(array);
        }
        else {
            final Stack<Integer[]> stack = new Stack<Integer[]>();
            stack.push(array);
            this.Map.put(array.length, (Object)stack);
        }
    }
    
    public Integer[] get(final int n) {
        if (this.Map.containsKey(n)) {
            final Stack stack = (Stack)this.Map.get(n);
            if (!stack.isEmpty()) {
                return stack.pop();
            }
        }
        return new Integer[n];
    }
    
    static {
        IntArrayCache.instance = new IntArrayCache();
    }
}
