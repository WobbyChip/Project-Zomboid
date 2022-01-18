// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public enum CompareMethod
{
    equals, 
    notequals, 
    lessthan, 
    morethan, 
    lessthanorequals, 
    morethanorequals;
    
    private static /* synthetic */ CompareMethod[] $values() {
        return new CompareMethod[] { CompareMethod.equals, CompareMethod.notequals, CompareMethod.lessthan, CompareMethod.morethan, CompareMethod.lessthanorequals, CompareMethod.morethanorequals };
    }
    
    static {
        $VALUES = $values();
    }
}
