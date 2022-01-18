// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.util.Pool;
import zombie.core.textures.Texture;
import zombie.scripting.objects.VehicleScript;
import zombie.iso.Vector3;
import zombie.core.opengl.PZGLUtil;
import org.lwjgl.opengl.GL11;
import zombie.characters.IsoPlayer;
import zombie.core.SpriteRenderer;
import zombie.GameProfiler;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.iso.IsoLightSource;
import java.util.ArrayList;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import org.joml.Matrix4fc;
import org.joml.Math;
import zombie.core.opengl.RenderSettings;
import org.joml.Vector3f;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoCamera;
import zombie.popman.ObjectPool;
import zombie.core.textures.ColorInfo;
import zombie.core.skinnedmodel.shader.Shader;
import org.joml.Matrix4f;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.ModelManager;
import zombie.iso.IsoMovingObject;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.TextureDraw;

public final class ModelSlotRenderData extends TextureDraw.GenericDrawer
{
    public IsoGameCharacter character;
    public IsoMovingObject object;
    private ModelManager.ModelSlot modelSlot;
    public final ModelInstanceRenderDataList modelData;
    private final ModelInstanceRenderDataList readyModelData;
    public ModelInstanceTextureCreator textureCreator;
    public AnimationPlayer animPlayer;
    public float animPlayerAngle;
    public float x;
    public float y;
    public float z;
    public float ambientR;
    public float ambientG;
    public float ambientB;
    public boolean bOutside;
    public final Matrix4f vehicleTransform;
    public boolean bInVehicle;
    public float inVehicleX;
    public float inVehicleY;
    public float inVehicleZ;
    public float vehicleAngleX;
    public float vehicleAngleY;
    public float vehicleAngleZ;
    public float alpha;
    private boolean bRendered;
    private boolean bReady;
    public final ModelInstance.EffectLight[] effectLights;
    public float centerOfMassY;
    public boolean RENDER_TO_TEXTURE;
    private static Shader solidColor;
    private static Shader solidColorStatic;
    private boolean bCharacterOutline;
    private final ColorInfo outlineColor;
    private ModelSlotDebugRenderData m_debugRenderData;
    private static final ObjectPool<ModelSlotRenderData> pool;
    
    public ModelSlotRenderData() {
        this.modelData = new ModelInstanceRenderDataList();
        this.readyModelData = new ModelInstanceRenderDataList();
        this.vehicleTransform = new Matrix4f();
        this.effectLights = new ModelInstance.EffectLight[5];
        this.bCharacterOutline = false;
        this.outlineColor = new ColorInfo(1.0f, 0.0f, 0.0f, 1.0f);
        for (int i = 0; i < this.effectLights.length; ++i) {
            this.effectLights[i] = new ModelInstance.EffectLight();
        }
    }
    
