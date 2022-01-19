// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import se.krka.kahlua.vm.KahluaTable;
import java.util.HashMap;

public class DBSchema
{
    private HashMap<String, HashMap<String, String>> schema;
    private KahluaTable fullTable;
    
    public DBSchema(final Connection connection) {
        this.schema = new HashMap<String, HashMap<String, String>>();
        try {
            final DatabaseMetaData metaData = connection.getMetaData();
            final ResultSet tables = metaData.getTables(null, null, null, new String[] { "TABLE" });
            while (tables.next()) {
                final String string = tables.getString(3);
                if (!string.startsWith("SQLITE_")) {
                    final ResultSet columns = metaData.getColumns(null, null, string, null);
                    final HashMap<String, String> value = new HashMap<String, String>();
                    while (columns.next()) {
                        final String string2 = columns.getString(4);
                        if (!string2.equals("world") && !string2.equals("moderator") && !string2.equals("admin") && !string2.equals("password") && !string2.equals("encryptedPwd") && !string2.equals("pwdEncryptType") && !string2.equals("transactionID")) {
                            value.put(string2, columns.getString(6));
                        }
                    }
                    this.schema.put(string, value);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public KahluaTable getFullTable() {
        return this.fullTable;
    }
    
    public void setFullTable(final KahluaTable fullTable) {
        this.fullTable = fullTable;
    }
    
    public HashMap<String, HashMap<String, String>> getSchema() {
        return this.schema;
    }
}
