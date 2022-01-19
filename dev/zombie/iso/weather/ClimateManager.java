// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.core.math.PZMath;
import zombie.iso.weather.dbg.ClimMngrDebug;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import java.util.GregorianCalendar;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import zombie.core.PerformanceSettings;
import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.sprite.SkyBox;
import zombie.characters.IsoPlayer;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.SandboxOptions;
import zombie.iso.IsoGridSquare;
import zombie.vehicles.BaseVehicle;
import zombie.Lua.LuaManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.Core;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.Lua.LuaEventManager;
import zombie.core.Rand;
import zombie.iso.weather.fx.SteppedUpdateFloat;
import zombie.GameTime;
import se.krka.kahlua.vm.KahluaTable;
import zombie.erosion.season.ErosionSeason;

public class ClimateManager
{
    private boolean DISABLE_SIMULATION;
    private boolean DISABLE_FX_UPDATE;
    private boolean DISABLE_WEATHER_GENERATION;
    public static final int FRONT_COLD = -1;
    public static final int FRONT_STATIONARY = 0;
    public static final int FRONT_WARM = 1;
    public static final float MAX_WINDSPEED_KPH = 120.0f;
    public static final float MAX_WINDSPEED_MPH = 74.5645f;
    private ErosionSeason season;
    private long lastMinuteStamp;
    private KahluaTable modDataTable;
    private float airMass;
    private float airMassDaily;
    private float airMassTemperature;
    private float baseTemperature;
    private float snowFall;
    private float snowStrength;
    private float snowMeltStrength;
    private float snowFracNow;
    boolean canDoWinterSprites;
    private float windPower;
    private WeatherPeriod weatherPeriod;
    private ThunderStorm thunderStorm;
    private double simplexOffsetA;
    private double simplexOffsetB;
    private double simplexOffsetC;
    private double simplexOffsetD;
    private boolean dayDoFog;
    private float dayFogStrength;
    private GameTime gt;
    private double worldAgeHours;
    private boolean tickIsClimateTick;
    private boolean tickIsDayChange;
    private int lastHourStamp;
    private boolean tickIsHourChange;
    private boolean tickIsTenMins;
    private AirFront currentFront;
    private ClimateColorInfo colDay;
    private ClimateColorInfo colDusk;
    private ClimateColorInfo colDawn;
    private ClimateColorInfo colNight;
    private ClimateColorInfo colNightNoMoon;
    private ClimateColorInfo colNightMoon;
    private ClimateColorInfo colTemp;
    private ClimateColorInfo colFog;
    private ClimateColorInfo colFogLegacy;
    private ClimateColorInfo colFogNew;
    private ClimateColorInfo fogTintStorm;
    private ClimateColorInfo fogTintTropical;
    private static ClimateManager instance;
    public static boolean WINTER_IS_COMING;
    public static boolean THE_DESCENDING_FOG;
    public static boolean A_STORM_IS_COMING;
    private ClimateValues climateValues;
    private ClimateForecaster climateForecaster;
    private ClimateHistory climateHistory;
    float dayLightLagged;
    float nightLagged;
    protected ClimateFloat desaturation;
    protected ClimateFloat globalLightIntensity;
    protected ClimateFloat nightStrength;
    protected ClimateFloat precipitationIntensity;
    protected ClimateFloat temperature;
    protected ClimateFloat fogIntensity;
    protected ClimateFloat windIntensity;
    protected ClimateFloat windAngleIntensity;
    protected ClimateFloat cloudIntensity;
    protected ClimateFloat ambient;
    protected ClimateFloat viewDistance;
    protected ClimateFloat dayLightStrength;
    protected ClimateFloat humidity;
    protected ClimateColor globalLight;
    protected ClimateColor colorNewFog;
    protected ClimateBool precipitationIsSnow;
    public static final int FLOAT_DESATURATION = 0;
    public static final int FLOAT_GLOBAL_LIGHT_INTENSITY = 1;
    public static final int FLOAT_NIGHT_STRENGTH = 2;
    public static final int FLOAT_PRECIPITATION_INTENSITY = 3;
    public static final int FLOAT_TEMPERATURE = 4;
    public static final int FLOAT_FOG_INTENSITY = 5;
    public static final int FLOAT_WIND_INTENSITY = 6;
    public static final int FLOAT_WIND_ANGLE_INTENSITY = 7;
    public static final int FLOAT_CLOUD_INTENSITY = 8;
    public static final int FLOAT_AMBIENT = 9;
    public static final int FLOAT_VIEW_DISTANCE = 10;
    public static final int FLOAT_DAYLIGHT_STRENGTH = 11;
    public static final int FLOAT_HUMIDITY = 12;
    public static final int FLOAT_MAX = 13;
    private final ClimateFloat[] climateFloats;
    public static final int COLOR_GLOBAL_LIGHT = 0;
    public static final int COLOR_NEW_FOG = 1;
    public static final int COLOR_MAX = 2;
    private final ClimateColor[] climateColors;
    public static final int BOOL_IS_SNOW = 0;
    public static final int BOOL_MAX = 1;
    private final ClimateBool[] climateBooleans;
    public static final float AVG_FAV_AIR_TEMPERATURE = 22.0f;
    private static double windNoiseOffset;
    private static double windNoiseBase;
    private static double windNoiseFinal;
    private static double windTickFinal;
    private ClimateColorInfo colFlare;
    private boolean flareLaunched;
    private SteppedUpdateFloat flareIntensity;
    private float flareIntens;
    private float flareMaxLifeTime;
    private float flareLifeTime;
    private int nextRandomTargetIntens;
    float fogLerpValue;
    private SeasonColor seasonColorDawn;
    private SeasonColor seasonColorDay;
    private SeasonColor seasonColorDusk;
    private DayInfo previousDay;
    private DayInfo currentDay;
    private DayInfo nextDay;
    public static final byte PacketUpdateClimateVars = 0;
    public static final byte PacketWeatherUpdate = 1;
    public static final byte PacketThunderEvent = 2;
    public static final byte PacketFlare = 3;
    public static final byte PacketAdminVarsUpdate = 4;
    public static final byte PacketRequestAdminVars = 5;
    public static final byte PacketClientChangedAdminVars = 6;
    public static final byte PacketClientChangedWeather = 7;
    private float networkLerp;
    private long networkUpdateStamp;
    private float networkLerpTime;
    private float networkLerpTimeBase;
    private float networkAdjustVal;
    private boolean networkPrint;
    private ClimateNetInfo netInfo;
    private ClimateValues climateValuesFronts;
    private static float[] windAngles;
    private static String[] windAngleStr;
    
    public float getMaxWindspeedKph() {
        return 120.0f;
    }
    
    public float getMaxWindspeedMph() {
        return 74.5645f;
    }
    
    public static float ToKph(final float n) {
        return n * 120.0f;
    }
    
    public static float ToMph(final float n) {
        return n * 74.5645f;
    }
    
    public static ClimateManager getInstance() {
        return ClimateManager.instance;
    }
    
    public static void setInstance(final ClimateManager instance) {
        ClimateManager.instance = instance;
    }
    
