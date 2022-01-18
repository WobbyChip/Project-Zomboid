// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.input.GameKeyboard;
import zombie.core.Color;
import zombie.core.PerformanceSettings;
import zombie.input.Mouse;
import zombie.core.Core;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.Texture;
import org.lwjgl.util.Rectangle;
import java.util.Stack;

public final class MoodlesUI extends UIElement
{
    public float clientH;
    public float clientW;
    public boolean Movable;
    public int ncclientH;
    public int ncclientW;
    private static MoodlesUI instance;
    private static final float OFFSCREEN_Y = 10000.0f;
    public Stack<Rectangle> nestedItems;
    float alpha;
    Texture Back_Bad_1;
    Texture Back_Bad_2;
    Texture Back_Bad_3;
    Texture Back_Bad_4;
    Texture Back_Good_1;
    Texture Back_Good_2;
    Texture Back_Good_3;
    Texture Back_Good_4;
    Texture Back_Neutral;
    Texture Endurance;
    Texture Bleeding;
    Texture Angry;
    Texture Stress;
    Texture Thirst;
    Texture Panic;
    Texture Hungry;
    Texture Injured;
    Texture Pain;
    Texture Sick;
    Texture Bored;
    Texture Unhappy;
    Texture Tired;
    Texture HeavyLoad;
    Texture Drunk;
    Texture Wet;
    Texture HasACold;
    Texture Dead;
    Texture Zombie;
    Texture Windchill;
    Texture CantSprint;
    Texture FoodEaten;
    Texture Hyperthermia;
    Texture Hypothermia;
    public static Texture plusRed;
    public static Texture plusGreen;
    public static Texture minusRed;
    public static Texture minusGreen;
    public static Texture chevronUp;
    public static Texture chevronUpBorder;
    public static Texture chevronDown;
    public static Texture chevronDownBorder;
    float MoodleDistY;
    boolean MouseOver;
    int MouseOverSlot;
    int NumUsedSlots;
    private int DebugKeyDelay;
    private int DistFromRighEdge;
    private int[] GoodBadNeutral;
    private int[] MoodleLevel;
    private float[] MoodleOscilationLevel;
    private float[] MoodleSlotsDesiredPos;
    private float[] MoodleSlotsPos;
    private int[] MoodleTypeInSlot;
    private float Oscilator;
    private float OscilatorDecelerator;
    private float OscilatorRate;
    private float OscilatorScalar;
    private float OscilatorStartLevel;
    private float OscilatorStep;
    private IsoGameCharacter UseCharacter;
    private boolean alphaIncrease;
    
