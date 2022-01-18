// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.inventory.types.InventoryContainer;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.opengl.Shader;
import zombie.iso.IsoUtils;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.ui.ObjectTooltip;
import java.io.IOException;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.network.ServerGUI;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItemFactory;
import zombie.util.Type;
import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.inventory.types.DrainableComboItem;
import zombie.core.logger.ExceptionLogger;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.network.GameServer;
import zombie.Lua.LuaEventManager;
import zombie.inventory.ItemSoundManager;
import zombie.iso.IsoWorld;
import zombie.iso.IsoCell;
import zombie.GameTime;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.inventory.ItemContainer;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;

public class IsoWorldInventoryObject extends IsoObject
{
    public InventoryItem item;
    public float xoff;
    public float yoff;
    public float zoff;
    public boolean removeProcess;
    public double dropTime;
    public boolean ignoreRemoveSandbox;
    
    public IsoWorldInventoryObject(final InventoryItem item, final IsoGridSquare square, final float xoff, final float yoff, final float zoff) {
        this.removeProcess = false;
        this.dropTime = -1.0;
        this.ignoreRemoveSandbox = false;
        this.OutlineOnMouseover = true;
        if (item.worldZRotation <= 0) {
            item.worldZRotation = Rand.Next(0, 360);
        }
        item.setContainer(null);
        this.xoff = xoff;
        this.yoff = yoff;
        this.zoff = zoff;
        if (this.xoff == 0.0f) {
            this.xoff = Rand.Next(1000) / 1000.0f;
        }
        if (this.yoff == 0.0f) {
            this.yoff = Rand.Next(1000) / 1000.0f;
        }
        this.item = item;
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        this.updateSprite();
        this.square = square;
        this.offsetY = 0.0f;
        this.offsetX = 0.0f;
        this.dropTime = GameTime.getInstance().getWorldAgeHours();
    }
    
    public IsoWorldInventoryObject(final IsoCell isoCell) {
        super(isoCell);
        this.removeProcess = false;
        this.dropTime = -1.0;
        this.ignoreRemoveSandbox = false;
        this.offsetY = 0.0f;
        this.offsetX = 0.0f;
    }
    
