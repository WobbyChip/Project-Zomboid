// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.core.math.PZMath;
import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Quaternion;

public final class Keyframe
{
    public Quaternion Rotation;
    public Vector3f Position;
    public Vector3f Scale;
    public int Bone;
    public String BoneName;
    public float Time;
    
    public Keyframe() {
        this.Scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.Time = -1.0f;
    }
    
    public Keyframe(final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        this.Scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.Time = -1.0f;
        this.Position = new Vector3f((ReadableVector3f)vector3f);
        this.Rotation = new Quaternion((ReadableVector4f)quaternion);
        this.Scale = new Vector3f((ReadableVector3f)vector3f2);
    }
    
    public void set(final Keyframe keyframe) {
        if (keyframe.Position != null) {
            this.setPosition(keyframe.Position);
        }
        if (keyframe.Rotation != null) {
            this.setRotation(keyframe.Rotation);
        }
        if (keyframe.Scale != null) {
            this.setScale(keyframe.Scale);
        }
        this.Time = keyframe.Time;
        this.Bone = keyframe.Bone;
        this.BoneName = keyframe.BoneName;
    }
    
    public void get(final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        setIfNotNull(vector3f, this.Position, 0.0f, 0.0f, 0.0f);
        setIfNotNull(quaternion, this.Rotation);
        setIfNotNull(vector3f2, this.Scale, 1.0f, 1.0f, 1.0f);
    }
    
    private void setScale(final Vector3f vector3f) {
        if (this.Scale == null) {
            this.Scale = new Vector3f();
        }
        this.Scale.set((ReadableVector3f)vector3f);
    }
    
    private void setRotation(final Quaternion quaternion) {
        if (this.Rotation == null) {
            this.Rotation = new Quaternion();
        }
        this.Rotation.set((ReadableVector4f)quaternion);
    }
    
    private void setPosition(final Vector3f vector3f) {
        if (this.Position == null) {
            this.Position = new Vector3f();
        }
        this.Position.set((ReadableVector3f)vector3f);
    }
    
    public void clear() {
        this.Time = -1.0f;
        this.Position = null;
        this.Rotation = null;
    }
    
    public void setIdentity() {
        setIdentity(this.Position, this.Rotation, this.Scale);
    }
    
    public static void setIdentity(final Vector3f vector3f, final Quaternion identityIfNotNull, final Vector3f vector3f2) {
        setIfNotNull(vector3f, 0.0f, 0.0f, 0.0f);
        setIdentityIfNotNull(identityIfNotNull);
        setIfNotNull(vector3f2, 1.0f, 1.0f, 1.0f);
    }
    
    public static Keyframe lerp(final Keyframe keyframe, final Keyframe keyframe2, final float time, final Keyframe keyframe3) {
        lerp(keyframe, keyframe2, time, keyframe3.Position, keyframe3.Rotation, keyframe3.Scale);
        keyframe3.Bone = keyframe2.Bone;
        keyframe3.BoneName = keyframe2.BoneName;
        keyframe3.Time = time;
        return keyframe3;
    }
    
    public static void setIfNotNull(final Vector3f vector3f, final Vector3f vector3f2, final float n, final float n2, final float n3) {
        if (vector3f != null) {
            if (vector3f2 != null) {
                vector3f.set((ReadableVector3f)vector3f2);
            }
            else {
                vector3f.set(n, n2, n3);
            }
        }
    }
    
    public static void setIfNotNull(final Vector3f vector3f, final float n, final float n2, final float n3) {
        if (vector3f != null) {
            vector3f.set(n, n2, n3);
        }
    }
    
    public static void setIfNotNull(final Quaternion quaternion, final Quaternion quaternion2) {
        if (quaternion != null) {
            if (quaternion2 != null) {
                quaternion.set((ReadableVector4f)quaternion2);
            }
            else {
                quaternion.setIdentity();
            }
        }
    }
    
    public static void setIdentityIfNotNull(final Quaternion quaternion) {
        if (quaternion != null) {
            quaternion.setIdentity();
        }
    }
    
    public static void lerp(final Keyframe keyframe, final Keyframe keyframe2, final float n, final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        if (keyframe2.Time == keyframe.Time) {
            keyframe2.get(vector3f, quaternion, vector3f2);
            return;
        }
        final float n2 = (n - keyframe.Time) / (keyframe2.Time - keyframe.Time);
        if (vector3f != null) {
            PZMath.lerp(vector3f, keyframe.Position, keyframe2.Position, n2);
        }
        if (quaternion != null) {
            PZMath.slerp(quaternion, keyframe.Rotation, keyframe2.Rotation, n2);
        }
        if (vector3f2 != null) {
            PZMath.lerp(vector3f2, keyframe.Scale, keyframe2.Scale, n2);
        }
    }
}
