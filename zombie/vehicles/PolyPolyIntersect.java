// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import org.joml.Vector2f;

public final class PolyPolyIntersect
{
    private static Vector2f tempVector2f_1;
    private static Vector2f tempVector2f_2;
    private static Vector2f tempVector2f_3;
    
    public static boolean intersects(final PolygonalMap2.VehiclePoly vehiclePoly, final PolygonalMap2.VehiclePoly vehiclePoly2) {
        for (int i = 0; i < 2; ++i) {
            final PolygonalMap2.VehiclePoly vehiclePoly3 = (i == 0) ? vehiclePoly : vehiclePoly2;
            for (int j = 0; j < 4; ++j) {
                final int n = (j + 1) % 4;
                final Vector2f point = getPoint(vehiclePoly3, j, PolyPolyIntersect.tempVector2f_1);
                final Vector2f point2 = getPoint(vehiclePoly3, n, PolyPolyIntersect.tempVector2f_2);
                final Vector2f set = PolyPolyIntersect.tempVector2f_3.set(point2.y - point.y, point.x - point2.x);
                double n2 = Double.MAX_VALUE;
                double n3 = Double.NEGATIVE_INFINITY;
                for (int k = 0; k < 4; ++k) {
                    final Vector2f point3 = getPoint(vehiclePoly, k, PolyPolyIntersect.tempVector2f_1);
                    final double n4 = set.x * point3.x + set.y * point3.y;
                    if (n4 < n2) {
                        n2 = n4;
                    }
                    if (n4 > n3) {
                        n3 = n4;
                    }
                }
                double n5 = Double.MAX_VALUE;
                double n6 = Double.NEGATIVE_INFINITY;
                for (int l = 0; l < 4; ++l) {
                    final Vector2f point4 = getPoint(vehiclePoly2, l, PolyPolyIntersect.tempVector2f_1);
                    final double n7 = set.x * point4.x + set.y * point4.y;
                    if (n7 < n5) {
                        n5 = n7;
                    }
                    if (n7 > n6) {
                        n6 = n7;
                    }
                }
                if (n3 < n5 || n6 < n2) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static Vector2f getPoint(final PolygonalMap2.VehiclePoly vehiclePoly, final int n, final Vector2f vector2f) {
        if (n == 0) {
            return vector2f.set(vehiclePoly.x1, vehiclePoly.y1);
        }
        if (n == 1) {
            return vector2f.set(vehiclePoly.x2, vehiclePoly.y2);
        }
        if (n == 2) {
            return vector2f.set(vehiclePoly.x3, vehiclePoly.y3);
        }
        if (n == 3) {
            return vector2f.set(vehiclePoly.x4, vehiclePoly.y4);
        }
        return null;
    }
    
    static {
        PolyPolyIntersect.tempVector2f_1 = new Vector2f();
        PolyPolyIntersect.tempVector2f_2 = new Vector2f();
        PolyPolyIntersect.tempVector2f_3 = new Vector2f();
    }
}
