// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.core.Color;
import java.util.function.Consumer;
import zombie.util.list.PZArrayUtil;
import zombie.iso.IsoWorld;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.ui.UIManager;
import zombie.core.Core;
import org.lwjgl.opengl.GL14;
import zombie.IndieGL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.core.skinnedmodel.ModelManager;

public final class TextureDraw
{
    public Type type;
    public int a;
    public int b;
    public float f1;
    public float[] vars;
    public int c;
    public int d;
    public int col0;
    public int col1;
    public int col2;
    public int col3;
    public float x0;
    public float x1;
    public float x2;
    public float x3;
    public float y0;
    public float y1;
    public float y2;
    public float y3;
    public float u0;
    public float u1;
    public float u2;
    public float u3;
    public float v0;
    public float v1;
    public float v2;
    public float v3;
    public Texture tex;
    public Texture tex1;
    public byte useAttribArray;
    public float tex1_u0;
    public float tex1_u1;
    public float tex1_u2;
    public float tex1_u3;
    public float tex1_v0;
    public float tex1_v1;
    public float tex1_v2;
    public float tex1_v3;
    public int tex1_col0;
    public int tex1_col1;
    public int tex1_col2;
    public int tex1_col3;
    public boolean bSingleCol;
    public boolean flipped;
    public GenericDrawer drawer;
    
    public TextureDraw() {
        this.type = Type.glDraw;
        this.a = 0;
        this.b = 0;
        this.f1 = 0.0f;
        this.c = 0;
        this.d = 0;
        this.bSingleCol = false;
        this.flipped = false;
    }
    
    public static void glStencilFunc(final TextureDraw textureDraw, final int a, final int b, final int c) {
        textureDraw.type = Type.glStencilFunc;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
    }
    
    public static void glBuffer(final TextureDraw textureDraw, final int a, final int b) {
        textureDraw.type = Type.glBuffer;
        textureDraw.a = a;
        textureDraw.b = b;
    }
    
    public static void glStencilOp(final TextureDraw textureDraw, final int a, final int b, final int c) {
        textureDraw.type = Type.glStencilOp;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
    }
    
    public static void glDisable(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glDisable;
        textureDraw.a = a;
    }
    
    public static void glClear(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glClear;
        textureDraw.a = a;
    }
    
    public static void glClearColor(final TextureDraw textureDraw, final int col0, final int col2, final int col3, final int col4) {
        textureDraw.type = Type.glClearColor;
        textureDraw.col0 = col0;
        textureDraw.col1 = col2;
        textureDraw.col2 = col3;
        textureDraw.col3 = col4;
    }
    
    public static void glEnable(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glEnable;
        textureDraw.a = a;
    }
    
    public static void glAlphaFunc(final TextureDraw textureDraw, final int a, final float f1) {
        textureDraw.type = Type.glAlphaFunc;
        textureDraw.a = a;
        textureDraw.f1 = f1;
    }
    
    public static void glColorMask(final TextureDraw textureDraw, final int a, final int b, final int c, final int n) {
        textureDraw.type = Type.glColorMask;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.x0 = (float)n;
    }
    
    public static void glStencilMask(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glStencilMask;
        textureDraw.a = a;
    }
    
    public static void glBlendFunc(final TextureDraw textureDraw, final int a, final int b) {
        textureDraw.type = Type.glBlendFunc;
        textureDraw.a = a;
        textureDraw.b = b;
    }
    
    public static void glBlendFuncSeparate(final TextureDraw textureDraw, final int a, final int b, final int c, final int d) {
        textureDraw.type = Type.glBlendFuncSeparate;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.d = d;
    }
    
    public static void glBlendEquation(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glBlendEquation;
        textureDraw.a = a;
    }
    
    public static void glDoEndFrame(final TextureDraw textureDraw) {
        textureDraw.type = Type.glDoEndFrame;
    }
    
    public static void glDoEndFrameFx(final TextureDraw textureDraw, final int c) {
        textureDraw.type = Type.glDoEndFrameFx;
        textureDraw.c = c;
    }
    
