// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.DisabledCommand;
import zombie.commands.CommandBase;

@DisabledCommand
@CommandNames({ @CommandName(name = "connections"), @CommandName(name = "list") })
@CommandHelp(helpText = "UI_ServerOptionDesc_Connections")
@RequiredRight(requiredRights = 44)
public class ConnectionsCommand extends CommandBase
{
    public ConnectionsCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        String s = "";
        String s2 = " <LINE> ";
        if (this.connection == null) {
            s2 = "\n";
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                if (udpConnection.usernames[j] != null) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;ISLjava/lang/String;ZLjava/lang/String;)Ljava/lang/String;, s, i + 1, GameServer.udpEngine.connections.size(), udpConnection.idStr, j + 1, udpConnection.playerIDs[j], udpConnection.usernames[j], udpConnection.isFullyConnected(), s2);
                }
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
}
