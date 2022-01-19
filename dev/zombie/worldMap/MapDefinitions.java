// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.Type;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import java.util.ArrayList;

public final class MapDefinitions
{
    private static MapDefinitions instance;
    private final ArrayList<String> m_definitions;
    
    public MapDefinitions() {
        this.m_definitions = new ArrayList<String>();
    }
    
    public static MapDefinitions getInstance() {
        if (MapDefinitions.instance == null) {
            MapDefinitions.instance = new MapDefinitions();
        }
        return MapDefinitions.instance;
    }
    
    public String pickRandom() {
        if (this.m_definitions.isEmpty()) {
            this.initDefinitionsFromLua();
        }
        if (this.m_definitions.isEmpty()) {
            return "Default";
        }
        return PZArrayUtil.pickRandom(this.m_definitions);
    }
    
    private void initDefinitionsFromLua() {
        final KahluaTable kahluaTable = Type.tryCastTo(LuaManager.env.rawget((Object)"LootMaps"), KahluaTable.class);
        if (kahluaTable == null) {
            return;
        }
        final KahluaTable kahluaTable2 = Type.tryCastTo(kahluaTable.rawget((Object)"Init"), KahluaTable.class);
        if (kahluaTable2 == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTable2.iterator();
        while (iterator.advance()) {
            final String e = Type.tryCastTo(iterator.getKey(), String.class);
            if (e == null) {
                continue;
            }
            this.m_definitions.add(e);
        }
    }
    
    public static void Reset() {
        if (MapDefinitions.instance == null) {
            return;
        }
        MapDefinitions.instance.m_definitions.clear();
        MapDefinitions.instance = null;
    }
}
