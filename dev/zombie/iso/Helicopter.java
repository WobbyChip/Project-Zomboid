// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.audio.GameSound;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.SoundManager;
import fmod.javafmod;
import zombie.GameSounds;
import fmod.fmod.FMODManager;
import zombie.core.Core;
import zombie.WorldSoundManager;
import zombie.GameTime;
import zombie.debug.DebugLog;
import zombie.core.Rand;
import zombie.characters.IsoPlayer;
import java.util.ArrayList;
import zombie.network.GameClient;
import zombie.network.GameServer;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import zombie.characters.IsoGameCharacter;

public class Helicopter
{
    private static float MAX_BOTHER_SECONDS;
    private static float MAX_UNSEEN_SECONDS;
    private static int RADIUS_HOVER;
    private static int RADIUS_SEARCH;
    protected State state;
    public IsoGameCharacter target;
    protected float timeSinceChopperSawPlayer;
    protected float hoverTime;
    protected float searchTime;
    public float x;
    public float y;
    protected float targetX;
    protected float targetY;
    protected Vector2 move;
    protected boolean bActive;
    protected static long inst;
    protected static FMOD_STUDIO_EVENT_DESCRIPTION event;
    protected boolean bSoundStarted;
    protected float volume;
    protected float occlusion;
    
    public Helicopter() {
        this.move = new Vector2();
    }
    
