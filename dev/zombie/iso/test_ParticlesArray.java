// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Comparator;
import zombie.core.Rand;
import org.junit.Test;
import org.junit.Assert;

public class test_ParticlesArray extends Assert
{
    @Test
    public void test_ParticlesArray_functional() {
        final ParticlesArray<Integer> particlesArray = new ParticlesArray<Integer>();
        particlesArray.addParticle(new Integer(1));
        particlesArray.addParticle(new Integer(2));
        particlesArray.addParticle(new Integer(3));
        particlesArray.addParticle(new Integer(4));
        particlesArray.addParticle(new Integer(5));
        particlesArray.addParticle(new Integer(6));
        particlesArray.addParticle(new Integer(7));
        particlesArray.addParticle(new Integer(8));
        particlesArray.addParticle(new Integer(9));
        assertEquals(9L, (long)particlesArray.size());
        assertEquals(9L, (long)particlesArray.getCount());
        for (int i = 0; i < 9; ++i) {
            assertEquals((long)(i + 1), (long)(int)particlesArray.get(i));
        }
        particlesArray.deleteParticle(0);
        particlesArray.deleteParticle(1);
        particlesArray.deleteParticle(4);
        particlesArray.deleteParticle(7);
        particlesArray.deleteParticle(8);
        assertEquals(9L, (long)particlesArray.size());
        assertEquals(4L, (long)particlesArray.getCount());
        assertEquals((Object)null, particlesArray.get(0));
        assertEquals((Object)null, particlesArray.get(1));
        assertEquals(3L, (long)(int)particlesArray.get(2));
        assertEquals(4L, (long)(int)particlesArray.get(3));
        assertEquals((Object)null, particlesArray.get(4));
        assertEquals(6L, (long)(int)particlesArray.get(5));
        assertEquals(7L, (long)(int)particlesArray.get(6));
        assertEquals((Object)null, particlesArray.get(7));
        assertEquals((Object)null, particlesArray.get(8));
        particlesArray.defragmentParticle();
        assertEquals(9L, (long)particlesArray.size());
        assertEquals(4L, (long)particlesArray.getCount());
        assertEquals(7L, (long)(int)particlesArray.get(0));
        assertEquals(6L, (long)(int)particlesArray.get(1));
        assertEquals(3L, (long)(int)particlesArray.get(2));
        assertEquals(4L, (long)(int)particlesArray.get(3));
        assertEquals((Object)null, particlesArray.get(4));
        assertEquals((Object)null, particlesArray.get(5));
        assertEquals((Object)null, particlesArray.get(6));
        assertEquals((Object)null, particlesArray.get(7));
        assertEquals((Object)null, particlesArray.get(8));
        particlesArray.addParticle(new Integer(11));
        particlesArray.addParticle(new Integer(12));
        particlesArray.addParticle(new Integer(13));
        particlesArray.addParticle(new Integer(14));
        particlesArray.addParticle(new Integer(15));
        particlesArray.addParticle(new Integer(16));
        assertEquals(10L, (long)particlesArray.size());
        assertEquals(10L, (long)particlesArray.getCount());
        assertEquals(7L, (long)(int)particlesArray.get(0));
        assertEquals(6L, (long)(int)particlesArray.get(1));
        assertEquals(3L, (long)(int)particlesArray.get(2));
        assertEquals(4L, (long)(int)particlesArray.get(3));
        assertEquals(11L, (long)(int)particlesArray.get(4));
        assertEquals(12L, (long)(int)particlesArray.get(5));
        assertEquals(13L, (long)(int)particlesArray.get(6));
        assertEquals(14L, (long)(int)particlesArray.get(7));
        assertEquals(15L, (long)(int)particlesArray.get(8));
        assertEquals(16L, (long)(int)particlesArray.get(9));
        particlesArray.deleteParticle(0);
        particlesArray.deleteParticle(1);
        particlesArray.deleteParticle(4);
        particlesArray.deleteParticle(7);
        particlesArray.deleteParticle(8);
        particlesArray.deleteParticle(9);
        assertEquals(10L, (long)particlesArray.size());
        assertEquals(4L, (long)particlesArray.getCount());
        assertEquals((Object)null, particlesArray.get(0));
        assertEquals((Object)null, particlesArray.get(1));
        assertEquals(3L, (long)(int)particlesArray.get(2));
        assertEquals(4L, (long)(int)particlesArray.get(3));
        assertEquals((Object)null, particlesArray.get(4));
        assertEquals(12L, (long)(int)particlesArray.get(5));
        assertEquals(13L, (long)(int)particlesArray.get(6));
        assertEquals((Object)null, particlesArray.get(7));
        assertEquals((Object)null, particlesArray.get(8));
        assertEquals((Object)null, particlesArray.get(9));
        particlesArray.defragmentParticle();
        assertEquals(10L, (long)particlesArray.size());
        assertEquals(4L, (long)particlesArray.getCount());
        assertEquals(13L, (long)(int)particlesArray.get(0));
        assertEquals(12L, (long)(int)particlesArray.get(1));
        assertEquals(3L, (long)(int)particlesArray.get(2));
        assertEquals(4L, (long)(int)particlesArray.get(3));
        assertEquals((Object)null, particlesArray.get(4));
        assertEquals((Object)null, particlesArray.get(5));
        assertEquals((Object)null, particlesArray.get(6));
        assertEquals((Object)null, particlesArray.get(7));
        assertEquals((Object)null, particlesArray.get(8));
        assertEquals((Object)null, particlesArray.get(9));
        particlesArray.addParticle(new Integer(21));
        particlesArray.addParticle(new Integer(22));
        assertEquals(10L, (long)particlesArray.size());
        assertEquals(6L, (long)particlesArray.getCount());
        assertEquals(13L, (long)(int)particlesArray.get(0));
        assertEquals(12L, (long)(int)particlesArray.get(1));
        assertEquals(3L, (long)(int)particlesArray.get(2));
        assertEquals(4L, (long)(int)particlesArray.get(3));
        assertEquals(21L, (long)(int)particlesArray.get(4));
        assertEquals(22L, (long)(int)particlesArray.get(5));
        assertEquals((Object)null, particlesArray.get(6));
        assertEquals((Object)null, particlesArray.get(7));
        assertEquals((Object)null, particlesArray.get(8));
        assertEquals((Object)null, particlesArray.get(9));
        assertEquals(6L, (long)particlesArray.addParticle(new Integer(31)));
        assertEquals(7L, (long)particlesArray.addParticle(new Integer(32)));
        assertEquals(8L, (long)particlesArray.addParticle(new Integer(33)));
        assertEquals(9L, (long)particlesArray.addParticle(new Integer(34)));
        assertEquals(10L, (long)particlesArray.addParticle(new Integer(35)));
        assertEquals(11L, (long)particlesArray.size());
        assertEquals(11L, (long)particlesArray.getCount());
        particlesArray.deleteParticle(4);
        assertEquals(11L, (long)particlesArray.size());
        assertEquals(10L, (long)particlesArray.getCount());
        assertEquals(4L, (long)particlesArray.addParticle(new Integer(36)));
    }
    
