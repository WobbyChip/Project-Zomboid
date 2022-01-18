// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

public final class ThreadGroups
{
    public static final ThreadGroup Root;
    public static final ThreadGroup Main;
    public static final ThreadGroup Workers;
    public static final ThreadGroup Network;
    
    static {
        Root = new ThreadGroup("PZ");
        Main = new ThreadGroup(ThreadGroups.Root, "Main");
        Workers = new ThreadGroup(ThreadGroups.Root, "Workers");
        Network = new ThreadGroup(ThreadGroups.Root, "Network");
    }
}
