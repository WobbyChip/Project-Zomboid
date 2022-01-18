// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.characters.IsoPlayer;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "releasesafehouse")
@CommandHelp(helpText = "UI_ServerOptionDesc_SafeHouse")
@RequiredRight(requiredRights = 63)
public class ReleaseSafehouseCommand extends CommandBase
{
    public ReleaseSafehouseCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() throws SQLException {
        if (this.isCommandComeFromServerConsole()) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CommandBase.getCommandName(this.getClass()));
        }
        final String executorUsername = this.getExecutorUsername();
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(executorUsername);
        final SafeHouse hasSafehouse = SafeHouse.hasSafehouse(executorUsername);
        if (hasSafehouse == null) {
            return "You have no safehouse";
        }
        if (!hasSafehouse.isOwner(playerByUserNameForCommand)) {
            return "Only owner can release safehouse";
        }
        hasSafehouse.removeSafeHouse(playerByUserNameForCommand);
        return "Your safehouse was released";
    }
}
