// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.network.PacketTypes;
import zombie.Lua.LuaManager;
import zombie.Lua.LuaEventManager;
import java.time.LocalDateTime;
import zombie.GameWindow;
import zombie.core.network.ByteBufferWriter;
import java.util.Iterator;
import java.util.Collection;
import zombie.core.Translator;
import zombie.core.Color;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.network.GameClient;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;
import java.util.ArrayList;
import zombie.network.chat.ChatType;

public abstract class ChatBase
{
    private static final int ID_NOT_SET = -29048394;
    private int id;
    private final String titleID;
    private final ChatType type;
    private ChatSettings settings;
    private boolean customSettings;
    private ChatTab chatTab;
    private String translatedTitle;
    protected final ArrayList<Short> members;
    private final ArrayList<Short> justAddedMembers;
    private final ArrayList<Short> justRemovedMembers;
    protected final ArrayList<ChatMessage> messages;
    private UdpConnection serverConnection;
    private ChatMode mode;
    private IsoPlayer chatOwner;
    private final Lock memberLock;
    
    protected ChatBase(final ChatType type) {
        this.customSettings = false;
        this.chatTab = null;
        this.justAddedMembers = new ArrayList<Short>();
        this.justRemovedMembers = new ArrayList<Short>();
        this.memberLock = new ReentrantLock();
        this.settings = new ChatSettings();
        this.customSettings = false;
        this.messages = new ArrayList<ChatMessage>();
        this.id = -29048394;
        this.titleID = type.getTitleID();
        this.type = type;
        this.members = new ArrayList<Short>();
        this.mode = ChatMode.SinglePlayer;
        this.serverConnection = null;
        this.chatOwner = IsoPlayer.getInstance();
    }
    
    public ChatBase(final ByteBuffer byteBuffer, final ChatType chatType, final ChatTab chatTab, final IsoPlayer chatOwner) {
        this(chatType);
        this.id = byteBuffer.getInt();
        this.customSettings = (byteBuffer.get() == 1);
        if (this.customSettings) {
            this.settings = new ChatSettings(byteBuffer);
        }
        this.chatTab = chatTab;
        this.mode = ChatMode.ClientMultiPlayer;
        this.serverConnection = GameClient.connection;
        this.chatOwner = chatOwner;
    }
    
    public ChatBase(final int id, final ChatType chatType, final ChatTab chatTab) {
        this(chatType);
        this.id = id;
        this.chatTab = chatTab;
        this.mode = ChatMode.ServerMultiPlayer;
    }
    
    public boolean isEnabled() {
        return ChatUtility.chatStreamEnabled(this.type);
    }
    
