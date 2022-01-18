// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.season;

import zombie.debug.DebugLog;
import zombie.erosion.utils.Noise2D;
import java.util.GregorianCalendar;

public final class ErosionSeason
{
    public static final int SEASON_DEFAULT = 0;
    public static final int SEASON_SPRING = 1;
    public static final int SEASON_SUMMER = 2;
    public static final int SEASON_SUMMER2 = 3;
    public static final int SEASON_AUTUMN = 4;
    public static final int SEASON_WINTER = 5;
    public static final int NUM_SEASONS = 6;
    private int lat;
    private int tempMax;
    private int tempMin;
    private int tempDiff;
    private float highNoon;
    private float highNoonCurrent;
    private int seasonLag;
    private final float[] rain;
    private double suSol;
    private double wiSol;
    private final GregorianCalendar zeroDay;
    private int day;
    private int month;
    private int year;
    private boolean isH1;
    private YearData[] yearData;
    private int curSeason;
    private float curSeasonDay;
    private float curSeasonDays;
    private float curSeasonStrength;
    private float curSeasonProgression;
    private float dayMeanTemperature;
    private float dayTemperature;
    private float dayNoiseVal;
    private boolean isRainDay;
    private float rainYearAverage;
    private float rainDayStrength;
    private boolean isThunderDay;
    private boolean isSunnyDay;
    private float dayDusk;
    private float dayDawn;
    private float dayDaylight;
    private float winterMod;
    private float summerMod;
    private float summerTilt;
    private float curDayPercent;
    private Noise2D per;
    private int seedA;
    private int seedB;
    private int seedC;
    String[] names;
    
    public ErosionSeason() {
        this.lat = 38;
        this.tempMax = 25;
        this.tempMin = 0;
        this.tempDiff = 7;
        this.highNoon = 12.5f;
        this.highNoonCurrent = 12.5f;
        this.seasonLag = 31;
        this.rain = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
        this.zeroDay = new GregorianCalendar(1970, 0, 1, 0, 0);
        this.yearData = new YearData[3];
        this.curDayPercent = 0.0f;
        this.per = new Noise2D();
        this.seedA = 64;
        this.seedB = 128;
        this.seedC = 255;
        this.names = new String[] { "Default", "Spring", "Early Summer", "Late Summer", "Autumn", "Winter" };
    }
    
    public void init(final int lat, final int tempMax, final int tempMin, final int tempDiff, final int seasonLag, final float n, final int seedA, final int seedB, final int seedC) {
        this.lat = lat;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.tempDiff = tempDiff;
        this.seasonLag = seasonLag;
        this.highNoon = n;
        this.highNoonCurrent = n;
        this.seedA = seedA;
        this.seedB = seedB;
        this.seedC = seedC;
        this.summerTilt = 2.0f;
        this.winterMod = ((this.tempMin < 0) ? (0.05f * -this.tempMin) : (0.02f * -this.tempMin));
        this.summerMod = ((this.tempMax < 0) ? (0.05f * this.tempMax) : (0.02f * this.tempMax));
        this.suSol = 2.0 * this.degree(Math.acos(-Math.tan(this.radian(this.lat)) * Math.tan(this.radian(23.44)))) / 15.0;
        this.wiSol = 2.0 * this.degree(Math.acos(Math.tan(this.radian(this.lat)) * Math.tan(this.radian(23.44)))) / 15.0;
        this.per.reset();
        this.per.addLayer(seedA, 8.0f, 2.0f);
        this.per.addLayer(seedB, 6.0f, 4.0f);
        this.per.addLayer(seedC, 4.0f, 6.0f);
        this.yearData[0] = new YearData();
        this.yearData[1] = new YearData();
        this.yearData[2] = new YearData();
    }
    
    public int getLat() {
        return this.lat;
    }
    
    public int getTempMax() {
        return this.tempMax;
    }
    
    public int getTempMin() {
        return this.tempMin;
    }
    
    public int getTempDiff() {
        return this.tempDiff;
    }
    
