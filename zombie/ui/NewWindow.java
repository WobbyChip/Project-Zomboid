// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Iterator;
import zombie.core.textures.Texture;
import org.lwjgl.util.Rectangle;
import java.util.Stack;

public class NewWindow extends UIElement
{
    public int clickX;
    public int clickY;
    public int clientH;
    public int clientW;
    public boolean Movable;
    public boolean moving;
    public int ncclientH;
    public int ncclientW;
    public Stack<Rectangle> nestedItems;
    public boolean ResizeToFitY;
    float alpha;
    Texture dialogBottomLeft;
    Texture dialogBottomMiddle;
    Texture dialogBottomRight;
    Texture dialogLeft;
    Texture dialogMiddle;
    Texture dialogRight;
    Texture titleCloseIcon;
    Texture titleLeft;
    Texture titleMiddle;
    Texture titleRight;
    HUDButton closeButton;
    
    public NewWindow(final int n, final int n2, int clientW, int clientH, final boolean b) {
        this.clickX = 0;
        this.clickY = 0;
        this.clientH = 0;
        this.clientW = 0;
        this.Movable = true;
        this.moving = false;
        this.ncclientH = 0;
        this.ncclientW = 0;
        this.nestedItems = new Stack<Rectangle>();
        this.ResizeToFitY = true;
        this.alpha = 1.0f;
        this.dialogBottomLeft = null;
        this.dialogBottomMiddle = null;
        this.dialogBottomRight = null;
        this.dialogLeft = null;
        this.dialogMiddle = null;
        this.dialogRight = null;
        this.titleCloseIcon = null;
        this.titleLeft = null;
        this.titleMiddle = null;
        this.titleRight = null;
        this.closeButton = null;
        this.x = n;
        this.y = n2;
        if (clientW < 156) {
            clientW = 156;
        }
        if (clientH < 78) {
            clientH = 78;
        }
        this.width = (float)clientW;
        this.height = (float)clientH;
        this.titleLeft = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Left.png");
        this.titleMiddle = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Middle.png");
        this.titleRight = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Right.png");
        this.dialogLeft = Texture.getSharedTexture("media/ui/Dialog_Left.png");
        this.dialogMiddle = Texture.getSharedTexture("media/ui/Dialog_Middle.png");
        this.dialogRight = Texture.getSharedTexture("media/ui/Dialog_Right.png");
        this.dialogBottomLeft = Texture.getSharedTexture("media/ui/Dialog_Bottom_Left.png");
        this.dialogBottomMiddle = Texture.getSharedTexture("media/ui/Dialog_Bottom_Middle.png");
        this.dialogBottomRight = Texture.getSharedTexture("media/ui/Dialog_Bottom_Right.png");
        if (b) {
            this.AddChild(this.closeButton = new HUDButton("close", (float)(clientW - 16), 2.0f, "media/ui/Dialog_Titlebar_CloseIcon.png", "media/ui/Dialog_Titlebar_CloseIcon.png", "media/ui/Dialog_Titlebar_CloseIcon.png", this));
        }
        this.clientW = clientW;
        this.clientH = clientH;
    }
    
    public void Nest(final UIElement uiElement, final int n, final int n2, final int n3, final int n4) {
        this.AddChild(uiElement);
        this.nestedItems.add(new Rectangle(n4, n, n2, n3));
        uiElement.setX(n4);
        uiElement.setY(n);
        uiElement.update();
    }
    
    @Override
    public void ButtonClicked(final String s) {
        super.ButtonClicked(s);
        if (s.equals("close")) {
            this.setVisible(false);
        }
    }
    
    @Override
    public Boolean onMouseDown(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        super.onMouseDown(n, n2);
        if (n2 < 18.0) {
            this.clickX = (int)n;
            this.clickY = (int)n2;
            if (this.Movable) {
                this.moving = true;
            }
            this.setCapture(true);
        }
        return Boolean.TRUE;
    }
    
