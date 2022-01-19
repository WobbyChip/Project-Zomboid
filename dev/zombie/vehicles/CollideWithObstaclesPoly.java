// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.ArrayDeque;
import java.util.Arrays;
import zombie.util.list.PZArrayUtil;
import zombie.GameWindow;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import org.joml.Vector3fc;
import org.joml.Vector3f;
import zombie.iso.IsoUtils;
import java.util.List;
import zombie.popman.ObjectPool;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import org.joml.Vector2f;
import zombie.characters.IsoGameCharacter;
import java.util.Collection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import java.nio.ByteBuffer;
import zombie.iso.Vector2;
import java.util.ArrayList;

public class CollideWithObstaclesPoly
{
    static final float RADIUS = 0.3f;
    private final ArrayList<CCObstacle> obstacles;
    private final ArrayList<CCNode> nodes;
    private final ImmutableRectF moveBounds;
    private final ImmutableRectF vehicleBounds;
    private static final Vector2 move;
    private static final Vector2 nodeNormal;
    private static final Vector2 edgeVec;
    private final ArrayList<BaseVehicle> vehicles;
    private Clipper clipper;
    private final ByteBuffer xyBuffer;
    private final ClosestPointOnEdge closestPointOnEdge;
    
    public CollideWithObstaclesPoly() {
        this.obstacles = new ArrayList<CCObstacle>();
        this.nodes = new ArrayList<CCNode>();
        this.moveBounds = new ImmutableRectF();
        this.vehicleBounds = new ImmutableRectF();
        this.vehicles = new ArrayList<BaseVehicle>();
        this.xyBuffer = ByteBuffer.allocateDirect(8192);
        this.closestPointOnEdge = new ClosestPointOnEdge();
    }
    
