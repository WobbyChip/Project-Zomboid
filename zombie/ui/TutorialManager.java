// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.network.GameServer;
import zombie.iso.IsoWorld;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoRadio;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoStove;
import zombie.characters.IsoZombie;
import zombie.characters.IsoSurvivor;

public final class TutorialManager
{
    public static boolean Debug;
    public boolean Active;
    public boolean ActiveControlZombies;
    public float TargetZombies;
    public Stage stage;
    public IsoSurvivor wife;
    private IsoZombie zombie;
    public IsoStove tutorialStove;
    public IsoBuilding tutBuilding;
    public boolean DoorsLocked;
    public int BarricadeCount;
    public String PrefMusic;
    public IsoSurvivor gunnut;
    public boolean StealControl;
    public int AlarmTime;
    public boolean ProfanityFilter;
    public int Timer;
    public int AlarmTickTime;
    public boolean DoneFirstSleep;
    public boolean wifeKilledByEarl;
    public boolean warnedHammer;
    public boolean TriggerFire;
    public boolean CanDragWife;
    public boolean AllowSleep;
    public boolean skipped;
    private boolean bDoneDeath;
    boolean bDoGunnutDeadTalk;
    public String millingTune;
    IsoRadio radio;
    public static TutorialManager instance;
    
    public TutorialManager() {
        this.Active = false;
        this.ActiveControlZombies = false;
        this.TargetZombies = 0.0f;
        this.stage = Stage.getBelt;
        this.wife = null;
        this.DoorsLocked = true;
        this.BarricadeCount = 0;
        this.PrefMusic = null;
        this.StealControl = false;
        this.AlarmTime = 0;
        this.ProfanityFilter = false;
        this.Timer = 0;
        this.AlarmTickTime = 160;
        this.DoneFirstSleep = false;
        this.wifeKilledByEarl = false;
        this.warnedHammer = false;
        this.TriggerFire = false;
        this.CanDragWife = false;
        this.AllowSleep = false;
        this.skipped = false;
        this.bDoneDeath = false;
        this.bDoGunnutDeadTalk = true;
        this.millingTune = "tune1.ogg";
        this.radio = null;
    }
    
    public boolean AllowUse(final IsoObject isoObject) {
        return true;
    }
    
    public void CheckWake() {
    }
    
    public void CreateQuests() {
        try {
            for (int i = 0; i < IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList().size(); ++i) {
                final IsoObject isoObject = IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList().get(i);
                if (isoObject instanceof IsoRadio) {
                    this.radio = (IsoRadio)isoObject;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.radio = null;
        }
    }
    
    public void init() {
        if (GameServer.bServer) {
            return;
        }
        if (!this.Active) {
            return;
        }
    }
    
    public void update() {
    }
    
    private void ForceKillZombies() {
        IsoWorld.instance.ForceKillAllZombies();
    }
    
    static {
        TutorialManager.Debug = false;
        TutorialManager.instance = new TutorialManager();
    }
    
    public enum Stage
    {
        getBelt, 
        RipSheet, 
        Apply, 
        FindShed, 
        getShedItems, 
        EquipHammer, 
        BoardUpHouse, 
        FindFood, 
        InHouseFood, 
        KillZombie, 
        StockUp, 
        ExploreHouse, 
        BreakBarricade, 
        getSoupIngredients, 
        MakeSoupPot, 
        LightStove, 
        Distraction, 
        InvestigateSound, 
        Alarm, 
        Mouseover, 
        Escape, 
        ShouldBeOk;
        
        private static /* synthetic */ Stage[] $values() {
            return new Stage[] { Stage.getBelt, Stage.RipSheet, Stage.Apply, Stage.FindShed, Stage.getShedItems, Stage.EquipHammer, Stage.BoardUpHouse, Stage.FindFood, Stage.InHouseFood, Stage.KillZombie, Stage.StockUp, Stage.ExploreHouse, Stage.BreakBarricade, Stage.getSoupIngredients, Stage.MakeSoupPot, Stage.LightStove, Stage.Distraction, Stage.InvestigateSound, Stage.Alarm, Stage.Mouseover, Stage.Escape, Stage.ShouldBeOk };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
