// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.util.LocationRNG;
import zombie.inventory.ItemContainer;
import zombie.core.textures.Texture;
import zombie.network.GameServer;
import zombie.iso.objects.IsoStove;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.StringUtils;
import java.util.Iterator;
import java.util.Map;
import se.krka.kahlua.j2se.KahluaTableImpl;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;

public class ContainerOverlays
{
    public static final ContainerOverlays instance;
    private static final ArrayList<ContainerOverlayEntry> tempEntries;
    private final THashMap<String, ContainerOverlay> overlayMap;
    
    public ContainerOverlays() {
        this.overlayMap = (THashMap<String, ContainerOverlay>)new THashMap();
    }
    
    private void parseContainerOverlayMapV0(final KahluaTableImpl kahluaTableImpl) {
        for (final Map.Entry<Object, V> entry : kahluaTableImpl.delegate.entrySet()) {
            final String string = entry.getKey().toString();
            final ContainerOverlay containerOverlay = new ContainerOverlay();
            containerOverlay.name = string;
            this.overlayMap.put((Object)containerOverlay.name, (Object)containerOverlay);
            for (final Map.Entry<Object, V> entry2 : ((KahluaTableImpl)entry.getValue()).delegate.entrySet()) {
                final String string2 = entry2.getKey().toString();
                final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry2.getValue();
                String string3 = null;
                if (kahluaTableImpl2.delegate.containsKey(1.0)) {
                    string3 = kahluaTableImpl2.rawget((Object)1.0).toString();
                }
                String string4 = null;
                if (kahluaTableImpl2.delegate.containsKey(2.0)) {
                    string4 = kahluaTableImpl2.rawget((Object)2.0).toString();
                }
                final ContainerOverlayEntry e = new ContainerOverlayEntry();
                e.manyItems = string3;
                e.fewItems = string4;
                e.room = string2;
                containerOverlay.entries.add(e);
            }
        }
    }
    
    private void parseContainerOverlayMapV1(final KahluaTableImpl kahluaTableImpl) {
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final String string = iterator.getKey().toString();
            if ("VERSION".equalsIgnoreCase(string)) {
                continue;
            }
            final ContainerOverlay containerOverlay = new ContainerOverlay();
            containerOverlay.name = string;
            final KahluaTableIterator iterator2 = ((KahluaTableImpl)iterator.getValue()).iterator();
            while (iterator2.advance()) {
                final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator2.getValue();
                final String rawgetStr = kahluaTableImpl2.rawgetStr((Object)"name");
                final KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget((Object)"tiles");
                final ContainerOverlayEntry e = new ContainerOverlayEntry();
                e.manyItems = (String)kahluaTableImpl3.rawget(1);
                e.fewItems = (String)kahluaTableImpl3.rawget(2);
                if (StringUtils.isNullOrWhitespace(e.manyItems) || "none".equalsIgnoreCase(e.manyItems)) {
                    e.manyItems = null;
                }
                if (StringUtils.isNullOrWhitespace(e.fewItems) || "none".equalsIgnoreCase(e.fewItems)) {
                    e.fewItems = null;
                }
                e.room = rawgetStr;
                containerOverlay.entries.add(e);
            }
            this.overlayMap.put((Object)containerOverlay.name, (Object)containerOverlay);
        }
    }
    
    public void addOverlays(final KahluaTableImpl kahluaTableImpl) {
        final int rawgetInt = kahluaTableImpl.rawgetInt((Object)"VERSION");
        if (rawgetInt == -1) {
            this.parseContainerOverlayMapV0(kahluaTableImpl);
        }
        else {
            if (rawgetInt != 1) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, rawgetInt));
            }
            this.parseContainerOverlayMapV1(kahluaTableImpl);
        }
    }
    
    public boolean hasOverlays(final IsoObject isoObject) {
        return isoObject != null && isoObject.sprite != null && isoObject.sprite.name != null && this.overlayMap.containsKey((Object)isoObject.sprite.name);
    }
    
    public void updateContainerOverlaySprite(final IsoObject isoObject) {
        if (isoObject == null) {
            return;
        }
        if (isoObject instanceof IsoStove) {
            return;
        }
        final IsoGridSquare square = isoObject.getSquare();
        if (square == null) {
            return;
        }
        String overlaySprite = null;
        final ItemContainer container = isoObject.getContainer();
        if (isoObject.sprite != null && isoObject.sprite.name != null && container != null && container.getItems() != null && !container.isEmpty()) {
            final ContainerOverlay containerOverlay = (ContainerOverlay)this.overlayMap.get((Object)isoObject.sprite.name);
            if (containerOverlay != null) {
                String name = "other";
                if (square.getRoom() != null) {
                    name = square.getRoom().getName();
                }
                ContainerOverlayEntry containerOverlayEntry = containerOverlay.pickRandom(name, square.x, square.y, square.z);
                if (containerOverlayEntry == null) {
                    containerOverlayEntry = containerOverlay.pickRandom("other", square.x, square.y, square.z);
                }
                if (containerOverlayEntry != null) {
                    overlaySprite = containerOverlayEntry.manyItems;
                    if (containerOverlayEntry.fewItems != null && container.getItems().size() < 7) {
                        overlaySprite = containerOverlayEntry.fewItems;
                    }
                }
            }
        }
        if (!StringUtils.isNullOrWhitespace(overlaySprite) && !GameServer.bServer && Texture.getSharedTexture(overlaySprite) == null) {
            overlaySprite = null;
        }
        isoObject.setOverlaySprite(overlaySprite);
    }
    
    public void Reset() {
        this.overlayMap.clear();
    }
    
    static {
        instance = new ContainerOverlays();
        tempEntries = new ArrayList<ContainerOverlayEntry>();
    }
    
    private static final class ContainerOverlayEntry
    {
        public String room;
        public String manyItems;
        public String fewItems;
    }
    
    private static final class ContainerOverlay
    {
        public String name;
        public final ArrayList<ContainerOverlayEntry> entries;
        
        private ContainerOverlay() {
            this.entries = new ArrayList<ContainerOverlayEntry>();
        }
        
        public void getEntries(final String anotherString, final ArrayList<ContainerOverlayEntry> list) {
            list.clear();
            for (int i = 0; i < this.entries.size(); ++i) {
                final ContainerOverlayEntry e = this.entries.get(i);
                if (e.room.equalsIgnoreCase(anotherString)) {
                    list.add(e);
                }
            }
        }
        
        public ContainerOverlayEntry pickRandom(final String s, final int n, final int n2, final int n3) {
            this.getEntries(s, ContainerOverlays.tempEntries);
            if (ContainerOverlays.tempEntries.isEmpty()) {
                return null;
            }
            return ContainerOverlays.tempEntries.get(LocationRNG.instance.nextInt(ContainerOverlays.tempEntries.size(), n, n2, n3));
        }
    }
}
