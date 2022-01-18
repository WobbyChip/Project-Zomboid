// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.network.GameServer;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import java.util.Iterator;
import zombie.PersistentOutfits;
import zombie.core.skinnedmodel.population.OutfitRNG;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.OutfitManager;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoGridSquare;
import zombie.util.StringUtils;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.Type;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.HashMap;
import java.util.ArrayList;

public final class ZombiesZoneDefinition
{
    private static final ArrayList<ZZDZone> s_zoneList;
    private static final HashMap<String, ZZDZone> s_zoneMap;
    public static boolean bDirty;
    private static final PickDefinition pickDef;
    private static final HashMap<String, ZZDOutfit> s_customOutfitMap;
    
    private static void checkDirty() {
        if (ZombiesZoneDefinition.bDirty) {
            ZombiesZoneDefinition.bDirty = false;
            init();
        }
    }
    
    private static void init() {
        ZombiesZoneDefinition.s_zoneList.clear();
        ZombiesZoneDefinition.s_zoneMap.clear();
        final KahluaTableImpl kahluaTableImpl = Type.tryCastTo(LuaManager.env.rawget((Object)"ZombiesZoneDefinition"), KahluaTableImpl.class);
        if (kahluaTableImpl == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(iterator.getValue(), KahluaTableImpl.class);
            if (kahluaTableImpl2 == null) {
                continue;
            }
            final ZZDZone initZone = initZone(iterator.getKey().toString(), kahluaTableImpl2);
            if (initZone == null) {
                continue;
            }
            ZombiesZoneDefinition.s_zoneList.add(initZone);
            ZombiesZoneDefinition.s_zoneMap.put(initZone.name, initZone);
        }
    }
    
