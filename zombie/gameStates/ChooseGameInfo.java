// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.core.znet.SteamWorkshop;
import zombie.core.GameVersion;
import zombie.core.Core;
import zombie.core.textures.TextureID;
import java.util.Collection;
import java.util.Arrays;
import zombie.core.IndieFileLoader;
import zombie.debug.DebugLog;
import java.util.List;
import zombie.core.logger.ExceptionLogger;
import zombie.core.Translator;
import zombie.core.textures.Texture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import zombie.util.StringUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.Language;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

public final class ChooseGameInfo
{
    private static final HashMap<String, Map> Maps;
    private static final HashMap<String, Mod> Mods;
    private static final HashSet<String> MissingMods;
    private static final ArrayList<String> tempStrings;
    
    private ChooseGameInfo() {
    }
    
    public static void Reset() {
        ChooseGameInfo.Maps.clear();
        ChooseGameInfo.Mods.clear();
        ChooseGameInfo.MissingMods.clear();
    }
    
    private static void readTitleDotTxt(final Map map, final String s, final Language language) throws IOException {
        final File file = new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, language.toString(), s)));
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final InputStreamReader in2 = new InputStreamReader(in, Charset.forName(language.charset()));
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in2);
                    try {
                        final String stripBOM = StringUtils.stripBOM(bufferedReader.readLine());
                        if (!StringUtils.isNullOrWhitespace(stripBOM)) {
                            map.title = stripBOM.trim();
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
                    in2.close();
                }
                catch (Throwable t2) {
                    try {
                        in2.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
                in.close();
            }
            catch (Throwable t3) {
                try {
                    in.close();
                }
                catch (Throwable exception3) {
                    t3.addSuppressed(exception3);
                }
                throw t3;
            }
        }
        catch (FileNotFoundException ex) {}
    }
    
    private static void readDescriptionDotTxt(final Map map, final String s, final Language language) throws IOException {
        final File file = new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, language.toString(), s)));
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final InputStreamReader in2 = new InputStreamReader(in, Charset.forName(language.charset()));
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in2);
                    try {
                        map.desc = "";
                        int n = 1;
                        String s2;
                        while ((s2 = bufferedReader.readLine()) != null) {
                            if (n != 0) {
                                s2 = StringUtils.stripBOM(s2);
                                n = 0;
                            }
                            map.desc = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, map.desc, s2);
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
                    in2.close();
                }
                catch (Throwable t2) {
                    try {
                        in2.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
                in.close();
            }
            catch (Throwable t3) {
                try {
                    in.close();
                }
                catch (Throwable exception3) {
                    t3.addSuppressed(exception3);
                }
                throw t3;
            }
        }
        catch (FileNotFoundException ex) {}
    }
    
    public static Map getMapDetails(final String s) {
        if (ChooseGameInfo.Maps.containsKey(s)) {
            return ChooseGameInfo.Maps.get(s);
        }
        final File file = new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)));
        if (!file.exists()) {
            return null;
        }
        final Map value = new Map();
        value.dir = new File(file.getParent()).getAbsolutePath();
        value.title = s;
        value.lotsDir = new ArrayList<String>();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String trim = line.trim();
                    if (trim.startsWith("title=")) {
                        value.title = trim.replace("title=", "");
                    }
                    else if (trim.startsWith("lots=")) {
                        value.lotsDir.add(trim.replace("lots=", "").trim());
                    }
                    else if (trim.startsWith("description=")) {
                        if (value.desc == null) {
                            value.desc = "";
                        }
                        final Map map = value;
                        map.desc = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, map.desc, trim.replace("description=", ""));
                    }
                    else {
                        if (!trim.startsWith("fixed2x=")) {
                            continue;
                        }
                        value.bFixed2x = Boolean.parseBoolean(trim.replace("fixed2x=", "").trim());
                    }
                }
            }
            catch (IOException thrown) {
                Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, null, thrown);
            }
            bufferedReader.close();
            value.thumb = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value.dir));
            final ArrayList list = new ArrayList<Language>();
            Translator.addLanguageToList(Translator.getLanguage(), list);
            Translator.addLanguageToList(Translator.getDefaultLanguage(), list);
            for (int i = list.size() - 1; i >= 0; --i) {
                final Language language = list.get(i);
                readTitleDotTxt(value, s, language);
                readDescriptionDotTxt(value, s, language);
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return null;
        }
        ChooseGameInfo.Maps.put(s, value);
        return value;
    }
    
    public static Mod getModDetails(final String s) {
        if (ChooseGameInfo.MissingMods.contains(s)) {
            return null;
        }
        if (ChooseGameInfo.Mods.containsKey(s)) {
            return ChooseGameInfo.Mods.get(s);
        }
        final String modDir = ZomboidFileSystem.instance.getModDir(s);
        if (modDir == null) {
            final ArrayList<String> tempStrings = ChooseGameInfo.tempStrings;
            ZomboidFileSystem.instance.getAllModFolders(tempStrings);
            final ArrayList<Mod> list = new ArrayList<Mod>();
            for (int i = 0; i < tempStrings.size(); ++i) {
                final File file = new File(tempStrings.get(i), "mod.info");
                list.clear();
                final Mod searchForModInfo = ZomboidFileSystem.instance.searchForModInfo(file, s, list);
                for (int j = 0; j < list.size(); ++j) {
                    final Mod value = list.get(j);
                    ChooseGameInfo.Mods.putIfAbsent(value.getId(), value);
                }
                if (searchForModInfo != null) {
                    return searchForModInfo;
                }
            }
        }
        final Mod modInfo = readModInfo(modDir);
        if (modInfo == null) {
            ChooseGameInfo.MissingMods.add(s);
        }
        return modInfo;
    }
    
    public static Mod getAvailableModDetails(final String s) {
        final Mod modDetails = getModDetails(s);
        if (modDetails != null && modDetails.isAvailable()) {
            return modDetails;
        }
        return null;
    }
    
    public static Mod readModInfo(final String s) {
        final Mod modInfoAux = readModInfoAux(s);
        if (modInfoAux != null) {
            final Mod mod = ChooseGameInfo.Mods.get(modInfoAux.getId());
            if (mod == null) {
                ChooseGameInfo.Mods.put(modInfoAux.getId(), modInfoAux);
            }
            else if (mod != modInfoAux) {
                ZomboidFileSystem.instance.getAllModFolders(ChooseGameInfo.tempStrings);
                if (ChooseGameInfo.tempStrings.indexOf(modInfoAux.getDir()) < ChooseGameInfo.tempStrings.indexOf(mod.getDir())) {
                    ChooseGameInfo.Mods.put(modInfoAux.getId(), modInfoAux);
                }
            }
        }
        return modInfoAux;
    }
    
    private static Mod readModInfoAux(final String s) {
        if (s != null) {
            final Mod modInfoForDir = ZomboidFileSystem.instance.getModInfoForDir(s);
            if (modInfoForDir.bRead) {
                return modInfoForDir.bValid ? modInfoForDir : null;
            }
            modInfoForDir.bRead = true;
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator);
            final File file = new File(pathname);
            if (!file.exists()) {
                DebugLog.Mod.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
                return null;
            }
            modInfoForDir.setId(file.getParentFile().getName());
            try {
                final InputStreamReader streamReader = IndieFileLoader.getStreamReader(pathname);
                try {
                    final BufferedReader bufferedReader = new BufferedReader(streamReader);
                    try {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("name=")) {
                                modInfoForDir.name = line.replace("name=", "");
                            }
                            else if (line.contains("poster=")) {
                                final String replace = line.replace("poster=", "");
                                if (StringUtils.isNullOrWhitespace(replace)) {
                                    continue;
                                }
                                modInfoForDir.posters.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, replace));
                            }
                            else if (line.contains("description=")) {
                                final Mod mod = modInfoForDir;
                                mod.desc = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, mod.desc, line.replace("description=", ""));
                            }
                            else if (line.contains("require=")) {
                                modInfoForDir.setRequire(new ArrayList<String>(Arrays.asList(line.replace("require=", "").split(","))));
                            }
                            else if (line.contains("id=")) {
                                modInfoForDir.setId(line.replace("id=", ""));
                            }
                            else if (line.contains("url=")) {
                                modInfoForDir.setUrl(line.replace("url=", ""));
                            }
                            else if (line.contains("pack=")) {
                                String s2 = line.replace("pack=", "").trim();
                                if (s2.isEmpty()) {
                                    DebugLog.Mod.error((Object)"pack= line requires a file name");
                                    final Mod mod2 = null;
                                    bufferedReader.close();
                                    if (streamReader != null) {
                                        streamReader.close();
                                    }
                                    return mod2;
                                }
                                int n = (TextureID.bUseCompressionOption ? 4 : 0) | 0x40;
                                final int index = s2.indexOf("type=");
                                if (index != -1) {
                                    final String substring;
                                    final String s3 = substring = s2.substring(index + "type=".length());
                                    switch (substring) {
                                        case "ui": {
                                            n = 2;
                                            break;
                                        }
                                        default: {
                                            DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3));
                                            break;
                                        }
                                    }
                                    s2 = s2.substring(0, s2.indexOf(32)).trim();
                                }
                                if (s2.endsWith(".floor")) {
                                    n &= 0xFFFFFFFB;
                                }
                                if (Core.TileScale == 2) {
                                    if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, File.separator, File.separator, s2)).isFile()) {
                                        DebugLog.Mod.printf("2x version of %s.pack found.\n", s2);
                                        s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
                                    }
                                    else {
                                        DebugLog.Mod.printf("2x version of %s.pack not found.\n", s2);
                                    }
                                }
                                modInfoForDir.addPack(s2, n);
                            }
                            else if (line.contains("tiledef=")) {
                                final String[] split = line.replace("tiledef=", "").trim().split("\\s+");
                                if (split.length != 2) {
                                    DebugLog.Mod.error((Object)"tiledef= line requires file name and file number");
                                    final Mod mod3 = null;
                                    bufferedReader.close();
                                    if (streamReader != null) {
                                        streamReader.close();
                                    }
                                    return mod3;
                                }
                                final String s4 = split[0];
                                int int1;
                                try {
                                    int1 = Integer.parseInt(split[1]);
                                }
                                catch (NumberFormatException ex4) {
                                    DebugLog.Mod.error((Object)"tiledef= line requires file name and file number");
                                    final Mod mod4 = null;
                                    bufferedReader.close();
                                    if (streamReader != null) {
                                        streamReader.close();
                                    }
                                    return mod4;
                                }
                                if (int1 < 100 || int1 > 1000) {
                                    DebugLog.Mod.error("tiledef=%s %d file number must be from 100 to 1000", s4, int1);
                                }
                                modInfoForDir.addTileDef(s4, int1);
                            }
                            else if (line.startsWith("versionMax=")) {
                                final String trim = line.replace("versionMax=", "").trim();
                                if (trim.isEmpty()) {
                                    continue;
                                }
                                try {
                                    modInfoForDir.versionMax = GameVersion.parse(trim);
                                }
                                catch (Exception ex) {
                                    DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex.getMessage()));
                                    final Mod mod5 = null;
                                    bufferedReader.close();
                                    if (streamReader != null) {
                                        streamReader.close();
                                    }
                                    return mod5;
                                }
                            }
                            else {
                                if (!line.startsWith("versionMin=")) {
                                    continue;
                                }
                                final String trim2 = line.replace("versionMin=", "").trim();
                                if (trim2.isEmpty()) {
                                    continue;
                                }
                                try {
                                    modInfoForDir.versionMin = GameVersion.parse(trim2);
                                }
                                catch (Exception ex2) {
                                    DebugLog.Mod.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex2.getMessage()));
                                    final Mod mod6 = null;
                                    bufferedReader.close();
                                    if (streamReader != null) {
                                        streamReader.close();
                                    }
                                    return mod6;
                                }
                            }
                        }
                        if (modInfoForDir.getUrl() == null) {
                            modInfoForDir.setUrl("");
                        }
                        modInfoForDir.bValid = true;
                        final Mod mod7 = modInfoForDir;
                        bufferedReader.close();
                        if (streamReader != null) {
                            streamReader.close();
                        }
                        return mod7;
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
                }
                catch (Throwable t2) {
                    if (streamReader != null) {
                        try {
                            streamReader.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception ex3) {
                ExceptionLogger.logException(ex3);
            }
        }
        return null;
    }
    
    static {
        Maps = new HashMap<String, Map>();
        Mods = new HashMap<String, Mod>();
        MissingMods = new HashSet<String>();
        tempStrings = new ArrayList<String>();
    }
    
    public static final class SpawnOrigin
    {
        public int x;
        public int y;
        public int w;
        public int h;
        
        public SpawnOrigin(final int x, final int y, final int w, final int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    
    public static final class Map
    {
        private String dir;
        private Texture thumb;
        private String title;
        private ArrayList<String> lotsDir;
        private String desc;
        private boolean bFixed2x;
        
        public String getDirectory() {
            return this.dir;
        }
        
        public void setDirectory(final String dir) {
            this.dir = dir;
        }
        
        public Texture getThumbnail() {
            return this.thumb;
        }
        
        public void setThumbnail(final Texture thumb) {
            this.thumb = thumb;
        }
        
        public String getTitle() {
            return this.title;
        }
        
        public void setTitle(final String title) {
            this.title = title;
        }
        
        public ArrayList<String> getLotDirectories() {
            return this.lotsDir;
        }
        
        public String getDescription() {
            return this.desc;
        }
        
        public void setDescription(final String desc) {
            this.desc = desc;
        }
        
        public boolean isFixed2x() {
            return this.bFixed2x;
        }
        
        public void setFixed2x(final boolean bFixed2x) {
            this.bFixed2x = bFixed2x;
        }
    }
    
    public static final class PackFile
    {
        public final String name;
        public final int flags;
        
        public PackFile(final String name, final int flags) {
            this.name = name;
            this.flags = flags;
        }
    }
    
    public static final class TileDef
    {
        public String name;
        public int fileNumber;
        
        public TileDef(final String name, final int fileNumber) {
            this.name = name;
            this.fileNumber = fileNumber;
        }
    }
    
    public static final class Mod
    {
        public String dir;
        public final File baseFile;
        public final File mediaFile;
        public final File actionGroupsFile;
        public final File animSetsFile;
        public final File animsXFile;
        private final ArrayList<String> posters;
        public Texture tex;
        private ArrayList<String> require;
        private String name;
        private String desc;
        private String id;
        private String url;
        private String workshopID;
        private boolean bAvailableDone;
        private boolean available;
        private GameVersion versionMin;
        private GameVersion versionMax;
        private final ArrayList<PackFile> packs;
        private final ArrayList<TileDef> tileDefs;
        private boolean bRead;
        private boolean bValid;
        
        public Mod(final String s) {
            this.posters = new ArrayList<String>();
            this.name = "Unnamed Mod";
            this.desc = "";
            this.bAvailableDone = false;
            this.available = true;
            this.packs = new ArrayList<PackFile>();
            this.tileDefs = new ArrayList<TileDef>();
            this.bRead = false;
            this.bValid = false;
            this.dir = s;
            File file = new File(s).getAbsoluteFile();
            try {
                file = file.getCanonicalFile();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
            this.baseFile = file;
            this.mediaFile = new File(file, "media");
            this.actionGroupsFile = new File(this.mediaFile, "actiongroups");
            this.animSetsFile = new File(this.mediaFile, "AnimSets");
            this.animsXFile = new File(this.mediaFile, "anims_X");
            final File parentFile = file.getParentFile();
            if (parentFile != null) {
                final File parentFile2 = parentFile.getParentFile();
                if (parentFile2 != null) {
                    this.workshopID = SteamWorkshop.instance.getIDFromItemInstallFolder(parentFile2.getAbsolutePath());
                }
            }
        }
        
        public Texture getTexture() {
            if (this.tex == null) {
                final String s = this.posters.isEmpty() ? null : this.posters.get(0);
                if (!StringUtils.isNullOrWhitespace(s)) {
                    this.tex = Texture.getSharedTexture(s);
                }
                if (this.tex == null || this.tex.isFailure()) {
                    if (Core.bDebug && this.tex == null) {
                        DebugLog.Mod.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (s == null) ? this.id : s));
                    }
                    this.tex = Texture.getWhite();
                }
            }
            return this.tex;
        }
        
        public void setTexture(final Texture tex) {
            this.tex = tex;
        }
        
        public int getPosterCount() {
            return this.posters.size();
        }
        
        public String getPoster(final int index) {
            if (index >= 0 && index < this.posters.size()) {
                return this.posters.get(index);
            }
            return null;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getDir() {
            return this.dir;
        }
        
        public String getDescription() {
            return this.desc;
        }
        
        public ArrayList<String> getRequire() {
            return this.require;
        }
        
        public void setRequire(final ArrayList<String> require) {
            this.require = require;
        }
        
        public String getId() {
            return this.id;
        }
        
        public void setId(final String id) {
            this.id = id;
        }
        
        public boolean isAvailable() {
            if (this.bAvailableDone) {
                return this.available;
            }
            this.bAvailableDone = true;
            if (!this.isAvailableSelf()) {
                return this.available = false;
            }
            ChooseGameInfo.tempStrings.clear();
            ChooseGameInfo.tempStrings.add(this.getId());
            if (!this.isAvailableRequired(ChooseGameInfo.tempStrings)) {
                return this.available = false;
            }
            return this.available = true;
        }
        
        private boolean isAvailableSelf() {
            final GameVersion gameVersion = Core.getInstance().getGameVersion();
            return (this.versionMin == null || !this.versionMin.isGreaterThan(gameVersion)) && (this.versionMax == null || !this.versionMax.isLessThan(gameVersion));
        }
        
        private boolean isAvailableRequired(final ArrayList<String> list) {
            if (this.require == null || this.require.isEmpty()) {
                return true;
            }
            for (int i = 0; i < this.require.size(); ++i) {
                final String trim = this.require.get(i).trim();
                if (!list.contains(trim)) {
                    list.add(trim);
                    final Mod modDetails = ChooseGameInfo.getModDetails(trim);
                    if (modDetails == null) {
                        return false;
                    }
                    if (!modDetails.isAvailableSelf()) {
                        return false;
                    }
                    if (!modDetails.isAvailableRequired(list)) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        @Deprecated
        public void setAvailable(final boolean b) {
        }
        
        public String getUrl() {
            if (this.url == null) {
                return "";
            }
            return this.url;
        }
        
        public void setUrl(final String url) {
            if (url.startsWith("http://theindiestone.com") || url.startsWith("http://www.theindiestone.com") || url.startsWith("http://pz-mods.net") || url.startsWith("http://www.pz-mods.net")) {
                this.url = url;
            }
        }
        
        public GameVersion getVersionMin() {
            return this.versionMin;
        }
        
        public GameVersion getVersionMax() {
            return this.versionMax;
        }
        
        public void addPack(final String s, final int n) {
            this.packs.add(new PackFile(s, n));
        }
        
        public void addTileDef(final String s, final int n) {
            this.tileDefs.add(new TileDef(s, n));
        }
        
        public ArrayList<PackFile> getPacks() {
            return this.packs;
        }
        
        public ArrayList<TileDef> getTileDefs() {
            return this.tileDefs;
        }
        
        public String getWorkshopID() {
            return this.workshopID;
        }
    }
}
