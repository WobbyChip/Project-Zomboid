// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.util.List;
import zombie.util.Pool;
import zombie.core.skinnedmodel.ModelManager;
import java.util.ArrayList;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.math.PZMath;
import zombie.util.StringUtils;
import org.joml.Vector3fc;
import zombie.scripting.objects.ModelAttachment;
import org.joml.Matrix4fc;
import zombie.vehicles.BaseVehicle;
import zombie.util.Type;
import org.lwjglx.BufferUtils;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.popman.ObjectPool;
import org.joml.Matrix4f;
import java.nio.FloatBuffer;
import zombie.core.textures.Texture;
import org.lwjgl.util.vector.Vector3f;

public final class ModelInstanceRenderData
{
    private static final Vector3f tempVector3f;
    public Model model;
    public Texture tex;
    public float depthBias;
    public float hue;
    public float tintR;
    public float tintG;
    public float tintB;
    public int parentBone;
    public FloatBuffer matrixPalette;
    public final Matrix4f xfrm;
    public SoftwareModelMeshInstance softwareMesh;
    public ModelInstance modelInstance;
    public boolean m_muzzleFlash;
    protected ModelInstanceDebugRenderData m_debugRenderData;
    private static final ObjectPool<ModelInstanceRenderData> pool;
    
    public ModelInstanceRenderData() {
        this.xfrm = new Matrix4f();
        this.m_muzzleFlash = false;
    }
    
    public ModelInstanceRenderData init(final ModelInstance modelInstance) {
        this.model = modelInstance.model;
        this.tex = modelInstance.tex;
        this.depthBias = modelInstance.depthBias;
        this.hue = modelInstance.hue;
        this.parentBone = modelInstance.parentBone;
        assert modelInstance.AnimPlayer != null;
        this.m_muzzleFlash = false;
        this.xfrm.identity();
        if (modelInstance.AnimPlayer != null) {
            if (!this.model.bStatic) {
                final SkinningData skinningData = (SkinningData)this.model.Tag;
                if (Core.bDebug && skinningData == null) {
                    DebugLog.General.warn((Object)"skinningData is null, matrixPalette may be invalid");
                }
                final org.lwjgl.util.vector.Matrix4f[] skinTransforms = modelInstance.AnimPlayer.getSkinTransforms(skinningData);
                if (this.matrixPalette == null || this.matrixPalette.capacity() < skinTransforms.length * 16) {
                    this.matrixPalette = BufferUtils.createFloatBuffer(skinTransforms.length * 16);
                }
                this.matrixPalette.clear();
                for (int i = 0; i < skinTransforms.length; ++i) {
                    skinTransforms[i].store(this.matrixPalette);
                }
                this.matrixPalette.flip();
            }
        }
        final VehicleSubModelInstance vehicleSubModelInstance = Type.tryCastTo(modelInstance, VehicleSubModelInstance.class);
        if (modelInstance instanceof VehicleModelInstance || vehicleSubModelInstance != null) {
            if (modelInstance instanceof VehicleModelInstance) {
                this.xfrm.set((Matrix4fc)((BaseVehicle)modelInstance.object).renderTransform);
            }
            else {
                this.xfrm.set((Matrix4fc)vehicleSubModelInstance.modelInfo.renderTransform);
            }
            if (modelInstance.model.Mesh != null && modelInstance.model.Mesh.isReady() && modelInstance.model.Mesh.m_transform != null) {
                modelInstance.model.Mesh.m_transform.transpose();
                this.xfrm.mul((Matrix4fc)modelInstance.model.Mesh.m_transform);
                modelInstance.model.Mesh.m_transform.transpose();
            }
        }
        this.softwareMesh = modelInstance.softwareMesh;
        this.modelInstance = modelInstance;
        ++modelInstance.renderRefCount;
        if (modelInstance.getTextureInitializer() != null) {
            modelInstance.getTextureInitializer().renderMain();
        }
        return this;
    }
    
    public void renderDebug() {
        if (this.m_debugRenderData != null) {
            this.m_debugRenderData.render();
        }
    }
    
    public void RenderCharacter(final ModelSlotRenderData modelSlotRenderData) {
        this.tintR = this.modelInstance.tintR;
        this.tintG = this.modelInstance.tintG;
        this.tintB = this.modelInstance.tintB;
        this.tex = this.modelInstance.tex;
        if (this.tex == null && this.modelInstance.model.tex == null) {
            return;
        }
        this.model.DrawChar(modelSlotRenderData, this);
    }
    
    public void RenderVehicle(final ModelSlotRenderData modelSlotRenderData) {
        this.tintR = this.modelInstance.tintR;
        this.tintG = this.modelInstance.tintG;
        this.tintB = this.modelInstance.tintB;
        this.tex = this.modelInstance.tex;
        if (this.tex == null && this.modelInstance.model.tex == null) {
            return;
        }
        this.model.DrawVehicle(modelSlotRenderData, this);
    }
    
    public static Matrix4f makeAttachmentTransform(final ModelAttachment modelAttachment, final Matrix4f matrix4f) {
        matrix4f.translation((Vector3fc)modelAttachment.getOffset());
        final org.joml.Vector3f rotate = modelAttachment.getRotate();
        matrix4f.rotateXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
        return matrix4f;
    }
    
