// 
// Decompiled by Procyon v0.5.36
// 

package zombie.profanity;

import java.util.regex.Matcher;

public class Phonizer
{
    private String name;
    private String regex;
    
    public Phonizer(final String name, final String regex) {
        this.name = name;
        this.regex = regex;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getRegex() {
        return this.regex;
    }
    
    public void execute(final Matcher matcher, final StringBuffer sb) {
        if (matcher.group(this.name) != null) {
            matcher.appendReplacement(sb, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
    }
}
