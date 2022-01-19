// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.util.IPooledObject;
import java.util.ArrayList;
import zombie.util.Pool;
import java.util.List;
import zombie.util.PooledObject;
import java.util.function.Supplier;
import java.util.UUID;
import zombie.util.lambda.Stacks;
import zombie.ui.TextManager;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.debug.DebugOptions;
import zombie.core.profiling.TriggerGameProfilerFile;
import java.util.Stack;

public final class GameProfiler
{
    private static final String s_currentSessionUUID;
    private static final ThreadLocal<GameProfiler> s_instance;
    private final Stack<ProfileArea> m_stack;
    private final RecordingFrame m_currentFrame;
    private final RecordingFrame m_previousFrame;
    private boolean m_isInFrame;
    private final GameProfileRecording m_recorder;
    private static final Object m_gameProfilerRecordingTriggerLock;
    private static PredicatedFileWatcher m_gameProfilerRecordingTriggerWatcher;
    
    private GameProfiler() {
        this.m_stack = new Stack<ProfileArea>();
        this.m_currentFrame = new RecordingFrame();
        this.m_previousFrame = new RecordingFrame();
        this.m_recorder = new GameProfileRecording(String.format("%s_GameProfiler_%s", this.getCurrentSessionUUID(), Thread.currentThread().getName().replace("-", "").replace(" ", "")));
    }
    
    private static void onTrigger_setAnimationRecorderTriggerFile(final TriggerGameProfilerFile triggerGameProfilerFile) {
        DebugOptions.instance.GameProfilerEnabled.setValue(triggerGameProfilerFile.isRecording);
    }
    
    private String getCurrentSessionUUID() {
        return GameProfiler.s_currentSessionUUID;
    }
    
    public static GameProfiler getInstance() {
        return GameProfiler.s_instance.get();
    }
    
    public void startFrame(final String frameInvokerKey) {
        if (this.m_isInFrame) {
            throw new RuntimeException("Already inside a frame.");
        }
        this.m_isInFrame = true;
        if (!this.m_stack.empty()) {
            throw new RuntimeException("Recording stack should be empty.");
        }
        final int frameCount = IsoCamera.frameState.frameCount;
        if (this.m_currentFrame.FrameNo != frameCount) {
            this.m_previousFrame.transferFrom(this.m_currentFrame);
            if (this.m_previousFrame.FrameNo != -1) {
                this.m_recorder.writeLine();
            }
            final long timeNs = getTimeNs();
            this.m_currentFrame.FrameNo = frameCount;
            this.m_currentFrame.m_frameInvokerKey = frameInvokerKey;
            this.m_currentFrame.m_startTime = timeNs;
            this.m_recorder.reset();
            this.m_recorder.setFrameNumber(this.m_currentFrame.FrameNo);
            this.m_recorder.setStartTime(this.m_currentFrame.m_startTime);
        }
    }
    
    public void endFrame() {
        this.m_currentFrame.m_endTime = getTimeNs();
        this.m_currentFrame.m_totalTime = this.m_currentFrame.m_endTime - this.m_currentFrame.m_startTime;
        this.m_isInFrame = false;
    }
    
    public void invokeAndMeasureFrame(final String s, final Runnable runnable) {
        if (!isRunning()) {
            runnable.run();
            return;
        }
        this.startFrame(s);
        try {
            this.invokeAndMeasure(s, runnable);
        }
        finally {
            this.endFrame();
        }
    }
    
    public void invokeAndMeasure(final String s, final Runnable runnable) {
        if (!isRunning()) {
            runnable.run();
            return;
        }
        if (!this.m_isInFrame) {
            DebugLog.General.warn((Object)"Not inside in a frame. Find the root caller function for this thread, and add call to invokeAndMeasureFrame.");
            return;
        }
        final ProfileArea start = this.start(s);
        try {
            runnable.run();
        }
        finally {
            this.end(start);
        }
    }
    
    public static boolean isRunning() {
        return DebugOptions.instance.GameProfilerEnabled.getValue();
    }
    
    public <T1> void invokeAndMeasure(final String s, final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
        if (!isRunning()) {
            callback.accept(t1);
            return;
        }
        Lambda.capture(this, s, t1, callback, (genericStack, gameProfiler, s2, o, callback2) -> gameProfiler.invokeAndMeasure(s2, genericStack.invoker(o, callback2)));
    }
    
    public <T1, T2> void invokeAndMeasure(final String s, final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
        if (!isRunning()) {
            callback.accept(t1, t2);
            return;
        }
        Lambda.capture(this, s, t1, t2, callback, (genericStack, gameProfiler, s2, o, o2, callback2) -> gameProfiler.invokeAndMeasure(s2, genericStack.invoker(o, o2, callback2)));
    }
    
    public <T1, T2, T3> void invokeAndMeasure(final String s, final T1 t1, final T2 t2, final T3 t3, final Invokers.Params3.ICallback<T1, T2, T3> callback) {
        if (!isRunning()) {
            callback.accept(t1, t2, t3);
            return;
        }
        Lambda.capture(this, s, t1, t2, t3, callback, (genericStack, gameProfiler, s2, o, o2, o3, callback2) -> gameProfiler.invokeAndMeasure(s2, genericStack.invoker(o, o2, o3, callback2)));
    }
    
