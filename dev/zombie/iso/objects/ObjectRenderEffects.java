// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.weather.ClimateManager;
import zombie.GameTime;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.network.GameServer;
import java.util.ArrayList;
import zombie.iso.IsoObject;
import java.util.ArrayDeque;

public class ObjectRenderEffects
{
    public static final boolean ENABLED = true;
    private static ArrayDeque<ObjectRenderEffects> pool;
    public double x1;
    public double y1;
    public double x2;
    public double y2;
    public double x3;
    public double y3;
    public double x4;
    public double y4;
    private double tx1;
    private double ty1;
    private double tx2;
    private double ty2;
    private double tx3;
    private double ty3;
    private double tx4;
    private double ty4;
    private double lx1;
    private double ly1;
    private double lx2;
    private double ly2;
    private double lx3;
    private double ly3;
    private double lx4;
    private double ly4;
    private double maxX;
    private double maxY;
    private float curTime;
    private float maxTime;
    private float totalTime;
    private float totalMaxTime;
    private RenderEffectType type;
    private IsoObject parent;
    private boolean finish;
    private boolean isTree;
    private boolean isBig;
    private boolean gust;
    private int windType;
    private static float T_MOD;
    private static int windCount;
    private static int windCountTree;
    private static final int EFFECTS_COUNT = 15;
    private static final int TYPE_COUNT = 3;
    private static final ObjectRenderEffects[][] WIND_EFFECTS;
    private static final ObjectRenderEffects[][] WIND_EFFECTS_TREES;
    private static final ArrayList<ObjectRenderEffects> DYNAMIC_EFFECTS;
    private static ObjectRenderEffects RANDOM_RUSTLE;
    private static float randomRustleTime;
    private static float randomRustleTotalTime;
    private static int randomRustleTarget;
    private static int randomRustleType;
    
    public static ObjectRenderEffects alloc() {
        return ObjectRenderEffects.pool.isEmpty() ? new ObjectRenderEffects() : ObjectRenderEffects.pool.pop();
    }
    
    public static void release(final ObjectRenderEffects o) {
        assert !ObjectRenderEffects.pool.contains(o);
        ObjectRenderEffects.pool.push(o.reset());
    }
    
    private ObjectRenderEffects() {
        this.curTime = 0.0f;
        this.maxTime = 0.0f;
        this.totalTime = 0.0f;
        this.totalMaxTime = 0.0f;
        this.finish = false;
        this.isTree = false;
        this.isBig = false;
        this.gust = false;
        this.windType = 1;
    }
    
    private ObjectRenderEffects reset() {
        this.parent = null;
        this.finish = false;
        this.isBig = false;
        this.isTree = false;
        this.curTime = 0.0f;
        this.maxTime = 0.0f;
        this.totalTime = 0.0f;
        this.totalMaxTime = 0.0f;
        this.x1 = 0.0;
        this.y1 = 0.0;
        this.x2 = 0.0;
        this.y2 = 0.0;
        this.x3 = 0.0;
        this.y3 = 0.0;
        this.x4 = 0.0;
        this.y4 = 0.0;
        this.tx1 = 0.0;
        this.ty1 = 0.0;
        this.tx2 = 0.0;
        this.ty2 = 0.0;
        this.tx3 = 0.0;
        this.ty3 = 0.0;
        this.tx4 = 0.0;
        this.ty4 = 0.0;
        this.swapTargetToLast();
        return this;
    }
    
    public static ObjectRenderEffects getNew(final IsoObject isoObject, final RenderEffectType renderEffectType, final boolean b) {
        return getNew(isoObject, renderEffectType, b, false);
    }
    
