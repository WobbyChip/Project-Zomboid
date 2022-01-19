// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.core.ImmutableColor;
import java.util.Collection;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;
import java.util.List;
import zombie.characters.HairOutfitDefinitions;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

public class Outfit implements Cloneable
{
    public String m_Name;
    public boolean m_Top;
    public boolean m_Pants;
    public final ArrayList<String> m_TopTextures;
    public final ArrayList<String> m_PantsTextures;
    public final ArrayList<ClothingItemReference> m_items;
    public boolean m_AllowPantsHue;
    public boolean m_AllowPantsTint;
    public boolean m_AllowTopTint;
    public boolean m_AllowTShirtDecal;
    @XmlTransient
    public String m_modID;
    @XmlTransient
    public boolean m_Immutable;
    @XmlTransient
    public final RandomData RandomData;
    
    public Outfit() {
        this.m_Name = "Outfit";
        this.m_Top = true;
        this.m_Pants = true;
        this.m_TopTextures = new ArrayList<String>();
        this.m_PantsTextures = new ArrayList<String>();
        this.m_items = new ArrayList<ClothingItemReference>();
        this.m_AllowPantsHue = true;
        this.m_AllowPantsTint = false;
        this.m_AllowTopTint = true;
        this.m_AllowTShirtDecal = true;
        this.m_Immutable = false;
        this.RandomData = new RandomData();
    }
    
    public void setModID(final String s) {
        this.m_modID = s;
        final Iterator<ClothingItemReference> iterator = this.m_items.iterator();
        while (iterator.hasNext()) {
            iterator.next().setModID(s);
        }
    }
    
    public void AddItem(final ClothingItemReference e) {
        this.m_items.add(e);
    }
    
    public void Randomize() {
        if (this.m_Immutable) {
            throw new RuntimeException("trying to randomize an immutable Outfit");
        }
        for (int i = 0; i < this.m_items.size(); ++i) {
            this.m_items.get(i).randomize();
        }
        this.RandomData.m_hairColor = HairOutfitDefinitions.instance.getRandomHaircutColor(this.m_Name);
        this.RandomData.m_femaleHairName = HairStyles.instance.getRandomFemaleStyle(this.m_Name);
        this.RandomData.m_maleHairName = HairStyles.instance.getRandomMaleStyle(this.m_Name);
        this.RandomData.m_beardName = BeardStyles.instance.getRandomStyle(this.m_Name);
        this.RandomData.m_topTint = OutfitRNG.randomImmutableColor();
        this.RandomData.m_pantsTint = OutfitRNG.randomImmutableColor();
        if (OutfitRNG.Next(4) == 0) {
            this.RandomData.m_pantsHue = OutfitRNG.Next(200) / 100.0f - 1.0f;
        }
        else {
            this.RandomData.m_pantsHue = 0.0f;
        }
        this.RandomData.m_hasTop = (OutfitRNG.Next(16) != 0);
        this.RandomData.m_hasTShirt = (OutfitRNG.Next(2) == 0);
        this.RandomData.m_hasTShirtDecal = (OutfitRNG.Next(4) == 0);
        if (this.m_Top) {
            this.RandomData.m_hasTop = true;
        }
        this.RandomData.m_topTexture = OutfitRNG.pickRandom(this.m_TopTextures);
        this.RandomData.m_pantsTexture = OutfitRNG.pickRandom(this.m_PantsTextures);
    }
    
    public void randomizeItem(final String anObject) {
        final ClothingItemReference clothingItemReference2 = PZArrayUtil.find(this.m_items, clothingItemReference -> clothingItemReference.itemGUID.equals(anObject));
        if (clothingItemReference2 != null) {
            clothingItemReference2.randomize();
        }
        else {
            DebugLog.Clothing.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
        }
    }
    
    public CharacterMask GetMask() {
        final CharacterMask characterMask = new CharacterMask();
        for (int i = 0; i < this.m_items.size(); ++i) {
            final ClothingItemReference clothingItemReference = this.m_items.get(i);
            if (clothingItemReference.RandomData.m_Active) {
                ClothingItem.tryGetCombinedMask(clothingItemReference, characterMask);
            }
        }
        return characterMask;
    }
    
    public boolean containsItemGuid(final String anObject) {
        boolean b = false;
        for (int i = 0; i < this.m_items.size(); ++i) {
            if (this.m_items.get(i).itemGUID.equals(anObject)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public ClothingItemReference findItemByGUID(final String anObject) {
        for (int i = 0; i < this.m_items.size(); ++i) {
            final ClothingItemReference clothingItemReference = this.m_items.get(i);
            if (clothingItemReference.itemGUID.equals(anObject)) {
                return clothingItemReference;
            }
        }
        return null;
    }
    
    public Outfit clone() {
        try {
            final Outfit outfit = new Outfit();
            outfit.m_Name = this.m_Name;
            outfit.m_Top = this.m_Top;
            outfit.m_Pants = this.m_Pants;
            outfit.m_PantsTextures.addAll(this.m_PantsTextures);
            outfit.m_TopTextures.addAll(this.m_TopTextures);
            PZArrayUtil.copy(outfit.m_items, this.m_items, ClothingItemReference::clone);
            outfit.m_AllowPantsHue = this.m_AllowPantsHue;
            outfit.m_AllowPantsTint = this.m_AllowPantsTint;
            outfit.m_AllowTopTint = this.m_AllowTopTint;
            outfit.m_AllowTShirtDecal = this.m_AllowTShirtDecal;
            return outfit;
        }
        catch (CloneNotSupportedException cause) {
            throw new RuntimeException("Outfit clone failed.", cause);
        }
    }
    
    public ClothingItemReference findHat() {
        for (final ClothingItemReference clothingItemReference : this.m_items) {
            if (!clothingItemReference.RandomData.m_Active) {
                continue;
            }
            final ClothingItem clothingItem = clothingItemReference.getClothingItem();
            if (clothingItem == null) {
                continue;
            }
            if (clothingItem.isHat()) {
                return clothingItemReference;
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < this.m_items.size(); ++i) {
            final ClothingItemReference clothingItemReference = this.m_items.get(i);
            final ClothingItem clothingItem = OutfitManager.instance.getClothingItem(clothingItemReference.itemGUID);
            if (clothingItem != null && clothingItem.isEmpty()) {
                return true;
            }
            for (int j = 0; j < clothingItemReference.subItems.size(); ++j) {
                final ClothingItem clothingItem2 = OutfitManager.instance.getClothingItem(clothingItemReference.subItems.get(j).itemGUID);
                if (clothingItem2 != null && clothingItem2.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void loadItems() {
        for (int i = 0; i < this.m_items.size(); ++i) {
            final ClothingItemReference clothingItemReference = this.m_items.get(i);
            OutfitManager.instance.getClothingItem(clothingItemReference.itemGUID);
            for (int j = 0; j < clothingItemReference.subItems.size(); ++j) {
                OutfitManager.instance.getClothingItem(clothingItemReference.subItems.get(j).itemGUID);
            }
        }
    }
    
    public static class RandomData
    {
        public ImmutableColor m_hairColor;
        public String m_maleHairName;
        public String m_femaleHairName;
        public String m_beardName;
        public ImmutableColor m_topTint;
        public ImmutableColor m_pantsTint;
        public float m_pantsHue;
        public boolean m_hasTop;
        public boolean m_hasTShirt;
        public boolean m_hasTShirtDecal;
        public String m_topTexture;
        public String m_pantsTexture;
    }
}
