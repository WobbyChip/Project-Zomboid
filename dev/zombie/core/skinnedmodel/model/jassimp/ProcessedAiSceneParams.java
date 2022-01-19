// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import org.lwjgl.util.vector.Quaternion;
import zombie.core.skinnedmodel.model.SkinningData;
import jassimp.AiScene;

public class ProcessedAiSceneParams
{
    public AiScene scene;
    public JAssImpImporter.LoadMode mode;
    public SkinningData skinnedTo;
    public String meshName;
    public float animBonesScaleModifier;
    public Quaternion animBonesRotateModifier;
    
    ProcessedAiSceneParams() {
        this.scene = null;
        this.mode = JAssImpImporter.LoadMode.Normal;
        this.skinnedTo = null;
        this.meshName = null;
        this.animBonesScaleModifier = 1.0f;
        this.animBonesRotateModifier = null;
    }
    
    public static ProcessedAiSceneParams create() {
        return new ProcessedAiSceneParams();
    }
    
    protected void set(final ProcessedAiSceneParams processedAiSceneParams) {
        this.scene = processedAiSceneParams.scene;
        this.mode = processedAiSceneParams.mode;
        this.skinnedTo = processedAiSceneParams.skinnedTo;
        this.meshName = processedAiSceneParams.meshName;
        this.animBonesScaleModifier = processedAiSceneParams.animBonesScaleModifier;
        this.animBonesRotateModifier = processedAiSceneParams.animBonesRotateModifier;
    }
}
