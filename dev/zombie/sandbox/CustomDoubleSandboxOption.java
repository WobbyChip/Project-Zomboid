// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomDoubleSandboxOption extends CustomSandboxOption
{
    public final double min;
    public final double max;
    public final double defaultValue;
    
    CustomDoubleSandboxOption(final String s, final double min, final double max, final double defaultValue) {
        super(s);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
    }
    
    static CustomDoubleSandboxOption parse(final ScriptParser.Block block) {
        final float valueFloat = CustomSandboxOption.getValueFloat(block, "min", Float.NaN);
        final float valueFloat2 = CustomSandboxOption.getValueFloat(block, "max", Float.NaN);
        final float valueFloat3 = CustomSandboxOption.getValueFloat(block, "default", Float.NaN);
        if (Float.isNaN(valueFloat) || Float.isNaN(valueFloat2) || Float.isNaN(valueFloat3)) {
            return null;
        }
        final CustomDoubleSandboxOption customDoubleSandboxOption = new CustomDoubleSandboxOption(block.id, valueFloat, valueFloat2, valueFloat3);
        if (!customDoubleSandboxOption.parseCommon(block)) {
            return null;
        }
        return customDoubleSandboxOption;
    }
}
