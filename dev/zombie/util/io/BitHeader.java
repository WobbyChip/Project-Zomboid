// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.io;

import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class BitHeader
{
    private static final ConcurrentLinkedDeque<BitHeaderByte> pool_byte;
    private static final ConcurrentLinkedDeque<BitHeaderShort> pool_short;
    private static final ConcurrentLinkedDeque<BitHeaderInt> pool_int;
    private static final ConcurrentLinkedDeque<BitHeaderLong> pool_long;
    public static boolean DEBUG;
    
    private static BitHeaderBase getHeader(final HeaderSize headerSize, final ByteBuffer byteBuffer, final boolean b) {
        if (headerSize == HeaderSize.Byte) {
            BitHeaderByte bitHeaderByte = BitHeader.pool_byte.poll();
            if (bitHeaderByte == null) {
                bitHeaderByte = new BitHeaderByte();
            }
            bitHeaderByte.setBuffer(byteBuffer);
            bitHeaderByte.setWrite(b);
            return bitHeaderByte;
        }
        if (headerSize == HeaderSize.Short) {
            BitHeaderShort bitHeaderShort = BitHeader.pool_short.poll();
            if (bitHeaderShort == null) {
                bitHeaderShort = new BitHeaderShort();
            }
            bitHeaderShort.setBuffer(byteBuffer);
            bitHeaderShort.setWrite(b);
            return bitHeaderShort;
        }
        if (headerSize == HeaderSize.Integer) {
            BitHeaderInt bitHeaderInt = BitHeader.pool_int.poll();
            if (bitHeaderInt == null) {
                bitHeaderInt = new BitHeaderInt();
            }
            bitHeaderInt.setBuffer(byteBuffer);
            bitHeaderInt.setWrite(b);
            return bitHeaderInt;
        }
        if (headerSize == HeaderSize.Long) {
            BitHeaderLong bitHeaderLong = BitHeader.pool_long.poll();
            if (bitHeaderLong == null) {
                bitHeaderLong = new BitHeaderLong();
            }
            bitHeaderLong.setBuffer(byteBuffer);
            bitHeaderLong.setWrite(b);
            return bitHeaderLong;
        }
        return null;
    }
    
    private BitHeader() {
    }
    
    public static void debug_print() {
        if (BitHeader.DEBUG) {
            DebugLog.log("*********************************************");
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, BitHeader.pool_byte.size()));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, BitHeader.pool_short.size()));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, BitHeader.pool_int.size()));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, BitHeader.pool_long.size()));
        }
    }
    
    public static BitHeaderWrite allocWrite(final HeaderSize headerSize, final ByteBuffer byteBuffer) {
        return allocWrite(headerSize, byteBuffer, false);
    }
    
    public static BitHeaderWrite allocWrite(final HeaderSize headerSize, final ByteBuffer byteBuffer, final boolean b) {
        final BitHeaderBase header = getHeader(headerSize, byteBuffer, true);
        if (!b) {
            header.create();
        }
        return header;
    }
    
    public static BitHeaderRead allocRead(final HeaderSize headerSize, final ByteBuffer byteBuffer) {
        return allocRead(headerSize, byteBuffer, false);
    }
    
    public static BitHeaderRead allocRead(final HeaderSize headerSize, final ByteBuffer byteBuffer, final boolean b) {
        final BitHeaderBase header = getHeader(headerSize, byteBuffer, false);
        if (!b) {
            header.read();
        }
        return header;
    }
    
    static {
        pool_byte = new ConcurrentLinkedDeque<BitHeaderByte>();
        pool_short = new ConcurrentLinkedDeque<BitHeaderShort>();
        pool_int = new ConcurrentLinkedDeque<BitHeaderInt>();
        pool_long = new ConcurrentLinkedDeque<BitHeaderLong>();
        BitHeader.DEBUG = true;
    }
    
    public enum HeaderSize
    {
        Byte, 
        Short, 
        Integer, 
        Long;
        
        private static /* synthetic */ HeaderSize[] $values() {
            return new HeaderSize[] { HeaderSize.Byte, HeaderSize.Short, HeaderSize.Integer, HeaderSize.Long };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public abstract static class BitHeaderBase implements BitHeaderRead, BitHeaderWrite
    {
        protected boolean isWrite;
        protected ByteBuffer buffer;
        protected int start_pos;
        
        public BitHeaderBase() {
            this.start_pos = -1;
        }
        
        protected void setBuffer(final ByteBuffer buffer) {
            this.buffer = buffer;
        }
        
        protected void setWrite(final boolean isWrite) {
            this.isWrite = isWrite;
        }
        
        @Override
        public int getStartPosition() {
            return this.start_pos;
        }
        
        protected void reset() {
            this.buffer = null;
            this.isWrite = false;
            this.start_pos = -1;
            this.reset_header();
        }
        
        @Override
        public abstract int getLen();
        
        @Override
        public abstract void release();
        
        protected abstract void reset_header();
        
        protected abstract void write_header();
        
        protected abstract void read_header();
        
        protected abstract void addflags_header(final int p0);
        
        protected abstract void addflags_header(final long p0);
        
        protected abstract boolean hasflags_header(final int p0);
        
        protected abstract boolean hasflags_header(final long p0);
        
        protected abstract boolean equals_header(final int p0);
        
        protected abstract boolean equals_header(final long p0);
        
        @Override
        public void create() {
            if (this.isWrite) {
                this.start_pos = this.buffer.position();
                this.reset_header();
                this.write_header();
                return;
            }
            throw new RuntimeException("BitHeader -> Cannot write to a non write Header.");
        }
        
        @Override
        public void write() {
            if (this.isWrite) {
                final int position = this.buffer.position();
                this.buffer.position(this.start_pos);
                this.write_header();
                this.buffer.position(position);
                return;
            }
            throw new RuntimeException("BitHeader -> Cannot write to a non write Header.");
        }
        
        @Override
        public void read() {
            if (!this.isWrite) {
                this.start_pos = this.buffer.position();
                this.read_header();
                return;
            }
            throw new RuntimeException("BitHeader -> Cannot read from a non read Header.");
        }
        
        @Override
        public void addFlags(final int n) {
            if (this.isWrite) {
                this.addflags_header(n);
                return;
            }
            throw new RuntimeException("BitHeader -> Cannot set bits on a non write Header.");
        }
        
        @Override
        public void addFlags(final long n) {
            if (this.isWrite) {
                this.addflags_header(n);
                return;
            }
            throw new RuntimeException("BitHeader -> Cannot set bits on a non write Header.");
        }
        
        @Override
        public boolean hasFlags(final int n) {
            return this.hasflags_header(n);
        }
        
        @Override
        public boolean hasFlags(final long n) {
            return this.hasflags_header(n);
        }
        
        @Override
        public boolean equals(final int n) {
            return this.equals_header(n);
        }
        
        @Override
        public boolean equals(final long n) {
            return this.equals_header(n);
        }
    }
    
    public static class BitHeaderByte extends BitHeaderBase
    {
        private ConcurrentLinkedDeque<BitHeaderByte> pool;
        private byte header;
        
        private BitHeaderByte() {
        }
        
        @Override
        public void release() {
            this.reset();
            BitHeader.pool_byte.offer(this);
        }
        
        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }
        
        @Override
        protected void reset_header() {
            this.header = 0;
        }
        
        @Override
        protected void write_header() {
            this.buffer.put(this.header);
        }
        
        @Override
        protected void read_header() {
            this.header = this.buffer.get();
        }
        
        @Override
        protected void addflags_header(final int n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected void addflags_header(final long n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final int n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final long n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean equals_header(final int n) {
            return this.header == n;
        }
        
        @Override
        protected boolean equals_header(final long n) {
            return this.header == n;
        }
    }
    
    public static class BitHeaderShort extends BitHeaderBase
    {
        private ConcurrentLinkedDeque<BitHeaderShort> pool;
        private short header;
        
        private BitHeaderShort() {
        }
        
        @Override
        public void release() {
            this.reset();
            BitHeader.pool_short.offer(this);
        }
        
        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }
        
        @Override
        protected void reset_header() {
            this.header = 0;
        }
        
        @Override
        protected void write_header() {
            this.buffer.putShort(this.header);
        }
        
        @Override
        protected void read_header() {
            this.header = this.buffer.getShort();
        }
        
        @Override
        protected void addflags_header(final int n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected void addflags_header(final long n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final int n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final long n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean equals_header(final int n) {
            return this.header == n;
        }
        
        @Override
        protected boolean equals_header(final long n) {
            return this.header == n;
        }
    }
    
    public static class BitHeaderInt extends BitHeaderBase
    {
        private ConcurrentLinkedDeque<BitHeaderInt> pool;
        private int header;
        
        private BitHeaderInt() {
        }
        
        @Override
        public void release() {
            this.reset();
            BitHeader.pool_int.offer(this);
        }
        
        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }
        
        @Override
        protected void reset_header() {
            this.header = 0;
        }
        
        @Override
        protected void write_header() {
            this.buffer.putInt(this.header);
        }
        
        @Override
        protected void read_header() {
            this.header = this.buffer.getInt();
        }
        
        @Override
        protected void addflags_header(final int n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected void addflags_header(final long n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final int n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final long n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean equals_header(final int n) {
            return this.header == n;
        }
        
        @Override
        protected boolean equals_header(final long n) {
            return this.header == n;
        }
    }
    
    public static class BitHeaderLong extends BitHeaderBase
    {
        private ConcurrentLinkedDeque<BitHeaderLong> pool;
        private long header;
        
        private BitHeaderLong() {
        }
        
        @Override
        public void release() {
            this.reset();
            BitHeader.pool_long.offer(this);
        }
        
        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }
        
        @Override
        protected void reset_header() {
            this.header = 0L;
        }
        
        @Override
        protected void write_header() {
            this.buffer.putLong(this.header);
        }
        
        @Override
        protected void read_header() {
            this.header = this.buffer.getLong();
        }
        
        @Override
        protected void addflags_header(final int n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected void addflags_header(final long n) {
            this.header = Bits.addFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final int n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean hasflags_header(final long n) {
            return Bits.hasFlags(this.header, n);
        }
        
        @Override
        protected boolean equals_header(final int n) {
            return this.header == n;
        }
        
        @Override
        protected boolean equals_header(final long n) {
            return this.header == n;
        }
    }
}
