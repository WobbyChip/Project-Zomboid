// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.SandboxOptions;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import zombie.debug.DebugLog;
import java.util.Random;
import zombie.GameTime;

public class ClimateValues
{
    private double simplexOffsetA;
    private double simplexOffsetB;
    private double simplexOffsetC;
    private double simplexOffsetD;
    private ClimateManager clim;
    private GameTime gt;
    private float time;
    private float dawn;
    private float dusk;
    private float noon;
    private float dayMeanTemperature;
    private double airMassNoiseFrequencyMod;
    private float noiseAirmass;
    private float airMassTemperature;
    private float baseTemperature;
    private float dayLightLagged;
    private float nightLagged;
    private float temperature;
    private boolean temperatureIsSnow;
    private float humidity;
    private float windIntensity;
    private float windAngleIntensity;
    private float windAngleDegrees;
    private float nightStrength;
    private float dayLightStrength;
    private float ambient;
    private float desaturation;
    private float dayLightStrengthBase;
    private float lerpNight;
    private float cloudyT;
    private float cloudIntensity;
    private float airFrontAirmass;
    private boolean dayDoFog;
    private float dayFogStrength;
    private float dayFogDuration;
    private ClimateManager.DayInfo testCurrentDay;
    private ClimateManager.DayInfo testNextDay;
    private double cacheWorldAgeHours;
    private int cacheYear;
    private int cacheMonth;
    private int cacheDay;
    private Random seededRandom;
    
    public ClimateValues(final ClimateManager clim) {
        this.simplexOffsetA = 0.0;
        this.simplexOffsetB = 0.0;
        this.simplexOffsetC = 0.0;
        this.simplexOffsetD = 0.0;
        this.time = 0.0f;
        this.dawn = 0.0f;
        this.dusk = 0.0f;
        this.noon = 0.0f;
        this.dayMeanTemperature = 0.0f;
        this.airMassNoiseFrequencyMod = 0.0;
        this.noiseAirmass = 0.0f;
        this.airMassTemperature = 0.0f;
        this.baseTemperature = 0.0f;
        this.dayLightLagged = 0.0f;
        this.nightLagged = 0.0f;
        this.temperature = 0.0f;
        this.temperatureIsSnow = false;
        this.humidity = 0.0f;
        this.windIntensity = 0.0f;
        this.windAngleIntensity = 0.0f;
        this.windAngleDegrees = 0.0f;
        this.nightStrength = 0.0f;
        this.dayLightStrength = 0.0f;
        this.ambient = 0.0f;
        this.desaturation = 0.0f;
        this.dayLightStrengthBase = 0.0f;
        this.lerpNight = 0.0f;
        this.cloudyT = 0.0f;
        this.cloudIntensity = 0.0f;
        this.airFrontAirmass = 0.0f;
        this.dayDoFog = false;
        this.dayFogStrength = 0.0f;
        this.dayFogDuration = 0.0f;
        this.cacheWorldAgeHours = 0.0;
        this.simplexOffsetA = clim.getSimplexOffsetA();
        this.simplexOffsetB = clim.getSimplexOffsetB();
        this.simplexOffsetC = clim.getSimplexOffsetC();
        this.simplexOffsetD = clim.getSimplexOffsetD();
        this.clim = clim;
        this.gt = GameTime.getInstance();
        this.seededRandom = new Random(1984L);
    }
    
    public ClimateValues getCopy() {
        final ClimateValues climateValues = new ClimateValues(this.clim);
        this.CopyValues(climateValues);
        return climateValues;
    }
    
    public void CopyValues(final ClimateValues climateValues) {
        if (climateValues != this) {
            climateValues.time = this.time;
            climateValues.dawn = this.dawn;
            climateValues.dusk = this.dusk;
            climateValues.noon = this.noon;
            climateValues.dayMeanTemperature = this.dayMeanTemperature;
            climateValues.airMassNoiseFrequencyMod = this.airMassNoiseFrequencyMod;
            climateValues.noiseAirmass = this.noiseAirmass;
            climateValues.airMassTemperature = this.airMassTemperature;
            climateValues.baseTemperature = this.baseTemperature;
            climateValues.dayLightLagged = this.dayLightLagged;
            climateValues.nightLagged = this.nightLagged;
            climateValues.temperature = this.temperature;
            climateValues.temperatureIsSnow = this.temperatureIsSnow;
            climateValues.humidity = this.humidity;
            climateValues.windIntensity = this.windIntensity;
            climateValues.windAngleIntensity = this.windAngleIntensity;
            climateValues.windAngleDegrees = this.windAngleDegrees;
            climateValues.nightStrength = this.nightStrength;
            climateValues.dayLightStrength = this.dayLightStrength;
            climateValues.ambient = this.ambient;
            climateValues.desaturation = this.desaturation;
            climateValues.dayLightStrengthBase = this.dayLightStrengthBase;
            climateValues.lerpNight = this.lerpNight;
            climateValues.cloudyT = this.cloudyT;
            climateValues.cloudIntensity = this.cloudIntensity;
            climateValues.airFrontAirmass = this.airFrontAirmass;
            climateValues.dayDoFog = this.dayDoFog;
            climateValues.dayFogStrength = this.dayFogStrength;
            climateValues.dayFogDuration = this.dayFogDuration;
            climateValues.cacheWorldAgeHours = this.cacheWorldAgeHours;
            climateValues.cacheYear = this.cacheYear;
            climateValues.cacheMonth = this.cacheMonth;
            climateValues.cacheDay = this.cacheDay;
        }
    }
    
