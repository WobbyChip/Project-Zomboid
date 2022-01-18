// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.znet.SteamUtils;
import zombie.network.ServerOptions;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.vehicles.VehicleManager;
import zombie.erosion.ErosionRegions;
import zombie.erosion.season.ErosionIceQueen;
import zombie.network.ServerMap;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.iso.sprite.IsoSprite;
import java.io.RandomAccessFile;
import zombie.core.Core;
import zombie.network.GameClient;
import java.io.IOException;
import zombie.gameStates.IngameState;
import zombie.gameStates.GameLoadingState;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.iso.sprite.IsoSpriteManager;
import java.util.HashMap;

public final class WorldConverter
{
    public static final WorldConverter instance;
    public static boolean converting;
    public HashMap<Integer, Integer> TilesetConversions;
    int oldID;
    
    public WorldConverter() {
        this.TilesetConversions = null;
        this.oldID = 0;
    }
    
    public void convert(final String s, final IsoSpriteManager isoSpriteManager) throws IOException {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s, File.separator));
        if (file.exists()) {
            WorldConverter.converting = true;
            final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            final int int1 = dataInputStream.readInt();
            dataInputStream.close();
            if (int1 < 186) {
                if (int1 < 24) {
                    GameLoadingState.build23Stop = true;
                    return;
                }
                try {
                    this.convert(s, int1, 186);
                }
                catch (Exception ex) {
                    IngameState.createWorld(s);
                    IngameState.copyWorld(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), s);
                    ex.printStackTrace();
                }
            }
            WorldConverter.converting = false;
        }
    }
    
    private void convert(final String s, final int n, final int n2) {
        if (GameClient.bClient) {
            return;
        }
        GameLoadingState.convertingWorld = true;
        final String gameSaveWorld = Core.GameSaveWorld;
        IngameState.createWorld(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        IngameState.copyWorld(s, Core.GameSaveWorld);
        Core.GameSaveWorld = gameSaveWorld;
        if (n2 >= 14 && n < 14) {
            try {
                this.convertchunks(s, 25, 25);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if (n == 7) {
            try {
                this.convertchunks(s);
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        if (n <= 4) {
            this.loadconversionmap(n, "tiledefinitions");
            this.loadconversionmap(n, "newtiledefinitions");
            try {
                this.convertchunks(s);
            }
            catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }
        GameLoadingState.convertingWorld = false;
    }
    
    private void convertchunks(final String s) throws IOException {
        final IsoChunkMap isoChunkMap = new IsoChunkMap(new IsoCell(300, 300));
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s, File.separator));
        if (!file.exists()) {
            file.mkdir();
        }
        for (final String s2 : file.list()) {
            if (s2.contains(".bin") && !s2.equals("map.bin") && !s2.equals("map_p.bin") && !s2.matches("map_p[0-9]+\\.bin") && !s2.equals("map_t.bin") && !s2.equals("map_c.bin") && !s2.equals("map_ver.bin") && !s2.equals("map_sand.bin") && !s2.equals("map_mov.bin") && !s2.equals("map_meta.bin") && !s2.equals("map_cm.bin")) {
                if (!s2.equals("pc.bin")) {
                    if (!s2.startsWith("zpop_")) {
                        if (!s2.startsWith("chunkdata_")) {
                            final String[] split = s2.replace(".bin", "").replace("map_", "").split("_");
                            isoChunkMap.LoadChunkForLater(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0, 0);
                            isoChunkMap.SwapChunkBuffers();
                            isoChunkMap.getChunk(0, 0).Save(true);
                        }
                    }
                }
            }
        }
    }
    
    private void convertchunks(final String s, final int saveoffsetx, final int saveoffsety) throws IOException {
        final IsoCell isoCell = new IsoCell(300, 300);
        final IsoChunkMap isoChunkMap = new IsoChunkMap(isoCell);
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s, File.separator));
        if (!file.exists()) {
            file.mkdir();
        }
        final String[] list = file.list();
        IsoWorld.saveoffsetx = saveoffsetx;
        IsoWorld.saveoffsety = saveoffsety;
        IsoWorld.instance.MetaGrid.Create();
        WorldStreamer.instance.create();
        for (final String s2 : list) {
            if (s2.contains(".bin") && !s2.equals("map.bin") && !s2.equals("map_p.bin") && !s2.matches("map_p[0-9]+\\.bin") && !s2.equals("map_t.bin") && !s2.equals("map_c.bin") && !s2.equals("map_ver.bin") && !s2.equals("map_sand.bin") && !s2.equals("map_mov.bin") && !s2.equals("map_meta.bin") && !s2.equals("map_cm.bin")) {
                if (!s2.equals("pc.bin")) {
                    if (!s2.startsWith("zpop_")) {
                        if (!s2.startsWith("chunkdata_")) {
                            final String[] split = s2.replace(".bin", "").replace("map_", "").split("_");
                            final int int1 = Integer.parseInt(split[0]);
                            final int int2 = Integer.parseInt(split[1]);
                            final IsoChunk isoChunk = new IsoChunk(isoCell);
                            isoChunk.refs.add(isoCell.ChunkMap[0]);
                            WorldStreamer.instance.addJobConvert(isoChunk, 0, 0, int1, int2);
                            while (!isoChunk.bLoaded) {
                                try {
                                    Thread.sleep(20L);
                                }
                                catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            final IsoChunk isoChunk2 = isoChunk;
                            isoChunk2.wx += saveoffsetx * 30;
                            final IsoChunk isoChunk3 = isoChunk;
                            isoChunk3.wy += saveoffsety * 30;
                            isoChunk.jobType = IsoChunk.JobType.Convert;
                            isoChunk.Save(true);
                            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s, File.separator, s2));
                            while (!ChunkSaveWorker.instance.toSaveQueue.isEmpty()) {
                                try {
                                    Thread.sleep(13L);
                                }
                                catch (InterruptedException ex2) {
                                    ex2.printStackTrace();
                                }
                            }
                            file2.delete();
                        }
                    }
                }
            }
        }
    }
    
    private void loadconversionmap(final int n, final String s) {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n));
        if (file.exists()) {
            try {
                final RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
                for (int int1 = IsoWorld.readInt(randomAccessFile), i = 0; i < int1; ++i) {
                    Thread.sleep(4L);
                    final String trim = IsoWorld.readString(randomAccessFile).trim();
                    IsoWorld.readString(randomAccessFile);
                    IsoWorld.readInt(randomAccessFile);
                    IsoWorld.readInt(randomAccessFile);
                    for (int int2 = IsoWorld.readInt(randomAccessFile), j = 0; j < int2; ++j) {
                        final IsoSprite isoSprite = IsoSpriteManager.instance.NamedMap.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, j));
                        if (this.TilesetConversions == null) {
                            this.TilesetConversions = new HashMap<Integer, Integer>();
                        }
                        this.TilesetConversions.put(this.oldID, isoSprite.ID);
                        ++this.oldID;
                        for (int int3 = IsoWorld.readInt(randomAccessFile), k = 0; k < int3; ++k) {
                            IsoWorld.readString(randomAccessFile).trim();
                            IsoWorld.readString(randomAccessFile).trim();
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public void softreset() {
        final String s = Core.GameSaveWorld = GameServer.ServerName;
        final IsoCell isoCell = new IsoCell(300, 300);
        final IsoChunk o = new IsoChunk(isoCell);
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s, File.separator));
        if (!file.exists()) {
            file.mkdir();
        }
        final String[] list = file.list();
        if (CoopSlave.instance != null) {
            CoopSlave.instance.sendMessage("softreset-count", null, Integer.toString(list.length));
        }
        IsoWorld.instance.MetaGrid.Create();
        ServerMap.instance.init(IsoWorld.instance.MetaGrid);
        new ErosionIceQueen(IsoSpriteManager.instance);
        ErosionRegions.init();
        WorldStreamer.instance.create();
        VehicleManager.instance = new VehicleManager();
        int length = list.length;
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, length));
        for (final String s2 : list) {
            --length;
            if (s2.startsWith("zpop_")) {
                deleteFile(s2);
            }
            else if (s2.equals("map_t.bin")) {
                deleteFile(s2);
            }
            else if (s2.equals("map_meta.bin") || s2.equals("map_zone.bin")) {
                deleteFile(s2);
            }
            else if (s2.equals("reanimated.bin")) {
                deleteFile(s2);
            }
            else if (s2.matches("map_[0-9]+_[0-9]+\\.bin")) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                final String[] split = s2.replace(".bin", "").replace("map_", "").split("_");
                final int int1 = Integer.parseInt(split[0]);
                final int int2 = Integer.parseInt(split[1]);
                o.refs.add(isoCell.ChunkMap[0]);
                WorldStreamer.instance.addJobWipe(o, 0, 0, int1, int2);
                while (!o.bLoaded) {
                    try {
                        Thread.sleep(20L);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                o.jobType = IsoChunk.JobType.Convert;
                o.FloorBloodSplats.clear();
                try {
                    o.Save(true);
                }
                catch (IOException ex2) {
                    ex2.printStackTrace();
                }
                o.doReuseGridsquares();
                IsoChunkMap.chunkStore.remove(o);
                if (length % 100 == 0) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, length));
                }
                if (CoopSlave.instance != null && length % 10 == 0) {
                    CoopSlave.instance.sendMessage("softreset-remaining", null, Integer.toString(length));
                }
            }
        }
        GameServer.ResetID = Rand.Next(10000000);
        ServerOptions.instance.putSaveOption("ResetID", String.valueOf(GameServer.ResetID));
        IsoWorld.instance.CurrentCell = null;
        DebugLog.log("soft-reset complete, server terminated");
        if (CoopSlave.instance != null) {
            CoopSlave.instance.sendMessage("softreset-finished", null, "");
        }
        SteamUtils.shutdown();
        System.exit(0);
    }
    
    private static void deleteFile(final String s) {
        new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, GameServer.ServerName, File.separator, s)).delete();
    }
    
    static {
        instance = new WorldConverter();
    }
}
