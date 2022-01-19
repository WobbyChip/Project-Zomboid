// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import zombie.core.skinnedmodel.HelperFunctions;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.math.PZMath;
import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.Quaternion;
import java.util.Arrays;
import zombie.core.PerformanceSettings;
import org.lwjgl.util.vector.Matrix4f;

@Deprecated
public class StaticAnimation
{
    private int framesPerSecond;
    public String name;
    public Matrix4f[][] Matrices;
    private Matrix4f[] RootMotion;
    public AnimationClip Clip;
    private int currentKeyframe;
    private float currentTimeValue;
    private Keyframe[] Pose;
    private Keyframe[] PrevPose;
    private float lastTime;
    
    public StaticAnimation(final AnimationClip clip) {
        this.currentKeyframe = 0;
        this.currentTimeValue = 0.0f;
        this.lastTime = 0.0f;
        this.Clip = clip;
        this.framesPerSecond = PerformanceSettings.BaseStaticAnimFramerate;
        this.Matrices = new Matrix4f[(int)(this.framesPerSecond * this.Clip.Duration)][60];
        this.RootMotion = new Matrix4f[(int)(this.framesPerSecond * this.Clip.Duration)];
        this.Pose = new Keyframe[60];
        this.PrevPose = new Keyframe[60];
        this.Create();
        Arrays.fill(this.Pose, null);
        this.Pose = null;
        Arrays.fill(this.PrevPose, null);
        this.PrevPose = null;
    }
    
    private Keyframe getNextKeyFrame(final int n, final int n2, final Keyframe keyframe) {
        final Keyframe[] keyframes = this.Clip.getKeyframes();
        for (int i = n2; i < keyframes.length; ++i) {
            final Keyframe keyframe2 = keyframes[i];
            if (keyframe2.Bone == n && keyframe2.Time > this.currentTimeValue && keyframe != keyframe2) {
                return keyframe2;
            }
        }
        return null;
    }
    
    public Quaternion getRotation(final Quaternion quaternion, final int n) {
        if (this.PrevPose[n] == null || !PerformanceSettings.InterpolateAnims) {
            quaternion.set((ReadableVector4f)this.Pose[n].Rotation);
            return quaternion;
        }
        float n2 = (this.currentTimeValue - this.PrevPose[n].Time) / (this.Pose[n].Time - this.PrevPose[n].Time);
        if (this.Pose[n].Time - this.PrevPose[n].Time == 0.0f) {
            n2 = 0.0f;
        }
        return PZMath.slerp(quaternion, this.PrevPose[n].Rotation, this.Pose[n].Rotation, n2);
    }
    
    public Vector3f getPosition(final Vector3f vector3f, final int n) {
        if (this.PrevPose[n] == null || !PerformanceSettings.InterpolateAnims) {
            vector3f.set((ReadableVector3f)this.Pose[n].Position);
            return vector3f;
        }
        float n2 = (this.currentTimeValue - this.PrevPose[n].Time) / (this.Pose[n].Time - this.PrevPose[n].Time);
        if (this.Pose[n].Time - this.PrevPose[n].Time == 0.0f) {
            n2 = 0.0f;
        }
        PZMath.lerp(vector3f, this.PrevPose[n].Position, this.Pose[n].Position, n2);
        return vector3f;
    }
    
