// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.HashMap;
import java.util.Map;

public final class RadioGlobalsManager
{
    private final Map<String, RadioGlobal> globals;
    private final RadioGlobalInt bufferInt;
    private final RadioGlobalString bufferString;
    private final RadioGlobalBool bufferBoolean;
    private final RadioGlobalFloat bufferFloat;
    private static RadioGlobalsManager instance;
    
    public static RadioGlobalsManager getInstance() {
        if (RadioGlobalsManager.instance == null) {
            RadioGlobalsManager.instance = new RadioGlobalsManager();
        }
        return RadioGlobalsManager.instance;
    }
    
    private RadioGlobalsManager() {
        this.globals = new HashMap<String, RadioGlobal>();
        this.bufferInt = new RadioGlobalInt("bufferInt", 0);
        this.bufferString = new RadioGlobalString("bufferString", "");
        this.bufferBoolean = new RadioGlobalBool("bufferBoolean", false);
        this.bufferFloat = new RadioGlobalFloat("bufferFloat", 0.0f);
    }
    
    public void reset() {
        RadioGlobalsManager.instance = null;
    }
    
    public boolean exists(final String s) {
        return this.globals.containsKey(s);
    }
    
    public RadioGlobalType getType(final String s) {
        if (this.globals.containsKey(s)) {
            return this.globals.get(s).getType();
        }
        return RadioGlobalType.Invalid;
    }
    
    public String getString(final String s) {
        final RadioGlobal global = this.getGlobal(s);
        if (global != null) {
            return global.getString();
        }
        return null;
    }
    
    public boolean addGlobal(final String s, final RadioGlobal radioGlobal) {
        if (!this.exists(s) && radioGlobal != null) {
            this.globals.put(s, radioGlobal);
            return true;
        }
        DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        return false;
    }
    
    public boolean addGlobalString(final String s, final String s2) {
        return this.addGlobal(s, new RadioGlobalString(s, s2));
    }
    
    public boolean addGlobalBool(final String s, final boolean b) {
        return this.addGlobal(s, new RadioGlobalBool(s, b));
    }
    
    public boolean addGlobalInt(final String s, final int n) {
        return this.addGlobal(s, new RadioGlobalInt(s, n));
    }
    
    public boolean addGlobalFloat(final String s, final float n) {
        return this.addGlobal(s, new RadioGlobalFloat(s, n));
    }
    
    public RadioGlobal getGlobal(final String s) {
        if (this.exists(s)) {
            return this.globals.get(s);
        }
        return null;
    }
    
    public RadioGlobalString getGlobalString(final String s) {
        final RadioGlobal global = this.getGlobal(s);
        return (global != null && global instanceof RadioGlobalString) ? ((RadioGlobalString)global) : null;
    }
    
    public RadioGlobalInt getGlobalInt(final String s) {
        final RadioGlobal global = this.getGlobal(s);
        return (global != null && global instanceof RadioGlobalInt) ? ((RadioGlobalInt)global) : null;
    }
    
    public RadioGlobalFloat getGlobalFloat(final String s) {
        final RadioGlobal global = this.getGlobal(s);
        return (global != null && global instanceof RadioGlobalFloat) ? ((RadioGlobalFloat)global) : null;
    }
    
    public RadioGlobalBool getGlobalBool(final String s) {
        final RadioGlobal global = this.getGlobal(s);
        return (global != null && global instanceof RadioGlobalBool) ? ((RadioGlobalBool)global) : null;
    }
    
    public boolean setGlobal(final String s, final RadioGlobal radioGlobal, final EditGlobalOps editGlobalOps) {
        final RadioGlobal global = this.getGlobal(s);
        return global != null && radioGlobal != null && global.setValue(radioGlobal, editGlobalOps);
    }
    
    public boolean setGlobal(final String s, final String value) {
        this.bufferString.setValue(value);
        return this.setGlobal(s, this.bufferString, EditGlobalOps.set);
    }
    
    public boolean setGlobal(final String s, final int value) {
        this.bufferInt.setValue(value);
        return this.setGlobal(s, this.bufferInt, EditGlobalOps.set);
    }
    
    public boolean setGlobal(final String s, final float value) {
        this.bufferFloat.setValue(value);
        return this.setGlobal(s, this.bufferFloat, EditGlobalOps.set);
    }
    
    public boolean setGlobal(final String s, final boolean value) {
        this.bufferBoolean.setValue(value);
        return this.setGlobal(s, this.bufferBoolean, EditGlobalOps.set);
    }
    
    public CompareResult compare(final RadioGlobal radioGlobal, final RadioGlobal radioGlobal2, final CompareMethod compareMethod) {
        if (radioGlobal != null && radioGlobal2 != null && radioGlobal.getType().equals(radioGlobal2.getType())) {
            return radioGlobal.compare(radioGlobal2, compareMethod);
        }
        return CompareResult.Invalid;
    }
    
    public CompareResult compare(final String s, final String s2, final CompareMethod compareMethod) {
        return this.compare(this.getGlobal(s), this.getGlobal(s2), compareMethod);
    }
}
