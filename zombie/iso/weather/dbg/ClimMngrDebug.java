// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.dbg;

import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.Date;
import java.text.SimpleDateFormat;
import zombie.network.GameClient;
import java.util.Iterator;
import zombie.iso.weather.SimplexNoise;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.GameTime;
import zombie.debug.DebugLog;
import zombie.iso.weather.ThunderStorm;
import java.io.FileWriter;
import zombie.erosion.season.ErosionSeason;
import java.util.ArrayList;
import zombie.iso.weather.WeatherPeriod;
import java.util.GregorianCalendar;
import zombie.iso.weather.ClimateManager;

public class ClimMngrDebug extends ClimateManager
{
    private GregorianCalendar calendar;
    private double worldAgeHours;
    private double worldAgeHoursStart;
    private double weatherPeriodTime;
    private double simplexOffsetA;
    private AirFront currentFront;
    private WeatherPeriod weatherPeriod;
    private boolean tickIsDayChange;
    public ArrayList<RunInfo> runs;
    private RunInfo currentRun;
    private ErosionSeason season;
    private int TotalDaysPeriodIndexMod;
    private boolean DoOverrideSandboxRainMod;
    private int SandboxRainModOverride;
    private int durDays;
    private static final int WEATHER_NORMAL = 0;
    private static final int WEATHER_STORM = 1;
    private static final int WEATHER_TROPICAL = 2;
    private static final int WEATHER_BLIZZARD = 3;
    private FileWriter writer;
    
    public ClimMngrDebug() {
        this.worldAgeHours = 0.0;
        this.worldAgeHoursStart = 0.0;
        this.weatherPeriodTime = 0.0;
        this.tickIsDayChange = false;
        this.runs = new ArrayList<RunInfo>();
        this.TotalDaysPeriodIndexMod = 5;
        this.DoOverrideSandboxRainMod = false;
        this.SandboxRainModOverride = 3;
        this.durDays = 0;
        this.currentFront = new AirFront();
        (this.weatherPeriod = new WeatherPeriod(this, null)).setPrintStuff(false);
    }
    
    public void setRainModOverride(final int sandboxRainModOverride) {
        this.DoOverrideSandboxRainMod = true;
        this.SandboxRainModOverride = sandboxRainModOverride;
    }
    
    public void unsetRainModOverride() {
        this.DoOverrideSandboxRainMod = false;
        this.SandboxRainModOverride = 3;
    }
    
