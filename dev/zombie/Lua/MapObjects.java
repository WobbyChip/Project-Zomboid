// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import gnu.trove.list.array.TShortArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.Prototype;
import zombie.iso.IsoChunk;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.iso.IsoWorld;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoGridSquare;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.iso.IsoObject;
import java.util.ArrayList;
import java.util.HashMap;

public final class MapObjects
{
    private static final HashMap<String, Callback> onNew;
    private static final HashMap<String, Callback> onLoad;
    private static final ArrayList<IsoObject> tempObjects;
    private static final Object[] params;
    
    private static Callback getOnNew(final String s) {
        Callback value = MapObjects.onNew.get(s);
        if (value == null) {
            value = new Callback(s);
            MapObjects.onNew.put(s, value);
        }
        return value;
    }
    
    public static void OnNewWithSprite(final String s, final LuaClosure e, final int n) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("invalid sprite name");
        }
        if (e == null) {
            throw new NullPointerException("function is null");
        }
        final Callback onNew = getOnNew(s);
        for (int i = 0; i < onNew.functions.size(); ++i) {
            if (onNew.priority.get(i) < n) {
                onNew.functions.add(i, e);
                onNew.priority.insert(i, (short)n);
                return;
            }
            if (onNew.priority.get(i) == n) {
                onNew.functions.set(i, e);
                onNew.priority.set(i, (short)n);
                return;
            }
        }
        onNew.functions.add(e);
        onNew.priority.add((short)n);
    }
    
    public static void OnNewWithSprite(final KahluaTable kahluaTable, final LuaClosure luaClosure, final int n) {
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            throw new IllegalArgumentException("invalid sprite-name table");
        }
        if (luaClosure == null) {
            throw new NullPointerException("function is null");
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object value = iterator.getValue();
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, value));
            }
            OnNewWithSprite((String)value, luaClosure, n);
        }
    }
    
    public static void newGridSquare(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare.getObjects().isEmpty()) {
            return;
        }
        MapObjects.tempObjects.clear();
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            MapObjects.tempObjects.add(isoGridSquare.getObjects().get(i));
        }
        for (int j = 0; j < MapObjects.tempObjects.size(); ++j) {
            final IsoObject isoObject = MapObjects.tempObjects.get(j);
            if (isoGridSquare.getObjects().contains(isoObject)) {
                if (!(isoObject instanceof IsoWorldInventoryObject)) {
                    if (isoObject != null) {
                        if (isoObject.sprite != null) {
                            final String key = (isoObject.sprite.name == null) ? isoObject.spriteName : isoObject.sprite.name;
                            if (key != null) {
                                if (!key.isEmpty()) {
                                    final Callback callback = MapObjects.onNew.get(key);
                                    if (callback != null) {
                                        MapObjects.params[0] = isoObject;
                                        for (int k = 0; k < callback.functions.size(); ++k) {
                                            try {
                                                LuaManager.caller.protectedCallVoid(LuaManager.thread, (Object)callback.functions.get(k), MapObjects.params);
                                            }
                                            catch (Throwable t) {
                                                ExceptionLogger.logException(t);
                                            }
                                            final String anObject = (isoObject.sprite == null || isoObject.sprite.name == null) ? isoObject.spriteName : isoObject.sprite.name;
                                            if (!isoGridSquare.getObjects().contains(isoObject) || isoObject.sprite == null) {
                                                break;
                                            }
                                            if (!callback.spriteName.equals(anObject)) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static Callback getOnLoad(final String s) {
        Callback value = MapObjects.onLoad.get(s);
        if (value == null) {
            value = new Callback(s);
            MapObjects.onLoad.put(s, value);
        }
        return value;
    }
    
    public static void OnLoadWithSprite(final String s, final LuaClosure e, final int n) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("invalid sprite name");
        }
        if (e == null) {
            throw new NullPointerException("function is null");
        }
        final Callback onLoad = getOnLoad(s);
        for (int i = 0; i < onLoad.functions.size(); ++i) {
            if (onLoad.priority.get(i) < n) {
                onLoad.functions.add(i, e);
                onLoad.priority.insert(i, (short)n);
                return;
            }
            if (onLoad.priority.get(i) == n) {
                onLoad.functions.set(i, e);
                onLoad.priority.set(i, (short)n);
                return;
            }
        }
        onLoad.functions.add(e);
        onLoad.priority.add((short)n);
    }
    
    public static void OnLoadWithSprite(final KahluaTable kahluaTable, final LuaClosure luaClosure, final int n) {
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            throw new IllegalArgumentException("invalid sprite-name table");
        }
        if (luaClosure == null) {
            throw new NullPointerException("function is null");
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object value = iterator.getValue();
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, value));
            }
            OnLoadWithSprite((String)value, luaClosure, n);
        }
    }
    
    public static void loadGridSquare(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare.getObjects().isEmpty()) {
            return;
        }
        MapObjects.tempObjects.clear();
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            MapObjects.tempObjects.add(isoGridSquare.getObjects().get(i));
        }
        for (int j = 0; j < MapObjects.tempObjects.size(); ++j) {
            final IsoObject isoObject = MapObjects.tempObjects.get(j);
            if (isoGridSquare.getObjects().contains(isoObject)) {
                if (!(isoObject instanceof IsoWorldInventoryObject)) {
                    if (isoObject != null) {
                        if (isoObject.sprite != null) {
                            final String key = (isoObject.sprite.name == null) ? isoObject.spriteName : isoObject.sprite.name;
                            if (key != null) {
                                if (!key.isEmpty()) {
                                    final Callback callback = MapObjects.onLoad.get(key);
                                    if (callback != null) {
                                        MapObjects.params[0] = isoObject;
                                        for (int k = 0; k < callback.functions.size(); ++k) {
                                            try {
                                                LuaManager.caller.protectedCallVoid(LuaManager.thread, (Object)callback.functions.get(k), MapObjects.params);
                                            }
                                            catch (Throwable t) {
                                                ExceptionLogger.logException(t);
                                            }
                                            final String anObject = (isoObject.sprite == null || isoObject.sprite.name == null) ? isoObject.spriteName : isoObject.sprite.name;
                                            if (!isoGridSquare.getObjects().contains(isoObject) || isoObject.sprite == null) {
                                                break;
                                            }
                                            if (!callback.spriteName.equals(anObject)) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void debugNewSquare(final int n, final int n2, final int n3) {
        if (!Core.bDebug) {
            return;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            return;
        }
        newGridSquare(gridSquare);
    }
    
    public static void debugLoadSquare(final int n, final int n2, final int n3) {
        if (!Core.bDebug) {
            return;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            return;
        }
        loadGridSquare(gridSquare);
    }
    
    public static void debugLoadChunk(final int n, final int n2) {
        if (!Core.bDebug) {
            return;
        }
        final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(n, n2) : IsoWorld.instance.CurrentCell.getChunk(n, n2);
        if (isoChunk == null) {
            return;
        }
        for (int i = 0; i <= isoChunk.maxLevel; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    final IsoGridSquare gridSquare = isoChunk.getGridSquare(j, k, i);
                    if (gridSquare != null) {
                        if (!gridSquare.getObjects().isEmpty()) {
                            loadGridSquare(gridSquare);
                        }
                    }
                }
            }
        }
    }
    
    public static void reroute(final Prototype prototype, final LuaClosure element) {
        for (final Callback callback : MapObjects.onNew.values()) {
            for (int i = 0; i < callback.functions.size(); ++i) {
                final LuaClosure luaClosure = callback.functions.get(i);
                if (luaClosure.prototype.filename.equals(prototype.filename) && luaClosure.prototype.name.equals(prototype.name)) {
                    callback.functions.set(i, element);
                }
            }
        }
    }
    
    public static void Reset() {
        MapObjects.onNew.clear();
        MapObjects.onLoad.clear();
    }
    
    static {
        onNew = new HashMap<String, Callback>();
        onLoad = new HashMap<String, Callback>();
        tempObjects = new ArrayList<IsoObject>();
        params = new Object[1];
    }
    
    private static final class Callback
    {
        final String spriteName;
        final ArrayList<LuaClosure> functions;
        final TShortArrayList priority;
        
        Callback(final String spriteName) {
            this.functions = new ArrayList<LuaClosure>();
            this.priority = new TShortArrayList();
            this.spriteName = spriteName;
        }
    }
}
