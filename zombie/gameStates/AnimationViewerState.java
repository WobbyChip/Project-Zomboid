// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.BooleanConfigOption;
import java.util.Iterator;
import java.util.Comparator;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.ModelManager;
import zombie.input.GameKeyboard;
import zombie.core.Core;
import java.util.Collection;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import zombie.config.ConfigOption;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ui.UIElement;
import java.util.ArrayList;
import zombie.vehicles.EditVehicleState;

public final class AnimationViewerState extends GameState
{
    public static AnimationViewerState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit;
    private final ArrayList<UIElement> m_gameUI;
    private final ArrayList<UIElement> m_selfUI;
    private boolean m_bSuspendUI;
    private KahluaTable m_table;
    private final ArrayList<String> m_clipNames;
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options;
    private BooleanDebugOption DrawGrid;
    private BooleanDebugOption Isometric;
    private BooleanDebugOption UseDeferredMovement;
    
    public AnimationViewerState() {
        this.bExit = false;
        this.m_gameUI = new ArrayList<UIElement>();
        this.m_selfUI = new ArrayList<UIElement>();
        this.m_table = null;
        this.m_clipNames = new ArrayList<String>();
        this.options = new ArrayList<ConfigOption>();
        this.DrawGrid = new BooleanDebugOption("DrawGrid", false);
        this.Isometric = new BooleanDebugOption("Isometric", false);
        this.UseDeferredMovement = new BooleanDebugOption("UseDeferredMovement", false);
    }
    
    @Override
    public void enter() {
        (AnimationViewerState.instance = this).load();
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }
        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget((Object)"AnimationViewerState_InitUI"), new Object[0]);
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
    
    public static AnimationViewerState checkInstance() {
        if (AnimationViewerState.instance != null) {
            if (AnimationViewerState.instance.m_table == null || AnimationViewerState.instance.m_table.getMetatable() == null) {
                AnimationViewerState.instance = null;
            }
            else if (AnimationViewerState.instance.m_table.getMetatable().rawget((Object)"_LUA_RELOADED_CHECK") == null) {
                AnimationViewerState.instance = null;
            }
        }
        if (AnimationViewerState.instance == null) {
            return new AnimationViewerState();
        }
        return AnimationViewerState.instance;
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
        ModelManager.instance.update();
        if (GameKeyboard.isKeyPressed(17)) {
            DebugOptions.instance.ModelRenderWireframe.setValue(!DebugOptions.instance.ModelRenderWireframe.getValue());
        }
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
            case "getClipNames": {
                if (this.m_clipNames.isEmpty()) {
                    final Iterator<AnimationClip> iterator = ModelManager.instance.getAllAnimationClips().iterator();
                    while (iterator.hasNext()) {
                        this.m_clipNames.add(iterator.next().Name);
                    }
                    this.m_clipNames.sort(Comparator.naturalOrder());
                }
                return this.m_clipNames;
            }
            default: {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
    }
    
    public Object fromLua1(final String s, final Object o) {
        s.hashCode();
        throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", s, o));
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
    
    public class BooleanDebugOption extends BooleanConfigOption
    {
        public BooleanDebugOption(final String s, final boolean b) {
            super(s, b);
            AnimationViewerState.this.options.add(this);
        }
    }
}
