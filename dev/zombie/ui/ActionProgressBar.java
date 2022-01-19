// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;

public final class ActionProgressBar extends UIElement
{
    Texture background;
    Texture foreground;
    float deltaValue;
    public int delayHide;
    
    public ActionProgressBar(final int n, final int n2) {
        this.deltaValue = 1.0f;
        this.delayHide = 0;
        this.background = Texture.getSharedTexture("BuildBar_Bkg");
        this.foreground = Texture.getSharedTexture("BuildBar_Bar");
        this.x = n;
        this.y = n2;
        this.width = (float)this.background.getWidth();
        this.height = (float)this.background.getHeight();
        this.followGameWorld = true;
    }
    
    @Override
    public void render() {
        if (!this.isVisible() || !UIManager.VisibleAllUI) {
            return;
        }
        this.DrawUVSliceTexture(this.background, 0.0, 0.0, this.background.getWidth(), this.background.getHeight(), Color.white, 0.0, 0.0, 1.0, 1.0);
        this.DrawUVSliceTexture(this.foreground, 3.0, 0.0, this.foreground.getWidth(), this.foreground.getHeight(), Color.white, 0.0, 0.0, this.deltaValue, 1.0);
    }
    
    public void setValue(final float deltaValue) {
        this.deltaValue = deltaValue;
    }
    
    public float getValue() {
        return this.deltaValue;
    }
}
