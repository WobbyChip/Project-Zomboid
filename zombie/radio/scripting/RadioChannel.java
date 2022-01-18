// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.scripting;

import zombie.core.math.PZMath;
import zombie.radio.ZomboidRadio;
import zombie.GameTime;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Rand;
import java.util.HashMap;
import java.util.UUID;
import zombie.radio.ChannelCategory;
import java.util.Map;
import zombie.radio.RadioData;

public class RadioChannel
{
    private String GUID;
    private RadioData radioData;
    private boolean isTimeSynced;
    private Map<String, RadioScript> scripts;
    private int frequency;
    private String name;
    private boolean isTv;
    private ChannelCategory category;
    private boolean playerIsListening;
    private RadioScript currentScript;
    private int currentScriptLoop;
    private int currentScriptMaxLoops;
    private RadioBroadCast airingBroadcast;
    private float airCounter;
    private String lastAiredLine;
    private String lastBroadcastID;
    private float airCounterMultiplier;
    private boolean louisvilleObfuscate;
    float minmod;
    float maxmod;
    
    public RadioChannel(final String s, final int n, final ChannelCategory channelCategory) {
        this(s, n, channelCategory, UUID.randomUUID().toString());
    }
    
    public RadioChannel(final String name, final int frequency, final ChannelCategory category, final String guid) {
        this.isTimeSynced = false;
        this.scripts = new HashMap<String, RadioScript>();
        this.frequency = -1;
        this.name = "Unnamed channel";
        this.isTv = false;
        this.category = ChannelCategory.Undefined;
        this.playerIsListening = false;
        this.currentScript = null;
        this.currentScriptLoop = 1;
        this.currentScriptMaxLoops = 1;
        this.airingBroadcast = null;
        this.airCounter = 0.0f;
        this.lastAiredLine = "";
        this.lastBroadcastID = null;
        this.airCounterMultiplier = 1.0f;
        this.louisvilleObfuscate = false;
        this.minmod = 1.5f;
        this.maxmod = 5.0f;
        this.name = name;
        this.frequency = frequency;
        this.category = category;
        this.isTv = (this.category == ChannelCategory.Television);
        this.GUID = guid;
    }
    
    public String getGUID() {
        return this.GUID;
    }
    
    public int GetFrequency() {
        return this.frequency;
    }
    
    public String GetName() {
        return this.name;
    }
    
    public boolean IsTv() {
        return this.isTv;
    }
    
    public ChannelCategory GetCategory() {
        return this.category;
    }
    
    public RadioScript getCurrentScript() {
        return this.currentScript;
    }
    
    public RadioBroadCast getAiringBroadcast() {
        return this.airingBroadcast;
    }
    
    public String getLastAiredLine() {
        return this.lastAiredLine;
    }
    
    public int getCurrentScriptLoop() {
        return this.currentScriptLoop;
    }
    
    public int getCurrentScriptMaxLoops() {
        return this.currentScriptMaxLoops;
    }
    
    public String getLastBroadcastID() {
        return this.lastBroadcastID;
    }
    
    public RadioData getRadioData() {
        return this.radioData;
    }
    
    public void setRadioData(final RadioData radioData) {
        this.radioData = radioData;
    }
    
    public boolean isTimeSynced() {
        return this.isTimeSynced;
    }
    
    public void setTimeSynced(final boolean isTimeSynced) {
        this.isTimeSynced = isTimeSynced;
    }
    
    public boolean isVanilla() {
        return this.radioData == null || this.radioData.isVanilla();
    }
    
    public void setLouisvilleObfuscate(final boolean louisvilleObfuscate) {
        this.louisvilleObfuscate = louisvilleObfuscate;
    }
    
    public void LoadAiringBroadcast(final String s, final int currentLineNumber) {
        if (this.currentScript != null) {
            this.airingBroadcast = this.currentScript.getBroadcastWithID(s);
            if (currentLineNumber < 0) {
                this.airingBroadcast = null;
            }
            if (this.airingBroadcast != null && currentLineNumber >= 0) {
                this.airingBroadcast.resetLineCounter();
                this.airingBroadcast.setCurrentLineNumber(currentLineNumber);
                this.airCounter = 120.0f;
                this.playerIsListening = true;
            }
        }
    }
    
    public void SetPlayerIsListening(final boolean playerIsListening) {
        this.playerIsListening = playerIsListening;
        if (this.playerIsListening && this.airingBroadcast == null && this.currentScript != null) {
            this.airingBroadcast = this.currentScript.getValidAirBroadcast();
            if (this.airingBroadcast != null) {
                this.airingBroadcast.resetLineCounter();
            }
            this.airCounter = 0.0f;
        }
    }
    
    public boolean GetPlayerIsListening() {
        return this.playerIsListening;
    }
    
