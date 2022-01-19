// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.core.logger.LoggerManager;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "createhorde2")
@CommandArgs(varArgs = true)
@CommandHelp(helpText = "UI_ServerOptionDesc_CreateHorde2")
@RequiredRight(requiredRights = 44)
public class CreateHorde2Command extends CommandBase
{
    public CreateHorde2Command(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() {
        int tryParseInt = -1;
        int tryParseInt2 = -1;
        int tryParseInt3 = -1;
        int tryParseInt4 = -1;
        int tryParseInt5 = -1;
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        float floatValue = 1.0f;
        String discardNullOrWhitespace = null;
        for (int i = 0; i < this.getCommandArgsCount() - 1; i += 2) {
            final String commandArg = this.getCommandArg(i);
            final String commandArg2 = this.getCommandArg(i + 1);
            final String s = commandArg;
            switch (s) {
                case "-count": {
                    tryParseInt = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-radius": {
                    tryParseInt2 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-x": {
                    tryParseInt3 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-y": {
                    tryParseInt4 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-z": {
                    tryParseInt5 = PZMath.tryParseInt(commandArg2, -1);
                    break;
                }
                case "-outfit": {
                    discardNullOrWhitespace = StringUtils.discardNullOrWhitespace(commandArg2);
                    break;
                }
                case "-crawler": {
                    b = !"false".equals(commandArg2);
                    break;
                }
                case "-isFallOnFront": {
                    b2 = !"false".equals(commandArg2);
                    break;
                }
                case "-isFakeDead": {
                    b3 = !"false".equals(commandArg2);
                    break;
                }
                case "-knockedDown": {
                    b4 = !"false".equals(commandArg2);
                    break;
                }
                case "-health": {
                    floatValue = Float.valueOf(commandArg2);
                    break;
                }
                default: {
                    return this.getHelp();
                }
            }
        }
        final int clamp = PZMath.clamp(tryParseInt, 1, 500);
        if (IsoWorld.instance.CurrentCell.getGridSquare(tryParseInt3, tryParseInt4, tryParseInt5) == null) {
            return "invalid location";
        }
        if (discardNullOrWhitespace != null && OutfitManager.instance.FindMaleOutfit(discardNullOrWhitespace) == null && OutfitManager.instance.FindFemaleOutfit(discardNullOrWhitespace) == null) {
            return "invalid outfit";
        }
        Integer n2 = null;
        if (discardNullOrWhitespace != null) {
            if (OutfitManager.instance.FindFemaleOutfit(discardNullOrWhitespace) == null) {
                n2 = Integer.MIN_VALUE;
            }
            else if (OutfitManager.instance.FindMaleOutfit(discardNullOrWhitespace) == null) {
                n2 = Integer.MAX_VALUE;
            }
        }
        for (int j = 0; j < clamp; ++j) {
            LuaManager.GlobalObject.addZombiesInOutfit((tryParseInt2 <= 0) ? tryParseInt3 : Rand.Next(tryParseInt3 - tryParseInt2, tryParseInt3 + tryParseInt2 + 1), (tryParseInt2 <= 0) ? tryParseInt4 : Rand.Next(tryParseInt4 - tryParseInt2, tryParseInt4 + tryParseInt2 + 1), tryParseInt5, 1, discardNullOrWhitespace, n2, b, b2, b3, b4, floatValue);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getExecutorUsername(), clamp, tryParseInt3, tryParseInt4), "IMPORTANT");
        return "Horde spawned.";
    }
}
