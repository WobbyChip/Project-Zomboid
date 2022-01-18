// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import org.junit.Test;
import zombie.iso.Vector2;
import org.junit.Assert;

public class test_IsQuadranglesAreTransposed extends Assert
{
    @Test
    public void testIsQuadranglesAreTransposed_1() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }, new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_2() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }, new Vector2[] { new Vector2(2.5f, 2.0f), new Vector2(3.5f, 2.0f), new Vector2(3.5f, 4.0f), new Vector2(2.5f, 4.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_3() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.5f), new Vector2(3.0f, 2.5f), new Vector2(3.0f, 4.5f), new Vector2(2.0f, 4.5f) }, new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_4() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }, new Vector2[] { new Vector2(2.5f, 2.5f), new Vector2(3.5f, 2.5f), new Vector2(3.5f, 4.5f), new Vector2(2.5f, 4.5f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_5() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(1.0f, 1.0f), new Vector2(6.0f, 1.0f), new Vector2(6.0f, 6.0f), new Vector2(1.0f, 6.0f) }, new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_6() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 5.0f), new Vector2(2.0f, 5.0f) }, new Vector2[] { new Vector2(1.0f, 3.0f), new Vector2(5.0f, 3.0f), new Vector2(5.0f, 4.0f), new Vector2(1.0f, 4.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_7() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(10.0f, 10.0f), new Vector2(15.0f, 15.0f), new Vector2(18.0f, 12.0f), new Vector2(13.0f, 7.0f) }, new Vector2[] { new Vector2(18.0f, 9.0f), new Vector2(14.0f, 16.0f), new Vector2(11.0f, 14.0f), new Vector2(17.0f, 7.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_8() {
        assertEquals((Object)false, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }, new Vector2[] { new Vector2(4.0f, 2.0f), new Vector2(5.0f, 2.0f), new Vector2(5.0f, 4.0f), new Vector2(4.0f, 4.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_9() {
        assertEquals((Object)false, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }, new Vector2[] { new Vector2(2.0f, 5.0f), new Vector2(3.0f, 5.0f), new Vector2(3.0f, 10.0f), new Vector2(2.0f, 10.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_10() {
        assertEquals((Object)false, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(2.0f, 2.0f), new Vector2(3.0f, 2.0f), new Vector2(3.0f, 4.0f), new Vector2(2.0f, 4.0f) }, new Vector2[] { new Vector2(5.0f, 5.0f), new Vector2(6.0f, 5.0f), new Vector2(6.0f, 7.0f), new Vector2(5.0f, 7.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_11() {
        assertEquals((Object)false, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(10.0f, 10.0f), new Vector2(15.0f, 15.0f), new Vector2(18.0f, 12.0f), new Vector2(13.0f, 7.0f) }, new Vector2[] { new Vector2(28.0f, 9.0f), new Vector2(24.0f, 16.0f), new Vector2(21.0f, 14.0f), new Vector2(27.0f, 7.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_12() {
        assertEquals((Object)true, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(10860.373f, 9928.479f), new Vector2(10860.373f, 9926.521f), new Vector2(10865.627f, 9926.521f), new Vector2(10865.627f, 9928.479f) }, new Vector2[] { new Vector2(10864.479f, 9929.127f), new Vector2(10862.521f, 9929.127f), new Vector2(10862.521f, 9923.873f), new Vector2(10864.479f, 9923.873f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_13() {
        assertEquals((Object)false, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(new Vector2[] { new Vector2(10.0f, 10.0f), new Vector2(15.0f, 15.0f), new Vector2(18.0f, 12.0f) }, new Vector2[] { new Vector2(28.0f, 9.0f), new Vector2(24.0f, 16.0f), new Vector2(21.0f, 14.0f) }));
    }
    
    @Test
    public void testIsQuadranglesAreTransposed_14() {
        assertEquals((Object)false, (Object)QuadranglesIntersection.IsQuadranglesAreIntersected(null, null));
    }
}
