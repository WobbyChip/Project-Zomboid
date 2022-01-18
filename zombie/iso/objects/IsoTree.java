// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.core.opengl.RenderThread;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import java.util.function.Consumer;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.iso.IsoUtils;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.Core;
import zombie.IndieGL;
import zombie.iso.IsoCamera;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoMovingObject;
import zombie.inventory.types.HandWeapon;
import zombie.audio.BaseSoundEmitter;
import zombie.WorldSoundManager;
import fmod.fmod.FMODManager;
import zombie.iso.IsoWorld;
import zombie.vehicles.BaseVehicle;
import zombie.Lua.LuaEventManager;
import zombie.GameTime;
import zombie.iso.LosUtil;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoCell;
import zombie.iso.CellLoader;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoObject;

public class IsoTree extends IsoObject
{
    public static final int MAX_SIZE = 6;
    public int LogYield;
    public int damage;
    public int size;
    public boolean bRenderFlag;
    public float fadeAlpha;
    private static final IsoGameCharacter.Location[] s_chopTreeLocation;
    private static final ArrayList<IsoGridSquare> s_chopTreeIndicators;
    private static IsoTree s_chopTreeHighlighted;
    
    public static IsoTree getNew() {
        synchronized (CellLoader.isoTreeCache) {
            if (CellLoader.isoTreeCache.isEmpty()) {
                return new IsoTree();
            }
            final IsoTree isoTree = CellLoader.isoTreeCache.pop();
            isoTree.sx = 0.0f;
            return isoTree;
        }
    }
    
    public IsoTree() {
        this.LogYield = 1;
        this.damage = 500;
        this.size = 4;
    }
    
