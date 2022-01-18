// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;

public class ClothingDecalGroup
{
    @XmlElement(name = "name")
    public String m_Name;
    @XmlElement(name = "decal")
    public final ArrayList<String> m_Decals;
    @XmlElement(name = "group")
    public final ArrayList<String> m_Groups;
    private final ArrayList<String> tempDecals;
    
    public ClothingDecalGroup() {
        this.m_Decals = new ArrayList<String>();
        this.m_Groups = new ArrayList<String>();
        this.tempDecals = new ArrayList<String>();
    }
    
    public String getRandomDecal() {
        this.tempDecals.clear();
        this.getDecals(this.tempDecals);
        final String s = OutfitRNG.pickRandom(this.tempDecals);
        return (s == null) ? null : s;
    }
    
    public void getDecals(final ArrayList<String> list) {
        list.addAll(this.m_Decals);
        for (int i = 0; i < this.m_Groups.size(); ++i) {
            final ClothingDecalGroup findGroup = ClothingDecals.instance.FindGroup(this.m_Groups.get(i));
            if (findGroup != null) {
                findGroup.getDecals(list);
            }
        }
    }
}