    private static ZZDZone initZone(final String name, final KahluaTableImpl kahluaTableImpl) {
        final ZZDZone zzdZone = new ZZDZone();
        zzdZone.name = name;
        zzdZone.femaleChance = kahluaTableImpl.rawgetInt((Object)"femaleChance");
        zzdZone.maleChance = kahluaTableImpl.rawgetInt((Object)"maleChance");
        zzdZone.chanceToSpawn = kahluaTableImpl.rawgetInt((Object)"chanceToSpawn");
        zzdZone.toSpawn = kahluaTableImpl.rawgetInt((Object)"toSpawn");
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(iterator.getValue(), KahluaTableImpl.class);
            if (kahluaTableImpl2 == null) {
                continue;
            }
            final ZZDOutfit initOutfit = initOutfit(kahluaTableImpl2);
            if (initOutfit == null) {
                continue;
            }
            initOutfit.customName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, zzdZone.name, initOutfit.name);
            zzdZone.outfits.add(initOutfit);
        }
        return zzdZone;
    }
    
    private static ZZDOutfit initOutfit(final KahluaTableImpl kahluaTableImpl) {
        final ZZDOutfit zzdOutfit = new ZZDOutfit();
        zzdOutfit.name = kahluaTableImpl.rawgetStr((Object)"name");
        zzdOutfit.chance = kahluaTableImpl.rawgetFloat((Object)"chance");
        zzdOutfit.gender = kahluaTableImpl.rawgetStr((Object)"gender");
        zzdOutfit.toSpawn = kahluaTableImpl.rawgetInt((Object)"toSpawn");
        zzdOutfit.mandatory = kahluaTableImpl.rawgetStr((Object)"mandatory");
        zzdOutfit.room = kahluaTableImpl.rawgetStr((Object)"room");
        zzdOutfit.femaleHairStyles = initStringChance(kahluaTableImpl.rawgetStr((Object)"femaleHairStyles"));
        zzdOutfit.maleHairStyles = initStringChance(kahluaTableImpl.rawgetStr((Object)"maleHairStyles"));
        zzdOutfit.beardStyles = initStringChance(kahluaTableImpl.rawgetStr((Object)"beardStyles"));
        return zzdOutfit;
    }
    
    private static ArrayList<StringChance> initStringChance(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        final ArrayList<StringChance> list = new ArrayList<StringChance>();
        final String[] split = s.split(";");
        for (int length = split.length, i = 0; i < length; ++i) {
            final String[] split2 = split[i].split(":");
            final StringChance e = new StringChance();
            e.str = split2[0];
            e.chance = Float.parseFloat(split2[1]);
            list.add(e);
        }
        return list;
    }
    
    public static void dressInRandomOutfit(final IsoZombie isoZombie) {
        if (isoZombie.isSkeleton()) {
            return;
        }
        final IsoGridSquare currentSquare = isoZombie.getCurrentSquare();
        if (currentSquare == null) {
            return;
        }
        final PickDefinition pickDefinition = pickDefinition(currentSquare.x, currentSquare.y, currentSquare.z, isoZombie.isFemale());
        if (pickDefinition == null) {
            isoZombie.dressInPersistentOutfit(getRandomDefaultOutfit(isoZombie.isFemale(), (currentSquare.getRoom() == null) ? null : currentSquare.getRoom().getName()).m_Name);
            UnderwearDefinition.addRandomUnderwear(isoZombie);
            return;
        }
        applyDefinition(isoZombie, pickDefinition.zone, pickDefinition.table, pickDefinition.bFemale);
        UnderwearDefinition.addRandomUnderwear(isoZombie);
    }
    
    public static IsoMetaGrid.Zone getDefinitionZoneAt(final int n, final int n2, final int n3) {
        final ArrayList<IsoMetaGrid.Zone> zones = IsoWorld.instance.MetaGrid.getZonesAt(n, n2, n3);
        for (int i = zones.size() - 1; i >= 0; --i) {
            final IsoMetaGrid.Zone zone = zones.get(i);
            if ("ZombiesType".equalsIgnoreCase(zone.type) || ZombiesZoneDefinition.s_zoneMap.containsKey(zone.type)) {
                return zone;
            }
        }
        return null;
    }
    
    public static PickDefinition pickDefinition(final int n, final int n2, final int n3, boolean bFemale) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            return null;
        }
        final String s = (gridSquare.getRoom() == null) ? null : gridSquare.getRoom().getName();
        checkDirty();
        IsoMetaGrid.Zone definitionZone = getDefinitionZoneAt(n, n2, n3);
        if (definitionZone == null) {
            return null;
        }
        if (definitionZone.spawnSpecialZombies == Boolean.FALSE) {
            return null;
        }
        final ZZDZone zzdZone = ZombiesZoneDefinition.s_zoneMap.get(StringUtils.isNullOrEmpty(definitionZone.name) ? definitionZone.type : definitionZone.name);
        if (zzdZone == null) {
            return null;
        }
        if (zzdZone.chanceToSpawn != -1) {
            final int chanceToSpawn = zzdZone.chanceToSpawn;
            final int toSpawn = zzdZone.toSpawn;
            ArrayList<Double> value = IsoWorld.instance.getSpawnedZombieZone().get(definitionZone.getName());
            if (value == null) {
                value = new ArrayList<Double>();
                IsoWorld.instance.getSpawnedZombieZone().put(definitionZone.getName(), value);
            }
            if (value.contains(definitionZone.id)) {
                definitionZone.spawnSpecialZombies = true;
            }
            if (toSpawn == -1 || (definitionZone.spawnSpecialZombies == null && value.size() < toSpawn)) {
                if (Rand.Next(100) < chanceToSpawn) {
                    definitionZone.spawnSpecialZombies = true;
                    value.add(definitionZone.id);
                }
                else {
                    definitionZone.spawnSpecialZombies = false;
                    definitionZone = null;
                }
            }
        }
        if (definitionZone == null) {
            return null;
        }
        final ArrayList<ZZDOutfit> list = new ArrayList<ZZDOutfit>();
        final ArrayList<ZZDOutfit> list2 = new ArrayList<ZZDOutfit>();
        final int maleChance = zzdZone.maleChance;
        final int femaleChance = zzdZone.femaleChance;
        if (maleChance > 0 && Rand.Next(100) < maleChance) {
            bFemale = false;
        }
        if (femaleChance > 0 && Rand.Next(100) < femaleChance) {
            bFemale = true;
        }
        for (int i = 0; i < zzdZone.outfits.size(); ++i) {
            final ZZDOutfit zzdOutfit = zzdZone.outfits.get(i);
            final String gender = zzdOutfit.gender;
            final String room = zzdOutfit.room;
            if (room != null) {
                if (s == null) {
                    continue;
                }
                if (!room.contains(s)) {
                    continue;
                }
            }
            if (!"male".equalsIgnoreCase(gender) || !bFemale) {
                if (!"female".equalsIgnoreCase(gender) || bFemale) {
                    final String name = zzdOutfit.name;
                    if (Boolean.parseBoolean(zzdOutfit.mandatory)) {
                        Integer value2 = definitionZone.spawnedZombies.get(name);
                        if (value2 == null) {
                            value2 = 0;
                        }
                        if (value2 < zzdOutfit.toSpawn) {
                            list.add(zzdOutfit);
                        }
                    }
                    else {
                        list2.add(zzdOutfit);
                    }
                }
            }
        }
        ZZDOutfit randomOutfitInSetList;
        if (!list.isEmpty()) {
            randomOutfitInSetList = PZArrayUtil.pickRandom(list);
        }
        else {
            randomOutfitInSetList = getRandomOutfitInSetList(list2, true);
        }
        if (randomOutfitInSetList == null) {
            return null;
        }
        ZombiesZoneDefinition.pickDef.table = randomOutfitInSetList;
        ZombiesZoneDefinition.pickDef.bFemale = bFemale;
        ZombiesZoneDefinition.pickDef.zone = definitionZone;
        return ZombiesZoneDefinition.pickDef;
    }
    
    public static void applyDefinition(final IsoZombie isoZombie, final IsoMetaGrid.Zone zone, final ZZDOutfit zzdOutfit, final boolean femaleEtc) {
        isoZombie.setFemaleEtc(femaleEtc);
        Outfit outfit;
        if (!femaleEtc) {
            outfit = OutfitManager.instance.FindMaleOutfit(zzdOutfit.name);
        }
        else {
            outfit = OutfitManager.instance.FindFemaleOutfit(zzdOutfit.name);
        }
        final String customName = zzdOutfit.customName;
        if (outfit == null) {
            outfit = OutfitManager.instance.GetRandomOutfit(femaleEtc);
            final String name = outfit.m_Name;
        }
        else if (zone != null) {
            Integer value = zone.spawnedZombies.get(outfit.m_Name);
            if (value == null) {
                value = 1;
            }
            zone.spawnedZombies.put(outfit.m_Name, value + 1);
        }
        if (outfit != null) {
            isoZombie.dressInPersistentOutfit(outfit.m_Name);
        }
        ModelManager.instance.ResetNextFrame(isoZombie);
        isoZombie.advancedAnimator.OnAnimDataChanged(false);
    }
    
    public static Outfit getRandomDefaultOutfit(final boolean b, final String s) {
        final ArrayList<ZZDOutfit> list = new ArrayList<ZZDOutfit>();
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"ZombiesZoneDefinition");
        final ZZDZone zzdZone = ZombiesZoneDefinition.s_zoneMap.get("Default");
        for (int i = 0; i < zzdZone.outfits.size(); ++i) {
            final ZZDOutfit e = zzdZone.outfits.get(i);
            final String gender = e.gender;
            final String room = e.room;
            if (room != null) {
                if (s == null) {
                    continue;
                }
                if (!room.contains(s)) {
                    continue;
                }
            }
            if (gender == null || ("male".equalsIgnoreCase(gender) && !b) || ("female".equalsIgnoreCase(gender) && b)) {
                list.add(e);
            }
        }
        final ZZDOutfit randomOutfitInSetList = getRandomOutfitInSetList(list, false);
        Outfit outfit = null;
        if (randomOutfitInSetList != null) {
            if (b) {
                outfit = OutfitManager.instance.FindFemaleOutfit(randomOutfitInSetList.name);
            }
            else {
                outfit = OutfitManager.instance.FindMaleOutfit(randomOutfitInSetList.name);
            }
        }
        if (outfit == null) {
            outfit = OutfitManager.instance.GetRandomOutfit(b);
        }
        return outfit;
    }
    
    public static ZZDOutfit getRandomOutfitInSetList(final ArrayList<ZZDOutfit> list, final boolean b) {
        float n = 0.0f;
        for (int i = 0; i < list.size(); ++i) {
            n += list.get(i).chance;
        }
        float n2 = Rand.Next(0.0f, 100.0f);
        if (!b || n > 100.0f) {
            n2 = Rand.Next(0.0f, n);
        }
        float n3 = 0.0f;
        for (int j = 0; j < list.size(); ++j) {
            final ZZDOutfit zzdOutfit = list.get(j);
            n3 += zzdOutfit.chance;
            if (n2 < n3) {
                return zzdOutfit;
            }
        }
        return null;
    }
    
    private static String getRandomHairOrBeard(final ArrayList<StringChance> list) {
        final float next = OutfitRNG.Next(0.0f, 100.0f);
        float n = 0.0f;
        int i = 0;
        while (i < list.size()) {
            final StringChance stringChance = list.get(i);
            n += stringChance.chance;
            if (next < n) {
                if ("null".equalsIgnoreCase(stringChance.str)) {
                    return "";
                }
                return stringChance.str;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    public static void registerCustomOutfits() {
        checkDirty();
        ZombiesZoneDefinition.s_customOutfitMap.clear();
        final Iterator<ZZDZone> iterator = ZombiesZoneDefinition.s_zoneList.iterator();
        while (iterator.hasNext()) {
            for (final ZZDOutfit value : iterator.next().outfits) {
                PersistentOutfits.instance.registerOutfitter(value.customName, true, ZombiesZoneDefinition::ApplyCustomOutfit);
                ZombiesZoneDefinition.s_customOutfitMap.put(value.customName, value);
            }
        }
    }
    
    private static void ApplyCustomOutfit(final int n, final String key, final IsoGameCharacter isoGameCharacter) {
        final ZZDOutfit zzdOutfit = ZombiesZoneDefinition.s_customOutfitMap.get(key);
        final boolean femaleEtc = (n & Integer.MIN_VALUE) != 0x0;
        final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (isoZombie != null) {
            isoZombie.setFemaleEtc(femaleEtc);
        }
        isoGameCharacter.dressInNamedOutfit(zzdOutfit.name);
        if (isoZombie == null) {
            PersistentOutfits.instance.removeFallenHat(n, isoGameCharacter);
            return;
        }
        AttachedWeaponDefinitions.instance.addRandomAttachedWeapon(isoZombie);
        isoZombie.addRandomBloodDirtHolesEtc();
        final boolean female = isoGameCharacter.isFemale();
        if (female && zzdOutfit.femaleHairStyles != null) {
            isoZombie.getHumanVisual().setHairModel(getRandomHairOrBeard(zzdOutfit.femaleHairStyles));
        }
        if (!female && zzdOutfit.maleHairStyles != null) {
            isoZombie.getHumanVisual().setHairModel(getRandomHairOrBeard(zzdOutfit.maleHairStyles));
        }
        if (!female && zzdOutfit.beardStyles != null) {
            isoZombie.getHumanVisual().setBeardModel(getRandomHairOrBeard(zzdOutfit.beardStyles));
        }
        PersistentOutfits.instance.removeFallenHat(n, isoGameCharacter);
    }
    
    public static int pickPersistentOutfit(final IsoGridSquare isoGridSquare) {
        if (!GameServer.bServer) {
            return 0;
        }
        boolean bFemale = Rand.Next(2) == 0;
        final PickDefinition pickDefinition = pickDefinition(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, bFemale);
        Outfit outfit;
        if (pickDefinition == null) {
            outfit = getRandomDefaultOutfit(bFemale, (isoGridSquare.getRoom() == null) ? null : isoGridSquare.getRoom().getName());
        }
        else {
            bFemale = pickDefinition.bFemale;
            final String name = pickDefinition.table.name;
            if (bFemale) {
                outfit = OutfitManager.instance.FindFemaleOutfit(name);
            }
            else {
                outfit = OutfitManager.instance.FindMaleOutfit(name);
            }
        }
        if (outfit != null) {
            final int pickOutfit = PersistentOutfits.instance.pickOutfit(outfit.m_Name, bFemale);
            if (pickOutfit != 0) {
                return pickOutfit;
            }
        }
        return 0;
    }
    
    static {
        s_zoneList = new ArrayList<ZZDZone>();
        s_zoneMap = new HashMap<String, ZZDZone>();
        ZombiesZoneDefinition.bDirty = true;
        pickDef = new PickDefinition();
        s_customOutfitMap = new HashMap<String, ZZDOutfit>();
    }
    
    public static final class PickDefinition
    {
        IsoMetaGrid.Zone zone;
        ZZDOutfit table;
        boolean bFemale;
    }
    
    private static final class StringChance
    {
        String str;
        float chance;
    }
    
    private static final class ZZDOutfit
    {
        String name;
        String customName;
        float chance;
        int toSpawn;
        String gender;
        String mandatory;
        String room;
        ArrayList<StringChance> femaleHairStyles;
        ArrayList<StringChance> maleHairStyles;
        ArrayList<StringChance> beardStyles;
    }
    
    private static final class ZZDZone
    {
        String name;
        int femaleChance;
        int maleChance;
        int chanceToSpawn;
        int toSpawn;
        final ArrayList<ZZDOutfit> outfits;
        
        private ZZDZone() {
            this.outfits = new ArrayList<ZZDOutfit>();
        }
    }
}
