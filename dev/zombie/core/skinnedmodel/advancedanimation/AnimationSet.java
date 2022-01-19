// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugType;
import zombie.debug.DebugLog;
import java.util.Locale;
import java.util.Iterator;
import java.util.HashMap;

public final class AnimationSet
{
    protected static final HashMap<String, AnimationSet> setMap;
    public final HashMap<String, AnimState> states;
    public String m_Name;
    
    public AnimationSet() {
        this.states = new HashMap<String, AnimState>();
        this.m_Name = "";
    }
    
    public static AnimationSet GetAnimationSet(final String s, final boolean b) {
        final AnimationSet set = AnimationSet.setMap.get(s);
        if (set != null && !b) {
            return set;
        }
        final AnimationSet value = new AnimationSet();
        value.Load(s);
        AnimationSet.setMap.put(s, value);
        return value;
    }
    
    public static void Reset() {
        final Iterator<AnimationSet> iterator = AnimationSet.setMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().clear();
        }
        AnimationSet.setMap.clear();
    }
    
    public AnimState GetState(final String s) {
        final AnimState animState = this.states.get(s.toLowerCase(Locale.ENGLISH));
        if (animState != null) {
            return animState;
        }
        DebugLog.Animation.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        return new AnimState();
    }
    
    public boolean containsState(final String s) {
        return this.states.containsKey(s.toLowerCase(Locale.ENGLISH));
    }
    
    public boolean Load(final String name) {
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        }
        this.m_Name = name;
        for (final String pathname : ZomboidFileSystem.instance.resolveAllDirectories(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), p0 -> true, false)) {
            final String name2 = new File(pathname).getName();
            final AnimState parse = AnimState.Parse(name2, pathname);
            parse.m_Set = this;
            this.states.put(name2, parse);
        }
        return true;
    }
    
    private void clear() {
        final Iterator<AnimState> iterator = this.states.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().clear();
        }
        this.states.clear();
    }
    
    static {
        setMap = new HashMap<String, AnimationSet>();
    }
}
