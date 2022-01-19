// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public final class RadioGlobalBool extends RadioGlobal<Boolean>
{
    public RadioGlobalBool(final boolean b) {
        super(b, RadioGlobalType.Boolean);
    }
    
    public RadioGlobalBool(final String s, final boolean b) {
        super(s, b, RadioGlobalType.Boolean);
    }
    
    public boolean getValue() {
        return (boolean)this.value;
    }
    
    public void setValue(final boolean b) {
        this.value = (T)Boolean.valueOf(b);
    }
    
    @Override
    public String getString() {
        return ((Boolean)this.value).toString();
    }
    
    @Override
    public CompareResult compare(final RadioGlobal radioGlobal, final CompareMethod compareMethod) {
        if (!(radioGlobal instanceof RadioGlobalBool)) {
            return CompareResult.Invalid;
        }
        final RadioGlobalBool radioGlobalBool = (RadioGlobalBool)radioGlobal;
        switch (compareMethod) {
            case equals: {
                return ((Boolean)this.value).equals(radioGlobalBool.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case notequals: {
                return ((Boolean)this.value).equals(radioGlobalBool.getValue()) ? CompareResult.False : CompareResult.True;
            }
            default: {
                return CompareResult.Invalid;
            }
        }
    }
    
    @Override
    public boolean setValue(final RadioGlobal radioGlobal, final EditGlobalOps editGlobalOps) {
        if (editGlobalOps.equals(EditGlobalOps.set) && radioGlobal instanceof RadioGlobalBool) {
            this.value = (T)Boolean.valueOf(((RadioGlobalBool)radioGlobal).getValue());
            return true;
        }
        return false;
    }
}
