// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

public class GameServerDetails
{
    public String address;
    public int port;
    public long steamId;
    public String name;
    public String gamedir;
    public String map;
    public String gameDescription;
    public String tags;
    public int ping;
    public int numPlayers;
    public int maxPlayers;
    public boolean passwordProtected;
    public int version;
    
    public GameServerDetails() {
    }
    
    public GameServerDetails(final String address, final int port, final long steamId, final String name, final String gamedir, final String map, final String gameDescription, final String tags, final int ping, final int numPlayers, final int maxPlayers, final boolean passwordProtected, final int version) {
        this.address = address;
        this.port = port;
        this.steamId = steamId;
        this.name = name;
        this.gamedir = gamedir;
        this.map = map;
        this.gameDescription = gameDescription;
        this.tags = tags;
        this.ping = ping;
        this.numPlayers = numPlayers;
        this.maxPlayers = maxPlayers;
        this.passwordProtected = passwordProtected;
        this.version = version;
    }
}
