// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.regex.Matcher;
import zombie.debug.DebugOptions;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import java.util.Iterator;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugLog;
import java.io.IOException;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.io.FileWriter;
import java.util.ArrayList;

public final class Translator
{
    private static ArrayList<Language> availableLanguage;
    public static boolean debug;
    private static FileWriter debugFile;
    private static boolean debugErrors;
    private static final HashSet<String> debugItemEvolvedRecipeName;
    private static final HashSet<String> debugItem;
    private static final HashSet<String> debugMultiStageBuild;
    private static final HashSet<String> debugRecipe;
    private static final HashMap<String, String> moodles;
    private static final HashMap<String, String> ui;
    private static final HashMap<String, String> survivalGuide;
    private static final HashMap<String, String> contextMenu;
    private static final HashMap<String, String> farming;
    private static final HashMap<String, String> recipe;
    private static final HashMap<String, String> igui;
    private static final HashMap<String, String> sandbox;
    private static final HashMap<String, String> tooltip;
    private static final HashMap<String, String> challenge;
    private static final HashSet<String> missing;
    private static ArrayList<String> azertyLanguages;
    private static final HashMap<String, String> news;
    private static final HashMap<String, String> stash;
    private static final HashMap<String, String> multiStageBuild;
    private static final HashMap<String, String> moveables;
    private static final HashMap<String, String> makeup;
    private static final HashMap<String, String> gameSound;
    private static final HashMap<String, String> dynamicRadio;
    private static final HashMap<String, String> items;
    private static final HashMap<String, String> itemName;
    private static final HashMap<String, String> itemEvolvedRecipeName;
    private static final HashMap<String, String> recordedMedia;
    private static final HashMap<String, String> recordedMedia_EN;
    public static Language language;
    private static final String newsHeader = "<IMAGE:media/ui/dot.png> <SIZE:small> ";
    
