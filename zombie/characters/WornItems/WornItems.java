// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.WornItems;

import zombie.inventory.ItemContainer;
import zombie.inventory.types.InventoryContainer;
import zombie.util.StringUtils;
import zombie.inventory.types.Clothing;
import zombie.inventory.InventoryItemFactory;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import java.util.function.Consumer;
import zombie.inventory.InventoryItem;
import java.util.Collection;
import java.util.ArrayList;

public final class WornItems
{
    protected final BodyLocationGroup group;
    protected final ArrayList<WornItem> items;
    
    public WornItems(final BodyLocationGroup group) {
        this.items = new ArrayList<WornItem>();
        this.group = group;
    }
    
    public WornItems(final WornItems wornItems) {
        this.items = new ArrayList<WornItem>();
        this.group = wornItems.group;
        this.copyFrom(wornItems);
    }
    
    public void copyFrom(final WornItems wornItems) {
        if (this.group != wornItems.group) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.group.id, wornItems.group.id));
        }
        this.items.clear();
        this.items.addAll(wornItems.items);
    }
    
    public BodyLocationGroup getBodyLocationGroup() {
        return this.group;
    }
    
    public WornItem get(final int index) {
        return this.items.get(index);
    }
    
    public void setItem(final String s, final InventoryItem inventoryItem) {
        this.group.checkValid(s);
        if (!this.group.isMultiItem(s)) {
            final int index = this.indexOf(s);
            if (index != -1) {
                this.items.remove(index);
            }
        }
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.group.isExclusive(s, this.items.get(i).location)) {
                this.items.remove(i--);
            }
        }
        if (inventoryItem == null) {
            return;
        }
        this.remove(inventoryItem);
        int size = this.items.size();
        for (int j = 0; j < this.items.size(); ++j) {
            if (this.group.indexOf(this.items.get(j).getLocation()) > this.group.indexOf(s)) {
                size = j;
                break;
            }
        }
        this.items.add(size, new WornItem(s, inventoryItem));
    }
    
    public InventoryItem getItem(final String s) {
        this.group.checkValid(s);
        final int index = this.indexOf(s);
        if (index == -1) {
            return null;
        }
        return this.items.get(index).item;
    }
    
    public InventoryItem getItemByIndex(final int index) {
        if (index < 0 || index >= this.items.size()) {
            return null;
        }
        return this.items.get(index).getItem();
    }
    
    public void remove(final InventoryItem inventoryItem) {
        final int index = this.indexOf(inventoryItem);
        if (index == -1) {
            return;
        }
        this.items.remove(index);
    }
    
    public void clear() {
        this.items.clear();
    }
    
    public String getLocation(final InventoryItem inventoryItem) {
        final int index = this.indexOf(inventoryItem);
        if (index == -1) {
            return null;
        }
        return this.items.get(index).getLocation();
    }
    
    public boolean contains(final InventoryItem inventoryItem) {
        return this.indexOf(inventoryItem) != -1;
    }
    
    public int size() {
        return this.items.size();
    }
    
    public boolean isEmpty() {
        return this.items.isEmpty();
    }
    
    public void forEach(final Consumer<WornItem> consumer) {
        for (int i = 0; i < this.items.size(); ++i) {
            consumer.accept(this.items.get(i));
        }
    }
    
    public void setFromItemVisuals(final ItemVisuals itemVisuals) {
        this.clear();
        for (int i = 0; i < itemVisuals.size(); ++i) {
            final ItemVisual itemVisual = itemVisuals.get(i);
            final InventoryItem createItem = InventoryItemFactory.CreateItem(itemVisual.getItemType());
            if (createItem != null) {
                if (createItem.getVisual() != null) {
                    createItem.getVisual().copyFrom(itemVisual);
                    createItem.synchWithVisual();
                }
                if (createItem instanceof Clothing && !StringUtils.isNullOrWhitespace(createItem.getBodyLocation())) {
                    this.setItem(createItem.getBodyLocation(), createItem);
                }
                else if (createItem instanceof InventoryContainer && !StringUtils.isNullOrWhitespace(((InventoryContainer)createItem).canBeEquipped())) {
                    this.setItem(((InventoryContainer)createItem).canBeEquipped(), createItem);
                }
            }
        }
    }
    
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        itemVisuals.clear();
        for (int i = 0; i < this.items.size(); ++i) {
            final InventoryItem item = this.items.get(i).getItem();
            final ItemVisual visual = item.getVisual();
            if (visual != null) {
                visual.setInventoryItem(item);
                itemVisuals.add(visual);
            }
        }
    }
    
    public void addItemsToItemContainer(final ItemContainer itemContainer) {
        for (int i = 0; i < this.items.size(); ++i) {
            final InventoryItem item = this.items.get(i).getItem();
            item.setCondition(item.getConditionMax() - item.getVisual().getHolesNumber() * 3);
            itemContainer.AddItem(item);
        }
    }
    
    private int indexOf(final String anObject) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).location.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    private int indexOf(final InventoryItem inventoryItem) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).getItem() == inventoryItem) {
                return i;
            }
        }
        return -1;
    }
}
