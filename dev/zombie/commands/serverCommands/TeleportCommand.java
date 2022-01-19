// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.network.PacketTypes;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.CommandBase;

@CommandNames({ @CommandName(name = "teleport"), @CommandName(name = "tp") })
@AltCommandArgs({ @CommandArgs(required = { "(.+)" }, argName = "just port to user"), @CommandArgs(required = { "(.+)", "(.+)" }, argName = "teleport user1 to user 2") })
@CommandHelp(helpText = "UI_ServerOptionDesc_Teleport")
@RequiredRight(requiredRights = 61)
public class TeleportCommand extends CommandBase
{
    public static final String justToUser = "just port to user";
    public static final String portUserToUser = "teleport user1 to user 2";
    private String username1;
    private String username2;
    
    public TeleportCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String argsName = this.argsName;
        switch (argsName) {
            case "just port to user": {
                this.username1 = this.getCommandArg(0);
                return this.TeleportMeToUser();
            }
            case "teleport user1 to user 2": {
                this.username1 = this.getCommandArg(0);
                this.username2 = this.getCommandArg(1);
                return this.TeleportUser1ToUser2();
            }
            default: {
                return this.CommandArgumentsNotMatch();
            }
        }
    }
    
    private String TeleportMeToUser() {
        if (this.connection == null) {
            return "Need player to teleport to, ex /teleport user1 user2";
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(this.username1);
        if (playerByUserNameForCommand != null) {
            this.username1 = playerByUserNameForCommand.getDisplayName();
            final ByteBufferWriter startPacket = this.connection.startPacket();
            PacketTypes.PacketType.Teleport.doPacket(startPacket);
            startPacket.putByte((byte)0);
            startPacket.putFloat(playerByUserNameForCommand.getX());
            startPacket.putFloat(playerByUserNameForCommand.getY());
            startPacket.putFloat(playerByUserNameForCommand.getZ());
            PacketTypes.PacketType.Teleport.send(this.connection);
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), this.username1));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.username1);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.username1);
    }
    
    private String TeleportUser1ToUser2() {
        if (this.getAccessLevel() == 1 && !this.username1.equals(this.getExecutorUsername())) {
            return "An Observer can only teleport himself";
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(this.username1);
        final IsoPlayer playerByUserNameForCommand2 = GameServer.getPlayerByUserNameForCommand(this.username2);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.username1);
        }
        if (playerByUserNameForCommand2 == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.username2);
        }
        this.username1 = playerByUserNameForCommand.getDisplayName();
        this.username2 = playerByUserNameForCommand2.getDisplayName();
        final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer(playerByUserNameForCommand);
        if (connectionFromPlayer == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.username1);
        }
        final ByteBufferWriter startPacket = connectionFromPlayer.startPacket();
        PacketTypes.PacketType.Teleport.doPacket(startPacket);
        startPacket.putByte((byte)playerByUserNameForCommand.PlayerIndex);
        startPacket.putFloat(playerByUserNameForCommand2.getX());
        startPacket.putFloat(playerByUserNameForCommand2.getY());
        startPacket.putFloat(playerByUserNameForCommand2.getZ());
        PacketTypes.PacketType.Teleport.send(connectionFromPlayer);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), this.username1, this.username2));
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.username1, this.username2);
    }
    
    private String CommandArgumentsNotMatch() {
        return this.getHelp();
    }
}
