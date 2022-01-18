// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

public final class KeyboardStateCache
{
    private final Object m_lock;
    private int m_stateIndexUsing;
    private int m_stateIndexPolling;
    private final KeyboardState[] m_states;
    
    public KeyboardStateCache() {
        this.m_lock = "KeyboardStateCache Lock";
        this.m_stateIndexUsing = 0;
        this.m_stateIndexPolling = 1;
        this.m_states = new KeyboardState[] { new KeyboardState(), new KeyboardState() };
    }
    
    public void poll() {
        synchronized (this.m_lock) {
            final KeyboardState statePolling = this.getStatePolling();
            if (statePolling.wasPolled()) {
                return;
            }
            statePolling.poll();
        }
    }
    
    public void swap() {
        synchronized (this.m_lock) {
            if (!this.getStatePolling().wasPolled()) {
                return;
            }
            this.m_stateIndexUsing = this.m_stateIndexPolling;
            this.m_stateIndexPolling = ((this.m_stateIndexPolling != 1) ? 1 : 0);
            this.getStatePolling().set(this.getState());
            this.getStatePolling().reset();
        }
    }
    
    public KeyboardState getState() {
        synchronized (this.m_lock) {
            return this.m_states[this.m_stateIndexUsing];
        }
    }
    
    public KeyboardState getStatePolling() {
        synchronized (this.m_lock) {
            return this.m_states[this.m_stateIndexPolling];
        }
    }
}
