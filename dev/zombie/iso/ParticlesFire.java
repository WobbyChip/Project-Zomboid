// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL20;
import zombie.iso.weather.ClimateManager;
import zombie.core.opengl.RenderThread;
import zombie.interfaces.ITexture;
import zombie.core.Rand;
import org.lwjglx.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import java.nio.ByteBuffer;

public final class ParticlesFire extends Particles
{
    int MaxParticles;
    int MaxVortices;
    int particles_data_buffer;
    ByteBuffer particule_data;
    private Texture texFireSmoke;
    private Texture texFlameFire;
    public FireShader EffectFire;
    public SmokeShader EffectSmoke;
    public Shader EffectVape;
    float windX;
    float windY;
    private static ParticlesFire instance;
    private ParticlesArray<Particle> particles;
    private ArrayList<Zone> zones;
    private int intensityFire;
    private int intensitySmoke;
    private int intensitySteam;
    private FloatBuffer floatBuffer;
    
    public static synchronized ParticlesFire getInstance() {
        if (ParticlesFire.instance == null) {
            ParticlesFire.instance = new ParticlesFire();
        }
        return ParticlesFire.instance;
    }
    
    public ParticlesFire() {
        this.MaxParticles = 1000000;
        this.MaxVortices = 4;
        this.intensityFire = 0;
        this.intensitySmoke = 0;
        this.intensitySteam = 0;
        this.floatBuffer = BufferUtils.createFloatBuffer(16);
        this.particles = new ParticlesArray<Particle>();
        this.zones = new ArrayList<Zone>();
        this.particule_data = BufferUtils.createByteBuffer(this.MaxParticles * 4 * 4);
        this.texFireSmoke = Texture.getSharedTexture("media/textures/FireSmokes.png");
        this.texFlameFire = Texture.getSharedTexture("media/textures/FireFlame.png");
        this.zones.clear();
        final float n = (float)(int)(IsoCamera.frameState.OffX + IsoCamera.frameState.OffscreenWidth / 2);
        final float n2 = (float)(int)(IsoCamera.frameState.OffY + IsoCamera.frameState.OffscreenHeight / 2);
        this.zones.add(new Zone(10, n - 30.0f, n2 - 10.0f, n + 30.0f, n2 + 10.0f));
        this.zones.add(new Zone(10, n - 200.0f, n2, 50.0f));
        this.zones.add(new Zone(40, n + 200.0f, n2, 100.0f));
        this.zones.add(new Zone(60, n - 150.0f, n2 - 300.0f, n + 250.0f, n2 - 300.0f, 10.0f));
        this.zones.add(new Zone(10, n - 350.0f, n2 - 200.0f, n - 350.0f, n2 - 300.0f, 10.0f));
    }
    
