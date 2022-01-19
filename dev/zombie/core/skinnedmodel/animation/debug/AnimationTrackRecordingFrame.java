// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.debug;

import zombie.core.skinnedmodel.animation.AnimationTrack;
import java.util.List;
import zombie.iso.Vector2;

public final class AnimationTrackRecordingFrame extends GenericNameWeightRecordingFrame
{
    private Vector2 m_deferredMovement;
    
    public AnimationTrackRecordingFrame(final String s) {
        super(s);
        this.m_deferredMovement = new Vector2();
    }
    
    @Override
    public void reset() {
        super.reset();
        this.m_deferredMovement.set(0.0f, 0.0f);
    }
    
    public void logAnimWeights(final List<AnimationTrack> list, final int[] array, final float[] array2, final Vector2 vector2) {
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i];
            if (n < 0) {
                break;
            }
            final float n2 = array2[i];
            final AnimationTrack animationTrack = list.get(n);
            this.logWeight(animationTrack.name, animationTrack.getLayerIdx(), n2);
        }
        this.m_deferredMovement.set(vector2);
    }
    
    public Vector2 getDeferredMovement() {
        return this.m_deferredMovement;
    }
    
    public void writeHeader(final StringBuilder sb) {
        sb.append(",");
        sb.append("dm.x").append(",").append("dm.y");
        super.writeHeader(sb);
    }
    
    @Override
    protected void writeData(final StringBuilder sb) {
        sb.append(",");
        sb.append(this.getDeferredMovement().x).append(",").append(this.getDeferredMovement().y);
        super.writeData(sb);
    }
}
