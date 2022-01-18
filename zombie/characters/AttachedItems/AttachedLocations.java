// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import java.util.ArrayList;

public final class AttachedLocations
{
    protected static final ArrayList<AttachedLocationGroup> groups;
    
    public static AttachedLocationGroup getGroup(final String anObject) {
        for (int i = 0; i < AttachedLocations.groups.size(); ++i) {
            final AttachedLocationGroup attachedLocationGroup = AttachedLocations.groups.get(i);
            if (attachedLocationGroup.id.equals(anObject)) {
                return attachedLocationGroup;
            }
        }
        final AttachedLocationGroup e = new AttachedLocationGroup(anObject);
        AttachedLocations.groups.add(e);
        return e;
    }
    
    public static void Reset() {
        AttachedLocations.groups.clear();
    }
    
    static {
        groups = new ArrayList<AttachedLocationGroup>();
    }
}
