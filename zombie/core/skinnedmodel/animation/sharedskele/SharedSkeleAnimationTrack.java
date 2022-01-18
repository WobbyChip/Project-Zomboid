// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.sharedskele;

import zombie.debug.DebugOptions;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.animation.AnimTrackSampler;

public class SharedSkeleAnimationTrack implements AnimTrackSampler
{
    private int m_numFrames;
    private float m_totalTime;
    private boolean m_isLooped;
    private BoneTrack[] m_boneTracks;
    private float m_currentTime;
    
    public SharedSkeleAnimationTrack() {
        this.m_currentTime = 0.0f;
    }
    
    public void set(final AnimTrackSampler animTrackSampler, final float n) {
        final float totalTime = animTrackSampler.getTotalTime();
        final boolean looped = animTrackSampler.isLooped();
        final int numBones = animTrackSampler.getNumBones();
        this.m_totalTime = totalTime;
        this.m_numFrames = PZMath.max((int)(totalTime * n + 0.99f), 1);
        this.m_isLooped = looped;
        this.m_boneTracks = new BoneTrack[numBones];
        for (int i = 0; i < numBones; ++i) {
            this.m_boneTracks[i] = new BoneTrack();
            this.m_boneTracks[i].m_animationData = new float[this.m_numFrames * 16];
        }
        final Matrix4f matrix4f = new Matrix4f();
        final float n2 = totalTime / (this.m_numFrames - 1);
        for (int j = 0; j < this.m_numFrames; ++j) {
            animTrackSampler.moveToTime(n2 * j);
            for (int k = 0; k < numBones; ++k) {
                animTrackSampler.getBoneMatrix(k, matrix4f);
                final int n3 = j * 16;
                final float[] animationData = this.m_boneTracks[k].m_animationData;
                animationData[n3] = matrix4f.m00;
                animationData[n3 + 1] = matrix4f.m01;
                animationData[n3 + 2] = matrix4f.m02;
                animationData[n3 + 3] = matrix4f.m03;
                animationData[n3 + 4] = matrix4f.m10;
                animationData[n3 + 5] = matrix4f.m11;
                animationData[n3 + 6] = matrix4f.m12;
                animationData[n3 + 7] = matrix4f.m13;
                animationData[n3 + 8] = matrix4f.m20;
                animationData[n3 + 9] = matrix4f.m21;
                animationData[n3 + 10] = matrix4f.m22;
                animationData[n3 + 11] = matrix4f.m23;
                animationData[n3 + 12] = matrix4f.m30;
                animationData[n3 + 13] = matrix4f.m31;
                animationData[n3 + 14] = matrix4f.m32;
                animationData[n3 + 15] = matrix4f.m33;
            }
        }
    }
    
    @Override
    public float getTotalTime() {
        return this.m_totalTime;
    }
    
    @Override
    public boolean isLooped() {
        return this.m_isLooped;
    }
    
    @Override
    public void moveToTime(final float currentTime) {
        this.m_currentTime = currentTime;
    }
    
    @Override
    public float getCurrentTime() {
        return this.m_currentTime;
    }
    
    @Override
    public void getBoneMatrix(final int n, final Matrix4f matrix4f) {
        final float n2 = this.getCurrentTime() / this.m_totalTime * (this.m_numFrames - 1);
        if (this.isLooped()) {
            this.sampleAtTime_Looped(matrix4f, n, n2);
        }
        else {
            this.sampleAtTime_NonLooped(matrix4f, n, n2);
        }
    }
    
    @Override
    public int getNumBones() {
        return (this.m_boneTracks != null) ? this.m_boneTracks.length : 0;
    }
    
