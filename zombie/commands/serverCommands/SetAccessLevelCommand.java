// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.network.chat.ChatServer;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import java.sql.SQLException;
import zombie.core.raknet.UdpConnection;
import zombie.commands.RequiredRight;
import zombie.commands.CommandHelp;
import zombie.commands.CommandArgs;
import zombie.commands.CommandName;
import zombie.commands.CommandBase;

@CommandName(name = "setaccesslevel")
@CommandArgs(required = { "(.+)", "(\\w+)" })
@CommandHelp(helpText = "UI_ServerOptionDesc_SetAccessLevel")
@RequiredRight(requiredRights = 36)
public class SetAccessLevelCommand extends CommandBase
{
    public SetAccessLevelCommand(final String s, final String s2, final String s3, final UdpConnection udpConnection) {
        super(s, s2, s3, udpConnection);
    }
    
    @Override
    protected String Command() throws SQLException {
        return update(this.getExecutorUsername(), this.connection, this.getCommandArg(0), "none".equals(this.getCommandArg(1)) ? "" : this.getCommandArg(1));
    }
    
    static String update(final String s, final UdpConnection udpConnection, final String s2, final String accessLevel) throws SQLException {
        if ((udpConnection == null || !udpConnection.isCoopHost) && !ServerWorldDatabase.instance.containsUser(s2) && udpConnection != null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
        }
        final IsoPlayer playerByUserName = GameServer.getPlayerByUserName(s2);
        if (udpConnection != null && udpConnection.accessLevel.equals("moderator") && accessLevel.equals("admin")) {
            return "Moderators can't set Admin access level";
        }
        if (!accessLevel.equals("") && !accessLevel.equals("admin") && !accessLevel.equals("moderator") && !accessLevel.equals("overseer") && !accessLevel.equals("gm") && !accessLevel.equals("observer")) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, accessLevel);
        }
        if (playerByUserName != null) {
            final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer(playerByUserName);
            String s3;
            if (connectionFromPlayer != null) {
                s3 = connectionFromPlayer.accessLevel;
            }
            else {
                s3 = playerByUserName.accessLevel;
            }
            if (!s3.equals(accessLevel)) {
                if (accessLevel.equals("admin")) {
                    ChatServer.getInstance().joinAdminChat(playerByUserName.OnlineID);
                }
                else if (s3.equals("admin") && !accessLevel.equals("admin")) {
                    ChatServer.getInstance().leaveAdminChat(playerByUserName.OnlineID);
                }
            }
            playerByUserName.accessLevel = accessLevel;
            if (connectionFromPlayer != null) {
                connectionFromPlayer.accessLevel = accessLevel;
            }
            if (accessLevel.equals("admin") || accessLevel.equals("moderator") || accessLevel.equals("overseer") || accessLevel.equals("gm") || accessLevel.equals("observer")) {
                playerByUserName.setGodMod(true);
                playerByUserName.setGhostMode(true);
                playerByUserName.setInvisible(true);
            }
            else {
                playerByUserName.setGodMod(false);
                playerByUserName.setGhostMode(false);
                playerByUserName.setInvisible(false);
            }
            GameServer.sendPlayerExtraInfo(playerByUserName, null);
        }
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, accessLevel, s2));
        if (udpConnection == null || !udpConnection.isCoopHost) {
            return ServerWorldDatabase.instance.setAccessLevel(s2, accessLevel);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, accessLevel);
    }
}
