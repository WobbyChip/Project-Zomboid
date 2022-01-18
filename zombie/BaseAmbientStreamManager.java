// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.iso.RoomDef;

public abstract class BaseAmbientStreamManager
{
    public abstract void stop();
    
    public abstract void doAlarm(final RoomDef p0);
    
    public abstract void doGunEvent();
    
    public abstract void init();
    
    public abstract void addBlend(final String p0, final float p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5);
    
    protected abstract void addRandomAmbient();
    
    public abstract void doOneShotAmbients();
    
    public abstract void update();
    
    public abstract void addAmbient(final String p0, final int p1, final int p2, final int p3, final float p4);
    
    public abstract void addAmbientEmitter(final float p0, final float p1, final int p2, final String p3);
    
    public abstract void addDaytimeAmbientEmitter(final float p0, final float p1, final int p2, final String p3);
}
