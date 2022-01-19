// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.IPooledObject;
import java.util.Iterator;
import java.util.Map;
import zombie.util.list.PZArrayUtil;
import java.util.function.Consumer;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;
import java.util.HashMap;
import zombie.core.skinnedmodel.animation.debug.GenericNameValueRecordingFrame;

public final class GameProfileRecording extends GenericNameValueRecordingFrame
{
    private long m_startTime;
    private final Row m_rootRow;
    private final HashMap<String, Integer> m_keyValueTable;
    protected PrintStream m_outSegment;
    private long m_firstFrameNo;
    private final List<String> m_segmentFilePaths;
    private int m_numFramesPerFile;
    private int m_currentSegmentFrameCount;
    
    public GameProfileRecording(final String s) {
        super(s, "_times");
        this.m_rootRow = new Row();
        this.m_keyValueTable = new HashMap<String, Integer>();
        this.m_outSegment = null;
        this.m_firstFrameNo = -1L;
        this.m_segmentFilePaths = new ArrayList<String>();
        this.m_numFramesPerFile = 60;
        this.m_currentSegmentFrameCount = 0;
        this.addColumnInternal("StartTime");
        this.addColumnInternal("EndTime");
        this.addColumnInternal("SegmentNo");
        this.addColumnInternal("Spans");
        this.addColumnInternal("key");
        this.addColumnInternal("Depth");
        this.addColumnInternal("StartTime");
        this.addColumnInternal("EndTime");
        this.addColumnInternal("Time Format");
        this.addColumnInternal("x * 100ns");
    }
    
    public void setNumFramesPerSegment(final int numFramesPerFile) {
        this.m_numFramesPerFile = numFramesPerFile;
    }
    
    public void setStartTime(final long startTime) {
        this.m_startTime = startTime;
    }
    
    public void logTimeSpan(final GameProfiler.ProfileArea profileArea) {
        if (this.m_firstFrameNo == -1L) {
            this.m_firstFrameNo = this.m_frameNumber;
        }
        final Span allocSpan = this.allocSpan(profileArea);
        final Row rootRow = this.m_rootRow;
        if (rootRow.Spans.isEmpty()) {
            rootRow.StartTime = allocSpan.StartTime;
        }
        rootRow.EndTime = allocSpan.EndTime;
        rootRow.Spans.add(allocSpan);
    }
    
    protected Span allocSpan(final GameProfiler.ProfileArea profileArea) {
        final int orCreateKey = this.getOrCreateKey(profileArea.Key);
        final long startTime = profileArea.StartTime - this.m_startTime;
        final long endTime = profileArea.EndTime - this.m_startTime;
        final Span alloc = Span.alloc();
        alloc.key = orCreateKey;
        alloc.Depth = profileArea.Depth;
        alloc.StartTime = startTime;
        alloc.EndTime = endTime;
        for (int i = 0; i < profileArea.Children.size(); ++i) {
            alloc.Children.add(this.allocSpan(profileArea.Children.get(i)));
        }
        return alloc;
    }
    
    private int getOrCreateKey(final String s) {
        Integer value = this.m_keyValueTable.get(s);
        if (value == null) {
            value = this.m_keyValueTable.size();
            this.m_keyValueTable.put(s, value);
            this.m_headerDirty = true;
        }
        return value;
    }
    
    @Override
    public String getValueAt(final int n) {
        throw new RuntimeException("Not implemented. Use getValueAt(row, col)");
    }
    
    @Override
    protected void onColumnAdded() {
    }
    
    @Override
    public void reset() {
        this.m_rootRow.reset();
    }
    
    protected void openSegmentFile(final boolean b) {
        if (this.m_outSegment != null) {
            this.m_outSegment.flush();
            this.m_outSegment.close();
        }
        final String format = String.format("%s%s_%04d", this.m_fileKey, this.m_valuesFileNameSuffix, this.m_segmentFilePaths.size());
        final List<String> segmentFilePaths = this.m_segmentFilePaths;
        Objects.requireNonNull(segmentFilePaths);
        this.m_outSegment = AnimationPlayerRecorder.openFileStream(format, b, (Consumer<String>)segmentFilePaths::add);
        this.m_currentSegmentFrameCount = 0;
        this.m_headerDirty = true;
    }
    
