// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class ZombieTriggerXmlFile
{
    public int spawnHorde;
    public boolean setDebugLoggingEnabled;
    public boolean bDebugLoggingEnabled;
    
    public ZombieTriggerXmlFile() {
        this.spawnHorde = 0;
        this.setDebugLoggingEnabled = false;
        this.bDebugLoggingEnabled = false;
    }
}
