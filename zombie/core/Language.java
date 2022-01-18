// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

public final class Language
{
    private final int index;
    private final String name;
    private final String text;
    private final String charset;
    private final String base;
    private final boolean azerty;
    
    Language(final int index, final String name, final String text, final String charset, final String base, final boolean azerty) {
        this.index = index;
        this.name = name;
        this.text = text;
        this.charset = charset;
        this.base = base;
        this.azerty = azerty;
    }
    
    public int index() {
        return this.index;
    }
    
    public String name() {
        return this.name;
    }
    
    public String text() {
        return this.text;
    }
    
    public String charset() {
        return this.charset;
    }
    
    public String base() {
        return this.base;
    }
    
    public boolean isAzerty() {
        return this.azerty;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static Language fromIndex(final int n) {
        return Languages.instance.getByIndex(n);
    }
    
    public static Language FromString(final String s) {
        Language language = Languages.instance.getByName(s);
        if (language == null) {
            language = Languages.instance.getDefaultLanguage();
        }
        return language;
    }
}
