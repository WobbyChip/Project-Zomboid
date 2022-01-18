// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.util.Iterator;
import zombie.network.ServerOptions;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "showoptions")
@CommandHelp(helpText = "UI_ServerOptionDesc_ShowOptions")
@RequiredRight(requiredRights = 61)
public class ShowOptionsCommand extends CommandBase
{
    public ShowOptionsCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final Iterator<String> iterator = ServerOptions.instance.getPublicOptions().iterator();
        String s = " <LINE> ";
        if (this.connection == null) {
            s = "\n";
        }
        String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        while (iterator.hasNext()) {
            final String s3 = iterator.next();
            if (!s3.equals("ServerWelcomeMessage")) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, ServerOptions.instance.getOptionByName(s3).asConfigOption().getValueAsString(), s);
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, ServerOptions.instance.ServerWelcomeMessage.getValue());
    }
}
