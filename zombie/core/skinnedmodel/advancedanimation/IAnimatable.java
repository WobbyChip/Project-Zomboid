// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.characters.action.ActionContext;

public interface IAnimatable extends IAnimationVariableSource
{
    ActionContext getActionContext();
    
    AnimationPlayer getAnimationPlayer();
    
    AnimationPlayerRecorder getAnimationPlayerRecorder();
    
    boolean isAnimationRecorderActive();
    
    AdvancedAnimator getAdvancedAnimator();
    
    ModelInstance getModelInstance();
    
    String GetAnimSetName();
    
    String getUID();
    
    default short getOnlineID() {
        return -1;
    }
}
