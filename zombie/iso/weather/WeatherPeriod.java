// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.GameWindow;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.erosion.season.ErosionSeason;
import java.util.List;
import java.util.Collections;
import zombie.SandboxOptions;
import zombie.iso.IsoWorld;
import java.util.Iterator;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.Lua.LuaEventManager;
import zombie.GameTime;
import zombie.debug.DebugLog;
import zombie.core.math.PZMath;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;

public class WeatherPeriod
{
    public static final int STAGE_START = 0;
    public static final int STAGE_SHOWERS = 1;
    public static final int STAGE_HEAVY_PRECIP = 2;
    public static final int STAGE_STORM = 3;
    public static final int STAGE_CLEARING = 4;
    public static final int STAGE_MODERATE = 5;
    public static final int STAGE_DRIZZLE = 6;
    public static final int STAGE_BLIZZARD = 7;
    public static final int STAGE_TROPICAL_STORM = 8;
    public static final int STAGE_INTERMEZZO = 9;
    public static final int STAGE_MODDED = 10;
    public static final int STAGE_KATEBOB_STORM = 11;
    public static final int STAGE_MAX = 12;
    public static final float FRONT_STRENGTH_THRESHOLD = 0.1f;
    private ClimateManager climateManager;
    private ClimateManager.AirFront frontCache;
    private double startTime;
    private double duration;
    private double currentTime;
    private WeatherStage currentStage;
    private ArrayList<WeatherStage> weatherStages;
    private int weatherStageIndex;
    private Stack<WeatherStage> stagesPool;
    private boolean isRunning;
    private float totalProgress;
    private float stageProgress;
    private float weatherNoise;
    private static float maxTemperatureInfluence;
    private float temperatureInfluence;
    private float currentStrength;
    private float rainThreshold;
    private float windAngleDirMod;
    private boolean isThunderStorm;
    private boolean isTropicalStorm;
    private boolean isBlizzard;
    private float precipitationFinal;
    private ThunderStorm thunderStorm;
    private ClimateColorInfo cloudColor;
    private ClimateColorInfo cloudColorReddish;
    private ClimateColorInfo cloudColorGreenish;
    private ClimateColorInfo cloudColorBlueish;
    private ClimateColorInfo cloudColorPurplish;
    private ClimateColorInfo cloudColorTropical;
    private ClimateColorInfo cloudColorBlizzard;
    private static boolean PRINT_STUFF;
    private static float kateBobStormProgress;
    private int kateBobStormX;
    private int kateBobStormY;
    private Random seededRandom;
    private ClimateValues climateValues;
    private boolean isDummy;
    private boolean hasStartedInit;
    private static final HashMap<Integer, StrLerpVal> cache;
    
    public WeatherPeriod(final ClimateManager climateManager, final ThunderStorm thunderStorm) {
        this.frontCache = new ClimateManager.AirFront();
        this.weatherStages = new ArrayList<WeatherStage>(20);
        this.weatherStageIndex = 0;
        this.stagesPool = new Stack<WeatherStage>();
        this.isRunning = false;
        this.totalProgress = 0.0f;
        this.stageProgress = 0.0f;
        this.temperatureInfluence = 0.0f;
        this.windAngleDirMod = 1.0f;
        this.isThunderStorm = false;
        this.isTropicalStorm = false;
        this.isBlizzard = false;
        this.precipitationFinal = 0.0f;
        this.cloudColor = new ClimateColorInfo(0.4f, 0.2f, 0.2f, 0.4f);
        this.cloudColorReddish = new ClimateColorInfo(0.66f, 0.12f, 0.12f, 0.4f);
        this.cloudColorGreenish = new ClimateColorInfo(0.32f, 0.48f, 0.12f, 0.4f);
        this.cloudColorBlueish = new ClimateColorInfo(0.16f, 0.48f, 0.48f, 0.4f);
        this.cloudColorPurplish = new ClimateColorInfo(0.66f, 0.12f, 0.66f, 0.4f);
        this.cloudColorTropical = new ClimateColorInfo(0.4f, 0.2f, 0.2f, 0.4f);
        this.cloudColorBlizzard = new ClimateColorInfo(0.12f, 0.13f, 0.21f, 0.5f, 0.38f, 0.4f, 0.5f, 0.8f);
        this.kateBobStormX = 2000;
        this.kateBobStormY = 2000;
        this.isDummy = false;
        this.hasStartedInit = false;
        this.climateManager = climateManager;
        this.thunderStorm = thunderStorm;
        for (int i = 0; i < 30; ++i) {
            this.stagesPool.push(new WeatherStage());
        }
        WeatherPeriod.PRINT_STUFF = true;
        this.seededRandom = new Random(1984L);
        this.climateValues = climateManager.getClimateValuesCopy();
    }
    
    public void setDummy(final boolean isDummy) {
        this.isDummy = isDummy;
    }
    
    public static float getMaxTemperatureInfluence() {
        return WeatherPeriod.maxTemperatureInfluence;
    }
    
    public void setKateBobStormProgress(final float n) {
        WeatherPeriod.kateBobStormProgress = PZMath.clamp_01(n);
    }
    
    public void setKateBobStormCoords(final int kateBobStormX, final int kateBobStormY) {
        this.kateBobStormX = kateBobStormX;
        this.kateBobStormY = kateBobStormY;
    }
    
    public ClimateColorInfo getCloudColorReddish() {
        return this.cloudColorReddish;
    }
    
    public ClimateColorInfo getCloudColorGreenish() {
        return this.cloudColorGreenish;
    }
    
    public ClimateColorInfo getCloudColorBlueish() {
        return this.cloudColorBlueish;
    }
    
    public ClimateColorInfo getCloudColorPurplish() {
        return this.cloudColorPurplish;
    }
    
    public ClimateColorInfo getCloudColorTropical() {
        return this.cloudColorTropical;
    }
    
    public ClimateColorInfo getCloudColorBlizzard() {
        return this.cloudColorBlizzard;
    }
    
    public boolean isRunning() {
        return this.isRunning;
    }
    
    public double getDuration() {
        return this.duration;
    }
    
    public ClimateManager.AirFront getFrontCache() {
        return this.frontCache;
    }
    
    public int getCurrentStageID() {
        return (this.currentStage != null) ? this.currentStage.stageID : -1;
    }
    
    public WeatherStage getCurrentStage() {
        return this.currentStage;
    }
    
    public double getWeatherNoise() {
        return this.weatherNoise;
    }
    
    public float getCurrentStrength() {
        return this.currentStrength;
    }
    
    public float getRainThreshold() {
        return this.rainThreshold;
    }
    
    public boolean isThunderStorm() {
        return this.isThunderStorm;
    }
    
