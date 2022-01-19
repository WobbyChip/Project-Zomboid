// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.IsoObject;
import zombie.audio.FMODParameter;
import fmod.fmod.FMODSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.core.Core;
import zombie.audio.parameters.ParameterFireSize;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import java.util.List;
import java.util.Collections;
import zombie.util.list.PZArrayUtil;
import java.util.Comparator;
import java.util.Collection;
import zombie.core.network.ByteBufferWriter;
import zombie.WorldSoundManager;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;

public class IsoFireManager
{
    public static double Red_Oscilator;
    public static double Green_Oscilator;
    public static double Blue_Oscilator;
    public static double Red_Oscilator_Rate;
    public static double Green_Oscilator_Rate;
    public static double Blue_Oscilator_Rate;
    public static double Red_Oscilator_Val;
    public static double Green_Oscilator_Val;
    public static double Blue_Oscilator_Val;
    public static double OscilatorSpeedScalar;
    public static double OscilatorEffectScalar;
    public static int MaxFireObjects;
    public static int FireRecalcDelay;
    public static int FireRecalc;
    public static boolean LightCalcFromBurningCharacters;
    public static float FireAlpha;
    public static float SmokeAlpha;
    public static float FireAnimDelay;
    public static float SmokeAnimDelay;
    public static ColorInfo FireTintMod;
    public static ColorInfo SmokeTintMod;
    public static final ArrayList<IsoFire> FireStack;
    public static final ArrayList<IsoGameCharacter> CharactersOnFire_Stack;
    private static final FireSounds fireSounds;
    private static Stack<IsoFire> updateStack;
    
    public static void Add(final IsoFire e) {
        if (IsoFireManager.FireStack.contains(e)) {
            System.out.println("IsoFireManager.Add already added fire, ignoring");
            return;
        }
        if (IsoFireManager.FireStack.size() < IsoFireManager.MaxFireObjects) {
            IsoFireManager.FireStack.add(e);
        }
        else {
            IsoFire isoFire = null;
            int age = 0;
            for (int i = 0; i < IsoFireManager.FireStack.size(); ++i) {
                if (IsoFireManager.FireStack.get(i).Age > age) {
                    age = IsoFireManager.FireStack.get(i).Age;
                    isoFire = IsoFireManager.FireStack.get(i);
                }
            }
            if (isoFire != null && isoFire.square != null) {
                isoFire.square.getProperties().UnSet(IsoFlagType.burning);
                isoFire.square.getProperties().UnSet(IsoFlagType.smoke);
                isoFire.RemoveAttachedAnims();
                isoFire.removeFromWorld();
                isoFire.removeFromSquare();
            }
            IsoFireManager.FireStack.add(e);
        }
    }
    
    public static void AddBurningCharacter(final IsoGameCharacter e) {
        for (int i = 0; i < IsoFireManager.CharactersOnFire_Stack.size(); ++i) {
            if (IsoFireManager.CharactersOnFire_Stack.get(i) == e) {
                return;
            }
        }
        IsoFireManager.CharactersOnFire_Stack.add(e);
    }
    
