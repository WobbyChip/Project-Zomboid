// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

import zombie.debug.DebugLog;

public class DoubleConfigOption extends ConfigOption
{
    protected double value;
    protected double defaultValue;
    protected double min;
    protected double max;
    
    public DoubleConfigOption(final String s, final double min, final double max, final double n) {
        super(s);
        if (n < min || n > max) {
            throw new IllegalArgumentException();
        }
        this.value = n;
        this.defaultValue = n;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public String getType() {
        return "double";
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
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
        try {
            this.setValue(Double.parseDouble(s));
        }
        catch (NumberFormatException ex) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.name, s));
        }
    }
    
    @Override
    public String getValueAsString() {
        return String.valueOf(this.value);
    }
    
    @Override
    public void setValueFromObject(final Object o) {
        if (o instanceof Double) {
            this.setValue((double)o);
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
        try {
            final double double1 = Double.parseDouble(s);
            return double1 >= this.min && double1 <= this.max;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public void setValue(final double value) {
        if (value < this.min) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;DD)Ljava/lang/String;, this.name, value, this.min));
            return;
        }
        if (value > this.max) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;DD)Ljava/lang/String;, this.name, value, this.max));
            return;
        }
        this.value = value;
    }
    
    public double getValue() {
        return this.value;
    }
    
    public double getDefaultValue() {
        return this.defaultValue;
    }
}
