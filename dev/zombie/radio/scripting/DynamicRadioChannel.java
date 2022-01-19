// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.scripting;

import zombie.radio.ChannelCategory;

public final class DynamicRadioChannel extends RadioChannel
{
    public DynamicRadioChannel(final String s, final int n, final ChannelCategory channelCategory) {
        super(s, n, channelCategory);
    }
    
    public DynamicRadioChannel(final String s, final int n, final ChannelCategory channelCategory, final String s2) {
        super(s, n, channelCategory, s2);
    }
    
    @Override
    public void LoadAiringBroadcast(final String s, final int n) {
    }
}
