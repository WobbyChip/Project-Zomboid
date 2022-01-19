// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import org.junit.Test;
import org.junit.Assert;

public class test_VehicleCache extends Assert
{
    @Test
    public void test_getInsertIndex() {
        VehicleCache.vehicleUpdate((short)1, 10.0f, 20.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)2, 10.0f, 21.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)3, 15.0f, 22.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)4, 1010.0f, 1020.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)5, 2000.0f, 2020.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)6, 3010.0f, 3000.0f, 0.0f);
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(1, 2));
        assertEquals((Object)null, (Object)VehicleCache.vehicleGet(1, 3));
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(10.0f, 20.0f));
        assertEquals((Object)null, (Object)VehicleCache.vehicleGet(10.0f, 30.0f));
        assertEquals(3L, (long)VehicleCache.vehicleGet(10.0f, 20.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(1010.0f, 1020.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(2000.0f, 2020.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(3010.0f, 3000.0f).size());
        VehicleCache.vehicleUpdate((short)1, 12.0f, 200.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)2, 10.0f, 210.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)3, 10.0f, 25.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)4, 1020.0f, 1020.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)5, 2000.0f, 2030.0f, 0.0f);
        VehicleCache.vehicleUpdate((short)6, 3010.3f, 3000.1f, 0.0f);
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(10.0f, 20.0f));
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(10.0f, 200.0f));
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(10.0f, 210.0f));
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(1020.0f, 1020.0f));
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(2000.0f, 2030.0f));
        assertNotEquals((Object)null, (Object)VehicleCache.vehicleGet(3010.0f, 3000.0f));
        assertEquals(1L, (long)VehicleCache.vehicleGet(10.0f, 20.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(10.0f, 200.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(10.0f, 210.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(1020.0f, 1020.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(2000.0f, 2030.0f).size());
        assertEquals(1L, (long)VehicleCache.vehicleGet(3010.0f, 3000.0f).size());
        assertEquals(0L, (long)VehicleCache.vehicleGet(1010.0f, 1020.0f).size());
        assertEquals(0L, (long)VehicleCache.vehicleGet(2000.0f, 2020.0f).size());
        assertEquals(3L, (long)VehicleCache.vehicleGet(10.0f, 20.0f).get(0).id);
        assertEquals(1L, (long)VehicleCache.vehicleGet(10.0f, 200.0f).get(0).id);
        assertEquals(2L, (long)VehicleCache.vehicleGet(10.0f, 210.0f).get(0).id);
        assertEquals(4L, (long)VehicleCache.vehicleGet(1020.0f, 1020.0f).get(0).id);
        assertEquals(5L, (long)VehicleCache.vehicleGet(2000.0f, 2030.0f).get(0).id);
        assertEquals(6L, (long)VehicleCache.vehicleGet(3010.0f, 3000.0f).get(0).id);
    }
}