    public static void glIgnoreStyles(final TextureDraw textureDraw, final boolean a) {
        textureDraw.type = Type.glIgnoreStyles;
        textureDraw.a = (a ? 1 : 0);
    }
    
    public static void glDoStartFrame(final TextureDraw textureDraw, final int n, final int n2, final float n3, final int n4) {
        glDoStartFrame(textureDraw, n, n2, n3, n4, false);
    }
    
    public static void glDoStartFrame(final TextureDraw textureDraw, final int a, final int b, final float f1, final int c, final boolean b2) {
        if (b2) {
            textureDraw.type = Type.glDoStartFrameText;
        }
        else {
            textureDraw.type = Type.glDoStartFrame;
        }
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.f1 = f1;
        textureDraw.c = c;
    }
    
    public static void glDoStartFrameFx(final TextureDraw textureDraw, final int a, final int b, final int c) {
        textureDraw.type = Type.glDoStartFrameFx;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
    }
    
    public static void glTexParameteri(final TextureDraw textureDraw, final int a, final int b, final int c) {
        textureDraw.type = Type.glTexParameteri;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
    }
    
    public static void drawModel(final TextureDraw textureDraw, final ModelManager.ModelSlot modelSlot) {
        textureDraw.type = Type.DrawModel;
        textureDraw.a = modelSlot.ID;
        textureDraw.drawer = ModelSlotRenderData.alloc().init(modelSlot);
    }
    
    public static void drawSkyBox(final TextureDraw textureDraw, final Shader shader, final int b, final int c, final int d) {
        textureDraw.type = Type.DrawSkyBox;
        textureDraw.a = shader.getID();
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.d = d;
        textureDraw.drawer = null;
    }
    
    public static void drawWater(final TextureDraw textureDraw, final Shader shader, final int b, final int c, final boolean d) {
        textureDraw.type = Type.DrawWater;
        textureDraw.a = shader.getID();
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.d = (d ? 1 : 0);
        textureDraw.drawer = null;
    }
    
    public static void drawPuddles(final TextureDraw textureDraw, final Shader shader, final int b, final int c, final int d) {
        textureDraw.type = Type.DrawPuddles;
        textureDraw.a = shader.getID();
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.d = d;
        textureDraw.drawer = null;
    }
    
    public static void drawParticles(final TextureDraw textureDraw, final int b, final int c, final int d) {
        textureDraw.type = Type.DrawParticles;
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.d = d;
        textureDraw.drawer = null;
    }
    
    public static void StartShader(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.StartShader;
        textureDraw.a = a;
    }
    
    public static void ShaderUpdate1i(final TextureDraw textureDraw, final int a, final int b, final int d) {
        textureDraw.type = Type.ShaderUpdate;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = -1;
        textureDraw.d = d;
    }
    
    public static void ShaderUpdate1f(final TextureDraw textureDraw, final int a, final int b, final float u0) {
        textureDraw.type = Type.ShaderUpdate;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = 1;
        textureDraw.u0 = u0;
    }
    
    public static void ShaderUpdate2f(final TextureDraw textureDraw, final int a, final int b, final float u0, final float u2) {
        textureDraw.type = Type.ShaderUpdate;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = 2;
        textureDraw.u0 = u0;
        textureDraw.u1 = u2;
    }
    
    public static void ShaderUpdate3f(final TextureDraw textureDraw, final int a, final int b, final float u0, final float u2, final float u3) {
        textureDraw.type = Type.ShaderUpdate;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = 3;
        textureDraw.u0 = u0;
        textureDraw.u1 = u2;
        textureDraw.u2 = u3;
    }
    
    public static void ShaderUpdate4f(final TextureDraw textureDraw, final int a, final int b, final float u0, final float u2, final float u3, final float u4) {
        textureDraw.type = Type.ShaderUpdate;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = 4;
        textureDraw.u0 = u0;
        textureDraw.u1 = u2;
        textureDraw.u2 = u3;
        textureDraw.u3 = u4;
    }
    
