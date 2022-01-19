// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import java.util.Locale;
import zombie.scripting.objects.Item;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.ImmutableColor;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.ModelManager;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;

public class PopTemplateManager
{
    public static final PopTemplateManager instance;
    public final ArrayList<String> m_MaleSkins;
    public final ArrayList<String> m_FemaleSkins;
    public final ArrayList<String> m_MaleSkins_Zombie1;
    public final ArrayList<String> m_FemaleSkins_Zombie1;
    public final ArrayList<String> m_MaleSkins_Zombie2;
    public final ArrayList<String> m_FemaleSkins_Zombie2;
    public final ArrayList<String> m_MaleSkins_Zombie3;
    public final ArrayList<String> m_FemaleSkins_Zombie3;
    public final ArrayList<String> m_SkeletonMaleSkins_Zombie;
    public final ArrayList<String> m_SkeletonFemaleSkins_Zombie;
    public static final int SKELETON_BURNED_SKIN_INDEX = 0;
    public static final int SKELETON_NORMAL_SKIN_INDEX = 1;
    public static final int SKELETON_MUSCLE_SKIN_INDEX = 2;
    
    public PopTemplateManager() {
        this.m_MaleSkins = new ArrayList<String>();
        this.m_FemaleSkins = new ArrayList<String>();
        this.m_MaleSkins_Zombie1 = new ArrayList<String>();
        this.m_FemaleSkins_Zombie1 = new ArrayList<String>();
        this.m_MaleSkins_Zombie2 = new ArrayList<String>();
        this.m_FemaleSkins_Zombie2 = new ArrayList<String>();
        this.m_MaleSkins_Zombie3 = new ArrayList<String>();
        this.m_FemaleSkins_Zombie3 = new ArrayList<String>();
        this.m_SkeletonMaleSkins_Zombie = new ArrayList<String>();
        this.m_SkeletonFemaleSkins_Zombie = new ArrayList<String>();
    }
    
