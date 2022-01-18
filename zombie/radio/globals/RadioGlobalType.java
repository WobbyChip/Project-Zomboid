// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public enum RadioGlobalType
{
    String, 
    Integer, 
    Boolean, 
    Float, 
    Invalid;
    
    private static /* synthetic */ RadioGlobalType[] $values() {
        return new RadioGlobalType[] { RadioGlobalType.String, RadioGlobalType.Integer, RadioGlobalType.Boolean, RadioGlobalType.Float, RadioGlobalType.Invalid };
    }
    
    static {
        $VALUES = $values();
    }
}
