// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;

public class ServerSettingsManager
{
    public static final ServerSettingsManager instance;
    protected ArrayList<ServerSettings> settings;
    protected ArrayList<String> suffixes;
    
    public ServerSettingsManager() {
        this.settings = new ArrayList<ServerSettings>();
        this.suffixes = new ArrayList<String>();
    }
    
    public String getSettingsFolder() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
    }
    
    public String getNameInSettingsFolder(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getSettingsFolder(), File.separator, s);
    }
    
    public void readAllSettings() {
        this.settings.clear();
        final File file = new File(this.getSettingsFolder());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(final Path path) throws IOException {
                final String string = path.getFileName().toString();
                return !Files.isDirectory(path, new LinkOption[0]) && string.endsWith(".ini") && !string.endsWith("_zombies.ini") && ServerSettingsManager.this.isValidName(string.replace(".ini", ""));
            }
        };
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file.toPath(), filter);
            try {
                final Iterator<Path> iterator = directoryStream.iterator();
                while (iterator.hasNext()) {
                    this.settings.add(new ServerSettings(iterator.next().getFileName().toString().replace(".ini", "")));
                }
                if (directoryStream != null) {
                    directoryStream.close();
                }
            }
            catch (Throwable t) {
                if (directoryStream != null) {
                    try {
                        directoryStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public int getSettingsCount() {
        return this.settings.size();
    }
    
    public ServerSettings getSettingsByIndex(final int index) {
        if (index >= 0 && index < this.settings.size()) {
            return this.settings.get(index);
        }
        return null;
    }
    
    public boolean isValidName(final String s) {
        return s != null && !s.isEmpty() && !s.contains("/") && !s.contains("\\") && !s.contains(":") && !s.contains(";") && !s.contains("\"") && !s.contains(".") && !s.contains("_zombies");
    }
    
    private boolean anyFilesExist(final String s) {
        this.getSuffixes();
        for (int i = 0; i < this.suffixes.size(); ++i) {
            if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getSettingsFolder(), File.separator, s, (String)this.suffixes.get(i))).exists()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isValidNewName(final String s) {
        return this.isValidName(s) && !this.anyFilesExist(s);
    }
    
    public ArrayList<String> getSuffixes() {
        if (this.suffixes.isEmpty()) {
            this.suffixes.add(".ini");
            this.suffixes.add("_SandboxVars.lua");
            this.suffixes.add("_spawnpoints.lua");
            this.suffixes.add("_spawnregions.lua");
            this.suffixes.add("_zombies.ini");
        }
        return this.suffixes;
    }
    
    static {
        instance = new ServerSettingsManager();
    }
}
