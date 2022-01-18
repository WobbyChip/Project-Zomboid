// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.SoundManager;
import zombie.DummySoundManager;
import org.junit.Test;
import zombie.network.ServerMap;
import zombie.core.Rand;
import java.util.HashSet;
import org.junit.Assert;

public class ZombieIDMapTest extends Assert
{
    HashSet<Short> IDs;
    
    public ZombieIDMapTest() {
        this.IDs = new HashSet<Short>();
    }
    
    @Test
    public void test10Allocations() {
        Rand.init();
        this.IDs.clear();
        for (int n = 10, i = 0; i < n; i = (short)(i + 1)) {
            System.out.println(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, ServerMap.instance.getUniqueZombieId()));
        }
    }
    
    @Test
    public void test32653Allocations() {
        Rand.init();
        this.IDs.clear();
        final int n = 34653;
        final long nanoTime = System.nanoTime();
        for (int i = 0; i < n; ++i) {
            final short uniqueZombieId = ServerMap.instance.getUniqueZombieId();
            assertFalse(this.IDs.contains(uniqueZombieId));
            this.IDs.add(uniqueZombieId);
        }
        final float n2 = (System.nanoTime() - nanoTime) / 1000000.0f;
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n2));
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n2 / n));
    }
    
    @Test
    public void test32653Adds() {
        SoundManager.instance = new DummySoundManager();
        Rand.init();
        SurvivorFactory.addMaleForename("Bob");
        SurvivorFactory.addFemaleForename("Kate");
        SurvivorFactory.addSurname("Testova");
        this.IDs.clear();
        final int n = 32653;
        final long nanoTime = System.nanoTime();
        for (int i = 0; i < n; i = (short)(i + 1)) {
            final short uniqueZombieId = ServerMap.instance.getUniqueZombieId();
            assertNull((Object)ServerMap.instance.ZombieMap.get(uniqueZombieId));
            assertFalse(this.IDs.contains(uniqueZombieId));
            ServerMap.instance.ZombieMap.put(uniqueZombieId, new IsoZombie(uniqueZombieId));
            assertEquals((long)uniqueZombieId, (long)ServerMap.instance.ZombieMap.get(uniqueZombieId).OnlineID);
            this.IDs.add(uniqueZombieId);
        }
        final float n2 = (System.nanoTime() - nanoTime) / 1000000.0f;
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n2));
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n2 / n));
    }
    
    @Test
    public void test32653Process() {
        Rand.init();
        ServerMap.instance = new ServerMap();
        SoundManager.instance = new DummySoundManager();
        SurvivorFactory.addMaleForename("Bob");
        SurvivorFactory.addFemaleForename("Kate");
        SurvivorFactory.addSurname("Testova");
        this.IDs.clear();
        final short n = 32653;
        final long nanoTime = System.nanoTime();
        for (short n2 = 0; n2 < n; ++n2) {
            assertNull((Object)ServerMap.instance.ZombieMap.get(n2));
            ServerMap.instance.ZombieMap.put(n2, new IsoZombie(n2));
            assertEquals((long)n2, (long)ServerMap.instance.ZombieMap.get(n2).OnlineID);
        }
        final long nanoTime2 = System.nanoTime();
        for (short n3 = 0; n3 < n; ++n3) {
            assertEquals((long)n3, (long)ServerMap.instance.ZombieMap.get(n3).OnlineID);
            ServerMap.instance.ZombieMap.remove(n3);
            assertNull((Object)ServerMap.instance.ZombieMap.get(n3));
        }
        final long nanoTime3 = System.nanoTime();
        for (short n4 = 0; n4 < n; ++n4) {
            assertNull((Object)ServerMap.instance.ZombieMap.get(n4));
            ServerMap.instance.ZombieMap.put(n4, new IsoZombie(n4));
            assertEquals((long)n4, (long)ServerMap.instance.ZombieMap.get(n4).OnlineID);
        }
        final long nanoTime4 = System.nanoTime();
        for (short n5 = 0; n5 < n; ++n5) {
            assertEquals((long)n5, (long)ServerMap.instance.ZombieMap.get(n5).OnlineID);
            ServerMap.instance.ZombieMap.remove(n5);
            assertNull((Object)ServerMap.instance.ZombieMap.get(n5));
        }
        final long nanoTime5 = System.nanoTime();
        for (short n6 = 0; n6 < n; ++n6) {
            assertNull((Object)ServerMap.instance.ZombieMap.get(n6));
            ServerMap.instance.ZombieMap.put(n6, new IsoZombie(n6));
            assertEquals((long)n6, (long)ServerMap.instance.ZombieMap.get(n6).OnlineID);
        }
        final long nanoTime6 = System.nanoTime();
        final float n7 = (nanoTime2 - nanoTime) / 1000000.0f;
        final float n8 = (nanoTime3 - nanoTime2) / 1000000.0f;
        final float n9 = (nanoTime4 - nanoTime3) / 1000000.0f;
        final float n10 = (nanoTime5 - nanoTime4) / 1000000.0f;
        final float n11 = (nanoTime6 - nanoTime5) / 1000000.0f;
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n7));
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n8));
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n9));
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n10));
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n11));
    }
}
