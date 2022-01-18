// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.characters.BodyDamage.Thermoregulator;
import zombie.characters.BodyDamage.BodyPart;
import zombie.core.math.PZMath;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.GameTime;
import zombie.ZomboidGlobals;
import zombie.inventory.types.Clothing;
import zombie.inventory.InventoryItem;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.characterTextures.BloodBodyPartType;
import java.util.ArrayList;
import zombie.core.skinnedmodel.visual.ItemVisuals;

public final class ClothingWetness
{
    private static final ItemVisuals itemVisuals;
    private static final ArrayList<BloodBodyPartType> coveredParts;
    public final IsoGameCharacter character;
    public final ItemList[] clothing;
    public final int[] perspiringParts;
    public boolean changed;
    
    public ClothingWetness(final IsoGameCharacter character) {
        this.clothing = new ItemList[BloodBodyPartType.MAX.index()];
        this.perspiringParts = new int[BloodBodyPartType.MAX.index()];
        this.changed = true;
        this.character = character;
        for (int i = 0; i < this.clothing.length; ++i) {
            this.clothing[i] = new ItemList();
        }
    }
    
    public void calculateExposedItems() {
        for (int i = 0; i < this.clothing.length; ++i) {
            this.clothing[i].clear();
        }
        this.character.getItemVisuals(ClothingWetness.itemVisuals);
        for (int j = ClothingWetness.itemVisuals.size() - 1; j >= 0; --j) {
            final InventoryItem inventoryItem = ClothingWetness.itemVisuals.get(j).getInventoryItem();
            final ArrayList<BloodClothingType> bloodClothingType = inventoryItem.getBloodClothingType();
            if (bloodClothingType != null) {
                ClothingWetness.coveredParts.clear();
                BloodClothingType.getCoveredParts(bloodClothingType, ClothingWetness.coveredParts);
                for (int k = 0; k < ClothingWetness.coveredParts.size(); ++k) {
                    this.clothing[ClothingWetness.coveredParts.get(k).index()].add(inventoryItem);
                }
            }
        }
    }
    