    public void print() {
        DebugLog.log("==================================================");
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.gt.getTimeOfDay()));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, this.gt.getWorldAgeHours()));
        DebugLog.log("--------------------------------------------------");
        if (this.testCurrentDay == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new SimpleDateFormat("yyyy MM dd").format(new GregorianCalendar(this.cacheYear, this.cacheMonth, this.cacheDay).getTime())));
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new SimpleDateFormat("yyyy MM dd").format(this.testCurrentDay.calendar.getTime())));
        }
        DebugLog.log("--------------------------------------------------");
        DebugLog.log(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, this.cacheWorldAgeHours));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.time));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.dawn));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.dusk));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.noon));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.dayMeanTemperature));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, this.airMassNoiseFrequencyMod));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.noiseAirmass));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.airMassTemperature));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.baseTemperature));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.dayLightLagged));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.nightLagged));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.temperature));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.temperatureIsSnow));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.humidity));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.windIntensity));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.windAngleIntensity));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.windAngleDegrees));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.nightStrength));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.dayLightStrength));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.ambient));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.desaturation));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.dayLightStrengthBase));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.lerpNight));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.cloudyT));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.cloudIntensity));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.airFrontAirmass));
    }
    
    public void pollDate(final int n, final int n2, final int n3) {
        this.pollDate(n, n2, n3, 0, 0);
    }
    
    public void pollDate(final int n, final int n2, final int n3, final int n4) {
        this.pollDate(n, n2, n3, n4, 0);
    }
    
    public void pollDate(final int year, final int month, final int dayOfMonth, final int hourOfDay, final int minute) {
        this.pollDate(new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute));
    }
    
    public void pollDate(final GregorianCalendar gregorianCalendar) {
        if (this.testCurrentDay == null) {
            this.testCurrentDay = new ClimateManager.DayInfo();
        }
        if (this.testNextDay == null) {
            this.testNextDay = new ClimateManager.DayInfo();
        }
        final double worldAgeHours = this.gt.getWorldAgeHours();
        this.clim.setDayInfo(this.testCurrentDay, gregorianCalendar.get(5), gregorianCalendar.get(2), gregorianCalendar.get(1), 0);
        this.clim.setDayInfo(this.testNextDay, gregorianCalendar.get(5), gregorianCalendar.get(2), gregorianCalendar.get(1), 1);
        this.updateValues(worldAgeHours + ChronoUnit.MINUTES.between(new GregorianCalendar(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.gt.getHour(), this.gt.getMinutes()).toInstant(), gregorianCalendar.toInstant()) / 60.0, gregorianCalendar.get(11) + gregorianCalendar.get(12) / 60.0f, this.testCurrentDay, this.testNextDay);
    }
    
    protected void updateValues(final double n, final float time, final ClimateManager.DayInfo dayInfo, final ClimateManager.DayInfo dayInfo2) {
        if (dayInfo.year != this.cacheYear || dayInfo.month != this.cacheMonth || dayInfo.day != this.cacheDay) {
            this.seededRandom.setSeed((dayInfo.year - 1990) * 100000 + (long)(dayInfo.month * dayInfo.day * 1234) + (dayInfo.year - 1990) * dayInfo.month * 10000 + ((int)this.clim.getSimplexOffsetD() - (int)this.clim.getSimplexOffsetC()) * dayInfo.day);
            this.dayFogStrength = 0.0f;
            this.dayDoFog = false;
            this.dayFogDuration = 0.0f;
            final float n2 = (float)this.seededRandom.nextInt(1000);
            this.dayDoFog = (n2 < 200.0f);
            if (this.dayDoFog) {
                this.dayFogDuration = 4.0f;
                if (n2 < 25.0f) {
                    this.dayFogStrength = 1.0f;
                    this.dayFogDuration += 2.0f;
                }
                else {
                    this.dayFogStrength = this.seededRandom.nextFloat();
                }
                final float n3 = dayInfo.season.getDayMeanTemperature() + (float)SimplexNoise.noise(this.simplexOffsetA, (n + 12.0 - 48.0) / this.clim.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier())) * 8.0f;
                final float nextFloat = this.seededRandom.nextFloat();
                if (n3 < 0.0f) {
                    this.dayFogDuration += 5.0f * this.dayFogStrength;
                    this.dayFogDuration += 8.0f * nextFloat;
                }
                else if (n3 < 10.0f) {
                    this.dayFogDuration += 2.5f * this.dayFogStrength;
                    this.dayFogDuration += 5.0f * nextFloat;
                }
                else if (n3 < 20.0f) {
                    this.dayFogDuration += 1.5f * this.dayFogStrength;
                    this.dayFogDuration += 2.5f * nextFloat;
                }
                else {
                    this.dayFogDuration += 1.0f * this.dayFogStrength;
                    this.dayFogDuration += 1.0f * nextFloat;
                }
                if (this.dayFogDuration > 24.0f - dayInfo.season.getDawn()) {
                    this.dayFogDuration = 24.0f - dayInfo.season.getDawn() - 1.0f;
                }
            }
        }
        this.cacheWorldAgeHours = n;
        this.cacheYear = dayInfo.year;
        this.cacheMonth = dayInfo.month;
        this.cacheDay = dayInfo.day;
        this.time = time;
        this.dawn = dayInfo.season.getDawn();
        this.dusk = dayInfo.season.getDusk();
        this.noon = dayInfo.season.getDayHighNoon();
        this.dayMeanTemperature = dayInfo.season.getDayMeanTemperature();
        final float n4 = time / 24.0f;
        final ClimateManager clim = this.clim;
        final float lerp = ClimateManager.lerp(n4, dayInfo.season.getCurDayPercent(), dayInfo2.season.getCurDayPercent());
        this.airMassNoiseFrequencyMod = this.clim.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
        this.noiseAirmass = (float)SimplexNoise.noise(this.simplexOffsetA, n / this.airMassNoiseFrequencyMod);
        final float n5 = (float)SimplexNoise.noise(this.simplexOffsetC, n / this.airMassNoiseFrequencyMod);
        this.airMassTemperature = (float)SimplexNoise.noise(this.simplexOffsetA, (n - 48.0) / this.airMassNoiseFrequencyMod);
        this.airFrontAirmass = (float)SimplexNoise.noise(this.simplexOffsetA, (Math.floor(n) + 12.0) / this.airMassNoiseFrequencyMod);
        final ClimateManager clim2 = this.clim;
        final float clerp = ClimateManager.clerp(n4, dayInfo.season.getDayTemperature(), dayInfo2.season.getDayTemperature());
        final ClimateManager clim3 = this.clim;
        final float clerp2 = ClimateManager.clerp(n4, dayInfo.season.getDayMeanTemperature(), dayInfo2.season.getDayMeanTemperature());
        final boolean b = clerp < clerp2;
        this.baseTemperature = clerp2 + this.airMassTemperature * 8.0f;
        final float n6 = 4.0f;
        float n7 = this.dusk + n6;
        if (n7 >= 24.0f) {
            n7 -= 24.0f;
        }
        this.dayLightLagged = this.clim.getTimeLerpHours(time, this.dawn + n6, n7, true);
        final float n8 = 5.0f * (1.0f - this.dayLightLagged);
        this.nightLagged = this.clim.getTimeLerpHours(time, n7, this.dawn + n6, true);
        this.temperature = this.baseTemperature + 1.0f - (n8 + 5.0f * this.nightLagged);
        Label_0955: {
            if (this.temperature >= 0.0f) {
                final ClimateManager clim4 = this.clim;
                if (!ClimateManager.WINTER_IS_COMING) {
                    this.temperatureIsSnow = false;
                    break Label_0955;
                }
            }
            this.temperatureIsSnow = true;
        }
        final float n9 = (45.0f - this.temperature) / 90.0f;
        final ClimateManager clim5 = this.clim;
        this.humidity = (1.0f + n5) * 0.5f * ClimateManager.clamp01(1.0f - n9);
        this.windIntensity = ((float)SimplexNoise.noise(n / 40.0, this.simplexOffsetA) + 1.0f) * 0.5f * ((1.0f - (this.airMassTemperature + 1.0f) * 0.5f) * (1.0f - lerp * 0.4f)) * 0.65f;
        this.windAngleIntensity = (float)SimplexNoise.noise(n / 80.0, this.simplexOffsetB);
        this.windAngleDegrees = 360.0f * (((float)SimplexNoise.noise(n / 40.0, this.simplexOffsetD) + 1.0f) * 0.5f);
        this.lerpNight = this.clim.getTimeLerpHours(time, this.dusk, this.dawn, true);
        final ClimateManager clim6 = this.clim;
        this.lerpNight = ClimateManager.clamp(0.0f, 1.0f, this.lerpNight * 2.0f);
        this.nightStrength = this.lerpNight;
        this.dayLightStrengthBase = 1.0f - this.nightStrength;
        this.dayLightStrengthBase *= 1.0f - 0.15f * lerp - 0.2f * this.windIntensity;
        this.dayLightStrength = this.dayLightStrengthBase;
        this.ambient = this.dayLightStrength;
        final float n10 = (1.0f - dayInfo.season.getCurDayPercent()) * 0.4f;
        final float n11 = (1.0f - dayInfo2.season.getCurDayPercent()) * 0.4f;
        final ClimateManager clim7 = this.clim;
        this.desaturation = ClimateManager.lerp(n4, n10, n11);
        final float n12 = 1.0f;
        final ClimateManager clim8 = this.clim;
        this.cloudyT = n12 - ClimateManager.clamp01((this.airMassTemperature + 0.8f) * 0.625f);
        this.cloudyT *= 0.8f;
        final ClimateManager clim9 = this.clim;
        this.cloudyT = ClimateManager.clamp01(this.cloudyT + this.windIntensity);
        final ClimateManager clim10 = this.clim;
        this.cloudIntensity = ClimateManager.clamp01(this.windIntensity * 2.0f);
        this.cloudIntensity -= this.cloudIntensity * 0.5f * this.nightStrength;
    }
    
    public float getTime() {
        return this.time;
    }
    
    public float getDawn() {
        return this.dawn;
    }
    
    public float getDusk() {
        return this.dusk;
    }
    
    public float getNoon() {
        return this.noon;
    }
    
    public double getAirMassNoiseFrequencyMod() {
        return this.airMassNoiseFrequencyMod;
    }
    
    public float getNoiseAirmass() {
        return this.noiseAirmass;
    }
    
    public float getAirMassTemperature() {
        return this.airMassTemperature;
    }
    
    public float getBaseTemperature() {
        return this.baseTemperature;
    }
    
    public float getDayLightLagged() {
        return this.dayLightLagged;
    }
    
    public float getNightLagged() {
        return this.nightLagged;
    }
    
    public float getTemperature() {
        return this.temperature;
    }
    
    public boolean isTemperatureIsSnow() {
        return this.temperatureIsSnow;
    }
    
    public float getHumidity() {
        return this.humidity;
    }
    
    public float getWindIntensity() {
        return this.windIntensity;
    }
    
    public float getWindAngleIntensity() {
        return this.windAngleIntensity;
    }
    
    public float getWindAngleDegrees() {
        return this.windAngleDegrees;
    }
    
    public float getNightStrength() {
        return this.nightStrength;
    }
    
    public float getDayLightStrength() {
        return this.dayLightStrength;
    }
    
    public float getAmbient() {
        return this.ambient;
    }
    
    public float getDesaturation() {
        return this.desaturation;
    }
    
    public float getDayLightStrengthBase() {
        return this.dayLightStrengthBase;
    }
    
    public float getLerpNight() {
        return this.lerpNight;
    }
    
    public float getCloudyT() {
        return this.cloudyT;
    }
    
    public float getCloudIntensity() {
        return this.cloudIntensity;
    }
    
    public float getAirFrontAirmass() {
        return this.airFrontAirmass;
    }
    
    public double getCacheWorldAgeHours() {
        return this.cacheWorldAgeHours;
    }
    
    public int getCacheYear() {
        return this.cacheYear;
    }
    
    public int getCacheMonth() {
        return this.cacheMonth;
    }
    
    public int getCacheDay() {
        return this.cacheDay;
    }
    
    public float getDayMeanTemperature() {
        return this.dayMeanTemperature;
    }
    
    public boolean isDayDoFog() {
        return this.dayDoFog;
    }
    
    public float getDayFogStrength() {
        return this.dayFogStrength;
    }
    
    public float getDayFogDuration() {
        return this.dayFogDuration;
    }
}
