// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

import java.util.regex.Matcher;
import zombie.radio.scripting.RadioLine;
import zombie.core.Rand;
import zombie.radio.scripting.RadioBroadCast;
import zombie.core.Core;
import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Collection;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.Iterator;
import zombie.core.Translator;
import zombie.core.Language;
import java.util.HashMap;
import java.util.regex.Pattern;
import zombie.radio.scripting.RadioScript;
import java.util.Map;
import org.w3c.dom.Node;
import zombie.radio.scripting.RadioChannel;
import java.util.ArrayList;

public final class RadioData
{
    static boolean PRINTDEBUG;
    private boolean isVanilla;
    private String GUID;
    private int version;
    private String xmlFilePath;
    private final ArrayList<RadioChannel> radioChannels;
    private final ArrayList<RadioTranslationData> translationDataList;
    private RadioTranslationData currentTranslation;
    private Node rootNode;
    private final Map<String, RadioScript> advertQue;
    private static final String fieldStart = "\\$\\{t:";
    private static final String fieldEnd = "\\}";
    private static final String regex = "\\$\\{t:([^}]+)\\}";
    private static final Pattern pattern;
    
    public RadioData(final String xmlFilePath) {
        this.isVanilla = false;
        this.radioChannels = new ArrayList<RadioChannel>();
        this.translationDataList = new ArrayList<RadioTranslationData>();
        this.advertQue = new HashMap<String, RadioScript>();
        this.xmlFilePath = xmlFilePath;
    }
    
    public ArrayList<RadioChannel> getRadioChannels() {
        return this.radioChannels;
    }
    
    public boolean isVanilla() {
        return this.isVanilla;
    }
    
