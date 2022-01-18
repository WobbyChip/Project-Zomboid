// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import zombie.core.Color;
import zombie.chat.ChatSettings;
import zombie.network.chat.ChatType;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatTab;
import java.nio.ByteBuffer;
import zombie.chat.ChatBase;

public class SafehouseChat extends ChatBase
{
    public SafehouseChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.safehouse, chatTab, isoPlayer);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public SafehouseChat(final int n, final ChatTab chatTab) {
        super(n, ChatType.safehouse, chatTab);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(Color.lightGreen);
        chatSettings.setShowAuthor(true);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        return chatSettings;
    }
}
