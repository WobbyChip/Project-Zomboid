// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.Collection;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Map;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class FileGuidTable
{
    public final ArrayList<FileGuidPair> files;
    @XmlTransient
    private final Map<String, String> guidToPath;
    @XmlTransient
    private final Map<String, String> pathToGuid;
    
    public FileGuidTable() {
        this.files = new ArrayList<FileGuidPair>();
        this.guidToPath = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        this.pathToGuid = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    }
    
    public void setModID(final String s) {
        for (final FileGuidPair fileGuidPair : this.files) {
            fileGuidPair.guid = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, fileGuidPair.guid);
        }
    }
    
    public void mergeFrom(final FileGuidTable fileGuidTable) {
        this.files.addAll(fileGuidTable.files);
    }
    
    public void loaded() {
        for (final FileGuidPair fileGuidPair : this.files) {
            this.guidToPath.put(fileGuidPair.guid, fileGuidPair.path);
            this.pathToGuid.put(fileGuidPair.path, fileGuidPair.guid);
        }
    }
    
    public void clear() {
        this.files.clear();
        this.guidToPath.clear();
        this.pathToGuid.clear();
    }
    
    public String getFilePathFromGuid(final String s) {
        return this.guidToPath.get(s);
    }
    
    public String getGuidFromFilePath(final String s) {
        return this.pathToGuid.get(s);
    }
}
