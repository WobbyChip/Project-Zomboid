// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.characters.IsoPlayer;
import java.io.IOException;
import zombie.inventory.InventoryItem;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.audio.BaseSoundEmitter;
import zombie.WorldSoundManager;
import zombie.SandboxOptions;
import zombie.core.math.PZMath;
import zombie.GameTime;
import zombie.util.StringUtils;
import zombie.core.textures.Texture;
import zombie.core.Core;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoGridSquare;
import zombie.core.PerformanceSettings;
import zombie.network.GameServer;
import zombie.iso.IsoCell;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoObject;

public class IsoTrap extends IsoObject
{
    private int timerBeforeExplosion;
    private int FPS;
    private int sensorRange;
    private int firePower;
    private int fireRange;
    private int explosionPower;
    private int explosionRange;
    private int smokeRange;
    private int noiseRange;
    private int noiseDuration;
    private float noiseStartTime;
    private float lastWorldSoundTime;
    private float extraDamage;
    private int remoteControlID;
    private String countDownSound;
    private String explosionSound;
    private int lastBeep;
    private HandWeapon weapon;
    private boolean instantExplosion;
    
    public IsoTrap(final IsoCell isoCell) {
        super(isoCell);
        this.timerBeforeExplosion = 0;
        this.sensorRange = 0;
        this.firePower = 0;
        this.fireRange = 0;
        this.explosionPower = 0;
        this.explosionRange = 0;
        this.smokeRange = 0;
        this.noiseRange = 0;
        this.noiseDuration = 0;
        this.noiseStartTime = 0.0f;
        this.lastWorldSoundTime = 0.0f;
        this.extraDamage = 0.0f;
        this.remoteControlID = -1;
        this.countDownSound = null;
        this.explosionSound = null;
        this.lastBeep = 0;
        this.FPS = (GameServer.bServer ? 10 : PerformanceSettings.getLockFPS());
    }
    
    public IsoTrap(final HandWeapon weapon, final IsoCell isoCell, final IsoGridSquare square) {
        this.timerBeforeExplosion = 0;
        this.sensorRange = 0;
        this.firePower = 0;
        this.fireRange = 0;
        this.explosionPower = 0;
        this.explosionRange = 0;
        this.smokeRange = 0;
        this.noiseRange = 0;
        this.noiseDuration = 0;
        this.noiseStartTime = 0.0f;
        this.lastWorldSoundTime = 0.0f;
        this.extraDamage = 0.0f;
        this.remoteControlID = -1;
        this.countDownSound = null;
        this.explosionSound = null;
        this.lastBeep = 0;
        this.square = square;
        this.initSprite(weapon);
        this.setSensorRange(weapon.getSensorRange());
        this.setFireRange(weapon.getFireRange());
        this.setFirePower(weapon.getFirePower());
        this.setExplosionPower(weapon.getExplosionPower());
        this.setExplosionRange(weapon.getExplosionRange());
        this.setSmokeRange(weapon.getSmokeRange());
        this.setNoiseRange(weapon.getNoiseRange());
        this.setNoiseDuration(weapon.getNoiseDuration());
        this.setExtraDamage(weapon.getExtraDamage());
        this.setRemoteControlID(weapon.getRemoteControlID());
        this.setCountDownSound(weapon.getCountDownSound());
        this.setExplosionSound(weapon.getExplosionSound());
        this.FPS = (GameServer.bServer ? 10 : PerformanceSettings.getLockFPS());
        if (weapon.getExplosionTimer() > 0) {
            this.timerBeforeExplosion = weapon.getExplosionTimer() * this.FPS - 1;
        }
        else if (!weapon.canBeRemote()) {
            this.timerBeforeExplosion = 1;
        }
        if (weapon.canBePlaced()) {
            this.weapon = weapon;
        }
        this.instantExplosion = weapon.isInstantExplosion();
    }
    
