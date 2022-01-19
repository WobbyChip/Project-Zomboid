// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.core.profiling.PerformanceProfileProbe;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import zombie.iso.IsoGridSquare;
import zombie.debug.DebugOptions;
import zombie.core.Styles.LightingStyle;
import zombie.util.list.PZArrayUtil;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.asset.Asset;
import zombie.core.textures.TextureAssetManager;
import org.lwjgl.opengl.GL13;
import zombie.debug.DebugLog;
import zombie.core.Styles.AlphaOp;
import org.lwjgl.opengl.GL11;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.iso.PlayerCamera;
import zombie.core.Styles.AbstractStyle;
import zombie.core.Styles.AdditiveStyle;
import zombie.core.opengl.RenderThread;
import java.util.function.BooleanSupplier;
import zombie.core.opengl.GLState;
import zombie.core.Styles.TransparentStyle;
import zombie.core.sprite.SpriteRenderState;
import zombie.GameProfiler;
import zombie.iso.IsoPuddles;
import zombie.core.textures.TextureFBO;
import zombie.core.Styles.Style;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.sprite.SpriteRendererStates;

public final class SpriteRenderer
{
    public static final SpriteRenderer instance;
    static final int VERTEX_SIZE = 32;
    static final int TEXTURE0_COORD_OFFSET = 8;
    static final int TEXTURE1_COORD_OFFSET = 16;
    static final int TEXTURE2_ATTRIB_OFFSET = 24;
    static final int COLOR_OFFSET = 28;
    public static final RingBuffer ringBuffer;
    public static final int NUM_RENDER_STATES = 3;
    public final SpriteRendererStates m_states;
    private volatile boolean m_waitingForRenderState;
    public static boolean GL_BLENDFUNC_ENABLED;
    
    public SpriteRenderer() {
        this.m_states = new SpriteRendererStates();
        this.m_waitingForRenderState = false;
    }
    
    public void create() {
        SpriteRenderer.ringBuffer.create();
    }
    
    public void clearSprites() {
        this.m_states.getPopulating().clear();
    }
    
    public void glDepthMask(final boolean b) {
        this.m_states.getPopulatingActiveState().glDepthMask(b);
    }
    
    public void renderflipped(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        this.m_states.getPopulatingActiveState().renderflipped(texture, n, n2, n3, n4, n5, n6, n7, n8, consumer);
    }
    
    public void drawModel(final ModelManager.ModelSlot modelSlot) {
        this.m_states.getPopulatingActiveState().drawModel(modelSlot);
    }
    
    public void drawSkyBox(final Shader shader, final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().drawSkyBox(shader, n, n2, n3);
    }
    
    public void drawWater(final Shader shader, final int n, final int n2, final boolean b) {
        this.m_states.getPopulatingActiveState().drawWater(shader, n, n2, b);
    }
    
    public void drawPuddles(final Shader shader, final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().drawPuddles(shader, n, n2, n3);
    }
    
    public void drawParticles(final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().drawParticles(n, n2, n3);
    }
    
    public void drawGeneric(final TextureDraw.GenericDrawer genericDrawer) {
        this.m_states.getPopulatingActiveState().drawGeneric(genericDrawer);
    }
    
    public void glDisable(final int n) {
        this.m_states.getPopulatingActiveState().glDisable(n);
    }
    
    public void glEnable(final int n) {
        this.m_states.getPopulatingActiveState().glEnable(n);
    }
    
    public void glStencilMask(final int n) {
        this.m_states.getPopulatingActiveState().glStencilMask(n);
    }
    
    public void glClear(final int n) {
        this.m_states.getPopulatingActiveState().glClear(n);
    }
    
    public void glClearColor(final int n, final int n2, final int n3, final int n4) {
        this.m_states.getPopulatingActiveState().glClearColor(n, n2, n3, n4);
    }
    
    public void glStencilFunc(final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().glStencilFunc(n, n2, n3);
    }
    
    public void glStencilOp(final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().glStencilOp(n, n2, n3);
    }
    
    public void glColorMask(final int n, final int n2, final int n3, final int n4) {
        this.m_states.getPopulatingActiveState().glColorMask(n, n2, n3, n4);
    }
    
    public void glAlphaFunc(final int n, final float n2) {
        this.m_states.getPopulatingActiveState().glAlphaFunc(n, n2);
    }
    
    public void glBlendFunc(final int n, final int n2) {
        this.m_states.getPopulatingActiveState().glBlendFunc(n, n2);
    }
    
    public void glBlendFuncSeparate(final int n, final int n2, final int n3, final int n4) {
        this.m_states.getPopulatingActiveState().glBlendFuncSeparate(n, n2, n3, n4);
    }
    
    public void glBlendEquation(final int n) {
        this.m_states.getPopulatingActiveState().glBlendEquation(n);
    }
    
    public void render(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final float n9, final float n10, final float n11, final float n12, final Consumer<TextureDraw> consumer) {
        this.m_states.getPopulatingActiveState().render(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, consumer);
    }
    
    public void render(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20, final float n21, final float n22, final float n23, final float n24, final Consumer<TextureDraw> consumer) {
        this.m_states.getPopulatingActiveState().render(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, consumer);
    }
    
