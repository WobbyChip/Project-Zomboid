// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

public enum ChatMode
{
    ServerMultiPlayer, 
    ClientMultiPlayer, 
    SinglePlayer;
    
    private static /* synthetic */ ChatMode[] $values() {
        return new ChatMode[] { ChatMode.ServerMultiPlayer, ChatMode.ClientMultiPlayer, ChatMode.SinglePlayer };
    }
    
    static {
        $VALUES = $values();
    }
}
