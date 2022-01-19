// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.popman.ObjectPool;
import se.krka.kahlua.integration.LuaReturn;
import java.util.Objects;
import java.util.function.Supplier;
import zombie.iso.objects.IsoCompost;
import zombie.SystemDisabler;
import zombie.core.Rand;
import java.util.LinkedHashMap;
import zombie.util.StringUtils;
import zombie.inventory.types.Key;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.PacketTypes;
import zombie.util.Type;
import zombie.characters.SurvivorDesc;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoStove;
import zombie.iso.IsoDirections;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.iso.objects.IsoMannequin;
import se.krka.kahlua.vm.LuaClosure;
import java.util.function.Predicate;
import java.util.Comparator;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.inventory.types.Drainable;
import zombie.scripting.objects.Item;
import zombie.inventory.types.Food;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import zombie.iso.IsoWorld;
import java.util.Iterator;
import zombie.inventory.types.Clothing;
import zombie.Lua.LuaManager;
import zombie.iso.objects.IsoDeadBody;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.InventoryContainer;
import zombie.vehicles.VehiclePart;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import java.util.ArrayList;

public final class ItemContainer
{
    private static final ArrayList<InventoryItem> tempList;
    private static final ArrayList<IsoObject> s_tempObjects;
    public boolean active;
    private boolean dirty;
    public boolean IsDevice;
    public float ageFactor;
    public float CookingFactor;
    public int Capacity;
    public InventoryItem containingItem;
    public ArrayList<InventoryItem> Items;
    public ArrayList<InventoryItem> IncludingObsoleteItems;
    public IsoObject parent;
    public IsoGridSquare SourceGrid;
    public VehiclePart vehiclePart;
    public InventoryContainer inventoryContainer;
    public boolean bExplored;
    public String type;
    public int ID;
    private boolean drawDirty;
    private float customTemperature;
    private boolean hasBeenLooted;
    private String openSound;
    private String closeSound;
    private String putSound;
    private String OnlyAcceptCategory;
    private String AcceptItemFunction;
    private int weightReduction;
    private String containerPosition;
    private String freezerPosition;
    private static final ThreadLocal<Comparators> TL_comparators;
    private static final ThreadLocal<InventoryItemListPool> TL_itemListPool;
    private static final ThreadLocal<Predicates> TL_predicates;
    
    public ItemContainer(final int id, final String type, final IsoGridSquare sourceGrid, final IsoObject parent) {
        this.active = false;
        this.dirty = true;
        this.IsDevice = false;
        this.ageFactor = 1.0f;
        this.CookingFactor = 1.0f;
        this.Capacity = 50;
        this.containingItem = null;
        this.Items = new ArrayList<InventoryItem>();
        this.IncludingObsoleteItems = new ArrayList<InventoryItem>();
        this.parent = null;
        this.SourceGrid = null;
        this.vehiclePart = null;
        this.inventoryContainer = null;
        this.bExplored = false;
        this.type = "none";
        this.ID = 0;
        this.drawDirty = true;
        this.customTemperature = 0.0f;
        this.hasBeenLooted = false;
        this.openSound = null;
        this.closeSound = null;
        this.putSound = null;
        this.OnlyAcceptCategory = null;
        this.AcceptItemFunction = null;
        this.weightReduction = 0;
        this.containerPosition = null;
        this.freezerPosition = null;
        this.ID = id;
        this.parent = parent;
        this.type = type;
        this.SourceGrid = sourceGrid;
        if (type.equals("fridge")) {
            this.ageFactor = 0.02f;
            this.CookingFactor = 0.0f;
        }
    }
    
    public ItemContainer(final String type, final IsoGridSquare sourceGrid, final IsoObject parent) {
        this.active = false;
        this.dirty = true;
        this.IsDevice = false;
        this.ageFactor = 1.0f;
        this.CookingFactor = 1.0f;
        this.Capacity = 50;
        this.containingItem = null;
        this.Items = new ArrayList<InventoryItem>();
        this.IncludingObsoleteItems = new ArrayList<InventoryItem>();
        this.parent = null;
        this.SourceGrid = null;
        this.vehiclePart = null;
        this.inventoryContainer = null;
        this.bExplored = false;
        this.type = "none";
        this.ID = 0;
        this.drawDirty = true;
        this.customTemperature = 0.0f;
        this.hasBeenLooted = false;
        this.openSound = null;
        this.closeSound = null;
        this.putSound = null;
        this.OnlyAcceptCategory = null;
        this.AcceptItemFunction = null;
        this.weightReduction = 0;
        this.containerPosition = null;
        this.freezerPosition = null;
        this.ID = -1;
        this.parent = parent;
        this.type = type;
        this.SourceGrid = sourceGrid;
        if (type.equals("fridge")) {
            this.ageFactor = 0.02f;
            this.CookingFactor = 0.0f;
        }
    }
    
    public ItemContainer(final int id) {
        this.active = false;
        this.dirty = true;
        this.IsDevice = false;
        this.ageFactor = 1.0f;
        this.CookingFactor = 1.0f;
        this.Capacity = 50;
        this.containingItem = null;
        this.Items = new ArrayList<InventoryItem>();
        this.IncludingObsoleteItems = new ArrayList<InventoryItem>();
        this.parent = null;
        this.SourceGrid = null;
        this.vehiclePart = null;
        this.inventoryContainer = null;
        this.bExplored = false;
        this.type = "none";
        this.ID = 0;
        this.drawDirty = true;
        this.customTemperature = 0.0f;
        this.hasBeenLooted = false;
        this.openSound = null;
        this.closeSound = null;
        this.putSound = null;
        this.OnlyAcceptCategory = null;
        this.AcceptItemFunction = null;
        this.weightReduction = 0;
        this.containerPosition = null;
        this.freezerPosition = null;
        this.ID = id;
    }
    
    public ItemContainer() {
        this.active = false;
        this.dirty = true;
        this.IsDevice = false;
        this.ageFactor = 1.0f;
        this.CookingFactor = 1.0f;
        this.Capacity = 50;
        this.containingItem = null;
        this.Items = new ArrayList<InventoryItem>();
        this.IncludingObsoleteItems = new ArrayList<InventoryItem>();
        this.parent = null;
        this.SourceGrid = null;
        this.vehiclePart = null;
        this.inventoryContainer = null;
        this.bExplored = false;
        this.type = "none";
        this.ID = 0;
        this.drawDirty = true;
        this.customTemperature = 0.0f;
        this.hasBeenLooted = false;
        this.openSound = null;
        this.closeSound = null;
        this.putSound = null;
        this.OnlyAcceptCategory = null;
        this.AcceptItemFunction = null;
        this.weightReduction = 0;
        this.containerPosition = null;
        this.freezerPosition = null;
        this.ID = -1;
    }
    
    public static float floatingPointCorrection(final float n) {
        final int n2 = 100;
        final float n3 = n * n2;
        return (int)((n3 - (int)n3 >= 0.5f) ? (n3 + 1.0f) : n3) / (float)n2;
    }
    
    public int getCapacity() {
        return this.Capacity;
    }
    
