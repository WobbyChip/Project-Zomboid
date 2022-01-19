// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "clear")
@RequiredRight(requiredRights = 32)
public class ClearCommand extends CommandBase
{
    public ClearCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        String s = "Console cleared";
        if (this.connection == null) {
            for (int i = 0; i < 100; ++i) {
                System.out.println();
            }
        }
        else {
            final StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 50; ++j) {
                sb.append("<LINE>");
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, sb.toString(), s);
        }
        return s;
    }
}
