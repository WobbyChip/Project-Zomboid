// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioBroadCast;
import java.util.Iterator;
import zombie.radio.scripting.RadioChannel;
import zombie.input.GameKeyboard;
import java.util.Map;
import zombie.radio.scripting.RadioScriptManager;
import zombie.core.Color;
import java.util.HashMap;

public final class RadioDebugConsole
{
    private final HashMap<Integer, Boolean> state;
    private int channelIndex;
    private int testcounter;
    private Color colRed;
    private Color colGreen;
    private Color colWhite;
    private Color colGrey;
    private Color colDyn;
    private int drawY;
    private int drawX;
    private int drawYLine;
    
    public RadioDebugConsole() {
        this.state = new HashMap<Integer, Boolean>();
        this.channelIndex = 0;
        this.testcounter = 0;
        this.colRed = new Color(255, 0, 0, 255);
        this.colGreen = new Color(0, 255, 0, 255);
        this.colWhite = new Color(255, 255, 255, 255);
        this.colGrey = new Color(150, 150, 150, 255);
        this.colDyn = new Color(255, 255, 255, 255);
        this.drawY = 0;
        this.drawX = 0;
        this.drawYLine = 20;
        this.state.put(12, false);
        this.state.put(13, false);
        this.state.put(53, false);
        this.state.put(26, false);
    }
    
    public void update() {
        final Map<Integer, RadioChannel> channels = RadioScriptManager.getInstance().getChannels();
        for (final Map.Entry<Integer, Boolean> entry : this.state.entrySet()) {
            final boolean keyDown = GameKeyboard.isKeyDown(entry.getKey());
            if (keyDown && entry.getValue() != keyDown) {
                switch (entry.getKey()) {
                    case 12: {
                        --this.channelIndex;
                        if (this.channelIndex < 0 && channels != null) {
                            this.channelIndex = channels.size() - 1;
                            break;
                        }
                        break;
                    }
                    case 13: {
                        ++this.channelIndex;
                        if (channels != null && this.channelIndex >= channels.size()) {
                            this.channelIndex = 0;
                            break;
                        }
                        break;
                    }
                }
            }
            entry.setValue(keyDown);
        }
    }
    
