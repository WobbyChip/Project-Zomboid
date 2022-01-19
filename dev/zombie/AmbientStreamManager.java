// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoUtils;
import zombie.iso.IsoCamera;
import zombie.input.Mouse;
import zombie.Lua.LuaEventManager;
import fmod.fmod.FMODSoundEmitter;
import java.util.Iterator;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import fmod.javafmod;
import zombie.iso.weather.ClimateManager;
import zombie.iso.objects.RainManager;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import zombie.iso.Vector2;
import zombie.audio.parameters.ParameterRoomSize;
import zombie.audio.parameters.ParameterMoodlePanic;
import zombie.audio.parameters.ParameterInside;
import zombie.audio.parameters.ParameterHardOfHearing;
import zombie.audio.parameters.ParameterClosestWallDistance;
import zombie.audio.parameters.ParameterCameraZoom;
import zombie.audio.parameters.ParameterZoneWaterSide;
import zombie.audio.parameters.ParameterZone;
import zombie.audio.parameters.ParameterWindIntensity;
import zombie.audio.parameters.ParameterWeatherEvent;
import zombie.audio.parameters.ParameterTemperature;
import zombie.audio.parameters.ParameterTimeOfDay;
import zombie.audio.parameters.ParameterStorm;
import zombie.audio.parameters.ParameterSnowIntensity;
import zombie.audio.parameters.ParameterSeason;
import zombie.audio.parameters.ParameterRainIntensity;
import zombie.audio.parameters.ParameterFogIntensity;
import java.util.ArrayDeque;
import zombie.iso.Alarm;
import java.util.ArrayList;

public final class AmbientStreamManager extends BaseAmbientStreamManager
{
    public static int OneInAmbienceChance;
    public static int MaxAmbientCount;
    public static float MaxRange;
    private final ArrayList<Alarm> alarmList;
    public static BaseAmbientStreamManager instance;
    public final ArrayList<Ambient> ambient;
    public final ArrayList<WorldSoundEmitter> worldEmitters;
    public final ArrayDeque<WorldSoundEmitter> freeEmitters;
    public final ArrayList<AmbientLoop> allAmbient;
    public final ArrayList<AmbientLoop> nightAmbient;
    public final ArrayList<AmbientLoop> dayAmbient;
    public final ArrayList<AmbientLoop> rainAmbient;
    public final ArrayList<AmbientLoop> indoorAmbient;
    public final ArrayList<AmbientLoop> outdoorAmbient;
    public final ArrayList<AmbientLoop> windAmbient;
    public boolean initialized;
    private final ParameterFogIntensity parameterFogIntensity;
    private final ParameterRainIntensity parameterRainIntensity;
    private final ParameterSeason parameterSeason;
    private final ParameterSnowIntensity parameterSnowIntensity;
    private final ParameterStorm parameterStorm;
    private final ParameterTimeOfDay parameterTimeOfDay;
    private final ParameterTemperature parameterTemperature;
    private final ParameterWeatherEvent parameterWeatherEvent;
    private final ParameterWindIntensity parameterWindIntensity;
    private final ParameterZone parameterZoneDeepForest;
    private final ParameterZone parameterZoneFarm;
    private final ParameterZone parameterZoneForest;
    private final ParameterZone parameterZoneNav;
    private final ParameterZone parameterZoneTown;
    private final ParameterZone parameterZoneTrailerPark;
    private final ParameterZone parameterZoneVegetation;
    private final ParameterZoneWaterSide parameterZoneWaterSide;
    private final ParameterCameraZoom parameterCameraZoom;
    private final ParameterClosestWallDistance parameterClosestWallDistance;
    private final ParameterHardOfHearing parameterHardOfHearing;
    private final ParameterInside parameterInside;
    private final ParameterMoodlePanic parameterMoodlePanic;
    private final ParameterRoomSize parameterRoomSize;
    private final Vector2 tempo;
    
