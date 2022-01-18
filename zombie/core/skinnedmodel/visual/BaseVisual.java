// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.visual;

import zombie.core.skinnedmodel.model.ModelInstance;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BaseVisual
{
    public abstract void save(final ByteBuffer p0) throws IOException;
    
    public abstract void load(final ByteBuffer p0, final int p1) throws IOException;
    
    public abstract ModelInstance createModelInstance();
}
