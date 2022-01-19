// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.network.CoopSlave;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.popman.ZombiePopulationManager;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "reloadoptions")
@CommandHelp(helpText = "UI_ServerOptionDesc_ReloadOptions")
@RequiredRight(requiredRights = 32)
public class ReloadOptionsCommand extends CommandBase
{
    public ReloadOptionsCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        ServerOptions.instance.init();
        GameServer.initClientCommandFilter();
        ZombiePopulationManager.instance.onConfigReloaded();
        GameServer.sendOptionsToClients();
        GameServer.udpEngine.SetServerPassword(GameServer.udpEngine.hashServerPassword(ServerOptions.instance.Password.getValue()));
        if (SteamUtils.isSteamModeEnabled()) {
            SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
            SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
            SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("mods", ServerOptions.instance.Mods.getValue());
            if (ServerOptions.instance.Public.getValue()) {
                SteamGameServer.SetGameTags((CoopSlave.instance != null) ? "hosted" : "");
            }
            else {
                SteamGameServer.SetGameTags(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (CoopSlave.instance != null) ? ";hosted" : ""));
            }
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
        return "Options reloaded";
    }
}
