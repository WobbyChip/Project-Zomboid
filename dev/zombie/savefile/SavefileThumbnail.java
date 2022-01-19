// 
// Decompiled by Procyon v0.5.36
// 

package zombie.savefile;

import zombie.core.sprite.SpriteRenderState;
import zombie.iso.PlayerCamera;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.opengl.GL11;
import zombie.interfaces.ITexture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.Texture;
import zombie.core.opengl.RenderSettings;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.core.textures.MultiTextureFBO2;
import zombie.ui.UIManager;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoCamera;
import zombie.core.Core;
import zombie.characters.IsoPlayer;

public final class SavefileThumbnail
{
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;
    
    public static void create() {
        int n = -1;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null) {
                n = i;
                break;
            }
        }
        if (n == -1) {
            return;
        }
        create(n);
    }
    
    public static void create(final int n) {
        final Core instance = Core.getInstance();
        final MultiTextureFBO2 offscreenBuffer = instance.OffscreenBuffer;
        final float n2 = offscreenBuffer.zoom[n];
        final float n3 = offscreenBuffer.targetZoom[n];
        setZoom(n, 1.0f, 1.0f);
        IsoCamera.cameras[n].center();
        renderWorld(n, true, true);
        SpriteRenderer.instance.drawGeneric(new TakeScreenShotDrawer(n));
        setZoom(n, n2, n3);
        IsoCamera.cameras[n].center();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null) {
                renderWorld(i, false, i == n);
            }
        }
        instance.RenderOffScreenBuffer();
        if (instance.StartFrameUI()) {
            UIManager.render();
        }
        instance.EndFrameUI();
    }
    
    private static void renderWorld(final int n, final boolean b, final boolean b2) {
        IsoPlayer.setInstance(IsoPlayer.players[n]);
        IsoCamera.CamCharacter = IsoPlayer.players[n];
        IsoSprite.globalOffsetX = -1.0f;
        Core.getInstance().StartFrame(n, b);
        if (b2) {
            SpriteRenderer.instance.drawGeneric(new FixCameraDrawer(n));
        }
        IsoCamera.frameState.set(n);
        IsoWorld.instance.render();
        RenderSettings.getInstance().legacyPostRender(n);
        Core.getInstance().EndFrame(n);
    }
    
    private static void setZoom(final int n, final float zoom, final float n2) {
        Core.getInstance().OffscreenBuffer.zoom[n] = zoom;
        Core.getInstance().OffscreenBuffer.targetZoom[n] = n2;
        IsoCamera.cameras[n].zoom = zoom;
        IsoCamera.cameras[n].OffscreenWidth = IsoCamera.getOffscreenWidth(n);
        IsoCamera.cameras[n].OffscreenHeight = IsoCamera.getOffscreenHeight(n);
    }
    
    private static void createWithRenderShader(final int n) {
        final int n2 = 256;
        final int n3 = 256;
        final TextureFBO textureFBO = new TextureFBO(new Texture(n2, n3, 16), false);
        GL11.glPushAttrib(1048575);
        try {
            textureFBO.startDrawing(true, false);
            GL11.glViewport(0, 0, n2, n3);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GLU.gluOrtho2D(0.0f, (float)n2, (float)n3, 0.0f);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            final Core instance = Core.getInstance();
            instance.RenderShader.Start();
            GL11.glDisable(3089);
            GL11.glDisable(2960);
            GL11.glDisable(3042);
            GL11.glDisable(3008);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            for (int i = 8; i > 1; --i) {
                GL13.glActiveTexture(33984 + i - 1);
                GL11.glDisable(3553);
            }
            GL13.glActiveTexture(33984);
            GL11.glEnable(3553);
            instance.getOffscreenBuffer().getTexture().bind();
            final int n4 = IsoCamera.getScreenLeft(n) + IsoCamera.getScreenWidth(n) / 2 - n2 / 2;
            final int n5 = IsoCamera.getScreenTop(n) + IsoCamera.getScreenHeight(n) / 2 - n3 / 2;
            final int widthHW = instance.getOffscreenBuffer().getTexture().getWidthHW();
            final int heightHW = instance.getOffscreenBuffer().getTexture().getHeightHW();
            final float n6 = n4 / (float)widthHW;
            final float n7 = (n4 + n2) / (float)widthHW;
            final float n8 = n5 / (float)heightHW;
            final float n9 = (n5 + n3) / (float)heightHW;
            GL11.glBegin(7);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glTexCoord2f(n6, n9);
            GL11.glVertex2d(0.0, 0.0);
            GL11.glTexCoord2f(n6, n8);
            GL11.glVertex2d(0.0, (double)n3);
            GL11.glTexCoord2f(n7, n8);
            GL11.glVertex2d((double)n2, (double)n3);
            GL11.glTexCoord2f(n7, n9);
            GL11.glVertex2d((double)n2, 0.0);
            GL11.glEnd();
            instance.RenderShader.End();
            instance.TakeScreenshot(0, 0, n2, n3, TextureFBO.getFuncs().GL_COLOR_ATTACHMENT0());
            textureFBO.endDrawing();
        }
        finally {
            textureFBO.destroy();
            GL11.glPopAttrib();
        }
    }
    
    private static final class FixCameraDrawer extends TextureDraw.GenericDrawer
    {
        int m_playerIndex;
        float m_zoom;
        int m_offscreenWidth;
        int m_offscreenHeight;
        
        FixCameraDrawer(final int playerIndex) {
            final PlayerCamera playerCamera = IsoCamera.cameras[playerIndex];
            this.m_playerIndex = playerIndex;
            this.m_zoom = playerCamera.zoom;
            this.m_offscreenWidth = playerCamera.OffscreenWidth;
            this.m_offscreenHeight = playerCamera.OffscreenHeight;
        }
        
        @Override
        public void render() {
            final SpriteRenderState renderingState = SpriteRenderer.instance.getRenderingState();
            renderingState.playerCamera[this.m_playerIndex].zoom = this.m_zoom;
            renderingState.playerCamera[this.m_playerIndex].OffscreenWidth = this.m_offscreenWidth;
            renderingState.playerCamera[this.m_playerIndex].OffscreenHeight = this.m_offscreenHeight;
            renderingState.zoomLevel[this.m_playerIndex] = this.m_zoom;
        }
    }
    
    private static final class TakeScreenShotDrawer extends TextureDraw.GenericDrawer
    {
        int m_playerIndex;
        
        TakeScreenShotDrawer(final int playerIndex) {
            this.m_playerIndex = playerIndex;
        }
        
        @Override
        public void render() {
            final Core instance = Core.getInstance();
            if (instance.OffscreenBuffer.Current == null) {
                Core.getInstance().TakeScreenshot(256, 256, 1029);
                return;
            }
            if (instance.RenderShader == null) {
                Core.getInstance().getOffscreenBuffer().startDrawing(false, false);
                Core.getInstance().TakeScreenshot(256, 256, TextureFBO.getFuncs().GL_COLOR_ATTACHMENT0());
                Core.getInstance().getOffscreenBuffer().endDrawing();
                return;
            }
            SavefileThumbnail.createWithRenderShader(this.m_playerIndex);
        }
    }
}
