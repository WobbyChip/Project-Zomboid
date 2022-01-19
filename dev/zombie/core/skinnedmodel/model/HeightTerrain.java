// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.nio.ByteOrder;
import zombie.core.SpriteRenderer;
import zombie.core.PerformanceSettings;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.textures.Texture;
import zombie.iso.IsoWorld;
import zombie.core.Core;
import zombie.iso.IsoUtils;
import zombie.iso.IsoCamera;
import zombie.characters.IsoPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;
import java.util.ArrayList;
import zombie.core.Rand;
import zombie.creative.creativerects.OpenSimplexNoise;
import java.nio.ByteBuffer;

public final class HeightTerrain
{
    private final ByteBuffer buffer;
    public VertexBufferObject vb;
    public static float isoAngle;
    public static float scale;
    OpenSimplexNoise noise;
    static float[] lightAmbient;
    static float[] lightDiffuse;
    static float[] lightPosition;
    static float[] specular;
    static float[] shininess;
    static float[] emission;
    static float[] ambient;
    static float[] diffuse;
    static ByteBuffer temp;
    
    public HeightTerrain(final int n, final int n2) {
        this.noise = new OpenSimplexNoise(Rand.Next(10000000));
        final ArrayList<VertexPositionNormalTangentTextureSkin> list = new ArrayList<VertexPositionNormalTangentTextureSkin>();
        final ArrayList<Integer> list2 = new ArrayList<Integer>();
        final Vector2 vector2 = new Vector2(2.0f, 0.0f);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                final float n3 = (float)this.calc((float)i, (float)j) * 1.0f + 1.0f;
                final VertexPositionNormalTangentTextureSkin e = new VertexPositionNormalTangentTextureSkin();
                (e.Position = new Vector3()).set((float)(-i), n3 * 30.0f, (float)(-j));
                (e.Normal = new Vector3()).set(0.0f, 1.0f, 0.0f);
                e.Normal.normalize();
                e.TextureCoordinates = new Vector2();
                e.TextureCoordinates = new Vector2(i / (float)(n - 1) * 16.0f, j / (float)(n2 - 1) * 16.0f);
                list.add(e);
            }
        }
        int index = 0;
        for (int k = 0; k < n; ++k) {
            for (int l = 0; l < n2; ++l) {
                Math.min(1.0f, Math.max(0.0f, (float)this.calc((float)k, (float)l) * 1.0f + 1.0f));
                final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = list.get(index);
                final Vector3 vector3 = new Vector3();
                final Vector3 vector4 = new Vector3();
                final float n4 = (float)this.calc((float)(k + 1), (float)l) * 1.0f + 1.0f;
                final float n5 = (float)this.calc((float)(k - 1), (float)l) * 1.0f + 1.0f;
                final float n6 = (float)this.calc((float)k, (float)(l + 1)) * 1.0f + 1.0f;
                final float n7 = (float)this.calc((float)k, (float)(l - 1)) * 1.0f + 1.0f;
                final float n8 = n4 * 700.0f;
                final float n9 = n5 * 700.0f;
                final float n10 = n6 * 700.0f;
                final float n11 = n7 * 700.0f;
                vector3.set(vector2.x, vector2.y, n8 - n9);
                vector4.set(vector2.y, vector2.x, n10 - n11);
                vector3.normalize();
                vector4.normalize();
                final Vector3 cross = vector3.cross(vector4);
                vertexPositionNormalTangentTextureSkin.Normal.x(cross.x());
                vertexPositionNormalTangentTextureSkin.Normal.y(cross.z());
                vertexPositionNormalTangentTextureSkin.Normal.z(cross.y());
                vertexPositionNormalTangentTextureSkin.Normal.normalize();
                System.out.println(invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z()));
                vertexPositionNormalTangentTextureSkin.Normal.normalize();
                ++index;
            }
        }
        int n12 = 0;
        for (int n13 = 0; n13 < n2 - 1; ++n13) {
            if ((n13 & 0x1) == 0x0) {
                for (int n14 = 0; n14 < n; ++n14) {
                    list2.add(n14 + (n13 + 1) * n);
                    list2.add(n14 + n13 * n);
                    ++n12;
                    ++n12;
                }
            }
            else {
                for (int n15 = n - 1; n15 > 0; --n15) {
                    list2.add(n15 - 1 + n13 * n);
                    list2.add(n15 + (n13 + 1) * n);
                    ++n12;
                    ++n12;
                }
            }
        }
        if ((n & 0x1) > 0 && n2 > 2) {
            list2.add((n2 - 1) * n);
            ++n12;
        }
        this.vb = new VertexBufferObject();
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(list.size() * 36);
        for (int index2 = 0; index2 < list.size(); ++index2) {
            final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin2 = list.get(index2);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.x());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.y());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.z());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.x());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.y());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.z());
            byteBuffer.putInt(-1);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.TextureCoordinates.x);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.TextureCoordinates.y);
        }
        byteBuffer.flip();
        final int[] array = new int[list2.size()];
        for (int n16 = 0; n16 < list2.size(); ++n16) {
            array[n16] = list2.get(list2.size() - 1 - n16);
        }
        this.vb._handle = this.vb.LoadSoftwareVBO(byteBuffer, this.vb._handle, array);
        this.buffer = byteBuffer;
    }
    
    double calcTerrain(float n, float n2) {
        n *= 10.0f;
        n2 *= 10.0f;
        final double n3 = this.noise.eval(n / 900.0f, n2 / 600.0f, 0.0) + this.noise.eval(n / 600.0f, n2 / 600.0f, 0.0) / 4.0 + (this.noise.eval(n / 300.0f, n2 / 300.0f, 0.0) + 1.0) / 8.0 + (this.noise.eval(n / 150.0f, n2 / 150.0f, 0.0) + 1.0) / 16.0 + (this.noise.eval(n / 75.0f, n2 / 75.0f, 0.0) + 1.0) / 32.0;
        final double n4 = (this.noise.eval(n, n2, 0.0) + 1.0) / 2.0 * ((this.noise.eval(n, n2, 0.0) + 1.0) / 2.0);
        return n3;
    }
    
    double calc(final float n, final float n2) {
        return this.calcTerrain(n, n2);
    }
    
    public void pushView(final int n, final int n2, final int n3) {
        GL11.glDepthMask(false);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        final int n4 = 0;
        final int n5 = 0;
        final int n6 = n4 + IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex());
        final int n7 = n5 + IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex());
        final double n8 = IsoUtils.XToIso((float)n4, (float)n5, 0.0f);
        final double n9 = IsoUtils.YToIso(0.0f, 0.0f, 0.0f);
        final double n10 = IsoUtils.XToIso((float)Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()), 0.0f, 0.0f);
        final double n11 = IsoUtils.YToIso((float)n6, (float)n5, 0.0f);
        final double n12 = IsoUtils.XToIso((float)n6, (float)n7, 0.0f);
        final double n13 = IsoUtils.YToIso((float)Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()), (float)Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()), 6.0f);
        final double n14 = IsoUtils.XToIso(-128.0f, (float)Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()), 6.0f);
        final double n15 = IsoUtils.YToIso((float)n4, (float)n7, 0.0f) - n11;
        final double n16 = Math.abs(Core.getInstance().getOffscreenWidth(0)) / 1920.0f;
        final double n17 = Math.abs(Core.getInstance().getOffscreenHeight(0)) / 1080.0f;
        GL11.glLoadIdentity();
        GL11.glOrtho(-n16 / 2.0, n16 / 2.0, -n17 / 2.0, n17 / 2.0, -10.0, 10.0);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glScaled((double)HeightTerrain.scale, (double)HeightTerrain.scale, (double)HeightTerrain.scale);
        GL11.glRotatef(HeightTerrain.isoAngle, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslated((double)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2), 0.0, (double)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2));
        GL11.glDepthRange(-100.0, 100.0);
    }
    
    public void popView() {
        GL11.glEnable(3008);
        GL11.glDepthFunc(519);
        GL11.glDepthMask(false);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
    }
    
    public void render() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glDisable(2884);
        GL11.glEnable(2929);
        GL11.glDepthFunc(519);
        GL11.glColorMask(true, true, true, true);
        GL11.glAlphaFunc(519, 0.0f);
        GL11.glDepthFunc(519);
        GL11.glDepthRange(-10.0, 10.0);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glEnable(16385);
        GL11.glEnable(2929);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glAlphaFunc(519, 0.0f);
        GL11.glDisable(3089);
        this.doLighting();
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glCullFace(1029);
        this.pushView(IsoPlayer.getInstance().getCurrentSquare().getChunk().wx / 30 * 300, IsoPlayer.getInstance().getCurrentSquare().getChunk().wy / 30 * 300, 0);
        Texture.getSharedTexture("media/textures/grass.png").bind();
        this.vb.DrawStrip(null);
        this.popView();
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glDisable(2929);
        GL11.glEnable(6144);
        if (PerformanceSettings.ModelLighting) {
            GL11.glDisable(2903);
            GL11.glDisable(2896);
            GL11.glDisable(16384);
            GL11.glDisable(16385);
        }
        GL11.glDepthRange(0.0, 100.0);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glEnable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glEnable(3553);
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
    }
    
    private void doLighting() {
        HeightTerrain.temp.order(ByteOrder.nativeOrder());
        HeightTerrain.temp.clear();
        GL11.glColorMaterial(1032, 5634);
        GL11.glDisable(2903);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glDisable(16385);
        HeightTerrain.lightAmbient[0] = 0.7f;
        HeightTerrain.lightAmbient[1] = 0.7f;
        HeightTerrain.lightAmbient[2] = 0.7f;
        HeightTerrain.lightAmbient[3] = 0.5f;
        HeightTerrain.lightDiffuse[0] = 0.5f;
        HeightTerrain.lightDiffuse[1] = 0.5f;
        HeightTerrain.lightDiffuse[2] = 0.5f;
        HeightTerrain.lightDiffuse[3] = 1.0f;
        final Vector3 vector3 = new Vector3(1.0f, 1.0f, 1.0f);
        vector3.normalize();
        HeightTerrain.lightPosition[0] = -vector3.x();
        HeightTerrain.lightPosition[1] = vector3.y();
        HeightTerrain.lightPosition[2] = -vector3.z();
        HeightTerrain.lightPosition[3] = 0.0f;
        GL11.glLightfv(16384, 4608, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.lightAmbient).flip());
        GL11.glLightfv(16384, 4609, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.lightDiffuse).flip());
        GL11.glLightfv(16384, 4611, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.lightPosition).flip());
        GL11.glLightf(16384, 4615, 0.0f);
        GL11.glLightf(16384, 4616, 0.0f);
        GL11.glLightf(16384, 4617, 0.0f);
        HeightTerrain.specular[0] = 0.0f;
        HeightTerrain.specular[1] = 0.0f;
        HeightTerrain.specular[2] = 0.0f;
        HeightTerrain.specular[3] = 0.0f;
        GL11.glMaterialfv(1032, 4610, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.specular).flip());
        GL11.glMaterialfv(1032, 5633, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.specular).flip());
        GL11.glMaterialfv(1032, 5632, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.specular).flip());
        HeightTerrain.ambient[0] = 0.6f;
        HeightTerrain.ambient[1] = 0.6f;
        HeightTerrain.ambient[2] = 0.6f;
        HeightTerrain.ambient[3] = 1.0f;
        HeightTerrain.diffuse[0] = 0.6f;
        HeightTerrain.diffuse[1] = 0.6f;
        HeightTerrain.diffuse[2] = 0.6f;
        HeightTerrain.diffuse[3] = 0.6f;
        GL11.glMaterialfv(1032, 4608, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.ambient).flip());
        GL11.glMaterialfv(1032, 4609, HeightTerrain.temp.asFloatBuffer().put(HeightTerrain.diffuse).flip());
    }
    
    static {
        HeightTerrain.isoAngle = 62.65607f;
        HeightTerrain.scale = 0.047085002f;
        HeightTerrain.lightAmbient = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.lightPosition = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.shininess = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.emission = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.ambient = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.diffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        HeightTerrain.temp = ByteBuffer.allocateDirect(16);
    }
}
