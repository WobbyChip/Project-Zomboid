// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.util.ArrayDeque;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL;
import zombie.core.utils.ImageUtils;
import java.util.Comparator;
import org.lwjglx.opengl.OpenGLException;
import zombie.core.SpriteRenderer;
import zombie.core.Rand;
import zombie.core.opengl.PZGLUtil;
import org.lwjgl.opengl.GL11;
import java.nio.IntBuffer;
import zombie.interfaces.ITexture;
import zombie.core.Core;
import org.lwjgl.opengl.GL13;
import java.util.ArrayList;

public final class TextureCombiner
{
    public static final TextureCombiner instance;
    public static int count;
    private TextureFBO fbo;
    private final float m_coordinateSpaceMax = 256.0f;
    private final ArrayList<CombinerFBO> fboPool;
    
    public TextureCombiner() {
        this.fboPool = new ArrayList<CombinerFBO>();
    }
    
    public void init() throws Exception {
    }
    
    public void combineStart() {
        this.clear();
        TextureCombiner.count = 33984;
        GL13.glEnable(3042);
        GL13.glEnable(3553);
        GL13.glTexEnvi(8960, 8704, 7681);
    }
    
    public void combineEnd() {
        GL13.glActiveTexture(33984);
    }
    
    public void clear() {
        for (int i = 33985; i <= TextureCombiner.count; ++i) {
            GL13.glActiveTexture(i);
            GL13.glDisable(3553);
        }
        GL13.glActiveTexture(33984);
    }
    
    public void overlay(final Texture texture) {
        GL13.glActiveTexture(TextureCombiner.count);
        GL13.glEnable(3553);
        GL13.glEnable(3042);
        texture.bind();
        if (TextureCombiner.count > 33984) {
            GL13.glTexEnvi(8960, 8704, 34160);
            GL13.glTexEnvi(8960, 34161, 34165);
            GL13.glTexEnvi(8960, 34176, 34168);
            GL13.glTexEnvi(8960, 34177, 5890);
            GL13.glTexEnvi(8960, 34178, 34168);
            GL13.glTexEnvi(8960, 34192, 768);
            GL13.glTexEnvi(8960, 34193, 768);
            GL13.glTexEnvi(8960, 34194, 770);
            GL13.glTexEnvi(8960, 34162, 34165);
            GL13.glTexEnvi(8960, 34184, 34168);
            GL13.glTexEnvi(8960, 34185, 5890);
            GL13.glTexEnvi(8960, 34186, 34168);
            GL13.glTexEnvi(8960, 34200, 770);
            GL13.glTexEnvi(8960, 34201, 770);
            GL13.glTexEnvi(8960, 34202, 770);
        }
        ++TextureCombiner.count;
    }
    
    public Texture combine(final Texture texture, final Texture texture2) throws Exception {
        Core.getInstance().DoStartFrameStuff(texture.width, texture2.width, 1.0f, 0);
        final Texture texture3 = new Texture(texture.width, texture2.height, 16);
        if (this.fbo == null) {
            this.fbo = new TextureFBO(texture3);
        }
        else {
            this.fbo.setTexture(texture3);
        }
        GL13.glActiveTexture(33984);
        GL13.glEnable(3553);
        GL13.glBindTexture(3553, texture.getID());
        this.fbo.startDrawing(true, true);
        GL13.glBegin(7);
        GL13.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL13.glTexCoord2f(0.0f, 0.0f);
        GL13.glVertex2d(0.0, 0.0);
        GL13.glTexCoord2f(0.0f, 1.0f);
        GL13.glVertex2d(0.0, (double)texture.height);
        GL13.glTexCoord2f(1.0f, 1.0f);
        GL13.glVertex2d((double)texture.width, (double)texture.height);
        GL13.glTexCoord2f(1.0f, 0.0f);
        GL13.glVertex2d((double)texture.width, 0.0);
        GL13.glEnd();
        GL13.glBindTexture(3553, texture2.getID());
        GL13.glBegin(7);
        GL13.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL13.glTexCoord2f(0.0f, 0.0f);
        GL13.glVertex2d(0.0, 0.0);
        GL13.glTexCoord2f(0.0f, 1.0f);
        GL13.glVertex2d(0.0, (double)texture.height);
        GL13.glTexCoord2f(1.0f, 1.0f);
        GL13.glVertex2d((double)texture.width, (double)texture.height);
        GL13.glTexCoord2f(1.0f, 0.0f);
        GL13.glVertex2d((double)texture.width, 0.0);
        GL13.glEnd();
        this.fbo.endDrawing();
        Core.getInstance().DoEndFrameStuff(texture.width, texture2.width);
        return texture3;
    }
    
