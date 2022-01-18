// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion;

import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaEventManager;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import zombie.debug.DebugLog;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.utils.Bits;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.erosion.season.ErosionSeason;
import zombie.erosion.utils.Noise2D;
import zombie.iso.IsoChunk;
import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.sprite.IsoSpriteManager;

public final class ErosionMain
{
    private static ErosionMain instance;
    private ErosionConfig cfg;
    private boolean debug;
    private IsoSpriteManager sprMngr;
    private ErosionIceQueen IceQueen;
    private boolean isSnow;
    private String world;
    private String cfgPath;
    private IsoChunk chunk;
    private ErosionData.Chunk chunkModData;
    private Noise2D noiseMain;
    private Noise2D noiseMoisture;
    private Noise2D noiseMinerals;
    private Noise2D noiseKudzu;
    private ErosionWorld World;
    private ErosionSeason Season;
    private int tickUnit;
    private int ticks;
    private int eTicks;
    private int day;
    private int month;
    private int year;
    private int epoch;
    private static final int[][] soilTable;
    private int snowFrac;
    private int snowFracYesterday;
    private int[] snowFracOnDay;
    
    public static ErosionMain getInstance() {
        return ErosionMain.instance;
    }
    
    public ErosionMain(final IsoSpriteManager sprMngr, final boolean debug) {
        this.tickUnit = 144;
        this.ticks = 0;
        this.eTicks = 0;
        this.day = 0;
        this.month = 0;
        this.year = 0;
        this.epoch = 0;
        this.snowFrac = 0;
        this.snowFracYesterday = 0;
        ErosionMain.instance = this;
        this.sprMngr = sprMngr;
        this.debug = debug;
        this.start();
    }
    
    public ErosionConfig getConfig() {
        return this.cfg;
    }
    
    public ErosionSeason getSeasons() {
        return this.Season;
    }
    
    public int getEtick() {
        return this.eTicks;
    }
    
    public IsoSpriteManager getSpriteManager() {
        return this.sprMngr;
    }
    
