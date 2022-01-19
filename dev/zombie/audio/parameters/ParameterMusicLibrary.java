// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.core.Core;
import zombie.audio.FMODGlobalParameter;

public final class ParameterMusicLibrary extends FMODGlobalParameter
{
    public ParameterMusicLibrary() {
        super("MusicLibrary");
    }
    
    @Override
    public float calculateCurrentValue() {
        float n = 0.0f;
        switch (Core.getInstance().getOptionMusicLibrary()) {
            case 2: {
                n = (float)Library.EarlyAccess.label;
                break;
            }
            case 3: {
                n = (float)Library.Random.label;
                break;
            }
            default: {
                n = (float)Library.Official.label;
                break;
            }
        }
        return n;
    }
    
    public enum Library
    {
        Official(0), 
        EarlyAccess(1), 
        Random(2);
        
        final int label;
        
        private Library(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ Library[] $values() {
            return new Library[] { Library.Official, Library.EarlyAccess, Library.Random };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
