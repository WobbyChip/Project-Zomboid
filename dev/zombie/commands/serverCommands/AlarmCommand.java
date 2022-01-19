// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.AmbientStreamManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "alarm")
@CommandHelp(helpText = "UI_ServerOptionDesc_Alarm")
@RequiredRight(requiredRights = 60)
public class AlarmCommand extends CommandBase
{
    public AlarmCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final IsoPlayer playerByUserName = GameServer.getPlayerByUserName(this.getExecutorUsername());
        if (playerByUserName != null && playerByUserName.getSquare() != null && playerByUserName.getSquare().getBuilding() != null) {
            playerByUserName.getSquare().getBuilding().getDef().bAlarmed = true;
            AmbientStreamManager.instance.doAlarm(playerByUserName.getSquare().getRoom().def);
            return "Alarm sounded";
        }
        return "Not in a room";
    }
}
