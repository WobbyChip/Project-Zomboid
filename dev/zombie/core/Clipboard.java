// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFW;

public final class Clipboard
{
    private static Thread MainThread;
    private static String PreviousKnownValue;
    private static String DelaySetMainThread;
    
    public static void initMainThread() {
        Clipboard.MainThread = Thread.currentThread();
        Clipboard.PreviousKnownValue = getClipboard();
    }
    
    public static void rememberCurrentValue() {
        if (Thread.currentThread() == Clipboard.MainThread) {
            final GLFWErrorCallback glfwSetErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)null);
            try {
                Clipboard.PreviousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
            }
            catch (Throwable t) {
                Clipboard.PreviousKnownValue = "";
            }
            finally {
                GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)glfwSetErrorCallback);
            }
        }
    }
    
    public static synchronized String getClipboard() {
        if (Thread.currentThread() == Clipboard.MainThread) {
            final GLFWErrorCallback glfwSetErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)null);
            try {
                return Clipboard.PreviousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
            }
            catch (Throwable t) {
                return Clipboard.PreviousKnownValue = "";
            }
            finally {
                GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)glfwSetErrorCallback);
            }
        }
        return Clipboard.PreviousKnownValue;
    }
    
    public static synchronized void setClipboard(final String s) {
        Clipboard.PreviousKnownValue = s;
        if (Thread.currentThread() == Clipboard.MainThread) {
            GLFW.glfwSetClipboardString(0L, (CharSequence)s);
        }
        else {
            Clipboard.DelaySetMainThread = s;
        }
    }
    
    public static synchronized void updateMainThread() {
        if (Clipboard.DelaySetMainThread != null) {
            setClipboard(Clipboard.DelaySetMainThread);
            Clipboard.DelaySetMainThread = null;
        }
    }
    
    static {
        Clipboard.MainThread = null;
        Clipboard.PreviousKnownValue = null;
        Clipboard.DelaySetMainThread = null;
    }
}
