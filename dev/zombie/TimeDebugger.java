// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.debug.DebugLog;
import zombie.network.GameServer;
import java.util.ArrayList;

public class TimeDebugger
{
    ArrayList<Long> records;
    ArrayList<String> recordStrings;
    String name;
    
    public TimeDebugger(final String name) {
        this.records = new ArrayList<Long>();
        this.recordStrings = new ArrayList<String>();
        this.name = "";
        this.name = name;
    }
    
    public void clear() {
        if (GameServer.bServer) {
            this.records.clear();
            this.recordStrings.clear();
        }
    }
    
    public void start() {
        if (GameServer.bServer) {
            this.records.clear();
            this.recordStrings.clear();
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add("Start");
        }
    }
    
    public void record() {
        if (GameServer.bServer) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add(String.valueOf(this.records.size()));
        }
    }
    
    public void record(final String e) {
        if (GameServer.bServer) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add(e);
        }
    }
    
    public void recordTO(final String e, final int n) {
        if (GameServer.bServer && this.records.get(this.records.size() - 1) - this.records.get(this.records.size() - 2) > n) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add(e);
        }
    }
    
    public void add(final TimeDebugger timeDebugger) {
        if (GameServer.bServer) {
            final String name = timeDebugger.name;
            for (int i = 0; i < timeDebugger.records.size(); ++i) {
                this.records.add(timeDebugger.records.get(i));
                this.recordStrings.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, (String)timeDebugger.recordStrings.get(i)));
            }
            timeDebugger.clear();
        }
    }
    
    public void print() {
        if (GameServer.bServer) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add("END");
            if (this.records.size() > 1) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
                final long longValue = this.records.get(0);
                for (int i = 1; i < this.records.size(); ++i) {
                    final long longValue2 = this.records.get(i - 1);
                    final long longValue3 = this.records.get(i);
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;JJ)Ljava/lang/String;, i, (String)this.recordStrings.get(i), longValue3 - longValue, longValue3 - longValue2));
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, this.name, this.records.get(this.records.size() - 1) - longValue));
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
            }
        }
    }
    
    public long getExecTime() {
        if (this.records.size() == 0) {
            return 0L;
        }
        return System.currentTimeMillis() - this.records.get(0);
    }
}
