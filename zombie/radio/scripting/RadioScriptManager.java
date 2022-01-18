// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.scripting;

import java.io.DataInputStream;
import java.io.BufferedReader;
import zombie.GameWindow;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.ZomboidRadio;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;

public final class RadioScriptManager
{
    private final Map<Integer, RadioChannel> channels;
    private static RadioScriptManager instance;
    private int currentTimeStamp;
    private ArrayList<RadioChannel> channelsList;
    
    public static boolean hasInstance() {
        return RadioScriptManager.instance != null;
    }
    
    public static RadioScriptManager getInstance() {
        if (RadioScriptManager.instance == null) {
            RadioScriptManager.instance = new RadioScriptManager();
        }
        return RadioScriptManager.instance;
    }
    
    private RadioScriptManager() {
        this.channels = new LinkedHashMap<Integer, RadioChannel>();
        this.currentTimeStamp = 0;
        this.channelsList = new ArrayList<RadioChannel>();
    }
    
    public void init(final int n) {
    }
    
    public Map<Integer, RadioChannel> getChannels() {
        return this.channels;
    }
    
    public ArrayList getChannelsList() {
        this.channelsList.clear();
        final Iterator<Map.Entry<Integer, RadioChannel>> iterator = this.channels.entrySet().iterator();
        while (iterator.hasNext()) {
            this.channelsList.add(iterator.next().getValue());
        }
        return this.channelsList;
    }
    
    public RadioChannel getRadioChannel(final String anObject) {
        for (final Map.Entry<Integer, RadioChannel> entry : this.channels.entrySet()) {
            if (entry.getValue().getGUID().equals(anObject)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public void simulateScriptsUntil(final int n, final boolean b) {
        final Iterator<Map.Entry<Integer, RadioChannel>> iterator = this.channels.entrySet().iterator();
        while (iterator.hasNext()) {
            this.simulateChannelUntil(iterator.next().getValue().GetFrequency(), n, b);
        }
    }
    
    public void simulateChannelUntil(final int n, final int n2, final boolean b) {
        if (this.channels.containsKey(n)) {
            final RadioChannel radioChannel = this.channels.get(n);
            if (radioChannel.isTimeSynced() && !b) {
                return;
            }
            for (int i = 0; i < n2; ++i) {
                radioChannel.UpdateScripts(this.currentTimeStamp, i * 24 * 60);
            }
            radioChannel.setTimeSynced(true);
        }
    }
    
    public int getCurrentTimeStamp() {
        return this.currentTimeStamp;
    }
    
    public void PlayerListensChannel(final int i, final boolean b, final boolean b2) {
        if (this.channels.containsKey(i) && this.channels.get(i).IsTv() == b2) {
            this.channels.get(i).SetPlayerIsListening(b);
        }
    }
    
    public void AddChannel(final RadioChannel radioChannel, final boolean b) {
        if (radioChannel != null && (b || !this.channels.containsKey(radioChannel.GetFrequency()))) {
            this.channels.put(radioChannel.GetFrequency(), radioChannel);
            ZomboidRadio.getInstance().addChannelName(radioChannel.GetName(), radioChannel.GetFrequency(), radioChannel.GetCategory().name(), b);
        }
        else {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (radioChannel != null) ? radioChannel.GetName() : "null"));
        }
    }
    
    public void RemoveChannel(final int n) {
        if (this.channels.containsKey(n)) {
            this.channels.remove(n);
            ZomboidRadio.getInstance().removeChannelName(n);
        }
    }
    
    public void UpdateScripts(final int n, final int n2, final int n3) {
        this.currentTimeStamp = n * 24 * 60 + n2 * 60 + n3;
        final Iterator<Map.Entry<Integer, RadioChannel>> iterator = this.channels.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().UpdateScripts(this.currentTimeStamp, n);
        }
    }
    