    public static void loadFiles() {
        Translator.language = null;
        Translator.availableLanguage = null;
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (Translator.debug) {
            try {
                if (Translator.debugFile != null) {
                    Translator.debugFile.close();
                }
                Translator.debugFile = new FileWriter(file);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Translator.moodles.clear();
        Translator.ui.clear();
        Translator.survivalGuide.clear();
        Translator.items.clear();
        Translator.itemName.clear();
        Translator.contextMenu.clear();
        Translator.farming.clear();
        Translator.recipe.clear();
        Translator.igui.clear();
        Translator.sandbox.clear();
        Translator.tooltip.clear();
        Translator.challenge.clear();
        Translator.news.clear();
        Translator.missing.clear();
        Translator.stash.clear();
        Translator.multiStageBuild.clear();
        Translator.moveables.clear();
        Translator.makeup.clear();
        Translator.gameSound.clear();
        Translator.dynamicRadio.clear();
        Translator.itemEvolvedRecipeName.clear();
        Translator.recordedMedia.clear();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getLanguage()));
        Translator.debugErrors = false;
        fillMapFromFile("Tooltip", Translator.tooltip);
        fillMapFromFile("IG_UI", Translator.igui);
        fillMapFromFile("Recipes", Translator.recipe);
        fillMapFromFile("Farming", Translator.farming);
        fillMapFromFile("ContextMenu", Translator.contextMenu);
        fillMapFromFile("SurvivalGuide", Translator.survivalGuide);
        fillMapFromFile("UI", Translator.ui);
        fillMapFromFile("Items", Translator.items);
        fillMapFromFile("ItemName", Translator.itemName);
        fillMapFromFile("Moodles", Translator.moodles);
        fillMapFromFile("Sandbox", Translator.sandbox);
        fillMapFromFile("Challenge", Translator.challenge);
        fillMapFromFile("Stash", Translator.stash);
        fillMapFromFile("MultiStageBuild", Translator.multiStageBuild);
        fillMapFromFile("Moveables", Translator.moveables);
        fillMapFromFile("MakeUp", Translator.makeup);
        fillMapFromFile("GameSound", Translator.gameSound);
        fillMapFromFile("DynamicRadio", Translator.dynamicRadio);
        fillMapFromFile("EvolvedRecipeName", Translator.itemEvolvedRecipeName);
        fillMapFromFile("Recorded_Media", Translator.recordedMedia);
        fillNewsFromFile(Translator.news);
        if (Translator.debug) {
            if (Translator.debugErrors) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
            }
            Translator.debugItemEvolvedRecipeName.clear();
            Translator.debugItem.clear();
            Translator.debugMultiStageBuild.clear();
            Translator.debugRecipe.clear();
        }
        PerkFactory.initTranslations();
    }
    
    private static void fillNewsFromFile(final HashMap<String, String> hashMap) {
        final HashMap<String, News> hashMap2 = new HashMap<String, News>();
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final String modDir = ZomboidFileSystem.instance.getModDir(modIDs.get(i));
            if (modDir != null) {
                tryFillNewsFromFile(modDir, hashMap, hashMap2, getLanguage());
                if (getLanguage() != getDefaultLanguage()) {
                    tryFillNewsFromFile(modDir, hashMap, hashMap2, getDefaultLanguage());
                }
            }
        }
        tryFillNewsFromFile(".", hashMap, hashMap2, getLanguage());
        if (getLanguage() != getDefaultLanguage()) {
            tryFillNewsFromFile(".", hashMap, hashMap2, getDefaultLanguage());
        }
        for (final News news : hashMap2.values()) {
            hashMap.put(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, news.version), news.toRichText());
        }
        hashMap2.clear();
    }
    
    private static void tryFillNewsFromFile(final String s, final HashMap<String, String> hashMap, final HashMap<String, News> hashMap2, final Language language) {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lzombie/core/Language;Ljava/lang/String;Lzombie/core/Language;)Ljava/lang/String;, s, File.separator, File.separator, File.separator, File.separator, File.separator, language, File.separator, language));
        if (file.exists()) {
            doNews(file, hashMap2, language);
        }
    }
    
    private static void doNews(final File file, final HashMap<String, News> hashMap, final Language language) {
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final InputStreamReader in2 = new InputStreamReader(in, Charset.forName(language.charset()));
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in2);
                    try {
                        News news = null;
                        ArrayList<String> list = null;
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.trim().isEmpty()) {
                                continue;
                            }
                            if (line.startsWith("[VERSION]")) {
                                final String trim = line.replaceFirst("\\[VERSION\\]", "").trim();
                                if (hashMap.containsKey(trim)) {
                                    news = null;
                                    list = null;
                                }
                                else {
                                    hashMap.put(trim, news = new News(trim));
                                    list = null;
                                }
                            }
                            if (news == null) {
                                continue;
                            }
                            if (line.startsWith("[SECTION]")) {
                                list = news.getOrCreateSectionList(line.replaceFirst("\\[SECTION\\]", "").trim());
                            }
                            else if (line.startsWith("[NEWS]")) {
                                list = news.getOrCreateSectionList("[New]");
                            }
                            else if (line.startsWith("[BALANCE]")) {
                                list = news.getOrCreateSectionList("[Balance]");
                            }
                            else if (line.startsWith("[BUG FIX]")) {
                                list = news.getOrCreateSectionList("[Bug Fix]");
                            }
                            else {
                                if (list == null) {
                                    continue;
                                }
                                addNewsLine(line, list);
                            }
                        }
                        bufferedReader.close();
                    }
                    catch (Throwable t) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                    in2.close();
                }
                catch (Throwable t2) {
                    try {
                        in2.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
                in.close();
            }
            catch (Throwable t3) {
                try {
                    in.close();
                }
                catch (Throwable exception3) {
                    t3.addSuppressed(exception3);
                }
                throw t3;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    private static void addNewsLine(String e, final ArrayList<String> list) {
        if (e.startsWith("[BOLD]")) {
            e = e.replaceFirst("\\[BOLD\\]", "<IMAGE:media/ui/dot.png> <SIZE:medium>");
            list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            return;
        }
        if (e.startsWith("[DOT2]")) {
            e = e.replaceFirst("\\[DOT2\\]", "<IMAGE:media/ui/dot2.png> <SIZE:small>");
            list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            return;
        }
        if (e.startsWith("[NODOT]")) {
            e = e.replaceFirst("\\[NODOT\\]", " <SIZE:small> ");
            e = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e);
            list.add(e);
            return;
        }
        list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, " <INDENT:%d> ".formatted(new Object[] { 21 - 7 }), e));
    }
    
    public static ArrayList<String> getNewsVersions() {
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        list.addAll((Collection<? extends Comparable>)Translator.news.keySet());
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, list.get(i).replace("News_", "").replace("_Disclaimer", ""));
        }
        Collections.sort((List<Comparable>)list);
        return list;
    }
    
    private static void tryFillMapFromFile(final String s, final String s2, final HashMap<String, String> hashMap, final Language language) {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lzombie/core/Language;Ljava/lang/String;Ljava/lang/String;Lzombie/core/Language;)Ljava/lang/String;, s, File.separator, File.separator, File.separator, File.separator, File.separator, language, File.separator, s2, language));
        if (file.exists()) {
            parseFile(file, hashMap, language);
        }
    }
    
    private static void tryFillMapFromMods(final String s, final HashMap<String, String> hashMap, final Language language) {
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final String modDir = ZomboidFileSystem.instance.getModDir(modIDs.get(i));
            if (modDir != null) {
                tryFillMapFromFile(modDir, s, hashMap, language);
            }
        }
    }
    
    public static void addLanguageToList(Language byName, final ArrayList<Language> list) {
        if (byName == null) {
            return;
        }
        if (list.contains(byName)) {
            return;
        }
        list.add(byName);
        if (byName.base() == null) {
            return;
        }
        byName = Languages.instance.getByName(byName.base());
        addLanguageToList(byName, list);
    }
    
    private static void fillMapFromFile(final String s, final HashMap<String, String> hashMap) {
        final ArrayList<Language> list = new ArrayList<Language>();
        addLanguageToList(getLanguage(), list);
        addLanguageToList(getDefaultLanguage(), list);
        for (int i = 0; i < list.size(); ++i) {
            final Language language = list.get(i);
            tryFillMapFromMods(s, hashMap, language);
            tryFillMapFromFile(ZomboidFileSystem.instance.base.getPath(), s, hashMap, language);
        }
        list.clear();
    }
    
    private static void parseFile(final File file, final HashMap<String, String> hashMap, final Language language) {
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final InputStreamReader in2 = new InputStreamReader(in, Charset.forName(language.charset()));
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in2);
                    try {
                        bufferedReader.readLine();
                        int n = 0;
                        String key = "";
                        String s = "";
                        int n2 = 1;
                        String replace = file.getName().replace(invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getDefaultLanguage()), invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getLanguage()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            ++n2;
                            try {
                                if (line.contains("=") && line.contains("\"")) {
                                    if (line.trim().startsWith("Recipe_")) {
                                        key = line.split("=")[0].replaceAll("Recipe_", "").replaceAll("_", " ").trim();
                                        s = line.split("=")[1];
                                        s = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                                    }
                                    else if (line.trim().startsWith("DisplayName")) {
                                        final String[] split = line.split("=");
                                        if (line.trim().startsWith("DisplayName_")) {
                                            key = split[0].replaceAll("DisplayName_", "").trim();
                                        }
                                        else {
                                            key = split[0].replaceAll("DisplayName", "").trim();
                                        }
                                        if ("Anti_depressants".equals(key)) {
                                            key = "Antidepressants";
                                        }
                                        s = split[1];
                                        s = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                                    }
                                    else if (line.trim().startsWith("EvolvedRecipeName_")) {
                                        final String[] split2 = line.split("=");
                                        key = split2[0].replaceAll("EvolvedRecipeName_", "").trim();
                                        s = split2[1];
                                        s = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                                    }
                                    else if (line.trim().startsWith("ItemName_")) {
                                        final String[] split3 = line.split("=");
                                        key = split3[0].replaceAll("ItemName_", "").trim();
                                        s = split3[1];
                                        s = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                                    }
                                    else {
                                        key = line.split("=")[0].trim();
                                        s = line.substring(line.indexOf("=") + 1);
                                        s = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                                        if (line.contains("..")) {
                                            n = 1;
                                        }
                                    }
                                }
                                else if (!line.contains("--") && !line.trim().isEmpty() && (line.trim().endsWith("..") || n != 0)) {
                                    n = 1;
                                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
                                }
                                else {
                                    n = 0;
                                }
                                if (n != 0 && line.trim().endsWith("..")) {
                                    continue;
                                }
                                if (!key.isEmpty()) {
                                    if (!hashMap.containsKey(key)) {
                                        hashMap.put(key, s);
                                        if (hashMap == Translator.recordedMedia && language == getDefaultLanguage()) {
                                            Translator.recordedMedia_EN.put(key, s);
                                        }
                                        if (Translator.debug && language == getDefaultLanguage() && getLanguage() != getDefaultLanguage()) {
                                            if (replace != null) {
                                                debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, replace));
                                                replace = null;
                                            }
                                            debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, s));
                                            Translator.debugErrors = true;
                                        }
                                    }
                                    else if (Translator.debug && language == getDefaultLanguage() && getLanguage() != getDefaultLanguage()) {
                                        final String s2 = hashMap.get(key);
                                        final String s3 = s;
                                        if (countSubstitutions(s2) != countSubstitutions(s3)) {
                                            debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/Language;Ljava/lang/String;Lzombie/core/Language;Ljava/lang/String;)Ljava/lang/String;, key, getDefaultLanguage(), s3, getLanguage(), s2));
                                            Translator.debugErrors = true;
                                        }
                                    }
                                }
                                n = 0;
                                s = "";
                                key = "";
                            }
                            catch (Exception ex) {
                                if (!Translator.debug) {
                                    continue;
                                }
                                if (replace != null) {
                                    debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, replace));
                                    replace = null;
                                }
                                debugwrite(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, n2, key, s));
                                if (Translator.debugFile != null) {
                                    ex.printStackTrace(new PrintWriter(Translator.debugFile));
                                }
                                debugwrite("\r\n");
                                Translator.debugErrors = true;
                            }
                        }
                        bufferedReader.close();
                    }
                    catch (Throwable t) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                    in2.close();
                }
                catch (Throwable t2) {
                    try {
                        in2.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
                in.close();
            }
            catch (Throwable t3) {
                try {
                    in.close();
                }
                catch (Throwable exception3) {
                    t3.addSuppressed(exception3);
                }
                throw t3;
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static String getText(final String s) {
        return getTextInternal(s, false);
    }
    
    public static String getTextOrNull(final String s) {
        return getTextInternal(s, true);
    }
    
    private static String getTextInternal(final String s, final boolean b) {
        if (Translator.ui == null) {
            loadFiles();
        }
        String s2 = null;
        if (s.startsWith("UI_")) {
            s2 = Translator.ui.get(s);
        }
        else if (s.startsWith("Moodles_")) {
            s2 = Translator.moodles.get(s);
        }
        else if (s.startsWith("SurvivalGuide_")) {
            s2 = Translator.survivalGuide.get(s);
        }
        else if (s.startsWith("Farming_")) {
            s2 = Translator.farming.get(s);
        }
        else if (s.startsWith("IGUI_")) {
            s2 = Translator.igui.get(s);
        }
        else if (s.startsWith("ContextMenu_")) {
            s2 = Translator.contextMenu.get(s);
        }
        else if (s.startsWith("GameSound_")) {
            s2 = Translator.gameSound.get(s);
        }
        else if (s.startsWith("Sandbox_")) {
            s2 = Translator.sandbox.get(s);
        }
        else if (s.startsWith("Tooltip_")) {
            s2 = Translator.tooltip.get(s);
        }
        else if (s.startsWith("Challenge_")) {
            s2 = Translator.challenge.get(s);
        }
        else if (s.startsWith("MakeUp")) {
            s2 = Translator.makeup.get(s);
        }
        else if (s.startsWith("News_")) {
            s2 = Translator.news.get(s);
        }
        else if (s.startsWith("Stash_")) {
            s2 = Translator.stash.get(s);
        }
        else if (s.startsWith("RM_")) {
            s2 = Translator.recordedMedia.get(s);
        }
        String s3 = (Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue()) ? "*" : null;
        if (s2 == null) {
            if (b) {
                return null;
            }
            if (!Translator.missing.contains(s)) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                if (Translator.debug) {
                    debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
                Translator.missing.add(s);
            }
            s2 = s;
            s3 = ((Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue()) ? "!" : null);
        }
        if (s2.contains("<br>")) {
            return s2.replaceAll("<br>", "\n");
        }
        return (s3 == null) ? s2 : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s2);
    }
    
    private static int countSubstitutions(final String s) {
        int n = 0;
        if (s.contains("%1")) {
            ++n;
        }
        if (s.contains("%2")) {
            ++n;
        }
        if (s.contains("%3")) {
            ++n;
        }
        if (s.contains("%4")) {
            ++n;
        }
        return n;
    }
    
    private static String subst(String s, final String s2, final Object o) {
        if (o != null) {
            if (o instanceof Double) {
                final double doubleValue = (double)o;
                s = s.replaceAll(s2, (doubleValue == (long)doubleValue) ? Long.toString((long)doubleValue) : o.toString());
            }
            else {
                s = s.replaceAll(s2, Matcher.quoteReplacement(o.toString()));
            }
        }
        return s;
    }
    
    public static String getText(final String s, final Object o) {
        return subst(getText(s), "%1", o);
    }
    
    public static String getText(final String s, final Object o, final Object o2) {
        return subst(subst(getText(s), "%1", o), "%2", o2);
    }
    
    public static String getText(final String s, final Object o, final Object o2, final Object o3) {
        return subst(subst(subst(getText(s), "%1", o), "%2", o2), "%3", o3);
    }
    
    public static String getText(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return subst(subst(subst(subst(getText(s), "%1", o), "%2", o2), "%3", o3), "%4", o4);
    }
    
    public static String getTextOrNull(final String s, final Object o) {
        final String textOrNull = getTextOrNull(s);
        if (textOrNull == null) {
            return null;
        }
        return subst(textOrNull, "%1", o);
    }
    
    public static String getTextOrNull(final String s, final Object o, final Object o2) {
        final String textOrNull = getTextOrNull(s);
        if (textOrNull == null) {
            return null;
        }
        return subst(subst(textOrNull, "%1", o), "%2", o2);
    }
    
    public static String getTextOrNull(final String s, final Object o, final Object o2, final Object o3) {
        final String textOrNull = getTextOrNull(s);
        if (textOrNull == null) {
            return null;
        }
        return subst(subst(subst(textOrNull, "%1", o), "%2", o2), "%3", o3);
    }
    
    public static String getTextOrNull(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        final String textOrNull = getTextOrNull(s);
        if (textOrNull == null) {
            return null;
        }
        return subst(subst(subst(subst(textOrNull, "%1", o), "%2", o2), "%3", o3), "%4", o4);
    }
    
    private static String getDefaultText(final String s) {
        return changeSomeStuff((String)((KahluaTable)LuaManager.env.rawget(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s.split("_")[0], getDefaultLanguage().name()))).rawget((Object)s));
    }
    
    private static String changeSomeStuff(final String s) {
        return s;
    }
    
    public static void setLanguage(Language defaultLanguage) {
        if (defaultLanguage == null) {
            defaultLanguage = getDefaultLanguage();
        }
        Translator.language = defaultLanguage;
    }
    
    public static void setLanguage(final int n) {
        setLanguage(Languages.instance.getByIndex(n));
    }
    
    public static Language getLanguage() {
        if (Translator.language == null) {
            final String optionLanguageName = Core.getInstance().getOptionLanguageName();
            if (!StringUtils.isNullOrWhitespace(optionLanguageName)) {
                Translator.language = Languages.instance.getByName(optionLanguageName);
            }
        }
        if (Translator.language == null) {
            Translator.language = Languages.instance.getByName(System.getProperty("user.language").toUpperCase());
        }
        if (Translator.language == null) {
            Translator.language = getDefaultLanguage();
        }
        return Translator.language;
    }
    
    public static String getCharset() {
        return getLanguage().charset();
    }
    
    public static ArrayList<Language> getAvailableLanguage() {
        if (Translator.availableLanguage == null) {
            Translator.availableLanguage = new ArrayList<Language>();
            for (int i = 0; i < Languages.instance.getNumLanguages(); ++i) {
                Translator.availableLanguage.add(Languages.instance.getByIndex(i));
            }
        }
        return Translator.availableLanguage;
    }
    
    public static String getDisplayItemName(final String s) {
        final String s2 = Translator.items.get(s.replaceAll(" ", "_").replaceAll("-", "_"));
        if (s2 == null) {
            return s;
        }
        return s2;
    }
    
    public static String getItemNameFromFullType(final String s) {
        if (!s.contains(".")) {
            throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
        }
        String displayName = Translator.itemName.get(s);
        if (displayName == null) {
            if (Translator.debug && getLanguage() != getDefaultLanguage() && !Translator.debugItem.contains(s)) {
                Translator.debugItem.add(s);
            }
            final Item item = ScriptManager.instance.getItem(s);
            if (item == null) {
                displayName = s;
            }
            else {
                displayName = item.getDisplayName();
            }
            Translator.itemName.put(s, displayName);
        }
        return displayName;
    }
    
    public static void setDefaultItemEvolvedRecipeName(final String s, final String value) {
        if (getLanguage() != getDefaultLanguage()) {
            return;
        }
        if (!s.contains(".")) {
            throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
        }
        if (Translator.itemEvolvedRecipeName.containsKey(s)) {
            return;
        }
        Translator.itemEvolvedRecipeName.put(s, value);
    }
    
    public static String getItemEvolvedRecipeName(final String s) {
        if (!s.contains(".")) {
            throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
        }
        String displayName = Translator.itemEvolvedRecipeName.get(s);
        if (displayName == null) {
            if (Translator.debug && getLanguage() != getDefaultLanguage() && !Translator.debugItemEvolvedRecipeName.contains(s)) {
                Translator.debugItemEvolvedRecipeName.add(s);
            }
            final Item item = ScriptManager.instance.getItem(s);
            if (item == null) {
                displayName = s;
            }
            else {
                displayName = item.getDisplayName();
            }
            Translator.itemEvolvedRecipeName.put(s, displayName);
        }
        return displayName;
    }
    
    public static String getMoveableDisplayName(final String s) {
        final String s2 = Translator.moveables.get(s.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", ""));
        if (s2 == null) {
            if (Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue()) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            return s;
        }
        else {
            if (Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue()) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
            }
            return s2;
        }
    }
    
    public static String getMultiStageBuild(final String s) {
        final String s2 = Translator.multiStageBuild.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (s2 == null) {
            if (Translator.debug && getLanguage() != getDefaultLanguage() && !Translator.debugMultiStageBuild.contains(s)) {
                Translator.debugMultiStageBuild.add(s);
            }
            return s;
        }
        return s2;
    }
    
    public static String getRecipeName(final String e) {
        final String s = Translator.recipe.get(e);
        if (s == null || s.isEmpty()) {
            if (Translator.debug && getLanguage() != getDefaultLanguage() && !Translator.debugRecipe.contains(e)) {
                Translator.debugRecipe.add(e);
            }
            return e;
        }
        return s;
    }
    
    public static Language getDefaultLanguage() {
        return Languages.instance.getDefaultLanguage();
    }
    
    public static void debugItemEvolvedRecipeNames() {
        if (!Translator.debug || Translator.debugItemEvolvedRecipeName.isEmpty()) {
            return;
        }
        debugwrite(invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getLanguage()));
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        list.addAll((Collection<?>)Translator.debugItemEvolvedRecipeName);
        Collections.sort((List<Comparable>)list);
        for (final String key : list) {
            debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, (String)Translator.itemEvolvedRecipeName.get(key)));
        }
        Translator.debugItemEvolvedRecipeName.clear();
    }
    
    public static void debugItemNames() {
        if (!Translator.debug || Translator.debugItem.isEmpty()) {
            return;
        }
        debugwrite(invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getLanguage()));
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        list.addAll((Collection<?>)Translator.debugItem);
        Collections.sort((List<Comparable>)list);
        for (final String key : list) {
            debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, (String)Translator.itemName.get(key)));
        }
        Translator.debugItem.clear();
    }
    
    public static void debugMultiStageBuildNames() {
        if (!Translator.debug || Translator.debugMultiStageBuild.isEmpty()) {
            return;
        }
        debugwrite(invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getLanguage()));
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        list.addAll((Collection<?>)Translator.debugMultiStageBuild);
        Collections.sort((List<Comparable>)list);
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)iterator.next()));
        }
        Translator.debugMultiStageBuild.clear();
    }
    
    public static void debugRecipeNames() {
        if (!Translator.debug || Translator.debugRecipe.isEmpty()) {
            return;
        }
        debugwrite(invokedynamic(makeConcatWithConstants:(Lzombie/core/Language;)Ljava/lang/String;, getLanguage()));
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        list.addAll((Collection<?>)Translator.debugRecipe);
        Collections.sort((List<Comparable>)list);
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, iterator.next().replace(" ", "_")));
        }
        Translator.debugRecipe.clear();
    }
    
    private static void debugwrite(final String str) {
        if (Translator.debugFile != null) {
            try {
                Translator.debugFile.write(str);
                Translator.debugFile.flush();
            }
            catch (IOException ex) {}
        }
    }
    
    public static ArrayList<String> getAzertyMap() {
        if (Translator.azertyLanguages == null) {
            (Translator.azertyLanguages = new ArrayList<String>()).add("FR");
        }
        return Translator.azertyLanguages;
    }
    
    public static String getRadioText(final String key) {
        final String s = Translator.dynamicRadio.get(key);
        if (s == null) {
            return key;
        }
        return s;
    }
    
    public static String getTextMediaEN(final String e) {
        if (Translator.ui == null) {
            loadFiles();
        }
        String s = null;
        if (e.startsWith("RM_")) {
            s = Translator.recordedMedia_EN.get(e);
        }
        String s2 = (Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue()) ? "*" : null;
        if (s == null) {
            if (!Translator.missing.contains(e)) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
                if (Translator.debug) {
                    debugwrite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
                }
                Translator.missing.add(e);
            }
            s = e;
            s2 = ((Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue()) ? "!" : null);
        }
        if (s.contains("<br>")) {
            return s.replaceAll("<br>", "\n");
        }
        return (s2 == null) ? s : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s);
    }
    
    static {
        Translator.availableLanguage = null;
        Translator.debug = false;
        Translator.debugFile = null;
        Translator.debugErrors = false;
        debugItemEvolvedRecipeName = new HashSet<String>();
        debugItem = new HashSet<String>();
        debugMultiStageBuild = new HashSet<String>();
        debugRecipe = new HashSet<String>();
        moodles = new HashMap<String, String>();
        ui = new HashMap<String, String>();
        survivalGuide = new HashMap<String, String>();
        contextMenu = new HashMap<String, String>();
        farming = new HashMap<String, String>();
        recipe = new HashMap<String, String>();
        igui = new HashMap<String, String>();
        sandbox = new HashMap<String, String>();
        tooltip = new HashMap<String, String>();
        challenge = new HashMap<String, String>();
        missing = new HashSet<String>();
        Translator.azertyLanguages = null;
        news = new HashMap<String, String>();
        stash = new HashMap<String, String>();
        multiStageBuild = new HashMap<String, String>();
        moveables = new HashMap<String, String>();
        makeup = new HashMap<String, String>();
        gameSound = new HashMap<String, String>();
        dynamicRadio = new HashMap<String, String>();
        items = new HashMap<String, String>();
        itemName = new HashMap<String, String>();
        itemEvolvedRecipeName = new HashMap<String, String>();
        recordedMedia = new HashMap<String, String>();
        recordedMedia_EN = new HashMap<String, String>();
        Translator.language = null;
    }
    
    private static final class News
    {
        String version;
        final ArrayList<String> sectionNames;
        final HashMap<String, ArrayList<String>> sectionLists;
        
        News(final String version) {
            this.sectionNames = new ArrayList<String>();
            this.sectionLists = new HashMap<String, ArrayList<String>>();
            this.version = version;
        }
        
        ArrayList<String> getOrCreateSectionList(final String s) {
            if (this.sectionNames.contains(s)) {
                return this.sectionLists.get(s);
            }
            this.sectionNames.add(s);
            final ArrayList<String> value = new ArrayList<String>();
            this.sectionLists.put(s, value);
            return value;
        }
        
        String toRichText() {
            final StringBuilder sb = new StringBuilder("");
            for (final String key : this.sectionNames) {
                final ArrayList<String> list = this.sectionLists.get(key);
                if (list.isEmpty()) {
                    continue;
                }
                sb.append("<LINE> <LEFT> <SIZE:medium> %s <LINE> <LINE> ".formatted(new Object[] { key }));
                final Iterator<String> iterator2 = list.iterator();
                while (iterator2.hasNext()) {
                    sb.append(iterator2.next());
                }
            }
            return sb.toString();
        }
    }
}
