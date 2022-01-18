// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import zombie.util.StringUtils;
import java.util.ArrayList;
import org.joml.Vector3f;

public final class ModelAttachment
{
    private String id;
    private final Vector3f offset;
    private final Vector3f rotate;
    private String bone;
    private ArrayList<String> canAttach;
    private float zoffset;
    private boolean updateConstraint;
    
    public ModelAttachment(final String id) {
        this.offset = new Vector3f();
        this.rotate = new Vector3f();
        this.updateConstraint = true;
        this.setId(id);
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        if (StringUtils.isNullOrWhitespace(id)) {
            throw new IllegalArgumentException("ModelAttachment id is null or empty");
        }
        this.id = id;
    }
    
    public Vector3f getOffset() {
        return this.offset;
    }
    
    public Vector3f getRotate() {
        return this.rotate;
    }
    
    public String getBone() {
        return this.bone;
    }
    
    public void setBone(String trim) {
        trim = trim.trim();
        this.bone = (trim.isEmpty() ? null : trim);
    }
    
    public ArrayList<String> getCanAttach() {
        return this.canAttach;
    }
    
    public void setCanAttach(final ArrayList<String> canAttach) {
        this.canAttach = canAttach;
    }
    
    public float getZOffset() {
        return this.zoffset;
    }
    
    public void setZOffset(final float zoffset) {
        this.zoffset = zoffset;
    }
    
    public boolean isUpdateConstraint() {
        return this.updateConstraint;
    }
    
    public void setUpdateConstraint(final boolean updateConstraint) {
        this.updateConstraint = updateConstraint;
    }
}
