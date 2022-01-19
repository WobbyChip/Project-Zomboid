// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

public final class AttachedModelName
{
    public String attachmentName;
    public String modelName;
    public float bloodLevel;
    
    public AttachedModelName(final AttachedModelName attachedModelName) {
        this.attachmentName = attachedModelName.attachmentName;
        this.modelName = attachedModelName.modelName;
        this.bloodLevel = attachedModelName.bloodLevel;
    }
    
    public AttachedModelName(final String attachmentName, final String modelName, final float bloodLevel) {
        this.attachmentName = attachmentName;
        this.modelName = modelName;
        this.bloodLevel = bloodLevel;
    }
}