    public ClimateManager() {
        this.DISABLE_SIMULATION = false;
        this.DISABLE_FX_UPDATE = false;
        this.DISABLE_WEATHER_GENERATION = false;
        this.lastMinuteStamp = -1L;
        this.modDataTable = null;
        this.snowFall = 0.0f;
        this.snowStrength = 0.0f;
        this.snowMeltStrength = 0.0f;
        this.snowFracNow = 0.0f;
        this.canDoWinterSprites = false;
        this.windPower = 0.0f;
        this.simplexOffsetA = 0.0;
        this.simplexOffsetB = 0.0;
        this.simplexOffsetC = 0.0;
        this.simplexOffsetD = 0.0;
        this.dayDoFog = false;
        this.dayFogStrength = 0.0f;
        this.tickIsClimateTick = false;
        this.tickIsDayChange = false;
        this.lastHourStamp = -1;
        this.tickIsHourChange = false;
        this.tickIsTenMins = false;
        this.currentFront = new AirFront();
        this.dayLightLagged = 0.0f;
        this.nightLagged = 0.0f;
        this.climateFloats = new ClimateFloat[13];
        this.climateColors = new ClimateColor[2];
        this.climateBooleans = new ClimateBool[1];
        this.colFlare = new ClimateColorInfo(1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
        this.flareLaunched = false;
        this.flareIntensity = new SteppedUpdateFloat(0.0f, 0.01f, 0.0f, 1.0f);
        this.nextRandomTargetIntens = 10;
        this.fogLerpValue = 0.0f;
        this.networkLerp = 0.0f;
        this.networkUpdateStamp = 0L;
        this.networkLerpTime = 5000.0f;
        this.networkLerpTimeBase = 5000.0f;
        this.networkAdjustVal = 0.0f;
        this.networkPrint = false;
        this.netInfo = new ClimateNetInfo();
        this.colDay = new ClimateColorInfo();
        this.colDawn = new ClimateColorInfo();
        this.colDusk = new ClimateColorInfo();
        this.colNight = new ClimateColorInfo();
        this.colNightMoon = new ClimateColorInfo();
        this.colFog = new ClimateColorInfo();
        this.colTemp = new ClimateColorInfo();
        this.colDay = new ClimateColorInfo();
        this.colDawn = new ClimateColorInfo();
        this.colDusk = new ClimateColorInfo();
        this.colNight = new ClimateColorInfo(0.33f, 0.33f, 1.0f, 0.4f, 0.33f, 0.33f, 1.0f, 0.4f);
        this.colNightNoMoon = new ClimateColorInfo(0.33f, 0.33f, 1.0f, 0.4f, 0.33f, 0.33f, 1.0f, 0.4f);
        this.colNightMoon = new ClimateColorInfo(0.33f, 0.33f, 1.0f, 0.4f, 0.33f, 0.33f, 1.0f, 0.4f);
        this.colFog = new ClimateColorInfo(0.4f, 0.4f, 0.4f, 0.8f, 0.4f, 0.4f, 0.4f, 0.8f);
        this.colFogLegacy = new ClimateColorInfo(0.3f, 0.3f, 0.3f, 0.8f, 0.3f, 0.3f, 0.3f, 0.8f);
        this.colFogNew = new ClimateColorInfo(0.5f, 0.5f, 0.55f, 0.4f, 0.5f, 0.5f, 0.55f, 0.8f);
        this.fogTintStorm = new ClimateColorInfo(0.5f, 0.45f, 0.4f, 1.0f, 0.5f, 0.45f, 0.4f, 1.0f);
        this.fogTintTropical = new ClimateColorInfo(0.8f, 0.75f, 0.55f, 1.0f, 0.8f, 0.75f, 0.55f, 1.0f);
        this.colTemp = new ClimateColorInfo();
        this.simplexOffsetA = Rand.Next(0, 8000);
        this.simplexOffsetB = Rand.Next(8000, 16000);
        this.simplexOffsetC = Rand.Next(0, -8000);
        this.simplexOffsetD = Rand.Next(-8000, -16000);
        this.initSeasonColors();
        this.setup();
        this.climateValues = new ClimateValues(this);
        this.thunderStorm = new ThunderStorm(this);
        this.weatherPeriod = new WeatherPeriod(this, this.thunderStorm);
        this.climateForecaster = new ClimateForecaster();
        this.climateHistory = new ClimateHistory();
        try {
            LuaEventManager.triggerEvent("OnClimateManagerInit", this);
        }
        catch (Exception ex) {
            System.out.print(ex.getMessage());
            System.out.print(ex.getStackTrace());
        }
    }
    
    public ClimateColorInfo getColNight() {
        return this.colNight;
    }
    
    public ClimateColorInfo getColNightNoMoon() {
        return this.colNightNoMoon;
    }
    
    public ClimateColorInfo getColNightMoon() {
        return this.colNightMoon;
    }
    
    public ClimateColorInfo getColFog() {
        return this.colFog;
    }
    
    public ClimateColorInfo getColFogLegacy() {
        return this.colFogLegacy;
    }
    
    public ClimateColorInfo getColFogNew() {
        return this.colFogNew;
    }
    
    public ClimateColorInfo getFogTintStorm() {
        return this.fogTintStorm;
    }
    
    public ClimateColorInfo getFogTintTropical() {
        return this.fogTintTropical;
    }
    
    private void setup() {
        for (int i = 0; i < this.climateFloats.length; ++i) {
            this.climateFloats[i] = new ClimateFloat();
        }
        for (int j = 0; j < this.climateColors.length; ++j) {
            this.climateColors[j] = new ClimateColor();
        }
        for (int k = 0; k < this.climateBooleans.length; ++k) {
            this.climateBooleans[k] = new ClimateBool();
        }
        this.desaturation = this.initClimateFloat(0, "DESATURATION");
        this.globalLightIntensity = this.initClimateFloat(1, "GLOBAL_LIGHT_INTENSITY");
        this.nightStrength = this.initClimateFloat(2, "NIGHT_STRENGTH");
        this.precipitationIntensity = this.initClimateFloat(3, "PRECIPITATION_INTENSITY");
        this.temperature = this.initClimateFloat(4, "TEMPERATURE");
        this.temperature.min = -80.0f;
        this.temperature.max = 80.0f;
        this.fogIntensity = this.initClimateFloat(5, "FOG_INTENSITY");
        this.windIntensity = this.initClimateFloat(6, "WIND_INTENSITY");
        this.windAngleIntensity = this.initClimateFloat(7, "WIND_ANGLE_INTENSITY");
        this.windAngleIntensity.min = -1.0f;
        this.cloudIntensity = this.initClimateFloat(8, "CLOUD_INTENSITY");
        this.ambient = this.initClimateFloat(9, "AMBIENT");
        this.viewDistance = this.initClimateFloat(10, "VIEW_DISTANCE");
        this.viewDistance.min = 0.0f;
        this.viewDistance.max = 100.0f;
        this.dayLightStrength = this.initClimateFloat(11, "DAYLIGHT_STRENGTH");
        this.humidity = this.initClimateFloat(12, "HUMIDITY");
        this.globalLight = this.initClimateColor(0, "GLOBAL_LIGHT");
        this.colorNewFog = this.initClimateColor(1, "COLOR_NEW_FOG");
        this.colorNewFog.internalValue.setExterior(0.9f, 0.9f, 0.95f, 1.0f);
        this.colorNewFog.internalValue.setInterior(0.9f, 0.9f, 0.95f, 1.0f);
        this.precipitationIsSnow = this.initClimateBool(0, "IS_SNOW");
    }
    
    public int getFloatMax() {
        return 13;
    }
    
    private ClimateFloat initClimateFloat(final int n, final String s) {
        if (n >= 0 && n < 13) {
            return this.climateFloats[n].init(n, s);
        }
        DebugLog.log("Climate: cannot get float override id.");
        return null;
    }
    
    public ClimateFloat getClimateFloat(final int n) {
        if (n >= 0 && n < 13) {
            return this.climateFloats[n];
        }
        DebugLog.log("Climate: cannot get float override id.");
        return null;
    }
    
    public int getColorMax() {
        return 2;
    }
    
    private ClimateColor initClimateColor(final int n, final String s) {
        if (n >= 0 && n < 2) {
            return this.climateColors[n].init(n, s);
        }
        DebugLog.log("Climate: cannot get float override id.");
        return null;
    }
    
    public ClimateColor getClimateColor(final int n) {
        if (n >= 0 && n < 2) {
            return this.climateColors[n];
        }
        DebugLog.log("Climate: cannot get float override id.");
        return null;
    }
    
    public int getBoolMax() {
        return 1;
    }
    
    private ClimateBool initClimateBool(final int n, final String s) {
        if (n >= 0 && n < 1) {
            return this.climateBooleans[n].init(n, s);
        }
        DebugLog.log("Climate: cannot get boolean id.");
        return null;
    }
    
    public ClimateBool getClimateBool(final int n) {
        if (n >= 0 && n < 1) {
            return this.climateBooleans[n];
        }
        DebugLog.log("Climate: cannot get boolean id.");
        return null;
    }
    
    public void setEnabledSimulation(final boolean b) {
        if (!GameClient.bClient && !GameServer.bServer) {
            this.DISABLE_SIMULATION = !b;
        }
        else {
            this.DISABLE_SIMULATION = false;
        }
    }
    
    public boolean getEnabledSimulation() {
        return !this.DISABLE_SIMULATION;
    }
    
    public boolean getEnabledFxUpdate() {
        return !this.DISABLE_FX_UPDATE;
    }
    
    public void setEnabledFxUpdate(final boolean b) {
        if (!GameClient.bClient && !GameServer.bServer) {
            this.DISABLE_FX_UPDATE = !b;
        }
        else {
            this.DISABLE_FX_UPDATE = false;
        }
    }
    
    public boolean getEnabledWeatherGeneration() {
        return this.DISABLE_WEATHER_GENERATION;
    }
    
    public void setEnabledWeatherGeneration(final boolean b) {
        this.DISABLE_WEATHER_GENERATION = !b;
    }
    
    public Color getGlobalLightInternal() {
        return this.globalLight.internalValue.getExterior();
    }
    
    public ClimateColorInfo getGlobalLight() {
        return this.globalLight.finalValue;
    }
    
    public float getGlobalLightIntensity() {
        return this.globalLightIntensity.finalValue;
    }
    
    public ClimateColorInfo getColorNewFog() {
        return this.colorNewFog.finalValue;
    }
    
    public void setNightStrength(final float n) {
        this.nightStrength.finalValue = clamp(0.0f, 1.0f, n);
    }
    
    public float getDesaturation() {
        return this.desaturation.finalValue;
    }
    
    public void setDesaturation(final float finalValue) {
        this.desaturation.finalValue = finalValue;
    }
    
    public float getAirMass() {
        return this.airMass;
    }
    
    public float getAirMassDaily() {
        return this.airMassDaily;
    }
    
    public float getAirMassTemperature() {
        return this.airMassTemperature;
    }
    
    public float getDayLightStrength() {
        return this.dayLightStrength.finalValue;
    }
    
    public float getNightStrength() {
        return this.nightStrength.finalValue;
    }
    
    public float getDayMeanTemperature() {
        return this.currentDay.season.getDayMeanTemperature();
    }
    
    public float getTemperature() {
        return this.temperature.finalValue;
    }
    
    public float getBaseTemperature() {
        return this.baseTemperature;
    }
    
    public float getSnowStrength() {
        return this.snowStrength;
    }
    
    public boolean getPrecipitationIsSnow() {
        return this.precipitationIsSnow.finalValue;
    }
    
    public float getPrecipitationIntensity() {
        return this.precipitationIntensity.finalValue;
    }
    
    public float getFogIntensity() {
        return this.fogIntensity.finalValue;
    }
    
    public float getWindIntensity() {
        return this.windIntensity.finalValue;
    }
    
    public float getWindAngleIntensity() {
        return this.windAngleIntensity.finalValue;
    }
    
    public float getCorrectedWindAngleIntensity() {
        return (this.windAngleIntensity.finalValue + 1.0f) * 0.5f;
    }
    
    public float getWindPower() {
        return this.windPower;
    }
    
    public float getWindspeedKph() {
        return this.windPower * 120.0f;
    }
    
    public float getCloudIntensity() {
        return this.cloudIntensity.finalValue;
    }
    
    public float getAmbient() {
        return this.ambient.finalValue;
    }
    
    public float getViewDistance() {
        return this.viewDistance.finalValue;
    }
    
    public float getHumidity() {
        return this.humidity.finalValue;
    }
    
    public float getWindAngleDegrees() {
        float n;
        if (this.windAngleIntensity.finalValue > 0.0f) {
            n = lerp(this.windAngleIntensity.finalValue, 45.0f, 225.0f);
        }
        else if (this.windAngleIntensity.finalValue > -0.25f) {
            n = lerp(Math.abs(this.windAngleIntensity.finalValue), 45.0f, 0.0f);
        }
        else {
            n = lerp(Math.abs(this.windAngleIntensity.finalValue) - 0.25f, 360.0f, 180.0f);
        }
        if (n > 360.0f) {
            n -= 360.0f;
        }
        if (n < 0.0f) {
            n += 360.0f;
        }
        return n;
    }
    
    public float getWindAngleRadians() {
        return (float)Math.toRadians(this.getWindAngleDegrees());
    }
    
    public float getWindSpeedMovement() {
        final float windIntensity = this.getWindIntensity();
        float n;
        if (windIntensity < 0.15f) {
            n = 0.0f;
        }
        else {
            n = (windIntensity - 0.15f) / 0.85f;
        }
        return n;
    }
    
    public float getWindForceMovement(final IsoGameCharacter isoGameCharacter, final float n) {
        if (isoGameCharacter.square != null && !isoGameCharacter.square.isInARoom()) {
            float n2 = n - this.getWindAngleRadians();
            if (n2 > 6.283185307179586) {
                n2 -= (float)6.283185307179586;
            }
            if (n2 < 0.0f) {
                n2 += (float)6.283185307179586;
            }
            if (n2 > 3.141592653589793) {
                n2 = (float)(3.141592653589793 - (n2 - 3.141592653589793));
            }
            return (float)(n2 / 3.141592653589793);
        }
        return 0.0f;
    }
    
    public boolean isRaining() {
        return this.getPrecipitationIntensity() > 0.0f && !this.getPrecipitationIsSnow();
    }
    
    public float getRainIntensity() {
        return this.isRaining() ? this.getPrecipitationIntensity() : 0.0f;
    }
    
    public boolean isSnowing() {
        return this.getPrecipitationIntensity() > 0.0f && this.getPrecipitationIsSnow();
    }
    
    public float getSnowIntensity() {
        return this.isSnowing() ? this.getPrecipitationIntensity() : 0.0f;
    }
    
    public void setAmbient(final float finalValue) {
        this.ambient.finalValue = finalValue;
    }
    
    public void setViewDistance(final float finalValue) {
        this.viewDistance.finalValue = finalValue;
    }
    
    public void setDayLightStrength(final float finalValue) {
        this.dayLightStrength.finalValue = finalValue;
    }
    
    public void setPrecipitationIsSnow(final boolean finalValue) {
        this.precipitationIsSnow.finalValue = finalValue;
    }
    
    public DayInfo getCurrentDay() {
        return this.currentDay;
    }
    
    public DayInfo getPreviousDay() {
        return this.previousDay;
    }
    
    public DayInfo getNextDay() {
        return this.nextDay;
    }
    
    public ErosionSeason getSeason() {
        return (this.currentDay != null && this.currentDay.getSeason() != null) ? this.currentDay.getSeason() : this.season;
    }
    
    public float getFrontStrength() {
        if (this.currentFront == null) {
            return 0.0f;
        }
        if (Core.bDebug) {
            this.CalculateWeatherFrontStrength(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.currentFront);
        }
        return this.currentFront.strength;
    }
    
    public void stopWeatherAndThunder() {
        if (GameClient.bClient) {
            return;
        }
        this.weatherPeriod.stopWeatherPeriod();
        this.thunderStorm.stopAllClouds();
        if (GameServer.bServer) {
            this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)1, null);
        }
    }
    
    public ThunderStorm getThunderStorm() {
        return this.thunderStorm;
    }
    
    public WeatherPeriod getWeatherPeriod() {
        return this.weatherPeriod;
    }
    
    public boolean getIsThunderStorming() {
        return this.weatherPeriod.isRunning() && (this.weatherPeriod.isThunderStorm() || this.weatherPeriod.isTropicalStorm());
    }
    
    public float getWeatherInterference() {
        if (!this.weatherPeriod.isRunning()) {
            return 0.0f;
        }
        if (this.weatherPeriod.isThunderStorm() || this.weatherPeriod.isTropicalStorm() || this.weatherPeriod.isBlizzard()) {
            return 0.7f * this.weatherPeriod.getCurrentStrength();
        }
        return 0.35f * this.weatherPeriod.getCurrentStrength();
    }
    
    public KahluaTable getModData() {
        if (this.modDataTable == null) {
            this.modDataTable = LuaManager.platform.newTable();
        }
        return this.modDataTable;
    }
    
    public float getAirTemperatureForCharacter(final IsoGameCharacter isoGameCharacter) {
        return this.getAirTemperatureForCharacter(isoGameCharacter, false);
    }
    
    public float getAirTemperatureForCharacter(final IsoGameCharacter isoGameCharacter, final boolean b) {
        if (isoGameCharacter.square == null) {
            return this.getTemperature();
        }
        if (isoGameCharacter.getVehicle() != null) {
            return this.getAirTemperatureForSquare(isoGameCharacter.square, isoGameCharacter.getVehicle(), b);
        }
        return this.getAirTemperatureForSquare(isoGameCharacter.square, null, b);
    }
    
    public float getAirTemperatureForSquare(final IsoGridSquare isoGridSquare) {
        return this.getAirTemperatureForSquare(isoGridSquare, null);
    }
    
    public float getAirTemperatureForSquare(final IsoGridSquare isoGridSquare, final BaseVehicle baseVehicle) {
        return this.getAirTemperatureForSquare(isoGridSquare, baseVehicle, false);
    }
    
    public float getAirTemperatureForSquare(final IsoGridSquare isoGridSquare, final BaseVehicle baseVehicle, final boolean b) {
        float n = this.getTemperature();
        if (isoGridSquare != null) {
            final boolean inARoom = isoGridSquare.isInARoom();
            if (inARoom || baseVehicle != null) {
                final boolean b2 = GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
                if (n <= 22.0f) {
                    if (baseVehicle == null) {
                        if (inARoom && b2) {
                            n = 22.0f;
                        }
                        final float n2 = 22.0f - n;
                        if (isoGridSquare.getZ() < 1) {
                            n += n2 * (0.4f + 0.2f * this.dayLightLagged);
                        }
                        else {
                            n += (float)(n2 * 0.85) * (0.4f + 0.2f * this.dayLightLagged);
                        }
                    }
                }
                else {
                    final float n3 = (n - 22.0f) / 3.5f;
                    if (baseVehicle == null) {
                        if (inARoom && b2) {
                            n = 22.0f;
                        }
                        final float n4 = n - 22.0f;
                        if (isoGridSquare.getZ() < 1) {
                            n -= (float)(n4 * 0.85) * (0.4f + 0.2f * this.dayLightLagged);
                        }
                        else {
                            n -= n4 * (0.4f + 0.2f * this.dayLightLagged + 0.2f * this.nightLagged);
                        }
                    }
                    else {
                        n = n + n3 + n3 * this.dayLightLagged;
                    }
                }
            }
            else if (b) {
                n = Temperature.WindchillCelsiusKph(n, this.getWindspeedKph());
            }
            final float heatSourceHighestTemperature = IsoWorld.instance.getCell().getHeatSourceHighestTemperature(n, isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
            if (heatSourceHighestTemperature > n) {
                n = heatSourceHighestTemperature;
            }
            if (baseVehicle != null) {
                n += baseVehicle.getInsideTemperature();
            }
        }
        return n;
    }
    
    public String getSeasonName() {
        return this.season.getSeasonName();
    }
    
    public float getSeasonProgression() {
        return this.season.getSeasonProgression();
    }
    
    public float getSeasonStrength() {
        return this.season.getSeasonStrength();
    }
    
    public void init(final IsoMetaGrid isoMetaGrid) {
        WorldFlares.Clear();
        this.season = ErosionMain.getInstance().getSeasons();
        ThunderStorm.MAP_MIN_X = isoMetaGrid.minX * 300 - 4000;
        ThunderStorm.MAP_MAX_X = isoMetaGrid.maxX * 300 + 4000;
        ThunderStorm.MAP_MIN_Y = isoMetaGrid.minY * 300 - 4000;
        ThunderStorm.MAP_MAX_Y = isoMetaGrid.maxY * 300 + 4000;
        ClimateManager.windNoiseOffset = 0.0;
        ClimateManager.WINTER_IS_COMING = IsoWorld.instance.getGameMode().equals("Winter is Coming");
        ClimateManager.THE_DESCENDING_FOG = IsoWorld.instance.getGameMode().equals("The Descending Fog");
        ClimateManager.A_STORM_IS_COMING = IsoWorld.instance.getGameMode().equals("A Storm is Coming");
        this.climateForecaster.init(this);
        this.climateHistory.init(this);
    }
    
    public void updateEveryTenMins() {
        this.tickIsTenMins = true;
    }
    
    public void update() {
        this.tickIsClimateTick = false;
        this.tickIsHourChange = false;
        this.tickIsDayChange = false;
        this.gt = GameTime.getInstance();
        this.worldAgeHours = this.gt.getWorldAgeHours();
        if (this.lastMinuteStamp != this.gt.getMinutesStamp()) {
            this.lastMinuteStamp = this.gt.getMinutesStamp();
            this.tickIsClimateTick = true;
            this.updateDayInfo(this.gt.getDayPlusOne(), this.gt.getMonth(), this.gt.getYear());
            this.currentDay.hour = this.gt.getHour();
            this.currentDay.minutes = this.gt.getMinutes();
            if (this.gt.getHour() != this.lastHourStamp) {
                this.tickIsHourChange = true;
                this.lastHourStamp = this.gt.getHour();
            }
            if (this.gt.getTimeOfDay() > 12.0f) {
                ClimateMoon.updatePhase(this.currentDay.getYear(), this.currentDay.getMonth(), this.currentDay.getDay());
            }
        }
        if (this.DISABLE_SIMULATION) {
            final IsoPlayer[] players = IsoPlayer.players;
            for (int i = 0; i < players.length; ++i) {
                final IsoPlayer isoPlayer = players[i];
                if (isoPlayer != null) {
                    isoPlayer.dirtyRecalcGridStackTime = 1.0f;
                }
            }
            return;
        }
        if (this.tickIsDayChange && !GameClient.bClient) {
            this.climateForecaster.updateDayChange(this);
            this.climateHistory.updateDayChange(this);
        }
        if (GameClient.bClient) {
            this.networkLerp = 1.0f;
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < this.networkUpdateStamp + this.networkLerpTime) {
                this.networkLerp = (currentTimeMillis - this.networkUpdateStamp) / this.networkLerpTime;
                if (this.networkLerp < 0.0f) {
                    this.networkLerp = 0.0f;
                }
            }
            for (int j = 0; j < this.climateFloats.length; ++j) {
                this.climateFloats[j].interpolate = this.networkLerp;
            }
            for (int k = 0; k < this.climateColors.length; ++k) {
                this.climateColors[k].interpolate = this.networkLerp;
            }
        }
        if (this.tickIsClimateTick && !GameClient.bClient) {
            this.updateValues();
            this.weatherPeriod.update(this.worldAgeHours);
        }
        if (this.tickIsClimateTick) {
            LuaEventManager.triggerEvent("OnClimateTick", this);
        }
        for (int l = 0; l < this.climateColors.length; ++l) {
            this.climateColors[l].calculate();
        }
        for (int n = 0; n < this.climateFloats.length; ++n) {
            this.climateFloats[n].calculate();
        }
        for (int n2 = 0; n2 < this.climateBooleans.length; ++n2) {
            this.climateBooleans[n2].calculate();
        }
        this.windPower = this.windIntensity.finalValue;
        this.updateWindTick();
        if (this.tickIsClimateTick) {}
        this.updateTestFlare();
        this.thunderStorm.update(this.worldAgeHours);
        if (GameClient.bClient) {
            this.updateSnow();
        }
        else if (this.tickIsClimateTick && !GameClient.bClient) {
            this.updateSnow();
        }
        if (!GameClient.bClient) {
            this.updateViewDistance();
        }
        if (this.tickIsClimateTick && Core.bDebug && !GameServer.bServer) {
            LuaEventManager.triggerEvent("OnClimateTickDebug", this);
        }
        if (this.tickIsClimateTick && GameServer.bServer && this.tickIsTenMins) {
            this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
            this.tickIsTenMins = false;
        }
        if (!this.DISABLE_FX_UPDATE) {
            this.updateFx();
        }
    }
    
    public static double getWindNoiseBase() {
        return ClimateManager.windNoiseBase;
    }
    
    public static double getWindNoiseFinal() {
        return ClimateManager.windNoiseFinal;
    }
    
    public static double getWindTickFinal() {
        return ClimateManager.windTickFinal;
    }
    
    private void updateWindTick() {
        if (GameServer.bServer) {
            return;
        }
        final float finalValue = this.windIntensity.finalValue;
        ClimateManager.windNoiseOffset += (4.0E-4 + 6.0E-4 * finalValue) * GameTime.getInstance().getMultiplier();
        ClimateManager.windNoiseBase = SimplexNoise.noise(0.0, ClimateManager.windNoiseOffset);
        ClimateManager.windNoiseFinal = ClimateManager.windNoiseBase;
        if (ClimateManager.windNoiseFinal > 0.0) {
            ClimateManager.windNoiseFinal *= 0.04 + 0.1 * finalValue;
        }
        else {
            ClimateManager.windNoiseFinal *= 0.04 + 0.1 * finalValue + 0.05f * (finalValue * finalValue);
        }
        ClimateManager.windTickFinal = clamp01(finalValue + (float)ClimateManager.windNoiseFinal);
    }
    
    public void updateOLD() {
        this.tickIsClimateTick = false;
        this.tickIsHourChange = false;
        this.tickIsDayChange = false;
        this.gt = GameTime.getInstance();
        this.worldAgeHours = this.gt.getWorldAgeHours();
        if (this.lastMinuteStamp != this.gt.getMinutesStamp()) {
            this.lastMinuteStamp = this.gt.getMinutesStamp();
            this.tickIsClimateTick = true;
            this.updateDayInfo(this.gt.getDay(), this.gt.getMonth(), this.gt.getYear());
            this.currentDay.hour = this.gt.getHour();
            this.currentDay.minutes = this.gt.getMinutes();
            if (this.gt.getHour() != this.lastHourStamp) {
                this.tickIsHourChange = true;
                this.lastHourStamp = this.gt.getHour();
            }
        }
        if (GameClient.bClient) {
            if (!this.DISABLE_SIMULATION) {
                this.networkLerp = 1.0f;
                final long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis < this.networkUpdateStamp + this.networkLerpTime) {
                    this.networkLerp = (currentTimeMillis - this.networkUpdateStamp) / this.networkLerpTime;
                    if (this.networkLerp < 0.0f) {
                        this.networkLerp = 0.0f;
                    }
                }
                for (int i = 0; i < this.climateFloats.length; ++i) {
                    this.climateFloats[i].interpolate = this.networkLerp;
                }
                for (int j = 0; j < this.climateColors.length; ++j) {
                    this.climateColors[j].interpolate = this.networkLerp;
                }
                if (this.tickIsClimateTick) {
                    LuaEventManager.triggerEvent("OnClimateTick", this);
                }
                this.updateOnTick();
                this.updateTestFlare();
                this.thunderStorm.update(this.worldAgeHours);
                this.updateSnow();
                if (this.tickIsTenMins) {
                    this.tickIsTenMins = false;
                }
            }
            this.updateFx();
        }
        else {
            if (!this.DISABLE_SIMULATION) {
                if (this.tickIsClimateTick) {
                    this.updateValues();
                    this.weatherPeriod.update(this.gt.getWorldAgeHours());
                }
                this.updateOnTick();
                this.updateTestFlare();
                this.thunderStorm.update(this.worldAgeHours);
                if (this.tickIsClimateTick) {
                    this.updateSnow();
                    LuaEventManager.triggerEvent("OnClimateTick", this);
                }
                this.updateViewDistance();
                if (this.tickIsClimateTick && this.tickIsTenMins) {
                    if (GameServer.bServer) {
                        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
                    }
                    this.tickIsTenMins = false;
                }
            }
            if (!this.DISABLE_FX_UPDATE && this.tickIsClimateTick) {
                this.updateFx();
            }
            if (this.DISABLE_SIMULATION) {
                final IsoPlayer[] players = IsoPlayer.players;
                for (int k = 0; k < players.length; ++k) {
                    final IsoPlayer isoPlayer = players[k];
                    if (isoPlayer != null) {
                        isoPlayer.dirtyRecalcGridStackTime = 1.0f;
                    }
                }
            }
        }
    }
    
    private void updateFx() {
        final IsoWeatherFX weatherFX = IsoWorld.instance.getCell().getWeatherFX();
        if (weatherFX == null) {
            return;
        }
        weatherFX.setPrecipitationIntensity(this.precipitationIntensity.finalValue);
        weatherFX.setWindIntensity(this.windIntensity.finalValue);
        weatherFX.setWindPrecipIntensity((float)ClimateManager.windTickFinal * (float)ClimateManager.windTickFinal);
        weatherFX.setWindAngleIntensity(this.windAngleIntensity.finalValue);
        weatherFX.setFogIntensity(this.fogIntensity.finalValue);
        weatherFX.setCloudIntensity(this.cloudIntensity.finalValue);
        weatherFX.setPrecipitationIsSnow(this.precipitationIsSnow.finalValue);
        SkyBox.getInstance().update(this);
        IsoWater.getInstance().update(this);
        IsoPuddles.getInstance().update(this);
    }
    
    private void updateSnow() {
        if (GameClient.bClient) {
            IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0f));
            ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2f);
            return;
        }
        if (!this.tickIsHourChange) {
            this.canDoWinterSprites = (this.season.isSeason(5) || ClimateManager.WINTER_IS_COMING);
            if (this.precipitationIsSnow.finalValue && this.precipitationIntensity.finalValue > this.snowFall) {
                this.snowFall = this.precipitationIntensity.finalValue;
            }
            if (this.temperature.finalValue > 0.0f) {
                final float n = this.temperature.finalValue / 10.0f;
                final float snowMeltStrength = n * 0.2f + n * 0.8f * this.dayLightStrength.finalValue;
                if (snowMeltStrength > this.snowMeltStrength) {
                    this.snowMeltStrength = snowMeltStrength;
                }
            }
            if (!this.precipitationIsSnow.finalValue && this.precipitationIntensity.finalValue > 0.0f) {
                this.snowMeltStrength += this.precipitationIntensity.finalValue;
            }
        }
        else {
            this.snowStrength += this.snowFall;
            this.snowStrength -= this.snowMeltStrength;
            this.snowStrength = clamp(0.0f, 10.0f, this.snowStrength);
            this.snowFracNow = ((this.snowStrength > 7.5f) ? 1.0f : (this.snowStrength / 7.5f));
            IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0f));
            ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2f);
            this.snowFall = 0.0f;
            this.snowMeltStrength = 0.0f;
        }
    }
    
    private void updateSnowOLD() {
    }
    
    public float getSnowFracNow() {
        return this.snowFracNow;
    }
    
    public void resetOverrides() {
        for (int i = 0; i < this.climateColors.length; ++i) {
            this.climateColors[i].setEnableOverride(false);
        }
        for (int j = 0; j < this.climateFloats.length; ++j) {
            this.climateFloats[j].setEnableOverride(false);
        }
        for (int k = 0; k < this.climateBooleans.length; ++k) {
            this.climateBooleans[k].setEnableOverride(false);
        }
    }
    
    public void resetModded() {
        for (int i = 0; i < this.climateColors.length; ++i) {
            this.climateColors[i].setEnableModded(false);
        }
        for (int j = 0; j < this.climateFloats.length; ++j) {
            this.climateFloats[j].setEnableModded(false);
        }
        for (int k = 0; k < this.climateBooleans.length; ++k) {
            this.climateBooleans[k].setEnableModded(false);
        }
    }
    
    public void resetAdmin() {
        for (int i = 0; i < this.climateColors.length; ++i) {
            this.climateColors[i].setEnableAdmin(false);
        }
        for (int j = 0; j < this.climateFloats.length; ++j) {
            this.climateFloats[j].setEnableAdmin(false);
        }
        for (int k = 0; k < this.climateBooleans.length; ++k) {
            this.climateBooleans[k].setEnableAdmin(false);
        }
    }
    
    public void triggerWinterIsComingStorm() {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            final AirFront airFront = new AirFront();
            airFront.copyFrom(this.currentFront);
            airFront.strength = 0.95f;
            airFront.type = 1;
            final GameTime instance = GameTime.getInstance();
            this.weatherPeriod.init(airFront, this.worldAgeHours, instance.getYear(), instance.getMonth(), instance.getDayPlusOne());
        }
    }
    
    public boolean triggerCustomWeather(final float strength, final boolean b) {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            final AirFront airFront = new AirFront();
            airFront.strength = strength;
            airFront.type = (b ? 1 : -1);
            final GameTime instance = GameTime.getInstance();
            this.weatherPeriod.init(airFront, this.worldAgeHours, instance.getYear(), instance.getMonth(), instance.getDayPlusOne());
            return true;
        }
        return false;
    }
    
    public boolean triggerCustomWeatherStage(final int n, final float n2) {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            final AirFront airFront = new AirFront();
            airFront.strength = 0.95f;
            airFront.type = 1;
            final GameTime instance = GameTime.getInstance();
            this.weatherPeriod.init(airFront, this.worldAgeHours, instance.getYear(), instance.getMonth(), instance.getDayPlusOne(), n, n2);
            return true;
        }
        return false;
    }
    
    private void updateOnTick() {
        for (int i = 0; i < this.climateColors.length; ++i) {
            this.climateColors[i].calculate();
        }
        for (int j = 0; j < this.climateFloats.length; ++j) {
            this.climateFloats[j].calculate();
        }
        for (int k = 0; k < this.climateBooleans.length; ++k) {
            this.climateBooleans[k].calculate();
        }
    }
    
    private void updateTestFlare() {
        WorldFlares.update();
    }
    
    public void launchFlare() {
        DebugLog.log("Launching improved flare.");
        final IsoPlayer instance = IsoPlayer.getInstance();
        WorldFlares.launchFlare(7200.0f, (int)instance.getX(), (int)instance.getY(), 50, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        if (IsoPlayer.getInstance() != null && !this.flareLaunched) {
            this.flareLaunched = true;
            this.flareLifeTime = 0.0f;
            this.flareMaxLifeTime = 7200.0f;
            this.flareIntensity.overrideCurrentValue(1.0f);
            this.flareIntens = 1.0f;
            this.nextRandomTargetIntens = 10;
        }
    }
    
    protected double getAirMassNoiseFrequencyMod(final int n) {
        if (n == 1) {
            return 300.0;
        }
        if (n == 2) {
            return 240.0;
        }
        if (n != 3) {
            if (n == 4) {
                return 145.0;
            }
            if (n == 5) {
                return 120.0;
            }
        }
        return 166.0;
    }
    
    protected float getRainTimeMultiplierMod(final int n) {
        if (n == 1) {
            return 0.5f;
        }
        if (n == 2) {
            return 0.75f;
        }
        if (n == 4) {
            return 1.25f;
        }
        if (n == 5) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    private void updateValues() {
        if (this.tickIsDayChange && Core.bDebug && !GameClient.bClient && !GameServer.bServer) {
            ErosionMain.getInstance().DebugUpdateMapNow();
        }
        this.climateValues.updateValues(this.worldAgeHours, this.gt.getTimeOfDay(), this.currentDay, this.nextDay);
        this.airMass = this.climateValues.getNoiseAirmass();
        this.airMassTemperature = this.climateValues.getAirMassTemperature();
        if (this.tickIsHourChange) {
            final int frontType = (this.airMass < 0.0f) ? -1 : 1;
            if (this.currentFront.type != frontType) {
                if (!this.DISABLE_WEATHER_GENERATION && (!ClimateManager.WINTER_IS_COMING || (ClimateManager.WINTER_IS_COMING && GameTime.instance.getWorldAgeHours() > 96.0))) {
                    if (ClimateManager.THE_DESCENDING_FOG) {
                        this.currentFront.type = -1;
                        this.currentFront.strength = Rand.Next(0.2f, 0.45f);
                        this.weatherPeriod.init(this.currentFront, this.worldAgeHours, this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne());
                    }
                    else {
                        this.CalculateWeatherFrontStrength(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.currentFront);
                        this.weatherPeriod.init(this.currentFront, this.worldAgeHours, this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne());
                    }
                }
                this.currentFront.setFrontType(frontType);
            }
            if (ClimateManager.WINTER_IS_COMING || ClimateManager.THE_DESCENDING_FOG || GameTime.instance.getWorldAgeHours() < 72.0 || GameTime.instance.getWorldAgeHours() > 96.0 || this.DISABLE_WEATHER_GENERATION || this.weatherPeriod.isRunning() || Rand.Next(0, 1000) < 50) {}
            if (this.tickIsDayChange) {}
        }
        this.dayDoFog = this.climateValues.isDayDoFog();
        this.dayFogStrength = this.climateValues.getDayFogStrength();
        if (PerformanceSettings.FogQuality == 2) {
            this.dayFogStrength = 0.5f + 0.5f * this.dayFogStrength;
        }
        else {
            this.dayFogStrength = 0.2f + 0.8f * this.dayFogStrength;
        }
        this.baseTemperature = this.climateValues.getBaseTemperature();
        this.dayLightLagged = this.climateValues.getDayLightLagged();
        this.nightLagged = this.climateValues.getDayLightLagged();
        this.temperature.internalValue = this.climateValues.getTemperature();
        this.precipitationIsSnow.internalValue = this.climateValues.isTemperatureIsSnow();
        this.humidity.internalValue = this.climateValues.getHumidity();
        this.windIntensity.internalValue = this.climateValues.getWindIntensity();
        this.windAngleIntensity.internalValue = this.climateValues.getWindAngleIntensity();
        this.windPower = this.windIntensity.internalValue;
        this.currentFront.setFrontWind(this.climateValues.getWindAngleDegrees());
        this.cloudIntensity.internalValue = this.climateValues.getCloudIntensity();
        this.precipitationIntensity.internalValue = 0.0f;
        this.nightStrength.internalValue = this.climateValues.getNightStrength();
        this.dayLightStrength.internalValue = this.climateValues.getDayLightStrength();
        this.ambient.internalValue = this.climateValues.getAmbient();
        this.desaturation.internalValue = this.climateValues.getDesaturation();
        final int season = this.season.getSeason();
        final float seasonProgression = this.season.getSeasonProgression();
        float n = 0.0f;
        int n2 = 0;
        int n3 = 0;
        if (season == 2) {
            n2 = SeasonColor.SPRING;
            n3 = SeasonColor.SUMMER;
            n = 0.5f + seasonProgression * 0.5f;
        }
        else if (season == 3) {
            n2 = SeasonColor.SUMMER;
            n3 = SeasonColor.FALL;
            n = seasonProgression * 0.5f;
        }
        else if (season == 4) {
            if (seasonProgression < 0.5f) {
                n2 = SeasonColor.SUMMER;
                n3 = SeasonColor.FALL;
                n = 0.5f + seasonProgression;
            }
            else {
                n2 = SeasonColor.FALL;
                n3 = SeasonColor.WINTER;
                n = seasonProgression - 0.5f;
            }
        }
        else if (season == 5) {
            if (seasonProgression < 0.5f) {
                n2 = SeasonColor.FALL;
                n3 = SeasonColor.WINTER;
                n = 0.5f + seasonProgression;
            }
            else {
                n2 = SeasonColor.WINTER;
                n3 = SeasonColor.SPRING;
                n = seasonProgression - 0.5f;
            }
        }
        else if (season == 1) {
            if (seasonProgression < 0.5f) {
                n2 = SeasonColor.WINTER;
                n3 = SeasonColor.SPRING;
                n = 0.5f + seasonProgression;
            }
            else {
                n2 = SeasonColor.SPRING;
                n3 = SeasonColor.SUMMER;
                n = seasonProgression - 0.5f;
            }
        }
        final float cloudyT = this.climateValues.getCloudyT();
        this.colDawn = this.seasonColorDawn.update(cloudyT, n, n2, n3);
        this.colDay = this.seasonColorDay.update(cloudyT, n, n2, n3);
        this.colDusk = this.seasonColorDusk.update(cloudyT, n, n2, n3);
        final float time = this.climateValues.getTime();
        final float dawn = this.climateValues.getDawn();
        final float dusk = this.climateValues.getDusk();
        final float noon = this.climateValues.getNoon();
        final float dayFogDuration = this.climateValues.getDayFogDuration();
        if (!ClimateManager.THE_DESCENDING_FOG) {
            if (this.dayDoFog && this.dayFogStrength > 0.0f && time > dawn - 2.0f && time < dawn + dayFogDuration) {
                final float clamp = clamp(0.0f, 1.0f, this.getTimeLerpHours(time, dawn - 2.0f, dawn + dayFogDuration, true) * (dayFogDuration / 3.0f));
                this.fogLerpValue = clamp;
                this.cloudIntensity.internalValue = lerp(clamp, this.cloudIntensity.internalValue, 0.0f);
                final float dayFogStrength = this.dayFogStrength;
                this.fogIntensity.internalValue = clerp(clamp, 0.0f, dayFogStrength);
                if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                    if (PerformanceSettings.FogQuality == 2) {
                        this.desaturation.internalValue = clerp(clamp, this.desaturation.internalValue, 0.8f * dayFogStrength);
                    }
                    else {
                        this.desaturation.internalValue = clerp(clamp, this.desaturation.internalValue, 0.65f * dayFogStrength);
                    }
                }
                else {
                    this.desaturation.internalValue = clerp(clamp, this.desaturation.internalValue, 0.8f * dayFogStrength);
                }
            }
            else {
                this.fogIntensity.internalValue = 0.0f;
            }
        }
        else {
            if (this.gt.getWorldAgeHours() < 72.0) {
                this.fogIntensity.internalValue = (float)this.gt.getWorldAgeHours() / 72.0f;
            }
            else {
                this.fogIntensity.internalValue = 1.0f;
            }
            this.cloudIntensity.internalValue = Math.min(this.cloudIntensity.internalValue, 1.0f - this.fogIntensity.internalValue);
            if (this.weatherPeriod.isRunning()) {
                this.fogIntensity.internalValue = Math.min(this.fogIntensity.internalValue, 0.6f);
            }
            if (PerformanceSettings.FogQuality == 2) {
                final ClimateFloat fogIntensity = this.fogIntensity;
                fogIntensity.internalValue *= 0.93f;
                this.desaturation.internalValue = 0.8f * this.fogIntensity.internalValue;
            }
            else {
                this.desaturation.internalValue = 0.65f * this.fogIntensity.internalValue;
            }
        }
        this.humidity.internalValue = clamp01(this.humidity.internalValue + this.fogIntensity.internalValue * 0.6f);
        final float n4 = 0.6f * this.climateValues.getDayLightStrengthBase();
        final float n5 = 0.4f;
        float n6 = 0.25f * this.climateValues.getDayLightStrengthBase();
        if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
            n6 = 0.8f * this.climateValues.getDayLightStrengthBase();
        }
        if (time < dawn || time > dusk) {
            final float n7 = 24.0f - dusk + dawn;
            if (time > dusk) {
                this.colDusk.interp(this.colDawn, (time - dusk) / n7, this.globalLight.internalValue);
            }
            else {
                this.colDusk.interp(this.colDawn, (24.0f - dusk + time) / n7, this.globalLight.internalValue);
            }
            this.globalLightIntensity.internalValue = lerp(this.climateValues.getLerpNight(), n6, n5);
        }
        else if (time < noon + 2.0f) {
            final float n8 = (time - dawn) / (noon + 2.0f - dawn);
            this.colDawn.interp(this.colDay, n8, this.globalLight.internalValue);
            this.globalLightIntensity.internalValue = lerp(n8, n6, n4);
        }
        else {
            final float n9 = (time - (noon + 2.0f)) / (dusk - (noon + 2.0f));
            this.colDay.interp(this.colDusk, n9, this.globalLight.internalValue);
            this.globalLightIntensity.internalValue = lerp(n9, n4, n6);
        }
        if (this.fogIntensity.internalValue > 0.0f) {
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                if (PerformanceSettings.FogQuality == 2) {
                    this.globalLight.internalValue.interp(this.colFog, this.fogIntensity.internalValue, this.globalLight.internalValue);
                }
                else {
                    this.globalLight.internalValue.interp(this.colFogNew, this.fogIntensity.internalValue, this.globalLight.internalValue);
                }
            }
            else {
                this.globalLight.internalValue.interp(this.colFogLegacy, this.fogIntensity.internalValue, this.globalLight.internalValue);
            }
            this.globalLightIntensity.internalValue = clerp(this.fogLerpValue, this.globalLightIntensity.internalValue, 0.8f);
        }
        this.colNightNoMoon.interp(this.colNightMoon, ClimateMoon.getMoonFloat(), this.colNight);
        this.globalLight.internalValue.interp(this.colNight, this.nightStrength.internalValue, this.globalLight.internalValue);
        final IsoPlayer[] players = IsoPlayer.players;
        for (int i = 0; i < players.length; ++i) {
            final IsoPlayer isoPlayer = players[i];
            if (isoPlayer != null) {
                isoPlayer.dirtyRecalcGridStackTime = 1.0f;
            }
        }
    }
    
    private void updateViewDistance() {
        final float finalValue = this.dayLightStrength.finalValue;
        final float finalValue2 = this.fogIntensity.finalValue;
        final float n = 19.0f - finalValue2 * 8.0f;
        final float n2 = n + 4.0f + 7.0f * finalValue * (1.0f - finalValue2);
        final float viewDistMin = n * 3.0f;
        final float viewDistMax = n2 * 3.0f;
        this.gt.setViewDistMin(viewDistMin);
        this.gt.setViewDistMax(viewDistMax);
        this.viewDistance.internalValue = viewDistMin + (viewDistMax - viewDistMin) * finalValue;
        this.viewDistance.finalValue = this.viewDistance.internalValue;
    }
    
    public void setSeasonColorDawn(final int n, final int n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        if (b) {
            this.seasonColorDawn.setColorExterior(n, n2, n3, n4, n5, n6);
        }
        else {
            this.seasonColorDawn.setColorInterior(n, n2, n3, n4, n5, n6);
        }
    }
    
    public void setSeasonColorDay(final int n, final int n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        if (b) {
            this.seasonColorDay.setColorExterior(n, n2, n3, n4, n5, n6);
        }
        else {
            this.seasonColorDay.setColorInterior(n, n2, n3, n4, n5, n6);
        }
    }
    
    public void setSeasonColorDusk(final int n, final int n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        if (b) {
            this.seasonColorDusk.setColorExterior(n, n2, n3, n4, n5, n6);
        }
        else {
            this.seasonColorDusk.setColorInterior(n, n2, n3, n4, n5, n6);
        }
    }
    
    public ClimateColorInfo getSeasonColor(final int n, final int n2, final int n3) {
        SeasonColor seasonColor = null;
        if (n == 0) {
            seasonColor = this.seasonColorDawn;
        }
        else if (n == 1) {
            seasonColor = this.seasonColorDay;
        }
        else if (n == 2) {
            seasonColor = this.seasonColorDusk;
        }
        if (seasonColor != null) {
            return seasonColor.getColor(n2, n3);
        }
        return null;
    }
    
    private void initSeasonColors() {
        final SeasonColor seasonColorDawn = new SeasonColor();
        seasonColorDawn.setIgnoreNormal(true);
        this.seasonColorDawn = seasonColorDawn;
        final SeasonColor seasonColorDay = new SeasonColor();
        seasonColorDay.setIgnoreNormal(true);
        this.seasonColorDay = seasonColorDay;
        final SeasonColor seasonColorDusk = new SeasonColor();
        seasonColorDusk.setIgnoreNormal(false);
        this.seasonColorDusk = seasonColorDusk;
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        if (!GameClient.bClient || GameServer.bServer) {
            dataOutputStream.writeByte(1);
            dataOutputStream.writeDouble(this.simplexOffsetA);
            dataOutputStream.writeDouble(this.simplexOffsetB);
            dataOutputStream.writeDouble(this.simplexOffsetC);
            dataOutputStream.writeDouble(this.simplexOffsetD);
            this.currentFront.save(dataOutputStream);
            dataOutputStream.writeFloat(this.snowFracNow);
            dataOutputStream.writeFloat(this.snowStrength);
            dataOutputStream.writeBoolean(this.canDoWinterSprites);
            dataOutputStream.writeBoolean(this.dayDoFog);
            dataOutputStream.writeFloat(this.dayFogStrength);
        }
        else {
            dataOutputStream.writeByte(0);
        }
        this.weatherPeriod.save(dataOutputStream);
        this.thunderStorm.save(dataOutputStream);
        if (GameServer.bServer) {
            this.desaturation.saveAdmin(dataOutputStream);
            this.globalLightIntensity.saveAdmin(dataOutputStream);
            this.nightStrength.saveAdmin(dataOutputStream);
            this.precipitationIntensity.saveAdmin(dataOutputStream);
            this.temperature.saveAdmin(dataOutputStream);
            this.fogIntensity.saveAdmin(dataOutputStream);
            this.windIntensity.saveAdmin(dataOutputStream);
            this.windAngleIntensity.saveAdmin(dataOutputStream);
            this.cloudIntensity.saveAdmin(dataOutputStream);
            this.ambient.saveAdmin(dataOutputStream);
            this.viewDistance.saveAdmin(dataOutputStream);
            this.dayLightStrength.saveAdmin(dataOutputStream);
            this.globalLight.saveAdmin(dataOutputStream);
            this.precipitationIsSnow.saveAdmin(dataOutputStream);
        }
        if (this.modDataTable != null) {
            dataOutputStream.writeByte(1);
            this.modDataTable.save(dataOutputStream);
        }
        else {
            dataOutputStream.writeByte(0);
        }
        if (GameServer.bServer) {
            this.humidity.saveAdmin(dataOutputStream);
        }
    }
    
    public void load(final DataInputStream dataInputStream, final int n) throws IOException {
        if (dataInputStream.readByte() == 1) {
            this.simplexOffsetA = dataInputStream.readDouble();
            this.simplexOffsetB = dataInputStream.readDouble();
            this.simplexOffsetC = dataInputStream.readDouble();
            this.simplexOffsetD = dataInputStream.readDouble();
            this.currentFront.load(dataInputStream);
            this.snowFracNow = dataInputStream.readFloat();
            this.snowStrength = dataInputStream.readFloat();
            this.canDoWinterSprites = dataInputStream.readBoolean();
            this.dayDoFog = dataInputStream.readBoolean();
            this.dayFogStrength = dataInputStream.readFloat();
        }
        this.weatherPeriod.load(dataInputStream, n);
        this.thunderStorm.load(dataInputStream);
        if (n >= 140 && GameServer.bServer) {
            this.desaturation.loadAdmin(dataInputStream, n);
            this.globalLightIntensity.loadAdmin(dataInputStream, n);
            this.nightStrength.loadAdmin(dataInputStream, n);
            this.precipitationIntensity.loadAdmin(dataInputStream, n);
            this.temperature.loadAdmin(dataInputStream, n);
            this.fogIntensity.loadAdmin(dataInputStream, n);
            this.windIntensity.loadAdmin(dataInputStream, n);
            this.windAngleIntensity.loadAdmin(dataInputStream, n);
            this.cloudIntensity.loadAdmin(dataInputStream, n);
            this.ambient.loadAdmin(dataInputStream, n);
            this.viewDistance.loadAdmin(dataInputStream, n);
            this.dayLightStrength.loadAdmin(dataInputStream, n);
            this.globalLight.loadAdmin(dataInputStream, n);
            this.precipitationIsSnow.loadAdmin(dataInputStream, n);
        }
        if (n >= 141 && dataInputStream.readByte() == 1) {
            if (this.modDataTable == null) {
                this.modDataTable = LuaManager.platform.newTable();
            }
            this.modDataTable.load(dataInputStream, n);
        }
        if (n >= 150 && GameServer.bServer) {
            this.humidity.loadAdmin(dataInputStream, n);
        }
        this.climateValues = new ClimateValues(this);
    }
    
    public void postCellLoadSetSnow() {
        IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0f));
        ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2f);
    }
    
    public void forceDayInfoUpdate() {
        this.currentDay.day = -1;
        this.currentDay.month = -1;
        this.currentDay.year = -1;
        this.gt = GameTime.getInstance();
        this.updateDayInfo(this.gt.getDayPlusOne(), this.gt.getMonth(), this.gt.getYear());
        this.currentDay.hour = this.gt.getHour();
        this.currentDay.minutes = this.gt.getMinutes();
    }
    
    private void updateDayInfo(final int n, final int n2, final int n3) {
        this.tickIsDayChange = false;
        if (this.currentDay == null || this.currentDay.day != n || this.currentDay.month != n2 || this.currentDay.year != n3) {
            this.tickIsDayChange = (this.currentDay != null);
            if (this.currentDay == null) {
                this.currentDay = new DayInfo();
            }
            this.setDayInfo(this.currentDay, n, n2, n3, 0);
            if (this.previousDay == null) {
                this.previousDay = new DayInfo();
                this.previousDay.season = this.season.clone();
            }
            this.setDayInfo(this.previousDay, n, n2, n3, -1);
            if (this.nextDay == null) {
                this.nextDay = new DayInfo();
                this.nextDay.season = this.season.clone();
            }
            this.setDayInfo(this.nextDay, n, n2, n3, 1);
        }
    }
    
    protected void setDayInfo(final DayInfo dayInfo, final int dayOfMonth, final int month, final int year, final int amount) {
        (dayInfo.calendar = new GregorianCalendar(year, month, dayOfMonth, 0, 0)).add(5, amount);
        dayInfo.day = dayInfo.calendar.get(5);
        dayInfo.month = dayInfo.calendar.get(2);
        dayInfo.year = dayInfo.calendar.get(1);
        dayInfo.dateValue = dayInfo.calendar.getTime().getTime();
        if (dayInfo.season == null) {
            dayInfo.season = this.season.clone();
        }
        dayInfo.season.setDay(dayInfo.day, dayInfo.month, dayInfo.year);
    }
    
    protected final void transmitClimatePacket(final ClimateNetAuth climateNetAuth, final byte b, final UdpConnection udpConnection) {
        if (!GameClient.bClient && !GameServer.bServer) {
            return;
        }
        if (climateNetAuth == ClimateNetAuth.Denied) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(BZ)Ljava/lang/String;, b, GameClient.bClient));
            return;
        }
        Label_0095: {
            if (GameClient.bClient) {
                if (climateNetAuth != ClimateNetAuth.ClientOnly) {
                    if (climateNetAuth != ClimateNetAuth.ClientAndServer) {
                        break Label_0095;
                    }
                }
                try {
                    if (this.writePacketContents(GameClient.connection, b)) {
                        PacketTypes.PacketType.ClimateManagerPacket.send(GameClient.connection);
                    }
                    else {
                        GameClient.connection.cancelPacket();
                    }
                }
                catch (Exception ex) {
                    DebugLog.log(ex.getMessage());
                }
            }
        }
        if (GameServer.bServer) {
            if (climateNetAuth != ClimateNetAuth.ServerOnly) {
                if (climateNetAuth != ClimateNetAuth.ClientAndServer) {
                    return;
                }
            }
            try {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    if (udpConnection == null || udpConnection != udpConnection2) {
                        if (this.writePacketContents(udpConnection2, b)) {
                            PacketTypes.PacketType.ClimateManagerPacket.send(udpConnection2);
                        }
                        else {
                            udpConnection2.cancelPacket();
                        }
                    }
                }
            }
            catch (Exception ex2) {
                DebugLog.log(ex2.getMessage());
            }
        }
    }
    
    private boolean writePacketContents(final UdpConnection udpConnection, final byte b) throws IOException {
        if (!GameClient.bClient && !GameServer.bServer) {
            return false;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ClimateManagerPacket.doPacket(startPacket);
        final ByteBuffer bb = startPacket.bb;
        bb.put(b);
        switch (b) {
            case 0: {
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketUpdateClimateVars");
                }
                for (int i = 0; i < this.climateFloats.length; ++i) {
                    bb.putFloat(this.climateFloats[i].finalValue);
                }
                for (int j = 0; j < this.climateColors.length; ++j) {
                    this.climateColors[j].finalValue.write(bb);
                }
                for (int k = 0; k < this.climateBooleans.length; ++k) {
                    bb.put((byte)(this.climateBooleans[k].finalValue ? 1 : 0));
                }
                bb.putFloat(this.airMass);
                bb.putFloat(this.airMassDaily);
                bb.putFloat(this.airMassTemperature);
                bb.putFloat(this.snowFracNow);
                bb.putFloat(this.snowStrength);
                bb.putFloat(this.windPower);
                bb.put((byte)(this.dayDoFog ? 1 : 0));
                bb.putFloat(this.dayFogStrength);
                bb.put((byte)(this.canDoWinterSprites ? 1 : 0));
                this.weatherPeriod.writeNetWeatherData(bb);
                return true;
            }
            case 1: {
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketWeatherUpdate");
                }
                this.weatherPeriod.writeNetWeatherData(bb);
                return true;
            }
            case 2: {
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketThunderEvent");
                }
                this.thunderStorm.writeNetThunderEvent(bb);
                return true;
            }
            case 3: {
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketFlare");
                }
                return true;
            }
            case 5: {
                if (!GameClient.bClient) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketRequestAdminVars");
                }
                bb.put((byte)1);
                return true;
            }
            case 6: {
                if (!GameClient.bClient) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketClientChangedAdminVars");
                }
                for (int l = 0; l < this.climateFloats.length; ++l) {
                    this.climateFloats[l].writeAdmin(bb);
                }
                for (int n = 0; n < this.climateColors.length; ++n) {
                    this.climateColors[n].writeAdmin(bb);
                }
                for (int n2 = 0; n2 < this.climateBooleans.length; ++n2) {
                    this.climateBooleans[n2].writeAdmin(bb);
                }
                return true;
            }
            case 4: {
                if (!GameServer.bServer) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketAdminVarsUpdate");
                }
                for (int n3 = 0; n3 < this.climateFloats.length; ++n3) {
                    this.climateFloats[n3].writeAdmin(bb);
                }
                for (int n4 = 0; n4 < this.climateColors.length; ++n4) {
                    this.climateColors[n4].writeAdmin(bb);
                }
                for (int n5 = 0; n5 < this.climateBooleans.length; ++n5) {
                    this.climateBooleans[n5].writeAdmin(bb);
                }
                return true;
            }
            case 7: {
                if (!GameClient.bClient) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: send PacketClientChangedWeather");
                }
                bb.put((byte)(this.netInfo.IsStopWeather ? 1 : 0));
                bb.put((byte)(this.netInfo.IsTrigger ? 1 : 0));
                bb.put((byte)(this.netInfo.IsGenerate ? 1 : 0));
                bb.putFloat(this.netInfo.TriggerDuration);
                bb.put((byte)(this.netInfo.TriggerStorm ? 1 : 0));
                bb.put((byte)(this.netInfo.TriggerTropical ? 1 : 0));
                bb.put((byte)(this.netInfo.TriggerBlizzard ? 1 : 0));
                bb.putFloat(this.netInfo.GenerateStrength);
                bb.putInt(this.netInfo.GenerateFront);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public final void receiveClimatePacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) throws IOException {
        if (!GameClient.bClient && !GameServer.bServer) {
            return;
        }
        this.readPacketContents(byteBuffer, byteBuffer.get(), udpConnection);
    }
    
    private boolean readPacketContents(final ByteBuffer byteBuffer, final byte b, final UdpConnection udpConnection) throws IOException {
        switch (b) {
            case 0: {
                if (!GameClient.bClient) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketUpdateClimateVars");
                }
                for (int i = 0; i < this.climateFloats.length; ++i) {
                    final ClimateFloat climateFloat = this.climateFloats[i];
                    climateFloat.internalValue = climateFloat.finalValue;
                    climateFloat.setOverride(byteBuffer.getFloat(), 0.0f);
                }
                for (int j = 0; j < this.climateColors.length; ++j) {
                    final ClimateColor climateColor = this.climateColors[j];
                    climateColor.internalValue.setTo(climateColor.finalValue);
                    climateColor.setOverride(byteBuffer, 0.0f);
                }
                for (int k = 0; k < this.climateBooleans.length; ++k) {
                    this.climateBooleans[k].setOverride(byteBuffer.get() == 1);
                }
                this.airMass = byteBuffer.getFloat();
                this.airMassDaily = byteBuffer.getFloat();
                this.airMassTemperature = byteBuffer.getFloat();
                this.snowFracNow = byteBuffer.getFloat();
                this.snowStrength = byteBuffer.getFloat();
                this.windPower = byteBuffer.getFloat();
                this.dayDoFog = (byteBuffer.get() == 1);
                this.dayFogStrength = byteBuffer.getFloat();
                this.canDoWinterSprites = (byteBuffer.get() == 1);
                final long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - this.networkUpdateStamp < this.networkLerpTime) {
                    ++this.networkAdjustVal;
                    if (this.networkAdjustVal > 10.0f) {
                        this.networkAdjustVal = 10.0f;
                    }
                }
                else {
                    --this.networkAdjustVal;
                    if (this.networkAdjustVal < 0.0f) {
                        this.networkAdjustVal = 0.0f;
                    }
                }
                if (this.networkAdjustVal > 0.0f) {
                    this.networkLerpTime = this.networkLerpTimeBase / this.networkAdjustVal;
                }
                else {
                    this.networkLerpTime = this.networkLerpTimeBase;
                }
                this.networkUpdateStamp = currentTimeMillis;
                this.weatherPeriod.readNetWeatherData(byteBuffer);
                return true;
            }
            case 1: {
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketWeatherUpdate");
                }
                this.weatherPeriod.readNetWeatherData(byteBuffer);
                return true;
            }
            case 2: {
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketThunderEvent");
                }
                this.thunderStorm.readNetThunderEvent(byteBuffer);
                return true;
            }
            case 3: {
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketFlare");
                }
                return true;
            }
            case 5: {
                if (!GameServer.bServer) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketRequestAdminVars");
                }
                byteBuffer.get();
                this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)4, null);
                return true;
            }
            case 6: {
                if (!GameServer.bServer) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketClientChangedAdminVars");
                }
                for (int l = 0; l < this.climateFloats.length; ++l) {
                    this.climateFloats[l].readAdmin(byteBuffer);
                }
                for (int n = 0; n < this.climateColors.length; ++n) {
                    this.climateColors[n].readAdmin(byteBuffer);
                }
                for (int n2 = 0; n2 < this.climateBooleans.length; ++n2) {
                    this.climateBooleans[n2].readAdmin(byteBuffer);
                    if (n2 == 0) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(ZZ)Ljava/lang/String;, this.climateBooleans[n2].adminValue, this.climateBooleans[n2].isAdminOverride));
                    }
                }
                this.serverReceiveClientChangeAdminVars();
                return true;
            }
            case 4: {
                if (!GameClient.bClient) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketAdminVarsUpdate");
                }
                for (int n3 = 0; n3 < this.climateFloats.length; ++n3) {
                    this.climateFloats[n3].readAdmin(byteBuffer);
                }
                for (int n4 = 0; n4 < this.climateColors.length; ++n4) {
                    this.climateColors[n4].readAdmin(byteBuffer);
                }
                for (int n5 = 0; n5 < this.climateBooleans.length; ++n5) {
                    this.climateBooleans[n5].readAdmin(byteBuffer);
                }
                return true;
            }
            case 7: {
                if (!GameServer.bServer) {
                    return false;
                }
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketClientChangedWeather");
                }
                this.netInfo.IsStopWeather = (byteBuffer.get() == 1);
                this.netInfo.IsTrigger = (byteBuffer.get() == 1);
                this.netInfo.IsGenerate = (byteBuffer.get() == 1);
                this.netInfo.TriggerDuration = byteBuffer.getFloat();
                this.netInfo.TriggerStorm = (byteBuffer.get() == 1);
                this.netInfo.TriggerTropical = (byteBuffer.get() == 1);
                this.netInfo.TriggerBlizzard = (byteBuffer.get() == 1);
                this.netInfo.GenerateStrength = byteBuffer.getFloat();
                this.netInfo.GenerateFront = byteBuffer.getInt();
                this.serverReceiveClientChangeWeather();
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void serverReceiveClientChangeAdminVars() {
        if (!GameServer.bServer) {
            return;
        }
        if (this.networkPrint) {
            DebugLog.log("clim: serverReceiveClientChangeAdminVars");
        }
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)4, null);
        this.updateOnTick();
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
    }
    
    private void serverReceiveClientChangeWeather() {
        if (!GameServer.bServer) {
            return;
        }
        if (this.networkPrint) {
            DebugLog.log("clim: serverReceiveClientChangeWeather");
        }
        if (this.netInfo.IsStopWeather) {
            if (this.networkPrint) {
                DebugLog.log("clim: IsStopWeather");
            }
            this.stopWeatherAndThunder();
        }
        else if (this.netInfo.IsTrigger) {
            this.stopWeatherAndThunder();
            if (this.netInfo.TriggerStorm) {
                if (this.networkPrint) {
                    DebugLog.log("clim: Trigger Storm");
                }
                this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
            }
            else if (this.netInfo.TriggerTropical) {
                if (this.networkPrint) {
                    DebugLog.log("clim: Trigger Tropical");
                }
                this.triggerCustomWeatherStage(8, this.netInfo.TriggerDuration);
            }
            else if (this.netInfo.TriggerBlizzard) {
                if (this.networkPrint) {
                    DebugLog.log("clim: Trigger Blizzard");
                }
                this.triggerCustomWeatherStage(7, this.netInfo.TriggerDuration);
            }
        }
        else if (this.netInfo.IsGenerate) {
            if (this.networkPrint) {
                DebugLog.log("clim: IsGenerate");
            }
            this.stopWeatherAndThunder();
            this.triggerCustomWeather(this.netInfo.GenerateStrength, this.netInfo.GenerateFront == 0);
        }
        this.updateOnTick();
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
    }
    
    public void transmitServerStopWeather() {
        if (!GameServer.bServer) {
            return;
        }
        this.stopWeatherAndThunder();
        if (this.networkPrint) {
            DebugLog.log("clim: SERVER transmitStopWeather");
        }
        this.updateOnTick();
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
    }
    
    public void transmitServerTriggerStorm(final float n) {
        if (!GameServer.bServer) {
            return;
        }
        if (this.networkPrint) {
            DebugLog.log("clim: SERVER transmitTriggerStorm");
        }
        this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
        this.updateOnTick();
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
    }
    
    public void transmitServerStartRain(final float n) {
        if (!GameServer.bServer) {
            return;
        }
        this.precipitationIntensity.setAdminValue(clamp01(n));
        this.updateOnTick();
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
    }
    
    public void transmitServerStopRain() {
        if (!GameServer.bServer) {
            return;
        }
        this.precipitationIntensity.setEnableAdmin(false);
        this.updateOnTick();
        this.transmitClimatePacket(ClimateNetAuth.ServerOnly, (byte)0, null);
    }
    
    public void transmitRequestAdminVars() {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitRequestAdminVars");
        }
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)5, null);
    }
    
    public void transmitClientChangeAdminVars() {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitClientChangeAdminVars");
        }
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)6, null);
    }
    
    public void transmitStopWeather() {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitStopWeather");
        }
        this.netInfo.reset();
        this.netInfo.IsStopWeather = true;
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)7, null);
    }
    
    public void transmitTriggerStorm(final float triggerDuration) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitTriggerStorm");
        }
        this.netInfo.reset();
        this.netInfo.IsTrigger = true;
        this.netInfo.TriggerStorm = true;
        this.netInfo.TriggerDuration = triggerDuration;
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)7, null);
    }
    
    public void transmitTriggerTropical(final float triggerDuration) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitTriggerTropical");
        }
        this.netInfo.reset();
        this.netInfo.IsTrigger = true;
        this.netInfo.TriggerTropical = true;
        this.netInfo.TriggerDuration = triggerDuration;
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)7, null);
    }
    
    public void transmitTriggerBlizzard(final float triggerDuration) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitTriggerBlizzard");
        }
        this.netInfo.reset();
        this.netInfo.IsTrigger = true;
        this.netInfo.TriggerBlizzard = true;
        this.netInfo.TriggerDuration = triggerDuration;
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)7, null);
    }
    
    public void transmitGenerateWeather(final float n, final int generateFront) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitGenerateWeather");
        }
        this.netInfo.reset();
        this.netInfo.IsGenerate = true;
        this.netInfo.GenerateStrength = clamp01(n);
        this.netInfo.GenerateFront = generateFront;
        if (this.netInfo.GenerateFront < 0 || this.netInfo.GenerateFront > 1) {
            this.netInfo.GenerateFront = 0;
        }
        this.transmitClimatePacket(ClimateNetAuth.ClientOnly, (byte)7, null);
    }
    
    protected float getTimeLerpHours(final float n, final float n2, final float n3) {
        return this.getTimeLerpHours(n, n2, n3, false);
    }
    
    protected float getTimeLerpHours(final float n, final float n2, final float n3, final boolean b) {
        return this.getTimeLerp(clamp(0.0f, 1.0f, n / 24.0f), clamp(0.0f, 1.0f, n2 / 24.0f), clamp(0.0f, 1.0f, n3 / 24.0f), b);
    }
    
    protected float getTimeLerp(final float n, final float n2, final float n3) {
        return this.getTimeLerp(n, n2, n3, false);
    }
    
    protected float getTimeLerp(final float n, final float n2, final float n3, final boolean b) {
        if (n2 <= n3) {
            if (n < n2 || n > n3) {
                return 0.0f;
            }
            final float n4 = n - n2;
            final float n5 = (n3 - n2) * 0.5f;
            if (n4 < n5) {
                return b ? clerp(n4 / n5, 0.0f, 1.0f) : lerp(n4 / n5, 0.0f, 1.0f);
            }
            return b ? clerp((n4 - n5) / n5, 1.0f, 0.0f) : lerp((n4 - n5) / n5, 1.0f, 0.0f);
        }
        else {
            if (n < n2 && n > n3) {
                return 0.0f;
            }
            final float n6 = 1.0f - n2;
            final float n7 = (n >= n2) ? (n - n2) : (n + n6);
            final float n8 = (n3 + n6) * 0.5f;
            if (n7 < n8) {
                return b ? clerp(n7 / n8, 0.0f, 1.0f) : lerp(n7 / n8, 0.0f, 1.0f);
            }
            return b ? clerp((n7 - n8) / n8, 1.0f, 0.0f) : lerp((n7 - n8) / n8, 1.0f, 0.0f);
        }
    }
    
    public static float clamp01(final float n) {
        return clamp(0.0f, 1.0f, n);
    }
    
    public static float clamp(final float a, final float a2, float n) {
        n = Math.min(a2, n);
        n = Math.max(a, n);
        return n;
    }
    
    public static int clamp(final int a, final int a2, int n) {
        n = Math.min(a2, n);
        n = Math.max(a, n);
        return n;
    }
    
    public static float lerp(final float n, final float n2, final float n3) {
        return n2 + n * (n3 - n2);
    }
    
    public static float clerp(final float n, final float n2, final float n3) {
        final float n4 = (float)(1.0 - Math.cos(n * 3.141592653589793)) / 2.0f;
        return n2 * (1.0f - n4) + n3 * n4;
    }
    
    public static float normalizeRange(final float n, final float n2) {
        return clamp(0.0f, 1.0f, n / n2);
    }
    
    public static float posToPosNegRange(final float n) {
        if (n > 0.5f) {
            return (n - 0.5f) * 2.0f;
        }
        if (n < 0.5f) {
            return -((0.5f - n) * 2.0f);
        }
        return 0.0f;
    }
    
    public void execute_Simulation() {
        if (Core.bDebug) {
            new ClimMngrDebug().SimulateDays(365, 5000);
        }
    }
    
    public void execute_Simulation(final int rainModOverride) {
        if (Core.bDebug) {
            final ClimMngrDebug climMngrDebug = new ClimMngrDebug();
            climMngrDebug.setRainModOverride(rainModOverride);
            climMngrDebug.SimulateDays(365, 5000);
        }
    }
    
    public void triggerKateBobIntroStorm(final int n, final int n2, final double n3, final float n4, final float n5, final float n6, final float n7) {
        this.triggerKateBobIntroStorm(n, n2, n3, n4, n5, n6, n7, null);
    }
    
    public void triggerKateBobIntroStorm(final int n, final int n2, final double n3, final float n4, final float kateBobStormProgress, final float n5, final float finalValue, final ClimateColorInfo cloudColor) {
        if (!GameClient.bClient) {
            this.stopWeatherAndThunder();
            if (this.weatherPeriod.startCreateModdedPeriod(true, n4, n5)) {
                this.weatherPeriod.setKateBobStormProgress(kateBobStormProgress);
                this.weatherPeriod.setKateBobStormCoords(n, n2);
                this.weatherPeriod.createAndAddStage(11, n3);
                this.weatherPeriod.createAndAddStage(2, n3 / 2.0);
                this.weatherPeriod.createAndAddStage(4, n3 / 4.0);
                this.weatherPeriod.endCreateModdedPeriod();
                if (cloudColor != null) {
                    this.weatherPeriod.setCloudColor(cloudColor);
                }
                else {
                    this.weatherPeriod.setCloudColor(this.weatherPeriod.getCloudColorBlueish());
                }
                IsoPuddles.getInstance().getPuddlesFloat(3).setFinalValue(finalValue);
                IsoPuddles.getInstance().getPuddlesFloat(1).setFinalValue(PZMath.clamp_01(finalValue * 1.2f));
            }
        }
    }
    
    public double getSimplexOffsetA() {
        return this.simplexOffsetA;
    }
    
    public double getSimplexOffsetB() {
        return this.simplexOffsetB;
    }
    
    public double getSimplexOffsetC() {
        return this.simplexOffsetC;
    }
    
    public double getSimplexOffsetD() {
        return this.simplexOffsetD;
    }
    
    public double getWorldAgeHours() {
        return this.worldAgeHours;
    }
    
    public ClimateValues getClimateValuesCopy() {
        return this.climateValues.getCopy();
    }
    
    public void CopyClimateValues(final ClimateValues climateValues) {
        this.climateValues.CopyValues(climateValues);
    }
    
    public ClimateForecaster getClimateForecaster() {
        return this.climateForecaster;
    }
    
    public ClimateHistory getClimateHistory() {
        return this.climateHistory;
    }
    
    public void CalculateWeatherFrontStrength(final int year, final int month, final int dayOfMonth, final AirFront airFront) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, dayOfMonth, 0, 0);
        gregorianCalendar.add(5, -3);
        if (this.climateValuesFronts == null) {
            this.climateValuesFronts = this.climateValues.getCopy();
        }
        final int type = airFront.type;
        for (int i = 0; i < 4; ++i) {
            this.climateValuesFronts.pollDate(gregorianCalendar);
            final float airFrontAirmass = this.climateValuesFronts.getAirFrontAirmass();
            if (((airFrontAirmass < 0.0f) ? -1 : 1) == type) {
                airFront.addDaySample(airFrontAirmass);
            }
            gregorianCalendar.add(5, 1);
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, airFront.getStrength()));
    }
    
    public static String getWindAngleString(final float n) {
        for (int i = 0; i < ClimateManager.windAngles.length; ++i) {
            if (n < ClimateManager.windAngles[i]) {
                return ClimateManager.windAngleStr[i];
            }
        }
        return ClimateManager.windAngleStr[ClimateManager.windAngleStr.length - 1];
    }
    
    public void sendInitialState(final UdpConnection udpConnection) throws IOException {
        if (!GameServer.bServer) {
            return;
        }
        if (this.writePacketContents(udpConnection, (byte)0)) {
            PacketTypes.PacketType.ClimateManagerPacket.send(udpConnection);
        }
        else {
            udpConnection.cancelPacket();
        }
    }
    
    static {
        ClimateManager.instance = new ClimateManager();
        ClimateManager.WINTER_IS_COMING = false;
        ClimateManager.THE_DESCENDING_FOG = false;
        ClimateManager.A_STORM_IS_COMING = false;
        ClimateManager.windNoiseOffset = 0.0;
        ClimateManager.windNoiseBase = 0.0;
        ClimateManager.windNoiseFinal = 0.0;
        ClimateManager.windTickFinal = 0.0;
        ClimateManager.windAngles = new float[] { 22.5f, 67.5f, 112.5f, 157.5f, 202.5f, 247.5f, 292.5f, 337.5f, 382.5f };
        ClimateManager.windAngleStr = new String[] { "SE", "S", "SW", "W", "NW", "N", "NE", "E", "SE" };
    }
    
    public static class ClimateFloat
    {
        protected float internalValue;
        protected float finalValue;
        protected boolean isOverride;
        protected float override;
        protected float interpolate;
        private boolean isModded;
        private float moddedValue;
        private float modInterpolate;
        private boolean isAdminOverride;
        private float adminValue;
        private float min;
        private float max;
        private int ID;
        private String name;
        
        public ClimateFloat() {
            this.isOverride = false;
            this.isModded = false;
            this.isAdminOverride = false;
            this.min = 0.0f;
            this.max = 1.0f;
        }
        
        public ClimateFloat init(final int id, final String name) {
            this.ID = id;
            this.name = name;
            return this;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public String getName() {
            return this.name;
        }
        
        public float getMin() {
            return this.min;
        }
        
        public float getMax() {
            return this.max;
        }
        
        public float getInternalValue() {
            return this.internalValue;
        }
        
        public float getOverride() {
            return this.override;
        }
        
        public float getOverrideInterpolate() {
            return this.interpolate;
        }
        
        public void setOverride(final float override, final float interpolate) {
            this.override = override;
            this.interpolate = interpolate;
            this.isOverride = true;
        }
        
        public void setEnableOverride(final boolean isOverride) {
            this.isOverride = isOverride;
        }
        
        public boolean isEnableOverride() {
            return this.isOverride;
        }
        
        public void setEnableAdmin(final boolean isAdminOverride) {
            this.isAdminOverride = isAdminOverride;
        }
        
        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }
        
        public void setAdminValue(final float n) {
            this.adminValue = ClimateManager.clamp(this.min, this.max, n);
        }
        
        public float getAdminValue() {
            return this.adminValue;
        }
        
        public void setEnableModded(final boolean isModded) {
            this.isModded = isModded;
        }
        
        public void setModdedValue(final float n) {
            this.moddedValue = ClimateManager.clamp(this.min, this.max, n);
        }
        
        public float getModdedValue() {
            return this.moddedValue;
        }
        
        public void setModdedInterpolate(final float n) {
            this.modInterpolate = ClimateManager.clamp01(n);
        }
        
        public void setFinalValue(final float finalValue) {
            this.finalValue = finalValue;
        }
        
        public float getFinalValue() {
            return this.finalValue;
        }
        
        private void calculate() {
            if (this.isAdminOverride && !GameClient.bClient) {
                this.finalValue = this.adminValue;
                return;
            }
            if (this.isModded && this.modInterpolate > 0.0f) {
                this.internalValue = ClimateManager.lerp(this.modInterpolate, this.internalValue, this.moddedValue);
            }
            if (this.isOverride && this.interpolate > 0.0f) {
                this.finalValue = ClimateManager.lerp(this.interpolate, this.internalValue, this.override);
            }
            else {
                this.finalValue = this.internalValue;
            }
        }
        
        private void writeAdmin(final ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
            byteBuffer.putFloat(this.adminValue);
        }
        
        private void readAdmin(final ByteBuffer byteBuffer) {
            this.isAdminOverride = (byteBuffer.get() == 1);
            this.adminValue = byteBuffer.getFloat();
        }
        
        private void saveAdmin(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(this.isAdminOverride);
            dataOutputStream.writeFloat(this.adminValue);
        }
        
        private void loadAdmin(final DataInputStream dataInputStream, final int n) throws IOException {
            this.isAdminOverride = dataInputStream.readBoolean();
            this.adminValue = dataInputStream.readFloat();
        }
    }
    
    public static class ClimateColor
    {
        protected ClimateColorInfo internalValue;
        protected ClimateColorInfo finalValue;
        protected boolean isOverride;
        protected ClimateColorInfo override;
        protected float interpolate;
        private boolean isModded;
        private ClimateColorInfo moddedValue;
        private float modInterpolate;
        private boolean isAdminOverride;
        private ClimateColorInfo adminValue;
        private int ID;
        private String name;
        
        public ClimateColor() {
            this.internalValue = new ClimateColorInfo();
            this.finalValue = new ClimateColorInfo();
            this.isOverride = false;
            this.override = new ClimateColorInfo();
            this.isModded = false;
            this.moddedValue = new ClimateColorInfo();
            this.isAdminOverride = false;
            this.adminValue = new ClimateColorInfo();
        }
        
        public ClimateColor init(final int id, final String name) {
            this.ID = id;
            this.name = name;
            return this;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public String getName() {
            return this.name;
        }
        
        public ClimateColorInfo getInternalValue() {
            return this.internalValue;
        }
        
        public ClimateColorInfo getOverride() {
            return this.override;
        }
        
        public float getOverrideInterpolate() {
            return this.interpolate;
        }
        
        public void setOverride(final ClimateColorInfo to, final float interpolate) {
            this.override.setTo(to);
            this.interpolate = interpolate;
            this.isOverride = true;
        }
        
        public void setOverride(final ByteBuffer byteBuffer, final float interpolate) {
            this.override.read(byteBuffer);
            this.interpolate = interpolate;
            this.isOverride = true;
        }
        
        public void setEnableOverride(final boolean isOverride) {
            this.isOverride = isOverride;
        }
        
        public boolean isEnableOverride() {
            return this.isOverride;
        }
        
        public void setEnableAdmin(final boolean isAdminOverride) {
            this.isAdminOverride = isAdminOverride;
        }
        
        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }
        
        public void setAdminValue(final float r, final float g, final float b, final float a, final float r2, final float g2, final float b2, final float a2) {
            this.adminValue.getExterior().r = r;
            this.adminValue.getExterior().g = g;
            this.adminValue.getExterior().b = b;
            this.adminValue.getExterior().a = a;
            this.adminValue.getInterior().r = r2;
            this.adminValue.getInterior().g = g2;
            this.adminValue.getInterior().b = b2;
            this.adminValue.getInterior().a = a2;
        }
        
        public void setAdminValueExterior(final float r, final float g, final float b, final float a) {
            this.adminValue.getExterior().r = r;
            this.adminValue.getExterior().g = g;
            this.adminValue.getExterior().b = b;
            this.adminValue.getExterior().a = a;
        }
        
        public void setAdminValueInterior(final float r, final float g, final float b, final float a) {
            this.adminValue.getInterior().r = r;
            this.adminValue.getInterior().g = g;
            this.adminValue.getInterior().b = b;
            this.adminValue.getInterior().a = a;
        }
        
        public void setAdminValue(final ClimateColorInfo to) {
            this.adminValue.setTo(to);
        }
        
        public ClimateColorInfo getAdminValue() {
            return this.adminValue;
        }
        
        public void setEnableModded(final boolean isModded) {
            this.isModded = isModded;
        }
        
        public void setModdedValue(final ClimateColorInfo to) {
            this.moddedValue.setTo(to);
        }
        
        public ClimateColorInfo getModdedValue() {
            return this.moddedValue;
        }
        
        public void setModdedInterpolate(final float n) {
            this.modInterpolate = ClimateManager.clamp01(n);
        }
        
        public void setFinalValue(final ClimateColorInfo to) {
            this.finalValue.setTo(to);
        }
        
        public ClimateColorInfo getFinalValue() {
            return this.finalValue;
        }
        
        private void calculate() {
            if (this.isAdminOverride && !GameClient.bClient) {
                this.finalValue.setTo(this.adminValue);
                return;
            }
            if (this.isModded && this.modInterpolate > 0.0f) {
                this.internalValue.interp(this.moddedValue, this.modInterpolate, this.internalValue);
            }
            if (this.isOverride && this.interpolate > 0.0f) {
                this.internalValue.interp(this.override, this.interpolate, this.finalValue);
            }
            else {
                this.finalValue.setTo(this.internalValue);
            }
        }
        
        private void writeAdmin(final ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
            this.adminValue.write(byteBuffer);
        }
        
        private void readAdmin(final ByteBuffer byteBuffer) {
            this.isAdminOverride = (byteBuffer.get() == 1);
            this.adminValue.read(byteBuffer);
        }
        
        private void saveAdmin(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(this.isAdminOverride);
            this.adminValue.save(dataOutputStream);
        }
        
        private void loadAdmin(final DataInputStream dataInputStream, final int n) throws IOException {
            this.isAdminOverride = dataInputStream.readBoolean();
            if (n < 143) {
                this.adminValue.getInterior().r = dataInputStream.readFloat();
                this.adminValue.getInterior().g = dataInputStream.readFloat();
                this.adminValue.getInterior().b = dataInputStream.readFloat();
                this.adminValue.getInterior().a = dataInputStream.readFloat();
                this.adminValue.getExterior().r = this.adminValue.getInterior().r;
                this.adminValue.getExterior().g = this.adminValue.getInterior().g;
                this.adminValue.getExterior().b = this.adminValue.getInterior().b;
                this.adminValue.getExterior().a = this.adminValue.getInterior().a;
            }
            else {
                this.adminValue.load(dataInputStream, n);
            }
        }
    }
    
    public static class ClimateBool
    {
        protected boolean internalValue;
        protected boolean finalValue;
        protected boolean isOverride;
        protected boolean override;
        private boolean isModded;
        private boolean moddedValue;
        private boolean isAdminOverride;
        private boolean adminValue;
        private int ID;
        private String name;
        
        public ClimateBool() {
            this.isModded = false;
            this.isAdminOverride = false;
        }
        
        public ClimateBool init(final int id, final String name) {
            this.ID = id;
            this.name = name;
            return this;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean getInternalValue() {
            return this.internalValue;
        }
        
        public boolean getOverride() {
            return this.override;
        }
        
        public void setOverride(final boolean override) {
            this.isOverride = true;
            this.override = override;
        }
        
        public void setEnableOverride(final boolean isOverride) {
            this.isOverride = isOverride;
        }
        
        public boolean isEnableOverride() {
            return this.isOverride;
        }
        
        public void setEnableAdmin(final boolean isAdminOverride) {
            this.isAdminOverride = isAdminOverride;
        }
        
        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }
        
        public void setAdminValue(final boolean adminValue) {
            this.adminValue = adminValue;
        }
        
        public boolean getAdminValue() {
            return this.adminValue;
        }
        
        public void setEnableModded(final boolean isModded) {
            this.isModded = isModded;
        }
        
        public void setModdedValue(final boolean moddedValue) {
            this.moddedValue = moddedValue;
        }
        
        public boolean getModdedValue() {
            return this.moddedValue;
        }
        
        public void setFinalValue(final boolean finalValue) {
            this.finalValue = finalValue;
        }
        
        private void calculate() {
            if (this.isAdminOverride && !GameClient.bClient) {
                this.finalValue = this.adminValue;
                return;
            }
            if (this.isModded) {
                this.finalValue = this.moddedValue;
                return;
            }
            this.finalValue = (this.isOverride ? this.override : this.internalValue);
        }
        
        private void writeAdmin(final ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
            byteBuffer.put((byte)(this.adminValue ? 1 : 0));
        }
        
        private void readAdmin(final ByteBuffer byteBuffer) {
            this.isAdminOverride = (byteBuffer.get() == 1);
            this.adminValue = (byteBuffer.get() == 1);
        }
        
        private void saveAdmin(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(this.isAdminOverride);
            dataOutputStream.writeBoolean(this.adminValue);
        }
        
        private void loadAdmin(final DataInputStream dataInputStream, final int n) throws IOException {
            this.isAdminOverride = dataInputStream.readBoolean();
            this.adminValue = dataInputStream.readBoolean();
        }
    }
    
    public static class AirFront
    {
        private float days;
        private float maxNoise;
        private float totalNoise;
        private int type;
        private float strength;
        private float tmpNoiseAbs;
        private float[] noiseCache;
        private float noiseCacheValue;
        private float frontWindAngleDegrees;
        
        public float getDays() {
            return this.days;
        }
        
        public float getMaxNoise() {
            return this.maxNoise;
        }
        
        public float getTotalNoise() {
            return this.totalNoise;
        }
        
        public int getType() {
            return this.type;
        }
        
        public float getStrength() {
            return this.strength;
        }
        
        public float getAngleDegrees() {
            return this.frontWindAngleDegrees;
        }
        
        public AirFront() {
            this.days = 0.0f;
            this.maxNoise = 0.0f;
            this.totalNoise = 0.0f;
            this.type = 0;
            this.strength = 0.0f;
            this.tmpNoiseAbs = 0.0f;
            this.noiseCache = new float[2];
            this.noiseCacheValue = 0.0f;
            this.frontWindAngleDegrees = 0.0f;
            this.reset();
        }
        
        public void setFrontType(final int type) {
            this.reset();
            this.type = type;
        }
        
        protected void setFrontWind(final float frontWindAngleDegrees) {
            this.frontWindAngleDegrees = frontWindAngleDegrees;
        }
        
        public void setStrength(final float strength) {
            this.strength = strength;
        }
        
        protected void reset() {
            this.days = 0.0f;
            this.maxNoise = 0.0f;
            this.totalNoise = 0.0f;
            this.type = 0;
            this.strength = 0.0f;
            this.frontWindAngleDegrees = 0.0f;
            for (int i = 0; i < this.noiseCache.length; ++i) {
                this.noiseCache[i] = -1.0f;
            }
        }
        
        public void save(final DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeFloat(this.days);
            dataOutputStream.writeFloat(this.maxNoise);
            dataOutputStream.writeFloat(this.totalNoise);
            dataOutputStream.writeInt(this.type);
            dataOutputStream.writeFloat(this.strength);
            dataOutputStream.writeFloat(this.frontWindAngleDegrees);
            dataOutputStream.writeInt(this.noiseCache.length);
            for (int i = 0; i < this.noiseCache.length; ++i) {
                dataOutputStream.writeFloat(this.noiseCache[i]);
            }
        }
        
        public void load(final DataInputStream dataInputStream) throws IOException {
            this.days = dataInputStream.readFloat();
            this.maxNoise = dataInputStream.readFloat();
            this.totalNoise = dataInputStream.readFloat();
            this.type = dataInputStream.readInt();
            this.strength = dataInputStream.readFloat();
            this.frontWindAngleDegrees = dataInputStream.readFloat();
            final int int1 = dataInputStream.readInt();
            for (int n = (int1 > this.noiseCache.length) ? int1 : this.noiseCache.length, i = 0; i < n; ++i) {
                if (i < int1) {
                    final float float1 = dataInputStream.readFloat();
                    if (i < this.noiseCache.length) {
                        this.noiseCache[i] = float1;
                    }
                }
                else if (i < this.noiseCache.length) {
                    this.noiseCache[i] = -1.0f;
                }
            }
        }
        
        public void addDaySample(final float a) {
            ++this.days;
            if ((this.type == 1 && a <= 0.0f) || (this.type == -1 && a >= 0.0f)) {
                this.strength = 0.0f;
                return;
            }
            this.tmpNoiseAbs = Math.abs(a);
            if (this.tmpNoiseAbs > this.maxNoise) {
                this.maxNoise = this.tmpNoiseAbs;
            }
            this.totalNoise += this.tmpNoiseAbs;
            this.noiseCacheValue = 0.0f;
            for (int i = this.noiseCache.length - 1; i >= 0; --i) {
                if (this.noiseCache[i] > this.noiseCacheValue) {
                    this.noiseCacheValue = this.noiseCache[i];
                }
                if (i < this.noiseCache.length - 1) {
                    this.noiseCache[i + 1] = this.noiseCache[i];
                }
            }
            this.noiseCache[0] = this.tmpNoiseAbs;
            if (this.tmpNoiseAbs > this.noiseCacheValue) {
                this.noiseCacheValue = this.tmpNoiseAbs;
            }
            this.strength = this.noiseCacheValue * 0.75f + this.maxNoise * 0.25f;
        }
        
        public void copyFrom(final AirFront airFront) {
            this.days = airFront.days;
            this.maxNoise = airFront.maxNoise;
            this.totalNoise = airFront.totalNoise;
            this.type = airFront.type;
            this.strength = airFront.strength;
            this.frontWindAngleDegrees = airFront.frontWindAngleDegrees;
        }
    }
    
    protected static class SeasonColor
    {
        public static int WARM;
        public static int NORMAL;
        public static int CLOUDY;
        public static int SUMMER;
        public static int FALL;
        public static int WINTER;
        public static int SPRING;
        private ClimateColorInfo finalCol;
        private ClimateColorInfo[] tempCol;
        private ClimateColorInfo[][] colors;
        private boolean ignoreNormal;
        
        public SeasonColor() {
            this.finalCol = new ClimateColorInfo();
            this.tempCol = new ClimateColorInfo[3];
            this.colors = new ClimateColorInfo[3][4];
            this.ignoreNormal = true;
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 4; ++j) {
                    this.colors[i][j] = new ClimateColorInfo();
                }
                this.tempCol[i] = new ClimateColorInfo();
            }
        }
        
        public void setIgnoreNormal(final boolean ignoreNormal) {
            this.ignoreNormal = ignoreNormal;
        }
        
        public ClimateColorInfo getColor(final int n, final int n2) {
            return this.colors[n][n2];
        }
        
        public void setColorInterior(final int n, final int n2, final float r, final float g, final float b, final float a) {
            this.colors[n][n2].getInterior().r = r;
            this.colors[n][n2].getInterior().g = g;
            this.colors[n][n2].getInterior().b = b;
            this.colors[n][n2].getInterior().a = a;
        }
        
        public void setColorExterior(final int n, final int n2, final float r, final float g, final float b, final float a) {
            this.colors[n][n2].getExterior().r = r;
            this.colors[n][n2].getExterior().g = g;
            this.colors[n][n2].getExterior().b = b;
            this.colors[n][n2].getExterior().a = a;
        }
        
        public ClimateColorInfo update(final float n, final float n2, final int n3, final int n4) {
            for (int i = 0; i < 3; ++i) {
                if (!this.ignoreNormal || i != 1) {
                    this.colors[i][n3].interp(this.colors[i][n4], n2, this.tempCol[i]);
                }
            }
            if (!this.ignoreNormal) {
                if (n < 0.5f) {
                    this.tempCol[SeasonColor.WARM].interp(this.tempCol[SeasonColor.NORMAL], n * 2.0f, this.finalCol);
                }
                else {
                    this.tempCol[SeasonColor.CLOUDY].interp(this.tempCol[SeasonColor.NORMAL], 1.0f - (n - 0.5f) * 2.0f, this.finalCol);
                }
            }
            else {
                this.tempCol[SeasonColor.WARM].interp(this.tempCol[SeasonColor.CLOUDY], n, this.finalCol);
            }
            return this.finalCol;
        }
        
        static {
            SeasonColor.WARM = 0;
            SeasonColor.NORMAL = 1;
            SeasonColor.CLOUDY = 2;
            SeasonColor.SUMMER = 0;
            SeasonColor.FALL = 1;
            SeasonColor.WINTER = 2;
            SeasonColor.SPRING = 3;
        }
    }
    
    public static class DayInfo
    {
        public int day;
        public int month;
        public int year;
        public int hour;
        public int minutes;
        public long dateValue;
        public GregorianCalendar calendar;
        public ErosionSeason season;
        
        public void set(final int n, final int n2, final int n3) {
            this.calendar = new GregorianCalendar(n3, n2, n, 0, 0);
            this.dateValue = this.calendar.getTime().getTime();
            this.day = n;
            this.month = n2;
            this.year = n3;
        }
        
        public int getDay() {
            return this.day;
        }
        
        public int getMonth() {
            return this.month;
        }
        
        public int getYear() {
            return this.year;
        }
        
        public int getHour() {
            return this.hour;
        }
        
        public int getMinutes() {
            return this.minutes;
        }
        
        public long getDateValue() {
            return this.dateValue;
        }
        
        public ErosionSeason getSeason() {
            return this.season;
        }
    }
    
    public enum ClimateNetAuth
    {
        Denied, 
        ClientOnly, 
        ServerOnly, 
        ClientAndServer;
        
        private static /* synthetic */ ClimateNetAuth[] $values() {
            return new ClimateNetAuth[] { ClimateNetAuth.Denied, ClimateNetAuth.ClientOnly, ClimateNetAuth.ServerOnly, ClimateNetAuth.ClientAndServer };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static class ClimateNetInfo
    {
        public boolean IsStopWeather;
        public boolean IsTrigger;
        public boolean IsGenerate;
        public float TriggerDuration;
        public boolean TriggerStorm;
        public boolean TriggerTropical;
        public boolean TriggerBlizzard;
        public float GenerateStrength;
        public int GenerateFront;
        
        private ClimateNetInfo() {
            this.IsStopWeather = false;
            this.IsTrigger = false;
            this.IsGenerate = false;
            this.TriggerDuration = 0.0f;
            this.TriggerStorm = false;
            this.TriggerTropical = false;
            this.TriggerBlizzard = false;
            this.GenerateStrength = 0.0f;
            this.GenerateFront = 0;
        }
        
        private void reset() {
            this.IsStopWeather = false;
            this.IsTrigger = false;
            this.IsGenerate = false;
            this.TriggerDuration = 0.0f;
            this.TriggerStorm = false;
            this.TriggerTropical = false;
            this.TriggerBlizzard = false;
            this.GenerateStrength = 0.0f;
            this.GenerateFront = 0;
        }
    }
}
