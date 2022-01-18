// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.editor;

import zombie.input.GameKeyboard;
import zombie.gameStates.GameStateMachine;
import zombie.core.Core;
import java.util.Collection;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ui.UIElement;
import java.util.ArrayList;
import zombie.vehicles.EditVehicleState;
import zombie.gameStates.GameState;

public final class WorldMapEditorState extends GameState
{
    public static WorldMapEditorState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit;
    private final ArrayList<UIElement> m_gameUI;
    private final ArrayList<UIElement> m_selfUI;
    private boolean m_bSuspendUI;
    private KahluaTable m_table;
    
    public WorldMapEditorState() {
        this.bExit = false;
        this.m_gameUI = new ArrayList<UIElement>();
        this.m_selfUI = new ArrayList<UIElement>();
        this.m_table = null;
    }
    
    @Override
    public void enter() {
        (WorldMapEditorState.instance = this).load();
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }
        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget((Object)"WorldMapEditor_InitUI"), (Object)this);
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
    }
    
    @Override
    public void render() {
        final int n = 0;
        Core.getInstance().StartFrame(n, true);
        this.renderScene();
        Core.getInstance().EndFrame(n);
        Core.getInstance().RenderOffScreenBuffer();
        UIManager.useUIFBO = (Core.getInstance().supportsFBO() && Core.OptionUIFBO);
        if (Core.getInstance().StartFrameUI()) {
            this.renderUI();
        }
        Core.getInstance().EndFrameUI();
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        if (this.bExit || GameKeyboard.isKeyPressed(65)) {
            return GameStateMachine.StateAction.Continue;
        }
        this.updateScene();
        return GameStateMachine.StateAction.Remain;
    }
    
    public static WorldMapEditorState checkInstance() {
        if (WorldMapEditorState.instance != null) {
            if (WorldMapEditorState.instance.m_table == null || WorldMapEditorState.instance.m_table.getMetatable() == null) {
                WorldMapEditorState.instance = null;
            }
            else if (WorldMapEditorState.instance.m_table.getMetatable().rawget((Object)"_LUA_RELOADED_CHECK") == null) {
                WorldMapEditorState.instance = null;
            }
        }
        if (WorldMapEditorState.instance == null) {
            return new WorldMapEditorState();
        }
        return WorldMapEditorState.instance;
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
    
    private void updateScene() {
    }
    
    private void renderScene() {
    }
    
    private void renderUI() {
        UIManager.render();
    }
    
    public void setTable(final KahluaTable table) {
        this.m_table = table;
    }
    
    public Object fromLua0(final String s) {
        switch (s) {
            case "exit": {
                this.bExit = true;
                return null;
            }
            default: {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
    }
    
    public void save() {
    }
    
    public void load() {
    }
}
