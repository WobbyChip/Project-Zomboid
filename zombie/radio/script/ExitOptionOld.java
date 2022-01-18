// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.script;

import java.util.Iterator;
import zombie.core.Rand;
import zombie.radio.globals.CompareResult;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.ArrayList;
import java.util.List;

public final class ExitOptionOld
{
    private String parentScript;
    private String name;
    private ConditionContainer condition;
    private List<RadioScriptEntry> scriptEntries;
    
    public ExitOptionOld(final String s, final String s2) {
        this.scriptEntries = new ArrayList<RadioScriptEntry>();
        this.parentScript = ((s != null) ? s : "Noname");
        this.name = ((s2 != null) ? s2 : "Noname");
    }
    
    public void setCondition(final ConditionContainer condition) {
        this.condition = condition;
    }
    
    public void addScriptEntry(final RadioScriptEntry radioScriptEntry) {
        if (radioScriptEntry != null) {
            this.scriptEntries.add(radioScriptEntry);
        }
        else {
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.parentScript, this.name));
        }
    }
    
    public RadioScriptEntry evaluate() {
        CompareResult compareResult = CompareResult.True;
        if (this.condition != null) {
            compareResult = this.condition.Evaluate();
        }
        if (compareResult.equals(CompareResult.True)) {
            if (this.scriptEntries != null && this.scriptEntries.size() > 0) {
                final int next = Rand.Next(100);
                for (final RadioScriptEntry radioScriptEntry : this.scriptEntries) {
                    if (radioScriptEntry != null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, radioScriptEntry.getScriptName()));
                        System.out.println(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, next, radioScriptEntry.getChanceMin(), radioScriptEntry.getChanceMax()));
                        if (next >= radioScriptEntry.getChanceMin() && next < radioScriptEntry.getChanceMax()) {
                            return radioScriptEntry;
                        }
                        continue;
                    }
                }
            }
        }
        else if (compareResult.equals(CompareResult.Invalid)) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.parentScript, this.name));
            DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.parentScript, this.name));
        }
        return null;
    }
}
