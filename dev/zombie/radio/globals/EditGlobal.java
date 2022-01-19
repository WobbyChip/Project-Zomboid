// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public final class EditGlobal
{
    private RadioGlobal global;
    private RadioGlobal value;
    private EditGlobalOps operator;
    
    public EditGlobal(final RadioGlobal global, final EditGlobalOps operator, final RadioGlobal value) {
        this.global = global;
        this.operator = operator;
        this.value = value;
    }
    
    public RadioGlobal getGlobal() {
        return this.global;
    }
    
    public EditGlobalOps getOperator() {
        return this.operator;
    }
    
    public RadioGlobal getValue() {
        return this.value;
    }
}
