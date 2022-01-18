// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSprite;
import zombie.characters.IsoPlayer;
import zombie.GameTime;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoMovingObject;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.IsoUtils;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;

public class IsoRaindrop extends IsoObject
{
    public int AnimSpriteIndex;
    public float GravMod;
    public int Life;
    public float SplashY;
    public float OffsetY;
    public float Vel_Y;
    
    @Override
    public boolean Serialize() {
        return false;
    }
    
    public IsoRaindrop(final IsoCell isoCell, final IsoGridSquare square, final boolean b) {
        if (!b) {
            return;
        }
        if (square == null) {
            return;
        }
        if (square.getProperties().Is(IsoFlagType.HasRaindrop)) {
            return;
        }
        this.Life = 0;
        this.square = square;
        final int n = 1 * Core.TileScale;
        final int n2 = 64 * Core.TileScale;
        final float next = Rand.Next(0.1f, 0.9f);
        final float next2 = Rand.Next(0.1f, 0.9f);
        final short n3 = (short)(IsoUtils.XToScreen(next, next2, 0.0f, 0) - n / 2);
        final short n4 = (short)(IsoUtils.YToScreen(next, next2, 0.0f, 0) - n2);
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.OffsetY = RainManager.RaindropStartDistance;
        this.SplashY = n4;
        this.AttachAnim("Rain", "00", 1, 0.0f, -n3, -n4, true, 0, false, 0.7f, RainManager.RaindropTintMod);
        if (this.AttachedAnimSprite != null) {
            this.AnimSpriteIndex = this.AttachedAnimSprite.size() - 1;
        }
        else {
            this.AnimSpriteIndex = 0;
        }
        this.AttachedAnimSprite.get(this.AnimSpriteIndex).setScale((float)Core.TileScale, (float)Core.TileScale);
        square.getProperties().Set(IsoFlagType.HasRaindrop);
        this.Vel_Y = 0.0f;
        this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * (1000000.0f / Rand.Next(1000000) + 1.0E-5f));
        RainManager.AddRaindrop(this);
    }
    
    @Override
    public boolean HasTooltip() {
        return false;
    }
    
    @Override
    public String getObjectName() {
        return "RainDrops";
    }
    
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare) {
        return this.square == isoGridSquare;
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return VisionResult.NoEffect;
    }
    
    public void ChangeTintMod(final ColorInfo colorInfo) {
    }
    
    @Override
    public void update() {
        final float n = 0.0f;
        this.sy = n;
        this.sx = n;
        ++this.Life;
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
            isoSpriteInstance.update();
            final IsoSpriteInstance isoSpriteInstance2 = isoSpriteInstance;
            isoSpriteInstance2.Frame += isoSpriteInstance.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f);
            final IsoSprite parentSprite = isoSpriteInstance.parentSprite;
            if ((int)isoSpriteInstance.Frame >= parentSprite.CurrentAnim.Frames.size() && parentSprite.Loop && isoSpriteInstance.Looped) {
                isoSpriteInstance.Frame = 0.0f;
            }
        }
        this.Vel_Y += this.GravMod * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f);
        this.OffsetY += this.Vel_Y;
        if (this.AttachedAnimSprite != null && this.AttachedAnimSprite.size() > this.AnimSpriteIndex && this.AnimSpriteIndex >= 0) {
            this.AttachedAnimSprite.get(this.AnimSpriteIndex).parentSprite.soffY = (short)(this.SplashY + (int)this.OffsetY);
        }
        if (this.OffsetY < 0.0f) {
            this.OffsetY = RainManager.RaindropStartDistance;
            this.Vel_Y = 0.0f;
            this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * (1000000.0f / Rand.Next(1000000) + 1.0E-5f));
        }
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                this.setAlphaAndTarget(j, 0.55f);
            }
            else {
                this.setAlphaAndTarget(j, 1.0f);
            }
        }
    }
    
    void Reset(final IsoGridSquare square, final boolean b) {
        if (!b) {
            return;
        }
        if (square == null) {
            return;
        }
        if (square.getProperties().Is(IsoFlagType.HasRaindrop)) {
            return;
        }
        this.Life = 0;
        this.square = square;
        this.OffsetY = RainManager.RaindropStartDistance;
        if (this.AttachedAnimSprite != null) {
            this.AnimSpriteIndex = this.AttachedAnimSprite.size() - 1;
        }
        else {
            this.AnimSpriteIndex = 0;
        }
        square.getProperties().Set(IsoFlagType.HasRaindrop);
        this.Vel_Y = 0.0f;
        this.GravMod = -(RainManager.GravModMin + (RainManager.GravModMax - RainManager.GravModMin) * (1000000.0f / Rand.Next(1000000) + 1.0E-5f));
        RainManager.AddRaindrop(this);
    }
}
