// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import org.joml.Math;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;

public final class CharacterModelCamera extends ModelCamera
{
    public static final CharacterModelCamera instance;
    
    @Override
    public void Begin() {
        if (this.m_bUseWorldIso) {
            Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, this.m_useAngle, this.m_bInVehicle);
            GL11.glDepthMask(this.bDepthMask);
            return;
        }
        final int n = 1024;
        final int n2 = 1024;
        final float n3 = 42.75f;
        final float n4 = 0.0f;
        final float n5 = -0.45f;
        final float n6 = 0.0f;
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        final float n7 = n / (float)n2;
        if (false) {
            GL11.glOrtho((double)(-n3 * n7), (double)(n3 * n7), (double)n3, (double)(-n3), -100.0, 100.0);
        }
        else {
            GL11.glOrtho((double)(-n3 * n7), (double)(n3 * n7), (double)(-n3), (double)n3, -100.0, 100.0);
        }
        final float sqrt = Math.sqrt(2048.0f);
        GL11.glScalef(-sqrt, sqrt, sqrt);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(n4, n5, n6);
        GL11.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotated(Math.toDegrees((double)this.m_useAngle) + 45.0, 0.0, 1.0, 0.0);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glDepthMask(this.bDepthMask);
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
        instance = new CharacterModelCamera();
    }
}