    @Test
    public void test_ParticlesArray_Failure() {
        final ParticlesArray<Integer> particlesArray = new ParticlesArray<Integer>();
        particlesArray.addParticle(new Integer(1));
        particlesArray.addParticle(new Integer(2));
        particlesArray.addParticle(new Integer(3));
        particlesArray.addParticle(new Integer(4));
        particlesArray.addParticle(new Integer(5));
        particlesArray.addParticle(new Integer(6));
        particlesArray.addParticle(new Integer(7));
        particlesArray.addParticle(new Integer(8));
        particlesArray.addParticle(new Integer(9));
        assertEquals(9L, (long)particlesArray.size());
        assertEquals(9L, (long)particlesArray.getCount());
        for (int i = 0; i < 9; ++i) {
            assertEquals((long)(i + 1), (long)(int)particlesArray.get(i));
        }
        particlesArray.deleteParticle(-1);
        particlesArray.deleteParticle(100);
        particlesArray.addParticle(null);
        assertEquals(9L, (long)particlesArray.size());
        assertEquals(9L, (long)particlesArray.getCount());
        for (int j = 0; j < 9; ++j) {
            assertEquals((long)(j + 1), (long)(int)particlesArray.get(j));
        }
        particlesArray.deleteParticle(3);
        particlesArray.deleteParticle(3);
        particlesArray.deleteParticle(3);
    }
    
