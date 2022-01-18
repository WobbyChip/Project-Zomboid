// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerAimState extends State
{
    private static final PlayerAimState _instance;
    
    public static PlayerAimState instance() {
        return PlayerAimState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        if (!"strafe".equals(isoGameCharacter.getPreviousActionContextStateName())) {
            final InventoryItem primaryHandItem = isoGameCharacter.getPrimaryHandItem();
            if (primaryHandItem != null && primaryHandItem.getBringToBearSound() != null) {
                isoGameCharacter.getEmitter().playSoundImpl(primaryHandItem.getBringToBearSound(), null);
            }
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    static {
        _instance = new PlayerAimState();
    }
}