    void getVehiclesInRect(final float n, final float n2, final float n3, final float n4, final int n5) {
        this.vehicles.clear();
        final int n6 = (int)(n / 10.0f);
        final int n7 = (int)(n2 / 10.0f);
        final int n8 = (int)Math.ceil(n3 / 10.0f);
        for (int n9 = (int)Math.ceil(n4 / 10.0f), i = n7; i < n9; ++i) {
            for (int j = n6; j < n8; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(j * 10, i * 10, 0);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final BaseVehicle e = isoChunk.vehicles.get(k);
                        if (e.getScript() != null && (int)e.z == n5) {
                            this.vehicles.add(e);
                        }
                    }
                }
            }
        }
    }
    
    void getObstaclesInRect(final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final boolean b) {
        if (this.clipper == null) {
            this.clipper = new Clipper();
        }
        this.clipper.clear();
        this.moveBounds.init(n - 2.0f, n2 - 2.0f, n3 - n + 4.0f, n4 - n2 + 4.0f);
        int n8 = (int)(this.moveBounds.x / 10.0f);
        int n9 = (int)(this.moveBounds.y / 10.0f);
        int n10 = (int)Math.ceil(this.moveBounds.right() / 10.0f);
        int n11 = (int)Math.ceil(this.moveBounds.bottom() / 10.0f);
        if (Math.abs(n3 - n) < 2.0f && Math.abs(n4 - n2) < 2.0f) {
            n8 = n5 / 10;
            n9 = n6 / 10;
            n10 = n8 + 1;
            n11 = n9 + 1;
        }
        for (int i = n9; i < n11; ++i) {
            for (int j = n8; j < n10; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk != null) {
                    final ChunkDataZ init = isoChunk.collision.init(isoChunk, n7, this);
                    final ArrayList<CCObstacle> list = b ? init.worldVehicleUnion : init.worldVehicleSeparate;
                    for (int k = 0; k < list.size(); ++k) {
                        final CCObstacle e = list.get(k);
                        if (e.bounds.intersects(this.moveBounds)) {
                            this.obstacles.add(e);
                        }
                    }
                    this.nodes.addAll(init.nodes);
                }
            }
        }
    }
    
    public Vector2f resolveCollision(final IsoGameCharacter isoGameCharacter, final float n, final float n2, final Vector2f vector2f) {
        vector2f.set(n, n2);
        final boolean b = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
        final float x = isoGameCharacter.x;
        final float y = isoGameCharacter.y;
        if (b) {
            LineDrawer.addLine(x, y, (float)(int)isoGameCharacter.z, n, n2, (float)(int)isoGameCharacter.z, 1.0f, 1.0f, 1.0f, null, true);
        }
        if (x == n && y == n2) {
            return vector2f;
        }
        CollideWithObstaclesPoly.move.set(n - isoGameCharacter.x, n2 - isoGameCharacter.y);
        CollideWithObstaclesPoly.move.normalize();
        this.nodes.clear();
        this.obstacles.clear();
        this.getObstaclesInRect(Math.min(x, n), Math.min(y, n2), Math.max(x, n), Math.max(y, n2), (int)isoGameCharacter.x, (int)isoGameCharacter.y, (int)isoGameCharacter.z, true);
        this.closestPointOnEdge.edge = null;
        this.closestPointOnEdge.node = null;
        this.closestPointOnEdge.distSq = Double.MAX_VALUE;
        for (int i = 0; i < this.obstacles.size(); ++i) {
            final CCObstacle ccObstacle = this.obstacles.get(i);
            if (ccObstacle.isPointInside(isoGameCharacter.x, isoGameCharacter.y, 0)) {
                ccObstacle.getClosestPointOnEdge(isoGameCharacter.x, isoGameCharacter.y, this.closestPointOnEdge);
            }
        }
        CCEdge ccEdge = this.closestPointOnEdge.edge;
        CCNode ccNode = this.closestPointOnEdge.node;
        if (ccEdge != null && ccEdge.normal.dot(CollideWithObstaclesPoly.move) >= 0.01f) {
            ccEdge = null;
        }
        if (ccNode != null && ccNode.getNormalAndEdgeVectors(CollideWithObstaclesPoly.nodeNormal, CollideWithObstaclesPoly.edgeVec) && CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.move) + 0.05f >= CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.edgeVec)) {
            ccNode = null;
            ccEdge = null;
        }
        if (ccEdge == null) {
            this.closestPointOnEdge.edge = null;
            this.closestPointOnEdge.node = null;
            this.closestPointOnEdge.distSq = Double.MAX_VALUE;
            for (int j = 0; j < this.obstacles.size(); ++j) {
                this.obstacles.get(j).lineSegmentIntersect(x, y, n, n2, this.closestPointOnEdge, b);
            }
            ccEdge = this.closestPointOnEdge.edge;
            ccNode = this.closestPointOnEdge.node;
        }
        if (ccNode != null) {
            CollideWithObstaclesPoly.move.set(n - isoGameCharacter.x, n2 - isoGameCharacter.y);
            CollideWithObstaclesPoly.move.normalize();
            final CCEdge ccEdge2 = ccEdge;
            CCEdge ccEdge3 = null;
            for (int k = 0; k < ccNode.edges.size(); ++k) {
                final CCEdge ccEdge4 = ccNode.edges.get(k);
                if (ccEdge4 != ccEdge) {
                    if (ccEdge2.node1.x != ccEdge4.node1.x || ccEdge2.node1.y != ccEdge4.node1.y || ccEdge2.node2.x != ccEdge4.node2.x || ccEdge2.node2.y != ccEdge4.node2.y) {
                        if (ccEdge2.node1.x != ccEdge4.node2.x || ccEdge2.node1.y != ccEdge4.node2.y || ccEdge2.node2.x != ccEdge4.node1.x || ccEdge2.node2.y != ccEdge4.node1.y) {
                            if (!ccEdge2.hasNode(ccEdge4.node1) || !ccEdge2.hasNode(ccEdge4.node2)) {
                                ccEdge3 = ccEdge4;
                            }
                        }
                    }
                }
            }
            if (ccEdge2 != null && ccEdge3 != null) {
                if (ccEdge == ccEdge2) {
                    final CCNode ccNode2 = (ccNode == ccEdge3.node1) ? ccEdge3.node2 : ccEdge3.node1;
                    CollideWithObstaclesPoly.edgeVec.set(ccNode2.x - ccNode.x, ccNode2.y - ccNode.y);
                    CollideWithObstaclesPoly.edgeVec.normalize();
                    if (CollideWithObstaclesPoly.move.dot(CollideWithObstaclesPoly.edgeVec) >= 0.0f) {
                        ccEdge = ccEdge3;
                    }
                }
                else if (ccEdge == ccEdge3) {
                    final CCNode ccNode3 = (ccNode == ccEdge2.node1) ? ccEdge2.node2 : ccEdge2.node1;
                    CollideWithObstaclesPoly.edgeVec.set(ccNode3.x - ccNode.x, ccNode3.y - ccNode.y);
                    CollideWithObstaclesPoly.edgeVec.normalize();
                    if (CollideWithObstaclesPoly.move.dot(CollideWithObstaclesPoly.edgeVec) >= 0.0f) {
                        ccEdge = ccEdge2;
                    }
                }
            }
        }
        if (ccEdge != null) {
            if (b) {
                LineDrawer.addLine(ccEdge.node1.x, ccEdge.node1.y, (float)ccEdge.node1.z, ccEdge.node2.x, ccEdge.node2.y, (float)ccEdge.node1.z, 0.0f, 1.0f, 1.0f, null, true);
            }
            this.closestPointOnEdge.distSq = Double.MAX_VALUE;
            ccEdge.getClosestPointOnEdge(n, n2, this.closestPointOnEdge);
            vector2f.set(this.closestPointOnEdge.point.x, this.closestPointOnEdge.point.y);
        }
        return vector2f;
    }
    
    boolean canStandAt(final float n, final float n2, final float n3, final BaseVehicle baseVehicle, final int n4) {
        final boolean b = (n4 & 0x1) != 0x0;
        final boolean b2 = (n4 & 0x2) != 0x0;
        final float n5 = n - 0.3f;
        final float n6 = n2 - 0.3f;
        final float n7 = n + 0.3f;
        final float n8 = n2 + 0.3f;
        this.nodes.clear();
        this.obstacles.clear();
        this.getObstaclesInRect(Math.min(n5, n7), Math.min(n6, n8), Math.max(n5, n7), Math.max(n6, n8), (int)n, (int)n2, (int)n3, baseVehicle == null);
        for (int i = 0; i < this.obstacles.size(); ++i) {
            final CCObstacle ccObstacle = this.obstacles.get(i);
            if (baseVehicle == null || ccObstacle.vehicle != baseVehicle) {
                if (ccObstacle.isPointInside(n, n2, n4)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isNotClear(float n, float n2, float n3, float n4, final int n5, final boolean b, final BaseVehicle baseVehicle, final boolean b2, final boolean b3) {
        final float n6 = n;
        final float n7 = n2;
        final float n8 = n3;
        final float n9 = n4;
        n /= 10.0f;
        n2 /= 10.0f;
        n3 /= 10.0f;
        n4 /= 10.0f;
        final double n10 = Math.abs(n3 - n);
        final double n11 = Math.abs(n4 - n2);
        int n12 = (int)Math.floor(n);
        int n13 = (int)Math.floor(n2);
        int i = 1;
        int n14;
        double n15;
        if (n10 == 0.0) {
            n14 = 0;
            n15 = Double.POSITIVE_INFINITY;
        }
        else if (n3 > n) {
            n14 = 1;
            i += (int)Math.floor(n3) - n12;
            n15 = (Math.floor(n) + 1.0 - n) * n11;
        }
        else {
            n14 = -1;
            i += n12 - (int)Math.floor(n3);
            n15 = (n - Math.floor(n)) * n11;
        }
        int n16;
        double n17;
        if (n11 == 0.0) {
            n16 = 0;
            n17 = n15 - Double.POSITIVE_INFINITY;
        }
        else if (n4 > n2) {
            n16 = 1;
            i += (int)Math.floor(n4) - n13;
            n17 = n15 - (Math.floor(n2) + 1.0 - n2) * n10;
        }
        else {
            n16 = -1;
            i += n13 - (int)Math.floor(n4);
            n17 = n15 - (n2 - Math.floor(n2)) * n10;
        }
        while (i > 0) {
            final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(n12, n13) : IsoWorld.instance.CurrentCell.getChunk(n12, n13);
            if (isoChunk != null) {
                if (b) {
                    LineDrawer.addRect((float)(n12 * 10), (float)(n13 * 10), (float)n5, 10.0f, 10.0f, 1.0f, 1.0f, 1.0f);
                }
                final ChunkDataZ init = isoChunk.collision.init(isoChunk, n5, this);
                final ArrayList<CCObstacle> list = (baseVehicle == null) ? init.worldVehicleUnion : init.worldVehicleSeparate;
                for (int j = 0; j < list.size(); ++j) {
                    final CCObstacle ccObstacle = list.get(j);
                    if (baseVehicle == null || ccObstacle.vehicle != baseVehicle) {
                        if (ccObstacle.lineSegmentIntersects(n6, n7, n8, n9, b)) {
                            return true;
                        }
                    }
                }
            }
            if (n17 > 0.0) {
                n13 += n16;
                n17 -= n10;
            }
            else {
                n12 += n14;
                n17 += n11;
            }
            --i;
        }
        return false;
    }
    
    private void vehicleMoved(final PolygonalMap2.VehiclePoly vehiclePoly) {
        final int n = 2;
        final int n2 = (int)Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
        final int n3 = (int)Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
        final int n4 = (int)Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
        final int n5 = (int)Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
        final int n6 = (int)vehiclePoly.z;
        final int n7 = (n2 - n) / 10;
        final int n8 = (n3 - n) / 10;
        final int n9 = (int)Math.ceil((n4 + n - 1.0f) / 10.0f);
        for (int n10 = (int)Math.ceil((n5 + n - 1.0f) / 10.0f), i = n8; i <= n10; ++i) {
            for (int j = n7; j <= n9; ++j) {
                final IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (chunk != null && chunk.collision.data[n6] != null) {
                    final ChunkDataZ chunkDataZ = chunk.collision.data[n6];
                    chunk.collision.data[n6] = null;
                    chunkDataZ.clear();
                    ChunkDataZ.pool.release(chunkDataZ);
                }
            }
        }
    }
    
    public void vehicleMoved(final PolygonalMap2.VehiclePoly vehiclePoly, final PolygonalMap2.VehiclePoly vehiclePoly2) {
        this.vehicleMoved(vehiclePoly);
        this.vehicleMoved(vehiclePoly2);
    }
    
    public void render() {
        if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue()) {
            final IsoPlayer instance = IsoPlayer.getInstance();
            if (instance == null) {
                return;
            }
            this.nodes.clear();
            this.obstacles.clear();
            this.getObstaclesInRect(instance.x, instance.y, instance.x, instance.y, (int)instance.x, (int)instance.y, (int)instance.z, true);
            if (DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
                for (final CCNode ccNode : this.nodes) {
                    if (ccNode.getNormalAndEdgeVectors(CollideWithObstaclesPoly.nodeNormal, CollideWithObstaclesPoly.edgeVec)) {
                        LineDrawer.addLine(ccNode.x, ccNode.y, (float)ccNode.z, ccNode.x + CollideWithObstaclesPoly.nodeNormal.x, ccNode.y + CollideWithObstaclesPoly.nodeNormal.y, (float)ccNode.z, 0.0f, 0.0f, 1.0f, null, true);
                    }
                }
            }
            final Iterator<CCObstacle> iterator2 = this.obstacles.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().render();
            }
        }
    }
    
    static {
        move = new Vector2();
        nodeNormal = new Vector2();
        edgeVec = new Vector2();
    }
    
    private static final class CCNode
    {
        float x;
        float y;
        int z;
        final ArrayList<CCEdge> edges;
        static final ObjectPool<CCNode> pool;
        
        private CCNode() {
            this.edges = new ArrayList<CCEdge>();
        }
        
        CCNode init(final float x, final float y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.edges.clear();
            return this;
        }
        
        CCNode setXY(final float x, final float y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        boolean getNormalAndEdgeVectors(final Vector2 vector2, final Vector2 vector3) {
            CCEdge ccEdge = null;
            CCEdge ccEdge2 = null;
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge3 = this.edges.get(i);
                if (ccEdge == null) {
                    ccEdge = ccEdge3;
                }
                else if (!ccEdge.hasNode(ccEdge3.node1) || !ccEdge.hasNode(ccEdge3.node2)) {
                    ccEdge2 = ccEdge3;
                }
            }
            if (ccEdge == null || ccEdge2 == null) {
                return false;
            }
            vector2.set(ccEdge.normal.x + ccEdge2.normal.x, ccEdge.normal.y + ccEdge2.normal.y);
            vector2.normalize();
            if (ccEdge.node1 == this) {
                vector3.set(ccEdge.node2.x - ccEdge.node1.x, ccEdge.node2.y - ccEdge.node1.y);
            }
            else {
                vector3.set(ccEdge.node1.x - ccEdge.node2.x, ccEdge.node1.y - ccEdge.node2.y);
            }
            vector3.normalize();
            return true;
        }
        
        static CCNode alloc() {
            return CCNode.pool.alloc();
        }
        
        void release() {
            CCNode.pool.release(this);
        }
        
        static void releaseAll(final ArrayList<CCNode> list) {
            CCNode.pool.releaseAll(list);
        }
        
        static {
            pool = new ObjectPool<CCNode>(CCNode::new);
        }
    }
    
    private static final class CCEdge
    {
        CCNode node1;
        CCNode node2;
        CCObstacle obstacle;
        final Vector2 normal;
        static final ObjectPool<CCEdge> pool;
        
        private CCEdge() {
            this.normal = new Vector2();
        }
        
        CCEdge init(final CCNode node1, final CCNode node2, final CCObstacle obstacle) {
            if (node1.x == node2.x && node1.y == node2.y) {}
            this.node1 = node1;
            this.node2 = node2;
            node1.edges.add(this);
            node2.edges.add(this);
            this.obstacle = obstacle;
            this.normal.set(node2.x - node1.x, node2.y - node1.y);
            this.normal.normalize();
            this.normal.rotate((float)Math.toRadians(90.0));
            return this;
        }
        
        boolean hasNode(final CCNode ccNode) {
            return ccNode == this.node1 || ccNode == this.node2;
        }
        
        void getClosestPointOnEdge(final float n, final float n2, final ClosestPointOnEdge closestPointOnEdge) {
            final float x = this.node1.x;
            final float y = this.node1.y;
            final float x2 = this.node2.x;
            final float y2 = this.node2.y;
            final double n3 = ((n - x) * (x2 - x) + (n2 - y) * (y2 - y)) / (Math.pow(x2 - x, 2.0) + Math.pow(y2 - y, 2.0));
            double n4 = x + n3 * (x2 - x);
            double n5 = y + n3 * (y2 - y);
            final double n6 = 0.001;
            CCNode node = null;
            if (n3 <= 0.0 + n6) {
                n4 = x;
                n5 = y;
                node = this.node1;
            }
            else if (n3 >= 1.0 - n6) {
                n4 = x2;
                n5 = y2;
                node = this.node2;
            }
            final double distSq = (n - n4) * (n - n4) + (n2 - n5) * (n2 - n5);
            if (distSq < closestPointOnEdge.distSq) {
                closestPointOnEdge.point.set((float)n4, (float)n5);
                closestPointOnEdge.distSq = distSq;
                closestPointOnEdge.edge = this;
                closestPointOnEdge.node = node;
            }
        }
        
        boolean isPointOn(final float n, final float n2) {
            final float x = this.node1.x;
            final float y = this.node1.y;
            final float x2 = this.node2.x;
            final float y2 = this.node2.y;
            final double n3 = ((n - x) * (x2 - x) + (n2 - y) * (y2 - y)) / (Math.pow(x2 - x, 2.0) + Math.pow(y2 - y, 2.0));
            double n4 = x + n3 * (x2 - x);
            double n5 = y + n3 * (y2 - y);
            if (n3 <= 0.0) {
                n4 = x;
                n5 = y;
            }
            else if (n3 >= 1.0) {
                n4 = x2;
                n5 = y2;
            }
            return (n - n4) * (n - n4) + (n2 - n5) * (n2 - n5) < 1.0E-6;
        }
        
        static CCEdge alloc() {
            return CCEdge.pool.alloc();
        }
        
        void release() {
            CCEdge.pool.release(this);
        }
        
        static void releaseAll(final ArrayList<CCEdge> list) {
            CCEdge.pool.releaseAll(list);
        }
        
        static {
            pool = new ObjectPool<CCEdge>(CCEdge::new);
        }
    }
    
    private enum EdgeRingHit
    {
        OnEdge, 
        Inside, 
        Outside;
        
        private static /* synthetic */ EdgeRingHit[] $values() {
            return new EdgeRingHit[] { EdgeRingHit.OnEdge, EdgeRingHit.Inside, EdgeRingHit.Outside };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static final class CCEdgeRing extends ArrayList<CCEdge>
    {
        static final ObjectPool<CCEdgeRing> pool;
        
        float isLeft(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            return (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
        }
        
        EdgeRingHit isPointInPolygon_WindingNumber(final float n, final float n2, final int n3) {
            int n4 = 0;
            for (int i = 0; i < this.size(); ++i) {
                final CCEdge ccEdge = this.get(i);
                if ((n3 & 0x10) != 0x0 && ccEdge.isPointOn(n, n2)) {
                    return EdgeRingHit.OnEdge;
                }
                if (ccEdge.node1.y <= n2) {
                    if (ccEdge.node2.y > n2 && this.isLeft(ccEdge.node1.x, ccEdge.node1.y, ccEdge.node2.x, ccEdge.node2.y, n, n2) > 0.0f) {
                        ++n4;
                    }
                }
                else if (ccEdge.node2.y <= n2 && this.isLeft(ccEdge.node1.x, ccEdge.node1.y, ccEdge.node2.x, ccEdge.node2.y, n, n2) < 0.0f) {
                    --n4;
                }
            }
            return (n4 == 0) ? EdgeRingHit.Outside : EdgeRingHit.Inside;
        }
        
        boolean lineSegmentIntersects(final float n, final float n2, final float n3, final float n4, final boolean b, final boolean b2) {
            CollideWithObstaclesPoly.move.set(n3 - n, n4 - n2);
            final float length = CollideWithObstaclesPoly.move.getLength();
            CollideWithObstaclesPoly.move.normalize();
            final float x = CollideWithObstaclesPoly.move.x;
            final float y = CollideWithObstaclesPoly.move.y;
            for (int i = 0; i < this.size(); ++i) {
                final CCEdge ccEdge = this.get(i);
                if (!ccEdge.isPointOn(n, n2)) {
                    if (!ccEdge.isPointOn(n3, n4)) {
                        if (ccEdge.normal.dot(CollideWithObstaclesPoly.move) < 0.01f) {
                            final float x2 = ccEdge.node1.x;
                            final float y2 = ccEdge.node1.y;
                            final float x3 = ccEdge.node2.x;
                            final float y3 = ccEdge.node2.y;
                            final float n5 = n - x2;
                            final float n6 = n2 - y2;
                            final float n7 = x3 - x2;
                            final float n8 = y3 - y2;
                            final float n9 = 1.0f / (n8 * x - n7 * y);
                            final float n10 = (n7 * n6 - n8 * n5) * n9;
                            if (n10 >= 0.0f && n10 <= length) {
                                final float n11 = (n6 * x - n5 * y) * n9;
                                if (n11 >= 0.0f && n11 <= 1.0f) {
                                    final float n12 = n + n10 * x;
                                    final float n13 = n2 + n10 * y;
                                    if (b) {
                                        this.render(b2);
                                        LineDrawer.addRect(n12 - 0.05f, n13 - 0.05f, (float)ccEdge.node1.z, 0.1f, 0.1f, 1.0f, 1.0f, 1.0f);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return this.isPointInPolygon_WindingNumber((n + n3) / 2.0f, (n2 + n4) / 2.0f, 0) != EdgeRingHit.Outside;
        }
        
        void lineSegmentIntersect(final float n, final float n2, final float n3, final float n4, final ClosestPointOnEdge closestPointOnEdge, final boolean b) {
            CollideWithObstaclesPoly.move.set(n3 - n, n4 - n2).normalize();
            for (int i = 0; i < this.size(); ++i) {
                final CCEdge ccEdge = this.get(i);
                if (ccEdge.normal.dot(CollideWithObstaclesPoly.move) < 0.0f) {
                    final float x = ccEdge.node1.x;
                    final float y = ccEdge.node1.y;
                    final float x2 = ccEdge.node2.x;
                    final float y2 = ccEdge.node2.y;
                    final float n5 = x + 0.5f * (x2 - x);
                    final float n6 = y + 0.5f * (y2 - y);
                    if (b && DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
                        LineDrawer.addLine(n5, n6, (float)ccEdge.node1.z, n5 + ccEdge.normal.x, n6 + ccEdge.normal.y, (float)ccEdge.node1.z, 0.0f, 0.0f, 1.0f, null, true);
                    }
                    final double n7 = (y2 - y) * (n3 - n) - (x2 - x) * (n4 - n2);
                    if (n7 != 0.0) {
                        final double n8 = ((x2 - x) * (n2 - y) - (y2 - y) * (n - x)) / n7;
                        final double n9 = ((n3 - n) * (n2 - y) - (n4 - n2) * (n - x)) / n7;
                        if (n8 >= 0.0 && n8 <= 1.0 && n9 >= 0.0 && n9 <= 1.0) {
                            if (n9 < 0.01 || n9 > 0.99) {
                                final CCNode node = (n9 < 0.01) ? ccEdge.node1 : ccEdge.node2;
                                final double distSq = IsoUtils.DistanceToSquared(n, n2, node.x, node.y);
                                if (distSq >= closestPointOnEdge.distSq) {
                                    continue;
                                }
                                if (node.getNormalAndEdgeVectors(CollideWithObstaclesPoly.nodeNormal, CollideWithObstaclesPoly.edgeVec)) {
                                    if (CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.move) + 0.05f >= CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.edgeVec)) {
                                        continue;
                                    }
                                    closestPointOnEdge.edge = ccEdge;
                                    closestPointOnEdge.node = node;
                                    closestPointOnEdge.distSq = distSq;
                                    continue;
                                }
                            }
                            final double distSq2 = IsoUtils.DistanceToSquared(n, n2, (float)(n + n8 * (n3 - n)), (float)(n2 + n8 * (n4 - n2)));
                            if (distSq2 < closestPointOnEdge.distSq) {
                                closestPointOnEdge.edge = ccEdge;
                                closestPointOnEdge.node = null;
                                closestPointOnEdge.distSq = distSq2;
                            }
                        }
                    }
                }
            }
        }
        
        void getClosestPointOnEdge(final float n, final float n2, final ClosestPointOnEdge closestPointOnEdge) {
            for (int i = 0; i < this.size(); ++i) {
                this.get(i).getClosestPointOnEdge(n, n2, closestPointOnEdge);
            }
        }
        
        void render(final boolean b) {
            if (this.isEmpty()) {
                return;
            }
            final float n = 0.0f;
            final float n2 = b ? 1.0f : 0.5f;
            final float n3 = b ? 0.0f : 0.5f;
            final BaseVehicle.Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
            for (final CCEdge ccEdge : this) {
                final CCNode node1 = ccEdge.node1;
                final CCNode node2 = ccEdge.node2;
                LineDrawer.addLine(node1.x, node1.y, (float)node1.z, node2.x, node2.y, (float)node2.z, n, n2, n3, null, true);
                if (!false) {
                    continue;
                }
                final Vector3f normalize = vector3fObjectPool.alloc().set(node2.x - node1.x, node2.y - node1.y, (float)(node2.z - node1.z)).normalize();
                final Vector3f normalize2 = vector3fObjectPool.alloc().set((Vector3fc)normalize).cross(0.0f, 0.0f, 1.0f).normalize();
                normalize.mul(0.9f);
                LineDrawer.addLine(node2.x - normalize.x * 0.1f - normalize2.x * 0.1f, node2.y - normalize.y * 0.1f - normalize2.y * 0.1f, (float)node2.z, node2.x, node2.y, (float)node2.z, n, n2, n3, null, true);
                LineDrawer.addLine(node2.x - normalize.x * 0.1f + normalize2.x * 0.1f, node2.y - normalize.y * 0.1f + normalize2.y * 0.1f, (float)node2.z, node2.x, node2.y, (float)node2.z, n, n2, n3, null, true);
                vector3fObjectPool.release(normalize);
                vector3fObjectPool.release(normalize2);
            }
            final CCNode node3 = this.get(0).node1;
            LineDrawer.addRect(node3.x - 0.1f, node3.y - 0.1f, (float)node3.z, 0.2f, 0.2f, 1.0f, 0.0f, 0.0f);
        }
        
        static void releaseAll(final ArrayList<CCEdgeRing> list) {
            CCEdgeRing.pool.releaseAll(list);
        }
        
        static {
            pool = new ObjectPool<CCEdgeRing>() {
                @Override
                public void release(final CCEdgeRing ccEdgeRing) {
                    CCEdge.releaseAll(ccEdgeRing);
                    this.clear();
                    super.release(ccEdgeRing);
                }
            };
        }
    }
    
    private static final class CCObstacle
    {
        final CCEdgeRing outer;
        final ArrayList<CCEdgeRing> inner;
        BaseVehicle vehicle;
        ImmutableRectF bounds;
        static final ObjectPool<CCObstacle> pool;
        
        private CCObstacle() {
            this.outer = new CCEdgeRing();
            this.inner = new ArrayList<CCEdgeRing>();
            this.vehicle = null;
        }
        
        CCObstacle init() {
            this.outer.clear();
            this.inner.clear();
            this.vehicle = null;
            return this;
        }
        
        boolean isPointInside(final float n, final float n2, final int n3) {
            if (this.outer.isPointInPolygon_WindingNumber(n, n2, n3) != EdgeRingHit.Inside) {
                return false;
            }
            if (this.inner.isEmpty()) {
                return true;
            }
            for (int i = 0; i < this.inner.size(); ++i) {
                if (this.inner.get(i).isPointInPolygon_WindingNumber(n, n2, n3) != EdgeRingHit.Outside) {
                    return false;
                }
            }
            return true;
        }
        
        boolean lineSegmentIntersects(final float n, final float n2, final float n3, final float n4, final boolean b) {
            if (this.outer.lineSegmentIntersects(n, n2, n3, n4, b, true)) {
                return true;
            }
            for (int i = 0; i < this.inner.size(); ++i) {
                if (this.inner.get(i).lineSegmentIntersects(n, n2, n3, n4, b, false)) {
                    return true;
                }
            }
            return false;
        }
        
        void lineSegmentIntersect(final float n, final float n2, final float n3, final float n4, final ClosestPointOnEdge closestPointOnEdge, final boolean b) {
            this.outer.lineSegmentIntersect(n, n2, n3, n4, closestPointOnEdge, b);
            for (int i = 0; i < this.inner.size(); ++i) {
                this.inner.get(i).lineSegmentIntersect(n, n2, n3, n4, closestPointOnEdge, b);
            }
        }
        
        void getClosestPointOnEdge(final float n, final float n2, final ClosestPointOnEdge closestPointOnEdge) {
            this.outer.getClosestPointOnEdge(n, n2, closestPointOnEdge);
            for (int i = 0; i < this.inner.size(); ++i) {
                this.inner.get(i).getClosestPointOnEdge(n, n2, closestPointOnEdge);
            }
        }
        
        void calcBounds() {
            float min = Float.MAX_VALUE;
            float min2 = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            float max2 = Float.MIN_VALUE;
            for (int i = 0; i < this.outer.size(); ++i) {
                final CCEdge ccEdge = this.outer.get(i);
                min = Math.min(min, ccEdge.node1.x);
                min2 = Math.min(min2, ccEdge.node1.y);
                max = Math.max(max, ccEdge.node1.x);
                max2 = Math.max(max2, ccEdge.node1.y);
            }
            if (this.bounds != null) {
                this.bounds.release();
            }
            final float n = 0.01f;
            this.bounds = ImmutableRectF.alloc().init(min - n, min2 - n, max - min + n * 2.0f, max2 - min2 + n * 2.0f);
        }
        
        void render() {
            this.outer.render(true);
            for (int i = 0; i < this.inner.size(); ++i) {
                this.inner.get(i).render(false);
            }
        }
        
        static CCObstacle alloc() {
            return CCObstacle.pool.alloc();
        }
        
        void release() {
            CCObstacle.pool.release(this);
        }
        
        static void releaseAll(final ArrayList<CCObstacle> list) {
            CCObstacle.pool.releaseAll(list);
        }
        
        static {
            pool = new ObjectPool<CCObstacle>() {
                @Override
                public void release(final CCObstacle ccObstacle) {
                    CCEdge.releaseAll(ccObstacle.outer);
                    CCEdgeRing.releaseAll(ccObstacle.inner);
                    ccObstacle.outer.clear();
                    ccObstacle.inner.clear();
                    ccObstacle.vehicle = null;
                    super.release(ccObstacle);
                }
            };
        }
    }
    
    public static final class ChunkDataZ
    {
        public final ArrayList<CCObstacle> worldVehicleUnion;
        public final ArrayList<CCObstacle> worldVehicleSeparate;
        public final ArrayList<CCNode> nodes;
        public int z;
        public static final ObjectPool<ChunkDataZ> pool;
        
        public ChunkDataZ() {
            this.worldVehicleUnion = new ArrayList<CCObstacle>();
            this.worldVehicleSeparate = new ArrayList<CCObstacle>();
            this.nodes = new ArrayList<CCNode>();
        }
        
        public void init(final IsoChunk isoChunk, final int z, final CollideWithObstaclesPoly collideWithObstaclesPoly) {
            this.z = z;
            final Clipper clipper = collideWithObstaclesPoly.clipper;
            clipper.clear();
            final float n = 0.19800001f;
            final int n2 = isoChunk.wx * 10;
            for (int n3 = isoChunk.wy * 10, i = n3 - 2; i < n3 + 10 + 2; ++i) {
                for (int j = n2 - 2; j < n2 + 10 + 2; ++j) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, i, z);
                    if (gridSquare != null) {
                        if (!gridSquare.getObjects().isEmpty()) {
                            if (gridSquare.isSolid() || (gridSquare.isSolidTrans() && !gridSquare.isAdjacentToWindow())) {
                                clipper.addAABBBevel(j - 0.3f, i - 0.3f, j + 1.0f + 0.3f, i + 1.0f + 0.3f, n);
                            }
                            boolean b = gridSquare.Is(IsoFlagType.collideW) || gridSquare.hasBlockedDoor(false) || gridSquare.HasStairsNorth();
                            if (gridSquare.Is(IsoFlagType.windowW) || gridSquare.Is(IsoFlagType.WindowW)) {
                                b = true;
                            }
                            if (b) {
                                if (!this.isCollideW(j, i - 1, z)) {}
                                final boolean b2 = false;
                                if (!this.isCollideW(j, i + 1, z)) {}
                                clipper.addAABBBevel(j - 0.3f, i - (b2 ? 0.0f : 0.3f), j + 0.3f, i + 1.0f + (false ? 0.0f : 0.3f), n);
                            }
                            boolean b3 = gridSquare.Is(IsoFlagType.collideN) || gridSquare.hasBlockedDoor(true) || gridSquare.HasStairsWest();
                            if (gridSquare.Is(IsoFlagType.windowN) || gridSquare.Is(IsoFlagType.WindowN)) {
                                b3 = true;
                            }
                            if (b3) {
                                if (!this.isCollideN(j - 1, i, z)) {}
                                final boolean b4 = false;
                                if (!this.isCollideN(j + 1, i, z)) {}
                                clipper.addAABBBevel(j - (b4 ? 0.0f : 0.3f), i - 0.3f, j + 1.0f + (false ? 0.0f : 0.3f), i + 0.3f, n);
                            }
                            if (gridSquare.HasStairsNorth()) {
                                if (IsoWorld.instance.CurrentCell.getGridSquare(j + 1, i, z) != null) {
                                    clipper.addAABBBevel(j + 1 - 0.3f, i - 0.3f, j + 1 + 0.3f, i + 1.0f + 0.3f, n);
                                }
                                if (gridSquare.Has(IsoObjectType.stairsTN)) {
                                    final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(j, i, z - 1);
                                    if (gridSquare2 == null || !gridSquare2.Has(IsoObjectType.stairsTN)) {
                                        clipper.addAABBBevel(j - 0.3f, i - 0.3f, j + 1.0f + 0.3f, i + 0.3f, n);
                                        clipper.clipAABB(j + 0.3f, i - 0.1f, j + 1.0f - 0.3f, i + 0.3f);
                                    }
                                }
                            }
                            if (gridSquare.HasStairsWest()) {
                                if (IsoWorld.instance.CurrentCell.getGridSquare(j, i + 1, z) != null) {
                                    clipper.addAABBBevel(j - 0.3f, i + 1 - 0.3f, j + 1.0f + 0.3f, i + 1 + 0.3f, n);
                                }
                                if (gridSquare.Has(IsoObjectType.stairsTW)) {
                                    final IsoGridSquare gridSquare3 = IsoWorld.instance.CurrentCell.getGridSquare(j, i, z - 1);
                                    if (gridSquare3 == null || !gridSquare3.Has(IsoObjectType.stairsTW)) {
                                        clipper.addAABBBevel(j - 0.3f, i - 0.3f, j + 0.3f, i + 1.0f + 0.3f, n);
                                        clipper.clipAABB(j - 0.1f, i + 0.3f, j + 0.3f, i + 1.0f - 0.3f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final ByteBuffer xyBuffer = collideWithObstaclesPoly.xyBuffer;
            assert this.worldVehicleSeparate.isEmpty();
            this.clipperToObstacles(clipper, xyBuffer, this.worldVehicleSeparate);
            int n4 = isoChunk.wx * 10;
            int n5 = isoChunk.wy * 10;
            int n6 = n4 + 10;
            int n7 = n5 + 10;
            n4 -= 2;
            n5 -= 2;
            n6 += 2;
            n7 += 2;
            final ImmutableRectF init = collideWithObstaclesPoly.moveBounds.init((float)n4, (float)n5, (float)(n6 - n4), (float)(n7 - n5));
            collideWithObstaclesPoly.getVehiclesInRect((float)(n4 - 5), (float)(n5 - 5), (float)(n6 + 5), (float)(n7 + 5), z);
            for (int k = 0; k < collideWithObstaclesPoly.vehicles.size(); ++k) {
                final BaseVehicle vehicle = collideWithObstaclesPoly.vehicles.get(k);
                final PolygonalMap2.VehiclePoly polyPlusRadius = vehicle.getPolyPlusRadius();
                final float min = Math.min(polyPlusRadius.x1, Math.min(polyPlusRadius.x2, Math.min(polyPlusRadius.x3, polyPlusRadius.x4)));
                final float min2 = Math.min(polyPlusRadius.y1, Math.min(polyPlusRadius.y2, Math.min(polyPlusRadius.y3, polyPlusRadius.y4)));
                collideWithObstaclesPoly.vehicleBounds.init(min, min2, Math.max(polyPlusRadius.x1, Math.max(polyPlusRadius.x2, Math.max(polyPlusRadius.x3, polyPlusRadius.x4))) - min, Math.max(polyPlusRadius.y1, Math.max(polyPlusRadius.y2, Math.max(polyPlusRadius.y3, polyPlusRadius.y4))) - min2);
                if (init.intersects(collideWithObstaclesPoly.vehicleBounds)) {
                    clipper.addPolygon(polyPlusRadius.x1, polyPlusRadius.y1, polyPlusRadius.x4, polyPlusRadius.y4, polyPlusRadius.x3, polyPlusRadius.y3, polyPlusRadius.x2, polyPlusRadius.y2);
                    final CCNode init2 = CCNode.alloc().init(polyPlusRadius.x1, polyPlusRadius.y1, z);
                    final CCNode init3 = CCNode.alloc().init(polyPlusRadius.x2, polyPlusRadius.y2, z);
                    final CCNode init4 = CCNode.alloc().init(polyPlusRadius.x3, polyPlusRadius.y3, z);
                    final CCNode init5 = CCNode.alloc().init(polyPlusRadius.x4, polyPlusRadius.y4, z);
                    final CCObstacle init6 = CCObstacle.alloc().init();
                    init6.vehicle = vehicle;
                    final CCEdge init7 = CCEdge.alloc().init(init2, init3, init6);
                    final CCEdge init8 = CCEdge.alloc().init(init3, init4, init6);
                    final CCEdge init9 = CCEdge.alloc().init(init4, init5, init6);
                    final CCEdge init10 = CCEdge.alloc().init(init5, init2, init6);
                    init6.outer.add(init7);
                    init6.outer.add(init8);
                    init6.outer.add(init9);
                    init6.outer.add(init10);
                    init6.calcBounds();
                    this.worldVehicleSeparate.add(init6);
                    this.nodes.add(init2);
                    this.nodes.add(init3);
                    this.nodes.add(init4);
                    this.nodes.add(init5);
                }
            }
            assert this.worldVehicleUnion.isEmpty();
            this.clipperToObstacles(clipper, xyBuffer, this.worldVehicleUnion);
        }
        
        private void getEdgesFromBuffer(final ByteBuffer byteBuffer, final CCObstacle ccObstacle, final boolean b) {
            final short short1 = byteBuffer.getShort();
            if (short1 < 3) {
                byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
                return;
            }
            CCEdgeRing outer = ccObstacle.outer;
            if (!b) {
                outer = CCEdgeRing.pool.alloc();
                outer.clear();
                ccObstacle.inner.add(outer);
            }
            final int size = this.nodes.size();
            for (short n = 0; n < short1; ++n) {
                this.nodes.add(size, CCNode.alloc().init(byteBuffer.getFloat(), byteBuffer.getFloat(), this.z));
            }
            for (int i = size; i < this.nodes.size() - 1; ++i) {
                outer.add(CCEdge.alloc().init(this.nodes.get(i), this.nodes.get(i + 1), ccObstacle));
            }
            outer.add(CCEdge.alloc().init(this.nodes.get(this.nodes.size() - 1), this.nodes.get(size), ccObstacle));
        }
        
        private void clipperToObstacles(final Clipper clipper, final ByteBuffer byteBuffer, final ArrayList<CCObstacle> list) {
            for (int generatePolygons = clipper.generatePolygons(), i = 0; i < generatePolygons; ++i) {
                byteBuffer.clear();
                clipper.getPolygon(i, byteBuffer);
                final CCObstacle init = CCObstacle.alloc().init();
                this.getEdgesFromBuffer(byteBuffer, init, true);
                for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
                    this.getEdgesFromBuffer(byteBuffer, init, false);
                }
                init.calcBounds();
                list.add(init);
            }
        }
        
        boolean isCollideW(final int n, final int n2, final int n3) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            return gridSquare != null && (gridSquare.Is(IsoFlagType.collideW) || gridSquare.hasBlockedDoor(false) || gridSquare.HasStairsNorth());
        }
        
        boolean isCollideN(final int n, final int n2, final int n3) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            return gridSquare != null && (gridSquare.Is(IsoFlagType.collideN) || gridSquare.hasBlockedDoor(true) || gridSquare.HasStairsWest());
        }
        
        boolean isOpenDoorAt(final int n, final int n2, final int n3, final boolean b) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            return gridSquare != null && gridSquare.getDoor(b) != null && !gridSquare.hasBlockedDoor(b);
        }
        
        public void clear() {
            CCNode.releaseAll(this.nodes);
            this.nodes.clear();
            CCObstacle.releaseAll(this.worldVehicleUnion);
            this.worldVehicleUnion.clear();
            CCObstacle.releaseAll(this.worldVehicleSeparate);
            this.worldVehicleSeparate.clear();
        }
        
        static {
            pool = new ObjectPool<ChunkDataZ>(ChunkDataZ::new);
        }
    }
    
    public static final class ChunkData
    {
        final ChunkDataZ[] data;
        private boolean bClear;
        
        public ChunkData() {
            this.data = new ChunkDataZ[8];
            this.bClear = false;
        }
        
        public ChunkDataZ init(final IsoChunk isoChunk, final int n, final CollideWithObstaclesPoly collideWithObstaclesPoly) {
            assert Thread.currentThread() == GameWindow.GameThread;
            if (this.bClear) {
                this.bClear = false;
                this.clearInner();
            }
            if (this.data[n] == null) {
                (this.data[n] = ChunkDataZ.pool.alloc()).init(isoChunk, n, collideWithObstaclesPoly);
            }
            return this.data[n];
        }
        
        private void clearInner() {
            PZArrayUtil.forEach(this.data, chunkDataZ -> {
                if (chunkDataZ != null) {
                    chunkDataZ.clear();
                    ChunkDataZ.pool.release(chunkDataZ);
                }
                return;
            });
            Arrays.fill(this.data, null);
        }
        
        public void clear() {
            this.bClear = true;
        }
    }
    
    private static final class ImmutableRectF
    {
        private float x;
        private float y;
        private float w;
        private float h;
        static final ArrayDeque<ImmutableRectF> pool;
        
        ImmutableRectF init(final float x, final float y, final float w, final float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            return this;
        }
        
        float left() {
            return this.x;
        }
        
        float top() {
            return this.y;
        }
        
        float right() {
            return this.x + this.w;
        }
        
        float bottom() {
            return this.y + this.h;
        }
        
        float width() {
            return this.w;
        }
        
        float height() {
            return this.h;
        }
        
        boolean containsPoint(final float n, final float n2) {
            return n >= this.left() && n < this.right() && n2 >= this.top() && n2 < this.bottom();
        }
        
        boolean intersects(final ImmutableRectF immutableRectF) {
            return this.left() < immutableRectF.right() && this.right() > immutableRectF.left() && this.top() < immutableRectF.bottom() && this.bottom() > immutableRectF.top();
        }
        
        static ImmutableRectF alloc() {
            return ImmutableRectF.pool.isEmpty() ? new ImmutableRectF() : ImmutableRectF.pool.pop();
        }
        
        void release() {
            assert !ImmutableRectF.pool.contains(this);
            ImmutableRectF.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<ImmutableRectF>();
        }
    }
    
    private static final class ClosestPointOnEdge
    {
        CCEdge edge;
        CCNode node;
        final Vector2f point;
        double distSq;
        
        private ClosestPointOnEdge() {
            this.point = new Vector2f();
        }
    }
}
