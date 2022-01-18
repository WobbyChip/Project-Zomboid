// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.iso.objects.ObjectRenderEffects;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoDirections;
import zombie.core.textures.Texture;

public final class IsoDirectionFrame
{
    public final Texture[] directions;
    boolean bDoFlip;
    
    public IsoDirectionFrame(final Texture texture) {
        this.directions = new Texture[8];
        this.bDoFlip = true;
        this.SetAllDirections(texture);
    }
    
    public IsoDirectionFrame() {
        this.directions = new Texture[8];
        this.bDoFlip = true;
    }
    
    public IsoDirectionFrame(final Texture texture, final Texture texture2, final Texture texture3, final Texture texture4, final Texture texture5) {
        this.directions = new Texture[8];
        this.bDoFlip = true;
        this.directions[0] = texture2;
        this.directions[1] = texture;
        this.directions[2] = texture2;
        this.directions[3] = texture3;
        this.directions[4] = texture4;
        this.directions[5] = texture5;
        this.directions[6] = texture4;
        this.directions[7] = texture3;
    }
    
    public IsoDirectionFrame(final Texture texture, final Texture texture2, final Texture texture3, final Texture texture4, final Texture texture5, final Texture texture6, final Texture texture7, final Texture texture8) {
        this.directions = new Texture[8];
        this.bDoFlip = true;
        if (texture5 == null) {}
        this.directions[0] = texture;
        this.directions[1] = texture8;
        this.directions[2] = texture7;
        this.directions[3] = texture6;
        this.directions[4] = texture5;
        this.directions[5] = texture4;
        this.directions[6] = texture3;
        this.directions[7] = texture2;
        this.bDoFlip = false;
    }
    
    public IsoDirectionFrame(final Texture texture, final Texture texture2, final Texture texture3, final Texture texture4) {
        this.directions = new Texture[8];
        this.bDoFlip = true;
        this.directions[0] = texture;
        this.directions[1] = texture;
        this.directions[2] = texture4;
        this.directions[3] = texture4;
        this.directions[4] = texture2;
        this.directions[5] = texture2;
        this.directions[6] = texture3;
        this.directions[7] = texture3;
        this.bDoFlip = false;
    }
    
    public Texture getTexture(final IsoDirections isoDirections) {
        return this.directions[isoDirections.index()];
    }
    
    public void SetAllDirections(final Texture texture) {
        this.directions[0] = texture;
        this.directions[1] = texture;
        this.directions[2] = texture;
        this.directions[3] = texture;
        this.directions[4] = texture;
        this.directions[5] = texture;
        this.directions[6] = texture;
        this.directions[7] = texture;
    }
    
    public void SetDirection(final Texture texture, final IsoDirections isoDirections) {
        this.directions[isoDirections.index()] = texture;
    }
    
    public void render(final float n, final float n2, final IsoDirections isoDirections, final ColorInfo colorInfo, final boolean b, final Consumer<TextureDraw> consumer) {
        final Texture texture = this.directions[isoDirections.index()];
        if (texture == null) {
            return;
        }
        if (b) {
            texture.flip = !texture.flip;
        }
        if (texture == null) {
            return;
        }
        if (!this.bDoFlip) {
            texture.flip = false;
        }
        texture.render(n, n2, (float)texture.getWidth(), (float)texture.getHeight(), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a, consumer);
        texture.flip = false;
    }
    
    void render(final float n, final float n2, final float n3, final float n4, final IsoDirections isoDirections, final ColorInfo colorInfo, final boolean b, final Consumer<TextureDraw> consumer) {
        final Texture texture = this.directions[isoDirections.index()];
        if (texture == null) {
            return;
        }
        if (b) {
            texture.flip = !texture.flip;
        }
        if (!this.bDoFlip) {
            texture.flip = false;
        }
        texture.render(n, n2, n3, n4, colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a, consumer);
        texture.flip = false;
    }
    
    void render(final ObjectRenderEffects objectRenderEffects, final float n, final float n2, final float n3, final float n4, final IsoDirections isoDirections, final ColorInfo colorInfo, final boolean b, final Consumer<TextureDraw> consumer) {
        final Texture texture = this.directions[isoDirections.index()];
        if (texture == null) {
            return;
        }
        if (b) {
            texture.flip = !texture.flip;
        }
        if (!this.bDoFlip) {
            texture.flip = false;
        }
        texture.render(objectRenderEffects, n, n2, n3, n4, colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a, consumer);
        texture.flip = false;
    }
    
    public void renderexplicit(final int n, final int n2, final IsoDirections isoDirections, final float n3) {
        this.renderexplicit(n, n2, isoDirections, n3, null);
    }
    
    public void renderexplicit(final int n, final int n2, final IsoDirections isoDirections, final float n3, final ColorInfo colorInfo) {
        final Texture texture = this.directions[isoDirections.index()];
        if (texture == null) {
            return;
        }
        float n4 = 1.0f;
        float n5 = 1.0f;
        float n6 = 1.0f;
        float n7 = 1.0f;
        if (colorInfo != null) {
            n4 *= colorInfo.a;
            n5 *= colorInfo.r;
            n6 *= colorInfo.g;
            n7 *= colorInfo.b;
        }
        texture.renderstrip(n, n2, (int)(texture.getWidth() * n3), (int)(texture.getHeight() * n3), n5, n6, n7, n4, null);
    }
}