    public static void Fire_LightCalc(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2, final int n) {
        if (isoGridSquare2 == null || isoGridSquare == null) {
            return;
        }
        final int n2 = 0;
        final int n3 = 8;
        final int n4 = n2 + Math.abs(isoGridSquare2.getX() - isoGridSquare.getX()) + Math.abs(isoGridSquare2.getY() - isoGridSquare.getY()) + Math.abs(isoGridSquare2.getZ() - isoGridSquare.getZ());
        if (n4 <= n3) {
            final float f;
            final float n5 = f = 0.199f / n3 * (n3 - n4);
            final float f2 = n5 * 0.6f;
            final float f3 = n5 * 0.4f;
            if (isoGridSquare2.getLightInfluenceR() == null) {
                isoGridSquare2.setLightInfluenceR(new ArrayList<Float>());
            }
            isoGridSquare2.getLightInfluenceR().add(f);
            if (isoGridSquare2.getLightInfluenceG() == null) {
                isoGridSquare2.setLightInfluenceG(new ArrayList<Float>());
            }
            isoGridSquare2.getLightInfluenceG().add(f2);
            if (isoGridSquare2.getLightInfluenceB() == null) {
                isoGridSquare2.setLightInfluenceB(new ArrayList<Float>());
            }
            isoGridSquare2.getLightInfluenceB().add(f3);
            final ColorInfo lightInfo;
            final ColorInfo colorInfo = lightInfo = isoGridSquare2.lighting[n].lightInfo();
            lightInfo.r += f;
            final ColorInfo colorInfo2 = colorInfo;
            colorInfo2.g += f2;
            final ColorInfo colorInfo3 = colorInfo;
            colorInfo3.b += f3;
            if (colorInfo.r > 1.0f) {
                colorInfo.r = 1.0f;
            }
            if (colorInfo.g > 1.0f) {
                colorInfo.g = 1.0f;
            }
            if (colorInfo.b > 1.0f) {
                colorInfo.b = 1.0f;
            }
        }
    }
    
    public static void LightTileWithFire(final IsoGridSquare isoGridSquare) {
    }
    
