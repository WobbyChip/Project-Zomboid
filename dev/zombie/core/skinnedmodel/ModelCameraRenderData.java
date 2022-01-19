// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

import zombie.iso.IsoMovingObject;
import zombie.vehicles.BaseVehicle;
import zombie.util.Type;
import zombie.characters.IsoGameCharacter;
import zombie.popman.ObjectPool;
import zombie.core.textures.TextureDraw;

public final class ModelCameraRenderData extends TextureDraw.GenericDrawer
{
    private ModelCamera m_camera;
    private float m_angle;
    private boolean m_bUseWorldIso;
    private float m_x;
    private float m_y;
    private float m_z;
    private boolean m_bInVehicle;
    public static final ObjectPool<ModelCameraRenderData> s_pool;
    
    public ModelCameraRenderData init(final ModelCamera camera, final ModelManager.ModelSlot modelSlot) {
        final IsoMovingObject object = modelSlot.model.object;
        final IsoGameCharacter isoGameCharacter = zombie.util.Type.tryCastTo(object, IsoGameCharacter.class);
        this.m_camera = camera;
        this.m_x = object.x;
        this.m_y = object.y;
        this.m_z = object.z;
        if (isoGameCharacter == null) {
            this.m_angle = 0.0f;
            this.m_bInVehicle = false;
            this.m_bUseWorldIso = !BaseVehicle.RENDER_TO_TEXTURE;
        }
        else {
            this.m_bInVehicle = isoGameCharacter.isSeatedInVehicle();
            if (this.m_bInVehicle) {
                this.m_angle = 0.0f;
                final BaseVehicle vehicle = isoGameCharacter.getVehicle();
                this.m_x = vehicle.x;
                this.m_y = vehicle.y;
                this.m_z = vehicle.z;
            }
            else {
                this.m_angle = isoGameCharacter.getAnimationPlayer().getRenderedAngle();
            }
            this.m_bUseWorldIso = true;
        }
        return this;
    }
    
    public ModelCameraRenderData init(final ModelCamera camera, final float angle, final boolean bUseWorldIso, final float x, final float y, final float z, final boolean bInVehicle) {
        this.m_camera = camera;
        this.m_angle = angle;
        this.m_bUseWorldIso = bUseWorldIso;
        this.m_x = x;
        this.m_y = y;
        this.m_z = z;
        this.m_bInVehicle = bInVehicle;
        return this;
    }
    
    @Override
    public void render() {
        this.m_camera.m_useAngle = this.m_angle;
        this.m_camera.m_bUseWorldIso = this.m_bUseWorldIso;
        this.m_camera.m_x = this.m_x;
        this.m_camera.m_y = this.m_y;
        this.m_camera.m_z = this.m_z;
        this.m_camera.m_bInVehicle = this.m_bInVehicle;
        ModelCamera.instance = this.m_camera;
    }
    
    @Override
    public void postRender() {
        ModelCameraRenderData.s_pool.release(this);
    }
    
    static {
        s_pool = new ObjectPool<ModelCameraRenderData>(ModelCameraRenderData::new);
    }
}
