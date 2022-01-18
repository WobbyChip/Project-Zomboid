// 
// Decompiled by Procyon v0.5.36
// 

package zombie.meta;

import java.util.Collection;
import zombie.iso.IsoMetaGrid;
import zombie.characters.IsoPlayer;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameServer;
import zombie.GameTime;
import zombie.network.GameClient;
import gnu.trove.set.hash.TIntHashSet;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;

public final class Meta
{
    public static final Meta instance;
    final ArrayList<IsoGridSquare> SquaresProcessing;
    private final ArrayList<IsoGridSquare> SquaresSeen;
    private final TIntHashSet SquaresSeenSet;
    
    public Meta() {
        this.SquaresProcessing = new ArrayList<IsoGridSquare>();
        this.SquaresSeen = new ArrayList<IsoGridSquare>(2000);
        this.SquaresSeenSet = new TIntHashSet();
    }
    
    public void dealWithSquareSeen(final IsoGridSquare e) {
        if (GameClient.bClient) {
            return;
        }
        if (e.hourLastSeen == (int)GameTime.getInstance().getWorldAgeHours()) {
            return;
        }
        synchronized (this.SquaresSeen) {
            if (!this.SquaresSeenSet.contains((int)e.getID())) {
                this.SquaresSeen.add(e);
                this.SquaresSeenSet.add((int)e.getID());
            }
        }
    }
    
    public void dealWithSquareSeenActual(final IsoGridSquare isoGridSquare) {
        if (GameClient.bClient) {
            return;
        }
        final IsoMetaGrid.Zone zone = isoGridSquare.zone;
        if (zone != null) {
            zone.setHourSeenToCurrent();
        }
        if (GameServer.bServer) {
            final SafeHouse safeHouse = SafeHouse.getSafeHouse(isoGridSquare);
            if (safeHouse != null) {
                safeHouse.updateSafehouse(null);
            }
        }
        isoGridSquare.setHourSeenToCurrent();
    }
    
    public void update() {
        if (GameClient.bClient) {
            return;
        }
        this.SquaresProcessing.clear();
        synchronized (this.SquaresSeen) {
            this.SquaresProcessing.addAll(this.SquaresSeen);
            this.SquaresSeen.clear();
            this.SquaresSeenSet.clear();
        }
        for (int i = 0; i < this.SquaresProcessing.size(); ++i) {
            this.dealWithSquareSeenActual(this.SquaresProcessing.get(i));
        }
        this.SquaresProcessing.clear();
    }
    
    static {
        instance = new Meta();
    }
}
