// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.io.IOException;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import java.util.Locale;
import zombie.debug.DebugOptions;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;

public abstract class FileTask_AbstractLoadModel extends FileTask
{
    protected String m_fileName;
    private final String m_mediaFilePath;
    private final String m_mediaFileXPath;
    
    protected FileTask_AbstractLoadModel(final FileSystem fileSystem, final IFileTaskCallback fileTaskCallback, final String mediaFilePath, final String mediaFileXPath) {
        super(fileSystem, fileTaskCallback);
        this.m_mediaFilePath = mediaFilePath;
        this.m_mediaFileXPath = mediaFileXPath;
    }
    
    @Override
    public Object call() throws Exception {
        this.checkSlowLoad();
        switch (this.checkExtensionType()) {
            case X: {
                return this.loadX();
            }
            case Fbx: {
                return this.loadFBX();
            }
            case Txt: {
                return this.loadTxt();
            }
            default: {
                return null;
            }
        }
    }
    
    private void checkSlowLoad() {
        if (DebugOptions.instance.AssetSlowLoad.getValue()) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    private ModelFileExtensionType checkExtensionType() {
        final String rawFileName = this.getRawFileName();
        String s = rawFileName.toLowerCase(Locale.ENGLISH);
        if (s.endsWith(".txt")) {
            return ModelFileExtensionType.Txt;
        }
        final boolean startsWith = rawFileName.startsWith("x:");
        if (startsWith) {
            DebugLog.Animation.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, rawFileName));
            s = rawFileName.substring(2);
        }
        if (rawFileName.contains("media/") || rawFileName.contains(".")) {
            this.m_fileName = rawFileName;
            this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
            if (new File(this.m_fileName).exists()) {
                if (this.m_fileName.endsWith(".fbx")) {
                    return ModelFileExtensionType.Fbx;
                }
                if (this.m_fileName.endsWith(".x")) {
                    return ModelFileExtensionType.X;
                }
                return ModelFileExtensionType.X;
            }
        }
        this.m_fileName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.m_mediaFileXPath, s);
        this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
        if (new File(this.m_fileName).exists()) {
            return ModelFileExtensionType.Fbx;
        }
        this.m_fileName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.m_mediaFileXPath, s);
        this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
        if (new File(this.m_fileName).exists()) {
            return ModelFileExtensionType.X;
        }
        if (startsWith) {
            return ModelFileExtensionType.None;
        }
        if (!s.endsWith(".x")) {
            this.m_fileName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.m_mediaFilePath, s);
            if (rawFileName.contains("media/")) {
                this.m_fileName = rawFileName;
            }
            this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
            if (new File(this.m_fileName).exists()) {
                return ModelFileExtensionType.Txt;
            }
        }
        return ModelFileExtensionType.None;
    }
    
    public abstract String getRawFileName();
    
    public abstract ProcessedAiScene loadX() throws IOException;
    
    public abstract ProcessedAiScene loadFBX() throws IOException;
    
    public abstract ModelTxt loadTxt() throws IOException;
}
