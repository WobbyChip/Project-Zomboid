// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.Arrays;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.core.Core;

public final class VehicleIDMap
{
    public static final VehicleIDMap instance;
    private static int MAX_IDS;
    private static int RESIZE_COUNT;
    private int capacity;
    private BaseVehicle[] idToVehicle;
    private short[] freeID;
    private short freeIDSize;
    private boolean noise;
    private int warnCount;
    
    VehicleIDMap() {
        this.capacity = 256;
        this.noise = false;
        this.warnCount = 0;
        this.idToVehicle = new BaseVehicle[this.capacity];
        this.freeID = new short[this.capacity];
        for (int i = 0; i < this.capacity; ++i) {
            final short[] freeID = this.freeID;
            final short freeIDSize = this.freeIDSize;
            this.freeIDSize = (short)(freeIDSize + 1);
            freeID[freeIDSize] = (short)i;
        }
    }
    
    public void put(final short n, final BaseVehicle baseVehicle) {
        if (Core.bDebug && this.noise) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
        if (GameClient.bClient && n >= this.capacity) {
            this.resize((n / VehicleIDMap.RESIZE_COUNT + 1) * VehicleIDMap.RESIZE_COUNT);
        }
        if (n < 0 || n >= this.capacity) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(SI)Ljava/lang/String;, n, this.capacity));
        }
        if (this.idToVehicle[n] != null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
        if (baseVehicle == null) {
            throw new IllegalArgumentException("vehicle is null");
        }
        this.idToVehicle[n] = baseVehicle;
    }
    
    public void remove(final short n) {
        if (Core.bDebug && this.noise) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
        if (n < 0 || n >= this.capacity) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(SI)Ljava/lang/String;, n, this.capacity));
        }
        if (this.idToVehicle[n] == null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
        this.idToVehicle[n] = null;
        if (GameClient.bClient || GameWindow.bLoadedAsClient) {
            return;
        }
        final short[] freeID = this.freeID;
        final short freeIDSize = this.freeIDSize;
        this.freeIDSize = (short)(freeIDSize + 1);
        freeID[freeIDSize] = n;
    }
    
    public BaseVehicle get(final short n) {
        return (n >= 0 && n < this.capacity) ? this.idToVehicle[n] : null;
    }
    
    public boolean containsKey(final short n) {
        return n >= 0 && n < this.capacity && this.idToVehicle[n] != null;
    }
    
    public void toArrayList(final ArrayList<BaseVehicle> list) {
        for (int i = 0; i < this.capacity; ++i) {
            if (this.idToVehicle[i] != null) {
                list.add(this.idToVehicle[i]);
            }
        }
    }
    
    public void Reset() {
        Arrays.fill(this.idToVehicle, null);
        this.freeIDSize = (short)this.capacity;
        for (short n = 0; n < this.capacity; ++n) {
            this.freeID[n] = n;
        }
    }
    
    public short allocateID() {
        if (GameClient.bClient) {
            throw new RuntimeException("client must not call this");
        }
        if (this.freeIDSize > 0) {
            final short[] freeID = this.freeID;
            final short freeIDSize = (short)(this.freeIDSize - 1);
            this.freeIDSize = freeIDSize;
            return freeID[freeIDSize];
        }
        if (this.capacity >= VehicleIDMap.MAX_IDS) {
            if (this.warnCount < 100) {
                DebugLog.log("warning: ran out of unique vehicle ids");
                ++this.warnCount;
            }
            return -1;
        }
        this.resize(this.capacity + VehicleIDMap.RESIZE_COUNT);
        return this.allocateID();
    }
    
    private void resize(final int n) {
        final int capacity = this.capacity;
        this.capacity = Math.min(n, VehicleIDMap.MAX_IDS);
        this.capacity = Math.min(n, 32767);
        this.idToVehicle = Arrays.copyOf(this.idToVehicle, this.capacity);
        this.freeID = Arrays.copyOf(this.freeID, this.capacity);
        for (int i = capacity; i < this.capacity; ++i) {
            final short[] freeID = this.freeID;
            final short freeIDSize = this.freeIDSize;
            this.freeIDSize = (short)(freeIDSize + 1);
            freeID[freeIDSize] = (short)i;
        }
    }
    
    static {
        instance = new VehicleIDMap();
        VehicleIDMap.MAX_IDS = 32767;
        VehicleIDMap.RESIZE_COUNT = 256;
    }
}
