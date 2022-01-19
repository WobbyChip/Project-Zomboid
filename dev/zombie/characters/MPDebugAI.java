// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.vehicles.PathFindBehavior2;
import zombie.ai.states.PathFindState;
import zombie.iso.IsoDirections;
import zombie.debug.DebugOptions;
import java.util.Iterator;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;
import zombie.iso.Vector2;

public class MPDebugAI
{
    private static final Vector2 tempo;
    private static final Vector2 tempo2;
    
    public static IsoPlayer getNearestPlayer(final IsoPlayer isoPlayer) {
        IsoMovingObject isoMovingObject = null;
        for (final IsoPlayer isoPlayer2 : GameClient.IDToPlayerMap.values()) {
            if (isoPlayer2 != isoPlayer && (isoMovingObject == null || isoMovingObject.getDistanceSq(isoPlayer) > isoPlayer2.getDistanceSq(isoPlayer))) {
                isoMovingObject = isoPlayer2;
            }
        }
        return (IsoPlayer)isoMovingObject;
    }
    
    public static boolean updateMovementFromInput(final IsoPlayer isoPlayer, final IsoPlayer.MoveVars moveVars) {
        if (GameClient.bClient && isoPlayer.isLocalPlayer() && (DebugOptions.instance.MultiplayerAttackPlayer.getValue() || DebugOptions.instance.MultiplayerFollowPlayer.getValue())) {
            final IsoPlayer nearestPlayer = getNearestPlayer(isoPlayer);
            if (nearestPlayer != null) {
                final Vector2 vector2 = new Vector2(nearestPlayer.x - isoPlayer.x, isoPlayer.y - nearestPlayer.y);
                vector2.rotate(-0.7853982f);
                vector2.normalize();
                moveVars.moveX = vector2.x;
                moveVars.moveY = vector2.y;
                moveVars.NewFacing = IsoDirections.fromAngle(vector2);
                if (nearestPlayer.isTeleporting() || nearestPlayer.getDistanceSq(isoPlayer) > 10.0f) {
                    isoPlayer.removeFromSquare();
                    isoPlayer.setX(nearestPlayer.realx);
                    isoPlayer.setY(nearestPlayer.realy);
                    isoPlayer.setZ(nearestPlayer.realz);
                    isoPlayer.setLx(nearestPlayer.realx);
                    isoPlayer.setLy(nearestPlayer.realy);
                    isoPlayer.setLz(nearestPlayer.realz);
                    isoPlayer.ensureOnTile();
                }
                else if (nearestPlayer.getDistanceSq(isoPlayer) > 5.0f) {
                    isoPlayer.setRunning(true);
                    isoPlayer.setSprinting(true);
                }
                else if (nearestPlayer.getDistanceSq(isoPlayer) > 2.5f) {
                    isoPlayer.setRunning(true);
                }
                else if (nearestPlayer.getDistanceSq(isoPlayer) < 1.25f) {
                    moveVars.moveX = 0.0f;
                    moveVars.moveY = 0.0f;
                }
            }
            final PathFindBehavior2 pathFindBehavior2 = isoPlayer.getPathFindBehavior2();
            if (moveVars.moveX == 0.0f && moveVars.moveY == 0.0f && isoPlayer.getPath2() != null && pathFindBehavior2.isStrafing() && !pathFindBehavior2.bStopping) {
                final Vector2 set = MPDebugAI.tempo.set(pathFindBehavior2.getTargetX() - isoPlayer.x, pathFindBehavior2.getTargetY() - isoPlayer.y);
                final Vector2 set2 = MPDebugAI.tempo2.set(-1.0f, 0.0f);
                final float n = 1.0f;
                MPDebugAI.tempo.set(set.dot(MPDebugAI.tempo2.set(0.0f, -1.0f)) / n, set.dot(set2) / n);
                MPDebugAI.tempo.normalize();
                MPDebugAI.tempo.rotate(0.7853982f);
                moveVars.moveX = MPDebugAI.tempo.x;
                moveVars.moveY = MPDebugAI.tempo.y;
            }
            if (moveVars.moveX != 0.0f || moveVars.moveY != 0.0f) {
                if (isoPlayer.stateMachine.getCurrent() == PathFindState.instance()) {
                    isoPlayer.setDefaultState();
                }
                isoPlayer.setJustMoved(true);
                isoPlayer.setMoveDelta(1.0f);
                if (isoPlayer.isStrafing()) {
                    MPDebugAI.tempo.set(moveVars.moveX, moveVars.moveY);
                    MPDebugAI.tempo.normalize();
                    float n2 = (float)(isoPlayer.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle() + 0.7853981633974483);
                    if (n2 > 6.283185307179586) {
                        n2 -= (float)6.283185307179586;
                    }
                    if (n2 < 0.0f) {
                        n2 += (float)6.283185307179586;
                    }
                    MPDebugAI.tempo.rotate(n2);
                    moveVars.strafeX = MPDebugAI.tempo.x;
                    moveVars.strafeY = MPDebugAI.tempo.y;
                }
                else {
                    MPDebugAI.tempo.set(moveVars.moveX, -moveVars.moveY);
                    MPDebugAI.tempo.normalize();
                    MPDebugAI.tempo.rotate(-0.7853982f);
                    isoPlayer.setForwardDirection(MPDebugAI.tempo);
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean updateInputState(final IsoPlayer isoPlayer, final IsoPlayer.InputState inputState) {
        if (GameClient.bClient && isoPlayer.isLocalPlayer() && DebugOptions.instance.MultiplayerAttackPlayer.getValue()) {
            final IsoPlayer nearestPlayer = getNearestPlayer(isoPlayer);
            inputState.bMelee = false;
            inputState.isAttacking = false;
            inputState.isCharging = false;
            inputState.isAiming = false;
            inputState.bRunning = false;
            inputState.bSprinting = false;
            if (nearestPlayer != null) {
                inputState.isCharging = true;
                inputState.isAiming = false;
                if (nearestPlayer.getDistanceSq(isoPlayer) < 0.5f) {
                    inputState.bMelee = true;
                    inputState.isAttacking = true;
                }
            }
            return true;
        }
        if (GameClient.bClient && isoPlayer.isLocalPlayer() && DebugOptions.instance.MultiplayerFollowPlayer.getValue()) {
            getNearestPlayer(isoPlayer);
            inputState.bMelee = false;
            inputState.isAttacking = false;
            inputState.isCharging = false;
            inputState.isAiming = false;
            inputState.bRunning = false;
            inputState.bSprinting = false;
            return true;
        }
        return false;
    }
    
    static {
        tempo = new Vector2();
        tempo2 = new Vector2();
    }
}
