// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.ui.SpeedControls;
import zombie.core.math.PZMath;
import zombie.core.logger.ExceptionLogger;
import java.io.ByteArrayInputStream;
import zombie.iso.SliceY;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.Lua.LuaManager;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import zombie.debug.DebugOptions;
import zombie.core.PerformanceSettings;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import zombie.radio.ZomboidRadio;
import zombie.erosion.ErosionMain;
import zombie.iso.weather.ClimateManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIManager;
import zombie.network.ServerOptions;
import zombie.characters.IsoZombie;
import zombie.iso.IsoWorld;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import java.util.concurrent.TimeUnit;
import zombie.network.GameClient;
import zombie.network.GameServer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.util.PZCalendar;

public final class GameTime
{
    public static GameTime instance;
    public static final float MULTIPLIER = 0.8f;
    private static long serverTimeShift;
    private static boolean serverTimeShiftIsSet;
    private static boolean isUTest;
    public float TimeOfDay;
    public int NightsSurvived;
    public PZCalendar Calender;
    public float FPSMultiplier;
    public float Moon;
    public float ServerTimeOfDay;
    public float ServerLastTimeOfDay;
    public int ServerNewDays;
    public float lightSourceUpdate;
    public float multiplierBias;
    public float LastLastTimeOfDay;
    private int HelicopterTime1Start;
    public float PerObjectMultiplier;
    private int HelicopterTime1End;
    private int HelicopterDay1;
    private float Ambient;
    private float AmbientMax;
    private float AmbientMin;
    private int Day;
    private int StartDay;
    private float MaxZombieCountStart;
    private float MinZombieCountStart;
    private float MaxZombieCount;
    private float MinZombieCount;
    private int Month;
    private int StartMonth;
    private float StartTimeOfDay;
    private float ViewDistMax;
    private float ViewDistMin;
    private int Year;
    private int StartYear;
    private double HoursSurvived;
    private float MinutesPerDayStart;
    private float MinutesPerDay;
    private float LastTimeOfDay;
    private int TargetZombies;
    private boolean RainingToday;
    private boolean bGunFireEventToday;
    private float[] GunFireTimes;
    private int NumGunFireEvents;
    private long lastPing;
    private long lastClockSync;
    private KahluaTable table;
    private int minutesMod;
    private boolean thunderDay;
    private boolean randomAmbientToday;
    private float Multiplier;
    private int dusk;
    private int dawn;
    private float NightMin;
    private float NightMax;
    private long minutesStamp;
    private long previousMinuteStamp;
    
    public GameTime() {
        this.TimeOfDay = 9.0f;
        this.NightsSurvived = 0;
        this.FPSMultiplier = 1.0f;
        this.Moon = 0.0f;
        this.lightSourceUpdate = 0.0f;
        this.multiplierBias = 1.0f;
        this.LastLastTimeOfDay = 0.0f;
        this.HelicopterTime1Start = 0;
        this.PerObjectMultiplier = 1.0f;
        this.HelicopterTime1End = 0;
        this.HelicopterDay1 = 0;
        this.Ambient = 0.9f;
        this.AmbientMax = 1.0f;
        this.AmbientMin = 0.24f;
        this.Day = 22;
        this.StartDay = 22;
        this.MaxZombieCountStart = 750.0f;
        this.MinZombieCountStart = 750.0f;
        this.MaxZombieCount = 750.0f;
        this.MinZombieCount = 750.0f;
        this.Month = 7;
        this.StartMonth = 7;
        this.StartTimeOfDay = 9.0f;
        this.ViewDistMax = 42.0f;
        this.ViewDistMin = 19.0f;
        this.Year = 2012;
        this.StartYear = 2012;
        this.HoursSurvived = 0.0;
        this.MinutesPerDayStart = 30.0f;
        this.MinutesPerDay = this.MinutesPerDayStart;
        this.TargetZombies = (int)this.MinZombieCountStart;
        this.RainingToday = true;
        this.bGunFireEventToday = false;
        this.GunFireTimes = new float[5];
        this.NumGunFireEvents = 1;
        this.lastPing = 0L;
        this.lastClockSync = 0L;
        this.table = null;
        this.minutesMod = -1;
        this.thunderDay = true;
        this.randomAmbientToday = true;
        this.Multiplier = 1.0f;
        this.dusk = 3;
        this.dawn = 12;
        this.NightMin = 0.0f;
        this.NightMax = 1.0f;
        this.minutesStamp = 0L;
        this.previousMinuteStamp = 0L;
    }
    
    public static GameTime getInstance() {
        return GameTime.instance;
    }
    
    public static void setInstance(final GameTime instance) {
        GameTime.instance = instance;
    }
    
