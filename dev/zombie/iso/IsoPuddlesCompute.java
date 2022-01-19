// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.joml.Vector4f;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import java.util.List;
import org.joml.Vector3fc;
import java.util.ArrayList;
import zombie.popman.ObjectPool;
import org.joml.Vector3f;
import org.joml.Vector2f;

public final class IsoPuddlesCompute
{
    private static final float Pi = 3.1415f;
    private static float puddlesDirNE;
    private static float puddlesDirNW;
    private static float puddlesDirAll;
    private static float puddlesDirNone;
    private static float puddlesSize;
    private static boolean hd_quality;
    private static final Vector2f add;
    private static final Vector3f add_xyy;
    private static final Vector3f add_xxy;
    private static final Vector3f add_xxx;
    private static final Vector3f add_xyx;
    private static final Vector3f add_yxy;
    private static final Vector3f add_yyx;
    private static final Vector3f add_yxx;
    private static final Vector3f HashVector31;
    private static final Vector3f HashVector32;
    private static final ObjectPool<Vector3f> pool_vector3f;
    private static final ArrayList<Vector3f> allocated_vector3f;
    private static final Vector2f temp_vector2f;
    
    private static Vector3f allocVector3f(final float n, final float n2, final float n3) {
        final Vector3f set = IsoPuddlesCompute.pool_vector3f.alloc().set(n, n2, n3);
        IsoPuddlesCompute.allocated_vector3f.add(set);
        return set;
    }
    
    private static Vector3f allocVector3f(final Vector3f vector3f) {
        return allocVector3f(vector3f.x, vector3f.y, vector3f.z);
    }
    
    private static Vector3f floor(final Vector3f vector3f) {
        return allocVector3f((float)Math.floor(vector3f.x), (float)Math.floor(vector3f.y), (float)Math.floor(vector3f.z));
    }
    
    private static Vector3f fract(final Vector3f vector3f) {
        return allocVector3f(fract(vector3f.x), fract(vector3f.y), fract(vector3f.z));
    }
    
    private static float fract(final float n) {
        return (float)(n - Math.floor(n));
    }
    
    private static float mix(final float n, final float n2, final float n3) {
        return n * (1.0f - n3) + n2 * n3;
    }
    
    private static float FuncHash(final Vector3f vector3f) {
        final Vector3f allocVector3f = allocVector3f(vector3f.dot((Vector3fc)IsoPuddlesCompute.HashVector31), vector3f.dot((Vector3fc)IsoPuddlesCompute.HashVector32), 0.0f);
        return fract((float)(Math.sin(allocVector3f.x * 2.1 + 1.1) + Math.sin(allocVector3f.y * 2.5 + 1.5)));
    }
    
