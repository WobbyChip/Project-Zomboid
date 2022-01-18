// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.junit.Test;
import org.joml.Vector2i;
import org.junit.Assert;

public class test_DiamondMatrixIterator extends Assert
{
    @Test
    public void test3a() {
        final int n = 3;
        final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(n);
        for (int i = 0; i <= n * n; ++i) {
            System.out.println(invokedynamic(makeConcatWithConstants:(ILorg/joml/Vector2i;)Ljava/lang/String;, i, diamondMatrixIterator.i2line(i)));
        }
        assertEquals((Object)new Vector2i(0, 0), (Object)diamondMatrixIterator.i2line(0));
        assertEquals((Object)new Vector2i(0, 1), (Object)diamondMatrixIterator.i2line(1));
        assertEquals((Object)new Vector2i(1, 1), (Object)diamondMatrixIterator.i2line(2));
        assertEquals((Object)new Vector2i(0, 2), (Object)diamondMatrixIterator.i2line(3));
        assertEquals((Object)new Vector2i(1, 2), (Object)diamondMatrixIterator.i2line(4));
        assertEquals((Object)new Vector2i(2, 2), (Object)diamondMatrixIterator.i2line(5));
        assertEquals((Object)new Vector2i(0, 3), (Object)diamondMatrixIterator.i2line(6));
        assertEquals((Object)new Vector2i(1, 3), (Object)diamondMatrixIterator.i2line(7));
        assertEquals((Object)new Vector2i(0, 4), (Object)diamondMatrixIterator.i2line(8));
    }
    
