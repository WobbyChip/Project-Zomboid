// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation.debug;

import zombie.core.Colors;
import java.util.List;
import java.util.Collections;
import java.util.ListIterator;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import java.util.Map;
import java.util.Iterator;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;

public final class AnimatorDebugMonitor
{
    private static final ArrayList<String> knownVariables;
    private static boolean knownVarsDirty;
    private String currentState;
    private MonitoredLayer[] monitoredLayers;
    private final HashMap<String, MonitoredVar> monitoredVariables;
    private final ArrayList<String> customVariables;
    private final LinkedList<MonitorLogLine> logLines;
    private final Queue<MonitorLogLine> logLineQueue;
    private boolean floatsListDirty;
    private boolean hasFilterChanges;
    private boolean hasLogUpdates;
    private String logString;
    private static final int maxLogSize = 1028;
    private static final int maxOutputLines = 128;
    private static final int maxFloatCache = 1024;
    private final ArrayList<Float> floatsOut;
    private MonitoredVar selectedVariable;
    private int tickCount;
    private boolean doTickStamps;
    private static final int tickStampLength = 10;
    private static final Color col_curstate;
    private static final Color col_layer_nodename;
    private static final Color col_layer_activated;
    private static final Color col_layer_deactivated;
    private static final Color col_track_activated;
    private static final Color col_track_deactivated;
    private static final Color col_node_activated;
    private static final Color col_node_deactivated;
    private static final Color col_var_activated;
    private static final Color col_var_changed;
    private static final Color col_var_deactivated;
    private static final String TAG_VAR = "[variable]";
    private static final String TAG_LAYER = "[layer]";
    private static final String TAG_NODE = "[active_nodes]";
    private static final String TAG_TRACK = "[anim_tracks]";
    private boolean[] logFlags;
    
    public AnimatorDebugMonitor(final IsoGameCharacter isoGameCharacter) {
        this.currentState = "null";
        this.monitoredVariables = new HashMap<String, MonitoredVar>();
        this.customVariables = new ArrayList<String>();
        this.logLines = new LinkedList<MonitorLogLine>();
        this.logLineQueue = new LinkedList<MonitorLogLine>();
        this.floatsListDirty = false;
        this.hasFilterChanges = false;
        this.hasLogUpdates = false;
        this.logString = "";
        this.floatsOut = new ArrayList<Float>();
        this.tickCount = 0;
        this.doTickStamps = false;
        (this.logFlags = new boolean[LogType.MAX.value()])[LogType.DEFAULT.value()] = true;
        for (int i = 0; i < this.logFlags.length; ++i) {
            this.logFlags[i] = true;
        }
        for (int j = 0; j < 1024; ++j) {
            this.floatsOut.add(0.0f);
        }
        this.initCustomVars();
        if (isoGameCharacter != null && isoGameCharacter.advancedAnimator != null) {
            final Iterator<String> iterator = isoGameCharacter.advancedAnimator.debugGetVariables().iterator();
            while (iterator.hasNext()) {
                registerVariable(iterator.next());
            }
        }
    }
    
    private void initCustomVars() {
        this.addCustomVariable("aim");
        this.addCustomVariable("bdead");
        this.addCustomVariable("bfalling");
        this.addCustomVariable("baimatfloor");
        this.addCustomVariable("battackfrombehind");
        this.addCustomVariable("attacktype");
        this.addCustomVariable("bundervehicle");
        this.addCustomVariable("reanimatetimer");
        this.addCustomVariable("isattacking");
        this.addCustomVariable("canclimbdownrope");
        this.addCustomVariable("frombehind");
        this.addCustomVariable("fallonfront");
        this.addCustomVariable("hashitreaction");
        this.addCustomVariable("hitreaction");
        this.addCustomVariable("collided");
        this.addCustomVariable("collidetype");
        this.addCustomVariable("intrees");
    }
    
    public void addCustomVariable(final String s) {
        final String lowerCase = s.toLowerCase();
        if (!this.customVariables.contains(lowerCase)) {
            this.customVariables.add(lowerCase);
        }
        registerVariable(s);
    }
    
    public void removeCustomVariable(final String s) {
        this.customVariables.remove(s.toLowerCase());
    }
    
    public void setFilter(final int n, final boolean b) {
        if (n >= 0 && n < LogType.MAX.value()) {
            this.logFlags[n] = b;
            this.hasFilterChanges = true;
        }
    }
    
    public boolean getFilter(final int n) {
        return n >= 0 && n < LogType.MAX.value() && this.logFlags[n];
    }
    
