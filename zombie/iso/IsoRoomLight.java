// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.iso.areas.IsoRoom;

public final class IsoRoomLight
{
    public static int NextID;
    private static int SHINE_DIST;
    public int ID;
    public IsoRoom room;
    public int x;
    public int y;
    public int z;
    public int width;
    public int height;
    public float r;
    public float g;
    public float b;
    public boolean bActive;
    public boolean bActiveJNI;
    public boolean bHydroPowered;
    
    public IsoRoomLight(final IsoRoom room, final int x, final int y, final int z, final int width, final int height) {
        this.bHydroPowered = true;
        this.room = room;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.r = 0.9f;
        this.b = 0.8f;
        this.b = 0.7f;
        this.bActive = room.def.bLightsActive;
    }
    
    public void addInfluence() {
        this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8f * IsoGridSquare.rmod * 0.7f;
        this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8f * IsoGridSquare.gmod * 0.7f;
        this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8f * IsoGridSquare.bmod * 0.7f;
        this.r *= 2.0f;
        this.g *= 2.0f;
        this.b *= 2.0f;
        this.shineIn(this.x - 1, this.y, this.x, this.y + this.height, IsoRoomLight.SHINE_DIST, 0);
        this.shineIn(this.x, this.y - 1, this.x + this.width, this.y, 0, IsoRoomLight.SHINE_DIST);
        this.shineIn(this.x + this.width, this.y, this.x + this.width + 1, this.y + this.height, -IsoRoomLight.SHINE_DIST, 0);
        this.shineIn(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 1, 0, -IsoRoomLight.SHINE_DIST);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
        this.bActive = this.room.def.bLightsActive;
        if (this.bHydroPowered && GameTime.instance.NightsSurvived >= SandboxOptions.instance.getElecShutModifier() && (gridSquare == null || !gridSquare.haveElectricity())) {
            this.bActive = false;
            return;
        }
        if (!this.bActive) {
            return;
        }
        this.r = 0.9f;
        this.g = 0.8f;
        this.b = 0.7f;
        for (int i = this.y; i < this.y + this.height; ++i) {
            for (int j = this.x; j < this.x + this.width; ++j) {
                final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(j, i, this.z);
                if (gridSquare2 != null) {
                    gridSquare2.setLampostTotalR(gridSquare2.getLampostTotalR() + this.r);
                    gridSquare2.setLampostTotalG(gridSquare2.getLampostTotalG() + this.g);
                    gridSquare2.setLampostTotalB(gridSquare2.getLampostTotalB() + this.b);
                }
            }
        }
        this.shineOut(this.x, this.y, this.x + 1, this.y + this.height, -IsoRoomLight.SHINE_DIST, 0);
        this.shineOut(this.x, this.y, this.x + this.width, this.y + 1, 0, -IsoRoomLight.SHINE_DIST);
        this.shineOut(this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, IsoRoomLight.SHINE_DIST, 0);
        this.shineOut(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0, IsoRoomLight.SHINE_DIST);
    }
    
