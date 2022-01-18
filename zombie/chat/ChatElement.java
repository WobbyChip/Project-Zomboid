// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.radio.ZomboidRadio;
import zombie.vehicles.VehiclePart;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoTelevision;
import zombie.ui.UIFont;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import java.util.HashSet;
import zombie.ui.TextDrawObject;
import zombie.characters.Talker;

public class ChatElement implements Talker
{
    protected PlayerLines[] playerLines;
    protected ChatElementOwner owner;
    protected float historyVal;
    protected boolean historyInRange;
    protected float historyRange;
    protected boolean useEuclidean;
    protected boolean hasChatToDisplay;
    protected int maxChatLines;
    protected int maxCharsPerLine;
    protected String sayLine;
    protected String sayLineTag;
    protected TextDrawObject sayLineObject;
    protected boolean Speaking;
    protected String talkerType;
    public static boolean doBackDrop;
    public static NineGridTexture backdropTexture;
    private int bufferX;
    private int bufferY;
    private static PlayerLinesList[] renderBatch;
    private static HashSet<String> noLogText;
    
    public ChatElement(final ChatElementOwner owner, final int maxChatLines, final String s) {
        this.playerLines = new PlayerLines[4];
        this.historyVal = 1.0f;
        this.historyInRange = false;
        this.historyRange = 15.0f;
        this.useEuclidean = true;
        this.hasChatToDisplay = false;
        this.maxChatLines = -1;
        this.maxCharsPerLine = -1;
        this.sayLine = null;
        this.sayLineTag = null;
        this.sayLineObject = null;
        this.Speaking = false;
        this.talkerType = "unknown";
        this.bufferX = 0;
        this.bufferY = 0;
        this.owner = owner;
        this.setMaxChatLines(maxChatLines);
        this.setMaxCharsPerLine(75);
        this.talkerType = ((s != null) ? s : this.talkerType);
        if (ChatElement.backdropTexture == null) {
            ChatElement.backdropTexture = new NineGridTexture("NineGridBlack", 5);
        }
    }
    
    public void setMaxChatLines(int maxChatLines) {
        maxChatLines = ((maxChatLines < 1) ? 1 : ((maxChatLines > 10) ? 10 : maxChatLines));
        if (maxChatLines != this.maxChatLines) {
            this.maxChatLines = maxChatLines;
            for (int i = 0; i < this.playerLines.length; ++i) {
                this.playerLines[i] = new PlayerLines(this.maxChatLines);
            }
        }
    }
    
    public int getMaxChatLines() {
        return this.maxChatLines;
    }
    
    public void setMaxCharsPerLine(final int n) {
        for (int i = 0; i < this.playerLines.length; ++i) {
            this.playerLines[i].setMaxCharsPerLine(n);
        }
        this.maxCharsPerLine = n;
    }
    
    @Override
    public boolean IsSpeaking() {
        return this.Speaking;
    }
    
    @Override
    public String getTalkerType() {
        return this.talkerType;
    }
    
    public void setTalkerType(final String s) {
        this.talkerType = ((s == null) ? "" : s);
    }
    
    @Override
    public String getSayLine() {
        return this.sayLine;
    }
    
    public String getSayLineTag() {
        if (this.Speaking && this.sayLineTag != null) {
            return this.sayLineTag;
        }
        return "";
    }
    
    public void setHistoryRange(final float historyRange) {
        this.historyRange = historyRange;
    }
    
    public void setUseEuclidean(final boolean useEuclidean) {
        this.useEuclidean = useEuclidean;
    }
    
    public boolean getHasChatToDisplay() {
        return this.hasChatToDisplay;
    }
    
    protected float getDistance(final IsoPlayer isoPlayer) {
        if (isoPlayer == null) {
            return -1.0f;
        }
        if (this.useEuclidean) {
            return (float)Math.sqrt(Math.pow(this.owner.getX() - isoPlayer.x, 2.0) + Math.pow(this.owner.getY() - isoPlayer.y, 2.0));
        }
        return Math.abs(this.owner.getX() - isoPlayer.x) + Math.abs(this.owner.getY() - isoPlayer.y);
    }
    
    protected boolean playerWithinBounds(final IsoPlayer isoPlayer, final float n) {
        return isoPlayer != null && isoPlayer.getX() > this.owner.getX() - n && isoPlayer.getX() < this.owner.getX() + n && isoPlayer.getY() > this.owner.getY() - n && isoPlayer.getY() < this.owner.getY() + n;
    }
    
