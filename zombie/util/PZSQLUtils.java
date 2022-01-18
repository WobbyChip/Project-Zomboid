// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.io.File;
import zombie.core.logger.ExceptionLogger;

public class PZSQLUtils
{
    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException ex) {
            ExceptionLogger.logException(ex);
            System.exit(1);
        }
        setupSqliteVariables();
    }
    
    private static void setupSqliteVariables() {
        if (!System.getProperty("os.name").contains("OS X")) {
            if (System.getProperty("os.name").startsWith("Win")) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    System.setProperty("org.sqlite.lib.path", searchPathForSqliteLib("sqlitejdbc64.dll"));
                    System.setProperty("org.sqlite.lib.name", "sqlitejdbc64.dll");
                }
            }
            else if (System.getProperty("sun.arch.data.model").equals("64")) {}
        }
    }
    
    private static String searchPathForSqliteLib(final String child) {
        for (final String parent : System.getProperty("java.library.path", "").split(File.pathSeparator)) {
            if (new File(parent, child).exists()) {
                return parent;
            }
        }
        return "";
    }
    
    public static Connection getConnection(final String s) throws SQLException {
        return DriverManager.getConnection(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
}