    public void SimulateDays(final int n, final int n2) {
        this.durDays = n;
        DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n2, n));
        final int month = 0;
        final int dayOfMonth = 0;
        DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, GameTime.instance.getYear(), month, dayOfMonth));
        for (int i = 0; i < n2; ++i) {
            this.calendar = new GregorianCalendar(GameTime.instance.getYear(), month, dayOfMonth, 0, 0);
            (this.season = ClimateManager.getInstance().getSeason().clone()).init(this.season.getLat(), this.season.getTempMax(), this.season.getTempMin(), this.season.getTempDiff(), this.season.getSeasonLag(), this.season.getHighNoon(), Rand.Next(0, 255), Rand.Next(0, 255), Rand.Next(0, 255));
            this.simplexOffsetA = Rand.Next(0, 8000);
            this.worldAgeHours = 250.0;
            this.weatherPeriodTime = this.worldAgeHours;
            this.worldAgeHoursStart = this.worldAgeHours;
            this.currentFront.setFrontType(((float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier())) < 0.0f) ? -1 : 1);
            this.weatherPeriod.stopWeatherPeriod();
            double n3 = this.worldAgeHours + 24.0;
            final int n4 = n * 24;
            this.currentRun = new RunInfo();
            this.currentRun.durationDays = n;
            this.currentRun.durationHours = n4;
            this.currentRun.seedA = this.simplexOffsetA;
            this.runs.add(this.currentRun);
            for (int j = 0; j < n4; ++j) {
                this.tickIsDayChange = false;
                ++this.worldAgeHours;
                if (this.worldAgeHours >= n3) {
                    this.tickIsDayChange = true;
                    n3 += 24.0;
                    this.calendar.add(5, 1);
                    this.season.setDay(this.calendar.get(5), this.calendar.get(2), this.calendar.get(1));
                }
                this.update_sim();
            }
        }
        this.saveData();
    }
    
    private void update_sim() {
        final double airMassNoiseFrequencyMod = this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
        final int frontType = ((float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / airMassNoiseFrequencyMod) < 0.0f) ? -1 : 1;
        if (this.currentFront.getType() != frontType) {
            if (this.worldAgeHours > this.weatherPeriodTime) {
                this.weatherPeriod.initSimulationDebug(this.currentFront, this.worldAgeHours);
                this.recordAndCloseWeatherPeriod();
            }
            this.currentFront.setFrontType(frontType);
        }
        if (!ClimMngrDebug.WINTER_IS_COMING && !ClimMngrDebug.THE_DESCENDING_FOG && this.worldAgeHours >= this.worldAgeHoursStart + 72.0 && this.worldAgeHours <= this.worldAgeHoursStart + 96.0 && !this.weatherPeriod.isRunning() && this.worldAgeHours > this.weatherPeriodTime && Rand.Next(0, 1000) < 50) {
            this.triggerCustomWeatherStage(3, 10.0f);
        }
        if (this.tickIsDayChange) {
            final float n = (float)SimplexNoise.noise(this.simplexOffsetA, (Math.floor(this.worldAgeHours) + 12.0) / airMassNoiseFrequencyMod);
            if (((n < 0.0f) ? -1 : 1) == this.currentFront.getType()) {
                this.currentFront.addDaySample(n);
            }
        }
    }
    
    private void recordAndCloseWeatherPeriod() {
        if (this.weatherPeriod.isRunning()) {
            if (this.worldAgeHours - this.weatherPeriodTime > 0.0) {
                this.currentRun.addRecord(this.worldAgeHours - this.weatherPeriodTime);
            }
            this.weatherPeriodTime = this.worldAgeHours + Math.ceil(this.weatherPeriod.getDuration());
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            for (final WeatherPeriod.WeatherStage weatherStage : this.weatherPeriod.getWeatherStages()) {
                if (weatherStage.getStageID() == 3) {
                    b = true;
                }
                if (weatherStage.getStageID() == 8) {
                    b2 = true;
                }
                if (weatherStage.getStageID() == 7) {
                    b3 = true;
                }
            }
            this.currentRun.addRecord(this.currentFront.getType(), this.weatherPeriod.getDuration(), this.weatherPeriod.getFrontCache().getStrength(), b, b2, b3);
        }
        this.weatherPeriod.stopWeatherPeriod();
    }
    
    @Override
    public boolean triggerCustomWeatherStage(final int n, final float n2) {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            final AirFront airFront = new AirFront();
            airFront.setFrontType(1);
            airFront.setStrength(0.95f);
            this.weatherPeriod.initSimulationDebug(airFront, this.worldAgeHours, n, n2);
            this.recordAndCloseWeatherPeriod();
            return true;
        }
        return false;
    }
    
    @Override
    protected double getAirMassNoiseFrequencyMod(final int n) {
        if (this.DoOverrideSandboxRainMod) {
            return super.getAirMassNoiseFrequencyMod(this.SandboxRainModOverride);
        }
        return super.getAirMassNoiseFrequencyMod(n);
    }
    
    @Override
    protected float getRainTimeMultiplierMod(final int n) {
        if (this.DoOverrideSandboxRainMod) {
            return super.getRainTimeMultiplierMod(this.SandboxRainModOverride);
        }
        return super.getRainTimeMultiplierMod(n);
    }
    
    @Override
    public ErosionSeason getSeason() {
        return this.season;
    }
    
    @Override
    public float getDayMeanTemperature() {
        return this.season.getDayMeanTemperature();
    }
    
    @Override
    public void resetOverrides() {
    }
    
    private RunInfo calculateTotal() {
        final RunInfo runInfo = new RunInfo();
        runInfo.totalDaysPeriod = new int[50];
        for (final RunInfo runInfo2 : this.runs) {
            if (runInfo2.totalPeriodDuration < runInfo.mostDryPeriod) {
                runInfo.mostDryPeriod = runInfo2.totalPeriodDuration;
            }
            if (runInfo2.totalPeriodDuration > runInfo.mostWetPeriod) {
                runInfo.mostWetPeriod = runInfo2.totalPeriodDuration;
            }
            final RunInfo runInfo3 = runInfo;
            runInfo3.totalPeriodDuration += runInfo2.totalPeriodDuration;
            if (runInfo2.longestPeriod > runInfo.longestPeriod) {
                runInfo.longestPeriod = runInfo2.longestPeriod;
            }
            if (runInfo2.shortestPeriod < runInfo.shortestPeriod) {
                runInfo.shortestPeriod = runInfo2.shortestPeriod;
            }
            final RunInfo runInfo4 = runInfo;
            runInfo4.totalPeriods += runInfo2.totalPeriods;
            final RunInfo runInfo5 = runInfo;
            runInfo5.averagePeriod += runInfo2.averagePeriod;
            if (runInfo2.longestEmpty > runInfo.longestEmpty) {
                runInfo.longestEmpty = runInfo2.longestEmpty;
            }
            if (runInfo2.shortestEmpty < runInfo.shortestEmpty) {
                runInfo.shortestEmpty = runInfo2.shortestEmpty;
            }
            final RunInfo runInfo6 = runInfo;
            runInfo6.totalEmpty += runInfo2.totalEmpty;
            final RunInfo runInfo7 = runInfo;
            runInfo7.averageEmpty += runInfo2.averageEmpty;
            if (runInfo2.highestStrength > runInfo.highestStrength) {
                runInfo.highestStrength = runInfo2.highestStrength;
            }
            if (runInfo2.lowestStrength < runInfo.lowestStrength) {
                runInfo.lowestStrength = runInfo2.lowestStrength;
            }
            final RunInfo runInfo8 = runInfo;
            runInfo8.averageStrength += runInfo2.averageStrength;
            if (runInfo2.highestWarmStrength > runInfo.highestWarmStrength) {
                runInfo.highestWarmStrength = runInfo2.highestWarmStrength;
            }
            if (runInfo2.lowestWarmStrength < runInfo.lowestWarmStrength) {
                runInfo.lowestWarmStrength = runInfo2.lowestWarmStrength;
            }
            final RunInfo runInfo9 = runInfo;
            runInfo9.averageWarmStrength += runInfo2.averageWarmStrength;
            if (runInfo2.highestColdStrength > runInfo.highestColdStrength) {
                runInfo.highestColdStrength = runInfo2.highestColdStrength;
            }
            if (runInfo2.lowestColdStrength < runInfo.lowestColdStrength) {
                runInfo.lowestColdStrength = runInfo2.lowestColdStrength;
            }
            final RunInfo runInfo10 = runInfo;
            runInfo10.averageColdStrength += runInfo2.averageColdStrength;
            final RunInfo runInfo11 = runInfo;
            runInfo11.countNormalWarm += runInfo2.countNormalWarm;
            final RunInfo runInfo12 = runInfo;
            runInfo12.countNormalCold += runInfo2.countNormalCold;
            final RunInfo runInfo13 = runInfo;
            runInfo13.countStorm += runInfo2.countStorm;
            final RunInfo runInfo14 = runInfo;
            runInfo14.countTropical += runInfo2.countTropical;
            final RunInfo runInfo15 = runInfo;
            runInfo15.countBlizzard += runInfo2.countBlizzard;
            for (int i = 0; i < runInfo2.dayCountPeriod.length; ++i) {
                final int[] dayCountPeriod = runInfo.dayCountPeriod;
                final int n = i;
                dayCountPeriod[n] += runInfo2.dayCountPeriod[i];
            }
            for (int j = 0; j < runInfo2.dayCountWarmPeriod.length; ++j) {
                final int[] dayCountWarmPeriod = runInfo.dayCountWarmPeriod;
                final int n2 = j;
                dayCountWarmPeriod[n2] += runInfo2.dayCountWarmPeriod[j];
            }
            for (int k = 0; k < runInfo2.dayCountColdPeriod.length; ++k) {
                final int[] dayCountColdPeriod = runInfo.dayCountColdPeriod;
                final int n3 = k;
                dayCountColdPeriod[n3] += runInfo2.dayCountColdPeriod[k];
            }
            for (int l = 0; l < runInfo2.dayCountEmpty.length; ++l) {
                final int[] dayCountEmpty = runInfo.dayCountEmpty;
                final int n4 = l;
                dayCountEmpty[n4] += runInfo2.dayCountEmpty[l];
            }
            for (int index = 0; index < runInfo2.exceedingPeriods.size(); ++index) {
                runInfo.exceedingPeriods.add(runInfo2.exceedingPeriods.get(index));
            }
            for (int index2 = 0; index2 < runInfo2.exceedingEmpties.size(); ++index2) {
                runInfo.exceedingEmpties.add(runInfo2.exceedingEmpties.get(index2));
            }
            final int n5 = (int)(runInfo2.totalPeriodDuration / (this.TotalDaysPeriodIndexMod * 24));
            if (n5 < runInfo.totalDaysPeriod.length) {
                final int[] totalDaysPeriod = runInfo.totalDaysPeriod;
                final int n6 = n5;
                ++totalDaysPeriod[n6];
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n5 * this.TotalDaysPeriodIndexMod));
            }
        }
        if (this.runs.size() > 0) {
            final int size = this.runs.size();
            final RunInfo runInfo16 = runInfo;
            runInfo16.totalPeriodDuration /= size;
            final RunInfo runInfo17 = runInfo;
            runInfo17.averagePeriod /= size;
            final RunInfo runInfo18 = runInfo;
            runInfo18.averageEmpty /= size;
            final RunInfo runInfo19 = runInfo;
            runInfo19.averageStrength /= size;
            final RunInfo runInfo20 = runInfo;
            runInfo20.averageWarmStrength /= size;
            final RunInfo runInfo21 = runInfo;
            runInfo21.averageColdStrength /= size;
        }
        return runInfo;
    }
    
    private void saveData() {
        if (this.runs.size() <= 0) {
            return;
        }
        try {
            final Iterator<RunInfo> iterator = this.runs.iterator();
            while (iterator.hasNext()) {
                iterator.next().calculate();
            }
            final RunInfo calculateTotal = this.calculateTotal();
            final String format = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            ZomboidFileSystem.instance.getFileInCurrentSave("climate").mkdirs();
            final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("climate");
            if (fileInCurrentSave.exists() && fileInCurrentSave.isDirectory()) {
                final String fileNameInCurrentSave = ZomboidFileSystem.instance.getFileNameInCurrentSave("climate", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, format));
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fileNameInCurrentSave));
                final File file = new File(fileNameInCurrentSave);
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fileNameInCurrentSave));
                try {
                    final FileWriter writer = new FileWriter(file, false);
                    try {
                        this.writer = writer;
                        final int size = this.runs.size();
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                        this.write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.runs.size(), this.durDays));
                        if (this.DoOverrideSandboxRainMod) {
                            this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.SandboxRainModOverride));
                        }
                        else {
                            this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, SandboxOptions.instance.getRainModifier()));
                        }
                        this.write("");
                        this.write("===================================================================");
                        this.write(" TOTALS OVERVIEW");
                        this.write("===================================================================");
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, calculateTotal.totalPeriods, calculateTotal.totalPeriods / size));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.longestPeriod)));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.shortestPeriod)));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.averagePeriod)));
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.totalPeriodDuration)));
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.mostDryPeriod)));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.mostWetPeriod)));
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, calculateTotal.totalEmpty, calculateTotal.totalEmpty / size));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.longestEmpty)));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.shortestEmpty)));
                        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(calculateTotal.averageEmpty)));
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.highestStrength));
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.lowestStrength));
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.averageStrength));
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.highestWarmStrength));
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.lowestWarmStrength));
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.averageWarmStrength));
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.highestColdStrength));
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.lowestColdStrength));
                        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, calculateTotal.averageColdStrength));
                        this.write("");
                        this.write("Weather period types:");
                        final double n = size;
                        this.write(invokedynamic(makeConcatWithConstants:(ID)Ljava/lang/String;, calculateTotal.countNormalWarm, this.round(calculateTotal.countNormalWarm / n)));
                        this.write(invokedynamic(makeConcatWithConstants:(ID)Ljava/lang/String;, calculateTotal.countNormalCold, this.round(calculateTotal.countNormalCold / n)));
                        this.write(invokedynamic(makeConcatWithConstants:(ID)Ljava/lang/String;, calculateTotal.countStorm, this.round(calculateTotal.countStorm / (double)size)));
                        this.write(invokedynamic(makeConcatWithConstants:(ID)Ljava/lang/String;, calculateTotal.countTropical, this.round(calculateTotal.countTropical / n)));
                        this.write(invokedynamic(makeConcatWithConstants:(ID)Ljava/lang/String;, calculateTotal.countBlizzard, this.round(calculateTotal.countBlizzard / n)));
                        this.write("");
                        this.write("Distribution duration in days (total periods)");
                        this.printCountTable(writer, calculateTotal.dayCountPeriod);
                        this.write("");
                        this.write("Distribution duration in days (WARM periods)");
                        this.printCountTable(writer, calculateTotal.dayCountWarmPeriod);
                        this.write("");
                        this.write("Distribution duration in days (COLD periods)");
                        this.printCountTable(writer, calculateTotal.dayCountColdPeriod);
                        this.write("");
                        this.write("Distribution duration in days (clear periods)");
                        this.printCountTable(writer, calculateTotal.dayCountEmpty);
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, calculateTotal.exceedingPeriods.size()));
                        if (calculateTotal.exceedingPeriods.size() > 0) {
                            final Iterator<Integer> iterator2 = calculateTotal.exceedingPeriods.iterator();
                            while (iterator2.hasNext()) {
                                this.writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;)Ljava/lang/String;, Integer.valueOf(iterator2.next())));
                            }
                        }
                        this.write("");
                        this.write("");
                        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, calculateTotal.exceedingEmpties.size()));
                        if (calculateTotal.exceedingEmpties.size() > 0) {
                            final Iterator<Integer> iterator3 = calculateTotal.exceedingEmpties.iterator();
                            while (iterator3.hasNext()) {
                                this.writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;)Ljava/lang/String;, Integer.valueOf(iterator3.next())));
                            }
                        }
                        this.write("");
                        this.write("");
                        this.write("Distribution duration total weather days:");
                        this.printCountTable(this.writer, calculateTotal.totalDaysPeriod, this.TotalDaysPeriodIndexMod);
                        this.writeDataExtremes();
                        this.writer = null;
                        writer.close();
                    }
                    catch (Throwable t) {
                        try {
                            writer.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                final File fileInCurrentSave2 = ZomboidFileSystem.instance.getFileInCurrentSave("climate", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, format));
                try {
                    final FileWriter writer2 = new FileWriter(fileInCurrentSave2, false);
                    try {
                        this.writer = writer2;
                        this.writeData();
                        this.writer = null;
                        writer2.close();
                    }
                    catch (Throwable t2) {
                        try {
                            writer2.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                        throw t2;
                    }
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
                final File fileInCurrentSave3 = ZomboidFileSystem.instance.getFileInCurrentSave("climate", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, format));
                try {
                    final FileWriter writer3 = new FileWriter(fileInCurrentSave3, false);
                    try {
                        this.writer = writer3;
                        this.writePatterns();
                        this.writer = null;
                        writer3.close();
                    }
                    catch (Throwable t3) {
                        try {
                            writer3.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                        throw t3;
                    }
                }
                catch (Exception ex3) {
                    ex3.printStackTrace();
                }
            }
        }
        catch (Exception ex4) {
            ex4.printStackTrace();
        }
    }
    
    private double round(final double n) {
        return Math.round(n * 100.0) / 100.0;
    }
    
    private void writeRunInfo(final RunInfo runInfo, final int n) throws Exception {
        this.write("===================================================================");
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        this.write("===================================================================");
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.totalPeriods));
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.longestPeriod)));
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.shortestPeriod)));
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.averagePeriod)));
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.totalPeriodDuration)));
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.totalEmpty));
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.longestEmpty)));
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.shortestEmpty)));
        this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.formatDuration(runInfo.averageEmpty)));
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.highestStrength));
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.lowestStrength));
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.averageStrength));
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.highestWarmStrength));
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.lowestWarmStrength));
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.averageWarmStrength));
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.highestColdStrength));
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.lowestColdStrength));
        this.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, runInfo.averageColdStrength));
        this.write("");
        this.write("Weather period types:");
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.countNormalWarm));
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.countNormalCold));
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.countStorm));
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.countTropical));
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.countBlizzard));
        this.write("");
        this.write("Distribution duration in days (total periods)");
        this.printCountTable(this.writer, runInfo.dayCountPeriod);
        this.write("");
        this.write("Distribution duration in days (WARM periods)");
        this.printCountTable(this.writer, runInfo.dayCountWarmPeriod);
        this.write("");
        this.write("Distribution duration in days (COLD periods)");
        this.printCountTable(this.writer, runInfo.dayCountColdPeriod);
        this.write("");
        this.write("Distribution duration in days (clear periods)");
        this.printCountTable(this.writer, runInfo.dayCountEmpty);
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.exceedingPeriods.size()));
        if (runInfo.exceedingPeriods.size() > 0) {
            final Iterator<Integer> iterator = runInfo.exceedingPeriods.iterator();
            while (iterator.hasNext()) {
                this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;)Ljava/lang/String;, Integer.valueOf(iterator.next())));
            }
        }
        this.write("");
        this.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, runInfo.exceedingEmpties.size()));
        if (runInfo.exceedingEmpties.size() > 0) {
            final Iterator<Integer> iterator2 = runInfo.exceedingEmpties.iterator();
            while (iterator2.hasNext()) {
                this.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;)Ljava/lang/String;, Integer.valueOf(iterator2.next())));
            }
        }
    }
    
    private void write(final String s) throws Exception {
        this.writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, System.lineSeparator()));
    }
    
    private void writeDataExtremes() throws Exception {
        int n = 0;
        int n2 = -1;
        int n3 = -1;
        RunInfo runInfo = null;
        RunInfo runInfo2 = null;
        for (final RunInfo runInfo3 : this.runs) {
            ++n;
            if (runInfo == null || runInfo3.totalPeriodDuration < runInfo.totalPeriodDuration) {
                runInfo = runInfo3;
                n2 = n;
            }
            if (runInfo2 == null || runInfo3.totalPeriodDuration > runInfo2.totalPeriodDuration) {
                runInfo2 = runInfo3;
                n3 = n;
            }
        }
        this.write("");
        this.write("MOST DRY RUN:");
        if (runInfo != null) {
            this.writeRunInfo(runInfo, n2);
        }
        this.write("");
        this.write("MOST WET RUN:");
        if (runInfo2 != null) {
            this.writeRunInfo(runInfo2, n3);
        }
    }
    
    private void writeData() throws Exception {
        int n = 0;
        for (final RunInfo runInfo : this.runs) {
            ++n;
            this.writeRunInfo(runInfo, n);
        }
    }
    
    private void writePatterns() throws Exception {
        final String replacement = "-";
        final String s = "#";
        final String replacement2 = "S";
        final String replacement3 = "T";
        final String replacement4 = "B";
        for (final RunInfo runInfo : this.runs) {
            int n = 0;
            for (final RecordInfo recordInfo : runInfo.records) {
                final int n2 = (int)Math.ceil(recordInfo.durationHours / 24.0);
                String str;
                if (recordInfo.isWeather && recordInfo.weatherType == 1) {
                    str = new String(new char[n2]).replace("\u0000", replacement2);
                }
                else if (recordInfo.isWeather && recordInfo.weatherType == 2) {
                    str = new String(new char[n2]).replace("\u0000", replacement3);
                }
                else if (recordInfo.isWeather && recordInfo.weatherType == 3) {
                    str = new String(new char[n2]).replace("\u0000", replacement4);
                }
                else if (n == 0 && !recordInfo.isWeather && n2 >= 2) {
                    str = new String(new char[n2 - 1]).replace("\u0000", replacement);
                }
                else {
                    str = new String(new char[n2]).replace("\u0000", recordInfo.isWeather ? s : replacement);
                }
                this.writer.write(str);
                ++n;
            }
            this.writer.write(System.lineSeparator());
        }
    }
    
    private void printCountTable(final FileWriter fileWriter, final int[] array) throws Exception {
        this.printCountTable(fileWriter, array, 1);
    }
    
    private void printCountTable(final FileWriter fileWriter, final int[] array, final int n) throws Exception {
        if (array == null || array.length <= 0) {
            return;
        }
        int n2 = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] > n2) {
                n2 = array[i];
            }
        }
        this.write("    DAYS   COUNT GRAPH");
        final float n3 = 50.0f / n2;
        if (n2 > 0) {
            for (int j = 0; j < array.length; ++j) {
                final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, "", String.format("%1$8s", new Object[] { invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, j * n, j * n + n) }));
                final int k = array[j];
                String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, String.format("%1$8s", k)));
                final int n4 = (int)(k * n3);
                if (n4 > 0) {
                    s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, new String(new char[n4]).replace("\u0000", "#"));
                }
                else if (k > 0) {
                    s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
                }
                this.write(s2);
            }
        }
    }
    
    private String formatDuration(final double n) {
        final int n2 = (int)(n / 24.0);
        return invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n2, (int)(n - n2 * 24));
    }
    
    private class RunInfo
    {
        public double seedA;
        public int durationDays;
        public double durationHours;
        public ArrayList<RecordInfo> records;
        public double totalPeriodDuration;
        public double longestPeriod;
        public double shortestPeriod;
        public int totalPeriods;
        public double averagePeriod;
        public double longestEmpty;
        public double shortestEmpty;
        public int totalEmpty;
        public double averageEmpty;
        public float highestStrength;
        public float lowestStrength;
        public float averageStrength;
        public float highestWarmStrength;
        public float lowestWarmStrength;
        public float averageWarmStrength;
        public float highestColdStrength;
        public float lowestColdStrength;
        public float averageColdStrength;
        public int countNormalWarm;
        public int countNormalCold;
        public int countStorm;
        public int countTropical;
        public int countBlizzard;
        public int[] dayCountPeriod;
        public int[] dayCountWarmPeriod;
        public int[] dayCountColdPeriod;
        public int[] dayCountEmpty;
        public ArrayList<Integer> exceedingPeriods;
        public ArrayList<Integer> exceedingEmpties;
        public double mostWetPeriod;
        public double mostDryPeriod;
        public int[] totalDaysPeriod;
        
        private RunInfo() {
            this.records = new ArrayList<RecordInfo>();
            this.totalPeriodDuration = 0.0;
            this.longestPeriod = 0.0;
            this.shortestPeriod = 9.99999999E8;
            this.totalPeriods = 0;
            this.averagePeriod = 0.0;
            this.longestEmpty = 0.0;
            this.shortestEmpty = 9.99999999E8;
            this.totalEmpty = 0;
            this.averageEmpty = 0.0;
            this.highestStrength = 0.0f;
            this.lowestStrength = 1.0f;
            this.averageStrength = 0.0f;
            this.highestWarmStrength = 0.0f;
            this.lowestWarmStrength = 1.0f;
            this.averageWarmStrength = 0.0f;
            this.highestColdStrength = 0.0f;
            this.lowestColdStrength = 1.0f;
            this.averageColdStrength = 0.0f;
            this.countNormalWarm = 0;
            this.countNormalCold = 0;
            this.countStorm = 0;
            this.countTropical = 0;
            this.countBlizzard = 0;
            this.dayCountPeriod = new int[16];
            this.dayCountWarmPeriod = new int[16];
            this.dayCountColdPeriod = new int[16];
            this.dayCountEmpty = new int[75];
            this.exceedingPeriods = new ArrayList<Integer>();
            this.exceedingEmpties = new ArrayList<Integer>();
            this.mostWetPeriod = 0.0;
            this.mostDryPeriod = 9.99999999E8;
        }
        
        public RecordInfo addRecord(final double durationHours) {
            final RecordInfo e = new RecordInfo();
            e.durationHours = durationHours;
            e.isWeather = false;
            this.records.add(e);
            return e;
        }
        
        public RecordInfo addRecord(final int airType, final double durationHours, final float strength, final boolean b, final boolean b2, final boolean b3) {
            final RecordInfo e = new RecordInfo();
            e.durationHours = durationHours;
            e.isWeather = true;
            e.airType = airType;
            e.strength = strength;
            e.weatherType = 0;
            if (b) {
                e.weatherType = 1;
            }
            else if (b2) {
                e.weatherType = 2;
            }
            else if (b3) {
                e.weatherType = 3;
            }
            this.records.add(e);
            return e;
        }
        
        public void calculate() {
            double n = 0.0;
            double n2 = 0.0;
            float n3 = 0.0f;
            float n4 = 0.0f;
            float n5 = 0.0f;
            int n6 = 0;
            int n7 = 0;
            for (final RecordInfo recordInfo : this.records) {
                final int n8 = (int)(recordInfo.durationHours / 24.0);
                if (recordInfo.isWeather) {
                    this.totalPeriodDuration += recordInfo.durationHours;
                    if (recordInfo.durationHours > this.longestPeriod) {
                        this.longestPeriod = recordInfo.durationHours;
                    }
                    if (recordInfo.durationHours < this.shortestPeriod) {
                        this.shortestPeriod = recordInfo.durationHours;
                    }
                    ++this.totalPeriods;
                    n += recordInfo.durationHours;
                    if (recordInfo.strength > this.highestStrength) {
                        this.highestStrength = recordInfo.strength;
                    }
                    if (recordInfo.strength < this.lowestStrength) {
                        this.lowestStrength = recordInfo.strength;
                    }
                    n3 += recordInfo.strength;
                    if (recordInfo.airType == 1) {
                        ++n6;
                        if (recordInfo.strength > this.highestWarmStrength) {
                            this.highestWarmStrength = recordInfo.strength;
                        }
                        if (recordInfo.strength < this.lowestWarmStrength) {
                            this.lowestWarmStrength = recordInfo.strength;
                        }
                        n4 += recordInfo.strength;
                        if (recordInfo.weatherType == 1) {
                            ++this.countStorm;
                        }
                        else if (recordInfo.weatherType == 2) {
                            ++this.countTropical;
                        }
                        else if (recordInfo.weatherType == 3) {
                            ++this.countBlizzard;
                        }
                        else {
                            ++this.countNormalWarm;
                        }
                        if (n8 < this.dayCountWarmPeriod.length) {
                            final int[] dayCountWarmPeriod = this.dayCountWarmPeriod;
                            final int n9 = n8;
                            ++dayCountWarmPeriod[n9];
                        }
                    }
                    else {
                        ++n7;
                        if (recordInfo.strength > this.highestColdStrength) {
                            this.highestColdStrength = recordInfo.strength;
                        }
                        if (recordInfo.strength < this.lowestColdStrength) {
                            this.lowestColdStrength = recordInfo.strength;
                        }
                        n5 += recordInfo.strength;
                        ++this.countNormalCold;
                        if (n8 < this.dayCountColdPeriod.length) {
                            final int[] dayCountColdPeriod = this.dayCountColdPeriod;
                            final int n10 = n8;
                            ++dayCountColdPeriod[n10];
                        }
                    }
                    if (n8 < this.dayCountPeriod.length) {
                        final int[] dayCountPeriod = this.dayCountPeriod;
                        final int n11 = n8;
                        ++dayCountPeriod[n11];
                    }
                    else {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n8));
                        this.exceedingPeriods.add(n8);
                    }
                }
                else {
                    if (recordInfo.durationHours > this.longestEmpty) {
                        this.longestEmpty = recordInfo.durationHours;
                    }
                    if (recordInfo.durationHours < this.shortestEmpty) {
                        this.shortestEmpty = recordInfo.durationHours;
                    }
                    ++this.totalEmpty;
                    n2 += recordInfo.durationHours;
                    if (n8 < this.dayCountEmpty.length) {
                        final int[] dayCountEmpty = this.dayCountEmpty;
                        final int n12 = n8;
                        ++dayCountEmpty[n12];
                    }
                    else {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n8));
                        this.exceedingEmpties.add(n8);
                    }
                }
            }
            if (this.totalPeriods > 0) {
                this.averagePeriod = n / this.totalPeriods;
                this.averageStrength = n3 / this.totalPeriods;
                if (n6 > 0) {
                    this.averageWarmStrength = n4 / n6;
                }
                if (n7 > 0) {
                    this.averageColdStrength = n5 / n7;
                }
            }
            if (this.totalEmpty > 0) {
                this.averageEmpty = n2 / this.totalEmpty;
            }
        }
    }
    
    private class RecordInfo
    {
        public boolean isWeather;
        public float strength;
        public int airType;
        public double durationHours;
        public int weatherType;
        
        private RecordInfo() {
            this.weatherType = 0;
        }
    }
}
