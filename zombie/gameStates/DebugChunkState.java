// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.ai.astar.Mover;
import zombie.iso.areas.IsoBuilding;
import zombie.core.utils.BooleanGrid;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.BooleanConfigOption;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.iso.LosUtil;
import zombie.iso.IsoCell;
import zombie.characters.IsoGameCharacter;
import zombie.erosion.ErosionData;
import java.util.Iterator;
import zombie.core.properties.PropertyContainer;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.iso.SpriteDetails.IsoFlagType;
import java.util.List;
import java.util.Collections;
import zombie.ui.TextManager;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedVehicleStory.VehicleStorySpawner;
import zombie.iso.IsoChunk;
import zombie.vehicles.PolygonalMap2;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoChunkMap;
import zombie.iso.NearestWalls;
import zombie.VirtualZombieManager;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.core.math.PZMath;
import zombie.core.BoxedStaticValues;
import zombie.iso.ParticlesFire;
import zombie.debug.DebugOptions;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.input.Mouse;
import zombie.iso.IsoObjectPicker;
import zombie.iso.BuildingDef;
import java.util.Stack;
import zombie.debug.LineDrawer;
import zombie.util.Type;
import zombie.iso.IsoGridSquare;
import zombie.FliesSound;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.core.SpriteRenderer;
import zombie.input.GameKeyboard;
import zombie.chat.ChatElement;
import zombie.ui.TextDrawObject;
import zombie.core.Core;
import zombie.iso.PlayerCamera;
import zombie.iso.IsoCamera;
import java.util.Collection;
import zombie.ui.UIManager;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaManager;
import zombie.config.ConfigOption;
import java.nio.ByteBuffer;
import zombie.vehicles.ClipperOffset;
import zombie.ui.UIFont;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ui.UIElement;
import java.util.ArrayList;
import zombie.vehicles.EditVehicleState;

public final class DebugChunkState extends GameState
{
    public static DebugChunkState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit;
    private final ArrayList<UIElement> m_gameUI;
    private final ArrayList<UIElement> m_selfUI;
    private boolean m_bSuspendUI;
    private KahluaTable m_table;
    private int m_playerIndex;
    private int m_z;
    private int gridX;
    private int gridY;
    private UIFont FONT;
    private String m_vehicleStory;
    static boolean keyQpressed;
    private static ClipperOffset m_clipperOffset;
    private static ByteBuffer m_clipperBuffer;
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options;
    private BooleanDebugOption BuildingRect;
    private BooleanDebugOption ChunkGrid;
    private BooleanDebugOption EmptySquares;
    private BooleanDebugOption FlyBuzzEmitters;
    private BooleanDebugOption LightSquares;
    private BooleanDebugOption LineClearCollide;
    private BooleanDebugOption NearestWallsOpt;
    private BooleanDebugOption ObjectPicker;
    private BooleanDebugOption RoomLightRects;
    private BooleanDebugOption VehicleStory;
    private BooleanDebugOption ZoneRect;
    
    public DebugChunkState() {
        this.bExit = false;
        this.m_gameUI = new ArrayList<UIElement>();
        this.m_selfUI = new ArrayList<UIElement>();
        this.m_table = null;
        this.m_playerIndex = 0;
        this.m_z = 0;
        this.gridX = -1;
        this.gridY = -1;
        this.FONT = UIFont.DebugConsole;
        this.m_vehicleStory = "Basic Car Crash";
        this.options = new ArrayList<ConfigOption>();
        this.BuildingRect = new BooleanDebugOption("BuildingRect", true);
        this.ChunkGrid = new BooleanDebugOption("ChunkGrid", true);
        this.EmptySquares = new BooleanDebugOption("EmptySquares", true);
        this.FlyBuzzEmitters = new BooleanDebugOption("FlyBuzzEmitters", true);
        this.LightSquares = new BooleanDebugOption("LightSquares", true);
        this.LineClearCollide = new BooleanDebugOption("LineClearCollide", true);
        this.NearestWallsOpt = new BooleanDebugOption("NearestWalls", true);
        this.ObjectPicker = new BooleanDebugOption("ObjectPicker", true);
        this.RoomLightRects = new BooleanDebugOption("RoomLightRects", true);
        this.VehicleStory = new BooleanDebugOption("VehicleStory", true);
        this.ZoneRect = new BooleanDebugOption("ZoneRect", true);
        DebugChunkState.instance = this;
    }
    
