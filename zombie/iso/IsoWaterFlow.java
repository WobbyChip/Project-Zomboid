// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.iso.SpriteDetails.IsoFlagType;
import org.joml.Vector2f;
import zombie.debug.DebugLog;
import org.joml.Matrix3f;
import org.joml.Vector4f;
import java.util.ArrayList;

public final class IsoWaterFlow
{
    private static final ArrayList<Vector4f> points;
    private static final ArrayList<Matrix3f> zones;
    
    public static void addFlow(final float n, final float n2, float n3, final float n4) {
        int n5 = (360 - (int)n3 - 45) % 360;
        if (n5 < 0) {
            n5 += 360;
        }
        n3 = (float)Math.toRadians(n5);
        IsoWaterFlow.points.add(new Vector4f(n, n2, n3, n4));
    }
    
    public static void addZone(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (n > n3 || n2 > n4 || n5 > 1.0) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, n, n2, n3, n4));
        }
        IsoWaterFlow.zones.add(new Matrix3f(n, n2, n3, n4, n5, n6, 0.0f, 0.0f, 0.0f));
    }
    
    public static int getShore(final int n, final int n2) {
        for (int i = 0; i < IsoWaterFlow.zones.size(); ++i) {
            final Matrix3f matrix3f = IsoWaterFlow.zones.get(i);
            if (matrix3f.m00 <= n && matrix3f.m02 >= n && matrix3f.m01 <= n2 && matrix3f.m10 >= n2) {
                return (int)matrix3f.m11;
            }
        }
        return 1;
    }
    
    public static Vector2f getFlow(final IsoGridSquare isoGridSquare, final int n, final int n2, final Vector2f vector2f) {
        Vector4f vector4f = null;
        float n3 = Float.MAX_VALUE;
        Vector4f vector4f2 = null;
        float n4 = Float.MAX_VALUE;
        Vector4f vector4f3 = null;
        float n5 = Float.MAX_VALUE;
        if (IsoWaterFlow.points.size() == 0) {
            return vector2f.set(0.0f, 0.0f);
        }
        for (int i = 0; i < IsoWaterFlow.points.size(); ++i) {
            final Vector4f vector4f4 = IsoWaterFlow.points.get(i);
            final double n6 = Math.pow(vector4f4.x - (isoGridSquare.x + n), 2.0) + Math.pow(vector4f4.y - (isoGridSquare.y + n2), 2.0);
            if (n6 < n3) {
                n3 = (float)n6;
                vector4f = vector4f4;
            }
        }
        for (int j = 0; j < IsoWaterFlow.points.size(); ++j) {
            final Vector4f vector4f5 = IsoWaterFlow.points.get(j);
            final double n7 = Math.pow(vector4f5.x - (isoGridSquare.x + n), 2.0) + Math.pow(vector4f5.y - (isoGridSquare.y + n2), 2.0);
            if (n7 < n4 && vector4f5 != vector4f) {
                n4 = (float)n7;
                vector4f2 = vector4f5;
            }
        }
        final float max = Math.max((float)Math.sqrt(n3), 0.1f);
        final float max2 = Math.max((float)Math.sqrt(n4), 0.1f);
        float z;
        float w;
        if (max > max2 * 10.0f) {
            z = vector4f.z;
            w = vector4f.w;
        }
        else {
            for (int k = 0; k < IsoWaterFlow.points.size(); ++k) {
                final Vector4f vector4f6 = IsoWaterFlow.points.get(k);
                final double n8 = Math.pow(vector4f6.x - (isoGridSquare.x + n), 2.0) + Math.pow(vector4f6.y - (isoGridSquare.y + n2), 2.0);
                if (n8 < n5 && vector4f6 != vector4f && vector4f6 != vector4f2) {
                    n5 = (float)n8;
                    vector4f3 = vector4f6;
                }
            }
            final float max3 = Math.max((float)Math.sqrt(n5), 0.1f);
            final float n9 = vector4f2.z * (1.0f - max2 / (max2 + max3)) + vector4f3.z * (1.0f - max3 / (max2 + max3));
            final float n10 = vector4f2.w * (1.0f - max2 / (max2 + max3)) + vector4f3.w * (1.0f - max3 / (max2 + max3));
            final float n11 = max2 * (1.0f - max2 / (max2 + max3)) + max3 * (1.0f - max3 / (max2 + max3));
            z = vector4f.z * (1.0f - max / (max + n11)) + n9 * (1.0f - n11 / (max + n11));
            w = vector4f.w * (1.0f - max / (max + n11)) + n10 * (1.0f - n11 / (max + n11));
        }
        float n12 = 1.0f;
        final IsoCell cell = isoGridSquare.getCell();
        for (int l = -5; l < 5; ++l) {
            for (int n13 = -5; n13 < 5; ++n13) {
                final IsoGridSquare gridSquare = cell.getGridSquare(isoGridSquare.x + n + l, isoGridSquare.y + n2 + n13, 0);
                if (gridSquare == null || !gridSquare.getProperties().Is(IsoFlagType.water)) {
                    n12 = (float)Math.min(n12, Math.max(0.0, Math.sqrt(l * l + n13 * n13)) / 4.0);
                }
            }
        }
        return vector2f.set(z, w * n12);
    }
    
    public static void Reset() {
        IsoWaterFlow.points.clear();
        IsoWaterFlow.zones.clear();
    }
    
    static {
        points = new ArrayList<Vector4f>();
        zones = new ArrayList<Matrix3f>();
    }
}