    public static void applyBoneTransform(final ModelInstance modelInstance, final String s, final Matrix4f matrix4f) {
        if (modelInstance == null || modelInstance.AnimPlayer == null) {
            return;
        }
        if (StringUtils.isNullOrWhitespace(s)) {
            return;
        }
        final int skinningBoneIndex = modelInstance.AnimPlayer.getSkinningBoneIndex(s, -1);
        if (skinningBoneIndex == -1) {
            return;
        }
        final org.lwjgl.util.vector.Matrix4f getPropBoneMatrix = modelInstance.AnimPlayer.GetPropBoneMatrix(skinningBoneIndex);
        final Matrix4f matrix4f2 = BaseVehicle.TL_matrix4f_pool.get().alloc();
        PZMath.convertMatrix(getPropBoneMatrix, matrix4f2);
        matrix4f2.transpose();
        matrix4f.mul((Matrix4fc)matrix4f2);
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f2);
    }
    
    public ModelInstanceRenderData transformToParent(final ModelInstanceRenderData modelInstanceRenderData) {
        if (this.modelInstance instanceof VehicleModelInstance || this.modelInstance instanceof VehicleSubModelInstance) {
            return this;
        }
        if (modelInstanceRenderData == null) {
            return this;
        }
        this.xfrm.set((Matrix4fc)modelInstanceRenderData.xfrm);
        this.xfrm.transpose();
        final Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
        final ModelAttachment attachmentById = modelInstanceRenderData.modelInstance.getAttachmentById(this.modelInstance.attachmentNameParent);
        if (attachmentById == null) {
            if (this.modelInstance.parentBoneName != null && modelInstanceRenderData.modelInstance.AnimPlayer != null) {
                applyBoneTransform(modelInstanceRenderData.modelInstance, this.modelInstance.parentBoneName, this.xfrm);
            }
        }
        else {
            applyBoneTransform(modelInstanceRenderData.modelInstance, attachmentById.getBone(), this.xfrm);
            makeAttachmentTransform(attachmentById, matrix4f);
            this.xfrm.mul((Matrix4fc)matrix4f);
        }
        final ModelAttachment attachmentById2 = this.modelInstance.getAttachmentById(this.modelInstance.attachmentNameSelf);
        if (attachmentById2 != null) {
            makeAttachmentTransform(attachmentById2, matrix4f);
            matrix4f.invert();
            this.xfrm.mul((Matrix4fc)matrix4f);
        }
        if (this.modelInstance.model.Mesh != null && this.modelInstance.model.Mesh.isReady() && this.modelInstance.model.Mesh.m_transform != null) {
            this.xfrm.mul((Matrix4fc)this.modelInstance.model.Mesh.m_transform);
        }
        if (this.modelInstance.scale != 1.0f) {
            this.xfrm.scale(this.modelInstance.scale);
        }
        this.xfrm.transpose();
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
        return this;
    }
    
    private void testOnBackItem(final ModelInstance modelInstance) {
        if (modelInstance.parent == null || modelInstance.parent.m_modelScript == null) {
            return;
        }
        final AnimationPlayer animPlayer = modelInstance.parent.AnimPlayer;
        ModelAttachment modelAttachment = null;
        for (int i = 0; i < modelInstance.parent.m_modelScript.getAttachmentCount(); ++i) {
            final ModelAttachment attachment = modelInstance.parent.getAttachment(i);
            if (attachment.getBone() != null && this.parentBone == animPlayer.getSkinningBoneIndex(attachment.getBone(), 0)) {
                modelAttachment = attachment;
                break;
            }
        }
        if (modelAttachment == null) {
            return;
        }
        final Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
        makeAttachmentTransform(modelAttachment, matrix4f);
        this.xfrm.transpose();
        this.xfrm.mul((Matrix4fc)matrix4f);
        this.xfrm.transpose();
        final ModelAttachment attachmentById = modelInstance.getAttachmentById(modelAttachment.getId());
        if (attachmentById != null) {
            makeAttachmentTransform(attachmentById, matrix4f);
            matrix4f.invert();
            this.xfrm.transpose();
            this.xfrm.mul((Matrix4fc)matrix4f);
            this.xfrm.transpose();
        }
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
    }
    
    public static ModelInstanceRenderData alloc() {
        return ModelInstanceRenderData.pool.alloc();
    }
    
    public static void release(final ArrayList<ModelInstanceRenderData> list) {
        for (int i = 0; i < list.size(); ++i) {
            final ModelInstanceRenderData modelInstanceRenderData = list.get(i);
            if (modelInstanceRenderData.modelInstance.getTextureInitializer() != null) {
                modelInstanceRenderData.modelInstance.getTextureInitializer().postRender();
            }
            ModelManager.instance.derefModelInstance(modelInstanceRenderData.modelInstance);
            modelInstanceRenderData.modelInstance = null;
            modelInstanceRenderData.model = null;
            modelInstanceRenderData.tex = null;
            modelInstanceRenderData.softwareMesh = null;
            modelInstanceRenderData.m_debugRenderData = Pool.tryRelease(modelInstanceRenderData.m_debugRenderData);
        }
        ModelInstanceRenderData.pool.release(list);
    }
    
    static {
        tempVector3f = new Vector3f();
        pool = new ObjectPool<ModelInstanceRenderData>(ModelInstanceRenderData::new);
    }
}
