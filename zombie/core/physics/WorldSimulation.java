// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.physics;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.textures.TextureDraw;
import zombie.core.network.ByteBufferWriter;
import zombie.network.ServerMap;
import org.joml.Vector3fc;
import zombie.characters.IsoGameCharacter;
import zombie.vehicles.VehicleManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.network.MPStatistic;
import zombie.iso.IsoChunkMap;
import zombie.GameTime;
import zombie.iso.IsoWorld;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import zombie.vehicles.BaseVehicle;
import java.util.ArrayList;
import java.io.BufferedWriter;
import zombie.iso.IsoMovingObject;
import java.util.HashMap;

public final class WorldSimulation
{
    public static WorldSimulation instance;
    public static final boolean LEVEL_ZERO_ONLY = true;
    public float offsetX;
    public float offsetY;
    public int maxSubSteps;
    public boolean created;
    public long time;
    public HashMap<Integer, IsoMovingObject> physicsObjectMap;
    int count;
    static final boolean DEBUG = false;
    BufferedWriter DebugInDataWriter;
    BufferedWriter DebugOutDataWriter;
    private final float[] ff;
    private final float[] wheelSteer;
    private final float[] wheelRotation;
    private final float[] wheelSkidInfo;
    private final float[] wheelSuspensionLength;
    ArrayList<BaseVehicle> collideVehicles;
    protected Transform tempTransform;
    protected Quaternionf javaxQuat4f;
    private Vector3f tempVector3f;
    private Vector3f tempVector3f_2;
    
    public WorldSimulation() {
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.created = false;
        this.physicsObjectMap = new HashMap<Integer, IsoMovingObject>();
        this.count = 0;
        this.ff = new float[8192];
        this.wheelSteer = new float[4];
        this.wheelRotation = new float[4];
        this.wheelSkidInfo = new float[4];
        this.wheelSuspensionLength = new float[4];
        this.collideVehicles = new ArrayList<BaseVehicle>(4);
        this.tempTransform = new Transform();
        this.javaxQuat4f = new Quaternionf();
        this.tempVector3f = new Vector3f();
        this.tempVector3f_2 = new Vector3f();
    }
    
    public void create() {
        if (this.created) {
            return;
        }
        this.offsetX = (float)(IsoWorld.instance.MetaGrid.getMinX() * 300);
        this.offsetY = (float)(IsoWorld.instance.MetaGrid.getMinY() * 300);
        this.time = GameTime.getServerTime();
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
        Bullet.initWorld((int)this.offsetX, (int)this.offsetY, isoChunkMap.getWorldXMin(), isoChunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth);
        for (int i = 0; i < 4; ++i) {
            this.wheelSteer[i] = 0.0f;
            this.wheelRotation[i] = 0.0f;
            this.wheelSkidInfo[i] = 0.0f;
            this.wheelSuspensionLength[i] = 0.0f;
        }
        this.created = true;
    }
    
    public void destroy() {
        Bullet.destroyWorld();
    }
    
    private void updatePhysic(final float n) {
        MPStatistic.getInstance().Bullet.Start();
        Bullet.stepSimulation(n, 2, 0.016666668f);
        MPStatistic.getInstance().Bullet.End();
        this.time = GameTime.getServerTime();
    }
    
    public void update() {
        s_performance.worldSimulationUpdate.invokeAndMeasure(this, WorldSimulation::updateInternal);
    }
    
