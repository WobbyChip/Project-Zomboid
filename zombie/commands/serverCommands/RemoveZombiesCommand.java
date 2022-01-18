// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.iso.IsoGridSquare;
import zombie.core.logger.LoggerManager;
import zombie.popman.NetworkZombiePacker;
import zombie.characters.IsoZombie;
import zombie.util.Type;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "removezombies")
@CommandArgs(varArgs = true)
@CommandHelp(helpText = "UI_ServerOptionDesc_RemoveZombies")
@RequiredRight(requiredRights = 44)
public class RemoveZombiesCommand extends CommandBase
{
    public RemoveZombiesCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        int tryParseInt = -1;
        int tryParseInt2 = -1;
        int tryParseInt3 = -1;
        int tryParseInt4 = -1;
        boolean tryParseBoolean = false;
        boolean tryParseBoolean2 = false;
        boolean tryParseBoolean3 = false;
        for (int i = 0; i < this.getCommandArgsCount() - 1; i += 2) {
            final String commandArg = this.getCommandArg(i);
            final String commandArg2 = this.getCommandArg(i + 1);
            final String s = commandArg;
            switch (s) {
                case "-radius": {
                    tryParseInt = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-reanimated": {
                    tryParseBoolean = StringUtils.tryParseBoolean(commandArg2);
                    break;
                }
                case "-x": {
                    tryParseInt2 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-y": {
                    tryParseInt3 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-z": {
                    tryParseInt4 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-remove": {
                    tryParseBoolean2 = StringUtils.tryParseBoolean(commandArg2);
                    break;
                }
                case "-clear": {
                    tryParseBoolean3 = StringUtils.tryParseBoolean(commandArg2);
                    break;
                }
                default: {
                    return this.getHelp();
                }
            }
        }
        if (tryParseBoolean2) {
            GameServer.removeZombiesConnection = this.connection;
            return "Zombies removed.";
        }
        if (tryParseInt4 < 0 || tryParseInt4 >= 8) {
            return "invalid z";
        }
        for (int j = tryParseInt3 - tryParseInt; j <= tryParseInt3 + tryParseInt; ++j) {
            for (int k = tryParseInt2 - tryParseInt; k <= tryParseInt2 + tryParseInt; ++k) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(k, j, tryParseInt4);
                if (gridSquare != null) {
                    if (tryParseBoolean3) {
                        if (!gridSquare.getStaticMovingObjects().isEmpty()) {
                            for (int l = gridSquare.getStaticMovingObjects().size() - 1; l >= 0; --l) {
                                final IsoDeadBody isoDeadBody = Type.tryCastTo(gridSquare.getStaticMovingObjects().get(l), IsoDeadBody.class);
                                if (isoDeadBody != null) {
                                    GameServer.sendRemoveCorpseFromMap(isoDeadBody);
                                    isoDeadBody.removeFromWorld();
                                    isoDeadBody.removeFromSquare();
                                }
                            }
                        }
                    }
                    else if (!gridSquare.getMovingObjects().isEmpty()) {
                        for (int index = gridSquare.getMovingObjects().size() - 1; index >= 0; --index) {
                            final IsoZombie isoZombie = Type.tryCastTo(gridSquare.getMovingObjects().get(index), IsoZombie.class);
                            if (isoZombie != null) {
                                if (tryParseBoolean || !isoZombie.isReanimatedPlayer()) {
                                    NetworkZombiePacker.getInstance().deleteZombie(isoZombie);
                                    isoZombie.removeFromWorld();
                                    isoZombie.removeFromSquare();
                                }
                            }
                        }
                    }
                }
            }
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getExecutorUsername(), tryParseInt2, tryParseInt3), "IMPORTANT");
        return "Zombies removed.";
    }
}
