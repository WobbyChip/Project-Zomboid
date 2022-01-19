// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.Lua.LuaEventManager;
import zombie.debug.LineDrawer;
import zombie.network.ServerGUI;
import zombie.debug.DebugOptions;
import zombie.iso.IsoChunkMap;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.characters.IsoZombie;
import zombie.iso.IsoChunk;
import zombie.popman.MPDebugInfo;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.popman.ZombiePopulationManager;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import java.util.Collection;
import zombie.iso.IsoCell;
import java.util.Stack;
import java.util.ArrayList;

public final class WorldSoundManager
{
    public static final WorldSoundManager instance;
    public final ArrayList<WorldSound> SoundList;
    private final Stack<WorldSound> freeSounds;
    private static final ResultBiggestSound resultBiggestSound;
    
    public WorldSoundManager() {
        this.SoundList = new ArrayList<WorldSound>();
        this.freeSounds = new Stack<WorldSound>();
    }
    
    public void init(final IsoCell isoCell) {
    }
    
    public void initFrame() {
    }
    
    public void KillCell() {
        this.freeSounds.addAll((Collection<?>)this.SoundList);
        this.SoundList.clear();
    }
    
    public WorldSound getNew() {
        if (this.freeSounds.isEmpty()) {
            return new WorldSound();
        }
        return this.freeSounds.pop();
    }
    
    public WorldSound addSound(final Object o, final int n, final int n2, final int n3, final int n4, final int n5) {
        return this.addSound(o, n, n2, n3, n4, n5, false, 0.0f, 1.0f);
    }
    
    public WorldSound addSound(final Object o, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b) {
        return this.addSound(o, n, n2, n3, n4, n5, b, 0.0f, 1.0f);
    }
    
    public WorldSound addSound(final Object o, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final float n6, final float n7) {
        return this.addSound(o, n, n2, n3, n4, n5, b, n6, n7, false, true, false);
    }
    
    public WorldSound addSound(final Object o, final int n, final int n2, final int n3, int n4, final int n5, final boolean b, final float n6, final float n7, final boolean sourceIsZombie, final boolean b2, final boolean b3) {
        if (n4 <= 0) {
            return null;
        }
        if (!b3) {
            if (SandboxOptions.instance.Lore.Hearing.getValue() == 1) {
                n4 *= (int)3.0f;
            }
            if (SandboxOptions.instance.Lore.Hearing.getValue() == 3) {
                n4 *= (int)0.45f;
            }
        }
        final WorldSound init;
        synchronized (this.SoundList) {
            init = this.getNew().init(o, n, n2, n3, n4, n5, b, n6, n7);
            if (o == null) {
                init.sourceIsZombie = sourceIsZombie;
            }
            if (!GameServer.bServer) {
                final int n8 = (n - n4) / 10;
                final int n9 = (n2 - n4) / 10;
                final int n10 = (int)Math.ceil((n + (float)n4) / 10.0f);
                final int n11 = (int)Math.ceil((n2 + (float)n4) / 10.0f);
                for (int i = n8; i < n10; ++i) {
                    for (int j = n9; j < n11; ++j) {
                        final IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(i, j);
                        if (chunk != null) {
                            chunk.SoundList.add(init);
                        }
                    }
                }
            }
            this.SoundList.add(init);
            ZombiePopulationManager.instance.addWorldSound(init, b2);
        }
        if (b2) {
            if (GameClient.bClient) {
                GameClient.instance.sendWorldSound(init);
            }
            else if (GameServer.bServer) {
                GameServer.sendWorldSound(init, null);
            }
        }
        if (Core.bDebug && GameClient.bClient) {
            MPDebugInfo.AddDebugSound(init);
        }
        return init;
    }
    
    public WorldSound addSoundRepeating(final Object o, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b) {
        final WorldSound addSound = this.addSound(o, n, n2, n3, n4, n5, b, 0.0f, 1.0f);
        if (addSound != null) {
            addSound.bRepeating = true;
        }
        return addSound;
    }
    
    public WorldSound getSoundZomb(final IsoZombie isoZombie) {
        if (isoZombie.soundSourceTarget == null) {
            return null;
        }
        if (isoZombie.getCurrentSquare() == null) {
            return null;
        }
        final IsoChunk chunk = isoZombie.getCurrentSquare().chunk;
        ArrayList<WorldSound> list;
        if (chunk == null || GameServer.bServer) {
            list = this.SoundList;
        }
        else {
            list = chunk.SoundList;
        }
        for (int i = 0; i < list.size(); ++i) {
            final WorldSound worldSound = list.get(i);
            if (isoZombie.soundSourceTarget == worldSound.source) {
                return worldSound;
            }
        }
        return null;
    }
    
