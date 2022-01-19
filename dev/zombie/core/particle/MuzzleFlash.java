// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.particle;

import org.lwjgl.opengl.GL11;
import zombie.core.opengl.PZGLUtil;
import org.joml.Matrix4f;
import zombie.core.textures.Texture;

public class MuzzleFlash
{
    private static Texture muzzleFlashStar;
    private static Texture muzzleFlashSide;
    
    public static void init() {
        MuzzleFlash.muzzleFlashStar = Texture.getSharedTexture("media/textures/muzzle-flash-star.png");
        MuzzleFlash.muzzleFlashSide = Texture.getSharedTexture("media/textures/muzzle-flash-side.png");
    }
    
    public static void render(final Matrix4f matrix4f) {
        if (MuzzleFlash.muzzleFlashStar == null || !MuzzleFlash.muzzleFlashStar.isReady()) {
            return;
        }
        if (MuzzleFlash.muzzleFlashSide == null || !MuzzleFlash.muzzleFlashSide.isReady()) {
            return;
        }
        PZGLUtil.pushAndMultMatrix(5888, matrix4f);
        GL11.glDisable(2884);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        MuzzleFlash.muzzleFlashStar.bind();
        final float n = 0.15f;
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-n / 2.0f, n / 2.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(n / 2.0f, n / 2.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(n / 2.0f, -n / 2.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-n / 2.0f, -n / 2.0f, 0.0f);
        GL11.glEnd();
        MuzzleFlash.muzzleFlashSide.bind();
        final float n2 = 0.05f;
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, n2 / 2.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(0.0f, n2 / 2.0f, n2 * 2.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(0.0f, -n2 / 2.0f, n2 * 2.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, -n2 / 2.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(2884);
        PZGLUtil.popMatrix(5888);
    }
}
