// 
// Decompiled by Procyon v0.5.36
// 

package zombie.profanity.locales;

import java.util.regex.Matcher;
import zombie.profanity.Phonizer;

public class LocaleEnglish extends Locale
{
    public LocaleEnglish(final String s) {
        super(s);
    }
    
    @Override
    protected void Init() {
        this.storeVowelsAmount = 3;
        this.addFilterRawWord("ass");
        this.addPhonizer(new Phonizer("strt", "(?<strt>^(?:KN|GN|PN|AE|WR))") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, matcher.group(this.getName()).toString().substring(1, 2));
                }
            }
        });
        this.addPhonizer(new Phonizer("dropY", "(?<dropY>(?<=M)B$)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "");
                }
            }
        });
        this.addPhonizer(new Phonizer("dropB", "(?<dropB>(?<=M)B$)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "");
                }
            }
        });
        this.addPhonizer(new Phonizer("z", "(?<z>Z)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "S");
                }
            }
        });
        this.addPhonizer(new Phonizer("ck", "(?<ck>CK)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "K");
                }
            }
        });
        this.addPhonizer(new Phonizer("q", "(?<q>Q)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "K");
                }
            }
        });
        this.addPhonizer(new Phonizer("v", "(?<v>V)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "F");
                }
            }
        });
        this.addPhonizer(new Phonizer("xS", "(?<xS>^X)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "S");
                }
            }
        });
        this.addPhonizer(new Phonizer("xKS", "(?<xKS>(?<=\\w)X)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "KS");
                }
            }
        });
        this.addPhonizer(new Phonizer("ph", "(?<ph>PH)") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "F");
                }
            }
        });
        this.addPhonizer(new Phonizer("c", "(?<c>C(?=[AUOIE]))") {
            @Override
            public void execute(final Matcher matcher, final StringBuffer sb) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(sb, "K");
                }
            }
        });
    }
}