    public void setActiveScriptNull() {
        this.currentScript = null;
        this.airingBroadcast = null;
    }
    
    public void setActiveScript(final String s, final int n) {
        this.setActiveScript(s, n, 1, -1);
    }
    
    public void setActiveScript(final String s, final int startDayStamp, final int currentScriptLoop, int next) {
        if (s != null && this.scripts.containsKey(s)) {
            this.currentScript = this.scripts.get(s);
            if (this.currentScript != null) {
                this.currentScript.Reset();
                this.currentScript.setStartDayStamp(startDayStamp);
                this.currentScriptLoop = currentScriptLoop;
                if (next == -1) {
                    final int loopMin = this.currentScript.getLoopMin();
                    final int loopMax = this.currentScript.getLoopMax();
                    if (loopMin == loopMax || loopMin > loopMax) {
                        next = loopMin;
                    }
                    else {
                        next = Rand.Next(loopMin, loopMax);
                    }
                }
                this.currentScriptMaxLoops = next;
                if (DebugLog.isEnabled(DebugType.Radio)) {
                    DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, s, startDayStamp, this.currentScript.getLoopMin(), this.currentScriptMaxLoops));
                }
            }
        }
    }
    
    private void getNextScript(final int startDayStamp) {
        if (this.currentScript != null) {
            if (this.currentScriptLoop < this.currentScriptMaxLoops) {
                ++this.currentScriptLoop;
                this.currentScript.Reset();
                this.currentScript.setStartDayStamp(startDayStamp);
            }
            else {
                final RadioScript.ExitOption nextScript = this.currentScript.getNextScript();
                this.currentScript = null;
                if (nextScript != null) {
                    this.setActiveScript(nextScript.getScriptname(), startDayStamp + nextScript.getStartDelay());
                }
            }
        }
    }
    
    public void UpdateScripts(final int n, final int n2) {
        this.playerIsListening = false;
        if (this.currentScript != null && !this.currentScript.UpdateScript(n)) {
            this.getNextScript(n2 + 1);
        }
    }
    
    public void update() {
        if (this.airingBroadcast != null) {
            this.airCounter -= 1.25f * GameTime.getInstance().getMultiplier();
            if (this.airCounter < 0.0f) {
                final RadioLine nextLine = this.airingBroadcast.getNextLine();
                if (nextLine == null) {
                    this.lastBroadcastID = this.airingBroadcast.getID();
                    this.airingBroadcast = null;
                    this.playerIsListening = false;
                }
                else {
                    this.lastAiredLine = nextLine.getText();
                    if (!ZomboidRadio.DISABLE_BROADCASTING) {
                        final String text = nextLine.getText();
                        if (this.louisvilleObfuscate && ZomboidRadio.LOUISVILLE_OBFUSCATION) {
                            ZomboidRadio.getInstance().SendTransmission(0, 0, this.frequency, ZomboidRadio.getInstance().scrambleString(text, 85, true, null), "", 0.7f, 0.5f, 0.5f, -1, this.isTv);
                        }
                        else {
                            ZomboidRadio.getInstance().SendTransmission(0, 0, this.frequency, text, nextLine.getEffectsString(), nextLine.getR(), nextLine.getG(), nextLine.getB(), -1, this.isTv);
                        }
                    }
                    if (nextLine.isCustomAirTime()) {
                        this.airCounter = nextLine.getAirTime() * 60.0f;
                    }
                    else {
                        this.airCounter = nextLine.getText().length() / 10.0f * 60.0f;
                        if (this.airCounter < 60.0f * this.minmod) {
                            this.airCounter = 60.0f * this.minmod;
                        }
                        else if (this.airCounter > 60.0f * this.maxmod) {
                            this.airCounter = 60.0f * this.maxmod;
                        }
                        this.airCounter *= this.airCounterMultiplier;
                    }
                }
            }
        }
    }
    
    public void AddRadioScript(final RadioScript radioScript) {
        if (radioScript != null && !this.scripts.containsKey(radioScript.GetName())) {
            this.scripts.put(radioScript.GetName(), radioScript);
        }
        else {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (radioScript != null) ? radioScript.GetName() : "null"));
        }
    }
    
    public RadioScript getRadioScript(final String s) {
        if (s != null && this.scripts.containsKey(s)) {
            return this.scripts.get(s);
        }
        return null;
    }
    
    public void setAiringBroadcast(final RadioBroadCast airingBroadcast) {
        this.airingBroadcast = airingBroadcast;
    }
    
    public float getAirCounterMultiplier() {
        return this.airCounterMultiplier;
    }
    
    public void setAirCounterMultiplier(final float n) {
        this.airCounterMultiplier = PZMath.clamp(n, 0.1f, 10.0f);
    }
}
