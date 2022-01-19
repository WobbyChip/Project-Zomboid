// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "additem")
@AltCommandArgs({ @CommandArgs(required = { "(.+)", "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)" }, optional = "(\\d+)", argName = "add item to player"), @CommandArgs(required = { "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)" }, optional = "(\\d+)", argName = "add item to me") })
@CommandHelp(helpText = "UI_ServerOptionDesc_AddItem")
@RequiredRight(requiredRights = 60)
public class AddItemCommand extends CommandBase
{
    public static final String toMe = "add item to me";
    public static final String toPlayer = "add item to player";
    
    public AddItemCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        int int1 = 1;
        if (this.argsName.equals("add item to me") && this.connection == null) {
            return "Pass username";
        }
        if (this.getCommandArgsCount() > 1) {
            final int commandArgsCount = this.getCommandArgsCount();
            if ((this.argsName.equals("add item to me") && commandArgsCount == 2) || (this.argsName.equals("add item to player") && commandArgsCount == 3)) {
                int1 = Integer.parseInt(this.getCommandArg(this.getCommandArgsCount() - 1));
            }
        }
        String s;
        if (this.argsName.equals("add item to player")) {
            final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(this.getCommandArg(0));
            if (playerByUserNameForCommand == null) {
                return "No such user";
            }
            s = playerByUserNameForCommand.getDisplayName();
        }
        else {
            final IsoPlayer playerByRealUserName = GameServer.getPlayerByRealUserName(this.getExecutorUsername());
            if (playerByRealUserName == null) {
                return "No such user";
            }
            s = playerByRealUserName.getDisplayName();
        }
        String s2;
        if (this.argsName.equals("add item to me")) {
            s2 = this.getCommandArg(0);
        }
        else {
            s2 = this.getCommandArg(1);
        }
        if (ScriptManager.instance.FindItem(s2) == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
        }
        final IsoPlayer playerByUserNameForCommand2 = GameServer.getPlayerByUserNameForCommand(s);
        if (playerByUserNameForCommand2 != null) {
            s = playerByUserNameForCommand2.getDisplayName();
            final UdpConnection connectionByPlayerOnlineID = GameServer.getConnectionByPlayerOnlineID(playerByUserNameForCommand2.OnlineID);
            if (connectionByPlayerOnlineID != null) {
                final ByteBufferWriter startPacket = connectionByPlayerOnlineID.startPacket();
                PacketTypes.PacketType.AddItemInInventory.doPacket(startPacket);
                startPacket.putShort(playerByUserNameForCommand2.OnlineID);
                startPacket.putUTF(s2);
                startPacket.putInt(int1);
                PacketTypes.PacketType.AddItemInInventory.send(connectionByPlayerOnlineID);
                LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername(), s2, s));
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s);
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
}
