// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.Core;
import zombie.util.StringUtils;
import zombie.characters.SurvivorDesc;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.IsoDirections;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.population.IClothingItemListener;

public final class UI3DModel extends UIElement implements IClothingItemListener
{
    private final AnimatedModel animatedModel;
    private IsoDirections dir;
    private boolean bDoExt;
    private long nextExt;
    private final Drawer[] drawers;
    private float zoom;
    private float yOffset;
    private float xOffset;
    
    public UI3DModel(final KahluaTable kahluaTable) {
        super(kahluaTable);
        this.animatedModel = new AnimatedModel();
        this.dir = IsoDirections.E;
        this.bDoExt = false;
        this.nextExt = -1L;
        this.drawers = new Drawer[3];
        this.zoom = 0.0f;
        this.yOffset = 0.0f;
        this.xOffset = 0.0f;
        for (int i = 0; i < this.drawers.length; ++i) {
            this.drawers[i] = new Drawer();
        }
        if (OutfitManager.instance != null) {
            OutfitManager.instance.addClothingItemListener(this);
        }
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        super.render();
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return;
        }
        if (this.bDoExt) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.nextExt < 0L) {
                this.nextExt = currentTimeMillis + Rand.Next(5000, 10000);
            }
            if (this.nextExt < currentTimeMillis) {
                this.animatedModel.getActionContext().reportEvent("EventDoExt");
                this.animatedModel.setVariable("Ext", (float)(Rand.Next(0, 6) + 1));
                this.nextExt = -1L;
            }
        }
        this.animatedModel.update();
        final Drawer drawer = this.drawers[SpriteRenderer.instance.getMainStateIndex()];
        drawer.init(this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue());
        SpriteRenderer.instance.drawGeneric(drawer);
    }
    
    public void setDirection(final IsoDirections dir) {
        this.dir = dir;
        this.animatedModel.setAngle(dir.ToVector());
    }
    
    public IsoDirections getDirection() {
        return this.dir;
    }
    
    public void setAnimate(final boolean animate) {
        this.animatedModel.setAnimate(animate);
    }
    
    public void setAnimSetName(final String animSetName) {
        this.animatedModel.setAnimSetName(animSetName);
    }
    
    public void setDoRandomExtAnimations(final boolean bDoExt) {
        this.bDoExt = bDoExt;
    }
    
    public void setIsometric(final boolean isometric) {
        this.animatedModel.setIsometric(isometric);
    }
    
    public void setOutfitName(final String s, final boolean b, final boolean b2) {
        this.animatedModel.setOutfitName(s, b, b2);
    }
    
    public void setCharacter(final IsoGameCharacter character) {
        this.animatedModel.setCharacter(character);
    }
    
    public void setSurvivorDesc(final SurvivorDesc survivorDesc) {
        this.animatedModel.setSurvivorDesc(survivorDesc);
    }
    
    public void setState(final String state) {
        this.animatedModel.setState(state);
    }
    
    public void reportEvent(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return;
        }
        this.animatedModel.getActionContext().reportEvent(s);
    }
    
    @Override
    public void clothingItemChanged(final String s) {
        this.animatedModel.clothingItemChanged(s);
    }
    
    public void setZoom(final float zoom) {
        this.zoom = zoom;
    }
    
    public void setYOffset(final float yOffset) {
        this.yOffset = yOffset;
    }
    
    public void setXOffset(final float xOffset) {
        this.xOffset = xOffset;
    }
    
    private final class Drawer extends TextureDraw.GenericDrawer
    {
        int absX;
        int absY;
        float m_animPlayerAngle;
        boolean bRendered;
        
        public void init(final int absX, final int absY) {
            this.absX = absX;
            this.absY = absY;
            this.m_animPlayerAngle = UI3DModel.this.animatedModel.getAnimationPlayer().getRenderedAngle();
            this.bRendered = false;
            float yOffset = UI3DModel.this.animatedModel.isIsometric() ? -0.45f : -0.5f;
            if (UI3DModel.this.yOffset != 0.0f) {
                yOffset = UI3DModel.this.yOffset;
            }
            UI3DModel.this.animatedModel.setOffset(UI3DModel.this.xOffset, yOffset, 0.0f);
            UI3DModel.this.animatedModel.renderMain();
        }
        
        @Override
        public void render() {
            float n = UI3DModel.this.animatedModel.isIsometric() ? 22.0f : 25.0f;
            if (UI3DModel.this.zoom > 0.0f) {
                n -= UI3DModel.this.zoom;
            }
            UI3DModel.this.animatedModel.DoRender(this.absX, Core.height - this.absY - (int)UI3DModel.this.height, (int)UI3DModel.this.width, (int)UI3DModel.this.height, n, this.m_animPlayerAngle);
            this.bRendered = true;
        }
        
        @Override
        public void postRender() {
            UI3DModel.this.animatedModel.postRender(this.bRendered);
        }
    }
}
