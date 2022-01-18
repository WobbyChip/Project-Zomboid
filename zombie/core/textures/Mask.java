// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import zombie.core.opengl.RenderThread;
import zombie.interfaces.ITexture;
import java.nio.ByteBuffer;
import zombie.core.utils.WrappedBuffer;
import zombie.core.utils.BooleanGrid;
import java.io.Serializable;

public final class Mask implements Serializable, Cloneable
{
    private static final long serialVersionUID = -5679205580926696806L;
    private boolean full;
    private int height;
    BooleanGrid mask;
    private int width;
    
    protected Mask() {
    }
    
    public Mask(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.mask = new BooleanGrid(width, height);
        this.full();
    }
    
    public Mask(final Texture texture, final Texture texture2, final int n, final int n2, int width, int height) {
        if (texture.getMask() == null) {
            return;
        }
        width = texture2.getWidth();
        height = texture2.getHeight();
        texture2.setMask(this);
        this.mask = new BooleanGrid(width, height);
        for (int i = n; i < n + width; ++i) {
            for (int j = n2; j < n2 + height; ++j) {
                this.mask.setValue(i - n, j - n2, texture.getMask().mask.getValue(i, j));
            }
        }
    }
    
    public Mask(final Mask mask, final int n, final int n2, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.mask = new BooleanGrid(width, height);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.mask.setValue(i, j, mask.mask.getValue(n + i, n2 + j));
            }
        }
    }
    
    public Mask(final boolean[] array, final int n, final int n2, final int n3, final int n4, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.mask = new BooleanGrid(width, height);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.mask.setValue(i, j, array[n3 + i + (n4 + j) * n]);
            }
        }
    }
    
    public Mask(final BooleanGrid booleanGrid, final int n, final int n2, final int n3, final int n4, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.mask = new BooleanGrid(width, height);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.mask.setValue(i, j, booleanGrid.getValue(n3 + i, n4 + j));
            }
        }
    }
    
    protected Mask(final Texture texture, final WrappedBuffer wrappedBuffer) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        final int widthHW = texture.getWidthHW();
        final int heightHW = texture.getHeightHW();
        final int n = (int)(texture.getXStart() * widthHW);
        final int n2 = (int)(texture.getXEnd() * widthHW);
        final int n3 = (int)(texture.getYStart() * heightHW);
        final int n4 = (int)(texture.getYEnd() * heightHW);
        this.mask = new BooleanGrid(this.width, this.height);
        texture.setMask(this);
        final ByteBuffer buffer = wrappedBuffer.getBuffer();
        buffer.rewind();
        for (int i = 0; i < texture.getHeightHW(); ++i) {
            for (int j = 0; j < texture.getWidthHW(); ++j) {
                buffer.get();
                buffer.get();
                buffer.get();
                final byte value = buffer.get();
                if (j >= n && j < n2 && i >= n3 && i < n4) {
                    if (value == 0) {
                        this.mask.setValue(j - n, i - n3, false);
                        this.full = false;
                    }
                    else {
                        if (value < 127) {
                            this.mask.setValue(j - n, i - n3, true);
                        }
                        this.mask.setValue(j - n, i - n3, true);
                    }
                }
                if (i >= n4) {
                    break;
                }
            }
        }
        wrappedBuffer.dispose();
    }
    
    public Mask(final ITexture texture, final boolean[] array) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        final int widthHW = texture.getWidthHW();
        final int n = (int)(texture.getXStart() * widthHW);
        final int n2 = (int)(texture.getXEnd() * widthHW);
        final int heightHW;
        final int n3 = (int)(texture.getYStart() * (heightHW = texture.getHeightHW()));
        final int n4 = (int)(texture.getYEnd() * heightHW);
        texture.setMask(this);
        this.mask = new BooleanGrid(this.width, this.height);
        for (int i = 0; i < texture.getHeight(); ++i) {
            for (int j = 0; j < texture.getWidth(); ++j) {
                this.mask.setValue(j, i, array[i * texture.getWidth() + j]);
            }
        }
    }
    
    public Mask(final ITexture texture, final BooleanGrid booleanGrid) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        texture.setMask(this);
        this.mask = new BooleanGrid(this.width, this.height);
        for (int i = 0; i < texture.getHeight(); ++i) {
            for (int j = 0; j < texture.getWidth(); ++j) {
                this.mask.setValue(j, i, booleanGrid.getValue(j, i));
            }
        }
    }
    
    public Mask(final ITexture texture) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        final int widthHW = texture.getWidthHW();
        final int n = (int)(texture.getXStart() * widthHW);
        final int n2 = (int)(texture.getXEnd() * widthHW);
        final int heightHW;
        final int n3 = (int)(texture.getYStart() * (heightHW = texture.getHeightHW()));
        final int n4 = (int)(texture.getYEnd() * heightHW);
        texture.setMask(this);
        this.mask = new BooleanGrid(this.width, this.height);
        final WrappedBuffer wrappedBuffer;
        final ByteBuffer byteBuffer;
        int i = 0;
        int j = 0;
        final int n5;
        final int n6;
        final int n7;
        final int n8;
        final int n9;
        RenderThread.invokeOnRenderContext(() -> {
            texture.getData();
            wrappedBuffer.getBuffer();
            byteBuffer.rewind();
            while (i < texture.getHeightHW()) {
                while (j < texture.getWidthHW()) {
                    byteBuffer.get();
                    byteBuffer.get();
                    byteBuffer.get();
                    byteBuffer.get();
                    if (j >= n5 && j < n6 && i >= n7 && i < n8) {
                        if (n9 == 0) {
                            this.mask.setValue(j - n5, i - n7, false);
                            this.full = false;
                        }
                        else {
                            if (n9 < 127) {
                                this.mask.setValue(j - n5, i - n7, true);
                            }
                            this.mask.setValue(j - n5, i - n7, true);
                        }
                    }
                    if (i >= n8) {
                        break;
                    }
                    else {
                        ++j;
                    }
                }
                ++i;
            }
            wrappedBuffer.dispose();
        });
    }
    
    public Mask(final Mask mask) {
        this.width = mask.width;
        this.height = mask.height;
        this.full = mask.full;
        try {
            this.mask = mask.mask.clone();
        }
        catch (CloneNotSupportedException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public Object clone() {
        return new Mask(this);
    }
    
    public void full() {
        this.mask.fill();
        this.full = true;
    }
    
    public void set(final int n, final int n2, final boolean b) {
        this.mask.setValue(n, n2, b);
        if (!b && this.full) {
            this.full = false;
        }
    }
    
    public boolean get(final int n, final int n2) {
        return this.full || this.mask.getValue(n, n2);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.width = objectInputStream.readInt();
        this.height = objectInputStream.readInt();
        this.full = objectInputStream.readBoolean();
        if (objectInputStream.readBoolean()) {
            this.mask = (BooleanGrid)objectInputStream.readObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.width);
        objectOutputStream.writeInt(this.height);
        objectOutputStream.writeBoolean(this.full);
        if (this.mask != null) {
            objectOutputStream.writeBoolean(true);
            objectOutputStream.writeObject(this.mask);
        }
        else {
            objectOutputStream.writeBoolean(false);
        }
    }
    
    public void save(final String s) {
    }
}