    private void initSprite(final HandWeapon handWeapon) {
        if (handWeapon == null) {
            return;
        }
        String s;
        if (handWeapon.getPlacedSprite() != null && !handWeapon.getPlacedSprite().isEmpty()) {
            s = handWeapon.getPlacedSprite();
        }
        else if (handWeapon.getTex() != null && handWeapon.getTex().getName() != null) {
            s = handWeapon.getTex().getName();
        }
        else {
            s = "media/inventory/world/WItem_Sack.png";
        }
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        final Texture loadFrameExplicit = this.sprite.LoadFrameExplicit(s);
        if (s.startsWith("Item_") && loadFrameExplicit != null) {
            if (handWeapon.getScriptItem() == null) {
                this.sprite.def.scaleAspect((float)loadFrameExplicit.getWidthOrig(), (float)loadFrameExplicit.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
            }
            else {
                final float n = handWeapon.getScriptItem().ScaleWorldIcon * (Core.TileScale / 2.0f);
                this.sprite.def.setScale(n, n);
            }
        }
    }
    
    @Override
    public void update() {
        if (this.timerBeforeExplosion > 0) {
            if (this.timerBeforeExplosion / this.FPS + 1 != this.lastBeep) {
                this.lastBeep = this.timerBeforeExplosion / this.FPS + 1;
                if (!GameServer.bServer) {
                    if (this.getObjectIndex() != -1) {
                        this.getOrCreateEmitter();
                        if (!StringUtils.isNullOrWhitespace(this.getCountDownSound())) {
                            this.emitter.playSound(this.getCountDownSound());
                        }
                        else if (this.lastBeep == 1) {
                            this.emitter.playSound("TrapTimerExpired");
                        }
                        else {
                            this.emitter.playSound("TrapTimerLoop");
                        }
                    }
                }
            }
            --this.timerBeforeExplosion;
            if (this.timerBeforeExplosion == 0) {
                this.triggerExplosion(this.getSensorRange() > 0);
            }
        }
        this.updateSounds();
    }
    
    private void updateSounds() {
        if (this.noiseStartTime > 0.0f) {
            final float lastWorldSoundTime = (float)GameTime.getInstance().getWorldAgeHours();
            this.noiseStartTime = PZMath.min(this.noiseStartTime, lastWorldSoundTime);
            this.lastWorldSoundTime = PZMath.min(this.lastWorldSoundTime, lastWorldSoundTime);
            final float n = 60.0f / SandboxOptions.getInstance().getDayLengthMinutes();
            final float n2 = 60.0f;
            if (lastWorldSoundTime - this.noiseStartTime > this.getNoiseDuration() / n2 * n) {
                this.noiseStartTime = 0.0f;
                if (this.emitter != null) {
                    this.emitter.stopAll();
                }
            }
            else {
                if (!GameServer.bServer && (this.emitter == null || !this.emitter.isPlaying(this.getExplosionSound()))) {
                    final BaseSoundEmitter orCreateEmitter = this.getOrCreateEmitter();
                    if (orCreateEmitter != null) {
                        orCreateEmitter.playSound(this.getExplosionSound());
                    }
                }
                if (lastWorldSoundTime - this.lastWorldSoundTime > 1.0f / n2 * n && this.getObjectIndex() != -1) {
                    this.lastWorldSoundTime = lastWorldSoundTime;
                    WorldSoundManager.instance.addSoundRepeating(null, this.getSquare().getX(), this.getSquare().getY(), this.getSquare().getZ(), this.getNoiseRange(), 1, true);
                }
            }
        }
        if (this.emitter != null) {
            this.emitter.tick();
        }
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.sprite.CurrentAnim == null || this.sprite.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final Texture texture = this.sprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
        if (texture == null) {
            return;
        }
        if (texture.getName().startsWith("Item_")) {
            final float n4 = texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0f;
            final float n5 = texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0f / 4.0f;
            this.setAlphaAndTarget(1.0f);
            this.offsetX = 0.0f;
            this.offsetY = 0.0f;
            this.sx = 0.0f;
            this.sprite.render(this, n + 0.5f, n2 + 0.5f, n3, this.dir, this.offsetX + n4, this.offsetY + n5, colorInfo, true);
        }
        else {
            this.offsetX = (float)(32 * Core.TileScale);
            this.offsetY = (float)(96 * Core.TileScale);
            this.sx = 0.0f;
            super.render(n, n2, n3, colorInfo, b, b2, shader);
        }
    }
    
    public void triggerExplosion(final boolean b) {
        if (b) {
            if (this.getSensorRange() > 0) {
                this.square.setTrapPositionX(this.square.getX());
                this.square.setTrapPositionY(this.square.getY());
                this.square.setTrapPositionZ(this.square.getZ());
                this.square.drawCircleExplosion(this.getSensorRange(), this, ExplosionMode.Sensor);
            }
        }
        else {
            if (this.getExplosionSound() != null) {
                this.playExplosionSound();
            }
            if (this.getNoiseRange() > 0) {
                WorldSoundManager.instance.addSound(null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), this.getNoiseRange(), 1);
            }
            else if (this.getExplosionSound() != null) {
                WorldSoundManager.instance.addSound(null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 50, 1);
            }
            if (this.getExplosionRange() > 0) {
                this.square.drawCircleExplosion(this.getExplosionRange(), this, ExplosionMode.Explosion);
            }
            if (this.getFireRange() > 0) {
                this.square.drawCircleExplosion(this.getFireRange(), this, ExplosionMode.Fire);
            }
            if (this.getSmokeRange() > 0) {
                this.square.drawCircleExplosion(this.getSmokeRange(), this, ExplosionMode.Smoke);
            }
            if (this.weapon == null || !this.weapon.canBeReused()) {
                if (GameServer.bServer) {
                    GameServer.RemoveItemFromMap(this);
                }
                else {
                    this.removeFromWorld();
                    this.removeFromSquare();
                }
            }
        }
    }
    
