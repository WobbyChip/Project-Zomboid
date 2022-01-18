// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.stash;

public final class StashContainer
{
    public String room;
    public String containerSprite;
    public String containerType;
    public int contX;
    public int contY;
    public int contZ;
    public String containerItem;
    
    public StashContainer(final String room, final String containerSprite, final String containerType) {
        this.contX = -1;
        this.contY = -1;
        this.contZ = -1;
        if (room == null) {
            this.room = "all";
        }
        else {
            this.room = room;
        }
        this.containerSprite = containerSprite;
        this.containerType = containerType;
    }
}
