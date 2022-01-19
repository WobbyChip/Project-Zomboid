// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import java.util.Iterator;
import zombie.inventory.InventoryItem;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import java.util.LinkedHashMap;
import zombie.iso.IsoObject;
import java.util.HashMap;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;

public final class RBTableStory extends RandomizedBuildingBase
{
    public static ArrayList<StoryDef> allStories;
    private float xOffset;
    private float yOffset;
    private IsoGridSquare currentSquare;
    public ArrayList<HashMap<String, Integer>> fullTableMap;
    public IsoObject table1;
    public IsoObject table2;
    
    public RBTableStory() {
        this.xOffset = 0.0f;
        this.yOffset = 0.0f;
        this.currentSquare = null;
        this.fullTableMap = new ArrayList<HashMap<String, Integer>>();
        this.table1 = null;
        this.table2 = null;
    }
    
    public void initStories() {
        if (!RBTableStory.allStories.isEmpty()) {
            return;
        }
        final ArrayList<String> list = new ArrayList<String>();
        list.add("livingroom");
        list.add("kitchen");
        final ArrayList<StorySpawnItem> list2 = new ArrayList<StorySpawnItem>();
        final LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
        linkedHashMap.put("BakingPan", 50);
        linkedHashMap.put("CakePrep", 50);
        list2.add(new StorySpawnItem(linkedHashMap, null, 100));
        list2.add(new StorySpawnItem(null, "Chocolate", 100));
        list2.add(new StorySpawnItem(null, "Butter", 70));
        list2.add(new StorySpawnItem(null, "Flour", 70));
        list2.add(new StorySpawnItem(null, "Spoon", 100));
        list2.add(new StorySpawnItem(null, "EggCarton", 100));
        list2.add(new StorySpawnItem(null, "Egg", 100));
        RBTableStory.allStories.add(new StoryDef(list2, list));
        final ArrayList<StorySpawnItem> list3 = new ArrayList<StorySpawnItem>();
        list3.add(new StorySpawnItem(linkedHashMap, null, 100));
        list3.add(new StorySpawnItem(null, "Flour", 70));
        list3.add(new StorySpawnItem(null, "Butter", 70));
        list3.add(new StorySpawnItem(null, "KitchenKnife", 100));
        list3.add(new StorySpawnItem(null, "Egg", 100));
        list3.add(new StorySpawnItem(null, "Spoon", 100));
        final LinkedHashMap<String, Integer> linkedHashMap2 = new LinkedHashMap<String, Integer>();
        linkedHashMap2.put("BerryBlack", 50);
        linkedHashMap2.put("BerryBlue", 50);
        list3.add(new StorySpawnItem(linkedHashMap2, null, 100));
        list3.add(new StorySpawnItem(linkedHashMap2, null, 70));
        list3.add(new StorySpawnItem(null, "Cherry", 100));
        list3.add(new StorySpawnItem(null, "Pineapple", 70));
        RBTableStory.allStories.add(new StoryDef(list3, list));
        final ArrayList<StorySpawnItem> list4 = new ArrayList<StorySpawnItem>();
        list4.add(new StorySpawnItem(null, "Rabbitmeat", 100, 0.1f));
        list4.add(new StorySpawnItem(null, "Rabbitmeat", 70, 0.1f));
        list4.add(new StorySpawnItem(null, "DeadRabbit", 100, 0.15f));
        list4.add(new StorySpawnItem(null, "Rabbitmeat", 100, 0.1f));
        list4.add(new StorySpawnItem(null, "HuntingKnife", 100));
        final StoryDef e = new StoryDef(list4, list);
        e.addBlood = true;
        RBTableStory.allStories.add(e);
        final ArrayList<StorySpawnItem> list5 = new ArrayList<StorySpawnItem>();
        list5.add(new StorySpawnItem(null, "Mugl", 100));
        list5.add(new StorySpawnItem(null, "Cereal", 100));
        list5.add(new StorySpawnItem(null, "Spoon", 100));
        final LinkedHashMap<String, Integer> linkedHashMap3 = new LinkedHashMap<String, Integer>();
        linkedHashMap3.put("Coffee2", 50);
        linkedHashMap3.put("Teabag2", 50);
        list5.add(new StorySpawnItem(linkedHashMap3, null, 100));
        RBTableStory.allStories.add(new StoryDef(list5, list));
        final ArrayList<StorySpawnItem> list6 = new ArrayList<StorySpawnItem>();
        list6.add(new StorySpawnItem(null, "Socks_Ankle", 100));
        list6.add(new StorySpawnItem(null, "Socks_Long", 70));
        list6.add(new StorySpawnItem(null, "Thread", 100));
        list6.add(new StorySpawnItem(null, "Thread", 50));
        list6.add(new StorySpawnItem(null, "Needle", 100));
        list6.add(new StorySpawnItem(null, "RippedSheets", 100));
        RBTableStory.allStories.add(new StoryDef(list6, list));
        final ArrayList<StorySpawnItem> list7 = new ArrayList<StorySpawnItem>();
        list7.add(new StorySpawnItem(null, "BoxOfJars", 100, 0.15f));
        list7.add(new StorySpawnItem(null, "JarLid", 100));
        list7.add(new StorySpawnItem(null, "EmptyJar", 100));
        list7.add(new StorySpawnItem(null, "Vinegar", 100));
        list7.add(new StorySpawnItem(null, "Sugar", 100));
        final LinkedHashMap<String, Integer> linkedHashMap4 = new LinkedHashMap<String, Integer>();
        linkedHashMap4.put("Carrots", 20);
        linkedHashMap4.put("farming.Tomato", 20);
        linkedHashMap4.put("farming.Potato", 20);
        linkedHashMap4.put("Eggplant", 20);
        linkedHashMap4.put("Leek", 20);
        list7.add(new StorySpawnItem(linkedHashMap4, null, 100));
        RBTableStory.allStories.add(new StoryDef(list7, list));
        final ArrayList<StorySpawnItem> list8 = new ArrayList<StorySpawnItem>();
        list8.add(new StorySpawnItem(null, "Screwdriver", 100));
        list8.add(new StorySpawnItem(null, "ScrewsBox", 100));
        list8.add(new StorySpawnItem(null, "Screws", 100));
        list8.add(new StorySpawnItem(null, "ElectronicsScrap", 100));
        final LinkedHashMap<String, Integer> linkedHashMap5 = new LinkedHashMap<String, Integer>();
        linkedHashMap5.put("VideoGame", 20);
        linkedHashMap5.put("CDplayer", 20);
        linkedHashMap5.put("CordlessPhone", 20);
        linkedHashMap5.put("HomeAlarm", 20);
        linkedHashMap5.put("MotionSensor", 20);
        list8.add(new StorySpawnItem(linkedHashMap5, null, 100));
        RBTableStory.allStories.add(new StoryDef(list8, list));
        final ArrayList<StorySpawnItem> list9 = new ArrayList<StorySpawnItem>();
        list9.add(new StorySpawnItem(null, "Drawer", 100, 0.2f));
        list9.add(new StorySpawnItem(null, "Screwdriver", 100));
        list9.add(new StorySpawnItem(null, "NailsBox", 100));
        list9.add(new StorySpawnItem(null, "Nails", 100));
        list9.add(new StorySpawnItem(null, "Hammer", 50));
        list9.add(new StorySpawnItem(null, "Needle", 100));
        list9.add(new StorySpawnItem(null, "Woodglue", 100));
        RBTableStory.allStories.add(new StoryDef(list9, list));
        final ArrayList<StorySpawnItem> list10 = new ArrayList<StorySpawnItem>();
        list10.add(new StorySpawnItem(null, "Sponge", 100, 0.1f));
        list10.add(new StorySpawnItem(null, "CleaningLiquid2", 100, 0.1f));
        list10.add(new StorySpawnItem(null, "DishCloth", 100));
        RBTableStory.allStories.add(new StoryDef(list10, list));
        final ArrayList<String> list11 = new ArrayList<String>();
        list11.add("livingroom");
        list11.add("kitchen");
        list11.add("bedroom");
        final ArrayList<StorySpawnItem> list12 = new ArrayList<StorySpawnItem>();
        list12.add(new StorySpawnItem(null, "ToyCar", 100));
        list12.add(new StorySpawnItem(null, "ToyBear", 100, 0.1f));
        list12.add(new StorySpawnItem(null, "CatToy", 70));
        list12.add(new StorySpawnItem(null, "ToyCar", 80));
        list12.add(new StorySpawnItem(null, "Bricktoys", 100));
        final StoryDef e2 = new StoryDef(list12, list11);
        e2.addBlood = true;
        RBTableStory.allStories.add(e2);
        final ArrayList<StorySpawnItem> list13 = new ArrayList<StorySpawnItem>();
        list13.add(new StorySpawnItem(null, "Notebook", 100));
        list13.add(new StorySpawnItem(null, "Pencil", 100));
        list13.add(new StorySpawnItem(null, "Pencil", 70));
        list13.add(new StorySpawnItem(null, "BluePen", 80));
        list13.add(new StorySpawnItem(null, "Pen", 80));
        list13.add(new StorySpawnItem(null, "RedPen", 80));
        RBTableStory.allStories.add(new StoryDef(list13, list11));
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return false;
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        this.initStories();
        if (this.table1 == null || this.table2 == null) {
            return;
        }
        if (this.table1.getSquare() == null || this.table1.getSquare().getRoom() == null) {
            return;
        }
        final ArrayList<StoryDef> list = new ArrayList<StoryDef>();
        for (int i = 0; i < RBTableStory.allStories.size(); ++i) {
            final StoryDef e = RBTableStory.allStories.get(i);
            if (e.rooms == null || e.rooms.contains(this.table1.getSquare().getRoom().getName())) {
                list.add(e);
            }
        }
        if (list.isEmpty()) {
            return;
        }
        final StoryDef storyDef = list.get(Rand.Next(0, list.size()));
        if (storyDef == null) {
            return;
        }
        boolean b = true;
        if ((int)this.table1.getY() != (int)this.table2.getY()) {
            b = false;
        }
        this.doSpawnTable(storyDef.items, b);
        if (storyDef.addBlood) {
            int n = (int)this.table1.getX() - 1;
            int n2 = (int)this.table1.getX() + 1;
            int n3 = (int)this.table1.getY() - 1;
            int n4 = (int)this.table2.getY() + 1;
            if (b) {
                n = (int)this.table1.getX() - 1;
                n2 = (int)this.table2.getX() + 1;
                n3 = (int)this.table1.getY() - 1;
                n4 = (int)this.table2.getY() + 1;
            }
            for (int j = n; j < n2 + 1; ++j) {
                for (int k = n3; k < n4 + 1; ++k) {
                    for (int next = Rand.Next(7, 15), l = 0; l < next; ++l) {
                        this.currentSquare.getChunk().addBloodSplat(j + Rand.Next(-0.5f, 0.5f), k + Rand.Next(-0.5f, 0.5f), this.table1.getZ(), Rand.Next(8));
                    }
                }
            }
        }
    }
    
