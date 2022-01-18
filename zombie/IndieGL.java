// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.util.lambda.Stacks;
import zombie.core.textures.Texture;
import org.lwjgl.opengl.GL11;
import zombie.core.math.Vector4;
import zombie.iso.Vector3;
import zombie.iso.Vector2;
import zombie.core.opengl.ShaderProgram;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;
import zombie.iso.IsoCamera;
import zombie.core.opengl.Shader;
import zombie.core.SpriteRenderer;
import java.util.Stack;
import zombie.core.opengl.GLState;

public final class IndieGL
{
    public static int nCount;
    private static final GLState.CIntValue tempInt;
    private static final GLState.C2IntsValue temp2Ints;
    private static final GLState.C3IntsValue temp3Ints;
    private static final GLState.C4IntsValue temp4Ints;
    private static final GLState.C4BooleansValue temp4Booleans;
    private static final GLState.CIntFloatValue tempIntFloat;
    private static final Stack<ShaderStackEntry> m_shaderStack;
    
    public static void glBlendFunc(final int n, final int n2) {
        if (SpriteRenderer.instance != null && SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            GLState.BlendFuncSeparate.set(IndieGL.temp4Ints.set(n, n2, n, n2));
        }
    }
    
    public static void glBlendFuncSeparate(final int n, final int n2, final int n3, final int n4) {
        if (SpriteRenderer.instance != null && SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            GLState.BlendFuncSeparate.set(IndieGL.temp4Ints.set(n, n2, n3, n4));
        }
    }
    
    public static void StartShader(final Shader shader) {
        StartShader(shader, IsoCamera.frameState.playerIndex);
    }
    
    public static void StartShader(final Shader shader, final int n) {
        if (shader != null) {
            StartShader(shader.getID(), n);
        }
        else {
            EndShader();
        }
    }
    
    public static void StartShader(final int n) {
        StartShader(n, IsoCamera.frameState.playerIndex);
    }
    
    public static void StartShader(final int n, final int n2) {
        SpriteRenderer.instance.StartShader(n, n2);
    }
    
    public static void EndShader() {
        SpriteRenderer.instance.EndShader();
    }
    
