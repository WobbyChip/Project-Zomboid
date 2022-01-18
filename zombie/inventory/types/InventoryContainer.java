// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.core.math.PZMath;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.core.Translator;
import java.util.HashSet;
import zombie.ui.ObjectTooltip;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemContainer;
import zombie.inventory.InventoryItem;

public final class InventoryContainer extends InventoryItem
{
    ItemContainer container;
    int capacity;
    int weightReduction;
    private String CanBeEquipped;
    
    public InventoryContainer(final String s, final String s2, final String type, final String s3) {
        super(s, s2, type, s3);
        this.container = new ItemContainer();
        this.capacity = 0;
        this.weightReduction = 0;
        this.CanBeEquipped = "";
        this.container.containingItem = this;
        this.container.type = type;
        this.container.inventoryContainer = this;
    }
    
    @Override
    public boolean IsInventoryContainer() {
        return true;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Container.ordinal();
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Container";
    }
    
    public ItemContainer getInventory() {
        return this.container;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putInt(this.container.ID);
        byteBuffer.putInt(this.weightReduction);
        this.container.save(byteBuffer);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        final int int1 = byteBuffer.getInt();
        this.setWeightReduction(byteBuffer.getInt());
        if (this.container == null) {
            this.container = new ItemContainer();
        }
        this.container.clear();
        this.container.containingItem = this;
        this.container.setWeightReduction(this.weightReduction);
        this.container.Capacity = this.capacity;
        this.container.ID = int1;
        this.container.load(byteBuffer, n);
        this.synchWithVisual();
    }
    
    public int getCapacity() {
        return this.container.getCapacity();
    }
    
    public void setCapacity(final int n) {
        this.capacity = n;
        if (this.container == null) {
            this.container = new ItemContainer();
        }
        this.container.Capacity = n;
    }
    
    public float getInventoryWeight() {
        if (this.getInventory() == null) {
            return 0.0f;
        }
        float n = 0.0f;
        final ArrayList<InventoryItem> items = this.getInventory().getItems();
        for (int i = 0; i < items.size(); ++i) {
            final InventoryItem inventoryItem = items.get(i);
            if (this.isEquipped()) {
                n += inventoryItem.getEquippedWeight();
            }
            else {
                n += inventoryItem.getUnequippedWeight();
            }
        }
        return n;
    }
    
    public int getEffectiveCapacity(final IsoGameCharacter isoGameCharacter) {
        return this.container.getEffectiveCapacity(isoGameCharacter);
    }
    
    public int getWeightReduction() {
        return this.weightReduction;
    }
    
    public void setWeightReduction(int n) {
        n = Math.min(n, 100);
        n = Math.max(n, 0);
        this.weightReduction = n;
        this.container.setWeightReduction(n);
    }
    
    @Override
    public void updateAge() {
        final ArrayList<InventoryItem> items = this.getInventory().getItems();
        for (int i = 0; i < items.size(); ++i) {
            items.get(i).updateAge();
        }
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip) {
        objectTooltip.render();
        super.DoTooltip(objectTooltip);
        int n = objectTooltip.getHeight().intValue() - objectTooltip.padBottom;
        if (objectTooltip.getWidth() < 160.0) {
            objectTooltip.setWidth(160.0);
        }
        if (!this.getItemContainer().getItems().isEmpty()) {
            int n2 = 5;
            n += 4;
            final HashSet<String> set = new HashSet<String>();
            for (int i = this.getItemContainer().getItems().size() - 1; i >= 0; --i) {
                final InventoryItem inventoryItem = this.getItemContainer().getItems().get(i);
                if (inventoryItem.getName() != null) {
                    if (set.contains(inventoryItem.getName())) {
                        continue;
                    }
                    set.add(inventoryItem.getName());
                }
                objectTooltip.DrawTextureScaledAspect(inventoryItem.getTex(), n2, n, 16.0, 16.0, 1.0, 1.0, 1.0, 1.0);
                n2 += 17;
                if (n2 + 16 > objectTooltip.width - objectTooltip.padRight) {
                    break;
                }
            }
            n += 16;
        }
        objectTooltip.setHeight(n + objectTooltip.padBottom);
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
        final float n = 0.0f;
        final float n2 = 0.6f;
        final float n3 = 0.0f;
        final float n4 = 0.7f;
        if (this.getEffectiveCapacity(objectTooltip.getCharacter()) != 0) {
            final ObjectTooltip.LayoutItem addItem = layout.addItem();
            addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_container_Capacity")), 1.0f, 1.0f, 1.0f, 1.0f);
            addItem.setValueRightNoPlus(this.getEffectiveCapacity(objectTooltip.getCharacter()));
        }
        if (this.getWeightReduction() != 0) {
            final ObjectTooltip.LayoutItem addItem2 = layout.addItem();
            addItem2.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_container_Weight_Reduction")), 1.0f, 1.0f, 1.0f, 1.0f);
            addItem2.setValueRightNoPlus(this.getWeightReduction());
        }
        if (this.getBloodClothingType() != null) {
            final float bloodLevel = this.getBloodLevel();
            if (bloodLevel != 0.0f) {
                final ObjectTooltip.LayoutItem addItem3 = layout.addItem();
                addItem3.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_clothing_bloody")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem3.setProgress(bloodLevel, n, n2, n3, n4);
            }
        }
    }
    
    public void setBloodLevel(final float n) {
        final ArrayList<BloodBodyPartType> coveredParts = BloodClothingType.getCoveredParts(this.getBloodClothingType());
        for (int i = 0; i < coveredParts.size(); ++i) {
            this.setBlood(coveredParts.get(i), PZMath.clamp(n, 0.0f, 100.0f));
        }
    }
    
    public float getBloodLevel() {
        final ArrayList<BloodBodyPartType> coveredParts = BloodClothingType.getCoveredParts(this.getBloodClothingType());
        float n = 0.0f;
        for (int i = 0; i < coveredParts.size(); ++i) {
            n += this.getBlood(coveredParts.get(i));
        }
        return n;
    }
    
    public void setCanBeEquipped(final String s) {
        this.CanBeEquipped = ((s == null) ? "" : s);
    }
    
    public String canBeEquipped() {
        return this.CanBeEquipped;
    }
    
    public ItemContainer getItemContainer() {
        return this.container;
    }
    
    public void setItemContainer(final ItemContainer container) {
        this.container = container;
    }
    
    @Override
    public float getContentsWeight() {
        return this.getInventory().getContentsWeight();
    }
    
    @Override
    public float getEquippedWeight() {
        float n = 1.0f;
        if (this.getWeightReduction() > 0) {
            n = 1.0f - this.getWeightReduction() / 100.0f;
        }
        return this.getActualWeight() * 0.3f + this.getContentsWeight() * n;
    }
    
    public String getClothingExtraSubmenu() {
        return this.ScriptItem.clothingExtraSubmenu;
    }
}
