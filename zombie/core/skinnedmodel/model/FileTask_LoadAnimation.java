// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.util.vector.Quaternion;
import java.io.IOException;
import jassimp.AiScene;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiSceneParams;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import java.util.Set;
import jassimp.Jassimp;
import java.util.EnumSet;
import jassimp.AiPostProcessSteps;
import zombie.debug.DebugLog;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileSystem;

public class FileTask_LoadAnimation extends FileTask_AbstractLoadModel
{
    private AnimationAsset m_anim;
    
    public FileTask_LoadAnimation(final AnimationAsset anim, final FileSystem fileSystem, final IFileTaskCallback fileTaskCallback) {
        super(fileSystem, fileTaskCallback, "media/anims", "media/anims_x");
        this.m_anim = anim;
    }
    
    @Override
    public String getRawFileName() {
        return this.m_anim.getPath().getPath();
    }
    
    @Override
    public String getErrorMessage() {
        return this.m_fileName;
    }
    
    @Override
    public void done() {
    }
    
    @Override
    public ProcessedAiScene loadX() throws IOException {
        DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
        final AiScene importFile = Jassimp.importFile(this.m_fileName, (Set)EnumSet.of(AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS));
        final JAssImpImporter.LoadMode animationOnly = JAssImpImporter.LoadMode.AnimationOnly;
        final ModelMesh animationsMesh = this.m_anim.assetParams.animationsMesh;
        final SkinningData skinnedTo = (animationsMesh == null) ? null : animationsMesh.skinningData;
        final ProcessedAiSceneParams create = ProcessedAiSceneParams.create();
        create.scene = importFile;
        create.mode = animationOnly;
        create.skinnedTo = skinnedTo;
        final ProcessedAiScene process = ProcessedAiScene.process(create);
        JAssImpImporter.takeOutTheTrash(importFile);
        return process;
    }
    
    @Override
    public ProcessedAiScene loadFBX() throws IOException {
        DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
        final AiScene importFile = Jassimp.importFile(this.m_fileName, (Set)EnumSet.of(AiPostProcessSteps.MAKE_LEFT_HANDED, AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS));
        final JAssImpImporter.LoadMode animationOnly = JAssImpImporter.LoadMode.AnimationOnly;
        final ModelMesh animationsMesh = this.m_anim.assetParams.animationsMesh;
        final SkinningData skinnedTo = (animationsMesh == null) ? null : animationsMesh.skinningData;
        final Quaternion animBonesRotateModifier = new Quaternion();
        animBonesRotateModifier.setFromAxisAngle(new Vector4f(1.0f, 0.0f, 0.0f, -1.5707964f));
        final ProcessedAiSceneParams create = ProcessedAiSceneParams.create();
        create.scene = importFile;
        create.mode = animationOnly;
        create.skinnedTo = skinnedTo;
        create.animBonesScaleModifier = 0.01f;
        create.animBonesRotateModifier = animBonesRotateModifier;
        final ProcessedAiScene process = ProcessedAiScene.process(create);
        JAssImpImporter.takeOutTheTrash(importFile);
        return process;
    }
    
    @Override
    public ModelTxt loadTxt() throws IOException {
        final boolean b = false;
        final boolean b2 = false;
        final ModelMesh animationsMesh = this.m_anim.assetParams.animationsMesh;
        return ModelLoader.instance.loadTxt(this.m_fileName, b, b2, (animationsMesh == null) ? null : animationsMesh.skinningData);
    }
}
