// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Arrays;
import zombie.core.Rand;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.core.textures.Texture;
import zombie.GameTime;
import java.util.Iterator;
import java.util.ArrayDeque;
import zombie.core.fonts.AngelCodeFont;
import java.util.ArrayList;

public final class TextDrawObject
{
    private String[] validImages;
    private String[] validFonts;
    private final ArrayList<DrawLine> lines;
    private int width;
    private int height;
    private int maxCharsLine;
    private UIFont defaultFontEnum;
    private AngelCodeFont defaultFont;
    private String original;
    private String unformatted;
    private DrawLine currentLine;
    private DrawElement currentElement;
    private boolean hasOpened;
    private boolean drawBackground;
    private boolean allowImages;
    private boolean allowChatIcons;
    private boolean allowColors;
    private boolean allowFonts;
    private boolean allowBBcode;
    private boolean allowAnyImage;
    private boolean allowLineBreaks;
    private boolean equalizeLineHeights;
    private boolean enabled;
    private int visibleRadius;
    private float scrambleVal;
    private float outlineR;
    private float outlineG;
    private float outlineB;
    private float outlineA;
    private float defaultR;
    private float defaultG;
    private float defaultB;
    private float defaultA;
    private int hearRange;
    private float internalClock;
    private String customTag;
    private int customImageMaxDim;
    private TextDrawHorizontal defaultHorz;
    private int drawMode;
    private static ArrayList<RenderBatch> renderBatch;
    private static ArrayDeque<RenderBatch> renderBatchPool;
    private String elemText;
    
    public TextDrawObject() {
        this(255, 255, 255, true, true, true, true, true, false);
    }
    
    public TextDrawObject(final int n, final int n2, final int n3, final boolean b) {
        this(n, n2, n3, b, true, true, true, true, false);
    }
    
