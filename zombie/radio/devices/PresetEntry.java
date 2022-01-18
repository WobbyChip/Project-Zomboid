// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.devices;

public final class PresetEntry
{
    public String name;
    public int frequency;
    
    public PresetEntry() {
        this.name = "New preset";
        this.frequency = 93200;
    }
    
    public PresetEntry(final String name, final int frequency) {
        this.name = "New preset";
        this.frequency = 93200;
        this.name = name;
        this.frequency = frequency;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getFrequency() {
        return this.frequency;
    }
    
    public void setFrequency(final int frequency) {
        this.frequency = frequency;
    }
}
