// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.input.Mouse;
import zombie.core.textures.Texture;
import zombie.core.Color;

public final class ScrollBar extends UIElement
{
    public final Color BackgroundColour;
    public final Color ButtonColour;
    public final Color ButtonHighlightColour;
    public boolean IsVerticle;
    private int FullLength;
    private int InsideLength;
    private int EndLength;
    private float ButtonInsideLength;
    private int ButtonEndLength;
    private int Thickness;
    private int ButtonThickness;
    private float ButtonOffset;
    private int MouseDragStartPos;
    private float ButtonDragStartPos;
    private Texture BackVertical;
    private Texture TopVertical;
    private Texture BottomVertical;
    private Texture ButtonBackVertical;
    private Texture ButtonTopVertical;
    private Texture ButtonBottomVertical;
    private Texture BackHorizontal;
    private Texture LeftHorizontal;
    private Texture RightHorizontal;
    private Texture ButtonBackHorizontal;
    private Texture ButtonLeftHorizontal;
    private Texture ButtonRightHorizontal;
    private boolean mouseOver;
    private boolean BeingDragged;
    private UITextBox2 ParentTextBox;
    UIEventHandler messageParent;
    private String name;
    
    public ScrollBar(final String name, final UIEventHandler messageParent, final int n, final int n2, final int fullLength, final boolean b) {
        this.BackgroundColour = new Color(255, 255, 255, 255);
        this.ButtonColour = new Color(255, 255, 255, 127);
        this.ButtonHighlightColour = new Color(255, 255, 255, 255);
        this.IsVerticle = true;
        this.FullLength = 114;
        this.InsideLength = 100;
        this.EndLength = 7;
        this.ButtonInsideLength = 30.0f;
        this.ButtonEndLength = 6;
        this.Thickness = 10;
        this.ButtonThickness = 9;
        this.ButtonOffset = 40.0f;
        this.MouseDragStartPos = 0;
        this.ButtonDragStartPos = 0.0f;
        this.mouseOver = false;
        this.BeingDragged = false;
        this.ParentTextBox = null;
        this.messageParent = messageParent;
        this.name = name;
        this.x = (float)n;
        this.y = (float)n2;
        this.FullLength = fullLength;
        this.InsideLength = fullLength - this.EndLength * 2;
        this.IsVerticle = true;
        this.width = (float)this.Thickness;
        this.height = (float)fullLength;
        this.ButtonInsideLength = this.height - this.ButtonEndLength * 2;
        this.ButtonOffset = 0.0f;
        this.BackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Middle.png");
        this.TopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Top.png");
        this.BottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Bottom.png");
        this.ButtonBackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Middle.png");
        this.ButtonTopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Top.png");
        this.ButtonBottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bottom.png");
        this.BackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Middle.png");
        this.LeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Bottom.png");
        this.RightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Top.png");
        this.ButtonBackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Middle.png");
        this.ButtonLeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bottom.png");
        this.ButtonRightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Top.png");
    }
    
    public void SetParentTextBox(final UITextBox2 parentTextBox) {
        this.ParentTextBox = parentTextBox;
    }
    
    @Override
    public void setHeight(final double height) {
        super.setHeight(height);
        this.FullLength = (int)height;
        this.InsideLength = (int)height - this.EndLength * 2;
    }
    
    @Override
    public void render() {
        if (this.IsVerticle) {
            this.DrawTextureScaledCol(this.TopVertical, 0.0, 0.0, this.Thickness, this.EndLength, this.BackgroundColour);
            this.DrawTextureScaledCol(this.BackVertical, 0.0, 0 + this.EndLength, this.Thickness, this.InsideLength, this.BackgroundColour);
            this.DrawTextureScaledCol(this.BottomVertical, 0.0, 0 + this.EndLength + this.InsideLength, this.Thickness, this.EndLength, this.BackgroundColour);
            Color color;
            if (this.mouseOver) {
                color = this.ButtonHighlightColour;
            }
            else {
                color = this.ButtonColour;
            }
            this.DrawTextureScaledCol(this.ButtonTopVertical, 1.0, (int)this.ButtonOffset + 1, this.ButtonThickness, this.ButtonEndLength, color);
            this.DrawTextureScaledCol(this.ButtonBackVertical, 1.0, (int)this.ButtonOffset + 1 + this.ButtonEndLength, this.ButtonThickness, this.ButtonInsideLength, color);
            this.DrawTextureScaledCol(this.ButtonBottomVertical, 1.0, (int)this.ButtonOffset + 1 + this.ButtonEndLength + this.ButtonInsideLength, this.ButtonThickness, this.ButtonEndLength, color);
        }
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        this.mouseOver = true;
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        this.mouseOver = false;
    }
    
