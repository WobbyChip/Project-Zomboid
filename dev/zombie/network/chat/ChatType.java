// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.chat;

public enum ChatType
{
    notDefined(Integer.valueOf(-1), ""), 
    general(Integer.valueOf(0), "UI_chat_general_chat_title_id"), 
    whisper(Integer.valueOf(1), "UI_chat_private_chat_title_id"), 
    say(Integer.valueOf(2), "UI_chat_local_chat_title_id"), 
    shout(Integer.valueOf(3), "UI_chat_local_chat_title_id"), 
    faction(Integer.valueOf(4), "UI_chat_faction_chat_title_id"), 
    safehouse(Integer.valueOf(5), "UI_chat_safehouse_chat_title_id"), 
    radio(Integer.valueOf(6), "UI_chat_radio_chat_title_id"), 
    admin(Integer.valueOf(7), "UI_chat_admin_chat_title_id"), 
    server(Integer.valueOf(8), "UI_chat_server_chat_title_id");
    
    private final int value;
    private final String titleID;
    
    public static ChatType valueOf(final Integer n) {
        if (ChatType.general.value == n) {
            return ChatType.general;
        }
        if (ChatType.whisper.value == n) {
            return ChatType.whisper;
        }
        if (ChatType.say.value == n) {
            return ChatType.say;
        }
        if (ChatType.shout.value == n) {
            return ChatType.shout;
        }
        if (ChatType.faction.value == n) {
            return ChatType.faction;
        }
        if (ChatType.safehouse.value == n) {
            return ChatType.safehouse;
        }
        if (ChatType.radio.value == n) {
            return ChatType.radio;
        }
        if (ChatType.admin.value == n) {
            return ChatType.admin;
        }
        if (ChatType.server.value == n) {
            return ChatType.server;
        }
        return ChatType.notDefined;
    }
    
    private ChatType(final Integer n, final String titleID) {
        this.value = n;
        this.titleID = titleID;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public String getTitleID() {
        return this.titleID;
    }
    
    private static /* synthetic */ ChatType[] $values() {
        return new ChatType[] { ChatType.notDefined, ChatType.general, ChatType.whisper, ChatType.say, ChatType.shout, ChatType.faction, ChatType.safehouse, ChatType.radio, ChatType.admin, ChatType.server };
    }
    
    static {
        $VALUES = $values();
    }
}
