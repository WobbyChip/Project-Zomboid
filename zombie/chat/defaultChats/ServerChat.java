// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.Lua.LuaEventManager;
import zombie.core.network.ByteBufferWriter;
import zombie.chat.ChatManager;
import zombie.network.GameClient;
import zombie.chat.ServerChatMessage;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.chat.ChatSettings;
import zombie.network.chat.ChatType;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatTab;
import java.nio.ByteBuffer;
import zombie.chat.ChatBase;

public class ServerChat extends ChatBase
{
    public ServerChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.server, chatTab, isoPlayer);
        this.setSettings(getDefaultSettings());
    }
    
    public ServerChat(final int n, final ChatTab chatTab) {
        super(n, ChatType.server, chatTab);
        this.setSettings(getDefaultSettings());
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(new Color(0, 128, 255, 255));
        chatSettings.setShowAuthor(false);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(false);
        chatSettings.setAllowColors(true);
        chatSettings.setAllowFonts(false);
        chatSettings.setAllowBBcode(false);
        return chatSettings;
    }
    
    public ChatMessage createMessage(final String author, final String s, final boolean b) {
        final ChatMessage message = this.createMessage(s);
        message.setAuthor(author);
        if (b) {
            message.setServerAlert(true);
        }
        return message;
    }
    
    public ServerChatMessage createServerMessage(final String s, final boolean serverAlert) {
        final ServerChatMessage serverMessage = this.createServerMessage(s);
        serverMessage.setServerAlert(serverAlert);
        return serverMessage;
    }
    
    @Override
    public short getTabID() {
        if (!GameClient.bClient) {
            return super.getTabID();
        }
        return ChatManager.getInstance().getFocusTab().getID();
    }
    
    @Override
    public ChatMessage unpackMessage(final ByteBuffer byteBuffer) {
        final ChatMessage unpackMessage = super.unpackMessage(byteBuffer);
        unpackMessage.setServerAlert(byteBuffer.get() == 1);
        unpackMessage.setServerAuthor(byteBuffer.get() == 1);
        return unpackMessage;
    }
    
    @Override
    public void packMessage(final ByteBufferWriter byteBufferWriter, final ChatMessage chatMessage) {
        super.packMessage(byteBufferWriter, chatMessage);
        byteBufferWriter.putBoolean(chatMessage.isServerAlert());
        byteBufferWriter.putBoolean(chatMessage.isServerAuthor());
    }
    
    @Override
    public String getMessagePrefix(final ChatMessage chatMessage) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getChatSettingsTags());
        boolean b = false;
        if (this.isShowTitle()) {
            sb.append("[").append(this.getTitle()).append("]");
            b = true;
        }
        if (!chatMessage.isServerAuthor() && this.isShowAuthor()) {
            sb.append("[").append(chatMessage.getAuthor()).append("]");
            b = true;
        }
        if (b) {
            sb.append(": ");
        }
        return sb.toString();
    }
    
    @Override
    public String getMessageTextWithPrefix(final ChatMessage chatMessage) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getMessagePrefix(chatMessage), chatMessage.getText());
    }
    
    @Override
    public void showMessage(final ChatMessage e) {
        this.messages.add(e);
        if (this.isEnabled()) {
            LuaEventManager.triggerEvent("OnAddMessage", e, this.getTabID());
        }
    }
    
    @Override
    public void sendMessageToChatMembers(final ChatMessage chatMessage) {
        final Iterator<Short> iterator = this.members.iterator();
        while (iterator.hasNext()) {
            this.sendMessageToPlayer(iterator.next(), chatMessage);
        }
        if (Core.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;I)Ljava/lang/String;, chatMessage, this.getID()));
        }
    }
}
