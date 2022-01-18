// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.Lua.LuaManager;
import zombie.core.textures.Texture;

public final class GenericButton extends UIElement
{
    public boolean clicked;
    public UIElement MessageTarget;
    public boolean mouseOver;
    public String name;
    public String text;
    Texture UpTexture;
    Texture DownTexture;
    private UIEventHandler MessageTarget2;
    
    public GenericButton(final UIElement messageTarget, final float n, final float n2, final float width, final float height, final String name, final String text, final Texture upTexture, final Texture downTexture) {
        this.clicked = false;
        this.mouseOver = false;
        this.UpTexture = null;
        this.DownTexture = null;
        this.MessageTarget2 = null;
        this.x = n;
        this.y = n2;
        this.MessageTarget = messageTarget;
        this.name = name;
        this.text = text;
        this.width = width;
        this.height = height;
        this.UpTexture = upTexture;
        this.DownTexture = downTexture;
    }
    
    public GenericButton(final UIEventHandler messageTarget2, final float n, final float n2, final float width, final float height, final String name, final String text, final Texture upTexture, final Texture downTexture) {
        this.clicked = false;
        this.mouseOver = false;
        this.UpTexture = null;
        this.DownTexture = null;
        this.MessageTarget2 = null;
        this.x = n;
        this.y = n2;
        this.MessageTarget2 = messageTarget2;
        this.name = name;
        this.text = text;
        this.width = width;
        this.height = height;
        this.UpTexture = upTexture;
        this.DownTexture = downTexture;
    }
    
    @Override
    public Boolean onMouseDown(final double d, final double d2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseDown") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseDown"), new Object[] { this.table, d, d2 });
        }
        this.clicked = true;
        return Boolean.TRUE;
    }
    
    @Override
    public Boolean onMouseMove(final double d, final double d2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseMove") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseMove"), new Object[] { this.table, d, d2 });
        }
        this.mouseOver = true;
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseMoveOutside(final double d, final double d2) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onMouseMoveOutside") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onMouseMoveOutside"), new Object[] { this.table, d, d2 });
        }
        this.clicked = false;
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
            else {
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
            this.DrawTextureScaled(this.DownTexture, 0.0, 0.0, this.getWidth(), this.getHeight(), 1.0);
            this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        }
        else {
            this.DrawTextureScaled(this.UpTexture, 0.0, 0.0, this.getWidth(), this.getHeight(), 1.0);
            this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        }
        super.render();
    }
}
