// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import org.joml.Matrix4f;
import zombie.asset.Asset;

public final class ModelMesh extends Asset
{
    public VertexBufferObject vb;
    public SkinningData skinningData;
    public SoftwareModelMesh softwareMesh;
    public MeshAssetParams assetParams;
    public Matrix4f m_transform;
    protected boolean bStatic;
    public ModelMesh m_animationsMesh;
    public String m_fullPath;
    public static final AssetType ASSET_TYPE;
    
    public ModelMesh(final AssetPath assetPath, final AssetManager assetManager, final MeshAssetParams assetParams) {
        super(assetPath, assetManager);
        this.assetParams = assetParams;
        this.bStatic = (this.assetParams != null && this.assetParams.bStatic);
        this.m_animationsMesh = ((this.assetParams == null) ? null : this.assetParams.animationsMesh);
    }
    
    protected void onLoadedX(final ProcessedAiScene processedAiScene) {
        processedAiScene.applyToMesh(this, this.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal, false, (this.assetParams.animationsMesh == null) ? null : this.assetParams.animationsMesh.skinningData);
    }
    
    protected void onLoadedTxt(final ModelTxt modelTxt) {
        ModelLoader.instance.applyToMesh(modelTxt, this, (this.assetParams.animationsMesh == null) ? null : this.assetParams.animationsMesh.skinningData);
    }
    
    public void SetVertexBuffer(final VertexBufferObject vb) {
        this.clear();
        this.vb = vb;
        this.bStatic = (vb == null || vb.bStatic);
    }
    
    public void Draw(final Shader shader) {
        if (this.vb != null) {
            this.vb.Draw(shader);
        }
    }
    
    public void onBeforeReady() {
        super.onBeforeReady();
        if (this.assetParams != null) {
            this.assetParams.animationsMesh = null;
            this.assetParams = null;
        }
    }
    
    @Override
    public void setAssetParams(final AssetManager.AssetParams assetParams) {
        this.assetParams = (MeshAssetParams)assetParams;
    }
    
    @Override
    public AssetType getType() {
        return ModelMesh.ASSET_TYPE;
    }
    
    public void clear() {
        if (this.vb == null) {
            return;
        }
        this.vb.clear();
        this.vb = null;
    }
    
    static {
        ASSET_TYPE = new AssetType("Mesh");
    }
    
    public static final class MeshAssetParams extends AssetManager.AssetParams
    {
        public boolean bStatic;
        public ModelMesh animationsMesh;
    }
}