    public static int[] flipPixels(final int[] array, final int n, final int n2) {
        int[] array2 = null;
        if (array != null) {
            array2 = new int[n * n2];
            for (int i = 0; i < n2; ++i) {
                for (int j = 0; j < n; ++j) {
                    array2[(n2 - i - 1) * n + j] = array[i * n + j];
                }
            }
        }
        return array2;
    }
    
    private CombinerFBO getFBO(final int n, final int n2) {
        for (int i = 0; i < this.fboPool.size(); ++i) {
            final CombinerFBO combinerFBO = this.fboPool.get(i);
            if (combinerFBO.fbo.getWidth() == n && combinerFBO.fbo.getHeight() == n2) {
                return combinerFBO;
            }
        }
        return null;
    }
    
    private Texture createTexture(final int n, final int n2) {
        CombinerFBO fbo = this.getFBO(n, n2);
        Texture texture;
        if (fbo == null) {
            fbo = new CombinerFBO();
            texture = new Texture(n, n2, 16);
            fbo.fbo = new TextureFBO(texture);
            this.fboPool.add(fbo);
        }
        else {
            texture = (fbo.textures.isEmpty() ? new Texture(n, n2, 16) : fbo.textures.pop());
            texture.bind();
            GL11.glTexImage2D(3553, 0, 6408, texture.getWidthHW(), texture.getHeightHW(), 0, 6408, 5121, (IntBuffer)null);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            texture.dataid.setMinFilter(9729);
            GL13.glBindTexture(3553, Texture.lastTextureID = 0);
            fbo.fbo.setTexture(texture);
        }
        this.fbo = fbo.fbo;
        return texture;
    }
    
    public void releaseTexture(final Texture e) {
        final CombinerFBO fbo = this.getFBO(e.getWidth(), e.getHeight());
        if (fbo != null && fbo.textures.size() < 100) {
            fbo.textures.push(e);
        }
        else {
            e.destroy();
        }
    }
    
    public Texture combine(final ArrayList<TextureCombinerCommand> list) throws Exception, OpenGLException {
        PZGLUtil.checkGLErrorThrow("Enter", new Object[0]);
        final int resultingWidth = getResultingWidth(list);
        final int resultingHeight = getResultingHeight(list);
        final Texture texture = this.createTexture(resultingWidth, resultingHeight);
        GL13.glPushAttrib(24576);
        GL11.glDisable(3089);
        GL11.glDisable(2960);
        this.fbo.startDrawing(true, true);
        PZGLUtil.checkGLErrorThrow("FBO.startDrawing %s", this.fbo);
        Core.getInstance().DoStartFrameStuffSmartTextureFx(resultingWidth, resultingHeight, -1);
        PZGLUtil.checkGLErrorThrow("Core.DoStartFrameStuffFx w:%d, h:%d", resultingWidth, resultingHeight);
        for (int i = 0; i < list.size(); ++i) {
            final TextureCombinerCommand textureCombinerCommand = list.get(i);
            if (textureCombinerCommand.shader != null) {
                textureCombinerCommand.shader.Start();
            }
            GL13.glActiveTexture(33984);
            GL11.glEnable(3553);
            final Texture texture2 = (textureCombinerCommand.tex == null) ? Texture.getErrorTexture() : textureCombinerCommand.tex;
            texture2.bind();
            if (textureCombinerCommand.mask != null) {
                GL13.glActiveTexture(33985);
                GL13.glEnable(3553);
                final int lastTextureID = Texture.lastTextureID;
                if (textureCombinerCommand.mask.getTextureId() != null) {
                    textureCombinerCommand.mask.getTextureId().setMagFilter(9728);
                    textureCombinerCommand.mask.getTextureId().setMinFilter(9728);
                }
                textureCombinerCommand.mask.bind();
                Texture.lastTextureID = lastTextureID;
            }
            else {
                GL13.glActiveTexture(33985);
                GL13.glDisable(3553);
            }
            if (textureCombinerCommand.shader != null) {
                if (textureCombinerCommand.shaderParams != null) {
                    final ArrayList<TextureCombinerShaderParam> shaderParams = textureCombinerCommand.shaderParams;
                    for (int j = 0; j < shaderParams.size(); ++j) {
                        final TextureCombinerShaderParam textureCombinerShaderParam = shaderParams.get(j);
                        textureCombinerCommand.shader.setValue(textureCombinerShaderParam.name, Rand.Next(textureCombinerShaderParam.min, textureCombinerShaderParam.max));
                    }
                }
                textureCombinerCommand.shader.setValue("DIFFUSE", texture2, 0);
                if (textureCombinerCommand.mask != null) {
                    textureCombinerCommand.shader.setValue("MASK", textureCombinerCommand.mask, 1);
                }
            }
            GL13.glBlendFunc(textureCombinerCommand.blendSrc, textureCombinerCommand.blendDest);
            if (textureCombinerCommand.x != -1) {
                final float n = resultingWidth / 256.0f;
                final float n2 = resultingHeight / 256.0f;
                GL13.glBegin(7);
                GL13.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL13.glTexCoord2f(0.0f, 1.0f);
                GL13.glVertex2d((double)(textureCombinerCommand.x * n), (double)(textureCombinerCommand.y * n2));
                GL13.glTexCoord2f(0.0f, 0.0f);
                GL13.glVertex2d((double)(textureCombinerCommand.x * n), (double)((textureCombinerCommand.y + textureCombinerCommand.h) * n2));
                GL13.glTexCoord2f(1.0f, 0.0f);
                GL13.glVertex2d((double)((textureCombinerCommand.x + textureCombinerCommand.w) * n), (double)((textureCombinerCommand.y + textureCombinerCommand.h) * n2));
                GL13.glTexCoord2f(1.0f, 1.0f);
                GL13.glVertex2d((double)((textureCombinerCommand.x + textureCombinerCommand.w) * n), (double)(textureCombinerCommand.y * n2));
                GL13.glEnd();
            }
            else {
                GL13.glBegin(7);
                GL13.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL13.glTexCoord2f(0.0f, 1.0f);
                GL13.glVertex2d(0.0, 0.0);
                GL13.glTexCoord2f(0.0f, 0.0f);
                GL13.glVertex2d(0.0, (double)resultingHeight);
                GL13.glTexCoord2f(1.0f, 0.0f);
                GL13.glVertex2d((double)resultingWidth, (double)resultingHeight);
                GL13.glTexCoord2f(1.0f, 1.0f);
                GL13.glVertex2d((double)resultingWidth, 0.0);
                GL13.glEnd();
            }
            if (textureCombinerCommand.shader != null) {
                textureCombinerCommand.shader.End();
            }
            PZGLUtil.checkGLErrorThrow("TextureCombinerCommand[%d}: %s", i, textureCombinerCommand);
        }
        Core.getInstance().DoEndFrameStuffFx(resultingWidth, resultingHeight, -1);
        this.fbo.releaseTexture();
        this.fbo.endDrawing();
        PZGLUtil.checkGLErrorThrow("FBO.endDrawing: %s", this.fbo);
        GL13.glBlendFunc(770, 771);
        GL13.glActiveTexture(33985);
        GL13.glDisable(3553);
        if (Core.OptionModelTextureMipmaps) {}
        GL13.glActiveTexture(33984);
        GL13.glBindTexture(3553, Texture.lastTextureID = 0);
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        GL13.glPopAttrib();
        PZGLUtil.checkGLErrorThrow("Exit.", new Object[0]);
        return texture;
    }
    
