// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

import org.lwjglx.input.Mouse;

public final class MouseState
{
    private boolean m_isCreated;
    private boolean[] m_buttonDownStates;
    private int m_mouseX;
    private int m_mouseY;
    private int m_wheelDelta;
    private boolean m_wasPolled;
    
    public MouseState() {
        this.m_isCreated = false;
        this.m_buttonDownStates = null;
        this.m_mouseX = -1;
        this.m_mouseY = -1;
        this.m_wheelDelta = 0;
        this.m_wasPolled = false;
    }
    
    public void poll() {
        final boolean b = !this.m_isCreated;
        if (!(this.m_isCreated = (this.m_isCreated || Mouse.isCreated()))) {
            return;
        }
        if (b) {
            this.m_buttonDownStates = new boolean[Mouse.getButtonCount()];
        }
        this.m_mouseX = Mouse.getX();
        this.m_mouseY = Mouse.getY();
        this.m_wheelDelta = Mouse.getDWheel();
        this.m_wasPolled = true;
        for (int i = 0; i < this.m_buttonDownStates.length; ++i) {
            this.m_buttonDownStates[i] = Mouse.isButtonDown(i);
        }
    }
    
    public boolean wasPolled() {
        return this.m_wasPolled;
    }
    
    public void set(final MouseState mouseState) {
        this.m_isCreated = mouseState.m_isCreated;
        if (mouseState.m_buttonDownStates != null) {
            if (this.m_buttonDownStates == null || this.m_buttonDownStates.length != mouseState.m_buttonDownStates.length) {
                this.m_buttonDownStates = new boolean[mouseState.m_buttonDownStates.length];
            }
            System.arraycopy(mouseState.m_buttonDownStates, 0, this.m_buttonDownStates, 0, this.m_buttonDownStates.length);
        }
        else {
            this.m_buttonDownStates = null;
        }
        this.m_mouseX = mouseState.m_mouseX;
        this.m_mouseY = mouseState.m_mouseY;
        this.m_wheelDelta = mouseState.m_wheelDelta;
        this.m_wasPolled = mouseState.m_wasPolled;
    }
    
    public void reset() {
        this.m_wasPolled = false;
    }
    
    public boolean isCreated() {
        return this.m_isCreated;
    }
    
    public int getX() {
        return this.m_mouseX;
    }
    
    public int getY() {
        return this.m_mouseY;
    }
    
    public int getDWheel() {
        return this.m_wheelDelta;
    }
    
    public void resetDWheel() {
        this.m_wheelDelta = 0;
    }
    
    public boolean isButtonDown(final int n) {
        return n < this.m_buttonDownStates.length && this.m_buttonDownStates[n];
    }
    
    public int getButtonCount() {
        return this.isCreated() ? this.m_buttonDownStates.length : 0;
    }
    
    public void setCursorPosition(final int n, final int n2) {
        Mouse.setCursorPosition(n, n2);
    }
}
