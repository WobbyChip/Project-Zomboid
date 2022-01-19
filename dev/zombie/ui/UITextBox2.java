// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.Lua.LuaManager;
import zombie.GameTime;
import zombie.core.fonts.AngelCodeFont;
import zombie.input.Mouse;
import org.lwjglx.input.Keyboard;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.core.textures.ColorInfo;
import gnu.trove.list.array.TIntArrayList;
import zombie.core.Color;
import java.util.Stack;

public class UITextBox2 extends UIElement
{
    public static boolean ConsoleHasFocus;
    public Stack<String> Lines;
    public UINineGrid Frame;
    public String Text;
    public boolean Centred;
    public Color StandardFrameColour;
    public Color TextEntryFrameColour;
    public Color TextEntryCursorColour;
    public Color TextEntryCursorColour2;
    public Color NuetralColour;
    public Color NuetralColour2;
    public Color BadColour;
    public Color GoodColour;
    public boolean DoingTextEntry;
    public int TextEntryCursorPos;
    public int TextEntryMaxLength;
    public boolean IsEditable;
    public boolean IsSelectable;
    public int CursorLine;
    public boolean multipleLine;
    public TIntArrayList TextOffsetOfLineStart;
    public int ToSelectionIndex;
    public String internalText;
    public String maskChr;
    public boolean bMask;
    public boolean ignoreFirst;
    UIFont font;
    int[] HighlightLines;
    boolean HasFrame;
    int NumVisibleLines;
    int TopLineIndex;
    int BlinkFramesOn;
    int BlinkFramesOff;
    float BlinkFrame;
    boolean BlinkState;
    private ColorInfo textColor;
    private int EdgeSize;
    private boolean SelectingRange;
    private int maxTextLength;
    private boolean forceUpperCase;
    private int XOffset;
    private int maxLines;
    private boolean onlyNumbers;
    private Texture clearButtonTexture;
    private boolean bClearButton;
    private UITransition clearButtonTransition;
    public boolean bAlwaysPaginate;
    public boolean bTextChanged;
    private int paginateWidth;
    private UIFont paginateFont;
    
    public UITextBox2(final UIFont font, final int n, final int n2, final int n3, final int n4, final String s, final boolean hasFrame) {
        this.Lines = new Stack<String>();
        this.Frame = null;
        this.Text = "";
        this.Centred = false;
        this.StandardFrameColour = new Color(50, 50, 50, 212);
        this.TextEntryFrameColour = new Color(50, 50, 127, 212);
        this.TextEntryCursorColour = new Color(170, 170, 220, 240);
        this.TextEntryCursorColour2 = new Color(100, 100, 220, 160);
        this.NuetralColour = new Color(0, 0, 255, 33);
        this.NuetralColour2 = new Color(127, 0, 255, 33);
        this.BadColour = new Color(255, 0, 0, 33);
        this.GoodColour = new Color(0, 255, 33);
        this.DoingTextEntry = false;
        this.TextEntryCursorPos = 0;
        this.TextEntryMaxLength = 2000;
        this.IsEditable = false;
        this.IsSelectable = false;
        this.CursorLine = 0;
        this.multipleLine = false;
        this.TextOffsetOfLineStart = new TIntArrayList();
        this.ToSelectionIndex = 0;
        this.internalText = "";
        this.maskChr = "*";
        this.bMask = false;
        this.HighlightLines = new int[1000];
        this.HasFrame = false;
        this.NumVisibleLines = 0;
        this.TopLineIndex = 0;
        this.BlinkFramesOn = 12;
        this.BlinkFramesOff = 8;
        this.BlinkFrame = (float)this.BlinkFramesOn;
        this.BlinkState = true;
        this.textColor = new ColorInfo();
        this.EdgeSize = 5;
        this.SelectingRange = false;
        this.maxTextLength = -1;
        this.forceUpperCase = false;
        this.XOffset = 0;
        this.maxLines = 1;
        this.onlyNumbers = false;
        this.bClearButton = false;
        this.bAlwaysPaginate = true;
        this.bTextChanged = false;
        this.paginateWidth = -1;
        this.paginateFont = null;
        this.font = font;
        this.x = n;
        this.y = n2;
        this.SetText(s);
        this.width = (float)n3;
        this.height = (float)n4;
        this.NumVisibleLines = 10;
        this.TopLineIndex = 0;
        Core.CurrentTextEntryBox = this;
        for (int i = 0; i < 1000; ++i) {
            this.HighlightLines[i] = 0;
        }
        this.HasFrame = hasFrame;
        if (hasFrame) {
            this.AddChild(this.Frame = new UINineGrid(0, 0, n3, n4, this.EdgeSize, this.EdgeSize, this.EdgeSize, this.EdgeSize, "media/ui/Box_TopLeft.png", "media/ui/Box_Top.png", "media/ui/Box_TopRight.png", "media/ui/Box_Left.png", "media/ui/Box_Center.png", "media/ui/Box_Right.png", "media/ui/Box_BottomLeft.png", "media/ui/Box_Bottom.png", "media/ui/Box_BottomRight.png"));
        }
        this.Paginate();
        this.DoingTextEntry = false;
        this.TextEntryMaxLength = 2000;
        this.TextEntryCursorPos = 0;
        this.ToSelectionIndex = this.TextEntryCursorPos;
        this.IsEditable = false;
        Keyboard.enableRepeatEvents(true);
        this.clearButtonTexture = Texture.getSharedTexture("media/ui/Panel_Icon_Close.png");
    }
    
