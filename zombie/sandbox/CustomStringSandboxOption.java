// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomStringSandboxOption extends CustomSandboxOption
{
    public final String defaultValue;
    
    CustomStringSandboxOption(final String s, final String defaultValue) {
        super(s);
        this.defaultValue = defaultValue;
    }
    
    static CustomStringSandboxOption parse(final ScriptParser.Block block) {
        final ScriptParser.Value value = block.getValue("default");
        if (value == null) {
            return null;
        }
        final CustomStringSandboxOption customStringSandboxOption = new CustomStringSandboxOption(block.id, value.getValue().trim());
        if (!customStringSandboxOption.parseCommon(block)) {
            return null;
        }
        return customStringSandboxOption;
    }
}
