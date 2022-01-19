// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.debug.DebugLog;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;

public final class ZomboidGlobals
{
    public static double RunningEnduranceReduce;
    public static double SprintingEnduranceReduce;
    public static double ImobileEnduranceReduce;
    public static double SittingEnduranceMultiplier;
    public static double ThirstIncrease;
    public static double ThirstSleepingIncrease;
    public static double ThirstLevelToAutoDrink;
    public static double ThirstLevelReductionOnAutoDrink;
    public static double HungerIncrease;
    public static double HungerIncreaseWhenWellFed;
    public static double HungerIncreaseWhileAsleep;
    public static double HungerIncreaseWhenExercise;
    public static double FatigueIncrease;
    public static double StressReduction;
    public static double BoredomIncreaseRate;
    public static double BoredomDecreaseRate;
    public static double UnhappinessIncrease;
    public static double StressFromSoundsMultiplier;
    public static double StressFromBiteOrScratch;
    public static double StressFromHemophobic;
    public static double AngerDecrease;
    public static double BroodingAngerDecreaseMultiplier;
    public static double SleepFatigueReduction;
    public static double WetnessIncrease;
    public static double WetnessDecrease;
    public static double CatchAColdIncreaseRate;
    public static double CatchAColdDecreaseRate;
    public static double PoisonLevelDecrease;
    public static double PoisonHealthReduction;
    public static double FoodSicknessDecrease;
    
