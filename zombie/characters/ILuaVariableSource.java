// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

public interface ILuaVariableSource
{
    String GetVariable(final String p0);
    
    void SetVariable(final String p0, final String p1);
    
    void ClearVariable(final String p0);
}