    public void pickRandomTarget() {
        ArrayList<IsoPlayer> players;
        if (GameServer.bServer) {
            players = GameServer.getPlayers();
        }
        else {
            if (GameClient.bClient) {
                throw new IllegalStateException("can't call this on the client");
            }
            players = new ArrayList<IsoPlayer>();
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer e = IsoPlayer.players[i];
                if (e != null && e.isAlive()) {
                    players.add(e);
                }
            }
        }
        if (players.isEmpty()) {
            this.bActive = false;
            this.target = null;
            return;
        }
        this.setTarget(players.get(Rand.Next(players.size())));
    }
    
    public void setTarget(final IsoGameCharacter target) {
        this.target = target;
        this.x = this.target.x + 1000.0f;
        this.y = this.target.y + 1000.0f;
        this.targetX = this.target.x;
        this.targetY = this.target.y;
        this.move.x = this.targetX - this.x;
        this.move.y = this.targetY - this.y;
        this.move.normalize();
        this.move.setLength(0.5f);
        this.state = State.Arriving;
        this.bActive = true;
        DebugLog.log("chopper: activated");
    }
    
    protected void changeState(final State state) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/iso/Helicopter$State;Lzombie/iso/Helicopter$State;)Ljava/lang/String;, this.state, state));
        this.state = state;
    }
    
    public void update() {
        if (!this.bActive) {
            return;
        }
        if (GameClient.bClient) {
            this.updateSound();
            return;
        }
        float trueMultiplier = 1.0f;
        if (GameServer.bServer) {
            if (!GameServer.Players.contains(this.target)) {
                this.target = null;
            }
        }
        else {
            trueMultiplier = GameTime.getInstance().getTrueMultiplier();
        }
        switch (this.state) {
            case Arriving: {
                if (this.target == null || this.target.isDead()) {
                    this.changeState(State.Leaving);
                    break;
                }
                if (IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY) < 4.0f) {
                    this.changeState(State.Hovering);
                    this.hoverTime = 0.0f;
                    this.searchTime = 0.0f;
                    this.timeSinceChopperSawPlayer = 0.0f;
                    break;
                }
                this.targetX = this.target.x;
                this.targetY = this.target.y;
                this.move.x = this.targetX - this.x;
                this.move.y = this.targetY - this.y;
                this.move.normalize();
                this.move.setLength(0.75f);
                break;
            }
            case Hovering: {
                if (this.target == null || this.target.isDead()) {
                    this.changeState(State.Leaving);
                    break;
                }
                this.hoverTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * trueMultiplier;
                if (this.hoverTime + this.searchTime > Helicopter.MAX_BOTHER_SECONDS) {
                    this.changeState(State.Leaving);
                    break;
                }
                if (!this.isTargetVisible()) {
                    this.timeSinceChopperSawPlayer += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * trueMultiplier;
                    if (this.timeSinceChopperSawPlayer > Helicopter.MAX_UNSEEN_SECONDS) {
                        this.changeState(State.Searching);
                        break;
                    }
                }
                if (IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY) < 1.0f) {
                    this.targetX = this.target.x + (Rand.Next(Helicopter.RADIUS_HOVER * 2) - Helicopter.RADIUS_HOVER);
                    this.targetY = this.target.y + (Rand.Next(Helicopter.RADIUS_HOVER * 2) - Helicopter.RADIUS_HOVER);
                    this.move.x = this.targetX - this.x;
                    this.move.y = this.targetY - this.y;
                    this.move.normalize();
                    this.move.setLength(0.5f);
                    break;
                }
                break;
            }
            case Searching: {
                if (this.target == null || this.target.isDead()) {
                    this.state = State.Leaving;
                    break;
                }
                this.searchTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * trueMultiplier;
                if (this.hoverTime + this.searchTime > Helicopter.MAX_BOTHER_SECONDS) {
                    this.changeState(State.Leaving);
                    break;
                }
                if (this.isTargetVisible()) {
                    this.timeSinceChopperSawPlayer = 0.0f;
                    this.changeState(State.Hovering);
                    break;
                }
                if (IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY) < 1.0f) {
                    this.targetX = this.target.x + (Rand.Next(Helicopter.RADIUS_SEARCH * 2) - Helicopter.RADIUS_SEARCH);
                    this.targetY = this.target.y + (Rand.Next(Helicopter.RADIUS_SEARCH * 2) - Helicopter.RADIUS_SEARCH);
                    this.move.x = this.targetX - this.x;
                    this.move.y = this.targetY - this.y;
                    this.move.normalize();
                    this.move.setLength(0.5f);
                    break;
                }
                break;
            }
            case Leaving: {
                boolean b = false;
                if (GameServer.bServer) {
                    final ArrayList<IsoPlayer> players = GameServer.getPlayers();
                    for (int i = 0; i < players.size(); ++i) {
                        final IsoPlayer isoPlayer = players.get(i);
                        if (IsoUtils.DistanceToSquared(this.x, this.y, isoPlayer.getX(), isoPlayer.getY()) < 1000000.0f) {
                            b = true;
                            break;
                        }
                    }
                }
                else {
                    for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                        final IsoPlayer isoPlayer2 = IsoPlayer.players[j];
                        if (isoPlayer2 != null) {
                            if (IsoUtils.DistanceToSquared(this.x, this.y, isoPlayer2.getX(), isoPlayer2.getY()) < 1000000.0f) {
                                b = true;
                                break;
                            }
                        }
                    }
                }
                if (!b) {
                    this.deactivate();
                    return;
                }
                break;
            }
        }
        if (Rand.Next(Rand.AdjustForFramerate(300)) == 0) {
            WorldSoundManager.instance.addSound(null, (int)this.x, (int)this.y, 0, 500, 500);
        }
        final float n = this.move.x * (GameTime.getInstance().getMultiplier() / 1.6f);
        final float n2 = this.move.y * (GameTime.getInstance().getMultiplier() / 1.6f);
        if (this.state != State.Leaving && IsoUtils.DistanceToSquared(this.x + n, this.y + n2, this.targetX, this.targetY) > IsoUtils.DistanceToSquared(this.x, this.y, this.targetX, this.targetY)) {
            this.x = this.targetX;
            this.y = this.targetY;
        }
        else {
            this.x += n;
            this.y += n2;
        }
        if (GameServer.bServer) {
            GameServer.sendHelicopter(this.x, this.y, this.bActive);
        }
        this.updateSound();
    }
    
    protected void updateSound() {
        if (GameServer.bServer) {
            return;
        }
        if (Core.SoundDisabled) {
            return;
        }
        if (FMODManager.instance.getNumListeners() == 0) {
            return;
        }
        final GameSound sound = GameSounds.getSound("Helicopter");
        if (sound == null || sound.clips.isEmpty()) {
            return;
        }
        if (Helicopter.inst == 0L) {
            Helicopter.event = sound.getRandomClip().eventDescription;
            if (Helicopter.event != null) {
                javafmod.FMOD_Studio_LoadEventSampleData(Helicopter.event.address);
                Helicopter.inst = javafmod.FMOD_Studio_System_CreateEventInstance(Helicopter.event.address);
            }
        }
        if (Helicopter.inst != 0L) {
            final float volume = SoundManager.instance.getSoundVolume() * sound.getUserVolume();
            if (volume != this.volume) {
                javafmod.FMOD_Studio_EventInstance_SetVolume(Helicopter.inst, volume);
                this.volume = volume;
            }
            javafmod.FMOD_Studio_EventInstance3D(Helicopter.inst, this.x, this.y, 200.0f);
            float occlusion = 0.0f;
            if (IsoPlayer.numPlayers == 1) {
                final IsoGridSquare currentSquare = IsoPlayer.getInstance().getCurrentSquare();
                if (currentSquare != null && !currentSquare.Is(IsoFlagType.exterior)) {
                    occlusion = 1.0f;
                }
            }
            if (this.occlusion != occlusion) {
                this.occlusion = occlusion;
                javafmod.FMOD_Studio_EventInstance_SetParameterByName(Helicopter.inst, "Occlusion", this.occlusion);
            }
            if (!this.bSoundStarted) {
                javafmod.FMOD_Studio_StartEvent(Helicopter.inst);
                this.bSoundStarted = true;
            }
        }
    }
    
    protected boolean isTargetVisible() {
        if (this.target == null || this.target.isDead()) {
            return false;
        }
        final IsoGridSquare currentSquare = this.target.getCurrentSquare();
        if (currentSquare == null) {
            return false;
        }
        if (!currentSquare.getProperties().Is(IsoFlagType.exterior)) {
            return false;
        }
        final IsoMetaGrid.Zone zone = currentSquare.getZone();
        return zone == null || (!"Forest".equals(zone.getType()) && !"DeepForest".equals(zone.getType()));
    }
    
    public void deactivate() {
        if (this.bActive) {
            this.bActive = false;
            if (this.bSoundStarted) {
                javafmod.FMOD_Studio_EventInstance_Stop(Helicopter.inst, false);
                this.bSoundStarted = false;
            }
            if (GameServer.bServer) {
                GameServer.sendHelicopter(this.x, this.y, this.bActive);
            }
            DebugLog.log("chopper: deactivated");
        }
    }
    
    public boolean isActive() {
        return this.bActive;
    }
    
    public void clientSync(final float x, final float y, final boolean bActive) {
        if (!GameClient.bClient) {
            return;
        }
        this.x = x;
        this.y = y;
        if (!bActive) {
            this.deactivate();
        }
        this.bActive = bActive;
    }
    
    static {
        Helicopter.MAX_BOTHER_SECONDS = 60.0f;
        Helicopter.MAX_UNSEEN_SECONDS = 15.0f;
        Helicopter.RADIUS_HOVER = 50;
        Helicopter.RADIUS_SEARCH = 100;
    }
    
    private enum State
    {
        Arriving, 
        Hovering, 
        Searching, 
        Leaving;
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.Arriving, State.Hovering, State.Searching, State.Leaving };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
