// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.runtime;

import zombie.core.skinnedmodel.animation.Keyframe;
import java.util.List;
import zombie.scripting.ScriptParser;

public interface IRuntimeAnimationCommand
{
    void parse(final ScriptParser.Block p0);
    
    void exec(final List<Keyframe> p0);
}
