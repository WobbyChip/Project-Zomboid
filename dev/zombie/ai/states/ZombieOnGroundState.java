// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.network.GameClient;
import zombie.GameTime;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.iso.Vector3;
import zombie.ai.State;

public final class ZombieOnGroundState extends State
{
    private static final ZombieOnGroundState _instance;
    static Vector3 tempVector;
    static Vector3 tempVectorBonePos;
    
    public static ZombieOnGroundState instance() {
        return ZombieOnGroundState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoGameCharacter.setCollidable(false);
        if (!isoGameCharacter.isDead()) {
            isoGameCharacter.setOnFloor(true);
        }
        if (isoGameCharacter.isDead() || isoZombie.isFakeDead()) {
            this.becomeCorpse(isoZombie);
            return;
        }
        if (isoZombie.isBecomeCrawler()) {
            return;
        }
        if (!"Tutorial".equals(Core.GameMode)) {
            isoGameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (isoGameCharacter.isDead() || isoZombie.isFakeDead()) {
            this.becomeCorpse(isoZombie);
            return;
        }
        if (!isoZombie.isBecomeCrawler()) {
            if (isoGameCharacter.hasAnimationPlayer()) {
                isoGameCharacter.getAnimationPlayer().setTargetToAngle();
            }
            isoGameCharacter.setReanimateTimer(isoGameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6f);
            if (isoGameCharacter.getReanimateTimer() <= 2.0f) {
                if (GameClient.bClient) {
                    if (isoGameCharacter.isBeingSteppedOn() && !isoZombie.isReanimatedPlayer()) {
                        isoGameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
                    }
                }
                else if (isoGameCharacter.isBeingSteppedOn() && isoZombie.getReanimatedPlayer() == null) {
                    isoGameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
                }
            }
            return;
        }
        if (isoZombie.isBeingSteppedOn() || isoZombie.isUnderVehicle()) {
            return;
        }
        isoZombie.setCrawler(true);
        isoZombie.setCanWalk(false);
        isoZombie.setReanimate(true);
        isoZombie.setBecomeCrawler(false);
    }
    
    private void becomeCorpse(final IsoZombie isoZombie) {
        if (!isoZombie.isOnDeathDone()) {
            if (GameClient.bClient) {
                if (isoZombie.shouldDoInventory()) {
                    isoZombie.becomeCorpse();
                }
                else {
                    isoZombie.networkAI.processDeadBody();
                }
            }
            else {
                isoZombie.becomeCorpse();
            }
        }
    }
    
    public static boolean isCharacterStandingOnOther(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2) {
        final AnimationPlayer animationPlayer = isoGameCharacter2.getAnimationPlayer();
        int n = DoCollisionBoneCheck(isoGameCharacter, isoGameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_Spine", -1), 0.32f);
        if (n == -1) {
            n = DoCollisionBoneCheck(isoGameCharacter, isoGameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_L_Calf", -1), 0.18f);
        }
        if (n == -1) {
            n = DoCollisionBoneCheck(isoGameCharacter, isoGameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_R_Calf", -1), 0.18f);
        }
        if (n == -1) {
            n = DoCollisionBoneCheck(isoGameCharacter, isoGameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_Head", -1), 0.28f);
        }
        return n > -1;
    }
    
    private static int DoCollisionBoneCheck(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2, final int n, final float n2) {
        final float n3 = 0.3f;
        Model.BoneToWorldCoords(isoGameCharacter2, n, ZombieOnGroundState.tempVectorBonePos);
        for (int i = 1; i <= 10; ++i) {
            final float n4 = i / 10.0f;
            ZombieOnGroundState.tempVector.x = isoGameCharacter.x;
            ZombieOnGroundState.tempVector.y = isoGameCharacter.y;
            ZombieOnGroundState.tempVector.z = isoGameCharacter.z;
            final Vector3 tempVector = ZombieOnGroundState.tempVector;
            tempVector.x += isoGameCharacter.getForwardDirection().x * n3 * n4;
            final Vector3 tempVector2 = ZombieOnGroundState.tempVector;
            tempVector2.y += isoGameCharacter.getForwardDirection().y * n3 * n4;
            ZombieOnGroundState.tempVector.x = ZombieOnGroundState.tempVectorBonePos.x - ZombieOnGroundState.tempVector.x;
            ZombieOnGroundState.tempVector.y = ZombieOnGroundState.tempVectorBonePos.y - ZombieOnGroundState.tempVector.y;
            ZombieOnGroundState.tempVector.z = 0.0f;
            if (ZombieOnGroundState.tempVector.getLength() < n2) {
                return n;
            }
        }
        return -1;
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    static {
        _instance = new ZombieOnGroundState();
        ZombieOnGroundState.tempVector = new Vector3();
        ZombieOnGroundState.tempVectorBonePos = new Vector3();
    }
}
