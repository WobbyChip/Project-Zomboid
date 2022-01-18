// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.textures.TextureCombiner;
import zombie.core.textures.SmartTexture;
import org.lwjgl.opengl.GL11;
import zombie.core.skinnedmodel.population.ClothingDecal;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.Core;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.characters.WornItems.BodyLocations;
import java.util.List;
import java.util.Arrays;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.characters.IsoGameCharacter;
import zombie.characterTextures.BloodBodyPartType;
import zombie.popman.ObjectPool;
import zombie.core.textures.Texture;
import zombie.characterTextures.ItemSmartTexture;
import zombie.characterTextures.CharacterSmartTexture;
import java.util.ArrayList;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.TextureDraw;

public final class ModelInstanceTextureCreator extends TextureDraw.GenericDrawer
{
    private boolean bZombie;
    public int renderRefCount;
    private final CharacterMask mask;
    private final boolean[] holeMask;
    private final ItemVisuals itemVisuals;
    private final CharacterData chrData;
    private final ArrayList<ItemData> itemData;
    private final CharacterSmartTexture characterSmartTexture;
    private final ItemSmartTexture itemSmartTexture;
    private final ArrayList<Texture> tempTextures;
    private boolean bRendered;
    private final ArrayList<Texture> texturesNotReady;
    public int testNotReady;
    private static final ObjectPool<ModelInstanceTextureCreator> pool;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public ModelInstanceTextureCreator() {
        this.mask = new CharacterMask();
        this.holeMask = new boolean[BloodBodyPartType.MAX.index()];
        this.itemVisuals = new ItemVisuals();
        this.chrData = new CharacterData();
        this.itemData = new ArrayList<ItemData>();
        this.characterSmartTexture = new CharacterSmartTexture();
        this.itemSmartTexture = new ItemSmartTexture((String)null);
        this.tempTextures = new ArrayList<Texture>();
        this.bRendered = false;
        this.texturesNotReady = new ArrayList<Texture>();
        this.testNotReady = -1;
    }
    
    public void init(final IsoGameCharacter isoGameCharacter) {
        final ModelManager.ModelSlot modelSlot = isoGameCharacter.legsSprite.modelSlot;
        final HumanVisual humanVisual = ((IHumanVisual)isoGameCharacter).getHumanVisual();
        isoGameCharacter.getItemVisuals(this.itemVisuals);
        this.init(humanVisual, this.itemVisuals, modelSlot.model);
        this.itemVisuals.clear();
    }
    
