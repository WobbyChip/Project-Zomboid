// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.util.PZXmlParserException;
import zombie.debug.DebugLog;
import zombie.util.PZXmlUtil;
import java.io.File;
import java.util.function.Predicate;

public final class PredicatedFileWatcher
{
    private final String m_path;
    private final Predicate<String> m_predicate;
    private final IPredicatedFileWatcherCallback m_callback;
    
    public PredicatedFileWatcher(final Predicate<String> predicate, final IPredicatedFileWatcherCallback predicatedFileWatcherCallback) {
        this(null, predicate, predicatedFileWatcherCallback);
    }
    
    public PredicatedFileWatcher(final String s, final IPredicatedFileWatcherCallback predicatedFileWatcherCallback) {
        this(s, null, predicatedFileWatcherCallback);
    }
    
    public <T> PredicatedFileWatcher(final String s, final Class<T> clazz, final IPredicatedDataPacketFileWatcherCallback<T> predicatedDataPacketFileWatcherCallback) {
        this(s, null, new GenericPredicatedFileWatcherCallback<Object>(clazz, predicatedDataPacketFileWatcherCallback));
    }
    
    public PredicatedFileWatcher(final String s, final Predicate<String> predicate, final IPredicatedFileWatcherCallback callback) {
        this.m_path = this.processPath(s);
        this.m_predicate = ((predicate != null) ? predicate : this::pathsEqual);
        this.m_callback = callback;
    }
    
    public String getPath() {
        return this.m_path;
    }
    
    private String processPath(final String s) {
        if (s != null) {
            return ZomboidFileSystem.processFilePath(s, File.separatorChar);
        }
        return null;
    }
    
    private boolean pathsEqual(final String s) {
        return s.equals(this.m_path);
    }
    
    public void onModified(final String s) {
        if (this.m_predicate.test(s)) {
            this.m_callback.call(s);
        }
    }
    
    public static class GenericPredicatedFileWatcherCallback<T> implements IPredicatedFileWatcherCallback
    {
        private final Class<T> m_class;
        private final IPredicatedDataPacketFileWatcherCallback<T> m_callback;
        
        public GenericPredicatedFileWatcherCallback(final Class<T> class1, final IPredicatedDataPacketFileWatcherCallback<T> callback) {
            this.m_class = class1;
            this.m_callback = callback;
        }
        
        @Override
        public void call(final String s) {
            T parse;
            try {
                parse = PZXmlUtil.parse(this.m_class, s);
            }
            catch (PZXmlParserException ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Lzombie/util/PZXmlParserException;)Ljava/lang/String;, ex));
                return;
            }
            this.m_callback.call(parse);
        }
    }
    
    public interface IPredicatedFileWatcherCallback
    {
        void call(final String p0);
    }
    
    public interface IPredicatedDataPacketFileWatcherCallback<T>
    {
        void call(final T p0);
    }
}