    private void updateInternal() {
        if (!this.created) {
            return;
        }
        this.updatePhysic(GameTime.instance.getRealworldSecondsSinceLastUpdate());
        this.collideVehicles.clear();
        IsoMovingObject vehicle = null;
        final IsoPlayer isoPlayer = IsoPlayer.players[IsoPlayer.getPlayerIndex()];
        if (isoPlayer != null) {
            vehicle = isoPlayer.getVehicle();
        }
        final int vehicleCount = Bullet.getVehicleCount();
        int i = 0;
        while (i < vehicleCount) {
            MPStatistic.getInstance().Bullet.Start();
            final int vehiclePhysics = Bullet.getVehiclePhysics(i, this.ff);
            MPStatistic.getInstance().Bullet.End();
            if (vehiclePhysics <= 0) {
                break;
            }
            i += vehiclePhysics;
            int n = 0;
            for (int j = 0; j < vehiclePhysics; ++j) {
                final int n2 = (int)this.ff[n++];
                final float n3 = this.ff[n++];
                final float n4 = this.ff[n++];
                final float n5 = this.ff[n++];
                this.tempTransform.origin.set(n3, n4, n5);
                final float n6 = this.ff[n++];
                final float n7 = this.ff[n++];
                final float n8 = this.ff[n++];
                final float n9 = this.ff[n++];
                this.javaxQuat4f.set(n6, n7, n8, n9);
                this.tempTransform.setRotation(this.javaxQuat4f);
                this.tempVector3f.set(this.ff[n++], this.ff[n++], this.ff[n++]);
                final float jniSpeed = this.ff[n++];
                final float n10 = this.ff[n++];
                final int n11 = (int)this.ff[n++];
                for (int k = 0; k < n11; ++k) {
                    this.wheelSteer[k] = this.ff[n++];
                    this.wheelRotation[k] = this.ff[n++];
                    this.wheelSkidInfo[k] = this.ff[n++];
                    this.wheelSuspensionLength[k] = this.ff[n++];
                }
                final int n12 = (int)(n3 * 100.0f + n4 * 100.0f + n5 * 100.0f + n6 * 100.0f + n7 * 100.0f + n8 * 100.0f + n9 * 100.0f);
                final BaseVehicle vehicleById = this.getVehicleById((short)n2);
                if (vehicleById != null) {
                    if (vehicleById.VehicleID == n2) {
                        if (n10 > 0.5f) {
                            this.collideVehicles.add(vehicleById);
                            vehicleById.authSimulationHash = n12;
                        }
                        if (GameServer.bServer) {
                            vehicleById.authorizationServerUpdate();
                        }
                    }
                    if (vehicleById != null) {
                        if (GameClient.bClient && vehicleById.netPlayerAuthorization == 1) {
                            if (vehicleById.authSimulationHash != n12) {
                                vehicleById.authSimulationTime = System.currentTimeMillis();
                                vehicleById.authSimulationHash = n12;
                            }
                            if (System.currentTimeMillis() - vehicleById.authSimulationTime > 1000L) {
                                VehicleManager.instance.sendCollide(vehicleById, isoPlayer, false);
                                vehicleById.authSimulationTime = 0L;
                            }
                        }
                        if (GameClient.bClient && (vehicleById.netPlayerAuthorization == 0 || vehicleById.netPlayerAuthorization == 4)) {
                            for (int l = 0; l < vehicleById.getScript().getWheelCount(); ++l) {
                                vehicleById.wheelInfo[l].suspensionLength = this.wheelSuspensionLength[l];
                            }
                        }
                        else {
                            if (this.compareTransform(this.tempTransform, vehicleById.getPoly().t)) {
                                vehicleById.polyDirty = true;
                            }
                            vehicleById.jniTransform.set(this.tempTransform);
                            vehicleById.jniLinearVelocity.set((Vector3fc)this.tempVector3f);
                            vehicleById.jniSpeed = jniSpeed;
                            vehicleById.jniIsCollide = (n10 > 0.5f);
                            for (int n13 = 0; n13 < n11; ++n13) {
                                vehicleById.wheelInfo[n13].steering = this.wheelSteer[n13];
                                vehicleById.wheelInfo[n13].rotation = this.wheelRotation[n13];
                                vehicleById.wheelInfo[n13].skidInfo = this.wheelSkidInfo[n13];
                                vehicleById.wheelInfo[n13].suspensionLength = this.wheelSuspensionLength[n13];
                            }
                        }
                    }
                }
            }
        }
        if (GameClient.bClient && vehicle != null) {
            for (int index = 0; index < this.collideVehicles.size(); ++index) {
                final BaseVehicle baseVehicle = this.collideVehicles.get(index);
                if (baseVehicle.DistTo(vehicle) < 8.0f && baseVehicle.netPlayerAuthorization == 0) {
                    VehicleManager.instance.sendCollide(baseVehicle, isoPlayer, true);
                    baseVehicle.authorizationClientForecast(true);
                    baseVehicle.authSimulationTime = System.currentTimeMillis();
                }
            }
        }
        MPStatistic.getInstance().Bullet.Start();
        final int objectPhysics = Bullet.getObjectPhysics(this.ff);
        MPStatistic.getInstance().Bullet.End();
        int n14 = 0;
        for (int n15 = 0; n15 < objectPhysics; ++n15) {
            final int m = (int)this.ff[n14++];
            final float n16 = this.ff[n14++];
            final float n17 = this.ff[n14++];
            final float n18 = this.ff[n14++];
            final float n19 = n16 + this.offsetX;
            final float y = n18 + this.offsetY;
            final IsoMovingObject isoMovingObject = this.physicsObjectMap.get(m);
            if (isoMovingObject != null) {
                isoMovingObject.removeFromSquare();
                isoMovingObject.setX(n19 + 0.18f);
                isoMovingObject.setY(y);
                isoMovingObject.setZ(Math.max(0.0f, n17 / 3.0f / 0.82f));
                isoMovingObject.setCurrent(IsoWorld.instance.getCell().getGridSquare(isoMovingObject.getX(), isoMovingObject.getY(), isoMovingObject.getZ()));
            }
        }
    }
    
