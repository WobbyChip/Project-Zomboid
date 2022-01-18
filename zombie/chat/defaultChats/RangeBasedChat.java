// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat.defaultChats;

import zombie.network.GameClient;
import zombie.core.Color;
import zombie.core.fonts.AngelCodeFont;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import java.util.Iterator;
import zombie.iso.IsoObject;
import zombie.debug.DebugLog;
import zombie.chat.ChatUtility;
import zombie.chat.ChatMode;
import zombie.chat.ChatMessage;
import zombie.chat.ChatTab;
import zombie.network.chat.ChatType;
import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import java.util.HashMap;
import zombie.chat.ChatElement;
import zombie.chat.ChatBase;

public abstract class RangeBasedChat extends ChatBase
{
    private static ChatElement overHeadChat;
    private static HashMap<String, IsoPlayer> players;
    private static String currentPlayerName;
    String customTag;
    
    RangeBasedChat(final ByteBuffer byteBuffer, final ChatType chatType, final ChatTab chatTab, final IsoPlayer isoPlayer) {
        super(byteBuffer, chatType, chatTab, isoPlayer);
        this.customTag = "default";
    }
    
    RangeBasedChat(final ChatType chatType) {
        super(chatType);
        this.customTag = "default";
    }
    
    RangeBasedChat(final int n, final ChatType chatType, final ChatTab chatTab) {
        super(n, chatType, chatTab);
        this.customTag = "default";
    }
    
    public void Init() {
        RangeBasedChat.currentPlayerName = this.getChatOwnerName();
        if (RangeBasedChat.players != null) {
            RangeBasedChat.players.clear();
        }
        RangeBasedChat.overHeadChat = this.getChatOwner().getChatElement();
    }
    
    @Override
    public boolean isSendingToRadio() {
        return true;
    }
    
    @Override
    public ChatMessage createMessage(final String s) {
        final ChatMessage message = super.createMessage(s);
        if (this.getMode() == ChatMode.SinglePlayer) {
            message.setShowInChat(false);
        }
        message.setOverHeadSpeech(true);
        message.setShouldAttractZombies(true);
        return message;
    }
    
    public ChatMessage createBubbleMessage(final String s) {
        final ChatMessage message = super.createMessage(s);
        message.setOverHeadSpeech(true);
        message.setShowInChat(false);
        return message;
    }
    
    @Override
    public void sendMessageToChatMembers(final ChatMessage chatMessage) {
        final IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
        if (this.getRange() == -1.0f) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getTitle(), chatMessage.getText()));
            return;
        }
        for (final short shortValue : this.members) {
            final IsoPlayer player2 = ChatUtility.findPlayer(shortValue);
            if (player2 != null) {
                if (player.getOnlineID() == shortValue) {
                    continue;
                }
                if (ChatUtility.getDistance(player, player2) >= this.getRange()) {
                    continue;
                }
                this.sendMessageToPlayer(shortValue, chatMessage);
            }
        }
    }
    
    @Override
    public void showMessage(final ChatMessage chatMessage) {
        super.showMessage(chatMessage);
        if (chatMessage.isOverHeadSpeech()) {
            this.showInSpeechBubble(chatMessage);
        }
    }
    
    protected ChatElement getSpeechBubble() {
        return RangeBasedChat.overHeadChat;
    }
    
    protected UIFont selectFont(final String s) {
        final char[] charArray = s.toCharArray();
        UIFont uiFont = UIFont.Dialogue;
        final AngelCodeFont fontFromEnum = TextManager.instance.getFontFromEnum(uiFont);
        for (int i = 0; i < charArray.length; ++i) {
            if (charArray[i] > fontFromEnum.chars.length) {
                uiFont = UIFont.Medium;
                break;
            }
        }
        return uiFont;
    }
    
    protected void showInSpeechBubble(final ChatMessage chatMessage) {
        final Color color = this.getColor();
        final String author = chatMessage.getAuthor();
        final IsoPlayer player = this.getPlayer(author);
        float n = color.r;
        float n2 = color.g;
        float n3 = color.b;
        if (player != null) {
            n = player.getSpeakColour().r;
            n2 = player.getSpeakColour().g;
            n3 = player.getSpeakColour().b;
        }
        final String stringForChatBubble = ChatUtility.parseStringForChatBubble(chatMessage.getText());
        if (author == null || "".equalsIgnoreCase(author) || author.equalsIgnoreCase(RangeBasedChat.currentPlayerName)) {
            RangeBasedChat.overHeadChat.addChatLine(stringForChatBubble, n, n2, n3, this.selectFont(stringForChatBubble), this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
        }
        else {
            if (!RangeBasedChat.players.containsKey(author)) {
                RangeBasedChat.players.put(author, this.getPlayer(author));
            }
            IsoPlayer player2 = RangeBasedChat.players.get(author);
            if (player2 != null) {
                if (player2.isDead()) {
                    player2 = this.getPlayer(author);
                    RangeBasedChat.players.replace(author, player2);
                }
                player2.getChatElement().addChatLine(stringForChatBubble, n, n2, n3, this.selectFont(stringForChatBubble), this.getRange(), this.customTag, this.isAllowBBcode(), this.isAllowImages(), this.isAllowChatIcons(), this.isAllowColors(), this.isAllowFonts(), this.isEqualizeLineHeights());
            }
        }
    }
    
    private IsoPlayer getPlayer(final String anObject) {
        final IsoPlayer isoPlayer = GameClient.bClient ? GameClient.instance.getPlayerFromUsername(anObject) : null;
        if (isoPlayer != null) {
            return isoPlayer;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer2 = IsoPlayer.players[i];
            if (isoPlayer2 != null) {
                if (isoPlayer2.getUsername().equals(anObject)) {
                    return isoPlayer2;
                }
            }
        }
        return null;
    }
    
    static {
        RangeBasedChat.overHeadChat = null;
        RangeBasedChat.players = null;
        RangeBasedChat.currentPlayerName = null;
        RangeBasedChat.players = new HashMap<String, IsoPlayer>();
    }
}
