// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

public final class MetaObject
{
    int type;
    int x;
    int y;
    RoomDef def;
    boolean bUsed;
    
    public MetaObject(final int type, final int x, final int y, final RoomDef def) {
        this.bUsed = false;
        this.type = type;
        this.x = x;
        this.y = y;
        this.def = def;
    }
    
    public RoomDef getRoom() {
        return this.def;
    }
    
    public boolean getUsed() {
        return this.bUsed;
    }
    
    public void setUsed(final boolean bUsed) {
        this.bUsed = bUsed;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getType() {
        return this.type;
    }
}
