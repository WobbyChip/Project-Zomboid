// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import java.util.GregorianCalendar;
import zombie.GameTime;
import java.util.ArrayList;

public class ClimateForecaster
{
    private static final int OffsetToday = 10;
    private ClimateValues climateValues;
    private DayForecast[] forecasts;
    private ArrayList<DayForecast> forecastList;
    
    public ClimateForecaster() {
        this.forecasts = new DayForecast[40];
        this.forecastList = new ArrayList<DayForecast>(40);
    }
    
    public ArrayList<DayForecast> getForecasts() {
        return this.forecastList;
    }
    
    public DayForecast getForecast() {
        return this.getForecast(0);
    }
    
    public DayForecast getForecast(final int n) {
        final int n2 = 10 + n;
        if (n2 >= 0 && n2 < this.forecasts.length) {
            return this.forecasts[n2];
        }
        return null;
    }
    
    private void populateForecastList() {
        this.forecastList.clear();
        for (int i = 0; i < this.forecasts.length; ++i) {
            this.forecastList.add(this.forecasts[i]);
        }
    }
    
    protected void init(final ClimateManager climateManager) {
        this.climateValues = climateManager.getClimateValuesCopy();
        for (int i = 0; i < this.forecasts.length; ++i) {
            final int indexOffset = i - 10;
            final DayForecast dayForecast = new DayForecast();
            (dayForecast.weatherPeriod = new WeatherPeriod(climateManager, climateManager.getThunderStorm())).setDummy(true);
            dayForecast.indexOffset = indexOffset;
            dayForecast.airFront = new ClimateManager.AirFront();
            this.sampleDay(climateManager, dayForecast, indexOffset);
            this.forecasts[i] = dayForecast;
        }
        this.populateForecastList();
    }
    
    protected void updateDayChange(final ClimateManager climateManager) {
        final DayForecast dayForecast = this.forecasts[0];
        for (int i = 0; i < this.forecasts.length; ++i) {
            if (i > 0 && i < this.forecasts.length) {
                this.forecasts[i].indexOffset = i - 1 - 10;
                this.forecasts[i - 1] = this.forecasts[i];
            }
        }
        dayForecast.reset();
        this.sampleDay(climateManager, dayForecast, this.forecasts.length - 1 - 10);
        dayForecast.indexOffset = this.forecasts.length - 1 - 10;
        this.forecasts[this.forecasts.length - 1] = dayForecast;
        this.populateForecastList();
    }
    
