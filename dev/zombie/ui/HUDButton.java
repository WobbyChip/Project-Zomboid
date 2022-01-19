// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.network.GameServer;
import zombie.core.textures.Texture;

public class HUDButton extends UIElement
{
    boolean clicked;
    UIElement display;
    Texture highlight;
    Texture overicon;
    boolean mouseOver;
    String name;
    Texture texture;
    UIEventHandler handler;
    public float notclickedAlpha;
    public float clickedalpha;
    
    public HUDButton(final String name, final double x, final double y, final String s, final String s2, final UIElement display) {
        this.clicked = false;
        this.overicon = null;
        this.mouseOver = false;
        this.notclickedAlpha = 0.85f;
        this.clickedalpha = 1.0f;
        if (GameServer.bServer) {
            return;
        }
        this.display = display;
        this.name = name;
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture(s);
            this.highlight = Texture.getSharedTexture(s2);
        }
        this.x = x;
        this.y = y;
        this.width = (float)this.texture.getWidth();
        this.height = (float)this.texture.getHeight();
    }
    
    public HUDButton(final String name, final float n, final float n2, final String s, final String s2, final UIEventHandler handler) {
        this.clicked = false;
        this.overicon = null;
        this.mouseOver = false;
        this.notclickedAlpha = 0.85f;
        this.clickedalpha = 1.0f;
        if (GameServer.bServer) {
            return;
        }
        this.texture = Texture.getSharedTexture(s);
        this.highlight = Texture.getSharedTexture(s2);
        this.handler = handler;
        this.name = name;
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture(s);
            this.highlight = Texture.getSharedTexture(s2);
        }
        this.x = n;
        this.y = n2;
        this.width = (float)this.texture.getWidth();
        this.height = (float)this.texture.getHeight();
    }
    
    public HUDButton(final String name, final float n, final float n2, final String s, final String s2, final String s3, final UIElement display) {
        this.clicked = false;
        this.overicon = null;
        this.mouseOver = false;
        this.notclickedAlpha = 0.85f;
        this.clickedalpha = 1.0f;
        if (GameServer.bServer) {
            return;
        }
        this.overicon = Texture.getSharedTexture(s3);
        this.display = display;
        this.texture = Texture.getSharedTexture(s);
        this.highlight = Texture.getSharedTexture(s2);
        this.name = name;
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture(s);
            this.highlight = Texture.getSharedTexture(s2);
        }
        this.x = n;
        this.y = n2;
        this.width = (float)this.texture.getWidth();
        this.height = (float)this.texture.getHeight();
    }
    
    public HUDButton(final String name, final float n, final float n2, final String s, final String s2, final String s3, final UIEventHandler handler) {
        this.clicked = false;
        this.overicon = null;
        this.mouseOver = false;
        this.notclickedAlpha = 0.85f;
        this.clickedalpha = 1.0f;
        if (GameServer.bServer) {
            return;
        }
        this.texture = Texture.getSharedTexture(s);
        this.highlight = Texture.getSharedTexture(s2);
        this.overicon = Texture.getSharedTexture(s3);
        this.handler = handler;
        this.name = name;
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture(s);
            this.highlight = Texture.getSharedTexture(s2);
        }
        this.x = n;
        this.y = n2;
        this.width = (float)this.texture.getWidth();
        this.height = (float)this.texture.getHeight();
    }
    
    @Override
    public Boolean onMouseDown(final double n, final double n2) {
        this.clicked = true;
        return Boolean.TRUE;
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        this.mouseOver = true;
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        this.clicked = false;
        if (this.display == null) {
            return;
        }
        if (!this.name.equals(this.display.getClickedValue())) {
            this.mouseOver = false;
        }
    }
    
    @Override
    public Boolean onMouseUp(final double n, final double n2) {
        if (this.clicked) {
            if (this.display != null) {
                this.display.ButtonClicked(this.name);
            }
            else if (this.handler != null) {
                this.handler.Selected(this.name, 0, 0);
            }
        }
        this.clicked = false;
        return Boolean.TRUE;
    }
    
    @Override
    public void render() {
        int n = 0;
        if (this.clicked) {
            ++n;
        }
        if (this.mouseOver || this.name.equals(this.display.getClickedValue())) {
            this.DrawTextureScaled(this.highlight, 0.0, n, this.getWidth(), this.getHeight(), this.clickedalpha);
        }
        else {
            this.DrawTextureScaled(this.texture, 0.0, n, this.getWidth(), this.getHeight(), this.notclickedAlpha);
        }
        if (this.overicon != null) {
            this.DrawTextureScaled(this.overicon, 0.0, n, this.overicon.getWidth(), this.overicon.getHeight(), 1.0);
        }
        super.render();
    }
    
    @Override
    public void update() {
        super.update();
    }
}
