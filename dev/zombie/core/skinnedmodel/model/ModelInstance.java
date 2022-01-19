// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.iso.weather.ClimateManager;
import org.lwjgl.util.vector.ReadableVector3f;
import zombie.network.GameClient;
import zombie.iso.IsoWorld;
import zombie.iso.IsoUtils;
import zombie.popman.ObjectPool;
import zombie.iso.IsoGridSquare;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Matrix4f;
import zombie.util.StringUtils;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.ScriptManager;
import zombie.iso.IsoCamera;
import zombie.iso.Vector2;
import zombie.GameTime;
import org.joml.Math;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import java.util.Arrays;
import zombie.core.opengl.RenderThread;
import java.util.Objects;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.ColorInfo;
import zombie.scripting.objects.ModelScript;
import zombie.core.skinnedmodel.visual.ItemVisual;
import java.util.ArrayList;
import zombie.iso.IsoMovingObject;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.Texture;
import zombie.core.skinnedmodel.animation.AnimationPlayer;

public class ModelInstance
{
    public static float MODEL_LIGHT_MULT_OUTSIDE;
    public static float MODEL_LIGHT_MULT_ROOM;
    public Model model;
    public AnimationPlayer AnimPlayer;
    public SkinningData data;
    public Texture tex;
    public ModelInstanceTextureInitializer m_textureInitializer;
    public IsoGameCharacter character;
    public IsoMovingObject object;
    public float tintR;
    public float tintG;
    public float tintB;
    public ModelInstance parent;
    public int parentBone;
    public String parentBoneName;
    public float hue;
    public float depthBias;
    public ModelInstance matrixModel;
    public SoftwareModelMeshInstance softwareMesh;
    public final ArrayList<ModelInstance> sub;
    private int instanceSkip;
    private ItemVisual itemVisual;
    public boolean bResetAfterRender;
    private Object m_owner;
    public int renderRefCount;
    private static final int INITIAL_SKIP_VALUE = Integer.MAX_VALUE;
    private int skipped;
    public final Object m_lock;
    public ModelScript m_modelScript;
    public String attachmentNameSelf;
    public String attachmentNameParent;
    public float scale;
    public String maskVariableValue;
    public PlayerData[] playerData;
    private static final ColorInfo tempColorInfo;
    private static final ColorInfo tempColorInfo2;
    
    public ModelInstance() {
        this.tintR = 1.0f;
        this.tintG = 1.0f;
        this.tintB = 1.0f;
        this.parentBoneName = null;
        this.sub = new ArrayList<ModelInstance>();
        this.itemVisual = null;
        this.bResetAfterRender = false;
        this.m_owner = null;
        this.skipped = Integer.MAX_VALUE;
        this.m_lock = "ModelInstance Thread Lock";
        this.m_modelScript = null;
        this.attachmentNameSelf = null;
        this.attachmentNameParent = null;
        this.scale = 1.0f;
        this.maskVariableValue = null;
    }
    
    public ModelInstance init(final Model model, final IsoGameCharacter isoGameCharacter, AnimationPlayer alloc) {
        this.data = (SkinningData)model.Tag;
        this.model = model;
        this.tex = model.tex;
        if (!model.bStatic && alloc == null) {
            alloc = AnimationPlayer.alloc(model);
        }
        this.AnimPlayer = alloc;
        this.character = isoGameCharacter;
        this.object = isoGameCharacter;
        return this;
    }
    
    public boolean isRendering() {
        return this.renderRefCount > 0;
    }
    
