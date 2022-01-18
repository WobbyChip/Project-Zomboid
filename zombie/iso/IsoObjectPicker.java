// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.iso.sprite.IsoSprite;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoLightSwitch;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoCurtain;
import zombie.input.Mouse;
import zombie.core.textures.Texture;
import zombie.core.logger.ExceptionLogger;
import java.util.List;
import java.util.Collections;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.objects.IsoWindow;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.iso.objects.IsoDoor;
import zombie.characters.IsoSurvivor;
import java.util.Comparator;
import java.util.ArrayList;

public final class IsoObjectPicker
{
    public static final IsoObjectPicker Instance;
    static final ArrayList<ClickObject> choices;
    static final Vector2 tempo;
    static final Vector2 tempo2;
    static final Comparator<ClickObject> comp;
    public ClickObject[] ClickObjectStore;
    public int count;
    public int counter;
    public int maxcount;
    public final ArrayList<ClickObject> ThisFrame;
    public boolean dirty;
    public float xOffSinceDirty;
    public float yOffSinceDirty;
    public boolean wasDirty;
    ClickObject LastPickObject;
    float lx;
    float ly;
    
    public IsoObjectPicker() {
        this.ClickObjectStore = new ClickObject[15000];
        this.count = 0;
        this.counter = 0;
        this.maxcount = 0;
        this.ThisFrame = new ArrayList<ClickObject>();
        this.dirty = true;
        this.xOffSinceDirty = 0.0f;
        this.yOffSinceDirty = 0.0f;
        this.wasDirty = false;
        this.LastPickObject = null;
        this.lx = 0.0f;
        this.ly = 0.0f;
    }
    
    public IsoObjectPicker getInstance() {
        return IsoObjectPicker.Instance;
    }
    
    public void Add(final int x, final int y, final int width, final int height, final IsoGridSquare square, final IsoObject tile, final boolean flip, final float scaleX, final float scaleY) {
        if (x + width <= this.lx - 32.0f || x >= this.lx + 32.0f || y + height <= this.ly - 32.0f || y >= this.ly + 32.0f) {
            return;
        }
        if (this.ThisFrame.size() >= 15000) {
            return;
        }
        if (tile.NoPicking) {
            return;
        }
        if (tile instanceof IsoSurvivor) {}
        if (tile instanceof IsoDoor) {}
        if (x > Core.getInstance().getOffscreenWidth(0)) {
            return;
        }
        if (y > Core.getInstance().getOffscreenHeight(0)) {
            return;
        }
        if (x + width < 0) {
            return;
        }
        if (y + height < 0) {
            return;
        }
        final ClickObject e = this.ClickObjectStore[this.ThisFrame.size()];
        this.ThisFrame.add(e);
        this.count = this.ThisFrame.size();
        e.x = x;
        e.y = y;
        e.width = width;
        e.height = height;
        e.square = square;
        e.tile = tile;
        e.flip = flip;
        e.scaleX = scaleX;
        e.scaleY = scaleY;
        if (e.tile instanceof IsoGameCharacter) {
            e.flip = false;
        }
        if (this.count > this.maxcount) {
            this.maxcount = this.count;
        }
    }
    
    public void Init() {
        this.ThisFrame.clear();
        this.LastPickObject = null;
        for (int i = 0; i < 15000; ++i) {
            this.ClickObjectStore[i] = new ClickObject();
        }
    }
    
