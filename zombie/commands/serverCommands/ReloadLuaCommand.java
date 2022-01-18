// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import java.util.Iterator;
import zombie.Lua.LuaManager;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "reloadlua")
@CommandArgs(required = { "(\\S+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_ReloadLua")
@RequiredRight(requiredRights = 32)
public class ReloadLuaCommand extends CommandBase
{
    public ReloadLuaCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        for (final String o : LuaManager.loaded) {
            if (o.endsWith(commandArg)) {
                LuaManager.loaded.remove(o);
                LuaManager.RunLua(o, true);
                return "Lua file reloaded";
            }
        }
        return "Unknown Lua file";
    }
}
