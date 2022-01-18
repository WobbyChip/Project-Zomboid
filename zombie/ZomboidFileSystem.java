// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.FileVisitOption;
import zombie.Lua.LuaEventManager;
import java.security.AccessControlException;
import zombie.debug.LogSeverity;
import java.io.FileFilter;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import java.io.FileInputStream;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoWorld;
import java.nio.file.Paths;
import zombie.network.GameClient;
import zombie.modding.ActiveModsFile;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import zombie.util.StringUtils;
import java.util.Collection;
import java.util.Arrays;
import zombie.debug.DebugLog;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.util.List;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamUtils;
import zombie.modding.ActiveMods;
import java.util.Iterator;
import zombie.network.GameServer;
import zombie.core.Core;
import java.io.IOException;
import java.util.Locale;
import java.util.HashSet;
import gnu.trove.map.hash.THashMap;
import java.net.URI;
import java.io.File;
import java.util.HashMap;
import zombie.gameStates.ChooseGameInfo;
import java.util.Map;
import java.util.ArrayList;

public final class ZomboidFileSystem
{
    public static final ZomboidFileSystem instance;
    private final ArrayList<String> loadList;
    private final Map<String, String> modIdToDir;
    private final Map<String, ChooseGameInfo.Mod> modDirToMod;
    private ArrayList<String> modFolders;
    private ArrayList<String> modFoldersOrder;
    public final HashMap<String, String> ActiveFileMap;
    public File base;
    public URI baseURI;
    private File workdir;
    private URI workdirURI;
    private File localWorkdir;
    private File anims;
    private URI animsURI;
    private File animsX;
    private URI animsXURI;
    private File animSets;
    private URI animSetsURI;
    private File actiongroups;
    private URI actiongroupsURI;
    private File cacheDir;
    private final THashMap<String, String> RelativeMap;
    public boolean IgnoreActiveFileMap;
    private final ArrayList<String> mods;
    private final HashSet<String> LoadedPacks;
    private FileGuidTable m_fileGuidTable;
    private boolean m_fileGuidTableWatcherActive;
    private final PredicatedFileWatcher m_modFileWatcher;
    private final HashSet<String> m_watchedModFolders;
    private long m_modsChangedTime;
    
    private ZomboidFileSystem() {
        this.loadList = new ArrayList<String>();
        this.modIdToDir = new HashMap<String, String>();
        this.modDirToMod = new HashMap<String, ChooseGameInfo.Mod>();
        this.ActiveFileMap = new HashMap<String, String>();
        this.RelativeMap = (THashMap<String, String>)new THashMap();
        this.IgnoreActiveFileMap = false;
        this.mods = new ArrayList<String>();
        this.LoadedPacks = new HashSet<String>();
        this.m_fileGuidTable = null;
        this.m_fileGuidTableWatcherActive = false;
        this.m_modFileWatcher = new PredicatedFileWatcher(this::isModFile, this::onModFileChanged);
        this.m_watchedModFolders = new HashSet<String>();
        this.m_modsChangedTime = 0L;
    }
    
