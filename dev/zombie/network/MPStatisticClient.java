// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.Arrays;
import zombie.core.network.ByteBufferWriter;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoUtils;
import zombie.characters.IsoZombie;

public class MPStatisticClient
{
    public static MPStatisticClient instance;
    private boolean needUpdate;
    private int zombiesLocalOwnership;
    private float zombiesDesyncAVG;
    private float zombiesDesyncMax;
    private int zombiesTeleports;
    private float remotePlayersDesyncAVG;
    private float remotePlayersDesyncMax;
    private int remotePlayersTeleports;
    private float FPS;
    long lastRender;
    short FPSAcc;
    private float[] fpsArray;
    private short fpsArrayCount;
    
    public MPStatisticClient() {
        this.needUpdate = true;
        this.zombiesLocalOwnership = 0;
        this.zombiesDesyncAVG = 0.0f;
        this.zombiesDesyncMax = 0.0f;
        this.zombiesTeleports = 0;
        this.remotePlayersDesyncAVG = 0.0f;
        this.remotePlayersDesyncMax = 0.0f;
        this.remotePlayersTeleports = 0;
        this.FPS = 0.0f;
        this.lastRender = System.currentTimeMillis();
        this.FPSAcc = 0;
        this.fpsArray = new float[1000];
        this.fpsArrayCount = 0;
    }
    
    public static MPStatisticClient getInstance() {
        return MPStatisticClient.instance;
    }
    
    public void incrementZombiesTeleports() {
        ++this.zombiesTeleports;
    }
    
    public void incrementRemotePlayersTeleports() {
        ++this.remotePlayersTeleports;
    }
    
    public float getFPS() {
        return this.FPS;
    }
    
    public void update() {
        if (this.needUpdate) {
            this.needUpdate = false;
            for (int i = 0; i < GameClient.IDToZombieMap.values().length; ++i) {
                final IsoZombie isoZombie = (IsoZombie)GameClient.IDToZombieMap.values()[i];
                if (!isoZombie.isRemoteZombie()) {
                    ++this.zombiesLocalOwnership;
                }
                else {
                    final float distanceTo = IsoUtils.DistanceTo(isoZombie.x, isoZombie.y, isoZombie.z, isoZombie.realx, isoZombie.realy, isoZombie.realz);
                    this.zombiesDesyncAVG += (distanceTo - this.zombiesDesyncAVG) * 0.05f;
                    if (distanceTo > this.zombiesDesyncMax) {
                        this.zombiesDesyncMax = distanceTo;
                    }
                }
            }
            for (final IsoPlayer isoPlayer : GameClient.IDToPlayerMap.values()) {
                if (isoPlayer.isLocalPlayer()) {
                    continue;
                }
                final float distanceTo2 = IsoUtils.DistanceTo(isoPlayer.x, isoPlayer.y, isoPlayer.z, isoPlayer.realx, isoPlayer.realy, isoPlayer.realz);
                this.remotePlayersDesyncAVG += (distanceTo2 - this.remotePlayersDesyncAVG) * 0.05f;
                if (distanceTo2 <= this.remotePlayersDesyncMax) {
                    continue;
                }
                this.remotePlayersDesyncMax = distanceTo2;
            }
        }
    }
    
    public void send(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(GameClient.IDToZombieMap.size());
        byteBufferWriter.putInt(this.zombiesLocalOwnership);
        byteBufferWriter.putFloat(this.zombiesDesyncAVG);
        byteBufferWriter.putFloat(this.zombiesDesyncMax);
        byteBufferWriter.putInt(this.zombiesTeleports);
        byteBufferWriter.putInt(GameClient.IDToPlayerMap.size());
        byteBufferWriter.putFloat(this.remotePlayersDesyncAVG);
        byteBufferWriter.putFloat(this.remotePlayersDesyncMax);
        byteBufferWriter.putInt(this.remotePlayersTeleports);
        float[] array = null;
        int fpsArrayCount = 0;
        synchronized (this.fpsArray) {
            array = this.fpsArray.clone();
            Arrays.fill(this.fpsArray, 0, this.fpsArrayCount, 0.0f);
            fpsArrayCount = this.fpsArrayCount;
            this.fpsArrayCount = 0;
        }
        float n = array[0];
        float n2 = array[0];
        float n3 = array[0];
        final short[] a = new short[32];
        Arrays.fill(a, (short)0);
        for (int i = 1; i < fpsArrayCount; ++i) {
            final float n4 = array[i];
            if (n > n4) {
                n = n4;
            }
            if (n3 < n4) {
                n3 = n4;
            }
            n2 += n4;
        }
        final float n5 = n2 / fpsArrayCount;
        if (n5 < n + 16.0f) {
            n = n5 - 16.0f;
        }
        if (n3 < n5 + 16.0f) {
            n3 = n5 + 16.0f;
        }
        final float n6 = (n5 - n) / (a.length / 2);
        final float n7 = (n3 - n5) / (a.length / 2);
        for (final float n8 : array) {
            if (n8 < n5) {
                final int n9 = (int)Math.ceil((n8 - n) / n6);
                final short[] array2 = a;
                final int n10 = n9;
                ++array2[n10];
            }
            if (n8 >= n5) {
                final int n11 = (int)Math.ceil((n8 - n5) / n7) + a.length / 2 - 1;
                final short[] array3 = a;
                final int n12 = n11;
                ++array3[n12];
            }
        }
        byteBufferWriter.putFloat(this.FPS);
        byteBufferWriter.putFloat(n);
        byteBufferWriter.putFloat(n5);
        byteBufferWriter.putFloat(n3);
        for (int k = 0; k < a.length; ++k) {
            byteBufferWriter.putShort(a[k]);
        }
        this.zombiesDesyncMax = 0.0f;
        this.zombiesTeleports = 0;
        this.remotePlayersDesyncMax = 0.0f;
        this.remotePlayersTeleports = 0;
        this.zombiesLocalOwnership = 0;
        this.needUpdate = true;
    }
    
    public void fpsProcess() {
        ++this.FPSAcc;
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastRender >= 1000L) {
            this.FPS = this.FPSAcc;
            this.FPSAcc = 0;
            this.lastRender = currentTimeMillis;
            if (this.fpsArrayCount < this.fpsArray.length) {
                synchronized (this.fpsArray) {
                    this.fpsArray[this.fpsArrayCount] = this.FPS;
                    ++this.fpsArrayCount;
                }
            }
        }
    }
    
    static {
        MPStatisticClient.instance = new MPStatisticClient();
    }
}
