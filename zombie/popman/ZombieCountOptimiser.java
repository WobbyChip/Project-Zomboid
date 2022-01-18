// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.network.MPStatistics;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.characters.IsoZombie;
import java.util.ArrayList;

public class ZombieCountOptimiser
{
    private static int zombieCountForDelete;
    public static final int maxZombieCount = 500;
    public static final int minZombieDistance = 20;
    public static final ArrayList<IsoZombie> zombiesForDelete;
    
    public static void startCount() {
        ZombieCountOptimiser.zombieCountForDelete = (int)(1.0f * Math.max(0, GameClient.IDToZombieMap.values().length - 500));
    }
    
    public static void incrementZombie(final IsoZombie e) {
        if (ZombieCountOptimiser.zombieCountForDelete > 0 && Rand.Next(10) == 0 && e.canBeDeletedUnnoticed(20.0f) && !e.isReanimatedPlayer()) {
            synchronized (ZombieCountOptimiser.zombiesForDelete) {
                ZombieCountOptimiser.zombiesForDelete.add(e);
            }
            --ZombieCountOptimiser.zombieCountForDelete;
            MPStatistics.clientZombieCulled();
        }
    }
    
    static {
        ZombieCountOptimiser.zombieCountForDelete = 0;
        zombiesForDelete = new ArrayList<IsoZombie>();
    }
}