    @Test
    public void test3() {
        final int n = 3;
        final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(n);
        for (int i = 0; i <= n * n; ++i) {
            System.out.println(invokedynamic(makeConcatWithConstants:(ILorg/joml/Vector2i;Lorg/joml/Vector2i;)Ljava/lang/String;, i, diamondMatrixIterator.i2line(i), diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(i))));
        }
        assertEquals((Object)new Vector2i(0, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(0)));
        assertEquals((Object)new Vector2i(0, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(1)));
        assertEquals((Object)new Vector2i(1, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(2)));
        assertEquals((Object)new Vector2i(0, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(3)));
        assertEquals((Object)new Vector2i(1, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(4)));
        assertEquals((Object)new Vector2i(2, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(5)));
        assertEquals((Object)new Vector2i(1, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(6)));
        assertEquals((Object)new Vector2i(2, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(7)));
        assertEquals((Object)new Vector2i(2, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(8)));
        assertEquals((Object)null, (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(9)));
    }
    
    @Test
    public void test3i() {
        final int n = 3;
        final Vector2i vector2i = new Vector2i();
        final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(n);
        for (int i = 0; i <= n * n; ++i) {
            diamondMatrixIterator.next(vector2i);
            System.out.println(invokedynamic(makeConcatWithConstants:(ILorg/joml/Vector2i;)Ljava/lang/String;, i, vector2i));
        }
        diamondMatrixIterator.reset();
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 2), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 2), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 2), (Object)vector2i);
        assertEquals((Object)false, (Object)diamondMatrixIterator.next(vector2i));
    }
    
    @Test
    public void test4() {
        final int n = 4;
        final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(n);
        for (int i = 0; i <= n * n; ++i) {
            System.out.println(invokedynamic(makeConcatWithConstants:(ILorg/joml/Vector2i;Lorg/joml/Vector2i;)Ljava/lang/String;, i, diamondMatrixIterator.i2line(i), diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(i))));
        }
        assertEquals((Object)new Vector2i(0, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(0)));
        assertEquals((Object)new Vector2i(0, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(1)));
        assertEquals((Object)new Vector2i(1, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(2)));
        assertEquals((Object)new Vector2i(0, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(3)));
        assertEquals((Object)new Vector2i(1, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(4)));
        assertEquals((Object)new Vector2i(2, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(5)));
        assertEquals((Object)new Vector2i(0, 3), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(6)));
        assertEquals((Object)new Vector2i(1, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(7)));
        assertEquals((Object)new Vector2i(2, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(8)));
        assertEquals((Object)new Vector2i(3, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(9)));
        assertEquals((Object)new Vector2i(1, 3), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(10)));
        assertEquals((Object)new Vector2i(2, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(11)));
        assertEquals((Object)new Vector2i(3, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(12)));
        assertEquals((Object)new Vector2i(2, 3), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(13)));
        assertEquals((Object)new Vector2i(3, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(14)));
        assertEquals((Object)new Vector2i(3, 3), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(15)));
        assertEquals((Object)null, (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(16)));
    }
    
    @Test
    public void test4i() {
        final int n = 4;
        final Vector2i vector2i = new Vector2i();
        final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(n);
        for (int i = 0; i <= n * n; ++i) {
            diamondMatrixIterator.next(vector2i);
            System.out.println(invokedynamic(makeConcatWithConstants:(ILorg/joml/Vector2i;)Ljava/lang/String;, i, vector2i));
        }
        diamondMatrixIterator.reset();
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 2), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(0, 3), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 2), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(3, 0), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(1, 3), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 2), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(3, 1), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(2, 3), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(3, 2), (Object)vector2i);
        assertEquals((Object)true, (Object)diamondMatrixIterator.next(vector2i));
        assertEquals((Object)new Vector2i(3, 3), (Object)vector2i);
        assertEquals((Object)false, (Object)diamondMatrixIterator.next(vector2i));
    }
    
    @Test
    public void test10() {
        final int y = 10;
        final DiamondMatrixIterator diamondMatrixIterator = new DiamondMatrixIterator(y);
        for (int i = 0; i <= y * y; ++i) {
            System.out.println(invokedynamic(makeConcatWithConstants:(ILorg/joml/Vector2i;Lorg/joml/Vector2i;)Ljava/lang/String;, i, diamondMatrixIterator.i2line(i), diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(i))));
        }
        final Vector2i vector2i = new Vector2i();
        vector2i.y = 0;
        while (vector2i.y < y) {
            vector2i.x = 0;
            while (vector2i.x <= vector2i.y) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/joml/Vector2i;Lorg/joml/Vector2i;)Ljava/lang/String;, vector2i, diamondMatrixIterator.line2coord(vector2i)));
                final Vector2i vector2i2 = vector2i;
                ++vector2i2.x;
            }
            final Vector2i vector2i3 = vector2i;
            ++vector2i3.y;
        }
        vector2i.y = y;
        while (vector2i.y <= y * 2) {
            vector2i.x = 0;
            while (vector2i.x <= 18 - vector2i.y) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/joml/Vector2i;Lorg/joml/Vector2i;)Ljava/lang/String;, vector2i, diamondMatrixIterator.line2coord(vector2i)));
                final Vector2i vector2i4 = vector2i;
                ++vector2i4.x;
            }
            final Vector2i vector2i5 = vector2i;
            ++vector2i5.y;
        }
        assertEquals((Object)new Vector2i(0, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(0)));
        assertEquals((Object)new Vector2i(0, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(1)));
        assertEquals((Object)new Vector2i(1, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(2)));
        assertEquals((Object)new Vector2i(0, 2), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(3)));
        assertEquals((Object)new Vector2i(1, 1), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(4)));
        assertEquals((Object)new Vector2i(2, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(5)));
        assertEquals((Object)new Vector2i(0, 9), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(45)));
        assertEquals((Object)new Vector2i(4, 5), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(49)));
        assertEquals((Object)new Vector2i(5, 4), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(50)));
        assertEquals((Object)new Vector2i(9, 0), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(54)));
        assertEquals((Object)new Vector2i(8, 9), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(97)));
        assertEquals((Object)new Vector2i(9, 8), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(98)));
        assertEquals((Object)new Vector2i(9, 9), (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(99)));
        assertEquals((Object)null, (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(100)));
        assertEquals((Object)null, (Object)diamondMatrixIterator.line2coord(diamondMatrixIterator.i2line(34536)));
    }
}
