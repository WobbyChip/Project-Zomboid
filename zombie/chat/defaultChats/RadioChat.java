// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import zombie.Lua.LuaManager;
import zombie.core.network.ByteBufferWriter;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIFont;
import zombie.chat.ChatMode;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.chat.ChatSettings;
import zombie.network.chat.ChatType;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatTab;
import java.nio.ByteBuffer;

public class RadioChat extends RangeBasedChat
{
    public RadioChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.radio, chatTab, isoPlayer);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
        this.customTag = "radio";
    }
    
    public RadioChat(final int n, final ChatTab chatTab) {
        super(n, ChatType.radio, chatTab);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
        this.customTag = "radio";
    }
    
    public RadioChat() {
        super(ChatType.radio);
        this.setSettings(getDefaultSettings());
        this.customTag = "radio";
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(Color.lightGray);
        chatSettings.setShowAuthor(false);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        chatSettings.setAllowColors(true);
        chatSettings.setAllowFonts(false);
        chatSettings.setAllowBBcode(true);
        chatSettings.setAllowImages(false);
        chatSettings.setAllowChatIcons(true);
        return chatSettings;
    }
    
    @Override
    public ChatMessage createMessage(final String s) {
        final ChatMessage message = super.createMessage(s);
        if (this.getMode() == ChatMode.SinglePlayer) {
            message.setOverHeadSpeech(true);
            message.setShowInChat(false);
        }
        message.setShouldAttractZombies(true);
        return message;
    }
    
    public ChatMessage createBroadcastingMessage(final String s, final int radioChannel) {
        final ChatMessage bubbleMessage = super.createBubbleMessage(s);
        bubbleMessage.setAuthor("");
        bubbleMessage.setShouldAttractZombies(false);
        bubbleMessage.setRadioChannel(radioChannel);
        return bubbleMessage;
    }
    
    public ChatMessage createStaticSoundMessage(final String s) {
        final ChatMessage bubbleMessage = super.createBubbleMessage(s);
        bubbleMessage.setAuthor("");
        bubbleMessage.setShouldAttractZombies(false);
        return bubbleMessage;
    }
    
    @Override
    protected void showInSpeechBubble(final ChatMessage chatMessage) {
        final Color color = this.getColor();
        this.getSpeechBubble().addChatLine(chatMessage.getText(), color.r, color.g, color.b, UIFont.Dialogue, this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
    }
    
    @Override
    public void showMessage(final ChatMessage chatMessage) {
        if (this.isEnabled() && chatMessage.isShowInChat() && this.hasChatTab()) {
            LuaEventManager.triggerEvent("OnAddMessage", chatMessage, this.getTabID());
        }
    }
    
    @Override
    public void sendToServer(final ChatMessage chatMessage) {
    }
    
    @Override
    public ChatMessage unpackMessage(final ByteBuffer byteBuffer) {
        final ChatMessage unpackMessage = super.unpackMessage(byteBuffer);
        unpackMessage.setRadioChannel(byteBuffer.getInt());
        unpackMessage.setOverHeadSpeech(byteBuffer.get() == 1);
        unpackMessage.setShowInChat(byteBuffer.get() == 1);
        unpackMessage.setShouldAttractZombies(byteBuffer.get() == 1);
        return unpackMessage;
    }
    
    @Override
    public void packMessage(final ByteBufferWriter byteBufferWriter, final ChatMessage chatMessage) {
        super.packMessage(byteBufferWriter, chatMessage);
        byteBufferWriter.putInt(chatMessage.getRadioChannel());
        byteBufferWriter.putBoolean(chatMessage.isOverHeadSpeech());
        byteBufferWriter.putBoolean(chatMessage.isShowInChat());
        byteBufferWriter.putBoolean(chatMessage.isShouldAttractZombies());
    }
    
    @Override
    public String getMessagePrefix(final ChatMessage chatMessage) {
        final StringBuilder sb = new StringBuilder(this.getChatSettingsTags());
        if (this.isShowTimestamp()) {
            sb.append("[").append(LuaManager.getHourMinuteJava()).append("]");
        }
        if (this.isShowTitle()) {
            sb.append("[").append(this.getTitle()).append("]");
        }
        if (this.isShowAuthor() && chatMessage.getAuthor() != null && !chatMessage.getAuthor().equals("")) {
            sb.append(" ").append(chatMessage.getAuthor()).append(" ");
        }
        else {
            sb.append(" ").append("Radio").append(" ");
        }
        sb.append(" (").append(this.getRadioChannelStr(chatMessage)).append("): ");
        return sb.toString();
    }
    
    private String getRadioChannelStr(final ChatMessage chatMessage) {
        final StringBuilder sb = new StringBuilder();
        final int radioChannel = chatMessage.getRadioChannel();
        int i;
        for (i = radioChannel % 1000; i % 10 == 0 && i != 0; i /= 10) {}
        sb.append(radioChannel / 1000).append(".").append(i).append(" MHz");
        return sb.toString();
    }
}
