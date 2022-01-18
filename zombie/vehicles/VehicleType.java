// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.core.Rand;
import java.util.Iterator;
import zombie.scripting.objects.VehicleScript;
import java.util.HashSet;
import zombie.scripting.ScriptManager;
import java.util.Map;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

public final class VehicleType
{
    public final ArrayList<VehicleTypeDefinition> vehiclesDefinition;
    public int chanceToSpawnNormal;
    public int chanceToSpawnBurnt;
    public int spawnRate;
    public int chanceOfOverCar;
    public boolean randomAngle;
    public float baseVehicleQuality;
    public String name;
    private int chanceToSpawnKey;
    public int chanceToPartDamage;
    public boolean isSpecialCar;
    public boolean isBurntCar;
    public int chanceToSpawnSpecial;
    public static final HashMap<String, VehicleType> vehicles;
    public static final ArrayList<VehicleType> specialVehicles;
    
    public VehicleType(final String name) {
        this.vehiclesDefinition = new ArrayList<VehicleTypeDefinition>();
        this.chanceToSpawnNormal = 80;
        this.chanceToSpawnBurnt = 0;
        this.spawnRate = 16;
        this.chanceOfOverCar = 0;
        this.randomAngle = false;
        this.baseVehicleQuality = 1.0f;
        this.name = "";
        this.chanceToSpawnKey = 70;
        this.chanceToPartDamage = 0;
        this.isSpecialCar = false;
        this.isBurntCar = false;
        this.chanceToSpawnSpecial = 5;
        this.name = name;
    }
    
    public static void init() {
        initNormal();
        validate(VehicleType.vehicles.values());
        validate(VehicleType.specialVehicles);
    }
    
    private static void validate(final Collection<VehicleType> collection) {
    }
    
