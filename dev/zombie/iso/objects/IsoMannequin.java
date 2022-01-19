// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoMetaGrid;
import zombie.characters.WornItems.WornItem;
import zombie.iso.SliceY;
import zombie.inventory.types.Moveable;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoMetaCell;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.iso.IsoWorld;
import zombie.core.Rand;
import zombie.iso.IsoUtils;
import zombie.core.textures.Texture;
import java.util.function.Consumer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import java.util.Iterator;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.util.list.PZArrayUtil;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import java.io.IOException;
import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.GameWindow;
import java.util.Map;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.network.GameServer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.inventory.types.InventoryContainer;
import zombie.util.StringUtils;
import zombie.inventory.types.Clothing;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.characters.WornItems.BodyLocations;
import zombie.iso.IsoCell;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.characters.WornItems.WornItems;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.textures.ColorInfo;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.iso.IsoObject;

public class IsoMannequin extends IsoObject implements IHumanVisual
{
    private static final ColorInfo inf;
    private boolean bInit;
    private boolean bFemale;
    private boolean bZombie;
    private boolean bSkeleton;
    private String pose;
    private final HumanVisual humanVisual;
    private final ItemVisuals itemVisuals;
    private final WornItems wornItems;
    private final PerPlayer[] perPlayer;
    private boolean bAnimate;
    private AnimatedModel animatedModel;
    private Drawer[] drawers;
    private float screenX;
    private float screenY;
    private static final StaticPerPlayer[] staticPerPlayer;
    
