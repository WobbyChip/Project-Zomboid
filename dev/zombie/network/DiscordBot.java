// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import de.btobastian.javacord.listener.Listener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.Javacord;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import de.btobastian.javacord.entities.Channel;
import java.util.Collection;
import de.btobastian.javacord.DiscordAPI;

public class DiscordBot
{
    private DiscordAPI api;
    private Collection<Channel> channels;
    private Channel current;
    private String currentChannelName;
    private String currentChannelID;
    private String name;
    private DiscordSender sender;
    
    public DiscordBot(final String name, final DiscordSender sender) {
        this.name = name;
        this.sender = sender;
        this.current = null;
    }
    
    public void connect(boolean b, final String s, final String currentChannelName, final String currentChannelID) {
        if (s == null || s.isEmpty()) {
            DebugLog.log(DebugType.Network, "DISCORD: token not configured");
            b = false;
        }
        if (!b) {
            DebugLog.log(DebugType.Network, "*** DISCORD DISABLED ****");
            this.current = null;
            return;
        }
        (this.api = Javacord.getApi(s, true)).connect((FutureCallback)new Connector());
        DebugLog.log(DebugType.Network, "*** DISCORD ENABLED ****");
        this.currentChannelName = currentChannelName;
        this.currentChannelID = currentChannelID;
    }
    
    private void setChannel(String channelByName, final String channelByID) {
        final Collection<String> channelNames = this.getChannelNames();
        if ((channelByName == null || channelByName.isEmpty()) && !channelNames.isEmpty()) {
            channelByName = channelNames.iterator().next();
            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, channelByName));
        }
        if (channelByID != null && !channelByID.isEmpty()) {
            this.setChannelByID(channelByID);
            return;
        }
        if (channelByName != null) {
            this.setChannelByName(channelByName);
        }
    }
    
    public void sendMessage(final String s, final String s2) {
        if (this.current != null) {
            this.current.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
        }
    }
    
    private Collection<String> getChannelNames() {
        final ArrayList<String> list = new ArrayList<String>();
        this.channels = (Collection<Channel>)this.api.getChannels();
        final Iterator<Channel> iterator = this.channels.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getName());
        }
        return list;
    }
    
    private void setChannelByName(final String anObject) {
        this.current = null;
        for (final Channel current : this.channels) {
            if (current.getName().equals(anObject)) {
                if (this.current != null) {
                    DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                    this.current = null;
                    return;
                }
                this.current = current;
            }
        }
        if (this.current == null) {
            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
        }
        else {
            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
        }
    }
    
    private void setChannelByID(final String anObject) {
        this.current = null;
        for (final Channel current : this.channels) {
            if (current.getId().equals(anObject)) {
                DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                this.current = current;
                break;
            }
        }
        if (this.current == null) {
            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
        }
    }
    
    class Listener implements MessageCreateListener
    {
        public void onMessageCreate(final DiscordAPI discordAPI, final Message message) {
            if (DiscordBot.this.current == null) {
                return;
            }
            if (discordAPI.getYourself().getId().equals(message.getAuthor().getId())) {
                return;
            }
            if (message.getChannelReceiver().getId().equals(DiscordBot.this.current.getId())) {
                DebugLog.log(DebugType.Network, "DISCORD: get message on current channel");
                DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, message.getContent(), message.getAuthor().getName()));
                final String removeSmilesAndImages = this.removeSmilesAndImages(this.replaceChannelIDByItsName(discordAPI, message));
                if (!removeSmilesAndImages.isEmpty() && !removeSmilesAndImages.matches("^\\s$")) {
                    DiscordBot.this.sender.sendMessageFromDiscord(message.getAuthor().getName(), removeSmilesAndImages);
                }
            }
        }
        
        private String replaceChannelIDByItsName(final DiscordAPI discordAPI, final Message message) {
            String s = message.getContent();
            final Matcher matcher = Pattern.compile("<#(\\d+)>").matcher(message.getContent());
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); ++i) {
                    final Channel channelById = discordAPI.getChannelById(matcher.group(i));
                    if (channelById != null) {
                        s = s.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, matcher.group(i)), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, channelById.getName()));
                    }
                }
            }
            return s;
        }
        
        private String removeSmilesAndImages(final String s) {
            final StringBuilder sb = new StringBuilder();
            for (final Character value : s.toCharArray()) {
                if (!Character.isLowSurrogate(value) && !Character.isHighSurrogate(value)) {
                    sb.append(value);
                }
            }
            return sb.toString();
        }
    }
    
    class Connector implements FutureCallback<DiscordAPI>
    {
        public void onSuccess(final DiscordAPI discordAPI) {
            DebugLog.log(DebugType.Network, "*** DISCORD API CONNECTED ****");
            DiscordBot.this.setChannel(DiscordBot.this.currentChannelName, DiscordBot.this.currentChannelID);
            discordAPI.registerListener((de.btobastian.javacord.listener.Listener)new Listener());
            discordAPI.updateUsername(DiscordBot.this.name);
            if (DiscordBot.this.current != null) {
                DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION SUCCEEDED ****");
            }
            else {
                DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION FAILED ****");
            }
        }
        
        public void onFailure(final Throwable t) {
            t.printStackTrace();
        }
    }
}
