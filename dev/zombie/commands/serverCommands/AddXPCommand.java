// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.network.PacketTypes;
import zombie.characters.skills.PerkFactory;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "addxp")
@CommandArgs(required = { "(.+)", "(\\S+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_AddXp")
@RequiredRight(requiredRights = 60)
public class AddXPCommand extends CommandBase
{
    public AddXPCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        final String commandArg2 = this.getCommandArg(1);
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(commandArg);
        if (playerByUserNameForCommand == null) {
            return "No such user";
        }
        String s = playerByUserNameForCommand.getDisplayName();
        final String[] split = commandArg2.split("=", 2);
        if (split.length != 2) {
            return this.getHelp();
        }
        final String trim = split[0].trim();
        if (PerkFactory.Perks.FromString(trim) == PerkFactory.Perks.MAX) {
            final String str = (this.connection == null) ? "\n" : " LINE ";
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < PerkFactory.PerkList.size(); ++i) {
                if (PerkFactory.PerkList.get(i) != PerkFactory.Perks.Passiv) {
                    sb.append(PerkFactory.PerkList.get(i));
                    if (i < PerkFactory.PerkList.size()) {
                        sb.append(str);
                    }
                }
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, str, sb.toString());
        }
        int int1;
        try {
            int1 = Integer.parseInt(split[1]);
        }
        catch (NumberFormatException ex) {
            return this.getHelp();
        }
        final IsoPlayer playerByUserNameForCommand2 = GameServer.getPlayerByUserNameForCommand(s);
        if (playerByUserNameForCommand2 != null) {
            s = playerByUserNameForCommand2.getDisplayName();
            final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer(playerByUserNameForCommand2);
            if (connectionFromPlayer != null) {
                final ByteBufferWriter startPacket = connectionFromPlayer.startPacket();
                PacketTypes.PacketType.AddXpCommand.doPacket(startPacket);
                startPacket.putShort(playerByUserNameForCommand2.OnlineID);
                startPacket.putInt(PerkFactory.Perks.FromString(trim).index());
                startPacket.putInt(int1);
                PacketTypes.PacketType.AddXpCommand.send(connectionFromPlayer);
                LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), int1, trim, s));
                return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, int1, trim, s);
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
}
