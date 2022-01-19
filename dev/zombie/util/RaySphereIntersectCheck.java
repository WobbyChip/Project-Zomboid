// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import zombie.iso.Vector3;

public class RaySphereIntersectCheck
{
    static Vector3 toSphere;
    static Vector3 dirNormal;
    
    public static boolean intersects(final Vector3 vector3, final Vector3 vector4, final Vector3 vector5, float n) {
        n *= n;
        RaySphereIntersectCheck.dirNormal.x = vector4.x;
        RaySphereIntersectCheck.dirNormal.y = vector4.y;
        RaySphereIntersectCheck.dirNormal.z = vector4.z;
        RaySphereIntersectCheck.dirNormal.normalize();
        RaySphereIntersectCheck.toSphere.x = vector5.x - vector3.x;
        RaySphereIntersectCheck.toSphere.y = vector5.y - vector3.y;
        RaySphereIntersectCheck.toSphere.z = vector5.z - vector3.z;
        final float length = RaySphereIntersectCheck.toSphere.getLength();
        if (length * length < n) {
            return false;
        }
        final float dot3d = RaySphereIntersectCheck.toSphere.dot3d(RaySphereIntersectCheck.dirNormal);
        return dot3d >= 0.0f && n + dot3d * dot3d - RaySphereIntersectCheck.toSphere.getLength() >= 0.0;
    }
    
    static {
        RaySphereIntersectCheck.toSphere = new Vector3();
        RaySphereIntersectCheck.dirNormal = new Vector3();
    }
}
