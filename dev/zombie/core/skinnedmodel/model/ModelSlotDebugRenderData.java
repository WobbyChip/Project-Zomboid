// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.opengl.PZGLUtil;
import zombie.core.Color;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL11;
import zombie.core.math.PZMath;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCamera;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.HelperFunctions;
import org.lwjgl.util.vector.Matrix4f;
import java.util.ArrayList;
import gnu.trove.list.array.TFloatArrayList;
import zombie.util.Pool;
import zombie.util.PooledObject;

public final class ModelSlotDebugRenderData extends PooledObject
{
    private static final Pool<ModelSlotDebugRenderData> s_pool;
    private ModelSlotRenderData m_slotData;
    private final TFloatArrayList m_boneCoords;
    private final ArrayList<Matrix4f> m_boneMatrices;
    private final TFloatArrayList m_squareLights;
    private org.joml.Matrix4f m_weaponMatrix;
    private float m_weaponLength;
    
    public static ModelSlotDebugRenderData alloc() {
        return ModelSlotDebugRenderData.s_pool.alloc();
    }
    
    public ModelSlotDebugRenderData() {
        this.m_boneCoords = new TFloatArrayList();
        this.m_boneMatrices = new ArrayList<Matrix4f>();
        this.m_squareLights = new TFloatArrayList();
    }
    
    public ModelSlotDebugRenderData init(final ModelSlotRenderData slotData) {
        this.m_slotData = slotData;
        this.initBoneAxis();
        this.initSkeleton();
        this.initLights();
        this.initWeaponHitPoint();
        for (int i = 0; i < slotData.modelData.size(); ++i) {
            final ModelInstanceRenderData modelInstanceRenderData = slotData.modelData.get(i);
            modelInstanceRenderData.m_debugRenderData = ModelInstanceDebugRenderData.alloc().init(slotData, modelInstanceRenderData);
        }
        return this;
    }
    
    private void initBoneAxis() {
        for (int i = 0; i < this.m_boneMatrices.size(); ++i) {
            HelperFunctions.returnMatrix(this.m_boneMatrices.get(i));
        }
        this.m_boneMatrices.clear();
        if (this.m_slotData.animPlayer == null || !this.m_slotData.animPlayer.hasSkinningData()) {
            return;
        }
        if (DebugOptions.instance.Character.Debug.Render.Bip01.getValue()) {
            this.initBoneAxis("Bip01");
        }
        if (DebugOptions.instance.Character.Debug.Render.PrimaryHandBone.getValue()) {
            this.initBoneAxis("Bip01_Prop1");
        }
        if (DebugOptions.instance.Character.Debug.Render.SecondaryHandBone.getValue()) {
            this.initBoneAxis("Bip01_Prop2");
        }
        if (DebugOptions.instance.Character.Debug.Render.TranslationData.getValue()) {
            this.initBoneAxis("Translation_Data");
        }
    }
    
    private void initBoneAxis(final String key) {
        final Integer n = this.m_slotData.animPlayer.getSkinningData().BoneIndices.get(key);
        if (n != null) {
            final Matrix4f matrix = HelperFunctions.getMatrix();
            matrix.load(this.m_slotData.animPlayer.modelTransforms[n]);
            this.m_boneMatrices.add(matrix);
        }
    }
    
    private void initSkeleton() {
        this.m_boneCoords.clear();
        if (!DebugOptions.instance.ModelRenderBones.getValue()) {
            return;
        }
        this.initSkeleton(this.m_slotData.animPlayer);
        if (this.m_slotData.object instanceof BaseVehicle) {
            for (int i = 0; i < this.m_slotData.modelData.size(); ++i) {
                final VehicleSubModelInstance vehicleSubModelInstance = Type.tryCastTo(this.m_slotData.modelData.get(i).modelInstance, VehicleSubModelInstance.class);
                if (vehicleSubModelInstance != null) {
                    this.initSkeleton(vehicleSubModelInstance.AnimPlayer);
                }
            }
        }
    }
    
    private void initSkeleton(final AnimationPlayer animationPlayer) {
        if (animationPlayer == null || !animationPlayer.hasSkinningData() || animationPlayer.isBoneTransformsNeedFirstFrame()) {
            return;
        }
        final Integer n = animationPlayer.getSkinningData().BoneIndices.get("Translation_Data");
        for (int i = 0; i < animationPlayer.modelTransforms.length; ++i) {
            if (n == null || i != n) {
                final int intValue = animationPlayer.getSkinningData().SkeletonHierarchy.get(i);
                if (intValue >= 0) {
                    this.initSkeleton(animationPlayer.modelTransforms, i);
                    this.initSkeleton(animationPlayer.modelTransforms, intValue);
                }
            }
        }
    }
    
