// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

public class EnumConfigOption extends IntegerConfigOption
{
    public EnumConfigOption(final String s, final int n, final int n2) {
        super(s, 1, n, n2);
    }
    
    @Override
    public String getType() {
        return "enum";
    }
    
    public int getNumValues() {
        return this.max;
    }
}
