// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.chat;

import java.util.Map;
import zombie.core.network.ByteBufferWriter;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.chat.defaultChats.WhisperChat;
import zombie.GameWindow;
import zombie.chat.ChatMessage;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.characters.IsoPlayer;
import zombie.network.PacketTypes;
import zombie.iso.areas.SafeHouse;
import zombie.characters.Faction;
import zombie.chat.ChatUtility;
import zombie.chat.defaultChats.ShoutChat;
import zombie.chat.defaultChats.SayChat;
import zombie.network.ServerOptions;
import zombie.core.logger.LoggerManager;
import zombie.core.Core;
import zombie.chat.ChatTab;
import zombie.core.logger.ZLogger;
import java.util.HashSet;
import zombie.chat.defaultChats.RadioChat;
import zombie.chat.defaultChats.ServerChat;
import zombie.chat.defaultChats.GeneralChat;
import zombie.chat.defaultChats.AdminChat;
import zombie.chat.defaultChats.SafehouseChat;
import zombie.chat.defaultChats.FactionChat;
import java.util.concurrent.ConcurrentHashMap;
import zombie.chat.ChatBase;
import java.util.HashMap;
import java.util.Stack;

public class ChatServer
{
    private static ChatServer instance;
    private static final Stack<Integer> availableChatsID;
    private static int lastChatId;
    private static final HashMap<ChatType, ChatBase> defaultChats;
    private static final ConcurrentHashMap<Integer, ChatBase> chats;
    private static final ConcurrentHashMap<String, FactionChat> factionChats;
    private static final ConcurrentHashMap<String, SafehouseChat> safehouseChats;
    private static AdminChat adminChat;
    private static GeneralChat generalChat;
    private static ServerChat serverChat;
    private static RadioChat radioChat;
    private static boolean inited;
    private static final HashSet<Short> players;
    private static final String logName = "chat";
    private static ZLogger logger;
    private static final HashMap<String, ChatTab> tabs;
    private static final String mainTabID = "main";
    private static final String adminTabID = "admin";
    
    public static ChatServer getInstance() {
        if (ChatServer.instance == null) {
            ChatServer.instance = new ChatServer();
        }
        return ChatServer.instance;
    }
    
    public static boolean isInited() {
        return ChatServer.inited;
    }
    
    private ChatServer() {
    }
    
