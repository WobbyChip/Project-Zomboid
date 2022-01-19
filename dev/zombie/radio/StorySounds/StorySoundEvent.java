// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.StorySounds;

import java.util.ArrayList;

public final class StorySoundEvent
{
    protected String name;
    protected ArrayList<EventSound> eventSounds;
    
    public StorySoundEvent() {
        this("Unnamed");
    }
    
    public StorySoundEvent(final String name) {
        this.eventSounds = new ArrayList<EventSound>();
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ArrayList<EventSound> getEventSounds() {
        return this.eventSounds;
    }
    
    public void setEventSounds(final ArrayList<EventSound> eventSounds) {
        this.eventSounds = eventSounds;
    }
}
