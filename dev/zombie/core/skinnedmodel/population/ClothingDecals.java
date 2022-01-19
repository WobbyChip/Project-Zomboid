// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import java.io.FileInputStream;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClothingDecals
{
    @XmlElement(name = "group")
    public final ArrayList<ClothingDecalGroup> m_Groups;
    @XmlTransient
    public static ClothingDecals instance;
    private final HashMap<String, CachedDecal> m_cachedDecals;
    
    public static void init() {
        if (ClothingDecals.instance != null) {
            throw new IllegalStateException("ClothingDecals Already Initialized.");
        }
        ClothingDecals.instance = Parse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.base.getAbsolutePath(), File.separator, ZomboidFileSystem.processFilePath("media/clothing/clothingDecals.xml", File.separatorChar)));
        if (ClothingDecals.instance == null) {
            return;
        }
        for (final String s : ZomboidFileSystem.instance.getModIDs()) {
            if (ChooseGameInfo.getAvailableModDetails(s) == null) {
                continue;
            }
            final ClothingDecals parse = Parse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getModDir(s), File.separator, ZomboidFileSystem.processFilePath("media/clothing/clothingDecals.xml", File.separatorChar)));
            if (parse == null) {
                continue;
            }
            for (final ClothingDecalGroup clothingDecalGroup : parse.m_Groups) {
                final ClothingDecalGroup findGroup = ClothingDecals.instance.FindGroup(clothingDecalGroup.m_Name);
                if (findGroup == null) {
                    ClothingDecals.instance.m_Groups.add(clothingDecalGroup);
                }
                else {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.println("mod \"%s\" overrides decal group \"%s\"", s, clothingDecalGroup.m_Name);
                    }
                    ClothingDecals.instance.m_Groups.set(ClothingDecals.instance.m_Groups.indexOf(findGroup), clothingDecalGroup);
                }
            }
        }
    }
    
    public static void Reset() {
        if (ClothingDecals.instance == null) {
            return;
        }
        ClothingDecals.instance.m_cachedDecals.clear();
        ClothingDecals.instance.m_Groups.clear();
        ClothingDecals.instance = null;
    }
    
    public ClothingDecals() {
        this.m_Groups = new ArrayList<ClothingDecalGroup>();
        this.m_cachedDecals = new HashMap<String, CachedDecal>();
    }
    
    public static ClothingDecals Parse(final String s) {
        try {
            return parse(s);
        }
        catch (FileNotFoundException ex) {}
        catch (IOException | JAXBException ex2) {
            final Throwable t;
            ExceptionLogger.logException(t);
        }
        return null;
    }
    
    public static ClothingDecals parse(final String name) throws JAXBException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(name);
        try {
            final ClothingDecals clothingDecals = (ClothingDecals)JAXBContext.newInstance(new Class[] { ClothingDecals.class }).createUnmarshaller().unmarshal((InputStream)fileInputStream);
            fileInputStream.close();
            return clothingDecals;
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
    
    public ClothingDecal getDecal(final String name) {
        if (StringUtils.isNullOrWhitespace(name)) {
            return null;
        }
        CachedDecal value = this.m_cachedDecals.get(name);
        if (value == null) {
            value = new CachedDecal();
            this.m_cachedDecals.put(name, value);
        }
        if (value.m_decal != null) {
            return value.m_decal;
        }
        final String string = ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        try {
            value.m_decal = PZXmlUtil.parse(ClothingDecal.class, string);
            value.m_decal.name = name;
        }
        catch (PZXmlParserException ex) {
            System.err.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string));
            ExceptionLogger.logException(ex);
            return null;
        }
        return value.m_decal;
    }
    
    public ClothingDecalGroup FindGroup(final String anotherString) {
        if (StringUtils.isNullOrWhitespace(anotherString)) {
            return null;
        }
        for (int i = 0; i < this.m_Groups.size(); ++i) {
            final ClothingDecalGroup clothingDecalGroup = this.m_Groups.get(i);
            if (clothingDecalGroup.m_Name.equalsIgnoreCase(anotherString)) {
                return clothingDecalGroup;
            }
        }
        return null;
    }
    
    public String getRandomDecal(final String s) {
        final ClothingDecalGroup findGroup = this.FindGroup(s);
        if (findGroup == null) {
            return null;
        }
        return findGroup.getRandomDecal();
    }
    
    private static final class CachedDecal
    {
        ClothingDecal m_decal;
    }
}
