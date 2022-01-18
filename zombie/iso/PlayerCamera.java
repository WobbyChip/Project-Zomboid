// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.ui.UIManager;
import zombie.network.GameServer;
import zombie.iso.sprite.IsoSprite;
import zombie.input.Mouse;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.core.math.PZMath;
import zombie.core.PerformanceSettings;
import org.joml.Vector3fc;
import zombie.vehicles.BaseVehicle;
import zombie.core.Core;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import org.joml.Vector3f;

public final class PlayerCamera
{
    public final int playerIndex;
    public float OffX;
    public float OffY;
    public float TOffX;
    public float TOffY;
    public float lastOffX;
    public float lastOffY;
    public float RightClickTargetX;
    public float RightClickTargetY;
    public float RightClickX;
    public float RightClickY;
    private float RightClickX_f;
    private float RightClickY_f;
    public float DeferedX;
    public float DeferedY;
    public float zoom;
    public int OffscreenWidth;
    public int OffscreenHeight;
    private static final Vector2 offVec;
    private static float PAN_SPEED;
    private long panTime;
    private final Vector3f m_lastVehicleForwardDirection;
    
    public PlayerCamera(final int playerIndex) {
        this.panTime = -1L;
        this.m_lastVehicleForwardDirection = new Vector3f();
        this.playerIndex = playerIndex;
    }
    
    public void center() {
        float offX = this.OffX;
        float offY = this.OffY;
        if (IsoCamera.CamCharacter != null) {
            final IsoGameCharacter camCharacter = IsoCamera.CamCharacter;
            final float xToScreen = IsoUtils.XToScreen(camCharacter.x + this.DeferedX, camCharacter.y + this.DeferedY, camCharacter.z, 0);
            final float yToScreen = IsoUtils.YToScreen(camCharacter.x + this.DeferedX, camCharacter.y + this.DeferedY, camCharacter.z, 0);
            final float n = xToScreen - IsoCamera.getOffscreenWidth(this.playerIndex) / 2;
            final float n2 = yToScreen - IsoCamera.getOffscreenHeight(this.playerIndex) / 2 - camCharacter.getOffsetY() * 1.5f;
            offX = n + IsoCamera.PLAYER_OFFSET_X;
            offY = n2 + IsoCamera.PLAYER_OFFSET_Y;
        }
        final float n3 = offX;
        this.TOffX = n3;
        this.OffX = n3;
        final float n4 = offY;
        this.TOffY = n4;
        this.OffY = n4;
    }
    
