// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world.moddata;

import se.krka.kahlua.vm.KahluaTable;
import java.util.List;
import java.util.ArrayList;

public final class ModData
{
    private static final ArrayList<String> temp_list;
    
    public static ArrayList<String> getTableNames() {
        GlobalModData.instance.collectTableNames(ModData.temp_list);
        return ModData.temp_list;
    }
    
    public static boolean exists(final String s) {
        return GlobalModData.instance.exists(s);
    }
    
    public static KahluaTable getOrCreate(final String s) {
        return GlobalModData.instance.getOrCreate(s);
    }
    
    public static KahluaTable get(final String s) {
        return GlobalModData.instance.get(s);
    }
    
    public static String create() {
        return GlobalModData.instance.create();
    }
    
    public static KahluaTable create(final String s) {
        return GlobalModData.instance.create(s);
    }
    
    public static KahluaTable remove(final String s) {
        return GlobalModData.instance.remove(s);
    }
    
    public static void add(final String s, final KahluaTable kahluaTable) {
        GlobalModData.instance.add(s, kahluaTable);
    }
    
    public static void transmit(final String s) {
        GlobalModData.instance.transmit(s);
    }
    
    public static void request(final String s) {
        GlobalModData.instance.request(s);
    }
    
    static {
        temp_list = new ArrayList<String>();
    }
}
