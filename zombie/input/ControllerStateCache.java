// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

public class ControllerStateCache
{
    private final Object m_lock;
    private int m_stateIndexUsing;
    private int m_stateIndexPolling;
    private final ControllerState[] m_states;
    
    public ControllerStateCache() {
        this.m_lock = "ControllerStateCache Lock";
        this.m_stateIndexUsing = 0;
        this.m_stateIndexPolling = 1;
        this.m_states = new ControllerState[] { new ControllerState(), new ControllerState() };
    }
    
    public void poll() {
        synchronized (this.m_lock) {
            final ControllerState statePolling = this.getStatePolling();
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
    
    public ControllerState getState() {
        synchronized (this.m_lock) {
            return this.m_states[this.m_stateIndexUsing];
        }
    }
    
    private ControllerState getStatePolling() {
        synchronized (this.m_lock) {
            return this.m_states[this.m_stateIndexPolling];
        }
    }
    
    public void quit() {
        this.m_states[0].quit();
        this.m_states[1].quit();
    }
}
