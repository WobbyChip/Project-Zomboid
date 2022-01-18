// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import zombie.chat.ChatUtility;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.chat.ChatSettings;
import zombie.network.chat.ChatType;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatTab;
import java.nio.ByteBuffer;

public class SayChat extends RangeBasedChat
{
    public SayChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.say, chatTab, isoPlayer);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public SayChat(final int n, final ChatTab chatTab) {
        super(n, ChatType.say, chatTab);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public SayChat() {
        super(ChatType.say);
        this.setSettings(getDefaultSettings());
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
        chatSettings.setAllowChatIcons(true);
        chatSettings.setAllowImages(true);
        chatSettings.setAllowFonts(false);
        chatSettings.setAllowBBcode(true);
        chatSettings.setEqualizeLineHeights(true);
        chatSettings.setRange(30.0f);
        chatSettings.setZombieAttractionRange(15.0f);
        return chatSettings;
    }
    
    public ChatMessage createInfoMessage(final String s) {
        final ChatMessage bubbleMessage = this.createBubbleMessage(s);
        bubbleMessage.setLocal(true);
        bubbleMessage.setShowInChat(false);
        return bubbleMessage;
    }
    
    public ChatMessage createCalloutMessage(final String s) {
        final ChatMessage bubbleMessage = this.createBubbleMessage(s);
        bubbleMessage.setLocal(false);
        bubbleMessage.setShouldAttractZombies(true);
        return bubbleMessage;
    }
    
    @Override
    public String getMessageTextWithPrefix(final ChatMessage chatMessage) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getMessagePrefix(chatMessage), ChatUtility.parseStringForChatLog(chatMessage.getTextWithReplacedParentheses()));
    }
}
