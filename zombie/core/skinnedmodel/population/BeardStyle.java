// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import zombie.util.StringUtils;
import java.util.ArrayList;

public class BeardStyle
{
    public String name;
    public String model;
    public String texture;
    public int level;
    public ArrayList<String> trimChoices;
    public boolean growReference;
    
    public BeardStyle() {
        this.name = "";
        this.texture = "F_Hair_White";
        this.level = 0;
        this.trimChoices = new ArrayList<String>();
        this.growReference = false;
    }
    
    public boolean isValid() {
        return !StringUtils.isNullOrWhitespace(this.model) && !StringUtils.isNullOrWhitespace(this.texture);
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArrayList<String> getTrimChoices() {
        return this.trimChoices;
    }
}
