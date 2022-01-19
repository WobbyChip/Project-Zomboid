// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import fmod.javafmod;

public abstract class FMODGlobalParameter extends FMODParameter
{
    public FMODGlobalParameter(final String s) {
        super(s);
        if (this.getParameterDescription() != null && !this.getParameterDescription().isGlobal()) {}
    }
    
    @Override
    public void setCurrentValue(final float n) {
        javafmod.FMOD_Studio_System_SetParameterByID(this.getParameterID(), n, false);
    }
    
    @Override
    public void startEventInstance(final long n) {
    }
    
    @Override
    public void stopEventInstance(final long n) {
    }
}
