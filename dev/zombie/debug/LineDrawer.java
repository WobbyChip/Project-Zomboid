// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

import zombie.iso.PlayerCamera;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.core.textures.Texture;
import zombie.iso.Vector2;
import java.util.ArrayDeque;
import java.util.ArrayList;

public final class LineDrawer
{
    private static final long serialVersionUID = -8792265397633463907L;
    public static int red;
    public static int green;
    public static int blue;
    public static int alpha;
    static int idLayer;
    static final ArrayList<DrawableLine> lines;
    static final ArrayDeque<DrawableLine> pool;
    private static int layer;
    static final Vector2 tempo;
    static final Vector2 tempo2;
    
    static void DrawTexturedRect(final Texture texture, float n, float n2, final float n3, final float n4, final int n5, final float n6, final float n7, final float n8) {
        n = (float)(int)n;
        n2 = (float)(int)n2;
        final Vector2 vector2 = new Vector2(n, n2);
        final Vector2 vector3 = new Vector2(n + n3, n2);
        final Vector2 vector4 = new Vector2(n + n3, n2 + n4);
        final Vector2 vector5 = new Vector2(n, n2 + n4);
        final Vector2 vector6 = new Vector2(IsoUtils.XToScreen(vector2.x, vector2.y, (float)n5, 0), IsoUtils.YToScreen(vector2.x, vector2.y, (float)n5, 0));
        final Vector2 vector7 = new Vector2(IsoUtils.XToScreen(vector3.x, vector3.y, (float)n5, 0), IsoUtils.YToScreen(vector3.x, vector3.y, (float)n5, 0));
        final Vector2 vector8 = new Vector2(IsoUtils.XToScreen(vector4.x, vector4.y, (float)n5, 0), IsoUtils.YToScreen(vector4.x, vector4.y, (float)n5, 0));
        final Vector2 vector9 = new Vector2(IsoUtils.XToScreen(vector5.x, vector5.y, (float)n5, 0), IsoUtils.YToScreen(vector5.x, vector5.y, (float)n5, 0));
        final PlayerCamera playerCamera = IsoCamera.cameras[IsoPlayer.getPlayerIndex()];
        final Vector2 vector10 = vector6;
        vector10.x -= playerCamera.OffX;
        final Vector2 vector11 = vector7;
        vector11.x -= playerCamera.OffX;
        final Vector2 vector12 = vector8;
        vector12.x -= playerCamera.OffX;
        final Vector2 vector13 = vector9;
        vector13.x -= playerCamera.OffX;
        final Vector2 vector14 = vector6;
        vector14.y -= playerCamera.OffY;
        final Vector2 vector15 = vector7;
        vector15.y -= playerCamera.OffY;
        final Vector2 vector16 = vector8;
        vector16.y -= playerCamera.OffY;
        final Vector2 vector17 = vector9;
        vector17.y -= playerCamera.OffY;
        final float n9 = -240.0f - 128.0f;
        final float n10 = -32.0f;
        final Vector2 vector18 = vector6;
        vector18.y -= n9;
        final Vector2 vector19 = vector7;
        vector19.y -= n9;
        final Vector2 vector20 = vector8;
        vector20.y -= n9;
        final Vector2 vector21 = vector9;
        vector21.y -= n9;
        final Vector2 vector22 = vector6;
        vector22.x -= n10;
        final Vector2 vector23 = vector7;
        vector23.x -= n10;
        final Vector2 vector24 = vector8;
        vector24.x -= n10;
        final Vector2 vector25 = vector9;
        vector25.x -= n10;
        SpriteRenderer.instance.renderdebug(texture, vector6.x, vector6.y, vector7.x, vector7.y, vector8.x, vector8.y, vector9.x, vector9.y, n6, n7, n8, 1.0f, n6, n7, n8, 1.0f, n6, n7, n8, 1.0f, n6, n7, n8, 1.0f, null);
    }
    