    public void mainTimer() {
        if (GameClient.bClient) {
            if (Core.bDebug) {
                this.cfg.writeFile(this.cfgPath);
            }
            return;
        }
        final int value = SandboxOptions.instance.ErosionDays.getValue();
        if (this.debug) {
            ++this.eTicks;
        }
        else if (value < 0) {
            this.eTicks = 0;
        }
        else if (value > 0) {
            ++this.ticks;
            this.eTicks = (int)(this.ticks / 144.0f / value * 100.0f);
        }
        else {
            ++this.ticks;
            if (this.ticks >= this.tickUnit) {
                this.ticks = 0;
                ++this.eTicks;
            }
        }
        if (this.eTicks < 0) {
            this.eTicks = Integer.MAX_VALUE;
        }
        final GameTime instance = GameTime.getInstance();
        if (instance.getDay() != this.day || instance.getMonth() != this.month || instance.getYear() != this.year) {
            this.month = instance.getMonth();
            this.year = instance.getYear();
            this.day = instance.getDay();
            ++this.epoch;
            this.Season.setDay(this.day, this.month, this.year);
            this.snowCheck();
        }
        if (GameServer.bServer) {
            for (int i = 0; i < ServerMap.instance.LoadedCells.size(); ++i) {
                final ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(i);
                if (serverCell.bLoaded) {
                    for (int j = 0; j < 5; ++j) {
                        for (int k = 0; k < 5; ++k) {
                            final IsoChunk isoChunk = serverCell.chunks[k][j];
                            if (isoChunk != null) {
                                final ErosionData.Chunk erosionData = isoChunk.getErosionData();
                                if (erosionData.eTickStamp != this.eTicks || erosionData.epoch != this.epoch) {
                                    for (int l = 0; l < 10; ++l) {
                                        for (int n = 0; n < 10; ++n) {
                                            final IsoGridSquare gridSquare = isoChunk.getGridSquare(n, l, 0);
                                            if (gridSquare != null) {
                                                this.loadGridsquare(gridSquare);
                                            }
                                        }
                                    }
                                    erosionData.eTickStamp = this.eTicks;
                                    erosionData.epoch = this.epoch;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.cfg.time.ticks = this.ticks;
        this.cfg.time.eticks = this.eTicks;
        this.cfg.time.epoch = this.epoch;
        this.cfg.writeFile(this.cfgPath);
    }
    
    public void snowCheck() {
    }
    
    public int getSnowFraction() {
        return this.snowFrac;
    }
    
    public int getSnowFractionYesterday() {
        return this.snowFracYesterday;
    }
    
    public boolean isSnow() {
        return this.isSnow;
    }
    
    public void sendState(final ByteBuffer byteBuffer) {
        if (!GameServer.bServer) {
            return;
        }
        byteBuffer.putInt(this.eTicks);
        byteBuffer.putInt(this.ticks);
        byteBuffer.putInt(this.epoch);
        byteBuffer.put((byte)this.getSnowFraction());
        byteBuffer.put((byte)this.getSnowFractionYesterday());
        byteBuffer.putFloat(GameTime.getInstance().getTimeOfDay());
    }
    
    public void receiveState(final ByteBuffer byteBuffer) {
        if (!GameClient.bClient) {
            return;
        }
        final int eTicks = this.eTicks;
        final int epoch = this.epoch;
        this.eTicks = byteBuffer.getInt();
        this.ticks = byteBuffer.getInt();
        this.epoch = byteBuffer.getInt();
        this.cfg.time.ticks = this.ticks;
        this.cfg.time.eticks = this.eTicks;
        this.cfg.time.epoch = this.epoch;
        byteBuffer.get();
        byteBuffer.get();
        byteBuffer.getFloat();
        final GameTime instance = GameTime.getInstance();
        if (instance.getDay() != this.day || instance.getMonth() != this.month || instance.getYear() != this.year) {
            this.month = instance.getMonth();
            this.year = instance.getYear();
            this.day = instance.getDay();
            this.Season.setDay(this.day, this.month, this.year);
        }
        if (eTicks != this.eTicks || epoch != this.epoch) {
            this.updateMapNow();
        }
    }
    
    private void loadGridsquare(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare != null && isoGridSquare.chunk != null && isoGridSquare.getZ() == 0) {
            this.getChunk(isoGridSquare);
            final ErosionData.Square erosionData = isoGridSquare.getErosionData();
            if (!erosionData.init) {
                this.initGridSquare(isoGridSquare, erosionData);
                this.World.validateSpawn(isoGridSquare, erosionData, this.chunkModData);
            }
            if (erosionData.doNothing) {
                return;
            }
            if (this.chunkModData.eTickStamp >= this.eTicks && this.chunkModData.epoch == this.epoch) {
                return;
            }
            this.World.update(isoGridSquare, erosionData, this.chunkModData, this.eTicks);
        }
    }
    
    private void initGridSquare(final IsoGridSquare isoGridSquare, final ErosionData.Square square) {
        final int x = isoGridSquare.getX();
        final int y = isoGridSquare.getY();
        final float layeredNoise = this.noiseMain.layeredNoise(x / 10.0f, y / 10.0f);
        square.noiseMainByte = Bits.packFloatUnitToByte(layeredNoise);
        square.noiseMain = layeredNoise;
        square.noiseMainInt = (int)Math.floor(square.noiseMain * 100.0f);
        square.noiseKudzu = this.noiseKudzu.layeredNoise(x / 10.0f, y / 10.0f);
        square.soil = this.chunkModData.soil;
        final float magicNum = square.rand(x, y, 100) / 100.0f;
        square.magicNumByte = Bits.packFloatUnitToByte(magicNum);
        square.magicNum = magicNum;
        square.regions.clear();
        square.init = true;
    }
    
    private void getChunk(final IsoGridSquare isoGridSquare) {
        this.chunk = isoGridSquare.getChunk();
        this.chunkModData = this.chunk.getErosionData();
        if (this.chunkModData.init) {
            return;
        }
        this.initChunk(this.chunk, this.chunkModData);
    }
    
    private void initChunk(final IsoChunk isoChunk, final ErosionData.Chunk chunk) {
        chunk.set(isoChunk);
        final float n = chunk.x / 5.0f;
        final float n2 = chunk.y / 5.0f;
        final float layeredNoise = this.noiseMoisture.layeredNoise(n, n2);
        final float layeredNoise2 = this.noiseMinerals.layeredNoise(n, n2);
        final int n3 = (layeredNoise < 1.0f) ? ((int)Math.floor(layeredNoise * 10.0f)) : 9;
        final int n4 = (layeredNoise2 < 1.0f) ? ((int)Math.floor(layeredNoise2 * 10.0f)) : 9;
        chunk.init = true;
        chunk.eTickStamp = -1;
        chunk.epoch = -1;
        chunk.moisture = layeredNoise;
        chunk.minerals = layeredNoise2;
        chunk.soil = ErosionMain.soilTable[n3][n4] - 1;
    }
    
    private boolean initConfig() {
        final String s = "erosion.ini";
        if (!GameClient.bClient) {
            this.cfg = new ErosionConfig();
            this.cfgPath = ZomboidFileSystem.instance.getFileNameInCurrentSave(s);
            final File file = new File(this.cfgPath);
            if (file.exists()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                if (this.cfg.readFile(file.getAbsolutePath())) {
                    return true;
                }
                this.cfg = new ErosionConfig();
            }
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s));
            if (!file2.exists() && !Core.getInstance().isNoSave()) {
                final File mediaFile = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, s));
                if (mediaFile.exists()) {
                    try {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, mediaFile.getAbsolutePath(), file2.getAbsolutePath()));
                        Files.copy(mediaFile.toPath(), file2.toPath(), new CopyOption[0]);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (file2.exists()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file2.getAbsolutePath()));
                if (!this.cfg.readFile(file2.getAbsolutePath())) {
                    this.cfg = new ErosionConfig();
                }
            }
            switch (SandboxOptions.instance.getErosionSpeed()) {
                case 1: {
                    final ErosionConfig.Time time = this.cfg.time;
                    time.tickunit /= 5;
                    break;
                }
                case 2: {
                    final ErosionConfig.Time time2 = this.cfg.time;
                    time2.tickunit /= 2;
                }
                case 4: {
                    final ErosionConfig.Time time3 = this.cfg.time;
                    time3.tickunit *= 2;
                    break;
                }
                case 5: {
                    final ErosionConfig.Time time4 = this.cfg.time;
                    time4.tickunit *= 5;
                    break;
                }
            }
            final float n = this.cfg.time.tickunit * 100 / 144.0f;
            final float n2 = (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
            this.cfg.time.eticks = (int)Math.floor(Math.min(1.0f, n2 / n) * 100.0f);
            final int value = SandboxOptions.instance.ErosionDays.getValue();
            if (value > 0) {
                this.cfg.time.tickunit = 144;
                this.cfg.time.eticks = (int)Math.floor(Math.min(1.0f, n2 / value) * 100.0f);
            }
            return true;
        }
        this.cfg = GameClient.instance.erosionConfig;
        assert this.cfg != null;
        GameClient.instance.erosionConfig = null;
        this.cfgPath = ZomboidFileSystem.instance.getFileNameInCurrentSave(s);
        return true;
    }
    
    public void start() {
        if (!this.initConfig()) {
            return;
        }
        this.world = Core.GameSaveWorld;
        this.tickUnit = this.cfg.time.tickunit;
        this.ticks = this.cfg.time.ticks;
        this.eTicks = this.cfg.time.eticks;
        this.month = GameTime.getInstance().getMonth();
        this.year = GameTime.getInstance().getYear();
        this.day = GameTime.getInstance().getDay();
        this.debug = (!GameServer.bServer && this.cfg.debug.enabled);
        this.cfg.consolePrint();
        (this.noiseMain = new Noise2D()).addLayer(this.cfg.seeds.seedMain_0, 0.5f, 3.0f);
        this.noiseMain.addLayer(this.cfg.seeds.seedMain_1, 2.0f, 5.0f);
        this.noiseMain.addLayer(this.cfg.seeds.seedMain_2, 5.0f, 8.0f);
        (this.noiseMoisture = new Noise2D()).addLayer(this.cfg.seeds.seedMoisture_0, 2.0f, 3.0f);
        this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_1, 1.6f, 5.0f);
        this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_2, 0.6f, 8.0f);
        (this.noiseMinerals = new Noise2D()).addLayer(this.cfg.seeds.seedMinerals_0, 2.0f, 3.0f);
        this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_1, 1.6f, 5.0f);
        this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_2, 0.6f, 8.0f);
        (this.noiseKudzu = new Noise2D()).addLayer(this.cfg.seeds.seedKudzu_0, 6.0f, 3.0f);
        this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_1, 3.0f, 5.0f);
        this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_2, 0.5f, 8.0f);
        this.Season = new ErosionSeason();
        final ErosionConfig.Season season = this.cfg.season;
        int tempMin = season.tempMin;
        int tempMax = season.tempMax;
        if (SandboxOptions.instance.getTemperatureModifier() == 1) {
            tempMin -= 10;
            tempMax -= 10;
        }
        else if (SandboxOptions.instance.getTemperatureModifier() == 2) {
            tempMin -= 5;
            tempMax -= 5;
        }
        else if (SandboxOptions.instance.getTemperatureModifier() == 4) {
            tempMin += (int)7.5;
            tempMax += 4;
        }
        else if (SandboxOptions.instance.getTemperatureModifier() == 5) {
            tempMin += 15;
            tempMax += 8;
        }
        this.Season.init(season.lat, tempMax, tempMin, season.tempDiff, season.seasonLag, season.noon, season.seedA, season.seedB, season.seedC);
        this.Season.setRain(season.jan, season.feb, season.mar, season.apr, season.may, season.jun, season.jul, season.aug, season.sep, season.oct, season.nov, season.dec);
        this.Season.setDay(this.day, this.month, this.year);
        LuaEventManager.triggerEvent("OnInitSeasons", this.Season);
        this.IceQueen = new ErosionIceQueen(this.sprMngr);
        this.World = new ErosionWorld();
        if (!this.World.init()) {
            return;
        }
        this.snowCheck();
        if (this.debug) {}
        if (GameServer.bServer) {}
    }
    
    private void loadChunk(final IsoChunk isoChunk) {
        final ErosionData.Chunk erosionData = isoChunk.getErosionData();
        if (!erosionData.init) {
            this.initChunk(isoChunk, erosionData);
        }
        erosionData.eTickStamp = this.eTicks;
        erosionData.epoch = this.epoch;
    }
    
    public void DebugUpdateMapNow() {
        this.updateMapNow();
    }
    
    private void updateMapNow() {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(i);
            if (!chunkMap.ignore) {
                IsoChunkMap.bSettingChunk.lock();
                try {
                    for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                        for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
                            final IsoChunk chunk = chunkMap.getChunk(k, j);
                            if (chunk != null) {
                                final ErosionData.Chunk erosionData = chunk.getErosionData();
                                if (erosionData.eTickStamp != this.eTicks || erosionData.epoch != this.epoch) {
                                    for (int l = 0; l < 10; ++l) {
                                        for (int n = 0; n < 10; ++n) {
                                            final IsoGridSquare gridSquare = chunk.getGridSquare(n, l, 0);
                                            if (gridSquare != null) {
                                                this.loadGridsquare(gridSquare);
                                            }
                                        }
                                    }
                                    erosionData.eTickStamp = this.eTicks;
                                    erosionData.epoch = this.epoch;
                                }
                            }
                        }
                    }
                }
                finally {
                    IsoChunkMap.bSettingChunk.unlock();
                }
            }
        }
    }
    
    public static void LoadGridsquare(final IsoGridSquare isoGridSquare) {
        ErosionMain.instance.loadGridsquare(isoGridSquare);
    }
    
    public static void ChunkLoaded(final IsoChunk isoChunk) {
        ErosionMain.instance.loadChunk(isoChunk);
    }
    
    public static void EveryTenMinutes() {
        ErosionMain.instance.mainTimer();
    }
    
    public static void Reset() {
        ErosionMain.instance = null;
    }
    
    static {
        soilTable = new int[][] { { 1, 1, 1, 1, 1, 4, 4, 4, 4, 4 }, { 1, 1, 1, 1, 2, 5, 4, 4, 4, 4 }, { 1, 1, 1, 2, 2, 5, 5, 4, 4, 4 }, { 1, 1, 2, 2, 3, 6, 5, 5, 4, 4 }, { 1, 2, 2, 3, 3, 6, 6, 5, 5, 4 }, { 7, 8, 8, 9, 9, 12, 12, 11, 11, 10 }, { 7, 7, 8, 8, 9, 12, 11, 11, 10, 10 }, { 7, 7, 7, 8, 8, 11, 11, 10, 10, 10 }, { 7, 7, 7, 7, 8, 11, 10, 10, 10, 10 }, { 7, 7, 7, 7, 7, 10, 10, 10, 10, 10 } };
    }
}
