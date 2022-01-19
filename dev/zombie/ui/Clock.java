// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.ArrayList;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.AlarmClock;
import zombie.characters.IsoGameCharacter;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.GameTime;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.textures.Texture;

public final class Clock extends UIElement
{
    Texture background;
    Texture[] digitsLarge;
    Texture[] digitsSmall;
    Texture colon;
    Texture slash;
    Texture minus;
    Texture dot;
    Texture tempC;
    Texture tempF;
    Texture tempE;
    Texture texAM;
    Texture texPM;
    Texture alarmOn;
    Texture alarmRinging;
    Color displayColour;
    Color ghostColour;
    int uxOriginal;
    int uyOriginal;
    int largeDigitSpacing;
    int smallDigitSpacing;
    int colonSpacing;
    int ampmSpacing;
    int alarmBellSpacing;
    int decimalSpacing;
    int degreeSpacing;
    int slashSpacing;
    int tempDateSpacing;
    int dateOffset;
    int minusOffset;
    int amVerticalSpacing;
    int pmVerticalSpacing;
    int alarmBellVerticalSpacing;
    int displayVerticalSpacing;
    int decimalVerticalSpacing;
    public boolean digital;
    public boolean isAlarmSet;
    public boolean isAlarmRinging;
    private IsoPlayer clockPlayer;
    public static Clock instance;
    
    public Clock(final int n, final int n2) {
        this.background = null;
        this.colon = null;
        this.slash = null;
        this.minus = null;
        this.dot = null;
        this.tempC = null;
        this.tempF = null;
        this.tempE = null;
        this.texAM = null;
        this.texPM = null;
        this.alarmOn = null;
        this.alarmRinging = null;
        this.displayColour = new Color(100, 200, 210, 255);
        this.ghostColour = new Color(40, 40, 40, 128);
        this.digital = false;
        this.isAlarmSet = false;
        this.isAlarmRinging = false;
        this.clockPlayer = null;
        this.x = n;
        this.y = n2;
        Clock.instance = this;
    }
    
    @Override
    public void render() {
        if (!this.visible) {
            return;
        }
        this.assignTextures(Core.getInstance().getOptionClockSize() == 2);
        this.DrawTexture(this.background, 0.0, 0.0, 0.75);
        this.renderDisplay(true, this.ghostColour);
        this.renderDisplay(false, this.displayColour);
        super.render();
    }
    
