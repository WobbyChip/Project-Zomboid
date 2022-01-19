// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action;

import org.w3c.dom.Element;
import java.util.HashMap;

public interface IActionCondition
{
    public static final HashMap<String, IFactory> s_factoryMap = new HashMap<String, IFactory>();
    
    String getDescription();
    
    boolean passes(final ActionContext p0, final int p1);
    
    IActionCondition clone();
    
    default IActionCondition createInstance(final Element element) {
        final IFactory factory = IActionCondition.s_factoryMap.get(element.getNodeName());
        if (factory != null) {
            return factory.create(element);
        }
        return null;
    }
    
    default void registerFactory(final String key, final IFactory value) {
        IActionCondition.s_factoryMap.put(key, value);
    }
    
    public interface IFactory
    {
        IActionCondition create(final Element p0);
    }
}
