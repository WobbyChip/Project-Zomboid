// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.CharacterTimedActions;

import java.util.Collection;
import java.util.Arrays;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.packets.EventPacket;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.characters.CharacterActionAnims;
import zombie.inventory.types.HandWeapon;
import zombie.ai.State;
import zombie.ai.states.PlayerActionsState;
import zombie.ui.UIManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.GameTime;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;

public class BaseAction
{
    public long SoundEffect;
    public float CurrentTime;
    public float LastTime;
    public int MaxTime;
    public float PrevLastTime;
    public boolean UseProgressBar;
    public boolean ForceProgressBar;
    public IsoGameCharacter chr;
    public boolean StopOnWalk;
    public boolean StopOnRun;
    public boolean StopOnAim;
    public float caloriesModifier;
    public float delta;
    public boolean blockMovementEtc;
    public boolean overrideAnimation;
    public final ArrayList<String> animVariables;
    public boolean loopAction;
    public boolean bStarted;
    public boolean forceStop;
    public boolean forceComplete;
    private static final ArrayList<String> specificNetworkAnim;
    private InventoryItem primaryHandItem;
    private InventoryItem secondaryHandItem;
    private String primaryHandMdl;
    private String secondaryHandMdl;
    public boolean overrideHandModels;
    
    public BaseAction(final IsoGameCharacter chr) {
        this.SoundEffect = -1L;
        this.CurrentTime = -2.0f;
        this.LastTime = -1.0f;
        this.MaxTime = 60;
        this.PrevLastTime = 0.0f;
        this.UseProgressBar = true;
        this.ForceProgressBar = false;
        this.StopOnWalk = true;
        this.StopOnRun = true;
        this.StopOnAim = false;
        this.caloriesModifier = 1.0f;
        this.delta = 0.0f;
        this.animVariables = new ArrayList<String>();
        this.loopAction = false;
        this.bStarted = false;
        this.forceStop = false;
        this.forceComplete = false;
        this.primaryHandItem = null;
        this.secondaryHandItem = null;
        this.overrideHandModels = false;
        this.chr = chr;
    }
    
    public void forceStop() {
        this.forceStop = true;
    }
    
    public void forceComplete() {
        this.forceComplete = true;
    }
    
    public void PlayLoopedSoundTillComplete(final String s, final int n, final float n2) {
        this.SoundEffect = this.chr.getEmitter().playSound(s);
    }
    
    public boolean hasStalled() {
        return this.bStarted && ((this.LastTime == this.CurrentTime && this.LastTime == this.PrevLastTime && this.LastTime < 0.0f) || this.CurrentTime < 0.0f);
    }
    
    public float getJobDelta() {
        return this.delta;
    }
    
    public void resetJobDelta() {
        this.delta = 0.0f;
        this.CurrentTime = 0.0f;
    }
    
    public void waitToStart() {
        if (this.chr.shouldWaitToStartTimedAction()) {
            return;
        }
        this.bStarted = true;
        this.start();
    }
    
    public void update() {
        this.PrevLastTime = this.LastTime;
        this.LastTime = this.CurrentTime;
        this.CurrentTime += GameTime.instance.getMultiplier();
        if (this.CurrentTime < 0.0f) {
            this.CurrentTime = 0.0f;
        }
        if (this.MaxTime == 0) {
            this.delta = 0.0f;
        }
        else if (this.MaxTime != -1) {
            this.delta = Math.min(this.CurrentTime / this.MaxTime, 1.0f);
        }
        if ((Core.getInstance().isOptionProgressBar() || this.ForceProgressBar) && this.UseProgressBar && this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && this.MaxTime != -1) {
            UIManager.getProgressBar(((IsoPlayer)this.chr).getPlayerNum()).setValue(this.delta);
        }
    }
    
    public void start() {
        this.forceComplete = false;
        this.forceStop = false;
        if (this.chr.isCurrentState(PlayerActionsState.instance())) {
            final InventoryItem primaryHandItem = this.chr.getPrimaryHandItem();
            final InventoryItem secondaryHandItem = this.chr.getSecondaryHandItem();
            this.chr.setHideWeaponModel(!(primaryHandItem instanceof HandWeapon) && !(secondaryHandItem instanceof HandWeapon));
        }
    }
    
    public void reset() {
        this.CurrentTime = 0.0f;
        this.forceComplete = false;
        this.forceStop = false;
    }
    
