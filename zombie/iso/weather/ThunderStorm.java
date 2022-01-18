// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import zombie.Lua.LuaEventManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.core.opengl.RenderSettings;
import zombie.audio.GameSoundClip;
import zombie.audio.GameSound;
import fmod.javafmod;
import zombie.GameSounds;
import fmod.fmod.FMODManager;
import zombie.ui.UIManager;
import zombie.ui.SpeedControls;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.GameTime;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.core.Core;
import java.util.ArrayList;

public class ThunderStorm
{
    public static int MAP_MIN_X;
    public static int MAP_MIN_Y;
    public static int MAP_MAX_X;
    public static int MAP_MAX_Y;
    private boolean hasActiveThunderClouds;
    private float cloudMaxRadius;
    private ThunderEvent[] events;
    private ThunderCloud[] clouds;
    private ClimateManager climateManager;
    private ArrayList<ThunderCloud> cloudCache;
    private boolean donoise;
    private int strikeRadius;
    private final PlayerLightningInfo[] lightningInfos;
    private ThunderEvent networkThunderEvent;
    private ThunderCloud dummyCloud;
    
    public ArrayList<ThunderCloud> getClouds() {
        if (this.cloudCache == null) {
            this.cloudCache = new ArrayList<ThunderCloud>(this.clouds.length);
            for (int i = 0; i < this.clouds.length; ++i) {
                this.cloudCache.add(this.clouds[i]);
            }
        }
        return this.cloudCache;
    }
    
    public ThunderStorm(final ClimateManager climateManager) {
        this.hasActiveThunderClouds = false;
        this.cloudMaxRadius = 20000.0f;
        this.events = new ThunderEvent[30];
        this.clouds = new ThunderCloud[3];
        this.donoise = false;
        this.strikeRadius = 4000;
        this.lightningInfos = new PlayerLightningInfo[4];
        this.networkThunderEvent = new ThunderEvent();
        this.climateManager = climateManager;
        for (int i = 0; i < this.events.length; ++i) {
            this.events[i] = new ThunderEvent();
        }
        for (int j = 0; j < this.clouds.length; ++j) {
            this.clouds[j] = new ThunderCloud();
        }
        for (int k = 0; k < 4; ++k) {
            this.lightningInfos[k] = new PlayerLightningInfo();
        }
    }
    
    private ThunderEvent getFreeEvent() {
        for (int i = 0; i < this.events.length; ++i) {
            if (!this.events[i].isRunning) {
                return this.events[i];
            }
        }
        return null;
    }
    
    private ThunderCloud getFreeCloud() {
        for (int i = 0; i < this.clouds.length; ++i) {
            if (!this.clouds[i].isRunning) {
                return this.clouds[i];
            }
        }
        return null;
    }
    
    private ThunderCloud getCloud(final int n) {
        final int n2 = 0;
        if (n2 < this.clouds.length) {
            return this.clouds[n2];
        }
        return null;
    }
    
    public boolean HasActiveThunderClouds() {
        return this.hasActiveThunderClouds;
    }
    
