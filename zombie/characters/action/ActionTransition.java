// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action;

import java.util.Iterator;
import zombie.debug.DebugType;
import zombie.characters.IsoZombie;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugOptions;
import zombie.network.GameClient;
import zombie.core.Core;
import zombie.util.StringUtils;
import zombie.util.Lambda;
import zombie.debug.DebugLog;
import zombie.util.PZXmlUtil;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public final class ActionTransition implements Cloneable
{
    String transitionTo;
    boolean asSubstate;
    boolean transitionOut;
    boolean forceParent;
    final List<IActionCondition> conditions;
    
    public ActionTransition() {
        this.conditions = new ArrayList<IActionCondition>();
    }
    
    public static boolean parse(final Element element, final String s, final List<ActionTransition> list) {
        if (element.getNodeName().equals("transitions")) {
            parseTransitions(element, s, list);
            return true;
        }
        if (element.getNodeName().equals("transition")) {
            parseTransition(element, list);
            return true;
        }
        return false;
    }
    
    public static void parseTransition(final Element element, final List<ActionTransition> list) {
        list.clear();
        final ActionTransition actionTransition = new ActionTransition();
        if (actionTransition.load(element)) {
            list.add(actionTransition);
        }
    }
    
    public static void parseTransitions(final Element element, final String s, final List<ActionTransition> list) {
        list.clear();
        ActionTransition actionTransition;
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, s, list, (element2, s2, list2) -> {
            if (!element2.getNodeName().equals("transition")) {
                DebugLog.ActionSystem.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName(), s2));
            }
            else {
                actionTransition = new ActionTransition();
                if (actionTransition.load(element2)) {
                    list2.add(actionTransition);
                }
            }
        });
    }
    
    private boolean load(final Element element) {
        try {
            final String anotherString;
            final IActionCondition actionCondition;
            PZXmlUtil.forEachElement(element, element2 -> {
                try {
                    element2.getNodeName();
                    if ("transitionTo".equalsIgnoreCase(anotherString)) {
                        this.transitionTo = element2.getTextContent();
                    }
                    else if ("transitionOut".equalsIgnoreCase(anotherString)) {
                        this.transitionOut = StringUtils.tryParseBoolean(element2.getTextContent());
                    }
                    else if ("forceParent".equalsIgnoreCase(anotherString)) {
                        this.forceParent = StringUtils.tryParseBoolean(element2.getTextContent());
                    }
                    else if ("asSubstate".equalsIgnoreCase(anotherString)) {
                        this.asSubstate = StringUtils.tryParseBoolean(element2.getTextContent());
                    }
                    else if ("conditions".equalsIgnoreCase(anotherString)) {
                        PZXmlUtil.forEachElement(element2, element3 -> {
                            IActionCondition.createInstance(element3);
                            if (actionCondition != null) {
                                this.conditions.add(actionCondition);
                            }
                        });
                    }
                }
                catch (Exception ex) {
                    DebugLog.ActionSystem.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName()));
                    DebugLog.ActionSystem.error(ex);
                }
                return;
            });
            return true;
        }
        catch (Exception ex2) {
            DebugLog.ActionSystem.error((Object)"Error while loading an ActionTransition element");
            DebugLog.ActionSystem.error(ex2);
            return false;
        }
    }
    
    public String getTransitionTo() {
        return this.transitionTo;
    }
    
    public boolean passes(final ActionContext actionContext, final int n) {
        for (int i = 0; i < this.conditions.size(); ++i) {
            if (!this.conditions.get(i).passes(actionContext, n)) {
                return false;
            }
        }
        if (Core.bDebug && GameClient.bClient && ((DebugOptions.instance.MultiplayerShowPlayerStatus.getValue() && actionContext.getOwner() instanceof IsoPlayer) || (DebugOptions.instance.MultiplayerShowZombieStatus.getValue() && actionContext.getOwner() instanceof IsoZombie))) {
            final StringBuilder append = new StringBuilder("Character ").append(actionContext.getOwner().getClass().getSimpleName()).append(" ").append("id=").append(actionContext.getOwner().getOnlineID()).append(" transition to \"").append(this.transitionTo).append("\":");
            final Iterator<IActionCondition> iterator = this.conditions.iterator();
            while (iterator.hasNext()) {
                append.append(" [").append(iterator.next().getDescription()).append("]");
            }
            DebugLog.log(DebugType.ActionSystem, append.toString());
        }
        return true;
    }
    
    public ActionTransition clone() {
        final ActionTransition actionTransition = new ActionTransition();
        actionTransition.transitionTo = this.transitionTo;
        actionTransition.asSubstate = this.asSubstate;
        actionTransition.transitionOut = this.transitionOut;
        actionTransition.forceParent = this.forceParent;
        final Iterator<IActionCondition> iterator = this.conditions.iterator();
        while (iterator.hasNext()) {
            actionTransition.conditions.add(iterator.next().clone());
        }
        return actionTransition;
    }
}