    @Test
    public void test_ParticlesArray_time() {
        final ParticlesArray<Integer> particlesArray = new ParticlesArray<Integer>();
        final long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            particlesArray.addParticle(new Integer(i));
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(JII)Ljava/lang/String;, System.currentTimeMillis() - currentTimeMillis, particlesArray.size(), particlesArray.getCount()));
        int n2 = 0;
        final long currentTimeMillis2 = System.currentTimeMillis();
        for (int j = 0; j < 1000000; ++j) {
            if (particlesArray.deleteParticle(j)) {
                ++n2;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, n2, System.currentTimeMillis() - currentTimeMillis2, particlesArray.size(), particlesArray.getCount()));
        final long currentTimeMillis3 = System.currentTimeMillis();
        for (int k = 0; k < 1000000; ++k) {
            particlesArray.addParticle(new Integer(k));
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(JII)Ljava/lang/String;, System.currentTimeMillis() - currentTimeMillis3, particlesArray.size(), particlesArray.getCount()));
        Rand.init();
        int n3 = 0;
        final long currentTimeMillis4 = System.currentTimeMillis();
        for (int l = 0; l < 500000; ++l) {
            if (particlesArray.deleteParticle(Rand.Next(1000000))) {
                ++n3;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, n3, System.currentTimeMillis() - currentTimeMillis4, particlesArray.size(), particlesArray.getCount()));
        final long currentTimeMillis5 = System.currentTimeMillis();
        for (int value = 0; value < 1000000; ++value) {
            particlesArray.addParticle(new Integer(value));
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(JII)Ljava/lang/String;, System.currentTimeMillis() - currentTimeMillis5, particlesArray.size(), particlesArray.getCount()));
        final Comparator<? super Object> c = (n, anotherInteger) -> n.compareTo(anotherInteger);
        final long currentTimeMillis6 = System.currentTimeMillis();
        particlesArray.sort(c);
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, particlesArray.size(), System.currentTimeMillis() - currentTimeMillis6, particlesArray.size(), particlesArray.getCount()));
        int n4 = 0;
        final long currentTimeMillis7 = System.currentTimeMillis();
        for (int n5 = 0; n5 < 500000; ++n5) {
            if (particlesArray.deleteParticle(Rand.Next(1000000))) {
                ++n4;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, n4, System.currentTimeMillis() - currentTimeMillis7, particlesArray.size(), particlesArray.getCount()));
        final long currentTimeMillis8 = System.currentTimeMillis();
        particlesArray.defragmentParticle();
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, particlesArray.size(), System.currentTimeMillis() - currentTimeMillis8, particlesArray.size(), particlesArray.getCount()));
        final long currentTimeMillis9 = System.currentTimeMillis();
        for (int value2 = 0; value2 < 1000000; ++value2) {
            particlesArray.addParticle(new Integer(value2));
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(JII)Ljava/lang/String;, System.currentTimeMillis() - currentTimeMillis9, particlesArray.size(), particlesArray.getCount()));
        int n6 = 0;
        final long currentTimeMillis10 = System.currentTimeMillis();
        for (int n7 = 0; n7 < 500000; ++n7) {
            if (particlesArray.deleteParticle(Rand.Next(1000000))) {
                ++n6;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, n6, System.currentTimeMillis() - currentTimeMillis10, particlesArray.size(), particlesArray.getCount()));
        final long currentTimeMillis11 = System.currentTimeMillis();
        for (int value3 = 0; value3 < 1000000; ++value3) {
            particlesArray.addParticle(new Integer(value3));
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(JII)Ljava/lang/String;, System.currentTimeMillis() - currentTimeMillis11, particlesArray.size(), particlesArray.getCount()));
        int n8 = 0;
        final long currentTimeMillis12 = System.currentTimeMillis();
        for (int n9 = 0; n9 < 1000000; ++n9) {
            if (particlesArray.deleteParticle(Rand.Next(1000000))) {
                ++n8;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, n8, System.currentTimeMillis() - currentTimeMillis12, particlesArray.size(), particlesArray.getCount()));
        final long currentTimeMillis13 = System.currentTimeMillis();
        int n10 = 0;
        for (int value4 = 0; value4 < 100000; ++value4) {
            for (int n11 = 0; n11 < particlesArray.size(); ++n11) {
                if (particlesArray.get(n11) == null) {
                    particlesArray.set(n11, new Integer(value4));
                    ++n10;
                    break;
                }
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(IJII)Ljava/lang/String;, n10, System.currentTimeMillis() - currentTimeMillis13, particlesArray.size(), particlesArray.getCount()));
    }
}
