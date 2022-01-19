// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomBooleanSandboxOption extends CustomSandboxOption
{
    public final boolean defaultValue;
    
    CustomBooleanSandboxOption(final String s, final boolean defaultValue) {
        super(s);
        this.defaultValue = defaultValue;
    }
    
    static CustomBooleanSandboxOption parse(final ScriptParser.Block block) {
        final ScriptParser.Value value = block.getValue("default");
        if (value == null) {
            return null;
        }
        final CustomBooleanSandboxOption customBooleanSandboxOption = new CustomBooleanSandboxOption(block.id, Boolean.parseBoolean(value.getValue().trim()));
        if (!customBooleanSandboxOption.parseCommon(block)) {
            return null;
        }
        return customBooleanSandboxOption;
    }
}
