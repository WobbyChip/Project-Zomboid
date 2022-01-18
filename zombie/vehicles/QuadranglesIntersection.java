// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import org.joml.Vector4f;
import zombie.iso.Vector2;

public final class QuadranglesIntersection
{
    private static final float EPS = 0.001f;
    
    public static boolean IsQuadranglesAreIntersected(final Vector2[] array, final Vector2[] array2) {
        if (array == null || array2 == null || array.length != 4 || array2.length != 4) {
            System.out.println("ERROR: IsQuadranglesAreIntersected");
            return false;
        }
        return lineIntersection(array[0], array[1], array2[0], array2[1]) || lineIntersection(array[0], array[1], array2[1], array2[2]) || lineIntersection(array[0], array[1], array2[2], array2[3]) || lineIntersection(array[0], array[1], array2[3], array2[0]) || lineIntersection(array[1], array[2], array2[0], array2[1]) || lineIntersection(array[1], array[2], array2[1], array2[2]) || lineIntersection(array[1], array[2], array2[2], array2[3]) || lineIntersection(array[1], array[2], array2[3], array2[0]) || lineIntersection(array[2], array[3], array2[0], array2[1]) || lineIntersection(array[2], array[3], array2[1], array2[2]) || lineIntersection(array[2], array[3], array2[2], array2[3]) || lineIntersection(array[2], array[3], array2[3], array2[0]) || lineIntersection(array[3], array[0], array2[0], array2[1]) || lineIntersection(array[3], array[0], array2[1], array2[2]) || lineIntersection(array[3], array[0], array2[2], array2[3]) || lineIntersection(array[3], array[0], array2[3], array2[0]) || (IsPointInTriangle(array[0], array2[0], array2[1], array2[2]) || IsPointInTriangle(array[0], array2[0], array2[2], array2[3])) || (IsPointInTriangle(array2[0], array[0], array[1], array[2]) || IsPointInTriangle(array2[0], array[0], array[2], array[3]));
    }
    
    public static boolean IsPointInTriangle(final Vector2 vector2, final Vector2[] array) {
        return IsPointInTriangle(vector2, array[0], array[1], array[2]) || IsPointInTriangle(vector2, array[0], array[2], array[3]);
    }
    
    public static float det(final float n, final float n2, final float n3, final float n4) {
        return n * n4 - n2 * n3;
    }
    
    private static boolean between(final float n, final float n2, final double n3) {
        return Math.min(n, n2) <= n3 + 0.0010000000474974513 && n3 <= Math.max(n, n2) + 0.001f;
    }
    
    private static boolean intersect_1(final float n, final float n2, final float n3, final float n4) {
        float a;
        float a2;
        if (n > n2) {
            a = n;
            a2 = n2;
        }
        else {
            a2 = n;
            a = n2;
        }
        float b;
        float b2;
        if (n3 > n4) {
            b = n3;
            b2 = n4;
        }
        else {
            b2 = n3;
            b = n4;
        }
        return Math.max(a2, b2) <= Math.min(a, b);
    }
    
    public static boolean lineIntersection(final Vector2 vector2, final Vector2 vector3, final Vector2 vector4, final Vector2 vector5) {
        final float n = vector2.y - vector3.y;
        final float n2 = vector3.x - vector2.x;
        final float n3 = -n * vector2.x - n2 * vector2.y;
        final float n4 = vector4.y - vector5.y;
        final float n5 = vector5.x - vector4.x;
        final float n6 = -n4 * vector4.x - n5 * vector4.y;
        final float det = det(n, n2, n4, n5);
        if (det != 0.0f) {
            final double n7 = -det(n3, n2, n6, n5) * 1.0 / det;
            final double n8 = -det(n, n3, n4, n6) * 1.0 / det;
            return between(vector2.x, vector3.x, n7) && between(vector2.y, vector3.y, n8) && between(vector4.x, vector5.x, n7) && between(vector4.y, vector5.y, n8);
        }
        return det(n, n3, n4, n6) == 0.0f && det(n2, n3, n5, n6) == 0.0f && intersect_1(vector2.x, vector3.x, vector4.x, vector5.x) && intersect_1(vector2.y, vector3.y, vector4.y, vector5.y);
    }
    
    public static boolean IsQuadranglesAreTransposed2(final Vector4f vector4f, final Vector4f vector4f2) {
        return IsPointInQuadrilateral(new Vector2(vector4f.x, vector4f.y), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w) || IsPointInQuadrilateral(new Vector2(vector4f.z, vector4f.y), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w) || IsPointInQuadrilateral(new Vector2(vector4f.x, vector4f.w), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w) || IsPointInQuadrilateral(new Vector2(vector4f.z, vector4f.w), vector4f2.x, vector4f2.z, vector4f2.y, vector4f2.w) || IsPointInQuadrilateral(new Vector2(vector4f2.x, vector4f2.y), vector4f.x, vector4f.z, vector4f.y, vector4f.w) || IsPointInQuadrilateral(new Vector2(vector4f2.z, vector4f2.y), vector4f.x, vector4f.z, vector4f.y, vector4f.w) || IsPointInQuadrilateral(new Vector2(vector4f2.x, vector4f2.w), vector4f.x, vector4f.z, vector4f.y, vector4f.w) || IsPointInQuadrilateral(new Vector2(vector4f2.z, vector4f2.w), vector4f.x, vector4f.z, vector4f.y, vector4f.w);
    }
    
    private static boolean IsPointInQuadrilateral(final Vector2 vector2, final float n, final float n2, final float n3, final float n4) {
        return IsPointInTriangle(vector2, new Vector2(n, n3), new Vector2(n, n4), new Vector2(n2, n4)) || IsPointInTriangle(vector2, new Vector2(n2, n4), new Vector2(n2, n3), new Vector2(n, n3));
    }
    
    private static boolean IsPointInTriangle(final Vector2 vector2, final Vector2 vector3, final Vector2 vector4, final Vector2 vector5) {
        final float n = (vector3.x - vector2.x) * (vector4.y - vector3.y) - (vector4.x - vector3.x) * (vector3.y - vector2.y);
        final float n2 = (vector4.x - vector2.x) * (vector5.y - vector4.y) - (vector5.x - vector4.x) * (vector4.y - vector2.y);
        final float n3 = (vector5.x - vector2.x) * (vector3.y - vector5.y) - (vector3.x - vector5.x) * (vector5.y - vector2.y);
        return (n >= 0.0f && n2 >= 0.0f && n3 >= 0.0f) || (n <= 0.0f && n2 <= 0.0f && n3 <= 0.0f);
    }
}
