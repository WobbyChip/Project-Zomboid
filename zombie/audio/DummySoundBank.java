// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODVoice;

public class DummySoundBank extends BaseSoundBank
{
    @Override
    public void addVoice(final String s, final String s2, final float n) {
    }
    
    @Override
    public void addFootstep(final String s, final String s2, final String s3, final String s4, final String s5) {
    }
    
    @Override
    public FMODVoice getVoice(final String s) {
        return null;
    }
    
    @Override
    public FMODFootstep getFootstep(final String s) {
        return null;
    }
}
