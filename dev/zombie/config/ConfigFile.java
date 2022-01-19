// 
// Decompiled by Procyon v0.5.36
// 

package zombie.config;

import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.debug.DebugLog;
import java.util.ArrayList;

public final class ConfigFile
{
    protected ArrayList<ConfigOption> options;
    protected int version;
    
    private void fileError(final String s, final int n, final String s2) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, s, n, s2));
    }
    
    public boolean read(final String pathname) {
        this.options = new ArrayList<ConfigOption>();
        this.version = 0;
        final File file = new File(pathname);
        if (!file.exists()) {
            return false;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
        try {
            final FileReader in = new FileReader(file);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    int n = 0;
                    while (true) {
                        final String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        ++n;
                        final String trim = line.trim();
                        if (trim.isEmpty()) {
                            continue;
                        }
                        if (trim.startsWith("#")) {
                            continue;
                        }
                        if (!trim.contains("=")) {
                            this.fileError(pathname, n, trim);
                        }
                        else {
                            final String[] split = trim.split("=");
                            if ("Version".equals(split[0])) {
                                try {
                                    this.version = Integer.parseInt(split[1]);
                                }
                                catch (NumberFormatException ex2) {
                                    this.fileError(pathname, n, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, split[1]));
                                }
                            }
                            else {
                                this.options.add(new StringConfigOption(split[0], (split.length > 1) ? split[1] : ""));
                            }
                        }
                    }
                    bufferedReader.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedReader.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean write(final String pathname, final int n, final ArrayList<? extends ConfigOption> list) {
        final File file = new File(pathname);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
        try {
            final FileWriter fileWriter = new FileWriter(file, false);
            try {
                if (n != 0) {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n, System.lineSeparator()));
                }
                for (int i = 0; i < list.size(); ++i) {
                    final ConfigOption configOption = (ConfigOption)list.get(i);
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, configOption.getName(), configOption.getValueAsString(), System.lineSeparator()));
                }
                fileWriter.close();
            }
            catch (Throwable t) {
                try {
                    fileWriter.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public ArrayList<ConfigOption> getOptions() {
        return this.options;
    }
    
    public int getVersion() {
        return this.version;
    }
}
