// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import java.util.ArrayList;

public class VoiceManagerData
{
    public static ArrayList<VoiceManagerData> data;
    public long userplaychannel;
    public long userplaysound;
    public boolean userplaymute;
    public long voicetimeout;
    short index;
    
    public VoiceManagerData(final short index) {
        this.userplaymute = false;
        this.userplaychannel = 0L;
        this.userplaysound = 0L;
        this.voicetimeout = 0L;
        this.index = index;
    }
    
    public static VoiceManagerData get(final short n) {
        if (VoiceManagerData.data.size() <= n) {
            for (short n2 = (short)VoiceManagerData.data.size(); n2 <= n; ++n2) {
                VoiceManagerData.data.add(new VoiceManagerData(n2));
            }
        }
        VoiceManagerData element = VoiceManagerData.data.get(n);
        if (element == null) {
            element = new VoiceManagerData(n);
            VoiceManagerData.data.set(n, element);
        }
        return element;
    }
    
    static {
        VoiceManagerData.data = new ArrayList<VoiceManagerData>();
    }
}