    public void reset() {
        if (this.tex instanceof SmartTexture) {
            final Texture tex = this.tex;
            Objects.requireNonNull(tex);
            RenderThread.queueInvokeOnRenderContext(tex::destroy);
        }
        this.AnimPlayer = null;
        this.character = null;
        this.data = null;
        this.hue = 0.0f;
        this.itemVisual = null;
        this.matrixModel = null;
        this.model = null;
        this.object = null;
        this.parent = null;
        this.parentBone = 0;
        this.parentBoneName = null;
        this.skipped = Integer.MAX_VALUE;
        this.sub.clear();
        this.softwareMesh = null;
        this.tex = null;
        if (this.m_textureInitializer != null) {
            this.m_textureInitializer.release();
            this.m_textureInitializer = null;
        }
        this.tintR = 1.0f;
        this.tintG = 1.0f;
        this.tintB = 1.0f;
        this.bResetAfterRender = false;
        this.renderRefCount = 0;
        this.scale = 1.0f;
        this.m_owner = null;
        this.m_modelScript = null;
        this.attachmentNameSelf = null;
        this.attachmentNameParent = null;
        this.maskVariableValue = null;
        if (this.playerData != null) {
            PlayerData.pool.release(this.playerData);
            Arrays.fill(this.playerData, null);
        }
    }
    
    public void LoadTexture(final String s) {
        if (s == null || s.length() == 0) {
            this.tex = null;
            return;
        }
        this.tex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (this.tex == null) {
            if (s.equals("Vest_White")) {
                this.tex = Texture.getSharedTexture("media/textures/Shirt_White.png");
            }
            else if (s.contains("Hair")) {
                this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
            }
            else if (s.contains("Beard")) {
                this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
    }
    
    public void dismember(final int n) {
        this.AnimPlayer.dismember(n);
    }
    
    public void UpdateDir() {
        if (this.AnimPlayer == null) {
            return;
        }
        this.AnimPlayer.UpdateDir(this.character);
    }
    
    public void Update() {
        if (this.character != null) {
            final float distTo = this.character.DistTo(IsoPlayer.getInstance());
            if (!this.character.amputations.isEmpty() && distTo > 0.0f && this.AnimPlayer != null) {
                this.AnimPlayer.dismembered.clear();
                final ArrayList<String> amputations = this.character.amputations;
                for (int i = 0; i < amputations.size(); ++i) {
                    this.AnimPlayer.dismember(this.AnimPlayer.getSkinningData().BoneIndices.get(amputations.get(i)));
                }
            }
            if (Math.abs(this.character.speedMod - 0.5957f) < 1.0E-4f) {}
        }
        this.instanceSkip = 0;
        if (this.AnimPlayer != null) {
            if (this.matrixModel == null) {
                if (this.skipped >= this.instanceSkip) {
                    if (this.skipped == Integer.MAX_VALUE) {
                        this.skipped = 1;
                    }
                    this.AnimPlayer.Update(GameTime.instance.getTimeDelta() * this.skipped);
                }
                else {
                    this.AnimPlayer.DoAngles();
                }
                this.AnimPlayer.parentPlayer = null;
            }
            else {
                this.AnimPlayer.parentPlayer = this.matrixModel.AnimPlayer;
            }
        }
        if (this.skipped >= this.instanceSkip) {
            this.skipped = 0;
        }
        ++this.skipped;
    }
    
    public void SetForceDir(final Vector2 vector2) {
        if (this.AnimPlayer != null) {
            this.AnimPlayer.SetForceDir(vector2);
        }
    }
    
    public void setInstanceSkip(final int n) {
        this.instanceSkip = n;
        for (int i = 0; i < this.sub.size(); ++i) {
            this.sub.get(i).instanceSkip = n;
        }
    }
    
    public void destroySmartTextures() {
        if (this.tex instanceof SmartTexture) {
            this.tex.destroy();
            this.tex = null;
        }
        for (int i = 0; i < this.sub.size(); ++i) {
            this.sub.get(i).destroySmartTextures();
        }
    }
    
    public void updateLights() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (this.playerData == null) {
            this.playerData = new PlayerData[4];
        }
        final boolean b = this.playerData[playerIndex] == null;
        if (this.playerData[playerIndex] == null) {
            this.playerData[playerIndex] = PlayerData.pool.alloc();
        }
        this.playerData[playerIndex].updateLights(this.character, b);
    }
    
    public ItemVisual getItemVisual() {
        return this.itemVisual;
    }
    
    public void setItemVisual(final ItemVisual itemVisual) {
        this.itemVisual = itemVisual;
    }
    
    public void applyModelScriptScale(final String s) {
        this.m_modelScript = ScriptManager.instance.getModelScript(s);
        if (this.m_modelScript != null) {
            this.scale = this.m_modelScript.scale;
        }
    }
    
    public ModelAttachment getAttachment(final int n) {
        return (this.m_modelScript == null) ? null : this.m_modelScript.getAttachment(n);
    }
    
    public ModelAttachment getAttachmentById(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        return (this.m_modelScript == null) ? null : this.m_modelScript.getAttachmentById(s);
    }
    
    public Matrix4f getAttachmentMatrix(final ModelAttachment modelAttachment, final Matrix4f matrix4f) {
        matrix4f.translation((Vector3fc)modelAttachment.getOffset());
        final Vector3f rotate = modelAttachment.getRotate();
        matrix4f.rotateXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
        return matrix4f;
    }
    
    public Matrix4f getAttachmentMatrix(final int n, final Matrix4f matrix4f) {
        final ModelAttachment attachment = this.getAttachment(n);
        if (attachment == null) {
            return matrix4f.identity();
        }
        return this.getAttachmentMatrix(attachment, matrix4f);
    }
    
    public Matrix4f getAttachmentMatrixById(final String s, final Matrix4f matrix4f) {
        final ModelAttachment attachmentById = this.getAttachmentById(s);
        if (attachmentById == null) {
            return matrix4f.identity();
        }
        return this.getAttachmentMatrix(attachmentById, matrix4f);
    }
    
    public void setOwner(final Object o) {
        Objects.requireNonNull(o);
        assert this.m_owner == null;
        this.m_owner = o;
    }
    
    public void clearOwner(final Object obj) {
        Objects.requireNonNull(obj);
        assert this.m_owner == obj;
        this.m_owner = null;
    }
    
    public Object getOwner() {
        return this.m_owner;
    }
    
    public void setTextureInitializer(final ModelInstanceTextureInitializer textureInitializer) {
        this.m_textureInitializer = textureInitializer;
    }
    
    public ModelInstanceTextureInitializer getTextureInitializer() {
        return this.m_textureInitializer;
    }
    
    public boolean hasTextureCreator() {
        return this.m_textureInitializer != null && this.m_textureInitializer.isDirty();
    }
    
    static {
        ModelInstance.MODEL_LIGHT_MULT_OUTSIDE = 1.7f;
        ModelInstance.MODEL_LIGHT_MULT_ROOM = 1.7f;
        tempColorInfo = new ColorInfo();
        tempColorInfo2 = new ColorInfo();
    }
    
    public static final class EffectLight
    {
        public float x;
        public float y;
        public float z;
        public float r;
        public float g;
        public float b;
        public int radius;
        
        public void set(final float x, final float y, final float z, final float r, final float g, final float b, final int radius) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            this.g = g;
            this.b = b;
            this.radius = radius;
        }
    }
    