    public void init() {
        ItemManager.init();
        for (int i = 1; i <= 5; ++i) {
            this.m_MaleSkins.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
        }
        for (int j = 1; j <= 5; ++j) {
            this.m_FemaleSkins.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j));
        }
        for (int k = 1; k <= 4; ++k) {
            this.m_MaleSkins_Zombie1.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            this.m_FemaleSkins_Zombie1.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            this.m_MaleSkins_Zombie2.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            this.m_FemaleSkins_Zombie2.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            this.m_MaleSkins_Zombie3.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            this.m_FemaleSkins_Zombie3.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
        }
        this.m_SkeletonMaleSkins_Zombie.add("SkeletonBurned");
        this.m_SkeletonMaleSkins_Zombie.add("Skeleton");
        this.m_SkeletonMaleSkins_Zombie.add("SkeletonMuscle");
        this.m_SkeletonFemaleSkins_Zombie.add("SkeletonBurned");
        this.m_SkeletonFemaleSkins_Zombie.add("Skeleton");
        this.m_SkeletonFemaleSkins_Zombie.add("SkeletonMuscle");
    }
    
    public ModelInstance addClothingItem(final IsoGameCharacter isoGameCharacter, final ModelManager.ModelSlot modelSlot, final ItemVisual itemVisual, final ClothingItem clothingItem) {
        final String model = clothingItem.getModel(isoGameCharacter.isFemale());
        if (StringUtils.isNullOrWhitespace(model)) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clothingItem.m_Name));
            }
            return null;
        }
        final String processModelFileName = this.processModelFileName(model);
        final String textureChoice = itemVisual.getTextureChoice(clothingItem);
        final ImmutableColor tint = itemVisual.getTint(clothingItem);
        itemVisual.getHue(clothingItem);
        final String attachBone = clothingItem.m_AttachBone;
        final String shader = clothingItem.m_Shader;
        ModelInstance modelInstance;
        if (attachBone != null && attachBone.length() > 0) {
            modelInstance = ModelManager.instance.newStaticInstance(modelSlot, processModelFileName, textureChoice, attachBone, shader);
        }
        else {
            modelInstance = ModelManager.instance.newAdditionalModelInstance(processModelFileName, textureChoice, isoGameCharacter, modelSlot.model.AnimPlayer, shader);
        }
        if (modelInstance == null) {
            return null;
        }
        this.postProcessNewItemInstance(modelInstance, modelSlot, tint);
        modelInstance.setItemVisual(itemVisual);
        return modelInstance;
    }
    
    private void addHeadHairItem(final IsoGameCharacter isoGameCharacter, final ModelManager.ModelSlot modelSlot, String processModelFileName, final String s, final ImmutableColor immutableColor) {
        if (StringUtils.isNullOrWhitespace(processModelFileName)) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.warn((Object)"No model specified.");
            }
            return;
        }
        processModelFileName = this.processModelFileName(processModelFileName);
        final ModelInstance additionalModelInstance = ModelManager.instance.newAdditionalModelInstance(processModelFileName, s, isoGameCharacter, modelSlot.model.AnimPlayer, null);
        if (additionalModelInstance == null) {
            return;
        }
        this.postProcessNewItemInstance(additionalModelInstance, modelSlot, immutableColor);
    }
    
    private void addHeadHair(final IsoGameCharacter isoGameCharacter, final ModelManager.ModelSlot modelSlot, final HumanVisual humanVisual, final ItemVisual itemVisual, final boolean b) {
        ImmutableColor immutableColor = humanVisual.getHairColor();
        if (b) {
            immutableColor = humanVisual.getBeardColor();
        }
        if (isoGameCharacter.isFemale()) {
            if (!b) {
                HairStyle hairStyle = HairStyles.instance.FindFemaleStyle(humanVisual.getHairModel());
                if (hairStyle != null && itemVisual != null && itemVisual.getClothingItem() != null) {
                    hairStyle = HairStyles.instance.getAlternateForHat(hairStyle, itemVisual.getClothingItem().m_HatCategory);
                }
                if (hairStyle != null && hairStyle.isValid()) {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hairStyle.name));
                    }
                    this.addHeadHairItem(isoGameCharacter, modelSlot, hairStyle.model, hairStyle.texture, immutableColor);
                }
            }
        }
        else if (!b) {
            HairStyle hairStyle2 = HairStyles.instance.FindMaleStyle(humanVisual.getHairModel());
            if (hairStyle2 != null && itemVisual != null && itemVisual.getClothingItem() != null) {
                hairStyle2 = HairStyles.instance.getAlternateForHat(hairStyle2, itemVisual.getClothingItem().m_HatCategory);
            }
            if (hairStyle2 != null && hairStyle2.isValid()) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hairStyle2.name));
                }
                this.addHeadHairItem(isoGameCharacter, modelSlot, hairStyle2.model, hairStyle2.texture, immutableColor);
            }
        }
        else {
            final BeardStyle findStyle = BeardStyles.instance.FindStyle(humanVisual.getBeardModel());
            if (findStyle != null && findStyle.isValid()) {
                if (itemVisual != null && itemVisual.getClothingItem() != null && !StringUtils.isNullOrEmpty(itemVisual.getClothingItem().m_HatCategory) && itemVisual.getClothingItem().m_HatCategory.contains("nobeard")) {
                    return;
                }
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, findStyle.name));
                }
                this.addHeadHairItem(isoGameCharacter, modelSlot, findStyle.model, findStyle.texture, immutableColor);
            }
        }
    }
    
    public void populateCharacterModelSlot(final IsoGameCharacter isoGameCharacter, final ModelManager.ModelSlot modelSlot) {
        if (!(isoGameCharacter instanceof IHumanVisual)) {
            DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoGameCharacter;)Ljava/lang/String;, isoGameCharacter));
            return;
        }
        final HumanVisual humanVisual = ((IHumanVisual)isoGameCharacter).getHumanVisual();
        final ItemVisuals itemVisuals = new ItemVisuals();
        isoGameCharacter.getItemVisuals(itemVisuals);
        final CharacterMask getMask = HumanVisual.GetMask(itemVisuals);
        if (DebugLog.isEnabled(DebugType.Clothing)) {
            DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoGameCharacter.getClass().getName(), isoGameCharacter.getName()));
        }
        if (getMask.isPartVisible(CharacterMask.Part.Head)) {
            this.addHeadHair(isoGameCharacter, modelSlot, humanVisual, itemVisuals.findHat(), false);
            this.addHeadHair(isoGameCharacter, modelSlot, humanVisual, itemVisuals.findMask(), true);
        }
        for (int i = itemVisuals.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual = itemVisuals.get(i);
            final ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem == null) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual));
                }
            }
            else if (!this.isItemModelHidden(isoGameCharacter.getBodyLocationGroup(), itemVisuals, itemVisual)) {
                this.addClothingItem(isoGameCharacter, modelSlot, itemVisual, clothingItem);
            }
        }
        for (int j = humanVisual.getBodyVisuals().size() - 1; j >= 0; --j) {
            final ItemVisual itemVisual2 = humanVisual.getBodyVisuals().get(j);
            final ClothingItem clothingItem2 = itemVisual2.getClothingItem();
            if (clothingItem2 == null) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual2));
                }
            }
            else {
                this.addClothingItem(isoGameCharacter, modelSlot, itemVisual2, clothingItem2);
            }
        }
        isoGameCharacter.postUpdateModelTextures();
        isoGameCharacter.updateSpeedModifiers();
    }
    
    public boolean isItemModelHidden(final BodyLocationGroup bodyLocationGroup, final ItemVisuals itemVisuals, final ItemVisual itemVisual) {
        final Item scriptItem = itemVisual.getScriptItem();
        if (scriptItem == null || bodyLocationGroup.getLocation(scriptItem.getBodyLocation()) == null) {
            return false;
        }
        for (int i = 0; i < itemVisuals.size(); ++i) {
            if (itemVisuals.get(i) != itemVisual) {
                final Item scriptItem2 = itemVisuals.get(i).getScriptItem();
                if (scriptItem2 != null) {
                    if (bodyLocationGroup.getLocation(scriptItem2.getBodyLocation()) != null) {
                        if (bodyLocationGroup.isHideModel(scriptItem2.getBodyLocation(), scriptItem.getBodyLocation())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private String processModelFileName(String s) {
        s = s.replaceAll("\\\\", "/");
        s = s.toLowerCase(Locale.ENGLISH);
        return s;
    }
    
    private void postProcessNewItemInstance(final ModelInstance modelInstance, final ModelManager.ModelSlot owner, final ImmutableColor immutableColor) {
        modelInstance.depthBias = 0.0f;
        modelInstance.matrixModel = owner.model;
        modelInstance.tintR = immutableColor.r;
        modelInstance.tintG = immutableColor.g;
        modelInstance.tintB = immutableColor.b;
        modelInstance.parent = owner.model;
        modelInstance.AnimPlayer = owner.model.AnimPlayer;
        owner.model.sub.add(0, modelInstance);
        owner.sub.add(0, modelInstance);
        modelInstance.setOwner(owner);
    }
    
    static {
        instance = new PopTemplateManager();
    }
}