    @Override
    public void close() {
        if (this.m_outSegment != null) {
            this.m_outSegment.close();
            this.m_outSegment = null;
        }
    }
    
    @Override
    public void closeAndDiscard() {
        super.closeAndDiscard();
        final List<String> segmentFilePaths = this.m_segmentFilePaths;
        final ZomboidFileSystem instance = ZomboidFileSystem.instance;
        Objects.requireNonNull(instance);
        PZArrayUtil.forEach((List<Object>)segmentFilePaths, (Consumer<? super Object>)instance::tryDeleteFile);
        this.m_segmentFilePaths.clear();
    }
    
    @Override
    protected void writeData() {
        if (this.m_outValues == null) {
            this.openValuesFile(false);
        }
        final StringBuilder lineBuffer = this.m_lineBuffer;
        lineBuffer.setLength(0);
        ++this.m_currentSegmentFrameCount;
        if (this.m_outSegment == null || this.m_currentSegmentFrameCount >= this.m_numFramesPerFile) {
            this.openSegmentFile(false);
        }
        this.writeDataRow(lineBuffer, this.m_rootRow);
        this.m_outSegment.print(this.m_frameNumber);
        this.m_outSegment.println(lineBuffer);
        final StringBuilder lineBuffer2 = this.m_lineBuffer;
        lineBuffer2.setLength(0);
        this.writeFrameTimeRow(lineBuffer2, this.m_rootRow, this.m_segmentFilePaths.size() - 1);
        this.m_outValues.print(this.m_frameNumber);
        this.m_outValues.println(lineBuffer2);
    }
    
    private void writeDataRow(final StringBuilder sb, final Row row) {
        for (int i = 0; i < row.Spans.size(); ++i) {
            this.writeSpan(sb, row, row.Spans.get(i));
        }
    }
    
    private void writeFrameTimeRow(final StringBuilder sb, final Row row, final int n) {
        GenericNameValueRecordingFrame.appendCell(sb, row.StartTime / 100L);
        GenericNameValueRecordingFrame.appendCell(sb, row.EndTime / 100L);
        GenericNameValueRecordingFrame.appendCell(sb, n);
    }
    
    private void writeSpan(final StringBuilder sb, final Row row, final Span span) {
        final long n = (span.StartTime - row.StartTime) / 100L;
        final long n2 = (span.EndTime - span.StartTime) / 100L;
        GenericNameValueRecordingFrame.appendCell(sb, span.key);
        GenericNameValueRecordingFrame.appendCell(sb, span.Depth);
        GenericNameValueRecordingFrame.appendCell(sb, n);
        GenericNameValueRecordingFrame.appendCell(sb, n2);
        for (int i = 0; i < span.Children.size(); ++i) {
            this.writeSpan(sb, row, span.Children.get(i));
        }
    }
    
    @Override
    protected void writeHeader() {
        super.writeHeader();
        this.m_outHeader.println();
        this.m_outHeader.println("Segmentation Info");
        this.m_outHeader.println(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.m_firstFrameNo));
        this.m_outHeader.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.m_numFramesPerFile));
        this.m_outHeader.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.m_segmentFilePaths.size()));
        this.m_outHeader.println();
        this.m_outHeader.println("KeyNamesTable");
        this.m_outHeader.println("Index,Name");
        final StringBuilder x = new StringBuilder();
        for (final Map.Entry<String, Integer> entry : this.m_keyValueTable.entrySet()) {
            x.setLength(0);
            x.append(entry.getValue());
            x.append(",");
            x.append(entry.getKey());
            this.m_outHeader.println(x);
        }
    }
    
    public static class Row
    {
        long StartTime;
        long EndTime;
        final List<Span> Spans;
        
        public Row() {
            this.Spans = new ArrayList<Span>();
        }
        
        public void reset() {
            IPooledObject.release(this.Spans);
        }
    }
    
    public static class Span extends PooledObject
    {
        int key;
        int Depth;
        long StartTime;
        long EndTime;
        final List<Span> Children;
        private static final Pool<Span> s_pool;
        
        public Span() {
            this.Children = new ArrayList<Span>();
        }
        
        @Override
        public void onReleased() {
            super.onReleased();
            IPooledObject.release(this.Children);
        }
        
        public static Span alloc() {
            return Span.s_pool.alloc();
        }
        
        static {
            s_pool = new Pool<Span>(Span::new);
        }
    }
}