    public static void pushShader(final Shader shader) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        IndieGL.m_shaderStack.push(ShaderStackEntry.alloc(shader, playerIndex));
        StartShader(shader, playerIndex);
    }
    
    public static void popShader(final Shader shader) {
        if (IndieGL.m_shaderStack.isEmpty()) {
            throw new RuntimeException("Push/PopShader mismatch. Cannot pop. Stack is empty.");
        }
        if (IndieGL.m_shaderStack.peek().getShader() != shader) {
            throw new RuntimeException("Push/PopShader mismatch. The popped shader != the pushed shader.");
        }
        IndieGL.m_shaderStack.pop().release();
        if (IndieGL.m_shaderStack.isEmpty()) {
            EndShader();
            return;
        }
        final ShaderStackEntry shaderStackEntry = IndieGL.m_shaderStack.peek();
        StartShader(shaderStackEntry.getShader(), shaderStackEntry.getPlayerIndex());
    }
    
    public static void bindShader(final Shader shader, final Runnable runnable) {
        pushShader(shader);
        try {
            runnable.run();
        }
        finally {
            popShader(shader);
        }
    }
    
    public static <T1> void bindShader(final Shader shader, final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
        Lambda.capture(shader, t1, callback, (genericStack, shader2, o, callback2) -> bindShader(shader2, genericStack.invoker(o, callback2)));
    }
    
    public static <T1, T2> void bindShader(final Shader shader, final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
        Lambda.capture(shader, t1, t2, callback, (genericStack, shader2, o, o2, callback2) -> bindShader(shader2, genericStack.invoker(o, o2, callback2)));
    }
    
    public static <T1, T2, T3> void bindShader(final Shader shader, final T1 t1, final T2 t2, final T3 t3, final Invokers.Params3.ICallback<T1, T2, T3> callback) {
        Lambda.capture(shader, t1, t2, t3, callback, (genericStack, shader2, o, o2, o3, callback2) -> bindShader(shader2, genericStack.invoker(o, o2, o3, callback2)));
    }
    
    public static <T1, T2, T3, T4> void bindShader(final Shader shader, final T1 t1, final T2 t2, final T3 t3, final T4 t4, final Invokers.Params4.ICallback<T1, T2, T3, T4> callback) {
        Lambda.capture(shader, t1, t2, t3, t4, callback, (genericStack, shader2, o, o2, o3, o4, callback2) -> bindShader(shader2, genericStack.invoker(o, o2, o3, o4, callback2)));
    }
    
    private static ShaderProgram.Uniform getShaderUniform(final Shader shader, final String s, final int n) {
        if (shader == null) {
            return null;
        }
        final ShaderProgram program = shader.getProgram();
        if (program == null) {
            return null;
        }
        return program.getUniform(s, n, false);
    }
    
    public static void shaderSetSamplerUnit(final Shader shader, final String s, final int sampler) {
        final ShaderProgram.Uniform shaderUniform = getShaderUniform(shader, s, 35678);
        if (shaderUniform != null) {
            shaderUniform.sampler = sampler;
            ShaderUpdate1i(shader.getID(), shaderUniform.loc, sampler);
        }
    }
    
    public static void shaderSetValue(final Shader shader, final String s, final float n) {
        final ShaderProgram.Uniform shaderUniform = getShaderUniform(shader, s, 5126);
        if (shaderUniform != null) {
            ShaderUpdate1f(shader.getID(), shaderUniform.loc, n);
        }
    }
    
    public static void shaderSetValue(final Shader shader, final String s, final int n) {
        final ShaderProgram.Uniform shaderUniform = getShaderUniform(shader, s, 5124);
        if (shaderUniform != null) {
            ShaderUpdate1i(shader.getID(), shaderUniform.loc, n);
        }
    }
    
    public static void shaderSetValue(final Shader shader, final String s, final Vector2 vector2) {
        shaderSetVector2(shader, s, vector2.x, vector2.y);
    }
    
    public static void shaderSetValue(final Shader shader, final String s, final Vector3 vector3) {
        shaderSetVector3(shader, s, vector3.x, vector3.y, vector3.z);
    }
    
    public static void shaderSetValue(final Shader shader, final String s, final Vector4 vector4) {
        shaderSetVector4(shader, s, vector4.x, vector4.y, vector4.z, vector4.w);
    }
    
    public static void shaderSetVector2(final Shader shader, final String s, final float n, final float n2) {
        final ShaderProgram.Uniform shaderUniform = getShaderUniform(shader, s, 35664);
        if (shaderUniform != null) {
            ShaderUpdate2f(shader.getID(), shaderUniform.loc, n, n2);
        }
    }
    
    public static void shaderSetVector3(final Shader shader, final String s, final float n, final float n2, final float n3) {
        final ShaderProgram.Uniform shaderUniform = getShaderUniform(shader, s, 35665);
        if (shaderUniform != null) {
            ShaderUpdate3f(shader.getID(), shaderUniform.loc, n, n2, n3);
        }
    }
    
    public static void shaderSetVector4(final Shader shader, final String s, final float n, final float n2, final float n3, final float n4) {
        final ShaderProgram.Uniform shaderUniform = getShaderUniform(shader, s, 35666);
        if (shaderUniform != null) {
            ShaderUpdate4f(shader.getID(), shaderUniform.loc, n, n2, n3, n4);
        }
    }
    
    public static void ShaderUpdate1i(final int n, final int n2, final int n3) {
        SpriteRenderer.instance.ShaderUpdate1i(n, n2, n3);
    }
    
    public static void ShaderUpdate1f(final int n, final int n2, final float n3) {
        SpriteRenderer.instance.ShaderUpdate1f(n, n2, n3);
    }
    
    public static void ShaderUpdate2f(final int n, final int n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate2f(n, n2, n3, n4);
    }
    
    public static void ShaderUpdate3f(final int n, final int n2, final float n3, final float n4, final float n5) {
        SpriteRenderer.instance.ShaderUpdate3f(n, n2, n3, n4, n5);
    }
    
    public static void ShaderUpdate4f(final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        SpriteRenderer.instance.ShaderUpdate4f(n, n2, n3, n4, n5, n6);
    }
    
    public static void glBlendFuncA(final int n, final int n2) {
        GL11.glBlendFunc(n, n2);
    }
    
    public static void glEnable(final int n) {
        SpriteRenderer.instance.glEnable(n);
    }
    
    public static void glDoStartFrame(final int n, final int n2, final float n3, final int n4) {
        glDoStartFrame(n, n2, n3, n4, false);
    }
    
    public static void glDoStartFrame(final int n, final int n2, final float n3, final int n4, final boolean b) {
        SpriteRenderer.instance.glDoStartFrame(n, n2, n3, n4, b);
    }
    
    public static void glDoEndFrame() {
        SpriteRenderer.instance.glDoEndFrame();
    }
    
    public static void glColorMask(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        GLState.ColorMask.set(IndieGL.temp4Booleans.set(b, b2, b3, b4));
    }
    
    public static void glColorMaskA(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        GL11.glColorMask(b, b, b4, b4);
    }
    
    public static void glEnableA(final int n) {
        GL11.glEnable(n);
    }
    
    public static void glAlphaFunc(final int n, final float n2) {
        GLState.AlphaFunc.set(IndieGL.tempIntFloat.set(n, n2));
    }
    
    public static void glAlphaFuncA(final int n, final float n2) {
        GL11.glAlphaFunc(n, n2);
    }
    
    public static void glStencilFunc(final int n, final int n2, final int n3) {
        GLState.StencilFunc.set(IndieGL.temp3Ints.set(n, n2, n3));
    }
    
    public static void glStencilFuncA(final int n, final int n2, final int n3) {
        GL11.glStencilFunc(n, n2, n3);
    }
    
    public static void glStencilOp(final int n, final int n2, final int n3) {
        GLState.StencilOp.set(IndieGL.temp3Ints.set(n, n2, n3));
    }
    
    public static void glStencilOpA(final int n, final int n2, final int n3) {
        GL11.glStencilOp(n, n2, n3);
    }
    
    public static void glTexParameteri(final int n, final int n2, final int n3) {
        SpriteRenderer.instance.glTexParameteri(n, n2, n3);
    }
    
    public static void glTexParameteriActual(final int n, final int n2, final int n3) {
        GL11.glTexParameteri(n, n2, n3);
    }
    
    public static void glStencilMask(final int n) {
        GLState.StencilMask.set(IndieGL.tempInt.set(n));
    }
    
    public static void glStencilMaskA(final int n) {
        GL11.glStencilMask(n);
    }
    
    public static void glDisable(final int n) {
        SpriteRenderer.instance.glDisable(n);
    }
    
    public static void glClear(final int n) {
        SpriteRenderer.instance.glClear(n);
    }
    
    public static void glClearA(final int n) {
        GL11.glClear(n);
    }
    
    public static void glDisableA(final int n) {
        GL11.glDisable(n);
    }
    
    public static void glLoadIdentity() {
        SpriteRenderer.instance.glLoadIdentity();
    }
    
    public static void glBind(final Texture texture) {
        SpriteRenderer.instance.glBind(texture.getID());
    }
    
    public static void enableAlphaTest() {
        GLState.AlphaTest.set(GLState.CBooleanValue.TRUE);
    }
    
    public static void disableAlphaTest() {
        GLState.AlphaTest.set(GLState.CBooleanValue.FALSE);
    }
    
    public static void enableStencilTest() {
        GLState.StencilTest.set(GLState.CBooleanValue.TRUE);
    }
    
    public static void disableStencilTest() {
        GLState.StencilTest.set(GLState.CBooleanValue.FALSE);
    }
    
    public static boolean isMaxZoomLevel() {
        return SpriteRenderer.instance.isMaxZoomLevel();
    }
    
    public static boolean isMinZoomLevel() {
        return SpriteRenderer.instance.isMinZoomLevel();
    }
    
    static {
        IndieGL.nCount = 0;
        tempInt = new GLState.CIntValue();
        temp2Ints = new GLState.C2IntsValue();
        temp3Ints = new GLState.C3IntsValue();
        temp4Ints = new GLState.C4IntsValue();
        temp4Booleans = new GLState.C4BooleansValue();
        tempIntFloat = new GLState.CIntFloatValue();
        m_shaderStack = new Stack<ShaderStackEntry>();
    }
}
