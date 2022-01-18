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
import zombie.commands.CommandBase;

@CommandName(name = "invisible")
@AltCommandArgs({ @CommandArgs(required = { "(.+)" }, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)") })
@CommandHelp(helpText = "UI_ServerOptionDesc_Invisible")
@RequiredRight(requiredRights = 61)
public class InvisibleCommand extends CommandBase
{
    public InvisibleCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        String executorUsername = this.getExecutorUsername();
        final String commandArg = this.getCommandArg(0);
        final String commandArg2 = this.getCommandArg(1);
        if (this.getCommandArgsCount() == 2 || (this.getCommandArgsCount() == 1 && !commandArg.equals("-true") && !commandArg.equals("-false"))) {
            executorUsername = commandArg;
            if (this.connection.accessLevel.equals("observer") && !executorUsername.equals(this.getExecutorUsername())) {
                return "An Observer can only toggle invisible on himself";
            }
        }
        boolean b = false;
        boolean invisible = true;
        if ("-false".equals(commandArg2)) {
            invisible = false;
            b = true;
        }
        else if ("-true".equals(commandArg2)) {
            b = true;
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(executorUsername);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, executorUsername);
        }
        if (!b) {
            invisible = !playerByUserNameForCommand.isInvisible();
        }
        final String displayName = playerByUserNameForCommand.getDisplayName();
        if (b) {
            playerByUserNameForCommand.setInvisible(invisible);
        }
        else {
            playerByUserNameForCommand.setInvisible(!playerByUserNameForCommand.isInvisible());
            invisible = playerByUserNameForCommand.isInvisible();
        }
        playerByUserNameForCommand.setGhostMode(invisible);
        GameServer.sendPlayerExtraInfo(playerByUserNameForCommand, this.connection);
        if (invisible) {
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), displayName));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, displayName);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), displayName));
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, displayName);
    }
}