    public static ArrayList<String> getTranslatorNames(final Language language) {
        final ArrayList<String> list = new ArrayList<String>();
        if (language != Translator.getDefaultLanguage()) {
            final Iterator<RadioData> iterator = fetchRadioData(false).iterator();
            while (iterator.hasNext()) {
                for (final RadioTranslationData radioTranslationData : iterator.next().translationDataList) {
                    if (radioTranslationData.getLanguageEnum() == language) {
                        for (final String s : radioTranslationData.getTranslators()) {
                            if (!list.contains(s)) {
                                list.add(s);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private static ArrayList<RadioData> fetchRadioData(final boolean b) {
        return fetchRadioData(b, DebugLog.isEnabled(DebugType.Radio));
    }
    
    private static ArrayList<RadioData> fetchRadioData(final boolean b, final boolean b2) {
        final ArrayList<RadioData> list = new ArrayList<RadioData>();
        try {
            final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
            if (b2) {
                System.out.println(":: Searching for radio data files:");
            }
            final ArrayList<Object> c = new ArrayList<Object>();
            searchForFiles(ZomboidFileSystem.instance.getMediaFile("radio"), "xml", (ArrayList<String>)c);
            final ArrayList list2 = new ArrayList<String>(c);
            if (b) {
                for (int i = 0; i < modIDs.size(); ++i) {
                    final String modDir = ZomboidFileSystem.instance.getModDir(modIDs.get(i));
                    if (modDir != null) {
                        searchForFiles(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDir, File.separator, File.separator)), "xml", (ArrayList<String>)c);
                    }
                }
            }
            for (final String anObject : c) {
                final RadioData readFile = ReadFile(anObject);
                if (readFile != null) {
                    if (b2) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                    }
                    final Iterator<String> iterator2 = list2.iterator();
                    while (iterator2.hasNext()) {
                        if (iterator2.next().equals(anObject)) {
                            readFile.isVanilla = true;
                        }
                    }
                    list.add(readFile);
                }
                else {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                }
            }
            if (b2) {
                System.out.println(":: Searching for translation files:");
            }
            c.clear();
            searchForFiles(ZomboidFileSystem.instance.getMediaFile("radio"), "txt", (ArrayList<String>)c);
            if (b) {
                for (int j = 0; j < modIDs.size(); ++j) {
                    final String modDir2 = ZomboidFileSystem.instance.getModDir(modIDs.get(j));
                    if (modDir2 != null) {
                        searchForFiles(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDir2, File.separator, File.separator)), "txt", (ArrayList<String>)c);
                    }
                }
            }
            for (final String s : c) {
                final RadioTranslationData readFile2 = RadioTranslationData.ReadFile(s);
                if (readFile2 != null) {
                    if (b2) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    }
                    for (final RadioData radioData : list) {
                        if (radioData.GUID.equals(readFile2.getGuid())) {
                            if (b2) {
                                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, radioData.GUID));
                            }
                            radioData.translationDataList.add(readFile2);
                        }
                    }
                }
                else {
                    if (!b2) {
                        continue;
                    }
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public static ArrayList<RadioData> fetchAllRadioData() {
        final boolean enabled = DebugLog.isEnabled(DebugType.Radio);
        final ArrayList<RadioData> fetchRadioData = fetchRadioData(true);
        for (int i = fetchRadioData.size() - 1; i >= 0; --i) {
            final RadioData radioData = fetchRadioData.get(i);
            if (radioData.loadRadioScripts()) {
                if (enabled) {
                    DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, radioData.isVanilla ? " (vanilla)" : "", radioData.xmlFilePath));
                    DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, radioData.GUID));
                }
                radioData.currentTranslation = null;
                radioData.translationDataList.clear();
            }
            else {
                DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, radioData.GUID));
                DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, radioData.xmlFilePath));
                fetchRadioData.remove(i);
            }
        }
        return fetchRadioData;
    }
    
    private static void searchForFiles(final File file, final String s, final ArrayList<String> list) {
        if (file.isDirectory()) {
            final String[] list2 = file.list();
            for (int i = 0; i < list2.length; ++i) {
                searchForFiles(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list2[i])), s, list);
            }
        }
        else if (file.getAbsolutePath().toLowerCase().contains(s)) {
            list.add(file.getAbsolutePath());
        }
    }
    
    private static RadioData ReadFile(final String pathname) {
        final RadioData radioData = new RadioData(pathname);
        boolean loadRootInfo = false;
        try {
            if (DebugLog.isEnabled(DebugType.Radio)) {
                DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            }
            final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(pathname));
            parse.getDocumentElement().normalize();
            final NodeList elementsByTagName = parse.getElementsByTagName("RadioData");
            if (DebugLog.isEnabled(DebugType.Radio)) {
                DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, elementsByTagName.getLength()));
            }
            if (elementsByTagName.getLength() > 0) {
                radioData.rootNode = elementsByTagName.item(0);
                loadRootInfo = radioData.loadRootInfo();
                if (DebugLog.isEnabled(DebugType.Radio)) {
                    DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, loadRootInfo));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (loadRootInfo) {
            return radioData;
        }
        return null;
    }
    
    private void print(final String s) {
        if (RadioData.PRINTDEBUG) {
            DebugLog.log(DebugType.Radio, s);
        }
    }
    
    private ArrayList<Node> getChildNodes(final Node node) {
        final ArrayList<Node> list = new ArrayList<Node>();
        if (node.hasChildNodes()) {
            for (Node e = node.getFirstChild(); e != null; e = e.getNextSibling()) {
                if (!(!(e instanceof Element))) {
                    list.add(e);
                }
            }
        }
        return list;
    }
    
    private String toLowerLocaleSafe(final String s) {
        return s.toLowerCase(Locale.ENGLISH);
    }
    
    private boolean nodeNameIs(final Node node, final String anObject) {
        return node.getNodeName().equals(anObject);
    }
    
    private String getAttrib(final Node node, final String s, final boolean b) {
        return this.getAttrib(node, s, b, false);
    }
    
    private String getAttrib(final Node node, final String s) {
        return this.getAttrib(node, s, true, false).trim();
    }
    
    private String getAttrib(final Node node, final String s, final boolean b, final boolean b2) {
        String s2 = node.getAttributes().getNamedItem(s).getTextContent();
        if (b) {
            s2 = s2.trim();
        }
        if (b2) {
            s2 = this.toLowerLocaleSafe(s2);
        }
        return s2;
    }
    
    private boolean loadRootInfo() {
        final boolean enabled = DebugLog.isEnabled(DebugType.Radio);
        if (enabled) {
            DebugLog.Radio.println("Reading RootInfo...");
        }
        for (final Node node : this.getChildNodes(this.rootNode)) {
            if (this.nodeNameIs(node, "RootInfo")) {
                if (enabled) {
                    DebugLog.Radio.println("RootInfo found");
                }
                for (final Node node2 : this.getChildNodes(node)) {
                    final String nodeName = node2.getNodeName();
                    final String textContent = node2.getTextContent();
                    if (nodeName != null && textContent != null) {
                        final String trim = nodeName.trim();
                        if (enabled) {
                            DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                        }
                        if (trim.equals("Version")) {
                            if (enabled) {
                                DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.version));
                            }
                            this.version = Integer.parseInt(textContent);
                        }
                        else {
                            if (!trim.equals("FileGUID")) {
                                continue;
                            }
                            if (enabled) {
                                DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, textContent));
                            }
                            this.GUID = textContent;
                        }
                    }
                }
            }
        }
        return this.GUID != null && this.version >= 0;
    }
    
    private boolean loadRadioScripts() {
        boolean b = false;
        this.currentTranslation = null;
        this.advertQue.clear();
        if (Core.getInstance().getContentTranslationsEnabled() && Translator.getLanguage() != Translator.getDefaultLanguage()) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getLanguage().toString()));
            for (final RadioTranslationData currentTranslation : this.translationDataList) {
                if (currentTranslation.getLanguageEnum() == Translator.getLanguage()) {
                    System.out.println("Translation found!");
                    if (currentTranslation.loadTranslations()) {
                        this.currentTranslation = currentTranslation;
                        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.currentTranslation.getTranslationCount()));
                    }
                    else {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.GUID));
                    }
                }
            }
        }
        else if (!Core.getInstance().getContentTranslationsEnabled()) {
            System.out.println("NOTE: Community Content Translations are disabled.");
        }
        for (final Node node : this.getChildNodes(this.rootNode)) {
            if (this.nodeNameIs(node, "Adverts")) {
                this.loadAdverts(node);
            }
        }
        for (final Node node2 : this.getChildNodes(this.rootNode)) {
            if (this.nodeNameIs(node2, "Channels")) {
                this.loadChannels(node2);
                b = true;
            }
        }
        return b;
    }
    
    private void loadAdverts(final Node node) {
        this.print(">>> Loading adverts...");
        for (final RadioScript radioScript : this.loadScripts(node, new ArrayList<RadioScript>(), true)) {
            if (!this.advertQue.containsKey(radioScript.GetName())) {
                this.advertQue.put(radioScript.GetGUID(), radioScript);
            }
        }
    }
    
    private void loadChannels(final Node node) {
        this.print(">>> Loading channels...");
        ArrayList<RadioScript> loadScripts = new ArrayList<RadioScript>();
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "ChannelEntry")) {
                final String attrib = this.getAttrib(node2, "ID");
                final String attrib2 = this.getAttrib(node2, "name");
                final String attrib3 = this.getAttrib(node2, "cat");
                final String attrib4 = this.getAttrib(node2, "freq");
                final String attrib5 = this.getAttrib(node2, "startscript");
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib2, attrib4, attrib3, attrib5, attrib));
                final RadioChannel e = new RadioChannel(attrib2, Integer.parseInt(attrib4), ChannelCategory.valueOf(attrib3), attrib);
                loadScripts.clear();
                loadScripts = this.loadScripts(node2, loadScripts, false);
                final Iterator<RadioScript> iterator2 = loadScripts.iterator();
                while (iterator2.hasNext()) {
                    e.AddRadioScript(iterator2.next());
                }
                e.setActiveScript(attrib5, 0);
                this.radioChannels.add(e);
                e.setRadioData(this);
            }
        }
    }
    
    private ArrayList<RadioScript> loadScripts(final Node node, final ArrayList<RadioScript> list, final boolean b) {
        this.print(" --> Loading scripts...");
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "ScriptEntry")) {
                final String attrib = this.getAttrib(node2, "ID");
                final String attrib2 = this.getAttrib(node2, "name");
                final String attrib3 = this.getAttrib(node2, "loopmin");
                final String attrib4 = this.getAttrib(node2, "loopmax");
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, attrib2));
                final RadioScript e = new RadioScript(attrib2, Integer.parseInt(attrib3), Integer.parseInt(attrib4), attrib);
                for (final Node node3 : this.getChildNodes(node2)) {
                    if (this.nodeNameIs(node3, "BroadcastEntry")) {
                        this.loadBroadcast(node3, e);
                    }
                    else {
                        if (b || !this.nodeNameIs(node3, "ExitOptions")) {
                            continue;
                        }
                        this.loadExitOptions(node3, e);
                    }
                }
                list.add(e);
            }
        }
        return list;
    }
    
    private RadioBroadCast loadBroadcast(final Node node, final RadioScript radioScript) {
        final String attrib = this.getAttrib(node, "ID");
        final String attrib2 = this.getAttrib(node, "timestamp");
        final String attrib3 = this.getAttrib(node, "endstamp");
        this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib2, attrib3));
        final int int1 = Integer.parseInt(attrib2);
        final int int2 = Integer.parseInt(attrib3);
        final boolean equals = this.toLowerLocaleSafe(this.getAttrib(node, "isSegment")).equals("true");
        final String attrib4 = this.getAttrib(node, "advertCat");
        final RadioBroadCast radioBroadCast = new RadioBroadCast(attrib, int1, int2);
        if (!equals && !this.toLowerLocaleSafe(attrib4).equals("none") && this.advertQue.containsKey(attrib4) && Rand.Next(101) < 75) {
            final RadioScript radioScript2 = this.advertQue.get(attrib4);
            if (radioScript2.getBroadcastList().size() > 0) {
                if (Rand.Next(101) < 50) {
                    radioBroadCast.setPreSegment(radioScript2.getBroadcastList().get(Rand.Next(radioScript2.getBroadcastList().size())));
                }
                else {
                    radioBroadCast.setPostSegment(radioScript2.getBroadcastList().get(Rand.Next(radioScript2.getBroadcastList().size())));
                }
            }
        }
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "LineEntry")) {
                final String attrib5 = this.getAttrib(node2, "ID");
                final String attrib6 = this.getAttrib(node2, "r");
                final String attrib7 = this.getAttrib(node2, "g");
                final String attrib8 = this.getAttrib(node2, "b");
                String attrib9 = null;
                if (node2.getAttributes().getNamedItem("codes") != null) {
                    attrib9 = this.getAttrib(node2, "codes");
                }
                final String textContent = node2.getTextContent();
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib6, attrib7, attrib8));
                final String checkForTranslation = this.checkForTranslation(attrib5, textContent);
                final RadioLine radioLine = new RadioLine(checkForTranslation, Float.parseFloat(attrib6) / 255.0f, Float.parseFloat(attrib7) / 255.0f, Float.parseFloat(attrib8) / 255.0f, attrib9);
                radioBroadCast.AddRadioLine(radioLine);
                final String trim = checkForTranslation.trim();
                if (!trim.toLowerCase().startsWith("${t:")) {
                    continue;
                }
                radioLine.setText(this.checkForCustomAirTimer(trim, radioLine));
            }
        }
        if (radioScript != null) {
            radioScript.AddBroadcast(radioBroadCast, equals);
        }
        return radioBroadCast;
    }
    
    private String checkForTranslation(final String s, final String s2) {
        if (this.currentTranslation != null) {
            final String translation = this.currentTranslation.getTranslation(s);
            if (translation != null) {
                return translation;
            }
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        return s2;
    }
    
    private void loadExitOptions(final Node node, final RadioScript radioScript) {
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "ExitOption")) {
                radioScript.AddExitOption(this.getAttrib(node2, "script"), Integer.parseInt(this.getAttrib(node2, "chance")), Integer.parseInt(this.getAttrib(node2, "delay")));
            }
        }
    }
    
    private String checkForCustomAirTimer(final String input, final RadioLine radioLine) {
        final Matcher matcher = RadioData.pattern.matcher(input);
        String replaceFirst = input;
        float float1 = -1.0f;
        if (matcher.find()) {
            final String trim = matcher.group(1).toLowerCase().trim();
            try {
                float1 = Float.parseFloat(trim);
                radioLine.setAirTime(float1);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            replaceFirst = replaceFirst.replaceFirst("\\$\\{t:([^}]+)\\}", "");
        }
        if (float1 >= 0.0f) {
            return invokedynamic(makeConcatWithConstants:(FLjava/lang/String;)Ljava/lang/String;, float1, replaceFirst.trim());
        }
        return replaceFirst.trim();
    }
    
    static {
        RadioData.PRINTDEBUG = false;
        pattern = Pattern.compile("\\$\\{t:([^}]+)\\}");
    }
}
