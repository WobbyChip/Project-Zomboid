// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

public class Userlog
{
    private String username;
    private String type;
    private String text;
    private String issuedBy;
    private int amount;
    
    public Userlog(final String username, final String type, final String text, final String issuedBy, final int amount) {
        this.username = username;
        this.type = type;
        this.text = text;
        this.issuedBy = issuedBy;
        this.amount = amount;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getIssuedBy() {
        return this.issuedBy;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    public enum UserlogType
    {
        AdminLog(0), 
        Kicked(1), 
        Banned(2), 
        DupeItem(3), 
        LuaChecksum(4), 
        WarningPoint(5);
        
        private int index;
        
        private UserlogType(final int index) {
            this.index = index;
        }
        
        public int index() {
            return this.index;
        }
        
        public static UserlogType fromIndex(final int n) {
            return UserlogType.class.getEnumConstants()[n];
        }
        
        public static UserlogType FromString(final String s) {
            return valueOf(s);
        }
        
        private static /* synthetic */ UserlogType[] $values() {
            return new UserlogType[] { UserlogType.AdminLog, UserlogType.Kicked, UserlogType.Banned, UserlogType.DupeItem, UserlogType.LuaChecksum, UserlogType.WarningPoint };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