    public InventoryItem FindAndReturnWaterItem(final int n) {
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            if (inventoryItem instanceof DrainableComboItem && inventoryItem.isWaterSource() && ((DrainableComboItem)inventoryItem).getDrainableUsesInt() >= n) {
                return inventoryItem;
            }
        }
        return null;
    }
    
    public InventoryItem getItemFromTypeRecurse(final String s) {
        return this.getFirstTypeRecurse(s);
    }
    
    public int getEffectiveCapacity(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter != null && !(this.parent instanceof IsoGameCharacter) && !(this.parent instanceof IsoDeadBody) && !"floor".equals(this.getType())) {
            if (isoGameCharacter.Traits.Organized.isSet()) {
                return (int)Math.max(this.Capacity * 1.3f, (float)(this.Capacity + 1));
            }
            if (isoGameCharacter.Traits.Disorganized.isSet()) {
                return (int)Math.max(this.Capacity * 0.7f, 1.0f);
            }
        }
        return this.Capacity;
    }
    
    public boolean hasRoomFor(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        return (this.vehiclePart != null && this.vehiclePart.getId().contains("Seat") && this.Items.isEmpty()) || (floatingPointCorrection(this.getCapacityWeight()) + inventoryItem.getUnequippedWeight() <= this.getEffectiveCapacity(isoGameCharacter) && (this.getContainingItem() == null || this.getContainingItem().getEquipParent() == null || this.getContainingItem().getEquipParent().getInventory() == null || this.getContainingItem().getEquipParent().getInventory().contains(inventoryItem) || floatingPointCorrection(this.getContainingItem().getEquipParent().getInventory().getCapacityWeight()) + inventoryItem.getUnequippedWeight() <= this.getContainingItem().getEquipParent().getInventory().getEffectiveCapacity(isoGameCharacter)));
    }
    
    public boolean hasRoomFor(final IsoGameCharacter isoGameCharacter, final float n) {
        return floatingPointCorrection(this.getCapacityWeight()) + n <= this.getEffectiveCapacity(isoGameCharacter);
    }
    
    public boolean isItemAllowed(final InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            return false;
        }
        final String onlyAcceptCategory = this.getOnlyAcceptCategory();
        if (onlyAcceptCategory != null && !onlyAcceptCategory.equalsIgnoreCase(inventoryItem.getCategory())) {
            return false;
        }
        final String acceptItemFunction = this.getAcceptItemFunction();
        if (acceptItemFunction != null) {
            final Object functionObject = LuaManager.getFunctionObject(acceptItemFunction);
            if (functionObject != null && LuaManager.caller.protectedCallBoolean(LuaManager.thread, functionObject, (Object)this, (Object)inventoryItem) != Boolean.TRUE) {
                return false;
            }
        }
        return (this.parent == null || this.parent.isItemAllowedInContainer(this, inventoryItem)) && (!this.getType().equals("clothingrack") || inventoryItem instanceof Clothing);
    }
    
    public boolean isRemoveItemAllowed(final InventoryItem inventoryItem) {
        return inventoryItem != null && (this.parent == null || this.parent.isRemoveItemAllowedFromContainer(this, inventoryItem));
    }
    
    public boolean isExplored() {
        return this.bExplored;
    }
    
    public void setExplored(final boolean bExplored) {
        this.bExplored = bExplored;
    }
    
    public boolean isInCharacterInventory(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.getInventory() == this) {
            return true;
        }
        if (this.containingItem != null) {
            if (isoGameCharacter.getInventory().contains(this.containingItem, true)) {
                return true;
            }
            if (this.containingItem.getContainer() != null) {
                return this.containingItem.getContainer().isInCharacterInventory(isoGameCharacter);
            }
        }
        return false;
    }
    
    public boolean isInside(final InventoryItem inventoryItem) {
        return this.containingItem != null && (this.containingItem == inventoryItem || (this.containingItem.getContainer() != null && this.containingItem.getContainer().isInside(inventoryItem)));
    }
    
    public InventoryItem getContainingItem() {
        return this.containingItem;
    }
    
    public InventoryItem DoAddItem(final InventoryItem inventoryItem) {
        return this.AddItem(inventoryItem);
    }
    
    public InventoryItem DoAddItemBlind(final InventoryItem inventoryItem) {
        return this.AddItem(inventoryItem);
    }
    
    public ArrayList<InventoryItem> AddItems(final String s, final int n) {
        final ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        for (int i = 0; i < n; ++i) {
            final InventoryItem addItem = this.AddItem(s);
            if (addItem != null) {
                list.add(addItem);
            }
        }
        return list;
    }
    
    public void AddItems(final InventoryItem inventoryItem, final int n) {
        for (int i = 0; i < n; ++i) {
            this.AddItem(inventoryItem.getFullType());
        }
    }
    
    public int getNumberOfItem(final String s, final boolean b) {
        return this.getNumberOfItem(s, b, false);
    }
    
    public int getNumberOfItem(final String s) {
        return this.getNumberOfItem(s, false);
    }
    
    public int getNumberOfItem(final String s, final boolean b, final ArrayList<ItemContainer> list) {
        int numberOfItem = this.getNumberOfItem(s, b);
        if (list != null) {
            for (final ItemContainer itemContainer : list) {
                if (itemContainer != this) {
                    numberOfItem += itemContainer.getNumberOfItem(s, b);
                }
            }
        }
        return numberOfItem;
    }
    
    public int getNumberOfItem(final String s, final boolean b, final boolean b2) {
        int n = 0;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.getFullType().equals(s) || inventoryItem.getType().equals(s)) {
                ++n;
            }
            else if (b2 && inventoryItem instanceof InventoryContainer) {
                n += ((InventoryContainer)inventoryItem).getItemContainer().getNumberOfItem(s);
            }
            else if (b && inventoryItem instanceof DrainableComboItem && ((DrainableComboItem)inventoryItem).getReplaceOnDeplete() != null) {
                final DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
                if (drainableComboItem.getReplaceOnDepleteFullType().equals(s) || drainableComboItem.getReplaceOnDeplete().equals(s)) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    public InventoryItem addItem(final InventoryItem inventoryItem) {
        return this.AddItem(inventoryItem);
    }
    
    public InventoryItem AddItem(final InventoryItem e) {
        if (e == null) {
            return null;
        }
        if (this.containsID(e.id)) {
            System.out.println("Error, container already has id");
            return this.getItemWithID(e.id);
        }
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.parent.DirtySlice();
        }
        if (e.container != null) {
            e.container.Remove(e);
        }
        e.container = this;
        this.Items.add(e);
        if (IsoWorld.instance.CurrentCell != null) {
            IsoWorld.instance.CurrentCell.addToProcessItems(e);
        }
        return e;
    }
    
    public InventoryItem AddItemBlind(final InventoryItem e) {
        if (e == null) {
            return null;
        }
        if (e.getWeight() + this.getCapacityWeight() > this.getCapacity()) {
            return null;
        }
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.parent.DirtySlice();
        }
        this.Items.add(e);
        return e;
    }
    
    public InventoryItem AddItem(final String s) {
        this.drawDirty = true;
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.dirty = true;
        }
        final Item findItem = ScriptManager.instance.FindItem(s);
        if (findItem == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return null;
        }
        if (findItem.OBSOLETE) {
            return null;
        }
        InventoryItem createItem = null;
        for (int count = findItem.getCount(), i = 0; i < count; ++i) {
            createItem = InventoryItemFactory.CreateItem(s);
            if (createItem == null) {
                return null;
            }
            createItem.container = this;
            this.Items.add(createItem);
            if (createItem instanceof Food) {
                ((Food)createItem).setHeat(this.getTemprature());
            }
            if (IsoWorld.instance.CurrentCell != null) {
                IsoWorld.instance.CurrentCell.addToProcessItems(createItem);
            }
        }
        return createItem;
    }
    
    public boolean AddItem(final String s, final float usedDelta) {
        this.drawDirty = true;
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.dirty = true;
        }
        final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
        if (createItem == null) {
            return false;
        }
        if (createItem instanceof Drainable) {
            ((Drainable)createItem).setUsedDelta(usedDelta);
        }
        createItem.container = this;
        this.Items.add(createItem);
        return true;
    }
    
    public boolean contains(final InventoryItem o) {
        return this.Items.contains(o);
    }
    
    public boolean containsWithModule(final String s) {
        return this.containsWithModule(s, false);
    }
    
    public boolean containsWithModule(final String s, final boolean b) {
        String s2 = s;
        String s3 = "Base";
        if (s.contains(".")) {
            s3 = s.split("\\.")[0];
            s2 = s.split("\\.")[1];
        }
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem == null) {
                this.Items.remove(i);
                --i;
            }
            else if (inventoryItem.type.equals(s2.trim()) && s3.equals(inventoryItem.getModule())) {
                if (!b || !(inventoryItem instanceof DrainableComboItem) || ((DrainableComboItem)inventoryItem).getUsedDelta() > 0.0f) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void removeItemOnServer(final InventoryItem inventoryItem) {
        if (GameClient.bClient) {
            if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
                GameClient.instance.addToItemRemoveSendBuffer(this.containingItem.getWorldItem(), this, inventoryItem);
            }
            else {
                GameClient.instance.addToItemRemoveSendBuffer(this.parent, this, inventoryItem);
            }
        }
    }
    
    public void addItemOnServer(final InventoryItem inventoryItem) {
        if (GameClient.bClient) {
            if (this.containingItem != null && this.containingItem.getWorldItem() != null) {
                GameClient.instance.addToItemSendBuffer(this.containingItem.getWorldItem(), this, inventoryItem);
            }
            else {
                GameClient.instance.addToItemSendBuffer(this.parent, this, inventoryItem);
            }
        }
    }
    
    public boolean contains(final InventoryItem inventoryItem, final boolean b) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem2 = this.Items.get(i);
            if (inventoryItem2 == null) {
                this.Items.remove(i);
                --i;
            }
            else {
                if (inventoryItem2 == inventoryItem) {
                    ItemContainer.TL_itemListPool.get().release(list);
                    return true;
                }
                if (b && inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getInventory() != null && !list.contains(inventoryItem2)) {
                    ((ArrayList<InventoryContainer>)list).add(inventoryItem2);
                }
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            if (((ArrayList<InventoryContainer>)list).get(j).getInventory().contains(inventoryItem, b)) {
                ItemContainer.TL_itemListPool.get().release(list);
                return true;
            }
        }
        ItemContainer.TL_itemListPool.get().release(list);
        return false;
    }
    
    public boolean contains(final String s, final boolean b) {
        return this.contains(s, b, false);
    }
    
    public boolean containsType(final String s) {
        return this.contains(s, false, false);
    }
    
    public boolean containsTypeRecurse(final String s) {
        return this.contains(s, true, false);
    }
    
    private boolean testBroken(final boolean b, final InventoryItem inventoryItem) {
        return !b || !inventoryItem.isBroken();
    }
    
    public boolean contains(final String s, final boolean b, final boolean b2) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        if (s.contains("Type:")) {
            for (int i = 0; i < this.Items.size(); ++i) {
                final InventoryItem inventoryItem = this.Items.get(i);
                if (s.contains("Food") && inventoryItem instanceof Food) {
                    ItemContainer.TL_itemListPool.get().release(list);
                    return true;
                }
                if (s.contains("Weapon") && inventoryItem instanceof HandWeapon && this.testBroken(b2, inventoryItem)) {
                    ItemContainer.TL_itemListPool.get().release(list);
                    return true;
                }
                if (s.contains("AlarmClock") && inventoryItem instanceof AlarmClock) {
                    ItemContainer.TL_itemListPool.get().release(list);
                    return true;
                }
                if (s.contains("AlarmClockClothing") && inventoryItem instanceof AlarmClockClothing) {
                    ItemContainer.TL_itemListPool.get().release(list);
                    return true;
                }
                if (b && inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory() != null && !list.contains(inventoryItem)) {
                    ((ArrayList<InventoryContainer>)list).add(inventoryItem);
                }
            }
        }
        else if (s.contains("/")) {
            for (final String s2 : s.split("/")) {
                for (int k = 0; k < this.Items.size(); ++k) {
                    final InventoryItem inventoryItem2 = this.Items.get(k);
                    if (compareType(s2.trim(), inventoryItem2) && this.testBroken(b2, inventoryItem2)) {
                        ItemContainer.TL_itemListPool.get().release(list);
                        return true;
                    }
                    if (b && inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getInventory() != null && !list.contains(inventoryItem2)) {
                        ((ArrayList<InventoryContainer>)list).add(inventoryItem2);
                    }
                }
            }
        }
        else {
            for (int l = 0; l < this.Items.size(); ++l) {
                final InventoryItem inventoryItem3 = this.Items.get(l);
                if (inventoryItem3 == null) {
                    this.Items.remove(l);
                    --l;
                }
                else {
                    if (compareType(s.trim(), inventoryItem3) && this.testBroken(b2, inventoryItem3)) {
                        ItemContainer.TL_itemListPool.get().release(list);
                        return true;
                    }
                    if (b && inventoryItem3 instanceof InventoryContainer && ((InventoryContainer)inventoryItem3).getInventory() != null && !list.contains(inventoryItem3)) {
                        ((ArrayList<InventoryContainer>)list).add(inventoryItem3);
                    }
                }
            }
        }
        for (int index = 0; index < list.size(); ++index) {
            if (((ArrayList<InventoryContainer>)list).get(index).getInventory().contains(s, b, b2)) {
                ItemContainer.TL_itemListPool.get().release(list);
                return true;
            }
        }
        ItemContainer.TL_itemListPool.get().release(list);
        return false;
    }
    
    public boolean contains(final String s) {
        return this.contains(s, false);
    }
    
    private static InventoryItem getBestOf(final InventoryItemList list, final Comparator<InventoryItem> comparator) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        InventoryItem inventoryItem = list.get(0);
        for (int i = 1; i < list.size(); ++i) {
            final InventoryItem inventoryItem2 = list.get(i);
            if (comparator.compare(inventoryItem2, inventoryItem) > 0) {
                inventoryItem = inventoryItem2;
            }
        }
        return inventoryItem;
    }
    
    public InventoryItem getBest(final Predicate<InventoryItem> predicate, final Comparator<InventoryItem> comparator) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAll(predicate, list);
        final InventoryItem best = getBestOf(list, comparator);
        ItemContainer.TL_itemListPool.get().release(list);
        return best;
    }
    
    public InventoryItem getBestRecurse(final Predicate<InventoryItem> predicate, final Comparator<InventoryItem> comparator) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAllRecurse(predicate, list);
        final InventoryItem best = getBestOf(list, comparator);
        ItemContainer.TL_itemListPool.get().release(list);
        return best;
    }
    
    public InventoryItem getBestType(final String s, final Comparator<InventoryItem> comparator) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        try {
            return this.getBest(init, comparator);
        }
        finally {
            ItemContainer.TL_predicates.get().type.release(init);
        }
    }
    
    public InventoryItem getBestTypeRecurse(final String s, final Comparator<InventoryItem> comparator) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        try {
            return this.getBestRecurse(init, comparator);
        }
        finally {
            ItemContainer.TL_predicates.get().type.release(init);
        }
    }
    
    public InventoryItem getBestEval(final LuaClosure luaClosure, final LuaClosure luaClosure2) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final EvalComparator init2 = ItemContainer.TL_comparators.get().eval.alloc().init(luaClosure2);
        try {
            return this.getBest(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().eval.release(init);
            ItemContainer.TL_comparators.get().eval.release(init2);
        }
    }
    
    public InventoryItem getBestEvalRecurse(final LuaClosure luaClosure, final LuaClosure luaClosure2) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final EvalComparator init2 = ItemContainer.TL_comparators.get().eval.alloc().init(luaClosure2);
        try {
            return this.getBestRecurse(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().eval.release(init);
            ItemContainer.TL_comparators.get().eval.release(init2);
        }
    }
    
    public InventoryItem getBestEvalArg(final LuaClosure luaClosure, final LuaClosure luaClosure2, final Object o) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final EvalArgComparator init2 = ItemContainer.TL_comparators.get().evalArg.alloc().init(luaClosure2, o);
        try {
            return this.getBest(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().evalArg.release(init);
            ItemContainer.TL_comparators.get().evalArg.release(init2);
        }
    }
    
    public InventoryItem getBestEvalArgRecurse(final LuaClosure luaClosure, final LuaClosure luaClosure2, final Object o) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final EvalArgComparator init2 = ItemContainer.TL_comparators.get().evalArg.alloc().init(luaClosure2, o);
        try {
            return this.getBestRecurse(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().evalArg.release(init);
            ItemContainer.TL_comparators.get().evalArg.release(init2);
        }
    }
    
    public InventoryItem getBestTypeEval(final String s, final LuaClosure luaClosure) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final EvalComparator init2 = ItemContainer.TL_comparators.get().eval.alloc().init(luaClosure);
        try {
            return this.getBest(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().type.release(init);
            ItemContainer.TL_comparators.get().eval.release(init2);
        }
    }
    
    public InventoryItem getBestTypeEvalRecurse(final String s, final LuaClosure luaClosure) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final EvalComparator init2 = ItemContainer.TL_comparators.get().eval.alloc().init(luaClosure);
        try {
            return this.getBestRecurse(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().type.release(init);
            ItemContainer.TL_comparators.get().eval.release(init2);
        }
    }
    
    public InventoryItem getBestTypeEvalArg(final String s, final LuaClosure luaClosure, final Object o) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final EvalArgComparator init2 = ItemContainer.TL_comparators.get().evalArg.alloc().init(luaClosure, o);
        try {
            return this.getBest(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().type.release(init);
            ItemContainer.TL_comparators.get().evalArg.release(init2);
        }
    }
    
    public InventoryItem getBestTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final EvalArgComparator init2 = ItemContainer.TL_comparators.get().evalArg.alloc().init(luaClosure, o);
        try {
            return this.getBestRecurse(init, init2);
        }
        finally {
            ItemContainer.TL_predicates.get().type.release(init);
            ItemContainer.TL_comparators.get().evalArg.release(init2);
        }
    }
    
    public InventoryItem getBestCondition(final Predicate<InventoryItem> predicate) {
        final ConditionComparator conditionComparator = ItemContainer.TL_comparators.get().condition.alloc();
        InventoryItem best = this.getBest(predicate, conditionComparator);
        ItemContainer.TL_comparators.get().condition.release(conditionComparator);
        if (best != null && best.getCondition() <= 0) {
            best = null;
        }
        return best;
    }
    
    public InventoryItem getBestConditionRecurse(final Predicate<InventoryItem> predicate) {
        final ConditionComparator conditionComparator = ItemContainer.TL_comparators.get().condition.alloc();
        InventoryItem bestRecurse = this.getBestRecurse(predicate, conditionComparator);
        ItemContainer.TL_comparators.get().condition.release(conditionComparator);
        if (bestRecurse != null && bestRecurse.getCondition() <= 0) {
            bestRecurse = null;
        }
        return bestRecurse;
    }
    
    public InventoryItem getBestCondition(final String s) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final InventoryItem bestCondition = this.getBestCondition(init);
        ItemContainer.TL_predicates.get().type.release(init);
        return bestCondition;
    }
    
    public InventoryItem getBestConditionRecurse(final String s) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final InventoryItem bestConditionRecurse = this.getBestConditionRecurse(init);
        ItemContainer.TL_predicates.get().type.release(init);
        return bestConditionRecurse;
    }
    
    public InventoryItem getBestConditionEval(final LuaClosure luaClosure) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final InventoryItem bestCondition = this.getBestCondition(init);
        ItemContainer.TL_predicates.get().eval.release(init);
        return bestCondition;
    }
    
    public InventoryItem getBestConditionEvalRecurse(final LuaClosure luaClosure) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final InventoryItem bestConditionRecurse = this.getBestConditionRecurse(init);
        ItemContainer.TL_predicates.get().eval.release(init);
        return bestConditionRecurse;
    }
    
    public InventoryItem getFirstEval(final LuaClosure luaClosure) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final InventoryItem first = this.getFirst(init);
        ItemContainer.TL_predicates.get().eval.release(init);
        return first;
    }
    
    public InventoryItem getFirstEvalArg(final LuaClosure luaClosure, final Object o) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final InventoryItem first = this.getFirst(init);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return first;
    }
    
    public boolean containsEval(final LuaClosure luaClosure) {
        return this.getFirstEval(luaClosure) != null;
    }
    
    public boolean containsEvalArg(final LuaClosure luaClosure, final Object o) {
        return this.getFirstEvalArg(luaClosure, o) != null;
    }
    
    public boolean containsEvalRecurse(final LuaClosure luaClosure) {
        return this.getFirstEvalRecurse(luaClosure) != null;
    }
    
    public boolean containsEvalArgRecurse(final LuaClosure luaClosure, final Object o) {
        return this.getFirstEvalArgRecurse(luaClosure, o) != null;
    }
    
    public boolean containsTag(final String s) {
        return this.getFirstTag(s) != null;
    }
    
    public boolean containsTagEval(final String s, final LuaClosure luaClosure) {
        return this.getFirstTagEval(s, luaClosure) != null;
    }
    
    public boolean containsTagRecurse(final String s) {
        return this.getFirstTagRecurse(s) != null;
    }
    
    public boolean containsTagEvalRecurse(final String s, final LuaClosure luaClosure) {
        return this.getFirstTagEvalRecurse(s, luaClosure) != null;
    }
    
    public boolean containsTagEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        return this.getFirstTagEvalArgRecurse(s, luaClosure, o) != null;
    }
    
    public boolean containsTypeEvalRecurse(final String s, final LuaClosure luaClosure) {
        return this.getFirstTypeEvalRecurse(s, luaClosure) != null;
    }
    
    public boolean containsTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        return this.getFirstTypeEvalArgRecurse(s, luaClosure, o) != null;
    }
    
    private static boolean compareType(final String s, final String s2) {
        if (!s.contains("/")) {
            return s.equals(s2);
        }
        final int index = s.indexOf(s2);
        if (index == -1) {
            return false;
        }
        final char c = (index > 0) ? s.charAt(index - 1) : '\0';
        final char c2 = (index + s2.length() < s.length()) ? s.charAt(index + s2.length()) : '\0';
        return (c == '\0' && c2 == '/') || (c == '/' && c2 == '\0') || (c == '/' && c2 == '/');
    }
    
    private static boolean compareType(final String s, final InventoryItem inventoryItem) {
        if (s.indexOf(46) == -1) {
            return compareType(s, inventoryItem.getType());
        }
        return compareType(s, inventoryItem.getFullType()) || compareType(s, inventoryItem.getType());
    }
    
    public InventoryItem getFirst(final Predicate<InventoryItem> predicate) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem == null) {
                this.Items.remove(i);
                --i;
            }
            else if (predicate.test(inventoryItem)) {
                return inventoryItem;
            }
        }
        return null;
    }
    
    public InventoryItem getFirstRecurse(final Predicate<InventoryItem> predicate) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem e = this.Items.get(i);
            if (e == null) {
                this.Items.remove(i);
                --i;
            }
            else {
                if (predicate.test(e)) {
                    ItemContainer.TL_itemListPool.get().release(list);
                    return e;
                }
                if (e instanceof InventoryContainer) {
                    ((ArrayList<InventoryContainer>)list).add(e);
                }
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            final InventoryItem firstRecurse = ((ArrayList<InventoryContainer>)list).get(j).getInventory().getFirstRecurse(predicate);
            if (firstRecurse != null) {
                ItemContainer.TL_itemListPool.get().release(list);
                return firstRecurse;
            }
        }
        ItemContainer.TL_itemListPool.get().release(list);
        return null;
    }
    
    public ArrayList<InventoryItem> getSome(final Predicate<InventoryItem> predicate, final int n, final ArrayList<InventoryItem> list) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem e = this.Items.get(i);
            if (e == null) {
                this.Items.remove(i);
                --i;
            }
            else if (predicate.test(e)) {
                list.add(e);
                if (list.size() >= n) {
                    break;
                }
            }
        }
        return list;
    }
    
    public ArrayList<InventoryItem> getSomeRecurse(final Predicate<InventoryItem> predicate, final int n, final ArrayList<InventoryItem> list) {
        final InventoryItemList list2 = ItemContainer.TL_itemListPool.get().alloc();
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem == null) {
                this.Items.remove(i);
                --i;
            }
            else {
                if (predicate.test(inventoryItem)) {
                    list.add(inventoryItem);
                    if (list.size() >= n) {
                        ItemContainer.TL_itemListPool.get().release(list2);
                        return list;
                    }
                }
                if (inventoryItem instanceof InventoryContainer) {
                    ((ArrayList<InventoryContainer>)list2).add(inventoryItem);
                }
            }
        }
        for (int j = 0; j < list2.size(); ++j) {
            ((ArrayList<InventoryContainer>)list2).get(j).getInventory().getSomeRecurse(predicate, n, list);
            if (list.size() >= n) {
                break;
            }
        }
        ItemContainer.TL_itemListPool.get().release(list2);
        return list;
    }
    
    public ArrayList<InventoryItem> getAll(final Predicate<InventoryItem> predicate, final ArrayList<InventoryItem> list) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem e = this.Items.get(i);
            if (e == null) {
                this.Items.remove(i);
                --i;
            }
            else if (predicate.test(e)) {
                list.add(e);
            }
        }
        return list;
    }
    
    public ArrayList<InventoryItem> getAllRecurse(final Predicate<InventoryItem> predicate, final ArrayList<InventoryItem> list) {
        final InventoryItemList list2 = ItemContainer.TL_itemListPool.get().alloc();
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem == null) {
                this.Items.remove(i);
                --i;
            }
            else {
                if (predicate.test(inventoryItem)) {
                    list.add(inventoryItem);
                }
                if (inventoryItem instanceof InventoryContainer) {
                    ((ArrayList<InventoryContainer>)list2).add(inventoryItem);
                }
            }
        }
        for (int j = 0; j < list2.size(); ++j) {
            ((ArrayList<InventoryContainer>)list2).get(j).getInventory().getAllRecurse(predicate, list);
        }
        ItemContainer.TL_itemListPool.get().release(list2);
        return list;
    }
    
    public int getCount(final Predicate<InventoryItem> predicate) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAll(predicate, list);
        final int size = list.size();
        ItemContainer.TL_itemListPool.get().release(list);
        return size;
    }
    
    public int getCountRecurse(final Predicate<InventoryItem> predicate) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAllRecurse(predicate, list);
        final int size = list.size();
        ItemContainer.TL_itemListPool.get().release(list);
        return size;
    }
    
    public int getCountTag(final String s) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().tag.release(init);
        return count;
    }
    
    public int getCountTagEval(final String s, final LuaClosure luaClosure) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return count;
    }
    
    public int getCountTagEvalArg(final String s, final LuaClosure luaClosure, final Object o) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return count;
    }
    
    public int getCountTagRecurse(final String s) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().tag.release(init);
        return countRecurse;
    }
    
    public int getCountTagEvalRecurse(final String s, final LuaClosure luaClosure) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return countRecurse;
    }
    
    public int getCountTagEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return countRecurse;
    }
    
    public int getCountType(final String s) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().type.release(init);
        return count;
    }
    
    public int getCountTypeEval(final String s, final LuaClosure luaClosure) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return count;
    }
    
    public int getCountTypeEvalArg(final String s, final LuaClosure luaClosure, final Object o) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return count;
    }
    
    public int getCountTypeRecurse(final String s) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().type.release(init);
        return countRecurse;
    }
    
    public int getCountTypeEvalRecurse(final String s, final LuaClosure luaClosure) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return countRecurse;
    }
    
    public int getCountTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return countRecurse;
    }
    
    public int getCountEval(final LuaClosure luaClosure) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().eval.release(init);
        return count;
    }
    
    public int getCountEvalArg(final LuaClosure luaClosure, final Object o) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final int count = this.getCount(init);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return count;
    }
    
    public int getCountEvalRecurse(final LuaClosure luaClosure) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().eval.release(init);
        return countRecurse;
    }
    
    public int getCountEvalArgRecurse(final LuaClosure luaClosure, final Object o) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final int countRecurse = this.getCountRecurse(init);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return countRecurse;
    }
    
    public InventoryItem getFirstCategory(final String s) {
        final CategoryPredicate init = ItemContainer.TL_predicates.get().category.alloc().init(s);
        final InventoryItem first = this.getFirst(init);
        ItemContainer.TL_predicates.get().category.release(init);
        return first;
    }
    
    public InventoryItem getFirstCategoryRecurse(final String s) {
        final CategoryPredicate init = ItemContainer.TL_predicates.get().category.alloc().init(s);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().category.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstEvalRecurse(final LuaClosure luaClosure) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().eval.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstEvalArgRecurse(final LuaClosure luaClosure, final Object o) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTag(final String s) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final InventoryItem first = this.getFirst(init);
        ItemContainer.TL_predicates.get().tag.release(init);
        return first;
    }
    
    public InventoryItem getFirstTagRecurse(final String s) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().tag.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTagEval(final String s, final LuaClosure luaClosure) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTagEvalRecurse(final String s, final LuaClosure luaClosure) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTagEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstType(final String s) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final InventoryItem first = this.getFirst(init);
        ItemContainer.TL_predicates.get().type.release(init);
        return first;
    }
    
    public InventoryItem getFirstTypeRecurse(final String s) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().type.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTypeEval(final String s, final LuaClosure luaClosure) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTypeEvalRecurse(final String s, final LuaClosure luaClosure) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return firstRecurse;
    }
    
    public InventoryItem getFirstTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final InventoryItem firstRecurse = this.getFirstRecurse(init);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return firstRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeCategory(final String s, final int n, final ArrayList<InventoryItem> list) {
        final CategoryPredicate init = ItemContainer.TL_predicates.get().category.alloc().init(s);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().category.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeCategoryRecurse(final String s, final int n, final ArrayList<InventoryItem> list) {
        final CategoryPredicate init = ItemContainer.TL_predicates.get().category.alloc().init(s);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().category.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeTag(final String s, final int n, final ArrayList<InventoryItem> list) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().tag.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeTagEval(final String s, final LuaClosure luaClosure, final int n, final ArrayList<InventoryItem> list) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeTagEvalArg(final String s, final LuaClosure luaClosure, final Object o, final int n, final ArrayList<InventoryItem> list) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeTagRecurse(final String s, final int n, final ArrayList<InventoryItem> list) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().tag.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeTagEvalRecurse(final String s, final LuaClosure luaClosure, final int n, final ArrayList<InventoryItem> list) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeTagEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o, final int n, final ArrayList<InventoryItem> list) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeType(final String s, final int n, final ArrayList<InventoryItem> list) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().type.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeTypeEval(final String s, final LuaClosure luaClosure, final int n, final ArrayList<InventoryItem> list) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeTypeEvalArg(final String s, final LuaClosure luaClosure, final Object o, final int n, final ArrayList<InventoryItem> list) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeTypeRecurse(final String s, final int n, final ArrayList<InventoryItem> list) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().type.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeTypeEvalRecurse(final String s, final LuaClosure luaClosure, final int n, final ArrayList<InventoryItem> list) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o, final int n, final ArrayList<InventoryItem> list) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeEval(final LuaClosure luaClosure, final int n, final ArrayList<InventoryItem> list) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().eval.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeEvalArg(final LuaClosure luaClosure, final Object o, final int n, final ArrayList<InventoryItem> list) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final ArrayList<InventoryItem> some = this.getSome(init, n, list);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return some;
    }
    
    public ArrayList<InventoryItem> getSomeEvalRecurse(final LuaClosure luaClosure, final int n, final ArrayList<InventoryItem> list) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().eval.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeEvalArgRecurse(final LuaClosure luaClosure, final Object o, final int n, final ArrayList<InventoryItem> list) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final ArrayList<InventoryItem> someRecurse = this.getSomeRecurse(init, n, list);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return someRecurse;
    }
    
    public ArrayList<InventoryItem> getAllCategory(final String s, final ArrayList<InventoryItem> list) {
        final CategoryPredicate init = ItemContainer.TL_predicates.get().category.alloc().init(s);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().category.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllCategoryRecurse(final String s, final ArrayList<InventoryItem> list) {
        final CategoryPredicate init = ItemContainer.TL_predicates.get().category.alloc().init(s);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().category.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllTag(final String s, final ArrayList<InventoryItem> list) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().tag.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllTagEval(final String s, final LuaClosure luaClosure, final ArrayList<InventoryItem> list) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllTagEvalArg(final String s, final LuaClosure luaClosure, final Object o, final ArrayList<InventoryItem> list) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllTagRecurse(final String s, final ArrayList<InventoryItem> list) {
        final TagPredicate init = ItemContainer.TL_predicates.get().tag.alloc().init(s);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().tag.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllTagEvalRecurse(final String s, final LuaClosure luaClosure, final ArrayList<InventoryItem> list) {
        final TagEvalPredicate init = ItemContainer.TL_predicates.get().tagEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().tagEval.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllTagEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o, final ArrayList<InventoryItem> list) {
        final TagEvalArgPredicate init = ItemContainer.TL_predicates.get().tagEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().tagEvalArg.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllType(final String s, final ArrayList<InventoryItem> list) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().type.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllTypeEval(final String s, final LuaClosure luaClosure, final ArrayList<InventoryItem> list) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllTypeEvalArg(final String s, final LuaClosure luaClosure, final Object o, final ArrayList<InventoryItem> list) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllTypeRecurse(final String s, final ArrayList<InventoryItem> list) {
        final TypePredicate init = ItemContainer.TL_predicates.get().type.alloc().init(s);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().type.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllTypeEvalRecurse(final String s, final LuaClosure luaClosure, final ArrayList<InventoryItem> list) {
        final TypeEvalPredicate init = ItemContainer.TL_predicates.get().typeEval.alloc().init(s, luaClosure);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().typeEval.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o, final ArrayList<InventoryItem> list) {
        final TypeEvalArgPredicate init = ItemContainer.TL_predicates.get().typeEvalArg.alloc().init(s, luaClosure, o);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().typeEvalArg.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllEval(final LuaClosure luaClosure, final ArrayList<InventoryItem> list) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().eval.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllEvalArg(final LuaClosure luaClosure, final Object o, final ArrayList<InventoryItem> list) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final ArrayList<InventoryItem> all = this.getAll(init, list);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return all;
    }
    
    public ArrayList<InventoryItem> getAllEvalRecurse(final LuaClosure luaClosure, final ArrayList<InventoryItem> list) {
        final EvalPredicate init = ItemContainer.TL_predicates.get().eval.alloc().init(luaClosure);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().eval.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getAllEvalArgRecurse(final LuaClosure luaClosure, final Object o, final ArrayList<InventoryItem> list) {
        final EvalArgPredicate init = ItemContainer.TL_predicates.get().evalArg.alloc().init(luaClosure, o);
        final ArrayList<InventoryItem> allRecurse = this.getAllRecurse(init, list);
        ItemContainer.TL_predicates.get().evalArg.release(init);
        return allRecurse;
    }
    
    public ArrayList<InventoryItem> getSomeCategory(final String s, final int n) {
        return this.getSomeCategory(s, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeEval(final LuaClosure luaClosure, final int n) {
        return this.getSomeEval(luaClosure, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeEvalArg(final LuaClosure luaClosure, final Object o, final int n) {
        return this.getSomeEvalArg(luaClosure, o, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTypeEval(final String s, final LuaClosure luaClosure, final int n) {
        return this.getSomeTypeEval(s, luaClosure, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTypeEvalArg(final String s, final LuaClosure luaClosure, final Object o, final int n) {
        return this.getSomeTypeEvalArg(s, luaClosure, o, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeEvalRecurse(final LuaClosure luaClosure, final int n) {
        return this.getSomeEvalRecurse(luaClosure, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeEvalArgRecurse(final LuaClosure luaClosure, final Object o, final int n) {
        return this.getSomeEvalArgRecurse(luaClosure, o, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTag(final String s, final int n) {
        return this.getSomeTag(s, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTagRecurse(final String s, final int n) {
        return this.getSomeTagRecurse(s, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTagEvalRecurse(final String s, final LuaClosure luaClosure, final int n) {
        return this.getSomeTagEvalRecurse(s, luaClosure, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTagEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o, final int n) {
        return this.getSomeTagEvalArgRecurse(s, luaClosure, o, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeType(final String s, final int n) {
        return this.getSomeType(s, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTypeRecurse(final String s, final int n) {
        return this.getSomeTypeRecurse(s, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTypeEvalRecurse(final String s, final LuaClosure luaClosure, final int n) {
        return this.getSomeTypeEvalRecurse(s, luaClosure, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getSomeTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o, final int n) {
        return this.getSomeTypeEvalArgRecurse(s, luaClosure, o, n, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAll(final Predicate<InventoryItem> predicate) {
        return this.getAll(predicate, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllCategory(final String s) {
        return this.getAllCategory(s, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllEval(final LuaClosure luaClosure) {
        return this.getAllEval(luaClosure, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllEvalArg(final LuaClosure luaClosure, final Object o) {
        return this.getAllEvalArg(luaClosure, o, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTagEval(final String s, final LuaClosure luaClosure) {
        return this.getAllTagEval(s, luaClosure, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTagEvalArg(final String s, final LuaClosure luaClosure, final Object o) {
        return this.getAllTagEvalArg(s, luaClosure, o, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTypeEval(final String s, final LuaClosure luaClosure) {
        return this.getAllTypeEval(s, luaClosure, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTypeEvalArg(final String s, final LuaClosure luaClosure, final Object o) {
        return this.getAllTypeEvalArg(s, luaClosure, o, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllEvalRecurse(final LuaClosure luaClosure) {
        return this.getAllEvalRecurse(luaClosure, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllEvalArgRecurse(final LuaClosure luaClosure, final Object o) {
        return this.getAllEvalArgRecurse(luaClosure, o, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllType(final String s) {
        return this.getAllType(s, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTypeRecurse(final String s) {
        return this.getAllTypeRecurse(s, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTypeEvalRecurse(final String s, final LuaClosure luaClosure) {
        return this.getAllTypeEvalRecurse(s, luaClosure, new ArrayList<InventoryItem>());
    }
    
    public ArrayList<InventoryItem> getAllTypeEvalArgRecurse(final String s, final LuaClosure luaClosure, final Object o) {
        return this.getAllTypeEvalArgRecurse(s, luaClosure, o, new ArrayList<InventoryItem>());
    }
    
    public InventoryItem FindAndReturnCategory(final String anObject) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.getCategory().equals(anObject)) {
                return inventoryItem;
            }
        }
        return null;
    }
    
    public ArrayList<InventoryItem> FindAndReturn(final String s, final int n) {
        return this.getSomeType(s, n);
    }
    
    public InventoryItem FindAndReturn(final String s, final ArrayList<InventoryItem> list) {
        if (s == null) {
            return null;
        }
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem o = this.Items.get(i);
            if (o.type != null) {
                if (compareType(s, o) && !list.contains(o)) {
                    return o;
                }
            }
        }
        return null;
    }
    
    public InventoryItem FindAndReturn(final String s) {
        return this.getFirstType(s);
    }
    
    public ArrayList<InventoryItem> FindAll(final String s) {
        return this.getAllType(s);
    }
    
    public InventoryItem FindAndReturnStack(final String s) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (compareType(s, inventoryItem) && inventoryItem.CanStack(InventoryItemFactory.CreateItem(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, inventoryItem.module, s)))) {
                return inventoryItem;
            }
        }
        return null;
    }
    
    public InventoryItem FindAndReturnStack(final InventoryItem inventoryItem) {
        final String type = inventoryItem.type;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem2 = this.Items.get(i);
            if (inventoryItem2.type == null) {
                if (type != null) {
                    continue;
                }
            }
            else if (!inventoryItem2.type.equals(type)) {
                continue;
            }
            if (inventoryItem2.CanStack(inventoryItem)) {
                return inventoryItem2;
            }
        }
        return null;
    }
    
    public boolean HasType(final ItemType itemType) {
        for (int i = 0; i < this.Items.size(); ++i) {
            if (this.Items.get(i).cat == itemType) {
                return true;
            }
        }
        return false;
    }
    
    public void Remove(final InventoryItem o) {
        for (int i = 0; i < this.Items.size(); ++i) {
            if (this.Items.get(i) == o) {
                if (o.uses > 1) {
                    --o.uses;
                }
                else {
                    this.Items.remove(o);
                }
                o.container = null;
                this.drawDirty = true;
                this.dirty = true;
                if (this.parent != null) {
                    this.dirty = true;
                }
                if (this.parent instanceof IsoDeadBody) {
                    ((IsoDeadBody)this.parent).checkClothing(o);
                }
                if (this.parent instanceof IsoMannequin) {
                    ((IsoMannequin)this.parent).checkClothing(o);
                }
                return;
            }
        }
    }
    
    public void DoRemoveItem(final InventoryItem o) {
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }
        this.Items.remove(o);
        o.container = null;
        if (this.parent instanceof IsoDeadBody) {
            ((IsoDeadBody)this.parent).checkClothing(o);
        }
        if (this.parent instanceof IsoMannequin) {
            ((IsoMannequin)this.parent).checkClothing(o);
        }
    }
    
    public void Remove(final String anObject) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem o = this.Items.get(i);
            if (o.type.equals(anObject)) {
                if (o.uses > 1) {
                    final InventoryItem inventoryItem = o;
                    --inventoryItem.uses;
                }
                else {
                    this.Items.remove(o);
                }
                o.container = null;
                this.drawDirty = true;
                this.dirty = true;
                if (this.parent != null) {
                    this.dirty = true;
                }
                return;
            }
        }
    }
    
    public InventoryItem Remove(final ItemType itemType) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem o = this.Items.get(i);
            if (o.cat == itemType) {
                this.Items.remove(o);
                o.container = null;
                this.drawDirty = true;
                this.dirty = true;
                if (this.parent != null) {
                    this.dirty = true;
                }
                return o;
            }
        }
        return null;
    }
    
    public InventoryItem Find(final ItemType itemType) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.cat == itemType) {
                return inventoryItem;
            }
        }
        return null;
    }
    
    public void RemoveAll(final String anObject) {
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }
        final ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.type.equals(anObject)) {
                inventoryItem.container = null;
                list.add(inventoryItem);
                this.dirty = true;
            }
        }
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.Items.remove(iterator.next());
        }
    }
    
    public boolean RemoveOneOf(final String s, final boolean b) {
        this.drawDirty = true;
        if (this.parent != null && !(this.parent instanceof IsoGameCharacter)) {
            this.dirty = true;
        }
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem o = this.Items.get(i);
            if (o.getFullType().equals(s) || o.type.equals(s)) {
                if (o.uses > 1) {
                    final InventoryItem inventoryItem = o;
                    --inventoryItem.uses;
                }
                else {
                    o.container = null;
                    this.Items.remove(o);
                }
                return this.dirty = true;
            }
        }
        if (b) {
            for (int j = 0; j < this.Items.size(); ++j) {
                final InventoryItem inventoryItem2 = this.Items.get(j);
                if (inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getItemContainer() != null && ((InventoryContainer)inventoryItem2).getItemContainer().RemoveOneOf(s, b)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void RemoveOneOf(final String s) {
        this.RemoveOneOf(s, true);
    }
    
    @Deprecated
    public int getWeight() {
        if (this.parent instanceof IsoPlayer && ((IsoPlayer)this.parent).isGhostMode()) {
            return 0;
        }
        float n = 0.0f;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            n += inventoryItem.ActualWeight * inventoryItem.uses;
        }
        return (int)(n * (this.weightReduction / 0.01f));
    }
    
    public float getContentsWeight() {
        float n = 0.0f;
        for (int i = 0; i < this.Items.size(); ++i) {
            n += this.Items.get(i).getUnequippedWeight();
        }
        return n;
    }
    
    public float getMaxWeight() {
        if (this.parent instanceof IsoGameCharacter) {
            return (float)((IsoGameCharacter)this.parent).getMaxWeight();
        }
        return (float)this.Capacity;
    }
    
    public float getCapacityWeight() {
        if (this.parent instanceof IsoPlayer) {
            if ((Core.bDebug && ((IsoPlayer)this.parent).isGhostMode()) || (!((IsoPlayer)this.parent).getAccessLevel().equals("None") && ((IsoPlayer)this.parent).isUnlimitedCarry())) {
                return 0.0f;
            }
            if (((IsoPlayer)this.parent).isUnlimitedCarry()) {
                return 0.0f;
            }
        }
        if (this.parent instanceof IsoGameCharacter) {
            return ((IsoGameCharacter)this.parent).getInventoryWeight();
        }
        return this.getContentsWeight();
    }
    
    public boolean isEmpty() {
        return this.Items == null || this.Items.isEmpty();
    }
    
    public boolean isMicrowave() {
        return "microwave".equals(this.getType());
    }
    
    private boolean isSquareInRoom(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && isoGridSquare.getRoom() != null;
    }
    
    private boolean isSquarePowered(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return false;
        }
        final boolean b = GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier();
        if (b && isoGridSquare.getRoom() != null) {
            return true;
        }
        if (isoGridSquare.haveElectricity()) {
            return true;
        }
        if (b && isoGridSquare.getRoom() == null) {
            final IsoGridSquare isoGridSquare2 = isoGridSquare.nav[IsoDirections.N.index()];
            final IsoGridSquare isoGridSquare3 = isoGridSquare.nav[IsoDirections.S.index()];
            final IsoGridSquare isoGridSquare4 = isoGridSquare.nav[IsoDirections.W.index()];
            final IsoGridSquare isoGridSquare5 = isoGridSquare.nav[IsoDirections.E.index()];
            if (this.isSquareInRoom(isoGridSquare2) || this.isSquareInRoom(isoGridSquare3) || this.isSquareInRoom(isoGridSquare4) || this.isSquareInRoom(isoGridSquare5)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isPowered() {
        if (this.parent == null || this.parent.getObjectIndex() == -1) {
            return false;
        }
        if (this.isSquarePowered(this.parent.getSquare())) {
            return true;
        }
        this.parent.getSpriteGridObjects(ItemContainer.s_tempObjects);
        for (int i = 0; i < ItemContainer.s_tempObjects.size(); ++i) {
            final IsoObject isoObject = ItemContainer.s_tempObjects.get(i);
            if (isoObject != this.parent) {
                if (this.isSquarePowered(isoObject.getSquare())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public float getTemprature() {
        if (this.customTemperature != 0.0f) {
            return this.customTemperature;
        }
        boolean is = false;
        if (this.getParent() != null && this.getParent().getSprite() != null) {
            is = this.getParent().getSprite().getProperties().Is("IsFridge");
        }
        if (this.isPowered()) {
            if (this.type.equals("fridge") || this.type.equals("freezer") || is) {
                return 0.2f;
            }
            if (("stove".equals(this.type) || "microwave".equals(this.type)) && this.parent instanceof IsoStove) {
                return ((IsoStove)this.parent).getCurrentTemperature() / 100.0f;
            }
        }
        if ("barbecue".equals(this.type) && this.parent instanceof IsoBarbecue) {
            return ((IsoBarbecue)this.parent).getTemperature();
        }
        if ("fireplace".equals(this.type) && this.parent instanceof IsoFireplace) {
            return ((IsoFireplace)this.parent).getTemperature();
        }
        if ("woodstove".equals(this.type) && this.parent instanceof IsoFireplace) {
            return ((IsoFireplace)this.parent).getTemperature();
        }
        if ((this.type.equals("fridge") || this.type.equals("freezer") || is) && GameTime.instance.NightsSurvived == SandboxOptions.instance.getElecShutModifier() && GameTime.instance.getTimeOfDay() < 13.0f) {
            return GameTime.instance.Lerp(0.2f, 1.0f, (GameTime.instance.getTimeOfDay() - 7.0f) / 6.0f);
        }
        return 1.0f;
    }
    
    public boolean isTemperatureChanging() {
        return this.parent instanceof IsoStove && ((IsoStove)this.parent).isTemperatureChanging();
    }
    
    public ArrayList<InventoryItem> save(final ByteBuffer byteBuffer, final IsoGameCharacter isoGameCharacter) throws IOException {
        GameWindow.WriteString(byteBuffer, this.type);
        byteBuffer.put((byte)(this.bExplored ? 1 : 0));
        final ArrayList<InventoryItem> save = CompressIdenticalItems.save(byteBuffer, this.Items, null);
        byteBuffer.put((byte)(this.isHasBeenLooted() ? 1 : 0));
        byteBuffer.putInt(this.Capacity);
        return save;
    }
    
    public ArrayList<InventoryItem> save(final ByteBuffer byteBuffer) throws IOException {
        return this.save(byteBuffer, null);
    }
    
    public ArrayList<InventoryItem> load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.type = GameWindow.ReadString(byteBuffer);
        this.bExplored = (byteBuffer.get() == 1);
        final ArrayList<InventoryItem> load = CompressIdenticalItems.load(byteBuffer, n, this.Items, this.IncludingObsoleteItems);
        for (int i = 0; i < this.Items.size(); ++i) {
            this.Items.get(i).container = this;
        }
        this.setHasBeenLooted(byteBuffer.get() == 1);
        this.Capacity = byteBuffer.getInt();
        this.dirty = false;
        return load;
    }
    
    public boolean isDrawDirty() {
        return this.drawDirty;
    }
    
    public void setDrawDirty(final boolean drawDirty) {
        this.drawDirty = drawDirty;
    }
    
    public InventoryItem getBestWeapon(final SurvivorDesc survivorDesc) {
        InventoryItem inventoryItem = null;
        float n = -1.0E7f;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem2 = this.Items.get(i);
            if (inventoryItem2 instanceof HandWeapon) {
                final float score = inventoryItem2.getScore(survivorDesc);
                if (score >= n) {
                    n = score;
                    inventoryItem = inventoryItem2;
                }
            }
        }
        return inventoryItem;
    }
    
    public InventoryItem getBestWeapon() {
        InventoryItem inventoryItem = null;
        float n = 0.0f;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem2 = this.Items.get(i);
            if (inventoryItem2 instanceof HandWeapon) {
                final float score = inventoryItem2.getScore(null);
                if (score >= n) {
                    n = score;
                    inventoryItem = inventoryItem2;
                }
            }
        }
        return inventoryItem;
    }
    
    public float getTotalFoodScore(final SurvivorDesc survivorDesc) {
        float n = 0.0f;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem instanceof Food) {
                n += inventoryItem.getScore(survivorDesc);
            }
        }
        return n;
    }
    
    public float getTotalWeaponScore(final SurvivorDesc survivorDesc) {
        float n = 0.0f;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem instanceof HandWeapon) {
                n += inventoryItem.getScore(survivorDesc);
            }
        }
        return n;
    }
    
    public InventoryItem getBestFood(final SurvivorDesc survivorDesc) {
        InventoryItem inventoryItem = null;
        float n = 0.0f;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem2 = this.Items.get(i);
            if (inventoryItem2 instanceof Food) {
                float score = inventoryItem2.getScore(survivorDesc);
                if (((Food)inventoryItem2).isbDangerousUncooked() && !inventoryItem2.isCooked()) {
                    score *= 0.2f;
                }
                if (((Food)inventoryItem2).Age > inventoryItem2.OffAge) {
                    score *= 0.2f;
                }
                if (score >= n) {
                    n = score;
                    inventoryItem = inventoryItem2;
                }
            }
        }
        return inventoryItem;
    }
    
    public InventoryItem getBestBandage(final SurvivorDesc survivorDesc) {
        InventoryItem inventoryItem = null;
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem2 = this.Items.get(i);
            if (inventoryItem2.isCanBandage()) {
                inventoryItem = inventoryItem2;
                break;
            }
        }
        return inventoryItem;
    }
    
    public int getNumItems(final String anObject) {
        int n = 0;
        if (anObject.contains("Type:")) {
            for (int i = 0; i < this.Items.size(); ++i) {
                final InventoryItem inventoryItem = this.Items.get(i);
                if (inventoryItem instanceof Food && anObject.contains("Food")) {
                    n += inventoryItem.uses;
                }
                if (inventoryItem instanceof HandWeapon && anObject.contains("Weapon")) {
                    n += inventoryItem.uses;
                }
            }
        }
        else {
            for (int j = 0; j < this.Items.size(); ++j) {
                final InventoryItem inventoryItem2 = this.Items.get(j);
                if (inventoryItem2.type.equals(anObject)) {
                    n += inventoryItem2.uses;
                }
            }
        }
        return n;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isIsDevice() {
        return this.IsDevice;
    }
    
    public void setIsDevice(final boolean isDevice) {
        this.IsDevice = isDevice;
    }
    
    public float getAgeFactor() {
        return this.ageFactor;
    }
    
    public void setAgeFactor(final float ageFactor) {
        this.ageFactor = ageFactor;
    }
    
    public float getCookingFactor() {
        return this.CookingFactor;
    }
    
    public void setCookingFactor(final float cookingFactor) {
        this.CookingFactor = cookingFactor;
    }
    
    public ArrayList<InventoryItem> getItems() {
        return this.Items;
    }
    
    public void setItems(final ArrayList<InventoryItem> items) {
        this.Items = items;
    }
    
    public IsoObject getParent() {
        return this.parent;
    }
    
    public void setParent(final IsoObject parent) {
        this.parent = parent;
    }
    
    public IsoGridSquare getSourceGrid() {
        return this.SourceGrid;
    }
    
    public void setSourceGrid(final IsoGridSquare sourceGrid) {
        this.SourceGrid = sourceGrid;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void clear() {
        this.Items.clear();
        this.dirty = true;
        this.drawDirty = true;
    }
    
    public int getWaterContainerCount() {
        int n = 0;
        for (int i = 0; i < this.Items.size(); ++i) {
            if (this.Items.get(i).CanStoreWater) {
                ++n;
            }
        }
        return n;
    }
    
    public InventoryItem FindWaterSource() {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.isWaterSource()) {
                if (!(inventoryItem instanceof Drainable)) {
                    return inventoryItem;
                }
                if (((Drainable)inventoryItem).getUsedDelta() > 0.0f) {
                    return inventoryItem;
                }
            }
        }
        return null;
    }
    
    public ArrayList<InventoryItem> getAllWaterFillables() {
        ItemContainer.tempList.clear();
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem e = this.Items.get(i);
            if (e.CanStoreWater) {
                ItemContainer.tempList.add(e);
            }
        }
        return ItemContainer.tempList;
    }
    
    public int getItemCount(final String s) {
        return this.getCountType(s);
    }
    
    public int getItemCountRecurse(final String s) {
        return this.getCountTypeRecurse(s);
    }
    
    public int getItemCount(final String s, final boolean b) {
        return b ? this.getCountTypeRecurse(s) : this.getCountType(s);
    }
    
    private static int getUses(final InventoryItemList list) {
        int n = 0;
        for (int i = 0; i < list.size(); ++i) {
            final DrainableComboItem drainableComboItem = Type.tryCastTo(list.get(i), DrainableComboItem.class);
            if (drainableComboItem != null) {
                n += drainableComboItem.getDrainableUsesInt();
            }
            else {
                ++n;
            }
        }
        return n;
    }
    
    public int getUsesRecurse(final Predicate<InventoryItem> predicate) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAllRecurse(predicate, list);
        final int uses = getUses(list);
        ItemContainer.TL_itemListPool.get().release(list);
        return uses;
    }
    
    public int getUsesType(final String s) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAllType(s, list);
        final int uses = getUses(list);
        ItemContainer.TL_itemListPool.get().release(list);
        return uses;
    }
    
    public int getUsesTypeRecurse(final String s) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        this.getAllTypeRecurse(s, list);
        final int uses = getUses(list);
        ItemContainer.TL_itemListPool.get().release(list);
        return uses;
    }
    
    public int getWeightReduction() {
        return this.weightReduction;
    }
    
    public void setWeightReduction(int weightReduction) {
        weightReduction = Math.min(weightReduction, 100);
        weightReduction = Math.max(weightReduction, 0);
        this.weightReduction = weightReduction;
    }
    
    public void removeAllItems() {
        this.drawDirty = true;
        if (this.parent != null) {
            this.dirty = true;
        }
        for (int i = 0; i < this.Items.size(); ++i) {
            this.Items.get(i).container = null;
        }
        this.Items.clear();
        if (this.parent instanceof IsoDeadBody) {
            ((IsoDeadBody)this.parent).checkClothing(null);
        }
        if (this.parent instanceof IsoMannequin) {
            ((IsoMannequin)this.parent).checkClothing(null);
        }
    }
    
    public boolean containsRecursive(final InventoryItem inventoryItem) {
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem2 = this.getItems().get(i);
            if (inventoryItem2 == inventoryItem) {
                return true;
            }
            if (inventoryItem2 instanceof InventoryContainer && ((InventoryContainer)inventoryItem2).getInventory().containsRecursive(inventoryItem)) {
                return true;
            }
        }
        return false;
    }
    
    public int getItemCountFromTypeRecurse(final String anObject) {
        int n = 0;
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            if (inventoryItem.getFullType().equals(anObject)) {
                ++n;
            }
            if (inventoryItem instanceof InventoryContainer) {
                n += ((InventoryContainer)inventoryItem).getInventory().getItemCountFromTypeRecurse(anObject);
            }
        }
        return n;
    }
    
    public float getCustomTemperature() {
        return this.customTemperature;
    }
    
    public void setCustomTemperature(final float customTemperature) {
        this.customTemperature = customTemperature;
    }
    
    public InventoryItem getItemFromType(String s, final IsoGameCharacter isoGameCharacter, final boolean b, final boolean b2, final boolean b3) {
        final InventoryItemList list = ItemContainer.TL_itemListPool.get().alloc();
        if (s.contains(".")) {
            s = s.split("\\.")[1];
        }
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            if (inventoryItem.getFullType().equals(s) || inventoryItem.getType().equals(s)) {
                if (!b || isoGameCharacter == null || !isoGameCharacter.isEquippedClothing(inventoryItem)) {
                    if (this.testBroken(b2, inventoryItem)) {
                        ItemContainer.TL_itemListPool.get().release(list);
                        return inventoryItem;
                    }
                }
            }
            else if (b3 && inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory() != null && !list.contains(inventoryItem)) {
                ((ArrayList<InventoryContainer>)list).add(inventoryItem);
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            final InventoryItem itemFromType = ((ArrayList<InventoryContainer>)list).get(j).getInventory().getItemFromType(s, isoGameCharacter, b, b2, b3);
            if (itemFromType != null) {
                ItemContainer.TL_itemListPool.get().release(list);
                return itemFromType;
            }
        }
        ItemContainer.TL_itemListPool.get().release(list);
        return null;
    }
    
    public InventoryItem getItemFromType(final String s, final boolean b, final boolean b2) {
        return this.getItemFromType(s, null, false, b, b2);
    }
    
    public InventoryItem getItemFromType(final String s) {
        return this.getFirstType(s);
    }
    
    public ArrayList<InventoryItem> getItemsFromType(final String s) {
        return this.getAllType(s);
    }
    
    public ArrayList<InventoryItem> getItemsFromFullType(final String s) {
        if (s == null || !s.contains(".")) {
            return new ArrayList<InventoryItem>();
        }
        return this.getAllType(s);
    }
    
    public ArrayList<InventoryItem> getItemsFromFullType(final String s, final boolean b) {
        if (s == null || !s.contains(".")) {
            return new ArrayList<InventoryItem>();
        }
        return b ? this.getAllTypeRecurse(s) : this.getAllType(s);
    }
    
    public ArrayList<InventoryItem> getItemsFromType(final String s, final boolean b) {
        return b ? this.getAllTypeRecurse(s) : this.getAllType(s);
    }
    
    public ArrayList<InventoryItem> getItemsFromCategory(final String s) {
        return this.getAllCategory(s);
    }
    
    public void sendContentsToRemoteContainer() {
        if (GameClient.bClient) {
            this.sendContentsToRemoteContainer(GameClient.connection);
        }
    }
    
    public void requestSync() {
        if (GameClient.bClient) {
            if (this.parent == null || this.parent.square == null || this.parent.square.chunk == null) {
                return;
            }
            GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(this.parent.square.chunk);
        }
    }
    
    public void requestServerItemsForContainer() {
        if (this.parent == null || this.parent.square == null) {
            return;
        }
        final UdpConnection connection = GameClient.connection;
        final ByteBufferWriter startPacket = connection.startPacket();
        PacketTypes.PacketType.RequestItemsForContainer.doPacket(startPacket);
        startPacket.putShort(IsoPlayer.getInstance().OnlineID);
        startPacket.putUTF(this.type);
        if (this.parent.square.getRoom() != null) {
            startPacket.putUTF(this.parent.square.getRoom().getName());
        }
        else {
            startPacket.putUTF("all");
        }
        startPacket.putInt(this.parent.square.getX());
        startPacket.putInt(this.parent.square.getY());
        startPacket.putInt(this.parent.square.getZ());
        final int index = this.parent.square.getObjects().indexOf(this.parent);
        if (index == -1 && this.parent.square.getStaticMovingObjects().indexOf(this.parent) != -1) {
            startPacket.putShort((short)0);
            startPacket.putByte((byte)this.parent.square.getStaticMovingObjects().indexOf(this.parent));
        }
        else if (this.parent instanceof IsoWorldInventoryObject) {
            startPacket.putShort((short)1);
            startPacket.putInt(((IsoWorldInventoryObject)this.parent).getItem().id);
        }
        else if (this.parent instanceof BaseVehicle) {
            startPacket.putShort((short)3);
            startPacket.putShort(((BaseVehicle)this.parent).VehicleID);
            startPacket.putByte((byte)this.vehiclePart.getIndex());
        }
        else {
            startPacket.putShort((short)2);
            startPacket.putByte((byte)index);
            startPacket.putByte((byte)this.parent.getContainerIndex(this));
        }
        PacketTypes.PacketType.RequestItemsForContainer.send(connection);
    }
    
    @Deprecated
    public void sendContentsToRemoteContainer(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
        startPacket.putInt(0);
        startPacket.putInt(this.parent.square.getX());
        startPacket.putInt(this.parent.square.getY());
        startPacket.putInt(this.parent.square.getZ());
        startPacket.putByte((byte)this.parent.square.getObjects().indexOf(this.parent));
        try {
            CompressIdenticalItems.save(startPacket.bb, this.Items, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
    }
    
    public InventoryItem getItemWithIDRecursiv(final int n) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.id == n) {
                return inventoryItem;
            }
            if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
                final InventoryItem itemWithIDRecursiv = ((InventoryContainer)inventoryItem).getItemContainer().getItemWithIDRecursiv(n);
                if (itemWithIDRecursiv != null) {
                    return itemWithIDRecursiv;
                }
            }
        }
        return null;
    }
    
    public InventoryItem getItemWithID(final int n) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.id == n) {
                return inventoryItem;
            }
        }
        return null;
    }
    
    public boolean removeItemWithID(final int n) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.id == n) {
                this.Remove(inventoryItem);
                return true;
            }
        }
        return false;
    }
    
    public boolean containsID(final int n) {
        for (int i = 0; i < this.Items.size(); ++i) {
            if (this.Items.get(i).id == n) {
                return true;
            }
        }
        return false;
    }
    
    public boolean removeItemWithIDRecurse(final int n) {
        for (int i = 0; i < this.Items.size(); ++i) {
            final InventoryItem inventoryItem = this.Items.get(i);
            if (inventoryItem.id == n) {
                this.Remove(inventoryItem);
                return true;
            }
            if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getInventory().removeItemWithIDRecurse(n)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isHasBeenLooted() {
        return this.hasBeenLooted;
    }
    
    public void setHasBeenLooted(final boolean hasBeenLooted) {
        this.hasBeenLooted = hasBeenLooted;
    }
    
    public String getOpenSound() {
        return this.openSound;
    }
    
    public void setOpenSound(final String openSound) {
        this.openSound = openSound;
    }
    
    public String getCloseSound() {
        return this.closeSound;
    }
    
    public void setCloseSound(final String closeSound) {
        this.closeSound = closeSound;
    }
    
    public String getPutSound() {
        return this.putSound;
    }
    
    public void setPutSound(final String putSound) {
        this.putSound = putSound;
    }
    
    public InventoryItem haveThisKeyId(final int n) {
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            if (inventoryItem instanceof Key) {
                final Key key = (Key)inventoryItem;
                if (key.getKeyId() == n) {
                    return key;
                }
            }
            else if (inventoryItem.getType().equals("KeyRing") && ((InventoryContainer)inventoryItem).getInventory().haveThisKeyId(n) != null) {
                return ((InventoryContainer)inventoryItem).getInventory().haveThisKeyId(n);
            }
        }
        return null;
    }
    
    public String getOnlyAcceptCategory() {
        return this.OnlyAcceptCategory;
    }
    
    public void setOnlyAcceptCategory(final String s) {
        this.OnlyAcceptCategory = StringUtils.discardNullOrWhitespace(s);
    }
    
    public String getAcceptItemFunction() {
        return this.AcceptItemFunction;
    }
    
    public void setAcceptItemFunction(final String s) {
        this.AcceptItemFunction = StringUtils.discardNullOrWhitespace(s);
    }
    
    public IsoGameCharacter getCharacter() {
        if (this.getParent() instanceof IsoGameCharacter) {
            return (IsoGameCharacter)this.getParent();
        }
        if (this.containingItem != null && this.containingItem.getContainer() != null) {
            return this.containingItem.getContainer().getCharacter();
        }
        return null;
    }
    
    public void emptyIt() {
        this.Items = new ArrayList<InventoryItem>();
    }
    
    public LinkedHashMap<String, InventoryItem> getItems4Admin() {
        final LinkedHashMap<Object, InventoryItem> linkedHashMap = (LinkedHashMap<Object, InventoryItem>)new LinkedHashMap<String, InventoryItem>();
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            inventoryItem.setCount(1);
            if (inventoryItem.getCat() != ItemType.Drainable && inventoryItem.getCat() != ItemType.Weapon && linkedHashMap.get(inventoryItem.getFullType()) != null && !(inventoryItem instanceof InventoryContainer)) {
                linkedHashMap.get(inventoryItem.getFullType()).setCount(linkedHashMap.get(inventoryItem.getFullType()).getCount() + 1);
            }
            else if (linkedHashMap.get(inventoryItem.getFullType()) != null) {
                linkedHashMap.put(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, inventoryItem.getFullType(), Rand.Next(100000)), inventoryItem);
            }
            else {
                linkedHashMap.put(inventoryItem.getFullType(), inventoryItem);
            }
        }
        return (LinkedHashMap<String, InventoryItem>)linkedHashMap;
    }
    
    public LinkedHashMap<String, InventoryItem> getAllItems(LinkedHashMap<String, InventoryItem> allItems, final boolean b) {
        if (allItems == null) {
            allItems = new LinkedHashMap<String, InventoryItem>();
        }
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            if (b) {
                inventoryItem.setWorker("inInv");
            }
            inventoryItem.setCount(1);
            if (inventoryItem.getCat() != ItemType.Drainable && inventoryItem.getCat() != ItemType.Weapon && allItems.get(inventoryItem.getFullType()) != null) {
                allItems.get(inventoryItem.getFullType()).setCount(allItems.get(inventoryItem.getFullType()).getCount() + 1);
            }
            else if (allItems.get(inventoryItem.getFullType()) != null) {
                allItems.put(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, inventoryItem.getFullType(), Rand.Next(100000)), (InventoryContainer)inventoryItem);
            }
            else {
                allItems.put(inventoryItem.getFullType(), inventoryItem);
            }
            if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
                allItems = ((InventoryContainer)inventoryItem).getItemContainer().getAllItems(allItems, true);
            }
        }
        return allItems;
    }
    
    public InventoryItem getItemById(final long n) {
        for (int i = 0; i < this.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getItems().get(i);
            if (inventoryItem.getID() == n) {
                return inventoryItem;
            }
            if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
                final InventoryItem itemById = ((InventoryContainer)inventoryItem).getItemContainer().getItemById(n);
                if (itemById != null) {
                    return itemById;
                }
            }
        }
        return null;
    }
    
    public void addItemsToProcessItems() {
        IsoWorld.instance.CurrentCell.addToProcessItems(this.Items);
    }
    
    public void removeItemsFromProcessItems() {
        IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.Items);
        if (!"floor".equals(this.type)) {
            ItemSoundManager.removeItems(this.Items);
        }
    }
    
    public boolean isExistYet() {
        if (!SystemDisabler.doWorldSyncEnable) {
            return true;
        }
        if (this.getCharacter() != null) {
            return true;
        }
        if (this.getParent() instanceof BaseVehicle) {
            return true;
        }
        if (this.parent instanceof IsoDeadBody) {
            return this.parent.getStaticMovingObjectIndex() != -1;
        }
        if (this.parent instanceof IsoCompost) {
            return this.parent.getObjectIndex() != -1;
        }
        if (this.containingItem != null && this.containingItem.worldItem != null) {
            return this.containingItem.worldItem.getWorldObjectIndex() != -1;
        }
        return this.getType().equals("floor") || (this.SourceGrid != null && this.SourceGrid.getObjects().contains(this.parent) && this.parent.getContainerIndex(this) != -1);
    }
    
    public String getContainerPosition() {
        return this.containerPosition;
    }
    
    public void setContainerPosition(final String containerPosition) {
        this.containerPosition = containerPosition;
    }
    
    public String getFreezerPosition() {
        return this.freezerPosition;
    }
    
    public void setFreezerPosition(final String freezerPosition) {
        this.freezerPosition = freezerPosition;
    }
    
    public VehiclePart getVehiclePart() {
        return this.vehiclePart;
    }
    
    static {
        tempList = new ArrayList<InventoryItem>();
        s_tempObjects = new ArrayList<IsoObject>();
        TL_comparators = ThreadLocal.withInitial((Supplier<? extends Comparators>)Comparators::new);
        TL_itemListPool = ThreadLocal.withInitial((Supplier<? extends InventoryItemListPool>)InventoryItemListPool::new);
        TL_predicates = ThreadLocal.withInitial((Supplier<? extends Predicates>)Predicates::new);
    }
    
    private static final class ConditionComparator implements Comparator<InventoryItem>
    {
        @Override
        public int compare(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
            return inventoryItem.getCondition() - inventoryItem2.getCondition();
        }
    }
    
    private static final class EvalComparator implements Comparator<InventoryItem>
    {
        LuaClosure functionObj;
        
        EvalComparator init(final LuaClosure obj) {
            this.functionObj = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public int compare(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
            final LuaReturn protectedCall = LuaManager.caller.protectedCall(LuaManager.thread, (Object)this.functionObj, new Object[] { inventoryItem, inventoryItem2 });
            if (protectedCall.isSuccess() && !protectedCall.isEmpty() && protectedCall.getFirst() instanceof Double) {
                return Double.compare((double)protectedCall.getFirst(), 0.0);
            }
            return 0;
        }
    }
    
    private static final class EvalArgComparator implements Comparator<InventoryItem>
    {
        LuaClosure functionObj;
        Object arg;
        
        EvalArgComparator init(final LuaClosure obj, final Object arg) {
            this.functionObj = Objects.requireNonNull(obj);
            this.arg = arg;
            return this;
        }
        
        @Override
        public int compare(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
            final LuaReturn protectedCall = LuaManager.caller.protectedCall(LuaManager.thread, (Object)this.functionObj, new Object[] { inventoryItem, inventoryItem2, this.arg });
            if (protectedCall.isSuccess() && !protectedCall.isEmpty() && protectedCall.getFirst() instanceof Double) {
                return Double.compare((double)protectedCall.getFirst(), 0.0);
            }
            return 0;
        }
    }
    
    private static final class Comparators
    {
        ObjectPool<ConditionComparator> condition;
        ObjectPool<EvalComparator> eval;
        ObjectPool<EvalArgComparator> evalArg;
        
        private Comparators() {
            this.condition = new ObjectPool<ConditionComparator>(ConditionComparator::new);
            this.eval = new ObjectPool<EvalComparator>(EvalComparator::new);
            this.evalArg = new ObjectPool<EvalArgComparator>(EvalArgComparator::new);
        }
    }
    
    private static final class InventoryItemList extends ArrayList<InventoryItem>
    {
        @Override
        public boolean equals(final Object o) {
            return this == o;
        }
    }
    
    private static final class InventoryItemListPool extends ObjectPool<InventoryItemList>
    {
        public InventoryItemListPool() {
            super(InventoryItemList::new);
        }
        
        @Override
        public void release(final InventoryItemList list) {
            list.clear();
            super.release(list);
        }
    }
    
    private static final class CategoryPredicate implements Predicate<InventoryItem>
    {
        String category;
        
        CategoryPredicate init(final String obj) {
            this.category = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return inventoryItem.getCategory().equals(this.category);
        }
    }
    
    private static final class EvalPredicate implements Predicate<InventoryItem>
    {
        LuaClosure functionObj;
        
        EvalPredicate init(final LuaClosure obj) {
            this.functionObj = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return LuaManager.caller.protectedCallBoolean(LuaManager.thread, (Object)this.functionObj, (Object)inventoryItem) == Boolean.TRUE;
        }
    }
    
    private static final class EvalArgPredicate implements Predicate<InventoryItem>
    {
        LuaClosure functionObj;
        Object arg;
        
        EvalArgPredicate init(final LuaClosure obj, final Object arg) {
            this.functionObj = Objects.requireNonNull(obj);
            this.arg = arg;
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return LuaManager.caller.protectedCallBoolean(LuaManager.thread, (Object)this.functionObj, (Object)inventoryItem, this.arg) == Boolean.TRUE;
        }
    }
    
    private static final class TagPredicate implements Predicate<InventoryItem>
    {
        String tag;
        
        TagPredicate init(final String obj) {
            this.tag = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return inventoryItem.hasTag(this.tag);
        }
    }
    
    private static final class TagEvalPredicate implements Predicate<InventoryItem>
    {
        String tag;
        LuaClosure functionObj;
        
        TagEvalPredicate init(final String tag, final LuaClosure obj) {
            this.tag = tag;
            this.functionObj = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return inventoryItem.hasTag(this.tag) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, (Object)this.functionObj, (Object)inventoryItem) == Boolean.TRUE;
        }
    }
    
    private static final class TagEvalArgPredicate implements Predicate<InventoryItem>
    {
        String tag;
        LuaClosure functionObj;
        Object arg;
        
        TagEvalArgPredicate init(final String tag, final LuaClosure obj, final Object arg) {
            this.tag = tag;
            this.functionObj = Objects.requireNonNull(obj);
            this.arg = arg;
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return inventoryItem.hasTag(this.tag) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, (Object)this.functionObj, (Object)inventoryItem, this.arg) == Boolean.TRUE;
        }
    }
    
    private static final class TypePredicate implements Predicate<InventoryItem>
    {
        String type;
        
        TypePredicate init(final String obj) {
            this.type = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return ItemContainer.compareType(this.type, inventoryItem);
        }
    }
    
    private static final class TypeEvalPredicate implements Predicate<InventoryItem>
    {
        String type;
        LuaClosure functionObj;
        
        TypeEvalPredicate init(final String type, final LuaClosure obj) {
            this.type = type;
            this.functionObj = Objects.requireNonNull(obj);
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return ItemContainer.compareType(this.type, inventoryItem) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, (Object)this.functionObj, (Object)inventoryItem) == Boolean.TRUE;
        }
    }
    
    private static final class TypeEvalArgPredicate implements Predicate<InventoryItem>
    {
        String type;
        LuaClosure functionObj;
        Object arg;
        
        TypeEvalArgPredicate init(final String type, final LuaClosure obj, final Object arg) {
            this.type = type;
            this.functionObj = Objects.requireNonNull(obj);
            this.arg = arg;
            return this;
        }
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            return ItemContainer.compareType(this.type, inventoryItem) && LuaManager.caller.protectedCallBoolean(LuaManager.thread, (Object)this.functionObj, (Object)inventoryItem, this.arg) == Boolean.TRUE;
        }
    }
    
    private static final class Predicates
    {
        final ObjectPool<CategoryPredicate> category;
        final ObjectPool<EvalPredicate> eval;
        final ObjectPool<EvalArgPredicate> evalArg;
        final ObjectPool<TagPredicate> tag;
        final ObjectPool<TagEvalPredicate> tagEval;
        final ObjectPool<TagEvalArgPredicate> tagEvalArg;
        final ObjectPool<TypePredicate> type;
        final ObjectPool<TypeEvalPredicate> typeEval;
        final ObjectPool<TypeEvalArgPredicate> typeEvalArg;
        
        private Predicates() {
            this.category = new ObjectPool<CategoryPredicate>(CategoryPredicate::new);
            this.eval = new ObjectPool<EvalPredicate>(EvalPredicate::new);
            this.evalArg = new ObjectPool<EvalArgPredicate>(EvalArgPredicate::new);
            this.tag = new ObjectPool<TagPredicate>(TagPredicate::new);
            this.tagEval = new ObjectPool<TagEvalPredicate>(TagEvalPredicate::new);
            this.tagEvalArg = new ObjectPool<TagEvalArgPredicate>(TagEvalArgPredicate::new);
            this.type = new ObjectPool<TypePredicate>(TypePredicate::new);
            this.typeEval = new ObjectPool<TypeEvalPredicate>(TypeEvalPredicate::new);
            this.typeEvalArg = new ObjectPool<TypeEvalArgPredicate>(TypeEvalArgPredicate::new);
        }
    }
}
