// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.logger.ExceptionLogger;
import zombie.core.Core;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.popman.ObjectPool;

public final class IsoPuddlesGeometry
{
    final float[] x;
    final float[] y;
    final float[] pdne;
    final float[] pdnw;
    final float[] pda;
    final float[] pnon;
    final int[] color;
    IsoGridSquare square;
    boolean bRecalc;
    private boolean interiorCalc;
    public static final ObjectPool<IsoPuddlesGeometry> pool;
    
    public IsoPuddlesGeometry() {
        this.x = new float[4];
        this.y = new float[4];
        this.pdne = new float[4];
        this.pdnw = new float[4];
        this.pda = new float[4];
        this.pnon = new float[4];
        this.color = new int[4];
        this.square = null;
        this.bRecalc = true;
        this.interiorCalc = false;
    }
    
    public IsoPuddlesGeometry init(final IsoGridSquare square) {
        this.interiorCalc = false;
        this.x[0] = IsoUtils.XToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3), (float)square.z, square.z);
        this.y[0] = IsoUtils.YToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3), (float)square.z, square.z);
        this.x[1] = IsoUtils.XToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3 + 1), 0.0f, 0);
        this.y[1] = IsoUtils.YToScreen((float)(square.x - square.z * 3), (float)(square.y - square.z * 3 + 1), 0.0f, 0);
        this.x[2] = IsoUtils.XToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3 + 1), 0.0f, 0);
        this.y[2] = IsoUtils.YToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3 + 1), 0.0f, 0);
        this.x[3] = IsoUtils.XToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3), 0.0f, 0);
        this.y[3] = IsoUtils.YToScreen((float)(square.x - square.z * 3 + 1), (float)(square.y - square.z * 3), 0.0f, 0);
        this.square = square;
        if (square.getProperties().Is(IsoFlagType.water) || !square.getProperties().Is(IsoFlagType.exterior)) {
            for (int i = 0; i < 4; ++i) {
                this.pdne[i] = 0.0f;
                this.pdnw[i] = 0.0f;
                this.pda[i] = 0.0f;
                this.pnon[i] = 0.0f;
            }
            return this;
        }
        for (int j = 0; j < 4; ++j) {
            this.pdne[j] = 0.0f;
            this.pdnw[j] = 0.0f;
            this.pda[j] = 1.0f;
            this.pnon[j] = 0.0f;
        }
        if (Core.getInstance().getPerfPuddles() > 1) {
            return this;
        }
        final IsoCell cell = square.getCell();
        final IsoGridSquare gridSquare = cell.getGridSquare(square.x - 1, square.y, square.z);
        final IsoGridSquare gridSquare2 = cell.getGridSquare(square.x - 1, square.y - 1, square.z);
        final IsoGridSquare gridSquare3 = cell.getGridSquare(square.x, square.y - 1, square.z);
        final IsoGridSquare gridSquare4 = cell.getGridSquare(square.x - 1, square.y + 1, square.z);
        final IsoGridSquare gridSquare5 = cell.getGridSquare(square.x, square.y + 1, square.z);
        final IsoGridSquare gridSquare6 = cell.getGridSquare(square.x + 1, square.y + 1, square.z);
        final IsoGridSquare gridSquare7 = cell.getGridSquare(square.x + 1, square.y, square.z);
        final IsoGridSquare gridSquare8 = cell.getGridSquare(square.x + 1, square.y - 1, square.z);
        if (gridSquare3 == null || gridSquare2 == null || gridSquare == null || gridSquare4 == null || gridSquare5 == null || gridSquare6 == null || gridSquare7 == null || gridSquare8 == null) {
            return this;
        }
        this.setFlags(0, gridSquare.getPuddlesDir() | gridSquare2.getPuddlesDir() | gridSquare3.getPuddlesDir());
        this.setFlags(1, gridSquare.getPuddlesDir() | gridSquare4.getPuddlesDir() | gridSquare5.getPuddlesDir());
        this.setFlags(2, gridSquare5.getPuddlesDir() | gridSquare6.getPuddlesDir() | gridSquare7.getPuddlesDir());
        this.setFlags(3, gridSquare7.getPuddlesDir() | gridSquare8.getPuddlesDir() | gridSquare3.getPuddlesDir());
        return this;
    }
    
    private void setFlags(final int n, final int n2) {
        this.pdne[n] = 0.0f;
        this.pdnw[n] = 0.0f;
        this.pda[n] = 0.0f;
        this.pnon[n] = 0.0f;
        if ((n2 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NE) != 0x0) {
            this.pdne[n] = 1.0f;
        }
        if ((n2 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NW) != 0x0) {
            this.pdnw[n] = 1.0f;
        }
        if ((n2 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_ALL) != 0x0) {
            this.pda[n] = 1.0f;
        }
    }
    
    public void recalcIfNeeded() {
        if (this.bRecalc) {
            this.bRecalc = false;
            try {
                this.init(this.square);
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
    }
    
    public boolean shouldRender() {
        this.recalcIfNeeded();
        for (int i = 0; i < 4; ++i) {
            if (this.pdne[i] + this.pdnw[i] + this.pda[i] + this.pnon[i] > 0.0f) {
                return true;
            }
        }
        if (this.square.getProperties().Is(IsoFlagType.water)) {
            return false;
        }
        if (IsoPuddles.leakingPuddlesInTheRoom && !this.interiorCalc && this.square != null) {
            for (int j = 0; j < 4; ++j) {
                this.pdne[j] = 0.0f;
                this.pdnw[j] = 0.0f;
                this.pda[j] = 0.0f;
                this.pnon[j] = 1.0f;
            }
            final IsoGridSquare adjacentSquare = this.square.getAdjacentSquare(IsoDirections.W);
            final IsoGridSquare adjacentSquare2 = this.square.getAdjacentSquare(IsoDirections.NW);
            final IsoGridSquare adjacentSquare3 = this.square.getAdjacentSquare(IsoDirections.N);
            final IsoGridSquare adjacentSquare4 = this.square.getAdjacentSquare(IsoDirections.SW);
            final IsoGridSquare adjacentSquare5 = this.square.getAdjacentSquare(IsoDirections.S);
            final IsoGridSquare adjacentSquare6 = this.square.getAdjacentSquare(IsoDirections.SE);
            final IsoGridSquare adjacentSquare7 = this.square.getAdjacentSquare(IsoDirections.E);
            final IsoGridSquare adjacentSquare8 = this.square.getAdjacentSquare(IsoDirections.NE);
            if (adjacentSquare == null || adjacentSquare3 == null || adjacentSquare5 == null || adjacentSquare7 == null || adjacentSquare2 == null || adjacentSquare8 == null || adjacentSquare4 == null || adjacentSquare6 == null || (!adjacentSquare.getProperties().Is(IsoFlagType.exterior) && !adjacentSquare3.getProperties().Is(IsoFlagType.exterior) && !adjacentSquare5.getProperties().Is(IsoFlagType.exterior) && !adjacentSquare7.getProperties().Is(IsoFlagType.exterior))) {
                return false;
            }
            if (!this.square.getProperties().Is(IsoFlagType.collideW) && adjacentSquare.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[0] = 0.0f;
                this.pnon[1] = 0.0f;
                for (int k = 0; k < 4; ++k) {
                    this.pda[k] = 1.0f;
                }
            }
            if (!adjacentSquare5.getProperties().Is(IsoFlagType.collideN) && adjacentSquare5.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[1] = 0.0f;
                this.pnon[2] = 0.0f;
                for (int l = 0; l < 4; ++l) {
                    this.pda[l] = 1.0f;
                }
            }
            if (!adjacentSquare7.getProperties().Is(IsoFlagType.collideW) && adjacentSquare7.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[2] = 0.0f;
                this.pnon[3] = 0.0f;
                for (int n = 0; n < 4; ++n) {
                    this.pda[n] = 1.0f;
                }
            }
            if (!this.square.getProperties().Is(IsoFlagType.collideN) && adjacentSquare3.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[3] = 0.0f;
                this.pnon[0] = 0.0f;
                for (int n2 = 0; n2 < 4; ++n2) {
                    this.pda[n2] = 1.0f;
                }
            }
            if (adjacentSquare3.getProperties().Is(IsoFlagType.collideW) || !adjacentSquare2.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[0] = 1.0f;
                for (int n3 = 0; n3 < 4; ++n3) {
                    this.pda[n3] = 1.0f;
                }
            }
            if (adjacentSquare5.getProperties().Is(IsoFlagType.collideW) || !adjacentSquare4.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[1] = 1.0f;
                for (int n4 = 0; n4 < 4; ++n4) {
                    this.pda[n4] = 1.0f;
                }
            }
            if (adjacentSquare4.getProperties().Is(IsoFlagType.collideN) || !adjacentSquare4.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[1] = 1.0f;
                for (int n5 = 0; n5 < 4; ++n5) {
                    this.pda[n5] = 1.0f;
                }
            }
            if (adjacentSquare6.getProperties().Is(IsoFlagType.collideN) || !adjacentSquare6.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[2] = 1.0f;
                for (int n6 = 0; n6 < 4; ++n6) {
                    this.pda[n6] = 1.0f;
                }
            }
            if (adjacentSquare6.getProperties().Is(IsoFlagType.collideW) || !adjacentSquare6.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[2] = 1.0f;
                for (int n7 = 0; n7 < 4; ++n7) {
                    this.pda[n7] = 1.0f;
                }
            }
            if (adjacentSquare8.getProperties().Is(IsoFlagType.collideW) || !adjacentSquare8.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[3] = 1.0f;
                for (int n8 = 0; n8 < 4; ++n8) {
                    this.pda[n8] = 1.0f;
                }
            }
            if (adjacentSquare7.getProperties().Is(IsoFlagType.collideN) || !adjacentSquare8.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[3] = 1.0f;
                for (int n9 = 0; n9 < 4; ++n9) {
                    this.pda[n9] = 1.0f;
                }
            }
            if (adjacentSquare.getProperties().Is(IsoFlagType.collideN) || !adjacentSquare2.getProperties().Is(IsoFlagType.exterior)) {
                this.pnon[0] = 1.0f;
                for (int n10 = 0; n10 < 4; ++n10) {
                    this.pda[n10] = 1.0f;
                }
            }
            this.interiorCalc = true;
            for (int n11 = 0; n11 < 4; ++n11) {
                if (this.pdne[n11] + this.pdnw[n11] + this.pda[n11] + this.pnon[n11] > 0.0f) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void updateLighting(final int n) {
        this.setLightingAtVert(0, this.square.getVertLight(0, n));
        this.setLightingAtVert(1, this.square.getVertLight(3, n));
        this.setLightingAtVert(2, this.square.getVertLight(2, n));
        this.setLightingAtVert(3, this.square.getVertLight(1, n));
    }
    
    private void setLightingAtVert(final int n, final int n2) {
        this.color[n] = n2;
    }
    
    static {
        pool = new ObjectPool<IsoPuddlesGeometry>(IsoPuddlesGeometry::new);
    }
}
