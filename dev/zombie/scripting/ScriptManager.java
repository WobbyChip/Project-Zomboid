// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting;

import zombie.core.Translator;
import zombie.SoundManager;
import zombie.GameSounds;
import zombie.inventory.RecipeManager;
import zombie.core.logger.ExceptionLogger;
import java.util.HashSet;
import zombie.iso.MultiStageBuilding;
import zombie.network.NetChecksum;
import zombie.network.GameClient;
import java.util.Collections;
import java.util.Comparator;
import zombie.world.WorldDictionary;
import java.util.Map;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import zombie.scripting.objects.GameSoundScript;
import java.util.Collection;
import zombie.scripting.objects.Fixing;
import java.util.List;
import java.util.Locale;
import zombie.util.StringUtils;
import java.util.Iterator;
import zombie.vehicles.VehicleEngineRPM;
import zombie.scripting.objects.VehicleTemplate;
import zombie.ZomboidFileSystem;
import java.io.File;
import java.net.URI;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.BufferedReader;
import zombie.core.IndieFileLoader;
import zombie.iso.IsoWorld;
import zombie.core.Core;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.UniqueRecipe;
import zombie.scripting.objects.EvolvedRecipe;
import java.util.Stack;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.SoundTimelineScript;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ScriptModule;
import java.util.HashMap;
import java.util.ArrayList;

public final class ScriptManager implements IScriptObjectStore
{
    public static final ScriptManager instance;
    public String currentFileName;
    public final ArrayList<String> scriptsWithVehicles;
    public final ArrayList<String> scriptsWithVehicleTemplates;
    public final HashMap<String, ScriptModule> ModuleMap;
    public final ArrayList<ScriptModule> ModuleList;
    private final HashMap<String, Item> FullTypeToItemMap;
    private final HashMap<String, SoundTimelineScript> SoundTimelineMap;
    public ScriptModule CurrentLoadingModule;
    private final HashMap<String, String> ModuleAliases;
    private final StringBuilder buf;
    private final HashMap<String, ScriptModule> CachedModules;
    private final ArrayList<Recipe> recipesTempList;
    private final Stack<EvolvedRecipe> evolvedRecipesTempList;
    private final Stack<UniqueRecipe> uniqueRecipesTempList;
    private final ArrayList<Item> itemTempList;
    private final HashMap<String, ArrayList<Item>> tagToItemMap;
    private final ArrayList<ModelScript> modelScriptTempList;
    private final ArrayList<VehicleScript> vehicleScriptTempList;
    private final HashMap<String, String> clothingToItemMap;
    private final ArrayList<String> visualDamagesList;
    private static final String Base = "Base";
    private String checksum;
    private HashMap<String, String> tempFileToModMap;
    private static String currentLoadFileMod;
    private static String currentLoadFileAbsPath;
    public static final String VanillaID = "pz-vanilla";
    
    public ScriptManager() {
        this.scriptsWithVehicles = new ArrayList<String>();
        this.scriptsWithVehicleTemplates = new ArrayList<String>();
        this.ModuleMap = new HashMap<String, ScriptModule>();
        this.ModuleList = new ArrayList<ScriptModule>();
        this.FullTypeToItemMap = new HashMap<String, Item>();
        this.SoundTimelineMap = new HashMap<String, SoundTimelineScript>();
        this.CurrentLoadingModule = null;
        this.ModuleAliases = new HashMap<String, String>();
        this.buf = new StringBuilder();
        this.CachedModules = new HashMap<String, ScriptModule>();
        this.recipesTempList = new ArrayList<Recipe>();
        this.evolvedRecipesTempList = new Stack<EvolvedRecipe>();
        this.uniqueRecipesTempList = new Stack<UniqueRecipe>();
        this.itemTempList = new ArrayList<Item>();
        this.tagToItemMap = new HashMap<String, ArrayList<Item>>();
        this.modelScriptTempList = new ArrayList<ModelScript>();
        this.vehicleScriptTempList = new ArrayList<VehicleScript>();
        this.clothingToItemMap = new HashMap<String, String>();
        this.visualDamagesList = new ArrayList<String>();
        this.checksum = "";
    }
    
