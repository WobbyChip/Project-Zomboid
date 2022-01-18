// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Arrays;
import java.io.File;
import zombie.ChunkMapFilenames;
import java.io.EOFException;
import zombie.util.BufferedRandomAccessFile;
import java.io.IOException;
import zombie.core.logger.ExceptionLogger;
import java.io.RandomAccessFile;
import gnu.trove.list.array.TIntArrayList;
import zombie.popman.ObjectPool;
import java.util.ArrayList;
import java.util.HashMap;

public class IsoLot
{
    public static final HashMap<String, LotHeader> InfoHeaders;
    public static final ArrayList<String> InfoHeaderNames;
    public static final HashMap<String, String> InfoFileNames;
    public static final ObjectPool<IsoLot> pool;
    private String m_lastUsedPath;
    public int wx;
    public int wy;
    final int[] m_offsetInData;
    final TIntArrayList m_data;
    private RandomAccessFile m_in;
    LotHeader info;
    
    public IsoLot() {
        this.m_lastUsedPath = "";
        this.wx = 0;
        this.wy = 0;
        this.m_offsetInData = new int[800];
        this.m_data = new TIntArrayList();
        this.m_in = null;
    }
    
    public static void Dispose() {
        IsoLot.InfoHeaders.clear();
        IsoLot.InfoHeaderNames.clear();
        IsoLot.InfoFileNames.clear();
        final RandomAccessFile in;
        IsoLot.pool.forEach(isoLot -> {
            in = isoLot.m_in;
            if (in != null) {
                isoLot.m_in = null;
                try {
                    in.close();
                }
                catch (IOException ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        });
    }
    
    public static String readString(final BufferedRandomAccessFile bufferedRandomAccessFile) throws EOFException, IOException {
        return bufferedRandomAccessFile.getNextLine();
    }
    
    public static int readInt(final RandomAccessFile randomAccessFile) throws EOFException, IOException {
        final int read = randomAccessFile.read();
        final int read2 = randomAccessFile.read();
        final int read3 = randomAccessFile.read();
        final int read4 = randomAccessFile.read();
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return (read << 0) + (read2 << 8) + (read3 << 16) + (read4 << 24);
    }
    
    public static int readShort(final RandomAccessFile randomAccessFile) throws EOFException, IOException {
        final int read = randomAccessFile.read();
        final int read2 = randomAccessFile.read();
        if ((read | read2) < 0) {
            throw new EOFException();
        }
        return (read << 0) + (read2 << 8);
    }
    
    public static synchronized void put(final IsoLot isoLot) {
        isoLot.info = null;
        isoLot.m_data.resetQuick();
        IsoLot.pool.release(isoLot);
    }
    
    public static synchronized IsoLot get(final Integer n, final Integer n2, final Integer n3, final Integer n4, final IsoChunk isoChunk) {
        final IsoLot isoLot = IsoLot.pool.alloc();
        isoLot.load(n, n2, n3, n4, isoChunk);
        return isoLot;
    }
    
    public void load(final Integer n, final Integer n2, final Integer n3, final Integer n4, final IsoChunk isoChunk) {
        this.info = IsoLot.InfoHeaders.get(ChunkMapFilenames.instance.getHeader(n, n2));
        this.wx = n3;
        this.wy = n4;
        isoChunk.lotheader = this.info;
        try {
            final File file = new File(IsoLot.InfoFileNames.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/String;, n, n2)));
            if (this.m_in == null || !this.m_lastUsedPath.equals(file.getAbsolutePath())) {
                if (this.m_in != null) {
                    this.m_in.close();
                }
                this.m_in = new BufferedRandomAccessFile(file.getAbsolutePath(), "r", 4096);
                this.m_lastUsedPath = file.getAbsolutePath();
            }
            int int1 = 0;
            this.m_in.seek(4 + ((this.wx - n * 30) * 30 + (this.wy - n2 * 30)) * 8);
            this.m_in.seek(readInt(this.m_in));
            this.m_data.resetQuick();
            for (int min = Math.min(this.info.levels, 8), i = 0; i < min; ++i) {
                for (int j = 0; j < 10; ++j) {
                    for (int k = 0; k < 10; ++k) {
                        final int n5 = j + k * 10 + i * 100;
                        this.m_offsetInData[n5] = -1;
                        if (int1 > 0) {
                            --int1;
                        }
                        else {
                            final int int2 = readInt(this.m_in);
                            if (int2 == -1) {
                                int1 = readInt(this.m_in);
                                if (int1 > 0) {
                                    --int1;
                                    continue;
                                }
                            }
                            if (int2 > 1) {
                                this.m_offsetInData[n5] = this.m_data.size();
                                this.m_data.add(int2 - 1);
                                readInt(this.m_in);
                                for (int l = 1; l < int2; ++l) {
                                    this.m_data.add(readInt(this.m_in));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            Arrays.fill(this.m_offsetInData, -1);
            this.m_data.resetQuick();
            ExceptionLogger.logException(ex);
        }
    }
    
    static {
        InfoHeaders = new HashMap<String, LotHeader>();
        InfoHeaderNames = new ArrayList<String>();
        InfoFileNames = new HashMap<String, String>();
        pool = new ObjectPool<IsoLot>(IsoLot::new);
    }
}
