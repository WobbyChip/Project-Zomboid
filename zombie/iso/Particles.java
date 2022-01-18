// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL20;
import zombie.core.SpriteRenderer;
import zombie.GameTime;
import zombie.core.opengl.RenderThread;
import org.lwjglx.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import zombie.core.VBO.GLBufferObjectARB;
import zombie.core.VBO.GLBufferObject15;
import org.lwjgl.opengl.GL;
import zombie.debug.DebugLog;
import java.nio.FloatBuffer;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.VBO.IGLBufferObject;
import java.util.ArrayList;

public abstract class Particles
{
    private float ParticlesTime;
    public static int ParticleSystemsCount;
    public static int ParticleSystemsLast;
    public static final ArrayList<Particles> ParticleSystems;
    private int id;
    int particle_vertex_buffer;
    public static IGLBufferObject funcs;
    private Matrix4f projectionMatrix;
    private Matrix4f mvpMatrix;
    private FloatBuffer floatBuffer;
    
    public static synchronized int addParticle(final Particles element) {
        if (Particles.ParticleSystems.size() == Particles.ParticleSystemsCount) {
            Particles.ParticleSystems.add(element);
            ++Particles.ParticleSystemsCount;
            return Particles.ParticleSystems.size() - 1;
        }
        final int particleSystemsLast = Particles.ParticleSystemsLast;
        if (particleSystemsLast < Particles.ParticleSystems.size()) {
            if (Particles.ParticleSystems.get(particleSystemsLast) == null) {
                Particles.ParticleSystemsLast = particleSystemsLast;
                Particles.ParticleSystems.set(particleSystemsLast, element);
                ++Particles.ParticleSystemsCount;
            }
            return particleSystemsLast;
        }
        final int index = 0;
        if (index < Particles.ParticleSystemsLast) {
            if (Particles.ParticleSystems.get(index) == null) {
                Particles.ParticleSystemsLast = index;
                Particles.ParticleSystems.set(index, element);
                ++Particles.ParticleSystemsCount;
            }
            return index;
        }
        DebugLog.log("ERROR: addParticle has unknown error");
        return -1;
    }
    
    public static synchronized void deleteParticle(final int index) {
        Particles.ParticleSystems.set(index, null);
        --Particles.ParticleSystemsCount;
    }
    
    public static void init() {
        if (Particles.funcs != null) {
            return;
        }
        if (!GL.getCapabilities().OpenGL33) {
            System.out.println("OpenGL 3.3 don't supported");
        }
        if (GL.getCapabilities().OpenGL15) {
            System.out.println("OpenGL 1.5 buffer objects supported");
            Particles.funcs = new GLBufferObject15();
        }
        else {
            if (!GL.getCapabilities().GL_ARB_vertex_buffer_object) {
                throw new RuntimeException("Neither OpenGL 1.5 nor GL_ARB_vertex_buffer_object supported");
            }
            System.out.println("GL_ARB_vertex_buffer_object supported");
            Particles.funcs = new GLBufferObjectARB();
        }
    }
    
    public void initBuffers() {
        final ByteBuffer memAlloc = MemoryUtil.memAlloc(48);
        memAlloc.clear();
        memAlloc.putFloat(-1.0f);
        memAlloc.putFloat(-1.0f);
        memAlloc.putFloat(0.0f);
        memAlloc.putFloat(1.0f);
        memAlloc.putFloat(-1.0f);
        memAlloc.putFloat(0.0f);
        memAlloc.putFloat(-1.0f);
        memAlloc.putFloat(1.0f);
        memAlloc.putFloat(0.0f);
        memAlloc.putFloat(1.0f);
        memAlloc.putFloat(1.0f);
        memAlloc.putFloat(0.0f);
        memAlloc.flip();
        this.particle_vertex_buffer = Particles.funcs.glGenBuffers();
        Particles.funcs.glBindBuffer(34962, this.particle_vertex_buffer);
        Particles.funcs.glBufferData(34962, memAlloc, 35044);
        MemoryUtil.memFree((Buffer)memAlloc);
        this.createParticleBuffers();
    }
    
