// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import zombie.core.secure.PZcrypt;
import java.util.Scanner;
import java.io.InputStreamReader;
import zombie.core.znet.SteamUtils;
import zombie.util.PZSQLUtils;
import zombie.debug.DebugLog;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import java.util.Iterator;
import java.util.HashMap;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.sql.PreparedStatement;
import se.krka.kahlua.vm.KahluaTable;
import java.sql.Connection;
import java.nio.charset.CharsetEncoder;

public class ServerWorldDatabase
{
    public static ServerWorldDatabase instance;
    public String CommandLineAdminUsername;
    public String CommandLineAdminPassword;
    public boolean doAdmin;
    public DBSchema dbSchema;
    static CharsetEncoder asciiEncoder;
    Connection conn;
    private static final String nullChar;
    
    public ServerWorldDatabase() {
        this.CommandLineAdminUsername = "admin";
        this.doAdmin = true;
        this.dbSchema = null;
    }
    
    public DBSchema getDBSchema() {
        if (this.dbSchema == null) {
            this.dbSchema = new DBSchema(this.conn);
        }
        return this.dbSchema;
    }
    
    public void executeQuery(final String s, final KahluaTable kahluaTable) throws SQLException {
        final PreparedStatement prepareStatement = this.conn.prepareStatement(s);
        final KahluaTableIterator iterator = kahluaTable.iterator();
        int n = 1;
        while (iterator.advance()) {
            prepareStatement.setString(n++, (String)iterator.getValue());
        }
        prepareStatement.executeUpdate();
    }
    
