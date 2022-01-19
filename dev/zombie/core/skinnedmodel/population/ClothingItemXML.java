// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clothingItem")
public class ClothingItemXML
{
    public String m_GUID;
    public String m_MaleModel;
    public String m_FemaleModel;
    public boolean m_Static;
    public ArrayList<String> m_BaseTextures;
    public String m_AttachBone;
    public ArrayList<Integer> m_Masks;
    public String m_MasksFolder;
    public String m_UnderlayMasksFolder;
    public ArrayList<String> textureChoices;
    public boolean m_AllowRandomHue;
    public boolean m_AllowRandomTint;
    public String m_DecalGroup;
    public String m_Shader;
    public String m_HatCategory;
    
    public ClothingItemXML() {
        this.m_Static = false;
        this.m_BaseTextures = new ArrayList<String>();
        this.m_Masks = new ArrayList<Integer>();
        this.m_MasksFolder = "media/textures/Body/Masks";
        this.m_UnderlayMasksFolder = "media/textures/Body/Masks";
        this.textureChoices = new ArrayList<String>();
        this.m_AllowRandomHue = false;
        this.m_AllowRandomTint = false;
        this.m_DecalGroup = null;
        this.m_Shader = null;
        this.m_HatCategory = null;
    }
}
