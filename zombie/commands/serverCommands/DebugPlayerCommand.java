// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "debugplayer")
@CommandArgs(required = { "(.+)" })
@RequiredRight(requiredRights = 32)
public class DebugPlayerCommand extends CommandBase
{
    public DebugPlayerCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        if (this.getCommandArgsCount() != 1) {
            return "/debugplayer \"username\"";
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(this.getCommandArg(0));
        if (playerByUserNameForCommand == null) {
            return "no such user";
        }
        final UdpConnection connectionByPlayerOnlineID = GameServer.getConnectionByPlayerOnlineID(playerByUserNameForCommand.OnlineID);
        if (connectionByPlayerOnlineID == null) {
            return "no connection for user";
        }
        if (GameServer.DebugPlayer.contains(connectionByPlayerOnlineID)) {
            GameServer.DebugPlayer.remove(connectionByPlayerOnlineID);
            return "debug off";
        }
        GameServer.DebugPlayer.add(connectionByPlayerOnlineID);
        return "debug on";
    }
}
