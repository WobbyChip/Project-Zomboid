// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.ArrayList;
import java.util.HashMap;

public class DBResult
{
    private HashMap<String, String> values;
    private ArrayList<String> columns;
    private String type;
    private String tableName;
    
    public DBResult() {
        this.values = new HashMap<String, String>();
        this.columns = new ArrayList<String>();
    }
    
    public HashMap<String, String> getValues() {
        return this.values;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public ArrayList<String> getColumns() {
        return this.columns;
    }
    
    public void setColumns(final ArrayList<String> columns) {
        this.columns = columns;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
}
