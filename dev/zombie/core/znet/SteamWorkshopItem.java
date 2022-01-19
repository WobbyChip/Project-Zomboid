// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.nio.file.FileSystems;
import zombie.core.textures.PNGDecoder;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.nio.file.LinkOption;
import java.nio.file.Files;
import java.nio.file.Path;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import zombie.ZomboidFileSystem;
import java.util.Collection;
import java.io.File;
import java.util.ArrayList;

public class SteamWorkshopItem
{
    private String workshopFolder;
    private String PublishedFileId;
    private String title;
    private String description;
    private String visibility;
    private final ArrayList<String> tags;
    private String changeNote;
    private boolean bHasMod;
    private boolean bHasMap;
    private final ArrayList<String> modIDs;
    private final ArrayList<String> mapFolders;
    private static final int VERSION1 = 1;
    private static final int LATEST_VERSION = 1;
    
    public SteamWorkshopItem(final String workshopFolder) {
        this.title = "";
        this.description = "";
        this.visibility = "public";
        this.tags = new ArrayList<String>();
        this.changeNote = "";
        this.modIDs = new ArrayList<String>();
        this.mapFolders = new ArrayList<String>();
        this.workshopFolder = workshopFolder;
    }
    
    public String getContentFolder() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.workshopFolder, File.separator);
    }
    
    public String getFolderName() {
        return new File(this.workshopFolder).getName();
    }
    
    public void setID(String publishedFileId) {
        if (publishedFileId != null && !SteamUtils.isValidSteamID(publishedFileId)) {
            publishedFileId = null;
        }
        this.PublishedFileId = publishedFileId;
    }
    
    public String getID() {
        return this.PublishedFileId;
    }
    
    public void setTitle(String title) {
        if (title == null) {
            title = "";
        }
        this.title = title;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }
    
    public String getVisibility() {
        return this.visibility;
    }
    
    public void setVisibilityInteger(final int n) {
        switch (n) {
            case 0: {
                this.visibility = "public";
                break;
            }
            case 1: {
                this.visibility = "friendsOnly";
                break;
            }
            case 2: {
                this.visibility = "private";
                break;
            }
            case 3: {
                this.visibility = "unlisted";
                break;
            }
            default: {
                this.visibility = "public";
                break;
            }
        }
    }
    
    public int getVisibilityInteger() {
        if ("public".equals(this.visibility)) {
            return 0;
        }
        if ("friendsOnly".equals(this.visibility)) {
            return 1;
        }
        if ("private".equals(this.visibility)) {
            return 2;
        }
        if ("unlisted".equals(this.visibility)) {
            return 3;
        }
        return 0;
    }
    
    public void setTags(final ArrayList<String> c) {
        this.tags.clear();
        this.tags.addAll(c);
    }
    
    public static ArrayList<String> getAllowedTags() {
        final ArrayList<String> list = new ArrayList<String>();
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile("WorkshopTags.txt");
        try {
            final FileReader in = new FileReader(mediaFile);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        final String trim = line.trim();
                        if (trim.isEmpty()) {
                            continue;
                        }
                        list.add(trim);
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
        }
        return list;
    }
    
    public ArrayList<String> getTags() {
        return this.tags;
    }
    
    public String getSubmitDescription() {
        String description = this.getDescription();
        if (!description.isEmpty()) {
            description = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, description);
        }
        String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, description, this.getID());
        for (int i = 0; i < this.modIDs.size(); ++i) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, (String)this.modIDs.get(i));
        }
        for (int j = 0; j < this.mapFolders.size(); ++j) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, (String)this.mapFolders.get(j));
        }
        return s;
    }
    
    public String[] getSubmitTags() {
        final ArrayList list = new ArrayList();
        list.addAll(this.tags);
        return list.toArray(new String[list.size()]);
    }
    
    public String getPreviewImage() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.workshopFolder, File.separator);
    }
    
    public void setChangeNote(String changeNote) {
        if (changeNote == null) {
            changeNote = "";
        }
        this.changeNote = changeNote;
    }
    
    public String getChangeNote() {
        return this.changeNote;
    }
    
    public boolean create() {
        return SteamWorkshop.instance.CreateWorkshopItem(this);
    }
    
    public boolean submitUpdate() {
        return SteamWorkshop.instance.SubmitWorkshopItem(this);
    }
    
    public boolean getUpdateProgress(final KahluaTable kahluaTable) {
        if (kahluaTable == null) {
            throw new NullPointerException("table is null");
        }
        final long[] array = new long[2];
        if (SteamWorkshop.instance.GetItemUpdateProgress(array)) {
            System.out.println(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, array[0], array[1]));
            kahluaTable.rawset((Object)"processed", (Object)array[0]);
            kahluaTable.rawset((Object)"total", (Object)Math.max(array[1], 1L));
            return true;
        }
        return false;
    }
    
    public int getUpdateProgressTotal() {
        return 1;
    }
    
    private String validateFileTypes(final Path dir) {
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (final Path path : directoryStream) {
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        final String validateFileTypes = this.validateFileTypes(path);
                        if (validateFileTypes != null) {
                            final String s = validateFileTypes;
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s;
                        }
                        continue;
                    }
                    else {
                        final String string = path.getFileName().toString();
                        if (string.endsWith(".exe") || string.endsWith(".dll") || string.endsWith(".bat") || string.endsWith(".app") || string.endsWith(".dylib") || string.endsWith(".sh") || string.endsWith(".so") || string.endsWith(".zip")) {
                            final String s2 = "FileTypeNotAllowed";
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s2;
                        }
                        continue;
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
            return "IOError";
        }
        return null;
    }
    
    private String validateModDotInfo(final Path path) {
        String trim = null;
        try {
            final FileReader in = new FileReader(path.toFile());
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith("id=")) {
                            trim = line.replace("id=", "").trim();
                            break;
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
        catch (FileNotFoundException ex2) {
            return "MissingModDotInfo";
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "IOError";
        }
        if (trim == null || trim.isEmpty()) {
            return "InvalidModDotInfo";
        }
        this.modIDs.add(trim);
        return null;
    }
    
    private String validateMapDotInfo(final Path path) {
        return null;
    }
    
    private String validateMapFolder(final Path dir) {
        boolean b = false;
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (final Path path : directoryStream) {
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        continue;
                    }
                    if (!"map.info".equals(path.getFileName().toString())) {
                        continue;
                    }
                    final String validateMapDotInfo = this.validateMapDotInfo(path);
                    if (validateMapDotInfo != null) {
                        final String s = validateMapDotInfo;
                        if (directoryStream != null) {
                            directoryStream.close();
                        }
                        return s;
                    }
                    b = true;
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
            return "IOError";
        }
        if (!b) {
            return "MissingMapDotInfo";
        }
        this.mapFolders.add(dir.getFileName().toString());
        return null;
    }
    
    private String validateMapsFolder(final Path dir) {
        boolean b = false;
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (final Path path : directoryStream) {
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        final String validateMapFolder = this.validateMapFolder(path);
                        if (validateMapFolder != null) {
                            final String s = validateMapFolder;
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s;
                        }
                        b = true;
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
            return "IOError";
        }
        if (!b) {
            return null;
        }
        this.bHasMap = true;
        return null;
    }
    
    private String validateMediaFolder(final Path dir) {
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (final Path path : directoryStream) {
                    if (Files.isDirectory(path, new LinkOption[0]) && "maps".equals(path.getFileName().toString())) {
                        final String validateMapsFolder = this.validateMapsFolder(path);
                        if (validateMapsFolder != null) {
                            final String s = validateMapsFolder;
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s;
                        }
                        continue;
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
            return "IOError";
        }
        return null;
    }
    
    private String validateModFolder(final Path dir) {
        boolean b = false;
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (final Path path : directoryStream) {
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        if (!"media".equals(path.getFileName().toString())) {
                            continue;
                        }
                        final String validateMediaFolder = this.validateMediaFolder(path);
                        if (validateMediaFolder != null) {
                            final String s = validateMediaFolder;
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s;
                        }
                        continue;
                    }
                    else {
                        if (!"mod.info".equals(path.getFileName().toString())) {
                            continue;
                        }
                        final String validateModDotInfo = this.validateModDotInfo(path);
                        if (validateModDotInfo != null) {
                            final String s2 = validateModDotInfo;
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s2;
                        }
                        b = true;
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
            return "IOError";
        }
        if (!b) {
            return "MissingModDotInfo";
        }
        return null;
    }
    
    private String validateModsFolder(final Path dir) {
        boolean b = false;
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (final Path path : directoryStream) {
                    if (!Files.isDirectory(path, new LinkOption[0])) {
                        final String s = "FileNotAllowedInMods";
                        if (directoryStream != null) {
                            directoryStream.close();
                        }
                        return s;
                    }
                    final String validateModFolder = this.validateModFolder(path);
                    if (validateModFolder != null) {
                        final String s2 = validateModFolder;
                        if (directoryStream != null) {
                            directoryStream.close();
                        }
                        return s2;
                    }
                    b = true;
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
            return "IOError";
        }
        if (!b) {
            return "EmptyModsFolder";
        }
        this.bHasMod = true;
        return null;
    }
    
    private String validateBuildingsFolder(final Path path) {
        return null;
    }
    
    private String validateCreativeFolder(final Path path) {
        return null;
    }
    
    public String validatePreviewImage(final Path path) throws IOException {
        if (!Files.exists(path, new LinkOption[0]) || !Files.isReadable(path) || Files.isDirectory(path, new LinkOption[0])) {
            return "PreviewNotFound";
        }
        if (Files.size(path) > 1024000L) {
            return "PreviewFileSize";
        }
        try {
            final FileInputStream in = new FileInputStream(path.toFile());
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    final PNGDecoder pngDecoder = new PNGDecoder(bufferedInputStream, false);
                    if (pngDecoder.getWidth() != 256 || pngDecoder.getHeight() != 256) {
                        final String s = "PreviewDimensions";
                        bufferedInputStream.close();
                        in.close();
                        return s;
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
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
        catch (IOException ex) {
            ex.printStackTrace();
            return "PreviewFormat";
        }
        return null;
    }
    
    public String validateContents() {
        this.bHasMod = false;
        this.bHasMap = false;
        this.modIDs.clear();
        this.mapFolders.clear();
        try {
            final Path path = FileSystems.getDefault().getPath(this.getContentFolder(), new String[0]);
            if (!Files.isDirectory(path, new LinkOption[0])) {
                return "MissingContents";
            }
            final String validatePreviewImage = this.validatePreviewImage(FileSystems.getDefault().getPath(this.getPreviewImage(), new String[0]));
            if (validatePreviewImage != null) {
                return validatePreviewImage;
            }
            boolean b = false;
            try {
                final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
                try {
                    for (final Path path2 : directoryStream) {
                        if (!Files.isDirectory(path2, new LinkOption[0])) {
                            final String s = "FileNotAllowedInContents";
                            if (directoryStream != null) {
                                directoryStream.close();
                            }
                            return s;
                        }
                        if ("buildings".equals(path2.getFileName().toString())) {
                            final String validateBuildingsFolder = this.validateBuildingsFolder(path2);
                            if (validateBuildingsFolder != null) {
                                final String s2 = validateBuildingsFolder;
                                if (directoryStream != null) {
                                    directoryStream.close();
                                }
                                return s2;
                            }
                        }
                        else if ("creative".equals(path2.getFileName().toString())) {
                            final String validateCreativeFolder = this.validateCreativeFolder(path2);
                            if (validateCreativeFolder != null) {
                                final String s3 = validateCreativeFolder;
                                if (directoryStream != null) {
                                    directoryStream.close();
                                }
                                return s3;
                            }
                        }
                        else {
                            if (!"mods".equals(path2.getFileName().toString())) {
                                final String s4 = "FolderNotAllowedInContents";
                                if (directoryStream != null) {
                                    directoryStream.close();
                                }
                                return s4;
                            }
                            final String validateModsFolder = this.validateModsFolder(path2);
                            if (validateModsFolder != null) {
                                final String s5 = validateModsFolder;
                                if (directoryStream != null) {
                                    directoryStream.close();
                                }
                                return s5;
                            }
                        }
                        b = true;
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
                return "IOError";
            }
            if (!b) {
                return "EmptyContentsFolder";
            }
            return this.validateFileTypes(path);
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
            return "IOError";
        }
    }
    
    public String getExtendedErrorInfo(final String s) {
        if ("FolderNotAllowedInContents".equals(s)) {
            return "buildings/ creative/ mods/";
        }
        if ("FileTypeNotAllowed".equals(s)) {
            return "*.exe *.dll *.bat *.app *.dylib *.sh *.so *.zip";
        }
        return "";
    }
    
    public boolean readWorkshopTxt() {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.workshopFolder, File.separator);
        if (!new File(s).exists()) {
            return true;
        }
        try {
            final FileReader in = new FileReader(s);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        final String trim = line.trim();
                        if (trim.isEmpty()) {
                            continue;
                        }
                        if (trim.startsWith("#")) {
                            continue;
                        }
                        if (trim.startsWith("//")) {
                            continue;
                        }
                        if (trim.startsWith("id=")) {
                            this.setID(trim.replace("id=", ""));
                        }
                        else if (trim.startsWith("description=")) {
                            if (!this.description.isEmpty()) {
                                this.description = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.description);
                            }
                            this.description = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.description, trim.replace("description=", ""));
                        }
                        else if (trim.startsWith("tags=")) {
                            this.tags.addAll(Arrays.asList(trim.replace("tags=", "").split(";")));
                        }
                        else if (trim.startsWith("title=")) {
                            this.title = trim.replace("title=", "");
                        }
                        else {
                            if (trim.startsWith("version=")) {
                                continue;
                            }
                            if (!trim.startsWith("visibility=")) {
                                continue;
                            }
                            this.visibility = trim.replace("visibility=", "");
                        }
                    }
                    final boolean b = true;
                    bufferedReader.close();
                    in.close();
                    return b;
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
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean writeWorkshopTxt() {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.workshopFolder, File.separator));
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("version=1");
            bufferedWriter.newLine();
            bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.PublishedFileId == null) ? "" : this.PublishedFileId));
            bufferedWriter.newLine();
            bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.title));
            bufferedWriter.newLine();
            final String[] split = this.description.split("\n");
            for (int length = split.length, i = 0; i < length; ++i) {
                bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, split[i]));
                bufferedWriter.newLine();
            }
            String s = "";
            for (int j = 0; j < this.tags.size(); ++j) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, (String)this.tags.get(j));
                if (j < this.tags.size() - 1) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                }
            }
            bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            bufferedWriter.newLine();
            bufferedWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.visibility));
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public enum ItemState
    {
        None(0), 
        Subscribed(1), 
        LegacyItem(2), 
        Installed(4), 
        NeedsUpdate(8), 
        Downloading(16), 
        DownloadPending(32);
        
        private final int value;
        
        private ItemState(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
        
        public boolean and(final ItemState itemState) {
            return (this.value & itemState.value) != 0x0;
        }
        
        public boolean and(final long n) {
            return ((long)this.value & n) != 0x0L;
        }
        
        public static String toString(final long n) {
            if (n == ItemState.None.getValue()) {
                return "None";
            }
            final StringBuilder sb = new StringBuilder();
            for (final ItemState itemState : values()) {
                if (itemState != ItemState.None && itemState.and(n)) {
                    if (sb.length() > 0) {
                        sb.append("|");
                    }
                    sb.append(itemState.name());
                }
            }
            return sb.toString();
        }
        
        private static /* synthetic */ ItemState[] $values() {
            return new ItemState[] { ItemState.None, ItemState.Subscribed, ItemState.LegacyItem, ItemState.Installed, ItemState.NeedsUpdate, ItemState.Downloading, ItemState.DownloadPending };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
