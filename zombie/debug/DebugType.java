// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

public enum DebugType
{
    NetworkPacketDebug, 
    NetworkFileDebug, 
    Network, 
    General, 
    Lua, 
    Mod, 
    Sound, 
    Zombie, 
    Combat, 
    Objects, 
    Fireplace, 
    Radio, 
    MapLoading, 
    Clothing, 
    Animation, 
    Asset, 
    Script, 
    Shader, 
    Input, 
    Recipe, 
    ActionSystem, 
    IsoRegion, 
    UnitTests, 
    FileIO, 
    Multiplayer, 
    Ownership, 
    Death, 
    Damage, 
    Statistic, 
    Vehicle;
    
    public static boolean Do(final DebugType debugType) {
        return DebugLog.isEnabled(debugType);
    }
    
    private static /* synthetic */ DebugType[] $values() {
        return new DebugType[] { DebugType.NetworkPacketDebug, DebugType.NetworkFileDebug, DebugType.Network, DebugType.General, DebugType.Lua, DebugType.Mod, DebugType.Sound, DebugType.Zombie, DebugType.Combat, DebugType.Objects, DebugType.Fireplace, DebugType.Radio, DebugType.MapLoading, DebugType.Clothing, DebugType.Animation, DebugType.Asset, DebugType.Script, DebugType.Shader, DebugType.Input, DebugType.Recipe, DebugType.ActionSystem, DebugType.IsoRegion, DebugType.UnitTests, DebugType.FileIO, DebugType.Multiplayer, DebugType.Ownership, DebugType.Death, DebugType.Damage, DebugType.Statistic, DebugType.Vehicle };
    }
    
    static {
        $VALUES = $values();
    }
}
