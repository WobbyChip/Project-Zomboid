// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.math;

import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;
import org.joml.Matrix4f;
import zombie.util.StringUtils;
import org.lwjgl.util.vector.Quaternion;
import zombie.iso.Vector2;
import org.lwjgl.util.vector.Vector3f;

public final class PZMath
{
    public static final float PI = 3.1415927f;
    public static final float PI2 = 6.2831855f;
    public static final float degToRads = 0.017453292f;
    public static final float radToDegs = 57.295776f;
    public static final long microsToNanos = 1000L;
    public static final long millisToMicros = 1000L;
    public static final long secondsToMillis = 1000L;
    public static long secondsToNanos;
    
    public static float almostUnitIdentity(final float n) {
        return n * n * (2.0f - n);
    }
    
    public static float almostIdentity(final float n, final float n2, final float n3) {
        if (n > n2) {
            return n;
        }
        final float n4 = 2.0f * n3 - n2;
        final float n5 = 2.0f * n2 - 3.0f * n3;
        final float n6 = n / n2;
        return (n4 * n6 + n5) * n6 * n6 + n3;
    }
    
    public static float gain(final float n, final float n2) {
        final float n3 = (float)(0.5 * Math.pow(2.0f * ((n < 0.5f) ? n : (1.0f - n)), n2));
        return (n < 0.5f) ? n3 : (1.0f - n3);
    }
    
    public static float clamp(final float n, final float n2, final float n3) {
        float n4 = n;
        if (n4 < n2) {
            n4 = n2;
        }
        if (n4 > n3) {
            n4 = n3;
        }
        return n4;
    }
    
    public static long clamp(final long n, final long n2, final long n3) {
        long n4 = n;
        if (n4 < n2) {
            n4 = n2;
        }
        if (n4 > n3) {
            n4 = n3;
        }
        return n4;
    }
    
    public static int clamp(final int n, final int n2, final int n3) {
        int n4 = n;
        if (n4 < n2) {
            n4 = n2;
        }
        if (n4 > n3) {
            n4 = n3;
        }
        return n4;
    }
    
    public static float clamp_01(final float n) {
        return clamp(n, 0.0f, 1.0f);
    }
    
    public static float lerp(final float n, final float n2, final float n3) {
        return n + (n2 - n) * n3;
    }
    
    public static float lerpAngle(final float n, final float n2, final float n3) {
        return wrap(n + n3 * getClosestAngle(n, n2), -3.1415927f, 3.1415927f);
    }
    
    public static Vector3f lerp(final Vector3f vector3f, final Vector3f vector3f2, final Vector3f vector3f3, final float n) {
        vector3f.set(vector3f2.x + (vector3f3.x - vector3f2.x) * n, vector3f2.y + (vector3f3.y - vector3f2.y) * n, vector3f2.z + (vector3f3.z - vector3f2.z) * n);
        return vector3f;
    }
    
    public static Vector2 lerp(final Vector2 vector2, final Vector2 vector3, final Vector2 vector4, final float n) {
        vector2.set(vector3.x + (vector4.x - vector3.x) * n, vector3.y + (vector4.y - vector3.y) * n);
        return vector2;
    }
    
    public static float c_lerp(final float n, final float n2, final float n3) {
        final float n4 = (float)(1.0 - Math.cos(n3 * 3.1415927f)) / 2.0f;
        return n * (1.0f - n4) + n2 * n4;
    }
    
