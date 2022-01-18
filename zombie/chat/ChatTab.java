// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.core.Translator;
import java.util.HashSet;

public class ChatTab
{
    private short id;
    private String titleID;
    private String translatedTitle;
    private HashSet<Integer> containedChats;
    private boolean enabled;
    
    public ChatTab(final short id, final String titleID) {
        this.enabled = false;
        this.id = id;
        this.titleID = titleID;
        this.translatedTitle = Translator.getText(titleID);
        this.containedChats = new HashSet<Integer>();
    }
    
    public ChatTab(final short n, final String s, final int i) {
        this(n, s);
        this.containedChats.add(i);
    }
    
    public void RemoveChat(final int n) {
        if (!this.containedChats.contains(n)) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(SI)Ljava/lang/String;, this.id, n));
        }
        this.containedChats.remove(n);
    }
    
    public String getTitleID() {
        return this.titleID;
    }
    
    public String getTitle() {
        return this.translatedTitle;
    }
    
    public short getID() {
        return this.id;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void sendAddTabPacket(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.AddChatTab.doPacket(startPacket);
        startPacket.putShort(this.getID());
        PacketTypes.PacketType.AddChatTab.send(udpConnection);
    }
    
    public void sendRemoveTabPacket(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.RemoveChatTab.doPacket(startPacket);
        startPacket.putShort(this.getID());
        PacketTypes.PacketType.RemoveChatTab.send(udpConnection);
    }
}
