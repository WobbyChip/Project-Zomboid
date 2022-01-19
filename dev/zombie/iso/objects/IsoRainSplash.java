// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSprite;
import zombie.characters.IsoPlayer;
import zombie.GameTime;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoMovingObject;
import zombie.core.Core;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.IsoUtils;
import zombie.core.Rand;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;

public class IsoRainSplash extends IsoObject
{
    public int Age;
    
    @Override
    public boolean Serialize() {
        return false;
    }
    
    public IsoRainSplash(final IsoCell isoCell, final IsoGridSquare square) {
        if (square == null) {
            return;
        }
        if (square.getProperties().Is(IsoFlagType.HasRainSplashes)) {
            return;
        }
        this.Age = 0;
        this.square = square;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        final int n = 1 + Rand.Next(2);
        final int n2 = 16;
        final int n3 = 8;
        for (int i = 0; i < n; ++i) {
            final float next = Rand.Next(0.1f, 0.9f);
            final float next2 = Rand.Next(0.1f, 0.9f);
            this.AttachAnim("RainSplash", "00", 4, RainManager.RainSplashAnimDelay, -(short)(IsoUtils.XToScreen(next, next2, 0.0f, 0) - n2 / 2), -(short)(IsoUtils.YToScreen(next, next2, 0.0f, 0) - n3 / 2), true, 0, false, 0.7f, RainManager.RainSplashTintMod);
            this.AttachedAnimSprite.get(i).Frame = (short)Rand.Next(4);
            this.AttachedAnimSprite.get(i).setScale((float)Core.TileScale, (float)Core.TileScale);
        }
        square.getProperties().Set(IsoFlagType.HasRainSplashes);
        RainManager.AddRainSplash(this);
    }
    
    @Override
    public String getObjectName() {
        return "RainSplashes";
    }
    
    @Override
    public boolean HasTooltip() {
        return false;
    }
    
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare) {
        return this.square == isoGridSquare;
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return VisionResult.NoEffect;
    }
    
    public void ChangeTintMod(final ColorInfo colorInfo) {
        if (this.AttachedAnimSprite != null) {
            for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {}
        }
    }
    
    @Override
    public void update() {
        final float n = 0.0f;
        this.sy = n;
        this.sx = n;
        ++this.Age;
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
            final IsoSprite parentSprite = isoSpriteInstance.parentSprite;
            isoSpriteInstance.update();
            final IsoSpriteInstance isoSpriteInstance2 = isoSpriteInstance;
            isoSpriteInstance2.Frame += isoSpriteInstance.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f);
            if ((int)isoSpriteInstance.Frame >= parentSprite.CurrentAnim.Frames.size() && parentSprite.Loop && isoSpriteInstance.Looped) {
                isoSpriteInstance.Frame = 0.0f;
            }
        }
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                this.setAlphaAndTarget(j, 0.25f);
            }
            else {
                this.setAlphaAndTarget(j, 0.6f);
            }
        }
    }
    
    void Reset(final IsoGridSquare square) {
        if (square == null) {
            return;
        }
        if (square.getProperties().Is(IsoFlagType.HasRainSplashes)) {
            return;
        }
        this.Age = 0;
        this.square = square;
        final int n = 1 + Rand.Next(2);
        if (this.AttachedAnimSprite != null) {
            for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {}
        }
        square.getProperties().Set(IsoFlagType.HasRainSplashes);
        RainManager.AddRainSplash(this);
    }
}
