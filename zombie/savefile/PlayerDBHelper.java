// 
// Decompiled by Procyon v0.5.36
// 

package zombie.savefile;

import zombie.core.BoxedStaticValues;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import zombie.vehicles.VehicleDBHelper;
import java.sql.Statement;
import java.sql.SQLException;
import zombie.debug.DebugLog;
import zombie.core.logger.ExceptionLogger;
import zombie.util.PZSQLUtils;
import java.io.File;
import zombie.core.Core;
import zombie.ZomboidFileSystem;
import java.sql.Connection;

public final class PlayerDBHelper
{
    public static Connection create() {
        Connection connection = null;
        final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld);
        final File file = new File(pathname);
        if (!file.exists()) {
            file.mkdirs();
        }
        final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pathname, File.separator));
        file2.setReadable(true, false);
        file2.setExecutable(true, false);
        file2.setWritable(true, false);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                connection = PZSQLUtils.getConnection(file2.getAbsolutePath());
                final Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE localPlayers (id   INTEGER PRIMARY KEY NOT NULL,name STRING,wx    INTEGER,wy    INTEGER,x    FLOAT,y    FLOAT,z    FLOAT,worldversion    INTEGER,data BLOB,isDead BOOLEAN);");
                statement.executeUpdate("CREATE TABLE networkPlayers (id   INTEGER PRIMARY KEY NOT NULL,world TEXT,username TEXT,playerIndex   INTEGER,name STRING,x    FLOAT,y    FLOAT,z    FLOAT,worldversion    INTEGER,data BLOB,isDead BOOLEAN);");
                statement.executeUpdate("CREATE INDEX inpusername ON networkPlayers (username);");
                statement.close();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                DebugLog.log("failed to create players database");
                System.exit(1);
            }
        }
        if (connection == null) {
            try {
                connection = PZSQLUtils.getConnection(file2.getAbsolutePath());
            }
            catch (Exception ex2) {
                ExceptionLogger.logException(ex2);
                DebugLog.log("failed to create players database");
                System.exit(1);
            }
        }
        try {
            final Statement statement2 = connection.createStatement();
            statement2.executeQuery("PRAGMA JOURNAL_MODE=TRUNCATE;");
            statement2.close();
        }
        catch (Exception ex3) {
            ExceptionLogger.logException(ex3);
            DebugLog.log("failed to config players.db");
            System.exit(1);
        }
        try {
            connection.setAutoCommit(false);
        }
        catch (SQLException ex4) {
            DebugLog.log("failed to setAutoCommit for players.db");
        }
        return connection;
    }
    
    public static void rollback(final Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        }
        catch (SQLException ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public static boolean isPlayerAlive(final String s, final int n) {
        if (Core.getInstance().isNoSave()) {
            return false;
        }
        if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator)).exists()) {
            return true;
        }
        if (VehicleDBHelper.isPlayerAlive(s, n)) {
            return true;
        }
        if (n == -1) {
            return false;
        }
        try {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator));
            if (!file.exists()) {
                return false;
            }
            file.setReadable(true, false);
            final Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath());
            try {
                final PreparedStatement prepareStatement = connection.prepareStatement("SELECT isDead FROM localPlayers WHERE id=?");
                try {
                    prepareStatement.setInt(1, n);
                    final ResultSet executeQuery = prepareStatement.executeQuery();
                    if (executeQuery.next()) {
                        final boolean b = !executeQuery.getBoolean(1);
                        if (prepareStatement != null) {
                            prepareStatement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                        return b;
                    }
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                }
                catch (Throwable t) {
                    if (prepareStatement != null) {
                        try {
                            prepareStatement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Throwable t2) {
                if (connection != null) {
                    try {
                        connection.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
        }
        return false;
    }
    
    public static ArrayList<Object> getPlayers(final String s) throws SQLException {
        final ArrayList<Boolean> list = new ArrayList<Boolean>();
        if (Core.getInstance().isNoSave()) {
            return (ArrayList<Object>)list;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator));
        if (!file.exists()) {
            return (ArrayList<Object>)list;
        }
        file.setReadable(true, false);
        final Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath());
        try {
            final PreparedStatement prepareStatement = connection.prepareStatement("SELECT id, name, isDead FROM localPlayers");
            try {
                final ResultSet executeQuery = prepareStatement.executeQuery();
                while (executeQuery.next()) {
                    final int int1 = executeQuery.getInt(1);
                    final String string = executeQuery.getString(2);
                    final boolean boolean1 = executeQuery.getBoolean(3);
                    list.add((Boolean)(Object)BoxedStaticValues.toDouble(int1));
                    list.add((Boolean)string);
                    list.add(boolean1 ? Boolean.TRUE : Boolean.FALSE);
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
            }
            catch (Throwable t) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
            if (connection != null) {
                connection.close();
            }
        }
        catch (Throwable t2) {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
            }
            throw t2;
        }
        return (ArrayList<Object>)list;
    }
    
    public static void setPlayer1(final String s, final int n) throws SQLException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        if (n == 1) {
            return;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator));
        if (!file.exists()) {
            return;
        }
        file.setReadable(true, false);
        final Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath());
        try {
            boolean b = false;
            boolean b2 = false;
            int n2 = -1;
            int max = -1;
            final PreparedStatement prepareStatement = connection.prepareStatement("SELECT id FROM localPlayers");
            try {
                final ResultSet executeQuery = prepareStatement.executeQuery();
                while (executeQuery.next()) {
                    final int int1 = executeQuery.getInt(1);
                    if (int1 == 1) {
                        b = true;
                    }
                    else if (n2 == -1 || n2 > int1) {
                        n2 = int1;
                    }
                    if (int1 == n) {
                        b2 = true;
                    }
                    max = Math.max(max, int1);
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
            }
            catch (Throwable t) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
            if (n <= 0) {
                if (!b) {
                    if (connection != null) {
                        connection.close();
                    }
                    return;
                }
                final PreparedStatement prepareStatement2 = connection.prepareStatement("UPDATE localPlayers SET id=? WHERE id=?");
                try {
                    prepareStatement2.setInt(1, max + 1);
                    prepareStatement2.setInt(2, 1);
                    prepareStatement2.executeUpdate();
                    if (prepareStatement2 != null) {
                        prepareStatement2.close();
                    }
                }
                catch (Throwable t2) {
                    if (prepareStatement2 != null) {
                        try {
                            prepareStatement2.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (connection != null) {
                    connection.close();
                }
            }
            else {
                if (!b2) {
                    if (connection != null) {
                        connection.close();
                    }
                    return;
                }
                if (b) {
                    final PreparedStatement prepareStatement3 = connection.prepareStatement("UPDATE localPlayers SET id=? WHERE id=?");
                    try {
                        prepareStatement3.setInt(1, max + 1);
                        prepareStatement3.setInt(2, 1);
                        prepareStatement3.executeUpdate();
                        prepareStatement3.setInt(1, 1);
                        prepareStatement3.setInt(2, n);
                        prepareStatement3.executeUpdate();
                        prepareStatement3.setInt(1, n);
                        prepareStatement3.setInt(2, max + 1);
                        prepareStatement3.executeUpdate();
                        if (prepareStatement3 != null) {
                            prepareStatement3.close();
                        }
                    }
                    catch (Throwable t3) {
                        if (prepareStatement3 != null) {
                            try {
                                prepareStatement3.close();
                            }
                            catch (Throwable exception3) {
                                t3.addSuppressed(exception3);
                            }
                        }
                        throw t3;
                    }
                }
                else {
                    final PreparedStatement prepareStatement4 = connection.prepareStatement("UPDATE localPlayers SET id=? WHERE id=?");
                    try {
                        prepareStatement4.setInt(1, 1);
                        prepareStatement4.setInt(2, n);
                        prepareStatement4.executeUpdate();
                        if (prepareStatement4 != null) {
                            prepareStatement4.close();
                        }
                    }
                    catch (Throwable t4) {
                        if (prepareStatement4 != null) {
                            try {
                                prepareStatement4.close();
                            }
                            catch (Throwable exception4) {
                                t4.addSuppressed(exception4);
                            }
                        }
                        throw t4;
                    }
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }
        catch (Throwable t5) {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Throwable exception5) {
                    t5.addSuppressed(exception5);
                }
            }
            throw t5;
        }
    }
}
