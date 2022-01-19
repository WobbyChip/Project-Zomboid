// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import java.io.IOException;
import zombie.network.TableNetworkUtils;
import zombie.Lua.LuaManager;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.Type;
import zombie.debug.DebugLog;
import zombie.core.Core;
import se.krka.kahlua.vm.KahluaTable;
import java.util.HashMap;
import java.util.ArrayList;

public final class CGlobalObjects
{
    protected static final ArrayList<CGlobalObjectSystem> systems;
    protected static final HashMap<String, KahluaTable> initialState;
    
    public static void noise(final String s) {
        if (Core.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public static CGlobalObjectSystem registerSystem(final String key) {
        CGlobalObjectSystem cGlobalObjectSystem = getSystemByName(key);
        if (cGlobalObjectSystem == null) {
            cGlobalObjectSystem = newSystem(key);
            final KahluaTable kahluaTable = CGlobalObjects.initialState.get(key);
            if (kahluaTable != null) {
                final KahluaTableIterator iterator = kahluaTable.iterator();
                while (iterator.advance()) {
                    final Object key2 = iterator.getKey();
                    final Object value = iterator.getValue();
                    if ("_objects".equals(key2)) {
                        final KahluaTable kahluaTable2 = Type.tryCastTo(value, KahluaTable.class);
                        for (int i = 1; i <= kahluaTable2.len(); ++i) {
                            final KahluaTable kahluaTable3 = Type.tryCastTo(kahluaTable2.rawget(i), KahluaTable.class);
                            final int intValue = ((Double)kahluaTable3.rawget((Object)"x")).intValue();
                            final int intValue2 = ((Double)kahluaTable3.rawget((Object)"y")).intValue();
                            final int intValue3 = ((Double)kahluaTable3.rawget((Object)"z")).intValue();
                            kahluaTable3.rawset((Object)"x", (Object)null);
                            kahluaTable3.rawset((Object)"y", (Object)null);
                            kahluaTable3.rawset((Object)"z", (Object)null);
                            final CGlobalObject cGlobalObject = Type.tryCastTo(cGlobalObjectSystem.newObject(intValue, intValue2, intValue3), CGlobalObject.class);
                            final KahluaTableIterator iterator2 = kahluaTable3.iterator();
                            while (iterator2.advance()) {
                                cGlobalObject.getModData().rawset(iterator2.getKey(), iterator2.getValue());
                            }
                        }
                        kahluaTable2.wipe();
                    }
                    else {
                        cGlobalObjectSystem.modData.rawset(key2, value);
                    }
                }
            }
        }
        return cGlobalObjectSystem;
    }
    
    public static CGlobalObjectSystem newSystem(final String s) throws IllegalStateException {
        if (getSystemByName(s) != null) {
            throw new IllegalStateException("system with that name already exists");
        }
        noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        final CGlobalObjectSystem e = new CGlobalObjectSystem(s);
        CGlobalObjects.systems.add(e);
        return e;
    }
    
    public static int getSystemCount() {
        return CGlobalObjects.systems.size();
    }
    
    public static CGlobalObjectSystem getSystemByIndex(final int index) {
        if (index < 0 || index >= CGlobalObjects.systems.size()) {
            return null;
        }
        return CGlobalObjects.systems.get(index);
    }
    
    public static CGlobalObjectSystem getSystemByName(final String anObject) {
        for (int i = 0; i < CGlobalObjects.systems.size(); ++i) {
            final CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.systems.get(i);
            if (cGlobalObjectSystem.name.equals(anObject)) {
                return cGlobalObjectSystem;
            }
        }
        return null;
    }
    
    public static void initSystems() {
        LuaEventManager.triggerEvent("OnCGlobalObjectSystemInit");
    }
    
    public static void loadInitialState(final ByteBuffer byteBuffer) throws IOException {
        for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
            final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
            if (byteBuffer.get() != 0) {
                final KahluaTable table = LuaManager.platform.newTable();
                CGlobalObjects.initialState.put(readStringUTF, table);
                TableNetworkUtils.load(table, byteBuffer);
            }
        }
    }
    
    public static boolean receiveServerCommand(final String s, final String s2, final KahluaTable kahluaTable) {
        final CGlobalObjectSystem systemByName = getSystemByName(s);
        if (systemByName == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        systemByName.receiveServerCommand(s2, kahluaTable);
        return true;
    }
    
    public static void Reset() {
        for (int i = 0; i < CGlobalObjects.systems.size(); ++i) {
            CGlobalObjects.systems.get(i).Reset();
        }
        CGlobalObjects.systems.clear();
        CGlobalObjects.initialState.clear();
        CGlobalObjectNetwork.Reset();
    }
    
    static {
        systems = new ArrayList<CGlobalObjectSystem>();
        initialState = new HashMap<String, KahluaTable>();
    }
}