    public ResultBiggestSound getBiggestSoundZomb(final int n, final int n2, final int n3, final boolean b, final IsoZombie isoZombie) {
        float n4 = -1000000.0f;
        WorldSound worldSound = null;
        IsoChunk chunk = null;
        if (isoZombie != null) {
            if (isoZombie.getCurrentSquare() == null) {
                return WorldSoundManager.resultBiggestSound.init(null, 0.0f);
            }
            chunk = isoZombie.getCurrentSquare().chunk;
        }
        ArrayList<WorldSound> list;
        if (chunk == null || GameServer.bServer) {
            list = this.SoundList;
        }
        else {
            list = chunk.SoundList;
        }
        for (int i = 0; i < list.size(); ++i) {
            final WorldSound worldSound2 = list.get(i);
            if (worldSound2 != null) {
                if (worldSound2.radius != 0) {
                    final float distanceToSquared = IsoUtils.DistanceToSquared((float)n, (float)n2, (float)worldSound2.x, (float)worldSound2.y);
                    if (distanceToSquared <= worldSound2.radius * worldSound2.radius) {
                        if (distanceToSquared >= worldSound2.zombieIgnoreDist * worldSound2.zombieIgnoreDist || n3 != worldSound2.z) {
                            if (!b || !worldSound2.sourceIsZombie) {
                                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(worldSound2.x, worldSound2.y, worldSound2.z);
                                final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
                                float n5 = distanceToSquared / (worldSound2.radius * worldSound2.radius);
                                if (gridSquare != null && gridSquare2 != null && gridSquare.getRoom() != gridSquare2.getRoom()) {
                                    n5 *= 1.2f;
                                    if (gridSquare2.getRoom() == null || gridSquare.getRoom() == null) {
                                        n5 *= 1.4f;
                                    }
                                }
                                float n6 = 1.0f - n5;
                                if (n6 > 0.0f) {
                                    if (n6 > 1.0f) {
                                        n6 = 1.0f;
                                    }
                                    final float n7 = worldSound2.volume * n6;
                                    if (n7 > n4) {
                                        n4 = n7;
                                        worldSound = worldSound2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return WorldSoundManager.resultBiggestSound.init(worldSound, n4);
    }
    
    public float getSoundAttract(final WorldSound worldSound, final IsoZombie isoZombie) {
        if (worldSound == null) {
            return 0.0f;
        }
        if (worldSound.radius == 0) {
            return 0.0f;
        }
        final float distanceToSquared = IsoUtils.DistanceToSquared(isoZombie.x, isoZombie.y, (float)worldSound.x, (float)worldSound.y);
        if (distanceToSquared > worldSound.radius * worldSound.radius) {
            return 0.0f;
        }
        if (distanceToSquared < worldSound.zombieIgnoreDist * worldSound.zombieIgnoreDist && isoZombie.z == worldSound.z) {
            return 0.0f;
        }
        if (worldSound.sourceIsZombie) {
            return 0.0f;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(worldSound.x, worldSound.y, worldSound.z);
        final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(isoZombie.x, isoZombie.y, isoZombie.z);
        float n = distanceToSquared / (worldSound.radius * worldSound.radius);
        if (gridSquare != null && gridSquare2 != null && gridSquare.getRoom() != gridSquare2.getRoom()) {
            n *= 1.2f;
            if (gridSquare2.getRoom() == null || gridSquare.getRoom() == null) {
                n *= 1.4f;
            }
        }
        float n2 = 1.0f - n;
        if (n2 <= 0.0f) {
            return 0.0f;
        }
        if (n2 > 1.0f) {
            n2 = 1.0f;
        }
        return worldSound.volume * n2;
    }
    
    public float getStressFromSounds(final int n, final int n2, final int n3) {
        float n4 = 0.0f;
        for (int i = 0; i < this.SoundList.size(); ++i) {
            final WorldSound worldSound = this.SoundList.get(i);
            if (worldSound.stresshumans) {
                if (worldSound.radius != 0) {
                    float n5 = 1.0f - IsoUtils.DistanceManhatten((float)n, (float)n2, (float)worldSound.x, (float)worldSound.y) / worldSound.radius;
                    if (n5 > 0.0f) {
                        if (n5 > 1.0f) {
                            n5 = 1.0f;
                        }
                        n4 += n5 * worldSound.stressMod;
                    }
                }
            }
        }
        return n4;
    }
    
    public void update() {
        if (!GameServer.bServer) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[i];
                if (!isoChunkMap.ignore) {
                    for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                        for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
                            final IsoChunk chunk = isoChunkMap.getChunk(k, j);
                            if (chunk != null) {
                                chunk.updateSounds();
                            }
                        }
                    }
                }
            }
        }
        for (int size = this.SoundList.size(), l = 0; l < size; ++l) {
            final WorldSound item = this.SoundList.get(l);
            if (item == null || item.life <= 0) {
                this.SoundList.remove(l);
                this.freeSounds.push(item);
                --l;
                --size;
            }
            else {
                final WorldSound worldSound = item;
                --worldSound.life;
            }
        }
    }
    
    public void render() {
        if (!Core.bDebug || !DebugOptions.instance.WorldSoundRender.getValue()) {
            return;
        }
        if (GameClient.bClient) {
            return;
        }
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return;
        }
        for (int i = 0; i < this.SoundList.size(); ++i) {
            final WorldSound worldSound = this.SoundList.get(i);
            for (double n = 0.0; n < 6.283185307179586; n += 0.15707963267948966) {
                this.DrawIsoLine(worldSound.x + worldSound.radius * (float)Math.cos(n), worldSound.y + worldSound.radius * (float)Math.sin(n), worldSound.x + worldSound.radius * (float)Math.cos(n + 0.15707963267948966), worldSound.y + worldSound.radius * (float)Math.sin(n + 0.15707963267948966), (float)worldSound.z, 1.0f, 1.0f, 1.0f, 1.0f, 1);
            }
        }
        if (GameServer.bServer) {
            return;
        }
        final IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(0);
        if (chunkMap == null || chunkMap.ignore) {
            return;
        }
        for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
            for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
                final IsoChunk chunk = chunkMap.getChunk(k, j);
                if (chunk != null) {
                    for (int l = 0; l < chunk.SoundList.size(); ++l) {
                        final WorldSound worldSound2 = chunk.SoundList.get(l);
                        for (double n2 = 0.0; n2 < 6.283185307179586; n2 += 0.15707963267948966) {
                            this.DrawIsoLine(worldSound2.x + worldSound2.radius * (float)Math.cos(n2), worldSound2.y + worldSound2.radius * (float)Math.sin(n2), worldSound2.x + worldSound2.radius * (float)Math.cos(n2 + 0.15707963267948966), worldSound2.y + worldSound2.radius * (float)Math.sin(n2 + 0.15707963267948966), (float)worldSound2.z, 0.0f, 1.0f, 1.0f, 1.0f, 1);
                            final float n3 = chunk.wx * 10 + 0.1f;
                            final float n4 = chunk.wy * 10 + 0.1f;
                            final float n5 = (chunk.wx + 1) * 10 - 0.1f;
                            final float n6 = (chunk.wy + 1) * 10 - 0.1f;
                            this.DrawIsoLine(n3, n4, n5, n4, (float)worldSound2.z, 0.0f, 1.0f, 1.0f, 1.0f, 1);
                            this.DrawIsoLine(n5, n4, n5, n6, (float)worldSound2.z, 0.0f, 1.0f, 1.0f, 1.0f, 1);
                            this.DrawIsoLine(n5, n6, n3, n6, (float)worldSound2.z, 0.0f, 1.0f, 1.0f, 1.0f, 1);
                            this.DrawIsoLine(n3, n6, n3, n4, (float)worldSound2.z, 0.0f, 1.0f, 1.0f, 1.0f, 1);
                        }
                    }
                }
            }
        }
    }
    
    private void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final int n10) {
        LineDrawer.drawLine(IsoUtils.XToScreenExact(n, n2, n5, 0), IsoUtils.YToScreenExact(n, n2, n5, 0), IsoUtils.XToScreenExact(n3, n4, n5, 0), IsoUtils.YToScreenExact(n3, n4, n5, 0), n6, n7, n8, n9, n10);
    }
    
    static {
        instance = new WorldSoundManager();
        resultBiggestSound = new ResultBiggestSound();
    }
    
    public static final class ResultBiggestSound
    {
        public WorldSound sound;
        public float attract;
        
        public ResultBiggestSound init(final WorldSound sound, final float attract) {
            this.sound = sound;
            this.attract = attract;
            return this;
        }
    }
    
    public class WorldSound
    {
        public Object source;
        public int life;
        public int radius;
        public boolean stresshumans;
        public int volume;
        public int x;
        public int y;
        public int z;
        public float zombieIgnoreDist;
        public boolean sourceIsZombie;
        public float stressMod;
        public boolean bRepeating;
        
        public WorldSound() {
            this.source = null;
            this.life = 1;
            this.zombieIgnoreDist = 0.0f;
            this.stressMod = 1.0f;
        }
        
        public WorldSound init(final Object o, final int n, final int n2, final int n3, final int n4, final int n5) {
            return this.init(o, n, n2, n3, n4, n5, false, 0.0f, 1.0f);
        }
        
        public WorldSound init(final Object o, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b) {
            return this.init(o, n, n2, n3, n4, n5, b, 0.0f, 1.0f);
        }
        
        public WorldSound init(final Object source, final int n, final int n2, final int n3, final int n4, final int n5, final boolean stresshumans, final float zombieIgnoreDist, final float stressMod) {
            this.source = source;
            this.life = 1;
            this.x = n;
            this.y = n2;
            this.z = n3;
            this.radius = n4;
            this.volume = n5;
            this.stresshumans = stresshumans;
            this.zombieIgnoreDist = zombieIgnoreDist;
            this.stressMod = stressMod;
            this.sourceIsZombie = (source instanceof IsoZombie);
            this.bRepeating = false;
            LuaEventManager.triggerEvent("OnWorldSound", n, n2, n3, n4, n5, source);
            return this;
        }
        
        public WorldSound init(final boolean sourceIsZombie, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final float n6, final float n7) {
            final WorldSound init = this.init(null, n, n2, n3, n4, n5, b, n6, n7);
            init.sourceIsZombie = sourceIsZombie;
            return init;
        }
    }
}