    public void swapItem(final InventoryItem item) {
        if (item == null) {
            return;
        }
        if (this.getItem() != null) {
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.getItem());
            ItemSoundManager.removeItem(this.getItem());
            this.getItem().setWorldItem(null);
            item.setID(this.getItem().getID());
            item.worldScale = this.getItem().worldScale;
            item.worldZRotation = this.getItem().worldZRotation;
        }
        this.item = item;
        if (item.getWorldItem() != null) {
            throw new IllegalArgumentException("newItem.getWorldItem() != null");
        }
        this.getItem().setWorldItem(this);
        this.setKeyId(this.getItem().getKeyId());
        this.setName(this.getItem().getName());
        if (this.getItem().shouldUpdateInWorld()) {
            IsoWorld.instance.CurrentCell.addToProcessWorldItems(this);
        }
        IsoWorld.instance.CurrentCell.addToProcessItems(item);
        this.updateSprite();
        LuaEventManager.triggerEvent("OnContainerUpdate");
        if (GameServer.bServer) {
            this.sendObjectChange("swapItem");
        }
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("swapItem".equals(anObject)) {
            if (this.getItem() == null) {
                return;
            }
            try {
                this.getItem().saveWithSize(byteBuffer, false);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        else {
            super.saveChange(anObject, kahluaTable, byteBuffer);
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("swapItem".equals(anObject)) {
            try {
                final InventoryItem loadItem = InventoryItem.loadItem(byteBuffer, 186);
                if (loadItem != null) {
                    this.swapItem(loadItem);
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        else {
            super.loadChange(anObject, byteBuffer);
        }
    }
    
    private boolean isWaterSource() {
        if (this.item == null) {
            return false;
        }
        if (this.item.isBroken()) {}
        if (!this.item.canStoreWater()) {
            return false;
        }
        if (this.item.isWaterSource() && this.item instanceof DrainableComboItem) {
            return ((DrainableComboItem)this.item).getRainFactor() > 0.0f;
        }
        if (this.item.getReplaceOnUseOn() != null && this.item.getReplaceOnUseOnString() != null) {
            final Item item = ScriptManager.instance.getItem(this.item.getReplaceOnUseOnString());
            if (item != null && item.getType() == Item.Type.Drainable) {
                return item.getCanStoreWater() && item.getRainFactor() > 0.0f;
            }
        }
        return false;
    }
    
    @Override
    public int getWaterAmount() {
        if (!this.isWaterSource()) {
            return 0;
        }
        if (this.item instanceof DrainableComboItem) {
            return ((DrainableComboItem)this.item).getRemainingUses();
        }
        return 0;
    }
    
    @Override
    public void setWaterAmount(final int n) {
        if (this.isWaterSource()) {
            final DrainableComboItem drainableComboItem = Type.tryCastTo(this.item, DrainableComboItem.class);
            if (drainableComboItem != null) {
                drainableComboItem.setUsedDelta(n * drainableComboItem.getUseDelta());
                if (n == 0 && drainableComboItem.getReplaceOnDeplete() != null) {
                    final InventoryItem createItem = InventoryItemFactory.CreateItem(drainableComboItem.getReplaceOnDepleteFullType());
                    if (createItem != null) {
                        createItem.setCondition(this.getItem().getCondition());
                        createItem.setFavorite(this.getItem().isFavorite());
                        this.swapItem(createItem);
                    }
                }
            }
            else if (n > 0) {
                final InventoryItem createItem2 = InventoryItemFactory.CreateItem(this.getItem().getReplaceOnUseOnString());
                if (createItem2 != null) {
                    createItem2.setCondition(this.getItem().getCondition());
                    createItem2.setFavorite(this.getItem().isFavorite());
                    createItem2.setTaintedWater(this.getItem().isTaintedWater());
                    final DrainableComboItem drainableComboItem2 = Type.tryCastTo(createItem2, DrainableComboItem.class);
                    if (drainableComboItem2 != null) {
                        drainableComboItem2.setUsedDelta(n * drainableComboItem2.getUseDelta());
                    }
                    this.swapItem(createItem2);
                }
            }
        }
    }
    
    @Override
    public int getWaterMax() {
        if (!this.isWaterSource()) {
            return 0;
        }
        float n;
        if (this.item instanceof DrainableComboItem) {
            n = 1.0f / ((DrainableComboItem)this.item).getUseDelta();
        }
        else {
            n = 1.0f / ScriptManager.instance.getItem(this.item.getReplaceOnUseOnString()).getUseDelta();
        }
        if (n - (int)n > 0.99f) {
            return (int)n + 1;
        }
        return (int)n;
    }
    
    @Override
    public boolean isTaintedWater() {
        return this.isWaterSource() && this.getItem().isTaintedWater();
    }
    
    @Override
    public void setTaintedWater(final boolean taintedWater) {
        if (this.isWaterSource()) {
            this.getItem().setTaintedWater(taintedWater);
        }
    }
    
    @Override
    public void update() {
        final IsoCell cell = IsoWorld.instance.getCell();
        if (!this.removeProcess && this.item != null && this.item.shouldUpdateInWorld()) {
            cell.addToProcessItems(this.item);
        }
    }
    
    public void updateSprite() {
        this.sprite.setTintMod(new ColorInfo(this.item.col.r, this.item.col.g, this.item.col.b, this.item.col.a));
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return;
        }
        String s = this.item.getTex().getName();
        if (this.item.isUseWorldItem()) {
            s = this.item.getWorldTexture();
        }
        try {
            if (Texture.getSharedTexture(s) == null) {
                s = this.item.getTex().getName();
            }
        }
        catch (Exception ex) {
            s = "media/inventory/world/WItem_Sack.png";
        }
        final Texture loadFrameExplicit = this.sprite.LoadFrameExplicit(s);
        if (this.item.getScriptItem() == null) {
            this.sprite.def.scaleAspect((float)loadFrameExplicit.getWidthOrig(), (float)loadFrameExplicit.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
        }
        else {
            final float n = this.item.getScriptItem().ScaleWorldIcon * (Core.TileScale / 2.0f);
            this.sprite.def.setScale(n, n);
        }
    }
    
    public boolean finishupdate() {
        return this.removeProcess || this.item == null || !this.item.shouldUpdateInWorld();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        this.xoff = byteBuffer.getFloat();
        this.yoff = byteBuffer.getFloat();
        this.zoff = byteBuffer.getFloat();
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        this.item = InventoryItem.loadItem(byteBuffer, n);
        if (this.item == null) {
            byteBuffer.getDouble();
            return;
        }
        this.item.setWorldItem(this);
        this.sprite.getTintMod().r = this.item.getR();
        this.sprite.getTintMod().g = this.item.getG();
        this.sprite.getTintMod().b = this.item.getB();
        if (n >= 108) {
            this.dropTime = byteBuffer.getDouble();
        }
        else {
            this.dropTime = GameTime.getInstance().getWorldAgeHours();
        }
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return;
        }
        String s = this.item.getTex().getName();
        if (this.item.isUseWorldItem()) {
            s = this.item.getWorldTexture();
        }
        try {
            if (Texture.getSharedTexture(s) == null) {
                s = this.item.getTex().getName();
            }
        }
        catch (Exception ex) {
            s = "media/inventory/world/WItem_Sack.png";
        }
        final Texture loadFrameExplicit = this.sprite.LoadFrameExplicit(s);
        if (loadFrameExplicit == null) {
            return;
        }
        if (n < 33) {
            final float n2 = float1 - loadFrameExplicit.getWidthOrig() / 2;
            final float n3 = float2 - loadFrameExplicit.getHeightOrig();
        }
        if (this.item.getScriptItem() == null) {
            this.sprite.def.scaleAspect((float)loadFrameExplicit.getWidthOrig(), (float)loadFrameExplicit.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
        }
        else {
            final float n4 = this.item.getScriptItem().ScaleWorldIcon * (Core.TileScale / 2.0f);
            this.sprite.def.setScale(n4, n4);
        }
    }
    
    @Override
    public boolean Serialize() {
        return true;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
        if (!this.Serialize()) {
            return;
        }
        byteBuffer.put(IsoObject.factoryGetClassID(this.getObjectName()));
        byteBuffer.putFloat(this.xoff);
        byteBuffer.putFloat(this.yoff);
        byteBuffer.putFloat(this.zoff);
        byteBuffer.putFloat(this.offsetX);
        byteBuffer.putFloat(this.offsetY);
        this.item.saveWithSize(byteBuffer, false);
        byteBuffer.putDouble(this.dropTime);
    }
    
    @Override
    public void softReset() {
        this.square.removeWorldObject(this);
    }
    
    @Override
    public String getObjectName() {
        return "WorldInventoryItem";
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip) {
        this.item.DoTooltip(objectTooltip);
    }
    
    @Override
    public boolean HasTooltip() {
        return false;
    }
    
    @Override
    public boolean onMouseLeftClick(final int n, final int n2) {
        return false;
    }
    
    private void debugDrawLocation(float n, float n2, float n3) {
        if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
            n += this.xoff;
            n2 += this.yoff;
            n3 += this.zoff;
            LineDrawer.DrawIsoLine(n - 0.25f, n2, n3, n + 0.25f, n2, n3, 1.0f, 1.0f, 1.0f, 0.5f, 1);
            LineDrawer.DrawIsoLine(n, n2 - 0.25f, n3, n, n2 + 0.25f, n3, 1.0f, 1.0f, 1.0f, 0.5f, 1);
        }
    }
    
    private void debugHitTest() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final float zoom = Core.getInstance().getZoom(playerIndex);
        final float n = (float)Mouse.getXA();
        final float n2 = (float)Mouse.getYA();
        final float n3 = n - IsoCamera.getScreenLeft(playerIndex);
        final float n4 = n2 - IsoCamera.getScreenTop(playerIndex);
        final float n5 = n3 * zoom;
        final float n6 = n4 * zoom;
        final float n7 = this.getScreenPosX(playerIndex) * zoom;
        final float n8 = this.getScreenPosY(playerIndex) * zoom;
        final float distanceTo2D = IsoUtils.DistanceTo2D(n7, n8, n5, n6);
        final int n9 = 48;
        if (distanceTo2D < n9) {
            LineDrawer.drawCircle(n7, n8, (float)n9, 16, 1.0f, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (Core.bDebug) {}
        if (!this.getItem().getScriptItem().isWorldRender()) {
            return;
        }
        if (WorldItemModelDrawer.renderMain(this.getItem(), this.getSquare(), this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0.0f)) {
            this.debugDrawLocation(n, n2, n3);
            return;
        }
        if (this.sprite.CurrentAnim == null || this.sprite.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final Texture texture = this.sprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
        if (texture == null) {
            return;
        }
        this.sprite.render(this, n + this.xoff, n2 + this.yoff, n3 + this.zoff, this.dir, this.offsetX + texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0f, this.offsetY + texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0f / 4.0f, colorInfo, true);
        this.debugDrawLocation(n, n2, n3);
    }
    
    @Override
    public void renderObjectPicker(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.sprite == null) {
            return;
        }
        if (this.sprite.CurrentAnim == null || this.sprite.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final Texture texture = this.sprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
        if (texture == null) {
            return;
        }
        final float n4 = (float)(texture.getWidthOrig() / 2);
        final float n5 = (float)texture.getHeightOrig();
        this.sprite.renderObjectPicker(this.sprite.def, this, this.dir);
    }
    
    public InventoryItem getItem() {
        return this.item;
    }
    
    @Override
    public void addToWorld() {
        if (this.item != null && this.item.shouldUpdateInWorld() && !IsoWorld.instance.CurrentCell.getProcessWorldItems().contains(this)) {
            IsoWorld.instance.CurrentCell.getProcessWorldItems().add(this);
        }
        if (this.item instanceof InventoryContainer) {
            final ItemContainer inventory = ((InventoryContainer)this.item).getInventory();
            if (inventory != null) {
                inventory.addItemsToProcessItems();
            }
        }
        super.addToWorld();
    }
    
    @Override
    public void removeFromWorld() {
        this.removeProcess = true;
        IsoWorld.instance.getCell().getProcessWorldItems().remove(this);
        if (this.item != null) {
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.item);
            ItemSoundManager.removeItem(this.item);
        }
        if (this.item instanceof InventoryContainer) {
            final ItemContainer inventory = ((InventoryContainer)this.item).getInventory();
            if (inventory != null) {
                inventory.removeItemsFromProcessItems();
            }
        }
        super.removeFromWorld();
    }
    
    @Override
    public void removeFromSquare() {
        if (this.square != null) {
            this.square.getWorldObjects().remove(this);
            this.square.chunk.recalcHashCodeObjects();
        }
        super.removeFromSquare();
    }
    
    public float getScreenPosX(final int n) {
        return (IsoUtils.XToScreen(this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0) - IsoCamera.cameras[n].getOffX()) / Core.getInstance().getZoom(n);
    }
    
    public float getScreenPosY(final int n) {
        final Texture texture = (this.sprite == null) ? null : this.sprite.getTextureForCurrentFrame(this.dir);
        return (IsoUtils.YToScreen(this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0) - IsoCamera.cameras[n].getOffY() - ((texture == null) ? 0.0f : (texture.getHeightOrig() * this.sprite.def.getScaleY() * 1.0f / 4.0f))) / Core.getInstance().getZoom(n);
    }
    
    public void setIgnoreRemoveSandbox(final boolean ignoreRemoveSandbox) {
        this.ignoreRemoveSandbox = ignoreRemoveSandbox;
    }
    
    public boolean isIgnoreRemoveSandbox() {
        return this.ignoreRemoveSandbox;
    }
    
    public float getWorldPosX() {
        return this.getX() + this.xoff;
    }
    
    public float getWorldPosY() {
        return this.getY() + this.yoff;
    }
    
    public float getWorldPosZ() {
        return this.getZ() + this.zoff;
    }
}