    public int getSeasonLag() {
        return this.seasonLag;
    }
    
    public float getHighNoon() {
        return this.highNoon;
    }
    
    public int getSeedA() {
        return this.seedA;
    }
    
    public int getSeedB() {
        return this.seedB;
    }
    
    public int getSeedC() {
        return this.seedC;
    }
    
    public void setRain(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.rain[0] = n;
        this.rain[1] = n2;
        this.rain[2] = n3;
        this.rain[3] = n4;
        this.rain[4] = n5;
        this.rain[5] = n6;
        this.rain[6] = n7;
        this.rain[7] = n8;
        this.rain[8] = n9;
        this.rain[9] = n10;
        this.rain[10] = n11;
        this.rain[11] = n12;
        float n13 = 0.0f;
        final float[] rain = this.rain;
        for (int length = rain.length, i = 0; i < length; ++i) {
            n13 += rain[i];
        }
        this.rainYearAverage = (float)(int)Math.floor(365.0f * (n13 / this.rain.length));
    }
    
    public ErosionSeason clone() {
        final ErosionSeason erosionSeason = new ErosionSeason();
        erosionSeason.init(this.lat, this.tempMax, this.tempMin, this.tempDiff, this.seasonLag, this.highNoon, this.seedA, this.seedB, this.seedC);
        erosionSeason.setRain(this.rain[0], this.rain[1], this.rain[2], this.rain[3], this.rain[4], this.rain[5], this.rain[6], this.rain[7], this.rain[8], this.rain[9], this.rain[10], this.rain[11]);
        return erosionSeason;
    }
    
    public float getCurDayPercent() {
        return this.curDayPercent;
    }
    
    public double getMaxDaylightWinter() {
        return this.wiSol;
    }
    
    public double getMaxDaylightSummer() {
        return this.suSol;
    }
    
    public float getDusk() {
        return this.dayDusk;
    }
    
    public float getDawn() {
        return this.dayDawn;
    }
    
    public float getDaylight() {
        return this.dayDaylight;
    }
    
    public float getDayTemperature() {
        return this.dayTemperature;
    }
    
    public float getDayMeanTemperature() {
        return this.dayMeanTemperature;
    }
    
    public int getSeason() {
        return this.curSeason;
    }
    
    public float getDayHighNoon() {
        return this.highNoonCurrent;
    }
    
    public String getSeasonName() {
        return this.names[this.curSeason];
    }
    
    public boolean isSeason(final int n) {
        return n == this.curSeason;
    }
    
    public GregorianCalendar getWinterStartDay(final int dayOfMonth, final int month, final int year) {
        if (new GregorianCalendar(year, month, dayOfMonth).getTime().getTime() < this.yearData[0].winterEndDayUnx) {
            return this.yearData[0].winterStartDay;
        }
        return this.yearData[1].winterStartDay;
    }
    
    public float getSeasonDay() {
        return this.curSeasonDay;
    }
    
    public float getSeasonDays() {
        return this.curSeasonDays;
    }
    
    public float getSeasonStrength() {
        return this.curSeasonStrength;
    }
    
    public float getSeasonProgression() {
        return this.curSeasonProgression;
    }
    
    public float getDayNoiseVal() {
        return this.dayNoiseVal;
    }
    
    public boolean isRainDay() {
        return this.isRainDay;
    }
    
    public float getRainDayStrength() {
        return this.rainDayStrength;
    }
    
    public float getRainYearAverage() {
        return this.rainYearAverage;
    }
    
    public boolean isThunderDay() {
        return this.isThunderDay;
    }
    
    public boolean isSunnyDay() {
        return this.isSunnyDay;
    }
    
