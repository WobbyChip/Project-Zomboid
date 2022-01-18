// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import java.util.function.Consumer;
import zombie.inventory.InventoryItem;
import java.util.Collection;
import java.util.ArrayList;

public final class AttachedItems
{
    protected final AttachedLocationGroup group;
    protected final ArrayList<AttachedItem> items;
    
    public AttachedItems(final AttachedLocationGroup group) {
        this.items = new ArrayList<AttachedItem>();
        this.group = group;
    }
    
    public AttachedItems(final AttachedItems attachedItems) {
        this.items = new ArrayList<AttachedItem>();
        this.group = attachedItems.group;
        this.copyFrom(attachedItems);
    }
    
    public void copyFrom(final AttachedItems attachedItems) {
        if (this.group != attachedItems.group) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.group.id, attachedItems.group.id));
        }
        this.items.clear();
        this.items.addAll(attachedItems.items);
    }
    
    public AttachedLocationGroup getGroup() {
        return this.group;
    }
    
    public AttachedItem get(final int index) {
        return this.items.get(index);
    }
    
    public void setItem(final String s, final InventoryItem inventoryItem) {
        this.group.checkValid(s);
        final int index = this.indexOf(s);
        if (index != -1) {
            this.items.remove(index);
        }
        if (inventoryItem == null) {
            return;
        }
        this.remove(inventoryItem);
        int size = this.items.size();
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.group.indexOf(this.items.get(i).getLocation()) > this.group.indexOf(s)) {
                size = i;
                break;
            }
        }
        this.items.add(size, new AttachedItem(s, inventoryItem));
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
    
    public void forEach(final Consumer<AttachedItem> consumer) {
        for (int i = 0; i < this.items.size(); ++i) {
            consumer.accept(this.items.get(i));
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
