// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.input;

import zombie.input.ControllerState;
import org.lwjglx.input.Mouse;
import zombie.input.GameKeyboard;
import zombie.core.Core;
import zombie.Lua.LuaEventManager;
import zombie.input.JoypadManager;
import org.lwjglx.input.Keyboard;
import zombie.input.ControllerStateCache;
import java.util.ArrayList;
import org.lwjglx.input.Controller;

public final class Input
{
    public static final int ANY_CONTROLLER = -1;
    private final Controller[] controllers;
    private final ArrayList<Controller> newlyConnected;
    private final ArrayList<Controller> newlyDisconnected;
    private final boolean[][] controllerPressed;
    private final boolean[][] controllerWasPressed;
    private final float[][] controllerPov;
    private final ControllerStateCache m_controllerStateCache;
    
    public Input() {
        this.controllers = new Controller[16];
        this.newlyConnected = new ArrayList<Controller>();
        this.newlyDisconnected = new ArrayList<Controller>();
        this.controllerPressed = new boolean[16][15];
        this.controllerWasPressed = new boolean[16][15];
        this.controllerPov = new float[16][2];
        this.m_controllerStateCache = new ControllerStateCache();
    }
    
    public static String getKeyName(final int n) {
        String keyName = Keyboard.getKeyName(n);
        if ("LSHIFT".equals(keyName)) {
            keyName = "Left SHIFT";
        }
        if ("RSHIFT".equals(keyName)) {
            keyName = "Right SHIFT";
        }
        if ("LMENU".equals(keyName)) {
            keyName = "Left ALT";
        }
        else if ("RMENU".equals(keyName)) {
            keyName = "Right ALT";
        }
        return keyName;
    }
    
    public static int getKeyCode(final String s) {
        if ("Right SHIFT".equals(s)) {
            return 54;
        }
        if ("Left SHIFT".equals(s)) {
            return 42;
        }
        if ("Left ALT".equals(s)) {
            return 56;
        }
        if ("Right ALT".equals(s)) {
            return 184;
        }
        return Keyboard.getKeyIndex(s);
    }
    
    public int getControllerCount() {
        return this.controllers.length;
    }
    
    public int getAxisCount(final int n) {
        final Controller controller = this.getController(n);
        if (controller == null) {
            return 0;
        }
        return controller.getAxisCount();
    }
    
    public float getAxisValue(final int n, final int n2) {
        final Controller controller = this.getController(n);
        if (controller == null) {
            return 0.0f;
        }
        return controller.getAxisValue(n2);
    }
    
    public String getAxisName(final int n, final int n2) {
        final Controller controller = this.getController(n);
        if (controller == null) {
            return null;
        }
        return controller.getAxisName(n2);
    }
    
    public boolean isControllerLeftD(final int n) {
        if (n == -1) {
            for (int i = 0; i < this.controllers.length; ++i) {
                if (this.isControllerLeftD(i)) {
                    return true;
                }
            }
            return false;
        }
        final Controller controller = this.getController(n);
        return controller != null && controller.getPovX() < -0.5f;
    }
    
    public boolean isControllerRightD(final int n) {
        if (n == -1) {
            for (int i = 0; i < this.controllers.length; ++i) {
                if (this.isControllerRightD(i)) {
                    return true;
                }
            }
            return false;
        }
        final Controller controller = this.getController(n);
        return controller != null && controller.getPovX() > 0.5f;
    }
    
    public boolean isControllerUpD(final int n) {
        if (n == -1) {
            for (int i = 0; i < this.controllers.length; ++i) {
                if (this.isControllerUpD(i)) {
                    return true;
                }
            }
            return false;
        }
        final Controller controller = this.getController(n);
        return controller != null && controller.getPovY() < -0.5f;
    }
    
    public boolean isControllerDownD(final int n) {
        if (n == -1) {
            for (int i = 0; i < this.controllers.length; ++i) {
                if (this.isControllerDownD(i)) {
                    return true;
                }
            }
            return false;
        }
        final Controller controller = this.getController(n);
        return controller != null && controller.getPovY() > 0.5f;
    }
    
    private Controller checkControllerButton(final int n, final int n2) {
        final Controller controller = this.getController(n);
        if (controller == null) {
            return null;
        }
        if (n2 < 0 || n2 >= controller.getButtonCount()) {
            return null;
        }
        return controller;
    }
    
    public boolean isButtonPressedD(final int n, final int n2) {
        if (n2 == -1) {
            for (int i = 0; i < this.controllers.length; ++i) {
                if (this.isButtonPressedD(n, i)) {
                    return true;
                }
            }
            return false;
        }
        return this.checkControllerButton(n2, n) != null && this.controllerPressed[n2][n];
    }
    
