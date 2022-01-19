// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public final class RadioGlobalFloat extends RadioGlobal<Float>
{
    public RadioGlobalFloat(final float f) {
        super(f, RadioGlobalType.Float);
    }
    
    public RadioGlobalFloat(final String s, final float f) {
        super(s, f, RadioGlobalType.Float);
    }
    
    public float getValue() {
        return (float)this.value;
    }
    
    public void setValue(final float f) {
        this.value = (T)Float.valueOf(f);
    }
    
    @Override
    public String getString() {
        return ((Float)this.value).toString();
    }
    
    @Override
    public CompareResult compare(final RadioGlobal radioGlobal, final CompareMethod compareMethod) {
        if (!(radioGlobal instanceof RadioGlobalFloat)) {
            return CompareResult.Invalid;
        }
        final RadioGlobalFloat radioGlobalFloat = (RadioGlobalFloat)radioGlobal;
        switch (compareMethod) {
            case equals: {
                return ((float)this.value == radioGlobalFloat.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case notequals: {
                return ((float)this.value != radioGlobalFloat.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case lessthan: {
                return ((float)this.value < radioGlobalFloat.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case morethan: {
                return ((float)this.value > radioGlobalFloat.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case lessthanorequals: {
                return ((float)this.value <= radioGlobalFloat.getValue()) ? CompareResult.True : CompareResult.False;
            }
            case morethanorequals: {
                return ((float)this.value >= radioGlobalFloat.getValue()) ? CompareResult.True : CompareResult.False;
            }
            default: {
                return CompareResult.Invalid;
            }
        }
    }
    
    @Override
    public boolean setValue(final RadioGlobal radioGlobal, final EditGlobalOps editGlobalOps) {
        if (radioGlobal instanceof RadioGlobalFloat) {
            final RadioGlobalFloat radioGlobalFloat = (RadioGlobalFloat)radioGlobal;
            switch (editGlobalOps) {
                case set: {
                    this.value = (T)Float.valueOf(radioGlobalFloat.getValue());
                    return true;
                }
                case add: {
                    this.value = (T)Float.valueOf((float)this.value + radioGlobalFloat.getValue());
                    return true;
                }
                case sub: {
                    this.value = (T)Float.valueOf((float)this.value - radioGlobalFloat.getValue());
                    return true;
                }
            }
        }
        return false;
    }
}
