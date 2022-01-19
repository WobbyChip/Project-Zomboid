// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.Collection;
import java.util.Arrays;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.characters.skills.PerkFactory;
import zombie.util.StringUtils;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;

public class Recipe extends BaseScriptObject
{
    private boolean canBeDoneFromFloor;
    public float TimeToMake;
    public String Sound;
    protected String AnimNode;
    protected String Prop1;
    protected String Prop2;
    public final ArrayList<Source> Source;
    public Result Result;
    public boolean AllowDestroyedItem;
    public String LuaTest;
    public String LuaCreate;
    public String LuaGrab;
    public String name;
    private String originalname;
    private String nearItem;
    private String LuaCanPerform;
    private String tooltip;
    public ArrayList<RequiredSkill> skillRequired;
    public String LuaGiveXP;
    private boolean needToBeLearn;
    protected String category;
    protected boolean removeResultItem;
    private float heat;
    protected boolean stopOnWalk;
    protected boolean stopOnRun;
    
    public boolean isCanBeDoneFromFloor() {
        return this.canBeDoneFromFloor;
    }
    
    public void setCanBeDoneFromFloor(final boolean canBeDoneFromFloor) {
        this.canBeDoneFromFloor = canBeDoneFromFloor;
    }
    
    public Recipe() {
        this.canBeDoneFromFloor = false;
        this.Source = new ArrayList<Source>();
        this.tooltip = null;
        this.skillRequired = null;
        this.needToBeLearn = false;
        this.category = null;
        this.removeResultItem = false;
        this.heat = 0.0f;
        this.stopOnWalk = true;
        this.stopOnRun = true;
        this.TimeToMake = 0.0f;
        this.Result = null;
        this.AllowDestroyedItem = false;
        this.LuaTest = null;
        this.LuaCreate = null;
        this.LuaGrab = null;
        this.setOriginalname(this.name = "recipe");
    }
    
    public int FindIndexOf(final InventoryItem inventoryItem) {
        return -1;
    }
    
    public ArrayList<Source> getSource() {
        return this.Source;
    }
    
    public int getNumberOfNeededItem() {
        int n = 0;
        for (int i = 0; i < this.getSource().size(); ++i) {
            final Source source = this.getSource().get(i);
            if (!source.getItems().isEmpty()) {
                n += (int)source.getCount();
            }
        }
        return n;
    }
    
