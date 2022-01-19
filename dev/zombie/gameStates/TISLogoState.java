// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.GameTime;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.core.Core;
import zombie.ui.UIManager;

public final class TISLogoState extends GameState
{
    public float alpha;
    public float alphaStep;
    public float logoDisplayTime;
    public int screenNumber;
    public int stage;
    public float targetAlpha;
    private boolean bNoRender;
    private final LogoElement logoTIS;
    private final LogoElement logoFMOD;
    private final LogoElement logoGA;
    private final LogoElement logoNW;
    private static final int SCREEN_TIS = 1;
    private static final int SCREEN_OTHER = 2;
    private static final int STAGE_FADING_IN_LOGO = 0;
    private static final int STAGE_HOLDING_LOGO = 1;
    private static final int STAGE_FADING_OUT_LOGO = 2;
    private static final int STAGE_EXIT = 3;
    
    public TISLogoState() {
        this.alpha = 0.0f;
        this.alphaStep = 0.02f;
        this.logoDisplayTime = 20.0f;
        this.screenNumber = 1;
        this.stage = 0;
        this.targetAlpha = 0.0f;
        this.bNoRender = false;
        this.logoTIS = new LogoElement("media/ui/TheIndieStoneLogo_Lineart_White.png");
        this.logoFMOD = new LogoElement("media/ui/FMODLogo.png");
        this.logoGA = new LogoElement("media/ui/GA-1280-white.png");
        this.logoNW = new LogoElement("media/ui/NW_Logo_Combined.png");
    }
    
    @Override
    public void enter() {
        UIManager.bSuspend = true;
        this.alpha = 0.0f;
        this.targetAlpha = 1.0f;
    }
    
    @Override
    public void exit() {
        UIManager.bSuspend = false;
    }
    
    @Override
    public void render() {
        if (this.bNoRender) {
            Core.getInstance().StartFrame();
            SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0f, 0.0f, 0.0f, 1.0f, null);
            Core.getInstance().EndFrame();
            return;
        }
        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        final boolean useUIFBO = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        Core.getInstance().StartFrameUI();
        SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0f, 0.0f, 0.0f, 1.0f, null);
        if (this.screenNumber == 1) {
            this.logoTIS.centerOnScreen();
            this.logoTIS.render(this.alpha);
        }
        if (this.screenNumber == 2) {
            this.renderAttribution();
        }
        Core.getInstance().EndFrameUI();
        UIManager.useUIFBO = useUIFBO;
    }
    
    private void renderAttribution() {
        final int screenWidth = Core.getInstance().getScreenWidth();
        final int screenHeight = Core.getInstance().getScreenHeight();
        final int n = 50;
        final int n2 = (screenHeight - (3 + 1) * n) / 3;
        final int n3 = n;
        final Texture texture = this.logoGA.m_texture;
        if (texture != null && texture.isReady()) {
            final int n4 = (int)(texture.getWidth() * n2 / (float)texture.getHeight());
            this.logoGA.setPos((screenWidth - n4) / 2, n3);
            this.logoGA.setSize(n4, n2);
            this.logoGA.render(this.alpha);
        }
        final int n5 = (int)(n3 + (n2 + n) + n2 * 0.15f);
        final Texture texture2 = this.logoNW.m_texture;
        if (texture2 != null && texture2.isReady()) {
            final float n6 = 0.5f;
            final int n7 = (int)(texture2.getWidth() * n6 * n2 / texture2.getHeight());
            final int n8 = (int)(n2 * n6);
            this.logoNW.setPos((screenWidth - n7) / 2, n5 + (n2 - n8) / 2);
            this.logoNW.setSize(n7, n8);
            this.logoNW.render(this.alpha);
        }
        final int n9 = n5 + (n2 + n);
        final Texture texture3 = this.logoFMOD.m_texture;
        if (texture3 != null && texture3.isReady()) {
            final int n10 = (int)(n2 * 0.35f - 16.0f - TextManager.instance.getFontHeight(UIFont.Small));
            final int n11 = (int)(texture3.getWidth() * (n10 / (float)texture3.getHeight()));
            final int n12 = (screenWidth - n11) / 2;
            final int n13 = (n2 - n10) / 2;
            final int n14 = n9 + n13 + n10 + 16;
            this.logoFMOD.setPos(n12, n9 + n13);
            this.logoFMOD.setSize(n11, n10);
            this.logoFMOD.render(this.alpha);
            TextManager.instance.DrawStringCentre(screenWidth / 2.0, n14, "Made with FMOD Studio by Firelight Technologies Pty Ltd.", 1.0, 1.0, 1.0, this.alpha);
        }
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
            this.stage = 3;
        }
        if (this.stage == 0) {
            this.targetAlpha = 1.0f;
            if (this.alpha == 1.0f) {
                this.stage = 1;
                this.logoDisplayTime = 20.0f;
            }
        }
        if (this.stage == 1) {
            this.logoDisplayTime -= GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.logoDisplayTime <= 0.0f) {
                this.stage = 2;
            }
        }
        if (this.stage == 2) {
            this.targetAlpha = 0.0f;
            if (this.alpha == 0.0f) {
                if (this.screenNumber == 1) {
                    this.screenNumber = 2;
                    this.stage = 0;
                }
                else {
                    this.stage = 3;
                }
            }
        }
        if (this.stage == 3) {
            this.targetAlpha = 0.0f;
            if (this.alpha == 0.0f) {
                this.bNoRender = true;
                return GameStateMachine.StateAction.Continue;
            }
        }
        if (this.alpha < this.targetAlpha) {
            this.alpha += this.alphaStep * GameTime.getInstance().getMultiplier();
            if (this.alpha > this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }
        else if (this.alpha > this.targetAlpha) {
            this.alpha -= this.alphaStep * GameTime.getInstance().getMultiplier();
            if (this.stage == 3) {
                this.alpha -= this.alphaStep * GameTime.getInstance().getMultiplier();
            }
            if (this.alpha < this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }
        return GameStateMachine.StateAction.Remain;
    }
    
    private static final class LogoElement
    {
        Texture m_texture;
        int m_x;
        int m_y;
        int m_width;
        int m_height;
        
        LogoElement(final String s) {
            this.m_texture = Texture.getSharedTexture(s);
            if (this.m_texture != null) {
                this.m_width = this.m_texture.getWidth();
                this.m_height = this.m_texture.getHeight();
            }
        }
        
        void centerOnScreen() {
            this.m_x = (Core.getInstance().getScreenWidth() - this.m_width) / 2;
            this.m_y = (Core.getInstance().getScreenHeight() - this.m_height) / 2;
        }
        
        void setPos(final int x, final int y) {
            this.m_x = x;
            this.m_y = y;
        }
        
        void setSize(final int width, final int height) {
            this.m_width = width;
            this.m_height = height;
        }
        
        void render(final float n) {
            if (this.m_texture == null || !this.m_texture.isReady()) {
                return;
            }
            SpriteRenderer.instance.renderi(this.m_texture, this.m_x, this.m_y, this.m_width, this.m_height, 1.0f, 1.0f, 1.0f, n, null);
        }
    }
}