    public IsoMannequin(final IsoCell isoCell) {
        super(isoCell);
        this.bInit = false;
        this.bFemale = false;
        this.bZombie = false;
        this.bSkeleton = false;
        this.pose = null;
        this.humanVisual = new HumanVisual(this);
        this.itemVisuals = new ItemVisuals();
        this.perPlayer = new PerPlayer[4];
        this.bAnimate = false;
        this.animatedModel = null;
        this.drawers = null;
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));
        for (int i = 0; i < 4; ++i) {
            this.perPlayer[i] = new PerPlayer();
        }
    }
    
    public IsoMannequin(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.bInit = false;
        this.bFemale = false;
        this.bZombie = false;
        this.bSkeleton = false;
        this.pose = null;
        this.humanVisual = new HumanVisual(this);
        this.itemVisuals = new ItemVisuals();
        this.perPlayer = new PerPlayer[4];
        this.bAnimate = false;
        this.animatedModel = null;
        this.drawers = null;
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));
        for (int i = 0; i < 4; ++i) {
            this.perPlayer[i] = new PerPlayer();
        }
    }
    
    @Override
    public String getObjectName() {
        return "Mannequin";
    }
    
    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }
    
    @Override
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        this.wornItems.getItemVisuals(itemVisuals);
    }
    
    @Override
    public boolean isFemale() {
        return this.bFemale;
    }
    
    @Override
    public boolean isZombie() {
        return this.bZombie;
    }
    
    @Override
    public boolean isSkeleton() {
        return this.bSkeleton;
    }
    
    @Override
    public boolean isItemAllowedInContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return (inventoryItem instanceof Clothing && !StringUtils.isNullOrWhitespace(((Clothing)inventoryItem).getBodyLocation())) || (inventoryItem instanceof InventoryContainer && !StringUtils.isNullOrWhitespace(((InventoryContainer)inventoryItem).canBeEquipped()));
    }
    
    public String getPose() {
        return this.pose;
    }
    
    public void setRenderDirection(final IsoDirections renderDirection) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (renderDirection == this.perPlayer[playerIndex].renderDirection) {
            return;
        }
        this.perPlayer[playerIndex].renderDirection = renderDirection;
    }
    
    public void rotate(final IsoDirections dir) {
        if (dir == null || dir == IsoDirections.Max) {
            return;
        }
        this.dir = dir;
        for (int i = 0; i < 4; ++i) {
            this.perPlayer[i].atlasTex = null;
        }
        if (GameServer.bServer) {
            this.sendObjectChange("rotate");
        }
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("rotate".equals(anObject)) {
            byteBuffer.put((byte)this.dir.index());
        }
        else {
            super.saveChange(anObject, kahluaTable, byteBuffer);
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("rotate".equals(anObject)) {
            this.rotate(IsoDirections.fromIndex(byteBuffer.get()));
        }
        else {
            super.loadChange(anObject, byteBuffer);
        }
    }
    
    public void getVariables(final Map<String, String> map) {
        map.put("Female", this.bFemale ? "true" : "false");
        map.put("Pose", this.getPose());
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.dir = IsoDirections.fromIndex(byteBuffer.get());
        this.bInit = (byteBuffer.get() == 1);
        this.bFemale = (byteBuffer.get() == 1);
        this.bZombie = (byteBuffer.get() == 1);
        this.bSkeleton = (byteBuffer.get() == 1);
        this.pose = GameWindow.ReadString(byteBuffer);
        this.humanVisual.load(byteBuffer, n);
        this.wornItems.clear();
        if (this.container == null) {
            (this.container = new ItemContainer("mannequin", this.getSquare(), this)).setExplored(true);
        }
        this.container.clear();
        if (byteBuffer.get() == 1) {
            try {
                this.container.ID = byteBuffer.getInt();
                final ArrayList<InventoryItem> load = this.container.load(byteBuffer, n);
                for (byte value = byteBuffer.get(), b2 = 0; b2 < value; ++b2) {
                    final String readString = GameWindow.ReadString(byteBuffer);
                    final short short1 = byteBuffer.getShort();
                    if (short1 >= 0 && short1 < load.size() && this.wornItems.getBodyLocationGroup().getLocation(readString) != null) {
                        this.wornItems.setItem(readString, load.get(short1));
                    }
                }
            }
            catch (Exception ex) {
                if (this.container != null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.container.ID));
                }
            }
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        final ItemContainer container = this.container;
        this.container = null;
        super.save(byteBuffer, b);
        this.container = container;
        byteBuffer.put((byte)this.dir.index());
        byteBuffer.put((byte)(this.bInit ? 1 : 0));
        byteBuffer.put((byte)(this.bFemale ? 1 : 0));
        byteBuffer.put((byte)(this.bZombie ? 1 : 0));
        byteBuffer.put((byte)(this.bSkeleton ? 1 : 0));
        GameWindow.WriteString(byteBuffer, this.pose);
        this.humanVisual.save(byteBuffer);
        if (container != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(container.ID);
            container.save(byteBuffer);
            if (this.wornItems.size() > 127) {
                throw new RuntimeException("too many worn items");
            }
            byteBuffer.put((byte)this.wornItems.size());
            final ArrayList list;
            this.wornItems.forEach(wornItem -> {
                GameWindow.WriteString(byteBuffer, wornItem.getLocation());
                byteBuffer.putShort((short)list.indexOf(wornItem.getItem()));
            });
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void saveState(final ByteBuffer byteBuffer) throws IOException {
        if (!this.bInit) {
            this.initOutfit();
        }
        this.save(byteBuffer);
    }
    
    @Override
    public void loadState(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.get();
        byteBuffer.get();
        this.load(byteBuffer, 186);
        this.initOutfit();
        this.validateSkinTexture();
        this.validatePose();
        this.syncModel();
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        this.initOutfit();
        this.validateSkinTexture();
        this.validatePose();
        this.syncModel();
    }
    
    private void validateSkinTexture() {
        final String skinTexture = this.humanVisual.getSkinTexture();
        if (this.bFemale) {
            if ("F_Mannequin_Black".equals(skinTexture)) {
                return;
            }
            if ("F_Mannequin_White".equals(skinTexture)) {
                return;
            }
        }
        else {
            if ("M_Mannequin_Black".equals(skinTexture)) {
                return;
            }
            if ("M_Mannequin_White".equals(skinTexture)) {
                return;
            }
        }
        this.humanVisual.setSkinTextureName(this.bFemale ? "F_Mannequin_White" : "M_Mannequin_White");
    }
    
    private void validatePose() {
        final AnimState getState = AnimationSet.GetAnimationSet("mannequin", false).GetState(this.bFemale ? "female" : "male");
        final Iterator<AnimNode> iterator = getState.m_Nodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().m_Name.equalsIgnoreCase(this.pose)) {
                return;
            }
        }
        if (getState.m_Nodes == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getState.m_Name, this.pose));
            this.pose = "Invalid";
            return;
        }
        this.pose = PZArrayUtil.pickRandom(getState.m_Nodes).m_Name;
    }
    
    @Override
    public void render(float n, float n2, final float n3, ColorInfo inf, final boolean b, final boolean b2, final Shader shader) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        n += 0.5f;
        n2 += 0.5f;
        this.calcScreenPos(n, n2, n3);
        this.renderShadow(n, n2, n3);
        if (this.bAnimate) {
            this.animatedModel.update();
            final Drawer drawer = this.drawers[SpriteRenderer.instance.getMainStateIndex()];
            drawer.init(n, n2, n3);
            SpriteRenderer.instance.drawGeneric(drawer);
            return;
        }
        final IsoDirections dir = this.dir;
        final PerPlayer perPlayer = this.perPlayer[playerIndex];
        if (perPlayer.renderDirection != null && perPlayer.renderDirection != IsoDirections.Max) {
            this.dir = perPlayer.renderDirection;
            perPlayer.renderDirection = null;
            perPlayer.bWasRenderDirection = true;
            perPlayer.atlasTex = null;
        }
        else if (perPlayer.bWasRenderDirection) {
            perPlayer.bWasRenderDirection = false;
            perPlayer.atlasTex = null;
        }
        if (perPlayer.atlasTex == null) {
            perPlayer.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
            DeadBodyAtlas.instance.render();
        }
        this.dir = dir;
        if (perPlayer.atlasTex != null) {
            if (this.isHighlighted()) {
                IsoMannequin.inf.r = this.getHighlightColor().r;
                IsoMannequin.inf.g = this.getHighlightColor().g;
                IsoMannequin.inf.b = this.getHighlightColor().b;
                IsoMannequin.inf.a = this.getHighlightColor().a;
            }
            else {
                IsoMannequin.inf.r = inf.r;
                IsoMannequin.inf.g = inf.g;
                IsoMannequin.inf.b = inf.b;
                IsoMannequin.inf.a = inf.a;
            }
            inf = IsoMannequin.inf;
            if (!this.isHighlighted() && PerformanceSettings.LightingFrameSkip < 3) {
                this.square.interpolateLight(inf, n - this.square.getX(), n2 - this.square.getY());
            }
            this.screenY -= 30 * Core.TileScale;
            final Texture atlasTex = perPlayer.atlasTex;
            atlasTex.render((float)((int)this.screenX - atlasTex.getWidth() / 2), (float)((int)this.screenY - atlasTex.getHeight() / 2), (float)atlasTex.getWidth(), (float)atlasTex.getHeight(), inf.r, inf.g, inf.b, this.getAlpha(playerIndex), null);
            if (Core.bDebug) {}
        }
    }
    
    @Override
    public void renderFxMask(final float n, final float n2, final float n3, final boolean b) {
    }
    
    private void calcScreenPos(final float n, final float n2, final float n3) {
        if (IsoSprite.globalOffsetX == -1.0f) {
            IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
            IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
        }
        this.screenX = IsoUtils.XToScreen(n, n2, n3, 0);
        this.screenY = IsoUtils.YToScreen(n, n2, n3, 0);
        this.sx = this.screenX;
        this.sy = this.screenY;
        this.screenX = this.sx + IsoSprite.globalOffsetX;
        this.screenY = this.sy + IsoSprite.globalOffsetY;
        final IsoObject[] array = this.square.getObjects().getElements();
        for (int i = 0; i < this.square.getObjects().size(); ++i) {
            final IsoObject isoObject = array[i];
            if (isoObject.isTableSurface()) {
                this.screenY -= (isoObject.getSurfaceOffset() + 1.0f) * Core.TileScale;
            }
        }
    }
    
    private void renderShadow(final float n, final float n2, final float n3) {
        final Texture sharedTexture = Texture.getSharedTexture("dropshadow");
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final float n4 = 0.8f * this.getAlpha(playerIndex);
        final ColorInfo lightInfo = this.square.lighting[playerIndex].lightInfo();
        SpriteRenderer.instance.render(sharedTexture, this.screenX - sharedTexture.getWidth() / 2.0f * Core.TileScale, this.screenY - sharedTexture.getHeight() / 2.0f * Core.TileScale, sharedTexture.getWidth() * (float)Core.TileScale, sharedTexture.getHeight() * (float)Core.TileScale, 1.0f, 1.0f, 1.0f, n4 * ((lightInfo.r + lightInfo.g + lightInfo.b) / 3.0f) * 0.8f, null);
    }
    
    private void initOutfit() {
        if (this.bInit) {
            return;
        }
        this.bInit = true;
        this.bFemale = (Rand.Next(2) == 0);
        String skin = "White";
        final String name = this.sprite.name;
        switch (name) {
            case "location_shop_mall_01_65": {
                this.bFemale = true;
                this.pose = "pose01";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_66": {
                this.bFemale = true;
                this.pose = "pose02";
                this.dir = IsoDirections.S;
                break;
            }
            case "location_shop_mall_01_67": {
                this.bFemale = true;
                this.pose = "pose03";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_68": {
                this.bFemale = false;
                this.pose = "pose01";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_69": {
                this.bFemale = false;
                this.pose = "pose02";
                this.dir = IsoDirections.S;
                break;
            }
            case "location_shop_mall_01_70": {
                this.bFemale = false;
                this.pose = "pose03";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_73": {
                this.bFemale = true;
                this.pose = "pose01";
                skin = "Black";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_74": {
                this.bFemale = true;
                this.pose = "pose02";
                skin = "Black";
                this.dir = IsoDirections.S;
                break;
            }
            case "location_shop_mall_01_75": {
                this.bFemale = true;
                this.pose = "pose03";
                skin = "Black";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_76": {
                this.bFemale = false;
                this.pose = "pose01";
                skin = "Black";
                this.dir = IsoDirections.SE;
                break;
            }
            case "location_shop_mall_01_77": {
                this.bFemale = false;
                this.pose = "pose02";
                skin = "Black";
                this.dir = IsoDirections.S;
                break;
            }
            case "location_shop_mall_01_78": {
                this.bFemale = false;
                this.pose = "pose03";
                skin = "Black";
                this.dir = IsoDirections.SE;
                break;
            }
        }
        final IsoMetaCell cellData = IsoWorld.instance.getMetaGrid().getCellData(this.square.x / 300, this.square.y / 300);
        final ArrayList<MannequinZone> list = (cellData == null) ? null : cellData.mannequinZones;
        if (list != null) {
            MannequinZone mannequinZone = null;
            for (int i = 0; i < list.size(); ++i) {
                mannequinZone = list.get(i);
                if (mannequinZone.contains(this.square.x, this.square.y, this.square.z)) {
                    break;
                }
                mannequinZone = null;
            }
            if (mannequinZone != null) {
                if (mannequinZone.bFemale != -1) {
                    this.bFemale = (mannequinZone.bFemale == 1);
                }
                if (mannequinZone.dir != IsoDirections.Max) {
                    this.dir = mannequinZone.dir;
                }
                if (mannequinZone.skin != null) {
                    skin = mannequinZone.skin;
                }
                if (mannequinZone.pose != null) {
                    this.pose = mannequinZone.pose;
                }
            }
        }
        if (this.pose == null) {
            this.pose = PZArrayUtil.pickRandom(AnimationSet.GetAnimationSet("mannequin", false).GetState(this.bFemale ? "female" : "male").m_Nodes).m_Name;
        }
        this.humanVisual.dressInNamedOutfit(OutfitManager.instance.GetRandomNonProfessionalOutfit(this.bFemale).m_Name, this.itemVisuals);
        this.humanVisual.setSkinTextureName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.bFemale ? "F_Mannequin_" : "M_Mannequin_", skin));
        this.humanVisual.setHairModel("");
        this.humanVisual.setBeardModel("");
        this.createInventory(this.itemVisuals);
    }
    
    private void syncModel() {
        this.wornItems.getItemVisuals(this.itemVisuals);
        for (int i = 0; i < 4; ++i) {
            this.perPlayer[i].atlasTex = null;
        }
        if (this.bAnimate) {
            if (this.animatedModel == null) {
                this.animatedModel = new AnimatedModel();
                this.drawers = new Drawer[3];
                for (int j = 0; j < this.drawers.length; ++j) {
                    this.drawers[j] = new Drawer();
                }
            }
            this.animatedModel.setAnimSetName("mannequin");
            this.animatedModel.setState(this.bFemale ? "female" : "male");
            this.animatedModel.setVariable("Female", this.bFemale);
            this.animatedModel.setVariable("Pose", this.getPose());
            this.animatedModel.setAngle(this.dir.ToVector());
            this.animatedModel.setModelData(this.humanVisual, this.itemVisuals);
        }
    }
    
    private void createInventory(final ItemVisuals fromItemVisuals) {
        if (this.container == null) {
            (this.container = new ItemContainer("mannequin", this.getSquare(), this)).setExplored(true);
        }
        this.container.clear();
        this.wornItems.setFromItemVisuals(fromItemVisuals);
        this.wornItems.addItemsToItemContainer(this.container);
    }
    
    public void wearItem(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter) {
        if (!this.container.contains(inventoryItem)) {
            return;
        }
        if (inventoryItem.getVisual() == null) {
            return;
        }
        if (inventoryItem instanceof Clothing && !StringUtils.isNullOrWhitespace(((Clothing)inventoryItem).getBodyLocation())) {
            this.wornItems.setItem(((Clothing)inventoryItem).getBodyLocation(), inventoryItem);
        }
        else {
            if (!(inventoryItem instanceof InventoryContainer) || StringUtils.isNullOrWhitespace(((InventoryContainer)inventoryItem).canBeEquipped())) {
                return;
            }
            this.wornItems.setItem(((InventoryContainer)inventoryItem).canBeEquipped(), inventoryItem);
        }
        if (isoGameCharacter != null) {
            final ArrayList<InventoryItem> items = this.container.getItems();
            for (int i = 0; i < items.size(); ++i) {
                final InventoryItem inventoryItem2 = items.get(i);
                if (!this.wornItems.contains(inventoryItem2)) {
                    this.container.removeItemOnServer(inventoryItem2);
                    this.container.Remove(inventoryItem2);
                    isoGameCharacter.getInventory().AddItem(inventoryItem2);
                    --i;
                }
            }
        }
        this.syncModel();
    }
    
    public void checkClothing(final InventoryItem inventoryItem) {
        for (int i = 0; i < this.wornItems.size(); ++i) {
            final InventoryItem itemByIndex = this.wornItems.getItemByIndex(i);
            if (this.container == null || this.container.getItems().indexOf(itemByIndex) == -1) {
                this.wornItems.remove(itemByIndex);
                this.syncModel();
                --i;
            }
        }
    }
    
    public void getCustomSettingsFromItem(final InventoryItem inventoryItem) throws IOException {
        if (inventoryItem instanceof Moveable) {
            final ByteBuffer byteData = inventoryItem.getByteData();
            byteData.rewind();
            final int int1 = byteData.getInt();
            byteData.get();
            byteData.get();
            this.load(byteData, int1);
        }
    }
    
    public void setCustomSettingsToItem(final InventoryItem inventoryItem) throws IOException {
        if (inventoryItem instanceof Moveable) {
            synchronized (SliceY.SliceBufferLock) {
                final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                sliceBuffer.clear();
                sliceBuffer.putInt(186);
                this.save(sliceBuffer);
                sliceBuffer.flip();
                (inventoryItem.byteData = ByteBuffer.allocate(sliceBuffer.limit())).put(sliceBuffer);
            }
            if (this.container != null) {
                inventoryItem.setActualWeight(inventoryItem.getActualWeight() + this.container.getContentsWeight());
            }
        }
    }
    
    public static boolean isMannequinSprite(final IsoSprite isoSprite) {
        return "Mannequin".equals(isoSprite.getProperties().Val("CustomName"));
    }
    
    public static void renderMoveableItem(final Moveable moveable, final int n, final int n2, final int n3, final IsoDirections isoDirections) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        StaticPerPlayer staticPerPlayer = IsoMannequin.staticPerPlayer[playerIndex];
        if (staticPerPlayer == null) {
            final StaticPerPlayer[] staticPerPlayer2 = IsoMannequin.staticPerPlayer;
            final int n4 = playerIndex;
            final StaticPerPlayer staticPerPlayer3 = new StaticPerPlayer(playerIndex);
            staticPerPlayer2[n4] = staticPerPlayer3;
            staticPerPlayer = staticPerPlayer3;
        }
        staticPerPlayer.renderMoveableItem(moveable, n, n2, n3, isoDirections);
    }
    
    public static void renderMoveableObject(final IsoMannequin isoMannequin, final int n, final int n2, final int n3, final IsoDirections renderDirection) {
        isoMannequin.setRenderDirection(renderDirection);
    }
    
    public static IsoDirections getDirectionFromItem(final Moveable moveable, final int n) {
        StaticPerPlayer staticPerPlayer = IsoMannequin.staticPerPlayer[n];
        if (staticPerPlayer == null) {
            final StaticPerPlayer[] staticPerPlayer2 = IsoMannequin.staticPerPlayer;
            final StaticPerPlayer staticPerPlayer3 = new StaticPerPlayer(n);
            staticPerPlayer2[n] = staticPerPlayer3;
            staticPerPlayer = staticPerPlayer3;
        }
        return staticPerPlayer.getDirectionFromItem(moveable);
    }
    
    static {
        inf = new ColorInfo();
        staticPerPlayer = new StaticPerPlayer[4];
    }
    
    private static final class PerPlayer
    {
        private Texture atlasTex;
        IsoDirections renderDirection;
        boolean bWasRenderDirection;
        
        private PerPlayer() {
            this.atlasTex = null;
            this.renderDirection = null;
            this.bWasRenderDirection = false;
        }
    }
    
    private final class Drawer extends TextureDraw.GenericDrawer
    {
        float x;
        float y;
        float z;
        float m_animPlayerAngle;
        boolean bRendered;
        
        public void init(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.bRendered = false;
            IsoMannequin.this.animatedModel.renderMain();
            this.m_animPlayerAngle = IsoMannequin.this.animatedModel.getAnimationPlayer().getRenderedAngle();
        }
        
        @Override
        public void render() {
            IsoMannequin.this.animatedModel.DoRenderToWorld(this.x, this.y, this.z, this.m_animPlayerAngle);
            this.bRendered = true;
        }
        
        @Override
        public void postRender() {
            IsoMannequin.this.animatedModel.postRender(this.bRendered);
        }
    }
    
    public static final class MannequinZone extends IsoMetaGrid.Zone
    {
        public int bFemale;
        public IsoDirections dir;
        public String pose;
        public String skin;
        
        public MannequinZone(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5, final KahluaTable kahluaTable) {
            super(s, s2, n, n2, n3, n4, n5);
            this.bFemale = -1;
            this.dir = IsoDirections.Max;
            this.pose = null;
            this.skin = null;
            if (kahluaTable != null) {
                final Object rawget = kahluaTable.rawget((Object)"Female");
                if (rawget instanceof Boolean) {
                    this.bFemale = ((rawget == Boolean.TRUE) ? 1 : 0);
                }
                final Object rawget2 = kahluaTable.rawget((Object)"Direction");
                if (rawget2 instanceof String) {
                    this.dir = IsoDirections.valueOf((String)rawget2);
                }
                final Object rawget3 = kahluaTable.rawget((Object)"Skin");
                if (rawget3 instanceof String) {
                    this.skin = (String)rawget3;
                }
                final Object rawget4 = kahluaTable.rawget((Object)"Pose");
                if (rawget4 instanceof String) {
                    this.pose = (String)rawget4;
                }
            }
        }
    }
    
    private static final class StaticPerPlayer
    {
        final int playerIndex;
        Moveable _moveable;
        IsoMannequin _mannequin;
        
        StaticPerPlayer(final int playerIndex) {
            this._moveable = null;
            this._mannequin = null;
            this.playerIndex = playerIndex;
        }
        
        void renderMoveableItem(final Moveable moveable, final int n, final int n2, final int n3, final IsoDirections renderDirection) {
            if (!this.checkItem(moveable)) {
                return;
            }
            if (this._moveable != moveable) {
                this._moveable = moveable;
                try {
                    this._mannequin.getCustomSettingsFromItem(this._moveable);
                }
                catch (IOException ex) {}
                this._mannequin.initOutfit();
                this._mannequin.validateSkinTexture();
                this._mannequin.validatePose();
                this._mannequin.syncModel();
                this._mannequin.perPlayer[this.playerIndex].atlasTex = null;
            }
            this._mannequin.square = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            if (this._mannequin.square == null) {
                return;
            }
            this._mannequin.perPlayer[this.playerIndex].renderDirection = renderDirection;
            IsoMannequin.inf.set(1.0f, 1.0f, 1.0f, 1.0f);
            this._mannequin.render((float)n, (float)n2, (float)n3, IsoMannequin.inf, false, false, null);
        }
        
        IsoDirections getDirectionFromItem(final Moveable moveable) {
            if (!this.checkItem(moveable)) {
                return IsoDirections.S;
            }
            this._moveable = null;
            try {
                this._mannequin.getCustomSettingsFromItem(moveable);
                return this._mannequin.getDir();
            }
            catch (Exception ex) {
                return IsoDirections.S;
            }
        }
        
        boolean checkItem(final Moveable moveable) {
            if (moveable == null) {
                return false;
            }
            final IsoSprite sprite = IsoSpriteManager.instance.getSprite(moveable.getWorldSprite());
            if (sprite == null || !IsoMannequin.isMannequinSprite(sprite)) {
                return false;
            }
            if (moveable.getByteData() == null) {
                return false;
            }
            if (this._mannequin == null || this._mannequin.getCell() != IsoWorld.instance.CurrentCell) {
                this._mannequin = new IsoMannequin(IsoWorld.instance.CurrentCell);
            }
            return true;
        }
    }
}
