// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.media;

import zombie.core.Color;
import zombie.debug.DebugLog;
import zombie.core.Translator;
import zombie.core.Core;
import java.util.ArrayList;

public class MediaData
{
    private final String id;
    private final String itemDisplayName;
    private String title;
    private String subtitle;
    private String author;
    private String extra;
    private short index;
    private String category;
    private final int spawning;
    private ArrayList<MediaLineData> lines;
    
    public MediaData(final String id, final String itemDisplayName, final int spawning) {
        this.lines = new ArrayList<MediaLineData>();
        this.itemDisplayName = itemDisplayName;
        this.id = id;
        this.spawning = spawning;
        if (Core.bDebug) {
            if (itemDisplayName == null) {
                throw new RuntimeException("ItemDisplayName may not be null.");
            }
            if (id == null) {
                throw new RuntimeException("Id may not be null.");
            }
        }
    }
    
    public void addLine(final String s, final float n, final float n2, final float n3, final String s2) {
        this.lines.add(new MediaLineData(s, n, n2, n3, s2));
    }
    
    public int getLineCount() {
        return this.lines.size();
    }
    
    public String getTranslatedItemDisplayName() {
        return Translator.getText(this.itemDisplayName);
    }
    
    public boolean hasTitle() {
        return this.title != null;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getTitleEN() {
        return (this.title != null) ? Translator.getTextMediaEN(this.title) : null;
    }
    
    public String getTranslatedTitle() {
        return (this.title != null) ? Translator.getText(this.title) : null;
    }
    
    public boolean hasSubTitle() {
        return this.subtitle != null;
    }
    
    public void setSubtitle(final String subtitle) {
        this.subtitle = subtitle;
    }
    
    public String getSubtitleEN() {
        return (this.subtitle != null) ? Translator.getTextMediaEN(this.subtitle) : null;
    }
    
    public String getTranslatedSubTitle() {
        return (this.subtitle != null) ? Translator.getText(this.subtitle) : null;
    }
    
    public boolean hasAuthor() {
        return this.author != null;
    }
    
    public void setAuthor(final String author) {
        this.author = author;
    }
    
    public String getAuthorEN() {
        return (this.author != null) ? Translator.getTextMediaEN(this.author) : null;
    }
    
    public String getTranslatedAuthor() {
        return (this.author != null) ? Translator.getText(this.author) : null;
    }
    
    public boolean hasExtra() {
        return this.extra != null;
    }
    
    public void setExtra(final String extra) {
        this.extra = extra;
    }
    
    public String getExtraEN() {
        return (this.extra != null) ? Translator.getTextMediaEN(this.extra) : null;
    }
    
    public String getTranslatedExtra() {
        return (this.extra != null) ? Translator.getText(this.extra) : null;
    }
    
    public String getId() {
        return this.id;
    }
    
    public short getIndex() {
        return this.index;
    }
    
    protected void setIndex(final short index) {
        this.index = index;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    protected void setCategory(final String category) {
        this.category = category;
    }
    
    public int getSpawning() {
        return this.spawning;
    }
    
    public byte getMediaType() {
        if (this.category == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.itemDisplayName != null) ? this.itemDisplayName : "unknown"));
        }
        return RecordedMedia.getMediaTypeForCategory(this.category);
    }
    
    public MediaLineData getLine(final int index) {
        if (index >= 0 && index < this.lines.size()) {
            return this.lines.get(index);
        }
        return null;
    }
    
    public class MediaLineData
    {
        private final String text;
        private final Color color;
        private final String codes;
        
        public MediaLineData(final String text, float n, float n2, float n3, final String codes) {
            this.text = text;
            this.codes = codes;
            if (n == 0.0f && n2 == 0.0f && n3 == 0.0f) {
                n = 1.0f;
                n2 = 1.0f;
                n3 = 1.0f;
            }
            this.color = new Color(n, n2, n3);
        }
        
        public String getTranslatedText() {
            return Translator.getText(this.text);
        }
        
        public Color getColor() {
            return this.color;
        }
        
        public float getR() {
            return this.color.r;
        }
        
        public float getG() {
            return this.color.g;
        }
        
        public float getB() {
            return this.color.b;
        }
        
        public String getCodes() {
            return this.codes;
        }
        
        public String getTextGuid() {
            return this.text;
        }
    }
}