    public static void explode(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final int n) {
        if (isoGridSquare == null) {
            return;
        }
        IsoFireManager.FireRecalc = 1;
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = 0; k <= 1; ++k) {
                    final IsoGridSquare gridSquare = isoCell.getGridSquare(isoGridSquare.getX() + i, isoGridSquare.getY() + j, isoGridSquare.getZ() + k);
                    if (gridSquare != null && Rand.Next(100) < n && IsoFire.CanAddFire(gridSquare, true)) {
                        StartFire(isoCell, gridSquare, true, Rand.Next(100, 250 + n));
                        gridSquare.BurnWalls(true);
                    }
                }
            }
        }
    }
    
    public static void MolotovSmash(final IsoCell isoCell, final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return;
        }
        IsoFireManager.FireRecalc = 1;
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX(), isoGridSquare.getY() - 1, isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX() + 1, isoGridSquare.getY() - 1, isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX() + 1, isoGridSquare.getY(), isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX() + 1, isoGridSquare.getY() + 1, isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX(), isoGridSquare.getY() + 1, isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX() - 1, isoGridSquare.getY() + 1, isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX() - 1, isoGridSquare.getY(), isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX() - 1, isoGridSquare.getY() - 1, isoGridSquare.getZ()), true, 50);
        StartFire(isoCell, isoCell.getGridSquare(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ()), true, 50);
    }
    
    public static void Remove(final IsoFire isoFire) {
        if (!IsoFireManager.FireStack.contains(isoFire)) {
            System.out.println("IsoFireManager.Remove unknown fire, ignoring");
            return;
        }
        IsoFireManager.FireStack.remove(isoFire);
    }
    
    public static void RemoveBurningCharacter(final IsoGameCharacter o) {
        IsoFireManager.CharactersOnFire_Stack.remove(o);
    }
    
    public static void StartFire(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final boolean b, int n, final int n2) {
        if (isoGridSquare.getFloor() != null && isoGridSquare.getFloor().getSprite() != null) {
            n -= isoGridSquare.getFloor().getSprite().firerequirement;
        }
        if (n < 5) {
            n = 5;
        }
        if (!IsoFire.CanAddFire(isoGridSquare, b)) {
            return;
        }
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.StartFire.doPacket(startPacket);
            startPacket.putInt(isoGridSquare.getX());
            startPacket.putInt(isoGridSquare.getY());
            startPacket.putInt(isoGridSquare.getZ());
            startPacket.putInt(n);
            startPacket.putBoolean(b);
            startPacket.putInt(n2);
            startPacket.putBoolean(false);
            PacketTypes.PacketType.StartFire.send(GameClient.connection);
            return;
        }
        if (GameServer.bServer) {
            GameServer.startFireOnClient(isoGridSquare, n, b, n2, false);
            return;
        }
        final IsoFire isoFire = new IsoFire(isoCell, isoGridSquare, b, n, n2);
        Add(isoFire);
        isoGridSquare.getObjects().add(isoFire);
        if (Rand.Next(5) == 0) {
            WorldSoundManager.instance.addSound(isoFire, isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ(), 20, 20);
        }
    }
    
    public static void StartSmoke(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final boolean b, final int n, final int n2) {
        if (!IsoFire.CanAddSmoke(isoGridSquare, b)) {
            return;
        }
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.StartFire.doPacket(startPacket);
            startPacket.putInt(isoGridSquare.getX());
            startPacket.putInt(isoGridSquare.getY());
            startPacket.putInt(isoGridSquare.getZ());
            startPacket.putInt(n);
            startPacket.putBoolean(b);
            startPacket.putInt(n2);
            startPacket.putBoolean(true);
            PacketTypes.PacketType.StartFire.send(GameClient.connection);
            return;
        }
        if (GameServer.bServer) {
            GameServer.startFireOnClient(isoGridSquare, n, b, n2, true);
            return;
        }
        final IsoFire isoFire = new IsoFire(isoCell, isoGridSquare, b, n, n2, true);
        Add(isoFire);
        isoGridSquare.getObjects().add(isoFire);
    }
    
    public static void StartFire(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final boolean b, final int n) {
        StartFire(isoCell, isoGridSquare, b, n, 0);
    }
    
    public static void Update() {
        IsoFireManager.Red_Oscilator_Val = Math.sin(IsoFireManager.Red_Oscilator += IsoFireManager.Blue_Oscilator_Rate * IsoFireManager.OscilatorSpeedScalar);
        IsoFireManager.Green_Oscilator_Val = Math.sin(IsoFireManager.Green_Oscilator += IsoFireManager.Blue_Oscilator_Rate * IsoFireManager.OscilatorSpeedScalar);
        IsoFireManager.Blue_Oscilator_Val = Math.sin(IsoFireManager.Blue_Oscilator += IsoFireManager.Blue_Oscilator_Rate * IsoFireManager.OscilatorSpeedScalar);
        IsoFireManager.Red_Oscilator_Val = (IsoFireManager.Red_Oscilator_Val + 1.0) / 2.0;
        IsoFireManager.Green_Oscilator_Val = (IsoFireManager.Green_Oscilator_Val + 1.0) / 2.0;
        IsoFireManager.Blue_Oscilator_Val = (IsoFireManager.Blue_Oscilator_Val + 1.0) / 2.0;
        IsoFireManager.Red_Oscilator_Val *= IsoFireManager.OscilatorEffectScalar;
        IsoFireManager.Green_Oscilator_Val *= IsoFireManager.OscilatorEffectScalar;
        IsoFireManager.Blue_Oscilator_Val *= IsoFireManager.OscilatorEffectScalar;
        IsoFireManager.updateStack.clear();
        IsoFireManager.updateStack.addAll((Collection<?>)IsoFireManager.FireStack);
        for (int i = 0; i < IsoFireManager.updateStack.size(); ++i) {
            final IsoFire o = IsoFireManager.updateStack.get(i);
            if (o.getObjectIndex() != -1) {
                if (IsoFireManager.FireStack.contains(o)) {
                    o.update();
                }
            }
        }
        --IsoFireManager.FireRecalc;
        if (IsoFireManager.FireRecalc < 0) {
            IsoFireManager.FireRecalc = IsoFireManager.FireRecalcDelay;
        }
        IsoFireManager.fireSounds.update();
    }
    
    public static void updateSound(final IsoFire isoFire) {
        IsoFireManager.fireSounds.addFire(isoFire);
    }
    
    public static void stopSound(final IsoFire isoFire) {
        IsoFireManager.fireSounds.removeFire(isoFire);
    }
    
    public static void RemoveAllOn(final IsoGridSquare isoGridSquare) {
        for (int i = IsoFireManager.FireStack.size() - 1; i >= 0; --i) {
            final IsoFire isoFire = IsoFireManager.FireStack.get(i);
            if (isoFire.square == isoGridSquare) {
                isoFire.extinctFire();
            }
        }
    }
    
    public static void Reset() {
        IsoFireManager.FireStack.clear();
        IsoFireManager.CharactersOnFire_Stack.clear();
        IsoFireManager.fireSounds.Reset();
    }
    
    static {
        IsoFireManager.Red_Oscilator = 0.0;
        IsoFireManager.Green_Oscilator = 0.0;
        IsoFireManager.Blue_Oscilator = 0.0;
        IsoFireManager.Red_Oscilator_Rate = 0.10000000149011612;
        IsoFireManager.Green_Oscilator_Rate = 0.12999999523162842;
        IsoFireManager.Blue_Oscilator_Rate = 0.08760000020265579;
        IsoFireManager.Red_Oscilator_Val = 0.0;
        IsoFireManager.Green_Oscilator_Val = 0.0;
        IsoFireManager.Blue_Oscilator_Val = 0.0;
        IsoFireManager.OscilatorSpeedScalar = 15.600000381469727;
        IsoFireManager.OscilatorEffectScalar = 0.0038999998942017555;
        IsoFireManager.MaxFireObjects = 75;
        IsoFireManager.FireRecalcDelay = 25;
        IsoFireManager.FireRecalc = IsoFireManager.FireRecalcDelay;
        IsoFireManager.LightCalcFromBurningCharacters = false;
        IsoFireManager.FireAlpha = 1.0f;
        IsoFireManager.SmokeAlpha = 0.3f;
        IsoFireManager.FireAnimDelay = 0.2f;
        IsoFireManager.SmokeAnimDelay = 0.2f;
        IsoFireManager.FireTintMod = new ColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
        IsoFireManager.SmokeTintMod = new ColorInfo(0.5f, 0.5f, 0.5f, 1.0f);
        FireStack = new ArrayList<IsoFire>();
        CharactersOnFire_Stack = new ArrayList<IsoGameCharacter>();
        fireSounds = new FireSounds(20);
        IsoFireManager.updateStack = new Stack<IsoFire>();
    }
    
    private static final class FireSounds
    {
        final ArrayList<IsoFire> fires;
        final Slot[] slots;
        final Comparator<IsoFire> comp;
        
        FireSounds(final int n) {
            this.fires = new ArrayList<IsoFire>();
            this.comp = new Comparator<IsoFire>() {
                @Override
                public int compare(final IsoFire isoFire, final IsoFire isoFire2) {
                    final float closestListener = FireSounds.this.getClosestListener(isoFire.square.x + 0.5f, isoFire.square.y + 0.5f, (float)isoFire.square.z);
                    final float closestListener2 = FireSounds.this.getClosestListener(isoFire2.square.x + 0.5f, isoFire2.square.y + 0.5f, (float)isoFire2.square.z);
                    if (closestListener > closestListener2) {
                        return 1;
                    }
                    if (closestListener < closestListener2) {
                        return -1;
                    }
                    return 0;
                }
            };
            this.slots = PZArrayUtil.newInstance(Slot.class, n, Slot::new);
        }
        
        void addFire(final IsoFire isoFire) {
            if (!this.fires.contains(isoFire)) {
                this.fires.add(isoFire);
            }
        }
        
        void removeFire(final IsoFire o) {
            this.fires.remove(o);
        }
        
        void update() {
            if (GameServer.bServer) {
                return;
            }
            for (int i = 0; i < this.slots.length; ++i) {
                this.slots[i].playing = false;
            }
            if (this.fires.isEmpty()) {
                this.stopNotPlaying();
                return;
            }
            Collections.sort(this.fires, this.comp);
            final int min = Math.min(this.fires.size(), this.slots.length);
            for (int j = 0; j < min; ++j) {
                final IsoFire isoFire = this.fires.get(j);
                if (this.shouldPlay(isoFire)) {
                    final int existingSlot = this.getExistingSlot(isoFire);
                    if (existingSlot != -1) {
                        this.slots[existingSlot].playSound(isoFire);
                    }
                }
            }
            for (int k = 0; k < min; ++k) {
                final IsoFire isoFire2 = this.fires.get(k);
                if (this.shouldPlay(isoFire2)) {
                    if (this.getExistingSlot(isoFire2) == -1) {
                        this.slots[this.getFreeSlot()].playSound(isoFire2);
                    }
                }
            }
            this.stopNotPlaying();
            this.fires.clear();
        }
        
        float getClosestListener(final float n, final float n2, final float n3) {
            float n4 = Float.MAX_VALUE;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && isoPlayer.getCurrentSquare() != null) {
                    float distanceToSquared = IsoUtils.DistanceToSquared(isoPlayer.getX(), isoPlayer.getY(), isoPlayer.getZ() * 3.0f, n, n2, n3 * 3.0f);
                    if (isoPlayer.Traits.HardOfHearing.isSet()) {
                        distanceToSquared *= 4.5f;
                    }
                    if (distanceToSquared < n4) {
                        n4 = distanceToSquared;
                    }
                }
            }
            return n4;
        }
        
        boolean shouldPlay(final IsoFire isoFire) {
            return isoFire != null && isoFire.getObjectIndex() != -1 && isoFire.LifeStage < 4;
        }
        
        int getExistingSlot(final IsoFire isoFire) {
            for (int i = 0; i < this.slots.length; ++i) {
                if (this.slots[i].fire == isoFire) {
                    return i;
                }
            }
            return -1;
        }
        
        int getFreeSlot() {
            for (int i = 0; i < this.slots.length; ++i) {
                if (!this.slots[i].playing) {
                    return i;
                }
            }
            return -1;
        }
        
        void stopNotPlaying() {
            for (int i = 0; i < this.slots.length; ++i) {
                final Slot slot = this.slots[i];
                if (!slot.playing) {
                    slot.stopPlaying();
                    slot.fire = null;
                }
            }
        }
        
        void Reset() {
            for (int i = 0; i < this.slots.length; ++i) {
                this.slots[i].stopPlaying();
                this.slots[i].fire = null;
                this.slots[i].playing = false;
            }
        }
        
        static final class Slot
        {
            IsoFire fire;
            BaseSoundEmitter emitter;
            final ParameterFireSize parameterFireSize;
            long instance;
            boolean playing;
            
            Slot() {
                this.parameterFireSize = new ParameterFireSize();
                this.instance = 0L;
            }
            
            void playSound(final IsoFire fire) {
                if (this.emitter == null) {
                    this.emitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
                    if (!Core.SoundDisabled) {
                        ((FMODSoundEmitter)this.emitter).addParameter((FMODParameter)this.parameterFireSize);
                    }
                }
                this.emitter.setPos(fire.square.x + 0.5f, fire.square.y + 0.5f, (float)fire.square.z);
                int size = 0;
                switch (fire.LifeStage) {
                    case 1:
                    case 3: {
                        size = 1;
                        break;
                    }
                    case 2: {
                        size = 2;
                        break;
                    }
                    default: {
                        size = 0;
                        break;
                    }
                }
                this.parameterFireSize.setSize(size);
                if (!this.emitter.isPlaying("Fire")) {
                    this.instance = this.emitter.playSoundImpl("Fire", (IsoObject)null);
                }
                this.fire = fire;
                this.playing = true;
                this.emitter.tick();
            }
            
            void stopPlaying() {
                if (this.emitter == null || this.instance == 0L) {
                    return;
                }
                if (this.emitter.hasSustainPoints(this.instance)) {
                    this.emitter.triggerCue(this.instance);
                    this.instance = 0L;
                    return;
                }
                this.emitter.stopAll();
                this.instance = 0L;
            }
        }
    }
}
