// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.gameStates.GameLoadingState;
import zombie.util.list.PZArrayUtil;
import zombie.input.GameKeyboard;
import zombie.iso.IsoMovingObject;
import zombie.iso.areas.SafeHouse;
import zombie.iso.IsoWorld;
import zombie.core.BoxedStaticValues;
import java.util.Collection;
import zombie.iso.IsoUtils;
import zombie.core.Styles.TransparentStyle;
import zombie.core.Translator;
import zombie.GameTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameWindow;
import zombie.iso.IsoCamera;
import zombie.input.Mouse;
import zombie.Lua.LuaManager;
import zombie.core.Styles.AbstractStyle;
import zombie.core.Styles.UIFBOStyle;
import zombie.Lua.LuaEventManager;
import zombie.network.CoopMaster;
import zombie.debug.DebugOptions;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.interfaces.ITexture;
import zombie.core.opengl.RenderThread;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import se.krka.kahlua.vm.KahluaThread;
import zombie.iso.Vector2;
import zombie.core.textures.TextureFBO;
import zombie.iso.IsoObject;
import zombie.core.textures.Texture;
import java.util.ArrayList;
import zombie.iso.IsoObjectPicker;

public final class UIManager
{
    public static int lastMouseX;
    public static int lastMouseY;
    public static IsoObjectPicker.ClickObject Picked;
    public static Clock clock;
    public static final ArrayList<UIElement> UI;
    public static ObjectTooltip toolTip;
    public static Texture mouseArrow;
    public static Texture mouseExamine;
    public static Texture mouseAttack;
    public static Texture mouseGrab;
    public static SpeedControls speedControls;
    public static UIDebugConsole DebugConsole;
    public static UIServerToolbox ServerToolbox;
    public static final MoodlesUI[] MoodleUI;
    public static boolean bFadeBeforeUI;
    public static final ActionProgressBar[] ProgressBar;
    public static float FadeAlpha;
    public static int FadeInTimeMax;
    public static int FadeInTime;
    public static boolean FadingOut;
    public static Texture lastMouseTexture;
    public static IsoObject LastPicked;
    public static final ArrayList<String> DoneTutorials;
    public static float lastOffX;
    public static float lastOffY;
    public static ModalDialog Modal;
    public static boolean KeyDownZoomIn;
    public static boolean KeyDownZoomOut;
    public static boolean doTick;
    public static boolean VisibleAllUI;
    public static TextureFBO UIFBO;
    public static boolean useUIFBO;
    public static Texture black;
    public static boolean bSuspend;
    public static float lastAlpha;
    public static final Vector2 PickedTileLocal;
    public static final Vector2 PickedTile;
    public static IsoObject RightDownObject;
    public static long uiUpdateTimeMS;
    public static long uiUpdateIntervalMS;
    public static long uiRenderTimeMS;
    public static long uiRenderIntervalMS;
    private static final ArrayList<UIElement> tutorialStack;
    public static final ArrayList<UIElement> toTop;
    public static KahluaThread defaultthread;
    public static KahluaThread previousThread;
    static final ArrayList<UIElement> toRemove;
    static final ArrayList<UIElement> toAdd;
    static int wheel;
    static int lastwheel;
    static final ArrayList<UIElement> debugUI;
    static boolean bShowLuaDebuggerOnError;
    static final Sync sync;
    private static boolean showPausedMessage;
    private static UIElement playerInventoryUI;
    private static UIElement playerLootUI;
    private static UIElement playerInventoryTooltip;
    private static UIElement playerLootTooltip;
    private static final FadeInfo[] playerFadeInfo;
    
    public static void AddUI(final UIElement uiElement) {
        UIManager.toRemove.remove(uiElement);
        UIManager.toRemove.add(uiElement);
        UIManager.toAdd.remove(uiElement);
        UIManager.toAdd.add(uiElement);
    }
    
    public static void RemoveElement(final UIElement e) {
        UIManager.toAdd.remove(e);
        UIManager.toRemove.remove(e);
        UIManager.toRemove.add(e);
    }
    
    public static void closeContainers() {
    }
    
    public static void CloseContainers() {
    }
    
