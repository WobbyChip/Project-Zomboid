// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.iso.IsoWorld;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "chopper")
@CommandArgs(optional = "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)")
@CommandHelp(helpText = "UI_ServerOptionDesc_Chopper")
@RequiredRight(requiredRights = 60)
public class ChopperCommand extends CommandBase
{
    public ChopperCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        String help;
        if (this.getCommandArgsCount() == 1) {
            if ("stop".equals(this.getCommandArg(0))) {
                IsoWorld.instance.helicopter.deactivate();
                help = "Chopper deactivated";
            }
            else if ("start".equals(this.getCommandArg(0))) {
                IsoWorld.instance.helicopter.pickRandomTarget();
                help = "Chopper activated";
            }
            else {
                help = this.getHelp();
            }
        }
        else {
            IsoWorld.instance.helicopter.pickRandomTarget();
            help = "Chopper launched";
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
        return help;
    }
}
