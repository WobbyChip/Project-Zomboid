// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.Type;
import zombie.network.ServerOptions;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;

public final class SpawnPoints
{
    public static final SpawnPoints instance;
    private KahluaTable SpawnRegions;
    private final ArrayList<IsoGameCharacter.Location> SpawnPoints;
    private final ArrayList<BuildingDef> SpawnBuildings;
    private final IsoGameCharacter.Location m_tempLocation;
    
    public SpawnPoints() {
        this.SpawnPoints = new ArrayList<IsoGameCharacter.Location>();
        this.SpawnBuildings = new ArrayList<BuildingDef>();
        this.m_tempLocation = new IsoGameCharacter.Location(-1, -1, -1);
    }
    
    public void init() {
        this.SpawnRegions = LuaManager.platform.newTable();
        this.SpawnPoints.clear();
        this.SpawnBuildings.clear();
    }
    
    public void initServer1() {
        this.init();
        this.initSpawnRegions();
    }
    
    public void initServer2() {
        if (this.parseServerSpawnPoint()) {
            return;
        }
        this.parseSpawnRegions();
        this.initSpawnBuildings();
    }
    
    public void initSinglePlayer() {
        this.init();
        this.initSpawnRegions();
        this.parseSpawnRegions();
        this.initSpawnBuildings();
    }
    
    private void initSpawnRegions() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"SpawnRegionMgr");
        if (kahluaTable == null) {
            DebugLog.General.error((Object)"SpawnRegionMgr is undefined");
            return;
        }
        final Object[] pcall = LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget((Object)"getSpawnRegions"), new Object[0]);
        if (pcall.length > 1 && pcall[1] instanceof KahluaTable) {
            this.SpawnRegions = (KahluaTable)pcall[1];
        }
    }
    
    private boolean parseServerSpawnPoint() {
        if (!GameServer.bServer) {
            return false;
        }
        if (ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
            return false;
        }
        final String[] split = ServerOptions.instance.SpawnPoint.getValue().split(",");
        if (split.length == 3) {
            try {
                final int int1 = Integer.parseInt(split[0].trim());
                final int int2 = Integer.parseInt(split[1].trim());
                final int int3 = Integer.parseInt(split[2].trim());
                if (int1 != 0 || int2 != 0) {
                    this.SpawnPoints.add(new IsoGameCharacter.Location(int1, int2, int3));
                    return true;
                }
            }
            catch (NumberFormatException ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.SpawnPoint.getValue()));
            }
        }
        else {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.SpawnPoint.getValue()));
        }
        return false;
    }
    
    private void parseSpawnRegions() {
        final KahluaTableIterator iterator = this.SpawnRegions.iterator();
        while (iterator.advance()) {
            final KahluaTable kahluaTable = Type.tryCastTo(iterator.getValue(), KahluaTable.class);
            if (kahluaTable != null) {
                this.parseRegion(kahluaTable);
            }
        }
    }
    
    private void parseRegion(final KahluaTable kahluaTable) {
        final KahluaTable kahluaTable2 = Type.tryCastTo(kahluaTable.rawget((Object)"points"), KahluaTable.class);
        if (kahluaTable2 != null) {
            final KahluaTableIterator iterator = kahluaTable2.iterator();
            while (iterator.advance()) {
                final KahluaTable kahluaTable3 = Type.tryCastTo(iterator.getValue(), KahluaTable.class);
                if (kahluaTable3 != null) {
                    this.parseProfession(kahluaTable3);
                }
            }
        }
    }
    
    private void parseProfession(final KahluaTable kahluaTable) {
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final KahluaTable kahluaTable2 = Type.tryCastTo(iterator.getValue(), KahluaTable.class);
            if (kahluaTable2 != null) {
                this.parsePoint(kahluaTable2);
            }
        }
    }
    
    private void parsePoint(final KahluaTable kahluaTable) {
        final Double n = Type.tryCastTo(kahluaTable.rawget((Object)"worldX"), Double.class);
        final Double n2 = Type.tryCastTo(kahluaTable.rawget((Object)"worldY"), Double.class);
        final Double n3 = Type.tryCastTo(kahluaTable.rawget((Object)"posX"), Double.class);
        final Double n4 = Type.tryCastTo(kahluaTable.rawget((Object)"posY"), Double.class);
        final Double n5 = Type.tryCastTo(kahluaTable.rawget((Object)"posZ"), Double.class);
        if (n == null || n2 == null || n3 == null || n4 == null) {
            return;
        }
        this.m_tempLocation.x = n.intValue() * 300 + n3.intValue();
        this.m_tempLocation.y = n2.intValue() * 300 + n4.intValue();
        this.m_tempLocation.z = ((n5 == null) ? 0 : n5.intValue());
        if (!this.SpawnPoints.contains(this.m_tempLocation)) {
            this.SpawnPoints.add(new IsoGameCharacter.Location(this.m_tempLocation.x, this.m_tempLocation.y, this.m_tempLocation.z));
        }
    }
    
    private void initSpawnBuildings() {
        for (int i = 0; i < this.SpawnPoints.size(); ++i) {
            final IsoGameCharacter.Location location = this.SpawnPoints.get(i);
            final RoomDef room = IsoWorld.instance.MetaGrid.getRoomAt(location.x, location.y, location.z);
            if (room == null || room.getBuilding() == null) {
                DebugLog.General.warn("initSpawnBuildings: no room or building at %d,%d,%d", location.x, location.y, location.z);
            }
            else {
                this.SpawnBuildings.add(room.getBuilding());
            }
        }
    }
    
    public boolean isSpawnBuilding(final BuildingDef o) {
        return this.SpawnBuildings.contains(o);
    }
    
    public KahluaTable getSpawnRegions() {
        return this.SpawnRegions;
    }
    
    static {
        instance = new SpawnPoints();
    }
}
