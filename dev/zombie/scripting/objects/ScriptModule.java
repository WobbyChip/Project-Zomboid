// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import zombie.scripting.ScriptParser;
import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.MultiStageBuilding;
import zombie.debug.DebugLog;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptManager;
import java.util.Comparator;
import zombie.vehicles.VehicleEngineRPM;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.scripting.IScriptObjectStore;

public final class ScriptModule extends BaseScriptObject implements IScriptObjectStore
{
    public String name;
    public String value;
    public final HashMap<String, Item> ItemMap;
    public final HashMap<String, GameSoundScript> GameSoundMap;
    public final ArrayList<GameSoundScript> GameSoundList;
    public final TreeMap<String, ModelScript> ModelScriptMap;
    public final HashMap<String, RuntimeAnimationScript> RuntimeAnimationScriptMap;
    public final HashMap<String, SoundTimelineScript> SoundTimelineMap;
    public final HashMap<String, VehicleScript> VehicleMap;
    public final HashMap<String, VehicleTemplate> VehicleTemplateMap;
    public final HashMap<String, VehicleEngineRPM> VehicleEngineRPMMap;
    public final ArrayList<Recipe> RecipeMap;
    public final HashMap<String, Recipe> RecipeByName;
    public final HashMap<String, Recipe> RecipesWithDotInName;
    public final ArrayList<EvolvedRecipe> EvolvedRecipeMap;
    public final ArrayList<UniqueRecipe> UniqueRecipeMap;
    public final HashMap<String, Fixing> FixingMap;
    public final ArrayList<String> Imports;
    public boolean disabled;
    
    public ScriptModule() {
        this.ItemMap = new HashMap<String, Item>();
        this.GameSoundMap = new HashMap<String, GameSoundScript>();
        this.GameSoundList = new ArrayList<GameSoundScript>();
        this.ModelScriptMap = new TreeMap<String, ModelScript>(String.CASE_INSENSITIVE_ORDER);
        this.RuntimeAnimationScriptMap = new HashMap<String, RuntimeAnimationScript>();
        this.SoundTimelineMap = new HashMap<String, SoundTimelineScript>();
        this.VehicleMap = new HashMap<String, VehicleScript>();
        this.VehicleTemplateMap = new HashMap<String, VehicleTemplate>();
        this.VehicleEngineRPMMap = new HashMap<String, VehicleEngineRPM>();
        this.RecipeMap = new ArrayList<Recipe>();
        this.RecipeByName = new HashMap<String, Recipe>();
        this.RecipesWithDotInName = new HashMap<String, Recipe>();
        this.EvolvedRecipeMap = new ArrayList<EvolvedRecipe>();
        this.UniqueRecipeMap = new ArrayList<UniqueRecipe>();
        this.FixingMap = new HashMap<String, Fixing>();
        this.Imports = new ArrayList<String>();
        this.disabled = false;
    }
    
    public void Load(final String name, final String s) {
        this.name = name;
        this.value = s.trim();
        (ScriptManager.instance.CurrentLoadingModule = this).ParseScriptPP(this.value);
        this.ParseScript(this.value);
        this.value = "";
    }
    
    private String GetTokenType(final String s) {
        final int index = s.indexOf(123);
        if (index == -1) {
            return null;
        }
        final String trim = s.substring(0, index).trim();
        final int index2 = trim.indexOf(32);
        final int index3 = trim.indexOf(9);
        if (index2 != -1 && index3 != -1) {
            return trim.substring(0, PZMath.min(index2, index3));
        }
        if (index2 != -1) {
            return trim.substring(0, index2);
        }
        if (index3 != -1) {
            return trim.substring(0, index3);
        }
        return trim;
    }
    
