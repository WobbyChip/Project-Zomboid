// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.characters.IsoZombie;
import java.util.Iterator;
import zombie.core.raknet.UdpConnection;
import java.util.LinkedList;

public class NetworkZombieList
{
    final LinkedList<NetworkZombie> networkZombies;
    public Object lock;
    
    public NetworkZombieList() {
        this.networkZombies = new LinkedList<NetworkZombie>();
        this.lock = new Object();
    }
    
    public NetworkZombie getNetworkZombie(final UdpConnection udpConnection) {
        if (udpConnection == null) {
            return null;
        }
        for (final NetworkZombie networkZombie : this.networkZombies) {
            if (networkZombie.connection == udpConnection) {
                return networkZombie;
            }
        }
        final NetworkZombie e = new NetworkZombie(udpConnection);
        this.networkZombies.add(e);
        return e;
    }
    
    public static class NetworkZombie
    {
        final LinkedList<IsoZombie> zombies;
        final UdpConnection connection;
        
        public NetworkZombie(final UdpConnection connection) {
            this.zombies = new LinkedList<IsoZombie>();
            this.connection = connection;
        }
    }
}