    protected String getChatOwnerName() {
        if (this.chatOwner == null) {
            if (this.mode != ChatMode.ServerMultiPlayer) {
                if (Core.bDebug) {
                    throw new NullPointerException("chat owner is null but name quired");
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/network/chat/ChatType;)Ljava/lang/String;, this.getType()));
            }
            return "";
        }
        return this.chatOwner.username;
    }
    
    protected IsoPlayer getChatOwner() {
        if (this.chatOwner != null || this.mode == ChatMode.ServerMultiPlayer) {
            return this.chatOwner;
        }
        if (Core.bDebug) {
            throw new NullPointerException("chat owner is null");
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/network/chat/ChatType;)Ljava/lang/String;, this.getType()));
        return null;
    }
    
    public ChatMode getMode() {
        return this.mode;
    }
    
    public ChatType getType() {
        return this.type;
    }
    
    public int getID() {
        return this.id;
    }
    
    public String getTitleID() {
        return this.titleID;
    }
    
    public Color getColor() {
        return this.settings.getFontColor();
    }
    
    public short getTabID() {
        return this.chatTab.getID();
    }
    
    public float getRange() {
        return this.settings.getRange();
    }
    
    public boolean isSendingToRadio() {
        return false;
    }
    
    public float getZombieAttractionRange() {
        return this.settings.getZombieAttractionRange();
    }
    
    public void setSettings(final ChatSettings settings) {
        this.settings = settings;
        this.customSettings = true;
    }
    
    public void setFontSize(final String s) {
        this.settings.setFontSize(s.toLowerCase());
    }
    
    public void setShowTimestamp(final boolean showTimestamp) {
        this.settings.setShowTimestamp(showTimestamp);
    }
    
    public void setShowTitle(final boolean showChatTitle) {
        this.settings.setShowChatTitle(showChatTitle);
    }
    
    protected boolean isCustomSettings() {
        return this.customSettings;
    }
    
    protected boolean isAllowImages() {
        return this.settings.isAllowImages();
    }
    
    protected boolean isAllowChatIcons() {
        return this.settings.isAllowChatIcons();
    }
    
    protected boolean isAllowColors() {
        return this.settings.isAllowColors();
    }
    
    protected boolean isAllowFonts() {
        return this.settings.isAllowFonts();
    }
    
    protected boolean isAllowBBcode() {
        return this.settings.isAllowBBcode();
    }
    
    protected boolean isEqualizeLineHeights() {
        return this.settings.isEqualizeLineHeights();
    }
    
    protected boolean isShowAuthor() {
        return this.settings.isShowAuthor();
    }
    
    protected boolean isShowTimestamp() {
        return this.settings.isShowTimestamp();
    }
    
    protected boolean isShowTitle() {
        return this.settings.isShowChatTitle();
    }
    
    protected String getFontSize() {
        return this.settings.getFontSize().toString();
    }
    
    protected String getTitle() {
        if (this.translatedTitle == null) {
            this.translatedTitle = Translator.getText(this.titleID);
        }
        return this.translatedTitle;
    }
    
    public void close() {
        synchronized (this.memberLock) {
            final Iterator<Short> iterator = new ArrayList<Short>(this.members).iterator();
            while (iterator.hasNext()) {
                this.leaveMember(iterator.next());
            }
            this.members.clear();
        }
    }
    
    protected void packChat(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.type.getValue());
        byteBufferWriter.putShort(this.getTabID());
        byteBufferWriter.putInt(this.id);
        byteBufferWriter.putBoolean(this.customSettings);
        if (this.customSettings) {
            this.settings.pack(byteBufferWriter);
        }
    }
    
    public ChatMessage unpackMessage(final ByteBuffer byteBuffer) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final ChatMessage message = this.createMessage(GameWindow.ReadString(byteBuffer));
        message.setAuthor(readString);
        return message;
    }
    
    public void packMessage(final ByteBufferWriter byteBufferWriter, final ChatMessage chatMessage) {
        byteBufferWriter.putInt(this.id);
        byteBufferWriter.putUTF(chatMessage.getAuthor());
        byteBufferWriter.putUTF(chatMessage.getText());
    }
    
    public ChatMessage createMessage(final String s) {
        return this.createMessage(this.getChatOwnerName(), s);
    }
    
    private ChatMessage createMessage(final String author, final String s) {
        final ChatMessage chatMessage = new ChatMessage(this, s);
        chatMessage.setAuthor(author);
        chatMessage.setServerAuthor(false);
        return chatMessage;
    }
    
    public ServerChatMessage createServerMessage(final String s) {
        final ServerChatMessage serverChatMessage = new ServerChatMessage(this, s);
        serverChatMessage.setServerAuthor(true);
        return serverChatMessage;
    }
    
    public void showMessage(final String s, final String author) {
        final ChatMessage chatMessage = new ChatMessage(this, LocalDateTime.now(), s);
        chatMessage.setAuthor(author);
        this.showMessage(chatMessage);
    }
    
    public void showMessage(final ChatMessage e) {
        this.messages.add(e);
        if (this.isEnabled() && e.isShowInChat() && this.chatTab != null) {
            LuaEventManager.triggerEvent("OnAddMessage", e, this.getTabID());
        }
    }
    
    public String getMessageTextWithPrefix(final ChatMessage chatMessage) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getMessagePrefix(chatMessage), chatMessage.getTextWithReplacedParentheses());
    }
    
    public void sendMessageToChatMembers(final ChatMessage chatMessage) {
        final IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
        if (player == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, chatMessage.getAuthor()));
            return;
        }
        synchronized (this.memberLock) {
            for (final short shortValue : this.members) {
                if (ChatUtility.findPlayer(shortValue) != null) {
                    if (player.getOnlineID() == shortValue) {
                        continue;
                    }
                    this.sendMessageToPlayer(shortValue, chatMessage);
                }
            }
        }
        if (Core.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;I)Ljava/lang/String;, chatMessage, this.getID()));
        }
    }
    
    public void sendMessageToChatMembers(final ServerChatMessage serverChatMessage) {
        synchronized (this.memberLock) {
            for (final short shortValue : this.members) {
                if (ChatUtility.findPlayer(shortValue) == null) {
                    continue;
                }
                this.sendMessageToPlayer(shortValue, serverChatMessage);
            }
        }
        if (Core.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ServerChatMessage;I)Ljava/lang/String;, serverChatMessage, this.getID()));
        }
    }
    
    public void sendMessageToPlayer(final UdpConnection udpConnection, final ChatMessage chatMessage) {
        synchronized (this.memberLock) {
            boolean contains = false;
            for (final Short value : udpConnection.playerIDs) {
                if (contains) {
                    break;
                }
                contains = this.members.contains(value);
            }
            if (!contains) {
                throw new RuntimeException("Passed connection didn't contained member of chat");
            }
            this.sendChatMessageToPlayer(udpConnection, chatMessage);
        }
    }
    
    public void sendMessageToPlayer(final short n, final ChatMessage chatMessage) {
        final UdpConnection connection = ChatUtility.findConnection(n);
        if (connection == null) {
            return;
        }
        this.sendChatMessageToPlayer(connection, chatMessage);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;SI)Ljava/lang/String;, chatMessage, n, this.getID()));
    }
    
    public String getMessagePrefix(final ChatMessage chatMessage) {
        final StringBuilder sb = new StringBuilder(this.getChatSettingsTags());
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
    
    protected String getColorTag() {
        return this.getColorTag(this.getColor());
    }
    
    protected String getColorTag(final Color color) {
        return invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, color.r, color.g, color.b);
    }
    
    protected String getFontSizeTag() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.settings.getFontSize());
    }
    
    protected String getChatSettingsTags() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getColorTag(), this.getFontSizeTag());
    }
    
    public void addMember(final short s) {
        synchronized (this.memberLock) {
            if (!this.hasMember(s)) {
                this.members.add(s);
                this.justAddedMembers.add(s);
                final UdpConnection connection = ChatUtility.findConnection(s);
                if (connection != null) {
                    this.sendPlayerJoinChatPacket(connection);
                    this.chatTab.sendAddTabPacket(connection);
                }
                else if (Core.bDebug) {
                    throw new RuntimeException("Connection should exist!");
                }
            }
        }
    }
    
    public void leaveMember(final Short n) {
        synchronized (this.memberLock) {
            if (this.hasMember(n)) {
                this.justRemovedMembers.add(n);
                final UdpConnection connection = ChatUtility.findConnection(n);
                if (connection != null) {
                    this.sendPlayerLeaveChatPacket(connection);
                }
                this.members.remove(n);
            }
        }
    }
    
    private boolean hasMember(final Short o) {
        return this.members.contains(o);
    }
    
    public void removeMember(final Short o) {
        synchronized (this.memberLock) {
            if (this.hasMember(o)) {
                this.members.remove(o);
            }
        }
    }
    
    public void syncMembersByUsernames(final ArrayList<String> list) {
        synchronized (this.memberLock) {
            this.justAddedMembers.clear();
            this.justRemovedMembers.clear();
            final ArrayList<Short> list2 = new ArrayList<Short>(list.size());
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                final IsoPlayer player = ChatUtility.findPlayer(iterator.next());
                if (player != null) {
                    list2.add(player.getOnlineID());
                }
            }
            this.syncMembers(list2);
        }
    }
    
    public ArrayList<Short> getJustAddedMembers() {
        synchronized (this.memberLock) {
            return this.justAddedMembers;
        }
    }
    
    public ArrayList<Short> getJustRemovedMembers() {
        synchronized (this.memberLock) {
            return this.justRemovedMembers;
        }
    }
    
    private void syncMembers(final ArrayList<Short> list) {
        final Iterator<Short> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addMember(iterator.next());
        }
        final ArrayList<Short> list2 = new ArrayList<Short>();
        synchronized (this.memberLock) {
            for (final Short n : this.members) {
                if (!list.contains(n)) {
                    list2.add(n);
                }
            }
            final Iterator<Short> iterator3 = list2.iterator();
            while (iterator3.hasNext()) {
                this.leaveMember(iterator3.next());
            }
        }
    }
    
    public void sendPlayerJoinChatPacket(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.PlayerJoinChat.doPacket(startPacket);
        this.packChat(startPacket);
        PacketTypes.PacketType.PlayerJoinChat.send(udpConnection);
    }
    
    public void sendPlayerLeaveChatPacket(final short n) {
        this.sendPlayerLeaveChatPacket(ChatUtility.findConnection(n));
    }
    
    public void sendPlayerLeaveChatPacket(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.PlayerLeaveChat.doPacket(startPacket);
        startPacket.putInt(this.getID());
        startPacket.putInt(this.getType().getValue());
        PacketTypes.PacketType.PlayerLeaveChat.send(udpConnection);
    }
    
    public void sendToServer(final ChatMessage chatMessage) {
        if (this.serverConnection == null) {
            DebugLog.log("Connection to server is null in client chat");
        }
        this.sendChatMessageFromPlayer(this.serverConnection, chatMessage);
    }
    
    private void sendChatMessageToPlayer(final UdpConnection udpConnection, final ChatMessage chatMessage) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ChatMessageToPlayer.doPacket(startPacket);
        this.packMessage(startPacket, chatMessage);
        PacketTypes.PacketType.ChatMessageToPlayer.send(udpConnection);
    }
    
    private void sendChatMessageFromPlayer(final UdpConnection udpConnection, final ChatMessage chatMessage) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ChatMessageFromPlayer.doPacket(startPacket);
        this.packMessage(startPacket, chatMessage);
        PacketTypes.PacketType.ChatMessageFromPlayer.send(udpConnection);
    }
    
    protected boolean hasChatTab() {
        return this.chatTab != null;
    }
}
