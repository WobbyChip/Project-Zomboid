// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import java.io.File;
import zombie.core.Translator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import zombie.core.Language;

public final class RadioTranslationData
{
    private String filePath;
    private String guid;
    private String language;
    private Language languageEnum;
    private int version;
    private final ArrayList<String> translators;
    private final Map<String, String> translations;
    
    public RadioTranslationData(final String filePath) {
        this.version = -1;
        this.translators = new ArrayList<String>();
        this.translations = new HashMap<String, String>();
        this.filePath = filePath;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public String getGuid() {
        return this.guid;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public Language getLanguageEnum() {
        return this.languageEnum;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public int getTranslationCount() {
        return this.translations.size();
    }
    
    public ArrayList<String> getTranslators() {
        return this.translators;
    }
    
    public boolean validate() {
        return this.guid != null && this.language != null && this.version >= 0;
    }
    
    public boolean loadTranslations() {
        boolean b = false;
        if (Translator.getLanguage() != this.languageEnum) {
            System.out.println("Radio translations trying to load language that is not the current language...");
            return false;
        }
        try {
            final File file = new File(this.filePath);
            if (file.exists() && !file.isDirectory()) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath), Charset.forName(this.languageEnum.charset())));
                boolean b2 = false;
                final ArrayList<String> list = new ArrayList<String>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String trim = line.trim();
                    if (trim.equals("[Translations]")) {
                        b2 = true;
                    }
                    else {
                        if (!b2) {
                            continue;
                        }
                        if (trim.equals("[Collection]")) {
                            String s = null;
                            String line2;
                            while ((line2 = bufferedReader.readLine()) != null) {
                                final String trim2 = line2.trim();
                                if (trim2.equals("[/Collection]")) {
                                    break;
                                }
                                final String[] split = trim2.split("=", 2);
                                if (split.length != 2) {
                                    continue;
                                }
                                final String trim3 = split[0].trim();
                                final String trim4 = split[1].trim();
                                if (trim3.equals("text")) {
                                    s = trim4;
                                }
                                else {
                                    if (!trim3.equals("member")) {
                                        continue;
                                    }
                                    list.add(trim4);
                                }
                            }
                            if (s != null && list.size() > 0) {
                                final Iterator<String> iterator = list.iterator();
                                while (iterator.hasNext()) {
                                    this.translations.put(iterator.next(), s);
                                }
                            }
                            list.clear();
                        }
                        else {
                            if (trim.equals("[/Translations]")) {
                                b = true;
                                break;
                            }
                            final String[] split2 = trim.split("=", 2);
                            if (split2.length != 2) {
                                continue;
                            }
                            this.translations.put(split2[0].trim(), split2[1].trim());
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            b = false;
        }
        return b;
    }
    
    public String getTranslation(final String s) {
        if (this.translations.containsKey(s)) {
            return this.translations.get(s);
        }
        return null;
    }
    
    public static RadioTranslationData ReadFile(final String s) {
        final RadioTranslationData radioTranslationData = new RadioTranslationData(s);
        final File file = new File(s);
        if (file.exists() && !file.isDirectory()) {
            try {
                final FileInputStream in = new FileInputStream(s);
                try {
                    String line;
                    while ((line = new BufferedReader(new InputStreamReader(in)).readLine()) != null) {
                        final String[] split = line.split("=");
                        if (split.length > 1) {
                            final String trim = split[0].trim();
                            String s2 = "";
                            for (int i = 1; i < split.length; ++i) {
                                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, split[i]);
                            }
                            final String trim2 = s2.trim();
                            if (trim.equals("guid")) {
                                radioTranslationData.guid = trim2;
                            }
                            else if (trim.equals("language")) {
                                radioTranslationData.language = trim2;
                            }
                            else if (trim.equals("version")) {
                                radioTranslationData.version = Integer.parseInt(trim2);
                            }
                            else if (trim.equals("translator")) {
                                final String[] split2 = trim2.split(",");
                                if (split2.length > 0) {
                                    final String[] array = split2;
                                    for (int length = array.length, j = 0; j < length; ++j) {
                                        radioTranslationData.translators.add(array[j]);
                                    }
                                }
                            }
                        }
                        if (line.trim().equals("[/Info]")) {
                            break;
                        }
                    }
                    in.close();
                }
                catch (Throwable t) {
                    try {
                        in.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        boolean b = false;
        if (radioTranslationData.language != null) {
            for (final Language languageEnum : Translator.getAvailableLanguage()) {
                if (languageEnum.toString().equals(radioTranslationData.language)) {
                    radioTranslationData.languageEnum = languageEnum;
                    b = true;
                    break;
                }
            }
        }
        if (!b && radioTranslationData.language != null) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, radioTranslationData.language));
            return null;
        }
        if (radioTranslationData.guid != null && radioTranslationData.language != null && radioTranslationData.version >= 0) {
            return radioTranslationData;
        }
        return null;
    }
}
