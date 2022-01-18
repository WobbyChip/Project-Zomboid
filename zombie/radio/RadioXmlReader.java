// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

import zombie.radio.scripting.RadioLine;
import zombie.core.Rand;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;
import zombie.radio.globals.RadioGlobalsManager;
import zombie.radio.globals.RadioGlobalBool;
import zombie.radio.globals.RadioGlobalFloat;
import zombie.radio.globals.RadioGlobalInt;
import zombie.radio.globals.RadioGlobalString;
import zombie.radio.globals.RadioGlobalType;
import org.w3c.dom.Element;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import zombie.radio.scripting.RadioBroadCast;
import java.util.Map;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.globals.RadioGlobal;
import java.util.ArrayList;

public final class RadioXmlReader
{
    private boolean printDebug;
    private ArrayList<RadioGlobal> globalQueue;
    private ArrayList<RadioChannel> channelQueue;
    private Map<String, ArrayList<RadioBroadCast>> advertQue;
    private final String charsNormal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String charsEncrypt = "UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn";
    private String radioVersion;
    private float version;
    private float formatVersion;
    private final Map<String, String> radioFileSettings;
    
    public RadioXmlReader() {
        this(false);
    }
    
    public RadioXmlReader(final boolean printDebug) {
        this.printDebug = false;
        this.radioVersion = "1.0";
        this.version = 1.0f;
        this.formatVersion = 1.0f;
        this.radioFileSettings = new HashMap<String, String>();
        this.printDebug = printDebug;
    }
    
