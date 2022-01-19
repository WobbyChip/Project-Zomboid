// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Arrays;
import zombie.debug.DebugLog;
import zombie.interfaces.ITexture;
import zombie.GameTime;
import zombie.iso.weather.ClimateManager;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL11;
import zombie.debug.DebugOptions;
import java.util.ArrayList;
import zombie.core.SpriteRenderer;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderThread;
import org.lwjgl.opengl.GL;
import zombie.core.Core;
import zombie.network.GameServer;
import org.lwjglx.BufferUtils;
import java.nio.FloatBuffer;
import org.joml.Vector4f;
import zombie.core.opengl.SharedVertexBufferObjects;
import zombie.core.textures.Texture;
import org.joml.Vector2f;
import zombie.core.opengl.Shader;

public final class IsoPuddles
{
    public Shader Effect;
    private float PuddlesWindAngle;
    private float PuddlesWindIntensity;
    private float PuddlesTime;
    private final Vector2f PuddlesParamWindINT;
    public static boolean leakingPuddlesInTheRoom;
    private Texture texHM;
    private int apiId;
    private static IsoPuddles instance;
    private static boolean isShaderEnable;
    static final int BYTES_PER_FLOAT = 4;
    static final int FLOATS_PER_VERTEX = 7;
    static final int BYTES_PER_VERTEX = 28;
    static final int VERTICES_PER_SQUARE = 4;
    public static final SharedVertexBufferObjects VBOs;
    private final RenderData[][] renderData;
    private final Vector4f shaderOffset;
    private final Vector4f shaderOffsetMain;
    private FloatBuffer floatBuffer;
    public static final int BOOL_MAX = 0;
    public static final int FLOAT_RAIN = 0;
    public static final int FLOAT_WETGROUND = 1;
    public static final int FLOAT_MUDDYPUDDLES = 2;
    public static final int FLOAT_PUDDLESSIZE = 3;
    public static final int FLOAT_RAININTENSITY = 4;
    public static final int FLOAT_MAX = 5;
    private PuddlesFloat rain;
    private PuddlesFloat wetGround;
    private PuddlesFloat muddyPuddles;
    private PuddlesFloat puddlesSize;
    private PuddlesFloat rainIntensity;
    private final PuddlesFloat[] climateFloats;
    
    public static synchronized IsoPuddles getInstance() {
        if (IsoPuddles.instance == null) {
            IsoPuddles.instance = new IsoPuddles();
        }
        return IsoPuddles.instance;
    }
    
    public boolean getShaderEnable() {
        return IsoPuddles.isShaderEnable;
    }
    
