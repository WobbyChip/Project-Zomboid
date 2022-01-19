// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.util.LocationRNG;
import zombie.core.math.PZMath;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.IsoSpriteInstance;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.textures.Texture;
import zombie.network.GameServer;
import zombie.core.Core;
import zombie.util.StringUtils;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.ArrayList;
import gnu.trove.map.hash.THashMap;

public class TileOverlays
{
    public static final TileOverlays instance;
    private static final THashMap<String, TileOverlay> overlayMap;
    private static final ArrayList<TileOverlayEntry> tempEntries;
    
    public void addOverlays(final KahluaTableImpl kahluaTableImpl) {
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final String string = iterator.getKey().toString();
            if ("VERSION".equalsIgnoreCase(string)) {
                continue;
            }
            final TileOverlay tileOverlay = new TileOverlay();
            tileOverlay.tile = string;
            final KahluaTableIterator iterator2 = ((KahluaTableImpl)iterator.getValue()).iterator();
            while (iterator2.advance()) {
                final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator2.getValue();
                final TileOverlayEntry e = new TileOverlayEntry();
                e.room = kahluaTableImpl2.rawgetStr((Object)"name");
                e.chance = kahluaTableImpl2.rawgetInt((Object)"chance");
                e.usage.parse(kahluaTableImpl2.rawgetStr((Object)"usage"));
                final KahluaTableIterator iterator3 = ((KahluaTableImpl)kahluaTableImpl2.rawget((Object)"tiles")).iterator();
                while (iterator3.advance()) {
                    String string2 = iterator3.getValue().toString();
                    if (StringUtils.isNullOrWhitespace(string2) || "none".equalsIgnoreCase(string2)) {
                        string2 = "";
                    }
                    else if (Core.bDebug && !GameServer.bServer && Texture.getSharedTexture(string2) == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string2));
                    }
                    e.tiles.add(string2);
                }
                tileOverlay.entries.add(e);
            }
            TileOverlays.overlayMap.put((Object)tileOverlay.tile, (Object)tileOverlay);
        }
    }
    
    public boolean hasOverlays(final IsoObject isoObject) {
        return isoObject != null && isoObject.sprite != null && isoObject.sprite.name != null && TileOverlays.overlayMap.containsKey((Object)isoObject.sprite.name);
    }
    
    public void updateTileOverlaySprite(final IsoObject isoObject) {
        if (isoObject == null) {
            return;
        }
        final IsoGridSquare square = isoObject.getSquare();
        if (square == null) {
            return;
        }
        String pickRandom = null;
        float tintr = -1.0f;
        float tintg = -1.0f;
        float tintb = -1.0f;
        float alpha = -1.0f;
        if (isoObject.sprite != null && isoObject.sprite.name != null) {
            final TileOverlay tileOverlay = (TileOverlay)TileOverlays.overlayMap.get((Object)isoObject.sprite.name);
            if (tileOverlay != null) {
                String name = "other";
                if (square.getRoom() != null) {
                    name = square.getRoom().getName();
                }
                TileOverlayEntry tileOverlayEntry = tileOverlay.pickRandom(name, square);
                if (tileOverlayEntry == null) {
                    tileOverlayEntry = tileOverlay.pickRandom("other", square);
                }
                if (tileOverlayEntry != null) {
                    if (tileOverlayEntry.usage.bTableTop && this.hasObjectOnTop(isoObject)) {
                        return;
                    }
                    pickRandom = tileOverlayEntry.pickRandom(square.x, square.y, square.z);
                    if (tileOverlayEntry.usage.alpha >= 0.0f) {
                        tintg = (tintr = (tintb = 1.0f));
                        alpha = tileOverlayEntry.usage.alpha;
                    }
                }
            }
        }
        if (!StringUtils.isNullOrWhitespace(pickRandom) && !GameServer.bServer && Texture.getSharedTexture(pickRandom) == null) {
            pickRandom = null;
        }
        if (!StringUtils.isNullOrWhitespace(pickRandom)) {
            if (isoObject.AttachedAnimSprite == null) {
                isoObject.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>(4);
            }
            final IsoSprite sprite = IsoSpriteManager.instance.getSprite(pickRandom);
            sprite.name = pickRandom;
            final IsoSpriteInstance value = IsoSpriteInstance.get(sprite);
            if (alpha > 0.0f) {
                value.tintr = tintr;
                value.tintg = tintg;
                value.tintb = tintb;
                value.alpha = alpha;
            }
            value.bCopyTargetAlpha = false;
            value.bMultiplyObjectAlpha = true;
            isoObject.AttachedAnimSprite.add(value);
        }
    }
    
    private boolean hasObjectOnTop(final IsoObject isoObject) {
        if (!isoObject.isTableSurface()) {
            return false;
        }
        final IsoGridSquare square = isoObject.getSquare();
        for (int i = isoObject.getObjectIndex() + 1; i < square.getObjects().size(); ++i) {
            final IsoObject isoObject2 = square.getObjects().get(i);
            if (isoObject2.isTableTopObject() || isoObject2.isTableSurface()) {
                return true;
            }
        }
        return false;
    }
    
    public void fixTableTopOverlays(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare.getObjects().isEmpty()) {
            return;
        }
        int n = 0;
        for (int i = isoGridSquare.getObjects().size() - 1; i >= 0; --i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (n != 0 && isoObject.isTableSurface()) {
                this.removeTableTopOverlays(isoObject);
            }
            if (isoObject.isTableSurface() || isoObject.isTableTopObject()) {
                n = 1;
            }
        }
    }
    
    private void removeTableTopOverlays(final IsoObject isoObject) {
        if (isoObject == null || !isoObject.isTableSurface()) {
            return;
        }
        if (isoObject.sprite == null || isoObject.sprite.name == null) {
            return;
        }
        if (isoObject.AttachedAnimSprite == null || isoObject.AttachedAnimSprite.isEmpty()) {
            return;
        }
        final TileOverlay tileOverlay = (TileOverlay)TileOverlays.overlayMap.get((Object)isoObject.sprite.name);
        if (tileOverlay == null) {
            return;
        }
        final int size = isoObject.AttachedAnimSprite.size();
        for (int i = 0; i < tileOverlay.entries.size(); ++i) {
            final TileOverlayEntry tileOverlayEntry = tileOverlay.entries.get(i);
            if (tileOverlayEntry.usage.bTableTop) {
                for (int j = 0; j < tileOverlayEntry.tiles.size(); ++j) {
                    this.tryRemoveAttachedSprite(isoObject.AttachedAnimSprite, tileOverlayEntry.tiles.get(j));
                }
            }
        }
        if (size != isoObject.AttachedAnimSprite.size()) {}
    }
    
    private void tryRemoveAttachedSprite(final ArrayList<IsoSpriteInstance> list, final String s) {
        for (int i = 0; i < list.size(); ++i) {
            final IsoSpriteInstance isoSpriteInstance = list.get(i);
            if (s.equals(isoSpriteInstance.getName())) {
                list.remove(i--);
                IsoSpriteInstance.add(isoSpriteInstance);
            }
        }
    }
    
    public void Reset() {
        TileOverlays.overlayMap.clear();
    }
    
    static {
        instance = new TileOverlays();
        overlayMap = new THashMap();
        tempEntries = new ArrayList<TileOverlayEntry>();
    }
    
    private static final class TileOverlayUsage
    {
        String usage;
        int zOnly;
        int zGreaterThan;
        float alpha;
        boolean bTableTop;
        
        private TileOverlayUsage() {
            this.zOnly = -1;
            this.zGreaterThan = -1;
            this.alpha = -1.0f;
            this.bTableTop = false;
        }
        
        boolean parse(final String s) {
            this.usage = s.trim();
            if (StringUtils.isNullOrWhitespace(this.usage)) {
                return true;
            }
            final String[] split = s.split(";");
            for (int i = 0; i < split.length; ++i) {
                final String s2 = split[i];
                if (s2.startsWith("z=")) {
                    this.zOnly = Integer.parseInt(s2.substring(2));
                }
                else if (s2.startsWith("z>")) {
                    this.zGreaterThan = Integer.parseInt(s2.substring(2));
                }
                else if (s2.startsWith("alpha=")) {
                    this.alpha = Float.parseFloat(s2.substring(6));
                    this.alpha = PZMath.clamp(this.alpha, 0.0f, 1.0f);
                }
                else {
                    if (!s2.startsWith("tabletop")) {
                        return false;
                    }
                    this.bTableTop = true;
                }
            }
            return true;
        }
        
        boolean match(final IsoGridSquare isoGridSquare) {
            return (this.zOnly == -1 || isoGridSquare.z == this.zOnly) && (this.zGreaterThan == -1 || isoGridSquare.z > this.zGreaterThan);
        }
    }
    
    private static final class TileOverlayEntry
    {
        public String room;
        public int chance;
        public final ArrayList<String> tiles;
        public final TileOverlayUsage usage;
        
        private TileOverlayEntry() {
            this.tiles = new ArrayList<String>();
            this.usage = new TileOverlayUsage();
        }
        
        public boolean matchUsage(final IsoGridSquare isoGridSquare) {
            return this.usage.match(isoGridSquare);
        }
        
        public String pickRandom(final int n, final int n2, final int n3) {
            if (LocationRNG.instance.nextInt(this.chance, n, n2, n3) == 0 && !this.tiles.isEmpty()) {
                return this.tiles.get(LocationRNG.instance.nextInt(this.tiles.size()));
            }
            return null;
        }
    }
    
    private static final class TileOverlay
    {
        public String tile;
        public final ArrayList<TileOverlayEntry> entries;
        
        private TileOverlay() {
            this.entries = new ArrayList<TileOverlayEntry>();
        }
        
        public void getEntries(final String anotherString, final IsoGridSquare isoGridSquare, final ArrayList<TileOverlayEntry> list) {
            list.clear();
            for (int i = 0; i < this.entries.size(); ++i) {
                final TileOverlayEntry e = this.entries.get(i);
                if (e.room.equalsIgnoreCase(anotherString) && e.matchUsage(isoGridSquare)) {
                    list.add(e);
                }
            }
        }
        
        public TileOverlayEntry pickRandom(final String s, final IsoGridSquare isoGridSquare) {
            this.getEntries(s, isoGridSquare, TileOverlays.tempEntries);
            if (TileOverlays.tempEntries.isEmpty()) {
                return null;
            }
            return TileOverlays.tempEntries.get(LocationRNG.instance.nextInt(TileOverlays.tempEntries.size(), isoGridSquare.x, isoGridSquare.y, isoGridSquare.z));
        }
    }
}