    private void ParticlesProcess() {
        for (int i = 0; i < this.zones.size(); ++i) {
            final Zone zone = this.zones.get(i);
            final int n = (int)Math.ceil((zone.intensity - zone.currentParticles) * 0.1f);
            if (zone.type == ZoneType.Rectangle) {
                for (int j = 0; j < n; ++j) {
                    final Particle particle = new Particle();
                    particle.x = Rand.Next(zone.x0, zone.x1);
                    particle.y = Rand.Next(zone.y0, zone.y1);
                    particle.vx = Rand.Next(-3.0f, 3.0f);
                    particle.vy = Rand.Next(1.0f, 5.0f);
                    particle.tShift = 0.0f;
                    particle.id = Rand.Next(-1000000.0f, 1000000.0f);
                    particle.zone = zone;
                    final Zone zone2 = zone;
                    ++zone2.currentParticles;
                    this.particles.addParticle(particle);
                }
            }
            if (zone.type == ZoneType.Circle) {
                for (int k = 0; k < n; ++k) {
                    final Particle particle2 = new Particle();
                    final float next = Rand.Next(0.0f, 6.2831855f);
                    final float next2 = Rand.Next(0.0f, zone.r);
                    particle2.x = (float)(zone.x0 + next2 * Math.cos(next));
                    particle2.y = (float)(zone.y0 + next2 * Math.sin(next));
                    particle2.vx = Rand.Next(-3.0f, 3.0f);
                    particle2.vy = Rand.Next(1.0f, 5.0f);
                    particle2.tShift = 0.0f;
                    particle2.id = Rand.Next(-1000000.0f, 1000000.0f);
                    particle2.zone = zone;
                    final Zone zone3 = zone;
                    ++zone3.currentParticles;
                    this.particles.addParticle(particle2);
                }
            }
            if (zone.type == ZoneType.Line) {
                for (int l = 0; l < n; ++l) {
                    final Particle particle3 = new Particle();
                    final float next3 = Rand.Next(0.0f, 6.2831855f);
                    final float next4 = Rand.Next(0.0f, zone.r);
                    final float next5 = Rand.Next(0.0f, 1.0f);
                    particle3.x = (float)(zone.x0 * next5 + zone.x1 * (1.0f - next5) + next4 * Math.cos(next3));
                    particle3.y = (float)(zone.y0 * next5 + zone.y1 * (1.0f - next5) + next4 * Math.sin(next3));
                    particle3.vx = Rand.Next(-3.0f, 3.0f);
                    particle3.vy = Rand.Next(1.0f, 5.0f);
                    particle3.tShift = 0.0f;
                    particle3.id = Rand.Next(-1000000.0f, 1000000.0f);
                    particle3.zone = zone;
                    final Zone zone4 = zone;
                    ++zone4.currentParticles;
                    this.particles.addParticle(particle3);
                }
            }
            if (n < 0) {
                for (int n2 = 0; n2 < -n; ++n2) {
                    final Zone zone5 = zone;
                    --zone5.currentParticles;
                    this.particles.deleteParticle(Rand.Next(0, this.particles.getCount() + 1));
                }
            }
        }
    }
    
    public FloatBuffer getParametersFire() {
        this.floatBuffer.clear();
        this.floatBuffer.put(this.windX);
        this.floatBuffer.put((float)this.intensityFire);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(this.windY);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.put(0.0f);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }
    
    public int getFireShaderID() {
        return this.EffectFire.getID();
    }
    
    public int getSmokeShaderID() {
        return this.EffectSmoke.getID();
    }
    
    public int getVapeShaderID() {
        return this.EffectVape.getID();
    }
    
    public ITexture getFireFlameTexture() {
        return this.texFlameFire;
    }
    
    public ITexture getFireSmokeTexture() {
        return this.texFireSmoke;
    }
    
    @Override
    public void reloadShader() {
        RenderThread.invokeOnRenderContext(() -> {
            this.EffectFire = new FireShader("fire");
            this.EffectSmoke = new SmokeShader("smoke");
            this.EffectVape = new Shader("vape");
        });
    }
    
    @Override
    void createParticleBuffers() {
        this.particles_data_buffer = ParticlesFire.funcs.glGenBuffers();
        ParticlesFire.funcs.glBindBuffer(34962, this.particles_data_buffer);
        ParticlesFire.funcs.glBufferData(34962, this.MaxParticles * 4 * 4, 35044);
    }
    
    @Override
    void destroyParticleBuffers() {
        ParticlesFire.funcs.glDeleteBuffers(this.particles_data_buffer);
    }
    
    @Override
    void updateParticleParams() {
        final float windAngleIntensity = ClimateManager.getInstance().getWindAngleIntensity();
        final float windIntensity = ClimateManager.getInstance().getWindIntensity();
        this.windX = (float)Math.sin(windAngleIntensity * 6.0f) * windIntensity;
        this.windY = (float)Math.cos(windAngleIntensity * 6.0f) * windIntensity;
        this.ParticlesProcess();
        if (this.particles.getNeedToUpdate()) {
            this.particles.defragmentParticle();
            this.particule_data.clear();
            for (int i = 0; i < this.particles.size(); ++i) {
                final Particle particle = this.particles.get(i);
                if (particle != null) {
                    this.particule_data.putFloat(particle.x);
                    this.particule_data.putFloat(particle.y);
                    this.particule_data.putFloat(particle.id);
                    this.particule_data.putFloat(i / (float)this.particles.size());
                }
            }
            this.particule_data.flip();
        }
        ParticlesFire.funcs.glBindBuffer(34962, this.particles_data_buffer);
        ParticlesFire.funcs.glBufferData(34962, this.particule_data, 35040);
        GL20.glEnableVertexAttribArray(1);
        ParticlesFire.funcs.glBindBuffer(34962, this.particles_data_buffer);
        GL20.glVertexAttribPointer(1, 4, 5126, false, 0, 0L);
        GL33.glVertexAttribDivisor(1, 1);
    }
    
