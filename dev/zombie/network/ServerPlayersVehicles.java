// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.vehicles.VehiclesDB2;
import zombie.savefile.ServerPlayerDB;
import zombie.core.logger.ExceptionLogger;

public class ServerPlayersVehicles
{
    public static final ServerPlayersVehicles instance;
    private SPVThread m_thread;
    
    public ServerPlayersVehicles() {
        this.m_thread = null;
    }
    
    public void init() {
        (this.m_thread = new SPVThread()).setName("ServerPlayersVehicles");
        this.m_thread.setDaemon(true);
        this.m_thread.start();
    }
    
    public void stop() {
        if (this.m_thread != null) {
            this.m_thread.m_bStop = true;
            while (this.m_thread.isAlive()) {
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException ex) {}
            }
            this.m_thread = null;
        }
    }
    
    static {
        instance = new ServerPlayersVehicles();
    }
    
    private static final class SPVThread extends Thread
    {
        boolean m_bStop;
        
        private SPVThread() {
            this.m_bStop = false;
        }
        
        @Override
        public void run() {
            while (!this.m_bStop) {
                try {
                    this.runInner();
                }
                catch (Throwable t) {
                    ExceptionLogger.logException(t);
                }
            }
        }
        
        void runInner() {
            ServerPlayerDB.getInstance().process();
            VehiclesDB2.instance.updateWorldStreamer();
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex) {}
        }
    }
}
