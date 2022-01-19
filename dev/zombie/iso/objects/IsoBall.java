// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoPlayer;
import zombie.WorldSoundManager;
import zombie.network.GameClient;
import zombie.iso.IsoMovingObject;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoPhysicsObject;

public class IsoBall extends IsoPhysicsObject
{
    private HandWeapon weapon;
    private IsoGameCharacter character;
    private int lastCheckX;
    private int lastCheckY;
    
    @Override
    public String getObjectName() {
        return "MolotovCocktail";
    }
    
    public IsoBall(final IsoCell isoCell) {
        super(isoCell);
        this.weapon = null;
        this.character = null;
        this.lastCheckX = 0;
        this.lastCheckY = 0;
    }
    
    public IsoBall(final IsoCell isoCell, final float n, final float n2, final float z, final float velX, final float velY, final HandWeapon weapon, final IsoGameCharacter character) {
        super(isoCell);
        this.weapon = null;
        this.character = null;
        this.lastCheckX = 0;
        this.lastCheckY = 0;
        this.weapon = weapon;
        this.character = character;
        this.velX = velX;
        this.velY = velY;
        final float n3 = Rand.Next(4000) / 10000.0f;
        final float n4 = Rand.Next(4000) / 10000.0f;
        final float n5 = n3 - 0.2f;
        final float n6 = n4 - 0.2f;
        this.velX += n5;
        this.velY += n6;
        this.x = n;
        this.y = n2;
        this.z = z;
        this.nx = n;
        this.ny = n2;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.terminalVelocity = -0.02f;
        final Texture loadFrameExplicit = this.sprite.LoadFrameExplicit(weapon.getTex().getName());
        if (loadFrameExplicit != null) {
            this.sprite.Animate = false;
            final int tileScale = Core.TileScale;
            this.sprite.def.scaleAspect((float)loadFrameExplicit.getWidthOrig(), (float)loadFrameExplicit.getHeightOrig(), (float)(16 * tileScale), (float)(16 * tileScale));
        }
        this.speedMod = 0.6f;
    }
    
    @Override
    public void collideGround() {
        this.Fall();
    }
    
    @Override
    public void collideWall() {
        this.Fall();
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        super.render(n, n2, n3, colorInfo, b, b2, shader);
        if (Core.bDebug) {}
    }
    
    void Fall() {
        this.getCurrentSquare().getMovingObjects().remove(this);
        this.getCell().Remove(this);
        if (!GameClient.bClient) {
            WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, 0, 600, 600);
        }
        if (this.character instanceof IsoPlayer) {
            if (((IsoPlayer)this.character).isLocalPlayer()) {
                this.square.AddWorldInventoryItem(this.weapon, Rand.Next(0.2f, 0.8f), Rand.Next(0.2f, 0.8f), 0.0f, true);
            }
        }
        else {
            DebugLog.General.error((Object)"IsoBall: character isn't instance of IsoPlayer");
        }
    }
}
