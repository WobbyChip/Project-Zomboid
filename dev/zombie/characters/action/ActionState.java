// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action;

import org.w3c.dom.Element;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.Collection;
import java.util.List;
import zombie.util.PZXmlUtil;
import java.io.File;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;
import java.util.Comparator;
import java.util.ArrayList;

public final class ActionState
{
    public final String name;
    public final ArrayList<ActionTransition> transitions;
    private String[] m_tags;
    private String[] m_childTags;
    private static final Comparator<ActionTransition> transitionComparator;
    
    public ActionState(final String name) {
        this.transitions = new ArrayList<ActionTransition>();
        this.name = name;
    }
    
    public final boolean canHaveSubStates() {
        return !PZArrayUtil.isNullOrEmpty(this.m_childTags);
    }
    
    public final boolean canBeSubstate() {
        return !PZArrayUtil.isNullOrEmpty(this.m_tags);
    }
    
    public final boolean canHaveSubState(final ActionState actionState) {
        return canHaveSubState(this, actionState);
    }
    
    public static boolean canHaveSubState(final ActionState actionState, final ActionState actionState2) {
        return tagsOverlap(actionState.m_childTags, actionState2.m_tags);
    }
    
    public static boolean tagsOverlap(final String[] array, final String[] array2) {
        if (PZArrayUtil.isNullOrEmpty(array)) {
            return false;
        }
        if (PZArrayUtil.isNullOrEmpty(array2)) {
            return false;
        }
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            for (int j = 0; j < array2.length; ++j) {
                if (StringUtils.equalsIgnoreCase(s, array2[j])) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void load(final String pathname) {
        final File[] listFiles = new File(pathname).getAbsoluteFile().listFiles((p0, s) -> s.toLowerCase().endsWith(".xml"));
        if (listFiles == null) {
            return;
        }
        final File[] array = listFiles;
        for (int length = array.length, i = 0; i < length; ++i) {
            this.parse(array[i]);
        }
        this.sortTransitions();
    }
    
    public void parse(final File file) {
        final ArrayList<ActionTransition> c = new ArrayList<ActionTransition>();
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> list2 = new ArrayList<String>();
        final String path = file.getPath();
        try {
            final Element xml = PZXmlUtil.parseXml(path);
            if (ActionTransition.parse(xml, path, c)) {
                this.transitions.addAll(c);
                if (DebugLog.isEnabled(DebugType.ActionSystem)) {
                    DebugLog.ActionSystem.debugln("Loaded transitions from file: %s", path);
                }
                return;
            }
            if (this.parseTags(xml, list, list2)) {
                this.m_tags = PZArrayUtil.concat(this.m_tags, (String[])list.toArray((E[])new String[0]));
                this.m_childTags = PZArrayUtil.concat(this.m_childTags, (String[])list2.toArray((E[])new String[0]));
                if (DebugLog.isEnabled(DebugType.ActionSystem)) {
                    DebugLog.ActionSystem.debugln("Loaded tags from file: %s", path);
                }
                return;
            }
            if (DebugLog.isEnabled(DebugType.ActionSystem)) {
                DebugLog.ActionSystem.warn("Unrecognized xml file. It does not appear to be a transition nor a tag(s). %s", path);
            }
        }
        catch (Exception ex) {
            DebugLog.ActionSystem.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path));
            DebugLog.ActionSystem.error(ex);
        }
    }
    
    private boolean parseTags(final Element element, final ArrayList<String> list, final ArrayList<String> list2) {
        list.clear();
        list2.clear();
        if (element.getNodeName().equals("tags")) {
            PZXmlUtil.forEachElement(element, element2 -> {
                if (element2.getNodeName().equals("tag")) {
                    list.add(element2.getTextContent());
                }
                return;
            });
            return true;
        }
        if (element.getNodeName().equals("childTags")) {
            PZXmlUtil.forEachElement(element, element3 -> {
                if (element3.getNodeName().equals("tag")) {
                    list2.add(element3.getTextContent());
                }
                return;
            });
            return true;
        }
        return false;
    }
    
    public void sortTransitions() {
        this.transitions.sort(ActionState.transitionComparator);
    }
    
    public void resetForReload() {
        this.transitions.clear();
        this.m_tags = null;
        this.m_childTags = null;
    }
    
    static {
        transitionComparator = ((actionTransition, actionTransition2) -> actionTransition2.conditions.size() - actionTransition.conditions.size());
    }
}