    public void renderdebug(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20, final float n21, final float n22, final float n23, final float n24, final Consumer<TextureDraw> consumer) {
        this.m_states.getPopulatingActiveState().renderdebug(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, consumer);
    }
    
    public void renderline(final Texture texture, final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        this.m_states.getPopulatingActiveState().renderline(texture, (float)n, (float)n2, (float)n3, (float)n4, n5, n6, n7, n8, n9);
    }
    
    public void renderline(final Texture texture, final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8) {
        this.m_states.getPopulatingActiveState().renderline(texture, n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final int n10, final int n11, final int n12) {
        this.m_states.getPopulatingActiveState().render(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        final float floor = PZMath.floor(n);
        final float floor2 = PZMath.floor(n2);
        this.m_states.getPopulatingActiveState().render(texture, floor, floor2, PZMath.ceil(n + n3) - floor, PZMath.ceil(n2 + n4) - floor2, n5, n6, n7, n8, consumer);
    }
    
    public void renderi(final Texture texture, final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        this.m_states.getPopulatingActiveState().render(texture, (float)n, (float)n2, (float)n3, (float)n4, n5, n6, n7, n8, consumer);
    }
    
    public void renderClamped(final Texture texture, int n, int n2, int n3, int n4, final int n5, final int n6, final int n7, final int n8, final float n9, final float n10, final float n11, final float n12, final Consumer<TextureDraw> consumer) {
        final int clamp = PZMath.clamp(n, n5, n5 + n7);
        final int clamp2 = PZMath.clamp(n2, n6, n6 + n8);
        final int clamp3 = PZMath.clamp(n + n3, n5, n5 + n7);
        final int clamp4 = PZMath.clamp(n2 + n4, n6, n6 + n8);
        if (clamp == clamp3 || clamp2 == clamp4) {
            return;
        }
        final int n13 = clamp - n;
        final int n14 = n + n3 - clamp3;
        final int n15 = clamp2 - n2;
        final int n16 = n2 + n4 - clamp4;
        if (n13 == 0 && n14 == 0 && n15 == 0 && n16 == 0) {
            this.m_states.getPopulatingActiveState().render(texture, (float)n, (float)n2, (float)n3, (float)n4, n9, n10, n11, n12, consumer);
            return;
        }
        float n17 = 0.0f;
        float n18 = 0.0f;
        float n19 = 1.0f;
        float n20 = 0.0f;
        float n21 = 1.0f;
        float n22 = 1.0f;
        float n23 = 0.0f;
        float n24 = 1.0f;
        if (texture != null) {
            n17 = n13 / (float)n3;
            n18 = n15 / (float)n4;
            n19 = (n3 - n14) / (float)n3;
            n20 = n15 / (float)n4;
            n21 = (n3 - n14) / (float)n3;
            n22 = (n4 - n16) / (float)n4;
            n23 = n13 / (float)n3;
            n24 = (n4 - n16) / (float)n4;
        }
        n = clamp;
        n2 = clamp2;
        n3 = clamp3 - clamp;
        n4 = clamp4 - clamp2;
        this.m_states.getPopulatingActiveState().render(texture, (float)n, (float)n2, (float)n3, (float)n4, n9, n10, n11, n12, n17, n18, n19, n20, n21, n22, n23, n24, consumer);
    }
    
    public void renderRect(final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8) {
        this.m_states.getPopulatingActiveState().renderRect(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void renderPoly(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.m_states.getPopulatingActiveState().renderPoly(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
    }
    
    public void renderPoly(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.m_states.getPopulatingActiveState().renderPoly(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
    }
    
    public void renderPoly(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20) {
        this.m_states.getPopulatingActiveState().renderPoly(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20);
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        this.m_states.getPopulatingActiveState().render(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, null);
    }
    
    public void render(final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final Consumer<TextureDraw> consumer) {
        this.m_states.getPopulatingActiveState().render(texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, consumer);
    }
    
    private static void buildDrawBuffer(final TextureDraw[] array, final Style[] array2, final int n, final RingBuffer ringBuffer) {
        for (int i = 0; i < n; ++i) {
            final TextureDraw textureDraw = array[i];
            final Style style = array2[i];
            TextureDraw textureDraw2 = null;
            if (i > 0) {
                textureDraw2 = array[i - 1];
            }
            ringBuffer.add(textureDraw, textureDraw2, style);
        }
    }
    
    public void prePopulating() {
        this.m_states.getPopulating().prePopulating();
    }
    
    public void postRender() {
        final SpriteRenderState rendering = this.m_states.getRendering();
        if (rendering.numSprites == 0 && rendering.stateUI.numSprites == 0) {
            rendering.onRendered();
            return;
        }
        TextureFBO.reset();
        IsoPuddles.VBOs.startFrame();
        GameProfiler.getInstance().invokeAndMeasure("buildStateUIDrawBuffer(UI)", this, rendering, SpriteRenderer::buildStateUIDrawBuffer);
        GameProfiler.getInstance().invokeAndMeasure("buildStateDrawBuffer", this, rendering, SpriteRenderer::buildStateDrawBuffer);
        rendering.onRendered();
        Core.getInstance().setLastRenderedFBO(rendering.fbo);
        this.notifyRenderStateQueue();
    }
    
    protected void buildStateDrawBuffer(final SpriteRenderState spriteRenderState) {
        SpriteRenderer.ringBuffer.begin();
        buildDrawBuffer(spriteRenderState.sprite, spriteRenderState.style, spriteRenderState.numSprites, SpriteRenderer.ringBuffer);
        GameProfiler.getInstance().invokeAndMeasure("ringBuffer.render", () -> SpriteRenderer.ringBuffer.render());
    }
    
    protected void buildStateUIDrawBuffer(final SpriteRenderState spriteRenderState) {
        if (spriteRenderState.stateUI.numSprites > 0) {
            SpriteRenderer.ringBuffer.begin();
            spriteRenderState.stateUI.bActive = true;
            buildDrawBuffer(spriteRenderState.stateUI.sprite, spriteRenderState.stateUI.style, spriteRenderState.stateUI.numSprites, SpriteRenderer.ringBuffer);
            SpriteRenderer.ringBuffer.render();
        }
        spriteRenderState.stateUI.bActive = false;
    }
    
    public void notifyRenderStateQueue() {
        synchronized (this.m_states) {
            this.m_states.notifyAll();
        }
    }
    
    public void glBuffer(final int n, final int n2) {
        this.m_states.getPopulatingActiveState().glBuffer(n, n2);
    }
    
    public void glDoStartFrame(final int n, final int n2, final float n3, final int n4) {
        this.m_states.getPopulatingActiveState().glDoStartFrame(n, n2, n3, n4);
    }
    
    public void glDoStartFrame(final int n, final int n2, final float n3, final int n4, final boolean b) {
        this.m_states.getPopulatingActiveState().glDoStartFrame(n, n2, n3, n4, b);
    }
    
    public void glDoStartFrameFx(final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().glDoStartFrameFx(n, n2, n3);
    }
    
    public void glIgnoreStyles(final boolean b) {
        this.m_states.getPopulatingActiveState().glIgnoreStyles(b);
    }
    
    public void glDoEndFrame() {
        this.m_states.getPopulatingActiveState().glDoEndFrame();
    }
    
    public void glDoEndFrameFx(final int n) {
        this.m_states.getPopulatingActiveState().glDoEndFrameFx(n);
    }
    
    public void doCoreIntParam(final int n, final float n2) {
        this.m_states.getPopulatingActiveState().doCoreIntParam(n, n2);
    }
    
    public void glTexParameteri(final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().glTexParameteri(n, n2, n3);
    }
    
    public void StartShader(final int n, final int n2) {
        this.m_states.getPopulatingActiveState().StartShader(n, n2);
    }
    
    public void EndShader() {
        this.m_states.getPopulatingActiveState().EndShader();
    }
    
    public void setCutawayTexture(final Texture texture, final int n, final int n2, final int n3, final int n4) {
        this.m_states.getPopulatingActiveState().setCutawayTexture(texture, n, n2, n3, n4);
    }
    
    public void clearCutawayTexture() {
        this.m_states.getPopulatingActiveState().clearCutawayTexture();
    }
    
    public void setUseVertColorsArray(final byte b, final int n, final int n2, final int n3, final int n4) {
        this.m_states.getPopulatingActiveState().setUseVertColorsArray(b, n, n2, n3, n4);
    }
    
    public void clearUseVertColorsArray() {
        this.m_states.getPopulatingActiveState().clearUseVertColorsArray();
    }
    
    public void setExtraWallShaderParams(final WallShaderTexRender extraWallShaderParams) {
        this.m_states.getPopulatingActiveState().setExtraWallShaderParams(extraWallShaderParams);
    }
    
    public void ShaderUpdate1i(final int n, final int n2, final int n3) {
        this.m_states.getPopulatingActiveState().ShaderUpdate1i(n, n2, n3);
    }
    
    public void ShaderUpdate1f(final int n, final int n2, final float n3) {
        this.m_states.getPopulatingActiveState().ShaderUpdate1f(n, n2, n3);
    }
    
    public void ShaderUpdate2f(final int n, final int n2, final float n3, final float n4) {
        this.m_states.getPopulatingActiveState().ShaderUpdate2f(n, n2, n3, n4);
    }
    
    public void ShaderUpdate3f(final int n, final int n2, final float n3, final float n4, final float n5) {
        this.m_states.getPopulatingActiveState().ShaderUpdate3f(n, n2, n3, n4, n5);
    }
    
    public void ShaderUpdate4f(final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        this.m_states.getPopulatingActiveState().ShaderUpdate4f(n, n2, n3, n4, n5, n6);
    }
    
    public void glLoadIdentity() {
        this.m_states.getPopulatingActiveState().glLoadIdentity();
    }
    
    public void glGenerateMipMaps(final int n) {
        this.m_states.getPopulatingActiveState().glGenerateMipMaps(n);
    }
    
    public void glBind(final int n) {
        this.m_states.getPopulatingActiveState().glBind(n);
    }
    
    public void glViewport(final int n, final int n2, final int n3, final int n4) {
        this.m_states.getPopulatingActiveState().glViewport(n, n2, n3, n4);
    }
    
    public void startOffscreenUI() {
        this.m_states.getPopulating().stateUI.bActive = true;
        this.m_states.getPopulating().stateUI.defaultStyle = TransparentStyle.instance;
        GLState.startFrame();
    }
    
    public void stopOffscreenUI() {
        this.m_states.getPopulating().stateUI.bActive = false;
    }
    
    public void pushFrameDown() {
        synchronized (this.m_states) {
            this.waitForReadySlotToOpen();
            this.m_states.movePopulatingToReady();
            this.notifyRenderStateQueue();
        }
    }
    
    public SpriteRenderState acquireStateForRendering(final BooleanSupplier booleanSupplier) {
        synchronized (this.m_states) {
            if (!this.waitForReadyState(booleanSupplier)) {
                return null;
            }
            this.m_states.moveReadyToRendering();
            this.notifyRenderStateQueue();
            return this.m_states.getRendering();
        }
    }
    
    private boolean waitForReadyState(final BooleanSupplier booleanSupplier) {
        try {
            s_performance.waitForReadyState.start();
            return this.waitForReadyStateInternal(booleanSupplier);
        }
        finally {
            s_performance.waitForReadyState.end();
        }
    }
    
    private boolean waitForReadyStateInternal(final BooleanSupplier booleanSupplier) {
        if (!RenderThread.isRunning() || this.m_states.getReady() != null) {
            return true;
        }
        if (!RenderThread.isWaitForRenderState() && !this.isWaitingForRenderState()) {
            return false;
        }
        while (this.m_states.getReady() == null) {
            try {
                if (!booleanSupplier.getAsBoolean()) {
                    return false;
                }
                this.m_states.wait();
            }
            catch (InterruptedException ex) {}
        }
        return true;
    }
    
    private void waitForReadySlotToOpen() {
        try {
            s_performance.waitForReadySlotToOpen.start();
            this.waitForReadySlotToOpenInternal();
        }
        finally {
            s_performance.waitForReadySlotToOpen.end();
        }
    }
    
    private void waitForReadySlotToOpenInternal() {
        if (this.m_states.getReady() == null || !RenderThread.isRunning()) {
            return;
        }
        this.m_waitingForRenderState = true;
        while (this.m_states.getReady() != null) {
            try {
                this.m_states.wait();
            }
            catch (InterruptedException ex) {}
        }
        this.m_waitingForRenderState = false;
    }
    
    public int getMainStateIndex() {
        return this.m_states.getPopulatingActiveState().index;
    }
    
    public int getRenderStateIndex() {
        return this.m_states.getRenderingActiveState().index;
    }
    
    public boolean getDoAdditive() {
        return this.m_states.getPopulatingActiveState().defaultStyle == AdditiveStyle.instance;
    }
    
    public void setDefaultStyle(final AbstractStyle defaultStyle) {
        this.m_states.getPopulatingActiveState().defaultStyle = defaultStyle;
    }
    
    public void setDoAdditive(final boolean b) {
        this.m_states.getPopulatingActiveState().defaultStyle = (b ? AdditiveStyle.instance : TransparentStyle.instance);
    }
    
    public void initFromIsoCamera(final int n) {
        this.m_states.getPopulating().playerCamera[n].initFromIsoCamera(n);
    }
    
    public void setRenderingPlayerIndex(final int playerIndex) {
        this.m_states.getRendering().playerIndex = playerIndex;
    }
    
    public int getRenderingPlayerIndex() {
        return this.m_states.getRendering().playerIndex;
    }
    
    public PlayerCamera getRenderingPlayerCamera(final int n) {
        return this.m_states.getRendering().playerCamera[n];
    }
    
    public SpriteRenderState getRenderingState() {
        return this.m_states.getRendering();
    }
    
    public SpriteRenderState getPopulatingState() {
        return this.m_states.getPopulating();
    }
    
    public boolean isMaxZoomLevel() {
        return this.getPlayerZoomLevel() >= this.getPlayerMaxZoom();
    }
    
    public boolean isMinZoomLevel() {
        return this.getPlayerZoomLevel() <= this.getPlayerMinZoom();
    }
    
    public float getPlayerZoomLevel() {
        final SpriteRenderState rendering = this.m_states.getRendering();
        return rendering.zoomLevel[rendering.playerIndex];
    }
    
    public float getPlayerMaxZoom() {
        return this.m_states.getRendering().maxZoomLevel;
    }
    
    public float getPlayerMinZoom() {
        return this.m_states.getRendering().minZoomLevel;
    }
    
    public boolean isWaitingForRenderState() {
        return this.m_waitingForRenderState;
    }
    
    static {
        instance = new SpriteRenderer();
        ringBuffer = new RingBuffer();
        SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
    }
    
    public static final class RingBuffer
    {
        GLVertexBufferObject[] vbo;
        GLVertexBufferObject[] ibo;
        long bufferSize;
        long bufferSizeInVertices;
        long indexBufferSize;
        int numBuffers;
        int sequence;
        int mark;
        FloatBuffer currentVertices;
        ShortBuffer currentIndices;
        FloatBuffer[] vertices;
        ByteBuffer[] verticesBytes;
        ShortBuffer[] indices;
        ByteBuffer[] indicesBytes;
        Texture lastRenderedTexture0;
        Texture currentTexture0;
        Texture lastRenderedTexture1;
        Texture currentTexture1;
        boolean shaderChangedTexture1;
        byte lastUseAttribArray;
        byte currentUseAttribArray;
        Style lastRenderedStyle;
        Style currentStyle;
        StateRun[] stateRun;
        public boolean restoreVBOs;
        public boolean restoreBoundTextures;
        int vertexCursor;
        int indexCursor;
        int numRuns;
        StateRun currentRun;
        public static boolean IGNORE_STYLES;
        
        RingBuffer() {
            this.sequence = -1;
            this.mark = -1;
            this.shaderChangedTexture1 = false;
        }
        
        void create() {
            GL11.glEnableClientState(32884);
            GL11.glEnableClientState(32886);
            GL11.glEnableClientState(32888);
            this.bufferSize = 65536L;
            this.numBuffers = (Core.bDebug ? 256 : 128);
            this.bufferSizeInVertices = this.bufferSize / 32L;
            this.indexBufferSize = this.bufferSizeInVertices * 3L;
            this.vertices = new FloatBuffer[this.numBuffers];
            this.verticesBytes = new ByteBuffer[this.numBuffers];
            this.indices = new ShortBuffer[this.numBuffers];
            this.indicesBytes = new ByteBuffer[this.numBuffers];
            this.stateRun = new StateRun[5000];
            for (int i = 0; i < 5000; ++i) {
                this.stateRun[i] = new StateRun();
            }
            this.vbo = new GLVertexBufferObject[this.numBuffers];
            this.ibo = new GLVertexBufferObject[this.numBuffers];
            for (int j = 0; j < this.numBuffers; ++j) {
                (this.vbo[j] = new GLVertexBufferObject(this.bufferSize, GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW())).create();
                (this.ibo[j] = new GLVertexBufferObject(this.indexBufferSize, GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW())).create();
            }
        }
        
        void add(final TextureDraw textureDraw, final TextureDraw textureDraw2, final Style style) {
            if (style == null) {
                return;
            }
            if (this.vertexCursor + 4 > this.bufferSizeInVertices || this.indexCursor + 6 > this.indexBufferSize) {
                this.render();
                this.next();
            }
            if (!this.prepareCurrentRun(textureDraw, textureDraw2, style)) {
                return;
            }
            final FloatBuffer currentVertices = this.currentVertices;
            final AlphaOp alphaOp = style.getAlphaOp();
            currentVertices.put(textureDraw.x0);
            currentVertices.put(textureDraw.y0);
            if (textureDraw.tex == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                if (textureDraw.flipped) {
                    currentVertices.put(textureDraw.u1);
                }
                else {
                    currentVertices.put(textureDraw.u0);
                }
                currentVertices.put(textureDraw.v0);
            }
            if (textureDraw.tex1 == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                currentVertices.put(textureDraw.tex1_u0);
                currentVertices.put(textureDraw.tex1_v0);
            }
            currentVertices.put(Float.intBitsToFloat((textureDraw.useAttribArray != -1) ? textureDraw.tex1_col0 : 0));
            alphaOp.op(textureDraw.getColor(0), 255, currentVertices);
            currentVertices.put(textureDraw.x1);
            currentVertices.put(textureDraw.y1);
            if (textureDraw.tex == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                if (textureDraw.flipped) {
                    currentVertices.put(textureDraw.u0);
                }
                else {
                    currentVertices.put(textureDraw.u1);
                }
                currentVertices.put(textureDraw.v1);
            }
            if (textureDraw.tex1 == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                currentVertices.put(textureDraw.tex1_u1);
                currentVertices.put(textureDraw.tex1_v1);
            }
            currentVertices.put(Float.intBitsToFloat((textureDraw.useAttribArray != -1) ? textureDraw.tex1_col1 : 0));
            alphaOp.op(textureDraw.getColor(1), 255, currentVertices);
            currentVertices.put(textureDraw.x2);
            currentVertices.put(textureDraw.y2);
            if (textureDraw.tex == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                if (textureDraw.flipped) {
                    currentVertices.put(textureDraw.u3);
                }
                else {
                    currentVertices.put(textureDraw.u2);
                }
                currentVertices.put(textureDraw.v2);
            }
            if (textureDraw.tex1 == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                currentVertices.put(textureDraw.tex1_u2);
                currentVertices.put(textureDraw.tex1_v2);
            }
            currentVertices.put(Float.intBitsToFloat((textureDraw.useAttribArray != -1) ? textureDraw.tex1_col2 : 0));
            alphaOp.op(textureDraw.getColor(2), 255, currentVertices);
            currentVertices.put(textureDraw.x3);
            currentVertices.put(textureDraw.y3);
            if (textureDraw.tex == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                if (textureDraw.flipped) {
                    currentVertices.put(textureDraw.u2);
                }
                else {
                    currentVertices.put(textureDraw.u3);
                }
                currentVertices.put(textureDraw.v3);
            }
            if (textureDraw.tex1 == null) {
                currentVertices.put(0.0f);
                currentVertices.put(0.0f);
            }
            else {
                currentVertices.put(textureDraw.tex1_u3);
                currentVertices.put(textureDraw.tex1_v3);
            }
            currentVertices.put(Float.intBitsToFloat((textureDraw.useAttribArray != -1) ? textureDraw.tex1_col3 : 0));
            alphaOp.op(textureDraw.getColor(3), 255, currentVertices);
            this.currentIndices.put((short)this.vertexCursor);
            this.currentIndices.put((short)(this.vertexCursor + 1));
            this.currentIndices.put((short)(this.vertexCursor + 2));
            this.currentIndices.put((short)this.vertexCursor);
            this.currentIndices.put((short)(this.vertexCursor + 2));
            this.currentIndices.put((short)(this.vertexCursor + 3));
            this.indexCursor += 6;
            this.vertexCursor += 4;
            final StateRun currentRun = this.currentRun;
            currentRun.endIndex += 6;
            final StateRun currentRun2 = this.currentRun;
            currentRun2.length += 4;
        }
        
        private boolean prepareCurrentRun(final TextureDraw e, final TextureDraw textureDraw, final Style style) {
            final Texture tex = e.tex;
            final Texture tex2 = e.tex1;
            final byte useAttribArray = e.useAttribArray;
            if (this.isStateChanged(e, textureDraw, style, tex, tex2, useAttribArray)) {
                this.currentRun = this.stateRun[this.numRuns];
                this.currentRun.start = this.vertexCursor;
                this.currentRun.length = 0;
                this.currentRun.style = style;
                this.currentRun.texture0 = tex;
                this.currentRun.texture1 = tex2;
                this.currentRun.useAttribArray = useAttribArray;
                this.currentRun.indices = this.currentIndices;
                this.currentRun.startIndex = this.indexCursor;
                this.currentRun.endIndex = this.indexCursor;
                ++this.numRuns;
                if (this.numRuns == this.stateRun.length) {
                    this.growStateRuns();
                }
                this.currentStyle = style;
                this.currentTexture0 = tex;
                this.currentTexture1 = tex2;
                this.currentUseAttribArray = useAttribArray;
            }
            if (e.type != TextureDraw.Type.glDraw) {
                this.currentRun.ops.add(e);
                return false;
            }
            return true;
        }
        
        private boolean isStateChanged(final TextureDraw textureDraw, final TextureDraw textureDraw2, final Style style, final Texture texture, final Texture texture2, final byte b) {
            if (this.currentRun == null) {
                return true;
            }
            if (textureDraw.type == TextureDraw.Type.DrawModel) {
                return true;
            }
            if (b != this.currentUseAttribArray) {
                return true;
            }
            if (texture != this.currentTexture0) {
                return true;
            }
            if (texture2 != this.currentTexture1) {
                return true;
            }
            if (textureDraw2 != null) {
                if (textureDraw2.type == TextureDraw.Type.DrawModel) {
                    return true;
                }
                if (textureDraw.type == TextureDraw.Type.glDraw && textureDraw2.type != TextureDraw.Type.glDraw) {
                    return true;
                }
                if (textureDraw.type != TextureDraw.Type.glDraw && textureDraw2.type == TextureDraw.Type.glDraw) {
                    return true;
                }
            }
            if (style != this.currentStyle) {
                if (this.currentStyle == null) {
                    return true;
                }
                if (style.getStyleID() != this.currentStyle.getStyleID()) {
                    return true;
                }
            }
            return false;
        }
        
        private void next() {
            ++this.sequence;
            if (this.sequence == this.numBuffers) {
                this.sequence = 0;
            }
            if (this.sequence == this.mark) {
                DebugLog.General.error((Object)"Buffer overrun.");
            }
            this.vbo[this.sequence].bind();
            final ByteBuffer map = this.vbo[this.sequence].map();
            if (this.vertices[this.sequence] == null || this.verticesBytes[this.sequence] != map) {
                this.verticesBytes[this.sequence] = map;
                this.vertices[this.sequence] = map.asFloatBuffer();
            }
            this.ibo[this.sequence].bind();
            final ByteBuffer map2 = this.ibo[this.sequence].map();
            if (this.indices[this.sequence] == null || this.indicesBytes[this.sequence] != map2) {
                this.indicesBytes[this.sequence] = map2;
                this.indices[this.sequence] = map2.asShortBuffer();
            }
            (this.currentVertices = this.vertices[this.sequence]).clear();
            (this.currentIndices = this.indices[this.sequence]).clear();
            this.vertexCursor = 0;
            this.indexCursor = 0;
            this.numRuns = 0;
            this.currentRun = null;
        }
        
        void begin() {
            this.currentStyle = null;
            this.currentTexture0 = null;
            this.currentTexture1 = null;
            this.currentUseAttribArray = -1;
            this.next();
            this.mark = this.sequence;
        }
        
        void render() {
            this.vbo[this.sequence].unmap();
            this.ibo[this.sequence].unmap();
            this.restoreVBOs = true;
            for (int i = 0; i < this.numRuns; ++i) {
                this.stateRun[i].render();
            }
        }
        
        void growStateRuns() {
            final StateRun[] stateRun = new StateRun[(int)(this.stateRun.length * 1.5f)];
            System.arraycopy(this.stateRun, 0, stateRun, 0, this.stateRun.length);
            for (int i = this.numRuns; i < stateRun.length; ++i) {
                stateRun[i] = new StateRun();
            }
            this.stateRun = stateRun;
        }
        
        public void shaderChangedTexture1() {
            this.shaderChangedTexture1 = true;
        }
        
        public void checkShaderChangedTexture1() {
            if (this.shaderChangedTexture1) {
                this.shaderChangedTexture1 = false;
                this.lastRenderedTexture1 = null;
                GL13.glActiveTexture(33985);
                GL13.glClientActiveTexture(33985);
                GL11.glDisable(3553);
                GL13.glActiveTexture(33984);
                GL13.glClientActiveTexture(33984);
            }
        }
        
        public void debugBoundTexture(final Texture texture, final int n) {
            if (GL11.glGetInteger(34016) == n) {
                final int glGetInteger = GL11.glGetInteger(32873);
                String s = null;
                if (texture == null && glGetInteger != 0) {
                    for (final Texture texture2 : TextureAssetManager.instance.getAssetTable().values()) {
                        if (texture2.getID() == glGetInteger) {
                            s = texture2.getPath().getPath();
                            break;
                        }
                    }
                    DebugLog.General.error(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, glGetInteger, s));
                }
                else if (texture != null && texture.getID() != -1 && glGetInteger != texture.getID()) {
                    for (final Texture texture3 : TextureAssetManager.instance.getAssetTable().values()) {
                        if (texture3.getID() == glGetInteger) {
                            s = texture3.getName();
                            break;
                        }
                    }
                    DebugLog.General.error(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, texture.getID(), glGetInteger, s));
                }
            }
        }
        
        static {
            RingBuffer.IGNORE_STYLES = false;
        }
        
        private class StateRun
        {
            Texture texture0;
            Texture texture1;
            byte useAttribArray;
            Style style;
            int start;
            int length;
            ShortBuffer indices;
            int startIndex;
            int endIndex;
            final ArrayList<TextureDraw> ops;
            
            private StateRun() {
                this.useAttribArray = -1;
                this.ops = new ArrayList<TextureDraw>();
            }
            
            @Override
            public String toString() {
                final String lineSeparator = System.lineSeparator();
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lzombie/core/textures/Texture;Ljava/lang/String;Lzombie/core/textures/Texture;Ljava/lang/String;BLjava/lang/String;Lzombie/core/Styles/Style;Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;Ljava/nio/ShortBuffer;Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), lineSeparator, PZArrayUtil.arrayToString(this.ops, "{", "}", ", "), lineSeparator, this.texture0, lineSeparator, this.texture1, lineSeparator, this.useAttribArray, lineSeparator, this.style, lineSeparator, this.start, lineSeparator, this.length, lineSeparator, this.indices, lineSeparator, this.startIndex, lineSeparator, this.endIndex, lineSeparator);
            }
            
            void render() {
                if (this.style == null) {
                    return;
                }
                final int size = this.ops.size();
                if (size > 0) {
                    for (int i = 0; i < size; ++i) {
                        this.ops.get(i).run();
                    }
                    this.ops.clear();
                    return;
                }
                if (this.style != RingBuffer.this.lastRenderedStyle) {
                    if (RingBuffer.this.lastRenderedStyle != null && (!RingBuffer.IGNORE_STYLES || (RingBuffer.this.lastRenderedStyle != AdditiveStyle.instance && RingBuffer.this.lastRenderedStyle != TransparentStyle.instance && RingBuffer.this.lastRenderedStyle != LightingStyle.instance))) {
                        RingBuffer.this.lastRenderedStyle.resetState();
                    }
                    if (this.style != null && (!RingBuffer.IGNORE_STYLES || (this.style != AdditiveStyle.instance && this.style != TransparentStyle.instance && this.style != LightingStyle.instance))) {
                        this.style.setupState();
                    }
                    RingBuffer.this.lastRenderedStyle = this.style;
                }
                if (RingBuffer.this.lastRenderedTexture0 != null && RingBuffer.this.lastRenderedTexture0.getID() != Texture.lastTextureID) {
                    RingBuffer.this.restoreBoundTextures = true;
                }
                if (RingBuffer.this.restoreBoundTextures) {
                    GL11.glBindTexture(3553, Texture.lastTextureID = 0);
                    if (this.texture0 == null) {
                        GL11.glDisable(3553);
                    }
                    RingBuffer.this.lastRenderedTexture0 = null;
                    RingBuffer.this.lastRenderedTexture1 = null;
                    RingBuffer.this.restoreBoundTextures = false;
                }
                if (this.texture0 != RingBuffer.this.lastRenderedTexture0) {
                    if (this.texture0 != null) {
                        if (RingBuffer.this.lastRenderedTexture0 == null) {
                            GL11.glEnable(3553);
                        }
                        this.texture0.bind();
                    }
                    else {
                        GL11.glDisable(3553);
                        GL11.glBindTexture(3553, Texture.lastTextureID = 0);
                    }
                    RingBuffer.this.lastRenderedTexture0 = this.texture0;
                }
                if (DebugOptions.instance.Checks.BoundTextures.getValue()) {
                    RingBuffer.this.debugBoundTexture(RingBuffer.this.lastRenderedTexture0, 33984);
                }
                if (this.texture1 != RingBuffer.this.lastRenderedTexture1) {
                    GL13.glActiveTexture(33985);
                    GL13.glClientActiveTexture(33985);
                    if (this.texture1 != null) {
                        GL11.glBindTexture(3553, this.texture1.getID());
                    }
                    else {
                        GL11.glDisable(3553);
                    }
                    GL13.glActiveTexture(33984);
                    GL13.glClientActiveTexture(33984);
                    RingBuffer.this.lastRenderedTexture1 = this.texture1;
                }
                if (this.useAttribArray != RingBuffer.this.lastUseAttribArray) {
                    if (this.useAttribArray != -1) {
                        if (this.useAttribArray == 1) {
                            final int a_wallShadeColor = IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor;
                            if (a_wallShadeColor != -1) {
                                GL20.glEnableVertexAttribArray(a_wallShadeColor);
                            }
                        }
                        if (this.useAttribArray == 2) {
                            final int a_wallShadeColor2 = IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor;
                            if (a_wallShadeColor2 != -1) {
                                GL20.glEnableVertexAttribArray(a_wallShadeColor2);
                            }
                        }
                    }
                    else {
                        if (RingBuffer.this.lastUseAttribArray == 1) {
                            final int a_wallShadeColor3 = IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor;
                            if (a_wallShadeColor3 != -1) {
                                GL20.glDisableVertexAttribArray(a_wallShadeColor3);
                            }
                        }
                        if (RingBuffer.this.lastUseAttribArray == 2) {
                            final int a_wallShadeColor4 = IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor;
                            if (a_wallShadeColor4 != -1) {
                                GL20.glDisableVertexAttribArray(a_wallShadeColor4);
                            }
                        }
                    }
                    RingBuffer.this.lastUseAttribArray = this.useAttribArray;
                }
                if (this.length == 0) {
                    return;
                }
                if (this.length == -1) {
                    RingBuffer.this.restoreVBOs = true;
                    return;
                }
                if (RingBuffer.this.restoreVBOs) {
                    RingBuffer.this.restoreVBOs = false;
                    RingBuffer.this.vbo[RingBuffer.this.sequence].bind();
                    RingBuffer.this.ibo[RingBuffer.this.sequence].bind();
                    GL11.glVertexPointer(2, 5126, 32, 0L);
                    GL11.glTexCoordPointer(2, 5126, 32, 8L);
                    GL11.glColorPointer(4, 5121, 32, 28L);
                    GL13.glActiveTexture(33985);
                    GL13.glClientActiveTexture(33985);
                    GL11.glTexCoordPointer(2, 5126, 32, 16L);
                    GL11.glEnableClientState(32888);
                    final int a_wallShadeColor5 = IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor;
                    if (a_wallShadeColor5 != -1) {
                        GL20.glVertexAttribPointer(a_wallShadeColor5, 4, 5121, true, 32, 24L);
                    }
                    final int a_wallShadeColor6 = IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor;
                    if (a_wallShadeColor6 != -1) {
                        GL20.glVertexAttribPointer(a_wallShadeColor6, 4, 5121, true, 32, 24L);
                    }
                    GL13.glActiveTexture(33984);
                    GL13.glClientActiveTexture(33984);
                }
                assert GL11.glGetInteger(34964) == RingBuffer.this.vbo[RingBuffer.this.sequence].getID();
                if (this.useAttribArray == 1) {
                    RingBuffer.this.vbo[RingBuffer.this.sequence].enableVertexAttribArray(IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor);
                    assert GL20.glGetVertexAttribi(IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor, 34975) != 0;
                }
                else if (this.useAttribArray == 2) {
                    RingBuffer.this.vbo[RingBuffer.this.sequence].enableVertexAttribArray(IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor);
                }
                else {
                    RingBuffer.this.vbo[RingBuffer.this.sequence].disableVertexAttribArray();
                }
                if (this.style.getRenderSprite()) {
                    GL12.glDrawRangeElements(4, this.start, this.start + this.length, this.endIndex - this.startIndex, 5123, this.startIndex * 2L);
                }
                else {
                    this.style.render(this.start, this.startIndex);
                }
            }
        }
    }
    
    public enum WallShaderTexRender
    {
        All, 
        LeftOnly, 
        RightOnly;
        
        private static /* synthetic */ WallShaderTexRender[] $values() {
            return new WallShaderTexRender[] { WallShaderTexRender.All, WallShaderTexRender.LeftOnly, WallShaderTexRender.RightOnly };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static class s_performance
    {
        private static final PerformanceProfileProbe waitForReadyState;
        private static final PerformanceProfileProbe waitForReadySlotToOpen;
        
        static {
            waitForReadyState = new PerformanceProfileProbe("waitForReadyState");
            waitForReadySlotToOpen = new PerformanceProfileProbe("waitForReadySlotToOpen");
        }
    }
}