    public static ObjectRenderEffects getNew(final IsoObject parent, final RenderEffectType type, final boolean b, final boolean b2) {
        if (GameServer.bServer) {
            return null;
        }
        if (type == RenderEffectType.Hit_Door && !Core.getInstance().getOptionDoDoorSpriteEffects()) {
            return null;
        }
        ObjectRenderEffects e = null;
        try {
            boolean b3 = false;
            if (b && parent != null && parent.getObjectRenderEffects() != null && parent.getObjectRenderEffects().type == type) {
                e = parent.getObjectRenderEffects();
                b3 = true;
            }
            else {
                e = alloc();
            }
            e.type = type;
            e.parent = parent;
            e.finish = false;
            e.isBig = false;
            e.totalTime = 0.0f;
            switch (type) {
                case Hit_Tree_Shudder: {
                    e.totalMaxTime = Rand.Next(45.0f, 60.0f) * ObjectRenderEffects.T_MOD;
                    break;
                }
                case Vegetation_Rustle: {
                    e.totalMaxTime = Rand.Next(45.0f, 60.0f) * ObjectRenderEffects.T_MOD;
                    if (parent != null && parent instanceof IsoTree) {
                        e.isTree = true;
                        e.isBig = (((IsoTree)parent).size > 4);
                        break;
                    }
                    break;
                }
                case Hit_Door: {
                    e.totalMaxTime = Rand.Next(15.0f, 30.0f) * ObjectRenderEffects.T_MOD;
                    break;
                }
            }
            if (!b3 && parent != null && parent.getWindRenderEffects() != null && Core.getInstance().getOptionDoWindSpriteEffects()) {
                e.copyMainFromOther(parent.getWindRenderEffects());
            }
            if (!b3 && !b2) {
                ObjectRenderEffects.DYNAMIC_EFFECTS.add(e);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }
    
    public static ObjectRenderEffects getNextWindEffect(final int n, final boolean b) {
        final int n2 = n - 1;
        if (n2 < 0 || n2 >= 3) {
            return null;
        }
        if (b) {
            if (++ObjectRenderEffects.windCountTree >= 15) {
                ObjectRenderEffects.windCountTree = 0;
            }
            return ObjectRenderEffects.WIND_EFFECTS_TREES[n2][ObjectRenderEffects.windCountTree];
        }
        if (++ObjectRenderEffects.windCount >= 15) {
            ObjectRenderEffects.windCount = 0;
        }
        return ObjectRenderEffects.WIND_EFFECTS[n2][ObjectRenderEffects.windCount];
    }
    
    public static void init() {
        if (!GameServer.bServer) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 15; ++j) {
                    final ObjectRenderEffects objectRenderEffects = new ObjectRenderEffects();
                    objectRenderEffects.windType = i + 1;
                    ObjectRenderEffects.WIND_EFFECTS[i][j] = objectRenderEffects;
                }
                for (int k = 0; k < 15; ++k) {
                    final ObjectRenderEffects objectRenderEffects2 = new ObjectRenderEffects();
                    objectRenderEffects2.isTree = true;
                    objectRenderEffects2.windType = i + 1;
                    ObjectRenderEffects.WIND_EFFECTS_TREES[i][k] = objectRenderEffects2;
                }
            }
            ObjectRenderEffects.DYNAMIC_EFFECTS.clear();
            ObjectRenderEffects.windCount = 0;
            ObjectRenderEffects.windCountTree = 0;
            ObjectRenderEffects.RANDOM_RUSTLE = null;
            ObjectRenderEffects.randomRustleTime = 0.0f;
            ObjectRenderEffects.randomRustleTotalTime = 0.0f;
            ObjectRenderEffects.randomRustleTarget = 0;
        }
    }
    
    public boolean update() {
        this.curTime += 1.0f * GameTime.getInstance().getMultiplier();
        this.totalTime += 1.0f * GameTime.getInstance().getMultiplier();
        if (this.curTime > this.maxTime) {
            if (this.finish) {
                return false;
            }
            this.curTime = 0.0f;
            this.swapTargetToLast();
            final float clamp01 = ClimateManager.clamp01(this.totalTime / this.totalMaxTime);
            final float n = 1.0f - clamp01;
            switch (this.type) {
                case Hit_Tree_Shudder: {
                    if (this.totalTime > this.totalMaxTime) {
                        this.maxTime = 10.0f * ObjectRenderEffects.T_MOD;
                        this.tx1 = 0.0;
                        this.tx2 = 0.0;
                        this.finish = true;
                        break;
                    }
                    this.maxTime = (3.0f + 15.0f * clamp01) * ObjectRenderEffects.T_MOD;
                    final double n2 = this.isBig ? Rand.Next(-0.01f + -0.08f * n, 0.01f + 0.08f * n) : ((double)Rand.Next(-0.02f + -0.16f * n, 0.02f + 0.16f * n));
                    this.tx1 = n2;
                    this.tx2 = n2;
                    break;
                }
                case Vegetation_Rustle: {
                    if (this.totalTime > this.totalMaxTime) {
                        this.maxTime = 3.0f * ObjectRenderEffects.T_MOD;
                        this.tx1 = 0.0;
                        this.tx2 = 0.0;
                        this.finish = true;
                        break;
                    }
                    this.maxTime = (2.0f + 6.0f * clamp01) * ObjectRenderEffects.T_MOD;
                    double n3 = this.isBig ? Rand.Next(-0.00625f, 0.00625f) : ((double)Rand.Next(-0.015f, 0.015f));
                    double n4 = this.isBig ? Rand.Next(-0.00625f, 0.00625f) : ((double)Rand.Next(-0.015f, 0.015f));
                    if (ClimateManager.getWindTickFinal() < 0.15) {
                        n3 *= 0.6;
                        n4 *= 0.6;
                    }
                    this.tx1 = n3;
                    this.ty1 = n4;
                    this.tx2 = n3;
                    this.ty2 = n4;
                    break;
                }
                case Hit_Door: {
                    if (this.totalTime > this.totalMaxTime) {
                        this.maxTime = 3.0f * ObjectRenderEffects.T_MOD;
                        this.tx1 = 0.0;
                        this.tx2 = 0.0;
                        this.finish = true;
                        break;
                    }
                    this.maxTime = (1.0f + 2.0f * clamp01) * ObjectRenderEffects.T_MOD;
                    final double n5 = Rand.Next(-0.005f, 0.005f);
                    final double n6 = Rand.Next(-0.0075f, 0.0075f);
                    this.tx1 = n5;
                    this.ty1 = n6;
                    this.tx2 = n5;
                    this.ty2 = n6;
                    this.tx3 = n5;
                    this.ty3 = n6;
                    this.tx4 = n5;
                    this.ty4 = n6;
                    break;
                }
                default: {
                    this.finish = true;
                    break;
                }
            }
        }
        this.lerpAll(this.curTime / this.maxTime);
        if (this.parent != null && this.parent.getWindRenderEffects() != null && Core.getInstance().getOptionDoWindSpriteEffects()) {
            this.add(this.parent.getWindRenderEffects());
        }
        return true;
    }
    
    private void update(float n, final float n2) {
        this.curTime += 1.0f * GameTime.getInstance().getMultiplier();
        if (this.curTime >= this.maxTime) {
            this.swapTargetToLast();
            if (this.isTree) {
                float n3 = 0.0f;
                float n4 = 0.04f;
                if (this.windType == 1) {
                    n3 = 0.6f;
                    n = ((n <= 0.08f) ? 0.0f : ((n - 0.08f) / 0.92f));
                }
                else if (this.windType == 2) {
                    n3 = 0.3f;
                    n4 = 0.06f;
                    n = ((n <= 0.15f) ? 0.0f : ((n - 0.15f) / 0.85f));
                }
                else if (this.windType == 3) {
                    n3 = 0.15f;
                    n = ((n <= 0.3f) ? 0.0f : ((n - 0.3f) / 0.7f));
                }
                final float clamp01 = ClimateManager.clamp01(1.0f - n);
                this.curTime = 0.0f;
                this.maxTime = Rand.Next(20.0f + 100.0f * clamp01, 70.0f + 200.0f * clamp01) * ObjectRenderEffects.T_MOD;
                if (n <= 0.01f || !Core.OptionDoWindSpriteEffects) {
                    this.tx1 = 0.0;
                    this.tx2 = 0.0;
                    this.ty1 = 0.0;
                    this.ty2 = 0.0;
                    return;
                }
                final float n5 = 0.6f * n + 0.4f * (n * n);
                double n6;
                if (this.gust) {
                    n6 = Rand.Next(-0.1f + 0.6f * n, 1.0f) * n2;
                    if (Rand.Next(0.0f, 1.0f) > Rand.Next(0.0f, 0.75f * n)) {
                        this.gust = false;
                    }
                }
                else {
                    n6 = Rand.Next(-0.1f, 0.2f) * n2;
                    this.gust = true;
                }
                final double n7 = n6 * (n3 * n5);
                this.tx1 = n7;
                this.tx2 = n7;
                this.ty1 = Rand.Next(-1.0f, 1.0f) * (0.01 + n4 * n5);
                this.ty2 = Rand.Next(-1.0f, 1.0f) * (0.01 + n4 * n5);
            }
            else {
                float n8 = 0.0f;
                if (this.windType == 1) {
                    n8 = 0.575f;
                    n = ((n <= 0.02f) ? 0.0f : ((n - 0.02f) / 0.98f));
                }
                else if (this.windType == 2) {
                    n8 = 0.375f;
                    n = ((n <= 0.2f) ? 0.0f : ((n - 0.2f) / 0.8f));
                }
                else if (this.windType == 3) {
                    n8 = 0.175f;
                    n = ((n <= 0.6f) ? 0.0f : ((n - 0.6f) / 0.4f));
                }
                final float clamp2 = ClimateManager.clamp01(1.0f - n);
                this.curTime = 0.0f;
                this.maxTime = Rand.Next(20.0f + 50.0f * clamp2, 60.0f + 100.0f * clamp2) * ObjectRenderEffects.T_MOD;
                if (n <= 0.05f || !Core.OptionDoWindSpriteEffects) {
                    this.tx1 = 0.0;
                    this.tx2 = 0.0;
                    this.ty1 = 0.0;
                    this.ty2 = 0.0;
                    return;
                }
                final float n9 = 0.55f * n + 0.45f * (n * n);
                double n10;
                if (this.gust) {
                    n10 = Rand.Next(-0.1f + 0.9f * n, 1.0f) * n2;
                    if (Rand.Next(0.0f, 1.0f) > Rand.Next(0.0f, 0.95f * n)) {
                        this.gust = false;
                    }
                }
                else {
                    n10 = Rand.Next(-0.1f, 0.2f) * n2;
                    this.gust = true;
                }
                final double n11 = n10 * (0.025f + n8 * n9);
                this.tx1 = n11;
                this.tx2 = n11;
                if (n > 0.5f) {
                    this.ty1 = Rand.Next(-1.0f, 1.0f) * (double)(0.05f * n9);
                    this.ty2 = Rand.Next(-1.0f, 1.0f) * (double)(0.05f * n9);
                }
                else {
                    this.ty1 = 0.0;
                    this.ty2 = 0.0;
                }
            }
        }
        else {
            this.lerpAll(this.curTime / this.maxTime);
        }
    }
    
    private void updateOLD(float clamp01, final float n) {
        this.curTime += 1.0f * GameTime.getInstance().getMultiplier();
        if (this.curTime >= this.maxTime) {
            this.curTime = 0.0f;
            final float clamp2 = ClimateManager.clamp01(1.0f - clamp01);
            this.maxTime = Rand.Next(20.0f + 100.0f * clamp2, 70.0f + 200.0f * clamp2) * ObjectRenderEffects.T_MOD;
            this.swapTargetToLast();
            final float n2 = clamp01;
            clamp01 = ClimateManager.clamp01(clamp01 * 1.25f);
            final double n3 = (Rand.Next(-0.65f, 0.65f) + (double)(n2 * n * 0.7f)) * (0.4f * clamp01);
            this.tx1 = n3;
            this.tx2 = n3;
            this.ty1 = Rand.Next(-1.0f, 1.0f) * (double)(0.05f * clamp01);
            this.ty2 = Rand.Next(-1.0f, 1.0f) * (double)(0.05f * clamp01);
        }
        else {
            this.lerpAll(this.curTime / this.maxTime);
        }
    }
    
    private void lerpAll(final float n) {
        this.x1 = ClimateManager.clerp(n, (float)this.lx1, (float)this.tx1);
        this.y1 = ClimateManager.clerp(n, (float)this.ly1, (float)this.ty1);
        this.x2 = ClimateManager.clerp(n, (float)this.lx2, (float)this.tx2);
        this.y2 = ClimateManager.clerp(n, (float)this.ly2, (float)this.ty2);
        this.x3 = ClimateManager.clerp(n, (float)this.lx3, (float)this.tx3);
        this.y3 = ClimateManager.clerp(n, (float)this.ly3, (float)this.ty3);
        this.x4 = ClimateManager.clerp(n, (float)this.lx4, (float)this.tx4);
        this.y4 = ClimateManager.clerp(n, (float)this.ly4, (float)this.ty4);
    }
    
    private void swapTargetToLast() {
        this.lx1 = this.tx1;
        this.ly1 = this.ty1;
        this.lx2 = this.tx2;
        this.ly2 = this.ty2;
        this.lx3 = this.tx3;
        this.ly3 = this.ty3;
        this.lx4 = this.tx4;
        this.ly4 = this.ty4;
    }
    
    public void copyMainFromOther(final ObjectRenderEffects objectRenderEffects) {
        this.x1 = objectRenderEffects.x1;
        this.y1 = objectRenderEffects.y1;
        this.x2 = objectRenderEffects.x2;
        this.y2 = objectRenderEffects.y2;
        this.x3 = objectRenderEffects.x3;
        this.y3 = objectRenderEffects.y3;
        this.x4 = objectRenderEffects.x4;
        this.y4 = objectRenderEffects.y4;
    }
    
    public void add(final ObjectRenderEffects objectRenderEffects) {
        this.x1 += objectRenderEffects.x1;
        this.y1 += objectRenderEffects.y1;
        this.x2 += objectRenderEffects.x2;
        this.y2 += objectRenderEffects.y2;
        this.x3 += objectRenderEffects.x3;
        this.y3 += objectRenderEffects.y3;
        this.x4 += objectRenderEffects.x4;
        this.y4 += objectRenderEffects.y4;
    }
    
    public static void updateStatic() {
        if (!GameServer.bServer) {
            try {
                final float n = (float)ClimateManager.getWindTickFinal();
                float n2;
                if (ClimateManager.getInstance().getWindAngleIntensity() < 0.0f) {
                    n2 = -1.0f;
                }
                else {
                    n2 = 1.0f;
                }
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 15; ++j) {
                        ObjectRenderEffects.WIND_EFFECTS[i][j].update(n, n2);
                    }
                    for (int k = 0; k < 15; ++k) {
                        ObjectRenderEffects.WIND_EFFECTS_TREES[i][k].update(n, n2);
                    }
                }
                ObjectRenderEffects.randomRustleTime += 1.0f * GameTime.getInstance().getMultiplier();
                if (ObjectRenderEffects.randomRustleTime > ObjectRenderEffects.randomRustleTotalTime && ObjectRenderEffects.RANDOM_RUSTLE == null) {
                    final float n3 = 1.0f - n;
                    ObjectRenderEffects.RANDOM_RUSTLE = getNew(null, RenderEffectType.Vegetation_Rustle, false, true);
                    ObjectRenderEffects.RANDOM_RUSTLE.isBig = false;
                    if (n > 0.45f && Rand.Next(0.0f, 1.0f) < Rand.Next(0.0f, 0.8f * n)) {
                        ObjectRenderEffects.RANDOM_RUSTLE.isBig = true;
                    }
                    ObjectRenderEffects.randomRustleType = Rand.Next(3);
                    ObjectRenderEffects.randomRustleTarget = Rand.Next(15);
                    ObjectRenderEffects.randomRustleTime = 0.0f;
                    ObjectRenderEffects.randomRustleTotalTime = Rand.Next(400.0f + 400.0f * n3, 1200.0f + 3200.0f * n3);
                }
                if (ObjectRenderEffects.RANDOM_RUSTLE != null) {
                    if (!ObjectRenderEffects.RANDOM_RUSTLE.update()) {
                        release(ObjectRenderEffects.RANDOM_RUSTLE);
                        ObjectRenderEffects.RANDOM_RUSTLE = null;
                    }
                    else {
                        ObjectRenderEffects.WIND_EFFECTS_TREES[ObjectRenderEffects.randomRustleType][ObjectRenderEffects.randomRustleTarget].add(ObjectRenderEffects.RANDOM_RUSTLE);
                    }
                }
                for (int l = ObjectRenderEffects.DYNAMIC_EFFECTS.size() - 1; l >= 0; --l) {
                    final ObjectRenderEffects objectRenderEffects = ObjectRenderEffects.DYNAMIC_EFFECTS.get(l);
                    if (!objectRenderEffects.update()) {
                        if (objectRenderEffects.parent != null) {
                            objectRenderEffects.parent.removeRenderEffect(objectRenderEffects);
                        }
                        ObjectRenderEffects.DYNAMIC_EFFECTS.remove(l);
                        release(objectRenderEffects);
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    static {
        ObjectRenderEffects.pool = new ArrayDeque<ObjectRenderEffects>();
        ObjectRenderEffects.T_MOD = 1.0f;
        ObjectRenderEffects.windCount = 0;
        ObjectRenderEffects.windCountTree = 0;
        WIND_EFFECTS = new ObjectRenderEffects[3][15];
        WIND_EFFECTS_TREES = new ObjectRenderEffects[3][15];
        DYNAMIC_EFFECTS = new ArrayList<ObjectRenderEffects>();
        ObjectRenderEffects.randomRustleTime = 0.0f;
        ObjectRenderEffects.randomRustleTotalTime = 0.0f;
        ObjectRenderEffects.randomRustleTarget = 0;
        ObjectRenderEffects.randomRustleType = 0;
    }
}
