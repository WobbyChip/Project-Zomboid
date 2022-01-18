// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import org.lwjgl.opengl.GL13;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.opengl.CharacterModelCamera;
import zombie.core.math.PZMath;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.opengl.GL11;
import zombie.iso.IsoCamera;
import zombie.interfaces.ITexture;
import zombie.core.textures.Texture;
import zombie.core.Core;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import zombie.popman.ObjectPool;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.TextureFBO;

public final class ModelOutlines
{
    public static final ModelOutlines instance;
    public TextureFBO m_fboA;
    public TextureFBO m_fboB;
    public TextureFBO m_fboC;
    public boolean m_dirty;
    private int m_playerIndex;
    private final ColorInfo m_outlineColor;
    private ModelSlotRenderData m_playerRenderData;
    private ShaderProgram m_thickenHShader;
    private ShaderProgram m_thickenVShader;
    private ShaderProgram m_blitShader;
    private final ObjectPool<Drawer> m_drawerPool;
    
    public ModelOutlines() {
        this.m_dirty = false;
        this.m_outlineColor = new ColorInfo();
        this.m_drawerPool = new ObjectPool<Drawer>(Drawer::new);
    }
    
    public void startFrameMain(final int playerIndex) {
        final Drawer drawer = this.m_drawerPool.alloc();
        drawer.m_startFrame = true;
        drawer.m_playerIndex = playerIndex;
        SpriteRenderer.instance.drawGeneric(drawer);
    }
    
    public void endFrameMain(final int playerIndex) {
        final Drawer drawer = this.m_drawerPool.alloc();
        drawer.m_startFrame = false;
        drawer.m_playerIndex = playerIndex;
        SpriteRenderer.instance.drawGeneric(drawer);
    }
    
    public void startFrame(final int playerIndex) {
        this.m_dirty = false;
        this.m_playerIndex = playerIndex;
        this.m_playerRenderData = null;
    }
    
    public void checkFBOs() {
        if (this.m_fboA != null && (this.m_fboA.getWidth() != Core.width || this.m_fboB.getHeight() != Core.height)) {
            this.m_fboA.destroy();
            this.m_fboB.destroy();
            this.m_fboC.destroy();
            this.m_fboA = null;
            this.m_fboB = null;
            this.m_fboC = null;
        }
        if (this.m_fboA == null) {
            this.m_fboA = new TextureFBO(new Texture(Core.width, Core.height, 16), false);
            this.m_fboB = new TextureFBO(new Texture(Core.width, Core.height, 16), false);
            this.m_fboC = new TextureFBO(new Texture(Core.width, Core.height, 16), false);
        }
    }
    
    public void setPlayerRenderData(final ModelSlotRenderData playerRenderData) {
        this.m_playerRenderData = playerRenderData;
    }
    
    public boolean beginRenderOutline(final ColorInfo colorInfo) {
        this.m_outlineColor.set(colorInfo);
        if (this.m_dirty) {
            return false;
        }
        this.m_dirty = true;
        this.checkFBOs();
        return true;
    }
    