    public IsoPuddles() {
        this.renderData = new RenderData[3][4];
        this.shaderOffset = new Vector4f();
        this.shaderOffsetMain = new Vector4f();
        this.floatBuffer = BufferUtils.createFloatBuffer(16);
        this.climateFloats = new PuddlesFloat[5];
        if (GameServer.bServer) {
            Core.getInstance().setPerfPuddles(3);
            this.applyPuddlesQuality();
            this.PuddlesParamWindINT = new Vector2f(0.0f);
            this.setup();
            return;
        }
        this.texHM = Texture.getSharedTexture("media/textures/puddles_hm.png");
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
        this.applyPuddlesQuality();
        this.PuddlesParamWindINT = new Vector2f(0.0f);
        for (int i = 0; i < this.renderData.length; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.renderData[i][j] = new RenderData();
            }
        }
        this.setup();
    }
    
    public void applyPuddlesQuality() {
        IsoPuddles.leakingPuddlesInTheRoom = (Core.getInstance().getPerfPuddles() == 0);
        if (Core.getInstance().getPerfPuddles() == 3) {
            IsoPuddles.isShaderEnable = false;
        }
        else {
            IsoPuddles.isShaderEnable = true;
            if (PerformanceSettings.PuddlesQuality == 2) {
                RenderThread.invokeOnRenderContext(() -> {
                    (this.Effect = new PuddlesShader("puddles_lq")).Start();
                    this.Effect.End();
                    return;
                });
            }
            if (PerformanceSettings.PuddlesQuality == 1) {
                RenderThread.invokeOnRenderContext(() -> {
                    (this.Effect = new PuddlesShader("puddles_mq")).Start();
                    this.Effect.End();
                    return;
                });
            }
            if (PerformanceSettings.PuddlesQuality == 0) {
                RenderThread.invokeOnRenderContext(() -> {
                    (this.Effect = new PuddlesShader("puddles_hq")).Start();
                    this.Effect.End();
                });
            }
        }
    }
    
    public Vector4f getShaderOffset() {
        final int renderingPlayerIndex = SpriteRenderer.instance.getRenderingPlayerIndex();
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(renderingPlayerIndex);
        return this.shaderOffset.set(renderingPlayerCamera.getOffX() - IsoCamera.getOffscreenLeft(renderingPlayerIndex) * renderingPlayerCamera.zoom, renderingPlayerCamera.getOffY() + IsoCamera.getOffscreenTop(renderingPlayerIndex) * renderingPlayerCamera.zoom, (float)renderingPlayerCamera.OffscreenWidth, (float)renderingPlayerCamera.OffscreenHeight);
    }
    
    public Vector4f getShaderOffsetMain() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final PlayerCamera playerCamera = IsoCamera.cameras[playerIndex];
        return this.shaderOffsetMain.set(playerCamera.getOffX() - IsoCamera.getOffscreenLeft(playerIndex) * playerCamera.zoom, playerCamera.getOffY() + IsoCamera.getOffscreenTop(playerIndex) * playerCamera.zoom, (float)IsoCamera.getOffscreenWidth(playerIndex), (float)IsoCamera.getOffscreenHeight(playerIndex));
    }
    
    public void render(final ArrayList<IsoGridSquare> list, final int n) {
        if (!DebugOptions.instance.Weather.WaterPuddles.getValue()) {
            return;
        }
        final int mainStateIndex = SpriteRenderer.instance.getMainStateIndex();
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final RenderData renderData = this.renderData[mainStateIndex][playerIndex];
        if (n == 0) {
            renderData.clear();
        }
        if (list.isEmpty()) {
            return;
        }
        if (!this.getShaderEnable()) {
            return;
        }
        if (!Core.getInstance().getUseShaders()) {
            return;
        }
        if (Core.getInstance().getPerfPuddles() == 3) {
            return;
        }
        if (n > 0 && Core.getInstance().getPerfPuddles() > 0) {
            return;
        }
        if (this.wetGround.getFinalValue() == 0.0 && this.puddlesSize.getFinalValue() == 0.0) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            final IsoPuddlesGeometry puddles = list.get(i).getPuddles();
            if (puddles != null && puddles.shouldRender()) {
                puddles.updateLighting(playerIndex);
                renderData.addSquare(n, puddles);
            }
        }
        if (renderData.squaresPerLevel[n] <= 0) {
            return;
        }
        SpriteRenderer.instance.drawPuddles(this.Effect, playerIndex, this.apiId, n);
    }
    
    public void puddlesProjection() {
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(SpriteRenderer.instance.getRenderingPlayerIndex());
        GL11.glOrtho((double)renderingPlayerCamera.getOffX(), (double)(renderingPlayerCamera.getOffX() + renderingPlayerCamera.OffscreenWidth), (double)(renderingPlayerCamera.getOffY() + renderingPlayerCamera.OffscreenHeight), (double)renderingPlayerCamera.getOffY(), -1.0, 1.0);
    }
    
    public void puddlesGeometry(final int n) {
        final RenderData renderData = this.renderData[SpriteRenderer.instance.getRenderStateIndex()][SpriteRenderer.instance.getRenderingPlayerIndex()];
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            n2 += renderData.squaresPerLevel[i];
        }
        int renderSome;
        for (int j = renderData.squaresPerLevel[n]; j > 0; j -= renderSome) {
            renderSome = this.renderSome(n2, j);
            n2 += renderSome;
        }
        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }
    
    private int renderSome(final int n, final int n2) {
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
        final RenderData renderData = this.renderData[SpriteRenderer.instance.getRenderStateIndex()][SpriteRenderer.instance.getRenderingPlayerIndex()];
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
    
    public void update(final ClimateManager climateManager) {
        this.PuddlesWindAngle = climateManager.getCorrectedWindAngleIntensity();
        this.PuddlesWindIntensity = climateManager.getWindIntensity();
        this.rain.setFinalValue(climateManager.getRainIntensity());
        final float n = GameTime.getInstance().getMultiplier() / 1.6f;
        final float n2 = 2.0E-5f * n * climateManager.getTemperature();
        final float n3 = 2.0E-5f * n;
        final float n4 = 2.0E-4f * n;
        final float finalValue = this.rain.getFinalValue();
        final float n5 = finalValue * finalValue * 0.05f * n;
        this.rainIntensity.setFinalValue(this.rain.getFinalValue() * 2.0f);
        this.wetGround.addFinalValue(n5);
        this.muddyPuddles.addFinalValue(n5 * 2.0f);
        this.puddlesSize.addFinalValueForMax(n5 * 0.01f, 0.7f);
        if (n5 == 0.0) {
            this.wetGround.addFinalValue(-n2);
            this.muddyPuddles.addFinalValue(-n4);
        }
        if (this.wetGround.getFinalValue() == 0.0) {
            this.puddlesSize.addFinalValue(-n3);
        }
        this.PuddlesTime += 0.0166f * GameTime.getInstance().getMultiplier();
        this.PuddlesParamWindINT.add((float)Math.sin(this.PuddlesWindAngle * 6.0f) * this.PuddlesWindIntensity * 0.05f, (float)Math.cos(this.PuddlesWindAngle * 6.0f) * this.PuddlesWindIntensity * 0.05f);
    }
    
    public float getShaderTime() {
        return this.PuddlesTime;
    }
    
    public float getPuddlesSize() {
        return this.puddlesSize.getFinalValue();
    }
    
    public ITexture getHMTexture() {
        return this.texHM;
    }
    
    public FloatBuffer getPuddlesParams(final int n) {
        this.floatBuffer.clear();
        this.floatBuffer.put(this.PuddlesParamWindINT.x);
        this.floatBuffer.put(this.muddyPuddles.getFinalValue());
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(this.PuddlesParamWindINT.y);
        this.floatBuffer.put(this.wetGround.getFinalValue());
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(this.PuddlesWindIntensity * 1.0f);
        this.floatBuffer.put(this.puddlesSize.getFinalValue());
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put((float)n);
        this.floatBuffer.put(this.rainIntensity.getFinalValue());
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }
    
    public float getRainIntensity() {
        return this.rainIntensity.getFinalValue();
    }
    
    public int getFloatMax() {
        return 5;
    }
    
    public int getBoolMax() {
        return 0;
    }
    
    public PuddlesFloat getPuddlesFloat(final int n) {
        if (n >= 0 && n < 5) {
            return this.climateFloats[n];
        }
        DebugLog.log("ERROR: Climate: cannot get float override id.");
        return null;
    }
    
    private PuddlesFloat initClimateFloat(final int n, final String s) {
        if (n >= 0 && n < 5) {
            return this.climateFloats[n].init(n, s);
        }
        DebugLog.log("ERROR: Climate: cannot get float override id.");
        return null;
    }
    
    private void setup() {
        for (int i = 0; i < this.climateFloats.length; ++i) {
            this.climateFloats[i] = new PuddlesFloat();
        }
        this.rain = this.initClimateFloat(0, "INPUT: RAIN");
        this.wetGround = this.initClimateFloat(1, "Wet Ground");
        this.muddyPuddles = this.initClimateFloat(2, "Muddy Puddles");
        this.puddlesSize = this.initClimateFloat(3, "Puddles Size");
        this.rainIntensity = this.initClimateFloat(4, "Rain Intensity");
    }
    
    static {
        IsoPuddles.leakingPuddlesInTheRoom = false;
        IsoPuddles.isShaderEnable = false;
        VBOs = new SharedVertexBufferObjects(28);
    }
    
    private static final class RenderData
    {
        final int[] squaresPerLevel;
        int numSquares;
        int capacity;
        float[] data;
        
        RenderData() {
            this.squaresPerLevel = new int[8];
            this.capacity = 512;
        }
        
        void clear() {
            this.numSquares = 0;
            Arrays.fill(this.squaresPerLevel, 0);
        }
        
        void addSquare(final int n, final IsoPuddlesGeometry isoPuddlesGeometry) {
            final int n2 = 4;
            if (this.data == null) {
                this.data = new float[this.capacity * n2 * 7];
            }
            if (this.numSquares + 1 > this.capacity) {
                this.capacity += 128;
                this.data = Arrays.copyOf(this.data, this.capacity * n2 * 7);
            }
            int n3 = this.numSquares * n2 * 7;
            for (int i = 0; i < 4; ++i) {
                this.data[n3++] = isoPuddlesGeometry.pdne[i];
                this.data[n3++] = isoPuddlesGeometry.pdnw[i];
                this.data[n3++] = isoPuddlesGeometry.pda[i];
                this.data[n3++] = isoPuddlesGeometry.pnon[i];
                this.data[n3++] = isoPuddlesGeometry.x[i];
                this.data[n3++] = isoPuddlesGeometry.y[i];
                this.data[n3++] = Float.intBitsToFloat(isoPuddlesGeometry.color[i]);
            }
            ++this.numSquares;
            final int[] squaresPerLevel = this.squaresPerLevel;
            ++squaresPerLevel[n];
        }
    }
    
    public static class PuddlesFloat
    {
        protected float finalValue;
        private boolean isAdminOverride;
        private float adminValue;
        private float min;
        private float max;
        private float delta;
        private int ID;
        private String name;
        
        public PuddlesFloat() {
            this.isAdminOverride = false;
            this.min = 0.0f;
            this.max = 1.0f;
            this.delta = 0.01f;
        }
        
        public PuddlesFloat init(final int id, final String name) {
            this.ID = id;
            this.name = name;
            return this;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public String getName() {
            return this.name;
        }
        
        public float getMin() {
            return this.min;
        }
        
        public float getMax() {
            return this.max;
        }
        
        public void setEnableAdmin(final boolean isAdminOverride) {
            this.isAdminOverride = isAdminOverride;
        }
        
        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }
        
        public void setAdminValue(final float b) {
            this.adminValue = Math.max(this.min, Math.min(this.max, b));
        }
        
        public float getAdminValue() {
            return this.adminValue;
        }
        
        public void setFinalValue(final float b) {
            this.finalValue = Math.max(this.min, Math.min(this.max, b));
        }
        
        public void addFinalValue(final float n) {
            this.finalValue = Math.max(this.min, Math.min(this.max, this.finalValue + n));
        }
        
        public void addFinalValueForMax(final float n, final float a) {
            this.finalValue = Math.max(this.min, Math.min(a, this.finalValue + n));
        }
        
        public float getFinalValue() {
            if (this.isAdminOverride) {
                return this.adminValue;
            }
            return this.finalValue;
        }
        
        public void interpolateFinalValue(final float finalValue) {
            if (Math.abs(this.finalValue - finalValue) < this.delta) {
                this.finalValue = finalValue;
            }
            else if (finalValue > this.finalValue) {
                this.finalValue += this.delta;
            }
            else {
                this.finalValue -= this.delta;
            }
        }
        
        private void calculate() {
            if (this.isAdminOverride) {
                this.finalValue = this.adminValue;
            }
        }
    }
}
