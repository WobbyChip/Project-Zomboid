// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public final class VehicleGauge extends UIElement
{
    protected int needleX;
    protected int needleY;
    protected float minAngle;
    protected float maxAngle;
    protected float value;
    protected Texture texture;
    protected int needleWidth;
    
    public VehicleGauge(final Texture texture, final int needleX, final int needleY, final float minAngle, final float maxAngle) {
        this.needleWidth = 45;
        this.texture = texture;
        this.needleX = needleX;
        this.needleY = needleY;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.width = (float)texture.getWidth();
        this.height = (float)texture.getHeight();
    }
    
    public void setNeedleWidth(final int needleWidth) {
        this.needleWidth = needleWidth;
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        super.render();
        this.DrawTexture(this.texture, 0.0, 0.0, 1.0);
        final double n = (this.minAngle < this.maxAngle) ? Math.toRadians(this.minAngle + (this.maxAngle - this.minAngle) * this.value) : Math.toRadians(this.maxAngle + (this.maxAngle - this.minAngle) * (1.0f - this.value));
        final double n2 = this.needleX;
        final double n3 = this.needleY;
        final double n4 = this.needleX + this.needleWidth * Math.cos(n);
        final double ceil = Math.ceil(this.needleY + this.needleWidth * Math.sin(n));
        final int intValue = this.getAbsoluteX().intValue();
        final int intValue2 = this.getAbsoluteY().intValue();
        SpriteRenderer.instance.renderline(null, intValue + (int)n2, intValue2 + (int)n3, intValue + (int)n4, intValue2 + (int)ceil, 1.0f, 0.0f, 0.0f, 1.0f);
    }
    
    public void setValue(final float a) {
        this.value = Math.min(a, 1.0f);
    }
    
    public void setTexture(final Texture texture) {
        this.texture = texture;
    }
}