    public TextDrawObject(final int n, final int n2, final int n3, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6) {
        this.validImages = new String[] { "Icon_music_notes", "media/ui/CarKey.png", "media/ui/ArrowUp.png", "media/ui/ArrowDown.png" };
        this.validFonts = new String[] { "Small", "Dialogue", "Medium", "Code", "Large", "Massive" };
        this.lines = new ArrayList<DrawLine>();
        this.width = 0;
        this.height = 0;
        this.maxCharsLine = -1;
        this.defaultFontEnum = UIFont.Dialogue;
        this.defaultFont = null;
        this.original = "";
        this.unformatted = "";
        this.hasOpened = false;
        this.drawBackground = false;
        this.allowImages = true;
        this.allowChatIcons = true;
        this.allowColors = true;
        this.allowFonts = true;
        this.allowBBcode = true;
        this.allowAnyImage = false;
        this.allowLineBreaks = true;
        this.equalizeLineHeights = false;
        this.enabled = true;
        this.visibleRadius = -1;
        this.scrambleVal = 0.0f;
        this.outlineR = 0.0f;
        this.outlineG = 0.0f;
        this.outlineB = 0.0f;
        this.outlineA = 1.0f;
        this.defaultR = 1.0f;
        this.defaultG = 1.0f;
        this.defaultB = 1.0f;
        this.defaultA = 1.0f;
        this.hearRange = -1;
        this.internalClock = 0.0f;
        this.customTag = "default";
        this.customImageMaxDim = 18;
        this.defaultHorz = TextDrawHorizontal.Center;
        this.drawMode = 0;
        this.setSettings(b, b2, b3, b4, b5, b6);
        this.setDefaultColors(n, n2, n3);
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean getEnabled() {
        return this.enabled;
    }
    
    public void setVisibleRadius(final int visibleRadius) {
        this.visibleRadius = visibleRadius;
    }
    
    public int getVisibleRadius() {
        return this.visibleRadius;
    }
    
    public void setDrawBackground(final boolean drawBackground) {
        this.drawBackground = drawBackground;
    }
    
    public void setAllowImages(final boolean allowImages) {
        this.allowImages = allowImages;
    }
    
    public void setAllowChatIcons(final boolean allowChatIcons) {
        this.allowChatIcons = allowChatIcons;
    }
    
    public void setAllowColors(final boolean allowColors) {
        this.allowColors = allowColors;
    }
    
    public void setAllowFonts(final boolean allowFonts) {
        this.allowFonts = allowFonts;
    }
    
    public void setAllowBBcode(final boolean allowBBcode) {
        this.allowBBcode = allowBBcode;
    }
    
    public void setAllowAnyImage(final boolean allowAnyImage) {
        this.allowAnyImage = allowAnyImage;
    }
    
    public void setAllowLineBreaks(final boolean allowLineBreaks) {
        this.allowLineBreaks = allowLineBreaks;
    }
    
    public void setEqualizeLineHeights(final boolean equalizeLineHeights) {
        this.equalizeLineHeights = equalizeLineHeights;
        this.calculateDimensions();
    }
    
    public void setSettings(final boolean allowBBcode, final boolean allowImages, final boolean allowChatIcons, final boolean allowColors, final boolean allowFonts, final boolean equalizeLineHeights) {
        this.allowImages = allowImages;
        this.allowChatIcons = allowChatIcons;
        this.allowColors = allowColors;
        this.allowFonts = allowFonts;
        this.allowBBcode = allowBBcode;
        this.equalizeLineHeights = equalizeLineHeights;
    }
    
    public void setCustomTag(final String customTag) {
        this.customTag = customTag;
    }
    
    public String getCustomTag() {
        return this.customTag;
    }
    
    public void setValidImages(final String[] validImages) {
        this.validImages = validImages;
    }
    
    public void setValidFonts(final String[] validFonts) {
        this.validFonts = validFonts;
    }
    
    public void setMaxCharsPerLine(final int n) {
        if (n <= 0) {
            return;
        }
        this.ReadString(this.original, n);
    }
    
    public void setCustomImageMaxDimensions(final int customImageMaxDim) {
        if (customImageMaxDim < 1) {
            return;
        }
        this.customImageMaxDim = customImageMaxDim;
        this.calculateDimensions();
    }
    
    public void setOutlineColors(final int n, final int n2, final int n3) {
        this.setOutlineColors(n / 255.0f, n2 / 255.0f, n3 / 255.0f, 1.0f);
    }
    
    public void setOutlineColors(final int n, final int n2, final int n3, final int n4) {
        this.setOutlineColors(n / 255.0f, n2 / 255.0f, n3 / 255.0f, n4 / 255.0f);
    }
    
    public void setOutlineColors(final float n, final float n2, final float n3) {
        this.setOutlineColors(n, n2, n3, 1.0f);
    }
    
    public void setOutlineColors(final float outlineR, final float outlineG, final float outlineB, final float outlineA) {
        this.outlineR = outlineR;
        this.outlineG = outlineG;
        this.outlineB = outlineB;
        this.outlineA = outlineA;
    }
    
    public void setDefaultColors(final int n, final int n2, final int n3) {
        this.setDefaultColors(n / 255.0f, n2 / 255.0f, n3 / 255.0f, 1.0f);
    }
    
    public void setDefaultColors(final int n, final int n2, final int n3, final int n4) {
        this.setDefaultColors(n / 255.0f, n2 / 255.0f, n3 / 255.0f, n4 / 255.0f);
    }
    
    public void setDefaultColors(final float n, final float n2, final float n3) {
        this.setDefaultColors(n, n2, n3, 1.0f);
    }
    
    public void setDefaultColors(final float defaultR, final float defaultG, final float defaultB, final float defaultA) {
        this.defaultR = defaultR;
        this.defaultG = defaultG;
        this.defaultB = defaultB;
        this.defaultA = defaultA;
    }
    
    public void setHorizontalAlign(final String s) {
        if (s.equals("left")) {
            this.defaultHorz = TextDrawHorizontal.Left;
        }
        else if (s.equals("center")) {
            this.defaultHorz = TextDrawHorizontal.Center;
        }
        if (s.equals("right")) {
            this.defaultHorz = TextDrawHorizontal.Right;
        }
    }
    
    public void setHorizontalAlign(final TextDrawHorizontal defaultHorz) {
        this.defaultHorz = defaultHorz;
    }
    
    public TextDrawHorizontal getHorizontalAlign() {
        return this.defaultHorz;
    }
    
    public String getOriginal() {
        return this.original;
    }
    
    public String getUnformatted() {
        if (this.scrambleVal > 0.0f) {
            String s = "";
            final Iterator<DrawLine> iterator = this.lines.iterator();
            while (iterator.hasNext()) {
                for (final DrawElement drawElement : iterator.next().elements) {
                    if (!drawElement.isImage) {
                        s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, drawElement.scrambleText);
                    }
                }
            }
            return s;
        }
        return this.unformatted;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public UIFont getDefaultFontEnum() {
        return this.defaultFontEnum;
    }
    
    public boolean isNullOrZeroLength() {
        return this.original == null || this.original.length() == 0;
    }
    
    public float getInternalClock() {
        return this.internalClock;
    }
    
    public void setInternalTickClock(final float internalClock) {
        if (internalClock > 0.0f) {
            this.internalClock = internalClock;
        }
    }
    
    public float updateInternalTickClock() {
        return this.updateInternalTickClock(1.25f * GameTime.getInstance().getMultiplier());
    }
    
    public float updateInternalTickClock(final float n) {
        if (this.internalClock <= 0.0f) {
            return 0.0f;
        }
        this.internalClock -= n;
        if (this.internalClock <= 0.0f) {
            this.internalClock = 0.0f;
        }
        return this.internalClock;
    }
    
    public void setScrambleVal(final float scrambleVal) {
        if (this.scrambleVal != scrambleVal) {
            this.scrambleVal = scrambleVal;
            if (this.scrambleVal > 0.0f) {
                final Iterator<DrawLine> iterator = this.lines.iterator();
                while (iterator.hasNext()) {
                    for (final DrawElement drawElement : iterator.next().elements) {
                        if (!drawElement.isImage) {
                            drawElement.scrambleText(this.scrambleVal);
                        }
                    }
                }
            }
        }
    }
    
    public float getScrambleVal() {
        return this.scrambleVal;
    }
    
    public void setHearRange(final int hearRange) {
        if (hearRange < 0) {
            this.hearRange = 0;
        }
        else {
            this.hearRange = hearRange;
        }
    }
    
    public int getHearRange() {
        return this.hearRange;
    }
    
    private boolean isValidFont(final String s) {
        final String[] validFonts = this.validFonts;
        for (int length = validFonts.length, i = 0; i < length; ++i) {
            if (s.equals(validFonts[i]) && UIFont.FromString(s) != null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidImage(final String s) {
        final String[] validImages = this.validImages;
        for (int length = validImages.length, i = 0; i < length; ++i) {
            if (s.equals(validImages[i])) {
                return true;
            }
        }
        return false;
    }
    
    private int tryColorInt(final String s) {
        if (s.length() <= 0 || s.length() > 3) {
            return -1;
        }
        try {
            final int int1 = Integer.parseInt(s);
            if (int1 >= 0 && int1 < 256) {
                return int1;
            }
        }
        catch (NumberFormatException ex) {
            return -1;
        }
        return -1;
    }
    
    private String readTagValue(final char[] array, final int n) {
        if (array[n] == '=') {
            String s = "";
            for (int i = n + 1; i < array.length; ++i) {
                final char c = array[i];
                if (c == ']') {
                    return s;
                }
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, s, c);
            }
        }
        return null;
    }
    
    public void Clear() {
        this.original = "";
        this.unformatted = "";
        this.reset();
    }
    
    private void reset() {
        this.lines.clear();
        this.currentLine = new DrawLine();
        this.lines.add(this.currentLine);
        this.currentElement = new DrawElement();
        this.currentLine.addElement(this.currentElement);
        this.enabled = true;
        this.scrambleVal = 0.0f;
    }
    
    private void addNewLine() {
        this.currentLine = new DrawLine();
        this.lines.add(this.currentLine);
        this.currentElement = this.currentElement.softclone();
        this.currentLine.addElement(this.currentElement);
    }
    
    private void addText(final String s) {
        this.currentElement.addText(s);
        final DrawLine currentLine = this.currentLine;
        currentLine.charW += s.length();
    }
    
    private void addWord(final String s) {
        if (this.maxCharsLine <= 0 || this.currentLine.charW + s.length() < this.maxCharsLine) {
            this.addText(s);
        }
        else {
            for (int i = 0; i < s.length() / this.maxCharsLine + 1; ++i) {
                final int n = i * this.maxCharsLine;
                final int n2 = (n + this.maxCharsLine < s.length()) ? (n + this.maxCharsLine) : s.length();
                if (s.substring(n, n2).length() > 0) {
                    if (i > 0 || this.currentLine.charW != 0) {
                        this.addNewLine();
                    }
                    this.addText(s.substring(n, n2));
                }
            }
        }
    }
    
    private void addNewElement() {
        if (this.currentElement.text.length() == 0) {
            this.currentElement.reset();
        }
        else {
            this.currentElement = new DrawElement();
            this.currentLine.addElement(this.currentElement);
        }
    }
    
    private int readTag(final char[] array, final int n, final String s) {
        if (this.allowFonts && s.equals("fnt")) {
            final String tagValue = this.readTagValue(array, n);
            if (tagValue != null && this.isValidFont(tagValue)) {
                this.addNewElement();
                this.currentElement.f = UIFont.FromString(tagValue);
                this.currentElement.useFont = true;
                this.currentElement.font = TextManager.instance.getFontFromEnum(this.currentElement.f);
                this.hasOpened = true;
                return n + tagValue.length() + 1;
            }
        }
        else if ((this.allowImages || this.allowChatIcons) && s.equals("img")) {
            String tagValue2 = this.readTagValue(array, n);
            if (tagValue2 != null && tagValue2.trim().length() > 0) {
                this.addNewElement();
                final int length = tagValue2.length();
                final String[] split = tagValue2.split(",");
                if (split.length > 1) {
                    tagValue2 = split[0];
                }
                this.currentElement.isImage = true;
                this.currentElement.text = tagValue2.trim();
                if (this.currentElement.text.equals("music")) {
                    this.currentElement.text = "Icon_music_notes";
                }
                if (this.allowChatIcons && this.isValidImage(this.currentElement.text)) {
                    this.currentElement.tex = Texture.getSharedTexture(this.currentElement.text);
                    this.currentElement.isTextImage = true;
                }
                else if (this.allowImages) {
                    this.currentElement.tex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.currentElement.text));
                    if (this.currentElement.tex == null) {
                        this.currentElement.tex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.currentElement.text));
                    }
                    if (this.currentElement.tex != null) {
                        this.currentElement.isTextImage = false;
                        this.currentElement.text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.currentElement.text);
                    }
                }
                if (this.allowAnyImage && this.currentElement.tex == null) {
                    this.currentElement.tex = Texture.getSharedTexture(this.currentElement.text);
                    if (this.currentElement.tex != null) {
                        this.currentElement.isTextImage = false;
                    }
                }
                if (split.length == 4) {
                    final int tryColorInt = this.tryColorInt(split[1]);
                    final int tryColorInt2 = this.tryColorInt(split[2]);
                    final int tryColorInt3 = this.tryColorInt(split[3]);
                    if (tryColorInt != -1 && tryColorInt2 != -1 && tryColorInt3 != -1) {
                        this.currentElement.useColor = true;
                        this.currentElement.R = tryColorInt / 255.0f;
                        this.currentElement.G = tryColorInt2 / 255.0f;
                        this.currentElement.B = tryColorInt3 / 255.0f;
                    }
                }
                this.addNewElement();
                return n + length + 1;
            }
        }
        else if (this.allowColors && s.equals("col")) {
            final String tagValue3 = this.readTagValue(array, n);
            if (tagValue3 != null) {
                final String[] split2 = tagValue3.split(",");
                if (split2.length == 3) {
                    final int tryColorInt4 = this.tryColorInt(split2[0]);
                    final int tryColorInt5 = this.tryColorInt(split2[1]);
                    final int tryColorInt6 = this.tryColorInt(split2[2]);
                    if (tryColorInt4 != -1 && tryColorInt5 != -1 && tryColorInt6 != -1) {
                        this.addNewElement();
                        this.currentElement.useColor = true;
                        this.currentElement.R = tryColorInt4 / 255.0f;
                        this.currentElement.G = tryColorInt5 / 255.0f;
                        this.currentElement.B = tryColorInt6 / 255.0f;
                        this.hasOpened = true;
                        return n + tagValue3.length() + 1;
                    }
                }
            }
        }
        else if (s.equals("cdt")) {
            final String tagValue4 = this.readTagValue(array, n);
            if (tagValue4 != null) {
                float internalClock = this.internalClock;
                try {
                    internalClock = Float.parseFloat(tagValue4);
                    internalClock *= 60.0f;
                }
                catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                this.internalClock = internalClock;
                return n + tagValue4.length() + 1;
            }
        }
        return -1;
    }
    
    public void setDefaultFont(final UIFont uiFont) {
        if (!uiFont.equals(this.defaultFontEnum)) {
            this.ReadString(uiFont, this.original, this.maxCharsLine);
        }
    }
    
    private void setDefaultFontInternal(final UIFont defaultFontEnum) {
        if (this.defaultFont == null || !defaultFontEnum.equals(this.defaultFontEnum)) {
            this.defaultFontEnum = defaultFontEnum;
            this.defaultFont = TextManager.instance.getFontFromEnum(defaultFontEnum);
        }
    }
    
    public void ReadString(final String s) {
        this.ReadString(this.defaultFontEnum, s, this.maxCharsLine);
    }
    
    public void ReadString(final String s, final int n) {
        this.ReadString(this.defaultFontEnum, s, n);
    }
    
    public void ReadString(final UIFont defaultFontInternal, String original, final int maxCharsLine) {
        if (original == null) {
            original = "";
        }
        this.reset();
        this.setDefaultFontInternal(defaultFontInternal);
        if (this.defaultFont == null) {
            return;
        }
        this.maxCharsLine = maxCharsLine;
        this.original = original;
        final char[] charArray = original.toCharArray();
        this.hasOpened = false;
        String s = "";
        for (int i = 0; i < charArray.length; ++i) {
            final char ch = charArray[i];
            if (this.allowBBcode && ch == '[') {
                if (s.length() > 0) {
                    this.addWord(s);
                    s = "";
                }
                if (i + 4 < charArray.length) {
                    final String lowerCase = invokedynamic(makeConcatWithConstants:(CCC)Ljava/lang/String;, charArray[i + 1], charArray[i + 2], charArray[i + 3]).toLowerCase();
                    if (this.allowLineBreaks && lowerCase.equals("br/")) {
                        this.addNewLine();
                        i += 4;
                        continue;
                    }
                    if (!this.hasOpened) {
                        final int tag = this.readTag(charArray, i + 4, lowerCase);
                        if (tag >= 0) {
                            i = tag;
                            continue;
                        }
                    }
                }
                if (this.hasOpened && i + 2 < charArray.length && charArray[i + 1] == '/' && charArray[i + 2] == ']') {
                    this.hasOpened = false;
                    this.addNewElement();
                    i += 2;
                    continue;
                }
            }
            if (Character.isWhitespace(ch) && i > 0 && !Character.isWhitespace(charArray[i - 1])) {
                this.addWord(s);
                s = "";
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, s, ch);
            this.unformatted = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, this.unformatted, ch);
        }
        if (s.length() > 0) {
            this.addWord(s);
        }
        this.calculateDimensions();
    }
    
    public void calculateDimensions() {
        this.width = 0;
        this.height = 0;
        int h = 0;
        for (int i = 0; i < this.lines.size(); ++i) {
            final DrawLine drawLine = this.lines.get(i);
            drawLine.h = 0;
            drawLine.w = 0;
            for (int j = 0; j < drawLine.elements.size(); ++j) {
                final DrawElement drawElement = drawLine.elements.get(j);
                drawElement.w = 0;
                drawElement.h = 0;
                if (drawElement.isImage && drawElement.tex != null) {
                    if (drawElement.isTextImage) {
                        drawElement.w = drawElement.tex.getWidth();
                        drawElement.h = drawElement.tex.getHeight();
                    }
                    else {
                        drawElement.w = (int)(drawElement.tex.getWidth() * 0.75f);
                        drawElement.h = (int)(drawElement.tex.getHeight() * 0.75f);
                    }
                }
                else if (drawElement.useFont && drawElement.font != null) {
                    drawElement.w = drawElement.font.getWidth(drawElement.text);
                    drawElement.h = drawElement.font.getHeight(drawElement.text);
                }
                else if (this.defaultFont != null) {
                    drawElement.w = this.defaultFont.getWidth(drawElement.text);
                    drawElement.h = this.defaultFont.getHeight(drawElement.text);
                }
                final DrawLine drawLine2 = drawLine;
                drawLine2.w += drawElement.w;
                if (drawElement.h > drawLine.h) {
                    drawLine.h = drawElement.h;
                }
            }
            if (drawLine.w > this.width) {
                this.width = drawLine.w;
            }
            this.height += drawLine.h;
            if (drawLine.h > h) {
                h = drawLine.h;
            }
        }
        if (this.equalizeLineHeights) {
            this.height = 0;
            for (int k = 0; k < this.lines.size(); ++k) {
                this.lines.get(k).h = h;
                this.height += h;
            }
        }
    }
    
    public void Draw(final double n, final double n2) {
        this.Draw(this.defaultHorz, n, n2, this.defaultR, this.defaultG, this.defaultB, this.defaultA, false);
    }
    
    public void Draw(final double n, final double n2, final boolean b) {
        this.Draw(this.defaultHorz, n, n2, this.defaultR, this.defaultG, this.defaultB, this.defaultA, b);
    }
    
    public void Draw(final double n, final double n2, final boolean b, final float n3) {
        this.Draw(this.defaultHorz, n, n2, this.defaultR, this.defaultG, this.defaultB, n3, b);
    }
    
    public void Draw(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final boolean b) {
        this.Draw(this.defaultHorz, n, n2, n3, n4, n5, n6, b);
    }
    
    public void Draw(final TextDrawHorizontal textDrawHorizontal, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final boolean b) {
        this.DrawRaw(textDrawHorizontal, n, n2, (float)n3, (float)n4, (float)n5, (float)n6, b);
    }
    
    public void AddBatchedDraw(final double n, final double n2) {
        this.AddBatchedDraw(this.defaultHorz, n, n2, this.defaultR, this.defaultG, this.defaultB, this.defaultA, false);
    }
    
    public void AddBatchedDraw(final double n, final double n2, final boolean b) {
        this.AddBatchedDraw(this.defaultHorz, n, n2, this.defaultR, this.defaultG, this.defaultB, this.defaultA, b);
    }
    
    public void AddBatchedDraw(final double n, final double n2, final boolean b, final float n3) {
        this.AddBatchedDraw(this.defaultHorz, n, n2, this.defaultR, this.defaultG, this.defaultB, n3, b);
    }
    
    public void AddBatchedDraw(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final boolean b) {
        this.AddBatchedDraw(this.defaultHorz, n, n2, n3, n4, n5, n6, b);
    }
    
    public void AddBatchedDraw(final TextDrawHorizontal horz, final double x, final double y, final double n, final double n2, final double n3, final double n4, final boolean drawOutlines) {
        if (GameServer.bServer) {
            return;
        }
        final RenderBatch e = TextDrawObject.renderBatchPool.isEmpty() ? new RenderBatch() : TextDrawObject.renderBatchPool.pop();
        e.playerNum = IsoPlayer.getPlayerIndex();
        e.element = this;
        e.horz = horz;
        e.x = x;
        e.y = y;
        e.r = (float)n;
        e.g = (float)n2;
        e.b = (float)n3;
        e.a = (float)n4;
        e.drawOutlines = drawOutlines;
        TextDrawObject.renderBatch.add(e);
    }
    
    public static void RenderBatch(final int n) {
        if (TextDrawObject.renderBatch.size() > 0) {
            for (int i = 0; i < TextDrawObject.renderBatch.size(); ++i) {
                final RenderBatch e = TextDrawObject.renderBatch.get(i);
                if (e.playerNum == n) {
                    e.element.DrawRaw(e.horz, e.x, e.y, e.r, e.g, e.b, e.a, e.drawOutlines);
                    TextDrawObject.renderBatchPool.add(e);
                    TextDrawObject.renderBatch.remove(i--);
                }
            }
        }
    }
    
    public static void NoRender(final int n) {
        for (int i = 0; i < TextDrawObject.renderBatch.size(); ++i) {
            final RenderBatch e = TextDrawObject.renderBatch.get(i);
            if (e.playerNum == n) {
                TextDrawObject.renderBatchPool.add(e);
                TextDrawObject.renderBatch.remove(i--);
            }
        }
    }
    
    public void DrawRaw(final TextDrawHorizontal textDrawHorizontal, final double n, final double n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        double n7 = n;
        double n8 = n2;
        final int screenWidth = Core.getInstance().getScreenWidth();
        final int screenHeight = Core.getInstance().getScreenHeight();
        final int n9 = 20;
        if (textDrawHorizontal == TextDrawHorizontal.Center) {
            n7 = n - this.getWidth() / 2;
        }
        else if (textDrawHorizontal == TextDrawHorizontal.Right) {
            n7 = n - this.getWidth();
        }
        if (n7 - n9 >= screenWidth || n7 + this.getWidth() + n9 <= 0.0 || n8 - n9 >= screenHeight || n8 + this.getHeight() + n9 <= 0.0) {
            return;
        }
        if (this.drawBackground && ChatElement.backdropTexture != null) {
            ChatElement.backdropTexture.renderInnerBased((int)n7, (int)n8, this.getWidth(), this.getHeight(), 0.0f, 0.0f, 0.0f, 0.4f * n6);
        }
        float outlineA = this.outlineA;
        if (b && n6 < 1.0f) {
            outlineA = this.outlineA * n6;
        }
        for (int i = 0; i < this.lines.size(); ++i) {
            final DrawLine drawLine = this.lines.get(i);
            double n10 = n;
            if (textDrawHorizontal == TextDrawHorizontal.Center) {
                n10 = n - drawLine.w / 2;
            }
            else if (textDrawHorizontal == TextDrawHorizontal.Right) {
                n10 = n - drawLine.w;
            }
            for (int j = 0; j < drawLine.elements.size(); ++j) {
                final DrawElement drawElement = drawLine.elements.get(j);
                final double n11 = drawLine.h / 2 - drawElement.h / 2;
                this.elemText = ((this.scrambleVal > 0.0f) ? drawElement.scrambleText : drawElement.text);
                if (drawElement.isImage && drawElement.tex != null) {
                    if (b && drawElement.isTextImage) {
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)(n10 - 1.0), (int)(n8 + n11 - 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, outlineA, null);
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)(n10 + 1.0), (int)(n8 + n11 + 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, outlineA, null);
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)(n10 - 1.0), (int)(n8 + n11 + 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, outlineA, null);
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)(n10 + 1.0), (int)(n8 + n11 - 1.0), drawElement.w, drawElement.h, this.outlineR, this.outlineG, this.outlineB, outlineA, null);
                    }
                    if (drawElement.useColor) {
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)n10, (int)(n8 + n11), drawElement.w, drawElement.h, drawElement.R, drawElement.G, drawElement.B, n6, null);
                    }
                    else if (drawElement.isTextImage) {
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)n10, (int)(n8 + n11), drawElement.w, drawElement.h, n3, n4, n5, n6, null);
                    }
                    else {
                        SpriteRenderer.instance.renderi(drawElement.tex, (int)n10, (int)(n8 + n11), drawElement.w, drawElement.h, 1.0f, 1.0f, 1.0f, n6, null);
                    }
                }
                else if (drawElement.useFont && drawElement.font != null) {
                    if (b) {
                        drawElement.font.drawString((float)(n10 - 1.0), (float)(n8 + n11 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                        drawElement.font.drawString((float)(n10 + 1.0), (float)(n8 + n11 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                        drawElement.font.drawString((float)(n10 - 1.0), (float)(n8 + n11 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                        drawElement.font.drawString((float)(n10 + 1.0), (float)(n8 + n11 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                    }
                    drawElement.font.drawString((float)n10, (float)(n8 + n11), this.elemText, n3, n4, n5, n6);
                }
                else if (this.defaultFont != null) {
                    if (b) {
                        this.defaultFont.drawString((float)(n10 - 1.0), (float)(n8 + n11 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                        this.defaultFont.drawString((float)(n10 + 1.0), (float)(n8 + n11 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                        this.defaultFont.drawString((float)(n10 - 1.0), (float)(n8 + n11 + 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                        this.defaultFont.drawString((float)(n10 + 1.0), (float)(n8 + n11 - 1.0), this.elemText, this.outlineR, this.outlineG, this.outlineB, outlineA);
                    }
                    if (drawElement.useColor) {
                        this.defaultFont.drawString((float)n10, (float)(n8 + n11), this.elemText, drawElement.R, drawElement.G, drawElement.B, n6);
                    }
                    else {
                        this.defaultFont.drawString((float)n10, (float)(n8 + n11), this.elemText, n3, n4, n5, n6);
                    }
                }
                n10 += drawElement.w;
            }
            n8 += drawLine.h;
        }
    }
    
    static {
        TextDrawObject.renderBatch = new ArrayList<RenderBatch>();
        TextDrawObject.renderBatchPool = new ArrayDeque<RenderBatch>();
    }
    
    private static final class DrawElement
    {
        private String text;
        private String scrambleText;
        private float currentScrambleVal;
        private UIFont f;
        private AngelCodeFont font;
        private float R;
        private float G;
        private float B;
        private int w;
        private int h;
        private boolean isImage;
        private boolean useFont;
        private boolean useColor;
        private Texture tex;
        private boolean isTextImage;
        private int charWidth;
        
        private DrawElement() {
            this.text = "";
            this.scrambleText = "";
            this.currentScrambleVal = 0.0f;
            this.f = UIFont.AutoNormSmall;
            this.font = null;
            this.R = 1.0f;
            this.G = 1.0f;
            this.B = 1.0f;
            this.w = 0;
            this.h = 0;
            this.isImage = false;
            this.useFont = false;
            this.useColor = false;
            this.tex = null;
            this.isTextImage = false;
            this.charWidth = 0;
        }
        
        private void reset() {
            this.text = "";
            this.scrambleText = "";
            this.f = UIFont.AutoNormSmall;
            this.font = null;
            this.R = 1.0f;
            this.G = 1.0f;
            this.B = 1.0f;
            this.w = 0;
            this.h = 0;
            this.isImage = false;
            this.useFont = false;
            this.useColor = false;
            this.tex = null;
            this.isTextImage = false;
            this.charWidth = 0;
        }
        
        private void addText(final String s) {
            this.text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.text, s);
            this.charWidth = this.text.length();
        }
        
        private void scrambleText(final float currentScrambleVal) {
            if (currentScrambleVal != this.currentScrambleVal) {
                this.currentScrambleVal = currentScrambleVal;
                final int n = (int)(currentScrambleVal * 100.0f);
                final String[] split = this.text.split("\\s+");
                this.scrambleText = "";
                for (final String s : split) {
                    if (Rand.Next(100) > n) {
                        this.scrambleText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.scrambleText, s);
                    }
                    else {
                        final char[] array2 = new char[s.length()];
                        Arrays.fill(array2, ".".charAt(0));
                        this.scrambleText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.scrambleText, new String(array2));
                    }
                }
            }
        }
        
        private void trim() {
            this.text = this.text.trim();
        }
        
        private DrawElement softclone() {
            final DrawElement drawElement = new DrawElement();
            if (this.useColor) {
                drawElement.R = this.R;
                drawElement.G = this.G;
                drawElement.B = this.B;
                drawElement.useColor = this.useColor;
            }
            if (this.useFont) {
                drawElement.f = this.f;
                drawElement.font = this.font;
                drawElement.useFont = this.useFont;
            }
            return drawElement;
        }
    }
    
    private static final class DrawLine
    {
        private final ArrayList<DrawElement> elements;
        private int h;
        private int w;
        private int charW;
        
        private DrawLine() {
            this.elements = new ArrayList<DrawElement>();
            this.h = 0;
            this.w = 0;
            this.charW = 0;
        }
        
        private void addElement(final DrawElement e) {
            this.elements.add(e);
        }
    }
    
    private static final class RenderBatch
    {
        int playerNum;
        TextDrawObject element;
        TextDrawHorizontal horz;
        double x;
        double y;
        float r;
        float g;
        float b;
        float a;
        boolean drawOutlines;
    }
}
