// 
// Decompiled by Procyon v0.5.36
// 

package zombie.chat;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public class NineGridTexture
{
    private Texture topLeft;
    private Texture topMid;
    private Texture topRight;
    private Texture left;
    private Texture mid;
    private Texture right;
    private Texture botLeft;
    private Texture botMid;
    private Texture botRight;
    private int outer;
    
    public NineGridTexture(final String s, final int outer) {
        this.outer = outer;
        this.topLeft = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.topMid = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.topRight = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.left = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.mid = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.right = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.botLeft = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.botMid = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.botRight = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public void renderInnerBased(final int n, int n2, final int n3, int n4, final float n5, final float n6, final float n7, final float n8) {
        n2 += 5;
        n4 -= 7;
        SpriteRenderer.instance.renderi(this.topLeft, n - this.outer, n2 - this.outer, this.outer, this.outer, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.topMid, n, n2 - this.outer, n3, this.outer, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.topRight, n + n3, n2 - this.outer, this.outer, this.outer, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.left, n - this.outer, n2, this.outer, n4, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.mid, n, n2, n3, n4, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.right, n + n3, n2, this.outer, n4, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.botLeft, n - this.outer, n2 + n4, this.outer, this.outer, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.botMid, n, n2 + n4, n3, this.outer, n5, n6, n7, n8, null);
        SpriteRenderer.instance.renderi(this.botRight, n + n3, n2 + n4, this.outer, this.outer, n5, n6, n7, n8, null);
    }
}
