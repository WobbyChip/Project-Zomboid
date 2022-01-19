// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.util.function.Consumer;

public final class SkinningBone
{
    public SkinningBone Parent;
    public String Name;
    public int Index;
    public SkinningBone[] Children;
    
    public void forEachDescendant(final Consumer<SkinningBone> consumer) {
        forEachDescendant(this, consumer);
    }
    
    private static void forEachDescendant(final SkinningBone skinningBone, final Consumer<SkinningBone> consumer) {
        if (skinningBone.Children == null || skinningBone.Children.length == 0) {
            return;
        }
        final SkinningBone[] children = skinningBone.Children;
        for (int length = children.length, i = 0; i < length; ++i) {
            consumer.accept(children[i]);
        }
        final SkinningBone[] children2 = skinningBone.Children;
        for (int length2 = children2.length, j = 0; j < length2; ++j) {
            forEachDescendant(children2[j], consumer);
        }
    }
    
    @Override
    public String toString() {
        final String lineSeparator = System.lineSeparator();
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getClass().getName(), lineSeparator, lineSeparator, this.Name, lineSeparator, this.Index, lineSeparator);
    }
}