    public MoodlesUI() {
        this.clientH = 0.0f;
        this.clientW = 0.0f;
        this.Movable = false;
        this.ncclientH = 0;
        this.ncclientW = 0;
        this.nestedItems = new Stack<Rectangle>();
        this.alpha = 1.0f;
        this.Back_Bad_1 = null;
        this.Back_Bad_2 = null;
        this.Back_Bad_3 = null;
        this.Back_Bad_4 = null;
        this.Back_Good_1 = null;
        this.Back_Good_2 = null;
        this.Back_Good_3 = null;
        this.Back_Good_4 = null;
        this.Back_Neutral = null;
        this.Endurance = null;
        this.Bleeding = null;
        this.Angry = null;
        this.Stress = null;
        this.Thirst = null;
        this.Panic = null;
        this.Hungry = null;
        this.Injured = null;
        this.Pain = null;
        this.Sick = null;
        this.Bored = null;
        this.Unhappy = null;
        this.Tired = null;
        this.HeavyLoad = null;
        this.Drunk = null;
        this.Wet = null;
        this.HasACold = null;
        this.Dead = null;
        this.Zombie = null;
        this.Windchill = null;
        this.CantSprint = null;
        this.FoodEaten = null;
        this.Hyperthermia = null;
        this.Hypothermia = null;
        this.MoodleDistY = 36.0f;
        this.MouseOver = false;
        this.MouseOverSlot = 0;
        this.NumUsedSlots = 0;
        this.DebugKeyDelay = 0;
        this.DistFromRighEdge = 46;
        this.GoodBadNeutral = new int[MoodleType.ToIndex(MoodleType.MAX)];
        this.MoodleLevel = new int[MoodleType.ToIndex(MoodleType.MAX)];
        this.MoodleOscilationLevel = new float[MoodleType.ToIndex(MoodleType.MAX)];
        this.MoodleSlotsDesiredPos = new float[MoodleType.ToIndex(MoodleType.MAX)];
        this.MoodleSlotsPos = new float[MoodleType.ToIndex(MoodleType.MAX)];
        this.MoodleTypeInSlot = new int[MoodleType.ToIndex(MoodleType.MAX)];
        this.Oscilator = 0.0f;
        this.OscilatorDecelerator = 0.96f;
        this.OscilatorRate = 0.8f;
        this.OscilatorScalar = 15.6f;
        this.OscilatorStartLevel = 1.0f;
        this.OscilatorStep = 0.0f;
        this.UseCharacter = null;
        this.alphaIncrease = true;
        this.x = Core.getInstance().getScreenWidth() - this.DistFromRighEdge;
        this.y = 100.0;
        this.width = 32.0f;
        this.height = 500.0f;
        this.Back_Bad_1 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_1.png");
        this.Back_Bad_2 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_2.png");
        this.Back_Bad_3 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_3.png");
        this.Back_Bad_4 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_4.png");
        this.Back_Good_1 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_1.png");
        this.Back_Good_2 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_2.png");
        this.Back_Good_3 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_3.png");
        this.Back_Good_4 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_4.png");
        this.Back_Neutral = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_1.png");
        this.Endurance = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Endurance.png");
        this.Tired = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Tired.png");
        this.Hungry = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Hungry.png");
        this.Panic = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Panic.png");
        this.Sick = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Sick.png");
        this.Bored = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Bored.png");
        this.Unhappy = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Unhappy.png");
        this.Bleeding = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Bleeding.png");
        this.Wet = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Wet.png");
        this.HasACold = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Cold.png");
        this.Angry = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Angry.png");
        this.Stress = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Stressed.png");
        this.Thirst = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Thirsty.png");
        this.Injured = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Injured.png");
        this.Pain = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Pain.png");
        this.HeavyLoad = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_HeavyLoad.png");
        this.Drunk = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Drunk.png");
        this.Dead = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Dead.png");
        this.Zombie = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Zombie.png");
        this.FoodEaten = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Hungry.png");
        this.Hyperthermia = Texture.getSharedTexture("media/ui/weather/Moodle_Icon_TempHot.png");
        this.Hypothermia = Texture.getSharedTexture("media/ui/weather/Moodle_Icon_TempCold.png");
        this.Windchill = Texture.getSharedTexture("media/ui/Moodle_Icon_Windchill.png");
        this.CantSprint = Texture.getSharedTexture("media/ui/Moodle_Icon_CantSprint.png");
        MoodlesUI.plusRed = Texture.getSharedTexture("media/ui/Moodle_internal_plus_red.png");
        MoodlesUI.minusRed = Texture.getSharedTexture("media/ui/Moodle_internal_minus_red.png");
        MoodlesUI.plusGreen = Texture.getSharedTexture("media/ui/Moodle_internal_plus_green.png");
        MoodlesUI.minusGreen = Texture.getSharedTexture("media/ui/Moodle_internal_minus_green.png");
        MoodlesUI.chevronUp = Texture.getSharedTexture("media/ui/Moodle_chevron_up.png");
        MoodlesUI.chevronUpBorder = Texture.getSharedTexture("media/ui/Moodle_chevron_up_border.png");
        MoodlesUI.chevronDown = Texture.getSharedTexture("media/ui/Moodle_chevron_down.png");
        MoodlesUI.chevronDownBorder = Texture.getSharedTexture("media/ui/Moodle_chevron_down_border.png");
        for (int i = 0; i < MoodleType.ToIndex(MoodleType.MAX); ++i) {
            this.MoodleSlotsPos[i] = 10000.0f;
            this.MoodleSlotsDesiredPos[i] = 10000.0f;
        }
        this.clientW = this.width;
        this.clientH = this.height;
        MoodlesUI.instance = this;
    }
    
