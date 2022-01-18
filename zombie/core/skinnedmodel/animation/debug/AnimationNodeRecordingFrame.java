// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.debug;

import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.ai.StateMachine;
import zombie.ai.State;
import zombie.util.list.PZArrayUtil;
import java.util.List;
import zombie.characters.action.ActionState;
import zombie.iso.Vector3;
import java.util.ArrayList;

public final class AnimationNodeRecordingFrame extends GenericNameWeightRecordingFrame
{
    private String m_actionStateName;
    private final ArrayList<String> m_actionSubStateNames;
    private String m_aiStateName;
    private String m_animStateName;
    private final ArrayList<String> m_animSubStateNames;
    private final ArrayList<String> m_aiSubStateNames;
    private final Vector3 m_characterToPlayerDiff;
    
    public AnimationNodeRecordingFrame(final String s) {
        super(s);
        this.m_actionSubStateNames = new ArrayList<String>();
        this.m_animSubStateNames = new ArrayList<String>();
        this.m_aiSubStateNames = new ArrayList<String>();
        this.m_characterToPlayerDiff = new Vector3();
    }
    
    public void logActionState(final ActionState actionState, final List<ActionState> list) {
        this.m_actionStateName = ((actionState != null) ? actionState.getName() : null);
        PZArrayUtil.arrayConvert(this.m_actionSubStateNames, (List<Object>)list, ActionState::getName);
    }
    
    public void logAIState(final State state, final List<StateMachine.SubstateSlot> list) {
        this.m_aiStateName = ((state != null) ? state.getName() : null);
        PZArrayUtil.arrayConvert(this.m_aiSubStateNames, list, substateSlot -> substateSlot.isEmpty() ? "" : substateSlot.getState().getName());
    }
    
    public void logAnimState(final AnimState animState) {
        this.m_animStateName = ((animState != null) ? animState.m_Name : null);
    }
    
    public void logCharacterToPlayerDiff(final Vector3 vector3) {
        this.m_characterToPlayerDiff.set(vector3);
    }
    
    public void writeHeader(final StringBuilder sb) {
        GenericNameValueRecordingFrame.appendCell(sb, "toPlayer.x");
        GenericNameValueRecordingFrame.appendCell(sb, "toPlayer.y");
        GenericNameValueRecordingFrame.appendCell(sb, "actionState");
        GenericNameValueRecordingFrame.appendCell(sb, "actionState.sub[0]");
        GenericNameValueRecordingFrame.appendCell(sb, "actionState.sub[1]");
        GenericNameValueRecordingFrame.appendCell(sb, "aiState");
        GenericNameValueRecordingFrame.appendCell(sb, "aiState.sub[0]");
        GenericNameValueRecordingFrame.appendCell(sb, "aiState.sub[1]");
        GenericNameValueRecordingFrame.appendCell(sb, "animState");
        GenericNameValueRecordingFrame.appendCell(sb, "animState.sub[0]");
        GenericNameValueRecordingFrame.appendCell(sb, "animState.sub[1]");
        GenericNameValueRecordingFrame.appendCell(sb, "nodeWeights.begin");
        super.writeHeader(sb);
    }
    
    @Override
    protected void writeData(final StringBuilder sb) {
        GenericNameValueRecordingFrame.appendCell(sb, this.m_characterToPlayerDiff.x);
        GenericNameValueRecordingFrame.appendCell(sb, this.m_characterToPlayerDiff.y);
        GenericNameValueRecordingFrame.appendCellQuot(sb, this.m_actionStateName);
        GenericNameValueRecordingFrame.appendCellQuot(sb, PZArrayUtil.getOrDefault(this.m_actionSubStateNames, 0, ""));
        GenericNameValueRecordingFrame.appendCellQuot(sb, PZArrayUtil.getOrDefault(this.m_actionSubStateNames, 1, ""));
        GenericNameValueRecordingFrame.appendCellQuot(sb, this.m_aiStateName);
        GenericNameValueRecordingFrame.appendCellQuot(sb, PZArrayUtil.getOrDefault(this.m_aiSubStateNames, 0, ""));
        GenericNameValueRecordingFrame.appendCellQuot(sb, PZArrayUtil.getOrDefault(this.m_aiSubStateNames, 1, ""));
        GenericNameValueRecordingFrame.appendCellQuot(sb, this.m_animStateName);
        GenericNameValueRecordingFrame.appendCellQuot(sb, PZArrayUtil.getOrDefault(this.m_animSubStateNames, 0, ""));
        GenericNameValueRecordingFrame.appendCellQuot(sb, PZArrayUtil.getOrDefault(this.m_animSubStateNames, 1, ""));
        GenericNameValueRecordingFrame.appendCell(sb);
        super.writeData(sb);
    }
}
