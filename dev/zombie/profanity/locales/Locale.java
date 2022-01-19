// 
// Decompiled by Procyon v0.5.36
// 

package zombie.profanity.locales;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.Arrays;
import zombie.profanity.ProfanityFilter;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import zombie.profanity.Phonizer;
import java.util.Map;

public abstract class Locale
{
    protected String id;
    protected int storeVowelsAmount;
    protected String phoneticRules;
    protected Map<String, Phonizer> phonizers;
    protected Map<String, String> filterWords;
    protected List<String> filterWordsRaw;
    protected List<String> filterContains;
    protected ArrayList<String> whitelistWords;
    protected Pattern pattern;
    private Pattern preProcessLeet;
    private Pattern preProcessDoubles;
    private Pattern preProcessVowels;
    
    protected Locale(final String id) {
        this.storeVowelsAmount = 3;
        this.phoneticRules = "";
        this.phonizers = new HashMap<String, Phonizer>();
        this.filterWords = new HashMap<String, String>();
        this.filterWordsRaw = new ArrayList<String>();
        this.filterContains = new ArrayList<String>();
        this.whitelistWords = new ArrayList<String>();
        this.preProcessLeet = Pattern.compile("(?<leet>[\\$@34701])\\k<leet>*|(?<nonWord>[^A-Z\\s\\$@34701]+)");
        this.preProcessDoubles = Pattern.compile("(?<doublechar>[A-Z])\\k<doublechar>+");
        this.preProcessVowels = Pattern.compile("(?<vowel>[AOUIE])");
        this.id = id;
        this.Init();
        this.finalizeData();
        this.loadFilterWords();
        this.loadFilterContains();
        this.loadWhiteListWords();
        ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.id));
    }
    
    public String getID() {
        return this.id;
    }
    
    public String getPhoneticRules() {
        return this.phoneticRules;
    }
    
    public int getFilterWordsCount() {
        return this.filterWords.size();
    }
    
    protected abstract void Init();
    
    public void addWhiteListWord(String trim) {
        trim = trim.toUpperCase().trim();
        if (!this.whitelistWords.contains(trim)) {
            this.whitelistWords.add(trim);
        }
    }
    
    public void removeWhiteListWord(String trim) {
        trim = trim.toUpperCase().trim();
        if (this.whitelistWords.contains(trim)) {
            this.whitelistWords.remove(trim);
        }
    }
    
    public boolean isWhiteListedWord(final String s) {
        return this.whitelistWords.contains(s.toUpperCase().trim());
    }
    
    public void addFilterWord(final String s) {
        final String phonizeWord = this.phonizeWord(s);
        if (phonizeWord.length() > 2) {
            String s2 = "";
            if (this.filterWords.containsKey(phonizeWord)) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, (String)this.filterWords.get(phonizeWord));
            }
            ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, phonizeWord));
            this.filterWords.put(phonizeWord, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s.toLowerCase()));
        }
        else {
            ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, phonizeWord));
        }
    }
    
    public void removeFilterWord(final String s) {
        final String phonizeWord = this.phonizeWord(s);
        if (this.filterWords.containsKey(phonizeWord)) {
            this.filterWords.remove(phonizeWord);
        }
    }
    
    public void addFilterContains(final String s) {
        if (s != null && !s.isEmpty() && !this.filterContains.contains(s.toUpperCase())) {
            this.filterContains.add(s.toUpperCase());
        }
    }
    
    public void removeFilterContains(final String s) {
        this.filterContains.remove(s.toUpperCase());
    }
    
    public void addFilterRawWord(final String s) {
        if (s != null && !s.isEmpty() && !this.filterWordsRaw.contains(s.toUpperCase())) {
            this.filterWordsRaw.add(s.toUpperCase());
        }
    }
    
    public void removeFilterWordRaw(final String s) {
        this.filterWordsRaw.remove(s.toUpperCase());
    }
    
    protected String repeatString(final int n, final char val) {
        final char[] array = new char[n];
        Arrays.fill(array, val);
        return new String(array);
    }
    
    protected boolean containsIgnoreCase(final String s, final String other) {
        if (s == null || other == null) {
            return false;
        }
        final int length = other.length();
        if (length == 0) {
            return true;
        }
        for (int i = s.length() - length; i >= 0; --i) {
            if (s.regionMatches(true, i, other, 0, length)) {
                return true;
            }
        }
        return false;
    }
    
    public String filterWord(final String s) {
        return this.filterWord(s, false);
    }
    
    public String filterWord(String replaceAll, final boolean b) {
        if (this.isWhiteListedWord(replaceAll)) {
            return replaceAll;
        }
        if (this.filterWords.containsKey(this.phonizeWord(replaceAll))) {
            return new String(new char[replaceAll.length()]).replace('\0', '*');
        }
        if (this.filterWordsRaw.size() > 0) {
            for (int i = 0; i < this.filterWordsRaw.size(); ++i) {
                if (replaceAll.equalsIgnoreCase(this.filterWordsRaw.get(i))) {
                    return new String(new char[replaceAll.length()]).replace('\0', '*');
                }
            }
        }
        if (b) {
            for (int j = 0; j < this.filterContains.size(); ++j) {
                final String s = this.filterContains.get(j);
                if (this.containsIgnoreCase(replaceAll, s)) {
                    replaceAll = replaceAll.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Pattern.quote(s)), this.repeatString(s.length(), '*'));
                }
            }
        }
        return replaceAll;
    }
    
    public String validateWord(final String s, final boolean b) {
        if (this.isWhiteListedWord(s)) {
            return null;
        }
        if (this.filterWords.containsKey(this.phonizeWord(s))) {
            return s;
        }
        if (this.filterWordsRaw.size() > 0) {
            for (int i = 0; i < this.filterWordsRaw.size(); ++i) {
                if (s.equalsIgnoreCase(this.filterWordsRaw.get(i))) {
                    return s;
                }
            }
        }
        if (b) {
            for (int j = 0; j < this.filterContains.size(); ++j) {
                final String s2 = this.filterContains.get(j);
                if (this.containsIgnoreCase(s, s2)) {
                    return s2.toLowerCase();
                }
            }
        }
        return null;
    }
    
    public String returnMatchSetForWord(final String s) {
        final String phonizeWord = this.phonizeWord(s);
        if (this.filterWords.containsKey(phonizeWord)) {
            return this.filterWords.get(phonizeWord);
        }
        return null;
    }
    
    public String returnPhonizedWord(final String s) {
        return this.phonizeWord(s);
    }
    
    protected String phonizeWord(String s) {
        s = s.toUpperCase().trim();
        if (this.whitelistWords.contains(s)) {
            return s;
        }
        s = this.preProcessWord(s);
        if (this.phonizers.size() <= 0) {
            return s;
        }
        final Matcher matcher = this.pattern.matcher(s);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            for (final Map.Entry<String, Phonizer> entry : this.phonizers.entrySet()) {
                if (matcher.group(entry.getKey()) != null) {
                    entry.getValue().execute(matcher, sb);
                    break;
                }
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    private String preProcessWord(final String input) {
        final Matcher matcher = this.preProcessLeet.matcher(input);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            if (matcher.group("leet") != null) {
                final String string = matcher.group("leet").toString();
                switch (string) {
                    case "$": {
                        matcher.appendReplacement(sb, "S");
                        continue;
                    }
                    case "4":
                    case "@": {
                        matcher.appendReplacement(sb, "A");
                        continue;
                    }
                    case "3": {
                        matcher.appendReplacement(sb, "E");
                        continue;
                    }
                    case "7": {
                        matcher.appendReplacement(sb, "T");
                        continue;
                    }
                    case "0": {
                        matcher.appendReplacement(sb, "O");
                        continue;
                    }
                    case "1": {
                        matcher.appendReplacement(sb, "I");
                        continue;
                    }
                }
            }
            else {
                if (matcher.group("nonWord") == null) {
                    continue;
                }
                matcher.appendReplacement(sb, "");
            }
        }
        matcher.appendTail(sb);
        final Matcher matcher2 = this.preProcessDoubles.matcher(sb.toString());
        sb.delete(0, sb.capacity());
        while (matcher2.find()) {
            if (matcher2.group("doublechar") != null) {
                matcher2.appendReplacement(sb, "${doublechar}");
            }
        }
        matcher2.appendTail(sb);
        final Matcher matcher3 = this.preProcessVowels.matcher(sb.toString());
        sb.delete(0, sb.capacity());
        int n2 = 0;
        while (matcher3.find()) {
            if (matcher3.group("vowel") != null) {
                if (n2 < this.storeVowelsAmount) {
                    matcher3.appendReplacement(sb, "${vowel}");
                    ++n2;
                }
                else {
                    matcher3.appendReplacement(sb, "");
                }
            }
        }
        matcher3.appendTail(sb);
        return sb.toString();
    }
    
    protected void addPhonizer(final Phonizer phonizer) {
        if (phonizer != null && !this.phonizers.containsKey(phonizer.getName())) {
            this.phonizers.put(phonizer.getName(), phonizer);
        }
    }
    
    protected void finalizeData() {
        this.phoneticRules = "";
        final int size = this.phonizers.size();
        int n = 0;
        final Iterator<Phonizer> iterator = this.phonizers.values().iterator();
        while (iterator.hasNext()) {
            this.phoneticRules = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.phoneticRules, iterator.next().getRegex());
            if (++n < size) {
                this.phoneticRules = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.phoneticRules);
            }
        }
        ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.phoneticRules));
        this.pattern = Pattern.compile(this.phoneticRules);
    }
    
    protected void loadFilterWords() {
        try {
            final FileReader in = new FileReader(new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ProfanityFilter.LOCALES_DIR, this.id))));
            final BufferedReader bufferedReader = new BufferedReader(in);
            int n = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.addFilterWord(line);
                ++n;
            }
            in.close();
            ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void loadFilterContains() {
        try {
            final FileReader in = new FileReader(new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ProfanityFilter.LOCALES_DIR, this.id))));
            final BufferedReader bufferedReader = new BufferedReader(in);
            int n = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("//")) {
                    continue;
                }
                this.addFilterContains(line);
                ++n;
            }
            in.close();
            ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void loadWhiteListWords() {
        try {
            final FileReader in = new FileReader(new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ProfanityFilter.LOCALES_DIR, this.id))));
            final BufferedReader bufferedReader = new BufferedReader(in);
            int n = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.addWhiteListWord(line);
                ++n;
            }
            in.close();
            ProfanityFilter.printDebug(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
