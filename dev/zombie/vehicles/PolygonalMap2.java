// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.core.utils.BooleanGrid;
import java.util.HashSet;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import java.util.Arrays;
import zombie.util.list.PZArrayUtil;
import zombie.popman.ObjectPool;
import gnu.trove.TFloatCollection;
import zombie.util.Type;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import gnu.trove.procedure.TIntObjectProcedure;
import astar.AStar;
import astar.IGoalNode;
import astar.ASearchNode;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.awt.geom.Line2D;
import org.joml.Vector2fc;
import gnu.trove.list.array.TIntArrayList;
import java.util.Collection;
import gnu.trove.list.array.TFloatArrayList;
import zombie.scripting.objects.VehicleScript;
import org.joml.Quaternionf;
import zombie.core.physics.Transform;
import org.joml.Vector3fc;
import zombie.network.GameClient;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoChunk;
import org.joml.Vector2f;
import zombie.Lua.LuaManager;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.input.GameKeyboard;
import zombie.characters.IsoGameCharacter;
import zombie.input.Mouse;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import astar.ISearchNode;
import zombie.characters.IsoPlayer;
import zombie.ai.KnownBlockedEdges;
import zombie.characters.IsoZombie;
import zombie.core.math.PZMath;
import java.util.Iterator;
import zombie.ai.astar.Mover;
import java.util.ArrayDeque;
import gnu.trove.procedure.TObjectProcedure;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.ByteBuffer;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import org.joml.Vector3f;
import zombie.iso.Vector2;

public final class PolygonalMap2
{
    public static final float RADIUS = 0.3f;
    private static final float RADIUS_DIAGONAL;
    public static final boolean CLOSE_TO_WALLS = true;
    public static final boolean PATHS_UNDER_VEHICLES = true;
    public static final boolean COLLIDE_CLIPPER = false;
    public static final boolean COLLIDE_BEVEL = false;
    public static final int CXN_FLAG_CAN_PATH = 1;
    public static final int CXN_FLAG_THUMP = 2;
    public static final int NODE_FLAG_CRAWL = 1;
    public static final int NODE_FLAG_CRAWL_INTERIOR = 2;
    public static final int NODE_FLAG_IN_CHUNK_DATA = 4;
    public static final int NODE_FLAG_PERIMETER = 8;
    public static final int NODE_FLAG_KEEP = 65536;
    private static final Vector2 temp;
    private static final Vector3f tempVec3f_1;
    private final ArrayList<VehicleCluster> clusters;
    private ClosestPointOnEdge closestPointOnEdge;
    private final TIntObjectHashMap<Node> squareToNode;
    private final ArrayList<Square> tempSquares;
    public static final PolygonalMap2 instance;
    private final ArrayList<VisibilityGraph> graphs;
    private Clipper clipperThread;
    private final ByteBuffer xyBufferThread;
    private final AdjustStartEndNodeData adjustStartData;
    private final AdjustStartEndNodeData adjustGoalData;
    private final LineClearCollide lcc;
    private final VGAStar astar;
    private final TestRequest testRequest;
    private int testZ;
    private final PathFindBehavior2.PointOnPath pointOnPath;
    private static final int SQUARES_PER_CHUNK = 10;
    private static final int LEVELS_PER_CHUNK = 8;
    private static final int SQUARES_PER_CELL = 300;
    private static final int CHUNKS_PER_CELL = 30;
    private static final int BIT_SOLID = 1;
    private static final int BIT_COLLIDE_W = 2;
    private static final int BIT_COLLIDE_N = 4;
    private static final int BIT_STAIR_TW = 8;
    private static final int BIT_STAIR_MW = 16;
    private static final int BIT_STAIR_BW = 32;
    private static final int BIT_STAIR_TN = 64;
    private static final int BIT_STAIR_MN = 128;
    private static final int BIT_STAIR_BN = 256;
    private static final int BIT_SOLID_FLOOR = 512;
    private static final int BIT_SOLID_TRANS = 1024;
    private static final int BIT_WINDOW_W = 2048;
    private static final int BIT_WINDOW_N = 4096;
    private static final int BIT_CAN_PATH_W = 8192;
    private static final int BIT_CAN_PATH_N = 16384;
    private static final int BIT_THUMP_W = 32768;
    private static final int BIT_THUMP_N = 65536;
    private static final int BIT_THUMPABLE = 131072;
    private static final int BIT_DOOR_E = 262144;
    private static final int BIT_DOOR_S = 524288;
    private static final int BIT_WINDOW_W_UNBLOCKED = 1048576;
    private static final int BIT_WINDOW_N_UNBLOCKED = 2097152;
    private static final int ALL_SOLID_BITS = 1025;
    private static final int ALL_STAIR_BITS = 504;
    private final ConcurrentLinkedQueue<IChunkTask> chunkTaskQueue;
    private final ConcurrentLinkedQueue<SquareUpdateTask> squareTaskQueue;
    private final ConcurrentLinkedQueue<IVehicleTask> vehicleTaskQueue;
    private final ArrayList<Vehicle> vehicles;
    private final HashMap<BaseVehicle, Vehicle> vehicleMap;
    private int minX;
    private int minY;
    private int width;
    private int height;
    private Cell[][] cells;
    private final HashMap<BaseVehicle, VehicleState> vehicleState;
    private final TObjectProcedure<Node> releaseNodeProc;
    private boolean rebuild;
    private final Path shortestPath;
    private final Sync sync;
    private final Object renderLock;
    private PMThread thread;
    private final ArrayDeque<PathFindRequest> requests;
    private final ConcurrentLinkedQueue<PathFindRequest> requestToMain;
    private final ConcurrentLinkedQueue<PathRequestTask> requestTaskQueue;
    private final HashMap<Mover, PathFindRequest> requestMap;
    public static final int LCC_ZERO = 0;
    public static final int LCC_IGNORE_DOORS = 1;
    public static final int LCC_CLOSE_TO_WALLS = 2;
    public static final int LCC_CHECK_COST = 4;
    public static final int LCC_RENDER = 8;
    public static final int LCC_ALLOW_ON_EDGE = 16;
    private final LineClearCollideMain lccMain;
    private final float[] tempFloats;
    private final CollideWithObstacles collideWithObstacles;
    private final CollideWithObstaclesPoly collideWithObstaclesPoly;
    
    public PolygonalMap2() {
        this.clusters = new ArrayList<VehicleCluster>();
        this.closestPointOnEdge = new ClosestPointOnEdge();
        this.squareToNode = (TIntObjectHashMap<Node>)new TIntObjectHashMap();
        this.tempSquares = new ArrayList<Square>();
        this.graphs = new ArrayList<VisibilityGraph>();
        this.xyBufferThread = ByteBuffer.allocateDirect(8192);
        this.adjustStartData = new AdjustStartEndNodeData();
        this.adjustGoalData = new AdjustStartEndNodeData();
        this.lcc = new LineClearCollide();
        this.astar = new VGAStar();
        this.testRequest = new TestRequest();
        this.testZ = 0;
        this.pointOnPath = new PathFindBehavior2.PointOnPath();
        this.chunkTaskQueue = new ConcurrentLinkedQueue<IChunkTask>();
        this.squareTaskQueue = new ConcurrentLinkedQueue<SquareUpdateTask>();
        this.vehicleTaskQueue = new ConcurrentLinkedQueue<IVehicleTask>();
        this.vehicles = new ArrayList<Vehicle>();
        this.vehicleMap = new HashMap<BaseVehicle, Vehicle>();
        this.vehicleState = new HashMap<BaseVehicle, VehicleState>();
        this.releaseNodeProc = (TObjectProcedure<Node>)new TObjectProcedure<Node>() {
            public boolean execute(final Node node) {
                node.release();
                return true;
            }
        };
        this.shortestPath = new Path();
        this.sync = new Sync();
        this.renderLock = new Object();
        this.requests = new ArrayDeque<PathFindRequest>();
        this.requestToMain = new ConcurrentLinkedQueue<PathFindRequest>();
        this.requestTaskQueue = new ConcurrentLinkedQueue<PathRequestTask>();
        this.requestMap = new HashMap<Mover, PathFindRequest>();
        this.lccMain = new LineClearCollideMain();
        this.tempFloats = new float[8];
        this.collideWithObstacles = new CollideWithObstacles();
        this.collideWithObstaclesPoly = new CollideWithObstaclesPoly();
    }
    
    private void createVehicleCluster(final VehicleRect e, final ArrayList<VehicleRect> list, final ArrayList<VehicleCluster> list2) {
        for (int i = 0; i < list.size(); ++i) {
            final VehicleRect vehicleRect = list.get(i);
            if (e != vehicleRect) {
                if (e.z == vehicleRect.z) {
                    if (e.cluster == null || e.cluster != vehicleRect.cluster) {
                        if (e.isAdjacent(vehicleRect)) {
                            if (e.cluster != null) {
                                if (vehicleRect.cluster == null) {
                                    vehicleRect.cluster = e.cluster;
                                    vehicleRect.cluster.rects.add(vehicleRect);
                                }
                                else {
                                    list2.remove(vehicleRect.cluster);
                                    e.cluster.merge(vehicleRect.cluster);
                                }
                            }
                            else if (vehicleRect.cluster != null) {
                                if (e.cluster == null) {
                                    e.cluster = vehicleRect.cluster;
                                    e.cluster.rects.add(e);
                                }
                                else {
                                    list2.remove(e.cluster);
                                    vehicleRect.cluster.merge(e.cluster);
                                }
                            }
                            else {
                                final VehicleCluster init = VehicleCluster.alloc().init();
                                e.cluster = init;
                                vehicleRect.cluster = init;
                                init.rects.add(e);
                                init.rects.add(vehicleRect);
                                list2.add(init);
                            }
                        }
                    }
                }
            }
        }
        if (e.cluster == null) {
            final VehicleCluster init2 = VehicleCluster.alloc().init();
            e.cluster = init2;
            init2.rects.add(e);
            list2.add(init2);
        }
    }
    
    private void createVehicleClusters() {
        this.clusters.clear();
        final ArrayList<VehicleRect> list = new ArrayList<VehicleRect>();
        for (int i = 0; i < this.vehicles.size(); ++i) {
            final Vehicle vehicle = this.vehicles.get(i);
            final VehicleRect alloc = VehicleRect.alloc();
            vehicle.polyPlusRadius.getAABB(alloc);
            alloc.vehicle = vehicle;
            list.add(alloc);
        }
        if (list.isEmpty()) {
            return;
        }
        for (int j = 0; j < list.size(); ++j) {
            this.createVehicleCluster(list.get(j), list, this.clusters);
        }
    }
    
    private Node getNodeForSquare(final Square square) {
        Node init = (Node)this.squareToNode.get((int)square.ID);
        if (init == null) {
            init = Node.alloc().init(square);
            this.squareToNode.put((int)square.ID, (Object)init);
        }
        return init;
    }
    
    private VisibilityGraph getVisGraphAt(final float n, final float n2, final int n3) {
        for (int i = 0; i < this.graphs.size(); ++i) {
            final VisibilityGraph visibilityGraph = this.graphs.get(i);
            if (visibilityGraph.contains(n, n2, n3)) {
                return visibilityGraph;
            }
        }
        return null;
    }
    
    private VisibilityGraph getVisGraphAt(final float n, final float n2, final int n3, final int n4) {
        for (int i = 0; i < this.graphs.size(); ++i) {
            final VisibilityGraph visibilityGraph = this.graphs.get(i);
            if (visibilityGraph.contains(n, n2, n3, n4)) {
                return visibilityGraph;
            }
        }
        return null;
    }
    
    private VisibilityGraph getVisGraphForSquare(final Square square) {
        for (int i = 0; i < this.graphs.size(); ++i) {
            final VisibilityGraph visibilityGraph = this.graphs.get(i);
            if (visibilityGraph.contains(square)) {
                return visibilityGraph;
            }
        }
        return null;
    }
    
    private Connection connectTwoNodes(final Node node, final Node node2, final int n) {
        final Connection init = Connection.alloc().init(node, node2, n);
        node.visible.add(init);
        node2.visible.add(init);
        return init;
    }
    
    private Connection connectTwoNodes(final Node node, final Node node2) {
        return this.connectTwoNodes(node, node2, 0);
    }
    
    private void breakConnection(final Connection connection) {
        connection.node1.visible.remove(connection);
        connection.node2.visible.remove(connection);
        connection.release();
    }
    
    private void breakConnection(final Node node, final Node node2) {
        for (int i = 0; i < node.visible.size(); ++i) {
            final Connection connection = node.visible.get(i);
            if (connection.otherNode(node) == node2) {
                this.breakConnection(connection);
                break;
            }
        }
    }
    