    public IsoTree(final IsoCell isoCell) {
        super(isoCell);
        this.LogYield = 1;
        this.damage = 500;
        this.size = 4;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)this.LogYield);
        byteBuffer.put((byte)(this.damage / 10));
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.LogYield = byteBuffer.get();
        this.damage = byteBuffer.get() * 10;
        if (this.sprite != null && this.sprite.getProperties().Val("tree") != null) {
            this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
            if (this.size < 1) {
                this.size = 1;
            }
            if (this.size > 6) {
                this.size = 6;
            }
        }
    }
    
    @Override
    protected void checkMoveWithWind() {
        this.checkMoveWithWind(true);
    }
    
    @Override
    public void reset() {
        super.reset();
    }
    
    public IsoTree(final IsoGridSquare isoGridSquare, final String s) {
        super(isoGridSquare, s, false);
        this.LogYield = 1;
        this.damage = 500;
        this.size = 4;
        this.initTree();
    }
    
    public IsoTree(final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoGridSquare.getCell(), isoGridSquare, isoSprite);
        this.LogYield = 1;
        this.damage = 500;
        this.size = 4;
        this.initTree();
    }
    
    public void initTree() {
        this.setType(IsoObjectType.tree);
        if (this.sprite.getProperties().Val("tree") != null) {
            this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
            if (this.size < 1) {
                this.size = 1;
            }
            if (this.size > 6) {
                this.size = 6;
            }
        }
        else {
            this.size = 4;
        }
        switch (this.size) {
            case 1:
            case 2: {
                this.LogYield = 1;
                break;
            }
            case 3:
            case 4: {
                this.LogYield = 2;
                break;
            }
            case 5: {
                this.LogYield = 3;
                break;
            }
            case 6: {
                this.LogYield = 4;
                break;
            }
        }
        this.damage = this.LogYield * 80;
    }
    
    @Override
    public String getObjectName() {
        return "Tree";
    }
    
    @Override
    public void Damage(final float n) {
        this.damage -= (int)(n * 0.05f);
        if (this.damage <= 0) {
            this.square.transmitRemoveItemFromSquare(this);
            this.square.RecalcAllWithNeighbours(true);
            for (int logYield = this.LogYield, i = 0; i < logYield; ++i) {
                this.square.AddWorldInventoryItem("Base.Log", 0.0f, 0.0f, 0.0f);
                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0f, 0.0f, 0.0f);
                }
                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.Twigs", 0.0f, 0.0f, 0.0f);
                }
            }
            this.reset();
            CellLoader.isoTreeCache.add(this);
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                LosUtil.cachecleared[j] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
    
    @Override
    public void HitByVehicle(final BaseVehicle baseVehicle, final float n) {
        final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter(this.square.x + 0.5f, this.square.y + 0.5f, (float)this.square.z);
        freeEmitter.setParameterValue(freeEmitter.playSound("VehicleHitTree"), FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
        WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
        this.Damage((float)this.damage);
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        int n = handWeapon.getConditionLowerChance() * 2 + isoGameCharacter.getMaintenanceMod();
        if (!handWeapon.getCategories().contains("Axe")) {
            n = handWeapon.getConditionLowerChance() / 2 + isoGameCharacter.getMaintenanceMod();
        }
        if (Rand.NextBool(n)) {
            handWeapon.setCondition(handWeapon.getCondition() - 1);
        }
        isoGameCharacter.getEmitter().playSound("ChopTree");
        WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
        this.setRenderEffect(RenderEffectType.Hit_Tree_Shudder, true);
        float n2 = (float)handWeapon.getTreeDamage();
        if (isoGameCharacter.Traits.Axeman.isSet() && handWeapon.getCategories().contains("Axe")) {
            n2 *= 1.5f;
        }
        this.damage -= (int)n2;
        if (this.damage <= 0) {
            this.square.transmitRemoveItemFromSquare(this);
            isoGameCharacter.getEmitter().playSound("FallingTree");
            this.square.RecalcAllWithNeighbours(true);
            for (int logYield = this.LogYield, i = 0; i < logYield; ++i) {
                this.square.AddWorldInventoryItem("Base.Log", 0.0f, 0.0f, 0.0f);
                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0f, 0.0f, 0.0f);
                }
                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.Twigs", 0.0f, 0.0f, 0.0f);
                }
            }
            this.reset();
            CellLoader.isoTreeCache.add(this);
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                LosUtil.cachecleared[j] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
        LuaEventManager.triggerEvent("OnWeaponHitTree", isoGameCharacter, handWeapon);
    }
    
    public void setHealth(final int a) {
        this.damage = Math.max(a, 0);
    }
    
    public int getHealth() {
        return this.damage;
    }
    
    public int getMaxHealth() {
        return this.LogYield * 80;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public float getSlowFactor(final IsoMovingObject isoMovingObject) {
        float n = 1.0f;
        if (isoMovingObject instanceof IsoGameCharacter) {
            if ("parkranger".equals(((IsoGameCharacter)isoMovingObject).getDescriptor().getProfession())) {
                n = 1.5f;
            }
            if ("lumberjack".equals(((IsoGameCharacter)isoMovingObject).getDescriptor().getProfession())) {
                n = 1.2f;
            }
        }
        if (this.size == 1 || this.size == 2) {
            return 0.8f * n;
        }
        if (this.size == 3 || this.size == 4) {
            return 0.5f * n;
        }
        return 0.3f * n;
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.isHighlighted()) {
            if (this.square != null) {
                IsoTree.s_chopTreeHighlighted = this;
            }
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (this.bRenderFlag || this.fadeAlpha < this.getTargetAlpha(playerIndex)) {
            IndieGL.enableStencilTest();
            IndieGL.glStencilFunc(517, 128, 128);
            this.renderInner(n, n2, n3, colorInfo, b, false);
            final float n4 = 0.044999998f * (GameTime.getInstance().getMultiplier() / 1.6f);
            if (this.bRenderFlag && this.fadeAlpha > 0.25f) {
                this.fadeAlpha -= n4;
                if (this.fadeAlpha < 0.25f) {
                    this.fadeAlpha = 0.25f;
                }
            }
            if (!this.bRenderFlag) {
                final float targetAlpha = this.getTargetAlpha(playerIndex);
                if (this.fadeAlpha < targetAlpha) {
                    this.fadeAlpha += n4;
                    if (this.fadeAlpha > targetAlpha) {
                        this.fadeAlpha = targetAlpha;
                    }
                }
            }
            final float alpha = this.getAlpha(playerIndex);
            final float targetAlpha2 = this.getTargetAlpha(playerIndex);
            this.setAlphaAndTarget(playerIndex, this.fadeAlpha);
            IndieGL.glStencilFunc(514, 128, 128);
            this.renderInner(n, n2, n3, colorInfo, true, false);
            this.setAlpha(playerIndex, alpha);
            this.setTargetAlpha(playerIndex, targetAlpha2);
            if (TreeShader.instance.StartShader()) {
                TreeShader.instance.setOutlineColor(0.1f, 0.1f, 0.1f, 1.0f - this.fadeAlpha);
                this.renderInner(n, n2, n3, colorInfo, true, true);
                IndieGL.EndShader();
            }
            IndieGL.glStencilFunc(519, 255, 255);
        }
        else {
            this.renderInner(n, n2, n3, colorInfo, b, false);
        }
        this.checkChopTreeIndicator(n, n2, n3);
    }
    
    private void renderInner(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2) {
        if (this.sprite != null && this.sprite.name != null && this.sprite.name.contains("JUMBO")) {
            final float offsetX = this.offsetX;
            final float offsetY = this.offsetY;
            this.offsetX = (float)(384 * Core.TileScale / 2 - 96 * Core.TileScale);
            this.offsetY = (float)(256 * Core.TileScale - 32 * Core.TileScale);
            if (this.offsetX != offsetX || this.offsetY != offsetY) {
                this.sx = 0.0f;
            }
        }
        else {
            final float offsetX2 = this.offsetX;
            final float offsetY2 = this.offsetY;
            this.offsetX = (float)(32 * Core.TileScale);
            this.offsetY = (float)(96 * Core.TileScale);
            if (this.offsetX != offsetX2 || this.offsetY != offsetY2) {
                this.sx = 0.0f;
            }
        }
        if (b2 && this.sprite != null) {
            final Texture textureForCurrentFrame = this.sprite.getTextureForCurrentFrame(this.dir);
            if (textureForCurrentFrame != null) {
                TreeShader.instance.setStepSize(0.25f, textureForCurrentFrame.getWidth(), textureForCurrentFrame.getHeight());
            }
        }
        super.render(n, n2, n3, colorInfo, false, false, null);
        if (this.AttachedAnimSprite != null) {
            for (int size = this.AttachedAnimSprite.size(), i = 0; i < size; ++i) {
                final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
                final int playerIndex = IsoCamera.frameState.playerIndex;
                final float targetAlpha = this.getTargetAlpha(playerIndex);
                this.setTargetAlpha(playerIndex, 1.0f);
                isoSpriteInstance.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY, this.isHighlighted() ? this.getHighlightColor() : colorInfo);
                this.setTargetAlpha(playerIndex, targetAlpha);
                isoSpriteInstance.update();
            }
        }
    }
    
    @Override
    public void setSprite(final IsoSprite sprite) {
        super.setSprite(sprite);
        this.initTree();
    }
    
    @Override
    public boolean isMaskClicked(final int n, final int n2, final boolean b) {
        if (super.isMaskClicked(n, n2, b)) {
            return true;
        }
        if (this.AttachedAnimSprite == null) {
            return false;
        }
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            if (this.AttachedAnimSprite.get(i).parentSprite.isMaskClicked(this.dir, n, n2, b)) {
                return true;
            }
        }
        return false;
    }
    
    public static void setChopTreeCursorLocation(final int n, final int x, final int y, final int z) {
        if (IsoTree.s_chopTreeLocation[n] == null) {
            IsoTree.s_chopTreeLocation[n] = new IsoGameCharacter.Location(-1, -1, -1);
        }
        final IsoGameCharacter.Location location = IsoTree.s_chopTreeLocation[n];
        location.x = x;
        location.y = y;
        location.z = z;
    }
    
    private void checkChopTreeIndicator(final float n, final float n2, final float n3) {
        if (this.isHighlighted()) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoGameCharacter.Location location = IsoTree.s_chopTreeLocation[playerIndex];
        if (location == null || location.x == -1 || this.square == null) {
            return;
        }
        if (this.getCell().getDrag(playerIndex) == null) {
            location.x = -1;
            return;
        }
        if (IsoUtils.DistanceToSquared(this.square.x + 0.5f, this.square.y + 0.5f, location.x + 0.5f, location.y + 0.5f) < 12.25f) {
            IsoTree.s_chopTreeIndicators.add(this.square);
        }
    }
    
    public static void renderChopTreeIndicators() {
        if (!IsoTree.s_chopTreeIndicators.isEmpty()) {
            PZArrayUtil.forEach(IsoTree.s_chopTreeIndicators, IsoTree::renderChopTreeIndicator);
            IsoTree.s_chopTreeIndicators.clear();
        }
        if (IsoTree.s_chopTreeHighlighted != null) {
            final IsoTree s_chopTreeHighlighted = IsoTree.s_chopTreeHighlighted;
            IsoTree.s_chopTreeHighlighted = null;
            s_chopTreeHighlighted.renderInner((float)s_chopTreeHighlighted.square.x, (float)s_chopTreeHighlighted.square.y, (float)s_chopTreeHighlighted.square.z, s_chopTreeHighlighted.getHighlightColor(), false, false);
        }
    }
    
    private static void renderChopTreeIndicator(final IsoGridSquare isoGridSquare) {
        final Texture sharedTexture = Texture.getSharedTexture("media/ui/chop_tree.png");
        if (sharedTexture == null || !sharedTexture.isReady()) {
            return;
        }
        final float n = (float)isoGridSquare.x;
        final float n2 = (float)isoGridSquare.y;
        final float n3 = (float)isoGridSquare.z;
        SpriteRenderer.instance.render(sharedTexture, IsoUtils.XToScreen(n, n2, n3, 0) + IsoSprite.globalOffsetX - 32 * Core.TileScale, IsoUtils.YToScreen(n, n2, n3, 0) + IsoSprite.globalOffsetY - 96 * Core.TileScale, (float)(64 * Core.TileScale), (float)(128 * Core.TileScale), 0.0f, 0.5f, 0.0f, 0.75f, null);
    }
    
    static {
        s_chopTreeLocation = new IsoGameCharacter.Location[4];
        s_chopTreeIndicators = new ArrayList<IsoGridSquare>();
        IsoTree.s_chopTreeHighlighted = null;
    }
    
    public static class TreeShader
    {
        public static final TreeShader instance;
        private ShaderProgram shaderProgram;
        private int stepSize;
        private int outlineColor;
        
        public void initShader() {
            this.shaderProgram = ShaderProgram.createShaderProgram("tree", false, true);
            if (this.shaderProgram.isCompiled()) {
                this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"stepSize");
                this.outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"outlineColor");
                ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
                ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001f, 0.001f);
                ARBShaderObjects.glUseProgramObjectARB(0);
            }
        }
        
        public void setOutlineColor(final float n, final float n2, final float n3, final float n4) {
            SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.outlineColor, n, n2, n3, n4);
        }
        
        public void setStepSize(final float n, final int n2, final int n3) {
            SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.stepSize, n / n2, n / n3);
        }
        
        public boolean StartShader() {
            if (this.shaderProgram == null) {
                RenderThread.invokeOnRenderContext(this::initShader);
            }
            if (this.shaderProgram.isCompiled()) {
                IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
                return true;
            }
            return false;
        }
        
        static {
            instance = new TreeShader();
        }
    }
}
