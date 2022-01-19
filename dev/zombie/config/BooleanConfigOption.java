// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

import zombie.debug.DebugLog;

public class BooleanConfigOption extends ConfigOption
{
    protected boolean value;
    protected boolean defaultValue;
    
    public BooleanConfigOption(final String s, final boolean b) {
        super(s);
        this.value = b;
        this.defaultValue = b;
    }
    
    @Override
    public String getType() {
        return "boolean";
    }
    
    @Override
    public void resetToDefault() {
        this.setValue(this.defaultValue);
    }
    
    @Override
    public void setDefaultToCurrentValue() {
        this.defaultValue = this.value;
    }
    
    @Override
    public void parse(final String s) {
        if (this.isValidString(s)) {
            this.setValue(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1"));
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.name, s));
        }
    }
    
    @Override
    public String getValueAsString() {
        return String.valueOf(this.value);
    }
    
    @Override
    public void setValueFromObject(final Object o) {
        if (o instanceof Boolean) {
            this.setValue((boolean)o);
        }
        else if (o instanceof Double) {
            this.setValue((double)o != 0.0);
        }
        else if (o instanceof String) {
            this.parse((String)o);
        }
    }
    
    @Override
    public Object getValueAsObject() {
        return this.value;
    }
    
    @Override
    public boolean isValidString(final String s) {
        return s != null && (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("0"));
    }
    
    public boolean getValue() {
        return this.value;
    }
    
    public void setValue(final boolean value) {
        this.value = value;
    }
    
    public boolean getDefaultValue() {
        return this.defaultValue;
    }
}
