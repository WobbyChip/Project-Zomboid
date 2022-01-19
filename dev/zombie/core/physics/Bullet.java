// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.physics;

import zombie.iso.IsoChunk;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import java.util.ArrayList;
import zombie.network.MPStatistic;
import java.nio.ByteOrder;
import zombie.GameWindow;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import java.nio.ByteBuffer;

public class Bullet
{
    public static ByteBuffer cmdBuf;
    public static final byte TO_ADD_VEHICLE = 4;
    public static final byte TO_SCROLL_CHUNKMAP = 5;
    public static final byte TO_ACTIVATE_CHUNKMAP = 6;
    public static final byte TO_INIT_WORLD = 7;
    public static final byte TO_UPDATE_CHUNK = 8;
    public static final byte TO_DEBUG_DRAW_WORLD = 9;
    public static final byte TO_STEP_SIMULATION = 10;
    public static final byte TO_UPDATE_PLAYER_LIST = 12;
    public static final byte TO_END = -1;
    
    public static void init() {
        String s = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.bullet"))) {
            DebugLog.log("***** Loading debug version of PZBullet");
            s = "d";
        }
        String s2 = "";
        if (GameServer.bServer && GameWindow.OSValidator.isUnix()) {
            s2 = "NoOpenGL";
        }
        if (System.getProperty("os.name").contains("OS X")) {
            System.loadLibrary("PZBullet");
        }
        else if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        }
        else {
            System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        }
        (Bullet.cmdBuf = ByteBuffer.allocateDirect(4096)).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private static native void ToBullet(final ByteBuffer p0);
    
    public static void CatchToBullet(final ByteBuffer byteBuffer) {
        try {
            MPStatistic.getInstance().Bullet.Start();
            ToBullet(byteBuffer);
            MPStatistic.getInstance().Bullet.End();
        }
        catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
    
    public static native void initWorld(final int p0, final int p1, final boolean p2);
    
    public static native void destroyWorld();
    
    public static native void activateChunkMap(final int p0, final int p1, final int p2, final int p3);
    
    public static native void deactivateChunkMap(final int p0);
    
    public static void initWorld(final int n, final int n2, final int n3, final int n4, final int n5) {
        MPStatistic.getInstance().Bullet.Start();
        initWorld(n, n2, GameServer.bServer);
        activateChunkMap(0, n3, n4, n5);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public static void updatePlayerList(final ArrayList<IsoPlayer> list) {
        Bullet.cmdBuf.clear();
        Bullet.cmdBuf.put((byte)12);
        Bullet.cmdBuf.putShort((short)list.size());
        for (final IsoPlayer isoPlayer : list) {
            Bullet.cmdBuf.putInt(isoPlayer.OnlineID);
            Bullet.cmdBuf.putInt((int)isoPlayer.getX());
            Bullet.cmdBuf.putInt((int)isoPlayer.getY());
        }
        Bullet.cmdBuf.put((byte)(-1));
        Bullet.cmdBuf.put((byte)(-1));
        CatchToBullet(Bullet.cmdBuf);
    }
    
    public static void beginUpdateChunk(final IsoChunk isoChunk) {
        Bullet.cmdBuf.clear();
        Bullet.cmdBuf.put((byte)8);
        Bullet.cmdBuf.putShort((short)isoChunk.wx);
        Bullet.cmdBuf.putShort((short)isoChunk.wy);
    }
    
    public static void updateChunk(final int n, final int n2, final int n3, final int n4, final byte[] array) {
        Bullet.cmdBuf.put((byte)n);
        Bullet.cmdBuf.put((byte)n2);
        Bullet.cmdBuf.put((byte)n3);
        Bullet.cmdBuf.put((byte)n4);
        for (int i = 0; i < n4; ++i) {
            Bullet.cmdBuf.put(array[i]);
        }
    }
    
    public static void endUpdateChunk() {
        if (Bullet.cmdBuf.position() == 5) {
            return;
        }
        Bullet.cmdBuf.put((byte)(-1));
        Bullet.cmdBuf.put((byte)(-1));
        CatchToBullet(Bullet.cmdBuf);
    }
    
    public static native void scrollChunkMap(final int p0, final int p1);
    
    public static void scrollChunkMapLeft(final int n) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(n, 0);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public static void scrollChunkMapRight(final int n) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(n, 1);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public static void scrollChunkMapUp(final int n) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(n, 2);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public static void scrollChunkMapDown(final int n) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(n, 3);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public static native void addVehicle(final int p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final String p8);
    
    public static native void removeVehicle(final int p0);
    
    public static native void controlVehicle(final int p0, final float p1, final float p2, final float p3);
    
    public static native void setVehicleActive(final int p0, final boolean p1);
    
    public static native void applyCentralForceToVehicle(final int p0, final float p1, final float p2, final float p3);
    
    public static native void applyTorqueToVehicle(final int p0, final float p1, final float p2, final float p3);
    
    public static native void teleportVehicle(final int p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
    
    public static native void setTireInflation(final int p0, final int p1, final float p2);
    
    public static native void setTireRemoved(final int p0, final int p1, final boolean p2);
    
    public static native void stepSimulation(final float p0, final int p1, final float p2);
    
    public static native int getVehicleCount();
    
    public static native int getVehiclePhysics(final int p0, final float[] p1);
    
    public static native int getOwnVehiclePhysics(final int p0, final float[] p1);
    
    public static native int setOwnVehiclePhysics(final int p0, final float[] p1);
    
    public static native int setVehicleParams(final int p0, final float[] p1);
    
    public static native int setVehicleMass(final int p0, final float p1);
    
    public static native int getObjectPhysics(final float[] p0);
    
    public static native void createServerCell(final int p0, final int p1);
    
    public static native void removeServerCell(final int p0, final int p1);
    
    public static native int addPhysicsObject(final float p0, final float p1);
    
    public static native void defineVehicleScript(final String p0, final float[] p1);
    
    public static native void setVehicleVelocityMultiplier(final int p0, final float p1, final float p2);
    
    public static native int getCollisions(final int p0, final float[] p1);
    
    public static native int setVehicleStatic(final int p0, final boolean p1);
    
    public static native int addHingeConstraint(final int p0, final int p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
    
    public static native int addPointConstraint(final int p0, final int p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
    
    public static native void removeConstraint(final int p0);
}
