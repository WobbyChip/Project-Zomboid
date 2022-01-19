// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "grantadmin")
@CommandArgs(required = { "(.+)" })
@RequiredRight(requiredRights = 32)
public class GrantAdminCommand extends CommandBase
{
    public GrantAdminCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() throws SQLException {
        return SetAccessLevelCommand.update(this.getExecutorUsername(), this.connection, this.getCommandArg(0), "admin");
    }
}
