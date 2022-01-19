// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.inventory.ItemPickerJava;
import zombie.characters.IsoPlayer;
import zombie.iso.TileOverlays;
import zombie.iso.ContainerOverlays;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import zombie.network.GameClient;

public final class LoadGridsquarePerformanceWorkaround
{
    public static void init(final int n, final int n2) {
        if (GameClient.bClient) {
            return;
        }
        ItemPicker.instance.init();
    }
    
    public static void LoadGridsquare(final IsoGridSquare isoGridSquare) {
        if (ItemPicker.instance.begin(isoGridSquare)) {
            final IsoObject[] array = isoGridSquare.getObjects().getElements();
            for (int size = isoGridSquare.getObjects().size(), i = 0; i < size; ++i) {
                final IsoObject isoObject = array[i];
                if (!(isoObject instanceof IsoWorldInventoryObject)) {
                    if (!GameClient.bClient) {
                        ItemPicker.instance.checkObject(isoObject);
                    }
                    if (isoObject.sprite != null && isoObject.sprite.name != null && !ContainerOverlays.instance.hasOverlays(isoObject)) {
                        TileOverlays.instance.updateTileOverlaySprite(isoObject);
                    }
                }
            }
        }
        ItemPicker.instance.end(isoGridSquare);
    }
    
    private static class ItemPicker
    {
        public static final ItemPicker instance;
        private IsoGridSquare square;
        
        public void init() {
        }
        
        public boolean begin(final IsoGridSquare square) {
            if (square.isOverlayDone()) {
                this.square = null;
                return false;
            }
            this.square = square;
            return true;
        }
        
        public void checkObject(final IsoObject isoObject) {
            final IsoSprite sprite = isoObject.getSprite();
            if (sprite == null || sprite.getName() == null) {
                return;
            }
            final ItemContainer container = isoObject.getContainer();
            if (container != null && !container.isExplored()) {
                ItemPickerJava.fillContainer(container, IsoPlayer.getInstance());
                container.setExplored(true);
                if (GameServer.bServer) {
                    GameServer.sendItemsInContainer(isoObject, container);
                }
            }
            if (container != null && container.isEmpty()) {
                return;
            }
            ItemPickerJava.updateOverlaySprite(isoObject);
        }
        
        public void end(final IsoGridSquare isoGridSquare) {
            isoGridSquare.setOverlayDone(true);
        }
        
        static {
            instance = new ItemPicker();
        }
    }
}