    public static void DrawTexture(final Texture texture, final double n, final double n2) {
        SpriteRenderer.instance.renderi(texture, (int)(n + texture.offsetX), (int)(n2 + texture.offsetY), texture.getWidth(), texture.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
    }
    
    public static void DrawTexture(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5) {
        SpriteRenderer.instance.renderi(texture, (int)(n + texture.offsetX), (int)(n2 + texture.offsetY), (int)n3, (int)n4, 1.0f, 1.0f, 1.0f, (float)n5, null);
    }
    
    public static void FadeIn(final double n) {
        setFadeInTimeMax((int)(n * 30.0 * (PerformanceSettings.getLockFPS() / 30.0f)));
        setFadeInTime(getFadeInTimeMax());
        setFadingOut(false);
    }
    
    public static void FadeOut(final double n) {
        setFadeInTimeMax((int)(n * 30.0 * (PerformanceSettings.getLockFPS() / 30.0f)));
        setFadeInTime(getFadeInTimeMax());
        setFadingOut(true);
    }
    
    public static void CreateFBO(final int n, final int n2) {
        if (Core.SafeMode) {
            UIManager.useUIFBO = false;
            return;
        }
        if (UIManager.useUIFBO && (UIManager.UIFBO == null || UIManager.UIFBO.getTexture().getWidth() != n || UIManager.UIFBO.getTexture().getHeight() != n2)) {
            if (UIManager.UIFBO != null) {
                RenderThread.invokeOnRenderContext(() -> UIManager.UIFBO.destroy());
            }
            try {
                UIManager.UIFBO = createTexture((float)n, (float)n2, false);
            }
            catch (Exception ex) {
                UIManager.useUIFBO = false;
                ex.printStackTrace();
            }
        }
    }
    
    public static TextureFBO createTexture(final float n, final float n2, final boolean b) throws Exception {
        if (b) {
            new TextureFBO(new Texture((int)n, (int)n2, 16)).destroy();
            return null;
        }
        return new TextureFBO(new Texture((int)n, (int)n2, 16));
    }
    
    public static void init() {
        UIManager.showPausedMessage = true;
        getUI().clear();
        UIManager.debugUI.clear();
        UIManager.clock = null;
        for (int i = 0; i < 4; ++i) {
            UIManager.MoodleUI[i] = null;
        }
        setSpeedControls(new SpeedControls());
        SpeedControls.instance = getSpeedControls();
        setbFadeBeforeUI(false);
        UIManager.VisibleAllUI = true;
        for (int j = 0; j < 4; ++j) {
            UIManager.playerFadeInfo[j].setFadeBeforeUI(false);
            UIManager.playerFadeInfo[j].setFadeTime(0);
            UIManager.playerFadeInfo[j].setFadingOut(false);
        }
        setPicked(null);
        setLastPicked(null);
        UIManager.RightDownObject = null;
        if (IsoPlayer.getInstance() == null) {
            return;
        }
        if (!Core.GameMode.equals("LastStand") && !GameClient.bClient) {
            getUI().add(getSpeedControls());
        }
        if (GameServer.bServer) {
            return;
        }
        setToolTip(new ObjectTooltip());
        if (Core.getInstance().getOptionClockSize() == 2) {
            setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 166, 10));
        }
        else {
            setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 91, 10));
        }
        if (!Core.GameMode.equals("LastStand")) {
            getUI().add(getClock());
        }
        getUI().add(getToolTip());
        setDebugConsole(new UIDebugConsole(20, Core.getInstance().getScreenHeight() - 265));
        setServerToolbox(new UIServerToolbox(100, 200));
        if (Core.bDebug && DebugOptions.instance.UIDebugConsoleStartVisible.getValue()) {
            UIManager.DebugConsole.setVisible(true);
        }
        else {
            UIManager.DebugConsole.setVisible(false);
        }
        if (CoopMaster.instance.isRunning()) {
            UIManager.ServerToolbox.setVisible(true);
        }
        else {
            UIManager.ServerToolbox.setVisible(false);
        }
        for (int k = 0; k < 4; ++k) {
            final MoodlesUI e = new MoodlesUI();
            setMoodleUI(k, e);
            e.setVisible(true);
            getUI().add(e);
        }
        getUI().add(getDebugConsole());
        getUI().add(getServerToolbox());
        setLastMouseTexture(getMouseArrow());
        resize();
        for (int l = 0; l < 4; ++l) {
            final ActionProgressBar e2 = new ActionProgressBar(300, 300);
            e2.setRenderThisPlayerOnly(l);
            setProgressBar(l, e2);
            getUI().add(e2);
            e2.setValue(1.0f);
            e2.setVisible(false);
        }
        UIManager.playerInventoryUI = null;
        UIManager.playerLootUI = null;
        LuaEventManager.triggerEvent("OnCreateUI");
    }
    
    public static void render() {
        if (UIManager.useUIFBO && !Core.getInstance().UIRenderThisFrame) {
            return;
        }
        if (UIManager.bSuspend) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        UIManager.uiRenderIntervalMS = Math.min(currentTimeMillis - UIManager.uiRenderTimeMS, 1000L);
        UIManager.uiRenderTimeMS = currentTimeMillis;
        UIElement.StencilLevel = 0;
        if (UIManager.useUIFBO) {
            SpriteRenderer.instance.setDefaultStyle(UIFBOStyle.instance);
        }
        UITransition.UpdateAll();
        if (getBlack() == null) {
            setBlack(Texture.getSharedTexture("black.png"));
        }
        if (LuaManager.thread == UIManager.defaultthread) {
            LuaEventManager.triggerEvent("OnPreUIDraw");
        }
        Mouse.getXA();
        Mouse.getYA();
        if (isbFadeBeforeUI()) {
            setFadeAlpha(getFadeInTime().floatValue() / getFadeInTimeMax().floatValue());
            if (getFadeAlpha() > 1.0) {
                setFadeAlpha(1.0);
            }
            if (getFadeAlpha() < 0.0) {
                setFadeAlpha(0.0);
            }
            if (isFadingOut()) {
                setFadeAlpha(1.0 - getFadeAlpha());
            }
            if (IsoCamera.CamCharacter != null && getFadeAlpha() > 0.0) {
                DrawTexture(getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), getFadeAlpha());
            }
        }
        setLastAlpha(getFadeAlpha().floatValue());
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null && UIManager.playerFadeInfo[i].isFadeBeforeUI()) {
                UIManager.playerFadeInfo[i].render();
            }
        }
        for (int j = 0; j < getUI().size(); ++j) {
            if (UIManager.UI.get(j).isIgnoreLossControl() || !TutorialManager.instance.StealControl) {
                if (!UIManager.UI.get(j).isFollowGameWorld()) {
                    try {
                        if (getUI().get(j).isDefaultDraw()) {
                            getUI().get(j).render();
                        }
                    }
                    catch (Exception thrown) {
                        Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
                    }
                }
            }
        }
        if (getToolTip() != null) {
            getToolTip().render();
        }
        if (isShowPausedMessage() && GameTime.isGamePaused() && (getModal() == null || !UIManager.Modal.isVisible()) && UIManager.VisibleAllUI) {
            final String text = Translator.getText("IGUI_GamePaused");
            final int n = TextManager.instance.MeasureStringX(UIFont.Small, text) + 32;
            final int lineHeight = TextManager.instance.font.getLineHeight();
            final int n2 = (int)Math.ceil(lineHeight * 1.5);
            SpriteRenderer.instance.renderi(null, Core.getInstance().getScreenWidth() / 2 - n / 2, Core.getInstance().getScreenHeight() / 2 - n2 / 2, n, n2, 0.0f, 0.0f, 0.0f, 0.75f, null);
            TextManager.instance.DrawStringCentre(Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2 - lineHeight / 2, text, 1.0, 1.0, 1.0, 1.0);
        }
        if (!isbFadeBeforeUI()) {
            setFadeAlpha(getFadeInTime() / getFadeInTimeMax());
            if (getFadeAlpha() > 1.0) {
                setFadeAlpha(1.0);
            }
            if (getFadeAlpha() < 0.0) {
                setFadeAlpha(0.0);
            }
            if (isFadingOut()) {
                setFadeAlpha(1.0 - getFadeAlpha());
            }
            if (IsoCamera.CamCharacter != null && getFadeAlpha() > 0.0) {
                DrawTexture(getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), getFadeAlpha());
            }
        }
        for (int k = 0; k < IsoPlayer.numPlayers; ++k) {
            if (IsoPlayer.players[k] != null && !UIManager.playerFadeInfo[k].isFadeBeforeUI()) {
                UIManager.playerFadeInfo[k].render();
            }
        }
        if (LuaManager.thread == UIManager.defaultthread) {
            LuaEventManager.triggerEvent("OnPostUIDraw");
        }
        if (UIManager.useUIFBO) {
            SpriteRenderer.instance.setDefaultStyle(TransparentStyle.instance);
        }
    }
    
    public static void resize() {
        if (UIManager.useUIFBO && UIManager.UIFBO != null) {
            CreateFBO(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
        }
        if (getClock() == null) {
            return;
        }
        setLastOffX((float)Core.getInstance().getScreenWidth());
        setLastOffY((float)Core.getInstance().getScreenHeight());
        for (int i = 0; i < 4; ++i) {
            int screenWidth = Core.getInstance().getScreenWidth();
            final int screenHeight = Core.getInstance().getScreenHeight();
            int n;
            if (!Clock.instance.isVisible()) {
                n = 24;
            }
            else {
                n = 64;
            }
            if ((i == 0 && IsoPlayer.numPlayers > 1) || i == 2) {
                screenWidth /= 2;
            }
            UIManager.MoodleUI[i].setX(screenWidth - 50);
            if ((i == 0 || i == 1) && IsoPlayer.numPlayers > 1) {
                UIManager.MoodleUI[i].setY(n);
            }
            if (i == 2 || i == 3) {
                UIManager.MoodleUI[i].setY(screenHeight / 2 + n);
            }
            UIManager.MoodleUI[i].setVisible(UIManager.VisibleAllUI && IsoPlayer.players[i] != null);
        }
        UIManager.clock.resize();
        if (IsoPlayer.numPlayers == 1) {
            if (Core.getInstance().getOptionClockSize() == 2) {
                UIManager.clock.setX(Core.getInstance().getScreenWidth() - 166);
            }
            else {
                UIManager.clock.setX(Core.getInstance().getScreenWidth() - 91);
            }
        }
        else {
            if (Core.getInstance().getOptionClockSize() == 2) {
                UIManager.clock.setX(Core.getInstance().getScreenWidth() / 2.0f - 83.0f);
            }
            else {
                UIManager.clock.setX(Core.getInstance().getScreenWidth() / 2.0f - 45.5f);
            }
            UIManager.clock.setY(Core.getInstance().getScreenHeight() - 70);
        }
        if (IsoPlayer.numPlayers == 1) {
            UIManager.speedControls.setX(Core.getInstance().getScreenWidth() - 110);
        }
        else {
            UIManager.speedControls.setX(Core.getInstance().getScreenWidth() / 2 - 50);
        }
        if (IsoPlayer.numPlayers == 1 && !UIManager.clock.isVisible()) {
            UIManager.speedControls.setY(UIManager.clock.getY());
        }
        else {
            UIManager.speedControls.setY(UIManager.clock.getY() + UIManager.clock.getHeight() + 6.0);
        }
        UIManager.speedControls.setVisible(UIManager.VisibleAllUI && !IsoPlayer.allPlayersDead());
    }
    
    public static Vector2 getTileFromMouse(final double n, final double n2, final double n3) {
        UIManager.PickedTile.x = IsoUtils.XToIso((float)(n - 0.0), (float)(n2 - 0.0), (float)n3);
        UIManager.PickedTile.y = IsoUtils.YToIso((float)(n - 0.0), (float)(n2 - 0.0), (float)n3);
        UIManager.PickedTileLocal.x = getPickedTile().x - (int)getPickedTile().x;
        UIManager.PickedTileLocal.y = getPickedTile().y - (int)getPickedTile().y;
        UIManager.PickedTile.x = (float)(int)getPickedTile().x;
        UIManager.PickedTile.y = (float)(int)getPickedTile().y;
        return getPickedTile();
    }
    
    public static void update() {
        if (UIManager.bSuspend) {
            return;
        }
        if (!UIManager.toRemove.isEmpty()) {
            UIManager.UI.removeAll(UIManager.toRemove);
        }
        UIManager.toRemove.clear();
        if (!UIManager.toAdd.isEmpty()) {
            UIManager.UI.addAll(UIManager.toAdd);
        }
        UIManager.toAdd.clear();
        setFadeInTime(getFadeInTime() - 1.0);
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            UIManager.playerFadeInfo[i].update();
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - UIManager.uiUpdateTimeMS >= 100L) {
            UIManager.doTick = true;
            UIManager.uiUpdateIntervalMS = Math.min(currentTimeMillis - UIManager.uiUpdateTimeMS, 1000L);
            UIManager.uiUpdateTimeMS = currentTimeMillis;
        }
        else {
            UIManager.doTick = false;
        }
        boolean b = false;
        boolean b2 = false;
        int n = 0;
        final int xa = Mouse.getXA();
        final int ya = Mouse.getYA();
        final int x = Mouse.getX();
        final int y = Mouse.getY();
        UIManager.tutorialStack.clear();
        for (int j = UIManager.UI.size() - 1; j >= 0; --j) {
            final UIElement e = UIManager.UI.get(j);
            if (e.getParent() != null) {
                UIManager.UI.remove(j);
                throw new IllegalStateException();
            }
            if (e.isFollowGameWorld()) {
                UIManager.tutorialStack.add(e);
            }
            if (e instanceof ObjectTooltip) {
                UIManager.UI.add(UIManager.UI.remove(j));
            }
        }
        for (int k = 0; k < UIManager.UI.size(); ++k) {
            final UIElement o = UIManager.UI.get(k);
            if (o.alwaysOnTop || UIManager.toTop.contains(o)) {
                final UIElement e2 = UIManager.UI.remove(k);
                --k;
                UIManager.toAdd.add(e2);
            }
        }
        if (!UIManager.toAdd.isEmpty()) {
            UIManager.UI.addAll(UIManager.toAdd);
            UIManager.toAdd.clear();
        }
        UIManager.toTop.clear();
        for (int l = 0; l < UIManager.UI.size(); ++l) {
            if (UIManager.UI.get(l).alwaysBack) {
                UIManager.UI.add(0, UIManager.UI.remove(l));
            }
        }
        for (int n2 = 0; n2 < UIManager.tutorialStack.size(); ++n2) {
            UIManager.UI.remove(UIManager.tutorialStack.get(n2));
            UIManager.UI.add(0, UIManager.tutorialStack.get(n2));
        }
        if (Mouse.isLeftPressed()) {
            Core.UnfocusActiveTextEntryBox();
            for (int index = UIManager.UI.size() - 1; index >= 0; --index) {
                final UIElement uiElement = UIManager.UI.get(index);
                if (getModal() == null || getModal() == uiElement || !getModal().isVisible()) {
                    if (uiElement.isIgnoreLossControl() || !TutorialManager.instance.StealControl) {
                        if (uiElement.isVisible()) {
                            if ((xa >= uiElement.getX() && ya >= uiElement.getY() && xa < uiElement.getX() + uiElement.getWidth() && ya < uiElement.getY() + uiElement.getHeight()) || uiElement.isCapture()) {
                                if (uiElement.onMouseDown(xa - uiElement.getX().intValue(), ya - uiElement.getY().intValue())) {
                                    b = true;
                                    break;
                                }
                            }
                            else {
                                uiElement.onMouseDownOutside(xa - uiElement.getX().intValue(), ya - uiElement.getY().intValue());
                            }
                        }
                    }
                }
            }
            if (checkPicked() && !b) {
                LuaEventManager.triggerEvent("OnObjectLeftMouseButtonDown", UIManager.Picked.tile, BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
            }
            if (!b) {
                LuaEventManager.triggerEvent("OnMouseDown", BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                CloseContainers();
                if (IsoWorld.instance.CurrentCell != null && !IsoWorld.instance.CurrentCell.DoBuilding(0, false) && getPicked() != null && !GameTime.isGamePaused() && IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().isAiming() && !IsoPlayer.getInstance().isAsleep()) {
                    getPicked().tile.onMouseLeftClick(getPicked().lx, getPicked().ly);
                }
            }
            else {
                Mouse.UIBlockButtonDown(0);
            }
        }
        if (Mouse.isLeftReleased()) {
            boolean b3 = false;
            for (int index2 = UIManager.UI.size() - 1; index2 >= 0; --index2) {
                final UIElement uiElement2 = UIManager.UI.get(index2);
                if (uiElement2.isIgnoreLossControl() || !TutorialManager.instance.StealControl) {
                    if (uiElement2.isVisible()) {
                        if (getModal() == null || getModal() == uiElement2 || !getModal().isVisible()) {
                            if ((xa >= uiElement2.getX() && ya >= uiElement2.getY() && xa < uiElement2.getX() + uiElement2.getWidth() && ya < uiElement2.getY() + uiElement2.getHeight()) || uiElement2.isCapture()) {
                                if (uiElement2.onMouseUp(xa - uiElement2.getX().intValue(), ya - uiElement2.getY().intValue())) {
                                    b3 = true;
                                    break;
                                }
                            }
                            else {
                                uiElement2.onMouseUpOutside(xa - uiElement2.getX().intValue(), ya - uiElement2.getY().intValue());
                            }
                        }
                    }
                }
            }
            if (!b3) {
                LuaEventManager.triggerEvent("OnMouseUp", BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                if (checkPicked() && !b) {
                    LuaEventManager.triggerEvent("OnObjectLeftMouseButtonUp", UIManager.Picked.tile, BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                }
            }
        }
        if (Mouse.isRightPressed()) {
            for (int index3 = UIManager.UI.size() - 1; index3 >= 0; --index3) {
                final UIElement uiElement3 = UIManager.UI.get(index3);
                if (uiElement3.isVisible()) {
                    if (getModal() == null || getModal() == uiElement3 || !getModal().isVisible()) {
                        if ((xa >= uiElement3.getX() && ya >= uiElement3.getY() && xa < uiElement3.getX() + uiElement3.getWidth() && ya < uiElement3.getY() + uiElement3.getHeight()) || uiElement3.isCapture()) {
                            if (uiElement3.onRightMouseDown(xa - uiElement3.getX().intValue(), ya - uiElement3.getY().intValue())) {
                                b2 = true;
                                break;
                            }
                        }
                        else {
                            uiElement3.onRightMouseDownOutside(xa - uiElement3.getX().intValue(), ya - uiElement3.getY().intValue());
                        }
                    }
                }
            }
            if (!b2) {
                LuaEventManager.triggerEvent("OnRightMouseDown", BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                if (checkPicked() && !b2) {
                    LuaEventManager.triggerEvent("OnObjectRightMouseButtonDown", UIManager.Picked.tile, BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                }
            }
            else {
                Mouse.UIBlockButtonDown(1);
            }
            if (IsoWorld.instance.CurrentCell != null && getPicked() != null && getSpeedControls() != null && !IsoPlayer.getInstance().isAiming() && !IsoPlayer.getInstance().isAsleep() && !GameTime.isGamePaused()) {
                getSpeedControls().SetCurrentGameSpeed(1);
                getPicked().tile.onMouseRightClick(getPicked().lx, getPicked().ly);
                setRightDownObject(getPicked().tile);
            }
        }
        if (Mouse.isRightReleased()) {
            boolean b4 = false;
            for (int index4 = UIManager.UI.size() - 1; index4 >= 0; --index4) {
                final UIElement uiElement4 = UIManager.UI.get(index4);
                if (uiElement4.isIgnoreLossControl() || !TutorialManager.instance.StealControl) {
                    if (uiElement4.isVisible()) {
                        if (getModal() == null || getModal() == uiElement4 || !getModal().isVisible()) {
                            if ((xa >= uiElement4.getX() && ya >= uiElement4.getY() && xa < uiElement4.getX() + uiElement4.getWidth() && ya < uiElement4.getY() + uiElement4.getHeight()) || uiElement4.isCapture()) {
                                if (uiElement4.onRightMouseUp(xa - uiElement4.getX().intValue(), ya - uiElement4.getY().intValue())) {
                                    b4 = true;
                                    break;
                                }
                            }
                            else {
                                uiElement4.onRightMouseUpOutside(xa - uiElement4.getX().intValue(), ya - uiElement4.getY().intValue());
                            }
                        }
                    }
                }
            }
            if (!b4) {
                LuaEventManager.triggerEvent("OnRightMouseUp", BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                if (checkPicked()) {
                    boolean b5 = true;
                    if (GameClient.bClient && UIManager.Picked.tile.getSquare() != null && SafeHouse.isSafeHouse(UIManager.Picked.tile.getSquare(), IsoPlayer.getInstance().getUsername(), true) != null) {
                        b5 = false;
                    }
                    if (b5) {
                        LuaEventManager.triggerEvent("OnObjectRightMouseButtonUp", UIManager.Picked.tile, BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya));
                    }
                }
            }
            if (IsoPlayer.getInstance() != null) {
                IsoPlayer.getInstance().setDragObject(null);
            }
            if (IsoWorld.instance.CurrentCell != null && getRightDownObject() != null && IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().IsAiming() && !IsoPlayer.getInstance().isAsleep()) {
                getRightDownObject().onMouseRightReleased();
                setRightDownObject(null);
            }
        }
        UIManager.lastwheel = 0;
        UIManager.wheel = Mouse.getWheelState();
        boolean b6 = false;
        if (UIManager.wheel != UIManager.lastwheel) {
            final int n3 = (UIManager.wheel - UIManager.lastwheel < 0) ? 1 : -1;
            for (int index5 = UIManager.UI.size() - 1; index5 >= 0; --index5) {
                final UIElement uiElement5 = UIManager.UI.get(index5);
                if (uiElement5.isIgnoreLossControl() || !TutorialManager.instance.StealControl) {
                    if (uiElement5.isVisible()) {
                        if ((uiElement5.isPointOver(xa, ya) || uiElement5.isCapture()) && uiElement5.onMouseWheel(n3)) {
                            b6 = true;
                            break;
                        }
                    }
                }
            }
            if (!b6) {
                Core.getInstance().doZoomScroll(0, n3);
            }
        }
        if (getLastMouseX() != xa || getLastMouseY() != ya) {
            for (int index6 = UIManager.UI.size() - 1; index6 >= 0; --index6) {
                final UIElement uiElement6 = UIManager.UI.get(index6);
                if (uiElement6.isIgnoreLossControl() || !TutorialManager.instance.StealControl) {
                    if (uiElement6.isVisible()) {
                        if ((xa >= uiElement6.getX() && ya >= uiElement6.getY() && xa < uiElement6.getX() + uiElement6.getWidth() && ya < uiElement6.getY() + uiElement6.getHeight()) || uiElement6.isCapture()) {
                            if (n == 0 && uiElement6.onMouseMove(xa - getLastMouseX(), ya - getLastMouseY())) {
                                n = 1;
                            }
                        }
                        else {
                            uiElement6.onMouseMoveOutside(xa - getLastMouseX(), ya - getLastMouseY());
                        }
                    }
                }
            }
        }
        if (n == 0 && IsoPlayer.players[0] != null) {
            setPicked(IsoObjectPicker.Instance.ContextPick(xa, ya));
            if (IsoCamera.CamCharacter != null) {
                setPickedTile(getTileFromMouse(x, y, (int)IsoPlayer.players[0].getZ()));
            }
            LuaEventManager.triggerEvent("OnMouseMove", BoxedStaticValues.toDouble(xa), BoxedStaticValues.toDouble(ya), BoxedStaticValues.toDouble(x), BoxedStaticValues.toDouble(y));
        }
        else {
            Mouse.UIBlockButtonDown(2);
        }
        setLastMouseX(xa);
        setLastMouseY(ya);
        for (int index7 = 0; index7 < UIManager.UI.size(); ++index7) {
            UIManager.UI.get(index7).update();
        }
        updateTooltip(xa, ya);
        handleZoomKeys();
        IsoCamera.cameras[0].lastOffX = (float)(int)IsoCamera.cameras[0].OffX;
        IsoCamera.cameras[0].lastOffY = (float)(int)IsoCamera.cameras[0].OffY;
    }
    
    private static boolean checkPicked() {
        return UIManager.Picked != null && UIManager.Picked.tile != null && UIManager.Picked.tile.getObjectIndex() != -1;
    }
    
    private static void handleZoomKeys() {
        boolean b = true;
        if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.IsEditable && Core.CurrentTextEntryBox.DoingTextEntry) {
            b = false;
        }
        if (GameTime.isGamePaused()) {
            b = false;
        }
        if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Zoom in"))) {
            if (b && !UIManager.KeyDownZoomIn) {
                Core.getInstance().doZoomScroll(0, -1);
            }
            UIManager.KeyDownZoomIn = true;
        }
        else {
            UIManager.KeyDownZoomIn = false;
        }
        if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Zoom out"))) {
            if (b && !UIManager.KeyDownZoomOut) {
                Core.getInstance().doZoomScroll(0, 1);
            }
            UIManager.KeyDownZoomOut = true;
        }
        else {
            UIManager.KeyDownZoomOut = false;
        }
    }
    
    public static Double getLastMouseX() {
        return BoxedStaticValues.toDouble(UIManager.lastMouseX);
    }
    
    public static void setLastMouseX(final double n) {
        UIManager.lastMouseX = (int)n;
    }
    
    public static Double getLastMouseY() {
        return BoxedStaticValues.toDouble(UIManager.lastMouseY);
    }
    
    public static void setLastMouseY(final double n) {
        UIManager.lastMouseY = (int)n;
    }
    
    public static IsoObjectPicker.ClickObject getPicked() {
        return UIManager.Picked;
    }
    
    public static void setPicked(final IsoObjectPicker.ClickObject picked) {
        UIManager.Picked = picked;
    }
    
    public static Clock getClock() {
        return UIManager.clock;
    }
    
    public static void setClock(final Clock clock) {
        UIManager.clock = clock;
    }
    
    public static ArrayList<UIElement> getUI() {
        return UIManager.UI;
    }
    
    public static void setUI(final ArrayList<UIElement> list) {
        PZArrayUtil.copy(UIManager.UI, list);
    }
    
    public static ObjectTooltip getToolTip() {
        return UIManager.toolTip;
    }
    
    public static void setToolTip(final ObjectTooltip toolTip) {
        UIManager.toolTip = toolTip;
    }
    
    public static Texture getMouseArrow() {
        return UIManager.mouseArrow;
    }
    
    public static void setMouseArrow(final Texture mouseArrow) {
        UIManager.mouseArrow = mouseArrow;
    }
    
    public static Texture getMouseExamine() {
        return UIManager.mouseExamine;
    }
    
    public static void setMouseExamine(final Texture mouseExamine) {
        UIManager.mouseExamine = mouseExamine;
    }
    
    public static Texture getMouseAttack() {
        return UIManager.mouseAttack;
    }
    
    public static void setMouseAttack(final Texture mouseAttack) {
        UIManager.mouseAttack = mouseAttack;
    }
    
    public static Texture getMouseGrab() {
        return UIManager.mouseGrab;
    }
    
    public static void setMouseGrab(final Texture mouseGrab) {
        UIManager.mouseGrab = mouseGrab;
    }
    
    public static SpeedControls getSpeedControls() {
        return UIManager.speedControls;
    }
    
    public static void setSpeedControls(final SpeedControls speedControls) {
        UIManager.speedControls = speedControls;
    }
    
    public static UIDebugConsole getDebugConsole() {
        return UIManager.DebugConsole;
    }
    
    public static void setDebugConsole(final UIDebugConsole debugConsole) {
        UIManager.DebugConsole = debugConsole;
    }
    
    public static UIServerToolbox getServerToolbox() {
        return UIManager.ServerToolbox;
    }
    
    public static void setServerToolbox(final UIServerToolbox serverToolbox) {
        UIManager.ServerToolbox = serverToolbox;
    }
    
    public static MoodlesUI getMoodleUI(final double n) {
        return UIManager.MoodleUI[(int)n];
    }
    
    public static void setMoodleUI(final double n, final MoodlesUI moodlesUI) {
        UIManager.MoodleUI[(int)n] = moodlesUI;
    }
    
    public static boolean isbFadeBeforeUI() {
        return UIManager.bFadeBeforeUI;
    }
    
    public static void setbFadeBeforeUI(final boolean bFadeBeforeUI) {
        UIManager.bFadeBeforeUI = bFadeBeforeUI;
    }
    
    public static ActionProgressBar getProgressBar(final double n) {
        return UIManager.ProgressBar[(int)n];
    }
    
    public static void setProgressBar(final double n, final ActionProgressBar actionProgressBar) {
        UIManager.ProgressBar[(int)n] = actionProgressBar;
    }
    
    public static Double getFadeAlpha() {
        return BoxedStaticValues.toDouble(UIManager.FadeAlpha);
    }
    
    public static void setFadeAlpha(final double n) {
        UIManager.FadeAlpha = (float)n;
    }
    
    public static Double getFadeInTimeMax() {
        return BoxedStaticValues.toDouble(UIManager.FadeInTimeMax);
    }
    
    public static void setFadeInTimeMax(final double n) {
        UIManager.FadeInTimeMax = (int)n;
    }
    
    public static Double getFadeInTime() {
        return BoxedStaticValues.toDouble(UIManager.FadeInTime);
    }
    
    public static void setFadeInTime(final double n) {
        UIManager.FadeInTime = Math.max((int)n, 0);
    }
    
    public static Boolean isFadingOut() {
        return UIManager.FadingOut ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static void setFadingOut(final boolean fadingOut) {
        UIManager.FadingOut = fadingOut;
    }
    
    public static Texture getLastMouseTexture() {
        return UIManager.lastMouseTexture;
    }
    
    public static void setLastMouseTexture(final Texture lastMouseTexture) {
        UIManager.lastMouseTexture = lastMouseTexture;
    }
    
    public static IsoObject getLastPicked() {
        return UIManager.LastPicked;
    }
    
    public static void setLastPicked(final IsoObject lastPicked) {
        UIManager.LastPicked = lastPicked;
    }
    
    public static ArrayList<String> getDoneTutorials() {
        return UIManager.DoneTutorials;
    }
    
    public static void setDoneTutorials(final ArrayList<String> list) {
        PZArrayUtil.copy(UIManager.DoneTutorials, list);
    }
    
    public static float getLastOffX() {
        return UIManager.lastOffX;
    }
    
    public static void setLastOffX(final float lastOffX) {
        UIManager.lastOffX = lastOffX;
    }
    
    public static float getLastOffY() {
        return UIManager.lastOffY;
    }
    
    public static void setLastOffY(final float lastOffY) {
        UIManager.lastOffY = lastOffY;
    }
    
    public static ModalDialog getModal() {
        return UIManager.Modal;
    }
    
    public static void setModal(final ModalDialog modal) {
        UIManager.Modal = modal;
    }
    
    public static Texture getBlack() {
        return UIManager.black;
    }
    
    public static void setBlack(final Texture black) {
        UIManager.black = black;
    }
    
    public static float getLastAlpha() {
        return UIManager.lastAlpha;
    }
    
    public static void setLastAlpha(final float lastAlpha) {
        UIManager.lastAlpha = lastAlpha;
    }
    
    public static Vector2 getPickedTileLocal() {
        return UIManager.PickedTileLocal;
    }
    
    public static void setPickedTileLocal(final Vector2 vector2) {
        UIManager.PickedTileLocal.set(vector2);
    }
    
    public static Vector2 getPickedTile() {
        return UIManager.PickedTile;
    }
    
    public static void setPickedTile(final Vector2 vector2) {
        UIManager.PickedTile.set(vector2);
    }
    
    public static IsoObject getRightDownObject() {
        return UIManager.RightDownObject;
    }
    
    public static void setRightDownObject(final IsoObject rightDownObject) {
        UIManager.RightDownObject = rightDownObject;
    }
    
    static void pushToTop(final UIElement e) {
        UIManager.toTop.add(e);
    }
    
    public static boolean isShowPausedMessage() {
        return UIManager.showPausedMessage;
    }
    
    public static void setShowPausedMessage(final boolean showPausedMessage) {
        UIManager.showPausedMessage = showPausedMessage;
    }
    
    public static void setShowLuaDebuggerOnError(final boolean bShowLuaDebuggerOnError) {
        UIManager.bShowLuaDebuggerOnError = bShowLuaDebuggerOnError;
    }
    
    public static boolean isShowLuaDebuggerOnError() {
        return UIManager.bShowLuaDebuggerOnError;
    }
    
    public static void debugBreakpoint(final String s, final long n) {
        if (!UIManager.bShowLuaDebuggerOnError) {
            return;
        }
        if (Core.CurrentTextEntryBox != null) {
            Core.CurrentTextEntryBox.DoingTextEntry = false;
            Core.CurrentTextEntryBox = null;
        }
        if (GameServer.bServer) {
            return;
        }
        if (GameWindow.states.current instanceof GameLoadingState) {
            return;
        }
        UIManager.previousThread = UIManager.defaultthread;
        UIManager.defaultthread = LuaManager.debugthread;
        final int frameStage = Core.getInstance().frameStage;
        if (frameStage != 0) {
            if (frameStage <= 1) {
                Core.getInstance().EndFrame(0);
            }
            if (frameStage <= 2) {
                Core.getInstance().StartFrameUI();
            }
            if (frameStage <= 3) {
                Core.getInstance().EndFrameUI();
            }
        }
        LuaManager.thread.bStep = false;
        LuaManager.thread.bStepInto = false;
        final ArrayList<UIElement> list = new ArrayList<UIElement>();
        final boolean bSuspend = UIManager.bSuspend;
        list.addAll(UIManager.UI);
        UIManager.UI.clear();
        setShowPausedMessage(UIManager.bSuspend = false);
        final boolean b = false;
        final boolean[] array = new boolean[11];
        for (int i = 0; i < 11; ++i) {
            array[i] = true;
        }
        if (UIManager.debugUI.size() == 0) {
            LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget((Object)"DoLuaDebugger"), new Object[] { s, n });
        }
        else {
            UIManager.UI.addAll(UIManager.debugUI);
            LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget((Object)"DoLuaDebuggerOnBreak"), new Object[] { s, n });
        }
        Mouse.setCursorVisible(true);
        UIManager.sync.begin();
        while (!b) {
            if (RenderThread.isCloseRequested()) {
                System.exit(0);
            }
            if (!GameWindow.bLuaDebuggerKeyDown && GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
                GameWindow.bLuaDebuggerKeyDown = true;
                executeGame(list, bSuspend, frameStage);
                return;
            }
            UIManager.sync.startFrame();
            for (int j = 0; j < 11; ++j) {
                if (GameKeyboard.isKeyDown(59 + j)) {
                    if (!array[j]) {
                        if (j + 1 == 5) {
                            LuaManager.thread.bStep = true;
                            LuaManager.thread.bStepInto = true;
                            executeGame(list, bSuspend, frameStage);
                            return;
                        }
                        if (j + 1 == 6) {
                            LuaManager.thread.bStep = true;
                            LuaManager.thread.bStepInto = false;
                            LuaManager.thread.lastCallFrame = LuaManager.thread.getCurrentCoroutine().getCallframeTop();
                            executeGame(list, bSuspend, frameStage);
                            return;
                        }
                    }
                    array[j] = true;
                }
                else {
                    array[j] = false;
                }
            }
            Mouse.update();
            GameKeyboard.update();
            Core.getInstance().DoFrameReady();
            update();
            Core.getInstance().StartFrame(0, true);
            Core.getInstance().EndFrame(0);
            Core.getInstance().RenderOffScreenBuffer();
            if (Core.getInstance().StartFrameUI()) {
                render();
            }
            Core.getInstance().EndFrameUI();
            resize();
            if (!GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
                GameWindow.bLuaDebuggerKeyDown = false;
            }
            UIManager.sync.endFrame();
            Core.getInstance().setScreenSize(RenderThread.getDisplayWidth(), RenderThread.getDisplayHeight());
        }
    }
    
    private static void executeGame(final ArrayList<UIElement> c, final boolean bSuspend, final int n) {
        UIManager.debugUI.clear();
        UIManager.debugUI.addAll(UIManager.UI);
        UIManager.UI.clear();
        UIManager.UI.addAll(c);
        UIManager.bSuspend = bSuspend;
        setShowPausedMessage(true);
        if (!LuaManager.thread.bStep && n != 0) {
            if (n == 1) {
                Core.getInstance().StartFrame(0, true);
            }
            if (n == 2) {
                Core.getInstance().StartFrame(0, true);
                Core.getInstance().EndFrame(0);
            }
            if (n == 3) {
                Core.getInstance().StartFrame(0, true);
                Core.getInstance().EndFrame(0);
                Core.getInstance().StartFrameUI();
            }
        }
        UIManager.defaultthread = UIManager.previousThread;
    }
    
    public static KahluaThread getDefaultThread() {
        if (UIManager.defaultthread == null) {
            UIManager.defaultthread = LuaManager.thread;
        }
        return UIManager.defaultthread;
    }
    
    public static Double getDoubleClickInterval() {
        return BoxedStaticValues.toDouble(500.0);
    }
    
    public static Double getDoubleClickDist() {
        return BoxedStaticValues.toDouble(5.0);
    }
    
    public static Boolean isDoubleClick(final double n, final double n2, final double n3, final double n4, final double n5) {
        if (Math.abs(n3 - n) > getDoubleClickDist()) {
            return false;
        }
        if (Math.abs(n4 - n2) > getDoubleClickDist()) {
            return false;
        }
        if (System.currentTimeMillis() - n5 > getDoubleClickInterval()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    protected static void updateTooltip(final double n, final double n2) {
        ObjectTooltip objectTooltip = null;
        for (int i = getUI().size() - 1; i >= 0; --i) {
            final UIElement uiElement = getUI().get(i);
            if (uiElement != UIManager.toolTip) {
                if (uiElement.isVisible()) {
                    if (n >= uiElement.getX() && n2 >= uiElement.getY() && n < uiElement.getX() + uiElement.getWidth() && n2 < uiElement.getY() + uiElement.getHeight() && (uiElement.maxDrawHeight == -1 || n2 < uiElement.getY() + uiElement.maxDrawHeight)) {
                        objectTooltip = (ObjectTooltip)uiElement;
                        break;
                    }
                }
            }
        }
        IsoObject tile = null;
        if (objectTooltip == null && getPicked() != null) {
            tile = getPicked().tile;
            if (tile != getLastPicked() && UIManager.toolTip != null) {
                UIManager.toolTip.targetAlpha = 0.0f;
                if (tile.haveSpecialTooltip()) {
                    if (getToolTip().Object != tile) {
                        getToolTip().show(tile, (int)n + 8, (int)n2 + 16);
                        if (UIManager.toolTip.isVisible()) {
                            UIManager.toolTip.showDelay = 0;
                        }
                    }
                    else {
                        UIManager.toolTip.targetAlpha = 1.0f;
                    }
                }
            }
        }
        setLastPicked(tile);
        if (UIManager.toolTip != null && (tile == null || (UIManager.toolTip.alpha <= 0.0f && UIManager.toolTip.targetAlpha <= 0.0f))) {
            UIManager.toolTip.hide();
        }
    }
    
    public static void setPlayerInventory(final int n, final UIElement playerInventoryUI, final UIElement playerLootUI) {
        if (n != 0) {
            return;
        }
        UIManager.playerInventoryUI = playerInventoryUI;
        UIManager.playerLootUI = playerLootUI;
    }
    
    public static void setPlayerInventoryTooltip(final int n, final UIElement playerInventoryTooltip, final UIElement playerLootTooltip) {
        if (n != 0) {
            return;
        }
        UIManager.playerInventoryTooltip = playerInventoryTooltip;
        UIManager.playerLootTooltip = playerLootTooltip;
    }
    
    public static boolean isMouseOverInventory() {
        return (UIManager.playerInventoryTooltip != null && UIManager.playerInventoryTooltip.isMouseOver()) || (UIManager.playerLootTooltip != null && UIManager.playerLootTooltip.isMouseOver()) || (UIManager.playerInventoryUI != null && UIManager.playerLootUI != null && ((UIManager.playerInventoryUI.getMaxDrawHeight() == -1.0 && UIManager.playerInventoryUI.isMouseOver()) || (UIManager.playerLootUI.getMaxDrawHeight() == -1.0 && UIManager.playerLootUI.isMouseOver())));
    }
    
    public static void updateBeforeFadeOut() {
        if (!UIManager.toRemove.isEmpty()) {
            UIManager.UI.removeAll(UIManager.toRemove);
            UIManager.toRemove.clear();
        }
        if (!UIManager.toAdd.isEmpty()) {
            UIManager.UI.addAll(UIManager.toAdd);
            UIManager.toAdd.clear();
        }
    }
    
    public static void setVisibleAllUI(final boolean visibleAllUI) {
        UIManager.VisibleAllUI = visibleAllUI;
    }
    
    public static void setFadeBeforeUI(final int n, final boolean fadeBeforeUI) {
        UIManager.playerFadeInfo[n].setFadeBeforeUI(fadeBeforeUI);
    }
    
    public static float getFadeAlpha(final double n) {
        return UIManager.playerFadeInfo[(int)n].getFadeAlpha();
    }
    
    public static void setFadeTime(final double n, final double n2) {
        UIManager.playerFadeInfo[(int)n].setFadeTime((int)n2);
    }
    
    public static void FadeIn(final double n, final double n2) {
        UIManager.playerFadeInfo[(int)n].FadeIn((int)n2);
    }
    
    public static void FadeOut(final double n, final double n2) {
        UIManager.playerFadeInfo[(int)n].FadeOut((int)n2);
    }
    
    public static boolean isFBOActive() {
        return UIManager.useUIFBO;
    }
    
    public static double getMillisSinceLastUpdate() {
        return (double)UIManager.uiUpdateIntervalMS;
    }
    
    public static double getSecondsSinceLastUpdate() {
        return UIManager.uiUpdateIntervalMS / 1000.0;
    }
    
    public static double getMillisSinceLastRender() {
        return (double)UIManager.uiRenderIntervalMS;
    }
    
    public static double getSecondsSinceLastRender() {
        return UIManager.uiRenderIntervalMS / 1000.0;
    }
    
    public static boolean onKeyPress(final int n) {
        for (int i = UIManager.UI.size() - 1; i >= 0; --i) {
            final UIElement uiElement = UIManager.UI.get(i);
            if (uiElement.isVisible() && uiElement.isWantKeyEvents()) {
                uiElement.onKeyPress(n);
                if (uiElement.isKeyConsumed(n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean onKeyRepeat(final int n) {
        for (int i = UIManager.UI.size() - 1; i >= 0; --i) {
            final UIElement uiElement = UIManager.UI.get(i);
            if (uiElement.isVisible() && uiElement.isWantKeyEvents()) {
                uiElement.onKeyRepeat(n);
                if (uiElement.isKeyConsumed(n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean onKeyRelease(final int n) {
        for (int i = UIManager.UI.size() - 1; i >= 0; --i) {
            final UIElement uiElement = UIManager.UI.get(i);
            if (uiElement.isVisible() && uiElement.isWantKeyEvents()) {
                uiElement.onKeyRelease(n);
                if (uiElement.isKeyConsumed(n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isForceCursorVisible() {
        for (int i = UIManager.UI.size() - 1; i >= 0; --i) {
            final UIElement uiElement = UIManager.UI.get(i);
            if (uiElement.isVisible() && (uiElement.isForceCursorVisible() || uiElement.isMouseOver())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        UIManager.lastMouseX = 0;
        UIManager.lastMouseY = 0;
        UIManager.Picked = null;
        UI = new ArrayList<UIElement>();
        UIManager.toolTip = null;
        MoodleUI = new MoodlesUI[4];
        UIManager.bFadeBeforeUI = false;
        ProgressBar = new ActionProgressBar[4];
        UIManager.FadeAlpha = 1.0f;
        UIManager.FadeInTimeMax = 180;
        UIManager.FadeInTime = 180;
        UIManager.FadingOut = false;
        UIManager.LastPicked = null;
        DoneTutorials = new ArrayList<String>();
        UIManager.lastOffX = 0.0f;
        UIManager.lastOffY = 0.0f;
        UIManager.Modal = null;
        UIManager.KeyDownZoomIn = false;
        UIManager.KeyDownZoomOut = false;
        UIManager.VisibleAllUI = true;
        UIManager.useUIFBO = false;
        UIManager.black = null;
        UIManager.bSuspend = false;
        UIManager.lastAlpha = 10000.0f;
        PickedTileLocal = new Vector2();
        PickedTile = new Vector2();
        UIManager.RightDownObject = null;
        UIManager.uiUpdateTimeMS = 0L;
        UIManager.uiUpdateIntervalMS = 0L;
        UIManager.uiRenderTimeMS = 0L;
        UIManager.uiRenderIntervalMS = 0L;
        tutorialStack = new ArrayList<UIElement>();
        toTop = new ArrayList<UIElement>();
        UIManager.defaultthread = null;
        UIManager.previousThread = null;
        toRemove = new ArrayList<UIElement>();
        toAdd = new ArrayList<UIElement>();
        UIManager.wheel = 0;
        UIManager.lastwheel = 0;
        debugUI = new ArrayList<UIElement>();
        UIManager.bShowLuaDebuggerOnError = true;
        sync = new Sync();
        UIManager.showPausedMessage = true;
        playerFadeInfo = new FadeInfo[4];
        for (int i = 0; i < 4; ++i) {
            UIManager.playerFadeInfo[i] = new FadeInfo(i);
        }
    }
    
    static class Sync
    {
        private int fps;
        private long period;
        private long excess;
        private long beforeTime;
        private long overSleepTime;
        
        Sync() {
            this.fps = 30;
            this.period = 1000000000L / this.fps;
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }
        
        void begin() {
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }
        
        void startFrame() {
            this.excess = 0L;
        }
        
        void endFrame() {
            final long nanoTime = System.nanoTime();
            final long n = this.period - (nanoTime - this.beforeTime) - this.overSleepTime;
            if (n > 0L) {
                try {
                    Thread.sleep(n / 1000000L);
                }
                catch (InterruptedException ex) {}
                this.overSleepTime = System.nanoTime() - nanoTime - n;
            }
            else {
                this.excess -= n;
                this.overSleepTime = 0L;
            }
            this.beforeTime = System.nanoTime();
        }
    }
    
    private static class FadeInfo
    {
        public int playerIndex;
        public boolean bFadeBeforeUI;
        public float FadeAlpha;
        public int FadeTime;
        public int FadeTimeMax;
        public boolean FadingOut;
        
        public FadeInfo(final int playerIndex) {
            this.bFadeBeforeUI = false;
            this.FadeAlpha = 0.0f;
            this.FadeTime = 2;
            this.FadeTimeMax = 2;
            this.FadingOut = false;
            this.playerIndex = playerIndex;
        }
        
        public boolean isFadeBeforeUI() {
            return this.bFadeBeforeUI;
        }
        
        public void setFadeBeforeUI(final boolean bFadeBeforeUI) {
            this.bFadeBeforeUI = bFadeBeforeUI;
        }
        
        public float getFadeAlpha() {
            return this.FadeAlpha;
        }
        
        public void setFadeAlpha(final float fadeAlpha) {
            this.FadeAlpha = fadeAlpha;
        }
        
        public int getFadeTime() {
            return this.FadeTime;
        }
        
        public void setFadeTime(final int fadeTime) {
            this.FadeTime = fadeTime;
        }
        
        public int getFadeTimeMax() {
            return this.FadeTimeMax;
        }
        
        public void setFadeTimeMax(final int fadeTimeMax) {
            this.FadeTimeMax = fadeTimeMax;
        }
        
        public boolean isFadingOut() {
            return this.FadingOut;
        }
        
        public void setFadingOut(final boolean fadingOut) {
            this.FadingOut = fadingOut;
        }
        
        public void FadeIn(final int n) {
            this.setFadeTimeMax((int)(n * 30 * (PerformanceSettings.getLockFPS() / 30.0f)));
            this.setFadeTime(this.getFadeTimeMax());
            this.setFadingOut(false);
        }
        
        public void FadeOut(final int n) {
            this.setFadeTimeMax((int)(n * 30 * (PerformanceSettings.getLockFPS() / 30.0f)));
            this.setFadeTime(this.getFadeTimeMax());
            this.setFadingOut(true);
        }
        
        public void update() {
            this.setFadeTime(this.getFadeTime() - 1);
        }
        
        public void render() {
            this.setFadeAlpha(this.getFadeTime() / (float)this.getFadeTimeMax());
            if (this.getFadeAlpha() > 1.0f) {
                this.setFadeAlpha(1.0f);
            }
            if (this.getFadeAlpha() < 0.0f) {
                this.setFadeAlpha(0.0f);
            }
            if (this.isFadingOut()) {
                this.setFadeAlpha(1.0f - this.getFadeAlpha());
            }
            if (this.getFadeAlpha() <= 0.0f) {
                return;
            }
            UIManager.DrawTexture(UIManager.getBlack(), IsoCamera.getScreenLeft(this.playerIndex), IsoCamera.getScreenTop(this.playerIndex), IsoCamera.getScreenWidth(this.playerIndex), IsoCamera.getScreenHeight(this.playerIndex), this.getFadeAlpha());
        }
    }
}
