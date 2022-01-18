// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.core.logger.LoggerManager;
import zombie.core.znet.SteamUtils;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "banid")
@CommandArgs(required = { "(.+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_BanSteamId")
@RequiredRight(requiredRights = 36)
public class BanSteamIDCommand extends CommandBase
{
    public BanSteamIDCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
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
        ServerWorldDatabase.instance.banSteamID(commandArg, "", true);
        final long convertStringToSteamID = SteamUtils.convertStringToSteamID(commandArg);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.steamID == convertStringToSteamID) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.Kicked.doPacket(startPacket);
                startPacket.putUTF("You have been banned from this server.");
                PacketTypes.PacketType.Kicked.send(udpConnection);
                udpConnection.forceDisconnect();
                break;
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
    }
}
