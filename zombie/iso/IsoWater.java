// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Arrays;
import zombie.GameTime;
import zombie.iso.weather.ClimateManager;
import zombie.interfaces.ITexture;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL11;
import zombie.core.SpriteRenderer;
import java.util.ArrayList;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderThread;
import org.lwjgl.opengl.GL;
import org.joml.Vector4f;
import zombie.core.textures.Texture;
import org.joml.Vector2f;
import zombie.core.opengl.Shader;

public final class IsoWater
{
    public Shader Effect;
    private float WaterTime;
    private float WaterWindAngle;
    private float WaterWindIntensity;
    private float WaterRainIntensity;
    private Vector2f WaterParamWindINT;
    private Texture texBottom;
    private int apiId;
    private static IsoWater instance;
    private static boolean isShaderEnable;
    private final RenderData[][] renderData;
    private final RenderData[][] renderDataShore;
    static final int BYTES_PER_FLOAT = 4;
    static final int FLOATS_PER_VERTEX = 7;
    static final int BYTES_PER_VERTEX = 28;
    static final int VERTICES_PER_SQUARE = 4;
    private final Vector4f shaderOffset;
    
    public static synchronized IsoWater getInstance() {
        if (IsoWater.instance == null) {
            IsoWater.instance = new IsoWater();
        }
        return IsoWater.instance;
    }
    
    public boolean getShaderEnable() {
        return IsoWater.isShaderEnable;
    }
    
