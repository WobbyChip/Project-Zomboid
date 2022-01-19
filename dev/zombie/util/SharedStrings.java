// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.HashMap;

public final class SharedStrings
{
    private final HashMap<String, String> strings;
    
    public SharedStrings() {
        this.strings = new HashMap<String, String>();
    }
    
    public String get(final String s) {
        String s2 = this.strings.get(s);
        if (s2 == null) {
            this.strings.put(s, s);
            s2 = s;
        }
        return s2;
    }
    
    public void clear() {
        this.strings.clear();
    }
}
