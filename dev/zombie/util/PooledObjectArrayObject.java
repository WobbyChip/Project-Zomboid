// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public final class PooledObjectArrayObject<T extends IPooledObject> extends PooledArrayObject<T>
{
    @Override
    public void onReleased() {
        for (int i = 0; i < this.length(); ++i) {
            this.get(i).release();
        }
    }
}
