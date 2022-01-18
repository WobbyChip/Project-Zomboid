// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.Pool;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.util.PooledObject;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.Rand;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.debug.DebugType;
import zombie.core.math.PZMath;
import org.joml.Math;
import zombie.GameTime;
import zombie.GameProfiler;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugOptions;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.IAnimListener;

public final class AnimLayer implements IAnimListener
{
    private final AnimLayer m_parentLayer;
    private final IAnimatable m_Character;
    private AnimState m_State;
    private AnimNode m_CurrentNode;
    private IAnimEventCallback m_AnimEventsCallback;
    private LiveAnimNode m_currentSyncNode;
    private AnimationTrack m_currentSyncTrack;
    private final List<AnimNode> m_reusableAnimNodes;
    private final List<LiveAnimNode> m_liveAnimNodes;
    private static final AnimEvent s_activeAnimLoopedEvent;
    private static final AnimEvent s_activeNonLoopedAnimFadeOutEvent;
    private static final AnimEvent s_activeAnimFinishingEvent;
    private static final AnimEvent s_activeNonLoopedAnimFinishedEvent;
    
    public AnimLayer(final IAnimatable animatable, final IAnimEventCallback animEventCallback) {
        this(null, animatable, animEventCallback);
    }
    
    public AnimLayer(final AnimLayer parentLayer, final IAnimatable character, final IAnimEventCallback animEventsCallback) {
        this.m_State = null;
        this.m_CurrentNode = null;
        this.m_reusableAnimNodes = new ArrayList<AnimNode>();
        this.m_liveAnimNodes = new ArrayList<LiveAnimNode>();
        this.m_parentLayer = parentLayer;
        this.m_Character = character;
        this.m_AnimEventsCallback = animEventsCallback;
    }
    
    public String getCurrentStateName() {
        return (this.m_State == null) ? null : this.m_State.m_Name;
    }
    
    public boolean hasState() {
        return this.m_State != null;
    }
    
    public boolean isStateless() {
        return this.m_State == null;
    }
    
    public boolean isSubLayer() {
        return this.m_parentLayer != null;
    }
    
    public boolean isCurrentState(final String s) {
        return this.m_State != null && StringUtils.equals(this.m_State.m_Name, s);
    }
    
    public AnimationMultiTrack getAnimationTrack() {
        if (this.m_Character == null) {
            return null;
        }
        final AnimationPlayer animationPlayer = this.m_Character.getAnimationPlayer();
        if (animationPlayer == null) {
            return null;
        }
        return animationPlayer.getMultiTrack();
    }
    
    public IAnimationVariableSource getVariableSource() {
        return this.m_Character;
    }
    
    public LiveAnimNode getCurrentSyncNode() {
        return this.m_currentSyncNode;
    }
    
    public AnimationTrack getCurrentSyncTrack() {
        return this.m_currentSyncTrack;
    }
    
    @Override
    public void onAnimStarted(final AnimationTrack animationTrack) {
    }
    
    @Override
    public void onLoopedAnim(final AnimationTrack animationTrack) {
        this.invokeAnimEvent(animationTrack, AnimLayer.s_activeAnimLoopedEvent, false);
    }
    
    @Override
    public void onNonLoopedAnimFadeOut(final AnimationTrack animationTrack) {
        this.invokeAnimEvent(animationTrack, AnimLayer.s_activeAnimFinishingEvent, true);
        this.invokeAnimEvent(animationTrack, AnimLayer.s_activeNonLoopedAnimFadeOutEvent, true);
    }
    
    @Override
    public void onNonLoopedAnimFinished(final AnimationTrack animationTrack) {
        this.invokeAnimEvent(animationTrack, AnimLayer.s_activeAnimFinishingEvent, false);
        this.invokeAnimEvent(animationTrack, AnimLayer.s_activeNonLoopedAnimFinishedEvent, true);
    }
    
