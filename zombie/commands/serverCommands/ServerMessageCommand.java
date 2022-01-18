// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.network.chat.ChatServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "servermsg")
@CommandArgs(required = { "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_ServerMsg")
@RequiredRight(requiredRights = 44)
public class ServerMessageCommand extends CommandBase
{
    public ServerMessageCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        if (this.connection == null) {
            ChatServer.getInstance().sendServerAlertMessageToServerChat(commandArg);
        }
        else {
            ChatServer.getInstance().sendServerAlertMessageToServerChat(this.getExecutorUsername(), commandArg);
        }
        return "Message sent.";
    }
}
