// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.sql.SQLException;
import zombie.core.network.ByteBufferWriter;
import zombie.network.ServerOptions;
import zombie.network.PacketTypes;
import zombie.core.znet.SteamUtils;
import zombie.network.GameServer;
import zombie.core.logger.LoggerManager;
import zombie.network.Userlog;
import zombie.network.ServerWorldDatabase;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "banuser")
@AltCommandArgs({ @CommandArgs(required = { "(.+)" }), @CommandArgs(required = { "(.+)", "-r", "(.+)" }) })
@CommandHelp(helpText = "UI_ServerOptionDesc_BanUser")
@RequiredRight(requiredRights = 36)
public class BanUserCommand extends CommandBase
{
    private String reason;
    
    public BanUserCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
        this.reason = "";
    }
    
    @Override
    protected String Command() throws SQLException {
        final String commandArg = this.getCommandArg(0);
        if (this.hasOptionalArg(1)) {
            this.reason = this.getCommandArg(1);
        }
        final String banUser = ServerWorldDatabase.instance.banUser(commandArg, true);
        ServerWorldDatabase.instance.addUserlog(commandArg, Userlog.UserlogType.Banned, this.reason, this.getExecutorUsername(), 1);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), commandArg, (this.reason != null) ? this.reason : ""), "IMPORTANT");
        boolean b = false;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.username.equals(commandArg)) {
                b = true;
                if (SteamUtils.isSteamModeEnabled()) {
                    LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), udpConnection.steamID, udpConnection.username, (this.reason != null) ? this.reason : ""), "IMPORTANT");
                    ServerWorldDatabase.instance.banSteamID(SteamUtils.convertSteamIDToString(udpConnection.steamID), this.reason, true);
                }
                else {
                    LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), udpConnection.ip, udpConnection.username, (this.reason != null) ? this.reason : ""), "IMPORTANT");
                    ServerWorldDatabase.instance.banIp(udpConnection.ip, commandArg, this.reason, true);
                }
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.Kicked.doPacket(startPacket);
                if ("".equals(this.reason)) {
                    startPacket.putUTF("You have been banned from this server.");
                }
                else {
                    startPacket.putUTF(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.reason));
                }
                PacketTypes.PacketType.Kicked.send(udpConnection);
                udpConnection.forceDisconnect();
                break;
            }
        }
        if (b && ServerOptions.instance.BanKickGlobalSound.getValue()) {
            GameServer.PlaySoundAtEveryPlayer("Thunder");
        }
        return banUser;
    }
}
