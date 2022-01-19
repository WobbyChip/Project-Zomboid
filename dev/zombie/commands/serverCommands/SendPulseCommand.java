// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "sendpulse")
@CommandHelp(helpText = "UI_ServerOptionDesc_SendPulse")
@RequiredRight(requiredRights = 32)
public class SendPulseCommand extends CommandBase
{
    public SendPulseCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        if (this.connection != null) {
            if (!(this.connection.sendPulse = !this.connection.sendPulse)) {
                final ByteBufferWriter startPacket = this.connection.startPacket();
                PacketTypes.PacketType.ServerPulse.doPacket(startPacket);
                startPacket.putLong(-1L);
                PacketTypes.PacketType.ServerPulse.send(this.connection);
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.connection.sendPulse ? "on" : "off");
        }
        return "can't do this from the server console";
    }
}
