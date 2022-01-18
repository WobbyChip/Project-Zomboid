// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.visual;

import zombie.core.skinnedmodel.population.ClothingItem;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class ItemVisuals extends ArrayList<ItemVisual>
{
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putShort((short)this.size());
        for (int i = 0; i < this.size(); ++i) {
            this.get(i).save(byteBuffer);
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.clear();
        for (short short1 = byteBuffer.getShort(), n2 = 0; n2 < short1; ++n2) {
            final ItemVisual e = new ItemVisual();
            e.load(byteBuffer, n);
            this.add(e);
        }
    }
    
    public ItemVisual findHat() {
        for (int i = 0; i < this.size(); ++i) {
            final ItemVisual itemVisual = this.get(i);
            final ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem != null) {
                if (clothingItem.isHat()) {
                    return itemVisual;
                }
            }
        }
        return null;
    }
    
    public ItemVisual findMask() {
        for (int i = 0; i < this.size(); ++i) {
            final ItemVisual itemVisual = this.get(i);
            final ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem != null) {
                if (clothingItem.isMask()) {
                    return itemVisual;
                }
            }
        }
        return null;
    }
}