    @Override
    public void onTrackDestroyed(final AnimationTrack animationTrack) {
    }
    
    protected void invokeAnimEvent(final AnimationTrack animationTrack, final AnimEvent animEvent, final boolean b) {
        if (this.m_AnimEventsCallback == null) {
            return;
        }
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
            if (!liveAnimNode.m_TransitioningOut || b) {
                if (liveAnimNode.getSourceNode().m_State == this.m_State) {
                    if (liveAnimNode.m_AnimationTracks.contains(animationTrack)) {
                        this.invokeAnimEvent(animEvent);
                        break;
                    }
                }
            }
        }
    }
    
    protected void invokeAnimEvent(final AnimEvent animEvent) {
        if (this.m_AnimEventsCallback == null) {
            DebugLog.Animation.warn("invokeAnimEvent. No listener. %s", animEvent.toDetailsString());
            return;
        }
        this.m_AnimEventsCallback.OnAnimEvent(this, animEvent);
    }
    
    public String GetDebugString() {
        String name = this.m_Character.getAdvancedAnimator().animSet.m_Name;
        if (this.m_State != null) {
            name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, this.m_State.m_Name);
            if (this.m_CurrentNode != null) {
                name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, this.m_CurrentNode.m_Name, this.m_CurrentNode.m_AnimName);
            }
        }
        String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        final Iterator<LiveAnimNode> iterator = this.m_liveAnimNodes.iterator();
        while (iterator.hasNext()) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, iterator.next().getSourceNode().m_Name);
        }
        final AnimationMultiTrack animationTrack = this.getAnimationTrack();
        if (animationTrack != null) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            for (final AnimationTrack animationTrack2 : animationTrack.getTracks()) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;F)Ljava/lang/String;, s, animationTrack2.name, animationTrack2.BlendDelta);
            }
        }
        return s;
    }
    
    public void Reset() {
        final AnimationMultiTrack animationTrack = this.getAnimationTrack();
        for (int i = this.m_liveAnimNodes.size() - 1; i >= 0; --i) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
            liveAnimNode.setActive(false);
            if (animationTrack != null) {
                animationTrack.removeTracks(liveAnimNode.m_AnimationTracks);
            }
            this.m_liveAnimNodes.remove(i).release();
        }
        this.m_State = null;
    }
    
    public boolean TransitionTo(final AnimState state, final boolean b) {
        if (this.getAnimationTrack() == null) {
            if (this.m_Character == null) {
                DebugLog.General.error((Object)"AnimationTrack is null. Character is null.");
                this.m_State = null;
                return false;
            }
            if (this.m_Character.getAnimationPlayer() == null) {
                DebugLog.General.error((Object)"AnimationTrack is null. Character ModelInstance.AnimPlayer is null.");
                this.m_State = null;
                return false;
            }
            DebugLog.General.error((Object)"AnimationTrack is null. Unknown reason.");
            return false;
        }
        else {
            if (state == this.m_State && !b) {
                return true;
            }
            if (DebugOptions.instance.Animation.AnimLayer.LogStateChanges.getValue()) {
                final String format = String.format("State: %s%s => %s", new Object[] { (this.m_parentLayer == null) ? "" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, AnimState.getStateName(this.m_parentLayer.m_State)), AnimState.getStateName(this.m_State), AnimState.getStateName(state) });
                DebugLog.General.debugln(format);
                if (this.m_Character instanceof IsoGameCharacter) {
                    ((IsoGameCharacter)this.m_Character).setSayLine(format);
                }
            }
            this.m_State = state;
            for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
                this.m_liveAnimNodes.get(i).m_TransitioningOut = true;
            }
            return true;
        }
    }
    
    public void Update() {
        GameProfiler.getInstance().invokeAndMeasure("AnimLayer.Update", this, AnimLayer::updateInternal);
    }
    
    private void updateInternal() {
        final float timeDelta = GameTime.instance.getTimeDelta();
        this.removeFadedOutNodes();
        this.updateNodeActiveFlags();
        final LiveAnimNode highestLiveNode = this.getHighestLiveNode();
        this.m_currentSyncNode = highestLiveNode;
        this.m_currentSyncTrack = null;
        if (highestLiveNode == null) {
            return;
        }
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            this.m_liveAnimNodes.get(i).update(timeDelta);
        }
        final IAnimatable character = this.m_Character;
        this.updateMaximumTwist(character);
        final boolean b = DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue() && character.getVariableBoolean("dbgForceAnim") && character.getVariableBoolean("dbgForceAnimScalars");
        final String s = b ? character.getVariableString("dbgForceAnimNodeName") : null;
        final AnimationTrack syncTrack = this.findSyncTrack(highestLiveNode);
        this.m_currentSyncTrack = syncTrack;
        final float n = (syncTrack != null) ? syncTrack.getCurrentTimeFraction() : -1.0f;
        for (int j = 0; j < this.m_liveAnimNodes.size(); ++j) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(j);
            float duration = 1.0f;
            for (int k = 0; k < liveAnimNode.getPlayingTrackCount(); ++k) {
                final AnimationTrack playingTrack = liveAnimNode.getPlayingTrackAt(k);
                if (playingTrack.IsPlaying) {
                    if (syncTrack != null && playingTrack.SyncTrackingEnabled && playingTrack.isLooping() && playingTrack != syncTrack) {
                        playingTrack.moveCurrentTimeValueToFraction(n);
                    }
                    if (playingTrack.name.equals(liveAnimNode.getSourceNode().m_AnimName)) {
                        duration = playingTrack.getDuration();
                        liveAnimNode.m_NodeAnimTime = playingTrack.getCurrentTimeValue();
                    }
                }
            }
            if (this.m_AnimEventsCallback != null && liveAnimNode.getSourceNode().m_Events.size() > 0) {
                final float n2 = liveAnimNode.m_NodeAnimTime / duration;
                final float n3 = liveAnimNode.m_PrevNodeAnimTime / duration;
                final List<AnimEvent> events = liveAnimNode.getSourceNode().m_Events;
                for (int l = 0; l < events.size(); ++l) {
                    final AnimEvent animEvent = events.get(l);
                    if (animEvent.m_Time == AnimEvent.AnimEventTime.Percentage) {
                        final float timePc = animEvent.m_TimePc;
                        if (n3 < timePc && timePc <= n2) {
                            this.invokeAnimEvent(animEvent);
                        }
                        else {
                            if (!liveAnimNode.isLooped() && n2 < timePc) {
                                break;
                            }
                            if (liveAnimNode.isLooped() && n3 > n2) {
                                if (n3 < timePc && timePc <= n2 + 1.0f) {
                                    this.invokeAnimEvent(animEvent);
                                }
                                else if (n3 > timePc && timePc <= n2) {
                                    this.invokeAnimEvent(animEvent);
                                }
                            }
                        }
                    }
                }
            }
            if (liveAnimNode.getPlayingTrackCount() != 0) {
                final boolean b2 = b && StringUtils.equalsIgnoreCase(liveAnimNode.getSourceNode().m_Name, s);
                final String s2 = b2 ? "dbgForceScalar" : liveAnimNode.getSourceNode().m_Scalar;
                final String s3 = b2 ? "dbgForceScalar2" : liveAnimNode.getSourceNode().m_Scalar2;
                liveAnimNode.setTransitionInBlendDelta(liveAnimNode.getTransitionInWeight());
                if (liveAnimNode.m_AnimationTracks.size() > 1) {
                    this.applyBlendField(liveAnimNode, character.getVariableFloat(s2, 0.0f), character.getVariableFloat(s3, 0.0f));
                }
                else if (!liveAnimNode.m_AnimationTracks.isEmpty()) {
                    liveAnimNode.m_AnimationTracks.get(0).BlendDelta = liveAnimNode.getWeight() * Math.abs(character.getVariableFloat(s2, 1.0f));
                }
            }
        }
        if (this.isRecording()) {
            this.logBlendWeights();
            this.logCurrentState();
        }
    }
    
    private void updateMaximumTwist(final IAnimationVariableSource animationVariableSource) {
        final IAnimationVariableSlot variable = animationVariableSource.getVariable("maxTwist");
        if (variable == null) {
            return;
        }
        final float valueFloat = variable.getValueFloat();
        float value = 0.0f;
        float n = 1.0f;
        for (int i = this.m_liveAnimNodes.size() - 1; i >= 0; --i) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
            final float weight = liveAnimNode.getWeight();
            if (n <= 0.0f) {
                break;
            }
            final float clamp = PZMath.clamp(weight, 0.0f, n);
            n -= clamp;
            value += PZMath.clamp(liveAnimNode.getSourceNode().m_maxTorsoTwist, 0.0f, 70.0f) * clamp;
        }
        if (n > 0.0f) {
            value += valueFloat * n;
        }
        variable.setValue(value);
    }
    
    public void updateNodeActiveFlags() {
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            this.m_liveAnimNodes.get(i).setActive(false);
        }
        final AnimState state = this.m_State;
        final IAnimatable character = this.m_Character;
        if (state != null && !character.getVariableBoolean("AnimLocked")) {
            final List<AnimNode> animNodes = state.getAnimNodes(character, this.m_reusableAnimNodes);
            for (int j = 0; j < animNodes.size(); ++j) {
                this.getOrCreateLiveNode(animNodes.get(j));
            }
        }
        this.updateNewNodeTransitions();
    }
    
    private void updateNewNodeTransitions() {
        GameProfiler.getInstance().invokeAndMeasure("updateNewNodeTransitions", this, AnimLayer::updateNewNodeTransitionsInternal);
    }
    
    private void updateNewNodeTransitionsInternal() {
        final IAnimatable character = this.m_Character;
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
            if (liveAnimNode.isNew()) {
                if (liveAnimNode.wasActivated()) {
                    final LiveAnimNode transitionToNewNode = this.findTransitionToNewNode(liveAnimNode, false);
                    if (transitionToNewNode != null) {
                        final AnimTransition transitionTo = transitionToNewNode.findTransitionTo(character, liveAnimNode.getName());
                        float speedScale = transitionTo.m_speedScale;
                        if (speedScale == Float.POSITIVE_INFINITY) {
                            speedScale = liveAnimNode.getSpeedScale(this.m_Character);
                        }
                        AnimationTrack startAnimTrack = null;
                        if (!StringUtils.isNullOrWhitespace(transitionTo.m_AnimName)) {
                            final StartAnimTrackParameters alloc = StartAnimTrackParameters.alloc();
                            alloc.subLayerBoneWeights = transitionToNewNode.getSubStateBoneWeights();
                            alloc.speedScale = speedScale;
                            alloc.deferredBoneName = transitionToNewNode.getDeferredBoneName();
                            alloc.deferredBoneAxis = transitionToNewNode.getDeferredBoneAxis();
                            alloc.priority = transitionToNewNode.getPriority();
                            startAnimTrack = this.startAnimTrack(transitionTo.m_AnimName, alloc);
                            alloc.release();
                            if (startAnimTrack == null) {
                                if (DebugLog.isEnabled(DebugType.Animation)) {
                                    DebugLog.Animation.println("  TransitionTo failed to play transition track: %s -> %s -> %s", transitionToNewNode.getName(), transitionTo.m_AnimName, liveAnimNode.getName());
                                }
                                continue;
                            }
                            else if (DebugLog.isEnabled(DebugType.Animation)) {
                                DebugLog.Animation.println("  TransitionTo found: %s -> %s -> %s", transitionToNewNode.getName(), transitionTo.m_AnimName, liveAnimNode.getName());
                            }
                        }
                        else if (DebugLog.isEnabled(DebugType.Animation)) {
                            DebugLog.Animation.println("  TransitionTo found: %s -> <no anim> -> %s", transitionToNewNode.getName(), liveAnimNode.getName());
                        }
                        liveAnimNode.startTransitionIn(transitionToNewNode, transitionTo, startAnimTrack);
                        transitionToNewNode.setTransitionOut(transitionTo);
                    }
                }
            }
        }
    }
    
    public LiveAnimNode findTransitionToNewNode(final LiveAnimNode liveAnimNode, final boolean b) {
        LiveAnimNode transitionToNewNode = null;
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            final LiveAnimNode liveAnimNode2 = this.m_liveAnimNodes.get(i);
            if (liveAnimNode2 != liveAnimNode) {
                if (b || liveAnimNode2.wasDeactivated()) {
                    if (liveAnimNode2.getSourceNode().findTransitionTo(this.m_Character, liveAnimNode.getName()) != null) {
                        transitionToNewNode = liveAnimNode2;
                        break;
                    }
                }
            }
        }
        if (transitionToNewNode == null && this.isSubLayer()) {
            transitionToNewNode = this.m_parentLayer.findTransitionToNewNode(liveAnimNode, true);
        }
        return transitionToNewNode;
    }
    
    public void removeFadedOutNodes() {
        for (int i = this.m_liveAnimNodes.size() - 1; i >= 0; --i) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
            if (!liveAnimNode.isActive()) {
                if (!liveAnimNode.isTransitioningIn() || liveAnimNode.getTransitionInWeight() <= 0.01f) {
                    if (liveAnimNode.getWeight() <= 0.01f) {
                        this.removeLiveNodeAt(i);
                    }
                }
            }
        }
    }
    
    public void render() {
        final IAnimatable character = this.m_Character;
        final boolean b = DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue() && character.getVariableBoolean("dbgForceAnim") && character.getVariableBoolean("dbgForceAnimScalars");
        final String s = b ? character.getVariableString("dbgForceAnimNodeName") : null;
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
            if (liveAnimNode.m_AnimationTracks.size() > 1) {
                final boolean b2 = b && StringUtils.equalsIgnoreCase(liveAnimNode.getSourceNode().m_Name, s);
                final String s2 = b2 ? "dbgForceScalar" : liveAnimNode.getSourceNode().m_Scalar;
                final String s3 = b2 ? "dbgForceScalar2" : liveAnimNode.getSourceNode().m_Scalar2;
                final float variableFloat = character.getVariableFloat(s2, 0.0f);
                final float variableFloat2 = character.getVariableFloat(s3, 0.0f);
                if (liveAnimNode.isActive()) {
                    liveAnimNode.getSourceNode().m_picker.render(variableFloat, variableFloat2);
                }
            }
        }
    }
    
    private void logBlendWeights() {
        final AnimationPlayerRecorder recorder = this.m_Character.getAnimationPlayer().getRecorder();
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            recorder.logAnimNode(this.m_liveAnimNodes.get(i));
        }
    }
    
    private void logCurrentState() {
        this.m_Character.getAnimationPlayer().getRecorder().logAnimState(this.m_State);
    }
    
    private void removeLiveNodeAt(final int n) {
        final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(n);
        final AnimationMultiTrack animationTrack = this.getAnimationTrack();
        animationTrack.removeTracks(liveAnimNode.m_AnimationTracks);
        animationTrack.removeTrack(liveAnimNode.getTransitionInTrack());
        this.m_liveAnimNodes.remove(n).release();
    }
    
    private void applyBlendField(final LiveAnimNode liveAnimNode, final float n, final float n2) {
        if (liveAnimNode.isActive()) {
            final AnimNode sourceNode = liveAnimNode.getSourceNode();
            final Anim2DBlendPicker.PickResults pick = sourceNode.m_picker.Pick(n, n2);
            final Anim2DBlend node1 = pick.node1;
            final Anim2DBlend node2 = pick.node2;
            final Anim2DBlend node3 = pick.node3;
            if (Float.isNaN(pick.scale1)) {
                pick.scale1 = 0.5f;
            }
            if (Float.isNaN(pick.scale2)) {
                pick.scale2 = 0.5f;
            }
            if (Float.isNaN(pick.scale3)) {
                pick.scale3 = 0.5f;
            }
            final float scale1 = pick.scale1;
            final float scale2 = pick.scale2;
            final float scale3 = pick.scale3;
            for (int i = 0; i < liveAnimNode.m_AnimationTracks.size(); ++i) {
                final Anim2DBlend anim2DBlend = sourceNode.m_2DBlends.get(i);
                final AnimationTrack animationTrack = liveAnimNode.m_AnimationTracks.get(i);
                if (anim2DBlend == node1) {
                    animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, scale1, 0.15f);
                }
                else if (anim2DBlend == node2) {
                    animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, scale2, 0.15f);
                }
                else if (anim2DBlend == node3) {
                    animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, scale3, 0.15f);
                }
                else {
                    animationTrack.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack.blendFieldWeight, 0.0f, 0.15f);
                }
                if (animationTrack.blendFieldWeight < 1.0E-4f) {
                    animationTrack.blendFieldWeight = 0.0f;
                }
                animationTrack.blendFieldWeight = PZMath.clamp(animationTrack.blendFieldWeight, 0.0f, 1.0f);
            }
        }
        final float weight = liveAnimNode.getWeight();
        for (int j = 0; j < liveAnimNode.m_AnimationTracks.size(); ++j) {
            final AnimationTrack animationTrack2 = liveAnimNode.m_AnimationTracks.get(j);
            animationTrack2.BlendDelta = animationTrack2.blendFieldWeight * weight;
        }
    }
    
    private void getOrCreateLiveNode(final AnimNode animNode) {
        final LiveAnimNode liveNode = this.findLiveNode(animNode);
        if (liveNode != null) {
            liveNode.setActive(true);
            return;
        }
        final LiveAnimNode alloc = LiveAnimNode.alloc(this, animNode, this.getDepth());
        if (animNode.m_2DBlends.size() > 0) {
            for (int i = 0; i < animNode.m_2DBlends.size(); ++i) {
                this.startAnimTrack(animNode.m_2DBlends.get(i).m_AnimName, alloc);
            }
        }
        else {
            this.startAnimTrack(animNode.m_AnimName, alloc);
        }
        alloc.setActive(true);
        this.m_liveAnimNodes.add(alloc);
    }
    
    private LiveAnimNode findLiveNode(final AnimNode animNode) {
        LiveAnimNode liveAnimNode = null;
        for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
            final LiveAnimNode liveAnimNode2 = this.m_liveAnimNodes.get(i);
            if (!liveAnimNode2.m_TransitioningOut) {
                if (liveAnimNode2.getSourceNode() == animNode) {
                    liveAnimNode = liveAnimNode2;
                    break;
                }
                if (liveAnimNode2.getSourceNode().m_State == animNode.m_State && liveAnimNode2.getSourceNode().m_Name.equals(animNode.m_Name)) {
                    liveAnimNode = liveAnimNode2;
                    break;
                }
            }
        }
        return liveAnimNode;
    }
    
    private void startAnimTrack(final String s, final LiveAnimNode liveAnimNode) {
        final AnimNode sourceNode = liveAnimNode.getSourceNode();
        final float speedScale = sourceNode.getSpeedScale(this.m_Character);
        final float lerp = PZMath.lerp(sourceNode.m_SpeedScaleRandomMultiplierMin, sourceNode.m_SpeedScaleRandomMultiplierMax, Rand.Next(0.0f, 1.0f));
        final StartAnimTrackParameters alloc = StartAnimTrackParameters.alloc();
        alloc.subLayerBoneWeights = sourceNode.m_SubStateBoneWeights;
        alloc.syncTrackingEnabled = sourceNode.m_SyncTrackingEnabled;
        alloc.speedScale = speedScale * lerp;
        alloc.initialWeight = liveAnimNode.getWeight();
        alloc.isLooped = liveAnimNode.isLooped();
        alloc.isReversed = sourceNode.m_AnimReverse;
        alloc.deferredBoneName = sourceNode.getDeferredBoneName();
        alloc.deferredBoneAxis = sourceNode.getDeferredBoneAxis();
        alloc.useDeferredRotation = sourceNode.m_useDeferedRotation;
        alloc.priority = sourceNode.getPriority();
        final AnimationTrack startAnimTrack = this.startAnimTrack(s, alloc);
        alloc.release();
        if (startAnimTrack != null) {
            startAnimTrack.addListener(liveAnimNode);
            liveAnimNode.addMainTrack(startAnimTrack);
        }
    }
    
    private AnimationTrack startAnimTrack(final String s, final StartAnimTrackParameters startAnimTrackParameters) {
        final AnimationPlayer animationPlayer = this.m_Character.getAnimationPlayer();
        if (!animationPlayer.isReady()) {
            return null;
        }
        final AnimationTrack play = animationPlayer.play(s, startAnimTrackParameters.isLooped);
        if (play == null) {
            return null;
        }
        final SkinningData skinningData = animationPlayer.getSkinningData();
        if (this.isSubLayer()) {
            play.setBoneWeights(startAnimTrackParameters.subLayerBoneWeights);
            play.initBoneWeights(skinningData);
        }
        else {
            play.setBoneWeights(null);
        }
        final SkinningBone bone = skinningData.getBone(startAnimTrackParameters.deferredBoneName);
        if (bone == null) {
            DebugLog.Animation.error("Deferred bone not found: \"%s\"", startAnimTrackParameters.deferredBoneName);
        }
        play.SpeedDelta = startAnimTrackParameters.speedScale;
        play.SyncTrackingEnabled = startAnimTrackParameters.syncTrackingEnabled;
        play.setDeferredBone(bone, startAnimTrackParameters.deferredBoneAxis);
        play.setUseDeferredRotation(startAnimTrackParameters.useDeferredRotation);
        play.BlendDelta = startAnimTrackParameters.initialWeight;
        play.setLayerIdx(this.getDepth());
        play.reverse = startAnimTrackParameters.isReversed;
        play.priority = startAnimTrackParameters.priority;
        play.addListener(this);
        return play;
    }
    
    public int getDepth() {
        if (this.m_parentLayer != null) {
            return this.m_parentLayer.getDepth() + 1;
        }
        return 0;
    }
    
    private LiveAnimNode getHighestLiveNode() {
        if (this.m_liveAnimNodes.isEmpty()) {
            return null;
        }
        LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(0);
        for (int i = this.m_liveAnimNodes.size() - 1; i >= 0; --i) {
            final LiveAnimNode liveAnimNode2 = this.m_liveAnimNodes.get(i);
            if (liveAnimNode2.getWeight() > liveAnimNode.getWeight()) {
                liveAnimNode = liveAnimNode2;
            }
        }
        return liveAnimNode;
    }
    
    private AnimationTrack findSyncTrack(final LiveAnimNode liveAnimNode) {
        AnimationTrack currentSyncTrack = null;
        if (this.m_parentLayer != null) {
            currentSyncTrack = this.m_parentLayer.getCurrentSyncTrack();
            if (currentSyncTrack != null) {
                return currentSyncTrack;
            }
        }
        for (int i = 0; i < liveAnimNode.getPlayingTrackCount(); ++i) {
            final AnimationTrack playingTrack = liveAnimNode.getPlayingTrackAt(i);
            if (playingTrack.SyncTrackingEnabled) {
                if (playingTrack.hasClip()) {
                    if (currentSyncTrack == null || playingTrack.BlendDelta > currentSyncTrack.BlendDelta) {
                        currentSyncTrack = playingTrack;
                    }
                }
            }
        }
        return currentSyncTrack;
    }
    
    public String getDebugNodeName() {
        String name = this.m_Character.getAdvancedAnimator().animSet.m_Name;
        if (this.m_State != null) {
            name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, this.m_State.m_Name);
            if (this.m_CurrentNode != null) {
                name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, this.m_CurrentNode.m_Name, this.m_CurrentNode.m_AnimName);
            }
            else if (!this.m_liveAnimNodes.isEmpty()) {
                for (int i = 0; i < this.m_liveAnimNodes.size(); ++i) {
                    final LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(i);
                    if (this.m_State.m_Nodes.contains(liveAnimNode.getSourceNode())) {
                        name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, liveAnimNode.getName());
                        break;
                    }
                }
            }
        }
        return name;
    }
    
    public List<LiveAnimNode> getLiveAnimNodes() {
        return this.m_liveAnimNodes;
    }
    
    public boolean isRecording() {
        return this.m_Character.getAdvancedAnimator().isRecording();
    }
    
    static {
        s_activeAnimLoopedEvent = new AnimEvent();
        AnimLayer.s_activeAnimLoopedEvent.m_TimePc = 1.0f;
        AnimLayer.s_activeAnimLoopedEvent.m_EventName = "ActiveAnimLooped";
        s_activeNonLoopedAnimFadeOutEvent = new AnimEvent();
        AnimLayer.s_activeNonLoopedAnimFadeOutEvent.m_TimePc = 1.0f;
        AnimLayer.s_activeNonLoopedAnimFadeOutEvent.m_EventName = "NonLoopedAnimFadeOut";
        s_activeAnimFinishingEvent = new AnimEvent();
        AnimLayer.s_activeAnimFinishingEvent.m_Time = AnimEvent.AnimEventTime.End;
        AnimLayer.s_activeAnimFinishingEvent.m_EventName = "ActiveAnimFinishing";
        s_activeNonLoopedAnimFinishedEvent = new AnimEvent();
        AnimLayer.s_activeNonLoopedAnimFinishedEvent.m_Time = AnimEvent.AnimEventTime.End;
        AnimLayer.s_activeNonLoopedAnimFinishedEvent.m_EventName = "ActiveAnimFinished";
    }
    
    private static class StartAnimTrackParameters extends PooledObject
    {
        public int priority;
        List<AnimBoneWeight> subLayerBoneWeights;
        boolean syncTrackingEnabled;
        float speedScale;
        float initialWeight;
        boolean isLooped;
        boolean isReversed;
        String deferredBoneName;
        BoneAxis deferredBoneAxis;
        boolean useDeferredRotation;
        private static final Pool<StartAnimTrackParameters> s_pool;
        
        private void reset() {
            this.priority = 0;
            this.subLayerBoneWeights = null;
            this.syncTrackingEnabled = false;
            this.speedScale = 1.0f;
            this.initialWeight = 0.0f;
            this.isLooped = false;
            this.isReversed = false;
            this.deferredBoneName = null;
            this.deferredBoneAxis = BoneAxis.Y;
            this.useDeferredRotation = false;
        }
        
        @Override
        public void onReleased() {
            this.reset();
        }
        
        protected StartAnimTrackParameters() {
        }
        
        public static StartAnimTrackParameters alloc() {
            return StartAnimTrackParameters.s_pool.alloc();
        }
        
        static {
            s_pool = new Pool<StartAnimTrackParameters>(StartAnimTrackParameters::new);
        }
    }
}
