// 
// Decompiled by Procyon v0.5.36
// 

package zombie.profanity;

import java.io.File;
import java.util.regex.Matcher;
import zombie.profanity.locales.LocaleChinese;
import zombie.profanity.locales.LocaleGerman;
import zombie.profanity.locales.LocaleEnglish;
import java.util.HashMap;
import java.util.regex.Pattern;
import zombie.profanity.locales.Locale;
import java.util.Map;

public class ProfanityFilter
{
    public static boolean DEBUG;
    private Map<String, Locale> locales;
    private Locale locale;
    private Locale localeDefault;
    private Pattern prePattern;
    private boolean enabled;
    public static String LOCALES_DIR;
    private static ProfanityFilter instance;
    
    public static ProfanityFilter getInstance() {
        if (ProfanityFilter.instance == null) {
            ProfanityFilter.instance = new ProfanityFilter();
        }
        return ProfanityFilter.instance;
    }
    
    private ProfanityFilter() {
        this.locales = new HashMap<String, Locale>();
        this.enabled = true;
        this.addLocale(new LocaleEnglish("EN"), true);
        this.addLocale(new LocaleGerman("GER"));
        this.addLocale(new LocaleChinese("CHIN"));
        this.prePattern = Pattern.compile("(?<spaced>(?:(?:\\s|\\W)[\\w\\$@](?=\\s|\\W)){2,20})|(?<word>[\\w'\\$@_-]+)");
    }
    
    public static void printDebug(final String x) {
        if (ProfanityFilter.DEBUG) {
            System.out.println(x);
        }
    }
    
    public void enable(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public int getFilterWordsCount() {
        if (this.locale != null) {
            return this.locale.getFilterWordsCount();
        }
        return 0;
    }
    
    public void addLocale(final Locale locale) {
        this.addLocale(locale, false);
    }
    
    public void addLocale(final Locale locale, final boolean b) {
        this.locales.put(locale.getID(), locale);
        if (b) {
            this.locale = locale;
            this.localeDefault = locale;
        }
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void addWhiteListWord(final String s) {
        if (this.locale != null) {
            this.locale.addWhiteListWord(s);
        }
    }
    
    public void removeWhiteListWord(final String s) {
        if (this.locale != null) {
            this.locale.removeWhiteListWord(s);
        }
    }
    
    public void addFilterWord(final String s) {
        if (this.locale != null) {
            this.locale.addFilterWord(s);
        }
    }
    
    public void removeFilterWord(final String s) {
        if (this.locale != null) {
            this.locale.removeFilterWord(s);
        }
    }
    
    public void setLocale(final String s) {
        if (this.locales.containsKey(s)) {
            this.locale = this.locales.get(s);
        }
        else {
            this.locale = this.localeDefault;
        }
    }
    
    public String filterString(final String input) {
        if (this.enabled && this.locale != null && input != null && this.locale.getFilterWordsCount() > 0) {
            try {
                final StringBuffer sb = new StringBuffer();
                final Matcher matcher = this.prePattern.matcher(input);
                while (matcher.find()) {
                    if (matcher.group("word") != null) {
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(this.locale.filterWord(matcher.group("word"), true)));
                    }
                    else {
                        if (matcher.group("spaced") == null) {
                            continue;
                        }
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.locale.filterWord(matcher.group("spaced").replaceAll("\\s+", "")))));
                    }
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
            catch (Exception ex) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, input));
            }
        }
        return input;
    }
    
    public String validateString(final String s) {
        return this.validateString(s, true, true, true);
    }
    
    public String validateString(final String input, final boolean b, final boolean b2, final boolean b3) {
        if (this.enabled && this.locale != null && input != null && this.locale.getFilterWordsCount() > 0) {
            try {
                int n = 0;
                final StringBuilder sb = new StringBuilder();
                final Matcher matcher = this.prePattern.matcher(input);
                while (matcher.find()) {
                    if (b && matcher.group("word") != null) {
                        final String validateWord = this.locale.validateWord(matcher.group("word"), b2);
                        if (validateWord == null) {
                            continue;
                        }
                        if (n != 0) {
                            sb.append(", ");
                        }
                        sb.append(validateWord);
                        n = 1;
                    }
                    else {
                        if (!b3 || matcher.group("spaced") == null) {
                            continue;
                        }
                        final String validateWord2 = this.locale.validateWord(matcher.group("spaced").replaceAll("\\s+", ""), false);
                        if (validateWord2 == null) {
                            continue;
                        }
                        if (n != 0) {
                            sb.append(", ");
                        }
                        sb.append(validateWord2);
                        n = 1;
                    }
                }
                return (n != 0) ? sb.toString() : null;
            }
            catch (Exception ex) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, input));
                ex.printStackTrace();
            }
        }
        return "Failed to parse string :(.";
    }
    
    static {
        ProfanityFilter.DEBUG = false;
        ProfanityFilter.LOCALES_DIR = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, File.separator, File.separator);
    }
}
