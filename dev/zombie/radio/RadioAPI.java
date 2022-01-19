// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

import java.util.Iterator;
import java.util.Map;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;

public final class RadioAPI
{
    private static RadioAPI instance;
    
    public static int timeToTimeStamp(final int n, final int n2, final int n3) {
        return n * 24 + n2 * 60 + n3;
    }
    
    public static int timeStampToDays(final int n) {
        return n / 1440;
    }
    
    public static int timeStampToHours(final int n) {
        return n / 60 % 24;
    }
    
    public static int timeStampToMinutes(final int n) {
        return n % 60;
    }
    
    public static boolean hasInstance() {
        return RadioAPI.instance != null;
    }
    
    public static RadioAPI getInstance() {
        if (RadioAPI.instance == null) {
            RadioAPI.instance = new RadioAPI();
        }
        return RadioAPI.instance;
    }
    
    private RadioAPI() {
    }
    
    public KahluaTable getChannels(final String s) {
        final Map<Integer, String> getChannelList = ZomboidRadio.getInstance().GetChannelList(s);
        final KahluaTable table = LuaManager.platform.newTable();
        if (getChannelList != null) {
            for (final Map.Entry<Integer, String> entry : getChannelList.entrySet()) {
                table.rawset((Object)entry.getKey(), (Object)entry.getValue());
            }
        }
        return table;
    }
}
