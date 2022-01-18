// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.j2se.J2SEPlatform;
import zombie.debug.DebugLog;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import org.joml.Vector2f;
import zombie.scripting.objects.ModelAttachment;
import org.joml.Vector3f;
import zombie.util.list.PZArrayUtil;
import java.util.Locale;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.ScriptManager;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.ModelManager;
import zombie.input.GameKeyboard;
import zombie.gameStates.GameStateMachine;
import zombie.core.Core;
import java.util.Collection;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ui.UIElement;
import java.util.ArrayList;
import zombie.gameStates.GameState;

public final class EditVehicleState extends GameState
{
    public static EditVehicleState instance;
    private LuaEnvironment m_luaEnv;
    private boolean bExit;
    private String m_initialScript;
    private final ArrayList<UIElement> m_gameUI;
    private final ArrayList<UIElement> m_selfUI;
    private boolean m_bSuspendUI;
    private KahluaTable m_table;
    
    public EditVehicleState() {
        this.bExit = false;
        this.m_initialScript = null;
        this.m_gameUI = new ArrayList<UIElement>();
        this.m_selfUI = new ArrayList<UIElement>();
        this.m_table = null;
        EditVehicleState.instance = this;
    }
    
    @Override
    public void enter() {
        EditVehicleState.instance = this;
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }
        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget((Object)"EditVehicleState_InitUI"), new Object[0]);
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
    
    public static EditVehicleState checkInstance() {
        if (EditVehicleState.instance != null) {
            if (EditVehicleState.instance.m_table == null || EditVehicleState.instance.m_table.getMetatable() == null) {
                EditVehicleState.instance = null;
            }
            else if (EditVehicleState.instance.m_table.getMetatable().rawget((Object)"_LUA_RELOADED_CHECK") == null) {
                EditVehicleState.instance = null;
            }
        }
        if (EditVehicleState.instance == null) {
            return new EditVehicleState();
        }
        return EditVehicleState.instance;
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
    
    public void setScript(final String initialScript) {
        if (this.m_table == null) {
            this.m_initialScript = initialScript;
        }
        else {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget((Object)"setScript"), new Object[] { this.m_table, initialScript });
        }
    }
    
    public Object fromLua0(final String s) {
        switch (s) {
            case "exit": {
                this.bExit = true;
                return null;
            }
            case "getInitialScript": {
                return this.m_initialScript;
            }
            default: {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
    }
    
    public Object fromLua1(final String s, final Object o) {
        switch (s) {
            case "writeScript": {
                final VehicleScript vehicle = ScriptManager.instance.getVehicle((String)o);
                if (vehicle == null) {
                    throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
                }
                final ArrayList<String> script = this.readScript(vehicle.getFileName());
                if (script != null) {
                    this.updateScript(vehicle.getFileName(), script, vehicle);
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
    
    private void updateScript(String string, final ArrayList<String> list, final VehicleScript vehicleScript) {
        string = ZomboidFileSystem.instance.getString(string);
        for (int i = list.size() - 1; i >= 0; --i) {
            final String trim = list.get(i).trim();
            final int index = trim.indexOf("{");
            final int lastIndex = trim.lastIndexOf("}");
            if (trim.substring(0, index).startsWith("module")) {
                final String[] split = trim.substring(0, index).trim().split("\\s+");
                final String s = (split.length > 1) ? split[1].trim() : "";
                if (s.equals(vehicleScript.getModule().getName())) {
                    final ArrayList<String> tokens = ScriptParser.parseTokens(trim.substring(index + 1, lastIndex).trim());
                    for (int j = tokens.size() - 1; j >= 0; --j) {
                        final String trim2 = tokens.get(j).trim();
                        if (trim2.startsWith("vehicle")) {
                            final String[] split2 = trim2.substring(0, trim2.indexOf("{")).trim().split("\\s+");
                            if (((split2.length > 1) ? split2[1].trim() : "").equals(vehicleScript.getName())) {
                                tokens.set(j, this.vehicleScriptToText(vehicleScript, trim2).trim());
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
    
    private String vehicleScriptToText(final VehicleScript vehicleScript, final String s) {
        final float modelScale = vehicleScript.getModelScale();
        final ScriptParser.Block block = ScriptParser.parse(s).children.get(0);
        final VehicleScript.Model model = vehicleScript.getModel();
        final ScriptParser.Block block2 = block.getBlock("model", null);
        if (model != null && block2 != null) {
            block2.setValue("scale", String.format(Locale.US, "%.4f", vehicleScript.getModelScale()));
            final Vector3f offset = vehicleScript.getModel().getOffset();
            block2.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", offset.x / modelScale, offset.y / modelScale, offset.z / modelScale));
        }
        final ArrayList<ScriptParser.Block> list = new ArrayList<ScriptParser.Block>();
        for (int i = 0; i < block.children.size(); ++i) {
            final ScriptParser.Block block3 = block.children.get(i);
            if ("physics".equals(block3.type)) {
                if (list.size() == vehicleScript.getPhysicsShapeCount()) {
                    block.elements.remove(block3);
                    block.children.remove(i);
                    --i;
                }
                else {
                    list.add(block3);
                }
            }
        }
        for (int j = 0; j < vehicleScript.getPhysicsShapeCount(); ++j) {
            final VehicleScript.PhysicsShape physicsShape = vehicleScript.getPhysicsShape(j);
            final boolean b = j < list.size();
            final ScriptParser.Block block4 = b ? list.get(j) : new ScriptParser.Block();
            block4.type = "physics";
            block4.id = physicsShape.getTypeString();
            if (b) {
                block4.elements.clear();
                block4.children.clear();
                block4.values.clear();
            }
            block4.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getOffset().x() / modelScale, physicsShape.getOffset().y() / modelScale, physicsShape.getOffset().z() / modelScale));
            if (physicsShape.type == 1) {
                block4.setValue("extents", String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getExtents().x() / modelScale, physicsShape.getExtents().y() / modelScale, physicsShape.getExtents().z() / modelScale));
                block4.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getRotate().x(), physicsShape.getRotate().y(), physicsShape.getRotate().z()));
            }
            if (physicsShape.type == 2) {
                block4.setValue("radius", String.format(Locale.US, "%.4f", physicsShape.getRadius() / modelScale));
            }
            if (!b) {
                block.elements.add(block4);
                block.children.add(block4);
            }
        }
        for (int k = block.children.size() - 1; k >= 0; --k) {
            final ScriptParser.Block o = block.children.get(k);
            if ("attachment".equals(o.type)) {
                block.elements.remove(o);
                block.children.remove(k);
            }
        }
        for (int l = 0; l < vehicleScript.getAttachmentCount(); ++l) {
            final ModelAttachment attachment = vehicleScript.getAttachment(l);
            ScriptParser.Block block5 = block.getBlock("attachment", attachment.getId());
            if (block5 == null) {
                block5 = new ScriptParser.Block();
                block5.type = "attachment";
                block5.id = attachment.getId();
                block.elements.add(block5);
                block.children.add(block5);
            }
            block5.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", attachment.getOffset().x() / modelScale, attachment.getOffset().y() / modelScale, attachment.getOffset().z() / modelScale));
            block5.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", attachment.getRotate().x(), attachment.getRotate().y(), attachment.getRotate().z()));
            if (attachment.getBone() != null) {
                block5.setValue("bone", attachment.getBone());
            }
            if (attachment.getCanAttach() != null) {
                block5.setValue("canAttach", PZArrayUtil.arrayToString(attachment.getCanAttach(), "", "", ","));
            }
            if (attachment.getZOffset() != 0.0f) {
                block5.setValue("zoffset", String.format(Locale.US, "%.4f", attachment.getZOffset()));
            }
            if (!attachment.isUpdateConstraint()) {
                block5.setValue("updateconstraint", "false");
            }
        }
        final Vector3f extents = vehicleScript.getExtents();
        block.setValue("extents", String.format(Locale.US, "%.4f %.4f %.4f", extents.x / modelScale, extents.y / modelScale, extents.z / modelScale));
        final Vector3f physicsChassisShape = vehicleScript.getPhysicsChassisShape();
        block.setValue("physicsChassisShape", String.format(Locale.US, "%.4f %.4f %.4f", physicsChassisShape.x / modelScale, physicsChassisShape.y / modelScale, physicsChassisShape.z / modelScale));
        final Vector3f centerOfMassOffset = vehicleScript.getCenterOfMassOffset();
        block.setValue("centerOfMassOffset", String.format(Locale.US, "%.4f %.4f %.4f", centerOfMassOffset.x / modelScale, centerOfMassOffset.y / modelScale, centerOfMassOffset.z / modelScale));
        final Vector2f shadowExtents = vehicleScript.getShadowExtents();
        final boolean b2 = block.getValue("shadowExtents") != null;
        block.setValue("shadowExtents", String.format(Locale.US, "%.4f %.4f", shadowExtents.x / modelScale, shadowExtents.y / modelScale));
        if (!b2) {
            block.moveValueAfter("shadowExtents", "centerOfMassOffset");
        }
        final Vector2f shadowOffset = vehicleScript.getShadowOffset();
        final boolean b3 = block.getValue("shadowOffset") != null;
        block.setValue("shadowOffset", String.format(Locale.US, "%.4f %.4f", shadowOffset.x / modelScale, shadowOffset.y / modelScale));
        if (!b3) {
            block.moveValueAfter("shadowOffset", "shadowExtents");
        }
        for (int n = 0; n < vehicleScript.getAreaCount(); ++n) {
            final VehicleScript.Area area = vehicleScript.getArea(n);
            final ScriptParser.Block block6 = block.getBlock("area", area.getId());
            if (block6 != null) {
                block6.setValue("xywh", String.format(Locale.US, "%.4f %.4f %.4f %.4f", area.getX() / modelScale, area.getY() / modelScale, area.getW() / modelScale, area.getH() / modelScale));
            }
        }
        for (int n2 = 0; n2 < vehicleScript.getPassengerCount(); ++n2) {
            final VehicleScript.Passenger passenger = vehicleScript.getPassenger(n2);
            final ScriptParser.Block block7 = block.getBlock("passenger", passenger.getId());
            if (block7 != null) {
                for (final VehicleScript.Position position : passenger.positions) {
                    final ScriptParser.Block block8 = block7.getBlock("position", position.id);
                    if (block8 == null) {
                        continue;
                    }
                    block8.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", position.offset.x / modelScale, position.offset.y / modelScale, position.offset.z / modelScale));
                    block8.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", position.rotate.x / modelScale, position.rotate.y / modelScale, position.rotate.z / modelScale));
                }
            }
        }
        for (int n3 = 0; n3 < vehicleScript.getWheelCount(); ++n3) {
            final VehicleScript.Wheel wheel = vehicleScript.getWheel(n3);
            final ScriptParser.Block block9 = block.getBlock("wheel", wheel.getId());
            if (block9 != null) {
                block9.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", wheel.offset.x / modelScale, wheel.offset.y / modelScale, wheel.offset.z / modelScale));
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
    
    public static final class LuaEnvironment
    {
        public J2SEPlatform platform;
        public KahluaTable env;
        public KahluaThread thread;
        public LuaCaller caller;
        
        public LuaEnvironment(final J2SEPlatform platform, final KahluaConverterManager kahluaConverterManager, final KahluaTable env) {
            this.platform = platform;
            this.env = env;
            this.thread = LuaManager.thread;
            this.caller = LuaManager.caller;
        }
    }
}
