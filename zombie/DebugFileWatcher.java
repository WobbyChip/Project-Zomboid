// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.Collection;
import java.util.Iterator;
import java.nio.file.LinkOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import zombie.core.logger.ExceptionLogger;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.nio.file.WatchKey;
import java.nio.file.Path;
import java.util.HashMap;

public final class DebugFileWatcher
{
    private final HashMap<Path, String> m_watchedFiles;
    private final HashMap<WatchKey, Path> m_watchkeyMapping;
    private final ArrayList<PredicatedFileWatcher> m_predicateWatchers;
    private final ArrayList<PredicatedFileWatcher> m_predicateWatchersInvoking;
    private final FileSystem m_fs;
    private WatchService m_watcher;
    private boolean m_predicateWatchersInvokingDirty;
    private long m_modificationTime;
    private final ArrayList<String> m_modifiedFiles;
    public static final DebugFileWatcher instance;
    
    private DebugFileWatcher() {
        this.m_watchedFiles = new HashMap<Path, String>();
        this.m_watchkeyMapping = new HashMap<WatchKey, Path>();
        this.m_predicateWatchers = new ArrayList<PredicatedFileWatcher>();
        this.m_predicateWatchersInvoking = new ArrayList<PredicatedFileWatcher>();
        this.m_fs = FileSystems.getDefault();
        this.m_predicateWatchersInvokingDirty = true;
        this.m_modificationTime = -1L;
        this.m_modifiedFiles = new ArrayList<String>();
    }
    
    public void init() {
        try {
            this.m_watcher = this.m_fs.newWatchService();
            this.registerDirRecursive(this.m_fs.getPath(ZomboidFileSystem.instance.getMediaRootPath(), new String[0]));
            this.registerDirRecursive(this.m_fs.getPath(ZomboidFileSystem.instance.getMessagingDir(), new String[0]));
        }
        catch (IOException ex) {
            this.m_watcher = null;
        }
    }
    
    private void registerDirRecursive(final Path start) {
        try {
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path path, final BasicFileAttributes basicFileAttributes) {
                    DebugFileWatcher.this.registerDir(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException ex) {
            ExceptionLogger.logException(ex);
            this.m_watcher = null;
        }
    }
    
    private void registerDir(final Path value) {
        try {
            this.m_watchkeyMapping.put(value.register(this.m_watcher, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE }), value);
        }
        catch (IOException ex) {
            ExceptionLogger.logException(ex);
            this.m_watcher = null;
        }
    }
    
    private void addWatchedFile(final String value) {
        if (value != null) {
            this.m_watchedFiles.put(this.m_fs.getPath(value, new String[0]), value);
        }
    }
    
    public void add(final PredicatedFileWatcher predicatedFileWatcher) {
        if (!this.m_predicateWatchers.contains(predicatedFileWatcher)) {
            this.addWatchedFile(predicatedFileWatcher.getPath());
            this.m_predicateWatchers.add(predicatedFileWatcher);
            this.m_predicateWatchersInvokingDirty = true;
        }
    }
    
    public void addDirectory(final String s) {
        if (s != null) {
            this.registerDir(this.m_fs.getPath(s, new String[0]));
        }
    }
    
    public void addDirectoryRecurse(final String s) {
        if (s != null) {
            this.registerDirRecursive(this.m_fs.getPath(s, new String[0]));
        }
    }
    
    public void remove(final PredicatedFileWatcher o) {
        this.m_predicateWatchers.remove(o);
    }
    
    public void update() {
        if (this.m_watcher == null) {
            return;
        }
        for (WatchKey watchKey = this.m_watcher.poll(); watchKey != null; watchKey = this.m_watcher.poll()) {
            try {
                final Path path = this.m_watchkeyMapping.getOrDefault(watchKey, null);
                for (final WatchEvent<Path> watchEvent : watchKey.pollEvents()) {
                    if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        final Path resolve = path.resolve(watchEvent.context());
                        final String s = this.m_watchedFiles.getOrDefault(resolve, resolve.toString());
                        this.m_modificationTime = System.currentTimeMillis();
                        if (this.m_modifiedFiles.contains(s)) {
                            continue;
                        }
                        this.m_modifiedFiles.add(s);
                    }
                    else {
                        if (watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE) {
                            continue;
                        }
                        final Path resolve2 = path.resolve(watchEvent.context());
                        if (Files.isDirectory(resolve2, new LinkOption[0])) {
                            this.registerDirRecursive(resolve2);
                        }
                        else {
                            final String s2 = this.m_watchedFiles.getOrDefault(resolve2, resolve2.toString());
                            this.m_modificationTime = System.currentTimeMillis();
                            if (this.m_modifiedFiles.contains(s2)) {
                                continue;
                            }
                            this.m_modifiedFiles.add(s2);
                        }
                    }
                }
            }
            finally {
                if (!watchKey.reset()) {
                    this.m_watchkeyMapping.remove(watchKey);
                }
            }
        }
        if (this.m_modifiedFiles.isEmpty()) {
            return;
        }
        if (this.m_modificationTime + 2000L > System.currentTimeMillis()) {
            return;
        }
        for (int i = this.m_modifiedFiles.size() - 1; i >= 0; --i) {
            final String s3 = this.m_modifiedFiles.remove(i);
            this.swapWatcherArrays();
            final Iterator<PredicatedFileWatcher> iterator2 = this.m_predicateWatchersInvoking.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().onModified(s3);
            }
        }
    }
    
    private void swapWatcherArrays() {
        if (this.m_predicateWatchersInvokingDirty) {
            this.m_predicateWatchersInvoking.clear();
            this.m_predicateWatchersInvoking.addAll(this.m_predicateWatchers);
            this.m_predicateWatchersInvokingDirty = false;
        }
    }
    
    static {
        instance = new DebugFileWatcher();
    }
}
