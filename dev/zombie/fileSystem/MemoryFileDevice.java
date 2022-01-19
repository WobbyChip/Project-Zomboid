// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;

public final class MemoryFileDevice implements IFileDevice
{
    @Override
    public IFile createFile(final IFile file) {
        return new MemoryFile(file, this);
    }
    
    @Override
    public void destroyFile(final IFile file) {
    }
    
    @Override
    public InputStream createStream(final String s, final InputStream inputStream) throws IOException {
        return null;
    }
    
    @Override
    public void destroyStream(final InputStream inputStream) {
    }
    
    @Override
    public String name() {
        return "memory";
    }
    
    private static class MemoryFile implements IFile
    {
        final MemoryFileDevice m_device;
        byte[] m_buffer;
        long m_size;
        long m_pos;
        IFile m_file;
        boolean m_write;
        
        MemoryFile(final IFile file, final MemoryFileDevice device) {
            this.m_device = device;
            this.m_buffer = null;
            this.m_size = 0L;
            this.m_pos = 0L;
            this.m_file = file;
            this.m_write = false;
        }
        
        @Override
        public boolean open(final String s, final int n) {
            assert this.m_buffer == null;
            this.m_write = ((n & 0x2) != 0x0);
            if (this.m_file != null) {
                if (this.m_file.open(s, n)) {
                    if ((n & 0x1) != 0x0) {
                        this.m_size = this.m_file.size();
                        this.m_buffer = new byte[(int)this.m_size];
                        this.m_file.read(this.m_buffer, this.m_size);
                        this.m_pos = 0L;
                    }
                    return true;
                }
            }
            else if ((n & 0x2) != 0x0) {
                return true;
            }
            return false;
        }
        
        @Override
        public void close() {
            if (this.m_file != null) {
                if (this.m_write) {
                    this.m_file.seek(FileSeekMode.BEGIN, 0L);
                    this.m_file.write(this.m_buffer, this.m_size);
                }
                this.m_file.close();
            }
            this.m_buffer = null;
        }
        
        @Override
        public boolean read(final byte[] array, final long n) {
            final long n2 = (this.m_pos + n < this.m_size) ? n : (this.m_size - this.m_pos);
            System.arraycopy(this.m_buffer, (int)this.m_pos, array, 0, (int)n2);
            this.m_pos += n2;
            return false;
        }
        
        @Override
        public boolean write(final byte[] array, final long n) {
            final long pos = this.m_pos;
            final long n2 = this.m_buffer.length;
            final long size = this.m_size;
            if (pos + n > n2) {
                this.m_buffer = Arrays.copyOf(this.m_buffer, (int)Math.max(n2 * 2L, pos + n));
            }
            System.arraycopy(array, 0, this.m_buffer, (int)pos, (int)n);
            this.m_pos += n;
            this.m_size = ((pos + n > size) ? (pos + n) : size);
            return true;
        }
        
        @Override
        public byte[] getBuffer() {
            return this.m_buffer;
        }
        
        @Override
        public long size() {
            return this.m_size;
        }
        
        @Override
        public boolean seek(final FileSeekMode fileSeekMode, final long pos) {
            switch (fileSeekMode) {
                case BEGIN: {
                    assert pos <= this.m_size;
                    this.m_pos = pos;
                    break;
                }
                case CURRENT: {
                    assert 0L <= this.m_pos + pos && this.m_pos + pos <= this.m_size;
                    this.m_pos += pos;
                    break;
                }
                case END: {
                    assert pos <= this.m_size;
                    this.m_pos = this.m_size - pos;
                    break;
                }
            }
            final boolean b = this.m_pos <= this.m_size;
            this.m_pos = Math.min(this.m_pos, this.m_size);
            return b;
        }
        
        @Override
        public long pos() {
            return this.m_pos;
        }
        
        @Override
        public InputStream getInputStream() {
            if (this.m_file != null) {
                return this.m_file.getInputStream();
            }
            return null;
        }
        
        @Override
        public IFileDevice getDevice() {
            return this.m_device;
        }
        
        @Override
        public void release() {
            this.m_buffer = null;
        }
    }
}
