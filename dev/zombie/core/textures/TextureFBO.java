// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import org.lwjgl.opengl.GL;
import zombie.debug.DebugLog;
import org.lwjgl.opengl.GL11;
import java.nio.IntBuffer;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.interfaces.ITexture;
import gnu.trove.stack.array.TIntArrayStack;

public final class TextureFBO
{
    private static IGLFramebufferObject funcs;
    private static int lastID;
    private static final TIntArrayStack stack;
    private int id;
    ITexture texture;
    private int depth;
    private int width;
    private int height;
    private static Boolean checked;
    
    public void swapTexture(final ITexture texture) {
        assert TextureFBO.lastID == this.id;
        if (texture == null || texture == this.texture) {
            return;
        }
        if (texture.getWidth() != this.width || texture.getHeight() != this.height) {
            return;
        }
        if (texture.getID() == -1) {
            texture.bind();
        }
        final IGLFramebufferObject funcs = getFuncs();
        funcs.glFramebufferTexture2D(funcs.GL_FRAMEBUFFER(), funcs.GL_COLOR_ATTACHMENT0(), 3553, texture.getID(), 0);
        this.texture = texture;
    }
    
    public TextureFBO(final ITexture texture) {
        this(texture, true);
    }
    
    public TextureFBO(final ITexture texture, final boolean b) {
        this.id = 0;
        this.depth = 0;
        RenderThread.invokeOnRenderContext(texture, b, this::init);
    }
    
    private void init(final ITexture texture, final boolean b) {
        final int lastID = TextureFBO.lastID;
        try {
            this.initInternal(texture, b);
        }
        finally {
            final IGLFramebufferObject funcs;
            funcs.glBindFramebuffer((funcs = getFuncs()).GL_FRAMEBUFFER(), TextureFBO.lastID = lastID);
        }
    }
    
    public static IGLFramebufferObject getFuncs() {
        if (TextureFBO.funcs == null) {
            checkFBOSupport();
        }
        return TextureFBO.funcs;
    }
    
