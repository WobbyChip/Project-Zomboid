// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.sprite;

import zombie.core.skinnedmodel.ModelManager;
import zombie.core.Color;
import zombie.core.opengl.Shader;
import java.util.function.Consumer;
import java.util.List;
import zombie.core.Styles.TransparentStyle;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.core.Styles.AbstractStyle;
import java.util.ArrayList;
import zombie.core.textures.TextureFBO;
import zombie.core.Styles.Style;
import zombie.core.textures.TextureDraw;

public abstract class GenericSpriteRenderState
{
    public final int index;
    public TextureDraw[] sprite;
    public Style[] style;
    public int numSprites;
    public TextureFBO fbo;
    public boolean bRendered;
    private boolean m_isRendering;
    public final ArrayList<TextureDraw> postRender;
    public AbstractStyle defaultStyle;
    public boolean bCursorVisible;
    public static final byte UVCA_NONE = -1;
    public static final byte UVCA_CIRCLE = 1;
    public static final byte UVCA_NOCIRCLE = 2;
    private byte useVertColorsArray;
    private int texture2_color0;
    private int texture2_color1;
    private int texture2_color2;
    private int texture2_color3;
    private SpriteRenderer.WallShaderTexRender wallShaderTexRender;
    private Texture texture1_cutaway;
    private int texture1_cutaway_x;
    private int texture1_cutaway_y;
    private int texture1_cutaway_w;
    private int texture1_cutaway_h;
    
    protected GenericSpriteRenderState(final int index) {
        this.sprite = new TextureDraw[2048];
        this.style = new Style[2048];
        this.postRender = new ArrayList<TextureDraw>();
        this.defaultStyle = TransparentStyle.instance;
        this.bCursorVisible = true;
        this.useVertColorsArray = -1;
        this.index = index;
        for (int i = 0; i < this.sprite.length; ++i) {
            this.sprite[i] = new TextureDraw();
        }
    }
    
    public void onRendered() {
        this.m_isRendering = false;
        this.bRendered = true;
    }
    
    public void onRenderAcquired() {
        this.m_isRendering = true;
    }
    
    public boolean isRendering() {
        return this.m_isRendering;
    }
    
    public void onReady() {
        this.bRendered = false;
    }
    
    public boolean isReady() {
        return !this.bRendered;
    }
    
    public boolean isRendered() {
        return this.bRendered;
    }
    
    public void CheckSpriteSlots() {
        if (this.numSprites != this.sprite.length) {
            return;
        }
        final TextureDraw[] sprite = this.sprite;
        this.sprite = new TextureDraw[this.numSprites * 3 / 2 + 1];
        for (int i = this.numSprites; i < this.sprite.length; ++i) {
            this.sprite[i] = new TextureDraw();
        }
        System.arraycopy(sprite, 0, this.sprite, 0, this.numSprites);
        System.arraycopy(this.style, 0, this.style = new Style[this.numSprites * 3 / 2 + 1], 0, this.numSprites);
    }
    
