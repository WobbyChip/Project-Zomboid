// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.textures.Texture;
import zombie.core.Core;
import zombie.SoundManager;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.GameTime;

public final class SpeedControls extends UIElement
{
    public static SpeedControls instance;
    public int CurrentSpeed;
    public int SpeedBeforePause;
    public float MultiBeforePause;
    float alpha;
    boolean MouseOver;
    public static HUDButton Play;
    public static HUDButton Pause;
    public static HUDButton FastForward;
    public static HUDButton FasterForward;
    public static HUDButton Wait;
    
    public SpeedControls() {
        this.CurrentSpeed = 1;
        this.SpeedBeforePause = 1;
        this.MultiBeforePause = 1.0f;
        this.alpha = 1.0f;
        this.MouseOver = false;
        this.x = 0.0;
        this.y = 0.0;
        final int n = 2;
        SpeedControls.Pause = new SCButton("Pause", 1.0f, 0.0f, "media/ui/Time_Pause_Off.png", "media/ui/Time_Pause_On.png", this);
        SpeedControls.Play = new SCButton("Play", (float)(SpeedControls.Pause.x + SpeedControls.Pause.width + n), 0.0f, "media/ui/Time_Play_Off.png", "media/ui/Time_Play_On.png", this);
        SpeedControls.FastForward = new SCButton("Fast Forward x 1", (float)(SpeedControls.Play.x + SpeedControls.Play.width + n), 0.0f, "media/ui/Time_FFwd1_Off.png", "media/ui/Time_FFwd1_On.png", this);
        SpeedControls.FasterForward = new SCButton("Fast Forward x 2", (float)(SpeedControls.FastForward.x + SpeedControls.FastForward.width + n), 0.0f, "media/ui/Time_FFwd2_Off.png", "media/ui/Time_FFwd2_On.png", this);
        SpeedControls.Wait = new SCButton("Wait", (float)(SpeedControls.FasterForward.x + SpeedControls.FasterForward.width + n), 0.0f, "media/ui/Time_Wait_Off.png", "media/ui/Time_Wait_On.png", this);
        this.width = (int)SpeedControls.Wait.x + SpeedControls.Wait.width;
        this.height = SpeedControls.Wait.height;
        this.AddChild(SpeedControls.Pause);
        this.AddChild(SpeedControls.Play);
        this.AddChild(SpeedControls.FastForward);
        this.AddChild(SpeedControls.FasterForward);
        this.AddChild(SpeedControls.Wait);
    }
    
    @Override
    public void ButtonClicked(final String anObject) {
        GameTime.instance.setMultiplier(1.0f);
        if ("Pause".equals(anObject)) {
            if (this.CurrentSpeed > 0) {
                this.SetCurrentGameSpeed(0);
            }
            else {
                this.SetCurrentGameSpeed(5);
            }
        }
        if ("Play".equals(anObject)) {
            this.SetCurrentGameSpeed(1);
            GameTime.instance.setMultiplier(1.0f);
        }
        if ("Fast Forward x 1".equals(anObject)) {
            this.SetCurrentGameSpeed(2);
            GameTime.instance.setMultiplier(5.0f);
        }
        if ("Fast Forward x 2".equals(anObject)) {
            this.SetCurrentGameSpeed(3);
            GameTime.instance.setMultiplier(20.0f);
        }
        if ("Wait".equals(anObject)) {
            this.SetCurrentGameSpeed(4);
            GameTime.instance.setMultiplier(40.0f);
        }
    }
    
    public int getCurrentGameSpeed() {
        if (GameClient.bClient || GameServer.bServer) {
            return 1;
        }
        return this.CurrentSpeed;
    }
    
    public void SetCorrectIconStates() {
        if (this.CurrentSpeed == 0) {
            super.ButtonClicked("Pause");
        }
        if (this.CurrentSpeed == 1) {
            super.ButtonClicked("Play");
        }
        if (GameTime.instance.getTrueMultiplier() == 5.0f) {
            super.ButtonClicked("Fast Forward x 1");
        }
        if (GameTime.instance.getTrueMultiplier() == 20.0f) {
            super.ButtonClicked("Fast Forward x 2");
        }
        if (GameTime.instance.getTrueMultiplier() == 40.0f) {
            super.ButtonClicked("Wait");
        }
    }
    
    public void SetCurrentGameSpeed(int speedBeforePause) {
        if (this.CurrentSpeed > 0 && speedBeforePause == 0) {
            SoundManager.instance.pauseSoundAndMusic();
            SoundManager.instance.setMusicState("PauseMenu");
        }
        else if (this.CurrentSpeed == 0 && speedBeforePause > 0) {
            SoundManager.instance.setMusicState("InGame");
            SoundManager.instance.resumeSoundAndMusic();
        }
        GameTime.instance.setMultiplier(1.0f);
        if (speedBeforePause == 0) {
            this.SpeedBeforePause = this.CurrentSpeed;
            this.MultiBeforePause = GameTime.instance.getMultiplier();
        }
        if (speedBeforePause == 5) {
            speedBeforePause = this.SpeedBeforePause;
            GameTime.instance.setMultiplier(this.MultiBeforePause);
        }
        this.CurrentSpeed = speedBeforePause;
        this.SetCorrectIconStates();
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        if (!this.isVisible()) {
            return false;
        }
        this.MouseOver = true;
        super.onMouseMove(n, n2);
        this.SetCorrectIconStates();
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        super.onMouseMoveOutside(n, n2);
        this.MouseOver = false;
        this.SetCorrectIconStates();
    }
    
    @Override
    public void render() {
        super.render();
        if ("Tutorial".equals(Core.GameMode)) {
            SpeedControls.Pause.setVisible(false);
            SpeedControls.Play.setVisible(false);
            SpeedControls.FastForward.setVisible(false);
            SpeedControls.FasterForward.setVisible(false);
            SpeedControls.Wait.setVisible(false);
        }
        this.SetCorrectIconStates();
    }
    
    @Override
    public void update() {
        super.update();
        this.SetCorrectIconStates();
    }
    
    static {
        SpeedControls.instance = null;
    }
    
    public static final class SCButton extends HUDButton
    {
        private static final int BORDER = 3;
        
        public SCButton(final String s, final float n, final float n2, final String s2, final String s3, final UIElement uiElement) {
            super(s, n, n2, s2, s3, uiElement);
            this.width += 6.0f;
            this.height += 6.0f;
        }
        
        @Override
        public void render() {
            int n = 3;
            if (this.clicked) {
                ++n;
            }
            this.DrawTextureScaledCol(null, 0.0, this.clicked ? 1.0 : 0.0, this.width, this.height, 0.0, 0.0, 0.0, 0.5);
            if (this.mouseOver || this.name.equals(this.display.getClickedValue())) {
                this.DrawTextureScaled(this.highlight, 3.0, n, this.highlight.getWidth(), this.highlight.getHeight(), this.clickedalpha);
            }
            else {
                this.DrawTextureScaled(this.texture, 3.0, n, this.texture.getWidth(), this.texture.getHeight(), this.notclickedAlpha);
            }
            if (this.overicon != null) {
                this.DrawTextureScaled(this.overicon, 3.0, n, this.overicon.getWidth(), this.overicon.getHeight(), 1.0);
            }
        }
    }
}
