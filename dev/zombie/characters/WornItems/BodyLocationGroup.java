// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.WornItems;

import java.util.ArrayList;

public final class BodyLocationGroup
{
    protected final String id;
    protected final ArrayList<BodyLocation> locations;
    
    public BodyLocationGroup(final String id) {
        this.locations = new ArrayList<BodyLocation>();
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        }
        this.id = id;
    }
    
    public BodyLocation getLocation(final String s) {
        for (int i = 0; i < this.locations.size(); ++i) {
            final BodyLocation bodyLocation = this.locations.get(i);
            if (bodyLocation.isID(s)) {
                return bodyLocation;
            }
        }
        return null;
    }
    
    public BodyLocation getLocationNotNull(final String s) {
        final BodyLocation location = this.getLocation(s);
        if (location == null) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        return location;
    }
    
    public BodyLocation getOrCreateLocation(final String s) {
        BodyLocation location = this.getLocation(s);
        if (location == null) {
            location = new BodyLocation(this, s);
            this.locations.add(location);
        }
        return location;
    }
    
    public BodyLocation getLocationByIndex(final int index) {
        if (index >= 0 && index < this.size()) {
            return this.locations.get(index);
        }
        return null;
    }
    
    public int size() {
        return this.locations.size();
    }
    
    public void setExclusive(final String exclusive, final String exclusive2) {
        final BodyLocation locationNotNull = this.getLocationNotNull(exclusive);
        final BodyLocation locationNotNull2 = this.getLocationNotNull(exclusive2);
        locationNotNull.setExclusive(exclusive2);
        locationNotNull2.setExclusive(exclusive);
    }
    
    public boolean isExclusive(final String s, final String o) {
        final BodyLocation locationNotNull = this.getLocationNotNull(s);
        this.checkValid(o);
        return locationNotNull.exclusive.contains(o);
    }
    
    public void setHideModel(final String s, final String hideModel) {
        final BodyLocation locationNotNull = this.getLocationNotNull(s);
        this.checkValid(hideModel);
        locationNotNull.setHideModel(hideModel);
    }
    
    public boolean isHideModel(final String s, final String s2) {
        final BodyLocation locationNotNull = this.getLocationNotNull(s);
        this.checkValid(s2);
        return locationNotNull.isHideModel(s2);
    }
    
    public int indexOf(final String s) {
        for (int i = 0; i < this.locations.size(); ++i) {
            if (this.locations.get(i).isID(s)) {
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
    
    public void setMultiItem(final String s, final boolean multiItem) {
        this.getLocationNotNull(s).setMultiItem(multiItem);
    }
    
    public boolean isMultiItem(final String s) {
        return this.getLocationNotNull(s).isMultiItem();
    }
    
    public ArrayList<BodyLocation> getAllLocations() {
        return this.locations;
    }
}
