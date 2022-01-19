// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.data;

import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import java.util.Arrays;
import zombie.debug.DebugLog;
import java.nio.ByteBuffer;
import zombie.iso.areas.isoregion.IsoRegions;
import java.util.HashSet;
import java.util.ArrayDeque;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import java.util.ArrayList;

public final class DataChunk
{
    private final DataCell cell;
    private final int hashId;
    private final int chunkX;
    private final int chunkY;
    protected int highestZ;
    protected long lastUpdateStamp;
    private final boolean[] activeZLayers;
    private final boolean[] dirtyZLayers;
    private byte[] squareFlags;
    private byte[] regionIDs;
    private final ArrayList<ArrayList<IsoChunkRegion>> chunkRegions;
    private static byte selectedFlags;
    private static final ArrayDeque<DataSquarePos> tmpSquares;
    private static final HashSet<Integer> tmpLinkedChunks;
    private static final boolean[] exploredPositions;
    private static IsoChunkRegion lastCurRegion;
    private static IsoChunkRegion lastOtherRegionFullConnect;
    private static ArrayList<IsoChunkRegion> oldList;
    private static final ArrayDeque<IsoChunkRegion> chunkQueue;
    
    protected DataChunk(final int chunkX, final int chunkY, final DataCell cell, final int n) {
        this.highestZ = 0;
        this.lastUpdateStamp = 0L;
        this.activeZLayers = new boolean[8];
        this.dirtyZLayers = new boolean[8];
        this.chunkRegions = new ArrayList<ArrayList<IsoChunkRegion>>(8);
        this.cell = cell;
        this.hashId = ((n < 0) ? IsoRegions.hash(chunkX, chunkY) : n);
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        for (int i = 0; i < 8; ++i) {
            this.chunkRegions.add(new ArrayList<IsoChunkRegion>());
        }
    }
    
    protected int getHashId() {
        return this.hashId;
    }
    
    public int getChunkX() {
        return this.chunkX;
    }
    
    public int getChunkY() {
        return this.chunkY;
    }
    
    protected ArrayList<IsoChunkRegion> getChunkRegions(final int index) {
        return this.chunkRegions.get(index);
    }
    
    public long getLastUpdateStamp() {
        return this.lastUpdateStamp;
    }
    
    public void setLastUpdateStamp(final long lastUpdateStamp) {
        this.lastUpdateStamp = lastUpdateStamp;
    }
    
    protected boolean isDirty(final int n) {
        return this.activeZLayers[n] && this.dirtyZLayers[n];
    }
    
    protected void setDirty(final int n) {
        if (this.activeZLayers[n]) {
            this.dirtyZLayers[n] = true;
            this.cell.dataRoot.EnqueueDirtyDataChunk(this);
        }
    }
    
    public void setDirtyAllActive() {
        int n = 0;
        for (int i = 0; i < 8; ++i) {
            if (this.activeZLayers[i]) {
                this.dirtyZLayers[i] = true;
                if (n == 0) {
                    this.cell.dataRoot.EnqueueDirtyDataChunk(this);
                    n = 1;
                }
            }
        }
    }
    
    protected void unsetDirtyAll() {
        for (int i = 0; i < 8; ++i) {
            this.dirtyZLayers[i] = false;
        }
    }
    
    private boolean validCoords(final int n, final int n2, final int n3) {
        return n >= 0 && n < 10 && n2 >= 0 && n2 < 10 && n3 >= 0 && n3 < this.highestZ + 1;
    }
    
    private int getCoord1D(final int n, final int n2, final int n3) {
        return n3 * 10 * 10 + n2 * 10 + n;
    }
    
    public byte getSquare(final int n, final int n2, final int n3) {
        return this.getSquare(n, n2, n3, false);
    }
    
    public byte getSquare(final int n, final int n2, final int n3, final boolean b) {
        if (this.squareFlags == null || (!b && !this.validCoords(n, n2, n3))) {
            return -1;
        }
        if (this.activeZLayers[n3]) {
            return this.squareFlags[this.getCoord1D(n, n2, n3)];
        }
        return -1;
    }
    
    protected byte setOrAddSquare(final int n, final int n2, final int n3, final byte b) {
        return this.setOrAddSquare(n, n2, n3, b, false);
    }
    
