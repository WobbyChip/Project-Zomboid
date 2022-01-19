// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.joml.Vector2fc;
import org.joml.Vector2f;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;

public final class IsoUtils
{
    public static float clamp(final float a, final float b, final float b2) {
        return Math.min(Math.max(a, b), b2);
    }
    
    public static float smoothstep(final float n, final float n2, final float n3) {
        final float clamp = clamp((n3 - n) / (n2 - n), 0.0f, 1.0f);
        return clamp * clamp * (3.0f - 2.0f * clamp);
    }
    
    public static float DistanceTo(final float n, final float n2, final float n3, final float n4) {
        return (float)Math.sqrt(Math.pow(n3 - n, 2.0) + Math.pow(n4 - n2, 2.0));
    }
    
    public static float DistanceTo2D(final float n, final float n2, final float n3, final float n4) {
        return (float)Math.sqrt(Math.pow(n3 - n, 2.0) + Math.pow(n4 - n2, 2.0));
    }
    
    public static float DistanceTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return (float)Math.sqrt(Math.pow(n4 - n, 2.0) + Math.pow(n5 - n2, 2.0) + Math.pow(n6 - n3, 2.0));
    }
    
    public static float DistanceToSquared(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return (float)(Math.pow(n4 - n, 2.0) + Math.pow(n5 - n2, 2.0) + Math.pow(n6 - n3, 2.0));
    }
    
    public static float DistanceToSquared(final float n, final float n2, final float n3, final float n4) {
        return (float)(Math.pow(n3 - n, 2.0) + Math.pow(n4 - n2, 2.0));
    }
    
    public static float DistanceManhatten(final float n, final float n2, final float n3, final float n4) {
        return Math.abs(n3 - n) + Math.abs(n4 - n2);
    }
    
    public static float DistanceManhatten(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return Math.abs(n3 - n) + Math.abs(n4 - n2) + Math.abs(n6 - n5) * 2.0f;
    }
    
    public static float DistanceManhattenSquare(final float n, final float n2, final float n3, final float n4) {
        return Math.max(Math.abs(n3 - n), Math.abs(n4 - n2));
    }
    
    public static float XToIso(final float n, final float n2, final float n3) {
        final float n4 = n + IsoCamera.getOffX();
        final float n5 = n2 + IsoCamera.getOffY();
        final float n6 = (n4 + 2.0f * n5) / (64.0f * Core.TileScale);
        final float n7 = (n4 - 2.0f * n5) / (-64.0f * Core.TileScale);
        return n6 + 3.0f * n3;
    }
    
    public static float XToIsoTrue(final float n, final float n2, final int n3) {
        final float n4 = n + (int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffX;
        final float n5 = n2 + (int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffY;
        final float n6 = (n4 + 2.0f * n5) / (64.0f * Core.TileScale);
        final float n7 = (n4 - 2.0f * n5) / (-64.0f * Core.TileScale);
        final float n8 = n6 + 3 * n3;
        final float n9 = n7 + 3 * n3;
        return n8;
    }
    
    public static float XToScreen(final float n, final float n2, final float n3, final int n4) {
        return 0.0f + n * (32 * Core.TileScale) - n2 * (32 * Core.TileScale);
    }
    
    public static float XToScreenInt(final int n, final int n2, final int n3, final int n4) {
        return XToScreen((float)n, (float)n2, (float)n3, n4);
    }
    
    public static float YToScreenExact(final float n, final float n2, final float n3, final int n4) {
        return YToScreen(n, n2, n3, n4) - IsoCamera.getOffY();
    }
    
    public static float XToScreenExact(final float n, final float n2, final float n3, final int n4) {
        return XToScreen(n, n2, n3, n4) - IsoCamera.getOffX();
    }
    
    public static float YToIso(final float n, final float n2, final float n3) {
        final float n4 = n + IsoCamera.getOffX();
        final float n5 = n2 + IsoCamera.getOffY();
        final float n6 = (n4 + 2.0f * n5) / (64.0f * Core.TileScale);
        return (n4 - 2.0f * n5) / (-64.0f * Core.TileScale) + 3.0f * n3;
    }
    
    public static float YToScreen(final float n, final float n2, final float n3, final int n4) {
        return 0.0f + n2 * (16 * Core.TileScale) + n * (16 * Core.TileScale) + (n4 - n3) * (96 * Core.TileScale);
    }
    
    public static float YToScreenInt(final int n, final int n2, final int n3, final int n4) {
        return YToScreen((float)n, (float)n2, (float)n3, n4);
    }
    
    public static boolean isSimilarDirection(final IsoGameCharacter isoGameCharacter, final float n, final float n2, final float n3, final float n4, final float n5) {
        final Vector2f vector2f = new Vector2f(n - isoGameCharacter.x, n2 - isoGameCharacter.y);
        vector2f.normalize();
        final Vector2f vector2f2 = new Vector2f(isoGameCharacter.x - n3, isoGameCharacter.y - n4);
        vector2f2.normalize();
        vector2f.add((Vector2fc)vector2f2);
        return vector2f.length() < n5;
    }
}
