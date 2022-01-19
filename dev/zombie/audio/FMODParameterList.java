// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;

public final class FMODParameterList
{
    public final ArrayList<FMODParameter> parameterList;
    public final FMODParameter[] parameterArray;
    
    public FMODParameterList() {
        this.parameterList = new ArrayList<FMODParameter>();
        this.parameterArray = new FMODParameter[96];
    }
    
    public void add(final FMODParameter e) {
        this.parameterList.add(e);
        if (e.getParameterDescription() != null) {
            this.parameterArray[e.getParameterDescription().globalIndex] = e;
        }
    }
    
    public FMODParameter get(final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION) {
        return (fmod_STUDIO_PARAMETER_DESCRIPTION == null) ? null : this.parameterArray[fmod_STUDIO_PARAMETER_DESCRIPTION.globalIndex];
    }
    
    public void update() {
        for (int i = 0; i < this.parameterList.size(); ++i) {
            this.parameterList.get(i).update();
        }
    }
}
