// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.core.network.ByteBufferWriter;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.ui.UIFont;
import zombie.core.Color;

public class ChatSettings
{
    private boolean unique;
    private Color fontColor;
    private UIFont font;
    private FontSize fontSize;
    private boolean bold;
    private boolean allowImages;
    private boolean allowChatIcons;
    private boolean allowColors;
    private boolean allowFonts;
    private boolean allowBBcode;
    private boolean equalizeLineHeights;
    private boolean showAuthor;
    private boolean showTimestamp;
    private boolean showChatTitle;
    private boolean useOnlyActiveTab;
    private float range;
    private float zombieAttractionRange;
    public static final float infinityRange = -1.0f;
    
    public ChatSettings() {
        this.unique = true;
        this.fontColor = Color.white;
        this.font = UIFont.Dialogue;
        this.bold = true;
        this.showAuthor = true;
        this.showTimestamp = true;
        this.showChatTitle = true;
        this.range = -1.0f;
        this.zombieAttractionRange = -1.0f;
        this.useOnlyActiveTab = false;
        this.fontSize = FontSize.Medium;
    }
    
    public ChatSettings(final ByteBuffer byteBuffer) {
        this.unique = (byteBuffer.get() == 1);
        this.fontColor = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
        this.font = UIFont.FromString(GameWindow.ReadString(byteBuffer));
        this.bold = (byteBuffer.get() == 1);
        this.allowImages = (byteBuffer.get() == 1);
        this.allowChatIcons = (byteBuffer.get() == 1);
        this.allowColors = (byteBuffer.get() == 1);
        this.allowFonts = (byteBuffer.get() == 1);
        this.allowBBcode = (byteBuffer.get() == 1);
        this.equalizeLineHeights = (byteBuffer.get() == 1);
        this.showAuthor = (byteBuffer.get() == 1);
        this.showTimestamp = (byteBuffer.get() == 1);
        this.showChatTitle = (byteBuffer.get() == 1);
        this.range = byteBuffer.getFloat();
        if (byteBuffer.get() == 1) {
            this.zombieAttractionRange = byteBuffer.getFloat();
        }
        else {
            this.zombieAttractionRange = this.range;
        }
        this.fontSize = FontSize.Medium;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public void setUnique(final boolean unique) {
        this.unique = unique;
    }
    
    public Color getFontColor() {
        return this.fontColor;
    }
    
    public void setFontColor(final Color fontColor) {
        this.fontColor = fontColor;
    }
    
    public void setFontColor(final float n, final float n2, final float n3, final float n4) {
        this.fontColor = new Color(n, n2, n3, n4);
    }
    
    public UIFont getFont() {
        return this.font;
    }
    
    public void setFont(final UIFont font) {
        this.font = font;
    }
    
    public String getFontSize() {
        return this.fontSize.toString().toLowerCase();
    }
    
    public void setFontSize(final String s) {
        switch (s) {
            case "small":
            case "Small": {
                this.fontSize = FontSize.Small;
                break;
            }
            case "medium":
            case "Medium": {
                this.fontSize = FontSize.Medium;
                break;
            }
            case "large":
            case "Large": {
                this.fontSize = FontSize.Large;
                break;
            }
            default: {
                this.fontSize = FontSize.NotDefine;
                break;
            }
        }
    }
    
    public boolean isBold() {
        return this.bold;
    }
    
    public void setBold(final boolean bold) {
        this.bold = bold;
    }
    
    public boolean isShowAuthor() {
        return this.showAuthor;
    }
    
    public void setShowAuthor(final boolean showAuthor) {
        this.showAuthor = showAuthor;
    }
    
    public boolean isShowTimestamp() {
        return this.showTimestamp;
    }
    
    public void setShowTimestamp(final boolean showTimestamp) {
        this.showTimestamp = showTimestamp;
    }
    
    public boolean isShowChatTitle() {
        return this.showChatTitle;
    }
    
    public void setShowChatTitle(final boolean showChatTitle) {
        this.showChatTitle = showChatTitle;
    }
    
    public boolean isAllowImages() {
        return this.allowImages;
    }
    
    public void setAllowImages(final boolean allowImages) {
        this.allowImages = allowImages;
    }
    
    public boolean isAllowChatIcons() {
        return this.allowChatIcons;
    }
    
    public void setAllowChatIcons(final boolean allowChatIcons) {
        this.allowChatIcons = allowChatIcons;
    }
    
    public boolean isAllowColors() {
        return this.allowColors;
    }
    
    public void setAllowColors(final boolean allowColors) {
        this.allowColors = allowColors;
    }
    
    public boolean isAllowFonts() {
        return this.allowFonts;
    }
    
    public void setAllowFonts(final boolean allowFonts) {
        this.allowFonts = allowFonts;
    }
    
    public boolean isAllowBBcode() {
        return this.allowBBcode;
    }
    
    public void setAllowBBcode(final boolean allowBBcode) {
        this.allowBBcode = allowBBcode;
    }
    
    public boolean isEqualizeLineHeights() {
        return this.equalizeLineHeights;
    }
    
    public void setEqualizeLineHeights(final boolean equalizeLineHeights) {
        this.equalizeLineHeights = equalizeLineHeights;
    }
    
    public float getRange() {
        return this.range;
    }
    
    public void setRange(final float range) {
        this.range = range;
    }
    
    public float getZombieAttractionRange() {
        if (this.zombieAttractionRange == -1.0f) {
            return this.range;
        }
        return this.zombieAttractionRange;
    }
    
    public void setZombieAttractionRange(final float zombieAttractionRange) {
        this.zombieAttractionRange = zombieAttractionRange;
    }
    
    public boolean isUseOnlyActiveTab() {
        return this.useOnlyActiveTab;
    }
    
    public void setUseOnlyActiveTab(final boolean useOnlyActiveTab) {
        this.useOnlyActiveTab = useOnlyActiveTab;
    }
    
    public void pack(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putBoolean(this.unique);
        byteBufferWriter.putFloat(this.fontColor.r);
        byteBufferWriter.putFloat(this.fontColor.g);
        byteBufferWriter.putFloat(this.fontColor.b);
        byteBufferWriter.putFloat(this.fontColor.a);
        byteBufferWriter.putUTF(this.font.toString());
        byteBufferWriter.putBoolean(this.bold);
        byteBufferWriter.putBoolean(this.allowImages);
        byteBufferWriter.putBoolean(this.allowChatIcons);
        byteBufferWriter.putBoolean(this.allowColors);
        byteBufferWriter.putBoolean(this.allowFonts);
        byteBufferWriter.putBoolean(this.allowBBcode);
        byteBufferWriter.putBoolean(this.equalizeLineHeights);
        byteBufferWriter.putBoolean(this.showAuthor);
        byteBufferWriter.putBoolean(this.showTimestamp);
        byteBufferWriter.putBoolean(this.showChatTitle);
        byteBufferWriter.putFloat(this.range);
        byteBufferWriter.putBoolean(this.range != this.zombieAttractionRange);
        if (this.range != this.zombieAttractionRange) {
            byteBufferWriter.putFloat(this.zombieAttractionRange);
        }
    }
    
    public enum FontSize
    {
        NotDefine, 
        Small, 
        Medium, 
        Large;
        
        private static /* synthetic */ FontSize[] $values() {
            return new FontSize[] { FontSize.NotDefine, FontSize.Small, FontSize.Medium, FontSize.Large };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
