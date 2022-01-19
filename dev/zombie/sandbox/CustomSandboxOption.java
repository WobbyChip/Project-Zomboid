// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;

public class CustomSandboxOption
{
    public final String m_id;
    public String m_page;
    public String m_translation;
    
    CustomSandboxOption(final String id) {
        this.m_id = id;
    }
    
    static float getValueFloat(final ScriptParser.Block block, final String s, final float n) {
        final ScriptParser.Value value = block.getValue(s);
        if (value == null) {
            return n;
        }
        return PZMath.tryParseFloat(value.getValue().trim(), n);
    }
    
    static int getValueInt(final ScriptParser.Block block, final String s, final int n) {
        final ScriptParser.Value value = block.getValue(s);
        if (value == null) {
            return n;
        }
        return PZMath.tryParseInt(value.getValue().trim(), n);
    }
    
    boolean parseCommon(final ScriptParser.Block block) {
        final ScriptParser.Value value = block.getValue("page");
        if (value != null) {
            this.m_page = StringUtils.discardNullOrWhitespace(value.getValue().trim());
        }
        final ScriptParser.Value value2 = block.getValue("translation");
        if (value2 != null) {
            this.m_translation = StringUtils.discardNullOrWhitespace(value2.getValue().trim());
        }
        return true;
    }
}
