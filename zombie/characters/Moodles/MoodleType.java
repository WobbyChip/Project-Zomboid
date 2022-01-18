// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.Moodles;

import zombie.core.Translator;

public enum MoodleType
{
    Endurance, 
    Tired, 
    Hungry, 
    Panic, 
    Sick, 
    Bored, 
    Unhappy, 
    Bleeding, 
    Wet, 
    HasACold, 
    Angry, 
    Stress, 
    Thirst, 
    Injured, 
    Pain, 
    HeavyLoad, 
    Drunk, 
    Dead, 
    Zombie, 
    Hyperthermia, 
    Hypothermia, 
    Windchill, 
    CantSprint, 
    FoodEaten, 
    MAX;
    
    public static MoodleType FromIndex(final int n) {
        switch (n) {
            case 0: {
                return MoodleType.Endurance;
            }
            case 1: {
                return MoodleType.Tired;
            }
            case 2: {
                return MoodleType.Hungry;
            }
            case 3: {
                return MoodleType.Panic;
            }
            case 4: {
                return MoodleType.Sick;
            }
            case 5: {
                return MoodleType.Bored;
            }
            case 6: {
                return MoodleType.Unhappy;
            }
            case 7: {
                return MoodleType.Bleeding;
            }
            case 8: {
                return MoodleType.Wet;
            }
            case 9: {
                return MoodleType.HasACold;
            }
            case 10: {
                return MoodleType.Angry;
            }
            case 11: {
                return MoodleType.Stress;
            }
            case 12: {
                return MoodleType.Thirst;
            }
            case 13: {
                return MoodleType.Injured;
            }
            case 14: {
                return MoodleType.Pain;
            }
            case 15: {
                return MoodleType.HeavyLoad;
            }
            case 16: {
                return MoodleType.Drunk;
            }
            case 17: {
                return MoodleType.Dead;
            }
            case 18: {
                return MoodleType.Zombie;
            }
            case 19: {
                return MoodleType.FoodEaten;
            }
            case 20: {
                return MoodleType.Hyperthermia;
            }
            case 21: {
                return MoodleType.Hypothermia;
            }
            case 22: {
                return MoodleType.Windchill;
            }
            case 23: {
                return MoodleType.CantSprint;
            }
            default: {
                return MoodleType.MAX;
            }
        }
    }
    
    public static MoodleType FromString(final String s) {
        if (s.equals("Endurance")) {
            return MoodleType.Endurance;
        }
        if (s.equals("Tired")) {
            return MoodleType.Tired;
        }
        if (s.equals("Hungry")) {
            return MoodleType.Hungry;
        }
        if (s.equals("Panic")) {
            return MoodleType.Panic;
        }
        if (s.equals("Sick")) {
            return MoodleType.Sick;
        }
        if (s.equals("Bored")) {
            return MoodleType.Bored;
        }
        if (s.equals("Unhappy")) {
            return MoodleType.Unhappy;
        }
        if (s.equals("Bleeding")) {
            return MoodleType.Bleeding;
        }
        if (s.equals("Wet")) {
            return MoodleType.Wet;
        }
        if (s.equals("HasACold")) {
            return MoodleType.HasACold;
        }
        if (s.equals("Angry")) {
            return MoodleType.Angry;
        }
        if (s.equals("Stress")) {
            return MoodleType.Stress;
        }
        if (s.equals("Thirst")) {
            return MoodleType.Thirst;
        }
        if (s.equals("Injured")) {
            return MoodleType.Injured;
        }
        if (s.equals("Pain")) {
            return MoodleType.Pain;
        }
        if (s.equals("HeavyLoad")) {
            return MoodleType.HeavyLoad;
        }
        if (s.equals("Drunk")) {
            return MoodleType.Drunk;
        }
        if (s.equals("Dead")) {
            return MoodleType.Dead;
        }
        if (s.equals("Zombie")) {
            return MoodleType.Zombie;
        }
        if (s.equals("Windchill")) {
            return MoodleType.Windchill;
        }
        if (s.equals("FoodEaten")) {
            return MoodleType.FoodEaten;
        }
        if (s.equals("Hyperthermia")) {
            return MoodleType.Hyperthermia;
        }
        if (s.equals("Hypothermia")) {
            return MoodleType.Hypothermia;
        }
        if (s.equals("CantSprint")) {
            return MoodleType.CantSprint;
        }
        return MoodleType.MAX;
    }
    
