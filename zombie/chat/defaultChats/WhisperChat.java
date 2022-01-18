// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.Lua.LuaManager;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.chat.ChatSettings;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import java.nio.ByteBuffer;
import zombie.network.chat.ChatType;
import zombie.chat.ChatTab;
import zombie.chat.ChatBase;

public class WhisperChat extends ChatBase
{
    private String myName;
    private String companionName;
    private final String player1;
    private final String player2;
    private boolean isInited;
    
    public WhisperChat(final int n, final ChatTab chatTab, final String player1, final String player2) {
        super(n, ChatType.whisper, chatTab);
        this.isInited = false;
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
        this.player1 = player1;
        this.player2 = player2;
    }
    
    public WhisperChat(final ByteBuffer byteBuffer, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, ChatType.whisper, chatTab, isoPlayer);
        this.isInited = false;
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
        this.player1 = GameWindow.ReadString(byteBuffer);
        this.player2 = GameWindow.ReadString(byteBuffer);
    }
    
    public static ChatSettings getDefaultSettings() {
        final ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(new Color(85, 26, 139));
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
    public String getMessagePrefix(final ChatMessage chatMessage) {
        if (!this.isInited) {
            this.init();
        }
        final StringBuilder sb = new StringBuilder(this.getChatSettingsTags());
        if (this.isShowTimestamp()) {
            sb.append("[").append(LuaManager.getHourMinuteJava()).append("]");
        }
        if (this.isShowTitle()) {
            sb.append("[").append(this.getTitle()).append("]");
        }
        if (!this.myName.equalsIgnoreCase(chatMessage.getAuthor())) {
            sb.append("[").append(this.companionName).append("]");
        }
        else {
            sb.append("[to ").append(this.companionName).append("]");
        }
        sb.append(": ");
        return sb.toString();
    }
    
    @Override
    protected void packChat(final ByteBufferWriter byteBufferWriter) {
        super.packChat(byteBufferWriter);
        byteBufferWriter.putUTF(this.player1);
        byteBufferWriter.putUTF(this.player2);
    }
    
    public String getCompanionName() {
        return this.companionName;
    }
    
    public void init() {
        if (this.player1.equals(IsoPlayer.getInstance().getUsername())) {
            this.myName = IsoPlayer.getInstance().getUsername();
            this.companionName = this.player2;
        }
        else if (this.player2.equals(IsoPlayer.getInstance().getUsername())) {
            this.myName = IsoPlayer.getInstance().getUsername();
            this.companionName = this.player1;
        }
        else {
            if (Core.bDebug) {
                throw new RuntimeException("Wrong id");
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.player1, this.player2));
            return;
        }
        this.isInited = true;
    }
    
    public enum ChatStatus
    {
        None, 
        Creating, 
        PlayerNotFound;
        
        private static /* synthetic */ ChatStatus[] $values() {
            return new ChatStatus[] { ChatStatus.None, ChatStatus.Creating, ChatStatus.PlayerNotFound };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
