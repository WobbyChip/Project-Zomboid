// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.skinnedmodel.animation.AnimationTrack;
import java.util.Map;
import zombie.debug.DebugType;
import zombie.characters.CharacterActionAnims;
import zombie.GameProfiler;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;
import zombie.util.list.PZArrayList;
import zombie.ai.StateMachine;
import zombie.characters.IsoZombie;
import java.util.Iterator;
import zombie.characters.IsoGameCharacter;
import org.w3c.dom.Element;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.ZomboidFileSystem;
import zombie.PredicatedFileWatcher;
import zombie.DebugFileWatcher;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import java.util.List;
import java.util.ArrayList;

public final class AdvancedAnimator implements IAnimEventCallback
{
    private IAnimatable character;
    public AnimationSet animSet;
    public final ArrayList<IAnimEventCallback> animCallbackHandlers;
    private AnimLayer m_rootLayer;
    private final List<SubLayerSlot> m_subLayers;
    public static float s_MotionScale;
    public static float s_RotationScale;
    private AnimatorDebugMonitor debugMonitor;
    private static long animSetModificationTime;
    private static long actionGroupModificationTime;
    private AnimationPlayerRecorder m_recorder;
    
    public AdvancedAnimator() {
        this.animCallbackHandlers = new ArrayList<IAnimEventCallback>();
        this.m_rootLayer = null;
        this.m_subLayers = new ArrayList<SubLayerSlot>();
        this.m_recorder = null;
    }
    
    public static void systemInit() {
        DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/AnimSets", AdvancedAnimator::isAnimSetFilePath, AdvancedAnimator::onAnimSetsRefreshTriggered));
        DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/actiongroups", AdvancedAnimator::isActionGroupFilePath, AdvancedAnimator::onActionGroupsRefreshTriggered));
        LoadDefaults();
    }
    
