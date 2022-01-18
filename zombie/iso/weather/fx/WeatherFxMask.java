// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import java.util.Iterator;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import java.util.ArrayList;
import zombie.input.GameKeyboard;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.opengl.RenderSettings;
import zombie.IndieGL;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.Lua.LuaManager;
import zombie.debug.DebugOptions;
import zombie.iso.IsoObject;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunkMap;
import zombie.characters.IsoPlayer;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.IsoUtils;
import zombie.iso.IsoCamera;
import zombie.interfaces.ITexture;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.characters.IsoGameCharacter;
import org.joml.Vector3f;
import org.joml.Vector2i;
import zombie.iso.DiamondMatrixIterator;
import zombie.iso.IsoGridSquare;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSprite;
import zombie.core.textures.TextureFBO;

public class WeatherFxMask
{
    private static boolean DEBUG_KEYS;
    private static TextureFBO fboMask;
    private static TextureFBO fboParticles;
    public static IsoSprite floorSprite;
    public static IsoSprite wallNSprite;
    public static IsoSprite wallWSprite;
    public static IsoSprite wallNWSprite;
    public static IsoSprite wallSESprite;
    private static Texture texWhite;
    private static int curPlayerIndex;
    public static final int BIT_FLOOR = 0;
    public static final int BIT_WALLN = 1;
    public static final int BIT_WALLW = 2;
    public static final int BIT_IS_CUT = 4;
    public static final int BIT_CHARS = 8;
    public static final int BIT_OBJECTS = 16;
    public static final int BIT_WALL_SE = 32;
    public static final int BIT_DOOR = 64;
    public static float offsetX;
    public static float offsetY;
    public static ColorInfo defColorInfo;
    private static int DIAMOND_ROWS;
    public int x;
    public int y;
    public int z;
    public int flags;
    public IsoGridSquare gs;
    public boolean enabled;
    private static PlayerFxMask[] playerMasks;
    private static DiamondMatrixIterator dmiter;
    private static final Vector2i diamondMatrixPos;
    private static Vector3f tmpVec;
    private static IsoGameCharacter.TorchInfo tmpTorch;
    private static ColorInfo tmpColInfo;
    private static int[] test;
    private static String[] testNames;
    private static int var1;
    private static int var2;
    private static float var3;
    private static int SCR_MASK_ADD;
    private static int DST_MASK_ADD;
    private static int SCR_MASK_SUB;
    private static int DST_MASK_SUB;
    private static int SCR_PARTICLES;
    private static int DST_PARTICLES;
    private static int SCR_MERGE;
    private static int DST_MERGE;
    private static int SCR_FINAL;
    private static int DST_FINAL;
    private static int ID_SCR_MASK_ADD;
    private static int ID_DST_MASK_ADD;
    private static int ID_SCR_MASK_SUB;
    private static int ID_DST_MASK_SUB;
    private static int ID_SCR_MERGE;
    private static int ID_DST_MERGE;
    private static int ID_SCR_FINAL;
    private static int ID_DST_FINAL;
    private static int ID_SCR_PARTICLES;
    private static int ID_DST_PARTICLES;
    private static int TARGET_BLEND;
    private static boolean DEBUG_MASK;
    public static boolean MASKING_ENABLED;
    private static boolean DEBUG_MASK_AND_PARTICLES;
    private static final boolean DEBUG_THROTTLE_KEYS = true;
    private static int keypause;
    
    public static TextureFBO getFboMask() {
        return WeatherFxMask.fboMask;
    }
    
    public static TextureFBO getFboParticles() {
        return WeatherFxMask.fboParticles;
    }
    
