// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting;

import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.Item;

public interface IScriptObjectStore
{
    Item getItem(final String p0);
    
    Recipe getRecipe(final String p0);
}
