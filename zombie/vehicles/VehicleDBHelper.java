// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import zombie.debug.DebugLog;
import zombie.util.PZSQLUtils;
import java.io.File;

public final class VehicleDBHelper
{
    public static boolean isPlayerAlive(final String s, final int n) {
        if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator)).exists()) {
            return true;
        }
        if (n == -1) {
            return false;
        }
        Connection connection = null;
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator));
        file.setReadable(true, false);
        if (!file.exists()) {
            return false;
        }
        try {
            connection = PZSQLUtils.getConnection(file.getAbsolutePath());
        }
        catch (Exception ex2) {
            DebugLog.log("failed to create vehicles database");
            System.exit(1);
        }
        boolean b = false;
        final String s2 = "SELECT isDead FROM localPlayers WHERE id=?";
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement(s2);
            prepareStatement.setInt(1, n);
            final ResultSet executeQuery = prepareStatement.executeQuery();
            if (executeQuery.next()) {
                b = !executeQuery.getBoolean(1);
            }
        }
        catch (SQLException ex3) {
            return false;
        }
        finally {
            try {
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
                connection.close();
            }
            catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return b;
    }
}
