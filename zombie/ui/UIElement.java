// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.IndieGL;
import java.util.Vector;
import java.util.Collection;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.input.Mouse;
import zombie.Lua.LuaManager;
import zombie.core.math.PZMath;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.core.BoxedStaticValues;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.textures.Texture;
import java.util.ArrayList;
import zombie.core.Color;

public class UIElement
{
    static final Color tempcol;
    static final ArrayList<UIElement> toAdd;
    static Texture white;
    static int StencilLevel;
    public boolean capture;
    public boolean IgnoreLossControl;
    public String clickedValue;
    public final ArrayList<UIElement> Controls;
    public boolean defaultDraw;
    public boolean followGameWorld;
    private int renderThisPlayerOnly;
    public float height;
    public UIElement Parent;
    public boolean visible;
    public float width;
    public double x;
    public double y;
    public KahluaTable table;
    public boolean alwaysBack;
    public boolean bScrollChildren;
    public boolean bScrollWithParent;
    private boolean bRenderClippedChildren;
    public boolean anchorTop;
    public boolean anchorLeft;
    public boolean anchorRight;
    public boolean anchorBottom;
    public int playerContext;
    boolean alwaysOnTop;
    int maxDrawHeight;
    Double yScroll;
    Double xScroll;
    int scrollHeight;
    double lastheight;
    double lastwidth;
    boolean bResizeDirty;
    boolean enabled;
    private final ArrayList<UIElement> toTop;
    private boolean bConsumeMouseEvents;
    private long leftDownTime;
    private boolean clicked;
    private double clickX;
    private double clickY;
    private String uiname;
    private boolean bWantKeyEvents;
    private boolean bForceCursorVisible;
    
    public UIElement() {
        this.capture = false;
        this.IgnoreLossControl = false;
        this.clickedValue = null;
        this.Controls = new ArrayList<UIElement>();
        this.defaultDraw = true;
        this.followGameWorld = false;
        this.renderThisPlayerOnly = -1;
        this.height = 256.0f;
        this.Parent = null;
        this.visible = true;
        this.width = 256.0f;
        this.x = 0.0;
        this.y = 0.0;
        this.bScrollChildren = false;
        this.bScrollWithParent = true;
        this.bRenderClippedChildren = true;
        this.anchorTop = true;
        this.anchorLeft = true;
        this.anchorRight = false;
        this.anchorBottom = false;
        this.playerContext = -1;
        this.alwaysOnTop = false;
        this.maxDrawHeight = -1;
        this.yScroll = 0.0;
        this.xScroll = 0.0;
        this.scrollHeight = 0;
        this.lastheight = -1.0;
        this.lastwidth = -1.0;
        this.bResizeDirty = false;
        this.enabled = true;
        this.toTop = new ArrayList<UIElement>(0);
        this.bConsumeMouseEvents = true;
        this.leftDownTime = 0L;
        this.uiname = "";
        this.bWantKeyEvents = false;
        this.bForceCursorVisible = false;
    }
    
    public UIElement(final KahluaTable table) {
        this.capture = false;
        this.IgnoreLossControl = false;
        this.clickedValue = null;
        this.Controls = new ArrayList<UIElement>();
        this.defaultDraw = true;
        this.followGameWorld = false;
        this.renderThisPlayerOnly = -1;
        this.height = 256.0f;
        this.Parent = null;
        this.visible = true;
        this.width = 256.0f;
        this.x = 0.0;
        this.y = 0.0;
        this.bScrollChildren = false;
        this.bScrollWithParent = true;
        this.bRenderClippedChildren = true;
        this.anchorTop = true;
        this.anchorLeft = true;
        this.anchorRight = false;
        this.anchorBottom = false;
        this.playerContext = -1;
        this.alwaysOnTop = false;
        this.maxDrawHeight = -1;
        this.yScroll = 0.0;
        this.xScroll = 0.0;
        this.scrollHeight = 0;
        this.lastheight = -1.0;
        this.lastwidth = -1.0;
        this.bResizeDirty = false;
        this.enabled = true;
        this.toTop = new ArrayList<UIElement>(0);
        this.bConsumeMouseEvents = true;
        this.leftDownTime = 0L;
        this.uiname = "";
        this.bWantKeyEvents = false;
        this.bForceCursorVisible = false;
        this.table = table;
    }
    
    public Double getMaxDrawHeight() {
        return BoxedStaticValues.toDouble(this.maxDrawHeight);
    }
    
    public void setMaxDrawHeight(final double n) {
        this.maxDrawHeight = (int)n;
    }
    
    public void clearMaxDrawHeight() {
        this.maxDrawHeight = -1;
    }
    
    public Double getXScroll() {
        return this.xScroll;
    }
    
    public void setXScroll(final double d) {
        this.xScroll = d;
    }
    
    public Double getYScroll() {
        return this.yScroll;
    }
    
    public void setYScroll(final double d) {
        this.yScroll = d;
    }
    
    public void setAlwaysOnTop(final boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }
    
    public void backMost() {
        this.alwaysBack = true;
    }
    
    public void AddChild(final UIElement e) {
        this.getControls().add(e);
        e.setParent(this);
    }
    
    public void RemoveChild(final UIElement o) {
        this.getControls().remove(o);
        o.setParent(null);
    }
    
    public Double getScrollHeight() {
        return BoxedStaticValues.toDouble(this.scrollHeight);
    }
    
    public void setScrollHeight(final double n) {
        this.scrollHeight = (int)n;
    }
    
    public boolean isConsumeMouseEvents() {
        return this.bConsumeMouseEvents;
    }
    
    public void setConsumeMouseEvents(final boolean bConsumeMouseEvents) {
        this.bConsumeMouseEvents = bConsumeMouseEvents;
    }
    
    public void ClearChildren() {
        this.getControls().clear();
    }
    
    public void ButtonClicked(final String clickedValue) {
        this.setClickedValue(clickedValue);
    }
    
    public void DrawText(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7) {
        TextManager.instance.DrawString(uiFont, n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, (float)n3, s, n4, n5, n6, n7);
    }
    
