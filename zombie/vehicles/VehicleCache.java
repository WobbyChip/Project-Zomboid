// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.LinkedList;
import java.util.List;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;

public final class VehicleCache
{
    public short id;
    float x;
    float y;
    float z;
    private static TShortObjectHashMap<VehicleCache> mapId;
    private static TIntObjectHashMap<List<VehicleCache>> mapXY;
    
    public static void vehicleUpdate(final short id, final float n, final float n2, final float n3) {
        final VehicleCache vehicleCache = (VehicleCache)VehicleCache.mapId.get(id);
        if (vehicleCache != null) {
            final int n4 = (int)(vehicleCache.x / 10.0f);
            final int n5 = (int)(vehicleCache.y / 10.0f);
            final int n6 = (int)(n / 10.0f);
            final int n7 = (int)(n2 / 10.0f);
            if (n4 != n6 || n5 != n7) {
                ((List)VehicleCache.mapXY.get(n4 * 65536 + n5)).remove(vehicleCache);
                if (VehicleCache.mapXY.get(n6 * 65536 + n7) == null) {
                    VehicleCache.mapXY.put(n6 * 65536 + n7, (Object)new LinkedList());
                }
                ((List)VehicleCache.mapXY.get(n6 * 65536 + n7)).add(vehicleCache);
            }
            vehicleCache.x = n;
            vehicleCache.y = n2;
            vehicleCache.z = n3;
        }
        else {
            final VehicleCache vehicleCache2 = new VehicleCache();
            vehicleCache2.id = id;
            vehicleCache2.x = n;
            vehicleCache2.y = n2;
            vehicleCache2.z = n3;
            VehicleCache.mapId.put(id, (Object)vehicleCache2);
            final int n8 = (int)(n / 10.0f);
            final int n9 = (int)(n2 / 10.0f);
            if (VehicleCache.mapXY.get(n8 * 65536 + n9) == null) {
                VehicleCache.mapXY.put(n8 * 65536 + n9, (Object)new LinkedList());
            }
            ((List)VehicleCache.mapXY.get(n8 * 65536 + n9)).add(vehicleCache2);
        }
    }
    
    public static List<VehicleCache> vehicleGet(final float n, final float n2) {
        return (List<VehicleCache>)VehicleCache.mapXY.get((int)(n / 10.0f) * 65536 + (int)(n2 / 10.0f));
    }
    
    public static List<VehicleCache> vehicleGet(final int n, final int n2) {
        return (List<VehicleCache>)VehicleCache.mapXY.get(n * 65536 + n2);
    }
    
    public static void remove(final short n) {
        final VehicleCache vehicleCache = (VehicleCache)VehicleCache.mapId.get(n);
        if (vehicleCache == null) {
            return;
        }
        VehicleCache.mapId.remove(n);
        final int n2 = (int)(vehicleCache.x / 10.0f) * 65536 + (int)(vehicleCache.y / 10.0f);
        assert VehicleCache.mapXY.containsKey(n2);
        assert ((List)VehicleCache.mapXY.get(n2)).contains(vehicleCache);
        ((List)VehicleCache.mapXY.get(n2)).remove(vehicleCache);
    }
    
    public static void Reset() {
        VehicleCache.mapId.clear();
        VehicleCache.mapXY.clear();
    }
    
    static {
        VehicleCache.mapId = (TShortObjectHashMap<VehicleCache>)new TShortObjectHashMap();
        VehicleCache.mapXY = (TIntObjectHashMap<List<VehicleCache>>)new TIntObjectHashMap();
        VehicleCache.mapId.setAutoCompactionFactor(0.0f);
        VehicleCache.mapXY.setAutoCompactionFactor(0.0f);
    }
}
