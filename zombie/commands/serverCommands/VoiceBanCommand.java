// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.VoiceManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "voiceban")
@AltCommandArgs({ @CommandArgs(required = { "(.+)" }, optional = "(-true|-false)"), @CommandArgs(optional = "(-true|-false)") })
@CommandHelp(helpText = "UI_ServerOptionDesc_VoiceBan")
@RequiredRight(requiredRights = 36)
public class VoiceBanCommand extends CommandBase
{
    public VoiceBanCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        String s = this.getExecutorUsername();
        if (this.getCommandArgsCount() == 2 || (this.getCommandArgsCount() == 1 && !this.getCommandArg(0).equals("-true") && !this.getCommandArg(0).equals("-false"))) {
            s = this.getCommandArg(0);
        }
        boolean b = true;
        if (this.getCommandArgsCount() > 0) {
            b = !this.getCommandArg(this.getCommandArgsCount() - 1).equals("-false");
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(s);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        final String displayName = playerByUserNameForCommand.getDisplayName();
        VoiceManager.instance.VMServerBan(playerByUserNameForCommand.OnlineID, b);
        if (b) {
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), displayName));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, displayName);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), displayName));
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, displayName);
    }
}
