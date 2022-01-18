// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoUtils;
import java.util.Comparator;
import java.util.ArrayList;

public abstract class BaseZombieSoundManager
{
    protected final ArrayList<IsoZombie> characters;
    private final long[] soundTime;
    private final int staleSlotMS;
    private final Comparator<IsoZombie> comp;
    
    public BaseZombieSoundManager(final int n, final int staleSlotMS) {
        this.characters = new ArrayList<IsoZombie>();
        this.comp = new Comparator<IsoZombie>() {
            @Override
            public int compare(final IsoZombie isoZombie, final IsoZombie isoZombie2) {
                final float closestListener = BaseZombieSoundManager.this.getClosestListener(isoZombie.x, isoZombie.y, isoZombie.z);
                final float closestListener2 = BaseZombieSoundManager.this.getClosestListener(isoZombie2.x, isoZombie2.y, isoZombie2.z);
                if (closestListener > closestListener2) {
                    return 1;
                }
                if (closestListener < closestListener2) {
                    return -1;
                }
                return 0;
            }
        };
        this.soundTime = new long[n];
        this.staleSlotMS = staleSlotMS;
    }
    
    public void addCharacter(final IsoZombie isoZombie) {
        if (!this.characters.contains(isoZombie)) {
            this.characters.add(isoZombie);
        }
    }
    
    public void update() {
        if (this.characters.isEmpty()) {
            return;
        }
        this.characters.sort(this.comp);
        final long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < this.soundTime.length; ++i) {
            if (i >= this.characters.size()) {
                break;
            }
            final IsoZombie isoZombie = this.characters.get(i);
            if (isoZombie.getCurrentSquare() != null) {
                final int freeSoundSlot = this.getFreeSoundSlot(currentTimeMillis);
                if (freeSoundSlot == -1) {
                    break;
                }
                this.playSound(isoZombie);
                this.soundTime[freeSoundSlot] = currentTimeMillis;
            }
        }
        this.postUpdate();
        this.characters.clear();
    }
    
    public abstract void playSound(final IsoZombie p0);
    
    public abstract void postUpdate();
    
    private float getClosestListener(final float n, final float n2, final float n3) {
        float n4 = Float.MAX_VALUE;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && isoPlayer.getCurrentSquare() != null) {
                float distanceToSquared = IsoUtils.DistanceToSquared(isoPlayer.getX(), isoPlayer.getY(), isoPlayer.getZ() * 3.0f, n, n2, n3 * 3.0f);
                if (isoPlayer.Traits.HardOfHearing.isSet()) {
                    distanceToSquared *= 4.5f;
                }
                if (distanceToSquared < n4) {
                    n4 = distanceToSquared;
                }
            }
        }
        return n4;
    }
    
    private int getFreeSoundSlot(final long n) {
        long n2 = Long.MAX_VALUE;
        int n3 = -1;
        for (int i = 0; i < this.soundTime.length; ++i) {
            if (this.soundTime[i] < n2) {
                n2 = this.soundTime[i];
                n3 = i;
            }
        }
        if (n - n2 < this.staleSlotMS) {
            return -1;
        }
        return n3;
    }
}
