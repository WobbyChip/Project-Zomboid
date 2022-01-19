// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.util.list.PZArrayList;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.popman.ObjectPool;
import org.joml.Vector2f;

public final class IsoWaterGeometry
{
    private static final Vector2f tempVector2f;
    boolean hasWater;
    boolean bShore;
    final float[] x;
    final float[] y;
    final float[] depth;
    final float[] flow;
    final float[] speed;
    float IsExternal;
    IsoGridSquare square;
    int m_adjacentChunkLoadedCounter;
    public static final ObjectPool<IsoWaterGeometry> pool;
    
    public IsoWaterGeometry() {
        this.hasWater = false;
        this.bShore = false;
        this.x = new float[4];
        this.y = new float[4];
        this.depth = new float[4];
        this.flow = new float[4];
        this.speed = new float[4];
        this.IsExternal = 0.0f;
        this.square = null;
    }
    
    public IsoWaterGeometry init(final IsoGridSquare square) throws Exception {
        this.x[0] = IsoUtils.XToScreen((float)square.x, (float)square.y, 0.0f, 0);
        this.y[0] = IsoUtils.YToScreen((float)square.x, (float)square.y, 0.0f, 0);
        this.x[1] = IsoUtils.XToScreen((float)square.x, (float)(square.y + 1), 0.0f, 0);
        this.y[1] = IsoUtils.YToScreen((float)square.x, (float)(square.y + 1), 0.0f, 0);
        this.x[2] = IsoUtils.XToScreen((float)(square.x + 1), (float)(square.y + 1), 0.0f, 0);
        this.y[2] = IsoUtils.YToScreen((float)(square.x + 1), (float)(square.y + 1), 0.0f, 0);
        this.x[3] = IsoUtils.XToScreen((float)(square.x + 1), (float)square.y, 0.0f, 0);
        this.y[3] = IsoUtils.YToScreen((float)(square.x + 1), (float)square.y, 0.0f, 0);
        this.hasWater = false;
        this.bShore = false;
        this.square = square;
        this.IsExternal = (square.getProperties().Is(IsoFlagType.exterior) ? 1.0f : 0.0f);
        final int shore = IsoWaterFlow.getShore(square.x, square.y);
        final IsoObject floor = square.getFloor();
        final String s = (floor == null) ? null : floor.getSprite().getName();
        if (square.getProperties().Is(IsoFlagType.water)) {
            this.hasWater = true;
            for (int i = 0; i < 4; ++i) {
                this.depth[i] = 1.0f;
            }
        }
        else if (shore == 1 && s != null && s.startsWith("blends_natural")) {
            for (int j = 0; j < 4; ++j) {
                this.depth[j] = 0.0f;
            }
            final IsoGridSquare adjacentSquare = square.getAdjacentSquare(IsoDirections.W);
            final IsoGridSquare adjacentSquare2 = square.getAdjacentSquare(IsoDirections.NW);
            final IsoGridSquare adjacentSquare3 = square.getAdjacentSquare(IsoDirections.N);
            final IsoGridSquare adjacentSquare4 = square.getAdjacentSquare(IsoDirections.SW);
            final IsoGridSquare adjacentSquare5 = square.getAdjacentSquare(IsoDirections.S);
            final IsoGridSquare adjacentSquare6 = square.getAdjacentSquare(IsoDirections.SE);
            final IsoGridSquare adjacentSquare7 = square.getAdjacentSquare(IsoDirections.E);
            final IsoGridSquare adjacentSquare8 = square.getAdjacentSquare(IsoDirections.NE);
            if (adjacentSquare3 == null || adjacentSquare2 == null || adjacentSquare == null || adjacentSquare4 == null || adjacentSquare5 == null || adjacentSquare6 == null || adjacentSquare7 == null || adjacentSquare8 == null) {
                return null;
            }
            if (adjacentSquare.getProperties().Is(IsoFlagType.water) || adjacentSquare2.getProperties().Is(IsoFlagType.water) || adjacentSquare3.getProperties().Is(IsoFlagType.water)) {
                this.bShore = true;
                this.depth[0] = 1.0f;
            }
            if (adjacentSquare.getProperties().Is(IsoFlagType.water) || adjacentSquare4.getProperties().Is(IsoFlagType.water) || adjacentSquare5.getProperties().Is(IsoFlagType.water)) {
                this.bShore = true;
                this.depth[1] = 1.0f;
            }
            if (adjacentSquare5.getProperties().Is(IsoFlagType.water) || adjacentSquare6.getProperties().Is(IsoFlagType.water) || adjacentSquare7.getProperties().Is(IsoFlagType.water)) {
                this.bShore = true;
                this.depth[2] = 1.0f;
            }
            if (adjacentSquare7.getProperties().Is(IsoFlagType.water) || adjacentSquare8.getProperties().Is(IsoFlagType.water) || adjacentSquare3.getProperties().Is(IsoFlagType.water)) {
                this.bShore = true;
                this.depth[3] = 1.0f;
            }
        }
        final Vector2f flow = IsoWaterFlow.getFlow(square, 0, 0, IsoWaterGeometry.tempVector2f);
        this.flow[0] = flow.x;
        this.speed[0] = flow.y;
        final Vector2f flow2 = IsoWaterFlow.getFlow(square, 0, 1, flow);
        this.flow[1] = flow2.x;
        this.speed[1] = flow2.y;
        final Vector2f flow3 = IsoWaterFlow.getFlow(square, 1, 1, flow2);
        this.flow[2] = flow3.x;
        this.speed[2] = flow3.y;
        final Vector2f flow4 = IsoWaterFlow.getFlow(square, 1, 0, flow3);
        this.flow[3] = flow4.x;
        this.speed[3] = flow4.y;
        this.hideWaterObjects(square);
        return this;
    }
    
    private void hideWaterObjects(final IsoGridSquare isoGridSquare) {
        final PZArrayList<IsoObject> objects = isoGridSquare.getObjects();
        for (int i = 0; i < objects.size(); ++i) {
            final IsoObject isoObject = objects.get(i);
            if (isoObject.sprite != null) {
                if (isoObject.sprite.name != null) {
                    final String name = isoObject.sprite.name;
                    if (name.startsWith("blends_natural_02")) {
                        if (name.endsWith("_0") || name.endsWith("_1") || name.endsWith("_2") || name.endsWith("_3") || name.endsWith("_4") || name.endsWith("_5") || name.endsWith("_6") || name.endsWith("_7") || name.endsWith("_8") || name.endsWith("_9") || name.endsWith("_10") || name.endsWith("_11") || name.endsWith("_12")) {
                            isoObject.sprite.setHideForWaterRender();
                        }
                    }
                }
            }
        }
    }
    
    public boolean isShore() {
        return IsoWaterFlow.getShore(this.square.x, this.square.y) == 0;
    }
    
    public float getFlow() {
        IsoWaterFlow.getShore(this.square.x, this.square.y);
        final Vector2f flow = IsoWaterFlow.getFlow(this.square, 0, 0, IsoWaterGeometry.tempVector2f);
        System.out.println(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, flow.x, flow.y));
        return flow.x;
    }
    
    static {
        tempVector2f = new Vector2f();
        pool = new ObjectPool<IsoWaterGeometry>(IsoWaterGeometry::new);
    }
}
