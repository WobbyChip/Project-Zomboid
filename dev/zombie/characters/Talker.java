// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

public interface Talker
{
    boolean IsSpeaking();
    
    void Say(final String p0);
    
    String getSayLine();
    
    String getTalkerType();
}
