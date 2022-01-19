// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import java.util.Collection;
import java.util.Arrays;
import zombie.core.Rand;
import java.util.Map;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import java.util.Iterator;
import zombie.GameTime;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.characters.IsoGameCharacter;

public final class Fitness
{
    private IsoGameCharacter parent;
    private HashMap<String, Float> regularityMap;
    private int fitnessLvl;
    private int strLvl;
    private final HashMap<String, Integer> stiffnessTimerMap;
    private final HashMap<String, Float> stiffnessIncMap;
    private final ArrayList<String> bodypartToIncStiffness;
    private final HashMap<String, FitnessExercise> exercises;
    private final HashMap<String, Long> exeTimer;
    private int lastUpdate;
    private FitnessExercise currentExe;
    private static final int HOURS_FOR_STIFFNESS = 12;
    private static final float BASE_STIFFNESS_INC = 0.5f;
    private static final float BASE_ENDURANCE_RED = 0.015f;
    private static final float BASE_REGULARITY_INC = 0.08f;
    private static final float BASE_REGULARITY_DEC = 0.002f;
    private static final float BASE_PAIN_INC = 2.5f;
    
    public Fitness(final IsoGameCharacter parent) {
        this.parent = null;
        this.regularityMap = new HashMap<String, Float>();
        this.fitnessLvl = 0;
        this.strLvl = 0;
        this.stiffnessTimerMap = new HashMap<String, Integer>();
        this.stiffnessIncMap = new HashMap<String, Float>();
        this.bodypartToIncStiffness = new ArrayList<String>();
        this.exercises = new HashMap<String, FitnessExercise>();
        this.exeTimer = new HashMap<String, Long>();
        this.lastUpdate = -1;
        this.setParent(parent);
    }
    
    public void update() {
        final int n = GameTime.getInstance().getMinutes() / 10;
        if (this.lastUpdate == -1) {
            this.lastUpdate = n;
        }
        if (n != this.lastUpdate) {
            this.lastUpdate = n;
            final ArrayList<String> list = new ArrayList<String>();
            this.decreaseRegularity();
            for (final String s : this.stiffnessTimerMap.keySet()) {
                final Integer value = this.stiffnessTimerMap.get(s) - 1;
                if (value <= 0) {
                    list.add(s);
                    this.bodypartToIncStiffness.add(s);
                }
                else {
                    this.stiffnessTimerMap.put(s, value);
                }
            }
            for (int i = 0; i < list.size(); ++i) {
                this.stiffnessTimerMap.remove(list.get(i));
            }
            for (int j = 0; j < this.bodypartToIncStiffness.size(); ++j) {
                final String key = this.bodypartToIncStiffness.get(j);
                final Float n2 = this.stiffnessIncMap.get(key);
                if (n2 == null) {
                    return;
                }
                final Float value2 = n2 - 1.0f;
                this.increasePain(key);
                if (value2 <= 0.0f) {
                    this.bodypartToIncStiffness.remove(j);
                    this.stiffnessIncMap.remove(key);
                    --j;
                }
                else {
                    this.stiffnessIncMap.put(key, value2);
                }
            }
        }
    }
    
    private void decreaseRegularity() {
        for (final String s : this.regularityMap.keySet()) {
            if (!this.exeTimer.containsKey(s)) {
                continue;
            }
            if (GameTime.getInstance().getCalender().getTimeInMillis() - this.exeTimer.get(s) <= 86400000L) {
                continue;
            }
            this.regularityMap.put(s, this.regularityMap.get(s) - 0.002f);
        }
    }
    
    private void increasePain(final String s) {
        if ("arms".equals(s)) {
            for (int i = BodyPartType.ForeArm_L.index(); i < BodyPartType.UpperArm_R.index() + 1; ++i) {
                final BodyPart bodyPart = this.parent.getBodyDamage().getBodyPart(BodyPartType.FromIndex(i));
                bodyPart.setStiffness(bodyPart.getStiffness() + 2.5f);
            }
        }
        if ("legs".equals(s)) {
            for (int j = BodyPartType.UpperLeg_L.index(); j < BodyPartType.LowerLeg_R.index() + 1; ++j) {
                final BodyPart bodyPart2 = this.parent.getBodyDamage().getBodyPart(BodyPartType.FromIndex(j));
                bodyPart2.setStiffness(bodyPart2.getStiffness() + 2.5f);
            }
        }
        if ("chest".equals(s)) {
            final BodyPart bodyPart3 = this.parent.getBodyDamage().getBodyPart(BodyPartType.Torso_Upper);
            bodyPart3.setStiffness(bodyPart3.getStiffness() + 2.5f);
        }
        if ("abs".equals(s)) {
            final BodyPart bodyPart4 = this.parent.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
            bodyPart4.setStiffness(bodyPart4.getStiffness() + 2.5f);
        }
    }
    
    public void setCurrentExercise(final String key) {
        this.currentExe = this.exercises.get(key);
    }
    
