// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.inventory.types.Radio;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.iso.areas.SafeHouse;
import zombie.characters.Faction;
import java.util.concurrent.TimeoutException;
import zombie.core.Translator;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.network.chat.ChatType;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import zombie.network.GameClient;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import java.util.concurrent.locks.ReentrantLock;
import zombie.core.logger.ZLogger;
import zombie.chat.defaultChats.ServerChat;
import zombie.chat.defaultChats.AdminChat;
import zombie.chat.defaultChats.RadioChat;
import zombie.chat.defaultChats.SafehouseChat;
import zombie.chat.defaultChats.FactionChat;
import zombie.chat.defaultChats.ShoutChat;
import zombie.chat.defaultChats.SayChat;
import zombie.chat.defaultChats.GeneralChat;
import zombie.characters.IsoPlayer;
import java.util.concurrent.locks.Lock;
import zombie.chat.defaultChats.WhisperChat;
import java.util.HashMap;
import zombie.core.raknet.UdpConnection;

public class ChatManager
{
    private static ChatManager instance;
    private UdpConnection serverConnection;
    private volatile HashMap<Integer, ChatBase> mpChats;
    private volatile HashMap<String, WhisperChat> whisperChats;
    private volatile WhisperChat.ChatStatus pmChatStatus;
    private final Lock whisperChatLocker;
    private final HashMap<Short, ChatTab> tabs;
    private ChatTab focusTab;
    private IsoPlayer player;
    private String myNickname;
    private boolean singlePlayerMode;
    private GeneralChat generalChat;
    private SayChat sayChat;
    private ShoutChat shoutChat;
    private FactionChat factionChat;
    private SafehouseChat safehouseChat;
    private RadioChat radioChat;
    private AdminChat adminChat;
    private ServerChat serverChat;
    private Stage chatManagerStage;
    private static volatile ZLogger logger;
    private static final String logNamePrefix = "client chat";
    
    private ChatManager() {
        this.serverConnection = null;
        this.pmChatStatus = WhisperChat.ChatStatus.None;
        this.whisperChatLocker = new ReentrantLock();
        this.singlePlayerMode = false;
        this.generalChat = null;
        this.sayChat = null;
        this.shoutChat = null;
        this.factionChat = null;
        this.safehouseChat = null;
        this.radioChat = null;
        this.adminChat = null;
        this.serverChat = null;
        this.chatManagerStage = Stage.notStarted;
        this.mpChats = new HashMap<Integer, ChatBase>();
        this.tabs = new HashMap<Short, ChatTab>();
        this.whisperChats = new HashMap<String, WhisperChat>();
    }
    
    public static ChatManager getInstance() {
        if (ChatManager.instance == null) {
            ChatManager.instance = new ChatManager();
        }
        return ChatManager.instance;
    }
    
    public boolean isSinglePlayerMode() {
        return this.singlePlayerMode;
    }
    
    public boolean isWorking() {
        return this.chatManagerStage == Stage.working;
    }
    
