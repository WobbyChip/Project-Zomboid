// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.skills;

public final class CustomPerk
{
    public String m_id;
    public String m_parent;
    public String m_translation;
    public boolean m_bPassive;
    public final int[] m_xp;
    
    public CustomPerk(final String id) {
        this.m_parent = "None";
        this.m_bPassive = false;
        this.m_xp = new int[10];
        this.m_id = id;
    }
}
