// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.secure;

import org.junit.Test;
import org.junit.Assert;

public class PZCryptTest extends Assert
{
    @Test
    public void hash() {
        assertEquals((Object)"$2a$12$O/BFHoDFPrfFaNPAACmWpuPkOtwkznuRQ7saS6/ouHjTT9KuVcKfq", (Object)PZcrypt.hash("123456"));
    }
    
    @Test
    public void hashSalt() {
        final String hashSalt = PZcrypt.hashSalt("1234567");
        final String hashSalt2 = PZcrypt.hashSalt("1234567");
        assertNotEquals((Object)hashSalt, (Object)hashSalt2);
        assertEquals((Object)true, (Object)PZcrypt.checkHashSalt(hashSalt, "1234567"));
        assertEquals((Object)false, (Object)PZcrypt.checkHashSalt(hashSalt, "1238567"));
        assertEquals((Object)true, (Object)PZcrypt.checkHashSalt(hashSalt2, "1234567"));
        assertEquals((Object)false, (Object)PZcrypt.checkHashSalt(hashSalt2, "dnfgdf;godf;ogdogi;"));
    }
}
