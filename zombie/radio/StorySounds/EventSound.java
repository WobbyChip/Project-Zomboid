// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.StorySounds;

import java.util.ArrayList;
import zombie.core.Color;

public final class EventSound
{
    protected String name;
    protected Color color;
    protected ArrayList<DataPoint> dataPoints;
    protected ArrayList<StorySound> storySounds;
    
    public EventSound() {
        this("Unnamed");
    }
    
    public EventSound(final String name) {
        this.color = new Color(1.0f, 1.0f, 1.0f);
        this.dataPoints = new ArrayList<DataPoint>();
        this.storySounds = new ArrayList<StorySound>();
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public ArrayList<DataPoint> getDataPoints() {
        return this.dataPoints;
    }
    
    public void setDataPoints(final ArrayList<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
    
    public ArrayList<StorySound> getStorySounds() {
        return this.storySounds;
    }
    
    public void setStorySounds(final ArrayList<StorySound> storySounds) {
        this.storySounds = storySounds;
    }
}
