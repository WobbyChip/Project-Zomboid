// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import org.lwjgl.opengl.EXTFramebufferObject;

public final class GLFramebufferObjectEXT implements IGLFramebufferObject
{
    @Override
    public int GL_FRAMEBUFFER() {
        return 36160;
    }
    
    @Override
    public int GL_RENDERBUFFER() {
        return 36161;
    }
    
    @Override
    public int GL_COLOR_ATTACHMENT0() {
        return 36064;
    }
    
    @Override
    public int GL_DEPTH_ATTACHMENT() {
        return 36096;
    }
    
    @Override
    public int GL_STENCIL_ATTACHMENT() {
        return 36128;
    }
    
    @Override
    public int GL_DEPTH_STENCIL() {
        return 34041;
    }
    
    @Override
    public int GL_DEPTH24_STENCIL8() {
        return 35056;
    }
    
    @Override
    public int GL_FRAMEBUFFER_COMPLETE() {
        return 36053;
    }
    
    @Override
    public int GL_FRAMEBUFFER_UNDEFINED() {
        return 0;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT() {
        return 36054;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT() {
        return 36055;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS() {
        return 36057;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_FORMATS() {
        return 36058;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER() {
        return 36059;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER() {
        return 36060;
    }
    
    @Override
    public int GL_FRAMEBUFFER_UNSUPPORTED() {
        return 36061;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE() {
        return 0;
    }
    
    @Override
    public int glGenFramebuffers() {
        return EXTFramebufferObject.glGenFramebuffersEXT();
    }
    
    @Override
    public void glBindFramebuffer(final int n, final int n2) {
        EXTFramebufferObject.glBindFramebufferEXT(n, n2);
    }
    
    @Override
    public void glFramebufferTexture2D(final int n, final int n2, final int n3, final int n4, final int n5) {
        EXTFramebufferObject.glFramebufferTexture2DEXT(n, n2, n3, n4, n5);
    }
    
    @Override
    public int glGenRenderbuffers() {
        return EXTFramebufferObject.glGenRenderbuffersEXT();
    }
    
    @Override
    public void glBindRenderbuffer(final int n, final int n2) {
        EXTFramebufferObject.glBindRenderbufferEXT(n, n2);
    }
    
    @Override
    public void glRenderbufferStorage(final int n, final int n2, final int n3, final int n4) {
        EXTFramebufferObject.glRenderbufferStorageEXT(n, n2, n3, n4);
    }
    
    @Override
    public void glFramebufferRenderbuffer(final int n, final int n2, final int n3, final int n4) {
        EXTFramebufferObject.glFramebufferRenderbufferEXT(n, n2, n3, n4);
    }
    
    @Override
    public int glCheckFramebufferStatus(final int n) {
        return EXTFramebufferObject.glCheckFramebufferStatusEXT(n);
    }
    
    @Override
    public void glDeleteFramebuffers(final int n) {
        EXTFramebufferObject.glDeleteFramebuffersEXT(n);
    }
    
    @Override
    public void glDeleteRenderbuffers(final int n) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(n);
    }
}
