// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import java.util.Iterator;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;

public class AnimationVariableSource implements IAnimationVariableMap
{
    private final Map<String, IAnimationVariableSlot> m_GameVariables;
    private IAnimationVariableSlot[] m_cachedGameVariableSlots;
    
    public AnimationVariableSource() {
        this.m_GameVariables = new TreeMap<String, IAnimationVariableSlot>(String.CASE_INSENSITIVE_ORDER);
        this.m_cachedGameVariableSlots = new IAnimationVariableSlot[0];
    }
    
    @Override
    public IAnimationVariableSlot getVariable(final AnimationVariableHandle animationVariableHandle) {
        if (animationVariableHandle == null) {
            return null;
        }
        final int variableIndex = animationVariableHandle.getVariableIndex();
        if (variableIndex < 0) {
            return null;
        }
        if (this.m_cachedGameVariableSlots != null && variableIndex < this.m_cachedGameVariableSlots.length) {
            IAnimationVariableSlot animationVariableSlot = this.m_cachedGameVariableSlots[variableIndex];
            if (animationVariableSlot == null) {
                this.m_cachedGameVariableSlots[variableIndex] = this.m_GameVariables.get(animationVariableHandle.getVariableName());
                animationVariableSlot = this.m_cachedGameVariableSlots[variableIndex];
            }
            return animationVariableSlot;
        }
        final IAnimationVariableSlot animationVariableSlot2 = this.m_GameVariables.get(animationVariableHandle.getVariableName());
        if (animationVariableSlot2 == null) {
            return null;
        }
        final IAnimationVariableSlot[] cachedGameVariableSlots = new IAnimationVariableSlot[variableIndex + 1];
        final IAnimationVariableSlot[] cachedGameVariableSlots2 = this.m_cachedGameVariableSlots;
        if (cachedGameVariableSlots2 != null) {
            this.m_cachedGameVariableSlots = PZArrayUtil.arrayCopy(cachedGameVariableSlots2, cachedGameVariableSlots, 0, cachedGameVariableSlots2.length);
        }
        cachedGameVariableSlots[variableIndex] = animationVariableSlot2;
        this.m_cachedGameVariableSlots = cachedGameVariableSlots;
        return animationVariableSlot2;
    }
    
    @Override
    public IAnimationVariableSlot getVariable(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        return this.m_GameVariables.get(s.trim());
    }
    
    @Override
    public IAnimationVariableSlot getOrCreateVariable(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        final String trim = s.trim();
        IAnimationVariableSlot variable = this.m_GameVariables.get(trim);
        if (variable == null) {
            variable = new AnimationVariableGenericSlot(trim.toLowerCase());
            this.setVariable(variable);
        }
        return variable;
    }
    
    @Override
    public void setVariable(final IAnimationVariableSlot animationVariableSlot) {
        this.m_GameVariables.put(animationVariableSlot.getKey(), animationVariableSlot);
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackBool(s, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackBool(s, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackString(s, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackString(s, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(s, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(s, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackInt(s, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackInt(s, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final boolean b, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackBool(s, b, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final boolean b, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackBool(s, b, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final String s2, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackString(s, s2, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final String s2, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackString(s, s2, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final float n, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(s, n, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final float n, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(s, n, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    public void setVariable(final String s, final int n, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackInt(s, n, callbackGetStrongTyped));
    }
    
    public void setVariable(final String s, final int n, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.setVariable(new AnimationVariableSlotCallbackInt(s, n, callbackGetStrongTyped, callbackSetStrongTyped));
    }
    
    @Override
    public void setVariable(final String s, final String value) {
        this.getOrCreateVariable(s).setValue(value);
    }
    
    @Override
    public void setVariable(final String s, final boolean value) {
        this.getOrCreateVariable(s).setValue(value);
    }
    
    @Override
    public void setVariable(final String s, final float value) {
        this.getOrCreateVariable(s).setValue(value);
    }
    
    @Override
    public void clearVariable(final String s) {
        final IAnimationVariableSlot variable = this.getVariable(s);
        if (variable != null) {
            variable.clear();
        }
    }
    
    @Override
    public void clearVariables() {
        final Iterator<IAnimationVariableSlot> iterator = this.getGameVariables().iterator();
        while (iterator.hasNext()) {
            iterator.next().clear();
        }
    }
    
    @Override
    public String getVariableString(final String s) {
        final IAnimationVariableSlot variable = this.getVariable(s);
        return (variable != null) ? variable.getValueString() : "";
    }
    
    @Override
    public float getVariableFloat(final String s, final float n) {
        final IAnimationVariableSlot variable = this.getVariable(s);
        return (variable != null) ? variable.getValueFloat() : n;
    }
    
    @Override
    public boolean getVariableBoolean(final String s) {
        final IAnimationVariableSlot variable = this.getVariable(s);
        return variable != null && variable.getValueBool();
    }
    
    @Override
    public Iterable<IAnimationVariableSlot> getGameVariables() {
        return this.m_GameVariables.values();
    }
    
    @Override
    public boolean isVariable(final String s, final String s2) {
        return StringUtils.equalsIgnoreCase(this.getVariableString(s), s2);
    }
    
    @Override
    public boolean containsVariable(final String s) {
        return !StringUtils.isNullOrWhitespace(s) && this.m_GameVariables.containsKey(s.trim());
    }
}