    private void CreateFromTokenPP(String trim) {
        trim = trim.trim();
        final String getTokenType = this.GetTokenType(trim);
        if (getTokenType == null) {
            return;
        }
        if ("item".equals(getTokenType)) {
            this.ItemMap.put(trim.split("[{}]")[0].replace("item", "").trim(), new Item());
        }
        else if ("model".equals(getTokenType)) {
            final String trim2 = trim.split("[{}]")[0].replace("model", "").trim();
            if (this.ModelScriptMap.containsKey(trim2)) {
                this.ModelScriptMap.get(trim2).reset();
            }
            else {
                this.ModelScriptMap.put(trim2, new ModelScript());
            }
        }
        else if ("sound".equals(getTokenType)) {
            final String trim3 = trim.split("[{}]")[0].replace("sound", "").trim();
            if (this.GameSoundMap.containsKey(trim3)) {
                this.GameSoundMap.get(trim3).reset();
            }
            else {
                final GameSoundScript gameSoundScript = new GameSoundScript();
                this.GameSoundMap.put(trim3, gameSoundScript);
                this.GameSoundList.add(gameSoundScript);
            }
        }
        else if ("soundTimeline".equals(getTokenType)) {
            final String trim4 = trim.split("[{}]")[0].replace("soundTimeline", "").trim();
            if (this.SoundTimelineMap.containsKey(trim4)) {
                this.SoundTimelineMap.get(trim4).reset();
            }
            else {
                this.SoundTimelineMap.put(trim4, new SoundTimelineScript());
            }
        }
        else if ("vehicle".equals(getTokenType)) {
            this.VehicleMap.put(trim.split("[{}]")[0].replace("vehicle", "").trim(), new VehicleScript());
        }
        else if ("template".equals(getTokenType)) {
            final String[] split = trim.split("[{}]")[0].replace("template", "").trim().split("\\s+");
            if (split.length == 2) {
                final String trim5 = split[0].trim();
                final String trim6 = split[1].trim();
                if ("vehicle".equals(trim5)) {
                    final VehicleTemplate value = new VehicleTemplate(this, trim6, trim);
                    value.module = this;
                    this.VehicleTemplateMap.put(trim6, value);
                }
            }
        }
        else if ("animation".equals(getTokenType)) {
            final String trim7 = trim.split("[{}]")[0].replace("animation", "").trim();
            if (this.RuntimeAnimationScriptMap.containsKey(trim7)) {
                this.RuntimeAnimationScriptMap.get(trim7).reset();
            }
            else {
                this.RuntimeAnimationScriptMap.put(trim7, new RuntimeAnimationScript());
            }
        }
        else if ("vehicleEngineRPM".equals(getTokenType)) {
            final String trim8 = trim.split("[{}]")[0].replace("vehicleEngineRPM", "").trim();
            if (this.VehicleEngineRPMMap.containsKey(trim8)) {
                this.VehicleEngineRPMMap.get(trim8).reset();
            }
            else {
                this.VehicleEngineRPMMap.put(trim8, new VehicleEngineRPM());
            }
        }
    }
    
