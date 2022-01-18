// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

import zombie.debug.DebugLog;

public class IntegerConfigOption extends ConfigOption
{
    protected int value;
    protected int defaultValue;
    protected int min;
    protected int max;
    
    public IntegerConfigOption(final String s, final int min, final int max, final int n) {
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
        return "integer";
    }
    
    @Override
    public void resetToDefault() {
        this.setValue(this.defaultValue);
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    @Override
    public void setDefaultToCurrentValue() {
        this.defaultValue = this.value;
    }
    
    @Override
    public void parse(final String s) {
        try {
            this.setValue((int)Double.parseDouble(s));
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
            this.setValue(((Double)o).intValue());
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
            final int int1 = Integer.parseInt(s);
            return int1 >= this.min && int1 <= this.max;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public void setValue(final int value) {
        if (value < this.min) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.name, value, this.min));
            return;
        }
        if (value > this.max) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.name, value, this.max));
            return;
        }
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getDefaultValue() {
        return this.defaultValue;
    }
}
