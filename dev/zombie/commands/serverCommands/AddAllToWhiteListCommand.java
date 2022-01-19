// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.network.ServerWorldDatabase;
import zombie.core.logger.LoggerManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "addalltowhitelist")
@CommandHelp(helpText = "UI_ServerOptionDesc_AddAllWhitelist")
@RequiredRight(requiredRights = 36)
public class AddAllToWhiteListCommand extends CommandBase
{
    public AddAllToWhiteListCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.password != null && !udpConnection.password.equals("")) {
                LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), udpConnection.username, udpConnection.password));
                try {
                    sb.append(ServerWorldDatabase.instance.addUser(udpConnection.username, udpConnection.password)).append(" <LINE> ");
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                sb.append("User ").append(udpConnection.username).append(" doesn't have a password. <LINE> ");
            }
        }
        sb.append("Done.");
        return sb.toString();
    }
}
