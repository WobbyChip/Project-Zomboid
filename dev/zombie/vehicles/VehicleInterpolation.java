// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.ListIterator;
import java.util.Collection;
import zombie.GameTime;
import java.nio.ByteBuffer;
import org.joml.Quaternionf;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.io.BufferedWriter;

public class VehicleInterpolation
{
    static final boolean PR = false;
    static final boolean DEBUG = false;
    BufferedWriter DebugInDataWriter;
    BufferedWriter DebugOutDataWriter;
    public int physicsDelayMs;
    public int physicsBufferMs;
    boolean buffering;
    long serverDelay;
    VehicleInterpolationData lastData;
    LinkedList<VehicleInterpolationData> dataList;
    private static final ArrayDeque<VehicleInterpolationData> pool;
    private float[] currentVehicleData;
    private float[] tempVehicleData;
    private boolean isSetCurrentVehicleData;
    private Quaternionf javaxQuat4f;
    
    VehicleInterpolation(final int n) {
        this.dataList = new LinkedList<VehicleInterpolationData>();
        this.currentVehicleData = new float[27];
        this.tempVehicleData = new float[27];
        this.isSetCurrentVehicleData = false;
        this.javaxQuat4f = new Quaternionf();
        this.physicsDelayMs = n;
        this.physicsBufferMs = n;
        this.buffering = true;
    }
    
    @Override
    protected void finalize() {
    }
    
    public void interpolationDataAdd(final ByteBuffer byteBuffer, final float x, final float y, final float z) {
        final VehicleInterpolationData vehicleInterpolationData = VehicleInterpolation.pool.isEmpty() ? new VehicleInterpolationData() : VehicleInterpolation.pool.pop();
        vehicleInterpolationData.time = byteBuffer.getLong();
        vehicleInterpolationData.x = x;
        vehicleInterpolationData.y = y;
        vehicleInterpolationData.z = z;
        vehicleInterpolationData.qx = byteBuffer.getFloat();
        vehicleInterpolationData.qy = byteBuffer.getFloat();
        vehicleInterpolationData.qz = byteBuffer.getFloat();
        vehicleInterpolationData.qw = byteBuffer.getFloat();
        vehicleInterpolationData.vx = byteBuffer.getFloat();
        vehicleInterpolationData.vy = byteBuffer.getFloat();
        vehicleInterpolationData.vz = byteBuffer.getFloat();
        vehicleInterpolationData.setNumWheels(byteBuffer.getShort());
        for (short n = 0; n < vehicleInterpolationData.w_count; ++n) {
            vehicleInterpolationData.w_st[n] = byteBuffer.getFloat();
            vehicleInterpolationData.w_rt[n] = byteBuffer.getFloat();
            vehicleInterpolationData.w_si[n] = byteBuffer.getFloat();
            vehicleInterpolationData.w_sl[n] = byteBuffer.getFloat();
        }
        final long serverDelay = GameTime.getServerTime() - vehicleInterpolationData.time;
        if (Math.abs(this.serverDelay - serverDelay) > 2000000000L) {
            this.serverDelay = serverDelay;
        }
        this.serverDelay += (long)((serverDelay - this.serverDelay) * 0.1);
        final ListIterator<VehicleInterpolationData> listIterator = (ListIterator<VehicleInterpolationData>)this.dataList.listIterator();
        long time = 0L;
        while (listIterator.hasNext()) {
            final VehicleInterpolationData e = listIterator.next();
            if (e.time > vehicleInterpolationData.time) {
                if (listIterator.hasPrevious()) {
                    listIterator.previous();
                    listIterator.add(vehicleInterpolationData);
                }
                else {
                    this.dataList.addFirst(vehicleInterpolationData);
                }
                return;
            }
            if (vehicleInterpolationData.time - e.time > (this.physicsBufferMs + this.physicsDelayMs) * 1000000) {
                VehicleInterpolation.pool.push(e);
                listIterator.remove();
            }
            else {
                if (e.time <= time) {
                    continue;
                }
                time = e.time;
            }
        }
        if (time == 0L || vehicleInterpolationData.time - time > (this.physicsBufferMs + this.physicsDelayMs) * 1000000) {
            if (!this.dataList.isEmpty()) {
                VehicleInterpolation.pool.addAll(this.dataList);
                this.dataList.clear();
            }
            this.buffering = true;
        }
        this.dataList.addLast(vehicleInterpolationData);
    }
    