    public void updateWetness(final float n, final float n2) {
        boolean b = false;
        final InventoryItem primaryHandItem = this.character.getPrimaryHandItem();
        if (primaryHandItem != null && primaryHandItem.isProtectFromRainWhileEquipped()) {
            b = true;
        }
        final InventoryItem secondaryHandItem = this.character.getSecondaryHandItem();
        if (secondaryHandItem != null && secondaryHandItem.isProtectFromRainWhileEquipped()) {
            b = true;
        }
        if (this.changed) {
            this.changed = false;
            this.calculateExposedItems();
        }
        this.character.getItemVisuals(ClothingWetness.itemVisuals);
        for (int i = 0; i < ClothingWetness.itemVisuals.size(); ++i) {
            final InventoryItem inventoryItem = ClothingWetness.itemVisuals.get(i).getInventoryItem();
            if (inventoryItem instanceof Clothing) {
                if (inventoryItem.getBloodClothingType() == null) {
                    ((Clothing)inventoryItem).updateWetness(true);
                }
                else {
                    ((Clothing)inventoryItem).flushWetness();
                }
            }
        }
        final float n3 = (float)ZomboidGlobals.WetnessIncrease * GameTime.instance.getMultiplier();
        final float n4 = (float)ZomboidGlobals.WetnessDecrease * GameTime.instance.getMultiplier();
        for (int j = 0; j < this.clothing.length; ++j) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(j);
            final BodyPartType fromIndex2 = BodyPartType.FromIndex(j);
            if (fromIndex2 != BodyPartType.MAX) {
                final BodyPart bodyPart = this.character.getBodyDamage().getBodyPart(fromIndex2);
                final Thermoregulator.ThermalNode nodeForBloodType = this.character.getBodyDamage().getThermoregulator().getNodeForBloodType(fromIndex);
                if (bodyPart != null) {
                    if (nodeForBloodType != null) {
                        final float n5 = 0.0f;
                        final float clamp = PZMath.clamp(nodeForBloodType.getSecondaryDelta(), 0.0f, 1.0f);
                        final float n6 = clamp * clamp * (0.2f + 0.8f * (1.0f - nodeForBloodType.getDistToCore()));
                        float n7;
                        if (n6 > 0.1f) {
                            n7 = n5 + n6;
                        }
                        else {
                            final float n8 = (nodeForBloodType.getSkinCelcius() - 20.0f) / 22.0f;
                            n7 = n5 - Math.max(0.0f, n8 * n8 - n);
                            if (n > 0.0f) {
                                n7 = 0.0f;
                            }
                        }
                        this.perspiringParts[j] = ((n7 > 0.0f) ? 1 : 0);
                        if (n7 != 0.0f) {
                            float n9;
                            if (n7 > 0.0f) {
                                n9 = n7 * n3;
                            }
                            else {
                                n9 = n7 * n4;
                            }
                            bodyPart.setWetness(bodyPart.getWetness() + n9);
                            if (n9 <= 0.0f || bodyPart.getWetness() >= 25.0f) {
                                if (n9 >= 0.0f || bodyPart.getWetness() <= 50.0f) {
                                    if (n9 > 0.0f) {
                                        n9 *= 0.4f + 0.6f * (PZMath.clamp(this.character.getBodyDamage().getThermoregulator().getExternalAirTemperature() + 10.0f, 0.0f, 20.0f) / 20.0f);
                                    }
                                    boolean b2 = false;
                                    boolean b3 = false;
                                    boolean b4 = false;
                                    for (int k = this.clothing[j].size() - 1; k >= 0; --k) {
                                        if (n9 > 0.0f) {
                                            final int[] perspiringParts = this.perspiringParts;
                                            final int n10 = j;
                                            ++perspiringParts[n10];
                                        }
                                        final InventoryItem inventoryItem2 = this.clothing[j].get(k);
                                        if (inventoryItem2 instanceof Clothing) {
                                            float max = 1.0f;
                                            final Clothing clothing = (Clothing)inventoryItem2;
                                            final ItemVisual visual = clothing.getVisual();
                                            if (visual != null) {
                                                if (visual.getHole(fromIndex) > 0.0f) {
                                                    b2 = true;
                                                    continue;
                                                }
                                                if (n9 > 0.0f && clothing.getWetness() >= 100.0f) {
                                                    b3 = true;
                                                    continue;
                                                }
                                                if (n9 < 0.0f && clothing.getWetness() <= 0.0f) {
                                                    b4 = true;
                                                    continue;
                                                }
                                                if (n9 > 0.0f && clothing.getWaterResistance() > 0.0f) {
                                                    max = PZMath.max(0.0f, 1.0f - clothing.getWaterResistance());
                                                    if (max <= 0.0f) {
                                                        final int[] perspiringParts2 = this.perspiringParts;
                                                        final int n11 = j;
                                                        --perspiringParts2[n11];
                                                        break;
                                                    }
                                                }
                                            }
                                            ClothingWetness.coveredParts.clear();
                                            BloodClothingType.getCoveredParts(inventoryItem2.getBloodClothingType(), ClothingWetness.coveredParts);
                                            ClothingWetness.coveredParts.size();
                                            float n12 = n9;
                                            if (n12 > 0.0f) {
                                                n12 *= max;
                                            }
                                            if (b2 || b3 || b4) {
                                                n12 /= 2.0f;
                                            }
                                            clothing.setWetness(clothing.getWetness() + n12);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int l = 0; l < this.clothing.length; ++l) {
            final BloodBodyPartType fromIndex3 = BloodBodyPartType.FromIndex(l);
            final BodyPartType fromIndex4 = BodyPartType.FromIndex(l);
            if (fromIndex4 != BodyPartType.MAX) {
                final BodyPart bodyPart2 = this.character.getBodyDamage().getBodyPart(fromIndex4);
                final Thermoregulator.ThermalNode nodeForBloodType2 = this.character.getBodyDamage().getThermoregulator().getNodeForBloodType(fromIndex3);
                if (bodyPart2 != null) {
                    if (nodeForBloodType2 != null) {
                        float n13 = 100.0f;
                        if (b) {
                            n13 = 100.0f * BodyPartType.GetUmbrellaMod(fromIndex4);
                        }
                        final float n14 = 0.0f;
                        float n15;
                        if (n > 0.0f) {
                            n15 = n * n3;
                        }
                        else {
                            n15 = n14 - n2 * n4;
                        }
                        boolean b5 = false;
                        boolean b6 = false;
                        boolean b7 = false;
                        float n16 = 2.0f;
                        for (int index = 0; index < this.clothing[l].size(); ++index) {
                            final int n17 = 1 + (this.clothing[l].size() - index);
                            float max2 = 1.0f;
                            final InventoryItem inventoryItem3 = this.clothing[l].get(index);
                            if (inventoryItem3 instanceof Clothing) {
                                final Clothing clothing2 = (Clothing)inventoryItem3;
                                final ItemVisual visual2 = clothing2.getVisual();
                                if (visual2 != null) {
                                    if (visual2.getHole(fromIndex3) > 0.0f) {
                                        b5 = true;
                                        continue;
                                    }
                                    if (n15 > 0.0f && clothing2.getWetness() >= 100.0f) {
                                        b6 = true;
                                        continue;
                                    }
                                    if (n15 < 0.0f && clothing2.getWetness() <= 0.0f) {
                                        b7 = true;
                                        continue;
                                    }
                                    if (n15 > 0.0f && clothing2.getWaterResistance() > 0.0f) {
                                        max2 = PZMath.max(0.0f, 1.0f - clothing2.getWaterResistance());
                                        if (max2 <= 0.0f) {
                                            break;
                                        }
                                    }
                                }
                                ClothingWetness.coveredParts.clear();
                                BloodClothingType.getCoveredParts(inventoryItem3.getBloodClothingType(), ClothingWetness.coveredParts);
                                final int size = ClothingWetness.coveredParts.size();
                                float n18 = n15;
                                if (n18 > 0.0f) {
                                    n18 *= max2;
                                }
                                float n19 = n18 / size;
                                if (b5 || b6 || b7) {
                                    n19 /= n16;
                                }
                                if ((n15 < 0.0f && n17 > this.perspiringParts[l]) || (n15 > 0.0f && clothing2.getWetness() <= n13)) {
                                    clothing2.setWetness(clothing2.getWetness() + n19);
                                }
                                if (n15 > 0.0f) {
                                    break;
                                }
                                if (b7) {
                                    n16 *= 2.0f;
                                }
                            }
                        }
                        if (this.clothing[l].isEmpty()) {
                            if ((n15 < 0.0f && this.perspiringParts[l] == 0) || bodyPart2.getWetness() <= n13) {
                                bodyPart2.setWetness(bodyPart2.getWetness() + n15);
                            }
                        }
                        else {
                            final InventoryItem inventoryItem4 = this.clothing[l].get(this.clothing[l].size() - 1);
                            if (inventoryItem4 instanceof Clothing) {
                                final Clothing clothing3 = (Clothing)inventoryItem4;
                                if (n15 > 0.0f && this.perspiringParts[l] == 0 && clothing3.getWetness() >= 50.0f && bodyPart2.getWetness() <= n13) {
                                    bodyPart2.setWetness(bodyPart2.getWetness() + n15 / 2.0f);
                                }
                                if (n15 < 0.0f && this.perspiringParts[l] == 0 && clothing3.getWetness() <= 50.0f) {
                                    bodyPart2.setWetness(bodyPart2.getWetness() + n15 / 2.0f);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Deprecated
    public void increaseWetness(final float n) {
        if (n <= 0.0f) {
            return;
        }
        if (this.changed) {
            this.changed = false;
            this.calculateExposedItems();
        }
        this.character.getItemVisuals(ClothingWetness.itemVisuals);
        for (int i = 0; i < ClothingWetness.itemVisuals.size(); ++i) {
            final InventoryItem inventoryItem = ClothingWetness.itemVisuals.get(i).getInventoryItem();
            if (inventoryItem instanceof Clothing) {
                ((Clothing)inventoryItem).flushWetness();
            }
        }
        int n2 = 0;
        for (int j = 0; j < this.clothing.length; ++j) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(j);
            boolean b = false;
            boolean b2 = false;
            for (int k = 0; k < this.clothing[j].size(); ++k) {
                final InventoryItem inventoryItem2 = this.clothing[j].get(k);
                if (inventoryItem2 instanceof Clothing) {
                    final Clothing clothing = (Clothing)inventoryItem2;
                    final ItemVisual visual = clothing.getVisual();
                    if (visual != null) {
                        if (visual.getHole(fromIndex) > 0.0f) {
                            b = true;
                            continue;
                        }
                        if (clothing.getWetness() >= 100.0f) {
                            b2 = true;
                            continue;
                        }
                    }
                    ClothingWetness.coveredParts.clear();
                    BloodClothingType.getCoveredParts(inventoryItem2.getBloodClothingType(), ClothingWetness.coveredParts);
                    float n3 = n / ClothingWetness.coveredParts.size();
                    if (b || b2) {
                        n3 /= 2.0f;
                    }
                    clothing.setWetness(clothing.getWetness() + n3);
                    break;
                }
            }
            if (this.clothing[j].isEmpty()) {
                ++n2;
            }
            else {
                final InventoryItem inventoryItem3 = this.clothing[j].get(this.clothing[j].size() - 1);
                if (inventoryItem3 instanceof Clothing && ((Clothing)inventoryItem3).getWetness() >= 100.0f) {
                    ++n2;
                }
            }
        }
        if (n2 > 0) {
            this.character.getBodyDamage().setWetness(this.character.getBodyDamage().getWetness() + n * (n2 / (float)this.clothing.length));
        }
    }
    
    @Deprecated
    public void decreaseWetness(final float n) {
        if (n <= 0.0f) {
            return;
        }
        if (this.changed) {
            this.changed = false;
            this.calculateExposedItems();
        }
        this.character.getItemVisuals(ClothingWetness.itemVisuals);
        for (int i = ClothingWetness.itemVisuals.size() - 1; i >= 0; --i) {
            final InventoryItem inventoryItem = ClothingWetness.itemVisuals.get(i).getInventoryItem();
            if (inventoryItem instanceof Clothing) {
                final Clothing clothing = (Clothing)inventoryItem;
                if (clothing.getWetness() > 0.0f) {
                    clothing.setWetness(clothing.getWetness() - n);
                }
            }
        }
    }
    
    static {
        itemVisuals = new ItemVisuals();
        coveredParts = new ArrayList<BloodBodyPartType>();
    }
    
    private static final class ItemList extends ArrayList<InventoryItem>
    {
    }
}
