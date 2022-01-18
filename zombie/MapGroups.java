// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.Translator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.util.Collection;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import zombie.iso.IsoWorld;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.gameStates.ChooseGameInfo;
import zombie.modding.ActiveMods;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class MapGroups
{
    private final ArrayList<MapGroup> groups;
    private final ArrayList<MapDirectory> realDirectories;
    
    public MapGroups() {
        this.groups = new ArrayList<MapGroup>();
        this.realDirectories = new ArrayList<MapDirectory>();
    }
    
    private static ArrayList<String> getVanillaMapDirectories(final boolean b) {
        final ArrayList<String> list = new ArrayList<String>();
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile("maps");
        final String[] list2 = mediaFile.list();
        if (list2 != null) {
            for (int i = 0; i < list2.length; ++i) {
                final String e = list2[i];
                if (e.equalsIgnoreCase("challengemaps")) {
                    if (b) {
                        try {
                            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(mediaFile.getPath(), e), path -> Files.isDirectory(path, new LinkOption[0]) && Files.exists(path.resolve("map.info"), new LinkOption[0]));
                            try {
                                final Iterator<Path> iterator = directoryStream.iterator();
                                while (iterator.hasNext()) {
                                    list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, e, iterator.next().getFileName().toString()));
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
                        catch (Exception ex) {}
                    }
                }
                else {
                    list.add(e);
                }
            }
        }
        return list;
    }
    
    public static String addMissingVanillaDirectories(final String s) {
        final ArrayList<String> vanillaMapDirectories = getVanillaMapDirectories(false);
        boolean b = false;
        final String[] split;
        final String[] array = split = s.split(";");
        for (int length = split.length, i = 0; i < length; ++i) {
            final String trim = split[i].trim();
            if (!trim.isEmpty()) {
                if (vanillaMapDirectories.contains(trim)) {
                    b = true;
                    break;
                }
            }
        }
        if (!b) {
            return s;
        }
        final ArrayList<String> list = new ArrayList<String>();
        final String[] array2 = array;
        for (int length2 = array2.length, j = 0; j < length2; ++j) {
            final String trim2 = array2[j].trim();
            if (!trim2.isEmpty()) {
                list.add(trim2);
            }
        }
        for (final String s2 : vanillaMapDirectories) {
            if (!list.contains(s2)) {
                list.add(s2);
            }
        }
        String s3 = "";
        for (final String s4 : list) {
            if (!s3.isEmpty()) {
                s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
            }
            s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4);
        }
        return s3;
    }
    
    public void createGroups() {
        this.createGroups(ActiveMods.getById("currentGame"), true);
    }
    
    public void createGroups(final ActiveMods activeMods, final boolean b) {
        this.createGroups(activeMods, b, false);
    }
    
    public void createGroups(final ActiveMods order, final boolean b, final boolean b2) {
        this.groups.clear();
        this.realDirectories.clear();
        final Iterator<String> iterator = order.getMods().iterator();
        while (iterator.hasNext()) {
            final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(iterator.next());
            if (availableModDetails == null) {
                continue;
            }
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, availableModDetails.getDir()));
            if (!file.exists()) {
                continue;
            }
            final String[] list = file.list();
            if (list == null) {
                continue;
            }
            for (int i = 0; i < list.length; ++i) {
                final String s = list[i];
                if (s.equalsIgnoreCase("challengemaps")) {
                    if (b2) {}
                }
                else {
                    this.handleMapDirectory(s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, availableModDetails.getDir(), s));
                }
            }
        }
        if (b) {
            final ArrayList<String> vanillaMapDirectories = getVanillaMapDirectories(b2);
            final String mediaPath = ZomboidFileSystem.instance.getMediaPath("maps");
            for (final String s2 : vanillaMapDirectories) {
                this.handleMapDirectory(s2, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, mediaPath, File.separator, s2));
            }
        }
        for (final MapDirectory mapDirectory : this.realDirectories) {
            final ArrayList<MapDirectory> list2 = new ArrayList<MapDirectory>();
            this.getDirsRecursively(mapDirectory, list2);
            MapGroup groupWithAnyOfTheseDirectories = this.findGroupWithAnyOfTheseDirectories(list2);
            if (groupWithAnyOfTheseDirectories == null) {
                groupWithAnyOfTheseDirectories = new MapGroup();
                this.groups.add(groupWithAnyOfTheseDirectories);
            }
            for (final MapDirectory mapDirectory2 : list2) {
                if (!groupWithAnyOfTheseDirectories.hasDirectory(mapDirectory2.name)) {
                    groupWithAnyOfTheseDirectories.addDirectory(mapDirectory2);
                }
            }
        }
        final Iterator<MapGroup> iterator5 = this.groups.iterator();
        while (iterator5.hasNext()) {
            iterator5.next().setPriority();
        }
        final Iterator<MapGroup> iterator6 = this.groups.iterator();
        while (iterator6.hasNext()) {
            iterator6.next().setOrder(order);
        }
        if (Core.bDebug) {
            int n = 1;
            for (final MapGroup mapGroup : this.groups) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, this.groups.size()));
                final Iterator<Object> iterator8 = mapGroup.directories.iterator();
                while (iterator8.hasNext()) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, iterator8.next().name));
                }
                ++n;
            }
            DebugLog.log("-----");
        }
    }
    
    private void getDirsRecursively(final MapDirectory mapDirectory, final ArrayList<MapDirectory> list) {
        if (list.contains(mapDirectory)) {
            return;
        }
        list.add(mapDirectory);
        for (final String anObject : mapDirectory.lotDirs) {
            for (final MapDirectory mapDirectory2 : this.realDirectories) {
                if (mapDirectory2.name.equals(anObject)) {
                    this.getDirsRecursively(mapDirectory2, list);
                    break;
                }
            }
        }
    }
    
    public int getNumberOfGroups() {
        return this.groups.size();
    }
    
    public ArrayList<String> getMapDirectoriesInGroup(final int index) {
        if (index < 0 || index >= this.groups.size()) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index));
        }
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<MapDirectory> iterator = this.groups.get(index).directories.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().name);
        }
        return list;
    }
    
    public void setWorld(final int n) {
        final ArrayList<String> mapDirectoriesInGroup = this.getMapDirectoriesInGroup(n);
        String map = "";
        for (int i = 0; i < mapDirectoriesInGroup.size(); ++i) {
            map = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, map, (String)mapDirectoriesInGroup.get(i));
            if (i < mapDirectoriesInGroup.size() - 1) {
                map = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, map);
            }
        }
        IsoWorld.instance.setMap(map);
    }
    
    private void handleMapDirectory(final String s, final String s2) {
        final ArrayList<String> lotDirectories = this.getLotDirectories(s2);
        if (lotDirectories == null) {
            return;
        }
        this.realDirectories.add(new MapDirectory(s, s2, lotDirectories));
    }
    
    private ArrayList<String> getLotDirectories(final String s) {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (!file.exists()) {
            return null;
        }
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final FileReader in = new FileReader(file.getAbsolutePath());
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        final String trim = line.trim();
                        if (trim.startsWith("lots=")) {
                            list.add(trim.replace("lots=", "").trim());
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
            return null;
        }
        return list;
    }
    
    private MapGroup findGroupWithAnyOfTheseDirectories(final ArrayList<MapDirectory> list) {
        for (final MapGroup mapGroup : this.groups) {
            if (mapGroup.hasAnyOfTheseDirectories(list)) {
                return mapGroup;
            }
        }
        return null;
    }
    
    public ArrayList<String> getAllMapsInOrder() {
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<MapGroup> iterator = this.groups.iterator();
        while (iterator.hasNext()) {
            final Iterator<Object> iterator2 = iterator.next().directories.iterator();
            while (iterator2.hasNext()) {
                list.add(iterator2.next().name);
            }
        }
        return list;
    }
    
    public boolean checkMapConflicts() {
        boolean b = false;
        final Iterator<MapGroup> iterator = this.groups.iterator();
        while (iterator.hasNext()) {
            b |= iterator.next().checkMapConflicts();
        }
        return b;
    }
    
    public ArrayList<String> getMapConflicts(final String s) {
        final Iterator<MapGroup> iterator = this.groups.iterator();
        while (iterator.hasNext()) {
            final MapDirectory directoryByName = iterator.next().getDirectoryByName(s);
            if (directoryByName == null) {
                continue;
            }
            final ArrayList<String> list = new ArrayList<String>();
            list.addAll(directoryByName.conflicts);
            return list;
        }
        return null;
    }
    
    private class MapDirectory
    {
        String name;
        String path;
        ArrayList<String> lotDirs;
        ArrayList<String> conflicts;
        
        public MapDirectory(final String name, final String path) {
            this.lotDirs = new ArrayList<String>();
            this.conflicts = new ArrayList<String>();
            this.name = name;
            this.path = path;
        }
        
        public MapDirectory(final String name, final String path, final ArrayList<String> c) {
            this.lotDirs = new ArrayList<String>();
            this.conflicts = new ArrayList<String>();
            this.name = name;
            this.path = path;
            this.lotDirs.addAll(c);
        }
        
        public void getLotHeaders(final ArrayList<String> list) {
            final File file = new File(this.path);
            if (!file.isDirectory()) {
                return;
            }
            final String[] list2 = file.list();
            if (list2 == null) {
                return;
            }
            for (int i = 0; i < list2.length; ++i) {
                if (list2[i].endsWith(".lotheader")) {
                    list.add(list2[i]);
                }
            }
        }
    }
    
    private class MapGroup
    {
        private LinkedList<MapDirectory> directories;
        
        private MapGroup() {
            this.directories = new LinkedList<MapDirectory>();
        }
        
        void addDirectory(final String s, final String s2) {
            assert !this.hasDirectory(s);
            this.directories.add(new MapDirectory(s, s2));
        }
        
        void addDirectory(final String s, final String s2, final ArrayList<String> list) {
            assert !this.hasDirectory(s);
            this.directories.add(new MapDirectory(s, s2, list));
        }
        
        void addDirectory(final MapDirectory e) {
            assert !this.hasDirectory(e.name);
            this.directories.add(e);
        }
        
        MapDirectory getDirectoryByName(final String anObject) {
            for (final MapDirectory mapDirectory : this.directories) {
                if (mapDirectory.name.equals(anObject)) {
                    return mapDirectory;
                }
            }
            return null;
        }
        
        boolean hasDirectory(final String s) {
            return this.getDirectoryByName(s) != null;
        }
        
        boolean hasAnyOfTheseDirectories(final ArrayList<MapDirectory> list) {
            final Iterator<MapDirectory> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (this.directories.contains(iterator.next())) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isReferencedByOtherMaps(final MapDirectory mapDirectory) {
            for (final MapDirectory mapDirectory2 : this.directories) {
                if (mapDirectory == mapDirectory2) {
                    continue;
                }
                if (mapDirectory2.lotDirs.contains(mapDirectory.name)) {
                    return true;
                }
            }
            return false;
        }
        
        void getDirsRecursively(final MapDirectory mapDirectory, final ArrayList<String> list) {
            if (list.contains(mapDirectory.name)) {
                return;
            }
            list.add(mapDirectory.name);
            final Iterator<String> iterator = mapDirectory.lotDirs.iterator();
            while (iterator.hasNext()) {
                final MapDirectory directoryByName = this.getDirectoryByName(iterator.next());
                if (directoryByName == null) {
                    continue;
                }
                this.getDirsRecursively(directoryByName, list);
            }
        }
        
        void setPriority() {
            for (final MapDirectory mapDirectory : new ArrayList<MapDirectory>(this.directories)) {
                if (this.isReferencedByOtherMaps(mapDirectory)) {
                    continue;
                }
                final ArrayList<String> priority = new ArrayList<String>();
                this.getDirsRecursively(mapDirectory, priority);
                this.setPriority(priority);
            }
        }
        
        void setPriority(final List<String> list) {
            final ArrayList<MapDirectory> list2 = new ArrayList<MapDirectory>(list.size());
            for (final String s : list) {
                if (!this.hasDirectory(s)) {
                    continue;
                }
                list2.add(this.getDirectoryByName(s));
            }
            for (int i = 0; i < this.directories.size(); ++i) {
                if (list.contains(this.directories.get(i).name)) {
                    this.directories.set(i, list2.remove(0));
                }
            }
        }
        
        void setOrder(final ActiveMods activeMods) {
            if (!activeMods.getMapOrder().isEmpty()) {
                this.setPriority(activeMods.getMapOrder());
            }
        }
        
        boolean checkMapConflicts() {
            final HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
            final ArrayList<String> list = new ArrayList<String>();
            for (final MapDirectory mapDirectory : this.directories) {
                mapDirectory.conflicts.clear();
                list.clear();
                mapDirectory.getLotHeaders(list);
                for (final String key : list) {
                    if (!hashMap.containsKey(key)) {
                        hashMap.put(key, new ArrayList<String>());
                    }
                    hashMap.get(key).add(mapDirectory.name);
                }
            }
            boolean b = false;
            for (final String key2 : hashMap.keySet()) {
                final ArrayList<String> list2 = hashMap.get(key2);
                if (list2.size() > 1) {
                    for (int i = 0; i < list2.size(); ++i) {
                        final MapDirectory directoryByName = this.getDirectoryByName(list2.get(i));
                        for (int j = 0; j < list2.size(); ++j) {
                            if (i != j) {
                                directoryByName.conflicts.add(Translator.getText("UI_MapConflict", directoryByName.name, list2.get(j), key2));
                                b = true;
                            }
                        }
                    }
                }
            }
            return b;
        }
    }
}
