// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.debug;

import zombie.ZomboidFileSystem;
import zombie.util.list.PZArrayUtil;
import java.io.PrintStream;
import java.util.HashMap;

public abstract class GenericNameValueRecordingFrame
{
    protected String[] m_columnNames;
    protected final HashMap<String, Integer> m_nameIndices;
    protected boolean m_headerDirty;
    protected final String m_fileKey;
    protected PrintStream m_outHeader;
    protected PrintStream m_outValues;
    private String m_headerFilePath;
    private String m_valuesFilePath;
    protected int m_frameNumber;
    protected static final String delim = ",";
    protected final String m_valuesFileNameSuffix;
    private String m_previousLine;
    private int m_previousFrameNo;
    protected final StringBuilder m_lineBuffer;
    
    public GenericNameValueRecordingFrame(final String fileKey, final String valuesFileNameSuffix) {
        this.m_columnNames = new String[0];
        this.m_nameIndices = new HashMap<String, Integer>();
        this.m_headerDirty = false;
        this.m_outHeader = null;
        this.m_outValues = null;
        this.m_headerFilePath = null;
        this.m_valuesFilePath = null;
        this.m_frameNumber = -1;
        this.m_previousLine = null;
        this.m_previousFrameNo = -1;
        this.m_lineBuffer = new StringBuilder();
        this.m_fileKey = fileKey;
        this.m_valuesFileNameSuffix = valuesFileNameSuffix;
    }
    
    protected int addColumnInternal(final String key) {
        final int length = this.m_columnNames.length;
        this.m_columnNames = PZArrayUtil.add(this.m_columnNames, key);
        this.m_nameIndices.put(key, length);
        this.m_headerDirty = true;
        this.onColumnAdded();
        return length;
    }
    
    public int getOrCreateColumn(final String s) {
        if (this.m_nameIndices.containsKey(s)) {
            return this.m_nameIndices.get(s);
        }
        return this.addColumnInternal(s);
    }
    
    public void setFrameNumber(final int frameNumber) {
        this.m_frameNumber = frameNumber;
    }
    
    public int getColumnCount() {
        return this.m_columnNames.length;
    }
    
    public String getNameAt(final int n) {
        return this.m_columnNames[n];
    }
    
    public abstract String getValueAt(final int p0);
    
    protected void openHeader(final boolean b) {
        this.m_outHeader = AnimationPlayerRecorder.openFileStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.m_fileKey), b, headerFilePath -> this.m_headerFilePath = headerFilePath);
    }
    
    protected void openValuesFile(final boolean b) {
        this.m_outValues = AnimationPlayerRecorder.openFileStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.m_fileKey, this.m_valuesFileNameSuffix), b, valuesFilePath -> this.m_valuesFilePath = valuesFilePath);
    }
    
    public void writeLine() {
        if (this.m_headerDirty || this.m_outHeader == null) {
            this.m_headerDirty = false;
            this.writeHeader();
        }
        this.writeData();
    }
    
    public void close() {
        if (this.m_outHeader != null) {
            this.m_outHeader.close();
            this.m_outHeader = null;
        }
        if (this.m_outValues != null) {
            this.m_outValues.close();
            this.m_outValues = null;
        }
    }
    
    public void closeAndDiscard() {
        this.close();
        ZomboidFileSystem.instance.tryDeleteFile(this.m_headerFilePath);
        this.m_headerFilePath = null;
        ZomboidFileSystem.instance.tryDeleteFile(this.m_valuesFilePath);
        this.m_valuesFilePath = null;
    }
    
    protected abstract void onColumnAdded();
    
    public abstract void reset();
    
    protected void writeHeader() {
        final StringBuilder x = new StringBuilder();
        x.append("frameNo");
        this.writeHeader(x);
        this.openHeader(false);
        this.m_outHeader.println(x);
    }
    
    protected void writeHeader(final StringBuilder sb) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            appendCell(sb, this.getNameAt(i));
        }
    }
    
    protected void writeData() {
        if (this.m_outValues == null) {
            this.openValuesFile(false);
        }
        final StringBuilder lineBuffer = this.m_lineBuffer;
        lineBuffer.setLength(0);
        this.writeData(lineBuffer);
        if (this.m_previousLine != null && this.m_previousLine.contentEquals(lineBuffer)) {
            return;
        }
        this.m_outValues.print(this.m_frameNumber);
        this.m_outValues.println(lineBuffer);
        this.m_previousLine = lineBuffer.toString();
        this.m_previousFrameNo = this.m_frameNumber;
    }
    
    protected void writeData(final StringBuilder sb) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            appendCell(sb, this.getValueAt(i));
        }
    }
    
    public static StringBuilder appendCell(final StringBuilder sb) {
        return sb.append(",");
    }
    
    public static StringBuilder appendCell(final StringBuilder sb, final String str) {
        return sb.append(",").append(str);
    }
    
    public static StringBuilder appendCell(final StringBuilder sb, final float f) {
        return sb.append(",").append(f);
    }
    
    public static StringBuilder appendCell(final StringBuilder sb, final int i) {
        return sb.append(",").append(i);
    }
    
    public static StringBuilder appendCell(final StringBuilder sb, final long lng) {
        return sb.append(",").append(lng);
    }
    
    public static StringBuilder appendCellQuot(final StringBuilder sb, final String str) {
        return sb.append(",").append('\"').append(str).append('\"');
    }
}