    public void exerciseRepeat() {
        this.fitnessLvl = this.parent.getPerkLevel(PerkFactory.Perks.Fitness);
        this.strLvl = this.parent.getPerkLevel(PerkFactory.Perks.Strength);
        this.incRegularity();
        this.reduceEndurance();
        this.incFutureStiffness();
        this.incStats();
        this.updateExeTimer();
    }
    
    private void updateExeTimer() {
        this.exeTimer.put(this.currentExe.type, GameTime.getInstance().getCalender().getTimeInMillis());
    }
    
    public void incRegularity() {
        final float n = 0.08f;
        final int n2 = 4;
        final float n3 = (float)(n * (Math.log(n2 + 1) / Math.log(this.fitnessLvl / 5.0f + n2)));
        Float value = this.regularityMap.get(this.currentExe.type);
        if (value == null) {
            value = 0.0f;
        }
        this.regularityMap.put(this.currentExe.type, Math.min(Math.max(value + n3, 0.0f), 100.0f));
    }
    
    public void reduceEndurance() {
        final float n = 0.015f;
        Float value = this.regularityMap.get(this.currentExe.type);
        if (value == null) {
            value = 0.0f;
        }
        final int n2 = 50;
        float n3 = (float)(n * (Math.log(value / 50.0f + n2) / Math.log(n2 + 1)));
        if (this.currentExe.metabolics == Metabolics.FitnessHeavy) {
            n3 *= 1.3f;
        }
        this.parent.getStats().setEndurance(this.parent.getStats().getEndurance() - n3 * (1 + this.parent.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) / 3));
    }
    
    public void incFutureStiffness() {
        Float value = this.regularityMap.get(this.currentExe.type);
        if (value == null) {
            value = 0.0f;
        }
        for (int i = 0; i < this.currentExe.stiffnessInc.size(); ++i) {
            final float n = 0.5f;
            final String key = this.currentExe.stiffnessInc.get(i);
            if (!this.stiffnessTimerMap.containsKey(key) && !this.bodypartToIncStiffness.contains(key)) {
                this.stiffnessTimerMap.put(key, 72);
            }
            Float value2 = this.stiffnessIncMap.get(key);
            if (value2 == null) {
                value2 = 0.0f;
            }
            float n2 = n * ((120.0f - value) / 170.0f);
            if (this.currentExe.metabolics == Metabolics.FitnessHeavy) {
                n2 *= 1.3f;
            }
            this.stiffnessIncMap.put(key, Math.min(value2 + n2 * (1 + this.parent.getMoodles().getMoodleLevel(MoodleType.Tired) / 3), 150.0f));
        }
    }
    
    public void incStats() {
        float n = 0.0f;
        float n2 = 0.0f;
        for (int i = 0; i < this.currentExe.stiffnessInc.size(); ++i) {
            final String s = this.currentExe.stiffnessInc.get(i);
            if ("arms".equals(s)) {
                n += 4.0f;
            }
            if ("chest".equals(s)) {
                n += 2.0f;
            }
            if ("legs".equals(s)) {
                n2 += 4.0f;
            }
            if ("abs".equals(s)) {
                n2 += 2.0f;
            }
        }
        if (this.strLvl > 5) {
            n *= 1 + (this.strLvl - 5) / 10;
        }
        if (this.fitnessLvl > 5) {
            n2 *= 1 + (this.fitnessLvl - 5) / 10;
        }
        final float n3 = n * this.currentExe.xpModifier;
        final float n4 = n2 * this.currentExe.xpModifier;
        this.parent.getXp().AddXP(PerkFactory.Perks.Strength, n3);
        this.parent.getXp().AddXP(PerkFactory.Perks.Fitness, n4);
    }
    
    public void resetValues() {
        this.stiffnessIncMap.clear();
        this.stiffnessTimerMap.clear();
        this.regularityMap.clear();
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putInt(this.stiffnessIncMap.size());
        for (final String key : this.stiffnessIncMap.keySet()) {
            GameWindow.WriteString(byteBuffer, key);
            byteBuffer.putFloat(this.stiffnessIncMap.get(key));
        }
        byteBuffer.putInt(this.stiffnessTimerMap.size());
        for (final String key2 : this.stiffnessTimerMap.keySet()) {
            GameWindow.WriteString(byteBuffer, key2);
            byteBuffer.putInt(this.stiffnessTimerMap.get(key2));
        }
        byteBuffer.putInt(this.regularityMap.size());
        for (final String key3 : this.regularityMap.keySet()) {
            GameWindow.WriteString(byteBuffer, key3);
            byteBuffer.putFloat(this.regularityMap.get(key3));
        }
        byteBuffer.putInt(this.bodypartToIncStiffness.size());
        for (int i = 0; i < this.bodypartToIncStiffness.size(); ++i) {
            GameWindow.WriteString(byteBuffer, this.bodypartToIncStiffness.get(i));
        }
        byteBuffer.putInt(this.exeTimer.size());
        for (final String key4 : this.exeTimer.keySet()) {
            GameWindow.WriteString(byteBuffer, key4);
            byteBuffer.putLong(this.exeTimer.get(key4));
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) {
        if (n < 167) {
            return;
        }
        final int int1 = byteBuffer.getInt();
        if (int1 > 0) {
            for (int i = 0; i < int1; ++i) {
                this.stiffnessIncMap.put(GameWindow.ReadString(byteBuffer), byteBuffer.getFloat());
            }
        }
        final int int2 = byteBuffer.getInt();
        if (int2 > 0) {
            for (int j = 0; j < int2; ++j) {
                this.stiffnessTimerMap.put(GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
            }
        }
        final int int3 = byteBuffer.getInt();
        if (int3 > 0) {
            for (int k = 0; k < int3; ++k) {
                this.regularityMap.put(GameWindow.ReadString(byteBuffer), byteBuffer.getFloat());
            }
        }
        final int int4 = byteBuffer.getInt();
        if (int4 > 0) {
            for (int l = 0; l < int4; ++l) {
                this.bodypartToIncStiffness.add(GameWindow.ReadString(byteBuffer));
            }
        }
        if (n < 169) {
            return;
        }
        final int int5 = byteBuffer.getInt();
        if (int5 > 0) {
            for (int n2 = 0; n2 < int5; ++n2) {
                this.exeTimer.put(GameWindow.ReadString(byteBuffer), byteBuffer.getLong());
            }
        }
    }
    
    public boolean onGoingStiffness() {
        return !this.bodypartToIncStiffness.isEmpty();
    }
    
    public int getCurrentExeStiffnessTimer(String s) {
        s = s.split(",")[0];
        return (this.stiffnessTimerMap.get(s) != null) ? this.stiffnessTimerMap.get(s) : 0;
    }
    
    public float getCurrentExeStiffnessInc(String s) {
        s = s.split(",")[0];
        return (this.stiffnessIncMap.get(s) != null) ? this.stiffnessIncMap.get(s) : 0.0f;
    }
    
    public IsoGameCharacter getParent() {
        return this.parent;
    }
    
    public void setParent(final IsoGameCharacter parent) {
        this.parent = parent;
    }
    
    public float getRegularity(final String key) {
        Float value = this.regularityMap.get(key);
        if (value == null) {
            value = 0.0f;
        }
        return value;
    }
    
    public HashMap<String, Float> getRegularityMap() {
        return this.regularityMap;
    }
    
    public void setRegularityMap(final HashMap<String, Float> regularityMap) {
        this.regularityMap = regularityMap;
    }
    
    public void init() {
        if (!this.exercises.isEmpty()) {
            return;
        }
        for (final Map.Entry<String, V> entry : ((KahluaTableImpl)((KahluaTableImpl)LuaManager.env.rawget((Object)"FitnessExercises")).rawget((Object)"exercisesType")).delegate.entrySet()) {
            this.exercises.put(entry.getKey(), new FitnessExercise((KahluaTableImpl)entry.getValue()));
        }
        this.initRegularityMapProfession();
    }
    
    public void initRegularityMapProfession() {
        if (!this.regularityMap.isEmpty()) {
            return;
        }
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        if (this.parent.getDescriptor().getProfession().equals("fitnessInstructor")) {
            b2 = true;
        }
        if (this.parent.getDescriptor().getProfession().equals("fireofficer")) {
            b = true;
        }
        if (this.parent.getDescriptor().getProfession().equals("securityguard")) {
            b3 = true;
        }
        if (!b && !b2 && !b3) {
            return;
        }
        final Iterator<String> iterator = this.exercises.keySet().iterator();
        while (iterator.hasNext()) {
            float f = (float)Rand.Next(7, 12);
            if (b) {
                f = (float)Rand.Next(10, 20);
            }
            if (b2) {
                f = (float)Rand.Next(40, 60);
            }
            this.regularityMap.put(iterator.next(), f);
        }
    }
    
    public static final class FitnessExercise
    {
        String type;
        Metabolics metabolics;
        ArrayList<String> stiffnessInc;
        float xpModifier;
        
        public FitnessExercise(final KahluaTableImpl kahluaTableImpl) {
            this.type = null;
            this.metabolics = null;
            this.stiffnessInc = null;
            this.xpModifier = 1.0f;
            this.type = kahluaTableImpl.rawgetStr((Object)"type");
            this.metabolics = (Metabolics)kahluaTableImpl.rawget((Object)"metabolics");
            this.stiffnessInc = new ArrayList<String>(Arrays.asList(kahluaTableImpl.rawgetStr((Object)"stiffness").split(",")));
            if (kahluaTableImpl.rawgetFloat((Object)"xpMod") > 0.0f) {
                this.xpModifier = kahluaTableImpl.rawgetFloat((Object)"xpMod");
            }
        }
    }
}
