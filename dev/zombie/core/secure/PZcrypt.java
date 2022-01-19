// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.secure;

import org.mindrot.jbcrypt.BCrypt;

public class PZcrypt
{
    static String salt;
    
    public static String hash(final String s, final boolean b) {
        if (b && s.isEmpty()) {
            return s;
        }
        return BCrypt.hashpw(s, PZcrypt.salt);
    }
    
    public static String hash(final String s) {
        return hash(s, true);
    }
    
    public static String hashSalt(final String s) {
        return BCrypt.hashpw(s, BCrypt.gensalt(12));
    }
    
    public static boolean checkHashSalt(final String s, final String s2) {
        return BCrypt.checkpw(s2, s);
    }
    
    static {
        PZcrypt.salt = "$2a$12$O/BFHoDFPrfFaNPAACmWpu";
    }
}
