// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.core.PerformanceSettings;
import zombie.core.properties.PropertyContainer;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoBarricade;
import zombie.core.opengl.Shader;
import zombie.IndieGL;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.IsoCell;
import zombie.debug.LineDrawer;
import zombie.WorldSoundManager;
import zombie.core.physics.WorldSimulation;
import zombie.iso.IsoObject;
import zombie.iso.IsoMovingObject;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.IsoUtils;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoCamera;
import zombie.characters.IsoPlayer;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.vehicles.BaseVehicle;
import zombie.core.opengl.RenderThread;
import org.lwjgl.opengl.GL11;
import zombie.iso.IsoObjectPicker;
import zombie.core.textures.TexturePackPage;
import zombie.GameWindow;
import zombie.ui.TextManager;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.gameStates.MainScreenState;
import org.lwjglx.opengl.PixelFormat;
import org.lwjglx.opengl.DisplayMode;
import zombie.core.Core;
import org.lwjglx.opengl.Display;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;

public class ServerGUI
{
    private static boolean created;
    private static int minX;
    private static int minY;
    private static int maxX;
    private static int maxY;
    private static int maxZ;
    private static final ArrayList<IsoGridSquare> GridStack;
    private static final ArrayList<IsoGridSquare> MinusFloorCharacters;
    private static final ArrayList<IsoGridSquare> SolidFloor;
    private static final ArrayList<IsoGridSquare> VegetationCorpses;
    private static final ColorInfo defColorInfo;
    
    public static boolean isCreated() {
        return ServerGUI.created;
    }
    
