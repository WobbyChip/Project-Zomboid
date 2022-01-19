// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

public enum Metabolics
{
    Sleeping(0.8f), 
    SeatedResting(1.0f), 
    StandingAtRest(1.2f), 
    SedentaryActivity(1.2f), 
    Default(1.6f), 
    DrivingCar(1.4f), 
    LightDomestic(1.9f), 
    HeavyDomestic(2.9f), 
    DefaultExercise(3.0f), 
    UsingTools(3.4f), 
    LightWork(4.3f), 
    MediumWork(5.4f), 
    DiggingSpade(6.5f), 
    HeavyWork(7.0f), 
    ForestryAxe(8.5f), 
    Walking2kmh(1.9f), 
    Walking5kmh(3.1f), 
    Running10kmh(6.5f), 
    Running15kmh(9.5f), 
    JumpFence(4.0f), 
    ClimbRope(8.0f), 
    Fitness(6.0f), 
    FitnessHeavy(9.0f), 
    MAX(10.3f);
    
    private final float met;
    
    private Metabolics(final float met) {
        this.met = met;
    }
    
    public float getMet() {
        return this.met;
    }
    
    public float getWm2() {
        return MetToWm2(this.met);
    }
    
    public float getW() {
        return MetToW(this.met);
    }
    
    public float getBtuHr() {
        return MetToBtuHr(this.met);
    }
    
    public static float MetToWm2(final float n) {
        return 58.0f * n;
    }
    
    public static float MetToW(final float n) {
        return MetToWm2(n) * 1.8f;
    }
    
    public static float MetToBtuHr(final float n) {
        return 356.0f * n;
    }
    
    private static /* synthetic */ Metabolics[] $values() {
        return new Metabolics[] { Metabolics.Sleeping, Metabolics.SeatedResting, Metabolics.StandingAtRest, Metabolics.SedentaryActivity, Metabolics.Default, Metabolics.DrivingCar, Metabolics.LightDomestic, Metabolics.HeavyDomestic, Metabolics.DefaultExercise, Metabolics.UsingTools, Metabolics.LightWork, Metabolics.MediumWork, Metabolics.DiggingSpade, Metabolics.HeavyWork, Metabolics.ForestryAxe, Metabolics.Walking2kmh, Metabolics.Walking5kmh, Metabolics.Running10kmh, Metabolics.Running15kmh, Metabolics.JumpFence, Metabolics.ClimbRope, Metabolics.Fitness, Metabolics.FitnessHeavy, Metabolics.MAX };
    }
    
    static {
        $VALUES = $values();
    }
}