    public void init(final boolean singlePlayerMode, final IsoPlayer player) {
        LoggerManager.init();
        LoggerManager.createLogger(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getDisplayName()), Core.bDebug);
        (ChatManager.logger = LoggerManager.getLogger(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getDisplayName()))).write("Init chat system...", "info");
        ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, singlePlayerMode ? "single player" : "multiplayer"), "info");
        ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getDisplayName()), "info");
        this.chatManagerStage = Stage.starting;
        this.singlePlayerMode = singlePlayerMode;
        this.generalChat = null;
        this.sayChat = null;
        this.shoutChat = null;
        this.factionChat = null;
        this.safehouseChat = null;
        this.radioChat = null;
        this.adminChat = null;
        this.serverChat = null;
        this.mpChats.clear();
        this.tabs.clear();
        this.focusTab = null;
        this.whisperChats.clear();
        this.player = player;
        this.myNickname = this.player.username;
        if (singlePlayerMode) {
            this.serverConnection = null;
            (this.sayChat = new SayChat()).Init();
            this.generalChat = new GeneralChat();
            (this.shoutChat = new ShoutChat()).Init();
            (this.radioChat = new RadioChat()).Init();
            this.adminChat = new AdminChat();
        }
        else {
            this.serverConnection = GameClient.connection;
            LuaEventManager.triggerEvent("OnChatWindowInit");
        }
    }
    
    public void processInitPlayerChatPacket(final ByteBuffer byteBuffer) {
        this.init(false, IsoPlayer.getInstance());
        for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
            final ChatTab value = new ChatTab(byteBuffer.getShort(), GameWindow.ReadString(byteBuffer));
            this.tabs.put(value.getID(), value);
        }
        this.addTab((short)0);
        this.focusOnTab(this.tabs.get(0).getID());
        LuaEventManager.triggerEvent("OnSetDefaultTab", this.tabs.get(0).getTitle());
    }
    
    public void setFullyConnected() {
        this.chatManagerStage = Stage.working;
    }
    
    public void processAddTabPacket(final ByteBuffer byteBuffer) {
        this.addTab(byteBuffer.getShort());
    }
    
    public void processRemoveTabPacket(final ByteBuffer byteBuffer) {
        this.removeTab(byteBuffer.getShort());
    }
    
    public void processJoinChatPacket(final ByteBuffer byteBuffer) {
        final ChatType value = ChatType.valueOf(byteBuffer.getInt());
        final ChatTab chatTab = this.tabs.get(byteBuffer.getShort());
        ChatBase value2 = null;
        switch (value) {
            case general: {
                this.generalChat = new GeneralChat(byteBuffer, chatTab, this.player);
                value2 = this.generalChat;
                break;
            }
            case say: {
                (this.sayChat = new SayChat(byteBuffer, chatTab, this.player)).Init();
                value2 = this.sayChat;
                break;
            }
            case shout: {
                (this.shoutChat = new ShoutChat(byteBuffer, chatTab, this.player)).Init();
                value2 = this.shoutChat;
                break;
            }
            case whisper: {
                try {
                    this.whisperChatLocker.lock();
                    final WhisperChat value3 = new WhisperChat(byteBuffer, chatTab, this.player);
                    value3.init();
                    this.whisperChats.put(value3.getCompanionName(), value3);
                    value2 = value3;
                }
                finally {
                    this.whisperChatLocker.unlock();
                }
                break;
            }
            case faction: {
                this.factionChat = new FactionChat(byteBuffer, chatTab, this.player);
                value2 = this.factionChat;
                break;
            }
            case safehouse: {
                this.safehouseChat = new SafehouseChat(byteBuffer, chatTab, this.player);
                value2 = this.safehouseChat;
                break;
            }
            case radio: {
                (this.radioChat = new RadioChat(byteBuffer, chatTab, this.player)).Init();
                value2 = this.radioChat;
                break;
            }
            case admin: {
                this.adminChat = new AdminChat(byteBuffer, chatTab, this.player);
                value2 = this.adminChat;
                break;
            }
            case server: {
                this.serverChat = new ServerChat(byteBuffer, chatTab, this.player);
                value2 = this.serverChat;
                break;
            }
            default: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value.toString()));
                return;
            }
        }
        this.mpChats.put(value2.getID(), value2);
        value2.setFontSize(Core.getInstance().getOptionChatFontSize());
        value2.setShowTimestamp(Core.getInstance().isOptionShowChatTimestamp());
        value2.setShowTitle(Core.getInstance().isOptionShowChatTitle());
    }
    
    public void processLeaveChatPacket(final ByteBuffer byteBuffer) {
        final Integer value = byteBuffer.getInt();
        final ChatType value2 = ChatType.valueOf(byteBuffer.getInt());
        switch (value2) {
            case general:
            case say:
            case shout:
            case radio:
            case server: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value2.toString()));
                break;
            }
            case admin: {
                this.mpChats.remove(value);
                this.removeTab(this.adminChat.getTabID());
                this.adminChat = null;
                DebugLog.log("You leaved admin chat");
                break;
            }
            case faction: {
                this.mpChats.remove(value);
                this.factionChat = null;
                DebugLog.log("You leaved faction chat");
                break;
            }
            case whisper: {
                this.whisperChats.remove(((WhisperChat)this.mpChats.get(value)).getCompanionName());
                this.mpChats.remove(value);
                break;
            }
            case safehouse: {
                this.mpChats.remove(value);
                this.safehouseChat = null;
                DebugLog.log("You leaved safehouse chat");
                break;
            }
            default: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value2.toString()));
                break;
            }
        }
    }
    
    public void processPlayerNotFound() {
        try {
            this.whisperChatLocker.lock();
            this.pmChatStatus = WhisperChat.ChatStatus.PlayerNotFound;
            ChatManager.logger.write("Got player not found packet", "info");
        }
        finally {
            this.whisperChatLocker.unlock();
        }
    }
    
    public ChatMessage unpackMessage(final ByteBuffer byteBuffer) {
        return this.mpChats.get(byteBuffer.getInt()).unpackMessage(byteBuffer);
    }
    
    public void processChatMessagePacket(final ByteBuffer byteBuffer) {
        final ChatMessage unpackMessage = this.unpackMessage(byteBuffer);
        final ChatBase chat = unpackMessage.getChat();
        if (ChatUtility.chatStreamEnabled(chat.getType())) {
            chat.showMessage(unpackMessage);
            ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Lzombie/chat/ChatMessage;)Ljava/lang/String;, unpackMessage), "info");
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/network/chat/ChatType;)Ljava/lang/String;, unpackMessage.getText(), chat.getType()));
            ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/network/chat/ChatType;)Ljava/lang/String;, unpackMessage.getText(), chat.getType()), "warning");
        }
    }
    
    public void updateChatSettings(final String s, final boolean b, final boolean b2) {
        Core.getInstance().setOptionChatFontSize(s);
        Core.getInstance().setOptionShowChatTimestamp(b);
        Core.getInstance().setOptionShowChatTitle(b2);
        for (final ChatBase chatBase : this.mpChats.values()) {
            chatBase.setFontSize(s);
            chatBase.setShowTimestamp(b);
            chatBase.setShowTitle(b2);
        }
    }
    
    public void showInfoMessage(final String s) {
        this.sayChat.showMessage(this.sayChat.createInfoMessage(s));
    }
    
    public void showInfoMessage(final String author, final String s) {
        if (this.sayChat == null) {
            return;
        }
        final ChatMessage infoMessage = this.sayChat.createInfoMessage(s);
        infoMessage.setAuthor(author);
        this.sayChat.showMessage(infoMessage);
    }
    
    public void sendMessageToChat(final String author, final ChatType chatType, String trim) {
        trim = trim.trim();
        if (trim.isEmpty()) {
            return;
        }
        final ChatBase chat = this.getChat(chatType);
        if (chat != null) {
            final ChatMessage message = chat.createMessage(trim);
            message.setAuthor(author);
            this.sendMessageToChat(chat, message);
            return;
        }
        if (Core.bDebug) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lzombie/network/chat/ChatType;)Ljava/lang/String;, chatType));
        }
        this.showChatDisabledMessage(chatType);
    }
    
    public void sendMessageToChat(final ChatType chatType, final String s) {
        this.sendMessageToChat(this.player.getUsername(), chatType, s);
    }
    
    public synchronized void sendWhisperMessage(final String s, final String s2) {
        ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s), "info");
        if (ChatUtility.chatStreamEnabled(ChatType.whisper)) {
            if (s == null || s.equalsIgnoreCase(this.myNickname)) {
                ChatManager.logger.write("Message can't be send to yourself");
                this.showServerChatMessage(Translator.getText("UI_chat_whisper_message_to_yourself_error"));
                return;
            }
            WhisperChat whisperChat;
            try {
                whisperChat = this.getWhisperChat(s);
                this.pmChatStatus = WhisperChat.ChatStatus.None;
                if (whisperChat == null) {
                    this.showServerChatMessage(Translator.getText("UI_chat_whisper_player_not_found_error", s));
                    return;
                }
            }
            catch (TimeoutException ex) {
                ChatManager.logger.write("Whisper chat is not created by timeout. See server chat logs", "error");
                ex.printStackTrace();
                return;
            }
            this.sendMessageToChat(whisperChat, whisperChat.createMessage(s2));
        }
        else {
            ChatManager.logger.write("Whisper chat is disabled", "info");
            this.showChatDisabledMessage(ChatType.whisper);
        }
    }
    
    public Boolean isPlayerCanUseChat(final ChatType chatType) {
        if (!ChatUtility.chatStreamEnabled(chatType)) {
            return false;
        }
        switch (chatType) {
            case radio: {
                return this.isPlayerCanUseRadioChat();
            }
            case admin: {
                return this.player.getAccessLevel().equalsIgnoreCase("admin");
            }
            case faction: {
                return Faction.isAlreadyInFaction(this.player);
            }
            case safehouse: {
                return SafeHouse.hasSafehouse(this.player) != null;
            }
            default: {
                return true;
            }
        }
    }
    
    public void focusOnTab(final Short n) {
        for (final ChatTab focusTab : this.tabs.values()) {
            if (focusTab.getID() == n) {
                this.focusTab = focusTab;
                return;
            }
        }
        throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Short;)Ljava/lang/String;, n));
    }
    
    public String getTabName(final short s) {
        if (this.tabs.containsKey(s)) {
            return this.tabs.get(s).getTitle();
        }
        return Short.toString(s);
    }
    
    public ChatTab getFocusTab() {
        return this.focusTab;
    }
    
    public void showRadioMessage(final ChatMessage chatMessage) {
        this.radioChat.showMessage(chatMessage);
    }
    
    public void showRadioMessage(final String s) {
        this.radioChat.showMessage(this.radioChat.createMessage(s));
    }
    
    public void showStaticRadioSound(final String s) {
        this.radioChat.showMessage(this.radioChat.createStaticSoundMessage(s));
    }
    
    public ChatMessage createRadiostationMessage(final String s, final int n) {
        return this.radioChat.createBroadcastingMessage(s, n);
    }
    
    public void showServerChatMessage(final String s) {
        this.serverChat.showMessage(this.serverChat.createServerMessage(s));
    }
    
    private void addMessage(final int i, final String s, final String s2) {
        this.mpChats.get(i).showMessage(s2, s);
    }
    
    public void addMessage(final String s, final String s2) throws RuntimeException {
        if (this.generalChat == null) {
            throw new RuntimeException();
        }
        this.addMessage(this.generalChat.getID(), s, s2);
    }
    
    private void sendMessageToChat(final ChatBase chatBase, final ChatMessage chatMessage) {
        if (chatBase.getType() != ChatType.radio) {
            chatBase.showMessage(chatMessage);
            if (chatBase.isEnabled()) {
                if (!this.isSinglePlayerMode() && !chatMessage.isLocal()) {
                    chatBase.sendToServer(chatMessage);
                }
            }
            else {
                this.showChatDisabledMessage(chatBase.getType());
            }
            return;
        }
        if (Core.bDebug) {
            throw new IllegalArgumentException("You can't send message to radio directly. Use radio and send say message");
        }
        DebugLog.log("You try to use radio chat directly. It's restricted. Try to use say chat");
    }
    
    private ChatBase getChat(final ChatType chatType) {
        if (chatType == ChatType.whisper) {
            throw new IllegalArgumentException("Whisper not unique chat");
        }
        switch (chatType) {
            case admin: {
                return this.adminChat;
            }
            case radio: {
                return this.radioChat;
            }
            case general: {
                return this.generalChat;
            }
            case say: {
                return this.sayChat;
            }
            case shout: {
                return this.shoutChat;
            }
            case faction: {
                return this.factionChat;
            }
            case safehouse: {
                return this.safehouseChat;
            }
            case server: {
                return this.serverChat;
            }
            default: {
                throw new IllegalArgumentException("Chat type is undefined");
            }
        }
    }
    
    private void addTab(final short s) {
        final ChatTab chatTab = this.tabs.get(s);
        if (chatTab.isEnabled()) {
            return;
        }
        chatTab.setEnabled(true);
        LuaEventManager.triggerEvent("OnTabAdded", chatTab.getTitle(), chatTab.getID());
    }
    
    private void removeTab(final Short key) {
        final ChatTab chatTab = this.tabs.get(key);
        if (!chatTab.isEnabled()) {
            return;
        }
        LuaEventManager.triggerEvent("OnTabRemoved", chatTab.getTitle(), chatTab.getID());
        chatTab.setEnabled(false);
    }
    
    private WhisperChat getWhisperChat(final String s) throws TimeoutException {
        if (this.whisperChats.containsKey(s)) {
            return this.whisperChats.get(s);
        }
        ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
        this.pmChatStatus = WhisperChat.ChatStatus.Creating;
        final ByteBufferWriter startPacket = this.serverConnection.startPacket();
        PacketTypes.PacketType.PlayerStartPMChat.doPacket(startPacket);
        startPacket.putUTF(this.myNickname);
        startPacket.putUTF(s);
        PacketTypes.PacketType.PlayerStartPMChat.send(this.serverConnection);
        ChatManager.logger.write("'Start PM chat' package sent. Waiting for a creating whisper chat by server...", "info");
        if (this.waitForJoinPMChat(s)) {
            ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, this.myNickname, s, this.whisperChats.get(s).getID()), "info");
            return this.whisperChats.get(s);
        }
        if (this.pmChatStatus == WhisperChat.ChatStatus.PlayerNotFound) {
            ChatManager.logger.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), "info");
            return null;
        }
        throw new TimeoutException();
    }
    
    private boolean waitForJoinPMChat(final String key) {
        for (int i = 0; i < 100; ++i) {
            try {
                Thread.sleep(100L);
                this.whisperChatLocker.lock();
                if (this.pmChatStatus == WhisperChat.ChatStatus.PlayerNotFound) {
                    return false;
                }
                if (this.whisperChats.containsKey(key)) {
                    return true;
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            finally {
                this.whisperChatLocker.unlock();
            }
        }
        return false;
    }
    
    private void showChatDisabledMessage(final ChatType chatType) {
        final StringBuilder sb = new StringBuilder();
        sb.append(Translator.getText("UI_chat_chat_disabled_msg", Translator.getText(chatType.getTitleID())));
        for (final ChatType chatType2 : ChatUtility.getAllowedChatStreams()) {
            if (this.isPlayerCanUseChat(chatType2)) {
                sb.append("    * ").append(Translator.getText(chatType2.getTitleID())).append(" <LINE> ");
            }
        }
        this.showServerChatMessage(sb.toString());
    }
    
    private boolean isPlayerCanUseRadioChat() {
        final Radio equipedRadio = this.player.getEquipedRadio();
        return equipedRadio != null && equipedRadio.getDeviceData() != null && (equipedRadio.getDeviceData().getIsTurnedOn() & equipedRadio.getDeviceData().getIsTwoWay() & equipedRadio.getDeviceData().getIsPortable() & !equipedRadio.getDeviceData().getMicIsMuted());
    }
    
    static {
        ChatManager.instance = null;
    }
    
    private enum Stage
    {
        notStarted, 
        starting, 
        working;
        
        private static /* synthetic */ Stage[] $values() {
            return new Stage[] { Stage.notStarted, Stage.starting, Stage.working };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
