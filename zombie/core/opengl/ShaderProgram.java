// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import org.lwjglx.BufferUtils;
import zombie.SystemDisabler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.textures.Texture;
import org.lwjgl.util.vector.Matrix4f;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import java.nio.ByteBuffer;
import zombie.ZomboidFileSystem;
import zombie.DebugFileWatcher;
import java.nio.IntBuffer;
import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.opengl.GL20;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.Iterator;
import java.util.Collection;
import org.lwjgl.opengl.ARBShaderObjects;
import java.nio.FloatBuffer;
import zombie.PredicatedFileWatcher;
import java.util.HashMap;
import java.util.ArrayList;

public final class ShaderProgram
{
    private int m_shaderID;
    private final String m_name;
    private final boolean m_isStatic;
    private final ArrayList<ShaderUnit> m_vertexUnits;
    private final ArrayList<ShaderUnit> m_fragmentUnits;
    private final HashMap<String, PredicatedFileWatcher> m_fileWatchers;
    private boolean m_sourceFilesChanged;
    private boolean m_compileFailed;
    private final HashMap<String, Uniform> uniformsByName;
    private final ArrayList<IShaderProgramListener> m_onCompiledListeners;
    private final int[] m_uvScaleUniforms;
    private static FloatBuffer floatBuffer;
    
    private ShaderProgram(final String name, final boolean isStatic) {
        this.m_shaderID = 0;
        this.m_vertexUnits = new ArrayList<ShaderUnit>();
        this.m_fragmentUnits = new ArrayList<ShaderUnit>();
        this.m_fileWatchers = new HashMap<String, PredicatedFileWatcher>();
        this.m_sourceFilesChanged = false;
        this.m_compileFailed = false;
        this.uniformsByName = new HashMap<String, Uniform>();
        this.m_onCompiledListeners = new ArrayList<IShaderProgramListener>();
        this.m_uvScaleUniforms = new int[10];
        this.m_name = name;
        this.m_isStatic = isStatic;
    }
    
    public String getName() {
        return this.m_name;
    }
    
    public void addCompileListener(final IShaderProgramListener shaderProgramListener) {
        if (this.m_onCompiledListeners.contains(shaderProgramListener)) {
            return;
        }
        this.m_onCompiledListeners.add(shaderProgramListener);
    }
    
    public void removeCompileListener(final IShaderProgramListener o) {
        this.m_onCompiledListeners.remove(o);
    }
    
