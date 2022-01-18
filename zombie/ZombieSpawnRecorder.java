// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.ArrayList;
import zombie.iso.areas.IsoRoom;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;

public final class ZombieSpawnRecorder
{
    public static final ZombieSpawnRecorder instance;
    public ZLogger m_logger;
    private final StringBuilder m_stringBuilder;
    
    public ZombieSpawnRecorder() {
        this.m_stringBuilder = new StringBuilder();
    }
    
    public void init() {
        if (this.m_logger != null) {
            this.m_logger.write("================================================================================");
            return;
        }
        LoggerManager.init();
        LoggerManager.createLogger("ZombieSpawn", Core.bDebug);
        this.m_logger = LoggerManager.getLogger("ZombieSpawn");
    }
    
    public void quit() {
        if (this.m_logger == null) {
            return;
        }
        if (this.m_stringBuilder.length() > 0) {
            this.m_logger.write(this.m_stringBuilder.toString());
            this.m_stringBuilder.setLength(0);
        }
    }
    
    public void record(final IsoZombie isoZombie, final String str) {
        if (isoZombie == null || isoZombie.getCurrentSquare() == null) {
            return;
        }
        if (this.m_logger == null) {
            return;
        }
        final IsoGridSquare currentSquare = isoZombie.getCurrentSquare();
        this.m_stringBuilder.append("reason = ");
        this.m_stringBuilder.append(str);
        this.m_stringBuilder.append(" x,y,z = ");
        this.m_stringBuilder.append(currentSquare.x);
        this.m_stringBuilder.append(',');
        this.m_stringBuilder.append(currentSquare.y);
        this.m_stringBuilder.append(',');
        this.m_stringBuilder.append(currentSquare.z);
        final IsoRoom room = currentSquare.getRoom();
        if (room != null && room.def != null) {
            this.m_stringBuilder.append(" room = ");
            this.m_stringBuilder.append(room.def.name);
        }
        this.m_stringBuilder.append(System.lineSeparator());
        if (this.m_stringBuilder.length() >= 1024) {
            this.m_logger.write(this.m_stringBuilder.toString());
            this.m_stringBuilder.setLength(0);
        }
    }
    
    public void record(final ArrayList<IsoZombie> list, final String s) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            this.record(list.get(i), s);
        }
    }
    
    static {
        instance = new ZombieSpawnRecorder();
    }
}
