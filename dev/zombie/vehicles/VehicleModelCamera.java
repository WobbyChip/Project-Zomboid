// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import org.joml.Math;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;

public final class VehicleModelCamera extends ModelCamera
{
    public static final VehicleModelCamera instance;
    
    @Override
    public void Begin() {
        if (this.m_bUseWorldIso) {
            Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, this.m_useAngle, true);
            GL11.glDepthMask(this.bDepthMask);
            return;
        }
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(-192.0, 192.0, -192.0, 192.0, -1000.0, 1000.0);
        final float sqrt = Math.sqrt(2048.0f);
        GL11.glScalef(-sqrt, sqrt, sqrt);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
    }
    
    @Override
    public void End() {
        if (this.m_bUseWorldIso) {
            Core.getInstance().DoPopIsoStuff();
            return;
        }
        GL11.glDepthFunc(519);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
    }
    
    static {
        instance = new VehicleModelCamera();
    }
}