    public void init() {
        if (ChatServer.inited) {
            return;
        }
        LoggerManager.createLogger("chat", Core.bDebug);
        (ChatServer.logger = LoggerManager.getLogger("chat")).write("Start chat server initialization...", "info");
        final ChatTab value = new ChatTab((short)0, "UI_chat_main_tab_title_id");
        final ChatTab value2 = new ChatTab((short)1, "UI_chat_admin_tab_title_id");
        final GeneralChat generalChat = new GeneralChat(this.getNextChatID(), value, ServerOptions.getInstance().DiscordEnable.getValue());
        final SayChat sayChat = new SayChat(this.getNextChatID(), value);
        final ShoutChat shoutChat = new ShoutChat(this.getNextChatID(), value);
        final RadioChat radioChat = new RadioChat(this.getNextChatID(), value);
        final AdminChat adminChat = new AdminChat(this.getNextChatID(), value2);
        final ServerChat serverChat = new ServerChat(this.getNextChatID(), value);
        ChatServer.chats.put(generalChat.getID(), generalChat);
        ChatServer.chats.put(sayChat.getID(), sayChat);
        ChatServer.chats.put(shoutChat.getID(), shoutChat);
        ChatServer.chats.put(radioChat.getID(), radioChat);
        ChatServer.chats.put(adminChat.getID(), adminChat);
        ChatServer.chats.put(serverChat.getID(), serverChat);
        ChatServer.defaultChats.put(generalChat.getType(), generalChat);
        ChatServer.defaultChats.put(sayChat.getType(), sayChat);
        ChatServer.defaultChats.put(shoutChat.getType(), shoutChat);
        ChatServer.defaultChats.put(serverChat.getType(), serverChat);
        ChatServer.defaultChats.put(radioChat.getType(), radioChat);
        ChatServer.tabs.put("main", value);
        ChatServer.tabs.put("admin", value2);
        ChatServer.generalChat = generalChat;
        ChatServer.adminChat = adminChat;
        ChatServer.serverChat = serverChat;
        ChatServer.radioChat = radioChat;
        ChatServer.inited = true;
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, generalChat.getID()), "info");
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, sayChat.getID()), "info");
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, shoutChat.getID()), "info");
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radioChat.getID()), "info");
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, adminChat.getID()), "info");
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ChatServer.serverChat.getID()), "info");
        ChatServer.logger.write("Chat server successfully initialized", "info");
    }
    
    public void initPlayer(final short n) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n), "info");
        synchronized (ChatServer.players) {
            if (ChatServer.players.contains(n)) {
                ChatServer.logger.write("Player already connected!", "warning");
                return;
            }
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n), "info");
        final IsoPlayer player = ChatUtility.findPlayer(n);
        final UdpConnection connection = ChatUtility.findConnection(n);
        if (connection == null || player == null) {
            ChatServer.logger.write("Player or connection is not found on server!", "error");
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (connection == null) ? "connection = null " : "", (player == null) ? "player = null" : ""), "error");
            return;
        }
        this.sendInitPlayerChatPacket(connection);
        this.addDefaultChats(n);
        ChatServer.logger.write("Player joined to default chats", "info");
        if (connection.accessLevel.equals("admin")) {
            this.joinAdminChat(n);
        }
        final Faction playerFaction = Faction.getPlayerFaction(player);
        if (playerFaction != null) {
            this.addMemberToFactionChat(playerFaction.getName(), n);
        }
        final SafeHouse hasSafehouse = SafeHouse.hasSafehouse(player);
        if (hasSafehouse != null) {
            this.addMemberToSafehouseChat(hasSafehouse.getId(), n);
        }
        PacketTypes.PacketType.PlayerConnectedToChat.doPacket(connection.startPacket());
        PacketTypes.PacketType.PlayerConnectedToChat.send(connection);
        synchronized (ChatServer.players) {
            ChatServer.players.add(n);
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, player.getUsername(), n), "info");
    }
    
    public void processMessageFromPlayerPacket(final ByteBuffer byteBuffer) {
        final int int1 = byteBuffer.getInt();
        synchronized (ChatServer.chats) {
            final ChatBase chatBase = ChatServer.chats.get(int1);
            final ChatMessage unpackMessage = chatBase.unpackMessage(byteBuffer);
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;)Ljava/lang/String;, unpackMessage), "info");
            if (!ChatUtility.chatStreamEnabled(chatBase.getType())) {
                ChatServer.logger.write("Message ignored by server because the chat disabled by server settings", "warning");
                return;
            }
            this.sendMessage(unpackMessage);
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;I)Ljava/lang/String;, unpackMessage, chatBase.getID()), "info");
        }
    }
    
    public void processPlayerStartWhisperChatPacket(final ByteBuffer byteBuffer) {
        ChatServer.logger.write("Whisper chat starting...", "info");
        if (!ChatUtility.chatStreamEnabled(ChatType.whisper)) {
            ChatServer.logger.write("Message for whisper chat is ignored because whisper chat is disabled by server settings", "info");
            return;
        }
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, readString, readString2), "info");
        final IsoPlayer player = ChatUtility.findPlayer(readString);
        final IsoPlayer player2 = ChatUtility.findPlayer(readString2);
        if (player == null) {
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString), "error");
            throw new RuntimeException("Player not found");
        }
        if (player2 == null) {
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, readString, readString2), "info");
            this.sendPlayerNotFoundMessage(ChatUtility.findConnection(player.getOnlineID()));
            return;
        }
        ChatServer.logger.write("Both players found", "info");
        final WhisperChat value = new WhisperChat(this.getNextChatID(), ChatServer.tabs.get("main"), readString, readString2);
        value.addMember(player.getOnlineID());
        value.addMember(player2.getOnlineID());
        ChatServer.chats.put(value.getID(), value);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, value.getID(), player.getUsername(), player2.getUsername()), "info");
    }
    
    private void sendPlayerNotFoundMessage(final UdpConnection udpConnection) {
        PacketTypes.PacketType.PlayerNotFound.doPacket(udpConnection.startPacket());
        PacketTypes.PacketType.PlayerNotFound.send(udpConnection);
        ChatServer.logger.write("'Player not found' packet was sent", "info");
    }
    
    public ChatMessage unpackChatMessage(final ByteBuffer byteBuffer) {
        return ChatServer.chats.get(byteBuffer.getInt()).unpackMessage(byteBuffer);
    }
    
    public void disconnectPlayer(final short n) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n), "info");
        synchronized (ChatServer.chats) {
            for (final ChatBase chatBase : ChatServer.chats.values()) {
                chatBase.removeMember(n);
                if (chatBase.getType() == ChatType.whisper) {
                    this.closeChat(chatBase.getID());
                }
            }
        }
        synchronized (ChatServer.players) {
            ChatServer.players.remove(n);
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n), "info");
    }
    
    private void closeChat(final int n) {
        synchronized (ChatServer.chats) {
            if (!ChatServer.chats.containsKey(n)) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            }
            ChatServer.chats.get(n).close();
            ChatServer.chats.remove(n);
        }
        synchronized (ChatServer.availableChatsID) {
            ChatServer.availableChatsID.push(n);
        }
    }
    
    public void joinAdminChat(final short n) {
        if (ChatServer.adminChat == null) {
            ChatServer.logger.write("Admin chat is null! Can't add player to it", "warning");
            return;
        }
        ChatServer.adminChat.addMember(n);
        ChatServer.logger.write("Player joined admin chat", "info");
    }
    
    public void leaveAdminChat(final short s) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, s), "info");
        final UdpConnection connection = ChatUtility.findConnection(s);
        if (ChatServer.adminChat == null) {
            ChatServer.logger.write("Admin chat is null. Can't leave it! ChatServer", "warning");
            return;
        }
        if (connection == null) {
            ChatServer.logger.write("Connection to player is null. Can't leave admin chat! ChatServer.leaveAdminChat", "warning");
            return;
        }
        ChatServer.adminChat.leaveMember(s);
        ChatServer.tabs.get("admin").sendRemoveTabPacket(connection);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, s), "info");
    }
    
    public FactionChat createFactionChat(final String key) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
        if (ChatServer.factionChats.containsKey(key)) {
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "warning");
            return ChatServer.factionChats.get(key);
        }
        final FactionChat factionChat = new FactionChat(this.getNextChatID(), ChatServer.tabs.get("main"));
        ChatServer.chats.put(factionChat.getID(), factionChat);
        ChatServer.factionChats.put(key, factionChat);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
        return factionChat;
    }
    
    public SafehouseChat createSafehouseChat(final String key) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
        if (ChatServer.safehouseChats.containsKey(key)) {
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "warning");
            return ChatServer.safehouseChats.get(key);
        }
        final SafehouseChat safehouseChat = new SafehouseChat(this.getNextChatID(), ChatServer.tabs.get("main"));
        ChatServer.chats.put(safehouseChat.getID(), safehouseChat);
        ChatServer.safehouseChats.put(key, safehouseChat);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
        return safehouseChat;
    }
    
    public void removeFactionChat(final String key) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
        final int id;
        synchronized (ChatServer.factionChats) {
            if (!ChatServer.factionChats.containsKey(key)) {
                final String message = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key);
                ChatServer.logger.write(message, "error");
                final RuntimeException ex = new RuntimeException(message);
                ChatServer.logger.write(ex);
                throw ex;
            }
            id = ChatServer.factionChats.get(key).getID();
            ChatServer.factionChats.remove(key);
        }
        this.closeChat(id);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
    }
    
    public void removeSafehouseChat(final String key) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
        final int id;
        synchronized (ChatServer.safehouseChats) {
            if (!ChatServer.safehouseChats.containsKey(key)) {
                final String message = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key);
                ChatServer.logger.write(message, "error");
                final RuntimeException ex = new RuntimeException(message);
                ChatServer.logger.write(ex);
                throw ex;
            }
            id = ChatServer.safehouseChats.get(key).getID();
            ChatServer.safehouseChats.remove(key);
        }
        this.closeChat(id);
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key), "info");
    }
    
    public void syncFactionChatMembers(final String s, final String e, final ArrayList<String> c) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
        if (s == null || e == null || c == null) {
            ChatServer.logger.write("Faction name or faction owner or players is null", "warning");
            return;
        }
        synchronized (ChatServer.factionChats) {
            if (!ChatServer.factionChats.containsKey(s)) {
                ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "warning");
                return;
            }
            final ArrayList<String> list = new ArrayList<String>(c);
            list.add(e);
            final FactionChat factionChat = ChatServer.factionChats.get(s);
            factionChat.syncMembersByUsernames(list);
            final StringBuilder sb = new StringBuilder("These members were added: ");
            final Iterator<Short> iterator = factionChat.getJustAddedMembers().iterator();
            while (iterator.hasNext()) {
                sb.append("'").append(ChatUtility.findPlayerName(iterator.next())).append("', ");
            }
            sb.append(". These members were removed: ");
            final Iterator<Short> iterator2 = factionChat.getJustRemovedMembers().iterator();
            while (iterator2.hasNext()) {
                sb.append("'").append(ChatUtility.findPlayerName(iterator2.next())).append("', ");
            }
            ChatServer.logger.write(sb.toString(), "info");
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
    }
    
    public void syncSafehouseChatMembers(final String s, final String e, final ArrayList<String> c) {
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
        if (s == null || e == null || c == null) {
            ChatServer.logger.write("Safehouse name or Safehouse owner or players is null", "warning");
            return;
        }
        synchronized (ChatServer.safehouseChats) {
            if (!ChatServer.safehouseChats.containsKey(s)) {
                ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "warning");
                return;
            }
            final ArrayList<String> list = new ArrayList<String>(c);
            list.add(e);
            final SafehouseChat safehouseChat = ChatServer.safehouseChats.get(s);
            safehouseChat.syncMembersByUsernames(list);
            final StringBuilder sb = new StringBuilder("These members were added: ");
            final Iterator<Short> iterator = safehouseChat.getJustAddedMembers().iterator();
            while (iterator.hasNext()) {
                sb.append("'").append(ChatUtility.findPlayerName(iterator.next())).append("', ");
            }
            sb.append("These members were removed: ");
            final Iterator<Short> iterator2 = safehouseChat.getJustRemovedMembers().iterator();
            while (iterator2.hasNext()) {
                sb.append("'").append(ChatUtility.findPlayerName(iterator2.next())).append("', ");
            }
            ChatServer.logger.write(sb.toString(), "info");
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
    }
    
    private void addMemberToSafehouseChat(final String s, final short n) {
        if (!ChatServer.safehouseChats.containsKey(s)) {
            ChatServer.logger.write("Safehouse chat is not initialized!", "warning");
            return;
        }
        synchronized (ChatServer.safehouseChats) {
            ChatServer.safehouseChats.get(s).addMember(n);
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
    }
    
    private void addMemberToFactionChat(final String s, final short n) {
        if (!ChatServer.factionChats.containsKey(s)) {
            ChatServer.logger.write("Faction chat is not initialized!", "warning");
            return;
        }
        synchronized (ChatServer.factionChats) {
            ChatServer.factionChats.get(s).addMember(n);
        }
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
    }
    
    public void sendServerAlertMessageToServerChat(final String s, final String s2) {
        ChatServer.serverChat.sendMessageToChatMembers(ChatServer.serverChat.createMessage(s, s2, true));
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
    }
    
    public void sendServerAlertMessageToServerChat(final String s) {
        ChatServer.serverChat.sendMessageToChatMembers(ChatServer.serverChat.createServerMessage(s, true));
        ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public ChatMessage createRadiostationMessage(final String s, final int n) {
        return ChatServer.radioChat.createBroadcastingMessage(s, n);
    }
    
    public void sendMessageToServerChat(final UdpConnection udpConnection, final String s) {
        ChatServer.serverChat.sendMessageToPlayer(udpConnection, ChatServer.serverChat.createServerMessage(s, false));
    }
    
    public void sendMessageToServerChat(final String s) {
        ChatServer.serverChat.sendMessageToChatMembers(ChatServer.serverChat.createServerMessage(s, false));
    }
    
    public void sendMessageFromDiscordToGeneralChat(final String author, final String s) {
        if (author != null && s != null) {
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, author));
        }
        final ChatMessage message = ChatServer.generalChat.createMessage(s);
        message.makeFromDiscord();
        message.setAuthor(author);
        if (ChatUtility.chatStreamEnabled(ChatType.general)) {
            this.sendMessage(message);
            ChatServer.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        else {
            ChatServer.generalChat.sendToDiscordGeneralChatDisabled();
            ChatServer.logger.write("General chat disabled so error message sent to discord", "warning");
        }
    }
    
    private int getNextChatID() {
        synchronized (ChatServer.availableChatsID) {
            if (ChatServer.availableChatsID.isEmpty()) {
                ++ChatServer.lastChatId;
                ChatServer.availableChatsID.push(ChatServer.lastChatId);
            }
            return ChatServer.availableChatsID.pop();
        }
    }
    
    private void sendMessage(final ChatMessage chatMessage) {
        synchronized (ChatServer.chats) {
            if (!ChatServer.chats.containsKey(chatMessage.getChatID())) {
                return;
            }
            ChatServer.chats.get(chatMessage.getChatID()).sendMessageToChatMembers(chatMessage);
        }
    }
    
    private void sendInitPlayerChatPacket(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.InitPlayerChat.doPacket(startPacket);
        startPacket.putShort((short)ChatServer.tabs.size());
        for (final ChatTab chatTab : ChatServer.tabs.values()) {
            startPacket.putShort(chatTab.getID());
            startPacket.putUTF(chatTab.getTitleID());
        }
        PacketTypes.PacketType.InitPlayerChat.send(udpConnection);
    }
    
    private void addDefaultChats(final short n) {
        final Iterator<Map.Entry<ChatType, ChatBase>> iterator = ChatServer.defaultChats.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().addMember(n);
        }
    }
    
    public void sendMessageToAdminChat(final String s) {
        ChatServer.adminChat.sendMessageToChatMembers(ChatServer.adminChat.createServerMessage(s));
    }
    
    static {
        ChatServer.instance = null;
        ChatServer.lastChatId = -1;
        ChatServer.adminChat = null;
        ChatServer.generalChat = null;
        ChatServer.serverChat = null;
        ChatServer.radioChat = null;
        ChatServer.inited = false;
        availableChatsID = new Stack<Integer>();
        defaultChats = new HashMap<ChatType, ChatBase>();
        chats = new ConcurrentHashMap<Integer, ChatBase>();
        factionChats = new ConcurrentHashMap<String, FactionChat>();
        safehouseChats = new ConcurrentHashMap<String, SafehouseChat>();
        tabs = new HashMap<String, ChatTab>();
        players = new HashSet<Short>();
    }
}
