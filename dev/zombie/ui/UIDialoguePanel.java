// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Iterator;
import zombie.core.Color;
import java.awt.Rectangle;
import java.util.Stack;
import zombie.core.textures.Texture;

public final class UIDialoguePanel extends UIElement
{
    float alpha;
    Texture dialogBottomLeft;
    Texture dialogBottomMiddle;
    Texture dialogBottomRight;
    Texture dialogLeft;
    Texture dialogMiddle;
    Texture dialogRight;
    Texture titleLeft;
    Texture titleMiddle;
    Texture titleRight;
    public float clientH;
    public float clientW;
    public Stack<Rectangle> nestedItems;
    
    public UIDialoguePanel(final float n, final float n2, final float n3, final float n4) {
        this.alpha = 1.0f;
        this.dialogBottomLeft = null;
        this.dialogBottomMiddle = null;
        this.dialogBottomRight = null;
        this.dialogLeft = null;
        this.dialogMiddle = null;
        this.dialogRight = null;
        this.titleLeft = null;
        this.titleMiddle = null;
        this.titleRight = null;
        this.clientH = 0.0f;
        this.clientW = 0.0f;
        this.nestedItems = new Stack<Rectangle>();
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
        this.titleLeft = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Left.png");
        this.titleMiddle = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Middle.png");
        this.titleRight = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Right.png");
        this.dialogLeft = Texture.getSharedTexture("media/ui/Dialog_Left.png");
        this.dialogMiddle = Texture.getSharedTexture("media/ui/Dialog_Middle.png");
        this.dialogRight = Texture.getSharedTexture("media/ui/Dialog_Right.png");
        this.dialogBottomLeft = Texture.getSharedTexture("media/ui/Dialog_Bottom_Left.png");
        this.dialogBottomMiddle = Texture.getSharedTexture("media/ui/Dialog_Bottom_Middle.png");
        this.dialogBottomRight = Texture.getSharedTexture("media/ui/Dialog_Bottom_Right.png");
        this.clientW = n3;
        this.clientH = n4;
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
        this.DrawTextureScaledCol(this.titleLeft, 0.0, 0.0, 28.0, 28.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.titleMiddle, 28.0, 0.0, this.getWidth() - 56.0, 28.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.titleRight, 0.0 + this.getWidth() - 28.0, 0.0, 28.0, 28.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogLeft, 0.0, 28.0, 78.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogMiddle, 78.0, 28.0, this.getWidth() - 156.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogRight, 0.0 + this.getWidth() - 78.0, 28.0, 78.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogBottomLeft, 0.0, 0.0 + this.getHeight() - 72.0, 78.0, 72.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogBottomMiddle, 78.0, 0.0 + this.getHeight() - 72.0, this.getWidth() - 156.0, 72.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogBottomRight, 0.0 + this.getWidth() - 78.0, 0.0 + this.getHeight() - 72.0, 78.0, 72.0, new Color(255, 255, 255, 100));
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
}
