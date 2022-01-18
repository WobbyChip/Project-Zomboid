// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.io.IOException;
import zombie.iso.sprite.IsoSpriteManager;
import java.nio.ByteBuffer;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.GameTime;
import zombie.core.Core;
import zombie.iso.sprite.IsoSprite;
import zombie.core.textures.ColorInfo;

public final class IsoWallBloodSplat
{
    private static final ColorInfo info;
    public float worldAge;
    public IsoSprite sprite;
    
    public IsoWallBloodSplat() {
    }
    
    public IsoWallBloodSplat(final float worldAge, final IsoSprite sprite) {
        this.worldAge = worldAge;
        this.sprite = sprite;
    }
    
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.sprite == null) {
            return;
        }
        if (this.sprite.CurrentAnim == null || this.sprite.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final int tileScale = Core.TileScale;
        final int n4 = 32 * tileScale;
        final int n5 = 96 * tileScale;
        if (IsoSprite.globalOffsetX == -1.0f) {
            IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
            IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
        }
        final float xToScreen = IsoUtils.XToScreen(n, n2, n3, 0);
        final float yToScreen = IsoUtils.YToScreen(n, n2, n3, 0);
        final float n6 = xToScreen - n4;
        final float n7 = yToScreen - n5;
        final float n8 = n6 + IsoSprite.globalOffsetX;
        final float n9 = n7 + IsoSprite.globalOffsetY;
        if (n8 >= IsoCamera.frameState.OffscreenWidth || n8 + 64 * tileScale <= 0.0f) {
            return;
        }
        if (n9 >= IsoCamera.frameState.OffscreenHeight || n9 + 128 * tileScale <= 0.0f) {
            return;
        }
        IsoWallBloodSplat.info.r = 0.7f * colorInfo.r;
        IsoWallBloodSplat.info.g = 0.9f * colorInfo.g;
        IsoWallBloodSplat.info.b = 0.9f * colorInfo.b;
        IsoWallBloodSplat.info.a = 0.4f;
        final float n10 = (float)GameTime.getInstance().getWorldAgeHours() - this.worldAge;
        if (n10 >= 0.0f && n10 < 72.0f) {
            final float n11 = 1.0f - n10 / 72.0f;
            final ColorInfo info = IsoWallBloodSplat.info;
            info.r *= 0.2f + n11 * 0.8f;
            final ColorInfo info2 = IsoWallBloodSplat.info;
            info2.g *= 0.2f + n11 * 0.8f;
            final ColorInfo info3 = IsoWallBloodSplat.info;
            info3.b *= 0.2f + n11 * 0.8f;
            final ColorInfo info4 = IsoWallBloodSplat.info;
            info4.a *= 0.25f + n11 * 0.75f;
        }
        else {
            final ColorInfo info5 = IsoWallBloodSplat.info;
            info5.r *= 0.2f;
            final ColorInfo info6 = IsoWallBloodSplat.info;
            info6.g *= 0.2f;
            final ColorInfo info7 = IsoWallBloodSplat.info;
            info7.b *= 0.2f;
            final ColorInfo info8 = IsoWallBloodSplat.info;
            info8.a *= 0.25f;
        }
        IsoWallBloodSplat.info.a = Math.max(IsoWallBloodSplat.info.a, 0.15f);
        this.sprite.CurrentAnim.Frames.get(0).render(n8, n9, IsoDirections.N, IsoWallBloodSplat.info, false, null);
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putFloat(this.worldAge);
        byteBuffer.putInt(this.sprite.ID);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.worldAge = byteBuffer.getFloat();
        this.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
    }
    
    static {
        info = new ColorInfo();
    }
}
