// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.visual;

import zombie.inventory.InventoryItemFactory;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.characterTextures.BloodBodyPartType;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Arrays;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;
import zombie.scripting.objects.Item;
import java.util.Objects;
import zombie.inventory.InventoryItem;
import zombie.core.ImmutableColor;

public final class ItemVisual extends BaseVisual
{
    private String m_fullType;
    private String m_clothingItemName;
    private String m_alternateModelName;
    public static final float NULL_HUE = Float.POSITIVE_INFINITY;
    public float m_Hue;
    public ImmutableColor m_Tint;
    public int m_BaseTexture;
    public int m_TextureChoice;
    public String m_Decal;
    private byte[] blood;
    private byte[] dirt;
    private byte[] holes;
    private byte[] basicPatches;
    private byte[] denimPatches;
    private byte[] leatherPatches;
    private InventoryItem inventoryItem;
    private static final int LASTSTAND_VERSION1 = 1;
    private static final int LASTSTAND_VERSION = 1;
    
    public ItemVisual() {
        this.m_Hue = Float.POSITIVE_INFINITY;
        this.m_Tint = null;
        this.m_BaseTexture = -1;
        this.m_TextureChoice = -1;
        this.m_Decal = null;
        this.inventoryItem = null;
    }
    
    public ItemVisual(final ItemVisual itemVisual) {
        this.m_Hue = Float.POSITIVE_INFINITY;
        this.m_Tint = null;
        this.m_BaseTexture = -1;
        this.m_TextureChoice = -1;
        this.m_Decal = null;
        this.inventoryItem = null;
        this.copyFrom(itemVisual);
    }
    
    public void setItemType(final String s) {
        Objects.requireNonNull(s);
        assert s.contains(".");
        this.m_fullType = s;
    }
    
    public String getItemType() {
        return this.m_fullType;
    }
    
    public void setAlternateModelName(final String alternateModelName) {
        this.m_alternateModelName = alternateModelName;
    }
    