    public boolean CurrentlyAnimating() {
        boolean b = false;
        for (int i = 0; i < MoodleType.ToIndex(MoodleType.MAX); ++i) {
            if (this.MoodleSlotsPos[i] != this.MoodleSlotsDesiredPos[i]) {
                b = true;
            }
        }
        return b;
    }
    
    public void Nest(final UIElement uiElement, final int n, final int n2, final int n3, final int n4) {
        this.AddChild(uiElement);
        this.nestedItems.add(new Rectangle(n4, n, n2, n3));
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        this.MouseOver = true;
        super.onMouseMove(n, n2);
        this.MouseOverSlot = (int)(((float)Mouse.getYA() - this.getY()) / this.MoodleDistY);
        if (this.MouseOverSlot >= this.NumUsedSlots) {
            this.MouseOverSlot = 1000;
        }
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        super.onMouseMoveOutside(n, n2);
        this.MouseOverSlot = 1000;
        this.MouseOver = false;
    }
    
    @Override
    public void render() {
        if (this.UseCharacter == null) {
            return;
        }
        this.OscilatorStep += this.OscilatorRate / (PerformanceSettings.getLockFPS() / 30.0f);
        this.Oscilator = (float)Math.sin(this.OscilatorStep);
        int n = 0;
        for (int i = 0; i < MoodleType.ToIndex(MoodleType.MAX); ++i) {
            if (this.MoodleSlotsPos[i] != 10000.0f) {
                final float n2 = this.Oscilator * this.OscilatorScalar * this.MoodleOscilationLevel[i];
                Texture texture = this.Back_Neutral;
                Texture texture2 = this.Tired;
                Label_0285: {
                    switch (this.GoodBadNeutral[i]) {
                        case 0: {
                            texture = this.Back_Neutral;
                            break;
                        }
                        case 1: {
                            switch (this.MoodleLevel[i]) {
                                case 1: {
                                    texture = this.Back_Good_1;
                                    break;
                                }
                                case 2: {
                                    texture = this.Back_Good_2;
                                    break;
                                }
                                case 3: {
                                    texture = this.Back_Good_3;
                                    break;
                                }
                                case 4: {
                                    texture = this.Back_Good_4;
                                    break;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (this.MoodleLevel[i]) {
                                case 1: {
                                    texture = this.Back_Bad_1;
                                    break Label_0285;
                                }
                                case 2: {
                                    texture = this.Back_Bad_2;
                                    break Label_0285;
                                }
                                case 3: {
                                    texture = this.Back_Bad_3;
                                    break Label_0285;
                                }
                                case 4: {
                                    texture = this.Back_Bad_4;
                                    break Label_0285;
                                }
                            }
                            break;
                        }
                    }
                }
                switch (i) {
                    case 0: {
                        texture2 = this.Endurance;
                        break;
                    }
                    case 1: {
                        texture2 = this.Tired;
                        break;
                    }
                    case 2: {
                        texture2 = this.Hungry;
                        break;
                    }
                    case 3: {
                        texture2 = this.Panic;
                        break;
                    }
                    case 4: {
                        texture2 = this.Sick;
                        break;
                    }
                    case 5: {
                        texture2 = this.Bored;
                        break;
                    }
                    case 6: {
                        texture2 = this.Unhappy;
                        break;
                    }
                    case 7: {
                        texture2 = this.Bleeding;
                        break;
                    }
                    case 8: {
                        texture2 = this.Wet;
                        break;
                    }
                    case 9: {
                        texture2 = this.HasACold;
                        break;
                    }
                    case 10: {
                        texture2 = this.Angry;
                        break;
                    }
                    case 11: {
                        texture2 = this.Stress;
                        break;
                    }
                    case 12: {
                        texture2 = this.Thirst;
                        break;
                    }
                    case 13: {
                        texture2 = this.Injured;
                        break;
                    }
                    case 14: {
                        texture2 = this.Pain;
                        break;
                    }
                    case 15: {
                        texture2 = this.HeavyLoad;
                        break;
                    }
                    case 16: {
                        texture2 = this.Drunk;
                        break;
                    }
                    case 17: {
                        texture2 = this.Dead;
                        break;
                    }
                    case 18: {
                        texture2 = this.Zombie;
                        break;
                    }
                    case 19: {
                        texture2 = this.FoodEaten;
                        break;
                    }
                    case 20: {
                        texture2 = this.Hyperthermia;
                        break;
                    }
                    case 21: {
                        texture2 = this.Hypothermia;
                        break;
                    }
                    case 22: {
                        texture2 = this.Windchill;
                        break;
                    }
                    case 23: {
                        texture2 = this.CantSprint;
                        break;
                    }
                }
                if (MoodleType.FromIndex(i).name().equals(Core.getInstance().getBlinkingMoodle())) {
                    if (this.alphaIncrease) {
                        this.alpha += 0.1f * (30.0f / PerformanceSettings.instance.getUIRenderFPS());
                        if (this.alpha > 1.0f) {
                            this.alpha = 1.0f;
                            this.alphaIncrease = false;
                        }
                    }
                    else {
                        this.alpha -= 0.1f * (30.0f / PerformanceSettings.instance.getUIRenderFPS());
                        if (this.alpha < 0.0f) {
                            this.alpha = 0.0f;
                            this.alphaIncrease = true;
                        }
                    }
                }
                if (Core.getInstance().getBlinkingMoodle() == null) {
                    this.alpha = 1.0f;
                }
                this.DrawTexture(texture, (int)n2, (int)this.MoodleSlotsPos[i], this.alpha);
                this.DrawTexture(texture2, (int)n2, (int)this.MoodleSlotsPos[i], this.alpha);
                if (this.UseCharacter.getMoodles().getMoodleChevronCount(i) > 0) {
                    final boolean moodleChevronIsUp = this.UseCharacter.getMoodles().getMoodleChevronIsUp(i);
                    final Color moodleChevronColor = this.UseCharacter.getMoodles().getMoodleChevronColor(i);
                    moodleChevronColor.a = this.alpha;
                    for (int j = 0; j < this.UseCharacter.getMoodles().getMoodleChevronCount(i); ++j) {
                        final int n3 = j * 4;
                        this.DrawTextureCol(moodleChevronIsUp ? MoodlesUI.chevronUp : MoodlesUI.chevronDown, (int)n2 + 16, (int)this.MoodleSlotsPos[i] + 20 - n3, moodleChevronColor);
                        this.DrawTextureCol(moodleChevronIsUp ? MoodlesUI.chevronUpBorder : MoodlesUI.chevronDownBorder, (int)n2 + 16, (int)this.MoodleSlotsPos[i] + 20 - n3, moodleChevronColor);
                    }
                }
                if (this.MouseOver && n == this.MouseOverSlot) {
                    final String moodleDisplayString = this.UseCharacter.getMoodles().getMoodleDisplayString(i);
                    final String moodleDescriptionString = this.UseCharacter.getMoodles().getMoodleDescriptionString(i);
                    final int max = Math.max(TextManager.instance.font.getWidth(moodleDisplayString), TextManager.instance.font.getWidth(moodleDescriptionString));
                    final int lineHeight = TextManager.instance.font.getLineHeight();
                    final int n4 = (int)this.MoodleSlotsPos[i] + 1;
                    this.DrawTextureScaledColor(null, -10.0 - max - 6.0, n4 - 2.0, max + 12.0, (double)((2 + lineHeight) * 2), 0.0, 0.0, 0.0, 0.6);
                    this.DrawTextRight(moodleDisplayString, -10.0, n4, 1.0, 1.0, 1.0, 1.0);
                    this.DrawTextRight(moodleDescriptionString, -10.0, n4 + lineHeight, 0.800000011920929, 0.800000011920929, 0.800000011920929, 1.0);
                }
                ++n;
            }
        }
        super.render();
    }
    
    public void wiggle(final MoodleType moodleType) {
        this.MoodleOscilationLevel[MoodleType.ToIndex(moodleType)] = this.OscilatorStartLevel;
    }
    
    @Override
    public void update() {
        super.update();
        if (this.UseCharacter == null) {
            return;
        }
        if (!this.CurrentlyAnimating()) {
            if (this.DebugKeyDelay > 0) {
                --this.DebugKeyDelay;
            }
            else if (GameKeyboard.isKeyDown(57)) {
                this.DebugKeyDelay = 10;
            }
        }
        final float n = PerformanceSettings.getLockFPS() / 30.0f;
        for (int i = 0; i < MoodleType.ToIndex(MoodleType.MAX); ++i) {
            final float[] moodleOscilationLevel = this.MoodleOscilationLevel;
            final int n2 = i;
            moodleOscilationLevel[n2] -= this.MoodleOscilationLevel[i] * (1.0f - this.OscilatorDecelerator) / n;
            if (this.MoodleOscilationLevel[i] < 0.01) {
                this.MoodleOscilationLevel[i] = 0.0f;
            }
        }
        if (this.UseCharacter.getMoodles().UI_RefreshNeeded()) {
            int numUsedSlots = 0;
            for (int j = 0; j < MoodleType.ToIndex(MoodleType.MAX); ++j) {
                if (this.UseCharacter.getMoodles().getMoodleLevel(j) > 0) {
                    boolean b = false;
                    if (this.MoodleLevel[j] != this.UseCharacter.getMoodles().getMoodleLevel(j)) {
                        b = true;
                        this.MoodleLevel[j] = this.UseCharacter.getMoodles().getMoodleLevel(j);
                        this.MoodleOscilationLevel[j] = this.OscilatorStartLevel;
                    }
                    this.MoodleSlotsDesiredPos[j] = this.MoodleDistY * numUsedSlots;
                    if (b) {
                        if (this.MoodleSlotsPos[j] == 10000.0f) {
                            this.MoodleSlotsPos[j] = this.MoodleSlotsDesiredPos[j] + 500.0f;
                            this.MoodleOscilationLevel[j] = 0.0f;
                        }
                        this.GoodBadNeutral[j] = this.UseCharacter.getMoodles().getGoodBadNeutral(j);
                    }
                    else {
                        this.MoodleOscilationLevel[j] = 0.0f;
                    }
                    this.MoodleTypeInSlot[numUsedSlots] = j;
                    ++numUsedSlots;
                }
                else {
                    this.MoodleSlotsPos[j] = 10000.0f;
                    this.MoodleSlotsDesiredPos[j] = 10000.0f;
                    this.MoodleOscilationLevel[j] = 0.0f;
                    this.MoodleLevel[j] = 0;
                }
            }
            this.NumUsedSlots = numUsedSlots;
        }
        for (int k = 0; k < MoodleType.ToIndex(MoodleType.MAX); ++k) {
            if (Math.abs(this.MoodleSlotsPos[k] - this.MoodleSlotsDesiredPos[k]) > 0.8f) {
                final float[] moodleSlotsPos = this.MoodleSlotsPos;
                final int n3 = k;
                moodleSlotsPos[n3] += (this.MoodleSlotsDesiredPos[k] - this.MoodleSlotsPos[k]) * 0.15f;
            }
            else {
                this.MoodleSlotsPos[k] = this.MoodleSlotsDesiredPos[k];
            }
        }
    }
    
    public void setCharacter(final IsoGameCharacter useCharacter) {
        if (useCharacter == this.UseCharacter) {
            return;
        }
        this.UseCharacter = useCharacter;
        if (this.UseCharacter != null && this.UseCharacter.getMoodles() != null) {
            this.UseCharacter.getMoodles().setMoodlesStateChanged(true);
        }
    }
    
    public static MoodlesUI getInstance() {
        return MoodlesUI.instance;
    }
    
    static {
        MoodlesUI.instance = null;
    }
}