    public void setDay(final int dayOfMonth, final int month, final int n) {
        if (n == 0) {
            DebugLog.log("NOTICE: year value is 0?");
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(n, month, dayOfMonth, 0, 0);
        final long time = gregorianCalendar.getTime().getTime();
        this.setYearData(n);
        this.setSeasonData((float)time, gregorianCalendar, n, month);
        this.setDaylightData(time, gregorianCalendar);
    }
    
    private void setYearData(final int n) {
        if (this.yearData[1].year == n) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            final int year = n + (i - 1);
            this.yearData[i].year = year;
            this.yearData[i].winSols = new GregorianCalendar(year, 11, 22);
            this.yearData[i].sumSols = new GregorianCalendar(year, 5, 22);
            this.yearData[i].winSolsUnx = this.yearData[i].winSols.getTime().getTime();
            this.yearData[i].sumSolsUnx = this.yearData[i].sumSols.getTime().getTime();
            this.yearData[i].hottestDay = new GregorianCalendar(year, 5, 22);
            this.yearData[i].coldestDay = new GregorianCalendar(year, 11, 22);
            this.yearData[i].hottestDay.add(5, this.seasonLag);
            this.yearData[i].coldestDay.add(5, this.seasonLag);
            this.yearData[i].hottestDayUnx = this.yearData[i].hottestDay.getTime().getTime();
            this.yearData[i].coldestDayUnx = this.yearData[i].coldestDay.getTime().getTime();
            this.yearData[i].winterS = this.per.layeredNoise((float)(64 + year), 64.0f);
            this.yearData[i].winterE = this.per.layeredNoise(64.0f, (float)(64 + year));
            this.yearData[i].winterStartDay = new GregorianCalendar(year, 11, 22);
            this.yearData[i].winterEndDay = new GregorianCalendar(year, 11, 22);
            this.yearData[i].winterStartDay.add(5, (int)(-Math.floor(40.0f + 40.0f * this.winterMod + 20.0f * this.yearData[i].winterS)));
            this.yearData[i].winterEndDay.add(5, (int)Math.floor(40.0f + 40.0f * this.winterMod + 20.0f * this.yearData[i].winterE));
            this.yearData[i].winterStartDayUnx = this.yearData[i].winterStartDay.getTime().getTime();
            this.yearData[i].winterEndDayUnx = this.yearData[i].winterEndDay.getTime().getTime();
            this.yearData[i].summerS = this.per.layeredNoise((float)(128 + year), 128.0f);
            this.yearData[i].summerE = this.per.layeredNoise(128.0f, (float)(128 + year));
            this.yearData[i].summerStartDay = new GregorianCalendar(year, 5, 22);
            this.yearData[i].summerEndDay = new GregorianCalendar(year, 5, 22);
            this.yearData[i].summerStartDay.add(5, (int)(-Math.floor(40.0f + 40.0f * this.summerMod + 20.0f * this.yearData[i].summerS)));
            this.yearData[i].summerEndDay.add(5, (int)Math.floor(40.0f + 40.0f * this.summerMod + 20.0f * this.yearData[i].summerE));
            this.yearData[i].summerStartDayUnx = this.yearData[i].summerStartDay.getTime().getTime();
            this.yearData[i].summerEndDayUnx = this.yearData[i].summerEndDay.getTime().getTime();
        }
        this.yearData[1].lastSummerStr = this.yearData[0].summerS + this.yearData[0].summerE - 1.0f;
        this.yearData[1].lastWinterStr = this.yearData[0].winterS + this.yearData[0].winterE - 1.0f;
        this.yearData[1].summerStr = this.yearData[1].summerS + this.yearData[1].summerE - 1.0f;
        this.yearData[1].winterStr = this.yearData[1].winterS + this.yearData[1].winterE - 1.0f;
        this.yearData[1].nextSummerStr = this.yearData[2].summerS + this.yearData[2].summerE - 1.0f;
        this.yearData[1].nextWinterStr = this.yearData[2].winterS + this.yearData[2].winterE - 1.0f;
    }
    
