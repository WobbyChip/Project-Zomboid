// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.skinnedmodel.model.Model;
import org.joml.Matrix4f;
import java.io.PrintStream;
import org.lwjglx.opengl.Util;
import org.lwjglx.opengl.OpenGLException;
import org.lwjgl.opengl.GL11;

public class PZGLUtil
{
    static int test;
    
    public static void checkGLErrorThrow(final String s, final Object... array) throws OpenGLException {
        final int glGetError = GL11.glGetError();
        if (glGetError != 0) {
            ++PZGLUtil.test;
            throw new OpenGLException(createErrorMessage(glGetError, s, array));
        }
    }
    
    private static String createErrorMessage(final int n, final String format, final Object... args) {
        final String lineSeparator = System.lineSeparator();
        return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, n, lineSeparator, createErrorMessage(n), lineSeparator, String.format(format, args));
    }
    
    private static String createErrorMessage(final int n) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, Util.translateGLErrorString(n), n);
    }
    
    public static boolean checkGLError(final boolean b) {
        try {
            Util.checkGLError();
            return true;
        }
        catch (OpenGLException ex) {
            RenderThread.logGLException(ex, b);
            return false;
        }
    }
    
    public static void printGLState(final PrintStream printStream) {
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(2979)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(2980)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(2981)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(2992)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(2993)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3381)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3382)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3383)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3384)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3385)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3387)));
        printStream.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(3440)));
    }
    
    public static void loadMatrix(final Matrix4f matrix4f) {
        matrix4f.get(Model.m_staticReusableFloatBuffer);
        Model.m_staticReusableFloatBuffer.position(16);
        Model.m_staticReusableFloatBuffer.flip();
        GL11.glLoadMatrixf(Model.m_staticReusableFloatBuffer);
    }
    
    public static void multMatrix(final Matrix4f matrix4f) {
        matrix4f.get(Model.m_staticReusableFloatBuffer);
        Model.m_staticReusableFloatBuffer.position(16);
        Model.m_staticReusableFloatBuffer.flip();
        GL11.glMultMatrixf(Model.m_staticReusableFloatBuffer);
    }
    
    public static void loadMatrix(final int n, final Matrix4f matrix4f) {
        GL11.glMatrixMode(n);
        loadMatrix(matrix4f);
    }
    
    public static void multMatrix(final int n, final Matrix4f matrix4f) {
        GL11.glMatrixMode(n);
        multMatrix(matrix4f);
    }
    
    public static void pushAndLoadMatrix(final int n, final Matrix4f matrix4f) {
        GL11.glMatrixMode(n);
        GL11.glPushMatrix();
        loadMatrix(matrix4f);
    }
    
    public static void pushAndMultMatrix(final int n, final Matrix4f matrix4f) {
        GL11.glMatrixMode(n);
        GL11.glPushMatrix();
        multMatrix(matrix4f);
    }
    
    public static void popMatrix(final int n) {
        GL11.glMatrixMode(n);
        GL11.glPopMatrix();
    }
    
    static {
        PZGLUtil.test = 0;
    }
}
