// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.util.Pool;

public final class TwistableBoneTransform extends BoneTransform
{
    public float BlendWeight;
    public float Twist;
    private static final Pool<TwistableBoneTransform> s_pool;
    
    protected TwistableBoneTransform() {
        this.BlendWeight = 0.0f;
        this.Twist = 0.0f;
    }
    
    public static TwistableBoneTransform alloc() {
        return TwistableBoneTransform.s_pool.alloc();
    }
    
    static {
        s_pool = new Pool<TwistableBoneTransform>(TwistableBoneTransform::new);
    }
}