    @Override
    int getParticleCount() {
        return this.particles.getCount();
    }
    
    enum ZoneType
    {
        Rectangle, 
        Circle, 
        Line;
        
        private static /* synthetic */ ZoneType[] $values() {
            return new ZoneType[] { ZoneType.Rectangle, ZoneType.Circle, ZoneType.Line };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public class Zone
    {
        ZoneType type;
        int intensity;
        int currentParticles;
        float x0;
        float y0;
        float x1;
        float y1;
        float r;
        float fireIntensity;
        float smokeIntensity;
        float sparksIntensity;
        float vortices;
        float vorticeSpeed;
        float area;
        float temperature;
        float centerX;
        float centerY;
        float centerRp2;
        float currentVorticesCount;
        
        Zone(final int intensity, final float n, final float n2, final float r) {
            this.type = ZoneType.Circle;
            this.intensity = intensity;
            this.currentParticles = 0;
            this.x0 = n;
            this.y0 = n2;
            this.r = r;
            this.area = (float)(3.141592653589793 * r * r);
            this.vortices = this.intensity * 0.3f;
            this.vorticeSpeed = 0.5f;
            this.temperature = 2000.0f;
            this.centerX = n;
            this.centerY = n2;
            this.centerRp2 = r * r;
        }
        
        Zone(final int intensity, final float n, final float n2, final float n3, final float n4) {
            this.type = ZoneType.Rectangle;
            this.intensity = intensity;
            this.currentParticles = 0;
            if (n < n3) {
                this.x0 = n;
                this.x1 = n3;
            }
            else {
                this.x1 = n;
                this.x0 = n3;
            }
            if (n2 < n4) {
                this.y0 = n2;
                this.y1 = n4;
            }
            else {
                this.y1 = n2;
                this.y0 = n4;
            }
            this.area = (this.x1 - this.x0) * (this.y1 - this.y0);
            this.vortices = this.intensity * 0.3f;
            this.vorticeSpeed = 0.5f;
            this.temperature = 2000.0f;
            this.centerX = (this.x0 + this.x1) * 0.5f;
            this.centerY = (this.y0 + this.y1) * 0.5f;
            this.centerRp2 = (this.x1 - this.x0) * (this.x1 - this.x0);
        }
        
        Zone(final int intensity, final float n, final float n2, final float n3, final float n4, final float r) {
            this.type = ZoneType.Line;
            this.intensity = intensity;
            this.currentParticles = 0;
            if (n < n3) {
                this.x0 = n;
                this.x1 = n3;
                this.y0 = n2;
                this.y1 = n4;
            }
            else {
                this.x1 = n;
                this.x0 = n3;
                this.y1 = n2;
                this.y0 = n4;
            }
            this.r = r;
            this.area = (float)(this.r * Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0)));
            this.vortices = this.intensity * 0.3f;
            this.vorticeSpeed = 0.5f;
            this.temperature = 2000.0f;
            this.centerX = (this.x0 + this.x1) * 0.5f;
            this.centerY = (this.y0 + this.y1) * 0.5f;
            this.centerRp2 = (this.x1 - this.x0 + r) * (this.x1 - this.x0 + r) * 100.0f;
        }
    }
    
    public class Particle
    {
        float id;
        float x;
        float y;
        float tShift;
        float vx;
        float vy;
        Zone zone;
    }
    
    public class Vortice
    {
        float x;
        float y;
        float z;
        float size;
        float vx;
        float vy;
        float speed;
        int life;
        int lifeTime;
        Zone zone;
    }
}
