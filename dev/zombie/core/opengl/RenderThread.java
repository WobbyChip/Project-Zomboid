// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.profiling.PerformanceProfileFrameProbe;
import zombie.util.lambda.Stacks;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;
import org.lwjglx.LWJGLException;
import org.lwjglx.opengl.Util;
import org.lwjgl.opengl.GL11;
import zombie.core.textures.TextureID;
import zombie.core.sprite.SpriteRenderState;
import zombie.network.MPStatisticClient;
import zombie.ui.FPSGraph;
import zombie.Lua.LuaManager;
import zombie.core.SpriteRenderer;
import org.lwjglx.opengl.OpenGLException;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import java.util.Collection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.core.Clipboard;
import org.lwjglx.input.Controllers;
import zombie.GameWindow;
import zombie.network.GameServer;
import zombie.core.ThreadGroups;
import org.lwjglx.opengl.Display;
import zombie.core.Core;
import java.util.ArrayList;

public class RenderThread
{
    private static Thread MainThread;
    public static Thread RenderThread;
    private static Thread ContextThread;
    private static boolean m_isDisplayCreated;
    private static int m_contextLockReentrantDepth;
    public static final Object m_contextLock;
    private static final ArrayList<RenderContextQueueItem> invokeOnRenderQueue;
    private static final ArrayList<RenderContextQueueItem> invokeOnRenderQueue_Invoking;
    private static boolean m_isInitialized;
    private static final Object m_initLock;
    private static volatile boolean m_isCloseRequested;
    private static volatile int m_displayWidth;
    private static volatile int m_displayHeight;
    private static final boolean s_legacyAllowSingleThreadRendering = false;
    private static volatile boolean m_renderingEnabled;
    private static volatile boolean m_waitForRenderState;
    private static volatile boolean m_hasContext;
    private static boolean m_cursorVisible;
    
    public static void init() {
        synchronized (zombie.core.opengl.RenderThread.m_initLock) {
            if (zombie.core.opengl.RenderThread.m_isInitialized) {
                return;
            }
            zombie.core.opengl.RenderThread.MainThread = Thread.currentThread();
            Core.bLoadedWithMultithreaded = Core.bMultithreadedRendering;
            zombie.core.opengl.RenderThread.RenderThread = Thread.currentThread();
            zombie.core.opengl.RenderThread.m_displayWidth = Display.getWidth();
            zombie.core.opengl.RenderThread.m_displayHeight = Display.getHeight();
            zombie.core.opengl.RenderThread.m_isInitialized = true;
        }
    }
    
    public static void initServerGUI() {
        synchronized (zombie.core.opengl.RenderThread.m_initLock) {
            if (zombie.core.opengl.RenderThread.m_isInitialized) {
                return;
            }
            zombie.core.opengl.RenderThread.MainThread = Thread.currentThread();
            Core.bLoadedWithMultithreaded = Core.bMultithreadedRendering;
            (zombie.core.opengl.RenderThread.RenderThread = new Thread(ThreadGroups.Main, zombie.core.opengl.RenderThread::renderLoop, "RenderThread Main Loop")).setName("Render Thread");
            zombie.core.opengl.RenderThread.RenderThread.setUncaughtExceptionHandler(zombie.core.opengl.RenderThread::uncaughtException);
            zombie.core.opengl.RenderThread.m_displayWidth = Display.getWidth();
            zombie.core.opengl.RenderThread.m_displayHeight = Display.getHeight();
            zombie.core.opengl.RenderThread.m_isInitialized = true;
        }
        zombie.core.opengl.RenderThread.RenderThread.start();
    }
    