    public boolean wasButtonPressed(final int n, final int n2) {
        return this.checkControllerButton(n, n2) != null && this.controllerWasPressed[n][n2];
    }
    
    public boolean isButtonStartPress(final int n, final int n2) {
        return !this.wasButtonPressed(n, n2) && this.isButtonPressedD(n2, n);
    }
    
    public boolean isButtonReleasePress(final int n, final int n2) {
        return this.wasButtonPressed(n, n2) && !this.isButtonPressedD(n2, n);
    }
    
    public void initControllers() {
        this.updateGameThread();
    }
    
    private void onControllerConnected(final Controller controller) {
        JoypadManager.instance.onControllerConnected(controller);
        LuaEventManager.triggerEvent("OnGamepadConnect", controller.getID());
    }
    
    private void onControllerDisconnected(final Controller controller) {
        JoypadManager.instance.onControllerDisconnected(controller);
        LuaEventManager.triggerEvent("OnGamepadDisconnect", controller.getID());
    }
    
    public void poll() {
        if (!Core.getInstance().isDoingTextEntry()) {
            while (GameKeyboard.getEventQueuePolling().next()) {}
        }
        while (Mouse.next()) {}
        this.m_controllerStateCache.poll();
    }
    
    public Controller getController(final int n) {
        if (n < 0 || n >= this.controllers.length) {
            return null;
        }
        return this.controllers[n];
    }
    
    public int getButtonCount(final int n) {
        final Controller controller = this.getController(n);
        return (controller == null) ? null : Integer.valueOf(controller.getButtonCount());
    }
    
    public String getButtonName(final int n, final int n2) {
        final Controller controller = this.getController(n);
        return (controller == null) ? null : controller.getButtonName(n2);
    }
    
    public void updateGameThread() {
        if (!this.m_controllerStateCache.getState().isCreated()) {
            this.m_controllerStateCache.swap();
            return;
        }
        if (this.checkConnectDisconnect(this.m_controllerStateCache.getState())) {
            for (int i = 0; i < this.newlyDisconnected.size(); ++i) {
                this.onControllerDisconnected(this.newlyDisconnected.get(i));
            }
            for (int j = 0; j < this.newlyConnected.size(); ++j) {
                this.onControllerConnected(this.newlyConnected.get(j));
            }
        }
        for (int k = 0; k < this.getControllerCount(); ++k) {
            final Controller controller = this.getController(k);
            if (controller != null) {
                for (int buttonCount = controller.getButtonCount(), l = 0; l < buttonCount; ++l) {
                    this.controllerWasPressed[k][l] = this.controllerPressed[k][l];
                    if (this.controllerPressed[k][l] && !controller.isButtonPressed(l)) {
                        this.controllerPressed[k][l] = false;
                    }
                    else if (!this.controllerPressed[k][l] && controller.isButtonPressed(l)) {
                        this.controllerPressed[k][l] = true;
                        JoypadManager.instance.onPressed(k, l);
                    }
                }
                for (int axisCount = controller.getAxisCount(), n = 0; n < axisCount; ++n) {
                    final float axisValue = controller.getAxisValue(n);
                    if ((controller.isGamepad() && n == 4) || n == 5) {
                        if (axisValue > 0.0f) {
                            JoypadManager.instance.onPressedTrigger(k, n);
                        }
                    }
                    else {
                        if (axisValue < -0.5f) {
                            JoypadManager.instance.onPressedAxisNeg(k, n);
                        }
                        if (axisValue > 0.5f) {
                            JoypadManager.instance.onPressedAxis(k, n);
                        }
                    }
                }
                final float povX = controller.getPovX();
                final float povY = controller.getPovY();
                if (povX != this.controllerPov[k][0] || povY != this.controllerPov[k][1]) {
                    this.controllerPov[k][0] = povX;
                    this.controllerPov[k][1] = povY;
                    JoypadManager.instance.onPressedPov(k);
                }
            }
        }
        this.m_controllerStateCache.swap();
    }
    
    private boolean checkConnectDisconnect(final ControllerState controllerState) {
        boolean b = false;
        this.newlyConnected.clear();
        this.newlyDisconnected.clear();
        for (int i = 0; i < 16; ++i) {
            Controller controller = controllerState.getController(i);
            if (controller != this.controllers[i]) {
                b = true;
                if (controller == null || !controller.isGamepad()) {
                    if (this.controllers[i] != null) {
                        this.newlyDisconnected.add(this.controllers[i]);
                    }
                    controller = null;
                }
                else {
                    this.newlyConnected.add(controller);
                }
                this.controllers[i] = controller;
            }
        }
        return b;
    }
    
    public void quit() {
        this.m_controllerStateCache.quit();
    }
}
