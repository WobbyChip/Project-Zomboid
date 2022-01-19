// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

public enum ChannelCategory
{
    Undefined, 
    Radio, 
    Television, 
    Military, 
    Amateur, 
    Bandit, 
    Emergency, 
    Other;
    
    private static /* synthetic */ ChannelCategory[] $values() {
        return new ChannelCategory[] { ChannelCategory.Undefined, ChannelCategory.Radio, ChannelCategory.Television, ChannelCategory.Military, ChannelCategory.Amateur, ChannelCategory.Bandit, ChannelCategory.Emergency, ChannelCategory.Other };
    }
    
    static {
        $VALUES = $values();
    }
}
