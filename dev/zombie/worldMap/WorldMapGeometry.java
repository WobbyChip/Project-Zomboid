// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.math.PZMath;
import java.nio.ByteBuffer;
import zombie.vehicles.Clipper;
import java.util.ArrayList;

public final class WorldMapGeometry
{
    public Type m_type;
    public final ArrayList<WorldMapPoints> m_points;
    public int m_minX;
    public int m_minY;
    public int m_maxX;
    public int m_maxY;
    public float[] m_triangles;
    public ArrayList<TrianglesPerZoom> m_trianglesPerZoom;
    public int m_vboIndex1;
    public int m_vboIndex2;
    public int m_vboIndex3;
    public int m_vboIndex4;
    private static Clipper s_clipper;
    private static ByteBuffer s_vertices;
    
    public WorldMapGeometry() {
        this.m_points = new ArrayList<WorldMapPoints>();
        this.m_triangles = null;
        this.m_trianglesPerZoom = null;
        this.m_vboIndex1 = -1;
        this.m_vboIndex2 = -1;
        this.m_vboIndex3 = -1;
        this.m_vboIndex4 = -1;
    }
    
    public void calculateBounds() {
        final int n = Integer.MAX_VALUE;
        this.m_minY = n;
        this.m_minX = n;
        final int n2 = Integer.MIN_VALUE;
        this.m_maxY = n2;
        this.m_maxX = n2;
        for (int i = 0; i < this.m_points.size(); ++i) {
            final WorldMapPoints worldMapPoints = this.m_points.get(i);
            worldMapPoints.calculateBounds();
            this.m_minX = PZMath.min(this.m_minX, worldMapPoints.m_minX);
            this.m_minY = PZMath.min(this.m_minY, worldMapPoints.m_minY);
            this.m_maxX = PZMath.max(this.m_maxX, worldMapPoints.m_maxX);
            this.m_maxY = PZMath.max(this.m_maxY, worldMapPoints.m_maxY);
        }
    }
    
    public boolean containsPoint(final float n, final float n2) {
        return this.m_type == Type.Polygon && !this.m_points.isEmpty() && this.isPointInPolygon_WindingNumber(n, n2, 0) != PolygonHit.Outside;
    }
    
