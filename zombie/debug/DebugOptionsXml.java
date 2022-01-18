// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
final class DebugOptionsXml
{
    public boolean setDebugMode;
    public boolean debugMode;
    public final ArrayList<OptionNode> options;
    
    DebugOptionsXml() {
        this.setDebugMode = false;
        this.debugMode = true;
        this.options = new ArrayList<OptionNode>();
    }
    
    public static final class OptionNode
    {
        public String name;
        public boolean value;
        
        public OptionNode() {
        }
        
        public OptionNode(final String name, final boolean value) {
            this.name = name;
            this.value = value;
        }
    }
}