    private void shineOut(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        for (int i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                this.shineOut(j, i, n5, n6);
            }
        }
    }
    
    private void shineOut(final int n, final int n2, final int n3, final int n4) {
        if (n3 > 0) {
            for (int i = 1; i <= n3; ++i) {
                this.shineFromTo(n, n2, n + i, n2);
            }
        }
        else if (n3 < 0) {
            for (int j = 1; j <= -n3; ++j) {
                this.shineFromTo(n, n2, n - j, n2);
            }
        }
        else if (n4 > 0) {
            for (int k = 1; k <= n4; ++k) {
                this.shineFromTo(n, n2, n, n2 + k);
            }
        }
        else if (n4 < 0) {
            for (int l = 1; l <= -n4; ++l) {
                this.shineFromTo(n, n2, n, n2 - l);
            }
        }
    }
    
    private void shineFromTo(final int n, final int n2, final int n3, final int n4) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n4, this.z);
        if (gridSquare == null) {
            return;
        }
        if (gridSquare.getRoom() == this.room) {
            return;
        }
        if (LosUtil.lineClear(IsoWorld.instance.CurrentCell, n, n2, this.z, n3, n4, this.z, false) == LosUtil.TestResults.Blocked) {
            return;
        }
        final float n5 = 1.0f - (Math.abs(n - n3) + Math.abs(n2 - n4)) / (float)IsoRoomLight.SHINE_DIST;
        final float n6 = n5 * n5;
        final float n7 = n6 * this.r * 2.0f;
        final float n8 = n6 * this.g * 2.0f;
        final float n9 = n6 * this.b * 2.0f;
        gridSquare.setLampostTotalR(gridSquare.getLampostTotalR() + n7);
        gridSquare.setLampostTotalG(gridSquare.getLampostTotalG() + n8);
        gridSquare.setLampostTotalB(gridSquare.getLampostTotalB() + n9);
    }
    
    private void shineIn(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        for (int i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                this.shineIn(j, i, n5, n6);
            }
        }
    }
    
    private void shineIn(final int n, final int n2, final int n3, final int n4) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, this.z);
        if (gridSquare == null || !gridSquare.Is(IsoFlagType.exterior)) {
            return;
        }
        if (n3 > 0) {
            for (int i = 1; i <= n3; ++i) {
                this.shineFromToIn(n, n2, n + i, n2);
            }
        }
        else if (n3 < 0) {
            for (int j = 1; j <= -n3; ++j) {
                this.shineFromToIn(n, n2, n - j, n2);
            }
        }
        else if (n4 > 0) {
            for (int k = 1; k <= n4; ++k) {
                this.shineFromToIn(n, n2, n, n2 + k);
            }
        }
        else if (n4 < 0) {
            for (int l = 1; l <= -n4; ++l) {
                this.shineFromToIn(n, n2, n, n2 - l);
            }
        }
    }
    
    private void shineFromToIn(final int n, final int n2, final int n3, final int n4) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n4, this.z);
        if (gridSquare == null) {
            return;
        }
        if (LosUtil.lineClear(IsoWorld.instance.CurrentCell, n, n2, this.z, n3, n4, this.z, false) == LosUtil.TestResults.Blocked) {
            return;
        }
        final float n5 = 1.0f - (Math.abs(n - n3) + Math.abs(n2 - n4)) / (float)IsoRoomLight.SHINE_DIST;
        final float n6 = n5 * n5;
        final float n7 = n6 * this.r * 2.0f;
        final float n8 = n6 * this.g * 2.0f;
        final float n9 = n6 * this.b * 2.0f;
        gridSquare.setLampostTotalR(gridSquare.getLampostTotalR() + n7);
        gridSquare.setLampostTotalG(gridSquare.getLampostTotalG() + n8);
        gridSquare.setLampostTotalB(gridSquare.getLampostTotalB() + n9);
    }
    
    public void clearInfluence() {
        for (int i = this.y - IsoRoomLight.SHINE_DIST; i < this.y + this.height + IsoRoomLight.SHINE_DIST; ++i) {
            for (int j = this.x - IsoRoomLight.SHINE_DIST; j < this.x + this.width + IsoRoomLight.SHINE_DIST; ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, i, this.z);
                if (gridSquare != null) {
                    gridSquare.setLampostTotalR(0.0f);
                    gridSquare.setLampostTotalG(0.0f);
                    gridSquare.setLampostTotalB(0.0f);
                }
            }
        }
    }
    
    public boolean isInBounds() {
        final IsoChunkMap[] chunkMap = IsoWorld.instance.CurrentCell.ChunkMap;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!chunkMap[i].ignore) {
                final int worldXMinTiles = chunkMap[i].getWorldXMinTiles();
                final int worldXMaxTiles = chunkMap[i].getWorldXMaxTiles();
                final int worldYMinTiles = chunkMap[i].getWorldYMinTiles();
                final int worldYMaxTiles = chunkMap[i].getWorldYMaxTiles();
                if (this.x - IsoRoomLight.SHINE_DIST < worldXMaxTiles && this.x + this.width + IsoRoomLight.SHINE_DIST > worldXMinTiles && this.y - IsoRoomLight.SHINE_DIST < worldYMaxTiles && this.y + this.height + IsoRoomLight.SHINE_DIST > worldYMinTiles) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        IsoRoomLight.NextID = 1;
        IsoRoomLight.SHINE_DIST = 5;
    }
}
