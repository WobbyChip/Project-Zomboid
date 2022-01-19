// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.skinnedmodel.ModelManager;
import zombie.characterTextures.ItemSmartTexture;
import org.lwjgl.opengl.GL11;
import zombie.core.textures.SmartTexture;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.InventoryItem;
import zombie.popman.ObjectPool;
import zombie.core.textures.Texture;
import java.util.ArrayList;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.TextureDraw;

public final class EquippedTextureCreator extends TextureDraw.GenericDrawer
{
    private boolean bRendered;
    private ModelInstance modelInstance;
    private float bloodLevel;
    private final ArrayList<Texture> texturesNotReady;
    private static final ObjectPool<EquippedTextureCreator> pool;
    
    public EquippedTextureCreator() {
        this.texturesNotReady = new ArrayList<Texture>();
    }
    
    public void init(final ModelInstance modelInstance, final InventoryItem inventoryItem) {
        float bloodLevel = 0.0f;
        if (inventoryItem instanceof HandWeapon) {
            bloodLevel = ((HandWeapon)inventoryItem).getBloodLevel();
        }
        this.init(modelInstance, bloodLevel);
    }
    
    public void init(final ModelInstance modelInstance, final float bloodLevel) {
        this.bRendered = false;
        this.texturesNotReady.clear();
        this.modelInstance = modelInstance;
        this.bloodLevel = bloodLevel;
        if (this.modelInstance != null) {
            final ModelInstance modelInstance2 = this.modelInstance;
            ++modelInstance2.renderRefCount;
            Texture tex = this.modelInstance.tex;
            if (tex instanceof SmartTexture) {
                tex = null;
            }
            if (tex != null && !tex.isReady()) {
                this.texturesNotReady.add(tex);
            }
            final Texture sharedTexture = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlayWeapon.png");
            if (sharedTexture != null && !sharedTexture.isReady()) {
                this.texturesNotReady.add(sharedTexture);
            }
            final Texture sharedTexture2 = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlayWeaponMask.png");
            if (sharedTexture2 != null && !sharedTexture2.isReady()) {
                this.texturesNotReady.add(sharedTexture2);
            }
        }
    }
    
    @Override
    public void render() {
        for (int i = 0; i < this.texturesNotReady.size(); ++i) {
            if (!this.texturesNotReady.get(i).isReady()) {
                return;
            }
        }
        GL11.glPushAttrib(2048);
        try {
            this.updateTexture(this.modelInstance, this.bloodLevel);
        }
        finally {
            GL11.glPopAttrib();
        }
        this.bRendered = true;
    }
    
    private void updateTexture(final ModelInstance modelInstance, final float n) {
        if (modelInstance == null) {
            return;
        }
        ItemSmartTexture tex = null;
        if (n > 0.0f) {
            if (modelInstance.tex instanceof ItemSmartTexture) {
                tex = (ItemSmartTexture)modelInstance.tex;
            }
            else if (modelInstance.tex != null) {
                tex = new ItemSmartTexture(modelInstance.tex.getName());
            }
        }
        else if (modelInstance.tex instanceof ItemSmartTexture) {
            tex = (ItemSmartTexture)modelInstance.tex;
        }
        if (tex == null) {
            return;
        }
        tex.setBlood("media/textures/BloodTextures/BloodOverlayWeapon.png", "media/textures/BloodTextures/BloodOverlayWeaponMask.png", n, 300);
        tex.calculate();
        modelInstance.tex = tex;
    }
    
    @Override
    public void postRender() {
        ModelManager.instance.derefModelInstance(this.modelInstance);
        this.texturesNotReady.clear();
        if (!this.bRendered) {}
        EquippedTextureCreator.pool.release(this);
    }
    
    public boolean isRendered() {
        return this.bRendered;
    }
    
    public static EquippedTextureCreator alloc() {
        return EquippedTextureCreator.pool.alloc();
    }
    
    static {
        pool = new ObjectPool<EquippedTextureCreator>(EquippedTextureCreator::new);
    }
}
