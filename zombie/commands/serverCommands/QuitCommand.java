// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.network.ServerMap;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "quit")
@CommandHelp(helpText = "UI_ServerOptionDesc_Quit")
@RequiredRight(requiredRights = 32)
public class QuitCommand extends CommandBase
{
    public QuitCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        ServerMap.instance.QueueSaveAll();
        ServerMap.instance.QueueQuit();
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
        return "Quit";
    }
}