    public AmbientStreamManager() {
        this.alarmList = new ArrayList<Alarm>();
        this.ambient = new ArrayList<Ambient>();
        this.worldEmitters = new ArrayList<WorldSoundEmitter>();
        this.freeEmitters = new ArrayDeque<WorldSoundEmitter>();
        this.allAmbient = new ArrayList<AmbientLoop>();
        this.nightAmbient = new ArrayList<AmbientLoop>();
        this.dayAmbient = new ArrayList<AmbientLoop>();
        this.rainAmbient = new ArrayList<AmbientLoop>();
        this.indoorAmbient = new ArrayList<AmbientLoop>();
        this.outdoorAmbient = new ArrayList<AmbientLoop>();
        this.windAmbient = new ArrayList<AmbientLoop>();
        this.initialized = false;
        this.parameterFogIntensity = new ParameterFogIntensity();
        this.parameterRainIntensity = new ParameterRainIntensity();
        this.parameterSeason = new ParameterSeason();
        this.parameterSnowIntensity = new ParameterSnowIntensity();
        this.parameterStorm = new ParameterStorm();
        this.parameterTimeOfDay = new ParameterTimeOfDay();
        this.parameterTemperature = new ParameterTemperature();
        this.parameterWeatherEvent = new ParameterWeatherEvent();
        this.parameterWindIntensity = new ParameterWindIntensity();
        this.parameterZoneDeepForest = new ParameterZone("ZoneDeepForest", "DeepForest");
        this.parameterZoneFarm = new ParameterZone("ZoneFarm", "Farm");
        this.parameterZoneForest = new ParameterZone("ZoneForest", "Forest");
        this.parameterZoneNav = new ParameterZone("ZoneNav", "Nav");
        this.parameterZoneTown = new ParameterZone("ZoneTown", "TownZone");
        this.parameterZoneTrailerPark = new ParameterZone("ZoneTrailerPark", "TrailerPark");
        this.parameterZoneVegetation = new ParameterZone("ZoneVegetation", "Vegitation");
        this.parameterZoneWaterSide = new ParameterZoneWaterSide();
        this.parameterCameraZoom = new ParameterCameraZoom();
        this.parameterClosestWallDistance = new ParameterClosestWallDistance();
        this.parameterHardOfHearing = new ParameterHardOfHearing();
        this.parameterInside = new ParameterInside();
        this.parameterMoodlePanic = new ParameterMoodlePanic();
        this.parameterRoomSize = new ParameterRoomSize();
        this.tempo = new Vector2();
    }
    
    public static BaseAmbientStreamManager getInstance() {
        return AmbientStreamManager.instance;
    }
    
