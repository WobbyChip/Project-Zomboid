// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.Moodles;

import zombie.characters.BodyDamage.Thermoregulator;
import zombie.iso.weather.Temperature;
import zombie.ui.MoodlesUI;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.characters.IsoGameCharacter;

public final class Moodle
{
    MoodleType Type;
    private int Level;
    IsoGameCharacter Parent;
    private int painTimer;
    private Color chevronColor;
    private boolean chevronIsUp;
    private int chevronCount;
    private int chevronMax;
    private static Color colorNeg;
    private static Color colorPos;
    private int cantSprintTimer;
    
    public Moodle(final MoodleType moodleType, final IsoGameCharacter isoGameCharacter) {
        this(moodleType, isoGameCharacter, 0);
    }
    
    public Moodle(final MoodleType type, final IsoGameCharacter parent, final int chevronMax) {
        this.painTimer = 0;
        this.chevronColor = Color.white;
        this.chevronIsUp = true;
        this.chevronCount = 0;
        this.chevronMax = 0;
        this.cantSprintTimer = 300;
        this.Parent = parent;
        this.Type = type;
        this.Level = 0;
        this.chevronMax = chevronMax;
    }
    
    public int getChevronCount() {
        return this.chevronCount;
    }
    
    public boolean isChevronIsUp() {
        return this.chevronIsUp;
    }
    
    public Color getChevronColor() {
        return this.chevronColor;
    }
    
    public boolean chevronDifference(final int n, final boolean b, final Color color) {
        return n != this.chevronCount || b != this.chevronIsUp || color != this.chevronColor;
    }
    
    public void setChevron(int chevronMax, final boolean chevronIsUp, final Color color) {
        if (chevronMax < 0) {
            chevronMax = 0;
        }
        if (chevronMax > this.chevronMax) {
            chevronMax = this.chevronMax;
        }
        this.chevronCount = chevronMax;
        this.chevronIsUp = chevronIsUp;
        this.chevronColor = ((color != null) ? color : Color.white);
    }
    
    public int getLevel() {
        return this.Level;
    }
    
    public void SetLevel(int level) {
        if (level < 0) {
            level = 0;
        }
        if (level > 4) {
            level = 4;
        }
        this.Level = level;
    }
    
