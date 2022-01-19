// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.util.StringUtils;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerActionsState extends State
{
    private static final PlayerActionsState _instance;
    
    public static PlayerActionsState instance() {
        return PlayerActionsState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final InventoryItem primaryHandItem = isoGameCharacter.getPrimaryHandItem();
        final InventoryItem secondaryHandItem = isoGameCharacter.getSecondaryHandItem();
        if (!(primaryHandItem instanceof HandWeapon) && !(secondaryHandItem instanceof HandWeapon)) {
            isoGameCharacter.setHideWeaponModel(true);
        }
        if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.isLocal() && !isoGameCharacter.getCharacterActions().isEmpty() && isoGameCharacter.getNetworkCharacterAI().getAction() == null) {
            isoGameCharacter.getNetworkCharacterAI().setAction(isoGameCharacter.getCharacterActions().get(0));
            GameClient.sendAction(isoGameCharacter.getNetworkCharacterAI().getAction(), true);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.isLocal() && !isoGameCharacter.getCharacterActions().isEmpty() && isoGameCharacter.getNetworkCharacterAI().getAction() != isoGameCharacter.getCharacterActions().get(0)) {
            GameClient.sendAction(isoGameCharacter.getNetworkCharacterAI().getAction(), false);
            isoGameCharacter.getNetworkCharacterAI().setAction(isoGameCharacter.getCharacterActions().get(0));
            GameClient.sendAction(isoGameCharacter.getNetworkCharacterAI().getAction(), true);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setHideWeaponModel(false);
        if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.isLocal() && isoGameCharacter.getNetworkCharacterAI().getAction() != null) {
            GameClient.sendAction(isoGameCharacter.getNetworkCharacterAI().getAction(), false);
            isoGameCharacter.getNetworkCharacterAI().setAction(null);
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (GameClient.bClient && animEvent != null && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.getNetworkCharacterAI().getAction() != null && !isoGameCharacter.isLocal() && "changeWeaponSprite".equalsIgnoreCase(animEvent.m_EventName) && !StringUtils.isNullOrEmpty(animEvent.m_ParameterValue)) {
            if ("original".equals(animEvent.m_ParameterValue)) {
                isoGameCharacter.getNetworkCharacterAI().setOverride(false, null, null);
            }
            else {
                isoGameCharacter.getNetworkCharacterAI().setOverride(true, animEvent.m_ParameterValue, null);
            }
        }
    }
    
    static {
        _instance = new PlayerActionsState();
    }
}
