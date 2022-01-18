// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.core.math.PZMath;
import zombie.core.BoxedStaticValues;
import zombie.iso.IsoUtils;
import zombie.input.Mouse;
import zombie.iso.IsoObjectPicker;
import zombie.globalObjects.CGlobalObjectSystem;
import zombie.debug.LineDrawer;
import zombie.globalObjects.GlobalObject;
import zombie.globalObjects.CGlobalObjects;
import zombie.iso.sprite.IsoSprite;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoChunkMap;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoWorld;
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
import zombie.ui.UIFont;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ui.UIElement;
import java.util.ArrayList;
import zombie.vehicles.EditVehicleState;

public final class DebugGlobalObjectState extends GameState
{
    public static DebugGlobalObjectState instance;
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
    
    public DebugGlobalObjectState() {
        this.bExit = false;
        this.m_gameUI = new ArrayList<UIElement>();
        this.m_selfUI = new ArrayList<UIElement>();
        this.m_table = null;
        this.m_playerIndex = 0;
        this.m_z = 0;
        this.gridX = -1;
        this.gridY = -1;
        this.FONT = UIFont.DebugConsole;
        DebugGlobalObjectState.instance = this;
    }
    
    @Override
    public void enter() {
        DebugGlobalObjectState.instance = this;
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }
        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            final IsoPlayer isoPlayer = IsoPlayer.players[this.m_playerIndex];
            this.m_z = ((isoPlayer == null) ? 0 : ((int)isoPlayer.z));
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget((Object)"DebugGlobalObjectState_InitUI"), (Object)this);
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
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[this.m_playerIndex];
        isoChunkMap.ProcessChunkPos(IsoPlayer.players[this.m_playerIndex]);
        isoChunkMap.update();
        return this.updateScene();
    }
    
    public void renderScene() {
        IsoCamera.frameState.set(this.m_playerIndex);
        SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
        SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
        SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
        IsoSprite.globalOffsetX = -1.0f;
        IsoWorld.instance.CurrentCell.render();
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[this.m_playerIndex];
        final int worldXMin = isoChunkMap.getWorldXMin();
        final int worldYMin = isoChunkMap.getWorldYMin();
        final int n = worldXMin + IsoChunkMap.ChunkGridWidth;
        final int n2 = worldYMin + IsoChunkMap.ChunkGridWidth;
        for (int systemCount = CGlobalObjects.getSystemCount(), i = 0; i < systemCount; ++i) {
            final CGlobalObjectSystem systemByIndex = CGlobalObjects.getSystemByIndex(i);
            for (int j = worldYMin; j < n2; ++j) {
                for (int k = worldXMin; k < n; ++k) {
                    final ArrayList<GlobalObject> objectsInChunk = systemByIndex.getObjectsInChunk(k, j);
                    for (int l = 0; l < objectsInChunk.size(); ++l) {
                        final GlobalObject globalObject = objectsInChunk.get(l);
                        float n3 = 1.0f;
                        float n4 = 1.0f;
                        float n5 = 1.0f;
                        if (globalObject.getZ() != this.m_z) {
                            n4 = (n3 = (n5 = 0.5f));
                        }
                        this.DrawIsoRect((float)globalObject.getX(), (float)globalObject.getY(), (float)globalObject.getZ(), 1.0f, 1.0f, n3, n4, n5, 1.0f, 1);
                    }
                    systemByIndex.finishedWithList(objectsInChunk);
                }
            }
        }
        LineDrawer.render();
        LineDrawer.clear();
    }
    
    private void renderUI() {
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
        IsoCamera.update();
        this.updateCursor();
        return GameStateMachine.StateAction.Remain;
    }
    
    private void updateCursor() {
        final int playerIndex = this.m_playerIndex;
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
    
    private void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final int n11) {
        LineDrawer.drawLine(IsoUtils.XToScreenExact(n, n2, n3, 0), IsoUtils.YToScreenExact(n, n2, n3, 0), IsoUtils.XToScreenExact(n4, n5, n6, 0), IsoUtils.YToScreenExact(n4, n5, n6, 0), n7, n8, n9, n10, n11);
    }
    
    private void DrawIsoRect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final int n10) {
        this.DrawIsoLine(n, n2, n3, n + n4, n2, n3, n6, n7, n8, n9, n10);
        this.DrawIsoLine(n + n4, n2, n3, n + n4, n2 + n5, n3, n6, n7, n8, n9, n10);
        this.DrawIsoLine(n + n4, n2 + n5, n3, n, n2 + n5, n3, n6, n7, n8, n9, n10);
        this.DrawIsoLine(n, n2 + n5, n3, n, n2, n3, n6, n7, n8, n9, n10);
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
            case "setPlayerIndex": {
                this.m_playerIndex = PZMath.clamp(((Double)o).intValue(), 0, 3);
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
}
