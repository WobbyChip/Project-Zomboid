// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.scripting;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.Iterator;
import zombie.core.Rand;
import java.util.UUID;
import java.util.ArrayList;

public final class RadioScript
{
    private final ArrayList<RadioBroadCast> broadcasts;
    private final ArrayList<ExitOption> exitOptions;
    private String GUID;
    private String name;
    private int startDay;
    private int startDayStamp;
    private int loopMin;
    private int loopMax;
    private int internalStamp;
    private RadioBroadCast currentBroadcast;
    private boolean currentHasAired;
    
    public RadioScript(final String s, final int n, final int n2) {
        this(s, n, n2, UUID.randomUUID().toString());
    }
    
    public RadioScript(final String name, final int loopMin, final int loopMax, final String guid) {
        this.broadcasts = new ArrayList<RadioBroadCast>();
        this.exitOptions = new ArrayList<ExitOption>();
        this.name = "Unnamed radioscript";
        this.startDay = 0;
        this.startDayStamp = 0;
        this.loopMin = 1;
        this.loopMax = 1;
        this.internalStamp = 0;
        this.currentBroadcast = null;
        this.currentHasAired = false;
        this.name = name;
        this.loopMin = loopMin;
        this.loopMax = loopMax;
        this.GUID = guid;
    }
    
    public String GetGUID() {
        return this.GUID;
    }
    
    public String GetName() {
        return this.name;
    }
    
    public int getStartDayStamp() {
        return this.startDayStamp;
    }
    
    public int getStartDay() {
        return this.startDay;
    }
    
    public int getLoopMin() {
        return this.loopMin;
    }
    
    public int getLoopMax() {
        return this.loopMax;
    }
    
    public RadioBroadCast getCurrentBroadcast() {
        return this.currentBroadcast;
    }
    
    public ArrayList<RadioBroadCast> getBroadcastList() {
        return this.broadcasts;
    }
    
    public void clearExitOptions() {
        this.exitOptions.clear();
    }
    
    public void setStartDayStamp(final int startDay) {
        this.startDay = startDay;
        this.startDayStamp = startDay * 24 * 60;
    }
    
    public RadioBroadCast getValidAirBroadcast() {
        if (!this.currentHasAired && this.currentBroadcast != null && this.internalStamp >= this.currentBroadcast.getStartStamp() && this.internalStamp < this.currentBroadcast.getEndStamp()) {
            this.currentHasAired = true;
            return this.currentBroadcast;
        }
        return null;
    }
    
    public void Reset() {
        this.currentBroadcast = null;
        this.currentHasAired = false;
    }
    
    private RadioBroadCast getNextBroadcast() {
        if (this.currentBroadcast == null || this.currentBroadcast.getEndStamp() <= this.internalStamp) {
            for (int i = 0; i < this.broadcasts.size(); ++i) {
                final RadioBroadCast radioBroadCast = this.broadcasts.get(i);
                if (radioBroadCast.getEndStamp() > this.internalStamp) {
                    this.currentHasAired = false;
                    return radioBroadCast;
                }
            }
            return null;
        }
        return this.currentBroadcast;
    }
    
    public RadioBroadCast getBroadcastWithID(final String anObject) {
        for (int i = 0; i < this.broadcasts.size(); ++i) {
            final RadioBroadCast currentBroadcast = this.broadcasts.get(i);
            if (currentBroadcast.getID().equals(anObject)) {
                this.currentBroadcast = currentBroadcast;
                this.currentHasAired = true;
                return currentBroadcast;
            }
        }
        return null;
    }
    
    public boolean UpdateScript(final int n) {
        this.internalStamp = n - this.startDayStamp;
        this.currentBroadcast = this.getNextBroadcast();
        return this.currentBroadcast != null;
    }
    
    public ExitOption getNextScript() {
        int n = 0;
        final int next = Rand.Next(100);
        for (final ExitOption exitOption : this.exitOptions) {
            if (next >= n && next < n + exitOption.getChance()) {
                return exitOption;
            }
            n += exitOption.getChance();
        }
        return null;
    }
    
    public void AddBroadcast(final RadioBroadCast radioBroadCast) {
        this.AddBroadcast(radioBroadCast, false);
    }
    
    public void AddBroadcast(final RadioBroadCast radioBroadCast, final boolean b) {
        boolean b2 = false;
        if (radioBroadCast != null && radioBroadCast.getID() != null) {
            if (b) {
                this.broadcasts.add(radioBroadCast);
                b2 = true;
            }
            else if (radioBroadCast.getStartStamp() >= 0 && radioBroadCast.getEndStamp() > radioBroadCast.getStartStamp()) {
                if (this.broadcasts.size() == 0 || this.broadcasts.get(this.broadcasts.size() - 1).getEndStamp() <= radioBroadCast.getStartStamp()) {
                    this.broadcasts.add(radioBroadCast);
                    b2 = true;
                }
                else if (this.broadcasts.size() > 0) {
                    DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, radioBroadCast.getStartStamp(), radioBroadCast.getEndStamp(), this.broadcasts.get(this.broadcasts.size() - 1).getEndStamp()));
                }
            }
            else {
                DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, radioBroadCast.getStartStamp(), radioBroadCast.getEndStamp()));
            }
        }
        if (!b2) {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (radioBroadCast != null) ? radioBroadCast.getID() : "null", this.name));
        }
    }
    
    public void AddExitOption(final String s, final int n, final int n2) {
        int n3 = n;
        final Iterator<ExitOption> iterator = this.exitOptions.iterator();
        while (iterator.hasNext()) {
            n3 += iterator.next().getChance();
        }
        if (n3 <= 100) {
            this.exitOptions.add(new ExitOption(s, n, n2));
        }
        else {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.name));
        }
    }
    
    public RadioBroadCast getValidAirBroadcastDebug() {
        if (this.currentBroadcast == null || this.currentBroadcast.getEndStamp() <= this.internalStamp) {
            for (int i = 0; i < this.broadcasts.size(); ++i) {
                final RadioBroadCast radioBroadCast = this.broadcasts.get(i);
                if (radioBroadCast.getEndStamp() > this.internalStamp) {
                    return radioBroadCast;
                }
            }
            return null;
        }
        return this.currentBroadcast;
    }
    
    public ArrayList<ExitOption> getExitOptions() {
        return this.exitOptions;
    }
    
    public static final class ExitOption
    {
        private String scriptname;
        private int chance;
        private int startDelay;
        
        public ExitOption(final String scriptname, final int chance, final int startDelay) {
            this.scriptname = "";
            this.chance = 0;
            this.startDelay = 0;
            this.scriptname = scriptname;
            this.chance = chance;
            this.startDelay = startDelay;
        }
        
        public String getScriptname() {
            return this.scriptname;
        }
        
        public int getChance() {
            return this.chance;
        }
        
        public int getStartDelay() {
            return this.startDelay;
        }
    }
}