    public ProfileArea start(final String key) {
        final long timeNs = getTimeNs();
        final ProfileArea alloc = ProfileArea.alloc();
        alloc.Key = key;
        return this.start(alloc, timeNs);
    }
    
    public ProfileArea start(final ProfileArea profileArea) {
        return this.start(profileArea, getTimeNs());
    }
    
    public ProfileArea start(final ProfileArea item, final long startTime) {
        item.StartTime = startTime;
        item.Depth = this.m_stack.size();
        if (!this.m_stack.isEmpty()) {
            this.m_stack.peek().Children.add(item);
        }
        this.m_stack.push(item);
        return item;
    }
    
    public void end(final ProfileArea profileArea) {
        profileArea.EndTime = getTimeNs();
        profileArea.Total = profileArea.EndTime - profileArea.StartTime;
        if (this.m_stack.peek() != profileArea) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Lzombie/GameProfiler$ProfileArea;Ljava/lang/Object;)Ljava/lang/String;, profileArea, this.m_stack.peek()));
        }
        this.m_stack.pop();
        if (this.m_stack.isEmpty()) {
            this.m_recorder.logTimeSpan(profileArea);
            profileArea.release();
        }
    }
    
    private void renderPercent(final String s, final long n, final int n2, final int n3, final float n4, final float n5, final float n6) {
        final float f = (int)(n / (float)this.m_previousFrame.m_totalTime * 100.0f * 10.0f) / 10.0f;
        TextManager.instance.DrawString(n2, n3, s, n4, n5, n6, 1.0);
        TextManager.instance.DrawString((double)(n2 + 300), (double)n3, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, String.valueOf(f)), (double)n4, (double)n5, (double)n6, 1.0);
    }
    
    public void render(final int n, final int n2) {
        this.renderPercent(this.m_previousFrame.m_frameInvokerKey, this.m_previousFrame.m_totalTime, n, n2, 1.0f, 1.0f, 1.0f);
    }
    
    public static long getTimeNs() {
        return System.nanoTime();
    }
    
    public static void init() {
        initTriggerWatcher();
    }
    
    private static void initTriggerWatcher() {
        if (GameProfiler.m_gameProfilerRecordingTriggerWatcher == null) {
            synchronized (GameProfiler.m_gameProfilerRecordingTriggerLock) {
                if (GameProfiler.m_gameProfilerRecordingTriggerWatcher == null) {
                    GameProfiler.m_gameProfilerRecordingTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_PerformanceProfiler.xml"), (Class<T>)TriggerGameProfilerFile.class, (PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T>)GameProfiler::onTrigger_setAnimationRecorderTriggerFile);
                    DebugFileWatcher.instance.add(GameProfiler.m_gameProfilerRecordingTriggerWatcher);
                }
            }
        }
    }
    
    static {
        s_currentSessionUUID = UUID.randomUUID().toString();
        s_instance = ThreadLocal.withInitial((Supplier<? extends GameProfiler>)GameProfiler::new);
        m_gameProfilerRecordingTriggerLock = "Game Profiler Recording Watcher, synchronization lock";
    }
    
    public static class ProfileArea extends PooledObject
    {
        public String Key;
        public long StartTime;
        public long EndTime;
        public long Total;
        public int Depth;
        public float r;
        public float g;
        public float b;
        public final List<ProfileArea> Children;
        private static final Pool<ProfileArea> s_pool;
        
        public ProfileArea() {
            this.r = 1.0f;
            this.g = 1.0f;
            this.b = 1.0f;
            this.Children = new ArrayList<ProfileArea>();
        }
        
        @Override
        public void onReleased() {
            super.onReleased();
            this.clear();
        }
        
        public void clear() {
            this.StartTime = 0L;
            this.EndTime = 0L;
            this.Total = 0L;
            this.Depth = 0;
            IPooledObject.release(this.Children);
        }
        
        public static ProfileArea alloc() {
            return ProfileArea.s_pool.alloc();
        }
        
        static {
            s_pool = new Pool<ProfileArea>(ProfileArea::new);
        }
    }
    
    public static class RecordingFrame
    {
        private String m_frameInvokerKey;
        private int FrameNo;
        private long m_startTime;
        private long m_endTime;
        private long m_totalTime;
        
        public RecordingFrame() {
            this.m_frameInvokerKey = "";
            this.FrameNo = -1;
            this.m_startTime = 0L;
            this.m_endTime = 0L;
            this.m_totalTime = 0L;
        }
        
        public void transferFrom(final RecordingFrame recordingFrame) {
            this.clear();
            this.FrameNo = recordingFrame.FrameNo;
            this.m_frameInvokerKey = recordingFrame.m_frameInvokerKey;
            this.m_startTime = recordingFrame.m_startTime;
            this.m_endTime = recordingFrame.m_endTime;
            this.m_totalTime = recordingFrame.m_totalTime;
            recordingFrame.clear();
        }
        
        public void clear() {
            this.FrameNo = -1;
            this.m_frameInvokerKey = "";
            this.m_startTime = 0L;
            this.m_endTime = 0L;
            this.m_totalTime = 0L;
        }
    }
}
