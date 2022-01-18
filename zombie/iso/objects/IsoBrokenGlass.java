// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.Rand;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;

public class IsoBrokenGlass extends IsoObject
{
    public IsoBrokenGlass(final IsoCell isoCell) {
        super(isoCell);
        this.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(4)));
    }
    
    @Override
    public String getObjectName() {
        return "IsoBrokenGlass";
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
    }
    
    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        super.render(n, n2, n3, colorInfo, b, b2, shader);
    }
    
    @Override
    public void renderObjectPicker(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
    }
}