    public static void syncServerTime(final long n, final long n2, final long n3) {
        final long serverTimeShift = n2 - n3 + (n3 - n) / 2L;
        final long serverTimeShift2 = GameTime.serverTimeShift;
        if (!GameTime.serverTimeShiftIsSet) {
            GameTime.serverTimeShift = serverTimeShift;
        }
        else {
            GameTime.serverTimeShift += (long)((serverTimeShift - GameTime.serverTimeShift) * 0.05f);
        }
        if (Math.abs(GameTime.serverTimeShift - serverTimeShift2) > 10000000L) {
            sendTimeSync();
        }
        else {
            GameTime.serverTimeShiftIsSet = true;
        }
    }
    
    public static long getServerTime() {
        if (GameTime.isUTest) {
            return System.nanoTime() + GameTime.serverTimeShift;
        }
        if (GameServer.bServer) {
            return System.nanoTime();
        }
        if (!GameClient.bClient) {
            return 0L;
        }
        if (!GameTime.serverTimeShiftIsSet) {
            return 0L;
        }
        return System.nanoTime() + GameTime.serverTimeShift;
    }
    
    public static long getServerTimeMills() {
        return TimeUnit.NANOSECONDS.toMillis(getServerTime());
    }
    
    public static boolean getServerTimeShiftIsSet() {
        return GameTime.serverTimeShiftIsSet;
    }
    
    public static void setServerTimeShift(final long serverTimeShift) {
        GameTime.isUTest = true;
        GameTime.serverTimeShift = serverTimeShift;
        GameTime.serverTimeShiftIsSet = true;
    }
    
    private static void sendTimeSync() {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.TimeSync.doPacket(startPacket);
        startPacket.putLong(System.nanoTime());
        startPacket.putLong(0L);
        PacketTypes.PacketType.TimeSync.send(GameClient.connection);
    }
    
    public static void receiveTimeSync(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        if (GameServer.bServer) {
            final long long1 = byteBuffer.getLong();
            final long nanoTime = System.nanoTime();
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.TimeSync.doPacket(startPacket);
            startPacket.putLong(long1);
            startPacket.putLong(nanoTime);
            PacketTypes.PacketType.TimeSync.send(udpConnection);
        }
        if (GameClient.bClient) {
            syncServerTime(byteBuffer.getLong(), byteBuffer.getLong(), System.nanoTime());
            DebugLog.printServerTime = true;
        }
    }
    
    public float getRealworldSecondsSinceLastUpdate() {
        return 0.016666668f * this.FPSMultiplier;
    }
    
    public float getMultipliedSecondsSinceLastUpdate() {
        return 0.016666668f * this.getUnmoddedMultiplier();
    }
    
    public float getGameWorldSecondsSinceLastUpdate() {
        return this.getTimeDelta() * (1440.0f / this.getMinutesPerDay());
    }
    
    public int daysInMonth(final int n, final int n2) {
        if (this.Calender == null) {
            this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
        }
        final int[] array2;
        final int[] array = array2 = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        final int n3 = 1;
        array2[n3] += (this.getCalender().isLeapYear(n) ? 1 : 0);
        return array[n2];
    }
    
    public String getDeathString(final IsoPlayer isoPlayer) {
        return Translator.getText("IGUI_Gametime_SurvivedFor", this.getTimeSurvived(isoPlayer));
    }
    
