// 
// Decompiled by Procyon v0.5.36
// 

package zombie.profanity.locales;

import java.util.regex.Matcher;
import zombie.profanity.Phonizer;

public class LocaleGerman extends LocaleEnglish
{
    public LocaleGerman(final String s) {
        super(s);
    }
    
    @Override
    protected void Init() {
        this.storeVowelsAmount = 3;
        super.Init();
        this.addPhonizer(new Phonizer("ringelS", "(?<ringelS>\u00c3\u0178)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "S");
                }
            }
        });
    }
}
