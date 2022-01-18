// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.network.GameServer;
import zombie.core.znet.SteamUtils;
import zombie.core.logger.LoggerManager;
import zombie.network.ServerWorldDatabase;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "unbanuser")
@CommandArgs(required = { "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_UnBanUser")
@RequiredRight(requiredRights = 36)
public class UnbanUserCommand extends CommandBase
{
    public UnbanUserCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() throws SQLException {
        final String commandArg = this.getCommandArg(0);
        final String banUser = ServerWorldDatabase.instance.banUser(commandArg, false);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg));
        if (!SteamUtils.isSteamModeEnabled()) {
            ServerWorldDatabase.instance.banIp(null, commandArg, null, false);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                if (udpConnection.username.equals(commandArg)) {
                    ServerWorldDatabase.instance.banIp(udpConnection.ip, commandArg, null, false);
                    break;
                }
            }
        }
        return banUser;
    }
}
