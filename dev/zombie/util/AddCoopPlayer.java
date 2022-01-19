// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoChunk;
import zombie.iso.IsoCell;
import zombie.core.network.ByteBufferWriter;
import zombie.SoundManager;
import zombie.ui.UIManager;
import zombie.Lua.LuaEventManager;
import zombie.iso.LosUtil;
import zombie.core.physics.WorldSimulation;
import zombie.popman.ZombiePopulationManager;
import zombie.network.GameServer;
import zombie.iso.LightingJNI;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;

public final class AddCoopPlayer
{
    private Stage stage;
    private IsoPlayer player;
    
    public AddCoopPlayer(final IsoPlayer player) {
        this.stage = Stage.Init;
        this.player = player;
    }
    
    public void update() {
        switch (this.stage) {
            case Init: {
                if (GameClient.bClient) {
                    final ByteBufferWriter startPacket = GameClient.connection.startPacket();
                    PacketTypes.PacketType.AddCoopPlayer.doPacket(startPacket);
                    startPacket.putByte((byte)1);
                    startPacket.putByte((byte)this.player.PlayerIndex);
                    startPacket.putUTF((this.player.username != null) ? this.player.username : "");
                    startPacket.putFloat(this.player.x);
                    startPacket.putFloat(this.player.y);
                    PacketTypes.PacketType.AddCoopPlayer.send(GameClient.connection);
                    this.stage = Stage.ReceiveClientConnect;
                    break;
                }
                this.stage = Stage.StartMapLoading;
                break;
            }
            case StartMapLoading: {
                final IsoCell currentCell = IsoWorld.instance.CurrentCell;
                final int playerIndex = this.player.PlayerIndex;
                final IsoChunkMap isoChunkMap = currentCell.ChunkMap[playerIndex];
                IsoChunkMap.bSettingChunk.lock();
                try {
                    isoChunkMap.Unload();
                    isoChunkMap.ignore = false;
                    final int worldX = (int)(this.player.x / 10.0f);
                    final int worldY = (int)(this.player.y / 10.0f);
                    try {
                        if (LightingJNI.init) {
                            LightingJNI.teleport(playerIndex, worldX - IsoChunkMap.ChunkGridWidth / 2, worldY - IsoChunkMap.ChunkGridWidth / 2);
                        }
                    }
                    catch (Exception ex) {}
                    if (!GameServer.bServer && !GameClient.bClient) {
                        ZombiePopulationManager.instance.playerSpawnedAt((int)this.player.x, (int)this.player.y, (int)this.player.z);
                    }
                    isoChunkMap.WorldX = worldX;
                    isoChunkMap.WorldY = worldY;
                    WorldSimulation.instance.activateChunkMap(playerIndex);
                    final int n = worldX - IsoChunkMap.ChunkGridWidth / 2;
                    final int n2 = worldY - IsoChunkMap.ChunkGridWidth / 2;
                    final int n3 = worldX + IsoChunkMap.ChunkGridWidth / 2 + 1;
                    final int n4 = worldY + IsoChunkMap.ChunkGridWidth / 2 + 1;
                    for (int i = n; i < n3; ++i) {
                        for (int j = n2; j < n4; ++j) {
                            if (IsoWorld.instance.getMetaGrid().isValidChunk(i, j)) {
                                final IsoChunk loadChunkForLater = isoChunkMap.LoadChunkForLater(i, j, i - n, j - n2);
                                if (loadChunkForLater != null && loadChunkForLater.bLoaded) {
                                    currentCell.setCacheChunk(loadChunkForLater, playerIndex);
                                }
                            }
                        }
                    }
                    isoChunkMap.SwapChunkBuffers();
                }
                finally {
                    IsoChunkMap.bSettingChunk.unlock();
                }
                this.stage = Stage.CheckMapLoading;
                break;
            }
            case CheckMapLoading: {
                final IsoCell currentCell2 = IsoWorld.instance.CurrentCell;
                final IsoChunkMap isoChunkMap2 = currentCell2.ChunkMap[this.player.PlayerIndex];
                isoChunkMap2.update();
                for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
                    for (int l = 0; l < IsoChunkMap.ChunkGridWidth; ++l) {
                        if (IsoWorld.instance.getMetaGrid().isValidChunk(isoChunkMap2.getWorldXMin() + l, isoChunkMap2.getWorldYMin() + k) && isoChunkMap2.getChunk(l, k) == null) {
                            return;
                        }
                    }
                }
                final IsoGridSquare gridSquare = currentCell2.getGridSquare((int)this.player.x, (int)this.player.y, (int)this.player.z);
                if (gridSquare != null && gridSquare.getRoom() != null) {
                    gridSquare.getRoom().def.setExplored(true);
                    gridSquare.getRoom().building.setAllExplored(true);
                }
                this.stage = (GameClient.bClient ? Stage.SendPlayerConnect : Stage.AddToWorld);
                break;
            }
            case SendPlayerConnect: {
                final ByteBufferWriter startPacket2 = GameClient.connection.startPacket();
                PacketTypes.PacketType.AddCoopPlayer.doPacket(startPacket2);
                startPacket2.putByte((byte)2);
                startPacket2.putByte((byte)this.player.PlayerIndex);
                GameClient.instance.writePlayerConnectData(startPacket2, this.player);
                PacketTypes.PacketType.AddCoopPlayer.send(GameClient.connection);
                this.stage = Stage.ReceivePlayerConnect;
            }
            case AddToWorld: {
                IsoPlayer.players[this.player.PlayerIndex] = this.player;
                LosUtil.cachecleared[this.player.PlayerIndex] = true;
                this.player.updateLightInfo();
                final IsoCell currentCell3 = IsoWorld.instance.CurrentCell;
                this.player.setCurrent(currentCell3.getGridSquare((int)this.player.x, (int)this.player.y, (int)this.player.z));
                this.player.updateUsername();
                this.player.setSceneCulled(false);
                if (currentCell3.isSafeToAdd()) {
                    currentCell3.getObjectList().add(this.player);
                }
                else {
                    currentCell3.getAddList().add(this.player);
                }
                this.player.getInventory().addItemsToProcessItems();
                LuaEventManager.triggerEvent("OnCreatePlayer", this.player.PlayerIndex, this.player);
                if (this.player.isAsleep()) {
                    UIManager.setFadeBeforeUI(this.player.PlayerIndex, true);
                    UIManager.FadeOut(this.player.PlayerIndex, 2.0);
                    UIManager.setFadeTime(this.player.PlayerIndex, 0.0);
                }
                this.stage = Stage.Finished;
                SoundManager.instance.stopMusic(IsoPlayer.DEATH_MUSIC_NAME);
                break;
            }
        }
    }
    
    public boolean isFinished() {
        return this.stage == Stage.Finished;
    }
    
    public void accessGranted(final int n) {
        if (this.player.PlayerIndex == n) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n + 1));
            this.stage = Stage.StartMapLoading;
        }
    }
    
    public void accessDenied(final int i, final String s) {
        if (this.player.PlayerIndex == i) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, i + 1, s));
            final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[this.player.PlayerIndex];
            isoChunkMap.Unload();
            isoChunkMap.ignore = true;
            this.stage = Stage.Finished;
            LuaEventManager.triggerEvent("OnCoopJoinFailed", i);
        }
    }
    
    public void receivePlayerConnect(final int n) {
        if (this.player.PlayerIndex == n) {
            this.stage = Stage.AddToWorld;
            this.update();
        }
    }
    
    public boolean isLoadingThisSquare(int n, int n2) {
        final int n3 = (int)(this.player.x / 10.0f);
        final int n4 = (int)(this.player.y / 10.0f);
        final int n5 = n3 - IsoChunkMap.ChunkGridWidth / 2;
        final int n6 = n4 - IsoChunkMap.ChunkGridWidth / 2;
        final int n7 = n5 + IsoChunkMap.ChunkGridWidth;
        final int n8 = n6 + IsoChunkMap.ChunkGridWidth;
        n /= 10;
        n2 /= 10;
        return n >= n5 && n < n7 && n2 >= n6 && n2 < n8;
    }
    
    public enum Stage
    {
        Init, 
        ReceiveClientConnect, 
        StartMapLoading, 
        CheckMapLoading, 
        SendPlayerConnect, 
        ReceivePlayerConnect, 
        AddToWorld, 
        Finished;
        
        private static /* synthetic */ Stage[] $values() {
            return new Stage[] { Stage.Init, Stage.ReceiveClientConnect, Stage.StartMapLoading, Stage.CheckMapLoading, Stage.SendPlayerConnect, Stage.ReceivePlayerConnect, Stage.AddToWorld, Stage.Finished };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
