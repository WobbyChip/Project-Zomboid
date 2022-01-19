// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

import org.lwjglx.input.Controllers;
import org.lwjglx.input.GamepadState;
import org.lwjglx.input.Controller;

public class ControllerState
{
    private boolean m_isCreated;
    private boolean m_wasPolled;
    private final Controller[] m_controllers;
    private final GamepadState[] m_gamepadState;
    
    ControllerState() {
        this.m_isCreated = false;
        this.m_wasPolled = false;
        this.m_controllers = new Controller[16];
        this.m_gamepadState = new GamepadState[16];
        for (int i = 0; i < this.m_controllers.length; ++i) {
            this.m_gamepadState[i] = new GamepadState();
        }
    }
    
    public void poll() {
        final boolean b = !this.m_isCreated;
        if (!(this.m_isCreated = (this.m_isCreated || Controllers.isCreated()))) {
            return;
        }
        if (b) {}
        this.m_wasPolled = true;
        Controllers.poll(this.m_gamepadState);
        for (int i = 0; i < Controllers.getControllerCount(); ++i) {
            this.m_controllers[i] = Controllers.getController(i);
        }
    }
    
    public boolean wasPolled() {
        return this.m_wasPolled;
    }
    
    public void set(final ControllerState controllerState) {
        this.m_isCreated = controllerState.m_isCreated;
        for (int i = 0; i < this.m_controllers.length; ++i) {
            this.m_controllers[i] = controllerState.m_controllers[i];
            if (this.m_controllers[i] != null) {
                this.m_gamepadState[i].set(controllerState.m_gamepadState[i]);
                this.m_controllers[i].gamepadState = this.m_gamepadState[i];
            }
        }
        this.m_wasPolled = controllerState.m_wasPolled;
    }
    
    public void reset() {
        this.m_wasPolled = false;
    }
    
    public boolean isCreated() {
        return this.m_isCreated;
    }
    
    public Controller getController(final int n) {
        return this.m_controllers[n];
    }
    
    public void quit() {
        for (int i = 0; i < this.m_controllers.length; ++i) {
            this.m_gamepadState[i].quit();
        }
    }
}
