// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

import zombie.iso.Vector2;
import zombie.debug.DebugOptions;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaEventManager;
import zombie.core.BoxedStaticValues;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.ZomboidFileSystem;
import org.lwjglx.input.Controller;
import zombie.GameWindow;
import java.util.HashSet;
import java.util.ArrayList;

public final class JoypadManager
{
    public static final JoypadManager instance;
    public final Joypad[] Joypads;
    public final Joypad[] JoypadsController;
    public final ArrayList<Joypad> JoypadList;
    public final HashSet<String> ActiveControllerGUIDs;
    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private static final int VERSION_LATEST = 2;
    
    public JoypadManager() {
        this.Joypads = new Joypad[4];
        this.JoypadsController = new Joypad[16];
        this.JoypadList = new ArrayList<Joypad>();
        this.ActiveControllerGUIDs = new HashSet<String>();
    }
    
    public Joypad addJoypad(final int id, final String s, final String name) {
        final Joypad e = new Joypad();
        e.ID = id;
        e.guid = s;
        e.name = name;
        this.doControllerFile(this.JoypadsController[id] = e);
        if (!e.isDisabled() && this.ActiveControllerGUIDs.contains(s)) {
            this.JoypadList.add(e);
        }
        return e;
    }
    
    private Joypad checkJoypad(final int n) {
        if (this.JoypadsController[n] == null) {
            final Controller controller = GameWindow.GameInput.getController(n);
            this.addJoypad(n, controller.getGUID(), controller.getGamepadName());
        }
        return this.JoypadsController[n];
    }
    
