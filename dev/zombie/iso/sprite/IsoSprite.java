// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import java.util.Stack;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.CharacterModelCamera;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWater;
import zombie.util.StringUtils;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import java.util.function.Consumer;
import zombie.iso.IsoUtils;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoCamera;
import zombie.vehicles.BaseVehicle;
import zombie.core.textures.TextureDraw;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.vehicles.VehicleModelCamera;
import zombie.core.skinnedmodel.ModelCameraRenderData;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import zombie.core.Core;
import zombie.core.textures.Mask;
import zombie.core.textures.Texture;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.iso.IsoDirections;
import java.util.Iterator;
import java.io.DataInputStream;
import java.io.IOException;
import zombie.GameWindow;
import java.io.DataOutputStream;
import zombie.iso.WorldConverter;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.core.skinnedmodel.ModelManager;
import java.util.ArrayList;
import zombie.core.properties.PropertyContainer;
import java.util.HashMap;
import zombie.core.textures.ColorInfo;

public final class IsoSprite
{
    public static int maxCount;
    public static float alphaStep;
    public static float globalOffsetX;
    public static float globalOffsetY;
    private static final ColorInfo info;
    private static final HashMap<String, Object[]> AnimNameSet;
    public int firerequirement;
    public String burntTile;
    public boolean forceAmbient;
    public boolean solidfloor;
    public boolean canBeRemoved;
    public boolean attachedFloor;
    public boolean cutW;
    public boolean cutN;
    public boolean solid;
    public boolean solidTrans;
    public boolean invisible;
    public boolean alwaysDraw;
    public boolean forceRender;
    public boolean moveWithWind;
    public boolean isBush;
    public static final byte RL_DEFAULT = 0;
    public static final byte RL_FLOOR = 1;
    public byte renderLayer;
    public int windType;
    public boolean Animate;
    public IsoAnim CurrentAnim;
    public boolean DeleteWhenFinished;
    public boolean Loop;
    public short soffX;
    public short soffY;
    public final PropertyContainer Properties;
    public final ColorInfo TintMod;
    public final HashMap<String, IsoAnim> AnimMap;
    public final ArrayList<IsoAnim> AnimStack;
    public String name;
    public int tileSheetIndex;
    public int ID;
    public IsoSpriteInstance def;
    public ModelManager.ModelSlot modelSlot;
    IsoSpriteManager parentManager;
    private IsoObjectType type;
    private String parentObjectName;
    private IsoSpriteGrid spriteGrid;
    public boolean treatAsWallOrder;
    private boolean hideForWaterRender;
    
    public void setHideForWaterRender() {
        this.hideForWaterRender = true;
    }
    
    public IsoSprite() {
        this.moveWithWind = false;
        this.isBush = false;
        this.renderLayer = 0;
        this.windType = 1;
        this.Animate = true;
        this.CurrentAnim = null;
        this.DeleteWhenFinished = false;
        this.Loop = true;
        this.soffX = 0;
        this.soffY = 0;
        this.Properties = new PropertyContainer();
        this.TintMod = new ColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
        this.AnimMap = new HashMap<String, IsoAnim>(2);
        this.AnimStack = new ArrayList<IsoAnim>(1);
        this.tileSheetIndex = 0;
        this.ID = 20000000;
        this.type = IsoObjectType.MAX;
        this.parentObjectName = null;
        this.treatAsWallOrder = false;
        this.hideForWaterRender = false;
        this.parentManager = IsoSpriteManager.instance;
        this.def = IsoSpriteInstance.get(this);
    }
    
    public IsoSprite(final IsoSpriteManager parentManager) {
        this.moveWithWind = false;
        this.isBush = false;
        this.renderLayer = 0;
        this.windType = 1;
        this.Animate = true;
        this.CurrentAnim = null;
        this.DeleteWhenFinished = false;
        this.Loop = true;
        this.soffX = 0;
        this.soffY = 0;
        this.Properties = new PropertyContainer();
        this.TintMod = new ColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
        this.AnimMap = new HashMap<String, IsoAnim>(2);
        this.AnimStack = new ArrayList<IsoAnim>(1);
        this.tileSheetIndex = 0;
        this.ID = 20000000;
        this.type = IsoObjectType.MAX;
        this.parentObjectName = null;
        this.treatAsWallOrder = false;
        this.hideForWaterRender = false;
        this.parentManager = parentManager;
        this.def = IsoSpriteInstance.get(this);
    }
    
    public static IsoSprite CreateSprite(final IsoSpriteManager isoSpriteManager) {
        return new IsoSprite(isoSpriteManager);
    }
    
    public static IsoSprite CreateSpriteUsingCache(final String s, final String s2, final int n) {
        return CreateSprite(IsoSpriteManager.instance).setFromCache(s, s2, n);
    }
    
    public static IsoSprite getSprite(final IsoSpriteManager isoSpriteManager, int intValue) {
        if (WorldConverter.instance.TilesetConversions != null && !WorldConverter.instance.TilesetConversions.isEmpty() && WorldConverter.instance.TilesetConversions.containsKey(intValue)) {
            intValue = WorldConverter.instance.TilesetConversions.get(intValue);
        }
        if (isoSpriteManager.IntMap.containsKey(intValue)) {
            return (IsoSprite)isoSpriteManager.IntMap.get(intValue);
        }
        return null;
    }
    
