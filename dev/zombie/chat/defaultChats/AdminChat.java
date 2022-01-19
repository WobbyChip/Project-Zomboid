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

public class AdminChat extends ChatBase
{
    public AdminChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.admin, chatTab, isoPlayer);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public AdminChat(final int n, final ChatTab chatTab) {
        super(n, ChatType.admin, chatTab);
        super.setSettings(getDefaultSettings());
    }
    
    public AdminChat() {
        super(ChatType.admin);
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(Color.white);
        chatSettings.setShowAuthor(true);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        chatSettings.setAllowColors(true);
        chatSettings.setAllowFonts(true);
        chatSettings.setAllowBBcode(true);
        return chatSettings;
    }
}
