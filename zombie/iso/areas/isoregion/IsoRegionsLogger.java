// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion;

import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.core.Color;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IsoRegionsLogger
{
    private final ConcurrentLinkedQueue<IsoRegionLog> pool;
    private final ConcurrentLinkedQueue<IsoRegionLog> loggerQueue;
    private final boolean consolePrint;
    private final ArrayList<IsoRegionLog> logs;
    private final int maxLogs = 100;
    private boolean isDirtyUI;
    
    public IsoRegionsLogger(final boolean consolePrint) {
        this.pool = new ConcurrentLinkedQueue<IsoRegionLog>();
        this.loggerQueue = new ConcurrentLinkedQueue<IsoRegionLog>();
        this.logs = new ArrayList<IsoRegionLog>();
        this.isDirtyUI = false;
        this.consolePrint = consolePrint;
    }
    
    public ArrayList<IsoRegionLog> getLogs() {
        return this.logs;
    }
    
    public boolean isDirtyUI() {
        return this.isDirtyUI;
    }
    
    public void unsetDirtyUI() {
        this.isDirtyUI = false;
    }
    
    private IsoRegionLog getLog() {
        IsoRegionLog isoRegionLog = this.pool.poll();
        if (isoRegionLog == null) {
            isoRegionLog = new IsoRegionLog();
        }
        return isoRegionLog;
    }
    
    protected void log(final String s) {
        this.log(s, null);
    }
    
    protected void log(final String str, final Color col) {
        if (Core.bDebug) {
            if (this.consolePrint) {
                DebugLog.IsoRegion.println(str);
            }
            final IsoRegionLog log = this.getLog();
            log.str = str;
            log.type = IsoRegionLogType.Normal;
            log.col = col;
            this.loggerQueue.offer(log);
        }
    }
    
    protected void warn(final String str) {
        DebugLog.IsoRegion.warn((Object)str);
        if (Core.bDebug) {
            final IsoRegionLog log = this.getLog();
            log.str = str;
            log.type = IsoRegionLogType.Warn;
            this.loggerQueue.offer(log);
        }
    }
    
    protected void update() {
        if (!Core.bDebug) {
            return;
        }
        for (IsoRegionLog e = this.loggerQueue.poll(); e != null; e = this.loggerQueue.poll()) {
            if (this.logs.size() >= 100) {
                final IsoRegionLog e2 = this.logs.remove(0);
                e2.col = null;
                this.pool.offer(e2);
            }
            this.logs.add(e);
            this.isDirtyUI = true;
        }
    }
    
    public static class IsoRegionLog
    {
        private String str;
        private IsoRegionLogType type;
        private Color col;
        
        public String getStr() {
            return this.str;
        }
        
        public IsoRegionLogType getType() {
            return this.type;
        }
        
        public Color getColor() {
            if (this.col != null) {
                return this.col;
            }
            if (this.type == IsoRegionLogType.Warn) {
                return Color.red;
            }
            return Color.white;
        }
    }
}
