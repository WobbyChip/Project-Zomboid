// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

public class StringConfigOption extends ConfigOption
{
    protected String value;
    protected String defaultValue;
    
    public StringConfigOption(final String s, String s2) {
        super(s);
        if (s2 == null) {
            s2 = "";
        }
        this.value = s2;
        this.defaultValue = s2;
    }
    
    @Override
    public String getType() {
        return "string";
    }
    
    @Override
    public void resetToDefault() {
        this.value = this.defaultValue;
    }
    
    @Override
    public void setDefaultToCurrentValue() {
        this.defaultValue = this.value;
    }
    
    @Override
    public void parse(final String valueFromObject) {
        this.setValueFromObject(valueFromObject);
    }
    
    @Override
    public String getValueAsString() {
        return this.value;
    }
    
    @Override
    public String getValueAsLuaString() {
        return String.format("\"%s\"", this.value.replace("\\", "\\\\").replace("\"", "\\\""));
    }
    
    @Override
    public void setValueFromObject(final Object o) {
        if (o == null) {
            this.value = "";
        }
        else if (o instanceof String) {
            this.value = (String)o;
        }
        else {
            this.value = o.toString();
        }
    }
    
    @Override
    public Object getValueAsObject() {
        return this.value;
    }
    
    @Override
    public boolean isValidString(final String s) {
        return true;
    }
    
    public void setValue(String value) {
        if (value == null) {
            value = "";
        }
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
}
