// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoDirections;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.Lua.LuaEventManager;
import zombie.iso.IsoCell;
import zombie.iso.IsoPushableObject;

public final class IsoSurvivor extends IsoLivingCharacter
{
    public boolean NoGoreDeath;
    public boolean Draggable;
    public IsoGameCharacter following;
    boolean Dragging;
    int repathDelay;
    public int nightsSurvived;
    public int ping;
    public IsoPushableObject collidePushable;
    private boolean tryToTeamUp;
    int NeightbourUpdate;
    int NeightbourUpdateMax;
    
    @Override
    public void Despawn() {
        if (this.descriptor != null) {
            this.descriptor.Instance = null;
        }
    }
    
    @Override
    public String getObjectName() {
        return "Survivor";
    }
    
    public IsoSurvivor(final IsoCell isoCell) {
        super(isoCell, 0.0f, 0.0f, 0.0f);
        this.NoGoreDeath = false;
        this.Draggable = false;
        this.following = null;
        this.repathDelay = 0;
        this.nightsSurvived = 0;
        this.ping = 0;
        this.tryToTeamUp = true;
        this.NeightbourUpdate = 20;
        this.NeightbourUpdateMax = 20;
        this.OutlineOnMouseover = true;
        this.getCell().getSurvivorList().add(this);
        LuaEventManager.triggerEvent("OnCreateSurvivor", this);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
    }
    
    public IsoSurvivor(final IsoCell isoCell, final int n, final int n2, final int n3) {
        super(isoCell, (float)n, (float)n2, (float)n3);
        this.NoGoreDeath = false;
        this.Draggable = false;
        this.following = null;
        this.repathDelay = 0;
        this.nightsSurvived = 0;
        this.ping = 0;
        this.tryToTeamUp = true;
        this.NeightbourUpdate = 20;
        this.NeightbourUpdateMax = 20;
        this.getCell().getSurvivorList().add(this);
        this.OutlineOnMouseover = true;
        this.descriptor = new SurvivorDesc();
        this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
        this.sprite.LoadFramesPcx("Wife", "death", 1);
        this.sprite.LoadFramesPcx("Wife", "dragged", 1);
        this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
        this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
        this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
        this.name = "Kate";
        this.solid = false;
        this.IgnoreStaggerBack = true;
        this.SpeakColour = new Color(204, 100, 100);
        this.dir = IsoDirections.S;
        this.OutlineOnMouseover = true;
        this.finder.maxSearchDistance = 120;
        LuaEventManager.triggerEvent("OnCreateSurvivor", this);
        LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
    }
    
    public IsoSurvivor(final SurvivorDesc descriptor, final IsoCell isoCell, final int n, final int n2, final int n3) {
        super(isoCell, (float)n, (float)n2, (float)n3);
        this.NoGoreDeath = false;
        this.Draggable = false;
        this.following = null;
        this.repathDelay = 0;
        this.nightsSurvived = 0;
        this.ping = 0;
        this.tryToTeamUp = true;
        this.NeightbourUpdate = 20;
        this.NeightbourUpdateMax = 20;
        this.setFemale(descriptor.isFemale());
        (this.descriptor = descriptor).setInstance(this);
        this.OutlineOnMouseover = true;
        // invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, "Zombie_palette")
        this.InitSpriteParts(descriptor);
        this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
        this.finder.maxSearchDistance = 120;
        this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
        this.Dressup(descriptor);
        LuaEventManager.triggerEventGarbage("OnCreateSurvivor", this);
        LuaEventManager.triggerEventGarbage("OnCreateLivingCharacter", this, this.descriptor);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
    }
    
    public void reloadSpritePart() {
    }
    
    public IsoSurvivor(final SurvivorDesc descriptor, final IsoCell isoCell, final int n, final int n2, final int n3, final boolean b) {
        super(isoCell, (float)n, (float)n2, (float)n3);
        this.NoGoreDeath = false;
        this.Draggable = false;
        this.following = null;
        this.repathDelay = 0;
        this.nightsSurvived = 0;
        this.ping = 0;
        this.tryToTeamUp = true;
        this.NeightbourUpdate = 20;
        this.NeightbourUpdateMax = 20;
        this.setFemale(descriptor.isFemale());
        this.descriptor = descriptor;
        if (b) {
            descriptor.setInstance(this);
        }
        this.OutlineOnMouseover = true;
        this.InitSpriteParts(descriptor);
        this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
        this.finder.maxSearchDistance = 120;
        this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
        this.Dressup(descriptor);
        LuaEventManager.triggerEvent("OnCreateSurvivor", this);
    }
}
