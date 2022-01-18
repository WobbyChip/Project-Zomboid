// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomIntegerSandboxOption extends CustomSandboxOption
{
    public final int min;
    public final int max;
    public final int defaultValue;
    
    CustomIntegerSandboxOption(final String s, final int min, final int max, final int defaultValue) {
        super(s);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
    }
    
    static CustomIntegerSandboxOption parse(final ScriptParser.Block block) {
        final int valueInt = CustomSandboxOption.getValueInt(block, "min", Integer.MIN_VALUE);
        final int valueInt2 = CustomSandboxOption.getValueInt(block, "max", Integer.MIN_VALUE);
        final int valueInt3 = CustomSandboxOption.getValueInt(block, "default", Integer.MIN_VALUE);
        if (valueInt == Integer.MIN_VALUE || valueInt2 == Integer.MIN_VALUE || valueInt3 == Integer.MIN_VALUE) {
            return null;
        }
        final CustomIntegerSandboxOption customIntegerSandboxOption = new CustomIntegerSandboxOption(block.id, valueInt, valueInt2, valueInt3);
        if (!customIntegerSandboxOption.parseCommon(block)) {
            return null;
        }
        return customIntegerSandboxOption;
    }
}
