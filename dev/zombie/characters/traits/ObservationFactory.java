// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.traits;

import java.util.ArrayList;
import zombie.interfaces.IListBoxItem;
import java.util.HashMap;

public final class ObservationFactory
{
    public static HashMap<String, Observation> ObservationMap;
    
    public static void init() {
    }
    
    public static void setMutualExclusive(final String s, final String s2) {
        ObservationFactory.ObservationMap.get(s).MutuallyExclusive.add(s2);
        ObservationFactory.ObservationMap.get(s2).MutuallyExclusive.add(s);
    }
    
    public static void addObservation(final String key, final String s, final String s2) {
        ObservationFactory.ObservationMap.put(key, new Observation(key, s, s2));
    }
    
    public static Observation getObservation(final String s) {
        if (ObservationFactory.ObservationMap.containsKey(s)) {
            return ObservationFactory.ObservationMap.get(s);
        }
        return null;
    }
    
    static {
        ObservationFactory.ObservationMap = new HashMap<String, Observation>();
    }
    
    public static class Observation implements IListBoxItem
    {
        private String traitID;
        private String name;
        private String description;
        public ArrayList<String> MutuallyExclusive;
        
        public Observation(final String traitID, final String name, final String description) {
            this.MutuallyExclusive = new ArrayList<String>(0);
            this.setTraitID(traitID);
            this.setName(name);
            this.setDescription(description);
        }
        
        @Override
        public String getLabel() {
            return this.getName();
        }
        
        @Override
        public String getLeftLabel() {
            return this.getName();
        }
        
        @Override
        public String getRightLabel() {
            return null;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public void setDescription(final String description) {
            this.description = description;
        }
        
        public String getTraitID() {
            return this.traitID;
        }
        
        public void setTraitID(final String traitID) {
            this.traitID = traitID;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
    }
}
