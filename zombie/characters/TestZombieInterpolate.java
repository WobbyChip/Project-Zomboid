// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.Vector2;
import org.junit.Test;
import org.junit.Assert;

public class TestZombieInterpolate extends Assert
{
    @Test
    public void test_predictor_stay() {
        final NetworkCharacter networkCharacter = new NetworkCharacter();
        int n = 10000;
        final int n2 = 250;
        final float n3 = 100.0f;
        final float n4 = 200.0f;
        final float n5 = 1.0f;
        final float n6 = -1.0f;
        networkCharacter.predict(n2, n, n3, n4, n5, n6);
        for (int i = 0; i < 10; ++i) {
            final NetworkCharacter.Transform predict = networkCharacter.predict(n2, n, n3, n4, n5, n6);
            assertEquals(100.0f, predict.position.x, 0.01f);
            assertEquals(200.0f, predict.position.y, 0.01f);
        }
        for (int j = 0; j < 10; ++j) {
            n += n2;
            final NetworkCharacter.Transform predict2 = networkCharacter.predict(n2, n, n3, n4, n5, n6);
            assertEquals(100.0f, predict2.position.x, 0.01f);
            assertEquals(200.0f, predict2.position.y, 0.01f);
        }
    }
    
    @Test
    public void test_predictor_normal_go() {
        final NetworkCharacter networkCharacter = new NetworkCharacter();
        int n = 10000;
        final int n2 = 250;
        float n3 = 100.0f;
        float n4 = 200.0f;
        final float n5 = 1.0f;
        final float n6 = -1.0f;
        final NetworkCharacter.Transform predict = networkCharacter.predict(n2, n, n3, n4, n5, n6);
        assertEquals(100.0f, predict.position.x, 0.01f);
        assertEquals(200.0f, predict.position.y, 0.01f);
        for (int i = 0; i < 30; ++i) {
            n += n2;
            n3 += 10.0f;
            n4 -= 2.5f;
            final NetworkCharacter.Transform predict2 = networkCharacter.predict(n2, n, n3, n4, n5, n6);
            assertEquals(n3 + 10.0f, predict2.position.x, 0.01f);
            assertEquals(n4 - 2.5f, predict2.position.y, 0.01f);
        }
    }
    
    @Test
    public void test_predictor() {
        final NetworkCharacter networkCharacter = new NetworkCharacter();
        final int n = 10000;
        final int n2 = 200;
        final float n3 = 100.0f;
        final float n4 = 200.0f;
        final float n5 = 1.0f;
        final float n6 = -1.0f;
        final NetworkCharacter.Transform predict = networkCharacter.predict(n2, n, n3, n4, n5, n6);
        assertEquals(100.0f, predict.position.x, 0.01f);
        assertEquals(200.0f, predict.position.y, 0.01f);
        int n7 = n + n2;
        final NetworkCharacter.Transform predict2 = networkCharacter.predict(n2, n7, n3 + 200.0f, n4 + 100.0f, n5, n6);
        assertEquals(500.0f, predict2.position.x, 0.01f);
        assertEquals(400.0f, predict2.position.y, 0.01f);
        n7 += 10000;
        final NetworkCharacter.Transform predict3 = networkCharacter.predict(n2, n7, 500.0f, 500.0f, n5, n6);
        assertEquals(500.0f, predict3.position.x, 0.01f);
        assertEquals(500.0f, predict3.position.y, 0.01f);
        final NetworkCharacter.Transform predict4 = networkCharacter.predict(n2, n7 + n2, 400.0f, 300.0f, n5, n6);
        assertEquals(300.0f, predict4.position.x, 0.01f);
        assertEquals(100.0f, predict4.position.y, 0.01f);
    }
    
    @Test
    public void test_predictor_normal_rotate() {
        final NetworkCharacter networkCharacter = new NetworkCharacter();
        int n = 10000;
        final int n2 = 250;
        float n3 = 100.0f;
        float n4 = 200.0f;
        final float n5 = 1.0f;
        final float n6 = -1.0f;
        final NetworkCharacter.Transform predict = networkCharacter.predict(n2, n, n3, n4, n5, n6);
        assertEquals(100.0f, predict.position.x, 0.01f);
        assertEquals(200.0f, predict.position.y, 0.01f);
        for (int i = 0; i < 10; ++i) {
            n += n2;
            n3 += 10.0f;
            n4 -= 2.5f;
            final NetworkCharacter.Transform predict2 = networkCharacter.predict(n2, n, n3, n4, n5, n6);
            assertEquals(n3 + 10.0f, predict2.position.x, 0.01f);
            assertEquals(n4 - 2.5f, predict2.position.y, 0.01f);
        }
        for (int j = 0; j < 10; ++j) {
            n += n2;
            n3 -= 10.0f;
            n4 += 2.5f;
            final NetworkCharacter.Transform predict3 = networkCharacter.predict(n2, n, n3, n4, n5, n6);
            assertEquals(n3 - 10.0f, predict3.position.x, 0.01f);
            assertEquals(n4 + 2.5f, predict3.position.y, 0.01f);
        }
    }
    
