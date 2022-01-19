// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.opengl.PZGLUtil;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.core.math.PZMath;
import org.joml.Matrix4fc;
import java.util.List;
import zombie.vehicles.BaseVehicle;
import zombie.debug.DebugOptions;
import org.joml.Matrix4f;
import java.util.ArrayList;
import zombie.util.Pool;
import zombie.util.PooledObject;

public final class ModelInstanceDebugRenderData extends PooledObject
{
    private static final Pool<ModelInstanceDebugRenderData> s_pool;
    private final ArrayList<Matrix4f> m_attachmentMatrices;
    
    public static ModelInstanceDebugRenderData alloc() {
        return ModelInstanceDebugRenderData.s_pool.alloc();
    }
    
    public ModelInstanceDebugRenderData() {
        this.m_attachmentMatrices = new ArrayList<Matrix4f>();
    }
    
    public ModelInstanceDebugRenderData init(final ModelSlotRenderData modelSlotRenderData, final ModelInstanceRenderData modelInstanceRenderData) {
        this.initAttachments(modelSlotRenderData, modelInstanceRenderData);
        return this;
    }
    
    public void render() {
        this.renderAttachments();
        if (DebugOptions.instance.ModelRenderAxis.getValue()) {
            Model.debugDrawAxis(0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        }
    }
    
    private void initAttachments(final ModelSlotRenderData modelSlotRenderData, final ModelInstanceRenderData modelInstanceRenderData) {
        final BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = BaseVehicle.TL_matrix4f_pool.get();
        matrix4fObjectPool.release(this.m_attachmentMatrices);
        this.m_attachmentMatrices.clear();
        if (!DebugOptions.instance.ModelRenderAttachments.getValue()) {
            return;
        }
        final ModelScript modelScript = modelInstanceRenderData.modelInstance.m_modelScript;
        if (modelScript == null) {
            return;
        }
        final Matrix4f set = matrix4fObjectPool.alloc().set((Matrix4fc)modelInstanceRenderData.xfrm);
        final Matrix4f matrix4f = matrix4fObjectPool.alloc();
        set.transpose();
        for (int i = 0; i < modelScript.getAttachmentCount(); ++i) {
            final ModelAttachment attachment = modelScript.getAttachment(i);
            final Matrix4f e = matrix4fObjectPool.alloc();
            modelInstanceRenderData.modelInstance.getAttachmentMatrix(attachment, e);
            if (modelInstanceRenderData.model.bStatic || attachment.getBone() == null) {
                set.mul((Matrix4fc)e, e);
            }
            else if (modelSlotRenderData.animPlayer != null && modelSlotRenderData.animPlayer.hasSkinningData()) {
                PZMath.convertMatrix(modelSlotRenderData.animPlayer.modelTransforms[modelSlotRenderData.animPlayer.getSkinningBoneIndex(attachment.getBone(), 0)], matrix4f);
                matrix4f.transpose();
                matrix4f.mul((Matrix4fc)e, e);
                set.mul((Matrix4fc)e, e);
            }
            this.m_attachmentMatrices.add(e);
        }
        matrix4fObjectPool.release(matrix4f);
        matrix4fObjectPool.release(set);
    }
    
    private void renderAttachments() {
        for (int i = 0; i < this.m_attachmentMatrices.size(); ++i) {
            PZGLUtil.pushAndMultMatrix(5888, this.m_attachmentMatrices.get(i));
            Model.debugDrawAxis(0.0f, 0.0f, 0.0f, 0.05f, 1.0f);
            PZGLUtil.popMatrix(5888);
        }
    }
    
    static {
        s_pool = new Pool<ModelInstanceDebugRenderData>(ModelInstanceDebugRenderData::new);
    }
}
