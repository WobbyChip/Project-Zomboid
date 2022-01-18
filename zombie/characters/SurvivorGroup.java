// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class SurvivorGroup
{
    public final ArrayList<SurvivorDesc> Members;
    public String Order;
    public BuildingDef Safehouse;
    
    public SurvivorGroup() {
        this.Members = new ArrayList<SurvivorDesc>();
    }
    
    public void addMember(final SurvivorDesc survivorDesc) {
    }
    
    public void removeMember(final SurvivorDesc survivorDesc) {
    }
    
    public SurvivorDesc getLeader() {
        return null;
    }
    
    public boolean isLeader(final SurvivorDesc survivorDesc) {
        return false;
    }
}
