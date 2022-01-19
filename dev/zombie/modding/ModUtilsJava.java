// 
// Decompiled by Procyon v0.5.36
// 

package zombie.modding;

import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import java.util.UUID;

public final class ModUtilsJava
{
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }
    
    public static boolean sendItemListNet(final IsoPlayer isoPlayer, final ArrayList<InventoryItem> list, final IsoPlayer isoPlayer2, String s, final String s2) {
        if (list != null) {
            s = ((s != null) ? s : "-1");
            if (GameClient.bClient) {
                if (list.size() > 50) {
                    return false;
                }
                for (int i = 0; i < list.size(); ++i) {
                    if (!isoPlayer.getInventory().getItems().contains(list.get(i))) {
                        return false;
                    }
                }
                final GameClient instance = GameClient.instance;
                return GameClient.sendItemListNet(isoPlayer, list, isoPlayer2, s, s2);
            }
            else if (GameServer.bServer) {
                return GameServer.sendItemListNet(null, isoPlayer, list, isoPlayer2, s, s2);
            }
        }
        return false;
    }
}
