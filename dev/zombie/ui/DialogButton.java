// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.Color;
import zombie.Lua.LuaManager;
import zombie.core.textures.Texture;

public final class DialogButton extends UIElement
{
    public boolean clicked;
    public UIElement MessageTarget;
    public boolean mouseOver;
    public String name;
    public String text;
    Texture downLeft;
    Texture downMid;
    Texture downRight;
    float origX;
    Texture upLeft;
    Texture upMid;
    Texture upRight;
    private UIEventHandler MessageTarget2;
    
    public DialogButton(final UIElement messageTarget, final float origX, final float n, final String text, final String name) {
        this.clicked = false;
        this.mouseOver = false;
        this.MessageTarget2 = null;
        this.x = origX;
        this.y = n;
        this.origX = origX;
        this.MessageTarget = messageTarget;
        this.upLeft = Texture.getSharedTexture("ButtonL_Up");
        this.upMid = Texture.getSharedTexture("ButtonM_Up");
        this.upRight = Texture.getSharedTexture("ButtonR_Up");
        this.downLeft = Texture.getSharedTexture("ButtonL_Down");
        this.downMid = Texture.getSharedTexture("ButtonM_Down");
        this.downRight = Texture.getSharedTexture("ButtonR_Down");
        this.name = name;
        this.text = text;
        this.width = (float)TextManager.instance.MeasureStringX(UIFont.Small, text);
        this.width += 8.0f;
        if (this.width < 40.0f) {
            this.width = 40.0f;
        }
        this.height = (float)this.downMid.getHeight();
        this.x -= this.width / 2.0f;
    }
    
    public DialogButton(final UIEventHandler messageTarget2, final int n, final int n2, final String text, final String name) {
        this.clicked = false;
        this.mouseOver = false;
        this.MessageTarget2 = null;
        this.x = n;
        this.y = n2;
        this.origX = (float)n;
        this.MessageTarget2 = messageTarget2;
        this.upLeft = Texture.getSharedTexture("ButtonL_Up");
        this.upMid = Texture.getSharedTexture("ButtonM_Up");
        this.upRight = Texture.getSharedTexture("ButtonR_Up");
        this.downLeft = Texture.getSharedTexture("ButtonL_Down");
        this.downMid = Texture.getSharedTexture("ButtonM_Down");
        this.downRight = Texture.getSharedTexture("ButtonR_Down");
        this.name = name;
        this.text = text;
        this.width = (float)TextManager.instance.MeasureStringX(UIFont.Small, text);
        this.width += 8.0f;
        if (this.width < 40.0f) {
            this.width = 40.0f;
        }
        this.height = (float)this.downMid.getHeight();
        this.x -= this.width / 2.0f;
    }
    
    @Override
    public Boolean onMouseDown(final double d, final double d2) {
        if (!this.isVisible()) {
            return false;
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseDown") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseDown"), new Object[] { this.table, d, d2 });
        }
        this.clicked = true;
        return Boolean.TRUE;
    }
    
    @Override
    public Boolean onMouseMove(final double d, final double d2) {
        this.mouseOver = true;
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseMove") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseMove"), new Object[] { this.table, d, d2 });
        }
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseMoveOutside(final double d, final double d2) {
        this.clicked = false;
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseMoveOutside") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseMoveOutside"), new Object[] { this.table, d, d2 });
        }
        this.mouseOver = false;
    }
    
    @Override
    public Boolean onMouseUp(final double d, final double d2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseUp") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseUp"), new Object[] { this.table, d, d2 });
        }
        if (this.clicked) {
            if (this.MessageTarget2 != null) {
                this.MessageTarget2.Selected(this.name, 0, 0);
            }
            else if (this.MessageTarget != null) {
                this.MessageTarget.ButtonClicked(this.name);
            }
        }
        this.clicked = false;
        return Boolean.TRUE;
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (this.clicked) {
            this.DrawTexture(this.downLeft, 0.0, 0.0, 1.0);
            this.DrawTextureScaledCol(this.downMid, this.downLeft.getWidth(), 0.0, (int)(this.getWidth() - this.downLeft.getWidth() * 2), this.downLeft.getHeight(), new Color(255, 255, 255, 255));
            this.DrawTexture(this.downRight, (int)(this.getWidth() - this.downRight.getWidth()), 0.0, 1.0);
            this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        }
        else {
            this.DrawTexture(this.upLeft, 0.0, 0.0, 1.0);
            this.DrawTextureScaledCol(this.upMid, this.downLeft.getWidth(), 0.0, (int)(this.getWidth() - this.downLeft.getWidth() * 2), this.downLeft.getHeight(), new Color(255, 255, 255, 255));
            this.DrawTexture(this.upRight, (int)(this.getWidth() - this.downRight.getWidth()), 0.0, 1.0);
            this.DrawTextCentre(this.text, this.getWidth() / 2.0, 0.0, 1.0, 1.0, 1.0, 1.0);
        }
        super.render();
    }
}
