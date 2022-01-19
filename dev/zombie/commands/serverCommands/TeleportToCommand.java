// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.logger.LoggerManager;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandNames;
import zombie.commands.CommandBase;

@CommandNames({ @CommandName(name = "teleportto"), @CommandName(name = "tpto") })
@AltCommandArgs({ @CommandArgs(required = { "(.+)", "(\\d+),(\\d+),(\\d+)" }, argName = "Teleport user"), @CommandArgs(required = { "(\\d+),(\\d+),(\\d+)" }, argName = "teleport me") })
@CommandHelp(helpText = "UI_ServerOptionDesc_TeleportTo")
@RequiredRight(requiredRights = 61)
public class TeleportToCommand extends CommandBase
{
    public static final String teleportMe = "teleport me";
    public static final String teleportUser = "Teleport user";
    private String username;
    private Float[] coords;
    
    public TeleportToCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String argsName = this.argsName;
        switch (argsName) {
            case "teleport me": {
                this.coords = new Float[3];
                for (int i = 0; i < 3; ++i) {
                    this.coords[i] = Float.parseFloat(this.getCommandArg(i));
                }
                return this.TeleportMeToCoords();
            }
            case "Teleport user": {
                this.username = this.getCommandArg(0);
                this.coords = new Float[3];
                for (int j = 0; j < 3; ++j) {
                    this.coords[j] = Float.parseFloat(this.getCommandArg(j + 1));
                }
                return this.TeleportUserToCoords();
            }
            default: {
                return this.CommandArgumentsNotMatch();
            }
        }
    }
    
    private String TeleportMeToCoords() {
        final float floatValue = this.coords[0];
        final float floatValue2 = this.coords[1];
        final float floatValue3 = this.coords[2];
        if (this.connection == null) {
            return "Error";
        }
        final ByteBufferWriter startPacket = this.connection.startPacket();
        PacketTypes.PacketType.Teleport.doPacket(startPacket);
        startPacket.putByte((byte)0);
        startPacket.putFloat(floatValue);
        startPacket.putFloat(floatValue2);
        startPacket.putFloat(floatValue3);
        PacketTypes.PacketType.Teleport.send(this.connection);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getExecutorUsername(), (int)floatValue, (int)floatValue2, (int)floatValue3));
        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, (int)floatValue, (int)floatValue2, (int)floatValue3);
    }
    
    private String TeleportUserToCoords() {
        final float floatValue = this.coords[0];
        final float floatValue2 = this.coords[1];
        final float floatValue3 = this.coords[2];
        if (this.connection != null && this.connection.accessLevel.equals("observer") && !this.username.equals(this.getExecutorUsername())) {
            return "An Observer can only teleport himself";
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(this.username);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.username);
        }
        final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer(playerByUserNameForCommand);
        final ByteBufferWriter startPacket = connectionFromPlayer.startPacket();
        PacketTypes.PacketType.Teleport.doPacket(startPacket);
        startPacket.putByte((byte)0);
        startPacket.putFloat(floatValue);
        startPacket.putFloat(floatValue2);
        startPacket.putFloat(floatValue3);
        PacketTypes.PacketType.Teleport.send(connectionFromPlayer);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getExecutorUsername(), (int)floatValue, (int)floatValue2, (int)floatValue3));
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.username, (int)floatValue, (int)floatValue2, (int)floatValue3);
    }
    
    private String CommandArgumentsNotMatch() {
        return this.getHelp();
    }
}