    public boolean isDoTickStamps() {
        return this.doTickStamps;
    }
    
    public void setDoTickStamps(final boolean doTickStamps) {
        if (this.doTickStamps != doTickStamps) {
            this.doTickStamps = doTickStamps;
            this.hasFilterChanges = true;
        }
    }
    
    private void queueLogLine(final String s) {
        this.addLogLine(LogType.DEFAULT, s, null, true);
    }
    
    private void queueLogLine(final String s, final Color color) {
        this.addLogLine(LogType.DEFAULT, s, color, true);
    }
    
    private void queueLogLine(final LogType logType, final String s, final Color color) {
        this.addLogLine(logType, s, color, true);
    }
    
    private void addLogLine(final String s) {
        this.addLogLine(LogType.DEFAULT, s, null, false);
    }
    
    private void addLogLine(final String s, final Color color) {
        this.addLogLine(LogType.DEFAULT, s, color, false);
    }
    
    private void addLogLine(final String s, final Color color, final boolean b) {
        this.addLogLine(LogType.DEFAULT, s, color, b);
    }
    
    private void addLogLine(final LogType logType, final String s, final Color color) {
        this.addLogLine(logType, s, color, false);
    }
    
    private void addLogLine(final LogType type, final String line, final Color color, final boolean b) {
        final MonitorLogLine monitorLogLine = new MonitorLogLine();
        monitorLogLine.line = line;
        monitorLogLine.color = color;
        monitorLogLine.type = type;
        monitorLogLine.tick = this.tickCount;
        if (b) {
            this.logLineQueue.add(monitorLogLine);
        }
        else {
            this.log(monitorLogLine);
        }
    }
    
    private void log(final MonitorLogLine e) {
        this.logLines.addFirst(e);
        if (this.logLines.size() > 1028) {
            this.logLines.removeLast();
        }
        this.hasLogUpdates = true;
    }
    
    private void processQueue() {
        while (this.logLineQueue.size() > 0) {
            this.log(this.logLineQueue.poll());
        }
    }
    