    private void doControllerFile(final Joypad joypad) {
        final File file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads"));
        if (!file.exists()) {
            file.mkdir();
        }
        final File file2 = new File(ZomboidFileSystem.instance.getCacheDirSub(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, joypad.guid)));
        try {
            final FileReader in = new FileReader(file2.getAbsolutePath());
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file2.getAbsolutePath()));
                    int int1 = -1;
                    try {
                        String line = "";
                        while (line != null) {
                            line = bufferedReader.readLine();
                            if (line != null) {
                                if (line.trim().length() == 0) {
                                    continue;
                                }
                                if (line.trim().startsWith("//")) {
                                    continue;
                                }
                                final String[] split = line.split("=");
                                if (split.length != 2) {
                                    continue;
                                }
                                split[0] = split[0].trim();
                                split[1] = split[1].trim();
                                if (split[0].equals("Version")) {
                                    int1 = Integer.parseInt(split[1]);
                                    if (int1 < 1 || int1 > 2) {
                                        DebugLog.General.warn("Unknown version %d in %s", int1, file2.getAbsolutePath());
                                        break;
                                    }
                                    if (int1 == 1) {
                                        DebugLog.General.warn("Obsolete version %d in %s.  Using default values.", int1, file2.getAbsolutePath());
                                        break;
                                    }
                                }
                                if (int1 == -1) {
                                    DebugLog.General.warn("Ignoring %s=%s because Version is missing", split[0], split[1]);
                                }
                                else if (split[0].equals("MovementAxisX")) {
                                    joypad.MovementAxisX = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("MovementAxisXFlipped")) {
                                    joypad.MovementAxisXFlipped = split[1].equals("true");
                                }
                                else if (split[0].equals("MovementAxisY")) {
                                    joypad.MovementAxisY = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("MovementAxisYFlipped")) {
                                    joypad.MovementAxisYFlipped = split[1].equals("true");
                                }
                                else if (split[0].equals("MovementAxisDeadZone")) {
                                    joypad.MovementAxisDeadZone = Float.parseFloat(split[1]);
                                }
                                else if (split[0].equals("AimingAxisX")) {
                                    joypad.AimingAxisX = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("AimingAxisXFlipped")) {
                                    joypad.AimingAxisXFlipped = split[1].equals("true");
                                }
                                else if (split[0].equals("AimingAxisY")) {
                                    joypad.AimingAxisY = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("AimingAxisYFlipped")) {
                                    joypad.AimingAxisYFlipped = split[1].equals("true");
                                }
                                else if (split[0].equals("AimingAxisDeadZone")) {
                                    joypad.AimingAxisDeadZone = Float.parseFloat(split[1]);
                                }
                                else if (split[0].equals("AButton")) {
                                    joypad.AButton = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("BButton")) {
                                    joypad.BButton = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("XButton")) {
                                    joypad.XButton = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("YButton")) {
                                    joypad.YButton = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("LBumper")) {
                                    joypad.BumperLeft = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("RBumper")) {
                                    joypad.BumperRight = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("L3")) {
                                    joypad.LeftStickButton = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("R3")) {
                                    joypad.RightStickButton = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("Back")) {
                                    joypad.Back = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("Start")) {
                                    joypad.Start = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("DPadUp")) {
                                    joypad.DPadUp = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("DPadDown")) {
                                    joypad.DPadDown = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("DPadLeft")) {
                                    joypad.DPadLeft = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("DPadRight")) {
                                    joypad.DPadRight = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("TriggersFlipped")) {
                                    joypad.TriggersFlipped = split[1].equals("true");
                                }
                                else if (split[0].equals("TriggerLeft")) {
                                    joypad.TriggerLeft = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("TriggerRight")) {
                                    joypad.TriggerRight = Integer.parseInt(split[1]);
                                }
                                else if (split[0].equals("Disabled")) {
                                    joypad.Disabled = split[1].equals("true");
                                }
                                else {
                                    if (!split[0].equals("Sensitivity")) {
                                        continue;
                                    }
                                    joypad.setDeadZone(Float.parseFloat(split[1]));
                                }
                            }
                        }
                    }
                    catch (Exception ex) {
                        ExceptionLogger.logException(ex);
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
        catch (FileNotFoundException ex4) {
            if (!this.ActiveControllerGUIDs.contains(joypad.guid)) {
                this.ActiveControllerGUIDs.add(joypad.guid);
                try {
                    Core.getInstance().saveOptions();
                }
                catch (Exception ex2) {
                    ExceptionLogger.logException(ex2);
                }
            }
        }
        catch (IOException ex3) {
            ExceptionLogger.logException(ex3);
        }
        this.saveFile(joypad);
    }
    
    private void saveFile(final Joypad joypad) {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            file.mkdir();
        }
        final File file2 = new File(ZomboidFileSystem.instance.getCacheDirSub(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, joypad.guid)));
        try {
            final FileWriter out = new FileWriter(file2.getAbsolutePath());
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(out);
                try {
                    final String property = System.getProperty("line.separator");
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, joypad.name, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.MovementAxisX, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, joypad.MovementAxisXFlipped, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.MovementAxisY, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, joypad.MovementAxisYFlipped, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(FLjava/lang/String;)Ljava/lang/String;, joypad.MovementAxisDeadZone, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.AimingAxisX, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, joypad.AimingAxisXFlipped, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.AimingAxisY, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, joypad.AimingAxisYFlipped, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(FLjava/lang/String;)Ljava/lang/String;, joypad.AimingAxisDeadZone, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.AButton, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.BButton, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.XButton, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.YButton, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.BumperLeft, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.BumperRight, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.LeftStickButton, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.RightStickButton, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.Back, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.Start, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.DPadUp, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.DPadDown, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.DPadLeft, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.DPadRight, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, joypad.TriggersFlipped, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.TriggerLeft, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, joypad.TriggerRight, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, joypad.Disabled, property));
                    bufferedWriter.write(invokedynamic(makeConcatWithConstants:(FLjava/lang/String;)Ljava/lang/String;, joypad.getDeadZone(0), property));
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
        catch (IOException ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void reloadControllerFiles() {
        for (int i = 0; i < GameWindow.GameInput.getControllerCount(); ++i) {
            final Controller controller = GameWindow.GameInput.getController(i);
            if (controller != null) {
                if (this.JoypadsController[i] == null) {
                    this.addJoypad(i, controller.getGUID(), controller.getGamepadName());
                }
                else {
                    this.doControllerFile(this.JoypadsController[i]);
                }
            }
        }
    }
    
    public void assignJoypad(final int n, final int player) {
        this.checkJoypad(n);
        this.Joypads[player] = this.JoypadsController[n];
        this.Joypads[player].player = player;
    }
    
    public Joypad getFromPlayer(final int n) {
        return this.Joypads[n];
    }
    
    public Joypad getFromControllerID(final int n) {
        return this.JoypadsController[n];
    }
    
    public void onPressed(final int n, final int n2) {
        this.checkJoypad(n);
        this.JoypadsController[n].onPressed(n2);
    }
    
    public boolean isDownPressed(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].isDownPressed();
    }
    
    public boolean isUpPressed(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].isUpPressed();
    }
    
    public boolean isRightPressed(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].isRightPressed();
    }
    
    public boolean isLeftPressed(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].isLeftPressed();
    }
    
    public boolean isLBPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isLBPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isLBPressed();
    }
    
    public boolean isRBPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isRBPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isRBPressed();
    }
    
    public boolean isL3Pressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isL3Pressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isL3Pressed();
    }
    
    public boolean isR3Pressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isR3Pressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isR3Pressed();
    }
    
    public boolean isRTPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isRTPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isRTPressed();
    }
    
    public boolean isLTPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isLTPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isLTPressed();
    }
    
    public boolean isAPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isAPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isAPressed();
    }
    
    public boolean isBPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isBPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isBPressed();
    }
    
    public boolean isXPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isXPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isXPressed();
    }
    
    public boolean isYPressed(final int n) {
        if (n < 0) {
            for (int i = 0; i < this.JoypadList.size(); ++i) {
                if (this.JoypadList.get(i).isYPressed()) {
                    return true;
                }
            }
            return false;
        }
        this.checkJoypad(n);
        return this.JoypadsController[n].isYPressed();
    }
    
    public boolean isButtonStartPress(final int n, final int n2) {
        return this.checkJoypad(n).isButtonStartPress(n2);
    }
    
    public boolean isButtonReleasePress(final int n, final int n2) {
        return this.checkJoypad(n).isButtonReleasePress(n2);
    }
    
    public boolean isAButtonStartPress(final int n) {
        return this.isButtonStartPress(n, this.checkJoypad(n).getAButton());
    }
    
    public boolean isBButtonStartPress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonStartPress(checkJoypad.getBButton());
    }
    
    public boolean isXButtonStartPress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonStartPress(checkJoypad.getXButton());
    }
    
    public boolean isYButtonStartPress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonStartPress(checkJoypad.getYButton());
    }
    
    public boolean isAButtonReleasePress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonReleasePress(checkJoypad.getAButton());
    }
    
    public boolean isBButtonReleasePress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonReleasePress(checkJoypad.getBButton());
    }
    
    public boolean isXButtonReleasePress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonReleasePress(checkJoypad.getXButton());
    }
    
    public boolean isYButtonReleasePress(final int n) {
        final Joypad checkJoypad = this.checkJoypad(n);
        return checkJoypad.isButtonReleasePress(checkJoypad.getYButton());
    }
    
    public float getMovementAxisX(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].getMovementAxisX();
    }
    
    public float getMovementAxisY(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].getMovementAxisY();
    }
    
    public float getAimingAxisX(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].getAimingAxisX();
    }
    
    public float getAimingAxisY(final int n) {
        this.checkJoypad(n);
        return this.JoypadsController[n].getAimingAxisY();
    }
    
    public void onPressedAxis(final int n, final int n2) {
        this.checkJoypad(n);
        this.JoypadsController[n].onPressedAxis(n2);
    }
    
    public void onPressedAxisNeg(final int n, final int n2) {
        this.checkJoypad(n);
        this.JoypadsController[n].onPressedAxisNeg(n2);
    }
    
    public void onPressedTrigger(final int n, final int n2) {
        this.checkJoypad(n);
        this.JoypadsController[n].onPressedTrigger(n2);
    }
    
    public void onPressedPov(final int n) {
        this.checkJoypad(n);
        this.JoypadsController[n].onPressedPov();
    }
    
    public float getDeadZone(final int n, final int n2) {
        this.checkJoypad(n);
        return this.JoypadsController[n].getDeadZone(n2);
    }
    
    public void setDeadZone(final int n, final int n2, final float n3) {
        this.checkJoypad(n);
        this.JoypadsController[n].setDeadZone(n2, n3);
    }
    
    public void saveControllerSettings(final int n) {
        this.checkJoypad(n);
        this.saveFile(this.JoypadsController[n]);
    }
    
    public long getLastActivity(final int n) {
        if (this.JoypadsController[n] == null) {
            return 0L;
        }
        return this.JoypadsController[n].lastActivity;
    }
    
    public void setControllerActive(final String s, final boolean b) {
        if (b) {
            this.ActiveControllerGUIDs.add(s);
        }
        else {
            this.ActiveControllerGUIDs.remove(s);
        }
        this.syncActiveControllers();
    }
    
    public void syncActiveControllers() {
        this.JoypadList.clear();
        for (int i = 0; i < this.JoypadsController.length; ++i) {
            final Joypad e = this.JoypadsController[i];
            if (e != null && !e.isDisabled() && this.ActiveControllerGUIDs.contains(e.guid)) {
                this.JoypadList.add(e);
            }
        }
    }
    
    public boolean isJoypadConnected(final int n) {
        if (n < 0 || n >= 16) {
            return false;
        }
        assert Thread.currentThread() == GameWindow.GameThread;
        return GameWindow.GameInput.getController(n) != null;
    }
    
    public void onControllerConnected(final Controller controller) {
        final Joypad joypad = this.JoypadsController[controller.getID()];
        if (joypad == null) {
            return;
        }
        LuaEventManager.triggerEvent("OnJoypadBeforeReactivate", BoxedStaticValues.toDouble(joypad.getID()));
        joypad.bConnected = true;
        LuaEventManager.triggerEvent("OnJoypadReactivate", BoxedStaticValues.toDouble(joypad.getID()));
    }
    
    public void onControllerDisconnected(final Controller controller) {
        final Joypad joypad = this.JoypadsController[controller.getID()];
        if (joypad == null) {
            return;
        }
        LuaEventManager.triggerEvent("OnJoypadBeforeDeactivate", BoxedStaticValues.toDouble(joypad.getID()));
        joypad.bConnected = false;
        LuaEventManager.triggerEvent("OnJoypadDeactivate", BoxedStaticValues.toDouble(joypad.getID()));
    }
    
    public void revertToKeyboardAndMouse() {
        for (int i = 0; i < this.JoypadList.size(); ++i) {
            final Joypad joypad = this.JoypadList.get(i);
            if (joypad.player == 0) {
                if (GameWindow.ActivatedJoyPad == joypad) {
                    GameWindow.ActivatedJoyPad = null;
                }
                final IsoPlayer isoPlayer = IsoPlayer.players[0];
                if (isoPlayer != null) {
                    isoPlayer.JoypadBind = -1;
                }
                this.JoypadsController[joypad.getID()] = null;
                this.Joypads[0] = null;
                this.JoypadList.remove(i);
                break;
            }
        }
    }
    
    public void renderUI() {
        assert Thread.currentThread() == GameWindow.GameThread;
        if (!DebugOptions.instance.JoypadRenderUI.getValue()) {
            return;
        }
        if (GameWindow.DrawReloadingLua) {
            return;
        }
        LuaEventManager.triggerEvent("OnJoypadRenderUI");
    }
    
    public void Reset() {
        for (int i = 0; i < this.Joypads.length; ++i) {
            this.Joypads[i] = null;
        }
    }
    
    static {
        instance = new JoypadManager();
    }
    
    public static final class Joypad
    {
        String guid;
        String name;
        int ID;
        int player;
        int MovementAxisX;
        boolean MovementAxisXFlipped;
        int MovementAxisY;
        boolean MovementAxisYFlipped;
        float MovementAxisDeadZone;
        int AimingAxisX;
        boolean AimingAxisXFlipped;
        int AimingAxisY;
        boolean AimingAxisYFlipped;
        float AimingAxisDeadZone;
        int AButton;
        int BButton;
        int XButton;
        int YButton;
        int DPadUp;
        int DPadDown;
        int DPadLeft;
        int DPadRight;
        int BumperLeft;
        int BumperRight;
        int Back;
        int Start;
        int LeftStickButton;
        int RightStickButton;
        boolean TriggersFlipped;
        int TriggerLeft;
        int TriggerRight;
        boolean Disabled;
        boolean bConnected;
        long lastActivity;
        private static final Vector2 tempVec2;
        
        public Joypad() {
            this.player = -1;
            this.MovementAxisX = 0;
            this.MovementAxisXFlipped = false;
            this.MovementAxisY = 1;
            this.MovementAxisYFlipped = false;
            this.MovementAxisDeadZone = 0.0f;
            this.AimingAxisX = 2;
            this.AimingAxisXFlipped = false;
            this.AimingAxisY = 3;
            this.AimingAxisYFlipped = false;
            this.AimingAxisDeadZone = 0.0f;
            this.AButton = 0;
            this.BButton = 1;
            this.XButton = 2;
            this.YButton = 3;
            this.DPadUp = -1;
            this.DPadDown = -1;
            this.DPadLeft = -1;
            this.DPadRight = -1;
            this.BumperLeft = 4;
            this.BumperRight = 5;
            this.Back = 6;
            this.Start = 7;
            this.LeftStickButton = 9;
            this.RightStickButton = 10;
            this.TriggersFlipped = false;
            this.TriggerLeft = 4;
            this.TriggerRight = 5;
            this.Disabled = false;
            this.bConnected = true;
        }
        
        public boolean isDownPressed() {
            if (this.DPadDown != -1) {
                return GameWindow.GameInput.isButtonPressedD(this.DPadDown, this.ID);
            }
            return GameWindow.GameInput.isControllerDownD(this.ID);
        }
        
        public boolean isUpPressed() {
            if (this.DPadUp != -1) {
                return GameWindow.GameInput.isButtonPressedD(this.DPadUp, this.ID);
            }
            return GameWindow.GameInput.isControllerUpD(this.ID);
        }
        
        public boolean isRightPressed() {
            if (this.DPadRight != -1) {
                return GameWindow.GameInput.isButtonPressedD(this.DPadRight, this.ID);
            }
            return GameWindow.GameInput.isControllerRightD(this.ID);
        }
        
        public boolean isLeftPressed() {
            if (this.DPadLeft != -1) {
                return GameWindow.GameInput.isButtonPressedD(this.DPadLeft, this.ID);
            }
            return GameWindow.GameInput.isControllerLeftD(this.ID);
        }
        
        public boolean isLBPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.BumperLeft, this.ID);
        }
        
        public boolean isRBPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.BumperRight, this.ID);
        }
        
        public boolean isL3Pressed() {
            return GameWindow.GameInput.isButtonPressedD(this.LeftStickButton, this.ID);
        }
        
        public boolean isR3Pressed() {
            return GameWindow.GameInput.isButtonPressedD(this.RightStickButton, this.ID);
        }
        
        public boolean isRTPressed() {
            final int triggerRight = this.TriggerRight;
            if (GameWindow.GameInput.getAxisCount(this.ID) <= triggerRight) {
                return this.isRBPressed();
            }
            if (this.TriggersFlipped) {
                return GameWindow.GameInput.getAxisValue(this.ID, triggerRight) < -0.7f;
            }
            return GameWindow.GameInput.getAxisValue(this.ID, triggerRight) > 0.7f;
        }
        
        public boolean isLTPressed() {
            final int triggerLeft = this.TriggerLeft;
            if (GameWindow.GameInput.getAxisCount(this.ID) <= triggerLeft) {
                return this.isLBPressed();
            }
            if (this.TriggersFlipped) {
                return GameWindow.GameInput.getAxisValue(this.ID, triggerLeft) < -0.7f;
            }
            return GameWindow.GameInput.getAxisValue(this.ID, triggerLeft) > 0.7f;
        }
        
        public boolean isAPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.AButton, this.ID);
        }
        
        public boolean isBPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.BButton, this.ID);
        }
        
        public boolean isXPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.XButton, this.ID);
        }
        
        public boolean isYPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.YButton, this.ID);
        }
        
        public boolean isButtonPressed(final int n) {
            return GameWindow.GameInput.isButtonPressedD(n, this.ID);
        }
        
        public boolean wasButtonPressed(final int n) {
            return GameWindow.GameInput.wasButtonPressed(this.ID, n);
        }
        
        public boolean isButtonStartPress(final int n) {
            return GameWindow.GameInput.isButtonStartPress(this.ID, n);
        }
        
        public boolean isButtonReleasePress(final int n) {
            return GameWindow.GameInput.isButtonReleasePress(this.ID, n);
        }
        
        public float getMovementAxisX() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisX) {
                return 0.0f;
            }
            this.MovementAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.MovementAxisX);
            final float movementAxisDeadZone = this.MovementAxisDeadZone;
            if (movementAxisDeadZone > 0.0f && movementAxisDeadZone < 1.0f) {
                final Vector2 set = Joypad.tempVec2.set(GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX), GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY));
                if (set.getLength() < movementAxisDeadZone) {
                    set.set(0.0f, 0.0f);
                }
                else {
                    set.setLength((set.getLength() - movementAxisDeadZone) / (1.0f - movementAxisDeadZone));
                }
                return this.MovementAxisXFlipped ? (-set.getX()) : set.getX();
            }
            if (this.MovementAxisXFlipped) {
                return -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
            }
            return GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
        }
        
        public float getMovementAxisY() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisY) {
                return 0.0f;
            }
            this.MovementAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.MovementAxisY);
            final float movementAxisDeadZone = this.MovementAxisDeadZone;
            if (movementAxisDeadZone > 0.0f && movementAxisDeadZone < 1.0f) {
                final Vector2 set = Joypad.tempVec2.set(GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX), GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY));
                if (set.getLength() < movementAxisDeadZone) {
                    set.set(0.0f, 0.0f);
                }
                else {
                    set.setLength((set.getLength() - movementAxisDeadZone) / (1.0f - movementAxisDeadZone));
                }
                return this.MovementAxisYFlipped ? (-set.getY()) : set.getY();
            }
            if (this.MovementAxisYFlipped) {
                return -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
            }
            return GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
        }
        
        public float getAimingAxisX() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisX) {
                return 0.0f;
            }
            this.AimingAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.AimingAxisX);
            final float aimingAxisDeadZone = this.AimingAxisDeadZone;
            if (aimingAxisDeadZone > 0.0f && aimingAxisDeadZone < 1.0f) {
                final Vector2 set = Joypad.tempVec2.set(GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX), GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY));
                if (set.getLength() < aimingAxisDeadZone) {
                    set.set(0.0f, 0.0f);
                }
                else {
                    set.setLength((set.getLength() - aimingAxisDeadZone) / (1.0f - aimingAxisDeadZone));
                }
                return this.AimingAxisXFlipped ? (-set.getX()) : set.getX();
            }
            if (this.AimingAxisXFlipped) {
                return -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
            }
            return GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
        }
        
        public float getAimingAxisY() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisY) {
                return 0.0f;
            }
            this.AimingAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.AimingAxisY);
            final float aimingAxisDeadZone = this.AimingAxisDeadZone;
            if (aimingAxisDeadZone > 0.0f && aimingAxisDeadZone < 1.0f) {
                final Vector2 set = Joypad.tempVec2.set(GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX), GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY));
                if (set.getLength() < aimingAxisDeadZone) {
                    set.set(0.0f, 0.0f);
                }
                else {
                    set.setLength((set.getLength() - aimingAxisDeadZone) / (1.0f - aimingAxisDeadZone));
                }
                return this.AimingAxisYFlipped ? (-set.getY()) : set.getY();
            }
            if (this.AimingAxisYFlipped) {
                return -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
            }
            return GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
        }
        
        public void onPressed(final int n) {
            this.lastActivity = System.currentTimeMillis();
        }
        
        public void onPressedAxis(final int n) {
            this.lastActivity = System.currentTimeMillis();
        }
        
        public void onPressedAxisNeg(final int n) {
            this.lastActivity = System.currentTimeMillis();
        }
        
        public void onPressedTrigger(final int n) {
            this.lastActivity = System.currentTimeMillis();
        }
        
        public void onPressedPov() {
            this.lastActivity = System.currentTimeMillis();
        }
        
        public float getDeadZone(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getAxisCount(this.ID)) {
                return 0.0f;
            }
            final float deadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(n);
            float b = 0.0f;
            if ((n == this.MovementAxisX || n == this.MovementAxisY) && this.MovementAxisDeadZone > 0.0f && this.MovementAxisDeadZone < 1.0f) {
                b = this.MovementAxisDeadZone;
            }
            if ((n == this.AimingAxisX || n == this.AimingAxisY) && this.AimingAxisDeadZone > 0.0f && this.AimingAxisDeadZone < 1.0f) {
                b = this.AimingAxisDeadZone;
            }
            return Math.max(deadZone, b);
        }
        
        public void setDeadZone(final int n, final float n2) {
            if (n < 0 || n >= GameWindow.GameInput.getAxisCount(this.ID)) {
                return;
            }
            GameWindow.GameInput.getController(this.ID).setDeadZone(n, n2);
        }
        
        public void setDeadZone(final float n) {
            for (int i = 0; i < GameWindow.GameInput.getAxisCount(this.ID); ++i) {
                GameWindow.GameInput.getController(this.ID).setDeadZone(i, n);
            }
        }
        
        public int getID() {
            return this.ID;
        }
        
        public boolean isDisabled() {
            return this.Disabled;
        }
        
        public int getAButton() {
            return this.AButton;
        }
        
        public int getBButton() {
            return this.BButton;
        }
        
        public int getXButton() {
            return this.XButton;
        }
        
        public int getYButton() {
            return this.YButton;
        }
        
        public int getLBumper() {
            return this.BumperLeft;
        }
        
        public int getRBumper() {
            return this.BumperRight;
        }
        
        public int getL3() {
            return this.LeftStickButton;
        }
        
        public int getR3() {
            return this.RightStickButton;
        }
        
        public int getBackButton() {
            return this.Back;
        }
        
        public int getStartButton() {
            return this.Start;
        }
        
        static {
            tempVec2 = new Vector2();
        }
    }
}
