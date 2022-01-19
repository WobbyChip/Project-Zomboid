// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.network.ReplayManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "replay")
@AltCommandArgs({ @CommandArgs(required = { "(.+)", "(-record|-play|-stop)", "(.+)" }), @CommandArgs(required = { "(.+)", "(-stop)" }) })
@CommandHelp(helpText = "UI_ServerOptionDesc_Replay")
@RequiredRight(requiredRights = 32)
public class ReplayCommands extends CommandBase
{
    public static final String RecordPlay = "(-record|-play|-stop)";
    public static final String Stop = "(-stop)";
    
    public ReplayCommands(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        final String commandArg2 = this.getCommandArg(1);
        final String commandArg3 = this.getCommandArg(2);
        boolean b = false;
        boolean b2 = false;
        if ("-play".equals(commandArg2)) {
            b2 = true;
        }
        else if ("-stop".equals(commandArg2)) {
            b = true;
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(commandArg);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        if (playerByUserNameForCommand.replay == null) {
            playerByUserNameForCommand.replay = new ReplayManager(playerByUserNameForCommand);
        }
        if (b) {
            final ReplayManager.State state = playerByUserNameForCommand.replay.getState();
            if (state == ReplayManager.State.Stop) {
                return "Nothing to stop.";
            }
            if (state == ReplayManager.State.Recording) {
                playerByUserNameForCommand.replay.stopRecordReplay();
                LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg));
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
            }
            playerByUserNameForCommand.replay.stopPlayReplay();
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        else if (b2) {
            if (!playerByUserNameForCommand.replay.startPlayReplay(playerByUserNameForCommand, commandArg3)) {
                return "Can't play replay";
            }
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, commandArg, commandArg3);
        }
        else {
            if (!playerByUserNameForCommand.replay.startRecordReplay(playerByUserNameForCommand, commandArg3)) {
                return "Can't record replay";
            }
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, commandArg, commandArg3);
        }
    }
}
