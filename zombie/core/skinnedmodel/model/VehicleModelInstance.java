// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.ModelManager;
import java.util.Arrays;
import zombie.iso.IsoLightSource;
import org.joml.Vector3f;
import zombie.core.textures.Texture;

public final class VehicleModelInstance extends ModelInstance
{
    public Texture textureRust;
    public Texture textureMask;
    public Texture textureLights;
    public Texture textureDamage1Overlay;
    public Texture textureDamage1Shell;
    public Texture textureDamage2Overlay;
    public Texture textureDamage2Shell;
    public final float[] textureUninstall1;
    public final float[] textureUninstall2;
    public final float[] textureLightsEnables1;
    public final float[] textureLightsEnables2;
    public final float[] textureDamage1Enables1;
    public final float[] textureDamage1Enables2;
    public final float[] textureDamage2Enables1;
    public final float[] textureDamage2Enables2;
    public final float[] matrixBlood1Enables1;
    public final float[] matrixBlood1Enables2;
    public final float[] matrixBlood2Enables1;
    public final float[] matrixBlood2Enables2;
    public float textureRustA;
    public float refWindows;
    public float refBody;
    public final Vector3f painColor;
    private IsoLightSource[] m_lights;
    public final Object m_lightsLock;
    
    public VehicleModelInstance() {
        this.textureRust = null;
        this.textureMask = null;
        this.textureLights = null;
        this.textureDamage1Overlay = null;
        this.textureDamage1Shell = null;
        this.textureDamage2Overlay = null;
        this.textureDamage2Shell = null;
        this.textureUninstall1 = new float[16];
        this.textureUninstall2 = new float[16];
        this.textureLightsEnables1 = new float[16];
        this.textureLightsEnables2 = new float[16];
        this.textureDamage1Enables1 = new float[16];
        this.textureDamage1Enables2 = new float[16];
        this.textureDamage2Enables1 = new float[16];
        this.textureDamage2Enables2 = new float[16];
        this.matrixBlood1Enables1 = new float[16];
        this.matrixBlood1Enables2 = new float[16];
        this.matrixBlood2Enables1 = new float[16];
        this.matrixBlood2Enables2 = new float[16];
        this.textureRustA = 0.0f;
        this.refWindows = 0.5f;
        this.refBody = 0.4f;
        this.painColor = new Vector3f(0.0f, 0.5f, 0.5f);
        this.m_lights = new IsoLightSource[3];
        this.m_lightsLock = "Model Lights Lock";
    }
    
    @Override
    public void reset() {
        super.reset();
        Arrays.fill(this.textureUninstall1, 0.0f);
        Arrays.fill(this.textureUninstall2, 0.0f);
        Arrays.fill(this.textureLightsEnables1, 0.0f);
        Arrays.fill(this.textureLightsEnables2, 0.0f);
        Arrays.fill(this.textureDamage1Enables1, 0.0f);
        Arrays.fill(this.textureDamage1Enables2, 0.0f);
        Arrays.fill(this.textureDamage2Enables1, 0.0f);
        Arrays.fill(this.textureDamage2Enables2, 0.0f);
        Arrays.fill(this.matrixBlood1Enables1, 0.0f);
        Arrays.fill(this.matrixBlood1Enables2, 0.0f);
        Arrays.fill(this.matrixBlood2Enables1, 0.0f);
        Arrays.fill(this.matrixBlood2Enables2, 0.0f);
        this.textureRustA = 0.0f;
        this.refWindows = 0.5f;
        this.refBody = 0.4f;
        this.painColor.set(0.0f, 0.5f, 0.5f);
        Arrays.fill(this.m_lights, null);
    }
    
    public void setLights(final IsoLightSource[] lights) {
        this.m_lights = lights;
    }
    
    public IsoLightSource[] getLights() {
        return this.m_lights;
    }
    
    public void UpdateLights() {
        synchronized (this.m_lightsLock) {
            ModelManager.instance.getClosestThreeLights(this.object, this.m_lights);
        }
    }
}
