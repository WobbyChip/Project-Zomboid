// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDoor;
import java.util.ArrayList;

public final class MapKnowledge
{
    private final ArrayList<KnownBlockedEdges> knownBlockedEdges;
    
    public MapKnowledge() {
        this.knownBlockedEdges = new ArrayList<KnownBlockedEdges>();
    }
    
    public ArrayList<KnownBlockedEdges> getKnownBlockedEdges() {
        return this.knownBlockedEdges;
    }
    
    public KnownBlockedEdges getKnownBlockedEdges(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.knownBlockedEdges.size(); ++i) {
            final KnownBlockedEdges knownBlockedEdges = this.knownBlockedEdges.get(i);
            if (knownBlockedEdges.x == n && knownBlockedEdges.y == n2 && knownBlockedEdges.z == n3) {
                return knownBlockedEdges;
            }
        }
        return null;
    }
    
    private KnownBlockedEdges createKnownBlockedEdges(final int n, final int n2, final int n3) {
        assert this.getKnownBlockedEdges(n, n2, n3) == null;
        final KnownBlockedEdges alloc = KnownBlockedEdges.alloc();
        alloc.init(n, n2, n3);
        this.knownBlockedEdges.add(alloc);
        return alloc;
    }
    
    public KnownBlockedEdges getOrCreateKnownBlockedEdges(final int n, final int n2, final int n3) {
        KnownBlockedEdges knownBlockedEdges = this.getKnownBlockedEdges(n, n2, n3);
        if (knownBlockedEdges == null) {
            knownBlockedEdges = this.createKnownBlockedEdges(n, n2, n3);
        }
        return knownBlockedEdges;
    }
    
    private void releaseIfEmpty(final KnownBlockedEdges o) {
        if (!o.n && !o.w) {
            this.knownBlockedEdges.remove(o);
            o.release();
        }
    }
    
    public void setKnownBlockedEdgeW(final int n, final int n2, final int n3, final boolean w) {
        final KnownBlockedEdges orCreateKnownBlockedEdges = this.getOrCreateKnownBlockedEdges(n, n2, n3);
        orCreateKnownBlockedEdges.w = w;
        this.releaseIfEmpty(orCreateKnownBlockedEdges);
    }
    
    public void setKnownBlockedEdgeN(final int n, final int n2, final int n3, final boolean n4) {
        final KnownBlockedEdges orCreateKnownBlockedEdges = this.getOrCreateKnownBlockedEdges(n, n2, n3);
        orCreateKnownBlockedEdges.n = n4;
        this.releaseIfEmpty(orCreateKnownBlockedEdges);
    }
    
    public void setKnownBlockedDoor(final IsoDoor isoDoor, final boolean b) {
        final IsoGridSquare square = isoDoor.getSquare();
        if (isoDoor.getNorth()) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, b);
        }
        else {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, b);
        }
    }
    
    public void setKnownBlockedDoor(final IsoThumpable isoThumpable, final boolean b) {
        if (!isoThumpable.isDoor()) {
            return;
        }
        final IsoGridSquare square = isoThumpable.getSquare();
        if (isoThumpable.getNorth()) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, b);
        }
        else {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, b);
        }
    }
    
    public void setKnownBlockedWindow(final IsoWindow isoWindow, final boolean b) {
        final IsoGridSquare square = isoWindow.getSquare();
        if (isoWindow.getNorth()) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, b);
        }
        else {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, b);
        }
    }
    
    public void setKnownBlockedWindowFrame(final IsoObject isoObject, final boolean b) {
        final IsoGridSquare square = isoObject.getSquare();
        if (IsoWindowFrame.isWindowFrame(isoObject, true)) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, b);
        }
        else if (IsoWindowFrame.isWindowFrame(isoObject, false)) {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, b);
        }
    }
    
    public void forget() {
        KnownBlockedEdges.releaseAll(this.knownBlockedEdges);
        this.knownBlockedEdges.clear();
    }
}
