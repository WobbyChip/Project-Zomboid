// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclesDB2;
import zombie.core.Core;
import zombie.debug.DebugLog;
import java.io.IOException;
import java.util.Iterator;
import zombie.network.GameServer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;

public class ChunkSaveWorker
{
    public static ChunkSaveWorker instance;
    private final ArrayList<IsoChunk> tempList;
    public ConcurrentLinkedQueue<IsoChunk> toSaveQueue;
    public boolean bSaving;
    
    public ChunkSaveWorker() {
        this.tempList = new ArrayList<IsoChunk>();
        this.toSaveQueue = new ConcurrentLinkedQueue<IsoChunk>();
    }
    
    public void Update(final IsoChunk isoChunk) {
        if (GameServer.bServer) {
            return;
        }
        IsoChunk o = null;
        if (!(this.bSaving = !this.toSaveQueue.isEmpty())) {
            return;
        }
        if (isoChunk != null) {
            for (final IsoChunk isoChunk2 : this.toSaveQueue) {
                if (isoChunk2.wx == isoChunk.wx && isoChunk2.wy == isoChunk.wy) {
                    o = isoChunk2;
                    break;
                }
            }
        }
        if (o == null) {
            o = this.toSaveQueue.poll();
        }
        else {
            this.toSaveQueue.remove(o);
        }
        if (o == null) {
            return;
        }
        try {
            o.Save(false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void SaveNow(final ArrayList<IsoChunk> list) {
        this.tempList.clear();
        for (IsoChunk e = this.toSaveQueue.poll(); e != null; e = this.toSaveQueue.poll()) {
            boolean b = false;
            for (int i = 0; i < list.size(); ++i) {
                final IsoChunk isoChunk = list.get(i);
                if (e.wx == isoChunk.wx && e.wy == isoChunk.wy) {
                    try {
                        e.Save(false);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    b = true;
                    break;
                }
            }
            if (!b) {
                this.tempList.add(e);
            }
        }
        for (int j = 0; j < this.tempList.size(); ++j) {
            this.toSaveQueue.add(this.tempList.get(j));
        }
        this.tempList.clear();
    }
    
    public void SaveNow() {
        DebugLog.log("EXITDEBUG: ChunkSaveWorker.SaveNow 1");
        for (IsoChunk isoChunk = this.toSaveQueue.poll(); isoChunk != null; isoChunk = this.toSaveQueue.poll()) {
            try {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoChunk.wx, isoChunk.wy));
                isoChunk.Save(false);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.bSaving = false;
        DebugLog.log("EXITDEBUG: ChunkSaveWorker.SaveNow 3");
    }
    
    public void Add(final IsoChunk isoChunk) {
        if (Core.getInstance().isNoSave()) {
            for (int i = 0; i < isoChunk.vehicles.size(); ++i) {
                VehiclesDB2.instance.updateVehicle(isoChunk.vehicles.get(i));
            }
        }
        if (!this.toSaveQueue.contains(isoChunk)) {
            this.toSaveQueue.add(isoChunk);
        }
    }
    
    static {
        ChunkSaveWorker.instance = new ChunkSaveWorker();
    }
}