    static void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        LineDrawer.tempo.set(n, n2);
        LineDrawer.tempo2.set(n3, n4);
        final Vector2 vector2 = new Vector2(IsoUtils.XToScreen(LineDrawer.tempo.x, LineDrawer.tempo.y, 0.0f, 0), IsoUtils.YToScreen(LineDrawer.tempo.x, LineDrawer.tempo.y, 0.0f, 0));
        final Vector2 vector3 = new Vector2(IsoUtils.XToScreen(LineDrawer.tempo2.x, LineDrawer.tempo2.y, 0.0f, 0), IsoUtils.YToScreen(LineDrawer.tempo2.x, LineDrawer.tempo2.y, 0.0f, 0));
        final Vector2 vector4 = vector2;
        vector4.x -= IsoCamera.getOffX();
        final Vector2 vector5 = vector3;
        vector5.x -= IsoCamera.getOffX();
        final Vector2 vector6 = vector2;
        vector6.y -= IsoCamera.getOffY();
        final Vector2 vector7 = vector3;
        vector7.y -= IsoCamera.getOffY();
        drawLine(vector2.x, vector2.y, vector3.x, vector3.y, n5, n6, n7, n8, n9);
    }
    
    public static void DrawIsoRect(float n, float n2, float n3, float n4, final int n5, final float n6, final float n7, final float n8) {
        if (n3 < 0.0f) {
            n3 = -n3;
            n -= n3;
        }
        if (n4 < 0.0f) {
            n4 = -n4;
            n2 -= n4;
        }
        final float xToScreenExact = IsoUtils.XToScreenExact(n, n2, (float)n5, 0);
        final float yToScreenExact = IsoUtils.YToScreenExact(n, n2, (float)n5, 0);
        final float xToScreenExact2 = IsoUtils.XToScreenExact(n + n3, n2, (float)n5, 0);
        final float yToScreenExact2 = IsoUtils.YToScreenExact(n + n3, n2, (float)n5, 0);
        final float xToScreenExact3 = IsoUtils.XToScreenExact(n + n3, n2 + n4, (float)n5, 0);
        final float yToScreenExact3 = IsoUtils.YToScreenExact(n + n3, n2 + n4, (float)n5, 0);
        final float xToScreenExact4 = IsoUtils.XToScreenExact(n, n2 + n4, (float)n5, 0);
        final float yToScreenExact4 = IsoUtils.YToScreenExact(n, n2 + n4, (float)n5, 0);
        drawLine(xToScreenExact, yToScreenExact, xToScreenExact2, yToScreenExact2, n6, n7, n8);
        drawLine(xToScreenExact2, yToScreenExact2, xToScreenExact3, yToScreenExact3, n6, n7, n8);
        drawLine(xToScreenExact3, yToScreenExact3, xToScreenExact4, yToScreenExact4, n6, n7, n8);
        drawLine(xToScreenExact4, yToScreenExact4, xToScreenExact, yToScreenExact, n6, n7, n8);
    }
    
    public static void DrawIsoRectRotated(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10) {
        final Vector2 setLengthAndDirection = LineDrawer.tempo.setLengthAndDirection(n6, 1.0f);
        final Vector2 set = LineDrawer.tempo2.set(setLengthAndDirection);
        set.tangent();
        final Vector2 vector2 = setLengthAndDirection;
        vector2.x *= n5 / 2.0f;
        final Vector2 vector3 = setLengthAndDirection;
        vector3.y *= n5 / 2.0f;
        final Vector2 vector4 = set;
        vector4.x *= n4 / 2.0f;
        final Vector2 vector5 = set;
        vector5.y *= n4 / 2.0f;
        final float n11 = n + setLengthAndDirection.x;
        final float n12 = n2 + setLengthAndDirection.y;
        final float n13 = n - setLengthAndDirection.x;
        final float n14 = n2 - setLengthAndDirection.y;
        final float n15 = n11 - set.x;
        final float n16 = n12 - set.y;
        final float n17 = n11 + set.x;
        final float n18 = n12 + set.y;
        final float n19 = n13 - set.x;
        final float n20 = n14 - set.y;
        final float n21 = n13 + set.x;
        final float n22 = n14 + set.y;
        final int n23 = 1;
        DrawIsoLine(n15, n16, n3, n17, n18, n3, n7, n8, n9, n10, n23);
        DrawIsoLine(n15, n16, n3, n19, n20, n3, n7, n8, n9, n10, n23);
        DrawIsoLine(n17, n18, n3, n21, n22, n3, n7, n8, n9, n10, n23);
        DrawIsoLine(n19, n20, n3, n21, n22, n3, n7, n8, n9, n10, n23);
    }
    
    public static void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final int n11) {
        drawLine(IsoUtils.XToScreenExact(n, n2, n3, 0), IsoUtils.YToScreenExact(n, n2, n3, 0), IsoUtils.XToScreenExact(n4, n5, n6, 0), IsoUtils.YToScreenExact(n4, n5, n6, 0), n7, n8, n9, n10, n11);
    }
    
    public static void DrawIsoTransform(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final int n7, final float n8, final float n9, final float n10, final float n11, final int n12) {
        DrawIsoCircle(n, n2, n3, n6, n7, n8, n9, n10, n11);
        DrawIsoLine(n, n2, n3, n + n4 + n6 / 2.0f, n2 + n5 + n6 / 2.0f, n3, n8, n9, n10, n11, n12);
    }
    
    public static void DrawIsoCircle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        DrawIsoCircle(n, n2, n3, n4, 16, n5, n6, n7, n8);
    }
    
    public static void DrawIsoCircle(final float n, final float n2, final float n3, final float n4, final int n5, final float n6, final float n7, final float n8, final float n9) {
        double n10 = n + n4 * Math.cos(Math.toRadians(0.0f / n5));
        double n11 = n2 + n4 * Math.sin(Math.toRadians(0.0f / n5));
        for (int i = 1; i <= n5; ++i) {
            final double n12 = n + n4 * Math.cos(Math.toRadians(i * 360.0f / n5));
            final double n13 = n2 + n4 * Math.sin(Math.toRadians(i * 360.0f / n5));
            addLine((float)n10, (float)n11, n3, (float)n12, (float)n13, n3, n6, n7, n8, n9);
            n10 = n12;
            n11 = n13;
        }
    }
    
    static void drawLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        SpriteRenderer.instance.renderline(null, (int)n - 1, (int)n2 - 1, (int)n3 - 1, (int)n4 - 1, 0.0f, 0.0f, 0.0f, 0.5f);
        SpriteRenderer.instance.renderline(null, (int)n, (int)n2, (int)n3, (int)n4, n5, n6, n7, 1.0f);
    }
    
    public static void drawLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        SpriteRenderer.instance.renderline(null, (int)n, (int)n2, (int)n3, (int)n4, n5, n6, n7, n8);
    }
    
    public static void drawRect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        SpriteRenderer.instance.render(null, n, n2 + n9, (float)n9, n4 - n9 * 2, n5, n6, n7, n8, null);
        SpriteRenderer.instance.render(null, n, n2, n3, (float)n9, n5, n6, n7, n8, null);
        SpriteRenderer.instance.render(null, n + n3 - n9, n2 + n9, 1.0f, n4 - n9 * 2, n5, n6, n7, n8, null);
        SpriteRenderer.instance.render(null, n, n2 + n4 - n9, n3, (float)n9, n5, n6, n7, n8, null);
    }
    
    public static void drawArc(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final int n7, final float n8, final float n9, final float n10, final float n11) {
        final float n12 = n5 + (float)Math.acos(n6);
        final float n13 = n5 - (float)Math.acos(n6);
        float n14 = n + (float)Math.cos(n12) * n4;
        float n15 = n2 + (float)Math.sin(n12) * n4;
        for (int i = 1; i <= n7; ++i) {
            final float n16 = n12 + (n13 - n12) * i / n7;
            final float n17 = n + (float)Math.cos(n16) * n4;
            final float n18 = n2 + (float)Math.sin(n16) * n4;
            DrawIsoLine(n14, n15, n3, n17, n18, n3, n8, n9, n10, n11, 1);
            n14 = n17;
            n15 = n18;
        }
    }
    
    public static void drawCircle(final float n, final float n2, final float n3, final int n4, final float n5, final float n6, final float n7) {
        double n8 = n + n3 * Math.cos(Math.toRadians(0.0f / n4));
        double n9 = n2 + n3 * Math.sin(Math.toRadians(0.0f / n4));
        for (int i = 1; i <= n4; ++i) {
            final double n10 = n + n3 * Math.cos(Math.toRadians(i * 360.0f / n4));
            final double n11 = n2 + n3 * Math.sin(Math.toRadians(i * 360.0f / n4));
            drawLine((float)n8, (float)n9, (float)n10, (float)n11, n5, n6, n7, 1.0f, 1);
            n8 = n10;
            n9 = n11;
        }
    }
    
    public static void drawDirectionLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final int n10) {
        DrawIsoLine(n, n2, n3, n + (float)Math.cos(n5) * n4, n2 + (float)Math.sin(n5) * n4, n3, n6, n7, n8, n9, n10);
    }
    
    public static void drawDotLines(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final int n11) {
        drawDirectionLine(n, n2, n3, n4, n5 + (float)Math.acos(n6), n7, n8, n9, n10, n11);
        drawDirectionLine(n, n2, n3, n4, n5 - (float)Math.acos(n6), n7, n8, n9, n10, n11);
    }
    
    public static void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10) {
        LineDrawer.lines.add((LineDrawer.pool.isEmpty() ? new DrawableLine() : LineDrawer.pool.pop()).init(n, n2, n3, n4, n5, n6, n7, n8, n9, n10));
    }
    
    public static void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final int n7, final int n8, final int n9, final String s) {
        addLine(n, n2, n3, n4, n5, n6, (float)n7, (float)n8, (float)n9, s, true);
    }
    
    public static void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final String s, final boolean b) {
        LineDrawer.lines.add((LineDrawer.pool.isEmpty() ? new DrawableLine() : LineDrawer.pool.pop()).init(n, n2, n3, n4, n5, n6, n7, n8, n9, s, b));
    }
    
    public static void addRect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        LineDrawer.lines.add((LineDrawer.pool.isEmpty() ? new DrawableLine() : LineDrawer.pool.pop()).init(n, n2, n3, n + n4, n2 + n5, n3, n6, n7, n8, null, false));
    }
    
    public static void clear() {
        if (LineDrawer.lines.isEmpty()) {
            return;
        }
        for (int i = 0; i < LineDrawer.lines.size(); ++i) {
            LineDrawer.pool.push(LineDrawer.lines.get(i));
        }
        LineDrawer.lines.clear();
    }
    
    public void removeLine(final String anObject) {
        for (int i = 0; i < LineDrawer.lines.size(); ++i) {
            if (LineDrawer.lines.get(i).name.equals(anObject)) {
                LineDrawer.lines.remove(LineDrawer.lines.get(i));
                --i;
            }
        }
    }
    
    public static void render() {
        for (int i = 0; i < LineDrawer.lines.size(); ++i) {
            final DrawableLine drawableLine = LineDrawer.lines.get(i);
            if (!drawableLine.bLine) {
                DrawIsoRect(drawableLine.xstart, drawableLine.ystart, drawableLine.xend - drawableLine.xstart, drawableLine.yend - drawableLine.ystart, (int)drawableLine.zstart, drawableLine.red, drawableLine.green, drawableLine.blue);
            }
            else {
                DrawIsoLine(drawableLine.xstart, drawableLine.ystart, drawableLine.zstart, drawableLine.xend, drawableLine.yend, drawableLine.zend, drawableLine.red, drawableLine.green, drawableLine.blue, drawableLine.alpha, 1);
            }
        }
    }
    
    public static void drawLines() {
        clear();
    }
    
    static {
        LineDrawer.red = 0;
        LineDrawer.green = 255;
        LineDrawer.blue = 0;
        LineDrawer.alpha = 255;
        LineDrawer.idLayer = -1;
        lines = new ArrayList<DrawableLine>();
        pool = new ArrayDeque<DrawableLine>();
        tempo = new Vector2();
        tempo2 = new Vector2();
    }
    
    static class DrawableLine
    {
        public boolean bLine;
        String name;
        float red;
        float green;
        float blue;
        float alpha;
        float xstart;
        float ystart;
        float zstart;
        float xend;
        float yend;
        float zend;
        
        DrawableLine() {
            this.bLine = false;
        }
        
        public DrawableLine init(final float xstart, final float ystart, final float zstart, final float xend, final float yend, final float zend, final float red, final float green, final float blue, final String name) {
            this.xstart = xstart;
            this.ystart = ystart;
            this.zstart = zstart;
            this.xend = xend;
            this.yend = yend;
            this.zend = zend;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = 1.0f;
            this.name = name;
            return this;
        }
        
        public DrawableLine init(final float xstart, final float ystart, final float zstart, final float xend, final float yend, final float zend, final float red, final float green, final float blue, final String name, final boolean bLine) {
            this.xstart = xstart;
            this.ystart = ystart;
            this.zstart = zstart;
            this.xend = xend;
            this.yend = yend;
            this.zend = zend;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = 1.0f;
            this.name = name;
            this.bLine = bLine;
            return this;
        }
        
        public DrawableLine init(final float xstart, final float ystart, final float zstart, final float xend, final float yend, final float zend, final float red, final float green, final float blue, final float alpha) {
            this.xstart = xstart;
            this.ystart = ystart;
            this.zstart = zstart;
            this.xend = xend;
            this.yend = yend;
            this.zend = zend;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.name = null;
            this.bLine = true;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof DrawableLine) {
                return ((DrawableLine)o).name.equals(this.name);
            }
            return o.equals(this);
        }
    }
}
