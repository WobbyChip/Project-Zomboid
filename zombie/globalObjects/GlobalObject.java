// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;

public abstract class GlobalObject
{
    protected GlobalObjectSystem system;
    protected int x;
    protected int y;
    protected int z;
    protected final KahluaTable modData;
    
    GlobalObject(final GlobalObjectSystem system, final int x, final int y, final int z) {
        this.system = system;
        this.x = x;
        this.y = y;
        this.z = z;
        this.modData = LuaManager.platform.newTable();
    }
    
    public GlobalObjectSystem getSystem() {
        return this.system;
    }
    
    public void setLocation(final int n, final int n2, final int n3) {
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public KahluaTable getModData() {
        return this.modData;
    }
    
    public void Reset() {
        this.system = null;
        this.modData.wipe();
    }
}
