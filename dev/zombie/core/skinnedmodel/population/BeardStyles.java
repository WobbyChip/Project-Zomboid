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
public class BeardStyles
{
    @XmlElement(name = "style")
    public final ArrayList<BeardStyle> m_Styles;
    @XmlTransient
    public static BeardStyles instance;
    
    public static void init() {
        BeardStyles.instance = Parse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.base.getAbsolutePath(), File.separator, ZomboidFileSystem.processFilePath("media/hairStyles/beardStyles.xml", File.separatorChar)));
        if (BeardStyles.instance == null) {
            return;
        }
        BeardStyles.instance.m_Styles.add(0, new BeardStyle());
        for (final String s : ZomboidFileSystem.instance.getModIDs()) {
            if (ChooseGameInfo.getAvailableModDetails(s) == null) {
                continue;
            }
            final BeardStyles parse = Parse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getModDir(s), File.separator, ZomboidFileSystem.processFilePath("media/hairStyles/beardStyles.xml", File.separatorChar)));
            if (parse == null) {
                continue;
            }
            for (final BeardStyle beardStyle : parse.m_Styles) {
                final BeardStyle findStyle = BeardStyles.instance.FindStyle(beardStyle.name);
                if (findStyle == null) {
                    BeardStyles.instance.m_Styles.add(beardStyle);
                }
                else {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.println("mod \"%s\" overrides beard \"%s\"", s, beardStyle.name);
                    }
                    BeardStyles.instance.m_Styles.set(BeardStyles.instance.m_Styles.indexOf(findStyle), beardStyle);
                }
            }
        }
    }
    
    public static void Reset() {
        if (BeardStyles.instance == null) {
            return;
        }
        BeardStyles.instance.m_Styles.clear();
        BeardStyles.instance = null;
    }
    
    public BeardStyles() {
        this.m_Styles = new ArrayList<BeardStyle>();
    }
    
    public static BeardStyles Parse(final String s) {
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
    
    public static BeardStyles parse(final String name) throws JAXBException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(name);
        try {
            final BeardStyles beardStyles = (BeardStyles)JAXBContext.newInstance(new Class[] { BeardStyles.class }).createUnmarshaller().unmarshal((InputStream)fileInputStream);
            fileInputStream.close();
            return beardStyles;
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
    
    public BeardStyle FindStyle(final String anotherString) {
        for (int i = 0; i < this.m_Styles.size(); ++i) {
            final BeardStyle beardStyle = this.m_Styles.get(i);
            if (beardStyle.name.equalsIgnoreCase(anotherString)) {
                return beardStyle;
            }
        }
        return null;
    }
    
    public String getRandomStyle(final String s) {
        return HairOutfitDefinitions.instance.getRandomBeard(s, this.m_Styles);
    }
    
    public BeardStyles getInstance() {
        return BeardStyles.instance;
    }
    
    public ArrayList<BeardStyle> getAllStyles() {
        return this.m_Styles;
    }
}
