// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.BoxedStaticValues;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.IsoPlayer;

public final class CGlobalObjectSystem extends GlobalObjectSystem
{
    public CGlobalObjectSystem(final String s) {
        super(s);
    }
    
    @Override
    protected GlobalObject makeObject(final int n, final int n2, final int n3) {
        return new CGlobalObject(this, n, n2, n3);
    }
    
    public void sendCommand(final String s, final IsoPlayer isoPlayer, final KahluaTable kahluaTable) {
        CGlobalObjectNetwork.sendClientCommand(isoPlayer, this.name, s, kahluaTable);
    }
    
    public void receiveServerCommand(final String s, final KahluaTable kahluaTable) {
        final Object rawget = this.modData.rawget((Object)"OnServerCommand");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcallvoid(LuaManager.thread, rawget, (Object)this.modData, (Object)s, (Object)kahluaTable);
    }
    
    public void receiveNewLuaObjectAt(final int n, final int n2, final int n3, final KahluaTable kahluaTable) {
        final Object rawget = this.modData.rawget((Object)"newLuaObjectAt");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { this.modData, BoxedStaticValues.toDouble(n), BoxedStaticValues.toDouble(n2), BoxedStaticValues.toDouble(n3) });
        final GlobalObject object = this.getObjectAt(n, n2, n3);
        if (object == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            object.getModData().rawset(iterator.getKey(), iterator.getValue());
        }
    }
    
    public void receiveRemoveLuaObjectAt(final int n, final int n2, final int n3) {
        final Object rawget = this.modData.rawget((Object)"removeLuaObjectAt");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { this.modData, BoxedStaticValues.toDouble(n), BoxedStaticValues.toDouble(n2), BoxedStaticValues.toDouble(n3) });
    }
    
    public void receiveUpdateLuaObjectAt(final int n, final int n2, final int n3, final KahluaTable kahluaTable) {
        final GlobalObject object = this.getObjectAt(n, n2, n3);
        if (object == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            object.getModData().rawset(iterator.getKey(), iterator.getValue());
        }
        final Object rawget = this.modData.rawget((Object)"OnLuaObjectUpdated");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { this.modData, object.getModData() });
    }
    
    @Override
    public void Reset() {
        super.Reset();
        this.modData.wipe();
    }
}
