// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import zombie.ZomboidFileSystem;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class IndieFileLoader
{
    public static InputStreamReader getStreamReader(final String s) throws FileNotFoundException {
        return getStreamReader(s, false);
    }
    
    public static InputStreamReader getStreamReader(final String s, final boolean b) throws FileNotFoundException {
        final InputStream in = null;
        InputStreamReader inputStreamReader;
        if (in != null && !b) {
            inputStreamReader = new InputStreamReader(in);
        }
        else {
            try {
                inputStreamReader = new InputStreamReader(new FileInputStream(ZomboidFileSystem.instance.getString(s)), "UTF-8");
            }
            catch (Exception ex) {
                inputStreamReader = new InputStreamReader(new FileInputStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getMyDocumentFolder(), File.separator, File.separator, s)));
            }
        }
        return inputStreamReader;
    }
}
