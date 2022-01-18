// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.script;

public final class RadioScriptEntry
{
    private int chanceMin;
    private int chanceMax;
    private String scriptName;
    private int Delay;
    
    public RadioScriptEntry(final String s, final int n) {
        this(s, n, 0, 100);
    }
    
    public RadioScriptEntry(final String scriptName, final int delay, final int chanceMin, final int chanceMax) {
        this.chanceMin = 0;
        this.chanceMax = 100;
        this.scriptName = "";
        this.Delay = 0;
        this.scriptName = scriptName;
        this.setChanceMin(chanceMin);
        this.setChanceMax(chanceMax);
        this.setDelay(delay);
    }
    
    public void setChanceMin(final int n) {
        this.chanceMin = ((n < 0) ? 0 : ((n > 100) ? 100 : n));
    }
    
    public int getChanceMin() {
        return this.chanceMin;
    }
    
    public void setChanceMax(final int n) {
        this.chanceMax = ((n < 0) ? 0 : ((n > 100) ? 100 : n));
    }
    
    public int getChanceMax() {
        return this.chanceMax;
    }
    
    public String getScriptName() {
        return this.scriptName;
    }
    
    public void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }
    
    public int getDelay() {
        return this.Delay;
    }
    
    public void setDelay(final int n) {
        this.Delay = ((n >= 0) ? n : 0);
    }
}
