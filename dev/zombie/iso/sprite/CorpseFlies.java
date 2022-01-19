// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.GameTime;
import zombie.network.GameServer;
import zombie.core.SpriteRenderer;
import zombie.core.Core;
import zombie.iso.IsoUtils;
import zombie.core.textures.Texture;

public final class CorpseFlies
{
    private static Texture TEXTURE;
    private static final int FRAME_WIDTH = 128;
    private static final int FRAME_HEIGHT = 128;
    private static final int COLUMNS = 8;
    private static final int ROWS = 7;
    private static final int NUM_FRAMES = 56;
    private static float COUNTER;
    private static int FRAME;
    
    public static void render(final int n, final int n2, final int n3) {
        if (CorpseFlies.TEXTURE == null) {
            CorpseFlies.TEXTURE = Texture.getSharedTexture("media/textures/CorpseFlies.png");
        }
        if (CorpseFlies.TEXTURE == null || !CorpseFlies.TEXTURE.isReady()) {
            return;
        }
        final int n4 = (CorpseFlies.FRAME + (n + n2)) % 56;
        final int n5 = n4 % 8;
        final int n6 = n4 / 8;
        final float n7 = n5 * 128 / (float)CorpseFlies.TEXTURE.getWidth();
        final float n8 = n6 * 128 / (float)CorpseFlies.TEXTURE.getHeight();
        final float n9 = (n5 + 1) * 128 / (float)CorpseFlies.TEXTURE.getWidth();
        final float n10 = (n6 + 1) * 128 / (float)CorpseFlies.TEXTURE.getHeight();
        final float n11 = IsoUtils.XToScreen(n + 0.5f, n2 + 0.5f, (float)n3, 0) + IsoSprite.globalOffsetX;
        final float n12 = IsoUtils.YToScreen(n + 0.5f, n2 + 0.5f, (float)n3, 0) + IsoSprite.globalOffsetY;
        final int n14;
        final int n13 = n14 = 64 * Core.TileScale;
        final float n15 = n11 - n13 / 2;
        final float n16 = n12 - (n14 + 16 * Core.TileScale);
        if (Core.bDebug) {}
        SpriteRenderer.instance.render(CorpseFlies.TEXTURE, n15, n16, (float)n13, (float)n14, 1.0f, 1.0f, 1.0f, 1.0f, n7, n8, n9, n8, n9, n10, n7, n10);
    }
    
    public static void update() {
        if (GameServer.bServer) {
            return;
        }
        CorpseFlies.COUNTER += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 1000.0f;
        final float n = 20.0f;
        if (CorpseFlies.COUNTER > 1000.0f / n) {
            CorpseFlies.COUNTER %= 1000.0f / n;
            ++CorpseFlies.FRAME;
            CorpseFlies.FRAME %= 56;
        }
    }
    
    public static void Reset() {
        CorpseFlies.TEXTURE = null;
    }
    
    static {
        CorpseFlies.COUNTER = 0.0f;
        CorpseFlies.FRAME = 0;
    }
}
