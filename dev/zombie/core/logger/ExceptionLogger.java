// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.logger;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.Core;
import zombie.ui.UIManager;
import zombie.network.GameServer;
import zombie.core.opengl.RenderThread;
import zombie.util.Type;
import org.lwjglx.opengl.OpenGLException;
import zombie.debug.DebugLogStream;
import zombie.debug.LogSeverity;
import zombie.debug.DebugLog;
import zombie.ui.UITransition;

public final class ExceptionLogger
{
    private static int exceptionCount;
    private static boolean bIgnore;
    private static boolean bExceptionPopup;
    private static long popupFrameMS;
    private static UITransition transition;
    private static boolean bHide;
    
    public static synchronized void logException(final Throwable t) {
        logException(t, null);
    }
    
    public static synchronized void logException(final Throwable t, final String s) {
        logException(t, s, DebugLog.General, LogSeverity.Error);
    }
    
    public static synchronized void logException(final Throwable t, final String s, final DebugLogStream debugLogStream, final LogSeverity logSeverity) {
        final OpenGLException ex = Type.tryCastTo(t, OpenGLException.class);
        if (ex != null) {
            RenderThread.logGLException(ex, false);
        }
        debugLogStream.printException(t, s, DebugLogStream.generateCallerPrefix(), logSeverity);
        try {
            if (ExceptionLogger.bIgnore) {
                return;
            }
            ExceptionLogger.bIgnore = true;
            ++ExceptionLogger.exceptionCount;
            if (GameServer.bServer) {
                return;
            }
            if (ExceptionLogger.bExceptionPopup) {
                showPopup();
            }
        }
        catch (Throwable t2) {
            debugLogStream.printException(t2, "Exception thrown while trying to logException.", LogSeverity.Error);
        }
        finally {
            ExceptionLogger.bIgnore = false;
        }
    }
    
    public static void showPopup() {
        final float elapsed = (ExceptionLogger.popupFrameMS > 0L) ? ExceptionLogger.transition.getElapsed() : 0.0f;
        ExceptionLogger.popupFrameMS = 3000L;
        ExceptionLogger.transition.setIgnoreUpdateTime(true);
        ExceptionLogger.transition.init(500.0f, false);
        ExceptionLogger.transition.setElapsed(elapsed);
        ExceptionLogger.bHide = false;
    }
    
    public static void render() {
        if (UIManager.useUIFBO && !Core.getInstance().UIRenderThisFrame) {
            return;
        }
        final boolean b = false;
        if (b) {
            ExceptionLogger.popupFrameMS = 3000L;
        }
        if (ExceptionLogger.popupFrameMS <= 0L) {
            return;
        }
        ExceptionLogger.popupFrameMS -= (long)UIManager.getMillisSinceLastRender();
        ExceptionLogger.transition.update();
        final int fontHeight = TextManager.instance.getFontHeight(UIFont.DebugConsole);
        final int n = 100;
        final int n2 = fontHeight * 2 + 4;
        final int n3 = Core.getInstance().getScreenWidth() - n;
        int n4 = Core.getInstance().getScreenHeight() - (int)(n2 * ExceptionLogger.transition.fraction());
        if (b) {
            n4 = Core.getInstance().getScreenHeight() - n2;
        }
        SpriteRenderer.instance.renderi(null, n3, n4, n, n2, 0.8f, 0.0f, 0.0f, 1.0f, null);
        SpriteRenderer.instance.renderi(null, n3 + 1, n4 + 1, n - 2, fontHeight - 1, 0.0f, 0.0f, 0.0f, 1.0f, null);
        TextManager.instance.DrawStringCentre(UIFont.DebugConsole, n3 + n / 2, n4, "ERROR", 1.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawStringCentre(UIFont.DebugConsole, n3 + n / 2, n4 + fontHeight, b ? "999" : Integer.toString(ExceptionLogger.exceptionCount), 0.0, 0.0, 0.0, 1.0);
        if (ExceptionLogger.popupFrameMS <= 0L && !ExceptionLogger.bHide) {
            ExceptionLogger.popupFrameMS = 500L;
            ExceptionLogger.transition.init(500.0f, true);
            ExceptionLogger.bHide = true;
        }
    }
    
    static {
        ExceptionLogger.bExceptionPopup = true;
        ExceptionLogger.popupFrameMS = 0L;
        ExceptionLogger.transition = new UITransition();
    }
}