    public static Quaternion slerp(final Quaternion quaternion, final Quaternion quaternion2, final Quaternion quaternion3, final float n) {
        final double n2 = quaternion2.x * quaternion3.x + quaternion2.y * quaternion3.y + quaternion2.z * quaternion3.z + quaternion2.w * quaternion3.w;
        final double n3 = (n2 < 0.0) ? (-n2) : n2;
        double n4 = 1.0f - n;
        double n5 = n;
        if (1.0 - n3 > 0.1) {
            final double acos = org.joml.Math.acos(n3);
            final double n6 = 1.0 / org.joml.Math.sin(acos);
            n4 = org.joml.Math.sin(acos * (1.0 - n)) * n6;
            n5 = org.joml.Math.sin(acos * n) * n6;
        }
        if (n2 < 0.0) {
            n5 = -n5;
        }
        quaternion.set((float)(n4 * quaternion2.x + n5 * quaternion3.x), (float)(n4 * quaternion2.y + n5 * quaternion3.y), (float)(n4 * quaternion2.z + n5 * quaternion3.z), (float)(n4 * quaternion2.w + n5 * quaternion3.w));
        return quaternion;
    }
    
    public static float sqrt(final float n) {
        return org.joml.Math.sqrt(n);
    }
    
    public static float lerpFunc_EaseOutQuad(final float n) {
        return n * n;
    }
    
    public static float lerpFunc_EaseInQuad(final float n) {
        final float n2 = 1.0f - n;
        return 1.0f - n2 * n2;
    }
    
    public static float lerpFunc_EaseOutInQuad(final float n) {
        if (n < 0.5f) {
            return lerpFunc_EaseOutQuad(n) * 2.0f;
        }
        return 0.5f + lerpFunc_EaseInQuad(2.0f * n - 1.0f) / 2.0f;
    }
    
