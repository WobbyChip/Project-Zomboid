// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;

public class ImportedSkeletonParams extends ProcessedAiSceneParams
{
    AiMesh mesh;
    
    ImportedSkeletonParams() {
        this.mesh = null;
    }
    
    public static ImportedSkeletonParams create(final ProcessedAiSceneParams processedAiSceneParams, final AiMesh mesh) {
        final ImportedSkeletonParams importedSkeletonParams = new ImportedSkeletonParams();
        importedSkeletonParams.set(processedAiSceneParams);
        importedSkeletonParams.mesh = mesh;
        return importedSkeletonParams;
    }
}
