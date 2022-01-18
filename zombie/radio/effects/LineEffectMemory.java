// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.effects;

import zombie.characters.IsoPlayer;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public final class LineEffectMemory
{
    private final Map<Integer, ArrayList<String>> memory;
    
    public LineEffectMemory() {
        this.memory = new HashMap<Integer, ArrayList<String>>();
    }
    
    public void addLine(final IsoPlayer isoPlayer, final String s) {
        final int id = isoPlayer.getDescriptor().getID();
        ArrayList<String> list;
        if (!this.memory.containsKey(id)) {
            list = new ArrayList<String>();
            this.memory.put(id, list);
        }
        else {
            list = this.memory.get(id);
        }
        if (!list.contains(s)) {
            list.add(s);
        }
    }
    
    public boolean contains(final IsoPlayer isoPlayer, final String o) {
        final int id = isoPlayer.getDescriptor().getID();
        return this.memory.containsKey(id) && this.memory.get(id).contains(o);
    }
}
