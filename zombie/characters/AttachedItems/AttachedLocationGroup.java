// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import java.util.ArrayList;

public final class AttachedLocationGroup
{
    protected final String id;
    protected final ArrayList<AttachedLocation> locations;
    
    public AttachedLocationGroup(final String id) {
        this.locations = new ArrayList<AttachedLocation>();
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        }
        this.id = id;
    }
    
    public AttachedLocation getLocation(final String anObject) {
        for (int i = 0; i < this.locations.size(); ++i) {
            final AttachedLocation attachedLocation = this.locations.get(i);
            if (attachedLocation.id.equals(anObject)) {
                return attachedLocation;
            }
        }
        return null;
    }
    
    public AttachedLocation getOrCreateLocation(final String s) {
        AttachedLocation location = this.getLocation(s);
        if (location == null) {
            location = new AttachedLocation(this, s);
            this.locations.add(location);
        }
        return location;
    }
    
    public AttachedLocation getLocationByIndex(final int index) {
        if (index >= 0 && index < this.size()) {
            return this.locations.get(index);
        }
        return null;
    }
    
    public int size() {
        return this.locations.size();
    }
    
    public int indexOf(final String anObject) {
        for (int i = 0; i < this.locations.size(); ++i) {
            if (this.locations.get(i).id.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    public void checkValid(final String s) {
        if (s == null) {
            throw new NullPointerException("locationId is null");
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException("locationId is empty");
        }
        if (this.indexOf(s) == -1) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
}
