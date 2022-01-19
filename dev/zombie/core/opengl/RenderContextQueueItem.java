// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import java.util.function.BooleanSupplier;

public final class RenderContextQueueItem
{
    private Runnable m_runnable;
    private boolean m_isFinished;
    private boolean m_isWaiting;
    private Throwable m_runnableThrown;
    private final Object m_waitLock;
    
    private RenderContextQueueItem() {
        this.m_runnableThrown = null;
        this.m_waitLock = "RenderContextQueueItem Wait Lock";
    }
    
    public static RenderContextQueueItem alloc(final Runnable runnable) {
        final RenderContextQueueItem renderContextQueueItem = new RenderContextQueueItem();
        renderContextQueueItem.resetInternal();
        renderContextQueueItem.m_runnable = runnable;
        return renderContextQueueItem;
    }
    
    private void resetInternal() {
        this.m_runnable = null;
        this.m_isFinished = false;
        this.m_runnableThrown = null;
    }
    
    public void waitUntilFinished(final BooleanSupplier booleanSupplier) throws InterruptedException {
        while (!this.isFinished()) {
            if (!booleanSupplier.getAsBoolean()) {
                return;
            }
            synchronized (this.m_waitLock) {
                if (this.isFinished()) {
                    continue;
                }
                this.m_waitLock.wait();
            }
        }
    }
    
    public boolean isFinished() {
        return this.m_isFinished;
    }
    
    public void setWaiting() {
        this.m_isWaiting = true;
    }
    
    public boolean isWaiting() {
        return this.m_isWaiting;
    }
    
    public void invoke() {
        try {
            this.m_runnableThrown = null;
            this.m_runnable.run();
        }
        catch (Throwable runnableThrown) {
            this.m_runnableThrown = runnableThrown;
            DebugLog.General.error("%s thrown during invoke().", runnableThrown.toString());
            ExceptionLogger.logException(runnableThrown);
            synchronized (this.m_waitLock) {
                this.m_isFinished = true;
                this.m_waitLock.notifyAll();
            }
        }
        finally {
            synchronized (this.m_waitLock) {
                this.m_isFinished = true;
                this.m_waitLock.notifyAll();
            }
        }
    }
    
    public Throwable getThrown() {
        return this.m_runnableThrown;
    }
    
    public void notifyWaitingListeners() {
        synchronized (this.m_waitLock) {
            this.m_waitLock.notifyAll();
        }
    }
}