    public void SayDebug(final int n, final String s) {
        if (!GameServer.bServer && n >= 0 && n < this.maxChatLines) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (IsoPlayer.players[i] != null) {
                    final PlayerLines playerLines = this.playerLines[i];
                    if (n < playerLines.chatLines.length) {
                        if (playerLines.chatLines[n].getOriginal() != null && playerLines.chatLines[n].getOriginal().equals(s)) {
                            playerLines.chatLines[n].setInternalTickClock((float)playerLines.lineDisplayTime);
                        }
                        else {
                            playerLines.chatLines[n].setSettings(true, true, true, true, true, true);
                            playerLines.chatLines[n].setInternalTickClock((float)playerLines.lineDisplayTime);
                            playerLines.chatLines[n].setCustomTag("default");
                            playerLines.chatLines[n].setDefaultColors(1.0f, 1.0f, 1.0f, 1.0f);
                            playerLines.chatLines[n].ReadString(UIFont.Medium, s, this.maxCharsPerLine);
                        }
                    }
                }
            }
            this.sayLine = s;
            this.sayLineTag = "default";
            this.hasChatToDisplay = true;
        }
    }
    
    @Override
    public void Say(final String s) {
        this.addChatLine(s, 1.0f, 1.0f, 1.0f, UIFont.Dialogue, 25.0f, "default", false, false, false, false, false, true);
    }
    
    public void addChatLine(final String s, final float n, final float n2, final float n3, final float n4) {
        this.addChatLine(s, n, n2, n3, UIFont.Dialogue, n4, "default", false, false, false, false, false, true);
    }
    
    public void addChatLine(final String s, final float n, final float n2, final float n3) {
        this.addChatLine(s, n, n2, n3, UIFont.Dialogue, 25.0f, "default", false, false, false, false, false, true);
    }
    
    public void addChatLine(final String sayLine, final float n, final float n2, final float n3, final UIFont uiFont, final float n4, final String s, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6) {
        if (!GameServer.bServer) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    if (isoPlayer.Traits.Deaf.isSet()) {
                        if (this.owner instanceof IsoTelevision) {
                            if (!((IsoTelevision)this.owner).isFacing(isoPlayer)) {
                                continue;
                            }
                        }
                        else {
                            if (this.owner instanceof IsoRadio) {
                                continue;
                            }
                            if (this.owner instanceof VehiclePart) {
                                continue;
                            }
                        }
                    }
                    final float scrambleValue = this.getScrambleValue(isoPlayer, n4);
                    if (scrambleValue < 1.0f) {
                        final PlayerLines playerLines = this.playerLines[i];
                        final TextDrawObject newLineObject = playerLines.getNewLineObject();
                        if (newLineObject != null) {
                            newLineObject.setSettings(b, b2, b3, b4, b5, b6);
                            newLineObject.setInternalTickClock((float)playerLines.lineDisplayTime);
                            newLineObject.setCustomTag(s);
                            String scrambleString;
                            if (scrambleValue > 0.0f) {
                                scrambleString = ZomboidRadio.getInstance().scrambleString(sayLine, (int)(100.0f * scrambleValue), true, "...");
                                newLineObject.setDefaultColors(0.5f, 0.5f, 0.5f, 1.0f);
                            }
                            else {
                                scrambleString = sayLine;
                                newLineObject.setDefaultColors(n, n2, n3, 1.0f);
                            }
                            newLineObject.ReadString(uiFont, scrambleString, this.maxCharsPerLine);
                            this.sayLine = sayLine;
                            this.sayLineTag = s;
                            this.hasChatToDisplay = true;
                        }
                    }
                }
            }
        }
    }
    
    protected float getScrambleValue(final IsoPlayer isoPlayer, final float n) {
        if (this.owner == isoPlayer) {
            return 0.0f;
        }
        float n2 = 1.0f;
        boolean b = false;
        boolean b2 = false;
        if (this.owner.getSquare() != null && isoPlayer.getSquare() != null) {
            if (isoPlayer.getBuilding() != null && this.owner.getSquare().getBuilding() != null && isoPlayer.getBuilding() == this.owner.getSquare().getBuilding()) {
                if (isoPlayer.getSquare().getRoom() == this.owner.getSquare().getRoom()) {
                    n2 *= 2.0;
                    b2 = true;
                }
                else if (Math.abs(isoPlayer.getZ() - this.owner.getZ()) < 1.0f) {
                    n2 *= 2.0;
                }
            }
            else if (isoPlayer.getBuilding() != null || this.owner.getSquare().getBuilding() != null) {
                n2 *= 0.5;
                b = true;
            }
            if (Math.abs(isoPlayer.getZ() - this.owner.getZ()) >= 1.0f) {
                n2 -= (float)(n2 * (Math.abs(isoPlayer.getZ() - this.owner.getZ()) * 0.25));
                b = true;
            }
        }
        final float n3 = n * n2;
        float n4 = 1.0f;
        if (n2 > 0.0f && this.playerWithinBounds(isoPlayer, n3)) {
            final float distance = this.getDistance(isoPlayer);
            if (distance >= 0.0f && distance < n3) {
                final float n5 = n3 * 0.6f;
                if (b2 || (!b && distance < n5)) {
                    n4 = 0.0f;
                }
                else if (n3 - n5 != 0.0f) {
                    n4 = (distance - n5) / (n3 - n5);
                    if (n4 < 0.2f) {
                        n4 = 0.2f;
                    }
                }
            }
        }
        return n4;
    }
    
    protected void updateChatLines() {
        this.Speaking = false;
        int n = 0;
        if (this.hasChatToDisplay) {
            this.hasChatToDisplay = false;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                float n2 = 1.25f * GameTime.getInstance().getMultiplier();
                final int lineDisplayTime = this.playerLines[i].lineDisplayTime;
                for (final TextDrawObject textDrawObject : this.playerLines[i].chatLines) {
                    final float updateInternalTickClock = textDrawObject.updateInternalTickClock(n2);
                    if (updateInternalTickClock > 0.0f) {
                        this.hasChatToDisplay = true;
                        if (n == 0 && !textDrawObject.getCustomTag().equals("radio")) {
                            if (updateInternalTickClock / (lineDisplayTime / 2.0f) >= 1.0f) {
                                this.Speaking = true;
                            }
                            n = 1;
                        }
                        n2 *= 1.2f;
                    }
                }
            }
        }
        if (!this.Speaking) {
            this.sayLine = null;
            this.sayLineTag = null;
        }
    }
    
    protected void updateHistory() {
        if (this.hasChatToDisplay) {
            this.historyInRange = false;
            final IsoPlayer instance = IsoPlayer.getInstance();
            if (instance != null) {
                if (instance == this.owner) {
                    this.historyVal = 1.0f;
                }
                else {
                    if (this.playerWithinBounds(instance, this.historyRange)) {
                        this.historyInRange = true;
                    }
                    else {
                        this.historyInRange = false;
                    }
                    if (this.historyInRange && this.historyVal != 1.0f) {
                        this.historyVal += 0.04f;
                        if (this.historyVal > 1.0f) {
                            this.historyVal = 1.0f;
                        }
                    }
                    if (!this.historyInRange && this.historyVal != 0.0f) {
                        this.historyVal -= 0.04f;
                        if (this.historyVal < 0.0f) {
                            this.historyVal = 0.0f;
                        }
                    }
                }
            }
        }
        else if (this.historyVal != 0.0f) {
            this.historyVal = 0.0f;
        }
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        this.updateChatLines();
        this.updateHistory();
    }
    
    public void renderBatched(final int n, final int n2, final int n3) {
        this.renderBatched(n, n2, n3, false);
    }
    
    public void renderBatched(final int n, final int renderX, final int renderY, final boolean ignoreRadioLines) {
        if (n < this.playerLines.length && this.hasChatToDisplay && !GameServer.bServer) {
            this.playerLines[n].renderX = renderX;
            this.playerLines[n].renderY = renderY;
            this.playerLines[n].ignoreRadioLines = ignoreRadioLines;
            if (ChatElement.renderBatch[n] == null) {
                ChatElement.renderBatch[n] = new PlayerLinesList();
            }
            ChatElement.renderBatch[n].add(this.playerLines[n]);
        }
    }
    
    public void clear(final int n) {
        this.playerLines[n].clear();
    }
    
    public static void RenderBatch(final int n) {
        if (ChatElement.renderBatch[n] != null && ChatElement.renderBatch[n].size() > 0) {
            for (int i = 0; i < ChatElement.renderBatch[n].size(); ++i) {
                ChatElement.renderBatch[n].get(i).render();
            }
            ChatElement.renderBatch[n].clear();
        }
    }
    
    public static void NoRender(final int n) {
        if (ChatElement.renderBatch[n] != null) {
            ChatElement.renderBatch[n].clear();
        }
    }
    
    public static void addNoLogText(final String e) {
        if (e == null || e.isEmpty()) {
            return;
        }
        ChatElement.noLogText.add(e);
    }
    
    static {
        ChatElement.doBackDrop = true;
        ChatElement.renderBatch = new PlayerLinesList[4];
        ChatElement.noLogText = new HashSet<String>();
    }
    
    class PlayerLines
    {
        protected int lineDisplayTime;
        protected int renderX;
        protected int renderY;
        protected boolean ignoreRadioLines;
        protected TextDrawObject[] chatLines;
        
        public PlayerLines(final int n) {
            this.lineDisplayTime = 314;
            this.renderX = 0;
            this.renderY = 0;
            this.ignoreRadioLines = false;
            this.chatLines = new TextDrawObject[n];
            for (int i = 0; i < this.chatLines.length; ++i) {
                (this.chatLines[i] = new TextDrawObject(0, 0, 0, true, true, true, true, true, true)).setDefaultFont(UIFont.Medium);
            }
        }
        
        public void setMaxCharsPerLine(final int maxCharsPerLine) {
            for (int i = 0; i < this.chatLines.length; ++i) {
                this.chatLines[i].setMaxCharsPerLine(maxCharsPerLine);
            }
        }
        
        public TextDrawObject getNewLineObject() {
            if (this.chatLines != null && this.chatLines.length > 0) {
                final TextDrawObject textDrawObject = this.chatLines[this.chatLines.length - 1];
                textDrawObject.Clear();
                for (int i = this.chatLines.length - 1; i > 0; --i) {
                    this.chatLines[i] = this.chatLines[i - 1];
                }
                return this.chatLines[0] = textDrawObject;
            }
            return null;
        }
        
        public void render() {
            if (GameServer.bServer) {
                return;
            }
            if (ChatElement.this.hasChatToDisplay) {
                int n = 0;
                for (final TextDrawObject textDrawObject : this.chatLines) {
                    if (textDrawObject.getEnabled()) {
                        if (textDrawObject.getWidth() <= 0 || textDrawObject.getHeight() <= 0) {
                            ++n;
                        }
                        else {
                            final float internalClock = textDrawObject.getInternalClock();
                            if (internalClock > 0.0f) {
                                if (!textDrawObject.getCustomTag().equals("radio") || !this.ignoreRadioLines) {
                                    float n2 = internalClock / (this.lineDisplayTime / 4.0f);
                                    if (n2 > 1.0f) {
                                        n2 = 1.0f;
                                    }
                                    this.renderY -= textDrawObject.getHeight() + 1;
                                    final boolean b = textDrawObject.getDefaultFontEnum() != UIFont.Dialogue;
                                    if (ChatElement.doBackDrop && ChatElement.backdropTexture != null) {
                                        ChatElement.backdropTexture.renderInnerBased(this.renderX - textDrawObject.getWidth() / 2, this.renderY, textDrawObject.getWidth(), textDrawObject.getHeight(), 0.0f, 0.0f, 0.0f, 0.4f);
                                    }
                                    if (n == 0) {
                                        textDrawObject.Draw(this.renderX, this.renderY, b, n2);
                                    }
                                    else if (ChatElement.this.historyVal > 0.0f) {
                                        textDrawObject.Draw(this.renderX, this.renderY, b, n2 * ChatElement.this.historyVal);
                                    }
                                    ++n;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        void clear() {
            if (!ChatElement.this.hasChatToDisplay) {
                return;
            }
            ChatElement.this.hasChatToDisplay = false;
            for (int i = 0; i < this.chatLines.length; ++i) {
                if (this.chatLines[i].getInternalClock() > 0.0f) {
                    this.chatLines[i].Clear();
                    this.chatLines[i].updateInternalTickClock(this.chatLines[i].getInternalClock());
                }
            }
            ChatElement.this.historyInRange = false;
            ChatElement.this.historyVal = 0.0f;
        }
    }
    
    class PlayerLinesList extends ArrayList<PlayerLines>
    {
    }
}