    public int getDaysSurvived() {
        float max = 0.0f;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                max = Math.max(max, (float)isoPlayer.getHoursSurvived());
            }
        }
        return (int)max / 24 % 30;
    }
    
    public String getTimeSurvived(final IsoPlayer isoPlayer) {
        String s = "";
        final float n = (float)isoPlayer.getHoursSurvived();
        final Integer value = (int)n % 24;
        final Integer value2 = (int)n / 24;
        final Integer value3 = value2 / 30;
        final Integer value4 = value2 % 30;
        final Integer value5 = value3 / 12;
        final Integer value6 = value3 % 12;
        String s2 = Translator.getText("IGUI_Gametime_day");
        String s3 = Translator.getText("IGUI_Gametime_year");
        String s4 = Translator.getText("IGUI_Gametime_hour");
        String s5 = Translator.getText("IGUI_Gametime_month");
        if (value5 != 0) {
            if (value5 > 1) {
                s3 = Translator.getText("IGUI_Gametime_years");
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Ljava/lang/String;, s, value5, s3);
        }
        if (value6 != 0) {
            if (value6 > 1) {
                s5 = Translator.getText("IGUI_Gametime_months");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Ljava/lang/String;, s, value6, s5);
        }
        if (value4 != 0) {
            if (value4 > 1) {
                s2 = Translator.getText("IGUI_Gametime_days");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Ljava/lang/String;, s, value4, s2);
        }
        if (value != 0) {
            if (value > 1) {
                s4 = Translator.getText("IGUI_Gametime_hours");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Ljava/lang/String;, s, value, s4);
        }
        if (s.trim().length() == 0) {
            final int n2 = (int)(n * 60.0f);
            s = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;ILjava/lang/String;)Ljava/lang/String;, n2, Translator.getText("IGUI_Gametime_minutes"), (int)(n * 60.0f * 60.0f) - n2 * 60, Translator.getText("IGUI_Gametime_secondes"));
        }
        return s;
    }
    
    public String getZombieKilledText(final IsoPlayer isoPlayer) {
        final int zombieKills = isoPlayer.getZombieKills();
        if (zombieKills == 0 || zombieKills > 1) {
            return Translator.getText("IGUI_Gametime_zombiesCount", zombieKills);
        }
        if (zombieKills == 1) {
            return Translator.getText("IGUI_Gametime_zombieCount", zombieKills);
        }
        return null;
    }
    
    public String getGameModeText() {
        String s = Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.GameMode));
        if (s == null) {
            s = Core.GameMode;
        }
        String textOrNull = Translator.getTextOrNull("IGUI_Gametime_GameMode", s);
        if (textOrNull == null) {
            textOrNull = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        if (Core.bDebug) {
            textOrNull = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, textOrNull);
        }
        return textOrNull;
    }
    
    public void init() {
        this.setDay(this.getStartDay());
        this.setTimeOfDay(this.getStartTimeOfDay());
        this.setMonth(this.getStartMonth());
        this.setYear(this.getStartYear());
        if (SandboxOptions.instance.Helicopter.getValue() != 1) {
            this.HelicopterDay1 = Rand.Next(6, 10);
            this.HelicopterTime1Start = Rand.Next(9, 19);
            this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
        }
        this.setMinutesStamp();
    }
    
    public float Lerp(final float n, final float n2, float n3) {
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n3 >= 1.0f) {
            n3 = 1.0f;
        }
        return n + (n2 - n) * n3;
    }
    
    public void RemoveZombiesIndiscriminate(int n) {
        if (n == 0) {
            return;
        }
        for (int i = 0; i < IsoWorld.instance.CurrentCell.getZombieList().size(); ++i) {
            final IsoZombie isoZombie = IsoWorld.instance.CurrentCell.getZombieList().get(0);
            IsoWorld.instance.CurrentCell.getZombieList().remove(i);
            IsoWorld.instance.CurrentCell.getRemoveList().add(isoZombie);
            isoZombie.getCurrentSquare().getMovingObjects().remove(isoZombie);
            --i;
            if (--n == 0 || IsoWorld.instance.CurrentCell.getZombieList().isEmpty()) {
                return;
            }
        }
    }
    
    public float TimeLerp(final float n, final float n2, float n3, float n4) {
        float timeOfDay = getInstance().getTimeOfDay();
        if (n4 < n3) {
            n4 += 24.0f;
        }
        boolean b = false;
        if ((timeOfDay > n4 && timeOfDay > n3) || (timeOfDay < n4 && timeOfDay < n3)) {
            n3 += 24.0f;
            b = true;
            final float n5 = n3;
            n3 = n4;
            n4 = n5;
            if (timeOfDay < n3) {
                timeOfDay += 24.0f;
            }
        }
        final float n6 = n4 - n3;
        final float n7 = timeOfDay - n3;
        float n8 = 0.0f;
        if (n7 > n6) {
            n8 = 1.0f;
        }
        if (n7 < n6 && n7 > 0.0f) {
            n8 = n7 / n6;
        }
        if (GameClient.bClient) {
            n8 *= (float)ServerOptions.instance.nightlengthmodifier.getValue();
        }
        if (b) {
            n8 = 1.0f - n8;
        }
        final float a = (n8 - 0.5f) * 2.0f;
        float n9;
        if (a < 0.0) {
            n9 = -1.0f;
        }
        else {
            n9 = 1.0f;
        }
        return this.Lerp(n, n2, (1.0f - (float)Math.pow(1.0f - Math.abs(a), 8.0)) * n9 * 0.5f + 0.5f);
    }
    
    public float getDeltaMinutesPerDay() {
        return this.MinutesPerDayStart / this.MinutesPerDay;
    }
    
    public float getNightMin() {
        return 1.0f - this.NightMin;
    }
    
    public void setNightMin(final float n) {
        this.NightMin = 1.0f - n;
    }
    
    public float getNightMax() {
        return 1.0f - this.NightMax;
    }
    
    public void setNightMax(final float n) {
        this.NightMax = 1.0f - n;
    }
    
    public int getMinutes() {
        return (int)((this.getTimeOfDay() - (int)this.getTimeOfDay()) * 60.0f);
    }
    
    public void setMoon(final float moon) {
        this.Moon = moon;
    }
    
    public void update(final boolean b) {
        final long currentTimeMillis = System.currentTimeMillis();
        if (GameClient.bClient && (this.lastPing == 0L || currentTimeMillis - this.lastPing > ServerOptions.instance.PingFrequency.getValue() * 1000)) {
            sendTimeSync();
            this.lastPing = currentTimeMillis;
        }
        int n = 9000;
        if (SandboxOptions.instance.MetaEvent.getValue() == 1) {
            n = -1;
        }
        if (SandboxOptions.instance.MetaEvent.getValue() == 3) {
            n = 6000;
        }
        if (!GameClient.bClient && this.randomAmbientToday && n != -1 && Rand.Next(Rand.AdjustForFramerate(n)) == 0 && !isGamePaused()) {
            AmbientStreamManager.instance.addRandomAmbient();
            this.randomAmbientToday = (SandboxOptions.instance.MetaEvent.getValue() == 3 && Rand.Next(3) == 0);
        }
        if (GameServer.bServer && UIManager.getSpeedControls() != null) {
            UIManager.getSpeedControls().SetCurrentGameSpeed(1);
        }
        if (GameServer.bServer || !GameClient.bClient) {
            if (this.bGunFireEventToday) {
                for (int i = 0; i < this.NumGunFireEvents; ++i) {
                    if (this.TimeOfDay > this.GunFireTimes[i] && this.LastLastTimeOfDay < this.GunFireTimes[i]) {
                        AmbientStreamManager.instance.doGunEvent();
                    }
                }
            }
            if (this.NightsSurvived == this.HelicopterDay1 && this.TimeOfDay > this.HelicopterTime1Start && this.TimeOfDay < this.HelicopterTime1End && !IsoWorld.instance.helicopter.isActive() && Rand.Next((int)(800.0f * this.getInvMultiplier())) == 0) {
                this.HelicopterTime1Start += (int)0.5f;
                IsoWorld.instance.helicopter.pickRandomTarget();
            }
            if (this.NightsSurvived > this.HelicopterDay1 && (SandboxOptions.instance.Helicopter.getValue() == 3 || SandboxOptions.instance.Helicopter.getValue() == 4)) {
                if (SandboxOptions.instance.Helicopter.getValue() == 3) {
                    this.HelicopterDay1 = this.NightsSurvived + Rand.Next(10, 16);
                }
                if (SandboxOptions.instance.Helicopter.getValue() == 4) {
                    this.HelicopterDay1 = this.NightsSurvived + Rand.Next(6, 10);
                }
                this.HelicopterTime1Start = Rand.Next(9, 19);
                this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
            }
        }
        final int hour = this.getHour();
        this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (int)this.getTimeOfDay()) * 60.0f));
        final float timeOfDay = this.getTimeOfDay();
        if (!isGamePaused()) {
            float n2 = 1.0f / this.getMinutesPerDay() / 60.0f * this.getMultiplier() / 2.0f;
            if (Core.bLastStand) {
                n2 = 1.0f / this.getMinutesPerDay() / 60.0f * this.getUnmoddedMultiplier() / 2.0f;
            }
            this.setTimeOfDay(this.getTimeOfDay() + n2);
            if (this.getHour() != hour) {
                LuaEventManager.triggerEvent("EveryHours");
            }
            if (!GameServer.bServer) {
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[j];
                    if (isoPlayer != null && isoPlayer.isAlive()) {
                        isoPlayer.setHoursSurvived(isoPlayer.getHoursSurvived() + n2);
                    }
                }
            }
            if (GameServer.bServer) {
                final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
                for (int k = 0; k < players.size(); ++k) {
                    final IsoPlayer isoPlayer2 = players.get(k);
                    isoPlayer2.setHoursSurvived(isoPlayer2.getHoursSurvived() + n2);
                }
            }
            if (GameClient.bClient) {
                final ArrayList<IsoPlayer> players2 = GameClient.instance.getPlayers();
                for (int l = 0; l < players2.size(); ++l) {
                    final IsoPlayer isoPlayer3 = players2.get(l);
                    if (isoPlayer3 != null && !isoPlayer3.isDead()) {
                        if (!isoPlayer3.isLocalPlayer()) {
                            isoPlayer3.setHoursSurvived(isoPlayer3.getHoursSurvived() + n2);
                        }
                    }
                }
            }
            for (int n3 = 0; n3 < IsoPlayer.numPlayers; ++n3) {
                final IsoPlayer isoPlayer4 = IsoPlayer.players[n3];
                if (isoPlayer4 != null) {
                    if (isoPlayer4.isAsleep()) {
                        isoPlayer4.setAsleepTime(isoPlayer4.getAsleepTime() + n2);
                        SleepingEvent.instance.update(isoPlayer4);
                    }
                    else {
                        isoPlayer4.setAsleepTime(0.0f);
                    }
                }
            }
        }
        if (!GameClient.bClient && timeOfDay <= 7.0f && this.getTimeOfDay() > 7.0f) {
            this.setNightsSurvived(this.getNightsSurvived() + 1);
            this.doMetaEvents();
        }
        if (GameClient.bClient) {
            if (this.getTimeOfDay() >= 24.0f) {
                this.setTimeOfDay(this.getTimeOfDay() - 24.0f);
            }
            while (this.ServerNewDays > 0) {
                --this.ServerNewDays;
                this.setDay(this.getDay() + 1);
                if (this.getDay() >= this.daysInMonth(this.getYear(), this.getMonth())) {
                    this.setDay(0);
                    this.setMonth(this.getMonth() + 1);
                    if (this.getMonth() >= 12) {
                        this.setMonth(0);
                        this.setYear(this.getYear() + 1);
                    }
                }
                this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
                LuaEventManager.triggerEvent("EveryDays");
            }
        }
        else if (this.getTimeOfDay() >= 24.0f) {
            this.setTimeOfDay(this.getTimeOfDay() - 24.0f);
            this.setDay(this.getDay() + 1);
            if (this.getDay() >= this.daysInMonth(this.getYear(), this.getMonth())) {
                this.setDay(0);
                this.setMonth(this.getMonth() + 1);
                if (this.getMonth() >= 12) {
                    this.setMonth(0);
                    this.setYear(this.getYear() + 1);
                }
            }
            this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
            LuaEventManager.triggerEvent("EveryDays");
            if (GameServer.bServer) {
                GameServer.syncClock();
                this.lastClockSync = currentTimeMillis;
            }
        }
        final float n4 = this.Moon * 20.0f;
        if (!ClimateManager.getInstance().getThunderStorm().isModifyingNight()) {
            this.setAmbient(this.TimeLerp(this.getAmbientMin(), this.getAmbientMax(), (float)this.getDusk(), (float)this.getDawn()));
        }
        if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
            this.setNightTint(0.0f);
        }
        this.setMinutesStamp();
        final int n5 = (int)((this.getTimeOfDay() - (int)this.getTimeOfDay()) * 60.0f);
        if (n5 / 10 != this.minutesMod) {
            final IsoPlayer[] players3 = IsoPlayer.players;
            for (int n6 = 0; n6 < players3.length; ++n6) {
                final IsoPlayer isoPlayer5 = players3[n6];
                if (isoPlayer5 != null) {
                    isoPlayer5.dirtyRecalcGridStackTime = 1.0f;
                }
            }
            ErosionMain.EveryTenMinutes();
            ClimateManager.getInstance().updateEveryTenMins();
            getInstance().updateRoomLight();
            LuaEventManager.triggerEvent("EveryTenMinutes");
            this.minutesMod = n5 / 10;
            ZomboidRadio.getInstance().UpdateScripts(this.getHour(), n5);
        }
        if (this.previousMinuteStamp != this.minutesStamp) {
            LuaEventManager.triggerEvent("EveryOneMinute");
            this.previousMinuteStamp = this.minutesStamp;
        }
        if (GameServer.bServer && (currentTimeMillis - this.lastClockSync > 10000L || GameServer.bFastForward)) {
            GameServer.syncClock();
            this.lastClockSync = currentTimeMillis;
        }
    }
    
    private void updateRoomLight() {
    }
    
    private void setMinutesStamp() {
        this.minutesStamp = (long)this.getWorldAgeHours() * 60L + this.getMinutes();
    }
    
    public long getMinutesStamp() {
        return this.minutesStamp;
    }
    
    public boolean getThunderStorm() {
        return ClimateManager.getInstance().getIsThunderStorming();
    }
    
    private void doMetaEvents() {
        int n = 3;
        if (SandboxOptions.instance.MetaEvent.getValue() == 1) {
            n = -1;
        }
        if (SandboxOptions.instance.MetaEvent.getValue() == 3) {
            n = 2;
        }
        this.bGunFireEventToday = (n != -1 && Rand.Next(n) == 0);
        if (this.bGunFireEventToday) {
            this.NumGunFireEvents = 1;
            for (int i = 0; i < this.NumGunFireEvents; ++i) {
                this.GunFireTimes[i] = Rand.Next(18000) / 1000.0f + 7.0f;
            }
        }
        this.randomAmbientToday = true;
    }
    
    @Deprecated
    public float getAmbient() {
        return ClimateManager.getInstance().getAmbient();
    }
    
    public void setAmbient(final float ambient) {
        this.Ambient = ambient;
    }
    
    public float getAmbientMax() {
        return this.AmbientMax;
    }
    
    public void setAmbientMax(float ambientMax) {
        ambientMax = Math.min(1.0f, ambientMax);
        ambientMax = Math.max(0.0f, ambientMax);
        this.AmbientMax = ambientMax;
    }
    
    public float getAmbientMin() {
        return this.AmbientMin;
    }
    
    public void setAmbientMin(float ambientMin) {
        ambientMin = Math.min(1.0f, ambientMin);
        ambientMin = Math.max(0.0f, ambientMin);
        this.AmbientMin = ambientMin;
    }
    
    public int getDay() {
        return this.Day;
    }
    
    public int getDayPlusOne() {
        return this.Day + 1;
    }
    
    public void setDay(final int day) {
        this.Day = day;
    }
    
    public int getStartDay() {
        return this.StartDay;
    }
    
    public void setStartDay(final int startDay) {
        this.StartDay = startDay;
    }
    
    public float getMaxZombieCountStart() {
        return 0.0f;
    }
    
    public void setMaxZombieCountStart(final float maxZombieCountStart) {
        this.MaxZombieCountStart = maxZombieCountStart;
    }
    
    public float getMinZombieCountStart() {
        return 0.0f;
    }
    
    public void setMinZombieCountStart(final float minZombieCountStart) {
        this.MinZombieCountStart = minZombieCountStart;
    }
    
    public float getMaxZombieCount() {
        return this.MaxZombieCount;
    }
    
    public void setMaxZombieCount(final float maxZombieCount) {
        this.MaxZombieCount = maxZombieCount;
    }
    
    public float getMinZombieCount() {
        return this.MinZombieCount;
    }
    
    public void setMinZombieCount(final float minZombieCount) {
        this.MinZombieCount = minZombieCount;
    }
    
    public int getMonth() {
        return this.Month;
    }
    
    public void setMonth(final int month) {
        this.Month = month;
    }
    
    public int getStartMonth() {
        return this.StartMonth;
    }
    
    public void setStartMonth(final int startMonth) {
        this.StartMonth = startMonth;
    }
    
    public float getNightTint() {
        return ClimateManager.getInstance().getNightStrength();
    }
    
    public void setNightTint(final float n) {
    }
    
    public float getNight() {
        return ClimateManager.getInstance().getNightStrength();
    }
    
    public void setNight(final float n) {
    }
    
    public float getTimeOfDay() {
        return this.TimeOfDay;
    }
    
    public void setTimeOfDay(final float timeOfDay) {
        this.TimeOfDay = timeOfDay;
    }
    
    public float getStartTimeOfDay() {
        return this.StartTimeOfDay;
    }
    
    public void setStartTimeOfDay(final float startTimeOfDay) {
        this.StartTimeOfDay = startTimeOfDay;
    }
    
    public float getViewDist() {
        return ClimateManager.getInstance().getViewDistance();
    }
    
    public float getViewDistMax() {
        return this.ViewDistMax;
    }
    
    public void setViewDistMax(final float viewDistMax) {
        this.ViewDistMax = viewDistMax;
    }
    
    public float getViewDistMin() {
        return this.ViewDistMin;
    }
    
    public void setViewDistMin(final float viewDistMin) {
        this.ViewDistMin = viewDistMin;
    }
    
    public int getYear() {
        return this.Year;
    }
    
    public void setYear(final int year) {
        this.Year = year;
    }
    
    public int getStartYear() {
        return this.StartYear;
    }
    
    public void setStartYear(final int startYear) {
        this.StartYear = startYear;
    }
    
    public int getNightsSurvived() {
        return this.NightsSurvived;
    }
    
    public void setNightsSurvived(final int nightsSurvived) {
        this.NightsSurvived = nightsSurvived;
    }
    
    public double getWorldAgeHours() {
        final float n = (float)(this.getNightsSurvived() * 24);
        float n2;
        if (this.getTimeOfDay() >= 7.0f) {
            n2 = n + (this.getTimeOfDay() - 7.0f);
        }
        else {
            n2 = n + (this.getTimeOfDay() + 17.0f);
        }
        return n2;
    }
    
    public double getHoursSurvived() {
        DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
        return this.HoursSurvived;
    }
    
    public void setHoursSurvived(final double hoursSurvived) {
        DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
        this.HoursSurvived = hoursSurvived;
    }
    
    public int getHour() {
        return (int)Math.floor(Math.floor(this.getTimeOfDay() * 3600.0f) / 3600.0);
    }
    
    public PZCalendar getCalender() {
        this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (int)this.getTimeOfDay()) * 60.0f));
        return this.Calender;
    }
    
    public void setCalender(final PZCalendar calender) {
        this.Calender = calender;
    }
    
    public void updateCalendar(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (this.Calender == null) {
            this.Calender = new PZCalendar(new GregorianCalendar());
        }
        this.Calender.set(n, n2, n3, n4, n5);
    }
    
    public float getMinutesPerDay() {
        return this.MinutesPerDay;
    }
    
    public void setMinutesPerDay(final float minutesPerDay) {
        this.MinutesPerDay = minutesPerDay;
    }
    
    public float getLastTimeOfDay() {
        return this.LastTimeOfDay;
    }
    
    public void setLastTimeOfDay(final float lastTimeOfDay) {
        this.LastTimeOfDay = lastTimeOfDay;
    }
    
    public void setTargetZombies(final int targetZombies) {
        this.TargetZombies = targetZombies;
    }
    
    public boolean isRainingToday() {
        return this.RainingToday;
    }
    
    public float getMultiplier() {
        if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersAsleep()) {
            return 200.0f * (30.0f / PerformanceSettings.getLockFPS());
        }
        float n = 1.0f;
        if (GameServer.bServer && GameServer.bFastForward) {
            n = (float)ServerOptions.instance.FastForwardMultiplier.getValue() / this.getDeltaMinutesPerDay();
        }
        else if (GameClient.bClient && GameClient.bFastForward) {
            n = (float)ServerOptions.instance.FastForwardMultiplier.getValue() / this.getDeltaMinutesPerDay();
        }
        float n2 = n * this.Multiplier * this.FPSMultiplier * this.multiplierBias * this.PerObjectMultiplier;
        if (DebugOptions.instance.GameTimeSpeedQuarter.getValue()) {
            n2 *= 0.25f;
        }
        if (DebugOptions.instance.GameTimeSpeedHalf.getValue()) {
            n2 *= 0.5f;
        }
        return n2 * 0.8f;
    }
    
    public float getTimeDelta() {
        return this.getMultiplier() / (0.8f * this.multiplierBias) / 60.0f;
    }
    
    public static float getAnimSpeedFix() {
        return 0.8f;
    }
    
    public void setMultiplier(final float multiplier) {
        this.Multiplier = multiplier;
    }
    
    public float getServerMultiplier() {
        final float n = this.Multiplier * (10.0f / GameWindow.averageFPS / (PerformanceSettings.ManualFrameSkips + 1)) * 0.5f;
        if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersAsleep()) {
            return 200.0f * (30.0f / PerformanceSettings.getLockFPS());
        }
        return n * 1.6f * this.multiplierBias;
    }
    
    public float getUnmoddedMultiplier() {
        if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersAsleep()) {
            return 200.0f * (30.0f / PerformanceSettings.getLockFPS());
        }
        return this.Multiplier * this.FPSMultiplier * this.PerObjectMultiplier;
    }
    
    public float getInvMultiplier() {
        return 1.0f / this.getMultiplier();
    }
    
    public float getTrueMultiplier() {
        return this.Multiplier * this.PerObjectMultiplier;
    }
    
    public void save() {
        final File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("map_t.bin"));
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        final DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(out));
        try {
            GameTime.instance.save(dataOutputStream);
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
        try {
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        catch (IOException ex3) {
            ex3.printStackTrace();
        }
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(71);
        dataOutputStream.writeByte(77);
        dataOutputStream.writeByte(84);
        dataOutputStream.writeByte(77);
        dataOutputStream.writeInt(186);
        dataOutputStream.writeFloat(this.Multiplier);
        dataOutputStream.writeInt(this.NightsSurvived);
        dataOutputStream.writeInt(this.TargetZombies);
        dataOutputStream.writeFloat(this.LastTimeOfDay);
        dataOutputStream.writeFloat(this.TimeOfDay);
        dataOutputStream.writeInt(this.Day);
        dataOutputStream.writeInt(this.Month);
        dataOutputStream.writeInt(this.Year);
        dataOutputStream.writeFloat(0.0f);
        dataOutputStream.writeFloat(0.0f);
        dataOutputStream.writeInt(0);
        if (this.table != null) {
            dataOutputStream.writeByte(1);
            this.table.save(dataOutputStream);
        }
        else {
            dataOutputStream.writeByte(0);
        }
        GameWindow.WriteString(dataOutputStream, Core.getInstance().getPoisonousBerry());
        GameWindow.WriteString(dataOutputStream, Core.getInstance().getPoisonousMushroom());
        dataOutputStream.writeInt(this.HelicopterDay1);
        dataOutputStream.writeInt(this.HelicopterTime1Start);
        dataOutputStream.writeInt(this.HelicopterTime1End);
        ClimateManager.getInstance().save(dataOutputStream);
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putFloat(this.Multiplier);
        byteBuffer.putInt(this.NightsSurvived);
        byteBuffer.putInt(this.TargetZombies);
        byteBuffer.putFloat(this.LastTimeOfDay);
        byteBuffer.putFloat(this.TimeOfDay);
        byteBuffer.putInt(this.Day);
        byteBuffer.putInt(this.Month);
        byteBuffer.putInt(this.Year);
        byteBuffer.putFloat(0.0f);
        byteBuffer.putFloat(0.0f);
        byteBuffer.putInt(0);
        if (this.table != null) {
            byteBuffer.put((byte)1);
            this.table.save(byteBuffer);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    public void load(final DataInputStream dataInputStream) throws IOException {
        int n = IsoWorld.SavedWorldVersion;
        if (n == -1) {
            n = 186;
        }
        dataInputStream.mark(0);
        final byte byte1 = dataInputStream.readByte();
        final byte byte2 = dataInputStream.readByte();
        final byte byte3 = dataInputStream.readByte();
        final byte byte4 = dataInputStream.readByte();
        if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
            n = dataInputStream.readInt();
        }
        else {
            dataInputStream.reset();
        }
        this.Multiplier = dataInputStream.readFloat();
        this.NightsSurvived = dataInputStream.readInt();
        this.TargetZombies = dataInputStream.readInt();
        this.LastTimeOfDay = dataInputStream.readFloat();
        this.TimeOfDay = dataInputStream.readFloat();
        this.Day = dataInputStream.readInt();
        this.Month = dataInputStream.readInt();
        this.Year = dataInputStream.readInt();
        dataInputStream.readFloat();
        dataInputStream.readFloat();
        dataInputStream.readInt();
        if (dataInputStream.readByte() == 1) {
            if (this.table == null) {
                this.table = LuaManager.platform.newTable();
            }
            this.table.load(dataInputStream, n);
        }
        if (n >= 74) {
            Core.getInstance().setPoisonousBerry(GameWindow.ReadString(dataInputStream));
            Core.getInstance().setPoisonousMushroom(GameWindow.ReadString(dataInputStream));
        }
        if (n >= 90) {
            this.HelicopterDay1 = dataInputStream.readInt();
            this.HelicopterTime1Start = dataInputStream.readInt();
            this.HelicopterTime1End = dataInputStream.readInt();
        }
        if (n >= 135) {
            ClimateManager.getInstance().load(dataInputStream, n);
        }
        this.setMinutesStamp();
    }
    
    public void load(final ByteBuffer byteBuffer) throws IOException {
        final int n = 186;
        this.Multiplier = byteBuffer.getFloat();
        this.NightsSurvived = byteBuffer.getInt();
        this.TargetZombies = byteBuffer.getInt();
        this.LastTimeOfDay = byteBuffer.getFloat();
        this.TimeOfDay = byteBuffer.getFloat();
        this.Day = byteBuffer.getInt();
        this.Month = byteBuffer.getInt();
        this.Year = byteBuffer.getInt();
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        byteBuffer.getInt();
        if (byteBuffer.get() == 1) {
            if (this.table == null) {
                this.table = LuaManager.platform.newTable();
            }
            this.table.load(byteBuffer, n);
        }
        this.setMinutesStamp();
    }
    
    public void load() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        final int read = bufferedInputStream.read(SliceY.SliceBuffer.array());
                        SliceY.SliceBuffer.limit(read);
                        this.load(new DataInputStream(new ByteArrayInputStream(SliceY.SliceBuffer.array(), 0, read)));
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex2) {}
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public int getDawn() {
        return this.dawn;
    }
    
    public void setDawn(final int dawn) {
        this.dawn = dawn;
    }
    
    public int getDusk() {
        return this.dusk;
    }
    
    public void setDusk(final int dusk) {
        this.dusk = dusk;
    }
    
    public KahluaTable getModData() {
        if (this.table == null) {
            this.table = LuaManager.platform.newTable();
        }
        return this.table;
    }
    
    public boolean isThunderDay() {
        return this.thunderDay;
    }
    
    public void setThunderDay(final boolean thunderDay) {
        this.thunderDay = thunderDay;
    }
    
    public void saveToPacket(final ByteBuffer byteBuffer) throws IOException {
        final KahluaTable modData = getInstance().getModData();
        final Object rawget = modData.rawget((Object)"camping");
        final Object rawget2 = modData.rawget((Object)"farming");
        final Object rawget3 = modData.rawget((Object)"trapping");
        modData.rawset((Object)"camping", (Object)null);
        modData.rawset((Object)"farming", (Object)null);
        modData.rawset((Object)"trapping", (Object)null);
        this.save(byteBuffer);
        modData.rawset((Object)"camping", rawget);
        modData.rawset((Object)"farming", rawget2);
        modData.rawset((Object)"trapping", rawget3);
    }
    
    public int getHelicopterDay1() {
        return this.HelicopterDay1;
    }
    
    public int getHelicopterDay() {
        return this.HelicopterDay1;
    }
    
    public void setHelicopterDay(final int n) {
        this.HelicopterDay1 = PZMath.max(n, 0);
    }
    
    public int getHelicopterStartHour() {
        return this.HelicopterTime1Start;
    }
    
    public void setHelicopterStartHour(final int n) {
        this.HelicopterTime1Start = PZMath.clamp(n, 0, 24);
    }
    
    public int getHelicopterEndHour() {
        return this.HelicopterTime1End;
    }
    
    public void setHelicopterEndHour(final int n) {
        this.HelicopterTime1End = PZMath.clamp(n, 0, 24);
    }
    
    public static boolean isGamePaused() {
        if (GameServer.bServer) {
            return GameServer.Players.isEmpty() && ServerOptions.instance.PauseEmpty.getValue();
        }
        if (GameClient.bClient) {
            return GameClient.IsClientPaused();
        }
        final SpeedControls speedControls = UIManager.getSpeedControls();
        return speedControls != null && speedControls.getCurrentGameSpeed() == 0;
    }
    
    static {
        GameTime.instance = new GameTime();
        GameTime.serverTimeShift = 0L;
        GameTime.serverTimeShiftIsSet = false;
        GameTime.isUTest = false;
    }
    
    public static class AnimTimer
    {
        public float Elapsed;
        public float Duration;
        public boolean Finished;
        public int Ticks;
        
        public AnimTimer() {
            this.Finished = true;
        }
        
        public void init(final int ticks) {
            this.Ticks = ticks;
            this.Elapsed = 0.0f;
            this.Duration = ticks * 1 / 30.0f;
            this.Finished = false;
        }
        
        public void update() {
            this.Elapsed += GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f / 30.0f;
            if (this.Elapsed >= this.Duration) {
                this.Elapsed = this.Duration;
                this.Finished = true;
            }
        }
        
        public float ratio() {
            return this.Elapsed / this.Duration;
        }
        
        public boolean finished() {
            return this.Finished;
        }
    }
}
