// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

public class SystemDisabler
{
    public static boolean doCharacterStats;
    public static boolean doZombieCreation;
    public static boolean doSurvivorCreation;
    public static boolean doPlayerCreation;
    public static boolean doOverridePOVCharacters;
    public static boolean doVehiclesEverywhere;
    public static boolean doWorldSyncEnable;
    public static boolean doObjectStateSyncEnable;
    private static boolean doAllowDebugConnections;
    private static boolean doOverrideServerConnectDebugCheck;
    private static boolean doHighFriction;
    private static boolean doVehicleLowRider;
    public static boolean doEnableDetectOpenGLErrorsInTexture;
    public static boolean doVehiclesWithoutTextures;
    public static boolean zombiesDontAttack;
    public static boolean zombiesSwitchOwnershipEachUpdate;
    private static boolean doMainLoopDealWithNetData;
    public static boolean useNetworkCharacter;
    private static boolean bEnableAdvancedSoundOptions;
    
    public static void setDoCharacterStats(final boolean doCharacterStats) {
        SystemDisabler.doCharacterStats = doCharacterStats;
    }
    
    public static void setDoZombieCreation(final boolean doZombieCreation) {
        SystemDisabler.doZombieCreation = doZombieCreation;
    }
    
    public static void setDoSurvivorCreation(final boolean doSurvivorCreation) {
        SystemDisabler.doSurvivorCreation = doSurvivorCreation;
    }
    
    public static void setDoPlayerCreation(final boolean doPlayerCreation) {
        SystemDisabler.doPlayerCreation = doPlayerCreation;
    }
    
    public static void setOverridePOVCharacters(final boolean doOverridePOVCharacters) {
        SystemDisabler.doOverridePOVCharacters = doOverridePOVCharacters;
    }
    
    public static void setVehiclesEverywhere(final boolean doVehiclesEverywhere) {
        SystemDisabler.doVehiclesEverywhere = doVehiclesEverywhere;
    }
    
    public static void setWorldSyncEnable(final boolean doWorldSyncEnable) {
        SystemDisabler.doWorldSyncEnable = doWorldSyncEnable;
    }
    
    public static void setObjectStateSyncEnable(final boolean doObjectStateSyncEnable) {
        SystemDisabler.doObjectStateSyncEnable = doObjectStateSyncEnable;
    }
    
    public static boolean getAllowDebugConnections() {
        return SystemDisabler.doAllowDebugConnections;
    }
    
    public static boolean getOverrideServerConnectDebugCheck() {
        return SystemDisabler.doOverrideServerConnectDebugCheck;
    }
    
    public static boolean getdoHighFriction() {
        return SystemDisabler.doHighFriction;
    }
    
    public static boolean getdoVehicleLowRider() {
        return SystemDisabler.doVehicleLowRider;
    }
    
    public static boolean getDoMainLoopDealWithNetData() {
        return SystemDisabler.doMainLoopDealWithNetData;
    }
    
    public static void setEnableAdvancedSoundOptions(final boolean bEnableAdvancedSoundOptions) {
        SystemDisabler.bEnableAdvancedSoundOptions = bEnableAdvancedSoundOptions;
    }
    
    public static boolean getEnableAdvancedSoundOptions() {
        return SystemDisabler.bEnableAdvancedSoundOptions;
    }
    
    public static void Reset() {
        SystemDisabler.doCharacterStats = true;
        SystemDisabler.doZombieCreation = true;
        SystemDisabler.doSurvivorCreation = false;
        SystemDisabler.doPlayerCreation = true;
        SystemDisabler.doOverridePOVCharacters = true;
        SystemDisabler.doVehiclesEverywhere = false;
        SystemDisabler.doAllowDebugConnections = false;
        SystemDisabler.doWorldSyncEnable = false;
        SystemDisabler.doObjectStateSyncEnable = false;
        SystemDisabler.doMainLoopDealWithNetData = true;
        SystemDisabler.bEnableAdvancedSoundOptions = false;
    }
    
    static {
        SystemDisabler.doCharacterStats = true;
        SystemDisabler.doZombieCreation = true;
        SystemDisabler.doSurvivorCreation = false;
        SystemDisabler.doPlayerCreation = true;
        SystemDisabler.doOverridePOVCharacters = true;
        SystemDisabler.doVehiclesEverywhere = false;
        SystemDisabler.doWorldSyncEnable = false;
        SystemDisabler.doObjectStateSyncEnable = false;
        SystemDisabler.doAllowDebugConnections = false;
        SystemDisabler.doOverrideServerConnectDebugCheck = false;
        SystemDisabler.doHighFriction = false;
        SystemDisabler.doVehicleLowRider = false;
        SystemDisabler.doEnableDetectOpenGLErrorsInTexture = false;
        SystemDisabler.doVehiclesWithoutTextures = false;
        SystemDisabler.zombiesDontAttack = false;
        SystemDisabler.zombiesSwitchOwnershipEachUpdate = false;
        SystemDisabler.doMainLoopDealWithNetData = true;
        SystemDisabler.useNetworkCharacter = false;
        SystemDisabler.bEnableAdvancedSoundOptions = false;
    }
}