    private BaseSoundEmitter getOrCreateEmitter() {
        if (this.getObjectIndex() == -1) {
            return null;
        }
        if (this.emitter == null) {
            this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, this.getZ());
            IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
        }
        return this.emitter;
    }
    
    public void playExplosionSound() {
        if (StringUtils.isNullOrWhitespace(this.getExplosionSound())) {
            return;
        }
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (this.getNoiseRange() > 0 && this.getNoiseDuration() > 0.0f) {
            this.noiseStartTime = (float)GameTime.getInstance().getWorldAgeHours();
        }
        if (GameServer.bServer) {
            GameServer.PlayWorldSoundServer(this.getExplosionSound(), false, this.getSquare(), 0.0f, 50.0f, 1.0f, false);
            return;
        }
        this.getOrCreateEmitter();
        if (!this.emitter.isPlaying(this.getExplosionSound())) {
            this.emitter.playSound(this.getExplosionSound());
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.sensorRange = byteBuffer.getInt();
        this.firePower = byteBuffer.getInt();
        this.fireRange = byteBuffer.getInt();
        this.explosionPower = byteBuffer.getInt();
        this.explosionRange = byteBuffer.getInt();
        this.smokeRange = byteBuffer.getInt();
        this.noiseRange = byteBuffer.getInt();
        if (n >= 180) {
            this.noiseDuration = byteBuffer.getInt();
            this.noiseStartTime = byteBuffer.getFloat();
        }
        this.extraDamage = byteBuffer.getFloat();
        this.remoteControlID = byteBuffer.getInt();
        if (n >= 78) {
            this.timerBeforeExplosion = byteBuffer.getInt() * this.FPS;
            this.countDownSound = GameWindow.ReadStringUTF(byteBuffer);
            this.explosionSound = GameWindow.ReadStringUTF(byteBuffer);
            if ("bigExplosion".equals(this.explosionSound)) {
                this.explosionSound = "BigExplosion";
            }
            if ("smallExplosion".equals(this.explosionSound)) {
                this.explosionSound = "SmallExplosion";
            }
            if ("feedback".equals(this.explosionSound)) {
                this.explosionSound = "NoiseTrapExplosion";
            }
        }
        if (n >= 82 && byteBuffer.get() == 1) {
            final InventoryItem loadItem = InventoryItem.loadItem(byteBuffer, n);
            if (loadItem instanceof HandWeapon) {
                this.initSprite(this.weapon = (HandWeapon)loadItem);
            }
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putInt(this.sensorRange);
        byteBuffer.putInt(this.firePower);
        byteBuffer.putInt(this.fireRange);
        byteBuffer.putInt(this.explosionPower);
        byteBuffer.putInt(this.explosionRange);
        byteBuffer.putInt(this.smokeRange);
        byteBuffer.putInt(this.noiseRange);
        byteBuffer.putInt(this.noiseDuration);
        byteBuffer.putFloat(this.noiseStartTime);
        byteBuffer.putFloat(this.extraDamage);
        byteBuffer.putInt(this.remoteControlID);
        byteBuffer.putInt((this.timerBeforeExplosion > 1) ? Math.max(this.timerBeforeExplosion / this.FPS, 1) : 0);
        GameWindow.WriteStringUTF(byteBuffer, this.countDownSound);
        GameWindow.WriteStringUTF(byteBuffer, this.explosionSound);
        if (this.weapon != null) {
            byteBuffer.put((byte)1);
            this.weapon.saveWithSize(byteBuffer, false);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
    }
    
    @Override
    public void removeFromWorld() {
        if (this.emitter != null) {
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
        }
        super.removeFromWorld();
    }
    
    public int getTimerBeforeExplosion() {
        return this.timerBeforeExplosion;
    }
    
    public void setTimerBeforeExplosion(final int timerBeforeExplosion) {
        this.timerBeforeExplosion = timerBeforeExplosion;
    }
    
    public int getSensorRange() {
        return this.sensorRange;
    }
    
    public void setSensorRange(final int sensorRange) {
        this.sensorRange = sensorRange;
    }
    
    public int getFireRange() {
        return this.fireRange;
    }
    
    public void setFireRange(final int fireRange) {
        this.fireRange = fireRange;
    }
    
    public int getFirePower() {
        return this.firePower;
    }
    
    public void setFirePower(final int firePower) {
        this.firePower = firePower;
    }
    
    public int getExplosionPower() {
        return this.explosionPower;
    }
    
    public void setExplosionPower(final int explosionPower) {
        this.explosionPower = explosionPower;
    }
    
    public int getNoiseDuration() {
        return this.noiseDuration;
    }
    
    public void setNoiseDuration(final int noiseDuration) {
        this.noiseDuration = noiseDuration;
    }
    
    public int getNoiseRange() {
        return this.noiseRange;
    }
    
    public void setNoiseRange(final int noiseRange) {
        this.noiseRange = noiseRange;
    }
    
    public int getExplosionRange() {
        return this.explosionRange;
    }
    
    public void setExplosionRange(final int explosionRange) {
        this.explosionRange = explosionRange;
    }
    
    public int getSmokeRange() {
        return this.smokeRange;
    }
    
    public void setSmokeRange(final int smokeRange) {
        this.smokeRange = smokeRange;
    }
    
    public float getExtraDamage() {
        return this.extraDamage;
    }
    
    public void setExtraDamage(final float extraDamage) {
        this.extraDamage = extraDamage;
    }
    
    @Override
    public String getObjectName() {
        return "IsoTrap";
    }
    
    public int getRemoteControlID() {
        return this.remoteControlID;
    }
    
    public void setRemoteControlID(final int remoteControlID) {
        this.remoteControlID = remoteControlID;
    }
    
    public String getCountDownSound() {
        return this.countDownSound;
    }
    
    public void setCountDownSound(final String countDownSound) {
        this.countDownSound = countDownSound;
    }
    
    public String getExplosionSound() {
        return this.explosionSound;
    }
    
    public void setExplosionSound(final String explosionSound) {
        this.explosionSound = explosionSound;
    }
    
    public InventoryItem getItem() {
        return this.weapon;
    }
    
    public static void triggerRemote(final IsoPlayer isoPlayer, final int n, final int n2) {
        final int n3 = (int)isoPlayer.getX();
        final int n4 = (int)isoPlayer.getY();
        final int n5 = (int)isoPlayer.getZ();
        final int max = Math.max(n5 - n2 / 2, 0);
        final int min = Math.min(n5 + n2 / 2, 8);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = max; i < min; ++i) {
            for (int j = n4 - n2; j < n4 + n2; ++j) {
                for (int k = n3 - n2; k < n3 + n2; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(k, j, i);
                    if (gridSquare != null) {
                        for (int l = gridSquare.getObjects().size() - 1; l >= 0; --l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (isoObject instanceof IsoTrap && ((IsoTrap)isoObject).getRemoteControlID() == n) {
                                ((IsoTrap)isoObject).triggerExplosion(false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean isInstantExplosion() {
        return this.instantExplosion;
    }
    
    public enum ExplosionMode
    {
        Explosion, 
        Fire, 
        Smoke, 
        Sensor;
        
        private static /* synthetic */ ExplosionMode[] $values() {
            return new ExplosionMode[] { ExplosionMode.Explosion, ExplosionMode.Fire, ExplosionMode.Smoke, ExplosionMode.Sensor };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
