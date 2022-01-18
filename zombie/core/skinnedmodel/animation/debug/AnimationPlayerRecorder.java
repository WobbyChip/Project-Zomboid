// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.debug;

import zombie.iso.Vector3;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.ai.StateMachine;
import zombie.ai.State;
import zombie.characters.action.ActionState;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import java.util.List;
import java.util.Calendar;
import zombie.core.logger.LoggerManager;
import java.io.FileNotFoundException;
import zombie.debug.DebugLog;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.function.Consumer;
import java.text.SimpleDateFormat;
import zombie.characters.IsoGameCharacter;

public final class AnimationPlayerRecorder
{
    private boolean m_isRecording;
    private final AnimationTrackRecordingFrame m_animationTrackFrame;
    private final AnimationNodeRecordingFrame m_animationNodeFrame;
    private final AnimationVariableRecordingFrame m_animationVariableFrame;
    private final IsoGameCharacter m_character;
    private static String s_startupTimeStamp;
    private static final SimpleDateFormat s_fileNameSdf;
    
    public AnimationPlayerRecorder(final IsoGameCharacter character) {
        this.m_isRecording = false;
        this.m_character = character;
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.m_character.getUID());
        this.m_animationTrackFrame = new AnimationTrackRecordingFrame(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.m_animationNodeFrame = new AnimationNodeRecordingFrame(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.m_animationVariableFrame = new AnimationVariableRecordingFrame(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public void beginLine(final int frameNumber) {
        this.m_animationTrackFrame.reset();
        this.m_animationTrackFrame.setFrameNumber(frameNumber);
        this.m_animationNodeFrame.reset();
        this.m_animationNodeFrame.setFrameNumber(frameNumber);
        this.m_animationVariableFrame.reset();
        this.m_animationVariableFrame.setFrameNumber(frameNumber);
    }
    
    public void endLine() {
        this.m_animationTrackFrame.writeLine();
        this.m_animationNodeFrame.writeLine();
        this.m_animationVariableFrame.writeLine();
    }
    
    public void discardRecording() {
        this.m_animationTrackFrame.closeAndDiscard();
        this.m_animationNodeFrame.closeAndDiscard();
        this.m_animationVariableFrame.closeAndDiscard();
    }
    
    public static PrintStream openFileStream(final String s, final boolean append, final Consumer<String> consumer) {
        final String timeStampedFilePath = getTimeStampedFilePath(s);
        try {
            consumer.accept(timeStampedFilePath);
            return new PrintStream(new FileOutputStream(new File(timeStampedFilePath), append));
        }
        catch (FileNotFoundException ex) {
            DebugLog.General.error((Object)"Exception thrown trying to create animation player recording file.");
            DebugLog.General.error(ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    private static String getTimeStampedFilePath(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LoggerManager.getLogsDir(), File.separator, getTimeStampedFileName(s));
    }
    
    private static String getTimeStampedFileName(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getStartupTimeStamp(), s);
    }
    
    private static String getStartupTimeStamp() {
        if (AnimationPlayerRecorder.s_startupTimeStamp == null) {
            AnimationPlayerRecorder.s_startupTimeStamp = AnimationPlayerRecorder.s_fileNameSdf.format(Calendar.getInstance().getTime());
        }
        return AnimationPlayerRecorder.s_startupTimeStamp;
    }
    
    public void logAnimWeights(final List<AnimationTrack> list, final int[] array, final float[] array2, final Vector2 vector2) {
        this.m_animationTrackFrame.logAnimWeights(list, array, array2, vector2);
    }
    
    public void logAnimNode(final LiveAnimNode liveAnimNode) {
        if (liveAnimNode.isTransitioningIn()) {
            this.m_animationNodeFrame.logWeight(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, liveAnimNode.getTransitionFrom(), liveAnimNode.getName()), liveAnimNode.getTransitionLayerIdx(), liveAnimNode.getTransitionInWeight());
        }
        this.m_animationNodeFrame.logWeight(liveAnimNode.getName(), liveAnimNode.getLayerIdx(), liveAnimNode.getWeight());
    }
    
    public void logActionState(final ActionState actionState, final List<ActionState> list) {
        this.m_animationNodeFrame.logActionState(actionState, list);
    }
    
    public void logAIState(final State state, final List<StateMachine.SubstateSlot> list) {
        this.m_animationNodeFrame.logAIState(state, list);
    }
    
    public void logAnimState(final AnimState animState) {
        this.m_animationNodeFrame.logAnimState(animState);
    }
    
    public void logVariables(final IAnimationVariableSource animationVariableSource) {
        this.m_animationVariableFrame.logVariables(animationVariableSource);
    }
    
    public void logCharacterPos() {
        this.m_animationNodeFrame.logCharacterToPlayerDiff(IsoPlayer.getInstance().getPosition(new Vector3()).sub(this.getOwner().getPosition(new Vector3()), new Vector3()));
    }
    
    public IsoGameCharacter getOwner() {
        return this.m_character;
    }
    
    public boolean isRecording() {
        return this.m_isRecording;
    }
    
    public void setRecording(final boolean isRecording) {
        if (this.m_isRecording != isRecording) {
            this.m_isRecording = isRecording;
            DebugLog.General.println("AnimationPlayerRecorder %s.", this.m_isRecording ? "recording" : "stopped");
        }
    }
    
    static {
        AnimationPlayerRecorder.s_startupTimeStamp = null;
        s_fileNameSdf = new SimpleDateFormat("yy-MM-dd_HH-mm");
    }
}
