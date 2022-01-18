// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action;

public final class ActionContextEvents
{
    private Event m_firstEvent;
    private Event m_eventPool;
    
    public void add(final String name, final int layer) {
        if (this.contains(name, layer, false)) {
            return;
        }
        final Event allocEvent = this.allocEvent();
        allocEvent.name = name;
        allocEvent.layer = layer;
        allocEvent.next = this.m_firstEvent;
        this.m_firstEvent = allocEvent;
    }
    
    public boolean contains(final String s, final int n) {
        return this.contains(s, n, true);
    }
    
    public boolean contains(final String anotherString, final int n, final boolean b) {
        for (Event event = this.m_firstEvent; event != null; event = event.next) {
            if (event.name.equalsIgnoreCase(anotherString)) {
                if (n == -1) {
                    return true;
                }
                if (event.layer == n) {
                    return true;
                }
                if (b && event.layer == -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void clear() {
        if (this.m_firstEvent == null) {
            return;
        }
        Event event;
        for (event = this.m_firstEvent; event.next != null; event = event.next) {}
        event.next = this.m_eventPool;
        this.m_eventPool = this.m_firstEvent;
        this.m_firstEvent = null;
    }
    
    public void clearEvent(final String anotherString) {
        Event event = null;
        Event next;
        for (Event firstEvent = this.m_firstEvent; firstEvent != null; firstEvent = next) {
            next = firstEvent.next;
            if (firstEvent.name.equalsIgnoreCase(anotherString)) {
                this.releaseEvent(firstEvent, event);
            }
            else {
                event = firstEvent;
            }
        }
    }
    
    private Event allocEvent() {
        if (this.m_eventPool == null) {
            return new Event();
        }
        final Event eventPool = this.m_eventPool;
        this.m_eventPool = eventPool.next;
        return eventPool;
    }
    
    private void releaseEvent(final Event eventPool, final Event event) {
        if (event == null) {
            assert eventPool == this.m_firstEvent;
            this.m_firstEvent = eventPool.next;
        }
        else {
            assert eventPool != this.m_firstEvent;
            assert event.next == eventPool;
            event.next = eventPool.next;
        }
        eventPool.next = this.m_eventPool;
        this.m_eventPool = eventPool;
    }
    
    private static final class Event
    {
        int layer;
        String name;
        Event next;
    }
}
