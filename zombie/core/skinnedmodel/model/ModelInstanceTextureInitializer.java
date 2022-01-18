// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.characters.EquippedTextureCreator;
import zombie.core.SpriteRenderer;
import zombie.util.Type;
import zombie.inventory.types.HandWeapon;
import zombie.popman.ObjectPool;
import zombie.inventory.InventoryItem;

public final class ModelInstanceTextureInitializer
{
    private boolean m_bRendered;
    private ModelInstance m_modelInstance;
    private InventoryItem m_item;
    private float m_bloodLevel;
    private int m_changeNumberMain;
    private int m_changeNumberThread;
    private final RenderData[] m_renderData;
    private static final ObjectPool<ModelInstanceTextureInitializer> pool;
    
    public ModelInstanceTextureInitializer() {
        this.m_changeNumberMain = 0;
        this.m_changeNumberThread = 0;
        this.m_renderData = new RenderData[3];
    }
    
    public void init(final ModelInstance modelInstance, final InventoryItem item) {
        this.m_item = item;
        this.m_modelInstance = modelInstance;
        final HandWeapon handWeapon = Type.tryCastTo(item, HandWeapon.class);
        this.m_bloodLevel = ((handWeapon == null) ? 0.0f : handWeapon.getBloodLevel());
        this.setDirty();
    }
    
    public void init(final ModelInstance modelInstance, final float bloodLevel) {
        this.m_item = null;
        this.m_modelInstance = modelInstance;
        this.m_bloodLevel = bloodLevel;
        this.setDirty();
    }
    
    public void setDirty() {
        ++this.m_changeNumberMain;
        this.m_bRendered = false;
    }
    
    public boolean isDirty() {
        return !this.m_bRendered;
    }
    
    public void renderMain() {
        if (this.m_bRendered) {
            return;
        }
        final int mainStateIndex = SpriteRenderer.instance.getMainStateIndex();
        if (this.m_renderData[mainStateIndex] == null) {
            this.m_renderData[mainStateIndex] = new RenderData();
        }
        final RenderData renderData = this.m_renderData[mainStateIndex];
        if (renderData.m_textureCreator != null) {
            return;
        }
        renderData.m_changeNumber = this.m_changeNumberMain;
        renderData.m_textureCreator = EquippedTextureCreator.alloc();
        if (this.m_item == null) {
            renderData.m_textureCreator.init(this.m_modelInstance, this.m_bloodLevel);
        }
        else {
            renderData.m_textureCreator.init(this.m_modelInstance, this.m_item);
        }
        renderData.m_bRendered = false;
    }
    
    public void render() {
        final RenderData renderData = this.m_renderData[SpriteRenderer.instance.getRenderStateIndex()];
        if (renderData == null) {
            return;
        }
        if (renderData.m_textureCreator == null) {
            return;
        }
        if (renderData.m_bRendered) {
            return;
        }
        if (renderData.m_changeNumber == this.m_changeNumberThread) {
            renderData.m_bRendered = true;
            return;
        }
        renderData.m_textureCreator.render();
        if (renderData.m_textureCreator.isRendered()) {
            this.m_changeNumberThread = renderData.m_changeNumber;
            renderData.m_bRendered = true;
        }
    }
    
    public void postRender() {
        final RenderData renderData = this.m_renderData[SpriteRenderer.instance.getMainStateIndex()];
        if (renderData == null) {
            return;
        }
        if (renderData.m_textureCreator == null) {
            return;
        }
        if (renderData.m_textureCreator.isRendered() && renderData.m_changeNumber == this.m_changeNumberMain) {
            this.m_bRendered = true;
        }
        if (renderData.m_bRendered) {
            renderData.m_textureCreator.postRender();
            renderData.m_textureCreator = null;
        }
    }
    
    public boolean isRendered() {
        final RenderData renderData = this.m_renderData[SpriteRenderer.instance.getRenderStateIndex()];
        return renderData == null || renderData.m_textureCreator == null || renderData.m_bRendered;
    }
    
    public static ModelInstanceTextureInitializer alloc() {
        return ModelInstanceTextureInitializer.pool.alloc();
    }
    
    public void release() {
        ModelInstanceTextureInitializer.pool.release(this);
    }
    
    static {
        pool = new ObjectPool<ModelInstanceTextureInitializer>(ModelInstanceTextureInitializer::new);
    }
    
    private static final class RenderData
    {
        int m_changeNumber;
        boolean m_bRendered;
        EquippedTextureCreator m_textureCreator;
        
        private RenderData() {
            this.m_changeNumber = 0;
        }
    }
}
