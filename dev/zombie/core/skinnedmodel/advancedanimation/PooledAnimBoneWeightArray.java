// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.list.PZArrayUtil;
import java.util.List;
import zombie.util.Pool;
import zombie.util.PooledArrayObject;

public class PooledAnimBoneWeightArray extends PooledArrayObject<AnimBoneWeight>
{
    private static final PooledAnimBoneWeightArray s_empty;
    private static final Pool<PooledAnimBoneWeightArray> s_pool;
    
    public static PooledAnimBoneWeightArray alloc(final int n) {
        if (n == 0) {
            return PooledAnimBoneWeightArray.s_empty;
        }
        final PooledAnimBoneWeightArray pooledAnimBoneWeightArray = PooledAnimBoneWeightArray.s_pool.alloc();
        pooledAnimBoneWeightArray.initCapacity(n, n2 -> new AnimBoneWeight[n2]);
        return pooledAnimBoneWeightArray;
    }
    
    public static PooledAnimBoneWeightArray toArray(final List<AnimBoneWeight> list) {
        if (list == null) {
            return null;
        }
        final PooledAnimBoneWeightArray alloc = alloc(list.size());
        PZArrayUtil.arrayCopy((AnimBoneWeight[])((PooledArrayObject<E>)alloc).array(), list);
        return alloc;
    }
    
    public static PooledAnimBoneWeightArray toArray(final PooledArrayObject<AnimBoneWeight> pooledArrayObject) {
        if (pooledArrayObject == null) {
            return null;
        }
        final PooledAnimBoneWeightArray alloc = alloc(pooledArrayObject.length());
        PZArrayUtil.arrayCopy((AnimBoneWeight[])((PooledArrayObject<E>)alloc).array(), (AnimBoneWeight[])pooledArrayObject.array());
        return alloc;
    }
    
    static {
        s_empty = new PooledAnimBoneWeightArray();
        s_pool = new Pool<PooledAnimBoneWeightArray>(PooledAnimBoneWeightArray::new);
    }
}