    private void setSeasonData(final float n, final GregorianCalendar gregorianCalendar, final int n2, final int n3) {
        GregorianCalendar gregorianCalendar2;
        GregorianCalendar gregorianCalendar3;
        if (n < this.yearData[0].winterEndDayUnx) {
            this.curSeason = 5;
            gregorianCalendar2 = this.yearData[0].winterStartDay;
            gregorianCalendar3 = this.yearData[0].winterEndDay;
        }
        else if (n < this.yearData[1].summerStartDayUnx) {
            this.curSeason = 1;
            gregorianCalendar2 = this.yearData[0].winterEndDay;
            gregorianCalendar3 = this.yearData[1].summerStartDay;
        }
        else if (n < this.yearData[1].summerEndDayUnx) {
            this.curSeason = 2;
            gregorianCalendar2 = this.yearData[1].summerStartDay;
            gregorianCalendar3 = this.yearData[1].summerEndDay;
        }
        else if (n < this.yearData[1].winterStartDayUnx) {
            this.curSeason = 4;
            gregorianCalendar2 = this.yearData[1].summerEndDay;
            gregorianCalendar3 = this.yearData[1].winterStartDay;
        }
        else {
            this.curSeason = 5;
            gregorianCalendar2 = this.yearData[1].winterStartDay;
            gregorianCalendar3 = this.yearData[1].winterEndDay;
        }
        this.curSeasonDay = this.dayDiff(gregorianCalendar, gregorianCalendar2);
        this.curSeasonDays = this.dayDiff(gregorianCalendar2, gregorianCalendar3);
        this.curSeasonStrength = this.curSeasonDays / 90.0f - 1.0f;
        this.curSeasonProgression = this.curSeasonDay / this.curSeasonDays;
        float n4;
        float n5;
        float n6;
        if (n < this.yearData[0].coldestDayUnx && n >= this.yearData[0].hottestDayUnx) {
            n4 = this.tempMax + this.tempDiff / 2 * this.yearData[1].lastSummerStr;
            n5 = this.tempMin + this.tempDiff / 2 * this.yearData[1].lastWinterStr;
            n6 = this.dayDiff(gregorianCalendar, this.yearData[0].hottestDay) / this.dayDiff(this.yearData[0].hottestDay, this.yearData[0].coldestDay);
        }
        else if (n < this.yearData[1].hottestDayUnx && n >= this.yearData[0].coldestDayUnx) {
            n4 = this.tempMin + this.tempDiff / 2 * this.yearData[1].lastWinterStr;
            n5 = this.tempMax + this.tempDiff / 2 * this.yearData[1].summerStr;
            n6 = this.dayDiff(gregorianCalendar, this.yearData[0].coldestDay) / this.dayDiff(this.yearData[1].hottestDay, this.yearData[0].coldestDay);
        }
        else if (n < this.yearData[1].coldestDayUnx && n >= this.yearData[1].hottestDayUnx) {
            n4 = this.tempMax + this.tempDiff / 2 * this.yearData[1].summerStr;
            n5 = this.tempMin + this.tempDiff / 2 * this.yearData[1].winterStr;
            n6 = this.dayDiff(gregorianCalendar, this.yearData[1].hottestDay) / this.dayDiff(this.yearData[1].hottestDay, this.yearData[1].coldestDay);
        }
        else {
            n4 = this.tempMin + this.tempDiff / 2 * this.yearData[1].winterStr;
            n5 = this.tempMax + this.tempDiff / 2 * this.yearData[1].nextSummerStr;
            n6 = this.dayDiff(gregorianCalendar, this.yearData[1].coldestDay) / this.dayDiff(this.yearData[1].coldestDay, this.yearData[2].hottestDay);
        }
        final float dayMeanTemperature = (float)this.clerp(n6, n4, n5);
        final float n7 = this.dayDiff(this.zeroDay, gregorianCalendar) / 20.0f;
        this.dayNoiseVal = this.per.layeredNoise(n7, 0.0f);
        this.dayTemperature = dayMeanTemperature + this.tempDiff * (this.dayNoiseVal * 2.0f - 1.0f);
        this.dayMeanTemperature = dayMeanTemperature;
        this.isThunderDay = false;
        this.isRainDay = false;
        this.isSunnyDay = false;
        final float n8 = (0.1f + this.rain[n3] <= 1.0f) ? (0.1f + this.rain[n3]) : 1.0f;
        if (n8 > 0.0f && this.dayNoiseVal < n8) {
            this.isRainDay = true;
            this.rainDayStrength = 1.0f - this.dayNoiseVal / n8;
            if (this.per.layeredNoise(0.0f, n7) > 0.6) {
                this.isThunderDay = true;
            }
        }
        if (this.dayNoiseVal > 0.6) {
            this.isSunnyDay = true;
        }
    }
    
