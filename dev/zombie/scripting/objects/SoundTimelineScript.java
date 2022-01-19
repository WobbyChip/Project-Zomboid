// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.Iterator;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import java.util.HashMap;

public final class SoundTimelineScript extends BaseScriptObject
{
    private String eventName;
    private HashMap<String, Integer> positionByName;
    
    public SoundTimelineScript() {
        this.positionByName = new HashMap<String, Integer>();
    }
    
    public void Load(final String eventName, final String s) {
        this.eventName = eventName;
        for (final ScriptParser.Value value : ScriptParser.parse(s).children.get(0).values) {
            this.positionByName.put(value.getKey().trim(), PZMath.tryParseInt(value.getValue().trim(), 0));
        }
    }
    
    public String getEventName() {
        return this.eventName;
    }
    
    public int getPosition(final String s) {
        if (this.positionByName.containsKey(s)) {
            return this.positionByName.get(s);
        }
        return -1;
    }
    
    public void reset() {
        this.positionByName.clear();
    }
}
