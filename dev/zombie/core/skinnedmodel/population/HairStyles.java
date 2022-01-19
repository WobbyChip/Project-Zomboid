// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.characters.HairOutfitDefinitions;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import java.io.File;
import zombie.ZomboidFileSystem;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HairStyles
{
    @XmlElement(name = "male")
    public final ArrayList<HairStyle> m_MaleStyles;
    @XmlElement(name = "female")
    public final ArrayList<HairStyle> m_FemaleStyles;
    @XmlTransient
    public static HairStyles instance;
    
    public static void init() {
        HairStyles.instance = Parse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.base.getAbsolutePath(), File.separator, ZomboidFileSystem.processFilePath("media/hairStyles/hairStyles.xml", File.separatorChar)));
        if (HairStyles.instance == null) {
            return;
        }
        for (final String s : ZomboidFileSystem.instance.getModIDs()) {
            if (ChooseGameInfo.getAvailableModDetails(s) == null) {
                continue;
            }
            final HairStyles parse = Parse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getModDir(s), File.separator, ZomboidFileSystem.processFilePath("media/hairStyles/hairStyles.xml", File.separatorChar)));
            if (parse == null) {
                continue;
            }
            for (final HairStyle hairStyle : parse.m_FemaleStyles) {
                final HairStyle findFemaleStyle = HairStyles.instance.FindFemaleStyle(hairStyle.name);
                if (findFemaleStyle == null) {
                    HairStyles.instance.m_FemaleStyles.add(hairStyle);
                }
                else {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.println("mod \"%s\" overrides hair \"%s\"", s, hairStyle.name);
                    }
                    HairStyles.instance.m_FemaleStyles.set(HairStyles.instance.m_FemaleStyles.indexOf(findFemaleStyle), hairStyle);
                }
            }
            for (final HairStyle hairStyle2 : parse.m_MaleStyles) {
                final HairStyle findMaleStyle = HairStyles.instance.FindMaleStyle(hairStyle2.name);
                if (findMaleStyle == null) {
                    HairStyles.instance.m_MaleStyles.add(hairStyle2);
                }
                else {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.println("mod \"%s\" overrides hair \"%s\"", s, hairStyle2.name);
                    }
                    HairStyles.instance.m_MaleStyles.set(HairStyles.instance.m_MaleStyles.indexOf(findMaleStyle), hairStyle2);
                }
            }
        }
    }
    
    public static void Reset() {
        if (HairStyles.instance == null) {
            return;
        }
        HairStyles.instance.m_FemaleStyles.clear();
        HairStyles.instance.m_MaleStyles.clear();
        HairStyles.instance = null;
    }
    
    public HairStyles() {
        this.m_MaleStyles = new ArrayList<HairStyle>();
        this.m_FemaleStyles = new ArrayList<HairStyle>();
    }
    
    public static HairStyles Parse(final String s) {
        try {
            return parse(s);
        }
        catch (FileNotFoundException ex) {}
        catch (JAXBException | IOException ex2) {
            final Throwable t;
            ExceptionLogger.logException(t);
        }
        return null;
    }
    
    public static HairStyles parse(final String name) throws JAXBException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(name);
        try {
            final HairStyles hairStyles = (HairStyles)JAXBContext.newInstance(new Class[] { HairStyles.class }).createUnmarshaller().unmarshal((InputStream)fileInputStream);
            fileInputStream.close();
            return hairStyles;
        }
        catch (Throwable t) {
            try {
                fileInputStream.close();
            }
            catch (Throwable exception) {
                t.addSuppressed(exception);
            }
            throw t;
        }
    }
    
    public HairStyle FindMaleStyle(final String s) {
        return this.FindStyle(this.m_MaleStyles, s);
    }
    
    public HairStyle FindFemaleStyle(final String s) {
        return this.FindStyle(this.m_FemaleStyles, s);
    }
    
    private HairStyle FindStyle(final ArrayList<HairStyle> list, final String s) {
        for (int i = 0; i < list.size(); ++i) {
            final HairStyle hairStyle = list.get(i);
            if (hairStyle.name.equalsIgnoreCase(s)) {
                return hairStyle;
            }
            if ("".equals(s) && hairStyle.name.equalsIgnoreCase("bald")) {
                return hairStyle;
            }
        }
        return null;
    }
    
    public String getRandomMaleStyle(final String s) {
        return HairOutfitDefinitions.instance.getRandomHaircut(s, this.m_MaleStyles);
    }
    
    public String getRandomFemaleStyle(final String s) {
        return HairOutfitDefinitions.instance.getRandomHaircut(s, this.m_FemaleStyles);
    }
    
    public HairStyle getAlternateForHat(final HairStyle hairStyle, final String s) {
        if ("nohair".equalsIgnoreCase(s) || "nohairnobeard".equalsIgnoreCase(s)) {
            return null;
        }
        if (this.m_FemaleStyles.contains(hairStyle)) {
            return this.FindFemaleStyle(hairStyle.getAlternate(s));
        }
        if (this.m_MaleStyles.contains(hairStyle)) {
            return this.FindMaleStyle(hairStyle.getAlternate(s));
        }
        return hairStyle;
    }
    
    public ArrayList<HairStyle> getAllMaleStyles() {
        return this.m_MaleStyles;
    }
    
    public ArrayList<HairStyle> getAllFemaleStyles() {
        return this.m_FemaleStyles;
    }
}
