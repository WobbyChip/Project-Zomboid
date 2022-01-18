// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

public class ServerChatMessage extends ChatMessage
{
    public ServerChatMessage(final ChatBase chatBase, final String s) {
        super(chatBase, s);
        super.setAuthor("Server");
        this.setServerAuthor(true);
    }
    
    @Override
    public String getAuthor() {
        return super.getAuthor();
    }
    
    @Override
    public void setAuthor(final String s) {
        throw new UnsupportedOperationException();
    }
}
