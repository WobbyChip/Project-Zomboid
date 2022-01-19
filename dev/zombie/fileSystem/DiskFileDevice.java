// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import zombie.core.logger.ExceptionLogger;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

public final class DiskFileDevice implements IFileDevice
{
    private final String m_name;
    
    public DiskFileDevice(final String name) {
        this.m_name = name;
    }
    
    @Override
    public IFile createFile(final IFile file) {
        return new DiskFile(file, this);
    }
    
    @Override
    public void destroyFile(final IFile file) {
    }
    
    @Override
    public InputStream createStream(final String name, final InputStream inputStream) throws IOException {
        return new FileInputStream(name);
    }
    
    @Override
    public void destroyStream(final InputStream inputStream) {
    }
    
    @Override
    public String name() {
        return this.m_name;
    }
    
    private static final class DiskFile implements IFile
    {
        final DiskFileDevice m_device;
        RandomAccessFile m_file;
        InputStream m_inputStream;
        final IFile m_fallthrough;
        boolean m_use_fallthrough;
        
        DiskFile(final IFile fallthrough, final DiskFileDevice device) {
            this.m_device = device;
            this.m_fallthrough = fallthrough;
            this.m_use_fallthrough = false;
        }
        
        @Override
        public boolean open(final String name, final int n) {
            final File file = new File(name);
            if ((n & 0x1) != 0x0 && !file.exists() && this.m_fallthrough != null) {
                this.m_use_fallthrough = true;
                return this.m_fallthrough.open(name, n);
            }
            try {
                if ((n & 0x10) == 0x0) {
                    this.m_file = new RandomAccessFile(name, FileOpenMode.toStringMode(n));
                }
                else {
                    this.m_inputStream = new FileInputStream(name);
                }
                return true;
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
                return false;
            }
        }
        
        @Override
        public void close() {
            if (this.m_fallthrough != null) {
                this.m_fallthrough.close();
            }
            if (this.m_file == null && this.m_inputStream == null) {
                return;
            }
            try {
                if (this.m_file != null) {
                    this.m_file.close();
                }
                if (this.m_inputStream != null) {
                    this.m_inputStream.close();
                }
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
            }
            this.m_file = null;
            this.m_inputStream = null;
            this.m_use_fallthrough = false;
        }
        
        @Override
        public boolean read(final byte[] b, final long n) {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.read(b, n);
            }
            if (this.m_file == null) {
                return false;
            }
            try {
                return this.m_file.read(b, 0, (int)n) == n;
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
                return false;
            }
        }
        
        @Override
        public boolean write(final byte[] b, final long n) {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.write(b, n);
            }
            if (this.m_file == null) {
                return false;
            }
            try {
                this.m_file.write(b, 0, (int)n);
                return true;
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
                return false;
            }
        }
        
        @Override
        public byte[] getBuffer() {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.getBuffer();
            }
            return null;
        }
        
        @Override
        public long size() {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.size();
            }
            if (this.m_file == null) {
                return 0L;
            }
            try {
                return this.m_file.length();
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
                return 0L;
            }
        }
        
        @Override
        public boolean seek(final FileSeekMode fileSeekMode, long pos) {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.seek(fileSeekMode, pos);
            }
            if (this.m_file == null) {
                return false;
            }
            try {
                switch (fileSeekMode) {
                    case CURRENT: {
                        pos += this.m_file.getFilePointer();
                        break;
                    }
                    case END: {
                        pos += this.m_file.length();
                        break;
                    }
                }
                this.m_file.seek(pos);
                return true;
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
                return false;
            }
        }
        
        @Override
        public long pos() {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.pos();
            }
            if (this.m_file == null) {
                return 0L;
            }
            try {
                return this.m_file.getFilePointer();
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
                return 0L;
            }
        }
        
        @Override
        public InputStream getInputStream() {
            return this.m_inputStream;
        }
        
        @Override
        public IFileDevice getDevice() {
            return this.m_device;
        }
        
        @Override
        public void release() {
            this.getDevice().destroyFile(this);
        }
    }
}
