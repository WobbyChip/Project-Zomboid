// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import zombie.iso.IsoObject;
import zombie.characters.IsoPlayer;
import java.io.IOException;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.network.TableNetworkUtils;
import zombie.core.BoxedStaticValues;
import zombie.Lua.LuaManager;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.SliceY;
import zombie.network.GameServer;
import zombie.Lua.LuaEventManager;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.core.Core;
import java.util.ArrayList;

public final class SGlobalObjects
{
    protected static final ArrayList<SGlobalObjectSystem> systems;
    
    public static void noise(final String s) {
        if (Core.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public static SGlobalObjectSystem registerSystem(final String s) {
        SGlobalObjectSystem sGlobalObjectSystem = getSystemByName(s);
        if (sGlobalObjectSystem == null) {
            sGlobalObjectSystem = newSystem(s);
            sGlobalObjectSystem.load();
        }
        return sGlobalObjectSystem;
    }
    
    public static SGlobalObjectSystem newSystem(final String s) throws IllegalStateException {
        if (getSystemByName(s) != null) {
            throw new IllegalStateException("system with that name already exists");
        }
        noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        final SGlobalObjectSystem e = new SGlobalObjectSystem(s);
        SGlobalObjects.systems.add(e);
        return e;
    }
    
    public static int getSystemCount() {
        return SGlobalObjects.systems.size();
    }
    
    public static SGlobalObjectSystem getSystemByIndex(final int index) {
        if (index < 0 || index >= SGlobalObjects.systems.size()) {
            return null;
        }
        return SGlobalObjects.systems.get(index);
    }
    
    public static SGlobalObjectSystem getSystemByName(final String anObject) {
        for (int i = 0; i < SGlobalObjects.systems.size(); ++i) {
            final SGlobalObjectSystem sGlobalObjectSystem = SGlobalObjects.systems.get(i);
            if (sGlobalObjectSystem.name.equals(anObject)) {
                return sGlobalObjectSystem;
            }
        }
        return null;
    }
    
    public static void update() {
        for (int i = 0; i < SGlobalObjects.systems.size(); ++i) {
            SGlobalObjects.systems.get(i).update();
        }
    }
    
    public static void chunkLoaded(final int n, final int n2) {
        for (int i = 0; i < SGlobalObjects.systems.size(); ++i) {
            SGlobalObjects.systems.get(i).chunkLoaded(n, n2);
        }
    }
    
    public static void initSystems() {
        if (GameClient.bClient) {
            return;
        }
        LuaEventManager.triggerEvent("OnSGlobalObjectSystemInit");
        if (GameServer.bServer) {
            return;
        }
        try {
            synchronized (SliceY.SliceBufferLock) {
                SliceY.SliceBuffer.clear();
                saveInitialStateForClient(SliceY.SliceBuffer);
                SliceY.SliceBuffer.flip();
                CGlobalObjects.loadInitialState(SliceY.SliceBuffer);
            }
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
    }
    
    public static void saveInitialStateForClient(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)SGlobalObjects.systems.size());
        for (int i = 0; i < SGlobalObjects.systems.size(); ++i) {
            final SGlobalObjectSystem sGlobalObjectSystem = SGlobalObjects.systems.get(i);
            GameWindow.WriteStringUTF(byteBuffer, sGlobalObjectSystem.name);
            KahluaTable kahluaTable = sGlobalObjectSystem.getInitialStateForClient();
            if (kahluaTable == null) {
                kahluaTable = LuaManager.platform.newTable();
            }
            final KahluaTable table = LuaManager.platform.newTable();
            kahluaTable.rawset((Object)"_objects", (Object)table);
            for (int j = 0; j < sGlobalObjectSystem.getObjectCount(); ++j) {
                final GlobalObject objectByIndex = sGlobalObjectSystem.getObjectByIndex(j);
                final KahluaTable table2 = LuaManager.platform.newTable();
                table2.rawset((Object)"x", (Object)BoxedStaticValues.toDouble(objectByIndex.getX()));
                table2.rawset((Object)"y", (Object)BoxedStaticValues.toDouble(objectByIndex.getY()));
                table2.rawset((Object)"z", (Object)BoxedStaticValues.toDouble(objectByIndex.getZ()));
                for (final String s : sGlobalObjectSystem.objectSyncKeys) {
                    table2.rawset((Object)s, objectByIndex.getModData().rawget((Object)s));
                }
                table.rawset(j + 1, (Object)table2);
            }
            if (kahluaTable == null || kahluaTable.isEmpty()) {
                byteBuffer.put((byte)0);
            }
            else {
                byteBuffer.put((byte)1);
                TableNetworkUtils.save(kahluaTable, byteBuffer);
            }
        }
    }
    
    public static boolean receiveClientCommand(final String s, final String s2, final IsoPlayer isoPlayer, final KahluaTable kahluaTable) {
        noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;S)Ljava/lang/String;, s, s2, isoPlayer.getOnlineID()));
        final SGlobalObjectSystem systemByName = getSystemByName(s);
        if (systemByName == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        systemByName.receiveClientCommand(s2, isoPlayer, kahluaTable);
        return true;
    }
    
    public static void load() {
    }
    
    public static void save() {
        for (int i = 0; i < SGlobalObjects.systems.size(); ++i) {
            SGlobalObjects.systems.get(i).save();
        }
    }
    
    public static void OnIsoObjectChangedItself(final String s, final IsoObject isoObject) {
        if (GameClient.bClient) {
            return;
        }
        final SGlobalObjectSystem systemByName = getSystemByName(s);
        if (systemByName == null) {
            return;
        }
        systemByName.OnIsoObjectChangedItself(isoObject);
    }
    
    public static void Reset() {
        for (int i = 0; i < SGlobalObjects.systems.size(); ++i) {
            SGlobalObjects.systems.get(i).Reset();
        }
        SGlobalObjects.systems.clear();
        GlobalObjectLookup.Reset();
    }
    
    static {
        systems = new ArrayList<SGlobalObjectSystem>();
    }
}
