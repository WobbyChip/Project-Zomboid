// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.util.StringUtils;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.BufferedReader;
import zombie.core.IndieFileLoader;
import java.util.Iterator;
import org.lwjgl.opengl.ARBShaderObjects;
import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public final class ShaderUnit
{
    private final ShaderProgram m_parentProgram;
    private final String m_fileName;
    private final Type m_unitType;
    private int m_glID;
    private boolean m_isAttached;
    
    public ShaderUnit(final ShaderProgram parentProgram, final String fileName, final Type unitType) {
        this.m_parentProgram = parentProgram;
        this.m_fileName = fileName;
        this.m_unitType = unitType;
        this.m_glID = 0;
        this.m_isAttached = false;
    }
    
    public String getFileName() {
        return this.m_fileName;
    }
    
    public boolean isCompiled() {
        return this.m_glID != 0;
    }
    
    public boolean compile() {
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln(this.getFileName());
        }
        final int glType = getGlType(this.m_unitType);
        final ArrayList<String> list = new ArrayList<String>();
        final String loadShaderFile = this.loadShaderFile(this.m_fileName, list);
        if (loadShaderFile == null) {
            return false;
        }
        for (final String s : list) {
            if (this.m_parentProgram == null) {
                DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getFileName(), s));
                break;
            }
            final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            if (DebugLog.isEnabled(DebugType.Shader)) {
                DebugLog.Shader.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getFileName(), s2));
            }
            final ShaderUnit addShader = this.m_parentProgram.addShader(s2, this.m_unitType);
            if (!addShader.isCompiled() && !addShader.compile()) {
                DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getFileName(), s2));
                return false;
            }
        }
        final int glCreateShaderObjectARB = ARBShaderObjects.glCreateShaderObjectARB(glType);
        if (glCreateShaderObjectARB == 0) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getFileName(), loadShaderFile));
            return false;
        }
        ARBShaderObjects.glShaderSourceARB(glCreateShaderObjectARB, (CharSequence)loadShaderFile);
        ARBShaderObjects.glCompileShaderARB(glCreateShaderObjectARB);
        ShaderProgram.printLogInfo(glCreateShaderObjectARB);
        this.m_glID = glCreateShaderObjectARB;
        return true;
    }
    
    public boolean attach() {
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln(this.getFileName());
        }
        if (this.getParentShaderProgramGLID() == 0) {
            DebugLog.Shader.error((Object)"Parent program does not exist.");
            return false;
        }
        if (!this.isCompiled()) {
            this.compile();
        }
        if (!this.isCompiled()) {
            return false;
        }
        ARBShaderObjects.glAttachObjectARB(this.getParentShaderProgramGLID(), this.getGLID());
        if (!PZGLUtil.checkGLError(false)) {
            this.destroy();
            return false;
        }
        return this.m_isAttached = true;
    }
    
    public void destroy() {
        if (this.m_glID == 0) {
            this.m_isAttached = false;
            return;
        }
        DebugLog.Shader.debugln(this.getFileName());
        try {
            if (this.m_isAttached && this.getParentShaderProgramGLID() != 0) {
                ARBShaderObjects.glDetachObjectARB(this.getParentShaderProgramGLID(), this.m_glID);
                if (!PZGLUtil.checkGLError(false)) {
                    DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getFileName()));
                    return;
                }
            }
            ARBShaderObjects.glDeleteObjectARB(this.m_glID);
            PZGLUtil.checkGLError(false);
        }
        finally {
            this.m_glID = 0;
            this.m_isAttached = false;
        }
    }
    
    public int getGLID() {
        return this.m_glID;
    }
    
    public int getParentShaderProgramGLID() {
        return (this.m_parentProgram != null) ? this.m_parentProgram.getShaderID() : 0;
    }
    
    private static int getGlType(final Type type) {
        return (type == Type.Vert) ? 35633 : 35632;
    }
    
    private String loadShaderFile(final String s, final ArrayList<String> list) {
        list.clear();
        String s2 = this.preProcessShaderFile(s, list);
        if (s2 == null) {
            return null;
        }
        final int index = s2.indexOf("#");
        if (index > 0) {
            s2 = s2.substring(index);
        }
        return s2;
    }
    
    private String preProcessShaderFile(final String s, final ArrayList<String> list) {
        final StringBuilder sb = new StringBuilder();
        try {
            final InputStreamReader streamReader = IndieFileLoader.getStreamReader(s, false);
            try {
                final BufferedReader bufferedReader = new BufferedReader(streamReader);
                try {
                    final String property = System.getProperty("line.separator");
                    for (String s2 = bufferedReader.readLine(); s2 != null; s2 = bufferedReader.readLine()) {
                        final String trim = s2.trim();
                        if (!trim.startsWith("#include ") || !this.processIncludeLine(s, sb, trim, property, list)) {
                            sb.append(trim).append(property);
                        }
                    }
                    bufferedReader.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedReader.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                if (streamReader != null) {
                    streamReader.close();
                }
            }
            catch (Throwable t2) {
                if (streamReader != null) {
                    try {
                        streamReader.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            ex.printStackTrace(DebugLog.Shader);
            return null;
        }
        return sb.toString();
    }
    
    private boolean processIncludeLine(final String s, final StringBuilder sb, final String str, final String s2, final ArrayList<String> list) {
        final String substring = str.substring("#include ".length());
        if (!substring.startsWith("\"") || !substring.endsWith("\"")) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, substring));
            return false;
        }
        final String parentFolder = this.getParentFolder(s);
        final String lowerCase = substring.substring(1, substring.length() - 1).trim().replace('\\', '/').toLowerCase();
        if (lowerCase.contains(":")) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, substring));
            return false;
        }
        if (lowerCase.startsWith("/")) {
            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, substring));
            return false;
        }
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, parentFolder, lowerCase);
        final ArrayList<String> list2 = new ArrayList<String>();
        for (final String e : s3.split("/")) {
            if (!e.equals(".")) {
                if (!e.isEmpty()) {
                    if (StringUtils.isNullOrWhitespace(e)) {
                        DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, substring));
                        return false;
                    }
                    if (e.equals("..")) {
                        if (list2.isEmpty()) {
                            DebugLog.Shader.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, substring));
                            return false;
                        }
                        list2.remove(list2.size() - 1);
                    }
                    else {
                        list2.add(e);
                    }
                }
            }
        }
        final StringBuilder sb2 = new StringBuilder(s3.length());
        for (final String str2 : list2) {
            if (sb2.length() > 0) {
                sb2.append('/');
            }
            sb2.append(str2);
        }
        final String string = sb2.toString();
        if (list.contains(string)) {
            sb.append("// Duplicate Include, skipped. ").append(str).append(s2);
            return true;
        }
        list.add(string);
        final String preProcessShaderFile = this.preProcessShaderFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string), list);
        sb.append(s2);
        sb.append("// Include begin ").append(str).append(s2);
        sb.append(preProcessShaderFile).append(s2);
        sb.append("// Include end   ").append(str).append(s2);
        sb.append(s2);
        return true;
    }
    
    private String getParentFolder(final String s) {
        final int lastIndex = s.lastIndexOf("/");
        if (lastIndex > -1) {
            return s.substring(0, lastIndex);
        }
        final int lastIndex2 = s.lastIndexOf("\\");
        if (lastIndex2 > -1) {
            return s.substring(0, lastIndex2);
        }
        return "";
    }
    
    public enum Type
    {
        Vert, 
        Frag;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.Vert, Type.Frag };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