    public void render() {
        final Map<Integer, RadioChannel> channels = RadioScriptManager.getInstance().getChannels();
        if (channels == null || channels.size() == 0) {
            return;
        }
        if (this.channelIndex < 0) {
            this.channelIndex = 0;
        }
        if (this.channelIndex >= channels.size()) {
            this.channelIndex = channels.size() - 1;
        }
        this.drawYLine = 20;
        this.drawX = 20;
        this.drawY = 200;
        final int n = 150;
        this.DrawLine("Scamble once: ", 0, false, this.colGrey);
        this.AddBlancLine();
        this.DrawLine("Radio Script Manager Debug.", 0, true);
        this.DrawLine("Real Time: ", 0, false, this.colGrey);
        this.DrawLine(timeStampToString(RadioScriptManager.getInstance().getCurrentTimeStamp()), n, true);
        this.AddBlancLine();
        this.AddBlancLine();
        this.DrawLine(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.channelIndex + 1, channels.size()), 0, true);
        final RadioChannel radioChannel = (RadioChannel)channels.values().toArray()[this.channelIndex];
        if (radioChannel != null) {
            this.DrawLine("Selected channel: ", 0, false, this.colGrey);
            this.DrawLine(radioChannel.GetName(), n, true);
            this.DrawLine("Type: ", 0, false, this.colGrey);
            this.DrawLine(radioChannel.IsTv() ? "Television" : "Radio", n, true);
            this.DrawLine("Frequency: ", 0, false, this.colGrey);
            this.DrawLine(Integer.toString(radioChannel.GetFrequency()), n, true);
            this.DrawLine("Category: ", 0, false, this.colGrey);
            this.DrawLine(radioChannel.GetCategory().toString(), n, true);
            this.DrawLine("PlayerListening: ", 0, false, this.colGrey);
            if (radioChannel.GetPlayerIsListening()) {
                this.DrawLine("Yes", n, true, this.colGreen);
            }
            else {
                this.DrawLine("No", n, true, this.colRed);
            }
            final RadioBroadCast airingBroadcast = radioChannel.getAiringBroadcast();
            if (airingBroadcast != null) {
                this.AddBlancLine();
                this.DrawLine("Is airing a broadcast:", 0, true, this.colGreen);
                this.DrawLine("ID: ", 0, false, this.colGrey);
                this.DrawLine(airingBroadcast.getID(), n, true);
                this.DrawLine("StartStamp: ", 0, false, this.colGrey);
                this.DrawLine(timeStampToString(airingBroadcast.getStartStamp()), n, true);
                this.DrawLine("EndStamp: ", 0, false, this.colGrey);
                this.DrawLine(timeStampToString(airingBroadcast.getEndStamp()), n, true);
                if (airingBroadcast.getCurrentLine() != null) {
                    this.colDyn.r = airingBroadcast.getCurrentLine().getR();
                    this.colDyn.g = airingBroadcast.getCurrentLine().getG();
                    this.colDyn.b = airingBroadcast.getCurrentLine().getB();
                    if (airingBroadcast.getCurrentLine().getText() != null) {
                        this.DrawLine("Next line to be aired: ", 0, false, this.colGrey);
                        this.DrawLine(airingBroadcast.PeekNextLineText(), n, true, this.colDyn);
                    }
                }
            }
            this.AddBlancLine();
            final RadioScript currentScript = radioChannel.getCurrentScript();
            if (currentScript != null) {
                this.DrawLine("Currently working on RadioScript: ", 0, true);
                this.DrawLine("Name: ", 0, false, this.colGrey);
                this.DrawLine(currentScript.GetName(), n, true);
                this.DrawLine("Start day: ", 0, false, this.colGrey);
                this.DrawLine(timeStampToString(currentScript.getStartDayStamp()), n, true);
                this.DrawLine("Current loop: ", 0, false, this.colGrey);
                this.DrawLine(Integer.toString(radioChannel.getCurrentScriptLoop()), n, true);
                this.DrawLine("Total loops: ", 0, false, this.colGrey);
                this.DrawLine(Integer.toString(radioChannel.getCurrentScriptMaxLoops()), n, true);
                final RadioBroadCast currentBroadcast = currentScript.getCurrentBroadcast();
                if (currentBroadcast != null) {
                    this.AddBlancLine();
                    this.DrawLine("Currently active broadcast:", 0, true);
                    this.DrawLine("ID: ", 0, false, this.colGrey);
                    this.DrawLine(currentBroadcast.getID(), n, true);
                    this.DrawLine("Real StartStamp: ", 0, false, this.colGrey);
                    this.DrawLine(timeStampToString(currentBroadcast.getStartStamp() + currentScript.getStartDayStamp()), n, true);
                    this.DrawLine("Real EndStamp: ", 0, false, this.colGrey);
                    this.DrawLine(timeStampToString(currentBroadcast.getEndStamp() + currentScript.getStartDayStamp()), n, true);
                    this.DrawLine("Script StartStamp: ", 0, false, this.colGrey);
                    this.DrawLine(timeStampToString(currentBroadcast.getStartStamp()), n, true);
                    this.DrawLine("Script EndStamp: ", 0, false, this.colGrey);
                    this.DrawLine(timeStampToString(currentBroadcast.getEndStamp()), n, true);
                    if (currentBroadcast.getCurrentLine() != null) {
                        this.colDyn.r = currentBroadcast.getCurrentLine().getR();
                        this.colDyn.g = currentBroadcast.getCurrentLine().getG();
                        this.colDyn.b = currentBroadcast.getCurrentLine().getB();
                        if (currentBroadcast.getCurrentLine().getText() != null) {
                            this.DrawLine("Next line to be aired: ", 0, false, this.colGrey);
                            this.DrawLine(currentBroadcast.PeekNextLineText(), n, true, this.colDyn);
                        }
                    }
                }
            }
        }
    }
    
    public static String timeStampToString(final int n) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Integer.toString(n / 1440), Integer.toString(n / 60 % 24), Integer.toString(n % 60));
    }
    
    private void AddBlancLine() {
        this.drawY += this.drawYLine;
    }
    
    private void DrawLine(final String s, final int n, final boolean b, final Color color) {
        TextManager.instance.DrawString(UIFont.Medium, this.drawX + n, this.drawY, s, color.r, color.g, color.b, color.a);
        if (b) {
            this.drawY += this.drawYLine;
        }
    }
    
    private void DrawLine(final String s, final int n, final boolean b) {
        this.DrawLine(s, n, b, this.colWhite);
    }
}
