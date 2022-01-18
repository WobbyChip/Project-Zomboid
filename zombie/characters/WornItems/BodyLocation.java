// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.WornItems;

import java.util.ArrayList;

public final class BodyLocation
{
    protected final BodyLocationGroup group;
    protected final String id;
    protected final ArrayList<String> aliases;
    protected final ArrayList<String> exclusive;
    protected final ArrayList<String> hideModel;
    protected boolean bMultiItem;
    
    public BodyLocation(final BodyLocationGroup group, final String id) {
        this.aliases = new ArrayList<String>();
        this.exclusive = new ArrayList<String>();
        this.hideModel = new ArrayList<String>();
        this.bMultiItem = false;
        this.checkId(id, "id");
        this.group = group;
        this.id = id;
    }
    
    public BodyLocation addAlias(final String s) {
        this.checkId(s, "alias");
        if (this.aliases.contains(s)) {
            return this;
        }
        this.aliases.add(s);
        return this;
    }
    
    public BodyLocation setExclusive(final String e) {
        this.checkId(e, "otherId");
        if (this.aliases.contains(e)) {
            return this;
        }
        if (this.exclusive.contains(e)) {
            return this;
        }
        this.exclusive.add(e);
        return this;
    }
    
    public BodyLocation setHideModel(final String s) {
        this.checkId(s, "otherId");
        if (this.hideModel.contains(s)) {
            return this;
        }
        this.hideModel.add(s);
        return this;
    }
    
    public boolean isMultiItem() {
        return this.bMultiItem;
    }
    
    public BodyLocation setMultiItem(final boolean bMultiItem) {
        this.bMultiItem = bMultiItem;
        return this;
    }
    
    public boolean isHideModel(final String o) {
        return this.hideModel.contains(o);
    }
    
    public boolean isExclusive(final String s) {
        return this.group.isExclusive(this.id, s);
    }
    
    public boolean isID(final String s) {
        return this.id.equals(s) || this.aliases.contains(s);
    }
    
    private void checkId(final String s, final String s2) {
        if (s == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        }
    }
    
    public String getId() {
        return this.id;
    }
}
