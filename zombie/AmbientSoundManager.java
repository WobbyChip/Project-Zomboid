// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.Lua.LuaEventManager;
import zombie.iso.RoomDef;
import zombie.core.Rand;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.Vector2;
import java.util.ArrayList;

public final class AmbientSoundManager extends BaseAmbientStreamManager
{
    public final ArrayList<Ambient> ambient;
    private final Vector2 tempo;
    public boolean initialized;
    
    public AmbientSoundManager() {
        this.ambient = new ArrayList<Ambient>();
        this.tempo = new Vector2();
        this.initialized = false;
    }
    
    @Override
    public void update() {
        if (!this.initialized) {
            return;
        }
        this.doOneShotAmbients();
    }
    
    @Override
    public void addAmbient(final String s, final int n, final int n2, final int n3, final float n4) {
    }
    
    @Override
    public void addAmbientEmitter(final float n, final float n2, final int n3, final String s) {
    }
    
    @Override
    public void addDaytimeAmbientEmitter(final float n, final float n2, final int n3, final String s) {
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
    
    @Override
    public void init() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
    }
    
    @Override
    public void addBlend(final String s, final float n, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
    }
    
    @Override
    protected void addRandomAmbient() {
        if (GameServer.Players.isEmpty()) {
            return;
        }
        final IsoPlayer isoPlayer = GameServer.Players.get(Rand.Next(GameServer.Players.size()));
        if (isoPlayer == null) {
            return;
        }
        String s = null;
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
        if (s == null) {
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
        this.ambient.add(new Ambient(s, n2, n3, 50.0f, Rand.Next(0.2f, 0.5f)));
        GameServer.sendAmbient(s, (int)n2, (int)n3, 50, Rand.Next(0.2f, 0.5f));
    }
    
    @Override
    public void doGunEvent() {
        final ArrayList<IsoPlayer> players = GameServer.getPlayers();
        if (players.isEmpty()) {
            return;
        }
        final IsoPlayer isoPlayer = players.get(Rand.Next(players.size()));
        String s = null;
        final float x = isoPlayer.x;
        final float y = isoPlayer.y;
        final int n = 600;
        final double n2 = Rand.Next(-3.1415927f, 3.1415927f);
        this.tempo.x = (float)Math.cos(n2);
        this.tempo.y = (float)Math.sin(n2);
        this.tempo.setLength((float)(n - 100));
        final float n3 = x + this.tempo.x;
        final float n4 = y + this.tempo.y;
        WorldSoundManager.instance.addSound(null, (int)n3 + Rand.Next(-10, 10), (int)n4 + Rand.Next(-10, 10), 0, 600, 600);
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
        final Ambient e = new Ambient(s, n3, n4, 700.0f, 1.0f);
        this.ambient.add(e);
        GameServer.sendAmbient(s, (int)n3, (int)n4, (int)Math.ceil(e.radius), e.volume);
    }
    
    @Override
    public void doAlarm(final RoomDef roomDef) {
        if (roomDef != null && roomDef.building != null && roomDef.building.bAlarmed) {
            final Ambient e = new Ambient("burglar2", (float)(roomDef.x + roomDef.getW() / 2), (float)(roomDef.y + roomDef.getH() / 2), 700.0f, 1.0f);
            e.duration = 49;
            e.worldSoundDelay = 3;
            roomDef.building.bAlarmed = false;
            roomDef.building.setAllExplored(true);
            this.ambient.add(e);
            GameServer.sendAlarm(roomDef.x + roomDef.getW() / 2, roomDef.y + roomDef.getH() / 2);
        }
    }
    
    @Override
    public void stop() {
        this.ambient.clear();
        this.initialized = false;
    }
    
    public class Ambient
    {
        public float x;
        public float y;
        public String name;
        public float radius;
        public float volume;
        long startTime;
        public int duration;
        public int worldSoundDelay;
        
        public Ambient(final String name, final float n, final float n2, final float radius, final float volume) {
            this.worldSoundDelay = 0;
            this.name = name;
            this.x = n;
            this.y = n2;
            this.radius = radius;
            this.volume = volume;
            this.startTime = System.currentTimeMillis() / 1000L;
            this.duration = 2;
            this.update();
            LuaEventManager.triggerEvent("OnAmbientSound", name, n, n2);
        }
        
        public boolean finished() {
            return System.currentTimeMillis() / 1000L - this.startTime >= this.duration;
        }
        
        public void update() {
            if (System.currentTimeMillis() / 1000L - this.startTime >= this.worldSoundDelay) {
                WorldSoundManager.instance.addSound(null, (int)this.x, (int)this.y, 0, 600, 600);
            }
        }
    }
}