    private void renderDisplay(final boolean b, final Color color) {
        int uxOriginal = this.uxOriginal;
        final int uyOriginal = this.uyOriginal;
        for (int i = 0; i < 4; ++i) {
            final int[] timeDigits = this.timeDigits();
            if (b) {
                this.DrawTextureCol(this.digitsLarge[8], uxOriginal, uyOriginal, color);
            }
            else {
                this.DrawTextureCol(this.digitsLarge[timeDigits[i]], uxOriginal, uyOriginal, color);
            }
            uxOriginal += this.digitsLarge[0].getWidth();
            if (i == 1) {
                final int n = uxOriginal + this.colonSpacing;
                this.DrawTextureCol(this.colon, n, uyOriginal, color);
                uxOriginal = n + (this.colon.getWidth() + this.colonSpacing);
            }
            else if (i < 3) {
                uxOriginal += this.largeDigitSpacing;
            }
        }
        final int n2 = uxOriginal + this.ampmSpacing;
        if (!Core.getInstance().getOptionClock24Hour() || b) {
            if (b) {
                this.DrawTextureCol(this.texAM, n2, uyOriginal + this.amVerticalSpacing, color);
                this.DrawTextureCol(this.texPM, n2, uyOriginal + this.pmVerticalSpacing, color);
            }
            else if (GameTime.getInstance().getTimeOfDay() < 12.0f) {
                this.DrawTextureCol(this.texAM, n2, uyOriginal + this.amVerticalSpacing, color);
            }
            else {
                this.DrawTextureCol(this.texPM, n2, uyOriginal + this.pmVerticalSpacing, color);
            }
        }
        if (this.isAlarmRinging || b) {
            this.DrawTextureCol(this.alarmRinging, n2 + this.texAM.getWidth() + this.alarmBellSpacing, uyOriginal + this.alarmBellVerticalSpacing, color);
        }
        else if (this.isAlarmSet) {
            this.DrawTextureCol(this.alarmOn, n2 + this.texAM.getWidth() + this.alarmBellSpacing, uyOriginal + this.alarmBellVerticalSpacing, color);
        }
        if (this.digital || b) {
            final int uxOriginal2 = this.uxOriginal;
            final int n3 = uyOriginal + (this.digitsLarge[0].getHeight() + this.displayVerticalSpacing);
            int n9;
            if (this.clockPlayer != null) {
                final int[] tempDigits = this.tempDigits();
                if (tempDigits[0] == 1 || b) {
                    this.DrawTextureCol(this.minus, uxOriginal2, n3, color);
                }
                final int n4 = uxOriginal2 + this.minusOffset;
                if (tempDigits[1] == 1 || b) {
                    this.DrawTextureCol(this.digitsSmall[1], n4, n3, color);
                }
                int n5 = n4 + (this.digitsSmall[0].getWidth() + this.smallDigitSpacing);
                for (int j = 2; j < 5; ++j) {
                    if (b) {
                        this.DrawTextureCol(this.digitsSmall[8], n5, n3, color);
                    }
                    else {
                        this.DrawTextureCol(this.digitsSmall[tempDigits[j]], n5, n3, color);
                    }
                    n5 += this.digitsSmall[0].getWidth();
                    if (j == 3) {
                        final int n6 = n5 + this.decimalSpacing;
                        this.DrawTextureCol(this.dot, n6, n3 + this.decimalVerticalSpacing, color);
                        n5 = n6 + (this.dot.getWidth() + this.decimalSpacing);
                    }
                    else if (j < 4) {
                        n5 += this.smallDigitSpacing;
                    }
                }
                final int n7 = n5 + this.degreeSpacing;
                this.DrawTextureCol(this.dot, n7, n3, color);
                final int n8 = n7 + (this.dot.getWidth() + this.degreeSpacing);
                if (b) {
                    this.DrawTextureCol(this.tempE, n8, n3, color);
                }
                else if (tempDigits[5] == 0) {
                    this.DrawTextureCol(this.tempC, n8, n3, color);
                }
                else {
                    this.DrawTextureCol(this.tempF, n8, n3, color);
                }
                n9 = n8 + (this.digitsSmall[0].getWidth() + this.tempDateSpacing);
            }
            else {
                n9 = uxOriginal2 + this.dateOffset;
            }
            final int[] dateDigits = this.dateDigits();
            for (int k = 0; k < 4; ++k) {
                if (b) {
                    this.DrawTextureCol(this.digitsSmall[8], n9, n3, color);
                }
                else {
                    this.DrawTextureCol(this.digitsSmall[dateDigits[k]], n9, n3, color);
                }
                n9 += this.digitsSmall[0].getWidth();
                if (k == 1) {
                    final int n10 = n9 + this.slashSpacing;
                    this.DrawTextureCol(this.slash, n10, n3, color);
                    n9 = n10 + (this.slash.getWidth() + this.slashSpacing);
                }
                else if (k < 3) {
                    n9 += this.smallDigitSpacing;
                }
            }
        }
    }
    
