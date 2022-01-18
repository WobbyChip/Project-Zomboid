// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.visual;

import zombie.characters.WornItems.BodyLocation;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.DefaultClothing;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.ModelInstance;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Collection;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.IsoWorld;
import zombie.characters.SurvivorDesc;
import java.util.Arrays;
import zombie.characterTextures.BloodBodyPartType;
import java.util.ArrayList;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.ImmutableColor;

public final class HumanVisual extends BaseVisual
{
    private final IHumanVisual owner;
    private ImmutableColor skinColor;
    private int skinTexture;
    private String skinTextureName;
    public int zombieRotStage;
    private ImmutableColor hairColor;
    private ImmutableColor beardColor;
    private String hairModel;
    private String beardModel;
    private int bodyHair;
    private final byte[] blood;
    private final byte[] dirt;
    private final byte[] holes;
    private final ItemVisuals bodyVisuals;
    private Outfit outfit;
    private String nonAttachedHair;
    private static final ArrayList<String> itemVisualLocations;
    private static final int LASTSTAND_VERSION1 = 1;
    private static final int LASTSTAND_VERSION = 1;
    
    public HumanVisual(final IHumanVisual owner) {
        this.skinColor = ImmutableColor.white;
        this.skinTexture = -1;
        this.skinTextureName = null;
        this.zombieRotStage = -1;
        this.bodyHair = -1;
        this.blood = new byte[BloodBodyPartType.MAX.index()];
        this.dirt = new byte[BloodBodyPartType.MAX.index()];
        this.holes = new byte[BloodBodyPartType.MAX.index()];
        this.bodyVisuals = new ItemVisuals();
        this.outfit = null;
        this.nonAttachedHair = null;
        this.owner = owner;
        Arrays.fill(this.blood, (byte)0);
        Arrays.fill(this.dirt, (byte)0);
        Arrays.fill(this.holes, (byte)0);
    }
    
    public boolean isFemale() {
        return this.owner.isFemale();
    }
    
    public boolean isZombie() {
        return this.owner.isZombie();
    }
    
    public boolean isSkeleton() {
        return this.owner.isSkeleton();
    }
    
    public void setSkinColor(final ImmutableColor skinColor) {
        this.skinColor = skinColor;
    }
    
    public ImmutableColor getSkinColor() {
        if (this.skinColor == null) {
            this.skinColor = new ImmutableColor(SurvivorDesc.getRandomSkinColor());
        }
        return this.skinColor;
    }
    
    public void setBodyHairIndex(final int bodyHair) {
        this.bodyHair = bodyHair;
    }
    
    public int getBodyHairIndex() {
        return this.bodyHair;
    }
    
    public void setSkinTextureIndex(final int skinTexture) {
        this.skinTexture = skinTexture;
    }
    
    public int getSkinTextureIndex() {
        return this.skinTexture;
    }
    
    public void setSkinTextureName(final String skinTextureName) {
        this.skinTextureName = skinTextureName;
    }
    
