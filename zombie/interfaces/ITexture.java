// 
// Decompiled by Procyon v0.5.36
// 

package zombie.interfaces;

import zombie.core.textures.Mask;
import java.nio.ByteBuffer;
import zombie.core.utils.WrappedBuffer;

public interface ITexture extends IDestroyable, IMaskerable
{
    void bind();
    
    void bind(final int p0);
    
    WrappedBuffer getData();
    
    int getHeight();
    
    int getHeightHW();
    
    int getID();
    
    int getWidth();
    
    int getWidthHW();
    
    float getXEnd();
    
    float getXStart();
    
    float getYEnd();
    
    float getYStart();
    
    boolean isSolid();
    
    void makeTransp(final int p0, final int p1, final int p2);
    
    void setAlphaForeach(final int p0, final int p1, final int p2, final int p3);
    
    void setData(final ByteBuffer p0);
    
    void setMask(final Mask p0);
    
    void setRegion(final int p0, final int p1, final int p2, final int p3);
}