    public static void renderLoop() {
        if (!GameServer.bServer) {
            synchronized (zombie.core.opengl.RenderThread.m_initLock) {
                try {
                    zombie.core.opengl.RenderThread.m_isInitialized = false;
                    GameWindow.InitDisplay();
                    Controllers.create();
                    Clipboard.initMainThread();
                }
                catch (Exception cause) {
                    throw new RuntimeException(cause);
                }
                finally {
                    zombie.core.opengl.RenderThread.m_isInitialized = true;
                }
            }
        }
        acquireContextReentrant();
        int i = 1;
        while (i != 0) {
            synchronized (zombie.core.opengl.RenderThread.m_contextLock) {
                if (!zombie.core.opengl.RenderThread.m_hasContext) {
                    acquireContextReentrant();
                }
                zombie.core.opengl.RenderThread.m_displayWidth = Display.getWidth();
                zombie.core.opengl.RenderThread.m_displayHeight = Display.getHeight();
                if (zombie.core.opengl.RenderThread.m_renderingEnabled) {
                    s_performance.renderStep.invokeAndMeasure(zombie.core.opengl.RenderThread::renderStep);
                }
                else if (zombie.core.opengl.RenderThread.m_isDisplayCreated && zombie.core.opengl.RenderThread.m_hasContext) {
                    Display.processMessages();
                }
                flushInvokeQueue();
                if (zombie.core.opengl.RenderThread.m_renderingEnabled) {
                    GameWindow.GameInput.poll();
                    Mouse.poll();
                    GameKeyboard.poll();
                    zombie.core.opengl.RenderThread.m_isCloseRequested = (zombie.core.opengl.RenderThread.m_isCloseRequested || Display.isCloseRequested());
                }
                else {
                    zombie.core.opengl.RenderThread.m_isCloseRequested = false;
                }
                if (!GameServer.bServer) {
                    Clipboard.updateMainThread();
                }
                DebugOptions.testThreadCrash(0);
                i = (GameWindow.bGameThreadExited ? 0 : 1);
            }
            Thread.yield();
        }
        releaseContextReentrant();
        synchronized (zombie.core.opengl.RenderThread.m_initLock) {
            zombie.core.opengl.RenderThread.RenderThread = null;
            zombie.core.opengl.RenderThread.m_isInitialized = false;
        }
        shutdown();
        System.exit(0);
    }
    
