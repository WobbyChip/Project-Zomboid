// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import zombie.util.Type;
import zombie.inventory.types.HandWeapon;
import zombie.util.StringUtils;
import java.util.ArrayList;

public final class AttachedModelNames
{
    protected AttachedLocationGroup group;
    protected final ArrayList<AttachedModelName> models;
    
    public AttachedModelNames() {
        this.models = new ArrayList<AttachedModelName>();
    }
    
    AttachedLocationGroup getGroup() {
        return this.group;
    }
    
    public void copyFrom(final AttachedModelNames attachedModelNames) {
        this.models.clear();
        for (int i = 0; i < attachedModelNames.models.size(); ++i) {
            this.models.add(new AttachedModelName(attachedModelNames.models.get(i)));
        }
    }
    
    public void initFrom(final AttachedItems attachedItems) {
        this.group = attachedItems.getGroup();
        this.models.clear();
        for (int i = 0; i < attachedItems.size(); ++i) {
            final AttachedItem value = attachedItems.get(i);
            final String staticModel = value.getItem().getStaticModel();
            if (!StringUtils.isNullOrWhitespace(staticModel)) {
                final String attachmentName = this.group.getLocation(value.getLocation()).getAttachmentName();
                final HandWeapon handWeapon = Type.tryCastTo(value.getItem(), HandWeapon.class);
                this.models.add(new AttachedModelName(attachmentName, staticModel, (handWeapon == null) ? 0.0f : handWeapon.getBloodLevel()));
            }
        }
    }
    
    public int size() {
        return this.models.size();
    }
    
    public AttachedModelName get(final int index) {
        return this.models.get(index);
    }
    
    public void clear() {
        this.models.clear();
    }
}