    public float lerp(final float n, final float n2, float n3) {
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n3 >= 1.0f) {
            n3 = 1.0f;
        }
        return n + (n2 - n) * n3;
    }
    
    public int pickRandomZombieRotStage() {
        final int max = Math.max((int)IsoWorld.instance.getWorldAgeDays(), 0);
        final float n = 20.0f;
        final float n2 = 90.0f;
        final float n3 = 100.0f;
        float n4 = 20.0f;
        final float n5 = 10.0f;
        float n6 = 30.0f;
        if (max >= 180) {
            n4 = 0.0f;
            n6 = 10.0f;
        }
        final float n7 = (max - n) / (n2 - n);
        final float lerp = this.lerp(n3, n4, n7);
        final float lerp2 = this.lerp(n5, n6, n7);
        final float n8 = (float)OutfitRNG.Next(100);
        if (n8 < lerp) {
            return 1;
        }
        if (n8 < lerp2 + lerp) {
            return 2;
        }
        return 3;
    }
    
    public String getSkinTexture() {
        if (this.skinTextureName != null) {
            return this.skinTextureName;
        }
        String s = "";
        ArrayList<String> list = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins : PopTemplateManager.instance.m_MaleSkins;
        if (this.owner.isZombie() && this.owner.isSkeleton()) {
            if (this.owner.isFemale()) {
                list = PopTemplateManager.instance.m_SkeletonFemaleSkins_Zombie;
            }
            else {
                list = PopTemplateManager.instance.m_SkeletonMaleSkins_Zombie;
            }
        }
        else if (this.owner.isZombie()) {
            if (this.zombieRotStage < 1 || this.zombieRotStage > 3) {
                this.zombieRotStage = this.pickRandomZombieRotStage();
            }
            switch (this.zombieRotStage) {
                case 1: {
                    list = (this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie1 : PopTemplateManager.instance.m_MaleSkins_Zombie1);
                    break;
                }
                case 2: {
                    list = (this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie2 : PopTemplateManager.instance.m_MaleSkins_Zombie2);
                    break;
                }
                case 3: {
                    list = (this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie3 : PopTemplateManager.instance.m_MaleSkins_Zombie3);
                    break;
                }
            }
        }
        else if (!this.owner.isFemale()) {
            s = ((!this.owner.isZombie() && this.bodyHair >= 0) ? "a" : "");
        }
        if (this.skinTexture < 0 || this.skinTexture >= list.size()) {
            this.skinTexture = OutfitRNG.Next(list.size());
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)list.get(this.skinTexture), s);
    }
    
    public void setHairColor(final ImmutableColor hairColor) {
        this.hairColor = hairColor;
    }
    
    public ImmutableColor getHairColor() {
        if (this.hairColor == null) {
            this.hairColor = HairOutfitDefinitions.instance.getRandomHaircutColor((this.outfit != null) ? this.outfit.m_Name : null);
        }
        return this.hairColor;
    }
    
    public void setBeardColor(final ImmutableColor beardColor) {
        this.beardColor = beardColor;
    }
    
    public ImmutableColor getBeardColor() {
        if (this.beardColor == null) {
            this.beardColor = this.getHairColor();
        }
        return this.beardColor;
    }
    
    public void setHairModel(final String hairModel) {
        this.hairModel = hairModel;
    }
    
    public String getHairModel() {
        if (this.owner.isFemale()) {
            if (HairStyles.instance.FindFemaleStyle(this.hairModel) == null) {
                this.hairModel = HairStyles.instance.getRandomFemaleStyle((this.outfit != null) ? this.outfit.m_Name : null);
            }
        }
        else if (HairStyles.instance.FindMaleStyle(this.hairModel) == null) {
            this.hairModel = HairStyles.instance.getRandomMaleStyle((this.outfit != null) ? this.outfit.m_Name : null);
        }
        return this.hairModel;
    }
    
    public void setBeardModel(final String beardModel) {
        this.beardModel = beardModel;
    }
    
    public String getBeardModel() {
        if (this.owner.isFemale()) {
            this.beardModel = null;
        }
        else if (BeardStyles.instance.FindStyle(this.beardModel) == null) {
            this.beardModel = BeardStyles.instance.getRandomStyle((this.outfit != null) ? this.outfit.m_Name : null);
        }
        return this.beardModel;
    }
    
    public void setBlood(final BloodBodyPartType bloodBodyPartType, float max) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        this.blood[bloodBodyPartType.index()] = (byte)(max * 255.0f);
    }
    
    public float getBlood(final BloodBodyPartType bloodBodyPartType) {
        return (this.blood[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public void setDirt(final BloodBodyPartType bloodBodyPartType, float max) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        this.dirt[bloodBodyPartType.index()] = (byte)(max * 255.0f);
    }
    
    public float getDirt(final BloodBodyPartType bloodBodyPartType) {
        return (this.dirt[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public void setHole(final BloodBodyPartType bloodBodyPartType) {
        this.holes[bloodBodyPartType.index()] = -1;
    }
    
    public float getHole(final BloodBodyPartType bloodBodyPartType) {
        return (this.holes[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public void removeBlood() {
        Arrays.fill(this.blood, (byte)0);
    }
    
    public void removeDirt() {
        Arrays.fill(this.dirt, (byte)0);
    }
    
    public void randomBlood() {
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            this.setBlood(BloodBodyPartType.FromIndex(i), OutfitRNG.Next(0.0f, 1.0f));
        }
    }
    
    public void randomDirt() {
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            this.setDirt(BloodBodyPartType.FromIndex(i), OutfitRNG.Next(0.0f, 1.0f));
        }
    }
    
    public float getTotalBlood() {
        float n = 0.0f;
        for (int i = 0; i < this.blood.length; ++i) {
            n += (this.blood[i] & 0xFF) / 255.0f;
        }
        return n;
    }
    
    public void clear() {
        this.skinColor = ImmutableColor.white;
        this.skinTexture = -1;
        this.skinTextureName = null;
        this.zombieRotStage = -1;
        this.hairColor = null;
        this.beardColor = null;
        this.hairModel = null;
        this.nonAttachedHair = null;
        this.beardModel = null;
        this.bodyHair = -1;
        Arrays.fill(this.blood, (byte)0);
        Arrays.fill(this.dirt, (byte)0);
        Arrays.fill(this.holes, (byte)0);
        this.bodyVisuals.clear();
    }
    
    public void copyFrom(final HumanVisual humanVisual) {
        if (humanVisual == null) {
            this.clear();
            return;
        }
        humanVisual.getHairColor();
        humanVisual.getHairModel();
        humanVisual.getBeardModel();
        humanVisual.getSkinTexture();
        this.skinColor = humanVisual.skinColor;
        this.skinTexture = humanVisual.skinTexture;
        this.skinTextureName = humanVisual.skinTextureName;
        this.zombieRotStage = humanVisual.zombieRotStage;
        this.hairColor = humanVisual.hairColor;
        this.beardColor = humanVisual.beardColor;
        this.hairModel = humanVisual.hairModel;
        this.nonAttachedHair = humanVisual.nonAttachedHair;
        this.beardModel = humanVisual.beardModel;
        this.bodyHair = humanVisual.bodyHair;
        this.outfit = humanVisual.outfit;
        System.arraycopy(humanVisual.blood, 0, this.blood, 0, this.blood.length);
        System.arraycopy(humanVisual.dirt, 0, this.dirt, 0, this.dirt.length);
        System.arraycopy(humanVisual.holes, 0, this.holes, 0, this.holes.length);
        this.bodyVisuals.clear();
        this.bodyVisuals.addAll(humanVisual.bodyVisuals);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byte b = 0;
        if (this.hairColor != null) {
            b |= 0x4;
        }
        if (this.beardColor != null) {
            b |= 0x2;
        }
        if (this.skinColor != null) {
            b |= 0x8;
        }
        if (this.beardModel != null) {
            b |= 0x10;
        }
        if (this.hairModel != null) {
            b |= 0x20;
        }
        if (this.skinTextureName != null) {
            b |= 0x40;
        }
        byteBuffer.put(b);
        if (this.hairColor != null) {
            byteBuffer.put(this.hairColor.getRedByte());
            byteBuffer.put(this.hairColor.getGreenByte());
            byteBuffer.put(this.hairColor.getBlueByte());
        }
        if (this.beardColor != null) {
            byteBuffer.put(this.beardColor.getRedByte());
            byteBuffer.put(this.beardColor.getGreenByte());
            byteBuffer.put(this.beardColor.getBlueByte());
        }
        if (this.skinColor != null) {
            byteBuffer.put(this.skinColor.getRedByte());
            byteBuffer.put(this.skinColor.getGreenByte());
            byteBuffer.put(this.skinColor.getBlueByte());
        }
        byteBuffer.put((byte)this.bodyHair);
        byteBuffer.put((byte)this.skinTexture);
        byteBuffer.put((byte)this.zombieRotStage);
        if (this.skinTextureName != null) {
            GameWindow.WriteString(byteBuffer, this.skinTextureName);
        }
        if (this.beardModel != null) {
            GameWindow.WriteString(byteBuffer, this.beardModel);
        }
        if (this.hairModel != null) {
            GameWindow.WriteString(byteBuffer, this.hairModel);
        }
        byteBuffer.put((byte)this.blood.length);
        for (int i = 0; i < this.blood.length; ++i) {
            byteBuffer.put(this.blood[i]);
        }
        byteBuffer.put((byte)this.dirt.length);
        for (int j = 0; j < this.dirt.length; ++j) {
            byteBuffer.put(this.dirt[j]);
        }
        byteBuffer.put((byte)this.holes.length);
        for (int k = 0; k < this.holes.length; ++k) {
            byteBuffer.put(this.holes[k]);
        }
        byteBuffer.put((byte)this.bodyVisuals.size());
        for (int l = 0; l < this.bodyVisuals.size(); ++l) {
            this.bodyVisuals.get(l).save(byteBuffer);
        }
        GameWindow.WriteString(byteBuffer, this.getNonAttachedHair());
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        final int n2 = byteBuffer.get() & 0xFF;
        if ((n2 & 0x4) != 0x0) {
            this.hairColor = new ImmutableColor(byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF);
        }
        if ((n2 & 0x2) != 0x0) {
            this.beardColor = new ImmutableColor(byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF);
        }
        if ((n2 & 0x8) != 0x0) {
            this.skinColor = new ImmutableColor(byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF);
        }
        this.bodyHair = byteBuffer.get();
        this.skinTexture = byteBuffer.get();
        if (n >= 156) {
            this.zombieRotStage = byteBuffer.get();
        }
        if ((n2 & 0x40) != 0x0) {
            this.skinTextureName = GameWindow.ReadString(byteBuffer);
        }
        if ((n2 & 0x10) != 0x0) {
            this.beardModel = GameWindow.ReadString(byteBuffer);
        }
        if ((n2 & 0x20) != 0x0) {
            this.hairModel = GameWindow.ReadString(byteBuffer);
        }
        for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
            final byte value2 = byteBuffer.get();
            if (b < this.blood.length) {
                this.blood[b] = value2;
            }
        }
        if (n >= 163) {
            for (byte value3 = byteBuffer.get(), b2 = 0; b2 < value3; ++b2) {
                final byte value4 = byteBuffer.get();
                if (b2 < this.dirt.length) {
                    this.dirt[b2] = value4;
                }
            }
        }
        for (byte value5 = byteBuffer.get(), b3 = 0; b3 < value5; ++b3) {
            final byte value6 = byteBuffer.get();
            if (b3 < this.holes.length) {
                this.holes[b3] = value6;
            }
        }
        for (byte value7 = byteBuffer.get(), b4 = 0; b4 < value7; ++b4) {
            final ItemVisual e = new ItemVisual();
            e.load(byteBuffer, n);
            this.bodyVisuals.add(e);
        }
        this.setNonAttachedHair(GameWindow.ReadString(byteBuffer));
    }
    
    @Override
    public ModelInstance createModelInstance() {
        return null;
    }
    
    public static CharacterMask GetMask(final ItemVisuals itemVisuals) {
        final CharacterMask characterMask = new CharacterMask();
        for (int i = itemVisuals.size() - 1; i >= 0; --i) {
            itemVisuals.get(i).getClothingItemCombinedMask(characterMask);
        }
        return characterMask;
    }
    
    public void synchWithOutfit(final Outfit outfit) {
        if (outfit == null) {
            return;
        }
        this.hairColor = outfit.RandomData.m_hairColor;
        this.beardColor = this.hairColor;
        this.hairModel = (this.owner.isFemale() ? outfit.RandomData.m_femaleHairName : outfit.RandomData.m_maleHairName);
        this.beardModel = (this.owner.isFemale() ? null : outfit.RandomData.m_beardName);
        this.getSkinTexture();
    }
    
    public void dressInNamedOutfit(final String s, final ItemVisuals itemVisuals) {
        itemVisuals.clear();
        if (StringUtils.isNullOrWhitespace(s)) {
            return;
        }
        final Outfit outfit = this.owner.isFemale() ? OutfitManager.instance.FindFemaleOutfit(s) : OutfitManager.instance.FindMaleOutfit(s);
        if (outfit == null) {
            return;
        }
        final Outfit clone = outfit.clone();
        clone.Randomize();
        this.dressInOutfit(clone, itemVisuals);
    }
    
    public void dressInClothingItem(final String s, final ItemVisuals itemVisuals) {
        this.dressInClothingItem(s, itemVisuals, true);
    }
    
    public void dressInClothingItem(final String itemGUID, final ItemVisuals itemVisuals, final boolean b) {
        if (b) {
            this.clear();
            itemVisuals.clear();
        }
        if (OutfitManager.instance.getClothingItem(itemGUID) == null) {
            return;
        }
        final Outfit outfit = new Outfit();
        final ClothingItemReference e = new ClothingItemReference();
        e.itemGUID = itemGUID;
        outfit.m_items.add(e);
        outfit.m_Pants = false;
        outfit.m_Top = false;
        outfit.Randomize();
        this.dressInOutfit(outfit, itemVisuals);
    }
    
    private void dressInOutfit(final Outfit outfit, final ItemVisuals itemVisuals) {
        this.setOutfit(outfit);
        this.getItemVisualLocations(itemVisuals, HumanVisual.itemVisualLocations);
        if (outfit.m_Pants) {
            this.addClothingItem(itemVisuals, HumanVisual.itemVisualLocations, outfit.m_AllowPantsHue ? DefaultClothing.instance.pickPantsHue() : (outfit.m_AllowPantsTint ? DefaultClothing.instance.pickPantsTint() : DefaultClothing.instance.pickPantsTexture()), null);
        }
        if (outfit.m_Top && outfit.RandomData.m_hasTop) {
            String s;
            if (outfit.RandomData.m_hasTShirt) {
                if (outfit.RandomData.m_hasTShirtDecal && outfit.GetMask().isTorsoVisible() && outfit.m_AllowTShirtDecal) {
                    s = (outfit.m_AllowTopTint ? DefaultClothing.instance.pickTShirtDecalTint() : DefaultClothing.instance.pickTShirtDecalTexture());
                }
                else {
                    s = (outfit.m_AllowTopTint ? DefaultClothing.instance.pickTShirtTint() : DefaultClothing.instance.pickTShirtTexture());
                }
            }
            else {
                s = (outfit.m_AllowTopTint ? DefaultClothing.instance.pickVestTint() : DefaultClothing.instance.pickVestTexture());
            }
            this.addClothingItem(itemVisuals, HumanVisual.itemVisualLocations, s, null);
        }
        for (int i = 0; i < outfit.m_items.size(); ++i) {
            final ClothingItemReference clothingItemReference = outfit.m_items.get(i);
            final ClothingItem clothingItem = clothingItemReference.getClothingItem();
            if (clothingItem != null) {
                if (clothingItem.isReady()) {
                    this.addClothingItem(itemVisuals, HumanVisual.itemVisualLocations, clothingItem.m_Name, clothingItemReference);
                }
            }
        }
        outfit.m_Pants = false;
        outfit.m_Top = false;
        outfit.RandomData.m_topTexture = null;
        outfit.RandomData.m_pantsTexture = null;
    }
    
    public ItemVisuals getBodyVisuals() {
        return this.bodyVisuals;
    }
    
    public ItemVisual addBodyVisual(final String anObject) {
        if (StringUtils.isNullOrWhitespace(anObject)) {
            return null;
        }
        final Item itemForClothingItem = ScriptManager.instance.getItemForClothingItem(anObject);
        if (itemForClothingItem == null) {
            return null;
        }
        final ClothingItem clothingItemAsset = itemForClothingItem.getClothingItemAsset();
        if (clothingItemAsset == null) {
            return null;
        }
        for (int i = 0; i < this.bodyVisuals.size(); ++i) {
            if (this.bodyVisuals.get(i).getClothingItemName().equals(anObject)) {
                return null;
            }
        }
        final ClothingItemReference clothingItemReference = new ClothingItemReference();
        clothingItemReference.itemGUID = clothingItemAsset.m_GUID;
        clothingItemReference.randomize();
        final ItemVisual e = new ItemVisual();
        e.setItemType(itemForClothingItem.getFullName());
        e.synchWithOutfit(clothingItemReference);
        this.bodyVisuals.add(e);
        return e;
    }
    
    private void getItemVisualLocations(final ItemVisuals itemVisuals, final ArrayList<String> list) {
        list.clear();
        for (int i = 0; i < itemVisuals.size(); ++i) {
            final Item scriptItem = itemVisuals.get(i).getScriptItem();
            if (scriptItem == null) {
                list.add(null);
            }
            else {
                String e = scriptItem.getBodyLocation();
                if (StringUtils.isNullOrWhitespace(e)) {
                    e = scriptItem.CanBeEquipped;
                }
                list.add(e);
            }
        }
    }
    
    public ItemVisual addClothingItem(final ItemVisuals itemVisuals, final Item item) {
        if (item == null) {
            return null;
        }
        final ClothingItem clothingItemAsset = item.getClothingItemAsset();
        if (clothingItemAsset == null) {
            return null;
        }
        if (!clothingItemAsset.isReady()) {
            return null;
        }
        this.getItemVisualLocations(itemVisuals, HumanVisual.itemVisualLocations);
        return this.addClothingItem(itemVisuals, HumanVisual.itemVisualLocations, clothingItemAsset.m_Name, null);
    }
    
    private ItemVisual addClothingItem(final ItemVisuals itemVisuals, final ArrayList<String> list, final String s, ClothingItemReference clothingItemReference) {
        assert itemVisuals.size() == list.size();
        if (clothingItemReference != null && !clothingItemReference.RandomData.m_Active) {
            return null;
        }
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        final Item itemForClothingItem = ScriptManager.instance.getItemForClothingItem(s);
        if (itemForClothingItem == null) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.warn("Could not find item type for %s", s);
            }
            return null;
        }
        final ClothingItem clothingItemAsset = itemForClothingItem.getClothingItemAsset();
        if (clothingItemAsset == null) {
            return null;
        }
        if (!clothingItemAsset.isReady()) {
            return null;
        }
        String s2 = itemForClothingItem.getBodyLocation();
        if (StringUtils.isNullOrWhitespace(s2)) {
            s2 = itemForClothingItem.CanBeEquipped;
        }
        if (StringUtils.isNullOrWhitespace(s2)) {
            return null;
        }
        if (clothingItemReference == null) {
            clothingItemReference = new ClothingItemReference();
            clothingItemReference.itemGUID = clothingItemAsset.m_GUID;
            clothingItemReference.randomize();
        }
        if (!clothingItemReference.RandomData.m_Active) {
            return null;
        }
        final BodyLocationGroup group = BodyLocations.getGroup("Human");
        final BodyLocation location = group.getLocation(s2);
        if (location == null) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, itemForClothingItem.name));
            return null;
        }
        if (!location.isMultiItem()) {
            final int index = list.indexOf(s2);
            if (index != -1) {
                itemVisuals.remove(index);
                list.remove(index);
            }
        }
        for (int i = 0; i < itemVisuals.size(); ++i) {
            if (group.isExclusive(s2, list.get(i))) {
                itemVisuals.remove(i);
                list.remove(i);
                --i;
            }
        }
        assert itemVisuals.size() == list.size();
        final int index2 = group.indexOf(s2);
        int size = itemVisuals.size();
        for (int j = 0; j < itemVisuals.size(); ++j) {
            if (group.indexOf(list.get(j)) > index2) {
                size = j;
                break;
            }
        }
        final ItemVisual element = new ItemVisual();
        element.setItemType(itemForClothingItem.getFullName());
        element.synchWithOutfit(clothingItemReference);
        itemVisuals.add(size, element);
        list.add(size, s2);
        return element;
    }
    
    public Outfit getOutfit() {
        return this.outfit;
    }
    
    public void setOutfit(final Outfit outfit) {
        this.outfit = outfit;
    }
    
    public String getNonAttachedHair() {
        return this.nonAttachedHair;
    }
    
    public void setNonAttachedHair(String nonAttachedHair) {
        if (StringUtils.isNullOrWhitespace(nonAttachedHair)) {
            nonAttachedHair = null;
        }
        this.nonAttachedHair = nonAttachedHair;
    }
    
    private static StringBuilder toString(final ImmutableColor immutableColor, final StringBuilder sb) {
        sb.append(immutableColor.getRedByte() & 0xFF);
        sb.append(",");
        sb.append(immutableColor.getGreenByte() & 0xFF);
        sb.append(",");
        sb.append(immutableColor.getBlueByte() & 0xFF);
        return sb;
    }
    
    private static ImmutableColor colorFromString(final String s) {
        final String[] split = s.split(",");
        if (split.length == 3) {
            try {
                return new ImmutableColor(Integer.parseInt(split[0]) / 255.0f, Integer.parseInt(split[1]) / 255.0f, Integer.parseInt(split[2]) / 255.0f);
            }
            catch (NumberFormatException ex) {}
        }
        return null;
    }
    
    public String getLastStandString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("version=");
        sb.append(1);
        sb.append(";");
        if (this.getHairColor() != null) {
            sb.append("hairColor=");
            toString(this.getHairColor(), sb);
            sb.append(";");
        }
        if (this.getBeardColor() != null) {
            sb.append("beardColor=");
            toString(this.getBeardColor(), sb);
            sb.append(";");
        }
        if (this.getSkinColor() != null) {
            sb.append("skinColor=");
            toString(this.getSkinColor(), sb);
            sb.append(";");
        }
        sb.append("bodyHair=");
        sb.append(this.getBodyHairIndex());
        sb.append(";");
        sb.append("skinTexture=");
        sb.append(this.getSkinTextureIndex());
        sb.append(";");
        if (this.getSkinTexture() != null) {
            sb.append("skinTextureName=");
            sb.append(this.getSkinTexture());
            sb.append(";");
        }
        if (this.getHairModel() != null) {
            sb.append("hairModel=");
            sb.append(this.getHairModel());
            sb.append(";");
        }
        if (this.getBeardModel() != null) {
            sb.append("beardModel=");
            sb.append(this.getBeardModel());
            sb.append(";");
        }
        return sb.toString();
    }
    
    public boolean loadLastStandString(String trim) {
        trim = trim.trim();
        if (StringUtils.isNullOrWhitespace(trim) || !trim.startsWith("version=")) {
            return false;
        }
        final String[] split = trim.split(";");
        for (int i = 0; i < split.length; ++i) {
            final int index = split[i].indexOf(61);
            if (index != -1) {
                final String trim2 = split[i].substring(0, index).trim();
                final String trim3 = split[i].substring(index + 1).trim();
                final String s = trim2;
                switch (s) {
                    case "version": {
                        final int int1 = Integer.parseInt(trim3);
                        if (int1 < 1 || int1 > 1) {
                            return false;
                        }
                        break;
                    }
                    case "beardColor": {
                        final ImmutableColor colorFromString = colorFromString(trim3);
                        if (colorFromString != null) {
                            this.setBeardColor(colorFromString);
                            break;
                        }
                        break;
                    }
                    case "beardModel": {
                        this.setBeardModel(trim3);
                        break;
                    }
                    case "bodyHair": {
                        try {
                            this.setBodyHairIndex(Integer.parseInt(trim3));
                        }
                        catch (NumberFormatException ex) {}
                        break;
                    }
                    case "hairColor": {
                        final ImmutableColor colorFromString2 = colorFromString(trim3);
                        if (colorFromString2 != null) {
                            this.setHairColor(colorFromString2);
                            break;
                        }
                        break;
                    }
                    case "hairModel": {
                        this.setHairModel(trim3);
                        break;
                    }
                    case "skinColor": {
                        final ImmutableColor colorFromString3 = colorFromString(trim3);
                        if (colorFromString3 != null) {
                            this.setSkinColor(colorFromString3);
                            break;
                        }
                        break;
                    }
                    case "skinTexture": {
                        try {
                            this.setSkinTextureIndex(Integer.parseInt(trim3));
                        }
                        catch (NumberFormatException ex2) {}
                        break;
                    }
                    case "skinTextureName": {
                        this.setSkinTextureName(trim3);
                        break;
                    }
                }
            }
        }
        return true;
    }
    
    static {
        itemVisualLocations = new ArrayList<String>();
    }
}
