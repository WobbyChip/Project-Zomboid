// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.debug.DebugLog;

public class ClimateMoon
{
    private static final int[] day_year;
    private static final String[] moon_phase_name;
    private static final float[] units;
    private static int last_year;
    private static int last_month;
    private static int last_day;
    private static int current_phase;
    private static float current_float;
    private static ClimateMoon instance;
    
    public static ClimateMoon getInstance() {
        return ClimateMoon.instance;
    }
    
    public static void updatePhase(final int last_year, final int last_month, final int last_day) {
        if (last_year != ClimateMoon.last_year || last_month != ClimateMoon.last_month || last_day != ClimateMoon.last_day) {
            ClimateMoon.last_year = last_year;
            ClimateMoon.last_month = last_month;
            ClimateMoon.last_day = last_day;
            ClimateMoon.current_phase = getMoonPhase(last_year, last_month, last_day);
            if (ClimateMoon.current_phase > 7) {
                ClimateMoon.current_phase = 7;
            }
            if (ClimateMoon.current_phase < 0) {
                ClimateMoon.current_phase = 0;
            }
            ClimateMoon.current_float = ClimateMoon.units[ClimateMoon.current_phase];
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;FI)Ljava/lang/String;, getPhaseName(), ClimateMoon.current_float, ClimateMoon.current_phase));
        }
    }
    
    public static String getPhaseName() {
        return ClimateMoon.moon_phase_name[ClimateMoon.current_phase];
    }
    
    public static float getMoonFloat() {
        return ClimateMoon.current_float;
    }
    
    public int getCurrentMoonPhase() {
        return ClimateMoon.current_phase;
    }
    
    private static int getMoonPhase(final int n, int n2, final int n3) {
        if (n2 < 0 || n2 > 12) {
            n2 = 0;
        }
        int n4 = n3 + ClimateMoon.day_year[n2];
        if (n2 > 2 && isLeapYearP(n)) {
            ++n4;
        }
        final int n5 = n / 100 + 1;
        final int n6 = n % 19 + 1;
        int n7 = (11 * n6 + 20 + (8 * n5 + 5) / 25 - 5 - (3 * n5 / 4 - 12)) % 30;
        if (n7 <= 0) {
            n7 += 30;
        }
        if ((n7 == 25 && n6 > 11) || n7 == 24) {
            ++n7;
        }
        return ((n4 + n7) * 6 + 11) % 177 / 22 & 0x7;
    }
    
    private static int daysInMonth(final int n, final int n2) {
        int n3 = 31;
        switch (n) {
            case 4:
            case 6:
            case 9:
            case 11: {
                n3 = 30;
                break;
            }
            case 2: {
                n3 = (isLeapYearP(n2) ? 29 : 28);
                break;
            }
        }
        return n3;
    }
    
    private static boolean isLeapYearP(final int n) {
        return n % 4 == 0 && (n % 400 == 0 || n % 100 != 0);
    }
    
    static {
        day_year = new int[] { -1, -1, 30, 58, 89, 119, 150, 180, 211, 241, 272, 303, 333 };
        moon_phase_name = new String[] { "New", "Waxing crescent", "First quarter", "Waxing gibbous", "Full", "Waning gibbous", "Third quarter", "Waning crescent" };
        units = new float[] { 0.0f, 0.25f, 0.5f, 0.75f, 1.0f, 0.75f, 0.5f, 0.25f };
        ClimateMoon.current_phase = 0;
        ClimateMoon.current_float = 0.0f;
        ClimateMoon.instance = new ClimateMoon();
    }
}
