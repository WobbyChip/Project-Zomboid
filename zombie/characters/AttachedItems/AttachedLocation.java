// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

public final class AttachedLocation
{
    protected final AttachedLocationGroup group;
    protected final String id;
    protected String attachmentName;
    
    public AttachedLocation(final AttachedLocationGroup group, final String id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        }
        this.group = group;
        this.id = id;
    }
    
    public void setAttachmentName(final String attachmentName) {
        if (this.id == null) {
            throw new NullPointerException("attachmentName is null");
        }
        if (this.id.isEmpty()) {
            throw new IllegalArgumentException("attachmentName is empty");
        }
        this.attachmentName = attachmentName;
    }
    
    public String getAttachmentName() {
        return this.attachmentName;
    }
    
    public String getId() {
        return this.id;
    }
}
