// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.textures.TextureID;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.PZGLUtil;
import java.util.Iterator;
import zombie.iso.IsoMetaCell;
import java.util.Set;
import zombie.iso.IsoWorld;
import zombie.core.Core;
import zombie.core.textures.Texture;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import zombie.core.skinnedmodel.ModelCamera;
import org.lwjgl.opengl.GL11;
import java.util.List;
import zombie.worldMap.styles.WorldMapTextureStyleLayer;
import java.util.Collection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.iso.IsoCamera;
import zombie.characters.IsoPlayer;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.TIntSet;
import gnu.trove.list.array.TIntArrayList;
import org.joml.Vector2f;
import java.util.HashSet;
import zombie.iso.IsoMetaGrid;
import zombie.worldMap.styles.WorldMapStyleLayer;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import java.util.function.Supplier;
import zombie.config.DoubleConfigOption;
import zombie.config.BooleanConfigOption;
import org.joml.Vector3fc;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import org.joml.Quaternionfc;
import zombie.ui.UIManager;
import zombie.core.math.PZMath;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import zombie.util.list.PZArrayUtil;
import zombie.config.ConfigOption;
import java.util.ArrayList;
import zombie.vehicles.UI3DScene;
import zombie.popman.ObjectPool;
import zombie.core.opengl.VBOLines;
import zombie.worldMap.styles.WorldMapStyle;
import org.joml.Quaternionf;
import org.joml.Matrix4f;

public final class WorldMapRenderer
{
    private WorldMap m_worldMap;
    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;
    private int m_zoom;
    private float m_zoomF;
    private float m_displayZoomF;
    private float m_centerWorldX;
    private float m_centerWorldY;
    private float m_zoomUIX;
    private float m_zoomUIY;
    private float m_zoomWorldX;
    private float m_zoomWorldY;
    private final Matrix4f m_projection;
    private final Matrix4f m_modelView;
    private final Quaternionf m_modelViewChange;
    private long m_viewChangeTime;
    private static long VIEW_CHANGE_TIME;
    private boolean m_isometric;
    private boolean m_firstUpdate;
    private WorldMapVisited m_visited;
    private final Drawer[] m_drawer;
    private final CharacterModelCamera m_CharacterModelCamera;
    private int m_dropShadowWidth;
    public WorldMapStyle m_style;
    protected static final VBOLines m_vboLines;
    protected static final VBOLinesUV m_vboLinesUV;
    private final int[] m_viewport;
    private static final ThreadLocal<ObjectPool<UI3DScene.Plane>> TL_Plane_pool;
    private static final ThreadLocal<ObjectPool<UI3DScene.Ray>> TL_Ray_pool;
    static final float SMALL_NUM = 1.0E-8f;
    private final ArrayList<ConfigOption> options;
    private final WorldMapBooleanOption BlurUnvisited;
    private final WorldMapBooleanOption BuildingsWithoutFeatures;
    private final WorldMapBooleanOption DebugInfo;
    private final WorldMapBooleanOption CellGrid;
    private final WorldMapBooleanOption TileGrid;
    private final WorldMapBooleanOption UnvisitedGrid;
    private final WorldMapBooleanOption Features;
    private final WorldMapBooleanOption ForestZones;
    private final WorldMapBooleanOption HideUnvisited;
    private final WorldMapBooleanOption HitTest;
    private final WorldMapBooleanOption ImagePyramid;
    private final WorldMapBooleanOption Isometric;
    private final WorldMapBooleanOption LineString;
    private final WorldMapBooleanOption Players;
    private final WorldMapBooleanOption Symbols;
    private final WorldMapBooleanOption Wireframe;
    private final WorldMapBooleanOption WorldBounds;
    private final WorldMapBooleanOption MiniMapSymbols;
    private final WorldMapBooleanOption VisibleCells;
    
    public WorldMapRenderer() {
        this.m_zoom = 0;
        this.m_zoomF = 0.0f;
        this.m_displayZoomF = 0.0f;
        this.m_projection = new Matrix4f();
        this.m_modelView = new Matrix4f();
        this.m_modelViewChange = new Quaternionf();
        this.m_firstUpdate = false;
        this.m_drawer = new Drawer[3];
        this.m_CharacterModelCamera = new CharacterModelCamera();
        this.m_dropShadowWidth = 12;
        this.m_style = null;
        this.m_viewport = new int[] { 0, 0, 0, 0 };
        this.options = new ArrayList<ConfigOption>();
        this.BlurUnvisited = new WorldMapBooleanOption("BlurUnvisited", true);
        this.BuildingsWithoutFeatures = new WorldMapBooleanOption("BuildingsWithoutFeatures", false);
        this.DebugInfo = new WorldMapBooleanOption("DebugInfo", false);
        this.CellGrid = new WorldMapBooleanOption("CellGrid", false);
        this.TileGrid = new WorldMapBooleanOption("TileGrid", false);
        this.UnvisitedGrid = new WorldMapBooleanOption("UnvisitedGrid", true);
        this.Features = new WorldMapBooleanOption("Features", true);
        this.ForestZones = new WorldMapBooleanOption("ForestZones", false);
        this.HideUnvisited = new WorldMapBooleanOption("HideUnvisited", false);
        this.HitTest = new WorldMapBooleanOption("HitTest", false);
        this.ImagePyramid = new WorldMapBooleanOption("ImagePyramid", false);
        this.Isometric = new WorldMapBooleanOption("Isometric", true);
        this.LineString = new WorldMapBooleanOption("LineString", true);
        this.Players = new WorldMapBooleanOption("Players", false);
        this.Symbols = new WorldMapBooleanOption("Symbols", true);
        this.Wireframe = new WorldMapBooleanOption("Wireframe", false);
        this.WorldBounds = new WorldMapBooleanOption("WorldBounds", true);
        this.MiniMapSymbols = new WorldMapBooleanOption("MiniMapSymbols", false);
        this.VisibleCells = new WorldMapBooleanOption("VisibleCells", false);
        PZArrayUtil.arrayPopulate(this.m_drawer, Drawer::new);
    }
    
    public int getAbsoluteX() {
        return this.m_x;
    }
    
    public int getAbsoluteY() {
        return this.m_y;
    }
    
    public int getWidth() {
        return this.m_width;
    }
    
    public int getHeight() {
        return this.m_height;
    }
    
    private void calcMatrices(final float n, final float n2, final float n3, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        matrix4f.setOrtho(-width / 2.0f, width / 2.0f, height / 2.0f, -height / 2.0f, -2000.0f, 2000.0f);
        matrix4f2.identity();
        if (this.Isometric.getValue()) {
            matrix4f2.rotateXYZ(1.0471976f, 0.0f, 0.7853982f);
        }
    }
    
    public Vector3f uiToScene(final float n, final float n2, final Matrix4f matrix4f, final Matrix4f matrix4f2, final Vector3f vector3f) {
        final UI3DScene.Plane allocPlane = allocPlane();
        allocPlane.point.set(0.0f);
        allocPlane.normal.set(0.0f, 0.0f, 1.0f);
        final UI3DScene.Ray cameraRay = this.getCameraRay(n, this.getHeight() - n2, matrix4f, matrix4f2, allocRay());
        if (this.intersect_ray_plane(allocPlane, cameraRay, vector3f) != 1) {
            vector3f.set(0.0f);
        }
        releasePlane(allocPlane);
        releaseRay(cameraRay);
        return vector3f;
    }
    