    public static int getResultingHeight(final ArrayList<TextureCombinerCommand> list) {
        if (list.isEmpty()) {
            return 32;
        }
        final TextureCombinerCommand dominantCommand = findDominantCommand(list, Comparator.comparingInt(textureCombinerCommand -> textureCombinerCommand.tex.height));
        if (dominantCommand == null) {
            return 32;
        }
        return ImageUtils.getNextPowerOfTwoHW(dominantCommand.tex.height);
    }
    
    public static int getResultingWidth(final ArrayList<TextureCombinerCommand> list) {
        if (list.isEmpty()) {
            return 32;
        }
        final TextureCombinerCommand dominantCommand = findDominantCommand(list, Comparator.comparingInt(textureCombinerCommand -> textureCombinerCommand.tex.width));
        if (dominantCommand == null) {
            return 32;
        }
        return ImageUtils.getNextPowerOfTwoHW(dominantCommand.tex.width);
    }
    
    private static TextureCombinerCommand findDominantCommand(final ArrayList<TextureCombinerCommand> list, final Comparator<TextureCombinerCommand> comparator) {
        TextureCombinerCommand textureCombinerCommand = null;
        for (int size = list.size(), i = 0; i < size; ++i) {
            final TextureCombinerCommand textureCombinerCommand2 = list.get(i);
            if (textureCombinerCommand2.tex != null) {
                if (textureCombinerCommand == null || comparator.compare(textureCombinerCommand2, textureCombinerCommand) > 0) {
                    textureCombinerCommand = textureCombinerCommand2;
                }
            }
        }
        return textureCombinerCommand;
    }
    
    private void createMipMaps(final Texture texture) {
        if (!GL.getCapabilities().OpenGL30) {
            return;
        }
        GL13.glActiveTexture(33984);
        texture.bind();
        GL30.glGenerateMipmap(3553);
        final int minFilter = 9987;
        GL11.glTexParameteri(3553, 10241, minFilter);
        texture.dataid.setMinFilter(minFilter);
    }
    
    static {
        instance = new TextureCombiner();
        TextureCombiner.count = 0;
    }
    
    private static final class CombinerFBO
    {
        TextureFBO fbo;
        final ArrayDeque<Texture> textures;
        
        private CombinerFBO() {
            this.textures = new ArrayDeque<Texture>();
        }
    }
}
