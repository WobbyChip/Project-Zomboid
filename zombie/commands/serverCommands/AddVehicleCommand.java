// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.iso.IsoGridSquare;
import zombie.scripting.objects.VehicleScript;
import zombie.characters.IsoPlayer;
import zombie.vehicles.VehiclesDB2;
import zombie.iso.IsoChunk;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.core.physics.WorldSimulation;
import zombie.scripting.ScriptManager;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "addvehicle")
@CommandArgs(required = { "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)" }, optional = "(.+)")
@CommandHelp(helpText = "UI_ServerOptionDesc_AddVehicle")
@RequiredRight(requiredRights = 60)
public class AddVehicleCommand extends CommandBase
{
    public AddVehicleCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        final String commandArg = this.getCommandArg(0);
        String s;
        if (this.getCommandArgsCount() == 2) {
            s = this.getCommandArg(1);
        }
        else {
            if (this.connection == null) {
                return "Pass a username";
            }
            s = this.getExecutorUsername();
        }
        final IsoPlayer playerByUserNameForCommand = GameServer.getPlayerByUserNameForCommand(s);
        if (playerByUserNameForCommand == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        final float x = playerByUserNameForCommand.getX();
        final float y = playerByUserNameForCommand.getY();
        final int n = (int)playerByUserNameForCommand.getZ();
        if (n > 0) {
            return "Z coordinate must be 0 for now";
        }
        final VehicleScript vehicle = ScriptManager.instance.getVehicle(commandArg);
        if (vehicle == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandArg);
        }
        final String scriptName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, vehicle.getModule().getName(), vehicle.getName());
        WorldSimulation.instance.create();
        if (!WorldSimulation.instance.created) {
            return "Physics couldn't be created";
        }
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare((int)x, (int)y, n);
        if (gridSquare == null) {
            return invokedynamic(makeConcatWithConstants:(FFI)Ljava/lang/String;, x, y, n);
        }
        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
        e.setScriptName(scriptName);
        e.setX(x - 1.0f);
        e.setY(y - 0.1f);
        e.setZ(n + 0.2f);
        if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
            e.setSquare(gridSquare);
            e.square.chunk.vehicles.add(e);
            e.chunk = e.square.chunk;
            e.addToWorld();
            VehiclesDB2.instance.addVehicle(e);
            return "Vehicle spawned";
        }
        return "ERROR: I can not spawn the vehicle. Invalid position. Try to change position.";
    }
}