    private void doSpawnTable(final ArrayList<StorySpawnItem> list, final boolean b) {
        this.xOffset = 0.0f;
        this.yOffset = 0.0f;
        int i = 0;
        if (b) {
            this.xOffset = 0.6f;
            this.yOffset = Rand.Next(0.5f, 1.1f);
        }
        else {
            this.yOffset = 0.6f;
            this.xOffset = Rand.Next(0.5f, 1.1f);
        }
        this.currentSquare = this.table1.getSquare();
        while (i < list.size()) {
            final StorySpawnItem storySpawnItem = list.get(i);
            final String itemFromSSI = this.getItemFromSSI(storySpawnItem);
            if (itemFromSSI != null) {
                final InventoryItem addWorldInventoryItem = this.currentSquare.AddWorldInventoryItem(itemFromSSI, this.xOffset, this.yOffset, 0.4f);
                if (addWorldInventoryItem != null) {
                    addWorldInventoryItem.setAutoAge();
                    this.increaseOffsets(b, storySpawnItem);
                }
            }
            ++i;
        }
    }
    
    private void increaseOffsets(final boolean b, final StorySpawnItem storySpawnItem) {
        final float n = 0.15f + storySpawnItem.forcedOffset;
        if (b) {
            this.xOffset += n;
            if (this.xOffset > 1.0f) {
                this.currentSquare = this.table2.getSquare();
                this.xOffset = 0.35f;
            }
            while (Math.abs(this.yOffset - this.yOffset) < 0.11f) {
                this.yOffset = Rand.Next(0.5f, 1.1f);
            }
        }
        else {
            this.yOffset += n;
            if (this.yOffset > 1.0f) {
                this.currentSquare = this.table2.getSquare();
                this.yOffset = 0.35f;
            }
            while (Math.abs(this.xOffset - this.xOffset) < 0.11f) {
                this.xOffset = Rand.Next(0.5f, 1.1f);
            }
        }
    }
    
