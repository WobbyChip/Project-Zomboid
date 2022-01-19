// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action.conditions;

import zombie.characters.action.ActionContext;
import org.w3c.dom.Element;
import zombie.characters.action.IActionCondition;

public final class EventOccurred implements IActionCondition
{
    public String eventName;
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.eventName);
    }
    
    private boolean load(final Element element) {
        this.eventName = element.getTextContent().toLowerCase();
        return true;
    }
    
    @Override
    public boolean passes(final ActionContext actionContext, final int n) {
        return actionContext.hasEventOccurred(this.eventName, n);
    }
    
    @Override
    public IActionCondition clone() {
        return null;
    }
    
    public static class Factory implements IFactory
    {
        @Override
        public IActionCondition create(final Element element) {
            final EventOccurred eventOccurred = new EventOccurred();
            if (eventOccurred.load(element)) {
                return eventOccurred;
            }
            return null;
        }
    }
}