    private void CreateFromToken(String trim) {
        trim = trim.trim();
        final String getTokenType = this.GetTokenType(trim);
        if (getTokenType == null) {
            return;
        }
        if ("imports".equals(getTokenType)) {
            final String[] split = trim.split("[{}]")[1].split(",");
            for (int i = 0; i < split.length; ++i) {
                if (split[i].trim().length() > 0) {
                    final String trim2 = split[i].trim();
                    if (trim2.equals(this.getName())) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName()));
                    }
                    else {
                        this.Imports.add(trim2);
                    }
                }
            }
        }
        else if ("item".equals(getTokenType)) {
            final String[] split2 = trim.split("[{}]");
            final String trim3 = split2[0].replace("item", "").trim();
            final String[] split3 = split2[1].split(",");
            final Item item = this.ItemMap.get(trim3);
            item.module = this;
            try {
                item.Load(trim3, split3);
            }
            catch (Exception ex) {
                DebugLog.log(ex);
            }
        }
        else if ("recipe".equals(getTokenType)) {
            final String[] split4 = trim.split("[{}]");
            final String trim4 = split4[0].replace("recipe", "").trim();
            final String[] split5 = split4[1].split(",");
            final Recipe value = new Recipe();
            this.RecipeMap.add(value);
            if (!this.RecipeByName.containsKey(trim4)) {
                this.RecipeByName.put(trim4, value);
            }
            if (trim4.contains(".")) {
                this.RecipesWithDotInName.put(trim4, value);
            }
            value.module = this;
            value.Load(trim4, split5);
        }
        else if ("uniquerecipe".equals(getTokenType)) {
            final String[] split6 = trim.split("[{}]");
            final String trim5 = split6[0].replace("uniquerecipe", "").trim();
            final String[] split7 = split6[1].split(",");
            final UniqueRecipe e = new UniqueRecipe(trim5);
            this.UniqueRecipeMap.add(e);
            e.module = this;
            e.Load(trim5, split7);
        }
        else if ("evolvedrecipe".equals(getTokenType)) {
            final String[] split8 = trim.split("[{}]");
            final String trim6 = split8[0].replace("evolvedrecipe", "").trim();
            final String[] split9 = split8[1].split(",");
            boolean b = false;
            for (final EvolvedRecipe evolvedRecipe : this.EvolvedRecipeMap) {
                if (evolvedRecipe.name.equals(trim6)) {
                    evolvedRecipe.Load(trim6, split9);
                    evolvedRecipe.module = this;
                    b = true;
                }
            }
            if (!b) {
                final EvolvedRecipe e2 = new EvolvedRecipe(trim6);
                this.EvolvedRecipeMap.add(e2);
                e2.module = this;
                e2.Load(trim6, split9);
            }
        }
        else if ("fixing".equals(getTokenType)) {
            final String[] split10 = trim.split("[{}]");
            final String trim7 = split10[0].replace("fixing", "").trim();
            final String[] split11 = split10[1].split(",");
            final Fixing value2 = new Fixing();
            value2.module = this;
            this.FixingMap.put(trim7, value2);
            value2.Load(trim7, split11);
        }
        else if ("multistagebuild".equals(getTokenType)) {
            final String[] split12 = trim.split("[{}]");
            final String trim8 = split12[0].replace("multistagebuild", "").trim();
            final String[] split13 = split12[1].split(",");
            final MultiStageBuilding.Stage stage = new MultiStageBuilding().new Stage();
            stage.Load(trim8, split13);
            MultiStageBuilding.addStage(stage);
        }
        else if ("model".equals(getTokenType)) {
            final String trim9 = trim.split("[{}]")[0].replace("model", "").trim();
            final ModelScript modelScript = this.ModelScriptMap.get(trim9);
            modelScript.module = this;
            try {
                modelScript.Load(trim9, trim);
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        else if ("sound".equals(getTokenType)) {
            final String trim10 = trim.split("[{}]")[0].replace("sound", "").trim();
            final GameSoundScript gameSoundScript = this.GameSoundMap.get(trim10);
            gameSoundScript.module = this;
            try {
                gameSoundScript.Load(trim10, trim);
            }
            catch (Throwable t2) {
                ExceptionLogger.logException(t2);
            }
        }
        else if ("soundTimeline".equals(getTokenType)) {
            final String trim11 = trim.split("[{}]")[0].replace("soundTimeline", "").trim();
            final SoundTimelineScript soundTimelineScript = this.SoundTimelineMap.get(trim11);
            soundTimelineScript.module = this;
            try {
                soundTimelineScript.Load(trim11, trim);
            }
            catch (Throwable t3) {
                ExceptionLogger.logException(t3);
            }
        }
        else if ("vehicle".equals(getTokenType)) {
            final String trim12 = trim.split("[{}]")[0].replace("vehicle", "").trim();
            final VehicleScript vehicleScript = this.VehicleMap.get(trim12);
            vehicleScript.module = this;
            try {
                vehicleScript.Load(trim12, trim);
                vehicleScript.Loaded();
            }
            catch (Exception ex2) {
                ExceptionLogger.logException(ex2);
            }
        }
        else if (!"template".equals(getTokenType)) {
            if ("animation".equals(getTokenType)) {
                final String trim13 = trim.split("[{}]")[0].replace("animation", "").trim();
                final RuntimeAnimationScript runtimeAnimationScript = this.RuntimeAnimationScriptMap.get(trim13);
                runtimeAnimationScript.module = this;
                try {
                    runtimeAnimationScript.Load(trim13, trim);
                }
                catch (Throwable t4) {
                    ExceptionLogger.logException(t4);
                }
            }
            else if ("vehicleEngineRPM".equals(getTokenType)) {
                final String trim14 = trim.split("[{}]")[0].replace("vehicleEngineRPM", "").trim();
                final VehicleEngineRPM vehicleEngineRPM = this.VehicleEngineRPMMap.get(trim14);
                vehicleEngineRPM.module = this;
                try {
                    vehicleEngineRPM.Load(trim14, trim);
                }
                catch (Throwable t5) {
                    this.VehicleEngineRPMMap.remove(trim14);
                    ExceptionLogger.logException(t5);
                }
            }
            else {
                DebugLog.Script.warn("unknown script object \"%s\"", getTokenType);
            }
        }
    }
    
    public void ParseScript(final String s) {
        final ArrayList<String> tokens = ScriptParser.parseTokens(s);
        for (int i = 0; i < tokens.size(); ++i) {
            this.CreateFromToken(tokens.get(i));
        }
    }
    
    public void ParseScriptPP(final String s) {
        final ArrayList<String> tokens = ScriptParser.parseTokens(s);
        for (int i = 0; i < tokens.size(); ++i) {
            this.CreateFromTokenPP(tokens.get(i));
        }
    }
    
    @Override
    public Item getItem(final String s) {
        if (s.contains(".")) {
            return ScriptManager.instance.getItem(s);
        }
        if (!this.ItemMap.containsKey(s)) {
            for (int i = 0; i < this.Imports.size(); ++i) {
                final Item item = ScriptManager.instance.getModule(this.Imports.get(i)).getItem(s);
                if (item != null) {
                    return item;
                }
            }
            return null;
        }
        return this.ItemMap.get(s);
    }
    
    @Override
    public Recipe getRecipe(final String s) {
        if (s.contains(".") && !this.RecipesWithDotInName.containsKey(s)) {
            return ScriptManager.instance.getRecipe(s);
        }
        final Recipe recipe = this.RecipeByName.get(s);
        if (recipe != null) {
            return recipe;
        }
        for (int i = 0; i < this.Imports.size(); ++i) {
            final ScriptModule module = ScriptManager.instance.getModule(this.Imports.get(i));
            if (module != null) {
                final Recipe recipe2 = module.getRecipe(s);
                if (recipe2 != null) {
                    return recipe2;
                }
            }
        }
        return null;
    }
    
    public VehicleScript getVehicle(final String s) {
        if (s.contains(".")) {
            return ScriptManager.instance.getVehicle(s);
        }
        if (!this.VehicleMap.containsKey(s)) {
            for (int i = 0; i < this.Imports.size(); ++i) {
                final VehicleScript vehicle = ScriptManager.instance.getModule(this.Imports.get(i)).getVehicle(s);
                if (vehicle != null) {
                    return vehicle;
                }
            }
            return null;
        }
        return this.VehicleMap.get(s);
    }
    
    public VehicleTemplate getVehicleTemplate(final String s) {
        if (s.contains(".")) {
            return ScriptManager.instance.getVehicleTemplate(s);
        }
        if (!this.VehicleTemplateMap.containsKey(s)) {
            for (int i = 0; i < this.Imports.size(); ++i) {
                final VehicleTemplate vehicleTemplate = ScriptManager.instance.getModule(this.Imports.get(i)).getVehicleTemplate(s);
                if (vehicleTemplate != null) {
                    return vehicleTemplate;
                }
            }
            return null;
        }
        return this.VehicleTemplateMap.get(s);
    }
    
    public VehicleEngineRPM getVehicleEngineRPM(final String key) {
        if (key.contains(".")) {
            return ScriptManager.instance.getVehicleEngineRPM(key);
        }
        return this.VehicleEngineRPMMap.get(key);
    }
    
    public boolean CheckExitPoints() {
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void Reset() {
        this.ItemMap.clear();
        this.GameSoundMap.clear();
        this.GameSoundList.clear();
        this.ModelScriptMap.clear();
        this.RuntimeAnimationScriptMap.clear();
        this.SoundTimelineMap.clear();
        this.VehicleMap.clear();
        this.VehicleTemplateMap.clear();
        this.VehicleEngineRPMMap.clear();
        this.RecipeMap.clear();
        this.RecipeByName.clear();
        this.RecipesWithDotInName.clear();
        this.EvolvedRecipeMap.clear();
        this.UniqueRecipeMap.clear();
        this.FixingMap.clear();
        this.Imports.clear();
    }
}
