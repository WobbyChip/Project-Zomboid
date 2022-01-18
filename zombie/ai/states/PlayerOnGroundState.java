// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.network.GameClient;
import zombie.GameTime;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerOnGroundState extends State
{
    private static final PlayerOnGroundState _instance;
    
    public static PlayerOnGroundState instance() {
        return PlayerOnGroundState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        ((IsoPlayer)isoGameCharacter).setBlockMovement(true);
        isoGameCharacter.setVariable("bAnimEnd", false);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (!GameServer.bServer && isoGameCharacter.isDead() && isoGameCharacter.getVariableBoolean("bAnimEnd")) {
            this.becomeCorpse((IsoPlayer)isoGameCharacter);
        }
        else {
            isoGameCharacter.setReanimateTimer(isoGameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6f);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        ((IsoPlayer)isoGameCharacter).setBlockMovement(false);
    }
    
    private void becomeCorpse(final IsoPlayer isoPlayer) {
        if (!isoPlayer.isOnDeathDone()) {
            if (GameClient.bClient) {
                isoPlayer.networkAI.processDeadBody();
            }
            isoPlayer.becomeCorpse();
        }
    }
    
    static {
        _instance = new PlayerOnGroundState();
    }
}