    public boolean isTropicalStorm() {
        return this.isTropicalStorm;
    }
    
    public boolean isBlizzard() {
        return this.isBlizzard;
    }
    
    public float getPrecipitationFinal() {
        return this.precipitationFinal;
    }
    
    public ClimateColorInfo getCloudColor() {
        return this.cloudColor;
    }
    
    public void setCloudColor(final ClimateColorInfo cloudColor) {
        this.cloudColor = cloudColor;
    }
    
    public float getTotalProgress() {
        return this.totalProgress;
    }
    
    public float getStageProgress() {
        return this.stageProgress;
    }
    
    public boolean hasTropical() {
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            if (this.weatherStages.get(i).getStageID() == 8) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasStorm() {
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            if (this.weatherStages.get(i).getStageID() == 3) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasBlizzard() {
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            if (this.weatherStages.get(i).getStageID() == 7) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasHeavyRain() {
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            if (this.weatherStages.get(i).getStageID() == 2) {
                return true;
            }
        }
        return false;
    }
    
    public float getTotalStrength() {
        return this.frontCache.getStrength();
    }
    
    public WeatherStage getStageForWorldAge(final double n) {
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            if (n >= this.weatherStages.get(i).getStageStart() && n < this.weatherStages.get(i).getStageEnd()) {
                return this.weatherStages.get(i);
            }
        }
        return null;
    }
    
    public float getWindAngleDegrees() {
        return this.frontCache.getAngleDegrees();
    }
    
    public int getFrontType() {
        return this.frontCache.getType();
    }
    
    private void print(final String s) {
        if (WeatherPeriod.PRINT_STUFF && !this.isDummy) {
            DebugLog.log(s);
        }
    }
    
    public void setPrintStuff(final boolean print_STUFF) {
        WeatherPeriod.PRINT_STUFF = print_STUFF;
    }
    
    public boolean getPrintStuff() {
        return WeatherPeriod.PRINT_STUFF;
    }
    
    public void initSimulationDebug(final ClimateManager.AirFront airFront, final double n) {
        final GameTime instance = GameTime.getInstance();
        this.init(airFront, n, instance.getYear(), instance.getMonth(), instance.getDayPlusOne(), -1, -1.0f);
    }
    
    public void initSimulationDebug(final ClimateManager.AirFront airFront, final double n, final int n2, final float n3) {
        final GameTime instance = GameTime.getInstance();
        this.init(airFront, n, instance.getYear(), instance.getMonth(), instance.getDayPlusOne(), n2, n3);
    }
    
    protected void init(final ClimateManager.AirFront airFront, final double n, final int n2, final int n3, final int n4) {
        this.init(airFront, n, n2, n3, n4, -1, -1.0f);
    }
    
    protected void init(final ClimateManager.AirFront airFront, final double n, final int n2, final int n3, final int n4, final int n5, final float n6) {
        this.climateValues.pollDate(n2, n3, n4);
        this.reseed(n2, n3, n4);
        this.hasStartedInit = false;
        if (!this.startInit(airFront, n)) {
            return;
        }
        if (n5 >= 0 && n5 < 12) {
            this.createSingleStage(n5, n6);
        }
        else {
            this.createWeatherPattern();
        }
        LuaEventManager.triggerEvent("OnWeatherPeriodStart", this);
        this.endInit();
    }
    
    protected void reseed(final int n, final int n2, final int n3) {
        final long seed = (n - 1990) * 100000 + (long)(n2 * n3 * 1234) + (n - 1990) * n2 * 10000 + ((int)this.climateManager.getSimplexOffsetB() - (int)this.climateManager.getSimplexOffsetA()) * n3;
        this.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, seed));
        this.seededRandom.setSeed(seed);
    }
    
    private float RandNext(float n, float n2) {
        if (n == n2) {
            return n;
        }
        if (n > n2) {
            n = (n2 = n2);
        }
        return n + this.seededRandom.nextFloat() * (n2 - n);
    }
    
    private float RandNext(final float n) {
        return this.seededRandom.nextFloat() * n;
    }
    
    private int RandNext(int n, int n2) {
        if (n == n2) {
            return n;
        }
        if (n > n2) {
            n = (n2 = n2);
        }
        return n + this.seededRandom.nextInt(n2 - n);
    }
    
    private int RandNext(final int bound) {
        return this.seededRandom.nextInt(bound);
    }
    
    public boolean startCreateModdedPeriod(final boolean b, final float n, final float n2) {
        final double worldAgeHours = GameTime.getInstance().getWorldAgeHours();
        final ClimateManager.AirFront airFront = new ClimateManager.AirFront();
        final float clamp = ClimateManager.clamp(0.0f, 360.0f, n2);
        airFront.setFrontType(b ? 1 : -1);
        airFront.setFrontWind(clamp);
        airFront.setStrength(ClimateManager.clamp01(n));
        final GameTime instance = GameTime.getInstance();
        this.reseed(instance.getYear(), instance.getMonth(), instance.getDayPlusOne());
        this.hasStartedInit = false;
        if (!this.startInit(airFront, worldAgeHours)) {
            return false;
        }
        this.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.frontCache.getStrength()));
        this.clearCurrentWeatherStages();
        return true;
    }
    
    public boolean endCreateModdedPeriod() {
        if (!this.endInit()) {
            return false;
        }
        this.linkWeatherStages();
        this.duration = 0.0;
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            this.duration += this.weatherStages.get(i).stageDuration;
        }
        this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, this.duration));
        this.weatherStageIndex = 0;
        this.currentStage = this.weatherStages.get(this.weatherStageIndex).startStage(this.startTime);
        this.print("WeatherPeriod: PATTERN GENERATION FINISHED.");
        return true;
    }
    
    private boolean startInit(final ClimateManager.AirFront airFront, final double startTime) {
        if (this.isRunning || GameClient.bClient || airFront.getStrength() < 0.1f) {
            return false;
        }
        this.startTime = startTime;
        this.frontCache.copyFrom(airFront);
        if (this.frontCache.getAngleDegrees() >= 90.0f && this.frontCache.getAngleDegrees() < 270.0f) {
            this.windAngleDirMod = 1.0f;
        }
        else {
            this.windAngleDirMod = -1.0f;
        }
        return this.hasStartedInit = true;
    }
    
    private boolean endInit() {
        if (this.hasStartedInit && !this.isRunning && !GameClient.bClient && this.weatherStages.size() > 0) {
            this.currentStrength = 0.0f;
            this.totalProgress = 0.0f;
            this.stageProgress = 0.0f;
            this.isRunning = true;
            if (GameServer.bServer && !this.isDummy) {
                this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, null);
            }
            this.hasStartedInit = false;
            return true;
        }
        return this.hasStartedInit = false;
    }
    
    public void stopWeatherPeriod() {
        this.clearCurrentWeatherStages();
        this.currentStage = null;
        this.resetClimateManagerOverrides();
        this.isRunning = false;
        this.totalProgress = 0.0f;
        this.stageProgress = 0.0f;
        LuaEventManager.triggerEvent("OnWeatherPeriodStop", this);
    }
    
    public void writeNetWeatherData(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)(this.isRunning ? 1 : 0));
        if (this.isRunning) {
            byteBuffer.put((byte)(this.isThunderStorm ? 1 : 0));
            byteBuffer.put((byte)(this.isTropicalStorm ? 1 : 0));
            byteBuffer.put((byte)(this.isBlizzard ? 1 : 0));
            byteBuffer.putFloat(this.currentStrength);
            byteBuffer.putDouble(this.duration);
            byteBuffer.putFloat(this.totalProgress);
            byteBuffer.putFloat(this.stageProgress);
        }
    }
    
    public void readNetWeatherData(final ByteBuffer byteBuffer) throws IOException {
        this.isRunning = (byteBuffer.get() == 1);
        if (this.isRunning) {
            this.isThunderStorm = (byteBuffer.get() == 1);
            this.isTropicalStorm = (byteBuffer.get() == 1);
            this.isBlizzard = (byteBuffer.get() == 1);
            this.currentStrength = byteBuffer.getFloat();
            this.duration = byteBuffer.getDouble();
            this.totalProgress = byteBuffer.getFloat();
            this.stageProgress = byteBuffer.getFloat();
        }
        else {
            this.isThunderStorm = false;
            this.isTropicalStorm = false;
            this.isBlizzard = false;
            this.currentStrength = 0.0f;
            this.duration = 0.0;
            this.totalProgress = 0.0f;
            this.stageProgress = 0.0f;
        }
    }
    
    public ArrayList<WeatherStage> getWeatherStages() {
        return this.weatherStages;
    }
    
    private void linkWeatherStages() {
        WeatherStage previousStage = null;
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            final WeatherStage weatherStage = this.weatherStages.get(i);
            WeatherStage nextStage = null;
            if (i + 1 < this.weatherStages.size()) {
                nextStage = this.weatherStages.get(i + 1);
            }
            weatherStage.previousStage = previousStage;
            weatherStage.nextStage = nextStage;
            weatherStage.creationFinished = true;
            previousStage = weatherStage;
        }
    }
    
    private void clearCurrentWeatherStages() {
        this.print("WeatherPeriod: Clearing existing stages...");
        for (final WeatherStage item : this.weatherStages) {
            item.reset();
            this.stagesPool.push(item);
        }
        this.weatherStages.clear();
    }
    
    private void createSingleStage(final int n, final float n2) {
        this.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.frontCache.getStrength()));
        if (n == 8) {
            this.cloudColor = this.cloudColorTropical;
        }
        else if (n == 7) {
            this.cloudColor = this.cloudColorBlizzard;
        }
        this.clearCurrentWeatherStages();
        this.createAndAddStage(0, 1.0);
        this.createAndAddStage(n, n2);
        this.createAndAddStage(4, 1.0);
        this.linkWeatherStages();
        this.duration = 0.0;
        for (int i = 0; i < this.weatherStages.size(); ++i) {
            this.duration += this.weatherStages.get(i).stageDuration;
        }
        this.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n2));
        this.weatherStageIndex = 0;
        this.currentStage = this.weatherStages.get(this.weatherStageIndex).startStage(this.startTime);
        this.print("WeatherPeriod: PATTERN GENERATION FINISHED.");
    }
    
    private void createWeatherPattern() {
        this.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.frontCache.getStrength()));
        this.clearCurrentWeatherStages();
        final ErosionSeason season = this.climateManager.getSeason();
        final float dayMeanTemperature = this.climateValues.getDayMeanTemperature();
        this.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, dayMeanTemperature));
        this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, season.getSeasonName()));
        float n = 0.0f;
        float n2 = 0.0f;
        float n3 = 0.0f;
        float n4 = 1.0f;
        final float randNext = this.RandNext(0.0f, 100.0f);
        int season2 = season.getSeason();
        final boolean equals = IsoWorld.instance.getGameMode().equals("Winter is Coming");
        if (equals) {
            season2 = 5;
        }
        switch (season2) {
            case 5: {
                if (randNext < 45.0f) {
                    this.cloudColor = this.cloudColorPurplish;
                }
                else {
                    this.cloudColor = this.cloudColorBlueish;
                }
                n = 10.0f;
                n2 = 0.0f;
                if (dayMeanTemperature < 5.5f) {
                    n3 = ClimateManager.clamp(0.0f, 85.0f, (5.5f - dayMeanTemperature) * 3.0f) + 25.0f;
                    if (dayMeanTemperature < 2.5f) {
                        n3 += 55.0f;
                    }
                    else if (dayMeanTemperature < 0.0f) {
                        n3 += 75.0f;
                    }
                    if (n3 > 95.0f) {
                        n3 = 95.0f;
                    }
                }
                else {
                    n3 = 0.0f;
                }
                if (!equals) {
                    break;
                }
                if (this.frontCache.getStrength() > 0.75f) {
                    n3 = 100.0f;
                }
                else {
                    n3 = 75.0f;
                }
                if (this.frontCache.getStrength() > 0.5f) {
                    n4 = 1.45f;
                    break;
                }
                break;
            }
            case 1: {
                if (randNext < 75.0f) {
                    this.cloudColor = this.cloudColorGreenish;
                }
                else {
                    this.cloudColor = this.cloudColorBlueish;
                }
                n = 75.0f;
                n2 = 10.0f;
                n3 = 0.0f;
                n4 = 1.25f;
                break;
            }
            case 2: {
                if (randNext < 25.0f) {
                    this.cloudColor = this.cloudColorGreenish;
                }
                else {
                    this.cloudColor = this.cloudColorReddish;
                }
                n = 60.0f;
                n2 = 55.0f;
                n3 = 0.0f;
                break;
            }
            case 3: {
                this.cloudColor = this.cloudColorReddish;
                n = 75.0f;
                n2 = 80.0f;
                n3 = 0.0f;
                n4 = 1.15f;
                break;
            }
            case 4: {
                if (randNext < 50.0f) {
                    this.cloudColor = this.cloudColorReddish;
                }
                else if (randNext < 75.0f) {
                    this.cloudColor = this.cloudColorPurplish;
                }
                else {
                    this.cloudColor = this.cloudColorBlueish;
                }
                n = 100.0f;
                n2 = 25.0f;
                n3 = 0.0f;
                n4 = 1.35f;
                break;
            }
        }
        final float n5 = n4 * this.climateManager.getRainTimeMultiplierMod(SandboxOptions.instance.getRainModifier());
        this.print(invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, this.cloudColor.getExterior().r, this.cloudColor.getExterior().g, this.cloudColor.getExterior().b));
        this.print(invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, n, n2, n3, n5));
        final ArrayList<WeatherStage> list = new ArrayList<WeatherStage>();
        if (this.frontCache.getType() == 1) {
            this.print("WeatherPeriod: Warm to cold front selected.");
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            if (this.frontCache.getStrength() > 0.75f) {
                if (n2 > 0.0f && this.RandNext(0.0f, 100.0f) < n2) {
                    this.print("WeatherPeriod: tropical storm triggered.");
                    b2 = true;
                }
                else if (n3 > 0.0f && this.RandNext(0.0f, 100.0f) < n3) {
                    this.print("WeatherPeriod: blizzard triggered.");
                    b = true;
                }
            }
            if (!b && !b2 && this.frontCache.getStrength() > 0.5f && n > 0.0f && this.RandNext(0.0f, 100.0f) < n) {
                this.print("WeatherPeriod: storm triggered.");
                b3 = true;
            }
            final float n6 = this.RandNext(24.0f, 48.0f) * this.frontCache.getStrength();
            float n7 = 0.0f;
            if (b2) {
                list.add(this.createStage(8, 8.0f + this.RandNext(0.0f, 16.0f * this.frontCache.getStrength())));
                this.cloudColor = this.cloudColorTropical;
                if (this.RandNext(0.0f, 100.0f) < 60.0f * this.frontCache.getStrength()) {
                    list.add(this.createStage(3, 5.0f + this.RandNext(0.0f, 5.0f * this.frontCache.getStrength())));
                }
                if (this.RandNext(0.0f, 100.0f) < 30.0f * this.frontCache.getStrength()) {
                    list.add(this.createStage(3, 5.0f + this.RandNext(0.0f, 5.0f * this.frontCache.getStrength())));
                }
            }
            else if (b) {
                list.add(this.createStage(7, 24.0f + this.RandNext(0.0f, 24.0f * this.frontCache.getStrength())));
                this.cloudColor = this.cloudColorBlizzard;
            }
            else if (b3) {
                list.add(this.createStage(3, 5.0f + this.RandNext(0.0f, 5.0f * this.frontCache.getStrength())));
                if (this.RandNext(0.0f, 100.0f) < 70.0f * this.frontCache.getStrength()) {
                    list.add(this.createStage(3, 4.0f + this.RandNext(0.0f, 4.0f * this.frontCache.getStrength())));
                }
                if (this.RandNext(0.0f, 100.0f) < 50.0f * this.frontCache.getStrength()) {
                    list.add(this.createStage(3, 4.0f + this.RandNext(0.0f, 4.0f * this.frontCache.getStrength())));
                }
                if (this.RandNext(0.0f, 100.0f) < 25.0f * this.frontCache.getStrength()) {
                    list.add(this.createStage(3, 4.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength())));
                }
                if (this.RandNext(0.0f, 100.0f) < 12.5f * this.frontCache.getStrength()) {
                    list.add(this.createStage(3, 4.0f + this.RandNext(0.0f, 2.0f * this.frontCache.getStrength())));
                }
            }
            for (int i = 0; i < list.size(); ++i) {
                n7 += (float)list.get(i).getStageDuration();
            }
            while (n7 < n6) {
                WeatherStage e = null;
                switch (this.RandNext(0, 10)) {
                    case 0: {
                        e = this.createStage(5, 1.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength()));
                        break;
                    }
                    case 1:
                    case 2:
                    case 3: {
                        e = this.createStage(1, 2.0f + this.RandNext(0.0f, 4.0f * this.frontCache.getStrength()));
                        break;
                    }
                    default: {
                        e = this.createStage(2, 2.0f + this.RandNext(0.0f, 4.0f * this.frontCache.getStrength()));
                        break;
                    }
                }
                n7 += (float)e.getStageDuration();
                list.add(e);
            }
        }
        else {
            this.print("WeatherPeriod: Cold to warm front selected.");
            if (this.cloudColor == this.cloudColorReddish) {
                if (this.RandNext(0.0f, 100.0f) < 50.0f) {
                    this.cloudColor = this.cloudColorBlueish;
                }
                else {
                    this.cloudColor = this.cloudColorPurplish;
                }
            }
            final float n8 = this.RandNext(12.0f, 24.0f) * this.frontCache.getStrength();
            float n9 = 0.0f;
            while (n9 < n8) {
                WeatherStage e2 = null;
                switch (this.RandNext(0, 10)) {
                    case 0: {
                        e2 = this.createStage(1, 2.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength()));
                        break;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 4: {
                        e2 = this.createStage(6, 2.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength()));
                        break;
                    }
                    default: {
                        e2 = this.createStage(5, 2.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength()));
                        break;
                    }
                }
                n9 += (float)e2.getStageDuration();
                list.add(e2);
            }
        }
        Collections.shuffle(list, this.seededRandom);
        float n10 = this.RandNext(30.0f, 60.0f);
        this.weatherStages.add(this.createStage(0, 1.0f + this.RandNext(0.0f, 2.0f * this.frontCache.getStrength())));
        for (int j = 0; j < list.size(); ++j) {
            this.weatherStages.add(list.get(j));
            if (j < list.size() - 1 && this.RandNext(0.0f, 100.0f) < n10) {
                this.weatherStages.add(this.createStage(4, 1.0f + this.RandNext(0.0f, 2.0f * this.frontCache.getStrength())));
                this.weatherStages.add(this.createStage(9, 1.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength())));
                n10 = this.RandNext(30.0f, 60.0f);
            }
        }
        if (this.weatherStages.get(this.weatherStages.size() - 1).getStageID() != 9) {
            this.weatherStages.add(this.createStage(4, 2.0f + this.RandNext(0.0f, 3.0f * this.frontCache.getStrength())));
        }
        for (int k = 0; k < this.weatherStages.size(); ++k) {
            final WeatherStage weatherStage = this.weatherStages.get(k);
            weatherStage.stageDuration *= n5;
        }
        this.linkWeatherStages();
        this.duration = 0.0;
        for (int l = 0; l < this.weatherStages.size(); ++l) {
            this.duration += this.weatherStages.get(l).stageDuration;
        }
        this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, this.duration));
        double stageStart = this.startTime;
        for (int index = 0; index < this.weatherStages.size(); ++index) {
            stageStart = this.weatherStages.get(index).setStageStart(stageStart);
        }
        this.weatherStageIndex = 0;
        this.currentStage = this.weatherStages.get(this.weatherStageIndex).startStage(this.startTime);
        this.print("WeatherPeriod: PATTERN GENERATION FINISHED.");
    }
    
    public WeatherStage createAndAddModdedStage(final String s, final double n) {
        return this.createAndAddStage(10, n, s);
    }
    
    public WeatherStage createAndAddStage(final int n, final double n2) {
        return this.createAndAddStage(n, n2, null);
    }
    
    private WeatherStage createAndAddStage(final int n, final double n2, final String s) {
        if (this.isRunning || !this.hasStartedInit || (n == 10 && s == null)) {
            return null;
        }
        final WeatherStage stage = this.createStage(n, n2, s);
        this.weatherStages.add(stage);
        return stage;
    }
    
    private WeatherStage createStage(final int n, final double n2) {
        return this.createStage(n, n2, null);
    }
    
    private WeatherStage createStage(final int stageID, final double stageDuration, final String modID) {
        WeatherStage weatherStage;
        if (!this.stagesPool.isEmpty()) {
            weatherStage = this.stagesPool.pop();
        }
        else {
            weatherStage = new WeatherStage();
        }
        weatherStage.stageID = stageID;
        weatherStage.modID = modID;
        weatherStage.setStageDuration(stageDuration);
        switch (stageID) {
            case 0: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.lerpEntryTo(StrLerpVal.NextTarget);
                break;
            }
            case 1: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = this.frontCache.getStrength() * 0.5f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.NextTarget);
                break;
            }
            case 2: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = this.frontCache.getStrength();
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.Target);
                break;
            }
            case 8: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = 1.0f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.Target);
                weatherStage.fogStrength = 0.6f + this.RandNext(0.0f, 0.4f);
                break;
            }
            case 3:
            case 11: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                if (stageID == 11) {
                    this.print("WeatherPeriod: this storm is a kate and bob storm...");
                }
                weatherStage.targetStrength = this.frontCache.getStrength();
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.Target);
                if (this.RandNext(0, 100) < 33) {
                    weatherStage.fogStrength = 0.1f + this.RandNext(0.0f, 0.4f);
                    break;
                }
                break;
            }
            case 4: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = this.frontCache.getStrength() * 0.25f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.None);
                break;
            }
            case 5: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = this.frontCache.getStrength() * 0.5f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.NextTarget);
                break;
            }
            case 6: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = this.frontCache.getStrength() * 0.25f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.NextTarget);
                break;
            }
            case 7: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = 1.0f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.Target);
                weatherStage.fogStrength = 0.55f + this.RandNext(0.0f, 0.2f);
                break;
            }
            case 9: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                weatherStage.targetStrength = 0.0f;
                weatherStage.lerpEntryTo(StrLerpVal.Target, StrLerpVal.NextTarget);
                break;
            }
            case 10: {
                this.print(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, stageDuration));
                LuaEventManager.triggerEvent("OnInitModdedWeatherStage", this, weatherStage, this.frontCache.getStrength());
                break;
            }
            default: {
                this.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, stageID));
                break;
            }
        }
        return weatherStage;
    }
    
    private void updateCurrentStage() {
        if (this.isDummy) {
            return;
        }
        this.isBlizzard = false;
        this.isThunderStorm = false;
        this.isTropicalStorm = false;
        switch (this.currentStage.stageID) {
            case 0: {
                this.rainThreshold = 0.35f - this.frontCache.getStrength() * 0.2f;
                this.climateManager.fogIntensity.setOverride(0.0f, this.currentStage.linearT);
                break;
            }
            case 1: {
                this.climateManager.fogIntensity.setOverride(0.0f, 1.0f);
                final float clamp01 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0f);
                this.climateManager.windIntensity.setOverride(0.1f * this.weatherNoise, clamp01);
                this.climateManager.windAngleIntensity.setOverride(0.0f, clamp01);
                break;
            }
            case 2: {
                float n = this.frontCache.getStrength() * 0.5f;
                if (this.currentStage.linearT < 0.1f) {
                    n = ClimateManager.clerp((float)((this.currentTime - this.currentStage.stageStart) / (this.currentStage.stageDuration * 0.1)), 0.0f, this.frontCache.getStrength() * 0.5f);
                }
                else if (this.currentStage.linearT > 0.9f) {
                    n = ClimateManager.clerp(1.0f - (float)((this.currentStage.stageEnd - this.currentTime) / (this.currentStage.stageDuration * 0.1)), this.frontCache.getStrength() * 0.5f, 0.0f);
                }
                this.weatherNoise = n + this.weatherNoise * (1.0f - n);
                this.climateManager.fogIntensity.setOverride(0.0f, 1.0f);
                final float clamp2 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0f);
                this.climateManager.windIntensity.setOverride(0.5f * this.weatherNoise, clamp2);
                this.climateManager.windAngleIntensity.setOverride(0.7f * this.weatherNoise * this.windAngleDirMod, clamp2);
                break;
            }
            case 8: {
                this.isTropicalStorm = true;
            }
            case 3:
            case 11: {
                this.isThunderStorm = !this.isTropicalStorm;
                if (!this.currentStage.hasStartedCloud) {
                    final float angleDegrees = this.frontCache.getAngleDegrees();
                    float strength = this.frontCache.getStrength();
                    float n2 = 8000.0f * strength;
                    final float n3 = strength;
                    float n4 = 0.6f * strength;
                    final double stageDuration = this.currentStage.stageDuration;
                    boolean b = strength > 0.7;
                    int next = Rand.Next(1, 3);
                    if (this.currentStage.stageID == 8) {
                        next = 1;
                        n2 = 15000.0f;
                        n4 = 0.8f;
                        b = true;
                        strength = 1.0f;
                    }
                    for (int i = 0; i < next; ++i) {
                        final ThunderStorm.ThunderCloud startThunderCloud = this.thunderStorm.startThunderCloud(strength, angleDegrees, n2, n3, n4, stageDuration, b, (this.currentStage.stageID == 11) ? WeatherPeriod.kateBobStormProgress : 0.0f);
                        if (this.currentStage.stageID == 11 && b && startThunderCloud != null) {
                            startThunderCloud.setCenter(this.kateBobStormX, this.kateBobStormY, angleDegrees);
                        }
                        b = false;
                    }
                    this.currentStage.hasStartedCloud = true;
                }
                float n5 = this.frontCache.getStrength() * 0.5f;
                if (this.currentStage.linearT < 0.1f) {
                    n5 = ClimateManager.clerp((float)((this.currentTime - this.currentStage.stageStart) / (this.currentStage.stageDuration * 0.1)), 0.0f, this.frontCache.getStrength() * 0.5f);
                }
                else if (this.currentStage.linearT > 0.9f) {
                    n5 = ClimateManager.clerp(1.0f - (float)((this.currentStage.stageEnd - this.currentTime) / (this.currentStage.stageDuration * 0.1)), this.frontCache.getStrength() * 0.5f, 0.0f);
                }
                this.weatherNoise = n5 + this.weatherNoise * (1.0f - n5);
                final float clamp3 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0f);
                if (this.currentStage.stageID == 8) {
                    this.climateManager.windIntensity.setOverride(0.4f + 0.6f * this.weatherNoise, clamp3);
                }
                else {
                    this.climateManager.windIntensity.setOverride(0.2f + 0.5f * this.weatherNoise, clamp3);
                }
                this.climateManager.windAngleIntensity.setOverride(0.7f * this.weatherNoise * this.windAngleDirMod, clamp3);
                if (PerformanceSettings.FogQuality == 2) {
                    break;
                }
                if (this.currentStage.fogStrength <= 0.0f) {
                    this.climateManager.fogIntensity.setOverride(0.0f, 1.0f);
                    break;
                }
                this.climateManager.fogIntensity.setOverride(this.currentStage.fogStrength, clamp3);
                if (this.currentStage.stageID == 8) {
                    this.climateManager.colorNewFog.setOverride(this.climateManager.getFogTintTropical(), clamp3);
                    break;
                }
                this.climateManager.colorNewFog.setOverride(this.climateManager.getFogTintStorm(), clamp3);
                break;
            }
            case 4: {
                this.climateManager.fogIntensity.setOverride(0.0f, 1.0f - this.currentStage.linearT);
                break;
            }
            case 5: {
                this.climateManager.fogIntensity.setOverride(0.0f, 1.0f);
                break;
            }
            case 6: {
                this.climateManager.fogIntensity.setOverride(0.0f, 1.0f);
                break;
            }
            case 7: {
                this.isBlizzard = true;
                float n6 = this.frontCache.getStrength() * 0.5f;
                if (this.currentStage.linearT < 0.1f) {
                    n6 = ClimateManager.clerp((float)((this.currentTime - this.currentStage.stageStart) / (this.currentStage.stageDuration * 0.1)), 0.0f, this.frontCache.getStrength() * 0.5f);
                }
                else if (this.currentStage.linearT > 0.9f) {
                    n6 = ClimateManager.clerp(1.0f - (float)((this.currentStage.stageEnd - this.currentTime) / (this.currentStage.stageDuration * 0.1)), this.frontCache.getStrength() * 0.5f, 0.0f);
                }
                this.weatherNoise = n6 + this.weatherNoise * (1.0f - n6);
                final float clamp4 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0f);
                this.climateManager.windIntensity.setOverride(0.75f + 0.25f * this.weatherNoise, clamp4);
                this.climateManager.windAngleIntensity.setOverride(0.7f * this.weatherNoise * this.windAngleDirMod, clamp4);
                if (PerformanceSettings.FogQuality == 2) {
                    break;
                }
                if (this.currentStage.fogStrength > 0.0f) {
                    this.climateManager.fogIntensity.setOverride(this.currentStage.fogStrength, clamp4);
                    break;
                }
                this.climateManager.fogIntensity.setOverride(1.0f, clamp4);
                break;
            }
            case 9: {
                this.climateManager.fogIntensity.setOverride(0.0f, 1.0f);
                break;
            }
            case 10: {
                LuaEventManager.triggerEvent("OnUpdateModdedWeatherStage", this, this.currentStage, this.frontCache.getStrength());
                break;
            }
            default: {
                this.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.currentStage.stageID));
                this.resetClimateManagerOverrides();
                this.isRunning = false;
                if (GameServer.bServer) {
                    this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, null);
                    break;
                }
                break;
            }
        }
    }
    
    public void update(final double currentTime) {
        if (GameClient.bClient || this.isDummy) {
            return;
        }
        if (!this.isRunning || this.currentStage == null || this.weatherStageIndex < 0 || this.weatherStages.size() == 0) {
            if (this.isRunning) {
                this.resetClimateManagerOverrides();
                this.isRunning = false;
                LuaEventManager.triggerEvent("OnWeatherPeriodComplete", this);
                if (GameServer.bServer) {
                    this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, null);
                }
            }
            return;
        }
        if (this.currentTime > this.currentStage.stageEnd) {
            ++this.weatherStageIndex;
            LuaEventManager.triggerEvent("OnWeatherPeriodStage", this);
            if (this.weatherStageIndex >= this.weatherStages.size()) {
                this.isRunning = false;
                this.currentStage = null;
                this.resetClimateManagerOverrides();
                if (GameServer.bServer) {
                    this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, null);
                }
                return;
            }
            if (this.currentStage != null) {
                this.currentStage.exitStrength = this.currentStrength;
            }
            this.currentStage = this.weatherStages.get(this.weatherStageIndex);
            this.currentStage.entryStrength = this.currentStrength;
            this.currentStage.startStage(currentTime);
        }
        this.currentTime = currentTime;
        this.weatherNoise = 0.3f * this.frontCache.getStrength() + (float)SimplexNoise.noise(currentTime, 24000.0) * (1.0f - 0.3f * this.frontCache.getStrength());
        this.weatherNoise = (this.weatherNoise + 1.0f) * 0.5f;
        this.currentStage.updateT(this.currentTime);
        this.stageProgress = this.currentStage.linearT;
        this.totalProgress = (float)(this.currentTime - this.weatherStages.get(0).stageStart) / (float)this.duration;
        this.totalProgress = ClimateManager.clamp01(this.totalProgress);
        this.currentStrength = this.currentStage.getStageCurrentStrength();
        this.updateCurrentStage();
        final float n = ClimateManager.clamp(-1.0f, 1.0f, this.currentStrength * 2.0f) * WeatherPeriod.maxTemperatureInfluence;
        if (this.frontCache.getType() == 1) {
            this.temperatureInfluence = this.climateManager.temperature.internalValue - n;
        }
        else {
            this.temperatureInfluence = this.climateManager.temperature.internalValue + n;
        }
        if (this.isRunning) {
            if (this.weatherNoise > this.rainThreshold) {
                this.precipitationFinal = (this.weatherNoise - this.rainThreshold) / (1.0f - this.rainThreshold);
                this.precipitationFinal *= this.currentStrength;
            }
            else {
                this.precipitationFinal = 0.0f;
            }
            final float precipitationFinal = this.precipitationFinal;
            final float n2 = precipitationFinal * (1.0f - this.climateManager.nightStrength.internalValue);
            float max = Math.max(0.5f + 0.5f * (1.0f - this.climateManager.nightStrength.internalValue), this.climateManager.cloudIntensity.internalValue);
            float n3 = 0.55f;
            if (PerformanceSettings.FogQuality != 2 && this.currentStage.stageID == 8) {
                n3 += 0.35f * this.currentStage.parabolicT;
            }
            final float min = Math.min(1.0f - n3 * precipitationFinal, 1.0f - this.climateManager.nightStrength.internalValue);
            if (PerformanceSettings.FogQuality != 2 && this.currentStage.stageID == 7) {
                max *= 1.0f - 0.75f * this.currentStage.parabolicT;
            }
            this.climateManager.cloudIntensity.setOverride(max, this.currentStrength);
            this.climateManager.precipitationIntensity.setOverride(this.precipitationFinal, 1.0f);
            this.climateManager.globalLight.setOverride(this.cloudColor, n2);
            this.climateManager.globalLightIntensity.setOverride(0.4f, n2);
            this.climateManager.desaturation.setOverride(0.3f, this.currentStrength);
            this.climateManager.temperature.setOverride(this.temperatureInfluence, this.currentStrength);
            this.climateManager.ambient.setOverride(min, precipitationFinal);
            this.climateManager.dayLightStrength.setOverride(min, precipitationFinal);
            if ((this.climateManager.getTemperature() < 0.0f && this.climateManager.getSeason().isSeason(5)) || ClimateManager.WINTER_IS_COMING) {
                this.climateManager.precipitationIsSnow.setOverride(true);
            }
            else {
                this.climateManager.precipitationIsSnow.setEnableOverride(false);
            }
        }
    }
    
    private void resetClimateManagerOverrides() {
        if (this.climateManager != null && !this.isDummy) {
            this.climateManager.resetOverrides();
        }
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        if (!GameClient.bClient || GameServer.bServer) {
            dataOutputStream.writeByte(1);
            dataOutputStream.writeBoolean(this.isRunning);
            if (this.isRunning) {
                dataOutputStream.writeInt(this.weatherStageIndex);
                dataOutputStream.writeFloat(this.currentStrength);
                dataOutputStream.writeFloat(this.rainThreshold);
                dataOutputStream.writeBoolean(this.isThunderStorm);
                dataOutputStream.writeBoolean(this.isTropicalStorm);
                dataOutputStream.writeBoolean(this.isBlizzard);
                this.frontCache.save(dataOutputStream);
                dataOutputStream.writeInt(this.weatherStages.size());
                for (int i = 0; i < this.weatherStages.size(); ++i) {
                    final WeatherStage weatherStage = this.weatherStages.get(i);
                    dataOutputStream.writeInt(weatherStage.stageID);
                    dataOutputStream.writeDouble(weatherStage.stageDuration);
                    weatherStage.save(dataOutputStream);
                }
                this.cloudColor.save(dataOutputStream);
            }
        }
        else {
            dataOutputStream.writeByte(0);
        }
    }
    
    public void load(final DataInputStream dataInputStream, final int n) throws IOException {
        if (dataInputStream.readByte() == 1) {
            this.isRunning = dataInputStream.readBoolean();
            if (this.isRunning) {
                this.weatherStageIndex = dataInputStream.readInt();
                this.currentStrength = dataInputStream.readFloat();
                this.rainThreshold = dataInputStream.readFloat();
                this.isThunderStorm = dataInputStream.readBoolean();
                this.isTropicalStorm = dataInputStream.readBoolean();
                this.isBlizzard = dataInputStream.readBoolean();
                this.frontCache.load(dataInputStream);
                if (this.frontCache.getAngleDegrees() >= 90.0f && this.frontCache.getAngleDegrees() < 270.0f) {
                    this.windAngleDirMod = 1.0f;
                }
                else {
                    this.windAngleDirMod = -1.0f;
                }
                this.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.frontCache.getStrength()));
                this.clearCurrentWeatherStages();
                for (int int1 = dataInputStream.readInt(), i = 0; i < int1; ++i) {
                    final int int2 = dataInputStream.readInt();
                    final double double1 = dataInputStream.readDouble();
                    final WeatherStage e = this.stagesPool.isEmpty() ? new WeatherStage() : this.stagesPool.pop();
                    e.stageID = int2;
                    e.setStageDuration(double1);
                    e.load(dataInputStream, n);
                    this.weatherStages.add(e);
                }
                if (n >= 170) {
                    this.cloudColor.load(dataInputStream, n);
                }
                this.linkWeatherStages();
                this.duration = 0.0;
                for (int j = 0; j < this.weatherStages.size(); ++j) {
                    this.duration += this.weatherStages.get(j).stageDuration;
                }
                if (this.weatherStageIndex >= 0 && this.weatherStageIndex < this.weatherStages.size()) {
                    this.currentStage = this.weatherStages.get(this.weatherStageIndex);
                    this.print("WeatherPeriod: Pattern loaded!");
                }
                else {
                    this.print("WeatherPeriod: Couldnt load stages correctly.");
                    this.isRunning = false;
                }
            }
        }
    }
    
    static {
        WeatherPeriod.maxTemperatureInfluence = 7.0f;
        WeatherPeriod.PRINT_STUFF = false;
        WeatherPeriod.kateBobStormProgress = 0.45f;
        cache = new HashMap<Integer, StrLerpVal>();
    }
    
    public static class WeatherStage
    {
        protected WeatherStage previousStage;
        protected WeatherStage nextStage;
        private double stageStart;
        private double stageEnd;
        private double stageDuration;
        protected int stageID;
        protected float entryStrength;
        protected float exitStrength;
        protected float targetStrength;
        protected StrLerpVal lerpMidVal;
        protected StrLerpVal lerpEndVal;
        protected boolean hasStartedCloud;
        protected float fogStrength;
        protected float linearT;
        protected float parabolicT;
        protected boolean isCycleFirstHalf;
        protected boolean creationFinished;
        protected String modID;
        private float m;
        private float e;
        
        public WeatherStage() {
            this.hasStartedCloud = false;
            this.fogStrength = 0.0f;
            this.isCycleFirstHalf = true;
            this.creationFinished = false;
        }
        
        public WeatherStage(final int stageID) {
            this.hasStartedCloud = false;
            this.fogStrength = 0.0f;
            this.isCycleFirstHalf = true;
            this.creationFinished = false;
            this.stageID = stageID;
        }
        
        public void setStageID(final int stageID) {
            this.stageID = stageID;
        }
        
        public double getStageStart() {
            return this.stageStart;
        }
        
        public double getStageEnd() {
            return this.stageEnd;
        }
        
        public double getStageDuration() {
            return this.stageDuration;
        }
        
        public int getStageID() {
            return this.stageID;
        }
        
        public String getModID() {
            return this.modID;
        }
        
        public float getLinearT() {
            return this.linearT;
        }
        
        public float getParabolicT() {
            return this.parabolicT;
        }
        
        public void setTargetStrength(final float targetStrength) {
            this.targetStrength = targetStrength;
        }
        
        public boolean getHasStartedCloud() {
            return this.hasStartedCloud;
        }
        
        public void setHasStartedCloud(final boolean b) {
            this.hasStartedCloud = true;
        }
        
        public void save(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeDouble(this.stageStart);
            dataOutputStream.writeFloat(this.entryStrength);
            dataOutputStream.writeFloat(this.exitStrength);
            dataOutputStream.writeFloat(this.targetStrength);
            dataOutputStream.writeInt(this.lerpMidVal.getValue());
            dataOutputStream.writeInt(this.lerpEndVal.getValue());
            dataOutputStream.writeBoolean(this.hasStartedCloud);
            dataOutputStream.writeByte((this.modID != null) ? 1 : 0);
            if (this.modID != null) {
                GameWindow.WriteString(dataOutputStream, this.modID);
            }
            dataOutputStream.writeFloat(this.fogStrength);
        }
        
        public void load(final DataInputStream dataInputStream, final int n) throws IOException {
            this.stageStart = dataInputStream.readDouble();
            this.stageEnd = this.stageStart + this.stageDuration;
            this.entryStrength = dataInputStream.readFloat();
            this.exitStrength = dataInputStream.readFloat();
            this.targetStrength = dataInputStream.readFloat();
            this.lerpMidVal = StrLerpVal.fromValue(dataInputStream.readInt());
            this.lerpEndVal = StrLerpVal.fromValue(dataInputStream.readInt());
            this.hasStartedCloud = dataInputStream.readBoolean();
            if (n >= 141 && dataInputStream.readByte() == 1) {
                this.modID = GameWindow.ReadString(dataInputStream);
            }
            if (n >= 170) {
                this.fogStrength = dataInputStream.readFloat();
            }
        }
        
        protected void reset() {
            this.previousStage = null;
            this.nextStage = null;
            this.isCycleFirstHalf = true;
            this.hasStartedCloud = false;
            this.lerpMidVal = StrLerpVal.None;
            this.lerpEndVal = StrLerpVal.None;
            this.entryStrength = 0.0f;
            this.exitStrength = 0.0f;
            this.modID = null;
            this.creationFinished = false;
            this.fogStrength = 0.0f;
        }
        
        protected WeatherStage startStage(final double stageStart) {
            this.stageStart = stageStart;
            this.stageEnd = stageStart + this.stageDuration;
            this.hasStartedCloud = false;
            return this;
        }
        
        protected double setStageStart(final double stageStart) {
            this.stageStart = stageStart;
            return this.stageEnd = stageStart + this.stageDuration;
        }
        
        protected WeatherStage setStageDuration(final double stageDuration) {
            this.stageDuration = stageDuration;
            if (this.stageDuration < 1.0) {
                this.stageDuration = 1.0;
            }
            return this;
        }
        
        protected WeatherStage overrideStageDuration(final double stageDuration) {
            this.stageDuration = stageDuration;
            return this;
        }
        
        public void lerpEntryTo(final int n, final int n2) {
            if (!this.creationFinished) {
                this.lerpEntryTo(StrLerpVal.fromValue(n), StrLerpVal.fromValue(n2));
            }
        }
        
        protected void lerpEntryTo(final StrLerpVal strLerpVal) {
            this.lerpEntryTo(StrLerpVal.None, strLerpVal);
        }
        
        protected void lerpEntryTo(final StrLerpVal lerpMidVal, final StrLerpVal lerpEndVal) {
            if (!this.creationFinished) {
                this.lerpMidVal = lerpMidVal;
                this.lerpEndVal = lerpEndVal;
            }
        }
        
        public float getStageCurrentStrength() {
            this.m = this.getLerpValue(this.lerpMidVal);
            this.e = this.getLerpValue(this.lerpEndVal);
            if (this.lerpMidVal == StrLerpVal.None) {
                return ClimateManager.clerp(this.linearT, this.entryStrength, this.e);
            }
            if (this.isCycleFirstHalf) {
                return ClimateManager.clerp(this.parabolicT, this.entryStrength, this.m);
            }
            return ClimateManager.clerp(this.parabolicT, this.e, this.m);
        }
        
        private float getLerpValue(final StrLerpVal strLerpVal) {
            switch (strLerpVal) {
                case Entry: {
                    return this.entryStrength;
                }
                case Target: {
                    return this.targetStrength;
                }
                case NextTarget: {
                    return (this.nextStage != null) ? this.nextStage.targetStrength : 0.0f;
                }
                case None: {
                    return 0.0f;
                }
                default: {
                    return 0.0f;
                }
            }
        }
        
        private WeatherStage updateT(final double n) {
            this.linearT = this.getPeriodLerpT(n);
            if (this.stageID == 11) {
                this.linearT = WeatherPeriod.kateBobStormProgress + (1.0f - WeatherPeriod.kateBobStormProgress) * this.linearT;
            }
            if (this.linearT < 0.5f) {
                this.parabolicT = this.linearT * 2.0f;
                this.isCycleFirstHalf = true;
            }
            else {
                this.parabolicT = 2.0f - this.linearT * 2.0f;
                this.isCycleFirstHalf = false;
            }
            return this;
        }
        
        private float getPeriodLerpT(final double n) {
            if (n < this.stageStart) {
                return 0.0f;
            }
            if (n > this.stageEnd) {
                return 1.0f;
            }
            return (float)((n - this.stageStart) / this.stageDuration);
        }
    }
    
    public enum StrLerpVal
    {
        Entry(1), 
        Target(2), 
        NextTarget(3), 
        None(0);
        
        private final int value;
        
        private StrLerpVal(final int i) {
            this.value = i;
            if (WeatherPeriod.cache.containsKey(i)) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            }
            WeatherPeriod.cache.put(i, this);
        }
        
        public int getValue() {
            return this.value;
        }
        
        public static StrLerpVal fromValue(final int n) {
            if (WeatherPeriod.cache.containsKey(n)) {
                return WeatherPeriod.cache.get(n);
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            return StrLerpVal.None;
        }
        
        private static /* synthetic */ StrLerpVal[] $values() {
            return new StrLerpVal[] { StrLerpVal.Entry, StrLerpVal.Target, StrLerpVal.NextTarget, StrLerpVal.None };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
