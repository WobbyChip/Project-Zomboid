// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class FishingState extends State
{
    private static final FishingState _instance;
    float pauseTime;
    private String stage;
    
    public FishingState() {
        this.pauseTime = 0.0f;
        this.stage = null;
    }
    
    public static FishingState instance() {
        return FishingState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        DebugLog.log("FISHINGSTATE - ENTER");
        isoGameCharacter.setVariable("FishingFinished", false);
        this.pauseTime = Rand.Next(60.0f, 120.0f);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            final String variableString = isoGameCharacter.getVariableString("FishingStage");
            if (variableString != null && !variableString.equals(this.stage)) {
                this.stage = variableString;
                if (!variableString.equals("idle")) {
                    GameClient.sendEvent((IsoPlayer)isoGameCharacter, "EventFishing");
                }
            }
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        DebugLog.log("FISHINGSTATE - EXIT");
        isoGameCharacter.clearVariable("FishingStage");
        isoGameCharacter.clearVariable("FishingFinished");
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    static {
        _instance = new FishingState();
    }
}
