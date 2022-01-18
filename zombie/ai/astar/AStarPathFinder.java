// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.astar;

public class AStarPathFinder
{
    public enum PathFindProgress
    {
        notrunning, 
        failed, 
        found, 
        notyetfound;
        
        private static /* synthetic */ PathFindProgress[] $values() {
            return new PathFindProgress[] { PathFindProgress.notrunning, PathFindProgress.failed, PathFindProgress.found, PathFindProgress.notyetfound };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
