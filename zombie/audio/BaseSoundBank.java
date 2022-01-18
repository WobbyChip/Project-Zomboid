// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODVoice;

public abstract class BaseSoundBank
{
    public static BaseSoundBank instance;
    
    public abstract void addVoice(final String p0, final String p1, final float p2);
    
    public abstract void addFootstep(final String p0, final String p1, final String p2, final String p3, final String p4);
    
    public abstract FMODVoice getVoice(final String p0);
    
    public abstract FMODFootstep getFootstep(final String p0);
}