    public boolean interpolationDataGet(final float[] array) {
        VehicleInterpolationData vehicleInterpolationData = null;
        VehicleInterpolationData vehicleInterpolationData2 = null;
        final long n = GameTime.getServerTime() - this.serverDelay - this.physicsDelayMs * 1000000;
        if (this.dataList.size() == 2 && this.dataList.getFirst().time == this.dataList.getLast().time) {
            this.dataList.removeFirst();
            vehicleInterpolationData2 = this.dataList.getLast();
        }
        else {
            if (this.buffering) {
                final ListIterator<VehicleInterpolationData> listIterator = (ListIterator<VehicleInterpolationData>)this.dataList.listIterator();
                long time = 0L;
                long time2 = 0L;
                while (listIterator.hasNext()) {
                    final VehicleInterpolationData vehicleInterpolationData3 = listIterator.next();
                    if (time == 0L || vehicleInterpolationData3.time < time) {
                        time = vehicleInterpolationData3.time;
                    }
                    if (vehicleInterpolationData3.time > time2) {
                        time2 = vehicleInterpolationData3.time;
                    }
                }
                if (time == 0L || time2 - time < this.physicsDelayMs * 1000000) {
                    return false;
                }
                this.buffering = false;
            }
            else if (this.dataList.size() == 0) {
                this.buffering = true;
                return false;
            }
            if (this.physicsDelayMs > 0) {
                final ListIterator<Object> listIterator2 = this.dataList.listIterator();
                while (listIterator2.hasNext()) {
                    final VehicleInterpolationData vehicleInterpolationData4 = listIterator2.next();
                    if (vehicleInterpolationData4.time >= n) {
                        vehicleInterpolationData2 = vehicleInterpolationData4;
                        if (!listIterator2.hasPrevious()) {
                            return false;
                        }
                        listIterator2.previous();
                        if (!listIterator2.hasPrevious()) {
                            return false;
                        }
                        vehicleInterpolationData = listIterator2.previous();
                        break;
                    }
                }
                while (listIterator2.hasPrevious()) {
                    VehicleInterpolation.pool.push(listIterator2.previous());
                    listIterator2.remove();
                }
            }
            else {
                vehicleInterpolationData2 = this.dataList.getFirst();
            }
            if (vehicleInterpolationData2 == null) {
                this.buffering = true;
                if (!this.dataList.isEmpty()) {
                    VehicleInterpolation.pool.addAll(this.dataList);
                    this.dataList.clear();
                }
                return false;
            }
        }
        if (vehicleInterpolationData == null) {
            int n2 = 0;
            array[n2++] = vehicleInterpolationData2.x;
            array[n2++] = vehicleInterpolationData2.y;
            array[n2++] = vehicleInterpolationData2.z;
            array[n2++] = vehicleInterpolationData2.qx;
            array[n2++] = vehicleInterpolationData2.qy;
            array[n2++] = vehicleInterpolationData2.qz;
            array[n2++] = vehicleInterpolationData2.qw;
            array[n2++] = vehicleInterpolationData2.vx;
            array[n2++] = vehicleInterpolationData2.vy;
            array[n2++] = vehicleInterpolationData2.vz;
            array[n2++] = vehicleInterpolationData2.w_count;
            for (short n3 = 0; n3 < vehicleInterpolationData2.w_count; ++n3) {
                array[n2++] = vehicleInterpolationData2.w_st[n3];
                array[n2++] = vehicleInterpolationData2.w_rt[n3];
                array[n2++] = vehicleInterpolationData2.w_si[n3];
                array[n2++] = vehicleInterpolationData2.w_sl[n3];
            }
            return true;
        }
        final float n4 = (n - vehicleInterpolationData.time) / (float)(vehicleInterpolationData2.time - vehicleInterpolationData.time);
        int n5 = 0;
        array[n5++] = (vehicleInterpolationData2.x - vehicleInterpolationData.x) * n4 + vehicleInterpolationData.x;
        array[n5++] = (vehicleInterpolationData2.y - vehicleInterpolationData.y) * n4 + vehicleInterpolationData.y;
        array[n5++] = (vehicleInterpolationData2.z - vehicleInterpolationData.z) * n4 + vehicleInterpolationData.z;
        if (vehicleInterpolationData2.qx * vehicleInterpolationData.qx + vehicleInterpolationData2.qy * vehicleInterpolationData.qy + vehicleInterpolationData2.qz * vehicleInterpolationData.qz + vehicleInterpolationData2.qw * vehicleInterpolationData.qw < 0.0f) {
            final VehicleInterpolationData vehicleInterpolationData5 = vehicleInterpolationData2;
            vehicleInterpolationData5.qx *= -1.0f;
            final VehicleInterpolationData vehicleInterpolationData6 = vehicleInterpolationData2;
            vehicleInterpolationData6.qy *= -1.0f;
            final VehicleInterpolationData vehicleInterpolationData7 = vehicleInterpolationData2;
            vehicleInterpolationData7.qz *= -1.0f;
            final VehicleInterpolationData vehicleInterpolationData8 = vehicleInterpolationData2;
            vehicleInterpolationData8.qw *= -1.0f;
        }
        array[n5++] = vehicleInterpolationData.qx * (1.0f - n4) + vehicleInterpolationData2.qx * n4;
        array[n5++] = vehicleInterpolationData.qy * (1.0f - n4) + vehicleInterpolationData2.qy * n4;
        array[n5++] = vehicleInterpolationData.qz * (1.0f - n4) + vehicleInterpolationData2.qz * n4;
        array[n5++] = vehicleInterpolationData.qw * (1.0f - n4) + vehicleInterpolationData2.qw * n4;
        array[n5++] = (vehicleInterpolationData2.vx - vehicleInterpolationData.vx) * n4 + vehicleInterpolationData.vx;
        array[n5++] = (vehicleInterpolationData2.vy - vehicleInterpolationData.vy) * n4 + vehicleInterpolationData.vy;
        array[n5++] = (vehicleInterpolationData2.vz - vehicleInterpolationData.vz) * n4 + vehicleInterpolationData.vz;
        array[n5++] = vehicleInterpolationData2.w_count;
        for (short n6 = 0; n6 < vehicleInterpolationData2.w_count; ++n6) {
            array[n5++] = (vehicleInterpolationData2.w_st[n6] - vehicleInterpolationData.w_st[n6]) * n4 + vehicleInterpolationData.w_st[n6];
            array[n5++] = (vehicleInterpolationData2.w_rt[n6] - vehicleInterpolationData.w_rt[n6]) * n4 + vehicleInterpolationData.w_rt[n6];
            array[n5++] = (vehicleInterpolationData2.w_si[n6] - vehicleInterpolationData.w_si[n6]) * n4 + vehicleInterpolationData.w_si[n6];
            array[n5++] = (vehicleInterpolationData2.w_sl[n6] - vehicleInterpolationData.w_sl[n6]) * n4 + vehicleInterpolationData.w_sl[n6];
        }
        return true;
    }
    
    public boolean interpolationDataGetPR(final float[] array) {
        return this.interpolationDataGet(array);
    }
    
    public void setVehicleData(final BaseVehicle baseVehicle) {
        if (!this.dataList.isEmpty()) {
            VehicleInterpolation.pool.addAll(this.dataList);
            this.dataList.clear();
        }
    }
    
    public void poolData() {
        if (this.dataList.isEmpty()) {
            return;
        }
        VehicleInterpolation.pool.addAll(this.dataList);
        this.dataList.clear();
    }
    
    static {
        pool = new ArrayDeque<VehicleInterpolationData>();
    }
}
