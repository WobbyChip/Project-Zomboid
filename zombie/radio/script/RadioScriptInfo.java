// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.script;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import zombie.radio.globals.RadioGlobal;
import java.util.Map;

public final class RadioScriptInfo
{
    private final Map<String, RadioGlobal> onStartSetters;
    private final List<ExitOptionOld> exitOptions;
    
    public RadioScriptInfo() {
        this.onStartSetters = new HashMap<String, RadioGlobal>();
        this.exitOptions = new ArrayList<ExitOptionOld>();
    }
    
    public RadioScriptEntry getNextScript() {
        RadioScriptEntry evaluate = null;
        for (final ExitOptionOld exitOptionOld : this.exitOptions) {
            if (exitOptionOld != null) {
                evaluate = exitOptionOld.evaluate();
                if (evaluate != null) {
                    break;
                }
                continue;
            }
        }
        return evaluate;
    }
    
    public void addExitOption(final ExitOptionOld exitOptionOld) {
        if (exitOptionOld != null) {
            this.exitOptions.add(exitOptionOld);
        }
    }
}
