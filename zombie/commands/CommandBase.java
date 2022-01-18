// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands;

import zombie.commands.serverCommands.ReplayCommands;
import zombie.commands.serverCommands.SetAccessLevelCommand;
import zombie.commands.serverCommands.SendPulseCommand;
import zombie.commands.serverCommands.RemoveZombiesCommand;
import zombie.commands.serverCommands.ReloadLuaCommand;
import zombie.commands.serverCommands.CreateHorde2Command;
import zombie.commands.serverCommands.CreateHordeCommand;
import zombie.commands.serverCommands.AddVehicleCommand;
import zombie.commands.serverCommands.AddXPCommand;
import zombie.commands.serverCommands.AddItemCommand;
import zombie.commands.serverCommands.PlayersCommand;
import zombie.commands.serverCommands.ClearCommand;
import zombie.commands.serverCommands.HelpCommand;
import zombie.commands.serverCommands.InvisibleCommand;
import zombie.commands.serverCommands.NoClipCommand;
import zombie.commands.serverCommands.VoiceBanCommand;
import zombie.commands.serverCommands.GodModeCommand;
import zombie.commands.serverCommands.ShowOptionsCommand;
import zombie.commands.serverCommands.ChangeOptionCommand;
import zombie.commands.serverCommands.RemoveUserFromWhiteList;
import zombie.commands.serverCommands.AddUserToWhiteListCommand;
import zombie.commands.serverCommands.UnbanSteamIDCommand;
import zombie.commands.serverCommands.UnbanUserCommand;
import zombie.commands.serverCommands.BanSteamIDCommand;
import zombie.commands.serverCommands.BanUserCommand;
import zombie.commands.serverCommands.ReloadOptionsCommand;
import zombie.commands.serverCommands.GunShotCommand;
import zombie.commands.serverCommands.ThunderCommand;
import zombie.commands.serverCommands.StopRainCommand;
import zombie.commands.serverCommands.StartRainCommand;
import zombie.commands.serverCommands.ReleaseSafehouseCommand;
import zombie.commands.serverCommands.TeleportToCommand;
import zombie.commands.serverCommands.TeleportCommand;
import zombie.commands.serverCommands.KickUserCommand;
import zombie.commands.serverCommands.AddAllToWhiteListCommand;
import zombie.commands.serverCommands.ChopperCommand;
import zombie.commands.serverCommands.AlarmCommand;
import zombie.commands.serverCommands.QuitCommand;
import zombie.commands.serverCommands.DebugPlayerCommand;
import zombie.commands.serverCommands.RemoveAdminCommand;
import zombie.commands.serverCommands.GrantAdminCommand;
import zombie.commands.serverCommands.AddUserCommand;
import zombie.commands.serverCommands.ConnectionsCommand;
import zombie.commands.serverCommands.ServerMessageCommand;
import zombie.commands.serverCommands.SaveCommand;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.ArrayList;
import zombie.core.Translator;
import java.util.regex.Pattern;
import zombie.core.raknet.UdpConnection;

public abstract class CommandBase
{
    private final int playerType;
    private final String username;
    private final String command;
    private String[] commandArgs;
    private boolean parsingSuccessful;
    private boolean parsed;
    private String message;
    protected final UdpConnection connection;
    protected String argsName;
    protected static final String defaultArgsName = "default args name. Nothing match";
    private static Class[] childrenClasses;
    
    public static Class[] getSubClasses() {
        return CommandBase.childrenClasses;
    }
    
