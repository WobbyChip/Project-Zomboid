// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import zombie.network.ServerOptions;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "help")
@CommandArgs(optional = "(\\w+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_Help")
@RequiredRight(requiredRights = 32)
public class HelpCommand extends CommandBase
{
    public HelpCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        if (commandArg == null) {
            String str = " <LINE> ";
            final StringBuilder sb = new StringBuilder();
            if (this.connection == null) {
                str = "\n";
            }
            if (!GameServer.bServer) {
                final Iterator<String> iterator = ServerOptions.getClientCommandList(this.connection != null).iterator();
                while (iterator.hasNext()) {
                    sb.append(iterator.next());
                }
            }
            sb.append("List of ").append("server").append(" commands : ");
            final TreeMap<String, String> treeMap = new TreeMap<String, String>();
            for (final Class clazz : CommandBase.getSubClasses()) {
                if (!CommandBase.isDisabled(clazz)) {
                    final String help = CommandBase.getHelp(clazz);
                    if (help != null) {
                        treeMap.put(CommandBase.getCommandName(clazz), help);
                    }
                }
            }
            for (final Map.Entry<String, String> entry : treeMap.entrySet()) {
                sb.append(str).append("* ").append(entry.getKey()).append(" : ").append(entry.getValue());
            }
            return sb.toString();
        }
        final Class commandCls = CommandBase.findCommandCls(commandArg);
        if (commandCls != null) {
            return CommandBase.getHelp(commandCls);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
    }
}