    private static void uncaughtException(final Thread thread, final Throwable t) {
        if (t instanceof ThreadDeath) {
            DebugLog.General.println("Render Thread exited: ", thread.getName());
            return;
        }
        try {
            GameWindow.uncaughtException(thread, t);
        }
        finally {
            final long n;
            final long n3;
            long n2;
            new Thread(() -> {
                System.currentTimeMillis();
                if (!GameWindow.bGameThreadExited) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException ex) {}
                    DebugLog.General.error((Object)"  Waiting for GameThread to exit...");
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException ex2) {}
                    while (!GameWindow.bGameThreadExited) {
                        Thread.yield();
                        System.currentTimeMillis();
                        if (n >= 120000L) {
                            DebugLog.General.error((Object)"  GameThread failed to exit within time limit.");
                            break;
                        }
                        else {
                            n2 = n3;
                        }
                    }
                }
                DebugLog.General.error((Object)"  Shutting down...");
                System.exit(1);
                return;
            }, "ForceCloseThread").start();
            DebugLog.General.error((Object)"Shutting down sequence starts.");
            zombie.core.opengl.RenderThread.m_isCloseRequested = true;
            DebugLog.General.error((Object)"  Notifying render state queue...");
            notifyRenderStateQueue();
            DebugLog.General.error((Object)"  Notifying InvokeOnRenderQueue...");
            synchronized (zombie.core.opengl.RenderThread.invokeOnRenderQueue) {
                zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.addAll(zombie.core.opengl.RenderThread.invokeOnRenderQueue);
                zombie.core.opengl.RenderThread.invokeOnRenderQueue.clear();
            }
            PZArrayUtil.forEach(zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking, RenderContextQueueItem::notifyWaitingListeners);
        }
    }
    
    private static boolean renderStep() {
        boolean lockStepRenderStep = false;
        try {
            lockStepRenderStep = lockStepRenderStep();
        }
        catch (OpenGLException ex) {
            logGLException(ex);
        }
        catch (Exception ex2) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ex2.getClass().getTypeName(), ex2.getMessage()));
            ex2.printStackTrace();
        }
        return lockStepRenderStep;
    }
    
    private static boolean lockStepRenderStep() {
        final SpriteRenderState acquireStateForRendering = SpriteRenderer.instance.acquireStateForRendering(zombie.core.opengl.RenderThread::waitForRenderStateCallback);
        if (acquireStateForRendering == null) {
            notifyRenderStateQueue();
            if (!zombie.core.opengl.RenderThread.m_waitForRenderState || (LuaManager.thread != null && LuaManager.thread.bStep)) {
                s_performance.displayUpdate.invokeAndMeasure(() -> Display.processMessages());
            }
            return true;
        }
        zombie.core.opengl.RenderThread.m_cursorVisible = acquireStateForRendering.bCursorVisible;
        s_performance.spriteRendererPostRender.invokeAndMeasure(() -> SpriteRenderer.instance.postRender());
        s_performance.displayUpdate.invokeAndMeasure(() -> {
            Display.update(true);
            checkControllers();
            return;
        });
        if (Core.bDebug && FPSGraph.instance != null) {
            FPSGraph.instance.addRender(System.currentTimeMillis());
        }
        MPStatisticClient.getInstance().fpsProcess();
        return true;
    }
    
    private static void checkControllers() {
    }
    
    private static boolean waitForRenderStateCallback() {
        flushInvokeQueue();
        return shouldContinueWaiting();
    }
    
    private static boolean shouldContinueWaiting() {
        return !zombie.core.opengl.RenderThread.m_isCloseRequested && !GameWindow.bGameThreadExited && (zombie.core.opengl.RenderThread.m_waitForRenderState || SpriteRenderer.instance.isWaitingForRenderState());
    }
    
    public static boolean isWaitForRenderState() {
        return zombie.core.opengl.RenderThread.m_waitForRenderState;
    }
    
    public static void setWaitForRenderState(final boolean waitForRenderState) {
        zombie.core.opengl.RenderThread.m_waitForRenderState = waitForRenderState;
    }
    
    private static void flushInvokeQueue() {
        synchronized (zombie.core.opengl.RenderThread.invokeOnRenderQueue) {
            zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.addAll(zombie.core.opengl.RenderThread.invokeOnRenderQueue);
            zombie.core.opengl.RenderThread.invokeOnRenderQueue.clear();
        }
        try {
            if (!zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.isEmpty()) {
                final long nanoTime = System.nanoTime();
                while (!zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.isEmpty()) {
                    final RenderContextQueueItem renderContextQueueItem = zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.remove(0);
                    final long nanoTime2 = System.nanoTime();
                    renderContextQueueItem.invoke();
                    final long nanoTime3 = System.nanoTime();
                    if (nanoTime3 - nanoTime2 > 1.0E7) {}
                    if (nanoTime3 - nanoTime > 1.0E7) {
                        break;
                    }
                }
                for (int i = zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.size() - 1; i >= 0; --i) {
                    if (zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.get(i).isWaiting()) {
                        while (i >= 0) {
                            zombie.core.opengl.RenderThread.invokeOnRenderQueue_Invoking.remove(0).invoke();
                            --i;
                        }
                        break;
                    }
                }
            }
            if (TextureID.deleteTextureIDS.position() > 0) {
                TextureID.deleteTextureIDS.flip();
                GL11.glDeleteTextures(TextureID.deleteTextureIDS);
                TextureID.deleteTextureIDS.clear();
            }
        }
        catch (OpenGLException ex) {
            logGLException(ex);
        }
        catch (Exception ex2) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ex2.getClass().getTypeName(), ex2.getMessage()));
            ex2.printStackTrace();
        }
    }
    
    public static void logGLException(final OpenGLException ex) {
        logGLException(ex, true);
    }
    
    public static void logGLException(final OpenGLException ex, final boolean b) {
        DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex.getMessage()));
        for (int i = GL11.glGetError(); i != 0; i = GL11.glGetError()) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, Util.translateGLErrorString(i), i));
        }
        if (b) {
            DebugLog.General.error((Object)"Stack trace:");
            ex.printStackTrace();
        }
    }
    
    public static void Ready() {
        SpriteRenderer.instance.pushFrameDown();
        if (!zombie.core.opengl.RenderThread.m_isInitialized) {
            invokeOnRenderContext(zombie.core.opengl.RenderThread::renderStep);
        }
    }
    
    private static void acquireContextReentrant() {
        synchronized (zombie.core.opengl.RenderThread.m_contextLock) {
            acquireContextReentrantInternal();
        }
    }
    
    private static void releaseContextReentrant() {
        synchronized (zombie.core.opengl.RenderThread.m_contextLock) {
            releaseContextReentrantInternal();
        }
    }
    
    private static void acquireContextReentrantInternal() {
        final Thread currentThread = Thread.currentThread();
        if (zombie.core.opengl.RenderThread.ContextThread != null && zombie.core.opengl.RenderThread.ContextThread != currentThread) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Thread;Ljava/lang/Thread;)Ljava/lang/String;, zombie.core.opengl.RenderThread.ContextThread, currentThread));
        }
        ++zombie.core.opengl.RenderThread.m_contextLockReentrantDepth;
        if (zombie.core.opengl.RenderThread.m_contextLockReentrantDepth > 1) {
            return;
        }
        zombie.core.opengl.RenderThread.ContextThread = currentThread;
        zombie.core.opengl.RenderThread.m_isDisplayCreated = Display.isCreated();
        if (zombie.core.opengl.RenderThread.m_isDisplayCreated) {
            try {
                zombie.core.opengl.RenderThread.m_hasContext = true;
                Display.makeCurrent();
                Display.setVSyncEnabled(Core.OptionVSync);
            }
            catch (LWJGLException ex) {
                DebugLog.General.error((Object)"Exception thrown trying to gain GL context.");
                ex.printStackTrace();
            }
        }
    }
    
    private static void releaseContextReentrantInternal() {
        final Thread currentThread = Thread.currentThread();
        if (zombie.core.opengl.RenderThread.ContextThread != currentThread) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Thread;Ljava/lang/Thread;)Ljava/lang/String;, zombie.core.opengl.RenderThread.ContextThread, currentThread));
        }
        if (zombie.core.opengl.RenderThread.m_contextLockReentrantDepth == 0) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Thread;Ljava/lang/Thread;)Ljava/lang/String;, zombie.core.opengl.RenderThread.ContextThread, currentThread));
        }
        --zombie.core.opengl.RenderThread.m_contextLockReentrantDepth;
        if (zombie.core.opengl.RenderThread.m_contextLockReentrantDepth > 0) {
            return;
        }
        if (zombie.core.opengl.RenderThread.m_isDisplayCreated && zombie.core.opengl.RenderThread.m_hasContext) {
            try {
                zombie.core.opengl.RenderThread.m_hasContext = false;
                Display.releaseContext();
            }
            catch (LWJGLException ex) {
                DebugLog.General.error((Object)"Exception thrown trying to release GL context.");
                ex.printStackTrace();
            }
        }
        zombie.core.opengl.RenderThread.ContextThread = null;
    }
    
    public static void invokeOnRenderContext(final Runnable runnable) throws RenderContextQueueException {
        final RenderContextQueueItem alloc = RenderContextQueueItem.alloc(runnable);
        alloc.setWaiting();
        queueInvokeOnRenderContext(alloc);
        try {
            alloc.waitUntilFinished(() -> {
                notifyRenderStateQueue();
                return !zombie.core.opengl.RenderThread.m_isCloseRequested && !GameWindow.bGameThreadExited;
            });
        }
        catch (InterruptedException ex) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Lzombie/core/opengl/RenderContextQueueItem;)Ljava/lang/String;, alloc));
            notifyRenderStateQueue();
        }
        final Throwable thrown = alloc.getThrown();
        if (thrown != null) {
            throw new RenderContextQueueException(thrown);
        }
    }
    
    public static <T1> void invokeOnRenderContext(final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
        Lambda.capture(t1, callback, (genericStack, o, callback2) -> invokeOnRenderContext(genericStack.invoker(o, callback2)));
    }
    
    public static <T1, T2> void invokeOnRenderContext(final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
        Lambda.capture(t1, t2, callback, (genericStack, o, o2, callback2) -> invokeOnRenderContext(genericStack.invoker(o, o2, callback2)));
    }
    
    public static <T1, T2, T3> void invokeOnRenderContext(final T1 t1, final T2 t2, final T3 t3, final Invokers.Params3.ICallback<T1, T2, T3> callback) {
        Lambda.capture(t1, t2, t3, callback, (genericStack, o, o2, o3, callback2) -> invokeOnRenderContext(genericStack.invoker(o, o2, o3, callback2)));
    }
    
    public static <T1, T2, T3, T4> void invokeOnRenderContext(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final Invokers.Params4.ICallback<T1, T2, T3, T4> callback) {
        Lambda.capture(t1, t2, t3, t4, callback, (genericStack, o, o2, o3, o4, callback2) -> invokeOnRenderContext(genericStack.invoker(o, o2, o3, o4, callback2)));
    }
    
    protected static void notifyRenderStateQueue() {
        if (SpriteRenderer.instance != null) {
            SpriteRenderer.instance.notifyRenderStateQueue();
        }
    }
    
    public static void queueInvokeOnRenderContext(final Runnable runnable) {
        queueInvokeOnRenderContext(RenderContextQueueItem.alloc(runnable));
    }
    
    public static void queueInvokeOnRenderContext(final RenderContextQueueItem e) {
        if (!zombie.core.opengl.RenderThread.m_isInitialized) {
            synchronized (zombie.core.opengl.RenderThread.m_initLock) {
                if (!zombie.core.opengl.RenderThread.m_isInitialized) {
                    try {
                        acquireContextReentrant();
                        e.invoke();
                    }
                    finally {
                        releaseContextReentrant();
                    }
                    return;
                }
            }
        }
        if (zombie.core.opengl.RenderThread.ContextThread == Thread.currentThread()) {
            e.invoke();
            return;
        }
        synchronized (zombie.core.opengl.RenderThread.invokeOnRenderQueue) {
            zombie.core.opengl.RenderThread.invokeOnRenderQueue.add(e);
        }
    }
    
    public static void shutdown() {
        GameWindow.GameInput.quit();
        if (zombie.core.opengl.RenderThread.m_isInitialized) {
            queueInvokeOnRenderContext(Display::destroy);
        }
        else {
            Display.destroy();
        }
    }
    
    public static boolean isCloseRequested() {
        if (zombie.core.opengl.RenderThread.m_isCloseRequested) {
            DebugLog.log("EXITDEBUG: RenderThread.isCloseRequested 1");
            return zombie.core.opengl.RenderThread.m_isCloseRequested;
        }
        if (!zombie.core.opengl.RenderThread.m_isInitialized) {
            synchronized (zombie.core.opengl.RenderThread.m_initLock) {
                if (!zombie.core.opengl.RenderThread.m_isInitialized) {
                    zombie.core.opengl.RenderThread.m_isCloseRequested = Display.isCloseRequested();
                    if (zombie.core.opengl.RenderThread.m_isCloseRequested) {
                        DebugLog.log("EXITDEBUG: RenderThread.isCloseRequested 2");
                    }
                }
            }
        }
        return zombie.core.opengl.RenderThread.m_isCloseRequested;
    }
    
    public static int getDisplayWidth() {
        if (!zombie.core.opengl.RenderThread.m_isInitialized) {
            return Display.getWidth();
        }
        return zombie.core.opengl.RenderThread.m_displayWidth;
    }
    
    public static int getDisplayHeight() {
        if (!zombie.core.opengl.RenderThread.m_isInitialized) {
            return Display.getHeight();
        }
        return zombie.core.opengl.RenderThread.m_displayHeight;
    }
    
    public static boolean isRunning() {
        return zombie.core.opengl.RenderThread.m_isInitialized;
    }
    
    public static void startRendering() {
        zombie.core.opengl.RenderThread.m_renderingEnabled = true;
    }
    
    public static void onGameThreadExited() {
        DebugLog.General.println("GameThread exited.");
        if (zombie.core.opengl.RenderThread.RenderThread != null) {
            zombie.core.opengl.RenderThread.RenderThread.interrupt();
        }
    }
    
    public static boolean isCursorVisible() {
        return zombie.core.opengl.RenderThread.m_cursorVisible;
    }
    
    static {
        zombie.core.opengl.RenderThread.ContextThread = null;
        zombie.core.opengl.RenderThread.m_isDisplayCreated = false;
        zombie.core.opengl.RenderThread.m_contextLockReentrantDepth = 0;
        m_contextLock = "RenderThread borrowContext Lock";
        invokeOnRenderQueue = new ArrayList<RenderContextQueueItem>();
        invokeOnRenderQueue_Invoking = new ArrayList<RenderContextQueueItem>();
        zombie.core.opengl.RenderThread.m_isInitialized = false;
        m_initLock = "RenderThread Initialization Lock";
        zombie.core.opengl.RenderThread.m_isCloseRequested = false;
        zombie.core.opengl.RenderThread.m_renderingEnabled = true;
        zombie.core.opengl.RenderThread.m_waitForRenderState = false;
        zombie.core.opengl.RenderThread.m_hasContext = false;
        zombie.core.opengl.RenderThread.m_cursorVisible = true;
    }
    
    private static class s_performance
    {
        static final PerformanceProfileFrameProbe renderStep;
        static final PerformanceProfileProbe displayUpdate;
        static final PerformanceProfileProbe spriteRendererPostRender;
        
        static {
            renderStep = new PerformanceProfileFrameProbe("RenderThread.renderStep");
            displayUpdate = new PerformanceProfileProbe("Display.update(true)");
            spriteRendererPostRender = new PerformanceProfileProbe("SpriteRenderer.postRender");
        }
    }
}
