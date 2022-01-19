// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public final class RadioGlobalString extends RadioGlobal<String>
{
    public RadioGlobalString(final String s) {
        super(s, RadioGlobalType.String);
    }
    
    public RadioGlobalString(final String s, final String s2) {
        super(s, s2, RadioGlobalType.String);
    }
    
    public String getValue() {
        return (String)this.value;
    }
    
    public void setValue(final String value) {
        this.value = (T)value;
    }
    
    @Override
    public String getString() {
        return (String)this.value;
    }
    
    @Override
    public CompareResult compare(final RadioGlobal radioGlobal, final CompareMethod compareMethod) {
        if (!(radioGlobal instanceof RadioGlobalString)) {
            return CompareResult.Invalid;
        }
        final RadioGlobalString radioGlobalString = (RadioGlobalString)radioGlobal;
        switch (compareMethod) {
            case equals: {
                return ((String)this.value).equals(radioGlobalString.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case notequals: {
                return ((String)this.value).equals(radioGlobalString.getValue()) ? CompareResult.False : CompareResult.True;
            }
            default: {
                return CompareResult.Invalid;
            }
        }
    }
    
    @Override
    public boolean setValue(final RadioGlobal radioGlobal, final EditGlobalOps editGlobalOps) {
        if (editGlobalOps.equals(EditGlobalOps.set) && radioGlobal instanceof RadioGlobalString) {
            this.value = (T)((RadioGlobalString)radioGlobal).getValue();
            return true;
        }
        return false;
    }
}
