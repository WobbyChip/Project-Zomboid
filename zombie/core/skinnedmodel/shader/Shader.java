// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.shader;

import zombie.characters.IsoPlayer;
import zombie.vehicles.BaseVehicle;
import zombie.core.math.PZMath;
import org.joml.Math;
import zombie.core.opengl.PZGLUtil;
import zombie.iso.IsoMovingObject;
import org.joml.Vector4f;
import org.lwjgl.util.vector.Matrix4f;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import zombie.core.textures.SmartTexture;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.core.textures.Texture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjglx.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import java.nio.FloatBuffer;
import zombie.core.opengl.ShaderProgram;

public final class Shader
{
    private int HueChange;
    private int LightingAmount;
    private int MirrorXID;
    private int TransformMatrixID;
    final String name;
    private final ShaderProgram m_shaderProgram;
    private int MatrixID;
    private int Light0Direction;
    private int Light0Colour;
    private int Light1Direction;
    private int Light1Colour;
    private int Light2Direction;
    private int Light2Colour;
    private int Light3Direction;
    private int Light3Colour;
    private int Light4Direction;
    private int Light4Colour;
    private int TintColour;
    private int Texture0;
    private int TexturePainColor;
    private int TextureRust;
    private int TextureRustA;
    private int TextureMask;
    private int TextureLights;
    private int TextureDamage1Overlay;
    private int TextureDamage1Shell;
    private int TextureDamage2Overlay;
    private int TextureDamage2Shell;
    private int TextureUninstall1;
    private int TextureUninstall2;
    private int TextureLightsEnables1;
    private int TextureLightsEnables2;
    private int TextureDamage1Enables1;
    private int TextureDamage1Enables2;
    private int TextureDamage2Enables1;
    private int TextureDamage2Enables2;
    private int MatBlood1Enables1;
    private int MatBlood1Enables2;
    private int MatBlood2Enables1;
    private int MatBlood2Enables2;
    private int Alpha;
    private int TextureReflectionA;
    private int TextureReflectionB;
    private int ReflectionParam;
    public int BoneIndicesAttrib;
    public int BoneWeightsAttrib;
    private int UVScale;
    final boolean bStatic;
    private static FloatBuffer floatBuffer;
    private static final int MAX_BONES = 64;
    private static final Vector3f tempVec3f;
    private final FloatBuffer floatBuffer2;
    
    public Shader(final String name, final boolean bStatic) {
        this.TransformMatrixID = 0;
        this.MatrixID = 0;
        this.floatBuffer2 = BufferUtils.createFloatBuffer(16);
        this.name = name;
        (this.m_shaderProgram = ShaderProgram.createShaderProgram(name, bStatic, false)).addCompileListener(this::onProgramCompiled);
        this.bStatic = bStatic;
        this.compile();
    }
    
    public boolean isStatic() {
        return this.bStatic;
    }
    
    public ShaderProgram getShaderProgram() {
        return this.m_shaderProgram;
    }
    
