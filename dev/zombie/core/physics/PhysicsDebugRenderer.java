// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.physics;

import zombie.iso.IsoUtils;
import zombie.core.textures.Texture;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.characters.IsoPlayer;
import gnu.trove.list.array.TFloatArrayList;
import zombie.core.opengl.VBOLines;
import zombie.popman.ObjectPool;
import zombie.core.textures.TextureDraw;

public final class PhysicsDebugRenderer extends TextureDraw.GenericDrawer
{
    private static final ObjectPool<PhysicsDebugRenderer> POOL;
    private static final VBOLines vboLines;
    private float camOffX;
    private float camOffY;
    private float deferredX;
    private float deferredY;
    private int drawOffsetX;
    private int drawOffsetY;
    private int playerIndex;
    private float playerX;
    private float playerY;
    private float playerZ;
    private float offscreenWidth;
    private float offscreenHeight;
    private final TFloatArrayList elements;
    
    public static PhysicsDebugRenderer alloc() {
        return PhysicsDebugRenderer.POOL.alloc();
    }
    
    public void release() {
        PhysicsDebugRenderer.POOL.release(this);
    }
    
    public PhysicsDebugRenderer() {
        this.elements = new TFloatArrayList();
    }
    
    public void init(final IsoPlayer isoPlayer) {
        this.playerIndex = isoPlayer.getPlayerNum();
        this.camOffX = IsoCamera.getRightClickOffX() + IsoCamera.PLAYER_OFFSET_X;
        this.camOffY = IsoCamera.getRightClickOffY() + IsoCamera.PLAYER_OFFSET_Y;
        this.camOffX += this.XToScreenExact(isoPlayer.x - (int)isoPlayer.x, isoPlayer.y - (int)isoPlayer.y, 0.0f, 0);
        this.camOffY += this.YToScreenExact(isoPlayer.x - (int)isoPlayer.x, isoPlayer.y - (int)isoPlayer.y, 0.0f, 0);
        this.deferredX = IsoCamera.cameras[this.playerIndex].DeferedX;
        this.deferredY = IsoCamera.cameras[this.playerIndex].DeferedY;
        this.drawOffsetX = (int)isoPlayer.x;
        this.drawOffsetY = (int)isoPlayer.y;
        this.playerX = isoPlayer.x;
        this.playerY = isoPlayer.y;
        this.playerZ = isoPlayer.z;
        this.offscreenWidth = (float)Core.getInstance().getOffscreenWidth(this.playerIndex);
        this.offscreenHeight = (float)Core.getInstance().getOffscreenHeight(this.playerIndex);
        this.elements.clear();
        this.n_debugDrawWorld((int)WorldSimulation.instance.offsetX - this.drawOffsetX, (int)WorldSimulation.instance.offsetY - this.drawOffsetY);
    }
    
    @Override
    public void render() {
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3553);
        GL11.glDisable(3042);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, (double)this.offscreenWidth, (double)this.offscreenHeight, 0.0, 10000.0, -10000.0);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        final int n = -this.drawOffsetX;
        final int n2 = -this.drawOffsetY;
        final float deferredX = this.deferredX;
        final float deferredY = this.deferredY;
        GL11.glTranslatef(this.offscreenWidth / 2.0f, this.offscreenHeight / 2.0f, 0.0f);
        GL11.glTranslatef(-(this.XToScreenExact(deferredX, deferredY, this.playerZ, 0) + this.camOffX), -(this.YToScreenExact(deferredX, deferredY, this.playerZ, 0) + this.camOffY), 0.0f);
        final int n3 = (int)(n + WorldSimulation.instance.offsetX);
        final int n4 = (int)(n2 + WorldSimulation.instance.offsetY);
        final int n5 = 32 * Core.TileScale;
        final float n6 = (float)Math.sqrt(n5 * n5 + n5 * n5);
        GL11.glScalef(n6, n6, n6);
        GL11.glRotatef(210.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(-45.0f, 0.0f, 1.0f, 0.0f);
        PhysicsDebugRenderer.vboLines.setLineWidth(1.0f);
        int i = 0;
        while (i < this.elements.size()) {
            PhysicsDebugRenderer.vboLines.addLine(this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), 1.0f, this.elements.getQuick(i++), this.elements.getQuick(i++), this.elements.getQuick(i++), 1.0f);
        }
        PhysicsDebugRenderer.vboLines.flush();
        GL11.glLineWidth(1.0f);
        GL11.glBegin(1);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 1.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 1.0);
        GL11.glEnd();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(3553);
        GL11.glPopAttrib();
        Texture.lastTextureID = -1;
    }
    
    @Override
    public void postRender() {
        this.release();
    }
    
    public float YToScreenExact(final float n, final float n2, final float n3, final int n4) {
        return IsoUtils.YToScreen(n, n2, n3, n4);
    }
    
    public float XToScreenExact(final float n, final float n2, final float n3, final int n4) {
        return IsoUtils.XToScreen(n, n2, n3, n4);
    }
    
    public void drawLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        if (n < -1000.0f || n > 1000.0f || n2 < -1000.0f || n2 > 1000.0f) {
            return;
        }
        this.elements.add(n);
        this.elements.add(n2);
        this.elements.add(n3);
        this.elements.add(n4);
        this.elements.add(n5);
        this.elements.add(n6);
        this.elements.add(n7);
        this.elements.add(n8);
        this.elements.add(n9);
        this.elements.add(n10);
        this.elements.add(n11);
        this.elements.add(n12);
    }
    
    public void drawSphere(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
    }
    
    public void drawTriangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13) {
    }
    
    public void drawContactPoint(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final int n8, final float n9, final float n10, final float n11) {
    }
    
    public native void n_debugDrawWorld(final int p0, final int p1);
    
    static {
        POOL = new ObjectPool<PhysicsDebugRenderer>(PhysicsDebugRenderer::new);
        vboLines = new VBOLines();
    }
}