    public String getAlternateModelName() {
        return this.m_alternateModelName;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.m_clothingItemName);
    }
    
    public String getClothingItemName() {
        return this.m_clothingItemName;
    }
    
    public void setClothingItemName(final String clothingItemName) {
        this.m_clothingItemName = clothingItemName;
    }
    
    public Item getScriptItem() {
        if (StringUtils.isNullOrWhitespace(this.m_fullType)) {
            return null;
        }
        return ScriptManager.instance.getItem(this.m_fullType);
    }
    
    public ClothingItem getClothingItem() {
        final Item scriptItem = this.getScriptItem();
        if (scriptItem == null) {
            return null;
        }
        if (!StringUtils.isNullOrWhitespace(this.m_alternateModelName)) {
            if ("LeftHand".equalsIgnoreCase(this.m_alternateModelName)) {
                return scriptItem.replaceSecondHand.clothingItem;
            }
            if ("RightHand".equalsIgnoreCase(this.m_alternateModelName)) {
                return scriptItem.replacePrimaryHand.clothingItem;
            }
        }
        return scriptItem.getClothingItemAsset();
    }
    
    public void getClothingItemCombinedMask(final CharacterMask characterMask) {
        ClothingItem.tryGetCombinedMask(this.getClothingItem(), characterMask);
    }
    
    public void setHue(float hue) {
        hue = Math.max(hue, -1.0f);
        hue = Math.min(hue, 1.0f);
        this.m_Hue = hue;
    }
    
    public float getHue(final ClothingItem clothingItem) {
        if (clothingItem.m_AllowRandomHue) {
            if (this.m_Hue == Float.POSITIVE_INFINITY) {
                this.m_Hue = OutfitRNG.Next(200) / 100.0f - 1.0f;
            }
            return this.m_Hue;
        }
        return this.m_Hue = 0.0f;
    }
    
    public void setTint(final ImmutableColor tint) {
        this.m_Tint = tint;
    }
    
    public ImmutableColor getTint(final ClothingItem clothingItem) {
        if (clothingItem.m_AllowRandomTint) {
            if (this.m_Tint == null) {
                this.m_Tint = OutfitRNG.randomImmutableColor();
            }
            return this.m_Tint;
        }
        return this.m_Tint = ImmutableColor.white;
    }
    
    public ImmutableColor getTint() {
        return this.m_Tint;
    }
    
    public String getBaseTexture(final ClothingItem clothingItem) {
        if (clothingItem.m_BaseTextures.isEmpty()) {
            this.m_BaseTexture = -1;
            return null;
        }
        if (this.m_BaseTexture < 0 || this.m_BaseTexture >= clothingItem.m_BaseTextures.size()) {
            this.m_BaseTexture = OutfitRNG.Next(clothingItem.m_BaseTextures.size());
        }
        return clothingItem.m_BaseTextures.get(this.m_BaseTexture);
    }
    
    public String getTextureChoice(final ClothingItem clothingItem) {
        if (clothingItem.textureChoices.isEmpty()) {
            this.m_TextureChoice = -1;
            return null;
        }
        if (this.m_TextureChoice < 0 || this.m_TextureChoice >= clothingItem.textureChoices.size()) {
            this.m_TextureChoice = OutfitRNG.Next(clothingItem.textureChoices.size());
        }
        return clothingItem.textureChoices.get(this.m_TextureChoice);
    }
    
    public void setDecal(final String decal) {
        this.m_Decal = decal;
    }
    
    public String getDecal(final ClothingItem clothingItem) {
        if (StringUtils.isNullOrWhitespace(clothingItem.m_DecalGroup)) {
            return this.m_Decal = null;
        }
        if (this.m_Decal == null) {
            this.m_Decal = ClothingDecals.instance.getRandomDecal(clothingItem.m_DecalGroup);
        }
        return this.m_Decal;
    }
    
    public void pickUninitializedValues(final ClothingItem clothingItem) {
        if (clothingItem == null || !clothingItem.isReady()) {
            return;
        }
        this.getHue(clothingItem);
        this.getTint(clothingItem);
        this.getBaseTexture(clothingItem);
        this.getTextureChoice(clothingItem);
        this.getDecal(clothingItem);
    }
    
    public void synchWithOutfit(final ClothingItemReference clothingItemReference) {
        final ClothingItem clothingItem = clothingItemReference.getClothingItem();
        this.m_clothingItemName = clothingItem.m_Name;
        this.m_Hue = clothingItemReference.RandomData.m_Hue;
        this.m_Tint = clothingItemReference.RandomData.m_Tint;
        this.m_BaseTexture = clothingItem.m_BaseTextures.indexOf(clothingItemReference.RandomData.m_BaseTexture);
        this.m_TextureChoice = clothingItem.textureChoices.indexOf(clothingItemReference.RandomData.m_TextureChoice);
        this.m_Decal = clothingItemReference.RandomData.m_Decal;
    }
    
    public void copyFrom(final ItemVisual itemVisual) {
        if (itemVisual == null) {
            this.m_fullType = null;
            this.m_clothingItemName = null;
            this.m_alternateModelName = null;
            this.m_Hue = Float.POSITIVE_INFINITY;
            this.m_Tint = null;
            this.m_BaseTexture = -1;
            this.m_TextureChoice = -1;
            this.m_Decal = null;
            if (this.blood != null) {
                Arrays.fill(this.blood, (byte)0);
            }
            if (this.dirt != null) {
                Arrays.fill(this.dirt, (byte)0);
            }
            if (this.holes != null) {
                Arrays.fill(this.holes, (byte)0);
            }
            if (this.basicPatches != null) {
                Arrays.fill(this.basicPatches, (byte)0);
            }
            if (this.denimPatches != null) {
                Arrays.fill(this.denimPatches, (byte)0);
            }
            if (this.leatherPatches != null) {
                Arrays.fill(this.leatherPatches, (byte)0);
            }
            return;
        }
        final ClothingItem clothingItem = itemVisual.getClothingItem();
        if (clothingItem != null) {
            itemVisual.pickUninitializedValues(clothingItem);
        }
        this.m_fullType = itemVisual.m_fullType;
        this.m_clothingItemName = itemVisual.m_clothingItemName;
        this.m_alternateModelName = itemVisual.m_alternateModelName;
        this.m_Hue = itemVisual.m_Hue;
        this.m_Tint = itemVisual.m_Tint;
        this.m_BaseTexture = itemVisual.m_BaseTexture;
        this.m_TextureChoice = itemVisual.m_TextureChoice;
        this.m_Decal = itemVisual.m_Decal;
        this.copyBlood(itemVisual);
        this.copyHoles(itemVisual);
        this.copyPatches(itemVisual);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byte b = 0;
        if (this.m_Tint != null) {
            b |= 0x1;
        }
        if (this.m_BaseTexture != -1) {
            b |= 0x2;
        }
        if (this.m_TextureChoice != -1) {
            b |= 0x4;
        }
        if (this.m_Hue != Float.POSITIVE_INFINITY) {
            b |= 0x8;
        }
        if (!StringUtils.isNullOrWhitespace(this.m_Decal)) {
            b |= 0x10;
        }
        byteBuffer.put(b);
        GameWindow.WriteString(byteBuffer, this.m_fullType);
        GameWindow.WriteString(byteBuffer, this.m_alternateModelName);
        GameWindow.WriteString(byteBuffer, this.m_clothingItemName);
        if (this.m_Tint != null) {
            byteBuffer.put(this.m_Tint.getRedByte());
            byteBuffer.put(this.m_Tint.getGreenByte());
            byteBuffer.put(this.m_Tint.getBlueByte());
        }
        if (this.m_BaseTexture != -1) {
            byteBuffer.put((byte)this.m_BaseTexture);
        }
        if (this.m_TextureChoice != -1) {
            byteBuffer.put((byte)this.m_TextureChoice);
        }
        if (this.m_Hue != Float.POSITIVE_INFINITY) {
            byteBuffer.putFloat(this.m_Hue);
        }
        if (!StringUtils.isNullOrWhitespace(this.m_Decal)) {
            GameWindow.WriteString(byteBuffer, this.m_Decal);
        }
        if (this.blood != null) {
            byteBuffer.put((byte)this.blood.length);
            for (int i = 0; i < this.blood.length; ++i) {
                byteBuffer.put(this.blood[i]);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.dirt != null) {
            byteBuffer.put((byte)this.dirt.length);
            for (int j = 0; j < this.dirt.length; ++j) {
                byteBuffer.put(this.dirt[j]);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.holes != null) {
            byteBuffer.put((byte)this.holes.length);
            for (int k = 0; k < this.holes.length; ++k) {
                byteBuffer.put(this.holes[k]);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.basicPatches != null) {
            byteBuffer.put((byte)this.basicPatches.length);
            for (int l = 0; l < this.basicPatches.length; ++l) {
                byteBuffer.put(this.basicPatches[l]);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.denimPatches != null) {
            byteBuffer.put((byte)this.denimPatches.length);
            for (int n = 0; n < this.denimPatches.length; ++n) {
                byteBuffer.put(this.denimPatches[n]);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.leatherPatches != null) {
            byteBuffer.put((byte)this.leatherPatches.length);
            for (int n2 = 0; n2 < this.leatherPatches.length; ++n2) {
                byteBuffer.put(this.leatherPatches[n2]);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        final int n2 = byteBuffer.get() & 0xFF;
        if (n >= 164) {
            this.m_fullType = GameWindow.ReadString(byteBuffer);
            this.m_alternateModelName = GameWindow.ReadString(byteBuffer);
        }
        this.m_clothingItemName = GameWindow.ReadString(byteBuffer);
        if (n < 164) {
            this.m_fullType = ScriptManager.instance.getItemTypeForClothingItem(this.m_clothingItemName);
        }
        if ((n2 & 0x1) != 0x0) {
            this.m_Tint = new ImmutableColor(byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF);
        }
        if ((n2 & 0x2) != 0x0) {
            this.m_BaseTexture = byteBuffer.get();
        }
        if ((n2 & 0x4) != 0x0) {
            this.m_TextureChoice = byteBuffer.get();
        }
        if (n >= 146) {
            if ((n2 & 0x8) != 0x0) {
                this.m_Hue = byteBuffer.getFloat();
            }
            if ((n2 & 0x10) != 0x0) {
                this.m_Decal = GameWindow.ReadString(byteBuffer);
            }
        }
        final byte value = byteBuffer.get();
        if (value > 0 && this.blood == null) {
            this.blood = new byte[BloodBodyPartType.MAX.index()];
        }
        for (byte b = 0; b < value; ++b) {
            final byte value2 = byteBuffer.get();
            if (b < this.blood.length) {
                this.blood[b] = value2;
            }
        }
        if (n >= 163) {
            final byte value3 = byteBuffer.get();
            if (value3 > 0 && this.dirt == null) {
                this.dirt = new byte[BloodBodyPartType.MAX.index()];
            }
            for (byte b2 = 0; b2 < value3; ++b2) {
                final byte value4 = byteBuffer.get();
                if (b2 < this.dirt.length) {
                    this.dirt[b2] = value4;
                }
            }
        }
        final byte value5 = byteBuffer.get();
        if (value5 > 0 && this.holes == null) {
            this.holes = new byte[BloodBodyPartType.MAX.index()];
        }
        for (byte b3 = 0; b3 < value5; ++b3) {
            final byte value6 = byteBuffer.get();
            if (b3 < this.holes.length) {
                this.holes[b3] = value6;
            }
        }
        if (n >= 154) {
            final byte value7 = byteBuffer.get();
            if (value7 > 0 && this.basicPatches == null) {
                this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
            }
            for (byte b4 = 0; b4 < value7; ++b4) {
                final byte value8 = byteBuffer.get();
                if (b4 < this.basicPatches.length) {
                    this.basicPatches[b4] = value8;
                }
            }
        }
        if (n >= 155) {
            final byte value9 = byteBuffer.get();
            if (value9 > 0 && this.denimPatches == null) {
                this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
            }
            for (byte b5 = 0; b5 < value9; ++b5) {
                final byte value10 = byteBuffer.get();
                if (b5 < this.denimPatches.length) {
                    this.denimPatches[b5] = value10;
                }
            }
            final byte value11 = byteBuffer.get();
            if (value11 > 0 && this.leatherPatches == null) {
                this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
            }
            for (byte b6 = 0; b6 < value11; ++b6) {
                final byte value12 = byteBuffer.get();
                if (b6 < this.leatherPatches.length) {
                    this.leatherPatches[b6] = value12;
                }
            }
        }
    }
    
    @Override
    public ModelInstance createModelInstance() {
        return null;
    }
    
    public void setDenimPatch(final BloodBodyPartType bloodBodyPartType) {
        if (this.denimPatches == null) {
            this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
        }
        this.denimPatches[bloodBodyPartType.index()] = -1;
    }
    
    public float getDenimPatch(final BloodBodyPartType bloodBodyPartType) {
        if (this.denimPatches == null) {
            return 0.0f;
        }
        return (this.denimPatches[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public void setLeatherPatch(final BloodBodyPartType bloodBodyPartType) {
        if (this.leatherPatches == null) {
            this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
        }
        this.leatherPatches[bloodBodyPartType.index()] = -1;
    }
    
    public float getLeatherPatch(final BloodBodyPartType bloodBodyPartType) {
        if (this.leatherPatches == null) {
            return 0.0f;
        }
        return (this.leatherPatches[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public void setBasicPatch(final BloodBodyPartType bloodBodyPartType) {
        if (this.basicPatches == null) {
            this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
        }
        this.basicPatches[bloodBodyPartType.index()] = -1;
    }
    
    public float getBasicPatch(final BloodBodyPartType bloodBodyPartType) {
        if (this.basicPatches == null) {
            return 0.0f;
        }
        return (this.basicPatches[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public int getBasicPatchesNumber() {
        if (this.basicPatches == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < this.basicPatches.length; ++i) {
            if (this.basicPatches[i] != 0) {
                ++n;
            }
        }
        return n;
    }
    
    public void setHole(final BloodBodyPartType bloodBodyPartType) {
        if (this.holes == null) {
            this.holes = new byte[BloodBodyPartType.MAX.index()];
        }
        this.holes[bloodBodyPartType.index()] = -1;
    }
    
    public float getHole(final BloodBodyPartType bloodBodyPartType) {
        if (this.holes == null) {
            return 0.0f;
        }
        return (this.holes[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public int getHolesNumber() {
        if (this.holes == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < this.holes.length; ++i) {
            if (this.holes[i] != 0) {
                ++n;
            }
        }
        return n;
    }
    
    public void setBlood(final BloodBodyPartType bloodBodyPartType, float max) {
        if (this.blood == null) {
            this.blood = new byte[BloodBodyPartType.MAX.index()];
        }
        max = Math.max(0.0f, Math.min(1.0f, max));
        this.blood[bloodBodyPartType.index()] = (byte)(max * 255.0f);
    }
    
    public float getBlood(final BloodBodyPartType bloodBodyPartType) {
        if (this.blood == null) {
            return 0.0f;
        }
        return (this.blood[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public float getDirt(final BloodBodyPartType bloodBodyPartType) {
        if (this.dirt == null) {
            return 0.0f;
        }
        return (this.dirt[bloodBodyPartType.index()] & 0xFF) / 255.0f;
    }
    
    public void setDirt(final BloodBodyPartType bloodBodyPartType, float max) {
        if (this.dirt == null) {
            this.dirt = new byte[BloodBodyPartType.MAX.index()];
        }
        max = Math.max(0.0f, Math.min(1.0f, max));
        this.dirt[bloodBodyPartType.index()] = (byte)(max * 255.0f);
    }
    
    public void copyBlood(final ItemVisual itemVisual) {
        if (itemVisual.blood != null) {
            if (this.blood == null) {
                this.blood = new byte[BloodBodyPartType.MAX.index()];
            }
            System.arraycopy(itemVisual.blood, 0, this.blood, 0, this.blood.length);
        }
        else if (this.blood != null) {
            Arrays.fill(this.blood, (byte)0);
        }
    }
    
    public void copyDirt(final ItemVisual itemVisual) {
        if (itemVisual.dirt != null) {
            if (this.dirt == null) {
                this.dirt = new byte[BloodBodyPartType.MAX.index()];
            }
            System.arraycopy(itemVisual.dirt, 0, this.dirt, 0, this.dirt.length);
        }
        else if (this.dirt != null) {
            Arrays.fill(this.dirt, (byte)0);
        }
    }
    
    public void copyHoles(final ItemVisual itemVisual) {
        if (itemVisual.holes != null) {
            if (this.holes == null) {
                this.holes = new byte[BloodBodyPartType.MAX.index()];
            }
            System.arraycopy(itemVisual.holes, 0, this.holes, 0, this.holes.length);
        }
        else if (this.holes != null) {
            Arrays.fill(this.holes, (byte)0);
        }
    }
    
    public void copyPatches(final ItemVisual itemVisual) {
        if (itemVisual.basicPatches != null) {
            if (this.basicPatches == null) {
                this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
            }
            System.arraycopy(itemVisual.basicPatches, 0, this.basicPatches, 0, this.basicPatches.length);
        }
        else if (this.basicPatches != null) {
            Arrays.fill(this.basicPatches, (byte)0);
        }
        if (itemVisual.denimPatches != null) {
            if (this.denimPatches == null) {
                this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
            }
            System.arraycopy(itemVisual.denimPatches, 0, this.denimPatches, 0, this.denimPatches.length);
        }
        else if (this.denimPatches != null) {
            Arrays.fill(this.denimPatches, (byte)0);
        }
        if (itemVisual.leatherPatches != null) {
            if (this.leatherPatches == null) {
                this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
            }
            System.arraycopy(itemVisual.leatherPatches, 0, this.leatherPatches, 0, this.leatherPatches.length);
        }
        else if (this.leatherPatches != null) {
            Arrays.fill(this.leatherPatches, (byte)0);
        }
    }
    
    public void removeHole(final int n) {
        if (this.holes != null) {
            this.holes[n] = 0;
        }
    }
    
    public void removePatch(final int n) {
        if (this.basicPatches != null) {
            this.basicPatches[n] = 0;
        }
        if (this.denimPatches != null) {
            this.denimPatches[n] = 0;
        }
        if (this.leatherPatches != null) {
            this.leatherPatches[n] = 0;
        }
    }
    
    public void removeBlood() {
        if (this.blood != null) {
            Arrays.fill(this.blood, (byte)0);
        }
    }
    
    public void removeDirt() {
        if (this.dirt != null) {
            Arrays.fill(this.dirt, (byte)0);
        }
    }
    
    public float getTotalBlood() {
        float n = 0.0f;
        if (this.blood != null) {
            for (int i = 0; i < this.blood.length; ++i) {
                n += (this.blood[i] & 0xFF) / 255.0f;
            }
        }
        return n;
    }
    
    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }
    
    public void setInventoryItem(final InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }
    
    public void setBaseTexture(final int baseTexture) {
        this.m_BaseTexture = baseTexture;
    }
    
    public int getBaseTexture() {
        return this.m_BaseTexture;
    }
    
    public void setTextureChoice(final int textureChoice) {
        this.m_TextureChoice = textureChoice;
    }
    
    public int getTextureChoice() {
        return this.m_TextureChoice;
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
        if (this.getScriptItem() == null) {
            return null;
        }
        final ClothingItem clothingItem = this.getClothingItem();
        if (clothingItem == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("version=");
        sb.append(1);
        sb.append(";");
        sb.append("type=");
        sb.append(this.inventoryItem.getFullType());
        sb.append(";");
        final ImmutableColor tint = this.getTint(clothingItem);
        sb.append("tint=");
        toString(tint, sb);
        sb.append(";");
        final int baseTexture = this.getBaseTexture();
        if (baseTexture != -1) {
            sb.append("baseTexture=");
            sb.append(baseTexture);
            sb.append(";");
        }
        final int textureChoice = this.getTextureChoice();
        if (textureChoice != -1) {
            sb.append("textureChoice=");
            sb.append(textureChoice);
            sb.append(";");
        }
        final float hue = this.getHue(clothingItem);
        if (hue != 0.0f) {
            sb.append("hue=");
            sb.append(hue);
            sb.append(";");
        }
        final String decal = this.getDecal(clothingItem);
        if (!StringUtils.isNullOrWhitespace(decal)) {
            sb.append("decal=");
            sb.append(decal);
            sb.append(";");
        }
        return sb.toString();
    }
    
    public static InventoryItem createLastStandItem(String trim) {
        trim = trim.trim();
        if (StringUtils.isNullOrWhitespace(trim) || !trim.startsWith("version=")) {
            return null;
        }
        InventoryItem createItem = null;
        ItemVisual visual = null;
        final String[] split = trim.split(";");
        if (split.length < 2 || !split[1].trim().startsWith("type=")) {
            return null;
        }
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
                            return null;
                        }
                        break;
                    }
                    case "baseTexture": {
                        try {
                            visual.setBaseTexture(Integer.parseInt(trim3));
                        }
                        catch (NumberFormatException ex) {}
                        break;
                    }
                    case "decal": {
                        if (!StringUtils.isNullOrWhitespace(trim3)) {
                            visual.setDecal(trim3);
                            break;
                        }
                        break;
                    }
                    case "hue": {
                        try {
                            visual.setHue(Float.parseFloat(trim3));
                        }
                        catch (NumberFormatException ex2) {}
                        break;
                    }
                    case "textureChoice": {
                        try {
                            visual.setTextureChoice(Integer.parseInt(trim3));
                        }
                        catch (NumberFormatException ex3) {}
                        break;
                    }
                    case "tint": {
                        final ImmutableColor colorFromString = colorFromString(trim3);
                        if (colorFromString != null) {
                            visual.setTint(colorFromString);
                            break;
                        }
                        break;
                    }
                    case "type": {
                        createItem = InventoryItemFactory.CreateItem(trim3);
                        if (createItem == null) {
                            return null;
                        }
                        visual = createItem.getVisual();
                        if (visual == null) {
                            return null;
                        }
                        break;
                    }
                }
            }
        }
        return createItem;
    }
}