    public void triangulate(final double[] array) {
        if (WorldMapGeometry.s_clipper == null) {
            WorldMapGeometry.s_clipper = new Clipper();
        }
        WorldMapGeometry.s_clipper.clear();
        final WorldMapPoints worldMapPoints = this.m_points.get(0);
        if (WorldMapGeometry.s_vertices == null || WorldMapGeometry.s_vertices.capacity() < worldMapPoints.size() * 50 * 4) {
            WorldMapGeometry.s_vertices = ByteBuffer.allocateDirect(worldMapPoints.size() * 50 * 4);
        }
        WorldMapGeometry.s_vertices.clear();
        if (worldMapPoints.isClockwise()) {
            for (int i = worldMapPoints.numPoints() - 1; i >= 0; --i) {
                WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints.getX(i));
                WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints.getY(i));
            }
        }
        else {
            for (int j = 0; j < worldMapPoints.numPoints(); ++j) {
                WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints.getX(j));
                WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints.getY(j));
            }
        }
        WorldMapGeometry.s_clipper.addPath(worldMapPoints.numPoints(), WorldMapGeometry.s_vertices, false);
        for (int k = 1; k < this.m_points.size(); ++k) {
            WorldMapGeometry.s_vertices.clear();
            final WorldMapPoints worldMapPoints2 = this.m_points.get(k);
            if (worldMapPoints2.isClockwise()) {
                for (int l = worldMapPoints2.numPoints() - 1; l >= 0; --l) {
                    WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints2.getX(l));
                    WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints2.getY(l));
                }
            }
            else {
                for (int n = 0; n < worldMapPoints2.numPoints(); ++n) {
                    WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints2.getX(n));
                    WorldMapGeometry.s_vertices.putFloat((float)worldMapPoints2.getY(n));
                }
            }
            WorldMapGeometry.s_clipper.addPath(worldMapPoints2.numPoints(), WorldMapGeometry.s_vertices, true);
        }
        if (this.m_minX < 0 || this.m_minY < 0 || this.m_maxX > 300 || this.m_maxY > 300) {
            final int n2 = 900;
            final float n3 = (float)(-n2);
            final float n4 = (float)(-n2);
            final float n5 = (float)(300 + n2);
            final float n6 = (float)(-n2);
            final float n7 = (float)(300 + n2);
            final float n8 = (float)(300 + n2);
            final float n9 = (float)(-n2);
            final float n10 = (float)(300 + n2);
            final float n11 = (float)(-n2);
            final float n12 = 0.0f;
            final float n13 = 0.0f;
            final float n14 = 0.0f;
            final float n15 = 0.0f;
            final float n16 = 300.0f;
            final float n17 = 300.0f;
            final float n18 = 300.0f;
            final float n19 = 300.0f;
            final float n20 = 0.0f;
            final float n21 = (float)(-n2);
            final float n22 = 0.0f;
            WorldMapGeometry.s_vertices.clear();
            WorldMapGeometry.s_vertices.putFloat(n3).putFloat(n4);
            WorldMapGeometry.s_vertices.putFloat(n5).putFloat(n6);
            WorldMapGeometry.s_vertices.putFloat(n7).putFloat(n8);
            WorldMapGeometry.s_vertices.putFloat(n9).putFloat(n10);
            WorldMapGeometry.s_vertices.putFloat(n11).putFloat(n12);
            WorldMapGeometry.s_vertices.putFloat(n13).putFloat(n14);
            WorldMapGeometry.s_vertices.putFloat(n15).putFloat(n16);
            WorldMapGeometry.s_vertices.putFloat(n17).putFloat(n18);
            WorldMapGeometry.s_vertices.putFloat(n19).putFloat(n20);
            WorldMapGeometry.s_vertices.putFloat(n21).putFloat(n22);
            WorldMapGeometry.s_clipper.addPath(10, WorldMapGeometry.s_vertices, true);
        }
        if (WorldMapGeometry.s_clipper.generatePolygons(0.0) <= 0) {
            return;
        }
        WorldMapGeometry.s_vertices.clear();
        final int triangulate = WorldMapGeometry.s_clipper.triangulate(0, WorldMapGeometry.s_vertices);
        this.m_triangles = new float[triangulate * 2];
        for (int n23 = 0; n23 < triangulate; ++n23) {
            this.m_triangles[n23 * 2] = WorldMapGeometry.s_vertices.getFloat();
            this.m_triangles[n23 * 2 + 1] = WorldMapGeometry.s_vertices.getFloat();
        }
        if (array == null) {
            return;
        }
        for (int n24 = 0; n24 < array.length; ++n24) {
            if (WorldMapGeometry.s_clipper.generatePolygons(array[n24] - ((n24 == 0) ? 0.0 : array[n24 - 1])) > 0) {
                WorldMapGeometry.s_vertices.clear();
                final int triangulate2 = WorldMapGeometry.s_clipper.triangulate(0, WorldMapGeometry.s_vertices);
                final TrianglesPerZoom e = new TrianglesPerZoom();
                e.m_triangles = new float[triangulate2 * 2];
                e.m_delta = array[n24];
                for (int n25 = 0; n25 < triangulate2; ++n25) {
                    e.m_triangles[n25 * 2] = WorldMapGeometry.s_vertices.getFloat();
                    e.m_triangles[n25 * 2 + 1] = WorldMapGeometry.s_vertices.getFloat();
                }
                if (this.m_trianglesPerZoom == null) {
                    this.m_trianglesPerZoom = new ArrayList<TrianglesPerZoom>();
                }
                this.m_trianglesPerZoom.add(e);
            }
        }
    }
    
    TrianglesPerZoom findTriangles(final double n) {
        if (this.m_trianglesPerZoom == null) {
            return null;
        }
        for (int i = 0; i < this.m_trianglesPerZoom.size(); ++i) {
            final TrianglesPerZoom trianglesPerZoom = this.m_trianglesPerZoom.get(i);
            if (trianglesPerZoom.m_delta == n) {
                return trianglesPerZoom;
            }
        }
        return null;
    }
    
    public void dispose() {
        this.m_points.clear();
        this.m_triangles = null;
    }
    
    float isLeft(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
    }
    
    PolygonHit isPointInPolygon_WindingNumber(final float n, final float n2, final int n3) {
        int n4 = 0;
        final WorldMapPoints worldMapPoints = this.m_points.get(0);
        for (int i = 0; i < worldMapPoints.numPoints(); ++i) {
            final int x = worldMapPoints.getX(i);
            final int y = worldMapPoints.getY(i);
            final int x2 = worldMapPoints.getX((i + 1) % worldMapPoints.numPoints());
            final int y2 = worldMapPoints.getY((i + 1) % worldMapPoints.numPoints());
            if (y <= n2) {
                if (y2 > n2 && this.isLeft((float)x, (float)y, (float)x2, (float)y2, n, n2) > 0.0f) {
                    ++n4;
                }
            }
            else if (y2 <= n2 && this.isLeft((float)x, (float)y, (float)x2, (float)y2, n, n2) < 0.0f) {
                --n4;
            }
        }
        return (n4 == 0) ? PolygonHit.Outside : PolygonHit.Inside;
    }
    
    static {
        WorldMapGeometry.s_clipper = null;
        WorldMapGeometry.s_vertices = null;
    }
    
    public enum Type
    {
        LineString, 
        Point, 
        Polygon;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.LineString, Type.Point, Type.Polygon };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class TrianglesPerZoom
    {
        public float[] m_triangles;
        double m_delta;
    }
    
    private enum PolygonHit
    {
        OnEdge, 
        Inside, 
        Outside;
        
        private static /* synthetic */ PolygonHit[] $values() {
            return new PolygonHit[] { PolygonHit.OnEdge, PolygonHit.Inside, PolygonHit.Outside };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
