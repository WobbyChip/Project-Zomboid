// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Iterator;
import zombie.core.Color;
import java.awt.Rectangle;
import java.util.Stack;
import zombie.core.textures.Texture;

public final class UINineGrid extends UIElement
{
    Texture GridTopLeft;
    Texture GridTop;
    Texture GridTopRight;
    Texture GridLeft;
    Texture GridCenter;
    Texture GridRight;
    Texture GridBottomLeft;
    Texture GridBottom;
    Texture GridBottomRight;
    int TopWidth;
    int LeftWidth;
    int RightWidth;
    int BottomWidth;
    public int clientH;
    public int clientW;
    public Stack<Rectangle> nestedItems;
    public Color Colour;
    
    public UINineGrid(final int n, final int n2, final int clientW, final int clientH, final int topWidth, final int leftWidth, final int rightWidth, final int bottomWidth, final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final String s9) {
        this.GridTopLeft = null;
        this.GridTop = null;
        this.GridTopRight = null;
        this.GridLeft = null;
        this.GridCenter = null;
        this.GridRight = null;
        this.GridBottomLeft = null;
        this.GridBottom = null;
        this.GridBottomRight = null;
        this.TopWidth = 10;
        this.LeftWidth = 10;
        this.RightWidth = 10;
        this.BottomWidth = 10;
        this.clientH = 0;
        this.clientW = 0;
        this.nestedItems = new Stack<Rectangle>();
        this.Colour = new Color(50, 50, 50, 212);
        this.x = n;
        this.y = n2;
        this.width = (float)clientW;
        this.height = (float)clientH;
        this.TopWidth = topWidth;
        this.LeftWidth = leftWidth;
        this.RightWidth = rightWidth;
        this.BottomWidth = bottomWidth;
        this.GridTopLeft = Texture.getSharedTexture(s);
        this.GridTop = Texture.getSharedTexture(s2);
        this.GridTopRight = Texture.getSharedTexture(s3);
        this.GridLeft = Texture.getSharedTexture(s4);
        this.GridCenter = Texture.getSharedTexture(s5);
        this.GridRight = Texture.getSharedTexture(s6);
        this.GridBottomLeft = Texture.getSharedTexture(s7);
        this.GridBottom = Texture.getSharedTexture(s8);
        this.GridBottomRight = Texture.getSharedTexture(s9);
        this.clientW = clientW;
        this.clientH = clientH;
    }
    
    public void Nest(final UIElement uiElement, final int y, final int width, final int height, final int x) {
        this.AddChild(uiElement);
        this.nestedItems.add(new Rectangle(x, y, width, height));
        uiElement.setX(x);
        uiElement.setY(y);
        uiElement.update();
    }
    
    @Override
    public void render() {
        this.DrawTextureScaledCol(this.GridTopLeft, 0.0, 0.0, this.LeftWidth, this.TopWidth, this.Colour);
        this.DrawTextureScaledCol(this.GridTop, this.LeftWidth, 0.0, this.getWidth() - (this.LeftWidth + this.RightWidth), this.TopWidth, this.Colour);
        this.DrawTextureScaledCol(this.GridTopRight, this.getWidth() - this.RightWidth, 0.0, this.RightWidth, this.TopWidth, this.Colour);
        this.DrawTextureScaledCol(this.GridLeft, 0.0, this.TopWidth, this.LeftWidth, this.getHeight() - (this.TopWidth + this.BottomWidth), this.Colour);
        this.DrawTextureScaledCol(this.GridCenter, this.LeftWidth, this.TopWidth, this.getWidth() - (this.LeftWidth + this.RightWidth), this.getHeight() - (this.TopWidth + this.BottomWidth), this.Colour);
        this.DrawTextureScaledCol(this.GridRight, this.getWidth() - this.RightWidth, this.TopWidth, this.RightWidth, this.getHeight() - (this.TopWidth + this.BottomWidth), this.Colour);
        this.DrawTextureScaledCol(this.GridBottomLeft, 0.0, this.getHeight() - this.BottomWidth, this.LeftWidth, this.BottomWidth, this.Colour);
        this.DrawTextureScaledCol(this.GridBottom, this.LeftWidth, this.getHeight() - this.BottomWidth, this.getWidth() - (this.LeftWidth + this.RightWidth), this.BottomWidth, this.Colour);
        this.DrawTextureScaledCol(this.GridBottomRight, this.getWidth() - this.RightWidth, this.getHeight() - this.BottomWidth, this.RightWidth, this.BottomWidth, this.Colour);
        super.render();
    }
    
    @Override
    public void update() {
        super.update();
        int index = 0;
        for (final Rectangle rectangle : this.nestedItems) {
            final UIElement uiElement = this.getControls().get(index);
            uiElement.setX((float)rectangle.getX());
            uiElement.setY((float)rectangle.getY());
            uiElement.setWidth((int)(this.clientW - (rectangle.getX() + rectangle.getWidth())));
            uiElement.setHeight((int)(this.clientH - (rectangle.getY() + rectangle.getHeight())));
            uiElement.onresize();
            ++index;
        }
    }
    
    public void setAlpha(final float a) {
        this.Colour.a = a;
    }
    
    public float getAlpha() {
        return this.Colour.a;
    }
}