    public enum FrameLightBlendStatus
    {
        In, 
        During, 
        Out;
        
        private static /* synthetic */ FrameLightBlendStatus[] $values() {
            return new FrameLightBlendStatus[] { FrameLightBlendStatus.In, FrameLightBlendStatus.During, FrameLightBlendStatus.Out };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class FrameLightInfo
    {
        public FrameLightBlendStatus Stage;
        public int id;
        public int x;
        public int y;
        public int z;
        public float distSq;
        public int radius;
        public float r;
        public float g;
        public float b;
        public int flags;
        public final org.lwjgl.util.vector.Vector3f currentColor;
        public final org.lwjgl.util.vector.Vector3f targetColor;
        public boolean active;
        public boolean foundThisFrame;
        
        public FrameLightInfo() {
            this.currentColor = new org.lwjgl.util.vector.Vector3f();
            this.targetColor = new org.lwjgl.util.vector.Vector3f();
        }
    }
    
    public static final class PlayerData
    {
        FrameLightInfo[] frameLights;
        ArrayList<IsoGridSquare.ResultLight> chosenLights;
        Vector3f targetAmbient;
        Vector3f currentAmbient;
        EffectLight[] effectLightsMain;
        private static final ObjectPool<PlayerData> pool;
        
        private void registerFrameLight(final IsoGridSquare.ResultLight e) {
            this.chosenLights.add(e);
        }
        
        private void initFrameLightsForFrame() {
            if (this.frameLights == null) {
                this.effectLightsMain = new EffectLight[5];
                for (int i = 0; i < 5; ++i) {
                    this.effectLightsMain[i] = new EffectLight();
                }
                this.frameLights = new FrameLightInfo[5];
                this.chosenLights = new ArrayList<IsoGridSquare.ResultLight>();
                this.targetAmbient = new Vector3f();
                this.currentAmbient = new Vector3f();
            }
            final EffectLight[] effectLightsMain = this.effectLightsMain;
            for (int length = effectLightsMain.length, j = 0; j < length; ++j) {
                effectLightsMain[j].radius = -1;
            }
            this.chosenLights.clear();
        }
        
        private void completeFrameLightsForFrame() {
            for (int i = 0; i < 5; ++i) {
                if (this.frameLights[i] != null) {
                    this.frameLights[i].foundThisFrame = false;
                }
            }
            for (int j = 0; j < this.chosenLights.size(); ++j) {
                final IsoGridSquare.ResultLight resultLight = this.chosenLights.get(j);
                boolean b = false;
                int n = 0;
                for (int k = 0; k < 5; ++k) {
                    if (this.frameLights[k] != null && this.frameLights[k].active) {
                        if (resultLight.id != -1) {
                            if (resultLight.id != this.frameLights[k].id) {
                                continue;
                            }
                        }
                        else {
                            if (this.frameLights[k].x != resultLight.x || this.frameLights[k].y != resultLight.y) {
                                continue;
                            }
                            if (this.frameLights[k].z != resultLight.z) {
                                continue;
                            }
                        }
                        b = true;
                        n = k;
                        break;
                    }
                }
                if (b) {
                    this.frameLights[n].foundThisFrame = true;
                    this.frameLights[n].x = resultLight.x;
                    this.frameLights[n].y = resultLight.y;
                    this.frameLights[n].z = resultLight.z;
                    this.frameLights[n].flags = resultLight.flags;
                    this.frameLights[n].radius = resultLight.radius;
                    this.frameLights[n].targetColor.x = resultLight.r;
                    this.frameLights[n].targetColor.y = resultLight.g;
                    this.frameLights[n].targetColor.z = resultLight.b;
                    this.frameLights[n].Stage = FrameLightBlendStatus.In;
                }
                else {
                    for (int l = 0; l < 5; ++l) {
                        if (this.frameLights[l] == null || !this.frameLights[l].active) {
                            if (this.frameLights[l] == null) {
                                this.frameLights[l] = new FrameLightInfo();
                            }
                            this.frameLights[l].x = resultLight.x;
                            this.frameLights[l].y = resultLight.y;
                            this.frameLights[l].z = resultLight.z;
                            this.frameLights[l].r = resultLight.r;
                            this.frameLights[l].g = resultLight.g;
                            this.frameLights[l].b = resultLight.b;
                            this.frameLights[l].flags = resultLight.flags;
                            this.frameLights[l].radius = resultLight.radius;
                            this.frameLights[l].id = resultLight.id;
                            this.frameLights[l].currentColor.x = 0.0f;
                            this.frameLights[l].currentColor.y = 0.0f;
                            this.frameLights[l].currentColor.z = 0.0f;
                            this.frameLights[l].targetColor.x = resultLight.r;
                            this.frameLights[l].targetColor.y = resultLight.g;
                            this.frameLights[l].targetColor.z = resultLight.b;
                            this.frameLights[l].Stage = FrameLightBlendStatus.In;
                            this.frameLights[l].active = true;
                            this.frameLights[l].foundThisFrame = true;
                            break;
                        }
                    }
                }
            }
            final float multiplier = GameTime.getInstance().getMultiplier();
            for (int n2 = 0; n2 < 5; ++n2) {
                final FrameLightInfo frameLightInfo = this.frameLights[n2];
                if (frameLightInfo != null && frameLightInfo.active) {
                    if (!frameLightInfo.foundThisFrame) {
                        frameLightInfo.targetColor.x = 0.0f;
                        frameLightInfo.targetColor.y = 0.0f;
                        frameLightInfo.targetColor.z = 0.0f;
                        frameLightInfo.Stage = FrameLightBlendStatus.Out;
                    }
                    frameLightInfo.currentColor.x = this.step(frameLightInfo.currentColor.x, frameLightInfo.targetColor.x, java.lang.Math.signum(frameLightInfo.targetColor.x - frameLightInfo.currentColor.x) / (60.0f * multiplier));
                    frameLightInfo.currentColor.y = this.step(frameLightInfo.currentColor.y, frameLightInfo.targetColor.y, java.lang.Math.signum(frameLightInfo.targetColor.y - frameLightInfo.currentColor.y) / (60.0f * multiplier));
                    frameLightInfo.currentColor.z = this.step(frameLightInfo.currentColor.z, frameLightInfo.targetColor.z, java.lang.Math.signum(frameLightInfo.targetColor.z - frameLightInfo.currentColor.z) / (60.0f * multiplier));
                    if (frameLightInfo.Stage == FrameLightBlendStatus.Out && frameLightInfo.currentColor.x < 0.01f && frameLightInfo.currentColor.y < 0.01f && frameLightInfo.currentColor.z < 0.01f) {
                        frameLightInfo.active = false;
                    }
                }
            }
        }
        
        private void sortLights(final IsoGameCharacter isoGameCharacter) {
            for (int i = 0; i < this.frameLights.length; ++i) {
                final FrameLightInfo frameLightInfo3 = this.frameLights[i];
                if (frameLightInfo3 != null) {
                    if (!frameLightInfo3.active) {
                        frameLightInfo3.distSq = Float.MAX_VALUE;
                    }
                    else {
                        frameLightInfo3.distSq = IsoUtils.DistanceToSquared(isoGameCharacter.x, isoGameCharacter.y, isoGameCharacter.z, frameLightInfo3.x + 0.5f, frameLightInfo3.y + 0.5f, (float)frameLightInfo3.z);
                    }
                }
            }
            final boolean b;
            final boolean b2;
            Arrays.sort(this.frameLights, (frameLightInfo, frameLightInfo2) -> {
                b = (frameLightInfo == null || frameLightInfo.radius == -1 || !frameLightInfo.active);
                b2 = (frameLightInfo2 == null || frameLightInfo2.radius == -1 || !frameLightInfo2.active);
                if (b && b2) {
                    return 0;
                }
                else if (b) {
                    return 1;
                }
                else if (b2) {
                    return -1;
                }
                else if (frameLightInfo.Stage.ordinal() < frameLightInfo2.Stage.ordinal()) {
                    return -1;
                }
                else if (frameLightInfo.Stage.ordinal() > frameLightInfo2.Stage.ordinal()) {
                    return 1;
                }
                else {
                    return (int)java.lang.Math.signum(frameLightInfo.distSq - frameLightInfo2.distSq);
                }
            });
        }
        
        private void updateLights(final IsoGameCharacter isoGameCharacter, final boolean b) {
            this.initFrameLightsForFrame();
            if (isoGameCharacter == null) {
                return;
            }
            if (isoGameCharacter.getCurrentSquare() == null) {
                return;
            }
            final IsoGridSquare.ILighting lighting = isoGameCharacter.getCurrentSquare().lighting[IsoCamera.frameState.playerIndex];
            final int min = Math.min(lighting.resultLightCount(), 4);
            for (int i = 0; i < min; ++i) {
                this.registerFrameLight(lighting.getResultLight(i));
            }
            if (b) {
                for (int j = 0; j < this.frameLights.length; ++j) {
                    if (this.frameLights[j] != null) {
                        this.frameLights[j].active = false;
                    }
                }
            }
            this.completeFrameLightsForFrame();
            isoGameCharacter.getCurrentSquare().interpolateLight(ModelInstance.tempColorInfo, isoGameCharacter.x % 1.0f, isoGameCharacter.y % 1.0f);
            this.targetAmbient.x = ModelInstance.tempColorInfo.r;
            this.targetAmbient.y = ModelInstance.tempColorInfo.g;
            this.targetAmbient.z = ModelInstance.tempColorInfo.b;
            if (isoGameCharacter.z - (int)isoGameCharacter.z > 0.2f) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)isoGameCharacter.x, (int)isoGameCharacter.y, (int)isoGameCharacter.z + 1);
                if (gridSquare != null) {
                    final ColorInfo tempColorInfo2 = ModelInstance.tempColorInfo2;
                    gridSquare.lighting[IsoCamera.frameState.playerIndex].lightInfo();
                    gridSquare.interpolateLight(tempColorInfo2, isoGameCharacter.x % 1.0f, isoGameCharacter.y % 1.0f);
                    ModelInstance.tempColorInfo.interp(tempColorInfo2, (isoGameCharacter.z - ((int)isoGameCharacter.z + 0.2f)) / 0.8f, ModelInstance.tempColorInfo);
                    this.targetAmbient.set(ModelInstance.tempColorInfo.r, ModelInstance.tempColorInfo.g, ModelInstance.tempColorInfo.b);
                }
            }
            final float multiplier = GameTime.getInstance().getMultiplier();
            this.currentAmbient.x = this.step(this.currentAmbient.x, this.targetAmbient.x, (this.targetAmbient.x - this.currentAmbient.x) / (10.0f * multiplier));
            this.currentAmbient.y = this.step(this.currentAmbient.y, this.targetAmbient.y, (this.targetAmbient.y - this.currentAmbient.y) / (10.0f * multiplier));
            this.currentAmbient.z = this.step(this.currentAmbient.z, this.targetAmbient.z, (this.targetAmbient.z - this.currentAmbient.z) / (10.0f * multiplier));
            if (b) {
                this.setCurrentToTarget();
            }
            this.sortLights(isoGameCharacter);
            final float n = 0.7f;
            for (int k = 0; k < 5; ++k) {
                final FrameLightInfo frameLightInfo = this.frameLights[k];
                if (frameLightInfo != null) {
                    if (frameLightInfo.active) {
                        final EffectLight effectLight = this.effectLightsMain[k];
                        if ((frameLightInfo.flags & 0x1) != 0x0) {
                            effectLight.set(isoGameCharacter.x, isoGameCharacter.y, (float)((int)isoGameCharacter.z + 1), frameLightInfo.currentColor.x * n, frameLightInfo.currentColor.y * n, frameLightInfo.currentColor.z * n, frameLightInfo.radius);
                        }
                        else if ((frameLightInfo.flags & 0x2) != 0x0) {
                            if (isoGameCharacter instanceof IsoPlayer) {
                                final int n2 = GameClient.bClient ? (((IsoPlayer)isoGameCharacter).OnlineID + 1) : (((IsoPlayer)isoGameCharacter).PlayerIndex + 1);
                                final int playerIndex = ((IsoPlayer)isoGameCharacter).PlayerIndex;
                                final int n3 = playerIndex * 4 + 1;
                                final int n4 = playerIndex * 4 + 3 + 1;
                                if (frameLightInfo.id < n3 || frameLightInfo.id > n4) {
                                    effectLight.set((float)frameLightInfo.x, (float)frameLightInfo.y, (float)frameLightInfo.z, frameLightInfo.currentColor.x, frameLightInfo.currentColor.y, frameLightInfo.currentColor.z, frameLightInfo.radius);
                                }
                            }
                            else {
                                effectLight.set((float)frameLightInfo.x, (float)frameLightInfo.y, (float)frameLightInfo.z, frameLightInfo.currentColor.x * 2.0f, frameLightInfo.currentColor.y, frameLightInfo.currentColor.z, frameLightInfo.radius);
                            }
                        }
                        else {
                            effectLight.set(frameLightInfo.x + 0.5f, frameLightInfo.y + 0.5f, frameLightInfo.z + 0.5f, frameLightInfo.currentColor.x * n, frameLightInfo.currentColor.y * n, frameLightInfo.currentColor.z * n, frameLightInfo.radius);
                        }
                    }
                }
            }
            if (min <= 3 && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.getTorchStrength() > 0.0f) {
                this.effectLightsMain[2].set(isoGameCharacter.x + isoGameCharacter.getForwardDirection().x * 0.5f, isoGameCharacter.y + isoGameCharacter.getForwardDirection().y * 0.5f, isoGameCharacter.z + 0.25f, 1.0f, 1.0f, 1.0f, 2);
            }
            final float n5 = 0.0f;
            final float n6 = 1.0f;
            final float lerp = this.lerp(n5, n6, this.currentAmbient.x);
            final float lerp2 = this.lerp(n5, n6, this.currentAmbient.y);
            final float lerp3 = this.lerp(n5, n6, this.currentAmbient.z);
            if (isoGameCharacter.getCurrentSquare().isOutside()) {
                final float n7 = lerp * ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                final float n8 = lerp2 * ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                final float n9 = lerp3 * ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                this.effectLightsMain[3].set(isoGameCharacter.x - 2.0f, isoGameCharacter.y - 2.0f, isoGameCharacter.z + 1.0f, n7 / 4.0f, n8 / 4.0f, n9 / 4.0f, 5000);
                this.effectLightsMain[4].set(isoGameCharacter.x + 2.0f, isoGameCharacter.y + 2.0f, isoGameCharacter.z + 1.0f, n7 / 4.0f, n8 / 4.0f, n9 / 4.0f, 5000);
            }
            else if (isoGameCharacter.getCurrentSquare().getRoom() != null) {
                final float n10 = lerp * ModelInstance.MODEL_LIGHT_MULT_ROOM;
                final float n11 = lerp2 * ModelInstance.MODEL_LIGHT_MULT_ROOM;
                final float n12 = lerp3 * ModelInstance.MODEL_LIGHT_MULT_ROOM;
                this.effectLightsMain[3].set(isoGameCharacter.x - 2.0f, isoGameCharacter.y - 2.0f, isoGameCharacter.z + 1.0f, n10 / 4.0f, n11 / 4.0f, n12 / 4.0f, 5000);
                this.effectLightsMain[4].set(isoGameCharacter.x + 2.0f, isoGameCharacter.y + 2.0f, isoGameCharacter.z + 1.0f, n10 / 4.0f, n11 / 4.0f, n12 / 4.0f, 5000);
            }
        }
        
        private float lerp(final float n, final float n2, final float n3) {
            return n + (n2 - n) * n3;
        }
        
        private void setCurrentToTarget() {
            for (int i = 0; i < this.frameLights.length; ++i) {
                final FrameLightInfo frameLightInfo = this.frameLights[i];
                if (frameLightInfo != null) {
                    frameLightInfo.currentColor.set((ReadableVector3f)frameLightInfo.targetColor);
                }
            }
            this.currentAmbient.set((Vector3fc)this.targetAmbient);
        }
        
        private float step(final float n, final float n2, final float n3) {
            if (n < n2) {
                return ClimateManager.clamp(0.0f, n2, n + n3);
            }
            if (n > n2) {
                return ClimateManager.clamp(n2, 1.0f, n + n3);
            }
            return n;
        }
        
        static {
            pool = new ObjectPool<PlayerData>(PlayerData::new);
        }
    }
}