    private static float FuncNoise(final Vector3f vector3f) {
        final Vector3f floor = floor(vector3f);
        final Vector3f fract = fract(vector3f);
        final Vector3f allocVector3f = allocVector3f(fract.x * fract.x * (4.5f - 3.5f * fract.x), fract.y * fract.y * (4.5f - 3.5f * fract.y), fract.z * fract.z * (4.5f - 3.5f * fract.z));
        return mix(mix(mix(FuncHash(floor), FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_xyy)), allocVector3f.x), mix(FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_yxy)), FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_xxy)), allocVector3f.x), allocVector3f.y), mix(mix(FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_yyx)), FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_xyx)), allocVector3f.x), mix(FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_yxx)), FuncHash(allocVector3f(floor).add((Vector3fc)IsoPuddlesCompute.add_xxx)), allocVector3f.x), allocVector3f.y), allocVector3f.z);
    }
    
    private static float PerlinNoise(final Vector3f vector3f) {
        if (IsoPuddlesCompute.hd_quality) {
            vector3f.mul(0.5f);
            final float n = 0.5f * FuncNoise(vector3f);
            vector3f.mul(3.0f);
            final float n2 = (float)(n + 0.25 * FuncNoise(vector3f));
            vector3f.mul(3.0f);
            return (float)((float)(n2 + 0.125 * FuncNoise(vector3f)) * Math.min(1.0, 2.0 * FuncNoise(allocVector3f(vector3f).mul(0.02f)) * Math.min(1.0, 1.0 * FuncNoise(allocVector3f(vector3f).mul(0.1f)))));
        }
        return FuncNoise(vector3f) * 0.5f;
    }
    
    private static float getPuddles(final Vector2f vector2f) {
        final float puddlesDirNE = IsoPuddlesCompute.puddlesDirNE;
        final float puddlesDirNW = IsoPuddlesCompute.puddlesDirNW;
        final float puddlesDirAll = IsoPuddlesCompute.puddlesDirAll;
        vector2f.mul(10.0f);
        return Math.min(0.7f, (float)((float)((float)(1.02f * IsoPuddlesCompute.puddlesSize + puddlesDirNE * Math.sin((vector2f.x * 1.0 + vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * Math.cos((vector2f.x * 1.0 + vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * 2.0) + puddlesDirNW * Math.sin((vector2f.x * 1.0 - vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * Math.cos((vector2f.x * 1.0 - vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * 2.0) + puddlesDirAll * 0.3) * PerlinNoise(allocVector3f(vector2f.x * 1.0f, 0.0f, vector2f.y * 2.0f))) + Math.min(0.7f, PerlinNoise(allocVector3f(vector2f.x * 0.7f, 1.0f, vector2f.y * 0.7f)));
    }
    
    public static float computePuddle(final IsoGridSquare isoGridSquare) {
        IsoPuddlesCompute.pool_vector3f.release(IsoPuddlesCompute.allocated_vector3f);
        IsoPuddlesCompute.allocated_vector3f.clear();
        IsoPuddlesCompute.hd_quality = (PerformanceSettings.PuddlesQuality == 0);
        if (!Core.getInstance().getUseShaders()) {
            return -0.1f;
        }
        if (Core.getInstance().getPerfPuddlesOnLoad() == 3 || Core.getInstance().getPerfPuddles() == 3) {
            return -0.1f;
        }
        if (Core.getInstance().getPerfPuddles() > 0 && isoGridSquare.z > 0) {
            return -0.1f;
        }
        final IsoPuddles instance = IsoPuddles.getInstance();
        IsoPuddlesCompute.puddlesSize = instance.getPuddlesSize();
        if (IsoPuddlesCompute.puddlesSize <= 0.0f) {
            return -0.1f;
        }
        final Vector4f shaderOffsetMain;
        final Vector4f vector4f = shaderOffsetMain = instance.getShaderOffsetMain();
        shaderOffsetMain.x -= 90000.0f;
        final Vector4f vector4f2 = vector4f;
        vector4f2.y -= 640000.0f;
        final int n = (int)IsoCamera.frameState.OffX;
        final int n2 = (int)IsoCamera.frameState.OffY;
        final float n3 = IsoUtils.XToScreen(isoGridSquare.x + 0.5f - isoGridSquare.z * 3.0f, isoGridSquare.y + 0.5f - isoGridSquare.z * 3.0f, 0.0f, 0) - n;
        final float n4 = IsoUtils.YToScreen(isoGridSquare.x + 0.5f - isoGridSquare.z * 3.0f, isoGridSquare.y + 0.5f - isoGridSquare.z * 3.0f, 0.0f, 0) - n2;
        final float n5 = n3 / IsoCamera.frameState.OffscreenWidth;
        final float n6 = n4 / IsoCamera.frameState.OffscreenHeight;
        if (Core.getInstance().getPerfPuddles() <= 1) {
            isoGridSquare.getPuddles().recalcIfNeeded();
            IsoPuddlesCompute.puddlesDirNE = (isoGridSquare.getPuddles().pdne[0] + isoGridSquare.getPuddles().pdne[2]) * 0.5f;
            IsoPuddlesCompute.puddlesDirNW = (isoGridSquare.getPuddles().pdnw[0] + isoGridSquare.getPuddles().pdnw[2]) * 0.5f;
            IsoPuddlesCompute.puddlesDirAll = (isoGridSquare.getPuddles().pda[0] + isoGridSquare.getPuddles().pda[2]) * 0.5f;
            IsoPuddlesCompute.puddlesDirNone = (isoGridSquare.getPuddles().pnon[0] + isoGridSquare.getPuddles().pnon[2]) * 0.5f;
        }
        else {
            IsoPuddlesCompute.puddlesDirNE = 0.0f;
            IsoPuddlesCompute.puddlesDirNW = 0.0f;
            IsoPuddlesCompute.puddlesDirAll = 1.0f;
            IsoPuddlesCompute.puddlesDirNone = 0.0f;
        }
        final float n7 = (float)Math.pow(getPuddles(IsoPuddlesCompute.temp_vector2f.set((n5 * vector4f.z + vector4f.x) * 8.0E-4f + isoGridSquare.z * 7.0f, (n6 * vector4f.w + vector4f.y) * 8.0E-4f + isoGridSquare.z * 7.0f)), 2.0);
        return ((float)Math.min(Math.pow(n7, 0.3), 1.0) + n7) * IsoPuddlesCompute.puddlesSize - 0.34f;
    }
    
    static {
        IsoPuddlesCompute.hd_quality = true;
        add = new Vector2f(1.0f, 0.0f);
        add_xyy = new Vector3f(1.0f, 0.0f, 0.0f);
        add_xxy = new Vector3f(1.0f, 1.0f, 0.0f);
        add_xxx = new Vector3f(1.0f, 1.0f, 1.0f);
        add_xyx = new Vector3f(1.0f, 0.0f, 1.0f);
        add_yxy = new Vector3f(0.0f, 1.0f, 0.0f);
        add_yyx = new Vector3f(0.0f, 0.0f, 1.0f);
        add_yxx = new Vector3f(0.0f, 1.0f, 1.0f);
        HashVector31 = new Vector3f(17.1f, 31.7f, 32.6f);
        HashVector32 = new Vector3f(29.5f, 13.3f, 42.6f);
        pool_vector3f = new ObjectPool<Vector3f>(Vector3f::new);
        allocated_vector3f = new ArrayList<Vector3f>();
        temp_vector2f = new Vector2f();
    }
}
