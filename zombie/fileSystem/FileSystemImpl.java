// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.util.Collection;
import zombie.core.logger.ExceptionLogger;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import zombie.gameStates.GameLoadingState;
import zombie.GameWindow;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.util.ArrayList;

public final class FileSystemImpl extends FileSystem
{
    private final ArrayList<DeviceList> m_devices;
    private final ArrayList<AsyncItem> m_in_progress;
    private final ArrayList<AsyncItem> m_pending;
    private int m_last_id;
    private DiskFileDevice m_disk_device;
    private MemoryFileDevice m_memory_device;
    private final HashMap<String, TexturePackDevice> m_texturepack_devices;
    private final HashMap<String, DeviceList> m_texturepack_devicelists;
    private DeviceList m_default_device;
    private final ExecutorService executor;
    private final AtomicBoolean lock;
    private final ArrayList<AsyncItem> m_added;
    public static final HashMap<String, Boolean> TexturePackCompression;
    
    public FileSystemImpl() {
        this.m_devices = new ArrayList<DeviceList>();
        this.m_in_progress = new ArrayList<AsyncItem>();
        this.m_pending = new ArrayList<AsyncItem>();
        this.m_last_id = 0;
        this.m_texturepack_devices = new HashMap<String, TexturePackDevice>();
        this.m_texturepack_devicelists = new HashMap<String, DeviceList>();
        this.lock = new AtomicBoolean(false);
        this.m_added = new ArrayList<AsyncItem>();
        this.m_disk_device = new DiskFileDevice("disk");
        this.m_memory_device = new MemoryFileDevice();
        (this.m_default_device = new DeviceList()).add(this.m_disk_device);
        this.m_default_device.add(this.m_memory_device);
        this.executor = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() <= 4) ? 2 : 4);
    }
    
    @Override
    public boolean mount(final IFileDevice fileDevice) {
        return true;
    }
    
    @Override
    public boolean unMount(final IFileDevice o) {
        return this.m_devices.remove(o);
    }
    
    @Override
    public IFile open(final DeviceList list, final String s, final int n) {
        final IFile file = list.createFile();
        if (file == null) {
            return null;
        }
        if (file.open(s, n)) {
            return file;
        }
        file.release();
        return null;
    }
    
    @Override
    public void close(final IFile file) {
        file.close();
        file.release();
    }
    
    @Override
    public int openAsync(final DeviceList list, final String path, final int mode, final IFileTask2Callback cb) {
        final IFile file = list.createFile();
        if (file != null) {
            final OpenTask openTask = new OpenTask(this);
            openTask.m_file = file;
            openTask.m_path = path;
            openTask.m_mode = mode;
            openTask.m_cb = cb;
            return this.runAsync(openTask);
        }
        return -1;
    }
    
    @Override
    public void closeAsync(final IFile file, final IFileTask2Callback cb) {
        final CloseTask closeTask = new CloseTask(this);
        closeTask.m_file = file;
        closeTask.m_cb = cb;
        this.runAsync(closeTask);
    }
    
    @Override
    public void cancelAsync(final int n) {
        if (n == -1) {
            return;
        }
        for (int i = 0; i < this.m_pending.size(); ++i) {
            final AsyncItem asyncItem = this.m_pending.get(i);
            if (asyncItem.m_id == n) {
                asyncItem.m_future.cancel(false);
                return;
            }
        }
        for (int j = 0; j < this.m_in_progress.size(); ++j) {
            final AsyncItem asyncItem2 = this.m_in_progress.get(j);
            if (asyncItem2.m_id == n) {
                asyncItem2.m_future.cancel(false);
                return;
            }
        }
        while (!this.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        for (int k = 0; k < this.m_added.size(); ++k) {
            final AsyncItem asyncItem3 = this.m_added.get(k);
            if (asyncItem3.m_id == n) {
                asyncItem3.m_future.cancel(false);
                break;
            }
        }
        this.lock.set(false);
    }
    
    @Override
    public InputStream openStream(final DeviceList list, final String s) throws IOException {
        return list.createStream(s);
    }
    
    @Override
    public void closeStream(final InputStream inputStream) {
    }
    
    private int runAsync(final AsyncItem e) {
        final Thread currentThread = Thread.currentThread();
        if (currentThread != GameWindow.GameThread && currentThread != GameLoadingState.loader) {}
        while (!this.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        e.m_id = this.m_last_id;
        ++this.m_last_id;
        if (this.m_last_id < 0) {
            this.m_last_id = 0;
        }
        this.m_added.add(e);
        this.lock.set(false);
        return e.m_id;
    }
    
    @Override
    public int runAsync(final FileTask fileTask) {
        final AsyncItem asyncItem = new AsyncItem();
        asyncItem.m_task = fileTask;
        asyncItem.m_future = new FutureTask<Object>(fileTask);
        return this.runAsync(asyncItem);
    }
    
    @Override
    public void updateAsyncTransactions() {
        for (int min = Math.min(this.m_in_progress.size(), 16), i = 0; i < min; ++i) {
            final AsyncItem asyncItem = this.m_in_progress.get(i);
            if (asyncItem.m_future.isDone()) {
                this.m_in_progress.remove(i--);
                --min;
                if (!asyncItem.m_future.isCancelled()) {
                    Object value = null;
                    try {
                        value = asyncItem.m_future.get();
                    }
                    catch (Throwable t) {
                        ExceptionLogger.logException(t, asyncItem.m_task.getErrorMessage());
                    }
                    asyncItem.m_task.handleResult(value);
                }
                asyncItem.m_task.done();
                asyncItem.m_task = null;
                asyncItem.m_future = null;
            }
        }
        while (!this.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        if (true) {
            for (int j = 0; j < this.m_added.size(); ++j) {
                final AsyncItem element = this.m_added.get(j);
                int size = this.m_pending.size();
                for (int k = 0; k < this.m_pending.size(); ++k) {
                    if (element.m_task.m_priority > this.m_pending.get(k).m_task.m_priority) {
                        size = k;
                        break;
                    }
                }
                this.m_pending.add(size, element);
            }
        }
        else {
            this.m_pending.addAll(this.m_added);
        }
        this.m_added.clear();
        this.lock.set(false);
        int n = 16 - this.m_in_progress.size();
        while (n > 0 && !this.m_pending.isEmpty()) {
            final AsyncItem e = this.m_pending.remove(0);
            if (e.m_future.isCancelled()) {
                continue;
            }
            this.m_in_progress.add(e);
            this.executor.submit(e.m_future);
            --n;
        }
    }
    
    @Override
    public boolean hasWork() {
        if (!this.m_pending.isEmpty() || !this.m_in_progress.isEmpty()) {
            return true;
        }
        while (!this.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        final boolean b = !this.m_added.isEmpty();
        this.lock.set(false);
        return b;
    }
    
    @Override
    public DeviceList getDefaultDevice() {
        return this.m_default_device;
    }
    
    @Override
    public void mountTexturePack(final String key, final TexturePackTextures texturePackTextures, final int n) {
        final TexturePackDevice value = new TexturePackDevice(key, n);
        if (texturePackTextures != null) {
            try {
                value.getSubTextureInfo(texturePackTextures);
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
            }
        }
        this.m_texturepack_devices.put(key, value);
        final DeviceList value2 = new DeviceList();
        value2.add(value);
        this.m_texturepack_devicelists.put(value.name(), value2);
    }
    
    @Override
    public DeviceList getTexturePackDevice(final String key) {
        return this.m_texturepack_devicelists.get(key);
    }
    
    @Override
    public int getTexturePackFlags(final String key) {
        return this.m_texturepack_devices.get(key).getTextureFlags();
    }
    
    @Override
    public boolean getTexturePackAlpha(final String key, final String s) {
        return this.m_texturepack_devices.get(key).isAlpha(s);
    }
    
    static {
        TexturePackCompression = new HashMap<String, Boolean>();
    }
    
    private static final class AsyncItem
    {
        int m_id;
        FileTask m_task;
        FutureTask<Object> m_future;
    }
    
    private static final class OpenTask extends FileTask
    {
        IFile m_file;
        String m_path;
        int m_mode;
        IFileTask2Callback m_cb;
        
        OpenTask(final FileSystem fileSystem) {
            super(fileSystem);
        }
        
        @Override
        public Object call() throws Exception {
            return this.m_file.open(this.m_path, this.m_mode);
        }
        
        @Override
        public void handleResult(final Object o) {
            if (this.m_cb != null) {
                this.m_cb.onFileTaskFinished(this.m_file, o);
            }
        }
        
        @Override
        public void done() {
            if ((this.m_mode & 0x5) == 0x5) {
                this.m_file_system.closeAsync(this.m_file, null);
            }
            this.m_file = null;
            this.m_path = null;
            this.m_cb = null;
        }
    }
    
    private static final class CloseTask extends FileTask
    {
        IFile m_file;
        IFileTask2Callback m_cb;
        
        CloseTask(final FileSystem fileSystem) {
            super(fileSystem);
        }
        
        @Override
        public Object call() throws Exception {
            this.m_file.close();
            this.m_file.release();
            return null;
        }
        
        @Override
        public void handleResult(final Object o) {
            if (this.m_cb != null) {
                this.m_cb.onFileTaskFinished(this.m_file, o);
            }
        }
        
        @Override
        public void done() {
            this.m_file = null;
            this.m_cb = null;
        }
    }
}