    public void noise(final String s) {
        if (this.donoise && (Core.bDebug || (GameServer.bServer && GameServer.bDebug))) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public void stopAllClouds() {
        for (int i = 0; i < this.clouds.length; ++i) {
            this.stopCloud(i);
        }
    }
    
    public void stopCloud(final int n) {
        final ThunderCloud cloud = this.getCloud(n);
        if (cloud != null) {
            cloud.isRunning = false;
        }
    }
    
    private static float addToAngle(float n, final float n2) {
        n += n2;
        if (n > 360.0f) {
            n -= 360.0f;
        }
        else if (n < 0.0f) {
            n += 360.0f;
        }
        return n;
    }
    
    public static int getMapDiagonal() {
        return (int)Math.sqrt(Math.pow(ThunderStorm.MAP_MAX_X - ThunderStorm.MAP_MIN_X, 2.0) + Math.pow(ThunderStorm.MAP_MAX_Y - ThunderStorm.MAP_MIN_Y, 2.0)) / 2;
    }
    
    public void startThunderCloud(final float n, final float n2, final float n3, final float n4, final float n5, final double n6, final boolean b) {
        this.startThunderCloud(n, n2, n3, n4, n5, n6, b);
    }
    
    public ThunderCloud startThunderCloud(final float n, float addToAngle, final float radius, final float eventFrequency, final float n2, final double duration, final boolean b, final float n3) {
        if (GameClient.bClient) {
            return null;
        }
        final ThunderCloud freeCloud = this.getFreeCloud();
        if (freeCloud != null) {
            addToAngle = addToAngle(addToAngle, Rand.Next(-10.0f, 10.0f));
            freeCloud.startTime = GameTime.instance.getWorldAgeHours();
            freeCloud.endTime = freeCloud.startTime + duration;
            freeCloud.duration = duration;
            freeCloud.strength = ClimateManager.clamp01(n);
            freeCloud.angle = addToAngle;
            freeCloud.radius = radius;
            if (freeCloud.radius > this.cloudMaxRadius) {
                freeCloud.radius = this.cloudMaxRadius;
            }
            freeCloud.eventFrequency = eventFrequency;
            freeCloud.thunderRatio = ClimateManager.clamp01(n2);
            freeCloud.percentageOffset = PZMath.clamp_01(n3);
            addToAngle(addToAngle, 180.0f);
            final int n4 = ThunderStorm.MAP_MAX_X - ThunderStorm.MAP_MIN_X;
            final int n5 = ThunderStorm.MAP_MAX_Y - ThunderStorm.MAP_MIN_Y;
            int n6 = Rand.Next(ThunderStorm.MAP_MIN_X + n4 / 5, ThunderStorm.MAP_MAX_X - n4 / 5);
            int n7 = Rand.Next(ThunderStorm.MAP_MIN_Y + n5 / 5, ThunderStorm.MAP_MAX_Y - n5 / 5);
            if (b) {
                if (!GameServer.bServer) {
                    final IsoPlayer instance = IsoPlayer.getInstance();
                    if (instance != null) {
                        n6 = (int)instance.getX();
                        n7 = (int)instance.getY();
                    }
                }
                else {
                    if (GameServer.Players.isEmpty()) {
                        DebugLog.log("Thundercloud couldnt target player...");
                        return null;
                    }
                    final ArrayList<IsoPlayer> players = GameServer.getPlayers();
                    for (int i = players.size() - 1; i >= 0; --i) {
                        if (players.get(i).getCurrentSquare() == null) {
                            players.remove(i);
                        }
                    }
                    if (!players.isEmpty()) {
                        final IsoPlayer isoPlayer = players.get(Rand.Next(players.size()));
                        n6 = isoPlayer.getCurrentSquare().getX();
                        n7 = isoPlayer.getCurrentSquare().getY();
                    }
                }
            }
            freeCloud.setCenter(n6, n7, addToAngle);
            freeCloud.isRunning = true;
            freeCloud.suspendTimer.init(3);
            return freeCloud;
        }
        return null;
    }
    
    public void update(final double n) {
        if (!GameClient.bClient || GameServer.bServer) {
            this.hasActiveThunderClouds = false;
            for (int i = 0; i < this.clouds.length; ++i) {
                final ThunderCloud thunderCloud = this.clouds[i];
                if (thunderCloud.isRunning) {
                    if (n < thunderCloud.endTime) {
                        float n2 = (float)((n - thunderCloud.startTime) / thunderCloud.duration);
                        if (thunderCloud.percentageOffset > 0.0f) {
                            n2 = thunderCloud.percentageOffset + (1.0f - thunderCloud.percentageOffset) * n2;
                        }
                        thunderCloud.currentX = (int)ClimateManager.lerp(n2, (float)thunderCloud.startX, (float)thunderCloud.endX);
                        thunderCloud.currentY = (int)ClimateManager.lerp(n2, (float)thunderCloud.startY, (float)thunderCloud.endY);
                        thunderCloud.suspendTimer.update();
                        this.hasActiveThunderClouds = true;
                        if (thunderCloud.suspendTimer.finished()) {
                            thunderCloud.suspendTimer.init((int)(Rand.Next(3.5f - 3.0f * thunderCloud.strength, 24.0f - 20.0f * thunderCloud.strength) * 60.0f));
                            final float next = Rand.Next(0.0f, 1.0f);
                            if (next < 0.6f) {
                                this.strikeRadius = (int)(thunderCloud.radius / 2.0f) / 3;
                            }
                            else if (next < 0.9f) {
                                this.strikeRadius = (int)(thunderCloud.radius / 2.0f) / 4 * 3;
                            }
                            else {
                                this.strikeRadius = (int)(thunderCloud.radius / 2.0f);
                            }
                            if (Rand.Next(0.0f, 1.0f) < thunderCloud.thunderRatio) {
                                this.noise("trigger thunder event");
                                this.triggerThunderEvent(Rand.Next(thunderCloud.currentX - this.strikeRadius, thunderCloud.currentX + this.strikeRadius), Rand.Next(thunderCloud.currentY - this.strikeRadius, thunderCloud.currentY + this.strikeRadius), true, true, Rand.Next(0.0f, 1.0f) > 0.4f);
                            }
                            else {
                                this.triggerThunderEvent(Rand.Next(thunderCloud.currentX - this.strikeRadius, thunderCloud.currentX + this.strikeRadius), Rand.Next(thunderCloud.currentY - this.strikeRadius, thunderCloud.currentY + this.strikeRadius), false, false, true);
                                this.noise("trigger rumble event");
                            }
                        }
                    }
                    else {
                        thunderCloud.isRunning = false;
                    }
                }
            }
        }
        if (GameClient.bClient || !GameServer.bServer) {
            for (int j = 0; j < 4; ++j) {
                final PlayerLightningInfo playerLightningInfo = this.lightningInfos[j];
                if (playerLightningInfo.lightningState == LightningState.ApplyLightning) {
                    playerLightningInfo.timer.update();
                    if (!playerLightningInfo.timer.finished()) {
                        playerLightningInfo.lightningMod = ClimateManager.clamp01(playerLightningInfo.timer.ratio());
                        final ClimateManager.ClimateFloat dayLightStrength = this.climateManager.dayLightStrength;
                        dayLightStrength.finalValue += (1.0f - this.climateManager.dayLightStrength.finalValue) * (1.0f - playerLightningInfo.lightningMod);
                        final IsoPlayer isoPlayer = IsoPlayer.players[j];
                        if (isoPlayer != null) {
                            isoPlayer.dirtyRecalcGridStackTime = 1.0f;
                        }
                    }
                    else {
                        this.noise("apply lightning done.");
                        playerLightningInfo.timer.init(2);
                        playerLightningInfo.lightningStrength = 0.0f;
                        playerLightningInfo.lightningState = LightningState.Idle;
                    }
                }
            }
            final boolean b = SpeedControls.instance.getCurrentGameSpeed() > 1;
            boolean b2 = false;
            boolean b3 = false;
            for (int k = 0; k < this.events.length; ++k) {
                final ThunderEvent thunderEvent = this.events[k];
                if (thunderEvent.isRunning) {
                    thunderEvent.soundDelay.update();
                    if (thunderEvent.soundDelay.finished()) {
                        thunderEvent.isRunning = false;
                        boolean b4 = true;
                        if (UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() > 1) {
                            b4 = false;
                        }
                        if (b4 && !Core.SoundDisabled && FMODManager.instance.getNumListeners() > 0) {
                            if (thunderEvent.doStrike && (!b || !b2)) {
                                this.noise("thunder sound");
                                final GameSound sound = GameSounds.getSound("Thunder");
                                final GameSoundClip gameSoundClip = (sound == null) ? null : sound.getRandomClip();
                                if (gameSoundClip != null && gameSoundClip.eventDescription != null) {
                                    final long fmod_Studio_System_CreateEventInstance = javafmod.FMOD_Studio_System_CreateEventInstance(gameSoundClip.eventDescription.address);
                                    javafmod.FMOD_Studio_EventInstance3D(fmod_Studio_System_CreateEventInstance, (float)thunderEvent.eventX, (float)thunderEvent.eventY, 100.0f);
                                    javafmod.FMOD_Studio_EventInstance_SetVolume(fmod_Studio_System_CreateEventInstance, gameSoundClip.getEffectiveVolume());
                                    javafmod.FMOD_Studio_StartEvent(fmod_Studio_System_CreateEventInstance);
                                    javafmod.FMOD_Studio_ReleaseEventInstance(fmod_Studio_System_CreateEventInstance);
                                }
                            }
                            if (thunderEvent.doRumble && (!b || !b3)) {
                                this.noise("rumble sound");
                                final GameSound sound2 = GameSounds.getSound("RumbleThunder");
                                final GameSoundClip gameSoundClip2 = (sound2 == null) ? null : sound2.getRandomClip();
                                if (gameSoundClip2 != null && gameSoundClip2.eventDescription != null) {
                                    final long fmod_Studio_System_CreateEventInstance2 = javafmod.FMOD_Studio_System_CreateEventInstance(gameSoundClip2.eventDescription.address);
                                    javafmod.FMOD_Studio_EventInstance3D(fmod_Studio_System_CreateEventInstance2, (float)thunderEvent.eventX, (float)thunderEvent.eventY, 200.0f);
                                    javafmod.FMOD_Studio_EventInstance_SetVolume(fmod_Studio_System_CreateEventInstance2, gameSoundClip2.getEffectiveVolume());
                                    javafmod.FMOD_Studio_StartEvent(fmod_Studio_System_CreateEventInstance2);
                                    javafmod.FMOD_Studio_ReleaseEventInstance(fmod_Studio_System_CreateEventInstance2);
                                }
                            }
                        }
                    }
                    else {
                        b2 = (b2 || thunderEvent.doStrike);
                        b3 = (b3 || thunderEvent.doRumble);
                    }
                }
            }
        }
    }
    
    public void applyLightningForPlayer(final RenderSettings.PlayerRenderSettings playerRenderSettings, final int n, final IsoPlayer isoPlayer) {
        final PlayerLightningInfo playerLightningInfo = this.lightningInfos[n];
        if (playerLightningInfo.lightningState == LightningState.ApplyLightning) {
            final ClimateColorInfo cm_GlobalLight = playerRenderSettings.CM_GlobalLight;
            playerLightningInfo.lightningColor.getExterior().r = cm_GlobalLight.getExterior().r + playerLightningInfo.lightningStrength * (1.0f - cm_GlobalLight.getExterior().r);
            playerLightningInfo.lightningColor.getExterior().g = cm_GlobalLight.getExterior().g + playerLightningInfo.lightningStrength * (1.0f - cm_GlobalLight.getExterior().g);
            playerLightningInfo.lightningColor.getExterior().b = cm_GlobalLight.getExterior().b + playerLightningInfo.lightningStrength * (1.0f - cm_GlobalLight.getExterior().b);
            playerLightningInfo.lightningColor.getInterior().r = cm_GlobalLight.getInterior().r + playerLightningInfo.lightningStrength * (1.0f - cm_GlobalLight.getInterior().r);
            playerLightningInfo.lightningColor.getInterior().g = cm_GlobalLight.getInterior().g + playerLightningInfo.lightningStrength * (1.0f - cm_GlobalLight.getInterior().g);
            playerLightningInfo.lightningColor.getInterior().b = cm_GlobalLight.getInterior().b + playerLightningInfo.lightningStrength * (1.0f - cm_GlobalLight.getInterior().b);
            playerLightningInfo.lightningColor.interp(playerRenderSettings.CM_GlobalLight, playerLightningInfo.lightningMod, playerLightningInfo.outColor);
            playerRenderSettings.CM_GlobalLight.getExterior().r = playerLightningInfo.outColor.getExterior().r;
            playerRenderSettings.CM_GlobalLight.getExterior().g = playerLightningInfo.outColor.getExterior().g;
            playerRenderSettings.CM_GlobalLight.getExterior().b = playerLightningInfo.outColor.getExterior().b;
            playerRenderSettings.CM_GlobalLight.getInterior().r = playerLightningInfo.outColor.getInterior().r;
            playerRenderSettings.CM_GlobalLight.getInterior().g = playerLightningInfo.outColor.getInterior().g;
            playerRenderSettings.CM_GlobalLight.getInterior().b = playerLightningInfo.outColor.getInterior().b;
            playerRenderSettings.CM_Ambient = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0f, playerRenderSettings.CM_Ambient);
            playerRenderSettings.CM_DayLightStrength = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0f, playerRenderSettings.CM_DayLightStrength);
            playerRenderSettings.CM_Desaturation = ClimateManager.lerp(playerLightningInfo.lightningMod, 0.0f, playerRenderSettings.CM_Desaturation);
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                playerRenderSettings.CM_GlobalLightIntensity = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0f, playerRenderSettings.CM_GlobalLightIntensity);
            }
            else {
                playerRenderSettings.CM_GlobalLightIntensity = ClimateManager.lerp(playerLightningInfo.lightningMod, 0.0f, playerRenderSettings.CM_GlobalLightIntensity);
            }
        }
    }
    
    public boolean isModifyingNight() {
        return false;
    }
    
    public void triggerThunderEvent(final int eventX, final int eventY, final boolean doStrike, final boolean doLightning, final boolean doRumble) {
        if (GameServer.bServer) {
            this.networkThunderEvent.eventX = eventX;
            this.networkThunderEvent.eventY = eventY;
            this.networkThunderEvent.doStrike = doStrike;
            this.networkThunderEvent.doLightning = doLightning;
            this.networkThunderEvent.doRumble = doRumble;
            this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)2, null);
        }
        else if (!GameClient.bClient) {
            this.enqueueThunderEvent(eventX, eventY, doStrike, doLightning, doRumble);
        }
    }
    
    public void writeNetThunderEvent(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putInt(this.networkThunderEvent.eventX);
        byteBuffer.putInt(this.networkThunderEvent.eventY);
        byteBuffer.put((byte)(this.networkThunderEvent.doStrike ? 1 : 0));
        byteBuffer.put((byte)(this.networkThunderEvent.doLightning ? 1 : 0));
        byteBuffer.put((byte)(this.networkThunderEvent.doRumble ? 1 : 0));
    }
    
    public void readNetThunderEvent(final ByteBuffer byteBuffer) throws IOException {
        this.enqueueThunderEvent(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.get() == 1, byteBuffer.get() == 1, byteBuffer.get() == 1);
    }
    
    public void enqueueThunderEvent(final int eventX, final int eventY, final boolean b, final boolean b2, final boolean b3) {
        LuaEventManager.triggerEvent("OnThunderEvent", eventX, eventY, b, b2, b3);
        if (b || b3) {
            int n = 9999999;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    final int getDistance = this.GetDistance((int)isoPlayer.getX(), (int)isoPlayer.getY(), eventX, eventY);
                    if (getDistance < n) {
                        n = getDistance;
                    }
                    if (b2) {
                        this.lightningInfos[i].distance = getDistance;
                        this.lightningInfos[i].x = eventX;
                        this.lightningInfos[i].y = eventY;
                    }
                }
            }
            this.noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            if (n < 10000) {
                final ThunderEvent freeEvent = this.getFreeEvent();
                if (freeEvent != null) {
                    freeEvent.doRumble = b3;
                    freeEvent.doStrike = b;
                    freeEvent.eventX = eventX;
                    freeEvent.eventY = eventY;
                    freeEvent.isRunning = true;
                    freeEvent.soundDelay.init((int)(n / 300.0f * 60.0f));
                    if (b2) {
                        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                            if (IsoPlayer.players[j] != null) {
                                if (this.lightningInfos[j].distance < 7500.0f) {
                                    final float lightningStrength = 1.0f - this.lightningInfos[j].distance / 7500.0f;
                                    this.lightningInfos[j].lightningState = LightningState.ApplyLightning;
                                    if (lightningStrength > this.lightningInfos[j].lightningStrength) {
                                        this.lightningInfos[j].lightningStrength = lightningStrength;
                                        this.lightningInfos[j].timer.init(20 + (int)(80.0f * this.lightningInfos[j].lightningStrength));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private int GetDistance(final int n, final int n2, final int n3, final int n4) {
        return (int)Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0));
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        if (!GameClient.bClient || GameServer.bServer) {
            dataOutputStream.writeByte(this.clouds.length);
            for (int i = 0; i < this.clouds.length; ++i) {
                final ThunderCloud thunderCloud = this.clouds[i];
                dataOutputStream.writeBoolean(thunderCloud.isRunning);
                if (thunderCloud.isRunning) {
                    dataOutputStream.writeInt(thunderCloud.startX);
                    dataOutputStream.writeInt(thunderCloud.startY);
                    dataOutputStream.writeInt(thunderCloud.endX);
                    dataOutputStream.writeInt(thunderCloud.endY);
                    dataOutputStream.writeFloat(thunderCloud.radius);
                    dataOutputStream.writeFloat(thunderCloud.angle);
                    dataOutputStream.writeFloat(thunderCloud.strength);
                    dataOutputStream.writeFloat(thunderCloud.thunderRatio);
                    dataOutputStream.writeDouble(thunderCloud.startTime);
                    dataOutputStream.writeDouble(thunderCloud.endTime);
                    dataOutputStream.writeDouble(thunderCloud.duration);
                    dataOutputStream.writeFloat(thunderCloud.percentageOffset);
                }
            }
        }
        else {
            dataOutputStream.writeByte(0);
        }
    }
    
    public void load(final DataInputStream dataInputStream) throws IOException {
        final byte byte1 = dataInputStream.readByte();
        if (byte1 == 0) {
            return;
        }
        if (byte1 > this.clouds.length && this.dummyCloud == null) {
            this.dummyCloud = new ThunderCloud();
        }
        for (byte b = 0; b < byte1; ++b) {
            final boolean boolean1 = dataInputStream.readBoolean();
            ThunderCloud dummyCloud;
            if (b >= this.clouds.length) {
                dummyCloud = this.dummyCloud;
            }
            else {
                dummyCloud = this.clouds[b];
            }
            if (dummyCloud.isRunning = boolean1) {
                dummyCloud.startX = dataInputStream.readInt();
                dummyCloud.startY = dataInputStream.readInt();
                dummyCloud.endX = dataInputStream.readInt();
                dummyCloud.endY = dataInputStream.readInt();
                dummyCloud.radius = dataInputStream.readFloat();
                dummyCloud.angle = dataInputStream.readFloat();
                dummyCloud.strength = dataInputStream.readFloat();
                dummyCloud.thunderRatio = dataInputStream.readFloat();
                dummyCloud.startTime = dataInputStream.readDouble();
                dummyCloud.endTime = dataInputStream.readDouble();
                dummyCloud.duration = dataInputStream.readDouble();
                dummyCloud.percentageOffset = dataInputStream.readFloat();
            }
        }
    }
    
    static {
        ThunderStorm.MAP_MIN_X = -3000;
        ThunderStorm.MAP_MIN_Y = -3000;
        ThunderStorm.MAP_MAX_X = 25000;
        ThunderStorm.MAP_MAX_Y = 20000;
    }
    
    private static class ThunderEvent
    {
        private int eventX;
        private int eventY;
        private boolean doLightning;
        private boolean doRumble;
        private boolean doStrike;
        private GameTime.AnimTimer soundDelay;
        private boolean isRunning;
        
        private ThunderEvent() {
            this.doLightning = false;
            this.doRumble = false;
            this.doStrike = false;
            this.soundDelay = new GameTime.AnimTimer();
            this.isRunning = false;
        }
    }
    
    public static class ThunderCloud
    {
        private int currentX;
        private int currentY;
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private double startTime;
        private double endTime;
        private double duration;
        private float strength;
        private float angle;
        private float radius;
        private float eventFrequency;
        private float thunderRatio;
        private float percentageOffset;
        private boolean isRunning;
        private GameTime.AnimTimer suspendTimer;
        
        public ThunderCloud() {
            this.isRunning = false;
            this.suspendTimer = new GameTime.AnimTimer();
        }
        
        public int getCurrentX() {
            return this.currentX;
        }
        
        public int getCurrentY() {
            return this.currentY;
        }
        
        public float getRadius() {
            return this.radius;
        }
        
        public boolean isRunning() {
            return this.isRunning;
        }
        
        public float getStrength() {
            return this.strength;
        }
        
        public double lifeTime() {
            return (this.startTime - this.endTime) / this.duration;
        }
        
        public void setCenter(final int n, final int n2, final float n3) {
            final int mapDiagonal = ThunderStorm.getMapDiagonal();
            final float addToAngle = ThunderStorm.addToAngle(n3, 180.0f);
            final int n4 = mapDiagonal + Rand.Next(1500, 7500);
            final int n5 = (int)(n + n4 * Math.cos(Math.toRadians(addToAngle)));
            final int n6 = (int)(n2 + n4 * Math.sin(Math.toRadians(addToAngle)));
            final int n7 = mapDiagonal + Rand.Next(1500, 7500);
            final int endX = (int)(n + n7 * Math.cos(Math.toRadians(n3)));
            final int endY = (int)(n2 + n7 * Math.sin(Math.toRadians(n3)));
            this.startX = n5;
            this.startY = n6;
            this.endX = endX;
            this.endY = endY;
            this.currentX = n5;
            this.currentY = n6;
        }
    }
    
    private enum LightningState
    {
        Idle, 
        ApplyLightning;
        
        private static /* synthetic */ LightningState[] $values() {
            return new LightningState[] { LightningState.Idle, LightningState.ApplyLightning };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private class PlayerLightningInfo
    {
        public LightningState lightningState;
        public GameTime.AnimTimer timer;
        public float lightningStrength;
        public float lightningMod;
        public ClimateColorInfo lightningColor;
        public ClimateColorInfo outColor;
        public int x;
        public int y;
        public int distance;
        
        private PlayerLightningInfo() {
            this.lightningState = LightningState.Idle;
            this.timer = new GameTime.AnimTimer();
            this.lightningStrength = 1.0f;
            this.lightningMod = 0.0f;
            this.lightningColor = new ClimateColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
            this.outColor = new ClimateColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
            this.x = 0;
            this.y = 0;
            this.distance = 0;
        }
    }
}
