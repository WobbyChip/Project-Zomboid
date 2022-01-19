// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.chat.ChatSettings;
import zombie.chat.defaultChats.FactionChat;
import zombie.network.chat.ChatServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.core.network.ByteBufferWriter;
import zombie.network.ServerOptions;
import zombie.network.GameClient;
import zombie.core.Rand;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;

public final class Faction
{
    private String name;
    private String owner;
    private String tag;
    private ColorInfo tagColor;
    private final ArrayList<String> players;
    public static ArrayList<Faction> factions;
    
    public Faction() {
        this.players = new ArrayList<String>();
    }
    
    public Faction(final String name, final String owner) {
        this.players = new ArrayList<String>();
        this.setName(name);
        this.setOwner(owner);
        this.tagColor = new ColorInfo(Rand.Next(0.3f, 1.0f), Rand.Next(0.3f, 1.0f), Rand.Next(0.3f, 1.0f), 1.0f);
    }
    
    public static Faction createFaction(final String s, final String s2) {
        if (!factionExist(s)) {
            final Faction e = new Faction(s, s2);
            Faction.factions.add(e);
            if (GameClient.bClient) {
                GameClient.sendFaction(e, false);
            }
            return e;
        }
        return null;
    }
    
    public static ArrayList<Faction> getFactions() {
        return Faction.factions;
    }
    
    public static boolean canCreateFaction(final IsoPlayer isoPlayer) {
        boolean value = ServerOptions.instance.Faction.getValue();
        if (value && ServerOptions.instance.FactionDaySurvivedToCreate.getValue() > 0 && isoPlayer.getHoursSurvived() / 24.0 < ServerOptions.instance.FactionDaySurvivedToCreate.getValue()) {
            value = false;
        }
        return value;
    }
    
    public boolean canCreateTag() {
        return this.players.size() + 1 >= ServerOptions.instance.FactionPlayersRequiredForTag.getValue();
    }
    