    public void update() {
        final Iterator<Map.Entry<Integer, RadioChannel>> iterator = this.channels.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().update();
        }
    }
    
    public void reset() {
        RadioScriptManager.instance = null;
    }
    
    public void Save(final Writer writer) throws IOException {
        for (final Map.Entry<Integer, RadioChannel> entry : this.channels.entrySet()) {
            writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;II)Ljava/lang/String;, entry.getKey(), entry.getValue().getCurrentScriptLoop(), entry.getValue().getCurrentScriptMaxLoops()));
            final RadioScript currentScript = entry.getValue().getCurrentScript();
            if (currentScript != null) {
                writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, currentScript.GetName(), currentScript.getStartDay()));
            }
            final RadioBroadCast airingBroadcast = entry.getValue().getAiringBroadcast();
            if (airingBroadcast != null) {
                writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, airingBroadcast.getID()));
            }
            else if (entry.getValue().getLastBroadcastID() != null) {
                writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, entry.getValue().getLastBroadcastID()));
            }
            else {
                writer.write(",none");
            }
            writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (airingBroadcast != null) ? invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, airingBroadcast.getCurrentLineNumber()) : "-1"));
            writer.write(System.lineSeparator());
        }
    }
    
    public void SaveOLD(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.channels.size());
        for (final Map.Entry<Integer, RadioChannel> entry : this.channels.entrySet()) {
            dataOutputStream.writeInt(entry.getKey());
            dataOutputStream.writeInt(entry.getValue().getCurrentScriptLoop());
            dataOutputStream.writeInt(entry.getValue().getCurrentScriptMaxLoops());
            final RadioScript currentScript = entry.getValue().getCurrentScript();
            dataOutputStream.writeByte((currentScript != null) ? 1 : 0);
            if (currentScript != null) {
                GameWindow.WriteString(dataOutputStream, currentScript.GetName());
                dataOutputStream.writeInt(currentScript.getStartDay());
            }
        }
    }
    
    public void Load(final BufferedReader bufferedReader) throws IOException {
        int int1 = 1;
        int int2 = 1;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            RadioChannel radioChannel = null;
            final String[] split = line.trim().split(",");
            if (split.length >= 3) {
                final int int3 = Integer.parseInt(split[0]);
                int1 = Integer.parseInt(split[1]);
                int2 = Integer.parseInt(split[2]);
                if (this.channels.containsKey(int3)) {
                    radioChannel = this.channels.get(int3);
                    radioChannel.setTimeSynced(true);
                }
            }
            if (radioChannel != null && split.length >= 5) {
                final String s = split[3];
                final int int4 = Integer.parseInt(split[4]);
                if (radioChannel != null) {
                    radioChannel.setActiveScript(s, int4, int1, int2);
                }
            }
            if (radioChannel != null && split.length >= 7) {
                final String s2 = split[5];
                if (s2.equals("none")) {
                    continue;
                }
                radioChannel.LoadAiringBroadcast(s2, Integer.parseInt(split[6]));
            }
        }
    }
    
    public void LoadOLD(final DataInputStream dataInputStream) throws IOException {
        final Iterator<Map.Entry<Integer, RadioChannel>> iterator = this.channels.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().setActiveScriptNull();
        }
        for (int int1 = dataInputStream.readInt(), i = 0; i < int1; ++i) {
            RadioChannel radioChannel = null;
            final int int2 = dataInputStream.readInt();
            if (this.channels.containsKey(int2)) {
                radioChannel = this.channels.get(int2);
                radioChannel.setTimeSynced(true);
            }
            final int int3 = dataInputStream.readInt();
            final int int4 = dataInputStream.readInt();
            if (dataInputStream.readByte() == 1) {
                final String readString = GameWindow.ReadString(dataInputStream);
                final int int5 = dataInputStream.readInt();
                if (radioChannel != null) {
                    radioChannel.setActiveScript(readString, int5, int3, int4);
                }
            }
        }
    }
}