    public ClickObject ContextPick(final int n, final int n2) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        IsoObjectPicker.choices.clear();
        ++this.counter;
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (!(clickObject.tile instanceof IsoPlayer) || clickObject.tile != IsoPlayer.players[0]) {
                if (clickObject.tile.sprite != null) {
                    if (clickObject.tile.getTargetAlpha(0) == 0.0f) {
                        continue;
                    }
                    if (clickObject.tile.sprite.Properties.Is(IsoFlagType.cutW) || clickObject.tile.sprite.Properties.Is(IsoFlagType.cutN)) {
                        if (!(clickObject.tile instanceof IsoWindow)) {
                            if (clickObject.tile.getTargetAlpha(0) < 1.0f) {
                                continue;
                            }
                        }
                    }
                }
                if (clickObject.tile == null || clickObject.tile.sprite != null) {}
                if (n3 > clickObject.x && n4 > clickObject.y && n3 <= clickObject.x + clickObject.width && n4 <= clickObject.y + clickObject.height) {
                    if (clickObject.tile instanceof IsoPlayer) {
                        if (clickObject.tile.sprite == null) {
                            continue;
                        }
                        if (clickObject.tile.sprite.def == null) {
                            continue;
                        }
                        if (clickObject.tile.sprite.CurrentAnim == null || clickObject.tile.sprite.CurrentAnim.Frames == null || clickObject.tile.sprite.def.Frame < 0.0f) {
                            continue;
                        }
                        if (clickObject.tile.sprite.def.Frame >= clickObject.tile.sprite.CurrentAnim.Frames.size()) {
                            continue;
                        }
                        final int n5 = (int)(n3 - clickObject.x);
                        final int n6 = (int)(n4 - clickObject.y);
                        final Texture texture = clickObject.tile.sprite.CurrentAnim.Frames.get((int)clickObject.tile.sprite.def.Frame).directions[clickObject.tile.dir.index()];
                        final int tileScale = Core.TileScale;
                        int n7;
                        int n8;
                        if (clickObject.flip) {
                            n7 = (int)(n6 - texture.offsetY);
                            n8 = texture.getWidth() - n7;
                        }
                        else {
                            n8 = (int)(n5 - texture.offsetX * tileScale);
                            n7 = (int)(n6 - texture.offsetY * tileScale);
                        }
                        if (n8 >= 0 && n7 >= 0 && n8 <= texture.getWidth() * tileScale) {
                            if (n7 <= texture.getHeight() * tileScale) {
                                clickObject.lx = (int)n3 - clickObject.x;
                                clickObject.ly = (int)n4 - clickObject.y;
                                this.LastPickObject = clickObject;
                                IsoObjectPicker.choices.clear();
                                IsoObjectPicker.choices.add(clickObject);
                                break;
                            }
                        }
                    }
                    if (clickObject.scaleX != 1.0f || clickObject.scaleY != 1.0f) {
                        if (clickObject.tile.isMaskClicked((int)(clickObject.x + (n3 - clickObject.x) / clickObject.scaleX - clickObject.x), (int)(clickObject.y + (n4 - clickObject.y) / clickObject.scaleY - clickObject.y), clickObject.flip)) {
                            if (clickObject.tile.rerouteMask != null) {
                                clickObject.tile = clickObject.tile.rerouteMask;
                            }
                            clickObject.lx = (int)n3 - clickObject.x;
                            clickObject.ly = (int)n4 - clickObject.y;
                            this.LastPickObject = clickObject;
                            IsoObjectPicker.choices.add(clickObject);
                        }
                    }
                    else if (clickObject.tile.isMaskClicked((int)(n3 - clickObject.x), (int)(n4 - clickObject.y), clickObject.flip)) {
                        if (clickObject.tile.rerouteMask != null) {
                            clickObject.tile = clickObject.tile.rerouteMask;
                        }
                        clickObject.lx = (int)n3 - clickObject.x;
                        clickObject.ly = (int)n4 - clickObject.y;
                        this.LastPickObject = clickObject;
                        IsoObjectPicker.choices.add(clickObject);
                    }
                }
            }
        }
        if (IsoObjectPicker.choices.isEmpty()) {
            return null;
        }
        for (int j = 0; j < IsoObjectPicker.choices.size(); ++j) {
            final ClickObject clickObject2 = IsoObjectPicker.choices.get(j);
            clickObject2.score = clickObject2.calculateScore();
        }
        try {
            Collections.sort(IsoObjectPicker.choices, IsoObjectPicker.comp);
        }
        catch (IllegalArgumentException ex) {
            if (Core.bDebug) {
                ExceptionLogger.logException(ex);
            }
            return null;
        }
        return IsoObjectPicker.choices.get(IsoObjectPicker.choices.size() - 1);
    }
    
    public ClickObject Pick(final int n, final int n2) {
        final float n3 = (float)n;
        final float n4 = (float)n2;
        final float n5 = (float)Core.getInstance().getScreenWidth();
        final float n6 = (float)Core.getInstance().getScreenHeight();
        final float n7 = n5 * Core.getInstance().getZoom(0);
        final float n8 = n6 * Core.getInstance().getZoom(0);
        final float n9 = (float)Core.getInstance().getOffscreenWidth(0);
        final float n10 = (float)Core.getInstance().getOffscreenHeight(0);
        final float n11 = n9 / n7;
        final float n12 = n10 / n8;
        final float n13 = n3 - n5 / 2.0f;
        final float n14 = n4 - n6 / 2.0f;
        final float n15 = n13 / n11;
        final float n16 = n14 / n12;
        final float n17 = n15 + n5 / 2.0f;
        final float n18 = n16 + n6 / 2.0f;
        ++this.counter;
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject lastPickObject = this.ThisFrame.get(i);
            if (lastPickObject.tile.square != null) {}
            if (!(lastPickObject.tile instanceof IsoPlayer)) {
                if (lastPickObject.tile.sprite == null || lastPickObject.tile.getTargetAlpha(0) != 0.0f) {
                    if (lastPickObject.tile == null || lastPickObject.tile.sprite != null) {}
                    if (n17 > lastPickObject.x && n18 > lastPickObject.y && n17 <= lastPickObject.x + lastPickObject.width && n18 <= lastPickObject.y + lastPickObject.height) {
                        if (!(lastPickObject.tile instanceof IsoSurvivor)) {
                            if (lastPickObject.tile.isMaskClicked((int)(n17 - lastPickObject.x), (int)(n18 - lastPickObject.y), lastPickObject.flip)) {
                                if (lastPickObject.tile.rerouteMask != null) {
                                    lastPickObject.tile = lastPickObject.tile.rerouteMask;
                                }
                                lastPickObject.lx = (int)n17 - lastPickObject.x;
                                lastPickObject.ly = (int)n18 - lastPickObject.y;
                                return this.LastPickObject = lastPickObject;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void StartRender() {
        final float lx = (float)Mouse.getX();
        final float ly = (float)Mouse.getY();
        if (lx != this.lx || ly != this.ly) {
            this.dirty = true;
        }
        this.lx = lx;
        this.ly = ly;
        if (this.dirty) {
            this.ThisFrame.clear();
            this.count = 0;
            this.wasDirty = true;
            this.dirty = false;
            this.xOffSinceDirty = 0.0f;
            this.yOffSinceDirty = 0.0f;
        }
        else {
            this.wasDirty = false;
        }
    }
    
    public IsoMovingObject PickTarget(final int n, final int n2) {
        final float n3 = (float)n;
        final float n4 = (float)n2;
        final float n5 = (float)Core.getInstance().getScreenWidth();
        final float n6 = (float)Core.getInstance().getScreenHeight();
        final float n7 = n5 * Core.getInstance().getZoom(0);
        final float n8 = n6 * Core.getInstance().getZoom(0);
        final float n9 = (float)Core.getInstance().getOffscreenWidth(0);
        final float n10 = (float)Core.getInstance().getOffscreenHeight(0);
        final float n11 = n9 / n7;
        final float n12 = n10 / n8;
        final float n13 = n3 - n5 / 2.0f;
        final float n14 = n4 - n6 / 2.0f;
        final float n15 = n13 / n11;
        final float n16 = n14 / n12;
        final float n17 = n15 + n5 / 2.0f;
        final float n18 = n16 + n6 / 2.0f;
        ++this.counter;
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject lastPickObject = this.ThisFrame.get(i);
            if (lastPickObject.tile.square != null) {}
            if (lastPickObject.tile != IsoPlayer.getInstance()) {
                if (lastPickObject.tile.sprite == null || lastPickObject.tile.getTargetAlpha() != 0.0f) {
                    if (lastPickObject.tile == null || lastPickObject.tile.sprite != null) {}
                    if (n17 > lastPickObject.x && n18 > lastPickObject.y && n17 <= lastPickObject.x + lastPickObject.width && n18 <= lastPickObject.y + lastPickObject.height && lastPickObject.tile instanceof IsoMovingObject && lastPickObject.tile.isMaskClicked((int)(n17 - lastPickObject.x), (int)(n18 - lastPickObject.y), lastPickObject.flip)) {
                        if (lastPickObject.tile.rerouteMask != null) {}
                        lastPickObject.lx = (int)(n17 - lastPickObject.x);
                        lastPickObject.ly = (int)(n18 - lastPickObject.y);
                        this.LastPickObject = lastPickObject;
                        return (IsoMovingObject)lastPickObject.tile;
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject PickDoor(final int n, final int n2, final boolean b) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        final int playerIndex = IsoPlayer.getPlayerIndex();
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (clickObject.tile instanceof IsoDoor) {
                if (clickObject.tile.getTargetAlpha(playerIndex) != 0.0f) {
                    if (b == clickObject.tile.getTargetAlpha(playerIndex) < 1.0f) {
                        if (n3 >= clickObject.x && n4 >= clickObject.y && n3 < clickObject.x + clickObject.width && n4 < clickObject.y + clickObject.height && clickObject.tile.isMaskClicked((int)(n3 - clickObject.x), (int)(n4 - clickObject.y), clickObject.flip)) {
                            return clickObject.tile;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject PickWindow(final int n, final int n2) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (clickObject.tile instanceof IsoWindow || clickObject.tile instanceof IsoCurtain) {
                if (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0f) {
                    if (n3 >= clickObject.x && n4 >= clickObject.y && n3 < clickObject.x + clickObject.width && n4 < clickObject.y + clickObject.height) {
                        final int n5 = (int)(n3 - clickObject.x);
                        final int n6 = (int)(n4 - clickObject.y);
                        if (clickObject.tile.isMaskClicked(n5, n6, clickObject.flip)) {
                            return clickObject.tile;
                        }
                        if (clickObject.tile instanceof IsoWindow) {
                            boolean b = false;
                            boolean b2 = false;
                            for (int j = n6; j >= 0; --j) {
                                if (clickObject.tile.isMaskClicked(n5, j)) {
                                    b = true;
                                    break;
                                }
                            }
                            for (int k = n6; k < clickObject.height; ++k) {
                                if (clickObject.tile.isMaskClicked(n5, k)) {
                                    b2 = true;
                                    break;
                                }
                            }
                            if (b && b2) {
                                return clickObject.tile;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject PickWindowFrame(final int n, final int n2) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (IsoWindowFrame.isWindowFrame(clickObject.tile)) {
                if (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0f) {
                    if (n3 >= clickObject.x && n4 >= clickObject.y && n3 < clickObject.x + clickObject.width && n4 < clickObject.y + clickObject.height) {
                        final int n5 = (int)(n3 - clickObject.x);
                        final int n6 = (int)(n4 - clickObject.y);
                        if (clickObject.tile.isMaskClicked(n5, n6, clickObject.flip)) {
                            return clickObject.tile;
                        }
                        boolean b = false;
                        boolean b2 = false;
                        for (int j = n6; j >= 0; --j) {
                            if (clickObject.tile.isMaskClicked(n5, j)) {
                                b = true;
                                break;
                            }
                        }
                        for (int k = n6; k < clickObject.height; ++k) {
                            if (clickObject.tile.isMaskClicked(n5, k)) {
                                b2 = true;
                                break;
                            }
                        }
                        if (b && b2) {
                            return clickObject.tile;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject PickThumpable(final int n, final int n2) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (clickObject.tile instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)clickObject.tile;
                if (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0f) {
                    if (n3 >= clickObject.x && n4 >= clickObject.y && n3 < clickObject.x + clickObject.width && n4 < clickObject.y + clickObject.height && clickObject.tile.isMaskClicked((int)(n3 - clickObject.x), (int)(n4 - clickObject.y), clickObject.flip)) {
                        return clickObject.tile;
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject PickCorpse(final int n, final int n2) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (n3 >= clickObject.x && n4 >= clickObject.y && n3 < clickObject.x + clickObject.width && n4 < clickObject.y + clickObject.height) {
                if (clickObject.tile.getTargetAlpha() >= 1.0f) {
                    if (clickObject.tile.isMaskClicked((int)(n3 - clickObject.x), (int)(n4 - clickObject.y), clickObject.flip) && !(clickObject.tile instanceof IsoWindow)) {
                        return null;
                    }
                    if (clickObject.tile instanceof IsoDeadBody) {
                        if (((IsoDeadBody)clickObject.tile).isMouseOver(n3, n4)) {
                            return clickObject.tile;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject PickTree(final int n, final int n2) {
        final float n3 = n * Core.getInstance().getZoom(0);
        final float n4 = n2 * Core.getInstance().getZoom(0);
        for (int i = this.ThisFrame.size() - 1; i >= 0; --i) {
            final ClickObject clickObject = this.ThisFrame.get(i);
            if (clickObject.tile instanceof IsoTree) {
                if (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0f) {
                    if (n3 >= clickObject.x && n4 >= clickObject.y && n3 < clickObject.x + clickObject.width && n4 < clickObject.y + clickObject.height && clickObject.tile.isMaskClicked((int)(n3 - clickObject.x), (int)(n4 - clickObject.y), clickObject.flip)) {
                        return clickObject.tile;
                    }
                }
            }
        }
        return null;
    }
    
    public BaseVehicle PickVehicle(final int n, final int n2) {
        final float xToIso = IsoUtils.XToIso((float)n, (float)n2, 0.0f);
        final float yToIso = IsoUtils.YToIso((float)n, (float)n2, 0.0f);
        for (int i = 0; i < IsoWorld.instance.CurrentCell.getVehicles().size(); ++i) {
            final BaseVehicle baseVehicle = IsoWorld.instance.CurrentCell.getVehicles().get(i);
            if (baseVehicle.isInBounds(xToIso, yToIso)) {
                return baseVehicle;
            }
        }
        return null;
    }
    
    static {
        Instance = new IsoObjectPicker();
        choices = new ArrayList<ClickObject>();
        tempo = new Vector2();
        tempo2 = new Vector2();
        comp = new Comparator<ClickObject>() {
            @Override
            public int compare(final ClickObject clickObject, final ClickObject clickObject2) {
                final int score = clickObject.getScore();
                final int score2 = clickObject2.getScore();
                if (score > score2) {
                    return 1;
                }
                if (score < score2) {
                    return -1;
                }
                if (clickObject.tile != null && clickObject.tile.square != null && clickObject2.tile != null && clickObject.tile.square == clickObject2.tile.square) {
                    return clickObject.tile.getObjectIndex() - clickObject2.tile.getObjectIndex();
                }
                return 0;
            }
        };
    }
    
    public static final class ClickObject
    {
        public int height;
        public IsoGridSquare square;
        public IsoObject tile;
        public int width;
        public int x;
        public int y;
        public int lx;
        public int ly;
        public float scaleX;
        public float scaleY;
        private boolean flip;
        private int score;
        
        public int calculateScore() {
            final float n = 1.0f;
            final IsoPlayer instance = IsoPlayer.getInstance();
            IsoObjectPicker.tempo.x = this.square.getX() + 0.5f;
            IsoObjectPicker.tempo.y = this.square.getY() + 0.5f;
            final Vector2 tempo = IsoObjectPicker.tempo;
            tempo.x -= instance.getX();
            final Vector2 tempo2 = IsoObjectPicker.tempo;
            tempo2.y -= instance.getY();
            IsoObjectPicker.tempo.normalize();
            final float n2 = n + Math.abs(instance.getVectorFromDirection(IsoObjectPicker.tempo2).dot(IsoObjectPicker.tempo) * 4.0f);
            final IsoGridSquare square = this.square;
            final IsoObject tile = this.tile;
            final IsoSprite sprite = tile.sprite;
            float n3;
            if (tile instanceof IsoDoor || (tile instanceof IsoThumpable && ((IsoThumpable)tile).isDoor())) {
                n3 = n2 + 6.0f;
                if (instance.getZ() > square.getZ()) {
                    n3 -= 1000.0f;
                }
            }
            else if (tile instanceof IsoWindow) {
                n3 = n2 + 4.0f;
                if (instance.getZ() > square.getZ()) {
                    n3 -= 1000.0f;
                }
            }
            else {
                if (instance.getCurrentSquare() != null && square.getRoom() == instance.getCurrentSquare().getRoom()) {
                    n3 = n2 + 1.0f;
                }
                else {
                    n3 = n2 - 100000.0f;
                }
                if (instance.getZ() > square.getZ()) {
                    n3 -= 1000.0f;
                }
                if (tile instanceof IsoPlayer) {
                    n3 -= 100000.0f;
                }
                else if (tile instanceof IsoThumpable && tile.getTargetAlpha() < 0.99f && (tile.getTargetAlpha() < 0.5f || tile.getContainer() == null)) {
                    n3 -= 100000.0f;
                }
                if (tile instanceof IsoCurtain) {
                    n3 += 3.0f;
                }
                else if (tile instanceof IsoLightSwitch) {
                    n3 += 20.0f;
                }
                else if (sprite.Properties.Is(IsoFlagType.bed)) {
                    n3 += 2.0f;
                }
                else if (tile.container != null) {
                    n3 += 10.0f;
                }
                else if (tile instanceof IsoWaveSignal) {
                    n3 += 20.0f;
                }
                else if (tile instanceof IsoThumpable && ((IsoThumpable)tile).getLightSource() != null) {
                    n3 += 3.0f;
                }
                else if (sprite.Properties.Is(IsoFlagType.waterPiped)) {
                    n3 += 3.0f;
                }
                else if (sprite.Properties.Is(IsoFlagType.solidfloor)) {
                    n3 -= 100.0f;
                }
                else if (sprite.getType() == IsoObjectType.WestRoofB) {
                    n3 -= 100.0f;
                }
                else if (sprite.getType() == IsoObjectType.WestRoofM) {
                    n3 -= 100.0f;
                }
                else if (sprite.getType() == IsoObjectType.WestRoofT) {
                    n3 -= 100.0f;
                }
                else if (sprite.Properties.Is(IsoFlagType.cutW) || sprite.Properties.Is(IsoFlagType.cutN)) {
                    n3 -= 2.0f;
                }
            }
            return (int)(n3 - IsoUtils.DistanceManhatten(square.getX() + 0.5f, square.getY() + 0.5f, instance.getX(), instance.getY()) / 2.0f);
        }
        
        public int getScore() {
            return this.score;
        }
    }
}