    @Override
    public void update() {
        if (!this.initialized) {
            return;
        }
        if (GameTime.isGamePaused()) {
            return;
        }
        if (IsoPlayer.getInstance() == null) {
            return;
        }
        if (IsoPlayer.getInstance().getCurrentSquare() == null) {
            return;
        }
        this.parameterFogIntensity.update();
        this.parameterRainIntensity.update();
        this.parameterSeason.update();
        this.parameterSnowIntensity.update();
        this.parameterStorm.update();
        this.parameterTemperature.update();
        this.parameterTimeOfDay.update();
        this.parameterWeatherEvent.update();
        this.parameterWindIntensity.update();
        this.parameterZoneDeepForest.update();
        this.parameterZoneFarm.update();
        this.parameterZoneForest.update();
        this.parameterZoneNav.update();
        this.parameterZoneVegetation.update();
        this.parameterZoneTown.update();
        this.parameterZoneTrailerPark.update();
        this.parameterZoneWaterSide.update();
        this.parameterCameraZoom.update();
        this.parameterClosestWallDistance.update();
        this.parameterHardOfHearing.update();
        this.parameterInside.update();
        this.parameterMoodlePanic.update();
        this.parameterRoomSize.update();
        final float timeOfDay = GameTime.instance.getTimeOfDay();
        for (int i = 0; i < this.worldEmitters.size(); ++i) {
            final WorldSoundEmitter worldSoundEmitter = this.worldEmitters.get(i);
            if (worldSoundEmitter.daytime != null) {
                if (IsoWorld.instance.CurrentCell.getGridSquare(worldSoundEmitter.x, worldSoundEmitter.y, worldSoundEmitter.z) == null) {
                    worldSoundEmitter.fmodEmitter.stopAll();
                    SoundManager.instance.unregisterEmitter((BaseSoundEmitter)worldSoundEmitter.fmodEmitter);
                    this.worldEmitters.remove(worldSoundEmitter);
                    this.freeEmitters.add(worldSoundEmitter);
                    --i;
                }
                else {
                    if (timeOfDay > worldSoundEmitter.dawn && timeOfDay < worldSoundEmitter.dusk) {
                        if (worldSoundEmitter.fmodEmitter.isEmpty()) {
                            worldSoundEmitter.channel = worldSoundEmitter.fmodEmitter.playAmbientLoopedImpl(worldSoundEmitter.daytime);
                        }
                    }
                    else if (!worldSoundEmitter.fmodEmitter.isEmpty()) {
                        worldSoundEmitter.fmodEmitter.stopSound(worldSoundEmitter.channel);
                        worldSoundEmitter.channel = 0L;
                    }
                    if (!worldSoundEmitter.fmodEmitter.isEmpty() && (IsoWorld.instance.emitterUpdate || worldSoundEmitter.fmodEmitter.hasSoundsToStart())) {
                        worldSoundEmitter.fmodEmitter.tick();
                    }
                }
            }
            else if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().Traits.Deaf.isSet()) {
                worldSoundEmitter.fmodEmitter.stopAll();
                SoundManager.instance.unregisterEmitter((BaseSoundEmitter)worldSoundEmitter.fmodEmitter);
                this.worldEmitters.remove(worldSoundEmitter);
                this.freeEmitters.add(worldSoundEmitter);
                --i;
            }
            else if (IsoWorld.instance.CurrentCell.getGridSquare(worldSoundEmitter.x, worldSoundEmitter.y, worldSoundEmitter.z) == null || worldSoundEmitter.fmodEmitter.isEmpty()) {
                worldSoundEmitter.fmodEmitter.stopAll();
                SoundManager.instance.unregisterEmitter((BaseSoundEmitter)worldSoundEmitter.fmodEmitter);
                this.worldEmitters.remove(worldSoundEmitter);
                this.freeEmitters.add(worldSoundEmitter);
                --i;
            }
            else {
                worldSoundEmitter.fmodEmitter.x = worldSoundEmitter.x;
                worldSoundEmitter.fmodEmitter.y = worldSoundEmitter.y;
                worldSoundEmitter.fmodEmitter.z = worldSoundEmitter.z;
                if (IsoWorld.instance.emitterUpdate || worldSoundEmitter.fmodEmitter.hasSoundsToStart()) {
                    worldSoundEmitter.fmodEmitter.tick();
                }
            }
        }
        final float night = GameTime.instance.getNight();
        final boolean b = IsoPlayer.getInstance().getCurrentSquare().getRoom() != null;
        final boolean booleanValue = RainManager.isRaining();
        for (int j = 0; j < this.allAmbient.size(); ++j) {
            this.allAmbient.get(j).targVol = 1.0f;
        }
        for (int k = 0; k < this.nightAmbient.size(); ++k) {
            final AmbientLoop ambientLoop = this.nightAmbient.get(k);
            ambientLoop.targVol *= night;
        }
        for (int l = 0; l < this.dayAmbient.size(); ++l) {
            final AmbientLoop ambientLoop2 = this.dayAmbient.get(l);
            ambientLoop2.targVol *= 1.0f - night;
        }
        for (int index = 0; index < this.indoorAmbient.size(); ++index) {
            final AmbientLoop ambientLoop3 = this.indoorAmbient.get(index);
            ambientLoop3.targVol *= (b ? 0.8f : 0.0f);
        }
        for (int index2 = 0; index2 < this.outdoorAmbient.size(); ++index2) {
            final AmbientLoop ambientLoop4 = this.outdoorAmbient.get(index2);
            ambientLoop4.targVol *= (b ? 0.15f : 0.8f);
        }
        for (int index3 = 0; index3 < this.rainAmbient.size(); ++index3) {
            final AmbientLoop ambientLoop5 = this.rainAmbient.get(index3);
            ambientLoop5.targVol *= (booleanValue ? 1.0f : 0.0f);
            if (this.rainAmbient.get(index3).channel != 0L) {
                javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.rainAmbient.get(index3).channel, "RainIntensity", ClimateManager.getInstance().getPrecipitationIntensity());
            }
        }
        for (int index4 = 0; index4 < this.allAmbient.size(); ++index4) {
            this.allAmbient.get(index4).update();
        }
        for (int index5 = 0; index5 < this.alarmList.size(); ++index5) {
            this.alarmList.get(index5).update();
            if (this.alarmList.get(index5).finished) {
                this.alarmList.remove(index5);
                --index5;
            }
        }
        this.doOneShotAmbients();
    }
    
    @Override
    public void doOneShotAmbients() {
        for (int i = 0; i < this.ambient.size(); ++i) {
            final Ambient ambient = this.ambient.get(i);
            if (ambient.finished()) {
                DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ambient.name));
                this.ambient.remove(i--);
            }
            else {
                ambient.update();
            }
        }
    }
    
    public void addRandomAmbient() {
        if (Core.GameMode.equals("LastStand") || Core.GameMode.equals("Tutorial")) {
            return;
        }
        final ArrayList<IsoPlayer> list = new ArrayList<IsoPlayer>();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer e = IsoPlayer.players[i];
            if (e != null && e.isAlive()) {
                list.add(e);
            }
        }
        if (list.isEmpty()) {
            return;
        }
        final IsoPlayer isoPlayer = list.get(Rand.Next(list.size()));
        String s = "";
        if (GameTime.instance.getHour() > 7 && GameTime.instance.getHour() < 21) {
            switch (Rand.Next(3)) {
                case 0: {
                    if (Rand.Next(10) < 2) {
                        s = "MetaDogBark";
                        break;
                    }
                    break;
                }
                case 1: {
                    if (Rand.Next(10) < 3) {
                        s = "MetaScream";
                        break;
                    }
                    break;
                }
            }
        }
        else {
            switch (Rand.Next(5)) {
                case 0: {
                    if (Rand.Next(10) < 2) {
                        s = "MetaDogBark";
                        break;
                    }
                    break;
                }
                case 1: {
                    if (Rand.Next(13) < 3) {
                        s = "MetaScream";
                        break;
                    }
                    break;
                }
                case 2: {
                    s = "MetaOwl";
                    break;
                }
                case 3: {
                    s = "MetaWolfHowl";
                    break;
                }
            }
        }
        if (s.isEmpty()) {
            return;
        }
        final float x = isoPlayer.x;
        final float y = isoPlayer.y;
        final double n = Rand.Next(-3.1415927f, 3.1415927f);
        this.tempo.x = (float)Math.cos(n);
        this.tempo.y = (float)Math.sin(n);
        this.tempo.setLength(1000.0f);
        final float n2 = x + this.tempo.x;
        final float n3 = y + this.tempo.y;
        if (!GameClient.bClient) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;FF)Ljava/lang/String;, s, Math.abs(n2 - isoPlayer.x), Math.abs(n3 - isoPlayer.y)));
            this.ambient.add(new Ambient(s, n2, n3, 50.0f, Rand.Next(0.2f, 0.5f)));
        }
    }
    
    @Override
    public void addBlend(final String s, final float n, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final AmbientLoop ambientLoop = new AmbientLoop(0.0f, s, n);
        this.allAmbient.add(ambientLoop);
        if (b) {
            this.indoorAmbient.add(ambientLoop);
        }
        else {
            this.outdoorAmbient.add(ambientLoop);
        }
        if (b2) {
            this.rainAmbient.add(ambientLoop);
        }
        if (b3) {
            this.nightAmbient.add(ambientLoop);
        }
        if (b4) {
            this.dayAmbient.add(ambientLoop);
        }
    }
    
    @Override
    public void init() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
    }
    
    @Override
    public void doGunEvent() {
        final ArrayList<IsoPlayer> list = new ArrayList<IsoPlayer>();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer e = IsoPlayer.players[i];
            if (e != null && e.isAlive()) {
                list.add(e);
            }
        }
        if (list.isEmpty()) {
            return;
        }
        final IsoPlayer isoPlayer = list.get(Rand.Next(list.size()));
        String s = null;
        switch (Rand.Next(6)) {
            case 0: {
                s = "MetaAssaultRifle1";
                break;
            }
            case 1: {
                s = "MetaPistol1";
                break;
            }
            case 2: {
                s = "MetaShotgun1";
                break;
            }
            case 3: {
                s = "MetaPistol2";
                break;
            }
            case 4: {
                s = "MetaPistol3";
                break;
            }
            case 5: {
                s = "MetaShotgun1";
                break;
            }
        }
        final float x = isoPlayer.x;
        final float y = isoPlayer.y;
        final int n = 600;
        final double n2 = Rand.Next(-3.1415927f, 3.1415927f);
        this.tempo.x = (float)Math.cos(n2);
        this.tempo.y = (float)Math.sin(n2);
        this.tempo.setLength((float)(n - 100));
        final float n3 = x + this.tempo.x;
        final float n4 = y + this.tempo.y;
        WorldSoundManager.instance.addSound(null, (int)n3, (int)n4, 0, n, n);
        this.ambient.add(new Ambient(s, n3, n4, 700.0f, 1.0f));
    }
    
    @Override
    public void doAlarm(final RoomDef roomDef) {
        if (roomDef != null && roomDef.building != null && roomDef.building.bAlarmed) {
            roomDef.building.bAlarmed = false;
            roomDef.building.setAllExplored(true);
            this.alarmList.add(new Alarm(roomDef.x + roomDef.getW() / 2, roomDef.y + roomDef.getH() / 2));
        }
    }
    
    @Override
    public void stop() {
        final Iterator<AmbientLoop> iterator = this.allAmbient.iterator();
        while (iterator.hasNext()) {
            iterator.next().stop();
        }
        this.allAmbient.clear();
        this.ambient.clear();
        this.dayAmbient.clear();
        this.indoorAmbient.clear();
        this.nightAmbient.clear();
        this.outdoorAmbient.clear();
        this.rainAmbient.clear();
        this.windAmbient.clear();
        this.alarmList.clear();
        this.initialized = false;
    }
    
    @Override
    public void addAmbient(final String s, final int n, final int n2, final int n3, final float n4) {
        if (!GameClient.bClient) {
            return;
        }
        this.ambient.add(new Ambient(s, (float)n, (float)n2, (float)n3, n4, true));
    }
    
    @Override
    public void addAmbientEmitter(final float n, final float n2, final int n3, final String s) {
        final WorldSoundEmitter e = this.freeEmitters.isEmpty() ? new WorldSoundEmitter() : this.freeEmitters.pop();
        e.x = n;
        e.y = n2;
        e.z = (float)n3;
        e.daytime = null;
        if (e.fmodEmitter == null) {
            e.fmodEmitter = new FMODSoundEmitter();
        }
        e.fmodEmitter.x = n;
        e.fmodEmitter.y = n2;
        e.fmodEmitter.z = (float)n3;
        e.channel = e.fmodEmitter.playAmbientLoopedImpl(s);
        e.fmodEmitter.randomStart();
        SoundManager.instance.registerEmitter((BaseSoundEmitter)e.fmodEmitter);
        this.worldEmitters.add(e);
    }
    
    @Override
    public void addDaytimeAmbientEmitter(final float n, final float n2, final int n3, final String daytime) {
        final WorldSoundEmitter e = this.freeEmitters.isEmpty() ? new WorldSoundEmitter() : this.freeEmitters.pop();
        e.x = n;
        e.y = n2;
        e.z = (float)n3;
        if (e.fmodEmitter == null) {
            e.fmodEmitter = new FMODSoundEmitter();
        }
        e.fmodEmitter.x = n;
        e.fmodEmitter.y = n2;
        e.fmodEmitter.z = (float)n3;
        e.daytime = daytime;
        e.dawn = Rand.Next(7.0f, 8.0f);
        e.dusk = Rand.Next(19.0f, 20.0f);
        SoundManager.instance.registerEmitter((BaseSoundEmitter)e.fmodEmitter);
        this.worldEmitters.add(e);
    }
    
    static {
        AmbientStreamManager.OneInAmbienceChance = 2500;
        AmbientStreamManager.MaxAmbientCount = 20;
        AmbientStreamManager.MaxRange = 1000.0f;
    }
    
    public static final class WorldSoundEmitter
    {
        public FMODSoundEmitter fmodEmitter;
        public float x;
        public float y;
        public float z;
        public long channel;
        public String daytime;
        public float dawn;
        public float dusk;
        
        public WorldSoundEmitter() {
            this.channel = -1L;
        }
    }
    
    public static final class Ambient
    {
        public float x;
        public float y;
        public String name;
        float radius;
        float volume;
        int worldSoundRadius;
        int worldSoundVolume;
        public boolean trackMouse;
        final FMODSoundEmitter emitter;
        
        public Ambient(final String s, final float n, final float n2, final float n3, final float n4) {
            this(s, n, n2, n3, n4, false);
        }
        
        public Ambient(final String name, final float f, final float f2, final float radius, final float volume, final boolean b) {
            this.trackMouse = false;
            this.emitter = new FMODSoundEmitter();
            this.name = name;
            this.x = f;
            this.y = f2;
            this.radius = radius;
            this.volume = volume;
            this.emitter.x = f;
            this.emitter.y = f2;
            this.emitter.z = 0.0f;
            this.emitter.playAmbientSound(name);
            this.update();
            LuaEventManager.triggerEvent("OnAmbientSound", name, f, f2);
        }
        
        public boolean finished() {
            return this.emitter.isEmpty();
        }
        
        public void update() {
            this.emitter.tick();
            if (this.trackMouse && IsoPlayer.getInstance() != null) {
                final float n = (float)Mouse.getXA();
                final float n2 = (float)Mouse.getYA();
                final float n3 = n - IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex());
                final float n4 = n2 - IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex());
                final float n5 = n3 * Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
                final float n6 = n4 * Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
                final int n7 = (int)IsoPlayer.getInstance().getZ();
                this.emitter.x = (float)(int)IsoUtils.XToIso(n5, n6, (float)n7);
                this.emitter.y = (float)(int)IsoUtils.YToIso(n5, n6, (float)n7);
            }
            if (!GameClient.bClient && this.worldSoundRadius > 0 && this.worldSoundVolume > 0) {
                WorldSoundManager.instance.addSound(null, (int)this.x, (int)this.y, 0, this.worldSoundRadius, this.worldSoundVolume);
            }
        }
        
        public void repeatWorldSounds(final int worldSoundRadius, final int worldSoundVolume) {
            this.worldSoundRadius = worldSoundRadius;
            this.worldSoundVolume = worldSoundVolume;
        }
        
        private IsoGameCharacter getClosestListener(final float n, final float n2) {
            IsoGameCharacter isoGameCharacter = null;
            float n3 = Float.MAX_VALUE;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && isoPlayer.getCurrentSquare() != null) {
                    float distanceToSquared = IsoUtils.DistanceToSquared(isoPlayer.getX(), isoPlayer.getY(), n, n2);
                    if (isoPlayer.Traits.HardOfHearing.isSet()) {
                        distanceToSquared *= 4.5f;
                    }
                    if (isoPlayer.Traits.Deaf.isSet()) {
                        distanceToSquared = Float.MAX_VALUE;
                    }
                    if (distanceToSquared < n3) {
                        isoGameCharacter = isoPlayer;
                        n3 = distanceToSquared;
                    }
                }
            }
            return isoGameCharacter;
        }
    }
    
    public static final class AmbientLoop
    {
        public static float volChangeAmount;
        public float targVol;
        public float currVol;
        public String name;
        public float volumedelta;
        public long channel;
        public final FMODSoundEmitter emitter;
        
        public AmbientLoop(final float targVol, final String s, final float volumedelta) {
            this.volumedelta = 1.0f;
            this.channel = -1L;
            this.emitter = new FMODSoundEmitter();
            this.volumedelta = volumedelta;
            this.channel = this.emitter.playAmbientLoopedImpl(s);
            this.targVol = targVol;
            this.currVol = 0.0f;
            this.update();
        }
        
        public void update() {
            if (this.targVol > this.currVol) {
                this.currVol += AmbientLoop.volChangeAmount;
                if (this.currVol > this.targVol) {
                    this.currVol = this.targVol;
                }
            }
            if (this.targVol < this.currVol) {
                this.currVol -= AmbientLoop.volChangeAmount;
                if (this.currVol < this.targVol) {
                    this.currVol = this.targVol;
                }
            }
            this.emitter.setVolumeAll(this.currVol * this.volumedelta);
            this.emitter.tick();
        }
        
        public void stop() {
            this.emitter.stopAll();
        }
        
        static {
            AmbientLoop.volChangeAmount = 0.01f;
        }
    }
}
