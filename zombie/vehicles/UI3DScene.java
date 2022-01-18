// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.iso.IsoMovingObject;
import zombie.debug.DebugOptions;
import zombie.iso.IsoUtils;
import zombie.Lua.LuaManager;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.textures.Texture;
import java.util.Objects;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import java.util.function.Supplier;
import zombie.characters.action.ActionState;
import zombie.core.opengl.PZGLUtil;
import zombie.scripting.objects.VehicleScript;
import zombie.characters.action.ActionContext;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.model.Model;
import zombie.scripting.objects.ModelScript;
import zombie.characters.action.ActionGroup;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.ModelManager;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import java.util.Iterator;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.math.PZMath;
import zombie.util.Type;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.BoxedStaticValues;
import zombie.iso.Vector2;
import zombie.debug.LineDrawer;
import zombie.ui.UIFont;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import org.joml.Vector3fc;
import zombie.input.Mouse;
import zombie.ui.UIManager;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import org.joml.Quaternionfc;
import org.joml.Matrix4fc;
import zombie.IndieGL;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.opengl.VBOLines;
import org.joml.Vector4f;
import zombie.popman.ObjectPool;
import org.joml.Quaternionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import zombie.ui.UIElement;

public final class UI3DScene extends UIElement
{
    private final ArrayList<SceneObject> m_objects;
    private View m_view;
    private TransformMode m_transformMode;
    private int m_view_x;
    private int m_view_y;
    private final Vector3f m_viewRotation;
    private int m_zoom;
    private int m_zoomMax;
    private int m_gridDivisions;
    private GridPlane m_gridPlane;
    private final Matrix4f m_projection;
    private final Matrix4f m_modelView;
    private long VIEW_CHANGE_TIME;
    private long m_viewChangeTime;
    private final Quaternionf m_modelViewChange;
    private boolean m_bDrawGrid;
    private boolean m_bDrawGridAxes;
    private final CharacterSceneModelCamera m_CharacterSceneModelCamera;
    private final VehicleSceneModelCamera m_VehicleSceneModelCamera;
    private static final ObjectPool<SetModelCamera> s_SetModelCameraPool;
    private final StateData[] m_stateData;
    private Gizmo m_gizmo;
    private final RotateGizmo m_rotateGizmo;
    private final ScaleGizmo m_scaleGizmo;
    private final TranslateGizmo m_translateGizmo;
    private final Vector3f m_gizmoPos;
    private final Vector3f m_gizmoRotate;
    private SceneObject m_gizmoParent;
    private SceneObject m_gizmoOrigin;
    private SceneObject m_gizmoChild;
    private final OriginAttachment m_originAttachment;
    private final OriginBone m_originBone;
    private final OriginGizmo m_originGizmo;
    private float m_gizmoScale;
    private String m_selectedAttachment;
    private final ArrayList<PositionRotation> m_axes;
    private final OriginBone m_highlightBone;
    private static final ObjectPool<PositionRotation> s_posRotPool;
    private final ArrayList<AABB> m_aabb;
    private static final ObjectPool<AABB> s_aabbPool;
    private final ArrayList<Box3D> m_box3D;
    private static final ObjectPool<Box3D> s_box3DPool;
    final Vector3f tempVector3f;
    final Vector4f tempVector4f;
    final int[] m_viewport;
    private final float GRID_DARK = 0.1f;
    private final float GRID_LIGHT = 0.2f;
    private float GRID_ALPHA;
    private final int HALF_GRID = 5;
    private static final VBOLines vboLines;
    private static final ThreadLocal<ObjectPool<Ray>> TL_Ray_pool;
    private static final ThreadLocal<ObjectPool<Plane>> TL_Plane_pool;
    static final float SMALL_NUM = 1.0E-8f;
    
    public UI3DScene(final KahluaTable kahluaTable) {
        super(kahluaTable);
        this.m_objects = new ArrayList<SceneObject>();
        this.m_view = View.Right;
        this.m_transformMode = TransformMode.Local;
        this.m_view_x = 0;
        this.m_view_y = 0;
        this.m_viewRotation = new Vector3f();
        this.m_zoom = 3;
        this.m_zoomMax = 10;
        this.m_gridDivisions = 1;
        this.m_gridPlane = GridPlane.YZ;
        this.m_projection = new Matrix4f();
        this.m_modelView = new Matrix4f();
        this.VIEW_CHANGE_TIME = 350L;
        this.m_modelViewChange = new Quaternionf();
        this.m_bDrawGrid = true;
        this.m_bDrawGridAxes = false;
        this.m_CharacterSceneModelCamera = new CharacterSceneModelCamera();
        this.m_VehicleSceneModelCamera = new VehicleSceneModelCamera();
        this.m_stateData = new StateData[3];
        this.m_rotateGizmo = new RotateGizmo();
        this.m_scaleGizmo = new ScaleGizmo();
        this.m_translateGizmo = new TranslateGizmo();
        this.m_gizmoPos = new Vector3f();
        this.m_gizmoRotate = new Vector3f();
        this.m_gizmoParent = null;
        this.m_gizmoOrigin = null;
        this.m_gizmoChild = null;
        this.m_originAttachment = new OriginAttachment(this);
        this.m_originBone = new OriginBone(this);
        this.m_originGizmo = new OriginGizmo(this);
        this.m_gizmoScale = 1.0f;
        this.m_selectedAttachment = null;
        this.m_axes = new ArrayList<PositionRotation>();
        this.m_highlightBone = new OriginBone(this);
        this.m_aabb = new ArrayList<AABB>();
        this.m_box3D = new ArrayList<Box3D>();
        this.tempVector3f = new Vector3f();
        this.tempVector4f = new Vector4f();
        this.m_viewport = new int[] { 0, 0, 0, 0 };
        this.GRID_ALPHA = 1.0f;
        for (int i = 0; i < this.m_stateData.length; ++i) {
            this.m_stateData[i] = new StateData();
            this.m_stateData[i].m_overlaysDrawer = new OverlaysDrawer();
        }
    }
    
