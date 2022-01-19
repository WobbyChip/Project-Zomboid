// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characterTextures;

import org.lwjgl.opengl.GL11;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.TextureCombinerCommand;
import zombie.core.textures.TextureCombinerShaderParam;
import zombie.core.textures.SmartTexture;

public final class CharacterSmartTexture extends SmartTexture
{
    public static int BodyCategory;
    public static int ClothingBottomCategory;
    public static int ClothingTopCategory;
    public static int ClothingItemCategory;
    public static int DecalOverlayCategory;
    public static int DirtOverlayCategory;
    public static final String[] MaskFiles;
    public static final String[] BasicPatchesMaskFiles;
    public static final String[] DenimPatchesMaskFiles;
    public static final String[] LeatherPatchesMaskFiles;
    
    public void setBlood(final BloodBodyPartType bloodBodyPartType, float max) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        final int n = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
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
            this.addOverlay("media/textures/BloodTextures/BloodOverlay.png", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), max, n);
        }
    }
    
    public void setDirt(final BloodBodyPartType bloodBodyPartType, float max) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        final int n = CharacterSmartTexture.DirtOverlayCategory + bloodBodyPartType.index();
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
            this.addDirtOverlay("media/textures/BloodTextures/GrimeOverlay.png", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), max, n);
        }
    }
    
    public void removeBlood() {
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            this.removeBlood(BloodBodyPartType.FromIndex(i));
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
    
    public float addBlood(final BloodBodyPartType bloodBodyPartType, final float n, final IsoGameCharacter isoGameCharacter) {
        final int n2 = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(n2);
        if (bloodBodyPartType == BloodBodyPartType.Head && isoGameCharacter != null) {
            if (isoGameCharacter.hair != null) {
                final ModelInstance hair = isoGameCharacter.hair;
                hair.tintR -= 0.022f;
                if (isoGameCharacter.hair.tintR < 0.0f) {
                    isoGameCharacter.hair.tintR = 0.0f;
                }
                final ModelInstance hair2 = isoGameCharacter.hair;
                hair2.tintG -= 0.03f;
                if (isoGameCharacter.hair.tintG < 0.0f) {
                    isoGameCharacter.hair.tintG = 0.0f;
                }
                final ModelInstance hair3 = isoGameCharacter.hair;
                hair3.tintB -= 0.03f;
                if (isoGameCharacter.hair.tintB < 0.0f) {
                    isoGameCharacter.hair.tintB = 0.0f;
                }
            }
            if (isoGameCharacter.beard != null) {
                final ModelInstance beard = isoGameCharacter.beard;
                beard.tintR -= 0.022f;
                if (isoGameCharacter.beard.tintR < 0.0f) {
                    isoGameCharacter.beard.tintR = 0.0f;
                }
                final ModelInstance beard2 = isoGameCharacter.beard;
                beard2.tintG -= 0.03f;
                if (isoGameCharacter.beard.tintG < 0.0f) {
                    isoGameCharacter.beard.tintG = 0.0f;
                }
                final ModelInstance beard3 = isoGameCharacter.beard;
                beard3.tintB -= 0.03f;
                if (isoGameCharacter.beard.tintB < 0.0f) {
                    isoGameCharacter.beard.tintB = 0.0f;
                }
            }
        }
        if (firstFromCategory != null) {
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
        }
        else {
            this.addOverlay("media/textures/BloodTextures/BloodOverlay.png", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), n, n2);
        }
        return n;
    }
    
    public float addDirt(final BloodBodyPartType bloodBodyPartType, final float n, final IsoGameCharacter isoGameCharacter) {
        final int n2 = CharacterSmartTexture.DirtOverlayCategory + bloodBodyPartType.index();
        final TextureCombinerCommand firstFromCategory = this.getFirstFromCategory(n2);
        if (bloodBodyPartType == BloodBodyPartType.Head && isoGameCharacter != null) {
            if (isoGameCharacter.hair != null) {
                final ModelInstance hair = isoGameCharacter.hair;
                hair.tintR -= 0.022f;
                if (isoGameCharacter.hair.tintR < 0.0f) {
                    isoGameCharacter.hair.tintR = 0.0f;
                }
                final ModelInstance hair2 = isoGameCharacter.hair;
                hair2.tintG -= 0.03f;
                if (isoGameCharacter.hair.tintG < 0.0f) {
                    isoGameCharacter.hair.tintG = 0.0f;
                }
                final ModelInstance hair3 = isoGameCharacter.hair;
                hair3.tintB -= 0.03f;
                if (isoGameCharacter.hair.tintB < 0.0f) {
                    isoGameCharacter.hair.tintB = 0.0f;
                }
            }
            if (isoGameCharacter.beard != null) {
                final ModelInstance beard = isoGameCharacter.beard;
                beard.tintR -= 0.022f;
                if (isoGameCharacter.beard.tintR < 0.0f) {
                    isoGameCharacter.beard.tintR = 0.0f;
                }
                final ModelInstance beard2 = isoGameCharacter.beard;
                beard2.tintG -= 0.03f;
                if (isoGameCharacter.beard.tintG < 0.0f) {
                    isoGameCharacter.beard.tintG = 0.0f;
                }
                final ModelInstance beard3 = isoGameCharacter.beard;
                beard3.tintB -= 0.03f;
                if (isoGameCharacter.beard.tintB < 0.0f) {
                    isoGameCharacter.beard.tintB = 0.0f;
                }
            }
        }
        if (firstFromCategory != null) {
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
        }
        else {
            this.addDirtOverlay("media/textures/BloodTextures/GrimeOverlay.png", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]), n, n2);
        }
        return n;
    }
    
    public void addShirtDecal(final String s) {
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        this.addRect(s, 102, 118, 52, 52);
    }
    
    static {
        CharacterSmartTexture.BodyCategory = 0;
        CharacterSmartTexture.ClothingBottomCategory = 1;
        CharacterSmartTexture.ClothingTopCategory = 2;
        CharacterSmartTexture.ClothingItemCategory = 3;
        CharacterSmartTexture.DecalOverlayCategory = 300;
        CharacterSmartTexture.DirtOverlayCategory = 400;
        MaskFiles = new String[] { "BloodMaskHandL", "BloodMaskHandR", "BloodMaskLArmL", "BloodMaskLArmR", "BloodMaskUArmL", "BloodMaskUArmR", "BloodMaskChest", "BloodMaskStomach", "BloodMaskHead", "BloodMaskNeck", "BloodMaskGroin", "BloodMaskULegL", "BloodMaskULegR", "BloodMaskLLegL", "BloodMaskLLegR", "BloodMaskFootL", "BloodMaskFootR", "BloodMaskBack" };
        BasicPatchesMaskFiles = new String[] { "patches_left_hand_sheet", "patches_right_hand_sheet", "patches_left_lower_arm_sheet", "patches_right_lower_arm_sheet", "patches_left_upper_arm_sheet", "patches_right_upper_arm_sheet", "patches_chest_sheet", "patches_abdomen_sheet", "", "", "patches_groin_sheet", "patches_left_upper_leg_sheet", "patches_right_upper_leg_sheet", "patches_left_lower_leg_sheet", "patches_right_lower_leg_sheet", "", "", "patches_back_sheet" };
        DenimPatchesMaskFiles = new String[] { "patches_left_hand_denim", "patches_right_hand_denim", "patches_left_lower_arm_denim", "patches_right_lower_arm_denim", "patches_left_upper_arm_denim", "patches_right_upper_arm_denim", "patches_chest_denim", "patches_abdomen_denim", "", "", "patches_groin_denim", "patches_left_upper_leg_denim", "patches_right_upper_leg_denim", "patches_left_lower_leg_denim", "patches_right_lower_leg_denim", "", "", "patches_back_denim" };
        LeatherPatchesMaskFiles = new String[] { "patches_left_hand_leather", "patches_right_hand_leather", "patches_left_lower_arm_leather", "patches_right_lower_arm_leather", "patches_left_upper_arm_leather", "patches_right_upper_arm_leather", "patches_chest_leather", "patches_abdomen_leather", "", "", "patches_groin_leather", "patches_left_upper_leg_leather", "patches_right_upper_leg_leather", "patches_left_lower_leg_leather", "patches_right_lower_leg_leather", "", "", "patches_back_leather" };
    }
}
