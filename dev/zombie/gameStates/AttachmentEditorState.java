// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import java.util.Iterator;
import zombie.debug.DebugLog;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import zombie.scripting.objects.ModelAttachment;
import java.util.Locale;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.ScriptManager;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.ModelManager;
import zombie.input.GameKeyboard;
import zombie.core.Core;
import java.util.Collection;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ui.UIElement;
import java.util.ArrayList;
import zombie.vehicles.EditVehicleState;

public final class AttachmentEditorState extends GameState
{
    public static AttachmentEditorState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit;
    private final ArrayList<UIElement> m_gameUI;
    private final ArrayList<UIElement> m_selfUI;
    private boolean m_bSuspendUI;
    private KahluaTable m_table;
    
    public AttachmentEditorState() {
        this.bExit = false;
        this.m_gameUI = new ArrayList<UIElement>();
        this.m_selfUI = new ArrayList<UIElement>();
        this.m_table = null;
    }
    
    @Override
    public void enter() {
        AttachmentEditorState.instance = this;
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }
        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget((Object)"AttachmentEditorState_InitUI"), new Object[0]);
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
        this.restoreGameUI();
    }
    
    @Override
    public void render() {
        final int n = 0;
        Core.getInstance().StartFrame(n, true);
        this.renderScene();
        Core.getInstance().EndFrame(n);
        Core.getInstance().RenderOffScreenBuffer();
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
    
    public static AttachmentEditorState checkInstance() {
        if (AttachmentEditorState.instance != null) {
            if (AttachmentEditorState.instance.m_table == null || AttachmentEditorState.instance.m_table.getMetatable() == null) {
                AttachmentEditorState.instance = null;
            }
            else if (AttachmentEditorState.instance.m_table.getMetatable().rawget((Object)"_LUA_RELOADED_CHECK") == null) {
                AttachmentEditorState.instance = null;
            }
        }
        if (AttachmentEditorState.instance == null) {
            return new AttachmentEditorState();
        }
        return AttachmentEditorState.instance;
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
            default: {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
    }
    
    public Object fromLua1(final String s, final Object o) {
        switch (s) {
            case "writeScript": {
                final ModelScript modelScript = ScriptManager.instance.getModelScript((String)o);
                if (modelScript == null) {
                    throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
                }
                final ArrayList<String> script = this.readScript(modelScript.getFileName());
                if (script != null) {
                    this.updateScript(modelScript.getFileName(), script, modelScript);
                }
                return null;
            }
            default: {
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", s, o));
            }
        }
    }
    
    private ArrayList<String> readScript(String string) {
        final StringBuilder sb = new StringBuilder();
        string = ZomboidFileSystem.instance.getString(string);
        final File file = new File(string);
        try {
            final FileReader in = new FileReader(file);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    final String lineSeparator = System.lineSeparator();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        sb.append(lineSeparator);
                    }
                    bufferedReader.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedReader.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
            return null;
        }
        return ScriptParser.parseTokens(ScriptParser.stripComments(sb.toString()));
    }
    
    private void updateScript(String string, final ArrayList<String> list, final ModelScript modelScript) {
        string = ZomboidFileSystem.instance.getString(string);
        for (int i = list.size() - 1; i >= 0; --i) {
            final String trim = list.get(i).trim();
            final int index = trim.indexOf("{");
            final int lastIndex = trim.lastIndexOf("}");
            if (trim.substring(0, index).startsWith("module")) {
                final String[] split = trim.substring(0, index).trim().split("\\s+");
                final String s = (split.length > 1) ? split[1].trim() : "";
                if (s.equals(modelScript.getModule().getName())) {
                    final ArrayList<String> tokens = ScriptParser.parseTokens(trim.substring(index + 1, lastIndex).trim());
                    for (int j = tokens.size() - 1; j >= 0; --j) {
                        final String trim2 = tokens.get(j).trim();
                        if (trim2.startsWith("model")) {
                            final String[] split2 = trim2.substring(0, trim2.indexOf("{")).trim().split("\\s+");
                            if (((split2.length > 1) ? split2[1].trim() : "").equals(modelScript.getName())) {
                                tokens.set(j, this.modelScriptToText(modelScript, trim2).trim());
                                final String lineSeparator = System.lineSeparator();
                                list.set(i, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, lineSeparator, lineSeparator, String.join(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator), (Iterable<? extends CharSequence>)tokens), lineSeparator, lineSeparator));
                                this.writeScript(string, list);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private String modelScriptToText(final ModelScript modelScript, final String s) {
        final ScriptParser.Block block = ScriptParser.parse(s).children.get(0);
        for (int i = block.children.size() - 1; i >= 0; --i) {
            final ScriptParser.Block o = block.children.get(i);
            if ("attachment".equals(o.type)) {
                block.elements.remove(o);
                block.children.remove(i);
            }
        }
        for (int j = 0; j < modelScript.getAttachmentCount(); ++j) {
            final ModelAttachment attachment = modelScript.getAttachment(j);
            final ScriptParser.Block block2 = block.getBlock("attachment", attachment.getId());
            if (block2 == null) {
                final ScriptParser.Block block3 = new ScriptParser.Block();
                block3.type = "attachment";
                block3.id = attachment.getId();
                block3.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", attachment.getOffset().x(), attachment.getOffset().y(), attachment.getOffset().z()));
                block3.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", attachment.getRotate().x(), attachment.getRotate().y(), attachment.getRotate().z()));
                if (attachment.getBone() != null) {
                    block3.setValue("bone", attachment.getBone());
                }
                block.elements.add(block3);
                block.children.add(block3);
            }
            else {
                block2.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", attachment.getOffset().x(), attachment.getOffset().y(), attachment.getOffset().z()));
                block2.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", attachment.getRotate().x(), attachment.getRotate().y(), attachment.getRotate().z()));
            }
        }
        final StringBuilder sb = new StringBuilder();
        block.prettyPrint(1, sb, System.lineSeparator());
        return sb.toString();
    }
    
    private void writeScript(final String s, final ArrayList<String> list) {
        final String string = ZomboidFileSystem.instance.getString(s);
        final File file = new File(string);
        try {
            final FileWriter out = new FileWriter(file);
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(out);
                try {
                    DebugLog.General.printf("writing %s\n", s);
                    final Iterator<String> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        bufferedWriter.write(iterator.next());
                    }
                    this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget((Object)"wroteScript"), new Object[] { this.m_table, string });
                    bufferedWriter.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedWriter.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
        }
    }
}
