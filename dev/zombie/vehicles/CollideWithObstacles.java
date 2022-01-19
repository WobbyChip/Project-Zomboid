// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.ArrayDeque;
import java.util.Iterator;
import zombie.iso.IsoUtils;
import org.joml.Vector2f;
import zombie.characters.IsoGameCharacter;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.iso.Vector2;
import java.util.ArrayList;

public final class CollideWithObstacles
{
    static final float RADIUS = 0.3f;
    private final ArrayList<CCObstacle> obstacles;
    private final ArrayList<CCNode> nodes;
    private final ArrayList<CCIntersection> intersections;
    private final ImmutableRectF moveBounds;
    private final ImmutableRectF vehicleBounds;
    private final Vector2 move;
    private final Vector2 closest;
    private final Vector2 nodeNormal;
    private final Vector2 edgeVec;
    private final ArrayList<BaseVehicle> vehicles;
    CCObjectOutline[][] oo;
    ArrayList<CCNode> obstacleTraceNodes;
    CompareIntersection comparator;
    
    public CollideWithObstacles() {
        this.obstacles = new ArrayList<CCObstacle>();
        this.nodes = new ArrayList<CCNode>();
        this.intersections = new ArrayList<CCIntersection>();
        this.moveBounds = new ImmutableRectF();
        this.vehicleBounds = new ImmutableRectF();
        this.move = new Vector2();
        this.closest = new Vector2();
        this.nodeNormal = new Vector2();
        this.edgeVec = new Vector2();
        this.vehicles = new ArrayList<BaseVehicle>();
        this.oo = new CCObjectOutline[5][5];
        this.obstacleTraceNodes = new ArrayList<CCNode>();
        this.comparator = new CompareIntersection();
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
    
    void getObstaclesInRect(final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7) {
        this.nodes.clear();
        this.obstacles.clear();
        this.moveBounds.init(n - 1.0f, n2 - 1.0f, n3 - n + 2.0f, n4 - n2 + 2.0f);
        this.getVehiclesInRect(n - 1.0f - 4.0f, n2 - 1.0f - 4.0f, n3 + 2.0f + 8.0f, n4 + 2.0f + 8.0f, n7);
        for (int i = 0; i < this.vehicles.size(); ++i) {
            final PolygonalMap2.VehiclePoly polyPlusRadius = this.vehicles.get(i).getPolyPlusRadius();
            final float min = Math.min(polyPlusRadius.x1, Math.min(polyPlusRadius.x2, Math.min(polyPlusRadius.x3, polyPlusRadius.x4)));
            final float min2 = Math.min(polyPlusRadius.y1, Math.min(polyPlusRadius.y2, Math.min(polyPlusRadius.y3, polyPlusRadius.y4)));
            this.vehicleBounds.init(min, min2, Math.max(polyPlusRadius.x1, Math.max(polyPlusRadius.x2, Math.max(polyPlusRadius.x3, polyPlusRadius.x4))) - min, Math.max(polyPlusRadius.y1, Math.max(polyPlusRadius.y2, Math.max(polyPlusRadius.y3, polyPlusRadius.y4))) - min2);
            if (this.moveBounds.intersects(this.vehicleBounds)) {
                final int n8 = (int)polyPlusRadius.z;
                final CCNode init = CCNode.alloc().init(polyPlusRadius.x1, polyPlusRadius.y1, n8);
                final CCNode init2 = CCNode.alloc().init(polyPlusRadius.x2, polyPlusRadius.y2, n8);
                final CCNode init3 = CCNode.alloc().init(polyPlusRadius.x3, polyPlusRadius.y3, n8);
                final CCNode init4 = CCNode.alloc().init(polyPlusRadius.x4, polyPlusRadius.y4, n8);
                final CCObstacle init5 = CCObstacle.alloc().init();
                final CCEdge init6 = CCEdge.alloc().init(init, init2, init5);
                final CCEdge init7 = CCEdge.alloc().init(init2, init3, init5);
                final CCEdge init8 = CCEdge.alloc().init(init3, init4, init5);
                final CCEdge init9 = CCEdge.alloc().init(init4, init, init5);
                init5.edges.add(init6);
                init5.edges.add(init7);
                init5.edges.add(init8);
                init5.edges.add(init9);
                init5.calcBounds();
                this.obstacles.add(init5);
                this.nodes.add(init);
                this.nodes.add(init2);
                this.nodes.add(init3);
                this.nodes.add(init4);
            }
        }
        if (this.obstacles.isEmpty()) {
            return;
        }
        final int n9 = n5 - 2;
        final int n10 = n6 - 2;
        final int n11 = n5 + 2 + 1;
        final int n12 = n6 + 2 + 1;
        for (int j = n10; j < n12; ++j) {
            for (int k = n9; k < n11; ++k) {
                CCObjectOutline.get(k - n9, j - n10, n7, this.oo).init(k - n9, j - n10, n7);
            }
        }
        for (int l = n10; l < n12 - 1; ++l) {
            for (int n13 = n9; n13 < n11 - 1; ++n13) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n13, l, n7);
                if (gridSquare != null) {
                    if (gridSquare.isSolid() || (gridSquare.isSolidTrans() && !gridSquare.isAdjacentToWindow()) || gridSquare.Has(IsoObjectType.stairsMN) || gridSquare.Has(IsoObjectType.stairsTN) || gridSquare.Has(IsoObjectType.stairsMW) || gridSquare.Has(IsoObjectType.stairsTW)) {
                        CCObjectOutline.setSolid(n13 - n9, l - n10, n7, this.oo);
                    }
                    int is = gridSquare.Is(IsoFlagType.collideW) ? 1 : 0;
                    if (gridSquare.Is(IsoFlagType.windowW) || gridSquare.Is(IsoFlagType.WindowW)) {
                        is = 1;
                    }
                    if (is != 0 && gridSquare.Is(IsoFlagType.doorW)) {
                        is = 0;
                    }
                    int is2 = gridSquare.Is(IsoFlagType.collideN) ? 1 : 0;
                    if (gridSquare.Is(IsoFlagType.windowN) || gridSquare.Is(IsoFlagType.WindowN)) {
                        is2 = 1;
                    }
                    if (is2 != 0 && gridSquare.Is(IsoFlagType.doorN)) {
                        is2 = 0;
                    }
                    if (is != 0 || gridSquare.hasBlockedDoor(false) || gridSquare.Has(IsoObjectType.stairsBN)) {
                        CCObjectOutline.setWest(n13 - n9, l - n10, n7, this.oo);
                    }
                    if (is2 != 0 || gridSquare.hasBlockedDoor(true) || gridSquare.Has(IsoObjectType.stairsBW)) {
                        CCObjectOutline.setNorth(n13 - n9, l - n10, n7, this.oo);
                    }
                    if (gridSquare.Has(IsoObjectType.stairsBN) && n13 != n11 - 2) {
                        if (IsoWorld.instance.CurrentCell.getGridSquare(n13 + 1, l, n7) != null) {
                            CCObjectOutline.setWest(n13 + 1 - n9, l - n10, n7, this.oo);
                        }
                    }
                    else if (gridSquare.Has(IsoObjectType.stairsBW) && l != n12 - 2 && IsoWorld.instance.CurrentCell.getGridSquare(n13, l + 1, n7) != null) {
                        CCObjectOutline.setNorth(n13 - n9, l + 1 - n10, n7, this.oo);
                    }
                }
            }
        }
        for (int n14 = 0; n14 < n12 - n10; ++n14) {
            for (int n15 = 0; n15 < n11 - n9; ++n15) {
                final CCObjectOutline value = CCObjectOutline.get(n15, n14, n7, this.oo);
                if (value != null && value.nw && value.nw_w && value.nw_n) {
                    value.trace(this.oo, this.obstacleTraceNodes);
                    if (!value.nodes.isEmpty()) {
                        final CCObstacle init10 = CCObstacle.alloc().init();
                        final CCNode ccNode = value.nodes.get(value.nodes.size() - 1);
                        for (int index = value.nodes.size() - 1; index > 0; --index) {
                            final CCNode e = value.nodes.get(index);
                            final CCNode ccNode2 = value.nodes.get(index - 1);
                            final CCNode ccNode3 = e;
                            ccNode3.x += n9;
                            final CCNode ccNode4 = e;
                            ccNode4.y += n10;
                            final CCEdge init11 = CCEdge.alloc().init(e, ccNode2, init10);
                            init11.normal.set(ccNode2.x + ((ccNode2 != ccNode) ? ((float)n9) : 0.0f) - e.x, ccNode2.y + ((ccNode2 != ccNode) ? ((float)n10) : 0.0f) - e.y);
                            init11.normal.normalize();
                            init11.normal.rotate((float)Math.toRadians(90.0));
                            init10.edges.add(init11);
                            this.nodes.add(e);
                        }
                        init10.calcBounds();
                        this.obstacles.add(init10);
                    }
                }
            }
        }
    }
    
    void checkEdgeIntersection() {
        final boolean b = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
        for (int i = 0; i < this.obstacles.size(); ++i) {
            final CCObstacle ccObstacle = this.obstacles.get(i);
            for (int j = i + 1; j < this.obstacles.size(); ++j) {
                final CCObstacle ccObstacle2 = this.obstacles.get(j);
                if (ccObstacle.bounds.intersects(ccObstacle2.bounds)) {
                    for (int k = 0; k < ccObstacle.edges.size(); ++k) {
                        final CCEdge ccEdge = ccObstacle.edges.get(k);
                        for (int l = 0; l < ccObstacle2.edges.size(); ++l) {
                            final CCEdge ccEdge2 = ccObstacle2.edges.get(l);
                            final CCIntersection intersection = this.getIntersection(ccEdge, ccEdge2);
                            if (intersection != null) {
                                ccEdge.intersections.add(intersection);
                                ccEdge2.intersections.add(intersection);
                                if (b) {
                                    LineDrawer.addLine(intersection.nodeSplit.x - 0.1f, intersection.nodeSplit.y - 0.1f, (float)ccEdge.node1.z, intersection.nodeSplit.x + 0.1f, intersection.nodeSplit.y + 0.1f, (float)ccEdge.node1.z, 1.0f, 0.0f, 0.0f, null, false);
                                }
                                if (!ccEdge.hasNode(intersection.nodeSplit) && !ccEdge2.hasNode(intersection.nodeSplit)) {
                                    this.nodes.add(intersection.nodeSplit);
                                }
                                this.intersections.add(intersection);
                            }
                        }
                    }
                }
            }
        }
        for (int index = 0; index < this.obstacles.size(); ++index) {
            final CCObstacle ccObstacle3 = this.obstacles.get(index);
            for (int index2 = ccObstacle3.edges.size() - 1; index2 >= 0; --index2) {
                final CCEdge edge = ccObstacle3.edges.get(index2);
                if (!edge.intersections.isEmpty()) {
                    this.comparator.edge = edge;
                    Collections.sort(edge.intersections, this.comparator);
                    for (int index3 = edge.intersections.size() - 1; index3 >= 0; --index3) {
                        edge.intersections.get(index3).split(edge);
                    }
                }
            }
        }
    }
    
    boolean collinear(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float n7 = (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
        return n7 >= -0.05f && n7 < 0.05f;
    }
    
    boolean within(final float n, final float n2, final float n3) {
        return (n <= n2 && n2 <= n3) || (n3 <= n2 && n2 <= n);
    }
    
    boolean is_on(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return this.collinear(n, n2, n3, n4, n5, n6) && ((n == n3) ? this.within(n2, n6, n4) : this.within(n, n5, n3));
    }
    
    public CCIntersection getIntersection(final CCEdge ccEdge, final CCEdge ccEdge2) {
        final float x = ccEdge.node1.x;
        final float y = ccEdge.node1.y;
        final float x2 = ccEdge.node2.x;
        final float y2 = ccEdge.node2.y;
        final float x3 = ccEdge2.node1.x;
        final float y3 = ccEdge2.node1.y;
        final float x4 = ccEdge2.node2.x;
        final float y4 = ccEdge2.node2.y;
        final double n = (y4 - y3) * (x2 - x) - (x4 - x3) * (y2 - y);
        if (n > -0.01 && n < 0.01) {
            return null;
        }
        final double n2 = ((x4 - x3) * (y - y3) - (y4 - y3) * (x - x3)) / n;
        final double n3 = ((x2 - x) * (y - y3) - (y2 - y) * (x - x3)) / n;
        if (n2 < 0.0 || n2 > 1.0 || n3 < 0.0 || n3 > 1.0) {
            return null;
        }
        final float n4 = (float)(x + n2 * (x2 - x));
        final float n5 = (float)(y + n2 * (y2 - y));
        CCNode ccNode = null;
        CCNode ccNode2 = null;
        if (n2 < 0.009999999776482582) {
            ccNode = ccEdge.node1;
        }
        else if (n2 > 0.9900000095367432) {
            ccNode = ccEdge.node2;
        }
        if (n3 < 0.009999999776482582) {
            ccNode2 = ccEdge2.node1;
        }
        else if (n3 > 0.9900000095367432) {
            ccNode2 = ccEdge2.node2;
        }
        if (ccNode != null && ccNode2 != null) {
            final CCIntersection init = CCIntersection.alloc().init(ccEdge, ccEdge2, (float)n2, (float)n3, ccNode);
            ccEdge.intersections.add(init);
            this.intersections.add(init);
            final CCIntersection init2 = CCIntersection.alloc().init(ccEdge, ccEdge2, (float)n2, (float)n3, ccNode2);
            ccEdge2.intersections.add(init2);
            this.intersections.add(init2);
            LineDrawer.addLine(init2.nodeSplit.x - 0.1f, init2.nodeSplit.y - 0.1f, (float)ccEdge.node1.z, init2.nodeSplit.x + 0.1f, init2.nodeSplit.y + 0.1f, (float)ccEdge.node1.z, 1.0f, 0.0f, 0.0f, null, false);
            return null;
        }
        if (ccNode == null && ccNode2 == null) {
            return CCIntersection.alloc().init(ccEdge, ccEdge2, (float)n2, (float)n3, n4, n5);
        }
        return CCIntersection.alloc().init(ccEdge, ccEdge2, (float)n2, (float)n3, (ccNode == null) ? ccNode2 : ccNode);
    }
    
    void checkNodesInObstacles() {
        for (int i = 0; i < this.nodes.size(); ++i) {
            final CCNode ccNode = this.nodes.get(i);
            for (int j = 0; j < this.obstacles.size(); ++j) {
                final CCObstacle ccObstacle = this.obstacles.get(j);
                boolean b = false;
                int k = 0;
                while (k < this.intersections.size()) {
                    final CCIntersection ccIntersection = this.intersections.get(k);
                    if (ccIntersection.nodeSplit == ccNode) {
                        if (ccIntersection.edge1.obstacle == ccObstacle || ccIntersection.edge2.obstacle == ccObstacle) {
                            b = true;
                            break;
                        }
                        break;
                    }
                    else {
                        ++k;
                    }
                }
                if (!b) {
                    if (ccObstacle.isNodeInsideOf(ccNode)) {
                        ccNode.ignore = true;
                        break;
                    }
                }
            }
        }
    }
    
    boolean isVisible(final CCNode ccNode, final CCNode ccNode2) {
        if (ccNode.sharesEdge(ccNode2)) {
            return !ccNode.onSameShapeButDoesNotShareAnEdge(ccNode2);
        }
        return !ccNode.sharesShape(ccNode2);
    }
    
    void calculateNodeVisibility() {
        for (int i = 0; i < this.obstacles.size(); ++i) {
            final CCObstacle ccObstacle = this.obstacles.get(i);
            for (int j = 0; j < ccObstacle.edges.size(); ++j) {
                final CCEdge ccEdge = ccObstacle.edges.get(j);
                if (!ccEdge.node1.ignore) {
                    if (!ccEdge.node2.ignore) {
                        if (this.isVisible(ccEdge.node1, ccEdge.node2)) {
                            ccEdge.node1.visible.add(ccEdge.node2);
                            ccEdge.node2.visible.add(ccEdge.node1);
                        }
                    }
                }
            }
        }
    }
    
    Vector2f resolveCollision(final IsoGameCharacter isoGameCharacter, final float n, final float n2, final Vector2f vector2f) {
        vector2f.set(n, n2);
        if (isoGameCharacter.getCurrentSquare() != null && isoGameCharacter.getCurrentSquare().HasStairs()) {
            return vector2f;
        }
        final boolean b = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
        final float x = isoGameCharacter.x;
        final float y = isoGameCharacter.y;
        if (b) {
            LineDrawer.addLine(x, y, (float)(int)isoGameCharacter.z, n, n2, (float)(int)isoGameCharacter.z, 1.0f, 1.0f, 1.0f, null, true);
        }
        if (x == n && y == n2) {
            return vector2f;
        }
        this.move.set(n - isoGameCharacter.x, n2 - isoGameCharacter.y);
        this.move.normalize();
        for (int i = 0; i < this.nodes.size(); ++i) {
            this.nodes.get(i).release();
        }
        for (int j = 0; j < this.obstacles.size(); ++j) {
            final CCObstacle ccObstacle = this.obstacles.get(j);
            for (int k = 0; k < ccObstacle.edges.size(); ++k) {
                ccObstacle.edges.get(k).release();
            }
            ccObstacle.release();
        }
        for (int l = 0; l < this.intersections.size(); ++l) {
            this.intersections.get(l).release();
        }
        this.intersections.clear();
        this.getObstaclesInRect(Math.min(x, n), Math.min(y, n2), Math.max(x, n), Math.max(y, n2), (int)isoGameCharacter.x, (int)isoGameCharacter.y, (int)isoGameCharacter.z);
        this.checkEdgeIntersection();
        this.checkNodesInObstacles();
        this.calculateNodeVisibility();
        if (b) {
            for (final CCNode ccNode : this.nodes) {
                for (final CCNode ccNode2 : ccNode.visible) {
                    LineDrawer.addLine(ccNode.x, ccNode.y, (float)ccNode.z, ccNode2.x, ccNode2.y, (float)ccNode2.z, 0.0f, 1.0f, 0.0f, null, true);
                }
                if (DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue() && ccNode.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
                    LineDrawer.addLine(ccNode.x, ccNode.y, (float)ccNode.z, ccNode.x + this.nodeNormal.x, ccNode.y + this.nodeNormal.y, (float)ccNode.z, 0.0f, 0.0f, 1.0f, null, true);
                }
                if (ccNode.ignore) {
                    LineDrawer.addLine(ccNode.x - 0.05f, ccNode.y - 0.05f, (float)ccNode.z, ccNode.x + 0.05f, ccNode.y + 0.05f, (float)ccNode.z, 1.0f, 1.0f, 0.0f, null, false);
                }
            }
        }
        CCEdge ccEdge = null;
        CCNode ccNode3 = null;
        double n3 = Double.MAX_VALUE;
        for (int index = 0; index < this.obstacles.size(); ++index) {
            final CCObstacle ccObstacle2 = this.obstacles.get(index);
            if (ccObstacle2.isPointInside(isoGameCharacter.x, isoGameCharacter.y, 0)) {
                for (int index2 = 0; index2 < ccObstacle2.edges.size(); ++index2) {
                    final CCEdge ccEdge2 = ccObstacle2.edges.get(index2);
                    if (ccEdge2.node1.visible.contains(ccEdge2.node2)) {
                        final CCNode closestPoint = ccEdge2.closestPoint(isoGameCharacter.x, isoGameCharacter.y, this.closest);
                        final double n4 = (isoGameCharacter.x - this.closest.x) * (isoGameCharacter.x - this.closest.x) + (isoGameCharacter.y - this.closest.y) * (isoGameCharacter.y - this.closest.y);
                        if (n4 < n3) {
                            n3 = n4;
                            ccEdge = ccEdge2;
                            ccNode3 = closestPoint;
                        }
                    }
                }
            }
        }
        if (ccEdge != null && ccEdge.normal.dot(this.move) >= 0.01f) {
            ccEdge = null;
        }
        if (ccNode3 != null && ccNode3.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec) && this.nodeNormal.dot(this.move) + 0.05f >= this.nodeNormal.dot(this.edgeVec)) {
            ccNode3 = null;
            ccEdge = null;
        }
        if (ccEdge == null) {
            double n5 = Double.MAX_VALUE;
            ccEdge = null;
            ccNode3 = null;
            for (int index3 = 0; index3 < this.obstacles.size(); ++index3) {
                final CCObstacle ccObstacle3 = this.obstacles.get(index3);
                for (int index4 = 0; index4 < ccObstacle3.edges.size(); ++index4) {
                    final CCEdge ccEdge3 = ccObstacle3.edges.get(index4);
                    if (ccEdge3.node1.visible.contains(ccEdge3.node2)) {
                        final float x2 = ccEdge3.node1.x;
                        final float y2 = ccEdge3.node1.y;
                        final float x3 = ccEdge3.node2.x;
                        final float y3 = ccEdge3.node2.y;
                        final float n6 = x2 + 0.5f * (x3 - x2);
                        final float n7 = y2 + 0.5f * (y3 - y2);
                        if (b && DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
                            LineDrawer.addLine(n6, n7, (float)ccEdge3.node1.z, n6 + ccEdge3.normal.x, n7 + ccEdge3.normal.y, (float)ccEdge3.node1.z, 0.0f, 0.0f, 1.0f, null, true);
                        }
                        final double n8 = (y3 - y2) * (n - x) - (x3 - x2) * (n2 - y);
                        if (n8 != 0.0) {
                            final double n9 = ((x3 - x2) * (y - y2) - (y3 - y2) * (x - x2)) / n8;
                            final double n10 = ((n - x) * (y - y2) - (n2 - y) * (x - x2)) / n8;
                            if (ccEdge3.normal.dot(this.move) < 0.0f) {
                                if (n9 >= 0.0 && n9 <= 1.0 && n10 >= 0.0 && n10 <= 1.0) {
                                    if (n10 < 0.01 || n10 > 0.99) {
                                        final CCNode ccNode4 = (n10 < 0.01) ? ccEdge3.node1 : ccEdge3.node2;
                                        if (ccNode4.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
                                            if (this.nodeNormal.dot(this.move) + 0.05f >= this.nodeNormal.dot(this.edgeVec)) {
                                                continue;
                                            }
                                            ccEdge = ccEdge3;
                                            ccNode3 = ccNode4;
                                            break;
                                        }
                                    }
                                    final double n11 = IsoUtils.DistanceToSquared(x, y, (float)(x + n9 * (n - x)), (float)(y + n9 * (n2 - y)));
                                    if (n11 < n5) {
                                        n5 = n11;
                                        ccEdge = ccEdge3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (ccNode3 != null) {
            final CCEdge ccEdge4 = ccEdge;
            CCEdge ccEdge5 = null;
            for (int index5 = 0; index5 < ccNode3.edges.size(); ++index5) {
                final CCEdge ccEdge6 = ccNode3.edges.get(index5);
                if (ccEdge6.node1.visible.contains(ccEdge6.node2)) {
                    if (ccEdge6 != ccEdge) {
                        if (ccEdge4.node1.x != ccEdge6.node1.x || ccEdge4.node1.y != ccEdge6.node1.y || ccEdge4.node2.x != ccEdge6.node2.x || ccEdge4.node2.y != ccEdge6.node2.y) {
                            if (ccEdge4.node1.x != ccEdge6.node2.x || ccEdge4.node1.y != ccEdge6.node2.y || ccEdge4.node2.x != ccEdge6.node1.x || ccEdge4.node2.y != ccEdge6.node1.y) {
                                if (!ccEdge4.hasNode(ccEdge6.node1) || !ccEdge4.hasNode(ccEdge6.node2)) {
                                    ccEdge5 = ccEdge6;
                                }
                            }
                        }
                    }
                }
            }
            if (ccEdge4 != null && ccEdge5 != null) {
                if (ccEdge == ccEdge4) {
                    final CCNode ccNode5 = (ccNode3 == ccEdge5.node1) ? ccEdge5.node2 : ccEdge5.node1;
                    this.edgeVec.set(ccNode5.x - ccNode3.x, ccNode5.y - ccNode3.y);
                    this.edgeVec.normalize();
                    if (this.move.dot(this.edgeVec) >= 0.0f) {
                        ccEdge = ccEdge5;
                    }
                }
                else if (ccEdge == ccEdge5) {
                    final CCNode ccNode6 = (ccNode3 == ccEdge4.node1) ? ccEdge4.node2 : ccEdge4.node1;
                    this.edgeVec.set(ccNode6.x - ccNode3.x, ccNode6.y - ccNode3.y);
                    this.edgeVec.normalize();
                    if (this.move.dot(this.edgeVec) >= 0.0f) {
                        ccEdge = ccEdge4;
                    }
                }
            }
        }
        if (ccEdge != null) {
            final float x4 = ccEdge.node1.x;
            final float y4 = ccEdge.node1.y;
            final float x5 = ccEdge.node2.x;
            final float y5 = ccEdge.node2.y;
            if (b) {
                LineDrawer.addLine(x4, y4, (float)ccEdge.node1.z, x5, y5, (float)ccEdge.node1.z, 0.0f, 1.0f, 1.0f, null, true);
            }
            ccEdge.closestPoint(n, n2, this.closest);
            vector2f.set(this.closest.x, this.closest.y);
        }
        return vector2f;
    }
    
    private static final class CCNode
    {
        float x;
        float y;
        int z;
        boolean ignore;
        final ArrayList<CCEdge> edges;
        final ArrayList<CCNode> visible;
        static ArrayList<CCObstacle> tempObstacles;
        static ArrayDeque<CCNode> pool;
        
        private CCNode() {
            this.edges = new ArrayList<CCEdge>();
            this.visible = new ArrayList<CCNode>();
        }
        
        CCNode init(final float x, final float y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.ignore = false;
            this.edges.clear();
            this.visible.clear();
            return this;
        }
        
        CCNode setXY(final float x, final float y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        boolean sharesEdge(final CCNode ccNode) {
            for (int i = 0; i < this.edges.size(); ++i) {
                if (this.edges.get(i).hasNode(ccNode)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean sharesShape(final CCNode ccNode) {
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
                for (int j = 0; j < ccNode.edges.size(); ++j) {
                    final CCEdge ccEdge2 = ccNode.edges.get(j);
                    if (ccEdge.obstacle != null && ccEdge.obstacle == ccEdge2.obstacle) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        void getObstacles(final ArrayList<CCObstacle> list) {
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
                if (!list.contains(ccEdge.obstacle)) {
                    list.add(ccEdge.obstacle);
                }
            }
        }
        
        boolean onSameShapeButDoesNotShareAnEdge(final CCNode ccNode) {
            CCNode.tempObstacles.clear();
            this.getObstacles(CCNode.tempObstacles);
            for (int i = 0; i < CCNode.tempObstacles.size(); ++i) {
                final CCObstacle ccObstacle = CCNode.tempObstacles.get(i);
                if (ccObstacle.hasNode(ccNode) && !ccObstacle.hasAdjacentNodes(this, ccNode)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean getNormalAndEdgeVectors(final Vector2 vector2, final Vector2 vector3) {
            CCEdge ccEdge = null;
            CCEdge ccEdge2 = null;
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge3 = this.edges.get(i);
                if (ccEdge3.node1.visible.contains(ccEdge3.node2)) {
                    if (ccEdge == null) {
                        ccEdge = ccEdge3;
                    }
                    else if (!ccEdge.hasNode(ccEdge3.node1) || !ccEdge.hasNode(ccEdge3.node2)) {
                        ccEdge2 = ccEdge3;
                    }
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
            if (CCNode.pool.isEmpty()) {}
            return CCNode.pool.isEmpty() ? new CCNode() : CCNode.pool.pop();
        }
        
        void release() {
            assert !CCNode.pool.contains(this);
            CCNode.pool.push(this);
        }
        
        static {
            CCNode.tempObstacles = new ArrayList<CCObstacle>();
            CCNode.pool = new ArrayDeque<CCNode>();
        }
    }
    
    private static final class CCEdge
    {
        CCNode node1;
        CCNode node2;
        CCObstacle obstacle;
        final ArrayList<CCIntersection> intersections;
        final Vector2 normal;
        static ArrayDeque<CCEdge> pool;
        
        private CCEdge() {
            this.intersections = new ArrayList<CCIntersection>();
            this.normal = new Vector2();
        }
        
        CCEdge init(final CCNode node1, final CCNode node2, final CCObstacle obstacle) {
            if (node1.x == node2.x && node1.y == node2.y) {}
            this.node1 = node1;
            this.node2 = node2;
            node1.edges.add(this);
            node2.edges.add(this);
            this.obstacle = obstacle;
            this.intersections.clear();
            this.normal.set(node2.x - node1.x, node2.y - node1.y);
            this.normal.normalize();
            this.normal.rotate((float)Math.toRadians(90.0));
            return this;
        }
        
        boolean hasNode(final CCNode ccNode) {
            return ccNode == this.node1 || ccNode == this.node2;
        }
        
        CCEdge split(final CCNode node2) {
            final CCEdge init = alloc().init(node2, this.node2, this.obstacle);
            this.obstacle.edges.add(this.obstacle.edges.indexOf(this) + 1, init);
            this.node2.edges.remove(this);
            this.node2 = node2;
            this.node2.edges.add(this);
            return init;
        }
        
        CCNode closestPoint(final float n, final float n2, final Vector2 vector2) {
            final float x = this.node1.x;
            final float y = this.node1.y;
            final float x2 = this.node2.x;
            final float y2 = this.node2.y;
            final double n3 = ((n - x) * (x2 - x) + (n2 - y) * (y2 - y)) / (Math.pow(x2 - x, 2.0) + Math.pow(y2 - y, 2.0));
            final double n4 = 0.001;
            if (n3 <= 0.0 + n4) {
                vector2.set(x, y);
                return this.node1;
            }
            if (n3 >= 1.0 - n4) {
                vector2.set(x2, y2);
                return this.node2;
            }
            vector2.set((float)(x + n3 * (x2 - x)), (float)(y + n3 * (y2 - y)));
            return null;
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
            return CCEdge.pool.isEmpty() ? new CCEdge() : CCEdge.pool.pop();
        }
        
        void release() {
            assert !CCEdge.pool.contains(this);
            CCEdge.pool.push(this);
        }
        
        static {
            CCEdge.pool = new ArrayDeque<CCEdge>();
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
    
    private static final class CCObstacle
    {
        final ArrayList<CCEdge> edges;
        ImmutableRectF bounds;
        static ArrayDeque<CCObstacle> pool;
        
        private CCObstacle() {
            this.edges = new ArrayList<CCEdge>();
        }
        
        CCObstacle init() {
            this.edges.clear();
            return this;
        }
        
        boolean hasNode(final CCNode ccNode) {
            for (int i = 0; i < this.edges.size(); ++i) {
                if (this.edges.get(i).hasNode(ccNode)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean hasAdjacentNodes(final CCNode ccNode, final CCNode ccNode2) {
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
                if (ccEdge.hasNode(ccNode) && ccEdge.hasNode(ccNode2)) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isPointInPolygon_CrossingNumber(final float n, final float n2) {
            int n3 = 0;
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
                if (((ccEdge.node1.y <= n2 && ccEdge.node2.y > n2) || (ccEdge.node1.y > n2 && ccEdge.node2.y <= n2)) && n < ccEdge.node1.x + (n2 - ccEdge.node1.y) / (ccEdge.node2.y - ccEdge.node1.y) * (ccEdge.node2.x - ccEdge.node1.x)) {
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
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
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
        
        boolean isPointInside(final float n, final float n2, final int n3) {
            return this.isPointInPolygon_WindingNumber(n, n2, n3) == EdgeRingHit.Inside;
        }
        
        boolean isNodeInsideOf(final CCNode ccNode) {
            return !this.hasNode(ccNode) && this.bounds.containsPoint(ccNode.x, ccNode.y) && this.isPointInside(ccNode.x, ccNode.y, 0);
        }
        
        CCNode getClosestPointOnEdge(final float n, final float n2, final Vector2 vector2) {
            double n3 = Double.MAX_VALUE;
            CCNode ccNode = null;
            float x = Float.MAX_VALUE;
            float y = Float.MAX_VALUE;
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
                if (ccEdge.node1.visible.contains(ccEdge.node2)) {
                    final CCNode closestPoint = ccEdge.closestPoint(n, n2, vector2);
                    final double n4 = (n - vector2.x) * (n - vector2.x) + (n2 - vector2.y) * (n2 - vector2.y);
                    if (n4 < n3) {
                        x = vector2.x;
                        y = vector2.y;
                        ccNode = closestPoint;
                        n3 = n4;
                    }
                }
            }
            vector2.set(x, y);
            return ccNode;
        }
        
        void calcBounds() {
            float min = Float.MAX_VALUE;
            float min2 = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            float max2 = Float.MIN_VALUE;
            for (int i = 0; i < this.edges.size(); ++i) {
                final CCEdge ccEdge = this.edges.get(i);
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
        
        static CCObstacle alloc() {
            return CCObstacle.pool.isEmpty() ? new CCObstacle() : CCObstacle.pool.pop();
        }
        
        void release() {
            assert !CCObstacle.pool.contains(this);
            CCObstacle.pool.push(this);
        }
        
        static {
            CCObstacle.pool = new ArrayDeque<CCObstacle>();
        }
    }
    
    private static final class CCObjectOutline
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
        ArrayList<CCNode> nodes;
        static ArrayDeque<CCObjectOutline> pool;
        
        CCObjectOutline init(final int x, final int y, final int z) {
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
        
        static void setSolid(final int n, final int n2, final int n3, final CCObjectOutline[][] array) {
            setWest(n, n2, n3, array);
            setNorth(n, n2, n3, array);
            setWest(n + 1, n2, n3, array);
            setNorth(n, n2 + 1, n3, array);
        }
        
        static void setWest(final int n, final int n2, final int n3, final CCObjectOutline[][] array) {
            final CCObjectOutline value = get(n, n2, n3, array);
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
            final CCObjectOutline ccObjectOutline = value;
            final CCObjectOutline value2 = get(n, n2 + 1, n3, array);
            if (value2 == null) {
                if (ccObjectOutline != null) {
                    ccObjectOutline.w_cutoff = true;
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
        
        static void setNorth(final int n, final int n2, final int n3, final CCObjectOutline[][] array) {
            final CCObjectOutline value = get(n, n2, n3, array);
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
            final CCObjectOutline ccObjectOutline = value;
            final CCObjectOutline value2 = get(n + 1, n2, n3, array);
            if (value2 == null) {
                if (ccObjectOutline != null) {
                    ccObjectOutline.n_cutoff = true;
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
        
        static CCObjectOutline get(final int n, final int n2, final int n3, final CCObjectOutline[][] array) {
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
        
        void trace_NW_N(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x + 0.3f, this.y - 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x + 0.3f, this.y - 0.3f, this.z));
            }
            this.nw_n = false;
            if (this.nw_e) {
                this.trace_NW_E(array, null);
            }
            else if (this.n_n) {
                this.trace_N_N(array, this.nodes.get(this.nodes.size() - 1));
            }
        }
        
        void trace_NW_S(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x - 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x - 0.3f, this.y + 0.3f, this.z));
            }
            this.nw_s = false;
            if (this.nw_w) {
                this.trace_NW_W(array, null);
            }
            else {
                final CCObjectOutline value = get(this.x - 1, this.y, this.z, array);
                if (value == null) {
                    return;
                }
                if (value.n_s) {
                    value.nodes = this.nodes;
                    value.trace_N_S(array, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }
        
        void trace_NW_W(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x - 0.3f, this.y - 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x - 0.3f, this.y - 0.3f, this.z));
            }
            this.nw_w = false;
            if (this.nw_n) {
                this.trace_NW_N(array, null);
            }
            else {
                final CCObjectOutline value = get(this.x, this.y - 1, this.z, array);
                if (value == null) {
                    return;
                }
                if (value.w_w) {
                    value.nodes = this.nodes;
                    value.trace_W_W(array, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }
        
        void trace_NW_E(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x + 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x + 0.3f, this.y + 0.3f, this.z));
            }
            this.nw_e = false;
            if (this.nw_s) {
                this.trace_NW_S(array, null);
            }
            else if (this.w_e) {
                this.trace_W_E(array, this.nodes.get(this.nodes.size() - 1));
            }
        }
        
        void trace_W_E(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x + 0.3f, this.y + 1 - 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x + 0.3f, this.y + 1 - 0.3f, this.z));
            }
            this.w_e = false;
            if (this.w_cutoff) {
                this.nodes.get(this.nodes.size() - 1).setXY(this.x + 0.3f, this.y + 1 + 0.3f);
                this.nodes.add(CCNode.alloc().init(this.x - 0.3f, this.y + 1 + 0.3f, this.z));
                final CCNode init = CCNode.alloc().init(this.x - 0.3f, this.y + 1 - 0.3f, this.z);
                this.nodes.add(init);
                this.trace_W_W(array, init);
                return;
            }
            final CCObjectOutline value = get(this.x, this.y + 1, this.z, array);
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
        
        void trace_W_W(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x - 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x - 0.3f, this.y + 0.3f, this.z));
            }
            this.w_w = false;
            if (this.nw_w) {
                this.trace_NW_W(array, this.nodes.get(this.nodes.size() - 1));
            }
            else {
                final CCObjectOutline value = get(this.x - 1, this.y, this.z, array);
                if (value == null) {
                    return;
                }
                if (value.n_s) {
                    value.nodes = this.nodes;
                    value.trace_N_S(array, null);
                }
            }
        }
        
        void trace_N_N(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x + 1 - 0.3f, this.y - 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x + 1 - 0.3f, this.y - 0.3f, this.z));
            }
            this.n_n = false;
            if (this.n_cutoff) {
                this.nodes.get(this.nodes.size() - 1).setXY(this.x + 1 + 0.3f, this.y - 0.3f);
                this.nodes.add(CCNode.alloc().init(this.x + 1 + 0.3f, this.y + 0.3f, this.z));
                final CCNode init = CCNode.alloc().init(this.x + 1 - 0.3f, this.y + 0.3f, this.z);
                this.nodes.add(init);
                this.trace_N_S(array, init);
                return;
            }
            final CCObjectOutline value = get(this.x + 1, this.y, this.z, array);
            if (value == null) {
                return;
            }
            if (value.nw_n) {
                value.nodes = this.nodes;
                value.trace_NW_N(array, this.nodes.get(this.nodes.size() - 1));
            }
            else {
                final CCObjectOutline value2 = get(this.x + 1, this.y - 1, this.z, array);
                if (value2 == null) {
                    return;
                }
                if (value2.w_w) {
                    value2.nodes = this.nodes;
                    value2.trace_W_W(array, null);
                }
            }
        }
        
        void trace_N_S(final CCObjectOutline[][] array, final CCNode ccNode) {
            if (ccNode != null) {
                ccNode.setXY(this.x + 0.3f, this.y + 0.3f);
            }
            else {
                this.nodes.add(CCNode.alloc().init(this.x + 0.3f, this.y + 0.3f, this.z));
            }
            this.n_s = false;
            if (this.nw_s) {
                this.trace_NW_S(array, this.nodes.get(this.nodes.size() - 1));
            }
            else if (this.w_e) {
                this.trace_W_E(array, null);
            }
        }
        
        void trace(final CCObjectOutline[][] array, final ArrayList<CCNode> nodes) {
            nodes.clear();
            this.nodes = nodes;
            final CCNode init = CCNode.alloc().init(this.x - 0.3f, this.y - 0.3f, this.z);
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
        
        static CCObjectOutline alloc() {
            return CCObjectOutline.pool.isEmpty() ? new CCObjectOutline() : CCObjectOutline.pool.pop();
        }
        
        void release() {
            assert !CCObjectOutline.pool.contains(this);
            CCObjectOutline.pool.push(this);
        }
        
        static {
            CCObjectOutline.pool = new ArrayDeque<CCObjectOutline>();
        }
    }
    
    private static final class CCIntersection
    {
        CCEdge edge1;
        CCEdge edge2;
        float dist1;
        float dist2;
        CCNode nodeSplit;
        static ArrayDeque<CCIntersection> pool;
        
        CCIntersection init(final CCEdge edge1, final CCEdge edge2, final float dist1, final float dist2, final float n, final float n2) {
            this.edge1 = edge1;
            this.edge2 = edge2;
            this.dist1 = dist1;
            this.dist2 = dist2;
            this.nodeSplit = CCNode.alloc().init(n, n2, edge1.node1.z);
            return this;
        }
        
        CCIntersection init(final CCEdge edge1, final CCEdge edge2, final float dist1, final float dist2, final CCNode nodeSplit) {
            this.edge1 = edge1;
            this.edge2 = edge2;
            this.dist1 = dist1;
            this.dist2 = dist2;
            this.nodeSplit = nodeSplit;
            return this;
        }
        
        CCEdge split(final CCEdge ccEdge) {
            if (ccEdge.hasNode(this.nodeSplit)) {
                return null;
            }
            if (ccEdge.node1.x == this.nodeSplit.x && ccEdge.node1.y == this.nodeSplit.y) {
                return null;
            }
            if (ccEdge.node2.x == this.nodeSplit.x && ccEdge.node2.y == this.nodeSplit.y) {
                return null;
            }
            return ccEdge.split(this.nodeSplit);
        }
        
        static CCIntersection alloc() {
            return CCIntersection.pool.isEmpty() ? new CCIntersection() : CCIntersection.pool.pop();
        }
        
        void release() {
            assert !CCIntersection.pool.contains(this);
            CCIntersection.pool.push(this);
        }
        
        static {
            CCIntersection.pool = new ArrayDeque<CCIntersection>();
        }
    }
    
    private static final class ImmutableRectF
    {
        private float x;
        private float y;
        private float w;
        private float h;
        static ArrayDeque<ImmutableRectF> pool;
        
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
            ImmutableRectF.pool = new ArrayDeque<ImmutableRectF>();
        }
    }
    
    static final class CompareIntersection implements Comparator<CCIntersection>
    {
        CCEdge edge;
        
        @Override
        public int compare(final CCIntersection ccIntersection, final CCIntersection ccIntersection2) {
            final float n = (this.edge == ccIntersection.edge1) ? ccIntersection.dist1 : ccIntersection.dist2;
            final float n2 = (this.edge == ccIntersection2.edge1) ? ccIntersection2.dist1 : ccIntersection2.dist2;
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
