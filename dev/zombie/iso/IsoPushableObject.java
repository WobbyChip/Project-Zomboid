// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.io.IOException;
import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.objects.IsoWheelieBin;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.core.Core;
import zombie.iso.sprite.IsoSprite;
import java.util.ArrayList;

public class IsoPushableObject extends IsoMovingObject
{
    public int carryCapacity;
    public float emptyWeight;
    public ArrayList<IsoPushableObject> connectList;
    public float ox;
    public float oy;
    
    public IsoPushableObject(final IsoCell isoCell) {
        super(isoCell);
        this.carryCapacity = 100;
        this.emptyWeight = 4.5f;
        this.connectList = null;
        this.ox = 0.0f;
        this.oy = 0.0f;
        this.getCell().getPushableObjectList().add(this);
    }
    
    public IsoPushableObject(final IsoCell isoCell, final int n, final int n2, final int n3) {
        super(isoCell, true);
        this.carryCapacity = 100;
        this.emptyWeight = 4.5f;
        this.connectList = null;
        this.ox = 0.0f;
        this.oy = 0.0f;
        this.getCell().getPushableObjectList().add(this);
    }
    
    public IsoPushableObject(final IsoCell isoCell, final IsoGridSquare square, final IsoSprite isoSprite) {
        super(isoCell, square, isoSprite, true);
        this.carryCapacity = 100;
        this.emptyWeight = 4.5f;
        this.connectList = null;
        this.ox = 0.0f;
        this.oy = 0.0f;
        this.setX(square.getX() + 0.5f);
        this.setY(square.getY() + 0.5f);
        this.setZ((float)square.getZ());
        this.ox = this.getX();
        this.oy = this.getY();
        this.setNx(this.getX());
        this.setNy(this.getNy());
        this.offsetX = (float)(6 * Core.TileScale);
        this.offsetY = (float)(-30 * Core.TileScale);
        this.setWeight(6.0f);
        this.setCurrent(this.square = square);
        this.getCurrentSquare().getMovingObjects().add(this);
        this.Collidable = true;
        this.solid = true;
        this.shootable = false;
        this.width = 0.5f;
        this.setAlphaAndTarget(0.0f);
        this.getCell().getPushableObjectList().add(this);
    }
    
    @Override
    public String getObjectName() {
        return "Pushable";
    }
    
    @Override
    public void update() {
        if (this.connectList != null) {
            final Iterator<IsoPushableObject> iterator = this.connectList.iterator();
            float alphaAndTarget = 0.0f;
            while (iterator.hasNext()) {
                final float alpha = iterator.next().getAlpha();
                if (alpha > alphaAndTarget) {
                    alphaAndTarget = alpha;
                }
            }
            this.setAlphaAndTarget(alphaAndTarget);
        }
        super.update();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        if (!(this instanceof IsoWheelieBin)) {
            this.sprite = IsoSpriteManager.instance.getSprite(byteBuffer.getInt());
        }
        if (byteBuffer.get() == 1) {
            (this.container = new ItemContainer()).load(byteBuffer, n);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        if (!(this instanceof IsoWheelieBin)) {
            byteBuffer.putInt(this.sprite.ID);
        }
        if (this.container != null) {
            byteBuffer.put((byte)1);
            this.container.save(byteBuffer);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public float getWeight(final float n, final float n2) {
        if (this.container == null) {
            return this.emptyWeight;
        }
        float n3 = this.container.getContentsWeight() / this.carryCapacity;
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n3 > 1.0f) {
            return this.getWeight() * 8.0f;
        }
        return this.getWeight() * n3 + this.emptyWeight;
    }
    
    @Override
    public boolean Serialize() {
        return true;
    }
    
    @Override
    public void DoCollideNorS() {
        if (this.connectList == null) {
            super.DoCollideNorS();
            return;
        }
        for (final IsoPushableObject isoPushableObject : this.connectList) {
            if (isoPushableObject == this) {
                continue;
            }
            if (isoPushableObject.ox < this.ox) {
                isoPushableObject.setNx(this.getNx() - 1.0f);
                isoPushableObject.setX(this.getX() - 1.0f);
            }
            else if (isoPushableObject.ox > this.ox) {
                isoPushableObject.setNx(this.getNx() + 1.0f);
                isoPushableObject.setX(this.getX() + 1.0f);
            }
            else {
                isoPushableObject.setNx(this.getNx());
                isoPushableObject.setX(this.getX());
            }
            if (isoPushableObject.oy < this.oy) {
                isoPushableObject.setNy(this.getNy() - 1.0f);
                isoPushableObject.setY(this.getY() - 1.0f);
            }
            else if (isoPushableObject.oy > this.oy) {
                isoPushableObject.setNy(this.getNy() + 1.0f);
                isoPushableObject.setY(this.getY() + 1.0f);
            }
            else {
                isoPushableObject.setNy(this.getNy());
                isoPushableObject.setY(this.getY());
            }
            isoPushableObject.setImpulsex(this.getImpulsex());
            isoPushableObject.setImpulsey(this.getImpulsey());
        }
    }
    
    @Override
    public void DoCollideWorE() {
        if (this.connectList == null) {
            super.DoCollideWorE();
            return;
        }
        for (final IsoPushableObject isoPushableObject : this.connectList) {
            if (isoPushableObject == this) {
                continue;
            }
            if (isoPushableObject.ox < this.ox) {
                isoPushableObject.setNx(this.getNx() - 1.0f);
                isoPushableObject.setX(this.getX() - 1.0f);
            }
            else if (isoPushableObject.ox > this.ox) {
                isoPushableObject.setNx(this.getNx() + 1.0f);
                isoPushableObject.setX(this.getX() + 1.0f);
            }
            else {
                isoPushableObject.setNx(this.getNx());
                isoPushableObject.setX(this.getX());
            }
            if (isoPushableObject.oy < this.oy) {
                isoPushableObject.setNy(this.getNy() - 1.0f);
                isoPushableObject.setY(this.getY() - 1.0f);
            }
            else if (isoPushableObject.oy > this.oy) {
                isoPushableObject.setNy(this.getNy() + 1.0f);
                isoPushableObject.setY(this.getY() + 1.0f);
            }
            else {
                isoPushableObject.setNy(this.getNy());
                isoPushableObject.setY(this.getY());
            }
            isoPushableObject.setImpulsex(this.getImpulsex());
            isoPushableObject.setImpulsey(this.getImpulsey());
        }
    }
}
