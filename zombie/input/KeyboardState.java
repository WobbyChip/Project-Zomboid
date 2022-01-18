// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

import org.lwjglx.input.Keyboard;
import org.lwjglx.input.KeyEventQueue;

public final class KeyboardState
{
    private boolean m_isCreated;
    private boolean[] m_keyDownStates;
    private final KeyEventQueue m_keyEventQueue;
    private boolean m_wasPolled;
    
    public KeyboardState() {
        this.m_isCreated = false;
        this.m_keyDownStates = null;
        this.m_keyEventQueue = new KeyEventQueue();
        this.m_wasPolled = false;
    }
    
    public void poll() {
        final boolean b = !this.m_isCreated;
        if (!(this.m_isCreated = (this.m_isCreated || Keyboard.isCreated()))) {
            return;
        }
        if (b) {
            this.m_keyDownStates = new boolean[256];
        }
        this.m_wasPolled = true;
        for (int i = 0; i < this.m_keyDownStates.length; ++i) {
            this.m_keyDownStates[i] = Keyboard.isKeyDown(i);
        }
    }
    
    public boolean wasPolled() {
        return this.m_wasPolled;
    }
    
    public void set(final KeyboardState keyboardState) {
        this.m_isCreated = keyboardState.m_isCreated;
        if (keyboardState.m_keyDownStates != null) {
            if (this.m_keyDownStates == null || this.m_keyDownStates.length != keyboardState.m_keyDownStates.length) {
                this.m_keyDownStates = new boolean[keyboardState.m_keyDownStates.length];
            }
            System.arraycopy(keyboardState.m_keyDownStates, 0, this.m_keyDownStates, 0, this.m_keyDownStates.length);
        }
        else {
            this.m_keyDownStates = null;
        }
        this.m_wasPolled = keyboardState.m_wasPolled;
    }
    
    public void reset() {
        this.m_wasPolled = false;
    }
    
    public boolean isCreated() {
        return this.m_isCreated;
    }
    
    public boolean isKeyDown(final int n) {
        return this.m_keyDownStates[n];
    }
    
    public int getKeyCount() {
        return this.m_keyDownStates.length;
    }
    
    public KeyEventQueue getEventQueue() {
        return this.m_keyEventQueue;
    }
}