    public static float tryParseFloat(final String s, final float n) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return n;
        }
        try {
            return Float.parseFloat(s.trim());
        }
        catch (NumberFormatException ex) {
            return n;
        }
    }
    
    public static boolean canParseFloat(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return false;
        }
        try {
            Float.parseFloat(s.trim());
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static int tryParseInt(final String s, final int n) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return n;
        }
        try {
            return Integer.parseInt(s.trim());
        }
        catch (NumberFormatException ex) {
            return n;
        }
    }
    
    public static float degToRad(final float n) {
        return 0.017453292f * n;
    }
    
    public static float radToDeg(final float n) {
        return 57.295776f * n;
    }
    
    public static float getClosestAngle(final float n, final float n2) {
        return wrap(wrap(n2, 6.2831855f) - wrap(n, 6.2831855f), -3.1415927f, 3.1415927f);
    }
    
    public static float getClosestAngleDegrees(final float n, final float n2) {
        return radToDeg(getClosestAngle(degToRad(n), degToRad(n2)));
    }
    
    public static int sign(final float n) {
        return (n > 0.0f) ? 1 : ((n < 0.0f) ? -1 : 0);
    }
    
    public static float floor(final float n) {
        if (n >= 0.0f) {
            return (float)(int)(n + 1.0E-7f);
        }
        return (float)(int)(n - 0.9999999f);
    }
    
    public static float ceil(final float n) {
        if (n >= 0.0f) {
            return (float)(int)(n + 0.9999999f);
        }
        return (float)(int)(n - 1.0E-7f);
    }
    
    public static float frac(final float n) {
        return n - floor(n);
    }
    
    public static float wrap(final float n, final float n2) {
        if (n2 == 0.0f) {
            return 0.0f;
        }
        if (n2 < 0.0f) {
            return 0.0f;
        }
        if (n < 0.0f) {
            return (1.0f - frac(-n / n2)) * n2;
        }
        return frac(n / n2) * n2;
    }
    
    public static float wrap(final float n, final float n2, final float n3) {
        final float max = max(n3, n2);
        final float min = min(n3, n2);
        return min + wrap(n - min, max - min);
    }
    
    public static float max(final float n, final float n2) {
        return (n > n2) ? n : n2;
    }
    
    public static int max(final int n, final int n2) {
        return (n > n2) ? n : n2;
    }
    
    public static float min(final float n, final float n2) {
        return (n > n2) ? n2 : n;
    }
    
    public static int min(final int n, final int n2) {
        return (n > n2) ? n2 : n;
    }
    
    public static float abs(final float n) {
        return n * sign(n);
    }
    
    public static boolean equal(final float n, final float n2) {
        return equal(n, n2, 1.0E-7f);
    }
    
    public static boolean equal(final float n, final float n2, final float n3) {
        return abs(n2 - n) < n3;
    }
    
    public static org.lwjgl.util.vector.Matrix4f convertMatrix(final Matrix4f matrix4f, org.lwjgl.util.vector.Matrix4f matrix4f2) {
        if (matrix4f2 == null) {
            matrix4f2 = new org.lwjgl.util.vector.Matrix4f();
        }
        matrix4f2.m00 = matrix4f.m00();
        matrix4f2.m01 = matrix4f.m01();
        matrix4f2.m02 = matrix4f.m02();
        matrix4f2.m03 = matrix4f.m03();
        matrix4f2.m10 = matrix4f.m10();
        matrix4f2.m11 = matrix4f.m11();
        matrix4f2.m12 = matrix4f.m12();
        matrix4f2.m13 = matrix4f.m13();
        matrix4f2.m20 = matrix4f.m20();
        matrix4f2.m21 = matrix4f.m21();
        matrix4f2.m22 = matrix4f.m22();
        matrix4f2.m23 = matrix4f.m23();
        matrix4f2.m30 = matrix4f.m30();
        matrix4f2.m31 = matrix4f.m31();
        matrix4f2.m32 = matrix4f.m32();
        matrix4f2.m33 = matrix4f.m33();
        return matrix4f2;
    }
    
    public static Matrix4f convertMatrix(final org.lwjgl.util.vector.Matrix4f matrix4f, Matrix4f matrix4f2) {
        if (matrix4f2 == null) {
            matrix4f2 = new Matrix4f();
        }
        return matrix4f2.set(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
    }
    
    public static float step(final float n, final float n2, final float n3) {
        if (n > n2) {
            return max(n + n3, n2);
        }
        if (n < n2) {
            return min(n + n3, n2);
        }
        return n;
    }
    
    public static SideOfLine testSideOfLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float n7 = (n5 - n) * (n4 - n2) - (n6 - n2) * (n3 - n);
        return (n7 > 0.0f) ? SideOfLine.Left : ((n7 < 0.0f) ? SideOfLine.Right : SideOfLine.OnLine);
    }
    
    public static float roundToNearest(final float n) {
        return floor(n + 0.5f * sign(n));
    }
    
    public static int roundToInt(final float n) {
        return (int)(roundToNearest(n) + 1.0E-4f);
    }
    
    public static float roundToIntPlus05(final float n) {
        return floor(n) + 0.5f;
    }
    
    public static float roundFromEdges(final float n) {
        final float n2 = (float)(int)n;
        final float n3 = n - n2;
        if (n3 < 0.2f) {
            return n2 + 0.2f;
        }
        if (n3 > 0.8f) {
            return n2 + 1.0f - 0.2f;
        }
        return n;
    }
    
    static {
        PZMath.secondsToNanos = 1000000000L;
        UnitTests.runAll();
    }
    
    public enum SideOfLine
    {
        Left, 
        OnLine, 
        Right;
        
        private static /* synthetic */ SideOfLine[] $values() {
            return new SideOfLine[] { SideOfLine.Left, SideOfLine.OnLine, SideOfLine.Right };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static final class UnitTests
    {
        private static final Runnable[] s_unitTests;
        
        private static void runAll() {
            PZArrayUtil.forEach(UnitTests.s_unitTests, Runnable::run);
        }
        
        static {
            s_unitTests = new Runnable[0];
        }
        
        private static final class lerpFunctions
        {
            public static void run() {
                DebugLog.General.println("UnitTest_lerpFunctions");
                DebugLog.General.println("x,Sqrt,EaseOutQuad,EaseInQuad,EaseOutInQuad");
                for (int i = 0; i < 100; ++i) {
                    final float f = i / 100.0f;
                    DebugLog.General.println("%f,%f,%f,%f", f, PZMath.lerpFunc_EaseOutQuad(f), PZMath.lerpFunc_EaseInQuad(f), PZMath.lerpFunc_EaseOutInQuad(f));
                }
                DebugLog.General.println("UnitTest_lerpFunctions. Complete");
            }
        }
        
        private static final class getClosestAngle
        {
            public static void run() {
                DebugLog.General.println("runUnitTests_getClosestAngle");
                DebugLog.General.println("a, b, result, expected, pass");
                runUnitTest(0.0f, 0.0f, 0.0f);
                runUnitTest(0.0f, 15.0f, 15.0f);
                runUnitTest(15.0f, 0.0f, -15.0f);
                runUnitTest(0.0f, 179.0f, 179.0f);
                runUnitTest(180.0f, 180.0f, 0.0f);
                runUnitTest(180.0f, 359.0f, 179.0f);
                runUnitTest(90.0f, 180.0f, 90.0f);
                runUnitTest(180.0f, 90.0f, -90.0f);
                for (int i = -360; i < 360; i += 10) {
                    for (int j = -360; j < 360; j += 10) {
                        runUnitTest_noexp((float)i, (float)j);
                    }
                }
                DebugLog.General.println("runUnitTests_getClosestAngle. Complete");
            }
            
            private static void runUnitTest_noexp(final float n, final float n2) {
                logResult(n, n2, PZMath.getClosestAngleDegrees(n, n2), "N/A", "N/A");
            }
            
            private static void runUnitTest(final float n, final float n2, final float f) {
                final float closestAngleDegrees = PZMath.getClosestAngleDegrees(n, n2);
                logResult(n, n2, closestAngleDegrees, String.valueOf(f), PZMath.equal(f, closestAngleDegrees, 1.0E-4f) ? "pass" : "fail");
            }
            
            private static void logResult(final float f, final float f2, final float f3, final String s, final String s2) {
                DebugLog.General.println("%f, %f, %f, %s, %s", f, f2, f3, s, s2);
            }
        }
        
        public static final class vector2
        {
            public static void run() {
                runUnitTest_direction();
            }
            
            private static void runUnitTest_direction() {
                DebugLog.General.println("runUnitTest_direction");
                DebugLog.General.println("x, y, angle, length, rdir.x, rdir.y, rangle, rlength, pass");
                checkDirection(1.0f, 0.0f);
                checkDirection(1.0f, 1.0f);
                checkDirection(0.0f, 1.0f);
                checkDirection(-1.0f, 1.0f);
                checkDirection(-1.0f, 0.0f);
                checkDirection(-1.0f, -1.0f);
                checkDirection(0.0f, -1.0f);
                checkDirection(1.0f, -1.0f);
                DebugLog.General.println("runUnitTest_direction. Complete");
            }
            
            private static void checkDirection(final float f, final float f2) {
                final Vector2 vector2 = new Vector2(f, f2);
                final float direction = vector2.getDirection();
                final float length = vector2.getLength();
                final Vector2 fromLengthDirection = Vector2.fromLengthDirection(length, direction);
                final float direction2 = fromLengthDirection.getDirection();
                final float length2 = fromLengthDirection.getLength();
                DebugLog.General.println("%f, %f, %f, %f, %f, %f, %f, %f, %s", f, f2, direction, length, fromLengthDirection.x, fromLengthDirection.y, direction2, length2, (PZMath.equal(vector2.x, fromLengthDirection.x, 1.0E-4f) && PZMath.equal(vector2.y, fromLengthDirection.y, 1.0E-4f) && PZMath.equal(direction, direction2, 1.0E-4f) && PZMath.equal(length, length2, 1.0E-4f)) ? "true" : "false");
            }
        }
    }
}
