// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.iso.objects.IsoLightSwitch;
import java.util.ArrayList;
import zombie.iso.areas.IsoBuilding;

public class IsoLightSource
{
    public static int NextID;
    public int ID;
    public int x;
    public int y;
    public int z;
    public float r;
    public float g;
    public float b;
    public float rJNI;
    public float gJNI;
    public float bJNI;
    public int radius;
    public boolean bActive;
    public boolean bWasActive;
    public boolean bActiveJNI;
    public int life;
    public int startlife;
    public IsoBuilding localToBuilding;
    public boolean bHydroPowered;
    public ArrayList<IsoLightSwitch> switches;
    public IsoChunk chunk;
    public Object lightMap;
    
    public IsoLightSource(final int x, final int y, final int z, final float r, final float g, final float b, final int radius) {
        this.life = -1;
        this.startlife = -1;
        this.bHydroPowered = false;
        this.switches = new ArrayList<IsoLightSwitch>(0);
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.bActive = true;
    }
    
    public IsoLightSource(final int x, final int y, final int z, final float r, final float g, final float b, final int radius, final IsoBuilding localToBuilding) {
        this.life = -1;
        this.startlife = -1;
        this.bHydroPowered = false;
        this.switches = new ArrayList<IsoLightSwitch>(0);
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.bActive = true;
        this.localToBuilding = localToBuilding;
    }
    
    public IsoLightSource(final int x, final int y, final int z, final float r, final float g, final float b, final int radius, final int n) {
        this.life = -1;
        this.startlife = -1;
        this.bHydroPowered = false;
        this.switches = new ArrayList<IsoLightSwitch>(0);
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.bActive = true;
        this.life = n;
        this.startlife = n;
    }
    
    public void update() {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
        if (this.bHydroPowered && GameTime.instance.NightsSurvived >= SandboxOptions.instance.getElecShutModifier() && (gridSquare == null || !gridSquare.haveElectricity())) {
            this.bActive = false;
            return;
        }
        if (!this.bActive) {
            return;
        }
        if (this.localToBuilding != null) {
            this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f;
            this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f;
            this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f;
        }
        if (this.life > 0) {
            --this.life;
        }
        if (this.localToBuilding != null && gridSquare != null) {
            this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8f * IsoGridSquare.rmod * 0.7f;
            this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8f * IsoGridSquare.gmod * 0.7f;
            this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8f * IsoGridSquare.bmod * 0.7f;
        }
        for (int i = this.x - this.radius; i < this.x + this.radius; ++i) {
            for (int j = this.y - this.radius; j < this.y + this.radius; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(i, j, k);
                    if (gridSquare2 != null) {
                        if (this.localToBuilding == null || this.localToBuilding == gridSquare2.getBuilding()) {
                            final LosUtil.TestResults lineClear = LosUtil.lineClear(gridSquare2.getCell(), this.x, this.y, this.z, gridSquare2.getX(), gridSquare2.getY(), gridSquare2.getZ(), false);
                            if ((gridSquare2.getX() == this.x && gridSquare2.getY() == this.y && gridSquare2.getZ() == this.z) || lineClear != LosUtil.TestResults.Blocked) {
                                float n;
                                if (Math.abs(gridSquare2.getZ() - this.z) <= 1) {
                                    n = IsoUtils.DistanceTo((float)this.x, (float)this.y, 0.0f, (float)gridSquare2.getX(), (float)gridSquare2.getY(), 0.0f);
                                }
                                else {
                                    n = IsoUtils.DistanceTo((float)this.x, (float)this.y, (float)this.z, (float)gridSquare2.getX(), (float)gridSquare2.getY(), (float)gridSquare2.getZ());
                                }
                                if (n <= this.radius) {
                                    final float n2 = 1.0f - n / this.radius;
                                    float n3 = n2 * n2;
                                    if (this.life > -1) {
                                        n3 *= this.life / (float)this.startlife;
                                    }
                                    final float n4 = n3 * this.r * 2.0f;
                                    final float n5 = n3 * this.g * 2.0f;
                                    final float n6 = n3 * this.b * 2.0f;
                                    gridSquare2.setLampostTotalR(gridSquare2.getLampostTotalR() + n4);
                                    gridSquare2.setLampostTotalG(gridSquare2.getLampostTotalG() + n5);
                                    gridSquare2.setLampostTotalB(gridSquare2.getLampostTotalB() + n6);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public void setZ(final int z) {
        this.z = z;
    }
    
    public float getR() {
        return this.r;
    }
    
    public void setR(final float r) {
        this.r = r;
    }
    
    public float getG() {
        return this.g;
    }
    
    public void setG(final float g) {
        this.g = g;
    }
    
    public float getB() {
        return this.b;
    }
    
    public void setB(final float b) {
        this.b = b;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public boolean isActive() {
        return this.bActive;
    }
    
    public void setActive(final boolean bActive) {
        this.bActive = bActive;
    }
    
    public boolean wasActive() {
        return this.bWasActive;
    }
    
    public void setWasActive(final boolean bWasActive) {
        this.bWasActive = bWasActive;
    }
    
    public ArrayList<IsoLightSwitch> getSwitches() {
        return this.switches;
    }
    
    public void setSwitches(final ArrayList<IsoLightSwitch> switches) {
        this.switches = switches;
    }
    
    public void clearInfluence() {
        for (int i = this.x - this.radius; i < this.x + this.radius; ++i) {
            for (int j = this.y - this.radius; j < this.y + this.radius; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        gridSquare.setLampostTotalR(0.0f);
                        gridSquare.setLampostTotalG(0.0f);
                        gridSquare.setLampostTotalB(0.0f);
                    }
                }
            }
        }
    }
    
    public boolean isInBounds(final int n, final int n2, final int n3, final int n4) {
        return this.x >= n && this.x < n3 && this.y >= n2 && this.y < n4;
    }
    
    public boolean isInBounds() {
        final IsoChunkMap[] chunkMap = IsoWorld.instance.CurrentCell.ChunkMap;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!chunkMap[i].ignore) {
                if (this.isInBounds(chunkMap[i].getWorldXMinTiles(), chunkMap[i].getWorldYMinTiles(), chunkMap[i].getWorldXMaxTiles(), chunkMap[i].getWorldYMaxTiles())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isHydroPowered() {
        return this.bHydroPowered;
    }
    
    public IsoBuilding getLocalToBuilding() {
        return this.localToBuilding;
    }
    
    static {
        IsoLightSource.NextID = 1;
    }
}
