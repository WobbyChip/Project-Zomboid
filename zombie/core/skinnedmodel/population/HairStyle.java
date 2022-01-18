// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import javax.xml.bind.annotation.XmlAttribute;
import zombie.util.StringUtils;
import java.util.ArrayList;

public final class HairStyle
{
    public String name;
    public String model;
    public String texture;
    public final ArrayList<Alternate> alternate;
    public int level;
    public final ArrayList<String> trimChoices;
    public boolean growReference;
    public boolean attachedHair;
    public boolean noChoose;
    
    public HairStyle() {
        this.name = "";
        this.texture = "F_Hair_White";
        this.alternate = new ArrayList<Alternate>();
        this.level = 0;
        this.trimChoices = new ArrayList<String>();
        this.growReference = false;
        this.attachedHair = false;
        this.noChoose = false;
    }
    
    public boolean isValid() {
        return !StringUtils.isNullOrWhitespace(this.model) && !StringUtils.isNullOrWhitespace(this.texture);
    }
    
    public String getAlternate(final String s) {
        for (int i = 0; i < this.alternate.size(); ++i) {
            final Alternate alternate = this.alternate.get(i);
            if (s.equalsIgnoreCase(alternate.category)) {
                return alternate.style;
            }
        }
        return this.name;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArrayList<String> getTrimChoices() {
        return this.trimChoices;
    }
    
    public boolean isAttachedHair() {
        return this.attachedHair;
    }
    
    public boolean isGrowReference() {
        return this.growReference;
    }
    
    public boolean isNoChoose() {
        return this.noChoose;
    }
    
    public static final class Alternate
    {
        @XmlAttribute
        public String category;
        @XmlAttribute
        public String style;
    }
}