    public static void setSpriteID(final IsoSpriteManager isoSpriteManager, final int id, final IsoSprite isoSprite) {
        if (isoSpriteManager.IntMap.containsKey(isoSprite.ID)) {
            isoSpriteManager.IntMap.remove(isoSprite.ID);
            isoSprite.ID = id;
            isoSpriteManager.IntMap.put(id, (Object)isoSprite);
        }
    }
    
    public static IsoSprite getSprite(final IsoSpriteManager isoSpriteManager, final IsoSprite isoSprite, final int n) {
        if (isoSprite.name.contains("_")) {
            final String[] split = isoSprite.name.split("_");
            return isoSpriteManager.NamedMap.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, isoSprite.name.substring(0, isoSprite.name.lastIndexOf("_")), Integer.parseInt(split[split.length - 1].trim()) + n));
        }
        return null;
    }
    
    public static IsoSprite getSprite(final IsoSpriteManager isoSpriteManager, final String key, final int n) {
        final IsoSprite isoSprite = isoSpriteManager.NamedMap.get(key);
        final String substring = isoSprite.name.substring(0, isoSprite.name.lastIndexOf(95));
        final String substring2 = isoSprite.name.substring(isoSprite.name.lastIndexOf(95) + 1);
        if (isoSprite.name.contains("_")) {
            return isoSpriteManager.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, substring, Integer.parseInt(substring2.trim()) + n));
        }
        return null;
    }
    
    public static void DisposeAll() {
        IsoSprite.AnimNameSet.clear();
    }
    
    public static boolean HasCache(final String key) {
        return IsoSprite.AnimNameSet.containsKey(key);
    }
    
    public IsoSpriteInstance newInstance() {
        return IsoSpriteInstance.get(this);
    }
    
    public PropertyContainer getProperties() {
        return this.Properties;
    }
    
    public String getParentObjectName() {
        return this.parentObjectName;
    }
    
    public void setParentObjectName(final String parentObjectName) {
        this.parentObjectName = parentObjectName;
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        GameWindow.WriteString(dataOutputStream, this.name);
    }
    
    public void load(final DataInputStream dataInputStream) throws IOException {
        this.LoadFramesNoDirPageSimple(this.name = GameWindow.ReadString(dataInputStream));
    }
    
    public void Dispose() {
        final Iterator<IsoAnim> iterator = this.AnimMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().Dispose();
        }
        this.AnimMap.clear();
        this.AnimStack.clear();
        this.CurrentAnim = null;
    }
    
    public boolean isMaskClicked(final IsoDirections isoDirections, int n, int n2) {
        try {
            final Texture texture = this.CurrentAnim.Frames.get((int)this.def.Frame).directions[isoDirections.index()];
            if (texture == null) {
                return false;
            }
            final Mask mask = texture.getMask();
            if (mask == null) {
                return false;
            }
            n -= (int)texture.offsetX;
            n2 -= (int)texture.offsetY;
            return mask.get(n, n2);
        }
        catch (Exception thrown) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
            return true;
        }
    }
    
    public boolean isMaskClicked(final IsoDirections isoDirections, int n, int n2, final boolean b) {
        if (this.CurrentAnim == null) {
            return false;
        }
        this.initSpriteInstance();
        try {
            if (this.CurrentAnim == null || this.CurrentAnim.Frames == null || this.def.Frame >= this.CurrentAnim.Frames.size()) {
                return false;
            }
            final Texture texture = this.CurrentAnim.Frames.get((int)this.def.Frame).directions[isoDirections.index()];
            if (texture == null) {
                return false;
            }
            final Mask mask = texture.getMask();
            if (mask == null) {
                return false;
            }
            if (b) {
                n -= (int)(texture.getWidthOrig() - texture.getWidth() - texture.offsetX);
                n2 -= (int)texture.offsetY;
                n = texture.getWidth() - n;
            }
            else {
                n -= (int)texture.offsetX;
                n2 -= (int)texture.offsetY;
            }
            return n >= 0 && n2 >= 0 && n <= texture.getWidth() && n2 <= texture.getHeight() && mask.get(n, n2);
        }
        catch (Exception thrown) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
            return true;
        }
    }
    
    public float getMaskClickedY(final IsoDirections isoDirections, int n, int n2, final boolean b) {
        try {
            final Texture texture = this.CurrentAnim.Frames.get((int)this.def.Frame).directions[isoDirections.index()];
            if (texture == null) {
                return 10000.0f;
            }
            if (texture.getMask() == null) {
                return 10000.0f;
            }
            if (b) {
                n -= (int)(texture.getWidthOrig() - texture.getWidth() - texture.offsetX);
                n2 -= (int)texture.offsetY;
                n = texture.getWidth() - n;
            }
            else {
                n -= (int)texture.offsetX;
                n2 -= (int)texture.offsetY;
                n = texture.getWidth() - n;
            }
            return (float)n2;
        }
        catch (Exception thrown) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
            return 10000.0f;
        }
    }
    
    public Texture LoadFrameExplicit(final String s) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put("default", this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        return this.CurrentAnim.LoadFrameExplicit(s);
    }
    
    public void LoadFrames(final String s, final String s2, final int n) {
        if (this.AnimMap.containsKey(s2)) {
            return;
        }
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(s2, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFrames(s, s2, n);
    }
    
    public void LoadFramesReverseAltName(final String s, final String s2, final String s3, final int n) {
        if (this.AnimMap.containsKey(s3)) {
            return;
        }
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(s3, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesReverseAltName(s, s2, s3, n);
    }
    
    public void LoadFramesNoDirPage(final String s, final String key, final int n) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(key, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesNoDirPage(s, key, n);
    }
    
    public void LoadFramesNoDirPageDirect(final String s, final String key, final int n) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(key, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesNoDirPageDirect(s, key, n);
    }
    
    public void LoadFramesNoDirPageSimple(final String s) {
        if (this.AnimMap.containsKey("default")) {
            this.AnimStack.remove(this.AnimMap.get("default"));
            this.AnimMap.remove("default");
        }
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put("default", this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesNoDirPage(s);
    }
    
    public void ReplaceCurrentAnimFrames(final String s) {
        if (this.CurrentAnim == null) {
            return;
        }
        this.CurrentAnim.Frames.clear();
        this.CurrentAnim.LoadFramesNoDirPage(s);
    }
    
    public void LoadFramesPageSimple(final String s, final String s2, final String s3, final String s4) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put("default", this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesPageSimple(s, s2, s3, s4);
    }
    
    public void LoadFramesPcx(final String s, final String s2, final int n) {
        if (this.AnimMap.containsKey(s2)) {
            return;
        }
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(s2, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesPcx(s, s2, n);
    }
    
    public void PlayAnim(final IsoAnim currentAnim) {
        if (this.CurrentAnim == null || this.CurrentAnim != currentAnim) {
            this.CurrentAnim = currentAnim;
        }
    }
    
    public void PlayAnim(final String key) {
        if ((this.CurrentAnim == null || !this.CurrentAnim.name.equals(key)) && this.AnimMap.containsKey(key)) {
            this.CurrentAnim = this.AnimMap.get(key);
        }
    }
    
    public void PlayAnimUnlooped(final String key) {
        if (this.AnimMap.containsKey(key)) {
            if (this.CurrentAnim == null || !this.CurrentAnim.name.equals(key)) {
                this.CurrentAnim = this.AnimMap.get(key);
            }
            this.CurrentAnim.looped = false;
        }
    }
    
    public void ChangeTintMod(final ColorInfo colorInfo) {
        this.TintMod.r = colorInfo.r;
        this.TintMod.g = colorInfo.g;
        this.TintMod.b = colorInfo.b;
        this.TintMod.a = colorInfo.a;
    }
    
    public void RenderGhostTile(final int n, final int n2, final int n3) {
        final IsoSpriteInstance value;
        final IsoSpriteInstance isoSpriteInstance = value = IsoSpriteInstance.get(this);
        final float n4 = 0.6f;
        isoSpriteInstance.targetAlpha = n4;
        value.alpha = n4;
        this.render(isoSpriteInstance, null, (float)n, (float)n2, (float)n3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo(), true);
    }
    
    public void RenderGhostTileRed(final int n, final int n2, final int n3) {
        final IsoSpriteInstance value = IsoSpriteInstance.get(this);
        value.tintr = 0.65f;
        value.tintg = 0.2f;
        value.tintb = 0.2f;
        final IsoSpriteInstance isoSpriteInstance = value;
        final IsoSpriteInstance isoSpriteInstance2 = value;
        final float n4 = 0.6f;
        isoSpriteInstance2.targetAlpha = n4;
        isoSpriteInstance.alpha = n4;
        this.render(value, null, (float)n, (float)n2, (float)n3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo(), true);
    }
    
    public void RenderGhostTileColor(final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final float n7) {
        this.RenderGhostTileColor(n, n2, n3, 0.0f, 0.0f, n4, n5, n6, n7);
    }
    
    public void RenderGhostTileColor(final int n, final int n2, final int n3, final float n4, final float n5, final float tintr, final float tintg, final float tintb, final float n6) {
        final IsoSpriteInstance value = IsoSpriteInstance.get(this);
        value.tintr = tintr;
        value.tintg = tintg;
        value.tintb = tintb;
        final IsoSpriteInstance isoSpriteInstance = value;
        value.targetAlpha = n6;
        isoSpriteInstance.alpha = n6;
        final ColorInfo defColorInfo = IsoGridSquare.getDefColorInfo();
        final ColorInfo defColorInfo2 = IsoGridSquare.getDefColorInfo();
        final ColorInfo defColorInfo3 = IsoGridSquare.getDefColorInfo();
        final ColorInfo defColorInfo4 = IsoGridSquare.getDefColorInfo();
        final float n7 = 1.0f;
        defColorInfo4.a = n7;
        defColorInfo3.b = n7;
        defColorInfo2.g = n7;
        defColorInfo.r = n7;
        final int tileScale = Core.TileScale;
        this.render(value, null, (float)n, (float)n2, (float)n3, IsoDirections.N, 32 * tileScale + n4, 96 * tileScale + n5, IsoGridSquare.getDefColorInfo(), true);
    }
    
    public boolean hasActiveModel() {
        return ModelManager.instance.bDebugEnableModels && ModelManager.instance.isCreated() && this.modelSlot != null && this.modelSlot.active;
    }
    
    public void renderVehicle(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final float n, final float n2, final float n3, final float n4, final float n5, final ColorInfo colorInfo, final boolean b) {
        if (isoSpriteInstance == null) {
            return;
        }
        if (this.hasActiveModel()) {
            SpriteRenderer.instance.drawGeneric(ModelCameraRenderData.s_pool.alloc().init(VehicleModelCamera.instance, this.modelSlot));
            SpriteRenderer.instance.drawModel(this.modelSlot);
            if (!BaseVehicle.RENDER_TO_TEXTURE) {
                return;
            }
        }
        IsoSprite.info.r = colorInfo.r;
        IsoSprite.info.g = colorInfo.g;
        IsoSprite.info.b = colorInfo.b;
        IsoSprite.info.a = colorInfo.a;
        try {
            if (b) {
                isoSpriteInstance.renderprep(isoObject);
            }
            float sx = 0.0f;
            float sy = 0.0f;
            if (IsoSprite.globalOffsetX == -1.0f) {
                IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
                IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
            }
            if (isoObject == null || isoObject.sx == 0.0f || isoObject instanceof IsoMovingObject) {
                final float xToScreen = IsoUtils.XToScreen(n + isoSpriteInstance.offX, n2 + isoSpriteInstance.offY, n3 + isoSpriteInstance.offZ, 0);
                final float yToScreen = IsoUtils.YToScreen(n + isoSpriteInstance.offX, n2 + isoSpriteInstance.offY, n3 + isoSpriteInstance.offZ, 0);
                sx = xToScreen - n4;
                sy = yToScreen - n5;
                if (isoObject != null) {
                    isoObject.sx = sx;
                    isoObject.sy = sy;
                }
            }
            float n8;
            float n9;
            if (isoObject != null) {
                final float n6 = isoObject.sx + IsoSprite.globalOffsetX;
                final float n7 = isoObject.sy + IsoSprite.globalOffsetY;
                n8 = n6 + this.soffX;
                n9 = n7 + this.soffY;
            }
            else {
                final float n10 = sx + IsoSprite.globalOffsetX;
                final float n11 = sy + IsoSprite.globalOffsetY;
                n8 = n10 + this.soffX;
                n9 = n11 + this.soffY;
            }
            if (b) {
                if (isoSpriteInstance.tintr != 1.0f || isoSpriteInstance.tintg != 1.0f || isoSpriteInstance.tintb != 1.0f) {
                    final ColorInfo info = IsoSprite.info;
                    info.r *= isoSpriteInstance.tintr;
                    final ColorInfo info2 = IsoSprite.info;
                    info2.g *= isoSpriteInstance.tintg;
                    final ColorInfo info3 = IsoSprite.info;
                    info3.b *= isoSpriteInstance.tintb;
                }
                IsoSprite.info.a = isoSpriteInstance.alpha;
            }
            if (!this.hasActiveModel() && (this.TintMod.r != 1.0f || this.TintMod.g != 1.0f || this.TintMod.b != 1.0f)) {
                final ColorInfo info4 = IsoSprite.info;
                info4.r *= this.TintMod.r;
                final ColorInfo info5 = IsoSprite.info;
                info5.g *= this.TintMod.g;
                final ColorInfo info6 = IsoSprite.info;
                info6.b *= this.TintMod.b;
            }
            if (this.hasActiveModel()) {
                final float n12 = isoSpriteInstance.getScaleX() * Core.TileScale;
                final float n13 = -isoSpriteInstance.getScaleY() * Core.TileScale;
                final float n14 = 0.666f;
                final float n15 = n12 / (4.0f * n14);
                final float n16 = n13 / (4.0f * n14);
                final int width = ModelManager.instance.bitmap.getTexture().getWidth();
                final int height = ModelManager.instance.bitmap.getTexture().getHeight();
                final float n17 = n8 - width * n15 / 2.0f;
                final float n18 = n9 - height * n16 / 2.0f + 96.0f * (((BaseVehicle)isoObject).jniTransform.origin.y / 2.46f) / n16 / n14 + 27.84f / n16 / n14;
                if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                    SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), n17, n18, width * n15, height * n16, 1.0f, 1.0f, 1.0f, IsoSprite.info.a, null);
                }
                else {
                    SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), n17, n18, width * n15, height * n16, IsoSprite.info.r, IsoSprite.info.g, IsoSprite.info.b, IsoSprite.info.a, null);
                }
                if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
                    LineDrawer.drawRect(n17, n18, width * n15, height * n16, 1.0f, 1.0f, 1.0f, 1.0f, 1);
                }
            }
            IsoSprite.info.r = 1.0f;
            IsoSprite.info.g = 1.0f;
            IsoSprite.info.b = 1.0f;
        }
        catch (Exception thrown) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
        }
    }
    
    private IsoSpriteInstance getSpriteInstance() {
        this.initSpriteInstance();
        return this.def;
    }
    
    private void initSpriteInstance() {
        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this);
        }
    }
    
    public final void render(final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final ColorInfo colorInfo, final boolean b) {
        this.render(isoObject, n, n2, n3, isoDirections, n4, n5, colorInfo, b, null);
    }
    
    public final void render(final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final ColorInfo colorInfo, final boolean b, final Consumer<TextureDraw> consumer) {
        this.render(this.getSpriteInstance(), isoObject, n, n2, n3, isoDirections, n4, n5, colorInfo, b, consumer);
    }
    
    public final void render(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final ColorInfo colorInfo, final boolean b) {
        this.render(isoSpriteInstance, isoObject, n, n2, n3, isoDirections, n4, n5, colorInfo, b, null);
    }
    
    public void render(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final ColorInfo colorInfo, final boolean b, final Consumer<TextureDraw> consumer) {
        if (this.hasActiveModel()) {
            this.renderActiveModel();
        }
        else {
            this.renderCurrentAnim(isoSpriteInstance, isoObject, n, n2, n3, isoDirections, n4, n5, colorInfo, b, consumer);
        }
    }
    
    public void renderCurrentAnim(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final ColorInfo colorInfo, final boolean b, final Consumer<TextureDraw> consumer) {
        if (!DebugOptions.instance.IsoSprite.RenderSprites.getValue()) {
            return;
        }
        if (this.CurrentAnim == null || this.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final float currentSpriteFrame = this.getCurrentSpriteFrame(isoSpriteInstance);
        IsoSprite.info.set(colorInfo);
        final Vector3 set = l_renderCurrentAnim.colorInfoBackup.set(IsoSprite.info.r, IsoSprite.info.g, IsoSprite.info.b);
        final Vector2 set2 = l_renderCurrentAnim.spritePos.set(0.0f, 0.0f);
        this.prepareToRenderSprite(isoSpriteInstance, isoObject, n, n2, n3, isoDirections, n4, n5, b, (int)currentSpriteFrame, set2);
        this.performRenderFrame(isoSpriteInstance, isoObject, isoDirections, (int)currentSpriteFrame, set2.x, set2.y, consumer);
        IsoSprite.info.r = set.x;
        IsoSprite.info.g = set.y;
        IsoSprite.info.b = set.z;
    }
    
    private float getCurrentSpriteFrame(final IsoSpriteInstance isoSpriteInstance) {
        if (this.CurrentAnim.FramesArray == null) {
            this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(new IsoDirectionFrame[0]);
        }
        if (this.CurrentAnim.FramesArray.length != this.CurrentAnim.Frames.size()) {
            this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
        }
        float frame;
        if (isoSpriteInstance.Frame >= this.CurrentAnim.Frames.size()) {
            frame = (float)(this.CurrentAnim.FramesArray.length - 1);
        }
        else if (isoSpriteInstance.Frame < 0.0f) {
            isoSpriteInstance.Frame = 0.0f;
            frame = 0.0f;
        }
        else {
            frame = isoSpriteInstance.Frame;
        }
        return frame;
    }
    
    private void prepareToRenderSprite(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final boolean b, final int n6, final Vector2 vector2) {
        if (b) {
            isoSpriteInstance.renderprep(isoObject);
        }
        final float n7 = 0.0f;
        final float n8 = 0.0f;
        if (IsoSprite.globalOffsetX == -1.0f) {
            IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
            IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
        }
        float n11;
        float n12;
        if (isoObject == null || isoObject.sx == 0.0f || isoObject instanceof IsoMovingObject) {
            final float xToScreen = IsoUtils.XToScreen(n + isoSpriteInstance.offX, n2 + isoSpriteInstance.offY, n3 + isoSpriteInstance.offZ, 0);
            final float yToScreen = IsoUtils.YToScreen(n + isoSpriteInstance.offX, n2 + isoSpriteInstance.offY, n3 + isoSpriteInstance.offZ, 0);
            final float sx = xToScreen - n4;
            final float sy = yToScreen - n5;
            if (isoObject != null) {
                isoObject.sx = sx;
                isoObject.sy = sy;
            }
            final float n9 = sx + IsoSprite.globalOffsetX;
            final float n10 = sy + IsoSprite.globalOffsetY;
            n11 = n9 + this.soffX;
            n12 = n10 + this.soffY;
        }
        else if (isoObject != null) {
            final float n13 = isoObject.sx + IsoSprite.globalOffsetX;
            final float n14 = isoObject.sy + IsoSprite.globalOffsetY;
            n11 = n13 + this.soffX;
            n12 = n14 + this.soffY;
        }
        else {
            final float n15 = n7 + IsoSprite.globalOffsetX;
            final float n16 = n8 + IsoSprite.globalOffsetY;
            n11 = n15 + this.soffX;
            n12 = n16 + this.soffY;
        }
        if (isoObject instanceof IsoMovingObject && this.CurrentAnim != null && this.CurrentAnim.FramesArray[n6].getTexture(isoDirections) != null) {
            n11 -= this.CurrentAnim.FramesArray[n6].getTexture(isoDirections).getWidthOrig() / 2 * isoSpriteInstance.getScaleX();
            n12 -= this.CurrentAnim.FramesArray[n6].getTexture(isoDirections).getHeightOrig() * isoSpriteInstance.getScaleY();
        }
        if (b) {
            if (isoSpriteInstance.tintr != 1.0f || isoSpriteInstance.tintg != 1.0f || isoSpriteInstance.tintb != 1.0f) {
                final ColorInfo info = IsoSprite.info;
                info.r *= isoSpriteInstance.tintr;
                final ColorInfo info2 = IsoSprite.info;
                info2.g *= isoSpriteInstance.tintg;
                final ColorInfo info3 = IsoSprite.info;
                info3.b *= isoSpriteInstance.tintb;
            }
            IsoSprite.info.a = isoSpriteInstance.alpha;
            if (isoSpriteInstance.bMultiplyObjectAlpha && isoObject != null) {
                final ColorInfo info4 = IsoSprite.info;
                info4.a *= isoObject.getAlpha(IsoCamera.frameState.playerIndex);
            }
        }
        if (this.TintMod.r != 1.0f || this.TintMod.g != 1.0f || this.TintMod.b != 1.0f) {
            final ColorInfo info5 = IsoSprite.info;
            info5.r *= this.TintMod.r;
            final ColorInfo info6 = IsoSprite.info;
            info6.g *= this.TintMod.g;
            final ColorInfo info7 = IsoSprite.info;
            info7.b *= this.TintMod.b;
        }
        vector2.set(n11, n12);
    }
    
    private void performRenderFrame(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final IsoDirections isoDirections, final int n, float n2, float n3, final Consumer<TextureDraw> consumer) {
        if (n >= this.CurrentAnim.FramesArray.length) {
            return;
        }
        final IsoDirectionFrame isoDirectionFrame = this.CurrentAnim.FramesArray[n];
        final Texture texture = isoDirectionFrame.getTexture(isoDirections);
        if (texture == null) {
            return;
        }
        if (Core.TileScale == 2 && texture.getWidthOrig() == 64 && texture.getHeightOrig() == 128) {
            isoSpriteInstance.setScale(2.0f, 2.0f);
        }
        if (Core.TileScale == 2 && isoSpriteInstance.scaleX == 2.0f && isoSpriteInstance.scaleY == 2.0f && texture.getWidthOrig() == 128 && texture.getHeightOrig() == 256) {
            isoSpriteInstance.setScale(1.0f, 1.0f);
        }
        if (isoSpriteInstance.scaleX <= 0.0f || isoSpriteInstance.scaleY <= 0.0f) {
            return;
        }
        float n4 = (float)texture.getWidth();
        float n5 = (float)texture.getHeight();
        final float scaleX = isoSpriteInstance.scaleX;
        final float scaleY = isoSpriteInstance.scaleY;
        if (scaleX != 1.0f) {
            n2 += texture.getOffsetX() * (scaleX - 1.0f);
            n4 *= scaleX;
        }
        if (scaleY != 1.0f) {
            n3 += texture.getOffsetY() * (scaleY - 1.0f);
            n5 *= scaleY;
        }
        if (DebugOptions.instance.IsoSprite.MovingObjectEdges.getValue() && isoObject instanceof IsoMovingObject) {
            this.renderSpriteOutline(n2, n3, texture, scaleX, scaleY);
        }
        if (DebugOptions.instance.IsoSprite.DropShadowEdges.getValue() && StringUtils.equals(texture.getName(), "dropshadow")) {
            this.renderSpriteOutline(n2, n3, texture, scaleX, scaleY);
        }
        if (!this.hideForWaterRender || !IsoWater.getInstance().getShaderEnable()) {
            if (isoObject != null && isoObject.getObjectRenderEffectsToApply() != null) {
                isoDirectionFrame.render(isoObject.getObjectRenderEffectsToApply(), n2, n3, n4, n5, isoDirections, IsoSprite.info, isoSpriteInstance.Flip, consumer);
            }
            else {
                isoDirectionFrame.render(n2, n3, n4, n5, isoDirections, IsoSprite.info, isoSpriteInstance.Flip, consumer);
            }
        }
        if (n < this.CurrentAnim.FramesArray.length && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0 && isoObject != null) {
            boolean b = isoDirections == IsoDirections.W || isoDirections == IsoDirections.SW || isoDirections == IsoDirections.S;
            if (isoSpriteInstance.Flip) {
                b = !b;
            }
            n2 = isoObject.sx + IsoSprite.globalOffsetX;
            n3 = isoObject.sy + IsoSprite.globalOffsetY;
            if (isoObject instanceof IsoMovingObject) {
                n2 -= texture.getWidthOrig() / 2 * scaleX;
                n3 -= texture.getHeightOrig() * scaleY;
            }
            IsoObjectPicker.Instance.Add((int)n2, (int)n3, (int)(texture.getWidthOrig() * scaleX), (int)(texture.getHeightOrig() * scaleY), isoObject.square, isoObject, b, scaleX, scaleY);
        }
    }
    
    private void renderSpriteOutline(final float n, final float n2, final Texture texture, final float n3, final float n4) {
        LineDrawer.drawRect(n, n2, texture.getWidthOrig() * n3, texture.getHeightOrig() * n4, 1.0f, 1.0f, 1.0f, 1.0f, 1);
        LineDrawer.drawRect(n + texture.getOffsetX() * n3, n2 + texture.getOffsetY() * n4, texture.getWidth() * n3, texture.getHeight() * n4, 1.0f, 1.0f, 1.0f, 1.0f, 1);
    }
    
    public void renderActiveModel() {
        if (!DebugOptions.instance.IsoSprite.RenderModels.getValue()) {
            return;
        }
        this.modelSlot.model.updateLights();
        SpriteRenderer.instance.drawGeneric(ModelCameraRenderData.s_pool.alloc().init(CharacterModelCamera.instance, this.modelSlot));
        SpriteRenderer.instance.drawModel(this.modelSlot);
    }
    
    public void renderBloodSplat(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.CurrentAnim == null || this.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final int n4 = 0;
        final int n5 = 0;
        try {
            if (IsoSprite.globalOffsetX == -1.0f) {
                IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
                IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
            }
            final float xToScreen = IsoUtils.XToScreen(n, n2, n3, 0);
            final float yToScreen = IsoUtils.YToScreen(n, n2, n3, 0);
            final float n6 = (float)(int)xToScreen;
            final float n7 = (float)(int)yToScreen;
            final float n8 = n6 - n4;
            final float n9 = n7 - n5;
            final float n10 = n8 + IsoSprite.globalOffsetX;
            final float n11 = n9 + IsoSprite.globalOffsetY;
            if (n10 >= IsoCamera.frameState.OffscreenWidth || n10 + 64.0f <= 0.0f) {
                return;
            }
            if (n11 >= IsoCamera.frameState.OffscreenHeight || n11 + 64.0f <= 0.0f) {
                return;
            }
            IsoSprite.info.r = colorInfo.r;
            IsoSprite.info.g = colorInfo.g;
            IsoSprite.info.b = colorInfo.b;
            IsoSprite.info.a = colorInfo.a;
            this.CurrentAnim.Frames.get(0).render(n10, n11, IsoDirections.N, IsoSprite.info, false, null);
        }
        catch (Exception thrown) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
        }
    }
    
    public void renderObjectPicker(final IsoSpriteInstance isoSpriteInstance, final IsoObject isoObject, final IsoDirections isoDirections) {
        if (this.CurrentAnim == null) {
            return;
        }
        if (isoSpriteInstance == null) {
            return;
        }
        if (IsoPlayer.getInstance() != IsoPlayer.players[0]) {
            return;
        }
        if (this.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        if (isoSpriteInstance.Frame >= this.CurrentAnim.Frames.size()) {
            isoSpriteInstance.Frame = 0.0f;
        }
        if (this.CurrentAnim.Frames.get((int)isoSpriteInstance.Frame).getTexture(isoDirections) == null) {
            return;
        }
        float n = isoObject.sx + IsoSprite.globalOffsetX;
        float n2 = isoObject.sy + IsoSprite.globalOffsetY;
        if (isoObject instanceof IsoMovingObject) {
            n -= this.CurrentAnim.Frames.get((int)isoSpriteInstance.Frame).getTexture(isoDirections).getWidthOrig() / 2 * isoSpriteInstance.getScaleX();
            n2 -= this.CurrentAnim.Frames.get((int)isoSpriteInstance.Frame).getTexture(isoDirections).getHeightOrig() * isoSpriteInstance.getScaleY();
        }
        if (isoSpriteInstance.Frame < this.CurrentAnim.Frames.size() && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
            final Texture texture = this.CurrentAnim.Frames.get((int)isoSpriteInstance.Frame).getTexture(isoDirections);
            boolean b = isoDirections == IsoDirections.W || isoDirections == IsoDirections.SW || isoDirections == IsoDirections.S;
            if (isoSpriteInstance.Flip) {
                b = !b;
            }
            IsoObjectPicker.Instance.Add((int)n, (int)n2, (int)(texture.getWidthOrig() * isoSpriteInstance.getScaleX()), (int)(texture.getHeightOrig() * isoSpriteInstance.getScaleY()), isoObject.square, isoObject, b, isoSpriteInstance.getScaleX(), isoSpriteInstance.getScaleY());
        }
    }
    
    public Texture getTextureForFrame(int n, final IsoDirections isoDirections) {
        if (this.CurrentAnim == null || this.CurrentAnim.Frames.isEmpty()) {
            return null;
        }
        if (this.CurrentAnim.FramesArray == null) {
            this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(new IsoDirectionFrame[0]);
        }
        if (this.CurrentAnim.FramesArray.length != this.CurrentAnim.Frames.size()) {
            this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
        }
        if (n >= this.CurrentAnim.FramesArray.length) {
            n = this.CurrentAnim.FramesArray.length - 1;
        }
        if (n < 0) {
            n = 0;
        }
        return this.CurrentAnim.FramesArray[n].getTexture(isoDirections);
    }
    
    public Texture getTextureForCurrentFrame(final IsoDirections isoDirections) {
        this.initSpriteInstance();
        return this.getTextureForFrame((int)this.def.Frame, isoDirections);
    }
    
    public void update() {
        this.update(this.def);
    }
    
    public void update(IsoSpriteInstance value) {
        if (value == null) {
            value = IsoSpriteInstance.get(this);
        }
        if (this.CurrentAnim == null) {
            return;
        }
        if (this.Animate && !value.Finished) {
            final float frame = value.Frame;
            if (!GameTime.isGamePaused()) {
                final IsoSpriteInstance isoSpriteInstance = value;
                isoSpriteInstance.Frame += value.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f);
            }
            if ((int)value.Frame >= this.CurrentAnim.Frames.size() && this.Loop && value.Looped) {
                value.Frame = 0.0f;
            }
            if ((int)frame != (int)value.Frame) {
                value.NextFrame = true;
            }
            if ((int)value.Frame >= this.CurrentAnim.Frames.size() && (!this.Loop || !value.Looped)) {
                value.Finished = true;
                value.Frame = this.CurrentAnim.FinishUnloopedOnFrame;
                if (this.DeleteWhenFinished) {
                    this.Dispose();
                    this.Animate = false;
                }
            }
        }
    }
    
    public void CacheAnims(final String s) {
        this.name = s;
        final Stack<String> stack = new Stack<String>();
        for (int i = 0; i < this.AnimStack.size(); ++i) {
            final IsoAnim value = this.AnimStack.get(i);
            final String key = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, value.name);
            stack.add(key);
            if (!IsoAnim.GlobalAnimMap.containsKey(key)) {
                IsoAnim.GlobalAnimMap.put(key, value);
            }
        }
        IsoSprite.AnimNameSet.put(s, stack.toArray());
    }
    
    public void LoadCache(final String s) {
        final Object[] array = IsoSprite.AnimNameSet.get(s);
        this.name = s;
        for (int i = 0; i < array.length; ++i) {
            final IsoAnim currentAnim = IsoAnim.GlobalAnimMap.get(array[i]);
            this.AnimMap.put(currentAnim.name, currentAnim);
            this.AnimStack.add(currentAnim);
            this.CurrentAnim = currentAnim;
        }
    }
    
    public IsoSprite setFromCache(final String s, final String s2, final int n) {
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
        if (HasCache(s3)) {
            this.LoadCache(s3);
        }
        else {
            this.LoadFramesNoDirPage(s, s2, n);
            this.CacheAnims(s3);
        }
        return this;
    }
    
    public IsoObjectType getType() {
        return this.type;
    }
    
    public void setType(final IsoObjectType type) {
        this.type = type;
    }
    
    public void AddProperties(final IsoSprite isoSprite) {
        this.getProperties().AddProperties(isoSprite.getProperties());
    }
    
    public int getID() {
        return this.ID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ColorInfo getTintMod() {
        return this.TintMod;
    }
    
    public void setTintMod(final ColorInfo colorInfo) {
        this.TintMod.set(colorInfo);
    }
    
    public void setAnimate(final boolean animate) {
        this.Animate = animate;
    }
    
    public IsoSpriteGrid getSpriteGrid() {
        return this.spriteGrid;
    }
    
    public void setSpriteGrid(final IsoSpriteGrid spriteGrid) {
        this.spriteGrid = spriteGrid;
    }
    
    public boolean isMoveWithWind() {
        return this.moveWithWind;
    }
    
    public int getSheetGridIdFromName() {
        if (this.name != null) {
            return getSheetGridIdFromName(this.name);
        }
        return -1;
    }
    
    public static int getSheetGridIdFromName(final String s) {
        if (s != null) {
            final int lastIndex = s.lastIndexOf(95);
            if (lastIndex > 0 && lastIndex + 1 < s.length()) {
                return Integer.parseInt(s.substring(lastIndex + 1));
            }
        }
        return -1;
    }
    
    static {
        IsoSprite.maxCount = 0;
        IsoSprite.alphaStep = 0.05f;
        IsoSprite.globalOffsetX = -1.0f;
        IsoSprite.globalOffsetY = -1.0f;
        info = new ColorInfo();
        AnimNameSet = new HashMap<String, Object[]>();
    }
    
    private static class l_renderCurrentAnim
    {
        static final Vector3 colorInfoBackup;
        static final Vector2 spritePos;
        
        static {
            colorInfoBackup = new Vector3();
            spritePos = new Vector2();
        }
    }
}