    private void preUpdate() {
        final Iterator<Map.Entry<String, MonitoredVar>> iterator = this.monitoredVariables.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().updated = false;
        }
        for (int i = 0; i < this.monitoredLayers.length; ++i) {
            final MonitoredLayer monitoredLayer = this.monitoredLayers[i];
            monitoredLayer.updated = false;
            final Iterator<Map.Entry<String, MonitoredNode>> iterator2 = monitoredLayer.activeNodes.entrySet().iterator();
            while (iterator2.hasNext()) {
                iterator2.next().getValue().updated = false;
            }
            final Iterator<Map.Entry<String, MonitoredTrack>> iterator3 = monitoredLayer.animTracks.entrySet().iterator();
            while (iterator3.hasNext()) {
                iterator3.next().getValue().updated = false;
            }
        }
    }
    
    private void postUpdate() {
        for (final Map.Entry<String, MonitoredVar> entry : this.monitoredVariables.entrySet()) {
            if (entry.getValue().active && !entry.getValue().updated) {
                this.addLogLine(LogType.VAR, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)entry.getKey(), entry.getValue().value), AnimatorDebugMonitor.col_var_deactivated);
                entry.getValue().active = false;
            }
        }
        for (int i = 0; i < this.monitoredLayers.length; ++i) {
            final MonitoredLayer monitoredLayer = this.monitoredLayers[i];
            for (final Map.Entry<String, MonitoredNode> entry2 : monitoredLayer.activeNodes.entrySet()) {
                if (entry2.getValue().active && !entry2.getValue().updated) {
                    this.addLogLine(LogType.NODE, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, monitoredLayer.index, entry2.getValue().name), AnimatorDebugMonitor.col_node_deactivated);
                    entry2.getValue().active = false;
                }
            }
            for (final Map.Entry<String, MonitoredTrack> entry3 : monitoredLayer.animTracks.entrySet()) {
                if (entry3.getValue().active && !entry3.getValue().updated) {
                    this.addLogLine(LogType.TRACK, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, monitoredLayer.index, entry3.getValue().name), AnimatorDebugMonitor.col_track_deactivated);
                    entry3.getValue().active = false;
                }
            }
            if (monitoredLayer.active && !monitoredLayer.updated) {
                this.addLogLine(LogType.LAYER, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, i, monitoredLayer.nodeName), AnimatorDebugMonitor.col_layer_deactivated);
                monitoredLayer.active = false;
            }
        }
    }
    
    public void update(final IsoGameCharacter isoGameCharacter, final AnimLayer[] array) {
        if (isoGameCharacter == null) {
            return;
        }
        this.ensureLayers(array);
        this.preUpdate();
        for (final IAnimationVariableSlot animationVariableSlot : isoGameCharacter.getGameVariables()) {
            this.updateVariable(animationVariableSlot.getKey(), animationVariableSlot.getValueString());
        }
        for (final String s : this.customVariables) {
            final String variableString = isoGameCharacter.getVariableString(s);
            if (variableString != null) {
                this.updateVariable(s, variableString);
            }
        }
        this.updateCurrentState((isoGameCharacter.getCurrentState() == null) ? "null" : isoGameCharacter.getCurrentState().getClass().getSimpleName());
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                this.updateLayer(i, array[i]);
            }
        }
        this.postUpdate();
        this.processQueue();
        ++this.tickCount;
    }
    
    private void updateCurrentState(final String s) {
        if (!this.currentState.equals(s)) {
            this.queueLogLine(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.currentState, s), AnimatorDebugMonitor.col_curstate);
            this.currentState = s;
        }
    }
    
    private void updateLayer(final int n, final AnimLayer animLayer) {
        final MonitoredLayer monitoredLayer = this.monitoredLayers[n];
        final String debugNodeName = animLayer.getDebugNodeName();
        if (!monitoredLayer.active) {
            monitoredLayer.active = true;
            this.queueLogLine(LogType.LAYER, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n, debugNodeName), AnimatorDebugMonitor.col_layer_activated);
        }
        if (!monitoredLayer.nodeName.equals(debugNodeName)) {
            this.queueLogLine(LogType.LAYER, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, n, monitoredLayer.nodeName, debugNodeName), AnimatorDebugMonitor.col_layer_nodename);
            monitoredLayer.nodeName = debugNodeName;
        }
        final Iterator<LiveAnimNode> iterator = animLayer.getLiveAnimNodes().iterator();
        while (iterator.hasNext()) {
            this.updateActiveNode(monitoredLayer, iterator.next().getSourceNode().m_Name);
        }
        if (animLayer.getAnimationTrack() != null) {
            for (final AnimationTrack animationTrack : animLayer.getAnimationTrack().getTracks()) {
                if (animationTrack.getLayerIdx() != n) {
                    continue;
                }
                this.updateAnimTrack(monitoredLayer, animationTrack.name, animationTrack.BlendDelta);
            }
        }
        monitoredLayer.updated = true;
    }
    
    private void updateActiveNode(final MonitoredLayer monitoredLayer, final String key) {
        MonitoredNode value = monitoredLayer.activeNodes.get(key);
        if (value == null) {
            value = new MonitoredNode();
            value.name = key;
            monitoredLayer.activeNodes.put(key, value);
        }
        if (!value.active) {
            value.active = true;
            this.queueLogLine(LogType.NODE, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, monitoredLayer.index, key), AnimatorDebugMonitor.col_node_activated);
        }
        value.updated = true;
    }
    
    private void updateAnimTrack(final MonitoredLayer monitoredLayer, final String key, final float n) {
        MonitoredTrack value = monitoredLayer.animTracks.get(key);
        if (value == null) {
            value = new MonitoredTrack();
            value.name = key;
            value.blendDelta = n;
            monitoredLayer.animTracks.put(key, value);
        }
        if (!value.active) {
            value.active = true;
            this.queueLogLine(LogType.TRACK, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, monitoredLayer.index, key), AnimatorDebugMonitor.col_track_activated);
        }
        if (value.blendDelta != n) {
            value.blendDelta = n;
        }
        value.updated = true;
    }
    
    private void updateVariable(final String key, final String s) {
        MonitoredVar value = this.monitoredVariables.get(key);
        boolean b = false;
        if (value == null) {
            value = new MonitoredVar();
            this.monitoredVariables.put(key, value);
            b = true;
        }
        Label_0223: {
            if (!value.active) {
                value.active = true;
                value.key = key;
                value.value = s;
                this.queueLogLine(LogType.VAR, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, s), AnimatorDebugMonitor.col_var_activated);
                if (b) {
                    registerVariable(key);
                }
            }
            else if (s == null) {
                if (value.isFloat) {
                    value.isFloat = false;
                    this.floatsListDirty = true;
                }
                value.value = null;
            }
            else {
                if (value.value != null) {
                    if (value.value.equals(s)) {
                        break Label_0223;
                    }
                }
                try {
                    value.logFloat(Float.parseFloat(s));
                    if (!value.isFloat) {
                        value.isFloat = true;
                        this.floatsListDirty = true;
                    }
                }
                catch (NumberFormatException ex) {
                    if (value.isFloat) {
                        value.isFloat = false;
                        this.floatsListDirty = true;
                    }
                }
                if (!value.isFloat) {
                    this.queueLogLine(LogType.VAR, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, value.value, s), AnimatorDebugMonitor.col_var_changed);
                }
                value.value = s;
            }
        }
        value.updated = true;
    }
    
    private void buildLogString() {
        final ListIterator<MonitorLogLine> listIterator = this.logLines.listIterator(0);
        int n = 0;
        int index = 0;
        while (listIterator.hasNext()) {
            final MonitorLogLine monitorLogLine = listIterator.next();
            ++index;
            if (!this.logFlags[monitorLogLine.type.value()]) {
                continue;
            }
            if (++n >= 128) {
                break;
            }
        }
        if (index == 0) {
            this.logString = "";
            return;
        }
        final ListIterator<MonitorLogLine> listIterator2 = this.logLines.listIterator(index);
        final StringBuilder sb = new StringBuilder();
        while (listIterator2.hasPrevious()) {
            final MonitorLogLine monitorLogLine2 = listIterator2.previous();
            if (!this.logFlags[monitorLogLine2.type.value()]) {
                continue;
            }
            sb.append(" <TEXT> ");
            if (this.doTickStamps) {
                sb.append("[");
                sb.append(String.format("%010d", monitorLogLine2.tick));
                sb.append("]");
            }
            if (monitorLogLine2.color != null) {
                sb.append(" <RGB:");
                sb.append(monitorLogLine2.color.r);
                sb.append(",");
                sb.append(monitorLogLine2.color.g);
                sb.append(",");
                sb.append(monitorLogLine2.color.b);
                sb.append("> ");
            }
            sb.append(monitorLogLine2.line);
            sb.append(" <LINE> ");
        }
        this.logString = sb.toString();
        this.hasLogUpdates = false;
        this.hasFilterChanges = false;
    }
    
    public boolean IsDirty() {
        return this.hasLogUpdates || this.hasFilterChanges;
    }
    
    public String getLogString() {
        if (this.hasLogUpdates || this.hasFilterChanges) {
            this.buildLogString();
        }
        return this.logString;
    }
    
    public boolean IsDirtyFloatList() {
        return this.floatsListDirty;
    }
    
    public ArrayList<String> getFloatNames() {
        this.floatsListDirty = false;
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        for (final Map.Entry<String, MonitoredVar> entry : this.monitoredVariables.entrySet()) {
            if (entry.getValue().isFloat) {
                list.add(entry.getValue().key);
            }
        }
        Collections.sort((List<Comparable>)list);
        return list;
    }
    
    public static boolean isKnownVarsDirty() {
        return AnimatorDebugMonitor.knownVarsDirty;
    }
    
    public static List<String> getKnownVariables() {
        AnimatorDebugMonitor.knownVarsDirty = false;
        Collections.sort(AnimatorDebugMonitor.knownVariables);
        return AnimatorDebugMonitor.knownVariables;
    }
    
    public void setSelectedVariable(final String key) {
        if (key == null) {
            this.selectedVariable = null;
        }
        else {
            this.selectedVariable = this.monitoredVariables.get(key);
        }
    }
    
    public String getSelectedVariable() {
        if (this.selectedVariable != null) {
            return this.selectedVariable.key;
        }
        return null;
    }
    
    public float getSelectedVariableFloat() {
        if (this.selectedVariable != null) {
            return this.selectedVariable.valFloat;
        }
        return 0.0f;
    }
    
    public String getSelectedVarMinFloat() {
        if (this.selectedVariable != null && this.selectedVariable.isFloat && this.selectedVariable.f_min != -1.0f) {
            return invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.selectedVariable.f_min);
        }
        return "-1.0";
    }
    
    public String getSelectedVarMaxFloat() {
        if (this.selectedVariable != null && this.selectedVariable.isFloat && this.selectedVariable.f_max != -1.0f) {
            return invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.selectedVariable.f_max);
        }
        return "1.0";
    }
    
    public ArrayList<Float> getSelectedVarFloatList() {
        if (this.selectedVariable != null && this.selectedVariable.isFloat) {
            final MonitoredVar selectedVariable = this.selectedVariable;
            int n = selectedVariable.f_index - 1;
            if (n < 0) {
                n = 0;
            }
            final float n2 = selectedVariable.f_max - selectedVariable.f_min;
            for (int i = 0; i < 1024; ++i) {
                this.floatsOut.set(i, (selectedVariable.f_floats[n--] - selectedVariable.f_min) / n2);
                if (n < 0) {
                    n = selectedVariable.f_floats.length - 1;
                }
            }
            return this.floatsOut;
        }
        return null;
    }
    
    public static void registerVariable(String lowerCase) {
        if (lowerCase == null) {
            return;
        }
        lowerCase = lowerCase.toLowerCase();
        if (!AnimatorDebugMonitor.knownVariables.contains(lowerCase)) {
            AnimatorDebugMonitor.knownVariables.add(lowerCase);
            AnimatorDebugMonitor.knownVarsDirty = true;
        }
    }
    
    private void ensureLayers(final AnimLayer[] array) {
        final int length = array.length;
        if (this.monitoredLayers == null || this.monitoredLayers.length != length) {
            this.monitoredLayers = new MonitoredLayer[length];
            for (int i = 0; i < length; ++i) {
                this.monitoredLayers[i] = new MonitoredLayer(i);
            }
        }
    }
    
    static {
        knownVariables = new ArrayList<String>();
        AnimatorDebugMonitor.knownVarsDirty = false;
        col_curstate = Colors.Cyan;
        col_layer_nodename = Colors.CornFlowerBlue;
        col_layer_activated = Colors.DarkTurquoise;
        col_layer_deactivated = Colors.Orange;
        col_track_activated = Colors.SandyBrown;
        col_track_deactivated = Colors.Salmon;
        col_node_activated = Colors.Pink;
        col_node_deactivated = Colors.Plum;
        col_var_activated = Colors.Chartreuse;
        col_var_changed = Colors.LimeGreen;
        col_var_deactivated = Colors.Gold;
    }
    
    private enum LogType
    {
        DEFAULT(0), 
        LAYER(1), 
        NODE(2), 
        TRACK(3), 
        VAR(4), 
        MAX(5);
        
        private final int val;
        
        private LogType(final int val) {
            this.val = val;
        }
        
        public int value() {
            return this.val;
        }
        
        private static /* synthetic */ LogType[] $values() {
            return new LogType[] { LogType.DEFAULT, LogType.LAYER, LogType.NODE, LogType.TRACK, LogType.VAR, LogType.MAX };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private class MonitoredVar
    {
        String key;
        String value;
        boolean isFloat;
        float valFloat;
        boolean active;
        boolean updated;
        float[] f_floats;
        int f_index;
        float f_min;
        float f_max;
        
        private MonitoredVar() {
            this.key = "";
            this.value = "";
            this.isFloat = false;
            this.active = false;
            this.updated = false;
            this.f_index = 0;
            this.f_min = -1.0f;
            this.f_max = 1.0f;
        }
        
        public void logFloat(final float f_max) {
            if (this.f_floats == null) {
                this.f_floats = new float[1024];
            }
            if (f_max == this.valFloat) {
                return;
            }
            this.valFloat = f_max;
            this.f_floats[this.f_index++] = f_max;
            if (f_max < this.f_min) {
                this.f_min = f_max;
            }
            if (f_max > this.f_max) {
                this.f_max = f_max;
            }
            if (this.f_index >= 1024) {
                this.f_index = 0;
            }
        }
    }
    
    private class MonitoredLayer
    {
        int index;
        String nodeName;
        HashMap<String, MonitoredNode> activeNodes;
        HashMap<String, MonitoredTrack> animTracks;
        boolean active;
        boolean updated;
        
        public MonitoredLayer(final int index) {
            this.nodeName = "";
            this.activeNodes = new HashMap<String, MonitoredNode>();
            this.animTracks = new HashMap<String, MonitoredTrack>();
            this.active = false;
            this.updated = false;
            this.index = index;
        }
    }
    
    private class MonitoredNode
    {
        String name;
        boolean active;
        boolean updated;
        
        private MonitoredNode() {
            this.name = "";
            this.active = false;
            this.updated = false;
        }
    }
    
    private class MonitoredTrack
    {
        String name;
        float blendDelta;
        boolean active;
        boolean updated;
        
        private MonitoredTrack() {
            this.name = "";
            this.active = false;
            this.updated = false;
        }
    }
    
    private class MonitorLogLine
    {
        String line;
        Color color;
        LogType type;
        int tick;
        
        private MonitorLogLine() {
            this.color = null;
            this.type = LogType.DEFAULT;
        }
    }
}