    public boolean Update() {
        boolean b = false;
        if (this.Parent.isDead() && this.Type != MoodleType.Dead && this.Type != MoodleType.Zombie) {
            final int n = 0;
            if (n != this.getLevel()) {
                this.SetLevel(n);
                b = true;
            }
            return b;
        }
        if (this.Type == MoodleType.CantSprint) {
            int n2 = 0;
            if (((IsoPlayer)this.Parent).MoodleCantSprint) {
                n2 = 1;
                --this.cantSprintTimer;
                MoodlesUI.getInstance().wiggle(MoodleType.CantSprint);
                if (this.cantSprintTimer == 0) {
                    n2 = 0;
                    this.cantSprintTimer = 300;
                    ((IsoPlayer)this.Parent).MoodleCantSprint = false;
                }
            }
            if (n2 != this.getLevel()) {
                this.SetLevel(n2);
                b = true;
            }
        }
        if (this.Type == MoodleType.Endurance) {
            int n3 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getStats().endurance > 0.75f) {
                    n3 = 0;
                }
                else if (this.Parent.getStats().endurance > 0.5f) {
                    n3 = 1;
                }
                else if (this.Parent.getStats().endurance > 0.25f) {
                    n3 = 2;
                }
                else if (this.Parent.getStats().endurance > 0.1f) {
                    n3 = 3;
                }
                else {
                    n3 = 4;
                }
            }
            if (n3 != this.getLevel()) {
                this.SetLevel(n3);
                b = true;
            }
        }
        if (this.Type == MoodleType.Angry) {
            int n4 = 0;
            if (this.Parent.getStats().Anger > 0.75f) {
                n4 = 4;
            }
            else if (this.Parent.getStats().Anger > 0.5f) {
                n4 = 3;
            }
            else if (this.Parent.getStats().Anger > 0.25f) {
                n4 = 2;
            }
            else if (this.Parent.getStats().Anger > 0.1f) {
                n4 = 1;
            }
            if (n4 != this.getLevel()) {
                this.SetLevel(n4);
                b = true;
            }
        }
        if (this.Type == MoodleType.Tired) {
            int n5 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getStats().fatigue > 0.6f) {
                    n5 = 1;
                }
                if (this.Parent.getStats().fatigue > 0.7f) {
                    n5 = 2;
                }
                if (this.Parent.getStats().fatigue > 0.8f) {
                    n5 = 3;
                }
                if (this.Parent.getStats().fatigue > 0.9f) {
                    n5 = 4;
                }
            }
            if (n5 != this.getLevel()) {
                this.SetLevel(n5);
                b = true;
            }
        }
        if (this.Type == MoodleType.Hungry) {
            int n6 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getStats().hunger > 0.15f) {
                    n6 = 1;
                }
                if (this.Parent.getStats().hunger > 0.25f) {
                    n6 = 2;
                }
                if (this.Parent.getStats().hunger > 0.45f) {
                    n6 = 3;
                }
                if (this.Parent.getStats().hunger > 0.7f) {
                    n6 = 4;
                }
            }
            if (n6 != this.getLevel()) {
                this.SetLevel(n6);
                b = true;
            }
        }
        if (this.Type == MoodleType.Panic) {
            int n7 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getStats().Panic > 6.0f) {
                    n7 = 1;
                }
                if (this.Parent.getStats().Panic > 30.0f) {
                    n7 = 2;
                }
                if (this.Parent.getStats().Panic > 65.0f) {
                    n7 = 3;
                }
                if (this.Parent.getStats().Panic > 80.0f) {
                    n7 = 4;
                }
            }
            if (n7 != this.getLevel()) {
                this.SetLevel(n7);
                b = true;
            }
        }
        if (this.Type == MoodleType.Sick) {
            int n8 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                this.Parent.getStats().Sickness = this.Parent.getBodyDamage().getApparentInfectionLevel() / 100.0f;
                if (this.Parent.getStats().Sickness > 0.25f) {
                    n8 = 1;
                }
                if (this.Parent.getStats().Sickness > 0.5f) {
                    n8 = 2;
                }
                if (this.Parent.getStats().Sickness > 0.75f) {
                    n8 = 3;
                }
                if (this.Parent.getStats().Sickness > 0.9f) {
                    n8 = 4;
                }
            }
            if (n8 != this.getLevel()) {
                this.SetLevel(n8);
                b = true;
            }
        }
        if (this.Type == MoodleType.Bored) {
            int n9 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                this.Parent.getStats().Boredom = this.Parent.getBodyDamage().getBoredomLevel() / 100.0f;
                if (this.Parent.getStats().Boredom > 0.25f) {
                    n9 = 1;
                }
                if (this.Parent.getStats().Boredom > 0.5f) {
                    n9 = 2;
                }
                if (this.Parent.getStats().Boredom > 0.75f) {
                    n9 = 3;
                }
                if (this.Parent.getStats().Boredom > 0.9f) {
                    n9 = 4;
                }
            }
            if (n9 != this.getLevel()) {
                this.SetLevel(n9);
                b = true;
            }
        }
        if (this.Type == MoodleType.Unhappy) {
            int n10 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getBodyDamage().getUnhappynessLevel() > 20.0f) {
                    n10 = 1;
                }
                if (this.Parent.getBodyDamage().getUnhappynessLevel() > 45.0f) {
                    n10 = 2;
                }
                if (this.Parent.getBodyDamage().getUnhappynessLevel() > 60.0f) {
                    n10 = 3;
                }
                if (this.Parent.getBodyDamage().getUnhappynessLevel() > 80.0f) {
                    n10 = 4;
                }
            }
            if (n10 != this.getLevel()) {
                this.SetLevel(n10);
                b = true;
            }
        }
        if (this.Type == MoodleType.Stress) {
            int n11 = 0;
            if (this.Parent.getStats().getStress() > 0.9f) {
                n11 = 4;
            }
            else if (this.Parent.getStats().getStress() > 0.75f) {
                n11 = 3;
            }
            else if (this.Parent.getStats().getStress() > 0.5f) {
                n11 = 2;
            }
            else if (this.Parent.getStats().getStress() > 0.25f) {
                n11 = 1;
            }
            if (n11 != this.getLevel()) {
                this.SetLevel(n11);
                b = true;
            }
        }
        if (this.Type == MoodleType.Thirst) {
            int n12 = 0;
            if (this.Parent.getStats().thirst > 0.12f) {
                n12 = 1;
            }
            if (this.Parent.getStats().thirst > 0.25f) {
                n12 = 2;
            }
            if (this.Parent.getStats().thirst > 0.7f) {
                n12 = 3;
            }
            if (this.Parent.getStats().thirst > 0.84f) {
                n12 = 4;
            }
            if (n12 != this.getLevel()) {
                this.SetLevel(n12);
                b = true;
            }
        }
        if (this.Type == MoodleType.Bleeding) {
            int numPartsBleeding = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                numPartsBleeding = this.Parent.getBodyDamage().getNumPartsBleeding();
                if (numPartsBleeding > 4) {
                    numPartsBleeding = 4;
                }
            }
            if (numPartsBleeding != this.getLevel()) {
                this.SetLevel(numPartsBleeding);
                b = true;
            }
        }
        if (this.Type == MoodleType.Wet) {
            int n13 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getBodyDamage().getWetness() > 15.0f) {
                    n13 = 1;
                }
                if (this.Parent.getBodyDamage().getWetness() > 40.0f) {
                    n13 = 2;
                }
                if (this.Parent.getBodyDamage().getWetness() > 70.0f) {
                    n13 = 3;
                }
                if (this.Parent.getBodyDamage().getWetness() > 90.0f) {
                    n13 = 4;
                }
            }
            if (n13 != this.getLevel()) {
                this.SetLevel(n13);
                b = true;
            }
        }
        if (this.Type == MoodleType.HasACold) {
            int n14 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getBodyDamage().getColdStrength() > 20.0f) {
                    n14 = 1;
                }
                if (this.Parent.getBodyDamage().getColdStrength() > 40.0f) {
                    n14 = 2;
                }
                if (this.Parent.getBodyDamage().getColdStrength() > 60.0f) {
                    n14 = 3;
                }
                if (this.Parent.getBodyDamage().getColdStrength() > 75.0f) {
                    n14 = 4;
                }
            }
            if (n14 != this.getLevel()) {
                this.SetLevel(n14);
                b = true;
            }
        }
        if (this.Type == MoodleType.Injured) {
            int n15 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (100.0f - this.Parent.getBodyDamage().getHealth() > 20.0f) {
                    n15 = 1;
                }
                if (100.0f - this.Parent.getBodyDamage().getHealth() > 40.0f) {
                    n15 = 2;
                }
                if (100.0f - this.Parent.getBodyDamage().getHealth() > 60.0f) {
                    n15 = 3;
                }
                if (100.0f - this.Parent.getBodyDamage().getHealth() > 75.0f) {
                    n15 = 4;
                }
            }
            if (n15 != this.getLevel()) {
                this.SetLevel(n15);
                b = true;
            }
        }
        if (this.Type == MoodleType.Pain) {
            ++this.painTimer;
            if (this.painTimer < 120) {
                return false;
            }
            this.painTimer = 0;
            int n16 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getStats().Pain > 10.0f) {
                    n16 = 1;
                }
                if (this.Parent.getStats().Pain > 20.0f) {
                    n16 = 2;
                }
                if (this.Parent.getStats().Pain > 50.0f) {
                    n16 = 3;
                }
                if (this.Parent.getStats().Pain > 75.0f) {
                    n16 = 4;
                }
            }
            if (n16 != this.getLevel()) {
                this.SetLevel(n16);
                b = true;
            }
        }
        if (this.Type == MoodleType.HeavyLoad) {
            int n17 = 0;
            final float n18 = this.Parent.getInventory().getCapacityWeight() / this.Parent.getMaxWeight();
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (n18 >= 1.75) {
                    n17 = 4;
                }
                else if (n18 >= 1.5) {
                    n17 = 3;
                }
                else if (n18 >= 1.25) {
                    n17 = 2;
                }
                else if (n18 > 1.0f) {
                    n17 = 1;
                }
            }
            if (n17 != this.getLevel()) {
                this.SetLevel(n17);
                b = true;
            }
        }
        if (this.Type == MoodleType.Drunk) {
            int n19 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getStats().Drunkenness > 10.0f) {
                    n19 = 1;
                }
                if (this.Parent.getStats().Drunkenness > 30.0f) {
                    n19 = 2;
                }
                if (this.Parent.getStats().Drunkenness > 50.0f) {
                    n19 = 3;
                }
                if (this.Parent.getStats().Drunkenness > 70.0f) {
                    n19 = 4;
                }
            }
            if (n19 != this.getLevel()) {
                this.SetLevel(n19);
                b = true;
            }
        }
        if (this.Type == MoodleType.Dead) {
            int n20 = 0;
            if (this.Parent.isDead()) {
                n20 = 4;
                if (!this.Parent.getBodyDamage().IsFakeInfected() && this.Parent.getBodyDamage().getInfectionLevel() >= 0.001f) {
                    n20 = 0;
                }
            }
            if (n20 != this.getLevel()) {
                this.SetLevel(n20);
                b = true;
            }
        }
        if (this.Type == MoodleType.Zombie) {
            int n21 = 0;
            if (this.Parent.isDead() && !this.Parent.getBodyDamage().IsFakeInfected() && this.Parent.getBodyDamage().getInfectionLevel() >= 0.001f) {
                n21 = 4;
            }
            if (n21 != this.getLevel()) {
                this.SetLevel(n21);
                b = true;
            }
        }
        if (this.Type == MoodleType.FoodEaten) {
            int n22 = 0;
            if (this.Parent.getBodyDamage().getHealth() != 0.0f) {
                if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > 0.0f) {
                    n22 = 1;
                }
                if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > this.Parent.getBodyDamage().getStandardHealthFromFoodTime()) {
                    n22 = 2;
                }
                if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > this.Parent.getBodyDamage().getStandardHealthFromFoodTime() * 2.0f) {
                    n22 = 3;
                }
                if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > this.Parent.getBodyDamage().getStandardHealthFromFoodTime() * 3.0f) {
                    n22 = 4;
                }
            }
            if (n22 != this.getLevel()) {
                this.SetLevel(n22);
                b = true;
            }
        }
        int n23 = this.chevronCount;
        boolean b2 = this.chevronIsUp;
        Color chevronColor = this.chevronColor;
        if ((this.Type == MoodleType.Hyperthermia || this.Type == MoodleType.Hypothermia) && this.Parent instanceof IsoPlayer) {
            if (this.Parent.getBodyDamage().getTemperature() < 36.5f || this.Parent.getBodyDamage().getTemperature() > 37.5f) {
                final Thermoregulator thermoregulator = this.Parent.getBodyDamage().getThermoregulator();
                if (thermoregulator == null) {
                    n23 = 0;
                }
                else {
                    b2 = thermoregulator.thermalChevronUp();
                    n23 = thermoregulator.thermalChevronCount();
                }
            }
            else {
                n23 = 0;
            }
        }
        if (this.Type == MoodleType.Hyperthermia) {
            int n24 = 0;
            if (n23 > 0) {
                chevronColor = (b2 ? Moodle.colorNeg : Moodle.colorPos);
            }
            if (this.Parent.getBodyDamage().getTemperature() != 0.0f) {
                if (this.Parent.getBodyDamage().getTemperature() > 37.5f) {
                    n24 = 1;
                }
                if (this.Parent.getBodyDamage().getTemperature() > 39.0f) {
                    n24 = 2;
                }
                if (this.Parent.getBodyDamage().getTemperature() > 40.0f) {
                    n24 = 3;
                }
                if (this.Parent.getBodyDamage().getTemperature() > 41.0f) {
                    n24 = 4;
                }
            }
            if (n24 != this.getLevel() || (n24 > 0 && this.chevronDifference(n23, b2, chevronColor))) {
                this.SetLevel(n24);
                this.setChevron(n23, b2, chevronColor);
                b = true;
            }
        }
        if (this.Type == MoodleType.Hypothermia) {
            int n25 = 0;
            if (n23 > 0) {
                chevronColor = (b2 ? Moodle.colorPos : Moodle.colorNeg);
            }
            if (this.Parent.getBodyDamage().getTemperature() != 0.0f) {
                if (this.Parent.getBodyDamage().getTemperature() < 36.5f && this.Parent.getStats().Drunkenness <= 30.0f) {
                    n25 = 1;
                }
                if (this.Parent.getBodyDamage().getTemperature() < 35.0f && this.Parent.getStats().Drunkenness <= 70.0f) {
                    n25 = 2;
                }
                if (this.Parent.getBodyDamage().getTemperature() < 30.0f) {
                    n25 = 3;
                }
                if (this.Parent.getBodyDamage().getTemperature() < 25.0f) {
                    n25 = 4;
                }
            }
            if (n25 != this.getLevel() || (n25 > 0 && this.chevronDifference(n23, b2, chevronColor))) {
                this.SetLevel(n25);
                this.setChevron(n23, b2, chevronColor);
                b = true;
            }
        }
        if (this.Type == MoodleType.Windchill) {
            int n26 = 0;
            if (this.Parent instanceof IsoPlayer) {
                final float windChillAmountForPlayer = Temperature.getWindChillAmountForPlayer((IsoPlayer)this.Parent);
                if (windChillAmountForPlayer > 5.0f) {
                    n26 = 1;
                }
                if (windChillAmountForPlayer > 10.0f) {
                    n26 = 2;
                }
                if (windChillAmountForPlayer > 15.0f) {
                    n26 = 3;
                }
                if (windChillAmountForPlayer > 20.0f) {
                    n26 = 4;
                }
            }
            if (n26 != this.getLevel()) {
                this.SetLevel(n26);
                b = true;
            }
        }
        return b;
    }
    
    static {
        Moodle.colorNeg = new Color(0.88235295f, 0.15686275f, 0.15686275f);
        Moodle.colorPos = new Color(0.15686275f, 0.88235295f, 0.15686275f);
    }
}