    public static void init() {
        ServerGUI.created = true;
        try {
            Display.setFullscreen(false);
            Display.setResizable(false);
            Display.setVSyncEnabled(false);
            Display.setTitle("Project Zomboid Server");
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
            Core.width = 1366;
            Core.height = 768;
            Display.setDisplayMode(new DisplayMode(Core.width, Core.height));
            Display.create(new PixelFormat(32, 0, 24, 8, 0));
            Display.setIcon(MainScreenState.loadIcons());
            GLVertexBufferObject.init();
            Display.makeCurrent();
            SpriteRenderer.instance.create();
            TextManager.instance.Init();
            while (TextManager.instance.font.isEmpty()) {
                GameWindow.fileSystem.updateAsyncTransactions();
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex2) {}
            }
            TexturePackPage.bIgnoreWorldItemTextures = true;
            final int n = 2;
            GameWindow.LoadTexturePack("UI", n);
            GameWindow.LoadTexturePack("UI2", n);
            GameWindow.LoadTexturePack("IconsMoveables", n);
            GameWindow.LoadTexturePack("RadioIcons", n);
            GameWindow.LoadTexturePack("ApComUI", n);
            GameWindow.LoadTexturePack("WeatherFx", n);
            TexturePackPage.bIgnoreWorldItemTextures = false;
            final int n2 = 0;
            GameWindow.LoadTexturePack("Tiles2x", n2);
            GameWindow.LoadTexturePack("JumboTrees2x", n2);
            GameWindow.LoadTexturePack("Overlays2x", n2);
            GameWindow.LoadTexturePack("Tiles2x.floor", 0);
            GameWindow.DoLoadingText("");
            GameWindow.setTexturePackLookup();
            IsoObjectPicker.Instance.Init();
            Display.makeCurrent();
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            Display.releaseContext();
            RenderThread.initServerGUI();
            RenderThread.startRendering();
            Core.getInstance().initFBOs();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ServerGUI.created = false;
        }
    }
    
    public static void init2() {
        if (!ServerGUI.created) {
            return;
        }
        BaseVehicle.LoadAllVehicleTextures();
    }
    
    public static void shutdown() {
        if (!ServerGUI.created) {
            return;
        }
        RenderThread.shutdown();
    }
    
    public static void update() {
        if (!ServerGUI.created) {
            return;
        }
        Mouse.update();
        GameKeyboard.update();
        Display.processMessages();
        if (RenderThread.isCloseRequested()) {}
        final int wheelState = Mouse.getWheelState();
        if (wheelState != 0) {
            Core.getInstance().doZoomScroll(0, (wheelState - 0 < 0) ? 1 : -1);
        }
        final int n = 0;
        final IsoPlayer playerToFollow = getPlayerToFollow();
        if (playerToFollow == null) {
            Core.getInstance().StartFrame();
            Core.getInstance().EndFrame();
            Core.getInstance().StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0f, 0.0f, 0.0f, 1.0f, null);
            Core.getInstance().EndFrameUI();
            return;
        }
        IsoPlayer.setInstance(playerToFollow);
        IsoPlayer.players[n] = playerToFollow;
        IsoCamera.CamCharacter = playerToFollow;
        Core.getInstance().StartFrame(n, true);
        renderWorld();
        Core.getInstance().EndFrame(n);
        Core.getInstance().RenderOffScreenBuffer();
        Core.getInstance().StartFrameUI();
        renderUI();
        Core.getInstance().EndFrameUI();
    }
    
    private static IsoPlayer getPlayerToFollow() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.isFullyConnected()) {
                for (int j = 0; j < 4; ++j) {
                    final IsoPlayer isoPlayer = udpConnection.players[j];
                    if (isoPlayer != null && isoPlayer.OnlineID != -1) {
                        return isoPlayer;
                    }
                }
            }
        }
        return null;
    }
    
    private static void updateCamera(final IsoPlayer camCharacter) {
        final int playerIndex = 0;
        final PlayerCamera playerCamera = IsoCamera.cameras[playerIndex];
        final float xToScreen = IsoUtils.XToScreen(camCharacter.x + playerCamera.DeferedX, camCharacter.y + playerCamera.DeferedY, camCharacter.z, 0);
        final float yToScreen = IsoUtils.YToScreen(camCharacter.x + playerCamera.DeferedX, camCharacter.y + playerCamera.DeferedY, camCharacter.z, 0);
        final float n = xToScreen - IsoCamera.getOffscreenWidth(playerIndex) / 2;
        final float n2 = yToScreen - IsoCamera.getOffscreenHeight(playerIndex) / 2 - camCharacter.getOffsetY() * 1.5f;
        final float offX = n + IsoCamera.PLAYER_OFFSET_X;
        final float offY = n2 + IsoCamera.PLAYER_OFFSET_Y;
        playerCamera.OffX = offX;
        playerCamera.OffY = offY;
        final IsoCamera.FrameState frameState = IsoCamera.frameState;
        frameState.Paused = false;
        frameState.playerIndex = playerIndex;
        frameState.CamCharacter = camCharacter;
        frameState.CamCharacterX = IsoCamera.CamCharacter.getX();
        frameState.CamCharacterY = IsoCamera.CamCharacter.getY();
        frameState.CamCharacterZ = IsoCamera.CamCharacter.getZ();
        frameState.CamCharacterSquare = IsoCamera.CamCharacter.getCurrentSquare();
        frameState.CamCharacterRoom = ((frameState.CamCharacterSquare == null) ? null : frameState.CamCharacterSquare.getRoom());
        frameState.OffX = IsoCamera.getOffX();
        frameState.OffY = IsoCamera.getOffY();
        frameState.OffscreenWidth = IsoCamera.getOffscreenWidth(playerIndex);
        frameState.OffscreenHeight = IsoCamera.getOffscreenHeight(playerIndex);
    }
    
    private static void renderWorld() {
        final IsoPlayer playerToFollow = getPlayerToFollow();
        if (playerToFollow == null) {
            return;
        }
        final int n = 0;
        IsoPlayer.setInstance(playerToFollow);
        IsoPlayer.players[0] = playerToFollow;
        updateCamera((IsoPlayer)(IsoCamera.CamCharacter = playerToFollow));
        SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
        SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
        SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
        IsoWorld.instance.sceneCullZombies();
        IsoSprite.globalOffsetX = -1.0f;
        final int n2 = 0;
        final int n3 = 0;
        final int n4 = n2 + IsoCamera.getOffscreenWidth(n);
        final int n5 = n3 + IsoCamera.getOffscreenHeight(n);
        final float xToIso = IsoUtils.XToIso((float)n2, (float)n3, 0.0f);
        final float yToIso = IsoUtils.YToIso((float)n4, (float)n3, 0.0f);
        final float xToIso2 = IsoUtils.XToIso((float)n4, (float)n5, 6.0f);
        final float yToIso2 = IsoUtils.YToIso((float)n2, (float)n5, 6.0f);
        ServerGUI.minY = (int)yToIso;
        ServerGUI.maxY = (int)yToIso2;
        ServerGUI.minX = (int)xToIso;
        ServerGUI.maxX = (int)xToIso2;
        ServerGUI.minX -= 2;
        ServerGUI.minY -= 2;
        ServerGUI.maxZ = (int)playerToFollow.getZ();
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        currentCell.DrawStencilMask();
        IsoObjectPicker.Instance.StartRender();
        RenderTiles();
        for (int i = 0; i < currentCell.getObjectList().size(); ++i) {
            currentCell.getObjectList().get(i).renderlast();
        }
        for (int j = 0; j < currentCell.getStaticUpdaterObjectList().size(); ++j) {
            currentCell.getStaticUpdaterObjectList().get(j).renderlast();
        }
        if (WorldSimulation.instance.created) {
            SpriteRenderer.instance.drawGeneric(WorldSimulation.getDrawer(n));
        }
        WorldSoundManager.instance.render();
        LineDrawer.clear();
    }
    
    private static void RenderTiles() {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        if (IsoCell.perPlayerRender[0] == null) {
            IsoCell.perPlayerRender[0] = new IsoCell.PerPlayerRender();
        }
        final IsoCell.PerPlayerRender perPlayerRender = IsoCell.perPlayerRender[0];
        if (perPlayerRender == null) {
            IsoCell.perPlayerRender[0] = new IsoCell.PerPlayerRender();
        }
        perPlayerRender.setSize(ServerGUI.maxX - ServerGUI.minX + 1, ServerGUI.maxY - ServerGUI.minY + 1);
        final short[][][] stencilValues = perPlayerRender.StencilValues;
        for (int i = 0; i <= ServerGUI.maxZ; ++i) {
            ServerGUI.GridStack.clear();
            for (int j = ServerGUI.minY; j < ServerGUI.maxY; ++j) {
                int k = ServerGUI.minX;
                IsoGridSquare e = ServerMap.instance.getGridSquare(k, j, i);
                final int index = IsoDirections.E.index();
                while (k < ServerGUI.maxX) {
                    if (i == 0) {
                        stencilValues[k - ServerGUI.minX][j - ServerGUI.minY][0] = 0;
                        stencilValues[k - ServerGUI.minX][j - ServerGUI.minY][1] = 0;
                    }
                    if (e != null && e.getY() != j) {
                        e = null;
                    }
                    if (e == null) {
                        e = ServerMap.instance.getGridSquare(k, j, i);
                        if (e == null) {
                            ++k;
                            continue;
                        }
                    }
                    if (e.getChunk() != null && e.IsOnScreen()) {
                        ServerGUI.GridStack.add(e);
                    }
                    e = e.nav[index];
                    ++k;
                }
            }
            ServerGUI.SolidFloor.clear();
            ServerGUI.VegetationCorpses.clear();
            ServerGUI.MinusFloorCharacters.clear();
            for (int l = 0; l < ServerGUI.GridStack.size(); ++l) {
                final IsoGridSquare e2 = ServerGUI.GridStack.get(l);
                e2.setLightInfoServerGUIOnly(ServerGUI.defColorInfo);
                int renderFloor = renderFloor(e2);
                if (!e2.getStaticMovingObjects().isEmpty()) {
                    renderFloor |= 0x2;
                }
                for (int index2 = 0; index2 < e2.getMovingObjects().size(); ++index2) {
                    final IsoMovingObject isoMovingObject = e2.getMovingObjects().get(index2);
                    boolean onFloor = isoMovingObject.isOnFloor();
                    if (onFloor && isoMovingObject instanceof IsoZombie) {
                        final IsoZombie isoZombie = (IsoZombie)isoMovingObject;
                        onFloor = (isoZombie.bCrawling || (isoZombie.legsSprite.CurrentAnim != null && isoZombie.legsSprite.CurrentAnim.name.equals("ZombieDeath") && isoZombie.def.isFinished()));
                    }
                    if (onFloor) {
                        renderFloor |= 0x2;
                    }
                    else {
                        renderFloor |= 0x4;
                    }
                }
                if ((renderFloor & 0x1) != 0x0) {
                    ServerGUI.SolidFloor.add(e2);
                }
                if ((renderFloor & 0x2) != 0x0) {
                    ServerGUI.VegetationCorpses.add(e2);
                }
                if ((renderFloor & 0x4) != 0x0) {
                    ServerGUI.MinusFloorCharacters.add(e2);
                }
            }
            LuaEventManager.triggerEvent("OnPostFloorLayerDraw", i);
            for (int index3 = 0; index3 < ServerGUI.VegetationCorpses.size(); ++index3) {
                final IsoGridSquare isoGridSquare = ServerGUI.VegetationCorpses.get(index3);
                renderMinusFloor(isoGridSquare, false, true);
                renderCharacters(isoGridSquare, true);
            }
            for (int index4 = 0; index4 < ServerGUI.MinusFloorCharacters.size(); ++index4) {
                final IsoGridSquare isoGridSquare2 = ServerGUI.MinusFloorCharacters.get(index4);
                final boolean renderMinusFloor = renderMinusFloor(isoGridSquare2, false, false);
                renderCharacters(isoGridSquare2, false);
                if (renderMinusFloor) {
                    renderMinusFloor(isoGridSquare2, true, false);
                }
            }
        }
        ServerGUI.MinusFloorCharacters.clear();
        ServerGUI.SolidFloor.clear();
        ServerGUI.VegetationCorpses.clear();
    }
    
    private static int renderFloor(final IsoGridSquare isoGridSquare) {
        int n = 0;
        final int n2 = 0;
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            boolean b = true;
            if (isoObject.sprite != null && !isoObject.sprite.Properties.Is(IsoFlagType.solidfloor)) {
                b = false;
                n |= 0x4;
            }
            if (b) {
                IndieGL.glAlphaFunc(516, 0.0f);
                isoObject.setAlphaAndTarget(n2, 1.0f);
                isoObject.render((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo, true, false, null);
                isoObject.renderObjectPicker((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo);
                if ((isoObject.highlightFlags & 0x2) != 0x0) {
                    final IsoObject isoObject2 = isoObject;
                    isoObject2.highlightFlags &= 0xFFFFFFFE;
                }
                n |= 0x1;
            }
            if (!b && isoObject.sprite != null && (isoObject.sprite.Properties.Is(IsoFlagType.canBeRemoved) || isoObject.sprite.Properties.Is(IsoFlagType.attachedFloor))) {
                n |= 0x2;
            }
        }
        return n;
    }
    
    private static boolean isSpriteOnSouthOrEastWall(final IsoObject isoObject) {
        if (isoObject instanceof IsoBarricade) {
            return isoObject.getDir() == IsoDirections.S || isoObject.getDir() == IsoDirections.E;
        }
        if (isoObject instanceof IsoCurtain) {
            final IsoCurtain isoCurtain = (IsoCurtain)isoObject;
            return isoCurtain.getType() == IsoObjectType.curtainS || isoCurtain.getType() == IsoObjectType.curtainE;
        }
        final PropertyContainer properties = isoObject.getProperties();
        return properties != null && (properties.Is(IsoFlagType.attachedE) || properties.Is(IsoFlagType.attachedS));
    }
    
    private static int DoWallLightingN(final IsoGridSquare isoGridSquare, final IsoObject isoObject, final int n) {
        isoObject.render((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo, true, false, null);
        return n;
    }
    
    private static int DoWallLightingW(final IsoGridSquare isoGridSquare, final IsoObject isoObject, final int n) {
        isoObject.render((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo, true, false, null);
        return n;
    }
    
    private static int DoWallLightingNW(final IsoGridSquare isoGridSquare, final IsoObject isoObject, final int n) {
        isoObject.render((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo, true, false, null);
        return n;
    }
    
    private static boolean renderMinusFloor(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2) {
        final int n = b ? (isoGridSquare.getObjects().size() - 1) : 0;
        final int n2 = b ? 0 : (isoGridSquare.getObjects().size() - 1);
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoGridSquare camCharacterSquare = IsoCamera.frameState.CamCharacterSquare;
        final IsoRoom camCharacterRoom = IsoCamera.frameState.CamCharacterRoom;
        final int n3 = (int)(IsoUtils.XToScreenInt(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, 0) - IsoCamera.frameState.OffX);
        final int n4 = (int)(IsoUtils.YToScreenInt(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, 0) - IsoCamera.frameState.OffY);
        boolean circleStencil = true;
        final IsoCell cell = isoGridSquare.getCell();
        if (n3 + 32 * Core.TileScale <= cell.StencilX1 || n3 - 32 * Core.TileScale >= cell.StencilX2 || n4 + 32 * Core.TileScale <= cell.StencilY1 || n4 - 96 * Core.TileScale >= cell.StencilY2) {
            circleStencil = false;
        }
        int n5 = 0;
        boolean b3 = false;
        int n6 = n;
        while (true) {
            if (b) {
                if (n6 < n2) {
                    break;
                }
            }
            else if (n6 > n2) {
                break;
            }
            final IsoObject isoObject = isoGridSquare.getObjects().get(n6);
            boolean b4 = true;
            IsoGridSquare.CircleStencil = false;
            if (isoObject.sprite != null && isoObject.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
                b4 = false;
            }
            Label_1436: {
                if (!b2 || isoObject.sprite == null || isoObject.sprite.Properties.Is(IsoFlagType.canBeRemoved) || isoObject.sprite.Properties.Is(IsoFlagType.attachedFloor)) {
                    if (!b2 && isoObject.sprite != null) {
                        if (isoObject.sprite.Properties.Is(IsoFlagType.canBeRemoved)) {
                            break Label_1436;
                        }
                        if (isoObject.sprite.Properties.Is(IsoFlagType.attachedFloor)) {
                            break Label_1436;
                        }
                    }
                    if (isoObject.sprite != null && (isoObject.sprite.getType() == IsoObjectType.WestRoofB || isoObject.sprite.getType() == IsoObjectType.WestRoofM || isoObject.sprite.getType() == IsoObjectType.WestRoofT) && isoGridSquare.z == ServerGUI.maxZ && isoGridSquare.z == (int)IsoCamera.CamCharacter.getZ()) {
                        b4 = false;
                    }
                    if (IsoCamera.CamCharacter.isClimbing() && isoObject.sprite != null && !isoObject.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
                        b4 = true;
                    }
                    if (isSpriteOnSouthOrEastWall(isoObject)) {
                        if (!b) {
                            b4 = false;
                        }
                        b3 = true;
                    }
                    else if (b) {
                        b4 = false;
                    }
                    if (b4) {
                        IndieGL.glAlphaFunc(516, 0.0f);
                        if (isoObject.sprite != null && !isoGridSquare.getProperties().Is(IsoFlagType.blueprint) && (isoObject.sprite.getType() == IsoObjectType.doorFrW || isoObject.sprite.getType() == IsoObjectType.doorFrN || isoObject.sprite.getType() == IsoObjectType.doorW || isoObject.sprite.getType() == IsoObjectType.doorN || isoObject.sprite.getProperties().Is(IsoFlagType.cutW) || isoObject.sprite.getProperties().Is(IsoFlagType.cutN)) && PerformanceSettings.LightingFrameSkip < 3) {
                            if (isoObject.getTargetAlpha(playerIndex) < 1.0f) {
                                int n7 = 0;
                                if (n7 != 0) {
                                    if (isoObject.sprite.getProperties().Is(IsoFlagType.cutW) && isoGridSquare.getProperties().Is(IsoFlagType.WallSE)) {
                                        final IsoGridSquare isoGridSquare2 = isoGridSquare.nav[IsoDirections.NW.index()];
                                        if (isoGridSquare2 == null || isoGridSquare2.getRoom() == null) {
                                            n7 = 0;
                                        }
                                    }
                                    else if (isoObject.sprite.getType() == IsoObjectType.doorFrW || isoObject.sprite.getType() == IsoObjectType.doorW || isoObject.sprite.getProperties().Is(IsoFlagType.cutW)) {
                                        final IsoGridSquare isoGridSquare3 = isoGridSquare.nav[IsoDirections.W.index()];
                                        if (isoGridSquare3 == null || isoGridSquare3.getRoom() == null) {
                                            n7 = 0;
                                        }
                                    }
                                    else if (isoObject.sprite.getType() == IsoObjectType.doorFrN || isoObject.sprite.getType() == IsoObjectType.doorN || isoObject.sprite.getProperties().Is(IsoFlagType.cutN)) {
                                        final IsoGridSquare isoGridSquare4 = isoGridSquare.nav[IsoDirections.N.index()];
                                        if (isoGridSquare4 == null || isoGridSquare4.getRoom() == null) {
                                            n7 = 0;
                                        }
                                    }
                                }
                                if (n7 == 0) {
                                    IsoGridSquare.CircleStencil = circleStencil;
                                }
                                isoObject.setAlphaAndTarget(playerIndex, 1.0f);
                            }
                            if (isoObject.sprite.getProperties().Is(IsoFlagType.cutW) && isoObject.sprite.getProperties().Is(IsoFlagType.cutN)) {
                                n5 = DoWallLightingNW(isoGridSquare, isoObject, n5);
                            }
                            else if (isoObject.sprite.getType() == IsoObjectType.doorFrW || isoObject.sprite.getType() == IsoObjectType.doorW || isoObject.sprite.getProperties().Is(IsoFlagType.cutW)) {
                                n5 = DoWallLightingW(isoGridSquare, isoObject, n5);
                            }
                            else if (isoObject.sprite.getType() == IsoObjectType.doorFrN || isoObject.sprite.getType() == IsoObjectType.doorN || isoObject.sprite.getProperties().Is(IsoFlagType.cutN)) {
                                n5 = DoWallLightingN(isoGridSquare, isoObject, n5);
                            }
                        }
                        else {
                            if (camCharacterSquare != null) {}
                            isoObject.setTargetAlpha(playerIndex, 1.0f);
                            if (IsoCamera.CamCharacter != null && isoObject.getProperties() != null && (isoObject.getProperties().Is(IsoFlagType.solid) || isoObject.getProperties().Is(IsoFlagType.solidtrans))) {
                                final int n8 = isoGridSquare.getX() - (int)IsoCamera.CamCharacter.getX();
                                final int n9 = isoGridSquare.getY() - (int)IsoCamera.CamCharacter.getY();
                                if ((n8 > 0 && n8 < 3 && n9 >= 0 && n9 < 3) || (n9 > 0 && n9 < 3 && n8 >= 0 && n8 < 3)) {
                                    isoObject.setTargetAlpha(playerIndex, 0.99f);
                                }
                            }
                            if (isoObject instanceof IsoWindow && isoObject.getTargetAlpha(playerIndex) < 1.0E-4f) {
                                final IsoGridSquare oppositeSquare = ((IsoWindow)isoObject).getOppositeSquare();
                                if (oppositeSquare != null && oppositeSquare != isoGridSquare && oppositeSquare.lighting[playerIndex].bSeen()) {
                                    isoObject.setTargetAlpha(playerIndex, oppositeSquare.lighting[playerIndex].darkMulti() * 2.0f);
                                }
                            }
                            if (isoObject instanceof IsoTree) {
                                if (circleStencil && isoGridSquare.x >= (int)IsoCamera.frameState.CamCharacterX && isoGridSquare.y >= (int)IsoCamera.frameState.CamCharacterY && camCharacterSquare != null && camCharacterSquare.Is(IsoFlagType.exterior)) {
                                    ((IsoTree)isoObject).bRenderFlag = true;
                                }
                                else {
                                    ((IsoTree)isoObject).bRenderFlag = false;
                                }
                            }
                            isoObject.render((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo, true, false, null);
                        }
                        if (isoObject.sprite != null) {
                            isoObject.renderObjectPicker((float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, ServerGUI.defColorInfo);
                        }
                        if ((isoObject.highlightFlags & 0x2) != 0x0) {
                            final IsoTree isoTree = (IsoTree)isoObject;
                            isoTree.highlightFlags &= 0xFFFFFFFE;
                        }
                    }
                }
            }
            n6 += (b ? -1 : 1);
        }
        return b3;
    }
    
    private static void renderCharacters(final IsoGridSquare isoGridSquare, final boolean b) {
        for (int size = isoGridSquare.getStaticMovingObjects().size(), i = 0; i < size; ++i) {
            final IsoMovingObject isoMovingObject = isoGridSquare.getStaticMovingObjects().get(i);
            if (isoMovingObject.sprite != null) {
                if (!b || isoMovingObject instanceof IsoDeadBody) {
                    if (b || !(isoMovingObject instanceof IsoDeadBody)) {
                        isoMovingObject.render(isoMovingObject.getX(), isoMovingObject.getY(), isoMovingObject.getZ(), ServerGUI.defColorInfo, true, false, null);
                        isoMovingObject.renderObjectPicker(isoMovingObject.getX(), isoMovingObject.getY(), isoMovingObject.getZ(), ServerGUI.defColorInfo);
                    }
                }
            }
        }
        for (int size2 = isoGridSquare.getMovingObjects().size(), j = 0; j < size2; ++j) {
            final IsoMovingObject isoMovingObject2 = isoGridSquare.getMovingObjects().get(j);
            if (isoMovingObject2 != null) {
                if (isoMovingObject2.sprite != null) {
                    boolean onFloor = isoMovingObject2.isOnFloor();
                    if (onFloor && isoMovingObject2 instanceof IsoZombie) {
                        final IsoZombie isoZombie = (IsoZombie)isoMovingObject2;
                        onFloor = (isoZombie.bCrawling || (isoZombie.legsSprite.CurrentAnim != null && isoZombie.legsSprite.CurrentAnim.name.equals("ZombieDeath") && isoZombie.def.isFinished()));
                    }
                    if (!b || onFloor) {
                        if (b || !onFloor) {
                            isoMovingObject2.setAlphaAndTarget(0, 1.0f);
                            if (isoMovingObject2 instanceof IsoGameCharacter) {
                                ((IsoZombie)isoMovingObject2).renderServerGUI();
                            }
                            else {
                                isoMovingObject2.render(isoMovingObject2.getX(), isoMovingObject2.getY(), isoMovingObject2.getZ(), ServerGUI.defColorInfo, true, false, null);
                            }
                            isoMovingObject2.renderObjectPicker(isoMovingObject2.getX(), isoMovingObject2.getY(), isoMovingObject2.getZ(), ServerGUI.defColorInfo);
                        }
                    }
                }
            }
        }
    }
    
    private static void renderUI() {
    }
    
    static {
        GridStack = new ArrayList<IsoGridSquare>();
        MinusFloorCharacters = new ArrayList<IsoGridSquare>(1000);
        SolidFloor = new ArrayList<IsoGridSquare>(5000);
        VegetationCorpses = new ArrayList<IsoGridSquare>(5000);
        defColorInfo = new ColorInfo();
    }
}