    public static void clearSprites(final List<TextureDraw> list) {
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).postRender();
        }
        list.clear();
    }
    
    public void clear() {
        clearSprites(this.postRender);
        this.numSprites = 0;
    }
    
    public void glDepthMask(final boolean b) {
        this.CheckSpriteSlots();
        TextureDraw.glDepthMask(this.sprite[this.numSprites], b);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderflipped(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        this.render(texture, n, n2, n3, n4, n5, n6, n7, n8, consumer);
        this.sprite[this.numSprites - 1].flipped = true;
    }
    
    public void drawSkyBox(final Shader shader, final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.drawSkyBox(this.sprite[this.numSprites], shader, n, n2, n3);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void drawWater(final Shader shader, final int n, final int n2, final boolean b) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        shader.startMainThread(this.sprite[this.numSprites], n);
        TextureDraw.drawWater(this.sprite[this.numSprites], shader, n, n2, b);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void drawPuddles(final Shader shader, final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.drawPuddles(this.sprite[this.numSprites], shader, n, n2, n3);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void drawParticles(final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.drawParticles(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void glDisable(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glDisable(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void glEnable(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glEnable(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glStencilMask(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glStencilMask(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glClear(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glClear(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glClearColor(final int n, final int n2, final int n3, final int n4) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glClearColor(this.sprite[this.numSprites], n, n2, n3, n4);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glStencilFunc(final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glStencilFunc(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glStencilOp(final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glStencilOp(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glColorMask(final int n, final int n2, final int n3, final int n4) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glColorMask(this.sprite[this.numSprites], n, n2, n3, n4);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glAlphaFunc(final int n, final float n2) {
        if (!SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glAlphaFunc(this.sprite[this.numSprites], n, n2);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glBlendFunc(final int n, final int n2) {
        if (!SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glBlendFunc(this.sprite[this.numSprites], n, n2);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glBlendFuncSeparate(final int n, final int n2, final int n3, final int n4) {
        if (!SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glBlendFuncSeparate(this.sprite[this.numSprites], n, n2, n3, n4);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glBlendEquation(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glBlendEquation(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void render(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final float n9, final float n10, final float n11, final float n12, final Consumer<TextureDraw> consumer) {
        this.render(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n9, n10, n11, n12, n9, n10, n11, n12, n9, n10, n11, n12, consumer);
    }
    
    public void render(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20, final float n21, final float n22, final float n23, final float n24, final Consumer<TextureDraw> consumer) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], texture, (float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, (float)n7, (float)n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, consumer);
        if (this.useVertColorsArray != -1) {
            final TextureDraw textureDraw = this.sprite[this.numSprites];
            textureDraw.useAttribArray = this.useVertColorsArray;
            textureDraw.tex1_col0 = this.texture2_color0;
            textureDraw.tex1_col1 = this.texture2_color1;
            textureDraw.tex1_col2 = this.texture2_color2;
            textureDraw.tex1_col3 = this.texture2_color3;
        }
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void setUseVertColorsArray(final byte useVertColorsArray, final int texture2_color0, final int texture2_color2, final int texture2_color3, final int texture2_color4) {
        this.useVertColorsArray = useVertColorsArray;
        this.texture2_color0 = texture2_color0;
        this.texture2_color1 = texture2_color2;
        this.texture2_color2 = texture2_color3;
        this.texture2_color3 = texture2_color4;
    }
    
    public void clearUseVertColorsArray() {
        this.useVertColorsArray = -1;
    }
    
    public void renderdebug(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20, final float n21, final float n22, final float n23, final float n24, final Consumer<TextureDraw> consumer) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, consumer);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderline(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        if (n <= n3 && n2 <= n4) {
            TextureDraw.Create(this.sprite[this.numSprites], texture, n + n9, n2 - n9, n3 + n9, n4 - n9, n3 - n9, n4 + n9, n - n9, n2 + n9, n5, n6, n7, n8);
        }
        else if (n >= n3 && n2 >= n4) {
            TextureDraw.Create(this.sprite[this.numSprites], texture, n + n9, n2 - n9, n - n9, n2 + n9, n3 - n9, n4 + n9, n3 + n9, n4 - n9, n5, n6, n7, n8);
        }
        else if (n >= n3 && n2 <= n4) {
            TextureDraw.Create(this.sprite[this.numSprites], texture, n3 - n9, n4 - n9, n - n9, n2 - n9, n + n9, n2 + n9, n3 + n9, n4 + n9, n5, n6, n7, n8);
        }
        else if (n <= n3 && n2 >= n4) {
            TextureDraw.Create(this.sprite[this.numSprites], texture, n - n9, n2 - n9, n + n9, n2 + n9, n3 + n9, n4 + n9, n3 - n9, n4 - n9, n5, n6, n7, n8);
        }
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderline(final Texture texture, final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8) {
        this.renderline(texture, (float)n, (float)n2, (float)n3, (float)n4, n5, n6, n7, n8, 1);
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final int n10, final int n11, final int n12) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        if (texture != null && !texture.isReady()) {
            return;
        }
        if (n8 == 0.0f) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        final int colorToABGR = Color.colorToABGR(n5, n6, n7, n8);
        final float n9 = n + n3;
        final float n10 = n2 + n4;
        TextureDraw textureDraw;
        if (this.wallShaderTexRender == null) {
            textureDraw = TextureDraw.Create(this.sprite[this.numSprites], texture, n, n2, n9, n2, n9, n10, n, n10, colorToABGR, colorToABGR, colorToABGR, colorToABGR, consumer);
        }
        else {
            textureDraw = TextureDraw.Create(this.sprite[this.numSprites], texture, this.wallShaderTexRender, n, n2, n9 - n, n10 - n2, n5, n6, n7, n8, consumer);
        }
        if (this.useVertColorsArray != -1) {
            textureDraw.useAttribArray = this.useVertColorsArray;
            textureDraw.tex1_col0 = this.texture2_color0;
            textureDraw.tex1_col1 = this.texture2_color1;
            textureDraw.tex1_col2 = this.texture2_color2;
            textureDraw.tex1_col3 = this.texture2_color3;
        }
        if (this.texture1_cutaway != null) {
            textureDraw.tex1 = this.texture1_cutaway;
            final float n11 = this.texture1_cutaway.xEnd - this.texture1_cutaway.xStart;
            final float n12 = this.texture1_cutaway.yEnd - this.texture1_cutaway.yStart;
            final float n13 = this.texture1_cutaway_x / (float)this.texture1_cutaway.getWidth();
            final float n14 = (this.texture1_cutaway_x + this.texture1_cutaway_w) / (float)this.texture1_cutaway.getWidth();
            final float n15 = this.texture1_cutaway_y / (float)this.texture1_cutaway.getHeight();
            final float n16 = (this.texture1_cutaway_y + this.texture1_cutaway_h) / (float)this.texture1_cutaway.getHeight();
            final TextureDraw textureDraw2 = textureDraw;
            final TextureDraw textureDraw3 = textureDraw;
            final float n17 = this.texture1_cutaway.xStart + n13 * n11;
            textureDraw3.tex1_u3 = n17;
            textureDraw2.tex1_u0 = n17;
            final TextureDraw textureDraw4 = textureDraw;
            final TextureDraw textureDraw5 = textureDraw;
            final float n18 = this.texture1_cutaway.yStart + n15 * n12;
            textureDraw5.tex1_v1 = n18;
            textureDraw4.tex1_v0 = n18;
            final TextureDraw textureDraw6 = textureDraw;
            final TextureDraw textureDraw7 = textureDraw;
            final float n19 = this.texture1_cutaway.xStart + n14 * n11;
            textureDraw7.tex1_u2 = n19;
            textureDraw6.tex1_u1 = n19;
            final TextureDraw textureDraw8 = textureDraw;
            final TextureDraw textureDraw9 = textureDraw;
            final float n20 = this.texture1_cutaway.yStart + n16 * n12;
            textureDraw9.tex1_v3 = n20;
            textureDraw8.tex1_v2 = n20;
        }
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderRect(final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8) {
        if (n8 == 0.0f) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], null, (float)n, (float)n2, (float)n3, (float)n4, n5, n6, n7, n8, null);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderPoly(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], null, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderPoly(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        if (texture != null && !texture.isReady()) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        if (texture != null) {
            final float xEnd = texture.getXEnd();
            final float xStart = texture.getXStart();
            final float yEnd = texture.getYEnd();
            final float yStart = texture.getYStart();
            final TextureDraw textureDraw = this.sprite[this.numSprites];
            textureDraw.u0 = xStart;
            textureDraw.u1 = xEnd;
            textureDraw.u2 = xEnd;
            textureDraw.u3 = xStart;
            textureDraw.v0 = yStart;
            textureDraw.v1 = yStart;
            textureDraw.v2 = yEnd;
            textureDraw.v3 = yEnd;
        }
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void renderPoly(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float u0, final float v0, final float u2, final float v2, final float u3, final float v3, final float u4, final float v4) {
        if (texture != null && !texture.isReady()) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        if (texture != null) {
            final TextureDraw textureDraw = this.sprite[this.numSprites];
            textureDraw.u0 = u0;
            textureDraw.u1 = u2;
            textureDraw.u2 = u3;
            textureDraw.u3 = u4;
            textureDraw.v0 = v0;
            textureDraw.v1 = v2;
            textureDraw.v2 = v3;
            textureDraw.v3 = v4;
        }
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final Consumer<TextureDraw> consumer) {
        if (n8 == 0.0f) {
            return;
        }
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, consumer);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void glBuffer(final int n, final int n2) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glBuffer(this.sprite[this.numSprites], n, n2);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glDoStartFrame(final int n, final int n2, final float n3, final int n4) {
        this.glDoStartFrame(n, n2, n3, n4, false);
    }
    
    public void glDoStartFrame(final int n, final int n2, final float n3, final int n4, final boolean b) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glDoStartFrame(this.sprite[this.numSprites], n, n2, n3, n4, b);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glDoStartFrameFx(final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glDoStartFrameFx(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glIgnoreStyles(final boolean b) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glIgnoreStyles(this.sprite[this.numSprites], b);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glDoEndFrame() {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glDoEndFrame(this.sprite[this.numSprites]);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glDoEndFrameFx(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glDoEndFrameFx(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void doCoreIntParam(final int n, final float n2) {
        this.CheckSpriteSlots();
        TextureDraw.doCoreIntParam(this.sprite[this.numSprites], n, n2);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glTexParameteri(final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glTexParameteri(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void setCutawayTexture(final Texture texture1_cutaway, final int texture1_cutaway_x, final int texture1_cutaway_y, final int texture1_cutaway_w, final int texture1_cutaway_h) {
        this.texture1_cutaway = texture1_cutaway;
        this.texture1_cutaway_x = texture1_cutaway_x;
        this.texture1_cutaway_y = texture1_cutaway_y;
        this.texture1_cutaway_w = texture1_cutaway_w;
        this.texture1_cutaway_h = texture1_cutaway_h;
    }
    
    public void clearCutawayTexture() {
        this.texture1_cutaway = null;
    }
    
    public void setExtraWallShaderParams(final SpriteRenderer.WallShaderTexRender wallShaderTexRender) {
        this.wallShaderTexRender = wallShaderTexRender;
    }
    
    public void ShaderUpdate1i(final int n, final int n2, final int n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.ShaderUpdate1i(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void ShaderUpdate1f(final int n, final int n2, final float n3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.ShaderUpdate1f(this.sprite[this.numSprites], n, n2, n3);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void ShaderUpdate2f(final int n, final int n2, final float n3, final float n4) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.ShaderUpdate2f(this.sprite[this.numSprites], n, n2, n3, n4);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void ShaderUpdate3f(final int n, final int n2, final float n3, final float n4, final float n5) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.ShaderUpdate3f(this.sprite[this.numSprites], n, n2, n3, n4, n5);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void ShaderUpdate4f(final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.ShaderUpdate4f(this.sprite[this.numSprites], n, n2, n3, n4, n5, n6);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glLoadIdentity() {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glLoadIdentity(this.sprite[this.numSprites]);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glGenerateMipMaps(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glGenerateMipMaps(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void glBind(final int n) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glBind(this.sprite[this.numSprites], n);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void glViewport(final int n, final int n2, final int n3, final int n4) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.glViewport(this.sprite[this.numSprites], n, n2, n3, n4);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
    }
    
    public void drawModel(final ModelManager.ModelSlot modelSlot) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.drawModel(this.sprite[this.numSprites], modelSlot);
        assert this.sprite[this.numSprites].drawer != null;
        this.postRender.add(this.sprite[this.numSprites]);
        this.style[this.numSprites] = this.defaultStyle;
        ++this.numSprites;
        ++modelSlot.renderRefCount;
    }
    
    public void drawGeneric(final TextureDraw.GenericDrawer drawer) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        this.sprite[this.numSprites].type = TextureDraw.Type.DrawModel;
        this.sprite[this.numSprites].drawer = drawer;
        this.style[this.numSprites] = this.defaultStyle;
        this.postRender.add(this.sprite[this.numSprites]);
        ++this.numSprites;
    }
    
    public void StartShader(final int n, final int n2) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.StartShader(this.sprite[this.numSprites], n);
        if (n != 0 && Shader.ShaderMap.containsKey(n)) {
            Shader.ShaderMap.get(n).startMainThread(this.sprite[this.numSprites], n2);
            this.postRender.add(this.sprite[this.numSprites]);
        }
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
    
    public void EndShader() {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }
        TextureDraw.StartShader(this.sprite[this.numSprites], 0);
        this.style[this.numSprites] = TransparentStyle.instance;
        ++this.numSprites;
    }
}