    private BaseVehicle getVehicleById(final short n) {
        return VehicleManager.instance.getVehicleByID(n);
    }
    
    private boolean compareTransform(final Transform transform, final Transform transform2) {
        if (Math.abs(transform.origin.x - transform2.origin.x) > 0.01f || Math.abs(transform.origin.z - transform2.origin.z) > 0.01f || (int)transform.origin.y != (int)transform2.origin.y) {
            return true;
        }
        final int n = 2;
        transform.basis.getColumn(n, this.tempVector3f_2);
        final float x = this.tempVector3f_2.x;
        final float z = this.tempVector3f_2.z;
        transform2.basis.getColumn(n, this.tempVector3f_2);
        final float x2 = this.tempVector3f_2.x;
        final float z2 = this.tempVector3f_2.z;
        return Math.abs(x - x2) > 0.001f || Math.abs(z - z2) > 0.001f;
    }
    
    public void createServerCell(final ServerMap.ServerCell serverCell) {
        this.create();
        MPStatistic.getInstance().Bullet.Start();
        Bullet.createServerCell(serverCell.WX, serverCell.WY);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public void removeServerCell(final ServerMap.ServerCell serverCell) {
        MPStatistic.getInstance().Bullet.Start();
        Bullet.removeServerCell(serverCell.WX, serverCell.WY);
        MPStatistic.getInstance().Bullet.End();
    }
    
    public int getOwnVehiclePhysics(final int n, final ByteBufferWriter byteBufferWriter) {
        if (Bullet.getOwnVehiclePhysics(n, this.ff) == 0) {
            for (int i = 0; i < 27; ++i) {
                byteBufferWriter.bb.putFloat(this.ff[i]);
            }
            return 1;
        }
        return -1;
    }
    
    public int setOwnVehiclePhysics(final int n, final float[] array) {
        return Bullet.setOwnVehiclePhysics(n, array);
    }
    
    public void activateChunkMap(final int n) {
        this.create();
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[n];
        if (GameServer.bServer) {
            return;
        }
        Bullet.activateChunkMap(n, isoChunkMap.getWorldXMin(), isoChunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth);
    }
    
    public void deactivateChunkMap(final int n) {
        if (!this.created) {
            return;
        }
        Bullet.deactivateChunkMap(n);
    }
    
    public void scrollGroundLeft(final int n) {
        if (!this.created) {
            return;
        }
        Bullet.scrollChunkMapLeft(n);
    }
    
    public void scrollGroundRight(final int n) {
        if (!this.created) {
            return;
        }
        Bullet.scrollChunkMapRight(n);
    }
    
    public void scrollGroundUp(final int n) {
        if (!this.created) {
            return;
        }
        Bullet.scrollChunkMapUp(n);
    }
    
    public void scrollGroundDown(final int n) {
        if (!this.created) {
            return;
        }
        Bullet.scrollChunkMapDown(n);
    }
    
    public static TextureDraw.GenericDrawer getDrawer(final int n) {
        final PhysicsDebugRenderer alloc = PhysicsDebugRenderer.alloc();
        alloc.init(IsoPlayer.players[n]);
        return alloc;
    }
    
    static {
        WorldSimulation.instance = new WorldSimulation();
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe worldSimulationUpdate;
        
        static {
            worldSimulationUpdate = new PerformanceProfileProbe("WorldSimulation.update");
        }
    }
}
