// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.PZXmlParserException;
import zombie.core.logger.ExceptionLogger;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.util.list.PZArrayUtil;
import zombie.util.PZXmlUtil;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import zombie.core.skinnedmodel.animation.BoneAxis;
import java.util.Comparator;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class AnimNode
{
    private static final Comparator<AnimEvent> s_eventsComparator;
    public String m_Name;
    public int m_Priority;
    public String m_AnimName;
    public String m_DeferredBoneName;
    public BoneAxis m_deferredBoneAxis;
    public boolean m_useDeferedRotation;
    public boolean m_Looped;
    public float m_BlendTime;
    public float m_BlendOutTime;
    public boolean m_StopAnimOnExit;
    public boolean m_EarlyTransitionOut;
    public String m_SpeedScale;
    public String m_SpeedScaleVariable;
    public float m_SpeedScaleRandomMultiplierMin;
    public float m_SpeedScaleRandomMultiplierMax;
    @XmlTransient
    private float m_SpeedScaleF;
    public float m_randomAdvanceFraction;
    public float m_maxTorsoTwist;
    public String m_Scalar;
    public String m_Scalar2;
    public boolean m_AnimReverse;
    public boolean m_SyncTrackingEnabled;
    public List<Anim2DBlend> m_2DBlends;
    public List<AnimCondition> m_Conditions;
    public List<AnimEvent> m_Events;
    public List<Anim2DBlendTriangle> m_2DBlendTri;
    public List<AnimTransition> m_Transitions;
    public List<AnimBoneWeight> m_SubStateBoneWeights;
    @XmlTransient
    public Anim2DBlendPicker m_picker;
    @XmlTransient
    public AnimState m_State;
    @XmlTransient
    private AnimTransition m_transitionOut;
    
    public AnimNode() {
        this.m_Name = "";
        this.m_Priority = 5;
        this.m_AnimName = "";
        this.m_DeferredBoneName = "";
        this.m_deferredBoneAxis = BoneAxis.Y;
        this.m_useDeferedRotation = false;
        this.m_Looped = true;
        this.m_BlendTime = 0.0f;
        this.m_BlendOutTime = -1.0f;
        this.m_StopAnimOnExit = false;
        this.m_EarlyTransitionOut = false;
        this.m_SpeedScale = "1.00";
        this.m_SpeedScaleVariable = null;
        this.m_SpeedScaleRandomMultiplierMin = 1.0f;
        this.m_SpeedScaleRandomMultiplierMax = 1.0f;
        this.m_SpeedScaleF = Float.POSITIVE_INFINITY;
        this.m_randomAdvanceFraction = 0.0f;
        this.m_maxTorsoTwist = 15.0f;
        this.m_Scalar = "";
        this.m_Scalar2 = "";
        this.m_AnimReverse = false;
        this.m_SyncTrackingEnabled = true;
        this.m_2DBlends = new ArrayList<Anim2DBlend>();
        this.m_Conditions = new ArrayList<AnimCondition>();
        this.m_Events = new ArrayList<AnimEvent>();
        this.m_2DBlendTri = new ArrayList<Anim2DBlendTriangle>();
        this.m_Transitions = new ArrayList<AnimTransition>();
        this.m_SubStateBoneWeights = new ArrayList<AnimBoneWeight>();
        this.m_State = null;
    }
    
    public static AnimNode Parse(final String s) {
        try {
            final AnimNode animNode = PZXmlUtil.parse(AnimNode.class, s);
            if (animNode.m_2DBlendTri.size() > 0) {
                (animNode.m_picker = new Anim2DBlendPicker()).SetPickTriangles(animNode.m_2DBlendTri);
            }
            final String[] array;
            PZArrayUtil.forEach(animNode.m_Events, animEvent -> {
                if ("SetVariable".equalsIgnoreCase(animEvent.m_EventName)) {
                    animEvent.m_ParameterValue.split("=");
                    if (array.length == 2) {
                        animEvent.m_SetVariable1 = array[0];
                        animEvent.m_SetVariable2 = array[1];
                    }
                }
                return;
            });
            animNode.m_Events.sort(AnimNode.s_eventsComparator);
            try {
                animNode.m_SpeedScaleF = Float.parseFloat(animNode.m_SpeedScale);
            }
            catch (NumberFormatException ex2) {
                animNode.m_SpeedScaleVariable = animNode.m_SpeedScale;
            }
            if (animNode.m_SubStateBoneWeights.isEmpty()) {
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Spine1", 0.5f));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Neck", 1.0f));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_BackPack", 1.0f));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Prop1", 1.0f));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Prop2", 1.0f));
            }
            for (int i = 0; i < animNode.m_SubStateBoneWeights.size(); ++i) {
                final AnimBoneWeight animBoneWeight = animNode.m_SubStateBoneWeights.get(i);
                animBoneWeight.boneName = JAssImpImporter.getSharedString(animBoneWeight.boneName, "AnimBoneWeight.boneName");
            }
            animNode.m_transitionOut = null;
            for (int j = 0; j < animNode.m_Transitions.size(); ++j) {
                final AnimTransition transitionOut = animNode.m_Transitions.get(j);
                if (StringUtils.isNullOrWhitespace(transitionOut.m_Target)) {
                    animNode.m_transitionOut = transitionOut;
                }
            }
            return animNode;
        }
        catch (PZXmlParserException ex) {
            System.err.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            ExceptionLogger.logException(ex);
            return null;
        }
    }
    
    public boolean checkConditions(final IAnimationVariableSource animationVariableSource) {
        return AnimCondition.pass(animationVariableSource, this.m_Conditions);
    }
    
    public float getSpeedScale(final IAnimationVariableSource animationVariableSource) {
        if (this.m_SpeedScaleF != Float.POSITIVE_INFINITY) {
            return this.m_SpeedScaleF;
        }
        return animationVariableSource.getVariableFloat(this.m_SpeedScale, 1.0f);
    }
    
    public boolean isIdleAnim() {
        return this.m_Name.contains("Idle");
    }
    
    public AnimTransition findTransitionTo(final IAnimationVariableSource animationVariableSource, final String s) {
        AnimTransition animTransition = null;
        for (int i = 0; i < this.m_Transitions.size(); ++i) {
            final AnimTransition animTransition2 = this.m_Transitions.get(i);
            if (StringUtils.equalsIgnoreCase(animTransition2.m_Target, s)) {
                if (AnimCondition.pass(animationVariableSource, animTransition2.m_Conditions)) {
                    animTransition = animTransition2;
                    break;
                }
            }
        }
        return animTransition;
    }
    
    @Override
    public String toString() {
        return String.format("AnimNode{ Name: %s, AnimName: %s, Conditions: %s }", this.m_Name, this.m_AnimName, this.getConditionsString());
    }
    
    public String getConditionsString() {
        return PZArrayUtil.arrayToString(this.m_Conditions, AnimCondition::getConditionString, "( ", " )", ", ");
    }
    
    public boolean isAbstract() {
        return StringUtils.isNullOrWhitespace(this.m_AnimName) && this.m_2DBlends.isEmpty();
    }
    
    public float getBlendOutTime() {
        if (this.m_transitionOut != null) {
            return this.m_transitionOut.m_blendOutTime;
        }
        if (this.m_BlendOutTime >= 0.0f) {
            return this.m_BlendOutTime;
        }
        return this.m_BlendTime;
    }
    
    public String getDeferredBoneName() {
        if (StringUtils.isNullOrWhitespace(this.m_DeferredBoneName)) {
            return "Translation_Data";
        }
        return this.m_DeferredBoneName;
    }
    
    public BoneAxis getDeferredBoneAxis() {
        return this.m_deferredBoneAxis;
    }
    
    public int getPriority() {
        return this.m_Priority;
    }
    
    static {
        s_eventsComparator = ((animEvent, animEvent2) -> Float.compare(animEvent.m_TimePc, animEvent2.m_TimePc));
    }
}
