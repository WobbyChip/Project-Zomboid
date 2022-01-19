// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.CommandBase;

@CommandNames({ @CommandName(name = "godmod"), @CommandName(name = "godmode") })
@AltCommandArgs({ @CommandArgs(required = { "(.+)" }, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)") })
@CommandHelp(helpText = "UI_ServerOptionDesc_GodMod")
@RequiredRight(requiredRights = 61)
public class GodModeCommand extends CommandBase
{
    public GodModeCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        String executorUsername = this.getExecutorUsername();
        final String commandArg = this.getCommandArg(0);
        final String commandArg2 = this.getCommandArg(1);
        if (this.getCommandArgsCount() == 2 || (this.getCommandArgsCount() == 1 && !commandArg.equals("-true") && !commandArg.equals("-false"))) {
            executorUsername = commandArg;
            if (this.connection != null && this.connection.accessLevel.equals("observer") && !executorUsername.equals(commandArg)) {
                return "An Observer can only toggle god mode on himself";
            }
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(executorUsername);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, executorUsername);
        }
        final String displayName = playerByUserNameForCommand.getDisplayName();
        if (commandArg2 != null) {
            playerByUserNameForCommand.setGodMod("-true".equals(commandArg2));
        }
        else {
            playerByUserNameForCommand.setGodMod(!playerByUserNameForCommand.isGodMod());
        }
        GameServer.sendPlayerExtraInfo(playerByUserNameForCommand, this.connection);
        if (playerByUserNameForCommand.isGodMod()) {
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), displayName));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, displayName);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), displayName));
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, displayName);
    }
}