    private void invokeProgramCompiledEvent() {
        this.Start();
        this.m_uvScaleUniforms[0] = ARBShaderObjects.glGetUniformLocationARB(this.m_shaderID, (CharSequence)"UVScale");
        for (int i = 1; i < this.m_uvScaleUniforms.length; ++i) {
            this.m_uvScaleUniforms[i] = ARBShaderObjects.glGetUniformLocationARB(this.m_shaderID, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
        }
        this.End();
        if (this.m_onCompiledListeners.isEmpty()) {
            return;
        }
        final Iterator<IShaderProgramListener> iterator = new ArrayList<IShaderProgramListener>(this.m_onCompiledListeners).iterator();
        while (iterator.hasNext()) {
            iterator.next().callback(this);
        }
    }
    
    public void compile() {
        this.m_sourceFilesChanged = false;
        this.m_compileFailed = false;
        if (this.isCompiled()) {
            this.destroy();
        }
        final String name = this.getName();
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, this.m_isStatic ? "(Static)" : ""));
        }
        this.m_shaderID = ARBShaderObjects.glCreateProgramObjectARB();
        if (this.m_shaderID == 0) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
            return;
        }
        this.addShader(this.getRootVertFileName(), ShaderUnit.Type.Vert);
        this.addShader(this.getRootFragFileName(name), ShaderUnit.Type.Frag);
        this.registerFileWatchers();
        if (!this.compileAllShaderUnits()) {
            this.m_compileFailed = true;
            this.destroy();
            return;
        }
        if (!this.attachAllShaderUnits()) {
            this.m_compileFailed = true;
            this.destroy();
            return;
        }
        this.registerFileWatchers();
        ARBShaderObjects.glLinkProgramARB(this.m_shaderID);
        if (ARBShaderObjects.glGetObjectParameteriARB(this.m_shaderID, 35714) == 0) {
            this.m_compileFailed = true;
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, name, this.m_isStatic));
            DebugLog.Shader.error((Object)getLogInfo(this.m_shaderID));
            this.destroy();
            return;
        }
        ARBShaderObjects.glValidateProgramARB(this.m_shaderID);
        if (ARBShaderObjects.glGetObjectParameteriARB(this.m_shaderID, 35715) == 0) {
            this.m_compileFailed = true;
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, name, this.m_isStatic));
            DebugLog.Shader.error((Object)getLogInfo(this.m_shaderID));
            this.destroy();
            return;
        }
        this.onCompileSuccess();
    }
    
    private void onCompileSuccess() {
        if (!this.isCompiled()) {
            return;
        }
        this.uniformsByName.clear();
        this.Start();
        final int shaderID = this.m_shaderID;
        final int glGetProgrami = GL20.glGetProgrami(shaderID, 35718);
        int n = 0;
        final IntBuffer memAllocInt = MemoryUtil.memAllocInt(1);
        final IntBuffer memAllocInt2 = MemoryUtil.memAllocInt(1);
        for (int i = 0; i < glGetProgrami; ++i) {
            final String glGetActiveUniform = GL20.glGetActiveUniform(shaderID, i, 255, memAllocInt, memAllocInt2);
            final int glGetUniformLocation = GL20.glGetUniformLocation(shaderID, (CharSequence)glGetActiveUniform);
            if (glGetUniformLocation != -1) {
                final int value = memAllocInt.get(0);
                final int value2 = memAllocInt2.get(0);
                final Uniform value3 = new Uniform();
                this.uniformsByName.put(glGetActiveUniform, value3);
                value3.name = glGetActiveUniform;
                value3.loc = glGetUniformLocation;
                value3.size = value;
                value3.type = value2;
                if (DebugLog.isEnabled(DebugType.Shader)) {
                    DebugLog.Shader.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, glGetActiveUniform, glGetUniformLocation, value2, value));
                }
                if (value3.type == 35678) {
                    if (n != 0) {
                        GL20.glUniform1i(value3.loc, n);
                    }
                    value3.sampler = n++;
                }
            }
        }
        MemoryUtil.memFree((Buffer)memAllocInt);
        MemoryUtil.memFree((Buffer)memAllocInt2);
        this.End();
        PZGLUtil.checkGLError(true);
        this.invokeProgramCompiledEvent();
    }
    
    private void registerFileWatchers() {
        final Iterator<PredicatedFileWatcher> iterator = this.m_fileWatchers.values().iterator();
        while (iterator.hasNext()) {
            DebugFileWatcher.instance.remove(iterator.next());
        }
        this.m_fileWatchers.clear();
        final Iterator<ShaderUnit> iterator2 = this.m_vertexUnits.iterator();
        while (iterator2.hasNext()) {
            this.registerFileWatcherInternal(iterator2.next().getFileName(), p0 -> this.onShaderFileChanged());
        }
        final Iterator<ShaderUnit> iterator3 = this.m_fragmentUnits.iterator();
        while (iterator3.hasNext()) {
            this.registerFileWatcherInternal(iterator3.next().getFileName(), p0 -> this.onShaderFileChanged());
        }
    }
    
    private void registerFileWatcherInternal(String string, final PredicatedFileWatcher.IPredicatedFileWatcherCallback predicatedFileWatcherCallback) {
        string = ZomboidFileSystem.instance.getString(string);
        final PredicatedFileWatcher value = new PredicatedFileWatcher(string, predicatedFileWatcherCallback);
        this.m_fileWatchers.put(string, value);
        DebugFileWatcher.instance.add(value);
    }
    
    private void onShaderFileChanged() {
        this.m_sourceFilesChanged = true;
    }
    
    private boolean compileAllShaderUnits() {
        for (final ShaderUnit shaderUnit : this.getShaderUnits()) {
            if (!shaderUnit.compile()) {
                DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getName(), shaderUnit.getFileName()));
                return false;
            }
        }
        return true;
    }
    
    private boolean attachAllShaderUnits() {
        for (final ShaderUnit shaderUnit : this.getShaderUnits()) {
            if (!shaderUnit.attach()) {
                DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getName(), shaderUnit.getFileName()));
                return false;
            }
        }
        return true;
    }
    
    private ArrayList<ShaderUnit> getShaderUnits() {
        final ArrayList<ShaderUnit> list = new ArrayList<ShaderUnit>();
        list.addAll(this.m_vertexUnits);
        list.addAll(this.m_fragmentUnits);
        return list;
    }
    
    private String getRootVertFileName() {
        if (this.m_isStatic) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName());
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName());
    }
    
    private String getRootFragFileName(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public ShaderUnit addShader(final String s, final ShaderUnit.Type type) {
        final ShaderUnit shader = this.findShader(s, type);
        if (shader != null) {
            return shader;
        }
        final ArrayList<ShaderUnit> shaderList = this.getShaderList(type);
        final ShaderUnit e = new ShaderUnit(this, s, type);
        shaderList.add(e);
        return e;
    }
    
    private ArrayList<ShaderUnit> getShaderList(final ShaderUnit.Type type) {
        return (type == ShaderUnit.Type.Vert) ? this.m_vertexUnits : this.m_fragmentUnits;
    }
    
    private ShaderUnit findShader(final String anObject, final ShaderUnit.Type type) {
        final ArrayList<ShaderUnit> shaderList = this.getShaderList(type);
        ShaderUnit shaderUnit = null;
        for (final ShaderUnit shaderUnit2 : shaderList) {
            if (shaderUnit2.getFileName().equals(anObject)) {
                shaderUnit = shaderUnit2;
                break;
            }
        }
        return shaderUnit;
    }
    
    public static ShaderProgram createShaderProgram(final String s, final boolean b, final boolean b2) {
        final ShaderProgram shaderProgram = new ShaderProgram(s, b);
        if (b2) {
            shaderProgram.compile();
        }
        return shaderProgram;
    }
    
    @Deprecated
    public static int createVertShader(final String s) {
        final ShaderUnit shaderUnit = new ShaderUnit(null, s, ShaderUnit.Type.Vert);
        shaderUnit.compile();
        return shaderUnit.getGLID();
    }
    
    @Deprecated
    public static int createFragShader(final String s) {
        final ShaderUnit shaderUnit = new ShaderUnit(null, s, ShaderUnit.Type.Frag);
        shaderUnit.compile();
        return shaderUnit.getGLID();
    }
    
    public static void printLogInfo(final int n) {
        final IntBuffer memAllocInt = MemoryUtil.memAllocInt(1);
        ARBShaderObjects.glGetObjectParameterivARB(n, 35716, memAllocInt);
        final int value = memAllocInt.get();
        MemoryUtil.memFree((Buffer)memAllocInt);
        if (value <= 1) {
            return;
        }
        final ByteBuffer memAlloc = MemoryUtil.memAlloc(value);
        memAllocInt.flip();
        ARBShaderObjects.glGetInfoLogARB(n, memAllocInt, memAlloc);
        final byte[] array = new byte[value];
        memAlloc.get(array);
        DebugLog.Shader.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new String(array)));
        MemoryUtil.memFree((Buffer)memAlloc);
    }
    
    public static String getLogInfo(final int n) {
        return ARBShaderObjects.glGetInfoLogARB(n, ARBShaderObjects.glGetObjectParameteriARB(n, 35716));
    }
    
    public boolean isCompiled() {
        return this.m_shaderID != 0;
    }
    
    public void destroy() {
        if (this.m_shaderID == 0) {
            this.m_vertexUnits.clear();
            this.m_fragmentUnits.clear();
            return;
        }
        try {
            DebugLog.Shader.debugln(this.getName());
            final Iterator<ShaderUnit> iterator = this.m_vertexUnits.iterator();
            while (iterator.hasNext()) {
                iterator.next().destroy();
            }
            this.m_vertexUnits.clear();
            final Iterator<ShaderUnit> iterator2 = this.m_fragmentUnits.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().destroy();
            }
            this.m_fragmentUnits.clear();
            ARBShaderObjects.glDeleteObjectARB(this.m_shaderID);
            PZGLUtil.checkGLError(true);
        }
        finally {
            this.m_vertexUnits.clear();
            this.m_fragmentUnits.clear();
            this.m_shaderID = 0;
        }
    }
    
    public int getShaderID() {
        if ((!this.m_compileFailed && !this.isCompiled()) || this.m_sourceFilesChanged) {
            RenderThread.invokeOnRenderContext(this::compile);
        }
        return this.m_shaderID;
    }
    
    public void Start() {
        ARBShaderObjects.glUseProgramObjectARB(this.getShaderID());
    }
    
    public void End() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }
    
    public void setSamplerUnit(final String s, final int sampler) {
        final Uniform uniform = this.getUniform(s, 35678);
        if (uniform != null) {
            uniform.sampler = sampler;
            ARBShaderObjects.glUniform1iARB(uniform.loc, sampler);
        }
    }
    
    public void setValueColor(final String s, final int n) {
        this.setVector4(s, 0.003921569f * (n >> 24 & 0xFF), 0.003921569f * (n >> 16 & 0xFF), 0.003921569f * (n >> 8 & 0xFF), 0.003921569f * (n & 0xFF));
    }
    
    public void setValueColorRGB(final String s, final int n) {
        this.setValueColor(s, n & 0xFF);
    }
    
    public void setValue(final String s, final float n) {
        final Uniform uniform = this.getUniform(s, 5126);
        if (uniform != null) {
            ARBShaderObjects.glUniform1fARB(uniform.loc, n);
        }
    }
    
    public void setValue(final String s, final int n) {
        final Uniform uniform = this.getUniform(s, 5124);
        if (uniform != null) {
            ARBShaderObjects.glUniform1iARB(uniform.loc, n);
        }
    }
    
    public void setValue(final String s, final Vector3 vector3) {
        this.setVector3(s, vector3.x, vector3.y, vector3.z);
    }
    
    public void setValue(final String s, final Vector2 vector2) {
        this.setVector2(s, vector2.x, vector2.y);
    }
    
    public void setVector2(final String s, final float n, final float n2) {
        final Uniform uniform = this.getUniform(s, 35664);
        if (uniform != null) {
            this.setVector2(uniform.loc, n, n2);
        }
    }
    
    public void setVector3(final String s, final float n, final float n2, final float n3) {
        final Uniform uniform = this.getUniform(s, 35665);
        if (uniform != null) {
            this.setVector3(uniform.loc, n, n2, n3);
        }
    }
    
    public void setVector4(final String s, final float n, final float n2, final float n3, final float n4) {
        final Uniform uniform = this.getUniform(s, 35666);
        if (uniform != null) {
            this.setVector4(uniform.loc, n, n2, n3, n4);
        }
    }
    
    public final Uniform getUniform(final String s, final int n) {
        return this.getUniform(s, n, false);
    }
    
    public Uniform getUniform(final String key, final int n, final boolean b) {
        final Uniform uniform = this.uniformsByName.get(key);
        if (uniform == null) {
            if (b) {
                DebugLog.Shader.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key));
            }
            return null;
        }
        if (uniform.type != n) {
            DebugLog.Shader.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, key, n, uniform.type));
            return null;
        }
        return uniform;
    }
    
    public void setValue(final String s, final Matrix4f matrix4f) {
        final Uniform uniform = this.getUniform(s, 35676);
        if (uniform != null) {
            this.setTransformMatrix(uniform.loc, matrix4f);
        }
    }
    
    public void setValue(final String s, final Texture texture, final int n) {
        final Uniform uniform = this.getUniform(s, 35678);
        if (uniform == null || texture == null) {
            return;
        }
        if (uniform.sampler != n) {
            uniform.sampler = n;
            GL20.glUniform1i(uniform.loc, uniform.sampler);
        }
        GL13.glActiveTexture(33984 + uniform.sampler);
        GL11.glEnable(3553);
        final int lastTextureID = Texture.lastTextureID;
        texture.bind();
        if (uniform.sampler > 0) {
            Texture.lastTextureID = lastTextureID;
        }
        final Vector2 uvScale = texture.getUVScale(L_setValue.vector2);
        this.setUVScale(n, uvScale.x, uvScale.y);
        if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
            PZGLUtil.checkGLErrorThrow("Shader.setValue<Texture> Loc: %s, Tex: %s, samplerUnit: %d", s, texture, n);
        }
    }
    
    private void setUVScale(final int n, final float n2, final float n3) {
        if (n < 0) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            return;
        }
        if (n >= this.m_uvScaleUniforms.length) {
            String s = "UVScale";
            if (n > 0) {
                s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n);
            }
            this.setVector2(s, n2, n3);
            return;
        }
        final int n4 = this.m_uvScaleUniforms[n];
        if (n4 >= 0) {
            this.setVector2(n4, n2, n3);
        }
    }
    
    public void setVector2(final int n, final float n2, final float n3) {
        ARBShaderObjects.glUniform2fARB(n, n2, n3);
    }
    
    public void setVector3(final int n, final float n2, final float n3, final float n4) {
        ARBShaderObjects.glUniform3fARB(n, n2, n3, n4);
    }
    
    public void setVector4(final int n, final float n2, final float n3, final float n4, final float n5) {
        ARBShaderObjects.glUniform4fARB(n, n2, n3, n4, n5);
    }
    
    void setTransformMatrix(final int n, final Matrix4f matrix4f) {
        if (ShaderProgram.floatBuffer == null) {
            ShaderProgram.floatBuffer = BufferUtils.createFloatBuffer(38400);
        }
        ShaderProgram.floatBuffer.clear();
        matrix4f.store(ShaderProgram.floatBuffer);
        ShaderProgram.floatBuffer.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(n, true, ShaderProgram.floatBuffer);
    }
    
    public static class Uniform
    {
        public String name;
        public int size;
        public int loc;
        public int type;
        public int sampler;
    }
    
    private static final class L_setValue
    {
        static final Vector2 vector2;
        
        static {
            vector2 = new Vector2();
        }
    }
}
