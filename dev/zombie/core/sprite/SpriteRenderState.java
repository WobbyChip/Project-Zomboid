// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.sprite;

import zombie.core.opengl.GLState;
import zombie.input.Mouse;
import zombie.core.Styles.TransparentStyle;
import zombie.core.opengl.RenderSettings;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.iso.PlayerCamera;
import zombie.core.textures.TextureFBO;

public final class SpriteRenderState extends GenericSpriteRenderState
{
    public TextureFBO fbo;
    public long time;
    public final SpriteRenderStateUI stateUI;
    public int playerIndex;
    public final PlayerCamera[] playerCamera;
    public final float[] playerAmbient;
    public float maxZoomLevel;
    public float minZoomLevel;
    public final float[] zoomLevel;
    
    public SpriteRenderState(final int n) {
        super(n);
        this.fbo = null;
        this.playerCamera = new PlayerCamera[4];
        this.playerAmbient = new float[4];
        this.maxZoomLevel = 0.0f;
        this.minZoomLevel = 0.0f;
        this.zoomLevel = new float[4];
        for (int i = 0; i < 4; ++i) {
            this.playerCamera[i] = new PlayerCamera(i);
        }
        this.stateUI = new SpriteRenderStateUI(n);
    }
    
    @Override
    public void onRendered() {
        super.onRendered();
        this.stateUI.onRendered();
    }
    
    @Override
    public void onReady() {
        super.onReady();
        this.stateUI.onReady();
    }
    
    @Override
    public void CheckSpriteSlots() {
        if (this.stateUI.bActive) {
            this.stateUI.CheckSpriteSlots();
        }
        else {
            super.CheckSpriteSlots();
        }
    }
    
    @Override
    public void clear() {
        this.stateUI.clear();
        super.clear();
    }
    
    public GenericSpriteRenderState getActiveState() {
        return this.stateUI.bActive ? this.stateUI : this;
    }
    
    public void prePopulating() {
        this.clear();
        this.fbo = Core.getInstance().getOffscreenBuffer();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null) {
                this.playerCamera[i].initFromIsoCamera(i);
                this.playerAmbient[i] = RenderSettings.getInstance().getAmbientForPlayer(i);
                this.zoomLevel[i] = Core.getInstance().getZoom(i);
                this.maxZoomLevel = Core.getInstance().getMaxZoom();
                this.minZoomLevel = Core.getInstance().getMinZoom();
            }
        }
        this.defaultStyle = TransparentStyle.instance;
        this.bCursorVisible = Mouse.isCursorVisible();
        GLState.startFrame();
    }
}
