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

public class ShoutChat extends RangeBasedChat
{
    public ShoutChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.shout, chatTab, isoPlayer);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public ShoutChat(final int n, final ChatTab chatTab) {
        super(n, ChatType.shout, chatTab);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public ShoutChat() {
        super(ChatType.shout);
        this.setSettings(getDefaultSettings());
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(new Color(255, 51, 51, 255));
        chatSettings.setShowAuthor(true);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        chatSettings.setAllowColors(false);
        chatSettings.setAllowFonts(false);
        chatSettings.setAllowBBcode(false);
        chatSettings.setEqualizeLineHeights(true);
        chatSettings.setRange(60.0f);
        return chatSettings;
    }
}
