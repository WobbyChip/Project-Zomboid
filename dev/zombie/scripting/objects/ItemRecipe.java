// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

public final class ItemRecipe
{
    public String name;
    public Integer use;
    public Boolean cooked;
    private String module;
    
    public Integer getUse() {
        return this.use;
    }
    
    public ItemRecipe(final String name, final String module, final Integer use) {
        this.use = -1;
        this.cooked = false;
        this.module = null;
        this.name = name;
        this.use = use;
        this.setModule(module);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getModule() {
        return this.module;
    }
    
    public void setModule(final String module) {
        this.module = module;
    }
    
    public String getFullType() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module, this.name);
    }
}
