// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.iso.weather.ClimateManager;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandName;
import zombie.commands.DisabledCommand;
import zombie.commands.CommandBase;

@DisabledCommand
@CommandName(name = "thunder")
@AltCommandArgs({ @CommandArgs(required = { "(start)" }, argName = "starts thunder"), @CommandArgs(required = { "(stop)" }, argName = "stops thunder") })
@CommandHelp(helpText = "UI_ServerOptionDesc_Thunder")
@RequiredRight(requiredRights = 60)
public class ThunderCommand extends CommandBase
{
    public static final String startThunder = "starts thunder";
    public static final String stopThunder = "stops thunder";
    
    public ThunderCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        if ("starts thunder".equals(this.argsName)) {
            ClimateManager.getInstance().transmitServerTriggerStorm(24.0f);
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
            return "Thunder started";
        }
        if ("stops thunder".equals(this.argsName)) {
            ClimateManager.getInstance().transmitServerStopWeather();
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
            return "Thunder stopped";
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
        return "missing/unknown argument to /thunder";
    }
}
