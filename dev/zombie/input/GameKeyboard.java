// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

import zombie.core.opengl.RenderThread;
import zombie.GameWindow;
import org.lwjglx.input.KeyEventQueue;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;

public final class GameKeyboard
{
    private static boolean[] bDown;
    private static boolean[] bLastDown;
    private static boolean[] bEatKey;
    public static boolean bNoEventsWhileLoading;
    public static boolean doLuaKeyPressed;
    private static final KeyboardStateCache s_keyboardStateCache;
    
    public static void update() {
        if (!GameKeyboard.s_keyboardStateCache.getState().isCreated()) {
            GameKeyboard.s_keyboardStateCache.swap();
            return;
        }
        final int keyCount = GameKeyboard.s_keyboardStateCache.getState().getKeyCount();
        if (GameKeyboard.bDown == null) {
            GameKeyboard.bDown = new boolean[keyCount];
            GameKeyboard.bLastDown = new boolean[keyCount];
            GameKeyboard.bEatKey = new boolean[keyCount];
        }
        final boolean b = Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry;
        for (int i = 1; i < keyCount; ++i) {
            GameKeyboard.bLastDown[i] = GameKeyboard.bDown[i];
            GameKeyboard.bDown[i] = GameKeyboard.s_keyboardStateCache.getState().isKeyDown(i);
            if (!GameKeyboard.bDown[i] && GameKeyboard.bLastDown[i]) {
                if (GameKeyboard.bEatKey[i]) {
                    GameKeyboard.bEatKey[i] = false;
                    continue;
                }
                if (GameKeyboard.bNoEventsWhileLoading) {
                    continue;
                }
                if (b) {
                    continue;
                }
                if (LuaManager.thread == UIManager.defaultthread && UIManager.onKeyRelease(i)) {
                    continue;
                }
                if (Core.bDebug && !GameKeyboard.doLuaKeyPressed) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                }
                if (LuaManager.thread == UIManager.defaultthread && GameKeyboard.doLuaKeyPressed) {
                    LuaEventManager.triggerEvent("OnKeyPressed", i);
                }
                if (LuaManager.thread == UIManager.defaultthread) {
                    LuaEventManager.triggerEvent("OnCustomUIKey", i);
                    LuaEventManager.triggerEvent("OnCustomUIKeyReleased", i);
                }
            }
            if (GameKeyboard.bDown[i] && GameKeyboard.bLastDown[i]) {
                if (GameKeyboard.bNoEventsWhileLoading) {
                    continue;
                }
                if (b) {
                    continue;
                }
                if (LuaManager.thread == UIManager.defaultthread && UIManager.onKeyRepeat(i)) {
                    continue;
                }
                if (LuaManager.thread == UIManager.defaultthread && GameKeyboard.doLuaKeyPressed) {
                    LuaEventManager.triggerEvent("OnKeyKeepPressed", i);
                }
            }
            if (GameKeyboard.bDown[i] && !GameKeyboard.bLastDown[i]) {
                if (!GameKeyboard.bNoEventsWhileLoading) {
                    if (!b) {
                        if (!GameKeyboard.bEatKey[i]) {
                            if (LuaManager.thread != UIManager.defaultthread || !UIManager.onKeyPress(i)) {
                                if (!GameKeyboard.bEatKey[i]) {
                                    if (LuaManager.thread == UIManager.defaultthread && GameKeyboard.doLuaKeyPressed) {
                                        LuaEventManager.triggerEvent("OnKeyStartPressed", i);
                                    }
                                    if (LuaManager.thread == UIManager.defaultthread) {
                                        LuaEventManager.triggerEvent("OnCustomUIKeyPressed", i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        GameKeyboard.s_keyboardStateCache.swap();
    }
    
    public static void poll() {
        GameKeyboard.s_keyboardStateCache.poll();
    }
    
    public static boolean isKeyPressed(final int n) {
        return isKeyDown(n) && !wasKeyDown(n);
    }
    
    public static boolean isKeyDown(final int n) {
        return (Core.CurrentTextEntryBox == null || !Core.CurrentTextEntryBox.DoingTextEntry) && GameKeyboard.bDown != null && GameKeyboard.bDown[n];
    }
    
    public static boolean wasKeyDown(final int n) {
        return (Core.CurrentTextEntryBox == null || !Core.CurrentTextEntryBox.DoingTextEntry) && GameKeyboard.bLastDown != null && GameKeyboard.bLastDown[n];
    }
    
    public static void eatKeyPress(final int n) {
        if (n < 0 || n >= GameKeyboard.bEatKey.length) {
            return;
        }
        GameKeyboard.bEatKey[n] = true;
    }
    
    public static void setDoLuaKeyPressed(final boolean doLuaKeyPressed) {
        GameKeyboard.doLuaKeyPressed = doLuaKeyPressed;
    }
    
    public static KeyEventQueue getEventQueue() {
        assert Thread.currentThread() == GameWindow.GameThread;
        return GameKeyboard.s_keyboardStateCache.getState().getEventQueue();
    }
    
    public static KeyEventQueue getEventQueuePolling() {
        assert Thread.currentThread() == RenderThread.RenderThread;
        return GameKeyboard.s_keyboardStateCache.getStatePolling().getEventQueue();
    }
    
    static {
        GameKeyboard.bNoEventsWhileLoading = false;
        GameKeyboard.doLuaKeyPressed = true;
        s_keyboardStateCache = new KeyboardStateCache();
    }
}
