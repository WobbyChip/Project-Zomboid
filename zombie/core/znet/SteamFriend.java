// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.core.textures.Texture;

public class SteamFriend
{
    private String name;
    private long steamID;
    private String steamIDString;
    
    public SteamFriend() {
        this.name = "";
    }
    
    public SteamFriend(final String name, final long steamID) {
        this.name = "";
        this.steamID = steamID;
        this.steamIDString = SteamUtils.convertSteamIDToString(steamID);
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getSteamID() {
        return this.steamIDString;
    }
    
    public Texture getAvatar() {
        return Texture.getSteamAvatar(this.steamID);
    }
    
    public String getState() {
        switch (SteamFriends.GetFriendPersonaState(this.steamID)) {
            case 0: {
                return "Offline";
            }
            case 1: {
                return "Online";
            }
            case 2: {
                return "Busy";
            }
            case 3: {
                return "Away";
            }
            case 4: {
                return "Snooze";
            }
            case 5: {
                return "LookingToTrade";
            }
            case 6: {
                return "LookingToPlay";
            }
            default: {
                return "Unknown";
            }
        }
    }
}