    public static void init() throws Exception {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < WeatherFxMask.playerMasks.length; ++i) {
            WeatherFxMask.playerMasks[i] = new PlayerFxMask();
        }
        WeatherFxMask.playerMasks[0].init();
        initGlIds();
        WeatherFxMask.floorSprite = IsoSpriteManager.instance.getSprite("floors_interior_tilesandwood_01_16");
        WeatherFxMask.wallNSprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_21");
        WeatherFxMask.wallWSprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_20");
        WeatherFxMask.wallNWSprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_22");
        WeatherFxMask.wallSESprite = IsoSpriteManager.instance.getSprite("walls_interior_house_01_23");
        WeatherFxMask.texWhite = Texture.getSharedTexture("media/textures/weather/fogwhite.png");
    }
    
    public static boolean checkFbos() {
        if (GameServer.bServer) {
            return false;
        }
        final TextureFBO offscreenBuffer = Core.getInstance().getOffscreenBuffer();
        if (Core.getInstance().getOffscreenBuffer() == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, offscreenBuffer != null));
            return false;
        }
        final int screenWidth = Core.getInstance().getScreenWidth();
        final int screenHeight = Core.getInstance().getScreenHeight();
        if (WeatherFxMask.fboMask == null || WeatherFxMask.fboParticles == null || WeatherFxMask.fboMask.getTexture().getWidth() != screenWidth || WeatherFxMask.fboMask.getTexture().getHeight() != screenHeight) {
            if (WeatherFxMask.fboMask != null) {
                WeatherFxMask.fboMask.destroy();
            }
            if (WeatherFxMask.fboParticles != null) {
                WeatherFxMask.fboParticles.destroy();
            }
            WeatherFxMask.fboMask = null;
            WeatherFxMask.fboParticles = null;
            try {
                WeatherFxMask.fboMask = new TextureFBO(new Texture(screenWidth, screenHeight, 16));
            }
            catch (Exception ex) {
                DebugLog.log(ex.getStackTrace());
                ex.printStackTrace();
            }
            try {
                WeatherFxMask.fboParticles = new TextureFBO(new Texture(screenWidth, screenHeight, 16));
            }
            catch (Exception ex2) {
                DebugLog.log(ex2.getStackTrace());
                ex2.printStackTrace();
            }
            return WeatherFxMask.fboMask != null && WeatherFxMask.fboParticles != null;
        }
        return WeatherFxMask.fboMask != null && WeatherFxMask.fboParticles != null;
    }
    
    public static void destroy() {
        if (WeatherFxMask.fboMask != null) {
            WeatherFxMask.fboMask.destroy();
        }
        WeatherFxMask.fboMask = null;
        if (WeatherFxMask.fboParticles != null) {
            WeatherFxMask.fboParticles.destroy();
        }
        WeatherFxMask.fboParticles = null;
    }
    
    public static void initMask() {
        if (GameServer.bServer) {
            return;
        }
        WeatherFxMask.curPlayerIndex = IsoCamera.frameState.playerIndex;
        WeatherFxMask.playerMasks[WeatherFxMask.curPlayerIndex].initMask();
    }
    
    private static boolean isOnScreen(final int n, final int n2, final int n3) {
        final float n4 = (float)(int)IsoUtils.XToScreenInt(n, n2, n3, 0);
        final float n5 = (float)(int)IsoUtils.YToScreenInt(n, n2, n3, 0);
        final float n6 = n4 - (int)IsoCamera.frameState.OffX;
        final float n7 = n5 - (int)IsoCamera.frameState.OffY;
        return n6 + 32 * Core.TileScale > 0.0f && n7 + 32 * Core.TileScale > 0.0f && n6 - 32 * Core.TileScale < IsoCamera.frameState.OffscreenWidth && n7 - 96 * Core.TileScale < IsoCamera.frameState.OffscreenHeight;
    }
    
    public boolean isLoc(final int n, final int n2, final int n3) {
        return this.x == n && this.y == n2 && this.z == n3;
    }
    
    public static boolean playerHasMaskToDraw(final int n) {
        return n < WeatherFxMask.playerMasks.length && WeatherFxMask.playerMasks[n].hasMaskToDraw;
    }
    
    public static void setDiamondIterDone(final int n) {
        if (n < WeatherFxMask.playerMasks.length) {
            WeatherFxMask.playerMasks[n].DIAMOND_ITER_DONE = true;
        }
    }
    
    public static void forceMaskUpdate(final int n) {
        if (n < WeatherFxMask.playerMasks.length) {
            WeatherFxMask.playerMasks[n].plrSquare = null;
        }
    }
    
    public static void forceMaskUpdateAll() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < WeatherFxMask.playerMasks.length; ++i) {
            WeatherFxMask.playerMasks[i].plrSquare = null;
        }
    }
    
    private static boolean getIsStairs(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && (isoGridSquare.Has(IsoObjectType.stairsBN) || isoGridSquare.Has(IsoObjectType.stairsBW) || isoGridSquare.Has(IsoObjectType.stairsMN) || isoGridSquare.Has(IsoObjectType.stairsMW) || isoGridSquare.Has(IsoObjectType.stairsTN) || isoGridSquare.Has(IsoObjectType.stairsTW));
    }
    
    private static boolean getHasDoor(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && (isoGridSquare.Is(IsoFlagType.cutN) || isoGridSquare.Is(IsoFlagType.cutW)) && (isoGridSquare.Is(IsoFlagType.DoorWallN) || isoGridSquare.Is(IsoFlagType.DoorWallW)) && !isoGridSquare.Is(IsoFlagType.doorN) && !isoGridSquare.Is(IsoFlagType.doorW) && isoGridSquare.getCanSee(WeatherFxMask.curPlayerIndex);
    }
    
    public static void addMaskLocation(final IsoGridSquare isoGridSquare, final int n, final int n2, final int n3) {
        if (GameServer.bServer) {
            return;
        }
        final PlayerFxMask playerFxMask = WeatherFxMask.playerMasks[WeatherFxMask.curPlayerIndex];
        if (!playerFxMask.requiresUpdate) {
            return;
        }
        if (!playerFxMask.hasMaskToDraw || playerFxMask.playerZ != n3) {
            return;
        }
        if (isInPlayerBuilding(isoGridSquare, n, n2, n3)) {
            final boolean b = !isInPlayerBuilding(IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n, n2 - 1, n3), n, n2 - 1, n3);
            final boolean b2 = !isInPlayerBuilding(IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n - 1, n2, n3), n - 1, n2, n3);
            final boolean b3 = !isInPlayerBuilding(IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n - 1, n2 - 1, n3), n - 1, n2 - 1, n3);
            int n4 = 0;
            if (b) {
                n4 |= 0x1;
            }
            if (b2) {
                n4 |= 0x2;
            }
            if (b3) {
                n4 |= 0x20;
            }
            boolean b4 = false;
            final boolean isStairs = getIsStairs(isoGridSquare);
            if (isoGridSquare != null && (b || b2 || b3)) {
                final int n5 = 24;
                if (b && !isoGridSquare.getProperties().Is(IsoFlagType.WallN) && !isoGridSquare.Is(IsoFlagType.WallNW)) {
                    playerFxMask.addMask(n - 1, n2, n3, null, 8, false);
                    playerFxMask.addMask(n, n2, n3, isoGridSquare, n5);
                    playerFxMask.addMask(n + 1, n2, n3, null, n5, false);
                    playerFxMask.addMask(n + 2, n2, n3, null, 8, false);
                    playerFxMask.addMask(n, n2 + 1, n3, null, 8, false);
                    playerFxMask.addMask(n + 1, n2 + 1, n3, null, n5, false);
                    playerFxMask.addMask(n + 2, n2 + 1, n3, null, n5, false);
                    playerFxMask.addMask(n + 2, n2 + 2, n3, null, 16, false);
                    playerFxMask.addMask(n + 3, n2 + 2, n3, null, 16, false);
                    b4 = true;
                }
                if (b2 && !isoGridSquare.getProperties().Is(IsoFlagType.WallW) && !isoGridSquare.getProperties().Is(IsoFlagType.WallNW)) {
                    playerFxMask.addMask(n, n2 - 1, n3, null, 8, false);
                    playerFxMask.addMask(n, n2, n3, isoGridSquare, n5);
                    playerFxMask.addMask(n, n2 + 1, n3, null, n5, false);
                    playerFxMask.addMask(n, n2 + 2, n3, null, 8, false);
                    playerFxMask.addMask(n + 1, n2, n3, null, 8, false);
                    playerFxMask.addMask(n + 1, n2 + 1, n3, null, n5, false);
                    playerFxMask.addMask(n + 1, n2 + 2, n3, null, n5, false);
                    playerFxMask.addMask(n + 2, n2 + 2, n3, null, 16, false);
                    playerFxMask.addMask(n + 2, n2 + 3, n3, null, 16, false);
                    b4 = true;
                }
                if (b3) {
                    playerFxMask.addMask(n, n2, n3, isoGridSquare, isStairs ? n5 : n4);
                    b4 = true;
                }
            }
            if (!b4) {
                playerFxMask.addMask(n, n2, n3, isoGridSquare, isStairs ? 24 : n4);
            }
        }
        else {
            final boolean inPlayerBuilding = isInPlayerBuilding(IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n, n2 - 1, n3), n, n2 - 1, n3);
            final boolean inPlayerBuilding2 = isInPlayerBuilding(IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n - 1, n2, n3), n - 1, n2, n3);
            if (inPlayerBuilding || inPlayerBuilding2) {
                int n6 = 4;
                if (inPlayerBuilding) {
                    n6 |= 0x1;
                }
                if (inPlayerBuilding2) {
                    n6 |= 0x2;
                }
                if (getHasDoor(isoGridSquare)) {
                    n6 |= 0x40;
                }
                playerFxMask.addMask(n, n2, n3, isoGridSquare, n6);
            }
            else if (isInPlayerBuilding(IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n - 1, n2 - 1, n3), n - 1, n2 - 1, n3)) {
                playerFxMask.addMask(n, n2, n3, isoGridSquare, 4);
            }
        }
    }
    
    private static boolean isInPlayerBuilding(final IsoGridSquare isoGridSquare, final int n, final int n2, final int n3) {
        final PlayerFxMask playerFxMask = WeatherFxMask.playerMasks[WeatherFxMask.curPlayerIndex];
        if (isoGridSquare != null && isoGridSquare.Is(IsoFlagType.solidfloor)) {
            if (isoGridSquare.getBuilding() != null && isoGridSquare.getBuilding() == playerFxMask.player.getBuilding()) {
                return true;
            }
            if (isoGridSquare.getBuilding() == null) {
                return playerFxMask.curIsoWorldRegion != null && isoGridSquare.getIsoWorldRegion() != null && isoGridSquare.getIsoWorldRegion().isFogMask() && (isoGridSquare.getIsoWorldRegion() == playerFxMask.curIsoWorldRegion || playerFxMask.curConnectedRegions.contains(isoGridSquare.getIsoWorldRegion()));
            }
        }
        else {
            if (isInteriorLocation(n, n2, n3)) {
                return true;
            }
            if (isoGridSquare != null && isoGridSquare.getBuilding() == null) {
                return playerFxMask.curIsoWorldRegion != null && isoGridSquare.getIsoWorldRegion() != null && isoGridSquare.getIsoWorldRegion().isFogMask() && (isoGridSquare.getIsoWorldRegion() == playerFxMask.curIsoWorldRegion || playerFxMask.curConnectedRegions.contains(isoGridSquare.getIsoWorldRegion()));
            }
            if (isoGridSquare == null && playerFxMask.curIsoWorldRegion != null) {
                final IWorldRegion isoWorldRegion = IsoRegions.getIsoWorldRegion(n, n2, n3);
                return isoWorldRegion != null && isoWorldRegion.isFogMask() && (isoWorldRegion == playerFxMask.curIsoWorldRegion || playerFxMask.curConnectedRegions.contains(isoWorldRegion));
            }
        }
        return false;
    }
    
    private static boolean isInteriorLocation(final int n, final int n2, final int n3) {
        final PlayerFxMask playerFxMask = WeatherFxMask.playerMasks[WeatherFxMask.curPlayerIndex];
        for (int i = n3; i >= 0; --i) {
            final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getChunkMap(WeatherFxMask.curPlayerIndex).getGridSquare(n, n2, i);
            if (gridSquare != null) {
                if (gridSquare.getBuilding() != null && gridSquare.getBuilding() == playerFxMask.player.getBuilding()) {
                    return true;
                }
                if (gridSquare.Is(IsoFlagType.exterior)) {
                    return false;
                }
            }
        }
        return false;
    }
    
    private static void scanForTiles(final int n) {
        if (WeatherFxMask.playerMasks[WeatherFxMask.curPlayerIndex].DIAMOND_ITER_DONE) {
            return;
        }
        final int n2 = (int)IsoPlayer.players[n].getZ();
        final int n3 = 0;
        final int n4 = 0;
        final int n5 = n3 + IsoCamera.getOffscreenWidth(n);
        final int n6 = n4 + IsoCamera.getOffscreenHeight(n);
        final float xToIso = IsoUtils.XToIso((float)n3, (float)n4, 0.0f);
        final float yToIso = IsoUtils.YToIso((float)n5, (float)n4, 0.0f);
        final float xToIso2 = IsoUtils.XToIso((float)n5, (float)n6, 6.0f);
        final float yToIso2 = IsoUtils.YToIso((float)n3, (float)n6, 6.0f);
        final float xToIso3 = IsoUtils.XToIso((float)n5, (float)n4, 0.0f);
        int n7 = (int)yToIso;
        final int n8 = (int)yToIso2;
        int n9 = (int)xToIso;
        final int n10 = (int)xToIso2;
        WeatherFxMask.DIAMOND_ROWS = (int)xToIso3 * 4;
        n9 -= 2;
        n7 -= 2;
        WeatherFxMask.dmiter.reset(n10 - n9);
        final Vector2i diamondMatrixPos = WeatherFxMask.diamondMatrixPos;
        final IsoChunkMap chunkMap = IsoWorld.instance.getCell().getChunkMap(n);
        while (WeatherFxMask.dmiter.next(diamondMatrixPos)) {
            if (diamondMatrixPos == null) {
                continue;
            }
            final IsoGridSquare gridSquare = chunkMap.getGridSquare(diamondMatrixPos.x + n9, diamondMatrixPos.y + n7, n2);
            if (gridSquare == null) {
                addMaskLocation(null, diamondMatrixPos.x + n9, diamondMatrixPos.y + n7, n2);
            }
            else {
                if (gridSquare.getChunk() == null || !gridSquare.IsOnScreen()) {
                    continue;
                }
                addMaskLocation(gridSquare, diamondMatrixPos.x + n9, diamondMatrixPos.y + n7, n2);
            }
        }
    }
    
    private static void renderMaskFloor(final int n, final int n2, final int n3) {
        WeatherFxMask.floorSprite.render(null, (float)n, (float)n2, (float)n3, IsoDirections.N, WeatherFxMask.offsetX, WeatherFxMask.offsetY, WeatherFxMask.defColorInfo, false);
    }
    
    private static void renderMaskWall(final IsoGridSquare isoGridSquare, final int n, final int n2, final int n3, final boolean b, final boolean b2, final int n4) {
        if (isoGridSquare == null) {
            return;
        }
        final IsoGridSquare isoGridSquare2 = isoGridSquare.nav[IsoDirections.S.index()];
        final IsoGridSquare isoGridSquare3 = isoGridSquare.nav[IsoDirections.E.index()];
        final long currentTimeMillis = System.currentTimeMillis();
        final boolean b3 = isoGridSquare2 != null && isoGridSquare2.getPlayerCutawayFlag(n4, currentTimeMillis);
        final boolean playerCutawayFlag = isoGridSquare.getPlayerCutawayFlag(n4, currentTimeMillis);
        final boolean b4 = isoGridSquare3 != null && isoGridSquare3.getPlayerCutawayFlag(n4, currentTimeMillis);
        IsoSprite isoSprite;
        IsoDirections isoDirections;
        if (b && b2) {
            isoSprite = WeatherFxMask.wallNWSprite;
            isoDirections = IsoDirections.NW;
        }
        else if (b) {
            isoSprite = WeatherFxMask.wallNSprite;
            isoDirections = IsoDirections.N;
        }
        else if (b2) {
            isoSprite = WeatherFxMask.wallWSprite;
            isoDirections = IsoDirections.W;
        }
        else {
            isoSprite = WeatherFxMask.wallSESprite;
            isoDirections = IsoDirections.W;
        }
        isoGridSquare.DoCutawayShaderSprite(isoSprite, isoDirections, b3, playerCutawayFlag, b4);
    }
    
    private static void renderMaskWallNoCuts(final int n, final int n2, final int n3, final boolean b, final boolean b2) {
        if (b && b2) {
            WeatherFxMask.wallNWSprite.render(null, (float)n, (float)n2, (float)n3, IsoDirections.N, WeatherFxMask.offsetX, WeatherFxMask.offsetY, WeatherFxMask.defColorInfo, false);
        }
        else if (b) {
            WeatherFxMask.wallNSprite.render(null, (float)n, (float)n2, (float)n3, IsoDirections.N, WeatherFxMask.offsetX, WeatherFxMask.offsetY, WeatherFxMask.defColorInfo, false);
        }
        else if (b2) {
            WeatherFxMask.wallWSprite.render(null, (float)n, (float)n2, (float)n3, IsoDirections.N, WeatherFxMask.offsetX, WeatherFxMask.offsetY, WeatherFxMask.defColorInfo, false);
        }
        else {
            WeatherFxMask.wallSESprite.render(null, (float)n, (float)n2, (float)n3, IsoDirections.N, WeatherFxMask.offsetX, WeatherFxMask.offsetY, WeatherFxMask.defColorInfo, false);
        }
    }
    
    public static void renderFxMask(final int n) {
        if (!DebugOptions.instance.Weather.Fx.getValue()) {
            return;
        }
        if (GameServer.bServer) {
            return;
        }
        if (IsoWeatherFX.instance == null) {
            return;
        }
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            return;
        }
        if (WeatherFxMask.DEBUG_KEYS && Core.bDebug) {
            updateDebugKeys();
        }
        if (!WeatherFxMask.playerMasks[n].maskEnabled) {
            return;
        }
        final PlayerFxMask playerFxMask = WeatherFxMask.playerMasks[WeatherFxMask.curPlayerIndex];
        if (!playerFxMask.maskEnabled) {
            return;
        }
        if (WeatherFxMask.MASKING_ENABLED && !checkFbos()) {
            WeatherFxMask.MASKING_ENABLED = false;
        }
        if (!WeatherFxMask.MASKING_ENABLED || !playerFxMask.hasMaskToDraw) {
            if (IsoWorld.instance.getCell() != null && IsoWorld.instance.getCell().getWeatherFX() != null) {
                SpriteRenderer.instance.glIgnoreStyles(true);
                SpriteRenderer.instance.glBlendFunc(770, 771);
                IsoWorld.instance.getCell().getWeatherFX().render();
                SpriteRenderer.instance.glIgnoreStyles(false);
            }
            return;
        }
        scanForTiles(n);
        IsoCamera.getOffscreenLeft(n);
        IsoCamera.getOffscreenTop(n);
        final int offscreenWidth = IsoCamera.getOffscreenWidth(n);
        final int offscreenHeight = IsoCamera.getOffscreenHeight(n);
        IsoCamera.getScreenWidth(n);
        IsoCamera.getScreenHeight(n);
        SpriteRenderer.instance.glIgnoreStyles(true);
        if (WeatherFxMask.MASKING_ENABLED) {
            SpriteRenderer.instance.glBuffer(4, n);
            SpriteRenderer.instance.glDoStartFrameFx(offscreenWidth, offscreenHeight, n);
            if (PerformanceSettings.LightingFrameSkip < 3) {
                IsoWorld.instance.getCell().DrawStencilMask();
                SpriteRenderer.instance.glClearColor(0, 0, 0, 0);
                SpriteRenderer.instance.glClear(16640);
                SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
            }
            final WeatherFxMask[] masks = WeatherFxMask.playerMasks[n].masks;
            for (int maskPointer = WeatherFxMask.playerMasks[n].maskPointer, i = 0; i < maskPointer; ++i) {
                final WeatherFxMask weatherFxMask = masks[i];
                if (weatherFxMask.enabled) {
                    if ((weatherFxMask.flags & 0x4) == 0x4) {
                        SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                        SpriteRenderer.instance.glBlendFunc(WeatherFxMask.SCR_MASK_SUB, WeatherFxMask.DST_MASK_SUB);
                        SpriteRenderer.instance.glBlendEquation(32779);
                        IndieGL.enableAlphaTest();
                        IndieGL.glAlphaFunc(516, 0.02f);
                        SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                        renderMaskWall(weatherFxMask.gs, weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, (weatherFxMask.flags & 0x1) == 0x1, (weatherFxMask.flags & 0x2) == 0x2, n);
                        SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                        SpriteRenderer.instance.glBlendEquation(32774);
                        SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                        if ((weatherFxMask.flags & 0x40) == 0x40 && weatherFxMask.gs != null) {
                            SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                            SpriteRenderer.instance.glBlendFunc(WeatherFxMask.SCR_MASK_ADD, WeatherFxMask.DST_MASK_ADD);
                            SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                            weatherFxMask.gs.RenderOpenDoorOnly();
                        }
                    }
                    else {
                        SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                        SpriteRenderer.instance.glBlendFunc(WeatherFxMask.SCR_MASK_ADD, WeatherFxMask.DST_MASK_ADD);
                        SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                        renderMaskFloor(weatherFxMask.x, weatherFxMask.y, weatherFxMask.z);
                        final boolean b = (weatherFxMask.flags & 0x10) == 0x10;
                        final boolean b2 = (weatherFxMask.flags & 0x8) == 0x8;
                        if (!b) {
                            final boolean b3 = (weatherFxMask.flags & 0x1) == 0x1;
                            final boolean b4 = (weatherFxMask.flags & 0x2) == 0x2;
                            if (b3 || b4) {
                                renderMaskWall(weatherFxMask.gs, weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, b3, b4, n);
                            }
                            else if ((weatherFxMask.flags & 0x20) == 0x20) {
                                renderMaskWall(weatherFxMask.gs, weatherFxMask.x, weatherFxMask.y, weatherFxMask.z, false, false, n);
                            }
                        }
                        if (b && weatherFxMask.gs != null) {
                            weatherFxMask.gs.RenderMinusFloorFxMask(weatherFxMask.z + 1, false, false);
                        }
                        if (b2 && weatherFxMask.gs != null) {
                            weatherFxMask.gs.renderCharacters(weatherFxMask.z + 1, false, false);
                            SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
                            SpriteRenderer.instance.glBlendFunc(WeatherFxMask.SCR_MASK_ADD, WeatherFxMask.DST_MASK_ADD);
                            SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
                        }
                    }
                }
            }
            SpriteRenderer.instance.glBlendFunc(770, 771);
            SpriteRenderer.instance.glBuffer(5, n);
            SpriteRenderer.instance.glDoEndFrameFx(n);
        }
        if (WeatherFxMask.DEBUG_MASK_AND_PARTICLES) {
            SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
            SpriteRenderer.instance.glClear(16640);
            SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
        }
        else if (WeatherFxMask.DEBUG_MASK) {
            SpriteRenderer.instance.glClearColor(0, 255, 0, 255);
            SpriteRenderer.instance.glClear(16640);
            SpriteRenderer.instance.glClearColor(0, 0, 0, 255);
        }
        if (!RenderSettings.getInstance().getPlayerSettings(n).isExterior()) {
            drawFxLayered(n, false, false, false);
        }
        if (IsoWeatherFX.instance.hasCloudsToRender()) {
            drawFxLayered(n, true, false, false);
        }
        if (IsoWeatherFX.instance.hasFogToRender() && PerformanceSettings.FogQuality == 2) {
            drawFxLayered(n, false, true, false);
        }
        if (Core.OptionRenderPrecipitation == 1 && IsoWeatherFX.instance.hasPrecipitationToRender()) {
            drawFxLayered(n, false, false, true);
        }
        SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
        SpriteRenderer.instance.glIgnoreStyles(false);
    }
    
    private static void drawFxLayered(final int n, final boolean b, final boolean b2, final boolean b3) {
        IsoCamera.getOffscreenLeft(n);
        IsoCamera.getOffscreenTop(n);
        final int offscreenWidth = IsoCamera.getOffscreenWidth(n);
        final int offscreenHeight = IsoCamera.getOffscreenHeight(n);
        final int screenLeft = IsoCamera.getScreenLeft(n);
        final int screenTop = IsoCamera.getScreenTop(n);
        final int screenWidth = IsoCamera.getScreenWidth(n);
        final int screenHeight = IsoCamera.getScreenHeight(n);
        SpriteRenderer.instance.glBuffer(6, n);
        SpriteRenderer.instance.glDoStartFrameFx(offscreenWidth, offscreenHeight, n);
        if (!b && !b2 && !b3) {
            final Color maskClearColorForPlayer = RenderSettings.getInstance().getMaskClearColorForPlayer(n);
            SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
            SpriteRenderer.instance.glBlendFuncSeparate(WeatherFxMask.SCR_PARTICLES, WeatherFxMask.DST_PARTICLES, 1, 771);
            SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
            SpriteRenderer.instance.renderi(WeatherFxMask.texWhite, 0, 0, offscreenWidth, offscreenHeight, maskClearColorForPlayer.r, maskClearColorForPlayer.g, maskClearColorForPlayer.b, maskClearColorForPlayer.a, null);
            SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
        }
        else if (IsoWorld.instance.getCell() != null && IsoWorld.instance.getCell().getWeatherFX() != null) {
            SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
            SpriteRenderer.instance.glBlendFuncSeparate(WeatherFxMask.SCR_PARTICLES, WeatherFxMask.DST_PARTICLES, 1, 771);
            SpriteRenderer.GL_BLENDFUNC_ENABLED = false;
            IsoWorld.instance.getCell().getWeatherFX().renderLayered(b, b2, b3);
            SpriteRenderer.GL_BLENDFUNC_ENABLED = true;
        }
        if (WeatherFxMask.MASKING_ENABLED) {
            SpriteRenderer.instance.glBlendFunc(WeatherFxMask.SCR_MERGE, WeatherFxMask.DST_MERGE);
            SpriteRenderer.instance.glBlendEquation(32779);
            ((Texture)WeatherFxMask.fboMask.getTexture()).rendershader2(0.0f, 0.0f, (float)offscreenWidth, (float)offscreenHeight, screenLeft, screenTop, screenWidth, screenHeight, 1.0f, 1.0f, 1.0f, 1.0f);
            SpriteRenderer.instance.glBlendEquation(32774);
        }
        SpriteRenderer.instance.glBlendFunc(770, 771);
        SpriteRenderer.instance.glBuffer(7, n);
        SpriteRenderer.instance.glDoEndFrameFx(n);
        Texture texture;
        if ((!WeatherFxMask.DEBUG_MASK && !WeatherFxMask.DEBUG_MASK_AND_PARTICLES) || WeatherFxMask.DEBUG_MASK_AND_PARTICLES) {
            texture = (Texture)WeatherFxMask.fboParticles.getTexture();
            SpriteRenderer.instance.glBlendFunc(WeatherFxMask.SCR_FINAL, WeatherFxMask.DST_FINAL);
        }
        else {
            texture = (Texture)WeatherFxMask.fboMask.getTexture();
            SpriteRenderer.instance.glBlendFunc(770, 771);
        }
        final float n2 = 1.0f;
        final float n3 = 1.0f;
        final float n4 = 1.0f;
        final float n5 = 1.0f;
        final int n6 = screenLeft;
        final int n7 = screenTop;
        final int n8 = screenWidth;
        final int n9 = screenHeight;
        final float n10 = n6 / (float)texture.getWidthHW();
        final float n11 = n7 / (float)texture.getHeightHW();
        final float n12 = (n6 + n8) / (float)texture.getWidthHW();
        final float n13 = (n7 + n9) / (float)texture.getHeightHW();
        SpriteRenderer.instance.render(texture, 0.0f, 0.0f, (float)offscreenWidth, (float)offscreenHeight, n2, n3, n4, n5, n10, n13, n12, n13, n12, n11, n10, n11);
    }
    
    private static void initGlIds() {
        for (int i = 0; i < WeatherFxMask.test.length; ++i) {
            if (WeatherFxMask.test[i] == WeatherFxMask.SCR_MASK_ADD) {
                WeatherFxMask.ID_SCR_MASK_ADD = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.DST_MASK_ADD) {
                WeatherFxMask.ID_DST_MASK_ADD = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.SCR_MASK_SUB) {
                WeatherFxMask.ID_SCR_MASK_SUB = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.DST_MASK_SUB) {
                WeatherFxMask.ID_DST_MASK_SUB = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.SCR_PARTICLES) {
                WeatherFxMask.ID_SCR_PARTICLES = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.DST_PARTICLES) {
                WeatherFxMask.ID_DST_PARTICLES = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.SCR_MERGE) {
                WeatherFxMask.ID_SCR_MERGE = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.DST_MERGE) {
                WeatherFxMask.ID_DST_MERGE = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.SCR_FINAL) {
                WeatherFxMask.ID_SCR_FINAL = i;
            }
            else if (WeatherFxMask.test[i] == WeatherFxMask.DST_FINAL) {
                WeatherFxMask.ID_DST_FINAL = i;
            }
        }
    }
    
    private static void updateDebugKeys() {
        if (WeatherFxMask.keypause > 0) {
            --WeatherFxMask.keypause;
        }
        if (WeatherFxMask.keypause == 0) {
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            boolean b5 = false;
            if (WeatherFxMask.TARGET_BLEND == 0) {
                WeatherFxMask.var1 = WeatherFxMask.ID_SCR_MASK_ADD;
                WeatherFxMask.var2 = WeatherFxMask.ID_DST_MASK_ADD;
            }
            else if (WeatherFxMask.TARGET_BLEND == 1) {
                WeatherFxMask.var1 = WeatherFxMask.ID_SCR_MASK_SUB;
                WeatherFxMask.var2 = WeatherFxMask.ID_DST_MASK_SUB;
            }
            else if (WeatherFxMask.TARGET_BLEND == 2) {
                WeatherFxMask.var1 = WeatherFxMask.ID_SCR_MERGE;
                WeatherFxMask.var2 = WeatherFxMask.ID_DST_MERGE;
            }
            else if (WeatherFxMask.TARGET_BLEND == 3) {
                WeatherFxMask.var1 = WeatherFxMask.ID_SCR_FINAL;
                WeatherFxMask.var2 = WeatherFxMask.ID_DST_FINAL;
            }
            else if (WeatherFxMask.TARGET_BLEND == 4) {
                WeatherFxMask.var1 = WeatherFxMask.ID_SCR_PARTICLES;
                WeatherFxMask.var2 = WeatherFxMask.ID_DST_PARTICLES;
            }
            if (GameKeyboard.isKeyDown(79)) {
                --WeatherFxMask.var1;
                if (WeatherFxMask.var1 < 0) {
                    WeatherFxMask.var1 = WeatherFxMask.test.length - 1;
                }
                b = true;
            }
            else if (GameKeyboard.isKeyDown(81)) {
                ++WeatherFxMask.var1;
                if (WeatherFxMask.var1 >= WeatherFxMask.test.length) {
                    WeatherFxMask.var1 = 0;
                }
                b = true;
            }
            else if (GameKeyboard.isKeyDown(75)) {
                --WeatherFxMask.var2;
                if (WeatherFxMask.var2 < 0) {
                    WeatherFxMask.var2 = WeatherFxMask.test.length - 1;
                }
                b = true;
            }
            else if (GameKeyboard.isKeyDown(77)) {
                ++WeatherFxMask.var2;
                if (WeatherFxMask.var2 >= WeatherFxMask.test.length) {
                    WeatherFxMask.var2 = 0;
                }
                b = true;
            }
            else if (GameKeyboard.isKeyDown(71)) {
                --WeatherFxMask.TARGET_BLEND;
                if (WeatherFxMask.TARGET_BLEND < 0) {
                    WeatherFxMask.TARGET_BLEND = 4;
                }
                b = true;
                b2 = true;
            }
            else if (GameKeyboard.isKeyDown(73)) {
                ++WeatherFxMask.TARGET_BLEND;
                if (WeatherFxMask.TARGET_BLEND >= 5) {
                    WeatherFxMask.TARGET_BLEND = 0;
                }
                b = true;
                b2 = true;
            }
            else if (WeatherFxMask.MASKING_ENABLED && GameKeyboard.isKeyDown(82)) {
                WeatherFxMask.DEBUG_MASK = !WeatherFxMask.DEBUG_MASK;
                b = true;
                b3 = true;
            }
            else if (WeatherFxMask.MASKING_ENABLED && GameKeyboard.isKeyDown(80)) {
                WeatherFxMask.DEBUG_MASK_AND_PARTICLES = !WeatherFxMask.DEBUG_MASK_AND_PARTICLES;
                b = true;
                b4 = true;
            }
            else if (!GameKeyboard.isKeyDown(72)) {
                if (GameKeyboard.isKeyDown(76)) {
                    WeatherFxMask.MASKING_ENABLED = !WeatherFxMask.MASKING_ENABLED;
                    b = true;
                    b5 = true;
                }
            }
            if (b) {
                if (b2) {
                    if (WeatherFxMask.TARGET_BLEND == 0) {
                        DebugLog.log("TargetBlend = MASK_ADD");
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 1) {
                        DebugLog.log("TargetBlend = MASK_SUB");
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 2) {
                        DebugLog.log("TargetBlend = MERGE");
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 3) {
                        DebugLog.log("TargetBlend = FINAL");
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 4) {
                        DebugLog.log("TargetBlend = PARTICLES");
                    }
                }
                else if (b3) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, WeatherFxMask.DEBUG_MASK));
                }
                else if (b4) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, WeatherFxMask.DEBUG_MASK_AND_PARTICLES));
                }
                else if (b5) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, WeatherFxMask.MASKING_ENABLED));
                }
                else {
                    if (WeatherFxMask.TARGET_BLEND == 0) {
                        WeatherFxMask.ID_SCR_MASK_ADD = WeatherFxMask.var1;
                        WeatherFxMask.ID_DST_MASK_ADD = WeatherFxMask.var2;
                        WeatherFxMask.SCR_MASK_ADD = WeatherFxMask.test[WeatherFxMask.ID_SCR_MASK_ADD];
                        WeatherFxMask.DST_MASK_ADD = WeatherFxMask.test[WeatherFxMask.ID_DST_MASK_ADD];
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 1) {
                        WeatherFxMask.ID_SCR_MASK_SUB = WeatherFxMask.var1;
                        WeatherFxMask.ID_DST_MASK_SUB = WeatherFxMask.var2;
                        WeatherFxMask.SCR_MASK_SUB = WeatherFxMask.test[WeatherFxMask.ID_SCR_MASK_SUB];
                        WeatherFxMask.DST_MASK_SUB = WeatherFxMask.test[WeatherFxMask.ID_DST_MASK_SUB];
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 2) {
                        WeatherFxMask.ID_SCR_MERGE = WeatherFxMask.var1;
                        WeatherFxMask.ID_DST_MERGE = WeatherFxMask.var2;
                        WeatherFxMask.SCR_MERGE = WeatherFxMask.test[WeatherFxMask.ID_SCR_MERGE];
                        WeatherFxMask.DST_MERGE = WeatherFxMask.test[WeatherFxMask.ID_DST_MERGE];
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 3) {
                        WeatherFxMask.ID_SCR_FINAL = WeatherFxMask.var1;
                        WeatherFxMask.ID_DST_FINAL = WeatherFxMask.var2;
                        WeatherFxMask.SCR_FINAL = WeatherFxMask.test[WeatherFxMask.ID_SCR_FINAL];
                        WeatherFxMask.DST_FINAL = WeatherFxMask.test[WeatherFxMask.ID_DST_FINAL];
                    }
                    else if (WeatherFxMask.TARGET_BLEND == 4) {
                        WeatherFxMask.ID_SCR_PARTICLES = WeatherFxMask.var1;
                        WeatherFxMask.ID_DST_PARTICLES = WeatherFxMask.var2;
                        WeatherFxMask.SCR_PARTICLES = WeatherFxMask.test[WeatherFxMask.ID_SCR_PARTICLES];
                        WeatherFxMask.DST_PARTICLES = WeatherFxMask.test[WeatherFxMask.ID_DST_PARTICLES];
                    }
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, WeatherFxMask.testNames[WeatherFxMask.var1], WeatherFxMask.testNames[WeatherFxMask.var2]));
                }
                WeatherFxMask.keypause = 30;
            }
        }
    }
    
    static {
        WeatherFxMask.DEBUG_KEYS = false;
        WeatherFxMask.offsetX = (float)(32 * Core.TileScale);
        WeatherFxMask.offsetY = (float)(96 * Core.TileScale);
        WeatherFxMask.defColorInfo = new ColorInfo();
        WeatherFxMask.DIAMOND_ROWS = 1000;
        WeatherFxMask.playerMasks = new PlayerFxMask[4];
        WeatherFxMask.dmiter = new DiamondMatrixIterator(0);
        diamondMatrixPos = new Vector2i();
        WeatherFxMask.tmpVec = new Vector3f();
        WeatherFxMask.tmpTorch = new IsoGameCharacter.TorchInfo();
        WeatherFxMask.tmpColInfo = new ColorInfo();
        WeatherFxMask.test = new int[] { 0, 1, 768, 769, 774, 775, 770, 771, 772, 773, 32769, 32770, 32771, 32772, 776, 35065, 35066, 34185, 35067 };
        WeatherFxMask.testNames = new String[] { "GL_ZERO", "GL_ONE", "GL_SRC_COLOR", "GL_ONE_MINUS_SRC_COLOR", "GL_DST_COLOR", "GL_ONE_MINUS_DST_COLOR", "GL_SRC_ALPHA", "GL_ONE_MINUS_SRC_ALPHA", "GL_DST_ALPHA", "GL_ONE_MINUS_DST_ALPHA", "GL_CONSTANT_COLOR", "GL_ONE_MINUS_CONSTANT_COLOR", "GL_CONSTANT_ALPHA", "GL_ONE_MINUS_CONSTANT_ALPHA", "GL_SRC_ALPHA_SATURATE", "GL_SRC1_COLOR (33)", "GL_ONE_MINUS_SRC1_COLOR (33)", "GL_SRC1_ALPHA (15)", "GL_ONE_MINUS_SRC1_ALPHA (33)" };
        WeatherFxMask.var1 = 1;
        WeatherFxMask.var2 = 1;
        WeatherFxMask.var3 = 1.0f;
        WeatherFxMask.SCR_MASK_ADD = 770;
        WeatherFxMask.DST_MASK_ADD = 771;
        WeatherFxMask.SCR_MASK_SUB = 0;
        WeatherFxMask.DST_MASK_SUB = 0;
        WeatherFxMask.SCR_PARTICLES = 1;
        WeatherFxMask.DST_PARTICLES = 771;
        WeatherFxMask.SCR_MERGE = 770;
        WeatherFxMask.DST_MERGE = 771;
        WeatherFxMask.SCR_FINAL = 770;
        WeatherFxMask.DST_FINAL = 771;
        WeatherFxMask.TARGET_BLEND = 0;
        WeatherFxMask.DEBUG_MASK = false;
        WeatherFxMask.MASKING_ENABLED = true;
        WeatherFxMask.DEBUG_MASK_AND_PARTICLES = false;
        WeatherFxMask.keypause = 0;
    }
    
    public static class PlayerFxMask
    {
        private WeatherFxMask[] masks;
        private int maskPointer;
        private boolean maskEnabled;
        private IsoGridSquare plrSquare;
        private int DISABLED_MASKS;
        private boolean requiresUpdate;
        private boolean hasMaskToDraw;
        private int playerIndex;
        private IsoPlayer player;
        private int playerZ;
        private IWorldRegion curIsoWorldRegion;
        private ArrayList<IWorldRegion> curConnectedRegions;
        private final ArrayList<IWorldRegion> isoWorldRegionTemp;
        private boolean DIAMOND_ITER_DONE;
        private boolean isFirstSquare;
        private IsoGridSquare firstSquare;
        
        public PlayerFxMask() {
            this.maskPointer = 0;
            this.maskEnabled = false;
            this.DISABLED_MASKS = 0;
            this.requiresUpdate = false;
            this.hasMaskToDraw = true;
            this.curConnectedRegions = new ArrayList<IWorldRegion>();
            this.isoWorldRegionTemp = new ArrayList<IWorldRegion>();
            this.DIAMOND_ITER_DONE = false;
            this.isFirstSquare = true;
        }
        
        private void init() {
            this.masks = new WeatherFxMask[30000];
            for (int i = 0; i < this.masks.length; ++i) {
                if (this.masks[i] == null) {
                    this.masks[i] = new WeatherFxMask();
                }
            }
            this.maskEnabled = true;
        }
        
        private void initMask() {
            if (GameServer.bServer) {
                return;
            }
            if (!this.maskEnabled) {
                this.init();
            }
            this.playerIndex = IsoCamera.frameState.playerIndex;
            this.player = IsoPlayer.players[this.playerIndex];
            this.playerZ = (int)this.player.getZ();
            this.DIAMOND_ITER_DONE = false;
            this.requiresUpdate = false;
            if (this.player != null) {
                if (this.isFirstSquare || this.plrSquare == null || this.plrSquare != this.player.getSquare()) {
                    this.plrSquare = this.player.getSquare();
                    this.maskPointer = 0;
                    this.DISABLED_MASKS = 0;
                    this.requiresUpdate = true;
                    if (this.firstSquare == null) {
                        this.firstSquare = this.plrSquare;
                    }
                    if (this.firstSquare != null && this.firstSquare != this.plrSquare) {
                        this.isFirstSquare = false;
                    }
                }
                this.curIsoWorldRegion = this.player.getMasterRegion();
                this.curConnectedRegions.clear();
                if (this.curIsoWorldRegion != null && this.player.getMasterRegion().isFogMask()) {
                    this.isoWorldRegionTemp.clear();
                    this.isoWorldRegionTemp.add(this.curIsoWorldRegion);
                    while (this.isoWorldRegionTemp.size() > 0) {
                        final IWorldRegion e = this.isoWorldRegionTemp.remove(0);
                        this.curConnectedRegions.add(e);
                        if (e.getNeighbors().size() == 0) {
                            continue;
                        }
                        for (final IsoWorldRegion e2 : e.getNeighbors()) {
                            if (!this.isoWorldRegionTemp.contains(e2)) {
                                if (this.curConnectedRegions.contains(e2)) {
                                    continue;
                                }
                                if (!e2.isFogMask()) {
                                    continue;
                                }
                                this.isoWorldRegionTemp.add(e2);
                            }
                        }
                    }
                }
                else {
                    this.curIsoWorldRegion = null;
                }
            }
            if (IsoWeatherFX.instance == null) {
                this.hasMaskToDraw = false;
                return;
            }
            this.hasMaskToDraw = true;
            if (this.hasMaskToDraw) {
                if ((this.player.getSquare() != null && (this.player.getSquare().getBuilding() != null || !this.player.getSquare().Is(IsoFlagType.exterior))) || (this.curIsoWorldRegion != null && this.curIsoWorldRegion.isFogMask())) {
                    this.hasMaskToDraw = true;
                }
                else {
                    this.hasMaskToDraw = false;
                }
            }
        }
        
        private void addMask(final int n, final int n2, final int n3, final IsoGridSquare isoGridSquare, final int n4) {
            this.addMask(n, n2, n3, isoGridSquare, n4, true);
        }
        
        private void addMask(final int n, final int n2, final int n3, final IsoGridSquare gs, final int flags, final boolean b) {
            if (!this.hasMaskToDraw || !this.requiresUpdate) {
                return;
            }
            if (!this.maskEnabled) {
                this.init();
            }
            final WeatherFxMask mask = this.getMask(n, n2, n3);
            if (mask == null) {
                final WeatherFxMask freeMask = this.getFreeMask();
                freeMask.x = n;
                freeMask.y = n2;
                freeMask.z = n3;
                freeMask.flags = flags;
                freeMask.gs = gs;
                freeMask.enabled = b;
                if (!b && this.DISABLED_MASKS < WeatherFxMask.DIAMOND_ROWS) {
                    ++this.DISABLED_MASKS;
                }
            }
            else {
                if (mask.flags != flags) {
                    final WeatherFxMask weatherFxMask = mask;
                    weatherFxMask.flags |= flags;
                }
                if (!mask.enabled && b) {
                    final WeatherFxMask freeMask2 = this.getFreeMask();
                    freeMask2.x = n;
                    freeMask2.y = n2;
                    freeMask2.z = n3;
                    freeMask2.flags = mask.flags;
                    freeMask2.gs = gs;
                    freeMask2.enabled = b;
                }
                else {
                    mask.enabled = (mask.enabled ? mask.enabled : b);
                    if (b && gs != null && mask.gs == null) {
                        mask.gs = gs;
                    }
                }
            }
        }
        
        private WeatherFxMask getFreeMask() {
            if (this.maskPointer >= this.masks.length) {
                DebugLog.log("Weather Mask buffer out of bounds. Increasing cache.");
                final WeatherFxMask[] masks = this.masks;
                this.masks = new WeatherFxMask[this.masks.length + 10000];
                for (int i = 0; i < this.masks.length; ++i) {
                    if (masks[i] != null) {
                        this.masks[i] = masks[i];
                    }
                    else {
                        this.masks[i] = new WeatherFxMask();
                    }
                }
            }
            return this.masks[this.maskPointer++];
        }
        
        private boolean masksContains(final int n, final int n2, final int n3) {
            return this.getMask(n, n2, n3) != null;
        }
        
        private WeatherFxMask getMask(final int n, final int n2, final int n3) {
            if (this.maskPointer <= 0) {
                return null;
            }
            int n4 = this.maskPointer - 1 - (WeatherFxMask.DIAMOND_ROWS + this.DISABLED_MASKS);
            if (n4 < 0) {
                n4 = 0;
            }
            for (int i = this.maskPointer - 1; i >= n4; --i) {
                if (this.masks[i].isLoc(n, n2, n3)) {
                    return this.masks[i];
                }
            }
            return null;
        }
    }
}
