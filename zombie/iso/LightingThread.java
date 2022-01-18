// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.GameWindow;
import zombie.ui.FPSGraph;
import zombie.core.Core;
import org.lwjglx.opengl.Display;
import zombie.core.ThreadGroups;
import zombie.network.GameServer;
import zombie.core.PerformanceSettings;

public final class LightingThread
{
    public static final LightingThread instance;
    public Thread lightingThread;
    public boolean bFinished;
    public volatile boolean Interrupted;
    public static boolean DebugLockTime;
    
    public LightingThread() {
        this.bFinished = false;
        this.Interrupted = false;
    }
    
    public void stop() {
        if (!PerformanceSettings.LightingThread) {
            LightingJNI.stop();
            return;
        }
        this.bFinished = true;
        while (this.lightingThread.isAlive()) {}
        LightingJNI.stop();
        this.lightingThread = null;
    }
    
    public void create() {
        if (GameServer.bServer) {
            return;
        }
        if (!PerformanceSettings.LightingThread) {
            return;
        }
        this.bFinished = false;
        (this.lightingThread = new Thread(ThreadGroups.Workers, () -> {
            while (!this.bFinished) {
                if (IsoWorld.instance.CurrentCell != null) {
                    try {
                        Display.sync(PerformanceSettings.LightingFPS);
                        LightingJNI.DoLightingUpdateNew(System.nanoTime());
                        while (LightingJNI.WaitingForMain() && !this.bFinished) {
                            Thread.sleep(13L);
                        }
                        if (Core.bDebug && FPSGraph.instance != null) {
                            FPSGraph.instance.addLighting(System.currentTimeMillis());
                        }
                        else {
                            continue;
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return;
        })).setPriority(5);
        this.lightingThread.setDaemon(true);
        this.lightingThread.setName("Lighting Thread");
        this.lightingThread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.lightingThread.start();
    }
    
    public void GameLoadingUpdate() {
    }
    
    public void update() {
        if (IsoWorld.instance == null || IsoWorld.instance.CurrentCell == null) {
            return;
        }
        if (LightingJNI.init) {
            LightingJNI.update();
        }
    }
    
    public void scrollLeft(final int n) {
        if (LightingJNI.init) {
            LightingJNI.scrollLeft(n);
        }
    }
    
    public void scrollRight(final int n) {
        if (LightingJNI.init) {
            LightingJNI.scrollRight(n);
        }
    }
    
    public void scrollUp(final int n) {
        if (LightingJNI.init) {
            LightingJNI.scrollUp(n);
        }
    }
    
    public void scrollDown(final int n) {
        if (LightingJNI.init) {
            LightingJNI.scrollDown(n);
        }
    }
    
    static {
        instance = new LightingThread();
        LightingThread.DebugLockTime = false;
    }
}
