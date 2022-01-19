// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayList;
import java.util.ArrayDeque;

public abstract class GlobalObjectSystem
{
    private static final ArrayDeque<ArrayList<GlobalObject>> objectListPool;
    protected final String name;
    protected final KahluaTable modData;
    protected final ArrayList<GlobalObject> objects;
    protected final GlobalObjectLookup lookup;
    
    GlobalObjectSystem(final String name) {
        this.objects = new ArrayList<GlobalObject>();
        this.lookup = new GlobalObjectLookup(this);
        this.name = name;
        this.modData = LuaManager.platform.newTable();
    }
    
    public String getName() {
        return this.name;
    }
    
    public final KahluaTable getModData() {
        return this.modData;
    }
    
    protected abstract GlobalObject makeObject(final int p0, final int p1, final int p2);
    
    public final GlobalObject newObject(final int n, final int n2, final int n3) {
        if (this.getObjectAt(n, n2, n3) != null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, n, n2, n3));
        }
        final GlobalObject object = this.makeObject(n, n2, n3);
        this.objects.add(object);
        this.lookup.addObject(object);
        return object;
    }
    
    public final void removeObject(final GlobalObject o) throws IllegalArgumentException, IllegalStateException {
        if (o == null) {
            throw new NullPointerException("object is null");
        }
        if (o.system != this) {
            throw new IllegalStateException("object not in this system");
        }
        this.objects.remove(o);
        this.lookup.removeObject(o);
        o.Reset();
    }
    
    public final GlobalObject getObjectAt(final int n, final int n2, final int n3) {
        return this.lookup.getObjectAt(n, n2, n3);
    }
    
    public final boolean hasObjectsInChunk(final int n, final int n2) {
        return this.lookup.hasObjectsInChunk(n, n2);
    }
    
    public final ArrayList<GlobalObject> getObjectsInChunk(final int n, final int n2) {
        return this.lookup.getObjectsInChunk(n, n2, this.allocList());
    }
    
    public final ArrayList<GlobalObject> getObjectsAdjacentTo(final int n, final int n2, final int n3) {
        return this.lookup.getObjectsAdjacentTo(n, n2, n3, this.allocList());
    }
    
    public final int getObjectCount() {
        return this.objects.size();
    }
    
    public final GlobalObject getObjectByIndex(final int index) {
        if (index < 0 || index >= this.objects.size()) {
            return null;
        }
        return this.objects.get(index);
    }
    
    public final ArrayList<GlobalObject> allocList() {
        return GlobalObjectSystem.objectListPool.isEmpty() ? new ArrayList<GlobalObject>() : GlobalObjectSystem.objectListPool.pop();
    }
    
    public final void finishedWithList(final ArrayList<GlobalObject> list) {
        if (list != null && !GlobalObjectSystem.objectListPool.contains(list)) {
            list.clear();
            GlobalObjectSystem.objectListPool.add(list);
        }
    }
    
    public void Reset() {
        for (int i = 0; i < this.objects.size(); ++i) {
            this.objects.get(i).Reset();
        }
        this.objects.clear();
        this.modData.wipe();
    }
    
    static {
        objectListPool = new ArrayDeque<ArrayList<GlobalObject>>();
    }
}