    public void ParseScript(final String s) {
        if (DebugLog.isEnabled(DebugType.Script)) {
            DebugLog.Script.debugln("Parsing...");
        }
        final ArrayList<String> tokens = ScriptParser.parseTokens(s);
        for (int i = 0; i < tokens.size(); ++i) {
            this.CreateFromToken(tokens.get(i));
        }
    }
    
    public void update() {
    }
    
    public void LoadFile(final String currentFileName, final boolean mapUseJar) throws FileNotFoundException {
        if (DebugLog.isEnabled(DebugType.Script)) {
            DebugLog.Script.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currentFileName, mapUseJar ? " bLoadJar" : ""));
        }
        if (!GameServer.bServer) {
            Thread.yield();
            Core.getInstance().DoFrameReady();
        }
        if (currentFileName.contains(".tmx")) {
            IsoWorld.mapPath = currentFileName.substring(0, currentFileName.lastIndexOf("/"));
            IsoWorld.mapUseJar = mapUseJar;
            DebugLog.Script.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, IsoWorld.mapPath, IsoWorld.mapUseJar ? " mapUseJar" : ""));
            return;
        }
        if (!currentFileName.endsWith(".txt")) {
            DebugLog.Script.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, currentFileName));
            return;
        }
        final InputStreamReader streamReader = IndieFileLoader.getStreamReader(currentFileName, !mapUseJar);
        final BufferedReader bufferedReader = new BufferedReader(streamReader);
        this.buf.setLength(0);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.buf.append(line);
                this.buf.append('\n');
            }
        }
        catch (Exception ex) {
            DebugLog.Script.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Exception;)Ljava/lang/String;, currentFileName, ex));
            return;
        }
        finally {
            try {
                bufferedReader.close();
                streamReader.close();
            }
            catch (Exception ex2) {
                DebugLog.Script.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Exception;)Ljava/lang/String;, currentFileName, ex2));
                ex2.printStackTrace(DebugLog.Script);
            }
        }
        final String stripComments = ScriptParser.stripComments(this.buf.toString());
        this.currentFileName = currentFileName;
        this.ParseScript(stripComments);
        this.currentFileName = null;
    }
    
    private void CreateFromToken(String trim) {
        trim = trim.trim();
        if (trim.indexOf("module") == 0) {
            final int index = trim.indexOf("{");
            final int lastIndex = trim.lastIndexOf("}");
            final String trim2 = trim.split("[{}]")[0].replace("module", "").trim();
            final String substring = trim.substring(index + 1, lastIndex);
            ScriptModule scriptModule = this.ModuleMap.get(trim2);
            if (scriptModule == null) {
                if (DebugLog.isEnabled(DebugType.Script)) {
                    DebugLog.Script.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim2));
                }
                scriptModule = new ScriptModule();
                this.ModuleMap.put(trim2, scriptModule);
                this.ModuleList.add(scriptModule);
            }
            scriptModule.Load(trim2, substring);
        }
    }
    
    public void searchFolders(final URI uri, final File file, final ArrayList<String> list) {
        if (file.isDirectory()) {
            final String[] list2 = file.list();
            for (int i = 0; i < list2.length; ++i) {
                this.searchFolders(uri, new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list2[i])), list);
            }
        }
        else if (file.getAbsolutePath().toLowerCase().endsWith(".txt")) {
            list.add(ZomboidFileSystem.instance.getRelativeFile(uri, file.getAbsolutePath()));
        }
    }
    
    public static String getItemName(final String s) {
        final int index = s.indexOf(46);
        if (index == -1) {
            return s;
        }
        return s.substring(index + 1);
    }
    
    public ScriptModule getModule(String key) {
        if (key.startsWith("Base")) {
            return this.ModuleMap.get("Base");
        }
        if (this.CachedModules.containsKey(key)) {
            return this.CachedModules.get(key);
        }
        ScriptModule module = null;
        if (this.ModuleAliases.containsKey(key)) {
            key = this.ModuleAliases.get(key);
        }
        if (this.CachedModules.containsKey(key)) {
            return this.CachedModules.get(key);
        }
        if (this.ModuleMap.containsKey(key)) {
            if (this.ModuleMap.get(key).disabled) {
                module = null;
            }
            else {
                module = this.ModuleMap.get(key);
            }
        }
        if (module != null) {
            this.CachedModules.put(key, module);
            return module;
        }
        final int index = key.indexOf(".");
        if (index != -1) {
            module = this.getModule(key.substring(0, index));
        }
        if (module != null) {
            this.CachedModules.put(key, module);
            return module;
        }
        return this.ModuleMap.get("Base");
    }
    
    public ScriptModule getModuleNoDisableCheck(String s) {
        if (this.ModuleAliases.containsKey(s)) {
            s = this.ModuleAliases.get(s);
        }
        if (this.ModuleMap.containsKey(s)) {
            return this.ModuleMap.get(s);
        }
        if (s.indexOf(".") != -1) {
            return this.getModule(s.split("\\.")[0]);
        }
        return null;
    }
    
    @Override
    public Item getItem(final String s) {
        if (s.contains(".") && this.FullTypeToItemMap.containsKey(s)) {
            return this.FullTypeToItemMap.get(s);
        }
        final ScriptModule module = this.getModule(s);
        if (module == null) {
            return null;
        }
        return module.getItem(getItemName(s));
    }
    
    public Item FindItem(final String s) {
        if (s.contains(".") && this.FullTypeToItemMap.containsKey(s)) {
            return this.FullTypeToItemMap.get(s);
        }
        final ScriptModule module = this.getModule(s);
        if (module == null) {
            return null;
        }
        Item item = module.getItem(getItemName(s));
        if (item == null) {
            for (int i = 0; i < this.ModuleList.size(); ++i) {
                if (!this.ModuleList.get(i).disabled) {
                    item = module.getItem(getItemName(s));
                    if (item != null) {
                        return item;
                    }
                }
            }
        }
        return item;
    }
    
    public boolean isDrainableItemType(final String s) {
        final Item findItem = this.FindItem(s);
        return findItem != null && findItem.getType() == Item.Type.Drainable;
    }
    
    @Override
    public Recipe getRecipe(final String s) {
        final ScriptModule module = this.getModule(s);
        if (module == null) {
            return null;
        }
        return module.getRecipe(getItemName(s));
    }
    
    public VehicleScript getVehicle(final String s) {
        final ScriptModule module = this.getModule(s);
        if (module == null) {
            return null;
        }
        return module.getVehicle(getItemName(s));
    }
    
    public VehicleTemplate getVehicleTemplate(final String s) {
        final ScriptModule module = this.getModule(s);
        if (module == null) {
            return null;
        }
        return module.getVehicleTemplate(getItemName(s));
    }
    
    public VehicleEngineRPM getVehicleEngineRPM(final String s) {
        final ScriptModule module = this.getModule(s);
        if (module == null) {
            return null;
        }
        return module.getVehicleEngineRPM(getItemName(s));
    }
    
    public void CheckExitPoints() {
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled && scriptModule.CheckExitPoints()) {
                return;
            }
        }
    }
    
    public ArrayList<Item> getAllItems() {
        if (this.itemTempList.isEmpty()) {
            for (int i = 0; i < this.ModuleList.size(); ++i) {
                final ScriptModule scriptModule = this.ModuleList.get(i);
                if (!scriptModule.disabled) {
                    final Iterator<Item> iterator = scriptModule.ItemMap.values().iterator();
                    while (iterator.hasNext()) {
                        this.itemTempList.add(iterator.next());
                    }
                }
            }
        }
        return this.itemTempList;
    }
    
    public ArrayList<Item> getItemsTag(String lowerCase) {
        if (StringUtils.isNullOrWhitespace(lowerCase)) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lowerCase));
        }
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        final ArrayList<Item> list = this.tagToItemMap.get(lowerCase);
        if (list != null) {
            return list;
        }
        final ArrayList<Item> value = new ArrayList<Item>();
        final ArrayList<Item> allItems = this.getAllItems();
        for (int i = 0; i < allItems.size(); ++i) {
            final Item e = allItems.get(i);
            for (int j = 0; j < e.Tags.size(); ++j) {
                if (e.Tags.get(j).equalsIgnoreCase(lowerCase)) {
                    value.add(e);
                    break;
                }
            }
        }
        this.tagToItemMap.put(lowerCase, value);
        return value;
    }
    
    public List<Fixing> getAllFixing(final List<Fixing> list) {
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                list.addAll(scriptModule.FixingMap.values());
            }
        }
        return list;
    }
    
    public ArrayList<Recipe> getAllRecipes() {
        this.recipesTempList.clear();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                for (int j = 0; j < scriptModule.RecipeMap.size(); ++j) {
                    this.recipesTempList.add(scriptModule.RecipeMap.get(j));
                }
            }
        }
        return this.recipesTempList;
    }
    
    public Stack<EvolvedRecipe> getAllEvolvedRecipes() {
        this.evolvedRecipesTempList.clear();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                for (int j = 0; j < scriptModule.EvolvedRecipeMap.size(); ++j) {
                    this.evolvedRecipesTempList.add(scriptModule.EvolvedRecipeMap.get(j));
                }
            }
        }
        return this.evolvedRecipesTempList;
    }
    
    public Stack<UniqueRecipe> getAllUniqueRecipes() {
        this.uniqueRecipesTempList.clear();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                final Iterator<UniqueRecipe> iterator = scriptModule.UniqueRecipeMap.iterator();
                while (iterator != null && iterator.hasNext()) {
                    this.uniqueRecipesTempList.add(iterator.next());
                }
            }
        }
        return this.uniqueRecipesTempList;
    }
    
    public ArrayList<GameSoundScript> getAllGameSounds() {
        final ArrayList<GameSoundScript> list = new ArrayList<GameSoundScript>();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                list.addAll(scriptModule.GameSoundList);
            }
        }
        return list;
    }
    
    public ArrayList<RuntimeAnimationScript> getAllRuntimeAnimationScripts() {
        final ArrayList<RuntimeAnimationScript> list = new ArrayList<RuntimeAnimationScript>();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                list.addAll(scriptModule.RuntimeAnimationScriptMap.values());
            }
        }
        return list;
    }
    
    public ModelScript getModelScript(String itemName) {
        final ScriptModule module = this.getModule(itemName);
        if (module == null) {
            return null;
        }
        itemName = getItemName(itemName);
        return module.ModelScriptMap.get(itemName);
    }
    
    public ArrayList<ModelScript> getAllModelScripts() {
        this.modelScriptTempList.clear();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                this.modelScriptTempList.addAll(scriptModule.ModelScriptMap.values());
            }
        }
        return this.modelScriptTempList;
    }
    
    public ArrayList<VehicleScript> getAllVehicleScripts() {
        this.vehicleScriptTempList.clear();
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule = this.ModuleList.get(i);
            if (!scriptModule.disabled) {
                this.vehicleScriptTempList.addAll(scriptModule.VehicleMap.values());
            }
        }
        return this.vehicleScriptTempList;
    }
    
    public SoundTimelineScript getSoundTimeline(final String key) {
        if (this.SoundTimelineMap.isEmpty()) {
            for (int i = 0; i < this.ModuleList.size(); ++i) {
                final ScriptModule scriptModule = this.ModuleList.get(i);
                if (!scriptModule.disabled) {
                    this.SoundTimelineMap.putAll(scriptModule.SoundTimelineMap);
                }
            }
        }
        return this.SoundTimelineMap.get(key);
    }
    
    public void Reset() {
        final Iterator<ScriptModule> iterator = this.ModuleList.iterator();
        while (iterator.hasNext()) {
            iterator.next().Reset();
        }
        this.ModuleMap.clear();
        this.ModuleList.clear();
        this.ModuleAliases.clear();
        this.CachedModules.clear();
        this.FullTypeToItemMap.clear();
        this.itemTempList.clear();
        this.tagToItemMap.clear();
        this.clothingToItemMap.clear();
        this.scriptsWithVehicles.clear();
        this.scriptsWithVehicleTemplates.clear();
        this.SoundTimelineMap.clear();
    }
    
    public String getChecksum() {
        return this.checksum;
    }
    
    public static String getCurrentLoadFileMod() {
        return ScriptManager.currentLoadFileMod;
    }
    
    public static String getCurrentLoadFileAbsPath() {
        return ScriptManager.currentLoadFileAbsPath;
    }
    
    public void Load() {
        try {
            WorldDictionary.StartScriptLoading();
            this.tempFileToModMap = new HashMap<String, String>();
            final ArrayList<Object> list = new ArrayList<Object>();
            this.searchFolders(ZomboidFileSystem.instance.baseURI, ZomboidFileSystem.instance.getMediaFile("scripts"), (ArrayList<String>)list);
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                this.tempFileToModMap.put(ZomboidFileSystem.instance.getAbsolutePath(iterator.next()), "pz-vanilla");
            }
            final ArrayList<String> list2 = new ArrayList<String>();
            final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
            for (int i = 0; i < modIDs.size(); ++i) {
                final String modDir = ZomboidFileSystem.instance.getModDir(modIDs.get(i));
                if (modDir != null) {
                    final URI uri = new File(modDir).toURI();
                    final int size = list2.size();
                    this.searchFolders(uri, new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDir, File.separator, File.separator)), list2);
                    if (modIDs.get(i).equals("pz-vanilla")) {
                        throw new RuntimeException("Warning mod id is named pz-vanilla!");
                    }
                    for (int j = size; j < list2.size(); ++j) {
                        this.tempFileToModMap.put(ZomboidFileSystem.instance.getAbsolutePath(list2.get(j)), modIDs.get(i));
                    }
                }
            }
            final Comparator<String> comparator = new Comparator<String>() {
                @Override
                public int compare(final String pathname, final String s) {
                    final String name = new File(pathname).getName();
                    final String name2 = new File(s).getName();
                    if (name.startsWith("template_") && !name2.startsWith("template_")) {
                        return -1;
                    }
                    if (!name.startsWith("template_") && name2.startsWith("template_")) {
                        return 1;
                    }
                    return pathname.compareTo(s);
                }
            };
            Collections.sort(list, (Comparator<? super Object>)comparator);
            Collections.sort((List<Object>)list2, (Comparator<? super Object>)comparator);
            list.addAll(list2);
            if (GameClient.bClient || GameServer.bServer) {
                NetChecksum.checksummer.reset(true);
                NetChecksum.GroupOfFiles.initChecksum();
            }
            MultiStageBuilding.stages.clear();
            final HashSet<String> set = new HashSet<String>();
            for (final String s : list) {
                if (set.contains(s)) {
                    continue;
                }
                set.add(s);
                final String key = ScriptManager.currentLoadFileAbsPath = ZomboidFileSystem.instance.getAbsolutePath(s);
                ScriptManager.currentLoadFileMod = this.tempFileToModMap.get(key);
                this.LoadFile(s, false);
                if (!GameClient.bClient && !GameServer.bServer) {
                    continue;
                }
                NetChecksum.checksummer.addFile(s, key);
            }
            if (GameClient.bClient || GameServer.bServer) {
                this.checksum = NetChecksum.checksummer.checksumToString();
                if (GameServer.bServer) {
                    DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.checksum));
                }
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        this.buf.setLength(0);
        for (int k = 0; k < this.ModuleList.size(); ++k) {
            for (final Item value : this.ModuleList.get(k).ItemMap.values()) {
                this.FullTypeToItemMap.put(value.getFullName(), value);
            }
        }
        this.debugItems();
        this.resolveItemTypes();
        WorldDictionary.ScriptsLoaded();
        RecipeManager.Loaded();
        GameSounds.ScriptsLoaded();
        ModelScript.ScriptsLoaded();
        if (SoundManager.instance != null) {
            SoundManager.instance.debugScriptSounds();
        }
        Translator.debugItemEvolvedRecipeNames();
        Translator.debugItemNames();
        Translator.debugMultiStageBuildNames();
        Translator.debugRecipeNames();
        this.createClothingItemMap();
        this.createZedDmgMap();
    }
    
    private void debugItems() {
        for (final Item item : ScriptManager.instance.getAllItems()) {
            if (item.getType() == Item.Type.Drainable && item.getReplaceOnUse() != null) {
                DebugLog.Script.warn("%s ReplaceOnUse instead of ReplaceOnDeplete", item.getFullName());
            }
            if (item.getType() == Item.Type.Weapon && !item.HitSound.equals(item.hitFloorSound)) {
                continue;
            }
        }
    }
    
    public ArrayList<Recipe> getAllRecipesFor(final String anObject) {
        final ArrayList<Recipe> allRecipes = this.getAllRecipes();
        final ArrayList<Recipe> list = new ArrayList<Recipe>();
        for (int i = 0; i < allRecipes.size(); ++i) {
            String s = allRecipes.get(i).Result.type;
            if (s.contains(".")) {
                s = s.substring(s.indexOf(".") + 1);
            }
            if (s.equals(anObject)) {
                list.add(allRecipes.get(i));
            }
        }
        return list;
    }
    
    public String getItemTypeForClothingItem(final String key) {
        return this.clothingToItemMap.get(key);
    }
    
    public Item getItemForClothingItem(final String s) {
        final String itemTypeForClothingItem = this.getItemTypeForClothingItem(s);
        if (itemTypeForClothingItem == null) {
            return null;
        }
        return this.FindItem(itemTypeForClothingItem);
    }
    
    private void createZedDmgMap() {
        this.visualDamagesList.clear();
        for (final Item item : this.getModule("Base").ItemMap.values()) {
            if (!StringUtils.isNullOrWhitespace(item.getBodyLocation()) && "ZedDmg".equals(item.getBodyLocation())) {
                this.visualDamagesList.add(item.getName());
            }
        }
    }
    
    public ArrayList<String> getZedDmgMap() {
        return this.visualDamagesList;
    }
    
    private void createClothingItemMap() {
        for (final Item item : this.getAllItems()) {
            if (StringUtils.isNullOrWhitespace(item.getClothingItem())) {
                continue;
            }
            if (DebugLog.isEnabled(DebugType.Script)) {
                DebugLog.Script.debugln("ClothingItem \"%s\" <---> Item \"%s\"", item.getClothingItem(), item.getFullName());
            }
            this.clothingToItemMap.put(item.getClothingItem(), item.getFullName());
        }
    }
    
    private void resolveItemTypes() {
        final Iterator<Item> iterator = this.getAllItems().iterator();
        while (iterator.hasNext()) {
            iterator.next().resolveItemTypes();
        }
    }
    
    public String resolveItemType(final ScriptModule scriptModule, final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        if (s.contains(".")) {
            return s;
        }
        final Item item = scriptModule.getItem(s);
        if (item != null) {
            return item.getFullName();
        }
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            final ScriptModule scriptModule2 = this.ModuleList.get(i);
            if (!scriptModule2.disabled) {
                final Item item2 = scriptModule2.getItem(s);
                if (item2 != null) {
                    return item2.getFullName();
                }
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    static {
        instance = new ScriptManager();
    }
}
