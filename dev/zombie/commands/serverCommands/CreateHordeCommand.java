// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.core.logger.LoggerManager;
import zombie.ZombieSpawnRecorder;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.VirtualZombieManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "createhorde")
@CommandArgs(required = { "(\\d+)" }, optional = "(.+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_CreateHorde")
@RequiredRight(requiredRights = 44)
public class CreateHordeCommand extends CommandBase
{
    public CreateHordeCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final Integer value = Integer.parseInt(this.getCommandArg(0));
        final String commandArg = this.getCommandArg(1);
        IsoMovingObject isoMovingObject = null;
        if (this.getCommandArgsCount() == 2) {
            isoMovingObject = GameServer.getPlayerByUserNameForCommand(commandArg);
            if (isoMovingObject == null) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
            }
        }
        else if (this.connection != null) {
            isoMovingObject = GameServer.getAnyPlayerFromConnection(this.connection);
        }
        if (value == null) {
            return this.getHelp();
        }
        final Integer value2 = Math.min(value, 500);
        if (isoMovingObject != null) {
            for (int i = 0; i < value2; ++i) {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(IsoWorld.instance.CurrentCell.getGridSquare(Rand.Next(isoMovingObject.getX() - 10.0f, isoMovingObject.getX() + 10.0f), Rand.Next(isoMovingObject.getY() - 10.0f, isoMovingObject.getY() + 10.0f), isoMovingObject.getZ()));
                final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
                if (realZombieAlways != null) {
                    ZombieSpawnRecorder.instance.record(realZombieAlways, this.getClass().getSimpleName());
                }
            }
            LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;FF)Ljava/lang/String;, this.getExecutorUsername(), value2, isoMovingObject.getX(), isoMovingObject.getY()), "IMPORTANT");
            return "Horde spawned.";
        }
        return "Specify a player to create the horde near to.";
    }
}
