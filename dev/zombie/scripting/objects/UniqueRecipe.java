// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.ArrayList;

public final class UniqueRecipe extends BaseScriptObject
{
    private String name;
    private String baseRecipe;
    private final ArrayList<String> items;
    private int hungerBonus;
    private int hapinessBonus;
    private int boredomBonus;
    
    public UniqueRecipe(final String name) {
        this.name = null;
        this.baseRecipe = null;
        this.items = new ArrayList<String>();
        this.hungerBonus = 0;
        this.hapinessBonus = 0;
        this.boredomBonus = 0;
        this.setName(name);
    }
    
    @Override
    public void Load(final String s, final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].trim().isEmpty()) {
                if (array[i].contains(":")) {
                    final String[] split = array[i].split(":");
                    final String trim = split[0].trim();
                    final String trim2 = split[1].trim();
                    if (trim.equals("BaseRecipeItem")) {
                        this.setBaseRecipe(trim2);
                    }
                    else if (trim.equals("Item")) {
                        this.items.add(trim2);
                    }
                    else if (trim.equals("Hunger")) {
                        this.setHungerBonus(Integer.parseInt(trim2));
                    }
                    else if (trim.equals("Hapiness")) {
                        this.setHapinessBonus(Integer.parseInt(trim2));
                    }
                    else if (trim.equals("Boredom")) {
                        this.setBoredomBonus(Integer.parseInt(trim2));
                    }
                }
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getBaseRecipe() {
        return this.baseRecipe;
    }
    
    public void setBaseRecipe(final String baseRecipe) {
        this.baseRecipe = baseRecipe;
    }
    
    public int getHungerBonus() {
        return this.hungerBonus;
    }
    
    public void setHungerBonus(final int hungerBonus) {
        this.hungerBonus = hungerBonus;
    }
    
    public int getHapinessBonus() {
        return this.hapinessBonus;
    }
    
    public void setHapinessBonus(final int hapinessBonus) {
        this.hapinessBonus = hapinessBonus;
    }
    
    public ArrayList<String> getItems() {
        return this.items;
    }
    
    public int getBoredomBonus() {
        return this.boredomBonus;
    }
    
    public void setBoredomBonus(final int boredomBonus) {
        this.boredomBonus = boredomBonus;
    }
}
