// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.astar;

public interface IPathfinder
{
    void Failed(final Mover p0);
    
    void Succeeded(final Path p0, final Mover p1);
    
    String getName();
}