    public void ClearHighlights() {
        for (int i = 0; i < 1000; ++i) {
            this.HighlightLines[i] = 0;
        }
    }
    
    public void setMasked(final boolean bMask) {
        this.bMask = bMask;
    }
    
    @Override
    public void onresize() {
        this.Paginate();
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return;
        }
        if (this.bMask) {
            if (this.internalText.length() != this.Text.length()) {
                String text = "";
                for (int i = 0; i < this.internalText.length(); ++i) {
                    text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, text, this.maskChr);
                }
                this.Text = text;
            }
        }
        else {
            this.Text = this.internalText;
        }
        super.render();
        this.Paginate();
        final int lineHeight = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
        final int inset = this.getInset();
        this.keepCursorVisible();
        int n = (int)this.width - inset;
        if (this.bClearButton && this.clearButtonTexture != null && !this.Lines.isEmpty()) {
            n -= 2 + this.clearButtonTexture.getWidth() + 2;
            float n2 = 0.5f;
            if (!this.SelectingRange && this.isMouseOver() && Mouse.getXA() >= this.getAbsoluteX() + n) {
                n2 = 1.0f;
            }
            this.clearButtonTransition.setFadeIn(n2 == 1.0f);
            this.clearButtonTransition.update();
            this.DrawTexture(this.clearButtonTexture, this.width - inset - 2.0f - this.clearButtonTexture.getWidth(), inset + (lineHeight - this.clearButtonTexture.getHeight()) / 2, n2 * this.clearButtonTransition.fraction() + 0.35f * (1.0f - this.clearButtonTransition.fraction()));
        }
        final Double clampToParentX = this.clampToParentX(this.getAbsoluteX().intValue() + inset);
        final Double clampToParentX2 = this.clampToParentX(this.getAbsoluteX().intValue() + n);
        final Double clampToParentY = this.clampToParentY(this.getAbsoluteY().intValue() + inset);
        final Double clampToParentY2 = this.clampToParentY(this.getAbsoluteY().intValue() + (int)this.height - inset);
        this.setStencilRect(clampToParentX.intValue() - this.getAbsoluteX().intValue(), clampToParentY.intValue() - this.getAbsoluteY().intValue(), clampToParentX2.intValue() - clampToParentX.intValue(), clampToParentY2.intValue() - clampToParentY.intValue());
        if (this.Lines.size() > 0) {
            int n3 = inset;
            for (int topLineIndex = this.TopLineIndex; topLineIndex < this.TopLineIndex + this.NumVisibleLines && topLineIndex < this.Lines.size(); ++topLineIndex) {
                if (this.Lines.get(topLineIndex) != null) {
                    if (topLineIndex >= 0 && topLineIndex < this.HighlightLines.length) {
                        if (this.HighlightLines[topLineIndex] == 1) {
                            this.DrawTextureScaledCol(null, inset - 1, n3, this.getWidth().intValue() - inset * 2 + 2, lineHeight, this.NuetralColour);
                        }
                        else if (this.HighlightLines[topLineIndex] == 2) {
                            this.DrawTextureScaledCol(null, inset - 1, n3, this.getWidth().intValue() - inset * 2 + 2, lineHeight, this.NuetralColour2);
                        }
                        else if (this.HighlightLines[topLineIndex] == 3) {
                            this.DrawTextureScaledCol(null, inset - 1, n3, this.getWidth().intValue() - inset * 2 + 2, lineHeight, this.BadColour);
                        }
                        else if (this.HighlightLines[topLineIndex] == 4) {
                            this.DrawTextureScaledCol(null, inset - 1, n3, this.getWidth().intValue() - inset * 2 + 2, lineHeight, this.GoodColour);
                        }
                    }
                    final String s = this.Lines.get(topLineIndex);
                    if (this.Centred) {
                        TextManager.instance.DrawStringCentre(this.font, this.getAbsoluteX().intValue() + this.getWidth() / 2.0 + inset, this.getAbsoluteY().intValue() + n3, s, this.textColor.r, this.textColor.g, this.textColor.b, 1.0);
                    }
                    else {
                        TextManager.instance.DrawString(this.font, -this.XOffset + this.getAbsoluteX().intValue() + inset, this.getAbsoluteY().intValue() + n3, s, this.textColor.r, this.textColor.g, this.textColor.b, 1.0);
                    }
                    n3 += lineHeight;
                }
            }
        }
        UITextBox2.ConsoleHasFocus = this.DoingTextEntry;
        if (this.TextEntryCursorPos > this.Text.length()) {
            this.TextEntryCursorPos = this.Text.length();
        }
        if (this.ToSelectionIndex > this.Text.length()) {
            this.ToSelectionIndex = this.Text.length();
        }
        this.CursorLine = this.toDisplayLine(this.TextEntryCursorPos);
        if (this.DoingTextEntry) {
            final AngelCodeFont fontFromEnum = TextManager.instance.getFontFromEnum(this.font);
            if (this.BlinkState) {
                int width = 0;
                if (this.Lines.size() > 0) {
                    width = fontFromEnum.getWidth(this.Lines.get(this.CursorLine), 0, Math.min(this.TextEntryCursorPos - this.TextOffsetOfLineStart.get(this.CursorLine), this.Lines.get(this.CursorLine).length()) - 1, true);
                    if (width > 0) {
                        --width;
                    }
                }
                this.DrawTextureScaledCol(Texture.getWhite(), -this.XOffset + inset + width, inset + this.CursorLine * lineHeight, 1.0, lineHeight, this.TextEntryCursorColour);
            }
            if (this.Lines.size() > 0 && this.ToSelectionIndex != this.TextEntryCursorPos) {
                final int min = Math.min(this.TextEntryCursorPos, this.ToSelectionIndex);
                final int max = Math.max(this.TextEntryCursorPos, this.ToSelectionIndex);
                final int displayLine = this.toDisplayLine(min);
                for (int displayLine2 = this.toDisplayLine(max), j = displayLine; j <= displayLine2; ++j) {
                    final int value = this.TextOffsetOfLineStart.get(j);
                    final int a = value + this.Lines.get(j).length();
                    final int max2 = Math.max(value, min);
                    final int min2 = Math.min(a, max);
                    final String s2 = this.Lines.get(j);
                    final int width2 = fontFromEnum.getWidth(s2, 0, max2 - this.TextOffsetOfLineStart.get(j) - 1, true);
                    this.DrawTextureScaledCol(null, -this.XOffset + inset + width2, inset + j * lineHeight, fontFromEnum.getWidth(s2, 0, min2 - this.TextOffsetOfLineStart.get(j) - 1, true) - width2, lineHeight, this.TextEntryCursorColour2);
                }
            }
        }
        this.clearStencilRect();
        if (UITextBox2.StencilLevel > 0) {
            this.repaintStencilRect(clampToParentX.intValue() - this.getAbsoluteX().intValue(), clampToParentY.intValue() - this.getAbsoluteY().intValue(), clampToParentX2.intValue() - clampToParentX.intValue(), clampToParentY2.intValue() - clampToParentY.intValue());
        }
    }
    
    public float getFrameAlpha() {
        return this.Frame.getAlpha();
    }
    
    public void setFrameAlpha(final float alpha) {
        this.Frame.setAlpha(alpha);
    }
    
    public void setTextColor(final ColorInfo textColor) {
        this.textColor = textColor;
    }
    
    private void keepCursorVisible() {
        if (this.Lines.isEmpty() || !this.DoingTextEntry || this.multipleLine) {
            this.XOffset = 0;
            return;
        }
        if (this.TextEntryCursorPos > this.Text.length()) {
            this.TextEntryCursorPos = this.Text.length();
        }
        final String s = this.Lines.get(0);
        final int measureStringX = TextManager.instance.MeasureStringX(this.font, s);
        final int inset = this.getInset();
        int n = this.getWidth().intValue() - inset * 2;
        if (this.bClearButton && this.clearButtonTexture != null) {
            n -= 2 + this.clearButtonTexture.getWidth() + 2;
        }
        if (measureStringX <= n) {
            this.XOffset = 0;
        }
        else if (-this.XOffset + measureStringX < n) {
            this.XOffset = measureStringX - n;
        }
        final int measureStringX2 = TextManager.instance.MeasureStringX(this.font, s.substring(0, this.TextEntryCursorPos));
        final int n2 = -this.XOffset + inset + measureStringX2 - 1;
        if (n2 < inset) {
            this.XOffset = measureStringX2;
        }
        else if (n2 >= inset + n) {
            this.XOffset = 0;
            final int cursorPosFromX = this.getCursorPosFromX(measureStringX2 - n);
            this.XOffset = TextManager.instance.MeasureStringX(this.font, s.substring(0, cursorPosFromX));
            if (-this.XOffset + inset + measureStringX2 - 1 >= inset + n) {
                this.XOffset = TextManager.instance.MeasureStringX(this.font, s.substring(0, cursorPosFromX + 1));
            }
            if (-this.XOffset + measureStringX < n) {
                this.XOffset = measureStringX - n;
            }
        }
    }
    
    public String getText() {
        return this.Text;
    }
    
    public String getInternalText() {
        return this.internalText;
    }
    
    @Override
    public void update() {
        if (this.maxTextLength > -1 && this.internalText.length() > this.maxTextLength) {
            this.internalText = this.internalText.substring(this.maxTextLength);
        }
        if (this.forceUpperCase) {
            this.internalText = this.internalText.toUpperCase();
        }
        if (this.bMask) {
            if (this.internalText.length() != this.Text.length()) {
                String text = "";
                for (int i = 0; i < this.internalText.length(); ++i) {
                    text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, text, this.maskChr);
                }
                if (this.DoingTextEntry && this.Text != text) {
                    this.resetBlink();
                }
                this.Text = text;
            }
        }
        else {
            if (this.DoingTextEntry && this.Text != this.internalText) {
                this.resetBlink();
            }
            this.Text = this.internalText;
        }
        this.Paginate();
        final int inset = this.getInset();
        final int lineHeight = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
        if (lineHeight + inset * 2 > this.getHeight()) {
            this.setHeight(lineHeight + inset * 2);
        }
        if (this.Frame != null) {
            this.Frame.setHeight(this.getHeight());
        }
        this.NumVisibleLines = (int)(this.getHeight() - inset * 2) / lineHeight;
        if (this.BlinkFrame > 0.0f) {
            this.BlinkFrame -= GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 30.0f;
        }
        else {
            this.BlinkState = !this.BlinkState;
            if (this.BlinkState) {
                this.BlinkFrame = (float)this.BlinkFramesOn;
            }
            else {
                this.BlinkFrame = (float)this.BlinkFramesOff;
            }
        }
        if (this.NumVisibleLines * lineHeight + inset * 2 < this.getHeight().intValue()) {
            if (this.NumVisibleLines < this.Lines.size()) {
                this.setScrollHeight((this.Lines.size() + 1) * lineHeight);
            }
            ++this.NumVisibleLines;
        }
        else {
            this.setScrollHeight(this.Lines.size() * lineHeight);
        }
        if (UIDebugConsole.instance == null || this != UIDebugConsole.instance.OutputLog) {
            this.TopLineIndex = (int)(-this.getYScroll() + inset) / lineHeight;
        }
        this.setYScroll(-this.TopLineIndex * lineHeight);
    }
    
    private void Paginate() {
        boolean bAlwaysPaginate = this.bAlwaysPaginate;
        if (!this.bAlwaysPaginate) {
            if (this.paginateFont != this.font) {
                this.paginateFont = this.font;
                bAlwaysPaginate = true;
            }
            if (this.paginateWidth != this.getWidth().intValue()) {
                this.paginateWidth = this.getWidth().intValue();
                bAlwaysPaginate = true;
            }
            if (this.bTextChanged) {
                this.bTextChanged = false;
                bAlwaysPaginate = true;
            }
            if (!bAlwaysPaginate) {
                return;
            }
        }
        this.Lines.clear();
        this.TextOffsetOfLineStart.resetQuick();
        if (this.Text.isEmpty()) {
            return;
        }
        if (!this.multipleLine) {
            this.Lines.add(this.Text);
            this.TextOffsetOfLineStart.add(0);
            return;
        }
        final String[] split = this.Text.split("\n", -1);
        int n = 0;
        for (String substring : split) {
            int endIndex = 0;
            if (substring.length() == 0) {
                this.Lines.add(this.multipleLine ? "" : " ");
                this.TextOffsetOfLineStart.add(n);
                ++n;
            }
            else {
                do {
                    int endIndex2;
                    int n2 = endIndex2 = substring.indexOf(" ", endIndex + 1);
                    if (endIndex2 == -1) {
                        endIndex2 = substring.length();
                    }
                    if (TextManager.instance.MeasureStringX(this.font, substring.substring(0, endIndex2)) >= this.getWidth() - this.getInset() * 2 - 17 && endIndex > 0) {
                        final String substring2 = substring.substring(0, endIndex);
                        substring = substring.substring(endIndex + 1);
                        this.Lines.add(substring2);
                        this.TextOffsetOfLineStart.add(n);
                        n += substring2.length() + 1;
                        n2 = 0;
                    }
                    else if (n2 == -1) {
                        final String e = substring;
                        this.Lines.add(e);
                        this.TextOffsetOfLineStart.add(n);
                        n += e.length() + 1;
                        break;
                    }
                    endIndex = n2;
                } while (substring.length() > 0);
            }
        }
    }
    
    public int getInset() {
        int edgeSize = 2;
        if (this.HasFrame) {
            edgeSize = this.EdgeSize;
        }
        return edgeSize;
    }
    
    public void setEditable(final boolean isEditable) {
        this.IsEditable = isEditable;
    }
    
    public void setSelectable(final boolean isSelectable) {
        this.IsSelectable = isSelectable;
    }
    
    @Override
    public Boolean onMouseUp(final double n, final double n2) {
        if (!this.isVisible()) {
            return false;
        }
        super.onMouseUp(n, n2);
        this.SelectingRange = false;
        return Boolean.TRUE;
    }
    
    @Override
    public void onMouseUpOutside(final double n, final double n2) {
        if (!this.isVisible()) {
            return;
        }
        super.onMouseUpOutside(n, n2);
        this.SelectingRange = false;
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        final int xa = Mouse.getXA();
        final int ya = Mouse.getYA();
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        final boolean consumeMouseEvents = this.isConsumeMouseEvents();
        this.setConsumeMouseEvents(false);
        final Boolean onMouseMove = super.onMouseMove(n, n2);
        this.setConsumeMouseEvents(consumeMouseEvents);
        if (onMouseMove) {
            return Boolean.TRUE;
        }
        if ((this.IsEditable || this.IsSelectable) && this.SelectingRange) {
            if (this.multipleLine) {
                this.CursorLine = (ya - this.getAbsoluteY().intValue() - this.getInset() - this.getYScroll().intValue()) / TextManager.instance.getFontFromEnum(this.font).getLineHeight();
                if (this.CursorLine > this.Lines.size() - 1) {
                    this.CursorLine = this.Lines.size() - 1;
                }
            }
            this.TextEntryCursorPos = this.getCursorPosFromX((int)(xa - this.getAbsoluteX()));
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        final int xa = Mouse.getXA();
        final int ya = Mouse.getYA();
        if (!Mouse.isButtonDown(0)) {
            this.SelectingRange = false;
        }
        if (!this.isVisible()) {
            return;
        }
        super.onMouseMoveOutside(n, n2);
        if ((this.IsEditable || this.IsSelectable) && this.SelectingRange) {
            if (this.multipleLine) {
                this.CursorLine = (ya - this.getAbsoluteY().intValue() - this.getInset() - this.getYScroll().intValue()) / TextManager.instance.getFontFromEnum(this.font).getLineHeight();
                if (this.CursorLine < 0) {
                    this.CursorLine = 0;
                }
                if (this.CursorLine > this.Lines.size() - 1) {
                    this.CursorLine = this.Lines.size() - 1;
                }
            }
            this.TextEntryCursorPos = this.getCursorPosFromX((int)(xa - this.getAbsoluteX()));
        }
    }
    
    public void focus() {
        this.DoingTextEntry = true;
        Core.CurrentTextEntryBox = this;
    }
    
    public void unfocus() {
        this.DoingTextEntry = false;
        if (Core.CurrentTextEntryBox == this) {
            Core.CurrentTextEntryBox = null;
        }
    }
    
    public void ignoreFirstInput() {
        this.ignoreFirst = true;
    }
    
    @Override
    public Boolean onMouseDown(final double n, final double n2) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        }
        if (!this.getControls().isEmpty()) {
            for (int i = 0; i < this.getControls().size(); ++i) {
                final UIElement uiElement = this.getControls().get(i);
                if (uiElement != this.Frame) {
                    if (uiElement.isMouseOver()) {
                        return ((boolean)uiElement.onMouseDown(n - uiElement.getXScrolled(this).intValue(), n2 - uiElement.getYScrolled(this).intValue())) ? Boolean.TRUE : Boolean.FALSE;
                    }
                }
            }
        }
        if (this.bClearButton && this.clearButtonTexture != null && !this.Lines.isEmpty() && n >= this.getWidth().intValue() - this.getInset() - (2 + this.clearButtonTexture.getWidth() + 2)) {
            this.clearInput();
            return Boolean.TRUE;
        }
        if (this.multipleLine) {
            this.CursorLine = ((int)n2 - this.getInset() - this.getYScroll().intValue()) / TextManager.instance.getFontFromEnum(this.font).getLineHeight();
            if (this.CursorLine > this.Lines.size() - 1) {
                this.CursorLine = this.Lines.size() - 1;
            }
        }
        if (this.IsEditable || this.IsSelectable) {
            if (Core.CurrentTextEntryBox != this) {
                if (Core.CurrentTextEntryBox != null) {
                    Core.CurrentTextEntryBox.DoingTextEntry = false;
                    if (Core.CurrentTextEntryBox.Frame != null) {
                        Core.CurrentTextEntryBox.Frame.Colour = this.StandardFrameColour;
                    }
                }
                Core.CurrentTextEntryBox = this;
                Core.CurrentTextEntryBox.SelectingRange = true;
            }
            if (!this.DoingTextEntry) {
                this.DoingTextEntry = true;
                this.TextEntryCursorPos = this.getCursorPosFromX((int)n);
                this.ToSelectionIndex = this.TextEntryCursorPos;
                if (this.Frame != null) {
                    this.Frame.Colour = this.TextEntryFrameColour;
                }
            }
            else {
                this.TextEntryCursorPos = this.getCursorPosFromX((int)n);
                this.ToSelectionIndex = this.TextEntryCursorPos;
            }
            return Boolean.TRUE;
        }
        if (this.Frame != null) {
            this.Frame.Colour = this.StandardFrameColour;
        }
        this.DoingTextEntry = false;
        return Boolean.FALSE;
    }
    
    private int getCursorPosFromX(final int n) {
        if (this.Lines.isEmpty()) {
            return 0;
        }
        final String s = this.Lines.get(this.CursorLine);
        if (s.length() == 0) {
            return this.TextOffsetOfLineStart.get(this.CursorLine);
        }
        if (n + this.XOffset < 0) {
            return this.TextOffsetOfLineStart.get(this.CursorLine);
        }
        for (int i = 0; i <= s.length(); ++i) {
            String substring = "";
            if (i > 0) {
                substring = s.substring(0, i);
            }
            if (TextManager.instance.MeasureStringX(this.font, substring) > n + this.XOffset && i >= 0) {
                return this.TextOffsetOfLineStart.get(this.CursorLine) + i - 1;
            }
        }
        return this.TextOffsetOfLineStart.get(this.CursorLine) + s.length();
    }
    
    public void updateText() {
        if (this.bMask) {
            String text = "";
            for (int i = 0; i < this.internalText.length(); ++i) {
                text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, text, this.maskChr);
            }
            this.Text = text;
        }
        else {
            this.Text = this.internalText;
        }
    }
    
    public void SetText(String text) {
        this.internalText = text;
        if (this.bMask) {
            text = "";
            for (int i = 0; i < this.internalText.length(); ++i) {
                text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, text, this.maskChr);
            }
            this.Text = text;
        }
        else {
            this.Text = text;
        }
        this.TextEntryCursorPos = text.length();
        this.ToSelectionIndex = this.TextEntryCursorPos;
        this.update();
        final int n = 0;
        this.ToSelectionIndex = n;
        this.TextEntryCursorPos = n;
        if (!this.Lines.isEmpty()) {
            final int index = this.Lines.size() - 1;
            final int n2 = this.TextOffsetOfLineStart.get(index) + this.Lines.get(index).length();
            this.ToSelectionIndex = n2;
            this.TextEntryCursorPos = n2;
        }
    }
    
    public void clearInput() {
        this.Text = "";
        this.internalText = "";
        this.TextEntryCursorPos = 0;
        this.ToSelectionIndex = 0;
        this.update();
        this.onTextChange();
    }
    
    public void onPressUp() {
        if (this.getTable() != null && this.getTable().rawget((Object)"onPressUp") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onPressUp"), (Object)this.getTable());
        }
    }
    
    public void onPressDown() {
        if (this.getTable() != null && this.getTable().rawget((Object)"onPressDown") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onPressDown"), (Object)this.getTable());
        }
    }
    
    public void onCommandEntered() {
        if (this.getTable() != null && this.getTable().rawget((Object)"onCommandEntered") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onCommandEntered"), (Object)this.getTable());
        }
    }
    
    public void onTextChange() {
        if (this.getTable() != null && this.getTable().rawget((Object)"onTextChange") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onTextChange"), (Object)this.getTable());
        }
    }
    
    public void onOtherKey(final int i) {
        if (this.getTable() != null && this.getTable().rawget((Object)"onOtherKey") != null) {
            LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget((Object)"onOtherKey"), new Object[] { this.getTable(), i });
        }
    }
    
    public int getMaxTextLength() {
        return this.maxTextLength;
    }
    
    public void setMaxTextLength(final int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }
    
    public boolean getForceUpperCase() {
        return this.forceUpperCase;
    }
    
    public void setForceUpperCase(final boolean forceUpperCase) {
        this.forceUpperCase = forceUpperCase;
    }
    
    public void setHasFrame(final boolean hasFrame) {
        if (this.HasFrame == hasFrame) {
            return;
        }
        this.HasFrame = hasFrame;
        if (this.HasFrame) {
            (this.Frame = new UINineGrid(0, 0, (int)this.width, (int)this.height, this.EdgeSize, this.EdgeSize, this.EdgeSize, this.EdgeSize, "media/ui/Box_TopLeft.png", "media/ui/Box_Top.png", "media/ui/Box_TopRight.png", "media/ui/Box_Left.png", "media/ui/Box_Center.png", "media/ui/Box_Right.png", "media/ui/Box_BottomLeft.png", "media/ui/Box_Bottom.png", "media/ui/Box_BottomRight.png")).setAnchorRight(true);
            this.AddChild(this.Frame);
        }
        else {
            this.RemoveChild(this.Frame);
            this.Frame = null;
        }
    }
    
    public void setClearButton(final boolean bClearButton) {
        this.bClearButton = bClearButton;
        if (this.bClearButton && this.clearButtonTransition == null) {
            this.clearButtonTransition = new UITransition();
        }
    }
    
    public int toDisplayLine(final int n) {
        for (int i = 0; i < this.Lines.size(); ++i) {
            if (n >= this.TextOffsetOfLineStart.get(i) && n <= this.TextOffsetOfLineStart.get(i) + ((String)this.Lines.get(i)).length()) {
                return i;
            }
        }
        return 0;
    }
    
    public void setMultipleLine(final boolean multipleLine) {
        this.multipleLine = multipleLine;
    }
    
    public void setCursorLine(final int cursorLine) {
        this.CursorLine = cursorLine;
    }
    
    public int getMaxLines() {
        return this.maxLines;
    }
    
    public void setMaxLines(final int maxLines) {
        this.maxLines = maxLines;
    }
    
    public boolean isFocused() {
        return this.DoingTextEntry;
    }
    
    public boolean isOnlyNumbers() {
        return this.onlyNumbers;
    }
    
    public void setOnlyNumbers(final boolean onlyNumbers) {
        this.onlyNumbers = onlyNumbers;
    }
    
    public void resetBlink() {
        this.BlinkState = true;
        this.BlinkFrame = (float)this.BlinkFramesOn;
    }
    
    public void selectAll() {
        this.TextEntryCursorPos = this.internalText.length();
        this.ToSelectionIndex = 0;
    }
    
    static {
        UITextBox2.ConsoleHasFocus = false;
    }
}
