// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public abstract class RadioGlobal<T>
{
    protected String name;
    protected T value;
    protected RadioGlobalType type;
    
    protected RadioGlobal(final T t, final RadioGlobalType radioGlobalType) {
        this(null, t, radioGlobalType);
    }
    
    protected RadioGlobal(final String name, final T value, final RadioGlobalType type) {
        this.type = RadioGlobalType.Invalid;
        this.name = name;
        this.value = value;
        this.type = type;
    }
    
    public final RadioGlobalType getType() {
        return this.type;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public abstract String getString();
    
    public abstract CompareResult compare(final RadioGlobal p0, final CompareMethod p1);
    
    public abstract boolean setValue(final RadioGlobal p0, final EditGlobalOps p1);
}