    public static boolean isAlreadyInFaction(final String s) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            final Faction faction = Faction.factions.get(i);
            if (faction.getOwner().equals(s)) {
                return true;
            }
            for (int j = 0; j < faction.getPlayers().size(); ++j) {
                if (faction.getPlayers().get(j).equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isAlreadyInFaction(final IsoPlayer isoPlayer) {
        return isAlreadyInFaction(isoPlayer.getUsername());
    }
    
    public void removePlayer(final String o) {
        this.getPlayers().remove(o);
        if (GameClient.bClient) {
            GameClient.sendFaction(this, false);
        }
    }
    
    public static boolean factionExist(final String anObject) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            if (Faction.factions.get(i).getName().equals(anObject)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean tagExist(final String anObject) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            if (Faction.factions.get(i).getTag() != null && Faction.factions.get(i).getTag().equals(anObject)) {
                return true;
            }
        }
        return false;
    }
    
    public static Faction getPlayerFaction(final IsoPlayer isoPlayer) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            final Faction faction = Faction.factions.get(i);
            if (faction.getOwner().equals(isoPlayer.getUsername())) {
                return faction;
            }
            for (int j = 0; j < faction.getPlayers().size(); ++j) {
                if (faction.getPlayers().get(j).equals(isoPlayer.getUsername())) {
                    return faction;
                }
            }
        }
        return null;
    }
    
    public static Faction getPlayerFaction(final String s) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            final Faction faction = Faction.factions.get(i);
            if (faction.getOwner().equals(s)) {
                return faction;
            }
            for (int j = 0; j < faction.getPlayers().size(); ++j) {
                if (faction.getPlayers().get(j).equals(s)) {
                    return faction;
                }
            }
        }
        return null;
    }
    
    public static Faction getFaction(final String anObject) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            if (Faction.factions.get(i).getName().equals(anObject)) {
                return Faction.factions.get(i);
            }
        }
        return null;
    }
    
    public void removeFaction() {
        getFactions().remove(this);
        if (GameClient.bClient) {
            GameClient.sendFaction(this, true);
        }
    }
    
    public void syncFaction() {
        if (GameClient.bClient) {
            GameClient.sendFaction(this, false);
        }
    }
    
    public boolean isOwner(final String anObject) {
        return this.getOwner().equals(anObject);
    }
    
    public boolean isPlayerMember(final IsoPlayer isoPlayer) {
        return this.isMember(isoPlayer.getUsername());
    }
    
    public boolean isMember(final String anObject) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            if (this.getPlayers().get(i).equals(anObject)) {
                return true;
            }
        }
        return false;
    }
    
    public void writeToBuffer(final ByteBufferWriter byteBufferWriter, final boolean b) {
        byteBufferWriter.putUTF(this.getName());
        byteBufferWriter.putUTF(this.getOwner());
        byteBufferWriter.putInt(this.getPlayers().size());
        if (this.getTag() != null) {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putUTF(this.getTag());
            byteBufferWriter.putFloat(this.getTagColor().r);
            byteBufferWriter.putFloat(this.getTagColor().g);
            byteBufferWriter.putFloat(this.getTagColor().b);
        }
        else {
            byteBufferWriter.putByte((byte)0);
        }
        final Iterator<String> iterator = this.getPlayers().iterator();
        while (iterator.hasNext()) {
            byteBufferWriter.putUTF(iterator.next());
        }
        byteBufferWriter.putBoolean(b);
    }
    
    public void save(final ByteBuffer byteBuffer) {
        GameWindow.WriteString(byteBuffer, this.getName());
        GameWindow.WriteString(byteBuffer, this.getOwner());
        byteBuffer.putInt(this.getPlayers().size());
        if (this.getTag() != null) {
            byteBuffer.put((byte)1);
            GameWindow.WriteString(byteBuffer, this.getTag());
            byteBuffer.putFloat(this.getTagColor().r);
            byteBuffer.putFloat(this.getTagColor().g);
            byteBuffer.putFloat(this.getTagColor().b);
        }
        else {
            byteBuffer.put((byte)0);
        }
        final Iterator<String> iterator = this.getPlayers().iterator();
        while (iterator.hasNext()) {
            GameWindow.WriteString(byteBuffer, iterator.next());
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) {
        this.setName(GameWindow.ReadString(byteBuffer));
        this.setOwner(GameWindow.ReadString(byteBuffer));
        final int int1 = byteBuffer.getInt();
        if (byteBuffer.get() == 1) {
            this.setTag(GameWindow.ReadString(byteBuffer));
            this.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        }
        else {
            this.setTagColor(new ColorInfo(Rand.Next(0.3f, 1.0f), Rand.Next(0.3f, 1.0f), Rand.Next(0.3f, 1.0f), 1.0f));
        }
        for (int i = 0; i < int1; ++i) {
            this.getPlayers().add(GameWindow.ReadString(byteBuffer));
        }
        if (ChatServer.isInited()) {
            final FactionChat factionChat = ChatServer.getInstance().createFactionChat(this.getName());
            final ChatSettings defaultSettings = FactionChat.getDefaultSettings();
            defaultSettings.setFontColor(this.tagColor.r, this.tagColor.g, this.tagColor.b, this.tagColor.a);
            factionChat.setSettings(defaultSettings);
        }
    }
    
    public void addPlayer(final String e) {
        for (int i = 0; i < Faction.factions.size(); ++i) {
            final Faction faction = Faction.factions.get(i);
            if (faction.getOwner().equals(e)) {
                return;
            }
            for (int j = 0; j < faction.getPlayers().size(); ++j) {
                if (faction.getPlayers().get(j).equals(e)) {
                    return;
                }
            }
        }
        this.players.add(e);
        if (GameClient.bClient) {
            GameClient.sendFaction(this, false);
        }
    }
    
    public ArrayList<String> getPlayers() {
        return this.players;
    }
    
    public ColorInfo getTagColor() {
        return this.tagColor;
    }
    
    public void setTagColor(final ColorInfo tagColor) {
        if (tagColor.r < 0.19f) {
            tagColor.r = 0.19f;
        }
        if (tagColor.g < 0.19f) {
            tagColor.g = 0.19f;
        }
        if (tagColor.b < 0.19f) {
            tagColor.b = 0.19f;
        }
        this.tagColor = tagColor;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(final String owner) {
        if (this.owner == null) {
            this.owner = owner;
            return;
        }
        if (!this.isMember(this.owner)) {
            this.getPlayers().add(this.owner);
            this.getPlayers().remove(owner);
        }
        this.owner = owner;
    }
    
    static {
        Faction.factions = new ArrayList<Faction>();
    }
}