    public void run() {
        switch (this.type) {
            case StartShader: {
                ARBShaderObjects.glUseProgramObjectARB(this.a);
                if (Shader.ShaderMap.containsKey(this.a)) {
                    Shader.ShaderMap.get(this.a).startRenderThread(this);
                }
                if (this.a == 0) {
                    SpriteRenderer.ringBuffer.checkShaderChangedTexture1();
                    break;
                }
                break;
            }
            case ShaderUpdate: {
                if (this.c == 1) {
                    ARBShaderObjects.glUniform1fARB(this.b, this.u0);
                }
                if (this.c == 2) {
                    ARBShaderObjects.glUniform2fARB(this.b, this.u0, this.u1);
                }
                if (this.c == 3) {
                    ARBShaderObjects.glUniform3fARB(this.b, this.u0, this.u1, this.u2);
                }
                if (this.c == 4) {
                    ARBShaderObjects.glUniform4fARB(this.b, this.u0, this.u1, this.u2, this.u3);
                }
                if (this.c == -1) {
                    ARBShaderObjects.glUniform1iARB(this.b, this.d);
                    break;
                }
                break;
            }
            case BindActiveTexture: {
                GL13.glActiveTexture(this.a);
                if (this.b != -1) {
                    GL11.glBindTexture(3553, this.b);
                }
                GL13.glActiveTexture(33984);
                break;
            }
            case DrawModel: {
                if (this.drawer != null) {
                    this.drawer.render();
                    break;
                }
                break;
            }
            case DrawSkyBox: {
                try {
                    ModelManager.instance.RenderSkyBox(this, this.a, this.b, this.c, this.d);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
            case DrawWater: {
                try {
                    ModelManager.instance.RenderWater(this, this.a, this.b, this.d == 1);
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
                break;
            }
            case DrawPuddles: {
                try {
                    ModelManager.instance.RenderPuddles(this.a, this.b, this.d);
                }
                catch (Exception ex3) {
                    ex3.printStackTrace();
                }
                break;
            }
            case DrawParticles: {
                try {
                    ModelManager.instance.RenderParticles(this, this.b, this.c);
                }
                catch (Exception ex4) {
                    ex4.printStackTrace();
                }
                break;
            }
            case glClear: {
                IndieGL.glClearA(this.a);
                break;
            }
            case glClearColor: {
                GL11.glClearColor(this.col0 / 255.0f, this.col1 / 255.0f, this.col2 / 255.0f, this.col3 / 255.0f);
                break;
            }
            case glBlendFunc: {
                IndieGL.glBlendFuncA(this.a, this.b);
                break;
            }
            case glBlendFuncSeparate: {
                GL14.glBlendFuncSeparate(this.a, this.b, this.c, this.d);
                break;
            }
            case glColorMask: {
                IndieGL.glColorMaskA(this.a == 1, this.b == 1, this.c == 1, this.x0 == 1.0f);
                break;
            }
            case glTexParameteri: {
                IndieGL.glTexParameteriActual(this.a, this.b, this.c);
                break;
            }
            case glStencilMask: {
                IndieGL.glStencilMaskA(this.a);
                break;
            }
            case glDoEndFrame: {
                Core.getInstance().DoEndFrameStuff(this.a, this.b);
                break;
            }
            case glDoEndFrameFx: {
                Core.getInstance().DoEndFrameStuffFx(this.a, this.b, this.c);
                break;
            }
            case glDoStartFrame: {
                Core.getInstance().DoStartFrameStuff(this.a, this.b, this.f1, this.c);
                break;
            }
            case glDoStartFrameText: {
                Core.getInstance().DoStartFrameStuff(this.a, this.b, this.f1, this.c, true);
                break;
            }
            case glDoStartFrameFx: {
                Core.getInstance().DoStartFrameStuffSmartTextureFx(this.a, this.b, this.c);
                break;
            }
            case glStencilFunc: {
                IndieGL.glStencilFuncA(this.a, this.b, this.c);
                break;
            }
            case glBuffer: {
                if (!Core.getInstance().supportsFBO()) {
                    break;
                }
                if (this.a == 1) {
                    SpriteRenderer.instance.getRenderingState().fbo.startDrawing(false, false);
                    break;
                }
                if (this.a == 2) {
                    UIManager.UIFBO.startDrawing(true, true);
                    break;
                }
                if (this.a == 3) {
                    UIManager.UIFBO.endDrawing();
                    break;
                }
                if (this.a == 4) {
                    WeatherFxMask.getFboMask().startDrawing(true, true);
                    break;
                }
                if (this.a == 5) {
                    WeatherFxMask.getFboMask().endDrawing();
                    break;
                }
                if (this.a == 6) {
                    WeatherFxMask.getFboParticles().startDrawing(true, true);
                    break;
                }
                if (this.a == 7) {
                    WeatherFxMask.getFboParticles().endDrawing();
                    break;
                }
                SpriteRenderer.instance.getRenderingState().fbo.endDrawing();
                break;
            }
            case glStencilOp: {
                IndieGL.glStencilOpA(this.a, this.b, this.c);
                break;
            }
            case glLoadIdentity: {
                GL11.glLoadIdentity();
                break;
            }
            case glBind: {
                GL11.glBindTexture(3553, this.a);
                Texture.lastlastTextureID = Texture.lastTextureID;
                Texture.lastTextureID = this.a;
                break;
            }
            case glViewport: {
                GL11.glViewport(this.a, this.b, this.c, this.d);
                break;
            }
            case drawTerrain: {
                IsoWorld.instance.renderTerrain();
                break;
            }
            case doCoreIntParam: {
                Core.getInstance().FloatParamMap.put(this.a, this.f1);
                break;
            }
            case glDepthMask: {
                GL11.glDepthMask(this.a == 1);
            }
            case glAlphaFunc: {
                IndieGL.glAlphaFuncA(this.a, this.f1);
                break;
            }
            case glEnable: {
                IndieGL.glEnableA(this.a);
                break;
            }
            case glDisable: {
                IndieGL.glDisableA(this.a);
                break;
            }
            case glBlendEquation: {
                GL14.glBlendEquation(this.a);
                break;
            }
            case glIgnoreStyles: {
                SpriteRenderer.RingBuffer.IGNORE_STYLES = (this.a == 1);
                break;
            }
        }
    }
    
    public static void glDepthMask(final TextureDraw textureDraw, final boolean a) {
        textureDraw.type = Type.glDepthMask;
        textureDraw.a = (a ? 1 : 0);
    }
    
    public static void doCoreIntParam(final TextureDraw textureDraw, final int a, final float f1) {
        textureDraw.type = Type.doCoreIntParam;
        textureDraw.a = a;
        textureDraw.f1 = f1;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/textures/TextureDraw$Type;IIFLjava/lang/String;IIIIIIFFFFFFFFFFFFFFFFFFFFLzombie/core/textures/Texture;Lzombie/core/textures/Texture;BFFFFFFFFIIIIZ)Ljava/lang/String;, this.getClass().getSimpleName(), this.type, this.a, this.b, this.f1, (this.vars != null) ? PZArrayUtil.arrayToString(this.vars, "{", "}", ", ") : "null", this.c, this.d, this.col0, this.col1, this.col2, this.col3, this.x0, this.x1, this.x2, this.x3, this.x0, this.x1, this.x2, this.x3, this.y0, this.y1, this.y2, this.y3, this.u0, this.u1, this.u2, this.u3, this.v0, this.v1, this.v2, this.v3, this.tex, this.tex1, this.useAttribArray, this.tex1_u0, this.tex1_u1, this.tex1_u2, this.tex1_u3, this.tex1_u0, this.tex1_u1, this.tex1_u2, this.tex1_u3, this.tex1_col0, this.tex1_col1, this.tex1_col2, this.tex1_col3, this.bSingleCol);
    }
    
    public static TextureDraw Create(final TextureDraw textureDraw, final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        final int colorToABGR = Color.colorToABGR(n5, n6, n7, n8);
        Create(textureDraw, texture, n, n2, n + n3, n2, n + n3, n2 + n4, n, n2 + n4, colorToABGR, colorToABGR, colorToABGR, colorToABGR, consumer);
        return textureDraw;
    }
    
    public static TextureDraw Create(final TextureDraw textureDraw, final Texture texture, final SpriteRenderer.WallShaderTexRender wallShaderTexRender, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        final int colorToABGR = Color.colorToABGR(n5, n6, n7, n8);
        float n9 = 0.0f;
        float n10 = 0.0f;
        float n11 = 1.0f;
        float n12 = 0.0f;
        float n13 = 1.0f;
        float n14 = 1.0f;
        float n15 = 0.0f;
        float n16 = 1.0f;
        float n17 = 0.0f;
        float n18 = 0.0f;
        float n19 = 0.0f;
        float n20 = 0.0f;
        float n22 = 0.0f;
        float n21 = 0.0f;
        float n24 = 0.0f;
        float n23 = 0.0f;
        switch (wallShaderTexRender) {
            case LeftOnly: {
                n17 = n;
                n18 = n;
                n19 = n2;
                n20 = n2;
                n21 = (n22 = n + n3 / 2.0f);
                n23 = (n24 = n2 + n4);
                if (texture != null) {
                    final float xEnd = texture.getXEnd();
                    final float xStart = texture.getXStart();
                    final float yEnd = texture.getYEnd();
                    final float yStart = texture.getYStart();
                    final float n25 = 0.5f * (xEnd - xStart);
                    n9 = xStart;
                    n11 = xStart + n25;
                    n13 = xStart + n25;
                    n15 = xStart;
                    n10 = yStart;
                    n12 = yStart;
                    n14 = yEnd;
                    n16 = yEnd;
                    break;
                }
                break;
            }
            case RightOnly: {
                n17 = (n18 = n + n3 / 2.0f);
                n19 = n2;
                n20 = n2;
                n21 = (n22 = n + n3);
                n23 = (n24 = n2 + n4);
                if (texture != null) {
                    final float xEnd2 = texture.getXEnd();
                    final float xStart2 = texture.getXStart();
                    final float yEnd2 = texture.getYEnd();
                    final float yStart2 = texture.getYStart();
                    final float n26 = 0.5f * (xEnd2 - xStart2);
                    n9 = xStart2 + n26;
                    n11 = xEnd2;
                    n13 = xEnd2;
                    n15 = xStart2 + n26;
                    n10 = yStart2;
                    n12 = yStart2;
                    n14 = yEnd2;
                    n16 = yEnd2;
                    break;
                }
                break;
            }
            default: {
                n17 = n;
                n18 = n;
                n19 = n2;
                n20 = n2;
                n21 = (n22 = n + n3);
                n23 = (n24 = n2 + n4);
                if (texture != null) {
                    final float xEnd3 = texture.getXEnd();
                    final float xStart3 = texture.getXStart();
                    final float yEnd3 = texture.getYEnd();
                    final float yStart3 = texture.getYStart();
                    n9 = xStart3;
                    n11 = xEnd3;
                    n13 = xEnd3;
                    n15 = xStart3;
                    n10 = yStart3;
                    n12 = yStart3;
                    n14 = yEnd3;
                    n16 = yEnd3;
                    break;
                }
                break;
            }
        }
        Create(textureDraw, texture, n18, n20, n22, n19, n21, n24, n17, n23, colorToABGR, colorToABGR, colorToABGR, colorToABGR, n9, n10, n11, n12, n13, n14, n15, n16, consumer);
        return textureDraw;
    }
    
    public static TextureDraw Create(final TextureDraw textureDraw, final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final Consumer<TextureDraw> consumer) {
        final int colorToABGR = Color.colorToABGR(n5, n6, n7, n8);
        Create(textureDraw, texture, n, n2, n + n3, n2, n + n3, n2 + n4, n, n2 + n4, colorToABGR, colorToABGR, colorToABGR, colorToABGR, n9, n10, n11, n12, n13, n14, n15, n16, consumer);
        return textureDraw;
    }
    
    public static void Create(final TextureDraw textureDraw, final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20, final float n21, final float n22, final float n23, final float n24, final Consumer<TextureDraw> consumer) {
        Create(textureDraw, texture, n, n2, n3, n4, n5, n6, n7, n8, Color.colorToABGR(n9, n10, n11, n12), Color.colorToABGR(n13, n14, n15, n16), Color.colorToABGR(n17, n18, n19, n20), Color.colorToABGR(n21, n22, n23, n24), consumer);
    }
    
    public static void Create(final TextureDraw textureDraw, final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        final int colorToABGR = Color.colorToABGR(n9, n10, n11, n12);
        Create(textureDraw, texture, n, n2, n3, n4, n5, n6, n7, n8, colorToABGR, colorToABGR, colorToABGR, colorToABGR, null);
    }
    
    public static void Create(final TextureDraw textureDraw, final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final int n10, final int n11, final int n12) {
        Create(textureDraw, texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, null);
    }
    
    public static TextureDraw Create(final TextureDraw textureDraw, final Texture texture, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final int n10, final int n11, final int n12, final Consumer<TextureDraw> consumer) {
        float n13 = 0.0f;
        float n14 = 0.0f;
        float n15 = 1.0f;
        float n16 = 0.0f;
        float n17 = 1.0f;
        float n18 = 1.0f;
        float n19 = 0.0f;
        float n20 = 1.0f;
        if (texture != null) {
            final float xEnd = texture.getXEnd();
            final float xStart = texture.getXStart();
            final float yEnd = texture.getYEnd();
            final float yStart = texture.getYStart();
            n13 = xStart;
            n14 = yStart;
            n15 = xEnd;
            n16 = yStart;
            n17 = xEnd;
            n18 = yEnd;
            n19 = xStart;
            n20 = yEnd;
        }
        return Create(textureDraw, texture, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, consumer);
    }
    
    public static TextureDraw Create(final TextureDraw textureDraw, final Texture tex, final float x0, final float y0, final float x2, final float y2, final float x3, final float y3, final float x4, final float y4, final int col0, final int col2, final int col3, final int col4, final float u0, final float v0, final float u2, final float v2, final float u3, final float v3, final float u4, final float v4, final Consumer<TextureDraw> consumer) {
        textureDraw.bSingleCol = (col0 == col2 && col0 == col3 && col0 == col4);
        textureDraw.tex = tex;
        textureDraw.x0 = x0;
        textureDraw.y0 = y0;
        textureDraw.x1 = x2;
        textureDraw.y1 = y2;
        textureDraw.x2 = x3;
        textureDraw.y2 = y3;
        textureDraw.x3 = x4;
        textureDraw.y3 = y4;
        textureDraw.col0 = col0;
        textureDraw.col1 = col2;
        textureDraw.col2 = col3;
        textureDraw.col3 = col4;
        textureDraw.u0 = u0;
        textureDraw.u1 = u2;
        textureDraw.u2 = u3;
        textureDraw.u3 = u4;
        textureDraw.v0 = v0;
        textureDraw.v1 = v2;
        textureDraw.v2 = v3;
        textureDraw.v3 = v4;
        if (tex != null) {
            textureDraw.flipped = tex.flip;
        }
        if (consumer != null) {
            consumer.accept(textureDraw);
            textureDraw.bSingleCol = (textureDraw.col0 == textureDraw.col1 && textureDraw.col0 == textureDraw.col2 && textureDraw.col0 == textureDraw.col3);
        }
        return textureDraw;
    }
    
    public int getColor(final int n) {
        if (this.bSingleCol) {
            return this.col0;
        }
        if (n == 0) {
            return this.col0;
        }
        if (n == 1) {
            return this.col1;
        }
        if (n == 2) {
            return this.col2;
        }
        if (n == 3) {
            return this.col3;
        }
        return this.col0;
    }
    
    public void reset() {
        this.type = Type.glDraw;
        this.flipped = false;
        this.tex = null;
        this.tex1 = null;
        this.useAttribArray = -1;
        this.col0 = -1;
        this.col1 = -1;
        this.col2 = -1;
        this.col3 = -1;
        this.bSingleCol = true;
        final float n = -1.0f;
        this.y3 = n;
        this.y2 = n;
        this.y1 = n;
        this.y0 = n;
        this.x3 = n;
        this.x2 = n;
        this.x1 = n;
        this.x0 = n;
        this.drawer = null;
    }
    
    public static void glLoadIdentity(final TextureDraw textureDraw) {
        textureDraw.type = Type.glLoadIdentity;
    }
    
    public static void glGenerateMipMaps(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glGenerateMipMaps;
        textureDraw.a = a;
    }
    
    public static void glBind(final TextureDraw textureDraw, final int a) {
        textureDraw.type = Type.glBind;
        textureDraw.a = a;
    }
    
    public static void glViewport(final TextureDraw textureDraw, final int a, final int b, final int c, final int d) {
        textureDraw.type = Type.glViewport;
        textureDraw.a = a;
        textureDraw.b = b;
        textureDraw.c = c;
        textureDraw.d = d;
    }
    
    public void postRender() {
        if (this.type == Type.StartShader) {
            final Shader shader = Shader.ShaderMap.get(this.a);
            if (shader != null) {
                shader.postRender(this);
            }
        }
        if (this.drawer != null) {
            this.drawer.postRender();
            this.drawer = null;
        }
    }
    
    public enum Type
    {
        glDraw, 
        glBuffer, 
        glStencilFunc, 
        glAlphaFunc, 
        glStencilOp, 
        glEnable, 
        glDisable, 
        glColorMask, 
        glStencilMask, 
        glClear, 
        glBlendFunc, 
        glDoStartFrame, 
        glDoStartFrameText, 
        glDoEndFrame, 
        glTexParameteri, 
        StartShader, 
        glLoadIdentity, 
        glGenerateMipMaps, 
        glBind, 
        glViewport, 
        DrawModel, 
        DrawSkyBox, 
        DrawWater, 
        DrawPuddles, 
        DrawParticles, 
        ShaderUpdate, 
        BindActiveTexture, 
        glBlendEquation, 
        glDoStartFrameFx, 
        glDoEndFrameFx, 
        glIgnoreStyles, 
        glClearColor, 
        glBlendFuncSeparate, 
        glDepthMask, 
        doCoreIntParam, 
        drawTerrain;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.glDraw, Type.glBuffer, Type.glStencilFunc, Type.glAlphaFunc, Type.glStencilOp, Type.glEnable, Type.glDisable, Type.glColorMask, Type.glStencilMask, Type.glClear, Type.glBlendFunc, Type.glDoStartFrame, Type.glDoStartFrameText, Type.glDoEndFrame, Type.glTexParameteri, Type.StartShader, Type.glLoadIdentity, Type.glGenerateMipMaps, Type.glBind, Type.glViewport, Type.DrawModel, Type.DrawSkyBox, Type.DrawWater, Type.DrawPuddles, Type.DrawParticles, Type.ShaderUpdate, Type.BindActiveTexture, Type.glBlendEquation, Type.glDoStartFrameFx, Type.glDoEndFrameFx, Type.glIgnoreStyles, Type.glClearColor, Type.glBlendFuncSeparate, Type.glDepthMask, Type.doCoreIntParam, Type.drawTerrain };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public abstract static class GenericDrawer
    {
        public abstract void render();
        
        public void postRender() {
        }
    }
}
