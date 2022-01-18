// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.util.list.PZArrayUtil;
import zombie.util.StringUtils;
import java.util.List;
import zombie.core.ImmutableColor;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

public class ClothingItemReference implements Cloneable
{
    public float probability;
    public String itemGUID;
    public ArrayList<ClothingItemReference> subItems;
    public boolean bRandomized;
    @XmlTransient
    public boolean m_Immutable;
    @XmlTransient
    public final RandomData RandomData;
    
    public ClothingItemReference() {
        this.probability = 1.0f;
        this.subItems = new ArrayList<ClothingItemReference>();
        this.bRandomized = false;
        this.m_Immutable = false;
        this.RandomData = new RandomData();
    }
    
    public void setModID(final String modID) {
        this.itemGUID = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modID, this.itemGUID);
        final Iterator<ClothingItemReference> iterator = this.subItems.iterator();
        while (iterator.hasNext()) {
            iterator.next().setModID(modID);
        }
    }
    
    public ClothingItem getClothingItem() {
        String s = this.itemGUID;
        if (!this.bRandomized) {
            throw new RuntimeException("not randomized yet");
        }
        if (this.RandomData.m_PickedItemRef != null) {
            s = this.RandomData.m_PickedItemRef.itemGUID;
        }
        return OutfitManager.instance.getClothingItem(s);
    }
    
    public void randomize() {
        if (this.m_Immutable) {
            throw new RuntimeException("trying to randomize an immutable ClothingItemReference");
        }
        this.RandomData.reset();
        for (int i = 0; i < this.subItems.size(); ++i) {
            this.subItems.get(i).randomize();
        }
        this.RandomData.m_PickedItemRef = this.pickRandomItemInternal();
        this.bRandomized = true;
        final ClothingItem clothingItem = this.getClothingItem();
        if (clothingItem == null) {
            this.RandomData.m_Active = false;
            return;
        }
        this.RandomData.m_Active = (OutfitRNG.Next(0.0f, 1.0f) <= this.probability);
        if (clothingItem.m_AllowRandomHue) {
            this.RandomData.m_Hue = OutfitRNG.Next(200) / 100.0f - 1.0f;
        }
        if (clothingItem.m_AllowRandomTint) {
            this.RandomData.m_Tint = OutfitRNG.randomImmutableColor();
        }
        else {
            this.RandomData.m_Tint = ImmutableColor.white;
        }
        this.RandomData.m_BaseTexture = OutfitRNG.pickRandom(clothingItem.m_BaseTextures);
        this.RandomData.m_TextureChoice = OutfitRNG.pickRandom(clothingItem.textureChoices);
        if (!StringUtils.isNullOrWhitespace(clothingItem.m_DecalGroup)) {
            this.RandomData.m_Decal = ClothingDecals.instance.getRandomDecal(clothingItem.m_DecalGroup);
        }
    }
    
    private ClothingItemReference pickRandomItemInternal() {
        if (this.subItems.isEmpty()) {
            return this;
        }
        final int next = OutfitRNG.Next(this.subItems.size() + 1);
        if (next == 0) {
            return this;
        }
        return this.subItems.get(next - 1).RandomData.m_PickedItemRef;
    }
    
    public ClothingItemReference clone() {
        try {
            final ClothingItemReference clothingItemReference = new ClothingItemReference();
            clothingItemReference.probability = this.probability;
            clothingItemReference.itemGUID = this.itemGUID;
            PZArrayUtil.copy(clothingItemReference.subItems, this.subItems, ClothingItemReference::clone);
            return clothingItemReference;
        }
        catch (CloneNotSupportedException cause) {
            throw new RuntimeException("ClothingItemReference clone failed.", cause);
        }
    }
    
    public static class RandomData
    {
        public boolean m_Active;
        public float m_Hue;
        public ImmutableColor m_Tint;
        public String m_BaseTexture;
        public ClothingItemReference m_PickedItemRef;
        public String m_TextureChoice;
        public String m_Decal;
        
        public RandomData() {
            this.m_Active = true;
            this.m_Hue = 0.0f;
            this.m_Tint = ImmutableColor.white;
        }
        
        public void reset() {
            this.m_Active = true;
            this.m_Hue = 0.0f;
            this.m_Tint = ImmutableColor.white;
            this.m_BaseTexture = null;
            this.m_PickedItemRef = null;
            this.m_TextureChoice = null;
            this.m_Decal = null;
        }
    }
}
