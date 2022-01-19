// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.asset.Asset;
import zombie.util.StringUtils;
import zombie.core.logger.ExceptionLogger;
import zombie.asset.AssetPath;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import zombie.util.PZXmlUtil;
import java.io.File;
import zombie.util.PZXmlParserException;
import zombie.core.Rand;
import zombie.util.list.PZArrayUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import zombie.PredicatedFileWatcher;
import zombie.DebugFileWatcher;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.ZomboidFileSystem;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Hashtable;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OutfitManager
{
    public ArrayList<Outfit> m_MaleOutfits;
    public ArrayList<Outfit> m_FemaleOutfits;
    @XmlTransient
    public static OutfitManager instance;
    @XmlTransient
    private final Hashtable<String, ClothingItemEntry> m_cachedClothingItems;
    @XmlTransient
    private final ArrayList<IClothingItemListener> m_clothingItemListeners;
    @XmlTransient
    private final TreeMap<String, Outfit> m_femaleOutfitMap;
    @XmlTransient
    private final TreeMap<String, Outfit> m_maleOutfitMap;
    
    public OutfitManager() {
        this.m_MaleOutfits = new ArrayList<Outfit>();
        this.m_FemaleOutfits = new ArrayList<Outfit>();
        this.m_cachedClothingItems = new Hashtable<String, ClothingItemEntry>();
        this.m_clothingItemListeners = new ArrayList<IClothingItemListener>();
        this.m_femaleOutfitMap = new TreeMap<String, Outfit>(String.CASE_INSENSITIVE_ORDER);
        this.m_maleOutfitMap = new TreeMap<String, Outfit>(String.CASE_INSENSITIVE_ORDER);
    }
    
    public static void init() {
        if (OutfitManager.instance != null) {
            throw new IllegalStateException("OutfitManager Already Initialized.");
        }
        OutfitManager.instance = tryParse("game", "media/clothing/clothing.xml");
        if (OutfitManager.instance == null) {
            return;
        }
        OutfitManager.instance.loaded();
    }
    
    public static void Reset() {
        if (OutfitManager.instance == null) {
            return;
        }
        OutfitManager.instance.unload();
        OutfitManager.instance = null;
    }
    
    private void loaded() {
        for (final String s : ZomboidFileSystem.instance.getModIDs()) {
            if (ChooseGameInfo.getAvailableModDetails(s) == null) {
                continue;
            }
            final OutfitManager tryParse = tryParse(s, "media/clothing/clothing.xml");
            if (tryParse == null) {
                continue;
            }
            for (final Outfit value : tryParse.m_MaleOutfits) {
                final Outfit findMaleOutfit = this.FindMaleOutfit(value.m_Name);
                if (findMaleOutfit == null) {
                    this.m_MaleOutfits.add(value);
                }
                else {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.println("mod \"%s\" overrides male outfit \"%s\"", s, value.m_Name);
                    }
                    this.m_MaleOutfits.set(this.m_MaleOutfits.indexOf(findMaleOutfit), value);
                }
                this.m_maleOutfitMap.put(value.m_Name, value);
            }
            for (final Outfit value2 : tryParse.m_FemaleOutfits) {
                final Outfit findFemaleOutfit = this.FindFemaleOutfit(value2.m_Name);
                if (findFemaleOutfit == null) {
                    this.m_FemaleOutfits.add(value2);
                }
                else {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.println("mod \"%s\" overrides female outfit \"%s\"", s, value2.m_Name);
                    }
                    this.m_FemaleOutfits.set(this.m_FemaleOutfits.indexOf(findFemaleOutfit), value2);
                }
                this.m_femaleOutfitMap.put(value2.m_Name, value2);
            }
        }
        DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/clothing/clothing.xml", p0 -> onClothingXmlFileChanged()));
        this.loadAllClothingItems();
        for (final Outfit outfit : this.m_MaleOutfits) {
            outfit.m_Immutable = true;
            final Iterator<ClothingItemReference> iterator5 = outfit.m_items.iterator();
            while (iterator5.hasNext()) {
                iterator5.next().m_Immutable = true;
            }
        }
        for (final Outfit outfit2 : this.m_FemaleOutfits) {
            outfit2.m_Immutable = true;
            final Iterator<ClothingItemReference> iterator7 = outfit2.m_items.iterator();
            while (iterator7.hasNext()) {
                iterator7.next().m_Immutable = true;
            }
        }
        Collections.shuffle(this.m_MaleOutfits);
        Collections.shuffle(this.m_FemaleOutfits);
    }
    
    private static void onClothingXmlFileChanged() {
        DebugLog.Clothing.println("OutfitManager.onClothingXmlFileChanged> Detected change in media/clothing/clothing.xml");
        Reload();
    }
    
    public static void Reload() {
        DebugLog.Clothing.println("Reloading OutfitManager");
        final OutfitManager instance = OutfitManager.instance;
        OutfitManager.instance = tryParse("game", "media/clothing/clothing.xml");
        if (OutfitManager.instance != null) {
            OutfitManager.instance.loaded();
        }
        if (instance != null && OutfitManager.instance != null) {
            OutfitManager.instance.onReloaded(instance);
        }
    }
    
    private void onReloaded(final OutfitManager outfitManager) {
        PZArrayUtil.copy(this.m_clothingItemListeners, outfitManager.m_clothingItemListeners);
        outfitManager.unload();
        this.loadAllClothingItems();
    }
    
    private void unload() {
        final Iterator<ClothingItemEntry> iterator = this.m_cachedClothingItems.values().iterator();
        while (iterator.hasNext()) {
            DebugFileWatcher.instance.remove(iterator.next().m_fileWatcher);
        }
        this.m_cachedClothingItems.clear();
        this.m_clothingItemListeners.clear();
    }
    
    public void addClothingItemListener(final IClothingItemListener clothingItemListener) {
        if (clothingItemListener == null) {
            return;
        }
        if (this.m_clothingItemListeners.contains(clothingItemListener)) {
            return;
        }
        this.m_clothingItemListeners.add(clothingItemListener);
    }
    
    public void removeClothingItemListener(final IClothingItemListener o) {
        this.m_clothingItemListeners.remove(o);
    }
    
    private void invokeClothingItemChangedEvent(final String s) {
        final Iterator<IClothingItemListener> iterator = this.m_clothingItemListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().clothingItemChanged(s);
        }
    }
    
    public Outfit GetRandomOutfit(final boolean b) {
        Outfit outfit;
        if (b) {
            outfit = PZArrayUtil.pickRandom(this.m_FemaleOutfits);
        }
        else {
            outfit = PZArrayUtil.pickRandom(this.m_MaleOutfits);
        }
        return outfit;
    }
    
    public Outfit GetRandomNonProfessionalOutfit(final boolean b) {
        String s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(5) + 1);
        if (Rand.NextBool(4)) {
            if (b) {
                switch (Rand.Next(3)) {
                    case 0: {
                        s = "Mannequin1";
                        break;
                    }
                    case 1: {
                        s = "Mannequin2";
                        break;
                    }
                    case 2: {
                        s = "Classy";
                        break;
                    }
                }
            }
            else {
                switch (Rand.Next(3)) {
                    case 0: {
                        s = "Classy";
                        break;
                    }
                    case 1: {
                        s = "Tourist";
                        break;
                    }
                    case 2: {
                        s = "MallSecurity";
                        break;
                    }
                }
            }
        }
        return this.GetSpecificOutfit(b, s);
    }
    
    public Outfit GetSpecificOutfit(final boolean b, final String s) {
        Outfit outfit;
        if (b) {
            outfit = this.FindFemaleOutfit(s);
        }
        else {
            outfit = this.FindMaleOutfit(s);
        }
        return outfit;
    }
    
    private static OutfitManager tryParse(final String s, final String s2) {
        try {
            return parse(s, s2);
        }
        catch (PZXmlParserException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static OutfitManager parse(final String modID, String pathname) throws PZXmlParserException {
        if ("game".equals(modID)) {
            pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.base.getAbsolutePath(), File.separator, ZomboidFileSystem.processFilePath(pathname, File.separatorChar));
        }
        else {
            pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getModDir(modID), File.separator, ZomboidFileSystem.processFilePath(pathname, File.separatorChar));
        }
        if (!new File(pathname).exists()) {
            return null;
        }
        final OutfitManager outfitManager = PZXmlUtil.parse(OutfitManager.class, pathname);
        if (outfitManager != null) {
            PZArrayUtil.forEach(outfitManager.m_MaleOutfits, outfit -> outfit.setModID(modID));
            PZArrayUtil.forEach(outfitManager.m_FemaleOutfits, outfit2 -> outfit2.setModID(modID));
            PZArrayUtil.forEach(outfitManager.m_MaleOutfits, value -> outfitManager.m_maleOutfitMap.put(value.m_Name, value));
            PZArrayUtil.forEach(outfitManager.m_FemaleOutfits, value2 -> outfitManager.m_femaleOutfitMap.put(value2.m_Name, value2));
        }
        return outfitManager;
    }
    
    private static void tryWrite(final OutfitManager outfitManager, final String s) {
        try {
            write(outfitManager, s);
        }
        catch (JAXBException | IOException ex) {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    private static void write(final OutfitManager outfitManager, final String name) throws IOException, JAXBException {
        final FileOutputStream fileOutputStream = new FileOutputStream(name);
        try {
            final Marshaller marshaller = JAXBContext.newInstance(new Class[] { OutfitManager.class }).createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", (Object)Boolean.TRUE);
            marshaller.marshal((Object)outfitManager, (OutputStream)fileOutputStream);
            fileOutputStream.close();
        }
        catch (Throwable t) {
            try {
                fileOutputStream.close();
            }
            catch (Throwable exception) {
                t.addSuppressed(exception);
            }
            throw t;
        }
    }
    
    public Outfit FindMaleOutfit(final String key) {
        return this.m_maleOutfitMap.get(key);
    }
    
    public Outfit FindFemaleOutfit(final String key) {
        return this.m_femaleOutfitMap.get(key);
    }
    
    private Outfit FindOutfit(final ArrayList<Outfit> list, final String anotherString) {
        Outfit outfit = null;
        for (int i = 0; i < list.size(); ++i) {
            final Outfit outfit2 = list.get(i);
            if (outfit2.m_Name.equalsIgnoreCase(anotherString)) {
                outfit = outfit2;
                break;
            }
        }
        return outfit;
    }
    
    public ClothingItem getClothingItem(final String guid) {
        final String filePathFromGuid = ZomboidFileSystem.instance.getFilePathFromGuid(guid);
        if (filePathFromGuid == null) {
            return null;
        }
        ClothingItemEntry value = this.m_cachedClothingItems.get(guid);
        if (value == null) {
            value = new ClothingItemEntry();
            value.m_filePath = filePathFromGuid;
            value.m_guid = guid;
            value.m_item = null;
            this.m_cachedClothingItems.put(guid, value);
        }
        if (value.m_item != null) {
            value.m_item.m_GUID = guid;
            return value.m_item;
        }
        try {
            value.m_item = (ClothingItem)ClothingItemAssetManager.instance.load(new AssetPath(ZomboidFileSystem.instance.resolveFileOrGUID(filePathFromGuid)));
            value.m_item.m_Name = this.extractClothingItemName(filePathFromGuid);
            value.m_item.m_GUID = guid;
        }
        catch (Exception ex) {
            System.err.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filePathFromGuid));
            ExceptionLogger.logException(ex);
            return null;
        }
        if (value.m_fileWatcher == null) {
            String s = value.m_filePath;
            if (!guid.startsWith("game-")) {
                s = ZomboidFileSystem.instance.getString(s);
            }
            final ClothingItemEntry clothingItemEntry;
            value.m_fileWatcher = new PredicatedFileWatcher(s, p1 -> this.onClothingItemFileChanged(clothingItemEntry));
            DebugFileWatcher.instance.add(value.m_fileWatcher);
        }
        return value.m_item;
    }
    
    private String extractClothingItemName(final String s) {
        return StringUtils.trimSuffix(StringUtils.trimPrefix(s, "media/clothing/clothingItems/"), ".xml");
    }
    
    private void onClothingItemFileChanged(final ClothingItemEntry clothingItemEntry) {
        ClothingItemAssetManager.instance.reload(clothingItemEntry.m_item);
    }
    
    public void onClothingItemStateChanged(final ClothingItem clothingItem) {
        if (clothingItem.isReady()) {
            this.invokeClothingItemChangedEvent(clothingItem.m_GUID);
        }
    }
    
    public void loadAllClothingItems() {
        final ArrayList<Item> allItems = ScriptManager.instance.getAllItems();
        for (int i = 0; i < allItems.size(); ++i) {
            final Item item = allItems.get(i);
            if (item.replacePrimaryHand != null) {
                final String guidFromFilePath = ZomboidFileSystem.instance.getGuidFromFilePath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, item.replacePrimaryHand.clothingItemName));
                if (guidFromFilePath != null) {
                    item.replacePrimaryHand.clothingItem = this.getClothingItem(guidFromFilePath);
                }
            }
            if (item.replaceSecondHand != null) {
                final String guidFromFilePath2 = ZomboidFileSystem.instance.getGuidFromFilePath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, item.replaceSecondHand.clothingItemName));
                if (guidFromFilePath2 != null) {
                    item.replaceSecondHand.clothingItem = this.getClothingItem(guidFromFilePath2);
                }
            }
            if (!StringUtils.isNullOrWhitespace(item.getClothingItem())) {
                final String guidFromFilePath3 = ZomboidFileSystem.instance.getGuidFromFilePath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, item.getClothingItem()));
                if (guidFromFilePath3 != null) {
                    item.setClothingItemAsset(this.getClothingItem(guidFromFilePath3));
                }
            }
        }
    }
    
    public boolean isLoadingClothingItems() {
        final Iterator<ClothingItemEntry> iterator = this.m_cachedClothingItems.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().m_item.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public void debugOutfits() {
        this.debugOutfits(this.m_FemaleOutfits);
        this.debugOutfits(this.m_MaleOutfits);
    }
    
    private void debugOutfits(final ArrayList<Outfit> list) {
        final Iterator<Outfit> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.debugOutfit(iterator.next());
        }
    }
    
    private void debugOutfit(final Outfit outfit) {
        String s = null;
        final Iterator<ClothingItemReference> iterator = outfit.m_items.iterator();
        while (iterator.hasNext()) {
            final ClothingItem clothingItem = this.getClothingItem(iterator.next().itemGUID);
            if (clothingItem != null) {
                if (clothingItem.isEmpty()) {
                    continue;
                }
                final String itemTypeForClothingItem = ScriptManager.instance.getItemTypeForClothingItem(clothingItem.m_Name);
                if (itemTypeForClothingItem == null) {
                    continue;
                }
                final Item item = ScriptManager.instance.getItem(itemTypeForClothingItem);
                if (item == null) {
                    continue;
                }
                if (item.getType() != Item.Type.Container) {
                    continue;
                }
                final String anObject = StringUtils.isNullOrWhitespace(item.getBodyLocation()) ? item.CanBeEquipped : item.getBodyLocation();
                if (s != null && s.equals(anObject)) {
                    DebugLog.Clothing.warn("outfit \"%s\" has multiple bags", outfit.m_Name);
                }
                s = anObject;
            }
        }
    }
    
    private static final class ClothingItemEntry
    {
        public ClothingItem m_item;
        public String m_guid;
        public String m_filePath;
        public PredicatedFileWatcher m_fileWatcher;
    }
}
