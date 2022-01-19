// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.obj;

import zombie.erosion.ErosionMain;
import zombie.iso.sprite.IsoSpriteInstance;
import java.util.ArrayList;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.objects.IsoTree;
import zombie.util.list.PZArrayList;
import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;

public final class ErosionObj
{
    private final ErosionObjSprites sprites;
    public String name;
    public int stages;
    public boolean hasSnow;
    public boolean hasFlower;
    public boolean hasChildSprite;
    public float bloomStart;
    public float bloomEnd;
    public boolean noSeasonBase;
    public int cycleTime;
    
    public ErosionObj(final ErosionObjSprites sprites, final int cycleTime, final float bloomStart, final float bloomEnd, final boolean noSeasonBase) {
        this.cycleTime = 1;
        this.sprites = sprites;
        this.name = sprites.name;
        this.stages = sprites.stages;
        this.hasSnow = sprites.hasSnow;
        this.hasFlower = sprites.hasFlower;
        this.hasChildSprite = sprites.hasChildSprite;
        this.bloomStart = bloomStart;
        this.bloomEnd = bloomEnd;
        this.noSeasonBase = noSeasonBase;
        this.cycleTime = cycleTime;
    }
    
    public IsoObject getObject(final IsoGridSquare isoGridSquare, final boolean b) {
        final PZArrayList<IsoObject> objects = isoGridSquare.getObjects();
        for (int i = objects.size() - 1; i >= 0; --i) {
            final IsoObject isoObject = objects.get(i);
            if (this.name.equals(isoObject.getName())) {
                if (b) {
                    objects.remove(i);
                }
                isoObject.doNotSync = true;
                return isoObject;
            }
        }
        return null;
    }
    
    public IsoObject createObject(final IsoGridSquare square, final int n, final boolean b, final int n2) {
        String base = this.sprites.getBase(n, this.noSeasonBase ? 0 : n2);
        if (base == null) {
            base = "";
        }
        IsoObject isoObject;
        if (b) {
            isoObject = IsoTree.getNew();
            isoObject.sprite = IsoSpriteManager.instance.NamedMap.get(base);
            isoObject.square = square;
            isoObject.sx = 0.0f;
            ((IsoTree)isoObject).initTree();
        }
        else {
            isoObject = IsoObject.getNew(square, base, this.name, false);
        }
        isoObject.setName(this.name);
        isoObject.doNotSync = true;
        return isoObject;
    }
    
    public boolean placeObject(final IsoGridSquare isoGridSquare, final int n, final boolean b, final int n2, final boolean b2) {
        final IsoObject object = this.createObject(isoGridSquare, n, b, n2);
        if (object != null && this.setStageObject(n, object, n2, b2)) {
            object.doNotSync = true;
            if (!b) {
                isoGridSquare.getObjects().add(object);
                object.addToWorld();
            }
            else {
                isoGridSquare.AddTileObject(object);
            }
            return true;
        }
        return false;
    }
    
    public boolean setStageObject(final int n, final IsoObject isoObject, final int n2, final boolean b) {
        isoObject.doNotSync = true;
        if (n < 0 || n >= this.stages || isoObject == null) {
            return false;
        }
        final String base = this.sprites.getBase(n, this.noSeasonBase ? 0 : n2);
        if (base == null) {
            isoObject.setSprite(this.getSprite(""));
            if (isoObject.AttachedAnimSprite != null) {
                isoObject.AttachedAnimSprite.clear();
            }
            return true;
        }
        isoObject.setSprite(this.getSprite(base));
        if (this.hasChildSprite || this.hasFlower) {
            if (isoObject.AttachedAnimSprite == null) {
                isoObject.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>();
            }
            isoObject.AttachedAnimSprite.clear();
            if (this.hasChildSprite && n2 != 0) {
                final String childSprite = this.sprites.getChildSprite(n, n2);
                if (childSprite != null) {
                    isoObject.AttachedAnimSprite.add(this.getSprite(childSprite).newInstance());
                }
            }
            if (this.hasFlower && b) {
                final String flower = this.sprites.getFlower(n);
                if (flower != null) {
                    isoObject.AttachedAnimSprite.add(this.getSprite(flower).newInstance());
                }
            }
        }
        return true;
    }
    
    public boolean setStage(final IsoGridSquare isoGridSquare, final int n, final int n2, final boolean b) {
        final IsoObject object = this.getObject(isoGridSquare, false);
        return object != null && this.setStageObject(n, object, n2, b);
    }
    
    public IsoObject removeObject(final IsoGridSquare isoGridSquare) {
        return this.getObject(isoGridSquare, true);
    }
    
    private IsoSprite getSprite(final String s) {
        return ErosionMain.getInstance().getSpriteManager().getSprite(s);
    }
}
