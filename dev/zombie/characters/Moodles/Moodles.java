// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.Moodles;

import zombie.core.Color;
import zombie.characters.IsoGameCharacter;
import java.util.Stack;

public final class Moodles
{
    boolean MoodlesStateChanged;
    private Stack<Moodle> MoodleList;
    private final IsoGameCharacter Parent;
    
    public Moodles(final IsoGameCharacter parent) {
        this.MoodlesStateChanged = false;
        this.MoodleList = new Stack<Moodle>();
        this.Parent = parent;
        this.MoodleList.add(new Moodle(MoodleType.Endurance, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Tired, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Hungry, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Panic, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Sick, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Bored, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Unhappy, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Bleeding, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Wet, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.HasACold, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Angry, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Stress, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Thirst, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Injured, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Pain, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.HeavyLoad, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Drunk, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Dead, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Zombie, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.FoodEaten, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Hyperthermia, this.Parent, 3));
        this.MoodleList.add(new Moodle(MoodleType.Hypothermia, this.Parent, 3));
        this.MoodleList.add(new Moodle(MoodleType.Windchill, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.CantSprint, this.Parent));
    }
    
    public int getGoodBadNeutral(final int index) {
        return MoodleType.GoodBadNeutral(this.MoodleList.get(index).Type);
    }
    
    public String getMoodleDisplayString(final int n) {
        return MoodleType.getDisplayName(this.MoodleList.get(n).Type, this.MoodleList.get(n).getLevel());
    }
    
    public String getMoodleDescriptionString(final int n) {
        return MoodleType.getDescriptionText(this.MoodleList.get(n).Type, this.MoodleList.get(n).getLevel());
    }
    
    public int getMoodleLevel(final int index) {
        return this.MoodleList.get(index).getLevel();
    }
    
    public int getMoodleLevel(final MoodleType moodleType) {
        return this.MoodleList.get(MoodleType.ToIndex(moodleType)).getLevel();
    }
    
    public int getMoodleChevronCount(final int index) {
        return this.MoodleList.get(index).getChevronCount();
    }
    
    public boolean getMoodleChevronIsUp(final int index) {
        return this.MoodleList.get(index).isChevronIsUp();
    }
    
    public Color getMoodleChevronColor(final int index) {
        return this.MoodleList.get(index).getChevronColor();
    }
    
    public MoodleType getMoodleType(final int index) {
        return this.MoodleList.get(index).Type;
    }
    
    public int getNumMoodles() {
        return this.MoodleList.size();
    }
    
    public void Randomise() {
    }
    
    public boolean UI_RefreshNeeded() {
        if (this.MoodlesStateChanged) {
            this.MoodlesStateChanged = false;
            return true;
        }
        return false;
    }
    
    public void setMoodlesStateChanged(final boolean moodlesStateChanged) {
        this.MoodlesStateChanged = moodlesStateChanged;
    }
    
    public void Update() {
        for (int i = 0; i < this.MoodleList.size(); ++i) {
            if (((Moodle)this.MoodleList.get(i)).Update()) {
                this.MoodlesStateChanged = true;
            }
        }
    }
}