    public ArrayList<DBResult> getTableResult(final String s) throws SQLException {
        final ArrayList<DBResult> list = new ArrayList<DBResult>();
        final PreparedStatement prepareStatement = this.conn.prepareStatement(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        final ResultSet executeQuery = prepareStatement.executeQuery();
        final ResultSet columns = this.conn.getMetaData().getColumns(null, null, s, null);
        final ArrayList<String> list2 = new ArrayList<String>();
        DBResult e = new DBResult();
        while (columns.next()) {
            final String string = columns.getString(4);
            if (!string.equals("world") && !string.equals("moderator") && !string.equals("admin") && !string.equals("password") && !string.equals("encryptedPwd") && !string.equals("pwdEncryptType") && !string.equals("transactionID")) {
                list2.add(string);
            }
        }
        e.setColumns(list2);
        e.setTableName(s);
        while (executeQuery.next()) {
            for (int i = 0; i < list2.size(); ++i) {
                final String key = list2.get(i);
                String string2 = executeQuery.getString(key);
                if ("'false'".equals(string2)) {
                    string2 = "false";
                }
                if ("'true'".equals(string2)) {
                    string2 = "true";
                }
                if (string2 == null) {
                    string2 = "";
                }
                e.getValues().put(key, string2);
            }
            list.add(e);
            e = new DBResult();
            e.setColumns(list2);
            e.setTableName(s);
        }
        prepareStatement.close();
        return list;
    }
    
    public void saveAllTransactionsID(final HashMap<String, Integer> hashMap) {
        try {
            for (final String key : hashMap.keySet()) {
                final Integer n = hashMap.get(key);
                final PreparedStatement prepareStatement = this.conn.prepareStatement("UPDATE whitelist SET transactionID = ? WHERE username = ?");
                prepareStatement.setString(1, n.toString());
                prepareStatement.setString(2, key);
                prepareStatement.executeUpdate();
                prepareStatement.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveTransactionID(final String s, final Integer n) {
        try {
            if (!this.containsUser(s)) {
                this.addUser(s, "");
            }
            final PreparedStatement prepareStatement = this.conn.prepareStatement("UPDATE whitelist SET transactionID = ? WHERE username = ?");
            prepareStatement.setString(1, n.toString());
            prepareStatement.setString(2, s);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean containsUser(final String s) {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, Core.GameSaveWorld);
            if (prepareStatement.executeQuery().next()) {
                prepareStatement.close();
                return true;
            }
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean containsCaseinsensitiveUser(final String s) {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE LOWER(username) = LOWER(?) AND world = ?");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, Core.GameSaveWorld);
            if (prepareStatement.executeQuery().next()) {
                prepareStatement.close();
                return true;
            }
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public String changeUsername(final String s, final String s2) throws SQLException {
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
        prepareStatement.setString(1, s);
        prepareStatement.setString(2, Core.GameSaveWorld);
        final ResultSet executeQuery = prepareStatement.executeQuery();
        if (executeQuery.next()) {
            final String string = executeQuery.getString("id");
            prepareStatement.close();
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE whitelist SET username = ? WHERE id = ?");
            prepareStatement2.setString(1, s2);
            prepareStatement2.setString(2, string);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
        }
        if (!ServerOptions.instance.getBoolean("Open")) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
    }
    
    public String addUser(final String s, final String s2) throws SQLException {
        if (this.containsCaseinsensitiveUser(s)) {
            return "A user with this name already exists";
        }
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, Core.GameSaveWorld);
            if (prepareStatement.executeQuery().next()) {
                prepareStatement.close();
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            prepareStatement.close();
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("INSERT INTO whitelist (world, username, password, encryptedPwd, pwdEncryptType) VALUES (?, ?, ?, 'true', '2')");
            prepareStatement2.setString(1, Core.GameSaveWorld);
            prepareStatement2.setString(2, s);
            prepareStatement2.setString(3, s2);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
    }
    
    public void updateDisplayName(final String s, final String s2) {
        try {
            PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
            preparedStatement.setString(1, s);
            preparedStatement.setString(2, Core.GameSaveWorld);
            if (preparedStatement.executeQuery().next()) {
                preparedStatement.close();
                preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET displayName = ? WHERE username = ?");
                preparedStatement.setString(1, s2);
                preparedStatement.setString(2, s);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            preparedStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getDisplayName(final String s) {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, Core.GameSaveWorld);
            final ResultSet executeQuery = prepareStatement.executeQuery();
            if (executeQuery.next()) {
                final String string = executeQuery.getString("displayName");
                prepareStatement.close();
                return string;
            }
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public String removeUser(final String s) throws SQLException {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("DELETE FROM whitelist WHERE world = ? and username = ?");
            prepareStatement.setString(1, Core.GameSaveWorld);
            prepareStatement.setString(2, s);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public void removeUserLog(final String s, final String s2, final String s3) throws SQLException {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("DELETE FROM userlog WHERE username = ? AND type = ? AND text = ?");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, s2);
            prepareStatement.setString(3, s3);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void create() throws SQLException, ClassNotFoundException {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            file.mkdirs();
        }
        final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, GameServer.ServerName));
        file2.setReadable(true, false);
        file2.setExecutable(true, false);
        file2.setWritable(true, false);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file2.getPath()));
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                this.conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
                final Statement statement = this.conn.createStatement();
                statement.executeUpdate(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameServer.ServerName));
                statement.executeUpdate("CREATE UNIQUE INDEX [id] ON [whitelist]([id]  ASC)");
                statement.executeUpdate("CREATE UNIQUE INDEX [username] ON [whitelist]([username]  ASC)");
                statement.executeUpdate("CREATE TABLE [bannedip] ([ip] TEXT NOT NULL,[username] TEXT NULL, [reason] TEXT NULL)");
                statement.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                DebugLog.log("failed to create user database, server shut down");
                System.exit(1);
            }
        }
        if (this.conn == null) {
            try {
                this.conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
                DebugLog.log("failed to open user database, server shut down");
                System.exit(1);
            }
        }
        final DatabaseMetaData metaData = this.conn.getMetaData();
        final ResultSet columns = metaData.getColumns(null, null, "whitelist", "admin");
        final Statement statement2 = this.conn.createStatement();
        if (!columns.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'admin' BOOLEAN NULL DEFAULT false");
        }
        columns.close();
        final ResultSet columns2 = metaData.getColumns(null, null, "whitelist", "moderator");
        if (!columns2.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'moderator' BOOLEAN NULL DEFAULT false");
        }
        columns2.close();
        final ResultSet columns3 = metaData.getColumns(null, null, "whitelist", "banned");
        if (!columns3.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'banned' BOOLEAN NULL DEFAULT false");
        }
        columns3.close();
        final ResultSet columns4 = metaData.getColumns(null, null, "whitelist", "lastConnection");
        if (!columns4.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'lastConnection' TEXT NULL");
        }
        columns4.close();
        final ResultSet columns5 = metaData.getColumns(null, null, "whitelist", "encryptedPwd");
        if (!columns5.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'encryptedPwd' BOOLEAN NULL DEFAULT false");
        }
        columns5.close();
        final ResultSet columns6 = metaData.getColumns(null, null, "whitelist", "pwdEncryptType");
        if (!columns6.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'pwdEncryptType' INTEGER NULL DEFAULT 1");
        }
        columns6.close();
        if (SteamUtils.isSteamModeEnabled()) {
            final ResultSet columns7 = metaData.getColumns(null, null, "whitelist", "steamid");
            if (!columns7.next()) {
                statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'steamid' TEXT NULL");
            }
            columns7.close();
            final ResultSet columns8 = metaData.getColumns(null, null, "whitelist", "ownerid");
            if (!columns8.next()) {
                statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'ownerid' TEXT NULL");
            }
            columns8.close();
        }
        final ResultSet columns9 = metaData.getColumns(null, null, "whitelist", "accesslevel");
        if (!columns9.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'accesslevel' TEXT NULL");
        }
        columns9.close();
        final ResultSet columns10 = metaData.getColumns(null, null, "whitelist", "transactionID");
        if (!columns10.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'transactionID' INTEGER NULL");
        }
        columns10.close();
        final ResultSet columns11 = metaData.getColumns(null, null, "whitelist", "displayName");
        if (!columns11.next()) {
            statement2.executeUpdate("ALTER TABLE 'whitelist' ADD 'displayName' TEXT NULL");
        }
        columns11.close();
        if (!statement2.executeQuery("SELECT * FROM sqlite_master WHERE type = 'index' AND sql LIKE '%UNIQUE%' and name = 'username'").next()) {
            try {
                statement2.executeUpdate("CREATE UNIQUE INDEX [username] ON [whitelist]([username]  ASC)");
            }
            catch (Exception ex3) {
                System.out.println("Can't create the username index because some of the username in the database are in double, will drop the double username.");
                statement2.executeUpdate("DELETE FROM whitelist WHERE whitelist.rowid > (SELECT rowid FROM whitelist dbl WHERE whitelist.rowid <> dbl.rowid AND  whitelist.username = dbl.username);");
                statement2.executeUpdate("CREATE UNIQUE INDEX [username] ON [whitelist]([username]  ASC)");
            }
        }
        final ResultSet tables = metaData.getTables(null, null, "bannedip", null);
        if (!tables.next()) {
            statement2.executeUpdate("CREATE TABLE [bannedip] ([ip] TEXT NOT NULL,[username] TEXT NULL, [reason] TEXT NULL)");
        }
        tables.close();
        final ResultSet tables2 = metaData.getTables(null, null, "bannedid", null);
        if (!tables2.next()) {
            statement2.executeUpdate("CREATE TABLE [bannedid] ([steamid] TEXT NOT NULL, [reason] TEXT NULL)");
        }
        tables2.close();
        final ResultSet tables3 = metaData.getTables(null, null, "userlog", null);
        if (!tables3.next()) {
            statement2.executeUpdate("CREATE TABLE [userlog] ([id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[username] TEXT  NULL,[type] TEXT  NULL, [text] TEXT  NULL, [issuedBy] TEXT  NULL, [amount] INTEGER NULL)");
        }
        tables3.close();
        final ResultSet columns12 = metaData.getColumns(null, null, "whitelist", "moderator");
        if (columns12.next()) {}
        columns12.close();
        final ResultSet columns13 = metaData.getColumns(null, null, "whitelist", "admin");
        if (columns13.next()) {
            columns13.close();
            final ResultSet executeQuery = this.conn.prepareStatement("SELECT * FROM whitelist where admin = 'true'").executeQuery();
            while (executeQuery.next()) {
                final PreparedStatement prepareStatement = this.conn.prepareStatement("UPDATE whitelist set accesslevel = 'admin' where id = ?");
                prepareStatement.setString(1, executeQuery.getString("id"));
                System.out.println(executeQuery.getString("username"));
                prepareStatement.executeUpdate();
            }
        }
        final ResultSet tables4 = metaData.getTables(null, null, "tickets", null);
        if (!tables4.next()) {
            statement2.executeUpdate("CREATE TABLE [tickets] ([id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, [message] TEXT NOT NULL, [author] TEXT NOT NULL,[answeredID] INTEGER,[viewed] BOOLEAN NULL DEFAULT false)");
        }
        tables4.close();
        final PreparedStatement prepareStatement2 = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ?");
        prepareStatement2.setString(1, this.CommandLineAdminUsername);
        if (!prepareStatement2.executeQuery().next()) {
            prepareStatement2.close();
            String anObject = this.CommandLineAdminPassword;
            if (anObject == null || anObject.isEmpty()) {
                final Scanner scanner = new Scanner(new InputStreamReader(System.in));
                System.out.println("User 'admin' not found, creating it ");
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.CommandLineAdminPassword));
                System.out.println("Enter new administrator password: ");
                for (anObject = scanner.nextLine(); anObject == null || "".equals(anObject); anObject = scanner.nextLine()) {
                    System.out.println("Enter new administrator password: ");
                }
                System.out.println("Confirm the password: ");
                for (String s = scanner.nextLine(); s == null || "".equals(s) || !anObject.equals(s); s = scanner.nextLine()) {
                    System.out.println("Wrong password, confirm the password: ");
                }
            }
            PreparedStatement preparedStatement;
            if (this.doAdmin) {
                preparedStatement = this.conn.prepareStatement("INSERT INTO whitelist (username, password, accesslevel, encryptedPwd, pwdEncryptType) VALUES (?, ?, 'admin', 'true', '2')");
            }
            else {
                preparedStatement = this.conn.prepareStatement("INSERT INTO whitelist (username, password, encryptedPwd, pwdEncryptType) VALUES (?, ?, 'true', '2')");
            }
            preparedStatement.setString(1, this.CommandLineAdminUsername);
            preparedStatement.setString(2, PZcrypt.hash(encrypt(anObject)));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.CommandLineAdminUsername));
        }
        else {
            prepareStatement2.close();
        }
        statement2.close();
        if (this.CommandLineAdminPassword != null && !this.CommandLineAdminPassword.isEmpty()) {
            final String hash = PZcrypt.hash(encrypt(this.CommandLineAdminPassword));
            PreparedStatement preparedStatement2 = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ?");
            preparedStatement2.setString(1, this.CommandLineAdminUsername);
            if (preparedStatement2.executeQuery().next()) {
                preparedStatement2.close();
                preparedStatement2 = this.conn.prepareStatement("UPDATE whitelist SET password = ? WHERE username = ?");
                preparedStatement2.setString(1, hash);
                preparedStatement2.setString(2, this.CommandLineAdminUsername);
                preparedStatement2.executeUpdate();
                System.out.println("admin password changed via -adminpassword option");
            }
            else {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.CommandLineAdminUsername));
            }
            preparedStatement2.close();
        }
    }
    
    public void close() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static boolean isValidUserName(final String s) {
        return s != null && !s.trim().isEmpty() && !s.contains(";") && !s.contains("@") && !s.contains("$") && !s.contains(",") && !s.contains("/") && !s.contains(".") && !s.contains("'") && !s.contains("?") && !s.contains("\"") && s.trim().length() >= 3 && s.length() <= 20 && !s.contains(ServerWorldDatabase.nullChar) && (s.trim().equals("admin") || !s.trim().toLowerCase().startsWith("admin"));
    }
    
    public LogonResult authClient(final String cs, final String anObject, final String s, final long n) {
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, cs));
        final LogonResult logonResult = new LogonResult();
        if (!ServerOptions.instance.AllowNonAsciiUsername.getValue() && !ServerWorldDatabase.asciiEncoder.canEncode(cs)) {
            logonResult.bAuthorized = false;
            logonResult.dcReason = "NonAsciiCharacters";
            return logonResult;
        }
        if (!isValidUserName(cs)) {
            logonResult.bAuthorized = false;
            logonResult.dcReason = "InvalidUsername";
            return logonResult;
        }
        try {
            if (!SteamUtils.isSteamModeEnabled() && !s.equals("127.0.0.1")) {
                final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM bannedip WHERE ip = ?");
                prepareStatement.setString(1, s);
                final ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    logonResult.bAuthorized = false;
                    logonResult.bannedReason = executeQuery.getString("reason");
                    logonResult.banned = true;
                    prepareStatement.close();
                    return logonResult;
                }
                prepareStatement.close();
            }
            if (isNullOrEmpty(anObject) && ServerOptions.instance.Open.getValue() && ServerOptions.instance.AutoCreateUserInWhiteList.getValue()) {
                logonResult.dcReason = "UserPasswordRequired";
                logonResult.bAuthorized = false;
                return logonResult;
            }
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("SELECT * FROM whitelist WHERE LOWER(username) = LOWER(?) AND world = ?");
            prepareStatement2.setString(1, cs);
            prepareStatement2.setString(2, Core.GameSaveWorld);
            ResultSet set = prepareStatement2.executeQuery();
            if (set.next()) {
                if (!isNullOrEmpty(set.getString("password")) && (set.getString("encryptedPwd").equals("false") || set.getString("encryptedPwd").equals("N"))) {
                    final String string = set.getString("password");
                    final String encrypt = encrypt(string);
                    final PreparedStatement prepareStatement3 = this.conn.prepareStatement("UPDATE whitelist SET encryptedPwd = 'true' WHERE username = ? and password = ?");
                    prepareStatement3.setString(1, cs);
                    prepareStatement3.setString(2, string);
                    prepareStatement3.executeUpdate();
                    prepareStatement3.close();
                    final PreparedStatement prepareStatement4 = this.conn.prepareStatement("UPDATE whitelist SET password = ? WHERE username = ? AND password = ?");
                    prepareStatement4.setString(1, encrypt);
                    prepareStatement4.setString(2, cs);
                    prepareStatement4.setString(3, string);
                    prepareStatement4.executeUpdate();
                    prepareStatement4.close();
                    set = prepareStatement2.executeQuery();
                }
                if (!isNullOrEmpty(set.getString("password")) && set.getInt("pwdEncryptType") == 1) {
                    final String string2 = set.getString("password");
                    final String hash = PZcrypt.hash(string2);
                    final PreparedStatement prepareStatement5 = this.conn.prepareStatement("UPDATE whitelist SET pwdEncryptType = '2', password = ? WHERE username = ? AND password = ?");
                    prepareStatement5.setString(1, hash);
                    prepareStatement5.setString(2, cs);
                    prepareStatement5.setString(3, string2);
                    prepareStatement5.executeUpdate();
                    prepareStatement5.close();
                    set = prepareStatement2.executeQuery();
                }
                if (!isNullOrEmpty(set.getString("password")) && !set.getString("password").equals(anObject)) {
                    logonResult.bAuthorized = false;
                    prepareStatement2.close();
                    if (isNullOrEmpty(anObject)) {
                        logonResult.dcReason = "DuplicateAccount";
                    }
                    else {
                        logonResult.dcReason = "InvalidUsernamePassword";
                    }
                    return logonResult;
                }
                logonResult.bAuthorized = true;
                logonResult.admin = ("true".equals(set.getString("admin")) || "Y".equals(set.getString("admin")));
                logonResult.accessLevel = set.getString("accesslevel");
                if (logonResult.accessLevel == null) {
                    logonResult.accessLevel = "";
                    if (logonResult.admin) {
                        logonResult.accessLevel = "admin";
                    }
                    this.setAccessLevel(cs, logonResult.accessLevel);
                }
                logonResult.banned = ("true".equals(set.getString("banned")) || "Y".equals(set.getString("banned")));
                if (logonResult.banned) {
                    logonResult.bAuthorized = false;
                }
                if (set.getString("transactionID") == null) {
                    logonResult.transactionID = 0;
                }
                else {
                    logonResult.transactionID = Integer.parseInt(set.getString("transactionID"));
                }
                prepareStatement2.close();
                return logonResult;
            }
            else if (ServerOptions.instance.Open.getValue()) {
                if (!this.isNewAccountAllowed(s, n)) {
                    prepareStatement2.close();
                    logonResult.bAuthorized = false;
                    logonResult.dcReason = "MaxAccountsReached";
                    return logonResult;
                }
                logonResult.bAuthorized = true;
                prepareStatement2.close();
                return logonResult;
            }
            else {
                logonResult.bAuthorized = false;
                logonResult.dcReason = "UnknownUsername";
                prepareStatement2.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return logonResult;
    }
    
    public LogonResult authClient(final long n) {
        final String convertSteamIDToString = SteamUtils.convertSteamIDToString(n);
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, convertSteamIDToString));
        final LogonResult logonResult = new LogonResult();
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM bannedid WHERE steamid = ?");
            prepareStatement.setString(1, convertSteamIDToString);
            final ResultSet executeQuery = prepareStatement.executeQuery();
            if (executeQuery.next()) {
                logonResult.bAuthorized = false;
                logonResult.bannedReason = executeQuery.getString("reason");
                logonResult.banned = true;
                prepareStatement.close();
                return logonResult;
            }
            prepareStatement.close();
            logonResult.bAuthorized = true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return logonResult;
    }
    
    public LogonResult authOwner(final long n, final long n2) {
        final String convertSteamIDToString = SteamUtils.convertSteamIDToString(n);
        final String convertSteamIDToString2 = SteamUtils.convertSteamIDToString(n2);
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, convertSteamIDToString, convertSteamIDToString2));
        final LogonResult logonResult = new LogonResult();
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM bannedid WHERE steamid = ?");
            prepareStatement.setString(1, convertSteamIDToString2);
            final ResultSet executeQuery = prepareStatement.executeQuery();
            if (executeQuery.next()) {
                logonResult.bAuthorized = false;
                logonResult.bannedReason = executeQuery.getString("reason");
                logonResult.banned = true;
                prepareStatement.close();
                return logonResult;
            }
            prepareStatement.close();
            logonResult.bAuthorized = true;
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE whitelist SET ownerid = ? where steamid = ?");
            prepareStatement2.setString(1, convertSteamIDToString2);
            prepareStatement2.setString(2, convertSteamIDToString);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return logonResult;
    }
    
    private boolean isNewAccountAllowed(final String s, final long n) {
        final int value = ServerOptions.instance.MaxAccountsPerUser.getValue();
        if (value <= 0) {
            return true;
        }
        if (!SteamUtils.isSteamModeEnabled()) {
            return true;
        }
        final String convertSteamIDToString = SteamUtils.convertSteamIDToString(n);
        int n2 = 0;
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE steamid = ? AND accessLevel = ?");
            try {
                prepareStatement.setString(1, convertSteamIDToString);
                prepareStatement.setString(2, "");
                while (prepareStatement.executeQuery().next()) {
                    ++n2;
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
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
        return n2 < value;
    }
    
    public static String encrypt(final String s) {
        if (isNullOrEmpty(s)) {
            return "";
        }
        byte[] digest = null;
        try {
            digest = MessageDigest.getInstance("MD5").digest(s.getBytes());
        }
        catch (NoSuchAlgorithmException ex) {
            System.out.println("Can't encrypt password");
            ex.printStackTrace();
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; ++i) {
            final String hexString = Integer.toHexString(digest[i]);
            if (hexString.length() == 1) {
                sb.append('0');
                sb.append(hexString.charAt(hexString.length() - 1));
            }
            else {
                sb.append(hexString.substring(hexString.length() - 2));
            }
        }
        return sb.toString();
    }
    
    public String changePwd(final String s, String s2, final String s3) throws SQLException {
        s2 = s2;
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND password = ? AND world = ?");
        prepareStatement.setString(1, s);
        prepareStatement.setString(2, s2);
        prepareStatement.setString(3, Core.GameSaveWorld);
        if (prepareStatement.executeQuery().next()) {
            prepareStatement.close();
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE whitelist SET pwdEncryptType = '2', password = ? WHERE username = ? and password = ?");
            prepareStatement2.setString(1, s3);
            prepareStatement2.setString(2, s);
            prepareStatement2.setString(3, s2);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
        }
        prepareStatement.close();
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public String grantAdmin(final String s, final boolean b) throws SQLException {
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
        prepareStatement.setString(1, s);
        prepareStatement.setString(2, Core.GameSaveWorld);
        if (!prepareStatement.executeQuery().next()) {
            prepareStatement.close();
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        prepareStatement.close();
        final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE whitelist SET admin = ? WHERE username = ?");
        prepareStatement2.setString(1, b ? "true" : "false");
        prepareStatement2.setString(2, s);
        prepareStatement2.executeUpdate();
        prepareStatement2.close();
        if (b) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public String setAccessLevel(final String s, String trim) throws SQLException {
        trim = trim.trim();
        if (!this.containsUser(s)) {
            this.addUser(s, "");
        }
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
        prepareStatement.setString(1, s);
        prepareStatement.setString(2, Core.GameSaveWorld);
        if (!prepareStatement.executeQuery().next()) {
            prepareStatement.close();
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        prepareStatement.close();
        final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE whitelist SET accesslevel = ? WHERE username = ?");
        prepareStatement2.setString(1, trim);
        prepareStatement2.setString(2, s);
        prepareStatement2.executeUpdate();
        prepareStatement2.close();
        if (trim.equals("")) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim);
    }
    
    public ArrayList<Userlog> getUserlog(final String s) {
        final ArrayList<Userlog> list = new ArrayList<Userlog>();
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM userlog WHERE username = ?");
            prepareStatement.setString(1, s);
            final ResultSet executeQuery = prepareStatement.executeQuery();
            while (executeQuery.next()) {
                list.add(new Userlog(s, executeQuery.getString("type"), executeQuery.getString("text"), executeQuery.getString("issuedBy"), executeQuery.getInt("amount")));
            }
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public void addUserlog(final String s, final Userlog.UserlogType userlogType, final String s2, final String s3, int n) {
        try {
            boolean b = true;
            if (userlogType == Userlog.UserlogType.LuaChecksum || userlogType == Userlog.UserlogType.DupeItem) {
                final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM userlog WHERE username = ? AND type = ?");
                prepareStatement.setString(1, s);
                prepareStatement.setString(2, userlogType.toString());
                final ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    b = false;
                    n = Integer.parseInt(executeQuery.getString("amount")) + 1;
                    prepareStatement.close();
                    final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE userlog set amount = ? WHERE username = ? AND type = ?");
                    prepareStatement2.setString(1, new Integer(n).toString());
                    prepareStatement2.setString(2, s);
                    prepareStatement2.setString(3, userlogType.toString());
                    prepareStatement2.executeUpdate();
                    prepareStatement2.close();
                }
            }
            if (b) {
                final PreparedStatement prepareStatement3 = this.conn.prepareStatement("INSERT INTO userlog (username, type, text, issuedBy, amount) VALUES (?, ?, ?, ?, ?)");
                prepareStatement3.setString(1, s);
                prepareStatement3.setString(2, userlogType.toString());
                prepareStatement3.setString(3, s2);
                prepareStatement3.setString(4, s3);
                prepareStatement3.setString(5, new Integer(n).toString());
                prepareStatement3.executeUpdate();
                prepareStatement3.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public String banUser(final String s, final boolean b) throws SQLException {
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
        prepareStatement.setString(1, s);
        prepareStatement.setString(2, Core.GameSaveWorld);
        int next = prepareStatement.executeQuery().next() ? 1 : 0;
        if (b && next == 0) {
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("INSERT INTO whitelist (world, username, password, encryptedPwd) VALUES (?, ?, 'bogus', 'false')");
            prepareStatement2.setString(1, Core.GameSaveWorld);
            prepareStatement2.setString(2, s);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
            prepareStatement.executeQuery();
            next = 1;
        }
        if (next == 0) {
            prepareStatement.close();
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        String s2 = "true";
        if (!b) {
            s2 = "false";
        }
        prepareStatement.close();
        final PreparedStatement prepareStatement3 = this.conn.prepareStatement("UPDATE whitelist SET banned = ? WHERE username = ?");
        prepareStatement3.setString(1, s2);
        prepareStatement3.setString(2, s);
        prepareStatement3.executeUpdate();
        prepareStatement3.close();
        if (SteamUtils.isSteamModeEnabled()) {
            final PreparedStatement prepareStatement4 = this.conn.prepareStatement("SELECT steamid FROM whitelist WHERE username = ? AND world = ?");
            prepareStatement4.setString(1, s);
            prepareStatement4.setString(2, Core.GameSaveWorld);
            final ResultSet executeQuery = prepareStatement4.executeQuery();
            if (executeQuery.next()) {
                final String string = executeQuery.getString("steamid");
                prepareStatement4.close();
                if (string != null && !string.isEmpty()) {
                    this.banSteamID(string, "", b);
                }
            }
            else {
                prepareStatement4.close();
            }
        }
        if (b) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public String banIp(final String s, final String s2, final String s3, final boolean b) throws SQLException {
        if (b) {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("INSERT INTO bannedip (ip, username, reason) VALUES (?, ?, ?)");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, s2);
            prepareStatement.setString(3, s3);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        else {
            if (s != null) {
                final PreparedStatement prepareStatement2 = this.conn.prepareStatement("DELETE FROM bannedip WHERE ip = ?");
                prepareStatement2.setString(1, s);
                prepareStatement2.executeUpdate();
                prepareStatement2.close();
            }
            final PreparedStatement prepareStatement3 = this.conn.prepareStatement("DELETE FROM bannedip WHERE username = ?");
            prepareStatement3.setString(1, s2);
            prepareStatement3.executeUpdate();
            prepareStatement3.close();
        }
        return "";
    }
    
    public String banSteamID(final String s, final String s2, final boolean b) throws SQLException {
        if (b) {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("INSERT INTO bannedid (steamid, reason) VALUES (?, ?)");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, s2);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        else {
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("DELETE FROM bannedid WHERE steamid = ?");
            prepareStatement2.setString(1, s);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
        }
        return "";
    }
    
    public String setUserSteamID(final String s, final String s2) {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ?");
            prepareStatement.setString(1, s);
            if (!prepareStatement.executeQuery().next()) {
                prepareStatement.close();
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            prepareStatement.close();
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("UPDATE whitelist SET steamid = ? WHERE username = ?");
            prepareStatement2.setString(1, s2);
            prepareStatement2.setString(2, s);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
    }
    
    public void setPassword(final String s, final String s2) throws SQLException {
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("UPDATE whitelist SET pwdEncryptType = '2', password = ? WHERE username = ? and world = ?");
            prepareStatement.setString(1, s2);
            prepareStatement.setString(2, s);
            prepareStatement.setString(3, Core.GameSaveWorld);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateLastConnectionDate(final String s, final String s2) {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final PreparedStatement prepareStatement = this.conn.prepareStatement("UPDATE whitelist SET lastConnection = ? WHERE username = ? AND password = ?");
            prepareStatement.setString(1, simpleDateFormat.format(Calendar.getInstance().getTime()));
            prepareStatement.setString(2, s);
            prepareStatement.setString(3, s2);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty();
    }
    
    public String addWarningPoint(final String s, final String s2, final int n, final String s3) throws SQLException {
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
        prepareStatement.setString(1, s);
        prepareStatement.setString(2, Core.GameSaveWorld);
        if (prepareStatement.executeQuery().next()) {
            this.addUserlog(s, Userlog.UserlogType.WarningPoint, s2, s3, n);
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public void addTicket(final String s, final String s2, final int n) throws SQLException {
        if (n > -1) {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("INSERT INTO tickets (author, message, answeredID) VALUES (?, ?, ?)");
            prepareStatement.setString(1, s);
            prepareStatement.setString(2, s2);
            prepareStatement.setInt(3, n);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        else {
            final PreparedStatement prepareStatement2 = this.conn.prepareStatement("INSERT INTO tickets (author, message) VALUES (?, ?)");
            prepareStatement2.setString(1, s);
            prepareStatement2.setString(2, s2);
            prepareStatement2.executeUpdate();
            prepareStatement2.close();
        }
    }
    
    public ArrayList<DBTicket> getTickets(final String s) throws SQLException {
        final ArrayList<DBTicket> list = new ArrayList<DBTicket>();
        PreparedStatement preparedStatement;
        if (s != null) {
            preparedStatement = this.conn.prepareStatement("SELECT * FROM tickets WHERE author = ? and answeredID is null");
            preparedStatement.setString(1, s);
        }
        else {
            preparedStatement = this.conn.prepareStatement("SELECT * FROM tickets where answeredID is null");
        }
        final ResultSet executeQuery = preparedStatement.executeQuery();
        while (executeQuery.next()) {
            final DBTicket e = new DBTicket(executeQuery.getString("author"), executeQuery.getString("message"), executeQuery.getInt("id"));
            list.add(e);
            final DBTicket answer = this.getAnswer(e.getTicketID());
            if (answer != null) {
                e.setAnswer(answer);
            }
        }
        return list;
    }
    
    private DBTicket getAnswer(final int n) throws SQLException {
        final PreparedStatement prepareStatement = this.conn.prepareStatement("SELECT * FROM tickets WHERE answeredID = ?");
        prepareStatement.setInt(1, n);
        final ResultSet executeQuery = prepareStatement.executeQuery();
        if (executeQuery.next()) {
            return new DBTicket(executeQuery.getString("author"), executeQuery.getString("message"), executeQuery.getInt("id"));
        }
        return null;
    }
    
    public void removeTicket(final int n) throws SQLException {
        final DBTicket answer = this.getAnswer(n);
        if (answer != null) {
            final PreparedStatement prepareStatement = this.conn.prepareStatement("DELETE FROM tickets WHERE id = ?");
            prepareStatement.setInt(1, answer.getTicketID());
            prepareStatement.executeUpdate();
            prepareStatement.close();
        }
        final PreparedStatement prepareStatement2 = this.conn.prepareStatement("DELETE FROM tickets WHERE id = ?");
        prepareStatement2.setInt(1, n);
        prepareStatement2.executeUpdate();
        prepareStatement2.close();
    }
    
    static {
        ServerWorldDatabase.instance = new ServerWorldDatabase();
        ServerWorldDatabase.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
        nullChar = String.valueOf('\0');
    }
    
    public class LogonResult
    {
        public boolean bAuthorized;
        public int x;
        public int y;
        public int z;
        public boolean admin;
        public boolean banned;
        public String bannedReason;
        public String dcReason;
        public String accessLevel;
        public int transactionID;
        
        public LogonResult() {
            this.bAuthorized = false;
            this.admin = false;
            this.banned = false;
            this.bannedReason = null;
            this.dcReason = null;
            this.accessLevel = "";
            this.transactionID = 0;
        }
    }
}
