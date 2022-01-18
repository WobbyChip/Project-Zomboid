// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public class ExecuteTimeAnalyse
{
    String caption;
    TimeStamp[] list;
    int listIndex;
    
    public ExecuteTimeAnalyse(final String caption, final int n) {
        this.listIndex = 0;
        this.caption = caption;
        this.list = new TimeStamp[n];
        for (int i = 0; i < n; ++i) {
            this.list[i] = new TimeStamp();
        }
    }
    
    public void reset() {
        this.listIndex = 0;
    }
    
    public void add(final String comment) {
        this.list[this.listIndex].time = System.nanoTime();
        this.list[this.listIndex].comment = comment;
        ++this.listIndex;
    }
    
    public long getNanoTime() {
        if (this.listIndex == 0) {
            return 0L;
        }
        return System.nanoTime() - this.list[0].time;
    }
    
    public int getMsTime() {
        if (this.listIndex == 0) {
            return 0;
        }
        return (int)((System.nanoTime() - this.list[0].time) / 1000000L);
    }
    
    public void print() {
        long n = this.list[0].time;
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.caption));
        for (int i = 1; i < this.listIndex; ++i) {
            System.out.println(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;J)Ljava/lang/String;, i, this.list[i].comment, (this.list[i].time - n) / 1000000L));
            n = this.list[i].time;
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, (System.nanoTime() - this.list[0].time) / 1000000L));
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.caption));
    }
    
    static class TimeStamp
    {
        long time;
        String comment;
        
        public TimeStamp(final String comment) {
            this.comment = comment;
            this.time = System.nanoTime();
        }
        
        public TimeStamp() {
        }
    }
}
