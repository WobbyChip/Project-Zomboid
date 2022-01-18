// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.bucket;

public final class BucketManager
{
    static final Bucket SharedBucket;
    
    public static Bucket Active() {
        return BucketManager.SharedBucket;
    }
    
    public static Bucket Shared() {
        return BucketManager.SharedBucket;
    }
    
    static {
        SharedBucket = new Bucket();
    }
}
