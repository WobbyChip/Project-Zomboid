// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.util.StringUtils;
import java.util.List;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import java.util.ArrayList;
import zombie.asset.Asset;

public final class ClothingItem extends Asset
{
    public String m_GUID;
    public String m_MaleModel;
    public String m_FemaleModel;
    public boolean m_Static;
    public ArrayList<String> m_BaseTextures;
    public String m_AttachBone;
    public ArrayList<Integer> m_Masks;
    public String m_MasksFolder;
    public String m_UnderlayMasksFolder;
    public ArrayList<String> textureChoices;
    public boolean m_AllowRandomHue;
    public boolean m_AllowRandomTint;
    public String m_DecalGroup;
    public String m_Shader;
    public String m_HatCategory;
    public static final String s_masksFolderDefault = "media/textures/Body/Masks";
    public String m_Name;
    public static final AssetType ASSET_TYPE;
    
    public ClothingItem(final AssetPath assetPath, final AssetManager assetManager) {
        super(assetPath, assetManager);
        this.m_Static = false;
        this.m_BaseTextures = new ArrayList<String>();
        this.m_Masks = new ArrayList<Integer>();
        this.m_MasksFolder = "media/textures/Body/Masks";
        this.m_UnderlayMasksFolder = "media/textures/Body/Masks";
        this.textureChoices = new ArrayList<String>();
        this.m_AllowRandomHue = false;
        this.m_AllowRandomTint = false;
        this.m_DecalGroup = null;
        this.m_Shader = null;
        this.m_HatCategory = null;
    }
    
    public ArrayList<String> getBaseTextures() {
        return this.m_BaseTextures;
    }
    
    public ArrayList<String> getTextureChoices() {
        return this.textureChoices;
    }
    
    public String GetATexture() {
        if (this.textureChoices.size() == 0) {
            return null;
        }
        return OutfitRNG.pickRandom(this.textureChoices);
    }
    
    public boolean getAllowRandomHue() {
        return this.m_AllowRandomHue;
    }
    
    public boolean getAllowRandomTint() {
        return this.m_AllowRandomTint;
    }
    
    public String getDecalGroup() {
        return this.m_DecalGroup;
    }
    
    public boolean isHat() {
        return !StringUtils.isNullOrWhitespace(this.m_HatCategory) && !"nobeard".equals(this.m_HatCategory);
    }
    
    public boolean isMask() {
        return !StringUtils.isNullOrWhitespace(this.m_HatCategory) && !this.m_HatCategory.contains("hair");
    }
    
    public void getCombinedMask(final CharacterMask characterMask) {
        characterMask.setPartsVisible(this.m_Masks, false);
    }
    
    public boolean hasModel() {
        return !StringUtils.isNullOrWhitespace(this.m_MaleModel) && !StringUtils.isNullOrWhitespace(this.m_FemaleModel);
    }
    
    public String getModel(final boolean b) {
        return b ? this.m_FemaleModel : this.m_MaleModel;
    }
    
    public String getFemaleModel() {
        return this.m_FemaleModel;
    }
    
    public String getMaleModel() {
        return this.m_MaleModel;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.m_Name, this.m_GUID);
    }
    
    public static void tryGetCombinedMask(final ClothingItemReference clothingItemReference, final CharacterMask characterMask) {
        tryGetCombinedMask(clothingItemReference.getClothingItem(), characterMask);
    }
    
    public static void tryGetCombinedMask(final ClothingItem clothingItem, final CharacterMask characterMask) {
        if (clothingItem != null) {
            clothingItem.getCombinedMask(characterMask);
        }
    }
    
    @Override
    public AssetType getType() {
        return ClothingItem.ASSET_TYPE;
    }
    
    static {
        ASSET_TYPE = new AssetType("ClothingItem");
    }
}