    private void sampleAtTime_NonLooped(final Matrix4f matrix4f, final int n, final float n2) {
        final int n3 = (int)n2;
        final float n4 = n2 - n3;
        final int clamp = PZMath.clamp(n3, 0, this.m_numFrames - 1);
        this.sampleBoneData(n, clamp, PZMath.clamp(clamp + 1, 0, this.m_numFrames - 1), n4, DebugOptions.instance.Animation.SharedSkeles.AllowLerping.getValue(), matrix4f);
    }
    
    private void sampleAtTime_Looped(final Matrix4f matrix4f, final int n, final float n2) {
        final int n3 = (int)n2;
        final float n4 = n2 - n3;
        final int n5 = n3 % this.m_numFrames;
        this.sampleBoneData(n, n5, (n5 + 1) % this.m_numFrames, n4, DebugOptions.instance.Animation.SharedSkeles.AllowLerping.getValue(), matrix4f);
    }
    
    private void sampleBoneData(final int n, final int n2, final int n3, final float n4, final boolean b, final Matrix4f matrix4f) {
        final int n5 = n2 * 16;
        final float[] animationData = this.m_boneTracks[n].m_animationData;
        if (n2 != n3 && b) {
            final int n6 = n3 * 16;
            matrix4f.m00 = PZMath.lerp(animationData[n5], animationData[n6], n4);
            matrix4f.m01 = PZMath.lerp(animationData[n5 + 1], animationData[n6 + 1], n4);
            matrix4f.m02 = PZMath.lerp(animationData[n5 + 2], animationData[n6 + 2], n4);
            matrix4f.m03 = PZMath.lerp(animationData[n5 + 3], animationData[n6 + 3], n4);
            matrix4f.m10 = PZMath.lerp(animationData[n5 + 4], animationData[n6 + 4], n4);
            matrix4f.m11 = PZMath.lerp(animationData[n5 + 5], animationData[n6 + 5], n4);
            matrix4f.m12 = PZMath.lerp(animationData[n5 + 6], animationData[n6 + 6], n4);
            matrix4f.m13 = PZMath.lerp(animationData[n5 + 7], animationData[n6 + 7], n4);
            matrix4f.m20 = PZMath.lerp(animationData[n5 + 8], animationData[n6 + 8], n4);
            matrix4f.m21 = PZMath.lerp(animationData[n5 + 9], animationData[n6 + 9], n4);
            matrix4f.m22 = PZMath.lerp(animationData[n5 + 10], animationData[n6 + 10], n4);
            matrix4f.m23 = PZMath.lerp(animationData[n5 + 11], animationData[n6 + 11], n4);
            matrix4f.m30 = PZMath.lerp(animationData[n5 + 12], animationData[n6 + 12], n4);
            matrix4f.m31 = PZMath.lerp(animationData[n5 + 13], animationData[n6 + 13], n4);
            matrix4f.m32 = PZMath.lerp(animationData[n5 + 14], animationData[n6 + 14], n4);
            matrix4f.m33 = PZMath.lerp(animationData[n5 + 15], animationData[n6 + 15], n4);
        }
        else {
            matrix4f.m00 = animationData[n5];
            matrix4f.m01 = animationData[n5 + 1];
            matrix4f.m02 = animationData[n5 + 2];
            matrix4f.m03 = animationData[n5 + 3];
            matrix4f.m10 = animationData[n5 + 4];
            matrix4f.m11 = animationData[n5 + 5];
            matrix4f.m12 = animationData[n5 + 6];
            matrix4f.m13 = animationData[n5 + 7];
            matrix4f.m20 = animationData[n5 + 8];
            matrix4f.m21 = animationData[n5 + 9];
            matrix4f.m22 = animationData[n5 + 10];
            matrix4f.m23 = animationData[n5 + 11];
            matrix4f.m30 = animationData[n5 + 12];
            matrix4f.m31 = animationData[n5 + 13];
            matrix4f.m32 = animationData[n5 + 14];
            matrix4f.m33 = animationData[n5 + 15];
        }
    }
    
    private static class BoneTrack
    {
        private float[] m_animationData;
    }
}
