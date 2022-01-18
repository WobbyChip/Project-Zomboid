// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import fmod.fmod.FMOD_STUDIO_PARAMETER_ID;
import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;

public abstract class FMODParameter
{
    private final String m_name;
    private final FMOD_STUDIO_PARAMETER_DESCRIPTION m_parameterDescription;
    private float m_currentValue;
    
    public FMODParameter(final String name) {
        this.m_currentValue = Float.NaN;
        this.m_name = name;
        this.m_parameterDescription = FMODManager.instance.getParameterDescription(name);
    }
    
    public String getName() {
        return this.m_name;
    }
    
    public FMOD_STUDIO_PARAMETER_DESCRIPTION getParameterDescription() {
        return this.m_parameterDescription;
    }
    
    public FMOD_STUDIO_PARAMETER_ID getParameterID() {
        return (this.m_parameterDescription == null) ? null : this.m_parameterDescription.id;
    }
    
    public float getCurrentValue() {
        return this.m_currentValue;
    }
    
    public void update() {
        final float calculateCurrentValue = this.calculateCurrentValue();
        if (calculateCurrentValue == this.m_currentValue) {
            return;
        }
        this.setCurrentValue(this.m_currentValue = calculateCurrentValue);
    }
    
    public void resetToDefault() {
    }
    
    public abstract float calculateCurrentValue();
    
    public abstract void setCurrentValue(final float p0);
    
    public abstract void startEventInstance(final long p0);
    
    public abstract void stopEventInstance(final long p0);
}
