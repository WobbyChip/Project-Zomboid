// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.sprite;

public final class SpriteRendererStates
{
    private SpriteRenderState m_populating;
    private SpriteRenderState m_ready;
    private SpriteRenderState m_rendering;
    private SpriteRenderState m_rendered;
    
    public SpriteRendererStates() {
        this.m_populating = new SpriteRenderState(0);
        this.m_ready = null;
        this.m_rendering = new SpriteRenderState(2);
        this.m_rendered = new SpriteRenderState(1);
    }
    
    public SpriteRenderState getPopulating() {
        return this.m_populating;
    }
    
    public GenericSpriteRenderState getPopulatingActiveState() {
        return this.m_populating.getActiveState();
    }
    
    public void setPopulating(final SpriteRenderState populating) {
        this.m_populating = populating;
    }
    
    public SpriteRenderState getReady() {
        return this.m_ready;
    }
    
    public void setReady(final SpriteRenderState ready) {
        this.m_ready = ready;
    }
    
    public SpriteRenderState getRendering() {
        return this.m_rendering;
    }
    
    public GenericSpriteRenderState getRenderingActiveState() {
        return this.m_rendering.getActiveState();
    }
    
    public void setRendering(final SpriteRenderState rendering) {
        this.m_rendering = rendering;
    }
    
    public SpriteRenderState getRendered() {
        return this.m_rendered;
    }
    
    public void setRendered(final SpriteRenderState rendered) {
        this.m_rendered = rendered;
    }
    
    public void movePopulatingToReady() {
        this.m_ready = this.m_populating;
        this.m_populating = this.m_rendered;
        this.m_rendered = null;
        this.m_ready.time = System.nanoTime();
        this.m_ready.onReady();
    }
    
    public void moveReadyToRendering() {
        this.m_rendered = this.m_rendering;
        this.m_rendering = this.m_ready;
        this.m_ready = null;
        this.m_rendering.onRenderAcquired();
    }
}