    public void endFrame(final int playerIndex) {
        if (!this.m_dirty) {
            return;
        }
        this.m_playerIndex = playerIndex;
        if (this.m_thickenHShader == null) {
            this.m_thickenHShader = ShaderProgram.createShaderProgram("aim_outline_h", false, true);
            this.m_thickenVShader = ShaderProgram.createShaderProgram("aim_outline_v", false, true);
            this.m_blitShader = ShaderProgram.createShaderProgram("aim_outline_blit", false, true);
        }
        final int screenLeft = IsoCamera.getScreenLeft(this.m_playerIndex);
        final int screenTop = IsoCamera.getScreenTop(this.m_playerIndex);
        final int screenWidth = IsoCamera.getScreenWidth(this.m_playerIndex);
        final int screenHeight = IsoCamera.getScreenHeight(this.m_playerIndex);
        final int n = screenLeft;
        final int n2 = screenTop;
        final int n3 = screenWidth;
        final int n4 = screenHeight;
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0.0f, (float)n3, (float)n4, 0.0f);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        final float n5 = (float)this.m_fboA.getWidth();
        final float n6 = (float)this.m_fboA.getHeight();
        final float lerp = PZMath.lerp(0.5f, 0.2f, SpriteRenderer.instance.getPlayerZoomLevel() / 2.5f);
        this.m_fboB.startDrawing(true, true);
        GL11.glViewport(n, n2, n3, n4);
        this.m_thickenHShader.Start();
        this.m_thickenHShader.setVector2("u_resolution", n5, n6);
        this.m_thickenHShader.setValue("u_radius", lerp);
        this.m_thickenHShader.setVector4("u_color", this.m_outlineColor.r, this.m_outlineColor.g, this.m_outlineColor.b, this.m_outlineColor.a);
        this.renderTexture(this.m_fboA.getTexture(), screenLeft, screenTop, screenWidth, screenHeight);
        this.m_thickenHShader.End();
        this.m_fboB.endDrawing();
        this.m_fboC.startDrawing(true, true);
        GL11.glViewport(n, n2, n3, n4);
        this.m_thickenVShader.Start();
        this.m_thickenVShader.setVector2("u_resolution", n5, n6);
        this.m_thickenVShader.setValue("u_radius", lerp);
        this.m_thickenVShader.setVector4("u_color", this.m_outlineColor.r, this.m_outlineColor.g, this.m_outlineColor.b, this.m_outlineColor.a);
        this.renderTexture(this.m_fboB.getTexture(), screenLeft, screenTop, screenWidth, screenHeight);
        this.m_thickenVShader.End();
        this.m_fboC.endDrawing();
        if (this.m_playerRenderData != null) {
            CharacterModelCamera.instance.m_x = this.m_playerRenderData.x;
            CharacterModelCamera.instance.m_y = this.m_playerRenderData.y;
            CharacterModelCamera.instance.m_z = this.m_playerRenderData.z;
            CharacterModelCamera.instance.m_bInVehicle = this.m_playerRenderData.bInVehicle;
            CharacterModelCamera.instance.m_useAngle = this.m_playerRenderData.animPlayerAngle;
            CharacterModelCamera.instance.m_bUseWorldIso = true;
            CharacterModelCamera.instance.bDepthMask = false;
            ModelCamera.instance = CharacterModelCamera.instance;
            GL11.glViewport(screenLeft, screenTop, screenWidth, screenHeight);
            this.m_playerRenderData.performRenderCharacterOutline();
        }
        GL11.glViewport(n, n2, n3, n4);
        this.m_blitShader.Start();
        this.m_blitShader.setSamplerUnit("texture", 0);
        this.m_blitShader.setSamplerUnit("mask", 1);
        GL13.glActiveTexture(33985);
        GL11.glBindTexture(3553, this.m_fboA.getTexture().getID());
        GL13.glActiveTexture(33984);
        this.renderTexture(this.m_fboC.getTexture(), screenLeft, screenTop, screenWidth, screenHeight);
        this.m_blitShader.End();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
    }
    
    private void renderTexture(final ITexture texture, int n, int n2, final int n3, final int n4) {
        texture.bind();
        final float n5 = n / (float)texture.getWidthHW();
        final float n6 = n2 / (float)texture.getHeightHW();
        final float n7 = (n + n3) / (float)texture.getWidthHW();
        final float n8 = (n2 + n4) / (float)texture.getHeightHW();
        n2 = (n = 0);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(n5, n8);
        GL11.glVertex2i(n, n2);
        GL11.glTexCoord2f(n7, n8);
        GL11.glVertex2i(n + n3, n2);
        GL11.glTexCoord2f(n7, n6);
        GL11.glVertex2i(n + n3, n2 + n4);
        GL11.glTexCoord2f(n5, n6);
        GL11.glVertex2i(n, n2 + n4);
        GL11.glEnd();
        GL11.glDepthMask(true);
    }
    
    public void renderDebug() {
    }
    
    static {
        instance = new ModelOutlines();
    }
    
    public static final class Drawer extends TextureDraw.GenericDrawer
    {
        boolean m_startFrame;
        int m_playerIndex;
        
        @Override
        public void render() {
            if (this.m_startFrame) {
                ModelOutlines.instance.startFrame(this.m_playerIndex);
            }
            else {
                ModelOutlines.instance.endFrame(this.m_playerIndex);
            }
        }
        
        @Override
        public void postRender() {
            ModelOutlines.instance.m_drawerPool.release(this);
        }
    }
}
