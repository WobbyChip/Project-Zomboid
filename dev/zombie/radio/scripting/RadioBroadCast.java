// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.scripting;

import java.util.ArrayList;

public final class RadioBroadCast
{
    private static final RadioLine pauseLine;
    private final ArrayList<RadioLine> lines;
    private String ID;
    private int startStamp;
    private int endStamp;
    private int lineCount;
    private RadioBroadCast preSegment;
    private RadioBroadCast postSegment;
    private boolean hasDonePreSegment;
    private boolean hasDonePostSegment;
    private boolean hasDonePostPause;
    
    public RadioBroadCast(final String id, final int startStamp, final int endStamp) {
        this.lines = new ArrayList<RadioLine>();
        this.ID = "";
        this.startStamp = 0;
        this.endStamp = 0;
        this.lineCount = 0;
        this.preSegment = null;
        this.postSegment = null;
        this.hasDonePreSegment = false;
        this.hasDonePostSegment = false;
        this.hasDonePostPause = false;
        this.ID = id;
        this.startStamp = startStamp;
        this.endStamp = endStamp;
    }
    
    public String getID() {
        return this.ID;
    }
    
    public int getStartStamp() {
        return this.startStamp;
    }
    
    public int getEndStamp() {
        return this.endStamp;
    }
    
    public void resetLineCounter() {
        this.resetLineCounter(true);
    }
    
    public void resetLineCounter(final boolean b) {
        this.lineCount = 0;
        if (b) {
            if (this.preSegment != null) {
                this.preSegment.resetLineCounter(false);
            }
            if (this.postSegment != null) {
                this.postSegment.resetLineCounter(false);
            }
        }
    }
    
    public void setPreSegment(final RadioBroadCast preSegment) {
        this.preSegment = preSegment;
    }
    
    public void setPostSegment(final RadioBroadCast postSegment) {
        this.postSegment = postSegment;
    }
    
    public RadioLine getNextLine() {
        return this.getNextLine(true);
    }
    
    public RadioLine getNextLine(final boolean b) {
        RadioLine radioLine = null;
        if (b && !this.hasDonePreSegment && this.lineCount == 0 && this.preSegment != null) {
            final RadioLine nextLine = this.preSegment.getNextLine();
            if (nextLine != null) {
                return nextLine;
            }
            this.hasDonePreSegment = true;
            return RadioBroadCast.pauseLine;
        }
        else {
            if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
                radioLine = this.lines.get(this.lineCount);
            }
            if (!b || radioLine != null || this.postSegment == null) {
                ++this.lineCount;
                return radioLine;
            }
            if (!this.hasDonePostPause) {
                this.hasDonePostPause = true;
                return RadioBroadCast.pauseLine;
            }
            return this.postSegment.getNextLine();
        }
    }
    
    public int getCurrentLineNumber() {
        return this.lineCount;
    }
    
    public void setCurrentLineNumber(final int lineCount) {
        this.lineCount = lineCount;
        if (this.lineCount < 0) {
            this.lineCount = 0;
        }
    }
    
    public RadioLine getCurrentLine() {
        if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
            return this.lines.get(this.lineCount);
        }
        return null;
    }
    
    public String PeekNextLineText() {
        if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
            return (this.lines.get(this.lineCount) != null && this.lines.get(this.lineCount).getText() != null) ? this.lines.get(this.lineCount).getText() : "Error";
        }
        return "None";
    }
    
    public void AddRadioLine(final RadioLine e) {
        if (e != null) {
            this.lines.add(e);
        }
    }
    
    public ArrayList<RadioLine> getLines() {
        return this.lines;
    }
    
    static {
        pauseLine = new RadioLine("~", 0.5f, 0.5f, 0.5f);
    }
}
