// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.core.logger.LoggerManager;
import zombie.core.secure.PZcrypt;
import zombie.network.ServerWorldDatabase;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "adduser")
@CommandArgs(required = { "(.+)", "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_AddUser")
@RequiredRight(requiredRights = 36)
public class AddUserCommand extends CommandBase
{
    public AddUserCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        final String hash = PZcrypt.hash(ServerWorldDatabase.encrypt(this.getCommandArg(1)));
        if (!ServerWorldDatabase.isValidUserName(commandArg)) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg.trim(), hash.trim()));
        try {
            return ServerWorldDatabase.instance.addUser(commandArg.trim(), hash.trim());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return "exception occurs";
        }
    }
}
