// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.io.IOException;
import zombie.iso.IsoObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.nio.ByteBuffer;
import zombie.core.logger.ExceptionLogger;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import zombie.iso.SliceY;
import zombie.ai.State;
import zombie.ai.states.ZombieIdleState;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.iso.IsoChunk;
import zombie.debug.DebugLog;
import zombie.characters.IsoZombie;
import java.util.ArrayList;

public final class ReanimatedPlayers
{
    public static ReanimatedPlayers instance;
    private final ArrayList<IsoZombie> Zombies;
    
    public ReanimatedPlayers() {
        this.Zombies = new ArrayList<IsoZombie>();
    }
    
    private static void noise(final String s) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public void addReanimatedPlayersToChunk(final IsoChunk isoChunk) {
        final int n = isoChunk.wx * 10;
        final int n2 = isoChunk.wy * 10;
        final int n3 = n + 10;
        final int n4 = n2 + 10;
        for (int i = 0; i < this.Zombies.size(); ++i) {
            final IsoZombie isoZombie = this.Zombies.get(i);
            if (isoZombie.getX() >= n && isoZombie.getX() < n3 && isoZombie.getY() >= n2 && isoZombie.getY() < n4) {
                final IsoGridSquare gridSquare = isoChunk.getGridSquare((int)isoZombie.getX() - n, (int)isoZombie.getY() - n2, (int)isoZombie.getZ());
                if (gridSquare != null) {
                    if (GameServer.bServer) {
                        if (isoZombie.OnlineID != -1) {
                            noise("ERROR? OnlineID != -1 for reanimated player zombie");
                        }
                        isoZombie.OnlineID = ServerMap.instance.getUniqueZombieId();
                        if (isoZombie.OnlineID == -1) {
                            continue;
                        }
                        ServerMap.instance.ZombieMap.put(isoZombie.OnlineID, isoZombie);
                    }
                    isoZombie.setCurrent(gridSquare);
                    assert !IsoWorld.instance.CurrentCell.getObjectList().contains(isoZombie);
                    assert !IsoWorld.instance.CurrentCell.getZombieList().contains(isoZombie);
                    IsoWorld.instance.CurrentCell.getObjectList().add(isoZombie);
                    IsoWorld.instance.CurrentCell.getZombieList().add(isoZombie);
                    this.Zombies.remove(i);
                    --i;
                    SharedDescriptors.createPlayerZombieDescriptor(isoZombie);
                    noise(invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoZombie;)Ljava/lang/String;, isoZombie));
                }
            }
        }
    }
    
    public void removeReanimatedPlayerFromWorld(final IsoZombie isoZombie) {
        if (!isoZombie.isReanimatedPlayer()) {
            return;
        }
        if (!GameServer.bServer) {
            isoZombie.setSceneCulled(true);
        }
        if (isoZombie.isOnFire()) {
            IsoFireManager.RemoveBurningCharacter(isoZombie);
            isoZombie.setOnFire(false);
        }
        if (isoZombie.AttachedAnimSprite != null) {
            final ArrayList<IsoSpriteInstance> attachedAnimSprite = isoZombie.AttachedAnimSprite;
            for (int i = 0; i < attachedAnimSprite.size(); ++i) {
                IsoSpriteInstance.add(attachedAnimSprite.get(i));
            }
            isoZombie.AttachedAnimSprite.clear();
        }
        if (!GameServer.bServer) {
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                final IsoPlayer isoPlayer = IsoPlayer.players[j];
                if (isoPlayer != null && isoPlayer.ReanimatedCorpse == isoZombie) {
                    isoPlayer.ReanimatedCorpse = null;
                    isoPlayer.ReanimatedCorpseID = -1;
                }
            }
        }
        if (GameServer.bServer && isoZombie.OnlineID != -1) {
            ServerMap.instance.ZombieMap.remove(isoZombie.OnlineID);
            isoZombie.OnlineID = -1;
        }
        SharedDescriptors.releasePlayerZombieDescriptor(isoZombie);
        assert !VirtualZombieManager.instance.isReused(isoZombie);
        if (isoZombie.isDead()) {
            return;
        }
        if (GameClient.bClient) {
            return;
        }
        if (this.Zombies.contains(isoZombie)) {
            return;
        }
        this.Zombies.add(isoZombie);
        noise(invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoZombie;)Ljava/lang/String;, isoZombie));
        isoZombie.setStateMachineLocked(false);
        isoZombie.changeState(ZombieIdleState.instance());
    }
    
    public void saveReanimatedPlayers() {
        if (GameClient.bClient) {
            return;
        }
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        try {
            final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
            sliceBuffer.clear();
            sliceBuffer.putInt(186);
            list.addAll(this.Zombies);
            for (final IsoZombie isoZombie : IsoWorld.instance.CurrentCell.getZombieList()) {
                if (isoZombie.isReanimatedPlayer() && !isoZombie.isDead() && !list.contains(isoZombie)) {
                    list.add(isoZombie);
                }
            }
            sliceBuffer.putInt(list.size());
            final Iterator<IsoZombie> iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().save(sliceBuffer);
            }
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(ZomboidFileSystem.instance.getFileInCurrentSave("reanimated.bin")));
            bufferedOutputStream.write(sliceBuffer.array(), 0, sliceBuffer.position());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()));
    }
    
    public void loadReanimatedPlayers() {
        if (GameClient.bClient) {
            return;
        }
        this.Zombies.clear();
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("reanimated.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                        sliceBuffer.clear();
                        sliceBuffer.limit(bufferedInputStream.read(sliceBuffer.array()));
                        this.loadReanimatedPlayers(sliceBuffer);
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex2) {
            return;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.Zombies.size()));
    }
    
    private void loadReanimatedPlayers(final ByteBuffer byteBuffer) throws IOException, RuntimeException {
        final int int1 = byteBuffer.getInt();
        for (int int2 = byteBuffer.getInt(), i = 0; i < int2; ++i) {
            final IsoObject factoryFromFileInput = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, byteBuffer);
            if (!(factoryFromFileInput instanceof IsoZombie)) {
                throw new RuntimeException("expected IsoZombie here");
            }
            final IsoZombie isoZombie = (IsoZombie)factoryFromFileInput;
            isoZombie.load(byteBuffer, int1);
            isoZombie.getDescriptor().setID(0);
            isoZombie.setReanimatedPlayer(true);
            IsoWorld.instance.CurrentCell.getAddList().remove(isoZombie);
            IsoWorld.instance.CurrentCell.getObjectList().remove(isoZombie);
            IsoWorld.instance.CurrentCell.getZombieList().remove(isoZombie);
            this.Zombies.add(isoZombie);
        }
    }
    
    static {
        ReanimatedPlayers.instance = new ReanimatedPlayers();
    }
}
