// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import zombie.core.Rand;
import java.io.File;
import zombie.ZomboidFileSystem;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ItemManager
{
    public ArrayList<CarriedItem> m_Items;
    @XmlTransient
    public static ItemManager instance;
    
    public ItemManager() {
        this.m_Items = new ArrayList<CarriedItem>();
    }
    
    public static void init() {
        ItemManager.instance = Parse(ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, File.separator)).getPath());
    }
    
    public CarriedItem GetRandomItem() {
        final int next = Rand.Next(this.m_Items.size() + 1);
        if (next < this.m_Items.size()) {
            return this.m_Items.get(next);
        }
        return null;
    }
    
    public static ItemManager Parse(final String s) {
        try {
            return parse(s);
        }
        catch (JAXBException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
        return null;
    }
    
    public static ItemManager parse(final String name) throws JAXBException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(name);
        try {
            final ItemManager itemManager = (ItemManager)JAXBContext.newInstance(new Class[] { ItemManager.class }).createUnmarshaller().unmarshal((InputStream)fileInputStream);
            fileInputStream.close();
            return itemManager;
        }
        catch (Throwable t) {
            try {
                fileInputStream.close();
            }
            catch (Throwable exception) {
                t.addSuppressed(exception);
            }
            throw t;
        }
    }
    
    public static void Write(final ItemManager itemManager, final String s) {
        try {
            write(itemManager, s);
        }
        catch (JAXBException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static void write(final ItemManager itemManager, final String name) throws IOException, JAXBException {
        final FileOutputStream fileOutputStream = new FileOutputStream(name);
        try {
            final Marshaller marshaller = JAXBContext.newInstance(new Class[] { ItemManager.class }).createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", (Object)Boolean.TRUE);
            marshaller.marshal((Object)itemManager, (OutputStream)fileOutputStream);
            fileOutputStream.close();
        }
        catch (Throwable t) {
            try {
                fileOutputStream.close();
            }
            catch (Throwable exception) {
                t.addSuppressed(exception);
            }
            throw t;
        }
    }
}
