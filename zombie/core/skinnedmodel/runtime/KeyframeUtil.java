// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.runtime;

import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.animation.Keyframe;
import org.lwjgl.util.vector.Quaternion;

public final class KeyframeUtil
{
    static final Quaternion end;
    
    public static Vector3f GetKeyFramePosition(final Keyframe[] array, final float n, final double n2) {
        final Vector3f vector3f = new Vector3f();
        if (array.length == 0) {
            return vector3f;
        }
        int n3;
        for (n3 = 0; n3 < array.length - 1 && n >= array[n3 + 1].Time; ++n3) {}
        final int n4 = (n3 + 1) % array.length;
        final Keyframe keyframe = array[n3];
        final Keyframe keyframe2 = array[n4];
        final float time = keyframe.Time;
        final float time2 = keyframe2.Time;
        float n5 = time2 - time;
        if (n5 < 0.0f) {
            n5 += (float)n2;
        }
        if (n5 > 0.0f) {
            final float n6 = (n - time) / (time2 - time);
            final float x = keyframe.Position.x;
            final float n7 = x + n6 * (keyframe2.Position.x - x);
            final float y = keyframe.Position.y;
            final float n8 = y + n6 * (keyframe2.Position.y - y);
            final float z = keyframe.Position.z;
            vector3f.set(n7, n8, z + n6 * (keyframe2.Position.z - z));
        }
        else {
            vector3f.set((ReadableVector3f)keyframe.Position);
        }
        return vector3f;
    }
    
    public static Quaternion GetKeyFrameRotation(final Keyframe[] array, final float n, final double n2) {
        final Quaternion quaternion = new Quaternion();
        if (array.length == 0) {
            return quaternion;
        }
        int n3;
        for (n3 = 0; n3 < array.length - 1 && n >= array[n3 + 1].Time; ++n3) {}
        final int n4 = (n3 + 1) % array.length;
        final Keyframe keyframe = array[n3];
        final Keyframe keyframe2 = array[n4];
        final float time = keyframe.Time;
        float n5 = keyframe2.Time - time;
        if (n5 < 0.0f) {
            n5 += (float)n2;
        }
        if (n5 > 0.0f) {
            final float n6 = (n - time) / n5;
            final Quaternion rotation = keyframe.Rotation;
            final Quaternion rotation2 = keyframe2.Rotation;
            double a = rotation.getX() * rotation2.getX() + rotation.getY() * rotation2.getY() + rotation.getZ() * rotation2.getZ() + rotation.getW() * rotation2.getW();
            KeyframeUtil.end.set((ReadableVector4f)rotation2);
            if (a < 0.0) {
                a *= -1.0;
                KeyframeUtil.end.setX(-KeyframeUtil.end.getX());
                KeyframeUtil.end.setY(-KeyframeUtil.end.getY());
                KeyframeUtil.end.setZ(-KeyframeUtil.end.getZ());
                KeyframeUtil.end.setW(-KeyframeUtil.end.getW());
            }
            double n7;
            double n8;
            if (1.0 - a > 1.0E-4) {
                final double acos = Math.acos(a);
                final double sin = Math.sin(acos);
                n7 = Math.sin((1.0 - n6) * acos) / sin;
                n8 = Math.sin(n6 * acos) / sin;
            }
            else {
                n7 = 1.0 - n6;
                n8 = n6;
            }
            quaternion.set((float)(n7 * rotation.getX() + n8 * KeyframeUtil.end.getX()), (float)(n7 * rotation.getY() + n8 * KeyframeUtil.end.getY()), (float)(n7 * rotation.getZ() + n8 * KeyframeUtil.end.getZ()), (float)(n7 * rotation.getW() + n8 * KeyframeUtil.end.getW()));
        }
        else {
            quaternion.set((ReadableVector4f)keyframe.Rotation);
        }
        return quaternion;
    }
    
    static {
        end = new Quaternion();
    }
}
