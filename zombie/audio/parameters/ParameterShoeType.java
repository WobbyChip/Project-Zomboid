// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.scripting.objects.Item;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.audio.FMODLocalParameter;

public final class ParameterShoeType extends FMODLocalParameter
{
    private static final ItemVisuals tempItemVisuals;
    private final IsoGameCharacter character;
    private ShoeType shoeType;
    
    public ParameterShoeType(final IsoGameCharacter character) {
        super("ShoeType");
        this.shoeType = null;
        this.character = character;
    }
    
    @Override
    public float calculateCurrentValue() {
        if (this.shoeType == null) {
            this.shoeType = this.getShoeType();
        }
        return (float)this.shoeType.label;
    }
    
    private ShoeType getShoeType() {
        this.character.getItemVisuals(ParameterShoeType.tempItemVisuals);
        Item item = null;
        for (int i = 0; i < ParameterShoeType.tempItemVisuals.size(); ++i) {
            final Item scriptItem = ParameterShoeType.tempItemVisuals.get(i).getScriptItem();
            if (scriptItem != null && "Shoes".equals(scriptItem.getBodyLocation())) {
                item = scriptItem;
                break;
            }
        }
        if (item == null) {
            return ShoeType.Barefoot;
        }
        final String name = item.getName();
        if (name.contains("Boots") || name.contains("Wellies")) {
            return ShoeType.Boots;
        }
        if (name.contains("FlipFlop")) {
            return ShoeType.FlipFlops;
        }
        if (name.contains("Slippers")) {
            return ShoeType.Slippers;
        }
        if (name.contains("Trainer")) {
            return ShoeType.Sneakers;
        }
        return ShoeType.Shoes;
    }
    
    public void setShoeType(final ShoeType shoeType) {
        this.shoeType = shoeType;
    }
    
    static {
        tempItemVisuals = new ItemVisuals();
    }
    
    private enum ShoeType
    {
        Barefoot(0), 
        Boots(1), 
        FlipFlops(2), 
        Shoes(3), 
        Slippers(4), 
        Sneakers(5);
        
        final int label;
        
        private ShoeType(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ ShoeType[] $values() {
            return new ShoeType[] { ShoeType.Barefoot, ShoeType.Boots, ShoeType.FlipFlops, ShoeType.Shoes, ShoeType.Slippers, ShoeType.Sneakers };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
