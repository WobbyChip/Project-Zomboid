// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.math.PZMath;
import zombie.core.SpriteRenderer;
import zombie.iso.Vector2;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.textures.Texture;

public final class RadialProgressBar extends UIElement
{
    private static final boolean DEBUG = false;
    Texture radialTexture;
    float deltaValue;
    private static final RadSegment[] segments;
    private final float PIx2 = 6.283185f;
    private final float PiOver2 = 1.570796f;
    
    public RadialProgressBar(final KahluaTable kahluaTable, final Texture radialTexture) {
        super(kahluaTable);
        this.deltaValue = 1.0f;
        this.radialTexture = radialTexture;
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public void render() {
        if (!this.enabled) {
            return;
        }
        if (!this.isVisible()) {
            return;
        }
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return;
        }
        if (this.radialTexture == null) {
            return;
        }
        final float n = (float)(this.x + this.xScroll + this.getAbsoluteX());
        final float n2 = (float)(this.y + this.yScroll + this.getAbsoluteY());
        final float xStart = this.radialTexture.xStart;
        final float yStart = this.radialTexture.yStart;
        final float n3 = this.radialTexture.xEnd - this.radialTexture.xStart;
        final float n4 = this.radialTexture.yEnd - this.radialTexture.yStart;
        final float n5 = n + 0.5f * this.width;
        final float n6 = n2 + 0.5f * this.height;
        final float deltaValue = this.deltaValue;
        final float n7 = deltaValue * 6.283185f - 1.570796f;
        final Vector2 vector2 = new Vector2((float)Math.cos(n7), (float)Math.sin(n7));
        float n8;
        float n9;
        if (Math.abs(this.width / 2.0f / vector2.x) < Math.abs(this.height / 2.0f / vector2.y)) {
            n8 = Math.abs(this.width / 2.0f / vector2.x);
            n9 = Math.abs(0.5f / vector2.x);
        }
        else {
            n8 = Math.abs(this.height / 2.0f / vector2.y);
            n9 = Math.abs(0.5f / vector2.y);
        }
        final float n10 = n5 + vector2.x * n8;
        final float n11 = n6 + vector2.y * n8;
        final float n12 = 0.5f + vector2.x * n9;
        final float n13 = 0.5f + vector2.y * n9;
        int n14 = (int)(deltaValue * 8.0f);
        if (deltaValue <= 0.0f) {
            n14 = -1;
        }
        for (int i = 0; i < RadialProgressBar.segments.length; ++i) {
            final RadSegment radSegment = RadialProgressBar.segments[i];
            if (radSegment != null) {
                if (i <= n14) {
                    if (i != n14) {
                        SpriteRenderer.instance.renderPoly(this.radialTexture, n + radSegment.vertex[0].x * this.width, n2 + radSegment.vertex[0].y * this.height, n + radSegment.vertex[1].x * this.width, n2 + radSegment.vertex[1].y * this.height, n + radSegment.vertex[2].x * this.width, n2 + radSegment.vertex[2].y * this.height, n + radSegment.vertex[2].x * this.width, n2 + radSegment.vertex[2].y * this.height, 1.0f, 1.0f, 1.0f, 1.0f, xStart + radSegment.uv[0].x * n3, yStart + radSegment.uv[0].y * n4, xStart + radSegment.uv[1].x * n3, yStart + radSegment.uv[1].y * n4, xStart + radSegment.uv[2].x * n3, yStart + radSegment.uv[2].y * n4, xStart + radSegment.uv[2].x * n3, yStart + radSegment.uv[2].y * n4);
                    }
                    else {
                        SpriteRenderer.instance.renderPoly(this.radialTexture, n + radSegment.vertex[0].x * this.width, n2 + radSegment.vertex[0].y * this.height, n10, n11, n + radSegment.vertex[2].x * this.width, n2 + radSegment.vertex[2].y * this.height, n + radSegment.vertex[2].x * this.width, n2 + radSegment.vertex[2].y * this.height, 1.0f, 1.0f, 1.0f, 1.0f, xStart + radSegment.uv[0].x * n3, yStart + radSegment.uv[0].y * n4, xStart + n12 * n3, yStart + n13 * n4, xStart + radSegment.uv[2].x * n3, yStart + radSegment.uv[2].y * n4, xStart + radSegment.uv[2].x * n3, yStart + radSegment.uv[2].y * n4);
                    }
                }
            }
        }
    }
    
    public void setValue(final float n) {
        this.deltaValue = PZMath.clamp(n, 0.0f, 1.0f);
    }
    
    public float getValue() {
        return this.deltaValue;
    }
    
    public void setTexture(final Texture radialTexture) {
        this.radialTexture = radialTexture;
    }
    
    public Texture getTexture() {
        return this.radialTexture;
    }
    
    static {
        segments = new RadSegment[8];
        (RadialProgressBar.segments[0] = new RadSegment()).set(0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.5f);
        (RadialProgressBar.segments[1] = new RadSegment()).set(1.0f, 0.0f, 1.0f, 0.5f, 0.5f, 0.5f);
        (RadialProgressBar.segments[2] = new RadSegment()).set(1.0f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f);
        (RadialProgressBar.segments[3] = new RadSegment()).set(1.0f, 1.0f, 0.5f, 1.0f, 0.5f, 0.5f);
        (RadialProgressBar.segments[4] = new RadSegment()).set(0.5f, 1.0f, 0.0f, 1.0f, 0.5f, 0.5f);
        (RadialProgressBar.segments[5] = new RadSegment()).set(0.0f, 1.0f, 0.0f, 0.5f, 0.5f, 0.5f);
        (RadialProgressBar.segments[6] = new RadSegment()).set(0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.5f);
        (RadialProgressBar.segments[7] = new RadSegment()).set(0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f);
    }
    
    private static class RadSegment
    {
        Vector2[] vertex;
        Vector2[] uv;
        
        private RadSegment() {
            this.vertex = new Vector2[3];
            this.uv = new Vector2[3];
        }
        
        private RadSegment set(final int n, final float n2, final float n3, final float n4, final float n5) {
            this.vertex[n] = new Vector2(n2, n3);
            this.uv[n] = new Vector2(n4, n5);
            return this;
        }
        
        private void set(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.vertex[0] = new Vector2(n, n2);
            this.vertex[1] = new Vector2(n3, n4);
            this.vertex[2] = new Vector2(n5, n6);
            this.uv[0] = new Vector2(n, n2);
            this.uv[1] = new Vector2(n3, n4);
            this.uv[2] = new Vector2(n5, n6);
        }
    }
}
