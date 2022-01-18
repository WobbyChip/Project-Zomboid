// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.junit.Test;
import org.junit.Assert;

public class test_ObjectsSyncRequests_getObjectInsertIndex extends Assert
{
    @Test
    public void test_getInsertIndex() {
        final long[] array = { 13L, 88L, 51L };
        final long[] array2 = { 8L, 13L, 52L, 21L, 88L, 36L, 51L, 15L };
        assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 8L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 13L));
        assertEquals(1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 52L));
        assertEquals(1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 21L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 88L));
        assertEquals(2L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 36L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 51L));
        assertEquals(3L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 15L));
    }
    
    @Test
    public void test_getInsertIndex2() {
        final long[] array = new long[0];
        final long[] array2 = { 81L, 45L, 72L };
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 8L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 13L));
        assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 81L));
        assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 45L));
        assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 72L));
    }
    
    @Test
    public void test_getInsertIndex3() {
        final long[] array = { 71L, 66L, 381L };
        final long[] array2 = { 55L, 81L, 71L, 41L, 66L, 381L, 68L };
        assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 55L));
        assertEquals(0L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 81L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 71L));
        assertEquals(1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 41L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 66L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 381L));
        assertEquals(3L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 68L));
        assertEquals(-1L, (long)ObjectsSyncRequests.getObjectInsertIndex(array, array2, 33L));
    }
}