    public void destroy() {
        deleteParticle(this.id);
        Particles.funcs.glDeleteBuffers(this.particle_vertex_buffer);
        this.destroyParticleBuffers();
    }
    
    public abstract void reloadShader();
    
    public Particles() {
        this.floatBuffer = BufferUtils.createFloatBuffer(16);
        RenderThread.invokeOnRenderContext(() -> {
            init();
            this.initBuffers();
            this.projectionMatrix = new Matrix4f();
            return;
        });
        this.reloadShader();
        this.id = addParticle(this);
    }
    
    private static Matrix4f orthogonal(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.m00 = 2.0f / (n2 - n);
        matrix4f.m11 = 2.0f / (n4 - n3);
        matrix4f.m22 = -2.0f / (n6 - n5);
        matrix4f.m32 = (-n6 - n5) / (n6 - n5);
        matrix4f.m30 = (-n2 - n) / (n2 - n);
        matrix4f.m31 = (-n4 - n3) / (n4 - n3);
        return matrix4f;
    }
    
    public void render() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        this.ParticlesTime += 0.0166f * GameTime.getInstance().getMultiplier();
        this.updateMVPMatrix();
        SpriteRenderer.instance.drawParticles(playerIndex, 0, 0);
    }
    
    private void updateMVPMatrix() {
        this.projectionMatrix = orthogonal(IsoCamera.frameState.OffX, IsoCamera.frameState.OffX + IsoCamera.frameState.OffscreenWidth, IsoCamera.frameState.OffY + IsoCamera.frameState.OffscreenHeight, IsoCamera.frameState.OffY, -1.0f, 1.0f);
        this.mvpMatrix = this.projectionMatrix;
    }
    
    public FloatBuffer getMVPMatrix() {
        this.floatBuffer.clear();
        this.floatBuffer.put(this.mvpMatrix.m00);
        this.floatBuffer.put(this.mvpMatrix.m10);
        this.floatBuffer.put(this.mvpMatrix.m20);
        this.floatBuffer.put(this.mvpMatrix.m30);
        this.floatBuffer.put(this.mvpMatrix.m01);
        this.floatBuffer.put(this.mvpMatrix.m11);
        this.floatBuffer.put(this.mvpMatrix.m21);
        this.floatBuffer.put(this.mvpMatrix.m31);
        this.floatBuffer.put(this.mvpMatrix.m02);
        this.floatBuffer.put(this.mvpMatrix.m12);
        this.floatBuffer.put(this.mvpMatrix.m22);
        this.floatBuffer.put(this.mvpMatrix.m32);
        this.floatBuffer.put(this.mvpMatrix.m03);
        this.floatBuffer.put(this.mvpMatrix.m13);
        this.floatBuffer.put(this.mvpMatrix.m23);
        this.floatBuffer.put(this.mvpMatrix.m33);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }
    
    public void getGeometry(final int n) {
        this.updateParticleParams();
        GL20.glEnableVertexAttribArray(0);
        Particles.funcs.glBindBuffer(34962, this.particle_vertex_buffer);
        GL20.glVertexAttribPointer(0, 3, 5126, false, 0, 0L);
        GL33.glVertexAttribDivisor(0, 0);
        GL31.glDrawArraysInstanced(5, 0, 4, this.getParticleCount());
    }
    
    public void getGeometryFire(final int n) {
        this.updateParticleParams();
        GL20.glEnableVertexAttribArray(0);
        Particles.funcs.glBindBuffer(34962, this.particle_vertex_buffer);
        GL20.glVertexAttribPointer(0, 3, 5126, false, 0, 0L);
        GL33.glVertexAttribDivisor(0, 0);
        GL31.glDrawArraysInstanced(5, 0, 4, this.getParticleCount());
    }
    
    public float getShaderTime() {
        return this.ParticlesTime;
    }
    
    abstract void createParticleBuffers();
    
    abstract void destroyParticleBuffers();
    
    abstract void updateParticleParams();
    
    abstract int getParticleCount();
    
    static {
        Particles.ParticleSystemsCount = 0;
        Particles.ParticleSystemsLast = 0;
        ParticleSystems = new ArrayList<Particles>();
        Particles.funcs = null;
    }
}