    public void DrawText(final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        TextManager.instance.DrawString(n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawText(final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        TextManager.instance.DrawString(n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n5, n6, n7, n8);
    }
    
    public void DrawText(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        if (s == null) {
            return;
        }
        final int n7 = (int)(n2 + this.getAbsoluteY() + this.yScroll);
        if (n7 + 100 < 0 || n7 > 4096) {
            return;
        }
        TextManager.instance.DrawString(uiFont, n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawTextUntrimmed(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        if (s == null) {
            return;
        }
        TextManager.instance.DrawStringUntrimmed(uiFont, n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawTextCentre(final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        TextManager.instance.DrawStringCentre(n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawTextCentre(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        TextManager.instance.DrawStringCentre(uiFont, n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawTextRight(final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        TextManager.instance.DrawStringRight(n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawTextRight(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        TextManager.instance.DrawStringRight(uiFont, n + this.getAbsoluteX() + this.xScroll, n2 + this.getAbsoluteY() + this.yScroll, s, n3, n4, n5, n6);
    }
    
    public void DrawTextureAngle(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7) {
        if (!this.isVisible()) {
            return;
        }
        final float n8 = (float)(texture.getWidth() / 2);
        final float n9 = (float)(texture.getHeight() / 2);
        final double radians = Math.toRadians(180.0 + n3);
        final double n10 = Math.cos(radians) * n8;
        final double n11 = Math.sin(radians) * n8;
        final double n12 = Math.cos(radians) * n9;
        final double n13 = Math.sin(radians) * n9;
        SpriteRenderer.instance.render(texture, n10 - n13 + (this.getAbsoluteX() + n), n12 + n11 + (this.getAbsoluteY() + n2), -n10 - n13 + (this.getAbsoluteX() + n), n12 - n11 + (this.getAbsoluteY() + n2), -n10 + n13 + (this.getAbsoluteX() + n), -n12 - n11 + (this.getAbsoluteY() + n2), n10 + n13 + (this.getAbsoluteX() + n), -n12 + n11 + (this.getAbsoluteY() + n2), (float)n4, (float)n5, (float)n6, (float)n7, (float)n4, (float)n5, (float)n6, (float)n7, (float)n4, (float)n5, (float)n6, (float)n7, (float)n4, (float)n5, (float)n6, (float)n7, null);
    }
    
    public void DrawTextureAngle(final Texture texture, final double n, final double n2, final double n3) {
        this.DrawTextureAngle(texture, n, n2, n3, 1.0, 1.0, 1.0, 1.0);
    }
    
    public void DrawTexture(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        SpriteRenderer.instance.render(texture, n, n2, n3, n4, n5, n6, n7, n8, (float)n9, (float)n10, (float)n11, (float)n12, (float)n9, (float)n10, (float)n11, (float)n12, (float)n9, (float)n10, (float)n11, (float)n12, (float)n9, (float)n10, (float)n11, (float)n12, null);
    }
    
    public void DrawTexture(final Texture texture, final double n, final double n2, final double n3) {
        if (!this.isVisible()) {
            return;
        }
        final double n4 = n + this.getAbsoluteX();
        final double n5 = n2 + this.getAbsoluteY();
        final double n6 = n4 + texture.offsetX;
        final double n7 = n5 + texture.offsetY;
        final int n8 = (int)(n7 + this.yScroll);
        if (n8 + texture.getHeight() < 0 || n8 > 4096) {
            return;
        }
        SpriteRenderer.instance.renderi(texture, (int)(n6 + this.xScroll), (int)(n7 + this.yScroll), texture.getWidth(), texture.getHeight(), 1.0f, 1.0f, 1.0f, (float)n3, null);
    }
    
    public void DrawTextureCol(final Texture texture, final double n, final double n2, final Color color) {
        if (!this.isVisible()) {
            return;
        }
        double n3 = n + this.getAbsoluteX();
        double n4 = n2 + this.getAbsoluteY();
        int width = 0;
        int height = 0;
        if (texture != null) {
            n3 += texture.offsetX;
            n4 += texture.offsetY;
            width = texture.getWidth();
            height = texture.getHeight();
        }
        final int n5 = (int)(n4 + this.yScroll);
        if (n5 + height < 0 || n5 > 4096) {
            return;
        }
        SpriteRenderer.instance.renderi(texture, (int)(n3 + this.xScroll), (int)(n4 + this.yScroll), width, height, color.r, color.g, color.b, color.a, null);
    }
    
    public void DrawTextureScaled(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5) {
        if (!this.isVisible()) {
            return;
        }
        SpriteRenderer.instance.renderi(texture, (int)(n + this.getAbsoluteX() + this.xScroll), (int)(n2 + this.getAbsoluteY() + this.yScroll), (int)n3, (int)n4, 1.0f, 1.0f, 1.0f, (float)n5, null);
    }
    
    public void DrawTextureScaledUniform(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7) {
        if (!this.isVisible() || texture == null) {
            return;
        }
        SpriteRenderer.instance.renderi(texture, (int)(n + this.getAbsoluteX() + texture.offsetX * n3 + this.xScroll), (int)(n2 + this.getAbsoluteY() + texture.offsetY * n3 + this.yScroll), (int)(texture.getWidth() * n3), (int)(texture.getHeight() * n3), (float)n4, (float)n5, (float)n6, (float)n7, null);
    }
    
    public void DrawTextureScaledAspect(final Texture texture, final double n, final double n2, double n3, double n4, final double n5, final double n6, final double n7, final double n8) {
        if (!this.isVisible() || texture == null) {
            return;
        }
        double n9 = n + this.getAbsoluteX();
        double n10 = n2 + this.getAbsoluteY();
        if (texture.getWidth() > 0 && texture.getHeight() > 0 && n3 > 0.0 && n4 > 0.0) {
            final double min = Math.min(n3 / texture.getWidthOrig(), n4 / texture.getHeightOrig());
            final double n11 = n3;
            final double n12 = n4;
            n3 = texture.getWidth() * min;
            n4 = texture.getHeight() * min;
            n9 -= (n3 - n11) / 2.0;
            n10 -= (n4 - n12) / 2.0;
        }
        SpriteRenderer.instance.renderi(texture, (int)(n9 + this.xScroll), (int)(n10 + this.yScroll), (int)n3, (int)n4, (float)n5, (float)n6, (float)n7, (float)n8, null);
    }
    
    public void DrawTextureScaledAspect2(final Texture texture, final double n, final double n2, double n3, double n4, final double n5, final double n6, final double n7, final double n8) {
        if (!this.isVisible() || texture == null) {
            return;
        }
        double n9 = n + this.getAbsoluteX();
        double n10 = n2 + this.getAbsoluteY();
        if (texture.getWidth() > 0 && texture.getHeight() > 0 && n3 > 0.0 && n4 > 0.0) {
            final double min = Math.min(n3 / texture.getWidth(), n4 / texture.getHeight());
            final double n11 = n3;
            final double n12 = n4;
            n3 = texture.getWidth() * min;
            n4 = texture.getHeight() * min;
            n9 -= (n3 - n11) / 2.0;
            n10 -= (n4 - n12) / 2.0;
        }
        SpriteRenderer.instance.render(texture, (float)(int)(n9 + this.xScroll), (float)(int)(n10 + this.yScroll), (float)(int)n3, (float)(int)n4, (float)n5, (float)n6, (float)n7, (float)n8, null);
    }
    
    public void DrawTextureScaledCol(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        if (texture != null) {}
        if (!this.isVisible()) {
            return;
        }
        final double n9 = n + this.getAbsoluteX();
        final double n10 = n2 + this.getAbsoluteY();
        final int n11 = (int)(n10 + this.yScroll);
        if (n11 + n4 < 0.0 || n11 > 4096) {
            return;
        }
        SpriteRenderer.instance.renderi(texture, (int)(n9 + this.xScroll), (int)(n10 + this.yScroll), (int)n3, (int)n4, (float)n5, (float)n6, (float)n7, (float)n8, null);
    }
    
    public void DrawTextureScaledCol(final Texture texture, final double n, final double n2, final double n3, final double n4, final Color color) {
        if (texture != null) {}
        if (!this.isVisible()) {
            return;
        }
        SpriteRenderer.instance.render(texture, (float)(int)(n + this.getAbsoluteX() + this.xScroll), (float)(int)(n2 + this.getAbsoluteY() + this.yScroll), (float)(int)n3, (float)(int)n4, color.r, color.g, color.b, color.a, null);
    }
    
    public void DrawTextureScaledColor(final Texture texture, final Double n, final Double n2, final Double n3, final Double n4, final Double n5, final Double n6, final Double n7, final Double n8) {
        this.DrawTextureScaledCol(texture, n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void DrawTextureColor(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        UIElement.tempcol.r = (float)n3;
        UIElement.tempcol.g = (float)n4;
        UIElement.tempcol.b = (float)n5;
        UIElement.tempcol.a = (float)n6;
        this.DrawTextureCol(texture, n, n2, UIElement.tempcol);
    }
    
    public void DrawSubTextureRGBA(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        if (texture == null || !this.isVisible() || n3 <= 0.0 || n4 <= 0.0 || n7 <= 0.0 || n8 <= 0.0) {
            return;
        }
        final double n13 = n5 + this.getAbsoluteX() + this.xScroll;
        final double n14 = n6 + this.getAbsoluteY() + this.yScroll;
        final double n15 = n13 + texture.offsetX;
        final double n16 = n14 + texture.offsetY;
        if (n16 + n8 < 0.0 || n16 > 4096.0) {
            return;
        }
        final float clamp = PZMath.clamp((float)n, 0.0f, (float)texture.getWidth());
        final float clamp2 = PZMath.clamp((float)n2, 0.0f, (float)texture.getHeight());
        final float n17 = PZMath.clamp((float)(clamp + n3), 0.0f, (float)texture.getWidth()) - clamp;
        final float n18 = PZMath.clamp((float)(clamp2 + n4), 0.0f, (float)texture.getHeight()) - clamp2;
        final float n19 = clamp / texture.getWidth();
        final float n20 = clamp2 / texture.getHeight();
        final float n21 = (clamp + n17) / texture.getWidth();
        final float n22 = (clamp2 + n18) / texture.getHeight();
        final float n23 = texture.getXEnd() - texture.getXStart();
        final float n24 = texture.getYEnd() - texture.getYStart();
        final float n25 = texture.getXStart() + n19 * n23;
        final float n26 = texture.getXStart() + n21 * n23;
        final float n27 = texture.getYStart() + n20 * n24;
        final float n28 = texture.getYStart() + n22 * n24;
        SpriteRenderer.instance.render(texture, (float)n15, (float)n16, (float)n7, (float)n8, (float)n9, (float)n10, (float)n11, (float)n12, n25, n27, n26, n27, n26, n28, n25, n28);
    }
    
    public void DrawTextureTiled(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        if (texture == null || !this.isVisible() || n3 <= 0.0 || n4 <= 0.0) {
            return;
        }
        for (double n9 = n2; n9 < n2 + n4; n9 += texture.getHeight()) {
            for (double n10 = n; n10 < n + n3; n10 += texture.getWidth()) {
                double n11 = texture.getWidth();
                double n12 = texture.getHeight();
                if (n10 + n11 > n + n3) {
                    n11 = n + n3 - n10;
                }
                if (n9 + texture.getHeight() > n2 + n4) {
                    n12 = n2 + n4 - n9;
                }
                this.DrawSubTextureRGBA(texture, 0.0, 0.0, n11, n12, n10, n9, n11, n12, n5, n6, n7, n8);
            }
        }
    }
    
    public void DrawTextureTiledX(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        if (texture == null || !this.isVisible() || n3 <= 0.0 || n4 <= 0.0) {
            return;
        }
        for (double n9 = n; n9 < n + n3; n9 += texture.getWidth()) {
            double n10 = texture.getWidth();
            final double n11 = texture.getHeight();
            if (n9 + n10 > n + n3) {
                n10 = n + n3 - n9;
            }
            this.DrawSubTextureRGBA(texture, 0.0, 0.0, n10, n11, n9, n2, n10, n11, n5, n6, n7, n8);
        }
    }
    
    public void DrawTextureTiledY(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        if (texture == null || !this.isVisible() || n3 <= 0.0 || n4 <= 0.0) {
            return;
        }
        for (double n9 = n2; n9 < n2 + n4; n9 += texture.getHeight()) {
            final double n10 = texture.getWidth();
            double n11 = texture.getHeight();
            if (n9 + texture.getHeight() > n2 + n4) {
                n11 = n2 + n4 - n9;
            }
            this.DrawSubTextureRGBA(texture, 0.0, 0.0, n10, n11, n, n9, n10, n11, n5, n6, n7, n8);
        }
    }
    
    public void DrawTextureIgnoreOffset(final Texture texture, final double n, final double n2, final int n3, final int n4, final Color color) {
        if (!this.isVisible()) {
            return;
        }
        SpriteRenderer.instance.render(texture, (float)(int)(n + this.getAbsoluteX() + this.xScroll), (float)(int)(n2 + this.getAbsoluteY() + this.yScroll), (float)n3, (float)n4, color.r, color.g, color.b, color.a, null);
    }
    
    public void DrawTexture_FlippedX(final Texture texture, final double n, final double n2, final int n3, final int n4, final Color color) {
        if (!this.isVisible()) {
            return;
        }
        SpriteRenderer.instance.renderflipped(texture, (float)(n + this.getAbsoluteX() + this.xScroll), (float)(n2 + this.getAbsoluteY() + this.yScroll), (float)n3, (float)n4, color.r, color.g, color.b, color.a, null);
    }
    
    public void DrawTexture_FlippedXIgnoreOffset(final Texture texture, final double n, final double n2, final int n3, final int n4, final Color color) {
        if (!this.isVisible()) {
            return;
        }
        SpriteRenderer.instance.renderflipped(texture, (float)(n + this.getAbsoluteX() + this.xScroll), (float)(n2 + this.getAbsoluteY() + this.yScroll), (float)n3, (float)n4, color.r, color.g, color.b, color.a, null);
    }
    
    public void DrawUVSliceTexture(final Texture texture, final double n, final double n2, final double n3, final double n4, final Color color, final double n5, final double n6, final double n7, final double n8) {
        if (!this.isVisible()) {
            return;
        }
        final double n9 = n + this.getAbsoluteX();
        final double n10 = n2 + this.getAbsoluteY();
        final double n11 = n9 + texture.offsetX;
        final double n12 = n10 + texture.offsetY;
        Texture.lr = color.r;
        Texture.lg = color.g;
        Texture.lb = color.b;
        Texture.la = color.a;
        final double n13 = texture.getXStart();
        final double n14 = texture.getYStart();
        final double n15 = texture.getXEnd();
        final double n16 = texture.getYEnd();
        final double n17 = n15 - n13;
        final double n18 = n16 - n14;
        final double n19 = n13 + n5 * n17;
        final double n20 = n14 + n6 * n18;
        final double n21 = n15 - (1.0 - n7) * n17;
        final double n22 = n16 - (1.0 - n8) * n18;
        final double n23 = (int)(n19 * 1000.0) / 1000.0f;
        final double n24 = (int)(n21 * 1000.0) / 1000.0f;
        final double n25 = (int)(n20 * 1000.0) / 1000.0f;
        final double n26 = (int)(n22 * 1000.0) / 1000.0f;
        final double n27 = n11 + n3;
        final double n28 = n12 + n4;
        final double n29 = n11 + n5 * n3;
        final double n30 = n12 + n6 * n4;
        SpriteRenderer.instance.render(texture, (float)n29 + this.getXScroll().intValue(), (float)n30 + this.getYScroll().intValue(), (float)(n27 - (1.0 - n7) * n3 - n29), (float)(n28 - (1.0 - n8) * n4 - n30), color.r, color.g, color.b, color.a, (float)n23, (float)n25, (float)n24, (float)n25, (float)n24, (float)n26, (float)n23, (float)n26);
    }
    
    public Boolean getScrollChildren() {
        return this.bScrollChildren ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setScrollChildren(final boolean bScrollChildren) {
        this.bScrollChildren = bScrollChildren;
    }
    
    public Boolean getScrollWithParent() {
        return this.bScrollWithParent ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setScrollWithParent(final boolean bScrollWithParent) {
        this.bScrollWithParent = bScrollWithParent;
    }
    
    public void setRenderClippedChildren(final boolean bRenderClippedChildren) {
        this.bRenderClippedChildren = bRenderClippedChildren;
    }
    
    public Double getAbsoluteX() {
        if (this.getParent() == null) {
            return BoxedStaticValues.toDouble(this.getX().intValue());
        }
        if (this.getParent().getScrollChildren() && this.getScrollWithParent()) {
            return BoxedStaticValues.toDouble(this.getParent().getAbsoluteX() + this.getX().intValue() + this.getParent().getXScroll().intValue());
        }
        return BoxedStaticValues.toDouble(this.getParent().getAbsoluteX() + this.getX().intValue());
    }
    
    public Double getAbsoluteY() {
        if (this.getParent() == null) {
            return BoxedStaticValues.toDouble(this.getY().intValue());
        }
        if (this.getParent().getScrollChildren() && this.getScrollWithParent()) {
            return BoxedStaticValues.toDouble(this.getParent().getAbsoluteY() + this.getY().intValue() + this.getParent().getYScroll().intValue());
        }
        return BoxedStaticValues.toDouble(this.getParent().getAbsoluteY() + this.getY().intValue());
    }
    
    public String getClickedValue() {
        return this.clickedValue;
    }
    
    public void setClickedValue(final String clickedValue) {
        this.clickedValue = clickedValue;
    }
    
    public void bringToTop() {
        UIManager.pushToTop(this);
        if (this.Parent != null) {
            this.Parent.addBringToTop(this);
        }
    }
    
    void onRightMouseUpOutside(final double n, final double n2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onRightMouseUpOutside") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onRightMouseUpOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            uiElement.onRightMouseUpOutside(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue());
        }
    }
    
    void onRightMouseDownOutside(final double n, final double n2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onRightMouseDownOutside") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onRightMouseDownOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            uiElement.onRightMouseDownOutside(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue());
        }
    }
    
    public void onMouseUpOutside(final double n, final double n2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseUpOutside") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseUpOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            uiElement.onMouseUpOutside(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue());
        }
    }
    
    void onMouseDownOutside(final double n, final double n2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseDownOutside") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseDownOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            uiElement.onMouseDownOutside(n - uiElement.getX().intValue(), n2 - uiElement.getY().intValue());
        }
    }
    
    public Boolean onMouseDown(final double clickX, final double clickY) {
        if (this.clicked && UIManager.isDoubleClick((int)this.clickX, (int)this.clickY, (int)clickX, (int)clickY, (double)this.leftDownTime) && this.getTable() != null && this.getTable().rawget((Object)"onMouseDoubleClick") != null) {
            this.clicked = false;
            return ((boolean)this.onMouseDoubleClick(clickX, clickY)) ? Boolean.TRUE : Boolean.FALSE;
        }
        this.clicked = true;
        this.clickX = clickX;
        this.clickY = clickY;
        this.leftDownTime = System.currentTimeMillis();
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= clickY) {
            return Boolean.FALSE;
        }
        if (this.maxDrawHeight != -1 && this.maxDrawHeight <= clickY) {
            return Boolean.FALSE;
        }
        if (!this.visible) {
            return Boolean.FALSE;
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"onFocus") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onFocus"), (Object)this.table, (Object)BoxedStaticValues.toDouble(clickX - this.xScroll), (Object)BoxedStaticValues.toDouble(clickY - this.yScroll));
        }
        boolean b = false;
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            if (!b && ((clickX > uiElement.getXScrolled(this) && clickY > uiElement.getYScrolled(this) && clickX < uiElement.getXScrolled(this) + uiElement.getWidth() && clickY < uiElement.getYScrolled(this) + uiElement.getHeight()) || uiElement.isCapture())) {
                if (uiElement.onMouseDown(clickX - uiElement.getXScrolled(this).intValue(), clickY - uiElement.getYScrolled(this).intValue())) {
                    b = true;
                }
            }
            else if (uiElement.getTable() != null && uiElement.getTable().rawget((Object)"onMouseDownOutside") != null) {
                LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), uiElement.getTable().rawget((Object)"onMouseDownOutside"), (Object)uiElement.getTable(), (Object)BoxedStaticValues.toDouble(clickX - this.xScroll), (Object)BoxedStaticValues.toDouble(clickY - this.yScroll));
            }
        }
        if (this.getTable() != null) {
            if (b) {
                if (this.getTable().rawget((Object)"onMouseDownOutside") != null) {
                    final Boolean protectedCallBoolean = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseDownOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(clickX - this.xScroll), (Object)BoxedStaticValues.toDouble(clickY - this.yScroll));
                    if (protectedCallBoolean == null) {
                        return Boolean.TRUE;
                    }
                    if (protectedCallBoolean == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }
            else if (this.getTable().rawget((Object)"onMouseDown") != null) {
                final Boolean protectedCallBoolean2 = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseDown"), (Object)this.table, (Object)BoxedStaticValues.toDouble(clickX - this.xScroll), (Object)BoxedStaticValues.toDouble(clickY - this.yScroll));
                if (protectedCallBoolean2 == null) {
                    return Boolean.TRUE;
                }
                if (protectedCallBoolean2 == Boolean.TRUE) {
                    return Boolean.TRUE;
                }
            }
        }
        return b;
    }
    
    private Boolean onMouseDoubleClick(final double n, final double n2) {
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return Boolean.FALSE;
        }
        if (this.maxDrawHeight != -1 && this.maxDrawHeight <= this.y) {
            return Boolean.FALSE;
        }
        if (!this.visible) {
            return Boolean.FALSE;
        }
        if (this.getTable().rawget((Object)"onMouseDoubleClick") != null) {
            final Boolean protectedCallBoolean = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseDoubleClick"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
            if (protectedCallBoolean == null) {
                return Boolean.TRUE;
            }
            if (protectedCallBoolean == Boolean.TRUE) {
                return Boolean.TRUE;
            }
        }
        return Boolean.TRUE;
    }
    
    public Boolean onMouseWheel(final double n) {
        final int xa = Mouse.getXA();
        final int ya = Mouse.getYA();
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseWheel") != null && LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseWheel"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n)) == Boolean.TRUE) {
            return Boolean.TRUE;
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            if (uiElement.isVisible()) {
                if (((xa >= uiElement.getAbsoluteX() && ya >= uiElement.getAbsoluteY() && xa < uiElement.getAbsoluteX() + uiElement.getWidth() && ya < uiElement.getAbsoluteY() + uiElement.getHeight()) || uiElement.isCapture()) && uiElement.onMouseWheel(n)) {
                    return this.bConsumeMouseEvents ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return Boolean.FALSE;
    }
    
    public Boolean onMouseMove(final double n, final double n2) {
        final int xa = Mouse.getXA();
        final int ya = Mouse.getYA();
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return Boolean.FALSE;
        }
        if (this.maxDrawHeight != -1 && this.maxDrawHeight <= ya - this.getAbsoluteY()) {
            return Boolean.FALSE;
        }
        if (!this.visible) {
            return Boolean.FALSE;
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseMove") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseMove"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n), (Object)BoxedStaticValues.toDouble(n2));
        }
        int n3 = 0;
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            if ((xa >= uiElement.getAbsoluteX() && ya >= uiElement.getAbsoluteY() && xa < uiElement.getAbsoluteX() + uiElement.getWidth() && ya < uiElement.getAbsoluteY() + uiElement.getHeight()) || uiElement.isCapture()) {
                if (n3 == 0 && uiElement.onMouseMove(n, n2)) {
                    n3 = 1;
                }
            }
            else {
                uiElement.onMouseMoveOutside(n, n2);
            }
        }
        return this.bConsumeMouseEvents ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void onMouseMoveOutside(final double n, final double n2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseMoveOutside") != null) {
            LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseMoveOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n), (Object)BoxedStaticValues.toDouble(n2));
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            this.getControls().get(i).onMouseMoveOutside(n, n2);
        }
    }
    
    public Boolean onMouseUp(final double n, final double n2) {
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= n2) {
            return Boolean.FALSE;
        }
        if (this.maxDrawHeight != -1 && this.maxDrawHeight <= n2) {
            return Boolean.FALSE;
        }
        if (!this.visible) {
            return Boolean.FALSE;
        }
        int n3 = 0;
        for (int i = this.getControls().size() - 1; i >= 0; i = PZMath.min(i, this.getControls().size()), --i) {
            final UIElement uiElement = this.getControls().get(i);
            if (n3 == 0 && ((n >= uiElement.getXScrolled(this) && n2 >= uiElement.getYScrolled(this) && n < uiElement.getXScrolled(this) + uiElement.getWidth() && n2 < uiElement.getYScrolled(this) + uiElement.getHeight()) || uiElement.isCapture())) {
                if (uiElement.onMouseUp(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue())) {
                    n3 = 1;
                }
            }
            else {
                uiElement.onMouseUpOutside(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue());
            }
        }
        if (this.getTable() != null) {
            if (n3 != 0) {
                if (this.getTable().rawget((Object)"onMouseUpOutside") != null) {
                    final Boolean protectedCallBoolean = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseUpOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
                    if (protectedCallBoolean == null) {
                        return Boolean.TRUE;
                    }
                    if (protectedCallBoolean == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }
            else if (this.getTable().rawget((Object)"onMouseUp") != null) {
                final Boolean protectedCallBoolean2 = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onMouseUp"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
                if (protectedCallBoolean2 == null) {
                    return Boolean.TRUE;
                }
                if (protectedCallBoolean2 == Boolean.TRUE) {
                    return Boolean.TRUE;
                }
            }
        }
        return (n3 != 0) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void onresize() {
    }
    
    public void onResize() {
        if (this.Parent != null && this.Parent.bResizeDirty) {
            final double n = this.Parent.getWidth() - this.Parent.lastwidth;
            final double n2 = this.Parent.getHeight() - this.Parent.lastheight;
            if (!this.anchorTop && this.anchorBottom) {
                this.setY(this.getY() + n2);
            }
            if (this.anchorTop && this.anchorBottom) {
                this.setHeight(this.getHeight() + n2);
            }
            if (!this.anchorLeft && this.anchorRight) {
                this.setX(this.getX() + n);
            }
            if (this.anchorLeft && this.anchorRight) {
                this.setWidth(this.getWidth() + n);
            }
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"onResize") != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onResize"), (Object)this.table, (Object)this.getWidth(), (Object)this.getHeight());
        }
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            if (uiElement == null) {
                this.getControls().remove(i);
            }
            else {
                uiElement.onResize();
            }
        }
        this.bResizeDirty = false;
        this.lastwidth = this.getWidth();
        this.lastheight = this.getHeight();
    }
    
    public Boolean onRightMouseDown(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= n2) {
            return Boolean.FALSE;
        }
        if (this.maxDrawHeight != -1 && this.maxDrawHeight <= n2) {
            return Boolean.FALSE;
        }
        int n3 = 0;
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            if (n3 == 0 && ((n >= uiElement.getXScrolled(this) && n2 >= uiElement.getYScrolled(this) && n < uiElement.getXScrolled(this) + uiElement.getWidth() && n2 < uiElement.getYScrolled(this) + uiElement.getHeight()) || uiElement.isCapture())) {
                if (uiElement.onRightMouseDown(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue())) {
                    n3 = 1;
                }
            }
            else if (uiElement.getTable() != null && uiElement.getTable().rawget((Object)"onRightMouseDownOutside") != null) {
                LuaManager.caller.protectedCallVoid(UIManager.getDefaultThread(), uiElement.getTable().rawget((Object)"onRightMouseDownOutside"), (Object)uiElement.getTable(), (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
            }
        }
        if (this.getTable() != null) {
            if (n3 != 0) {
                if (this.getTable().rawget((Object)"onRightMouseDownOutside") != null) {
                    final Boolean protectedCallBoolean = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onRightMouseDownOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
                    if (protectedCallBoolean == null) {
                        return Boolean.TRUE;
                    }
                    if (protectedCallBoolean == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }
            else if (this.getTable().rawget((Object)"onRightMouseDown") != null) {
                final Boolean protectedCallBoolean2 = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onRightMouseDown"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
                if (protectedCallBoolean2 == null) {
                    return Boolean.TRUE;
                }
                if (protectedCallBoolean2 == Boolean.TRUE) {
                    return Boolean.TRUE;
                }
            }
        }
        return (n3 != 0) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public Boolean onRightMouseUp(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= n2) {
            return Boolean.FALSE;
        }
        if (this.maxDrawHeight != -1 && this.maxDrawHeight <= n2) {
            return Boolean.FALSE;
        }
        int n3 = 0;
        for (int i = this.getControls().size() - 1; i >= 0; --i) {
            final UIElement uiElement = this.getControls().get(i);
            if (n3 == 0 && ((n >= uiElement.getXScrolled(this) && n2 >= uiElement.getYScrolled(this) && n < uiElement.getXScrolled(this) + uiElement.getWidth() && n2 < uiElement.getYScrolled(this) + uiElement.getHeight()) || uiElement.isCapture())) {
                if (uiElement.onRightMouseUp(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue())) {
                    n3 = 1;
                }
            }
            else {
                uiElement.onRightMouseUpOutside(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue());
            }
        }
        if (this.getTable() != null) {
            if (n3 != 0) {
                if (this.getTable().rawget((Object)"onRightMouseUpOutside") != null) {
                    final Boolean protectedCallBoolean = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onRightMouseUpOutside"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
                    if (protectedCallBoolean == null) {
                        return Boolean.TRUE;
                    }
                    if (protectedCallBoolean == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }
            else if (this.getTable().rawget((Object)"onRightMouseUp") != null) {
                final Boolean protectedCallBoolean2 = LuaManager.caller.protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget((Object)"onRightMouseUp"), (Object)this.table, (Object)BoxedStaticValues.toDouble(n - this.xScroll), (Object)BoxedStaticValues.toDouble(n2 - this.yScroll));
                if (protectedCallBoolean2 == null) {
                    return Boolean.TRUE;
                }
                if (protectedCallBoolean2 == Boolean.TRUE) {
                    return Boolean.TRUE;
                }
            }
        }
        return (n3 != 0) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void RemoveControl(final UIElement o) {
        this.getControls().remove(o);
        o.setParent(null);
    }
    
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
        if (this.Parent != null && !this.Parent.bRenderClippedChildren) {
            final Double absoluteY = this.Parent.getAbsoluteY();
            final double doubleValue = this.getAbsoluteY();
            if (doubleValue + this.getHeight() <= absoluteY || doubleValue >= absoluteY + this.getParent().getHeight()) {
                return;
            }
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"prerender") != null) {
            try {
                LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"prerender"), (Object)this.table);
            }
            catch (Exception ex) {}
        }
        for (int i = 0; i < this.getControls().size(); ++i) {
            this.getControls().get(i).render();
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"render") != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"render"), (Object)this.table);
        }
        if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
            if (this.table != null && "ISScrollingListBox".equals(this.table.rawget((Object)"Type"))) {
                this.repaintStencilRect(0.0, 0.0, (int)this.width, (int)this.height);
            }
            final Double value = -this.getXScroll();
            final Double value2 = -this.getYScroll();
            double n = 1.0;
            if (this.isMouseOver()) {
                n = 0.0;
            }
            final double n2 = (this.maxDrawHeight == -1) ? this.height : ((double)this.maxDrawHeight);
            this.DrawTextureScaledColor(null, value, value2, 1.0, n2, n, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + 1.0, value2, this.width - 2.0, 1.0, n, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + this.width - 1.0, value2, 1.0, n2, n, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + 1.0, value2 + n2 - 1.0, this.width - 2.0, 1.0, n, 1.0, 1.0, 0.5);
        }
    }
    
    public void update() {
        if (!this.enabled) {
            return;
        }
        for (int i = 0; i < this.Controls.size(); ++i) {
            if (this.toTop.contains(this.Controls.get(i))) {
                final UIElement e = this.Controls.remove(i);
                --i;
                UIElement.toAdd.add(e);
            }
        }
        this.Controls.addAll(UIElement.toAdd);
        UIElement.toAdd.clear();
        this.toTop.clear();
        if (UIManager.doTick && this.getTable() != null && this.getTable().rawget((Object)"update") != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget((Object)"update"), (Object)this.table);
        }
        if (this.bResizeDirty) {
            this.onResize();
            this.lastwidth = this.width;
            this.lastheight = this.height;
            this.bResizeDirty = false;
        }
        for (int j = 0; j < this.getControls().size(); ++j) {
            this.getControls().get(j).update();
        }
    }
    
    public void BringToTop(final UIElement uiElement) {
        this.getControls().remove(uiElement);
        this.getControls().add(uiElement);
    }
    
    public Boolean isCapture() {
        return this.capture ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setCapture(final boolean capture) {
        this.capture = capture;
    }
    
    public Boolean isIgnoreLossControl() {
        return this.IgnoreLossControl ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setIgnoreLossControl(final boolean ignoreLossControl) {
        this.IgnoreLossControl = ignoreLossControl;
    }
    
    public ArrayList<UIElement> getControls() {
        return this.Controls;
    }
    
    public void setControls(final Vector<UIElement> controls) {
        this.setControls(controls);
    }
    
    public Boolean isDefaultDraw() {
        return this.defaultDraw ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setDefaultDraw(final boolean defaultDraw) {
        this.defaultDraw = defaultDraw;
    }
    
    public Boolean isFollowGameWorld() {
        return this.followGameWorld ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setFollowGameWorld(final boolean followGameWorld) {
        this.followGameWorld = followGameWorld;
    }
    
    public int getRenderThisPlayerOnly() {
        return this.renderThisPlayerOnly;
    }
    
    public void setRenderThisPlayerOnly(final int renderThisPlayerOnly) {
        this.renderThisPlayerOnly = renderThisPlayerOnly;
    }
    
    public Double getHeight() {
        return BoxedStaticValues.toDouble(this.height);
    }
    
    public void setHeight(final double n) {
        if (this.height != n) {
            this.bResizeDirty = true;
        }
        this.lastheight = this.height;
        this.height = (float)n;
    }
    
    public UIElement getParent() {
        return this.Parent;
    }
    
    public void setParent(final UIElement parent) {
        this.Parent = parent;
    }
    
    public Boolean isVisible() {
        return this.visible ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public Double getWidth() {
        return BoxedStaticValues.toDouble(this.width);
    }
    
    public void setWidth(final double n) {
        if (this.width != n) {
            this.bResizeDirty = true;
        }
        this.lastwidth = this.width;
        this.width = (float)n;
    }
    
    public Double getX() {
        return BoxedStaticValues.toDouble(this.x);
    }
    
    public void setX(final double n) {
        this.x = (float)n;
    }
    
    public Double getXScrolled(final UIElement uiElement) {
        if (uiElement != null && uiElement.bScrollChildren && this.bScrollWithParent) {
            return BoxedStaticValues.toDouble(this.x + uiElement.getXScroll());
        }
        return BoxedStaticValues.toDouble(this.x);
    }
    
    public Double getYScrolled(final UIElement uiElement) {
        if (uiElement != null && uiElement.bScrollChildren && this.bScrollWithParent) {
            return BoxedStaticValues.toDouble(this.y + uiElement.getYScroll());
        }
        return BoxedStaticValues.toDouble(this.y);
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public Double getY() {
        return BoxedStaticValues.toDouble(this.y);
    }
    
    public void setY(final double n) {
        this.y = (float)n;
    }
    
    public void suspendStencil() {
        IndieGL.disableStencilTest();
        IndieGL.disableAlphaTest();
    }
    
    public void resumeStencil() {
        IndieGL.enableStencilTest();
        IndieGL.enableAlphaTest();
    }
    
    public void setStencilRect(double n, double n2, final double n3, final double n4) {
        n += this.getAbsoluteX();
        n2 += this.getAbsoluteY();
        IndieGL.glStencilMask(255);
        IndieGL.enableStencilTest();
        IndieGL.enableAlphaTest();
        IndieGL.glStencilFunc(519, ++UIElement.StencilLevel, 255);
        IndieGL.glStencilOp(7680, 7680, 7681);
        IndieGL.glColorMask(false, false, false, false);
        SpriteRenderer.instance.renderi(null, (int)n, (int)n2, (int)n3, (int)n4, 1.0f, 0.0f, 0.0f, 1.0f, null);
        IndieGL.glColorMask(true, true, true, true);
        IndieGL.glStencilOp(7680, 7680, 7680);
        IndieGL.glStencilFunc(514, UIElement.StencilLevel, 255);
    }
    
    public void clearStencilRect() {
        if (UIElement.StencilLevel > 0) {
            --UIElement.StencilLevel;
        }
        if (UIElement.StencilLevel > 0) {
            IndieGL.glStencilFunc(514, UIElement.StencilLevel, 255);
        }
        else {
            IndieGL.glAlphaFunc(519, 0.0f);
            IndieGL.disableStencilTest();
            IndieGL.disableAlphaTest();
            IndieGL.glStencilFunc(519, 255, 255);
            IndieGL.glStencilOp(7680, 7680, 7680);
            IndieGL.glClear(1280);
        }
    }
    
    public void repaintStencilRect(double n, double n2, final double n3, final double n4) {
        if (UIElement.StencilLevel <= 0) {
            return;
        }
        n += this.getAbsoluteX();
        n2 += this.getAbsoluteY();
        IndieGL.glStencilFunc(519, UIElement.StencilLevel, 255);
        IndieGL.glStencilOp(7680, 7680, 7681);
        IndieGL.glColorMask(false, false, false, false);
        SpriteRenderer.instance.renderi(null, (int)n, (int)n2, (int)n3, (int)n4, 1.0f, 0.0f, 0.0f, 1.0f, null);
        IndieGL.glColorMask(true, true, true, true);
        IndieGL.glStencilOp(7680, 7680, 7680);
        IndieGL.glStencilFunc(514, UIElement.StencilLevel, 255);
    }
    
    public KahluaTable getTable() {
        return this.table;
    }
    
    public void setTable(final KahluaTable table) {
        this.table = table;
    }
    
    public void setHeightSilent(final double n) {
        this.lastheight = this.height;
        this.height = (float)n;
    }
    
    public void setWidthSilent(final double n) {
        this.lastwidth = this.width;
        this.width = (float)n;
    }
    
    public void setHeightOnly(final double n) {
        this.height = (float)n;
    }
    
    public void setWidthOnly(final double n) {
        this.width = (float)n;
    }
    
    public boolean isAnchorTop() {
        return this.anchorTop;
    }
    
    public void setAnchorTop(final boolean anchorTop) {
        this.anchorTop = anchorTop;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }
    
    public void ignoreWidthChange() {
        this.lastwidth = this.width;
    }
    
    public void ignoreHeightChange() {
        this.lastheight = this.height;
    }
    
    public Boolean isAnchorLeft() {
        return this.anchorLeft ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setAnchorLeft(final boolean anchorLeft) {
        this.anchorLeft = anchorLeft;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }
    
    public Boolean isAnchorRight() {
        return this.anchorRight ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setAnchorRight(final boolean anchorRight) {
        this.anchorRight = anchorRight;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }
    
    public Boolean isAnchorBottom() {
        return this.anchorBottom ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public void setAnchorBottom(final boolean anchorBottom) {
        this.anchorBottom = anchorBottom;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }
    
    private void addBringToTop(final UIElement e) {
        this.toTop.add(e);
    }
    
    public int getPlayerContext() {
        return this.playerContext;
    }
    
    public void setPlayerContext(final int playerContext) {
        this.playerContext = playerContext;
    }
    
    public String getUIName() {
        return this.uiname;
    }
    
    public void setUIName(final String s) {
        this.uiname = ((s != null) ? s : "");
    }
    
    public Double clampToParentX(double n) {
        if (this.getParent() == null) {
            return BoxedStaticValues.toDouble(n);
        }
        final double doubleValue = this.getParent().clampToParentX(this.getParent().getAbsoluteX());
        final double doubleValue2 = this.getParent().clampToParentX(doubleValue + this.getParent().getWidth().intValue());
        if (n < doubleValue) {
            n = doubleValue;
        }
        if (n > doubleValue2) {
            n = doubleValue2;
        }
        return BoxedStaticValues.toDouble(n);
    }
    
    public Double clampToParentY(double n) {
        if (this.getParent() == null) {
            return n;
        }
        final double doubleValue = this.getParent().clampToParentY(this.getParent().getAbsoluteY());
        final double doubleValue2 = this.getParent().clampToParentY(doubleValue + this.getParent().getHeight().intValue());
        if (n < doubleValue) {
            n = doubleValue;
        }
        if (n > doubleValue2) {
            n = doubleValue2;
        }
        return n;
    }
    
    public Boolean isPointOver(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        int a = this.getHeight().intValue();
        if (this.maxDrawHeight != -1) {
            a = Math.min(a, this.maxDrawHeight);
        }
        final double n3 = n - this.getAbsoluteX();
        final double n4 = n2 - this.getAbsoluteY();
        if (n3 < 0.0 || n3 >= this.getWidth() || n4 < 0.0 || n4 >= a) {
            return Boolean.FALSE;
        }
        if (this.Parent == null) {
            final ArrayList<UIElement> ui = UIManager.getUI();
            for (int i = ui.size() - 1; i >= 0; --i) {
                final UIElement uiElement = ui.get(i);
                if (uiElement == this) {
                    break;
                }
                if (uiElement.isPointOver(n, n2)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }
        for (int j = this.Parent.Controls.size() - 1; j >= 0; --j) {
            final UIElement uiElement2 = this.Parent.Controls.get(j);
            if (uiElement2 == this) {
                break;
            }
            if (uiElement2.isVisible()) {
                int a2 = uiElement2.getHeight().intValue();
                if (uiElement2.maxDrawHeight != -1) {
                    a2 = Math.min(a2, uiElement2.maxDrawHeight);
                }
                final double n5 = n - uiElement2.getAbsoluteX();
                final double n6 = n2 - uiElement2.getAbsoluteY();
                if (n5 >= 0.0 && n5 < uiElement2.getWidth() && n6 >= 0.0 && n6 < a2) {
                    return Boolean.FALSE;
                }
            }
        }
        return ((boolean)this.Parent.isPointOver(n, n2)) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public Boolean isMouseOver() {
        return ((boolean)this.isPointOver(Mouse.getXA(), Mouse.getYA())) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    protected Object tryGetTableValue(final String s) {
        if (this.getTable() == null) {
            return null;
        }
        return this.getTable().rawget((Object)s);
    }
    
    public void setWantKeyEvents(final boolean bWantKeyEvents) {
        this.bWantKeyEvents = bWantKeyEvents;
    }
    
    public boolean isWantKeyEvents() {
        return this.bWantKeyEvents;
    }
    
    public boolean isKeyConsumed(final int n) {
        final Object tryGetTableValue = this.tryGetTableValue("isKeyConsumed");
        return tryGetTableValue != null && LuaManager.caller.pcallBoolean(UIManager.getDefaultThread(), tryGetTableValue, (Object)this.getTable(), (Object)BoxedStaticValues.toDouble(n));
    }
    
    public void onKeyPress(final int n) {
        final Object tryGetTableValue = this.tryGetTableValue("onKeyPress");
        if (tryGetTableValue == null) {
            return;
        }
        LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), tryGetTableValue, (Object)this.getTable(), (Object)BoxedStaticValues.toDouble(n));
    }
    
    public void onKeyRepeat(final int n) {
        final Object tryGetTableValue = this.tryGetTableValue("onKeyRepeat");
        if (tryGetTableValue == null) {
            return;
        }
        LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), tryGetTableValue, (Object)this.getTable(), (Object)BoxedStaticValues.toDouble(n));
    }
    
    public void onKeyRelease(final int n) {
        final Object tryGetTableValue = this.tryGetTableValue("onKeyRelease");
        if (tryGetTableValue == null) {
            return;
        }
        LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), tryGetTableValue, (Object)this.getTable(), (Object)BoxedStaticValues.toDouble(n));
    }
    
    public boolean isForceCursorVisible() {
        return this.bForceCursorVisible;
    }
    
    public void setForceCursorVisible(final boolean bForceCursorVisible) {
        this.bForceCursorVisible = bForceCursorVisible;
    }
    
    static {
        tempcol = new Color(0, 0, 0, 0);
        toAdd = new ArrayList<UIElement>(0);
        UIElement.StencilLevel = 0;
    }
}