    public float getCurrentTime() {
        return this.CurrentTime;
    }
    
    public void stop() {
        if (this.SoundEffect > -1L) {
            this.chr.getEmitter().stopSound(this.SoundEffect);
            this.SoundEffect = -1L;
        }
        this.stopTimedActionAnim();
    }
    
    public boolean valid() {
        return true;
    }
    
    public boolean finished() {
        return this.CurrentTime >= this.MaxTime && this.MaxTime != -1;
    }
    
    public void perform() {
        if (!this.loopAction) {
            this.stopTimedActionAnim();
        }
    }
    
    public void setUseProgressBar(final boolean useProgressBar) {
        this.UseProgressBar = useProgressBar;
    }
    
    public void setBlockMovementEtc(final boolean blockMovementEtc) {
        this.blockMovementEtc = blockMovementEtc;
    }
    
    public void setOverrideAnimation(final boolean overrideAnimation) {
        this.overrideAnimation = overrideAnimation;
    }
    
    public void stopTimedActionAnim() {
        for (int i = 0; i < this.animVariables.size(); ++i) {
            this.chr.clearVariable(this.animVariables.get(i));
        }
        this.chr.setVariable("IsPerformingAnAction", false);
        if (this.overrideHandModels) {
            this.overrideHandModels = false;
            this.chr.resetEquippedHandsModels();
        }
    }
    
    public void setAnimVariable(final String s, final String s2) {
        if (!this.animVariables.contains(s)) {
            this.animVariables.add(s);
        }
        this.chr.setVariable(s, s2);
    }
    
    public void setAnimVariable(final String s, final boolean b) {
        if (!this.animVariables.contains(s)) {
            this.animVariables.add(s);
        }
        this.chr.setVariable(s, String.valueOf(b));
    }
    
    public String getPrimaryHandMdl() {
        return this.primaryHandMdl;
    }
    
    public String getSecondaryHandMdl() {
        return this.secondaryHandMdl;
    }
    
    public InventoryItem getPrimaryHandItem() {
        return this.primaryHandItem;
    }
    
    public InventoryItem getSecondaryHandItem() {
        return this.secondaryHandItem;
    }
    
    public void setActionAnim(final CharacterActionAnims characterActionAnims) {
        this.setActionAnim(characterActionAnims.toString());
    }
    
    public void setActionAnim(final String s) {
        this.setAnimVariable("PerformingAction", s);
        this.chr.setVariable("IsPerformingAnAction", true);
        if (Core.bDebug) {
            this.chr.advancedAnimator.printDebugCharacterActions(s);
        }
    }
    
    public void setOverrideHandModels(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
        this.setOverrideHandModels(inventoryItem, inventoryItem2, true);
    }
    
    public void setOverrideHandModels(final InventoryItem inventoryItem, final InventoryItem inventoryItem2, final boolean b) {
        this.setOverrideHandModelsObject(inventoryItem, inventoryItem2, b);
    }
    
    public void setOverrideHandModelsString(final String s, final String s2) {
        this.setOverrideHandModelsString(s, s2, true);
    }
    
    public void setOverrideHandModelsString(final String s, final String s2, final boolean b) {
        this.setOverrideHandModelsObject(s, s2, b);
    }
    
    public void setOverrideHandModelsObject(final Object o, final Object o2, final boolean b) {
        this.overrideHandModels = true;
        this.primaryHandItem = Type.tryCastTo(o, InventoryItem.class);
        this.secondaryHandItem = Type.tryCastTo(o2, InventoryItem.class);
        this.primaryHandMdl = StringUtils.discardNullOrWhitespace(Type.tryCastTo(o, String.class));
        this.secondaryHandMdl = StringUtils.discardNullOrWhitespace(Type.tryCastTo(o2, String.class));
        if (b) {
            this.chr.resetEquippedHandsModels();
        }
        if (this.primaryHandItem != null || this.secondaryHandItem != null) {
            this.chr.reportEvent(EventPacket.EventType.EventOverrideItem.name());
        }
    }
    
    public void OnAnimEvent(final AnimEvent animEvent) {
    }
    
    public void setLoopedAction(final boolean loopAction) {
        this.loopAction = loopAction;
    }
    
    static {
        specificNetworkAnim = new ArrayList<String>(Arrays.asList("Reload", "Bandage", "Loot", "AttachItem", "Drink", "Eat", "Pour", "Read", "fill_container_tap", "drink_tap", "WearClothing"));
    }
}