    protected void sampleDay(final ClimateManager climateManager, final DayForecast dayForecast, final int amount) {
        final GameTime instance = GameTime.getInstance();
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(instance.getYear(), instance.getMonth(), instance.getDayPlusOne(), 0, 0);
        gregorianCalendar.add(5, amount);
        boolean b = true;
        dayForecast.weatherOverlap = this.getWeatherOverlap(amount + 10, 0.0f);
        dayForecast.weatherPeriod.stopWeatherPeriod();
        dayForecast.name = invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gregorianCalendar.get(1), gregorianCalendar.get(2) + 1, gregorianCalendar.get(5));
        for (int i = 0; i < 24; ++i) {
            if (i != 0) {
                gregorianCalendar.add(11, 1);
            }
            this.climateValues.pollDate(gregorianCalendar);
            if (i == 0) {
                b = (this.climateValues.getNoiseAirmass() >= 0.0f);
                dayForecast.airFrontString = (b ? "WARM" : "COLD");
                dayForecast.dawn = this.climateValues.getDawn();
                dayForecast.dusk = this.climateValues.getDusk();
                dayForecast.dayLightHours = dayForecast.dusk - dayForecast.dawn;
            }
            if (!dayForecast.weatherStarts && ((b && this.climateValues.getNoiseAirmass() < 0.0f) || (!b && this.climateValues.getNoiseAirmass() >= 0.0f))) {
                dayForecast.airFront.setFrontType((this.climateValues.getNoiseAirmass() >= 0.0f) ? -1 : 1);
                climateManager.CalculateWeatherFrontStrength(gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5), dayForecast.airFront);
                dayForecast.airFront.setFrontWind(this.climateValues.getWindAngleDegrees());
                if (dayForecast.airFront.getStrength() >= 0.1f) {
                    final DayForecast weatherOverlap = this.getWeatherOverlap(amount + 10, (float)i);
                    if (((weatherOverlap != null) ? weatherOverlap.weatherPeriod.getTotalStrength() : -1.0f) < 0.1f) {
                        dayForecast.weatherStarts = true;
                        dayForecast.weatherStartTime = (float)i;
                        dayForecast.weatherPeriod.init(dayForecast.airFront, this.climateValues.getCacheWorldAgeHours(), gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5));
                    }
                }
                if (!dayForecast.weatherStarts) {
                    b = !b;
                }
            }
            final boolean b2 = i > this.climateValues.getDawn() && i <= this.climateValues.getDusk();
            float temperature = this.climateValues.getTemperature();
            final float humidity = this.climateValues.getHumidity();
            float n = this.climateValues.getWindAngleDegrees();
            float windIntensity = this.climateValues.getWindIntensity();
            float cloudIntensity = this.climateValues.getCloudIntensity();
            if (dayForecast.weatherStarts || dayForecast.weatherOverlap != null) {
                final WeatherPeriod weatherPeriod = dayForecast.weatherStarts ? dayForecast.weatherPeriod : dayForecast.weatherOverlap.weatherPeriod;
                Label_0906: {
                    if (weatherPeriod != null) {
                        n = weatherPeriod.getWindAngleDegrees();
                        final WeatherPeriod.WeatherStage stageForWorldAge = weatherPeriod.getStageForWorldAge(this.climateValues.getCacheWorldAgeHours());
                        if (stageForWorldAge != null) {
                            if (!dayForecast.weatherStages.contains(stageForWorldAge.getStageID())) {
                                dayForecast.weatherStages.add(stageForWorldAge.getStageID());
                            }
                            switch (stageForWorldAge.getStageID()) {
                                case 7: {
                                    dayForecast.chanceOnSnow = true;
                                    windIntensity = 0.75f + 0.25f * weatherPeriod.getTotalStrength();
                                    temperature -= WeatherPeriod.getMaxTemperatureInfluence() * windIntensity;
                                    cloudIntensity = 0.5f + 0.5f * windIntensity;
                                    dayForecast.hasBlizzard = true;
                                    break Label_0906;
                                }
                                case 2: {
                                    windIntensity = 0.5f * weatherPeriod.getTotalStrength();
                                    temperature -= WeatherPeriod.getMaxTemperatureInfluence() * windIntensity;
                                    cloudIntensity = 0.5f + 0.5f * windIntensity;
                                    dayForecast.hasHeavyRain = true;
                                    break Label_0906;
                                }
                                case 3: {
                                    windIntensity = 0.2f + 0.5f * weatherPeriod.getTotalStrength();
                                    temperature -= WeatherPeriod.getMaxTemperatureInfluence() * windIntensity;
                                    cloudIntensity = 0.5f + 0.5f * windIntensity;
                                    dayForecast.hasStorm = true;
                                    break Label_0906;
                                }
                                case 8: {
                                    windIntensity = 0.4f + 0.6f * weatherPeriod.getTotalStrength();
                                    temperature -= WeatherPeriod.getMaxTemperatureInfluence() * windIntensity;
                                    cloudIntensity = 0.5f + 0.5f * windIntensity;
                                    dayForecast.hasTropicalStorm = true;
                                    break Label_0906;
                                }
                                case 1: {
                                    dayForecast.hasHeavyRain = true;
                                    break;
                                }
                            }
                            temperature -= WeatherPeriod.getMaxTemperatureInfluence() * 0.25f;
                            cloudIntensity = 0.35f + 0.5f * weatherPeriod.getTotalStrength();
                        }
                        else if (dayForecast.weatherOverlap != null && i < dayForecast.weatherEndTime) {
                            dayForecast.weatherEndTime = (float)i;
                        }
                    }
                }
                if (temperature < 0.0f) {
                    dayForecast.chanceOnSnow = true;
                }
            }
            dayForecast.temperature.add(temperature, b2);
            dayForecast.humidity.add(humidity, b2);
            dayForecast.windDirection.add(n, b2);
            dayForecast.windPower.add(windIntensity, b2);
            dayForecast.cloudiness.add(cloudIntensity, b2);
        }
        dayForecast.temperature.calculate();
        dayForecast.humidity.calculate();
        dayForecast.windDirection.calculate();
        dayForecast.windPower.calculate();
        dayForecast.cloudiness.calculate();
        dayForecast.hasFog = this.climateValues.isDayDoFog();
        dayForecast.fogStrength = this.climateValues.getDayFogStrength();
        dayForecast.fogDuration = this.climateValues.getDayFogDuration();
    }
    
    private DayForecast getWeatherOverlap(final int n, final float n2) {
        final int max = Math.max(0, n - 10);
        if (max == n) {
            return null;
        }
        for (int i = max; i < n; ++i) {
            if (this.forecasts[i].weatherStarts && i + this.forecasts[i].weatherStartTime / 24.0f + (float)this.forecasts[i].weatherPeriod.getDuration() / 24.0f > n + n2 / 24.0f) {
                return this.forecasts[i];
            }
        }
        return null;
    }
    
    public int getDaysTillFirstWeather() {
        int n = -1;
        for (int i = 10; i < this.forecasts.length - 1; ++i) {
            if (this.forecasts[i].weatherStarts && n < 0) {
                n = i;
            }
        }
        return n;
    }
    
    public static class ForecastValue
    {
        private float dayMin;
        private float dayMax;
        private float dayMean;
        private int dayMeanTicks;
        private float nightMin;
        private float nightMax;
        private float nightMean;
        private int nightMeanTicks;
        private float totalMin;
        private float totalMax;
        private float totalMean;
        private int totalMeanTicks;
        
        public ForecastValue() {
            this.reset();
        }
        
        public float getDayMin() {
            return this.dayMin;
        }
        
        public float getDayMax() {
            return this.dayMax;
        }
        
        public float getDayMean() {
            return this.dayMean;
        }
        
        public float getNightMin() {
            return this.nightMin;
        }
        
        public float getNightMax() {
            return this.nightMax;
        }
        
        public float getNightMean() {
            return this.nightMean;
        }
        
        public float getTotalMin() {
            return this.totalMin;
        }
        
        public float getTotalMax() {
            return this.totalMax;
        }
        
        public float getTotalMean() {
            return this.totalMean;
        }
        
        protected void add(final float n, final boolean b) {
            if (b) {
                if (n < this.dayMin) {
                    this.dayMin = n;
                }
                if (n > this.dayMax) {
                    this.dayMax = n;
                }
                this.dayMean += n;
                ++this.dayMeanTicks;
            }
            else {
                if (n < this.nightMin) {
                    this.nightMin = n;
                }
                if (n > this.nightMax) {
                    this.nightMax = n;
                }
                this.nightMean += n;
                ++this.nightMeanTicks;
            }
            if (n < this.totalMin) {
                this.totalMin = n;
            }
            if (n > this.totalMax) {
                this.totalMax = n;
            }
            this.totalMean += n;
            ++this.totalMeanTicks;
        }
        
        protected void calculate() {
            if (this.totalMeanTicks <= 0) {
                this.totalMean = 0.0f;
            }
            else {
                this.totalMean /= this.totalMeanTicks;
            }
            if (this.dayMeanTicks <= 0) {
                this.dayMin = this.totalMin;
                this.dayMax = this.totalMax;
                this.dayMean = this.totalMean;
            }
            else {
                this.dayMean /= this.dayMeanTicks;
            }
            if (this.nightMeanTicks <= 0) {
                this.nightMin = this.totalMin;
                this.nightMax = this.totalMax;
                this.nightMean = this.totalMean;
            }
            else {
                this.nightMean /= this.nightMeanTicks;
            }
        }
        
        protected void reset() {
            this.dayMin = 10000.0f;
            this.dayMax = -10000.0f;
            this.dayMean = 0.0f;
            this.dayMeanTicks = 0;
            this.nightMin = 10000.0f;
            this.nightMax = -10000.0f;
            this.nightMean = 0.0f;
            this.nightMeanTicks = 0;
            this.totalMin = 10000.0f;
            this.totalMax = -10000.0f;
            this.totalMean = 0.0f;
            this.totalMeanTicks = 0;
        }
    }
    
    public static class DayForecast
    {
        private int indexOffset;
        private String name;
        private WeatherPeriod weatherPeriod;
        private ForecastValue temperature;
        private ForecastValue humidity;
        private ForecastValue windDirection;
        private ForecastValue windPower;
        private ForecastValue cloudiness;
        private boolean weatherStarts;
        private float weatherStartTime;
        private float weatherEndTime;
        private boolean chanceOnSnow;
        private String airFrontString;
        private boolean hasFog;
        private float fogStrength;
        private float fogDuration;
        private ClimateManager.AirFront airFront;
        private DayForecast weatherOverlap;
        private boolean hasHeavyRain;
        private boolean hasStorm;
        private boolean hasTropicalStorm;
        private boolean hasBlizzard;
        private float dawn;
        private float dusk;
        private float dayLightHours;
        private ArrayList<Integer> weatherStages;
        
        public DayForecast() {
            this.indexOffset = 0;
            this.name = "Day x";
            this.temperature = new ForecastValue();
            this.humidity = new ForecastValue();
            this.windDirection = new ForecastValue();
            this.windPower = new ForecastValue();
            this.cloudiness = new ForecastValue();
            this.weatherStarts = false;
            this.weatherStartTime = 0.0f;
            this.weatherEndTime = 24.0f;
            this.chanceOnSnow = false;
            this.airFrontString = "";
            this.hasFog = false;
            this.fogStrength = 0.0f;
            this.fogDuration = 0.0f;
            this.hasHeavyRain = false;
            this.hasStorm = false;
            this.hasTropicalStorm = false;
            this.hasBlizzard = false;
            this.dawn = 0.0f;
            this.dusk = 0.0f;
            this.dayLightHours = 0.0f;
            this.weatherStages = new ArrayList<Integer>();
        }
        
        public int getIndexOffset() {
            return this.indexOffset;
        }
        
        public String getName() {
            return this.name;
        }
        
        public ForecastValue getTemperature() {
            return this.temperature;
        }
        
        public ForecastValue getHumidity() {
            return this.humidity;
        }
        
        public ForecastValue getWindDirection() {
            return this.windDirection;
        }
        
        public ForecastValue getWindPower() {
            return this.windPower;
        }
        
        public ForecastValue getCloudiness() {
            return this.cloudiness;
        }
        
        public WeatherPeriod getWeatherPeriod() {
            return this.weatherPeriod;
        }
        
        public boolean isWeatherStarts() {
            return this.weatherStarts;
        }
        
        public float getWeatherStartTime() {
            return this.weatherStartTime;
        }
        
        public float getWeatherEndTime() {
            return this.weatherEndTime;
        }
        
        public boolean isChanceOnSnow() {
            return this.chanceOnSnow;
        }
        
        public String getAirFrontString() {
            return this.airFrontString;
        }
        
        public boolean isHasFog() {
            return this.hasFog;
        }
        
        public ClimateManager.AirFront getAirFront() {
            return this.airFront;
        }
        
        public DayForecast getWeatherOverlap() {
            return this.weatherOverlap;
        }
        
        public String getMeanWindAngleString() {
            return ClimateManager.getWindAngleString(this.windDirection.getTotalMean());
        }
        
        public float getFogStrength() {
            return this.fogStrength;
        }
        
        public float getFogDuration() {
            return this.fogDuration;
        }
        
        public boolean isHasHeavyRain() {
            return this.hasHeavyRain;
        }
        
        public boolean isHasStorm() {
            return this.hasStorm;
        }
        
        public boolean isHasTropicalStorm() {
            return this.hasTropicalStorm;
        }
        
        public boolean isHasBlizzard() {
            return this.hasBlizzard;
        }
        
        public ArrayList<Integer> getWeatherStages() {
            return this.weatherStages;
        }
        
        public float getDawn() {
            return this.dawn;
        }
        
        public float getDusk() {
            return this.dusk;
        }
        
        public float getDayLightHours() {
            return this.dayLightHours;
        }
        
        private void reset() {
            this.weatherPeriod.stopWeatherPeriod();
            this.temperature.reset();
            this.humidity.reset();
            this.windDirection.reset();
            this.windPower.reset();
            this.cloudiness.reset();
            this.weatherStarts = false;
            this.weatherStartTime = 0.0f;
            this.weatherEndTime = 24.0f;
            this.chanceOnSnow = false;
            this.hasFog = false;
            this.fogStrength = 0.0f;
            this.fogDuration = 0.0f;
            this.weatherOverlap = null;
            this.hasHeavyRain = false;
            this.hasStorm = false;
            this.hasTropicalStorm = false;
            this.hasBlizzard = false;
            this.weatherStages.clear();
        }
    }
}
