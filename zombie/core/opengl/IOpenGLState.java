// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

public abstract class IOpenGLState<T extends Value>
{
    protected T currentValue;
    private boolean dirty;
    
    public IOpenGLState() {
        this.currentValue = this.defaultValue();
        this.dirty = true;
    }
    
    public void set(final T currentValue) {
        if (this.dirty || !currentValue.equals(this.currentValue)) {
            this.setCurrentValue(currentValue);
            this.Set(currentValue);
        }
    }
    
    void setCurrentValue(final T t) {
        this.dirty = false;
        this.currentValue.set(t);
    }
    
    public void setDirty() {
        this.dirty = true;
    }
    
    T getCurrentValue() {
        return this.currentValue;
    }
    
    abstract T defaultValue();
    
    abstract void Set(final T p0);
    
    public interface Value
    {
        Value set(final Value p0);
    }
}