    @Override
    public void enter() {
        (DebugChunkState.instance = this).load();
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }
        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            final IsoPlayer isoPlayer = IsoPlayer.players[this.m_playerIndex];
            this.m_z = ((isoPlayer == null) ? 0 : ((int)isoPlayer.z));
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget((Object)"DebugChunkState_InitUI"), (Object)this);
            if (this.m_table != null && this.m_table.getMetatable() != null) {
                this.m_table.getMetatable().rawset((Object)"_LUA_RELOADED_CHECK", (Object)Boolean.FALSE);
            }
        }
        else {
            UIManager.UI.addAll(this.m_selfUI);
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget((Object)"showUI"), (Object)this.m_table);
        }
        this.bExit = false;
    }
    
    @Override
    public void yield() {
        this.restoreGameUI();
    }
    
    @Override
    public void reenter() {
        this.saveGameUI();
    }
    
    @Override
    public void exit() {
        this.save();
        this.restoreGameUI();
        for (int i = 0; i < IsoCamera.cameras.length; ++i) {
            final PlayerCamera playerCamera = IsoCamera.cameras[i];
            final PlayerCamera playerCamera2 = IsoCamera.cameras[i];
            final float n = 0.0f;
            playerCamera2.DeferedY = n;
            playerCamera.DeferedX = n;
        }
    }
    
    @Override
    public void render() {
        IsoPlayer.setInstance(IsoPlayer.players[this.m_playerIndex]);
        IsoCamera.CamCharacter = IsoPlayer.players[this.m_playerIndex];
        boolean b = true;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (i != this.m_playerIndex && IsoPlayer.players[i] != null) {
                Core.getInstance().StartFrame(i, b);
                Core.getInstance().EndFrame(i);
                b = false;
            }
        }
        Core.getInstance().StartFrame(this.m_playerIndex, b);
        this.renderScene();
        Core.getInstance().EndFrame(this.m_playerIndex);
        Core.getInstance().RenderOffScreenBuffer();
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            TextDrawObject.NoRender(j);
            ChatElement.NoRender(j);
        }
        if (Core.getInstance().StartFrameUI()) {
            this.renderUI();
        }
        Core.getInstance().EndFrameUI();
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        if (this.bExit || GameKeyboard.isKeyPressed(60)) {
            return GameStateMachine.StateAction.Continue;
        }
        return this.updateScene();
    }
    
    public static DebugChunkState checkInstance() {
        DebugChunkState.instance = null;
        if (DebugChunkState.instance != null) {
            if (DebugChunkState.instance.m_table == null || DebugChunkState.instance.m_table.getMetatable() == null) {
                DebugChunkState.instance = null;
            }
            else if (DebugChunkState.instance.m_table.getMetatable().rawget((Object)"_LUA_RELOADED_CHECK") == null) {
                DebugChunkState.instance = null;
            }
        }
        if (DebugChunkState.instance == null) {
            return new DebugChunkState();
        }
        return DebugChunkState.instance;
    }
    
    public void renderScene() {
        IsoCamera.frameState.set(this.m_playerIndex);
        SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
        SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
        SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
        IsoSprite.globalOffsetX = -1.0f;
        IsoWorld.instance.CurrentCell.render();
        if (this.ChunkGrid.getValue()) {
            this.drawGrid();
        }
        this.drawCursor();
        if (this.LightSquares.getValue()) {
            final Stack<IsoLightSource> lamppostPositions = IsoWorld.instance.getCell().getLamppostPositions();
            for (int i = 0; i < lamppostPositions.size(); ++i) {
                final IsoLightSource isoLightSource = lamppostPositions.get(i);
                if (isoLightSource.z == this.m_z) {
                    this.paintSquare(isoLightSource.x, isoLightSource.y, isoLightSource.z, 1.0f, 1.0f, 0.0f, 0.5f);
                }
            }
        }
        if (this.ZoneRect.getValue()) {
            this.drawZones();
        }
        if (this.BuildingRect.getValue()) {
            final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, this.m_z);
            if (gridSquare != null && gridSquare.getBuilding() != null) {
                final BuildingDef def = gridSquare.getBuilding().getDef();
                this.DrawIsoLine((float)def.getX(), (float)def.getY(), (float)def.getX2(), (float)def.getY(), 1.0f, 1.0f, 1.0f, 1.0f, 2);
                this.DrawIsoLine((float)def.getX2(), (float)def.getY(), (float)def.getX2(), (float)def.getY2(), 1.0f, 1.0f, 1.0f, 1.0f, 2);
                this.DrawIsoLine((float)def.getX2(), (float)def.getY2(), (float)def.getX(), (float)def.getY2(), 1.0f, 1.0f, 1.0f, 1.0f, 2);
                this.DrawIsoLine((float)def.getX(), (float)def.getY2(), (float)def.getX(), (float)def.getY(), 1.0f, 1.0f, 1.0f, 1.0f, 2);
            }
        }
        if (this.RoomLightRects.getValue()) {
            final ArrayList<IsoRoomLight> roomLights = IsoWorld.instance.CurrentCell.roomLights;
            for (int j = 0; j < roomLights.size(); ++j) {
                final IsoRoomLight isoRoomLight = roomLights.get(j);
                if (isoRoomLight.z == this.m_z) {
                    this.DrawIsoRect((float)isoRoomLight.x, (float)isoRoomLight.y, (float)isoRoomLight.width, (float)isoRoomLight.height, 0.0f, 1.0f, 1.0f, 1.0f, 1);
                }
            }
        }
        if (this.FlyBuzzEmitters.getValue()) {
            FliesSound.instance.render();
        }
        if (this.m_table != null && this.m_table.rawget((Object)"selectedSquare") != null) {
            final IsoGridSquare isoGridSquare = Type.tryCastTo(this.m_table.rawget((Object)"selectedSquare"), IsoGridSquare.class);
            if (isoGridSquare != null) {
                this.DrawIsoRect((float)isoGridSquare.x, (float)isoGridSquare.y, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 2);
            }
        }
        LineDrawer.render();
        LineDrawer.clear();
    }
    
    private void renderUI() {
        final int playerIndex = this.m_playerIndex;
        final Stack<IsoLightSource> lamppostPositions = IsoWorld.instance.getCell().getLamppostPositions();
        int n = 0;
        for (int i = 0; i < lamppostPositions.size(); ++i) {
            if (((IsoLightSource)lamppostPositions.get(i)).bActive) {
                ++n;
            }
        }
        UIManager.render();
    }
    
    public void setTable(final KahluaTable table) {
        this.m_table = table;
    }
    
    public GameStateMachine.StateAction updateScene() {
        IsoPlayer.setInstance(IsoPlayer.players[this.m_playerIndex]);
        IsoCamera.CamCharacter = IsoPlayer.players[this.m_playerIndex];
        UIManager.setPicked(IsoObjectPicker.Instance.ContextPick(Mouse.getXA(), Mouse.getYA()));
        UIManager.setLastPicked((UIManager.getPicked() == null) ? null : UIManager.getPicked().tile);
        if (GameKeyboard.isKeyDown(16)) {
            if (!DebugChunkState.keyQpressed) {
                final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, 0);
                if (gridSquare != null) {
                    GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(gridSquare.chunk);
                    DebugLog.General.debugln("Requesting sync IsoChunk %s", gridSquare.chunk);
                }
                DebugChunkState.keyQpressed = true;
            }
        }
        else {
            DebugChunkState.keyQpressed = false;
        }
        if (GameKeyboard.isKeyDown(19)) {
            if (!DebugChunkState.keyQpressed) {
                DebugOptions.instance.Terrain.RenderTiles.NewRender.setValue(true);
                DebugChunkState.keyQpressed = true;
                DebugLog.General.debugln("IsoCell.newRender = %s", DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue());
            }
        }
        else {
            DebugChunkState.keyQpressed = false;
        }
        if (GameKeyboard.isKeyDown(20)) {
            if (!DebugChunkState.keyQpressed) {
                DebugOptions.instance.Terrain.RenderTiles.NewRender.setValue(false);
                DebugChunkState.keyQpressed = true;
                DebugLog.General.debugln("IsoCell.newRender = %s", DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue());
            }
        }
        else {
            DebugChunkState.keyQpressed = false;
        }
        if (GameKeyboard.isKeyDown(31)) {
            if (!DebugChunkState.keyQpressed) {
                ParticlesFire.getInstance().reloadShader();
                DebugChunkState.keyQpressed = true;
                DebugLog.General.debugln("ParticlesFire.reloadShader");
            }
        }
        else {
            DebugChunkState.keyQpressed = false;
        }
        IsoCamera.update();
        this.updateCursor();
        return GameStateMachine.StateAction.Remain;
    }
    
    private void saveGameUI() {
        this.m_gameUI.clear();
        this.m_gameUI.addAll(UIManager.UI);
        UIManager.UI.clear();
        this.m_bSuspendUI = UIManager.bSuspend;
        UIManager.setShowPausedMessage(UIManager.bSuspend = false);
        UIManager.defaultthread = this.m_luaEnv.thread;
    }
    
    private void restoreGameUI() {
        this.m_selfUI.clear();
        this.m_selfUI.addAll(UIManager.UI);
        UIManager.UI.clear();
        UIManager.UI.addAll(this.m_gameUI);
        UIManager.bSuspend = this.m_bSuspendUI;
        UIManager.setShowPausedMessage(true);
        UIManager.defaultthread = LuaManager.thread;
    }
    
    public Object fromLua0(final String s) {
        switch (s) {
            case "exit": {
                this.bExit = true;
                return null;
            }
            case "getCameraDragX": {
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedX);
            }
            case "getCameraDragY": {
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedY);
            }
            case "getPlayerIndex": {
                return BoxedStaticValues.toDouble(this.m_playerIndex);
            }
            case "getVehicleStory": {
                return this.m_vehicleStory;
            }
            case "getZ": {
                return BoxedStaticValues.toDouble(this.m_z);
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\"", s));
            }
        }
    }
    
    public Object fromLua1(final String s, final Object o) {
        switch (s) {
            case "getCameraDragX": {
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedX);
            }
            case "getCameraDragY": {
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedY);
            }
            case "setPlayerIndex": {
                this.m_playerIndex = PZMath.clamp(((Double)o).intValue(), 0, 3);
                return null;
            }
            case "setVehicleStory": {
                this.m_vehicleStory = (String)o;
                return null;
            }
            case "setZ": {
                this.m_z = PZMath.clamp(((Double)o).intValue(), 0, 7);
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", s, o));
            }
        }
    }
    
    public Object fromLua2(final String s, final Object o, final Object o2) {
        switch (s) {
            case "dragCamera": {
                final float floatValue = ((Double)o).floatValue();
                final float floatValue2 = ((Double)o2).floatValue();
                IsoCamera.cameras[this.m_playerIndex].DeferedX = -floatValue;
                IsoCamera.cameras[this.m_playerIndex].DeferedY = -floatValue2;
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \\\"%s\\\"", s, o, o2));
            }
        }
    }
    
    private void updateCursor() {
        final int playerIndex = this.m_playerIndex;
        final int tileScale = Core.TileScale;
        final float n = (float)Mouse.getXA();
        final float n2 = (float)Mouse.getYA();
        final float n3 = n - IsoCamera.getScreenLeft(playerIndex);
        final float n4 = n2 - IsoCamera.getScreenTop(playerIndex);
        final float n5 = n3 * Core.getInstance().getZoom(playerIndex);
        final float n6 = n4 * Core.getInstance().getZoom(playerIndex);
        final int z = this.m_z;
        this.gridX = (int)IsoUtils.XToIso(n5, n6, (float)z);
        this.gridY = (int)IsoUtils.YToIso(n5, n6, (float)z);
    }
    
    private void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        final float n10 = (float)this.m_z;
        LineDrawer.drawLine(IsoUtils.XToScreenExact(n, n2, n10, 0), IsoUtils.YToScreenExact(n, n2, n10, 0), IsoUtils.XToScreenExact(n3, n4, n10, 0), IsoUtils.YToScreenExact(n3, n4, n10, 0), n5, n6, n7, n8, n9);
    }
    
    private void DrawIsoRect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
        this.DrawIsoLine(n, n2, n + n3, n2, n5, n6, n7, n8, n9);
        this.DrawIsoLine(n + n3, n2, n + n3, n2 + n4, n5, n6, n7, n8, n9);
        this.DrawIsoLine(n + n3, n2 + n4, n, n2 + n4, n5, n6, n7, n8, n9);
        this.DrawIsoLine(n, n2 + n4, n, n2, n5, n6, n7, n8, n9);
    }
    
    private void drawGrid() {
        final int playerIndex = this.m_playerIndex;
        final float xToIso = IsoUtils.XToIso(-128.0f, -256.0f, 0.0f);
        final float yToIso = IsoUtils.YToIso((float)(Core.getInstance().getOffscreenWidth(playerIndex) + 128), -256.0f, 0.0f);
        final float xToIso2 = IsoUtils.XToIso((float)(Core.getInstance().getOffscreenWidth(playerIndex) + 128), (float)(Core.getInstance().getOffscreenHeight(playerIndex) + 256), 6.0f);
        final float yToIso2 = IsoUtils.YToIso(-128.0f, (float)(Core.getInstance().getOffscreenHeight(playerIndex) + 256), 6.0f);
        int n = (int)yToIso;
        final int n2 = (int)yToIso2;
        int n3 = (int)xToIso;
        final int n4 = (int)xToIso2;
        n3 -= 2;
        n -= 2;
        for (int i = n; i <= n2; ++i) {
            if (i % 10 == 0) {
                this.DrawIsoLine((float)n3, (float)i, (float)n4, (float)i, 1.0f, 1.0f, 1.0f, 0.5f, 1);
            }
        }
        for (int j = n3; j <= n4; ++j) {
            if (j % 10 == 0) {
                this.DrawIsoLine((float)j, (float)n, (float)j, (float)n2, 1.0f, 1.0f, 1.0f, 0.5f, 1);
            }
        }
        for (int k = n; k <= n2; ++k) {
            if (k % 300 == 0) {
                this.DrawIsoLine((float)n3, (float)k, (float)n4, (float)k, 0.0f, 1.0f, 0.0f, 0.5f, 1);
            }
        }
        for (int l = n3; l <= n4; ++l) {
            if (l % 300 == 0) {
                this.DrawIsoLine((float)l, (float)n, (float)l, (float)n2, 0.0f, 1.0f, 0.0f, 0.5f, 1);
            }
        }
        if (GameClient.bClient) {
            for (int n5 = n; n5 <= n2; ++n5) {
                if (n5 % 50 == 0) {
                    this.DrawIsoLine((float)n3, (float)n5, (float)n4, (float)n5, 1.0f, 0.0f, 0.0f, 0.5f, 1);
                }
            }
            for (int n6 = n3; n6 <= n4; ++n6) {
                if (n6 % 50 == 0) {
                    this.DrawIsoLine((float)n6, (float)n, (float)n6, (float)n2, 1.0f, 0.0f, 0.0f, 0.5f, 1);
                }
            }
        }
    }
    
    private void drawCursor() {
        final int playerIndex = this.m_playerIndex;
        final int tileScale = Core.TileScale;
        final float n = (float)this.m_z;
        final int n2 = (int)IsoUtils.XToScreenExact((float)this.gridX, (float)(this.gridY + 1), n, 0);
        final int n3 = (int)IsoUtils.YToScreenExact((float)this.gridX, (float)(this.gridY + 1), n, 0);
        SpriteRenderer.instance.renderPoly((float)n2, (float)n3, (float)(n2 + 32 * tileScale), (float)(n3 - 16 * tileScale), (float)(n2 + 64 * tileScale), (float)n3, (float)(n2 + 32 * tileScale), (float)(n3 + 16 * tileScale), 0.0f, 0.0f, 1.0f, 0.5f);
        final IsoChunkMap isoChunkMap = IsoWorld.instance.getCell().ChunkMap[playerIndex];
        for (int i = isoChunkMap.getWorldYMinTiles(); i < isoChunkMap.getWorldYMaxTiles(); ++i) {
            for (int j = isoChunkMap.getWorldXMinTiles(); j < isoChunkMap.getWorldXMaxTiles(); ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(j, i, n);
                if (gridSquare != null) {
                    if (gridSquare != isoChunkMap.getGridSquare(j, i, (int)n)) {
                        final int n4 = (int)IsoUtils.XToScreenExact((float)j, (float)(i + 1), n, 0);
                        final int n5 = (int)IsoUtils.YToScreenExact((float)j, (float)(i + 1), n, 0);
                        SpriteRenderer.instance.renderPoly((float)n4, (float)n5, (float)(n4 + 32), (float)(n5 - 16), (float)(n4 + 64), (float)n5, (float)(n4 + 32), (float)(n5 + 16), 1.0f, 0.0f, 0.0f, 0.8f);
                    }
                    if (gridSquare == null || gridSquare.getX() != j || gridSquare.getY() != i || gridSquare.getZ() != n || (gridSquare.e != null && gridSquare.e.w != null && gridSquare.e.w != gridSquare) || (gridSquare.w != null && gridSquare.w.e != null && gridSquare.w.e != gridSquare) || (gridSquare.n != null && gridSquare.n.s != null && gridSquare.n.s != gridSquare) || (gridSquare.s != null && gridSquare.s.n != null && gridSquare.s.n != gridSquare) || (gridSquare.nw != null && gridSquare.nw.se != null && gridSquare.nw.se != gridSquare) || (gridSquare.se != null && gridSquare.se.nw != null && gridSquare.se.nw != gridSquare)) {
                        final int n6 = (int)IsoUtils.XToScreenExact((float)j, (float)(i + 1), n, 0);
                        final int n7 = (int)IsoUtils.YToScreenExact((float)j, (float)(i + 1), n, 0);
                        SpriteRenderer.instance.renderPoly((float)n6, (float)n7, (float)(n6 + 32), (float)(n7 - 16), (float)(n6 + 64), (float)n7, (float)(n6 + 32), (float)(n7 + 16), 1.0f, 0.0f, 0.0f, 0.5f);
                    }
                    if (gridSquare != null) {
                        final IsoGridSquare isoGridSquare = gridSquare.testPathFindAdjacent(null, -1, 0, 0) ? null : gridSquare.nav[IsoDirections.W.index()];
                        final IsoGridSquare isoGridSquare2 = gridSquare.testPathFindAdjacent(null, 0, -1, 0) ? null : gridSquare.nav[IsoDirections.N.index()];
                        final IsoGridSquare isoGridSquare3 = gridSquare.testPathFindAdjacent(null, 1, 0, 0) ? null : gridSquare.nav[IsoDirections.E.index()];
                        final IsoGridSquare isoGridSquare4 = gridSquare.testPathFindAdjacent(null, 0, 1, 0) ? null : gridSquare.nav[IsoDirections.S.index()];
                        final IsoGridSquare isoGridSquare5 = gridSquare.testPathFindAdjacent(null, -1, -1, 0) ? null : gridSquare.nav[IsoDirections.NW.index()];
                        final IsoGridSquare isoGridSquare6 = gridSquare.testPathFindAdjacent(null, 1, -1, 0) ? null : gridSquare.nav[IsoDirections.NE.index()];
                        final IsoGridSquare isoGridSquare7 = gridSquare.testPathFindAdjacent(null, -1, 1, 0) ? null : gridSquare.nav[IsoDirections.SW.index()];
                        final IsoGridSquare isoGridSquare8 = gridSquare.testPathFindAdjacent(null, 1, 1, 0) ? null : gridSquare.nav[IsoDirections.SE.index()];
                        if (isoGridSquare != gridSquare.w || isoGridSquare2 != gridSquare.n || isoGridSquare3 != gridSquare.e || isoGridSquare4 != gridSquare.s || isoGridSquare5 != gridSquare.nw || isoGridSquare6 != gridSquare.ne || isoGridSquare7 != gridSquare.sw || isoGridSquare8 != gridSquare.se) {
                            this.paintSquare(j, i, (int)n, 1.0f, 0.0f, 0.0f, 0.5f);
                        }
                    }
                    if (gridSquare != null && ((gridSquare.nav[IsoDirections.NW.index()] != null && gridSquare.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] != gridSquare) || (gridSquare.nav[IsoDirections.NE.index()] != null && gridSquare.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] != gridSquare) || (gridSquare.nav[IsoDirections.SW.index()] != null && gridSquare.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] != gridSquare) || (gridSquare.nav[IsoDirections.SE.index()] != null && gridSquare.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] != gridSquare) || (gridSquare.nav[IsoDirections.N.index()] != null && gridSquare.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] != gridSquare) || (gridSquare.nav[IsoDirections.S.index()] != null && gridSquare.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] != gridSquare) || (gridSquare.nav[IsoDirections.W.index()] != null && gridSquare.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] != gridSquare) || (gridSquare.nav[IsoDirections.E.index()] != null && gridSquare.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] != gridSquare))) {
                        final int n8 = (int)IsoUtils.XToScreenExact((float)j, (float)(i + 1), n, 0);
                        final int n9 = (int)IsoUtils.YToScreenExact((float)j, (float)(i + 1), n, 0);
                        SpriteRenderer.instance.renderPoly((float)n8, (float)n9, (float)(n8 + 32), (float)(n9 - 16), (float)(n8 + 64), (float)n9, (float)(n8 + 32), (float)(n9 + 16), 1.0f, 0.0f, 0.0f, 0.5f);
                    }
                    if (this.EmptySquares.getValue() && gridSquare.getObjects().isEmpty()) {
                        this.paintSquare(j, i, (int)n, 1.0f, 1.0f, 0.0f, 0.5f);
                    }
                    if (gridSquare.getRoom() != null && gridSquare.isFree(false) && !VirtualZombieManager.instance.canSpawnAt(j, i, (int)n)) {
                        this.paintSquare(j, i, (int)n, 1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    if (gridSquare.roofHideBuilding != null) {
                        this.paintSquare(j, i, (int)n, 0.0f, 0.0f, 1.0f, 0.25f);
                    }
                }
            }
        }
        if (IsoCamera.CamCharacter.getCurrentSquare() != null && Math.abs(this.gridX - (int)IsoCamera.CamCharacter.x) <= 1 && Math.abs(this.gridY - (int)IsoCamera.CamCharacter.y) <= 1) {
            final IsoObject testCollideSpecialObjects = IsoCamera.CamCharacter.getCurrentSquare().testCollideSpecialObjects(IsoWorld.instance.CurrentCell.getGridSquare(this.gridX, this.gridY, this.m_z));
            if (testCollideSpecialObjects != null) {
                testCollideSpecialObjects.getSprite().RenderGhostTileRed((int)testCollideSpecialObjects.getX(), (int)testCollideSpecialObjects.getY(), (int)testCollideSpecialObjects.getZ());
            }
        }
        if (this.LineClearCollide.getValue()) {
            this.lineClearCached(IsoWorld.instance.CurrentCell, this.gridX, this.gridY, (int)n, (int)IsoCamera.CamCharacter.getX(), (int)IsoCamera.CamCharacter.getY(), this.m_z, false);
        }
        if (this.NearestWallsOpt.getValue()) {
            NearestWalls.render(this.gridX, this.gridY, this.m_z);
        }
        if (this.VehicleStory.getValue()) {
            this.drawVehicleStory();
        }
    }
    
    private void drawZones() {
        final ArrayList<IsoMetaGrid.Zone> zones = IsoWorld.instance.MetaGrid.getZonesAt(this.gridX, this.gridY, this.m_z, new ArrayList<IsoMetaGrid.Zone>());
        for (int i = 0; i < zones.size(); ++i) {
            final IsoMetaGrid.Zone zone = zones.get(i);
            if (!zone.isPolyline()) {
                if (!zone.points.isEmpty()) {
                    for (int j = 0; j < zone.points.size(); j += 2) {
                        this.DrawIsoLine((float)zone.points.get(j), (float)zone.points.get(j + 1), (float)zone.points.get((j + 2) % zone.points.size()), (float)zone.points.get((j + 3) % zone.points.size()), 1.0f, 1.0f, 0.0f, 1.0f, 1);
                    }
                }
                else {
                    this.DrawIsoLine((float)zone.x, (float)zone.y, (float)(zone.x + zone.w), (float)zone.y, 1.0f, 1.0f, 0.0f, 1.0f, 1);
                    this.DrawIsoLine((float)zone.x, (float)(zone.y + zone.h), (float)(zone.x + zone.w), (float)(zone.y + zone.h), 1.0f, 1.0f, 0.0f, 1.0f, 1);
                    this.DrawIsoLine((float)zone.x, (float)zone.y, (float)zone.x, (float)(zone.y + zone.h), 1.0f, 1.0f, 0.0f, 1.0f, 1);
                    this.DrawIsoLine((float)(zone.x + zone.w), (float)zone.y, (float)(zone.x + zone.w), (float)(zone.y + zone.h), 1.0f, 1.0f, 0.0f, 1.0f, 1);
                }
            }
        }
        final ArrayList<IsoMetaGrid.Zone> zonesIntersecting = IsoWorld.instance.MetaGrid.getZonesIntersecting(this.gridX - 1, this.gridY - 1, this.m_z, 3, 3, new ArrayList<IsoMetaGrid.Zone>());
        final PolygonalMap2.LiangBarsky liangBarsky = new PolygonalMap2.LiangBarsky();
        final double[] array = new double[2];
        final IsoChunk chunkForGridSquare = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, this.m_z);
        for (int k = 0; k < zonesIntersecting.size(); ++k) {
            final IsoMetaGrid.Zone zone2 = zonesIntersecting.get(k);
            if (zone2 != null && zone2.isPolyline() && !zone2.points.isEmpty()) {
                for (int l = 0; l < zone2.points.size() - 2; l += 2) {
                    final int value = zone2.points.get(l);
                    final int value2 = zone2.points.get(l + 1);
                    final int value3 = zone2.points.get(l + 2);
                    final int value4 = zone2.points.get(l + 3);
                    this.DrawIsoLine((float)value, (float)value2, (float)value3, (float)value4, 1.0f, 1.0f, 0.0f, 1.0f, 1);
                    final float n = (float)(value3 - value);
                    final float n2 = (float)(value4 - value2);
                    if (chunkForGridSquare != null && liangBarsky.lineRectIntersect((float)value, (float)value2, n, n2, (float)(chunkForGridSquare.wx * 10), (float)(chunkForGridSquare.wy * 10), (float)(chunkForGridSquare.wx * 10 + 10), (float)(chunkForGridSquare.wy * 10 + 10), array)) {
                        this.DrawIsoLine(value + (float)array[0] * n, value2 + (float)array[0] * n2, value + (float)array[1] * n, value2 + (float)array[1] * n2, 0.0f, 1.0f, 0.0f, 1.0f, 1);
                    }
                }
                if (zone2.polylineOutlinePoints != null) {
                    final float[] polylineOutlinePoints = zone2.polylineOutlinePoints;
                    for (int n3 = 0; n3 < polylineOutlinePoints.length; n3 += 2) {
                        this.DrawIsoLine(polylineOutlinePoints[n3], polylineOutlinePoints[n3 + 1], polylineOutlinePoints[(n3 + 2) % polylineOutlinePoints.length], polylineOutlinePoints[(n3 + 3) % polylineOutlinePoints.length], 1.0f, 1.0f, 0.0f, 1.0f, 1);
                    }
                }
            }
        }
        final IsoMetaGrid.VehicleZone vehicleZone = IsoWorld.instance.MetaGrid.getVehicleZoneAt(this.gridX, this.gridY, this.m_z);
        if (vehicleZone != null) {
            final float n4 = 0.5f;
            final float n5 = 1.0f;
            final float n6 = 0.5f;
            final float n7 = 1.0f;
            this.DrawIsoLine((float)vehicleZone.x, (float)vehicleZone.y, (float)(vehicleZone.x + vehicleZone.w), (float)vehicleZone.y, n4, n5, n6, n7, 1);
            this.DrawIsoLine((float)vehicleZone.x, (float)(vehicleZone.y + vehicleZone.h), (float)(vehicleZone.x + vehicleZone.w), (float)(vehicleZone.y + vehicleZone.h), n4, n5, n6, n7, 1);
            this.DrawIsoLine((float)vehicleZone.x, (float)vehicleZone.y, (float)vehicleZone.x, (float)(vehicleZone.y + vehicleZone.h), n4, n5, n6, n7, 1);
            this.DrawIsoLine((float)(vehicleZone.x + vehicleZone.w), (float)vehicleZone.y, (float)(vehicleZone.x + vehicleZone.w), (float)(vehicleZone.y + vehicleZone.h), n4, n5, n6, n7, 1);
        }
    }
    
    private void drawVehicleStory() {
        final ArrayList<IsoMetaGrid.Zone> zonesIntersecting = IsoWorld.instance.MetaGrid.getZonesIntersecting(this.gridX - 1, this.gridY - 1, this.m_z, 3, 3, new ArrayList<IsoMetaGrid.Zone>());
        if (zonesIntersecting.isEmpty()) {
            return;
        }
        final IsoChunk chunkForGridSquare = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, this.m_z);
        if (chunkForGridSquare == null) {
            return;
        }
        for (int i = 0; i < zonesIntersecting.size(); ++i) {
            final IsoMetaGrid.Zone zone = zonesIntersecting.get(i);
            if ("Nav".equals(zone.type)) {
                final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
                final RandomizedVehicleStoryBase randomizedVehicleStoryByName = IsoWorld.instance.getRandomizedVehicleStoryByName(this.m_vehicleStory);
                if (randomizedVehicleStoryByName != null) {
                    if (randomizedVehicleStoryByName.isValid(zone, chunkForGridSquare, true)) {
                        if (randomizedVehicleStoryByName.initVehicleStorySpawner(zone, chunkForGridSquare, true)) {
                            final int minZoneWidth = randomizedVehicleStoryByName.getMinZoneWidth();
                            final int minZoneHeight = randomizedVehicleStoryByName.getMinZoneHeight();
                            final float[] array = new float[3];
                            if (randomizedVehicleStoryByName.getSpawnPoint(zone, chunkForGridSquare, array)) {
                                final float n = array[0];
                                final float n2 = array[1];
                                instance.spawn(n, n2, 0.0f, array[2] + 1.5707964f, (p0, p1) -> {});
                                instance.render(n, n2, 0.0f, (float)minZoneWidth, (float)minZoneHeight, array[2]);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void DrawBehindStuff() {
        this.IsBehindStuff(IsoCamera.CamCharacter.getCurrentSquare());
    }
    
    private boolean IsBehindStuff(final IsoGridSquare isoGridSquare) {
        for (int n = 1; n < 8 && isoGridSquare.getZ() + n < 8; ++n) {
            for (int i = -5; i <= 6; ++i) {
                for (int j = -5; j <= 6; ++j) {
                    final int n2 = i;
                    if (j >= n2 - 5) {
                        if (j <= n2 + 5) {
                            this.paintSquare(isoGridSquare.getX() + j + n * 3, isoGridSquare.getY() + i + n * 3, isoGridSquare.getZ() + n, 1.0f, 1.0f, 0.0f, 0.25f);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean IsBehindStuffRecY(final int n, final int n2, final int n3) {
        IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (n3 >= 15) {
            return false;
        }
        this.paintSquare(n, n2, n3, 1.0f, 1.0f, 0.0f, 0.25f);
        return this.IsBehindStuffRecY(n, n2 + 1, n3 + 1);
    }
    
    private boolean IsBehindStuffRecXY(final int n, final int n2, final int n3, final int n4) {
        IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (n3 >= 15) {
            return false;
        }
        this.paintSquare(n, n2, n3, 1.0f, 1.0f, 0.0f, 0.25f);
        return this.IsBehindStuffRecXY(n + n4, n2 + n4, n3 + 1, n4);
    }
    
    private boolean IsBehindStuffRecX(final int n, final int n2, final int n3) {
        IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (n3 >= 15) {
            return false;
        }
        this.paintSquare(n, n2, n3, 1.0f, 1.0f, 0.0f, 0.25f);
        return this.IsBehindStuffRecX(n + 1, n2, n3 + 1);
    }
    
    private void paintSquare(final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final float n7) {
        final int tileScale = Core.TileScale;
        final int n8 = (int)IsoUtils.XToScreenExact((float)n, (float)(n2 + 1), (float)n3, 0);
        final int n9 = (int)IsoUtils.YToScreenExact((float)n, (float)(n2 + 1), (float)n3, 0);
        SpriteRenderer.instance.renderPoly((float)n8, (float)n9, (float)(n8 + 32 * tileScale), (float)(n9 - 16 * tileScale), (float)(n8 + 64 * tileScale), (float)n9, (float)(n8 + 32 * tileScale), (float)(n9 + 16 * tileScale), n4, n5, n6, n7);
    }
    
    void drawModData() {
        final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, this.m_z);
        final int n = Core.getInstance().getScreenWidth() - 250;
        int n2 = 10;
        final int lineHeight = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
        if (gridSquare != null && gridSquare.getModData() != null) {
            final KahluaTable modData = gridSquare.getModData();
            int n3;
            this.DrawString(n, n3 = n2 + lineHeight, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.getX(), gridSquare.getY(), gridSquare.getZ()));
            final KahluaTableIterator iterator = modData.iterator();
            while (iterator.advance()) {
                this.DrawString(n, n3 += lineHeight, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, iterator.getKey().toString(), iterator.getValue().toString()));
                if (iterator.getValue() instanceof KahluaTable) {
                    final KahluaTableIterator iterator2 = ((KahluaTable)iterator.getValue()).iterator();
                    while (iterator2.advance()) {
                        this.DrawString(n + 8, n3 += lineHeight, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, iterator2.getKey().toString(), iterator2.getValue().toString()));
                    }
                }
            }
            n2 = n3 + lineHeight;
        }
        if (gridSquare != null) {
            final PropertyContainer properties = gridSquare.getProperties();
            final ArrayList<String> propertyNames = properties.getPropertyNames();
            if (!propertyNames.isEmpty()) {
                this.DrawString(n, n2 += lineHeight, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.getX(), gridSquare.getY(), gridSquare.getZ()));
                Collections.sort((List<Comparable>)propertyNames);
                for (final String s : propertyNames) {
                    this.DrawString(n, n2 += lineHeight, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, properties.Val(s)));
                }
            }
            for (final IsoFlagType isoFlagType : IsoFlagType.values()) {
                if (properties.Is(isoFlagType)) {
                    this.DrawString(n, n2 += lineHeight, isoFlagType.toString());
                }
            }
        }
        if (gridSquare != null) {
            final ErosionData.Square erosionData = gridSquare.getErosionData();
            if (erosionData != null) {
                final int n4;
                this.DrawString(n, n4 = n2 + lineHeight + lineHeight, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.getX(), gridSquare.getY(), gridSquare.getZ()));
                final int n5;
                this.DrawString(n, n5 = n4 + lineHeight, invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, erosionData.init));
                final int n6;
                this.DrawString(n, n6 = n5 + lineHeight, invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, erosionData.doNothing));
                this.DrawString(n, n6 + lineHeight, invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, gridSquare.chunk.getErosionData().init));
            }
        }
    }
    
    void drawPlayerInfo() {
        final int n = Core.getInstance().getScreenWidth() - 250;
        final int n2 = Core.getInstance().getScreenHeight() / 2;
        final int lineHeight = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
        final IsoGameCharacter camCharacter = IsoCamera.CamCharacter;
        final int n3;
        this.DrawString(n, n3 = n2 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getBodyDamage().getBoredomLevel()));
        final int n4;
        this.DrawString(n, n4 = n3 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().endurance));
        final int n5;
        this.DrawString(n, n5 = n4 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().fatigue));
        final int n6;
        this.DrawString(n, n6 = n5 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().hunger));
        final int n7;
        this.DrawString(n, n7 = n6 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().Pain));
        final int n8;
        this.DrawString(n, n8 = n7 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().Panic));
        final int n9;
        this.DrawString(n, n9 = n8 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().getStress()));
        final int n10;
        this.DrawString(n, n10 = n9 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, ((IsoPlayer)camCharacter).getPlayerClothingTemperature()));
        final int n11;
        this.DrawString(n, n11 = n10 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getTemperature()));
        final int n12;
        this.DrawString(n, n12 = n11 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getStats().thirst));
        final int n13;
        this.DrawString(n, n13 = n12 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getBodyDamage().getFoodSicknessLevel()));
        final int n14;
        this.DrawString(n, n14 = n13 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getBodyDamage().getPoisonLevel()));
        final int n15;
        this.DrawString(n, n15 = n14 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getBodyDamage().getUnhappynessLevel()));
        final int n16;
        this.DrawString(n, n16 = n15 + lineHeight, invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, camCharacter.getBodyDamage().isInfected()));
        final int n17;
        this.DrawString(n, n17 = n16 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getBodyDamage().getInfectionLevel()));
        final int n18;
        this.DrawString(n, n18 = n17 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, camCharacter.getBodyDamage().getFakeInfectionLevel()));
        final int n19;
        this.DrawString(n, n19 = n18 + lineHeight + lineHeight, "WORLD");
        this.DrawString(n, n19 + lineHeight, invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, IsoWorld.instance.getGlobalTemperature()));
    }
    
    public LosUtil.TestResults lineClearCached(final IsoCell isoCell, final int n, final int n2, final int n3, int i, int j, int k, final boolean b) {
        final int a = n2 - j;
        final int a2 = n - i;
        final int n4 = n3 - k;
        int n5 = a2;
        int n6 = a;
        int n7 = n4;
        n5 += 100;
        n6 += 100;
        n7 += 16;
        if (n5 < 0 || n6 < 0 || n7 < 0 || n5 >= 200 || n6 >= 200) {
            return LosUtil.TestResults.Blocked;
        }
        final LosUtil.TestResults clear = LosUtil.TestResults.Clear;
        int n8 = 1;
        final float n9 = 0.5f;
        final float n10 = 0.5f;
        IsoGridSquare gridSquare = isoCell.getGridSquare(i, j, k);
        if (Math.abs(a2) > Math.abs(a) && Math.abs(a2) > Math.abs(n4)) {
            final float n11 = a / (float)a2;
            final float n12 = n4 / (float)a2;
            float n13 = n9 + j;
            float n14 = n10 + k;
            final int n15 = (a2 < 0) ? -1 : 1;
            final float n16 = n11 * n15;
            final float n17 = n12 * n15;
            while (i != n) {
                i += n15;
                n13 += n16;
                n14 += n17;
                final IsoGridSquare gridSquare2 = isoCell.getGridSquare(i, (int)n13, (int)n14);
                this.paintSquare(i, (int)n13, (int)n14, 1.0f, 1.0f, 1.0f, 0.5f);
                if (gridSquare2 != null && gridSquare != null && gridSquare2.testVisionAdjacent(gridSquare.getX() - gridSquare2.getX(), gridSquare.getY() - gridSquare2.getY(), gridSquare.getZ() - gridSquare2.getZ(), true, b) == LosUtil.TestResults.Blocked) {
                    this.paintSquare(i, (int)n13, (int)n14, 1.0f, 0.0f, 0.0f, 0.5f);
                    this.paintSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ(), 1.0f, 0.0f, 0.0f, 0.5f);
                    n8 = 4;
                }
                gridSquare = gridSquare2;
                final int n18 = (int)n13;
                final int n19 = (int)n14;
            }
        }
        else if (Math.abs(a) >= Math.abs(a2) && Math.abs(a) > Math.abs(n4)) {
            final float n20 = a2 / (float)a;
            final float n21 = n4 / (float)a;
            float n22 = n9 + i;
            float n23 = n10 + k;
            final int n24 = (a < 0) ? -1 : 1;
            final float n25 = n20 * n24;
            final float n26 = n21 * n24;
            while (j != n2) {
                j += n24;
                n22 += n25;
                n23 += n26;
                final IsoGridSquare gridSquare3 = isoCell.getGridSquare((int)n22, j, (int)n23);
                this.paintSquare((int)n22, j, (int)n23, 1.0f, 1.0f, 1.0f, 0.5f);
                if (gridSquare3 != null && gridSquare != null && gridSquare3.testVisionAdjacent(gridSquare.getX() - gridSquare3.getX(), gridSquare.getY() - gridSquare3.getY(), gridSquare.getZ() - gridSquare3.getZ(), true, b) == LosUtil.TestResults.Blocked) {
                    this.paintSquare((int)n22, j, (int)n23, 1.0f, 0.0f, 0.0f, 0.5f);
                    this.paintSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ(), 1.0f, 0.0f, 0.0f, 0.5f);
                    n8 = 4;
                }
                gridSquare = gridSquare3;
                final int n27 = (int)n22;
                final int n28 = (int)n23;
            }
        }
        else {
            final float n29 = a2 / (float)n4;
            final float n30 = a / (float)n4;
            float n31 = n9 + i;
            float n32 = n10 + j;
            final int n33 = (n4 < 0) ? -1 : 1;
            final float n34 = n29 * n33;
            final float n35 = n30 * n33;
            while (k != n3) {
                k += n33;
                n31 += n34;
                n32 += n35;
                final IsoGridSquare gridSquare4 = isoCell.getGridSquare((int)n31, (int)n32, k);
                this.paintSquare((int)n31, (int)n32, k, 1.0f, 1.0f, 1.0f, 0.5f);
                if (gridSquare4 != null && gridSquare != null && gridSquare4.testVisionAdjacent(gridSquare.getX() - gridSquare4.getX(), gridSquare.getY() - gridSquare4.getY(), gridSquare.getZ() - gridSquare4.getZ(), true, b) == LosUtil.TestResults.Blocked) {
                    n8 = 4;
                }
                gridSquare = gridSquare4;
                final int n36 = (int)n31;
                final int n37 = (int)n32;
            }
        }
        if (n8 == 1) {
            return LosUtil.TestResults.Clear;
        }
        if (n8 == 2) {
            return LosUtil.TestResults.ClearThroughOpenDoor;
        }
        if (n8 == 3) {
            return LosUtil.TestResults.ClearThroughWindow;
        }
        if (n8 == 4) {
            return LosUtil.TestResults.Blocked;
        }
        return LosUtil.TestResults.Blocked;
    }
    
    private void DrawString(final int n, final int n2, final String s) {
        SpriteRenderer.instance.renderi(null, n - 1, n2, TextManager.instance.MeasureStringX(this.FONT, s) + 2, TextManager.instance.getFontFromEnum(this.FONT).getLineHeight(), 0.0f, 0.0f, 0.0f, 0.8f, null);
        TextManager.instance.DrawString(this.FONT, n, n2, s, 1.0, 1.0, 1.0, 1.0);
    }
    
    public ConfigOption getOptionByName(final String anObject) {
        for (int i = 0; i < this.options.size(); ++i) {
            final ConfigOption configOption = this.options.get(i);
            if (configOption.getName().equals(anObject)) {
                return configOption;
            }
        }
        return null;
    }
    
    public int getOptionCount() {
        return this.options.size();
    }
    
    public ConfigOption getOptionByIndex(final int index) {
        return this.options.get(index);
    }
    
    public void setBoolean(final String s, final boolean value) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof BooleanConfigOption) {
            ((BooleanConfigOption)optionByName).setValue(value);
        }
    }
    
    public boolean getBoolean(final String s) {
        final ConfigOption optionByName = this.getOptionByName(s);
        return optionByName instanceof BooleanConfigOption && ((BooleanConfigOption)optionByName).getValue();
    }
    
    public void save() {
        new ConfigFile().write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator), 1, (ArrayList<? extends ConfigOption>)this.options);
    }
    
    public void load() {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(s)) {
            for (int i = 0; i < configFile.getOptions().size(); ++i) {
                final ConfigOption configOption = configFile.getOptions().get(i);
                final ConfigOption optionByName = this.getOptionByName(configOption.getName());
                if (optionByName != null) {
                    optionByName.parse(configOption.getValueAsString());
                }
            }
        }
    }
    
    static {
        DebugChunkState.keyQpressed = false;
        DebugChunkState.m_clipperOffset = null;
    }
    
    private class FloodFill
    {
        private IsoGridSquare start;
        private final int FLOOD_SIZE = 11;
        private BooleanGrid visited;
        private Stack<IsoGridSquare> stack;
        private IsoBuilding building;
        private Mover mover;
        
        private FloodFill() {
            this.start = null;
            this.visited = new BooleanGrid(11, 11);
            this.stack = new Stack<IsoGridSquare>();
            this.building = null;
            this.mover = null;
        }
        
        void calculate(final Mover mover, IsoGridSquare pop) {
            this.start = pop;
            this.mover = mover;
            if (this.start.getRoom() != null) {
                this.building = this.start.getRoom().getBuilding();
            }
            if (!this.push(this.start.getX(), this.start.getY())) {
                return;
            }
            while ((pop = this.pop()) != null) {
                int x;
                int y;
                for (x = pop.getX(), y = pop.getY(); this.shouldVisit(x, y, x, y - 1); --y) {}
                int n2;
                int n = n2 = 0;
                do {
                    this.visited.setValue(this.gridX(x), this.gridY(y), true);
                    if (n2 == 0 && this.shouldVisit(x, y, x - 1, y)) {
                        if (!this.push(x - 1, y)) {
                            return;
                        }
                        n2 = 1;
                    }
                    else if (n2 != 0 && !this.shouldVisit(x, y, x - 1, y)) {
                        n2 = 0;
                    }
                    else if (n2 != 0 && !this.shouldVisit(x - 1, y, x - 1, y - 1) && !this.push(x - 1, y)) {
                        return;
                    }
                    if (n == 0 && this.shouldVisit(x, y, x + 1, y)) {
                        if (!this.push(x + 1, y)) {
                            return;
                        }
                        n = 1;
                    }
                    else if (n != 0 && !this.shouldVisit(x, y, x + 1, y)) {
                        n = 0;
                    }
                    else if (n != 0 && !this.shouldVisit(x + 1, y, x + 1, y - 1) && !this.push(x + 1, y)) {
                        return;
                    }
                    ++y;
                } while (this.shouldVisit(x, y - 1, x, y));
            }
        }
        
        boolean shouldVisit(final int n, final int n2, final int n3, final int n4) {
            if (this.gridX(n3) >= 11 || this.gridX(n3) < 0) {
                return false;
            }
            if (this.gridY(n4) >= 11 || this.gridY(n4) < 0) {
                return false;
            }
            if (this.visited.getValue(this.gridX(n3), this.gridY(n4))) {
                return false;
            }
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n4, this.start.getZ());
            return gridSquare != null && !gridSquare.Has(IsoObjectType.stairsBN) && !gridSquare.Has(IsoObjectType.stairsMN) && !gridSquare.Has(IsoObjectType.stairsTN) && !gridSquare.Has(IsoObjectType.stairsBW) && !gridSquare.Has(IsoObjectType.stairsMW) && !gridSquare.Has(IsoObjectType.stairsTW) && (gridSquare.getRoom() == null || this.building != null) && (gridSquare.getRoom() != null || this.building == null) && !IsoWorld.instance.CurrentCell.blocked(this.mover, n3, n4, this.start.getZ(), n, n2, this.start.getZ());
        }
        
        boolean push(final int n, final int n2) {
            this.stack.push(IsoWorld.instance.CurrentCell.getGridSquare(n, n2, this.start.getZ()));
            return true;
        }
        
        IsoGridSquare pop() {
            return this.stack.isEmpty() ? null : this.stack.pop();
        }
        
        int gridX(final int n) {
            return n - (this.start.getX() - 5);
        }
        
        int gridY(final int n) {
            return n - (this.start.getY() - 5);
        }
        
        int gridX(final IsoGridSquare isoGridSquare) {
            return isoGridSquare.getX() - (this.start.getX() - 5);
        }
        
        int gridY(final IsoGridSquare isoGridSquare) {
            return isoGridSquare.getY() - (this.start.getY() - 5);
        }
        
        void draw() {
            final int n = this.start.getX() - 5;
            final int n2 = this.start.getY() - 5;
            for (int i = 0; i < 11; ++i) {
                for (int j = 0; j < 11; ++j) {
                    if (this.visited.getValue(j, i)) {
                        final int n3 = (int)IsoUtils.XToScreenExact((float)(n + j), (float)(n2 + i + 1), (float)this.start.getZ(), 0);
                        final int n4 = (int)IsoUtils.YToScreenExact((float)(n + j), (float)(n2 + i + 1), (float)this.start.getZ(), 0);
                        SpriteRenderer.instance.renderPoly((float)n3, (float)n4, (float)(n3 + 32), (float)(n4 - 16), (float)(n3 + 64), (float)n4, (float)(n3 + 32), (float)(n4 + 16), 1.0f, 1.0f, 0.0f, 0.5f);
                    }
                }
            }
        }
    }
    
    public class BooleanDebugOption extends BooleanConfigOption
    {
        public BooleanDebugOption(final String s, final boolean b) {
            super(s, b);
            DebugChunkState.this.options.add(this);
        }
    }
}
