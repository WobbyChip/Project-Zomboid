// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

public final class MouseStateCache
{
    private final Object m_lock;
    private int m_stateIndexUsing;
    private int m_stateIndexPolling;
    private final MouseState[] m_states;
    
    public MouseStateCache() {
        this.m_lock = "MouseStateCache Lock";
        this.m_stateIndexUsing = 0;
        this.m_stateIndexPolling = 1;
        this.m_states = new MouseState[] { new MouseState(), new MouseState() };
    }
    
    public void poll() {
        synchronized (this.m_lock) {
            final MouseState statePolling = this.getStatePolling();
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
    
    public MouseState getState() {
        synchronized (this.m_lock) {
            return this.m_states[this.m_stateIndexUsing];
        }
    }
    
    private MouseState getStatePolling() {
        synchronized (this.m_lock) {
            return this.m_states[this.m_stateIndexPolling];
        }
    }
}
