// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import org.lwjglx.BufferUtils;
import zombie.util.Lambda;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.ModelManager;
import zombie.iso.sprite.SkyBox;
import zombie.util.Type;
import zombie.core.opengl.PZGLUtil;
import zombie.iso.IsoLightSource;
import org.lwjgl.opengl.GL13;
import zombie.scripting.objects.ModelAttachment;
import zombie.core.particle.MuzzleFlash;
import org.joml.Matrix4fc;
import zombie.vehicles.BaseVehicle;
import zombie.GameProfiler;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.input.GameKeyboard;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugOptions;
import org.lwjgl.opengl.GL11;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.util.StringUtils;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.iso.Vector3;
import zombie.core.Color;
import org.joml.Matrix4f;
import java.nio.FloatBuffer;
import zombie.core.textures.Texture;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.asset.Asset;

public final class Model extends Asset
{
    public String Name;
    public final ModelMesh Mesh;
    public Shader Effect;
    public Object Tag;
    public boolean bStatic;
    public Texture tex;
    public SoftwareModelMesh softwareMesh;
    public static final FloatBuffer m_staticReusableFloatBuffer;
    private static final Matrix4f IDENTITY;
    public static final Color[] debugDrawColours;
    public ModelAssetParams assetParams;
    static Vector3 tempo;
    public static final AssetType ASSET_TYPE;
    
