// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.audio.BaseSoundEmitter;
import zombie.characters.BodyDamage.BodyDamage;
import java.util.HashMap;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoUtils;
import zombie.core.Core;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import java.util.ArrayList;
import zombie.iso.IsoGridSquare;

public final class FliesSound
{
    public static final FliesSound instance;
    private static final IsoGridSquare[] tempSquares;
    private final PlayerData[] playerData;
    private final ArrayList<FadeEmitter> fadeEmitters;
    private float fliesVolume;
    
    public FliesSound() {
        this.playerData = new PlayerData[4];
        this.fadeEmitters = new ArrayList<FadeEmitter>();
        this.fliesVolume = -1.0f;
        for (int i = 0; i < this.playerData.length; ++i) {
            this.playerData[i] = new PlayerData();
        }
    }
    
    public void Reset() {
        for (int i = 0; i < this.playerData.length; ++i) {
            this.playerData[i].Reset();
        }
    }
    
    public void update() {
        if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
            return;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                if (isoPlayer.getCurrentSquare() != null) {
                    this.playerData[i].update(isoPlayer);
                }
            }
        }
        for (int j = 0; j < this.fadeEmitters.size(); ++j) {
            if (this.fadeEmitters.get(j).update()) {
                this.fadeEmitters.remove(j--);
            }
        }
    }
    
    public void render() {
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                final IsoChunk chunk = isoChunkMap.getChunk(j, i);
                if (chunk != null) {
                    final ChunkData corpseData = chunk.corpseData;
                    if (corpseData != null) {
                        final int n = (int)IsoPlayer.players[0].z;
                        final ChunkLevelData chunkLevelData = corpseData.levelData[n];
                        for (int k = 0; k < chunkLevelData.emitters.length; ++k) {
                            final FadeEmitter fadeEmitter = chunkLevelData.emitters[k];
                            if (fadeEmitter != null && fadeEmitter.emitter != null) {
                                this.paintSquare(fadeEmitter.sq.x, fadeEmitter.sq.y, fadeEmitter.sq.z, 0.0f, 1.0f, 0.0f, 1.0f);
                            }
                            if (chunkLevelData.refCount[k] > 0) {
                                this.paintSquare(chunk.wx * 10 + 5, chunk.wy * 10 + 5, 0, 0.0f, 0.0f, 1.0f, 1.0f);
                            }
                        }
                        final IsoBuilding currentBuilding = IsoPlayer.players[0].getCurrentBuilding();
                        if (currentBuilding != null && chunkLevelData.buildingCorpseCount != null && chunkLevelData.buildingCorpseCount.containsKey(currentBuilding)) {
                            this.paintSquare(chunk.wx * 10 + 5, chunk.wy * 10 + 5, n, 1.0f, 0.0f, 0.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }
    
    private void paintSquare(final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final float n7) {
        final int tileScale = Core.TileScale;
        final int n8 = (int)IsoUtils.XToScreenExact((float)n, (float)(n2 + 1), (float)n3, 0);
        final int n9 = (int)IsoUtils.YToScreenExact((float)n, (float)(n2 + 1), (float)n3, 0);
        SpriteRenderer.instance.renderPoly((float)n8, (float)n9, (float)(n8 + 32 * tileScale), (float)(n9 - 16 * tileScale), (float)(n8 + 64 * tileScale), (float)n9, (float)(n8 + 32 * tileScale), (float)(n9 + 16 * tileScale), n4, n5, n6, n7);
    }
    
    public void chunkLoaded(final IsoChunk isoChunk) {
        if (isoChunk.corpseData == null) {
            isoChunk.corpseData = new ChunkData(isoChunk.wx, isoChunk.wy);
        }
        isoChunk.corpseData.wx = isoChunk.wx;
        isoChunk.corpseData.wy = isoChunk.wy;
        isoChunk.corpseData.Reset();
    }
    
    public void corpseAdded(final int i, final int j, final int k) {
        if (k < 0 || k >= 8) {
            DebugLog.General.error("invalid z-coordinate %d,%d,%d", i, j, k);
            return;
        }
        final ChunkData chunkData = this.getChunkData(i, j);
        if (chunkData == null) {
            return;
        }
        chunkData.corpseAdded(i, j, k);
        for (int l = 0; l < this.playerData.length; ++l) {
            if (chunkData.levelData[k].refCount[l] > 0) {
                this.playerData[l].forceUpdate = true;
            }
        }
    }
    
    public void corpseRemoved(final int i, final int j, final int k) {
        if (k < 0 || k >= 8) {
            DebugLog.General.error("invalid z-coordinate %d,%d,%d", i, j, k);
            return;
        }
        final ChunkData chunkData = this.getChunkData(i, j);
        if (chunkData == null) {
            return;
        }
        chunkData.corpseRemoved(i, j, k);
        for (int l = 0; l < this.playerData.length; ++l) {
            if (chunkData.levelData[k].refCount[l] > 0) {
                this.playerData[l].forceUpdate = true;
            }
        }
    }
    
    public int getCorpseCount(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.getCurrentSquare() == null) {
            return 0;
        }
        return this.getCorpseCount((int)isoGameCharacter.getX() / 10, (int)isoGameCharacter.getY() / 10, (int)isoGameCharacter.getZ(), isoGameCharacter.getBuilding());
    }
    
    private int getCorpseCount(final int n, final int n2, final int n3, final IsoBuilding key) {
        int n4 = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final ChunkData chunkData = this.getChunkData((n + j) * 10, (n2 + i) * 10);
                if (chunkData != null) {
                    final ChunkLevelData chunkLevelData = chunkData.levelData[n3];
                    if (key == null) {
                        n4 += chunkLevelData.corpseCount;
                    }
                    else if (chunkLevelData.buildingCorpseCount != null) {
                        final Integer n5 = chunkLevelData.buildingCorpseCount.get(key);
                        if (n5 != null) {
                            n4 += n5;
                        }
                    }
                }
            }
        }
        return n4;
    }
    
    private ChunkData getChunkData(final int n, final int n2) {
        final IsoChunk chunkForGridSquare = IsoWorld.instance.CurrentCell.getChunkForGridSquare(n, n2, 0);
        if (chunkForGridSquare != null) {
            return chunkForGridSquare.corpseData;
        }
        return null;
    }
    
    static {
        instance = new FliesSound();
        tempSquares = new IsoGridSquare[100];
    }
    
    private class ChunkLevelData
    {
        int corpseCount;
        HashMap<IsoBuilding, Integer> buildingCorpseCount;
        final int[] refCount;
        final FadeEmitter[] emitters;
        
        ChunkLevelData() {
            this.corpseCount = 0;
            this.buildingCorpseCount = null;
            this.refCount = new int[4];
            this.emitters = new FadeEmitter[4];
        }
        
        void corpseAdded(final int n, final int n2, final IsoBuilding key) {
            if (key == null) {
                ++this.corpseCount;
            }
            else {
                if (this.buildingCorpseCount == null) {
                    this.buildingCorpseCount = new HashMap<IsoBuilding, Integer>();
                }
                final Integer n3 = this.buildingCorpseCount.get(key);
                if (n3 == null) {
                    this.buildingCorpseCount.put(key, 1);
                }
                else {
                    this.buildingCorpseCount.put(key, n3 + 1);
                }
            }
        }
        
        void corpseRemoved(final int n, final int n2, final IsoBuilding key) {
            if (key == null) {
                --this.corpseCount;
            }
            else if (this.buildingCorpseCount != null) {
                final Integer n3 = this.buildingCorpseCount.get(key);
                if (n3 != null) {
                    if (n3 > 1) {
                        this.buildingCorpseCount.put(key, n3 - 1);
                    }
                    else {
                        this.buildingCorpseCount.remove(key);
                    }
                }
            }
        }
        
        IsoGridSquare calcSoundPos(final int n, final int n2, final int n3, final IsoBuilding isoBuilding) {
            final IsoChunk chunkForGridSquare = IsoWorld.instance.CurrentCell.getChunkForGridSquare(n * 10, n2 * 10, n3);
            if (chunkForGridSquare == null) {
                return null;
            }
            int n4 = 0;
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    final IsoGridSquare gridSquare = chunkForGridSquare.getGridSquare(j, i, n3);
                    if (gridSquare != null) {
                        if (!gridSquare.getStaticMovingObjects().isEmpty()) {
                            if (gridSquare.getBuilding() == isoBuilding) {
                                FliesSound.tempSquares[n4++] = gridSquare;
                            }
                        }
                    }
                }
            }
            if (n4 > 0) {
                return FliesSound.tempSquares[n4 / 2];
            }
            return null;
        }
        
        void update(final int n, final int n2, final int n3, final IsoPlayer isoPlayer) {
            final int[] refCount = this.refCount;
            final int playerIndex = isoPlayer.PlayerIndex;
            ++refCount[playerIndex];
            if (BodyDamage.getSicknessFromCorpsesRate(FliesSound.this.getCorpseCount(n, n2, n3, isoPlayer.getCurrentBuilding())) > ZomboidGlobals.FoodSicknessDecrease) {
                final IsoGridSquare calcSoundPos = this.calcSoundPos(n, n2, n3, isoPlayer.getCurrentBuilding());
                if (calcSoundPos == null) {
                    return;
                }
                if (this.emitters[isoPlayer.PlayerIndex] == null) {
                    this.emitters[isoPlayer.PlayerIndex] = new FadeEmitter();
                }
                final FadeEmitter e = this.emitters[isoPlayer.PlayerIndex];
                if (e.emitter == null) {
                    (e.emitter = IsoWorld.instance.getFreeEmitter((float)calcSoundPos.x, (float)calcSoundPos.y, (float)n3)).playSoundLoopedImpl("CorpseFlies");
                    e.emitter.setVolumeAll(0.0f);
                    e.volume = 0.0f;
                    FliesSound.this.fadeEmitters.add(e);
                }
                else {
                    e.sq.setHasFlies(false);
                    e.emitter.setPos((float)calcSoundPos.x, (float)calcSoundPos.y, (float)n3);
                    if (e.targetVolume != 1.0f && !FliesSound.this.fadeEmitters.contains(e)) {
                        FliesSound.this.fadeEmitters.add(e);
                    }
                }
                e.targetVolume = 1.0f;
                (e.sq = calcSoundPos).setHasFlies(true);
            }
            else {
                final FadeEmitter fadeEmitter = this.emitters[isoPlayer.PlayerIndex];
                if (fadeEmitter != null && fadeEmitter.emitter != null) {
                    if (!FliesSound.this.fadeEmitters.contains(fadeEmitter)) {
                        FliesSound.this.fadeEmitters.add(fadeEmitter);
                    }
                    fadeEmitter.targetVolume = 0.0f;
                    fadeEmitter.sq.setHasFlies(false);
                }
            }
        }
        
        void deref(final IsoPlayer isoPlayer) {
            final int playerIndex = isoPlayer.PlayerIndex;
            final int[] refCount = this.refCount;
            final int n = playerIndex;
            --refCount[n];
            if (this.refCount[playerIndex] > 0) {
                return;
            }
            if (this.emitters[playerIndex] != null && this.emitters[playerIndex].emitter != null) {
                if (!FliesSound.this.fadeEmitters.contains(this.emitters[playerIndex])) {
                    FliesSound.this.fadeEmitters.add(this.emitters[playerIndex]);
                }
                this.emitters[playerIndex].targetVolume = 0.0f;
                this.emitters[playerIndex].sq.setHasFlies(false);
            }
        }
        
        void Reset() {
            this.corpseCount = 0;
            if (this.buildingCorpseCount != null) {
                this.buildingCorpseCount.clear();
            }
            for (int i = 0; i < 4; ++i) {
                this.refCount[i] = 0;
                if (this.emitters[i] != null) {
                    this.emitters[i].Reset();
                }
            }
        }
    }
    
    public class ChunkData
    {
        private int wx;
        private int wy;
        private final ChunkLevelData[] levelData;
        
        private ChunkData(final int wx, final int wy) {
            this.levelData = new ChunkLevelData[8];
            this.wx = wx;
            this.wy = wy;
            for (int i = 0; i < this.levelData.length; ++i) {
                this.levelData[i] = new ChunkLevelData();
            }
        }
        
        private void corpseAdded(final int n, final int n2, final int n3) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            this.levelData[n3].corpseAdded(n - this.wx * 10, n2 - this.wy * 10, (gridSquare == null) ? null : gridSquare.getBuilding());
        }
        
        private void corpseRemoved(final int n, final int n2, final int n3) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            this.levelData[n3].corpseRemoved(n - this.wx * 10, n2 - this.wy * 10, (gridSquare == null) ? null : gridSquare.getBuilding());
        }
        
        private void Reset() {
            for (int i = 0; i < this.levelData.length; ++i) {
                this.levelData[i].Reset();
            }
        }
    }
    
    private class PlayerData
    {
        int wx;
        int wy;
        int z;
        IsoBuilding building;
        boolean forceUpdate;
        
        PlayerData() {
            this.wx = -1;
            this.wy = -1;
            this.z = -1;
            this.building = null;
            this.forceUpdate = false;
        }
        
        boolean isSameLocation(final IsoPlayer isoPlayer) {
            final IsoGridSquare currentSquare = isoPlayer.getCurrentSquare();
            return (currentSquare == null || currentSquare.getBuilding() == this.building) && (int)isoPlayer.getX() / 10 == this.wx && (int)isoPlayer.getY() / 10 == this.wy && (int)isoPlayer.getZ() == this.z;
        }
        
        void update(final IsoPlayer isoPlayer) {
            if (!this.forceUpdate && this.isSameLocation(isoPlayer)) {
                return;
            }
            this.forceUpdate = false;
            final int wx = this.wx;
            final int wy = this.wy;
            final int z = this.z;
            final IsoGridSquare currentSquare = isoPlayer.getCurrentSquare();
            this.wx = currentSquare.getX() / 10;
            this.wy = currentSquare.getY() / 10;
            this.z = currentSquare.getZ();
            this.building = currentSquare.getBuilding();
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    final ChunkData chunkData = FliesSound.this.getChunkData((this.wx + j) * 10, (this.wy + i) * 10);
                    if (chunkData != null) {
                        chunkData.levelData[this.z].update(this.wx + j, this.wy + i, this.z, isoPlayer);
                    }
                }
            }
            if (z == -1) {
                return;
            }
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    final ChunkData chunkData2 = FliesSound.this.getChunkData((wx + l) * 10, (wy + k) * 10);
                    if (chunkData2 != null) {
                        chunkData2.levelData[z].deref(isoPlayer);
                    }
                }
            }
        }
        
        void Reset() {
            final int wx = -1;
            this.z = wx;
            this.wy = wx;
            this.wx = wx;
            this.building = null;
            this.forceUpdate = false;
        }
    }
    
    private class FadeEmitter
    {
        private static final float FADE_IN_RATE = 0.01f;
        private static final float FADE_OUT_RATE = -0.01f;
        BaseSoundEmitter emitter;
        float volume;
        float targetVolume;
        IsoGridSquare sq;
        
        private FadeEmitter() {
            this.emitter = null;
            this.volume = 1.0f;
            this.targetVolume = 1.0f;
            this.sq = null;
        }
        
        boolean update() {
            if (this.emitter == null) {
                return true;
            }
            if (this.volume < this.targetVolume) {
                this.volume += 0.01f * (GameTime.getInstance().getMultiplier() / 1.6f);
                if (this.volume >= this.targetVolume) {
                    this.volume = this.targetVolume;
                    return true;
                }
            }
            else {
                this.volume += -0.01f * (GameTime.getInstance().getMultiplier() / 1.6f);
                if (this.volume <= 0.0f) {
                    this.volume = 0.0f;
                    this.emitter.stopAll();
                    this.emitter = null;
                    return true;
                }
            }
            this.emitter.setVolumeAll(this.volume);
            return false;
        }
        
        void Reset() {
            this.emitter = null;
            this.volume = 1.0f;
            this.targetVolume = 1.0f;
            this.sq = null;
        }
    }
}
