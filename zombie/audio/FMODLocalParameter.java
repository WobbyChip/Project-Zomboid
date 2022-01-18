// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import fmod.javafmod;
import gnu.trove.list.array.TLongArrayList;

public class FMODLocalParameter extends FMODParameter
{
    private final TLongArrayList m_instances;
    
    public FMODLocalParameter(final String s) {
        super(s);
        this.m_instances = new TLongArrayList();
        if (this.getParameterDescription() != null && this.getParameterDescription().isGlobal()) {}
    }
    
    @Override
    public float calculateCurrentValue() {
        return 0.0f;
    }
    
    @Override
    public void setCurrentValue(final float n) {
        for (int i = 0; i < this.m_instances.size(); ++i) {
            javafmod.FMOD_Studio_EventInstance_SetParameterByID(this.m_instances.get(i), this.getParameterID(), n, false);
        }
    }
    
    @Override
    public void startEventInstance(final long n) {
        this.m_instances.add(n);
        javafmod.FMOD_Studio_EventInstance_SetParameterByID(n, this.getParameterID(), this.getCurrentValue(), false);
    }
    
    @Override
    public void stopEventInstance(final long n) {
        this.m_instances.remove(n);
    }
}