    public Vector3f sceneToUI(final float n, final float n2, final float n3, final Matrix4f matrix4f, final Matrix4f matrix4f2, final Vector3f vector3f) {
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)matrix4f);
        allocMatrix4f.mul((Matrix4fc)matrix4f2);
        this.m_viewport[0] = 0;
        this.m_viewport[1] = 0;
        this.m_viewport[2] = this.getWidth();
        this.m_viewport[3] = this.getHeight();
        allocMatrix4f.project(n, n2, n3, this.m_viewport, vector3f);
        releaseMatrix4f(allocMatrix4f);
        return vector3f;
    }
    
    public float uiToWorldX(final float n, final float n2, final float n3, final float n4, final float n5) {
        final Matrix4f allocMatrix4f = allocMatrix4f();
        final Matrix4f allocMatrix4f2 = allocMatrix4f();
        this.calcMatrices(n4, n5, n3, allocMatrix4f, allocMatrix4f2);
        final float uiToWorldX = this.uiToWorldX(n, n2, n3, n4, n5, allocMatrix4f, allocMatrix4f2);
        releaseMatrix4f(allocMatrix4f);
        releaseMatrix4f(allocMatrix4f2);
        return uiToWorldX;
    }
    
    public float uiToWorldY(final float n, final float n2, final float n3, final float n4, final float n5) {
        final Matrix4f allocMatrix4f = allocMatrix4f();
        final Matrix4f allocMatrix4f2 = allocMatrix4f();
        this.calcMatrices(n4, n5, n3, allocMatrix4f, allocMatrix4f2);
        final float uiToWorldY = this.uiToWorldY(n, n2, n3, n4, n5, allocMatrix4f, allocMatrix4f2);
        releaseMatrix4f(allocMatrix4f);
        releaseMatrix4f(allocMatrix4f2);
        return uiToWorldY;
    }
    
    public float uiToWorldX(final float n, final float n2, final float n3, final float n4, final float n5, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        final Vector3f uiToScene = this.uiToScene(n, n2, matrix4f, matrix4f2, allocVector3f());
        uiToScene.mul(1.0f / this.getWorldScale(n3));
        final float n6 = uiToScene.x() + n4;
        releaseVector3f(uiToScene);
        return n6;
    }
    
    public float uiToWorldY(final float n, final float n2, final float n3, final float n4, final float n5, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        final Vector3f uiToScene = this.uiToScene(n, n2, matrix4f, matrix4f2, allocVector3f());
        uiToScene.mul(1.0f / this.getWorldScale(n3));
        final float n6 = uiToScene.y() + n5;
        releaseVector3f(uiToScene);
        return n6;
    }
    
    public float worldToUIX(final float n, final float n2, final float n3, final float n4, final float n5, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        final float worldScale = this.getWorldScale(n3);
        final Vector3f sceneToUI = this.sceneToUI((n - n4) * worldScale, (n2 - n5) * worldScale, 0.0f, matrix4f, matrix4f2, allocVector3f());
        final float x = sceneToUI.x();
        releaseVector3f(sceneToUI);
        return x;
    }
    
    public float worldToUIY(final float n, final float n2, final float n3, final float n4, final float n5, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        final float worldScale = this.getWorldScale(n3);
        final Vector3f sceneToUI = this.sceneToUI((n - n4) * worldScale, (n2 - n5) * worldScale, 0.0f, matrix4f, matrix4f2, allocVector3f());
        final float n6 = this.getHeight() - sceneToUI.y();
        releaseVector3f(sceneToUI);
        return n6;
    }
    
    public float worldOriginUIX(final float n, final float n2) {
        return this.worldToUIX(0.0f, 0.0f, n, n2, this.m_centerWorldY, this.m_projection, this.m_modelView);
    }
    
    public float worldOriginUIY(final float n, final float n2) {
        return this.worldToUIY(0.0f, 0.0f, n, this.m_centerWorldX, n2, this.m_projection, this.m_modelView);
    }
    
    public int getZoom() {
        return this.m_zoom;
    }
    
    public float getZoomF() {
        return this.m_zoomF;
    }
    
    public float getDisplayZoomF() {
        return this.m_displayZoomF;
    }
    
    public float zoomMult() {
        return this.zoomMult(this.m_zoomF);
    }
    
    public float zoomMult(final float n) {
        return (float)Math.pow(2.0, n);
    }
    
    public float getWorldScale(final float n) {
        return (float)(1.0 / MapProjection.metersPerPixelAtZoom(n, this.getHeight()));
    }
    
    public void zoomAt(final int n, final int n2, final int n3) {
        final float uiToWorldX = this.uiToWorldX((float)n, (float)n2, this.m_displayZoomF, this.m_centerWorldX, this.m_centerWorldY);
        final float uiToWorldY = this.uiToWorldY((float)n, (float)n2, this.m_displayZoomF, this.m_centerWorldX, this.m_centerWorldY);
        this.m_zoomF = PZMath.clamp(this.m_zoomF + n3 / 2.0f, this.getBaseZoom(), 24.0f);
        this.m_zoom = (int)this.m_zoomF;
        this.m_zoomWorldX = uiToWorldX;
        this.m_zoomWorldY = uiToWorldY;
        this.m_zoomUIX = (float)n;
        this.m_zoomUIY = (float)n2;
    }
    
    public float getCenterWorldX() {
        return this.m_centerWorldX;
    }
    
    public float getCenterWorldY() {
        return this.m_centerWorldY;
    }
    
    public void centerOn(final float n, final float n2) {
        this.m_centerWorldX = n;
        this.m_centerWorldY = n2;
        if (this.m_displayZoomF != this.m_zoomF) {
            this.m_zoomWorldX = n;
            this.m_zoomWorldY = n2;
            this.m_zoomUIX = this.m_width / 2.0f;
            this.m_zoomUIY = this.m_height / 2.0f;
        }
    }
    
    public void moveView(final int n, final int n2) {
        this.centerOn(this.m_centerWorldX + n, this.m_centerWorldY + n2);
    }
    
    public double log2(final double a) {
        return Math.log(a) / Math.log(2.0);
    }
    
    public float getBaseZoom() {
        double n = MapProjection.zoomAtMetersPerPixel(this.m_worldMap.getHeightInSquares() / (double)this.getHeight(), this.getHeight());
        if (this.m_worldMap.getWidthInSquares() * this.getWorldScale((float)n) > this.getWidth()) {
            n = MapProjection.zoomAtMetersPerPixel(this.m_worldMap.getWidthInSquares() / (double)this.getWidth(), this.getHeight());
        }
        return (float)((int)(n * 2.0) / 2.0);
    }
    
    public void setZoom(final float n) {
        this.m_zoomF = PZMath.clamp(n, this.getBaseZoom(), 24.0f);
        this.m_zoom = (int)this.m_zoomF;
        this.m_displayZoomF = this.m_zoomF;
    }
    
    public void resetView() {
        this.m_zoomF = this.getBaseZoom();
        this.m_zoom = (int)this.m_zoomF;
        this.m_centerWorldX = this.m_worldMap.getMinXInSquares() + this.m_worldMap.getWidthInSquares() / 2.0f;
        this.m_centerWorldY = this.m_worldMap.getMinYInSquares() + this.m_worldMap.getHeightInSquares() / 2.0f;
        this.m_zoomWorldX = this.m_centerWorldX;
        this.m_zoomWorldY = this.m_centerWorldY;
        this.m_zoomUIX = this.getWidth() / 2.0f;
        this.m_zoomUIY = this.getHeight() / 2.0f;
    }
    
    public Matrix4f getProjectionMatrix() {
        return this.m_projection;
    }
    
    public Matrix4f getModelViewMatrix() {
        return this.m_modelView;
    }
    
    public void setMap(final WorldMap worldMap, final int x, final int y, final int width, final int height) {
        this.m_worldMap = worldMap;
        this.m_x = x;
        this.m_y = y;
        this.m_width = width;
        this.m_height = height;
    }
    
    public WorldMap getWorldMap() {
        return this.m_worldMap;
    }
    
    public void setVisited(final WorldMapVisited visited) {
        this.m_visited = visited;
    }
    
    public void updateView() {
        if (this.m_displayZoomF != this.m_zoomF) {
            final float n = (float)(UIManager.getMillisSinceLastRender() / 750.0);
            final float abs = Math.abs(this.m_zoomF - this.m_displayZoomF);
            final float n2 = (abs > 0.25f) ? (abs / 0.25f) : 1.0f;
            if (this.m_displayZoomF < this.m_zoomF) {
                this.m_displayZoomF = PZMath.min(this.m_displayZoomF + n * n2, this.m_zoomF);
            }
            else if (this.m_displayZoomF > this.m_zoomF) {
                this.m_displayZoomF = PZMath.max(this.m_displayZoomF - n * n2, this.m_zoomF);
            }
            final float uiToWorldX = this.uiToWorldX(this.m_zoomUIX, this.m_zoomUIY, this.m_displayZoomF, 0.0f, 0.0f);
            final float uiToWorldY = this.uiToWorldY(this.m_zoomUIX, this.m_zoomUIY, this.m_displayZoomF, 0.0f, 0.0f);
            this.m_centerWorldX = this.m_zoomWorldX - uiToWorldX;
            this.m_centerWorldY = this.m_zoomWorldY - uiToWorldY;
        }
        if (!this.m_firstUpdate) {
            this.m_firstUpdate = true;
            this.m_isometric = this.Isometric.getValue();
        }
        if (this.m_isometric != this.Isometric.getValue()) {
            this.m_isometric = this.Isometric.getValue();
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.m_viewChangeTime + WorldMapRenderer.VIEW_CHANGE_TIME < currentTimeMillis) {
                this.m_modelViewChange.setFromUnnormalized((Matrix4fc)this.m_modelView);
            }
            this.m_viewChangeTime = currentTimeMillis;
        }
        this.calcMatrices(this.m_centerWorldX, this.m_centerWorldY, this.m_displayZoomF, this.m_projection, this.m_modelView);
        final long currentTimeMillis2 = System.currentTimeMillis();
        if (this.m_viewChangeTime + WorldMapRenderer.VIEW_CHANGE_TIME > currentTimeMillis2) {
            final float n3 = (this.m_viewChangeTime + WorldMapRenderer.VIEW_CHANGE_TIME - currentTimeMillis2) / (float)WorldMapRenderer.VIEW_CHANGE_TIME;
            final Quaternionf setFromUnnormalized = allocQuaternionf().setFromUnnormalized((Matrix4fc)this.m_modelView);
            this.m_modelView.set((Quaternionfc)this.m_modelViewChange.slerp((Quaternionfc)setFromUnnormalized, 1.0f - n3));
            releaseQuaternionf(setFromUnnormalized);
        }
    }
    
    public void render(final UIWorldMap uiWorldMap) {
        this.m_style = uiWorldMap.getAPI().getStyle();
        final int mainStateIndex = SpriteRenderer.instance.getMainStateIndex();
        this.m_drawer[mainStateIndex].init(this, uiWorldMap);
        SpriteRenderer.instance.drawGeneric(this.m_drawer[mainStateIndex]);
    }
    
    public void setDropShadowWidth(final int dropShadowWidth) {
        this.m_dropShadowWidth = dropShadowWidth;
    }
    
    private static Matrix4f allocMatrix4f() {
        return BaseVehicle.TL_matrix4f_pool.get().alloc();
    }
    
    private static void releaseMatrix4f(final Matrix4f matrix4f) {
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
    }
    
    private static Quaternionf allocQuaternionf() {
        return BaseVehicle.TL_quaternionf_pool.get().alloc();
    }
    
    private static void releaseQuaternionf(final Quaternionf quaternionf) {
        BaseVehicle.TL_quaternionf_pool.get().release(quaternionf);
    }
    
    private static UI3DScene.Ray allocRay() {
        return WorldMapRenderer.TL_Ray_pool.get().alloc();
    }
    
    private static void releaseRay(final UI3DScene.Ray ray) {
        WorldMapRenderer.TL_Ray_pool.get().release(ray);
    }
    
    private static UI3DScene.Plane allocPlane() {
        return WorldMapRenderer.TL_Plane_pool.get().alloc();
    }
    
    private static void releasePlane(final UI3DScene.Plane plane) {
        WorldMapRenderer.TL_Plane_pool.get().release(plane);
    }
    
    private static Vector2 allocVector2() {
        return BaseVehicle.TL_vector2_pool.get().alloc();
    }
    
    private static void releaseVector2(final Vector2 vector2) {
        BaseVehicle.TL_vector2_pool.get().release(vector2);
    }
    
    private static Vector3f allocVector3f() {
        return BaseVehicle.TL_vector3f_pool.get().alloc();
    }
    
    private static void releaseVector3f(final Vector3f vector3f) {
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
    }
    
    UI3DScene.Ray getCameraRay(final float n, final float n2, final UI3DScene.Ray ray) {
        return this.getCameraRay(n, n2, this.m_projection, this.m_modelView, ray);
    }
    
    UI3DScene.Ray getCameraRay(final float n, final float n2, final Matrix4f matrix4f, final Matrix4f matrix4f2, final UI3DScene.Ray ray) {
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)matrix4f);
        allocMatrix4f.mul((Matrix4fc)matrix4f2);
        allocMatrix4f.invert();
        this.m_viewport[0] = 0;
        this.m_viewport[1] = 0;
        this.m_viewport[2] = this.getWidth();
        this.m_viewport[3] = this.getHeight();
        final Vector3f unprojectInv = allocMatrix4f.unprojectInv(n, n2, 0.0f, this.m_viewport, allocVector3f());
        final Vector3f unprojectInv2 = allocMatrix4f.unprojectInv(n, n2, 1.0f, this.m_viewport, allocVector3f());
        ray.origin.set((Vector3fc)unprojectInv);
        ray.direction.set((Vector3fc)unprojectInv2.sub((Vector3fc)unprojectInv).normalize());
        releaseVector3f(unprojectInv2);
        releaseVector3f(unprojectInv);
        releaseMatrix4f(allocMatrix4f);
        return ray;
    }
    
    int intersect_ray_plane(final UI3DScene.Plane plane, final UI3DScene.Ray ray, final Vector3f vector3f) {
        final Vector3f mul = allocVector3f().set((Vector3fc)ray.direction).mul(10000.0f);
        final Vector3f sub = allocVector3f().set((Vector3fc)ray.origin).sub((Vector3fc)plane.point);
        try {
            final float dot = plane.normal.dot((Vector3fc)mul);
            final float n = -plane.normal.dot((Vector3fc)sub);
            if (Math.abs(dot) < 1.0E-8f) {
                if (n == 0.0f) {
                    return 2;
                }
                return 0;
            }
            else {
                final float n2 = n / dot;
                if (n2 < 0.0f || n2 > 1.0f) {
                    return 0;
                }
                vector3f.set((Vector3fc)ray.origin).add((Vector3fc)mul.mul(n2));
                return 1;
            }
        }
        finally {
            releaseVector3f(mul);
            releaseVector3f(sub);
        }
    }
    
    public ConfigOption getOptionByName(final String anObject) {
        for (int i = 0; i < this.options.size(); ++i) {
            final ConfigOption configOption = this.options.get(i);
            if (configOption.getName().equals(anObject)) {
                return configOption;
            }
        }
        return null;
    }
    
    public int getOptionCount() {
        return this.options.size();
    }
    
    public ConfigOption getOptionByIndex(final int index) {
        return this.options.get(index);
    }
    
    public void setBoolean(final String s, final boolean value) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof BooleanConfigOption) {
            ((BooleanConfigOption)optionByName).setValue(value);
        }
    }
    
    public boolean getBoolean(final String s) {
        final ConfigOption optionByName = this.getOptionByName(s);
        return optionByName instanceof BooleanConfigOption && ((BooleanConfigOption)optionByName).getValue();
    }
    
    public void setDouble(final String s, final double value) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof DoubleConfigOption) {
            ((DoubleConfigOption)optionByName).setValue(value);
        }
    }
    
    public double getDouble(final String s, final double n) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof DoubleConfigOption) {
            return ((DoubleConfigOption)optionByName).getValue();
        }
        return n;
    }
    
    static {
        WorldMapRenderer.VIEW_CHANGE_TIME = 350L;
        m_vboLines = new VBOLines();
        m_vboLinesUV = new VBOLinesUV();
        TL_Plane_pool = ThreadLocal.withInitial((Supplier<? extends ObjectPool<UI3DScene.Plane>>)UI3DScene.PlaneObjectPool::new);
        TL_Ray_pool = ThreadLocal.withInitial((Supplier<? extends ObjectPool<UI3DScene.Ray>>)UI3DScene.RayObjectPool::new);
    }
    
    private static final class PlayerRenderData
    {
        ModelSlotRenderData m_modelSlotRenderData;
        float m_angle;
        float m_x;
        float m_y;
    }
    
    public static final class Drawer extends TextureDraw.GenericDrawer
    {
        WorldMapRenderer m_renderer;
        final WorldMapStyle m_style;
        WorldMap m_worldMap;
        int m_x;
        int m_y;
        int m_width;
        int m_height;
        float m_centerWorldX;
        float m_centerWorldY;
        int m_zoom;
        public float m_zoomF;
        float m_worldScale;
        float m_renderOriginX;
        float m_renderOriginY;
        float m_renderCellX;
        float m_renderCellY;
        private final Matrix4f m_projection;
        private final Matrix4f m_modelView;
        private final PlayerRenderData[] m_playerRenderData;
        final WorldMapStyleLayer.FilterArgs m_filterArgs;
        final WorldMapStyleLayer.RenderArgs m_renderArgs;
        final ArrayList<WorldMapRenderLayer> m_renderLayers;
        final ArrayList<WorldMapFeature> m_features;
        final ArrayList<IsoMetaGrid.Zone> m_zones;
        final HashSet<IsoMetaGrid.Zone> m_zoneSet;
        WorldMapStyleLayer.RGBAf m_fill;
        int m_triangulationsThisFrame;
        float[] m_floatArray;
        final Vector2f m_vector2f;
        final TIntArrayList m_rasterizeXY;
        final TIntSet m_rasterizeSet;
        float m_rasterizeMinTileX;
        float m_rasterizeMinTileY;
        float m_rasterizeMaxTileX;
        float m_rasterizeMaxTileY;
        final Rasterize m_rasterize;
        int[] m_rasterizeXY_ints;
        int m_rasterizeMult;
        
        Drawer() {
            this.m_style = new WorldMapStyle();
            this.m_zoom = 0;
            this.m_zoomF = 0.0f;
            this.m_projection = new Matrix4f();
            this.m_modelView = new Matrix4f();
            this.m_playerRenderData = new PlayerRenderData[4];
            this.m_filterArgs = new WorldMapStyleLayer.FilterArgs();
            this.m_renderArgs = new WorldMapStyleLayer.RenderArgs();
            this.m_renderLayers = new ArrayList<WorldMapRenderLayer>();
            this.m_features = new ArrayList<WorldMapFeature>();
            this.m_zones = new ArrayList<IsoMetaGrid.Zone>();
            this.m_zoneSet = new HashSet<IsoMetaGrid.Zone>();
            this.m_triangulationsThisFrame = 0;
            this.m_vector2f = new Vector2f();
            this.m_rasterizeXY = new TIntArrayList();
            this.m_rasterizeSet = (TIntSet)new TIntHashSet();
            this.m_rasterize = new Rasterize();
            this.m_rasterizeMult = 1;
            PZArrayUtil.arrayPopulate(this.m_playerRenderData, PlayerRenderData::new);
        }
        
        void init(final WorldMapRenderer renderer, final UIWorldMap uiWorldMap) {
            this.m_renderer = renderer;
            this.m_style.copyFrom(this.m_renderer.m_style);
            this.m_worldMap = renderer.m_worldMap;
            this.m_x = renderer.m_x;
            this.m_y = renderer.m_y;
            this.m_width = renderer.m_width;
            this.m_height = renderer.m_height;
            this.m_centerWorldX = renderer.m_centerWorldX;
            this.m_centerWorldY = renderer.m_centerWorldY;
            this.m_zoomF = renderer.m_displayZoomF;
            this.m_zoom = (int)this.m_zoomF;
            this.m_worldScale = this.getWorldScale();
            this.m_renderOriginX = (this.m_renderer.m_worldMap.getMinXInSquares() - this.m_centerWorldX) * this.m_worldScale;
            this.m_renderOriginY = (this.m_renderer.m_worldMap.getMinYInSquares() - this.m_centerWorldY) * this.m_worldScale;
            this.m_projection.set((Matrix4fc)renderer.m_projection);
            this.m_modelView.set((Matrix4fc)renderer.m_modelView);
            this.m_fill = uiWorldMap.m_color;
            this.m_triangulationsThisFrame = 0;
            if (this.m_renderer.m_visited != null) {
                this.m_renderer.m_visited.renderMain();
            }
            for (int i = 0; i < 4; ++i) {
                this.m_playerRenderData[i].m_modelSlotRenderData = null;
            }
            if (this.m_renderer.Players.getValue() && this.m_zoomF >= 20.0f) {
                for (int j = 0; j < 4; ++j) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[j];
                    if (isoPlayer != null && !isoPlayer.isDead()) {
                        if (isoPlayer.legsSprite.hasActiveModel()) {
                            float n = isoPlayer.x;
                            float n2 = isoPlayer.y;
                            if (isoPlayer.getVehicle() != null) {
                                n = isoPlayer.getVehicle().getX();
                                n2 = isoPlayer.getVehicle().getY();
                            }
                            final float worldToUIX = this.m_renderer.worldToUIX(n, n2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
                            final float worldToUIY = this.m_renderer.worldToUIY(n, n2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
                            if (worldToUIX >= -100.0f && worldToUIX <= this.m_width + 100 && worldToUIY >= -100.0f) {
                                if (worldToUIY <= this.m_height + 100) {
                                    this.m_playerRenderData[j].m_angle = ((isoPlayer.getVehicle() == null) ? isoPlayer.getAnimationPlayer().getAngle() : 4.712389f);
                                    this.m_playerRenderData[j].m_x = n - this.m_centerWorldX;
                                    this.m_playerRenderData[j].m_y = n2 - this.m_centerWorldY;
                                    isoPlayer.legsSprite.modelSlot.model.updateLights();
                                    final int playerIndex = IsoCamera.frameState.playerIndex;
                                    IsoCamera.frameState.playerIndex = j;
                                    this.m_playerRenderData[j].m_modelSlotRenderData = ModelSlotRenderData.alloc().init(isoPlayer.legsSprite.modelSlot);
                                    this.m_playerRenderData[j].m_modelSlotRenderData.centerOfMassY = 0.0f;
                                    IsoCamera.frameState.playerIndex = playerIndex;
                                    final ModelManager.ModelSlot modelSlot = isoPlayer.legsSprite.modelSlot;
                                    ++modelSlot.renderRefCount;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        public int getAbsoluteX() {
            return this.m_x;
        }
        
        public int getAbsoluteY() {
            return this.m_y;
        }
        
        public int getWidth() {
            return this.m_width;
        }
        
        public int getHeight() {
            return this.m_height;
        }
        
        public float getWorldScale() {
            return this.m_renderer.getWorldScale(this.m_zoomF);
        }
        
        public float uiToWorldX(final float n, final float n2) {
            return this.m_renderer.uiToWorldX(n, n2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
        }
        
        public float uiToWorldY(final float n, final float n2) {
            return this.m_renderer.uiToWorldY(n, n2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
        }
        
        public float worldOriginUIX(final float n) {
            return this.m_renderer.worldOriginUIX(this.m_zoomF, n);
        }
        
        public float worldOriginUIY(final float n) {
            return this.m_renderer.worldOriginUIY(this.m_zoomF, n);
        }
        
        private void renderCellFeatures() {
            for (int i = 0; i < this.m_rasterizeXY.size() - 1; i += 2) {
                final int cellX = this.m_rasterizeXY_ints[i];
                final int cellY = this.m_rasterizeXY_ints[i + 1];
                if (this.m_renderer.m_visited == null || this.m_renderer.m_visited.isCellVisible(cellX, cellY)) {
                    this.m_features.clear();
                    for (int j = 0; j < this.m_worldMap.m_data.size(); ++j) {
                        final WorldMapData worldMapData = this.m_worldMap.m_data.get(j);
                        if (worldMapData.isReady()) {
                            final WorldMapCell cell = worldMapData.getCell(cellX, cellY);
                            if (cell != null) {
                                if (!cell.m_features.isEmpty()) {
                                    this.m_features.addAll(cell.m_features);
                                    if (this.m_worldMap.isLastDataInDirectory(worldMapData)) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (this.m_features.isEmpty()) {
                        this.m_renderArgs.renderer = this.m_renderer;
                        this.m_renderArgs.drawer = this;
                        this.m_renderArgs.cellX = cellX;
                        this.m_renderArgs.cellY = cellY;
                        this.m_renderCellX = this.m_renderOriginX + (cellX * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
                        this.m_renderCellY = this.m_renderOriginY + (cellY * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
                        for (int k = 0; k < this.m_style.m_layers.size(); ++k) {
                            final WorldMapStyleLayer worldMapStyleLayer = this.m_style.m_layers.get(k);
                            if (worldMapStyleLayer instanceof WorldMapTextureStyleLayer) {
                                worldMapStyleLayer.renderCell(this.m_renderArgs);
                            }
                        }
                    }
                    else {
                        this.renderCell(cellX, cellY, this.m_features);
                    }
                }
            }
        }
        
        private void renderCell(final int cellX, final int cellY, final ArrayList<WorldMapFeature> list) {
            this.m_renderCellX = this.m_renderOriginX + (cellX * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
            this.m_renderCellY = this.m_renderOriginY + (cellY * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
            WorldMapRenderLayer.s_pool.release(this.m_renderLayers);
            this.m_renderLayers.clear();
            this.m_filterArgs.renderer = this.m_renderer;
            this.filterFeatures(list, this.m_filterArgs, this.m_renderLayers);
            this.m_renderArgs.renderer = this.m_renderer;
            this.m_renderArgs.drawer = this;
            this.m_renderArgs.cellX = cellX;
            this.m_renderArgs.cellY = cellY;
            for (int i = 0; i < this.m_renderLayers.size(); ++i) {
                final WorldMapRenderLayer worldMapRenderLayer = this.m_renderLayers.get(i);
                worldMapRenderLayer.m_styleLayer.renderCell(this.m_renderArgs);
                for (int j = 0; j < worldMapRenderLayer.m_features.size(); ++j) {
                    worldMapRenderLayer.m_styleLayer.render(worldMapRenderLayer.m_features.get(j), this.m_renderArgs);
                }
            }
        }
        
        void filterFeatures(final ArrayList<WorldMapFeature> list, final WorldMapStyleLayer.FilterArgs filterArgs, final ArrayList<WorldMapRenderLayer> list2) {
            for (int i = 0; i < this.m_style.m_layers.size(); ++i) {
                final WorldMapStyleLayer worldMapStyleLayer = this.m_style.m_layers.get(i);
                if (worldMapStyleLayer.m_minZoom <= this.m_zoomF) {
                    if (worldMapStyleLayer.m_id.equals("mylayer")) {}
                    WorldMapRenderLayer e = null;
                    if (worldMapStyleLayer instanceof WorldMapTextureStyleLayer) {
                        final WorldMapRenderLayer e2 = WorldMapRenderLayer.s_pool.alloc();
                        e2.m_styleLayer = worldMapStyleLayer;
                        e2.m_features.clear();
                        list2.add(e2);
                    }
                    else {
                        for (int j = 0; j < list.size(); ++j) {
                            final WorldMapFeature e3 = list.get(j);
                            if (worldMapStyleLayer.filter(e3, filterArgs)) {
                                if (e == null) {
                                    e = WorldMapRenderLayer.s_pool.alloc();
                                    e.m_styleLayer = worldMapStyleLayer;
                                    e.m_features.clear();
                                    list2.add(e);
                                }
                                e.m_features.add(e3);
                            }
                        }
                    }
                }
            }
        }
        
        void renderCellGrid(final int n, final int n2, final int n3, final int n4) {
            final float n5 = this.m_renderOriginX + (n * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
            final float n6 = this.m_renderOriginY + (n2 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
            final float n7 = n5 + (n3 - n + 1) * 300 * this.m_worldScale;
            final float n8 = n6 + (n4 - n2 + 1) * 300 * this.m_worldScale;
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(1.0f);
            for (int i = n; i <= n3 + 1; ++i) {
                WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (i * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale, n6, 0.0f, this.m_renderOriginX + (i * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale, n8, 0.0f, 0.25f, 0.25f, 0.25f, 1.0f);
            }
            for (int j = n2; j <= n4 + 1; ++j) {
                WorldMapRenderer.m_vboLines.addLine(n5, this.m_renderOriginY + (j * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale, 0.0f, n7, this.m_renderOriginY + (j * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale, 0.0f, 0.25f, 0.25f, 0.25f, 1.0f);
            }
            WorldMapRenderer.m_vboLines.flush();
        }
        
        void renderPlayers() {
            int n = 1;
            for (int i = 0; i < this.m_playerRenderData.length; ++i) {
                final PlayerRenderData playerRenderData = this.m_playerRenderData[i];
                if (playerRenderData.m_modelSlotRenderData != null) {
                    if (n != 0) {
                        GL11.glClear(256);
                        n = 0;
                    }
                    this.m_renderer.m_CharacterModelCamera.m_worldScale = this.m_worldScale;
                    this.m_renderer.m_CharacterModelCamera.m_bUseWorldIso = true;
                    this.m_renderer.m_CharacterModelCamera.m_angle = playerRenderData.m_angle;
                    this.m_renderer.m_CharacterModelCamera.m_playerX = playerRenderData.m_x;
                    this.m_renderer.m_CharacterModelCamera.m_playerY = playerRenderData.m_y;
                    this.m_renderer.m_CharacterModelCamera.m_bVehicle = playerRenderData.m_modelSlotRenderData.bInVehicle;
                    ModelCamera.instance = this.m_renderer.m_CharacterModelCamera;
                    playerRenderData.m_modelSlotRenderData.render();
                }
            }
            if (UIManager.useUIFBO) {
                GL14.glBlendFuncSeparate(770, 771, 1, 771);
            }
        }
        
        public void drawLineStringXXX(final WorldMapStyleLayer.RenderArgs renderArgs, final WorldMapFeature worldMapFeature, final WorldMapStyleLayer.RGBAf rgbAf, final float lineWidth) {
            final float renderCellX = this.m_renderCellX;
            final float renderCellY = this.m_renderCellY;
            final float worldScale = this.m_worldScale;
            final float r = rgbAf.r;
            final float g = rgbAf.g;
            final float b = rgbAf.b;
            final float a = rgbAf.a;
            for (int i = 0; i < worldMapFeature.m_geometries.size(); ++i) {
                final WorldMapGeometry worldMapGeometry = worldMapFeature.m_geometries.get(i);
                switch (worldMapGeometry.m_type) {
                    case LineString: {
                        WorldMapRenderer.m_vboLines.setMode(1);
                        WorldMapRenderer.m_vboLines.setLineWidth(lineWidth);
                        for (int j = 0; j < worldMapGeometry.m_points.size(); ++j) {
                            final WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(j);
                            for (int k = 0; k < worldMapPoints.numPoints() - 1; ++k) {
                                WorldMapRenderer.m_vboLines.addLine(renderCellX + worldMapPoints.getX(k) * worldScale, renderCellY + worldMapPoints.getY(k) * worldScale, 0.0f, renderCellX + worldMapPoints.getX(k + 1) * worldScale, renderCellY + worldMapPoints.getY(k + 1) * worldScale, 0.0f, r, g, b, a);
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        public void drawLineStringYYY(final WorldMapStyleLayer.RenderArgs renderArgs, final WorldMapFeature worldMapFeature, final WorldMapStyleLayer.RGBAf rgbAf, final float width) {
            final float renderCellX = this.m_renderCellX;
            final float renderCellY = this.m_renderCellY;
            final float worldScale = this.m_worldScale;
            final float r = rgbAf.r;
            final float g = rgbAf.g;
            final float b = rgbAf.b;
            final float a = rgbAf.a;
            for (int i = 0; i < worldMapFeature.m_geometries.size(); ++i) {
                final WorldMapGeometry worldMapGeometry = worldMapFeature.m_geometries.get(i);
                switch (worldMapGeometry.m_type) {
                    case LineString: {
                        final StrokeGeometry.Point[] array = new StrokeGeometry.Point[worldMapGeometry.m_points.size()];
                        final WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(0);
                        for (int j = 0; j < worldMapPoints.numPoints(); ++j) {
                            array[j] = StrokeGeometry.newPoint(renderCellX + worldMapPoints.getX(j) * worldScale, renderCellY + worldMapPoints.getY(j) * worldScale);
                        }
                        final StrokeGeometry.Attrs attrs = new StrokeGeometry.Attrs();
                        attrs.join = "miter";
                        attrs.width = width;
                        final ArrayList<StrokeGeometry.Point> strokeGeometry = StrokeGeometry.getStrokeGeometry(array, attrs);
                        if (strokeGeometry == null) {
                            break;
                        }
                        WorldMapRenderer.m_vboLines.setMode(4);
                        for (int k = 0; k < strokeGeometry.size(); ++k) {
                            WorldMapRenderer.m_vboLines.addElement((float)strokeGeometry.get(k).x, (float)strokeGeometry.get(k).y, 0.0f, r, g, b, a);
                        }
                        StrokeGeometry.release(strokeGeometry);
                        break;
                    }
                }
            }
        }
        
        public void drawLineString(final WorldMapStyleLayer.RenderArgs renderArgs, final WorldMapFeature worldMapFeature, final WorldMapStyleLayer.RGBAf rgbAf, final float n) {
            if (!this.m_renderer.LineString.getValue()) {
                return;
            }
            final float renderCellX = this.m_renderCellX;
            final float renderCellY = this.m_renderCellY;
            final float worldScale = this.m_worldScale;
            final float r = rgbAf.r;
            final float g = rgbAf.g;
            final float b = rgbAf.b;
            final float a = rgbAf.a;
            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            for (int i = 0; i < worldMapFeature.m_geometries.size(); ++i) {
                final WorldMapGeometry worldMapGeometry = worldMapFeature.m_geometries.get(i);
                switch (worldMapGeometry.m_type) {
                    case LineString: {
                        final WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(0);
                        if (this.m_floatArray == null || this.m_floatArray.length < worldMapPoints.numPoints() * 2) {
                            this.m_floatArray = new float[worldMapPoints.numPoints() * 2];
                        }
                        for (int j = 0; j < worldMapPoints.numPoints(); ++j) {
                            final float n2 = (float)worldMapPoints.getX(j);
                            final float n3 = (float)worldMapPoints.getY(j);
                            this.m_floatArray[j * 2] = renderCellX + n2 * worldScale;
                            this.m_floatArray[j * 2 + 1] = renderCellY + n3 * worldScale;
                        }
                        GL13.glActiveTexture(33984);
                        GL11.glDisable(3553);
                        GL11.glEnable(3042);
                        break;
                    }
                }
            }
        }
        
        public void drawLineStringTexture(final WorldMapStyleLayer.RenderArgs renderArgs, final WorldMapFeature worldMapFeature, final WorldMapStyleLayer.RGBAf rgbAf, final float n, final Texture texture) {
            final float renderCellX = this.m_renderCellX;
            final float renderCellY = this.m_renderCellY;
            final float worldScale = this.m_worldScale;
            if (texture == null || !texture.isReady()) {
                return;
            }
            if (texture.getID() == -1) {
                texture.bind();
            }
            for (int i = 0; i < worldMapFeature.m_geometries.size(); ++i) {
                final WorldMapGeometry worldMapGeometry = worldMapFeature.m_geometries.get(i);
                if (worldMapGeometry.m_type == WorldMapGeometry.Type.LineString) {
                    WorldMapRenderer.m_vboLinesUV.setMode(7);
                    WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
                    final WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(0);
                    for (int j = 0; j < worldMapPoints.numPoints() - 1; ++j) {
                        final float n2 = renderCellX + worldMapPoints.getX(j) * worldScale;
                        final float n3 = renderCellY + worldMapPoints.getY(j) * worldScale;
                        final float n4 = renderCellX + worldMapPoints.getX(j + 1) * worldScale;
                        final float n5 = renderCellY + worldMapPoints.getY(j + 1) * worldScale;
                        final Vector2f set = this.m_vector2f.set(n5 - n3, -(n4 - n2));
                        set.normalize();
                        final float n6 = n2 + set.x * n / 2.0f;
                        final float n7 = n3 + set.y * n / 2.0f;
                        final float n8 = n4 + set.x * n / 2.0f;
                        final float n9 = n5 + set.y * n / 2.0f;
                        final float n10 = n4 - set.x * n / 2.0f;
                        final float n11 = n5 - set.y * n / 2.0f;
                        final float n12 = n2 - set.x * n / 2.0f;
                        final float n13 = n3 - set.y * n / 2.0f;
                        final float length = Vector2f.length(n4 - n2, n5 - n3);
                        WorldMapRenderer.m_vboLinesUV.addQuad(n6, n7, 0.0f, length / (n * (texture.getHeight() / (float)texture.getWidth())), n8, n9, 0.0f, 0.0f, n10, n11, 1.0f, 0.0f, n12, n13, 1.0f, length / (n * (texture.getHeight() / (float)texture.getWidth())), 0.0f, rgbAf.r, rgbAf.g, rgbAf.b, rgbAf.a);
                    }
                }
            }
        }
        
        public void fillPolygon(final WorldMapStyleLayer.RenderArgs renderArgs, final WorldMapFeature worldMapFeature, final WorldMapStyleLayer.RGBAf rgbAf) {
            WorldMapRenderer.m_vboLinesUV.flush();
            final float renderCellX = this.m_renderCellX;
            final float renderCellY = this.m_renderCellY;
            final float worldScale = this.m_worldScale;
            float r = rgbAf.r;
            float g = rgbAf.g;
            float b = rgbAf.b;
            final float a = rgbAf.a;
            for (int i = 0; i < worldMapFeature.m_geometries.size(); ++i) {
                final WorldMapGeometry worldMapGeometry = worldMapFeature.m_geometries.get(i);
                if (worldMapGeometry.m_type == WorldMapGeometry.Type.Polygon) {
                    final boolean b2 = false;
                    if (worldMapGeometry.m_triangles == null) {
                        if (this.m_triangulationsThisFrame > 500) {
                            continue;
                        }
                        ++this.m_triangulationsThisFrame;
                        worldMapGeometry.triangulate((double[])(worldMapFeature.m_properties.containsKey("highway") ? new double[] { 1.0, 2.0, 4.0, 8.0, 12.0, 18.0 } : null));
                        if (worldMapGeometry.m_triangles == null) {
                            if (Core.bDebug) {
                                WorldMapRenderer.m_vboLines.setMode(1);
                                r = 1.0f;
                                b = (g = 0.0f);
                                WorldMapRenderer.m_vboLines.setLineWidth(4.0f);
                                for (int j = 0; j < worldMapGeometry.m_points.size(); ++j) {
                                    final WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(j);
                                    for (int k = 0; k < worldMapPoints.numPoints(); ++k) {
                                        final int x = worldMapPoints.getX(k);
                                        final int y = worldMapPoints.getY(k);
                                        final int x2 = worldMapPoints.getX((k + 1) % worldMapPoints.numPoints());
                                        final int y2 = worldMapPoints.getY((k + 1) % worldMapPoints.numPoints());
                                        WorldMapRenderer.m_vboLines.reserve(2);
                                        WorldMapRenderer.m_vboLines.addElement(renderCellX + x * worldScale, renderCellY + y * worldScale, 0.0f, r, g, b, a);
                                        WorldMapRenderer.m_vboLines.addElement(renderCellX + x2 * worldScale, renderCellY + y2 * worldScale, 0.0f, r, g, b, a);
                                    }
                                }
                                WorldMapRenderer.m_vboLines.setLineWidth(1.0f);
                            }
                            continue;
                        }
                        else if (b2) {
                            this.uploadTrianglesToVBO(worldMapGeometry);
                        }
                    }
                    if (b2) {
                        GL11.glTranslatef(renderCellX, renderCellY, 0.0f);
                        GL11.glScalef(worldScale, worldScale, worldScale);
                        GL11.glColor4f(r, g, b, a);
                        if (worldMapGeometry.m_triangles.length / 2 > 2340) {
                            final int min = PZMath.min(worldMapGeometry.m_triangles.length / 2, 2340);
                            WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex1, worldMapGeometry.m_vboIndex2, min);
                            WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex3, worldMapGeometry.m_vboIndex4, worldMapGeometry.m_triangles.length / 2 - min);
                        }
                        else {
                            WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex1, worldMapGeometry.m_vboIndex2, worldMapGeometry.m_triangles.length / 2);
                        }
                        GL11.glScalef(1.0f / worldScale, 1.0f / worldScale, 1.0f / worldScale);
                        GL11.glTranslatef(-renderCellX, -renderCellY, 0.0f);
                    }
                    else {
                        WorldMapRenderer.m_vboLines.setMode(4);
                        double n = 0.0;
                        if (this.m_zoomF <= 11.5) {
                            n = 18.0;
                        }
                        else if (this.m_zoomF <= 12.0) {
                            n = 12.0;
                        }
                        else if (this.m_zoomF <= 12.5) {
                            n = 8.0;
                        }
                        else if (this.m_zoomF <= 13.0) {
                            n = 4.0;
                        }
                        else if (this.m_zoomF <= 13.5) {
                            n = 2.0;
                        }
                        else if (this.m_zoomF <= 14.0) {
                            n = 1.0;
                        }
                        final WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = (n == 0.0) ? null : worldMapGeometry.findTriangles(n);
                        if (trianglesPerZoom != null) {
                            final float[] triangles = trianglesPerZoom.m_triangles;
                            for (int l = 0; l < triangles.length; l += 6) {
                                final float n2 = triangles[l];
                                final float n3 = triangles[l + 1];
                                final float n4 = triangles[l + 2];
                                final float n5 = triangles[l + 3];
                                final float n6 = triangles[l + 4];
                                final float n7 = triangles[l + 5];
                                WorldMapRenderer.m_vboLines.reserve(3);
                                final float n8 = 1.0f;
                                final float n9 = a;
                                WorldMapRenderer.m_vboLines.addElement(renderCellX + n2 * worldScale, renderCellY + n3 * worldScale, 0.0f, r * n8, g * n8, b * n8, n9);
                                WorldMapRenderer.m_vboLines.addElement(renderCellX + n4 * worldScale, renderCellY + n5 * worldScale, 0.0f, r * n8, g * n8, b * n8, n9);
                                WorldMapRenderer.m_vboLines.addElement(renderCellX + n6 * worldScale, renderCellY + n7 * worldScale, 0.0f, r * n8, g * n8, b * n8, n9);
                            }
                        }
                        else {
                            final float[] triangles2 = worldMapGeometry.m_triangles;
                            for (int n10 = 0; n10 < triangles2.length; n10 += 6) {
                                final float n11 = triangles2[n10];
                                final float n12 = triangles2[n10 + 1];
                                final float n13 = triangles2[n10 + 2];
                                final float n14 = triangles2[n10 + 3];
                                final float n15 = triangles2[n10 + 4];
                                final float n16 = triangles2[n10 + 5];
                                WorldMapRenderer.m_vboLines.reserve(3);
                                WorldMapRenderer.m_vboLines.addElement(renderCellX + n11 * worldScale, renderCellY + n12 * worldScale, 0.0f, r, g, b, a);
                                WorldMapRenderer.m_vboLines.addElement(renderCellX + n13 * worldScale, renderCellY + n14 * worldScale, 0.0f, r, g, b, a);
                                WorldMapRenderer.m_vboLines.addElement(renderCellX + n15 * worldScale, renderCellY + n16 * worldScale, 0.0f, r, g, b, a);
                            }
                        }
                    }
                }
            }
        }
        
        public void fillPolygon(final WorldMapStyleLayer.RenderArgs renderArgs, final WorldMapFeature worldMapFeature, final WorldMapStyleLayer.RGBAf rgbAf, final Texture texture, final float n) {
            WorldMapRenderer.m_vboLines.flush();
            final float renderCellX = this.m_renderCellX;
            final float renderCellY = this.m_renderCellY;
            final float worldScale = this.m_worldScale;
            final float r = rgbAf.r;
            final float g = rgbAf.g;
            final float b = rgbAf.b;
            final float a = rgbAf.a;
            for (int i = 0; i < worldMapFeature.m_geometries.size(); ++i) {
                final WorldMapGeometry worldMapGeometry = worldMapFeature.m_geometries.get(i);
                if (worldMapGeometry.m_type == WorldMapGeometry.Type.Polygon) {
                    if (worldMapGeometry.m_triangles == null) {
                        worldMapGeometry.triangulate(null);
                        if (worldMapGeometry.m_triangles == null) {
                            continue;
                        }
                    }
                    GL11.glEnable(3553);
                    GL11.glTexParameteri(3553, 10241, 9728);
                    GL11.glTexParameteri(3553, 10240, 9728);
                    WorldMapRenderer.m_vboLinesUV.setMode(4);
                    WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
                    final float[] triangles = worldMapGeometry.m_triangles;
                    final float n2 = (float)(renderArgs.cellX * 300 + worldMapGeometry.m_minX);
                    final float n3 = (float)(renderArgs.cellY * 300 + worldMapGeometry.m_minY);
                    final float n4 = texture.getWidth() * n;
                    final float n5 = texture.getHeight() * n;
                    final float n6 = (float)texture.getWidthHW();
                    final float n7 = (float)texture.getHeightHW();
                    final float n8 = PZMath.floor(n2 / n4) * n4;
                    final float n9 = PZMath.floor(n3 / n5) * n5;
                    for (int j = 0; j < triangles.length; j += 6) {
                        final float n10 = triangles[j];
                        final float n11 = triangles[j + 1];
                        final float n12 = triangles[j + 2];
                        final float n13 = triangles[j + 3];
                        final float n14 = triangles[j + 4];
                        final float n15 = triangles[j + 5];
                        final float n16 = (n10 + renderArgs.cellX * 300 - n8) / n;
                        final float n17 = (n11 + renderArgs.cellY * 300 - n9) / n;
                        final float n18 = (n12 + renderArgs.cellX * 300 - n8) / n;
                        final float n19 = (n13 + renderArgs.cellY * 300 - n9) / n;
                        final float n20 = (n14 + renderArgs.cellX * 300 - n8) / n;
                        final float n21 = (n15 + renderArgs.cellY * 300 - n9) / n;
                        final float n22 = renderCellX + n10 * worldScale;
                        final float n23 = renderCellY + n11 * worldScale;
                        final float n24 = renderCellX + n12 * worldScale;
                        final float n25 = renderCellY + n13 * worldScale;
                        final float n26 = renderCellX + n14 * worldScale;
                        final float n27 = renderCellY + n15 * worldScale;
                        final float n28 = n16 / n6;
                        final float n29 = n17 / n7;
                        final float n30 = n18 / n6;
                        final float n31 = n19 / n7;
                        final float n32 = n20 / n6;
                        final float n33 = n21 / n7;
                        WorldMapRenderer.m_vboLinesUV.reserve(3);
                        WorldMapRenderer.m_vboLinesUV.addElement(n22, n23, 0.0f, n28, n29, r, g, b, a);
                        WorldMapRenderer.m_vboLinesUV.addElement(n24, n25, 0.0f, n30, n31, r, g, b, a);
                        WorldMapRenderer.m_vboLinesUV.addElement(n26, n27, 0.0f, n32, n33, r, g, b, a);
                    }
                    GL11.glDisable(3553);
                }
            }
        }
        
        void uploadTrianglesToVBO(final WorldMapGeometry worldMapGeometry) {
            final int[] array = new int[2];
            int i = worldMapGeometry.m_triangles.length / 2;
            if (i > 2340) {
                int n = 0;
                while (i > 0) {
                    final int min = PZMath.min(i / 3, 780);
                    WorldMapVBOs.getInstance().reserveVertices(min * 3, array);
                    if (worldMapGeometry.m_vboIndex1 == -1) {
                        worldMapGeometry.m_vboIndex1 = array[0];
                        worldMapGeometry.m_vboIndex2 = array[1];
                    }
                    else {
                        worldMapGeometry.m_vboIndex3 = array[0];
                        worldMapGeometry.m_vboIndex4 = array[1];
                    }
                    final float[] triangles = worldMapGeometry.m_triangles;
                    for (int j = n * 3 * 2; j < (n + min) * 3 * 2; j += 6) {
                        final float n2 = triangles[j];
                        final float n3 = triangles[j + 1];
                        final float n4 = triangles[j + 2];
                        final float n5 = triangles[j + 3];
                        final float n6 = triangles[j + 4];
                        final float n7 = triangles[j + 5];
                        WorldMapVBOs.getInstance().addElement(n2, n3, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
                        WorldMapVBOs.getInstance().addElement(n4, n5, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
                        WorldMapVBOs.getInstance().addElement(n6, n7, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    n += min;
                    i -= min * 3;
                }
            }
            else {
                WorldMapVBOs.getInstance().reserveVertices(i, array);
                worldMapGeometry.m_vboIndex1 = array[0];
                worldMapGeometry.m_vboIndex2 = array[1];
                final float[] triangles2 = worldMapGeometry.m_triangles;
                for (int k = 0; k < triangles2.length; k += 6) {
                    final float n8 = triangles2[k];
                    final float n9 = triangles2[k + 1];
                    final float n10 = triangles2[k + 2];
                    final float n11 = triangles2[k + 3];
                    final float n12 = triangles2[k + 4];
                    final float n13 = triangles2[k + 5];
                    WorldMapVBOs.getInstance().addElement(n8, n9, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
                    WorldMapVBOs.getInstance().addElement(n10, n11, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
                    WorldMapVBOs.getInstance().addElement(n12, n13, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        
        void outlineTriangles(final WorldMapGeometry worldMapGeometry, final float n, final float n2, final float n3) {
            WorldMapRenderer.m_vboLines.setMode(1);
            final float n4 = 1.0f;
            final float n5 = 1.0f;
            final float n7;
            final float n6 = n7 = 0.0f;
            final float[] triangles = worldMapGeometry.m_triangles;
            for (int i = 0; i < triangles.length; i += 6) {
                final float n8 = triangles[i];
                final float n9 = triangles[i + 1];
                final float n10 = triangles[i + 2];
                final float n11 = triangles[i + 3];
                final float n12 = triangles[i + 4];
                final float n13 = triangles[i + 5];
                WorldMapRenderer.m_vboLines.addElement(n + n8 * n3, n2 + n9 * n3, 0.0f, n5, n7, n6, n4);
                WorldMapRenderer.m_vboLines.addElement(n + n10 * n3, n2 + n11 * n3, 0.0f, n5, n7, n6, n4);
                WorldMapRenderer.m_vboLines.addElement(n + n10 * n3, n2 + n11 * n3, 0.0f, n5, n7, n6, n4);
                WorldMapRenderer.m_vboLines.addElement(n + n12 * n3, n2 + n13 * n3, 0.0f, n5, n7, n6, n4);
                WorldMapRenderer.m_vboLines.addElement(n + n12 * n3, n2 + n13 * n3, 0.0f, n5, n7, n6, n4);
                WorldMapRenderer.m_vboLines.addElement(n + n8 * n3, n2 + n9 * n3, 0.0f, n5, n7, n6, n4);
            }
        }
        
        void outlinePolygon(final WorldMapGeometry worldMapGeometry, final float n, final float n2, final float n3) {
            WorldMapRenderer.m_vboLines.setMode(1);
            final float n4 = 1.0f;
            final float n7;
            final float n6;
            final float n5 = n6 = (n7 = 0.8f);
            WorldMapRenderer.m_vboLines.setLineWidth(4.0f);
            for (int i = 0; i < worldMapGeometry.m_points.size(); ++i) {
                final WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(i);
                for (int j = 0; j < worldMapPoints.numPoints(); ++j) {
                    final int x = worldMapPoints.getX(j);
                    final int y = worldMapPoints.getY(j);
                    final int x2 = worldMapPoints.getX((j + 1) % worldMapPoints.numPoints());
                    final int y2 = worldMapPoints.getY((j + 1) % worldMapPoints.numPoints());
                    WorldMapRenderer.m_vboLines.addElement(n + x * n3, n2 + y * n3, 0.0f, n6, n5, n7, n4);
                    WorldMapRenderer.m_vboLines.addElement(n + x2 * n3, n2 + y2 * n3, 0.0f, n6, n5, n7, n4);
                }
            }
            WorldMapRenderer.m_vboLines.setLineWidth(1.0f);
        }
        
        public void drawTexture(final Texture texture, final WorldMapStyleLayer.RGBAf rgbAf, final int n, final int n2, final int n3, final int n4) {
            if (texture == null || !texture.isReady()) {
                return;
            }
            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            final float worldScale = this.m_worldScale;
            final float n5 = (n - this.m_centerWorldX) * worldScale;
            final float n6 = (n2 - this.m_centerWorldY) * worldScale;
            final float n7 = n5 + (n3 - n) * worldScale;
            final float n8 = n6 + (n4 - n2) * worldScale;
            final float clamp = PZMath.clamp(n5, this.m_renderCellX, this.m_renderCellX + 300.0f * worldScale);
            final float clamp2 = PZMath.clamp(n6, this.m_renderCellY, this.m_renderCellY + 300.0f * worldScale);
            final float clamp3 = PZMath.clamp(n7, this.m_renderCellX, this.m_renderCellX + 300.0f * worldScale);
            final float clamp4 = PZMath.clamp(n8, this.m_renderCellY, this.m_renderCellY + 300.0f * worldScale);
            if (clamp >= clamp3 || clamp2 >= clamp4) {
                return;
            }
            final float n9 = texture.getWidth() / (float)(n3 - n);
            final float n10 = texture.getHeight() / (float)(n4 - n2);
            GL11.glEnable(3553);
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            if (texture.getID() == -1) {
                texture.bind();
            }
            else {
                GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
            }
            final float n11 = (clamp - n5) / (texture.getWidthHW() * worldScale) * n9;
            final float n12 = (clamp2 - n6) / (texture.getHeightHW() * worldScale) * n10;
            final float n13 = (clamp3 - n5) / (texture.getWidthHW() * worldScale) * n9;
            final float n14 = (clamp4 - n6) / (texture.getHeightHW() * worldScale) * n10;
            WorldMapRenderer.m_vboLinesUV.setMode(7);
            WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
            WorldMapRenderer.m_vboLinesUV.addQuad(clamp, clamp2, n11, n12, clamp3, clamp4, n13, n14, 0.0f, rgbAf.r, rgbAf.g, rgbAf.b, rgbAf.a);
        }
        
        public void drawTextureTiled(final Texture texture, final WorldMapStyleLayer.RGBAf rgbAf, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            if (texture == null || !texture.isReady()) {
                return;
            }
            if (n5 * 300 >= n3 || (n5 + 1) * 300 <= n) {
                return;
            }
            if (n6 * 300 >= n4 || (n6 + 1) * 300 <= n2) {
                return;
            }
            WorldMapRenderer.m_vboLines.flush();
            final float worldScale = this.m_worldScale;
            final int width = texture.getWidth();
            final int height = texture.getHeight();
            final int n7 = (int)(PZMath.floor(n5 * 300.0f / width) * width);
            final int n8 = (int)(PZMath.floor(n6 * 300.0f / height) * height);
            final int n9 = n7 + (int)Math.ceil(((n5 + 1) * 300.0f - n7) / width) * width;
            final int n10 = n8 + (int)Math.ceil(((n6 + 1) * 300.0f - n8) / height) * height;
            final float n11 = (float)PZMath.clamp(n7, n5 * 300, (n5 + 1) * 300);
            final float n12 = (float)PZMath.clamp(n8, n6 * 300, (n6 + 1) * 300);
            final float n13 = (float)PZMath.clamp(n9, n5 * 300, (n5 + 1) * 300);
            final float n14 = (float)PZMath.clamp(n10, n6 * 300, (n6 + 1) * 300);
            final float clamp = PZMath.clamp(n11, (float)n, (float)n3);
            final float clamp2 = PZMath.clamp(n12, (float)n2, (float)n4);
            final float clamp3 = PZMath.clamp(n13, (float)n, (float)n3);
            final float clamp4 = PZMath.clamp(n14, (float)n2, (float)n4);
            final float n15 = (clamp - n) / width;
            final float n16 = (clamp2 - n2) / height;
            final float n17 = (clamp3 - n) / width;
            final float n18 = (clamp4 - n2) / height;
            final float n19 = (clamp - this.m_centerWorldX) * worldScale;
            final float n20 = (clamp2 - this.m_centerWorldY) * worldScale;
            final float n21 = (clamp3 - this.m_centerWorldX) * worldScale;
            final float n22 = (clamp4 - this.m_centerWorldY) * worldScale;
            final float n23 = n15 * texture.xEnd;
            final float n24 = n16 * texture.yEnd;
            final float n25 = (int)n17 + (n17 - (int)n17) * texture.xEnd;
            final float n26 = (int)n18 + (n18 - (int)n18) * texture.yEnd;
            GL11.glEnable(3553);
            if (texture.getID() == -1) {
                texture.bind();
            }
            else {
                GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexParameteri(3553, 10242, 10497);
                GL11.glTexParameteri(3553, 10243, 10497);
            }
            WorldMapRenderer.m_vboLinesUV.setMode(7);
            WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
            WorldMapRenderer.m_vboLinesUV.addQuad(n19, n20, n23, n24, n21, n22, n25, n26, 0.0f, rgbAf.r, rgbAf.g, rgbAf.b, rgbAf.a);
            GL11.glDisable(3553);
        }
        
        public void drawTextureTiled(final Texture texture, final WorldMapStyleLayer.RGBAf rgbAf, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
            if (texture == null || !texture.isReady()) {
                return;
            }
            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            final float worldScale = this.m_worldScale;
            final float n9 = (float)n;
            final float n10 = (float)n2;
            final float n11 = (float)n3;
            final float n12 = (float)n4;
            final float clamp = PZMath.clamp(n9, (float)(n7 * 300), (float)((n7 + 1) * 300));
            final float clamp2 = PZMath.clamp(n10, (float)(n8 * 300), (float)((n8 + 1) * 300));
            final float clamp3 = PZMath.clamp(n11, (float)(n7 * 300), (float)((n7 + 1) * 300));
            final float clamp4 = PZMath.clamp(n12, (float)(n8 * 300), (float)((n8 + 1) * 300));
            final float n13 = (clamp - n) / n5;
            final float n14 = (clamp2 - n2) / n6;
            final float n15 = (clamp3 - n) / n5;
            final float n16 = (clamp4 - n2) / n6;
            final float n17 = (clamp - this.m_centerWorldX) * worldScale;
            final float n18 = (clamp2 - this.m_centerWorldY) * worldScale;
            final float n19 = (clamp3 - this.m_centerWorldX) * worldScale;
            final float n20 = (clamp4 - this.m_centerWorldY) * worldScale;
            final float n21 = n13 * texture.xEnd;
            final float n22 = n14 * texture.yEnd;
            final float n23 = (int)n15 + (n15 - (int)n15) * texture.xEnd;
            final float n24 = (int)n16 + (n16 - (int)n16) * texture.yEnd;
            GL11.glEnable(3553);
            if (texture.getID() == -1) {
                texture.bind();
            }
            else {
                GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexParameteri(3553, 10242, 10497);
                GL11.glTexParameteri(3553, 10243, 10497);
            }
            GL11.glColor4f(rgbAf.r, rgbAf.g, rgbAf.b, rgbAf.a);
            GL11.glBegin(7);
            GL11.glTexCoord2f(n21, n22);
            GL11.glVertex2f(n17, n18);
            GL11.glTexCoord2f(n23, n22);
            GL11.glVertex2f(n19, n18);
            GL11.glTexCoord2f(n23, n24);
            GL11.glVertex2f(n19, n20);
            GL11.glTexCoord2f(n21, n24);
            GL11.glVertex2f(n17, n20);
            GL11.glEnd();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(3553);
        }
        
        void renderZones() {
            this.m_zoneSet.clear();
            for (int i = 0; i < this.m_rasterizeXY.size() - 1; i += 2) {
                final int n = this.m_rasterizeXY_ints[i];
                final int n2 = this.m_rasterizeXY_ints[i + 1];
                if (this.m_renderer.m_visited == null || this.m_renderer.m_visited.isCellVisible(n, n2)) {
                    final IsoMetaCell cellData = IsoWorld.instance.MetaGrid.getCellData(n, n2);
                    if (cellData != null) {
                        cellData.getZonesUnique(this.m_zoneSet);
                    }
                }
            }
            this.m_zones.clear();
            this.m_zones.addAll(this.m_zoneSet);
            this.renderZones(this.m_zones, "Forest", 0.0f, 1.0f, 0.0f, 0.25f);
            this.renderZones(this.m_zones, "DeepForest", 0.0f, 0.5f, 0.0f, 0.25f);
            this.renderZones(this.m_zones, "Nav", 0.0f, 0.0f, 1.0f, 0.25f);
        }
        
        void renderZones(final ArrayList<IsoMetaGrid.Zone> list, final String s, final float n, final float n2, final float n3, final float n4) {
            WorldMapRenderer.m_vboLinesUV.flush();
            final float worldScale = this.m_worldScale;
            WorldMapRenderer.m_vboLines.setMode(4);
            for (final IsoMetaGrid.Zone zone : list) {
                if (!s.equals(zone.type)) {
                    continue;
                }
                if (zone.isRectangle()) {
                    WorldMapRenderer.m_vboLines.addQuad((zone.x - this.m_centerWorldX) * worldScale, (zone.y - this.m_centerWorldY) * worldScale, (zone.x + zone.w - this.m_centerWorldX) * worldScale, (zone.y + zone.h - this.m_centerWorldY) * worldScale, 0.0f, n, n2, n3, n4);
                }
                if (zone.isPolygon()) {
                    final float[] polygonTriangles = zone.getPolygonTriangles();
                    if (polygonTriangles == null) {
                        continue;
                    }
                    for (int i = 0; i < polygonTriangles.length; i += 6) {
                        WorldMapRenderer.m_vboLines.addTriangle((polygonTriangles[i] - this.m_centerWorldX) * worldScale, (polygonTriangles[i + 1] - this.m_centerWorldY) * worldScale, 0.0f, (polygonTriangles[i + 2] - this.m_centerWorldX) * worldScale, (polygonTriangles[i + 3] - this.m_centerWorldY) * worldScale, 0.0f, (polygonTriangles[i + 4] - this.m_centerWorldX) * worldScale, (polygonTriangles[i + 5] - this.m_centerWorldY) * worldScale, 0.0f, n, n2, n3, n4);
                    }
                }
                if (!zone.isPolyline()) {
                    continue;
                }
                final float[] polylineOutlineTriangles = zone.getPolylineOutlineTriangles();
                if (polylineOutlineTriangles == null) {
                    continue;
                }
                for (int j = 0; j < polylineOutlineTriangles.length; j += 6) {
                    WorldMapRenderer.m_vboLines.addTriangle((polylineOutlineTriangles[j] - this.m_centerWorldX) * worldScale, (polylineOutlineTriangles[j + 1] - this.m_centerWorldY) * worldScale, 0.0f, (polylineOutlineTriangles[j + 2] - this.m_centerWorldX) * worldScale, (polylineOutlineTriangles[j + 3] - this.m_centerWorldY) * worldScale, 0.0f, (polylineOutlineTriangles[j + 4] - this.m_centerWorldX) * worldScale, (polylineOutlineTriangles[j + 5] - this.m_centerWorldY) * worldScale, 0.0f, n, n2, n3, n4);
                }
            }
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(2.0f);
            for (final IsoMetaGrid.Zone zone2 : list) {
                if (!s.equals(zone2.type)) {
                    continue;
                }
                if (zone2.isRectangle()) {
                    final float n5 = (zone2.x - this.m_centerWorldX) * worldScale;
                    final float n6 = (zone2.y - this.m_centerWorldY) * worldScale;
                    final float n7 = (zone2.x + zone2.w - this.m_centerWorldX) * worldScale;
                    final float n8 = (zone2.y + zone2.h - this.m_centerWorldY) * worldScale;
                    WorldMapRenderer.m_vboLines.addLine(n5, n6, 0.0f, n7, n6, 0.0f, n, n2, n3, 1.0f);
                    WorldMapRenderer.m_vboLines.addLine(n7, n6, 0.0f, n7, n8, 0.0f, n, n2, n3, 1.0f);
                    WorldMapRenderer.m_vboLines.addLine(n7, n8, 0.0f, n5, n8, 0.0f, n, n2, n3, 1.0f);
                    WorldMapRenderer.m_vboLines.addLine(n5, n8, 0.0f, n5, n6, 0.0f, n, n2, n3, 1.0f);
                }
                if (zone2.isPolygon()) {
                    for (int k = 0; k < zone2.points.size(); k += 2) {
                        WorldMapRenderer.m_vboLines.addLine((zone2.points.getQuick(k) - this.m_centerWorldX) * worldScale, (zone2.points.getQuick(k + 1) - this.m_centerWorldY) * worldScale, 0.0f, (zone2.points.getQuick((k + 2) % zone2.points.size()) - this.m_centerWorldX) * worldScale, (zone2.points.getQuick((k + 3) % zone2.points.size()) - this.m_centerWorldY) * worldScale, 0.0f, n, n2, n3, 1.0f);
                    }
                }
                if (!zone2.isPolyline()) {
                    continue;
                }
                final float[] polylineOutlinePoints = zone2.polylineOutlinePoints;
                if (polylineOutlinePoints == null) {
                    continue;
                }
                for (int l = 0; l < polylineOutlinePoints.length; l += 2) {
                    WorldMapRenderer.m_vboLines.addLine((polylineOutlinePoints[l] - this.m_centerWorldX) * worldScale, (polylineOutlinePoints[l + 1] - this.m_centerWorldY) * worldScale, 0.0f, (polylineOutlinePoints[(l + 2) % polylineOutlinePoints.length] - this.m_centerWorldX) * worldScale, (polylineOutlinePoints[(l + 3) % polylineOutlinePoints.length] - this.m_centerWorldY) * worldScale, 0.0f, n, n2, n3, 1.0f);
                }
            }
        }
        
        @Override
        public void render() {
            try {
                PZGLUtil.pushAndLoadMatrix(5889, this.m_projection);
                PZGLUtil.pushAndLoadMatrix(5888, this.m_modelView);
                this.renderInternal();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
            finally {
                PZGLUtil.popMatrix(5889);
                PZGLUtil.popMatrix(5888);
            }
        }
        
        private void renderInternal() {
            final float worldScale = this.m_worldScale;
            final int n = (int)Math.max(this.uiToWorldX(0.0f, 0.0f), (float)this.m_worldMap.getMinXInSquares()) / 300;
            final int n2 = (int)Math.max(this.uiToWorldY(0.0f, 0.0f), (float)this.m_worldMap.getMinYInSquares()) / 300;
            final int n3 = (int)Math.min(this.uiToWorldX((float)this.getWidth(), (float)this.getHeight()), (float)(this.m_worldMap.m_maxX * 300)) / 300;
            final int n4 = (int)Math.min(this.uiToWorldY((float)this.getWidth(), (float)this.getHeight()), (float)(this.m_worldMap.m_maxY * 300)) / 300;
            final int minXInSquares = this.m_worldMap.getMinXInSquares();
            final int minYInSquares = this.m_worldMap.getMinYInSquares();
            final int maxX = this.m_worldMap.m_maxX;
            final int maxY = this.m_worldMap.m_maxY;
            GL11.glViewport(this.m_x, Core.height - this.m_height - this.m_y, this.m_width, this.m_height);
            GLVertexBufferObject.funcs.glBindBuffer(GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), 0);
            GLVertexBufferObject.funcs.glBindBuffer(GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), 0);
            GL11.glPolygonMode(1032, this.m_renderer.Wireframe.getValue() ? 6913 : 6914);
            if (this.m_renderer.ImagePyramid.getValue()) {
                this.renderImagePyramids();
            }
            this.calculateVisibleCells();
            if (this.m_renderer.Features.getValue()) {
                this.renderCellFeatures();
            }
            if (this.m_renderer.ForestZones.getValue()) {
                this.renderZones();
            }
            if (this.m_renderer.VisibleCells.getValue()) {
                this.renderVisibleCells();
            }
            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            GL11.glEnableClientState(32884);
            GL11.glEnableClientState(32886);
            GL13.glActiveTexture(33984);
            GL13.glClientActiveTexture(33984);
            GL11.glEnableClientState(32888);
            GL11.glTexEnvi(8960, 8704, 8448);
            GL11.glPolygonMode(1032, 6914);
            GL11.glEnable(3042);
            SpriteRenderer.ringBuffer.restoreBoundTextures = true;
            SpriteRenderer.ringBuffer.restoreVBOs = true;
            if (this.m_renderer.m_visited != null) {
                this.m_renderer.m_visited.render(this.m_renderOriginX - (this.m_worldMap.getMinXInSquares() - this.m_renderer.m_visited.getMinX() * 300) * worldScale, this.m_renderOriginY - (this.m_worldMap.getMinYInSquares() - this.m_renderer.m_visited.getMinY() * 300) * worldScale, minXInSquares / 300, minYInSquares / 300, maxX / 300, maxY / 300, worldScale, this.m_renderer.BlurUnvisited.getValue());
                if (this.m_renderer.UnvisitedGrid.getValue()) {
                    this.m_renderer.m_visited.renderGrid(this.m_renderOriginX - (this.m_worldMap.getMinXInSquares() - this.m_renderer.m_visited.getMinX() * 300) * worldScale, this.m_renderOriginY - (this.m_worldMap.getMinYInSquares() - this.m_renderer.m_visited.getMinY() * 300) * worldScale, minXInSquares / 300, minYInSquares / 300, maxX / 300, maxY / 300, worldScale, this.m_zoomF);
                }
            }
            this.renderPlayers();
            if (this.m_renderer.CellGrid.getValue()) {
                this.renderCellGrid(minXInSquares / 300, minYInSquares / 300, maxX / 300, maxY / 300);
            }
            if (Core.bDebug) {}
            this.paintAreasOutsideBounds(minXInSquares, minYInSquares, maxX, maxY, worldScale);
            if (this.m_renderer.WorldBounds.getValue()) {
                this.renderWorldBounds();
            }
            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            GL11.glViewport(0, 0, Core.width, Core.height);
        }
        
        private void rasterizeCellsCallback(final int n, final int n2) {
            final int n3 = n + n2 * this.m_worldMap.getWidthInCells();
            if (this.m_rasterizeSet.contains(n3)) {
                return;
            }
            for (int i = n2 * this.m_rasterizeMult; i < n2 * this.m_rasterizeMult + this.m_rasterizeMult; ++i) {
                for (int j = n * this.m_rasterizeMult; j < n * this.m_rasterizeMult + this.m_rasterizeMult; ++j) {
                    if (j >= this.m_worldMap.getMinXInCells() && j <= this.m_worldMap.getMaxXInCells() && i >= this.m_worldMap.getMinYInCells()) {
                        if (i <= this.m_worldMap.getMaxYInCells()) {
                            this.m_rasterizeSet.add(n3);
                            this.m_rasterizeXY.add(j);
                            this.m_rasterizeXY.add(i);
                        }
                    }
                }
            }
        }
        
        private void rasterizeTilesCallback(final int n, final int n2) {
            final int n3 = n + n2 * 1000;
            if (this.m_rasterizeSet.contains(n3)) {
                return;
            }
            if (n < this.m_rasterizeMinTileX || n > this.m_rasterizeMaxTileX || n2 < this.m_rasterizeMinTileY || n2 > this.m_rasterizeMaxTileY) {
                return;
            }
            this.m_rasterizeSet.add(n3);
            this.m_rasterizeXY.add(n);
            this.m_rasterizeXY.add(n2);
        }
        
        private void calculateVisibleCells() {
            final int n = (Core.bDebug && this.m_renderer.VisibleCells.getValue()) ? 200 : 0;
            if (1.0f / this.m_worldScale > 100.0f) {
                this.m_rasterizeXY.clear();
                for (int i = this.m_worldMap.getMinYInCells(); i <= this.m_worldMap.getMaxYInCells(); ++i) {
                    for (int j = this.m_worldMap.getMinXInCells(); j <= this.m_worldMap.getMaxYInCells(); ++j) {
                        this.m_rasterizeXY.add(j);
                        this.m_rasterizeXY.add(i);
                    }
                }
                if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
                    this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
                }
                this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
                return;
            }
            float n2;
            float n3;
            float n4;
            float n5;
            float n6;
            float n7;
            float n8;
            float n9;
            int rasterizeMult;
            for (n2 = this.uiToWorldX(n + 0.0f, n + 0.0f) / 300.0f, n3 = this.uiToWorldY(n + 0.0f, n + 0.0f) / 300.0f, n4 = this.uiToWorldX((float)(this.getWidth() - n), 0.0f + n) / 300.0f, n5 = this.uiToWorldY((float)(this.getWidth() - n), 0.0f + n) / 300.0f, n6 = this.uiToWorldX((float)(this.getWidth() - n), (float)(this.getHeight() - n)) / 300.0f, n7 = this.uiToWorldY((float)(this.getWidth() - n), (float)(this.getHeight() - n)) / 300.0f, n8 = this.uiToWorldX(0.0f + n, (float)(this.getHeight() - n)) / 300.0f, n9 = this.uiToWorldY(0.0f + n, (float)(this.getHeight() - n)) / 300.0f, rasterizeMult = 1; this.triangleArea(n8 / rasterizeMult, n9 / rasterizeMult, n6 / rasterizeMult, n7 / rasterizeMult, n4 / rasterizeMult, n5 / rasterizeMult) + this.triangleArea(n4 / rasterizeMult, n5 / rasterizeMult, n2 / rasterizeMult, n3 / rasterizeMult, n8 / rasterizeMult, n9 / rasterizeMult) > 80.0f; ++rasterizeMult) {}
            this.m_rasterizeMult = rasterizeMult;
            this.m_rasterizeXY.clear();
            this.m_rasterizeSet.clear();
            this.m_rasterize.scanTriangle(n8 / rasterizeMult, n9 / rasterizeMult, n6 / rasterizeMult, n7 / rasterizeMult, n4 / rasterizeMult, n5 / rasterizeMult, 0, 1000, this::rasterizeCellsCallback);
            this.m_rasterize.scanTriangle(n4 / rasterizeMult, n5 / rasterizeMult, n2 / rasterizeMult, n3 / rasterizeMult, n8 / rasterizeMult, n9 / rasterizeMult, 0, 1000, this::rasterizeCellsCallback);
            if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
                this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
            }
            this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
        }
        
        void renderVisibleCells() {
            final int n = (Core.bDebug && this.m_renderer.VisibleCells.getValue()) ? 200 : 0;
            final float worldScale = this.m_worldScale;
            if (1.0f / worldScale > 100.0f) {
                return;
            }
            WorldMapRenderer.m_vboLines.setMode(4);
            for (int i = 0; i < this.m_rasterizeXY.size(); i += 2) {
                final int value = this.m_rasterizeXY.get(i);
                final int value2 = this.m_rasterizeXY.get(i + 1);
                final float n2 = this.m_renderOriginX + (value * 300 - this.m_worldMap.getMinXInSquares()) * worldScale;
                final float n3 = this.m_renderOriginY + (value2 * 300 - this.m_worldMap.getMinYInSquares()) * worldScale;
                final float n4 = this.m_renderOriginX + ((value + 1) * 300 - this.m_worldMap.getMinXInSquares()) * worldScale;
                final float n5 = this.m_renderOriginY + ((value2 + 1) * 300 - this.m_worldMap.getMinYInSquares()) * worldScale;
                WorldMapRenderer.m_vboLines.addElement(n2, n3, 0.0f, 0.0f, 1.0f, 0.0f, 0.2f);
                WorldMapRenderer.m_vboLines.addElement(n4, n3, 0.0f, 0.0f, 1.0f, 0.0f, 0.2f);
                WorldMapRenderer.m_vboLines.addElement(n2, n5, 0.0f, 0.0f, 1.0f, 0.0f, 0.2f);
                WorldMapRenderer.m_vboLines.addElement(n4, n3, 0.0f, 0.0f, 0.0f, 1.0f, 0.2f);
                WorldMapRenderer.m_vboLines.addElement(n4, n5, 0.0f, 0.0f, 0.0f, 1.0f, 0.2f);
                WorldMapRenderer.m_vboLines.addElement(n2, n5, 0.0f, 0.0f, 0.0f, 1.0f, 0.2f);
            }
            WorldMapRenderer.m_vboLines.flush();
            final float n6 = this.uiToWorldX(n + 0.0f, n + 0.0f) / 300.0f;
            final float n7 = this.uiToWorldY(n + 0.0f, n + 0.0f) / 300.0f;
            final float n8 = this.uiToWorldX((float)(this.getWidth() - n), 0.0f + n) / 300.0f;
            final float n9 = this.uiToWorldY((float)(this.getWidth() - n), 0.0f + n) / 300.0f;
            final float n10 = this.uiToWorldX((float)(this.getWidth() - n), (float)(this.getHeight() - n)) / 300.0f;
            final float n11 = this.uiToWorldY((float)(this.getWidth() - n), (float)(this.getHeight() - n)) / 300.0f;
            final float n12 = this.uiToWorldX(0.0f + n, (float)(this.getHeight() - n)) / 300.0f;
            final float n13 = this.uiToWorldY(0.0f + n, (float)(this.getHeight() - n)) / 300.0f;
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(4.0f);
            WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (n12 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n13 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, this.m_renderOriginX + (n10 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n11 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (n10 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n11 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, this.m_renderOriginX + (n8 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n9 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (n8 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n9 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, this.m_renderOriginX + (n12 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n13 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, 0.5f, 0.5f, 0.5f, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (n8 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n9 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, this.m_renderOriginX + (n6 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n7 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (n6 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n7 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, this.m_renderOriginX + (n12 * 300.0f - this.m_worldMap.getMinXInSquares()) * worldScale, this.m_renderOriginY + (n13 * 300.0f - this.m_worldMap.getMinYInSquares()) * worldScale, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        }
        
        void calcVisiblePyramidTiles(final WorldMapImages worldMapImages) {
            if (Core.bDebug) {}
            final boolean b = false;
            final int n = b ? 200 : 0;
            final float worldScale = this.m_worldScale;
            final float n2 = (float)(256 * (1 << worldMapImages.getZoom(this.m_zoomF)));
            final int minX = worldMapImages.getMinX();
            final int minY = worldMapImages.getMinY();
            final float n3 = (this.uiToWorldX(n + 0.0f, n + 0.0f) - minX) / n2;
            final float n4 = (this.uiToWorldY(n + 0.0f, n + 0.0f) - minY) / n2;
            final float n5 = (this.uiToWorldX((float)(this.getWidth() - n), 0.0f + n) - minX) / n2;
            final float n6 = (this.uiToWorldY((float)(this.getWidth() - n), 0.0f + n) - minY) / n2;
            final float n7 = (this.uiToWorldX((float)(this.getWidth() - n), (float)(this.getHeight() - n)) - minX) / n2;
            final float n8 = (this.uiToWorldY((float)(this.getWidth() - n), (float)(this.getHeight() - n)) - minY) / n2;
            final float n9 = (this.uiToWorldX(0.0f + n, (float)(this.getHeight() - n)) - minX) / n2;
            final float n10 = (this.uiToWorldY(0.0f + n, (float)(this.getHeight() - n)) - minY) / n2;
            if (b) {
                WorldMapRenderer.m_vboLines.setMode(1);
                WorldMapRenderer.m_vboLines.setLineWidth(4.0f);
                WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + n9 * n2 * worldScale, this.m_renderOriginY + n10 * n2 * worldScale, 0.0f, this.m_renderOriginX + n7 * n2 * worldScale, this.m_renderOriginY + n8 * n2 * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
                WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + n7 * n2 * worldScale, this.m_renderOriginY + n8 * n2 * worldScale, 0.0f, this.m_renderOriginX + n5 * n2 * worldScale, this.m_renderOriginY + n6 * n2 * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
                WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + n5 * n2 * worldScale, this.m_renderOriginY + n6 * n2 * worldScale, 0.0f, this.m_renderOriginX + n9 * n2 * worldScale, this.m_renderOriginY + n10 * n2 * worldScale, 0.0f, 0.5f, 0.5f, 0.5f, 1.0f);
                WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + n5 * n2 * worldScale, this.m_renderOriginY + n6 * n2 * worldScale, 0.0f, this.m_renderOriginX + n3 * n2 * worldScale, this.m_renderOriginY + n4 * n2 * worldScale, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
                WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + n3 * n2 * worldScale, this.m_renderOriginY + n4 * n2 * worldScale, 0.0f, this.m_renderOriginX + n9 * n2 * worldScale, this.m_renderOriginY + n10 * n2 * worldScale, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
            }
            this.m_rasterizeXY.clear();
            this.m_rasterizeSet.clear();
            this.m_rasterizeMinTileX = (float)(int)((this.m_worldMap.getMinXInSquares() - worldMapImages.getMinX()) / n2);
            this.m_rasterizeMinTileY = (float)(int)((this.m_worldMap.getMinYInSquares() - worldMapImages.getMinY()) / n2);
            this.m_rasterizeMaxTileX = (this.m_worldMap.getMaxXInSquares() - worldMapImages.getMinX()) / n2;
            this.m_rasterizeMaxTileY = (this.m_worldMap.getMaxYInSquares() - worldMapImages.getMinY()) / n2;
            this.m_rasterize.scanTriangle(n9, n10, n7, n8, n5, n6, 0, 1000, this::rasterizeTilesCallback);
            this.m_rasterize.scanTriangle(n5, n6, n3, n4, n9, n10, 0, 1000, this::rasterizeTilesCallback);
            if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
                this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
            }
            this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
            if (b) {
                WorldMapRenderer.m_vboLines.setMode(4);
                for (int i = 0; i < this.m_rasterizeXY.size(); i += 2) {
                    final int value = this.m_rasterizeXY.get(i);
                    final int value2 = this.m_rasterizeXY.get(i + 1);
                    final float n11 = this.m_renderOriginX + value * n2 * worldScale;
                    final float n12 = this.m_renderOriginY + value2 * n2 * worldScale;
                    final float n13 = this.m_renderOriginX + (value + 1) * n2 * worldScale;
                    final float n14 = this.m_renderOriginY + (value2 + 1) * n2 * worldScale;
                    WorldMapRenderer.m_vboLines.addElement(n11, n12, 0.0f, 0.0f, 1.0f, 0.0f, 0.2f);
                    WorldMapRenderer.m_vboLines.addElement(n13, n12, 0.0f, 0.0f, 1.0f, 0.0f, 0.2f);
                    WorldMapRenderer.m_vboLines.addElement(n11, n14, 0.0f, 0.0f, 1.0f, 0.0f, 0.2f);
                    WorldMapRenderer.m_vboLines.addElement(n13, n12, 0.0f, 0.0f, 0.0f, 1.0f, 0.2f);
                    WorldMapRenderer.m_vboLines.addElement(n13, n14, 0.0f, 0.0f, 0.0f, 1.0f, 0.2f);
                    WorldMapRenderer.m_vboLines.addElement(n11, n14, 0.0f, 0.0f, 0.0f, 1.0f, 0.2f);
                }
                WorldMapRenderer.m_vboLines.flush();
            }
        }
        
        void renderImagePyramids() {
            for (int i = this.m_worldMap.getImagesCount() - 1; i >= 0; --i) {
                this.renderImagePyramid(this.m_worldMap.getImagesByIndex(i));
                GL11.glDisable(3553);
            }
        }
        
        void renderImagePyramid(final WorldMapImages worldMapImages) {
            final float worldScale = this.m_worldScale;
            final int n = 256;
            final int zoom = worldMapImages.getZoom(this.m_zoomF);
            final float n2 = (float)(n * (1 << zoom));
            this.calcVisiblePyramidTiles(worldMapImages);
            GL11.glEnable(3553);
            GL11.glEnable(3042);
            WorldMapRenderer.m_vboLinesUV.setMode(4);
            final int clamp = PZMath.clamp(worldMapImages.getMinX(), this.m_worldMap.getMinXInSquares(), this.m_worldMap.getMaxXInSquares());
            final int clamp2 = PZMath.clamp(worldMapImages.getMinY(), this.m_worldMap.getMinYInSquares(), this.m_worldMap.getMaxYInSquares());
            final int clamp3 = PZMath.clamp(worldMapImages.getMaxX(), this.m_worldMap.getMinXInSquares(), this.m_worldMap.getMaxXInSquares() + 1);
            final int clamp4 = PZMath.clamp(worldMapImages.getMaxY(), this.m_worldMap.getMinYInSquares(), this.m_worldMap.getMaxYInSquares() + 1);
            for (int i = 0; i < this.m_rasterizeXY.size() - 1; i += 2) {
                final int n3 = this.m_rasterizeXY_ints[i];
                final int n4 = this.m_rasterizeXY_ints[i + 1];
                final TextureID texture = worldMapImages.getPyramid().getTexture(n3, n4, zoom);
                if (texture != null) {
                    if (texture.isReady()) {
                        WorldMapRenderer.m_vboLinesUV.startRun(texture);
                        final float n5 = worldMapImages.getMinX() + n3 * n2;
                        final float n6 = worldMapImages.getMinY() + n4 * n2;
                        final float n7 = n5 + n2;
                        final float n8 = n6 + n2;
                        final float clamp5 = PZMath.clamp(n5, (float)clamp, (float)clamp3);
                        final float clamp6 = PZMath.clamp(n6, (float)clamp2, (float)clamp4);
                        final float clamp7 = PZMath.clamp(n7, (float)clamp, (float)clamp3);
                        final float clamp8 = PZMath.clamp(n8, (float)clamp2, (float)clamp4);
                        final float n9 = (clamp5 - this.m_centerWorldX) * worldScale;
                        final float n10 = (clamp6 - this.m_centerWorldY) * worldScale;
                        final float n11 = (clamp7 - this.m_centerWorldX) * worldScale;
                        final float n12 = (clamp6 - this.m_centerWorldY) * worldScale;
                        final float n13 = (clamp7 - this.m_centerWorldX) * worldScale;
                        final float n14 = (clamp8 - this.m_centerWorldY) * worldScale;
                        final float n15 = (clamp5 - this.m_centerWorldX) * worldScale;
                        final float n16 = (clamp8 - this.m_centerWorldY) * worldScale;
                        final float n17 = (clamp5 - n5) / n2;
                        final float n18 = (clamp6 - n6) / n2;
                        final float n19 = (clamp7 - n5) / n2;
                        final float n20 = (clamp6 - n6) / n2;
                        final float n21 = (clamp7 - n5) / n2;
                        final float n22 = (clamp8 - n6) / n2;
                        final float n23 = (clamp5 - n5) / n2;
                        final float n24 = (clamp8 - n6) / n2;
                        final float n25 = 1.0f;
                        final float n26 = 1.0f;
                        final float n27 = 1.0f;
                        final float n28 = 1.0f;
                        WorldMapRenderer.m_vboLinesUV.addElement(n9, n10, 0.0f, n17, n18, n25, n26, n27, n28);
                        WorldMapRenderer.m_vboLinesUV.addElement(n11, n12, 0.0f, n19, n20, n25, n26, n27, n28);
                        WorldMapRenderer.m_vboLinesUV.addElement(n15, n16, 0.0f, n23, n24, n25, n26, n27, n28);
                        WorldMapRenderer.m_vboLinesUV.addElement(n11, n12, 0.0f, n19, n20, n25, n26, n27, n28);
                        WorldMapRenderer.m_vboLinesUV.addElement(n13, n14, 0.0f, n21, n22, n25, n26, n27, n28);
                        WorldMapRenderer.m_vboLinesUV.addElement(n15, n16, 0.0f, n23, n24, n25, n26, n27, n28);
                        if (this.m_renderer.TileGrid.getValue()) {
                            WorldMapRenderer.m_vboLinesUV.flush();
                            WorldMapRenderer.m_vboLines.setMode(1);
                            WorldMapRenderer.m_vboLines.setLineWidth(2.0f);
                            WorldMapRenderer.m_vboLines.addLine((n5 - this.m_centerWorldX) * worldScale, (n6 - this.m_centerWorldY) * worldScale, 0.0f, (n7 - this.m_centerWorldX) * worldScale, (n6 - this.m_centerWorldY) * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 0.5f);
                            WorldMapRenderer.m_vboLines.addLine((n5 - this.m_centerWorldX) * worldScale, (n8 - this.m_centerWorldY) * worldScale, 0.0f, (n7 - this.m_centerWorldX) * worldScale, (n8 - this.m_centerWorldY) * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 0.5f);
                            WorldMapRenderer.m_vboLines.addLine((n7 - this.m_centerWorldX) * worldScale, (n6 - this.m_centerWorldY) * worldScale, 0.0f, (n7 - this.m_centerWorldX) * worldScale, (n8 - this.m_centerWorldY) * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 0.5f);
                            WorldMapRenderer.m_vboLines.addLine((n5 - this.m_centerWorldX) * worldScale, (n6 - this.m_centerWorldY) * worldScale, 0.0f, (n5 - this.m_centerWorldX) * worldScale, (n8 - this.m_centerWorldY) * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 0.5f);
                            WorldMapRenderer.m_vboLines.flush();
                        }
                    }
                }
            }
        }
        
        void renderImagePyramidGrid(final WorldMapImages worldMapImages) {
            final float worldScale = this.m_worldScale;
            final float n = (float)(256 * (1 << worldMapImages.getZoom(this.m_zoomF)));
            final float n2 = (worldMapImages.getMinX() - this.m_centerWorldX) * worldScale;
            final float n3 = (worldMapImages.getMinY() - this.m_centerWorldY) * worldScale;
            final int n4 = (int)Math.ceil((worldMapImages.getMaxX() - worldMapImages.getMinX()) / n);
            final int n5 = (int)Math.ceil((worldMapImages.getMaxY() - worldMapImages.getMinY()) / n);
            final float n6 = n2;
            final float n7 = n3;
            final float n8 = n6 + n4 * n * worldScale;
            final float n9 = n7 + n5 * n * worldScale;
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(2.0f);
            for (int i = 0; i < n4 + 1; ++i) {
                WorldMapRenderer.m_vboLines.addLine(n2 + i * n * worldScale, n7, 0.0f, n2 + i * n * worldScale, n9, 0.0f, 1.0f, 0.0f, 0.0f, 0.5f);
            }
            for (int j = 0; j < n5 + 1; ++j) {
                WorldMapRenderer.m_vboLines.addLine(n6, n3 + j * n * worldScale, 0.0f, n8, n3 + j * n * worldScale, 0.0f, 1.0f, 0.0f, 0.0f, 0.5f);
            }
            WorldMapRenderer.m_vboLines.flush();
        }
        
        float triangleArea(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            final float length = Vector2f.length(n3 - n, n4 - n2);
            final float length2 = Vector2f.length(n5 - n3, n6 - n4);
            final float length3 = Vector2f.length(n - n5, n2 - n6);
            final float n7 = (length + length2 + length3) / 2.0f;
            return (float)Math.sqrt(n7 * (n7 - length) * (n7 - length2) * (n7 - length3));
        }
        
        void paintAreasOutsideBounds(final int n, final int n2, final int n3, final int n4, final float n5) {
            final float n6 = this.m_renderOriginX - n % 300 * n5;
            final float n7 = this.m_renderOriginY - n2 % 300 * n5;
            final float n8 = this.m_renderOriginX + ((this.m_worldMap.getMaxXInCells() + 1) * 300 - n) * n5;
            final float n9 = this.m_renderOriginY + ((this.m_worldMap.getMaxYInCells() + 1) * 300 - n2) * n5;
            final float n10 = 0.0f;
            final WorldMapStyleLayer.RGBAf fill = this.m_fill;
            if (n % 300 != 0) {
                final float n11 = n6;
                final float n12 = n7;
                final float renderOriginX = this.m_renderOriginX;
                final float n13 = n9;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(n11, n12, renderOriginX, n13, n10, fill.r, fill.g, fill.b, fill.a);
            }
            if (n2 % 300 != 0) {
                final float renderOriginX2 = this.m_renderOriginX;
                final float n14 = n7;
                final float n15 = renderOriginX2 + this.m_worldMap.getWidthInSquares() * this.m_worldScale;
                final float renderOriginY = this.m_renderOriginY;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(renderOriginX2, n14, n15, renderOriginY, n10, fill.r, fill.g, fill.b, fill.a);
            }
            if (n3 + 1 != 0) {
                final float n16 = this.m_renderOriginX + (n3 - n + 1) * n5;
                final float n17 = n7;
                final float n18 = n8;
                final float n19 = n9;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(n16, n17, n18, n19, n10, fill.r, fill.g, fill.b, fill.a);
            }
            if (n4 + 1 != 0) {
                final float renderOriginX3 = this.m_renderOriginX;
                final float n20 = this.m_renderOriginY + this.m_worldMap.getHeightInSquares() * n5;
                final float n21 = this.m_renderOriginX + this.m_worldMap.getWidthInSquares() * n5;
                final float n22 = n9;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(renderOriginX3, n20, n21, n22, n10, fill.r, fill.g, fill.b, fill.a);
            }
        }
        
        void renderWorldBounds() {
            final float renderOriginX = this.m_renderOriginX;
            final float renderOriginY = this.m_renderOriginY;
            final float n = renderOriginX + this.m_worldMap.getWidthInSquares() * this.m_worldScale;
            final float n2 = renderOriginY + this.m_worldMap.getHeightInSquares() * this.m_worldScale;
            this.renderDropShadow();
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(2.0f);
            final float n3 = 0.5f;
            WorldMapRenderer.m_vboLines.addLine(renderOriginX, renderOriginY, 0.0f, n, renderOriginY, 0.0f, n3, n3, n3, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(n, renderOriginY, 0.0f, n, n2, 0.0f, n3, n3, n3, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(n, n2, 0.0f, renderOriginX, n2, 0.0f, n3, n3, n3, 1.0f);
            WorldMapRenderer.m_vboLines.addLine(renderOriginX, n2, 0.0f, renderOriginX, renderOriginY, 0.0f, n3, n3, n3, 1.0f);
        }
        
        private void renderDropShadow() {
            final float n = this.m_renderer.m_dropShadowWidth * (this.m_renderer.getHeight() / 1080.0f) * this.m_worldScale / this.m_renderer.getWorldScale(this.m_renderer.getBaseZoom());
            if (n < 2.0f) {
                return;
            }
            final float renderOriginX = this.m_renderOriginX;
            final float renderOriginY = this.m_renderOriginY;
            final float n2 = renderOriginX + this.m_worldMap.getWidthInSquares() * this.m_worldScale;
            final float n3 = renderOriginY + this.m_worldMap.getHeightInSquares() * this.m_worldScale;
            WorldMapRenderer.m_vboLines.setMode(4);
            WorldMapRenderer.m_vboLines.addElement(renderOriginX + n, n3, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f);
            WorldMapRenderer.m_vboLines.addElement(n2, n3, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f);
            WorldMapRenderer.m_vboLines.addElement(renderOriginX + n, n3 + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f);
            WorldMapRenderer.m_vboLines.addElement(n2, n3, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f);
            WorldMapRenderer.m_vboLines.addElement(n2 + n, n3 + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f);
            WorldMapRenderer.m_vboLines.addElement(renderOriginX + n, n3 + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f);
            WorldMapRenderer.m_vboLines.addElement(n2, renderOriginY + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f);
            WorldMapRenderer.m_vboLines.addElement(n2 + n, renderOriginY + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f);
            WorldMapRenderer.m_vboLines.addElement(n2, n3, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f);
            WorldMapRenderer.m_vboLines.addElement(n2 + n, renderOriginY + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f);
            WorldMapRenderer.m_vboLines.addElement(n2 + n, n3 + n, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f);
            WorldMapRenderer.m_vboLines.addElement(n2, n3, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f);
        }
        
        @Override
        public void postRender() {
            for (int i = 0; i < this.m_playerRenderData.length; ++i) {
                final PlayerRenderData playerRenderData = this.m_playerRenderData[i];
                if (playerRenderData.m_modelSlotRenderData != null) {
                    playerRenderData.m_modelSlotRenderData.postRender();
                }
            }
        }
    }
    
    private static final class CharacterModelCamera extends ModelCamera
    {
        float m_worldScale;
        float m_angle;
        float m_playerX;
        float m_playerY;
        boolean m_bVehicle;
        
        @Override
        public void Begin() {
            final Matrix4f allocMatrix4f = WorldMapRenderer.allocMatrix4f();
            allocMatrix4f.identity();
            allocMatrix4f.translate(this.m_playerX * this.m_worldScale, this.m_playerY * this.m_worldScale, 0.0f);
            allocMatrix4f.rotateX(1.5707964f);
            allocMatrix4f.rotateY(this.m_angle + 4.712389f);
            if (this.m_bVehicle) {
                allocMatrix4f.scale(this.m_worldScale);
            }
            else {
                allocMatrix4f.scale(1.5f * this.m_worldScale);
            }
            PZGLUtil.pushAndMultMatrix(5888, allocMatrix4f);
            WorldMapRenderer.releaseMatrix4f(allocMatrix4f);
        }
        
        @Override
        public void End() {
            PZGLUtil.popMatrix(5888);
        }
    }
    
    public final class WorldMapBooleanOption extends BooleanConfigOption
    {
        public WorldMapBooleanOption(final String s, final boolean b) {
            super(s, b);
            WorldMapRenderer.this.options.add(this);
        }
    }
    
    public final class WorldMapDoubleOption extends DoubleConfigOption
    {
        public WorldMapDoubleOption(final String s, final double n, final double n2, final double n3) {
            super(s, n, n2, n3);
            WorldMapRenderer.this.options.add(this);
        }
    }
}