    public static void Load() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"ZomboidGlobals");
        ZomboidGlobals.SprintingEnduranceReduce = (double)kahluaTable.rawget((Object)"SprintingEnduranceReduce");
        ZomboidGlobals.RunningEnduranceReduce = (double)kahluaTable.rawget((Object)"RunningEnduranceReduce");
        ZomboidGlobals.ImobileEnduranceReduce = (double)kahluaTable.rawget((Object)"ImobileEnduranceIncrease");
        ZomboidGlobals.ThirstIncrease = (double)kahluaTable.rawget((Object)"ThirstIncrease");
        ZomboidGlobals.ThirstSleepingIncrease = (double)kahluaTable.rawget((Object)"ThirstSleepingIncrease");
        ZomboidGlobals.ThirstLevelToAutoDrink = (double)kahluaTable.rawget((Object)"ThirstLevelToAutoDrink");
        ZomboidGlobals.ThirstLevelReductionOnAutoDrink = (double)kahluaTable.rawget((Object)"ThirstLevelReductionOnAutoDrink");
        ZomboidGlobals.HungerIncrease = (double)kahluaTable.rawget((Object)"HungerIncrease");
        ZomboidGlobals.HungerIncreaseWhenWellFed = (double)kahluaTable.rawget((Object)"HungerIncreaseWhenWellFed");
        ZomboidGlobals.HungerIncreaseWhileAsleep = (double)kahluaTable.rawget((Object)"HungerIncreaseWhileAsleep");
        ZomboidGlobals.HungerIncreaseWhenExercise = (double)kahluaTable.rawget((Object)"HungerIncreaseWhenExercise");
        ZomboidGlobals.FatigueIncrease = (double)kahluaTable.rawget((Object)"FatigueIncrease");
        ZomboidGlobals.StressReduction = (double)kahluaTable.rawget((Object)"StressDecrease");
        ZomboidGlobals.BoredomIncreaseRate = (double)kahluaTable.rawget((Object)"BoredomIncrease");
        ZomboidGlobals.BoredomDecreaseRate = (double)kahluaTable.rawget((Object)"BoredomDecrease");
        ZomboidGlobals.UnhappinessIncrease = (double)kahluaTable.rawget((Object)"UnhappinessIncrease");
        ZomboidGlobals.StressFromSoundsMultiplier = (double)kahluaTable.rawget((Object)"StressFromSoundsMultiplier");
        ZomboidGlobals.StressFromBiteOrScratch = (double)kahluaTable.rawget((Object)"StressFromBiteOrScratch");
        ZomboidGlobals.StressFromHemophobic = (double)kahluaTable.rawget((Object)"StressFromHemophobic");
        ZomboidGlobals.AngerDecrease = (double)kahluaTable.rawget((Object)"AngerDecrease");
        ZomboidGlobals.BroodingAngerDecreaseMultiplier = (double)kahluaTable.rawget((Object)"BroodingAngerDecreaseMultiplier");
        ZomboidGlobals.SleepFatigueReduction = (double)kahluaTable.rawget((Object)"SleepFatigueReduction");
        ZomboidGlobals.WetnessIncrease = (double)kahluaTable.rawget((Object)"WetnessIncrease");
        ZomboidGlobals.WetnessDecrease = (double)kahluaTable.rawget((Object)"WetnessDecrease");
        ZomboidGlobals.CatchAColdIncreaseRate = (double)kahluaTable.rawget((Object)"CatchAColdIncreaseRate");
        ZomboidGlobals.CatchAColdDecreaseRate = (double)kahluaTable.rawget((Object)"CatchAColdDecreaseRate");
        ZomboidGlobals.PoisonLevelDecrease = (double)kahluaTable.rawget((Object)"PoisonLevelDecrease");
        ZomboidGlobals.PoisonHealthReduction = (double)kahluaTable.rawget((Object)"PoisonHealthReduction");
        ZomboidGlobals.FoodSicknessDecrease = (double)kahluaTable.rawget((Object)"FoodSicknessDecrease");
    }
    
    public static void toLua() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"ZomboidGlobals");
        if (kahluaTable == null) {
            DebugLog.log("ERROR: ZomboidGlobals table undefined in Lua");
            return;
        }
        double d = 1.0;
        if (SandboxOptions.instance.getFoodLootModifier() == 1) {
            d = 0.2;
        }
        else if (SandboxOptions.instance.getFoodLootModifier() == 2) {
            d = 0.6;
        }
        else if (SandboxOptions.instance.getFoodLootModifier() == 3) {
            d = 1.0;
        }
        else if (SandboxOptions.instance.getFoodLootModifier() == 4) {
            d = 2.0;
        }
        else if (SandboxOptions.instance.getFoodLootModifier() == 5) {
            d = 4.0;
        }
        kahluaTable.rawset((Object)"FoodLootModifier", (Object)d);
        double d2 = 1.0;
        if (SandboxOptions.instance.getWeaponLootModifier() == 1) {
            d2 = 0.2;
        }
        else if (SandboxOptions.instance.getWeaponLootModifier() == 2) {
            d2 = 0.6;
        }
        else if (SandboxOptions.instance.getWeaponLootModifier() == 3) {
            d2 = 1.0;
        }
        else if (SandboxOptions.instance.getWeaponLootModifier() == 4) {
            d2 = 2.0;
        }
        else if (SandboxOptions.instance.getWeaponLootModifier() == 5) {
            d2 = 4.0;
        }
        kahluaTable.rawset((Object)"WeaponLootModifier", (Object)d2);
        double d3 = 1.0;
        if (SandboxOptions.instance.getOtherLootModifier() == 1) {
            d3 = 0.2;
        }
        else if (SandboxOptions.instance.getOtherLootModifier() == 2) {
            d3 = 0.6;
        }
        else if (SandboxOptions.instance.getOtherLootModifier() == 3) {
            d3 = 1.0;
        }
        else if (SandboxOptions.instance.getOtherLootModifier() == 4) {
            d3 = 2.0;
        }
        else if (SandboxOptions.instance.getOtherLootModifier() == 5) {
            d3 = 4.0;
        }
        kahluaTable.rawset((Object)"OtherLootModifier", (Object)d3);
    }
    
    static {
        ZomboidGlobals.RunningEnduranceReduce = 0.0;
        ZomboidGlobals.SprintingEnduranceReduce = 0.0;
        ZomboidGlobals.ImobileEnduranceReduce = 0.0;
        ZomboidGlobals.SittingEnduranceMultiplier = 5.0;
        ZomboidGlobals.ThirstIncrease = 0.0;
        ZomboidGlobals.ThirstSleepingIncrease = 0.0;
        ZomboidGlobals.ThirstLevelToAutoDrink = 0.0;
        ZomboidGlobals.ThirstLevelReductionOnAutoDrink = 0.0;
        ZomboidGlobals.HungerIncrease = 0.0;
        ZomboidGlobals.HungerIncreaseWhenWellFed = 0.0;
        ZomboidGlobals.HungerIncreaseWhileAsleep = 0.0;
        ZomboidGlobals.HungerIncreaseWhenExercise = 0.0;
        ZomboidGlobals.FatigueIncrease = 0.0;
        ZomboidGlobals.StressReduction = 0.0;
        ZomboidGlobals.BoredomIncreaseRate = 0.0;
        ZomboidGlobals.BoredomDecreaseRate = 0.0;
        ZomboidGlobals.UnhappinessIncrease = 0.0;
        ZomboidGlobals.StressFromSoundsMultiplier = 0.0;
        ZomboidGlobals.StressFromBiteOrScratch = 0.0;
        ZomboidGlobals.StressFromHemophobic = 0.0;
        ZomboidGlobals.AngerDecrease = 0.0;
        ZomboidGlobals.BroodingAngerDecreaseMultiplier = 0.0;
        ZomboidGlobals.SleepFatigueReduction = 0.0;
        ZomboidGlobals.WetnessIncrease = 0.0;
        ZomboidGlobals.WetnessDecrease = 0.0;
        ZomboidGlobals.CatchAColdIncreaseRate = 0.0;
        ZomboidGlobals.CatchAColdDecreaseRate = 0.0;
        ZomboidGlobals.PoisonLevelDecrease = 0.0;
        ZomboidGlobals.PoisonHealthReduction = 0.0;
        ZomboidGlobals.FoodSicknessDecrease = 0.0;
    }
}
