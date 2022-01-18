// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

public interface IGLFramebufferObject
{
    int GL_FRAMEBUFFER();
    
    int GL_RENDERBUFFER();
    
    int GL_COLOR_ATTACHMENT0();
    
    int GL_DEPTH_ATTACHMENT();
    
    int GL_STENCIL_ATTACHMENT();
    
    int GL_DEPTH_STENCIL();
    
    int GL_DEPTH24_STENCIL8();
    
    int GL_FRAMEBUFFER_COMPLETE();
    
    int GL_FRAMEBUFFER_UNDEFINED();
    
    int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT();
    
    int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT();
    
    int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS();
    
    int GL_FRAMEBUFFER_INCOMPLETE_FORMATS();
    
    int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER();
    
    int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER();
    
    int GL_FRAMEBUFFER_UNSUPPORTED();
    
    int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE();
    
    int glGenFramebuffers();
    
    void glBindFramebuffer(final int p0, final int p1);
    
    void glFramebufferTexture2D(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int glGenRenderbuffers();
    
    void glBindRenderbuffer(final int p0, final int p1);
    
    void glRenderbufferStorage(final int p0, final int p1, final int p2, final int p3);
    
    void glFramebufferRenderbuffer(final int p0, final int p1, final int p2, final int p3);
    
    int glCheckFramebufferStatus(final int p0);
    
    void glDeleteFramebuffers(final int p0);
    
    void glDeleteRenderbuffers(final int p0);
}
