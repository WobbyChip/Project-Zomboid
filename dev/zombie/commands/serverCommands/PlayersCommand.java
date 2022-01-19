// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.util.Iterator;
import zombie.network.GameServer;
import java.util.ArrayList;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "players")
@CommandHelp(helpText = "UI_ServerOptionDesc_Players")
@RequiredRight(requiredRights = 61)
public class PlayersCommand extends CommandBase
{
    public PlayersCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                if (udpConnection.usernames[j] != null) {
                    list.add(udpConnection.usernames[j]);
                }
            }
        }
        final StringBuilder sb = new StringBuilder(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()));
        String s = " <LINE> ";
        if (this.connection == null) {
            s = "\n";
        }
        sb.append(s);
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            sb.append("-").append(iterator.next()).append(s);
        }
        return sb.toString();
    }
}