    public static String getDisplayName(final MoodleType moodleType, int n) {
        if (n > 4) {
            n = 4;
        }
        if (n == 0) {
            return "Invalid Moodle Level";
        }
        if (moodleType == MoodleType.CantSprint) {
            return Translator.getText("Moodles_CantSprint");
        }
        if (moodleType == MoodleType.Endurance) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_endurance_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_endurance_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_endurance_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_endurance_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Angry) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_angry_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_angry_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_angry_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_angry_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Stress) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_stress_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_stress_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_stress_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_stress_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Thirst) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_thirst_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_thirst_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_thirst_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_thirst_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Tired) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_tired_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_tired_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_tired_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_tired_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Hungry) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hungry_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hungry_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hungry_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hungry_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Panic) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_panic_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_panic_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_panic_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_panic_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Sick) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_sick_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_sick_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_sick_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_sick_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Bored) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_bored_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_bored_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_bored_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_bored_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Unhappy) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_unhappy_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_unhappy_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_unhappy_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_unhappy_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Bleeding) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_bleeding_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_bleeding_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_bleeding_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_bleeding_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Wet) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_wet_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_wet_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_wet_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_wet_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.HasACold) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hascold_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hascold_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hascold_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hascold_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Injured) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_injured_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_injured_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_injured_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_injured_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Pain) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_pain_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_pain_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_pain_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_pain_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.HeavyLoad) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_heavyload_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_heavyload_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_heavyload_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_heavyload_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Drunk) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_drunk_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_drunk_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_drunk_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_drunk_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Dead) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_dead_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_dead_lvl1");
                }
                case 3: {
                    return Translator.getText("Moodles_dead_lvl1");
                }
                case 4: {
                    return Translator.getText("Moodles_dead_lvl1");
                }
            }
        }
        if (moodleType == MoodleType.Zombie) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_zombie_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_zombie_lvl1");
                }
                case 3: {
                    return Translator.getText("Moodles_zombie_lvl1");
                }
                case 4: {
                    return Translator.getText("Moodles_zombie_lvl1");
                }
            }
        }
        if (moodleType == MoodleType.Windchill) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_windchill_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_windchill_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_windchill_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_windchill_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.FoodEaten) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_foodeaten_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_foodeaten_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_foodeaten_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_foodeaten_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Hyperthermia) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hyperthermia_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hyperthermia_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hyperthermia_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hyperthermia_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Hypothermia) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hypothermia_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hypothermia_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hypothermia_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hypothermia_lvl4");
                }
            }
        }
        return "Unkown Moodle Type";
    }
    
    public static String getDescriptionText(final MoodleType moodleType, int n) {
        if (n > 4) {
            n = 4;
        }
        if (n == 0) {
            return "Invalid Moodle Level";
        }
        if (moodleType == MoodleType.CantSprint) {
            return Translator.getText("Moodles_CantSprint_desc");
        }
        if (moodleType == MoodleType.Endurance) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_endurance_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_endurance_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_endurance_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_endurance_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Angry) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_angry_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_angry_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_angry_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_angry_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Stress) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_stress_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_stress_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_stress_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_stress_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Thirst) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_thirst_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_thirst_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_thirst_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_thirst_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Tired) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_tired_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_tired_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_tired_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_tired_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Hungry) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hungry_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hungry_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hungry_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hungry_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Panic) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_panic_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_panic_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_panic_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_panic_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Sick) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_sick_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_sick_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_sick_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_sick_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Bored) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_bored_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_bored_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_bored_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_bored_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Unhappy) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_unhappy_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_unhappy_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_unhappy_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_unhappy_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Bleeding) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_bleed_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_bleed_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_bleed_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_bleed_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Wet) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_wet_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_wet_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_wet_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_wet_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.HasACold) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hasacold_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hasacold_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hasacold_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hasacold_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Injured) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_injured_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_injured_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_injured_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_injured_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Pain) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_pain_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_pain_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_pain_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_pain_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.HeavyLoad) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_heavyload_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_heavyload_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_heavyload_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_heavyload_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Drunk) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_drunk_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_drunk_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_drunk_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_drunk_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Dead) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_dead_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_dead_desc_lvl1");
                }
                case 3: {
                    return Translator.getText("Moodles_dead_desc_lvl1");
                }
                case 4: {
                    return Translator.getText("Moodles_dead_desc_lvl1");
                }
            }
        }
        if (moodleType == MoodleType.Zombie) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_zombified_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_zombified_desc_lvl1");
                }
                case 3: {
                    return Translator.getText("Moodles_zombified_desc_lvl1");
                }
                case 4: {
                    return Translator.getText("Moodles_zombified_desc_lvl1");
                }
            }
        }
        if (moodleType == MoodleType.Windchill) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_windchill_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_windchill_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_windchill_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_windchill_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.FoodEaten) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_foodeaten_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_foodeaten_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_foodeaten_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_foodeaten_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Hyperthermia) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hyperthermia_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hyperthermia_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hyperthermia_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hyperthermia_desc_lvl4");
                }
            }
        }
        if (moodleType == MoodleType.Hypothermia) {
            switch (n) {
                case 1: {
                    return Translator.getText("Moodles_hypothermia_desc_lvl1");
                }
                case 2: {
                    return Translator.getText("Moodles_hypothermia_desc_lvl2");
                }
                case 3: {
                    return Translator.getText("Moodles_hypothermia_desc_lvl3");
                }
                case 4: {
                    return Translator.getText("Moodles_hypothermia_desc_lvl4");
                }
            }
        }
        return "Unkown Moodle Type";
    }
    
    public static int GoodBadNeutral(final MoodleType moodleType) {
        if (moodleType == MoodleType.Endurance) {
            return 2;
        }
        if (moodleType == MoodleType.Tired) {
            return 2;
        }
        if (moodleType == MoodleType.Hungry) {
            return 2;
        }
        if (moodleType == MoodleType.Panic) {
            return 2;
        }
        if (moodleType == MoodleType.Sick) {
            return 2;
        }
        if (moodleType == MoodleType.Bored) {
            return 2;
        }
        if (moodleType == MoodleType.Unhappy) {
            return 2;
        }
        if (moodleType == MoodleType.Bleeding) {
            return 2;
        }
        if (moodleType == MoodleType.Wet) {
            return 2;
        }
        if (moodleType == MoodleType.HasACold) {
            return 2;
        }
        if (moodleType == MoodleType.Angry) {
            return 2;
        }
        if (moodleType == MoodleType.Stress) {
            return 2;
        }
        if (moodleType == MoodleType.Thirst) {
            return 2;
        }
        if (moodleType == MoodleType.CantSprint) {
            return 2;
        }
        if (moodleType == MoodleType.Injured) {
            return 2;
        }
        if (moodleType == MoodleType.Pain) {
            return 2;
        }
        if (moodleType == MoodleType.HeavyLoad) {
            return 2;
        }
        if (moodleType == MoodleType.Drunk) {
            return 2;
        }
        if (moodleType == MoodleType.Dead) {
            return 2;
        }
        if (moodleType == MoodleType.Zombie) {
            return 2;
        }
        if (moodleType == MoodleType.Windchill) {
            return 2;
        }
        if (moodleType == MoodleType.FoodEaten) {
            return 1;
        }
        if (moodleType == MoodleType.Hyperthermia) {
            return 2;
        }
        if (moodleType == MoodleType.Hypothermia) {
            return 2;
        }
        return 2;
    }
    
    public static int ToIndex(final MoodleType moodleType) {
        if (moodleType == null) {
            return 0;
        }
        switch (moodleType) {
            case Endurance: {
                return 0;
            }
            case Tired: {
                return 1;
            }
            case Hungry: {
                return 2;
            }
            case Panic: {
                return 3;
            }
            case Sick: {
                return 4;
            }
            case Bored: {
                return 5;
            }
            case Unhappy: {
                return 6;
            }
            case Bleeding: {
                return 7;
            }
            case Wet: {
                return 8;
            }
            case HasACold: {
                return 9;
            }
            case Angry: {
                return 10;
            }
            case Stress: {
                return 11;
            }
            case Thirst: {
                return 12;
            }
            case Injured: {
                return 13;
            }
            case Pain: {
                return 14;
            }
            case HeavyLoad: {
                return 15;
            }
            case Drunk: {
                return 16;
            }
            case Dead: {
                return 17;
            }
            case Zombie: {
                return 18;
            }
            case FoodEaten: {
                return 19;
            }
            case Hyperthermia: {
                return 20;
            }
            case Hypothermia: {
                return 21;
            }
            case Windchill: {
                return 22;
            }
            case CantSprint: {
                return 23;
            }
            case MAX: {
                return 24;
            }
            default: {
                return 0;
            }
        }
    }
    
    private static /* synthetic */ MoodleType[] $values() {
        return new MoodleType[] { MoodleType.Endurance, MoodleType.Tired, MoodleType.Hungry, MoodleType.Panic, MoodleType.Sick, MoodleType.Bored, MoodleType.Unhappy, MoodleType.Bleeding, MoodleType.Wet, MoodleType.HasACold, MoodleType.Angry, MoodleType.Stress, MoodleType.Thirst, MoodleType.Injured, MoodleType.Pain, MoodleType.HeavyLoad, MoodleType.Drunk, MoodleType.Dead, MoodleType.Zombie, MoodleType.Hyperthermia, MoodleType.Hypothermia, MoodleType.Windchill, MoodleType.CantSprint, MoodleType.FoodEaten, MoodleType.MAX };
    }
    
    static {
        $VALUES = $values();
    }
}
