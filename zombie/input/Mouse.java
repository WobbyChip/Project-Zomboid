// 
// Decompiled by Procyon v0.5.36
// 

package zombie.input;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import javax.imageio.ImageIO;
import zombie.ZomboidFileSystem;
import org.lwjglx.LWJGLException;
import zombie.core.Core;
import zombie.core.textures.Texture;
import org.lwjglx.input.Cursor;

public final class Mouse
{
    protected static int x;
    protected static int y;
    public static boolean bLeftDown;
    public static boolean bLeftWasDown;
    public static boolean bRightDown;
    public static boolean bRightWasDown;
    public static boolean bMiddleDown;
    public static boolean bMiddleWasDown;
    public static boolean[] m_buttonDownStates;
    public static long lastActivity;
    public static int wheelDelta;
    private static final MouseStateCache s_mouseStateCache;
    public static boolean[] UICaptured;
    static Cursor blankCursor;
    static Cursor defaultCursor;
    private static boolean isCursorVisible;
    private static Texture mouseCursorTexture;
    
    public static int getWheelState() {
        return Mouse.wheelDelta;
    }
    
    public static synchronized int getXA() {
        return Mouse.x;
    }
    
    public static synchronized int getYA() {
        return Mouse.y;
    }
    
    public static synchronized int getX() {
        return (int)(Mouse.x * Core.getInstance().getZoom(0));
    }
    
    public static synchronized int getY() {
        return (int)(Mouse.y * Core.getInstance().getZoom(0));
    }
    
    public static boolean isButtonDown(final int n) {
        return Mouse.m_buttonDownStates != null && Mouse.m_buttonDownStates[n];
    }
    
    public static void UIBlockButtonDown(final int n) {
        Mouse.UICaptured[n] = true;
    }
    
    public static boolean isButtonDownUICheck(final int n) {
        if (Mouse.m_buttonDownStates != null) {
            final boolean b = Mouse.m_buttonDownStates[n];
            if (!b) {
                Mouse.UICaptured[n] = false;
            }
            else if (Mouse.UICaptured[n]) {
                return false;
            }
            return b;
        }
        return false;
    }
    
    public static boolean isLeftDown() {
        return Mouse.bLeftDown;
    }
    
    public static boolean isLeftPressed() {
        return !Mouse.bLeftWasDown && Mouse.bLeftDown;
    }
    
    public static boolean isLeftReleased() {
        return Mouse.bLeftWasDown && !Mouse.bLeftDown;
    }
    
    public static boolean isLeftUp() {
        return !Mouse.bLeftDown;
    }
    
    public static boolean isMiddleDown() {
        return Mouse.bMiddleDown;
    }
    
    public static boolean isMiddlePressed() {
        return !Mouse.bMiddleWasDown && Mouse.bMiddleDown;
    }
    
    public static boolean isMiddleReleased() {
        return Mouse.bMiddleWasDown && !Mouse.bMiddleDown;
    }
    
    public static boolean isMiddleUp() {
        return !Mouse.bMiddleDown;
    }
    
    public static boolean isRightDown() {
        return Mouse.bRightDown;
    }
    
    public static boolean isRightPressed() {
        return !Mouse.bRightWasDown && Mouse.bRightDown;
    }
    
    public static boolean isRightReleased() {
        return Mouse.bRightWasDown && !Mouse.bRightDown;
    }
    
    public static boolean isRightUp() {
        return !Mouse.bRightDown;
    }
    
    public static synchronized void update() {
        final MouseState state = Mouse.s_mouseStateCache.getState();
        if (!state.isCreated()) {
            Mouse.s_mouseStateCache.swap();
            try {
                org.lwjglx.input.Mouse.create();
            }
            catch (LWJGLException ex) {
                ex.printStackTrace();
            }
            return;
        }
        Mouse.bLeftWasDown = Mouse.bLeftDown;
        Mouse.bRightWasDown = Mouse.bRightDown;
        Mouse.bMiddleWasDown = Mouse.bMiddleDown;
        final int x = Mouse.x;
        final int y = Mouse.y;
        Mouse.x = state.getX();
        Mouse.y = Core.getInstance().getScreenHeight() - state.getY() - 1;
        Mouse.bLeftDown = state.isButtonDown(0);
        Mouse.bRightDown = state.isButtonDown(1);
        Mouse.bMiddleDown = state.isButtonDown(2);
        Mouse.wheelDelta = state.getDWheel();
        state.resetDWheel();
        if (Mouse.m_buttonDownStates == null) {
            Mouse.m_buttonDownStates = new boolean[state.getButtonCount()];
        }
        for (int i = 0; i < Mouse.m_buttonDownStates.length; ++i) {
            Mouse.m_buttonDownStates[i] = state.isButtonDown(i);
        }
        if (x != Mouse.x || y != Mouse.y || Mouse.wheelDelta != 0 || Mouse.bLeftWasDown != Mouse.bLeftDown || Mouse.bRightWasDown != Mouse.bRightDown || Mouse.bMiddleWasDown != Mouse.bMiddleDown) {
            Mouse.lastActivity = System.currentTimeMillis();
        }
        Mouse.s_mouseStateCache.swap();
    }
    
    public static void poll() {
        Mouse.s_mouseStateCache.poll();
    }
    
    public static synchronized void setXY(final int n, final int n2) {
        Mouse.s_mouseStateCache.getState().setCursorPosition(n, Core.getInstance().getOffscreenHeight(0) - 1 - n2);
    }
    
    public static Cursor loadCursor(final String s) throws LWJGLException {
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        try {
            final BufferedImage read = ImageIO.read(mediaFile);
            final int width = read.getWidth();
            final int height = read.getHeight();
            final int[] src = new int[width * height];
            for (int i = 0; i < src.length; ++i) {
                src[i] = read.getRGB(i % width, height - 1 - i / width);
            }
            final IntBuffer intBuffer = BufferUtils.createIntBuffer(width * height);
            intBuffer.put(src);
            intBuffer.rewind();
            return new Cursor(width, height, 1, 1, 1, intBuffer, (IntBuffer)null);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static void initCustomCursor() {
        if (Mouse.blankCursor == null) {
            try {
                Mouse.blankCursor = loadCursor("cursor_blank.png");
                Mouse.defaultCursor = loadCursor("cursor_white.png");
            }
            catch (LWJGLException ex) {
                ex.printStackTrace();
            }
        }
        if (Mouse.defaultCursor == null) {
            return;
        }
        try {
            org.lwjglx.input.Mouse.setNativeCursor(Mouse.defaultCursor);
        }
        catch (LWJGLException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static void setCursorVisible(final boolean isCursorVisible) {
        Mouse.isCursorVisible = isCursorVisible;
    }
    
    public static boolean isCursorVisible() {
        return Mouse.isCursorVisible;
    }
    
    public static void renderCursorTexture() {
        if (!isCursorVisible()) {
            return;
        }
        if (Mouse.mouseCursorTexture == null) {
            Mouse.mouseCursorTexture = Texture.getSharedTexture("media/ui/cursor_white.png");
        }
        if (Mouse.mouseCursorTexture == null || !Mouse.mouseCursorTexture.isReady()) {
            return;
        }
        SpriteRenderer.instance.render(Mouse.mouseCursorTexture, (float)(getXA() - 1), (float)(getYA() - 1), (float)Mouse.mouseCursorTexture.getWidth(), (float)Mouse.mouseCursorTexture.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
    }
    
    static {
        s_mouseStateCache = new MouseStateCache();
        Mouse.UICaptured = new boolean[10];
        Mouse.isCursorVisible = true;
        Mouse.mouseCursorTexture = null;
    }
}
