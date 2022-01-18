// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.network.ByteBufferWriter;
import zombie.network.ServerOptions;
import zombie.network.PacketTypes;
import zombie.network.GameServer;
import zombie.network.Userlog;
import zombie.network.ServerWorldDatabase;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.CommandBase;

@CommandNames({ @CommandName(name = "kickuser"), @CommandName(name = "disconnect") })
@CommandArgs(required = { "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_Kick")
@RequiredRight(requiredRights = 44)
public class KickUserCommand extends CommandBase
{
    public KickUserCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg));
        ServerWorldDatabase.instance.addUserlog(commandArg, Userlog.UserlogType.Kicked, "", "server", 1);
        boolean b = false;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                if (commandArg.equals(udpConnection.usernames[j])) {
                    b = true;
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.Kicked.doPacket(startPacket);
                    startPacket.putUTF("You have been kicked from this server.");
                    PacketTypes.PacketType.Kicked.send(udpConnection);
                    udpConnection.forceDisconnect();
                    GameServer.addDisconnect(udpConnection);
                    break;
                }
            }
        }
        if (b && ServerOptions.instance.BanKickGlobalSound.getValue()) {
            GameServer.PlaySoundAtEveryPlayer("RumbleThunder");
        }
        if (b) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
    }
}
