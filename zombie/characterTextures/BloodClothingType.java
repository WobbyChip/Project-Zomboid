// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characterTextures;

import zombie.scripting.objects.Item;
import zombie.util.Type;
import zombie.inventory.types.Clothing;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public enum BloodClothingType
{
    Jacket, 
    LongJacket, 
    Trousers, 
    ShortsShort, 
    Shirt, 
    ShirtLongSleeves, 
    ShirtNoSleeves, 
    Jumper, 
    JumperNoSleeves, 
    Shoes, 
    FullHelmet, 
    Apron, 
    Bag, 
    Hands, 
    Head, 
    Neck, 
    UpperBody, 
    LowerBody, 
    LowerLegs, 
    UpperLegs, 
    LowerArms, 
    UpperArms, 
    Groin;
    
    private static HashMap<BloodClothingType, ArrayList<BloodBodyPartType>> coveredParts;
    private static final ArrayList<BloodBodyPartType> bodyParts;
    
    public static BloodClothingType fromString(final String anObject) {
        if (BloodClothingType.Jacket.toString().equals(anObject)) {
            return BloodClothingType.Jacket;
        }
        if (BloodClothingType.LongJacket.toString().equals(anObject)) {
            return BloodClothingType.LongJacket;
        }
        if (BloodClothingType.Trousers.toString().equals(anObject)) {
            return BloodClothingType.Trousers;
        }
        if (BloodClothingType.ShortsShort.toString().equals(anObject)) {
            return BloodClothingType.ShortsShort;
        }
        if (BloodClothingType.Shirt.toString().equals(anObject)) {
            return BloodClothingType.Shirt;
        }
        if (BloodClothingType.ShirtLongSleeves.toString().equals(anObject)) {
            return BloodClothingType.ShirtLongSleeves;
        }
        if (BloodClothingType.ShirtNoSleeves.toString().equals(anObject)) {
            return BloodClothingType.ShirtNoSleeves;
        }
        if (BloodClothingType.Jumper.toString().equals(anObject)) {
            return BloodClothingType.Jumper;
        }
        if (BloodClothingType.JumperNoSleeves.toString().equals(anObject)) {
            return BloodClothingType.JumperNoSleeves;
        }
        if (BloodClothingType.Shoes.toString().equals(anObject)) {
            return BloodClothingType.Shoes;
        }
        if (BloodClothingType.FullHelmet.toString().equals(anObject)) {
            return BloodClothingType.FullHelmet;
        }
        if (BloodClothingType.Bag.toString().equals(anObject)) {
            return BloodClothingType.Bag;
        }
        if (BloodClothingType.Hands.toString().equals(anObject)) {
            return BloodClothingType.Hands;
        }
        if (BloodClothingType.Head.toString().equals(anObject)) {
            return BloodClothingType.Head;
        }
        if (BloodClothingType.Neck.toString().equals(anObject)) {
            return BloodClothingType.Neck;
        }
        if (BloodClothingType.Apron.toString().equals(anObject)) {
            return BloodClothingType.Apron;
        }
        if (BloodClothingType.Bag.toString().equals(anObject)) {
            return BloodClothingType.Bag;
        }
        if (BloodClothingType.Hands.toString().equals(anObject)) {
            return BloodClothingType.Hands;
        }
        if (BloodClothingType.Head.toString().equals(anObject)) {
            return BloodClothingType.Head;
        }
        if (BloodClothingType.Neck.toString().equals(anObject)) {
            return BloodClothingType.Neck;
        }
        if (BloodClothingType.UpperBody.toString().equals(anObject)) {
            return BloodClothingType.UpperBody;
        }
        if (BloodClothingType.LowerBody.toString().equals(anObject)) {
            return BloodClothingType.LowerBody;
        }
        if (BloodClothingType.LowerLegs.toString().equals(anObject)) {
            return BloodClothingType.LowerLegs;
        }
        if (BloodClothingType.UpperLegs.toString().equals(anObject)) {
            return BloodClothingType.UpperLegs;
        }
        if (BloodClothingType.LowerArms.toString().equals(anObject)) {
            return BloodClothingType.LowerArms;
        }
        if (BloodClothingType.UpperArms.toString().equals(anObject)) {
            return BloodClothingType.UpperArms;
        }
        if (BloodClothingType.Groin.toString().equals(anObject)) {
            return BloodClothingType.Groin;
        }
        return null;
    }
    
    private static void init() {
        if (BloodClothingType.coveredParts == null) {
            BloodClothingType.coveredParts = new HashMap<BloodClothingType, ArrayList<BloodBodyPartType>>();
            final ArrayList<BloodBodyPartType> value = new ArrayList<BloodBodyPartType>();
            value.add(BloodBodyPartType.Torso_Upper);
            value.add(BloodBodyPartType.Torso_Lower);
            value.add(BloodBodyPartType.UpperLeg_L);
            value.add(BloodBodyPartType.UpperLeg_R);
            BloodClothingType.coveredParts.put(BloodClothingType.Apron, value);
            final ArrayList<BloodBodyPartType> c = new ArrayList<BloodBodyPartType>();
            c.add(BloodBodyPartType.Torso_Upper);
            c.add(BloodBodyPartType.Torso_Lower);
            c.add(BloodBodyPartType.Back);
            BloodClothingType.coveredParts.put(BloodClothingType.ShirtNoSleeves, c);
            BloodClothingType.coveredParts.put(BloodClothingType.JumperNoSleeves, c);
            final ArrayList<BloodBodyPartType> list = new ArrayList<BloodBodyPartType>();
            list.addAll(c);
            list.add(BloodBodyPartType.UpperArm_L);
            list.add(BloodBodyPartType.UpperArm_R);
            BloodClothingType.coveredParts.put(BloodClothingType.Shirt, list);
            final ArrayList<BloodBodyPartType> list2 = new ArrayList<BloodBodyPartType>();
            list2.addAll(list);
            list2.add(BloodBodyPartType.ForeArm_L);
            list2.add(BloodBodyPartType.ForeArm_R);
            BloodClothingType.coveredParts.put(BloodClothingType.ShirtLongSleeves, list2);
            BloodClothingType.coveredParts.put(BloodClothingType.Jumper, list2);
            final ArrayList<BloodBodyPartType> value2 = new ArrayList<BloodBodyPartType>();
            value2.addAll(list2);
            value2.add(BloodBodyPartType.Neck);
            BloodClothingType.coveredParts.put(BloodClothingType.Jacket, value2);
            final ArrayList<BloodBodyPartType> value3 = new ArrayList<BloodBodyPartType>();
            value3.addAll(list2);
            value3.add(BloodBodyPartType.Neck);
            value3.add(BloodBodyPartType.Groin);
            value3.add(BloodBodyPartType.UpperLeg_L);
            value3.add(BloodBodyPartType.UpperLeg_R);
            BloodClothingType.coveredParts.put(BloodClothingType.LongJacket, value3);
            final ArrayList<BloodBodyPartType> list3 = new ArrayList<BloodBodyPartType>();
            list3.add(BloodBodyPartType.Groin);
            list3.add(BloodBodyPartType.UpperLeg_L);
            list3.add(BloodBodyPartType.UpperLeg_R);
            BloodClothingType.coveredParts.put(BloodClothingType.ShortsShort, list3);
            final ArrayList<BloodBodyPartType> value4 = new ArrayList<BloodBodyPartType>();
            value4.addAll(list3);
            value4.add(BloodBodyPartType.LowerLeg_L);
            value4.add(BloodBodyPartType.LowerLeg_R);
            BloodClothingType.coveredParts.put(BloodClothingType.Trousers, value4);
            final ArrayList<BloodBodyPartType> value5 = new ArrayList<BloodBodyPartType>();
            value5.add(BloodBodyPartType.Foot_L);
            value5.add(BloodBodyPartType.Foot_R);
            BloodClothingType.coveredParts.put(BloodClothingType.Shoes, value5);
            final ArrayList<BloodBodyPartType> value6 = new ArrayList<BloodBodyPartType>();
            value6.add(BloodBodyPartType.Head);
            BloodClothingType.coveredParts.put(BloodClothingType.FullHelmet, value6);
            final ArrayList<BloodBodyPartType> value7 = new ArrayList<BloodBodyPartType>();
            value7.add(BloodBodyPartType.Back);
            BloodClothingType.coveredParts.put(BloodClothingType.Bag, value7);
            final ArrayList<BloodBodyPartType> value8 = new ArrayList<BloodBodyPartType>();
            value8.add(BloodBodyPartType.Hand_L);
            value8.add(BloodBodyPartType.Hand_R);
            BloodClothingType.coveredParts.put(BloodClothingType.Hands, value8);
            final ArrayList<BloodBodyPartType> value9 = new ArrayList<BloodBodyPartType>();
            value9.add(BloodBodyPartType.Head);
            BloodClothingType.coveredParts.put(BloodClothingType.Head, value9);
            final ArrayList<BloodBodyPartType> value10 = new ArrayList<BloodBodyPartType>();
            value10.add(BloodBodyPartType.Neck);
            BloodClothingType.coveredParts.put(BloodClothingType.Neck, value10);
            final ArrayList<BloodBodyPartType> value11 = new ArrayList<BloodBodyPartType>();
            value11.add(BloodBodyPartType.Groin);
            BloodClothingType.coveredParts.put(BloodClothingType.Groin, value11);
            final ArrayList<BloodBodyPartType> value12 = new ArrayList<BloodBodyPartType>();
            value12.add(BloodBodyPartType.Torso_Upper);
            BloodClothingType.coveredParts.put(BloodClothingType.UpperBody, value12);
            final ArrayList<BloodBodyPartType> value13 = new ArrayList<BloodBodyPartType>();
            value13.add(BloodBodyPartType.Torso_Lower);
            BloodClothingType.coveredParts.put(BloodClothingType.LowerBody, value13);
            final ArrayList<BloodBodyPartType> value14 = new ArrayList<BloodBodyPartType>();
            value14.add(BloodBodyPartType.LowerLeg_L);
            value14.add(BloodBodyPartType.LowerLeg_R);
            BloodClothingType.coveredParts.put(BloodClothingType.LowerLegs, value14);
            final ArrayList<BloodBodyPartType> value15 = new ArrayList<BloodBodyPartType>();
            value15.add(BloodBodyPartType.UpperLeg_L);
            value15.add(BloodBodyPartType.UpperLeg_R);
            BloodClothingType.coveredParts.put(BloodClothingType.UpperLegs, value15);
            final ArrayList<BloodBodyPartType> value16 = new ArrayList<BloodBodyPartType>();
            value16.add(BloodBodyPartType.UpperArm_L);
            value16.add(BloodBodyPartType.UpperArm_R);
            BloodClothingType.coveredParts.put(BloodClothingType.UpperArms, value16);
            final ArrayList<BloodBodyPartType> value17 = new ArrayList<BloodBodyPartType>();
            value17.add(BloodBodyPartType.ForeArm_L);
            value17.add(BloodBodyPartType.ForeArm_R);
            BloodClothingType.coveredParts.put(BloodClothingType.LowerArms, value17);
        }
    }
    
    public static ArrayList<BloodBodyPartType> getCoveredParts(final ArrayList<BloodClothingType> list) {
        return getCoveredParts(list, new ArrayList<BloodBodyPartType>());
    }
    
    public static ArrayList<BloodBodyPartType> getCoveredParts(final ArrayList<BloodClothingType> list, final ArrayList<BloodBodyPartType> list2) {
        if (list == null) {
            return list2;
        }
        init();
        for (int i = 0; i < list.size(); ++i) {
            list2.addAll(BloodClothingType.coveredParts.get(list.get(i)));
        }
        return list2;
    }
    
    public static int getCoveredPartCount(final ArrayList<BloodClothingType> list) {
        if (list == null) {
            return 0;
        }
        init();
        int n = 0;
        for (int i = 0; i < list.size(); ++i) {
            n += BloodClothingType.coveredParts.get(list.get(i)).size();
        }
        return n;
    }
    
    public static void addBlood(final int n, final HumanVisual humanVisual, final ArrayList<ItemVisual> list, final boolean b) {
        for (int i = 0; i < n; ++i) {
            addBlood(BloodBodyPartType.FromIndex(Rand.Next(0, BloodBodyPartType.MAX.index())), humanVisual, list, b);
        }
    }
    
    public static void addBlood(final BloodBodyPartType bloodBodyPartType, final HumanVisual humanVisual, final ArrayList<ItemVisual> list, final boolean b) {
        init();
        float next = 0.0f;
        if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
            float n = 0.01f;
            float n2 = 0.05f;
            if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
                n = 0.001f;
                n2 = 0.01f;
            }
            if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
                n = 0.05f;
                n2 = 0.1f;
            }
            next = OutfitRNG.Next(n, n2);
        }
        addBlood(bloodBodyPartType, next, humanVisual, list, b);
    }
    
    public static void addDirt(final BloodBodyPartType bloodBodyPartType, final HumanVisual humanVisual, final ArrayList<ItemVisual> list, final boolean b) {
        init();
        float next = 0.0f;
        if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
            float n = 0.01f;
            float n2 = 0.05f;
            if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
                n = 0.001f;
                n2 = 0.01f;
            }
            if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
                n = 0.05f;
                n2 = 0.1f;
            }
            next = OutfitRNG.Next(n, n2);
        }
        addDirt(bloodBodyPartType, next, humanVisual, list, b);
    }
    
    public static void addHole(final BloodBodyPartType bloodBodyPartType, final HumanVisual humanVisual, final ArrayList<ItemVisual> list) {
        addHole(bloodBodyPartType, humanVisual, list, false);
    }
    
    public static void addHole(final BloodBodyPartType hole, final HumanVisual humanVisual, final ArrayList<ItemVisual> list, final boolean b) {
        init();
        ItemVisual itemVisual = null;
        for (int i = list.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual2 = list.get(i);
            final Item scriptItem = itemVisual2.getScriptItem();
            if (scriptItem != null) {
                if (itemVisual2.getInventoryItem() == null || !itemVisual2.getInventoryItem().isBroken()) {
                    final ArrayList<BloodClothingType> bloodClothingType = scriptItem.getBloodClothingType();
                    if (bloodClothingType != null) {
                        for (int j = 0; j < bloodClothingType.size(); ++j) {
                            if (BloodClothingType.coveredParts.get(scriptItem.getBloodClothingType().get(j)).contains(hole) && scriptItem.canHaveHoles && itemVisual2.getHole(hole) == 0.0f) {
                                itemVisual = itemVisual2;
                                break;
                            }
                        }
                        if (itemVisual != null) {
                            itemVisual.setHole(hole);
                            final Clothing clothing = Type.tryCastTo(itemVisual.getInventoryItem(), Clothing.class);
                            if (clothing != null) {
                                clothing.removePatch(hole);
                                clothing.setCondition(clothing.getCondition() - clothing.getCondLossPerHole());
                            }
                            if (!b) {
                                break;
                            }
                            itemVisual = null;
                        }
                    }
                }
            }
        }
        if (itemVisual == null || b) {
            humanVisual.setHole(hole);
        }
    }
    
    public static void addBasicPatch(final BloodBodyPartType bloodBodyPartType, final HumanVisual humanVisual, final ArrayList<ItemVisual> list) {
        init();
        ItemVisual itemVisual = null;
        for (int i = list.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual2 = list.get(i);
            final Item scriptItem = itemVisual2.getScriptItem();
            if (scriptItem != null) {
                final ArrayList<BloodClothingType> bloodClothingType = scriptItem.getBloodClothingType();
                if (bloodClothingType != null) {
                    for (int j = 0; j < bloodClothingType.size(); ++j) {
                        if (BloodClothingType.coveredParts.get(bloodClothingType.get(j)).contains(bloodBodyPartType) && itemVisual2.getBasicPatch(bloodBodyPartType) == 0.0f) {
                            itemVisual = itemVisual2;
                            break;
                        }
                    }
                    if (itemVisual != null) {
                        break;
                    }
                }
            }
        }
        if (itemVisual != null) {
            itemVisual.removeHole(BloodBodyPartType.ToIndex(bloodBodyPartType));
            itemVisual.setBasicPatch(bloodBodyPartType);
        }
    }
    
    public static void addDirt(final BloodBodyPartType bloodBodyPartType, final float n, final HumanVisual humanVisual, final ArrayList<ItemVisual> list, final boolean b) {
        init();
        ItemVisual itemVisual = null;
        if (!b) {
            for (int i = list.size() - 1; i >= 0; --i) {
                final ItemVisual itemVisual2 = list.get(i);
                final Item scriptItem = itemVisual2.getScriptItem();
                if (scriptItem != null) {
                    final ArrayList<BloodClothingType> bloodClothingType = scriptItem.getBloodClothingType();
                    if (bloodClothingType != null) {
                        for (int j = 0; j < bloodClothingType.size(); ++j) {
                            if (BloodClothingType.coveredParts.get(bloodClothingType.get(j)).contains(bloodBodyPartType) && itemVisual2.getHole(bloodBodyPartType) == 0.0f) {
                                itemVisual = itemVisual2;
                                break;
                            }
                        }
                        if (itemVisual != null) {
                            break;
                        }
                    }
                }
            }
            if (itemVisual != null) {
                if (n > 0.0f) {
                    itemVisual.setDirt(bloodBodyPartType, itemVisual.getDirt(bloodBodyPartType) + n);
                    if (itemVisual.getInventoryItem() instanceof Clothing) {
                        calcTotalDirtLevel((Clothing)itemVisual.getInventoryItem());
                    }
                }
            }
            else {
                humanVisual.setDirt(bloodBodyPartType, humanVisual.getDirt(bloodBodyPartType) + 0.05f);
            }
        }
        else {
            humanVisual.setDirt(bloodBodyPartType, humanVisual.getDirt(bloodBodyPartType) + 0.05f);
            float n2 = humanVisual.getDirt(bloodBodyPartType);
            if (Rand.NextBool(Math.abs(new Float(n2 * 100.0f).intValue() - 100))) {
                return;
            }
            for (int k = 0; k < list.size(); ++k) {
                ItemVisual itemVisual3 = null;
                final ItemVisual itemVisual4 = list.get(k);
                final Item scriptItem2 = itemVisual4.getScriptItem();
                if (scriptItem2 != null) {
                    final ArrayList<BloodClothingType> bloodClothingType2 = scriptItem2.getBloodClothingType();
                    if (bloodClothingType2 != null) {
                        for (int l = 0; l < bloodClothingType2.size(); ++l) {
                            if (BloodClothingType.coveredParts.get(bloodClothingType2.get(l)).contains(bloodBodyPartType) && itemVisual4.getHole(bloodBodyPartType) == 0.0f) {
                                itemVisual3 = itemVisual4;
                                break;
                            }
                        }
                        if (itemVisual3 != null) {
                            if (n > 0.0f) {
                                itemVisual3.setDirt(bloodBodyPartType, itemVisual3.getDirt(bloodBodyPartType) + n);
                                if (itemVisual3.getInventoryItem() instanceof Clothing) {
                                    calcTotalDirtLevel((Clothing)itemVisual3.getInventoryItem());
                                }
                                n2 = itemVisual3.getDirt(bloodBodyPartType);
                            }
                            if (Rand.NextBool(Math.abs(new Float(n2 * 100.0f).intValue() - 100))) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void addBlood(final BloodBodyPartType bloodBodyPartType, final float n, final HumanVisual humanVisual, final ArrayList<ItemVisual> list, final boolean b) {
        init();
        ItemVisual itemVisual = null;
        if (!b) {
            for (int i = list.size() - 1; i >= 0; --i) {
                final ItemVisual itemVisual2 = list.get(i);
                final Item scriptItem = itemVisual2.getScriptItem();
                if (scriptItem != null) {
                    final ArrayList<BloodClothingType> bloodClothingType = scriptItem.getBloodClothingType();
                    if (bloodClothingType != null) {
                        for (int j = 0; j < bloodClothingType.size(); ++j) {
                            if (BloodClothingType.coveredParts.get(bloodClothingType.get(j)).contains(bloodBodyPartType) && itemVisual2.getHole(bloodBodyPartType) == 0.0f) {
                                itemVisual = itemVisual2;
                                break;
                            }
                        }
                        if (itemVisual != null) {
                            break;
                        }
                    }
                }
            }
            if (itemVisual != null) {
                if (n > 0.0f) {
                    itemVisual.setBlood(bloodBodyPartType, itemVisual.getBlood(bloodBodyPartType) + n);
                    if (itemVisual.getInventoryItem() instanceof Clothing) {
                        calcTotalBloodLevel((Clothing)itemVisual.getInventoryItem());
                    }
                }
            }
            else {
                humanVisual.setBlood(bloodBodyPartType, humanVisual.getBlood(bloodBodyPartType) + 0.05f);
            }
        }
        else {
            humanVisual.setBlood(bloodBodyPartType, humanVisual.getBlood(bloodBodyPartType) + 0.05f);
            float n2 = humanVisual.getBlood(bloodBodyPartType);
            if (OutfitRNG.NextBool(Math.abs(new Float(n2 * 100.0f).intValue() - 100))) {
                return;
            }
            for (int k = 0; k < list.size(); ++k) {
                ItemVisual itemVisual3 = null;
                final ItemVisual itemVisual4 = list.get(k);
                final Item scriptItem2 = itemVisual4.getScriptItem();
                if (scriptItem2 != null) {
                    final ArrayList<BloodClothingType> bloodClothingType2 = scriptItem2.getBloodClothingType();
                    if (bloodClothingType2 != null) {
                        for (int l = 0; l < bloodClothingType2.size(); ++l) {
                            if (BloodClothingType.coveredParts.get(bloodClothingType2.get(l)).contains(bloodBodyPartType) && itemVisual4.getHole(bloodBodyPartType) == 0.0f) {
                                itemVisual3 = itemVisual4;
                                break;
                            }
                        }
                        if (itemVisual3 != null) {
                            if (n > 0.0f) {
                                itemVisual3.setBlood(bloodBodyPartType, itemVisual3.getBlood(bloodBodyPartType) + n);
                                if (itemVisual3.getInventoryItem() instanceof Clothing) {
                                    calcTotalBloodLevel((Clothing)itemVisual3.getInventoryItem());
                                }
                                n2 = itemVisual3.getBlood(bloodBodyPartType);
                            }
                            if (OutfitRNG.NextBool(Math.abs(new Float(n2 * 100.0f).intValue() - 100))) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static synchronized void calcTotalBloodLevel(final Clothing clothing) {
        final ItemVisual visual = clothing.getVisual();
        if (visual == null) {
            clothing.setBloodLevel(0.0f);
            return;
        }
        final ArrayList<BloodClothingType> bloodClothingType = clothing.getBloodClothingType();
        if (bloodClothingType == null) {
            clothing.setBloodLevel(0.0f);
            return;
        }
        BloodClothingType.bodyParts.clear();
        getCoveredParts(bloodClothingType, BloodClothingType.bodyParts);
        if (BloodClothingType.bodyParts.isEmpty()) {
            clothing.setBloodLevel(0.0f);
            return;
        }
        float n = 0.0f;
        for (int i = 0; i < BloodClothingType.bodyParts.size(); ++i) {
            n += visual.getBlood(BloodClothingType.bodyParts.get(i)) * 100.0f;
        }
        clothing.setBloodLevel(n / BloodClothingType.bodyParts.size());
    }
    
    public static synchronized void calcTotalDirtLevel(final Clothing clothing) {
        final ItemVisual visual = clothing.getVisual();
        if (visual == null) {
            clothing.setDirtyness(0.0f);
            return;
        }
        final ArrayList<BloodClothingType> bloodClothingType = clothing.getBloodClothingType();
        if (bloodClothingType == null) {
            clothing.setDirtyness(0.0f);
            return;
        }
        BloodClothingType.bodyParts.clear();
        getCoveredParts(bloodClothingType, BloodClothingType.bodyParts);
        if (BloodClothingType.bodyParts.isEmpty()) {
            clothing.setDirtyness(0.0f);
            return;
        }
        float n = 0.0f;
        for (int i = 0; i < BloodClothingType.bodyParts.size(); ++i) {
            n += visual.getDirt(BloodClothingType.bodyParts.get(i)) * 100.0f;
        }
        clothing.setDirtyness(n / BloodClothingType.bodyParts.size());
    }
    
    private static /* synthetic */ BloodClothingType[] $values() {
        return new BloodClothingType[] { BloodClothingType.Jacket, BloodClothingType.LongJacket, BloodClothingType.Trousers, BloodClothingType.ShortsShort, BloodClothingType.Shirt, BloodClothingType.ShirtLongSleeves, BloodClothingType.ShirtNoSleeves, BloodClothingType.Jumper, BloodClothingType.JumperNoSleeves, BloodClothingType.Shoes, BloodClothingType.FullHelmet, BloodClothingType.Apron, BloodClothingType.Bag, BloodClothingType.Hands, BloodClothingType.Head, BloodClothingType.Neck, BloodClothingType.UpperBody, BloodClothingType.LowerBody, BloodClothingType.LowerLegs, BloodClothingType.UpperLegs, BloodClothingType.LowerArms, BloodClothingType.UpperArms, BloodClothingType.Groin };
    }
    
    static {
        $VALUES = $values();
        BloodClothingType.coveredParts = null;
        bodyParts = new ArrayList<BloodBodyPartType>();
    }
}
