// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.astar;

public class AStarPathFinderResult
{
    public AStarPathFinder.PathFindProgress progress;
    public int maxSearchDistance;
    
    public AStarPathFinderResult() {
        this.progress = AStarPathFinder.PathFindProgress.notrunning;
        this.maxSearchDistance = 120;
    }
}