    private void initSkeleton(final Matrix4f[] array, final int n) {
        final float m03 = array[n].m03;
        final float m4 = array[n].m13;
        final float m5 = array[n].m23;
        this.m_boneCoords.add(m03);
        this.m_boneCoords.add(m4);
        this.m_boneCoords.add(m5);
    }
    
    private void initLights() {
        this.m_squareLights.clear();
        if (!DebugOptions.instance.ModelRenderLights.getValue()) {
            return;
        }
        if (this.m_slotData.character == null) {
            return;
        }
        if (this.m_slotData.character.getCurrentSquare() == null) {
            return;
        }
        final IsoGridSquare.ILighting lighting = this.m_slotData.character.getCurrentSquare().lighting[IsoCamera.frameState.playerIndex];
        for (int i = 0; i < lighting.resultLightCount(); ++i) {
            final IsoGridSquare.ResultLight resultLight = lighting.getResultLight(i);
            this.m_squareLights.add((float)resultLight.x);
            this.m_squareLights.add((float)resultLight.y);
            this.m_squareLights.add((float)resultLight.z);
        }
    }
    
    private void initWeaponHitPoint() {
        if (this.m_weaponMatrix != null) {
            BaseVehicle.TL_matrix4f_pool.get().release(this.m_weaponMatrix);
            this.m_weaponMatrix = null;
        }
        if (!DebugOptions.instance.ModelRenderWeaponHitPoint.getValue()) {
            return;
        }
        if (this.m_slotData.animPlayer == null || !this.m_slotData.animPlayer.hasSkinningData()) {
            return;
        }
        if (this.m_slotData.character == null) {
            return;
        }
        final Integer n = this.m_slotData.animPlayer.getSkinningData().BoneIndices.get("Bip01_Prop1");
        if (n == null) {
            return;
        }
        final HandWeapon handWeapon = Type.tryCastTo(this.m_slotData.character.getPrimaryHandItem(), HandWeapon.class);
        if (handWeapon == null) {
            return;
        }
        this.m_weaponLength = handWeapon.WeaponLength;
        PZMath.convertMatrix(this.m_slotData.animPlayer.modelTransforms[n], this.m_weaponMatrix = BaseVehicle.TL_matrix4f_pool.get().alloc());
        this.m_weaponMatrix.transpose();
    }
    
    public void render() {
        this.renderBonesAxis();
        this.renderSkeleton();
        this.renderLights();
        this.renderWeaponHitPoint();
    }
    
    private void renderBonesAxis() {
        for (int i = 0; i < this.m_boneMatrices.size(); ++i) {
            Model.drawBoneMtx(this.m_boneMatrices.get(i));
        }
    }
    
    private void renderSkeleton() {
        if (this.m_boneCoords.isEmpty()) {
            return;
        }
        GL11.glDisable(2929);
        for (int i = 7; i >= 0; --i) {
            GL13.glActiveTexture(33984 + i);
            GL11.glDisable(3553);
        }
        GL11.glLineWidth(1.0f);
        GL11.glBegin(1);
        for (int j = 0; j < this.m_boneCoords.size(); j += 6) {
            final Color color = Model.debugDrawColours[j % Model.debugDrawColours.length];
            GL11.glColor3f(color.r, color.g, color.b);
            GL11.glVertex3f(this.m_boneCoords.get(j), this.m_boneCoords.get(j + 1), this.m_boneCoords.get(j + 2));
            GL11.glVertex3f(this.m_boneCoords.get(j + 3), this.m_boneCoords.get(j + 4), this.m_boneCoords.get(j + 5));
        }
        GL11.glEnd();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
    }
    
    private void renderLights() {
        for (int i = 0; i < this.m_squareLights.size(); i += 3) {
            Model.debugDrawLightSource(this.m_squareLights.get(i), this.m_squareLights.get(i + 1), this.m_squareLights.get(i + 2), this.m_slotData.x, this.m_slotData.y, this.m_slotData.z, -this.m_slotData.animPlayerAngle);
        }
    }
    
    private void renderWeaponHitPoint() {
        if (this.m_weaponMatrix == null) {
            return;
        }
        PZGLUtil.pushAndMultMatrix(5888, this.m_weaponMatrix);
        Model.debugDrawAxis(0.0f, this.m_weaponLength, 0.0f, 0.05f, 1.0f);
        PZGLUtil.popMatrix(5888);
    }
    
    static {
        s_pool = new Pool<ModelSlotDebugRenderData>(ModelSlotDebugRenderData::new);
    }
}
