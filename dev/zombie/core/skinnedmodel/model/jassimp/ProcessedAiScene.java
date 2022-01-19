// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import zombie.core.skinnedmodel.animation.Keyframe;
import java.util.Map;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.animation.AnimationClip;
import java.util.HashMap;
import java.util.List;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.model.VertexBufferObject;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.ModelMesh;
import java.util.Iterator;
import zombie.util.StringUtils;
import jassimp.AiMesh;
import jassimp.AiScene;
import jassimp.AiMatrix4f;
import jassimp.AiWrapperProvider;
import jassimp.AiNode;
import jassimp.AiBuiltInWrapperProvider;
import zombie.debug.DebugLog;
import org.lwjgl.util.vector.Matrix4f;

public final class ProcessedAiScene
{
    private ImportedSkeleton skeleton;
    private ImportedSkinnedMesh skinnedMesh;
    private ImportedStaticMesh staticMesh;
    private Matrix4f transform;
    
    private ProcessedAiScene() {
        this.transform = null;
    }
    
    public static ProcessedAiScene process(final ProcessedAiSceneParams processedAiSceneParams) {
        final ProcessedAiScene processedAiScene = new ProcessedAiScene();
        processedAiScene.processAiScene(processedAiSceneParams);
        return processedAiScene;
    }
    
    private void processAiScene(final ProcessedAiSceneParams processedAiSceneParams) {
        final AiScene scene = processedAiSceneParams.scene;
        final JAssImpImporter.LoadMode mode = processedAiSceneParams.mode;
        final String meshName = processedAiSceneParams.meshName;
        final AiMesh mesh = this.findMesh(scene, meshName);
        if (mesh == null) {
            DebugLog.General.error("No such mesh \"%s\"", meshName);
            return;
        }
        if (mode == JAssImpImporter.LoadMode.StaticMesh || !mesh.hasBones()) {
            this.staticMesh = new ImportedStaticMesh(mesh);
        }
        else {
            this.skeleton = ImportedSkeleton.process(ImportedSkeletonParams.create(processedAiSceneParams, mesh));
            if (mode != JAssImpImporter.LoadMode.AnimationOnly) {
                this.skinnedMesh = new ImportedSkinnedMesh(this.skeleton, mesh);
            }
        }
        if (this.staticMesh == null && this.skinnedMesh == null) {
            return;
        }
        final AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
        final AiNode parentNodeForMesh = this.findParentNodeForMesh(scene.getMeshes().indexOf(mesh), (AiNode)scene.getSceneRoot((AiWrapperProvider)aiBuiltInWrapperProvider));
        if (parentNodeForMesh == null) {
            return;
        }
        this.transform = JAssImpImporter.getMatrixFromAiMatrix((AiMatrix4f)parentNodeForMesh.getTransform((AiWrapperProvider)aiBuiltInWrapperProvider));
        for (AiNode aiNode = parentNodeForMesh.getParent(); aiNode != null; aiNode = aiNode.getParent()) {
            Matrix4f.mul(JAssImpImporter.getMatrixFromAiMatrix((AiMatrix4f)aiNode.getTransform((AiWrapperProvider)aiBuiltInWrapperProvider)), this.transform, this.transform);
        }
    }
    
    private AiMesh findMesh(final AiScene aiScene, final String anotherString) {
        if (aiScene.getNumMeshes() == 0) {
            return null;
        }
        if (StringUtils.isNullOrWhitespace(anotherString)) {
            for (final AiMesh aiMesh : aiScene.getMeshes()) {
                if (aiMesh.hasBones()) {
                    return aiMesh;
                }
            }
            return aiScene.getMeshes().get(0);
        }
        for (final AiMesh aiMesh2 : aiScene.getMeshes()) {
            if (aiMesh2.getName().equalsIgnoreCase(anotherString)) {
                return aiMesh2;
            }
        }
        final AiNode findNode = JAssImpImporter.FindNode(anotherString, (AiNode)aiScene.getSceneRoot((AiWrapperProvider)new AiBuiltInWrapperProvider()));
        if (findNode != null && findNode.getNumMeshes() == 1) {
            return (AiMesh)aiScene.getMeshes().get(findNode.getMeshes()[0]);
        }
        return null;
    }
    
