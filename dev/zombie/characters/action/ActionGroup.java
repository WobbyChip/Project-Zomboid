// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action;

import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugType;
import java.util.Iterator;
import java.io.PrintStream;
import zombie.debug.DebugLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ActionGroup
{
    private static final Map<String, ActionGroup> actionGroupMap;
    String initialState;
    private List<ActionState> states;
    private Map<String, ActionState> stateLookup;
    
    public ActionGroup() {
        this.states = new ArrayList<ActionState>();
    }
    
    public static ActionGroup getActionGroup(String lowerCase) {
        lowerCase = lowerCase.toLowerCase();
        final ActionGroup actionGroup = ActionGroup.actionGroupMap.get(lowerCase);
        if (actionGroup != null || ActionGroup.actionGroupMap.containsKey(lowerCase)) {
            return actionGroup;
        }
        final ActionGroup actionGroup2 = new ActionGroup();
        ActionGroup.actionGroupMap.put(lowerCase, actionGroup2);
        try {
            actionGroup2.load(lowerCase);
        }
        catch (Exception ex) {
            DebugLog.ActionSystem.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lowerCase));
            ex.printStackTrace(DebugLog.ActionSystem);
        }
        return actionGroup2;
    }
    
    public static void reloadAll() {
        for (final Map.Entry<String, ActionGroup> entry : ActionGroup.actionGroupMap.entrySet()) {
            final ActionGroup actionGroup = entry.getValue();
            final Iterator<ActionState> iterator2 = actionGroup.states.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().resetForReload();
            }
            actionGroup.load(entry.getKey());
        }
    }
    
    void load(final String s) {
        if (DebugLog.isEnabled(DebugType.ActionSystem)) {
            DebugLog.ActionSystem.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (mediaFile.exists() && mediaFile.canRead()) {
            this.loadGroupData(mediaFile);
        }
        final File[] listFiles = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)).listFiles();
        if (listFiles != null) {
            for (final File file : listFiles) {
                if (file.isDirectory()) {
                    this.getOrCreate(file.getName()).load(file.getPath());
                }
            }
        }
    }
    
    private void loadGroupData(final File f) {
        Document parse;
        try {
            parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        }
        catch (ParserConfigurationException | SAXException | IOException ex) {
            final Object o2;
            final Object o = o2;
            DebugLog.ActionSystem.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, f.getPath()));
            ((Throwable)o).printStackTrace(DebugLog.ActionSystem);
            return;
        }
        parse.getDocumentElement().normalize();
        final Element documentElement = parse.getDocumentElement();
        if (!documentElement.getNodeName().equals("actiongroup")) {
            DebugLog.ActionSystem.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, f.getPath(), documentElement.getNodeName()));
            return;
        }
        for (Node node = documentElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeName().equals("inherit")) {
                if (node instanceof Element) {
                    this.inherit(getActionGroup(node.getTextContent().trim()));
                }
            }
        }
        for (Node node2 = documentElement.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2 instanceof Element) {
                final Element element = (Element)node2;
                final String nodeName = element.getNodeName();
                switch (nodeName) {
                    case "initial": {
                        this.initialState = element.getTextContent().trim();
                        break;
                    }
                    case "inherit": {
                        break;
                    }
                    default: {
                        DebugLog.ActionSystem.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, f.getPath()));
                        break;
                    }
                }
            }
        }
    }
    
    private void inherit(final ActionGroup actionGroup) {
        if (actionGroup == null) {
            return;
        }
        if (actionGroup.initialState != null) {
            this.initialState = actionGroup.initialState;
        }
        for (final ActionState actionState : actionGroup.states) {
            final ActionState orCreate = this.getOrCreate(actionState.name);
            final Iterator<ActionTransition> iterator2 = actionState.transitions.iterator();
            while (iterator2.hasNext()) {
                orCreate.transitions.add(iterator2.next().clone());
                orCreate.sortTransitions();
            }
        }
    }
    
    private void rebuildLookup() {
        final HashMap<String, ActionState> stateLookup = new HashMap<String, ActionState>();
        for (final ActionState actionState : this.states) {
            stateLookup.put(actionState.name.toLowerCase(), actionState);
        }
        this.stateLookup = stateLookup;
    }
    
    public void addState(final ActionState actionState) {
        this.states.add(actionState);
        this.stateLookup = null;
    }
    
    public ActionState get(final String s) {
        if (this.stateLookup == null) {
            this.rebuildLookup();
        }
        return this.stateLookup.get(s.toLowerCase());
    }
    
    ActionState getOrCreate(String lowerCase) {
        if (this.stateLookup == null) {
            this.rebuildLookup();
        }
        lowerCase = lowerCase.toLowerCase();
        ActionState actionState = this.stateLookup.get(lowerCase);
        if (actionState == null) {
            actionState = new ActionState(lowerCase);
            this.states.add(actionState);
            this.stateLookup.put(lowerCase, actionState);
        }
        return actionState;
    }
    
    public ActionState getInitialState() {
        ActionState value = null;
        if (this.initialState != null) {
            value = this.get(this.initialState);
        }
        if (value == null && this.states.size() > 0) {
            value = this.states.get(0);
        }
        return value;
    }
    
    public ActionState getDefaultState() {
        return this.getInitialState();
    }
    
    static {
        actionGroupMap = new HashMap<String, ActionGroup>();
    }
}
