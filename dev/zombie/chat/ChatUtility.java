// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.textures.Texture;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatType;
import java.util.ArrayList;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoObject;
import java.util.HashMap;

public final class ChatUtility
{
    private static final boolean useEuclidean = true;
    private static final HashMap<String, String> allowedChatIcons;
    private static final HashMap<String, String> allowedChatIconsFull;
    private static final StringBuilder builder;
    private static final StringBuilder builderTest;
    
    private ChatUtility() {
    }
    
    public static float getScrambleValue(final IsoObject isoObject, final IsoPlayer isoPlayer, final float n) {
        return getScrambleValue(isoObject.getX(), isoObject.getY(), isoObject.getZ(), isoObject.getSquare(), isoPlayer, n);
    }
    
    public static float getScrambleValue(final float n, final float n2, final float n3, final IsoGridSquare isoGridSquare, final IsoPlayer isoPlayer, final float n4) {
        float n5 = 1.0f;
        boolean b = false;
        boolean b2 = false;
        if (isoGridSquare != null && isoPlayer.getSquare() != null) {
            if (isoPlayer.getBuilding() != null && isoGridSquare.getBuilding() != null && isoPlayer.getBuilding() == isoGridSquare.getBuilding()) {
                if (isoPlayer.getSquare().getRoom() == isoGridSquare.getRoom()) {
                    n5 *= 2.0;
                    b2 = true;
                }
                else if (Math.abs(isoPlayer.getZ() - n3) < 1.0f) {
                    n5 *= 2.0;
                }
            }
            else if (isoPlayer.getBuilding() != null || isoGridSquare.getBuilding() != null) {
                n5 *= 0.5;
                b = true;
            }
            if (Math.abs(isoPlayer.getZ() - n3) >= 1.0f) {
                n5 -= (float)(n5 * (Math.abs(isoPlayer.getZ() - n3) * 0.25));
                b = true;
            }
        }
        final float n6 = n4 * n5;
        float n7 = 1.0f;
        if (n5 > 0.0f && playerWithinBounds(n, n2, isoPlayer, n6)) {
            final float distance = getDistance(n, n2, isoPlayer);
            if (distance >= 0.0f && distance < n6) {
                final float n8 = n6 * 0.6f;
                if (b2 || (!b && distance < n8)) {
                    n7 = 0.0f;
                }
                else if (n6 - n8 != 0.0f) {
                    n7 = (distance - n8) / (n6 - n8);
                    if (n7 < 0.2f) {
                        n7 = 0.2f;
                    }
                }
            }
        }
        return n7;
    }
    
    public static boolean playerWithinBounds(final IsoObject isoObject, final IsoObject isoObject2, final float n) {
        return playerWithinBounds(isoObject.getX(), isoObject.getY(), isoObject2, n);
    }
    
    public static boolean playerWithinBounds(final float n, final float n2, final IsoObject isoObject, final float n3) {
        return isoObject != null && isoObject.getX() > n - n3 && isoObject.getX() < n + n3 && isoObject.getY() > n2 - n3 && isoObject.getY() < n2 + n3;
    }
    
    public static float getDistance(final IsoObject isoObject, final IsoPlayer isoPlayer) {
        if (isoPlayer == null) {
            return -1.0f;
        }
        return (float)Math.sqrt(Math.pow(isoObject.getX() - isoPlayer.x, 2.0) + Math.pow(isoObject.getY() - isoPlayer.y, 2.0));
    }
    
    public static float getDistance(final float n, final float n2, final IsoPlayer isoPlayer) {
        if (isoPlayer == null) {
            return -1.0f;
        }
        return (float)Math.sqrt(Math.pow(n - isoPlayer.x, 2.0) + Math.pow(n2 - isoPlayer.y, 2.0));
    }
    
    public static UdpConnection findConnection(final short n) {
        UdpConnection udpConnection = null;
        if (GameServer.udpEngine != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                for (int j = 0; j < udpConnection2.playerIDs.length; ++j) {
                    if (udpConnection2.playerIDs[j] == n) {
                        udpConnection = udpConnection2;
                        break;
                    }
                }
            }
        }
        if (udpConnection == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
        return udpConnection;
    }
    
