// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;

public final class SGlobalObject extends GlobalObject
{
    private static KahluaTable tempTable;
    
    SGlobalObject(final SGlobalObjectSystem sGlobalObjectSystem, final int n, final int n2, final int n3) {
        super(sGlobalObjectSystem, n, n2, n3);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        if (byteBuffer.get() != 0) {
            this.modData.load(byteBuffer, n);
        }
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putInt(this.x);
        byteBuffer.putInt(this.y);
        byteBuffer.put((byte)this.z);
        if (SGlobalObject.tempTable == null) {
            SGlobalObject.tempTable = LuaManager.platform.newTable();
        }
        SGlobalObject.tempTable.wipe();
        final KahluaTableIterator iterator = this.modData.iterator();
        while (iterator.advance()) {
            final Object key = iterator.getKey();
            if (((SGlobalObjectSystem)this.system).objectModDataKeys.contains(key)) {
                SGlobalObject.tempTable.rawset(key, this.modData.rawget(key));
            }
        }
        if (SGlobalObject.tempTable.isEmpty()) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            SGlobalObject.tempTable.save(byteBuffer);
            SGlobalObject.tempTable.wipe();
        }
    }
}
