// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.sadisticAIDirector;

import zombie.GameTime;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;

public final class SleepingEventData
{
    protected int forceWakeUpTime;
    protected boolean zombiesIntruders;
    protected int nightmareWakeUp;
    protected IsoWindow weakestWindow;
    protected IsoDoor openDoor;
    protected boolean bRaining;
    protected boolean bWasRainingAtStart;
    protected double rainTimeStartHours;
    protected float sleepingTime;
    
    public SleepingEventData() {
        this.forceWakeUpTime = -1;
        this.zombiesIntruders = true;
        this.nightmareWakeUp = -1;
        this.weakestWindow = null;
        this.openDoor = null;
        this.bRaining = false;
        this.bWasRainingAtStart = false;
        this.rainTimeStartHours = -1.0;
        this.sleepingTime = 8.0f;
    }
    
    public void reset() {
        this.forceWakeUpTime = -1;
        this.zombiesIntruders = false;
        this.nightmareWakeUp = -1;
        this.openDoor = null;
        this.weakestWindow = null;
        this.bRaining = false;
        this.bWasRainingAtStart = false;
        this.rainTimeStartHours = -1.0;
        this.sleepingTime = 8.0f;
    }
    
    public double getHoursSinceRainStarted() {
        return GameTime.getInstance().getWorldAgeHours() - this.rainTimeStartHours;
    }
}
