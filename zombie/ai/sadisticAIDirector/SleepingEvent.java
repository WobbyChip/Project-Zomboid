// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.sadisticAIDirector;

import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.logger.ExceptionLogger;
import zombie.GameWindow;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.ZombieSpawnRecorder;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoDirections;
import zombie.VirtualZombieManager;
import zombie.vehicles.BaseVehicle;
import zombie.characters.Stats;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameClient;
import zombie.characters.Moodles.MoodleType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoStove;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.iso.weather.ClimateManager;
import zombie.characters.IsoPlayer;

public final class SleepingEvent
{
    public static final SleepingEvent instance;
    public static boolean zombiesInvasion;
    
    public void setPlayerFallAsleep(final IsoPlayer isoPlayer, final int n) {
        final SleepingEventData orCreateSleepingEventData = isoPlayer.getOrCreateSleepingEventData();
        orCreateSleepingEventData.reset();
        if (ClimateManager.getInstance().isRaining() && this.isExposedToPrecipitation(isoPlayer)) {
            orCreateSleepingEventData.bRaining = true;
            orCreateSleepingEventData.bWasRainingAtStart = true;
            orCreateSleepingEventData.rainTimeStartHours = GameTime.getInstance().getWorldAgeHours();
        }
        orCreateSleepingEventData.sleepingTime = (float)n;
        isoPlayer.setTimeOfSleep(GameTime.instance.getTimeOfDay());
        this.doDelayToSleep(isoPlayer);
        this.checkNightmare(isoPlayer, n);
        if (orCreateSleepingEventData.nightmareWakeUp > -1) {
            return;
        }
        if (SandboxOptions.instance.SleepingEvent.getValue() == 1 || !SleepingEvent.zombiesInvasion) {
            return;
        }
        if (isoPlayer.getCurrentSquare() != null && isoPlayer.getCurrentSquare().getZone() != null && isoPlayer.getCurrentSquare().getZone().haveConstruction) {
            return;
        }
        boolean b = false;
        if (((GameTime.instance.getHour() >= 0 && GameTime.instance.getHour() < 5) || GameTime.instance.getHour() > 18) && n >= 4) {
            b = true;
        }
        int n2 = 20;
        if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
            n2 = 45;
        }
        if (Rand.Next(100) <= n2 && isoPlayer.getCell().getZombieList().size() >= 1 && n >= 4) {
            int n3 = 0;
            if (isoPlayer.getCurrentBuilding() != null) {
                for (int i = 0; i < 3; ++i) {
                    for (int j = isoPlayer.getCurrentBuilding().getDef().getX() - 2; j < isoPlayer.getCurrentBuilding().getDef().getX2() + 2; ++j) {
                        for (int k = isoPlayer.getCurrentBuilding().getDef().getY() - 2; k < isoPlayer.getCurrentBuilding().getDef().getY2() + 2; ++k) {
                            final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(j, k, i);
                            if (gridSquare != null) {
                                if (gridSquare.haveElectricity() || GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier()) {
                                    for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                                        final IsoObject isoObject = gridSquare.getObjects().get(l);
                                        if (isoObject.getContainer() != null && (isoObject.getContainer().getType().equals("fridge") || isoObject.getContainer().getType().equals("freezer"))) {
                                            n3 += 3;
                                        }
                                        if (isoObject instanceof IsoStove && ((IsoStove)isoObject).Activated()) {
                                            n3 += 5;
                                        }
                                        if (isoObject instanceof IsoTelevision && ((IsoTelevision)isoObject).getDeviceData().getIsTurnedOn()) {
                                            n3 += 30;
                                        }
                                        if (isoObject instanceof IsoRadio && ((IsoRadio)isoObject).getDeviceData().getIsTurnedOn()) {
                                            n3 += 30;
                                        }
                                    }
                                }
                                final IsoWindow window = gridSquare.getWindow();
                                if (window != null) {
                                    n3 += this.checkWindowStatus(window);
                                }
                                final IsoDoor isoDoor = gridSquare.getIsoDoor();
                                if (isoDoor != null && isoDoor.isExteriorDoor(null) && isoDoor.IsOpen()) {
                                    n3 += 25;
                                    orCreateSleepingEventData.openDoor = isoDoor;
                                }
                            }
                        }
                    }
                }
                if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
                    n3 *= (int)1.5;
                }
                if (n3 > 70) {
                    n3 = 70;
                }
                if (!b) {
                    n3 /= 2;
                }
                if (Rand.Next(100) <= n3) {
                    orCreateSleepingEventData.forceWakeUpTime = Rand.Next(n - 4, n - 1);
                    orCreateSleepingEventData.zombiesIntruders = true;
                }
            }
        }
    }
    
    private void doDelayToSleep(final IsoPlayer isoPlayer) {
        float n = 0.3f;
        final float n2 = 2.0f;
        if (isoPlayer.Traits.Insomniac.isSet()) {
            n = 1.0f;
        }
        if (isoPlayer.getMoodles().getMoodleLevel(MoodleType.Pain) > 0) {
            n += 1.0f + isoPlayer.getMoodles().getMoodleLevel(MoodleType.Pain) * 0.2f;
        }
        if (isoPlayer.getMoodles().getMoodleLevel(MoodleType.Stress) > 0) {
            n *= 1.2f;
        }
        if ("badBed".equals(isoPlayer.getBedType())) {
            n *= 1.3f;
        }
        else if ("goodBed".equals(isoPlayer.getBedType())) {
            n *= 0.8f;
        }
        else if ("floor".equals(isoPlayer.getBedType())) {
            n *= 1.6f;
        }
        if (isoPlayer.Traits.NightOwl.isSet()) {
            n *= 0.5f;
        }
        if (isoPlayer.getSleepingTabletEffect() > 1000.0f) {
            n = 0.1f;
        }
        if (n > n2) {
            n = n2;
        }
        isoPlayer.setDelayToSleep(GameTime.instance.getTimeOfDay() + Rand.Next(0.0f, n));
    }
    
    private void checkNightmare(final IsoPlayer isoPlayer, final int n) {
        if (GameClient.bClient) {
            return;
        }
        final SleepingEventData orCreateSleepingEventData = isoPlayer.getOrCreateSleepingEventData();
        if (n >= 3 && Rand.Next(100) < 5 + isoPlayer.getMoodles().getMoodleLevel(MoodleType.Stress) * 10) {
            orCreateSleepingEventData.nightmareWakeUp = Rand.Next(3, n - 2);
        }
    }
    
    private int checkWindowStatus(final IsoWindow isoWindow) {
        IsoGridSquare isoGridSquare = isoWindow.getSquare();
        if (isoWindow.getSquare().getRoom() == null) {
            if (!isoWindow.north) {
                isoGridSquare = isoWindow.getSquare().getCell().getGridSquare(isoWindow.getSquare().getX() - 1, isoWindow.getSquare().getY(), isoWindow.getSquare().getZ());
            }
            else {
                isoGridSquare = isoWindow.getSquare().getCell().getGridSquare(isoWindow.getSquare().getX(), isoWindow.getSquare().getY() - 1, isoWindow.getSquare().getZ());
            }
        }
        boolean b = false;
        for (int i = 0; i < isoGridSquare.getRoom().lightSwitches.size(); ++i) {
            if (isoGridSquare.getRoom().lightSwitches.get(i).isActivated()) {
                b = true;
                break;
            }
        }
        if (b) {
            int n = 20;
            if (isoWindow.HasCurtains() != null && !isoWindow.HasCurtains().open) {
                n -= 17;
            }
            IsoBarricade isoBarricade = isoWindow.getBarricadeOnOppositeSquare();
            if (isoBarricade == null) {
                isoBarricade = isoWindow.getBarricadeOnSameSquare();
            }
            if (isoBarricade != null && (isoBarricade.getNumPlanks() > 4 || isoBarricade.isMetal())) {
                n -= 20;
            }
            if (n < 0) {
                n = 0;
            }
            if (isoGridSquare.getZ() > 0) {
                n /= 2;
            }
            return n;
        }
        int n2 = 5;
        if (isoWindow.HasCurtains() != null && !isoWindow.HasCurtains().open) {
            n2 -= 5;
        }
        IsoBarricade isoBarricade2 = isoWindow.getBarricadeOnOppositeSquare();
        if (isoBarricade2 == null) {
            isoBarricade2 = isoWindow.getBarricadeOnSameSquare();
        }
        if (isoBarricade2 != null && (isoBarricade2.getNumPlanks() > 3 || isoBarricade2.isMetal())) {
            n2 -= 5;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        if (isoGridSquare.getZ() > 0) {
            n2 /= 2;
        }
        return n2;
    }
    
    public void update(final IsoPlayer isoPlayer) {
        if (isoPlayer == null) {
            return;
        }
        final SleepingEventData orCreateSleepingEventData = isoPlayer.getOrCreateSleepingEventData();
        if (orCreateSleepingEventData.nightmareWakeUp == (int)isoPlayer.getAsleepTime()) {
            final Stats stats = isoPlayer.getStats();
            stats.Panic += 70.0f;
            final Stats stats2 = isoPlayer.getStats();
            stats2.stress += 0.5f;
            WorldSoundManager.instance.addSound(isoPlayer, (int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)isoPlayer.getZ(), 6, 1);
            SoundManager.instance.setMusicWakeState(isoPlayer, "WakeNightmare");
            this.wakeUp(isoPlayer);
        }
        if (orCreateSleepingEventData.forceWakeUpTime == (int)isoPlayer.getAsleepTime() && orCreateSleepingEventData.zombiesIntruders) {
            this.spawnZombieIntruders(isoPlayer);
            WorldSoundManager.instance.addSound(isoPlayer, (int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)isoPlayer.getZ(), 6, 1);
            SoundManager.instance.setMusicWakeState(isoPlayer, "WakeZombies");
            this.wakeUp(isoPlayer);
        }
        this.updateRain(isoPlayer);
        this.updateSnow(isoPlayer);
        this.updateTemperature(isoPlayer);
        this.updateWetness(isoPlayer);
    }
    
    private void updateRain(final IsoPlayer isoPlayer) {
        final SleepingEventData orCreateSleepingEventData = isoPlayer.getOrCreateSleepingEventData();
        if (!ClimateManager.getInstance().isRaining()) {
            orCreateSleepingEventData.bRaining = false;
            orCreateSleepingEventData.bWasRainingAtStart = false;
            orCreateSleepingEventData.rainTimeStartHours = -1.0;
            return;
        }
        if (!this.isExposedToPrecipitation(isoPlayer)) {
            return;
        }
        final double worldAgeHours = GameTime.getInstance().getWorldAgeHours();
        if (!orCreateSleepingEventData.bWasRainingAtStart) {
            if (!orCreateSleepingEventData.bRaining) {
                orCreateSleepingEventData.rainTimeStartHours = worldAgeHours;
            }
            if (orCreateSleepingEventData.getHoursSinceRainStarted() >= 0.16666666666666666) {}
        }
        orCreateSleepingEventData.bRaining = true;
    }
    
    private void updateSnow(final IsoPlayer isoPlayer) {
        if (!ClimateManager.getInstance().isSnowing()) {
            return;
        }
        if (!this.isExposedToPrecipitation(isoPlayer)) {
            return;
        }
    }
    
    private void updateTemperature(final IsoPlayer isoPlayer) {
    }
    
    private void updateWetness(final IsoPlayer isoPlayer) {
    }
    
    private boolean isExposedToPrecipitation(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.getCurrentSquare() == null) {
            return false;
        }
        if (isoGameCharacter.getCurrentSquare().isInARoom() || isoGameCharacter.getCurrentSquare().haveRoof) {
            return false;
        }
        if (isoGameCharacter.getBed() != null && "Tent".equals(isoGameCharacter.getBed().getName())) {
            return false;
        }
        final BaseVehicle vehicle = isoGameCharacter.getVehicle();
        return vehicle == null || !vehicle.hasRoof(vehicle.getSeat(isoGameCharacter));
    }
    
    private void spawnZombieIntruders(final IsoPlayer target) {
        final SleepingEventData orCreateSleepingEventData = target.getOrCreateSleepingEventData();
        IsoGridSquare e = null;
        if (orCreateSleepingEventData.openDoor != null) {
            e = orCreateSleepingEventData.openDoor.getSquare();
        }
        else {
            orCreateSleepingEventData.weakestWindow = this.getWeakestWindow(target);
            if (orCreateSleepingEventData.weakestWindow != null && orCreateSleepingEventData.weakestWindow.getZ() == 0.0f) {
                if (!orCreateSleepingEventData.weakestWindow.north) {
                    if (orCreateSleepingEventData.weakestWindow.getSquare().getRoom() == null) {
                        e = orCreateSleepingEventData.weakestWindow.getSquare();
                    }
                    else {
                        e = orCreateSleepingEventData.weakestWindow.getSquare().getCell().getGridSquare(orCreateSleepingEventData.weakestWindow.getSquare().getX() - 1, orCreateSleepingEventData.weakestWindow.getSquare().getY(), orCreateSleepingEventData.weakestWindow.getSquare().getZ());
                    }
                }
                else if (orCreateSleepingEventData.weakestWindow.getSquare().getRoom() == null) {
                    e = orCreateSleepingEventData.weakestWindow.getSquare();
                }
                else {
                    e = orCreateSleepingEventData.weakestWindow.getSquare().getCell().getGridSquare(orCreateSleepingEventData.weakestWindow.getSquare().getX(), orCreateSleepingEventData.weakestWindow.getSquare().getY() + 1, orCreateSleepingEventData.weakestWindow.getSquare().getZ());
                }
                IsoBarricade isoBarricade = orCreateSleepingEventData.weakestWindow.getBarricadeOnOppositeSquare();
                if (isoBarricade == null) {
                    isoBarricade = orCreateSleepingEventData.weakestWindow.getBarricadeOnSameSquare();
                }
                if (isoBarricade != null) {
                    isoBarricade.Damage(Rand.Next(500, 900));
                }
                else {
                    orCreateSleepingEventData.weakestWindow.Damage(200.0f);
                    orCreateSleepingEventData.weakestWindow.smashWindow();
                    if (orCreateSleepingEventData.weakestWindow.HasCurtains() != null) {
                        orCreateSleepingEventData.weakestWindow.removeSheet(null);
                    }
                    if (e != null) {
                        e.addBrokenGlass();
                    }
                }
            }
        }
        target.getStats().setPanic(target.getStats().getPanic() + Rand.Next(30, 60));
        if (e == null) {
            return;
        }
        if (IsoWorld.getZombiesEnabled()) {
            for (int n = Rand.Next(3) + 1, i = 0; i < n; ++i) {
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(e);
                final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
                if (realZombieAlways != null) {
                    realZombieAlways.setTarget(target);
                    realZombieAlways.pathToCharacter(target);
                    realZombieAlways.spotted(target, true);
                    ZombieSpawnRecorder.instance.record(realZombieAlways, this.getClass().getSimpleName());
                }
            }
        }
    }
    
    private IsoWindow getWeakestWindow(final IsoPlayer isoPlayer) {
        IsoWindow isoWindow = null;
        int n = 0;
        for (int i = isoPlayer.getCurrentBuilding().getDef().getX() - 2; i < isoPlayer.getCurrentBuilding().getDef().getX2() + 2; ++i) {
            for (int j = isoPlayer.getCurrentBuilding().getDef().getY() - 2; j < isoPlayer.getCurrentBuilding().getDef().getY2() + 2; ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(i, j, 0);
                if (gridSquare != null) {
                    final IsoWindow window = gridSquare.getWindow();
                    if (window != null) {
                        final int checkWindowStatus = this.checkWindowStatus(window);
                        if (checkWindowStatus > n) {
                            n = checkWindowStatus;
                            isoWindow = window;
                        }
                    }
                }
            }
        }
        return isoWindow;
    }
    
    public void wakeUp(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null) {
            return;
        }
        this.wakeUp(isoGameCharacter, false);
    }
    
    public void wakeUp(final IsoGameCharacter isoGameCharacter, final boolean b) {
        final SleepingEventData orCreateSleepingEventData = isoGameCharacter.getOrCreateSleepingEventData();
        if (GameClient.bClient && !b) {
            GameClient.instance.wakeUpPlayer((IsoPlayer)isoGameCharacter);
        }
        boolean b2 = false;
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (isoPlayer != null && isoPlayer.isLocalPlayer()) {
            UIManager.setFadeBeforeUI(isoPlayer.getPlayerNum(), true);
            UIManager.FadeIn(isoPlayer.getPlayerNum(), 2.0);
            if (!GameClient.bClient && IsoPlayer.allPlayersAsleep()) {
                UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                b2 = true;
            }
            isoGameCharacter.setLastHourSleeped((int)isoPlayer.getHoursSurvived());
        }
        isoGameCharacter.setForceWakeUpTime(-1.0f);
        isoGameCharacter.setAsleep(false);
        if (b2) {
            try {
                GameWindow.save(true);
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        final BodyPart bodyPart = isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck);
        final float n = orCreateSleepingEventData.sleepingTime / 8.0f;
        if ("goodBed".equals(isoGameCharacter.getBedType())) {
            isoGameCharacter.getStats().setFatigue(isoGameCharacter.getStats().getFatigue() - Rand.Next(0.05f, 0.12f) * n);
            if (isoGameCharacter.getStats().getFatigue() < 0.0f) {
                isoGameCharacter.getStats().setFatigue(0.0f);
            }
        }
        else if ("badBed".equals(isoGameCharacter.getBedType())) {
            isoGameCharacter.getStats().setFatigue(isoGameCharacter.getStats().getFatigue() + Rand.Next(0.1f, 0.2f) * n);
            if (Rand.Next(5) == 0) {
                bodyPart.AddDamage(Rand.Next(5.0f, 15.0f));
                bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(30.0f, 50.0f));
            }
        }
        else if ("floor".equals(isoGameCharacter.getBedType())) {
            isoGameCharacter.getStats().setFatigue(isoGameCharacter.getStats().getFatigue() + Rand.Next(0.15f, 0.25f) * n);
            if (Rand.Next(5) == 0) {
                bodyPart.AddDamage(Rand.Next(10.0f, 20.0f));
                bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(30.0f, 50.0f));
            }
        }
        else if (Rand.Next(10) == 0) {
            bodyPart.AddDamage(Rand.Next(3.0f, 12.0f));
            bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(10.0f, 30.0f));
        }
        orCreateSleepingEventData.reset();
    }
    
    static {
        instance = new SleepingEvent();
        SleepingEvent.zombiesInvasion = false;
    }
}
