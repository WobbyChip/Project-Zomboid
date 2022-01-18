// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

public abstract class ConfigOption
{
    protected final String name;
    
    public ConfigOption(final String name) {
        if (name == null || name.isEmpty() || name.contains("=")) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public abstract String getType();
    
    public abstract void resetToDefault();
    
    public abstract void setDefaultToCurrentValue();
    
    public abstract void parse(final String p0);
    
    public abstract String getValueAsString();
    
    public String getValueAsLuaString() {
        return this.getValueAsString();
    }
    
    public abstract void setValueFromObject(final Object p0);
    
    public abstract Object getValueAsObject();
    
    public abstract boolean isValidString(final String p0);
}
