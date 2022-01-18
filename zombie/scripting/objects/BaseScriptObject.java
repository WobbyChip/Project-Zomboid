// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

public class BaseScriptObject
{
    public ScriptModule module;
    
    public BaseScriptObject() {
        this.module = null;
    }
    
    public void Load(final String s, final String[] array) {
    }
    
    public ScriptModule getModule() {
        return this.module;
    }
}
