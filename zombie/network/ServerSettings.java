// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import se.krka.kahlua.vm.KahluaTable;
import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import java.io.File;
import zombie.core.Translator;
import zombie.util.StringUtils;
import zombie.profanity.ProfanityFilter;
import java.util.ArrayList;
import zombie.SandboxOptions;

public class ServerSettings
{
    protected String name;
    protected ServerOptions serverOptions;
    protected SandboxOptions sandboxOptions;
    protected ArrayList<SpawnRegions.Region> spawnRegions;
    protected ArrayList<SpawnRegions.Profession> spawnPoints;
    private boolean valid;
    private String errorMsg;
    
    public ServerSettings(final String name) {
        this.valid = true;
        this.errorMsg = null;
        this.errorMsg = null;
        this.valid = true;
        this.name = name;
        final String validateString = ProfanityFilter.getInstance().validateString(name, true, true, true);
        if (!StringUtils.isNullOrEmpty(validateString)) {
            this.errorMsg = Translator.getText("UI_BadWordCheck", validateString);
            this.valid = false;
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void resetToDefault() {
        this.serverOptions = new ServerOptions();
        this.sandboxOptions = new SandboxOptions();
        this.spawnRegions = new SpawnRegions().getDefaultServerRegions();
        this.spawnPoints = null;
    }
    
    public boolean loadFiles() {
        (this.serverOptions = new ServerOptions()).loadServerTextFile(this.name);
        (this.sandboxOptions = new SandboxOptions()).loadServerLuaFile(this.name);
        this.sandboxOptions.loadServerZombiesFile(this.name);
        final SpawnRegions spawnRegions = new SpawnRegions();
        this.spawnRegions = spawnRegions.loadRegionsFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name)));
        if (this.spawnRegions == null) {
            this.spawnRegions = spawnRegions.getDefaultServerRegions();
        }
        this.spawnPoints = spawnRegions.loadPointsFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name)));
        return true;
    }
    
    public boolean saveFiles() {
        if (this.serverOptions == null) {
            return false;
        }
        this.serverOptions.saveServerTextFile(this.name);
        this.sandboxOptions.saveServerLuaFile(this.name);
        if (this.spawnRegions != null) {
            new SpawnRegions().saveRegionsFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name)), this.spawnRegions);
        }
        if (this.spawnPoints != null) {
            new SpawnRegions().savePointsFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name)), this.spawnPoints);
        }
        this.tryDeleteFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        return true;
    }
    
    private boolean tryDeleteFile(final String s) {
        try {
            final File file = new File(ServerSettingsManager.instance.getNameInSettingsFolder(s));
            if (file.exists()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                file.delete();
            }
            return true;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
    }
    
    public boolean deleteFiles() {
        this.tryDeleteFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        this.tryDeleteFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        this.tryDeleteFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        this.tryDeleteFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        this.tryDeleteFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        return true;
    }
    
    public boolean duplicateFiles(final String name) {
        if (!ServerSettingsManager.instance.isValidNewName(name)) {
            return false;
        }
        final ServerSettings serverSettings = new ServerSettings(this.name);
        serverSettings.loadFiles();
        if (serverSettings.spawnRegions != null) {
            for (final SpawnRegions.Region region : serverSettings.spawnRegions) {
                if (region.serverfile != null && region.serverfile.equals(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name))) {
                    region.serverfile = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
                }
            }
        }
        serverSettings.name = name;
        serverSettings.saveFiles();
        return true;
    }
    
    public boolean rename(final String name) {
        if (!ServerSettingsManager.instance.isValidNewName(name)) {
            return false;
        }
        this.loadFiles();
        this.deleteFiles();
        if (this.spawnRegions != null) {
            for (final SpawnRegions.Region region : this.spawnRegions) {
                if (region.serverfile != null && region.serverfile.equals(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name))) {
                    region.serverfile = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
                }
            }
        }
        this.name = name;
        this.saveFiles();
        return true;
    }
    
    public ServerOptions getServerOptions() {
        return this.serverOptions;
    }
    
    public SandboxOptions getSandboxOptions() {
        return this.sandboxOptions;
    }
    
    public int getNumSpawnRegions() {
        return this.spawnRegions.size();
    }
    
    public String getSpawnRegionName(final int index) {
        return this.spawnRegions.get(index).name;
    }
    
    public String getSpawnRegionFile(final int index) {
        final SpawnRegions.Region region = this.spawnRegions.get(index);
        return (region.file != null) ? region.file : region.serverfile;
    }
    
    public void clearSpawnRegions() {
        this.spawnRegions.clear();
    }
    
    public void addSpawnRegion(final String name, final String s) {
        if (name == null || s == null) {
            throw new NullPointerException();
        }
        final SpawnRegions.Region e = new SpawnRegions.Region();
        e.name = name;
        if (s.startsWith("media")) {
            e.file = s;
        }
        else {
            e.serverfile = s;
        }
        this.spawnRegions.add(e);
    }
    
    public void removeSpawnRegion(final int index) {
        this.spawnRegions.remove(index);
    }
    
    public KahluaTable loadSpawnPointsFile(final String s) {
        return new SpawnRegions().loadPointsTable(ServerSettingsManager.instance.getNameInSettingsFolder(s));
    }
    
    public boolean saveSpawnPointsFile(final String s, final KahluaTable kahluaTable) {
        return new SpawnRegions().savePointsTable(ServerSettingsManager.instance.getNameInSettingsFolder(s), kahluaTable);
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
