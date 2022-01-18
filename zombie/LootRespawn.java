// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.CompressIdenticalItems;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.ItemPickerJava;
import java.util.Collection;
import zombie.inventory.ItemContainer;
import zombie.iso.BuildingDef;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoObject;
import zombie.network.ServerOptions;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;

public final class LootRespawn
{
    private static int LastRespawnHour;
    private static final ArrayList<InventoryItem> existingItems;
    private static final ArrayList<InventoryItem> newItems;
    
    public static void update() {
        if (GameClient.bClient) {
            return;
        }
        final int respawnInterval = getRespawnInterval();
        if (respawnInterval <= 0) {
            return;
        }
        final int lastRespawnHour = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / respawnInterval) * respawnInterval;
        if (LootRespawn.LastRespawnHour >= lastRespawnHour) {
            return;
        }
        LootRespawn.LastRespawnHour = lastRespawnHour;
        if (GameServer.bServer) {
            for (int i = 0; i < ServerMap.instance.LoadedCells.size(); ++i) {
                final ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(i);
                if (serverCell.bLoaded) {
                    for (int j = 0; j < 5; ++j) {
                        for (int k = 0; k < 5; ++k) {
                            checkChunk(serverCell.chunks[k][j]);
                        }
                    }
                }
            }
        }
        else {
            for (int l = 0; l < IsoPlayer.numPlayers; ++l) {
                final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[l];
                if (!isoChunkMap.ignore) {
                    for (int n = 0; n < IsoChunkMap.ChunkGridWidth; ++n) {
                        for (int n2 = 0; n2 < IsoChunkMap.ChunkGridWidth; ++n2) {
                            checkChunk(isoChunkMap.getChunk(n2, n));
                        }
                    }
                }
            }
        }
    }
    
    public static void Reset() {
        LootRespawn.LastRespawnHour = -1;
    }
    
    public static void chunkLoaded(final IsoChunk isoChunk) {
        if (GameClient.bClient) {
            return;
        }
        checkChunk(isoChunk);
    }
    
    private static void checkChunk(final IsoChunk isoChunk) {
        if (isoChunk == null) {
            return;
        }
        final int respawnInterval = getRespawnInterval();
        if (respawnInterval <= 0) {
            return;
        }
        if (GameTime.getInstance().getWorldAgeHours() < respawnInterval) {
            return;
        }
        final int n = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / respawnInterval) * respawnInterval;
        if (isoChunk.lootRespawnHour > n) {
            isoChunk.lootRespawnHour = n;
        }
        if (isoChunk.lootRespawnHour >= n) {
            return;
        }
        isoChunk.lootRespawnHour = n;
        respawnInChunk(isoChunk);
    }
    
    private static int getRespawnInterval() {
        if (GameServer.bServer) {
            return ServerOptions.instance.HoursForLootRespawn.getValue();
        }
        if (!GameClient.bClient) {
            final int value = SandboxOptions.instance.LootRespawn.getValue();
            if (value == 1) {
                return 0;
            }
            if (value == 2) {
                return 24;
            }
            if (value == 3) {
                return 168;
            }
            if (value == 4) {
                return 720;
            }
            if (value == 5) {
                return 1440;
            }
        }
        return 0;
    }
    
    private static void respawnInChunk(final IsoChunk isoChunk) {
        final boolean b = GameServer.bServer && ServerOptions.instance.ConstructionPreventsLootRespawn.getValue();
        final int value = SandboxOptions.instance.SeenHoursPreventLootRespawn.getValue();
        final double worldAgeHours = GameTime.getInstance().getWorldAgeHours();
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                final IsoGridSquare gridSquare = isoChunk.getGridSquare(j, i, 0);
                final IsoMetaGrid.Zone zone = (gridSquare == null) ? null : gridSquare.getZone();
                if (zone != null) {
                    if ("TownZone".equals(zone.getType()) || "TownZones".equals(zone.getType()) || "TrailerPark".equals(zone.getType())) {
                        if (!b || !zone.haveConstruction) {
                            if (value <= 0 || zone.getHoursSinceLastSeen() > value) {
                                if (gridSquare.getBuilding() != null) {
                                    final BuildingDef def = gridSquare.getBuilding().getDef();
                                    if (def != null) {
                                        if (def.lootRespawnHour > worldAgeHours) {
                                            def.lootRespawnHour = 0;
                                        }
                                        if (def.lootRespawnHour < isoChunk.lootRespawnHour) {
                                            def.setKeySpawned(0);
                                            def.lootRespawnHour = isoChunk.lootRespawnHour;
                                        }
                                    }
                                }
                                for (int k = 0; k < 8; ++k) {
                                    final IsoGridSquare gridSquare2 = isoChunk.getGridSquare(j, i, k);
                                    if (gridSquare2 != null) {
                                        final int size = gridSquare2.getObjects().size();
                                        for (final IsoObject isoObject : gridSquare2.getObjects().getElements()) {
                                            if (!(isoObject instanceof IsoDeadBody) && !(isoObject instanceof IsoThumpable)) {
                                                if (!(isoObject instanceof IsoCompost)) {
                                                    for (int n = 0; n < isoObject.getContainerCount(); ++n) {
                                                        final ItemContainer containerByIndex = isoObject.getContainerByIndex(n);
                                                        if (containerByIndex.bExplored) {
                                                            if (containerByIndex.isHasBeenLooted()) {
                                                                respawnInContainer(isoObject, containerByIndex);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void respawnInContainer(final IsoObject isoObject, final ItemContainer itemContainer) {
        if (itemContainer == null || itemContainer.getItems() == null) {
            return;
        }
        final int size = itemContainer.getItems().size();
        int value = 5;
        if (GameServer.bServer) {
            value = ServerOptions.instance.MaxItemsForLootRespawn.getValue();
        }
        if (size >= value) {
            return;
        }
        LootRespawn.existingItems.clear();
        LootRespawn.existingItems.addAll(itemContainer.getItems());
        ItemPickerJava.fillContainer(itemContainer, null);
        final ArrayList<InventoryItem> items = itemContainer.getItems();
        if (items == null || size == items.size()) {
            return;
        }
        itemContainer.setHasBeenLooted(false);
        LootRespawn.newItems.clear();
        for (int i = 0; i < items.size(); ++i) {
            final InventoryItem inventoryItem = items.get(i);
            if (!LootRespawn.existingItems.contains(inventoryItem)) {
                LootRespawn.newItems.add(inventoryItem);
                inventoryItem.setAge(0.0f);
            }
        }
        ItemPickerJava.updateOverlaySprite(isoObject);
        if (GameServer.bServer) {
            for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(j);
                if (udpConnection.RelevantTo((float)isoObject.square.x, (float)isoObject.square.y)) {
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
                    startPacket.putShort((short)2);
                    startPacket.putInt((int)isoObject.getX());
                    startPacket.putInt((int)isoObject.getY());
                    startPacket.putInt((int)isoObject.getZ());
                    startPacket.putByte((byte)isoObject.getObjectIndex());
                    startPacket.putByte((byte)isoObject.getContainerIndex(itemContainer));
                    try {
                        CompressIdenticalItems.save(startPacket.bb, LootRespawn.newItems, null);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
                }
            }
        }
    }
    
    static {
        LootRespawn.LastRespawnHour = -1;
        existingItems = new ArrayList<InventoryItem>();
        newItems = new ArrayList<InventoryItem>();
    }
}
