// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStream;

public class WinReqistry
{
    public static String getSteamDirectory() {
        return readRegistry("HKEY_CURRENT_USER\\Software\\Valve\\Steam", "SteamPath");
    }
    
    public static final String readRegistry(final String s, final String s2) {
        try {
            final Process exec = Runtime.getRuntime().exec(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
            final StreamReader streamReader = new StreamReader(exec.getInputStream());
            streamReader.start();
            exec.waitFor();
            streamReader.join();
            final String result = streamReader.getResult();
            if (result == null || result.equals("")) {
                return null;
            }
            final String[] split = result.substring(result.indexOf("REG_SZ") + 7).trim().split("\t");
            return split[split.length - 1];
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    static class StreamReader extends Thread
    {
        private InputStream is;
        private StringWriter sw;
        
        public StreamReader(final InputStream is) {
            this.sw = new StringWriter();
            this.is = is;
        }
        
        @Override
        public void run() {
            try {
                int read;
                while ((read = this.is.read()) != -1) {
                    this.sw.write(read);
                }
            }
            catch (IOException ex) {}
        }
        
        public String getResult() {
            return this.sw.toString();
        }
    }
}
