// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.asset.AssetPath;
import java.util.ArrayList;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.asset.AssetTask;
import zombie.fileSystem.FileTask;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.FileTask_ParseXML;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public class ClothingItemAssetManager extends AssetManager
{
    public static final ClothingItemAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
        final AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(new FileTask_ParseXML(ClothingItemXML.class, asset.getPath().getPath(), o -> this.onFileTaskFinished((ClothingItem)asset, o), asset.getAssetManager().getOwner().getFileSystem()), asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }
    
    private void onFileTaskFinished(final ClothingItem clothingItem, final Object o) {
        if (o instanceof ClothingItemXML) {
            final ClothingItemXML clothingItemXML = (ClothingItemXML)o;
            clothingItem.m_MaleModel = this.fixPath(clothingItemXML.m_MaleModel);
            clothingItem.m_FemaleModel = this.fixPath(clothingItemXML.m_FemaleModel);
            clothingItem.m_Static = clothingItemXML.m_Static;
            PZArrayUtil.arrayCopy(clothingItem.m_BaseTextures, (List<?>)this.fixPaths(clothingItemXML.m_BaseTextures));
            clothingItem.m_AttachBone = clothingItemXML.m_AttachBone;
            PZArrayUtil.arrayCopy(clothingItem.m_Masks, (List<?>)clothingItemXML.m_Masks);
            clothingItem.m_MasksFolder = this.fixPath(clothingItemXML.m_MasksFolder);
            clothingItem.m_UnderlayMasksFolder = this.fixPath(clothingItemXML.m_UnderlayMasksFolder);
            PZArrayUtil.arrayCopy(clothingItem.textureChoices, (List<?>)this.fixPaths(clothingItemXML.textureChoices));
            clothingItem.m_AllowRandomHue = clothingItemXML.m_AllowRandomHue;
            clothingItem.m_AllowRandomTint = clothingItemXML.m_AllowRandomTint;
            clothingItem.m_DecalGroup = clothingItemXML.m_DecalGroup;
            clothingItem.m_Shader = clothingItemXML.m_Shader;
            clothingItem.m_HatCategory = clothingItemXML.m_HatCategory;
            this.onLoadingSucceeded(clothingItem);
        }
        else {
            this.onLoadingFailed(clothingItem);
        }
    }
    
    private String fixPath(final String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("\\\\", "/");
    }
    
    private ArrayList<String> fixPaths(final ArrayList<String> list) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, this.fixPath(list.get(i)));
        }
        return list;
    }
    
    @Override
    public void onStateChanged(final Asset.State state, final Asset.State state2, final Asset asset) {
        super.onStateChanged(state, state2, asset);
        if (state2 == Asset.State.READY) {
            OutfitManager.instance.onClothingItemStateChanged((ClothingItem)asset);
        }
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new ClothingItem(assetPath, this);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new ClothingItemAssetManager();
    }
}