    private void addStairNodes() {
        final ArrayList<Square> tempSquares = this.tempSquares;
        tempSquares.clear();
        for (int i = 0; i < this.graphs.size(); ++i) {
            this.graphs.get(i).getStairSquares(tempSquares);
        }
        for (int j = 0; j < tempSquares.size(); ++j) {
            final Square square = tempSquares.get(j);
            Square square2 = null;
            Square square3 = null;
            Square square4 = null;
            Square square5 = null;
            Square square6 = null;
            if (square.has(8)) {
                square2 = this.getSquare(square.x - 1, square.y, square.z + 1);
                square3 = square;
                square4 = this.getSquare(square.x + 1, square.y, square.z);
                square5 = this.getSquare(square.x + 2, square.y, square.z);
                square6 = this.getSquare(square.x + 3, square.y, square.z);
            }
            if (square.has(64)) {
                square2 = this.getSquare(square.x, square.y - 1, square.z + 1);
                square3 = square;
                square4 = this.getSquare(square.x, square.y + 1, square.z);
                square5 = this.getSquare(square.x, square.y + 2, square.z);
                square6 = this.getSquare(square.x, square.y + 3, square.z);
            }
            if (square2 != null && square3 != null && square4 != null && square5 != null && square6 != null) {
                final VisibilityGraph visGraphForSquare = this.getVisGraphForSquare(square2);
                Node node;
                if (visGraphForSquare == null) {
                    node = this.getNodeForSquare(square2);
                }
                else {
                    node = Node.alloc().init(square2);
                    final Iterator<Obstacle> iterator = visGraphForSquare.obstacles.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().isNodeInsideOf(node)) {
                            node.ignore = true;
                        }
                    }
                    node.addGraph(visGraphForSquare);
                    visGraphForSquare.addNode(node);
                    this.squareToNode.put((int)square2.ID, (Object)node);
                }
                final VisibilityGraph visGraphForSquare2 = this.getVisGraphForSquare(square6);
                Node node2;
                if (visGraphForSquare2 == null) {
                    node2 = this.getNodeForSquare(square6);
                }
                else {
                    node2 = Node.alloc().init(square6);
                    final Iterator<Obstacle> iterator2 = visGraphForSquare2.obstacles.iterator();
                    while (iterator2.hasNext()) {
                        if (iterator2.next().isNodeInsideOf(node2)) {
                            node2.ignore = true;
                        }
                    }
                    node2.addGraph(visGraphForSquare2);
                    visGraphForSquare2.addNode(node2);
                    this.squareToNode.put((int)square6.ID, (Object)node2);
                }
                if (node != null && node2 != null) {
                    final Node nodeForSquare = this.getNodeForSquare(square3);
                    final Node nodeForSquare2 = this.getNodeForSquare(square4);
                    final Node nodeForSquare3 = this.getNodeForSquare(square5);
                    this.connectTwoNodes(node, nodeForSquare);
                    this.connectTwoNodes(nodeForSquare, nodeForSquare2);
                    this.connectTwoNodes(nodeForSquare2, nodeForSquare3);
                    this.connectTwoNodes(nodeForSquare3, node2);
                }
            }
        }
    }
    
    private void addCanPathNodes() {
        final ArrayList<Square> tempSquares = this.tempSquares;
        tempSquares.clear();
        for (int i = 0; i < this.graphs.size(); ++i) {
            this.graphs.get(i).getCanPathSquares(tempSquares);
        }
        for (int j = 0; j < tempSquares.size(); ++j) {
            final Square square = tempSquares.get(j);
            if (!square.isNonThumpableSolid() && !square.has(504)) {
                if (square.has(512)) {
                    if (square.isCanPathW()) {
                        final Square square2 = this.getSquare(square.x - 1, square.y, square.z);
                        if (square2 != null && !square2.isNonThumpableSolid() && !square2.has(504) && square2.has(512)) {
                            final Node orCreateCanPathNode = this.getOrCreateCanPathNode(square);
                            final Node orCreateCanPathNode2 = this.getOrCreateCanPathNode(square2);
                            int n = 1;
                            if (square.has(163840) || square2.has(131072)) {
                                n |= 0x2;
                            }
                            this.connectTwoNodes(orCreateCanPathNode, orCreateCanPathNode2, n);
                        }
                    }
                    if (square.isCanPathN()) {
                        final Square square3 = this.getSquare(square.x, square.y - 1, square.z);
                        if (square3 != null && !square3.isNonThumpableSolid() && !square3.has(504) && square3.has(512)) {
                            final Node orCreateCanPathNode3 = this.getOrCreateCanPathNode(square);
                            final Node orCreateCanPathNode4 = this.getOrCreateCanPathNode(square3);
                            int n2 = 1;
                            if (square.has(196608) || square3.has(131072)) {
                                n2 |= 0x2;
                            }
                            this.connectTwoNodes(orCreateCanPathNode3, orCreateCanPathNode4, n2);
                        }
                    }
                }
            }
        }
    }
    
    private Node getOrCreateCanPathNode(final Square square) {
        final VisibilityGraph visGraphForSquare = this.getVisGraphForSquare(square);
        final Node nodeForSquare = this.getNodeForSquare(square);
        if (visGraphForSquare != null) {
            if (!visGraphForSquare.nodes.contains(nodeForSquare)) {
                final Iterator<Obstacle> iterator = visGraphForSquare.obstacles.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().isNodeInsideOf(nodeForSquare)) {
                        nodeForSquare.ignore = true;
                        break;
                    }
                }
                visGraphForSquare.addNode(nodeForSquare);
            }
        }
        return nodeForSquare;
    }
    
    private Node getPointOutsideObjects(final Square square, final float n, final float n2) {
        final Square square2 = PolygonalMap2.instance.getSquare(square.x - 1, square.y, square.z);
        final Square square3 = PolygonalMap2.instance.getSquare(square.x - 1, square.y - 1, square.z);
        final Square square4 = PolygonalMap2.instance.getSquare(square.x, square.y - 1, square.z);
        final Square square5 = PolygonalMap2.instance.getSquare(square.x + 1, square.y - 1, square.z);
        final Square square6 = PolygonalMap2.instance.getSquare(square.x + 1, square.y, square.z);
        final Square square7 = PolygonalMap2.instance.getSquare(square.x + 1, square.y + 1, square.z);
        final Square square8 = PolygonalMap2.instance.getSquare(square.x, square.y + 1, square.z);
        final Square square9 = PolygonalMap2.instance.getSquare(square.x - 1, square.y + 1, square.z);
        float n3 = (float)square.x;
        float n4 = (float)square.y;
        float n5 = (float)(square.x + 1);
        float n6 = (float)(square.y + 1);
        if (square.isCollideW()) {
            n3 += 0.35000002f;
        }
        if (square.isCollideN()) {
            n4 += 0.35000002f;
        }
        if (square6 != null && (square6.has(2) || square6.has(504) || square6.isReallySolid())) {
            n5 -= 0.35000002f;
        }
        if (square8 != null && (square8.has(4) || square8.has(504) || square8.isReallySolid())) {
            n6 -= 0.35000002f;
        }
        float clamp = PZMath.clamp(n, n3, n5);
        float clamp2 = PZMath.clamp(n2, n4, n6);
        if (clamp <= square.x + 0.3f && clamp2 <= square.y + 0.3f && ((square3 != null && (square3.has(504) || square3.isReallySolid())) | (square4 != null && square4.has(2)) | (square2 != null && square2.has(4)))) {
            final float n7 = square.x + 0.3f + 0.05f;
            final float n8 = square.y + 0.3f + 0.05f;
            if (n7 - clamp <= n8 - clamp2) {
                clamp = n7;
            }
            else {
                clamp2 = n8;
            }
        }
        if (clamp >= square.x + 1 - 0.3f && clamp2 <= square.y + 0.3f && ((square5 != null && (square5.has(2) || square5.has(504) || square5.isReallySolid())) | (square6 != null && square6.has(4)))) {
            final float n9 = square.x + 1 - 0.3f - 0.05f;
            final float n10 = square.y + 0.3f + 0.05f;
            if (clamp - n9 <= n10 - clamp2) {
                clamp = n9;
            }
            else {
                clamp2 = n10;
            }
        }
        if (clamp <= square.x + 0.3f && clamp2 >= square.y + 1 - 0.3f && ((square9 != null && (square9.has(4) || square9.has(504) || square9.isReallySolid())) | (square8 != null && square8.has(2)))) {
            final float n11 = square.x + 0.3f + 0.05f;
            final float n12 = square.y + 1 - 0.3f - 0.05f;
            if (n11 - clamp <= clamp2 - n12) {
                clamp = n11;
            }
            else {
                clamp2 = n12;
            }
        }
        if (clamp >= square.x + 1 - 0.3f && clamp2 >= square.y + 1 - 0.3f && (square7 != null && (square7.has(2) || square7.has(4) || square7.has(504) || square7.isReallySolid()))) {
            final float n13 = square.x + 1 - 0.3f - 0.05f;
            final float n14 = square.y + 1 - 0.3f - 0.05f;
            if (clamp - n13 <= clamp2 - n14) {
                clamp = n13;
            }
            else {
                clamp2 = n14;
            }
        }
        return Node.alloc().init(clamp, clamp2, square.z);
    }
    
    private void createVisibilityGraph(final VehicleCluster vehicleCluster) {
        final VisibilityGraph init = VisibilityGraph.alloc().init(vehicleCluster);
        init.addPerimeterEdges();
        this.graphs.add(init);
    }
    
    private void createVisibilityGraphs() {
        this.createVehicleClusters();
        this.graphs.clear();
        this.squareToNode.clear();
        for (int i = 0; i < this.clusters.size(); ++i) {
            this.createVisibilityGraph(this.clusters.get(i));
        }
        this.addStairNodes();
        this.addCanPathNodes();
    }
    
    private boolean findPath(final PathFindRequest pathFindRequest, final boolean b) {
        int n = 16;
        if (!(pathFindRequest.mover instanceof IsoZombie)) {
            n |= 0x4;
        }
        if ((int)pathFindRequest.startZ == (int)pathFindRequest.targetZ && !this.lcc.isNotClear(this, pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.targetX, pathFindRequest.targetY, (int)pathFindRequest.startZ, n)) {
            pathFindRequest.path.addNode(pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.startZ);
            pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ);
            if (b) {
                final Iterator<VisibilityGraph> iterator = this.graphs.iterator();
                while (iterator.hasNext()) {
                    iterator.next().render();
                }
            }
            return true;
        }
        this.astar.init(this.graphs, this.squareToNode);
        this.astar.knownBlockedEdges.clear();
        for (int i = 0; i < pathFindRequest.knownBlockedEdges.size(); ++i) {
            final KnownBlockedEdges knownBlockedEdges = pathFindRequest.knownBlockedEdges.get(i);
            final Square square = this.getSquare(knownBlockedEdges.x, knownBlockedEdges.y, knownBlockedEdges.z);
            if (square != null) {
                this.astar.knownBlockedEdges.put((int)square.ID, (Object)knownBlockedEdges);
            }
        }
        VisibilityGraph visibilityGraph = null;
        VisibilityGraph visibilityGraph2 = null;
        SearchNode searchNode = null;
        SearchNode searchNode2 = null;
        boolean b2 = false;
        boolean b3 = false;
        try {
            final Square square2 = this.getSquare((int)pathFindRequest.startX, (int)pathFindRequest.startY, (int)pathFindRequest.startZ);
            if (square2 == null || square2.isReallySolid()) {
                return false;
            }
            if (square2.has(504)) {
                searchNode = this.astar.getSearchNode(square2);
            }
            else {
                final VisibilityGraph visGraphForSquare = this.astar.getVisGraphForSquare(square2);
                if (visGraphForSquare != null) {
                    if (!visGraphForSquare.created) {
                        visGraphForSquare.create();
                    }
                    Node node = null;
                    final int pointOutsideObstacles = visGraphForSquare.getPointOutsideObstacles(pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.startZ, this.adjustStartData);
                    if (pointOutsideObstacles == -1) {
                        return false;
                    }
                    if (pointOutsideObstacles == 1) {
                        b2 = true;
                        node = this.adjustStartData.node;
                        if (this.adjustStartData.isNodeNew) {
                            visibilityGraph = visGraphForSquare;
                        }
                    }
                    if (node == null) {
                        node = Node.alloc().init(pathFindRequest.startX, pathFindRequest.startY, (int)pathFindRequest.startZ);
                        visGraphForSquare.addNode(node);
                        visibilityGraph = visGraphForSquare;
                    }
                    searchNode = this.astar.getSearchNode(node);
                }
            }
            if (searchNode == null) {
                searchNode = this.astar.getSearchNode(square2);
            }
            if (pathFindRequest.targetX < 0.0f || pathFindRequest.targetY < 0.0f || this.getChunkFromSquarePos((int)pathFindRequest.targetX, (int)pathFindRequest.targetY) == null) {
                searchNode2 = this.astar.getSearchNode((int)pathFindRequest.targetX, (int)pathFindRequest.targetY);
            }
            else {
                final Square square3 = this.getSquare((int)pathFindRequest.targetX, (int)pathFindRequest.targetY, (int)pathFindRequest.targetZ);
                if (square3 == null || square3.isReallySolid()) {
                    return false;
                }
                if (((int)pathFindRequest.startX != (int)pathFindRequest.targetX || (int)pathFindRequest.startY != (int)pathFindRequest.targetY || (int)pathFindRequest.startZ != (int)pathFindRequest.targetZ) && this.isBlockedInAllDirections((int)pathFindRequest.targetX, (int)pathFindRequest.targetY, (int)pathFindRequest.targetZ)) {
                    return false;
                }
                if (square3.has(504)) {
                    searchNode2 = this.astar.getSearchNode(square3);
                }
                else {
                    final VisibilityGraph visGraphForSquare2 = this.astar.getVisGraphForSquare(square3);
                    if (visGraphForSquare2 != null) {
                        if (!visGraphForSquare2.created) {
                            visGraphForSquare2.create();
                        }
                        Node node2 = null;
                        final int pointOutsideObstacles2 = visGraphForSquare2.getPointOutsideObstacles(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, this.adjustGoalData);
                        if (pointOutsideObstacles2 == -1) {
                            return false;
                        }
                        if (pointOutsideObstacles2 == 1) {
                            b3 = true;
                            node2 = this.adjustGoalData.node;
                            if (this.adjustGoalData.isNodeNew) {
                                visibilityGraph2 = visGraphForSquare2;
                            }
                        }
                        if (node2 == null) {
                            node2 = Node.alloc().init(pathFindRequest.targetX, pathFindRequest.targetY, (int)pathFindRequest.targetZ);
                            visGraphForSquare2.addNode(node2);
                            visibilityGraph2 = visGraphForSquare2;
                        }
                        searchNode2 = this.astar.getSearchNode(node2);
                    }
                    else {
                        for (int j = 0; j < this.graphs.size(); ++j) {
                            final VisibilityGraph visibilityGraph3 = this.graphs.get(j);
                            if (visibilityGraph3.contains(square3, 1)) {
                                final Node pointOutsideObjects = this.getPointOutsideObjects(square3, pathFindRequest.targetX, pathFindRequest.targetY);
                                visibilityGraph3.addNode(pointOutsideObjects);
                                if (pointOutsideObjects.x != pathFindRequest.targetX || pointOutsideObjects.y != pathFindRequest.targetY) {
                                    b3 = true;
                                    this.adjustGoalData.isNodeNew = false;
                                }
                                visibilityGraph2 = visibilityGraph3;
                                searchNode2 = this.astar.getSearchNode(pointOutsideObjects);
                                break;
                            }
                        }
                    }
                }
                if (searchNode2 == null) {
                    searchNode2 = this.astar.getSearchNode(square3);
                }
            }
            final ArrayList<ISearchNode> shortestPath = this.astar.shortestPath(pathFindRequest, searchNode, searchNode2);
            if (shortestPath != null) {
                if (shortestPath.size() == 1) {
                    pathFindRequest.path.addNode(searchNode);
                    final SearchNode searchNode3 = searchNode2;
                    if (!b3 && searchNode3.square != null && searchNode3.square.x + 0.5f != pathFindRequest.targetX && searchNode2.square.y + 0.5f != pathFindRequest.targetY) {
                        pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, 0);
                    }
                    else {
                        pathFindRequest.path.addNode(searchNode3);
                    }
                    return true;
                }
                this.cleanPath(shortestPath, pathFindRequest, b2, b3, searchNode2);
                if (pathFindRequest.mover instanceof IsoPlayer && !((IsoPlayer)pathFindRequest.mover).isNPC()) {
                    this.smoothPath(pathFindRequest.path);
                }
                return true;
            }
        }
        finally {
            if (b) {
                final Iterator<VisibilityGraph> iterator2 = this.graphs.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().render();
                }
            }
            if (visibilityGraph != null) {
                visibilityGraph.removeNode(searchNode.vgNode);
            }
            if (visibilityGraph2 != null) {
                visibilityGraph2.removeNode(searchNode2.vgNode);
            }
            for (int k = 0; k < this.astar.searchNodes.size(); ++k) {
                this.astar.searchNodes.get(k).release();
            }
            if (b2 && this.adjustStartData.isNodeNew) {
                for (int l = 0; l < this.adjustStartData.node.edges.size(); ++l) {
                    final Edge edge = this.adjustStartData.node.edges.get(l);
                    edge.obstacle.unsplit(this.adjustStartData.node, edge.edgeRing);
                }
                this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
            }
            if (b3 && this.adjustGoalData.isNodeNew) {
                for (int index = 0; index < this.adjustGoalData.node.edges.size(); ++index) {
                    final Edge edge2 = this.adjustGoalData.node.edges.get(index);
                    edge2.obstacle.unsplit(this.adjustGoalData.node, edge2.edgeRing);
                }
                this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
            }
        }
        return false;
    }
    
    private void cleanPath(final ArrayList<ISearchNode> list, final PathFindRequest pathFindRequest, final boolean b, final boolean b2, final SearchNode searchNode) {
        final boolean b3 = pathFindRequest.mover instanceof IsoPlayer && ((IsoPlayer)pathFindRequest.mover).isNPC();
        Square square = null;
        int n = -123;
        int n2 = -123;
        for (int i = 0; i < list.size(); ++i) {
            final SearchNode searchNode2 = (SearchNode)list.get(i);
            final float x = searchNode2.getX();
            final float y = searchNode2.getY();
            final float z = searchNode2.getZ();
            int n3 = (searchNode2.vgNode == null) ? 0 : searchNode2.vgNode.flags;
            final Square square2 = searchNode2.square;
            boolean b4 = false;
            if (square2 != null && square != null && square2.z == square.z) {
                final int n4 = square2.x - square.x;
                final int n5 = square2.y - square.y;
                if (n4 == n && n5 == n2) {
                    if (pathFindRequest.path.nodes.size() > 1) {
                        b4 = true;
                        if (pathFindRequest.path.getLastNode().hasFlag(65536)) {
                            b4 = false;
                        }
                    }
                    if (n4 == 0 && n5 == -1 && square.has(16384)) {
                        b4 = false;
                    }
                    else if (n4 == 0 && n5 == 1 && square2.has(16384)) {
                        b4 = false;
                    }
                    else if (n4 == -1 && n5 == 0 && square.has(8192)) {
                        b4 = false;
                    }
                    else if (n4 == 1 && n5 == 0 && square2.has(8192)) {
                        b4 = false;
                    }
                }
                else {
                    n = n4;
                    n2 = n5;
                }
            }
            else {
                n2 = (n = -123);
            }
            if (square2 != null) {
                square = square2;
            }
            else {
                square = null;
            }
            if (b3) {
                b4 = false;
            }
            if (b4) {
                final PathNode lastNode = pathFindRequest.path.getLastNode();
                lastNode.x = square2.x + 0.5f;
                lastNode.y = square2.y + 0.5f;
            }
            else {
                if (pathFindRequest.path.nodes.size() > 1) {
                    final PathNode lastNode2 = pathFindRequest.path.getLastNode();
                    if (Math.abs(lastNode2.x - x) < 0.01f && Math.abs(lastNode2.y - y) < 0.01f && Math.abs(lastNode2.z - z) < 0.01f) {
                        lastNode2.x = x;
                        lastNode2.y = y;
                        lastNode2.z = z;
                        continue;
                    }
                }
                if (i > 0 && searchNode2.square != null) {
                    final SearchNode searchNode3 = (SearchNode)list.get(i - 1);
                    if (searchNode3.square != null) {
                        final int n6 = searchNode2.square.x - searchNode3.square.x;
                        final int n7 = searchNode2.square.y - searchNode3.square.y;
                        if (n6 == 0 && n7 == -1 && searchNode3.square.has(16384)) {
                            n3 |= 0x10000;
                        }
                        else if (n6 == 0 && n7 == 1 && searchNode2.square.has(16384)) {
                            n3 |= 0x10000;
                        }
                        else if (n6 == -1 && n7 == 0 && searchNode3.square.has(8192)) {
                            n3 |= 0x10000;
                        }
                        else if (n6 == 1 && n7 == 0 && searchNode2.square.has(8192)) {
                            n3 |= 0x10000;
                        }
                    }
                }
                pathFindRequest.path.addNode(x, y, z, n3);
            }
        }
        if (pathFindRequest.mover instanceof IsoPlayer && !b3) {
            final PathNode pathNode = pathFindRequest.path.isEmpty() ? null : pathFindRequest.path.getNode(0);
            if (!b2 && searchNode.square != null && IsoUtils.DistanceToSquared(searchNode.square.x + 0.5f, searchNode.square.y + 0.5f, pathFindRequest.targetX, pathFindRequest.targetY) > 0.010000000000000002) {
                pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, 0);
            }
        }
        PathNode pathNode2 = null;
        for (int j = 0; j < pathFindRequest.path.nodes.size(); ++j) {
            final PathNode pathNode3 = pathFindRequest.path.nodes.get(j);
            final PathNode pathNode4 = (j < pathFindRequest.path.nodes.size() - 1) ? pathFindRequest.path.nodes.get(j + 1) : null;
            if (pathNode3.hasFlag(1) && (pathNode2 == null || !pathNode2.hasFlag(2)) && (pathNode4 == null || !pathNode4.hasFlag(2))) {
                final PathNode pathNode5 = pathNode3;
                pathNode5.flags &= 0xFFFFFFFC;
            }
            pathNode2 = pathNode3;
        }
    }
    
    private void smoothPath(final Path path) {
        int i = 0;
        while (i < path.nodes.size() - 2) {
            final PathNode pathNode = path.nodes.get(i);
            final PathNode e = path.nodes.get(i + 1);
            final PathNode pathNode2 = path.nodes.get(i + 2);
            if ((int)pathNode.z != (int)e.z || (int)pathNode.z != (int)pathNode2.z) {
                ++i;
            }
            else if (!this.lcc.isNotClear(this, pathNode.x, pathNode.y, pathNode2.x, pathNode2.y, (int)pathNode.z, 20)) {
                path.nodes.remove(i + 1);
                path.nodePool.push(e);
            }
            else {
                ++i;
            }
        }
    }
    
    float getApparentZ(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.Has(IsoObjectType.stairsTW) || isoGridSquare.Has(IsoObjectType.stairsTN)) {
            return isoGridSquare.z + 0.75f;
        }
        if (isoGridSquare.Has(IsoObjectType.stairsMW) || isoGridSquare.Has(IsoObjectType.stairsMN)) {
            return isoGridSquare.z + 0.5f;
        }
        if (isoGridSquare.Has(IsoObjectType.stairsBW) || isoGridSquare.Has(IsoObjectType.stairsBN)) {
            return isoGridSquare.z + 0.25f;
        }
        return (float)isoGridSquare.z;
    }
    
    public void render() {
        if (!Core.bDebug) {
            return;
        }
        final boolean b = DebugOptions.instance.PathfindPathToMouseEnable.getValue() && !this.testRequest.done && IsoPlayer.getInstance().getPath2() == null;
        if (DebugOptions.instance.PolymapRenderClusters.getValue()) {
            synchronized (this.renderLock) {
                for (final VehicleCluster vehicleCluster : this.clusters) {
                    for (final VehicleRect vehicleRect : vehicleCluster.rects) {
                        LineDrawer.addLine((float)vehicleRect.x, (float)vehicleRect.y, (float)vehicleRect.z, (float)vehicleRect.right(), (float)vehicleRect.bottom(), (float)vehicleRect.z, 0.0f, 0.0f, 1.0f, null, false);
                    }
                    vehicleCluster.bounds().release();
                }
                if (!b) {
                    final Iterator<VisibilityGraph> iterator3 = this.graphs.iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().render();
                    }
                }
            }
        }
        if (DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
            final float n = (float)Mouse.getX();
            final float n2 = (float)Mouse.getY();
            final int n3 = (int)IsoPlayer.getInstance().getZ();
            final float xToIso = IsoUtils.XToIso(n, n2, (float)n3);
            final float yToIso = IsoUtils.YToIso(n, n2, (float)n3);
            LineDrawer.addLine(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, (float)n3, xToIso, yToIso, (float)n3, 1, 1, 1, null);
            if (this.lccMain.isNotClear(this, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, xToIso, yToIso, n3, null, 0x9 | 0x2)) {
                final Vector2f resolveCollision = this.resolveCollision(IsoPlayer.getInstance(), xToIso, yToIso, L_render.vector2f);
                LineDrawer.addLine(resolveCollision.x - 0.05f, resolveCollision.y - 0.05f, (float)n3, resolveCollision.x + 0.05f, resolveCollision.y + 0.05f, (float)n3, 1.0f, 1.0f, 0.0f, null, false);
            }
        }
        if (GameKeyboard.isKeyDown(209) && !GameKeyboard.wasKeyDown(209)) {
            this.testZ = Math.max(this.testZ - 1, 0);
        }
        if (GameKeyboard.isKeyDown(201) && !GameKeyboard.wasKeyDown(201)) {
            this.testZ = Math.min(this.testZ + 1, 7);
        }
        if (b) {
            final float n4 = (float)Mouse.getX();
            final float n5 = (float)Mouse.getY();
            final int testZ = this.testZ;
            final float xToIso2 = IsoUtils.XToIso(n4, n5, (float)testZ);
            final float yToIso2 = IsoUtils.YToIso(n4, n5, (float)testZ);
            final float f = (float)testZ;
            for (int i = -1; i <= 2; ++i) {
                LineDrawer.addLine((float)((int)xToIso2 - 1), (float)((int)yToIso2 + i), (float)(int)f, (float)((int)xToIso2 + 2), (float)((int)yToIso2 + i), (float)(int)f, 0.3f, 0.3f, 0.3f, null, false);
            }
            for (int j = -1; j <= 2; ++j) {
                LineDrawer.addLine((float)((int)xToIso2 + j), (float)((int)yToIso2 - 1), (float)(int)f, (float)((int)xToIso2 + j), (float)((int)yToIso2 + 2), (float)(int)f, 0.3f, 0.3f, 0.3f, null, false);
            }
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    final float n6 = 0.3f;
                    final float n7 = 0.0f;
                    final float n8 = 0.0f;
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)xToIso2 + l, (int)yToIso2 + k, (int)f);
                    if (gridSquare == null || gridSquare.isSolid() || gridSquare.isSolidTrans() || gridSquare.HasStairs()) {
                        LineDrawer.addLine((float)((int)xToIso2 + l), (float)((int)yToIso2 + k), (float)(int)f, (float)((int)xToIso2 + l + 1), (float)((int)yToIso2 + k + 1), (float)(int)f, n6, n7, n8, null, false);
                    }
                }
            }
            if (testZ < (int)IsoPlayer.getInstance().getZ()) {
                LineDrawer.addLine((float)(int)xToIso2, (float)(int)yToIso2, (float)(int)f, (float)(int)xToIso2, (float)(int)yToIso2, (float)(int)IsoPlayer.getInstance().getZ(), 0.3f, 0.3f, 0.3f, null, true);
            }
            else if (testZ > (int)IsoPlayer.getInstance().getZ()) {
                LineDrawer.addLine((float)(int)xToIso2, (float)(int)yToIso2, (float)(int)f, (float)(int)xToIso2, (float)(int)yToIso2, (float)(int)IsoPlayer.getInstance().getZ(), 0.3f, 0.3f, 0.3f, null, true);
            }
            final PathFindRequest init = PathFindRequest.alloc().init(this.testRequest, IsoPlayer.getInstance(), IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, xToIso2, yToIso2, f);
            if (DebugOptions.instance.PathfindPathToMouseAllowCrawl.getValue()) {
                init.bCanCrawl = true;
                if (DebugOptions.instance.PathfindPathToMouseIgnoreCrawlCost.getValue()) {
                    init.bIgnoreCrawlCost = true;
                }
            }
            if (DebugOptions.instance.PathfindPathToMouseAllowThump.getValue()) {
                init.bCanThump = true;
            }
            this.testRequest.done = false;
            synchronized (this.renderLock) {
                if (this.findPath(init, DebugOptions.instance.PolymapRenderClusters.getValue()) && !init.path.isEmpty()) {
                    for (int index = 0; index < init.path.nodes.size() - 1; ++index) {
                        final PathNode pathNode = init.path.nodes.get(index);
                        final PathNode pathNode2 = init.path.nodes.get(index + 1);
                        final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(pathNode.x, pathNode.y, pathNode.z);
                        final IsoGridSquare gridSquare3 = IsoWorld.instance.CurrentCell.getGridSquare(pathNode2.x, pathNode2.y, pathNode2.z);
                        final float n9 = (gridSquare2 == null) ? pathNode.z : this.getApparentZ(gridSquare2);
                        final float n10 = (gridSquare3 == null) ? pathNode2.z : this.getApparentZ(gridSquare3);
                        final float n11 = 1.0f;
                        float n12 = 1.0f;
                        final float n13 = 0.0f;
                        if (n9 != (int)n9 || n10 != (int)n10) {
                            n12 = 0.0f;
                        }
                        LineDrawer.addLine(pathNode.x, pathNode.y, n9, pathNode2.x, pathNode2.y, n10, n11, n12, n13, null, true);
                        LineDrawer.addRect(pathNode.x - 0.05f, pathNode.y - 0.05f, n9, 0.1f, 0.1f, n11, n12, n13);
                    }
                    PathFindBehavior2.closestPointOnPath(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, IsoPlayer.getInstance(), init.path, this.pointOnPath);
                    final PathNode pathNode3 = init.path.nodes.get(this.pointOnPath.pathIndex);
                    final PathNode pathNode4 = init.path.nodes.get(this.pointOnPath.pathIndex + 1);
                    final IsoGridSquare gridSquare4 = IsoWorld.instance.CurrentCell.getGridSquare(pathNode3.x, pathNode3.y, pathNode3.z);
                    final IsoGridSquare gridSquare5 = IsoWorld.instance.CurrentCell.getGridSquare(pathNode4.x, pathNode4.y, pathNode4.z);
                    final float n14 = (gridSquare4 == null) ? pathNode3.z : this.getApparentZ(gridSquare4);
                    final float n15 = n14 + (((gridSquare5 == null) ? pathNode4.z : this.getApparentZ(gridSquare5)) - n14) * this.pointOnPath.dist;
                    LineDrawer.addLine(this.pointOnPath.x - 0.05f, this.pointOnPath.y - 0.05f, n15, this.pointOnPath.x + 0.05f, this.pointOnPath.y + 0.05f, n15, 0.0f, 1.0f, 0.0f, null, true);
                    LineDrawer.addLine(this.pointOnPath.x - 0.05f, this.pointOnPath.y + 0.05f, n15, this.pointOnPath.x + 0.05f, this.pointOnPath.y - 0.05f, n15, 0.0f, 1.0f, 0.0f, null, true);
                    if (GameKeyboard.isKeyDown(207) && !GameKeyboard.wasKeyDown(207)) {
                        final Object rawget = LuaManager.env.rawget((Object)"ISPathFindAction_pathToLocationF");
                        if (rawget != null) {
                            LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { xToIso2, yToIso2, f });
                        }
                    }
                }
                init.release();
            }
        }
        else {
            for (int index2 = 0; index2 < this.testRequest.path.nodes.size() - 1; ++index2) {
                final PathNode pathNode5 = this.testRequest.path.nodes.get(index2);
                final PathNode pathNode6 = this.testRequest.path.nodes.get(index2 + 1);
                final float n16 = 1.0f;
                float n17 = 1.0f;
                final float n18 = 0.0f;
                if (pathNode5.z != (int)pathNode5.z || pathNode6.z != (int)pathNode6.z) {
                    n17 = 0.0f;
                }
                LineDrawer.addLine(pathNode5.x, pathNode5.y, pathNode5.z, pathNode6.x, pathNode6.y, pathNode6.z, n16, n17, n18, null, true);
            }
            this.testRequest.done = false;
        }
        if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
            final float n19 = (float)Mouse.getX();
            final float n20 = (float)Mouse.getY();
            final int testZ2 = this.testZ;
            final float xToIso3 = IsoUtils.XToIso(n19, n20, (float)testZ2);
            final float yToIso3 = IsoUtils.YToIso(n19, n20, (float)testZ2);
            final VisibilityGraph visGraph = this.getVisGraphAt(xToIso3, yToIso3, testZ2, 1);
            if (visGraph != null) {
                final Node closestNodeTo = visGraph.getClosestNodeTo(xToIso3, yToIso3);
                if (closestNodeTo != null) {
                    final Iterator<Connection> iterator4 = closestNodeTo.visible.iterator();
                    while (iterator4.hasNext()) {
                        final Node otherNode = iterator4.next().otherNode(closestNodeTo);
                        LineDrawer.addLine(closestNodeTo.x, closestNodeTo.y, (float)testZ2, otherNode.x, otherNode.y, (float)testZ2, 1.0f, 0.0f, 0.0f, null, true);
                    }
                }
            }
        }
        this.updateMain();
    }
    
    public void squareChanged(final IsoGridSquare isoGridSquare) {
        this.squareTaskQueue.add(SquareUpdateTask.alloc().init(this, isoGridSquare));
        this.thread.wake();
    }
    
    public void addChunkToWorld(final IsoChunk isoChunk) {
        this.chunkTaskQueue.add(ChunkUpdateTask.alloc().init(this, isoChunk));
        this.thread.wake();
    }
    
    public void removeChunkFromWorld(final IsoChunk isoChunk) {
        if (this.thread == null) {
            return;
        }
        this.chunkTaskQueue.add(ChunkRemoveTask.alloc().init(this, isoChunk));
        this.thread.wake();
    }
    
    public void addVehicleToWorld(final BaseVehicle key) {
        final VehicleAddTask alloc = VehicleAddTask.alloc();
        alloc.init(this, key);
        this.vehicleTaskQueue.add(alloc);
        this.vehicleState.put(key, VehicleState.alloc().init(key));
        this.thread.wake();
    }
    
    public void updateVehicle(final BaseVehicle baseVehicle) {
        final VehicleUpdateTask alloc = VehicleUpdateTask.alloc();
        alloc.init(this, baseVehicle);
        this.vehicleTaskQueue.add(alloc);
        this.thread.wake();
    }
    
    public void removeVehicleFromWorld(final BaseVehicle key) {
        if (this.thread == null) {
            return;
        }
        final VehicleRemoveTask alloc = VehicleRemoveTask.alloc();
        alloc.init(this, key);
        this.vehicleTaskQueue.add(alloc);
        final VehicleState vehicleState = this.vehicleState.remove(key);
        if (vehicleState != null) {
            vehicleState.vehicle = null;
            vehicleState.release();
        }
        this.thread.wake();
    }
    
    private Cell getCellFromSquarePos(int n, int n2) {
        n -= this.minX * 300;
        n2 -= this.minY * 300;
        if (n < 0 || n2 < 0) {
            return null;
        }
        final int n3 = n / 300;
        final int n4 = n2 / 300;
        if (n3 >= this.width || n4 >= this.height) {
            return null;
        }
        return this.cells[n3][n4];
    }
    
    private Cell getCellFromChunkPos(final int n, final int n2) {
        return this.getCellFromSquarePos(n * 10, n2 * 10);
    }
    
    private Chunk allocChunkIfNeeded(final int n, final int n2) {
        final Cell cellFromChunkPos = this.getCellFromChunkPos(n, n2);
        if (cellFromChunkPos == null) {
            return null;
        }
        return cellFromChunkPos.allocChunkIfNeeded(n, n2);
    }
    
    private Chunk getChunkFromChunkPos(final int n, final int n2) {
        final Cell cellFromChunkPos = this.getCellFromChunkPos(n, n2);
        if (cellFromChunkPos == null) {
            return null;
        }
        return cellFromChunkPos.getChunkFromChunkPos(n, n2);
    }
    
    private Chunk getChunkFromSquarePos(final int n, final int n2) {
        final Cell cellFromSquarePos = this.getCellFromSquarePos(n, n2);
        if (cellFromSquarePos == null) {
            return null;
        }
        return cellFromSquarePos.getChunkFromChunkPos(n / 10, n2 / 10);
    }
    
    private Square getSquare(final int n, final int n2, final int n3) {
        final Chunk chunkFromSquarePos = this.getChunkFromSquarePos(n, n2);
        if (chunkFromSquarePos == null) {
            return null;
        }
        return chunkFromSquarePos.getSquare(n, n2, n3);
    }
    
    private boolean isBlockedInAllDirections(final int n, final int n2, final int n3) {
        final Square square = this.getSquare(n, n2, n3);
        if (square == null) {
            return false;
        }
        final Square square2 = this.getSquare(n, n2 - 1, n3);
        final Square square3 = this.getSquare(n, n2 + 1, n3);
        final Square square4 = this.getSquare(n - 1, n2, n3);
        final Square square5 = this.getSquare(n + 1, n2, n3);
        final boolean b = square2 != null && this.astar.canNotMoveBetween(square, square2, false);
        final boolean b2 = square3 != null && this.astar.canNotMoveBetween(square, square3, false);
        final boolean b3 = square4 != null && this.astar.canNotMoveBetween(square, square4, false);
        final boolean b4 = square5 != null && this.astar.canNotMoveBetween(square, square5, false);
        return b && b2 && b3 && b4;
    }
    
    public void init(final IsoMetaGrid isoMetaGrid) {
        this.minX = isoMetaGrid.getMinX();
        this.minY = isoMetaGrid.getMinY();
        this.width = isoMetaGrid.getWidth();
        this.height = isoMetaGrid.getHeight();
        this.cells = new Cell[this.width][this.height];
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                this.cells[j][i] = Cell.alloc().init(this, this.minX + j, this.minY + i);
            }
        }
        (this.thread = new PMThread()).setName("PolyPathThread");
        this.thread.setDaemon(true);
        this.thread.start();
    }
    
    public void stop() {
        this.thread.bStop = true;
        this.thread.wake();
        while (this.thread.isAlive()) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException ex) {}
        }
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                if (this.cells[j][i] != null) {
                    this.cells[j][i].release();
                }
            }
        }
        for (IChunkTask chunkTask = this.chunkTaskQueue.poll(); chunkTask != null; chunkTask = this.chunkTaskQueue.poll()) {
            chunkTask.release();
        }
        for (SquareUpdateTask squareUpdateTask = this.squareTaskQueue.poll(); squareUpdateTask != null; squareUpdateTask = this.squareTaskQueue.poll()) {
            squareUpdateTask.release();
        }
        for (IVehicleTask vehicleTask = this.vehicleTaskQueue.poll(); vehicleTask != null; vehicleTask = this.vehicleTaskQueue.poll()) {
            vehicleTask.release();
        }
        for (PathRequestTask pathRequestTask = this.requestTaskQueue.poll(); pathRequestTask != null; pathRequestTask = this.requestTaskQueue.poll()) {
            pathRequestTask.release();
        }
        while (!this.requests.isEmpty()) {
            this.requests.removeLast().release();
        }
        while (!this.requestToMain.isEmpty()) {
            this.requestToMain.remove().release();
        }
        for (int k = 0; k < this.vehicles.size(); ++k) {
            this.vehicles.get(k).release();
        }
        final Iterator<VehicleState> iterator = this.vehicleState.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().release();
        }
        this.requestMap.clear();
        this.vehicles.clear();
        this.vehicleState.clear();
        this.vehicleMap.clear();
        this.cells = null;
        this.thread = null;
        this.rebuild = true;
    }
    
    public void updateMain() {
        final ArrayList<BaseVehicle> vehicles = IsoWorld.instance.CurrentCell.getVehicles();
        for (int i = 0; i < vehicles.size(); ++i) {
            final BaseVehicle key = vehicles.get(i);
            final VehicleState vehicleState = this.vehicleState.get(key);
            if (vehicleState != null && vehicleState.check()) {
                this.updateVehicle(key);
            }
        }
        for (PathFindRequest pathFindRequest = this.requestToMain.poll(); pathFindRequest != null; pathFindRequest = this.requestToMain.poll()) {
            if (this.requestMap.get(pathFindRequest.mover) == pathFindRequest) {
                this.requestMap.remove(pathFindRequest.mover);
            }
            if (!pathFindRequest.cancel) {
                if (pathFindRequest.path.isEmpty()) {
                    pathFindRequest.finder.Failed(pathFindRequest.mover);
                }
                else {
                    pathFindRequest.finder.Succeeded(pathFindRequest.path, pathFindRequest.mover);
                }
            }
            pathFindRequest.release();
        }
    }
    
    public void updateThread() {
        for (IChunkTask chunkTask = this.chunkTaskQueue.poll(); chunkTask != null; chunkTask = this.chunkTaskQueue.poll()) {
            chunkTask.execute();
            chunkTask.release();
            this.rebuild = true;
        }
        for (SquareUpdateTask squareUpdateTask = this.squareTaskQueue.poll(); squareUpdateTask != null; squareUpdateTask = this.squareTaskQueue.poll()) {
            squareUpdateTask.execute();
            squareUpdateTask.release();
        }
        for (IVehicleTask vehicleTask = this.vehicleTaskQueue.poll(); vehicleTask != null; vehicleTask = this.vehicleTaskQueue.poll()) {
            vehicleTask.execute();
            vehicleTask.release();
            this.rebuild = true;
        }
        for (PathRequestTask pathRequestTask = this.requestTaskQueue.poll(); pathRequestTask != null; pathRequestTask = this.requestTaskQueue.poll()) {
            pathRequestTask.execute();
            pathRequestTask.release();
        }
        if (this.rebuild) {
            for (int i = 0; i < this.graphs.size(); ++i) {
                this.graphs.get(i).release();
            }
            this.squareToNode.forEachValue((TObjectProcedure)this.releaseNodeProc);
            this.createVisibilityGraphs();
            this.rebuild = false;
            ++ChunkDataZ.EPOCH;
        }
        int n = 2;
        while (!this.requests.isEmpty()) {
            final PathFindRequest pathFindRequest = this.requests.removeFirst();
            if (pathFindRequest.cancel) {
                this.requestToMain.add(pathFindRequest);
            }
            else {
                try {
                    this.findPath(pathFindRequest, false);
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
                if (!pathFindRequest.targetXYZ.isEmpty()) {
                    this.shortestPath.copyFrom(pathFindRequest.path);
                    float targetX = pathFindRequest.targetX;
                    float targetY = pathFindRequest.targetY;
                    float targetZ = pathFindRequest.targetZ;
                    float n2 = this.shortestPath.isEmpty() ? Float.MAX_VALUE : this.shortestPath.length();
                    for (int j = 0; j < pathFindRequest.targetXYZ.size(); j += 3) {
                        pathFindRequest.targetX = pathFindRequest.targetXYZ.get(j);
                        pathFindRequest.targetY = pathFindRequest.targetXYZ.get(j + 1);
                        pathFindRequest.targetZ = pathFindRequest.targetXYZ.get(j + 2);
                        pathFindRequest.path.clear();
                        this.findPath(pathFindRequest, false);
                        if (!pathFindRequest.path.isEmpty()) {
                            final float length = pathFindRequest.path.length();
                            if (length < n2) {
                                n2 = length;
                                this.shortestPath.copyFrom(pathFindRequest.path);
                                targetX = pathFindRequest.targetX;
                                targetY = pathFindRequest.targetY;
                                targetZ = pathFindRequest.targetZ;
                            }
                        }
                    }
                    pathFindRequest.path.copyFrom(this.shortestPath);
                    pathFindRequest.targetX = targetX;
                    pathFindRequest.targetY = targetY;
                    pathFindRequest.targetZ = targetZ;
                }
                this.requestToMain.add(pathFindRequest);
                if (--n == 0) {
                    break;
                }
                continue;
            }
        }
    }
    
    public PathFindRequest addRequest(final IPathfinder pathfinder, final Mover key, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.cancelRequest(key);
        final PathFindRequest init = PathFindRequest.alloc().init(pathfinder, key, n, n2, n3, n4, n5, n6);
        this.requestMap.put(key, init);
        this.requestTaskQueue.add(PathRequestTask.alloc().init(this, init));
        this.thread.wake();
        return init;
    }
    
    public void cancelRequest(final Mover key) {
        final PathFindRequest pathFindRequest = this.requestMap.remove(key);
        if (pathFindRequest != null) {
            pathFindRequest.cancel = true;
        }
    }
    
    public ArrayList<Point> getPointInLine(final float n, final float n2, final float n3, final float n4, final int n5) {
        final PointPool pointPool = new PointPool();
        final ArrayList<Point> list = new ArrayList<Point>();
        this.supercover(n, n2, n3, n4, n5, pointPool, list);
        return list;
    }
    
    private void supercover(final float n, final float n2, final float n3, final float n4, final int n5, final PointPool pointPool, final ArrayList<Point> list) {
        final double n6 = Math.abs(n3 - n);
        final double n7 = Math.abs(n4 - n2);
        int n8 = (int)Math.floor(n);
        int n9 = (int)Math.floor(n2);
        int i = 1;
        int n10;
        double n11;
        if (n6 == 0.0) {
            n10 = 0;
            n11 = Double.POSITIVE_INFINITY;
        }
        else if (n3 > n) {
            n10 = 1;
            i += (int)Math.floor(n3) - n8;
            n11 = (Math.floor(n) + 1.0 - n) * n7;
        }
        else {
            n10 = -1;
            i += n8 - (int)Math.floor(n3);
            n11 = (n - Math.floor(n)) * n7;
        }
        int n12;
        double n13;
        if (n7 == 0.0) {
            n12 = 0;
            n13 = n11 - Double.POSITIVE_INFINITY;
        }
        else if (n4 > n2) {
            n12 = 1;
            i += (int)Math.floor(n4) - n9;
            n13 = n11 - (Math.floor(n2) + 1.0 - n2) * n6;
        }
        else {
            n12 = -1;
            i += n9 - (int)Math.floor(n4);
            n13 = n11 - (n2 - Math.floor(n2)) * n6;
        }
        while (i > 0) {
            final Point init = pointPool.alloc().init(n8, n9);
            if (list.contains(init)) {
                pointPool.release(init);
            }
            else {
                list.add(init);
            }
            if (n13 > 0.0) {
                n9 += n12;
                n13 -= n6;
            }
            else {
                n8 += n10;
                n13 += n7;
            }
            --i;
        }
    }
    
    public boolean lineClearCollide(final float n, final float n2, final float n3, final float n4, final int n5) {
        return this.lineClearCollide(n, n2, n3, n4, n5, null);
    }
    
    public boolean lineClearCollide(final float n, final float n2, final float n3, final float n4, final int n5, final IsoMovingObject isoMovingObject) {
        return this.lineClearCollide(n, n2, n3, n4, n5, isoMovingObject, true, true);
    }
    
    public boolean lineClearCollide(final float n, final float n2, final float n3, final float n4, final int n5, final IsoMovingObject isoMovingObject, final boolean b, final boolean b2) {
        int n6 = 0;
        if (b) {
            n6 |= 0x1;
        }
        if (b2) {
            n6 |= 0x2;
        }
        if (Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
            n6 |= 0x8;
        }
        return this.lineClearCollide(n, n2, n3, n4, n5, isoMovingObject, n6);
    }
    
    public boolean lineClearCollide(final float n, final float n2, final float n3, final float n4, final int n5, final IsoMovingObject isoMovingObject, final int n6) {
        BaseVehicle vehicle = null;
        if (isoMovingObject instanceof IsoGameCharacter) {
            vehicle = ((IsoGameCharacter)isoMovingObject).getVehicle();
        }
        else if (isoMovingObject instanceof BaseVehicle) {
            vehicle = (BaseVehicle)isoMovingObject;
        }
        return this.lccMain.isNotClear(this, n, n2, n3, n4, n5, vehicle, n6);
    }
    
    public Vector2 getCollidepoint(final float n, final float n2, final float n3, final float n4, final int n5, final IsoMovingObject isoMovingObject, final int n6) {
        BaseVehicle vehicle = null;
        if (isoMovingObject instanceof IsoGameCharacter) {
            vehicle = ((IsoGameCharacter)isoMovingObject).getVehicle();
        }
        else if (isoMovingObject instanceof BaseVehicle) {
            vehicle = (BaseVehicle)isoMovingObject;
        }
        return this.lccMain.getCollidepoint(this, n, n2, n3, n4, n5, vehicle, n6);
    }
    
    public boolean canStandAt(final float n, final float n2, final int n3, final IsoMovingObject isoMovingObject, final boolean b, final boolean b2) {
        BaseVehicle vehicle = null;
        if (isoMovingObject instanceof IsoGameCharacter) {
            vehicle = ((IsoGameCharacter)isoMovingObject).getVehicle();
        }
        else if (isoMovingObject instanceof BaseVehicle) {
            vehicle = (BaseVehicle)isoMovingObject;
        }
        int n4 = 0;
        if (b) {
            n4 |= 0x1;
        }
        if (b2) {
            n4 |= 0x2;
        }
        if (Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
            n4 |= 0x8;
        }
        return this.canStandAt(n, n2, n3, vehicle, n4);
    }
    
    public boolean canStandAt(final float n, final float n2, final int n3, final BaseVehicle baseVehicle, final int n4) {
        return this.lccMain.canStandAtOld(this, n, n2, (float)n3, baseVehicle, n4);
    }
    
    public boolean intersectLineWithVehicle(final float n, final float n2, final float n3, final float n4, final BaseVehicle baseVehicle, final Vector2 vector2) {
        if (baseVehicle == null || baseVehicle.getScript() == null) {
            return false;
        }
        final float[] tempFloats = this.tempFloats;
        tempFloats[0] = baseVehicle.getPoly().x1;
        tempFloats[1] = baseVehicle.getPoly().y1;
        tempFloats[2] = baseVehicle.getPoly().x2;
        tempFloats[3] = baseVehicle.getPoly().y2;
        tempFloats[4] = baseVehicle.getPoly().x3;
        tempFloats[5] = baseVehicle.getPoly().y3;
        tempFloats[6] = baseVehicle.getPoly().x4;
        tempFloats[7] = baseVehicle.getPoly().y4;
        float n5 = Float.MAX_VALUE;
        for (int i = 0; i < 8; i += 2) {
            final float n6 = tempFloats[i % 8];
            final float n7 = tempFloats[(i + 1) % 8];
            final float n8 = tempFloats[(i + 2) % 8];
            final float n9 = tempFloats[(i + 3) % 8];
            final double n10 = (n9 - n7) * (n3 - n) - (n8 - n6) * (n4 - n2);
            if (n10 == 0.0) {
                return false;
            }
            final double n11 = ((n8 - n6) * (n2 - n7) - (n9 - n7) * (n - n6)) / n10;
            final double n12 = ((n3 - n) * (n2 - n7) - (n4 - n2) * (n - n6)) / n10;
            if (n11 >= 0.0 && n11 <= 1.0 && n12 >= 0.0 && n12 <= 1.0) {
                final float n13 = (float)(n + n11 * (n3 - n));
                final float n14 = (float)(n2 + n11 * (n4 - n2));
                final float distanceTo = IsoUtils.DistanceTo(n, n2, n13, n14);
                if (distanceTo < n5) {
                    vector2.set(n13, n14);
                    n5 = distanceTo;
                }
            }
        }
        return n5 < Float.MAX_VALUE;
    }
    
    public Vector2f resolveCollision(final IsoGameCharacter isoGameCharacter, final float n, final float n2, final Vector2f vector2f) {
        if (GameClient.bClient && isoGameCharacter.isSkipResolveCollision()) {
            return vector2f.set(n, n2);
        }
        return this.collideWithObstacles.resolveCollision(isoGameCharacter, n, n2, vector2f);
    }
    
    static {
        RADIUS_DIAGONAL = (float)Math.sqrt(0.18000000715255737);
        temp = new Vector2();
        tempVec3f_1 = new Vector3f();
        instance = new PolygonalMap2();
    }
    
    private static final class Connection
    {
        Node node1;
        Node node2;
        int flags;
        static final ArrayDeque<Connection> pool;
        
        Connection init(final Node node1, final Node node2, final int flags) {
            this.node1 = node1;
            this.node2 = node2;
            this.flags = flags;
            return this;
        }
        
        Node otherNode(final Node node) {
            assert node == this.node2;
            return (node == this.node1) ? this.node2 : this.node1;
        }
        
        boolean has(final int n) {
            return (this.flags & n) != 0x0;
        }
        
        static Connection alloc() {
            if (Connection.pool.isEmpty()) {}
            return Connection.pool.isEmpty() ? new Connection() : Connection.pool.pop();
        }
        
        void release() {
            assert !Connection.pool.contains(this);
            Connection.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<Connection>();
        }
    }
    
    private static final class Node
    {
        static int nextID;
        final int ID;
        float x;
        float y;
        int z;
        boolean ignore;
        Square square;
        ArrayList<VisibilityGraph> graphs;
        final ArrayList<Edge> edges;
        final ArrayList<Connection> visible;
        int flags;
        static final ArrayList<Obstacle> tempObstacles;
        static final ArrayDeque<Node> pool;
        
        Node() {
            this.edges = new ArrayList<Edge>();
            this.visible = new ArrayList<Connection>();
            this.flags = 0;
            this.ID = Node.nextID++;
        }
        
        Node init(final float x, final float y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.ignore = false;
            this.square = null;
            if (this.graphs != null) {
                this.graphs.clear();
            }
            this.edges.clear();
            this.visible.clear();
            this.flags = 0;
            return this;
        }
        
        Node init(final Square square) {
            this.x = square.x + 0.5f;
            this.y = square.y + 0.5f;
            this.z = square.z;
            this.ignore = false;
            this.square = square;
            if (this.graphs != null) {
                this.graphs.clear();
            }
            this.edges.clear();
            this.visible.clear();
            this.flags = 0;
            return this;
        }
        
        Node setXY(final float x, final float y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        void addGraph(final VisibilityGraph visibilityGraph) {
            if (this.graphs == null) {
                this.graphs = new ArrayList<VisibilityGraph>();
            }
            assert !this.graphs.contains(visibilityGraph);
            this.graphs.add(visibilityGraph);
        }
        
        boolean sharesEdge(final Node node) {
            for (int i = 0; i < this.edges.size(); ++i) {
                if (this.edges.get(i).hasNode(node)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean sharesShape(final Node node) {
            for (int i = 0; i < this.edges.size(); ++i) {
                final Edge edge = this.edges.get(i);
                for (int j = 0; j < node.edges.size(); ++j) {
                    final Edge edge2 = node.edges.get(j);
                    if (edge.obstacle != null && edge.obstacle == edge2.obstacle) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        void getObstacles(final ArrayList<Obstacle> list) {
            for (int i = 0; i < this.edges.size(); ++i) {
                final Edge edge = this.edges.get(i);
                if (!list.contains(edge.obstacle)) {
                    list.add(edge.obstacle);
                }
            }
        }
        
        boolean onSameShapeButDoesNotShareAnEdge(final Node node) {
            Node.tempObstacles.clear();
            this.getObstacles(Node.tempObstacles);
            for (int i = 0; i < Node.tempObstacles.size(); ++i) {
                final Obstacle obstacle = Node.tempObstacles.get(i);
                if (obstacle.hasNode(node) && !obstacle.hasAdjacentNodes(this, node)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean hasFlag(final int n) {
            return (this.flags & n) != 0x0;
        }
        
        boolean isConnectedTo(final Node node) {
            if (this.hasFlag(4)) {
                return true;
            }
            for (int i = 0; i < this.visible.size(); ++i) {
                final Connection connection = this.visible.get(i);
                if (connection.node1 == node || connection.node2 == node) {
                    return true;
                }
            }
            return false;
        }
        
        static Node alloc() {
            if (Node.pool.isEmpty()) {}
            return Node.pool.isEmpty() ? new Node() : Node.pool.pop();
        }
        
        void release() {
            assert !Node.pool.contains(this);
            for (int i = this.visible.size() - 1; i >= 0; --i) {
                PolygonalMap2.instance.breakConnection(this.visible.get(i));
            }
            Node.pool.push(this);
        }
        
        static void releaseAll(final ArrayList<Node> list) {
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).release();
            }
        }
        
        static {
            Node.nextID = 1;
            tempObstacles = new ArrayList<Obstacle>();
            pool = new ArrayDeque<Node>();
        }
    }
    
    private static final class Edge
    {
        Node node1;
        Node node2;
        Obstacle obstacle;
        EdgeRing edgeRing;
        final ArrayList<Intersection> intersections;
        final Vector2 normal;
        static final ArrayDeque<Edge> pool;
        
        private Edge() {
            this.intersections = new ArrayList<Intersection>();
            this.normal = new Vector2();
        }
        
        Edge init(final Node node1, final Node node2, final Obstacle obstacle, final EdgeRing edgeRing) {
            if (node1 == null) {}
            this.node1 = node1;
            this.node2 = node2;
            node1.edges.add(this);
            node2.edges.add(this);
            this.obstacle = obstacle;
            this.edgeRing = edgeRing;
            this.intersections.clear();
            this.normal.set(node2.x - node1.x, node2.y - node1.y);
            this.normal.normalize();
            this.normal.rotate(1.5707964f);
            return this;
        }
        
        boolean hasNode(final Node node) {
            return node == this.node1 || node == this.node2;
        }
        
        void getClosestPointOnEdge(final float n, final float n2, final ClosestPointOnEdge closestPointOnEdge) {
            if (!this.node1.isConnectedTo(this.node2)) {
                return;
            }
            final float x = this.node1.x;
            final float y = this.node1.y;
            final float x2 = this.node2.x;
            final float y2 = this.node2.y;
            final double n3 = ((n - x) * (x2 - x) + (n2 - y) * (y2 - y)) / (Math.pow(x2 - x, 2.0) + Math.pow(y2 - y, 2.0));
            double n4 = x + n3 * (x2 - x);
            double n5 = y + n3 * (y2 - y);
            Node node = null;
            if (n3 <= 0.0) {
                n4 = x;
                n5 = y;
                node = this.node1;
            }
            else if (n3 >= 1.0) {
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
            if (!this.node1.isConnectedTo(this.node2)) {
                return false;
            }
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
        
        Edge split(final Node node2) {
            final Edge init = alloc().init(node2, this.node2, this.obstacle, this.edgeRing);
            this.edgeRing.add(this.edgeRing.indexOf(this) + 1, init);
            PolygonalMap2.instance.breakConnection(this.node1, this.node2);
            this.node2.edges.remove(this);
            this.node2 = node2;
            this.node2.edges.add(this);
            return init;
        }
        
        static Edge alloc() {
            return Edge.pool.isEmpty() ? new Edge() : Edge.pool.pop();
        }
        
        void release() {
            assert !Edge.pool.contains(this);
            this.node1 = null;
            this.node2 = null;
            this.obstacle = null;
            this.edgeRing = null;
            this.intersections.clear();
            Edge.pool.push(this);
        }
        
        static void releaseAll(final ArrayList<Edge> list) {
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).release();
            }
        }
        
        static {
            pool = new ArrayDeque<Edge>();
        }
    }
    
    private static final class ClosestPointOnEdge
    {
        Edge edge;
        Node node;
        final Vector2f point;
        double distSq;
        
        private ClosestPointOnEdge() {
            this.point = new Vector2f();
        }
    }
    
    static final class L_lineSegmentIntersects
    {
        static final Vector2 v1;
        
        static {
            v1 = new Vector2();
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
    
    private static final class EdgeRing extends ArrayList<Edge>
    {
        static final ArrayDeque<EdgeRing> pool;
        
        @Override
        public boolean add(final Edge edge) {
            assert !this.contains(edge);
            return super.add(edge);
        }
        
        public boolean hasNode(final Node node) {
            for (int i = 0; i < this.size(); ++i) {
                if (this.get(i).hasNode(node)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean hasAdjacentNodes(final Node node, final Node node2) {
            for (int i = 0; i < this.size(); ++i) {
                final Edge edge = this.get(i);
                if (edge.hasNode(node) && edge.hasNode(node2)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isPointInPolygon_CrossingNumber(final float n, final float n2) {
            int n3 = 0;
            for (int i = 0; i < this.size(); ++i) {
                final Edge edge = this.get(i);
                if (((edge.node1.y <= n2 && edge.node2.y > n2) || (edge.node1.y > n2 && edge.node2.y <= n2)) && n < edge.node1.x + (n2 - edge.node1.y) / (edge.node2.y - edge.node1.y) * (edge.node2.x - edge.node1.x)) {
                    ++n3;
                }
            }
            return n3 % 2 == 1;
        }
        
        float isLeft(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            return (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
        }
        
        EdgeRingHit isPointInPolygon_WindingNumber(final float n, final float n2, final int n3) {
            int n4 = 0;
            for (int i = 0; i < this.size(); ++i) {
                final Edge edge = this.get(i);
                if ((n3 & 0x10) != 0x0 && edge.isPointOn(n, n2)) {
                    return EdgeRingHit.OnEdge;
                }
                if (edge.node1.y <= n2) {
                    if (edge.node2.y > n2 && this.isLeft(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y, n, n2) > 0.0f) {
                        ++n4;
                    }
                }
                else if (edge.node2.y <= n2 && this.isLeft(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y, n, n2) < 0.0f) {
                    --n4;
                }
            }
            return (n4 == 0) ? EdgeRingHit.Outside : EdgeRingHit.Inside;
        }
        
        boolean lineSegmentIntersects(final float n, final float n2, final float n3, final float n4) {
            final Vector2 v1 = L_lineSegmentIntersects.v1;
            v1.set(n3 - n, n4 - n2);
            final float length = v1.getLength();
            v1.normalize();
            final float x = v1.x;
            final float y = v1.y;
            for (int i = 0; i < this.size(); ++i) {
                final Edge edge = this.get(i);
                if (!edge.isPointOn(n, n2)) {
                    if (!edge.isPointOn(n3, n4)) {
                        if (edge.normal.dot(v1) >= 0.01f) {}
                        final float x2 = edge.node1.x;
                        final float y2 = edge.node1.y;
                        final float x3 = edge.node2.x;
                        final float y3 = edge.node2.y;
                        final float n5 = n - x2;
                        final float n6 = n2 - y2;
                        final float n7 = x3 - x2;
                        final float n8 = y3 - y2;
                        final float n9 = 1.0f / (n8 * x - n7 * y);
                        final float n10 = (n7 * n6 - n8 * n5) * n9;
                        if (n10 >= 0.0f && n10 <= length) {
                            final float n11 = (n6 * x - n5 * y) * n9;
                            if (n11 >= 0.0f && n11 <= 1.0f) {
                                return true;
                            }
                        }
                    }
                }
            }
            return this.isPointInPolygon_WindingNumber((n + n3) / 2.0f, (n2 + n4) / 2.0f, 0) != EdgeRingHit.Outside;
        }
        
        void getClosestPointOnEdge(final float n, final float n2, final ClosestPointOnEdge closestPointOnEdge) {
            for (int i = 0; i < this.size(); ++i) {
                this.get(i).getClosestPointOnEdge(n, n2, closestPointOnEdge);
            }
        }
        
        static EdgeRing alloc() {
            return EdgeRing.pool.isEmpty() ? new EdgeRing() : EdgeRing.pool.pop();
        }
        
        public void release() {
            Edge.releaseAll(this);
        }
        
        static void releaseAll(final ArrayList<EdgeRing> list) {
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).release();
            }
        }
        
        static {
            pool = new ArrayDeque<EdgeRing>();
        }
    }
    
    private static final class Obstacle
    {
        Vehicle vehicle;
        final EdgeRing outer;
        final ArrayList<EdgeRing> inner;
        ImmutableRectF bounds;
        Node nodeCrawlFront;
        Node nodeCrawlRear;
        final ArrayList<Node> crawlNodes;
        static final ArrayDeque<Obstacle> pool;
        
        private Obstacle() {
            this.outer = new EdgeRing();
            this.inner = new ArrayList<EdgeRing>();
            this.crawlNodes = new ArrayList<Node>();
        }
        
        Obstacle init(final Vehicle vehicle) {
            this.vehicle = vehicle;
            this.outer.clear();
            this.inner.clear();
            final Node node = null;
            this.nodeCrawlRear = node;
            this.nodeCrawlFront = node;
            this.crawlNodes.clear();
            return this;
        }
        
        Obstacle init(final IsoGridSquare isoGridSquare) {
            this.vehicle = null;
            this.outer.clear();
            this.inner.clear();
            final Node node = null;
            this.nodeCrawlRear = node;
            this.nodeCrawlFront = node;
            this.crawlNodes.clear();
            return this;
        }
        
        boolean hasNode(final Node node) {
            if (this.outer.hasNode(node)) {
                return true;
            }
            for (int i = 0; i < this.inner.size(); ++i) {
                if (this.inner.get(i).hasNode(node)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean hasAdjacentNodes(final Node node, final Node node2) {
            if (this.outer.hasAdjacentNodes(node, node2)) {
                return true;
            }
            for (int i = 0; i < this.inner.size(); ++i) {
                if (this.inner.get(i).hasAdjacentNodes(node, node2)) {
                    return true;
                }
            }
            return false;
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
        
        boolean isPointInside(final float n, final float n2) {
            return this.isPointInside(n, n2, 0);
        }
        
        boolean lineSegmentIntersects(final float n, final float n2, final float n3, final float n4) {
            if (this.outer.lineSegmentIntersects(n, n2, n3, n4)) {
                return true;
            }
            for (int i = 0; i < this.inner.size(); ++i) {
                if (this.inner.get(i).lineSegmentIntersects(n, n2, n3, n4)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isNodeInsideOf(final Node node) {
            return !this.hasNode(node) && this.bounds.containsPoint(node.x, node.y) && this.isPointInside(node.x, node.y);
        }
        
        void getClosestPointOnEdge(final float n, final float n2, final ClosestPointOnEdge closestPointOnEdge) {
            closestPointOnEdge.edge = null;
            closestPointOnEdge.node = null;
            closestPointOnEdge.distSq = Double.MAX_VALUE;
            this.outer.getClosestPointOnEdge(n, n2, closestPointOnEdge);
            for (int i = 0; i < this.inner.size(); ++i) {
                this.inner.get(i).getClosestPointOnEdge(n, n2, closestPointOnEdge);
            }
        }
        
        boolean splitEdgeAtNearestPoint(final ClosestPointOnEdge closestPointOnEdge, final int n, final AdjustStartEndNodeData adjustStartEndNodeData) {
            if (closestPointOnEdge.edge == null) {
                return false;
            }
            adjustStartEndNodeData.obstacle = this;
            if (closestPointOnEdge.node == null) {
                adjustStartEndNodeData.node = Node.alloc().init(closestPointOnEdge.point.x, closestPointOnEdge.point.y, n);
                adjustStartEndNodeData.newEdge = closestPointOnEdge.edge.split(adjustStartEndNodeData.node);
                adjustStartEndNodeData.isNodeNew = true;
            }
            else {
                adjustStartEndNodeData.node = closestPointOnEdge.node;
                adjustStartEndNodeData.newEdge = null;
                adjustStartEndNodeData.isNodeNew = false;
            }
            return true;
        }
        
        void unsplit(final Node node, final ArrayList<Edge> list) {
            for (int i = 0; i < list.size(); ++i) {
                final Edge edge = list.get(i);
                if (edge.node1 == node) {
                    if (i > 0) {
                        final Edge edge2 = list.get(i - 1);
                        edge2.node2 = edge.node2;
                        assert edge.node2.edges.contains(edge);
                        edge.node2.edges.remove(edge);
                        assert !edge.node2.edges.contains(edge2);
                        edge.node2.edges.add(edge2);
                        PolygonalMap2.instance.connectTwoNodes(edge2.node1, edge2.node2);
                    }
                    else {
                        list.get(i + 1).node1 = list.get(list.size() - 1).node2;
                    }
                    edge.release();
                    list.remove(i);
                    break;
                }
            }
        }
        
        void calcBounds() {
            float min = Float.MAX_VALUE;
            float min2 = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            float max2 = Float.MIN_VALUE;
            for (int i = 0; i < this.outer.size(); ++i) {
                final Edge edge = this.outer.get(i);
                min = Math.min(min, edge.node1.x);
                min2 = Math.min(min2, edge.node1.y);
                max = Math.max(max, edge.node1.x);
                max2 = Math.max(max2, edge.node1.y);
            }
            if (this.bounds != null) {
                this.bounds.release();
            }
            final float n = 0.01f;
            this.bounds = ImmutableRectF.alloc().init(min - n, min2 - n, max - min + n * 2.0f, max2 - min2 + n * 2.0f);
        }
        
        void render(final ArrayList<Edge> list, final boolean b) {
            if (list.isEmpty()) {
                return;
            }
            float n = 0.0f;
            final float n2 = b ? 1.0f : 0.5f;
            final float n3 = b ? 0.0f : 0.5f;
            for (final Edge edge : list) {
                final Node node1 = edge.node1;
                final Node node2 = edge.node2;
                LineDrawer.addLine(node1.x, node1.y, (float)node1.z, node2.x, node2.y, (float)node2.z, n, n2, n3, null, true);
                final Vector3f normalize = new Vector3f(node2.x - node1.x, node2.y - node1.y, (float)(node2.z - node1.z)).normalize();
                final Vector3f normalize2 = new Vector3f((Vector3fc)normalize).cross(0.0f, 0.0f, 1.0f).normalize();
                normalize.mul(0.9f);
                LineDrawer.addLine(node2.x - normalize.x * 0.1f - normalize2.x * 0.1f, node2.y - normalize.y * 0.1f - normalize2.y * 0.1f, (float)node2.z, node2.x, node2.y, (float)node2.z, n, n2, n3, null, true);
                LineDrawer.addLine(node2.x - normalize.x * 0.1f + normalize2.x * 0.1f, node2.y - normalize.y * 0.1f + normalize2.y * 0.1f, (float)node2.z, node2.x, node2.y, (float)node2.z, n, n2, n3, null, true);
                n = 1.0f - n;
            }
            final Node node3 = list.get(0).node1;
            LineDrawer.addLine(node3.x - 0.1f, node3.y - 0.1f, (float)node3.z, node3.x + 0.1f, node3.y + 0.1f, (float)node3.z, 1.0f, 0.0f, 0.0f, null, false);
        }
        
        void render() {
            this.render(this.outer, true);
            for (int i = 0; i < this.inner.size(); ++i) {
                this.render(this.inner.get(i), false);
            }
        }
        
        void connectCrawlNodes(final VisibilityGraph visibilityGraph, final Obstacle obstacle) {
            this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlFront, obstacle.nodeCrawlFront);
            this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlFront, obstacle.nodeCrawlRear);
            this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlRear, obstacle.nodeCrawlFront);
            this.connectCrawlNode(visibilityGraph, obstacle, this.nodeCrawlRear, obstacle.nodeCrawlRear);
            for (int i = 0; i < this.crawlNodes.size(); i += 3) {
                final Node node = this.crawlNodes.get(i);
                final Node node2 = this.crawlNodes.get(i + 2);
                for (int j = 0; j < obstacle.crawlNodes.size(); j += 3) {
                    final Node node3 = obstacle.crawlNodes.get(j);
                    final Node node4 = obstacle.crawlNodes.get(j + 2);
                    this.connectCrawlNode(visibilityGraph, obstacle, node, node3);
                    this.connectCrawlNode(visibilityGraph, obstacle, node, node4);
                    this.connectCrawlNode(visibilityGraph, obstacle, node2, node3);
                    this.connectCrawlNode(visibilityGraph, obstacle, node2, node4);
                }
            }
        }
        
        void connectCrawlNode(final VisibilityGraph visibilityGraph, final Obstacle obstacle, Node closestInteriorCrawlNode, final Node node) {
            if (this.isNodeInsideOf(node)) {
                node.flags |= 0x2;
                closestInteriorCrawlNode = this.getClosestInteriorCrawlNode(node.x, node.y);
                if (closestInteriorCrawlNode == null) {
                    return;
                }
                if (closestInteriorCrawlNode.isConnectedTo(node)) {
                    return;
                }
                PolygonalMap2.instance.connectTwoNodes(closestInteriorCrawlNode, node);
            }
            else {
                if (closestInteriorCrawlNode.ignore || node.ignore) {
                    return;
                }
                if (closestInteriorCrawlNode.isConnectedTo(node)) {
                    return;
                }
                if (visibilityGraph.isVisible(closestInteriorCrawlNode, node)) {
                    PolygonalMap2.instance.connectTwoNodes(closestInteriorCrawlNode, node);
                }
            }
        }
        
        Node getClosestInteriorCrawlNode(final float n, final float n2) {
            Node node = null;
            float n3 = Float.MAX_VALUE;
            for (int i = 0; i < this.crawlNodes.size(); i += 3) {
                final Node node2 = this.crawlNodes.get(i + 1);
                final float distanceToSquared = IsoUtils.DistanceToSquared(node2.x, node2.y, n, n2);
                if (distanceToSquared < n3) {
                    node = node2;
                    n3 = distanceToSquared;
                }
            }
            return node;
        }
        
        static Obstacle alloc() {
            return Obstacle.pool.isEmpty() ? new Obstacle() : Obstacle.pool.pop();
        }
        
        void release() {
            assert !Obstacle.pool.contains(this);
            this.outer.release();
            this.outer.clear();
            EdgeRing.releaseAll(this.inner);
            this.inner.clear();
            Obstacle.pool.push(this);
        }
        
        static void releaseAll(final ArrayList<Obstacle> list) {
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).release();
            }
        }
        
        static {
            pool = new ArrayDeque<Obstacle>();
        }
    }
    
    @Deprecated
    private static final class ObjectOutline
    {
        int x;
        int y;
        int z;
        boolean nw;
        boolean nw_w;
        boolean nw_n;
        boolean nw_e;
        boolean nw_s;
        boolean w_w;
        boolean w_e;
        boolean w_cutoff;
        boolean n_n;
        boolean n_s;
        boolean n_cutoff;
        ArrayList<Node> nodes;
        static final ArrayDeque<ObjectOutline> pool;
        
        ObjectOutline init(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            final boolean b = false;
            this.nw_e = b;
            this.nw_n = b;
            this.nw_w = b;
            this.nw = b;
            final boolean w_w = false;
            this.w_cutoff = w_w;
            this.w_e = w_w;
            this.w_w = w_w;
            final boolean n_n = false;
            this.n_cutoff = n_n;
            this.n_s = n_n;
            this.n_n = n_n;
            return this;
        }
        
        static void setSolid(final int n, final int n2, final int n3, final ObjectOutline[][] array) {
            setWest(n, n2, n3, array);
            setNorth(n, n2, n3, array);
            setWest(n + 1, n2, n3, array);
            setNorth(n, n2 + 1, n3, array);
        }
        
        static void setWest(final int n, final int n2, final int n3, final ObjectOutline[][] array) {
            final ObjectOutline value = get(n, n2, n3, array);
            if (value != null) {
                if (value.nw) {
                    value.nw_s = false;
                }
                else {
                    value.nw = true;
                    value.nw_w = true;
                    value.nw_n = true;
                    value.nw_e = true;
                    value.nw_s = false;
                }
                value.w_w = true;
                value.w_e = true;
            }
            final ObjectOutline objectOutline = value;
            final ObjectOutline value2 = get(n, n2 + 1, n3, array);
            if (value2 == null) {
                if (objectOutline != null) {
                    objectOutline.w_cutoff = true;
                }
            }
            else if (value2.nw) {
                value2.nw_n = false;
            }
            else {
                value2.nw = true;
                value2.nw_n = false;
                value2.nw_w = true;
                value2.nw_e = true;
                value2.nw_s = true;
            }
        }
        
        static void setNorth(final int n, final int n2, final int n3, final ObjectOutline[][] array) {
            final ObjectOutline value = get(n, n2, n3, array);
            if (value != null) {
                if (value.nw) {
                    value.nw_e = false;
                }
                else {
                    value.nw = true;
                    value.nw_w = true;
                    value.nw_n = true;
                    value.nw_e = false;
                    value.nw_s = true;
                }
                value.n_n = true;
                value.n_s = true;
            }
            final ObjectOutline objectOutline = value;
            final ObjectOutline value2 = get(n + 1, n2, n3, array);
            if (value2 == null) {
                if (objectOutline != null) {
                    objectOutline.n_cutoff = true;
                }
            }
            else if (value2.nw) {
                value2.nw_w = false;
            }
            else {
                value2.nw = true;
                value2.nw_n = true;
                value2.nw_w = false;
                value2.nw_e = true;
                value2.nw_s = true;
            }
        }
        
        static ObjectOutline get(final int n, final int n2, final int n3, final ObjectOutline[][] array) {
            if (n < 0 || n >= array.length) {
                return null;
            }
            if (n2 < 0 || n2 >= array[0].length) {
                return null;
            }
            if (array[n][n2] == null) {
                array[n][n2] = alloc().init(n, n2, n3);
            }
            return array[n][n2];
        }
        
        void trace_NW_N(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x + 0.3f, this.y - 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x + 0.3f, this.y - 0.3f, this.z));
            }
            this.nw_n = false;
            if (this.nw_e) {
                this.trace_NW_E(array, null);
            }
            else if (this.n_n) {
                this.trace_N_N(array, this.nodes.get(this.nodes.size() - 1));
            }
        }
        
        void trace_NW_S(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x - 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x - 0.3f, this.y + 0.3f, this.z));
            }
            this.nw_s = false;
            if (this.nw_w) {
                this.trace_NW_W(array, null);
            }
            else {
                final ObjectOutline value = get(this.x - 1, this.y, this.z, array);
                if (value == null) {
                    return;
                }
                if (value.n_s) {
                    value.nodes = this.nodes;
                    value.trace_N_S(array, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }
        
        void trace_NW_W(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x - 0.3f, this.y - 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x - 0.3f, this.y - 0.3f, this.z));
            }
            this.nw_w = false;
            if (this.nw_n) {
                this.trace_NW_N(array, null);
            }
            else {
                final ObjectOutline value = get(this.x, this.y - 1, this.z, array);
                if (value == null) {
                    return;
                }
                if (value.w_w) {
                    value.nodes = this.nodes;
                    value.trace_W_W(array, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }
        
        void trace_NW_E(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x + 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x + 0.3f, this.y + 0.3f, this.z));
            }
            this.nw_e = false;
            if (this.nw_s) {
                this.trace_NW_S(array, null);
            }
            else if (this.w_e) {
                this.trace_W_E(array, this.nodes.get(this.nodes.size() - 1));
            }
        }
        
        void trace_W_E(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x + 0.3f, this.y + 1 - 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x + 0.3f, this.y + 1 - 0.3f, this.z));
            }
            this.w_e = false;
            if (this.w_cutoff) {
                this.nodes.get(this.nodes.size() - 1).setXY(this.x + 0.3f, this.y + 1 + 0.3f);
                this.nodes.add(Node.alloc().init(this.x - 0.3f, this.y + 1 + 0.3f, this.z));
                final Node init = Node.alloc().init(this.x - 0.3f, this.y + 1 - 0.3f, this.z);
                this.nodes.add(init);
                this.trace_W_W(array, init);
                return;
            }
            final ObjectOutline value = get(this.x, this.y + 1, this.z, array);
            if (value == null) {
                return;
            }
            if (value.nw && value.nw_e) {
                value.nodes = this.nodes;
                value.trace_NW_E(array, this.nodes.get(this.nodes.size() - 1));
            }
            else if (value.n_n) {
                value.nodes = this.nodes;
                value.trace_N_N(array, null);
            }
        }
        
        void trace_W_W(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x - 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x - 0.3f, this.y + 0.3f, this.z));
            }
            this.w_w = false;
            if (this.nw_w) {
                this.trace_NW_W(array, this.nodes.get(this.nodes.size() - 1));
            }
            else {
                final ObjectOutline value = get(this.x - 1, this.y, this.z, array);
                if (value == null) {
                    return;
                }
                if (value.n_s) {
                    value.nodes = this.nodes;
                    value.trace_N_S(array, null);
                }
            }
        }
        
        void trace_N_N(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x + 1 - 0.3f, this.y - 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x + 1 - 0.3f, this.y - 0.3f, this.z));
            }
            this.n_n = false;
            if (this.n_cutoff) {
                this.nodes.get(this.nodes.size() - 1).setXY(this.x + 1 + 0.3f, this.y - 0.3f);
                this.nodes.add(Node.alloc().init(this.x + 1 + 0.3f, this.y + 0.3f, this.z));
                final Node init = Node.alloc().init(this.x + 1 - 0.3f, this.y + 0.3f, this.z);
                this.nodes.add(init);
                this.trace_N_S(array, init);
                return;
            }
            final ObjectOutline value = get(this.x + 1, this.y, this.z, array);
            if (value == null) {
                return;
            }
            if (value.nw_n) {
                value.nodes = this.nodes;
                value.trace_NW_N(array, this.nodes.get(this.nodes.size() - 1));
            }
            else {
                final ObjectOutline value2 = get(this.x + 1, this.y - 1, this.z, array);
                if (value2 == null) {
                    return;
                }
                if (value2.w_w) {
                    value2.nodes = this.nodes;
                    value2.trace_W_W(array, null);
                }
            }
        }
        
        void trace_N_S(final ObjectOutline[][] array, final Node node) {
            if (node != null) {
                node.setXY(this.x + 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(Node.alloc().init(this.x + 0.3f, this.y + 0.3f, this.z));
            }
            this.n_s = false;
            if (this.nw_s) {
                this.trace_NW_S(array, this.nodes.get(this.nodes.size() - 1));
            }
            else if (this.w_e) {
                this.trace_W_E(array, null);
            }
        }
        
        void trace(final ObjectOutline[][] array, final ArrayList<Node> nodes) {
            nodes.clear();
            this.nodes = nodes;
            final Node init = Node.alloc().init(this.x - 0.3f, this.y - 0.3f, this.z);
            nodes.add(init);
            this.trace_NW_N(array, null);
            if (nodes.size() == 2 || init.x != nodes.get(nodes.size() - 1).x || init.y != nodes.get(nodes.size() - 1).y) {
                nodes.clear();
            }
            else {
                nodes.get(nodes.size() - 1).release();
                nodes.set(nodes.size() - 1, init);
            }
        }
        
        static ObjectOutline alloc() {
            return ObjectOutline.pool.isEmpty() ? new ObjectOutline() : ObjectOutline.pool.pop();
        }
        
        void release() {
            assert !ObjectOutline.pool.contains(this);
            ObjectOutline.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<ObjectOutline>();
        }
    }
    
    private static final class ClusterOutline
    {
        int x;
        int y;
        int z;
        boolean w;
        boolean n;
        boolean e;
        boolean s;
        boolean tw;
        boolean tn;
        boolean te;
        boolean ts;
        boolean inner;
        boolean innerCorner;
        boolean start;
        static final ArrayDeque<ClusterOutline> pool;
        
        ClusterOutline init(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            final boolean b = false;
            this.s = b;
            this.e = b;
            this.n = b;
            this.w = b;
            final boolean b2 = false;
            this.ts = b2;
            this.te = b2;
            this.tn = b2;
            this.tw = b2;
            final boolean inner = false;
            this.start = inner;
            this.innerCorner = inner;
            this.inner = inner;
            return this;
        }
        
        static ClusterOutline alloc() {
            return ClusterOutline.pool.isEmpty() ? new ClusterOutline() : ClusterOutline.pool.pop();
        }
        
        void release() {
            assert !ClusterOutline.pool.contains(this);
            ClusterOutline.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<ClusterOutline>();
        }
    }
    
    private static final class ClusterOutlineGrid
    {
        ClusterOutline[] elements;
        int W;
        int H;
        
        ClusterOutlineGrid setSize(final int w, final int h) {
            if (this.elements == null || this.elements.length < w * h) {
                this.elements = new ClusterOutline[w * h];
            }
            this.W = w;
            this.H = h;
            return this;
        }
        
        void releaseElements() {
            for (int i = 0; i < this.H; ++i) {
                for (int j = 0; j < this.W; ++j) {
                    if (this.elements[j + i * this.W] != null) {
                        this.elements[j + i * this.W].release();
                        this.elements[j + i * this.W] = null;
                    }
                }
            }
        }
        
        void setInner(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            if (value != null) {
                value.inner = true;
            }
        }
        
        void setWest(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            if (value != null) {
                value.w = true;
            }
        }
        
        void setNorth(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            if (value != null) {
                value.n = true;
            }
        }
        
        void setEast(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            if (value != null) {
                value.e = true;
            }
        }
        
        void setSouth(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            if (value != null) {
                value.s = true;
            }
        }
        
        boolean canTrace_W(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            return value != null && value.inner && value.w && !value.tw;
        }
        
        boolean canTrace_N(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            return value != null && value.inner && value.n && !value.tn;
        }
        
        boolean canTrace_E(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            return value != null && value.inner && value.e && !value.te;
        }
        
        boolean canTrace_S(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            return value != null && value.inner && value.s && !value.ts;
        }
        
        boolean isInner(final int n, final int n2, final int n3) {
            final ClusterOutline value = this.get(n, n2, n3);
            return value != null && (value.start || value.inner);
        }
        
        ClusterOutline get(final int n, final int n2, final int n3) {
            if (n < 0 || n >= this.W) {
                return null;
            }
            if (n2 < 0 || n2 >= this.H) {
                return null;
            }
            if (this.elements[n + n2 * this.W] == null) {
                this.elements[n + n2 * this.W] = ClusterOutline.alloc().init(n, n2, n3);
            }
            return this.elements[n + n2 * this.W];
        }
        
        void trace_W(final ClusterOutline clusterOutline, final ArrayList<Node> list, final Node node) {
            final int x = clusterOutline.x;
            final int y = clusterOutline.y;
            final int z = clusterOutline.z;
            if (node != null) {
                node.setXY((float)x, (float)y);
            }
            else {
                list.add(Node.alloc().init((float)x, (float)y, z));
            }
            clusterOutline.tw = true;
            if (this.canTrace_S(x - 1, y - 1, z)) {
                this.get(x, y - 1, z).innerCorner = true;
                this.trace_S(this.get(x - 1, y - 1, z), list, null);
            }
            else if (this.canTrace_W(x, y - 1, z)) {
                this.trace_W(this.get(x, y - 1, z), list, list.get(list.size() - 1));
            }
            else if (this.canTrace_N(x, y, z)) {
                this.trace_N(clusterOutline, list, null);
            }
        }
        
        void trace_N(final ClusterOutline clusterOutline, final ArrayList<Node> list, final Node node) {
            final int x = clusterOutline.x;
            final int y = clusterOutline.y;
            final int z = clusterOutline.z;
            if (node != null) {
                node.setXY((float)(x + 1), (float)y);
            }
            else {
                list.add(Node.alloc().init((float)(x + 1), (float)y, z));
            }
            clusterOutline.tn = true;
            if (this.canTrace_W(x + 1, y - 1, z)) {
                this.get(x + 1, y, z).innerCorner = true;
                this.trace_W(this.get(x + 1, y - 1, z), list, null);
            }
            else if (this.canTrace_N(x + 1, y, z)) {
                this.trace_N(this.get(x + 1, y, z), list, list.get(list.size() - 1));
            }
            else if (this.canTrace_E(x, y, z)) {
                this.trace_E(clusterOutline, list, null);
            }
        }
        
        void trace_E(final ClusterOutline clusterOutline, final ArrayList<Node> list, final Node node) {
            final int x = clusterOutline.x;
            final int y = clusterOutline.y;
            final int z = clusterOutline.z;
            if (node != null) {
                node.setXY((float)(x + 1), (float)(y + 1));
            }
            else {
                list.add(Node.alloc().init((float)(x + 1), (float)(y + 1), z));
            }
            clusterOutline.te = true;
            if (this.canTrace_N(x + 1, y + 1, z)) {
                this.get(x, y + 1, z).innerCorner = true;
                this.trace_N(this.get(x + 1, y + 1, z), list, null);
            }
            else if (this.canTrace_E(x, y + 1, z)) {
                this.trace_E(this.get(x, y + 1, z), list, list.get(list.size() - 1));
            }
            else if (this.canTrace_S(x, y, z)) {
                this.trace_S(clusterOutline, list, null);
            }
        }
        
        void trace_S(final ClusterOutline clusterOutline, final ArrayList<Node> list, final Node node) {
            final int x = clusterOutline.x;
            final int y = clusterOutline.y;
            final int z = clusterOutline.z;
            if (node != null) {
                node.setXY((float)x, (float)(y + 1));
            }
            else {
                list.add(Node.alloc().init((float)x, (float)(y + 1), z));
            }
            clusterOutline.ts = true;
            if (this.canTrace_E(x - 1, y + 1, z)) {
                this.get(x - 1, y, z).innerCorner = true;
                this.trace_E(this.get(x - 1, y + 1, z), list, null);
            }
            else if (this.canTrace_S(x - 1, y, z)) {
                this.trace_S(this.get(x - 1, y, z), list, list.get(list.size() - 1));
            }
            else if (this.canTrace_W(x, y, z)) {
                this.trace_W(clusterOutline, list, null);
            }
        }
        
        ArrayList<Node> trace(final ClusterOutline clusterOutline) {
            final int x = clusterOutline.x;
            final int y = clusterOutline.y;
            final int z = clusterOutline.z;
            final ArrayList<Node> list = new ArrayList<Node>();
            final Node init = Node.alloc().init((float)x, (float)y, z);
            list.add(init);
            clusterOutline.start = true;
            this.trace_N(clusterOutline, list, null);
            final Node node = list.get(list.size() - 1);
            final float n = 0.1f;
            if ((int)(init.x + n) == (int)(node.x + n) && (int)(init.y + n) == (int)(node.y + n)) {
                node.release();
                list.set(list.size() - 1, init);
            }
            return list;
        }
    }
    
    private static final class Intersection
    {
        Edge edge1;
        Edge edge2;
        float dist1;
        float dist2;
        Node nodeSplit;
        
        Intersection(final Edge edge1, final Edge edge2, final float dist1, final float dist2, final float n, final float n2) {
            this.edge1 = edge1;
            this.edge2 = edge2;
            this.dist1 = dist1;
            this.dist2 = dist2;
            this.nodeSplit = Node.alloc().init(n, n2, edge1.node1.z);
        }
        
        Intersection(final Edge edge1, final Edge edge2, final float dist1, final float dist2, final Node nodeSplit) {
            this.edge1 = edge1;
            this.edge2 = edge2;
            this.dist1 = dist1;
            this.dist2 = dist2;
            this.nodeSplit = nodeSplit;
        }
        
        Edge split(final Edge edge) {
            return edge.split(this.nodeSplit);
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
    
    private static final class VehicleRect
    {
        VehicleCluster cluster;
        Vehicle vehicle;
        int x;
        int y;
        int w;
        int h;
        int z;
        static final ArrayDeque<VehicleRect> pool;
        
        VehicleRect init(final Vehicle vehicle, final int x, final int y, final int w, final int h, final int z) {
            this.cluster = null;
            this.vehicle = vehicle;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.z = z;
            return this;
        }
        
        VehicleRect init(final int x, final int y, final int w, final int h, final int z) {
            this.cluster = null;
            this.vehicle = null;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.z = z;
            return this;
        }
        
        int left() {
            return this.x;
        }
        
        int top() {
            return this.y;
        }
        
        int right() {
            return this.x + this.w;
        }
        
        int bottom() {
            return this.y + this.h;
        }
        
        boolean containsPoint(final float n, final float n2, final float n3) {
            return n >= this.left() && n < this.right() && n2 >= this.top() && n2 < this.bottom() && (int)n3 == this.z;
        }
        
        boolean containsPoint(final float n, final float n2, final float n3, final int n4) {
            final int n5 = this.x - n4;
            final int n6 = this.y - n4;
            final int n7 = this.right() + n4;
            final int n8 = this.bottom() + n4;
            return n >= n5 && n < n7 && n2 >= n6 && n2 < n8 && (int)n3 == this.z;
        }
        
        boolean intersects(final VehicleRect vehicleRect) {
            return this.left() < vehicleRect.right() && this.right() > vehicleRect.left() && this.top() < vehicleRect.bottom() && this.bottom() > vehicleRect.top();
        }
        
        boolean isAdjacent(final VehicleRect vehicleRect) {
            --this.x;
            --this.y;
            this.w += 2;
            this.h += 2;
            final boolean intersects = this.intersects(vehicleRect);
            ++this.x;
            ++this.y;
            this.w -= 2;
            this.h -= 2;
            return intersects;
        }
        
        static VehicleRect alloc() {
            if (VehicleRect.pool.isEmpty()) {}
            return VehicleRect.pool.isEmpty() ? new VehicleRect() : VehicleRect.pool.pop();
        }
        
        void release() {
            assert !VehicleRect.pool.contains(this);
            VehicleRect.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<VehicleRect>();
        }
    }
    
    public static final class VehiclePoly
    {
        public Transform t;
        public float x1;
        public float y1;
        public float x2;
        public float y2;
        public float x3;
        public float y3;
        public float x4;
        public float y4;
        public float z;
        public final Vector2[] borders;
        private static final Quaternionf tempQuat;
        
        VehiclePoly() {
            this.t = new Transform();
            this.borders = new Vector2[4];
            for (int i = 0; i < this.borders.length; ++i) {
                this.borders[i] = new Vector2();
            }
        }
        
        VehiclePoly init(final VehiclePoly vehiclePoly) {
            this.x1 = vehiclePoly.x1;
            this.y1 = vehiclePoly.y1;
            this.x2 = vehiclePoly.x2;
            this.y2 = vehiclePoly.y2;
            this.x3 = vehiclePoly.x3;
            this.y3 = vehiclePoly.y3;
            this.x4 = vehiclePoly.x4;
            this.y4 = vehiclePoly.y4;
            this.z = vehiclePoly.z;
            return this;
        }
        
        VehiclePoly init(final BaseVehicle baseVehicle, final float n) {
            final VehicleScript script = baseVehicle.getScript();
            final Vector3f extents = script.getExtents();
            final Vector3f centerOfMassOffset = script.getCenterOfMassOffset();
            final float n2 = 1.0f;
            final Vector2[] borders = this.borders;
            final Quaternionf tempQuat = VehiclePoly.tempQuat;
            baseVehicle.getWorldTransform(this.t);
            this.t.getRotation(tempQuat);
            final float n3 = extents.x * n2 + n * 2.0f;
            final float n4 = extents.z * n2 + n * 2.0f;
            final float n5 = extents.y * n2 + n * 2.0f;
            final float n6 = n3 / 2.0f;
            final float n7 = n4 / 2.0f;
            final float n8 = n5 / 2.0f;
            final Vector3f tempVec3f_1 = PolygonalMap2.tempVec3f_1;
            if (tempQuat.x < 0.0f) {
                baseVehicle.getWorldPos(centerOfMassOffset.x - n6, 0.0f, centerOfMassOffset.z + n7, tempVec3f_1);
                borders[0].set(tempVec3f_1.x, tempVec3f_1.y);
                baseVehicle.getWorldPos(centerOfMassOffset.x + n6, n8, centerOfMassOffset.z + n7, tempVec3f_1);
                borders[1].set(tempVec3f_1.x, tempVec3f_1.y);
                baseVehicle.getWorldPos(centerOfMassOffset.x + n6, n8, centerOfMassOffset.z - n7, tempVec3f_1);
                borders[2].set(tempVec3f_1.x, tempVec3f_1.y);
                baseVehicle.getWorldPos(centerOfMassOffset.x - n6, 0.0f, centerOfMassOffset.z - n7, tempVec3f_1);
                borders[3].set(tempVec3f_1.x, tempVec3f_1.y);
                this.z = baseVehicle.z;
            }
            else {
                baseVehicle.getWorldPos(centerOfMassOffset.x - n6, n8, centerOfMassOffset.z + n7, tempVec3f_1);
                borders[0].set(tempVec3f_1.x, tempVec3f_1.y);
                baseVehicle.getWorldPos(centerOfMassOffset.x + n6, 0.0f, centerOfMassOffset.z + n7, tempVec3f_1);
                borders[1].set(tempVec3f_1.x, tempVec3f_1.y);
                baseVehicle.getWorldPos(centerOfMassOffset.x + n6, 0.0f, centerOfMassOffset.z - n7, tempVec3f_1);
                borders[2].set(tempVec3f_1.x, tempVec3f_1.y);
                baseVehicle.getWorldPos(centerOfMassOffset.x - n6, n8, centerOfMassOffset.z - n7, tempVec3f_1);
                borders[3].set(tempVec3f_1.x, tempVec3f_1.y);
                this.z = baseVehicle.z;
            }
            int n9 = 0;
            for (int i = 0; i < borders.length; ++i) {
                final Vector2 vector2 = borders[i];
                final Vector2 vector3 = borders[(i + 1) % borders.length];
                n9 += (int)((vector3.x - vector2.x) * (vector3.y + vector2.y));
            }
            if (n9 < 0) {
                final Vector2 vector4 = borders[1];
                final Vector2 vector5 = borders[2];
                borders[1] = borders[3];
                borders[2] = vector5;
                borders[3] = vector4;
            }
            this.x1 = borders[0].x;
            this.y1 = borders[0].y;
            this.x2 = borders[1].x;
            this.y2 = borders[1].y;
            this.x3 = borders[2].x;
            this.y3 = borders[2].y;
            this.x4 = borders[3].x;
            this.y4 = borders[3].y;
            return this;
        }
        
        public static Vector2 lineIntersection(final Vector2 vector2, final Vector2 vector3, final Vector2 vector4, final Vector2 vector5) {
            final Vector2 vector6 = new Vector2();
            final float n = vector2.y - vector3.y;
            final float n2 = vector3.x - vector2.x;
            final float n3 = -n * vector2.x - n2 * vector2.y;
            final float n4 = vector4.y - vector5.y;
            final float n5 = vector5.x - vector4.x;
            final float n6 = -n4 * vector4.x - n5 * vector4.y;
            final float det = QuadranglesIntersection.det(n, n2, n4, n5);
            if (det != 0.0f) {
                vector6.x = -QuadranglesIntersection.det(n3, n2, n6, n5) * 1.0f / det;
                vector6.y = -QuadranglesIntersection.det(n, n3, n4, n6) * 1.0f / det;
                return vector6;
            }
            return null;
        }
        
        VehicleRect getAABB(final VehicleRect vehicleRect) {
            final float min = Math.min(this.x1, Math.min(this.x2, Math.min(this.x3, this.x4)));
            final float min2 = Math.min(this.y1, Math.min(this.y2, Math.min(this.y3, this.y4)));
            return vehicleRect.init(null, (int)min, (int)min2, (int)Math.ceil(Math.max(this.x1, Math.max(this.x2, Math.max(this.x3, this.x4)))) - (int)min, (int)Math.ceil(Math.max(this.y1, Math.max(this.y2, Math.max(this.y3, this.y4)))) - (int)min2, (int)this.z);
        }
        
        float isLeft(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            return (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
        }
        
        public boolean containsPoint(final float n, final float n2) {
            int n3 = 0;
            for (int i = 0; i < 4; ++i) {
                final Vector2 vector2 = this.borders[i];
                final Vector2 vector3 = (i == 3) ? this.borders[0] : this.borders[i + 1];
                if (vector2.y <= n2) {
                    if (vector3.y > n2 && this.isLeft(vector2.x, vector2.y, vector3.x, vector3.y, n, n2) > 0.0f) {
                        ++n3;
                    }
                }
                else if (vector3.y <= n2 && this.isLeft(vector2.x, vector2.y, vector3.x, vector3.y, n, n2) < 0.0f) {
                    --n3;
                }
            }
            return n3 != 0;
        }
        
        static {
            tempQuat = new Quaternionf();
        }
    }
    
    private static final class Vehicle
    {
        final VehiclePoly poly;
        final VehiclePoly polyPlusRadius;
        final TFloatArrayList crawlOffsets;
        float upVectorDot;
        static final ArrayDeque<Vehicle> pool;
        
        private Vehicle() {
            this.poly = new VehiclePoly();
            this.polyPlusRadius = new VehiclePoly();
            this.crawlOffsets = new TFloatArrayList();
        }
        
        static Vehicle alloc() {
            return Vehicle.pool.isEmpty() ? new Vehicle() : Vehicle.pool.pop();
        }
        
        void release() {
            assert !Vehicle.pool.contains(this);
            Vehicle.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<Vehicle>();
        }
    }
    
    private static final class VehicleCluster
    {
        int z;
        final ArrayList<VehicleRect> rects;
        static final ArrayDeque<VehicleCluster> pool;
        
        private VehicleCluster() {
            this.rects = new ArrayList<VehicleRect>();
        }
        
        VehicleCluster init() {
            this.rects.clear();
            return this;
        }
        
        void merge(final VehicleCluster vehicleCluster) {
            for (int i = 0; i < vehicleCluster.rects.size(); ++i) {
                vehicleCluster.rects.get(i).cluster = this;
            }
            this.rects.addAll(vehicleCluster.rects);
            vehicleCluster.rects.clear();
        }
        
        VehicleRect bounds() {
            int min = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int max2 = Integer.MIN_VALUE;
            for (int i = 0; i < this.rects.size(); ++i) {
                final VehicleRect vehicleRect = this.rects.get(i);
                min = Math.min(min, vehicleRect.left());
                min2 = Math.min(min2, vehicleRect.top());
                max = Math.max(max, vehicleRect.right());
                max2 = Math.max(max2, vehicleRect.bottom());
            }
            return VehicleRect.alloc().init(min, min2, max - min, max2 - min2, this.z);
        }
        
        static VehicleCluster alloc() {
            return VehicleCluster.pool.isEmpty() ? new VehicleCluster() : VehicleCluster.pool.pop();
        }
        
        void release() {
            assert !VehicleCluster.pool.contains(this);
            VehicleCluster.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<VehicleCluster>();
        }
    }
    
    private static final class VisibilityGraph
    {
        boolean created;
        VehicleCluster cluster;
        final ArrayList<Node> nodes;
        final ArrayList<Edge> edges;
        final ArrayList<Obstacle> obstacles;
        final ArrayList<Node> intersectNodes;
        final ArrayList<Node> perimeterNodes;
        final ArrayList<Edge> perimeterEdges;
        final ArrayList<Node> obstacleTraceNodes;
        final TIntArrayList splitXY;
        static final CompareIntersection comparator;
        private static final ClusterOutlineGrid clusterOutlineGrid;
        private static final ArrayDeque<VisibilityGraph> pool;
        
        private VisibilityGraph() {
            this.nodes = new ArrayList<Node>();
            this.edges = new ArrayList<Edge>();
            this.obstacles = new ArrayList<Obstacle>();
            this.intersectNodes = new ArrayList<Node>();
            this.perimeterNodes = new ArrayList<Node>();
            this.perimeterEdges = new ArrayList<Edge>();
            this.obstacleTraceNodes = new ArrayList<Node>();
            this.splitXY = new TIntArrayList();
        }
        
        VisibilityGraph init(final VehicleCluster cluster) {
            this.created = false;
            this.cluster = cluster;
            this.edges.clear();
            this.nodes.clear();
            this.obstacles.clear();
            this.intersectNodes.clear();
            this.perimeterEdges.clear();
            this.perimeterNodes.clear();
            return this;
        }
        
        void addEdgesForVehicle(final Vehicle vehicle) {
            final VehiclePoly polyPlusRadius = vehicle.polyPlusRadius;
            final int n = (int)polyPlusRadius.z;
            final Node init = Node.alloc().init(polyPlusRadius.x1, polyPlusRadius.y1, n);
            final Node init2 = Node.alloc().init(polyPlusRadius.x2, polyPlusRadius.y2, n);
            final Node init3 = Node.alloc().init(polyPlusRadius.x3, polyPlusRadius.y3, n);
            final Node init4 = Node.alloc().init(polyPlusRadius.x4, polyPlusRadius.y4, n);
            final Obstacle init5 = Obstacle.alloc().init(vehicle);
            this.obstacles.add(init5);
            final Edge init6 = Edge.alloc().init(init, init2, init5, init5.outer);
            final Edge init7 = Edge.alloc().init(init2, init3, init5, init5.outer);
            final Edge init8 = Edge.alloc().init(init3, init4, init5, init5.outer);
            Edge edge = Edge.alloc().init(init4, init, init5, init5.outer);
            init5.outer.add(init6);
            init5.outer.add(init7);
            init5.outer.add(init8);
            init5.outer.add(edge);
            init5.calcBounds();
            this.nodes.add(init);
            this.nodes.add(init2);
            this.nodes.add(init3);
            this.nodes.add(init4);
            this.edges.add(init6);
            this.edges.add(init7);
            this.edges.add(init8);
            this.edges.add(edge);
            if (vehicle.upVectorDot < 0.95f) {
                return;
            }
            init5.nodeCrawlFront = Node.alloc().init((polyPlusRadius.x1 + polyPlusRadius.x2) / 2.0f, (polyPlusRadius.y1 + polyPlusRadius.y2) / 2.0f, n);
            init5.nodeCrawlRear = Node.alloc().init((polyPlusRadius.x3 + polyPlusRadius.x4) / 2.0f, (polyPlusRadius.y3 + polyPlusRadius.y4) / 2.0f, n);
            final Node nodeCrawlFront = init5.nodeCrawlFront;
            nodeCrawlFront.flags |= 0x1;
            final Node nodeCrawlRear = init5.nodeCrawlRear;
            nodeCrawlRear.flags |= 0x1;
            this.nodes.add(init5.nodeCrawlFront);
            this.nodes.add(init5.nodeCrawlRear);
            final Edge split = init6.split(init5.nodeCrawlFront);
            final Edge split2 = init8.split(init5.nodeCrawlRear);
            this.edges.add(split);
            this.edges.add(split2);
            final BaseVehicle.Vector2fObjectPool vector2fObjectPool = BaseVehicle.TL_vector2f_pool.get();
            final Vector2f vector2f = vector2fObjectPool.alloc();
            final Vector2f vector2f2 = vector2fObjectPool.alloc();
            init5.crawlNodes.clear();
            for (int i = 0; i < vehicle.crawlOffsets.size(); ++i) {
                final float value = vehicle.crawlOffsets.get(i);
                vector2f.set(init3.x, init3.y);
                vector2f2.set(init2.x, init2.y);
                vector2f2.sub((Vector2fc)vector2f).mul(value).add((Vector2fc)vector2f);
                final Node init9;
                final Node node = init9 = Node.alloc().init(vector2f2.x, vector2f2.y, n);
                init9.flags |= 0x1;
                vector2f.set(init4.x, init4.y);
                vector2f2.set(init.x, init.y);
                vector2f2.sub((Vector2fc)vector2f).mul(value).add((Vector2fc)vector2f);
                final Node init10;
                final Node node2 = init10 = Node.alloc().init(vector2f2.x, vector2f2.y, n);
                init10.flags |= 0x1;
                final Node init11;
                final Node node3 = init11 = Node.alloc().init((node.x + node2.x) / 2.0f, (node.y + node2.y) / 2.0f, n);
                init11.flags |= 0x3;
                init5.crawlNodes.add(node);
                init5.crawlNodes.add(node3);
                init5.crawlNodes.add(node2);
                this.nodes.add(node);
                this.nodes.add(node3);
                this.nodes.add(node2);
                final Edge split3 = init7.split(node);
                edge = edge.split(node2);
                this.edges.add(split3);
                this.edges.add(edge);
            }
            vector2fObjectPool.release(vector2f);
            vector2fObjectPool.release(vector2f2);
        }
        
        boolean isVisible(final Node node, final Node node2) {
            if (node.sharesEdge(node2)) {
                return !node.onSameShapeButDoesNotShareAnEdge(node2);
            }
            if (node.sharesShape(node2)) {
                return false;
            }
            for (int i = 0; i < this.edges.size(); ++i) {
                if (this.intersects(node, node2, this.edges.get(i))) {
                    return false;
                }
            }
            for (int j = 0; j < this.perimeterEdges.size(); ++j) {
                if (this.intersects(node, node2, this.perimeterEdges.get(j))) {
                    return false;
                }
            }
            return true;
        }
        
        boolean intersects(final Node node, final Node node2, final Edge edge) {
            return !edge.hasNode(node) && !edge.hasNode(node2) && Line2D.linesIntersect(node.x, node.y, node2.x, node2.y, edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y);
        }
        
        public Intersection getIntersection(final Edge edge, final Edge edge2) {
            final float x = edge.node1.x;
            final float y = edge.node1.y;
            final float x2 = edge.node2.x;
            final float y2 = edge.node2.y;
            final float x3 = edge2.node1.x;
            final float y3 = edge2.node1.y;
            final float x4 = edge2.node2.x;
            final float y4 = edge2.node2.y;
            final double n = (y4 - y3) * (x2 - x) - (x4 - x3) * (y2 - y);
            if (n == 0.0) {
                return null;
            }
            final double n2 = ((x4 - x3) * (y - y3) - (y4 - y3) * (x - x3)) / n;
            final double n3 = ((x2 - x) * (y - y3) - (y2 - y) * (x - x3)) / n;
            if (n2 >= 0.0 && n2 <= 1.0 && n3 >= 0.0 && n3 <= 1.0) {
                return new Intersection(edge, edge2, (float)n2, (float)n3, (float)(x + n2 * (x2 - x)), (float)(y + n2 * (y2 - y)));
            }
            return null;
        }
        
        @Deprecated
        void addWorldObstacles() {
            final VehicleRect bounds;
            final VehicleRect vehicleRect = bounds = this.cluster.bounds();
            --bounds.x;
            final VehicleRect vehicleRect2 = vehicleRect;
            --vehicleRect2.y;
            final VehicleRect vehicleRect3 = vehicleRect;
            vehicleRect3.w += 3;
            final VehicleRect vehicleRect4 = vehicleRect;
            vehicleRect4.h += 3;
            final ObjectOutline[][] array = new ObjectOutline[vehicleRect.w][vehicleRect.h];
            final int z = this.cluster.z;
            for (int i = vehicleRect.top(); i < vehicleRect.bottom() - 1; ++i) {
                for (int j = vehicleRect.left(); j < vehicleRect.right() - 1; ++j) {
                    final Square square = PolygonalMap2.instance.getSquare(j, i, z);
                    if (square != null && this.contains(square, 1)) {
                        if (square.has(504) || square.isReallySolid()) {
                            ObjectOutline.setSolid(j - vehicleRect.left(), i - vehicleRect.top(), z, array);
                        }
                        if (square.has(2)) {
                            ObjectOutline.setWest(j - vehicleRect.left(), i - vehicleRect.top(), z, array);
                        }
                        if (square.has(4)) {
                            ObjectOutline.setNorth(j - vehicleRect.left(), i - vehicleRect.top(), z, array);
                        }
                        if (square.has(262144)) {
                            ObjectOutline.setWest(j - vehicleRect.left() + 1, i - vehicleRect.top(), z, array);
                        }
                        if (square.has(524288)) {
                            ObjectOutline.setNorth(j - vehicleRect.left(), i - vehicleRect.top() + 1, z, array);
                        }
                    }
                }
            }
            for (int k = 0; k < vehicleRect.h; ++k) {
                for (int l = 0; l < vehicleRect.w; ++l) {
                    final ObjectOutline value = ObjectOutline.get(l, k, z, array);
                    if (value != null && value.nw && value.nw_w && value.nw_n) {
                        value.trace(array, this.obstacleTraceNodes);
                        if (!value.nodes.isEmpty()) {
                            final Obstacle init = Obstacle.alloc().init((IsoGridSquare)null);
                            for (int index = 0; index < value.nodes.size() - 1; ++index) {
                                final Node e = value.nodes.get(index);
                                final Node node = value.nodes.get(index + 1);
                                final Node node2 = e;
                                node2.x += vehicleRect.left();
                                final Node node3 = e;
                                node3.y += vehicleRect.top();
                                if (!this.contains(e.x, e.y, e.z)) {
                                    e.ignore = true;
                                }
                                init.outer.add(Edge.alloc().init(e, node, init, init.outer));
                                this.nodes.add(e);
                            }
                            init.calcBounds();
                            this.obstacles.add(init);
                            this.edges.addAll(init.outer);
                        }
                    }
                }
            }
            for (int n = 0; n < vehicleRect.h; ++n) {
                for (int n2 = 0; n2 < vehicleRect.w; ++n2) {
                    if (array[n2][n] != null) {
                        array[n2][n].release();
                    }
                }
            }
            vehicleRect.release();
        }
        
        void addWorldObstaclesClipper() {
            final VehicleRect bounds;
            final VehicleRect vehicleRect = bounds = this.cluster.bounds();
            --bounds.x;
            final VehicleRect vehicleRect2 = vehicleRect;
            --vehicleRect2.y;
            final VehicleRect vehicleRect3 = vehicleRect;
            vehicleRect3.w += 2;
            final VehicleRect vehicleRect4 = vehicleRect;
            vehicleRect4.h += 2;
            if (PolygonalMap2.instance.clipperThread == null) {
                PolygonalMap2.instance.clipperThread = new Clipper();
            }
            final Clipper clipperThread = PolygonalMap2.instance.clipperThread;
            clipperThread.clear();
            final int z = this.cluster.z;
            for (int i = vehicleRect.top(); i < vehicleRect.bottom(); ++i) {
                for (int j = vehicleRect.left(); j < vehicleRect.right(); ++j) {
                    final Square square = PolygonalMap2.instance.getSquare(j, i, z);
                    if (square != null && this.contains(square, 1)) {
                        if (square.has(504) || square.isReallySolid()) {
                            clipperThread.addAABB(j - 0.3f, i - 0.3f, j + 1 + 0.3f, i + 1 + 0.3f);
                        }
                        if (square.has(2)) {
                            clipperThread.addAABB(j - 0.3f, i - 0.3f, j + 0.3f, i + 1 + 0.3f);
                        }
                        if (square.has(4)) {
                            clipperThread.addAABB(j - 0.3f, i - 0.3f, j + 1 + 0.3f, i + 0.3f);
                        }
                    }
                }
            }
            vehicleRect.release();
            final ByteBuffer xyBufferThread = PolygonalMap2.instance.xyBufferThread;
            for (int generatePolygons = clipperThread.generatePolygons(), k = 0; k < generatePolygons; ++k) {
                xyBufferThread.clear();
                clipperThread.getPolygon(k, xyBufferThread);
                final Obstacle init = Obstacle.alloc().init((IsoGridSquare)null);
                this.getEdgesFromBuffer(xyBufferThread, init, true, z);
                for (short short1 = xyBufferThread.getShort(), n = 0; n < short1; ++n) {
                    this.getEdgesFromBuffer(xyBufferThread, init, false, z);
                }
                init.calcBounds();
                this.obstacles.add(init);
                this.edges.addAll(init.outer);
                for (int l = 0; l < init.inner.size(); ++l) {
                    this.edges.addAll(init.inner.get(l));
                }
            }
        }
        
        void getEdgesFromBuffer(final ByteBuffer byteBuffer, final Obstacle obstacle, final boolean b, final int n) {
            final short short1 = byteBuffer.getShort();
            if (short1 < 3) {
                byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
                return;
            }
            EdgeRing e = obstacle.outer;
            if (!b) {
                e = EdgeRing.alloc();
                e.clear();
                obstacle.inner.add(e);
            }
            final int size = this.nodes.size();
            for (int i = short1 - 1; i >= 0; --i) {
                this.nodes.add(Node.alloc().init(byteBuffer.getFloat(), byteBuffer.getFloat(), n));
            }
            for (int j = size; j < this.nodes.size() - 1; ++j) {
                final Node node = this.nodes.get(j);
                final Node node2 = this.nodes.get(j + 1);
                if (!this.contains(node.x, node.y, node.z)) {
                    node.ignore = true;
                }
                e.add(Edge.alloc().init(node, node2, obstacle, e));
            }
            e.add(Edge.alloc().init(this.nodes.get(this.nodes.size() - 1), this.nodes.get(size), obstacle, e));
        }
        
        void trySplit(final Edge edge, final VehicleRect vehicleRect, final TIntArrayList list) {
            if (Math.abs(edge.node1.x - edge.node2.x) > Math.abs(edge.node1.y - edge.node2.y)) {
                final float min = Math.min(edge.node1.x, edge.node2.x);
                final float max = Math.max(edge.node1.x, edge.node2.x);
                final float y = edge.node1.y;
                if (vehicleRect.left() > min && vehicleRect.left() < max && vehicleRect.top() < y && vehicleRect.bottom() > y && !list.contains(vehicleRect.left()) && !this.contains(vehicleRect.left() - 0.5f, y, this.cluster.z)) {
                    list.add(vehicleRect.left());
                }
                if (vehicleRect.right() > min && vehicleRect.right() < max && vehicleRect.top() < y && vehicleRect.bottom() > y && !list.contains(vehicleRect.right()) && !this.contains(vehicleRect.right() + 0.5f, y, this.cluster.z)) {
                    list.add(vehicleRect.right());
                }
            }
            else {
                final float min2 = Math.min(edge.node1.y, edge.node2.y);
                final float max2 = Math.max(edge.node1.y, edge.node2.y);
                final float x = edge.node1.x;
                if (vehicleRect.top() > min2 && vehicleRect.top() < max2 && vehicleRect.left() < x && vehicleRect.right() > x && !list.contains(vehicleRect.top()) && !this.contains(x, vehicleRect.top() - 0.5f, this.cluster.z)) {
                    list.add(vehicleRect.top());
                }
                if (vehicleRect.bottom() > min2 && vehicleRect.bottom() < max2 && vehicleRect.left() < x && vehicleRect.right() > x && !list.contains(vehicleRect.bottom()) && !this.contains(x, vehicleRect.bottom() + 0.5f, this.cluster.z)) {
                    list.add(vehicleRect.bottom());
                }
            }
        }
        
        void splitWorldObstacleEdges(final EdgeRing edgeRing) {
            for (int i = edgeRing.size() - 1; i >= 0; --i) {
                final Edge edge = edgeRing.get(i);
                this.splitXY.clear();
                for (int j = 0; j < this.cluster.rects.size(); ++j) {
                    this.trySplit(edge, this.cluster.rects.get(j), this.splitXY);
                }
                if (!this.splitXY.isEmpty()) {
                    this.splitXY.sort();
                    if (Math.abs(edge.node1.x - edge.node2.x) > Math.abs(edge.node1.y - edge.node2.y)) {
                        if (edge.node1.x < edge.node2.x) {
                            for (int k = this.splitXY.size() - 1; k >= 0; --k) {
                                final Node init = Node.alloc().init((float)this.splitXY.get(k), edge.node1.y, this.cluster.z);
                                final Edge split = edge.split(init);
                                this.nodes.add(init);
                                this.edges.add(split);
                            }
                        }
                        else {
                            for (int l = 0; l < this.splitXY.size(); ++l) {
                                final Node init2 = Node.alloc().init((float)this.splitXY.get(l), edge.node1.y, this.cluster.z);
                                final Edge split2 = edge.split(init2);
                                this.nodes.add(init2);
                                this.edges.add(split2);
                            }
                        }
                    }
                    else if (edge.node1.y < edge.node2.y) {
                        for (int n = this.splitXY.size() - 1; n >= 0; --n) {
                            final Node init3 = Node.alloc().init(edge.node1.x, (float)this.splitXY.get(n), this.cluster.z);
                            final Edge split3 = edge.split(init3);
                            this.nodes.add(init3);
                            this.edges.add(split3);
                        }
                    }
                    else {
                        for (int n2 = 0; n2 < this.splitXY.size(); ++n2) {
                            final Node init4 = Node.alloc().init(edge.node1.x, (float)this.splitXY.get(n2), this.cluster.z);
                            final Edge split4 = edge.split(init4);
                            this.nodes.add(init4);
                            this.edges.add(split4);
                        }
                    }
                }
            }
        }
        
        void getStairSquares(final ArrayList<Square> list) {
            final VehicleRect bounds;
            final VehicleRect vehicleRect = bounds = this.cluster.bounds();
            bounds.x -= 4;
            final VehicleRect vehicleRect2 = vehicleRect;
            vehicleRect2.w += 4;
            final VehicleRect vehicleRect3 = vehicleRect;
            ++vehicleRect3.w;
            final VehicleRect vehicleRect4 = vehicleRect;
            vehicleRect4.y -= 4;
            final VehicleRect vehicleRect5 = vehicleRect;
            vehicleRect5.h += 4;
            final VehicleRect vehicleRect6 = vehicleRect;
            ++vehicleRect6.h;
            for (int i = vehicleRect.top(); i < vehicleRect.bottom(); ++i) {
                for (int j = vehicleRect.left(); j < vehicleRect.right(); ++j) {
                    final Square square = PolygonalMap2.instance.getSquare(j, i, this.cluster.z);
                    if (square != null) {
                        if (square.has(72) && !list.contains(square)) {
                            list.add(square);
                        }
                    }
                }
            }
            vehicleRect.release();
        }
        
        void getCanPathSquares(final ArrayList<Square> list) {
            final VehicleRect bounds;
            final VehicleRect vehicleRect = bounds = this.cluster.bounds();
            --bounds.x;
            final VehicleRect vehicleRect2 = vehicleRect;
            vehicleRect2.w += 2;
            final VehicleRect vehicleRect3 = vehicleRect;
            --vehicleRect3.y;
            final VehicleRect vehicleRect4 = vehicleRect;
            vehicleRect4.h += 2;
            for (int i = vehicleRect.top(); i < vehicleRect.bottom(); ++i) {
                for (int j = vehicleRect.left(); j < vehicleRect.right(); ++j) {
                    final Square square = PolygonalMap2.instance.getSquare(j, i, this.cluster.z);
                    if (square != null) {
                        if ((square.isCanPathW() || square.isCanPathN()) && !list.contains(square)) {
                            list.add(square);
                        }
                    }
                }
            }
            vehicleRect.release();
        }
        
        void connectVehicleCrawlNodes() {
            for (int i = 0; i < this.obstacles.size(); ++i) {
                final Obstacle obstacle = this.obstacles.get(i);
                if (obstacle.vehicle != null) {
                    if (obstacle.nodeCrawlFront != null) {
                        for (int j = 0; j < obstacle.crawlNodes.size(); j += 3) {
                            final Node node = obstacle.crawlNodes.get(j);
                            final Node node2 = obstacle.crawlNodes.get(j + 1);
                            final Node node3 = obstacle.crawlNodes.get(j + 2);
                            PolygonalMap2.instance.connectTwoNodes(node, node2);
                            PolygonalMap2.instance.connectTwoNodes(node3, node2);
                            if (j + 3 < obstacle.crawlNodes.size()) {
                                PolygonalMap2.instance.connectTwoNodes(node2, obstacle.crawlNodes.get(j + 3 + 1));
                            }
                        }
                        if (!obstacle.crawlNodes.isEmpty()) {
                            PolygonalMap2.instance.connectTwoNodes(obstacle.nodeCrawlFront, obstacle.crawlNodes.get(obstacle.crawlNodes.size() - 2));
                            PolygonalMap2.instance.connectTwoNodes(obstacle.nodeCrawlRear, obstacle.crawlNodes.get(1));
                        }
                        if (!obstacle.crawlNodes.isEmpty()) {
                            final ImmutableRectF bounds = obstacle.bounds;
                            final int n = (int)bounds.x;
                            final int n2 = (int)bounds.y;
                            final int n3 = (int)Math.ceil(bounds.right());
                            for (int n4 = (int)Math.ceil(bounds.bottom()), k = n2; k < n4; ++k) {
                                for (int l = n; l < n3; ++l) {
                                    final Square square = PolygonalMap2.instance.getSquare(l, k, this.cluster.z);
                                    if (square != null) {
                                        if (obstacle.isPointInside(l + 0.5f, k + 0.5f)) {
                                            final Node nodeForSquare = PolygonalMap2.instance.getNodeForSquare(square);
                                            for (int index = nodeForSquare.visible.size() - 1; index >= 0; --index) {
                                                final Connection connection = nodeForSquare.visible.get(index);
                                                if (connection.has(1)) {
                                                    final Node otherNode = connection.otherNode(nodeForSquare);
                                                    final Node closestInteriorCrawlNode = obstacle.getClosestInteriorCrawlNode(nodeForSquare.x, nodeForSquare.y);
                                                    for (int index2 = 0; index2 < obstacle.outer.size(); ++index2) {
                                                        final Edge edge = obstacle.outer.get(index2);
                                                        final float x = edge.node1.x;
                                                        final float y = edge.node1.y;
                                                        final float x2 = edge.node2.x;
                                                        final float y2 = edge.node2.y;
                                                        final float x3 = connection.node1.x;
                                                        final float y3 = connection.node1.y;
                                                        final float x4 = connection.node2.x;
                                                        final float y4 = connection.node2.y;
                                                        final double n5 = (y4 - y3) * (x2 - x) - (x4 - x3) * (y2 - y);
                                                        if (n5 != 0.0) {
                                                            final double n6 = ((x4 - x3) * (y - y3) - (y4 - y3) * (x - x3)) / n5;
                                                            final double n7 = ((x2 - x) * (y - y3) - (y2 - y) * (x - x3)) / n5;
                                                            if (n6 >= 0.0 && n6 <= 1.0 && n7 >= 0.0 && n7 <= 1.0) {
                                                                final Node init;
                                                                final Node e = init = Node.alloc().init((float)(x + n6 * (x2 - x)), (float)(y + n6 * (y2 - y)), this.cluster.z);
                                                                init.flags |= 0x1;
                                                                final boolean connectedTo = edge.node1.isConnectedTo(edge.node2);
                                                                final Edge split = edge.split(e);
                                                                if (connectedTo) {
                                                                    PolygonalMap2.instance.connectTwoNodes(edge.node1, edge.node2);
                                                                    PolygonalMap2.instance.connectTwoNodes(split.node1, split.node2);
                                                                }
                                                                this.edges.add(split);
                                                                this.nodes.add(e);
                                                                PolygonalMap2.instance.connectTwoNodes(otherNode, e, (connection.flags & 0x2) | 0x1);
                                                                PolygonalMap2.instance.connectTwoNodes(e, closestInteriorCrawlNode, 0);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    PolygonalMap2.instance.breakConnection(connection);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for (int index3 = i + 1; index3 < this.obstacles.size(); ++index3) {
                            final Obstacle obstacle2 = this.obstacles.get(index3);
                            if (obstacle2.vehicle != null) {
                                if (obstacle2.nodeCrawlFront != null) {
                                    obstacle.connectCrawlNodes(this, obstacle2);
                                    obstacle2.connectCrawlNodes(this, obstacle);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        void checkEdgeIntersection() {
            for (int i = 0; i < this.obstacles.size(); ++i) {
                final Obstacle obstacle = this.obstacles.get(i);
                for (int j = i + 1; j < this.obstacles.size(); ++j) {
                    final Obstacle obstacle2 = this.obstacles.get(j);
                    if (obstacle.bounds.intersects(obstacle2.bounds)) {
                        this.checkEdgeIntersection(obstacle.outer, obstacle2.outer);
                        for (int k = 0; k < obstacle2.inner.size(); ++k) {
                            this.checkEdgeIntersection(obstacle.outer, obstacle2.inner.get(k));
                        }
                        for (int l = 0; l < obstacle.inner.size(); ++l) {
                            final EdgeRing edgeRing = obstacle.inner.get(l);
                            this.checkEdgeIntersection(edgeRing, obstacle2.outer);
                            for (int index = 0; index < obstacle2.inner.size(); ++index) {
                                this.checkEdgeIntersection(edgeRing, obstacle2.inner.get(index));
                            }
                        }
                    }
                }
            }
            for (int index2 = 0; index2 < this.obstacles.size(); ++index2) {
                final Obstacle obstacle3 = this.obstacles.get(index2);
                this.checkEdgeIntersectionSplit(obstacle3.outer);
                for (int index3 = 0; index3 < obstacle3.inner.size(); ++index3) {
                    this.checkEdgeIntersectionSplit(obstacle3.inner.get(index3));
                }
            }
        }
        
        void checkEdgeIntersection(final EdgeRing edgeRing, final EdgeRing edgeRing2) {
            for (int i = 0; i < edgeRing.size(); ++i) {
                final Edge edge = edgeRing.get(i);
                for (int j = 0; j < edgeRing2.size(); ++j) {
                    final Edge edge2 = edgeRing2.get(j);
                    if (this.intersects(edge.node1, edge.node2, edge2)) {
                        final Intersection intersection = this.getIntersection(edge, edge2);
                        if (intersection != null) {
                            edge.intersections.add(intersection);
                            edge2.intersections.add(intersection);
                            this.nodes.add(intersection.nodeSplit);
                            this.intersectNodes.add(intersection.nodeSplit);
                        }
                    }
                }
            }
        }
        
        void checkEdgeIntersectionSplit(final EdgeRing edgeRing) {
            for (int i = edgeRing.size() - 1; i >= 0; --i) {
                final Edge edge = edgeRing.get(i);
                if (!edge.intersections.isEmpty()) {
                    VisibilityGraph.comparator.edge = edge;
                    Collections.sort(edge.intersections, VisibilityGraph.comparator);
                    for (int j = edge.intersections.size() - 1; j >= 0; --j) {
                        this.edges.add(edge.intersections.get(j).split(edge));
                    }
                }
            }
        }
        
        void checkNodesInObstacles() {
            for (int i = 0; i < this.nodes.size(); ++i) {
                final Node node = this.nodes.get(i);
                for (int j = 0; j < this.obstacles.size(); ++j) {
                    if (this.obstacles.get(j).isNodeInsideOf(node)) {
                        node.ignore = true;
                        break;
                    }
                }
            }
            for (int k = 0; k < this.perimeterNodes.size(); ++k) {
                final Node node2 = this.perimeterNodes.get(k);
                for (int l = 0; l < this.obstacles.size(); ++l) {
                    if (this.obstacles.get(l).isNodeInsideOf(node2)) {
                        node2.ignore = true;
                        break;
                    }
                }
            }
        }
        
        void addPerimeterEdges() {
            final VehicleRect bounds;
            final VehicleRect vehicleRect = bounds = this.cluster.bounds();
            --bounds.x;
            final VehicleRect vehicleRect2 = vehicleRect;
            --vehicleRect2.y;
            final VehicleRect vehicleRect3 = vehicleRect;
            vehicleRect3.w += 2;
            final VehicleRect vehicleRect4 = vehicleRect;
            vehicleRect4.h += 2;
            final ClusterOutlineGrid setSize = VisibilityGraph.clusterOutlineGrid.setSize(vehicleRect.w, vehicleRect.h);
            final int z = this.cluster.z;
            for (int i = 0; i < this.cluster.rects.size(); ++i) {
                final VehicleRect vehicleRect5 = this.cluster.rects.get(i);
                final VehicleRect init = VehicleRect.alloc().init(vehicleRect5.x - 1, vehicleRect5.y - 1, vehicleRect5.w + 2, vehicleRect5.h + 2, vehicleRect5.z);
                for (int j = init.top(); j < init.bottom(); ++j) {
                    for (int k = init.left(); k < init.right(); ++k) {
                        setSize.setInner(k - vehicleRect.left(), j - vehicleRect.top(), z);
                    }
                }
                init.release();
            }
            for (int l = 0; l < vehicleRect.h; ++l) {
                for (int n = 0; n < vehicleRect.w; ++n) {
                    final ClusterOutline value = setSize.get(n, l, z);
                    if (value.inner) {
                        if (!setSize.isInner(n - 1, l, z)) {
                            value.w = true;
                        }
                        if (!setSize.isInner(n, l - 1, z)) {
                            value.n = true;
                        }
                        if (!setSize.isInner(n + 1, l, z)) {
                            value.e = true;
                        }
                        if (!setSize.isInner(n, l + 1, z)) {
                            value.s = true;
                        }
                    }
                }
            }
            for (int n2 = 0; n2 < vehicleRect.h; ++n2) {
                for (int n3 = 0; n3 < vehicleRect.w; ++n3) {
                    final ClusterOutline value2 = setSize.get(n3, n2, z);
                    if (value2 != null && (value2.w || value2.n || value2.e || value2.s || value2.innerCorner)) {
                        final Square square = PolygonalMap2.instance.getSquare(vehicleRect.x + n3, vehicleRect.y + n2, z);
                        if (square != null && !square.isNonThumpableSolid() && !square.has(504)) {
                            final Node nodeForSquare;
                            final Node e = nodeForSquare = PolygonalMap2.instance.getNodeForSquare(square);
                            nodeForSquare.flags |= 0x8;
                            e.addGraph(this);
                            this.perimeterNodes.add(e);
                        }
                    }
                    if (value2 != null && value2.n && value2.w && value2.inner && !(value2.tw | value2.tn | value2.te | value2.ts)) {
                        final ArrayList<Node> trace = setSize.trace(value2);
                        if (!trace.isEmpty()) {
                            for (int index = 0; index < trace.size() - 1; ++index) {
                                final Node node = trace.get(index);
                                final Node node2 = trace.get(index + 1);
                                final Node node3 = node;
                                node3.x += vehicleRect.left();
                                final Node node4 = node;
                                node4.y += vehicleRect.top();
                                this.perimeterEdges.add(Edge.alloc().init(node, node2, null, null));
                            }
                            if (trace.get(trace.size() - 1) != trace.get(0)) {
                                final Node node5 = trace.get(trace.size() - 1);
                                node5.x += vehicleRect.left();
                                final Node node6 = trace.get(trace.size() - 1);
                                node6.y += vehicleRect.top();
                            }
                        }
                    }
                }
            }
            setSize.releaseElements();
            vehicleRect.release();
        }
        
        void calculateNodeVisibility() {
            final ArrayList<Node> list = new ArrayList<Node>();
            list.addAll(this.nodes);
            list.addAll(this.perimeterNodes);
            for (int i = 0; i < list.size(); ++i) {
                final Node node = list.get(i);
                if (!node.ignore) {
                    if (node.square == null || !node.square.has(504)) {
                        for (int j = i + 1; j < list.size(); ++j) {
                            final Node node2 = list.get(j);
                            if (!node2.ignore) {
                                if (node2.square == null || !node2.square.has(504)) {
                                    if (!node.hasFlag(8) || !node2.hasFlag(8)) {
                                        if (node.isConnectedTo(node2)) {
                                            if (node.square != null) {
                                                if (node.square.isCanPathW()) {
                                                    continue;
                                                }
                                                if (node.square.isCanPathN()) {
                                                    continue;
                                                }
                                            }
                                            if (node2.square != null) {
                                                if (node2.square.isCanPathW()) {
                                                    continue;
                                                }
                                                if (node2.square.isCanPathN()) {
                                                    continue;
                                                }
                                            }
                                            assert false;
                                        }
                                        else if (this.isVisible(node, node2)) {
                                            PolygonalMap2.instance.connectTwoNodes(node, node2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        void addNode(final Node e) {
            if (this.created && !e.ignore) {
                final ArrayList<Node> list = new ArrayList<Node>();
                list.addAll(this.nodes);
                list.addAll(this.perimeterNodes);
                for (int i = 0; i < list.size(); ++i) {
                    final Node node = list.get(i);
                    if (!node.ignore) {
                        if (this.isVisible(node, e)) {
                            PolygonalMap2.instance.connectTwoNodes(e, node);
                        }
                    }
                }
            }
            this.nodes.add(e);
        }
        
        void removeNode(final Node o) {
            this.nodes.remove(o);
            for (int i = o.visible.size() - 1; i >= 0; --i) {
                PolygonalMap2.instance.breakConnection(o.visible.get(i));
            }
        }
        
        boolean contains(final float n, final float n2, final int n3) {
            for (int i = 0; i < this.cluster.rects.size(); ++i) {
                if (this.cluster.rects.get(i).containsPoint(n, n2, (float)n3)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean contains(final float n, final float n2, final int n3, final int n4) {
            for (int i = 0; i < this.cluster.rects.size(); ++i) {
                if (this.cluster.rects.get(i).containsPoint(n, n2, (float)n3, n4)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean contains(final Square square) {
            for (int i = 0; i < this.cluster.rects.size(); ++i) {
                if (this.cluster.rects.get(i).containsPoint(square.x + 0.5f, square.y + 0.5f, (float)square.z)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean contains(final Square square, final int n) {
            for (int i = 0; i < this.cluster.rects.size(); ++i) {
                if (this.cluster.rects.get(i).containsPoint(square.x + 0.5f, square.y + 0.5f, (float)square.z, n)) {
                    return true;
                }
            }
            return false;
        }
        
        private int getPointOutsideObstacles(final float n, final float n2, final float n3, final AdjustStartEndNodeData adjustStartEndNodeData) {
            final ClosestPointOnEdge closestPointOnEdge = PolygonalMap2.instance.closestPointOnEdge;
            double distSq = Double.MAX_VALUE;
            Edge edge = null;
            Node node = null;
            float x = 0.0f;
            float y = 0.0f;
            for (int i = 0; i < this.obstacles.size(); ++i) {
                final Obstacle obstacle = this.obstacles.get(i);
                if (obstacle.bounds.containsPoint(n, n2) && obstacle.isPointInside(n, n2)) {
                    obstacle.getClosestPointOnEdge(n, n2, closestPointOnEdge);
                    if (closestPointOnEdge.edge != null && closestPointOnEdge.distSq < distSq) {
                        distSq = closestPointOnEdge.distSq;
                        edge = closestPointOnEdge.edge;
                        node = closestPointOnEdge.node;
                        x = closestPointOnEdge.point.x;
                        y = closestPointOnEdge.point.y;
                    }
                }
            }
            if (edge == null) {
                return 0;
            }
            closestPointOnEdge.edge = edge;
            closestPointOnEdge.node = node;
            closestPointOnEdge.point.set(x, y);
            closestPointOnEdge.distSq = distSq;
            if (edge.obstacle.splitEdgeAtNearestPoint(closestPointOnEdge, (int)n3, adjustStartEndNodeData)) {
                adjustStartEndNodeData.graph = this;
                if (adjustStartEndNodeData.isNodeNew) {
                    this.edges.add(adjustStartEndNodeData.newEdge);
                    this.addNode(adjustStartEndNodeData.node);
                }
                return 1;
            }
            return -1;
        }
        
        Node getClosestNodeTo(final float n, final float n2) {
            Node node = null;
            float n3 = Float.MAX_VALUE;
            for (int i = 0; i < this.nodes.size(); ++i) {
                final Node node2 = this.nodes.get(i);
                final float distanceToSquared = IsoUtils.DistanceToSquared(node2.x, node2.y, n, n2);
                if (distanceToSquared < n3) {
                    node = node2;
                    n3 = distanceToSquared;
                }
            }
            return node;
        }
        
        void create() {
            for (int i = 0; i < this.cluster.rects.size(); ++i) {
                this.addEdgesForVehicle(this.cluster.rects.get(i).vehicle);
            }
            this.addWorldObstaclesClipper();
            for (int j = 0; j < this.obstacles.size(); ++j) {
                final Obstacle obstacle = this.obstacles.get(j);
                if (obstacle.vehicle == null) {
                    this.splitWorldObstacleEdges(obstacle.outer);
                    for (int k = 0; k < obstacle.inner.size(); ++k) {
                        this.splitWorldObstacleEdges(obstacle.inner.get(k));
                    }
                }
            }
            this.checkEdgeIntersection();
            this.checkNodesInObstacles();
            this.calculateNodeVisibility();
            this.connectVehicleCrawlNodes();
            this.created = true;
        }
        
        static VisibilityGraph alloc() {
            return VisibilityGraph.pool.isEmpty() ? new VisibilityGraph() : VisibilityGraph.pool.pop();
        }
        
        void release() {
            for (int i = 0; i < this.nodes.size(); ++i) {
                if (!PolygonalMap2.instance.squareToNode.containsValue((Object)this.nodes.get(i))) {
                    this.nodes.get(i).release();
                }
            }
            for (int j = 0; j < this.perimeterEdges.size(); ++j) {
                this.perimeterEdges.get(j).node1.release();
                this.perimeterEdges.get(j).release();
            }
            for (int k = 0; k < this.obstacles.size(); ++k) {
                this.obstacles.get(k).release();
            }
            for (int l = 0; l < this.cluster.rects.size(); ++l) {
                this.cluster.rects.get(l).release();
            }
            this.cluster.release();
            assert !VisibilityGraph.pool.contains(this);
            VisibilityGraph.pool.push(this);
        }
        
        void render() {
            float n = 1.0f;
            for (final Edge edge : this.perimeterEdges) {
                LineDrawer.addLine(edge.node1.x, edge.node1.y, (float)this.cluster.z, edge.node2.x, edge.node2.y, (float)this.cluster.z, n, 0.5f, 0.5f, null, true);
                n = 1.0f - n;
            }
            for (final Obstacle obstacle : this.obstacles) {
                float n2 = 1.0f;
                for (final Edge edge2 : obstacle.outer) {
                    LineDrawer.addLine(edge2.node1.x, edge2.node1.y, (float)this.cluster.z, edge2.node2.x, edge2.node2.y, (float)this.cluster.z, n2, 0.5f, 0.5f, null, true);
                    n2 = 1.0f - n2;
                }
                final Iterator<EdgeRing> iterator4 = obstacle.inner.iterator();
                while (iterator4.hasNext()) {
                    for (final Edge edge3 : iterator4.next()) {
                        LineDrawer.addLine(edge3.node1.x, edge3.node1.y, (float)this.cluster.z, edge3.node2.x, edge3.node2.y, (float)this.cluster.z, n2, 0.5f, 0.5f, null, true);
                        n2 = 1.0f - n2;
                    }
                }
                if (DebugOptions.instance.PolymapRenderCrawling.getValue()) {
                    for (final Node node : obstacle.crawlNodes) {
                        LineDrawer.addLine(node.x - 0.05f, node.y - 0.05f, (float)this.cluster.z, node.x + 0.05f, node.y + 0.05f, (float)this.cluster.z, 0.5f, 1.0f, 0.5f, null, false);
                        final Iterator<Connection> iterator7 = node.visible.iterator();
                        while (iterator7.hasNext()) {
                            final Node otherNode = iterator7.next().otherNode(node);
                            if (otherNode.hasFlag(1)) {
                                LineDrawer.addLine(node.x, node.y, (float)this.cluster.z, otherNode.x, otherNode.y, (float)this.cluster.z, 0.5f, 1.0f, 0.5f, null, true);
                            }
                        }
                    }
                }
            }
            for (final Node node2 : this.perimeterNodes) {
                if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
                    final Iterator<Connection> iterator9 = node2.visible.iterator();
                    while (iterator9.hasNext()) {
                        final Node otherNode2 = iterator9.next().otherNode(node2);
                        LineDrawer.addLine(node2.x, node2.y, (float)this.cluster.z, otherNode2.x, otherNode2.y, (float)this.cluster.z, 0.0f, 0.25f, 0.0f, null, true);
                    }
                }
                if (DebugOptions.instance.PolymapRenderNodes.getValue()) {
                    final float n3 = 1.0f;
                    float n4 = 0.5f;
                    final float n5 = 0.0f;
                    if (node2.ignore) {
                        n4 = 1.0f;
                    }
                    LineDrawer.addLine(node2.x - 0.05f, node2.y - 0.05f, (float)this.cluster.z, node2.x + 0.05f, node2.y + 0.05f, (float)this.cluster.z, n3, n4, n5, null, false);
                }
            }
            for (final Node node3 : this.nodes) {
                if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
                    final Iterator<Connection> iterator11 = node3.visible.iterator();
                    while (iterator11.hasNext()) {
                        final Node otherNode3 = iterator11.next().otherNode(node3);
                        if (this.nodes.contains(otherNode3)) {
                            LineDrawer.addLine(node3.x, node3.y, (float)this.cluster.z, otherNode3.x, otherNode3.y, (float)this.cluster.z, 0.0f, 1.0f, 0.0f, null, true);
                        }
                    }
                }
                if (DebugOptions.instance.PolymapRenderNodes.getValue() || node3.ignore) {
                    LineDrawer.addLine(node3.x - 0.05f, node3.y - 0.05f, (float)this.cluster.z, node3.x + 0.05f, node3.y + 0.05f, (float)this.cluster.z, 1.0f, 1.0f, 0.0f, null, false);
                }
            }
            for (final Node node4 : this.intersectNodes) {
                LineDrawer.addLine(node4.x - 0.1f, node4.y - 0.1f, (float)this.cluster.z, node4.x + 0.1f, node4.y + 0.1f, (float)this.cluster.z, 1.0f, 0.0f, 0.0f, null, false);
            }
        }
        
        static {
            comparator = new CompareIntersection();
            clusterOutlineGrid = new ClusterOutlineGrid();
            pool = new ArrayDeque<VisibilityGraph>();
        }
        
        static final class CompareIntersection implements Comparator<Intersection>
        {
            Edge edge;
            
            @Override
            public int compare(final Intersection intersection, final Intersection intersection2) {
                final float n = (this.edge == intersection.edge1) ? intersection.dist1 : intersection.dist2;
                final float n2 = (this.edge == intersection2.edge1) ? intersection2.dist1 : intersection2.dist2;
                if (n < n2) {
                    return -1;
                }
                if (n > n2) {
                    return 1;
                }
                return 0;
            }
        }
    }
    
    static final class PathNode
    {
        float x;
        float y;
        float z;
        int flags;
        
        PathNode init(final float x, final float y, final float z, final int flags) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.flags = flags;
            return this;
        }
        
        PathNode init(final PathNode pathNode) {
            this.x = pathNode.x;
            this.y = pathNode.y;
            this.z = pathNode.z;
            this.flags = pathNode.flags;
            return this;
        }
        
        boolean hasFlag(final int n) {
            return (this.flags & n) != 0x0;
        }
    }
    
    public static final class Path
    {
        final ArrayList<PathNode> nodes;
        final ArrayDeque<PathNode> nodePool;
        
        public Path() {
            this.nodes = new ArrayList<PathNode>();
            this.nodePool = new ArrayDeque<PathNode>();
        }
        
        void clear() {
            for (int i = 0; i < this.nodes.size(); ++i) {
                this.nodePool.push(this.nodes.get(i));
            }
            this.nodes.clear();
        }
        
        boolean isEmpty() {
            return this.nodes.isEmpty();
        }
        
        PathNode addNode(final float n, final float n2, final float n3) {
            return this.addNode(n, n2, n3, 0);
        }
        
        PathNode addNode(final float n, final float n2, final float n3, final int n4) {
            final PathNode e = this.nodePool.isEmpty() ? new PathNode() : this.nodePool.pop();
            e.init(n, n2, n3, n4);
            this.nodes.add(e);
            return e;
        }
        
        PathNode addNode(final SearchNode searchNode) {
            return this.addNode(searchNode.getX(), searchNode.getY(), searchNode.getZ(), (searchNode.vgNode == null) ? 0 : searchNode.vgNode.flags);
        }
        
        PathNode getNode(final int index) {
            return this.nodes.get(index);
        }
        
        PathNode getLastNode() {
            return this.nodes.get(this.nodes.size() - 1);
        }
        
        void copyFrom(final Path path) {
            assert this != path;
            this.clear();
            for (int i = 0; i < path.nodes.size(); ++i) {
                final PathNode pathNode = path.nodes.get(i);
                this.addNode(pathNode.x, pathNode.y, pathNode.z, pathNode.flags);
            }
        }
        
        float length() {
            float n = 0.0f;
            for (int i = 0; i < this.nodes.size() - 1; ++i) {
                final PathNode pathNode = this.nodes.get(i);
                final PathNode pathNode2 = this.nodes.get(i + 1);
                n += IsoUtils.DistanceTo(pathNode.x, pathNode.y, pathNode.z, pathNode2.x, pathNode2.y, pathNode2.z);
            }
            return n;
        }
        
        public boolean crossesSquare(final int n, final int n2, final int n3) {
            for (int i = 0; i < this.nodes.size() - 1; ++i) {
                final PathNode pathNode = this.nodes.get(i);
                final PathNode pathNode2 = this.nodes.get(i + 1);
                if ((int)pathNode.z == n3 || (int)pathNode2.z == n3) {
                    if (Line2D.linesIntersect(pathNode.x, pathNode.y, pathNode2.x, pathNode2.y, n, n2, n + 1, n2)) {
                        return true;
                    }
                    if (Line2D.linesIntersect(pathNode.x, pathNode.y, pathNode2.x, pathNode2.y, n + 1, n2, n + 1, n2 + 1)) {
                        return true;
                    }
                    if (Line2D.linesIntersect(pathNode.x, pathNode.y, pathNode2.x, pathNode2.y, n + 1, n2 + 1, n, n2 + 1)) {
                        return true;
                    }
                    if (Line2D.linesIntersect(pathNode.x, pathNode.y, pathNode2.x, pathNode2.y, n, n2 + 1, n, n2)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    private static final class AdjustStartEndNodeData
    {
        Obstacle obstacle;
        Node node;
        Edge newEdge;
        boolean isNodeNew;
        VisibilityGraph graph;
    }
    
    private static final class TestRequest implements IPathfinder
    {
        final Path path;
        boolean done;
        
        private TestRequest() {
            this.path = new Path();
        }
        
        @Override
        public void Succeeded(final Path path, final Mover mover) {
            this.path.copyFrom(path);
            this.done = true;
        }
        
        @Override
        public void Failed(final Mover mover) {
            this.path.clear();
            this.done = true;
        }
    }
    
    private static final class SearchNode extends ASearchNode
    {
        VGAStar astar;
        Node vgNode;
        Square square;
        int tx;
        int ty;
        SearchNode parent;
        static int nextID;
        Integer ID;
        private static final double SQRT2;
        static final ArrayDeque<SearchNode> pool;
        
        SearchNode() {
            this.ID = SearchNode.nextID++;
        }
        
        SearchNode init(final VGAStar astar, final Node vgNode) {
            this.setG(0.0);
            this.astar = astar;
            this.vgNode = vgNode;
            this.square = null;
            final int n = -1;
            this.ty = n;
            this.tx = n;
            this.parent = null;
            return this;
        }
        
        SearchNode init(final VGAStar astar, final Square square) {
            this.setG(0.0);
            this.astar = astar;
            this.vgNode = null;
            this.square = square;
            final int n = -1;
            this.ty = n;
            this.tx = n;
            this.parent = null;
            return this;
        }
        
        SearchNode init(final VGAStar astar, final int tx, final int ty) {
            this.setG(0.0);
            this.astar = astar;
            this.vgNode = null;
            this.square = null;
            this.tx = tx;
            this.ty = ty;
            this.parent = null;
            return this;
        }
        
        public double h() {
            return this.dist(this.astar.goalNode.searchNode);
        }
        
        public double c(final ISearchNode searchNode) {
            final SearchNode searchNode2 = (SearchNode)searchNode;
            double n = 0.0;
            final boolean b = this.astar.mover instanceof IsoZombie && ((IsoZombie)this.astar.mover).bCrawling;
            if ((this.astar.mover instanceof IsoZombie || ((IsoZombie)this.astar.mover).bCrawling) && this.square != null && searchNode2.square != null) {
                if (this.square.x == searchNode2.square.x - 1 && this.square.y == searchNode2.square.y) {
                    if (searchNode2.square.has(2048)) {
                        n = ((!b && searchNode2.square.has(1048576)) ? 20.0 : 200.0);
                    }
                }
                else if (this.square.x == searchNode2.square.x + 1 && this.square.y == searchNode2.square.y) {
                    if (this.square.has(2048)) {
                        n = ((!b && this.square.has(1048576)) ? 20.0 : 200.0);
                    }
                }
                else if (this.square.y == searchNode2.square.y - 1 && this.square.x == searchNode2.square.x) {
                    if (searchNode2.square.has(4096)) {
                        n = ((!b && searchNode2.square.has(2097152)) ? 20.0 : 200.0);
                    }
                }
                else if (this.square.y == searchNode2.square.y + 1 && this.square.x == searchNode2.square.x && this.square.has(4096)) {
                    n = ((!b && this.square.has(2097152)) ? 20.0 : 200.0);
                }
            }
            if (searchNode2.square != null && searchNode2.square.has(131072)) {
                n = 20.0;
            }
            if (this.vgNode != null && searchNode2.vgNode != null) {
                int i = 0;
                while (i < this.vgNode.visible.size()) {
                    final Connection connection = this.vgNode.visible.get(i);
                    if (connection.otherNode(this.vgNode) == searchNode2.vgNode) {
                        if (this.vgNode.square != null && this.vgNode.square.has(131072)) {
                            break;
                        }
                        if (connection.has(2)) {
                            n = 20.0;
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            final Square square = (this.square == null) ? PolygonalMap2.instance.getSquare((int)this.vgNode.x, (int)this.vgNode.y, 0) : this.square;
            final Square square2 = (searchNode2.square == null) ? PolygonalMap2.instance.getSquare((int)searchNode2.vgNode.x, (int)searchNode2.vgNode.y, 0) : searchNode2.square;
            if (square != null && square2 != null) {
                if (square.x == square2.x - 1 && square.y == square2.y) {
                    if (square2.has(32768)) {
                        n = 20.0;
                    }
                }
                else if (square.x == square2.x + 1 && square.y == square2.y) {
                    if (square.has(32768)) {
                        n = 20.0;
                    }
                }
                else if (square.y == square2.y - 1 && square.x == square2.x) {
                    if (square2.has(65536)) {
                        n = 20.0;
                    }
                }
                else if (square.y == square2.y + 1 && square.x == square2.x && square.has(65536)) {
                    n = 20.0;
                }
                if (b) {
                    if (square.x == square2.x - 1 && square.y == square2.y) {
                        if (square2.has(2) && square2.has(8192)) {
                            n = 20.0;
                        }
                    }
                    else if (square.x == square2.x + 1 && square.y == square2.y) {
                        if (square.has(2) && square.has(8192)) {
                            n = 20.0;
                        }
                    }
                    else if (square.y == square2.y - 1 && square.x == square2.x) {
                        if (square2.has(4) && square2.has(16384)) {
                            n = 20.0;
                        }
                    }
                    else if (square.y == square2.y + 1 && square.x == square2.x && square.has(4) && square.has(16384)) {
                        n = 20.0;
                    }
                }
            }
            final boolean b2 = this.vgNode != null && this.vgNode.hasFlag(2);
            final boolean b3 = searchNode2.vgNode != null && searchNode2.vgNode.hasFlag(2);
            if (!b2 && b3 && !this.astar.bIgnoreCrawlCost) {
                n += 10.0;
            }
            if (searchNode2.square != null) {
                n += searchNode2.square.cost;
            }
            return this.dist(searchNode2) + n;
        }
        
        public void getSuccessors(final ArrayList<ISearchNode> list) {
            if (this.vgNode != null) {
                if (this.vgNode.graphs != null) {
                    for (int i = 0; i < this.vgNode.graphs.size(); ++i) {
                        final VisibilityGraph visibilityGraph = this.vgNode.graphs.get(i);
                        if (!visibilityGraph.created) {
                            visibilityGraph.create();
                        }
                    }
                }
                for (int j = 0; j < this.vgNode.visible.size(); ++j) {
                    final Connection connection = this.vgNode.visible.get(j);
                    final Node otherNode = connection.otherNode(this.vgNode);
                    final SearchNode searchNode = this.astar.getSearchNode(otherNode);
                    if (this.vgNode.square == null || searchNode.square == null || !this.astar.isKnownBlocked(this.vgNode.square, searchNode.square)) {
                        if (this.astar.bCanCrawl || !otherNode.hasFlag(2)) {
                            if (this.astar.bCanThump || !connection.has(2)) {
                                list.add((ISearchNode)searchNode);
                            }
                        }
                    }
                }
                if (!this.vgNode.hasFlag(8)) {
                    return;
                }
            }
            if (this.square != null) {
                for (int k = -1; k <= 1; ++k) {
                    for (int l = -1; l <= 1; ++l) {
                        if (l != 0 || k != 0) {
                            final Square square = PolygonalMap2.instance.getSquare(this.square.x + l, this.square.y + k, this.square.z);
                            if (square != null && !this.astar.isSquareInCluster(square) && !this.astar.canNotMoveBetween(this.square, square, false)) {
                                final SearchNode searchNode2 = this.astar.getSearchNode(square);
                                if (!list.contains(searchNode2)) {
                                    list.add((ISearchNode)searchNode2);
                                }
                            }
                        }
                    }
                }
                if (this.square.z > 0) {
                    final Square square2 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y + 1, this.square.z - 1);
                    if (square2 != null && square2.has(64) && !this.astar.isSquareInCluster(square2)) {
                        final SearchNode searchNode3 = this.astar.getSearchNode(square2);
                        if (!list.contains(searchNode3)) {
                            list.add((ISearchNode)searchNode3);
                        }
                    }
                    final Square square3 = PolygonalMap2.instance.getSquare(this.square.x + 1, this.square.y, this.square.z - 1);
                    if (square3 != null && square3.has(8) && !this.astar.isSquareInCluster(square3)) {
                        final SearchNode searchNode4 = this.astar.getSearchNode(square3);
                        if (!list.contains(searchNode4)) {
                            list.add((ISearchNode)searchNode4);
                        }
                    }
                }
                if (this.square.z < 8 && this.square.has(64)) {
                    final Square square4 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y - 1, this.square.z + 1);
                    if (square4 != null && !this.astar.isSquareInCluster(square4)) {
                        final SearchNode searchNode5 = this.astar.getSearchNode(square4);
                        if (!list.contains(searchNode5)) {
                            list.add((ISearchNode)searchNode5);
                        }
                    }
                }
                if (this.square.z < 8 && this.square.has(8)) {
                    final Square square5 = PolygonalMap2.instance.getSquare(this.square.x - 1, this.square.y, this.square.z + 1);
                    if (square5 != null && !this.astar.isSquareInCluster(square5)) {
                        final SearchNode searchNode6 = this.astar.getSearchNode(square5);
                        if (!list.contains(searchNode6)) {
                            list.add((ISearchNode)searchNode6);
                        }
                    }
                }
            }
        }
        
        public ISearchNode getParent() {
            return (ISearchNode)this.parent;
        }
        
        public void setParent(final ISearchNode searchNode) {
            this.parent = (SearchNode)searchNode;
        }
        
        public Integer keyCode() {
            return this.ID;
        }
        
        public float getX() {
            if (this.square != null) {
                return this.square.x + 0.5f;
            }
            if (this.vgNode != null) {
                return this.vgNode.x;
            }
            return (float)this.tx;
        }
        
        public float getY() {
            if (this.square != null) {
                return this.square.y + 0.5f;
            }
            if (this.vgNode != null) {
                return this.vgNode.y;
            }
            return (float)this.ty;
        }
        
        public float getZ() {
            if (this.square != null) {
                return (float)this.square.z;
            }
            if (this.vgNode != null) {
                return (float)this.vgNode.z;
            }
            return 0.0f;
        }
        
        public double dist(final SearchNode searchNode) {
            if (this.square == null || searchNode.square == null || Math.abs(this.square.x - searchNode.square.x) > 1 || Math.abs(this.square.y - searchNode.square.y) > 1) {
                return Math.sqrt(Math.pow(this.getX() - searchNode.getX(), 2.0) + Math.pow(this.getY() - searchNode.getY(), 2.0));
            }
            if (this.square.x != searchNode.square.x && this.square.y != searchNode.square.y) {
                return SearchNode.SQRT2;
            }
            return 1.0;
        }
        
        float getApparentZ() {
            if (this.square == null) {
                return (float)this.vgNode.z;
            }
            if (this.square.has(8) || this.square.has(64)) {
                return this.square.z + 0.75f;
            }
            if (this.square.has(16) || this.square.has(128)) {
                return this.square.z + 0.5f;
            }
            if (this.square.has(32) || this.square.has(256)) {
                return this.square.z + 0.25f;
            }
            return (float)this.square.z;
        }
        
        static SearchNode alloc() {
            return SearchNode.pool.isEmpty() ? new SearchNode() : SearchNode.pool.pop();
        }
        
        void release() {
            assert !SearchNode.pool.contains(this);
            SearchNode.pool.push(this);
        }
        
        static {
            SearchNode.nextID = 1;
            SQRT2 = Math.sqrt(2.0);
            pool = new ArrayDeque<SearchNode>();
        }
    }
    
    private static final class GoalNode implements IGoalNode
    {
        SearchNode searchNode;
        
        GoalNode init(final SearchNode searchNode) {
            this.searchNode = searchNode;
            return this;
        }
        
        public boolean inGoal(final ISearchNode searchNode) {
            if (this.searchNode.tx != -1) {
                final SearchNode searchNode2 = (SearchNode)searchNode;
                final int n = (int)searchNode2.getX();
                final int n2 = (int)searchNode2.getY();
                return (n % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(n - 1, n2) == null) || (n % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(n + 1, n2) == null) || (n2 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(n, n2 - 1) == null) || (n2 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(n, n2 + 1) == null);
            }
            return searchNode == this.searchNode;
        }
    }
    
    private static final class VGAStar extends AStar
    {
        ArrayList<VisibilityGraph> graphs;
        final ArrayList<SearchNode> searchNodes;
        final TIntObjectHashMap<SearchNode> nodeMap;
        final GoalNode goalNode;
        final TIntObjectHashMap<SearchNode> squareToNode;
        Mover mover;
        boolean bCanCrawl;
        boolean bIgnoreCrawlCost;
        boolean bCanThump;
        final TIntObjectHashMap<KnownBlockedEdges> knownBlockedEdges;
        final InitProc initProc;
        
        private VGAStar() {
            this.searchNodes = new ArrayList<SearchNode>();
            this.nodeMap = (TIntObjectHashMap<SearchNode>)new TIntObjectHashMap();
            this.goalNode = new GoalNode();
            this.squareToNode = (TIntObjectHashMap<SearchNode>)new TIntObjectHashMap();
            this.knownBlockedEdges = (TIntObjectHashMap<KnownBlockedEdges>)new TIntObjectHashMap();
            this.initProc = new InitProc();
        }
        
        VGAStar init(final ArrayList<VisibilityGraph> graphs, final TIntObjectHashMap<Node> tIntObjectHashMap) {
            this.setMaxSteps(5000);
            this.graphs = graphs;
            this.searchNodes.clear();
            this.nodeMap.clear();
            this.squareToNode.clear();
            this.mover = null;
            tIntObjectHashMap.forEachEntry((TIntObjectProcedure)this.initProc);
            return this;
        }
        
        VisibilityGraph getVisGraphForSquare(final Square square) {
            for (int i = 0; i < this.graphs.size(); ++i) {
                final VisibilityGraph visibilityGraph = this.graphs.get(i);
                if (visibilityGraph.contains(square)) {
                    return visibilityGraph;
                }
            }
            return null;
        }
        
        boolean isSquareInCluster(final Square square) {
            return this.getVisGraphForSquare(square) != null;
        }
        
        SearchNode getSearchNode(final Node node) {
            if (node.square != null) {
                return this.getSearchNode(node.square);
            }
            SearchNode init = (SearchNode)this.nodeMap.get(node.ID);
            if (init == null) {
                init = SearchNode.alloc().init(this, node);
                this.searchNodes.add(init);
                this.nodeMap.put(node.ID, (Object)init);
            }
            return init;
        }
        
        SearchNode getSearchNode(final Square square) {
            SearchNode init = (SearchNode)this.squareToNode.get((int)square.ID);
            if (init == null) {
                init = SearchNode.alloc().init(this, square);
                this.searchNodes.add(init);
                this.squareToNode.put((int)square.ID, (Object)init);
            }
            return init;
        }
        
        SearchNode getSearchNode(final int n, final int n2) {
            final SearchNode init = SearchNode.alloc().init(this, n, n2);
            this.searchNodes.add(init);
            return init;
        }
        
        ArrayList<ISearchNode> shortestPath(final PathFindRequest pathFindRequest, final SearchNode searchNode, final SearchNode searchNode2) {
            this.mover = pathFindRequest.mover;
            this.bCanCrawl = pathFindRequest.bCanCrawl;
            this.bIgnoreCrawlCost = pathFindRequest.bIgnoreCrawlCost;
            this.bCanThump = pathFindRequest.bCanThump;
            this.goalNode.init(searchNode2);
            return (ArrayList<ISearchNode>)this.shortestPath((ISearchNode)searchNode, (IGoalNode)this.goalNode);
        }
        
        boolean canNotMoveBetween(final Square square, final Square square2, final boolean b) {
            assert Math.abs(square.x - square2.x) <= 1;
            assert Math.abs(square.y - square2.y) <= 1;
            assert square.z == square2.z;
            assert square != square2;
            if (square.x == 10921 && square.y == 10137 && square2.x == square.x - 1 && square2.y == square.y) {}
            final boolean b2 = square2.x < square.x;
            final boolean b3 = square2.x > square.x;
            final boolean b4 = square2.y < square.y;
            final boolean b5 = square2.y > square.y;
            if (square2.isNonThumpableSolid() || (!this.bCanThump && square2.isReallySolid())) {
                return true;
            }
            if (square2.y < square.y && square.has(64)) {
                return true;
            }
            if (square2.x < square.x && square.has(8)) {
                return true;
            }
            if (square2.y > square.y && square2.x == square.x && square2.has(64)) {
                return true;
            }
            if (square2.x > square.x && square2.y == square.y && square2.has(8)) {
                return true;
            }
            if (square2.x != square.x && square2.has(448)) {
                return true;
            }
            if (square2.y != square.y && square2.has(56)) {
                return true;
            }
            if (square2.x != square.x && square.has(448)) {
                return true;
            }
            if (square2.y != square.y && square.has(56)) {
                return true;
            }
            if (!square2.has(512) && !square2.has(504)) {
                return true;
            }
            if (this.isKnownBlocked(square, square2)) {
                return true;
            }
            if (square.x == 11920 && square2.y == 6803 && square2.has(131072)) {}
            final boolean b6 = square.isCanPathN() && (this.bCanThump || !square.isThumpN());
            final boolean b7 = square.isCanPathW() && (this.bCanThump || !square.isThumpW());
            final boolean b8 = b4 && square.isCollideN() && (square.x != square2.x || b || !b6);
            final boolean b9 = b2 && square.isCollideW() && (square.y != square2.y || b || !b7);
            final boolean b10 = square2.isCanPathN() && (this.bCanThump || !square2.isThumpN());
            final boolean b11 = square2.isCanPathW() && (this.bCanThump || !square2.isThumpW());
            final boolean b12 = b5 && square2.has(131076) && (square.x != square2.x || b || !b10);
            final boolean b13 = b3 && square2.has(131074) && (square.y != square2.y || b || !b11);
            if (b8 || b9 || b12 || b13) {
                return true;
            }
            if (square2.x != square.x && square2.y != square.y) {
                final Square square3 = PolygonalMap2.instance.getSquare(square.x, square2.y, square.z);
                final Square square4 = PolygonalMap2.instance.getSquare(square2.x, square.y, square.z);
                assert square3 != square && square3 != square2;
                assert square4 != square && square4 != square2;
                if (square2.x == square.x + 1 && square2.y == square.y + 1 && square3 != null && square4 != null) {
                    if (square3.has(4096) && square4.has(2048)) {
                        return true;
                    }
                    if (square3.isThumpN() && square4.isThumpW()) {
                        return true;
                    }
                }
                if (square2.x == square.x - 1 && square2.y == square.y - 1 && square3 != null && square4 != null) {
                    if (square3.has(2048) && square4.has(4096)) {
                        return true;
                    }
                    if (square3.isThumpW() && square4.isThumpN()) {
                        return true;
                    }
                }
                if (square3 != null && this.canNotMoveBetween(square, square3, true)) {
                    return true;
                }
                if (square4 != null && this.canNotMoveBetween(square, square4, true)) {
                    return true;
                }
                if (square3 != null && this.canNotMoveBetween(square2, square3, true)) {
                    return true;
                }
                if (square4 != null && this.canNotMoveBetween(square2, square4, true)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isKnownBlocked(final Square square, final Square square2) {
            if (square.z != square2.z) {
                return false;
            }
            final KnownBlockedEdges knownBlockedEdges = (KnownBlockedEdges)this.knownBlockedEdges.get((int)square.ID);
            final KnownBlockedEdges knownBlockedEdges2 = (KnownBlockedEdges)this.knownBlockedEdges.get((int)square2.ID);
            return (knownBlockedEdges != null && knownBlockedEdges.isBlocked(square2.x, square2.y)) || (knownBlockedEdges2 != null && knownBlockedEdges2.isBlocked(square.x, square.y));
        }
        
        final class InitProc implements TIntObjectProcedure<Node>
        {
            public boolean execute(final int n, final Node node) {
                final SearchNode init = SearchNode.alloc().init(VGAStar.this, node);
                init.square = node.square;
                VGAStar.this.squareToNode.put(n, (Object)init);
                VGAStar.this.nodeMap.put(node.ID, (Object)init);
                VGAStar.this.searchNodes.add(init);
                return true;
            }
        }
    }
    
    private static final class ChunkUpdateTask implements IChunkTask
    {
        PolygonalMap2 map;
        int wx;
        int wy;
        final int[][][] data;
        final short[][][] cost;
        static final ArrayDeque<ChunkUpdateTask> pool;
        
        private ChunkUpdateTask() {
            this.data = new int[10][10][8];
            this.cost = new short[10][10][8];
        }
        
        ChunkUpdateTask init(final PolygonalMap2 map, final IsoChunk isoChunk) {
            this.map = map;
            this.wx = isoChunk.wx;
            this.wy = isoChunk.wy;
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 10; ++j) {
                    for (int k = 0; k < 10; ++k) {
                        final IsoGridSquare gridSquare = isoChunk.getGridSquare(k, j, i);
                        if (gridSquare == null) {
                            this.data[k][j][i] = 0;
                            this.cost[k][j][i] = 0;
                        }
                        else {
                            this.data[k][j][i] = SquareUpdateTask.getBits(gridSquare);
                            this.cost[k][j][i] = SquareUpdateTask.getCost(gridSquare);
                        }
                    }
                }
            }
            return this;
        }
        
        @Override
        public void execute() {
            this.map.allocChunkIfNeeded(this.wx, this.wy).setData(this);
        }
        
        static ChunkUpdateTask alloc() {
            synchronized (ChunkUpdateTask.pool) {
                return ChunkUpdateTask.pool.isEmpty() ? new ChunkUpdateTask() : ChunkUpdateTask.pool.pop();
            }
        }
        
        @Override
        public void release() {
            synchronized (ChunkUpdateTask.pool) {
                assert !ChunkUpdateTask.pool.contains(this);
                ChunkUpdateTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<ChunkUpdateTask>();
        }
    }
    
    private static final class ChunkRemoveTask implements IChunkTask
    {
        PolygonalMap2 map;
        int wx;
        int wy;
        static final ArrayDeque<ChunkRemoveTask> pool;
        
        ChunkRemoveTask init(final PolygonalMap2 map, final IsoChunk isoChunk) {
            this.map = map;
            this.wx = isoChunk.wx;
            this.wy = isoChunk.wy;
            return this;
        }
        
        @Override
        public void execute() {
            this.map.getCellFromChunkPos(this.wx, this.wy).removeChunk(this.wx, this.wy);
        }
        
        static ChunkRemoveTask alloc() {
            synchronized (ChunkRemoveTask.pool) {
                return ChunkRemoveTask.pool.isEmpty() ? new ChunkRemoveTask() : ChunkRemoveTask.pool.pop();
            }
        }
        
        @Override
        public void release() {
            synchronized (ChunkRemoveTask.pool) {
                assert !ChunkRemoveTask.pool.contains(this);
                ChunkRemoveTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<ChunkRemoveTask>();
        }
    }
    
    private static final class SquareUpdateTask
    {
        PolygonalMap2 map;
        int x;
        int y;
        int z;
        int bits;
        short cost;
        static final ArrayDeque<SquareUpdateTask> pool;
        
        SquareUpdateTask init(final PolygonalMap2 map, final IsoGridSquare isoGridSquare) {
            this.map = map;
            this.x = isoGridSquare.x;
            this.y = isoGridSquare.y;
            this.z = isoGridSquare.z;
            this.bits = getBits(isoGridSquare);
            this.cost = getCost(isoGridSquare);
            return this;
        }
        
        void execute() {
            final Chunk chunkFromChunkPos = this.map.getChunkFromChunkPos(this.x / 10, this.y / 10);
            if (chunkFromChunkPos != null && chunkFromChunkPos.setData(this)) {
                ++ChunkDataZ.EPOCH;
                this.map.rebuild = true;
            }
        }
        
        static int getBits(final IsoGridSquare isoGridSquare) {
            int n = 0;
            if (isoGridSquare.Is(IsoFlagType.solidfloor)) {
                n |= 0x200;
            }
            if (isoGridSquare.isSolid()) {
                n |= 0x1;
            }
            if (isoGridSquare.isSolidTrans()) {
                n |= 0x400;
            }
            if (isoGridSquare.Is(IsoFlagType.collideW)) {
                n |= 0x2;
            }
            if (isoGridSquare.Is(IsoFlagType.collideN)) {
                n |= 0x4;
            }
            if (isoGridSquare.Has(IsoObjectType.stairsTW)) {
                n |= 0x8;
            }
            if (isoGridSquare.Has(IsoObjectType.stairsMW)) {
                n |= 0x10;
            }
            if (isoGridSquare.Has(IsoObjectType.stairsBW)) {
                n |= 0x20;
            }
            if (isoGridSquare.Has(IsoObjectType.stairsTN)) {
                n |= 0x40;
            }
            if (isoGridSquare.Has(IsoObjectType.stairsMN)) {
                n |= 0x80;
            }
            if (isoGridSquare.Has(IsoObjectType.stairsBN)) {
                n |= 0x100;
            }
            if (isoGridSquare.Is(IsoFlagType.windowW) || isoGridSquare.Is(IsoFlagType.WindowW)) {
                n |= 0x802;
                if (isWindowUnblocked(isoGridSquare, false)) {
                    n |= 0x100000;
                }
            }
            if (isoGridSquare.Is(IsoFlagType.windowN) || isoGridSquare.Is(IsoFlagType.WindowN)) {
                n |= 0x1004;
                if (isWindowUnblocked(isoGridSquare, true)) {
                    n |= 0x200000;
                }
            }
            if (isoGridSquare.Is(IsoFlagType.canPathW)) {
                n |= 0x2000;
            }
            if (isoGridSquare.Is(IsoFlagType.canPathN)) {
                n |= 0x4000;
            }
            for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
                final IsoObject isoObject = isoGridSquare.getSpecialObjects().get(i);
                IsoDirections isoDirections = IsoDirections.Max;
                if (isoObject instanceof IsoDoor) {
                    isoDirections = ((IsoDoor)isoObject).getSpriteEdge(false);
                    if (((IsoDoor)isoObject).IsOpen()) {
                        isoDirections = IsoDirections.Max;
                    }
                }
                else if (isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).isDoor()) {
                    isoDirections = ((IsoThumpable)isoObject).getSpriteEdge(false);
                    if (((IsoThumpable)isoObject).IsOpen()) {
                        isoDirections = IsoDirections.Max;
                    }
                }
                if (isoDirections == IsoDirections.W) {
                    n = (n | 0x2000 | 0x2);
                }
                else if (isoDirections == IsoDirections.N) {
                    n = (n | 0x4000 | 0x4);
                }
                else if (isoDirections == IsoDirections.S) {
                    n |= 0x80000;
                }
                else if (isoDirections == IsoDirections.E) {
                    n |= 0x40000;
                }
            }
            if (isoGridSquare.Is(IsoFlagType.DoorWallW)) {
                n = (n | 0x2000 | 0x2);
            }
            if (isoGridSquare.Is(IsoFlagType.DoorWallN)) {
                n = (n | 0x4000 | 0x4);
            }
            if (hasSquareThumpable(isoGridSquare)) {
                n = (n | 0x2000 | 0x4000 | 0x20000);
            }
            if (hasWallThumpableN(isoGridSquare)) {
                n |= 0x14000;
            }
            if (hasWallThumpableW(isoGridSquare)) {
                n |= 0xA000;
            }
            return n;
        }
        
        static boolean isWindowUnblocked(final IsoGridSquare isoGridSquare, final boolean b) {
            for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
                final IsoObject isoObject = isoGridSquare.getSpecialObjects().get(i);
                if (isoObject instanceof IsoThumpable) {
                    final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                    if (isoThumpable.isWindow() && b == isoThumpable.north) {
                        return !isoThumpable.isBarricaded();
                    }
                }
                if (isoObject instanceof IsoWindow) {
                    final IsoWindow isoWindow = (IsoWindow)isoObject;
                    if (b == isoWindow.north) {
                        return !isoWindow.isBarricaded() && !isoWindow.isInvincible() && (isoWindow.IsOpen() || (isoWindow.isDestroyed() && isoWindow.isGlassRemoved()));
                    }
                }
            }
            return IsoWindowFrame.canClimbThrough(isoGridSquare.getWindowFrame(b), null);
        }
        
        static boolean hasSquareThumpable(final IsoGridSquare isoGridSquare) {
            for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
                final IsoThumpable isoThumpable = Type.tryCastTo(isoGridSquare.getSpecialObjects().get(i), IsoThumpable.class);
                if (isoThumpable != null && isoThumpable.isThumpable() && isoThumpable.isBlockAllTheSquare()) {
                    return true;
                }
            }
            for (int j = 0; j < isoGridSquare.getObjects().size(); ++j) {
                if (isoGridSquare.getObjects().get(j).isMovedThumpable()) {
                    return true;
                }
            }
            return false;
        }
        
        static boolean hasWallThumpableN(final IsoGridSquare isoGridSquare) {
            final IsoGridSquare adjacentSquare = isoGridSquare.getAdjacentSquare(IsoDirections.N);
            if (adjacentSquare == null) {
                return false;
            }
            for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
                final IsoThumpable isoThumpable = Type.tryCastTo(isoGridSquare.getSpecialObjects().get(i), IsoThumpable.class);
                if (isoThumpable != null) {
                    if (!isoThumpable.canClimbThrough(null)) {
                        if (!isoThumpable.canClimbOver(null)) {
                            if (isoThumpable.isThumpable() && !isoThumpable.isBlockAllTheSquare() && !isoThumpable.isDoor() && isoThumpable.TestCollide(null, isoGridSquare, adjacentSquare)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        
        static boolean hasWallThumpableW(final IsoGridSquare isoGridSquare) {
            final IsoGridSquare adjacentSquare = isoGridSquare.getAdjacentSquare(IsoDirections.W);
            if (adjacentSquare == null) {
                return false;
            }
            for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
                final IsoThumpable isoThumpable = Type.tryCastTo(isoGridSquare.getSpecialObjects().get(i), IsoThumpable.class);
                if (isoThumpable != null) {
                    if (!isoThumpable.canClimbThrough(null)) {
                        if (!isoThumpable.canClimbOver(null)) {
                            if (isoThumpable.isThumpable() && !isoThumpable.isBlockAllTheSquare() && !isoThumpable.isDoor() && isoThumpable.TestCollide(null, isoGridSquare, adjacentSquare)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        
        static short getCost(final IsoGridSquare isoGridSquare) {
            short n = 0;
            if (isoGridSquare.HasTree() || isoGridSquare.getProperties().Is("Bush")) {
                n += 5;
            }
            return n;
        }
        
        static SquareUpdateTask alloc() {
            synchronized (SquareUpdateTask.pool) {
                return SquareUpdateTask.pool.isEmpty() ? new SquareUpdateTask() : SquareUpdateTask.pool.pop();
            }
        }
        
        public void release() {
            synchronized (SquareUpdateTask.pool) {
                assert !SquareUpdateTask.pool.contains(this);
                SquareUpdateTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<SquareUpdateTask>();
        }
    }
    
    private static final class VehicleAddTask implements IVehicleTask
    {
        PolygonalMap2 map;
        BaseVehicle vehicle;
        final VehiclePoly poly;
        final VehiclePoly polyPlusRadius;
        final TFloatArrayList crawlOffsets;
        float upVectorDot;
        static final ArrayDeque<VehicleAddTask> pool;
        
        private VehicleAddTask() {
            this.poly = new VehiclePoly();
            this.polyPlusRadius = new VehiclePoly();
            this.crawlOffsets = new TFloatArrayList();
        }
        
        @Override
        public void init(final PolygonalMap2 map, final BaseVehicle vehicle) {
            this.map = map;
            this.vehicle = vehicle;
            this.poly.init(vehicle.getPoly());
            this.polyPlusRadius.init(vehicle.getPolyPlusRadius());
            this.crawlOffsets.resetQuick();
            this.crawlOffsets.addAll((TFloatCollection)vehicle.getScript().getCrawlOffsets());
            this.upVectorDot = vehicle.getUpVectorDot();
        }
        
        @Override
        public void execute() {
            final Vehicle alloc = Vehicle.alloc();
            alloc.poly.init(this.poly);
            alloc.polyPlusRadius.init(this.polyPlusRadius);
            alloc.crawlOffsets.resetQuick();
            alloc.crawlOffsets.addAll((TFloatCollection)this.crawlOffsets);
            alloc.upVectorDot = this.upVectorDot;
            this.map.vehicles.add(alloc);
            this.map.vehicleMap.put(this.vehicle, alloc);
            this.vehicle = null;
        }
        
        static VehicleAddTask alloc() {
            synchronized (VehicleAddTask.pool) {
                return VehicleAddTask.pool.isEmpty() ? new VehicleAddTask() : VehicleAddTask.pool.pop();
            }
        }
        
        @Override
        public void release() {
            synchronized (VehicleAddTask.pool) {
                assert !VehicleAddTask.pool.contains(this);
                VehicleAddTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<VehicleAddTask>();
        }
    }
    
    private static final class VehicleUpdateTask implements IVehicleTask
    {
        PolygonalMap2 map;
        BaseVehicle vehicle;
        final VehiclePoly poly;
        final VehiclePoly polyPlusRadius;
        float upVectorDot;
        static final ArrayDeque<VehicleUpdateTask> pool;
        
        private VehicleUpdateTask() {
            this.poly = new VehiclePoly();
            this.polyPlusRadius = new VehiclePoly();
        }
        
        @Override
        public void init(final PolygonalMap2 map, final BaseVehicle vehicle) {
            this.map = map;
            this.vehicle = vehicle;
            this.poly.init(vehicle.getPoly());
            this.polyPlusRadius.init(vehicle.getPolyPlusRadius());
            this.upVectorDot = vehicle.getUpVectorDot();
        }
        
        @Override
        public void execute() {
            final Vehicle vehicle = this.map.vehicleMap.get(this.vehicle);
            vehicle.poly.init(this.poly);
            vehicle.polyPlusRadius.init(this.polyPlusRadius);
            vehicle.upVectorDot = this.upVectorDot;
            this.vehicle = null;
        }
        
        static VehicleUpdateTask alloc() {
            synchronized (VehicleUpdateTask.pool) {
                return VehicleUpdateTask.pool.isEmpty() ? new VehicleUpdateTask() : VehicleUpdateTask.pool.pop();
            }
        }
        
        @Override
        public void release() {
            synchronized (VehicleUpdateTask.pool) {
                assert !VehicleUpdateTask.pool.contains(this);
                VehicleUpdateTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<VehicleUpdateTask>();
        }
    }
    
    private static final class VehicleRemoveTask implements IVehicleTask
    {
        PolygonalMap2 map;
        BaseVehicle vehicle;
        static final ArrayDeque<VehicleRemoveTask> pool;
        
        @Override
        public void init(final PolygonalMap2 map, final BaseVehicle vehicle) {
            this.map = map;
            this.vehicle = vehicle;
        }
        
        @Override
        public void execute() {
            final Vehicle o = this.map.vehicleMap.remove(this.vehicle);
            if (o != null) {
                this.map.vehicles.remove(o);
                o.release();
            }
            this.vehicle = null;
        }
        
        static VehicleRemoveTask alloc() {
            synchronized (VehicleRemoveTask.pool) {
                return VehicleRemoveTask.pool.isEmpty() ? new VehicleRemoveTask() : VehicleRemoveTask.pool.pop();
            }
        }
        
        @Override
        public void release() {
            synchronized (VehicleRemoveTask.pool) {
                assert !VehicleRemoveTask.pool.contains(this);
                VehicleRemoveTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<VehicleRemoveTask>();
        }
    }
    
    private static final class Square
    {
        static int nextID;
        Integer ID;
        int x;
        int y;
        int z;
        int bits;
        short cost;
        static final ArrayDeque<Square> pool;
        
        Square() {
            this.ID = Square.nextID++;
        }
        
        Square init(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
        
        boolean has(final int n) {
            return (this.bits & n) != 0x0;
        }
        
        boolean isReallySolid() {
            return this.has(1) || (this.has(1024) && !this.isAdjacentToWindow());
        }
        
        boolean isNonThumpableSolid() {
            return this.isReallySolid() && !this.has(131072);
        }
        
        boolean isCanPathW() {
            if (this.has(8192)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
            return square != null && (square.has(131072) || square.has(262144));
        }
        
        boolean isCanPathN() {
            if (this.has(16384)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
            return square != null && (square.has(131072) || square.has(524288));
        }
        
        boolean isCollideW() {
            if (this.has(2)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
            return square != null && (square.has(262144) || square.has(448) || square.isReallySolid());
        }
        
        boolean isCollideN() {
            if (this.has(4)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
            return square != null && (square.has(524288) || square.has(56) || square.isReallySolid());
        }
        
        boolean isThumpW() {
            if (this.has(32768)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
            return square != null && square.has(131072);
        }
        
        boolean isThumpN() {
            if (this.has(65536)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
            return square != null && square.has(131072);
        }
        
        boolean isAdjacentToWindow() {
            if (this.has(2048) || this.has(4096)) {
                return true;
            }
            final Square square = PolygonalMap2.instance.getSquare(this.x, this.y + 1, this.z);
            if (square != null && square.has(4096)) {
                return true;
            }
            final Square square2 = PolygonalMap2.instance.getSquare(this.x + 1, this.y, this.z);
            return square2 != null && square2.has(2048);
        }
        
        static Square alloc() {
            return Square.pool.isEmpty() ? new Square() : Square.pool.pop();
        }
        
        void release() {
            assert !Square.pool.contains(this);
            Square.pool.push(this);
        }
        
        static {
            Square.nextID = 1;
            pool = new ArrayDeque<Square>();
        }
    }
    
    private static final class ChunkDataZ
    {
        public Chunk chunk;
        public final ArrayList<Obstacle> obstacles;
        public final ArrayList<Node> nodes;
        public int z;
        static short EPOCH;
        short epoch;
        public static final ObjectPool<ChunkDataZ> pool;
        
        private ChunkDataZ() {
            this.obstacles = new ArrayList<Obstacle>();
            this.nodes = new ArrayList<Node>();
        }
        
        public void init(final Chunk chunk, final int z) {
            this.chunk = chunk;
            this.z = z;
            this.epoch = ChunkDataZ.EPOCH;
            if (PolygonalMap2.instance.clipperThread == null) {
                PolygonalMap2.instance.clipperThread = new Clipper();
            }
            final Clipper clipperThread = PolygonalMap2.instance.clipperThread;
            clipperThread.clear();
            final int n = chunk.wx * 10;
            for (int n2 = chunk.wy * 10, i = n2 - 2; i < n2 + 10 + 2; ++i) {
                for (int j = n - 2; j < n + 10 + 2; ++j) {
                    final Square square = PolygonalMap2.instance.getSquare(j, i, z);
                    if (square == null || !square.has(512)) {
                        clipperThread.addAABB((float)j, (float)i, j + 1.0f, i + 1.0f);
                    }
                    else {
                        if (square.isReallySolid() || square.has(128) || square.has(64) || square.has(16) || square.has(8)) {
                            clipperThread.addAABBBevel(j - 0.3f, i - 0.3f, j + 1.0f + 0.3f, i + 1.0f + 0.3f, 0.19800001f);
                        }
                        if (square.has(2) || square.has(256)) {
                            clipperThread.addAABBBevel(j - 0.3f, i - 0.3f, j + 0.3f, i + 1.0f + 0.3f, 0.19800001f);
                        }
                        if (square.has(4) || square.has(32)) {
                            clipperThread.addAABBBevel(j - 0.3f, i - 0.3f, j + 1.0f + 0.3f, i + 0.3f, 0.19800001f);
                        }
                        if (square.has(256) && PolygonalMap2.instance.getSquare(j + 1, i, z) != null) {
                            clipperThread.addAABBBevel(j + 1 - 0.3f, i - 0.3f, j + 1 + 0.3f, i + 1.0f + 0.3f, 0.19800001f);
                        }
                        if (square.has(32) && PolygonalMap2.instance.getSquare(j, i + 1, z) != null) {
                            clipperThread.addAABBBevel(j - 0.3f, i + 1 - 0.3f, j + 1.0f + 0.3f, i + 1 + 0.3f, 0.19800001f);
                        }
                    }
                }
            }
            final ByteBuffer xyBufferThread = PolygonalMap2.instance.xyBufferThread;
            for (int generatePolygons = clipperThread.generatePolygons(), k = 0; k < generatePolygons; ++k) {
                xyBufferThread.clear();
                clipperThread.getPolygon(k, xyBufferThread);
                final Obstacle init = Obstacle.alloc().init((IsoGridSquare)null);
                this.getEdgesFromBuffer(xyBufferThread, init, true);
                for (short short1 = xyBufferThread.getShort(), n3 = 0; n3 < short1; ++n3) {
                    this.getEdgesFromBuffer(xyBufferThread, init, false);
                }
                init.calcBounds();
                this.obstacles.add(init);
            }
            int n4 = chunk.wx * 10;
            int n5 = chunk.wy * 10;
            int n6 = n4 + 10;
            int n7 = n5 + 10;
            n4 -= 2;
            n5 -= 2;
            n6 += 2;
            n7 += 2;
            final ImmutableRectF alloc = ImmutableRectF.alloc();
            alloc.init((float)n4, (float)n5, (float)(n6 - n4), (float)(n7 - n5));
            final ImmutableRectF alloc2 = ImmutableRectF.alloc();
            for (int l = 0; l < PolygonalMap2.instance.vehicles.size(); ++l) {
                final Vehicle vehicle = PolygonalMap2.instance.vehicles.get(l);
                final VehiclePoly polyPlusRadius = vehicle.polyPlusRadius;
                final float min = Math.min(polyPlusRadius.x1, Math.min(polyPlusRadius.x2, Math.min(polyPlusRadius.x3, polyPlusRadius.x4)));
                final float min2 = Math.min(polyPlusRadius.y1, Math.min(polyPlusRadius.y2, Math.min(polyPlusRadius.y3, polyPlusRadius.y4)));
                alloc2.init(min, min2, Math.max(polyPlusRadius.x1, Math.max(polyPlusRadius.x2, Math.max(polyPlusRadius.x3, polyPlusRadius.x4))) - min, Math.max(polyPlusRadius.y1, Math.max(polyPlusRadius.y2, Math.max(polyPlusRadius.y3, polyPlusRadius.y4))) - min2);
                if (alloc.intersects(alloc2)) {
                    this.addEdgesForVehicle(vehicle);
                }
            }
            alloc.release();
            alloc2.release();
        }
        
        private void getEdgesFromBuffer(final ByteBuffer byteBuffer, final Obstacle obstacle, final boolean b) {
            final short short1 = byteBuffer.getShort();
            if (short1 < 3) {
                byteBuffer.position(byteBuffer.position() + short1 * 4 * 2);
                return;
            }
            EdgeRing e = obstacle.outer;
            if (!b) {
                e = EdgeRing.alloc();
                e.clear();
                obstacle.inner.add(e);
            }
            final int size = this.nodes.size();
            for (short n = 0; n < short1; ++n) {
                final Node init;
                final Node element = init = Node.alloc().init(byteBuffer.getFloat(), byteBuffer.getFloat(), this.z);
                init.flags |= 0x4;
                this.nodes.add(size, element);
            }
            for (int i = size; i < this.nodes.size() - 1; ++i) {
                e.add(Edge.alloc().init(this.nodes.get(i), this.nodes.get(i + 1), obstacle, e));
            }
            e.add(Edge.alloc().init(this.nodes.get(this.nodes.size() - 1), this.nodes.get(size), obstacle, e));
        }
        
        private void addEdgesForVehicle(final Vehicle vehicle) {
            final VehiclePoly polyPlusRadius = vehicle.polyPlusRadius;
            final int n = (int)polyPlusRadius.z;
            final Node init = Node.alloc().init(polyPlusRadius.x1, polyPlusRadius.y1, n);
            final Node init2 = Node.alloc().init(polyPlusRadius.x2, polyPlusRadius.y2, n);
            final Node init3 = Node.alloc().init(polyPlusRadius.x3, polyPlusRadius.y3, n);
            final Node init4 = Node.alloc().init(polyPlusRadius.x4, polyPlusRadius.y4, n);
            final Node node = init;
            node.flags |= 0x4;
            final Node node2 = init2;
            node2.flags |= 0x4;
            final Node node3 = init3;
            node3.flags |= 0x4;
            final Node node4 = init4;
            node4.flags |= 0x4;
            final Obstacle init5 = Obstacle.alloc().init(vehicle);
            this.obstacles.add(init5);
            final Edge init6 = Edge.alloc().init(init, init2, init5, init5.outer);
            final Edge init7 = Edge.alloc().init(init2, init3, init5, init5.outer);
            final Edge init8 = Edge.alloc().init(init3, init4, init5, init5.outer);
            final Edge init9 = Edge.alloc().init(init4, init, init5, init5.outer);
            init5.outer.add(init6);
            init5.outer.add(init7);
            init5.outer.add(init8);
            init5.outer.add(init9);
            init5.calcBounds();
            this.nodes.add(init);
            this.nodes.add(init2);
            this.nodes.add(init3);
            this.nodes.add(init4);
        }
        
        public void clear() {
            Node.releaseAll(this.nodes);
            this.nodes.clear();
            Obstacle.releaseAll(this.obstacles);
            this.obstacles.clear();
        }
        
        static {
            ChunkDataZ.EPOCH = 0;
            pool = new ObjectPool<ChunkDataZ>(ChunkDataZ::new);
        }
    }
    
    private static final class ChunkData
    {
        final ChunkDataZ[] data;
        
        private ChunkData() {
            this.data = new ChunkDataZ[8];
        }
        
        public ChunkDataZ init(final Chunk chunk, final int n) {
            if (this.data[n] == null) {
                (this.data[n] = ChunkDataZ.pool.alloc()).init(chunk, n);
            }
            else if (this.data[n].epoch != ChunkDataZ.EPOCH) {
                this.data[n].clear();
                this.data[n].init(chunk, n);
            }
            return this.data[n];
        }
        
        public void clear() {
            PZArrayUtil.forEach(this.data, chunkDataZ -> {
                if (chunkDataZ != null) {
                    chunkDataZ.clear();
                    ChunkDataZ.pool.release(chunkDataZ);
                }
                return;
            });
            Arrays.fill(this.data, null);
        }
    }
    
    private static final class Chunk
    {
        short wx;
        short wy;
        Square[][][] squares;
        final ChunkData collision;
        static final ArrayDeque<Chunk> pool;
        
        private Chunk() {
            this.squares = new Square[10][10][8];
            this.collision = new ChunkData();
        }
        
        void init(final int n, final int n2) {
            this.wx = (short)n;
            this.wy = (short)n2;
        }
        
        Square getSquare(int n, int n2, final int n3) {
            n -= this.wx * 10;
            n2 -= this.wy * 10;
            if (n < 0 || n >= 10 || n2 < 0 || n2 >= 10 || n3 < 0 || n3 >= 8) {
                return null;
            }
            return this.squares[n][n2][n3];
        }
        
        void setData(final ChunkUpdateTask chunkUpdateTask) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 10; ++j) {
                    for (int k = 0; k < 10; ++k) {
                        Square alloc = this.squares[k][j][i];
                        final int bits = chunkUpdateTask.data[k][j][i];
                        if (bits == 0) {
                            if (alloc != null) {
                                alloc.release();
                                this.squares[k][j][i] = null;
                            }
                        }
                        else {
                            if (alloc == null) {
                                alloc = Square.alloc();
                                this.squares[k][j][i] = alloc;
                            }
                            alloc.init(this.wx * 10 + k, this.wy * 10 + j, i);
                            alloc.bits = bits;
                            alloc.cost = chunkUpdateTask.cost[k][j][i];
                        }
                    }
                }
            }
            ++ChunkDataZ.EPOCH;
        }
        
        boolean setData(final SquareUpdateTask squareUpdateTask) {
            final int n = squareUpdateTask.x - this.wx * 10;
            final int n2 = squareUpdateTask.y - this.wy * 10;
            if (n < 0 || n >= 10) {
                return false;
            }
            if (n2 < 0 || n2 >= 10) {
                return false;
            }
            Square init = this.squares[n][n2][squareUpdateTask.z];
            if (squareUpdateTask.bits == 0) {
                if (init != null) {
                    init.release();
                    this.squares[n][n2][squareUpdateTask.z] = null;
                    return true;
                }
            }
            else {
                if (init == null) {
                    init = Square.alloc().init(squareUpdateTask.x, squareUpdateTask.y, squareUpdateTask.z);
                    this.squares[n][n2][squareUpdateTask.z] = init;
                }
                if (init.bits != squareUpdateTask.bits || init.cost != squareUpdateTask.cost) {
                    init.bits = squareUpdateTask.bits;
                    init.cost = squareUpdateTask.cost;
                    return true;
                }
            }
            return false;
        }
        
        static Chunk alloc() {
            return Chunk.pool.isEmpty() ? new Chunk() : Chunk.pool.pop();
        }
        
        void release() {
            assert !Chunk.pool.contains(this);
            Chunk.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<Chunk>();
        }
    }
    
    private static final class Cell
    {
        PolygonalMap2 map;
        public short cx;
        public short cy;
        public Chunk[][] chunks;
        static final ArrayDeque<Cell> pool;
        
        Cell init(final PolygonalMap2 map, final int n, final int n2) {
            this.map = map;
            this.cx = (short)n;
            this.cy = (short)n2;
            return this;
        }
        
        Chunk getChunkFromChunkPos(int n, int n2) {
            if (this.chunks == null) {
                return null;
            }
            n -= this.cx * 30;
            n2 -= this.cy * 30;
            if (n < 0 || n >= 30 || n2 < 0 || n2 >= 30) {
                return null;
            }
            return this.chunks[n][n2];
        }
        
        Chunk allocChunkIfNeeded(int n, int n2) {
            n -= this.cx * 30;
            n2 -= this.cy * 30;
            if (n < 0 || n >= 30 || n2 < 0 || n2 >= 30) {
                return null;
            }
            if (this.chunks == null) {
                this.chunks = new Chunk[30][30];
            }
            if (this.chunks[n][n2] == null) {
                this.chunks[n][n2] = Chunk.alloc();
            }
            this.chunks[n][n2].init(this.cx * 30 + n, this.cy * 30 + n2);
            return this.chunks[n][n2];
        }
        
        void removeChunk(int n, int n2) {
            if (this.chunks == null) {
                return;
            }
            n -= this.cx * 30;
            n2 -= this.cy * 30;
            if (n < 0 || n >= 30 || n2 < 0 || n2 >= 30) {
                return;
            }
            final Chunk chunk = this.chunks[n][n2];
            if (chunk != null) {
                chunk.release();
                this.chunks[n][n2] = null;
            }
        }
        
        static Cell alloc() {
            return Cell.pool.isEmpty() ? new Cell() : Cell.pool.pop();
        }
        
        void release() {
            assert !Cell.pool.contains(this);
            Cell.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<Cell>();
        }
    }
    
    private static final class VehicleState
    {
        BaseVehicle vehicle;
        float x;
        float y;
        float z;
        final Vector3f forward;
        final VehiclePoly polyPlusRadius;
        static final ArrayDeque<VehicleState> pool;
        
        private VehicleState() {
            this.forward = new Vector3f();
            this.polyPlusRadius = new VehiclePoly();
        }
        
        VehicleState init(final BaseVehicle vehicle) {
            this.vehicle = vehicle;
            this.x = vehicle.x;
            this.y = vehicle.y;
            this.z = vehicle.z;
            vehicle.getForwardVector(this.forward);
            this.polyPlusRadius.init(vehicle.getPolyPlusRadius());
            return this;
        }
        
        boolean check() {
            boolean b = this.x != this.vehicle.x || this.y != this.vehicle.y || (int)this.z != (int)this.vehicle.z;
            if (!b) {
                final BaseVehicle.Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
                final Vector3f forwardVector = this.vehicle.getForwardVector(vector3fObjectPool.alloc());
                b = (this.forward.dot((Vector3fc)forwardVector) < 0.999f);
                if (b) {
                    this.forward.set((Vector3fc)forwardVector);
                }
                vector3fObjectPool.release(forwardVector);
            }
            if (b) {
                this.x = this.vehicle.x;
                this.y = this.vehicle.y;
                this.z = this.vehicle.z;
            }
            return b;
        }
        
        static VehicleState alloc() {
            return VehicleState.pool.isEmpty() ? new VehicleState() : VehicleState.pool.pop();
        }
        
        void release() {
            assert !VehicleState.pool.contains(this);
            VehicleState.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<VehicleState>();
        }
    }
    
    private static final class Sync
    {
        private int fps;
        private long period;
        private long excess;
        private long beforeTime;
        private long overSleepTime;
        
        private Sync() {
            this.fps = 20;
            this.period = 1000000000L / this.fps;
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }
        
        void begin() {
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }
        
        void startFrame() {
            this.excess = 0L;
        }
        
        void endFrame() {
            final long nanoTime = System.nanoTime();
            final long n = this.period - (nanoTime - this.beforeTime) - this.overSleepTime;
            if (n > 0L) {
                try {
                    Thread.sleep(n / 1000000L);
                }
                catch (InterruptedException ex) {}
                this.overSleepTime = System.nanoTime() - nanoTime - n;
            }
            else {
                this.excess -= n;
                this.overSleepTime = 0L;
            }
            this.beforeTime = System.nanoTime();
        }
    }
    
    private final class PMThread extends Thread
    {
        public boolean bStop;
        public final Object notifier;
        
        private PMThread() {
            this.notifier = new Object();
        }
        
        @Override
        public void run() {
            while (!this.bStop) {
                try {
                    this.runInner();
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
        
        private void runInner() {
            MPStatistic.getInstance().PolyPathThread.Start();
            PolygonalMap2.this.sync.startFrame();
            synchronized (PolygonalMap2.this.renderLock) {
                PolygonalMap2.instance.updateThread();
            }
            PolygonalMap2.this.sync.endFrame();
            MPStatistic.getInstance().PolyPathThread.End();
            while (this.shouldWait()) {
                synchronized (this.notifier) {
                    try {
                        this.notifier.wait();
                    }
                    catch (InterruptedException ex) {}
                }
            }
        }
        
        private boolean shouldWait() {
            return !this.bStop && PolygonalMap2.instance.chunkTaskQueue.isEmpty() && PolygonalMap2.instance.squareTaskQueue.isEmpty() && PolygonalMap2.instance.vehicleTaskQueue.isEmpty() && PolygonalMap2.instance.requestTaskQueue.isEmpty() && PolygonalMap2.instance.requests.isEmpty();
        }
        
        void wake() {
            synchronized (this.notifier) {
                this.notifier.notify();
            }
        }
    }
    
    static final class PathFindRequest
    {
        IPathfinder finder;
        Mover mover;
        boolean bCanCrawl;
        boolean bIgnoreCrawlCost;
        boolean bCanThump;
        final ArrayList<KnownBlockedEdges> knownBlockedEdges;
        float startX;
        float startY;
        float startZ;
        float targetX;
        float targetY;
        float targetZ;
        final TFloatArrayList targetXYZ;
        final Path path;
        boolean cancel;
        static final ArrayDeque<PathFindRequest> pool;
        
        PathFindRequest() {
            this.knownBlockedEdges = new ArrayList<KnownBlockedEdges>();
            this.targetXYZ = new TFloatArrayList();
            this.path = new Path();
            this.cancel = false;
        }
        
        PathFindRequest init(final IPathfinder finder, final Mover mover, final float startX, final float startY, final float startZ, final float targetX, final float targetY, final float targetZ) {
            this.finder = finder;
            this.mover = mover;
            this.bCanCrawl = false;
            this.bIgnoreCrawlCost = false;
            this.bCanThump = false;
            final IsoZombie isoZombie = Type.tryCastTo(mover, IsoZombie.class);
            if (isoZombie != null) {
                this.bCanCrawl = (isoZombie.isCrawling() || isoZombie.isCanCrawlUnderVehicle());
                this.bIgnoreCrawlCost = (isoZombie.isCrawling() && !isoZombie.isCanWalk());
                this.bCanThump = true;
            }
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetZ = targetZ;
            this.targetXYZ.resetQuick();
            this.path.clear();
            this.cancel = false;
            final IsoGameCharacter isoGameCharacter = Type.tryCastTo(mover, IsoGameCharacter.class);
            if (isoGameCharacter != null) {
                final ArrayList<KnownBlockedEdges> knownBlockedEdges = isoGameCharacter.getMapKnowledge().getKnownBlockedEdges();
                for (int i = 0; i < knownBlockedEdges.size(); ++i) {
                    this.knownBlockedEdges.add(KnownBlockedEdges.alloc().init(knownBlockedEdges.get(i)));
                }
            }
            return this;
        }
        
        void addTargetXYZ(final float n, final float n2, final float n3) {
            this.targetXYZ.add(n);
            this.targetXYZ.add(n2);
            this.targetXYZ.add(n3);
        }
        
        static PathFindRequest alloc() {
            return PathFindRequest.pool.isEmpty() ? new PathFindRequest() : PathFindRequest.pool.pop();
        }
        
        public void release() {
            KnownBlockedEdges.releaseAll(this.knownBlockedEdges);
            this.knownBlockedEdges.clear();
            assert !PathFindRequest.pool.contains(this);
            PathFindRequest.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<PathFindRequest>();
        }
    }
    
    private static final class PathRequestTask
    {
        PolygonalMap2 map;
        PathFindRequest request;
        static final ArrayDeque<PathRequestTask> pool;
        
        PathRequestTask init(final PolygonalMap2 map, final PathFindRequest request) {
            this.map = map;
            this.request = request;
            return this;
        }
        
        void execute() {
            if (this.request.mover instanceof IsoPlayer) {
                this.map.requests.addFirst(this.request);
            }
            else {
                this.map.requests.add(this.request);
            }
        }
        
        static PathRequestTask alloc() {
            synchronized (PathRequestTask.pool) {
                return PathRequestTask.pool.isEmpty() ? new PathRequestTask() : PathRequestTask.pool.pop();
            }
        }
        
        public void release() {
            synchronized (PathRequestTask.pool) {
                assert !PathRequestTask.pool.contains(this);
                PathRequestTask.pool.push(this);
            }
        }
        
        static {
            pool = new ArrayDeque<PathRequestTask>();
        }
    }
    
    public static final class Point
    {
        public int x;
        public int y;
        
        Point init(final int x, final int y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Point && ((Point)o).x == this.x && ((Point)o).y == this.y;
        }
    }
    
    private static final class PointPool
    {
        final ArrayDeque<Point> pool;
        
        private PointPool() {
            this.pool = new ArrayDeque<Point>();
        }
        
        Point alloc() {
            return this.pool.isEmpty() ? new Point() : this.pool.pop();
        }
        
        void release(final Point e) {
            this.pool.push(e);
        }
    }
    
    public static final class LiangBarsky
    {
        private final double[] p;
        private final double[] q;
        
        public LiangBarsky() {
            this.p = new double[4];
            this.q = new double[4];
        }
        
        public boolean lineRectIntersect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
            return this.lineRectIntersect(n, n2, n3, n4, n5, n6, n7, n8, null);
        }
        
        public boolean lineRectIntersect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final double[] array) {
            this.p[0] = -n3;
            this.p[1] = n3;
            this.p[2] = -n4;
            this.p[3] = n4;
            this.q[0] = n - n5;
            this.q[1] = n7 - n;
            this.q[2] = n2 - n6;
            this.q[3] = n8 - n2;
            double n9 = 0.0;
            double n10 = 1.0;
            for (int i = 0; i < 4; ++i) {
                if (this.p[i] == 0.0) {
                    if (this.q[i] < 0.0) {
                        return false;
                    }
                }
                else {
                    final double n11 = this.q[i] / this.p[i];
                    if (this.p[i] < 0.0 && n9 < n11) {
                        n9 = n11;
                    }
                    else if (this.p[i] > 0.0 && n10 > n11) {
                        n10 = n11;
                    }
                }
            }
            if (n9 >= n10) {
                return false;
            }
            if (array != null) {
                array[0] = n9;
                array[1] = n10;
            }
            return true;
        }
    }
    
    private static final class LineClearCollide
    {
        final Vector2 perp;
        final ArrayList<Point> pts;
        final VehicleRect sweepAABB;
        final VehicleRect vehicleAABB;
        final Vector2[] polyVec;
        final Vector2[] vehicleVec;
        final PointPool pointPool;
        final LiangBarsky LB;
        
        LineClearCollide() {
            this.perp = new Vector2();
            this.pts = new ArrayList<Point>();
            this.sweepAABB = new VehicleRect();
            this.vehicleAABB = new VehicleRect();
            this.polyVec = new Vector2[4];
            this.vehicleVec = new Vector2[4];
            this.pointPool = new PointPool();
            this.LB = new LiangBarsky();
            for (int i = 0; i < 4; ++i) {
                this.polyVec[i] = new Vector2();
                this.vehicleVec[i] = new Vector2();
            }
        }
        
        private float clamp(float n, final float n2, final float n3) {
            if (n < n2) {
                n = n2;
            }
            if (n > n3) {
                n = n3;
            }
            return n;
        }
        
        @Deprecated
        boolean canStandAt(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final float n5, final Vehicle vehicle) {
            if (((int)n != (int)n3 || (int)n2 != (int)n4) && polygonalMap2.isBlockedInAllDirections((int)n3, (int)n4, (int)n5)) {
                return false;
            }
            final int n6 = (int)Math.floor(n3 - 0.3f);
            final int n7 = (int)Math.floor(n4 - 0.3f);
            final int n8 = (int)Math.ceil(n3 + 0.3f);
            for (int n9 = (int)Math.ceil(n4 + 0.3f), i = n7; i < n9; ++i) {
                for (int j = n6; j < n8; ++j) {
                    final Square square = polygonalMap2.getSquare(j, i, (int)n5);
                    final boolean b = n3 >= j && n4 >= i && n3 < j + 1 && n4 < i + 1;
                    boolean b2 = false;
                    if (!b && square != null && square.has(448)) {
                        b2 = (n3 < square.x || n3 >= square.x + 1 || (square.has(64) && n4 < square.y));
                    }
                    else if (!b && square != null && square.has(56)) {
                        b2 = (n4 < square.y || n4 >= square.y + 1 || (square.has(8) && n3 < square.x));
                    }
                    if ((square == null || square.isReallySolid() || b2 || !square.has(512)) && b) {
                        return false;
                    }
                }
            }
            for (int k = 0; k < polygonalMap2.vehicles.size(); ++k) {
                final Vehicle vehicle2 = polygonalMap2.vehicles.get(k);
                if (vehicle2 != vehicle) {
                    if ((int)vehicle2.polyPlusRadius.z == (int)n5) {
                        if (vehicle2.polyPlusRadius.containsPoint(n3, n4)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        
        boolean canStandAtClipper(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final float n5, final Vehicle vehicle, final int n6) {
            if (((int)n != (int)n3 || (int)n2 != (int)n4) && polygonalMap2.isBlockedInAllDirections((int)n3, (int)n4, (int)n5)) {
                return false;
            }
            final Chunk chunkFromSquarePos = polygonalMap2.getChunkFromSquarePos((int)n3, (int)n4);
            if (chunkFromSquarePos == null) {
                return false;
            }
            final ChunkDataZ init = chunkFromSquarePos.collision.init(chunkFromSquarePos, (int)n5);
            for (int i = 0; i < init.obstacles.size(); ++i) {
                final Obstacle obstacle = init.obstacles.get(i);
                if (vehicle == null || obstacle.vehicle != vehicle) {
                    if (obstacle.bounds.containsPoint(n3, n4) && obstacle.isPointInside(n3, n4, n6)) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        float isLeft(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            return (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
        }
        
        boolean isPointInPolygon_WindingNumber(final float n, final float n2, final VehiclePoly vehiclePoly) {
            this.polyVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
            this.polyVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
            this.polyVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
            this.polyVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
            int n3 = 0;
            for (int i = 0; i < 4; ++i) {
                final Vector2 vector2 = this.polyVec[i];
                final Vector2 vector3 = (i == 3) ? this.polyVec[0] : this.polyVec[i + 1];
                if (vector2.y <= n2) {
                    if (vector3.y > n2 && this.isLeft(vector2.x, vector2.y, vector3.x, vector3.y, n, n2) > 0.0f) {
                        ++n3;
                    }
                }
                else if (vector3.y <= n2 && this.isLeft(vector2.x, vector2.y, vector3.x, vector3.y, n, n2) < 0.0f) {
                    --n3;
                }
            }
            return n3 != 0;
        }
        
        @Deprecated
        boolean isNotClearOld(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final int n6) {
            final boolean b = (n6 & 0x4) != 0x0;
            final Square square = polygonalMap2.getSquare((int)n, (int)n2, n5);
            if (square != null && square.has(504)) {
                return true;
            }
            if (!this.canStandAt(polygonalMap2, n, n2, n3, n4, (float)n5, null)) {
                return true;
            }
            final float n7 = n4 - n2;
            final float n8 = -(n3 - n);
            this.perp.set(n7, n8);
            this.perp.normalize();
            final float n9 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n10 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n11 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n12 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-n7, -n8);
            this.perp.normalize();
            final float n13 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n14 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n15 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n16 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            for (int i = 0; i < this.pts.size(); ++i) {
                this.pointPool.release(this.pts.get(i));
            }
            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)n, (int)n2));
            if ((int)n != (int)n3 || (int)n2 != (int)n4) {
                this.pts.add(this.pointPool.alloc().init((int)n3, (int)n4));
            }
            polygonalMap2.supercover(n9, n10, n11, n12, n5, this.pointPool, this.pts);
            polygonalMap2.supercover(n13, n14, n15, n16, n5, this.pointPool, this.pts);
            for (int j = 0; j < this.pts.size(); ++j) {
                final Point point = this.pts.get(j);
                final Square square2 = polygonalMap2.getSquare(point.x, point.y, n5);
                if (b && square2 != null && square2.cost > 0) {
                    return true;
                }
                if (square2 == null || square2.isReallySolid() || square2.has(504) || !square2.has(512)) {
                    float n17 = 0.3f;
                    float n18 = 0.3f;
                    float n19 = 0.3f;
                    float n20 = 0.3f;
                    if (n < point.x && n3 < point.x) {
                        n17 = 0.0f;
                    }
                    else if (n >= point.x + 1 && n3 >= point.x + 1) {
                        n19 = 0.0f;
                    }
                    if (n2 < point.y && n4 < point.y) {
                        n18 = 0.0f;
                    }
                    else if (n2 >= point.y + 1 && n4 >= point.y + 1) {
                        n20 = 0.0f;
                    }
                    if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point.x - n17, point.y - n18, point.x + 1.0f + n19, point.y + 1.0f + n20)) {
                        return true;
                    }
                }
                else {
                    if (square2.isCollideW()) {
                        float n21 = 0.3f;
                        float n22 = 0.3f;
                        float n23 = 0.3f;
                        float n24 = 0.3f;
                        if (n < point.x && n3 < point.x) {
                            n21 = 0.0f;
                        }
                        else if (n >= point.x && n3 >= point.x) {
                            n23 = 0.0f;
                        }
                        if (n2 < point.y && n4 < point.y) {
                            n22 = 0.0f;
                        }
                        else if (n2 >= point.y + 1 && n4 >= point.y + 1) {
                            n24 = 0.0f;
                        }
                        if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point.x - n21, point.y - n22, point.x + n23, point.y + 1.0f + n24)) {
                            return true;
                        }
                    }
                    if (square2.isCollideN()) {
                        float n25 = 0.3f;
                        float n26 = 0.3f;
                        float n27 = 0.3f;
                        float n28 = 0.3f;
                        if (n < point.x && n3 < point.x) {
                            n25 = 0.0f;
                        }
                        else if (n >= point.x + 1 && n3 >= point.x + 1) {
                            n27 = 0.0f;
                        }
                        if (n2 < point.y && n4 < point.y) {
                            n26 = 0.0f;
                        }
                        else if (n2 >= point.y && n4 >= point.y) {
                            n28 = 0.0f;
                        }
                        if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point.x - n25, point.y - n26, point.x + 1.0f + n27, point.y + n28)) {
                            return true;
                        }
                    }
                }
            }
            final float plus_RADIUS = BaseVehicle.PLUS_RADIUS;
            this.perp.set(n7, n8);
            this.perp.normalize();
            final float n29 = n + this.perp.x * plus_RADIUS;
            final float n30 = n2 + this.perp.y * plus_RADIUS;
            final float n31 = n3 + this.perp.x * plus_RADIUS;
            final float n32 = n4 + this.perp.y * plus_RADIUS;
            this.perp.set(-n7, -n8);
            this.perp.normalize();
            final float n33 = n + this.perp.x * plus_RADIUS;
            final float n34 = n2 + this.perp.y * plus_RADIUS;
            final float n35 = n3 + this.perp.x * plus_RADIUS;
            final float n36 = n4 + this.perp.y * plus_RADIUS;
            final float min = Math.min(n29, Math.min(n31, Math.min(n33, n35)));
            final float min2 = Math.min(n30, Math.min(n32, Math.min(n34, n36)));
            this.sweepAABB.init((int)min, (int)min2, (int)Math.ceil(Math.max(n29, Math.max(n31, Math.max(n33, n35)))) - (int)min, (int)Math.ceil(Math.max(n30, Math.max(n32, Math.max(n34, n36)))) - (int)min2, n5);
            this.polyVec[0].set(n29, n30);
            this.polyVec[1].set(n31, n32);
            this.polyVec[2].set(n35, n36);
            this.polyVec[3].set(n33, n34);
            for (int k = 0; k < polygonalMap2.vehicles.size(); ++k) {
                final Vehicle vehicle = polygonalMap2.vehicles.get(k);
                if (vehicle.poly.getAABB(this.vehicleAABB).intersects(this.sweepAABB) && this.polyVehicleIntersect(vehicle.poly)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isNotClearClipper(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final int n6) {
            final boolean b = (n6 & 0x4) != 0x0;
            final Square square = polygonalMap2.getSquare((int)n, (int)n2, n5);
            if (square != null && square.has(504)) {
                return true;
            }
            if (!this.canStandAtClipper(polygonalMap2, n, n2, n3, n4, (float)n5, null, n6)) {
                return true;
            }
            final float n7 = n / 10.0f;
            final float n8 = n2 / 10.0f;
            final float n9 = n3 / 10.0f;
            final float n10 = n4 / 10.0f;
            final double n11 = Math.abs(n9 - n7);
            final double n12 = Math.abs(n10 - n8);
            int n13 = (int)Math.floor(n7);
            int n14 = (int)Math.floor(n8);
            int i = 1;
            int n15;
            double n16;
            if (n11 == 0.0) {
                n15 = 0;
                n16 = Double.POSITIVE_INFINITY;
            }
            else if (n9 > n7) {
                n15 = 1;
                i += (int)Math.floor(n9) - n13;
                n16 = (Math.floor(n7) + 1.0 - n7) * n12;
            }
            else {
                n15 = -1;
                i += n13 - (int)Math.floor(n9);
                n16 = (n7 - Math.floor(n7)) * n12;
            }
            int n17;
            double n18;
            if (n12 == 0.0) {
                n17 = 0;
                n18 = n16 - Double.POSITIVE_INFINITY;
            }
            else if (n10 > n8) {
                n17 = 1;
                i += (int)Math.floor(n10) - n14;
                n18 = n16 - (Math.floor(n8) + 1.0 - n8) * n11;
            }
            else {
                n17 = -1;
                i += n14 - (int)Math.floor(n10);
                n18 = n16 - (n8 - Math.floor(n8)) * n11;
            }
            while (i > 0) {
                final Chunk chunkFromChunkPos = PolygonalMap2.instance.getChunkFromChunkPos(n13, n14);
                if (chunkFromChunkPos != null) {
                    final ArrayList<Obstacle> obstacles = chunkFromChunkPos.collision.init(chunkFromChunkPos, n5).obstacles;
                    for (int j = 0; j < obstacles.size(); ++j) {
                        if (obstacles.get(j).lineSegmentIntersects(n, n2, n3, n4)) {
                            return true;
                        }
                    }
                }
                if (n18 > 0.0) {
                    n14 += n17;
                    n18 -= n11;
                }
                else {
                    n13 += n15;
                    n18 += n12;
                }
                --i;
            }
            return b && this.isNotClearCost(n, n2, n3, n4, n5);
        }
        
        boolean isNotClearCost(final float n, final float n2, final float n3, final float n4, final int n5) {
            final float n6 = n4 - n2;
            final float n7 = -(n3 - n);
            this.perp.set(n6, n7);
            this.perp.normalize();
            final float n8 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n9 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n10 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n11 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-n6, -n7);
            this.perp.normalize();
            final float n12 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n13 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n14 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n15 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            for (int i = 0; i < this.pts.size(); ++i) {
                this.pointPool.release(this.pts.get(i));
            }
            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)n, (int)n2));
            if ((int)n != (int)n3 || (int)n2 != (int)n4) {
                this.pts.add(this.pointPool.alloc().init((int)n3, (int)n4));
            }
            PolygonalMap2.instance.supercover(n8, n9, n10, n11, n5, this.pointPool, this.pts);
            PolygonalMap2.instance.supercover(n12, n13, n14, n15, n5, this.pointPool, this.pts);
            for (int j = 0; j < this.pts.size(); ++j) {
                final Point point = this.pts.get(j);
                final Square square = PolygonalMap2.instance.getSquare(point.x, point.y, n5);
                if (square != null && square.cost > 0) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isNotClear(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final int n6) {
            return this.isNotClearOld(polygonalMap2, n, n2, n3, n4, n5, n6);
        }
        
        boolean polyVehicleIntersect(final VehiclePoly vehiclePoly) {
            this.vehicleVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
            this.vehicleVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
            this.vehicleVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
            this.vehicleVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
            boolean b = false;
            for (int i = 0; i < 4; ++i) {
                final Vector2 vector2 = this.polyVec[i];
                final Vector2 vector3 = (i == 3) ? this.polyVec[0] : this.polyVec[i + 1];
                for (int j = 0; j < 4; ++j) {
                    final Vector2 vector4 = this.vehicleVec[j];
                    final Vector2 vector5 = (j == 3) ? this.vehicleVec[0] : this.vehicleVec[j + 1];
                    if (Line2D.linesIntersect(vector2.x, vector2.y, vector3.x, vector3.y, vector4.x, vector4.y, vector5.x, vector5.y)) {
                        b = true;
                    }
                }
            }
            return b;
        }
    }
    
    private static final class LineClearCollideMain
    {
        final Vector2 perp;
        final ArrayList<Point> pts;
        final VehicleRect sweepAABB;
        final VehicleRect vehicleAABB;
        final VehiclePoly vehiclePoly;
        final Vector2[] polyVec;
        final Vector2[] vehicleVec;
        final PointPool pointPool;
        final LiangBarsky LB;
        
        LineClearCollideMain() {
            this.perp = new Vector2();
            this.pts = new ArrayList<Point>();
            this.sweepAABB = new VehicleRect();
            this.vehicleAABB = new VehicleRect();
            this.vehiclePoly = new VehiclePoly();
            this.polyVec = new Vector2[4];
            this.vehicleVec = new Vector2[4];
            this.pointPool = new PointPool();
            this.LB = new LiangBarsky();
            for (int i = 0; i < 4; ++i) {
                this.polyVec[i] = new Vector2();
                this.vehicleVec[i] = new Vector2();
            }
        }
        
        private float clamp(float n, final float n2, final float n3) {
            if (n < n2) {
                n = n2;
            }
            if (n > n3) {
                n = n3;
            }
            return n;
        }
        
        @Deprecated
        boolean canStandAtOld(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final BaseVehicle baseVehicle, final int n4) {
            final boolean b = (n4 & 0x1) != 0x0;
            final boolean b2 = (n4 & 0x2) != 0x0;
            final int n5 = (int)Math.floor(n - 0.3f);
            final int n6 = (int)Math.floor(n2 - 0.3f);
            final int n7 = (int)Math.ceil(n + 0.3f);
            for (int n8 = (int)Math.ceil(n2 + 0.3f), i = n6; i < n8; ++i) {
                for (int j = n5; j < n7; ++j) {
                    final boolean b3 = n >= j && n2 >= i && n < j + 1 && n2 < i + 1;
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, i, (int)n3);
                    boolean b4 = false;
                    if (!b3 && gridSquare != null && gridSquare.HasStairsNorth()) {
                        b4 = (n < gridSquare.x || n >= gridSquare.x + 1 || (gridSquare.Has(IsoObjectType.stairsTN) && n2 < gridSquare.y));
                    }
                    else if (!b3 && gridSquare != null && gridSquare.HasStairsWest()) {
                        b4 = (n2 < gridSquare.y || n2 >= gridSquare.y + 1 || (gridSquare.Has(IsoObjectType.stairsTW) && n < gridSquare.x));
                    }
                    Label_0364: {
                        if (gridSquare != null && !gridSquare.isSolid() && (!gridSquare.isSolidTrans() || gridSquare.isAdjacentToWindow()) && !b4) {
                            if (gridSquare.SolidFloorCached) {
                                if (!gridSquare.SolidFloor) {
                                    break Label_0364;
                                }
                            }
                            else if (!gridSquare.TreatAsSolidFloor()) {
                                break Label_0364;
                            }
                            if (b2) {
                                continue;
                            }
                            if (gridSquare.Is(IsoFlagType.collideW) || (!b && gridSquare.hasBlockedDoor(false))) {
                                final float n9 = (float)j;
                                final float clamp = this.clamp(n2, (float)i, (float)(i + 1));
                                final float n10 = n - n9;
                                final float n11 = n2 - clamp;
                                if (n10 * n10 + n11 * n11 < 0.09f) {
                                    return false;
                                }
                            }
                            if (!gridSquare.Is(IsoFlagType.collideN) && (b || !gridSquare.hasBlockedDoor(true))) {
                                continue;
                            }
                            final float clamp2 = this.clamp(n, (float)j, (float)(j + 1));
                            final float n12 = (float)i;
                            final float n13 = n - clamp2;
                            final float n14 = n2 - n12;
                            if (n13 * n13 + n14 * n14 < 0.09f) {
                                return false;
                            }
                            continue;
                        }
                    }
                    if (b2) {
                        if (b3) {
                            return false;
                        }
                    }
                    else {
                        final float clamp3 = this.clamp(n, (float)j, (float)(j + 1));
                        final float clamp4 = this.clamp(n2, (float)i, (float)(i + 1));
                        final float n15 = n - clamp3;
                        final float n16 = n2 - clamp4;
                        if (n15 * n15 + n16 * n16 < 0.09f) {
                            return false;
                        }
                    }
                }
            }
            final int n17 = ((int)n - 4) / 10 - 1;
            final int n18 = ((int)n2 - 4) / 10 - 1;
            final int n19 = (int)Math.ceil((n + 4.0f) / 10.0f) + 1;
            for (int n20 = (int)Math.ceil((n2 + 4.0f) / 10.0f) + 1, k = n18; k < n20; ++k) {
                for (int l = n17; l < n19; ++l) {
                    final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(l, k) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(l * 10, k * 10, 0);
                    if (isoChunk != null) {
                        for (int index = 0; index < isoChunk.vehicles.size(); ++index) {
                            final BaseVehicle baseVehicle2 = isoChunk.vehicles.get(index);
                            if (baseVehicle2 != baseVehicle) {
                                if (baseVehicle2.addedToWorld) {
                                    if ((int)baseVehicle2.z == (int)n3) {
                                        if (baseVehicle2.getPolyPlusRadius().containsPoint(n, n2)) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        
        boolean canStandAtClipper(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final BaseVehicle baseVehicle, final int n4) {
            return PolygonalMap2.instance.collideWithObstaclesPoly.canStandAt(n, n2, n3, baseVehicle, n4);
        }
        
        public void drawCircle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
            LineDrawer.DrawIsoCircle(n, n2, n3, n4, 16, n5, n6, n7, n8);
        }
        
        boolean isNotClearOld(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final BaseVehicle baseVehicle, final int n6) {
            final boolean b = (n6 & 0x1) != 0x0;
            final boolean b2 = (n6 & 0x2) != 0x0;
            final boolean b3 = (n6 & 0x4) != 0x0;
            final boolean b4 = (n6 & 0x8) != 0x0;
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)n, (int)n2, n5);
            if (gridSquare != null && gridSquare.HasStairs()) {
                return !gridSquare.isSameStaircase((int)n3, (int)n4, n5);
            }
            if (!this.canStandAtOld(polygonalMap2, n3, n4, (float)n5, baseVehicle, n6)) {
                if (b4) {
                    this.drawCircle(n3, n4, (float)n5, 0.3f, 1.0f, 0.0f, 0.0f, 1.0f);
                }
                return true;
            }
            final float n7 = n4 - n2;
            final float n8 = -(n3 - n);
            this.perp.set(n7, n8);
            this.perp.normalize();
            final float n9 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n10 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n11 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n12 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-n7, -n8);
            this.perp.normalize();
            final float n13 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n14 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n15 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n16 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            for (int i = 0; i < this.pts.size(); ++i) {
                this.pointPool.release(this.pts.get(i));
            }
            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)n, (int)n2));
            if ((int)n != (int)n3 || (int)n2 != (int)n4) {
                this.pts.add(this.pointPool.alloc().init((int)n3, (int)n4));
            }
            polygonalMap2.supercover(n9, n10, n11, n12, n5, this.pointPool, this.pts);
            polygonalMap2.supercover(n13, n14, n15, n16, n5, this.pointPool, this.pts);
            if (b4) {
                for (int j = 0; j < this.pts.size(); ++j) {
                    final Point point = this.pts.get(j);
                    LineDrawer.addLine((float)point.x, (float)point.y, (float)n5, point.x + 1.0f, point.y + 1.0f, (float)n5, 1.0f, 1.0f, 0.0f, null, false);
                }
            }
            boolean b5 = false;
            for (int k = 0; k < this.pts.size(); ++k) {
                final Point point2 = this.pts.get(k);
                final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(point2.x, point2.y, n5);
                if (b3 && gridSquare2 != null && SquareUpdateTask.getCost(gridSquare2) > 0) {
                    return true;
                }
                Label_0705: {
                    if (gridSquare2 != null && !gridSquare2.isSolid() && (!gridSquare2.isSolidTrans() || gridSquare2.isAdjacentToWindow()) && !gridSquare2.HasStairs()) {
                        if (gridSquare2.SolidFloorCached) {
                            if (!gridSquare2.SolidFloor) {
                                break Label_0705;
                            }
                        }
                        else if (!gridSquare2.TreatAsSolidFloor()) {
                            break Label_0705;
                        }
                        if (gridSquare2.Is(IsoFlagType.collideW) || (!b && gridSquare2.hasBlockedDoor(false))) {
                            float n17 = 0.3f;
                            float n18 = 0.3f;
                            float n19 = 0.3f;
                            float n20 = 0.3f;
                            if (n < point2.x && n3 < point2.x) {
                                n17 = 0.0f;
                            }
                            else if (n >= point2.x && n3 >= point2.x) {
                                n19 = 0.0f;
                            }
                            if (n2 < point2.y && n4 < point2.y) {
                                n18 = 0.0f;
                            }
                            else if (n2 >= point2.y + 1 && n4 >= point2.y + 1) {
                                n20 = 0.0f;
                            }
                            if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point2.x - n17, point2.y - n18, point2.x + n19, point2.y + 1.0f + n20)) {
                                if (!b4) {
                                    return true;
                                }
                                LineDrawer.addLine(point2.x - n17, point2.y - n18, (float)n5, point2.x + n19, point2.y + 1.0f + n20, (float)n5, 1.0f, 0.0f, 0.0f, null, false);
                                b5 = true;
                            }
                        }
                        if (!gridSquare2.Is(IsoFlagType.collideN) && (b || !gridSquare2.hasBlockedDoor(true))) {
                            continue;
                        }
                        float n21 = 0.3f;
                        float n22 = 0.3f;
                        float n23 = 0.3f;
                        float n24 = 0.3f;
                        if (n < point2.x && n3 < point2.x) {
                            n21 = 0.0f;
                        }
                        else if (n >= point2.x + 1 && n3 >= point2.x + 1) {
                            n23 = 0.0f;
                        }
                        if (n2 < point2.y && n4 < point2.y) {
                            n22 = 0.0f;
                        }
                        else if (n2 >= point2.y && n4 >= point2.y) {
                            n24 = 0.0f;
                        }
                        if (!this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point2.x - n21, point2.y - n22, point2.x + 1.0f + n23, point2.y + n24)) {
                            continue;
                        }
                        if (b4) {
                            LineDrawer.addLine(point2.x - n21, point2.y - n22, (float)n5, point2.x + 1.0f + n23, point2.y + n24, (float)n5, 1.0f, 0.0f, 0.0f, null, false);
                            b5 = true;
                            continue;
                        }
                        return true;
                    }
                }
                float n25 = 0.3f;
                float n26 = 0.3f;
                float n27 = 0.3f;
                float n28 = 0.3f;
                if (n < point2.x && n3 < point2.x) {
                    n25 = 0.0f;
                }
                else if (n >= point2.x + 1 && n3 >= point2.x + 1) {
                    n27 = 0.0f;
                }
                if (n2 < point2.y && n4 < point2.y) {
                    n26 = 0.0f;
                }
                else if (n2 >= point2.y + 1 && n4 >= point2.y + 1) {
                    n28 = 0.0f;
                }
                if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point2.x - n25, point2.y - n26, point2.x + 1.0f + n27, point2.y + 1.0f + n28)) {
                    if (!b4) {
                        return true;
                    }
                    LineDrawer.addLine(point2.x - n25, point2.y - n26, (float)n5, point2.x + 1.0f + n27, point2.y + 1.0f + n28, (float)n5, 1.0f, 0.0f, 0.0f, null, false);
                    b5 = true;
                }
            }
            final float plus_RADIUS = BaseVehicle.PLUS_RADIUS;
            this.perp.set(n7, n8);
            this.perp.normalize();
            final float n29 = n + this.perp.x * plus_RADIUS;
            final float n30 = n2 + this.perp.y * plus_RADIUS;
            final float n31 = n3 + this.perp.x * plus_RADIUS;
            final float n32 = n4 + this.perp.y * plus_RADIUS;
            this.perp.set(-n7, -n8);
            this.perp.normalize();
            final float n33 = n + this.perp.x * plus_RADIUS;
            final float n34 = n2 + this.perp.y * plus_RADIUS;
            final float n35 = n3 + this.perp.x * plus_RADIUS;
            final float n36 = n4 + this.perp.y * plus_RADIUS;
            final float min = Math.min(n29, Math.min(n31, Math.min(n33, n35)));
            final float min2 = Math.min(n30, Math.min(n32, Math.min(n34, n36)));
            this.sweepAABB.init((int)min, (int)min2, (int)Math.ceil(Math.max(n29, Math.max(n31, Math.max(n33, n35)))) - (int)min, (int)Math.ceil(Math.max(n30, Math.max(n32, Math.max(n34, n36)))) - (int)min2, n5);
            this.polyVec[0].set(n29, n30);
            this.polyVec[1].set(n31, n32);
            this.polyVec[2].set(n35, n36);
            this.polyVec[3].set(n33, n34);
            final int n37 = this.sweepAABB.left() / 10 - 1;
            final int n38 = this.sweepAABB.top() / 10 - 1;
            final int n39 = (int)Math.ceil(this.sweepAABB.right() / 10.0f) + 1;
            for (int n40 = (int)Math.ceil(this.sweepAABB.bottom() / 10.0f) + 1, l = n38; l < n40; ++l) {
                for (int n41 = n37; n41 < n39; ++n41) {
                    final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(n41, l) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(n41 * 10, l * 10, 0);
                    if (isoChunk != null) {
                        for (int index = 0; index < isoChunk.vehicles.size(); ++index) {
                            final BaseVehicle baseVehicle2 = isoChunk.vehicles.get(index);
                            if (baseVehicle2 != baseVehicle) {
                                if (baseVehicle2.VehicleID != -1) {
                                    this.vehiclePoly.init(baseVehicle2.getPoly());
                                    this.vehiclePoly.getAABB(this.vehicleAABB);
                                    if (this.vehicleAABB.intersects(this.sweepAABB) && this.polyVehicleIntersect(this.vehiclePoly, b4)) {
                                        b5 = true;
                                        if (!b4) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return b5;
        }
        
        boolean isNotClearClipper(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final BaseVehicle baseVehicle, final int n6) {
            final boolean b = (n6 & 0x1) != 0x0;
            final boolean b2 = (n6 & 0x2) != 0x0;
            final boolean b3 = (n6 & 0x4) != 0x0;
            final boolean b4 = (n6 & 0x8) != 0x0;
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)n, (int)n2, n5);
            if (gridSquare != null && gridSquare.HasStairs()) {
                return !gridSquare.isSameStaircase((int)n3, (int)n4, n5);
            }
            if (!this.canStandAtClipper(polygonalMap2, n3, n4, (float)n5, baseVehicle, n6)) {
                if (b4) {
                    this.drawCircle(n3, n4, (float)n5, 0.3f, 1.0f, 0.0f, 0.0f, 1.0f);
                }
                return true;
            }
            return PolygonalMap2.instance.collideWithObstaclesPoly.isNotClear(n, n2, n3, n4, n5, b4, baseVehicle, b, b2);
        }
        
        boolean isNotClear(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final BaseVehicle baseVehicle, final int n6) {
            return this.isNotClearOld(polygonalMap2, n, n2, n3, n4, n5, baseVehicle, n6);
        }
        
        Vector2 getCollidepoint(final PolygonalMap2 polygonalMap2, final float n, final float n2, final float n3, final float n4, final int n5, final BaseVehicle baseVehicle, final int n6) {
            final boolean b = (n6 & 0x1) != 0x0;
            final boolean b2 = (n6 & 0x2) != 0x0;
            final boolean b3 = (n6 & 0x4) != 0x0;
            final boolean b4 = (n6 & 0x8) != 0x0;
            final float n7 = n4 - n2;
            final float n8 = -(n3 - n);
            this.perp.set(n7, n8);
            this.perp.normalize();
            final float n9 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n10 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n11 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n12 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-n7, -n8);
            this.perp.normalize();
            final float n13 = n + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n14 = n2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            final float n15 = n3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            final float n16 = n4 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            for (int i = 0; i < this.pts.size(); ++i) {
                this.pointPool.release(this.pts.get(i));
            }
            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)n, (int)n2));
            if ((int)n != (int)n3 || (int)n2 != (int)n4) {
                this.pts.add(this.pointPool.alloc().init((int)n3, (int)n4));
            }
            polygonalMap2.supercover(n9, n10, n11, n12, n5, this.pointPool, this.pts);
            polygonalMap2.supercover(n13, n14, n15, n16, n5, this.pointPool, this.pts);
            this.pts.sort((point, point2) -> (int)(IsoUtils.DistanceManhatten(n, n2, (float)point.x, (float)point.y) - IsoUtils.DistanceManhatten(n, n2, (float)point2.x, (float)point2.y)));
            if (b4) {
                for (int j = 0; j < this.pts.size(); ++j) {
                    final Point point3 = this.pts.get(j);
                    LineDrawer.addLine((float)point3.x, (float)point3.y, (float)n5, point3.x + 1.0f, point3.y + 1.0f, (float)n5, 1.0f, 1.0f, 0.0f, null, false);
                }
            }
            for (int k = 0; k < this.pts.size(); ++k) {
                final Point point4 = this.pts.get(k);
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(point4.x, point4.y, n5);
                if (b3 && gridSquare != null && SquareUpdateTask.getCost(gridSquare) > 0) {
                    return PolygonalMap2.temp.set(point4.x + 0.5f, point4.y + 0.5f);
                }
                Label_0646: {
                    if (gridSquare != null && !gridSquare.isSolid() && (!gridSquare.isSolidTrans() || gridSquare.isAdjacentToWindow()) && !gridSquare.HasStairs()) {
                        if (gridSquare.SolidFloorCached) {
                            if (!gridSquare.SolidFloor) {
                                break Label_0646;
                            }
                        }
                        else if (!gridSquare.TreatAsSolidFloor()) {
                            break Label_0646;
                        }
                        if (gridSquare.Is(IsoFlagType.collideW) || (!b && gridSquare.hasBlockedDoor(false))) {
                            float n17 = 0.3f;
                            float n18 = 0.3f;
                            float n19 = 0.3f;
                            float n20 = 0.3f;
                            if (n < point4.x && n3 < point4.x) {
                                n17 = 0.0f;
                            }
                            else if (n >= point4.x && n3 >= point4.x) {
                                n19 = 0.0f;
                            }
                            if (n2 < point4.y && n4 < point4.y) {
                                n18 = 0.0f;
                            }
                            else if (n2 >= point4.y + 1 && n4 >= point4.y + 1) {
                                n20 = 0.0f;
                            }
                            if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point4.x - n17, point4.y - n18, point4.x + n19, point4.y + 1.0f + n20)) {
                                if (b4) {
                                    LineDrawer.addLine(point4.x - n17, point4.y - n18, (float)n5, point4.x + n19, point4.y + 1.0f + n20, (float)n5, 1.0f, 0.0f, 0.0f, null, false);
                                }
                                return PolygonalMap2.temp.set(point4.x + ((n - n3 < 0.0f) ? -0.5f : 0.5f), point4.y + 0.5f);
                            }
                        }
                        if (!gridSquare.Is(IsoFlagType.collideN) && (b || !gridSquare.hasBlockedDoor(true))) {
                            continue;
                        }
                        float n21 = 0.3f;
                        float n22 = 0.3f;
                        float n23 = 0.3f;
                        float n24 = 0.3f;
                        if (n < point4.x && n3 < point4.x) {
                            n21 = 0.0f;
                        }
                        else if (n >= point4.x + 1 && n3 >= point4.x + 1) {
                            n23 = 0.0f;
                        }
                        if (n2 < point4.y && n4 < point4.y) {
                            n22 = 0.0f;
                        }
                        else if (n2 >= point4.y && n4 >= point4.y) {
                            n24 = 0.0f;
                        }
                        if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point4.x - n21, point4.y - n22, point4.x + 1.0f + n23, point4.y + n24)) {
                            if (b4) {
                                LineDrawer.addLine(point4.x - n21, point4.y - n22, (float)n5, point4.x + 1.0f + n23, point4.y + n24, (float)n5, 1.0f, 0.0f, 0.0f, null, false);
                            }
                            return PolygonalMap2.temp.set(point4.x + 0.5f, point4.y + ((n2 - n4 < 0.0f) ? -0.5f : 0.5f));
                        }
                        continue;
                    }
                }
                float n25 = 0.3f;
                float n26 = 0.3f;
                float n27 = 0.3f;
                float n28 = 0.3f;
                if (n < point4.x && n3 < point4.x) {
                    n25 = 0.0f;
                }
                else if (n >= point4.x + 1 && n3 >= point4.x + 1) {
                    n27 = 0.0f;
                }
                if (n2 < point4.y && n4 < point4.y) {
                    n26 = 0.0f;
                }
                else if (n2 >= point4.y + 1 && n4 >= point4.y + 1) {
                    n28 = 0.0f;
                }
                if (this.LB.lineRectIntersect(n, n2, n3 - n, n4 - n2, point4.x - n25, point4.y - n26, point4.x + 1.0f + n27, point4.y + 1.0f + n28)) {
                    if (b4) {
                        LineDrawer.addLine(point4.x - n25, point4.y - n26, (float)n5, point4.x + 1.0f + n27, point4.y + 1.0f + n28, (float)n5, 1.0f, 0.0f, 0.0f, null, false);
                    }
                    return PolygonalMap2.temp.set(point4.x + 0.5f, point4.y + 0.5f);
                }
            }
            return PolygonalMap2.temp.set(n3, n4);
        }
        
        boolean polyVehicleIntersect(final VehiclePoly vehiclePoly, final boolean b) {
            this.vehicleVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
            this.vehicleVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
            this.vehicleVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
            this.vehicleVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
            boolean b2 = false;
            for (int i = 0; i < 4; ++i) {
                final Vector2 vector2 = this.polyVec[i];
                final Vector2 vector3 = (i == 3) ? this.polyVec[0] : this.polyVec[i + 1];
                for (int j = 0; j < 4; ++j) {
                    final Vector2 vector4 = this.vehicleVec[j];
                    final Vector2 vector5 = (j == 3) ? this.vehicleVec[0] : this.vehicleVec[j + 1];
                    if (Line2D.linesIntersect(vector2.x, vector2.y, vector3.x, vector3.y, vector4.x, vector4.y, vector5.x, vector5.y)) {
                        if (b) {
                            LineDrawer.addLine(vector2.x, vector2.y, 0.0f, vector3.x, vector3.y, 0.0f, 1.0f, 0.0f, 0.0f, null, true);
                            LineDrawer.addLine(vector4.x, vector4.y, 0.0f, vector5.x, vector5.y, 0.0f, 1.0f, 0.0f, 0.0f, null, true);
                        }
                        b2 = true;
                    }
                }
            }
            return b2;
        }
    }
    
    private static final class ConnectedRegions
    {
        PolygonalMap2 map;
        HashSet<Chunk> doneChunks;
        int minX;
        int minY;
        int maxX;
        int maxY;
        int MINX;
        int MINY;
        int WIDTH;
        int HEIGHT;
        BooleanGrid visited;
        int[] stack;
        int stackLen;
        int[] choices;
        int choicesLen;
        
        private ConnectedRegions() {
            this.doneChunks = new HashSet<Chunk>();
            this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
        }
        
        void findAdjacentChunks(final int n, final int n2) {
            this.doneChunks.clear();
            final int n3 = Integer.MAX_VALUE;
            this.minY = n3;
            this.minX = n3;
            final int n4 = Integer.MIN_VALUE;
            this.maxY = n4;
            this.maxX = n4;
            this.findAdjacentChunks(this.map.getChunkFromSquarePos(n, n2));
        }
        
        void findAdjacentChunks(final Chunk chunk) {
            if (chunk == null || this.doneChunks.contains(chunk)) {
                return;
            }
            this.minX = Math.min(this.minX, chunk.wx);
            this.minY = Math.min(this.minY, chunk.wy);
            this.maxX = Math.max(this.maxX, chunk.wx);
            this.maxY = Math.max(this.maxY, chunk.wy);
            this.doneChunks.add(chunk);
            final Chunk chunkFromChunkPos = this.map.getChunkFromChunkPos(chunk.wx - 1, chunk.wy);
            final Chunk chunkFromChunkPos2 = this.map.getChunkFromChunkPos(chunk.wx, chunk.wy - 1);
            final Chunk chunkFromChunkPos3 = this.map.getChunkFromChunkPos(chunk.wx + 1, chunk.wy);
            final Chunk chunkFromChunkPos4 = this.map.getChunkFromChunkPos(chunk.wx, chunk.wy + 1);
            this.findAdjacentChunks(chunkFromChunkPos);
            this.findAdjacentChunks(chunkFromChunkPos2);
            this.findAdjacentChunks(chunkFromChunkPos3);
            this.findAdjacentChunks(chunkFromChunkPos4);
        }
        
        void floodFill(final int n, final int n2) {
            this.findAdjacentChunks(n, n2);
            this.MINX = this.minX * 10;
            this.MINY = this.minY * 10;
            this.WIDTH = (this.maxX - this.minX + 1) * 10;
            this.HEIGHT = (this.maxY - this.minY + 1) * 10;
            this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
            this.stack = new int[this.WIDTH * this.WIDTH];
            this.choices = new int[this.WIDTH * this.HEIGHT];
            this.stackLen = 0;
            this.choicesLen = 0;
            if (!this.push(n, n2)) {
                return;
            }
            int pop;
        Label_0133:
            while ((pop = this.pop()) != -1) {
                int n3;
                int n4;
                for (n3 = this.MINX + (pop & 0xFFFF), n4 = (this.MINY + (pop >> 16) & 0xFFFF); this.shouldVisit(n3, n4, n3, n4 - 1); --n4) {}
                int n5 = 0;
                int n6 = 0;
                while (this.visit(n3, n4)) {
                    if (n5 == 0 && this.shouldVisit(n3, n4, n3 - 1, n4)) {
                        if (!this.push(n3 - 1, n4)) {
                            return;
                        }
                        n5 = 1;
                    }
                    else if (n5 != 0 && !this.shouldVisit(n3, n4, n3 - 1, n4)) {
                        n5 = 0;
                    }
                    else if (n5 != 0 && !this.shouldVisit(n3 - 1, n4, n3 - 1, n4 - 1) && !this.push(n3 - 1, n4)) {
                        return;
                    }
                    if (n6 == 0 && this.shouldVisit(n3, n4, n3 + 1, n4)) {
                        if (!this.push(n3 + 1, n4)) {
                            return;
                        }
                        n6 = 1;
                    }
                    else if (n6 != 0 && !this.shouldVisit(n3, n4, n3 + 1, n4)) {
                        n6 = 0;
                    }
                    else if (n6 != 0 && !this.shouldVisit(n3 + 1, n4, n3 + 1, n4 - 1) && !this.push(n3 + 1, n4)) {
                        return;
                    }
                    ++n4;
                    if (!this.shouldVisit(n3, n4 - 1, n3, n4)) {
                        continue Label_0133;
                    }
                }
                return;
            }
            System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.choicesLen));
        }
        
        boolean shouldVisit(final int n, final int n2, final int n3, final int n4) {
            if (n3 >= this.MINX + this.WIDTH || n3 < this.MINX) {
                return false;
            }
            if (n4 >= this.MINY + this.WIDTH || n4 < this.MINY) {
                return false;
            }
            if (this.visited.getValue(this.gridX(n3), this.gridY(n4))) {
                return false;
            }
            final Square square = PolygonalMap2.instance.getSquare(n, n2, 0);
            final Square square2 = PolygonalMap2.instance.getSquare(n3, n4, 0);
            return square != null && square2 != null && !this.isBlocked(square, square2, false);
        }
        
        boolean visit(final int n, final int n2) {
            if (this.choicesLen >= this.WIDTH * this.WIDTH) {
                return false;
            }
            this.choices[this.choicesLen++] = (this.gridY(n2) << 16 | (short)this.gridX(n));
            this.visited.setValue(this.gridX(n), this.gridY(n2), true);
            return true;
        }
        
        boolean push(final int n, final int n2) {
            if (this.stackLen >= this.WIDTH * this.WIDTH) {
                return false;
            }
            this.stack[this.stackLen++] = (this.gridY(n2) << 16 | (short)this.gridX(n));
            return true;
        }
        
        int pop() {
            int n;
            if (this.stackLen == 0) {
                n = -1;
            }
            else {
                final int[] stack = this.stack;
                final int stackLen = this.stackLen - 1;
                this.stackLen = stackLen;
                n = stack[stackLen];
            }
            return n;
        }
        
        int gridX(final int n) {
            return n - this.MINX;
        }
        
        int gridY(final int n) {
            return n - this.MINY;
        }
        
        boolean isBlocked(final Square square, final Square square2, final boolean b) {
            assert Math.abs(square.x - square2.x) <= 1;
            assert Math.abs(square.y - square2.y) <= 1;
            assert square.z == square2.z;
            assert square != square2;
            final boolean b2 = square2.x < square.x;
            final boolean b3 = square2.x > square.x;
            final boolean b4 = square2.y < square.y;
            final boolean b5 = square2.y > square.y;
            if (square2.isReallySolid()) {
                return true;
            }
            if (square2.y < square.y && square.has(64)) {
                return true;
            }
            if (square2.x < square.x && square.has(8)) {
                return true;
            }
            if (square2.y > square.y && square2.x == square.x && square2.has(64)) {
                return true;
            }
            if (square2.x > square.x && square2.y == square.y && square2.has(8)) {
                return true;
            }
            if (square2.x != square.x && square2.has(448)) {
                return true;
            }
            if (square2.y != square.y && square2.has(56)) {
                return true;
            }
            if (square2.x != square.x && square.has(448)) {
                return true;
            }
            if (square2.y != square.y && square.has(56)) {
                return true;
            }
            if (!square2.has(512) && !square2.has(504)) {
                return true;
            }
            final boolean b6 = b4 && square.has(4) && (square.x != square2.x || b || !square.has(16384));
            final boolean b7 = b2 && square.has(2) && (square.y != square2.y || b || !square.has(8192));
            final boolean b8 = b5 && square2.has(4) && (square.x != square2.x || b || !square2.has(16384));
            final boolean b9 = b3 && square2.has(2) && (square.y != square2.y || b || !square2.has(8192));
            if (b6 || b7 || b8 || b9) {
                return true;
            }
            if (square2.x != square.x && square2.y != square.y) {
                final Square square3 = PolygonalMap2.instance.getSquare(square.x, square2.y, square.z);
                final Square square4 = PolygonalMap2.instance.getSquare(square2.x, square.y, square.z);
                assert square3 != square && square3 != square2;
                assert square4 != square && square4 != square2;
                if (square2.x == square.x + 1 && square2.y == square.y + 1 && square3 != null && square4 != null && square3.has(4096) && square4.has(2048)) {
                    return true;
                }
                if (square2.x == square.x - 1 && square2.y == square.y - 1 && square3 != null && square4 != null && square3.has(2048) && square4.has(4096)) {
                    return true;
                }
                if (square3 != null && this.isBlocked(square, square3, true)) {
                    return true;
                }
                if (square4 != null && this.isBlocked(square, square4, true)) {
                    return true;
                }
                if (square3 != null && this.isBlocked(square2, square3, true)) {
                    return true;
                }
                if (square4 != null && this.isBlocked(square2, square4, true)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private static final class L_render
    {
        static final Vector2f vector2f;
        
        static {
            vector2f = new Vector2f();
        }
    }
    
    private interface IChunkTask
    {
        void execute();
        
        void release();
    }
    
    public interface IPathfinder
    {
        void Succeeded(final Path p0, final Mover p1);
        
        void Failed(final Mover p0);
    }
    
    private interface IVehicleTask
    {
        void init(final PolygonalMap2 p0, final BaseVehicle p1);
        
        void execute();
        
        void release();
    }
}
