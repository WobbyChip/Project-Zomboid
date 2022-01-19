// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.stash;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.Translator;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.ArrayList;

public final class Stash
{
    public String name;
    public String type;
    public String item;
    public String customName;
    public int buildingX;
    public int buildingY;
    public String spawnTable;
    public ArrayList<StashAnnotation> annotations;
    public boolean spawnOnlyOnZed;
    public int minDayToSpawn;
    public int maxDayToSpawn;
    public int minTrapToSpawn;
    public int maxTrapToSpawn;
    public int zombies;
    public ArrayList<StashContainer> containers;
    public int barricades;
    
    public Stash(final String name) {
        this.minDayToSpawn = -1;
        this.maxDayToSpawn = -1;
        this.minTrapToSpawn = -1;
        this.maxTrapToSpawn = -1;
        this.name = name;
    }
    
    public void load(final KahluaTableImpl kahluaTableImpl) {
        this.type = kahluaTableImpl.rawgetStr((Object)"type");
        this.item = kahluaTableImpl.rawgetStr((Object)"item");
        final StashBuilding e = new StashBuilding(this.name, kahluaTableImpl.rawgetInt((Object)"buildingX"), kahluaTableImpl.rawgetInt((Object)"buildingY"));
        StashSystem.possibleStashes.add(e);
        this.buildingX = e.buildingX;
        this.buildingY = e.buildingY;
        this.spawnTable = kahluaTableImpl.rawgetStr((Object)"spawnTable");
        this.customName = Translator.getText(kahluaTableImpl.rawgetStr((Object)"customName"));
        this.zombies = kahluaTableImpl.rawgetInt((Object)"zombies");
        this.barricades = kahluaTableImpl.rawgetInt((Object)"barricades");
        this.spawnOnlyOnZed = kahluaTableImpl.rawgetBool((Object)"spawnOnlyOnZed");
        final String rawgetStr = kahluaTableImpl.rawgetStr((Object)"daysToSpawn");
        if (rawgetStr != null) {
            final String[] split = rawgetStr.split("-");
            if (split.length == 2) {
                this.minDayToSpawn = Integer.parseInt(split[0]);
                this.maxDayToSpawn = Integer.parseInt(split[1]);
            }
            else {
                this.minDayToSpawn = Integer.parseInt(split[0]);
            }
        }
        final String rawgetStr2 = kahluaTableImpl.rawgetStr((Object)"traps");
        if (rawgetStr2 != null) {
            final String[] split2 = rawgetStr2.split("-");
            if (split2.length == 2) {
                this.minTrapToSpawn = Integer.parseInt(split2[0]);
                this.maxTrapToSpawn = Integer.parseInt(split2[1]);
            }
            else {
                this.minTrapToSpawn = Integer.parseInt(split2[0]);
                this.maxTrapToSpawn = this.minTrapToSpawn;
            }
        }
        final KahluaTable kahluaTable = (KahluaTable)kahluaTableImpl.rawget((Object)"containers");
        if (kahluaTable != null) {
            this.containers = new ArrayList<StashContainer>();
            final KahluaTableIterator iterator = kahluaTable.iterator();
            while (iterator.advance()) {
                final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator.getValue();
                final StashContainer e2 = new StashContainer(kahluaTableImpl2.rawgetStr((Object)"room"), kahluaTableImpl2.rawgetStr((Object)"containerSprite"), kahluaTableImpl2.rawgetStr((Object)"containerType"));
                e2.contX = kahluaTableImpl2.rawgetInt((Object)"contX");
                e2.contY = kahluaTableImpl2.rawgetInt((Object)"contY");
                e2.contZ = kahluaTableImpl2.rawgetInt((Object)"contZ");
                e2.containerItem = kahluaTableImpl2.rawgetStr((Object)"containerItem");
                if (e2.containerItem != null && ScriptManager.instance.getItem(e2.containerItem) == null) {
                    DebugLog.General.error("Stash containerItem \"%s\" doesn't exist.", e2.containerItem);
                }
                this.containers.add(e2);
            }
        }
        if ("Map".equals(this.type)) {
            final KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl.rawget((Object)"annotations");
            if (kahluaTableImpl3 != null) {
                this.annotations = new ArrayList<StashAnnotation>();
                final KahluaTableIterator iterator2 = ((KahluaTable)kahluaTableImpl3).iterator();
                while (iterator2.advance()) {
                    final KahluaTable kahluaTable2 = (KahluaTable)iterator2.getValue();
                    final StashAnnotation e3 = new StashAnnotation();
                    e3.fromLua(kahluaTable2);
                    this.annotations.add(e3);
                }
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getItem() {
        return this.item;
    }
    
    public int getBuildingX() {
        return this.buildingX;
    }
    
    public int getBuildingY() {
        return this.buildingY;
    }
}
