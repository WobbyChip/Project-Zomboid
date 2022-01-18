// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.util.vector.Quaternion;
import zombie.debug.DebugLog;
import java.io.IOException;
import jassimp.AiScene;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiSceneParams;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import java.util.Set;
import jassimp.Jassimp;
import java.util.EnumSet;
import jassimp.AiPostProcessSteps;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileSystem;

public class FileTask_LoadMesh extends FileTask_AbstractLoadModel
{
    ModelMesh mesh;
    
    public FileTask_LoadMesh(final ModelMesh mesh, final FileSystem fileSystem, final IFileTaskCallback fileTaskCallback) {
        super(fileSystem, fileTaskCallback, "media/models", "media/models_x");
        this.mesh = mesh;
    }
    
    @Override
    public String getErrorMessage() {
        return this.m_fileName;
    }
    
    @Override
    public void done() {
        MeshAssetManager.instance.addWatchedFile(this.m_fileName);
        this.mesh.m_fullPath = this.m_fileName;
        this.m_fileName = null;
        this.mesh = null;
    }
    
    @Override
    public String getRawFileName() {
        final String path = this.mesh.getPath().getPath();
        final int index = path.indexOf(124);
        if (index != -1) {
            return path.substring(0, index);
        }
        return path;
    }
    
    private String getMeshName() {
        final String path = this.mesh.getPath().getPath();
        final int index = path.indexOf(124);
        if (index != -1) {
            return path.substring(index + 1);
        }
        return null;
    }
    
    @Override
    public ProcessedAiScene loadX() throws IOException {
        final AiScene importFile = Jassimp.importFile(this.m_fileName, (Set)EnumSet.of(AiPostProcessSteps.FIND_INSTANCES, new AiPostProcessSteps[] { AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.LIMIT_BONE_WEIGHTS, AiPostProcessSteps.TRIANGULATE, AiPostProcessSteps.OPTIMIZE_MESHES, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS, AiPostProcessSteps.JOIN_IDENTICAL_VERTICES }));
        final JAssImpImporter.LoadMode mode = this.mesh.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
        final ModelMesh animationsMesh = this.mesh.assetParams.animationsMesh;
        final SkinningData skinnedTo = (animationsMesh == null) ? null : animationsMesh.skinningData;
        final ProcessedAiSceneParams create = ProcessedAiSceneParams.create();
        create.scene = importFile;
        create.mode = mode;
        create.skinnedTo = skinnedTo;
        create.meshName = this.getMeshName();
        final ProcessedAiScene process = ProcessedAiScene.process(create);
        JAssImpImporter.takeOutTheTrash(importFile);
        return process;
    }
    
    @Override
    public ProcessedAiScene loadFBX() throws IOException {
        DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
        final AiScene importFile = Jassimp.importFile(this.m_fileName, (Set)EnumSet.of(AiPostProcessSteps.FIND_INSTANCES, new AiPostProcessSteps[] { AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.LIMIT_BONE_WEIGHTS, AiPostProcessSteps.TRIANGULATE, AiPostProcessSteps.OPTIMIZE_MESHES, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS, AiPostProcessSteps.JOIN_IDENTICAL_VERTICES }));
        final JAssImpImporter.LoadMode mode = this.mesh.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
        final ModelMesh animationsMesh = this.mesh.assetParams.animationsMesh;
        final SkinningData skinnedTo = (animationsMesh == null) ? null : animationsMesh.skinningData;
        final Quaternion animBonesRotateModifier = new Quaternion();
        animBonesRotateModifier.setFromAxisAngle(new Vector4f(1.0f, 0.0f, 0.0f, -1.5707964f));
        final ProcessedAiSceneParams create = ProcessedAiSceneParams.create();
        create.scene = importFile;
        create.mode = mode;
        create.skinnedTo = skinnedTo;
        create.meshName = this.getMeshName();
        create.animBonesScaleModifier = 0.01f;
        create.animBonesRotateModifier = animBonesRotateModifier;
        final ProcessedAiScene process = ProcessedAiScene.process(create);
        JAssImpImporter.takeOutTheTrash(importFile);
        return process;
    }
    
    @Override
    public ModelTxt loadTxt() throws IOException {
        final boolean bStatic = this.mesh.assetParams.bStatic;
        final boolean b = false;
        final ModelMesh animationsMesh = this.mesh.assetParams.animationsMesh;
        return ModelLoader.instance.loadTxt(this.m_fileName, bStatic, b, (animationsMesh == null) ? null : animationsMesh.skinningData);
    }
    
    enum LoadMode
    {
        Assimp, 
        Txt, 
        Missing;
        
        private static /* synthetic */ LoadMode[] $values() {
            return new LoadMode[] { LoadMode.Assimp, LoadMode.Txt, LoadMode.Missing };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