    SceneObject getSceneObjectById(final String anotherString, final boolean b) {
        for (int i = 0; i < this.m_objects.size(); ++i) {
            final SceneObject sceneObject = this.m_objects.get(i);
            if (sceneObject.m_id.equalsIgnoreCase(anotherString)) {
                return sceneObject;
            }
        }
        if (b) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anotherString));
        }
        return null;
    }
    
     <C> C getSceneObjectById(final String anotherString, final Class<C> clazz, final boolean b) {
        for (int i = 0; i < this.m_objects.size(); ++i) {
            final SceneObject obj = this.m_objects.get(i);
            if (obj.m_id.equalsIgnoreCase(anotherString)) {
                if (obj.getClass() == clazz) {
                    return (C)clazz.cast(obj);
                }
                if (b) {
                    throw new ClassCastException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, anotherString, obj.getClass().getSimpleName(), clazz.getSimpleName()));
                }
            }
        }
        if (b) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anotherString));
        }
        return null;
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        super.render();
        IndieGL.glClear(256);
        final StateData stateDataMain = this.stateDataMain();
        this.calcMatrices(this.m_projection, this.m_modelView);
        stateDataMain.m_projection.set((Matrix4fc)this.m_projection);
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME > currentTimeMillis) {
            final float n = (this.m_viewChangeTime + this.VIEW_CHANGE_TIME - currentTimeMillis) / (float)this.VIEW_CHANGE_TIME;
            final Quaternionf setFromUnnormalized = allocQuaternionf().setFromUnnormalized((Matrix4fc)this.m_modelView);
            stateDataMain.m_modelView.set((Quaternionfc)this.m_modelViewChange.slerp((Quaternionfc)setFromUnnormalized, 1.0f - n));
            releaseQuaternionf(setFromUnnormalized);
        }
        else {
            stateDataMain.m_modelView.set((Matrix4fc)this.m_modelView);
        }
        stateDataMain.m_zoom = this.m_zoom;
        PZArrayUtil.forEach(stateDataMain.m_objectData, SceneObjectRenderData::release);
        stateDataMain.m_objectData.clear();
        for (int i = 0; i < this.m_objects.size(); ++i) {
            final SceneObject sceneObject = this.m_objects.get(i);
            if (sceneObject.m_visible) {
                if (sceneObject.m_autoRotate) {
                    final SceneObject sceneObject2 = sceneObject;
                    sceneObject2.m_autoRotateAngle += (float)(UIManager.getMillisSinceLastRender() / 30.0);
                    if (sceneObject.m_autoRotateAngle > 360.0f) {
                        sceneObject.m_autoRotateAngle = 0.0f;
                    }
                }
                final SceneObjectRenderData renderMain = sceneObject.renderMain();
                if (renderMain != null) {
                    stateDataMain.m_objectData.add(renderMain);
                }
            }
        }
        final float n2 = (float)(Mouse.getXA() - this.getAbsoluteX().intValue());
        final float n3 = (float)(Mouse.getYA() - this.getAbsoluteY().intValue());
        stateDataMain.m_gizmo = this.m_gizmo;
        if (this.m_gizmo != null) {
            stateDataMain.m_gizmoTranslate.set((Vector3fc)this.m_gizmoPos);
            stateDataMain.m_gizmoRotate.set((Vector3fc)this.m_gizmoRotate);
            stateDataMain.m_gizmoTransform.translation((Vector3fc)this.m_gizmoPos);
            stateDataMain.m_gizmoTransform.rotateXYZ(this.m_gizmoRotate.x * 0.017453292f, this.m_gizmoRotate.y * 0.017453292f, this.m_gizmoRotate.z * 0.017453292f);
            stateDataMain.m_gizmoAxis = this.m_gizmo.hitTest(n2, n3);
        }
        stateDataMain.m_gizmoChildTransform.identity();
        stateDataMain.m_selectedAttachmentIsChildAttachment = (this.m_gizmoChild != null && this.m_gizmoChild.m_attachment != null && this.m_gizmoChild.m_attachment.equals(this.m_selectedAttachment));
        if (this.m_gizmoChild != null) {
            this.m_gizmoChild.getLocalTransform(stateDataMain.m_gizmoChildTransform);
        }
        stateDataMain.m_gizmoOriginTransform.identity();
        stateDataMain.m_hasGizmoOrigin = (this.m_gizmoOrigin != null);
        if (this.m_gizmoOrigin != null && this.m_gizmoOrigin != this.m_gizmoParent) {
            this.m_gizmoOrigin.getGlobalTransform(stateDataMain.m_gizmoOriginTransform);
        }
        stateDataMain.m_gizmoParentTransform.identity();
        if (this.m_gizmoParent != null) {
            this.m_gizmoParent.getGlobalTransform(stateDataMain.m_gizmoParentTransform);
        }
        stateDataMain.m_overlaysDrawer.init();
        SpriteRenderer.instance.drawGeneric(stateDataMain.m_overlaysDrawer);
        if (this.m_bDrawGrid) {
            final Vector3f uiToScene = this.uiToScene(n2, n3, 0.0f, this.tempVector3f);
            if (this.m_view == View.UserDefined) {
                final Vector3f allocVector3f = allocVector3f();
                switch (this.m_gridPlane) {
                    case XY: {
                        allocVector3f.set(0.0f, 0.0f, 1.0f);
                        break;
                    }
                    case XZ: {
                        allocVector3f.set(0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case YZ: {
                        allocVector3f.set(1.0f, 0.0f, 0.0f);
                        break;
                    }
                }
                final Vector3f set = allocVector3f().set(0.0f);
                final Plane set2 = allocPlane().set(allocVector3f, set);
                releaseVector3f(allocVector3f);
                releaseVector3f(set);
                final Ray cameraRay = this.getCameraRay(n2, this.screenHeight() - n3, allocRay());
                if (this.intersect_ray_plane(set2, cameraRay, uiToScene) != 1) {
                    uiToScene.set(0.0f);
                }
                releasePlane(set2);
                releaseRay(cameraRay);
            }
            uiToScene.x = Math.round(uiToScene.x * this.gridMult()) / this.gridMult();
            uiToScene.y = Math.round(uiToScene.y * this.gridMult()) / this.gridMult();
            uiToScene.z = Math.round(uiToScene.z * this.gridMult()) / this.gridMult();
            this.DrawText(UIFont.Small, String.valueOf(uiToScene.x), this.width - 200.0f, 10.0, 1.0, 0.0, 0.0, 1.0);
            this.DrawText(UIFont.Small, String.valueOf(uiToScene.y), this.width - 150.0f, 10.0, 0.0, 1.0, 0.0, 1.0);
            this.DrawText(UIFont.Small, String.valueOf(uiToScene.z), this.width - 100.0f, 10.0, 0.0, 0.5, 1.0, 1.0);
        }
        if (this.m_gizmo == this.m_rotateGizmo && this.m_rotateGizmo.m_trackAxis != Axis.None) {
            final Vector3f translation = this.m_rotateGizmo.m_startXfrm.getTranslation(allocVector3f());
            LineDrawer.drawLine(this.sceneToUIX(translation.x, translation.y, translation.z), this.sceneToUIY(translation.x, translation.y, translation.z), n2, n3, 0.5f, 0.5f, 0.5f, 1.0f, 1);
            releaseVector3f(translation);
        }
        if (this.m_highlightBone.m_boneName != null) {
            final Matrix4f globalTransform = this.m_highlightBone.getGlobalTransform(allocMatrix4f());
            this.m_highlightBone.m_character.getGlobalTransform(allocMatrix4f()).mul((Matrix4fc)globalTransform, globalTransform);
            final Vector3f translation2 = globalTransform.getTranslation(allocVector3f());
            LineDrawer.drawCircle(this.sceneToUIX(translation2.x, translation2.y, translation2.z), this.sceneToUIY(translation2.x, translation2.y, translation2.z), 10.0f, 16, 1.0f, 1.0f, 1.0f);
            releaseVector3f(translation2);
            releaseMatrix4f(globalTransform);
        }
    }
    
    private float gridMult() {
        return (float)(100 * this.m_gridDivisions);
    }
    
    private float zoomMult() {
        return (float)Math.exp(this.m_zoom * 0.2f) * 160.0f / Math.max(1.82f, 1.0f);
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
    
    private static Ray allocRay() {
        return UI3DScene.TL_Ray_pool.get().alloc();
    }
    
    private static void releaseRay(final Ray ray) {
        UI3DScene.TL_Ray_pool.get().release(ray);
    }
    
    private static Plane allocPlane() {
        return UI3DScene.TL_Plane_pool.get().alloc();
    }
    
    private static void releasePlane(final Plane plane) {
        UI3DScene.TL_Plane_pool.get().release(plane);
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
    
    public Object fromLua0(final String s) {
        switch (s) {
            case "clearAABBs": {
                UI3DScene.s_aabbPool.release(this.m_aabb);
                this.m_aabb.clear();
                return null;
            }
            case "clearAxes": {
                UI3DScene.s_posRotPool.release(this.m_axes);
                this.m_axes.clear();
                return null;
            }
            case "clearBox3Ds": {
                UI3DScene.s_box3DPool.release(this.m_box3D);
                this.m_box3D.clear();
                return null;
            }
            case "clearGizmoRotate": {
                this.m_gizmoRotate.set(0.0f);
                return null;
            }
            case "clearHighlightBone": {
                this.m_highlightBone.m_boneName = null;
                return null;
            }
            case "getGizmoPos": {
                return this.m_gizmoPos;
            }
            case "getGridMult": {
                return BoxedStaticValues.toDouble(this.gridMult());
            }
            case "getView": {
                return this.m_view.name();
            }
            case "getViewRotation": {
                return this.m_viewRotation;
            }
            case "getModelCount": {
                int n2 = 0;
                for (int i = 0; i < this.m_objects.size(); ++i) {
                    if (this.m_objects.get(i) instanceof SceneModel) {
                        ++n2;
                    }
                }
                return BoxedStaticValues.toDouble(n2);
            }
            case "stopGizmoTracking": {
                if (this.m_gizmo != null) {
                    this.m_gizmo.stopTracking();
                }
                return null;
            }
            default: {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
    }
    
    public Object fromLua1(final String s, final Object o) {
        switch (s) {
            case "createCharacter": {
                if (this.getSceneObjectById((String)o, false) != null) {
                    throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
                }
                final SceneCharacter e = new SceneCharacter(this, (String)o);
                this.m_objects.add(e);
                return e;
            }
            case "createVehicle": {
                if (this.getSceneObjectById((String)o, false) != null) {
                    throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
                }
                this.m_objects.add(new SceneVehicle(this, (String)o));
                return null;
            }
            case "getCharacterAnimationDuration": {
                final AnimationPlayer animationPlayer = this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.getAnimationPlayer();
                if (animationPlayer == null) {
                    return null;
                }
                final AnimationMultiTrack multiTrack = animationPlayer.getMultiTrack();
                if (multiTrack == null || multiTrack.getTracks().isEmpty()) {
                    return null;
                }
                return KahluaUtil.toDouble((double)multiTrack.getTracks().get(0).getDuration());
            }
            case "getCharacterAnimationTime": {
                final AnimationPlayer animationPlayer2 = this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.getAnimationPlayer();
                if (animationPlayer2 == null) {
                    return null;
                }
                final AnimationMultiTrack multiTrack2 = animationPlayer2.getMultiTrack();
                if (multiTrack2 == null || multiTrack2.getTracks().isEmpty()) {
                    return null;
                }
                return KahluaUtil.toDouble((double)multiTrack2.getTracks().get(0).getCurrentTimeValue());
            }
            case "getModelScript": {
                int n2 = 0;
                for (int i = 0; i < this.m_objects.size(); ++i) {
                    final SceneModel sceneModel = Type.tryCastTo(this.m_objects.get(i), SceneModel.class);
                    if (sceneModel != null && n2++ == ((Double)o).intValue()) {
                        return sceneModel.m_modelScript;
                    }
                }
                return null;
            }
            case "getObjectAutoRotate": {
                return this.getSceneObjectById((String)o, true).m_autoRotate ? Boolean.TRUE : Boolean.FALSE;
            }
            case "getObjectParent": {
                final SceneObject sceneObjectById = this.getSceneObjectById((String)o, true);
                return (sceneObjectById.m_parent == null) ? null : sceneObjectById.m_parent.m_id;
            }
            case "getObjectParentAttachment": {
                return this.getSceneObjectById((String)o, true).m_parentAttachment;
            }
            case "getObjectRotation": {
                return this.getSceneObjectById((String)o, true).m_rotate;
            }
            case "getObjectTranslation": {
                return this.getSceneObjectById((String)o, true).m_translate;
            }
            case "getVehicleScript": {
                return this.getSceneObjectById((String)o, SceneVehicle.class, true).m_script;
            }
            case "isCharacterFemale": {
                return this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.isFemale();
            }
            case "isObjectVisible": {
                return this.getSceneObjectById((String)o, true).m_visible ? Boolean.TRUE : Boolean.FALSE;
            }
            case "removeModel": {
                final SceneModel o2 = this.getSceneObjectById((String)o, SceneModel.class, true);
                this.m_objects.remove(o2);
                for (final SceneObject sceneObject : this.m_objects) {
                    if (sceneObject.m_parent == o2) {
                        sceneObject.m_attachment = null;
                        sceneObject.m_parent = null;
                        sceneObject.m_parentAttachment = null;
                    }
                }
                return null;
            }
            case "setDrawGrid": {
                this.m_bDrawGrid = (boolean)o;
                return null;
            }
            case "setDrawGridAxes": {
                this.m_bDrawGridAxes = (boolean)o;
                return null;
            }
            case "setGizmoOrigin": {
                final String s2 = (String)o;
                switch (s2) {
                    case "none": {
                        this.m_gizmoParent = null;
                        this.m_gizmoOrigin = null;
                        this.m_gizmoChild = null;
                        break;
                    }
                }
                return null;
            }
            case "setGizmoPos": {
                final Vector3f vector3f = (Vector3f)o;
                if (!this.m_gizmoPos.equals((Object)vector3f)) {
                    this.m_gizmoPos.set((Vector3fc)vector3f);
                }
                return null;
            }
            case "setGizmoRotate": {
                final Vector3f vector3f2 = (Vector3f)o;
                if (!this.m_gizmoRotate.equals((Object)vector3f2)) {
                    this.m_gizmoRotate.set((Vector3fc)vector3f2);
                }
                return null;
            }
            case "setGizmoScale": {
                this.m_gizmoScale = Math.max(((Double)o).floatValue(), 0.01f);
                return null;
            }
            case "setGizmoVisible": {
                final String anotherString = (String)o;
                this.m_rotateGizmo.m_visible = "rotate".equalsIgnoreCase(anotherString);
                this.m_scaleGizmo.m_visible = "scale".equalsIgnoreCase(anotherString);
                this.m_translateGizmo.m_visible = "translate".equalsIgnoreCase(anotherString);
                final String s3 = anotherString;
                switch (s3) {
                    case "rotate": {
                        this.m_gizmo = this.m_rotateGizmo;
                        break;
                    }
                    case "scale": {
                        this.m_gizmo = this.m_scaleGizmo;
                        break;
                    }
                    case "translate": {
                        this.m_gizmo = this.m_translateGizmo;
                        break;
                    }
                    default: {
                        this.m_gizmo = null;
                        break;
                    }
                }
                return null;
            }
            case "setGridMult": {
                this.m_gridDivisions = PZMath.clamp(((Double)o).intValue(), 1, 100);
                return null;
            }
            case "setGridPlane": {
                this.m_gridPlane = GridPlane.valueOf((String)o);
                return null;
            }
            case "setMaxZoom": {
                this.m_zoomMax = PZMath.clamp(((Double)o).intValue(), 1, 20);
                return null;
            }
            case "setSelectedAttachment": {
                this.m_selectedAttachment = (String)o;
                return null;
            }
            case "setTransformMode": {
                this.m_transformMode = TransformMode.valueOf((String)o);
                return null;
            }
            case "setZoom": {
                this.m_zoom = PZMath.clamp(((Double)o).intValue(), 1, this.m_zoomMax);
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            }
            case "setView": {
                final View view = this.m_view;
                this.m_view = View.valueOf((String)o);
                if (view != this.m_view) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME < currentTimeMillis) {
                        this.m_modelViewChange.setFromUnnormalized((Matrix4fc)this.m_modelView);
                    }
                    this.m_viewChangeTime = currentTimeMillis;
                }
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            }
            case "zoom": {
                final int n5 = -((Double)o).intValue();
                final float n6 = (float)(Mouse.getXA() - this.getAbsoluteX().intValue());
                final float n7 = (float)(Mouse.getYA() - this.getAbsoluteY().intValue());
                final float uiToSceneX = this.uiToSceneX(n6, n7);
                final float uiToSceneY = this.uiToSceneY(n6, n7);
                this.m_zoom = PZMath.clamp(this.m_zoom + n5, 1, this.m_zoomMax);
                this.calcMatrices(this.m_projection, this.m_modelView);
                final float uiToSceneX2 = this.uiToSceneX(n6, n7);
                final float uiToSceneY2 = this.uiToSceneY(n6, n7);
                this.m_view_x -= (int)((uiToSceneX2 - uiToSceneX) * this.zoomMult());
                this.m_view_y += (int)((uiToSceneY2 - uiToSceneY) * this.zoomMult());
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", s, o));
            }
        }
    }
    
    public Object fromLua2(final String s, final Object o, Object o2) {
        switch (s) {
            case "addAttachment": {
                final SceneModel sceneModel = this.getSceneObjectById((String)o, SceneModel.class, true);
                if (sceneModel.m_modelScript.getAttachmentById((String)o2) != null) {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, o, o2));
                }
                final ModelAttachment modelAttachment = new ModelAttachment((String)o2);
                sceneModel.m_modelScript.addAttachment(modelAttachment);
                return modelAttachment;
            }
            case "addBoneAxis": {
                this.m_axes.add(this.getSceneObjectById((String)o, SceneCharacter.class, true).getBoneAxis((String)o2, UI3DScene.s_posRotPool.alloc()));
                return null;
            }
            case "applyDeltaRotation": {
                final Vector3f vector3f = (Vector3f)o;
                final Vector3f vector3f2 = (Vector3f)o2;
                final Quaternionf rotationXYZ = allocQuaternionf().rotationXYZ(vector3f.x * 0.017453292f, vector3f.y * 0.017453292f, vector3f.z * 0.017453292f);
                final Quaternionf rotationXYZ2 = allocQuaternionf().rotationXYZ(vector3f2.x * 0.017453292f, vector3f2.y * 0.017453292f, vector3f2.z * 0.017453292f);
                rotationXYZ.mul((Quaternionfc)rotationXYZ2);
                rotationXYZ.getEulerAnglesXYZ(vector3f);
                releaseQuaternionf(rotationXYZ);
                releaseQuaternionf(rotationXYZ2);
                vector3f.mul(57.295776f);
                vector3f.x = (float)Math.floor(vector3f.x + 0.5f);
                vector3f.y = (float)Math.floor(vector3f.y + 0.5f);
                vector3f.z = (float)Math.floor(vector3f.z + 0.5f);
                return vector3f;
            }
            case "createModel": {
                if (this.getSceneObjectById((String)o, false) != null) {
                    throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
                }
                final ModelScript modelScript = ScriptManager.instance.getModelScript((String)o2);
                if (modelScript == null) {
                    throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o2));
                }
                final Model loadedModel = ModelManager.instance.getLoadedModel((String)o2);
                if (loadedModel == null) {
                    throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o2));
                }
                this.m_objects.add(new SceneModel(this, (String)o, modelScript, loadedModel));
                return null;
            }
            case "dragGizmo": {
                final float floatValue = ((Double)o).floatValue();
                final float floatValue2 = ((Double)o2).floatValue();
                if (this.m_gizmo == null) {
                    throw new NullPointerException("gizmo is null");
                }
                this.m_gizmo.updateTracking(floatValue, floatValue2);
                return null;
            }
            case "dragView": {
                final int intValue = ((Double)o).intValue();
                final int intValue2 = ((Double)o2).intValue();
                this.m_view_x -= intValue;
                this.m_view_y -= intValue2;
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            }
            case "getCharacterAnimationKeyframeTimes": {
                final AnimationPlayer animationPlayer = this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.getAnimationPlayer();
                if (animationPlayer == null) {
                    return null;
                }
                final AnimationMultiTrack multiTrack = animationPlayer.getMultiTrack();
                if (multiTrack == null || multiTrack.getTracks().isEmpty()) {
                    return null;
                }
                final AnimationClip clip = multiTrack.getTracks().get(0).getClip();
                if (clip == null) {
                    return null;
                }
                if (o2 == null) {
                    o2 = new ArrayList();
                }
                final ArrayList list = (ArrayList)o2;
                list.clear();
                final Keyframe[] keyframes = clip.getKeyframes();
                for (int i = 0; i < keyframes.length; ++i) {
                    final Double double1 = KahluaUtil.toDouble((double)keyframes[i].Time);
                    if (!list.contains(double1)) {
                        list.add(double1);
                    }
                }
                return list;
            }
            case "removeAttachment": {
                final SceneModel sceneModel2 = this.getSceneObjectById((String)o, SceneModel.class, true);
                final ModelAttachment attachmentById = sceneModel2.m_modelScript.getAttachmentById((String)o2);
                if (attachmentById == null) {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, o, o2));
                }
                sceneModel2.m_modelScript.removeAttachment(attachmentById);
                return null;
            }
            case "setCharacterAlpha": {
                this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.setAlpha(((Double)o2).floatValue());
                return null;
            }
            case "setCharacterAnimate": {
                this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.setAnimate((boolean)o2);
                return null;
            }
            case "setCharacterAnimationClip": {
                final SceneCharacter sceneCharacter = this.getSceneObjectById((String)o, SceneCharacter.class, true);
                final AnimationSet getAnimationSet = AnimationSet.GetAnimationSet(sceneCharacter.m_animatedModel.GetAnimSetName(), false);
                if (getAnimationSet == null) {
                    return null;
                }
                final AnimState getState = getAnimationSet.GetState(sceneCharacter.m_animatedModel.getState());
                if (getState == null || getState.m_Nodes.isEmpty()) {
                    return null;
                }
                getState.m_Nodes.get(0).m_AnimName = (String)o2;
                sceneCharacter.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
                sceneCharacter.m_animatedModel.getAdvancedAnimator().SetState(getState.m_Name);
                return null;
            }
            case "setCharacterAnimationSpeed": {
                final AnimationMultiTrack multiTrack2 = this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.getAnimationPlayer().getMultiTrack();
                if (multiTrack2.getTracks().isEmpty()) {
                    return null;
                }
                multiTrack2.getTracks().get(0).SpeedDelta = PZMath.clamp(((Double)o2).floatValue(), 0.0f, 10.0f);
                return null;
            }
            case "setCharacterAnimationTime": {
                final SceneCharacter sceneCharacter2 = this.getSceneObjectById((String)o, SceneCharacter.class, true);
                sceneCharacter2.m_animatedModel.setTrackTime(((Double)o2).floatValue());
                final AnimationPlayer animationPlayer2 = sceneCharacter2.m_animatedModel.getAnimationPlayer();
                if (animationPlayer2 == null) {
                    return null;
                }
                final AnimationMultiTrack multiTrack3 = animationPlayer2.getMultiTrack();
                if (multiTrack3 == null || multiTrack3.getTracks().isEmpty()) {
                    return null;
                }
                multiTrack3.getTracks().get(0).setCurrentTimeValue(((Double)o2).floatValue());
                return null;
            }
            case "setCharacterAnimSet": {
                final SceneCharacter sceneCharacter3 = this.getSceneObjectById((String)o, SceneCharacter.class, true);
                final String animSetName = (String)o2;
                if (!animSetName.equals(sceneCharacter3.m_animatedModel.GetAnimSetName())) {
                    sceneCharacter3.m_animatedModel.setAnimSetName(animSetName);
                    sceneCharacter3.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
                    final ActionGroup actionGroup = ActionGroup.getActionGroup(sceneCharacter3.m_animatedModel.GetAnimSetName());
                    final ActionContext actionContext = sceneCharacter3.m_animatedModel.getActionContext();
                    if (actionGroup != actionContext.getGroup()) {
                        actionContext.setGroup(actionGroup);
                    }
                    sceneCharacter3.m_animatedModel.getAdvancedAnimator().SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), actionState -> actionState.name));
                }
                return null;
            }
            case "setCharacterClearDepthBuffer": {
                this.getSceneObjectById((String)o, SceneCharacter.class, true).m_bClearDepthBuffer = (boolean)o2;
                return null;
            }
            case "setCharacterFemale": {
                final SceneCharacter sceneCharacter4 = this.getSceneObjectById((String)o, SceneCharacter.class, true);
                final boolean booleanValue = (boolean)o2;
                if (booleanValue != sceneCharacter4.m_animatedModel.isFemale()) {
                    sceneCharacter4.m_animatedModel.setOutfitName("Naked", booleanValue, false);
                }
                return null;
            }
            case "setCharacterShowBones": {
                this.getSceneObjectById((String)o, SceneCharacter.class, true).m_bShowBones = (boolean)o2;
                return null;
            }
            case "setCharacterUseDeferredMovement": {
                this.getSceneObjectById((String)o, SceneCharacter.class, true).m_bUseDeferredMovement = (boolean)o2;
                return null;
            }
            case "setGizmoOrigin": {
                final String s2 = (String)o;
                switch (s2) {
                    case "centerOfMass": {
                        this.m_gizmoParent = this.getSceneObjectById((String)o2, SceneVehicle.class, true);
                        this.m_gizmoOrigin = this.m_gizmoParent;
                        this.m_gizmoChild = null;
                        break;
                    }
                    case "chassis": {
                        final SceneVehicle gizmoParent = this.getSceneObjectById((String)o2, SceneVehicle.class, true);
                        this.m_gizmoParent = gizmoParent;
                        this.m_originGizmo.m_translate.set((Vector3fc)gizmoParent.m_script.getCenterOfMassOffset());
                        this.m_originGizmo.m_rotate.zero();
                        this.m_gizmoOrigin = this.m_originGizmo;
                        this.m_gizmoChild = null;
                        break;
                    }
                    case "character": {
                        this.m_gizmoParent = this.getSceneObjectById((String)o2, SceneCharacter.class, true);
                        this.m_gizmoOrigin = this.m_gizmoParent;
                        this.m_gizmoChild = null;
                        break;
                    }
                    case "model": {
                        this.m_gizmoParent = this.getSceneObjectById((String)o2, SceneModel.class, true);
                        this.m_gizmoOrigin = this.m_gizmoParent;
                        this.m_gizmoChild = null;
                        break;
                    }
                    case "vehicleModel": {
                        final SceneVehicle gizmoParent2 = this.getSceneObjectById((String)o2, SceneVehicle.class, true);
                        this.m_gizmoParent = gizmoParent2;
                        this.m_originGizmo.m_translate.set((Vector3fc)gizmoParent2.m_script.getModel().getOffset());
                        this.m_originGizmo.m_rotate.zero();
                        this.m_gizmoOrigin = this.m_originGizmo;
                        this.m_gizmoChild = null;
                        break;
                    }
                }
                return null;
            }
            case "setCharacterState": {
                this.getSceneObjectById((String)o, SceneCharacter.class, true).m_animatedModel.setState((String)o2);
                return null;
            }
            case "setHighlightBone": {
                final SceneCharacter character = this.getSceneObjectById((String)o, SceneCharacter.class, true);
                final String boneName = (String)o2;
                this.m_highlightBone.m_character = character;
                this.m_highlightBone.m_boneName = boneName;
                return null;
            }
            case "setObjectAutoRotate": {
                final SceneObject sceneObjectById = this.getSceneObjectById((String)o, true);
                if (!(sceneObjectById.m_autoRotate = (boolean)o2)) {
                    sceneObjectById.m_autoRotateAngle = 0.0f;
                }
                return null;
            }
            case "setObjectVisible": {
                this.getSceneObjectById((String)o, true).m_visible = (boolean)o2;
                return null;
            }
            case "setVehicleScript": {
                this.getSceneObjectById((String)o, SceneVehicle.class, true).setScriptName((String)o2);
                return null;
            }
            case "testGizmoAxis": {
                final int intValue3 = ((Double)o).intValue();
                final int intValue4 = ((Double)o2).intValue();
                if (this.m_gizmo == null) {
                    return "None";
                }
                return this.m_gizmo.hitTest((float)intValue3, (float)intValue4).toString();
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\"", s, o, o2));
            }
        }
    }
    
    public Object fromLua3(final String s, final Object o, final Object o2, final Object o3) {
        switch (s) {
            case "addAxis": {
                this.m_axes.add(UI3DScene.s_posRotPool.alloc().set(((Double)o).floatValue(), ((Double)o2).floatValue(), ((Double)o3).floatValue()));
                return null;
            }
            case "pickCharacterBone": {
                return this.getSceneObjectById((String)o, SceneCharacter.class, true).pickBone(((Double)o2).floatValue(), ((Double)o3).floatValue());
            }
            case "setGizmoOrigin": {
                final String s2 = (String)o;
                switch (s2) {
                    case "bone": {
                        final SceneCharacter sceneCharacter = this.getSceneObjectById((String)o2, SceneCharacter.class, true);
                        this.m_gizmoParent = sceneCharacter;
                        this.m_originBone.m_character = sceneCharacter;
                        this.m_originBone.m_boneName = (String)o3;
                        this.m_gizmoOrigin = this.m_originBone;
                        this.m_gizmoChild = null;
                        break;
                    }
                }
                return null;
            }
            case "setGizmoXYZ": {
                this.m_gizmoPos.set(((Double)o).floatValue(), ((Double)o2).floatValue(), ((Double)o3).floatValue());
                return null;
            }
            case "startGizmoTracking": {
                final float floatValue = ((Double)o).floatValue();
                final float floatValue2 = ((Double)o2).floatValue();
                final Axis value = Axis.valueOf((String)o3);
                if (this.m_gizmo != null) {
                    this.m_gizmo.startTracking(floatValue, floatValue2, value);
                }
                return null;
            }
            case "setViewRotation": {
                this.m_viewRotation.set(((Double)o).floatValue() % 360.0f, ((Double)o2).floatValue() % 360.0f, ((Double)o3).floatValue() % 360.0f);
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\"", s, o, o2, o3));
            }
        }
    }
    
    public Object fromLua4(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        switch (s) {
            case "setGizmoOrigin": {
                final String s2 = (String)o;
                switch (s2) {
                    case "attachment": {
                        final SceneObject sceneObjectById = this.getSceneObjectById((String)o2, true);
                        this.m_gizmoParent = this.getSceneObjectById((String)o3, true);
                        this.m_originAttachment.m_object = this.m_gizmoParent;
                        this.m_originAttachment.m_attachmentName = (String)o4;
                        this.m_gizmoOrigin = this.m_originAttachment;
                        this.m_gizmoChild = sceneObjectById;
                        break;
                    }
                }
                return null;
            }
            case "setObjectParent": {
                final SceneObject sceneObjectById2 = this.getSceneObjectById((String)o, true);
                sceneObjectById2.m_translate.zero();
                sceneObjectById2.m_rotate.zero();
                sceneObjectById2.m_attachment = (String)o2;
                sceneObjectById2.m_parent = this.getSceneObjectById((String)o3, false);
                sceneObjectById2.m_parentAttachment = (String)o4;
                if (sceneObjectById2.m_parent != null && sceneObjectById2.m_parent.m_parent == sceneObjectById2) {
                    sceneObjectById2.m_parent.m_parent = null;
                }
                return null;
            }
            case "setObjectPosition": {
                this.getSceneObjectById((String)o, true).m_translate.set(((Double)o2).floatValue(), ((Double)o3).floatValue(), ((Double)o4).floatValue());
                return null;
            }
            case "setPassengerPosition": {
                final SceneCharacter sceneCharacter = this.getSceneObjectById((String)o, SceneCharacter.class, true);
                final SceneVehicle parent = this.getSceneObjectById((String)o2, SceneVehicle.class, true);
                final VehicleScript.Passenger passengerById = parent.m_script.getPassengerById((String)o3);
                if (passengerById == null) {
                    return null;
                }
                final VehicleScript.Position positionById = passengerById.getPositionById((String)o4);
                if (positionById != null) {
                    this.tempVector3f.set((Vector3fc)parent.m_script.getModel().getOffset());
                    this.tempVector3f.add((Vector3fc)positionById.getOffset());
                    final Vector3f tempVector3f = this.tempVector3f;
                    tempVector3f.z *= -1.0f;
                    sceneCharacter.m_translate.set((Vector3fc)this.tempVector3f);
                    sceneCharacter.m_rotate.set((Vector3fc)positionById.rotate);
                    sceneCharacter.m_parent = parent;
                    if (sceneCharacter.m_animatedModel != null) {
                        final String animSetName = "inside".equalsIgnoreCase(positionById.getId()) ? "player-vehicle" : "player-editor";
                        if (!animSetName.equals(sceneCharacter.m_animatedModel.GetAnimSetName())) {
                            sceneCharacter.m_animatedModel.setAnimSetName(animSetName);
                            sceneCharacter.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
                            final ActionGroup actionGroup = ActionGroup.getActionGroup(sceneCharacter.m_animatedModel.GetAnimSetName());
                            final ActionContext actionContext = sceneCharacter.m_animatedModel.getActionContext();
                            if (actionGroup != actionContext.getGroup()) {
                                actionContext.setGroup(actionGroup);
                            }
                            sceneCharacter.m_animatedModel.getAdvancedAnimator().SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), actionState -> actionState.name));
                        }
                    }
                }
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\"", s, o, o2, o3));
            }
        }
    }
    
    public Object fromLua6(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        switch (s) {
            case "addAABB": {
                this.m_aabb.add(UI3DScene.s_aabbPool.alloc().set(((Double)o).floatValue(), ((Double)o2).floatValue(), ((Double)o3).floatValue(), ((Double)o4).floatValue(), ((Double)o5).floatValue(), ((Double)o6).floatValue(), 1.0f, 1.0f, 1.0f));
                return null;
            }
            case "addAxis": {
                this.m_axes.add(UI3DScene.s_posRotPool.alloc().set(((Double)o).floatValue(), ((Double)o2).floatValue(), ((Double)o3).floatValue(), ((Double)o4).floatValue(), ((Double)o5).floatValue(), ((Double)o6).floatValue()));
                return null;
            }
            case "addBox3D": {
                final Vector3f vector3f = (Vector3f)o;
                final Vector3f vector3f2 = (Vector3f)o2;
                final Vector3f vector3f3 = (Vector3f)o3;
                this.m_box3D.add(UI3DScene.s_box3DPool.alloc().set(vector3f.x, vector3f.y, vector3f.z, vector3f2.x, vector3f2.y, vector3f2.z, vector3f3.x, vector3f3.y, vector3f3.z, ((Double)o4).floatValue(), ((Double)o5).floatValue(), ((Double)o6).floatValue()));
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", s, o, o2, o3, o4, o5, o6));
            }
        }
    }
    
    public Object fromLua9(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        switch (s) {
            case "addAABB": {
                this.m_aabb.add(UI3DScene.s_aabbPool.alloc().set(((Double)o).floatValue(), ((Double)o2).floatValue(), ((Double)o3).floatValue(), ((Double)o4).floatValue(), ((Double)o5).floatValue(), ((Double)o6).floatValue(), ((Double)o7).floatValue(), ((Double)o8).floatValue(), ((Double)o9).floatValue()));
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", s, o, o2, o3, o4, o5, o6, o7, o8, o9));
            }
        }
    }
    
    private int screenWidth() {
        return (int)this.width;
    }
    
    private int screenHeight() {
        return (int)this.height;
    }
    
    public float uiToSceneX(final float n, final float n2) {
        return (n - this.screenWidth() / 2.0f + this.m_view_x) / this.zoomMult();
    }
    
    public float uiToSceneY(final float n, final float n2) {
        return ((n2 - this.screenHeight() / 2.0f) * -1.0f - this.m_view_y) / this.zoomMult();
    }
    
    public Vector3f uiToScene(final float n, final float n2, final float n3, final Vector3f vector3f) {
        this.uiToScene(null, n, n2, n3, vector3f);
        switch (this.m_view) {
            case Left:
            case Right: {
                vector3f.x = 0.0f;
                break;
            }
            case Top:
            case Bottom: {
                vector3f.y = 0.0f;
                break;
            }
            case Front:
            case Back: {
                vector3f.z = 0.0f;
                break;
            }
        }
        return vector3f;
    }
    
    public Vector3f uiToScene(final Matrix4f matrix4f, final float n, float n2, final float n3, final Vector3f vector3f) {
        n2 = this.screenHeight() - n2;
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)this.m_projection);
        allocMatrix4f.mul((Matrix4fc)this.m_modelView);
        if (matrix4f != null) {
            allocMatrix4f.mul((Matrix4fc)matrix4f);
        }
        allocMatrix4f.invert();
        this.m_viewport[2] = this.screenWidth();
        this.m_viewport[3] = this.screenHeight();
        allocMatrix4f.unprojectInv(n, n2, n3, this.m_viewport, vector3f);
        releaseMatrix4f(allocMatrix4f);
        return vector3f;
    }
    
    public float sceneToUIX(final float n, final float n2, final float n3) {
        this.tempVector4f.set(n, n2, n3, 1.0f);
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)this.m_projection);
        allocMatrix4f.mul((Matrix4fc)this.m_modelView);
        this.m_viewport[2] = this.screenWidth();
        this.m_viewport[3] = this.screenHeight();
        allocMatrix4f.project(n, n2, n3, this.m_viewport, this.tempVector3f);
        releaseMatrix4f(allocMatrix4f);
        return this.tempVector3f.x();
    }
    
    public float sceneToUIY(final float n, final float n2, final float n3) {
        this.tempVector4f.set(n, n2, n3, 1.0f);
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)this.m_projection);
        allocMatrix4f.mul((Matrix4fc)this.m_modelView);
        allocMatrix4f.project(n, n2, n3, new int[] { 0, 0, this.screenWidth(), this.screenHeight() }, this.tempVector3f);
        releaseMatrix4f(allocMatrix4f);
        return this.screenHeight() - this.tempVector3f.y();
    }
    
    private void renderGridXY(final int n) {
        for (int i = -5; i < 5; ++i) {
            for (int j = 1; j < n; ++j) {
                UI3DScene.vboLines.addLine(i + j / (float)n, -5.0f, 0.0f, i + j / (float)n, 5.0f, 0.0f, 0.2f, 0.2f, 0.2f, this.GRID_ALPHA);
            }
        }
        for (int k = -5; k < 5; ++k) {
            for (int l = 1; l < n; ++l) {
                UI3DScene.vboLines.addLine(-5.0f, k + l / (float)n, 0.0f, 5.0f, k + l / (float)n, 0.0f, 0.2f, 0.2f, 0.2f, this.GRID_ALPHA);
            }
        }
        for (int n2 = -5; n2 <= 5; ++n2) {
            UI3DScene.vboLines.addLine((float)n2, -5.0f, 0.0f, (float)n2, 5.0f, 0.0f, 0.1f, 0.1f, 0.1f, this.GRID_ALPHA);
        }
        for (int n3 = -5; n3 <= 5; ++n3) {
            UI3DScene.vboLines.addLine(-5.0f, (float)n3, 0.0f, 5.0f, (float)n3, 0.0f, 0.1f, 0.1f, 0.1f, this.GRID_ALPHA);
        }
        if (this.m_bDrawGridAxes) {
            final int n4 = 0;
            UI3DScene.vboLines.addLine(-5.0f, 0.0f, (float)n4, 5.0f, 0.0f, (float)n4, 1.0f, 0.0f, 0.0f, this.GRID_ALPHA);
            final int n5 = 0;
            UI3DScene.vboLines.addLine(0.0f, -5.0f, (float)n5, 0.0f, 5.0f, (float)n5, 0.0f, 1.0f, 0.0f, this.GRID_ALPHA);
        }
    }
    
    private void renderGridXZ(final int n) {
        for (int i = -5; i < 5; ++i) {
            for (int j = 1; j < n; ++j) {
                UI3DScene.vboLines.addLine(i + j / (float)n, 0.0f, -5.0f, i + j / (float)n, 0.0f, 5.0f, 0.2f, 0.2f, 0.2f, this.GRID_ALPHA);
            }
        }
        for (int k = -5; k < 5; ++k) {
            for (int l = 1; l < n; ++l) {
                UI3DScene.vboLines.addLine(-5.0f, 0.0f, k + l / (float)n, 5.0f, 0.0f, k + l / (float)n, 0.2f, 0.2f, 0.2f, this.GRID_ALPHA);
            }
        }
        for (int n2 = -5; n2 <= 5; ++n2) {
            UI3DScene.vboLines.addLine((float)n2, 0.0f, -5.0f, (float)n2, 0.0f, 5.0f, 0.1f, 0.1f, 0.1f, this.GRID_ALPHA);
        }
        for (int n3 = -5; n3 <= 5; ++n3) {
            UI3DScene.vboLines.addLine(-5.0f, 0.0f, (float)n3, 5.0f, 0.0f, (float)n3, 0.1f, 0.1f, 0.1f, this.GRID_ALPHA);
        }
        if (this.m_bDrawGridAxes) {
            final int n4 = 0;
            UI3DScene.vboLines.addLine(-5.0f, 0.0f, (float)n4, 5.0f, 0.0f, (float)n4, 1.0f, 0.0f, 0.0f, this.GRID_ALPHA);
            final int n5 = 0;
            UI3DScene.vboLines.addLine((float)n5, 0.0f, -5.0f, (float)n5, 0.0f, 5.0f, 0.0f, 0.0f, 1.0f, this.GRID_ALPHA);
        }
    }
    
    private void renderGridYZ(final int n) {
        for (int i = -5; i < 5; ++i) {
            for (int j = 1; j < n; ++j) {
                UI3DScene.vboLines.addLine(0.0f, i + j / (float)n, -5.0f, 0.0f, i + j / (float)n, 5.0f, 0.2f, 0.2f, 0.2f, this.GRID_ALPHA);
            }
        }
        for (int k = -5; k < 5; ++k) {
            for (int l = 1; l < n; ++l) {
                UI3DScene.vboLines.addLine(0.0f, -5.0f, k + l / (float)n, 0.0f, 5.0f, k + l / (float)n, 0.2f, 0.2f, 0.2f, this.GRID_ALPHA);
            }
        }
        for (int n2 = -5; n2 <= 5; ++n2) {
            UI3DScene.vboLines.addLine(0.0f, (float)n2, -5.0f, 0.0f, (float)n2, 5.0f, 0.1f, 0.1f, 0.1f, this.GRID_ALPHA);
        }
        for (int n3 = -5; n3 <= 5; ++n3) {
            UI3DScene.vboLines.addLine(0.0f, -5.0f, (float)n3, 0.0f, 5.0f, (float)n3, 0.1f, 0.1f, 0.1f, this.GRID_ALPHA);
        }
        if (this.m_bDrawGridAxes) {
            final int n4 = 0;
            UI3DScene.vboLines.addLine(0.0f, -5.0f, (float)n4, 0.0f, 5.0f, (float)n4, 0.0f, 1.0f, 0.0f, this.GRID_ALPHA);
            final int n5 = 0;
            UI3DScene.vboLines.addLine((float)n5, 0.0f, -5.0f, (float)n5, 0.0f, 5.0f, 0.0f, 0.0f, 1.0f, this.GRID_ALPHA);
        }
    }
    
    private void renderGrid() {
        UI3DScene.vboLines.setLineWidth(1.0f);
        this.GRID_ALPHA = 1.0f;
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME > currentTimeMillis) {
            this.GRID_ALPHA = 1.0f - (this.m_viewChangeTime + this.VIEW_CHANGE_TIME - currentTimeMillis) / (float)this.VIEW_CHANGE_TIME;
            this.GRID_ALPHA *= this.GRID_ALPHA;
        }
        switch (this.m_view) {
            case Left:
            case Right: {
                this.renderGridYZ(10);
            }
            case Front:
            case Back: {
                this.renderGridXY(10);
            }
            case Top:
            case Bottom: {
                this.renderGridXZ(10);
            }
            default: {
                switch (this.m_gridPlane) {
                    case XY: {
                        this.renderGridXY(10);
                        break;
                    }
                    case XZ: {
                        this.renderGridXZ(10);
                        break;
                    }
                    case YZ: {
                        this.renderGridYZ(10);
                        break;
                    }
                }
            }
        }
    }
    
    void renderAxis(final PositionRotation positionRotation) {
        this.renderAxis(positionRotation.pos, positionRotation.rot);
    }
    
    void renderAxis(final Vector3f vector3f, final Vector3f vector3f2) {
        final StateData stateDataRender = this.stateDataRender();
        UI3DScene.vboLines.flush();
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)stateDataRender.m_gizmoParentTransform);
        allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoOriginTransform);
        allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoChildTransform);
        allocMatrix4f.translate((Vector3fc)vector3f);
        allocMatrix4f.rotateXYZ(vector3f2.x * 0.017453292f, vector3f2.y * 0.017453292f, vector3f2.z * 0.017453292f);
        stateDataRender.m_modelView.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
        PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
        releaseMatrix4f(allocMatrix4f);
        final float n = 0.1f;
        UI3DScene.vboLines.setLineWidth(3.0f);
        UI3DScene.vboLines.addLine(0.0f, 0.0f, 0.0f, n, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
        UI3DScene.vboLines.addLine(0.0f, 0.0f, 0.0f, 0.0f, 0.0f + n, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f);
        UI3DScene.vboLines.addLine(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f + n, 0.0f, 0.0f, 1.0f, 1.0f);
        UI3DScene.vboLines.flush();
        PZGLUtil.popMatrix(5888);
    }
    
    private void renderAABB(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        final float n10 = n4 / 2.0f;
        final float n11 = n5 / 2.0f;
        final float n12 = n6 / 2.0f;
        UI3DScene.vboLines.setOffset(n, n2, n3);
        UI3DScene.vboLines.setLineWidth(1.0f);
        final float n13 = 1.0f;
        UI3DScene.vboLines.addLine(n10, n11, n12, -n10, n11, n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, n11, n12, n10, -n11, n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, n11, n12, n10, n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(-n10, n11, n12, -n10, -n11, n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(-n10, n11, n12, -n10, n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, n11, -n12, n10, -n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, n11, -n12, -n10, n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(-n10, n11, -n12, -n10, -n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, -n11, -n12, -n10, -n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, -n11, n12, n10, -n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(-n10, -n11, n12, -n10, -n11, -n12, n7, n8, n9, n13);
        UI3DScene.vboLines.addLine(n10, -n11, n12, -n10, -n11, n12, n7, n8, n9, n13);
        UI3DScene.vboLines.setOffset(0.0f, 0.0f, 0.0f);
    }
    
    private void renderBox3D(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        final StateData stateDataRender = this.stateDataRender();
        UI3DScene.vboLines.flush();
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.identity();
        allocMatrix4f.translate(n, n2, n3);
        allocMatrix4f.rotateXYZ(n7 * 0.017453292f, n8 * 0.017453292f, n9 * 0.017453292f);
        stateDataRender.m_modelView.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
        PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
        releaseMatrix4f(allocMatrix4f);
        this.renderAABB(n * 0.0f, n2 * 0.0f, n3 * 0.0f, n4, n5, n6, n10, n11, n12);
        UI3DScene.vboLines.flush();
        PZGLUtil.popMatrix(5888);
    }
    
    private void calcMatrices(final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        final float n = 1366.0f / this.screenWidth();
        final float n2 = this.screenHeight() * n;
        final float n3 = 1366.0f / this.zoomMult();
        final float n4 = n2 / this.zoomMult();
        matrix4f.setOrtho(-n3 / 2.0f, n3 / 2.0f, -n4 / 2.0f, n4 / 2.0f, -10.0f, 10.0f);
        matrix4f.translate(-(this.m_view_x / this.zoomMult() * n), this.m_view_y / this.zoomMult() * n, 0.0f);
        matrix4f2.identity();
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;
        switch (this.m_view) {
            case Left: {
                y = 270.0f;
                break;
            }
            case Right: {
                y = 90.0f;
            }
            case Back: {
                y = 180.0f;
                break;
            }
            case Top: {
                y = 90.0f;
                z = 90.0f;
                break;
            }
            case Bottom: {
                y = 90.0f;
                z = 270.0f;
                break;
            }
            case UserDefined: {
                x = this.m_viewRotation.x;
                y = this.m_viewRotation.y;
                z = this.m_viewRotation.z;
                break;
            }
        }
        matrix4f2.rotateXYZ(x * 0.017453292f, y * 0.017453292f, z * 0.017453292f);
    }
    
    Ray getCameraRay(final float n, final float n2, final Ray ray) {
        return this.getCameraRay(n, n2, this.m_projection, this.m_modelView, ray);
    }
    
    Ray getCameraRay(final float n, final float n2, final Matrix4f matrix4f, final Matrix4f matrix4f2, final Ray ray) {
        final Matrix4f allocMatrix4f = allocMatrix4f();
        allocMatrix4f.set((Matrix4fc)matrix4f);
        allocMatrix4f.mul((Matrix4fc)matrix4f2);
        allocMatrix4f.invert();
        this.m_viewport[2] = this.screenWidth();
        this.m_viewport[3] = this.screenHeight();
        final Vector3f unprojectInv = allocMatrix4f.unprojectInv(n, n2, 0.0f, this.m_viewport, allocVector3f());
        final Vector3f unprojectInv2 = allocMatrix4f.unprojectInv(n, n2, 1.0f, this.m_viewport, allocVector3f());
        ray.origin.set((Vector3fc)unprojectInv);
        ray.direction.set((Vector3fc)unprojectInv2.sub((Vector3fc)unprojectInv).normalize());
        releaseVector3f(unprojectInv2);
        releaseVector3f(unprojectInv);
        releaseMatrix4f(allocMatrix4f);
        return ray;
    }
    
    float closest_distance_between_lines(final Ray ray, final Ray ray2) {
        final Vector3f set = allocVector3f().set((Vector3fc)ray.direction);
        final Vector3f set2 = allocVector3f().set((Vector3fc)ray2.direction);
        final Vector3f sub = allocVector3f().set((Vector3fc)ray.origin).sub((Vector3fc)ray2.origin);
        final float dot = set.dot((Vector3fc)set);
        final float dot2 = set.dot((Vector3fc)set2);
        final float dot3 = set2.dot((Vector3fc)set2);
        final float dot4 = set.dot((Vector3fc)sub);
        final float dot5 = set2.dot((Vector3fc)sub);
        final float n = dot * dot3 - dot2 * dot2;
        float t;
        float t2;
        if (n < 1.0E-8f) {
            t = 0.0f;
            t2 = ((dot2 > dot3) ? (dot4 / dot2) : (dot5 / dot3));
        }
        else {
            t = (dot2 * dot5 - dot3 * dot4) / n;
            t2 = (dot * dot5 - dot2 * dot4) / n;
        }
        final Vector3f sub2 = sub.add((Vector3fc)set.mul(t)).sub((Vector3fc)set2.mul(t2));
        ray.t = t;
        ray2.t = t2;
        releaseVector3f(set);
        releaseVector3f(set2);
        releaseVector3f(sub);
        return sub2.length();
    }
    
    Vector3f project(final Vector3f vector3f, final Vector3f vector3f2, final Vector3f vector3f3) {
        return vector3f3.set((Vector3fc)vector3f2).mul(vector3f.dot((Vector3fc)vector3f2) / vector3f2.dot((Vector3fc)vector3f2));
    }
    
    Vector3f reject(final Vector3f vector3f, final Vector3f vector3f2, final Vector3f vector3f3) {
        final Vector3f project = this.project(vector3f, vector3f2, allocVector3f());
        vector3f3.set((Vector3fc)vector3f).sub((Vector3fc)project);
        releaseVector3f(project);
        return vector3f3;
    }
    
    int intersect_ray_plane(final Plane plane, final Ray ray, final Vector3f vector3f) {
        final Vector3f mul = allocVector3f().set((Vector3fc)ray.direction).mul(100.0f);
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
    
    float distance_between_point_ray(final Vector3f vector3f, final Ray ray) {
        final Vector3f mul = allocVector3f().set((Vector3fc)ray.direction).mul(100.0f);
        final Vector3f sub = allocVector3f().set((Vector3fc)vector3f).sub((Vector3fc)ray.origin);
        final float length = mul.mul(sub.dot((Vector3fc)mul) / mul.dot((Vector3fc)mul)).add((Vector3fc)ray.origin).sub((Vector3fc)vector3f).length();
        releaseVector3f(sub);
        releaseVector3f(mul);
        return length;
    }
    
    float closest_distance_line_circle(final Ray ray, final Circle circle, final Vector3f vector3f) {
        final Plane set = allocPlane().set(circle.orientation, circle.center);
        final Vector3f allocVector3f = allocVector3f();
        float n;
        if (this.intersect_ray_plane(set, ray, allocVector3f) == 1) {
            vector3f.set((Vector3fc)allocVector3f).sub((Vector3fc)circle.center).normalize().mul(circle.radius).add((Vector3fc)circle.center);
            n = allocVector3f.sub((Vector3fc)vector3f).length();
        }
        else {
            final Vector3f sub = allocVector3f().set((Vector3fc)ray.origin).sub((Vector3fc)circle.center);
            final Vector3f reject = this.reject(sub, circle.orientation, allocVector3f());
            vector3f.set((Vector3fc)reject.normalize().mul(circle.radius).add((Vector3fc)circle.center));
            n = this.distance_between_point_ray(vector3f, ray);
            releaseVector3f(reject);
            releaseVector3f(sub);
        }
        releaseVector3f(allocVector3f);
        releasePlane(set);
        return n;
    }
    
    private StateData stateDataMain() {
        return this.m_stateData[SpriteRenderer.instance.getMainStateIndex()];
    }
    
    private StateData stateDataRender() {
        return this.m_stateData[SpriteRenderer.instance.getRenderStateIndex()];
    }
    
    static {
        s_SetModelCameraPool = new ObjectPool<SetModelCamera>(SetModelCamera::new);
        s_posRotPool = new ObjectPool<PositionRotation>(PositionRotation::new);
        s_aabbPool = new ObjectPool<AABB>(AABB::new);
        s_box3DPool = new ObjectPool<Box3D>(Box3D::new);
        vboLines = new VBOLines();
        TL_Ray_pool = ThreadLocal.withInitial((Supplier<? extends ObjectPool<Ray>>)RayObjectPool::new);
        TL_Plane_pool = ThreadLocal.withInitial((Supplier<? extends ObjectPool<Plane>>)PlaneObjectPool::new);
    }
    
    private enum GridPlane
    {
        XY, 
        XZ, 
        YZ;
        
        private static /* synthetic */ GridPlane[] $values() {
            return new GridPlane[] { GridPlane.XY, GridPlane.XZ, GridPlane.YZ };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private enum View
    {
        Left, 
        Right, 
        Top, 
        Bottom, 
        Front, 
        Back, 
        UserDefined;
        
        private static /* synthetic */ View[] $values() {
            return new View[] { View.Left, View.Right, View.Top, View.Bottom, View.Front, View.Back, View.UserDefined };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private enum TransformMode
    {
        Global, 
        Local;
        
        private static /* synthetic */ TransformMode[] $values() {
            return new TransformMode[] { TransformMode.Global, TransformMode.Local };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private abstract static class SceneObject
    {
        final UI3DScene m_scene;
        final String m_id;
        boolean m_visible;
        final Vector3f m_translate;
        final Vector3f m_rotate;
        SceneObject m_parent;
        String m_attachment;
        String m_parentAttachment;
        boolean m_autoRotate;
        float m_autoRotateAngle;
        
        SceneObject(final UI3DScene scene, final String id) {
            this.m_visible = true;
            this.m_translate = new Vector3f();
            this.m_rotate = new Vector3f();
            this.m_autoRotate = false;
            this.m_autoRotateAngle = 0.0f;
            this.m_scene = scene;
            this.m_id = id;
        }
        
        abstract SceneObjectRenderData renderMain();
        
        Matrix4f getLocalTransform(final Matrix4f matrix4f) {
            matrix4f.translation((Vector3fc)this.m_translate);
            float y = this.m_rotate.y;
            if (this.m_autoRotate) {
                y += this.m_autoRotateAngle;
            }
            matrix4f.rotateXYZ(this.m_rotate.x * 0.017453292f, y * 0.017453292f, this.m_rotate.z * 0.017453292f);
            if (this.m_attachment != null) {
                final Matrix4f attachmentTransform = this.getAttachmentTransform(this.m_attachment, UI3DScene.allocMatrix4f());
                attachmentTransform.invert();
                matrix4f.mul((Matrix4fc)attachmentTransform);
                UI3DScene.releaseMatrix4f(attachmentTransform);
            }
            return matrix4f;
        }
        
        Matrix4f getGlobalTransform(final Matrix4f matrix4f) {
            this.getLocalTransform(matrix4f);
            if (this.m_parent != null) {
                if (this.m_parentAttachment != null) {
                    final Matrix4f attachmentTransform = this.m_parent.getAttachmentTransform(this.m_parentAttachment, UI3DScene.allocMatrix4f());
                    attachmentTransform.mul((Matrix4fc)matrix4f, matrix4f);
                    UI3DScene.releaseMatrix4f(attachmentTransform);
                }
                final Matrix4f globalTransform = this.m_parent.getGlobalTransform(UI3DScene.allocMatrix4f());
                globalTransform.mul((Matrix4fc)matrix4f, matrix4f);
                UI3DScene.releaseMatrix4f(globalTransform);
            }
            return matrix4f;
        }
        
        Matrix4f getAttachmentTransform(final String s, final Matrix4f matrix4f) {
            matrix4f.identity();
            return matrix4f;
        }
    }
    
    private static final class OriginGizmo extends SceneObject
    {
        OriginGizmo(final UI3DScene ui3DScene) {
            super(ui3DScene, "OriginGizmo");
        }
        
        @Override
        SceneObjectRenderData renderMain() {
            return null;
        }
    }
    
    private static final class OriginAttachment extends SceneObject
    {
        SceneObject m_object;
        String m_attachmentName;
        
        OriginAttachment(final UI3DScene ui3DScene) {
            super(ui3DScene, "OriginAttachment");
        }
        
        @Override
        SceneObjectRenderData renderMain() {
            return null;
        }
        
        @Override
        Matrix4f getGlobalTransform(final Matrix4f matrix4f) {
            return this.m_object.getAttachmentTransform(this.m_attachmentName, matrix4f);
        }
    }
    
    private static final class OriginBone extends SceneObject
    {
        SceneCharacter m_character;
        String m_boneName;
        
        OriginBone(final UI3DScene ui3DScene) {
            super(ui3DScene, "OriginBone");
        }
        
        @Override
        SceneObjectRenderData renderMain() {
            return null;
        }
        
        @Override
        Matrix4f getGlobalTransform(final Matrix4f matrix4f) {
            return this.m_character.getBoneMatrix(this.m_boneName, matrix4f);
        }
    }
    
    private static final class SceneCharacter extends SceneObject
    {
        final AnimatedModel m_animatedModel;
        boolean m_bShowBones;
        boolean m_bClearDepthBuffer;
        boolean m_bUseDeferredMovement;
        
        SceneCharacter(final UI3DScene ui3DScene, final String s) {
            super(ui3DScene, s);
            this.m_bShowBones = false;
            this.m_bClearDepthBuffer = true;
            this.m_bUseDeferredMovement = false;
            (this.m_animatedModel = new AnimatedModel()).setAnimSetName("player-vehicle");
            this.m_animatedModel.setState("idle");
            this.m_animatedModel.setOutfitName("Naked", false, false);
            this.m_animatedModel.getHumanVisual().setHairModel("Bald");
            this.m_animatedModel.getHumanVisual().setBeardModel("");
            this.m_animatedModel.getHumanVisual().setSkinTextureIndex(0);
            this.m_animatedModel.setAlpha(0.5f);
            this.m_animatedModel.setAnimate(false);
        }
        
        @Override
        SceneObjectRenderData renderMain() {
            this.m_animatedModel.update();
            final CharacterRenderData characterRenderData = CharacterRenderData.s_pool.alloc();
            characterRenderData.initCharacter(this);
            SpriteRenderer.instance.drawGeneric(characterRenderData.m_drawer);
            return characterRenderData;
        }
        
        @Override
        Matrix4f getLocalTransform(final Matrix4f matrix4f) {
            matrix4f.identity();
            matrix4f.rotateY(3.1415927f);
            matrix4f.translate(-this.m_translate.x, this.m_translate.y, this.m_translate.z);
            matrix4f.scale(-1.5f, 1.5f, 1.5f);
            float y = this.m_rotate.y;
            if (this.m_autoRotate) {
                y += this.m_autoRotateAngle;
            }
            matrix4f.rotateXYZ(this.m_rotate.x * 0.017453292f, y * 0.017453292f, this.m_rotate.z * 0.017453292f);
            if (this.m_animatedModel.getAnimationPlayer().getMultiTrack().getTracks().isEmpty()) {
                return matrix4f;
            }
            if (this.m_bUseDeferredMovement) {
                final AnimationMultiTrack multiTrack = this.m_animatedModel.getAnimationPlayer().getMultiTrack();
                multiTrack.getTracks().get(0).getCurrentDeferredRotation();
                final org.lwjgl.util.vector.Vector3f vector3f = new org.lwjgl.util.vector.Vector3f();
                multiTrack.getTracks().get(0).getCurrentDeferredPosition(vector3f);
                matrix4f.translate(vector3f.x, vector3f.y, vector3f.z);
            }
            return matrix4f;
        }
        
        @Override
        Matrix4f getAttachmentTransform(final String s, final Matrix4f matrix4f) {
            matrix4f.identity();
            final ModelScript modelScript = ScriptManager.instance.getModelScript(this.m_animatedModel.isFemale() ? "FemaleBody" : "MaleBody");
            if (modelScript == null) {
                return matrix4f;
            }
            final ModelAttachment attachmentById = modelScript.getAttachmentById(s);
            if (attachmentById == null) {
                return matrix4f;
            }
            matrix4f.translation((Vector3fc)attachmentById.getOffset());
            final Vector3f rotate = attachmentById.getRotate();
            matrix4f.rotateXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
            if (attachmentById.getBone() != null) {
                final Matrix4f boneMatrix = this.getBoneMatrix(attachmentById.getBone(), UI3DScene.allocMatrix4f());
                boneMatrix.mul((Matrix4fc)matrix4f, matrix4f);
                UI3DScene.releaseMatrix4f(boneMatrix);
            }
            return matrix4f;
        }
        
        int hitTestBone(final int n, final Ray ray, final Ray ray2, final Matrix4f matrix4f) {
            final AnimationPlayer animationPlayer = this.m_animatedModel.getAnimationPlayer();
            final int intValue = animationPlayer.getSkinningData().SkeletonHierarchy.get(n);
            if (intValue == -1) {
                return -1;
            }
            final org.lwjgl.util.vector.Matrix4f matrix4f2 = animationPlayer.modelTransforms[intValue];
            ray.origin.set(matrix4f2.m03, matrix4f2.m13, matrix4f2.m23);
            matrix4f.transformPosition(ray.origin);
            final org.lwjgl.util.vector.Matrix4f matrix4f3 = animationPlayer.modelTransforms[n];
            final Vector3f allocVector3f = UI3DScene.allocVector3f();
            allocVector3f.set(matrix4f3.m03, matrix4f3.m13, matrix4f3.m23);
            matrix4f.transformPosition(allocVector3f);
            ray.direction.set((Vector3fc)allocVector3f).sub((Vector3fc)ray.origin);
            final float length = ray.direction.length();
            ray.direction.normalize();
            this.m_scene.closest_distance_between_lines(ray2, ray);
            final float sceneToUIX = this.m_scene.sceneToUIX(ray2.origin.x + ray2.direction.x * ray2.t, ray2.origin.y + ray2.direction.y * ray2.t, ray2.origin.z + ray2.direction.z * ray2.t);
            final float sceneToUIY = this.m_scene.sceneToUIY(ray2.origin.x + ray2.direction.x * ray2.t, ray2.origin.y + ray2.direction.y * ray2.t, ray2.origin.z + ray2.direction.z * ray2.t);
            final float sceneToUIX2 = this.m_scene.sceneToUIX(ray.origin.x + ray.direction.x * ray.t, ray.origin.y + ray.direction.y * ray.t, ray.origin.z + ray.direction.z * ray.t);
            final float sceneToUIY2 = this.m_scene.sceneToUIY(ray.origin.x + ray.direction.x * ray.t, ray.origin.y + ray.direction.y * ray.t, ray.origin.z + ray.direction.z * ray.t);
            int n2 = -1;
            if ((float)Math.sqrt(Math.pow(sceneToUIX2 - sceneToUIX, 2.0) + Math.pow(sceneToUIY2 - sceneToUIY, 2.0)) < 10.0f) {
                if (ray.t >= 0.0f && ray.t < length * 0.5f) {
                    n2 = intValue;
                }
                else if (ray.t >= length * 0.5f && ray.t < length) {
                    n2 = n;
                }
            }
            UI3DScene.releaseVector3f(allocVector3f);
            return n2;
        }
        
        String pickBone(final float n, float n2) {
            if (this.m_animatedModel.getAnimationPlayer().modelTransforms == null) {
                return "";
            }
            n2 = this.m_scene.screenHeight() - n2;
            final Ray cameraRay = this.m_scene.getCameraRay(n, n2, UI3DScene.allocRay());
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            this.getLocalTransform(allocMatrix4f);
            final Ray allocRay = UI3DScene.allocRay();
            int hitTestBone = -1;
            for (int i = 0; i < this.m_animatedModel.getAnimationPlayer().modelTransforms.length; ++i) {
                hitTestBone = this.hitTestBone(i, allocRay, cameraRay, allocMatrix4f);
                if (hitTestBone != -1) {
                    break;
                }
            }
            UI3DScene.releaseRay(allocRay);
            UI3DScene.releaseRay(cameraRay);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            return (hitTestBone == -1) ? "" : this.m_animatedModel.getAnimationPlayer().getSkinningData().getBoneAt(hitTestBone).Name;
        }
        
        Matrix4f getBoneMatrix(final String s, Matrix4f convertMatrix) {
            convertMatrix.identity();
            if (this.m_animatedModel.getAnimationPlayer().modelTransforms == null) {
                return convertMatrix;
            }
            final SkinningBone bone = this.m_animatedModel.getAnimationPlayer().getSkinningData().getBone(s);
            if (bone == null) {
                return convertMatrix;
            }
            convertMatrix = PZMath.convertMatrix(this.m_animatedModel.getAnimationPlayer().modelTransforms[bone.Index], convertMatrix);
            convertMatrix.transpose();
            return convertMatrix;
        }
        
        PositionRotation getBoneAxis(final String s, final PositionRotation positionRotation) {
            final Matrix4f identity = UI3DScene.allocMatrix4f().identity();
            identity.getTranslation(positionRotation.pos);
            UI3DScene.releaseMatrix4f(identity);
            final Quaternionf unnormalizedRotation = identity.getUnnormalizedRotation(UI3DScene.allocQuaternionf());
            unnormalizedRotation.getEulerAnglesXYZ(positionRotation.rot);
            UI3DScene.releaseQuaternionf(unnormalizedRotation);
            return positionRotation;
        }
    }
    
    private static final class SceneModel extends SceneObject
    {
        ModelScript m_modelScript;
        Model m_model;
        
        SceneModel(final UI3DScene ui3DScene, final String s, final ModelScript modelScript, final Model model) {
            super(ui3DScene, s);
            Objects.requireNonNull(modelScript);
            Objects.requireNonNull(model);
            this.m_modelScript = modelScript;
            this.m_model = model;
        }
        
        @Override
        SceneObjectRenderData renderMain() {
            if (!this.m_model.isReady()) {
                return null;
            }
            final ModelRenderData modelRenderData = ModelRenderData.s_pool.alloc();
            modelRenderData.initModel(this);
            SpriteRenderer.instance.drawGeneric(modelRenderData.m_drawer);
            return modelRenderData;
        }
        
        @Override
        Matrix4f getLocalTransform(final Matrix4f matrix4f) {
            super.getLocalTransform(matrix4f);
            return matrix4f;
        }
        
        @Override
        Matrix4f getAttachmentTransform(final String s, final Matrix4f matrix4f) {
            matrix4f.identity();
            final ModelAttachment attachmentById = this.m_modelScript.getAttachmentById(s);
            if (attachmentById == null) {
                return matrix4f;
            }
            matrix4f.translation((Vector3fc)attachmentById.getOffset());
            final Vector3f rotate = attachmentById.getRotate();
            matrix4f.rotateXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
            return matrix4f;
        }
    }
    
    private static final class SceneVehicle extends SceneObject
    {
        String m_scriptName;
        VehicleScript m_script;
        Model m_model;
        
        SceneVehicle(final UI3DScene ui3DScene, final String s) {
            super(ui3DScene, s);
            this.setScriptName(this.m_scriptName = "Base.ModernCar");
        }
        
        @Override
        SceneObjectRenderData renderMain() {
            if (this.m_script == null) {
                this.m_model = null;
                return null;
            }
            this.m_model = ModelManager.instance.getLoadedModel(this.m_script.getModel().file);
            if (this.m_model == null) {
                return null;
            }
            if (this.m_script.getSkinCount() > 0) {
                this.m_model.tex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.m_script.getSkin(0).texture));
            }
            final VehicleRenderData vehicleRenderData = VehicleRenderData.s_pool.alloc();
            vehicleRenderData.initVehicle(this);
            SpriteRenderer.instance.drawGeneric(UI3DScene.s_SetModelCameraPool.alloc().init(this.m_scene.m_VehicleSceneModelCamera, vehicleRenderData));
            SpriteRenderer.instance.drawGeneric(vehicleRenderData.m_drawer);
            return vehicleRenderData;
        }
        
        void setScriptName(final String scriptName) {
            this.m_scriptName = scriptName;
            this.m_script = ScriptManager.instance.getVehicle(scriptName);
        }
    }
    
    private static class SceneObjectRenderData
    {
        SceneObject m_object;
        final Matrix4f m_transform;
        private static final ObjectPool<SceneObjectRenderData> s_pool;
        
        private SceneObjectRenderData() {
            this.m_transform = new Matrix4f();
        }
        
        SceneObjectRenderData init(final SceneObject object) {
            (this.m_object = object).getGlobalTransform(this.m_transform);
            return this;
        }
        
        void release() {
            SceneObjectRenderData.s_pool.release(this);
        }
        
        static {
            s_pool = new ObjectPool<SceneObjectRenderData>(SceneObjectRenderData::new);
        }
    }
    
    private static class CharacterRenderData extends SceneObjectRenderData
    {
        final CharacterDrawer m_drawer;
        private static final ObjectPool<CharacterRenderData> s_pool;
        
        private CharacterRenderData() {
            this.m_drawer = new CharacterDrawer();
        }
        
        SceneObjectRenderData initCharacter(final SceneCharacter sceneCharacter) {
            this.m_drawer.init(sceneCharacter, this);
            super.init(sceneCharacter);
            return this;
        }
        
        @Override
        void release() {
            CharacterRenderData.s_pool.release(this);
        }
        
        static {
            s_pool = new ObjectPool<CharacterRenderData>(CharacterRenderData::new);
        }
    }
    
    private static class ModelRenderData extends SceneObjectRenderData
    {
        final ModelDrawer m_drawer;
        private static final ObjectPool<ModelRenderData> s_pool;
        
        private ModelRenderData() {
            this.m_drawer = new ModelDrawer();
        }
        
        SceneObjectRenderData initModel(final SceneModel sceneModel) {
            super.init(sceneModel);
            if (sceneModel.m_model.isReady() && sceneModel.m_model.Mesh.m_transform != null) {
                sceneModel.m_model.Mesh.m_transform.transpose();
                this.m_transform.mul((Matrix4fc)sceneModel.m_model.Mesh.m_transform);
                sceneModel.m_model.Mesh.m_transform.transpose();
            }
            if (sceneModel.m_modelScript != null && sceneModel.m_modelScript.scale != 1.0f) {
                this.m_transform.scale(sceneModel.m_modelScript.scale);
            }
            this.m_drawer.init(sceneModel, this);
            return this;
        }
        
        @Override
        void release() {
            ModelRenderData.s_pool.release(this);
        }
        
        static {
            s_pool = new ObjectPool<ModelRenderData>(ModelRenderData::new);
        }
    }
    
    private static class VehicleRenderData extends SceneObjectRenderData
    {
        final ArrayList<Model> m_models;
        final ArrayList<Matrix4f> m_transforms;
        final VehicleDrawer m_drawer;
        private static final ObjectPool<VehicleRenderData> s_pool;
        
        private VehicleRenderData() {
            this.m_models = new ArrayList<Model>();
            this.m_transforms = new ArrayList<Matrix4f>();
            this.m_drawer = new VehicleDrawer();
        }
        
        SceneObjectRenderData initVehicle(final SceneVehicle sceneVehicle) {
            super.init(sceneVehicle);
            this.m_models.clear();
            BaseVehicle.TL_matrix4f_pool.get().release(this.m_transforms);
            this.m_transforms.clear();
            final VehicleScript script = sceneVehicle.m_script;
            if (script.getModel() == null) {
                return null;
            }
            this.initVehicleModel(sceneVehicle);
            final float modelScale = script.getModelScale();
            final Vector3f offset = script.getModel().getOffset();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.translationRotateScale(offset.x * 1.0f, offset.y, offset.z, 0.0f, 0.0f, 0.0f, 1.0f, modelScale);
            this.m_transform.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
            for (int i = 0; i < script.getPartCount(); ++i) {
                final VehicleScript.Part part = script.getPart(i);
                if (part.wheel != null) {
                    this.initWheelModel(sceneVehicle, part, allocMatrix4f);
                }
            }
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            this.m_drawer.init(sceneVehicle, this);
            return this;
        }
        
        private void initVehicleModel(final SceneVehicle sceneVehicle) {
            final VehicleScript script = sceneVehicle.m_script;
            final float modelScale = script.getModelScale();
            float scale = 1.0f;
            final ModelScript modelScript = ScriptManager.instance.getModelScript(script.getModel().file);
            if (modelScript != null && modelScript.scale != 1.0f) {
                scale = modelScript.scale;
            }
            float n = 1.0f;
            if (modelScript != null) {
                n = (modelScript.invertX ? -1.0f : 1.0f);
            }
            final float n2 = n * -1.0f;
            final Quaternionf allocQuaternionf = UI3DScene.allocQuaternionf();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            final Vector3f rotate = script.getModel().getRotate();
            allocQuaternionf.rotationXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
            final Vector3f offset = script.getModel().getOffset();
            allocMatrix4f.translationRotateScale(offset.x * 1.0f, offset.y, offset.z, allocQuaternionf.x, allocQuaternionf.y, allocQuaternionf.z, allocQuaternionf.w, modelScale * scale * n2, modelScale * scale, modelScale * scale);
            if (sceneVehicle.m_model.Mesh != null && sceneVehicle.m_model.Mesh.isReady() && sceneVehicle.m_model.Mesh.m_transform != null) {
                sceneVehicle.m_model.Mesh.m_transform.transpose();
                allocMatrix4f.mul((Matrix4fc)sceneVehicle.m_model.Mesh.m_transform);
                sceneVehicle.m_model.Mesh.m_transform.transpose();
            }
            this.m_transform.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
            UI3DScene.releaseQuaternionf(allocQuaternionf);
            this.m_models.add(sceneVehicle.m_model);
            this.m_transforms.add(allocMatrix4f);
        }
        
        private void initWheelModel(final SceneVehicle sceneVehicle, final VehicleScript.Part part, final Matrix4f matrix4f) {
            final VehicleScript script = sceneVehicle.m_script;
            final float modelScale = script.getModelScale();
            final VehicleScript.Wheel wheelById = script.getWheelById(part.wheel);
            if (wheelById == null || part.models.isEmpty()) {
                return;
            }
            final VehicleScript.Model model = part.models.get(0);
            final Vector3f offset = model.getOffset();
            final Vector3f rotate = model.getRotate();
            final Model loadedModel = ModelManager.instance.getLoadedModel(model.file);
            if (loadedModel == null) {
                return;
            }
            final float scale = model.scale;
            float scale2 = 1.0f;
            float n = 1.0f;
            final ModelScript modelScript = ScriptManager.instance.getModelScript(model.file);
            if (modelScript != null) {
                scale2 = modelScript.scale;
                n = (modelScript.invertX ? -1.0f : 1.0f);
            }
            final Quaternionf allocQuaternionf = UI3DScene.allocQuaternionf();
            allocQuaternionf.rotationXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.translation(wheelById.offset.x / modelScale * 1.0f, wheelById.offset.y / modelScale, wheelById.offset.z / modelScale);
            final Matrix4f allocMatrix4f2 = UI3DScene.allocMatrix4f();
            allocMatrix4f2.translationRotateScale(offset.x * 1.0f, offset.y, offset.z, allocQuaternionf.x, allocQuaternionf.y, allocQuaternionf.z, allocQuaternionf.w, scale * scale2 * n, scale * scale2, scale * scale2);
            allocMatrix4f.mul((Matrix4fc)allocMatrix4f2);
            UI3DScene.releaseMatrix4f(allocMatrix4f2);
            matrix4f.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
            if (loadedModel.Mesh != null && loadedModel.Mesh.isReady() && loadedModel.Mesh.m_transform != null) {
                loadedModel.Mesh.m_transform.transpose();
                allocMatrix4f.mul((Matrix4fc)loadedModel.Mesh.m_transform);
                loadedModel.Mesh.m_transform.transpose();
            }
            UI3DScene.releaseQuaternionf(allocQuaternionf);
            this.m_models.add(loadedModel);
            this.m_transforms.add(allocMatrix4f);
        }
        
        @Override
        void release() {
            VehicleRenderData.s_pool.release(this);
        }
        
        static {
            s_pool = new ObjectPool<VehicleRenderData>(VehicleRenderData::new);
        }
    }
    
    private static final class AABB
    {
        float x;
        float y;
        float z;
        float w;
        float h;
        float L;
        float r;
        float g;
        float b;
        
        AABB set(final AABB aabb) {
            return this.set(aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.L, aabb.r, aabb.g, aabb.b);
        }
        
        AABB set(final float x, final float y, final float z, final float w, final float h, final float l, final float r, final float g, final float b) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            this.h = h;
            this.L = l;
            this.r = r;
            this.g = g;
            this.b = b;
            return this;
        }
    }
    
    private static final class Box3D
    {
        float x;
        float y;
        float z;
        float w;
        float h;
        float L;
        float rx;
        float ry;
        float rz;
        float r;
        float g;
        float b;
        
        Box3D set(final Box3D box3D) {
            return this.set(box3D.x, box3D.y, box3D.z, box3D.w, box3D.h, box3D.L, box3D.rx, box3D.ry, box3D.rz, box3D.r, box3D.g, box3D.b);
        }
        
        Box3D set(final float x, final float y, final float z, final float w, final float h, final float l, final float rx, final float ry, final float rz, final float r, final float g, final float b) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            this.h = h;
            this.L = l;
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.r = r;
            this.g = g;
            this.b = b;
            return this;
        }
    }
    
    private abstract class SceneModelCamera extends ModelCamera
    {
        SceneObjectRenderData m_renderData;
    }
    
    private final class CharacterSceneModelCamera extends SceneModelCamera
    {
        @Override
        public void Begin() {
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            GL11.glViewport(UI3DScene.this.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(), UI3DScene.this.getWidth().intValue(), UI3DScene.this.getHeight().intValue());
            PZGLUtil.pushAndLoadMatrix(5889, stateDataRender.m_projection);
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataRender.m_modelView);
            allocMatrix4f.mul((Matrix4fc)this.m_renderData.m_transform);
            PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
        }
        
        @Override
        public void End() {
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }
    
    private final class VehicleSceneModelCamera extends SceneModelCamera
    {
        @Override
        public void Begin() {
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            GL11.glViewport(UI3DScene.this.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(), UI3DScene.this.getWidth().intValue(), UI3DScene.this.getHeight().intValue());
            PZGLUtil.pushAndLoadMatrix(5889, stateDataRender.m_projection);
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataRender.m_modelView);
            allocMatrix4f.mul((Matrix4fc)this.m_renderData.m_transform);
            PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            GL11.glDepthRange(0.0, 1.0);
            GL11.glDepthMask(true);
        }
        
        @Override
        public void End() {
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }
    
    private static final class SetModelCamera extends TextureDraw.GenericDrawer
    {
        SceneModelCamera m_camera;
        SceneObjectRenderData m_renderData;
        
        SetModelCamera init(final SceneModelCamera camera, final SceneObjectRenderData renderData) {
            this.m_camera = camera;
            this.m_renderData = renderData;
            return this;
        }
        
        @Override
        public void render() {
            this.m_camera.m_renderData = this.m_renderData;
            ModelCamera.instance = this.m_camera;
        }
        
        @Override
        public void postRender() {
            UI3DScene.s_SetModelCameraPool.release(this);
        }
    }
    
    private final class OverlaysDrawer extends TextureDraw.GenericDrawer
    {
        void init() {
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            UI3DScene.s_aabbPool.release(stateDataMain.m_aabb);
            stateDataMain.m_aabb.clear();
            for (int i = 0; i < UI3DScene.this.m_aabb.size(); ++i) {
                stateDataMain.m_aabb.add(UI3DScene.s_aabbPool.alloc().set(UI3DScene.this.m_aabb.get(i)));
            }
            UI3DScene.s_box3DPool.release(stateDataMain.m_box3D);
            stateDataMain.m_box3D.clear();
            for (int j = 0; j < UI3DScene.this.m_box3D.size(); ++j) {
                stateDataMain.m_box3D.add(UI3DScene.s_box3DPool.alloc().set(UI3DScene.this.m_box3D.get(j)));
            }
            UI3DScene.s_posRotPool.release(stateDataMain.m_axes);
            stateDataMain.m_axes.clear();
            for (int k = 0; k < UI3DScene.this.m_axes.size(); ++k) {
                stateDataMain.m_axes.add(UI3DScene.s_posRotPool.alloc().set(UI3DScene.this.m_axes.get(k)));
            }
        }
        
        @Override
        public void render() {
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            PZGLUtil.pushAndLoadMatrix(5889, stateDataRender.m_projection);
            PZGLUtil.pushAndLoadMatrix(5888, stateDataRender.m_modelView);
            UI3DScene.vboLines.setOffset(0.0f, 0.0f, 0.0f);
            if (UI3DScene.this.m_bDrawGrid) {
                UI3DScene.this.renderGrid();
            }
            for (int i = 0; i < stateDataRender.m_aabb.size(); ++i) {
                final AABB aabb = stateDataRender.m_aabb.get(i);
                UI3DScene.this.renderAABB(aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.L, aabb.r, aabb.g, aabb.b);
            }
            for (int j = 0; j < stateDataRender.m_box3D.size(); ++j) {
                final Box3D box3D = stateDataRender.m_box3D.get(j);
                UI3DScene.this.renderBox3D(box3D.x, box3D.y, box3D.z, box3D.w, box3D.h, box3D.L, box3D.rx, box3D.ry, box3D.rz, box3D.r, box3D.g, box3D.b);
            }
            for (int k = 0; k < stateDataRender.m_axes.size(); ++k) {
                UI3DScene.this.renderAxis(stateDataRender.m_axes.get(k));
            }
            UI3DScene.vboLines.flush();
            if (stateDataRender.m_gizmo != null) {
                stateDataRender.m_gizmo.render();
            }
            UI3DScene.vboLines.flush();
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }
    
    enum Axis
    {
        None, 
        X, 
        Y, 
        Z;
        
        private static /* synthetic */ Axis[] $values() {
            return new Axis[] { Axis.None, Axis.X, Axis.Y, Axis.Z };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static final class PositionRotation
    {
        final Vector3f pos;
        final Vector3f rot;
        
        private PositionRotation() {
            this.pos = new Vector3f();
            this.rot = new Vector3f();
        }
        
        PositionRotation set(final PositionRotation positionRotation) {
            this.pos.set((Vector3fc)positionRotation.pos);
            this.rot.set((Vector3fc)positionRotation.rot);
            return this;
        }
        
        PositionRotation set(final float n, final float n2, final float n3) {
            this.pos.set(n, n2, n3);
            this.rot.set(0.0f, 0.0f, 0.0f);
            return this;
        }
        
        PositionRotation set(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.pos.set(n, n2, n3);
            this.rot.set(n4, n5, n6);
            return this;
        }
    }
    
    private abstract class Gizmo
    {
        float LENGTH;
        float THICKNESS;
        boolean m_visible;
        
        private Gizmo() {
            this.LENGTH = 0.5f;
            this.THICKNESS = 0.05f;
            this.m_visible = false;
        }
        
        abstract Axis hitTest(final float p0, final float p1);
        
        abstract void startTracking(final float p0, final float p1, final Axis p2);
        
        abstract void updateTracking(final float p0, final float p1);
        
        abstract void stopTracking();
        
        abstract void render();
        
        Vector3f getPointOnAxis(final float n, float n2, final Axis axis, final Matrix4f matrix4f, final Vector3f vector3f) {
            UI3DScene.this.stateDataMain();
            n2 = UI3DScene.this.screenHeight() - n2;
            final Ray cameraRay = UI3DScene.this.getCameraRay(n, n2, UI3DScene.allocRay());
            final Ray allocRay = UI3DScene.allocRay();
            matrix4f.transformPosition(allocRay.origin.set(0.0f, 0.0f, 0.0f));
            switch (axis) {
                case X: {
                    allocRay.direction.set(1.0f, 0.0f, 0.0f);
                    break;
                }
                case Y: {
                    allocRay.direction.set(0.0f, 1.0f, 0.0f);
                    break;
                }
                case Z: {
                    allocRay.direction.set(0.0f, 0.0f, 1.0f);
                    break;
                }
            }
            matrix4f.transformDirection(allocRay.direction).normalize();
            UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            UI3DScene.releaseRay(cameraRay);
            vector3f.set((Vector3fc)allocRay.direction).mul(allocRay.t).add((Vector3fc)allocRay.origin);
            UI3DScene.releaseRay(allocRay);
            return vector3f;
        }
        
        boolean hitTestRect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
            final float sceneToUIX = UI3DScene.this.sceneToUIX(n3, n4, n5);
            final float sceneToUIY = UI3DScene.this.sceneToUIY(n3, n4, n5);
            final float sceneToUIX2 = UI3DScene.this.sceneToUIX(n6, n7, n8);
            final float sceneToUIY2 = UI3DScene.this.sceneToUIY(n6, n7, n8);
            final float n9 = this.THICKNESS / 2.0f * UI3DScene.this.zoomMult();
            final float n10 = this.THICKNESS / 2.0f * UI3DScene.this.zoomMult();
            final float min = Math.min(sceneToUIX - n9, sceneToUIX2 - n9);
            final float max = Math.max(sceneToUIX + n9, sceneToUIX2 + n9);
            final float min2 = Math.min(sceneToUIY - n10, sceneToUIY2 - n10);
            final float max2 = Math.max(sceneToUIY + n10, sceneToUIY2 + n10);
            return n >= min && n2 >= min2 && n < max && n2 < max2;
        }
        
        void renderLineToOrigin() {
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            if (!stateDataRender.m_hasGizmoOrigin) {
                return;
            }
            UI3DScene.this.renderAxis(stateDataRender.m_gizmoTranslate, stateDataRender.m_gizmoRotate);
            final Vector3f gizmoTranslate = stateDataRender.m_gizmoTranslate;
            UI3DScene.vboLines.flush();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataRender.m_modelView);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoChildTransform);
            PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            UI3DScene.vboLines.setLineWidth(1.0f);
            UI3DScene.vboLines.addLine(gizmoTranslate.x, gizmoTranslate.y, gizmoTranslate.z, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
            UI3DScene.vboLines.flush();
            PZGLUtil.popMatrix(5888);
        }
    }
    
    public static final class Ray
    {
        public final Vector3f origin;
        public final Vector3f direction;
        public float t;
        
        Ray() {
            this.origin = new Vector3f();
            this.direction = new Vector3f();
        }
        
        Ray(final Ray ray) {
            this.origin = new Vector3f();
            this.direction = new Vector3f();
            this.origin.set((Vector3fc)ray.origin);
            this.direction.set((Vector3fc)ray.direction);
            this.t = ray.t;
        }
    }
    
    public static final class RayObjectPool extends ObjectPool<Ray>
    {
        int allocated;
        
        public RayObjectPool() {
            super(Ray::new);
            this.allocated = 0;
        }
        
        @Override
        protected Ray makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    private static final class Circle
    {
        final Vector3f center;
        final Vector3f orientation;
        float radius;
        
        private Circle() {
            this.center = new Vector3f();
            this.orientation = new Vector3f();
            this.radius = 1.0f;
        }
    }
    
    public static final class Plane
    {
        public final Vector3f point;
        public final Vector3f normal;
        
        public Plane() {
            this.point = new Vector3f();
            this.normal = new Vector3f();
        }
        
        public Plane(final Vector3f vector3f, final Vector3f vector3f2) {
            this.point = new Vector3f();
            this.normal = new Vector3f();
            this.point.set((Vector3fc)vector3f2);
            this.normal.set((Vector3fc)vector3f);
        }
        
        public Plane set(final Vector3f vector3f, final Vector3f vector3f2) {
            this.point.set((Vector3fc)vector3f2);
            this.normal.set((Vector3fc)vector3f);
            return this;
        }
    }
    
    public static final class PlaneObjectPool extends ObjectPool<Plane>
    {
        int allocated;
        
        public PlaneObjectPool() {
            super(Plane::new);
            this.allocated = 0;
        }
        
        @Override
        protected Plane makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    private final class ScaleGizmo extends Gizmo
    {
        final Matrix4f m_startXfrm;
        final Matrix4f m_startInvXfrm;
        final Vector3f m_startPos;
        final Vector3f m_currentPos;
        Axis m_trackAxis;
        boolean m_hideX;
        boolean m_hideY;
        boolean m_hideZ;
        final Cylinder cylinder;
        
        private ScaleGizmo() {
            this.m_startXfrm = new Matrix4f();
            this.m_startInvXfrm = new Matrix4f();
            this.m_startPos = new Vector3f();
            this.m_currentPos = new Vector3f();
            this.m_trackAxis = Axis.None;
            this.cylinder = new Cylinder();
        }
        
        @Override
        Axis hitTest(final float n, float n2) {
            if (!this.m_visible) {
                return Axis.None;
            }
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                allocMatrix4f.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            n2 = UI3DScene.this.screenHeight() - n2;
            final Ray cameraRay = UI3DScene.this.getCameraRay(n, n2, UI3DScene.allocRay());
            final Ray allocRay = UI3DScene.allocRay();
            allocMatrix4f.transformProject(allocRay.origin.set(0.0f, 0.0f, 0.0f));
            final float n3 = UI3DScene.this.m_gizmoScale / stateDataMain.zoomMult() * 1000.0f;
            final float n4 = this.LENGTH * n3;
            final float n5 = this.THICKNESS * n3;
            final float n6 = 0.1f * n3;
            allocMatrix4f.transformDirection(allocRay.direction.set(1.0f, 0.0f, 0.0f)).normalize();
            float closest_distance_between_lines = UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            float t = allocRay.t;
            final float t2 = cameraRay.t;
            if (t < n6 || t >= n6 + n4) {
                t = (closest_distance_between_lines = Float.MAX_VALUE);
            }
            this.m_hideX = (Math.abs(allocRay.direction.dot((Vector3fc)cameraRay.direction)) > 0.9f);
            allocMatrix4f.transformDirection(allocRay.direction.set(0.0f, 1.0f, 0.0f)).normalize();
            float closest_distance_between_lines2 = UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            float t3 = allocRay.t;
            final float t4 = cameraRay.t;
            if (t3 < n6 || t3 >= n6 + n4) {
                t3 = (closest_distance_between_lines2 = Float.MAX_VALUE);
            }
            this.m_hideY = (Math.abs(allocRay.direction.dot((Vector3fc)cameraRay.direction)) > 0.9f);
            allocMatrix4f.transformDirection(allocRay.direction.set(0.0f, 0.0f, 1.0f)).normalize();
            float closest_distance_between_lines3 = UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            float t5 = allocRay.t;
            final float t6 = cameraRay.t;
            if (t5 < n6 || t5 >= n6 + n4) {
                t5 = (closest_distance_between_lines3 = Float.MAX_VALUE);
            }
            this.m_hideZ = (Math.abs(allocRay.direction.dot((Vector3fc)cameraRay.direction)) > 0.9f);
            UI3DScene.releaseRay(allocRay);
            UI3DScene.releaseRay(cameraRay);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            if (t >= n6 && t < n6 + n4 && closest_distance_between_lines < closest_distance_between_lines2 && closest_distance_between_lines < closest_distance_between_lines3) {
                return (closest_distance_between_lines <= n5 / 2.0f) ? Axis.X : Axis.None;
            }
            if (t3 >= n6 && t3 < n6 + n4 && closest_distance_between_lines2 < closest_distance_between_lines && closest_distance_between_lines2 < closest_distance_between_lines3) {
                return (closest_distance_between_lines2 <= n5 / 2.0f) ? Axis.Y : Axis.None;
            }
            if (t5 >= n6 && t5 < n6 + n4 && closest_distance_between_lines3 < closest_distance_between_lines && closest_distance_between_lines3 < closest_distance_between_lines2) {
                return (closest_distance_between_lines3 <= n5 / 2.0f) ? Axis.Z : Axis.None;
            }
            return Axis.None;
        }
        
        @Override
        void startTracking(final float n, final float n2, final Axis trackAxis) {
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            this.m_startXfrm.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            this.m_startXfrm.setRotationXYZ(0.0f, 0.0f, 0.0f);
            this.m_startInvXfrm.set((Matrix4fc)this.m_startXfrm);
            this.m_startInvXfrm.invert();
            this.getPointOnAxis(n, n2, this.m_trackAxis = trackAxis, this.m_startXfrm, this.m_startPos);
        }
        
        @Override
        void updateTracking(final float n, final float n2) {
            final Vector3f pointOnAxis = this.getPointOnAxis(n, n2, this.m_trackAxis, this.m_startXfrm, UI3DScene.allocVector3f());
            if (this.m_currentPos.equals((Object)pointOnAxis)) {
                UI3DScene.releaseVector3f(pointOnAxis);
                return;
            }
            UI3DScene.releaseVector3f(pointOnAxis);
            this.m_currentPos.set((Vector3fc)pointOnAxis);
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            final Vector3f sub = new Vector3f((Vector3fc)this.m_currentPos).sub((Vector3fc)this.m_startPos);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                final Vector3f transformPosition = this.m_startInvXfrm.transformPosition((Vector3fc)this.m_startPos, new Vector3f());
                final Vector3f transformPosition2 = this.m_startInvXfrm.transformPosition((Vector3fc)this.m_currentPos, new Vector3f());
                final Matrix4f invert = new Matrix4f((Matrix4fc)stateDataMain.m_gizmoParentTransform).invert();
                invert.transformPosition(transformPosition);
                invert.transformPosition(transformPosition2);
                sub.set((Vector3fc)transformPosition2).sub((Vector3fc)transformPosition);
            }
            else {
                sub.set((Vector3fc)this.m_startInvXfrm.transformPosition((Vector3fc)this.m_currentPos, new Vector3f())).sub((Vector3fc)this.m_startInvXfrm.transformPosition((Vector3fc)this.m_startPos, new Vector3f()));
            }
            sub.x = (float)Math.floor(sub.x * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
            sub.y = (float)Math.floor(sub.y * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
            sub.z = (float)Math.floor(sub.z * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
            LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget((Object)"onGizmoChanged"), new Object[] { UI3DScene.this.table, sub });
        }
        
        @Override
        void stopTracking() {
            this.m_trackAxis = Axis.None;
        }
        
        @Override
        void render() {
            if (!this.m_visible) {
                return;
            }
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            final float n = UI3DScene.this.m_gizmoScale / stateDataRender.zoomMult() * 1000.0f;
            final float n2 = this.LENGTH * n;
            final float n3 = this.THICKNESS * n;
            final float n4 = 0.1f * n;
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataRender.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoChildTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                allocMatrix4f.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            stateDataRender.m_modelView.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
            PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            if (!this.m_hideX) {
                GL11.glColor3f((stateDataRender.m_gizmoAxis == Axis.X) ? 1.0f : 0.5f, 0.0f, 0.0f);
                GL11.glRotated(90.0, 0.0, 1.0, 0.0);
                GL11.glTranslatef(0.0f, 0.0f, n4);
                this.cylinder.draw(n3 / 2.0f, n3 / 2.0f, n2, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, n2);
                this.cylinder.draw(n3, n3, 0.1f * n, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, -n4 - n2);
                GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
            }
            if (!this.m_hideY) {
                GL11.glColor3f(0.0f, (stateDataRender.m_gizmoAxis == Axis.Y) ? 1.0f : 0.5f, 0.0f);
                GL11.glRotated(-90.0, 1.0, 0.0, 0.0);
                GL11.glTranslatef(0.0f, 0.0f, n4);
                this.cylinder.draw(n3 / 2.0f, n3 / 2.0f, n2, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, n2);
                this.cylinder.draw(n3, n3, 0.1f * n, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, -n4 - n2);
                GL11.glRotated(90.0, 1.0, 0.0, 0.0);
            }
            if (!this.m_hideZ) {
                GL11.glColor3f(0.0f, 0.0f, (stateDataRender.m_gizmoAxis == Axis.Z) ? 1.0f : 0.5f);
                GL11.glTranslatef(0.0f, 0.0f, n4);
                this.cylinder.draw(n3 / 2.0f, n3 / 2.0f, n2, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, n2);
                this.cylinder.draw(n3, n3, 0.1f * n, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, -0.1f - n2);
            }
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            PZGLUtil.popMatrix(5888);
            this.renderLineToOrigin();
        }
    }
    
    private final class RotateGizmo extends Gizmo
    {
        Axis m_trackAxis;
        final Circle m_trackCircle;
        final Matrix4f m_startXfrm;
        final Matrix4f m_startInvXfrm;
        final Vector3f m_startPointOnCircle;
        final Vector3f m_currentPointOnCircle;
        final ArrayList<Vector3f> m_circlePointsMain;
        final ArrayList<Vector3f> m_circlePointsRender;
        
        private RotateGizmo() {
            this.m_trackAxis = Axis.None;
            this.m_trackCircle = new Circle();
            this.m_startXfrm = new Matrix4f();
            this.m_startInvXfrm = new Matrix4f();
            this.m_startPointOnCircle = new Vector3f();
            this.m_currentPointOnCircle = new Vector3f();
            this.m_circlePointsMain = new ArrayList<Vector3f>();
            this.m_circlePointsRender = new ArrayList<Vector3f>();
        }
        
        @Override
        Axis hitTest(final float n, float n2) {
            if (!this.m_visible) {
                return Axis.None;
            }
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            n2 = UI3DScene.this.screenHeight() - n2;
            final Ray cameraRay = UI3DScene.this.getCameraRay(n, n2, UI3DScene.allocRay());
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoChildTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            final Vector3f scale = allocMatrix4f.getScale(UI3DScene.allocVector3f());
            allocMatrix4f.scale(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z);
            UI3DScene.releaseVector3f(scale);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                allocMatrix4f.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            final float n3 = this.LENGTH * (UI3DScene.this.m_gizmoScale / stateDataMain.zoomMult() * 1000.0f);
            final Vector3f transformProject = allocMatrix4f.transformProject(UI3DScene.allocVector3f().set(0.0f, 0.0f, 0.0f));
            final Vector3f normalize = allocMatrix4f.transformDirection(UI3DScene.allocVector3f().set(1.0f, 0.0f, 0.0f)).normalize();
            final Vector3f normalize2 = allocMatrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0f, 1.0f, 0.0f)).normalize();
            final Vector3f normalize3 = allocMatrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0f, 0.0f, 1.0f)).normalize();
            final Vector2 allocVector2 = UI3DScene.allocVector2();
            this.getCircleSegments(transformProject, n3, normalize2, normalize3, this.m_circlePointsMain);
            final float hitTestCircle = this.hitTestCircle(cameraRay, this.m_circlePointsMain, allocVector2);
            BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsMain);
            this.m_circlePointsMain.clear();
            this.getCircleSegments(transformProject, n3, normalize, normalize3, this.m_circlePointsMain);
            final float hitTestCircle2 = this.hitTestCircle(cameraRay, this.m_circlePointsMain, allocVector2);
            BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsMain);
            this.m_circlePointsMain.clear();
            this.getCircleSegments(transformProject, n3, normalize, normalize2, this.m_circlePointsMain);
            final float hitTestCircle3 = this.hitTestCircle(cameraRay, this.m_circlePointsMain, allocVector2);
            BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsMain);
            this.m_circlePointsMain.clear();
            UI3DScene.releaseVector2(allocVector2);
            UI3DScene.releaseVector3f(normalize);
            UI3DScene.releaseVector3f(normalize2);
            UI3DScene.releaseVector3f(normalize3);
            UI3DScene.releaseVector3f(transformProject);
            UI3DScene.releaseRay(cameraRay);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            final float n4 = 8.0f;
            if (hitTestCircle < hitTestCircle2 && hitTestCircle < hitTestCircle3) {
                return (hitTestCircle <= n4) ? Axis.X : Axis.None;
            }
            if (hitTestCircle2 < hitTestCircle && hitTestCircle2 < hitTestCircle3) {
                return (hitTestCircle2 <= n4) ? Axis.Y : Axis.None;
            }
            if (hitTestCircle3 < hitTestCircle && hitTestCircle3 < hitTestCircle2) {
                return (hitTestCircle3 <= n4) ? Axis.Z : Axis.None;
            }
            return Axis.None;
        }
        
        @Override
        void startTracking(final float n, final float n2, final Axis trackAxis) {
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            this.m_startXfrm.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoChildTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                this.m_startXfrm.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            this.m_startInvXfrm.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            this.m_startInvXfrm.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            this.m_startInvXfrm.mul((Matrix4fc)stateDataMain.m_gizmoChildTransform);
            this.m_startInvXfrm.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            this.m_startInvXfrm.invert();
            this.getPointOnAxis(n, n2, this.m_trackAxis = trackAxis, this.m_trackCircle, this.m_startXfrm, this.m_startPointOnCircle);
        }
        
        @Override
        void updateTracking(final float n, final float n2) {
            final Vector3f pointOnAxis = this.getPointOnAxis(n, n2, this.m_trackAxis, this.m_trackCircle, this.m_startXfrm, UI3DScene.allocVector3f());
            if (this.m_currentPointOnCircle.equals((Object)pointOnAxis)) {
                UI3DScene.releaseVector3f(pointOnAxis);
                return;
            }
            this.m_currentPointOnCircle.set((Vector3fc)pointOnAxis);
            UI3DScene.releaseVector3f(pointOnAxis);
            float calculateRotation = this.calculateRotation(this.m_startPointOnCircle, this.m_currentPointOnCircle, this.m_trackCircle);
            switch (this.m_trackAxis) {
                case X: {
                    this.m_trackCircle.orientation.set(1.0f, 0.0f, 0.0f);
                    break;
                }
                case Y: {
                    this.m_trackCircle.orientation.set(0.0f, 1.0f, 0.0f);
                    break;
                }
                case Z: {
                    this.m_trackCircle.orientation.set(0.0f, 0.0f, 1.0f);
                    break;
                }
            }
            final Vector3f set = UI3DScene.allocVector3f().set((Vector3fc)this.m_trackCircle.orientation);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                this.m_startInvXfrm.transformDirection(set);
            }
            final Ray cameraRay = UI3DScene.this.getCameraRay(n, n2, UI3DScene.allocRay());
            final Vector3f normalize = this.m_startXfrm.transformDirection(UI3DScene.allocVector3f().set((Vector3fc)set)).normalize();
            final float dot = cameraRay.direction.dot((Vector3fc)normalize);
            UI3DScene.releaseVector3f(normalize);
            UI3DScene.releaseRay(cameraRay);
            if (UI3DScene.this.m_gizmoParent instanceof SceneCharacter) {
                if (dot > 0.0f) {
                    calculateRotation *= -1.0f;
                }
            }
            else if (dot < 0.0f) {
                calculateRotation *= -1.0f;
            }
            final Quaternionf fromAxisAngleDeg = UI3DScene.allocQuaternionf().fromAxisAngleDeg((Vector3fc)set, calculateRotation);
            UI3DScene.releaseVector3f(set);
            final Vector3f eulerAnglesXYZ = fromAxisAngleDeg.getEulerAnglesXYZ(new Vector3f());
            UI3DScene.releaseQuaternionf(fromAxisAngleDeg);
            eulerAnglesXYZ.x = (float)Math.floor(eulerAnglesXYZ.x * 57.295776f + 0.5f);
            eulerAnglesXYZ.y = (float)Math.floor(eulerAnglesXYZ.y * 57.295776f + 0.5f);
            eulerAnglesXYZ.z = (float)Math.floor(eulerAnglesXYZ.z * 57.295776f + 0.5f);
            LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget((Object)"onGizmoChanged"), new Object[] { UI3DScene.this.table, eulerAnglesXYZ });
        }
        
        @Override
        void stopTracking() {
            this.m_trackAxis = Axis.None;
        }
        
        @Override
        void render() {
            if (!this.m_visible) {
                return;
            }
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataRender.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoChildTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoTransform);
            final Vector3f scale = allocMatrix4f.getScale(UI3DScene.allocVector3f());
            allocMatrix4f.scale(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z);
            UI3DScene.releaseVector3f(scale);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                allocMatrix4f.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            final Ray cameraRay = UI3DScene.this.getCameraRay((float)(Mouse.getXA() - UI3DScene.this.getAbsoluteX().intValue()), UI3DScene.this.screenHeight() - (float)(Mouse.getYA() - UI3DScene.this.getAbsoluteY().intValue()), stateDataRender.m_projection, stateDataRender.m_modelView, UI3DScene.allocRay());
            final float n = this.LENGTH * (UI3DScene.this.m_gizmoScale / stateDataRender.zoomMult() * 1000.0f);
            final Vector3f transformProject = allocMatrix4f.transformProject(UI3DScene.allocVector3f().set(0.0f, 0.0f, 0.0f));
            final Vector3f normalize = allocMatrix4f.transformDirection(UI3DScene.allocVector3f().set(1.0f, 0.0f, 0.0f)).normalize();
            final Vector3f normalize2 = allocMatrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0f, 1.0f, 0.0f)).normalize();
            final Vector3f normalize3 = allocMatrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0f, 0.0f, 1.0f)).normalize();
            GL11.glClear(256);
            GL11.glEnable(2929);
            final Axis axis = (this.m_trackAxis == Axis.None) ? stateDataRender.m_gizmoAxis : this.m_trackAxis;
            if (this.m_trackAxis == Axis.None || this.m_trackAxis == Axis.X) {
                this.renderAxis(transformProject, n, normalize2, normalize3, (axis == Axis.X) ? 1.0f : 0.5f, 0.0f, 0.0f, cameraRay);
            }
            if (this.m_trackAxis == Axis.None || this.m_trackAxis == Axis.Y) {
                this.renderAxis(transformProject, n, normalize, normalize3, 0.0f, (axis == Axis.Y) ? 1.0f : 0.5f, 0.0f, cameraRay);
            }
            if (this.m_trackAxis == Axis.None || this.m_trackAxis == Axis.Z) {
                this.renderAxis(transformProject, n, normalize, normalize2, 0.0f, 0.0f, (axis == Axis.Z) ? 1.0f : 0.5f, cameraRay);
            }
            UI3DScene.releaseVector3f(transformProject);
            UI3DScene.releaseVector3f(normalize);
            UI3DScene.releaseVector3f(normalize2);
            UI3DScene.releaseVector3f(normalize3);
            UI3DScene.releaseRay(cameraRay);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            this.renderLineToOrigin();
        }
        
        void getCircleSegments(final Vector3f vector3f, final float n, final Vector3f vector3f2, final Vector3f vector3f3, final ArrayList<Vector3f> list) {
            final Vector3f allocVector3f = UI3DScene.allocVector3f();
            final Vector3f allocVector3f2 = UI3DScene.allocVector3f();
            final int n2 = 32;
            final double n3 = 0.0 / n2 * 0.01745329238474369;
            final double cos = Math.cos(n3);
            final double sin = Math.sin(n3);
            vector3f2.mul((float)cos, allocVector3f);
            vector3f3.mul((float)sin, allocVector3f2);
            allocVector3f.add((Vector3fc)allocVector3f2).mul(n);
            list.add(UI3DScene.allocVector3f().set((Vector3fc)vector3f).add((Vector3fc)allocVector3f));
            for (int i = 1; i <= n2; ++i) {
                final double n4 = i * 360.0 / n2 * 0.01745329238474369;
                final double cos2 = Math.cos(n4);
                final double sin2 = Math.sin(n4);
                vector3f2.mul((float)cos2, allocVector3f);
                vector3f3.mul((float)sin2, allocVector3f2);
                allocVector3f.add((Vector3fc)allocVector3f2).mul(n);
                list.add(UI3DScene.allocVector3f().set((Vector3fc)vector3f).add((Vector3fc)allocVector3f));
            }
            UI3DScene.releaseVector3f(allocVector3f);
            UI3DScene.releaseVector3f(allocVector3f2);
        }
        
        private float hitTestCircle(final Ray ray, final ArrayList<Vector3f> list, final Vector2 vector2) {
            final Ray allocRay = UI3DScene.allocRay();
            final Vector3f allocVector3f = UI3DScene.allocVector3f();
            final float sceneToUIX = UI3DScene.this.sceneToUIX(ray.origin.x, ray.origin.y, ray.origin.z);
            final float sceneToUIY = UI3DScene.this.sceneToUIY(ray.origin.x, ray.origin.y, ray.origin.z);
            float n = Float.MAX_VALUE;
            Vector3f vector3f = list.get(0);
            for (int i = 1; i < list.size(); ++i) {
                final Vector3f vector3f2 = list.get(i);
                final float sceneToUIX2 = UI3DScene.this.sceneToUIX(vector3f.x, vector3f.y, vector3f.z);
                final float sceneToUIY2 = UI3DScene.this.sceneToUIY(vector3f.x, vector3f.y, vector3f.z);
                final float sceneToUIX3 = UI3DScene.this.sceneToUIX(vector3f2.x, vector3f2.y, vector3f2.z);
                final float sceneToUIY3 = UI3DScene.this.sceneToUIY(vector3f2.x, vector3f2.y, vector3f2.z);
                final double n2 = Math.pow(sceneToUIX3 - sceneToUIX2, 2.0) + Math.pow(sceneToUIY3 - sceneToUIY2, 2.0);
                if (n2 < 0.001) {
                    vector3f = vector3f2;
                }
                else {
                    final double n3 = ((sceneToUIX - sceneToUIX2) * (sceneToUIX3 - sceneToUIX2) + (sceneToUIY - sceneToUIY2) * (sceneToUIY3 - sceneToUIY2)) / n2;
                    double n4 = sceneToUIX2 + n3 * (sceneToUIX3 - sceneToUIX2);
                    double n5 = sceneToUIY2 + n3 * (sceneToUIY3 - sceneToUIY2);
                    if (n3 <= 0.0) {
                        n4 = sceneToUIX2;
                        n5 = sceneToUIY2;
                    }
                    else if (n3 >= 1.0) {
                        n4 = sceneToUIX3;
                        n5 = sceneToUIY3;
                    }
                    final float distanceTo2D = IsoUtils.DistanceTo2D(sceneToUIX, sceneToUIY, (float)n4, (float)n5);
                    if (distanceTo2D < n) {
                        n = distanceTo2D;
                        vector2.set((float)n4, (float)n5);
                    }
                    vector3f = vector3f2;
                }
            }
            UI3DScene.releaseVector3f(allocVector3f);
            UI3DScene.releaseRay(allocRay);
            return n;
        }
        
        void renderAxis(final Vector3f vector3f, final float n, final Vector3f vector3f2, final Vector3f vector3f3, final float n2, final float n3, final float n4, final Ray ray) {
            UI3DScene.vboLines.flush();
            UI3DScene.vboLines.setLineWidth(6.0f);
            this.getCircleSegments(vector3f, n, vector3f2, vector3f3, this.m_circlePointsRender);
            final Vector3f allocVector3f = UI3DScene.allocVector3f();
            Vector3f vector3f4 = this.m_circlePointsRender.get(0);
            for (int i = 1; i < this.m_circlePointsRender.size(); ++i) {
                final Vector3f vector3f5 = this.m_circlePointsRender.get(i);
                allocVector3f.set(vector3f5.x - vector3f.x, vector3f5.y - vector3f.y, vector3f5.z - vector3f.z).normalize();
                if (allocVector3f.dot((Vector3fc)ray.direction) < 0.1f) {
                    UI3DScene.vboLines.addLine(vector3f4.x, vector3f4.y, vector3f4.z, vector3f5.x, vector3f5.y, vector3f5.z, n2, n3, n4, 1.0f);
                }
                else {
                    UI3DScene.vboLines.addLine(vector3f4.x, vector3f4.y, vector3f4.z, vector3f5.x, vector3f5.y, vector3f5.z, n2 / 2.0f, n3 / 2.0f, n4 / 2.0f, 0.25f);
                }
                vector3f4 = vector3f5;
            }
            BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsRender);
            this.m_circlePointsRender.clear();
            UI3DScene.releaseVector3f(allocVector3f);
            UI3DScene.vboLines.flush();
        }
        
        Vector3f getPointOnAxis(final float n, final float n2, final Axis axis, final Circle circle, final Matrix4f matrix4f, final Vector3f vector3f) {
            circle.radius = this.LENGTH * 1.0f;
            matrix4f.getTranslation(circle.center);
            circle.center.set(UI3DScene.this.sceneToUIX(circle.center.x, circle.center.y, circle.center.z), UI3DScene.this.sceneToUIY(circle.center.x, circle.center.y, circle.center.z), 0.0f);
            circle.orientation.set(0.0f, 0.0f, 1.0f);
            final Ray allocRay = UI3DScene.allocRay();
            allocRay.origin.set(n, n2, 0.0f);
            allocRay.direction.set(0.0f, 0.0f, -1.0f);
            UI3DScene.this.closest_distance_line_circle(allocRay, circle, vector3f);
            UI3DScene.releaseRay(allocRay);
            return vector3f;
        }
        
        float calculateRotation(final Vector3f vector3f, final Vector3f vector3f2, final Circle circle) {
            if (vector3f.equals((Object)vector3f2)) {
                return 0.0f;
            }
            final Vector3f normalize = UI3DScene.allocVector3f().set((Vector3fc)vector3f).sub((Vector3fc)circle.center).normalize();
            final Vector3f normalize2 = UI3DScene.allocVector3f().set((Vector3fc)vector3f2).sub((Vector3fc)circle.center).normalize();
            final float n = (float)Math.acos(normalize2.dot((Vector3fc)normalize));
            final Vector3f cross = normalize.cross((Vector3fc)normalize2, UI3DScene.allocVector3f());
            final int n2 = (int)Math.signum(cross.dot((Vector3fc)circle.orientation));
            UI3DScene.releaseVector3f(normalize);
            UI3DScene.releaseVector3f(normalize2);
            UI3DScene.releaseVector3f(cross);
            return n2 * n * 57.295776f;
        }
    }
    
    private static final class TranslateGizmoRenderData
    {
        boolean m_hideX;
        boolean m_hideY;
        boolean m_hideZ;
    }
    
    private final class TranslateGizmo extends Gizmo
    {
        final Matrix4f m_startXfrm;
        final Matrix4f m_startInvXfrm;
        final Vector3f m_startPos;
        final Vector3f m_currentPos;
        Axis m_trackAxis;
        Cylinder cylinder;
        
        private TranslateGizmo() {
            this.m_startXfrm = new Matrix4f();
            this.m_startInvXfrm = new Matrix4f();
            this.m_startPos = new Vector3f();
            this.m_currentPos = new Vector3f();
            this.m_trackAxis = Axis.None;
            this.cylinder = new Cylinder();
        }
        
        @Override
        Axis hitTest(final float n, float n2) {
            if (!this.m_visible) {
                return Axis.None;
            }
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoChildTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                allocMatrix4f.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            n2 = UI3DScene.this.screenHeight() - n2;
            final Ray cameraRay = UI3DScene.this.getCameraRay(n, n2, UI3DScene.allocRay());
            final Ray allocRay = UI3DScene.allocRay();
            allocMatrix4f.transformPosition(allocRay.origin.set(0.0f, 0.0f, 0.0f));
            final float n3 = UI3DScene.this.m_gizmoScale / stateDataMain.zoomMult() * 1000.0f;
            final float n4 = this.LENGTH * n3;
            final float n5 = this.THICKNESS * n3;
            final float n6 = 0.1f * n3;
            allocMatrix4f.transformDirection(allocRay.direction.set(1.0f, 0.0f, 0.0f)).normalize();
            float closest_distance_between_lines = UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            float t = allocRay.t;
            final float t2 = cameraRay.t;
            if (t < n6 || t >= n6 + n4) {
                t = (closest_distance_between_lines = Float.MAX_VALUE);
            }
            stateDataMain.m_translateGizmoRenderData.m_hideX = (Math.abs(allocRay.direction.dot((Vector3fc)cameraRay.direction)) > 0.9f);
            allocMatrix4f.transformDirection(allocRay.direction.set(0.0f, 1.0f, 0.0f)).normalize();
            float closest_distance_between_lines2 = UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            float t3 = allocRay.t;
            final float t4 = cameraRay.t;
            if (t3 < n6 || t3 >= n6 + n4) {
                t3 = (closest_distance_between_lines2 = Float.MAX_VALUE);
            }
            stateDataMain.m_translateGizmoRenderData.m_hideY = (Math.abs(allocRay.direction.dot((Vector3fc)cameraRay.direction)) > 0.9f);
            allocMatrix4f.transformDirection(allocRay.direction.set(0.0f, 0.0f, 1.0f)).normalize();
            float closest_distance_between_lines3 = UI3DScene.this.closest_distance_between_lines(allocRay, cameraRay);
            float t5 = allocRay.t;
            final float t6 = cameraRay.t;
            if (t5 < n6 || t5 >= n6 + n4) {
                t5 = (closest_distance_between_lines3 = Float.MAX_VALUE);
            }
            stateDataMain.m_translateGizmoRenderData.m_hideZ = (Math.abs(allocRay.direction.dot((Vector3fc)cameraRay.direction)) > 0.9f);
            UI3DScene.releaseRay(allocRay);
            UI3DScene.releaseRay(cameraRay);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            if (t >= n6 && t < n6 + n4 && closest_distance_between_lines < closest_distance_between_lines2 && closest_distance_between_lines < closest_distance_between_lines3) {
                return (closest_distance_between_lines <= n5 / 2.0f) ? Axis.X : Axis.None;
            }
            if (t3 >= n6 && t3 < n6 + n4 && closest_distance_between_lines2 < closest_distance_between_lines && closest_distance_between_lines2 < closest_distance_between_lines3) {
                return (closest_distance_between_lines2 <= n5 / 2.0f) ? Axis.Y : Axis.None;
            }
            if (t5 >= n6 && t5 < n6 + n4 && closest_distance_between_lines3 < closest_distance_between_lines && closest_distance_between_lines3 < closest_distance_between_lines2) {
                return (closest_distance_between_lines3 <= n5 / 2.0f) ? Axis.Z : Axis.None;
            }
            return Axis.None;
        }
        
        @Override
        void startTracking(final float n, final float n2, final Axis trackAxis) {
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            this.m_startXfrm.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoChildTransform);
            this.m_startXfrm.mul((Matrix4fc)stateDataMain.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                this.m_startXfrm.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            this.m_startInvXfrm.set((Matrix4fc)this.m_startXfrm);
            this.m_startInvXfrm.invert();
            this.getPointOnAxis(n, n2, this.m_trackAxis = trackAxis, this.m_startXfrm, this.m_startPos);
        }
        
        @Override
        void updateTracking(final float n, final float n2) {
            final Vector3f pointOnAxis = this.getPointOnAxis(n, n2, this.m_trackAxis, this.m_startXfrm, UI3DScene.allocVector3f());
            if (this.m_currentPos.equals((Object)pointOnAxis)) {
                UI3DScene.releaseVector3f(pointOnAxis);
                return;
            }
            UI3DScene.releaseVector3f(pointOnAxis);
            this.m_currentPos.set((Vector3fc)pointOnAxis);
            final StateData stateDataMain = UI3DScene.this.stateDataMain();
            final Vector3f sub = new Vector3f((Vector3fc)this.m_currentPos).sub((Vector3fc)this.m_startPos);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                final Vector3f transformPosition = this.m_startInvXfrm.transformPosition((Vector3fc)this.m_startPos, UI3DScene.allocVector3f());
                final Vector3f transformPosition2 = this.m_startInvXfrm.transformPosition((Vector3fc)this.m_currentPos, UI3DScene.allocVector3f());
                final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
                allocMatrix4f.set((Matrix4fc)stateDataMain.m_gizmoParentTransform);
                allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoOriginTransform);
                allocMatrix4f.mul((Matrix4fc)stateDataMain.m_gizmoChildTransform);
                allocMatrix4f.invert();
                allocMatrix4f.transformPosition(transformPosition);
                allocMatrix4f.transformPosition(transformPosition2);
                UI3DScene.releaseMatrix4f(allocMatrix4f);
                sub.set((Vector3fc)transformPosition2).sub((Vector3fc)transformPosition);
                UI3DScene.releaseVector3f(transformPosition);
                UI3DScene.releaseVector3f(transformPosition2);
            }
            else {
                final Vector3f transformPosition3 = this.m_startInvXfrm.transformPosition((Vector3fc)this.m_startPos, UI3DScene.allocVector3f());
                final Vector3f transformPosition4 = this.m_startInvXfrm.transformPosition((Vector3fc)this.m_currentPos, UI3DScene.allocVector3f());
                final Matrix4f allocMatrix4f2 = UI3DScene.allocMatrix4f();
                allocMatrix4f2.set((Matrix4fc)stateDataMain.m_gizmoTransform);
                allocMatrix4f2.transformPosition(transformPosition3);
                allocMatrix4f2.transformPosition(transformPosition4);
                UI3DScene.releaseMatrix4f(allocMatrix4f2);
                sub.set((Vector3fc)transformPosition4).sub((Vector3fc)transformPosition3);
                UI3DScene.releaseVector3f(transformPosition3);
                UI3DScene.releaseVector3f(transformPosition4);
            }
            sub.x = (float)Math.floor(sub.x * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
            sub.y = (float)Math.floor(sub.y * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
            sub.z = (float)Math.floor(sub.z * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
            if (stateDataMain.m_selectedAttachmentIsChildAttachment) {
                sub.mul(-1.0f);
            }
            LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget((Object)"onGizmoChanged"), new Object[] { UI3DScene.this.table, sub });
        }
        
        @Override
        void stopTracking() {
            this.m_trackAxis = Axis.None;
        }
        
        @Override
        void render() {
            if (!this.m_visible) {
                return;
            }
            final StateData stateDataRender = UI3DScene.this.stateDataRender();
            final Matrix4f allocMatrix4f = UI3DScene.allocMatrix4f();
            allocMatrix4f.set((Matrix4fc)stateDataRender.m_gizmoParentTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoOriginTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoChildTransform);
            allocMatrix4f.mul((Matrix4fc)stateDataRender.m_gizmoTransform);
            final Vector3f scale = allocMatrix4f.getScale(UI3DScene.allocVector3f());
            allocMatrix4f.scale(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z);
            UI3DScene.releaseVector3f(scale);
            if (UI3DScene.this.m_transformMode == TransformMode.Global) {
                allocMatrix4f.setRotationXYZ(0.0f, 0.0f, 0.0f);
            }
            stateDataRender.m_modelView.mul((Matrix4fc)allocMatrix4f, allocMatrix4f);
            PZGLUtil.pushAndLoadMatrix(5888, allocMatrix4f);
            UI3DScene.releaseMatrix4f(allocMatrix4f);
            final float n = UI3DScene.this.m_gizmoScale / stateDataRender.zoomMult() * 1000.0f;
            final float n2 = this.THICKNESS * n;
            final float n3 = this.LENGTH * n;
            final float n4 = 0.1f * n;
            if (!stateDataRender.m_translateGizmoRenderData.m_hideX) {
                GL11.glColor3f((stateDataRender.m_gizmoAxis == Axis.X) ? 1.0f : 0.5f, 0.0f, 0.0f);
                GL11.glRotated(90.0, 0.0, 1.0, 0.0);
                GL11.glTranslatef(0.0f, 0.0f, n4);
                this.cylinder.draw(n2 / 2.0f, n2 / 2.0f, n3, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, n3);
                this.cylinder.draw(n2 / 2.0f * 2.0f, 0.0f, 0.1f * n, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, -n4 - n3);
                GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
            }
            if (!stateDataRender.m_translateGizmoRenderData.m_hideY) {
                GL11.glColor3f(0.0f, (stateDataRender.m_gizmoAxis == Axis.Y) ? 1.0f : 0.5f, 0.0f);
                GL11.glRotated(-90.0, 1.0, 0.0, 0.0);
                GL11.glTranslatef(0.0f, 0.0f, n4);
                this.cylinder.draw(n2 / 2.0f, n2 / 2.0f, n3, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, n3);
                this.cylinder.draw(n2 / 2.0f * 2.0f, 0.0f, 0.1f * n, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, -n4 - n3);
                GL11.glRotated(90.0, 1.0, 0.0, 0.0);
            }
            if (!stateDataRender.m_translateGizmoRenderData.m_hideZ) {
                GL11.glColor3f(0.0f, 0.0f, (stateDataRender.m_gizmoAxis == Axis.Z) ? 1.0f : 0.5f);
                GL11.glTranslatef(0.0f, 0.0f, n4);
                this.cylinder.draw(n2 / 2.0f, n2 / 2.0f, n3, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, n3);
                this.cylinder.draw(n2 / 2.0f * 2.0f, 0.0f, 0.1f * n, 8, 1);
                GL11.glTranslatef(0.0f, 0.0f, -n4 - n3);
            }
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            PZGLUtil.popMatrix(5888);
            this.renderLineToOrigin();
        }
    }
    
    private static final class CharacterDrawer extends TextureDraw.GenericDrawer
    {
        SceneCharacter m_character;
        CharacterRenderData m_renderData;
        boolean bRendered;
        
        public void init(final SceneCharacter character, final CharacterRenderData renderData) {
            this.m_character = character;
            this.m_renderData = renderData;
            this.bRendered = false;
            this.m_character.m_animatedModel.renderMain();
        }
        
        @Override
        public void render() {
            if (this.m_character.m_bClearDepthBuffer) {
                GL11.glClear(256);
            }
            final boolean value = DebugOptions.instance.ModelRenderBones.getValue();
            DebugOptions.instance.ModelRenderBones.setValue(this.m_character.m_bShowBones);
            this.m_character.m_scene.m_CharacterSceneModelCamera.m_renderData = this.m_renderData;
            this.m_character.m_animatedModel.DoRender(this.m_character.m_scene.m_CharacterSceneModelCamera);
            DebugOptions.instance.ModelRenderBones.setValue(value);
            GL11.glDepthMask(this.bRendered = true);
        }
        
        @Override
        public void postRender() {
            this.m_character.m_animatedModel.postRender(this.bRendered);
        }
    }
    
    private static final class ModelDrawer extends TextureDraw.GenericDrawer
    {
        SceneModel m_model;
        ModelRenderData m_renderData;
        boolean bRendered;
        
        public void init(final SceneModel model, final ModelRenderData renderData) {
            this.m_model = model;
            this.m_renderData = renderData;
            this.bRendered = false;
        }
        
        @Override
        public void render() {
            final StateData stateDataRender = this.m_model.m_scene.stateDataRender();
            PZGLUtil.pushAndLoadMatrix(5889, stateDataRender.m_projection);
            PZGLUtil.pushAndLoadMatrix(5888, stateDataRender.m_modelView);
            final Model model = this.m_model.m_model;
            final Shader effect = model.Effect;
            if (effect != null && model.Mesh != null && model.Mesh.isReady()) {
                GL11.glPushAttrib(1048575);
                GL11.glPushClientAttrib(-1);
                GL11.glDepthFunc(513);
                GL11.glDepthMask(true);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glEnable(2929);
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                effect.Start();
                if (model.tex != null) {
                    effect.setTexture(model.tex, "Texture", 0);
                }
                effect.setDepthBias(0.0f);
                effect.setAmbient(1.0f);
                effect.setLightingAmount(1.0f);
                effect.setHueShift(0.0f);
                effect.setTint(1.0f, 1.0f, 1.0f);
                effect.setAlpha(1.0f);
                for (int i = 0; i < 5; ++i) {
                    effect.setLight(i, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, 0.0f, 0.0f, 0.0f, null);
                }
                effect.setTransformMatrix(this.m_renderData.m_transform, false);
                model.Mesh.Draw(effect);
                effect.End();
                GL11.glPopAttrib();
                GL11.glPopClientAttrib();
                Texture.lastTextureID = -1;
                SpriteRenderer.ringBuffer.restoreBoundTextures = true;
                SpriteRenderer.ringBuffer.restoreVBOs = true;
            }
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
            this.bRendered = true;
        }
        
        @Override
        public void postRender() {
        }
    }
    
    private static final class VehicleDrawer extends TextureDraw.GenericDrawer
    {
        SceneVehicle m_vehicle;
        VehicleRenderData m_renderData;
        boolean bRendered;
        final float[] fzeroes;
        final Vector3f paintColor;
        final Matrix4f IDENTITY;
        
        private VehicleDrawer() {
            this.fzeroes = new float[16];
            this.paintColor = new Vector3f(0.0f, 0.5f, 0.5f);
            this.IDENTITY = new Matrix4f();
        }
        
        public void init(final SceneVehicle vehicle, final VehicleRenderData renderData) {
            this.m_vehicle = vehicle;
            this.m_renderData = renderData;
            this.bRendered = false;
        }
        
        @Override
        public void render() {
            for (int i = 0; i < this.m_renderData.m_models.size(); ++i) {
                GL11.glPushAttrib(1048575);
                GL11.glPushClientAttrib(-1);
                this.render(i);
                GL11.glPopAttrib();
                GL11.glPopClientAttrib();
                Texture.lastTextureID = -1;
                SpriteRenderer.ringBuffer.restoreBoundTextures = true;
                SpriteRenderer.ringBuffer.restoreVBOs = true;
            }
        }
        
        private void render(final int n) {
            this.m_renderData.m_transform.set((Matrix4fc)this.m_renderData.m_transforms.get(n));
            ModelCamera.instance.Begin();
            final Model model = this.m_renderData.m_models.get(n);
            final boolean bStatic = model.bStatic;
            if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
                GL11.glPolygonMode(1032, 6913);
                GL11.glEnable(2848);
                GL11.glLineWidth(0.75f);
                final Shader orCreateShader = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", bStatic);
                if (orCreateShader != null) {
                    orCreateShader.Start();
                    orCreateShader.setTransformMatrix(this.IDENTITY.identity(), false);
                    model.Mesh.Draw(orCreateShader);
                    orCreateShader.End();
                }
                GL11.glDisable(2848);
                ModelCamera.instance.End();
                return;
            }
            final Shader effect = model.Effect;
            if (effect != null && effect.isVehicleShader()) {
                GL11.glDepthFunc(513);
                GL11.glDepthMask(true);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glEnable(2929);
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                effect.Start();
                if (model.tex != null) {
                    effect.setTexture(model.tex, "Texture0", 0);
                    GL11.glTexEnvi(8960, 8704, 7681);
                    if (this.m_vehicle.m_script.getSkinCount() > 0 && this.m_vehicle.m_script.getSkin(0).textureMask != null) {
                        effect.setTexture(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.m_vehicle.m_script.getSkin(0).textureMask)), "TextureMask", 2);
                        GL11.glTexEnvi(8960, 8704, 7681);
                    }
                }
                effect.setDepthBias(0.0f);
                effect.setAmbient(1.0f);
                effect.setLightingAmount(1.0f);
                effect.setHueShift(0.0f);
                effect.setTint(1.0f, 1.0f, 1.0f);
                effect.setAlpha(1.0f);
                for (int i = 0; i < 5; ++i) {
                    effect.setLight(i, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, 0.0f, 0.0f, 0.0f, null);
                }
                effect.setTextureUninstall1(this.fzeroes);
                effect.setTextureUninstall2(this.fzeroes);
                effect.setTextureLightsEnables2(this.fzeroes);
                effect.setTextureDamage1Enables1(this.fzeroes);
                effect.setTextureDamage1Enables2(this.fzeroes);
                effect.setTextureDamage2Enables1(this.fzeroes);
                effect.setTextureDamage2Enables2(this.fzeroes);
                effect.setMatrixBlood1(this.fzeroes, this.fzeroes);
                effect.setMatrixBlood2(this.fzeroes, this.fzeroes);
                effect.setTextureRustA(0.0f);
                effect.setTexturePainColor(this.paintColor, 1.0f);
                effect.setTransformMatrix(this.IDENTITY.identity(), false);
                model.Mesh.Draw(effect);
                effect.End();
            }
            else if (effect != null && model.Mesh != null && model.Mesh.isReady()) {
                GL11.glDepthFunc(513);
                GL11.glDepthMask(true);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glEnable(2929);
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                effect.Start();
                if (model.tex != null) {
                    effect.setTexture(model.tex, "Texture", 0);
                }
                effect.setDepthBias(0.0f);
                effect.setAmbient(1.0f);
                effect.setLightingAmount(1.0f);
                effect.setHueShift(0.0f);
                effect.setTint(1.0f, 1.0f, 1.0f);
                effect.setAlpha(1.0f);
                for (int j = 0; j < 5; ++j) {
                    effect.setLight(j, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, 0.0f, 0.0f, 0.0f, null);
                }
                effect.setTransformMatrix(this.IDENTITY.identity(), false);
                model.Mesh.Draw(effect);
                effect.End();
            }
            ModelCamera.instance.End();
            this.bRendered = true;
        }
        
        @Override
        public void postRender() {
        }
    }
    
    private static final class StateData
    {
        final Matrix4f m_projection;
        final Matrix4f m_modelView;
        int m_zoom;
        OverlaysDrawer m_overlaysDrawer;
        final ArrayList<SceneObjectRenderData> m_objectData;
        Gizmo m_gizmo;
        final Vector3f m_gizmoTranslate;
        final Vector3f m_gizmoRotate;
        final Matrix4f m_gizmoParentTransform;
        final Matrix4f m_gizmoOriginTransform;
        final Matrix4f m_gizmoChildTransform;
        final Matrix4f m_gizmoTransform;
        boolean m_hasGizmoOrigin;
        boolean m_selectedAttachmentIsChildAttachment;
        Axis m_gizmoAxis;
        final TranslateGizmoRenderData m_translateGizmoRenderData;
        final ArrayList<PositionRotation> m_axes;
        final ArrayList<AABB> m_aabb;
        final ArrayList<Box3D> m_box3D;
        
        private StateData() {
            this.m_projection = new Matrix4f();
            this.m_modelView = new Matrix4f();
            this.m_objectData = new ArrayList<SceneObjectRenderData>();
            this.m_gizmo = null;
            this.m_gizmoTranslate = new Vector3f();
            this.m_gizmoRotate = new Vector3f();
            this.m_gizmoParentTransform = new Matrix4f();
            this.m_gizmoOriginTransform = new Matrix4f();
            this.m_gizmoChildTransform = new Matrix4f();
            this.m_gizmoTransform = new Matrix4f();
            this.m_gizmoAxis = Axis.None;
            this.m_translateGizmoRenderData = new TranslateGizmoRenderData();
            this.m_axes = new ArrayList<PositionRotation>();
            this.m_aabb = new ArrayList<AABB>();
            this.m_box3D = new ArrayList<Box3D>();
        }
        
        private float zoomMult() {
            return (float)Math.exp(this.m_zoom * 0.2f) * 160.0f / Math.max(1.82f, 1.0f);
        }
    }
}
