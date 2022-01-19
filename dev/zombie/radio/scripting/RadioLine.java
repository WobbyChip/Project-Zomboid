// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.scripting;

public final class RadioLine
{
    private float r;
    private float g;
    private float b;
    private String text;
    private String effects;
    private float airTime;
    
    public RadioLine(final String s, final float n, final float n2, final float n3) {
        this(s, n, n2, n3, null);
    }
    
    public RadioLine(final String s, final float r, final float g, final float b, final String s2) {
        this.r = 1.0f;
        this.g = 1.0f;
        this.b = 1.0f;
        this.text = "<!text missing!>";
        this.effects = "";
        this.airTime = -1.0f;
        this.text = ((s != null) ? s : this.text);
        this.r = r;
        this.g = g;
        this.b = b;
        this.effects = ((s2 != null) ? s2 : this.effects);
    }
    
    public float getR() {
        return this.r;
    }
    
    public float getG() {
        return this.g;
    }
    
    public float getB() {
        return this.b;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getEffectsString() {
        return this.effects;
    }
    
    public boolean isCustomAirTime() {
        return this.airTime > 0.0f;
    }
    
    public float getAirTime() {
        return this.airTime;
    }
    
    public void setAirTime(final float airTime) {
        this.airTime = airTime;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
}