    public static Class findCommandCls(final String input) {
        for (final Class clazz : CommandBase.childrenClasses) {
            if (!isDisabled(clazz)) {
                final CommandName[] array = clazz.getAnnotationsByType(CommandName.class);
                for (int length2 = array.length, j = 0; j < length2; ++j) {
                    if (Pattern.compile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, array[j].name()), 2).matcher(input).find()) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }
    
    public static String getHelp(final Class clazz) {
        final CommandHelp commandHelp = getAnnotation(CommandHelp.class, clazz);
        if (commandHelp == null) {
            return null;
        }
        if (commandHelp.shouldTranslated()) {
            return Translator.getText(commandHelp.helpText());
        }
        return commandHelp.helpText();
    }
    
    public static String getCommandName(final Class clazz) {
        return clazz.getAnnotationsByType(CommandName.class)[0].name();
    }
    
    public static boolean isDisabled(final Class clazz) {
        return getAnnotation(DisabledCommand.class, clazz) != null;
    }
    
    public static int accessLevelToInt(final String s) {
        switch (s) {
            case "admin": {
                return 32;
            }
            case "observer": {
                return 1;
            }
            case "moderator": {
                return 4;
            }
            case "overseer": {
                return 8;
            }
            case "gm": {
                return 16;
            }
            default: {
                return 2;
            }
        }
    }
    
    protected CommandBase(final String username, final String s, final String s2, final UdpConnection connection) {
        this.parsingSuccessful = false;
        this.parsed = false;
        this.message = "";
        this.argsName = "default args name. Nothing match";
        this.username = username;
        this.command = s2;
        this.connection = connection;
        this.playerType = accessLevelToInt(s);
        final ArrayList<String> list = new ArrayList<String>();
        final Matcher matcher = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(s2);
        while (matcher.find()) {
            list.add(matcher.group(1).replace("\"", ""));
        }
        this.commandArgs = new String[list.size() - 1];
        for (int i = 1; i < list.size(); ++i) {
            this.commandArgs[i - 1] = list.get(i);
        }
    }
    
    public String Execute() throws SQLException {
        if (this.canBeExecuted()) {
            return this.Command();
        }
        return this.message;
    }
    
    public boolean canBeExecuted() {
        if (this.parsed) {
            return this.parsingSuccessful;
        }
        if (!this.PlayerSatisfyRequiredRights()) {
            this.message = this.playerHasNoRightError();
            return false;
        }
        return this.parsingSuccessful = this.parseCommand();
    }
    
    public boolean isCommandComeFromServerConsole() {
        return this.connection == null;
    }
    
    protected RequiredRight getRequiredRights() {
        return this.getClass().getAnnotation(RequiredRight.class);
    }
    
    protected CommandArgs[] getCommandArgVariants() {
        return this.getClass().getAnnotationsByType(CommandArgs.class);
    }
    
    public boolean hasHelp() {
        return this.getClass().getAnnotation(CommandHelp.class) != null;
    }
    
    protected String getHelp() {
        return getHelp(this.getClass());
    }
    
    public String getCommandArg(final Integer n) {
        if (this.commandArgs == null || n < 0 || n >= this.commandArgs.length) {
            return null;
        }
        return this.commandArgs[n];
    }
    
    public boolean hasOptionalArg(final Integer n) {
        return this.commandArgs != null && n >= 0 && n < this.commandArgs.length;
    }
    
    public int getCommandArgsCount() {
        return this.commandArgs.length;
    }
    
    protected abstract String Command() throws SQLException;
    
    public boolean parseCommand() {
        final CommandArgs[] commandArgVariants = this.getCommandArgVariants();
        if (commandArgVariants.length == 1 && commandArgVariants[0].varArgs()) {
            return this.parsed = true;
        }
        boolean b = (commandArgVariants.length != 0 && this.commandArgs.length != 0) || (commandArgVariants.length == 0 && this.commandArgs.length == 0);
        final ArrayList<String> list = new ArrayList<String>();
        for (final CommandArgs commandArgs : commandArgVariants) {
            list.clear();
            this.message = "";
            int n = 0;
            b = true;
            for (int j = 0; j < commandArgs.required().length; ++j) {
                final String regex = commandArgs.required()[j];
                if (n == this.commandArgs.length) {
                    b = false;
                    break;
                }
                final Matcher matcher = Pattern.compile(regex).matcher(this.commandArgs[n]);
                if (!matcher.matches()) {
                    b = false;
                    break;
                }
                for (int k = 0; k < matcher.groupCount(); ++k) {
                    list.add(matcher.group(k + 1));
                }
                ++n;
            }
            if (b) {
                if (n == this.commandArgs.length) {
                    this.argsName = commandArgs.argName();
                    break;
                }
                if (!commandArgs.optional().equals("no value")) {
                    final Matcher matcher2 = Pattern.compile(commandArgs.optional()).matcher(this.commandArgs[n]);
                    if (matcher2.matches()) {
                        for (int l = 0; l < matcher2.groupCount(); ++l) {
                            list.add(matcher2.group(l + 1));
                        }
                    }
                    else {
                        b = false;
                    }
                }
                else if (n < this.commandArgs.length) {
                    b = false;
                }
                if (b) {
                    this.argsName = commandArgs.argName();
                    break;
                }
            }
        }
        if (b) {
            this.commandArgs = new String[list.size()];
            this.commandArgs = list.toArray(this.commandArgs);
        }
        else {
            this.message = this.invalidCommand();
            this.commandArgs = new String[0];
        }
        this.parsed = true;
        return b;
    }
    
    protected int getAccessLevel() {
        return this.playerType;
    }
    
    protected String getExecutorUsername() {
        return this.username;
    }
    
    protected String getCommand() {
        return this.command;
    }
    
    protected static <T> T getAnnotation(final Class<T> annotationClass, final Class clazz) {
        return clazz.getAnnotation(annotationClass);
    }
    
    public boolean isParsingSuccessful() {
        if (!this.parsed) {
            this.parsingSuccessful = this.parseCommand();
        }
        return this.parsingSuccessful;
    }
    
    private boolean PlayerSatisfyRequiredRights() {
        return (this.playerType & this.getRequiredRights().requiredRights()) != 0x0;
    }
    
    private String invalidCommand() {
        if (this.hasHelp()) {
            return this.getHelp();
        }
        return Translator.getText("UI_command_arg_parse_failed", this.command);
    }
    
    private String playerHasNoRightError() {
        return Translator.getText("UI_has_no_right_to_execute_command", this.username, this.command);
    }
    
    static {
        CommandBase.childrenClasses = new Class[] { SaveCommand.class, ServerMessageCommand.class, ConnectionsCommand.class, AddUserCommand.class, GrantAdminCommand.class, RemoveAdminCommand.class, DebugPlayerCommand.class, QuitCommand.class, AlarmCommand.class, ChopperCommand.class, AddAllToWhiteListCommand.class, KickUserCommand.class, TeleportCommand.class, TeleportToCommand.class, ReleaseSafehouseCommand.class, StartRainCommand.class, StopRainCommand.class, ThunderCommand.class, GunShotCommand.class, ReloadOptionsCommand.class, BanUserCommand.class, BanSteamIDCommand.class, UnbanUserCommand.class, UnbanSteamIDCommand.class, AddUserToWhiteListCommand.class, RemoveUserFromWhiteList.class, ChangeOptionCommand.class, ShowOptionsCommand.class, GodModeCommand.class, VoiceBanCommand.class, NoClipCommand.class, InvisibleCommand.class, HelpCommand.class, ClearCommand.class, PlayersCommand.class, AddItemCommand.class, AddXPCommand.class, AddVehicleCommand.class, CreateHordeCommand.class, CreateHorde2Command.class, ReloadLuaCommand.class, RemoveZombiesCommand.class, SendPulseCommand.class, SetAccessLevelCommand.class, ReplayCommands.class };
    }
}