    private void onProgramCompiled(final ShaderProgram shaderProgram) {
        this.Start();
        final int shaderID = this.m_shaderProgram.getShaderID();
        if (!this.bStatic) {
            this.MatrixID = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"MatrixPalette");
        }
        else {
            this.TransformMatrixID = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"transform");
        }
        this.HueChange = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"HueChange");
        this.LightingAmount = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"LightingAmount");
        this.Light0Colour = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light0Colour");
        this.Light0Direction = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light0Direction");
        this.Light1Colour = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light1Colour");
        this.Light1Direction = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light1Direction");
        this.Light2Colour = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light2Colour");
        this.Light2Direction = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light2Direction");
        this.Light3Colour = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light3Colour");
        this.Light3Direction = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light3Direction");
        this.Light4Colour = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light4Colour");
        this.Light4Direction = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Light4Direction");
        this.TintColour = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TintColour");
        this.Texture0 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Texture0");
        this.TexturePainColor = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TexturePainColor");
        this.TextureRust = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureRust");
        this.TextureMask = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureMask");
        this.TextureLights = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureLights");
        this.TextureDamage1Overlay = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage1Overlay");
        this.TextureDamage1Shell = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage1Shell");
        this.TextureDamage2Overlay = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage2Overlay");
        this.TextureDamage2Shell = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage2Shell");
        this.TextureRustA = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureRustA");
        this.TextureUninstall1 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureUninstall1");
        this.TextureUninstall2 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureUninstall2");
        this.TextureLightsEnables1 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureLightsEnables1");
        this.TextureLightsEnables2 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureLightsEnables2");
        this.TextureDamage1Enables1 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage1Enables1");
        this.TextureDamage1Enables2 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage1Enables2");
        this.TextureDamage2Enables1 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage2Enables1");
        this.TextureDamage2Enables2 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureDamage2Enables2");
        this.MatBlood1Enables1 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"MatBlood1Enables1");
        this.MatBlood1Enables2 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"MatBlood1Enables2");
        this.MatBlood2Enables1 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"MatBlood2Enables1");
        this.MatBlood2Enables2 = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"MatBlood2Enables2");
        this.Alpha = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"Alpha");
        this.TextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureReflectionA");
        this.TextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"TextureReflectionB");
        this.ReflectionParam = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"ReflectionParam");
        this.UVScale = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"UVScale");
        this.m_shaderProgram.setSamplerUnit("Texture", 0);
        if (this.Texture0 != -1) {
            ARBShaderObjects.glUniform1iARB(this.Texture0, 0);
        }
        if (this.TextureRust != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureRust, 1);
        }
        if (this.TextureMask != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureMask, 2);
        }
        if (this.TextureLights != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureLights, 3);
        }
        if (this.TextureDamage1Overlay != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage1Overlay, 4);
        }
        if (this.TextureDamage1Shell != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage1Shell, 5);
        }
        if (this.TextureDamage2Overlay != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage2Overlay, 6);
        }
        if (this.TextureDamage2Shell != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage2Shell, 7);
        }
        if (this.TextureReflectionA != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureReflectionA, 8);
        }
        if (this.TextureReflectionB != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureReflectionB, 9);
        }
        this.MirrorXID = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"MirrorX");
        this.BoneIndicesAttrib = GL20.glGetAttribLocation(shaderID, (CharSequence)"boneIndices");
        this.BoneWeightsAttrib = GL20.glGetAttribLocation(shaderID, (CharSequence)"boneWeights");
        this.End();
    }
    
    private void compile() {
        this.m_shaderProgram.compile();
    }
    
    public void setTexture(final Texture texture, final String s, final int n) {
        this.m_shaderProgram.setValue(s, texture, n);
    }
    
    private void setUVScale(final float n, final float n2) {
        if (this.UVScale > 0) {
            this.m_shaderProgram.setVector2(this.UVScale, n, n2);
        }
    }
    
    public int getID() {
        return this.m_shaderProgram.getShaderID();
    }
    
    public void Start() {
        this.m_shaderProgram.Start();
    }
    
    public void End() {
        this.m_shaderProgram.End();
    }
    
    public void startCharacter(final ModelSlotRenderData modelSlotRenderData, final ModelInstanceRenderData modelInstanceRenderData) {
        if (this.bStatic) {
            this.setTransformMatrix(modelInstanceRenderData.xfrm, true);
        }
        else {
            this.setMatrixPalette(modelInstanceRenderData.matrixPalette);
        }
        final float n = modelSlotRenderData.ambientR * 0.45f;
        final float n2 = modelSlotRenderData.ambientG * 0.45f;
        final float n3 = modelSlotRenderData.ambientB * 0.45f;
        this.setLights(modelSlotRenderData, 5);
        Texture engineMipmapTexture = (modelInstanceRenderData.tex != null) ? modelInstanceRenderData.tex : modelInstanceRenderData.model.tex;
        if (DebugOptions.instance.IsoSprite.CharacterMipmapColors.getValue()) {
            final Texture texture = (engineMipmapTexture instanceof SmartTexture) ? ((SmartTexture)engineMipmapTexture).result : engineMipmapTexture;
            if (texture != null && texture.getTextureId() != null && texture.getTextureId().hasMipMaps()) {
                engineMipmapTexture = Texture.getEngineMipmapTexture();
            }
        }
        this.setTexture(engineMipmapTexture, "Texture", 0);
        this.setDepthBias(modelInstanceRenderData.depthBias / 50.0f);
        this.setAmbient(n, n2, n3);
        this.setLightingAmount(1.0f);
        this.setHueShift(modelInstanceRenderData.hue);
        this.setTint(modelInstanceRenderData.tintR, modelInstanceRenderData.tintG, modelInstanceRenderData.tintB);
        this.setAlpha(modelSlotRenderData.alpha);
    }
    
    private void setLights(final ModelSlotRenderData modelSlotRenderData, final int n) {
        for (int i = 0; i < n; ++i) {
            final ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[i];
            if (GameServer.bServer && ServerGUI.isCreated()) {
                final ModelInstance.EffectLight effectLight2 = effectLight;
                final ModelInstance.EffectLight effectLight3 = effectLight;
                final ModelInstance.EffectLight effectLight4 = effectLight;
                final float r = 1.0f;
                effectLight4.b = r;
                effectLight3.g = r;
                effectLight2.r = r;
            }
            this.setLight(i, effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, (float)effectLight.radius, modelSlotRenderData.animPlayerAngle, modelSlotRenderData.x, modelSlotRenderData.y, modelSlotRenderData.z, modelSlotRenderData.object);
        }
    }
    
    public void updateAlpha(final IsoGameCharacter isoGameCharacter, final int n) {
        if (isoGameCharacter != null) {
            this.setAlpha(isoGameCharacter.getAlpha(n));
        }
    }
    
    public void setAlpha(final float n) {
        ARBShaderObjects.glUniform1fARB(this.Alpha, n);
    }
    
    public void updateParams() {
    }
    
    public void setMatrixPalette(final Matrix4f[] array) {
        if (this.bStatic) {
            return;
        }
        if (Shader.floatBuffer == null) {
            Shader.floatBuffer = BufferUtils.createFloatBuffer(1024);
        }
        Shader.floatBuffer.clear();
        for (int length = array.length, i = 0; i < length; ++i) {
            array[i].store(Shader.floatBuffer);
        }
        Shader.floatBuffer.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, true, Shader.floatBuffer);
    }
    
    public void setMatrixPalette(final FloatBuffer floatBuffer) {
        this.setMatrixPalette(floatBuffer, true);
    }
    
    public void setMatrixPalette(final FloatBuffer floatBuffer, final boolean b) {
        if (this.bStatic) {
            return;
        }
        ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, b, floatBuffer);
    }
    
    public void setMatrixPalette(final org.joml.Matrix4f[] array) {
        if (this.bStatic) {
            return;
        }
        if (Shader.floatBuffer == null) {
            Shader.floatBuffer = BufferUtils.createFloatBuffer(1024);
        }
        Shader.floatBuffer.clear();
        for (int length = array.length, i = 0; i < length; ++i) {
            array[i].get(Shader.floatBuffer);
            Shader.floatBuffer.position(Shader.floatBuffer.position() + 16);
        }
        Shader.floatBuffer.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, true, Shader.floatBuffer);
    }
    
    public void setTint(final float n, final float n2, final float n3) {
        ARBShaderObjects.glUniform3fARB(this.TintColour, n, n2, n3);
    }
    
    public void setTextureRustA(final float n) {
        ARBShaderObjects.glUniform1fARB(this.TextureRustA, n);
    }
    
    public void setTexturePainColor(final float n, final float n2, final float n3, final float n4) {
        ARBShaderObjects.glUniform4fARB(this.TexturePainColor, n, n2, n3, n4);
    }
    
    public void setTexturePainColor(final org.joml.Vector3f vector3f, final float n) {
        ARBShaderObjects.glUniform4fARB(this.TexturePainColor, vector3f.x(), vector3f.y(), vector3f.z(), n);
    }
    
    public void setTexturePainColor(final Vector4f vector4f) {
        ARBShaderObjects.glUniform4fARB(this.TexturePainColor, vector4f.x(), vector4f.y(), vector4f.z(), vector4f.w());
    }
    
    public void setReflectionParam(final float n, final float n2, final float n3) {
        ARBShaderObjects.glUniform3fARB(this.ReflectionParam, n, n2, n3);
    }
    
    public void setTextureUninstall1(final float[] array) {
        this.setMatrix(this.TextureUninstall1, array);
    }
    
    public void setTextureUninstall2(final float[] array) {
        this.setMatrix(this.TextureUninstall2, array);
    }
    
    public void setTextureLightsEnables1(final float[] array) {
        this.setMatrix(this.TextureLightsEnables1, array);
    }
    
    public void setTextureLightsEnables2(final float[] array) {
        this.setMatrix(this.TextureLightsEnables2, array);
    }
    
    public void setTextureDamage1Enables1(final float[] array) {
        this.setMatrix(this.TextureDamage1Enables1, array);
    }
    
    public void setTextureDamage1Enables2(final float[] array) {
        this.setMatrix(this.TextureDamage1Enables2, array);
    }
    
    public void setTextureDamage2Enables1(final float[] array) {
        this.setMatrix(this.TextureDamage2Enables1, array);
    }
    
    public void setTextureDamage2Enables2(final float[] array) {
        this.setMatrix(this.TextureDamage2Enables2, array);
    }
    
    public void setMatrixBlood1(final float[] array, final float[] array2) {
        if (this.MatBlood1Enables1 == -1 || this.MatBlood1Enables2 == -1) {
            return;
        }
        this.setMatrix(this.MatBlood1Enables1, array);
        this.setMatrix(this.MatBlood1Enables2, array2);
    }
    
    public void setMatrixBlood2(final float[] array, final float[] array2) {
        if (this.MatBlood2Enables1 == -1 || this.MatBlood2Enables2 == -1) {
            return;
        }
        this.setMatrix(this.MatBlood2Enables1, array);
        this.setMatrix(this.MatBlood2Enables2, array2);
    }
    
    public void setShaderAlpha(final float n) {
        ARBShaderObjects.glUniform1fARB(this.Alpha, n);
    }
    
    public void setLight(final int n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final ModelInstance modelInstance) {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;
        final IsoMovingObject object = modelInstance.object;
        if (object != null) {
            x = object.x;
            y = object.y;
            z = object.z;
        }
        this.setLight(n, n2, n3, n4, n5, n6, n7, n8, n9, x, y, z, object);
    }
    
    public void setLight(final int n, final float n2, final float n3, final float n4, float clamp, float clamp2, float clamp3, final float n5, final float n6, final float n7, final float n8, final float n9, final IsoMovingObject isoMovingObject) {
        PZGLUtil.checkGLError(true);
        int n10 = this.Light0Direction;
        int n11 = this.Light0Colour;
        if (n == 1) {
            n10 = this.Light1Direction;
            n11 = this.Light1Colour;
        }
        if (n == 2) {
            n10 = this.Light2Direction;
            n11 = this.Light2Colour;
        }
        if (n == 3) {
            n10 = this.Light3Direction;
            n11 = this.Light3Colour;
        }
        if (n == 4) {
            n10 = this.Light4Direction;
            n11 = this.Light4Colour;
        }
        if (clamp + clamp2 + clamp3 == 0.0f || n5 <= 0.0f) {
            this.doVector3(n10, 0.0f, 1.0f, 0.0f);
            this.doVector3(n11, 0.0f, 0.0f, 0.0f);
            return;
        }
        final Vector3f tempVec3f = Shader.tempVec3f;
        if (!Float.isNaN(n6)) {
            tempVec3f.set(n2, n3, n4);
            final Vector3f vector3f = tempVec3f;
            vector3f.x -= n7;
            final Vector3f vector3f2 = tempVec3f;
            vector3f2.y -= n8;
            final Vector3f vector3f3 = tempVec3f;
            vector3f3.z -= n9;
        }
        else {
            tempVec3f.set(n2, n3, n4);
        }
        final float length = tempVec3f.length();
        if (length < 1.0E-4f) {
            tempVec3f.set(0.0f, 0.0f, 1.0f);
        }
        else {
            tempVec3f.normalise();
        }
        if (!Float.isNaN(n6)) {
            final float n12 = -n6;
            final float x = tempVec3f.x;
            final float y = tempVec3f.y;
            tempVec3f.x = x * Math.cos(n12) - y * Math.sin(n12);
            tempVec3f.y = x * Math.sin(n12) + y * Math.cos(n12);
        }
        final float y2 = tempVec3f.y;
        tempVec3f.y = tempVec3f.z;
        tempVec3f.z = y2;
        if (tempVec3f.length() < 1.0E-4f) {
            tempVec3f.set(0.0f, 1.0f, 0.0f);
        }
        tempVec3f.normalise();
        float n13 = 1.0f - length / n5;
        if (n13 < 0.0f) {
            n13 = 0.0f;
        }
        if (n13 > 1.0f) {
            n13 = 1.0f;
        }
        clamp *= n13;
        clamp2 *= n13;
        clamp3 *= n13;
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        clamp2 = PZMath.clamp(clamp2, 0.0f, 1.0f);
        clamp3 = PZMath.clamp(clamp3, 0.0f, 1.0f);
        if (isoMovingObject instanceof BaseVehicle) {
            this.doVector3(n10, -tempVec3f.x, tempVec3f.y, tempVec3f.z);
        }
        else {
            this.doVector3(n10, -tempVec3f.x, tempVec3f.y, tempVec3f.z);
        }
        if (isoMovingObject instanceof IsoPlayer) {}
        this.doVector3(n11, clamp, clamp2, clamp3);
        PZGLUtil.checkGLErrorThrow("Shader.setLightInternal.", new Object[0]);
    }
    
    private void doVector3(final int n, final float n2, final float n3, final float n4) {
        this.m_shaderProgram.setVector3(n, n2, n3, n4);
    }
    
    public void setHueShift(final float n) {
        if (this.HueChange > 0) {
            this.m_shaderProgram.setValue("HueChange", n);
        }
    }
    
    public void setLightingAmount(final float n) {
        if (this.LightingAmount > 0) {
            this.m_shaderProgram.setValue("LightingAmount", n);
        }
    }
    
    public void setDepthBias(final float n) {
        this.m_shaderProgram.setValue("DepthBias", n / 300.0f);
    }
    
    public void setAmbient(final float n) {
        this.m_shaderProgram.setVector3("AmbientColour", n, n, n);
    }
    
    public void setAmbient(final float n, final float n2, final float n3) {
        this.m_shaderProgram.setVector3("AmbientColour", n, n2, n3);
    }
    
    public void setTransformMatrix(final Matrix4f matrix4f, final boolean b) {
        if (Shader.floatBuffer == null) {
            Shader.floatBuffer = BufferUtils.createFloatBuffer(1024);
        }
        Shader.floatBuffer.clear();
        matrix4f.store(Shader.floatBuffer);
        Shader.floatBuffer.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(this.TransformMatrixID, b, Shader.floatBuffer);
    }
    
    public void setTransformMatrix(final org.joml.Matrix4f matrix4f, final boolean b) {
        this.floatBuffer2.clear();
        matrix4f.get(this.floatBuffer2);
        this.floatBuffer2.position(16);
        this.floatBuffer2.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(this.TransformMatrixID, b, this.floatBuffer2);
    }
    
    public void setMatrix(final int n, final org.joml.Matrix4f matrix4f) {
        this.floatBuffer2.clear();
        matrix4f.get(this.floatBuffer2);
        this.floatBuffer2.position(16);
        this.floatBuffer2.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(n, true, this.floatBuffer2);
    }
    
    public void setMatrix(final int n, final float[] src) {
        this.floatBuffer2.clear();
        this.floatBuffer2.put(src);
        this.floatBuffer2.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(n, true, this.floatBuffer2);
    }
    
    public boolean isVehicleShader() {
        return this.TextureRust != -1;
    }
    
    static {
        tempVec3f = new Vector3f();
    }
}
