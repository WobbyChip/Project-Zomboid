// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.runtime;

import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import java.util.List;
import java.util.Iterator;
import zombie.scripting.ScriptParser;
import java.util.ArrayList;
import zombie.scripting.objects.BaseScriptObject;

public final class RuntimeAnimationScript extends BaseScriptObject
{
    protected String m_name;
    protected final ArrayList<IRuntimeAnimationCommand> m_commands;
    
    public RuntimeAnimationScript() {
        this.m_name = this.toString();
        this.m_commands = new ArrayList<IRuntimeAnimationCommand>();
    }
    
    public void Load(final String name, final String s) {
        this.m_name = name;
        final ScriptParser.Block block = ScriptParser.parse(s).children.get(0);
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            value.getValue().trim();
            if ("xxx".equals(trim)) {}
        }
        for (final ScriptParser.Block block2 : block.children) {
            if ("CopyFrame".equals(block2.type)) {
                final CopyFrame e = new CopyFrame();
                e.parse(block2);
                this.m_commands.add(e);
            }
            else {
                if (!"CopyFrames".equals(block2.type)) {
                    continue;
                }
                final CopyFrames e2 = new CopyFrames();
                e2.parse(block2);
                this.m_commands.add(e2);
            }
        }
    }
    
    public void exec() {
        final ArrayList list = new ArrayList<Object>();
        final Iterator<IRuntimeAnimationCommand> iterator = this.m_commands.iterator();
        while (iterator.hasNext()) {
            iterator.next().exec(list);
        }
        float max = 0.0f;
        for (int i = 0; i < list.size(); ++i) {
            max = Math.max(max, ((Keyframe)list.get(i)).Time);
        }
        final AnimationClip animationClip = new AnimationClip(max, list, this.m_name, true);
        list.clear();
        ModelManager.instance.addAnimationClip(animationClip.Name, animationClip);
        list.clear();
    }
    
    public void reset() {
        this.m_name = this.toString();
        this.m_commands.clear();
    }
}