    public void init() throws IOException {
        this.base = new File("./").getAbsoluteFile().getCanonicalFile();
        this.baseURI = this.base.toURI();
        this.workdir = new File(this.base, "media").getAbsoluteFile().getCanonicalFile();
        this.workdirURI = this.workdir.toURI();
        this.localWorkdir = this.base.toPath().relativize(this.workdir.toPath()).toFile();
        this.anims = new File(this.workdir, "anims");
        this.animsURI = this.anims.toURI();
        this.animsX = new File(this.workdir, "anims_X");
        this.animsXURI = this.animsX.toURI();
        this.animSets = new File(this.workdir, "AnimSets");
        this.animSetsURI = this.animSets.toURI();
        this.actiongroups = new File(this.workdir, "actiongroups");
        this.actiongroupsURI = this.actiongroups.toURI();
        this.searchFolders(this.workdir);
        for (int i = 0; i < this.loadList.size(); ++i) {
            final String relativeFile = this.getRelativeFile(this.loadList.get(i));
            final File absoluteFile = new File(this.loadList.get(i)).getAbsoluteFile();
            String absolutePath = absoluteFile.getAbsolutePath();
            if (absoluteFile.isDirectory()) {
                absolutePath = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, absolutePath, File.separator);
            }
            this.ActiveFileMap.put(relativeFile.toLowerCase(Locale.ENGLISH), absolutePath);
        }
        this.loadList.clear();
    }
    
    public String getGameModeCacheDir() {
        if (Core.GameMode == null) {
            Core.GameMode = "Sandbox";
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getSaveDir(), File.separator, Core.GameMode, File.separator);
    }
    
    public String getFileNameInCurrentSave(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator, s);
    }
    
    public String getFileNameInCurrentSave(final String s, final String s2) {
        return this.getFileNameInCurrentSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, s2));
    }
    
    public String getFileNameInCurrentSave(final String s, final String s2, final String s3) {
        return this.getFileNameInCurrentSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, s2, File.separator, s3));
    }
    
    public File getFileInCurrentSave(final String s) {
        return new File(this.getFileNameInCurrentSave(s));
    }
    
    public File getFileInCurrentSave(final String s, final String s2) {
        return new File(this.getFileNameInCurrentSave(s, s2));
    }
    
    public File getFileInCurrentSave(final String s, final String s2, final String s3) {
        return new File(this.getFileNameInCurrentSave(s, s2, s3));
    }
    
    public String getSaveDir() {
        final String cacheDirSub = this.getCacheDirSub("Saves");
        ensureFolderExists(cacheDirSub);
        return cacheDirSub;
    }
    
    public String getSaveDirSub(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getSaveDir(), File.separator, s);
    }
    
    public String getScreenshotDir() {
        final String cacheDirSub = this.getCacheDirSub("Screenshots");
        ensureFolderExists(cacheDirSub);
        return cacheDirSub;
    }
    
    public String getScreenshotDirSub(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getScreenshotDir(), File.separator, s);
    }
    
    public void setCacheDir(String replace) {
        replace = replace.replace("/", File.separator);
        ensureFolderExists(this.cacheDir = new File(replace).getAbsoluteFile());
    }
    
    public String getCacheDir() {
        if (this.cacheDir == null) {
            String s = System.getProperty("deployment.user.cachedir");
            if (s == null || System.getProperty("os.name").startsWith("Win")) {
                s = System.getProperty("user.home");
            }
            this.setCacheDir(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator));
        }
        return this.cacheDir.getPath();
    }
    
    public String getCacheDirSub(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getCacheDir(), File.separator, s);
    }
    
    public String getMessagingDir() {
        final String cacheDirSub = this.getCacheDirSub("messaging");
        ensureFolderExists(cacheDirSub);
        return cacheDirSub;
    }
    
    public String getMessagingDirSub(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getMessagingDir(), File.separator, s);
    }
    
    public File getMediaRootFile() {
        assert this.workdir != null;
        return this.workdir;
    }
    
    public String getMediaRootPath() {
        return this.workdir.getPath();
    }
    
    public File getMediaFile(final String child) {
        assert this.workdir != null;
        return new File(this.workdir, child);
    }
    
    public String getMediaPath(final String s) {
        return this.getMediaFile(s).getPath();
    }
    
    public String getAbsoluteWorkDir() {
        return this.workdir.getPath();
    }
    
    public String getLocalWorkDir() {
        return this.localWorkdir.getPath();
    }
    
    public String getLocalWorkDirSub(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getLocalWorkDir(), File.separator, s);
    }
    
    public String getAnimSetsPath() {
        return this.animSets.getPath();
    }
    
    public String getActionGroupsPath() {
        return this.actiongroups.getPath();
    }
    
    public static boolean ensureFolderExists(final String pathname) {
        return ensureFolderExists(new File(pathname).getAbsoluteFile());
    }
    
    public static boolean ensureFolderExists(final File file) {
        return file.exists() || file.mkdirs();
    }
    
    public void searchFolders(final File file) {
        if (!GameServer.bServer) {
            Thread.yield();
            Core.getInstance().DoFrameReady();
        }
        if (file.isDirectory()) {
            final String replace = file.getAbsolutePath().replace("\\", "/").replace("./", "");
            if (replace.contains("media/maps/")) {
                this.loadList.add(replace);
            }
            final String[] list = file.list();
            for (int i = 0; i < list.length; ++i) {
                this.searchFolders(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list[i])));
            }
        }
        else {
            this.loadList.add(file.getAbsolutePath().replace("\\", "/").replace("./", ""));
        }
    }
    
    public Object[] getAllPathsContaining(final String s) {
        final ArrayList<String> list = new ArrayList<String>();
        for (final Map.Entry<String, String> entry : this.ActiveFileMap.entrySet()) {
            if (entry.getKey().contains(s)) {
                list.add(entry.getValue());
            }
        }
        return list.toArray();
    }
    
    public Object[] getAllPathsContaining(final String s, final String s2) {
        final ArrayList<String> list = new ArrayList<String>();
        for (final Map.Entry<String, String> entry : this.ActiveFileMap.entrySet()) {
            if (entry.getKey().contains(s) && entry.getKey().contains(s2)) {
                list.add(entry.getValue());
            }
        }
        return list.toArray();
    }
    
    public synchronized String getString(final String s) {
        if (this.IgnoreActiveFileMap) {
            return s;
        }
        final String lowerCase = s.toLowerCase(Locale.ENGLISH);
        final String s2 = (String)this.RelativeMap.get((Object)lowerCase);
        String lowerCase2;
        if (s2 != null) {
            lowerCase2 = s2;
        }
        else {
            final String s3 = lowerCase;
            lowerCase2 = this.getRelativeFile(s).toLowerCase(Locale.ENGLISH);
            this.RelativeMap.put((Object)s3, (Object)lowerCase2);
        }
        final String s4 = this.ActiveFileMap.get(lowerCase2);
        if (s4 != null) {
            return s4;
        }
        return s;
    }
    
    public String getAbsolutePath(final String s) {
        return this.ActiveFileMap.get(s.toLowerCase(Locale.ENGLISH));
    }
    
    public void Reset() {
        this.loadList.clear();
        this.ActiveFileMap.clear();
        this.modIdToDir.clear();
        this.modDirToMod.clear();
        this.mods.clear();
        this.modFolders = null;
        ActiveMods.Reset();
        if (this.m_fileGuidTable != null) {
            this.m_fileGuidTable.clear();
            this.m_fileGuidTable = null;
        }
    }
    
    public void resetModFolders() {
        this.modFolders = null;
    }
    
    public void getInstalledItemModsFolders(final ArrayList<String> list) {
        if (SteamUtils.isSteamModeEnabled()) {
            final String[] getInstalledItemFolders = SteamWorkshop.instance.GetInstalledItemFolders();
            if (getInstalledItemFolders != null) {
                final String[] array = getInstalledItemFolders;
                for (int length = array.length, i = 0; i < length; ++i) {
                    final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, array[i], File.separator));
                    if (file.exists()) {
                        list.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }
    
    public void getStagedItemModsFolders(final ArrayList<String> list) {
        if (SteamUtils.isSteamModeEnabled()) {
            final ArrayList<String> stageFolders = SteamWorkshop.instance.getStageFolders();
            for (int i = 0; i < stageFolders.size(); ++i) {
                final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)stageFolders.get(i), File.separator, File.separator));
                if (file.exists()) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
    }
    
    private void getAllModFoldersAux(final String s, final List<String> list) {
        final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(final Path path) throws IOException {
                return Files.isDirectory(path, new LinkOption[0]) && Files.exists(path.resolve("mod.info"), new LinkOption[0]);
            }
        };
        final Path path = FileSystems.getDefault().getPath(s, new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, filter);
            try {
                for (final Path path2 : directoryStream) {
                    if (path2.getFileName().toString().toLowerCase().equals("examplemod")) {
                        DebugLog.Mod.println(invokedynamic(makeConcatWithConstants:(Ljava/nio/file/Path;)Ljava/lang/String;, path2.getFileName()));
                    }
                    else {
                        final String string = path2.toAbsolutePath().toString();
                        if (!this.m_watchedModFolders.contains(string)) {
                            this.m_watchedModFolders.add(string);
                            DebugFileWatcher.instance.addDirectory(string);
                            final Path resolve = path2.resolve("media");
                            if (Files.exists(resolve, new LinkOption[0])) {
                                DebugFileWatcher.instance.addDirectoryRecurse(resolve.toAbsolutePath().toString());
                            }
                        }
                        list.add(string);
                    }
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
            ex.printStackTrace();
        }
    }
    
    public void setModFoldersOrder(final String s) {
        this.modFoldersOrder = new ArrayList<String>(Arrays.asList(s.split(",")));
    }
    
    public void getAllModFolders(final List<String> list) {
        if (this.modFolders == null) {
            this.modFolders = new ArrayList<String>();
            if (this.modFoldersOrder == null) {
                this.setModFoldersOrder("workshop,steam,mods");
            }
            final ArrayList<String> list2 = new ArrayList<String>();
            for (int i = 0; i < this.modFoldersOrder.size(); ++i) {
                final String anObject = this.modFoldersOrder.get(i);
                if ("workshop".equals(anObject)) {
                    this.getStagedItemModsFolders(list2);
                }
                if ("steam".equals(anObject)) {
                    this.getInstalledItemModsFolders(list2);
                }
                if ("mods".equals(anObject)) {
                    list2.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getMyDocumentFolder(), File.separator));
                }
            }
            for (int j = 0; j < list2.size(); ++j) {
                final String s = list2.get(j);
                if (!this.m_watchedModFolders.contains(s)) {
                    this.m_watchedModFolders.add(s);
                    DebugFileWatcher.instance.addDirectory(s);
                }
                this.getAllModFoldersAux(s, this.modFolders);
            }
            DebugFileWatcher.instance.add(this.m_modFileWatcher);
        }
        list.clear();
        list.addAll(this.modFolders);
    }
    
    public ArrayList<ChooseGameInfo.Mod> getWorkshopItemMods(final long n) {
        final ArrayList<ChooseGameInfo.Mod> list = new ArrayList<ChooseGameInfo.Mod>();
        if (!SteamUtils.isSteamModeEnabled()) {
            return list;
        }
        final String getItemInstallFolder = SteamWorkshop.instance.GetItemInstallFolder(n);
        if (getItemInstallFolder == null) {
            return list;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getItemInstallFolder, File.separator));
        if (!file.exists() || !file.isDirectory()) {
            return list;
        }
        for (final File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                final ChooseGameInfo.Mod modInfo = ChooseGameInfo.readModInfo(file2.getAbsolutePath());
                if (modInfo != null) {
                    list.add(modInfo);
                }
            }
        }
        return list;
    }
    
    public ChooseGameInfo.Mod searchForModInfo(final File file, final String anObject, final ArrayList<ChooseGameInfo.Mod> list) {
        if (file.isDirectory()) {
            final String[] list2 = file.list();
            if (list2 == null) {
                return null;
            }
            for (int i = 0; i < list2.length; ++i) {
                final ChooseGameInfo.Mod searchForModInfo = this.searchForModInfo(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list2[i])), anObject, list);
                if (searchForModInfo != null) {
                    return searchForModInfo;
                }
            }
        }
        else if (file.getAbsolutePath().endsWith("mod.info")) {
            final ChooseGameInfo.Mod modInfo = ChooseGameInfo.readModInfo(file.getAbsoluteFile().getParent());
            if (modInfo == null) {
                return null;
            }
            if (!StringUtils.isNullOrWhitespace(modInfo.getId())) {
                this.modIdToDir.put(modInfo.getId(), modInfo.getDir());
                list.add(modInfo);
            }
            if (modInfo.getId().equals(anObject)) {
                return modInfo;
            }
        }
        return null;
    }
    
    public void loadMod(final String s) {
        if (this.getModDir(s) != null) {
            DebugLog.Mod.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            final File file = new File(this.getModDir(s));
            final URI uri = file.toURI();
            this.loadList.clear();
            this.searchFolders(file);
            for (int i = 0; i < this.loadList.size(); ++i) {
                final String lowerCase = this.getRelativeFile(uri, this.loadList.get(i)).toLowerCase(Locale.ENGLISH);
                if (this.ActiveFileMap.containsKey(lowerCase) && !lowerCase.endsWith("mod.info") && !lowerCase.endsWith("poster.png")) {
                    DebugLog.Mod.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, lowerCase));
                }
                this.ActiveFileMap.put(lowerCase, new File(this.loadList.get(i)).getAbsolutePath());
            }
            this.loadList.clear();
        }
    }
    
    private ArrayList<String> readLoadedDotTxt() {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getMyDocumentFolder(), File.separator, File.separator);
        final File file = new File(s);
        if (!file.exists()) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        try {
            final FileReader in = new FileReader(s);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    for (String s2 = bufferedReader.readLine(); s2 != null; s2 = bufferedReader.readLine()) {
                        final String trim = s2.trim();
                        if (!trim.isEmpty()) {
                            list.add(trim);
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
            ExceptionLogger.logException(ex);
            list = null;
        }
        try {
            file.delete();
        }
        catch (Exception ex2) {
            ExceptionLogger.logException(ex2);
        }
        return list;
    }
    
    private ActiveMods readDefaultModsTxt() {
        final ActiveMods byId = ActiveMods.getById("default");
        final ArrayList<String> loadedDotTxt = this.readLoadedDotTxt();
        if (loadedDotTxt != null) {
            byId.getMods().addAll(loadedDotTxt);
            this.saveModsFile();
        }
        byId.clear();
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getMyDocumentFolder(), File.separator, File.separator);
        try {
            if (new ActiveModsFile().read(s, byId)) {}
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        return byId;
    }
    
    public void loadMods(final String anotherString) {
        if (!Core.OptionModsEnabled) {
            return;
        }
        if (GameClient.bClient) {
            final ArrayList<String> list = new ArrayList<String>();
            this.loadTranslationMods(list);
            list.addAll(GameClient.instance.ServerMods);
            this.loadMods(list);
            return;
        }
        final ActiveMods byId = ActiveMods.getById(anotherString);
        if (!"default".equalsIgnoreCase(anotherString)) {
            ActiveMods.setLoadedMods(byId);
            this.loadMods(byId.getMods());
            return;
        }
        try {
            final ActiveMods defaultModsTxt = this.readDefaultModsTxt();
            defaultModsTxt.checkMissingMods();
            defaultModsTxt.checkMissingMaps();
            ActiveMods.setLoadedMods(defaultModsTxt);
            this.loadMods(defaultModsTxt.getMods());
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    private boolean isTranslationMod(final String s) {
        final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(s);
        if (availableModDetails == null) {
            return false;
        }
        boolean b = false;
        final File file = new File(availableModDetails.getDir());
        final URI uri = file.toURI();
        this.loadList.clear();
        this.searchFolders(file);
        for (int i = 0; i < this.loadList.size(); ++i) {
            final String relativeFile = this.getRelativeFile(uri, this.loadList.get(i));
            if (relativeFile.endsWith(".lua")) {
                return false;
            }
            if (relativeFile.startsWith("media/maps/")) {
                return false;
            }
            if (relativeFile.startsWith("media/scripts/")) {
                return false;
            }
            if (relativeFile.startsWith("media/lua/")) {
                if (!relativeFile.startsWith("media/lua/shared/Translate/")) {
                    return false;
                }
                b = true;
            }
        }
        this.loadList.clear();
        return b;
    }
    
    private void loadTranslationMods(final ArrayList<String> list) {
        if (!GameClient.bClient) {
            return;
        }
        final ActiveMods defaultModsTxt = this.readDefaultModsTxt();
        final ArrayList<String> list2 = new ArrayList<String>();
        if (this.loadModsAux(defaultModsTxt.getMods(), list2) == null) {
            for (final String s : list2) {
                if (this.isTranslationMod(s)) {
                    DebugLog.Mod.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    if (list.contains(s)) {
                        continue;
                    }
                    list.add(s);
                }
            }
        }
    }
    
    private String loadModAndRequired(final String e, final ArrayList<String> list) {
        if (e.isEmpty()) {
            return null;
        }
        if (e.toLowerCase().equals("examplemod")) {
            DebugLog.Mod.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            return null;
        }
        if (list.contains(e)) {
            return null;
        }
        final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(e);
        if (availableModDetails == null) {
            if (GameServer.bServer) {
                GameServer.ServerMods.remove(e);
            }
            DebugLog.Mod.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            return e;
        }
        if (availableModDetails.getRequire() != null) {
            final String loadModsAux = this.loadModsAux(availableModDetails.getRequire(), list);
            if (loadModsAux != null) {
                return loadModsAux;
            }
        }
        list.add(e);
        return null;
    }
    
    public String loadModsAux(final ArrayList<String> list, final ArrayList<String> list2) {
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            final String loadModAndRequired = this.loadModAndRequired(iterator.next(), list2);
            if (loadModAndRequired != null) {
                return loadModAndRequired;
            }
        }
        return null;
    }
    
    public void loadMods(final ArrayList<String> list) {
        this.mods.clear();
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.loadModAndRequired(iterator.next(), this.mods);
        }
        final Iterator<String> iterator2 = this.mods.iterator();
        while (iterator2.hasNext()) {
            this.loadMod(iterator2.next());
        }
    }
    
    public ArrayList<String> getModIDs() {
        return this.mods;
    }
    
    public String getModDir(final String s) {
        return this.modIdToDir.get(s);
    }
    
    public ChooseGameInfo.Mod getModInfoForDir(final String s) {
        ChooseGameInfo.Mod mod = this.modDirToMod.get(s);
        if (mod == null) {
            mod = new ChooseGameInfo.Mod(s);
            this.modDirToMod.put(s, mod);
        }
        return mod;
    }
    
    public String getRelativeFile(final File file) {
        return this.getRelativeFile(this.baseURI, file.getAbsolutePath());
    }
    
    public String getRelativeFile(final String s) {
        return this.getRelativeFile(this.baseURI, s);
    }
    
    public String getRelativeFile(final URI uri, final File file) {
        return this.getRelativeFile(uri, file.getAbsolutePath());
    }
    
    public String getRelativeFile(final URI uri, final String pathname) {
        final URI uri2 = new File(pathname).getAbsoluteFile().toURI();
        final URI relativize = uri.relativize(uri2);
        if (relativize.equals(uri2)) {
            return pathname;
        }
        String path = relativize.getPath();
        if (pathname.endsWith("/") && !path.endsWith("/")) {
            path = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path);
        }
        return path;
    }
    
    public String getAnimName(final URI uri, final File file) {
        String s = this.getRelativeFile(uri, file).toLowerCase(Locale.ENGLISH);
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex > -1) {
            s = s.substring(0, lastIndex);
        }
        if (s.startsWith("anims/")) {
            s = s.substring("anims/".length());
        }
        else if (s.startsWith("anims_x/")) {
            s = s.substring("anims_x/".length());
        }
        return s;
    }
    
    public String resolveRelativePath(final String first, final String other) {
        return this.getRelativeFile(Paths.get(first, new String[0]).getParent().resolve(other).toString());
    }
    
    public void saveModsFile() {
        try {
            ensureFolderExists(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getMyDocumentFolder(), File.separator));
            new ActiveModsFile().write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getMyDocumentFolder(), File.separator, File.separator), ActiveMods.getById("default"));
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void loadModPackFiles() {
        for (final String s : this.mods) {
            try {
                final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(s);
                if (availableModDetails == null) {
                    continue;
                }
                for (final ChooseGameInfo.PackFile packFile : availableModDetails.getPacks()) {
                    if (!this.ActiveFileMap.containsKey(this.getRelativeFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, packFile.name)).toLowerCase(Locale.ENGLISH))) {
                        DebugLog.Mod.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, packFile.name, s));
                    }
                    else {
                        final String string = ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, packFile.name));
                        if (this.LoadedPacks.contains(string)) {
                            continue;
                        }
                        GameWindow.LoadTexturePack(packFile.name, packFile.flags, s);
                        this.LoadedPacks.add(string);
                    }
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        GameWindow.setTexturePackLookup();
    }
    
    public void loadModTileDefs() {
        final HashSet<Integer> set = new HashSet<Integer>();
        for (final String s : this.mods) {
            try {
                final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(s);
                if (availableModDetails == null) {
                    continue;
                }
                for (final ChooseGameInfo.TileDef tileDef : availableModDetails.getTileDefs()) {
                    if (set.contains(tileDef.fileNumber)) {
                        DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, tileDef.fileNumber));
                    }
                    else {
                        final String lowerCase = this.getRelativeFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, tileDef.name)).toLowerCase(Locale.ENGLISH);
                        if (!this.ActiveFileMap.containsKey(lowerCase)) {
                            DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, tileDef.name, s));
                        }
                        else {
                            IsoWorld.instance.LoadTileDefinitions(IsoSpriteManager.instance, this.ActiveFileMap.get(lowerCase), tileDef.fileNumber);
                            set.add(tileDef.fileNumber);
                        }
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void loadModTileDefPropertyStrings() {
        final HashSet<Integer> set = new HashSet<Integer>();
        for (final String s : this.mods) {
            try {
                final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(s);
                if (availableModDetails == null) {
                    continue;
                }
                for (final ChooseGameInfo.TileDef tileDef : availableModDetails.getTileDefs()) {
                    if (set.contains(tileDef.fileNumber)) {
                        DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, tileDef.fileNumber));
                    }
                    else {
                        final String lowerCase = this.getRelativeFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, tileDef.name)).toLowerCase(Locale.ENGLISH);
                        if (!this.ActiveFileMap.containsKey(lowerCase)) {
                            DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, tileDef.name, s));
                        }
                        else {
                            IsoWorld.instance.LoadTileDefinitionsPropertyStrings(IsoSpriteManager.instance, this.ActiveFileMap.get(lowerCase), tileDef.fileNumber);
                            set.add(tileDef.fileNumber);
                        }
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void loadFileGuidTable() {
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile("fileGuidTable.xml");
        try {
            final FileInputStream fileInputStream = new FileInputStream(mediaFile);
            try {
                (this.m_fileGuidTable = (FileGuidTable)JAXBContext.newInstance(new Class[] { FileGuidTable.class }).createUnmarshaller().unmarshal((InputStream)fileInputStream)).setModID("game");
                fileInputStream.close();
            }
            catch (Throwable t) {
                try {
                    fileInputStream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (JAXBException | IOException ex3) {
            final Object o2;
            final Object o = o2;
            System.err.println("Failed to load file Guid table.");
            ExceptionLogger.logException((Throwable)o);
            return;
        }
        try {
            final Unmarshaller unmarshaller = JAXBContext.newInstance(new Class[] { FileGuidTable.class }).createUnmarshaller();
            for (final String modID : this.getModIDs()) {
                if (ChooseGameInfo.getAvailableModDetails(modID) == null) {
                    continue;
                }
                try {
                    final FileInputStream fileInputStream2 = new FileInputStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getModDir(modID)));
                    try {
                        final FileGuidTable fileGuidTable = (FileGuidTable)unmarshaller.unmarshal((InputStream)fileInputStream2);
                        fileGuidTable.setModID(modID);
                        this.m_fileGuidTable.mergeFrom(fileGuidTable);
                        fileInputStream2.close();
                    }
                    catch (Throwable t2) {
                        try {
                            fileInputStream2.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                        throw t2;
                    }
                }
                catch (FileNotFoundException ex4) {}
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
        catch (Exception ex2) {
            ExceptionLogger.logException(ex2);
        }
        this.m_fileGuidTable.loaded();
        if (!this.m_fileGuidTableWatcherActive) {
            DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/fileGuidTable.xml", p0 -> this.loadFileGuidTable()));
            this.m_fileGuidTableWatcherActive = true;
        }
    }
    
    public FileGuidTable getFileGuidTable() {
        if (this.m_fileGuidTable == null) {
            this.loadFileGuidTable();
        }
        return this.m_fileGuidTable;
    }
    
    public String getFilePathFromGuid(final String s) {
        final FileGuidTable fileGuidTable = this.getFileGuidTable();
        if (fileGuidTable != null) {
            return fileGuidTable.getFilePathFromGuid(s);
        }
        return null;
    }
    
    public String getGuidFromFilePath(final String s) {
        final FileGuidTable fileGuidTable = this.getFileGuidTable();
        if (fileGuidTable != null) {
            return fileGuidTable.getGuidFromFilePath(s);
        }
        return null;
    }
    
    public String resolveFileOrGUID(final String s) {
        String s2 = s;
        final String filePathFromGuid = this.getFilePathFromGuid(s);
        if (filePathFromGuid != null) {
            s2 = filePathFromGuid;
        }
        final String lowerCase = s2.toLowerCase(Locale.ENGLISH);
        if (this.ActiveFileMap.containsKey(lowerCase)) {
            return this.ActiveFileMap.get(lowerCase);
        }
        return s2;
    }
    
    public boolean isValidFilePathGuid(final String s) {
        return this.getFilePathFromGuid(s) != null;
    }
    
    public static File[] listAllDirectories(final String pathname, final FileFilter fileFilter, final boolean b) {
        return listAllDirectories(new File(pathname).getAbsoluteFile(), fileFilter, b);
    }
    
    public static File[] listAllDirectories(final File file, final FileFilter fileFilter, final boolean b) {
        if (!file.isDirectory()) {
            return new File[0];
        }
        final ArrayList<File> list = new ArrayList<File>();
        listAllDirectoriesInternal(file, fileFilter, b, list);
        return list.toArray(new File[0]);
    }
    
    private static void listAllDirectoriesInternal(final File file, final FileFilter fileFilter, final boolean b, final ArrayList<File> list) {
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (final File e : listFiles) {
            if (!e.isFile()) {
                if (e.isDirectory()) {
                    if (fileFilter.accept(e)) {
                        list.add(e);
                    }
                    if (b) {
                        listAllFilesInternal(e, fileFilter, true, list);
                    }
                }
            }
        }
    }
    
    public static File[] listAllFiles(final String pathname, final FileFilter fileFilter, final boolean b) {
        return listAllFiles(new File(pathname).getAbsoluteFile(), fileFilter, b);
    }
    
    public static File[] listAllFiles(final File file, final FileFilter fileFilter, final boolean b) {
        if (!file.isDirectory()) {
            return new File[0];
        }
        final ArrayList<File> list = new ArrayList<File>();
        listAllFilesInternal(file, fileFilter, b, list);
        return list.toArray(new File[0]);
    }
    
    private static void listAllFilesInternal(final File file, final FileFilter fileFilter, final boolean b, final ArrayList<File> list) {
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (final File e : listFiles) {
            if (e.isFile()) {
                if (fileFilter.accept(e)) {
                    list.add(e);
                }
            }
            else if (e.isDirectory() && b) {
                listAllFilesInternal(e, fileFilter, true, list);
            }
        }
    }
    
    public void walkGameAndModFiles(final String s, final boolean b, final IWalkFilesVisitor walkFilesVisitor) {
        this.walkGameAndModFilesInternal(this.base, s, b, walkFilesVisitor);
        final ArrayList<String> modIDs = this.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final String modDir = this.getModDir(modIDs.get(i));
            if (modDir != null) {
                this.walkGameAndModFilesInternal(new File(modDir), s, b, walkFilesVisitor);
            }
        }
    }
    
    private void walkGameAndModFilesInternal(final File parent, final String child, final boolean b, final IWalkFilesVisitor walkFilesVisitor) {
        final File file = new File(parent, child);
        if (!file.isDirectory()) {
            return;
        }
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (final File file2 : listFiles) {
            walkFilesVisitor.visit(file2, child);
            if (b && file2.isDirectory()) {
                this.walkGameAndModFilesInternal(parent, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, child, file2.getName()), true, walkFilesVisitor);
            }
        }
    }
    
    public String[] resolveAllDirectories(final String s, final FileFilter fileFilter, final boolean b) {
        final ArrayList list = new ArrayList();
        final ArrayList<String> list2;
        final String s3;
        this.walkGameAndModFiles(s, b, (file, s2) -> {
            if (file.isDirectory() && fileFilter.accept(file)) {
                // invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, file.getName())
                if (!list2.contains(s3)) {
                    list2.add(s3);
                }
            }
            return;
        });
        return list.toArray(new String[0]);
    }
    
    public String[] resolveAllFiles(final String s, final FileFilter fileFilter, final boolean b) {
        final ArrayList list = new ArrayList();
        final ArrayList<String> list2;
        final String s3;
        this.walkGameAndModFiles(s, b, (file, s2) -> {
            if (file.isFile() && fileFilter.accept(file)) {
                // invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, file.getName())
                if (!list2.contains(s3)) {
                    list2.add(s3);
                }
            }
            return;
        });
        return list.toArray(new String[0]);
    }
    
    public String normalizeFolderPath(String s) {
        s = s.toLowerCase(Locale.ENGLISH).replace('\\', '/');
        s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        s = s.replace("///", "/").replace("//", "/");
        return s;
    }
    
    public static String processFilePath(String s, final char c) {
        if (c != '\\') {
            s = s.replace('\\', c);
        }
        if (c != '/') {
            s = s.replace('/', c);
        }
        return s;
    }
    
    public boolean tryDeleteFile(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return false;
        }
        try {
            return this.deleteFile(s);
        }
        catch (IOException | AccessControlException ex) {
            final Throwable t;
            ExceptionLogger.logException(t, String.format("Failed to delete file: \"%s\"", s), DebugLog.FileIO, LogSeverity.General);
            return false;
        }
    }
    
    public boolean deleteFile(final String pathname) throws IOException {
        final File absoluteFile = new File(pathname).getAbsoluteFile();
        if (!absoluteFile.isFile()) {
            throw new FileNotFoundException(String.format("File path not found: \"%s\"", pathname));
        }
        if (absoluteFile.delete()) {
            DebugLog.FileIO.debugln("File deleted successfully: \"%s\"", pathname);
            return true;
        }
        DebugLog.FileIO.debugln("Failed to delete file: \"%s\"", pathname);
        return false;
    }
    
    public void update() {
        if (this.m_modsChangedTime == 0L) {
            return;
        }
        if (this.m_modsChangedTime > System.currentTimeMillis()) {
            return;
        }
        this.m_modsChangedTime = 0L;
        this.modFolders = null;
        this.modIdToDir.clear();
        this.modDirToMod.clear();
        ChooseGameInfo.Reset();
        final Iterator<String> iterator = this.getModIDs().iterator();
        while (iterator.hasNext()) {
            ChooseGameInfo.getModDetails(iterator.next());
        }
        LuaEventManager.triggerEvent("OnModsModified");
    }
    
    private boolean isModFile(String replace) {
        if (this.m_modsChangedTime > 0L) {
            return false;
        }
        if (this.modFolders == null) {
            return false;
        }
        replace = replace.toLowerCase().replace('\\', '/');
        if (replace.endsWith("/mods/default.txt")) {
            return false;
        }
        for (int i = 0; i < this.modFolders.size(); ++i) {
            if (replace.startsWith(this.modFolders.get(i).toLowerCase().replace('\\', '/'))) {
                return true;
            }
        }
        return false;
    }
    
    private void onModFileChanged(final String s) {
        this.m_modsChangedTime = System.currentTimeMillis() + 2000L;
    }
    
    public void cleanMultiplayerSaves() {
        DebugLog.FileIO.println("Start cleaning save fs");
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getSaveDir(), File.separator, File.separator));
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            for (final File file2 : file.listFiles()) {
                DebugLog.FileIO.println(invokedynamic(makeConcatWithConstants:(Ljava/io/File;)Ljava/lang/String;, file2.getAbsoluteFile()));
                if (file2.isDirectory() && new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file2.toString(), File.separator)).exists()) {
                    DebugLog.FileIO.println(invokedynamic(makeConcatWithConstants:(Ljava/io/File;)Ljava/lang/String;, file2.getAbsoluteFile()));
                    try {
                        Files.walk(file2.toPath(), new FileVisitOption[0]).forEach(path -> {
                            if (path.getFileName().toString().matches("map_\\d+_\\d+.bin")) {
                                DebugLog.FileIO.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path.getFileName().toString()));
                                path.toFile().delete();
                            }
                            return;
                        });
                    }
                    catch (IOException cause) {
                        throw new RuntimeException(cause);
                    }
                }
            }
        }
        catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
    
    public void resetDefaultModsForNewRelease(final String s) {
        ensureFolderExists(this.getCacheDirSub("mods"));
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getCacheDirSub("mods"), File.separator, s));
        if (file.exists()) {
            return;
        }
        try {
            final FileWriter out = new FileWriter(file);
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(out);
                try {
                    bufferedWriter.write("If this file does not exist, default.txt will be reset to empty (no mods active).");
                    bufferedWriter.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedWriter.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return;
        }
        ActiveMods.getById("default").clear();
        this.saveModsFile();
    }
    
    static {
        instance = new ZomboidFileSystem();
    }
    
    public interface IWalkFilesVisitor
    {
        void visit(final File p0, final String p1);
    }
}
