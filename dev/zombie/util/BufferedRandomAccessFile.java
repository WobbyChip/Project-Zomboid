// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class BufferedRandomAccessFile extends RandomAccessFile
{
    private byte[] buffer;
    private int buf_end;
    private int buf_pos;
    private long real_pos;
    private final int BUF_SIZE;
    
    public BufferedRandomAccessFile(final String name, final String mode, final int buf_SIZE) throws IOException {
        super(name, mode);
        this.buf_end = 0;
        this.buf_pos = 0;
        this.real_pos = 0L;
        this.invalidate();
        this.BUF_SIZE = buf_SIZE;
        this.buffer = new byte[this.BUF_SIZE];
    }
    
    public BufferedRandomAccessFile(final File file, final String mode, final int buf_SIZE) throws IOException {
        super(file, mode);
        this.buf_end = 0;
        this.buf_pos = 0;
        this.real_pos = 0L;
        this.invalidate();
        this.BUF_SIZE = buf_SIZE;
        this.buffer = new byte[this.BUF_SIZE];
    }
    
    @Override
    public final int read() throws IOException {
        if (this.buf_pos >= this.buf_end && this.fillBuffer() < 0) {
            return -1;
        }
        if (this.buf_end == 0) {
            return -1;
        }
        return this.buffer[this.buf_pos++] & 0xFF;
    }
    
    private int fillBuffer() throws IOException {
        final int read = super.read(this.buffer, 0, this.BUF_SIZE);
        if (read >= 0) {
            this.real_pos += read;
            this.buf_end = read;
            this.buf_pos = 0;
        }
        return read;
    }
    
    private void invalidate() throws IOException {
        this.buf_end = 0;
        this.buf_pos = 0;
        this.real_pos = super.getFilePointer();
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 <= this.buf_end - this.buf_pos) {
            System.arraycopy(this.buffer, this.buf_pos, array, n, n2);
            this.buf_pos += n2;
            return n2;
        }
        for (int i = 0; i < n2; ++i) {
            final int read = this.read();
            if (read == -1) {
                return i;
            }
            array[n + i] = (byte)read;
        }
        return n2;
    }
    
    @Override
    public long getFilePointer() throws IOException {
        return this.real_pos - this.buf_end + this.buf_pos;
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        final int n = (int)(this.real_pos - pos);
        if (n >= 0 && n <= this.buf_end) {
            this.buf_pos = this.buf_end - n;
        }
        else {
            super.seek(pos);
            this.invalidate();
        }
    }
    
    public final String getNextLine() throws IOException {
        if (this.buf_end - this.buf_pos <= 0 && this.fillBuffer() < 0) {
            throw new IOException("error in filling buffer!");
        }
        int n = -1;
        for (int i = this.buf_pos; i < this.buf_end; ++i) {
            if (this.buffer[i] == 10) {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            String s;
            if (n > 0 && this.buffer[n - 1] == 13) {
                s = new String(this.buffer, this.buf_pos, n - this.buf_pos - 1, StandardCharsets.UTF_8);
            }
            else {
                s = new String(this.buffer, this.buf_pos, n - this.buf_pos, StandardCharsets.UTF_8);
            }
            this.buf_pos = n + 1;
            return s;
        }
        final StringBuilder sb = new StringBuilder(128);
        int read;
        while ((read = this.read()) != -1 && read != 10) {
            sb.append((char)read);
        }
        if (read == -1 && sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }
}