    private static void initNormal() {
        final boolean enabled = DebugLog.isEnabled(DebugType.Lua);
        for (final Map.Entry<Object, V> entry : ((KahluaTableImpl)LuaManager.env.rawget((Object)"VehicleZoneDistribution")).delegate.entrySet()) {
            final String string = entry.getKey().toString();
            final VehicleType vehicleType = new VehicleType(string);
            final ArrayList<VehicleTypeDefinition> vehiclesDefinition = vehicleType.vehiclesDefinition;
            final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)entry.getValue();
            for (final Map.Entry<Object, V> entry2 : ((KahluaTableImpl)kahluaTableImpl.rawget((Object)"vehicles")).delegate.entrySet()) {
                final String string2 = entry2.getKey().toString();
                if (ScriptManager.instance.getVehicle(string2) == null) {
                    DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string2));
                }
                final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry2.getValue();
                vehiclesDefinition.add(new VehicleTypeDefinition(string2, kahluaTableImpl2.rawgetInt((Object)"index"), (float)kahluaTableImpl2.rawgetInt((Object)"spawnChance")));
            }
            float n = 0.0f;
            for (int i = 0; i < vehiclesDefinition.size(); ++i) {
                n += vehiclesDefinition.get(i).spawnChance;
            }
            final float n2 = 100.0f / n;
            if (enabled) {
                DebugLog.Lua.println("Vehicle spawn rate:");
            }
            for (int j = 0; j < vehiclesDefinition.size(); ++j) {
                final VehicleTypeDefinition vehicleTypeDefinition = vehiclesDefinition.get(j);
                vehicleTypeDefinition.spawnChance *= n2;
                if (enabled) {
                    DebugLog.Lua.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;F)Ljava/lang/String;, string, vehiclesDefinition.get(j).vehicleType, vehiclesDefinition.get(j).spawnChance));
                }
            }
            if (kahluaTableImpl.delegate.containsKey("chanceToPartDamage")) {
                vehicleType.chanceToPartDamage = kahluaTableImpl.rawgetInt((Object)"chanceToPartDamage");
            }
            if (kahluaTableImpl.delegate.containsKey("chanceToSpawnNormal")) {
                vehicleType.chanceToSpawnNormal = kahluaTableImpl.rawgetInt((Object)"chanceToSpawnNormal");
            }
            if (kahluaTableImpl.delegate.containsKey("chanceToSpawnSpecial")) {
                vehicleType.chanceToSpawnSpecial = kahluaTableImpl.rawgetInt((Object)"chanceToSpawnSpecial");
            }
            if (kahluaTableImpl.delegate.containsKey("specialCar")) {
                vehicleType.isSpecialCar = kahluaTableImpl.rawgetBool((Object)"specialCar");
            }
            if (kahluaTableImpl.delegate.containsKey("burntCar")) {
                vehicleType.isBurntCar = kahluaTableImpl.rawgetBool((Object)"burntCar");
            }
            if (kahluaTableImpl.delegate.containsKey("baseVehicleQuality")) {
                vehicleType.baseVehicleQuality = kahluaTableImpl.rawgetFloat((Object)"baseVehicleQuality");
            }
            if (kahluaTableImpl.delegate.containsKey("chanceOfOverCar")) {
                vehicleType.chanceOfOverCar = kahluaTableImpl.rawgetInt((Object)"chanceOfOverCar");
            }
            if (kahluaTableImpl.delegate.containsKey("randomAngle")) {
                vehicleType.randomAngle = kahluaTableImpl.rawgetBool((Object)"randomAngle");
            }
            if (kahluaTableImpl.delegate.containsKey("spawnRate")) {
                vehicleType.spawnRate = kahluaTableImpl.rawgetInt((Object)"spawnRate");
            }
            if (kahluaTableImpl.delegate.containsKey("chanceToSpawnKey")) {
                vehicleType.chanceToSpawnKey = kahluaTableImpl.rawgetInt((Object)"chanceToSpawnKey");
            }
            if (kahluaTableImpl.delegate.containsKey("chanceToSpawnBurnt")) {
                vehicleType.chanceToSpawnBurnt = kahluaTableImpl.rawgetInt((Object)"chanceToSpawnBurnt");
            }
            VehicleType.vehicles.put(string, vehicleType);
            if (vehicleType.isSpecialCar) {
                VehicleType.specialVehicles.add(vehicleType);
            }
        }
        final HashSet<String> set = new HashSet<String>();
        final Iterator<VehicleType> iterator3 = VehicleType.vehicles.values().iterator();
        while (iterator3.hasNext()) {
            final Iterator<VehicleTypeDefinition> iterator4 = iterator3.next().vehiclesDefinition.iterator();
            while (iterator4.hasNext()) {
                set.add(iterator4.next().vehicleType);
            }
        }
        for (final VehicleScript vehicleScript : ScriptManager.instance.getAllVehicleScripts()) {
            if (!set.contains(vehicleScript.getFullName())) {
                DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, vehicleScript.getFullName()));
            }
        }
    }
    
    public static boolean hasTypeForZone(String lowerCase) {
        if (VehicleType.vehicles.isEmpty()) {
            init();
        }
        lowerCase = lowerCase.toLowerCase();
        return VehicleType.vehicles.containsKey(lowerCase);
    }
    
    public static VehicleType getRandomVehicleType(final String s) {
        return getRandomVehicleType(s, true);
    }
    
    public static VehicleType getRandomVehicleType(String lowerCase, final Boolean b) {
        if (VehicleType.vehicles.isEmpty()) {
            init();
        }
        lowerCase = lowerCase.toLowerCase();
        VehicleType vehicleType = VehicleType.vehicles.get(lowerCase);
        if (vehicleType == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lowerCase));
            return null;
        }
        if (Rand.Next(100) < vehicleType.chanceToSpawnBurnt) {
            VehicleType vehicleType2;
            if (Rand.Next(100) < 80) {
                vehicleType2 = VehicleType.vehicles.get("normalburnt");
            }
            else {
                vehicleType2 = VehicleType.vehicles.get("specialburnt");
            }
            return vehicleType2;
        }
        if (b && vehicleType.isSpecialCar && Rand.Next(100) < vehicleType.chanceToSpawnNormal) {
            vehicleType = VehicleType.vehicles.get("parkingstall");
        }
        if (!vehicleType.isBurntCar && !vehicleType.isSpecialCar && Rand.Next(100) < vehicleType.chanceToSpawnSpecial) {
            vehicleType = PZArrayUtil.pickRandom(VehicleType.specialVehicles);
        }
        if (vehicleType.isBurntCar) {
            if (Rand.Next(100) < 80) {
                vehicleType = VehicleType.vehicles.get("normalburnt");
            }
            else {
                vehicleType = VehicleType.vehicles.get("specialburnt");
            }
        }
        return vehicleType;
    }
    
    public static VehicleType getTypeFromName(final String key) {
        if (VehicleType.vehicles.isEmpty()) {
            init();
        }
        return VehicleType.vehicles.get(key);
    }
    
    public float getBaseVehicleQuality() {
        return this.baseVehicleQuality;
    }
    
    public float getRandomBaseVehicleQuality() {
        return Rand.Next(this.baseVehicleQuality - 0.1f, this.baseVehicleQuality + 0.1f);
    }
    
    public int getChanceToSpawnKey() {
        return this.chanceToSpawnKey;
    }
    
    public void setChanceToSpawnKey(final int chanceToSpawnKey) {
        this.chanceToSpawnKey = chanceToSpawnKey;
    }
    
    public static void Reset() {
        VehicleType.vehicles.clear();
        VehicleType.specialVehicles.clear();
    }
    
    static {
        vehicles = new HashMap<String, VehicleType>();
        specialVehicles = new ArrayList<VehicleType>();
    }
    
    public static class VehicleTypeDefinition
    {
        public String vehicleType;
        public int index;
        public float spawnChance;
        
        public VehicleTypeDefinition(final String vehicleType, final int index, final float spawnChance) {
            this.index = -1;
            this.spawnChance = 0.0f;
            this.vehicleType = vehicleType;
            this.index = index;
            this.spawnChance = spawnChance;
        }
    }
}