    private AiNode findParentNodeForMesh(final int n, final AiNode aiNode) {
        for (int i = 0; i < aiNode.getNumMeshes(); ++i) {
            if (aiNode.getMeshes()[i] == n) {
                return aiNode;
            }
        }
        final Iterator<AiNode> iterator = aiNode.getChildren().iterator();
        while (iterator.hasNext()) {
            final AiNode parentNodeForMesh = this.findParentNodeForMesh(n, iterator.next());
            if (parentNodeForMesh != null) {
                return parentNodeForMesh;
            }
        }
        return null;
    }
    
    public void applyToMesh(final ModelMesh modelMesh, final JAssImpImporter.LoadMode loadMode, final boolean b, final SkinningData skinningData) {
        modelMesh.m_transform = null;
        if (this.transform != null) {
            modelMesh.m_transform = PZMath.convertMatrix(this.transform, new org.joml.Matrix4f());
        }
        if (this.staticMesh != null && !ModelManager.NoOpenGL) {
            RenderThread.queueInvokeOnRenderContext(() -> {
                modelMesh.SetVertexBuffer(new VertexBufferObject(this.staticMesh.verticesUnskinned, this.staticMesh.elements));
                if (ModelManager.instance.bCreateSoftwareMeshes) {
                    modelMesh.softwareMesh.vb = modelMesh.vb;
                }
                return;
            });
        }
        if (modelMesh.skinningData != null) {
            if (skinningData == null || modelMesh.skinningData.AnimationClips != skinningData.AnimationClips) {
                modelMesh.skinningData.AnimationClips.clear();
            }
            modelMesh.skinningData.InverseBindPose.clear();
            modelMesh.skinningData.BindPose.clear();
            modelMesh.skinningData.BoneOffset.clear();
            modelMesh.skinningData.BoneIndices.clear();
            modelMesh.skinningData.SkeletonHierarchy.clear();
            modelMesh.skinningData = null;
        }
        if (this.skeleton != null) {
            final ImportedSkeleton skeleton = this.skeleton;
            HashMap<String, AnimationClip> hashMap = skeleton.clips;
            if (skinningData != null) {
                skeleton.clips.clear();
                hashMap = skinningData.AnimationClips;
            }
            JAssImpImporter.replaceHashMapKeys(skeleton.boneIndices, "SkinningData.boneIndices");
            modelMesh.skinningData = new SkinningData(hashMap, skeleton.bindPose, skeleton.invBindPose, skeleton.skinOffsetMatrices, skeleton.SkeletonHierarchy, skeleton.boneIndices);
        }
        if (this.skinnedMesh != null && !ModelManager.NoOpenGL) {
            RenderThread.queueInvokeOnRenderContext(() -> {
                modelMesh.SetVertexBuffer(new VertexBufferObject(this.skinnedMesh.vertices, this.skinnedMesh.elements, b));
                if (ModelManager.instance.bCreateSoftwareMeshes) {
                    modelMesh.softwareMesh.vb = modelMesh.vb;
                }
                return;
            });
        }
        this.skeleton = null;
        this.skinnedMesh = null;
        this.staticMesh = null;
    }
    
    public void applyToAnimation(final AnimationAsset animationAsset) {
        final Iterator<Map.Entry<String, AnimationClip>> iterator = this.skeleton.clips.entrySet().iterator();
        while (iterator.hasNext()) {
            for (final Keyframe keyframe : iterator.next().getValue().getKeyframes()) {
                keyframe.BoneName = JAssImpImporter.getSharedString(keyframe.BoneName, "Keyframe.BoneName");
            }
        }
        animationAsset.AnimationClips = this.skeleton.clips;
        this.skeleton = null;
    }
}