    public void init(final HumanVisual humanVisual, final ItemVisuals itemVisuals, final ModelInstance modelInstance) {
        final boolean enabled = DebugLog.isEnabled(DebugType.Clothing);
        this.bRendered = false;
        this.bZombie = humanVisual.isZombie();
        final CharacterMask mask = this.mask;
        mask.setAllVisible(true);
        String underlayMasksFolder = "media/textures/Body/Masks";
        Arrays.fill(this.holeMask, false);
        ItemData.pool.release(this.itemData);
        this.itemData.clear();
        this.texturesNotReady.clear();
        final BodyLocationGroup group = BodyLocations.getGroup("Human");
        for (int i = itemVisuals.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual = itemVisuals.get(i);
            final ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem == null) {
                if (enabled) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual));
                }
            }
            else if (!clothingItem.isReady()) {
                if (enabled) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual));
                }
            }
            else if (!PopTemplateManager.instance.isItemModelHidden(group, itemVisuals, itemVisual)) {
                final ModelInstance modelInstance2 = this.findModelInstance(modelInstance.sub, itemVisual);
                if (modelInstance2 == null && !StringUtils.isNullOrWhitespace(clothingItem.getModel(humanVisual.isFemale()))) {
                    if (enabled) {
                        DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual));
                    }
                }
                else {
                    this.addClothingItem(modelInstance2, itemVisual, clothingItem, mask, underlayMasksFolder);
                    for (int j = 0; j < BloodBodyPartType.MAX.index(); ++j) {
                        final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(j);
                        if (itemVisual.getHole(fromIndex) > 0.0f && mask.isBloodBodyPartVisible(fromIndex)) {
                            this.holeMask[j] = true;
                        }
                    }
                    for (int k = 0; k < clothingItem.m_Masks.size(); ++k) {
                        for (final BloodBodyPartType bloodBodyPartType : CharacterMask.Part.fromInt(clothingItem.m_Masks.get(k)).getBloodBodyPartTypes()) {
                            if (itemVisual.getHole(bloodBodyPartType) <= 0.0f) {
                                this.holeMask[bloodBodyPartType.index()] = false;
                            }
                        }
                    }
                    itemVisual.getClothingItemCombinedMask(mask);
                    if (!StringUtils.equalsIgnoreCase(clothingItem.m_UnderlayMasksFolder, "media/textures/Body/Masks")) {
                        underlayMasksFolder = clothingItem.m_UnderlayMasksFolder;
                    }
                }
            }
        }
        this.chrData.modelInstance = modelInstance;
        this.chrData.mask.copyFrom(mask);
        this.chrData.maskFolder = "media/textures/Body/Masks";
        this.chrData.baseTexture = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, humanVisual.getSkinTexture());
        Arrays.fill(this.chrData.blood, 0.0f);
        for (int n = 0; n < BloodBodyPartType.MAX.index(); ++n) {
            final BloodBodyPartType fromIndex2 = BloodBodyPartType.FromIndex(n);
            this.chrData.blood[n] = humanVisual.getBlood(fromIndex2);
            this.chrData.dirt[n] = humanVisual.getDirt(fromIndex2);
        }
        final Texture sharedTexture = Texture.getSharedTexture(this.chrData.baseTexture);
        if (sharedTexture != null && !sharedTexture.isReady()) {
            this.texturesNotReady.add(sharedTexture);
        }
        if (!this.chrData.mask.isAllVisible() && !this.chrData.mask.isNothingVisible()) {
            final Texture e;
            this.chrData.mask.forEachVisible(Lambda.consumer(this.chrData.maskFolder, this.texturesNotReady, (part, s, list) -> {
                Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/skinnedmodel/model/CharacterMask$Part;)Ljava/lang/String;, s, part));
                if (e != null && !e.isReady()) {
                    list.add(e);
                }
                return;
            }));
        }
        final Texture sharedTexture2 = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlay.png");
        if (sharedTexture2 != null && !sharedTexture2.isReady()) {
            this.texturesNotReady.add(sharedTexture2);
        }
        final Texture sharedTexture3 = Texture.getSharedTexture("media/textures/BloodTextures/GrimeOverlay.png");
        if (sharedTexture3 != null && !sharedTexture3.isReady()) {
            this.texturesNotReady.add(sharedTexture3);
        }
        final Texture sharedTexture4 = Texture.getSharedTexture("media/textures/patches/patchesmask.png");
        if (sharedTexture4 != null && !sharedTexture4.isReady()) {
            this.texturesNotReady.add(sharedTexture4);
        }
        for (int n2 = 0; n2 < BloodBodyPartType.MAX.index(); ++n2) {
            final BloodBodyPartType fromIndex3 = BloodBodyPartType.FromIndex(n2);
            final Texture sharedTexture5 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[fromIndex3.index()]));
            if (sharedTexture5 != null && !sharedTexture5.isReady()) {
                this.texturesNotReady.add(sharedTexture5);
            }
            final Texture sharedTexture6 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[fromIndex3.index()]));
            if (sharedTexture6 != null && !sharedTexture6.isReady()) {
                this.texturesNotReady.add(sharedTexture6);
            }
            final Texture sharedTexture7 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.BasicPatchesMaskFiles[fromIndex3.index()]));
            if (sharedTexture7 != null && !sharedTexture7.isReady()) {
                this.texturesNotReady.add(sharedTexture7);
            }
            final Texture sharedTexture8 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.DenimPatchesMaskFiles[fromIndex3.index()]));
            if (sharedTexture8 != null && !sharedTexture8.isReady()) {
                this.texturesNotReady.add(sharedTexture8);
            }
            final Texture sharedTexture9 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.LeatherPatchesMaskFiles[fromIndex3.index()]));
            if (sharedTexture9 != null && !sharedTexture9.isReady()) {
                this.texturesNotReady.add(sharedTexture9);
            }
        }
        mask.setAllVisible(true);
        final String s2 = "media/textures/Body/Masks";
        for (int index = humanVisual.getBodyVisuals().size() - 1; index >= 0; --index) {
            final ItemVisual itemVisual2 = humanVisual.getBodyVisuals().get(index);
            final ClothingItem clothingItem2 = itemVisual2.getClothingItem();
            if (clothingItem2 == null) {
                if (enabled) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual2));
                }
            }
            else if (!clothingItem2.isReady()) {
                if (enabled) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual2));
                }
            }
            else {
                final ModelInstance modelInstance3 = this.findModelInstance(modelInstance.sub, itemVisual2);
                if (modelInstance3 == null && !StringUtils.isNullOrWhitespace(clothingItem2.getModel(humanVisual.isFemale()))) {
                    if (enabled) {
                        DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/visual/ItemVisual;)Ljava/lang/String;, itemVisual2));
                    }
                }
                else {
                    this.addClothingItem(modelInstance3, itemVisual2, clothingItem2, mask, s2);
                }
            }
        }
    }
    
    private ModelInstance findModelInstance(final ArrayList<ModelInstance> list, final ItemVisual itemVisual) {
        for (int i = 0; i < list.size(); ++i) {
            final ModelInstance modelInstance = list.get(i);
            final ItemVisual itemVisual2 = modelInstance.getItemVisual();
            if (itemVisual2 != null && itemVisual2.getClothingItem() == itemVisual.getClothingItem()) {
                return modelInstance;
            }
        }
        return null;
    }
    
    private void addClothingItem(final ModelInstance modelInstance, final ItemVisual itemVisual, final ClothingItem clothingItem, final CharacterMask characterMask, final String maskFolder) {
        final String s2 = (modelInstance == null) ? itemVisual.getBaseTexture(clothingItem) : itemVisual.getTextureChoice(clothingItem);
        final ImmutableColor tint = itemVisual.getTint(clothingItem);
        final float hue = itemVisual.getHue(clothingItem);
        final ItemData itemData = ItemData.pool.alloc();
        itemData.modelInstance = modelInstance;
        itemData.category = CharacterSmartTexture.ClothingItemCategory;
        itemData.mask.copyFrom(characterMask);
        itemData.maskFolder = clothingItem.m_MasksFolder;
        if (StringUtils.equalsIgnoreCase(itemData.maskFolder, "media/textures/Body/Masks")) {
            itemData.maskFolder = maskFolder;
        }
        if (StringUtils.equalsIgnoreCase(itemData.maskFolder, "none")) {
            itemData.mask.setAllVisible(true);
        }
        if (itemData.maskFolder.contains("Clothes/Hat/Masks")) {
            itemData.mask.setAllVisible(true);
        }
        itemData.baseTexture = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
        itemData.tint = tint;
        itemData.hue = hue;
        itemData.decalTexture = null;
        Arrays.fill(itemData.basicPatches, 0.0f);
        Arrays.fill(itemData.denimPatches, 0.0f);
        Arrays.fill(itemData.leatherPatches, 0.0f);
        Arrays.fill(itemData.blood, 0.0f);
        Arrays.fill(itemData.dirt, 0.0f);
        Arrays.fill(itemData.hole, 0.0f);
        final Texture sharedTexture = Texture.getSharedTexture(itemData.baseTexture, ModelManager.instance.getTextureFlags());
        if (sharedTexture != null && !sharedTexture.isReady()) {
            this.texturesNotReady.add(sharedTexture);
        }
        if (!itemData.mask.isAllVisible() && !itemData.mask.isNothingVisible()) {
            final Texture e;
            itemData.mask.forEachVisible(Lambda.consumer(itemData.maskFolder, this.texturesNotReady, (part, s, list) -> {
                Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/skinnedmodel/model/CharacterMask$Part;)Ljava/lang/String;, s, part));
                if (e != null && !e.isReady()) {
                    list.add(e);
                }
                return;
            }));
        }
        if (Core.getInstance().isOptionSimpleClothingTextures(this.bZombie)) {
            this.itemData.add(itemData);
            return;
        }
        final String decal = itemVisual.getDecal(clothingItem);
        if (!StringUtils.isNullOrWhitespace(decal)) {
            final ClothingDecal decal2 = ClothingDecals.instance.getDecal(decal);
            if (decal2 != null && decal2.isValid()) {
                itemData.decalTexture = decal2.texture;
                itemData.decalX = decal2.x;
                itemData.decalY = decal2.y;
                itemData.decalWidth = decal2.width;
                itemData.decalHeight = decal2.height;
                final Texture sharedTexture2 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, itemData.decalTexture));
                if (sharedTexture2 != null && !sharedTexture2.isReady()) {
                    this.texturesNotReady.add(sharedTexture2);
                }
            }
        }
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(i);
            itemData.blood[i] = itemVisual.getBlood(fromIndex);
            itemData.dirt[i] = itemVisual.getDirt(fromIndex);
            itemData.basicPatches[i] = itemVisual.getBasicPatch(fromIndex);
            itemData.denimPatches[i] = itemVisual.getDenimPatch(fromIndex);
            itemData.leatherPatches[i] = itemVisual.getLeatherPatch(fromIndex);
            itemData.hole[i] = itemVisual.getHole(fromIndex);
            if (itemData.hole[i] > 0.0f) {
                final Texture sharedTexture3 = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[fromIndex.index()]));
                if (sharedTexture3 != null && !sharedTexture3.isReady()) {
                    this.texturesNotReady.add(sharedTexture3);
                }
            }
            if (itemData.hole[i] == 0.0f && this.holeMask[i]) {
                itemData.hole[i] = -1.0f;
                if (itemData.mask.isBloodBodyPartVisible(fromIndex)) {}
            }
        }
        this.itemData.add(itemData);
    }
    
    @Override
    public void render() {
        if (this.bRendered) {
            return;
        }
        for (int i = 0; i < this.texturesNotReady.size(); ++i) {
            if (!this.texturesNotReady.get(i).isReady()) {
                return;
            }
        }
        GL11.glPushAttrib(2048);
        try {
            this.tempTextures.clear();
            final CharacterSmartTexture fullCharacterTexture = this.createFullCharacterTexture();
            assert fullCharacterTexture == this.characterSmartTexture;
            if (!(this.chrData.modelInstance.tex instanceof CharacterSmartTexture)) {
                this.chrData.modelInstance.tex = new CharacterSmartTexture();
            }
            ((CharacterSmartTexture)this.chrData.modelInstance.tex).clear();
            this.applyCharacterTexture(fullCharacterTexture.result, (CharacterSmartTexture)this.chrData.modelInstance.tex);
            fullCharacterTexture.clear();
            this.tempTextures.add(fullCharacterTexture.result);
            fullCharacterTexture.result = null;
            final CharacterSmartTexture characterSmartTexture = (CharacterSmartTexture)this.chrData.modelInstance.tex;
            for (int j = this.itemData.size() - 1; j >= 0; --j) {
                final ItemData itemData = this.itemData.get(j);
                Texture tex;
                if (this.isSimpleTexture(itemData)) {
                    tex = Texture.getSharedTexture(itemData.baseTexture, ModelManager.instance.getTextureFlags());
                    if (!this.isItemSmartTextureRequired(itemData)) {
                        itemData.modelInstance.tex = tex;
                        continue;
                    }
                }
                else {
                    final ItemSmartTexture fullItemTexture = this.createFullItemTexture(itemData);
                    assert fullItemTexture == this.itemSmartTexture;
                    tex = fullItemTexture.result;
                    this.tempTextures.add(fullItemTexture.result);
                    fullItemTexture.result = null;
                }
                if (itemData.modelInstance == null) {
                    this.applyItemTexture(itemData, tex, characterSmartTexture);
                }
                else {
                    if (!(itemData.modelInstance.tex instanceof ItemSmartTexture)) {
                        itemData.modelInstance.tex = new ItemSmartTexture((String)null);
                    }
                    ((ItemSmartTexture)itemData.modelInstance.tex).clear();
                    this.applyItemTexture(itemData, tex, (SmartTexture)itemData.modelInstance.tex);
                    ((ItemSmartTexture)itemData.modelInstance.tex).calculate();
                    ((ItemSmartTexture)itemData.modelInstance.tex).clear();
                }
            }
            characterSmartTexture.calculate();
            characterSmartTexture.clear();
            this.itemSmartTexture.clear();
            for (int k = 0; k < this.tempTextures.size(); ++k) {
                for (int l = 0; l < this.itemData.size(); ++l) {
                    final ModelInstance modelInstance = this.itemData.get(l).modelInstance;
                    if (modelInstance != null && this.tempTextures.get(k) == modelInstance.tex && !ModelInstanceTextureCreator.$assertionsDisabled) {
                        throw new AssertionError();
                    }
                }
                TextureCombiner.instance.releaseTexture(this.tempTextures.get(k));
            }
            this.tempTextures.clear();
        }
        finally {
            GL11.glPopAttrib();
        }
        this.bRendered = true;
    }
    
    private CharacterSmartTexture createFullCharacterTexture() {
        final CharacterSmartTexture characterSmartTexture = this.characterSmartTexture;
        characterSmartTexture.clear();
        characterSmartTexture.addTexture(this.chrData.baseTexture, CharacterSmartTexture.BodyCategory, ImmutableColor.white, 0.0f);
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(i);
            if (this.chrData.dirt[i] > 0.0f) {
                characterSmartTexture.addDirt(fromIndex, this.chrData.dirt[i], null);
            }
            if (this.chrData.blood[i] > 0.0f) {
                characterSmartTexture.addBlood(fromIndex, this.chrData.blood[i], null);
            }
        }
        characterSmartTexture.calculate();
        return characterSmartTexture;
    }
    
    private void applyCharacterTexture(final Texture texture, final CharacterSmartTexture characterSmartTexture) {
        characterSmartTexture.addMaskedTexture(this.chrData.mask, this.chrData.maskFolder, texture, CharacterSmartTexture.BodyCategory, ImmutableColor.white, 0.0f);
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(i);
            if (this.holeMask[i]) {
                characterSmartTexture.removeHole(texture, fromIndex);
            }
        }
    }
    
    private boolean isSimpleTexture(final ItemData itemData) {
        if (itemData.hue != 0.0f) {
            return false;
        }
        ImmutableColor immutableColor = itemData.tint;
        if (itemData.modelInstance != null) {
            immutableColor = ImmutableColor.white;
        }
        if (!immutableColor.equals(ImmutableColor.white)) {
            return false;
        }
        if (itemData.decalTexture != null) {
            return false;
        }
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            if (itemData.blood[i] > 0.0f) {
                return false;
            }
            if (itemData.dirt[i] > 0.0f) {
                return false;
            }
            if (itemData.hole[i] > 0.0f) {
                return false;
            }
            if (itemData.basicPatches[i] > 0.0f) {
                return false;
            }
            if (itemData.denimPatches[i] > 0.0f) {
                return false;
            }
            if (itemData.leatherPatches[i] > 0.0f) {
                return false;
            }
        }
        return true;
    }
    
    private ItemSmartTexture createFullItemTexture(final ItemData itemData) {
        final ItemSmartTexture itemSmartTexture = this.itemSmartTexture;
        itemSmartTexture.clear();
        final ImmutableColor tint = itemData.tint;
        if (itemData.modelInstance != null) {
            final ModelInstance modelInstance = itemData.modelInstance;
            final ModelInstance modelInstance2 = itemData.modelInstance;
            final ModelInstance modelInstance3 = itemData.modelInstance;
            final float tintR = 1.0f;
            modelInstance3.tintB = tintR;
            modelInstance2.tintG = tintR;
            modelInstance.tintR = tintR;
        }
        itemSmartTexture.addTexture(itemData.baseTexture, itemData.category, tint, itemData.hue);
        if (itemData.decalTexture != null) {
            itemSmartTexture.addRect(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, itemData.decalTexture), itemData.decalX, itemData.decalY, itemData.decalWidth, itemData.decalHeight);
        }
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            if (itemData.blood[i] > 0.0f) {
                itemSmartTexture.addBlood("media/textures/BloodTextures/BloodOverlay.png", BloodBodyPartType.FromIndex(i), itemData.blood[i]);
            }
            if (itemData.dirt[i] > 0.0f) {
                itemSmartTexture.addDirt("media/textures/BloodTextures/GrimeOverlay.png", BloodBodyPartType.FromIndex(i), itemData.dirt[i]);
            }
            if (itemData.basicPatches[i] > 0.0f) {
                itemSmartTexture.setBasicPatches(BloodBodyPartType.FromIndex(i));
            }
            if (itemData.denimPatches[i] > 0.0f) {
                itemSmartTexture.setDenimPatches(BloodBodyPartType.FromIndex(i));
            }
            if (itemData.leatherPatches[i] > 0.0f) {
                itemSmartTexture.setLeatherPatches(BloodBodyPartType.FromIndex(i));
            }
        }
        for (int j = 0; j < BloodBodyPartType.MAX.index(); ++j) {
            if (itemData.hole[j] > 0.0f) {
                final Texture addHole = itemSmartTexture.addHole(BloodBodyPartType.FromIndex(j));
                assert addHole != itemSmartTexture.result;
                this.tempTextures.add(addHole);
            }
        }
        itemSmartTexture.calculate();
        return itemSmartTexture;
    }
    
    private boolean isItemSmartTextureRequired(final ItemData itemData) {
        if (itemData.modelInstance == null) {
            return true;
        }
        if (itemData.modelInstance.tex instanceof ItemSmartTexture) {
            return true;
        }
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            if (itemData.hole[i] < 0.0f) {
                return true;
            }
        }
        return !itemData.mask.isAllVisible();
    }
    
    private void applyItemTexture(final ItemData itemData, final Texture texture, final SmartTexture smartTexture) {
        smartTexture.addMaskedTexture(itemData.mask, itemData.maskFolder, texture, itemData.category, ImmutableColor.white, 0.0f);
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            if (itemData.hole[i] < 0.0f) {
                smartTexture.removeHole(texture, BloodBodyPartType.FromIndex(i));
            }
        }
    }
    
    @Override
    public void postRender() {
        if (!this.bRendered) {
            if (this.chrData.modelInstance.character == null) {}
        }
        for (int i = 0; i < this.itemData.size(); ++i) {
            this.itemData.get(i).modelInstance = null;
        }
        this.chrData.modelInstance = null;
        this.texturesNotReady.clear();
        ItemData.pool.release(this.itemData);
        this.itemData.clear();
        ModelInstanceTextureCreator.pool.release(this);
    }
    
    public boolean isRendered() {
        return this.testNotReady <= 0 && this.bRendered;
    }
    
    public static ModelInstanceTextureCreator alloc() {
        return ModelInstanceTextureCreator.pool.alloc();
    }
    
    static {
        pool = new ObjectPool<ModelInstanceTextureCreator>(ModelInstanceTextureCreator::new);
    }
    
    private static final class CharacterData
    {
        ModelInstance modelInstance;
        final CharacterMask mask;
        String maskFolder;
        String baseTexture;
        final float[] blood;
        final float[] dirt;
        
        private CharacterData() {
            this.mask = new CharacterMask();
            this.blood = new float[BloodBodyPartType.MAX.index()];
            this.dirt = new float[BloodBodyPartType.MAX.index()];
        }
    }
    
    private static final class ItemData
    {
        ModelInstance modelInstance;
        final CharacterMask mask;
        String maskFolder;
        String baseTexture;
        int category;
        ImmutableColor tint;
        float hue;
        String decalTexture;
        int decalX;
        int decalY;
        int decalWidth;
        int decalHeight;
        final float[] blood;
        final float[] dirt;
        final float[] basicPatches;
        final float[] denimPatches;
        final float[] leatherPatches;
        final float[] hole;
        static final ObjectPool<ItemData> pool;
        
        private ItemData() {
            this.mask = new CharacterMask();
            this.blood = new float[BloodBodyPartType.MAX.index()];
            this.dirt = new float[BloodBodyPartType.MAX.index()];
            this.basicPatches = new float[BloodBodyPartType.MAX.index()];
            this.denimPatches = new float[BloodBodyPartType.MAX.index()];
            this.leatherPatches = new float[BloodBodyPartType.MAX.index()];
            this.hole = new float[BloodBodyPartType.MAX.index()];
        }
        
        static {
            pool = new ObjectPool<ItemData>(ItemData::new);
        }
    }
}
