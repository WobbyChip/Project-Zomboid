// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;

public class DiamondShaper implements Consumer<TextureDraw>
{
    public static final DiamondShaper instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.MeshCutdown.getValue()) {
            return;
        }
        final float x0 = textureDraw.x0;
        final float y0 = textureDraw.y0;
        final float x2 = textureDraw.x1;
        final float y2 = textureDraw.y1;
        final float y3 = textureDraw.y2;
        final float y4 = textureDraw.y3;
        final float n = x2 - x0;
        final float n2 = y3 - y2;
        final float n3 = x0 + n * 0.5f;
        final float n4 = y2 + n2 * 0.5f;
        final float u0 = textureDraw.u0;
        final float v0 = textureDraw.v0;
        final float u2 = textureDraw.u1;
        final float v2 = textureDraw.v1;
        final float v3 = textureDraw.v2;
        final float v4 = textureDraw.v3;
        final float n5 = u2 - u0;
        final float n6 = v3 - v0;
        final float n7 = u0 + n5 * 0.5f;
        final float n8 = v2 + n6 * 0.5f;
        textureDraw.x0 = n3;
        textureDraw.y0 = y0;
        textureDraw.u0 = n7;
        textureDraw.v0 = v0;
        textureDraw.x1 = x2;
        textureDraw.y1 = n4;
        textureDraw.u1 = u2;
        textureDraw.v1 = n8;
        textureDraw.x2 = n3;
        textureDraw.y2 = y4;
        textureDraw.u2 = n7;
        textureDraw.v2 = v4;
        textureDraw.x3 = x0;
        textureDraw.y3 = n4;
        textureDraw.u3 = u0;
        textureDraw.v3 = n8;
    }
    
    static {
        instance = new DiamondShaper();
    }
}