    public void getPose() {
        final Keyframe[] keyframes = this.Clip.getKeyframes();
        this.currentKeyframe = 0;
        while (this.currentKeyframe < keyframes.length) {
            final Keyframe keyframe = keyframes[this.currentKeyframe];
            if (this.currentKeyframe != keyframes.length - 1 && keyframe.Time <= this.currentTimeValue) {
                if (keyframe.Bone >= 0) {
                    this.Pose[keyframe.Bone] = keyframe;
                }
                this.lastTime = keyframe.Time;
                ++this.currentKeyframe;
            }
            else {
                if (!PerformanceSettings.InterpolateAnims) {
                    break;
                }
                for (int i = 0; i < 60; ++i) {
                    if (this.Pose[i] == null || this.currentTimeValue >= this.Pose[i].Time) {
                        final Keyframe nextKeyFrame = this.getNextKeyFrame(i, this.currentKeyframe, this.Pose[i]);
                        if (nextKeyFrame != null) {
                            this.PrevPose[nextKeyFrame.Bone] = this.Pose[nextKeyFrame.Bone];
                            this.Pose[nextKeyFrame.Bone] = nextKeyFrame;
                        }
                        else {
                            this.PrevPose[i] = null;
                        }
                    }
                }
                break;
            }
        }
    }
    
    public void Create() {
        final float n = (float)this.Matrices.length;
        final double n2 = this.Clip.Duration / (double)n;
        double n3 = 0.0;
        int n4 = 0;
        final Matrix4f matrix4f = new Matrix4f();
        while (n4 < n) {
            this.currentTimeValue = (float)n3;
            this.getPose();
            for (int i = 0; i < 60; ++i) {
                if (this.Pose[i] == null) {
                    this.Matrices[n4][i] = matrix4f;
                }
                else {
                    final Quaternion quaternion = new Quaternion();
                    this.getRotation(quaternion, i);
                    final Vector3f vector3f = new Vector3f();
                    this.getPosition(vector3f, i);
                    this.Matrices[n4][i] = HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion, new Vector3f(1.0f, 1.0f, 1.0f), new Matrix4f());
                }
            }
            n3 += n2;
            ++n4;
        }
    }
    
    public Keyframe interpolate(final List<Keyframe> list, final float n) {
        int i = 0;
        Keyframe keyframe = null;
        while (i < list.size()) {
            final Keyframe keyframe3;
            final Keyframe keyframe2 = keyframe3 = list.get(i);
            if (keyframe3.Time > n && keyframe.Time <= n) {
                final Quaternion rotation = new Quaternion();
                final Vector3f position = new Vector3f();
                final float n2 = (n - keyframe.Time) / (keyframe3.Time - keyframe.Time);
                PZMath.slerp(rotation, keyframe.Rotation, keyframe3.Rotation, n2);
                PZMath.lerp(position, keyframe.Position, keyframe3.Position, n2);
                final Keyframe keyframe4 = new Keyframe();
                keyframe4.Position = position;
                keyframe4.Rotation = rotation;
                keyframe4.Scale = new Vector3f(1.0f, 1.0f, 1.0f);
                keyframe4.Time = keyframe.Time + (keyframe3.Time - keyframe.Time) * n2;
                return keyframe4;
            }
            ++i;
            keyframe = keyframe2;
        }
        return list.get(list.size() - 1);
    }
    
    public void interpolate(final List<Keyframe> list) {
        if (list.isEmpty()) {
            return;
        }
        if (list.get(0).Position.equals((Object)list.get(list.size() - 1).Position)) {
            return;
        }
        final float n = (float)(this.Matrices.length + 1);
        final double n2 = this.Clip.Duration / (double)n;
        double n3 = 0.0;
        final ArrayList<Keyframe> list2 = new ArrayList<Keyframe>();
        for (int n4 = 0; n4 < n - 1.0f; ++n4, n3 += n2) {
            list2.add(this.interpolate(list, (float)n3));
        }
        list.clear();
        list.addAll(list2);
    }
    
    public void doRootMotion(final List<Keyframe> list) {
        final float n = (float)this.Matrices.length;
        if (list.size() <= 3) {
            return;
        }
        for (int n2 = 0; n2 < n && n2 < list.size(); ++n2) {
            final Keyframe keyframe = list.get(n2);
            this.RootMotion[n2] = HelperFunctions.CreateFromQuaternionPositionScale(keyframe.Position, keyframe.Rotation, keyframe.Scale, new Matrix4f());
        }
    }
}
