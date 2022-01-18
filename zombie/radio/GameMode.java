// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

public enum GameMode
{
    SinglePlayer, 
    Server, 
    Client;
    
    private static /* synthetic */ GameMode[] $values() {
        return new GameMode[] { GameMode.SinglePlayer, GameMode.Server, GameMode.Client };
    }
    
    static {
        $VALUES = $values();
    }
}