    @Override
    public Boolean onMouseUp(final double n, final double n2) {
        this.BeingDragged = false;
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean onMouseDown(final double n, final double n2) {
        boolean b = false;
        if (n2 >= this.ButtonOffset && n2 <= this.ButtonOffset + this.ButtonInsideLength + this.ButtonEndLength * 2) {
            b = true;
        }
        if (b) {
            this.BeingDragged = true;
            this.MouseDragStartPos = Mouse.getY();
            this.ButtonDragStartPos = this.ButtonOffset;
        }
        else {
            this.ButtonOffset = (float)(n2 - (this.ButtonInsideLength + this.ButtonEndLength * 2) / 2.0f);
        }
        if (this.ButtonOffset < 0.0f) {
            this.ButtonOffset = 0.0f;
        }
        if (this.ButtonOffset > this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 1.0f) {
            this.ButtonOffset = this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 1.0f;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public void update() {
        super.update();
        if (this.BeingDragged) {
            this.ButtonOffset = this.ButtonDragStartPos - (this.MouseDragStartPos - Mouse.getY());
            if (this.ButtonOffset < 0.0f) {
                this.ButtonOffset = 0.0f;
            }
            if (this.ButtonOffset > this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 0.0f) {
                this.ButtonOffset = this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 0.0f;
            }
            if (!Mouse.isButtonDown(0)) {
                this.BeingDragged = false;
            }
        }
        if (this.ParentTextBox != null) {
            final int lineHeight = TextManager.instance.getFontFromEnum(this.ParentTextBox.font).getLineHeight();
            if (this.ParentTextBox.Lines.size() > this.ParentTextBox.NumVisibleLines) {
                if (this.ParentTextBox.Lines.size() > 0) {
                    int numVisibleLines = this.ParentTextBox.NumVisibleLines;
                    if (numVisibleLines * lineHeight > this.ParentTextBox.getHeight().intValue() - this.ParentTextBox.getInset() * 2) {
                        --numVisibleLines;
                    }
                    this.ButtonInsideLength = (float)((int)(this.getHeight().intValue() * (numVisibleLines / (float)this.ParentTextBox.Lines.size())) - this.ButtonEndLength * 2);
                    this.ButtonInsideLength = Math.max(this.ButtonInsideLength, 0.0f);
                    final float n = this.ButtonInsideLength + this.ButtonEndLength * 2;
                    if (this.ButtonOffset < 0.0f) {
                        this.ButtonOffset = 0.0f;
                    }
                    if (this.ButtonOffset > this.getHeight().intValue() - n - 0.0f) {
                        this.ButtonOffset = this.getHeight().intValue() - n - 0.0f;
                    }
                    this.ParentTextBox.TopLineIndex = (int)(this.ParentTextBox.Lines.size() * (this.ButtonOffset / this.getHeight().intValue()));
                    this.ParentTextBox.TopLineIndex = (int)(this.ButtonOffset / ((this.getHeight().intValue() - (int)n) / (float)(lineHeight * (this.ParentTextBox.Lines.size() - numVisibleLines))) / lineHeight);
                }
                else {
                    this.ButtonOffset = 0.0f;
                    this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
                    this.ParentTextBox.TopLineIndex = 0;
                }
            }
            else {
                this.ButtonOffset = 0.0f;
                this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
                this.ParentTextBox.TopLineIndex = 0;
            }
        }
    }
    
    public void scrollToBottom() {
        this.ButtonOffset = this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 0.0f;
    }
}