    @Test
    public void test_reconstructor_stay() {
        final NetworkCharacter networkCharacter = new NetworkCharacter(0.0f, 100.0f, 0L);
        NetworkCharacter.Transform transform = networkCharacter.transform;
        int n = 10000;
        final int n2 = 250;
        final float n3 = 100.0f;
        final float n4 = 200.0f;
        final float n5 = 1.0f;
        final float n6 = -1.0f;
        networkCharacter.updateInterpolationPoint(n, n3, n4, n5, n6);
        for (int i = 0; i < 10; ++i) {
            networkCharacter.updateInterpolationPoint(n, n3, n4, n5, n6);
            transform = networkCharacter.reconstruct(n, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            assertEquals(100.0f, transform.position.x, 0.01f);
            assertEquals(200.0f, transform.position.y, 0.01f);
        }
        for (int j = 0; j < 10; ++j) {
            n += n2;
            networkCharacter.updateInterpolationPoint(n, n3, n4, n5, n6);
            transform = networkCharacter.reconstruct(n, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            if (Float.isNaN(transform.position.x)) {
                assertEquals(100.0f, transform.position.x, 0.01f);
            }
            assertEquals(200.0f, transform.position.y, 0.01f);
        }
    }
    
    @Test
    public void test_reconstructor_normal_go() {
        final NetworkCharacter networkCharacter = new NetworkCharacter(0.0f, 100.0f, 0L);
        NetworkCharacter.Transform transform = networkCharacter.transform;
        int n2;
        int n = n2 = 10000;
        final int n3 = 250;
        float n4 = 100.0f;
        float n5 = 200.0f;
        final float n6 = 4.0f;
        final float n7 = -1.0f;
        networkCharacter.updateInterpolationPoint(n, n4, n5, n6, n7);
        for (int i = 0; i < 30; ++i) {
            n += n3;
            n4 += 10.0f;
            n5 -= 2.5f;
            networkCharacter.updateInterpolationPoint(n, n4, n5, n6, n7);
            transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            for (int j = 0; j < 5; ++j) {
                n2 += n3 / 5;
                transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
                assertEquals(n4 + (j + 1) * 2.0f - 10.0f, transform.position.x, 0.9f);
                assertEquals(n5 - (j + 1) * 0.5f + 2.5f, transform.position.y, 0.9f);
            }
        }
    }
    
    @Test
    public void test_reconstructor_unnormal_go() {
        final NetworkCharacter.Transform transform = new NetworkCharacter.Transform();
        transform.position = new Vector2();
        transform.rotation = new Vector2();
        final NetworkCharacter networkCharacter = new NetworkCharacter(0.0f, 100.0f, 0L);
        final NetworkCharacter.Transform transform2 = networkCharacter.transform;
        int n2;
        final int n = n2 = 10000;
        final int n3 = 250;
        final float n4 = 100.0f;
        final float n5 = 200.0f;
        final float n6 = 4.0f;
        final float n7 = -1.0f;
        System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n4, n5, n));
        networkCharacter.updateInterpolationPoint(n, n4, n5, n6, n7);
        int n8 = n + n3;
        float n9 = n4 + 10.0f;
        float n10 = n5 - 2.5f;
        System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n9, n10, n8));
        networkCharacter.updateInterpolationPoint(n8, n9, n10, n6, n7);
        NetworkCharacter.Transform transform3 = networkCharacter.reconstruct(n2, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y);
        for (int i = 0; i < 5; ++i) {
            n2 += n3 / 5;
            transform3 = networkCharacter.reconstruct(n2, transform3.position.x, transform3.position.y, transform3.rotation.x, transform3.rotation.y);
            System.out.print(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, transform3.position.x, transform3.position.y));
            assertEquals(n9 + (i + 1) * 2.0f - 10.0f, transform3.position.x, 1.9f);
            assertEquals(n10 - (i + 1) * 0.5f + 2.5f, transform3.position.y, 1.5f);
        }
        for (int j = 0; j < 30; ++j) {
            n8 += n3;
            n9 += 10.0f;
            n10 -= 2.5f;
            System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n9, n10, n8));
            networkCharacter.updateInterpolationPoint(n8, n9, n10, n6, n7);
            for (int k = 0; k < 5; ++k) {
                n2 += n3 / 5;
                transform3 = networkCharacter.reconstruct(n2, transform3.position.x, transform3.position.y, transform3.rotation.x, transform3.rotation.y);
                System.out.print(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, transform3.position.x, transform3.position.y));
                assertEquals(n9 + (k + 1) * 2.0f - 10.0f, transform3.position.x, 1.1f);
                assertEquals(n10 - (k + 1) * 0.5f + 2.5f, transform3.position.y, 1.1f);
                transform.position.set(transform3.position);
                transform.rotation.set(transform3.rotation);
            }
        }
    }
    
    @Test
    public void test_all() {
        final NetworkCharacter networkCharacter = new NetworkCharacter(0.0f, 100.0f, 0L);
        NetworkCharacter.Transform transform = networkCharacter.transform;
        int n2;
        int n = n2 = 10000;
        final int n3 = 250;
        float n4 = 100.0f;
        float n5 = 200.0f;
        final float n6 = 0.04f;
        final float n7 = -0.01f;
        System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n4, n5, n));
        networkCharacter.updateInterpolationPoint(n, n4, n5, n6, n7);
        System.out.print("Normal interpolate\n");
        for (int i = 0; i < 10; ++i) {
            n += n3;
            n4 += 10.0f;
            n5 -= 2.5f;
            System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n4, n5, n));
            networkCharacter.updateInterpolationPoint(n, n4, n5, n6, n7);
            for (int j = 0; j < 5; ++j) {
                n2 += n3 / 5;
                transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
            }
        }
        System.out.print("Extrapolate\n");
        for (int k = 0; k < 20; ++k) {
            n2 += n3 / 5;
            transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
        }
        System.out.print("Teleport\n");
        int n8 = n + n3 * 10;
        float n9 = n4 + 100.0f;
        float n10 = n5 - 25.0f;
        System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n9, n10, n8));
        networkCharacter.updateInterpolationPoint(n8, n9, n10, n6, n7);
        for (int l = 0; l < 30; ++l) {
            n2 += n3 / 5;
            transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
        }
        System.out.print("Normal interpolate\n");
        for (int n11 = 0; n11 < 10; ++n11) {
            n8 += n3;
            n9 += 10.0f;
            n10 -= 2.5f;
            System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n9, n10, n8));
            networkCharacter.updateInterpolationPoint(n8, n9, n10, n6, n7);
            for (int n12 = 0; n12 < 5; ++n12) {
                n2 += n3 / 5;
                transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
            }
        }
        System.out.print("Extrapolate\n");
        for (int n13 = 0; n13 < 20; ++n13) {
            n2 += n3;
            transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
        }
        int n14 = n8 + n3 * 20;
        float n15 = n9 + 200.0f;
        float n16 = n10 - 50.0f;
        System.out.print("Normal interpolate\n");
        for (int n17 = 0; n17 < 10; ++n17) {
            n14 += n3;
            n15 += 10.0f;
            n16 -= 2.5f;
            System.out.print(invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, n15, n16, n14));
            networkCharacter.updateInterpolationPoint(n14, n15, n16, n6, n7);
            for (int n18 = 0; n18 < 5; ++n18) {
                n2 += n3 / 5;
                transform = networkCharacter.reconstruct(n2, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out.print(invokedynamic(makeConcatWithConstants:(FFFFI)Ljava/lang/String;, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y, n2));
            }
        }
    }
    
    @Test
    public void test_case1() {
        final NetworkCharacter.Transform transform = new NetworkCharacter.Transform();
        transform.position = new Vector2();
        transform.rotation = new Vector2();
        final long[] array = { 982999607L, 982999623L, 982999640L, 982999656L, 982999674L, 982999690L, 982999706L, 982999723L, 982999740L, 982999756L, 982999773L, 982999791L, 982999807L, 982999823L, 982999840L, 982999856L, 982999872L };
        final NetworkCharacter networkCharacter = new NetworkCharacter(0.0f, 100.0f, 0L);
        NetworkCharacter.Transform transform2 = networkCharacter.transform;
        System.out.print("update x:10593.158 y:9952.486 t:982998656\n");
        System.out.print("update x:10593.23 y:9950.746 t:982999872\n");
        networkCharacter.updateInterpolationPoint(982998656, 10593.158f, 9952.486f, 0.0f, -0.0014706347f);
        networkCharacter.updateInterpolationPoint(982999872, 10593.23f, 9950.746f, 0.0f, -0.0014323471f);
        int n = (int)array[0];
        for (final long n2 : array) {
            transform2 = networkCharacter.reconstruct((int)n2, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y);
            System.out.print(invokedynamic(makeConcatWithConstants:(FFFFJJ)Ljava/lang/String;, transform2.position.x, transform2.position.y, transform2.rotation.x, transform2.rotation.y, n2, n2 - n));
            if (n2 > array[0]) {}
            transform.position.set(transform2.position);
            transform.rotation.set(transform2.rotation);
            n = (int)n2;
        }
    }
}