    public ModelSlotRenderData init(final ModelManager.ModelSlot modelSlot) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        this.modelSlot = modelSlot;
        this.object = modelSlot.model.object;
        this.x = this.object.x;
        this.y = this.object.y;
        this.z = this.object.z;
        this.character = modelSlot.character;
        final BaseVehicle baseVehicle = zombie.util.Type.tryCastTo(this.object, BaseVehicle.class);
        if (baseVehicle != null) {
            this.textureCreator = null;
            this.animPlayer = baseVehicle.getAnimationPlayer();
            this.animPlayerAngle = Float.NaN;
            this.centerOfMassY = baseVehicle.jniTransform.origin.y - BaseVehicle.CENTER_OF_MASS_MAGIC;
            if (BaseVehicle.RENDER_TO_TEXTURE) {
                this.centerOfMassY = 0.0f - BaseVehicle.CENTER_OF_MASS_MAGIC;
            }
            this.alpha = this.object.getAlpha(playerIndex);
            final IsoLightSource[] lights = ((VehicleModelInstance)modelSlot.model).getLights();
            for (int i = 0; i < this.effectLights.length; ++i) {
                this.effectLights[i].set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0);
            }
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            for (int j = 0; j < lights.length; ++j) {
                final IsoLightSource isoLightSource = lights[j];
                if (isoLightSource != null) {
                    final Vector3f localPos = baseVehicle.getLocalPos(isoLightSource.x + 0.5f, isoLightSource.y + 0.5f, isoLightSource.z + 0.75f, vector3f);
                    baseVehicle.fixLightbarModelLighting(isoLightSource, vector3f);
                    this.effectLights[j].set(localPos.x, localPos.y, localPos.z, isoLightSource.r, isoLightSource.g, isoLightSource.b, isoLightSource.radius);
                }
            }
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            final float ambientR = (1.0f - Math.min(RenderSettings.getInstance().getPlayerSettings(playerIndex).getDarkness() * 0.6f, 0.8f)) * 0.9f;
            this.ambientB = ambientR;
            this.ambientG = ambientR;
            this.ambientR = ambientR;
            this.vehicleTransform.set((Matrix4fc)baseVehicle.vehicleTransform);
        }
        else {
            this.textureCreator = this.character.getTextureCreator();
            if (this.textureCreator != null && this.textureCreator.isRendered()) {
                this.textureCreator = null;
            }
            final ModelInstance.PlayerData playerData = modelSlot.model.playerData[playerIndex];
            this.animPlayer = this.character.getAnimationPlayer();
            this.animPlayerAngle = this.animPlayer.getRenderedAngle();
            for (int k = 0; k < this.effectLights.length; ++k) {
                final ModelInstance.EffectLight effectLight = playerData.effectLightsMain[k];
                this.effectLights[k].set(effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, effectLight.radius);
            }
            this.ambientR = playerData.currentAmbient.x;
            this.ambientG = playerData.currentAmbient.y;
            this.ambientB = playerData.currentAmbient.z;
            this.bOutside = (this.character.getCurrentSquare() != null && this.character.getCurrentSquare().isOutside());
            this.alpha = this.character.getAlpha(playerIndex);
            if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                final float ambientR2 = 1.0f;
                this.ambientB = ambientR2;
                this.ambientG = ambientR2;
                this.ambientR = ambientR2;
            }
            if (GameServer.bServer && ServerGUI.isCreated()) {
                final float ambientR3 = 1.0f;
                this.ambientB = ambientR3;
                this.ambientG = ambientR3;
                this.ambientR = ambientR3;
            }
            this.bCharacterOutline = this.character.bOutline[playerIndex];
            if (this.bCharacterOutline) {
                this.outlineColor.set(this.character.outlineColor[playerIndex]);
            }
            this.bInVehicle = this.character.isSeatedInVehicle();
            if (this.bInVehicle) {
                this.animPlayerAngle = 0.0f;
                final BaseVehicle vehicle = this.character.getVehicle();
                this.centerOfMassY = vehicle.jniTransform.origin.y - BaseVehicle.CENTER_OF_MASS_MAGIC;
                this.x = vehicle.x;
                this.y = vehicle.y;
                this.z = vehicle.z;
                final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
                vehicle.getPassengerLocalPos(vehicle.getSeat(this.character), vector3f2);
                this.inVehicleX = vector3f2.x;
                this.inVehicleY = vector3f2.y;
                this.inVehicleZ = vector3f2.z;
                BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
                final Vector3f eulerAnglesZYX = vehicle.vehicleTransform.getEulerAnglesZYX((Vector3f)BaseVehicle.TL_vector3f_pool.get().alloc());
                this.vehicleAngleZ = (float)java.lang.Math.toDegrees(eulerAnglesZYX.z);
                this.vehicleAngleY = (float)java.lang.Math.toDegrees(eulerAnglesZYX.y);
                this.vehicleAngleX = (float)java.lang.Math.toDegrees(eulerAnglesZYX.x);
                BaseVehicle.TL_vector3f_pool.get().release(eulerAnglesZYX);
            }
        }
        this.RENDER_TO_TEXTURE = BaseVehicle.RENDER_TO_TEXTURE;
        this.modelData.clear();
        ModelInstanceRenderData init = null;
        if (modelSlot.model.model.isReady() && (modelSlot.model.AnimPlayer == null || modelSlot.model.AnimPlayer.isReady())) {
            init = ModelInstanceRenderData.alloc().init(modelSlot.model);
            this.modelData.add(init);
            if (modelSlot.sub.size() != modelSlot.model.sub.size()) {}
        }
        this.initRenderData(modelSlot.model.sub, init);
        boolean b = false;
        for (int l = 0; l < this.modelData.size(); ++l) {
            final ModelInstanceRenderData modelInstanceRenderData = this.modelData.get(l);
            if (this.character != null && modelInstanceRenderData.modelInstance == this.character.primaryHandModel && this.character.isMuzzleFlash()) {
                modelInstanceRenderData.m_muzzleFlash = true;
            }
            if (modelInstanceRenderData.modelInstance != null && modelInstanceRenderData.modelInstance.hasTextureCreator()) {
                b = true;
            }
        }
        if (this.textureCreator != null) {
            final ModelInstanceTextureCreator textureCreator = this.textureCreator;
            ++textureCreator.renderRefCount;
        }
        if (this.character != null && (this.textureCreator != null || b)) {
            assert this.readyModelData.isEmpty();
            ModelInstanceRenderData.release(this.readyModelData);
            this.readyModelData.clear();
            for (int index = 0; index < this.character.getReadyModelData().size(); ++index) {
                final ModelInstance modelInstance = this.character.getReadyModelData().get(index);
                final ModelInstanceRenderData init2 = ModelInstanceRenderData.alloc().init(modelInstance);
                init2.transformToParent(this.getParentData(modelInstance));
                this.readyModelData.add(init2);
            }
        }
        if (Core.bDebug) {
            this.m_debugRenderData = ModelSlotDebugRenderData.alloc().init(this);
        }
        this.bRendered = false;
        return this;
    }
    
    private ModelInstanceRenderData getParentData(final ModelInstance modelInstance) {
        for (int i = 0; i < this.readyModelData.size(); ++i) {
            final ModelInstanceRenderData modelInstanceRenderData = this.readyModelData.get(i);
            if (modelInstanceRenderData.modelInstance == modelInstance.parent) {
                return modelInstanceRenderData;
            }
        }
        return null;
    }
    
    private ModelInstanceRenderData initRenderData(final ModelInstance modelInstance, final ModelInstanceRenderData modelInstanceRenderData) {
        final ModelInstanceRenderData init = ModelInstanceRenderData.alloc().init(modelInstance);
        init.transformToParent(modelInstanceRenderData);
        this.modelData.add(init);
        this.initRenderData(modelInstance.sub, init);
        return init;
    }
    
    private void initRenderData(final ArrayList<ModelInstance> list, final ModelInstanceRenderData modelInstanceRenderData) {
        for (int i = 0; i < list.size(); ++i) {
            final ModelInstance modelInstance = list.get(i);
            if (modelInstance.model.isReady() && (modelInstance.AnimPlayer == null || modelInstance.AnimPlayer.isReady())) {
                this.initRenderData(modelInstance, modelInstanceRenderData);
            }
        }
    }
    
    @Override
    public void render() {
        if (this.character == null) {
            this.renderVehicle();
        }
        else {
            this.renderCharacter();
        }
    }
    
    public void renderDebug() {
        if (this.m_debugRenderData != null) {
            this.m_debugRenderData.render();
        }
    }
    
    private void renderCharacter() {
        this.bReady = true;
        if (this.textureCreator != null && !this.textureCreator.isRendered()) {
            this.textureCreator.render();
            if (!this.textureCreator.isRendered()) {
                this.bReady = false;
            }
        }
        for (int i = 0; i < this.modelData.size(); ++i) {
            final ModelInstanceTextureInitializer textureInitializer = this.modelData.get(i).modelInstance.getTextureInitializer();
            if (textureInitializer != null && !textureInitializer.isRendered()) {
                textureInitializer.render();
                if (!textureInitializer.isRendered()) {
                    this.bReady = false;
                }
            }
        }
        if (!this.bReady && this.readyModelData.isEmpty()) {
            return;
        }
        if (this.bCharacterOutline) {
            ModelCamera.instance.bDepthMask = false;
            GameProfiler.getInstance().invokeAndMeasure("performRenderCharacterOutline", this, ModelSlotRenderData::performRenderCharacterOutline);
        }
        ModelCamera.instance.bDepthMask = true;
        GameProfiler.getInstance().invokeAndMeasure("renderCharacter", this, ModelSlotRenderData::performRenderCharacter);
        final int renderingPlayerIndex = SpriteRenderer.instance.getRenderingPlayerIndex();
        final IsoPlayer isoPlayer = zombie.util.Type.tryCastTo(this.character, IsoPlayer.class);
        if (isoPlayer != null && !this.bCharacterOutline && isoPlayer == IsoPlayer.players[renderingPlayerIndex]) {
            ModelOutlines.instance.setPlayerRenderData(this);
        }
        this.bRendered = this.bReady;
    }
    
    private void renderVehicleDebug() {
        if (!Core.bDebug) {
            return;
        }
        final Vector3 tempo = Model.tempo;
        ModelCamera.instance.Begin();
        GL11.glMatrixMode(5888);
        GL11.glTranslatef(0.0f, this.centerOfMassY, 0.0f);
        if (this.m_debugRenderData != null && !this.modelData.isEmpty()) {
            PZGLUtil.pushAndMultMatrix(5888, this.modelData.get(0).xfrm);
            this.m_debugRenderData.render();
            PZGLUtil.popMatrix(5888);
        }
        if (DebugOptions.instance.ModelRenderAttachments.getValue()) {
            final BaseVehicle baseVehicle = (BaseVehicle)this.object;
            final ModelInstanceRenderData modelInstanceRenderData = this.modelData.get(0);
            PZGLUtil.pushAndMultMatrix(5888, this.vehicleTransform);
            final float modelScale = baseVehicle.getScript().getModelScale();
            final float scale = modelInstanceRenderData.modelInstance.scale;
            final Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
            matrix4f.scaling(1.0f / modelScale);
            final Matrix4f matrix4f2 = BaseVehicle.TL_matrix4f_pool.get().alloc();
            for (int i = 0; i < baseVehicle.getScript().getAttachmentCount(); ++i) {
                modelInstanceRenderData.modelInstance.getAttachmentMatrix(baseVehicle.getScript().getAttachment(i), matrix4f2);
                matrix4f.mul((Matrix4fc)matrix4f2, matrix4f2);
                PZGLUtil.pushAndMultMatrix(5888, matrix4f2);
                Model.debugDrawAxis(0.0f, 0.0f, 0.0f, 1.0f, 2.0f);
                PZGLUtil.popMatrix(5888);
            }
            BaseVehicle.TL_matrix4f_pool.get().release(matrix4f2);
            BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
            PZGLUtil.popMatrix(5888);
        }
        if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue() && !this.modelData.isEmpty()) {
            final BaseVehicle baseVehicle2 = (BaseVehicle)this.object;
            GL11.glMatrixMode(5888);
            final Vector3f eulerAnglesZYX = this.vehicleTransform.getEulerAnglesZYX((Vector3f)BaseVehicle.TL_vector3f_pool.get().alloc());
            GL11.glRotatef((float)java.lang.Math.toDegrees(eulerAnglesZYX.z), 0.0f, 0.0f, 1.0f);
            GL11.glRotatef((float)java.lang.Math.toDegrees(eulerAnglesZYX.y), 0.0f, 1.0f, 0.0f);
            GL11.glRotatef((float)java.lang.Math.toDegrees(eulerAnglesZYX.x), 1.0f, 0.0f, 0.0f);
            BaseVehicle.TL_vector3f_pool.get().release(eulerAnglesZYX);
            Model.debugDrawAxis(0.0f, 0.0f, 0.0f, 1.0f, 4.0f);
            for (int j = 1; j < this.modelData.size(); ++j) {
                final VehicleSubModelInstance vehicleSubModelInstance = zombie.util.Type.tryCastTo(this.modelData.get(j).modelInstance, VehicleSubModelInstance.class);
                if (vehicleSubModelInstance != null && vehicleSubModelInstance.modelInfo.wheelIndex >= 0) {
                    final float n = 1.0f;
                    final VehicleScript.Wheel wheel = baseVehicle2.getScript().getWheel(vehicleSubModelInstance.modelInfo.wheelIndex);
                    tempo.set(wheel.offset.x * -1, baseVehicle2.getScript().getModel().offset.y + wheel.offset.y + baseVehicle2.getScript().getSuspensionRestLength(), wheel.offset.z);
                    Model.debugDrawAxis(tempo.x / n, tempo.y / n, tempo.z / n, baseVehicle2.getScript().getSuspensionRestLength() / n, 2.0f);
                }
            }
        }
        ModelCamera.instance.End();
    }
    
    private void performRenderCharacter() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glEnable(2929);
        GL11.glDisable(3089);
        ModelInstanceRenderDataList list = this.modelData;
        if (this.character != null && !this.bReady) {
            list = this.readyModelData;
        }
        Model.CharacterModelCameraBegin(this);
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).RenderCharacter(this);
        }
        if (Core.bDebug) {
            this.renderDebug();
            for (int j = 0; j < list.size(); ++j) {
                list.get(j).renderDebug();
            }
        }
        Model.CharacterModelCameraEnd();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glEnable(3553);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
    }
    
    protected void performRenderCharacterOutline() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glEnable(2929);
        GL11.glDisable(3089);
        ModelInstanceRenderDataList list = this.modelData;
        if (this.character != null && !this.bReady) {
            list = this.readyModelData;
        }
        if (ModelSlotRenderData.solidColor == null) {
            ModelSlotRenderData.solidColor = new Shader("aim_outline_solid", false);
            ModelSlotRenderData.solidColorStatic = new Shader("aim_outline_solid", true);
        }
        ModelSlotRenderData.solidColor.Start();
        ModelSlotRenderData.solidColor.getShaderProgram().setVector4("u_color", this.outlineColor.r, this.outlineColor.g, this.outlineColor.b, this.outlineColor.a);
        ModelSlotRenderData.solidColor.End();
        ModelSlotRenderData.solidColorStatic.Start();
        ModelSlotRenderData.solidColorStatic.getShaderProgram().setVector4("u_color", this.outlineColor.r, this.outlineColor.g, this.outlineColor.b, this.outlineColor.a);
        ModelSlotRenderData.solidColorStatic.End();
        ModelOutlines.instance.m_fboA.startDrawing(ModelOutlines.instance.beginRenderOutline(this.outlineColor), true);
        Model.CharacterModelCameraBegin(this);
        for (int i = 0; i < list.size(); ++i) {
            final ModelInstanceRenderData modelInstanceRenderData = list.get(i);
            final Shader effect = modelInstanceRenderData.model.Effect;
            try {
                modelInstanceRenderData.model.Effect = (modelInstanceRenderData.model.bStatic ? ModelSlotRenderData.solidColorStatic : ModelSlotRenderData.solidColor);
                modelInstanceRenderData.RenderCharacter(this);
            }
            finally {
                modelInstanceRenderData.model.Effect = effect;
            }
        }
        Model.CharacterModelCameraEnd();
        ModelOutlines.instance.m_fboA.endDrawing();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glEnable(3553);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
    }
    
    private void renderVehicle() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        if (this.RENDER_TO_TEXTURE) {
            GL11.glClear(256);
        }
        GL11.glEnable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glDisable(3089);
        if (this.RENDER_TO_TEXTURE) {
            ModelManager.instance.bitmap.startDrawing(true, true);
            GL11.glViewport(0, 0, ModelManager.instance.bitmap.getWidth(), ModelManager.instance.bitmap.getHeight());
        }
        for (int i = 0; i < this.modelData.size(); ++i) {
            this.modelData.get(i).RenderVehicle(this);
        }
        this.renderVehicleDebug();
        if (this.RENDER_TO_TEXTURE) {
            ModelManager.instance.bitmap.endDrawing();
        }
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glEnable(3553);
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
    }
    
    private void doneWithTextureCreator(final ModelInstanceTextureCreator modelInstanceTextureCreator) {
        if (modelInstanceTextureCreator == null) {
            return;
        }
        if (modelInstanceTextureCreator.testNotReady > 0) {
            --modelInstanceTextureCreator.testNotReady;
        }
        if (modelInstanceTextureCreator.renderRefCount > 0) {
            return;
        }
        if (modelInstanceTextureCreator.isRendered()) {
            modelInstanceTextureCreator.postRender();
            if (modelInstanceTextureCreator == this.character.getTextureCreator()) {
                this.character.setTextureCreator(null);
            }
        }
        else if (modelInstanceTextureCreator != this.character.getTextureCreator()) {
            modelInstanceTextureCreator.postRender();
        }
    }
    
    @Override
    public void postRender() {
        assert this.modelSlot.renderRefCount > 0;
        final ModelManager.ModelSlot modelSlot = this.modelSlot;
        --modelSlot.renderRefCount;
        if (this.textureCreator != null) {
            final ModelInstanceTextureCreator textureCreator = this.textureCreator;
            --textureCreator.renderRefCount;
            this.doneWithTextureCreator(this.textureCreator);
            this.textureCreator = null;
        }
        ModelInstanceRenderData.release(this.readyModelData);
        this.readyModelData.clear();
        if (this.bRendered) {
            ModelManager.instance.derefModelInstances(this.character.getReadyModelData());
            this.character.getReadyModelData().clear();
            for (int i = 0; i < this.modelData.size(); ++i) {
                final ModelInstance modelInstance;
                final ModelInstance e = modelInstance = this.modelData.get(i).modelInstance;
                ++modelInstance.renderRefCount;
                this.character.getReadyModelData().add(e);
            }
        }
        this.character = null;
        this.object = null;
        this.animPlayer = null;
        this.m_debugRenderData = Pool.tryRelease(this.m_debugRenderData);
        ModelInstanceRenderData.release(this.modelData);
        ModelSlotRenderData.pool.release(this);
    }
    
    public static ModelSlotRenderData alloc() {
        return ModelSlotRenderData.pool.alloc();
    }
    
    static {
        pool = new ObjectPool<ModelSlotRenderData>(ModelSlotRenderData::new);
    }
}