    public void update() {
        this.center();
        final float n = (this.TOffX - this.OffX) / 15.0f;
        final float n2 = (this.TOffY - this.OffY) / 15.0f;
        this.OffX += n;
        this.OffY += n2;
        if (this.lastOffX == 0.0f && this.lastOffY == 0.0f) {
            this.lastOffX = this.OffX;
            this.lastOffY = this.OffY;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        PlayerCamera.PAN_SPEED = 110.0f;
        final float n3 = 1.0f / ((this.panTime < 0L) ? 1.0f : ((currentTimeMillis - this.panTime) / 1000.0f * PlayerCamera.PAN_SPEED));
        this.panTime = currentTimeMillis;
        final IsoPlayer isoPlayer = IsoPlayer.players[this.playerIndex];
        final boolean b = GameWindow.ActivatedJoyPad != null && isoPlayer != null && isoPlayer.JoypadBind != -1;
        final BaseVehicle baseVehicle = (isoPlayer == null) ? null : isoPlayer.getVehicle();
        if (baseVehicle != null && baseVehicle.getCurrentSpeedKmHour() <= 1.0f) {
            baseVehicle.getForwardVector(this.m_lastVehicleForwardDirection);
        }
        if (Core.getInstance().getOptionPanCameraWhileDriving() && baseVehicle != null && baseVehicle.getCurrentSpeedKmHour() > 1.0f) {
            final float zoom = Core.getInstance().getZoom(this.playerIndex);
            final float n4 = baseVehicle.getCurrentSpeedKmHour() * BaseVehicle.getFakeSpeedModifier() / 10.0f * zoom;
            final Vector3f forwardVector = baseVehicle.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
            final float n5 = this.m_lastVehicleForwardDirection.angle((Vector3fc)forwardVector) * 57.295776f;
            if (n5 > 1.0f) {
                this.m_lastVehicleForwardDirection.lerp((Vector3fc)forwardVector, PZMath.max(n5 / 180.0f / PerformanceSettings.getLockFPS(), 0.1f), forwardVector);
                this.m_lastVehicleForwardDirection.set((Vector3fc)forwardVector);
            }
            this.RightClickTargetX = (float)(int)IsoUtils.XToScreen(forwardVector.x * n4, forwardVector.z * n4, isoPlayer.z, 0);
            this.RightClickTargetY = (float)(int)IsoUtils.YToScreen(forwardVector.x * n4, forwardVector.z * n4, isoPlayer.z, 0);
            BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
            final int n6 = 0;
            final int n7 = 0;
            final int offscreenWidth = IsoCamera.getOffscreenWidth(this.playerIndex);
            final int offscreenHeight = IsoCamera.getOffscreenHeight(this.playerIndex);
            final float n8 = n6 + offscreenWidth / 2.0f;
            final float n9 = n7 + offscreenHeight / 2.0f;
            final float n10 = 150.0f * zoom;
            this.RightClickTargetX = (int)PZMath.clamp(n8 + this.RightClickTargetX, n10, offscreenWidth - n10) - n8;
            this.RightClickTargetY = (int)PZMath.clamp(n9 + this.RightClickTargetY, n10, offscreenHeight - n10) - n9;
            if (Math.abs(n4) < 5.0f) {
                this.returnToCenter(1.0f / (16.0f * n3 / (1.0f - Math.abs(n4) / 5.0f)));
            }
            else {
                float n11 = n3 / (0.5f * zoom);
                final float xToScreenExact = IsoUtils.XToScreenExact(isoPlayer.x, isoPlayer.y, isoPlayer.z, 0);
                final float yToScreenExact = IsoUtils.YToScreenExact(isoPlayer.x, isoPlayer.y, isoPlayer.z, 0);
                if (xToScreenExact < n10 / 2.0f || xToScreenExact > offscreenWidth - n10 / 2.0f || yToScreenExact < n10 / 2.0f || yToScreenExact > offscreenHeight - n10 / 2.0f) {
                    n11 /= 4.0f;
                }
                this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, 1.875f * PZMath.sign(this.RightClickTargetX - this.RightClickX_f) / n11);
                this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, 1.875f * PZMath.sign(this.RightClickTargetY - this.RightClickY_f) / n11);
                this.RightClickX = (float)(int)this.RightClickX_f;
                this.RightClickY = (float)(int)this.RightClickY_f;
            }
        }
        else if (b && isoPlayer != null) {
            if ((isoPlayer.IsAiming() || isoPlayer.isLookingWhileInVehicle()) && JoypadManager.instance.isRBPressed(isoPlayer.JoypadBind) && !isoPlayer.bJoypadIgnoreAimUntilCentered) {
                this.RightClickTargetX = JoypadManager.instance.getAimingAxisX(isoPlayer.JoypadBind) * 1500.0f;
                this.RightClickTargetY = JoypadManager.instance.getAimingAxisY(isoPlayer.JoypadBind) * 1500.0f;
                final float n12 = n3 / (0.5f * Core.getInstance().getZoom(this.playerIndex));
                this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) / (80.0f * n12));
                this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) / (80.0f * n12));
                this.RightClickX = (float)(int)this.RightClickX_f;
                this.RightClickY = (float)(int)this.RightClickY_f;
                isoPlayer.dirtyRecalcGridStackTime = 2.0f;
            }
            else {
                this.returnToCenter(1.0f / (16.0f * n3));
            }
        }
        else if (this.playerIndex == 0 && isoPlayer != null && !isoPlayer.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey("PanCamera"))) {
            final int screenWidth = IsoCamera.getScreenWidth(this.playerIndex);
            final int screenHeight = IsoCamera.getScreenHeight(this.playerIndex);
            final int screenLeft = IsoCamera.getScreenLeft(this.playerIndex);
            final int screenTop = IsoCamera.getScreenTop(this.playerIndex);
            float n13 = Mouse.getXA() - (screenLeft + screenWidth / 2.0f);
            float n14 = Mouse.getYA() - (screenTop + screenHeight / 2.0f);
            float n15;
            if (screenWidth > screenHeight) {
                n15 = screenHeight / (float)screenWidth;
                n13 *= n15;
            }
            else {
                n15 = screenWidth / (float)screenHeight;
                n14 *= n15;
            }
            final float n16 = n15 * (screenWidth / 1366.0f);
            PlayerCamera.offVec.set(n13, n14);
            PlayerCamera.offVec.setLength(Math.min(PlayerCamera.offVec.getLength(), Math.min(screenWidth, screenHeight) / 2.0f));
            final float n17 = PlayerCamera.offVec.x / n16;
            final float n18 = PlayerCamera.offVec.y / n16;
            this.RightClickTargetX = n17 * 2.0f;
            this.RightClickTargetY = n18 * 2.0f;
            final float n19 = n3 / (0.5f * Core.getInstance().getZoom(this.playerIndex));
            this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) / (80.0f * n19));
            this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) / (80.0f * n19));
            this.RightClickX = (float)(int)this.RightClickX_f;
            this.RightClickY = (float)(int)this.RightClickY_f;
            isoPlayer.dirtyRecalcGridStackTime = 2.0f;
            IsoSprite.globalOffsetX = -1.0f;
        }
        else if (this.playerIndex == 0 && Core.getInstance().getOptionPanCameraWhileAiming()) {
            final boolean b2 = !GameServer.bServer;
            final boolean b3 = !UIManager.isMouseOverInventory() && isoPlayer != null && isoPlayer.isAiming();
            final boolean b4 = !b && isoPlayer != null && !isoPlayer.isDead();
            if (b2 && b3 && b4) {
                final int screenWidth2 = IsoCamera.getScreenWidth(this.playerIndex);
                final int screenHeight2 = IsoCamera.getScreenHeight(this.playerIndex);
                final int screenLeft2 = IsoCamera.getScreenLeft(this.playerIndex);
                final int screenTop2 = IsoCamera.getScreenTop(this.playerIndex);
                float n20 = Mouse.getXA() - (screenLeft2 + screenWidth2 / 2.0f);
                float n21 = Mouse.getYA() - (screenTop2 + screenHeight2 / 2.0f);
                float n22;
                if (screenWidth2 > screenHeight2) {
                    n22 = screenHeight2 / (float)screenWidth2;
                    n20 *= n22;
                }
                else {
                    n22 = screenWidth2 / (float)screenHeight2;
                    n21 *= n22;
                }
                final float n23 = n22 * (screenWidth2 / 1366.0f);
                final float n24 = Math.min(screenWidth2, screenHeight2) / 2.0f - Math.min(screenWidth2, screenHeight2) / 6.0f;
                PlayerCamera.offVec.set(n20, n21);
                float n26;
                float n25;
                if (PlayerCamera.offVec.getLength() < n24) {
                    n25 = (n26 = 0.0f);
                }
                else {
                    PlayerCamera.offVec.setLength(Math.min(PlayerCamera.offVec.getLength(), Math.min(screenWidth2, screenHeight2) / 2.0f) - n24);
                    n26 = PlayerCamera.offVec.x / n23;
                    n25 = PlayerCamera.offVec.y / n23;
                }
                this.RightClickTargetX = n26 * 7.0f;
                this.RightClickTargetY = n25 * 7.0f;
                final float n27 = n3 / (0.5f * Core.getInstance().getZoom(this.playerIndex));
                this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) / (80.0f * n27));
                this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) / (80.0f * n27));
                this.RightClickX = (float)(int)this.RightClickX_f;
                this.RightClickY = (float)(int)this.RightClickY_f;
                isoPlayer.dirtyRecalcGridStackTime = 2.0f;
            }
            else {
                this.returnToCenter(1.0f / (16.0f * n3));
            }
            IsoSprite.globalOffsetX = -1.0f;
        }
        else {
            this.returnToCenter(1.0f / (16.0f * n3));
        }
        this.zoom = Core.getInstance().getZoom(this.playerIndex);
    }
    
    private void returnToCenter(final float n) {
        this.RightClickTargetX = 0.0f;
        this.RightClickTargetY = 0.0f;
        if (this.RightClickTargetX != this.RightClickX || this.RightClickTargetY != this.RightClickY) {
            this.RightClickX_f = PZMath.step(this.RightClickX_f, this.RightClickTargetX, (this.RightClickTargetX - this.RightClickX_f) * n);
            this.RightClickY_f = PZMath.step(this.RightClickY_f, this.RightClickTargetY, (this.RightClickTargetY - this.RightClickY_f) * n);
            this.RightClickX = (float)(int)this.RightClickX_f;
            this.RightClickY = (float)(int)this.RightClickY_f;
            if (Math.abs(this.RightClickTargetX - this.RightClickX_f) < 0.001f) {
                this.RightClickX = (float)(int)this.RightClickTargetX;
                this.RightClickX_f = this.RightClickX;
            }
            if (Math.abs(this.RightClickTargetY - this.RightClickY_f) < 0.001f) {
                this.RightClickY = (float)(int)this.RightClickTargetY;
                this.RightClickY_f = this.RightClickY;
            }
            IsoPlayer.players[this.playerIndex].dirtyRecalcGridStackTime = 2.0f;
        }
    }
    
    public float getOffX() {
        return (float)(int)(this.OffX + this.RightClickX);
    }
    
    public float getOffY() {
        return (float)(int)(this.OffY + this.RightClickY);
    }
    
    public float getTOffX() {
        return (float)(int)(this.OffX + this.RightClickX - (this.TOffX - this.OffX));
    }
    
    public float getTOffY() {
        return (float)(int)(this.OffY + this.RightClickY - (this.TOffY - this.OffY));
    }
    
    public float getLastOffX() {
        return (float)(int)(this.lastOffX + this.RightClickX);
    }
    
    public float getLastOffY() {
        return (float)(int)(this.lastOffY + this.RightClickY);
    }
    
    public float XToIso(float n, float n2, final float n3) {
        n = (float)(int)n;
        n2 = (float)(int)n2;
        return (n + this.getOffX() + 2.0f * (n2 + this.getOffY())) / (64.0f * Core.TileScale) + 3.0f * n3;
    }
    
    public float YToIso(float n, float n2, final float n3) {
        n = (float)(int)n;
        n2 = (float)(int)n2;
        return (n + this.getOffX() - 2.0f * (n2 + this.getOffY())) / (-64.0f * Core.TileScale) + 3.0f * n3;
    }
    
    public float YToScreenExact(final float n, final float n2, final float n3, final int n4) {
        return IsoUtils.YToScreen(n, n2, n3, n4) - this.getOffY();
    }
    
    public float XToScreenExact(final float n, final float n2, final float n3, final int n4) {
        return IsoUtils.XToScreen(n, n2, n3, n4) - this.getOffX();
    }
    
    public void copyFrom(final PlayerCamera playerCamera) {
        this.OffX = playerCamera.OffX;
        this.OffY = playerCamera.OffY;
        this.TOffX = playerCamera.TOffX;
        this.TOffY = playerCamera.TOffY;
        this.lastOffX = playerCamera.lastOffX;
        this.lastOffY = playerCamera.lastOffY;
        this.RightClickTargetX = playerCamera.RightClickTargetX;
        this.RightClickTargetY = playerCamera.RightClickTargetY;
        this.RightClickX = playerCamera.RightClickX;
        this.RightClickY = playerCamera.RightClickY;
        this.DeferedX = playerCamera.DeferedX;
        this.DeferedY = playerCamera.DeferedY;
        this.zoom = playerCamera.zoom;
        this.OffscreenWidth = playerCamera.OffscreenWidth;
        this.OffscreenHeight = playerCamera.OffscreenHeight;
    }
    
    public void initFromIsoCamera(final int n) {
        this.copyFrom(IsoCamera.cameras[n]);
        this.zoom = Core.getInstance().getZoom(n);
        this.OffscreenWidth = IsoCamera.getOffscreenWidth(n);
        this.OffscreenHeight = IsoCamera.getOffscreenHeight(n);
    }
    
    static {
        offVec = new Vector2();
        PlayerCamera.PAN_SPEED = 1.0f;
    }
}
