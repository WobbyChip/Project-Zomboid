// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import zombie.scripting.ScriptManager;

public final class VehicleTemplate extends BaseScriptObject
{
    public String name;
    public String body;
    public VehicleScript script;
    
    public VehicleTemplate(final ScriptModule module, final String name, final String body) {
        final ScriptManager instance = ScriptManager.instance;
        if (!instance.scriptsWithVehicleTemplates.contains(instance.currentFileName)) {
            instance.scriptsWithVehicleTemplates.add(instance.currentFileName);
        }
        this.module = module;
        this.name = name;
        this.body = body;
    }
    
    public VehicleScript getScript() {
        if (this.script == null) {
            this.script = new VehicleScript();
            this.script.module = this.getModule();
            this.script.Load(this.name, this.body);
        }
        return this.script;
    }
}
