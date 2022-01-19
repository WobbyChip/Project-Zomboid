// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import java.util.Set;
import jassimp.Jassimp;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTaskCallback;
import jassimp.AiPostProcessSteps;
import java.util.EnumSet;
import zombie.fileSystem.FileTask;

public final class FileTask_LoadAiScene extends FileTask
{
    String m_filename;
    EnumSet<AiPostProcessSteps> m_post_process_step_set;
    
    public FileTask_LoadAiScene(final String filename, final EnumSet<AiPostProcessSteps> post_process_step_set, final IFileTaskCallback fileTaskCallback, final FileSystem fileSystem) {
        super(fileSystem, fileTaskCallback);
        this.m_filename = filename;
        this.m_post_process_step_set = post_process_step_set;
    }
    
    @Override
    public String getErrorMessage() {
        return this.m_filename;
    }
    
    @Override
    public void done() {
        this.m_filename = null;
        this.m_post_process_step_set = null;
    }
    
    @Override
    public Object call() throws Exception {
        return Jassimp.importFile(this.m_filename, (Set)this.m_post_process_step_set);
    }
}
