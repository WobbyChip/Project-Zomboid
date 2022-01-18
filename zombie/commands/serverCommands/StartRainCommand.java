// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.iso.weather.ClimateManager;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "startrain")
@CommandHelp(helpText = "UI_ServerOptionDesc_StartRain")
@RequiredRight(requiredRights = 60)
public class StartRainCommand extends CommandBase
{
    public StartRainCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        ClimateManager.getInstance().transmitServerStartRain(1.0f);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
        return "Rain started";
    }
}
