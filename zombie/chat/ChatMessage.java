// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.core.network.ByteBufferWriter;
import java.time.format.DateTimeFormatter;
import zombie.core.Color;
import java.time.LocalDateTime;

public class ChatMessage implements Cloneable
{
    private ChatBase chat;
    private LocalDateTime datetime;
    private String author;
    private String text;
    private boolean scramble;
    private String customTag;
    private Color textColor;
    private boolean customColor;
    private boolean overHeadSpeech;
    private boolean showInChat;
    private boolean fromDiscord;
    private boolean serverAlert;
    private int radioChannel;
    private boolean local;
    private boolean shouldAttractZombies;
    private boolean serverAuthor;
    
    public ChatMessage(final ChatBase chatBase, final String s) {
        this(chatBase, LocalDateTime.now(), s);
    }
    
    public ChatMessage(final ChatBase chat, final LocalDateTime datetime, final String text) {
        this.scramble = false;
        this.overHeadSpeech = true;
        this.showInChat = true;
        this.fromDiscord = false;
        this.serverAlert = false;
        this.radioChannel = -1;
        this.local = false;
        this.shouldAttractZombies = false;
        this.serverAuthor = false;
        this.chat = chat;
        this.datetime = datetime;
        this.text = text;
        this.textColor = chat.getColor();
        this.customColor = false;
    }
    
    public boolean isShouldAttractZombies() {
        return this.shouldAttractZombies;
    }
    
    public void setShouldAttractZombies(final boolean shouldAttractZombies) {
        this.shouldAttractZombies = shouldAttractZombies;
    }
    
    public boolean isLocal() {
        return this.local;
    }
    
    public void setLocal(final boolean local) {
        this.local = local;
    }
    
    public String getTextWithReplacedParentheses() {
        if (this.text != null) {
            return this.text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }
        return null;
    }
    
    public void setScrambledText(final String text) {
        this.scramble = true;
        this.text = text;
    }
    
    public int getRadioChannel() {
        return this.radioChannel;
    }
    
    public void setRadioChannel(final int radioChannel) {
        this.radioChannel = radioChannel;
    }
    
    public boolean isServerAuthor() {
        return this.serverAuthor;
    }
    
    public void setServerAuthor(final boolean serverAuthor) {
        this.serverAuthor = serverAuthor;
    }
    
    public boolean isFromDiscord() {
        return this.fromDiscord;
    }
    
    public void makeFromDiscord() {
        this.fromDiscord = true;
    }
    
    public boolean isOverHeadSpeech() {
        return this.overHeadSpeech;
    }
    
    public void setOverHeadSpeech(final boolean overHeadSpeech) {
        this.overHeadSpeech = overHeadSpeech;
    }
    
    public boolean isShowInChat() {
        return this.showInChat;
    }
    
    public void setShowInChat(final boolean showInChat) {
        this.showInChat = showInChat;
    }
    
    public LocalDateTime getDatetime() {
        return this.datetime;
    }
    
    public String getDatetimeStr() {
        return this.datetime.format(DateTimeFormatter.ofPattern("h:m"));
    }
    
    public void setDatetime(final LocalDateTime datetime) {
        this.datetime = datetime;
    }
    
    public boolean isShowAuthor() {
        return this.getChat().isShowAuthor();
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(final String author) {
        this.author = author;
    }
    
    public ChatBase getChat() {
        return this.chat;
    }
    
    public int getChatID() {
        return this.chat.getID();
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public String getTextWithPrefix() {
        return this.chat.getMessageTextWithPrefix(this);
    }
    
    public boolean isScramble() {
        return this.scramble;
    }
    
    public String getCustomTag() {
        return this.customTag;
    }
    
    public void setCustomTag(final String customTag) {
        this.customTag = customTag;
    }
    
    public Color getTextColor() {
        return this.textColor;
    }
    
    public void setTextColor(final Color textColor) {
        this.customColor = true;
        this.textColor = textColor;
    }
    
    public boolean isCustomColor() {
        return this.customColor;
    }
    
    public void pack(final ByteBufferWriter byteBufferWriter) {
        this.chat.packMessage(byteBufferWriter, this);
    }
    
    public ChatMessage clone() {
        ChatMessage chatMessage;
        try {
            chatMessage = (ChatMessage)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException();
        }
        chatMessage.datetime = this.datetime;
        chatMessage.chat = this.chat;
        chatMessage.author = this.author;
        chatMessage.text = this.text;
        chatMessage.scramble = this.scramble;
        chatMessage.customTag = this.customTag;
        chatMessage.textColor = this.textColor;
        chatMessage.customColor = this.customColor;
        chatMessage.overHeadSpeech = this.overHeadSpeech;
        return chatMessage;
    }
    
    public boolean isServerAlert() {
        return this.serverAlert;
    }
    
    public void setServerAlert(final boolean serverAlert) {
        this.serverAlert = serverAlert;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.chat.getTitle(), this.author, this.text);
    }
}
