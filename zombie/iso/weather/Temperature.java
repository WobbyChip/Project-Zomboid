// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.core.Color;

public class Temperature
{
    public static boolean DO_DEFAULT_BASE;
    public static boolean DO_DAYLEN_MOD;
    public static String CELSIUS_POSTFIX;
    public static String FAHRENHEIT_POSTFIX;
    public static final float skinCelciusMin = 20.0f;
    public static final float skinCelciusFavorable = 33.0f;
    public static final float skinCelciusMax = 42.0f;
    public static final float homeostasisDefault = 37.0f;
    public static final float FavorableNakedTemp = 27.0f;
    public static final float FavorableRoomTemp = 22.0f;
    public static final float coreCelciusMin = 20.0f;
    public static final float coreCelciusMax = 42.0f;
    public static final float neutralZone = 27.0f;
    public static final float Hypothermia_1 = 36.5f;
    public static final float Hypothermia_2 = 35.0f;
    public static final float Hypothermia_3 = 30.0f;
    public static final float Hypothermia_4 = 25.0f;
    public static final float Hyperthermia_1 = 37.5f;
    public static final float Hyperthermia_2 = 39.0f;
    public static final float Hyperthermia_3 = 40.0f;
    public static final float Hyperthermia_4 = 41.0f;
    public static final float TrueInsulationMultiplier = 2.0f;
    public static final float TrueWindresistMultiplier = 1.0f;
    public static final float BodyMinTemp = 20.0f;
    public static final float BodyMaxTemp = 42.0f;
    private static String cacheTempString;
    private static float cacheTemp;
    private static Color tempColor;
    private static Color col_0;
    private static Color col_25;
    private static Color col_50;
    private static Color col_75;
    private static Color col_100;
    
    public static String getCelsiusPostfix() {
        return Temperature.CELSIUS_POSTFIX;
    }
    
    public static String getFahrenheitPostfix() {
        return Temperature.FAHRENHEIT_POSTFIX;
    }
    
    public static String getTemperaturePostfix() {
        return Core.OptionTemperatureDisplayCelsius ? Temperature.CELSIUS_POSTFIX : Temperature.FAHRENHEIT_POSTFIX;
    }
    
    public static String getTemperatureString(final float n) {
        final float cacheTemp = Math.round((Core.OptionTemperatureDisplayCelsius ? n : CelsiusToFahrenheit(n)) * 10.0f) / 10.0f;
        if (Temperature.cacheTemp != cacheTemp) {
            Temperature.cacheTemp = cacheTemp;
            Temperature.cacheTempString = invokedynamic(makeConcatWithConstants:(FLjava/lang/String;)Ljava/lang/String;, cacheTemp, getTemperaturePostfix());
        }
        return Temperature.cacheTempString;
    }
    
    public static float CelsiusToFahrenheit(final float n) {
        return n * 1.8f + 32.0f;
    }
    
    public static float FahrenheitToCelsius(final float n) {
        return (n - 32.0f) / 1.8f;
    }
    
    public static float WindchillCelsiusKph(final float n, final float n2) {
        final float n3 = 13.12f + 0.6215f * n - 11.37f * (float)Math.pow(n2, 0.1599999964237213) + 0.3965f * n * (float)Math.pow(n2, 0.1599999964237213);
        return (n3 < n) ? n3 : n;
    }
    
    public static float getTrueInsulationValue(final float n) {
        return n * 2.0f + 0.5f * n * n * n;
    }
    
    public static float getTrueWindresistanceValue(final float n) {
        return n * 1.0f + 0.5f * n * n;
    }
    
    public static void reset() {
    }
    
    public static float getFractionForRealTimeRatePerMin(final float n) {
        if (Temperature.DO_DEFAULT_BASE) {
            return n / (1440.0f / SandboxOptions.instance.getDayLengthMinutesDefault());
        }
        if (!Temperature.DO_DAYLEN_MOD) {
            return n / (1440.0f / SandboxOptions.instance.getDayLengthMinutes());
        }
        float n2 = SandboxOptions.instance.getDayLengthMinutes() / (float)SandboxOptions.instance.getDayLengthMinutesDefault();
        if (n2 < 1.0f) {
            n2 = 0.5f + 0.5f * n2;
        }
        else if (n2 > 1.0f) {
            n2 = 1.0f + n2 / 16.0f;
        }
        return n / (1440.0f / SandboxOptions.instance.getDayLengthMinutes()) * n2;
    }
    
    public static Color getValueColor(float clamp) {
        clamp = ClimateManager.clamp(0.0f, 1.0f, clamp);
        Temperature.tempColor.set(0.0f, 0.0f, 0.0f, 1.0f);
        if (clamp < 0.25f) {
            Temperature.col_0.interp(Temperature.col_25, clamp / 0.25f, Temperature.tempColor);
        }
        else if (clamp < 0.5f) {
            Temperature.col_25.interp(Temperature.col_50, (clamp - 0.25f) / 0.25f, Temperature.tempColor);
        }
        else if (clamp < 0.75f) {
            Temperature.col_50.interp(Temperature.col_75, (clamp - 0.5f) / 0.25f, Temperature.tempColor);
        }
        else {
            Temperature.col_75.interp(Temperature.col_100, (clamp - 0.75f) / 0.25f, Temperature.tempColor);
        }
        return Temperature.tempColor;
    }
    
    public static float getWindChillAmountForPlayer(final IsoPlayer isoPlayer) {
        if (isoPlayer.getVehicle() != null || (isoPlayer.getSquare() != null && isoPlayer.getSquare().isInARoom())) {
            return 0.0f;
        }
        final ClimateManager instance = ClimateManager.getInstance();
        final float airTemperatureForCharacter = instance.getAirTemperatureForCharacter(isoPlayer, true);
        float n = 0.0f;
        if (airTemperatureForCharacter < instance.getTemperature()) {
            n = instance.getTemperature() - airTemperatureForCharacter;
        }
        return n;
    }
    
    static {
        Temperature.DO_DEFAULT_BASE = false;
        Temperature.DO_DAYLEN_MOD = true;
        Temperature.CELSIUS_POSTFIX = "°C";
        Temperature.FAHRENHEIT_POSTFIX = "°F";
        Temperature.cacheTempString = "";
        Temperature.cacheTemp = -9000.0f;
        Temperature.tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        Temperature.col_0 = new Color(29, 34, 237);
        Temperature.col_25 = new Color(0, 255, 234);
        Temperature.col_50 = new Color(84, 255, 55);
        Temperature.col_75 = new Color(255, 246, 0);
        Temperature.col_100 = new Color(255, 0, 0);
    }
}
