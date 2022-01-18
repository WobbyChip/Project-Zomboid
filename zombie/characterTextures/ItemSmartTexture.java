// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characterTextures;

import zombie.core.textures.TextureCombinerCommand;
import zombie.core.textures.TextureCombinerShaderParam;
import zombie.util.StringUtils;
import zombie.core.textures.SmartTexture;

public final class ItemSmartTexture extends SmartTexture
{
    public static final int DecalOverlayCategory = 300;
    private String m_texName;
    
    public ItemSmartTexture(final String texName) {
        this.m_texName = null;
        if (texName == null) {
            return;
        }
        this.add(texName);
        this.m_texName = texName;
    }
    
    public ItemSmartTexture(final String texName, final float n) {
        this.m_texName = null;
        this.addHue(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, texName), 300, n);
        this.m_texName = texName;
    }
    
    public void setDenimPatches(final BloodBodyPartType bloodBodyPartType) {
        if (StringUtils.isNullOrEmpty(CharacterSmartTexture.DenimPatchesMaskFiles[bloodBodyPartType.index()])) {
            return;
        }
        this.addOverlayPatches(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.DenimPatchesMaskFiles[bloodBodyPartType.index()]), "media/textures/patches/patchesmask.png", CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
    }
    
    public void setLeatherPatches(final BloodBodyPartType bloodBodyPartType) {
        if (StringUtils.isNullOrEmpty(CharacterSmartTexture.LeatherPatchesMaskFiles[bloodBodyPartType.index()])) {
            return;
        }
        this.addOverlayPatches(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.LeatherPatchesMaskFiles[bloodBodyPartType.index()]), "media/textures/patches/patchesmask.png", CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
    }
    
    public void setBasicPatches(final BloodBodyPartType bloodBodyPartType) {
        if (StringUtils.isNullOrEmpty(CharacterSmartTexture.BasicPatchesMaskFiles[bloodBodyPartType.index()])) {
            return;
        }
        this.addOverlayPatches(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.BasicPatchesMaskFiles[bloodBodyPartType.index()]), "media/textures/patches/patchesmask.png", CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
    }
    
    public void setBlood(final String s, final BloodBodyPartType bloodBodyPartType, final float n) {
        this.setBlood(s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), n, CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
    }
    
    public void setBlood(final String s, final String s2, float max, final int n) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(n);
        if (firstFromCategory != null) {
            for (int i = 0; i < firstFromCategory.shaderParams.size(); ++i) {
                final TextureCombinerShaderParam textureCombinerShaderParam = firstFromCategory.shaderParams.get(i);
                if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != max || textureCombinerShaderParam.max != max)) {
                    final TextureCombinerShaderParam textureCombinerShaderParam2 = textureCombinerShaderParam;
                    final TextureCombinerShaderParam textureCombinerShaderParam3 = textureCombinerShaderParam;
                    final float n2 = max;
                    textureCombinerShaderParam3.max = n2;
                    textureCombinerShaderParam2.min = n2;
                    this.setDirty();
                }
            }
        }
        else if (max > 0.0f) {
            this.addOverlay(s, s2, max, n);
        }
    }
    
    public float addBlood(final String s, final BloodBodyPartType bloodBodyPartType, final float n) {
        return this.addBlood(s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), n, CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
    }
    
    public float addDirt(final String s, final BloodBodyPartType bloodBodyPartType, final float n) {
        return this.addDirt(s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), n, CharacterSmartTexture.DirtOverlayCategory + bloodBodyPartType.index());
    }
    
    public float addBlood(final String s, final String s2, final float n, final int n2) {
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(n2);
        if (firstFromCategory == null) {
            this.addOverlay(s, s2, n, n2);
            return n;
        }
        for (int i = 0; i < firstFromCategory.shaderParams.size(); ++i) {
            final TextureCombinerShaderParam textureCombinerShaderParam = firstFromCategory.shaderParams.get(i);
            if (textureCombinerShaderParam.name.equals("intensity")) {
                final float min = Math.min(1.0f, textureCombinerShaderParam.min + n);
                if (textureCombinerShaderParam.min != min || textureCombinerShaderParam.max != min) {
                    final TextureCombinerShaderParam textureCombinerShaderParam2 = textureCombinerShaderParam;
                    final TextureCombinerShaderParam textureCombinerShaderParam3 = textureCombinerShaderParam;
                    final float n3 = min;
                    textureCombinerShaderParam3.max = n3;
                    textureCombinerShaderParam2.min = n3;
                    this.setDirty();
                }
                return min;
            }
        }
        this.addOverlay(s, s2, n, n2);
        return n;
    }
    
    public float addDirt(final String s, final String s2, final float n, final int n2) {
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(n2);
        if (firstFromCategory == null) {
            this.addDirtOverlay(s, s2, n, n2);
            return n;
        }
        for (int i = 0; i < firstFromCategory.shaderParams.size(); ++i) {
            final TextureCombinerShaderParam textureCombinerShaderParam = firstFromCategory.shaderParams.get(i);
            if (textureCombinerShaderParam.name.equals("intensity")) {
                final float min = Math.min(1.0f, textureCombinerShaderParam.min + n);
                if (textureCombinerShaderParam.min != min || textureCombinerShaderParam.max != min) {
                    final TextureCombinerShaderParam textureCombinerShaderParam2 = textureCombinerShaderParam;
                    final TextureCombinerShaderParam textureCombinerShaderParam3 = textureCombinerShaderParam;
                    final float n3 = min;
                    textureCombinerShaderParam3.max = n3;
                    textureCombinerShaderParam2.min = n3;
                    this.setDirty();
                }
                return min;
            }
        }
        this.addOverlay(s, s2, n, n2);
        return n;
    }
    
    public void removeBlood() {
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            this.removeBlood(BloodBodyPartType.FromIndex(i));
        }
    }
    
    public void removeDirt() {
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            this.removeDirt(BloodBodyPartType.FromIndex(i));
        }
    }
    
    public void removeBlood(final BloodBodyPartType bloodBodyPartType) {
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
        if (firstFromCategory != null) {
            for (int i = 0; i < firstFromCategory.shaderParams.size(); ++i) {
                final TextureCombinerShaderParam textureCombinerShaderParam = firstFromCategory.shaderParams.get(i);
                if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != 0.0f || textureCombinerShaderParam.max != 0.0f)) {
                    final TextureCombinerShaderParam textureCombinerShaderParam2 = textureCombinerShaderParam;
                    final TextureCombinerShaderParam textureCombinerShaderParam3 = textureCombinerShaderParam;
                    final float n = 0.0f;
                    textureCombinerShaderParam3.max = n;
                    textureCombinerShaderParam2.min = n;
                    this.setDirty();
                }
            }
        }
    }
    
    public void removeDirt(final BloodBodyPartType bloodBodyPartType) {
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(CharacterSmartTexture.DirtOverlayCategory + bloodBodyPartType.index());
        if (firstFromCategory != null) {
            for (int i = 0; i < firstFromCategory.shaderParams.size(); ++i) {
                final TextureCombinerShaderParam textureCombinerShaderParam = firstFromCategory.shaderParams.get(i);
                if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != 0.0f || textureCombinerShaderParam.max != 0.0f)) {
                    final TextureCombinerShaderParam textureCombinerShaderParam2 = textureCombinerShaderParam;
                    final TextureCombinerShaderParam textureCombinerShaderParam3 = textureCombinerShaderParam;
                    final float n = 0.0f;
                    textureCombinerShaderParam3.max = n;
                    textureCombinerShaderParam2.min = n;
                    this.setDirty();
                }
            }
        }
    }
    
    public String getTexName() {
        return this.m_texName;
    }
}