    private void setDaylightData(final long n, final GregorianCalendar gregorianCalendar) {
        GregorianCalendar gregorianCalendar2;
        GregorianCalendar gregorianCalendar3;
        if (n < this.yearData[1].winSolsUnx && n >= this.yearData[1].sumSolsUnx) {
            this.isH1 = false;
            gregorianCalendar2 = this.yearData[1].sumSols;
            gregorianCalendar3 = this.yearData[1].winSols;
        }
        else {
            this.isH1 = true;
            if (n >= this.yearData[1].winSolsUnx) {
                gregorianCalendar2 = this.yearData[1].winSols;
                gregorianCalendar3 = this.yearData[2].sumSols;
            }
            else {
                gregorianCalendar2 = this.yearData[0].winSols;
                gregorianCalendar3 = this.yearData[1].sumSols;
            }
        }
        float curDayPercent;
        final float n2 = curDayPercent = this.dayDiff(gregorianCalendar, gregorianCalendar2) / this.dayDiff(gregorianCalendar2, gregorianCalendar3);
        if (this.isH1) {
            this.dayDaylight = (float)this.clerp(n2, this.wiSol, this.suSol);
        }
        else {
            this.dayDaylight = (float)this.clerp(n2, this.suSol, this.wiSol);
            curDayPercent = 1.0f - curDayPercent;
        }
        this.curDayPercent = curDayPercent;
        this.highNoonCurrent = this.highNoon + this.summerTilt * curDayPercent;
        this.dayDawn = this.highNoonCurrent - this.dayDaylight / 2.0f;
        this.dayDusk = this.highNoonCurrent + this.dayDaylight / 2.0f;
    }
    
    private float dayDiff(final GregorianCalendar gregorianCalendar, final GregorianCalendar gregorianCalendar2) {
        return (float)Math.abs((gregorianCalendar.getTime().getTime() - gregorianCalendar2.getTime().getTime()) / 86400000L);
    }
    
    private double clerp(final double n, final double n2, final double n3) {
        final double n4 = (1.0 - Math.cos(n * 3.141592653589793)) / 2.0;
        return n2 * (1.0 - n4) + n3 * n4;
    }
    
    private double lerp(final double n, final double n2, final double n3) {
        return n2 + n * (n3 - n2);
    }
    
    private double radian(final double n) {
        return n * 0.017453292519943295;
    }
    
    private double degree(final double n) {
        return n * 57.29577951308232;
    }
    
    public static void Reset() {
    }
    
    public void setCurSeason(final int curSeason) {
        this.curSeason = curSeason;
    }
    
    private static class YearData
    {
        public int year;
        public GregorianCalendar winSols;
        public GregorianCalendar sumSols;
        public long winSolsUnx;
        public long sumSolsUnx;
        public GregorianCalendar hottestDay;
        public GregorianCalendar coldestDay;
        public long hottestDayUnx;
        public long coldestDayUnx;
        public float winterS;
        public float winterE;
        public GregorianCalendar winterStartDay;
        public GregorianCalendar winterEndDay;
        public long winterStartDayUnx;
        public long winterEndDayUnx;
        public float summerS;
        public float summerE;
        public GregorianCalendar summerStartDay;
        public GregorianCalendar summerEndDay;
        public long summerStartDayUnx;
        public long summerEndDayUnx;
        public float lastSummerStr;
        public float lastWinterStr;
        public float summerStr;
        public float winterStr;
        public float nextSummerStr;
        public float nextWinterStr;
        
        private YearData() {
            this.year = Integer.MIN_VALUE;
        }
    }
}