    public float getTimeToMake() {
        return this.TimeToMake;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFullType() {
        return invokedynamic(makeConcatWithConstants:(Lzombie/scripting/objects/ScriptModule;Ljava/lang/String;)Ljava/lang/String;, this.module, this.originalname);
    }
    
    @Override
    public void Load(final String s, final String[] array) {
        this.name = Translator.getRecipeName(s);
        this.originalname = s;
        boolean equalsIgnoreCase = false;
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].trim().isEmpty()) {
                if (array[i].contains(":")) {
                    final String[] split = array[i].split(":");
                    final String trim = split[0].trim();
                    final String trim2 = split[1].trim();
                    if (trim.equals("Override")) {
                        equalsIgnoreCase = trim2.trim().equalsIgnoreCase("true");
                    }
                    if (trim.equals("AnimNode")) {
                        this.AnimNode = trim2.trim();
                    }
                    if (trim.equals("Prop1")) {
                        this.Prop1 = trim2.trim();
                    }
                    if (trim.equals("Prop2")) {
                        this.Prop2 = trim2.trim();
                    }
                    if (trim.equals("Time")) {
                        this.TimeToMake = Float.parseFloat(trim2);
                    }
                    if (trim.equals("Sound")) {
                        this.Sound = trim2.trim();
                    }
                    if (trim.equals("Result")) {
                        this.DoResult(trim2);
                    }
                    if (trim.equals("OnCanPerform")) {
                        this.LuaCanPerform = StringUtils.discardNullOrWhitespace(trim2);
                    }
                    if (trim.equals("OnTest")) {
                        this.LuaTest = trim2;
                    }
                    if (trim.equals("OnCreate")) {
                        this.LuaCreate = trim2;
                    }
                    if (trim.equals("AllowDestroyedItem")) {
                        this.AllowDestroyedItem = Boolean.parseBoolean(trim2);
                    }
                    if (trim.equals("OnGrab")) {
                        this.LuaGrab = trim2;
                    }
                    if (trim.toLowerCase().equals("needtobelearn")) {
                        this.setNeedToBeLearn(trim2.trim().equalsIgnoreCase("true"));
                    }
                    if (trim.toLowerCase().equals("category")) {
                        this.setCategory(trim2.trim());
                    }
                    if (trim.equals("RemoveResultItem")) {
                        this.removeResultItem = trim2.trim().equalsIgnoreCase("true");
                    }
                    if (trim.equals("CanBeDoneFromFloor")) {
                        this.setCanBeDoneFromFloor(trim2.trim().equalsIgnoreCase("true"));
                    }
                    if (trim.equals("NearItem")) {
                        this.setNearItem(trim2.trim());
                    }
                    if (trim.equals("SkillRequired")) {
                        this.skillRequired = new ArrayList<RequiredSkill>();
                        final String[] split2 = trim2.split(";");
                        for (int j = 0; j < split2.length; ++j) {
                            final String[] split3 = split2[j].split("=");
                            final PerkFactory.Perk fromString = PerkFactory.Perks.FromString(split3[0]);
                            if (fromString == PerkFactory.Perks.MAX) {
                                DebugLog.Recipe.warn("Unknown skill \"%s\" in recipe \"%s\"", split3, this.name);
                            }
                            else {
                                this.skillRequired.add(new RequiredSkill(fromString, PZMath.tryParseInt(split3[1], 1)));
                            }
                        }
                    }
                    if (trim.equals("OnGiveXP")) {
                        this.LuaGiveXP = trim2;
                    }
                    if (trim.equalsIgnoreCase("Tooltip")) {
                        this.tooltip = StringUtils.discardNullOrWhitespace(trim2);
                    }
                    if (trim.equals("Obsolete") && trim2.trim().toLowerCase().equals("true")) {
                        this.module.RecipeMap.remove(this);
                        this.module.RecipeByName.remove(this.getOriginalname());
                        this.module.RecipesWithDotInName.remove(this);
                        return;
                    }
                    if (trim.equals("Heat")) {
                        this.heat = Float.parseFloat(trim2);
                    }
                    if (trim.equals("NoBrokenItems")) {
                        this.AllowDestroyedItem = !StringUtils.tryParseBoolean(trim2);
                    }
                    if (trim.equals("StopOnWalk")) {
                        this.stopOnWalk = trim2.trim().equalsIgnoreCase("true");
                    }
                    if (trim.equals("StopOnRun")) {
                        this.stopOnRun = trim2.trim().equalsIgnoreCase("true");
                    }
                }
                else {
                    this.DoSource(array[i].trim());
                }
            }
        }
        if (equalsIgnoreCase) {
            final Recipe recipe = this.module.getRecipe(s);
            if (recipe != null && recipe != this) {
                this.module.RecipeMap.remove(recipe);
                this.module.RecipeByName.put(s, this);
            }
        }
    }
    
    public void DoSource(String e) {
        final Source e2 = new Source();
        if (e.contains("=")) {
            e2.count = new Float(e.split("=")[1].trim());
            e = e.split("=")[0].trim();
        }
        if (e.indexOf("keep") == 0) {
            e = e.replace("keep ", "");
            e2.keep = true;
        }
        if (e.contains(";")) {
            final String[] split = e.split(";");
            e = split[0];
            e2.use = Float.parseFloat(split[1]);
        }
        if (e.indexOf("destroy") == 0) {
            e = e.replace("destroy ", "");
            e2.destroy = true;
        }
        if (e.equals("null")) {
            e2.getItems().clear();
        }
        else if (e.contains("/")) {
            e = e.replaceFirst("keep ", "").trim();
            e2.getItems().addAll(Arrays.asList(e.split("/")));
        }
        else {
            e2.getItems().add(e);
        }
        if (!e.isEmpty()) {
            this.Source.add(e2);
        }
    }
    
    public void DoResult(String type) {
        final Result result = new Result();
        if (type.contains("=")) {
            final String[] split = type.split("=");
            type = split[0].trim();
            result.count = Integer.parseInt(split[1].trim());
        }
        if (type.contains(";")) {
            final String[] split2 = type.split(";");
            type = split2[0].trim();
            result.drainableCount = Integer.parseInt(split2[1].trim());
        }
        if (type.contains(".")) {
            result.type = type.split("\\.")[1];
            result.module = type.split("\\.")[0];
        }
        else {
            result.type = type;
        }
        this.Result = result;
    }
    
    public Result getResult() {
        return this.Result;
    }
    
    public String getSound() {
        return this.Sound;
    }
    
    public void setSound(final String sound) {
        this.Sound = sound;
    }
    
    public String getOriginalname() {
        return this.originalname;
    }
    
    public void setOriginalname(final String originalname) {
        this.originalname = originalname;
    }
    
    public boolean needToBeLearn() {
        return this.needToBeLearn;
    }
    
    public void setNeedToBeLearn(final boolean needToBeLearn) {
        this.needToBeLearn = needToBeLearn;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String category) {
        this.category = category;
    }
    
    public ArrayList<String> getRequiredSkills() {
        ArrayList<String> list = null;
        if (this.skillRequired != null) {
            list = new ArrayList<String>();
            for (int i = 0; i < this.skillRequired.size(); ++i) {
                final RequiredSkill requiredSkill = this.skillRequired.get(i);
                final PerkFactory.Perk perk = PerkFactory.getPerk(requiredSkill.perk);
                if (perk == null) {
                    list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, requiredSkill.perk.name, requiredSkill.level));
                }
                else {
                    list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, perk.name, requiredSkill.level));
                }
            }
        }
        return list;
    }
    
    public int getRequiredSkillCount() {
        return (this.skillRequired == null) ? 0 : this.skillRequired.size();
    }
    
    public RequiredSkill getRequiredSkill(final int index) {
        if (this.skillRequired == null || index < 0 || index >= this.skillRequired.size()) {
            return null;
        }
        return this.skillRequired.get(index);
    }
    
    public void clearRequiredSkills() {
        if (this.skillRequired == null) {
            return;
        }
        this.skillRequired.clear();
    }
    
    public void addRequiredSkill(final PerkFactory.Perk perk, final int n) {
        if (this.skillRequired == null) {
            this.skillRequired = new ArrayList<RequiredSkill>();
        }
        this.skillRequired.add(new RequiredSkill(perk, n));
    }
    
    public Source findSource(final String anObject) {
        for (int i = 0; i < this.Source.size(); ++i) {
            final Source source = this.Source.get(i);
            for (int j = 0; j < source.getItems().size(); ++j) {
                if (source.getItems().get(j).equals(anObject)) {
                    return source;
                }
            }
        }
        return null;
    }
    
    public boolean isDestroy(final String s) {
        final Source source = this.findSource(s);
        if (source != null) {
            return source.isDestroy();
        }
        throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getOriginalname(), s));
    }
    
    public boolean isKeep(final String s) {
        final Source source = this.findSource(s);
        if (source != null) {
            return source.isKeep();
        }
        throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getOriginalname(), s));
    }
    
    public float getHeat() {
        return this.heat;
    }
    
    public boolean noBrokenItems() {
        return !this.AllowDestroyedItem;
    }
    
    public boolean isAllowDestroyedItem() {
        return this.AllowDestroyedItem;
    }
    
    public void setAllowDestroyedItem(final boolean allowDestroyedItem) {
        this.AllowDestroyedItem = allowDestroyedItem;
    }
    
    public int getWaterAmountNeeded() {
        final Source source = this.findSource("Water");
        if (source != null) {
            return (int)source.getCount();
        }
        return 0;
    }
    
    public String getNearItem() {
        return this.nearItem;
    }
    
    public void setNearItem(final String nearItem) {
        this.nearItem = nearItem;
    }
    
    public String getCanPerform() {
        return this.LuaCanPerform;
    }
    
    public void setCanPerform(final String luaCanPerform) {
        this.LuaCanPerform = luaCanPerform;
    }
    
    public String getLuaTest() {
        return this.LuaTest;
    }
    
    public void setLuaTest(final String luaTest) {
        this.LuaTest = luaTest;
    }
    
    public String getLuaCreate() {
        return this.LuaCreate;
    }
    
    public void setLuaCreate(final String luaCreate) {
        this.LuaCreate = luaCreate;
    }
    
    public String getLuaGrab() {
        return this.LuaGrab;
    }
    
    public void setLuaGrab(final String luaGrab) {
        this.LuaGrab = luaGrab;
    }
    
    public String getLuaGiveXP() {
        return this.LuaGiveXP;
    }
    
    public void setLuaGiveXP(final String luaGiveXP) {
        this.LuaGiveXP = luaGiveXP;
    }
    
    public boolean isRemoveResultItem() {
        return this.removeResultItem;
    }
    
    public void setRemoveResultItem(final boolean removeResultItem) {
        this.removeResultItem = removeResultItem;
    }
    
    public String getAnimNode() {
        return this.AnimNode;
    }
    
    public void setAnimNode(final String animNode) {
        this.AnimNode = animNode;
    }
    
    public String getProp1() {
        return this.Prop1;
    }
    
    public void setProp1(final String prop1) {
        this.Prop1 = prop1;
    }
    
    public String getProp2() {
        return this.Prop2;
    }
    
    public void setProp2(final String prop2) {
        this.Prop2 = prop2;
    }
    
    public String getTooltip() {
        return this.tooltip;
    }
    
    public void setTopOnWalk(final boolean stopOnWalk) {
        this.stopOnWalk = stopOnWalk;
    }
    
    public boolean isStopOnWalk() {
        return this.stopOnWalk;
    }
    
    public void setTopOnRun(final boolean stopOnRun) {
        this.stopOnRun = stopOnRun;
    }
    
    public boolean isStopOnRun() {
        return this.stopOnRun;
    }
    
    public static final class Result
    {
        public String module;
        public String type;
        public int count;
        public int drainableCount;
        
        public Result() {
            this.module = null;
            this.count = 1;
            this.drainableCount = 0;
        }
        
        public String getType() {
            return this.type;
        }
        
        public void setType(final String type) {
            this.type = type;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public void setCount(final int count) {
            this.count = count;
        }
        
        public String getModule() {
            return this.module;
        }
        
        public void setModule(final String module) {
            this.module = module;
        }
        
        public String getFullType() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module, this.type);
        }
        
        public int getDrainableCount() {
            return this.drainableCount;
        }
        
        public void setDrainableCount(final int drainableCount) {
            this.drainableCount = drainableCount;
        }
    }
    
    public static final class Source
    {
        public boolean keep;
        private final ArrayList<String> items;
        public boolean destroy;
        public float count;
        public float use;
        
        public Source() {
            this.keep = false;
            this.items = new ArrayList<String>();
            this.destroy = false;
            this.count = 1.0f;
            this.use = 0.0f;
        }
        
        public boolean isDestroy() {
            return this.destroy;
        }
        
        public void setDestroy(final boolean destroy) {
            this.destroy = destroy;
        }
        
        public boolean isKeep() {
            return this.keep;
        }
        
        public void setKeep(final boolean keep) {
            this.keep = keep;
        }
        
        public float getCount() {
            return this.count;
        }
        
        public void setCount(final float count) {
            this.count = count;
        }
        
        public float getUse() {
            return this.use;
        }
        
        public void setUse(final float use) {
            this.use = use;
        }
        
        public ArrayList<String> getItems() {
            return this.items;
        }
        
        public String getOnlyItem() {
            if (this.items.size() != 1) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.items.size()));
            }
            return this.items.get(0);
        }
    }
    
    public static final class RequiredSkill
    {
        private final PerkFactory.Perk perk;
        private final int level;
        
        public RequiredSkill(final PerkFactory.Perk perk, final int level) {
            this.perk = perk;
            this.level = level;
        }
        
        public PerkFactory.Perk getPerk() {
            return this.perk;
        }
        
        public int getLevel() {
            return this.level;
        }
    }
}
