// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.vehicles.PathFindState2;
import zombie.ai.State;

public final class PathFindState extends State
{
    private static final PathFindState2 _instance;
    
    public static PathFindState2 instance() {
        return PathFindState._instance;
    }
    
    static {
        _instance = new PathFindState2();
    }
}