    private void assignTextures(final boolean b) {
        if (this.digitsLarge != null) {
            return;
        }
        String s = "Medium";
        String s2 = "Small";
        if (b) {
            s = "Large";
            s2 = "Medium";
            this.assignLargeOffsets();
        }
        else {
            this.assignSmallOffsets();
        }
        this.digitsLarge = new Texture[10];
        this.digitsSmall = new Texture[10];
        for (int i = 0; i < 10; ++i) {
            this.digitsLarge[i] = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, i));
            this.digitsSmall[i] = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s2, i));
        }
        this.colon = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.slash = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        this.minus = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        this.dot = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        this.tempC = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        this.tempF = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        this.tempE = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
        this.texAM = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.texPM = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.alarmOn = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.alarmRinging = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    private void assignSmallOffsets() {
        this.uxOriginal = 3;
        this.uyOriginal = 3;
        this.largeDigitSpacing = 1;
        this.smallDigitSpacing = 1;
        this.colonSpacing = 1;
        this.ampmSpacing = 1;
        this.alarmBellSpacing = 1;
        this.decimalSpacing = 1;
        this.degreeSpacing = 1;
        this.slashSpacing = 1;
        this.tempDateSpacing = 5;
        this.dateOffset = 33;
        this.minusOffset = 0;
        this.amVerticalSpacing = 7;
        this.pmVerticalSpacing = 12;
        this.alarmBellVerticalSpacing = 1;
        this.displayVerticalSpacing = 2;
        this.decimalVerticalSpacing = 6;
    }
    
    private void assignLargeOffsets() {
        this.uxOriginal = 3;
        this.uyOriginal = 3;
        this.largeDigitSpacing = 2;
        this.smallDigitSpacing = 1;
        this.colonSpacing = 3;
        this.ampmSpacing = 3;
        this.alarmBellSpacing = 5;
        this.decimalSpacing = 2;
        this.degreeSpacing = 2;
        this.slashSpacing = 2;
        this.tempDateSpacing = 8;
        this.dateOffset = 65;
        this.minusOffset = -2;
        this.amVerticalSpacing = 15;
        this.pmVerticalSpacing = 25;
        this.alarmBellVerticalSpacing = 1;
        this.displayVerticalSpacing = 5;
        this.decimalVerticalSpacing = 15;
    }
    
    private int[] timeDigits() {
        float n = GameTime.getInstance().getTimeOfDay();
        if (GameClient.bClient && GameClient.bFastForward) {
            n = GameTime.getInstance().ServerTimeOfDay;
        }
        if (!Core.getInstance().getOptionClock24Hour()) {
            if (n >= 13.0f) {
                n -= 12.0f;
            }
            if (n < 1.0f) {
                n += 12.0f;
            }
        }
        final int n2 = (int)n;
        return new int[] { n2 / 10, n2 % 10, (int)((n - (int)n) * 60.0f / 10.0f), 0 };
    }
    
    private int[] dateDigits() {
        final int n = (GameTime.getInstance().getDay() + 1) / 10;
        final int n2 = (GameTime.getInstance().getDay() + 1) % 10;
        final int n3 = (GameTime.getInstance().getMonth() + 1) / 10;
        final int n4 = (GameTime.getInstance().getMonth() + 1) % 10;
        if (Core.getInstance().getOptionClockFormat() == 1) {
            return new int[] { n3, n4, n, n2 };
        }
        return new int[] { n, n2, n3, n4 };
    }
    
    private int[] tempDigits() {
        float airTemperatureForCharacter = ClimateManager.getInstance().getAirTemperatureForCharacter(this.clockPlayer, false);
        boolean b = false;
        boolean b2 = false;
        if (!Core.OptionTemperatureDisplayCelsius) {
            airTemperatureForCharacter = airTemperatureForCharacter * 1.8f + 32.0f;
            b2 = true;
        }
        if (airTemperatureForCharacter < 0.0f) {
            b = true;
            airTemperatureForCharacter *= -1.0f;
        }
        return new int[] { b ? 1 : 0, (int)airTemperatureForCharacter / 100, (int)(airTemperatureForCharacter % 100.0f) / 10, (int)airTemperatureForCharacter % 10, (int)(airTemperatureForCharacter * 10.0f) % 10, b2 ? 1 : 0 };
    }
    
    public void resize() {
        this.visible = false;
        this.digital = false;
        this.clockPlayer = null;
        this.isAlarmSet = false;
        this.isAlarmRinging = false;
        if (IsoPlayer.getInstance() != null) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    if (!isoPlayer.isDead()) {
                        for (int j = 0; j < isoPlayer.getWornItems().size(); ++j) {
                            final InventoryItem itemByIndex = isoPlayer.getWornItems().getItemByIndex(j);
                            if (itemByIndex instanceof AlarmClock || itemByIndex instanceof AlarmClockClothing) {
                                this.visible = UIManager.VisibleAllUI;
                                this.digital |= itemByIndex.hasTag("Digital");
                                if (itemByIndex instanceof AlarmClock) {
                                    if (((AlarmClock)itemByIndex).isAlarmSet()) {
                                        this.isAlarmSet = true;
                                    }
                                    if (((AlarmClock)itemByIndex).isRinging()) {
                                        this.isAlarmRinging = true;
                                    }
                                }
                                else {
                                    if (((AlarmClockClothing)itemByIndex).isAlarmSet()) {
                                        this.isAlarmSet = true;
                                    }
                                    if (((AlarmClockClothing)itemByIndex).isRinging()) {
                                        this.isAlarmRinging = true;
                                    }
                                }
                                this.clockPlayer = isoPlayer;
                            }
                        }
                        if (this.clockPlayer != null) {
                            break;
                        }
                        final ArrayList<InventoryItem> items = isoPlayer.getInventory().getItems();
                        for (int k = 0; k < items.size(); ++k) {
                            final InventoryItem inventoryItem = items.get(k);
                            if (inventoryItem instanceof AlarmClock || inventoryItem instanceof AlarmClockClothing) {
                                this.visible = UIManager.VisibleAllUI;
                                this.digital |= inventoryItem.hasTag("Digital");
                                if (inventoryItem instanceof AlarmClock) {
                                    if (((AlarmClock)inventoryItem).isAlarmSet()) {
                                        this.isAlarmSet = true;
                                    }
                                    if (((AlarmClock)inventoryItem).isRinging()) {
                                        this.isAlarmRinging = true;
                                    }
                                }
                                else {
                                    if (((AlarmClockClothing)inventoryItem).isAlarmSet()) {
                                        this.isAlarmSet = true;
                                    }
                                    if (((AlarmClockClothing)inventoryItem).isRinging()) {
                                        this.isAlarmRinging = true;
                                    }
                                }
                                this.clockPlayer = isoPlayer;
                            }
                        }
                    }
                }
            }
        }
        if (DebugOptions.instance.CheatClockVisible.getValue()) {
            this.digital = true;
            this.visible = UIManager.VisibleAllUI;
        }
        if (this.background == null) {
            if (Core.getInstance().getOptionClockSize() == 2) {
                this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockLargeBackground.png");
            }
            else {
                this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockSmallBackground.png");
            }
        }
        this.setHeight(this.background.getHeight());
        this.setWidth(this.background.getWidth());
    }
    
    public boolean isDateVisible() {
        return this.visible && this.digital;
    }
    
    @Override
    public Boolean onMouseDown(final double n, final double n2) {
        if (!this.isVisible()) {
            return false;
        }
        if (this.isAlarmRinging) {
            if (IsoPlayer.getInstance() != null) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[i];
                    if (isoPlayer != null) {
                        if (!isoPlayer.isDead()) {
                            for (int j = 0; j < isoPlayer.getWornItems().size(); ++j) {
                                final InventoryItem itemByIndex = isoPlayer.getWornItems().getItemByIndex(j);
                                if (itemByIndex instanceof AlarmClock) {
                                    ((AlarmClock)itemByIndex).stopRinging();
                                }
                                else if (itemByIndex instanceof AlarmClockClothing) {
                                    ((AlarmClockClothing)itemByIndex).stopRinging();
                                }
                            }
                            for (int k = 0; k < isoPlayer.getInventory().getItems().size(); ++k) {
                                final InventoryItem inventoryItem = isoPlayer.getInventory().getItems().get(k);
                                if (inventoryItem instanceof AlarmClock) {
                                    ((AlarmClock)inventoryItem).stopRinging();
                                }
                                else if (inventoryItem instanceof AlarmClockClothing) {
                                    ((AlarmClockClothing)inventoryItem).stopRinging();
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (this.isAlarmSet) {
            if (IsoPlayer.getInstance() != null) {
                for (int l = 0; l < IsoPlayer.numPlayers; ++l) {
                    final IsoPlayer isoPlayer2 = IsoPlayer.players[l];
                    if (isoPlayer2 != null) {
                        if (!isoPlayer2.isDead()) {
                            for (int n3 = 0; n3 < isoPlayer2.getWornItems().size(); ++n3) {
                                final InventoryItem itemByIndex2 = isoPlayer2.getWornItems().getItemByIndex(n3);
                                if (itemByIndex2 instanceof AlarmClock && ((AlarmClock)itemByIndex2).isAlarmSet()) {
                                    ((AlarmClock)itemByIndex2).setAlarmSet(false);
                                }
                                else if (itemByIndex2 instanceof AlarmClockClothing && ((AlarmClockClothing)itemByIndex2).isAlarmSet()) {
                                    ((AlarmClockClothing)itemByIndex2).setAlarmSet(false);
                                }
                            }
                            for (int index = 0; index < isoPlayer2.getInventory().getItems().size(); ++index) {
                                final InventoryItem inventoryItem2 = isoPlayer2.getInventory().getItems().get(index);
                                if (inventoryItem2 instanceof AlarmClockClothing && ((AlarmClockClothing)inventoryItem2).isAlarmSet()) {
                                    ((AlarmClockClothing)inventoryItem2).setAlarmSet(false);
                                }
                                if (inventoryItem2 instanceof AlarmClock && ((AlarmClock)inventoryItem2).isAlarmSet()) {
                                    ((AlarmClock)inventoryItem2).setAlarmSet(false);
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (IsoPlayer.getInstance() != null) {
            for (int n4 = 0; n4 < IsoPlayer.numPlayers; ++n4) {
                final IsoPlayer isoPlayer3 = IsoPlayer.players[n4];
                if (isoPlayer3 != null) {
                    if (!isoPlayer3.isDead()) {
                        for (int n5 = 0; n5 < isoPlayer3.getWornItems().size(); ++n5) {
                            final InventoryItem itemByIndex3 = isoPlayer3.getWornItems().getItemByIndex(n5);
                            if (itemByIndex3 instanceof AlarmClock && ((AlarmClock)itemByIndex3).isDigital() && !((AlarmClock)itemByIndex3).isAlarmSet()) {
                                ((AlarmClock)itemByIndex3).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }
                            if (itemByIndex3 instanceof AlarmClockClothing && ((AlarmClockClothing)itemByIndex3).isDigital() && !((AlarmClockClothing)itemByIndex3).isAlarmSet()) {
                                ((AlarmClockClothing)itemByIndex3).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }
                        }
                        for (int index2 = 0; index2 < isoPlayer3.getInventory().getItems().size(); ++index2) {
                            final InventoryItem inventoryItem3 = isoPlayer3.getInventory().getItems().get(index2);
                            if (inventoryItem3 instanceof AlarmClock && ((AlarmClock)inventoryItem3).isDigital() && !((AlarmClock)inventoryItem3).isAlarmSet()) {
                                ((AlarmClock)inventoryItem3).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }
                            if (inventoryItem3 instanceof AlarmClockClothing && ((AlarmClockClothing)inventoryItem3).isDigital() && !((AlarmClockClothing)inventoryItem3).isAlarmSet()) {
                                ((AlarmClockClothing)inventoryItem3).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    static {
        Clock.instance = null;
    }
}