    private void initInternal(final ITexture texture, final boolean b) {
        final IGLFramebufferObject funcs = getFuncs();
        try {
            PZGLUtil.checkGLErrorThrow("Enter.", new Object[0]);
            this.texture = texture;
            this.width = this.texture.getWidth();
            this.height = this.texture.getHeight();
            if (!checkFBOSupport()) {
                throw new RuntimeException("Could not create FBO. FBO's not supported.");
            }
            if (this.texture == null) {
                throw new NullPointerException("Could not create FBO. Texture is null.");
            }
            this.texture.bind();
            PZGLUtil.checkGLErrorThrow("Binding texture. %s", this.texture);
            GL11.glTexImage2D(3553, 0, 6408, this.texture.getWidthHW(), this.texture.getHeightHW(), 0, 6408, 5121, (IntBuffer)null);
            PZGLUtil.checkGLErrorThrow("glTexImage2D(width: %d, height: %d)", this.texture.getWidthHW(), this.texture.getHeightHW());
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glBindTexture(3553, Texture.lastTextureID = 0);
            this.id = funcs.glGenFramebuffers();
            PZGLUtil.checkGLErrorThrow("glGenFrameBuffers", new Object[0]);
            funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), this.id);
            PZGLUtil.checkGLErrorThrow("glBindFramebuffer(%d)", this.id);
            funcs.glFramebufferTexture2D(funcs.GL_FRAMEBUFFER(), funcs.GL_COLOR_ATTACHMENT0(), 3553, this.texture.getID(), 0);
            PZGLUtil.checkGLErrorThrow("glFramebufferTexture2D texture: %s", this.texture);
            this.depth = funcs.glGenRenderbuffers();
            PZGLUtil.checkGLErrorThrow("glGenRenderbuffers", new Object[0]);
            funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), this.depth);
            PZGLUtil.checkGLErrorThrow("glBindRenderbuffer depth: %d", this.depth);
            if (b) {
                funcs.glRenderbufferStorage(funcs.GL_RENDERBUFFER(), funcs.GL_DEPTH24_STENCIL8(), this.texture.getWidthHW(), this.texture.getHeightHW());
                PZGLUtil.checkGLErrorThrow("glRenderbufferStorage(width: %d, height: %d)", this.texture.getWidthHW(), this.texture.getHeightHW());
                funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), 0);
                funcs.glFramebufferRenderbuffer(funcs.GL_FRAMEBUFFER(), funcs.GL_DEPTH_ATTACHMENT(), funcs.GL_RENDERBUFFER(), this.depth);
                PZGLUtil.checkGLErrorThrow("glFramebufferRenderbuffer(depth: %d)", this.depth);
                funcs.glFramebufferRenderbuffer(funcs.GL_FRAMEBUFFER(), funcs.GL_STENCIL_ATTACHMENT(), funcs.GL_RENDERBUFFER(), this.depth);
                PZGLUtil.checkGLErrorThrow("glFramebufferRenderbuffer(stencil: %d)", this.depth);
            }
            else {
                funcs.glRenderbufferStorage(funcs.GL_RENDERBUFFER(), 6402, this.texture.getWidthHW(), this.texture.getHeightHW());
                PZGLUtil.checkGLErrorThrow("glRenderbufferStorage(width: %d, height: %d)", this.texture.getWidthHW(), this.texture.getHeightHW());
                funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), 0);
                funcs.glFramebufferRenderbuffer(funcs.GL_FRAMEBUFFER(), funcs.GL_DEPTH_ATTACHMENT(), funcs.GL_RENDERBUFFER(), this.depth);
                PZGLUtil.checkGLErrorThrow("glFramebufferRenderbuffer(depth: %d)", this.depth);
            }
            final int glCheckFramebufferStatus = funcs.glCheckFramebufferStatus(funcs.GL_FRAMEBUFFER());
            if (glCheckFramebufferStatus != funcs.GL_FRAMEBUFFER_COMPLETE()) {
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_UNDEFINED()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_UNDEFINED");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_FORMATS()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_FORMATS");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_UNSUPPORTED()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_UNSUPPORTED");
                }
                if (glCheckFramebufferStatus == funcs.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE()) {
                    DebugLog.General.error((Object)"glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
                }
                throw new RuntimeException("Could not create FBO!");
            }
        }
        catch (Exception ex) {
            funcs.glDeleteFramebuffers(this.id);
            funcs.glDeleteRenderbuffers(this.depth);
            this.id = 0;
            this.depth = 0;
            this.texture = null;
            throw ex;
        }
    }
    
    public static boolean checkFBOSupport() {
        if (TextureFBO.checked != null) {
            return TextureFBO.checked;
        }
        if (GL.getCapabilities().OpenGL30) {
            DebugLog.General.debugln("OpenGL 3.0 framebuffer objects supported");
            TextureFBO.funcs = new GLFramebufferObject30();
            return TextureFBO.checked = Boolean.TRUE;
        }
        if (GL.getCapabilities().GL_ARB_framebuffer_object) {
            DebugLog.General.debugln("GL_ARB_framebuffer_object supported");
            TextureFBO.funcs = new GLFramebufferObjectARB();
            return TextureFBO.checked = Boolean.TRUE;
        }
        if (GL.getCapabilities().GL_EXT_framebuffer_object) {
            DebugLog.General.debugln("GL_EXT_framebuffer_object supported");
            if (!GL.getCapabilities().GL_EXT_packed_depth_stencil) {
                DebugLog.General.debugln("GL_EXT_packed_depth_stencil not supported");
            }
            TextureFBO.funcs = new GLFramebufferObjectEXT();
            return TextureFBO.checked = Boolean.TRUE;
        }
        DebugLog.General.debugln("None of OpenGL 3.0, GL_ARB_framebuffer_object or GL_EXT_framebuffer_object are supported, zoom disabled");
        return TextureFBO.checked = Boolean.TRUE;
    }
    
    public void destroy() {
        if (this.id == 0 || this.depth == 0) {
            return;
        }
        if (TextureFBO.lastID == this.id) {
            TextureFBO.lastID = 0;
        }
        final IGLFramebufferObject iglFramebufferObject;
        RenderThread.invokeOnRenderContext(() -> {
            if (this.texture != null) {
                this.texture.destroy();
                this.texture = null;
            }
            getFuncs();
            iglFramebufferObject.glDeleteFramebuffers(this.id);
            iglFramebufferObject.glDeleteRenderbuffers(this.depth);
            this.id = 0;
            this.depth = 0;
        });
    }
    
    public void destroyLeaveTexture() {
        if (this.id == 0 || this.depth == 0) {
            return;
        }
        final IGLFramebufferObject iglFramebufferObject;
        RenderThread.invokeOnRenderContext(() -> {
            this.texture = null;
            getFuncs();
            iglFramebufferObject.glDeleteFramebuffers(this.id);
            iglFramebufferObject.glDeleteRenderbuffers(this.depth);
            this.id = 0;
            this.depth = 0;
        });
    }
    
    public void releaseTexture() {
        final IGLFramebufferObject funcs = getFuncs();
        funcs.glFramebufferTexture2D(funcs.GL_FRAMEBUFFER(), funcs.GL_COLOR_ATTACHMENT0(), 3553, 0, 0);
        this.texture = null;
    }
    
    public void endDrawing() {
        if (TextureFBO.stack.size() != 0) {
            TextureFBO.lastID = TextureFBO.stack.pop();
        }
        else {
            TextureFBO.lastID = 0;
        }
        final IGLFramebufferObject funcs = getFuncs();
        funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), TextureFBO.lastID);
    }
    
    public ITexture getTexture() {
        return this.texture;
    }
    
    public int getBufferId() {
        return this.id;
    }
    
    public boolean isDestroyed() {
        return this.texture == null || this.id == 0 || this.depth == 0;
    }
    
    public void startDrawing() {
        this.startDrawing(false, false);
    }
    
    public void startDrawing(final boolean b, final boolean b2) {
        TextureFBO.stack.push(TextureFBO.lastID);
        TextureFBO.lastID = this.id;
        final IGLFramebufferObject funcs = getFuncs();
        funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), this.id);
        if (this.texture == null) {
            return;
        }
        if (b) {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, b2 ? 0.0f : 1.0f);
            GL11.glClear(16640);
            if (b2) {
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }
    
    public void setTexture(final Texture texture) {
        final int lastID = TextureFBO.lastID;
        final IGLFramebufferObject funcs = getFuncs();
        funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), TextureFBO.lastID = this.id);
        this.swapTexture(texture);
        funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), TextureFBO.lastID = lastID);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public static int getCurrentID() {
        return TextureFBO.lastID;
    }
    
    public static void reset() {
        TextureFBO.stack.clear();
        if (TextureFBO.lastID != 0) {
            final IGLFramebufferObject funcs;
            funcs.glBindFramebuffer((funcs = getFuncs()).GL_FRAMEBUFFER(), TextureFBO.lastID = 0);
        }
    }
    
    static {
        TextureFBO.lastID = 0;
        stack = new TIntArrayStack();
        TextureFBO.checked = null;
    }
}
