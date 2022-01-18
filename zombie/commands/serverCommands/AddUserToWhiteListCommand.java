// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.core.logger.LoggerManager;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "addusertowhitelist")
@CommandArgs(required = { "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_AddWhitelist")
@RequiredRight(requiredRights = 36)
public class AddUserToWhiteListCommand extends CommandBase
{
    public AddUserToWhiteListCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() throws SQLException {
        final String commandArg = this.getCommandArg(0);
        if (!ServerWorldDatabase.isValidUserName(commandArg)) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        int i = 0;
        while (i < GameServer.udpEngine.connections.size()) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.username.equals(commandArg)) {
                if (udpConnection.password != null && !udpConnection.password.equals("")) {
                    LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), udpConnection.username, udpConnection.password));
                    return ServerWorldDatabase.instance.addUser(udpConnection.username, udpConnection.password);
                }
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
            }
            else {
                ++i;
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
    }
}
