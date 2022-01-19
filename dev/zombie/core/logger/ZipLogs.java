// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.logger;

import zombie.network.MD5Checksum;
import java.util.Iterator;
import java.nio.file.OpenOption;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.nio.file.FileVisitOption;
import zombie.core.Core;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.util.zip.ZipError;
import zombie.debug.DebugLog;
import java.io.IOException;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.HashMap;
import java.nio.file.FileSystems;
import java.net.URI;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;

public final class ZipLogs
{
    static ArrayList<String> filePaths;
    
    public static void addZipFile(final boolean b) {
        FileSystem fileSystem = null;
        try {
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
            final URI create = URI.create(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new File(pathname).toURI().toString()));
            final Path absolutePath = FileSystems.getDefault().getPath(pathname, new String[0]).toAbsolutePath();
            final HashMap<String, String> env = new HashMap<String, String>();
            env.put("create", String.valueOf(Files.notExists(absolutePath, new LinkOption[0])));
            try {
                fileSystem = FileSystems.newFileSystem(create, env);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            catch (ZipError zipError) {
                zipError.printStackTrace();
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
                try {
                    Files.deleteIfExists(absolutePath);
                }
                catch (IOException ex2) {
                    ex2.printStackTrace();
                }
                return;
            }
            final long md5FromZip = getMD5FromZip(fileSystem, "/meta/console.txt.md5");
            final long md5FromZip2 = getMD5FromZip(fileSystem, "/meta/coop-console.txt.md5");
            final long md5FromZip3 = getMD5FromZip(fileSystem, "/meta/server-console.txt.md5");
            final long md5FromZip4 = getMD5FromZip(fileSystem, "/meta/DebugLog.txt.md5");
            addLogToZip(fileSystem, "console", "console.txt", md5FromZip);
            addLogToZip(fileSystem, "coop-console", "coop-console.txt", md5FromZip2);
            addLogToZip(fileSystem, "server-console", "server-console.txt", md5FromZip3);
            addDebugLogToZip(fileSystem, "debug-log", "DebugLog.txt", md5FromZip4);
            addToZip(fileSystem, "/configs/options.ini", "options.ini");
            addToZip(fileSystem, "/configs/popman-options.ini", "popman-options.ini");
            addToZip(fileSystem, "/configs/latestSave.ini", "latestSave.ini");
            addToZip(fileSystem, "/configs/debug-options.ini", "debug-options.ini");
            addToZip(fileSystem, "/configs/sounds.ini", "sounds.ini");
            addToZip(fileSystem, "/addition/translationProblems.txt", "translationProblems.txt");
            addToZip(fileSystem, "/addition/gamepadBinding.config", "gamepadBinding.config");
            addFilelistToZip(fileSystem, "/addition/mods.txt", "mods");
            addDirToZipLua(fileSystem, "/lua", "Lua");
            addDirToZip(fileSystem, "/db", "db");
            addDirToZip(fileSystem, "/server", "Server");
            addDirToZip(fileSystem, "/statistic", "Statistic");
            if (!b) {
                addSaveOldToZip(fileSystem, "/save_old/map_t.bin", "map_t.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_ver.bin", "map_ver.bin");
                addSaveOldToZip(fileSystem, "/save_old/map.bin", "map.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_sand.bin", "map_sand.bin");
                addSaveOldToZip(fileSystem, "/save_old/reanimated.bin", "reanimated.bin");
                addSaveOldToZip(fileSystem, "/save_old/zombies.ini", "zombies.ini");
                addSaveOldToZip(fileSystem, "/save_old/z_outfits.bin", "z_outfits.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_p.bin", "map_p.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_meta.bin", "map_meta.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_zone.bin", "map_zone.bin");
                addSaveOldToZip(fileSystem, "/save_old/serverid.dat", "serverid.dat");
                addSaveOldToZip(fileSystem, "/save_old/thumb.png", "thumb.png");
                addSaveOldToZip(fileSystem, "/save_old/players.db", "players.db");
                addSaveOldToZip(fileSystem, "/save_old/players.db-journal", "players.db-journal");
                addSaveOldToZip(fileSystem, "/save_old/vehicles.db", "vehicles.db");
                addSaveOldToZip(fileSystem, "/save_old/vehicles.db-journal", "vehicles.db-journal");
                putTextFile(fileSystem, "/save_old/description.txt", getLastSaveDescription());
            }
            else {
                addSaveToZip(fileSystem, "/save/map_t.bin", "map_t.bin");
                addSaveToZip(fileSystem, "/save/map_ver.bin", "map_ver.bin");
                addSaveToZip(fileSystem, "/save/map.bin", "map.bin");
                addSaveToZip(fileSystem, "/save/map_sand.bin", "map_sand.bin");
                addSaveToZip(fileSystem, "/save/reanimated.bin", "reanimated.bin");
                addSaveToZip(fileSystem, "/save/zombies.ini", "zombies.ini");
                addSaveToZip(fileSystem, "/save/z_outfits.bin", "z_outfits.bin");
                addSaveToZip(fileSystem, "/save/map_p.bin", "map_p.bin");
                addSaveToZip(fileSystem, "/save/map_meta.bin", "map_meta.bin");
                addSaveToZip(fileSystem, "/save/map_zone.bin", "map_zone.bin");
                addSaveToZip(fileSystem, "/save/serverid.dat", "serverid.dat");
                addSaveToZip(fileSystem, "/save/thumb.png", "thumb.png");
                addSaveToZip(fileSystem, "/save/players.db", "players.db");
                addSaveToZip(fileSystem, "/save/players.db-journal", "players.db-journal");
                addSaveToZip(fileSystem, "/save/vehicles.db", "vehicles.db");
                addSaveToZip(fileSystem, "/save/vehicles.db-journal", "vehicles.db-journal");
                putTextFile(fileSystem, "/save/description.txt", getCurrentSaveDescription());
            }
            try {
                fileSystem.close();
            }
            catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }
        catch (Exception ex5) {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                }
                catch (IOException ex4) {
                    ex4.printStackTrace();
                }
            }
            ex5.printStackTrace();
        }
    }
    
    private static void copyToZip(final Path path, final Path path2, final Path path3) throws IOException {
        final Path resolve = path.resolve(path2.relativize(path3).toString());
        if (Files.isDirectory(path3, new LinkOption[0])) {
            Files.createDirectories(resolve, (FileAttribute<?>[])new FileAttribute[0]);
        }
        else {
            Files.copy(path3, resolve, new CopyOption[0]);
        }
    }
    
    private static void addToZip(final FileSystem fileSystem, final String s, final String s2) {
        try {
            final Path path = fileSystem.getPath(s, new String[0]);
            Files.createDirectories(path.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            final Path absolutePath = FileSystems.getDefault().getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2), new String[0]).toAbsolutePath();
            Files.deleteIfExists(path);
            if (Files.exists(absolutePath, new LinkOption[0])) {
                Files.copy(absolutePath, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void addSaveToZip(final FileSystem fileSystem, final String s, final String s2) {
        try {
            final Path path = fileSystem.getPath(s, new String[0]);
            Files.createDirectories(path.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            final Path absolutePath = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getFileNameInCurrentSave(s2), new String[0]).toAbsolutePath();
            Files.deleteIfExists(path);
            if (Files.exists(absolutePath, new LinkOption[0])) {
                Files.copy(absolutePath, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void addSaveOldToZip(final FileSystem fileSystem, final String s, final String s2) {
        try {
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator))));
            }
            catch (FileNotFoundException ex2) {
                return;
            }
            final String line = bufferedReader.readLine();
            final String line2 = bufferedReader.readLine();
            bufferedReader.close();
            final Path path = fileSystem.getPath(s, new String[0]);
            Files.createDirectories(path.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            final Path absolutePath = FileSystems.getDefault().getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, line2, File.separator, line, File.separator, s2), new String[0]).toAbsolutePath();
            Files.deleteIfExists(path);
            if (Files.exists(absolutePath, new LinkOption[0])) {
                Files.copy(absolutePath, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static String getLastSaveDescription() {
        try {
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator))));
            }
            catch (FileNotFoundException ex2) {
                return "-";
            }
            final String line = bufferedReader.readLine();
            final String line2 = bufferedReader.readLine();
            bufferedReader.close();
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, line, line2);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "-";
        }
    }
    
    private static String getCurrentSaveDescription() {
        String gameMode = "Sandbox";
        if (Core.GameMode != null) {
            gameMode = Core.GameMode;
        }
        String gameSaveWorld = "-";
        if (Core.GameSaveWorld != null) {
            gameSaveWorld = Core.GameSaveWorld;
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, gameSaveWorld, gameMode);
    }
    
    private static void addDirToZip(final FileSystem fileSystem, final String s, final String s2) {
        try {
            final Path path2 = fileSystem.getPath(s, new String[0]);
            deleteDirectory(fileSystem, path2);
            Files.createDirectories(path2, (FileAttribute<?>[])new FileAttribute[0]);
            final Path path3;
            final Path path4;
            Files.walk(FileSystems.getDefault().getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2), new String[0]).toAbsolutePath(), new FileVisitOption[0]).forEach(path -> {
                try {
                    copyToZip(path3, path4, path);
                }
                catch (IOException cause) {
                    throw new RuntimeException(cause);
                }
            });
        }
        catch (IOException ex) {}
    }
    
    private static void addDirToZipLua(final FileSystem fileSystem, final String s, final String s2) {
        try {
            final Path path2 = fileSystem.getPath(s, new String[0]);
            deleteDirectory(fileSystem, path2);
            Files.createDirectories(path2, (FileAttribute<?>[])new FileAttribute[0]);
            final Path path3;
            final Path path4;
            Files.walk(FileSystems.getDefault().getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2), new String[0]).toAbsolutePath(), new FileVisitOption[0]).forEach(path -> {
                try {
                    if (!path.endsWith("ServerList.txt") && !path.endsWith("ServerListSteam.txt")) {
                        copyToZip(path3, path4, path);
                    }
                }
                catch (IOException cause) {
                    throw new RuntimeException(cause);
                }
            });
        }
        catch (IOException ex) {}
    }
    
    private static void addFilelistToZip(final FileSystem fileSystem, final String s, final String s2) {
        try {
            final Path path = fileSystem.getPath(s, new String[0]);
            final String s3 = Files.list(FileSystems.getDefault().getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2), new String[0]).toAbsolutePath()).map((Function<? super Path, ?>)Path::getFileName).map((Function<? super Object, ?>)Path::toString).collect((Collector<? super Object, ?, String>)Collectors.joining("; "));
            Files.deleteIfExists(path);
            Files.write(path, s3.getBytes(), new OpenOption[0]);
        }
        catch (IOException ex) {}
    }
    
    static void deleteDirectory(final FileSystem fileSystem, final Path path) {
        ZipLogs.filePaths.clear();
        getDirectoryFiles(path);
        for (final String s : ZipLogs.filePaths) {
            try {
                Files.delete(fileSystem.getPath(s, new String[0]));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    static void getDirectoryFiles(final Path start) {
        try {
            Files.walk(start, new FileVisitOption[0]).forEach(path -> {
                if (!path.toString().equals(start.toString())) {
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        getDirectoryFiles(path);
                    }
                    else if (!ZipLogs.filePaths.contains(path.toString())) {
                        ZipLogs.filePaths.add(path.toString());
                    }
                }
                return;
            });
            ZipLogs.filePaths.add(start.toString());
        }
        catch (IOException ex) {}
    }
    
    private static void addLogToZip(final FileSystem fileSystem, final String s, final String s2, final long n) {
        long checksum;
        try {
            checksum = MD5Checksum.createChecksum(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2));
        }
        catch (Exception ex2) {
            checksum = 0L;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2));
        if (file.exists() && !file.isDirectory() && checksum != n) {
            try {
                Files.delete(fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), new String[0]));
            }
            catch (Exception ex3) {}
            for (int i = 5; i > 0; --i) {
                final Path path = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, i), new String[0]);
                final Path path2 = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, i + 1), new String[0]);
                try {
                    Files.move(path, path2, new CopyOption[0]);
                }
                catch (Exception ex4) {}
            }
            try {
                final Path path3 = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), new String[0]);
                Files.createDirectories(path3.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
                Files.copy(FileSystems.getDefault().getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s2), new String[0]).toAbsolutePath(), path3, StandardCopyOption.REPLACE_EXISTING);
                final Path path4 = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2), new String[0]);
                Files.createDirectories(path4.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
                try {
                    Files.delete(path4);
                }
                catch (Exception ex5) {}
                Files.write(path4, String.valueOf(checksum).getBytes(), new OpenOption[0]);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static void addDebugLogToZip(final FileSystem fileSystem, final String s, final String s2, final long n) {
        String pathname = null;
        final String[] list = new File(LoggerManager.getLogsDir()).list();
        for (int i = 0; i < list.length; ++i) {
            final String s3 = list[i];
            if (s3.contains("DebugLog.txt")) {
                pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LoggerManager.getLogsDir(), File.separator, s3);
                break;
            }
        }
        if (pathname == null) {
            return;
        }
        long checksum;
        try {
            checksum = MD5Checksum.createChecksum(pathname);
        }
        catch (Exception ex2) {
            checksum = 0L;
        }
        final File file = new File(pathname);
        if (file.exists() && !file.isDirectory() && checksum != n) {
            try {
                Files.delete(fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), new String[0]));
            }
            catch (Exception ex3) {}
            for (int j = 5; j > 0; --j) {
                final Path path = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, j), new String[0]);
                final Path path2 = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, j + 1), new String[0]);
                try {
                    Files.move(path, path2, new CopyOption[0]);
                }
                catch (Exception ex4) {}
            }
            try {
                final Path path3 = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), new String[0]);
                Files.createDirectories(path3.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
                Files.copy(FileSystems.getDefault().getPath(pathname, new String[0]).toAbsolutePath(), path3, StandardCopyOption.REPLACE_EXISTING);
                final Path path4 = fileSystem.getPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2), new String[0]);
                Files.createDirectories(path4.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
                try {
                    Files.delete(path4);
                }
                catch (Exception ex5) {}
                Files.write(path4, String.valueOf(checksum).getBytes(), new OpenOption[0]);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static long getMD5FromZip(final FileSystem fileSystem, final String s) {
        long long1 = 0L;
        try {
            final Path path = fileSystem.getPath(s, new String[0]);
            if (Files.exists(path, new LinkOption[0])) {
                long1 = Long.parseLong(Files.readAllLines(path).get(0));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return long1;
    }
    
    private static void putTextFile(final FileSystem fileSystem, final String s, final String s2) {
        try {
            final Path path = fileSystem.getPath(s, new String[0]);
            Files.createDirectories(path.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            try {
                Files.delete(path);
            }
            catch (Exception ex2) {}
            Files.write(path, s2.getBytes(), new OpenOption[0]);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        ZipLogs.filePaths = new ArrayList<String>();
    }
}