    private String getItemFromSSI(final StorySpawnItem storySpawnItem) {
        if (Rand.Next(100) > storySpawnItem.chanceToSpawn) {
            return null;
        }
        if (storySpawnItem.eitherObject != null && !storySpawnItem.eitherObject.isEmpty()) {
            final int next = Rand.Next(100);
            int n = 0;
            for (final String key : storySpawnItem.eitherObject.keySet()) {
                n += storySpawnItem.eitherObject.get(key);
                if (n >= next) {
                    return key;
                }
            }
            return null;
        }
        return storySpawnItem.object;
    }
    
    static {
        RBTableStory.allStories = new ArrayList<StoryDef>();
    }
    
    public class StorySpawnItem
    {
        LinkedHashMap<String, Integer> eitherObject;
        String object;
        Integer chanceToSpawn;
        float forcedOffset;
        
        public StorySpawnItem(final LinkedHashMap<String, Integer> eitherObject, final String object, final Integer chanceToSpawn) {
            this.eitherObject = null;
            this.object = null;
            this.chanceToSpawn = null;
            this.forcedOffset = 0.0f;
            this.eitherObject = eitherObject;
            this.object = object;
            this.chanceToSpawn = chanceToSpawn;
        }
        
        public StorySpawnItem(final LinkedHashMap<String, Integer> eitherObject, final String object, final Integer chanceToSpawn, final float forcedOffset) {
            this.eitherObject = null;
            this.object = null;
            this.chanceToSpawn = null;
            this.forcedOffset = 0.0f;
            this.eitherObject = eitherObject;
            this.object = object;
            this.chanceToSpawn = chanceToSpawn;
            this.forcedOffset = forcedOffset;
        }
    }
    
    public class StoryDef
    {
        public ArrayList<StorySpawnItem> items;
        public boolean addBlood;
        public ArrayList<String> rooms;
        
        public StoryDef(final ArrayList<StorySpawnItem> items) {
            this.items = null;
            this.addBlood = false;
            this.rooms = null;
            this.items = items;
        }
        
        public StoryDef(final ArrayList<StorySpawnItem> items, final ArrayList<String> rooms) {
            this.items = null;
            this.addBlood = false;
            this.rooms = null;
            this.items = items;
            this.rooms = rooms;
        }
    }
}
