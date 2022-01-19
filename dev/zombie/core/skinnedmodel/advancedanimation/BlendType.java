// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public enum BlendType
{
    Linear, 
    InverseExponential, 
    Type;
    
    private static /* synthetic */ BlendType[] $values() {
        return new BlendType[] { BlendType.Linear, BlendType.InverseExponential, BlendType.Type };
    }
    
    static {
        $VALUES = $values();
    }
}
