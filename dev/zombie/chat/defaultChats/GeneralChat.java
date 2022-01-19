// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import zombie.core.network.ByteBufferWriter;
import zombie.Lua.LuaManager;
import zombie.core.Translator;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.chat.ChatUtility;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.network.chat.ChatType;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatTab;
import java.nio.ByteBuffer;
import zombie.core.Color;
import zombie.chat.ChatBase;

public class GeneralChat extends ChatBase
{
    private boolean discordEnabled;
    private final Color discordMessageColor;
    
    public GeneralChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.general, chatTab, isoPlayer);
        this.discordEnabled = false;
        this.discordMessageColor = new Color(114, 137, 218);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public GeneralChat(final int n, final ChatTab chatTab, final boolean discordEnabled) {
        super(n, ChatType.general, chatTab);
        this.discordEnabled = false;
        this.discordMessageColor = new Color(114, 137, 218);
        this.discordEnabled = discordEnabled;
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }
    
    public GeneralChat() {
        super(ChatType.general);
        this.discordEnabled = false;
        this.discordMessageColor = new Color(114, 137, 218);
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(new Color(255, 165, 0));
        chatSettings.setShowAuthor(true);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        chatSettings.setAllowColors(true);
        chatSettings.setAllowFonts(true);
        chatSettings.setAllowBBcode(true);
        return chatSettings;
    }
    
    @Override
    public void sendMessageToChatMembers(final ChatMessage chatMessage) {
        if (this.discordEnabled) {
            final IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
            if (chatMessage.isFromDiscord()) {
                final Iterator<Short> iterator = this.members.iterator();
                while (iterator.hasNext()) {
                    this.sendMessageToPlayer(iterator.next(), chatMessage);
                }
            }
            else {
                GameServer.discordBot.sendMessage(chatMessage.getAuthor(), chatMessage.getText());
                for (final short shortValue : this.members) {
                    if (player == null || player.getOnlineID() != shortValue) {
                        this.sendMessageToPlayer(shortValue, chatMessage);
                    }
                }
            }
        }
        else {
            super.sendMessageToChatMembers(chatMessage);
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;I)Ljava/lang/String;, chatMessage, this.getID()));
    }
    
    public void sendToDiscordGeneralChatDisabled() {
        GameServer.discordBot.sendMessage("Server", Translator.getText("UI_chat_general_chat_disabled"));
    }
    
    @Override
    public String getMessagePrefix(final ChatMessage chatMessage) {
        final StringBuilder sb = new StringBuilder();
        if (chatMessage.isFromDiscord()) {
            sb.append(this.getColorTag(this.discordMessageColor));
        }
        else {
            sb.append(this.getColorTag());
        }
        sb.append(" ").append(this.getFontSizeTag()).append(" ");
        if (this.isShowTimestamp()) {
            sb.append("[").append(LuaManager.getHourMinuteJava()).append("]");
        }
        if (this.isShowTitle()) {
            sb.append("[").append(this.getTitle()).append("]");
        }
        if (this.isShowAuthor()) {
            sb.append("[").append(chatMessage.getAuthor()).append("]");
        }
        sb.append(": ");
        return sb.toString();
    }
    
    @Override
    public void packMessage(final ByteBufferWriter byteBufferWriter, final ChatMessage chatMessage) {
        super.packMessage(byteBufferWriter, chatMessage);
        byteBufferWriter.putBoolean(chatMessage.isFromDiscord());
    }
    
    @Override
    public ChatMessage unpackMessage(final ByteBuffer byteBuffer) {
        final ChatMessage unpackMessage = super.unpackMessage(byteBuffer);
        if (byteBuffer.get() == 1) {
            unpackMessage.makeFromDiscord();
        }
        return unpackMessage;
    }
}
