// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;
import java.util.HashMap;

public class AnimationVariableHandlePool
{
    private static final Object s_threadLock;
    private static HashMap<String, AnimationVariableHandle> s_handlePool;
    private static int s_globalIndexGenerator;
    
    public static AnimationVariableHandle getOrCreate(final String s) {
        synchronized (AnimationVariableHandlePool.s_threadLock) {
            return getOrCreateInternal(s);
        }
    }
    
    private static AnimationVariableHandle getOrCreateInternal(final String key) {
        if (!isVariableNameValid(key)) {
            return null;
        }
        final AnimationVariableHandle animationVariableHandle = AnimationVariableHandlePool.s_handlePool.get(key);
        if (animationVariableHandle != null) {
            return animationVariableHandle;
        }
        final AnimationVariableHandle value = new AnimationVariableHandle();
        value.setVariableName(key);
        value.setVariableIndex(generateNewVariableIndex());
        AnimationVariableHandlePool.s_handlePool.put(key, value);
        return value;
    }
    
    private static boolean isVariableNameValid(final String s) {
        return !StringUtils.isNullOrWhitespace(s);
    }
    
    private static int generateNewVariableIndex() {
        return AnimationVariableHandlePool.s_globalIndexGenerator++;
    }
    
    static {
        s_threadLock = "AnimationVariableHandlePool.ThreadLock";
        AnimationVariableHandlePool.s_handlePool = new HashMap<String, AnimationVariableHandle>();
        AnimationVariableHandlePool.s_globalIndexGenerator = 0;
    }
}
