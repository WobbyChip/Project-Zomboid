// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "save")
@CommandHelp(helpText = "UI_ServerOptionDesc_Save")
@RequiredRight(requiredRights = 32)
public class SaveCommand extends CommandBase
{
    public SaveCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        ServerMap.instance.QueueSaveAll();
        GameServer.PauseAllClients();
        return "World saved";
    }
}
