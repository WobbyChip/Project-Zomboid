// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action.conditions;

import org.w3c.dom.Element;
import zombie.characters.action.ActionContext;
import zombie.characters.action.IActionCondition;

public final class LuaCall implements IActionCondition
{
    @Override
    public String getDescription() {
        return "<luaCheck>";
    }
    
    @Override
    public boolean passes(final ActionContext actionContext, final int n) {
        return false;
    }
    
    @Override
    public IActionCondition clone() {
        return new LuaCall();
    }
    
    public static class Factory implements IFactory
    {
        @Override
        public IActionCondition create(final Element element) {
            return new LuaCall();
        }
    }
}