    public static UdpConnection findConnection(final String anotherString) {
        UdpConnection udpConnection = null;
        if (GameServer.udpEngine != null) {
            for (int n = 0; n < GameServer.udpEngine.connections.size() && udpConnection == null; ++n) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(n);
                for (int i = 0; i < udpConnection2.players.length; ++i) {
                    if (udpConnection2.players[i] != null && udpConnection2.players[i].username.equalsIgnoreCase(anotherString)) {
                        udpConnection = udpConnection2;
                        break;
                    }
                }
            }
        }
        if (udpConnection == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anotherString));
        }
        return udpConnection;
    }
    
    public static IsoPlayer findPlayer(final int n) {
        IsoPlayer isoPlayer = null;
        if (GameServer.udpEngine != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                for (int j = 0; j < udpConnection.playerIDs.length; ++j) {
                    if (udpConnection.playerIDs[j] == n) {
                        isoPlayer = udpConnection.players[j];
                        break;
                    }
                }
            }
        }
        if (isoPlayer == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        return isoPlayer;
    }
    
    public static String findPlayerName(final int n) {
        return findPlayer(n).getUsername();
    }
    
    public static IsoPlayer findPlayer(final String s) {
        IsoPlayer isoPlayer = null;
        if (GameClient.bClient) {
            isoPlayer = GameClient.instance.getPlayerFromUsername(s);
        }
        else if (GameServer.bServer) {
            isoPlayer = GameServer.getPlayerByUserName(s);
        }
        if (isoPlayer == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        return isoPlayer;
    }
    
    public static ArrayList<ChatType> getAllowedChatStreams() {
        final String[] split = ServerOptions.getInstance().ChatStreams.getValue().replaceAll("\"", "").split(",");
        final ArrayList<ChatType> list = new ArrayList<ChatType>();
        list.add(ChatType.server);
        for (final String s : split) {
            switch (s) {
                case "s": {
                    list.add(ChatType.say);
                    break;
                }
                case "r": {
                    list.add(ChatType.radio);
                    break;
                }
                case "a": {
                    list.add(ChatType.admin);
                    break;
                }
                case "w": {
                    list.add(ChatType.whisper);
                    break;
                }
                case "y": {
                    list.add(ChatType.shout);
                    break;
                }
                case "sh": {
                    list.add(ChatType.safehouse);
                    break;
                }
                case "f": {
                    list.add(ChatType.faction);
                    break;
                }
                case "all": {
                    list.add(ChatType.general);
                    break;
                }
            }
        }
        return list;
    }
    
    public static boolean chatStreamEnabled(final ChatType o) {
        return getAllowedChatStreams().contains(o);
    }
    
    public static void InitAllowedChatIcons() {
        ChatUtility.allowedChatIcons.clear();
        Texture.collectAllIcons(ChatUtility.allowedChatIcons, ChatUtility.allowedChatIconsFull);
    }
    
    private static String getColorString(final String s, final boolean b) {
        if (!Colors.ColorExists(s)) {
            if (s.length() <= 11 && s.contains(",")) {
                final String[] split = s.split(",");
                if (split.length == 3) {
                    final int colorInt = parseColorInt(split[0]);
                    final int colorInt2 = parseColorInt(split[1]);
                    final int colorInt3 = parseColorInt(split[2]);
                    if (colorInt != -1 && colorInt2 != -1 && colorInt3 != -1) {
                        if (b) {
                            return invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, colorInt / 255.0f, colorInt2 / 255.0f, colorInt3 / 255.0f);
                        }
                        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, colorInt, colorInt2, colorInt3);
                    }
                }
            }
            return null;
        }
        final Color getColorByName = Colors.GetColorByName(s);
        if (b) {
            return invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, getColorByName.getRedFloat(), getColorByName.getGreenFloat(), getColorByName.getBlueFloat());
        }
        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, getColorByName.getRed(), getColorByName.getGreen(), getColorByName.getBlue());
    }
    
    private static int parseColorInt(final String s) {
        try {
            final int int1 = Integer.parseInt(s);
            if (int1 >= 0 && int1 <= 255) {
                return int1;
            }
            return -1;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public static String parseStringForChatBubble(String s) {
        try {
            ChatUtility.builder.delete(0, ChatUtility.builder.length());
            ChatUtility.builderTest.delete(0, ChatUtility.builderTest.length());
            s = s.replaceAll("\\[br/]", "");
            s = s.replaceAll("\\[cdt=", "");
            final char[] charArray = s.toCharArray();
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            for (int i = 0; i < charArray.length; ++i) {
                final char c = charArray[i];
                if (c == '*') {
                    if (n == 0) {
                        n = 1;
                    }
                    else {
                        final String string = ChatUtility.builderTest.toString();
                        ChatUtility.builderTest.delete(0, ChatUtility.builderTest.length());
                        final String colorString = getColorString(string, false);
                        if (colorString != null) {
                            if (n2 != 0) {
                                ChatUtility.builder.append("[/]");
                            }
                            ChatUtility.builder.append("[col=");
                            ChatUtility.builder.append(colorString);
                            ChatUtility.builder.append(']');
                            n = 0;
                            n2 = 1;
                        }
                        else if (n3 < 10 && (string.equalsIgnoreCase("music") || ChatUtility.allowedChatIcons.containsKey(string.toLowerCase()))) {
                            if (n2 != 0) {
                                ChatUtility.builder.append("[/]");
                                n2 = 0;
                            }
                            ChatUtility.builder.append("[img=");
                            ChatUtility.builder.append(string.equalsIgnoreCase("music") ? "music" : ChatUtility.allowedChatIcons.get(string.toLowerCase()));
                            ChatUtility.builder.append(']');
                            n = 0;
                            ++n3;
                        }
                        else {
                            ChatUtility.builder.append('*');
                            ChatUtility.builder.append(string);
                        }
                    }
                }
                else if (n != 0) {
                    ChatUtility.builderTest.append(c);
                }
                else {
                    ChatUtility.builder.append(c);
                }
            }
            if (n != 0) {
                ChatUtility.builder.append('*');
                final String string2 = ChatUtility.builderTest.toString();
                if (string2.length() > 0) {
                    ChatUtility.builder.append(string2);
                }
                if (n2 != 0) {
                    ChatUtility.builder.append("[/]");
                }
            }
            return ChatUtility.builder.toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return s;
        }
    }
    
    public static String parseStringForChatLog(final String s) {
        try {
            ChatUtility.builder.delete(0, ChatUtility.builder.length());
            ChatUtility.builderTest.delete(0, ChatUtility.builderTest.length());
            final char[] charArray = s.toCharArray();
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            for (int i = 0; i < charArray.length; ++i) {
                final char c = charArray[i];
                if (c == '*') {
                    if (n == 0) {
                        n = 1;
                    }
                    else {
                        final String string = ChatUtility.builderTest.toString();
                        ChatUtility.builderTest.delete(0, ChatUtility.builderTest.length());
                        final String colorString = getColorString(string, true);
                        if (colorString != null) {
                            ChatUtility.builder.append(" <RGB:");
                            ChatUtility.builder.append(colorString);
                            ChatUtility.builder.append('>');
                            n = 0;
                            n2 = 1;
                        }
                        else {
                            if (n3 < 10 && (string.equalsIgnoreCase("music") || ChatUtility.allowedChatIconsFull.containsKey(string.toLowerCase()))) {
                                if (n2 != 0) {
                                    ChatUtility.builder.append(" <RGB:");
                                    ChatUtility.builder.append("1.0,1.0,1.0");
                                    ChatUtility.builder.append('>');
                                    n2 = 0;
                                }
                                final String str = string.equalsIgnoreCase("music") ? "Icon_music_notes" : ChatUtility.allowedChatIconsFull.get(string.toLowerCase());
                                final Texture sharedTexture = Texture.getSharedTexture(str);
                                if (Texture.getSharedTexture(str) != null) {
                                    int n4 = (int)(sharedTexture.getWidth() * 0.5f);
                                    int n5 = (int)(sharedTexture.getHeight() * 0.5f);
                                    if (string.equalsIgnoreCase("music")) {
                                        n4 = (int)(sharedTexture.getWidth() * 0.75f);
                                        n5 = (int)(sharedTexture.getHeight() * 0.75f);
                                    }
                                    ChatUtility.builder.append("<IMAGE:");
                                    ChatUtility.builder.append(str);
                                    ChatUtility.builder.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n4, n5));
                                    n = 0;
                                    ++n3;
                                    continue;
                                }
                            }
                            ChatUtility.builder.append('*');
                            ChatUtility.builder.append(string);
                        }
                    }
                }
                else if (n != 0) {
                    ChatUtility.builderTest.append(c);
                }
                else {
                    ChatUtility.builder.append(c);
                }
            }
            if (n != 0) {
                ChatUtility.builder.append('*');
                final String string2 = ChatUtility.builderTest.toString();
                if (string2.length() > 0) {
                    ChatUtility.builder.append(string2);
                }
            }
            return ChatUtility.builder.toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return s;
        }
    }
    
    static {
        allowedChatIcons = new HashMap<String, String>();
        allowedChatIconsFull = new HashMap<String, String>();
        builder = new StringBuilder();
        builderTest = new StringBuilder();
    }
}
