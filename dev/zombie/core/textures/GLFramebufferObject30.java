// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import org.lwjgl.opengl.GL30;

public final class GLFramebufferObject30 implements IGLFramebufferObject
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
        return 33305;
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
        return 0;
    }
    
    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_FORMATS() {
        return 0;
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
        return 36182;
    }
    
    @Override
    public int glGenFramebuffers() {
        return GL30.glGenFramebuffers();
    }
    
    @Override
    public void glBindFramebuffer(final int n, final int n2) {
        GL30.glBindFramebuffer(n, n2);
    }
    
    @Override
    public void glFramebufferTexture2D(final int n, final int n2, final int n3, final int n4, final int n5) {
        GL30.glFramebufferTexture2D(n, n2, n3, n4, n5);
    }
    
    @Override
    public int glGenRenderbuffers() {
        return GL30.glGenRenderbuffers();
    }
    
    @Override
    public void glBindRenderbuffer(final int n, final int n2) {
        GL30.glBindRenderbuffer(n, n2);
    }
    
    @Override
    public void glRenderbufferStorage(final int n, final int n2, final int n3, final int n4) {
        GL30.glRenderbufferStorage(n, n2, n3, n4);
    }
    
    @Override
    public void glFramebufferRenderbuffer(final int n, final int n2, final int n3, final int n4) {
        GL30.glFramebufferRenderbuffer(n, n2, n3, n4);
    }
    
    @Override
    public int glCheckFramebufferStatus(final int n) {
        return GL30.glCheckFramebufferStatus(n);
    }
    
    @Override
    public void glDeleteFramebuffers(final int n) {
        GL30.glDeleteFramebuffers(n);
    }
    
    @Override
    public void glDeleteRenderbuffers(final int n) {
        GL30.glDeleteRenderbuffers(n);
    }
}