    public IsoWater() {
        this.renderData = new RenderData[3][4];
        this.renderDataShore = new RenderData[3][4];
        this.shaderOffset = new Vector4f();
        this.texBottom = Texture.getSharedTexture("media/textures/river_bottom.png");
        RenderThread.invokeOnRenderContext(() -> {
            if (GL.getCapabilities().OpenGL30) {
                this.apiId = 1;
            }
            if (GL.getCapabilities().GL_ARB_framebuffer_object) {
                this.apiId = 2;
            }
            if (GL.getCapabilities().GL_EXT_framebuffer_object) {
                this.apiId = 3;
            }
            return;
        });
        for (int i = 0; i < this.renderData.length; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.renderData[i][j] = new RenderData();
                this.renderDataShore[i][j] = new RenderData();
            }
        }
        this.applyWaterQuality();
        this.WaterParamWindINT = new Vector2f(0.0f);
    }
    
    public void applyWaterQuality() {
        if (PerformanceSettings.WaterQuality == 2) {
            IsoWater.isShaderEnable = false;
        }
        if (PerformanceSettings.WaterQuality == 1) {
            IsoWater.isShaderEnable = true;
            RenderThread.invokeOnRenderContext(() -> {
                ARBShaderObjects.glUseProgramObjectARB(0);
                this.Effect = new WaterShader("water");
                ARBShaderObjects.glUseProgramObjectARB(0);
                return;
            });
        }
        if (PerformanceSettings.WaterQuality == 0) {
            IsoWater.isShaderEnable = true;
            RenderThread.invokeOnRenderContext(() -> {
                (this.Effect = new WaterShader("water_hq")).Start();
                this.Effect.End();
            });
        }
    }
    
    public void render(final ArrayList<IsoGridSquare> list, final boolean b) {
        if (!this.getShaderEnable()) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final int mainStateIndex = SpriteRenderer.instance.getMainStateIndex();
        final RenderData renderData = this.renderData[mainStateIndex][playerIndex];
        final RenderData renderData2 = this.renderDataShore[mainStateIndex][playerIndex];
        if (b) {
            if (renderData2.numSquares > 0) {
                SpriteRenderer.instance.drawWater(this.Effect, playerIndex, this.apiId, true);
            }
            return;
        }
        renderData.clear();
        renderData2.clear();
        for (int i = 0; i < list.size(); ++i) {
            final IsoGridSquare isoGridSquare = list.get(i);
            if (isoGridSquare.chunk == null || !isoGridSquare.chunk.bLightingNeverDone[playerIndex]) {
                final IsoWaterGeometry water = isoGridSquare.getWater();
                if (water != null) {
                    if (water.bShore) {
                        renderData2.addSquare(water);
                    }
                    else if (water.hasWater) {
                        renderData.addSquare(water);
                    }
                }
            }
        }
        if (renderData.numSquares == 0) {
            return;
        }
        SpriteRenderer.instance.drawWater(this.Effect, playerIndex, this.apiId, false);
    }
    
    public void waterProjection() {
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(SpriteRenderer.instance.getRenderingPlayerIndex());
        GL11.glOrtho((double)renderingPlayerCamera.getOffX(), (double)(renderingPlayerCamera.getOffX() + renderingPlayerCamera.OffscreenWidth), (double)(renderingPlayerCamera.getOffY() + renderingPlayerCamera.OffscreenHeight), (double)renderingPlayerCamera.getOffY(), -1.0, 1.0);
    }
    
    public void waterGeometry(final boolean b) {
        System.nanoTime();
        final int renderStateIndex = SpriteRenderer.instance.getRenderStateIndex();
        final int renderingPlayerIndex = SpriteRenderer.instance.getRenderingPlayerIndex();
        final RenderData renderData = b ? this.renderDataShore[renderStateIndex][renderingPlayerIndex] : this.renderData[renderStateIndex][renderingPlayerIndex];
        int n = 0;
        int renderSome;
        for (int i = renderData.numSquares; i > 0; i -= renderSome) {
            renderSome = this.renderSome(n, i, b);
            n += renderSome;
        }
        System.nanoTime();
        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }
    
    private int renderSome(final int n, final int n2, final boolean b) {
        IsoPuddles.VBOs.next();
        final FloatBuffer vertices = IsoPuddles.VBOs.vertices;
        final ShortBuffer indices = IsoPuddles.VBOs.indices;
        GL13.glActiveTexture(33985);
        GL13.glClientActiveTexture(33985);
        GL11.glTexCoordPointer(2, 5126, 28, 8L);
        GL11.glEnableClientState(32888);
        GL13.glActiveTexture(33984);
        GL13.glClientActiveTexture(33984);
        GL11.glTexCoordPointer(2, 5126, 28, 0L);
        GL11.glColorPointer(4, 5121, 28, 24L);
        GL11.glVertexPointer(2, 5126, 28, 16L);
        final int renderStateIndex = SpriteRenderer.instance.getRenderStateIndex();
        final int renderingPlayerIndex = SpriteRenderer.instance.getRenderingPlayerIndex();
        final RenderData renderData = b ? this.renderDataShore[renderStateIndex][renderingPlayerIndex] : this.renderData[renderStateIndex][renderingPlayerIndex];
        final int min = Math.min(n2 * 4, IsoPuddles.VBOs.bufferSizeVertices);
        vertices.put(renderData.data, n * 4 * 7, min * 7);
        int n3 = 0;
        int n4 = 0;
        for (int i = 0; i < min / 4; ++i) {
            indices.put((short)n3);
            indices.put((short)(n3 + 1));
            indices.put((short)(n3 + 2));
            indices.put((short)n3);
            indices.put((short)(n3 + 2));
            indices.put((short)(n3 + 3));
            n3 += 4;
            n4 += 6;
        }
        IsoPuddles.VBOs.unmap();
        final int n5 = 0;
        final int n6 = n3;
        final int n7 = 0;
        GL12.glDrawRangeElements(4, n5, n5 + n6, n4 - n7, 5123, (long)(n7 * 2));
        return min / 4;
    }
    
    public ITexture getTextureBottom() {
        return this.texBottom;
    }
    
    public float getShaderTime() {
        return this.WaterTime;
    }
    
    public float getRainIntensity() {
        return this.WaterRainIntensity;
    }
    
    public void update(final ClimateManager climateManager) {
        this.WaterWindAngle = climateManager.getCorrectedWindAngleIntensity();
        this.WaterWindIntensity = climateManager.getWindIntensity() * 5.0f;
        this.WaterRainIntensity = climateManager.getRainIntensity();
        this.WaterTime += 0.0166f * GameTime.getInstance().getMultiplier();
        this.WaterParamWindINT.add((float)Math.sin(this.WaterWindAngle * 6.0f) * this.WaterWindIntensity * 0.05f, (float)Math.cos(this.WaterWindAngle * 6.0f) * this.WaterWindIntensity * 0.15f);
    }
    
    public float getWaterWindX() {
        return this.WaterParamWindINT.x;
    }
    
    public float getWaterWindY() {
        return this.WaterParamWindINT.y;
    }
    
    public float getWaterWindSpeed() {
        return this.WaterWindIntensity * 2.0f;
    }
    
    public Vector4f getShaderOffset() {
        final int renderingPlayerIndex = SpriteRenderer.instance.getRenderingPlayerIndex();
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(renderingPlayerIndex);
        return this.shaderOffset.set(renderingPlayerCamera.getOffX() - IsoCamera.getOffscreenLeft(renderingPlayerIndex) * renderingPlayerCamera.zoom, renderingPlayerCamera.getOffY() + IsoCamera.getOffscreenTop(renderingPlayerIndex) * renderingPlayerCamera.zoom, (float)renderingPlayerCamera.OffscreenWidth, (float)renderingPlayerCamera.OffscreenHeight);
    }
    
    public void FBOStart() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
    }
    
    public void FBOEnd() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
    }
    
    static {
        IsoWater.isShaderEnable = false;
    }
    
    private static final class RenderData
    {
        int numSquares;
        int capacity;
        float[] data;
        
        private RenderData() {
            this.capacity = 512;
        }
        
        void clear() {
            this.numSquares = 0;
        }
        
        void addSquare(final IsoWaterGeometry isoWaterGeometry) {
            final int playerIndex = IsoCamera.frameState.playerIndex;
            final int n = 4;
            if (this.data == null) {
                this.data = new float[this.capacity * n * 7];
            }
            if (this.numSquares + 1 > this.capacity) {
                this.capacity += 128;
                this.data = Arrays.copyOf(this.data, this.capacity * n * 7);
            }
            int n2 = this.numSquares * n * 7;
            for (int i = 0; i < 4; ++i) {
                this.data[n2++] = isoWaterGeometry.depth[i];
                this.data[n2++] = isoWaterGeometry.flow[i];
                this.data[n2++] = isoWaterGeometry.speed[i];
                this.data[n2++] = isoWaterGeometry.IsExternal;
                this.data[n2++] = isoWaterGeometry.x[i];
                this.data[n2++] = isoWaterGeometry.y[i];
                if (isoWaterGeometry.square != null) {
                    this.data[n2++] = Float.intBitsToFloat(isoWaterGeometry.square.getVertLight((4 - i) % 4, playerIndex));
                }
                else {
                    ++n2;
                }
            }
            ++this.numSquares;
        }
    }
}