    private static boolean isAnimSetFilePath(final String s) {
        if (s == null) {
            return false;
        }
        if (!s.endsWith(".xml")) {
            return false;
        }
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(modIDs.get(i));
            if (modDetails != null) {
                if (modDetails.animSetsFile != null) {
                    if (s.startsWith(modDetails.animSetsFile.getPath())) {
                        return true;
                    }
                }
            }
        }
        return s.startsWith(ZomboidFileSystem.instance.getAnimSetsPath());
    }
    
    private static boolean isActionGroupFilePath(final String s) {
        if (s == null) {
            return false;
        }
        if (!s.endsWith(".xml")) {
            return false;
        }
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(modIDs.get(i));
            if (modDetails != null) {
                if (modDetails.actionGroupsFile != null) {
                    if (s.startsWith(modDetails.actionGroupsFile.getPath())) {
                        return true;
                    }
                }
            }
        }
        return s.startsWith(ZomboidFileSystem.instance.getActionGroupsPath());
    }
    
    private static void onActionGroupsRefreshTriggered(final String s) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        AdvancedAnimator.actionGroupModificationTime = System.currentTimeMillis() + 1000L;
    }
    
    private static void onAnimSetsRefreshTriggered(final String s) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        AdvancedAnimator.animSetModificationTime = System.currentTimeMillis() + 1000L;
    }
    
    public static void checkModifiedFiles() {
        if (AdvancedAnimator.animSetModificationTime != -1L && AdvancedAnimator.animSetModificationTime < System.currentTimeMillis()) {
            DebugLog.General.println("Refreshing AnimSets.");
            AdvancedAnimator.animSetModificationTime = -1L;
            LoadDefaults();
            LuaManager.GlobalObject.refreshAnimSets(true);
        }
        if (AdvancedAnimator.actionGroupModificationTime != -1L && AdvancedAnimator.actionGroupModificationTime < System.currentTimeMillis()) {
            DebugLog.General.println("Refreshing action groups.");
            AdvancedAnimator.actionGroupModificationTime = -1L;
            LuaManager.GlobalObject.reloadActionGroups();
        }
    }
    
    private static void LoadDefaults() {
        try {
            final Element xml = PZXmlUtil.parseXml("media/AnimSets/Defaults.xml");
            AdvancedAnimator.s_MotionScale = Float.parseFloat(xml.getElementsByTagName("MotionScale").item(0).getTextContent());
            AdvancedAnimator.s_RotationScale = Float.parseFloat(xml.getElementsByTagName("RotationScale").item(0).getTextContent());
        }
        catch (PZXmlParserException ex) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Lzombie/util/PZXmlParserException;)Ljava/lang/String;, ex));
            ex.printStackTrace();
        }
    }
    
    public String GetDebug() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GameState: ");
        if (this.character instanceof IsoGameCharacter) {
            final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)this.character;
            sb.append((isoGameCharacter.getCurrentState() == null) ? "null" : isoGameCharacter.getCurrentState().getClass().getSimpleName()).append("\n");
        }
        if (this.m_rootLayer != null) {
            sb.append("Layer: ").append(0).append("\n");
            sb.append(this.m_rootLayer.GetDebugString()).append("\n");
        }
        sb.append("Variables:\n");
        sb.append("Weapon: ").append(this.character.getVariableString("weapon")).append("\n");
        sb.append("Aim: ").append(this.character.getVariableString("aim")).append("\n");
        for (final IAnimationVariableSlot animationVariableSlot : this.character.getGameVariables()) {
            sb.append("  ").append(animationVariableSlot.getKey()).append(" : ").append(animationVariableSlot.getValueString()).append("\n");
        }
        return sb.toString();
    }
    
    public void OnAnimDataChanged(final boolean b) {
        if (b && this.character instanceof IsoGameCharacter) {
            final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)this.character;
            final StateMachine stateMachine = isoGameCharacter.getStateMachine();
            ++stateMachine.activeStateChanged;
            isoGameCharacter.setDefaultState();
            if (isoGameCharacter instanceof IsoZombie) {
                isoGameCharacter.setOnFloor(false);
            }
            final StateMachine stateMachine2 = isoGameCharacter.getStateMachine();
            --stateMachine2.activeStateChanged;
        }
        this.SetAnimSet(AnimationSet.GetAnimationSet(this.character.GetAnimSetName(), false));
        if (this.character.getAnimationPlayer() != null) {
            this.character.getAnimationPlayer().reset();
        }
        if (this.m_rootLayer != null) {
            this.m_rootLayer.Reset();
        }
        for (int i = 0; i < this.m_subLayers.size(); ++i) {
            this.m_subLayers.get(i).animLayer.Reset();
        }
    }
    
    public void Reload() {
    }
    
    public void init(final IAnimatable character) {
        this.character = character;
        this.m_rootLayer = new AnimLayer(character, this);
    }
    
    public void SetAnimSet(final AnimationSet animSet) {
        this.animSet = animSet;
    }
    
    @Override
    public void OnAnimEvent(final AnimLayer animLayer, final AnimEvent animEvent) {
        for (int i = 0; i < this.animCallbackHandlers.size(); ++i) {
            this.animCallbackHandlers.get(i).OnAnimEvent(animLayer, animEvent);
        }
    }
    
    public String getCurrentStateName() {
        return (this.m_rootLayer == null) ? null : this.m_rootLayer.getCurrentStateName();
    }
    
    public boolean containsState(final String s) {
        return this.animSet != null && this.animSet.containsState(s);
    }
    
    public void SetState(final String s) {
        this.SetState(s, (List<String>)PZArrayList.emptyList());
    }
    
    public void SetState(final String s2, final List<String> list) {
        if (this.animSet == null) {
            DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
            return;
        }
        if (!this.animSet.containsState(s2)) {
            DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        }
        this.m_rootLayer.TransitionTo(this.animSet.GetState(s2), false);
        PZArrayUtil.forEach(this.m_subLayers, subLayerSlot -> subLayerSlot.shouldBeActive = false);
        Lambda.forEachFrom(PZArrayUtil::forEach, list, this, (s, advancedAnimator) -> advancedAnimator.getOrCreateSlot(s).transitionTo(advancedAnimator.animSet.GetState(s), false));
        PZArrayUtil.forEach(this.m_subLayers, SubLayerSlot::applyTransition);
    }
    
    protected SubLayerSlot getOrCreateSlot(final String s) {
        SubLayerSlot subLayerSlot = null;
        for (int i = 0; i < this.m_subLayers.size(); ++i) {
            final SubLayerSlot subLayerSlot2 = this.m_subLayers.get(i);
            if (subLayerSlot2.animLayer.isCurrentState(s)) {
                subLayerSlot = subLayerSlot2;
                break;
            }
        }
        if (subLayerSlot != null) {
            return subLayerSlot;
        }
        for (int j = 0; j < this.m_subLayers.size(); ++j) {
            final SubLayerSlot subLayerSlot3 = this.m_subLayers.get(j);
            if (subLayerSlot3.animLayer.isStateless()) {
                subLayerSlot = subLayerSlot3;
                break;
            }
        }
        if (subLayerSlot != null) {
            return subLayerSlot;
        }
        final SubLayerSlot subLayerSlot4 = new SubLayerSlot(this.m_rootLayer, this.character, this);
        this.m_subLayers.add(subLayerSlot4);
        return subLayerSlot4;
    }
    
    public void update() {
        GameProfiler.getInstance().invokeAndMeasure("AdvancedAnimator.Update", this, AdvancedAnimator::updateInternal);
    }
    
    private void updateInternal() {
        if (this.character.getAnimationPlayer() == null) {
            return;
        }
        if (!this.character.getAnimationPlayer().isReady()) {
            return;
        }
        if (this.animSet == null) {
            return;
        }
        if (!this.m_rootLayer.hasState()) {
            this.m_rootLayer.TransitionTo(this.animSet.GetState("Idle"), true);
        }
        this.m_rootLayer.Update();
        for (int i = 0; i < this.m_subLayers.size(); ++i) {
            this.m_subLayers.get(i).update();
        }
        if (this.debugMonitor != null && this.character instanceof IsoGameCharacter) {
            final AnimLayer[] array = new AnimLayer[1 + this.getActiveSubLayerCount()];
            array[0] = this.m_rootLayer;
            int n = 0;
            for (int j = 0; j < this.m_subLayers.size(); ++j) {
                final SubLayerSlot subLayerSlot = this.m_subLayers.get(j);
                if (subLayerSlot.shouldBeActive) {
                    array[1 + n] = subLayerSlot.animLayer;
                    ++n;
                }
            }
            this.debugMonitor.update((IsoGameCharacter)this.character, array);
        }
    }
    
    public void render() {
        if (this.character.getAnimationPlayer() == null) {
            return;
        }
        if (!this.character.getAnimationPlayer().isReady()) {
            return;
        }
        if (this.animSet == null) {
            return;
        }
        if (!this.m_rootLayer.hasState()) {
            return;
        }
        this.m_rootLayer.render();
    }
    
    public void printDebugCharacterActions(final String s) {
        if (this.animSet != null) {
            final AnimState getState = this.animSet.GetState("actions");
            if (getState != null) {
                boolean b = false;
                for (final CharacterActionAnims characterActionAnims : CharacterActionAnims.values()) {
                    boolean b2 = false;
                    String string;
                    if (characterActionAnims == CharacterActionAnims.None) {
                        string = s;
                        b2 = true;
                    }
                    else {
                        string = characterActionAnims.toString();
                    }
                    boolean b3 = false;
                    final Iterator<AnimNode> iterator = getState.m_Nodes.iterator();
                    while (iterator.hasNext()) {
                        for (final AnimCondition animCondition : iterator.next().m_Conditions) {
                            if (animCondition.m_Type == AnimCondition.Type.STRING && animCondition.m_Name.toLowerCase().equals("performingaction") && animCondition.m_StringValue.equalsIgnoreCase(string)) {
                                b3 = true;
                                break;
                            }
                        }
                        if (b3) {
                            break;
                        }
                    }
                    if (b3) {
                        if (b2) {
                            b = true;
                        }
                    }
                    else {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string));
                    }
                }
                if (b) {
                    if (DebugLog.isEnabled(DebugType.Animation)) {
                        DebugLog.Animation.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    }
                }
                else if (DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
            }
        }
    }
    
    public ArrayList<String> debugGetVariables() {
        final ArrayList<String> list = new ArrayList<String>();
        if (this.animSet != null) {
            final Iterator<Map.Entry<String, AnimState>> iterator = this.animSet.states.entrySet().iterator();
            while (iterator.hasNext()) {
                final Iterator<AnimNode> iterator2 = iterator.next().getValue().m_Nodes.iterator();
                while (iterator2.hasNext()) {
                    for (final AnimCondition animCondition : iterator2.next().m_Conditions) {
                        if (animCondition.m_Name != null && !list.contains(animCondition.m_Name.toLowerCase())) {
                            list.add(animCondition.m_Name.toLowerCase());
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public AnimatorDebugMonitor getDebugMonitor() {
        return this.debugMonitor;
    }
    
    public void setDebugMonitor(final AnimatorDebugMonitor debugMonitor) {
        this.debugMonitor = debugMonitor;
    }
    
    public IAnimatable getCharacter() {
        return this.character;
    }
    
    public void updateSpeedScale(final String s, final float speedDelta) {
        if (this.m_rootLayer != null) {
            final List<LiveAnimNode> liveAnimNodes = this.m_rootLayer.getLiveAnimNodes();
            for (int i = 0; i < liveAnimNodes.size(); ++i) {
                final LiveAnimNode liveAnimNode = liveAnimNodes.get(i);
                if (liveAnimNode.isActive() && liveAnimNode.getSourceNode() != null && s.equals(liveAnimNode.getSourceNode().m_SpeedScaleVariable)) {
                    liveAnimNode.getSourceNode().m_SpeedScale = invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, speedDelta);
                    for (int j = 0; j < liveAnimNode.m_AnimationTracks.size(); ++j) {
                        liveAnimNode.m_AnimationTracks.get(j).SpeedDelta = speedDelta;
                    }
                }
            }
        }
    }
    
    public boolean containsAnyIdleNodes() {
        if (this.m_rootLayer == null) {
            return false;
        }
        boolean b = false;
        final List<LiveAnimNode> liveAnimNodes = this.m_rootLayer.getLiveAnimNodes();
        for (int n = 0; n < liveAnimNodes.size() && !b; b = liveAnimNodes.get(n).isIdleAnimActive(), ++n) {}
        for (int i = 0; i < this.getSubLayerCount(); ++i) {
            final List<LiveAnimNode> liveAnimNodes2 = this.getSubLayerAt(i).getLiveAnimNodes();
            for (int j = 0; j < liveAnimNodes2.size(); ++j) {
                b = liveAnimNodes2.get(j).isIdleAnimActive();
                if (!b) {
                    break;
                }
            }
        }
        return b;
    }
    
    public AnimLayer getRootLayer() {
        return this.m_rootLayer;
    }
    
    public int getSubLayerCount() {
        return this.m_subLayers.size();
    }
    
    public AnimLayer getSubLayerAt(final int n) {
        return this.m_subLayers.get(n).animLayer;
    }
    
    public int getActiveSubLayerCount() {
        int n = 0;
        for (int i = 0; i < this.m_subLayers.size(); ++i) {
            if (this.m_subLayers.get(i).shouldBeActive) {
                ++n;
            }
        }
        return n;
    }
    
    public void setRecorder(final AnimationPlayerRecorder recorder) {
        this.m_recorder = recorder;
    }
    
    public boolean isRecording() {
        return this.m_recorder != null && this.m_recorder.isRecording();
    }
    
    static {
        AdvancedAnimator.s_MotionScale = 0.76f;
        AdvancedAnimator.s_RotationScale = 0.76f;
        AdvancedAnimator.animSetModificationTime = -1L;
        AdvancedAnimator.actionGroupModificationTime = -1L;
    }
    
    public static class SubLayerSlot
    {
        public boolean shouldBeActive;
        public final AnimLayer animLayer;
        
        public SubLayerSlot(final AnimLayer animLayer, final IAnimatable animatable, final IAnimEventCallback animEventCallback) {
            this.shouldBeActive = false;
            this.animLayer = new AnimLayer(animLayer, animatable, animEventCallback);
        }
        
        public void update() {
            this.animLayer.Update();
        }
        
        public void transitionTo(final AnimState animState, final boolean b) {
            this.animLayer.TransitionTo(animState, b);
            this.shouldBeActive = (animState != null);
        }
        
        public void applyTransition() {
            if (!this.shouldBeActive) {
                this.transitionTo(null, false);
            }
        }
    }
}
