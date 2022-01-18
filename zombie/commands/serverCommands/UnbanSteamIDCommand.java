// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.network.ServerWorldDatabase;
import zombie.core.logger.LoggerManager;
import zombie.core.znet.SteamUtils;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "unbanid")
@CommandArgs(required = { "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_UnBanSteamId")
@RequiredRight(requiredRights = 36)
public class UnbanSteamIDCommand extends CommandBase
{
    public UnbanSteamIDCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() throws SQLException {
        final String commandArg = this.getCommandArg(0);
        if (!SteamUtils.isSteamModeEnabled()) {
            return "Server is not in Steam mode";
        }
        if (!SteamUtils.isValidSteamID(commandArg)) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg), "IMPORTANT");
        ServerWorldDatabase.instance.banSteamID(commandArg, "", false);
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
    }
}
