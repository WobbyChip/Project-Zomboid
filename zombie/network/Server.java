// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

public class Server
{
    private String name;
    private String ip;
    private String localIP;
    private String port;
    private String serverpwd;
    private String description;
    private String userName;
    private String pwd;
    private int lastUpdate;
    private String players;
    private String maxPlayers;
    private boolean open;
    private boolean bPublic;
    private String version;
    private String mods;
    private boolean passwordProtected;
    private String steamId;
    private String ping;
    private boolean hosted;
    
    public Server() {
        this.name = "My Server";
        this.ip = "127.0.0.1";
        this.localIP = "";
        this.port = "16262";
        this.serverpwd = "";
        this.description = "";
        this.userName = "";
        this.pwd = "";
        this.lastUpdate = 0;
        this.players = null;
        this.maxPlayers = null;
        this.open = false;
        this.bPublic = true;
        this.version = null;
        this.mods = null;
        this.steamId = null;
        this.ping = null;
        this.hosted = false;
    }
    
    public String getPort() {
        return this.port;
    }
    
    public void setPort(final String port) {
        this.port = port;
    }
    
    public String getIp() {
        return this.ip;
    }
    
    public void setIp(final String ip) {
        this.ip = ip;
    }
    
    public String getLocalIP() {
        return this.localIP;
    }
    
    public void setLocalIP(final String localIP) {
        this.localIP = localIP;
    }
    
    public String getServerPassword() {
        return this.serverpwd;
    }
    
    public void setServerPassword(final String s) {
        this.serverpwd = ((s == null) ? "" : s);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getPwd() {
        return this.pwd;
    }
    
    public void setPwd(final String pwd) {
        this.pwd = pwd;
    }
    
    public int getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(final int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public String getPlayers() {
        return this.players;
    }
    
    public void setPlayers(final String players) {
        this.players = players;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public boolean isPublic() {
        return this.bPublic;
    }
    
    public void setPublic(final boolean bPublic) {
        this.bPublic = bPublic;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public void setMaxPlayers(final String maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public String getMods() {
        return this.mods;
    }
    
    public void setMods(final String mods) {
        this.mods = mods;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getPing() {
        return this.ping;
    }
    
    public void setPing(final String ping) {
        this.ping = ping;
    }
    
    public boolean isPasswordProtected() {
        return this.passwordProtected;
    }
    
    public void setPasswordProtected(final boolean passwordProtected) {
        this.passwordProtected = passwordProtected;
    }
    
    public String getSteamId() {
        return this.steamId;
    }
    
    public void setSteamId(final String steamId) {
        this.steamId = steamId;
    }
    
    public boolean isHosted() {
        return this.hosted;
    }
    
    public void setHosted(final boolean hosted) {
        this.hosted = hosted;
    }
}