    public Model(final AssetPath assetPath, final AssetManager assetManager, final ModelAssetParams assetParams) {
        super(assetPath, assetManager);
        this.bStatic = false;
        this.tex = null;
        this.assetParams = assetParams;
        this.bStatic = (this.assetParams != null && this.assetParams.bStatic);
        final ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
        meshAssetParams.bStatic = this.bStatic;
        meshAssetParams.animationsMesh = ((this.assetParams == null) ? null : this.assetParams.animationsModel);
        this.Mesh = (ModelMesh)MeshAssetManager.instance.load(new AssetPath(assetParams.meshName), meshAssetParams);
        if (!StringUtils.isNullOrWhitespace(assetParams.textureName)) {
            if (assetParams.textureName.contains("media/")) {
                this.tex = Texture.getSharedTexture(assetParams.textureName, assetParams.textureFlags);
            }
            else {
                this.tex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, assetParams.textureName), assetParams.textureFlags);
            }
        }
        if (!StringUtils.isNullOrWhitespace(assetParams.shaderName)) {
            this.CreateShader(assetParams.shaderName);
        }
        this.onCreated(this.Mesh.getState());
        this.addDependency(this.Mesh);
        if (this.isReady()) {
            this.Tag = this.Mesh.skinningData;
            this.softwareMesh = this.Mesh.softwareMesh;
            this.assetParams = null;
        }
    }
    
    public static void VectorToWorldCoords(final IsoGameCharacter isoGameCharacter, final Vector3 vector3) {
        final float renderedAngle = isoGameCharacter.getAnimationPlayer().getRenderedAngle();
        vector3.x = -vector3.x;
        vector3.rotatey(renderedAngle);
        final float y = vector3.y;
        vector3.y = vector3.z;
        vector3.z = y * 0.6f;
        vector3.x *= 1.5f;
        vector3.y *= 1.5f;
        vector3.x += isoGameCharacter.x;
        vector3.y += isoGameCharacter.y;
        vector3.z += isoGameCharacter.z;
    }
    
    public static void BoneToWorldCoords(final IsoGameCharacter isoGameCharacter, final int n, final Vector3 vector3) {
        final AnimationPlayer animationPlayer = isoGameCharacter.getAnimationPlayer();
        vector3.x = animationPlayer.modelTransforms[n].m03;
        vector3.y = animationPlayer.modelTransforms[n].m13;
        vector3.z = animationPlayer.modelTransforms[n].m23;
        VectorToWorldCoords(isoGameCharacter, vector3);
    }
    
    public static void BoneYDirectionToWorldCoords(final IsoGameCharacter isoGameCharacter, final int n, final Vector3 vector3, final float n2) {
        final AnimationPlayer animationPlayer = isoGameCharacter.getAnimationPlayer();
        vector3.x = animationPlayer.modelTransforms[n].m01 * n2;
        vector3.y = animationPlayer.modelTransforms[n].m11 * n2;
        vector3.z = animationPlayer.modelTransforms[n].m21 * n2;
        vector3.x += animationPlayer.modelTransforms[n].m03;
        vector3.y += animationPlayer.modelTransforms[n].m13;
        vector3.z += animationPlayer.modelTransforms[n].m23;
        VectorToWorldCoords(isoGameCharacter, vector3);
    }
    
    public static void VectorToWorldCoords(final ModelSlotRenderData modelSlotRenderData, final Vector3 vector3) {
        final float animPlayerAngle = modelSlotRenderData.animPlayerAngle;
        vector3.x = -vector3.x;
        vector3.rotatey(animPlayerAngle);
        final float y = vector3.y;
        vector3.y = vector3.z;
        vector3.z = y * 0.6f;
        vector3.x *= 1.5f;
        vector3.y *= 1.5f;
        vector3.x += modelSlotRenderData.x;
        vector3.y += modelSlotRenderData.y;
        vector3.z += modelSlotRenderData.z;
    }
    
    public static void BoneToWorldCoords(final ModelSlotRenderData modelSlotRenderData, final int n, final Vector3 vector3) {
        final AnimationPlayer animPlayer = modelSlotRenderData.animPlayer;
        vector3.x = animPlayer.modelTransforms[n].m03;
        vector3.y = animPlayer.modelTransforms[n].m13;
        vector3.z = animPlayer.modelTransforms[n].m23;
        VectorToWorldCoords(modelSlotRenderData, vector3);
    }
    
    public static void CharacterModelCameraBegin(final ModelSlotRenderData modelSlotRenderData) {
        ModelCamera.instance.Begin();
        if (modelSlotRenderData.bInVehicle) {
            GL11.glMatrixMode(5888);
            GL11.glTranslatef(0.0f, modelSlotRenderData.centerOfMassY, 0.0f);
            GL11.glMatrixMode(5888);
            GL11.glRotatef(modelSlotRenderData.vehicleAngleZ, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(modelSlotRenderData.vehicleAngleY, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(modelSlotRenderData.vehicleAngleX, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(modelSlotRenderData.inVehicleX, modelSlotRenderData.inVehicleY, modelSlotRenderData.inVehicleZ * -1);
            GL11.glScalef(1.5f, 1.5f, 1.5f);
        }
    }
    
    public static void CharacterModelCameraEnd() {
        ModelCamera.instance.End();
    }
    
    public void DrawChar(final ModelSlotRenderData modelSlotRenderData, final ModelInstanceRenderData modelInstanceRenderData) {
        if (DebugOptions.instance.Character.Debug.Render.SkipCharacters.getValue()) {
            return;
        }
        if (modelSlotRenderData.character == IsoPlayer.getInstance()) {}
        if (modelSlotRenderData.alpha < 0.01f) {
            return;
        }
        if (modelSlotRenderData.animPlayer == null) {
            return;
        }
        if (Core.bDebug && GameKeyboard.isKeyDown(199)) {
            this.Effect = null;
        }
        if (this.Effect == null) {
            this.CreateShader("basicEffect");
        }
        final Shader effect = this.Effect;
        GL11.glEnable(2884);
        GL11.glCullFace(1028);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glDepthFunc(513);
        GL11.glAlphaFunc(516, 0.01f);
        GL11.glBlendFunc(770, 771);
        if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
            GL11.glPolygonMode(1032, 6913);
            GL11.glEnable(2848);
            GL11.glLineWidth(0.75f);
            final Shader orCreateShader = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", this.bStatic);
            if (orCreateShader != null) {
                orCreateShader.Start();
                if (this.bStatic) {
                    orCreateShader.setTransformMatrix(modelInstanceRenderData.xfrm, true);
                }
                else {
                    orCreateShader.setMatrixPalette(modelInstanceRenderData.matrixPalette, true);
                }
                this.Mesh.Draw(orCreateShader);
                orCreateShader.End();
            }
            GL11.glPolygonMode(1032, 6914);
            GL11.glDisable(2848);
            return;
        }
        if (effect != null) {
            effect.Start();
            effect.startCharacter(modelSlotRenderData, modelInstanceRenderData);
        }
        if (!DebugOptions.instance.DebugDraw_SkipDrawNonSkinnedModel.getValue()) {
            GameProfiler.getInstance().invokeAndMeasure("Mesh.Draw.Call", effect, this.Mesh, (shader, modelMesh) -> modelMesh.Draw(shader));
        }
        if (effect != null) {
            effect.End();
        }
        this.drawMuzzleFlash(modelInstanceRenderData);
    }
    
    private void drawMuzzleFlash(final ModelInstanceRenderData modelInstanceRenderData) {
        if (modelInstanceRenderData.m_muzzleFlash) {
            final ModelAttachment attachmentById = modelInstanceRenderData.modelInstance.getAttachmentById("muzzle");
            if (attachmentById != null) {
                final BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = BaseVehicle.TL_matrix4f_pool.get();
                final Matrix4f set = matrix4fObjectPool.alloc().set((Matrix4fc)modelInstanceRenderData.xfrm);
                set.transpose();
                final Matrix4f attachmentMatrix = modelInstanceRenderData.modelInstance.getAttachmentMatrix(attachmentById, matrix4fObjectPool.alloc());
                set.mul((Matrix4fc)attachmentMatrix, attachmentMatrix);
                MuzzleFlash.render(attachmentMatrix);
                matrix4fObjectPool.release(set);
                matrix4fObjectPool.release(attachmentMatrix);
            }
        }
    }
    
    private void drawVehicleLights(final ModelSlotRenderData modelSlotRenderData) {
        for (int i = 7; i >= 0; --i) {
            GL13.glActiveTexture(33984 + i);
            GL11.glDisable(3553);
        }
        GL11.glLineWidth(1.0f);
        GL11.glColor3f(1.0f, 1.0f, 0.0f);
        GL11.glDisable(2929);
        for (int j = 0; j < 3; ++j) {
            final ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[j];
            if (effectLight.radius > 0.0f) {
                final float x = effectLight.x;
                final float y = effectLight.y;
                final float z = effectLight.z;
                final float n = y;
                final float n2 = z;
                final float n3 = x * -54.0f;
                final float n4 = n2 * 54.0f;
                final float n5 = n * 54.0f;
                GL11.glBegin(1);
                GL11.glVertex3f(n3, n4, n5);
                GL11.glVertex3f(0.0f, 0.0f, 0.0f);
                GL11.glEnd();
            }
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
    }
    
    public static void drawBoneMtx(final org.lwjgl.util.vector.Matrix4f matrix4f) {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBegin(1);
        drawBoneMtxInternal(matrix4f);
        GL11.glEnd();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
    }
    
    private static void drawBoneMtxInternal(final org.lwjgl.util.vector.Matrix4f matrix4f) {
        final float n = 0.5f;
        final float n2 = 0.15f;
        final float n3 = 0.1f;
        final float m03 = matrix4f.m03;
        final float m4 = matrix4f.m13;
        final float m5 = matrix4f.m23;
        final float m6 = matrix4f.m00;
        final float m7 = matrix4f.m10;
        final float m8 = matrix4f.m20;
        final float m9 = matrix4f.m01;
        final float m10 = matrix4f.m11;
        final float m11 = matrix4f.m21;
        final float m12 = matrix4f.m02;
        final float m13 = matrix4f.m12;
        final float m14 = matrix4f.m22;
        drawArrowInternal(m03, m4, m5, m6, m7, m8, m12, m13, m14, n, n2, n3, 1.0f, 0.0f, 0.0f);
        drawArrowInternal(m03, m4, m5, m9, m10, m11, m12, m13, m14, n, n2, n3, 0.0f, 1.0f, 0.0f);
        drawArrowInternal(m03, m4, m5, m12, m13, m14, m6, m7, m8, n, n2, n3, 0.0f, 0.0f, 1.0f);
    }
    
    private static void drawArrowInternal(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15) {
        final float n16 = 1.0f - n11;
        GL11.glColor3f(n13, n14, n15);
        GL11.glVertex3f(n, n2, n3);
        GL11.glVertex3f(n + n4 * n10, n2 + n5 * n10, n3 + n6 * n10);
        GL11.glVertex3f(n + n4 * n10, n2 + n5 * n10, n3 + n6 * n10);
        GL11.glVertex3f(n + (n4 * n16 + n7 * n12) * n10, n2 + (n5 * n16 + n8 * n12) * n10, n3 + (n6 * n16 + n9 * n12) * n10);
        GL11.glVertex3f(n + n4 * n10, n2 + n5 * n10, n3 + n6 * n10);
        GL11.glVertex3f(n + (n4 * n16 - n7 * n12) * n10, n2 + (n5 * n16 - n8 * n12) * n10, n3 + (n6 * n16 - n9 * n12) * n10);
    }
    
    public void debugDrawLightSource(final IsoLightSource isoLightSource, final float n, final float n2, final float n3, final float n4) {
        debugDrawLightSource((float)isoLightSource.x, (float)isoLightSource.y, (float)isoLightSource.z, n, n2, n3, n4);
    }
    
    public static void debugDrawLightSource(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        final float n8 = n - n4 + 0.5f;
        final float n9 = n2 - n5 + 0.5f;
        final float n10 = n3 - n6 + 0.0f;
        final float n11 = n8 * 0.67f;
        final float n12 = n9 * 0.67f;
        final float n13 = n11;
        final float n14 = n12;
        final float n15 = (float)(n13 * Math.cos(n7) - n14 * Math.sin(n7));
        final float n16 = (float)(n13 * Math.sin(n7) + n14 * Math.cos(n7));
        final float n17 = n15 * -1.0f;
        final float n18 = n10;
        final float n19 = n16;
        final float n20 = n18;
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glBegin(1);
        GL11.glColor3f(1.0f, 1.0f, 0.0f);
        GL11.glVertex3f(n17, n20, n19);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glVertex3f(n17, n20, n19);
        GL11.glVertex3f(n17 + 0.1f, n20, n19);
        GL11.glVertex3f(n17, n20, n19);
        GL11.glVertex3f(n17, n20 + 0.1f, n19);
        GL11.glVertex3f(n17, n20, n19);
        GL11.glVertex3f(n17, n20, n19 + 0.1f);
        GL11.glEnd();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
    }
    
    public void DrawVehicle(final ModelSlotRenderData modelSlotRenderData, final ModelInstanceRenderData modelInstanceRenderData) {
        if (DebugOptions.instance.ModelRenderSkipVehicles.getValue()) {
            return;
        }
        final ModelInstance modelInstance = modelInstanceRenderData.modelInstance;
        final float ambientR = modelSlotRenderData.ambientR;
        final Texture tex = modelInstanceRenderData.tex;
        final float tintR = modelInstanceRenderData.tintR;
        final float tintG = modelInstanceRenderData.tintG;
        final float tintB = modelInstanceRenderData.tintB;
        PZGLUtil.checkGLErrorThrow("Model.drawVehicle Enter inst: %s, instTex: %s, slotData: %s", modelInstance, tex, modelSlotRenderData);
        GL11.glEnable(2884);
        GL11.glCullFace((modelInstance.m_modelScript != null && modelInstance.m_modelScript.invertX) ? 1029 : 1028);
        GL11.glEnable(2929);
        GL11.glDepthFunc(513);
        ModelCamera.instance.Begin();
        GL11.glMatrixMode(5888);
        GL11.glTranslatef(0.0f, modelSlotRenderData.centerOfMassY, 0.0f);
        final Shader effect = this.Effect;
        PZGLUtil.pushAndMultMatrix(5888, modelInstanceRenderData.xfrm);
        if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
            GL11.glPolygonMode(1032, 6913);
            GL11.glEnable(2848);
            GL11.glLineWidth(0.75f);
            final Shader orCreateShader = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", this.bStatic);
            if (orCreateShader != null) {
                orCreateShader.Start();
                if (this.bStatic) {
                    orCreateShader.setTransformMatrix(Model.IDENTITY, false);
                }
                else {
                    orCreateShader.setMatrixPalette(modelInstanceRenderData.matrixPalette, true);
                }
                this.Mesh.Draw(orCreateShader);
                orCreateShader.End();
            }
            GL11.glDisable(2848);
            PZGLUtil.popMatrix(5888);
            ModelCamera.instance.End();
            return;
        }
        if (effect != null) {
            effect.Start();
            this.setLights(modelSlotRenderData, 3);
            if (effect.isVehicleShader()) {
                VehicleModelInstance vehicleModelInstance = Type.tryCastTo(modelInstance, VehicleModelInstance.class);
                if (modelInstance instanceof VehicleSubModelInstance) {
                    vehicleModelInstance = Type.tryCastTo(modelInstance.parent, VehicleModelInstance.class);
                }
                effect.setTexture(vehicleModelInstance.tex, "Texture0", 0);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureRust, "TextureRust", 1);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureMask, "TextureMask", 2);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureLights, "TextureLights", 3);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureDamage1Overlay, "TextureDamage1Overlay", 4);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureDamage1Shell, "TextureDamage1Shell", 5);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureDamage2Overlay, "TextureDamage2Overlay", 6);
                GL11.glTexEnvi(8960, 8704, 7681);
                effect.setTexture(vehicleModelInstance.textureDamage2Shell, "TextureDamage2Shell", 7);
                GL11.glTexEnvi(8960, 8704, 7681);
                try {
                    if (Core.getInstance().getPerfReflectionsOnLoad()) {
                        effect.setTexture((Texture)SkyBox.getInstance().getTextureCurrent(), "TextureReflectionA", 8);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        GL11.glGetError();
                    }
                }
                catch (Throwable t) {}
                try {
                    if (Core.getInstance().getPerfReflectionsOnLoad()) {
                        effect.setTexture((Texture)SkyBox.getInstance().getTexturePrev(), "TextureReflectionB", 9);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        GL11.glGetError();
                    }
                }
                catch (Throwable t2) {}
                effect.setReflectionParam(SkyBox.getInstance().getTextureShift(), vehicleModelInstance.refWindows, vehicleModelInstance.refBody);
                effect.setTextureUninstall1(vehicleModelInstance.textureUninstall1);
                effect.setTextureUninstall2(vehicleModelInstance.textureUninstall2);
                effect.setTextureLightsEnables1(vehicleModelInstance.textureLightsEnables1);
                effect.setTextureLightsEnables2(vehicleModelInstance.textureLightsEnables2);
                effect.setTextureDamage1Enables1(vehicleModelInstance.textureDamage1Enables1);
                effect.setTextureDamage1Enables2(vehicleModelInstance.textureDamage1Enables2);
                effect.setTextureDamage2Enables1(vehicleModelInstance.textureDamage2Enables1);
                effect.setTextureDamage2Enables2(vehicleModelInstance.textureDamage2Enables2);
                effect.setMatrixBlood1(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2);
                effect.setMatrixBlood2(vehicleModelInstance.matrixBlood2Enables1, vehicleModelInstance.matrixBlood2Enables2);
                effect.setTextureRustA(vehicleModelInstance.textureRustA);
                effect.setTexturePainColor(vehicleModelInstance.painColor, modelSlotRenderData.alpha);
                if (this.bStatic) {
                    effect.setTransformMatrix(Model.IDENTITY, false);
                }
                else {
                    effect.setMatrixPalette(modelInstanceRenderData.matrixPalette, true);
                }
            }
            else if (modelInstance instanceof VehicleSubModelInstance) {
                GL13.glActiveTexture(33984);
                effect.setTexture(tex, "Texture", 0);
                effect.setShaderAlpha(modelSlotRenderData.alpha);
                if (this.bStatic) {
                    effect.setTransformMatrix(Model.IDENTITY, false);
                }
            }
            else {
                GL13.glActiveTexture(33984);
                effect.setTexture(tex, "Texture", 0);
            }
            effect.setAmbient(ambientR);
            effect.setTint(tintR, tintG, tintB);
            this.Mesh.Draw(effect);
            effect.End();
        }
        if (Core.bDebug && DebugOptions.instance.ModelRenderLights.getValue() && modelInstanceRenderData == modelSlotRenderData.modelData.get(0)) {
            this.drawVehicleLights(modelSlotRenderData);
        }
        PZGLUtil.popMatrix(5888);
        ModelCamera.instance.End();
        PZGLUtil.checkGLErrorThrow("Model.drawVehicle Exit inst: %s, instTex: %s, slotData: %s", modelInstance, tex, modelSlotRenderData);
    }
    
    public static void debugDrawAxis(final float n, final float n2, final float n3, final float n4, final float n5) {
        for (int i = 0; i < 8; ++i) {
            GL13.glActiveTexture(33984 + i);
            GL11.glDisable(3553);
        }
        GL11.glDisable(2929);
        GL11.glLineWidth(n5);
        GL11.glBegin(1);
        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glVertex3f(n, n2, n3);
        GL11.glVertex3f(n + n4, n2, n3);
        GL11.glColor3f(0.0f, 1.0f, 0.0f);
        GL11.glVertex3f(n, n2, n3);
        GL11.glVertex3f(n, n2 + n4, n3);
        GL11.glColor3f(0.0f, 0.0f, 1.0f);
        GL11.glVertex3f(n, n2, n3);
        GL11.glVertex3f(n, n2, n3 + n4);
        GL11.glEnd();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL13.glActiveTexture(33984);
        GL11.glEnable(3553);
    }
    
    private void setLights(final ModelSlotRenderData modelSlotRenderData, final int n) {
        for (int i = 0; i < n; ++i) {
            final ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[i];
            this.Effect.setLight(i, effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, (float)effectLight.radius, modelSlotRenderData.animPlayerAngle, modelSlotRenderData.x, modelSlotRenderData.y, modelSlotRenderData.z, modelSlotRenderData.object);
        }
    }
    
    public void CreateShader(final String s) {
        if (ModelManager.NoOpenGL) {
            return;
        }
        Lambda.invoke(RenderThread::invokeOnRenderContext, this, s, (model, s2) -> model.Effect = ShaderManager.instance.getOrCreateShader(s2, model.bStatic));
    }
    
    @Override
    public AssetType getType() {
        return Model.ASSET_TYPE;
    }
    
    @Override
    protected void onBeforeReady() {
        super.onBeforeReady();
        this.Tag = this.Mesh.skinningData;
        this.softwareMesh = this.Mesh.softwareMesh;
        this.assetParams = null;
    }
    
    static {
        m_staticReusableFloatBuffer = BufferUtils.createFloatBuffer(128);
        IDENTITY = new Matrix4f();
        debugDrawColours = new Color[] { new Color(230, 25, 75), new Color(60, 180, 75), new Color(255, 225, 25), new Color(0, 130, 200), new Color(245, 130, 48), new Color(145, 30, 180), new Color(70, 240, 240), new Color(240, 50, 230), new Color(210, 245, 60), new Color(250, 190, 190), new Color(0, 128, 128), new Color(230, 190, 255), new Color(170, 110, 40), new Color(255, 250, 200), new Color(128, 0, 0), new Color(170, 255, 195), new Color(128, 128, 0), new Color(255, 215, 180), new Color(0, 0, 128), new Color(128, 128, 128), new Color(255, 255, 255), new Color(0, 0, 0) };
        Model.tempo = new Vector3();
        ASSET_TYPE = new AssetType("Model");
    }
    
    public static final class ModelAssetParams extends AssetManager.AssetParams
    {
        public String meshName;
        public String textureName;
        public int textureFlags;
        public String shaderName;
        public boolean bStatic;
        public ModelMesh animationsModel;
        
        public ModelAssetParams() {
            this.bStatic = false;
        }
    }
}
