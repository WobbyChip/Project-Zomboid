// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.util.StringUtils;
import zombie.scripting.ScriptParser;

public final class CustomEnumSandboxOption extends CustomSandboxOption
{
    public final int numValues;
    public final int defaultValue;
    public String m_valueTranslation;
    
    CustomEnumSandboxOption(final String s, final int numValues, final int defaultValue) {
        super(s);
        this.numValues = numValues;
        this.defaultValue = defaultValue;
    }
    
    static CustomEnumSandboxOption parse(final ScriptParser.Block block) {
        final int valueInt = CustomSandboxOption.getValueInt(block, "numValues", -1);
        final int valueInt2 = CustomSandboxOption.getValueInt(block, "default", -1);
        if (valueInt <= 0 || valueInt2 <= 0) {
            return null;
        }
        final CustomEnumSandboxOption customEnumSandboxOption = new CustomEnumSandboxOption(block.id, valueInt, valueInt2);
        if (!customEnumSandboxOption.parseCommon(block)) {
            return null;
        }
        final ScriptParser.Value value = block.getValue("valueTranslation");
        if (value != null) {
            customEnumSandboxOption.m_valueTranslation = StringUtils.discardNullOrWhitespace(value.getValue().trim());
        }
        return customEnumSandboxOption;
    }
}