    protected byte setOrAddSquare(final int n, final int n2, final int dirty, final byte b, final boolean b2) {
        if (b2 || this.validCoords(n, n2, dirty)) {
            this.ensureSquares(dirty);
            final int coord1D = this.getCoord1D(n, n2, dirty);
            if (this.squareFlags[coord1D] != b) {
                this.setDirty(dirty);
            }
            return this.squareFlags[coord1D] = b;
        }
        return -1;
    }
    
    private void ensureSquares(final int highestZ) {
        if (highestZ < 0 || highestZ >= 8) {
            return;
        }
        if (!this.activeZLayers[highestZ]) {
            this.ensureSquareArray(highestZ);
            this.activeZLayers[highestZ] = true;
            if (highestZ > this.highestZ) {
                this.highestZ = highestZ;
            }
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    this.squareFlags[this.getCoord1D(j, i, highestZ)] = (byte)((highestZ == 0) ? 16 : 0);
                }
            }
        }
    }
    
    private void ensureSquareArray(final int n) {
        final int n2 = (n + 1) * 10 * 10;
        if (this.squareFlags == null || this.squareFlags.length < n2) {
            final byte[] squareFlags = this.squareFlags;
            final byte[] regionIDs = this.regionIDs;
            this.squareFlags = new byte[n2];
            this.regionIDs = new byte[n2];
            if (squareFlags != null) {
                for (int i = 0; i < squareFlags.length; ++i) {
                    this.squareFlags[i] = squareFlags[i];
                    this.regionIDs[i] = regionIDs[i];
                }
            }
        }
    }
    
    public void save(final ByteBuffer byteBuffer) {
        try {
            final int position = byteBuffer.position();
            byteBuffer.putInt(0);
            byteBuffer.putInt(this.highestZ);
            final int n = (this.highestZ + 1) * 100;
            byteBuffer.putInt(n);
            for (int i = 0; i < n; ++i) {
                byteBuffer.put(this.squareFlags[i]);
            }
            final int position2 = byteBuffer.position();
            byteBuffer.position(position);
            byteBuffer.putInt(position2 - position);
            byteBuffer.position(position2);
        }
        catch (Exception ex) {
            DebugLog.log(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) {
        try {
            if (b) {
                byteBuffer.getInt();
            }
            this.highestZ = byteBuffer.getInt();
            for (int i = this.highestZ; i >= 0; --i) {
                this.ensureSquares(i);
            }
            for (int int1 = byteBuffer.getInt(), j = 0; j < int1; ++j) {
                this.squareFlags[j] = byteBuffer.get();
            }
        }
        catch (Exception ex) {
            DebugLog.log(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void setSelectedFlags(final int n, final int n2, final int n3) {
        if (n3 >= 0 && n3 <= this.highestZ) {
            DataChunk.selectedFlags = this.squareFlags[this.getCoord1D(n, n2, n3)];
        }
        else {
            DataChunk.selectedFlags = -1;
        }
    }
    
    public boolean selectedHasFlags(final byte b) {
        return (DataChunk.selectedFlags & b) == b;
    }
    
    protected boolean squareHasFlags(final int n, final int n2, final int n3, final byte b) {
        return this.squareHasFlags(this.getCoord1D(n, n2, n3), b);
    }
    
    private boolean squareHasFlags(final int n, final byte b) {
        return (this.squareFlags[n] & b) == b;
    }
    
    public byte squareGetFlags(final int n, final int n2, final int n3) {
        return this.squareGetFlags(this.getCoord1D(n, n2, n3));
    }
    
    private byte squareGetFlags(final int n) {
        return this.squareFlags[n];
    }
    
    protected void squareAddFlags(final int n, final int n2, final int n3, final byte b) {
        this.squareAddFlags(this.getCoord1D(n, n2, n3), b);
    }
    
    private void squareAddFlags(final int n, final byte b) {
        final byte[] squareFlags = this.squareFlags;
        squareFlags[n] |= b;
    }
    
    protected void squareRemoveFlags(final int n, final int n2, final int n3, final byte b) {
        this.squareRemoveFlags(this.getCoord1D(n, n2, n3), b);
    }
    
    private void squareRemoveFlags(final int n, final byte b) {
        final byte[] squareFlags = this.squareFlags;
        squareFlags[n] ^= b;
    }
    
    protected boolean squareCanConnect(final int n, final int n2, final int n3, final byte b) {
        return this.squareCanConnect(this.getCoord1D(n, n2, n3), n3, b);
    }
    
    private boolean squareCanConnect(final int n, final int n2, final byte b) {
        if (n2 >= 0 && n2 < this.highestZ + 1) {
            if (b == 0) {
                return !this.squareHasFlags(n, (byte)1);
            }
            if (b == 1) {
                return !this.squareHasFlags(n, (byte)2);
            }
            if (b == 2) {
                return true;
            }
            if (b == 3) {
                return true;
            }
            if (b == 4) {
                return !this.squareHasFlags(n, (byte)64);
            }
            if (b == 5) {
                return !this.squareHasFlags(n, (byte)16);
            }
        }
        return false;
    }
    
    public IsoChunkRegion getIsoChunkRegion(final int n, final int n2, final int n3) {
        return this.getIsoChunkRegion(this.getCoord1D(n, n2, n3), n3);
    }
    
    private IsoChunkRegion getIsoChunkRegion(final int n, final int n2) {
        if (n2 >= 0 && n2 < this.highestZ + 1) {
            final byte index = this.regionIDs[n];
            if (index >= 0 && index < this.chunkRegions.get(n2).size()) {
                return this.chunkRegions.get(n2).get(index);
            }
        }
        return null;
    }
    
    public void setRegion(final int n, final int n2, final int n3, final byte b) {
        this.regionIDs[this.getCoord1D(n, n2, n3)] = b;
    }
    
    protected void recalculate() {
        for (int i = 0; i <= this.highestZ; ++i) {
            if (this.dirtyZLayers[i]) {
                if (this.activeZLayers[i]) {
                    this.recalculate(i);
                }
            }
        }
    }
    
    private void recalculate(final int index) {
        final ArrayList<IsoChunkRegion> list = this.chunkRegions.get(index);
        for (int i = list.size() - 1; i >= 0; --i) {
            final IsoChunkRegion isoChunkRegion = list.get(i);
            final IsoWorldRegion unlinkFromIsoWorldRegion = isoChunkRegion.unlinkFromIsoWorldRegion();
            if (unlinkFromIsoWorldRegion != null && unlinkFromIsoWorldRegion.size() <= 0) {
                this.cell.dataRoot.regionManager.releaseIsoWorldRegion(unlinkFromIsoWorldRegion);
            }
            this.cell.dataRoot.regionManager.releaseIsoChunkRegion(isoChunkRegion);
            list.remove(i);
        }
        list.clear();
        final int n = 100;
        Arrays.fill(this.regionIDs, index * n, index * n + n, (byte)(-1));
        for (int j = 0; j < 10; ++j) {
            for (int k = 0; k < 10; ++k) {
                if (this.regionIDs[this.getCoord1D(k, j, index)] == -1) {
                    this.floodFill(k, j, index);
                }
            }
        }
    }
    
    private IsoChunkRegion floodFill(final int n, final int n2, final int n3) {
        final IsoChunkRegion allocIsoChunkRegion = this.cell.dataRoot.regionManager.allocIsoChunkRegion(n3);
        final byte b = (byte)this.chunkRegions.get(n3).size();
        this.chunkRegions.get(n3).add(allocIsoChunkRegion);
        this.clearExploredPositions();
        DataChunk.tmpSquares.clear();
        DataChunk.tmpLinkedChunks.clear();
        DataChunk.tmpSquares.add(DataSquarePos.alloc(n, n2, n3));
        DataSquarePos dataSquarePos;
        while ((dataSquarePos = DataChunk.tmpSquares.poll()) != null) {
            final int coord1D = this.getCoord1D(dataSquarePos.x, dataSquarePos.y, dataSquarePos.z);
            this.setExploredPosition(coord1D, dataSquarePos.z);
            if (this.regionIDs[coord1D] == -1) {
                this.regionIDs[coord1D] = b;
                allocIsoChunkRegion.addSquareCount();
                for (byte b2 = 0; b2 < 4; ++b2) {
                    final DataSquarePos neighbor = this.getNeighbor(dataSquarePos, b2);
                    if (neighbor != null) {
                        final int coord1D2 = this.getCoord1D(neighbor.x, neighbor.y, neighbor.z);
                        if (this.isExploredPosition(coord1D2, neighbor.z)) {
                            DataSquarePos.release(neighbor);
                        }
                        else {
                            if (this.squareCanConnect(coord1D, dataSquarePos.z, b2) && this.squareCanConnect(coord1D2, neighbor.z, IsoRegions.GetOppositeDir(b2))) {
                                if (this.regionIDs[coord1D2] == -1) {
                                    DataChunk.tmpSquares.add(neighbor);
                                    this.setExploredPosition(coord1D2, neighbor.z);
                                    continue;
                                }
                            }
                            else {
                                final IsoChunkRegion isoChunkRegion = this.getIsoChunkRegion(coord1D2, neighbor.z);
                                if (isoChunkRegion != null && isoChunkRegion != allocIsoChunkRegion) {
                                    if (!DataChunk.tmpLinkedChunks.contains(isoChunkRegion.getID())) {
                                        allocIsoChunkRegion.addNeighbor(isoChunkRegion);
                                        isoChunkRegion.addNeighbor(allocIsoChunkRegion);
                                        DataChunk.tmpLinkedChunks.add(isoChunkRegion.getID());
                                    }
                                    this.setExploredPosition(coord1D2, neighbor.z);
                                    DataSquarePos.release(neighbor);
                                    continue;
                                }
                            }
                            DataSquarePos.release(neighbor);
                        }
                    }
                    else if (this.squareCanConnect(coord1D, dataSquarePos.z, b2)) {
                        allocIsoChunkRegion.addChunkBorderSquaresCnt();
                    }
                }
            }
        }
        return allocIsoChunkRegion;
    }
    
    private boolean isExploredPosition(final int n, final int n2) {
        return DataChunk.exploredPositions[n - n2 * 10 * 10];
    }
    
    private void setExploredPosition(final int n, final int n2) {
        DataChunk.exploredPositions[n - n2 * 10 * 10] = true;
    }
    
    private void clearExploredPositions() {
        Arrays.fill(DataChunk.exploredPositions, false);
    }
    
    private DataSquarePos getNeighbor(final DataSquarePos dataSquarePos, final byte b) {
        int x = dataSquarePos.x;
        int y = dataSquarePos.y;
        if (b == 1) {
            x = dataSquarePos.x - 1;
        }
        else if (b == 3) {
            x = dataSquarePos.x + 1;
        }
        if (b == 0) {
            y = dataSquarePos.y - 1;
        }
        else if (b == 2) {
            y = dataSquarePos.y + 1;
        }
        if (x < 0 || x >= 10 || y < 0 || y >= 10) {
            return null;
        }
        return DataSquarePos.alloc(x, y, dataSquarePos.z);
    }
    
    protected void link(final DataChunk dataChunk, final DataChunk dataChunk2, final DataChunk dataChunk3, final DataChunk dataChunk4) {
        for (int i = 0; i <= this.highestZ; ++i) {
            if (this.dirtyZLayers[i]) {
                if (this.activeZLayers[i]) {
                    this.linkRegionsOnSide(i, dataChunk, (byte)0);
                    this.linkRegionsOnSide(i, dataChunk2, (byte)1);
                    this.linkRegionsOnSide(i, dataChunk3, (byte)2);
                    this.linkRegionsOnSide(i, dataChunk4, (byte)3);
                }
            }
        }
    }
    
    private void linkRegionsOnSide(final int n, final DataChunk dataChunk, final byte b) {
        int n2;
        int n3;
        int n4;
        int n5;
        if (b == 0 || b == 2) {
            n2 = 0;
            n3 = 10;
            n4 = ((b == 0) ? 0 : 9);
            n5 = n4 + 1;
        }
        else {
            n2 = ((b == 1) ? 0 : 9);
            n3 = n2 + 1;
            n4 = 0;
            n5 = 10;
        }
        if (dataChunk != null && dataChunk.isDirty(n)) {
            dataChunk.resetEnclosedSide(n, IsoRegions.GetOppositeDir(b));
        }
        DataChunk.lastCurRegion = null;
        DataChunk.lastOtherRegionFullConnect = null;
        for (int i = n4; i < n5; ++i) {
            for (int j = n2; j < n3; ++j) {
                int n6;
                int n7;
                if (b == 0 || b == 2) {
                    n6 = j;
                    n7 = ((b == 0) ? 9 : 0);
                }
                else {
                    n6 = ((b == 1) ? 9 : 0);
                    n7 = i;
                }
                final int coord1D = this.getCoord1D(j, i, n);
                final int coord1D2 = this.getCoord1D(n6, n7, n);
                final IsoChunkRegion isoChunkRegion = this.getIsoChunkRegion(coord1D, n);
                final IsoChunkRegion lastOtherRegionFullConnect = (dataChunk != null) ? dataChunk.getIsoChunkRegion(coord1D2, n) : null;
                if (isoChunkRegion == null) {
                    IsoRegions.warn("ds.getRegion()==null, shouldnt happen at this point.");
                }
                else {
                    if (DataChunk.lastCurRegion != null && DataChunk.lastCurRegion != isoChunkRegion) {
                        DataChunk.lastOtherRegionFullConnect = null;
                    }
                    if (DataChunk.lastCurRegion == null || DataChunk.lastCurRegion != isoChunkRegion || lastOtherRegionFullConnect == null || DataChunk.lastOtherRegionFullConnect != lastOtherRegionFullConnect) {
                        if (dataChunk == null || lastOtherRegionFullConnect == null) {
                            if (this.squareCanConnect(coord1D, n, b)) {
                                isoChunkRegion.setEnclosed(b, false);
                            }
                        }
                        else if (this.squareCanConnect(coord1D, n, b) && dataChunk.squareCanConnect(coord1D2, n, IsoRegions.GetOppositeDir(b))) {
                            isoChunkRegion.addConnectedNeighbor(lastOtherRegionFullConnect);
                            lastOtherRegionFullConnect.addConnectedNeighbor(isoChunkRegion);
                            isoChunkRegion.addNeighbor(lastOtherRegionFullConnect);
                            lastOtherRegionFullConnect.addNeighbor(isoChunkRegion);
                            if (!lastOtherRegionFullConnect.getIsEnclosed()) {
                                lastOtherRegionFullConnect.setEnclosed(IsoRegions.GetOppositeDir(b), true);
                            }
                            DataChunk.lastOtherRegionFullConnect = lastOtherRegionFullConnect;
                        }
                        else {
                            isoChunkRegion.addNeighbor(lastOtherRegionFullConnect);
                            lastOtherRegionFullConnect.addNeighbor(isoChunkRegion);
                            if (!lastOtherRegionFullConnect.getIsEnclosed()) {
                                lastOtherRegionFullConnect.setEnclosed(IsoRegions.GetOppositeDir(b), true);
                            }
                            DataChunk.lastOtherRegionFullConnect = null;
                        }
                        DataChunk.lastCurRegion = isoChunkRegion;
                    }
                }
            }
        }
    }
    
    private void resetEnclosedSide(final int index, final byte b) {
        final ArrayList<IsoChunkRegion> list = this.chunkRegions.get(index);
        for (int i = 0; i < list.size(); ++i) {
            final IsoChunkRegion isoChunkRegion = list.get(i);
            if (isoChunkRegion.getzLayer() == index) {
                isoChunkRegion.setEnclosed(b, true);
            }
        }
    }
    
    protected void interConnect() {
        for (int i = 0; i <= this.highestZ; ++i) {
            if (this.dirtyZLayers[i]) {
                if (this.activeZLayers[i]) {
                    final ArrayList<IsoChunkRegion> list = this.chunkRegions.get(i);
                    for (int j = 0; j < list.size(); ++j) {
                        final IsoChunkRegion isoChunkRegion = list.get(j);
                        if (isoChunkRegion.getzLayer() == i && isoChunkRegion.getIsoWorldRegion() == null) {
                            if (isoChunkRegion.getConnectedNeighbors().size() == 0) {
                                final IsoWorldRegion allocIsoWorldRegion = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(allocIsoWorldRegion);
                                allocIsoWorldRegion.addIsoChunkRegion(isoChunkRegion);
                            }
                            else {
                                final IsoChunkRegion connectedNeighborWithLargestIsoWorldRegion = isoChunkRegion.getConnectedNeighborWithLargestIsoWorldRegion();
                                if (connectedNeighborWithLargestIsoWorldRegion != null) {
                                    final IsoWorldRegion isoWorldRegion = connectedNeighborWithLargestIsoWorldRegion.getIsoWorldRegion();
                                    DataChunk.oldList.clear();
                                    DataChunk.oldList = isoWorldRegion.swapIsoChunkRegions(DataChunk.oldList);
                                    for (int k = 0; k < DataChunk.oldList.size(); ++k) {
                                        DataChunk.oldList.get(k).setIsoWorldRegion(null);
                                    }
                                    this.cell.dataRoot.regionManager.releaseIsoWorldRegion(isoWorldRegion);
                                    final IsoWorldRegion allocIsoWorldRegion2 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                    this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(allocIsoWorldRegion2);
                                    this.floodFillExpandWorldRegion(isoChunkRegion, allocIsoWorldRegion2);
                                    for (int l = 0; l < DataChunk.oldList.size(); ++l) {
                                        final IsoChunkRegion isoChunkRegion2 = DataChunk.oldList.get(l);
                                        if (isoChunkRegion2.getIsoWorldRegion() == null) {
                                            final IsoWorldRegion allocIsoWorldRegion3 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                            this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(allocIsoWorldRegion3);
                                            this.floodFillExpandWorldRegion(isoChunkRegion2, allocIsoWorldRegion3);
                                        }
                                    }
                                    ++DataRoot.floodFills;
                                }
                                else {
                                    final IsoWorldRegion allocIsoWorldRegion4 = this.cell.dataRoot.regionManager.allocIsoWorldRegion();
                                    this.cell.dataRoot.EnqueueDirtyIsoWorldRegion(allocIsoWorldRegion4);
                                    this.floodFillExpandWorldRegion(isoChunkRegion, allocIsoWorldRegion4);
                                    ++DataRoot.floodFills;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void floodFillExpandWorldRegion(final IsoChunkRegion e, final IsoWorldRegion isoWorldRegion) {
        DataChunk.chunkQueue.add(e);
        IsoChunkRegion isoChunkRegion;
        while ((isoChunkRegion = DataChunk.chunkQueue.poll()) != null) {
            isoWorldRegion.addIsoChunkRegion(isoChunkRegion);
            if (isoChunkRegion.getConnectedNeighbors().size() == 0) {
                continue;
            }
            for (int i = 0; i < isoChunkRegion.getConnectedNeighbors().size(); ++i) {
                final IsoChunkRegion isoChunkRegion2 = isoChunkRegion.getConnectedNeighbors().get(i);
                if (!DataChunk.chunkQueue.contains(isoChunkRegion2)) {
                    if (isoChunkRegion2.getIsoWorldRegion() == null) {
                        DataChunk.chunkQueue.add(isoChunkRegion2);
                    }
                    else if (isoChunkRegion2.getIsoWorldRegion() != isoWorldRegion) {
                        isoWorldRegion.merge(isoChunkRegion2.getIsoWorldRegion());
                    }
                }
            }
        }
    }
    
    protected void recalcRoofs() {
        if (this.highestZ < 1) {
            return;
        }
        for (int i = 0; i < this.chunkRegions.size(); ++i) {
            for (int j = 0; j < this.chunkRegions.get(i).size(); ++j) {
                this.chunkRegions.get(i).get(j).resetRoofCnt();
            }
        }
        final int highestZ = this.highestZ;
        for (int k = 0; k < 10; ++k) {
            for (int l = 0; l < 10; ++l) {
                final byte square = this.getSquare(l, k, highestZ);
                int n = 0;
                if (square > 0) {
                    n = (this.squareHasFlags(l, k, highestZ, (byte)16) ? 1 : 0);
                }
                if (highestZ >= 1) {
                    for (int n2 = highestZ - 1; n2 >= 0; --n2) {
                        if (this.getSquare(l, k, n2) > 0) {
                            n = ((n != 0 || this.squareHasFlags(l, k, n2, (byte)32)) ? 1 : 0);
                            if (n != 0) {
                                final IsoChunkRegion isoChunkRegion = this.getIsoChunkRegion(l, k, n2);
                                if (isoChunkRegion != null) {
                                    isoChunkRegion.addRoof();
                                    if (isoChunkRegion.getIsoWorldRegion() != null && !isoChunkRegion.getIsoWorldRegion().isEnclosed()) {
                                        n = 0;
                                    }
                                }
                                else {
                                    n = 0;
                                }
                            }
                            if (n == 0) {
                                n = (this.squareHasFlags(l, k, n2, (byte)16) ? 1 : 0);
                            }
                        }
                        else {
                            n = 0;
                        }
                    }
                }
            }
        }
    }
    
    static {
        tmpSquares = new ArrayDeque<DataSquarePos>();
        tmpLinkedChunks = new HashSet<Integer>();
        exploredPositions = new boolean[100];
        DataChunk.oldList = new ArrayList<IsoChunkRegion>();
        chunkQueue = new ArrayDeque<IsoChunkRegion>();
    }
}
