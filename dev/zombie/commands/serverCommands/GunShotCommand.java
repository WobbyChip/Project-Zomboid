// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.AmbientStreamManager;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "gunshot")
@CommandHelp(helpText = "UI_ServerOptionDesc_Gunshot")
@RequiredRight(requiredRights = 60)
public class GunShotCommand extends CommandBase
{
    public GunShotCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        AmbientStreamManager.instance.doGunEvent();
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getExecutorUsername()));
        return "Gunshot fired";
    }
}