    public void setMovable(final boolean movable) {
        this.Movable = movable;
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        super.onMouseMove(n, n2);
        if (this.moving) {
            this.setX(this.getX() + n);
            this.setY(this.getY() + n2);
        }
        return Boolean.FALSE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        if (!this.isVisible()) {
            return;
        }
        super.onMouseMoveOutside(n, n2);
        if (this.moving) {
            this.setX(this.getX() + n);
            this.setY(this.getY() + n2);
        }
    }
    
    @Override
    public Boolean onMouseUp(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        super.onMouseUp(n, n2);
        this.setCapture(this.moving = false);
        return Boolean.TRUE;
    }
    
    @Override
    public void render() {
        final float n = 0.8f * this.alpha;
        final int n2 = 0;
        final int n3 = 0;
        this.DrawTexture(this.titleLeft, n2, n3, n);
        this.DrawTexture(this.titleRight, this.getWidth() - this.titleRight.getWidth(), n3, n);
        this.DrawTextureScaled(this.titleMiddle, this.titleLeft.getWidth(), n3, this.getWidth() - this.titleLeft.getWidth() * 2, this.titleMiddle.getHeight(), n);
        final int n4 = n3 + this.titleRight.getHeight();
        this.DrawTextureScaled(this.dialogLeft, n2, n4, this.dialogLeft.getWidth(), this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight(), n);
        this.DrawTextureScaled(this.dialogMiddle, this.dialogLeft.getWidth(), n4, this.getWidth() - this.dialogRight.getWidth() * 2, this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight(), n);
        this.DrawTextureScaled(this.dialogRight, this.getWidth() - this.dialogRight.getWidth(), n4, this.dialogLeft.getWidth(), this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight(), n);
        final int n5 = (int)(n4 + (this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight()));
        this.DrawTextureScaled(this.dialogBottomMiddle, this.dialogBottomLeft.getWidth(), n5, this.getWidth() - this.dialogBottomLeft.getWidth() * 2, this.dialogBottomMiddle.getHeight(), n);
        this.DrawTexture(this.dialogBottomLeft, n2, n5, n);
        this.DrawTexture(this.dialogBottomRight, this.getWidth() - this.dialogBottomRight.getWidth(), n5, n);
        super.render();
    }
    
    @Override
    public void update() {
        super.update();
        if (this.closeButton != null) {
            this.closeButton.setX(4.0);
            this.closeButton.setY(3.0);
        }
        int n = 0;
        if (!this.ResizeToFitY) {
            for (final Rectangle rectangle : this.nestedItems) {
                final UIElement uiElement = this.getControls().get(n);
                if (uiElement != this.closeButton) {
                    uiElement.setX(rectangle.getX());
                    uiElement.setY(rectangle.getY());
                    uiElement.setWidth(this.clientW - (rectangle.getX() + rectangle.getWidth()));
                    uiElement.setHeight(this.clientH - (rectangle.getY() + rectangle.getHeight()));
                    uiElement.onresize();
                    ++n;
                }
            }
        }
        else {
            int intValue = 100000;
            int intValue2 = 100000;
            float n2 = 0.0f;
            float n3 = 0.0f;
            for (final Rectangle rectangle2 : this.nestedItems) {
                final UIElement uiElement2 = this.getControls().get(n);
                if (uiElement2 != this.closeButton) {
                    if (intValue > uiElement2.getAbsoluteX()) {
                        intValue = uiElement2.getAbsoluteX().intValue();
                    }
                    if (intValue2 > uiElement2.getAbsoluteX()) {
                        intValue2 = uiElement2.getAbsoluteX().intValue();
                    }
                    if (n2 < uiElement2.getWidth()) {
                        n2 = (float)uiElement2.getWidth().intValue();
                    }
                    if (n3 < uiElement2.getHeight()) {
                        n3 = (float)uiElement2.getHeight().intValue();
                    }
                    ++n;
                }
            }
            this.height = n3 + 50.0f;
        }
    }
}
