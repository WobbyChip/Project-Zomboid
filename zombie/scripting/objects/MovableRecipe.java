// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.Collection;
import java.util.Arrays;
import zombie.debug.DebugLog;
import zombie.characters.skills.PerkFactory;

public class MovableRecipe extends Recipe
{
    private boolean isValid;
    private String worldSprite;
    private PerkFactory.Perk xpPerk;
    private Source primaryTools;
    private Source secondaryTools;
    
    public MovableRecipe() {
        this.isValid = false;
        this.worldSprite = "";
        this.xpPerk = PerkFactory.Perks.MAX;
        this.AnimNode = "Disassemble";
        this.removeResultItem = true;
        this.AllowDestroyedItem = false;
        this.name = "Disassemble Movable";
        this.setCanBeDoneFromFloor(false);
    }
    
    public void setResult(final String s, final int count) {
        final Result result = new Result();
        result.count = count;
        if (s.contains(".")) {
            result.type = s.split("\\.")[1];
            result.module = s.split("\\.")[0];
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        this.Result = result;
    }
    
    public void setSource(final String e) {
        final Source e2 = new Source();
        e2.getItems().add(e);
        this.Source.add(e2);
    }
    
    public void setTool(String trim, final boolean b) {
        final Source e = new Source();
        e.keep = true;
        if (trim.contains("/")) {
            trim = trim.replaceFirst("keep ", "").trim();
            e.getItems().addAll(Arrays.asList(trim.split("/")));
        }
        else {
            e.getItems().add(trim);
        }
        if (b) {
            this.primaryTools = e;
        }
        else {
            this.secondaryTools = e;
        }
        this.Source.add(e);
    }
    
    public Source getPrimaryTools() {
        return this.primaryTools;
    }
    
    public Source getSecondaryTools() {
        return this.secondaryTools;
    }
    
    public void setRequiredSkill(final PerkFactory.Perk perk, final int n) {
        this.skillRequired.add(new RequiredSkill(perk, n));
    }
    
    public void setXpPerk(final PerkFactory.Perk xpPerk) {
        this.xpPerk = xpPerk;
    }
    
    public PerkFactory.Perk getXpPerk() {
        return this.xpPerk;
    }
    
    public boolean hasXpPerk() {
        return this.xpPerk != PerkFactory.Perks.MAX;
    }
    
    public void setOnCreate(final String luaCreate) {
        this.LuaCreate = luaCreate;
    }
    
    public void setOnXP(final String luaGiveXP) {
        this.LuaGiveXP = luaGiveXP;
    }
    
    public void setTime(final float timeToMake) {
        this.TimeToMake = timeToMake;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getWorldSprite() {
        return this.worldSprite;
    }
    
    public void setWorldSprite(final String worldSprite) {
        this.worldSprite = worldSprite;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    public void setValid(final boolean isValid) {
        this.isValid = isValid;
    }
}
