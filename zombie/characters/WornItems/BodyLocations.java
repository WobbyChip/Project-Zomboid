// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.WornItems;

import java.util.ArrayList;

public final class BodyLocations
{
    protected static final ArrayList<BodyLocationGroup> groups;
    
    public static BodyLocationGroup getGroup(final String anObject) {
        for (int i = 0; i < BodyLocations.groups.size(); ++i) {
            final BodyLocationGroup bodyLocationGroup = BodyLocations.groups.get(i);
            if (bodyLocationGroup.id.equals(anObject)) {
                return bodyLocationGroup;
            }
        }
        final BodyLocationGroup e = new BodyLocationGroup(anObject);
        BodyLocations.groups.add(e);
        return e;
    }
    
    public static void Reset() {
        BodyLocations.groups.clear();
    }
    
    static {
        groups = new ArrayList<BodyLocationGroup>();
    }
}
