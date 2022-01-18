// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

public interface IAnimListener
{
    void onAnimStarted(final AnimationTrack p0);
    
    void onLoopedAnim(final AnimationTrack p0);
    
    void onNonLoopedAnimFadeOut(final AnimationTrack p0);
    
    void onNonLoopedAnimFinished(final AnimationTrack p0);
    
    void onTrackDestroyed(final AnimationTrack p0);
}
