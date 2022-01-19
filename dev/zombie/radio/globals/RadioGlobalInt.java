// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public final class RadioGlobalInt extends RadioGlobal<Integer>
{
    public RadioGlobalInt(final int i) {
        super(i, RadioGlobalType.Integer);
    }
    
    public RadioGlobalInt(final String s, final int i) {
        super(s, i, RadioGlobalType.Integer);
    }
    
    public int getValue() {
        return (int)this.value;
    }
    
    public void setValue(final int i) {
        this.value = (T)Integer.valueOf(i);
    }
    
    @Override
    public String getString() {
        return ((Integer)this.value).toString();
    }
    
    @Override
    public CompareResult compare(final RadioGlobal radioGlobal, final CompareMethod compareMethod) {
        if (!(radioGlobal instanceof RadioGlobalInt)) {
            return CompareResult.Invalid;
        }
        final RadioGlobalInt radioGlobalInt = (RadioGlobalInt)radioGlobal;
        switch (compareMethod) {
            case equals: {
                return ((int)this.value == radioGlobalInt.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case notequals: {
                return ((int)this.value != radioGlobalInt.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case lessthan: {
                return ((int)this.value < radioGlobalInt.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case morethan: {
                return ((int)this.value > radioGlobalInt.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case lessthanorequals: {
                return ((int)this.value <= radioGlobalInt.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case morethanorequals: {
                return ((int)this.value >= radioGlobalInt.getValue()) ? CompareResult.True : CompareResult.False;
            }
            default: {
                return CompareResult.Invalid;
            }
        }
    }
    
    @Override
    public boolean setValue(final RadioGlobal radioGlobal, final EditGlobalOps editGlobalOps) {
        if (radioGlobal instanceof RadioGlobalInt) {
            final RadioGlobalInt radioGlobalInt = (RadioGlobalInt)radioGlobal;
            switch (editGlobalOps) {
                case set: {
                    this.value = (T)Integer.valueOf(radioGlobalInt.getValue());
                    return true;
                }
                case add: {
                    this.value = (T)Integer.valueOf((int)this.value + radioGlobalInt.getValue());
                    return true;
                }
                case sub: {
                    this.value = (T)Integer.valueOf((int)this.value - radioGlobalInt.getValue());
                    return true;
                }
            }
        }
        return false;
    }
}