    public static RadioData ReadFileHeader(final String s) {
        final RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);
        return null;
    }
    
    private void readfileheader(final String pathname) throws ParserConfigurationException, IOException, SAXException {
        final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(pathname));
        parse.getDocumentElement().normalize();
        final NodeList elementsByTagName = parse.getElementsByTagName("RadioData");
        if (elementsByTagName.getLength() > 0) {
            final Node item = elementsByTagName.item(0);
            Node node = null;
            for (final Node node2 : this.getChildNodes(item)) {
                if (this.nodeNameIs(node2, "RootInfo")) {
                    node = node2;
                    break;
                }
            }
            this.loadRootInfo(node);
        }
    }
    
    public static boolean LoadFile(final String s) {
        final RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);
        try {
            radioXmlReader.start(s);
        }
        catch (Exception ex) {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex.getMessage()));
            ex.printStackTrace();
            return false;
        }
        finally {
            DebugLog.log(DebugType.Radio, "RadioSystem online.");
            return true;
        }
    }
    
    public static ArrayList<String> LoadTranslatorNames(final String s) {
        ArrayList<String> translatorNames = new ArrayList<String>();
        final RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);
        try {
            translatorNames = radioXmlReader.readTranslatorNames(s);
            return translatorNames;
        }
        catch (Exception ex) {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex.getMessage()));
            ex.printStackTrace();
            return translatorNames;
        }
        finally {
            DebugLog.log(DebugType.Radio, "Returning translator names.");
            return translatorNames;
        }
    }
    
    private void print(final String s) {
        if (this.printDebug) {
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
            s2 = s2.toLowerCase();
        }
        return s2;
    }
    
    private RadioGlobal getGlobalFromQueue(final String anObject) {
        for (final RadioGlobal radioGlobal : this.globalQueue) {
            if (radioGlobal != null && radioGlobal.getName().equals(anObject)) {
                return radioGlobal;
            }
        }
        return null;
    }
    
    private RadioGlobal createGlobal(final String s, final String s2) {
        return this.createGlobal("", s, s2);
    }
    
    private RadioGlobal createGlobal(final String s, final String s2, final String s3) {
        if (s == null || s2 == null || s3 == null) {
            return null;
        }
        switch (RadioGlobalType.valueOf(s2.trim())) {
            case String: {
                return new RadioGlobalString(s, s3);
            }
            case Integer: {
                return new RadioGlobalInt(s, Integer.parseInt(s3.trim()));
            }
            case Float: {
                return new RadioGlobalFloat(s, Float.parseFloat(s3.trim()));
            }
            case Boolean: {
                return new RadioGlobalBool(s, Boolean.parseBoolean(s3.trim().toLowerCase()));
            }
            default: {
                return null;
            }
        }
    }
    
    private ArrayList<String> readTranslatorNames(final String pathname) throws ParserConfigurationException, IOException, SAXException {
        final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(pathname));
        parse.getDocumentElement().normalize();
        final ArrayList<String> list = new ArrayList<String>();
        final NodeList elementsByTagName = parse.getElementsByTagName("TranslationData");
        if (elementsByTagName.getLength() > 0) {
            for (final Node node : this.getChildNodes(elementsByTagName.item(0))) {
                if (this.nodeNameIs(node, "RootInfo")) {
                    for (final Node node2 : this.getChildNodes(node)) {
                        if (this.nodeNameIs(node2, "Translators")) {
                            final Iterator<Node> iterator3 = this.getChildNodes(node2).iterator();
                            while (iterator3.hasNext()) {
                                final String attrib = this.getAttrib(iterator3.next(), "name", true, false);
                                if (attrib != null) {
                                    list.add(attrib);
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return list;
    }
    
    private void start(final String pathname) throws ParserConfigurationException, IOException, SAXException {
        final File f = new File(pathname);
        this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, f.getAbsolutePath()));
        final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        parse.getDocumentElement().normalize();
        this.globalQueue = new ArrayList<RadioGlobal>();
        this.channelQueue = new ArrayList<RadioChannel>();
        this.advertQue = new HashMap<String, ArrayList<RadioBroadCast>>();
        final NodeList elementsByTagName = parse.getElementsByTagName("RadioData");
        if (elementsByTagName.getLength() > 0) {
            final Node item = elementsByTagName.item(0);
            Node node = null;
            for (final Node node2 : this.getChildNodes(item)) {
                if (this.nodeNameIs(node2, "RootInfo")) {
                    node = node2;
                    break;
                }
            }
            this.loadRootInfo(node);
            for (final Node node3 : this.getChildNodes(item)) {
                if (this.nodeNameIs(node3, "Globals")) {
                    this.loadGlobals(node3);
                }
                else if (this.nodeNameIs(node3, "Adverts")) {
                    this.loadAdverts(node3);
                }
                else {
                    if (!this.nodeNameIs(node3, "Channels")) {
                        continue;
                    }
                    this.loadChannels(node3);
                }
            }
        }
        final RadioGlobalsManager instance = RadioGlobalsManager.getInstance();
        for (final RadioGlobal radioGlobal : this.globalQueue) {
            instance.addGlobal(radioGlobal.getName(), radioGlobal);
        }
        final RadioScriptManager instance2 = RadioScriptManager.getInstance();
        final Iterator<RadioChannel> iterator4 = this.channelQueue.iterator();
        while (iterator4.hasNext()) {
            instance2.AddChannel(iterator4.next(), false);
        }
    }
    
    private void loadRootInfo(final Node node) {
        this.print(">>> Loading root info...");
        if (node == null) {
            this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.radioVersion));
            this.radioFileSettings.put("Version", this.radioVersion);
            return;
        }
        this.print(" -> Reading RootInfo");
        for (final Node node2 : this.getChildNodes(node)) {
            final String nodeName = node2.getNodeName();
            final String textContent = node2.getTextContent();
            if (nodeName != null && textContent != null) {
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, nodeName, textContent));
                this.radioFileSettings.put(nodeName, textContent);
                if (!nodeName.equals("Version")) {
                    continue;
                }
                this.radioVersion = textContent;
                this.version = Float.parseFloat(this.radioVersion);
            }
        }
    }
    
    private void loadGlobals(final Node node) {
        this.print(">>> Loading globals...");
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "GlobalEntry")) {
                final String attrib = this.getAttrib(node2, "name");
                final String attrib2 = this.getAttrib(node2, "type");
                final String textContent = node2.getTextContent();
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib, attrib2, textContent));
                final RadioGlobal global = this.createGlobal(attrib, attrib2, textContent);
                if (global != null) {
                    this.globalQueue.add(global);
                }
                else {
                    this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib, attrib2, textContent));
                }
            }
        }
    }
    
    private void loadAdverts(final Node node) {
        this.print(">>> Loading adverts...");
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "AdvertCategory")) {
                final String attrib = this.getAttrib(node2, "name");
                if (!this.advertQue.containsKey(attrib)) {
                    this.advertQue.put(attrib, new ArrayList<RadioBroadCast>());
                }
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, attrib));
                final Iterator<Node> iterator2 = this.getChildNodes(node2).iterator();
                while (iterator2.hasNext()) {
                    this.advertQue.get(attrib).add(this.loadBroadcast(iterator2.next(), null));
                }
            }
        }
    }
    
    private void loadChannels(final Node node) {
        this.print(">>> Loading channels...");
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "ChannelEntry")) {
                final String attrib = this.getAttrib(node2, "name");
                final String attrib2 = this.getAttrib(node2, "cat");
                final String attrib3 = this.getAttrib(node2, "freq");
                final String attrib4 = this.getAttrib(node2, "startscript");
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib, attrib3, attrib2, attrib4));
                final RadioChannel e = new RadioChannel(attrib, Integer.parseInt(attrib3), ChannelCategory.valueOf(attrib2));
                this.loadScripts(node2, e);
                e.setActiveScript(attrib4, 0);
                this.channelQueue.add(e);
            }
        }
    }
    
    private void loadScripts(final Node node, final RadioChannel radioChannel) {
        this.print(" --> Loading scripts...");
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "ScriptEntry")) {
                final String attrib = this.getAttrib(node2, "name");
                final String attrib2 = this.getAttrib(node2, "loopmin");
                final String attrib3 = this.getAttrib(node2, "loopmin");
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, attrib));
                final RadioScript radioScript = new RadioScript(attrib, Integer.parseInt(attrib2), Integer.parseInt(attrib3));
                for (final Node node3 : this.getChildNodes(node2)) {
                    if (this.nodeNameIs(node3, "BroadcastEntry")) {
                        this.loadBroadcast(node3, radioScript);
                    }
                    else {
                        if (!this.nodeNameIs(node3, "ExitOptions")) {
                            continue;
                        }
                        this.loadExitOptions(node3, radioScript);
                    }
                }
                radioChannel.AddRadioScript(radioScript);
            }
        }
    }
    
    private RadioBroadCast loadBroadcast(final Node node, final RadioScript radioScript) {
        final String attrib = this.getAttrib(node, "ID");
        final String attrib2 = this.getAttrib(node, "timestamp");
        final String attrib3 = this.getAttrib(node, "endstamp");
        this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib2, attrib3));
        final int int1 = Integer.parseInt(attrib2);
        final int int2 = Integer.parseInt(attrib3);
        final String attrib4 = this.getAttrib(node, "preCat");
        final int int3 = Integer.parseInt(this.getAttrib(node, "preChance"));
        final String attrib5 = this.getAttrib(node, "postCat");
        final int int4 = Integer.parseInt(this.getAttrib(node, "postChance"));
        final RadioBroadCast radioBroadCast = new RadioBroadCast(attrib, int1, int2);
        if (!attrib4.equals("none") && this.advertQue.containsKey(attrib4)) {
            final int next = Rand.Next(101);
            final int size = this.advertQue.get(attrib4).size();
            if (size > 0 && next <= int3) {
                radioBroadCast.setPreSegment(this.advertQue.get(attrib4).get(Rand.Next(size)));
            }
        }
        if (!attrib5.equals("none") && this.advertQue.containsKey(attrib5)) {
            final int next2 = Rand.Next(101);
            final int size2 = this.advertQue.get(attrib5).size();
            if (size2 > 0 && next2 <= int4) {
                radioBroadCast.setPostSegment(this.advertQue.get(attrib5).get(Rand.Next(size2)));
            }
        }
        for (final Node node2 : this.getChildNodes(node)) {
            if (this.nodeNameIs(node2, "LineEntry")) {
                final String attrib6 = this.getAttrib(node2, "r");
                final String attrib7 = this.getAttrib(node2, "g");
                final String attrib8 = this.getAttrib(node2, "b");
                String s = null;
                final String textContent = node2.getTextContent();
                this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, attrib6, attrib7, attrib8));
                final Iterator<Node> iterator2 = this.getChildNodes(node2).iterator();
                while (iterator2.hasNext()) {
                    if (this.nodeNameIs(iterator2.next(), "LineEffects")) {
                        s = "";
                        for (final Node node3 : this.getChildNodes(node2)) {
                            if (this.nodeNameIs(node3, "Effect")) {
                                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.getAttrib(node3, "tag"), this.getAttrib(node3, "value"));
                            }
                        }
                        break;
                    }
                }
                radioBroadCast.AddRadioLine(new RadioLine(this.simpleDecrypt(textContent), Float.parseFloat(attrib6) / 255.0f, Float.parseFloat(attrib7) / 255.0f, Float.parseFloat(attrib8) / 255.0f, s));
            }
        }
        if (radioScript != null) {
            radioScript.AddBroadcast(radioBroadCast);
        }
        return radioBroadCast;
    }
    
    private String simpleDecrypt(final String s) {
        String s2 = "";
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if ("UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn".indexOf(char1) != -1) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, s2, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt("UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn".indexOf(char1)));
            }
            else {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, s2, char1);
            }
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
}
